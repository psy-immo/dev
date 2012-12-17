/**
 * EditorApplet.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.efml.editor;

import java.applet.Applet;

/**
 * implements an interface to the webbrowser that exhibits the compiler to the
 * editor web page
 * 
 * @author albrecht
 * 
 */

public class EditorApplet extends Applet {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 2929417487951462196L;

	private StringCompiler compiler;

	/**
	 * Compile some efml code, to be done before content retrieval
	 * 
	 * @param efml
	 *            efml document as string
	 * @return errors that occured during compilation
	 */

	public String compileEfml(String efml) {
		return compiler.compileEfml(efml);
	}

	/**
	 * 
	 * @return previously generated html content
	 */

	public String getHtml() {
		return compiler.getHtml();
	}
}
