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

import java.io.FileNotFoundException;
import java.io.IOException;

import de.tu_dresden.psy.fca.util.BitSetMatrix;

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

		BitSetMatrix m = new BitSetMatrix(100, 100);
		m.RandomizeMatrix(0.4);

		BitSetMatrix old = m.Copy();


		System.out.println("---");
		m.swapColumns(0, 1);
		m.swapRows(0, 1);

		System.out.println(m.compareTo(old));


		/*
		 * ConexpCljBridge mygui = new ConexpCljBridge();
		 * System.out.println("Send"); mygui.sendCommands("(+ 2 3)");
		 * System.out.println("Wait"); System.out.println(mygui.waitOutput());
		 * System.out.println("Done"); mygui.sendCommands("(use 'conexp.main)");
		 * mygui.sendCommands("(use 'conexp.contrib.gui)"); mygui.sendCommands(
		 * "(@(ns-resolve 'conexp.contrib.gui 'gui) :default-close-operation javax.swing.JFrame/EXIT_ON_CLOSE)"
		 * ); mygui.waitForResults(); System.out.println(mygui.readOutput());
		 * 
		 * BitSetContext ctx = new BitSetContext(FileFormat.Burmeister,
		 * "/tmp/myRandom.bur"); // ctx = new BitSetContext(6, 6); //
		 * ctx.RandomizeContext(0.5);
		 * 
		 * System.out.println(ctx);
		 * 
		 * Lattice l = ctx.conceptLattice();
		 * 
		 * BitSetAscendingHasseNeighbors neighs = new
		 * BitSetAscendingHasseNeighbors( l.Elements());
		 * 
		 * System.out.println(l.join(l.bottom(), l.top()));
		 * System.out.println(l.meet(l.bottom(), l.top()));
		 * System.out.println(neighs.asDotCode(true));
		 * 
		 * try { BufferedWriter out = new BufferedWriter(new FileWriter(
		 * "/tmp/lattice.dot")); out.write(neighs.asDotCode(false));
		 * out.close(); } catch (IOException e) { }
		 * 
		 * HasseLayout h = new HasseLayout(l.Elements()); System.out.println(h);
		 * 
		 * try { BufferedWriter out = new BufferedWriter(new FileWriter(
		 * "/tmp/context.csv")); out.write(ctx.toCSV()); out.close(); } catch
		 * (IOException e) { }
		 * 
		 * int count = 0, count2 = 0;
		 * 
		 * long startTime0 = System.currentTimeMillis();
		 * 
		 * System.out.println("NextClosure:"); for (int q = 0; q < 10; ++q) {
		 * count = 0; for (FormalConcept b = ctx.bottomConcept(); b != null; b =
		 * ctx .nextClosure(b)) { count++; } }
		 * 
		 * long startTime = System.currentTimeMillis();
		 * 
		 * System.out.println("NextClosureWorseThanVanilla:"); for (int q = 0; q
		 * < 10; ++q) { count = 0; for (FormalConcept b = ctx.bottomConcept(); b
		 * != null; b = ctx .nextClosureWorseThanVanilla(b)) { count++; } }
		 * 
		 * long startTime2 = System.currentTimeMillis();
		 * 
		 * System.out.println("NextClosureVanilla:"); for (int q = 0; q < 10;
		 * ++q) { count2 = 0;
		 * 
		 * for (FormalConcept b = ctx.bottomConcept(); b != null; b = ctx
		 * .nextClosureVanilla(b)) { count2++; } }
		 * 
		 * long stopTime = System.currentTimeMillis();
		 * 
		 * System.out.println(count + " concepts vs. " + count2 +
		 * " vanilla concepts.");
		 * 
		 * System.out.println((startTime - startTime0) + " vs. " + (startTime2 -
		 * startTime) + " vs. " + (stopTime - startTime2));
		 */
	}
}
