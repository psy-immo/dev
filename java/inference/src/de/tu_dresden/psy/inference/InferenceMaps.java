/**
 * InferenceMaps.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * implements a meta-inference map that just calls all inference maps and joins the inferred assertions
 * @author immanuel
 *
 */

public class InferenceMaps implements InferenceMap {
	
	private Set<InferenceMap> maps;
	
	/**
	 * create a new set of inference maps
	 * @param maps  - the set is (shallow-) copied
	 */
	public InferenceMaps(Set<InferenceMap> maps) {
		this.maps = new HashSet<InferenceMap>();
		this.maps.addAll(maps);
	}

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises) {
		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();
		
		System.out.print("\n inferring: ");
		
		for (Iterator<InferenceMap> it = maps.iterator();it.hasNext();) {
			InferenceMap phi = it.next();
			System.out.print(".");
			inferred.addAll(phi.inferNew(validPremises));
		}
		
		System.out.println("");
		
		return inferred;
	}

	@Override
	public String ruleName() {
		return "(ANY)";
	}

}
