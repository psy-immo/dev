/**
 * OrderElement.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

/**
 * 
 * @author immo
 * 
 *         interface that provides comparison for elements of (partially)
 *         ordered sets, i.e. sets with a reflexive and transitive relation on
 *         them.
 * 
 */

public interface OrderElement extends Comparable<OrderElement> {

	static int lessObjects = 1;
	static int lessThan = 1;
	static int moreAttributes = 1;
	static int equalConcept = 2;
	static int moreObjects = 4;
	static int lessAttributes = 4;
	static int greaterThan = 1;
	static int incomparable = 16;
	static int comparableMask = 1 | 2 | 4;
	static int LessEq = 1 | 2;
	static int GreaterEq = 4 | 2;

	/**
	 * compare this concept to another concept (of the same context and
	 * corresponding concept lattice object)
	 * 
	 * @param r
	 *            other concept
	 * @return any of the ints above, so a.cmp(b) == lessObjects means, that
	 *         a.commonObjects() is a subset of b.commonObjects()
	 * 
	 */

	public abstract int cmp(OrderElement r);

}