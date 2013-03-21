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

public class ComparableBitSet extends BitSet implements
Comparable<Object> {

	@Override
	public int compareTo(Object obj) {
		/**
		 * xor with this.compareTo(this) will delete the information!
		 */

		if (this == obj) {
			return 0;
		}

		if (obj instanceof BitSet) {
			BitSet o = (BitSet) obj;
			// System.out.println("B4: " + this + " vs " + o);
			this.xor(o);
			// System.out.println("Diff: " + this);
			int i = this.nextSetBit(0);
			this.xor(o);
			// System.out.println("After: " + this);

			if (i < 0) {
				// System.out.println(this + "==" + o + "!");
				return 0;
			}

			if (this.get(i)) {
				// System.out.println(this + ">" + o + "!");
				return 1;
			}
			// System.out.println(this + "<" + o + "!");
			return -1;
		}
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return this.compareTo(obj) == 0;
	}

}
