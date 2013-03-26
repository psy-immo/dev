/**
 * Main.java, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import de.tu_dresden.psy.fca.layout.HasseDiagram;

/**
 * 
 * @author immo
 * 
 *         this class contains the test program main function
 * 
 */

public class Main {
	public static void main(String[] args) throws Exception,
			FileNotFoundException, IOException {

		BitSetContext ctx = new BitSetContext(8, 8);

		ctx.RandomizeContext(0.3);

		System.out.println(ctx);

		Lattice l = ctx.conceptLattice();

		HasseDiagram diag = new HasseDiagram(l.Elements());

		System.out.println(diag.toSVG());

		FileWriter fileWriter = null;
		try {
			String content = diag.toHtml();
			File newTextFile = new File("/tmp/test.html");
			fileWriter = new FileWriter(newTextFile);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {

		} finally {
			try {
				fileWriter.close();
			} catch (IOException ex) {

			}
		}

	}
}
