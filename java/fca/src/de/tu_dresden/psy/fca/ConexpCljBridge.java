/**
 * ConexpCljBridge.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
package de.tu_dresden.psy.fca;

import java.io.StringReader;

/**
 * 
 * @author immo
 * 
 *         implements a bridge to conexp-clj, see:
 *         https://github.com/exot/conexp-clj and
 *         http://www.math.tu-dresden.de/~borch/
 * 
 */

public class ConexpCljBridge {

	private Compiler compiler;

	public ConexpCljBridge() {
		// Load the Clojure script -- as a side effect this initializes the
		// runtime.
		String str = "(use 'conexp.main)\n"
				+ "                                (use 'conexp.contrib.gui)\n"
				+ "                                (@(ns-resolve 'conexp.contrib.gui 'gui)\n"
				+ "                                 :default-close-operation javax.swing.JFrame/EXIT_ON_CLOSE)"
				+ "(ns user) (defn foo [a b]   (str a \" \" b))";

		Compiler.load(new StringReader(str));

		// Get a reference to the foo function.
		Var foo = RT.var("user", "foo");

		// Call it!
		Object result = foo.invoke("Hi", "there");
		System.out.println(result);
	}
}
