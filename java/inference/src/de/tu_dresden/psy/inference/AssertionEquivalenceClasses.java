/**
 * AssertionEquivalenceClasses.java, (c) 2012, Immanuel Albrecht; Dresden
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

package de.tu_dresden.psy.inference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * implements the management of assertion equivalence classes
 * 
 * @author albrecht
 * 
 */
public class AssertionEquivalenceClasses {
	/**
	 * set of all classes
	 */
	private Set<AssertionInterface> equivalenceClasses;
	private Map<EquivalentAssertions, EquivalentAssertions> representants;

	

	/**
	 * default constructor
	 */

	public AssertionEquivalenceClasses() {
		equivalenceClasses = new HashSet<AssertionInterface>();
		representants = new HashMap<EquivalentAssertions, EquivalentAssertions>();
	}


	/**
	 * copy constructor
	 * 
	 * @param copyContents
	 */

	public AssertionEquivalenceClasses(AssertionEquivalenceClasses copyContents) {
		equivalenceClasses = new HashSet<AssertionInterface>(
				copyContents.representants.size());
		representants = new HashMap<EquivalentAssertions, EquivalentAssertions>(
				copyContents.representants.size());

		for (EquivalentAssertions ea : copyContents.representants.keySet()) {
			EquivalentAssertions copy = new EquivalentAssertions(ea);
			equivalenceClasses.add(copy);
			representants.put(copy, copy);
		}
	}

	/**
	 * 
	 * @return set of EquivalentAssertions
	 */

	public Set<AssertionInterface> getClasses() {
		return equivalenceClasses;
	}

	/**
	 * 
	 * @param ass
	 * @return true, if there is an assertion class that equals the given
	 *         assertion
	 */

	public boolean contains(AssertionInterface ass) {
		EquivalentAssertions ec = new EquivalentAssertions(ass);
		return representants.get(ec) != null;
	}

	/**
	 * mark all equivalence classes old
	 */
	public void markAllOld() {
		int count_new_olds = 0;
		for (AssertionInterface assertion : equivalenceClasses) {
			if (assertion.isOld() == false) {
				assertion.markAsOld();
				count_new_olds++;
				// System.out.println("now old: "+assertion);
			}

		}
		System.out.println("\n ~~~~ Assertions marked as old: "
				+ count_new_olds + " of " + equivalenceClasses.size());
	}

	/**
	 * adds the given assertions to the respective classes
	 * 
	 * @param newAssertions
	 */
	public void addNewAssertions(Set<AssertionInterface> newAssertions) {
		System.out.println("\n ~~~~ New assertions: " + newAssertions.size());

		int count = 0;
		int size = newAssertions.size();
		int new_classes = 0;

		for (AssertionInterface assertion : newAssertions) {
			++count;
			System.err.print("\r ~~~~ done: " + ((count * 100) / size) + "% ("
					+ count + ")  new classes: " + new_classes);

			EquivalentAssertions e_class = new EquivalentAssertions(assertion);

			EquivalentAssertions previous_class = representants.get(e_class);

			if (previous_class != null) {
				previous_class.add(assertion);
			} else {
				equivalenceClasses.add(e_class);
				representants.put(e_class, e_class);
				new_classes++;
			}

		}
		System.out.println("");
	}

	/**
	 * 
	 * @param assertion
	 * @return justification level of the given assertion
	 */

	public int justification(AssertionInterface assertion) {
		EquivalentAssertions ec = new EquivalentAssertions(assertion);

		EquivalentAssertions r = representants.get(ec);
		if (r == null)
			return EquivalentAssertions.notJustified;

		return r.getJustificationDepth();
	}

	/**
	 * calculate all ancestral relations
	 */

	public void calculateAncestors() {
		for (EquivalentAssertions ea : representants.keySet()) {
			ea.updateDirectAncestors();
		}

		boolean keep_going = true;

		while (keep_going) {
			keep_going = false;
			for (EquivalentAssertions ea : representants.keySet()) {
				if (ea.updateAllAncestors())
					keep_going = true;
			}
		}
	}
}
