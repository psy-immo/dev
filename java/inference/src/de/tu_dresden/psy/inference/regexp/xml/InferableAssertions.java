/**
 * InferrableAssertions.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp.xml;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import de.tu_dresden.psy.inference.AssertionEquivalenceClasses;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.EquivalentAssertions;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferenceMaps;
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;

/**
 * class for keeping track of inferred assertions
 * 
 * @author immanuel
 * 
 */

public class InferableAssertions {

	public static enum State {
		/**
		 * there are given assertions that have to be closed under the given
		 * rules
		 */
		open,
		/**
		 * there are no more inferable assertions using the given rules, and
		 * none of them are invalid
		 */
		closed,
		/**
		 * some invalid assertions could be inferred
		 */
		invalid,
		/**
		 * inference did not stop within some given limit
		 */
		excess
	};

	private AssertionEquivalenceClasses givenAssertions;
	private AssertionEquivalenceClasses validAssertions;
	private Set<InferenceMap> usedRules;
	private Set<ConstrainedAssertionFilter> invalid, trivial;
	private Set<AssertionInterface> invalidInferredAssertions;
	private InferableAssertions.State state;

	/**
	 * used to make a premise vector for triviality tests
	 */
	private Set<AssertionInterface> trivialInferred;

	/**
	 * 
	 * @param implicit
	 * @param given
	 * @param collection
	 * @param invalid
	 * @param trivial
	 */

	public InferableAssertions(Set<AssertionInterface> implicit,
			Set<AssertionInterface> given, Collection<InferenceMap> rules,
			Set<ConstrainedAssertionFilter> invalid,
			Set<ConstrainedAssertionFilter> trivial) {
		givenAssertions = new AssertionEquivalenceClasses();

		givenAssertions.addNewAssertions(implicit);

		for (AssertionInterface a : givenAssertions.getClasses()) {
			if (a instanceof EquivalentAssertions) {
				EquivalentAssertions ea = (EquivalentAssertions) a;
				ea.considerJustified();
			}
		}

		givenAssertions.addNewAssertions(given);
		validAssertions = new AssertionEquivalenceClasses(givenAssertions);

		state = State.open;

		invalidInferredAssertions = new HashSet<AssertionInterface>();

		trivialInferred = new HashSet<AssertionInterface>();

		usedRules = new HashSet<InferenceMap>(rules.size());
		usedRules.addAll(rules);

		this.invalid = invalid;
		this.trivial = trivial;
	}

	/**
	 * 
	 * @return a quick report about the state of the inference mechanism :)
	 */

	public String getReport() {
		String report = "";

		if (state == State.open) {
			report += "No inference attempts have been made so far.\n";
		} else if (state == State.closed) {
			report += "All inferable assertions have been found.\n";
		} else if (state == State.closed) {
			report += "It was possible to infer invalid assertions.\n";
		} else if (state == State.excess) {
			report += "Inference was stopped because the process exceeded the time limit.\n";
		}

		if (invalidInferredAssertions.isEmpty() == false) {
			report += "\nThe following invalid assertions could be inferred:\n";
			for (AssertionInterface ia : invalidInferredAssertions) {
				report += " !> " + ia.getSubject() + "·"
						+ ia.getPredicate() + "·" + ia.getObject() + "\n";
			}
		}

		Set<String> orderedOutput = new TreeSet<String>();

		for (AssertionInterface a : validAssertions.getClasses()) {
			EquivalentAssertions ea = (EquivalentAssertions) a;
			if (ea.getJustificationDepth() == EquivalentAssertions.notJustified) {
				orderedOutput.add(" [unjustified] " + ea.getSubject() + "·"
						+ ea.getPredicate() + "·" + ea.getObject() + "\n");
			} else {
				orderedOutput.add(" ["
						+ String.format("%1d", ea.getJustificationDepth())
						+ "-justified] " + ea.getSubject() + "·"
						+ ea.getPredicate() + "·" + ea.getObject() + "\n");
			}
		}

		for (String s : orderedOutput) {
			report += s;
		}

		return report;
	}

	/**
	 * 
	 * update ancestor terms
	 * 
	 * @param excessLimit
	 * 
	 */

	public void calculateAncestors(ExcessLimit excessLimit) {
		validAssertions.calculateAncestors(excessLimit);
	}

	/**
	 * 
	 * @return all inferred assertions
	 */

	public Set<AssertionInterface> getInferred() {
		return validAssertions.getClasses();
	}

	/**
	 * 
	 * @return invalid inferred assertions
	 */

	public Set<AssertionInterface> getInvalid() {
		return invalidInferredAssertions;
	}

	/**
	 * 
	 * @return closure state
	 */
	public InferableAssertions.State getState() {
		return state;
	}

	/**
	 * NOTE: close the valid assertions first
	 * 
	 * @param assertion
	 * @return true, if assertion has been inferred
	 */
	public boolean isInferable(AssertionInterface assertion) {
		return validAssertions.contains(assertion);
	}

	/**
	 * NOTE: close the valid assertions first, and update justification
	 * 
	 * @param assertion
	 * @return justification level of the assertion
	 */
	public int justificationLevel(AssertionInterface assertion) {
		return validAssertions.justification(assertion);
	}

	/**
	 * use the given filters to update the justification levels
	 * 
	 * @param justified
	 */

	public void updateJustification(
			Set<ConstrainedAssertionFilter> justified) {
		for (ConstrainedAssertionFilter filter : justified) {
			for (AssertionInterface a : filter.filter(validAssertions
					.getClasses())) {
				if (a instanceof EquivalentAssertions) {
					EquivalentAssertions ea = (EquivalentAssertions) a;
					ea.considerJustified();
				}
			}
		}

		boolean keep_updating = true;
		while (keep_updating) {
			keep_updating = false;

			for (AssertionInterface a : validAssertions.getClasses()) {
				if (a instanceof EquivalentAssertions) {
					EquivalentAssertions ea = (EquivalentAssertions) a;
					if (ea.updateJustificationDepth())
						keep_updating = true;
				}
			}
		}
	}

	/**
	 * 
	 * close the valid assertions
	 * 
	 * @param limit
	 *            excess time limit
	 * @return closure state
	 */

	public InferableAssertions.State closeValid(ExcessLimit limit) {
		InferenceMaps maps = new InferenceMaps(usedRules);

		int lastCount = 0;
		int validCount = validAssertions.getClasses().size();

		state = State.closed;

		while (validCount > lastCount) {

			if (limit.exceeded()) {
				state = State.excess;
				break;
			}

			lastCount = validCount;

			Set<AssertionInterface> inferred = maps.inferNew(
					validAssertions.getClasses(), limit);

			if (limit.exceeded()) {
				state = State.excess;
			}

			trivialInferred.clear();

			for (ConstrainedAssertionFilter filter : trivial) {
				trivialInferred.addAll(filter.filter(inferred));
			}

			for (Iterator<AssertionInterface> i_assertion = inferred
					.iterator(); i_assertion.hasNext();) {
				if (trivialInferred.contains(i_assertion.next()))
					i_assertion.remove();
			}

			for (ConstrainedAssertionFilter filter : invalid) {
				invalidInferredAssertions.addAll(filter.filter(inferred));
			}

			validAssertions.addNewAssertions(inferred);
			validCount = validAssertions.getClasses().size();

			if (invalidInferredAssertions.isEmpty() == false) {
				state = State.invalid;
				break;
			}
		}

		return state;
	}
}