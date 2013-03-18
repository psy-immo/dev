/**
 * BitSetAscendingHasseNeighbors.java, (c) 2013, Immanuel Albrecht; Dresden
 * University of Technology, Professur für die Psychologie des Lernen und
 * Lehrens
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tu_dresden.psy.fca.util.BitSetMatrix;
import de.tu_dresden.psy.fca.util.Permutation;

/**
 * 
 * @author immo
 * 
 *         class that sorts a set of ordered elements such that the upper
 *         neighbor relation may be seen
 * 
 */

public class BitSetAscendingHasseNeighbors {

	private Map<Integer, OrderElement> numberToElement;
	private Map<OrderElement, Integer> elementToNumber;
	static OrderZero bottom = new OrderZero();

	/**
	 * note that upperNeighbors.get(0) is the bit set of all elements that have
	 * no lower neighbors, i.e. that are minimal wrt. the poset
	 */
	private ArrayList<BitSet> upperNeighbors;

	private BitSetAscendingHasseNeighbors(
			Map<Integer, OrderElement> numberToElement,
			Map<OrderElement, Integer> elementToNumber,
			ArrayList<BitSet> upperNeighbors) {
		this.numberToElement = numberToElement;
		this.elementToNumber = elementToNumber;
		this.upperNeighbors = upperNeighbors;
	}

	public BitSetAscendingHasseNeighbors(Set<OrderElement> poset) {
		this.numberToElement = new TreeMap<Integer, OrderElement>();
		this.elementToNumber = new TreeMap<OrderElement, Integer>();
		this.numberToElement.put(0, bottom);
		this.elementToNumber.put(bottom, 0);
		int element_count = poset.size();
		this.upperNeighbors = new ArrayList<BitSet>(element_count + 1);
		this.upperNeighbors.add(new BitSet(element_count + 1));
		this.upperNeighbors.get(0).set(1, element_count + 1);
		int id = 1;
		for (OrderElement p : poset) {
			this.numberToElement.put(id, p);
			this.elementToNumber.put(p, id);
			this.upperNeighbors.add(new BitSet(element_count + 1));
			id++;
		}

		BitSet continueMoving = new BitSet(element_count + 1);

		continueMoving.set(0);

		while (continueMoving.isEmpty() == false) {
			for (int base = continueMoving.nextSetBit(0); base >= 0; base = continueMoving
					.nextSetBit(base + 1)) {
				continueMoving.clear(base);
				if (this.moveUp(base)) {
					continueMoving.or(this.upperNeighbors.get(base));
				}
			}
		}
	}

	/**
	 * 
	 * compares all elements that are bigger than base pairwise, and moves up
	 * those elements that are bigger than the compared elements
	 * 
	 * @param base
	 *            element
	 * @return whether we moved elements up
	 */

	private boolean moveUp(int base) {
		BitSet R = this.upperNeighbors.get(base);
		boolean changed = false;

		OUTER_LOOP: for (int p = R.nextSetBit(0); p >= 0; p = R
				.nextSetBit(p + 1)) {

			OrderElement P = this.numberToElement.get(p);
			for (int q = R.nextSetBit(p + 1); q >= 0; q = R.nextSetBit(q + 1)) {
				OrderElement Q = this.numberToElement.get(q);
				int result = P.cmp(Q);

				if ((result & OrderElement.lessThan) > 0) {
					/*
					 * base < P < Q, thus (not base <. Q)
					 */
					R.clear(q);

					/*
					 * find all O < Q, and set O <. Q
					 */

					for (int o = R.nextSetBit(0); o >= 0; o = R
							.nextSetBit(o + 1)) {
						OrderElement O = this.numberToElement.get(o);
						if ((O.cmp(Q) & OrderElement.lessThan) > 0) {
							this.upperNeighbors.get(o).set(q);
						}
					}
					changed = true;
				} else if ((result & OrderElement.greaterThan) > 0) {
					/*
					 * base < Q < P, thus (not base <. P)
					 */
					R.clear(p);

					/*
					 * find all O < P, and set O <. P
					 */

					for (int o = R.nextSetBit(0); o >= 0; o = R
							.nextSetBit(o + 1)) {
						OrderElement O = this.numberToElement.get(o);
						if ((O.cmp(P) & OrderElement.lessThan) > 0) {
							this.upperNeighbors.get(o).set(p);
						}
					}

					changed = true;
					continue OUTER_LOOP;
				}
			}
		}

		return changed;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < this.upperNeighbors.size(); ++i) {
			BitSet uppers = this.upperNeighbors.get(i);
			if (uppers.isEmpty() == false) {

				out.append(this.numberToElement.get(i));
				out.append(" <. ");

				boolean first = true;
				for (int o = uppers.nextSetBit(0); o >= 0; o = uppers
						.nextSetBit(o + 1)) {
					if (first) {
						first = false;
					} else {
						out.append(", ");
					}
					out.append(this.numberToElement.get(o));

				}
				out.append("\n");
			}
		}

