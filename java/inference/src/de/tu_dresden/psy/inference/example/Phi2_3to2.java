/**
 * Phi2_3To2.java, (c) 2011, Immanuel Albrecht; Dresden University of
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
 * implements the inference map phi_2,3->2
 * 
 * @author immanuel
 * 
 */

public class Phi2_3to2 implements InferenceMap {

	static final String[] prefixes = { "the current through", "the voltage of",
			"the resistance of", "the input power of", "the luminosity of" };

	static final String[] operands = { "current", "voltage", "resistance",
			"input power", "luminosity" };

	/**
	 * map a prefix to the corresponding abstract noun
	 * 
	 * @param prefix
	 * @return operand noun
	 */
	static final String prefixToOperand(String prefix) {
		for (int i = 0; i < prefixes.length; ++i) {
			if (prefix.equals(prefixes[i]))
				return operands[i];
		}
		return "";
	}

	/**
	 * map a noun to the corresponding prefix
	 * 
	 * @param operand
	 * @return prefix
	 */
	static final String operandToPrefix(String operand) {
		for (int i = 0; i < operands.length; ++i) {
			if (operand.equals(operands[i]))
				return prefixes[i];
		}
		return "";
	}

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit) {
		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();

		Map<String, Set<AssertionInterface>> by_prefix = new HashMap<String, Set<AssertionInterface>>();
		Map<String, Set<AssertionInterface>> by_operand = new HashMap<String, Set<AssertionInterface>>();

		Iterator<AssertionInterface> it;
		for (it = validPremises.iterator(); it.hasNext();) {
			AssertionInterface a = it.next();

			if (a.getPredicate().equals("means") == true) {
				if ((a.getSubject() instanceof String)
						&& (a.getObject() instanceof String)) {
					String subject = (String) a.getSubject();

					if (subject.startsWith("a smaller")) {
						subject = subject.substring(10);
					} else // a bigger
					{
						subject = subject.substring(9);
					}

					if (by_operand.containsKey(subject)) {
						by_operand.get(subject).add(a);
					} else {
						Set<AssertionInterface> s = new HashSet<AssertionInterface>();
						s.add(a);
						by_operand.put(subject, s);
					}
				}
			} else if (a.getPredicate().equals("is bigger than")
					|| a.getPredicate().equals("is smaller than")
					|| a.getPredicate().equals("is as big as")) {

				if (a.getSubject() instanceof String) {
					String subject = (String) a.getSubject();
					if (a.getObject() instanceof String) {
						String object = (String) a.getObject();
						for (int i = 0; i < prefixes.length; i++) {
							if (subject.startsWith(prefixes[i])
									&& object.startsWith(prefixes[i])) {
								String prefix = prefixes[i];
								if (by_prefix.containsKey(prefix)) {
									by_prefix.get(prefix).add(a);
								} else {
									Set<AssertionInterface> s = new HashSet<AssertionInterface>();
									s.add(a);
									by_prefix.put(prefix, s);
								}
							}
						}
					}
				}
			}
		}

		Iterator<String> iprefix;
		for (iprefix = by_prefix.keySet().iterator(); iprefix.hasNext();) {
			String prefix = iprefix.next();

			String operand = prefixToOperand(prefix);

			if (by_operand.containsKey(operand)) {

				Iterator<AssertionInterface> ia;

				for (ia = by_prefix.get(prefix).iterator(); ia.hasNext();) {
					AssertionInterface a = ia.next();

					String subject_a = (String) a.getSubject();
					subject_a = subject_a.substring(prefix.length() + 1);
					String object_a = (String) a.getObject();
					object_a = object_a.substring(prefix.length() + 1);

					Iterator<AssertionInterface> ib;
					for (ib = by_operand.get(operand).iterator(); ib.hasNext();) {
						AssertionInterface b = ib.next();

						String object = (String) b.getObject();

						boolean is_smaller = false;

						if (object.startsWith("a smaller")) {
							object = object.substring(10);
							is_smaller = true;
						} else // a bigger
						{
							object = object.substring(9);
						}

						String new_prefix = operandToPrefix(object);

						if (is_smaller == ((String) b.getSubject())
								.startsWith("a smaller")) {
							/**
							 * monotone case
							 */
							inferred.add(new InferredAssertion(this,new Assertion(new_prefix + " "
									+ subject_a, a.getPredicate(), new_prefix
									+ " " + object_a),a,b));
						} else {
							/**
							 * antitone case
							 */
							if (a.getPredicate().equals("is smaller than")) {
								inferred.add(new InferredAssertion(this,new Assertion(new_prefix + " "
										+ subject_a, "is bigger than",
										new_prefix + " " + object_a),a,b));
							} else if (a.getPredicate()
									.equals("is bigger than")) {
								inferred.add(new InferredAssertion(this,new Assertion(new_prefix + " "
										+ subject_a, "is smaller than",
										new_prefix + " " + object_a),a,b));
							} else
								inferred.add(new InferredAssertion(this,new Assertion(new_prefix + " "
										+ subject_a, a.getPredicate(),
										new_prefix + " " + object_a),a,b));

						}

					}
				}
			}
		}

		return inferred;
	}

	@Override
	public String ruleName() {
		return "2,3→2";
	}

	public static void main(String[] args) {
		Phi2_3to2 phi2_3to_2 = new Phi2_3to2();

		Set<AssertionInterface> premises = new HashSet<AssertionInterface>();

		premises.add(new Assertion("a smaller resistance", "means",
				"a bigger current"));
		premises.add(new Assertion("a bigger current", "means",
				"a bigger voltage"));
		premises.add(new Assertion("the resistance of A", "is smaller than",
				"the resistance of B"));
		premises.add(new Assertion("the current through C", "is bigger than",
				"the current through D"));

		Set<AssertionInterface> level1 = phi2_3to_2.inferNew(premises, null);

		System.out.println("first degree: ");

		for (Iterator<AssertionInterface> it = level1.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(premises);

		Set<AssertionInterface> level2 = phi2_3to_2.inferNew(level1, null);

		System.out.println("first+second degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}

		level1.addAll(level2);

		level2 = phi2_3to_2.inferNew(level1, null);

		System.out.println("first+second+third degree: ");

		for (Iterator<AssertionInterface> it = level2.iterator(); it.hasNext();) {
			System.out.println(it.next());
		}
	}
}
