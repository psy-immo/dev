/**
 * Phi2Neg.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

package de.tu_dresden.psy.inference.example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tu_dresden.psy.inference.Assertion;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferredAssertion;

/**
 * implements inference map phi_2,¬
 * 
 * @author immanuel
 * 
 */
public class Phi2Neg implements InferenceMap {

	@Override
	public String ruleName() {
		return "2→2";
	}

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit) {

		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();

		Iterator<AssertionInterface> it;

		for (it = validPremises.iterator(); it.hasNext();) {
			AssertionInterface a = it.next();

			if (a.getPredicate() instanceof String) {
				String predicate = (String) a.getPredicate();
				if (predicate.equals("is smaller than") == true) {
					inferred.add(new InferredAssertion(this, new Assertion(a
							.getObject(), "is bigger than", a.getSubject()), a));
				} else if (predicate.equals("is bigger than") == true) {
					inferred.add(new InferredAssertion(this, new Assertion(a
							.getObject(), "is smaller than", a.getSubject()), a));
				}
			}
		}

		return inferred;
	}

	public static void main(String[] args) {
		Phi2Neg phi2neg = new Phi2Neg();

		Set<AssertionInterface> premises = new HashSet<AssertionInterface>();

		premises.add(new Assertion("A", "is bigger than", "B"));
		premises.add(new Assertion("C", "is smaller than", "D"));
		premises.add(new Assertion("a bigger B", "means", "a smaller D"));
		premises.add(new Assertion("a bigger A", "means", "a bigger E"));
		premises.add(new Assertion("a bigger E", "means", "a bigger B"));

		Set<AssertionInterface> level1 = phi2neg.inferNew(premises, null);

		System.out.println("first degree: ");

		for (Iterator<AssertionInterface> it = level1.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(premises);

		Set<AssertionInterface> level2 = phi2neg.inferNew(level1, null);

		System.out.println("first+second degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(level2);

		level2 = phi2neg.inferNew(level1, null);

		System.out.println("first+second+third degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
	}

}
