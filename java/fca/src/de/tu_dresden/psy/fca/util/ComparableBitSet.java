/**
 * ComparableBitSet.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.fca.util;

import java.util.BitSet;

/**
 * 
 * @author immo
 * 
 *         implements the Comparable interface for BitSet
 * 
 */

public class ComparableBitSet extends BitSet implements Comparable<BitSet> {

	@Override
	public int compareTo(BitSet o) {
		this.xor(o);
		if (this.isEmpty()) {
			this.xor(o);
			return 0;
		}
		int i = this.nextSetBit(0);
		this.xor(o);
		if (this.get(i)) {
			return 1;
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BitSet) {
			BitSet o = (BitSet) obj;
			return this.compareTo(o) == 0;
		}

		return super.equals(obj);
	}
}
