/**
 * AnswerCheckTokenizer.java, (c) 2012, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.tu_dresden.psy.efml;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * turns a &lt;answer>&lt;check>-term into a series of tokens
 * 
 * @author immanuel
 * 
 */

public class AnswerCheckTokenizer {

	/**
	 * stores a parsed token
	 * 
	 * @author immanuel
	 * 
	 */
	public class Token {
		/**
		 * true, if this token represents a quoted string
		 */
		boolean quoted;
		/**
		 * token value
		 */
		String value;
		
		/**
		 * position of the last character of the token in the input string
		 */
		int lastCharacter;

		public Token(boolean quoted, String value, int before) {
			this.quoted = quoted;
			this.value = value;
			this.lastCharacter = before;
		}
	}

	private ArrayList<Token> tokens;
	

	public AnswerCheckTokenizer(String term) {
		

		tokens = new ArrayList<Token>();

		/**
		 * this is a simple transducing automaton, we have three states 0
		 * unquoted state 1 quoted state 2 escaped quoted state
		 * 
		 * and a hold variable that stores the current token until its end is
		 * reached
		 */

		int quote_escape_state = 0;
		String hold = "";

		for (int i = 0; i < term.length(); ++i) {
			char c = term.charAt(i);

			switch (quote_escape_state) {
			case 0:
				/**
				 * outside of ""
				 */
				if (Character.isLetterOrDigit(c) == true) {
					hold += c;
				} else if (Character.isWhitespace(c) == true) {
					if (hold.isEmpty() != true) {
						tokens.add(new Token(false, hold,i-1));
						hold = "";
					}
				} else if (c == '"') {
					if (hold.isEmpty() != true) {
						tokens.add(new Token(false, hold,i-1));
						hold = "";
					}
					quote_escape_state = 1;
				} else {
					if (hold.isEmpty() != true) {
						tokens.add(new Token(false, hold,i-1));
						hold = "";
					}
					tokens.add(new Token(false, new String() + c, i));
				}
				break;
			case 1:
				/**
				 * inside of ""
				 */
				if (c == '"') {
					quote_escape_state = 0;
					tokens.add(new Token(true, hold, i));
					hold = "";
				} else if (c == '\\') {
					quote_escape_state = 2;
				} else {
					hold += c;
				}
				break;
			case 2:
				/**
				 * inside of "", after \
				 */
				if (c == 'n') {
					hold += '\n';
				} else if (c == 'r') {
					hold += '\r';
				} else if (c == 't') {
					hold += '\t';
				} else
					hold += c;
				quote_escape_state = 1;
				break;
			}
		}

		/**
		 * add final state token for grace
		 */

		if (hold.isEmpty() != true) {
			tokens.add(new Token((quote_escape_state > 0), hold, term.length()-1));
		}
	}

	/**
	 * 
	 * @return iterator of term tokens
	 */
	public Iterator<Token> iterator() {
		return tokens.iterator();
	}

	/**
	 * 
	 * @return number of term tokens
	 */
	public int length() {
		return tokens.size();
	}
	
	/**
	 * 
	 * @param which
	 * @return  true, if the token is a quoted string
	 */
	public boolean isQuoted(int which) {
		return tokens.get(which).quoted;
	}
	
	/**
	 * 
	 * @param which
	 * @return  the corresponding token or token value
	 */
	public String getValue(int which) {
		return tokens.get(which).value;
	}

	/**
	 * 
	 * @param which
	 * @return  end index token value
	 */
	public int getEndIndex(int which) {
		return tokens.get(which).lastCharacter;
	}

	
}
