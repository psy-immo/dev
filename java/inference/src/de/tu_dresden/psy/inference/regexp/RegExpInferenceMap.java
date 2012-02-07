/**
 * RegExpInferenceMap.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
import java.util.Vector;

import de.tu_dresden.psy.inference.Assertion;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.regexp.SplittedStringRelation;

/**
 * implements inference on String-Assertions via regular expressions
 * 
 * @author albrecht
 *
 */
public class RegExpInferenceMap implements InferenceMap {
	
	private Vector<AssertionFilter> premiseForms;
	
	/**
	 * implements a check whether given premiseForms are compatible
	 * @author albrecht
	 */

	
	public static class IsCompatibleChecker {
		private Assertion.AssertionPart leftPart;
		private Assertion.AssertionPart rightPart;
		private int leftIndex;
		private int rightIndex;
		
		private SplittedStringRelation phi;
		
		/**
		 * 
		 * @param premises
		 * @return true, if the premises are compatible
		 */
		public boolean check(Vector<Assertion> premises) {
			Object left = Assertion.getAssertionPart(premises.get(leftIndex),leftPart);
			Object right = Assertion.getAssertionPart(premises.get(rightIndex),rightPart);
			if (phi != null) {
				if (left instanceof String) {
					String x = (String) left;
					return phi.allMaps(x).contains(right);
				} else return false;
			} else
				return left.equals(right);
		}
	}
	
	private Set<IsCompatibleChecker> checkPremises;
	
	/**
	 * implements the recombination of valid and checked premises into new assertions
	 * 
	 * @author albrecht
	 *
	 */
	public static class PremiseCombinator {
		
		public Set<Assertion> combine(Vector<Assertion> premises) {
			Set<Assertion> results = new HashSet<Assertion>();
			
			return results;
		}
	}
	
	private Set<PremiseCombinator> conclusions;
	

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ruleName() {
		// TODO Auto-generated method stub
		return null;
	}

}
