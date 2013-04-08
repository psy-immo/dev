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
import de.tu_dresden.psy.fca.layout.Neighborhood;
import de.tu_dresden.psy.fca.util.DoubleMatrix;
import de.tu_dresden.psy.fca.util.DoubleMatrix.SpecialMatrix;
import de.tu_dresden.psy.fca.util.DoubleVector;

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

		DoubleMatrix m = new DoubleMatrix(2, 8, SpecialMatrix.HasseDefault);
		System.out.println(m);
		System.out.println(m.rMult(new DoubleVector(8, 2)));

		BitSetContext ctx = new BitSetContext(8, 8);

		ctx.RandomizeContext(0.3);

		System.out.println(ctx);

		Lattice l = ctx.conceptLattice();

		Neighborhood n = new Neighborhood(l.Elements());

		System.out.println("ELTS=" + l.Elements().size());
		System.out.println("REDUCED ELTS=" + n.MeetIrreducibles().size());

		System.out.println("IRREDUCIBLES = " + n.MeetIrreducibles());

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
