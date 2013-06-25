/**
 * InferenceMap.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.util.Set;

/**
 * provides common interface for difference inference methods
 * 
 * @author albrecht
 *
 */

public interface InferenceMap {

	/**
	 * do one inference step
	 * 
	 * @param validPremises
	 *            set of assertions considered to be valid
	 * @param limit
	 *            excess limit for inference
	 * @return a set of (new) assertions that can be inferred from the valid
	 *         premises
	 */
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit);

	/**
	 * 
	 * @return name of the inference rule
	 */
	public String ruleName();

	/**
	 * 
	 * @return true, if this rule has been marked trivial
	 */
	public boolean isTrivial();
}
