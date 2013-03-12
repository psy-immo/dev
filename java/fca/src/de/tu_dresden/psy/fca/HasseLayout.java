/**
 * HasseLayout.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author immo
 * 
 *         this class provides a layout for Hasse diagrams
 * 
 */

public class HasseLayout {

	private BitSetAscendingHasseNeighbors nextUpperRelation;

	private Map<OrderElement, LayoutPositionVector> layout;
	private Map<OrderElement, Set<OrderElement>> nextUpSets;

	public HasseLayout(Set<OrderElement> poset) {
		this.nextUpperRelation = new BitSetAscendingHasseNeighbors(poset);
		this.nextUpSets = new TreeMap<OrderElement, Set<OrderElement>>();
		this.layout = new TreeMap<OrderElement, LayoutPositionVector>();
		for (OrderElement e : poset) {
			this.layout.put(e, new LayoutPositionVector());
		}

		Set<OrderElement> current_level = null;
		Set<OrderElement> next_level = this.nextUpperRelation.Minimal();
		int rank = 0;

		while (next_level.isEmpty() == false) {
			current_level = next_level;
			next_level = new TreeSet<OrderElement>();

			int rankX = 0;

			for (OrderElement e : current_level) {
				this.layout.get(e).rankUpTo(rank);
				this.layout.get(e).setRankX(rankX);
				this.nextUpSets
						.put(e, this.nextUpperRelation.UpperNeighbors(e));
				next_level.addAll(this.nextUpSets.get(e));
				rankX++;
			}
			rank++;
		}

	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();

		for (OrderElement e : this.layout.keySet()) {
			b.append(e + ": " + this.layout.get(e) + "\n");
		}

		return b.toString();
	}

}
