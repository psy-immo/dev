/**
 * ConstrainedAssertionFilter.java, (c) 2012, Immanuel Albrecht; Dresden
 * University of Technology, Professur für die Psychologie des Lernen und
 * Lehrens
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

package de.tu_dresden.psy.inference.regexp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import de.tu_dresden.psy.inference.AssertionInterface;

/**
 * specializes AssertionFilter to check for constraints
 * 
 * @author albrecht
 * 
 */

public class ConstrainedAssertionFilter extends AssertionFilter {

	private Set<ConstraintInterface> constraints;
	private Vector<AssertionInterface> singleton_set;

	/**
	 * create an AssertionFilter with no initial constraints
	 * @param subjectPattern
	 * @param predicatePattern
	 * @param objectPattern
	 */
	
	public ConstrainedAssertionFilter(String subjectPattern,
			String predicatePattern, String objectPattern) {
		super(subjectPattern, predicatePattern, objectPattern);
		constraints = new HashSet<ConstraintInterface>();
		singleton_set = new Vector<AssertionInterface>();
		singleton_set.add(null);
	}
	
	/**
	 * add another constraint that must be met
	 * @param constraint
	 */
	
	public void addConstraint(ConstraintInterface constraint) {
		constraints.add(constraint);
	}

	@Override
	public Set<AssertionInterface> filter(Set<AssertionInterface> assertions) {
		Set<AssertionInterface> result = super.filter(assertions);
		
		/**
		 * after pattern match check the constraints
		 */

		for (ConstraintInterface constraint : constraints) {
			for (Iterator<AssertionInterface> i_assertion = result.iterator(); i_assertion
					.hasNext();) {
				AssertionInterface assertion = (AssertionInterface) i_assertion
						.next();
				singleton_set.set(0, assertion);
				if (constraint.check(singleton_set) == false) {
					i_assertion.remove();
				}
			}
		}

		return result;
	}
}
