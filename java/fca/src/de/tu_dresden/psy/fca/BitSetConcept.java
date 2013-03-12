/**
 * BitSetConcept.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

/**
 * 
 * @author immo
 * 
 *         class that represent a formal concept for a BitSetContext object
 * 
 */

public class BitSetConcept implements FormalConcept {

	private BitSet os, as;

	public BitSetConcept(BitSet os, BitSet as) {
		this.os = os;
		this.as = as;
	}

	@Override
	public BitSet commonObjects() {
		return this.os;
	}

	@Override
	public BitSet commonAttributes() {
		return this.as;
	}

	@Override
	public String toString() {
		return "(" + os + "," + as + ")";
	}

	@Override
	public int compareTo(FormalConcept o) {
		BitSet attribs = o.commonAttributes();

		int boundary = Math.max(as.length(), attribs.length());

		for (int i = 0; i < boundary; ++i) {
			if (as.get(i) == true) {
				if (attribs.get(i) == false)
					return -1;
			} else if (attribs.get(i) == true)
				return 1;
		}
		return 0;
	}

	@Override
	public int cmp(OrderElement r) {
		if (r instanceof FormalConcept) {
			FormalConcept c = (FormalConcept) r;

			BitSet objs = c.commonObjects();
			boolean less = false;
			boolean more = false;

			int boundary = Math.max(os.length(), objs.length());

			for (int i = 0; i < boundary; ++i) {
				if (os.get(i)) {
					if (objs.get(i) == false)
						more = true;
				} else if (objs.get(i))
					less = true;
				if (more && less)
					return OrderElement.incomparable;
			}

			if (less)
				return OrderElement.lessObjects;
			if (more)
				return OrderElement.moreObjects;
			return OrderElement.equalConcept;
		} else
			return OrderElement.incomparable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((as == null) ? 0 : as.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BitSetConcept other = (BitSetConcept) obj;
		if (as == null) {
			if (other.as != null) {
				return false;
			}
		} else if (!as.equals(other.as)) {
			return false;
		}
		return true;
	}

}
