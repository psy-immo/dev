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
import java.util.Map;
import java.util.TreeMap;

import de.tu_dresden.psy.fca.util.BitSetMatrix;
import de.tu_dresden.psy.fca.util.ComparableBitSet;
import de.tu_dresden.psy.fca.util.Permutation;

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

		ComparableBitSet b1 = new ComparableBitSet();
		ComparableBitSet b2 = new ComparableBitSet();
		Map<ComparableBitSet, Integer> x = new TreeMap<ComparableBitSet, Integer>();


		b1.set(10);
		b2.set(10);
		b2.set(12);

		System.out.println(b1);
		x.put(b1, 1);

		System.out.println(x);

		System.out.println(b2);
		System.out.println(b1.compareTo(b2));
		System.out.println(b1.clone());

		Permutation p = new Permutation();
		p.rightSwap(2, 4);
		p.leftSwap(4, 6);
		System.out.println(p + "\n");

		Permutation q = new Permutation();
		q.rightSwap(1, 3);
		q.rightSwap(5, 7);
		System.out.println(q + "\n");

		System.out.println(p.after(q));

		BitSetMatrix m = new BitSetMatrix(100, 100);
		m.RandomizeMatrix(0.4);

		BitSetMatrix old = m.Copy();


		System.out.println("---");
		m.swapColumns(0, 1);
		m.swapRows(0, 1);

		System.out.println(m.compareTo(old));


		BitSetContext ctx = new BitSetContext(5, 5);

		ctx.RandomizeContext(0.5);


		System.out.println(ctx);

		Lattice l = ctx.conceptLattice();


		BitSetAscendingHasseNeighbors neighs = new BitSetAscendingHasseNeighbors(
				l.Elements());


		// System.out.println(neighs.Normalize().AdjacencyMatrix());
		System.out.println("Elements = " + neighs.size());
		BitSetAscendingHasseNeighbors a = neighs.PseudoNormalize();
		System.out.println("XXX");

		BitSetAscendingHasseNeighbors b = neighs.Shake().PseudoNormalize();
		// System.out.println(neighs.Shake().Normalize().AdjacencyMatrix());

		System.out.println("Compare NF? "
				+ a.AdjacencyMatrix().compareTo(b.AdjacencyMatrix()));

	}
}
