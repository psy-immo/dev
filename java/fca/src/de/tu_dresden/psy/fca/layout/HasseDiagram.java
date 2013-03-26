/**
 * HasseDiagram.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import de.tu_dresden.psy.fca.FormalConcept;
import de.tu_dresden.psy.fca.OrderElement;

/**
 * 
 * @author immo
 * 
 *         objects of this class represent a (k+1)-dimensional HASSE diagram
 * 
 */

public class HasseDiagram {

	private int k;
	private Map<Integer, OrderElement> fromNumber;
	private Map<OrderElement, Integer> toNumber;
	private ArrayList<BitSet> vectors;

	public HasseDiagram(Set<OrderElement> poset) {
		int x = 0;
		this.fromNumber = new TreeMap<Integer, OrderElement>();
		this.toNumber = new TreeMap<OrderElement, Integer>();
		for (OrderElement e : poset) {
			this.fromNumber.put(x, e);
			this.toNumber.put(e, x);
			++x;
		}

		/**
		 * check whether we already have formal concepts
		 */

		boolean fcs = true;
		int nbrO = 0, nbrA = 0;
		for (OrderElement e : poset) {
			if (!(e instanceof FormalConcept)) {
				fcs = false;
				break;
			} else {
				FormalConcept c = (FormalConcept) e;
				if (nbrO < c.contextObjectCount()) {
					nbrO = c.contextObjectCount();
				}
				if (nbrA < c.contextAttributeCount()) {
					nbrA = c.contextAttributeCount();
				}
			}
		}

		this.vectors = new ArrayList<BitSet>();

		if (fcs) {
			/**
			 * use the original context
			 */

			if (nbrA < nbrO) {
				/**
				 * use inverse attribute vectors
				 */
				this.k = nbrA - 1;
				for (int i = 0; i < x; ++i) {
					BitSet b = new BitSet(nbrA);
					b.set(0, nbrA);
					FormalConcept c = (FormalConcept) this.fromNumber.get(i);
					b.xor(c.commonAttributes());
					this.vectors.add(b);
				}
			} else {
				/**
				 * use object vectors
				 */
				this.k = nbrO - 1;
				for (int i = 0; i < x; ++i) {
					BitSet b = new BitSet(nbrO);
					FormalConcept c = (FormalConcept) this.fromNumber.get(i);
					b.or(c.commonObjects());
					this.vectors.add(b);
				}
			}
		} else {
			/**
			 * use the standard context of the poset
			 */
			this.k = x - 1;
			for (int i = 0; i < x; ++i) {
				BitSet b = new BitSet(x);
				for (int j = 0; j < x; ++j) {
					if ((this.fromNumber.get(j).cmp(this.fromNumber.get(i)) & OrderElement.LessEq) > 0) {
						b.set(j);
					}
				}
				this.vectors.add(b);
			}
		}

	}

	public int getDimension() {
		return this.k + 1;
	}

}
