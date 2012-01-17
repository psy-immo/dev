/**
 * StringEscape.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

/**
 * provides static functions that escape strings
 * 
 * @author immanuel
 * 
 */

public class StringEscape {

	/**
	 * use static only
	 */
	private StringEscape() {

	}

	/**
	 * translate \ to \\, newline to \n, tab to \t, return to \r, " to \"
	 * 
	 * @param unescaped
	 * @return escaped
	 */

	public static String escapeToJavaScript(String unescaped) {

		return unescaped.replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("\"", "\\\\\"").replaceAll("\\n", "\\\\n")
				.replaceAll("\\t", "\\\\t").replaceAll("\\r", "\\\\r");
	}

	/**
	 * translate string to java script function call of DecodeString
	 * 
	 * @param unescaped
	 * @return escaped
	 */

	public static String escapeToDecodeInJavaScript(String unescaped) {
		/**
		 * the corresponding java script function looks like this:
		 */

		// function encodeString(s){
		// var previous = Math.round(Math.random()*255);
		// var codes = [s.length, previous];
		//
		// for ( var int = 0; int < s.length; int++) {
		// var array_element = s.charCodeAt(int);
		// codes[int+2] = (array_element ^ 173)^previous;
		// previous = array_element;
		// };
		// for (var int = s.length; int < 128; int++) {
		// codes[int+2] = ((Math.round(Math.random()*255)) ^ 173 ) ^previous;
		// previous = codes[int];
		// }
		// codes[0] = codes[128]^codes[0];
		// return codes;
		// };

		int[] code = new int[2 + Math.max(unescaped.length(), 128)];

		code[0] = unescaped.length();
		code[1] = (int) Math.round(Math.random() * 255.);
		int previous = code[1];

		for (int i = 0; i < unescaped.length(); ++i) {
			int array_element = unescaped.charAt(i);
			code[i + 2] = (array_element ^ 173) ^ previous;
			previous = array_element;
		}
		for (int i = unescaped.length(); i < 128; ++i) {
			int array_element = (int) Math.round(Math.random() * 255.);
			code[i + 2] = (array_element ^ 173) ^ previous;
			previous = array_element;
		}

		code[0] = code[128] ^ code[0];

		String jsCode = "decodeString([" + code[0];
		for (int i = 1; i < code.length; ++i) {
			jsCode += ", " + code[i];
		}
		return jsCode + "])";
	}

	/**
	 * translate < to &lt;, > to &gt;, & to &amp;, " to &quot;
	 * 
	 * @param unescaped
	 * @return escaped *
	 */
	public static String escapeToHtml(String unescaped) {

		return unescaped.replaceAll("\\&", "\\&amp;")
				.replaceAll("\\<", "\\&lt;").replaceAll("\\>", "\\&gt;")
				.replaceAll("\\\"", "\\&quot;");
	}

	/**
	 * translate &lt; to <, &gt; to >, &amp; to &, &quot; to "
	 * 
	 * @param escaped
	 * @return unescaped
	 */
	public static String unescapeFromHtml(String escaped) {
		return escaped.replaceAll("\\&quot;", "\\\"")
				.replaceAll("\\&lt;", "\\<").replaceAll("\\&gt;", "\\>")
				.replaceAll("\\&amp;", "\\&");

	}
	
	
}
