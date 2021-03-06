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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tu_dresden.psy.inference.AssertionEquivalenceClasses;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.EquivalentAssertions;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferenceMaps;
import de.tu_dresden.psy.inference.compiler.StringIds;
import de.tu_dresden.psy.inference.forms.AnnotableDisjunctiveNormalForm;
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

	private int highestDepth, highestCount;

	/**
	 * used to make a premise vector for triviality tests
	 */
	private Set<AssertionInterface> trivialInferred;

	private Set<ArrayList<Integer>> sampleSolutions;
	private StringIds assertionDomain;

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
			Set<ConstrainedAssertionFilter> trivial,
			Set<ArrayList<Integer>> sampleSolutions, StringIds assertionDomain) {
		this.sampleSolutions = sampleSolutions;
		this.assertionDomain = assertionDomain;
		this.givenAssertions = new AssertionEquivalenceClasses(
				this.sampleSolutions, this.assertionDomain);

		this.givenAssertions.addNewAssertions(implicit);

		for (AssertionInterface a : this.givenAssertions.getClasses()) {
			if (a instanceof EquivalentAssertions) {
				EquivalentAssertions ea = (EquivalentAssertions) a;
				ea.considerJustified();
			}
		}

		this.givenAssertions.addNewAssertions(given);

		this.validAssertions = new AssertionEquivalenceClasses(this.givenAssertions);

		this.state = State.open;

		this.invalidInferredAssertions = new HashSet<AssertionInterface>();

		this.trivialInferred = new HashSet<AssertionInterface>();

		this.usedRules = new HashSet<InferenceMap>(rules.size());
		this.usedRules.addAll(rules);

		this.invalid = invalid;
		this.trivial = trivial;

		this.highestCount = 0;
		this.highestDepth = 0;
	}

	/**
	 * 
	 * @return valid assertions that have been inferred
	 */

	public AssertionEquivalenceClasses getValid() {
		return this.validAssertions;
	}

	/**
	 * 
	 * @return given assertions
	 */

	public AssertionEquivalenceClasses getGiven() {
		return this.givenAssertions;
	}

	/**
	 * 
	 * @return a quick report about the state of the inference mechanism :)
	 */

	public String getReport() {
		String report = "";

		if (this.state == State.open) {
			report += "No inference attempts have been made so far.\n";
		} else if (this.state == State.closed) {
			report += "All inferable assertions have been found.\n";
		} else if (this.state == State.closed) {
			report += "It was possible to infer invalid assertions.\n";
		} else if (this.state == State.excess) {
			report += "Inference was stopped because the process exceeded the time limit.\n";
		}

		if (this.invalidInferredAssertions.isEmpty() == false) {
			report += "\nThe following invalid assertions could be inferred:\n";
			for (AssertionInterface ia : this.invalidInferredAssertions) {
				report += " !> " + ia.getSubject() + "·" + ia.getPredicate()
						+ "·" + ia.getObject() + "\n";
			}
		}

		Set<String> orderedOutput = new TreeSet<String>();

		for (AssertionInterface a : this.validAssertions.getClasses()) {
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
		this.validAssertions.calculateAncestors(excessLimit);
	}

	/**
	 * 
	 * @return all inferred assertions
	 */

	public Set<AssertionInterface> getInferred() {
		return this.validAssertions.getClasses();
	}

	/**
	 * 
	 * @return invalid inferred assertions
	 */

	public Set<AssertionInterface> getInvalid() {
		return this.invalidInferredAssertions;
	}

	/**
	 * 
	 * @return closure state
	 */
	public InferableAssertions.State getState() {
		return this.state;
	}

	/**
	 * NOTE: close the valid assertions first
	 * 
	 * @param assertion
	 * @return true, if assertion has been inferred
	 */
	public boolean isInferable(AssertionInterface assertion) {
		return this.validAssertions.contains(assertion);
	}

	/**
	 * NOTE: close the valid assertions first, and update justification
	 * 
	 * @param assertion
	 * @return justification level of the assertion
	 */
	public int justificationLevel(AssertionInterface assertion) {
		return this.validAssertions.justification(assertion);
	}

	/**
	 * NOTE: close the valid assertions first, then update justification, and
	 * then calculate ancestors
	 * 
	 * @param assertion
	 * @return all ancestors of the assertion
	 */

	public AnnotableDisjunctiveNormalForm<EquivalentAssertions> getAncestors(
			AssertionInterface assertion) {
		return this.validAssertions.ancestors(assertion);
	}

	/**
	 * NOTE: close the valid assertions first, then update justification, and
	 * then calculate the preimages
	 * 
	 * @param assertion
	 * @return all ancestors of the assertion
	 */

	public AnnotableDisjunctiveNormalForm<EquivalentAssertions> getPreimages(
			AssertionInterface assertion) {
		return this.validAssertions.preimages(assertion);
	}

	/**
	 * use the given filters to update the justification levels
	 * 
	 * @param justified
	 *            filter for justification
	 * @param valid
	 *            assertions that are considered valid
	 * @param useClosure
	 *            if true, use all validAssertions as justification base,
	 *            otherwise only given assertions
	 */

	public void updateJustification(Set<ConstrainedAssertionFilter> justified,
			AssertionEquivalenceClasses valid, boolean useClosure) {
		for (ConstrainedAssertionFilter filter : justified) {
			for (AssertionInterface a : filter.filter(this.validAssertions
					.getClasses())) {
				if ((useClosure == true) || (this.givenAssertions.contains(a))) {
					if (a instanceof EquivalentAssertions) {
						if (valid.contains(a)) {
							EquivalentAssertions ea = (EquivalentAssertions) a;
							ea.considerJustified();
						}
					}
				}
			}
		}

		boolean keep_updating = true;
		while (keep_updating) {
			keep_updating = false;

			for (AssertionInterface a : this.validAssertions.getClasses()) {
				if (a instanceof EquivalentAssertions) {
					EquivalentAssertions ea = (EquivalentAssertions) a;
					if (ea.updateJustificationDepth()) {
						keep_updating = true;
					}
				}
			}
		}
	}

	/**
	 * 
	 * calculate the relative justifiedness of given (student) assertions
	 * 
	 * @param expert
	 *            structure containing validness and justifiedness information
	 *            that is used as comparator for relative justification levels
	 */

	public void relativeJustification(InferableAssertions expert) {

		/**
		 * mark all assertions that need no further justification as justified
		 */

		for (AssertionInterface a : this.validAssertions.getClasses()) {
			if (a instanceof EquivalentAssertions) {
				EquivalentAssertions ea = (EquivalentAssertions) a;
				if (expert.validAssertions.contains(ea)) {
					int justification_level = expert.validAssertions
							.justification(ea);
					if (justification_level < 1) {
						/**
						 * expert level is either 0 which indicates that this
						 * assertion does not need further justification, or the
						 * expert didn't justify this (valid) assertion as well,
						 * so we default that the student does not need to give
						 * justification for the fact.
						 * 
						 */
						ea.considerJustified();
					}
				}
			}
		}

		/**
		 * keep track of assertions we are asking the student to justify
		 */

		Set<EquivalentAssertions> ask_for_justification = new HashSet<EquivalentAssertions>();

		boolean keep_going = true;
		boolean ask_for_more;

		while (keep_going) {
			keep_going = false;
			ask_for_more = true;

			/**
			 * now update all relative justifications
			 */

			for (AssertionInterface a : this.validAssertions.getClasses()) {
				if (a instanceof EquivalentAssertions) {
					EquivalentAssertions ea = (EquivalentAssertions) a;
					if ((this.validAssertions.justification(ea) < 0)
							&& (ask_for_justification.contains(ea)==false)
							&& (expert.validAssertions.contains(ea))) {
						AnnotableDisjunctiveNormalForm<EquivalentAssertions> preimages = expert.validAssertions
								.preimages(ea);

						/**
						 * check whether there are justified or
						 * asked-for-justification preimages for ea
						 */

						for (Set<EquivalentAssertions> cons : preimages
								.getTerm()) {
							boolean relative_justified = true;

							for (EquivalentAssertions prea : cons) {
								if (this.validAssertions.contains(prea) == false) {
									relative_justified = false;
									break;
								}

								if ((this.validAssertions.justification(prea) < 0)
										&& (ask_for_justification
												.contains(prea) == false)) {
									relative_justified = false;
									break;
								}
							}

							/**
							 * found possible justification path, consider ea to
							 * be relatively justified for now
							 */

							if (relative_justified) {
								keep_going = true;
								ask_for_more = false;

								ea.considerJustified();

								break;
							}
						}
					}
				}
			}

			/**
			 * if we didn't manage to justify another assertion, add another
			 * given assertion to the ask for more pool
			 */

			if (ask_for_more) {
				EquivalentAssertions ask_for_this = null;
				int justification_of_this = -1;

				for (AssertionInterface a : this.givenAssertions.getClasses()) {
					if (a instanceof EquivalentAssertions) {
						EquivalentAssertions ea = (EquivalentAssertions) a;
						if ((this.validAssertions.justification(ea) < 0)
								&& (ask_for_justification.contains(ea)==false)
								&& (expert.validAssertions.contains(ea))) {
							if (ask_for_this == null) {
								ask_for_this = ea;
								justification_of_this = expert.validAssertions.justification(ea);
							} else {
								if (expert.validAssertions.justification(ea) < justification_of_this) {
									ask_for_this = ea;
									justification_of_this = expert.validAssertions.justification(ea);
								}
							}
						}
					}
				}

				/**
				 * we found another good assertion that we will go and ask for justification
				 */

				if (ask_for_this != null) {
					keep_going = true;
					ask_for_justification.add(ask_for_this);
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
		InferenceMaps maps = new InferenceMaps(this.usedRules);

		int lastCount = 0;
		int validCount = this.validAssertions.getClasses().size();

		this.state = State.closed;

		boolean close_again = true;

		while ((validCount > lastCount) || close_again) {

			close_again = (validCount > lastCount);

			if (limit.exceeded()) {
				this.state = State.excess;
				break;
			}

			lastCount = validCount;

			Set<AssertionInterface> inferred = maps.inferNew(
					this.validAssertions.getClasses(), limit);

			if (limit.exceeded()) {
				this.state = State.excess;
			}

			this.trivialInferred.clear();

			for (ConstrainedAssertionFilter filter : this.trivial) {
				this.trivialInferred.addAll(filter.filter(inferred));
			}

			for (Iterator<AssertionInterface> i_assertion = inferred.iterator(); i_assertion
					.hasNext();) {
				if (this.trivialInferred.contains(i_assertion.next())) {
					i_assertion.remove();
				}
			}

			for (ConstrainedAssertionFilter filter : this.invalid) {
				this.invalidInferredAssertions.addAll(filter.filter(inferred));
			}

			this.validAssertions.addNewAssertions(inferred);
			validCount = this.validAssertions.getClasses().size();

			if (this.invalidInferredAssertions.isEmpty() == false) {
				this.state = State.invalid;
				System.err.println("Inferred INVALID Assertions:");
				for (AssertionInterface ai : this.invalidInferredAssertions) {
					System.err.println("INVALID  " + ai.toString());
				}
				break;
			}
		}

		return this.state;
	}

	/**
	 * 
	 * @param justificationNeeded
	 *            assertion for that the lookup is done
	 * @param otherGivenAssertions
	 *            other assertions that may be used
	 * @return all minimal in justification-depth-plus-one sum of non-given
	 *         assertions direct ancestor missing sets
	 */

	public Set<Set<EquivalentAssertions>> getMinimalDepthPrecursorSets(
			AssertionInterface justificationNeeded,
			Set<EquivalentAssertions> otherGivenAssertions) {
		Set<Set<EquivalentAssertions>> minimal_precursors = new HashSet<Set<EquivalentAssertions>>();

		if (this.isInferable(justificationNeeded) == false) {
			return minimal_precursors;
		}

		if (this.justificationLevel(justificationNeeded) == EquivalentAssertions.notJustified) {
			return minimal_precursors;
		}

		int minimal_depth = Integer.MAX_VALUE;

		for (Set<EquivalentAssertions> precursors : this.validAssertions.precursors(
				justificationNeeded).getTerm()) {

			int depth_sum = 0;
			Set<EquivalentAssertions> missing = new HashSet<EquivalentAssertions>();

			for (EquivalentAssertions ea : precursors) {
				if (otherGivenAssertions.contains(ea) == false) {
					depth_sum += ea.getJustificationDepth() + 1;
					missing.add(ea);
				}
			}

			if (missing.isEmpty()) {
				/**
				 * this argument is relatively justified with the given
				 * assertions
				 */
				minimal_precursors.clear();
				return minimal_precursors;
			}

			if (depth_sum < minimal_depth) {
				minimal_precursors.clear();
				minimal_depth = depth_sum;
			}

			if (depth_sum == minimal_depth) {
				minimal_precursors.add(missing);
			}

		}

		return minimal_precursors;
	}

	/**
	 * 
	 * @param toJustify
	 * @param disregard
	 *            these assertions are considered to be justified already
	 * @param forbidden
	 *            these assertions are to be justified right now
	 * @param currentDepth
	 * @param maximumDepth
	 * 
	 * @return number of ways to justify the given assertion
	 */

	public int countJustificationDelta(AssertionInterface toJustify,
			Set<EquivalentAssertions> disregard,
			Set<EquivalentAssertions> forbidden, int currentDepth,
			int maximumDepth) {

		if (currentDepth == maximumDepth) {
			return 0;
		}

		Set<Set<EquivalentAssertions>> ways = this.validAssertions.preimages(
				toJustify).getTerm();

		int count = 0;

		for (Set<EquivalentAssertions> way : ways) {
			boolean no_way = false;

			for (EquivalentAssertions ea : way) {
				if (forbidden.contains(ea)) {
					no_way = true;
					break;
				}
			}

			if (no_way == false) {
				int factor = 1;

				for (EquivalentAssertions ea : way) {
					if (true != (disregard.contains(ea) || (ea
							.getJustificationDepth() == 0))) {

						forbidden.add(ea);

						factor *= this.countJustificationDelta(ea, disregard,
								forbidden, currentDepth + 1, maximumDepth);

						forbidden.remove(ea);

						if (factor == 0) {
							break;
						}
					}
				}

				count += factor;
			}
		}

		if (count > this.highestCount) {
			this.highestCount = count;

			System.err.println("C count=" + count + "  depth="
					+ (currentDepth + 1));
		}

		if ((currentDepth > this.highestDepth) && (count > 0)) {
			this.highestDepth = currentDepth;
			System.err.println("D count=" + count + "  depth="
					+ (currentDepth + 1));
		}

		return count;
	}

	/**
	 * 
	 * @param assertion
	 * @return number of different ways to justify an assertion
	 */

	public int countPossibleJustifications(AssertionInterface assertion) {
		if (this.validAssertions.contains(assertion) == false) {
			return 0;
		}

		Set<EquivalentAssertions> disregard = new HashSet<EquivalentAssertions>();
		Set<EquivalentAssertions> forbidden = new HashSet<EquivalentAssertions>();

		forbidden
		.add(new EquivalentAssertions(assertion, this.sampleSolutions,
				this.assertionDomain));

		return this.countJustificationDelta(assertion, disregard, forbidden, 0, 5);
	}

	/**
	 * 
	 * 
	 * @param ways
	 * @return String that describes the DNF term
	 */

	public String waysToString(Set<Set<EquivalentAssertions>> ways) {
		String justifications = "";

		for (Set<EquivalentAssertions> combination : ways) {
			if (justifications.isEmpty() == false) {
				justifications += "\n  or";
			}

			boolean add_and = false;

			for (EquivalentAssertions ea2 : combination) {
				if (add_and) {
					justifications += "\n    and ";
				} else {
					justifications += "\n        ";
					add_and = true;
				}
				justifications += ea2.getSubject() + "·" + ea2.getPredicate()
						+ "·" + ea2.getObject();
			}
		}

		return justifications;
	}

	/**
	 * 
	 * @param needJustification
	 * @param otherGivenAssertions
	 * @return tips for justification
	 */

	public String getJustificationTips(
			Set<AssertionInterface> needJustification,
			Set<EquivalentAssertions> otherGivenAssertions,
			Map<String, ConstrainedAssertionFilter> qualities) {
		return this.getJustificationTips(needJustification, otherGivenAssertions, qualities, false);
	}

	public String getJustificationTips(
			Set<AssertionInterface> needJustification,
			Set<EquivalentAssertions> otherGivenAssertions,
			Map<String, ConstrainedAssertionFilter> qualities,
			boolean justQualities) {

		Map<EquivalentAssertions, Set<Set<EquivalentAssertions>>> justified_by = new HashMap<EquivalentAssertions, Set<Set<EquivalentAssertions>>>();

		Set<String> qualities_lacking = new TreeSet<String>();

		for (AssertionInterface a : needJustification) {
			EquivalentAssertions ea = new EquivalentAssertions(a,
					this.sampleSolutions, this.assertionDomain);

			justified_by.put(ea,
					this.getMinimalDepthPrecursorSets(ea, otherGivenAssertions));
		}

		boolean is_closed = false;

		do_again: while (is_closed == false) {
			is_closed = true;
			for (EquivalentAssertions ea : justified_by.keySet()) {
				for (Set<EquivalentAssertions> combinations : justified_by
						.get(ea)) {
					for (EquivalentAssertions ea2 : combinations) {
						if (justified_by.containsKey(ea2) == false) {
							justified_by.put(
									ea2,
									this.getMinimalDepthPrecursorSets(ea2,
											otherGivenAssertions));
							is_closed = false;
							continue do_again;
						}
					}
				}
			}
		}

		Map<String, String> ordered = new TreeMap<String, String>();

		Set<AssertionInterface> filter_input = new HashSet<AssertionInterface>();

		for (EquivalentAssertions ea : justified_by.keySet()) {
			String justifications = "";

			Set<Set<EquivalentAssertions>> ways = justified_by.get(ea);

			if (ways.isEmpty()) {
				continue;
			}

			for (Set<EquivalentAssertions> combination : ways) {
				if (justifications.isEmpty() == false) {
					justifications += "\n  or";
				}

				boolean add_and = false;

				for (EquivalentAssertions ea2 : combination) {
					if (add_and) {
						justifications += "\n    and ";
					} else {
						justifications += "\n        ";
						add_and = true;
					}
					justifications += ea2.getSubject() + "·"
							+ ea2.getPredicate() + "·" + ea2.getObject();

					for (String name : qualities.keySet()) {
						if (qualities_lacking.contains(name) == false) {
							filter_input.clear();
							filter_input.add(ea2);
							if (qualities.get(name).filter(filter_input)
									.isEmpty() == false) {
								qualities_lacking.add(name);
							}
						}
					}
				}
			}

			if (needJustification.contains(ea)) {
				ordered.put(" " + ea.getSubject() + "·" + ea.getPredicate()
						+ "·" + ea.getObject(), justifications);
			} else {
				ordered.put("*" + ea.getSubject() + "·" + ea.getPredicate()
						+ "·" + ea.getObject(), justifications);
			}

			for (String name : qualities.keySet()) {
				if (qualities_lacking.contains(name) == false) {
					filter_input.clear();
					filter_input.add(ea);
					if (qualities.get(name).filter(filter_input).isEmpty() == false) {
						qualities_lacking.add(name);
					}
				}
			}

		}

		StringBuffer tips = new StringBuffer();

		for (String key : ordered.keySet()) {
			tips.append("\n\n");
			tips.append(key);
			tips.append(" may be justified by further asserting that:");
			tips.append(ordered.get(key));
		}

		tips.append("\n\nThere are missing pieces of information regarding:");

		for (String name : qualities_lacking) {
			tips.append("\n     " + name);
		}

		if (justQualities) {
			String qs = "";
			for (String name : qualities_lacking) {
				if (qs.isEmpty()==false) {
					qs += "&";
				}
				qs += name;
			}

			return qs;

		}

		return tips.toString();
	}

	/**
	 * 
	 * update rule ancestor terms, do after calculating justifications
	 * 
	 * @param excessLimit
	 * 
	 */

	public void calculateRuleAncestors(ExcessLimit result) {
		this.validAssertions.calculateRuleAncestors(result);
	}

}