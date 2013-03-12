/**
 * Lattice.java, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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

import java.util.Set;

/**
 * 
 * @author immo
 * 
 *         interface for lattice structures, i.e. ordered sets which have infima
 *         and suprema for all subsets of elements
 * 
 */

public interface Lattice {

	/**
	 * 
	 * @param P
	 *            set of lattice elements
	 * @return greatest lower bound of P
	 */
	OrderElement Inf(Set<OrderElement> P);

	/**
	 * 
	 * @param P
	 *            set of lattice elements
	 * @return smallest upper bound of P
	 */

	OrderElement Sup(Set<OrderElement> P);

	/**
	 * 
	 * @param p
	 * @param q
	 * @return p v q
	 */
	OrderElement join(OrderElement p, OrderElement q);

	/**
	 * 
	 * @param p
	 * @param q
	 * @return p ^ q
	 */

	OrderElement meet(OrderElement p, OrderElement q);

	/**
	 * 
	 * @return biggest element of the lattice
	 */

	OrderElement top();

	/**
	 * 
	 * @return smallest element of the lattice
	 */

	OrderElement bottom();

	/**
	 * 
	 * @return the support of the lattice
	 */
	Set<OrderElement> Elements();
}
