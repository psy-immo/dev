/**
 * Phi2Combine.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tu_dresden.psy.inference.Assertion;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferredAssertion;

/**
 * 
 * implements inference map phi_2,=>
 * 
 * @author immanuel
 * 
 */

public class Phi2Combine implements InferenceMap {
	
	@Override
	public String ruleName() {
		return "2,2→2";
	}

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit) {
		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();

		Map<Object, Set<AssertionInterface>> by_subject = new HashMap<Object, Set<AssertionInterface>>();
		Map<Object, Set<AssertionInterface>> by_object = new HashMap<Object, Set<AssertionInterface>>();

		Iterator<AssertionInterface> it;

		for (it = validPremises.iterator(); it.hasNext();) {

			AssertionInterface a = it.next();

			if (a.getPredicate() instanceof String) {
				String predicate = (String) a.getPredicate();

				if ((predicate.equals("is smaller than") == true)
						|| (predicate.equals("is bigger than") == true)
						|| (predicate.equals("is as big as") == true)) {
					Object subject = a.getSubject();
					Object object = a.getObject();

					if (by_subject.containsKey(subject) == true) {
						by_subject.get(subject).add(a);
					} else {
						Set<AssertionInterface> s = new HashSet<AssertionInterface>();
						s.add(a);
						by_subject.put(subject, s);
					}

					if (by_object.containsKey(object) == true) {
						by_object.get(object).add(a);
					} else {
						Set<AssertionInterface> s = new HashSet<AssertionInterface>();
						s.add(a);
						by_object.put(object, s);
					}
				}
			}
		} // for it...

		for (Iterator<Object> iy = by_object.keySet().iterator(); iy.hasNext();) {
			Object y = iy.next();

			for (Iterator<AssertionInterface> ixy = by_object.get(y).iterator(); ixy
					.hasNext();) {
				AssertionInterface xy = ixy.next();
				if (by_subject.containsKey(y))
					for (Iterator<AssertionInterface> iyz = by_subject.get(y)
							.iterator(); iyz.hasNext();) {
						AssertionInterface yz = iyz.next();

						int pred_xy = predicateToInt((String) xy.getPredicate());
						int pred_yz = predicateToInt((String) yz.getPredicate());

						if (pred_xy == pred_yz) {
							/**
							 * <, =, > are transitive relations,
							 * 
							 * x ~ y & y ~ z => x ~ z
							 */
							inferred.add(new InferredAssertion(this,new Assertion(xy.getSubject(), xy
									.getPredicate(), yz.getObject()),xy,yz));
						} else if (pred_xy + pred_yz != 0) {
							/**
							 * pred_xy + pred_yz == 0 if x < y & y > z or x > y
							 * & y < z
							 * 
							 * x = y & y < z => x < z x < y & y = z => x < z x =
							 * y & y > z => x > z x > y & y = z => x > z *
							 */

							inferred.add(new InferredAssertion(this,new Assertion(xy.getSubject(),
									(pred_xy + pred_yz < 0 ? "is smaller than"
											: "is bigger than"), yz.getObject()),xy,yz));
						}

					}

			}
		}

		return inferred;
	}

	/**
	 * convert string predicate to int value
	 * 
	 * @param predicate
	 * @return numerical representation of the predicate: -1 smaller, 0 equal, 1
	 *         bigger
	 */
	static int predicateToInt(String predicate) {
		if (predicate.equals("is smaller than") == true)
			return -1;

		if (predicate.equals("is bigger than") == true)
			return 1;

		return 0;
	}

	public static void main(String[] args) {
		Phi2Combine phi2combine = new Phi2Combine();

		Set<AssertionInterface> premises = new HashSet<AssertionInterface>();

		premises.add(new Assertion("A·is smaller than·B"));
		premises.add(new Assertion("B·is as big as·C"));
		premises.add(new Assertion("B·is bigger than·D"));
		premises.add(new Assertion("D·is as big as·A"));
		premises.add(new Assertion("B·is smaller than·E"));

		Set<AssertionInterface> level1 = phi2combine.inferNew(premises, null);

		System.out.println("first degree: ");

		for (Iterator<AssertionInterface> it = level1.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(premises);

		Set<AssertionInterface> level2 = phi2combine.inferNew(level1, null);

		System.out.println("first+second degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(level2);

		level2 = phi2combine.inferNew(level1, null);

		System.out.println("first+second+third degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
	}

}
