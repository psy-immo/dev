/**
 * IncomparabilityRelation.java, (c) 2013, Immanuel Albrecht; Dresden University
 * of Technology, Professur für die Psychologie des Lernen und Lehrens
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

import de.tu_dresden.psy.fca.OrderElement;

/**
 * 
 * @author immo
 * 
 *         class that calculates the incomparability relation for a given poset
 * 
 */

public class IncomparabilityRelation {
	private Map<Integer, OrderElement> numberToElement;
	private Map<OrderElement, Integer> elementToNumber;

	private ArrayList<BitSet> incomparableElements;

	public IncomparabilityRelation(Set<OrderElement> poset) {
		this.numberToElement = new TreeMap<Integer, OrderElement>();
		this.elementToNumber = new TreeMap<OrderElement, Integer>();
		int element_count = poset.size();
		this.incomparableElements = new ArrayList<BitSet>(element_count);

		int id = 0;
		for (OrderElement p : poset) {
			this.numberToElement.put(id, p);
			this.elementToNumber.put(p, id);
			this.incomparableElements.add(new BitSet(element_count));
			id++;
		}

		/**
		 * Test comparability for each pair of elements in the poset
		 */

		for (int i = 0; i < element_count; ++i) {
			OrderElement p = this.numberToElement.get(i);
			BitSet p_array = this.incomparableElements.get(i);
			for (int j = i + 1; j < element_count; ++j) {
				OrderElement q = this.numberToElement.get(j);
				if ((p.cmp(q) & OrderElement.comparableMask) == 0) {
					BitSet q_array = this.incomparableElements.get(j);

					p_array.set(j);
					q_array.set(i);
				}
			}
		}

	}

}
