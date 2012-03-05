/**
 * Phi1To2.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * implements the inference map phi_2->1
 * 
 * @author immanuel
 * 
 */

public class Phi1to2 implements InferenceMap {
	
	@Override
	public String ruleName() {
		return "1→2";
	}

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit) {

		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();

		Iterator<AssertionInterface> it;

		for (it = validPremises.iterator(); it.hasNext();) {
			AssertionInterface a = it.next();

			if (a.getSubject() instanceof String) {
				String subject = (String) a.getSubject();
				if (a.getObject() instanceof String) {
					String object = (String) a.getObject();

					if (a.getPredicate().equals("is serial connected with") == true) {
						inferred.add(new InferredAssertion(this,new Assertion("the current through "
								+ subject, "is as big as",
								"the current through " + object),a));
						inferred.add(new InferredAssertion(this,new Assertion("the current through "
								+ object, "is as big as",
								"the current through " + subject),a));
					} else if (a.getPredicate().equals("is connected in parallel with")) {
						inferred.add(new InferredAssertion(this,new Assertion("the voltage of "
								+ subject, "is as big as",
								"the voltage of " + object),a));
						inferred.add(new InferredAssertion(this,new Assertion("the voltage of "
								+ object, "is as big as",
								"the voltage of " + subject),a));
					}
				}
			}
		}

		return inferred;
	}

	public static void main(String[] args) {
		Phi1to2 phi1to2 = new Phi1to2();
		
		Set<AssertionInterface> premises = new HashSet<AssertionInterface>();
		
		premises.add(new Assertion("bulb A", "is serial connected with", "bulb B"));
		premises.add(new Assertion("bulb chain AB", "is connected in parallel with", "bulb A"));
		premises.add(new Assertion("a bigger B", "means", "a smaller D"));
		premises.add(new Assertion("a bigger A", "means", "a bigger E"));
		premises.add(new Assertion("a bigger E", "means", "a bigger B"));
		
		Set<AssertionInterface> level1 = phi1to2.inferNew(premises, null);
		
		System.out.println("first degree: ");
		
		for (Iterator<AssertionInterface> it = level1.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
		
		level1.addAll(premises);
		
		Set<AssertionInterface> level2 = phi1to2.inferNew(level1, null);
		
		System.out.println("first+second degree: ");
		
		for (Iterator<AssertionInterface> it = level2.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
		
		level1.addAll(level2);
		
		level2 = phi1to2.inferNew(level1, null);
		
		System.out.println("first+second+third degree: ");
		
		for (Iterator<AssertionInterface> it = level2.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
	}

	
}
