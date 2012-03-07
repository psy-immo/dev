/**
 * InferenceMachine.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

import java.applet.Applet;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
 * 
 * implements an java applet interface for the provided inference facilities
 * 
 * @author immanuel
 * 
 */

public class InferenceMachine extends Applet {

	/**
	 * serialization
	 */
	private static final long serialVersionUID = 8101906904403439152L;

	/**
	 * stop inference process after ... seconds
	 */

	private static final float defaultExcessTimeLimit = 5;

	/**
	 * machine state variables
	 */
	private Set<AssertionInterface> implicit, expert, studentArguments,
			studentConclusions;
	private Map<String, InferenceMap> inferenceMaps;
	private Set<ConstrainedAssertionFilter> trivial, invalid, justified;
	private float excessTimeLimit;

	/**
	 * class for keeping track of inferred assertions
	 * 
	 * @author immanuel
	 * 
	 */

	public static class InferrableAssertions {

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
		private State state;

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

		public InferrableAssertions(Set<AssertionInterface> implicit,
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
		 */

		public void calculateAncestors() {
			validAssertions.calculateAncestors();
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
		public State getState() {
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

		public State closeValid(ExcessLimit limit) {
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
	};

	/**
	 * keep inference states
	 */

	InferrableAssertions expertValid, studentValid;

	/**
	 * reset the state of the machine
	 */

	public void resetState() {
		implicit = new HashSet<AssertionInterface>();
		expert = new HashSet<AssertionInterface>();
		studentArguments = new HashSet<AssertionInterface>();
		studentConclusions = new HashSet<AssertionInterface>();
		inferenceMaps = new HashMap<String, InferenceMap>();
		trivial = new HashSet<ConstrainedAssertionFilter>();
		invalid = new HashSet<ConstrainedAssertionFilter>();
		justified = new HashSet<ConstrainedAssertionFilter>();
		expertValid = new InferrableAssertions(
				new HashSet<AssertionInterface>(),
				new HashSet<AssertionInterface>(), new HashSet<InferenceMap>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ConstrainedAssertionFilter>());
		studentValid = new InferrableAssertions(
				new HashSet<AssertionInterface>(),
				new HashSet<AssertionInterface>(), new HashSet<InferenceMap>(),
				new HashSet<ConstrainedAssertionFilter>(),
				new HashSet<ConstrainedAssertionFilter>());
	}

	public InferenceMachine() {
		resetState();

		excessTimeLimit = defaultExcessTimeLimit;
	}

	/**
	 * add data from inference xml file to the current machine state
	 * 
	 * @param root
	 */
	private void addXml(XmlRootTag root) {
		implicit.addAll(root.getImplicitAssertions());
		expert.addAll(root.getExpertAssertions());
		studentArguments.addAll(root.getGivenAssertions());
		studentConclusions.addAll(root.getGivenConclusions());

		Map<String, InferenceMap> updated_rules = root.getInferenceMapsByName();

		for (String key : updated_rules.keySet()) {
			inferenceMaps.put(key, updated_rules.get(key));
		}

		trivial.addAll(root.getTrivialityFilters());
		invalid.addAll(root.getInvalidityFilters());
		justified.addAll(root.getJustifiedFilters());
	}

	/**
	 * 
	 * @return excess time limit in seconds as string
	 */

	public String getExcessTimeLimit() {
		return "" + excessTimeLimit;
	}

	/**
	 * add inference xml data from a local file
	 * 
	 * @param location
	 * @return Error Message
	 */
	public String addXmlFile(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			handler.readStream(new FileInputStream(location));

			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * add inference xml data from an url
	 * 
	 * @param location
	 * @return Error message
	 */
	public String addXmlUrl(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			URL url = new URL(location);
			handler.readStream(url.openStream());

			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * add inference xml data given as string without surrounding &lt;root>-tag
	 * 
	 * @param rootContents
	 * @return Error message
	 */

	public String addXmlString(String rootContents) {

		try {
			XmlHandler handler = new XmlHandler();

			handler.readString(rootContents);

			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding xml string \n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * close expert assertions under given rules
	 * 
	 * @return "closed", "invalid" or "excess"
	 */

	public String closeExpertAssertions() {
		expertValid = new InferrableAssertions(implicit, expert,
				inferenceMaps.values(), invalid, trivial);
		return expertValid.closeValid(new ExcessLimit(excessTimeLimit)).name();
	}

	/**
	 * close student assertions under given rules
	 * 
	 * @return "closed", "invalid" or "excess"
	 */

	public String closeStudentAssertions() {
		studentValid = new InferrableAssertions(implicit, studentArguments,
				inferenceMaps.values(), invalid, trivial);
		return studentValid.closeValid(new ExcessLimit(excessTimeLimit)).name();
	}

	/**
	 * 
	 * @return correct conclusions separated by '\n'
	 */
	public String getCorrectStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : studentConclusions) {
			if (expertValid.isInferable(conclusion)) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return incorrect conclusions separated by '\n'
	 */
	public String getIncorrectStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : studentConclusions) {
			if (expertValid.isInferable(conclusion) == false) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return inferable conclusions separated by '\n'
	 */
	public String getInferableStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : studentConclusions) {
			if (studentValid.isInferable(conclusion)) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * 
	 * @return non-inferable conclusions separated by '\n'
	 */
	public String getNonInferableStudentConclusions() {
		String result = "";

		for (AssertionInterface conclusion : studentConclusions) {
			if (studentValid.isInferable(conclusion) == false) {
				if (result.isEmpty() == false) {
					result += "\n";
				}
				result += conclusion.toString();
			}
		}

		return result;
	}

	/**
	 * calculate justification levels
	 */

	public void updateExpertJustification() {
		expertValid.updateJustification(justified);
	}

	/**
	 * calculate justification levels
	 */

	public void updateStudentJustification() {
		studentValid.updateJustification(justified);
	}

	/**
	 * 
	 * @return report on the inference of the expert assertions
	 */
	public String getExpertReport() {
		return expertValid.getReport();
	}

	/**
	 * 
	 * @return report on the inference of the student assertions
	 */
	public String getStudentReport() {
		return studentValid.getReport();
	}

	public static String orderedAssertionSetString(String pre,
			Collection<? extends AssertionInterface> set, String post,
			String heading) {

		if (set.isEmpty())
			return "";

		String result = heading;

		Set<String> s = new TreeSet<String>();

		for (AssertionInterface a : set) {
			s.add(a.getSubject().toString() + "·" + a.getPredicate().toString()
					+ "·" + a.getObject().toString());
		}

		for (String x : s) {
			result += pre + x + post;
		}

		return result;
	}

	/**
	 * 
	 * @return report on the student assertions, conclusions etc. relative to
	 *         the expert solution
	 */
	public String getReport() {
		String report = "";

		Set<AssertionInterface> correct_arguments = new HashSet<AssertionInterface>();
		Set<AssertionInterface> incorrect_arguments = new HashSet<AssertionInterface>();
		Set<AssertionInterface> correct_arguments_yet_unjustified = new HashSet<AssertionInterface>();

		for (AssertionInterface a : studentArguments) {
			if (expertValid.isInferable(a)) {
				correct_arguments.add(a);
				if (studentValid.justificationLevel(a) == EquivalentAssertions.notJustified) {
					correct_arguments_yet_unjustified.add(a);
				}
			} else {
				incorrect_arguments.add(a);
			}

		}

		Set<AssertionInterface> correct_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> incorrect_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> inferable_conclusions = new HashSet<AssertionInterface>();
		Set<AssertionInterface> inferable_conclusions_yet_unjustified = new HashSet<AssertionInterface>();

		for (AssertionInterface a : studentConclusions) {
			if (expertValid.isInferable(a)) {
				correct_conclusions.add(a);
			} else {
				incorrect_conclusions.add(a);
			}
			if (studentValid.isInferable(a)) {
				inferable_conclusions.add(a);
				if (studentValid.justificationLevel(a) == EquivalentAssertions.notJustified) {
					inferable_conclusions_yet_unjustified.add(a);
				}
			}
		}

		report += orderedAssertionSetString("     ", correct_arguments, "\n",
				"\nThe following arguments are correct:\n");
		report += orderedAssertionSetString("     ",
				correct_arguments_yet_unjustified, "\n",
				" where the following arguments need further justification:\n");

		report += orderedAssertionSetString("     ", incorrect_arguments, "\n",
				"\nThe following arguments are incorrect:\n");

		report += orderedAssertionSetString("     ", correct_conclusions, "\n",
				"\nThe following conclusions are correct:\n");

		report += orderedAssertionSetString("     ", incorrect_conclusions,
				"\n",
				"\nThe following conclusions are incorrect:\n");

		report += orderedAssertionSetString("     ", inferable_conclusions,
				"\n",
				"\nThe following conclusions can be infered from the arguments:\n");

		report += orderedAssertionSetString("     ",
				inferable_conclusions_yet_unjustified, "\n",
				" where the following conclusions need further justification:\n");

		return report;
	}

	public String calculateAncestors() {
		String result = "";
		expertValid.calculateAncestors();
		return result;
	}
}
