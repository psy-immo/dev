/**
 * FormalContext.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
 * You should have received a copy of the GNU General Public License aint with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.tu_dresden.psy.fca;

import java.util.BitSet;

/**
 * interface that defines a formal context
 * 
 * @author immo
 * 
 */
public interface FormalContext {
	/**
	 * 
	 * @return number of objects of the formal context
	 */
	int numberOfObjects() throws Exception;

	/**
	 * 
	 * @return number of attributes of the formal context
	 */
	int numberOfAttributes() throws Exception;

	/**
	 * 
	 * @return iterator for the objects
	 */

	Iterable<Integer> Objects() throws Exception;

	/**
	 * 
	 * @return iterator for the attributes
	 */

	Iterable<Integer> Attributes() throws Exception;

	/**
	 * 
	 * @return name of the formal context
	 */
	String nameOfContext() throws Exception;

	/**
	 * 
	 * @param o
	 *            number identifying the object
	 * @return the name of the object
	 */
	String objectName(int o) throws Exception;

	/**
	 * 
	 * @param a
	 *            number identifying the attribute
	 * @return the name of the attribute
	 */
	String attributeName(int a) throws Exception;

	/**
	 * 
	 * @param name
	 *            name of the object
	 * 
	 * @return number of the desired object
	 */
	int objectByName(String name) throws Exception;

	/**
	 * @param name
	 *            name of the attribute
	 * 
	 * @return number of the desired attribute
	 */
	int attributeByName(String name) throws Exception;

	/**
	 * 
	 * @param o
	 *            object
	 * @param a
	 *            attribute
	 * @return oIa for the context (O,A,I)
	 */
	boolean incidenceRelation(int o, int a) throws Exception;

	/**
	 * 
	 * @param o
	 *            object
	 * @return the object's attribute row
	 */
	BitSet objectRow(int o) throws Exception;

	/**
	 * 
	 * @param a
	 *            attribute
	 * @return the attribute's object column
	 */
	BitSet attributeCol(int a) throws Exception;

	/**
	 * 
	 * @param os
	 *            objects
	 * @return common attributes
	 */
	BitSet commonAttributes(BitSet os) throws Exception;

	/**
	 * 
	 * @param as
	 *            attributes
	 * @return common objects
	 */
	BitSet commonObjects(BitSet as) throws Exception;

	/**
	 * 
	 * @param os
	 *            objects
	 * @return generated concept
	 */

	FormalConcept closeObjects(BitSet os) throws Exception;

	/**
	 * 
	 * @param as
	 *            attributes
	 * @return generated concept
	 */

	FormalConcept closeAttributes(BitSet as) throws Exception;

	/**
	 * 
	 * @return the minimal concept
	 */

	FormalConcept bottomConcept() throws Exception;

	/**
	 * 
	 * @return the maximal concept
	 */

	FormalConcept topConcept() throws Exception;

	/**
	 * 
	 * @param x
	 *            concept
	 * @return next concept with respect to lectic order, or null if x ==
	 *         topConcept()
	 */
	FormalConcept nextClosure(FormalConcept x) throws Exception;
}