		return out.toString();
	}

	/**
	 * 
	 * @return dot file contents that generate the current Hasse diagram
	 */
	public String asDotCode(boolean labels) {
		StringBuffer out = new StringBuffer();

		out.append("digraph HasseDiagramm {\n");
		out.append("rankdir=BT;\n");

		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}
			String label = this.numberToElement.get(n).toString();

			out.append("node" + n);
			out.append("[");
			if (labels) {

				// out.append("shape=circle,");
				out.append("label=\"" + label + "\"");

			} else {
				out.append("label=\"\",shape=point,color=red,width=.1,height=.1");
			}
			out.append("];\n");
		}
		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}
			BitSet upper = this.upperNeighbors.get(n);
			for (int o = upper.nextSetBit(0); o >= 0; o = upper
					.nextSetBit(o + 1)) {
				out.append("node" + n + " -> node" + o + "[dir=none];\n");
			}
		}

		out.append("}");

		return out.toString();
	}

	/**
	 * 
	 * @return the set of minimal elements
	 */

	public Set<OrderElement> Minimal() {
		Set<OrderElement> s = new TreeSet<OrderElement>();
		BitSet x = this.upperNeighbors.get(0);
		for (int o = x.nextSetBit(0); o >= 0; o = x.nextSetBit(o + 1)) {
			s.add(this.numberToElement.get(o));
		}
		return s;
	}

	/**
	 * 
	 * @param p
	 *            element
	 * @return all upper neighbors of p, i.e. all q with p <. q
	 */

	public Set<OrderElement> UpperNeighbors(OrderElement p) {
		Set<OrderElement> s = new TreeSet<OrderElement>();
		BitSet x = this.upperNeighbors.get(this.elementToNumber.get(p));
		for (int o = x.nextSetBit(0); o >= 0; o = x.nextSetBit(o + 1)) {
			s.add(this.numberToElement.get(o));
		}
		return s;
	}

	/**
	 * change the order element numbers by random permutation
	 * 
	 * @return a copy of this object, but with different element numbering
	 */

	public BitSetAscendingHasseNeighbors Shake() {
		int n_elements = this.elementToNumber.size();
		Permutation p = new Permutation(n_elements-1);
		Map<Integer, OrderElement> numberToElement;
		Map<OrderElement, Integer> elementToNumber;

		ArrayList<BitSet> upperNeighbors;
		numberToElement = new TreeMap<Integer, OrderElement>();
		elementToNumber = new TreeMap<OrderElement, Integer>();

		numberToElement.put(0, bottom);
		elementToNumber.put(bottom, 0);

		for(Integer n: this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}

			OrderElement e = this.numberToElement.get(n);
			int new_n = p.Forward(n-1)+1;

			numberToElement.put(new_n, e);
			elementToNumber.put(e, new_n);
		}

		upperNeighbors = new ArrayList<BitSet>();
		for (int i=0;i<n_elements;++i) {
			int old_i = i;
			if (i!=0) {
				old_i = p.Backward(i - 1) + 1;
			}
			BitSet old_u = this.upperNeighbors.get(old_i);
			BitSet u = new BitSet(n_elements);
			for (int j = 0; j < n_elements; ++j) {
				int old_j = j;
				if (j != 0) {
					old_j = p.Backward(j - 1) + 1;
				}
				if (old_u.get(old_j)) {
					u.set(j);
				}
			}
			upperNeighbors.add(u);
		}

		return new BitSetAscendingHasseNeighbors(numberToElement,
				elementToNumber, upperNeighbors);
	}

	/**
	 * 
	 * @return the adjacency matrix of this object without the bottom element
	 */

	public BitSetMatrix AdjacencyMatrix() {
		Iterator<BitSet> i = this.upperNeighbors.iterator();
		i.next();
		int n_elements = this.elementToNumber.size();
		return new BitSetMatrix(n_elements - 1, n_elements - 1, i, 1);
	}

}
