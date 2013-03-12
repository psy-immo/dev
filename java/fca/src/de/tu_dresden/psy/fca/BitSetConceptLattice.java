/**
 * BitSetConceptLattice.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author immo
 * 
 *         implements the lattice structure of a BitSetContext
 * 
 */

public class BitSetConceptLattice implements Lattice {

	private Set<BitSetConcept> elements;
	private BitSetConcept topEl, bottomEl;
	private BitSetContext K;

	public BitSetConceptLattice(BitSetContext K) {
		this.K = K;
		this.elements = new TreeSet<BitSetConcept>();
		try {
			this.bottomEl = (BitSetConcept) K.bottomConcept();
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		try {
			this.topEl = (BitSetConcept) K.topConcept();
		} catch (Exception e) {

			e.printStackTrace();
		}
		try {
			for (FormalConcept b = this.bottomEl; b != null; b = K
					.nextClosureVanilla(b)) {
				this.elements.add((BitSetConcept) b);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	public OrderElement Inf(Set<OrderElement> P) {
		int nG = 0;
		try {
			nG = this.K.numberOfObjects();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BitSet g = new BitSet(nG);
		g.set(0, nG);
		for (OrderElement p : P) {
			BitSetConcept b = (BitSetConcept) p;
			g.and(b.commonObjects());
		}

		try {
			return this.K.closeObjects(g);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OrderElement Sup(Set<OrderElement> P) {
		int nM = 0;
		try {
			nM = this.K.numberOfAttributes();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BitSet m = new BitSet(nM);
		m.set(0, nM);
		for (OrderElement p : P) {
			BitSetConcept b = (BitSetConcept) p;
			m.and(b.commonObjects());
		}

		try {
			return this.K.closeAttributes(m);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OrderElement join(OrderElement p, OrderElement q) {
		BitSetConcept P = (BitSetConcept) p;
		BitSetConcept Q = (BitSetConcept) q;
		BitSet m = BitSet.valueOf((P.commonAttributes().toByteArray()));

		m.and(Q.commonAttributes());

		try {
			return this.K.closeAttributes(m);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public OrderElement meet(OrderElement p, OrderElement q) {
		BitSetConcept P = (BitSetConcept) p;
		BitSetConcept Q = (BitSetConcept) q;
		BitSet g = BitSet.valueOf((P.commonObjects().toByteArray()));

		g.and(Q.commonObjects());

		try {
			return this.K.closeObjects(g);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OrderElement top() {
		return this.topEl;
	}

	@Override
	public OrderElement bottom() {
		return this.bottomEl;
	}

	@Override
	public Set<OrderElement> Elements() {

		return new TreeSet<OrderElement>(this.elements);
	}

}
