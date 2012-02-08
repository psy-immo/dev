/**
 * AssertionFilter.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import de.tu_dresden.psy.inference.AssertionInterface;

public class AssertionFilter {

	private Pattern subjectPattern;
	private Pattern predicatePattern;
	private Pattern objectPattern;

	/**
	 * constructs the filter from the given regular expressions
	 * 
	 * @param subjectPattern
	 * @param predicatePattern
	 * @param objectPattern
	 */

	public AssertionFilter(String subjectPattern, String predicatePattern,
			String objectPattern) {
		this.subjectPattern = Pattern.compile(subjectPattern);
		this.predicatePattern = Pattern.compile(predicatePattern);
		this.objectPattern = Pattern.compile(objectPattern);
	}
	
	/**
	 * 
	 * @param assertions
	 * @return all assertions from the set that match the regular expressions of this filter
	 */

	public Set<AssertionInterface> filter(Set<AssertionInterface> assertions) {
		Set<AssertionInterface> result = new HashSet<AssertionInterface>();
		for (AssertionInterface assertion : assertions) {
			if (assertion.getSubject() instanceof String) {
				String subject = (String) assertion.getSubject();

				if (assertion.getPredicate() instanceof String) {
					String predicate = (String) assertion.getPredicate();

					if (assertion.getObject() instanceof String) {
						String object = (String) assertion.getObject();

						if (predicatePattern.matcher(predicate).matches()) {
							if (subjectPattern.matcher(subject).matches()) {
								if (objectPattern.matcher(object).matches()) {
									result.add(assertion);
								}
							}
						}
					}
				}

			}
		}
		return result;
	}
}
