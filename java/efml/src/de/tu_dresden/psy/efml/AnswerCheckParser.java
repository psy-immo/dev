/**
 * AnswerCheckParser.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * parses terms from &lt;answer>&lt;check>-tags
 * 
 * @author immanuel
 * 
 */
public class AnswerCheckParser {

	private AnswerCheckTokenizer tokens;
	private ArrayList<Integer> levels;
	private String inputTerm;

	public AnswerCheckParser(String term) {

		this.inputTerm = term;

		/**
		 * run tokenizer on the term
		 */
		this.tokens = new AnswerCheckTokenizer(term);

		/**
		 * levelize the term, i.e. ( -> level up, ) -> level down
		 */

		int current_level = 0;

		levels = new ArrayList<Integer>();

		for (Iterator<AnswerCheckTokenizer.Token> it = tokens.iterator(); it
				.hasNext();) {
			AnswerCheckTokenizer.Token token = it.next();
			if (token.quoted == false) {
				if (token.value.equalsIgnoreCase("(")
						|| token.value.equalsIgnoreCase("[")
						|| token.value.equalsIgnoreCase("{")) {
					current_level += 1;
				}
			}

			levels.add(current_level);
			System.out.println(token.value + " " + current_level);

			if (token.quoted == false) {
				if (token.value.equalsIgnoreCase(")")
						|| token.value.equalsIgnoreCase("]")
						|| token.value.equalsIgnoreCase("}")) {
					current_level -= 1;
				}
			}
		}
	}

	/**
	 * checks the syntax
	 * 
	 * @return null, if no syntax errors are found, otherwise error description
	 */
	public String checkSyntax() {
		return checkSyntaxTree(0, this.tokens.length());
	}

	/**
	 * checks the syntax of a part, for internal usage only!
	 * 
	 * @param first
	 *            first element of the part to check
	 * @param last
	 *            element after the last element of the part that is to check
	 * @return null, if no syntax errors are found, otherwise error description
	 */
	private String checkSyntaxTree(int first, int last) {

		/**
		 * the empty string is a valid term
		 */
		if (first >= last)
			return null;

		String error_description = "token utterly unrecognized";
		int where = tokens.getEndIndex(first);

		/**
		 * get the local depth of the term
		 */
		int local_depth = levels.get(first);
		for (int c = first; c < last; ++c) {
			if (levels.get(c) < local_depth) {
				local_depth = levels.get(c);
			}
		}

		/**
		 * create intermediate sub term structure
		 */

		ArrayList<Integer> subterm_start = new ArrayList<Integer>();
		ArrayList<Integer> subterm_end = new ArrayList<Integer>();
		ArrayList<Boolean> subterm_rootlevel = new ArrayList<Boolean>();

		int start = first;
		boolean rootlevel = levels.get(first) == local_depth;

		for (int end = first; end < last; ++end) {
			if ((levels.get(end) == local_depth) || (rootlevel)) {
				subterm_start.add(start);
				subterm_end.add(end);
				subterm_rootlevel.add(rootlevel);

				rootlevel = levels.get(end) == local_depth;
				start = end;
			}
		}

		subterm_start.add(start);
		subterm_end.add(last);
		subterm_rootlevel.add(rootlevel);

		for (int c = 0; c < subterm_rootlevel.size(); ++c) {
			if (subterm_rootlevel.get(c) == true) {
				where = tokens.getEndIndex(subterm_start.get(c));
				break;
			}
		}

		/**
		 * check the subterm/token structure
		 */

		if (subterm_rootlevel.get(0) == false) {
			/**
			 * term begins with subterm, could be n-ary "and", or n-ary "or"
			 * term
			 */

			String middle_token = tokens.getValue(subterm_start.get(1));
			boolean operator_known = false;

			if (middle_token.equalsIgnoreCase("and") == true) {
				/**
				 * n-ary "and" operator
				 */

				operator_known = true;

			} else if (middle_token.equalsIgnoreCase("or") == true) {
				/**
				 * n-ary "or" operator
				 */

				operator_known = true;
			}

			/**
			 * check for alternating structure: (subterm token)+ subterm 
			 */

			if (operator_known) {

				for (int i = 0; i < subterm_rootlevel.size(); ++i) {
					if (i % 2 == 0) {
						if (subterm_rootlevel.get(i) != false) {
							error_description = "Token should be in parenthesis";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						}
					} else {

						if (subterm_rootlevel.get(i) != true) {
							/**
							 * this code will never be reached according to the
							 * upper subterm by level algorithm,
							 * 
							 * we will keep it anyway
							 */
							error_description = "Two terms without connecting token";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						} else if (middle_token.equalsIgnoreCase(tokens
								.getValue(subterm_start.get(i))) != true) {
							error_description = "Connective '" + middle_token
									+ "' may not be mixed with '"
									+ tokens.getValue(subterm_start.get(i))
									+ "'";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						}
					}
				}

				if (subterm_rootlevel.size() % 2 == 0) {
					error_description = "Term connective without right hand term";
					where = tokens.getEndIndex(subterm_start.get(subterm_start
							.size() - 1));

				} else {
					/**
					 * and/or n-ary term well-formed, if subterms are
					 * well-formed
					 */

					for (int i = 0; i < subterm_rootlevel.size(); i += 2) {
						String error = checkSyntaxTree(subterm_start.get(i),
								subterm_end.get(i));
						if (error != null)
							return error;
					}

					return null;
				}
			} else {
				error_description = "Term connective unknown";
				where = tokens.getEndIndex(subterm_start.get(1));
			}

		} else {
			/**
			 * term begins with a token
			 */
			if (tokens.isQuoted(first) == true) {
				/**
				 * term begins with string literal
				 */

			} else {
				String first_token = tokens.getValue(first);

				if (first_token.equalsIgnoreCase("(") == true) {
					if ((tokens.isQuoted(last - 1) == false)
							&& (tokens.getValue(last - 1).equalsIgnoreCase(")"))) {
						/**
						 * term in parenthesis is valid, if the enclosed subterm
						 * is valid
						 */
						return checkSyntaxTree(first + 1, last - 1);
					} else {
						error_description = "Closing parenthesis is missing";
						where = tokens.getEndIndex(last - 1);

					}
				} else if ((first_token.equalsIgnoreCase("or") == true)||(first_token.equalsIgnoreCase("and") == true)) {
					error_description = "Term connective without left hand term";
					where = tokens.getEndIndex(first);
					
				} 
			}
		}

		/**
		 * format error message
		 */

		String message = "Syntax-Error: " + error_description
				+ " just before the " + (2 + where) + "th character: ";

		int end_index = where;

		if (where > 16) {
			message += "[...]";
		} else
			where = 16;

		int start_index = where - 16;

		message += inputTerm.substring(start_index, end_index + 1);

		message += " <--HERE--|";

		if (end_index + 1 < inputTerm.length()) {

			if (inputTerm.length() - end_index - 1 > 16) {
				message += " "
						+ inputTerm.substring(end_index + 1, end_index + 17)
						+ "[...]";
			} else
				message += " " + inputTerm.substring(end_index + 1);
		}
		return message;
	}

	public static void main(String[] args) {
		AnswerCheckParser parsed = new AnswerCheckParser(
				"() or () or () or ( () and ( and ) )");
		System.out.println(parsed.checkSyntax());
	}

}
