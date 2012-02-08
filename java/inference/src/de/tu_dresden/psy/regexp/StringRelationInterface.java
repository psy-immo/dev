/**
 * StringRelationInterface.java, (c) 2012, Immanuel Albrecht; Dresden University
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

package de.tu_dresden.psy.regexp;

import java.util.Set;

/**
 * interface for string relations (i.e. one input string yields multiple output possibilities
 * 
 * @author albrecht
 *
 */

public interface StringRelationInterface {
	
	/**
	 * map using all inputs and all outputs
	 * 
	 * @param s
	 *            input String
	 * @return all possible result values
	 */
	public Set<String> allMaps(String s);

}
