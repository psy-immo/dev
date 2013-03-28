/**
 * Neighborhood.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.fca.layout;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tu_dresden.psy.fca.OrderElement;
import de.tu_dresden.psy.fca.OrderZero;

/**
 * 
 * @author immo
 * 
 *         implements the neighborhood relation for a given poset
 * 
 */

public class Neighborhood {
	private Map<Integer, OrderElement> numberToElement;
	private Map<OrderElement, Integer> elementToNumber;
	static OrderZero bottom = new OrderZero();

	/**
	 * note that upperNeighbors.get(0) is the bit set of all elements that have
	 * no lower neighbors, i.e. that are minimal wrt. the poset
	 */
	private ArrayList<BitSet> upperNeighbors;

	/**
	 * 
	 * @return size of the poset that is the basis of this structure
	 */

	public int size() {
		return this.numberToElement.size() - 1;
	}

	public Neighborhood(Set<OrderElement> poset) {
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

}
