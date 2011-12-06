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
 * @author immanuel
 *
 */

public class StringEscape {

	/**
	 * use static only
	 */
	private StringEscape() {

	}

	public static String escapeToJavaScript(String unescaped) {
		/**
		 * translate         to
		 *           \         \\
		 *           newline   \n
		 *           tab       \t
		 *           return    \r
		 *           "         \"
		 */
		return unescaped.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"")
				.replaceAll("\\n", "\\\\n").replaceAll("\\t", "\\\\t")
				.replaceAll("\\r", "\\\\r");
	}
	
	public static String escapeToHtml(String unescaped) {
		/**
		 * translate         to
		 *           <         &lt;
		 *           >         &gt;
		 *           &         &amp;
		 *           "         &quot;
		 */
		return unescaped.replaceAll("\\&","\\&amp;").replaceAll("\\<", "\\&lt;").replaceAll("\\>", "\\&gt;").
				replaceAll("\\\"","\\&quot;");
	}
}
