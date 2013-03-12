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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	/**
	 * note that upperNeighbors.get(0) is the bit set of all elements that have
	 * no lower neighbors, i.e. that are minimal wrt. the poset
	 */
	private ArrayList<BitSet> upperNeighbors;

	public BitSetAscendingHasseNeighbors(Set<OrderElement> poset) {
		this.numberToElement = new TreeMap<Integer, OrderElement>();
		this.elementToNumber = new TreeMap<OrderElement, Integer>();
		int element_count = poset.size();
		this.upperNeighbors = new ArrayList<BitSet>(element_count + 1);
		this.upperNeighbors.add(new BitSet(element_count + 1));
		this.upperNeighbors.get(0).set(1, element_count + 1);
		int id = 1;
		for (OrderElement p : poset) {
			numberToElement.put(id, p);
			elementToNumber.put(p, id);
			this.upperNeighbors.add(new BitSet(element_count + 1));
			id++;
		}
	}

	private boolean moveUp(int base) {
		BitSet R = upperNeighbors.get(base);
		boolean changed = false;

		for (int p = R.nextSetBit(0); p >= 0; p = R.nextSetBit(p + 1)) {
			BitSet Ps = upperNeighbors.get(p);
			for (int q = R.nextSetBit(p + 1); q >= 0; q = R.nextSetBit(q + 1)) {
				OrderElement P = numberToElement.get(p);
				OrderElement Q = numberToElement.get(q);
				int result = P.cmp(Q);

				if ((result & OrderElement.lessThan) > 0) {
					/**
					 * P < Q
					 */
					R.clear(q);
					Ps.set(p);
				} else if ((result & OrderElement.greaterThan) > 0) {
					/**
					 * Q < P
					 */
					R.clear(p);
				}
			}
		}

		return changed;
	}
}
