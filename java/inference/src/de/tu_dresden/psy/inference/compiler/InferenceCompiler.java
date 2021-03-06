/**
 * InferenceCompiler.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.compiler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.tu_dresden.psy.efml.StringEscape;
import de.tu_dresden.psy.inference.AssertionEquivalenceClasses;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.EquivalentAssertions;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.forms.AnnotableDisjunctiveNormalForm;
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;
import de.tu_dresden.psy.inference.regexp.xml.InferableAssertions;
import de.tu_dresden.psy.inference.regexp.xml.InferableAssertions.State;
import de.tu_dresden.psy.inference.regexp.xml.XmlRootTag;
import de.tu_dresden.psy.inference.regexp.xml.XmlTag;
import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatchers;

/**
 * 
 * class that implements facilities for inference machine compilation, i.e. to
 * create the data needed for the efjs website for qualitative reasoning tasks
 * 
 * @author immo
 * 
 */

public class InferenceCompiler {

	/**
	 * there may be errors/loopholes in the inference xml data that would cause
	 * some actions (like closing the correct assertions base using inference
	 * rules) to take a very long time or even make them an infinite loop. So we
	 * choose some maximal time that we will spend on these actions.
	 **/

	private float excessTimeLimit;

	private static final float defaultExcessTimeLimit = 12000;

	/**
	 * keep track of sample solutions
	 */

	private Set<ArrayList<Integer>> sampleSolutionIds;

	/**
	 * keep track of the modified justification depths for each sample solution
	 */

	private Map<ArrayList<Integer>, Map<Integer, Integer>> modifiedJustificationDepths;

	/**
	 * keep track of the assertion domain
	 */

	private StringIds assertionDomain;

	/**
	 * implicit assertions are added to the given assertions any time
	 */

	private Set<AssertionInterface> implicitAssertions;

	/**
	 * ids of the implicit assertions
	 */

	private Set<Integer> implicitIds;

	/**
	 * these are correct assertions given by the expert, that are used to
	 * determine the correct assertions wrt. the assertion domain
	 */

	private Set<AssertionInterface> correctAssertionBase;

	/**
	 * these are the correct assertions that have been determined from the
	 * correct assertion base by using the inference rules
	 */

	private Set<AssertionInterface> correctAssertions;

	/**
	 * these are the correct assertions that are justified wrt to the problem
	 */

	private Set<AssertionInterface> justifiedSubsetOfCorrectAssertions;

	/**
	 * all basic inference rules wrt. assertion domain
	 */

	private Map<String, InferenceMap> inferenceRules;

	/**
	 * a subset of the basic inference rules which are considered to be trivial,
	 * these rules are used to turn assertions into equivalent assertions that
	 * are formulated slightly differently, such that the other inference rules
	 * may recognize valid reasoning even if they are not presented in the same
	 * syntax as the rules are written. (So you do not have to create a new
	 * inference rulen for every combination of the expressing the same fact as
	 * assertion.)
	 * 
	 */
	private Map<String, InferenceMap> trivialSubsetOfInferenceRules;

	/**
	 * filters that filter out assertions like
	 * 
	 * the current through bulb A is as big as the current through bulb A
	 */
	private Set<ConstrainedAssertionFilter> trivialAssertionFilters;

	/**
	 * filters that filter out assertions like
	 * 
	 * a bigger current means a smaller current
	 */
	private Set<ConstrainedAssertionFilter> invalidAssertionFilters;

	/**
	 * filters that filter out assertions that need no more justification
	 * regarding the problem, like
	 * 
	 * bulb A is connected in parallel with bulb chain BC
	 */
	private Set<ConstrainedAssertionFilter> justifiedAssertionFilters;

	/**
	 * filters that filter out assertions that have the correct form to be
	 * considered a conclusion rather than only a point in the reasoning
	 */
	private Set<ConstrainedAssertionFilter> concludingAssertionFilters;

	/**
	 * save the premises and the conclusion of all inference rule applications
	 */
	private Set<DirectedHyperEdge> inferenceHyperGraph;

	/**
	 * save the ids of the trivial assertions
	 */
	private Set<Integer> trivialAssertionIds;

	/**
	 * save the ids of the assertions that have to be justified by the student
	 * 
	 */

	private Set<Integer> todoJustifyIds;

	/**
	 * save the ids of the concluding assertions
	 */
	private Set<Integer> concludingAssertionIds;

	/**
	 * save the ids that correspond to some special kind of conclusion
	 */

	private Map<String, Set<Integer>> solutionPartIds;

	/**
	 * save the ids that correspond to some quality of lacking information
	 */

	private Map<String, Set<Integer>> lackingQualityIds;

	/**
	 * save the justification depths for assertions that are not justified yet
	 * justifyable
	 */

	private Map<Integer, Integer> justificationDepths;

	/**
	 * keep track of the inference xml data structure
	 */

	private XmlRootTag inferenceRoot;

	public InferenceCompiler() {

		this.excessTimeLimit = defaultExcessTimeLimit;

		this.resetInferenceCompiler();
	}

	/**
	 * reset the class object to the default initial state
	 */

	private void resetInferenceCompiler() {

		this.assertionDomain = new StringIds();
		this.inferenceRoot = new XmlRootTag();
		this.implicitAssertions = new HashSet<AssertionInterface>();
		this.correctAssertionBase = new HashSet<AssertionInterface>();
		this.correctAssertions = new HashSet<AssertionInterface>();
		this.justifiedSubsetOfCorrectAssertions = new HashSet<AssertionInterface>();
		this.inferenceRules = new HashMap<String, InferenceMap>();
		this.trivialSubsetOfInferenceRules = new HashMap<String, InferenceMap>();

		this.trivialAssertionFilters = new HashSet<ConstrainedAssertionFilter>();
		this.invalidAssertionFilters = new HashSet<ConstrainedAssertionFilter>();
		this.justifiedAssertionFilters = new HashSet<ConstrainedAssertionFilter>();
		this.concludingAssertionFilters = new HashSet<ConstrainedAssertionFilter>();

		this.inferenceHyperGraph = new HashSet<DirectedHyperEdge>();
		this.trivialAssertionIds = new HashSet<Integer>();
		this.concludingAssertionIds = new HashSet<Integer>();

		this.solutionPartIds = new HashMap<String, Set<Integer>>();
		this.justificationDepths = new HashMap<Integer, Integer>();


		this.lackingQualityIds = new HashMap<String, Set<Integer>>();

		this.todoJustifyIds = new HashSet<Integer>();
		this.implicitIds = new HashSet<Integer>();

		this.sampleSolutionIds = new HashSet<ArrayList<Integer>>();
		this.modifiedJustificationDepths = new HashMap<ArrayList<Integer>, Map<Integer, Integer>>();
	}

	/**
	 * 
	 * Writes the HTML code needed to create an inference machine from the
	 * processed xml data using EFJS
	 * 
	 * @param writer
	 * @param jsAcceptTagsArray
	 *            accept tags as java script array (like "[\"good\"]" )
	 * @param jsRejectTagsArray
	 *            reject tags as ...
	 * @param pointsTag
	 *            points tag (as plain string)
	 * @param conclusionsTag
	 *            conclusion tag (as plain string)
	 * @param machineOptions
	 *            more options for the machine, this string is appended to the
	 *            output just before the .WriteHtml() part
	 * @throws IOException
	 */

	public void writeInferenceMachineCode(Writer writer,
			String name,
			String jsAcceptTagsArray, String jsRejectTagsArray,
			String pointsTag, String conclusionsTag, String machineOptions)
					throws IOException {
		writer.write("<script type=\"text/javascript\">");

		writer.write("new InferenceMachine(");
		writer.write("\"" + StringEscape.escapeToJavaScript(name) + "\", ");

		writer.write(jsAcceptTagsArray + ", ");
		writer.write(jsRejectTagsArray + ", ");

		writer.write(this.assertionDomain.getJSCode() + ", ");

		/**
		 * Write the inference hyper graph object that contains all data on the
		 * assertion domain that is needed to check student solutions and
		 * provide feedback
		 */

		writer.write("new InferenceGraph()");

		for (DirectedHyperEdge e : this.inferenceHyperGraph) {
			writer.write(".AddInference([");

			for (Integer p : e.getPremise().getPremise()) {
				writer.write(StringEscape.obfuscateInt(p) + ",");
			}

			writer.write("], [");

			for (Integer c : e.getConclusions()) {
				writer.write(StringEscape.obfuscateInt(c) + ",");
			}

			writer.write("], ");

			if (e.isTrivial()) {
				writer.write("true");
			} else {
				writer.write("false");
			}

			writer.write(")");
		}

		/**
		 * trivial assertions
		 */
		writer.write(".AddTrivial([");

		for (Integer id : this.trivialAssertionIds) {
			writer.write(StringEscape.obfuscateInt(id) + ", ");
		}

		writer.write("])");

		/**
		 * concluding assertions
		 */
		writer.write(".AddConcluding([");

		for (Integer id : this.concludingAssertionIds) {
			writer.write(StringEscape.obfuscateInt(id) + ", ");
		}

		writer.write("])");

		/**
		 * solution parts
		 */

		for (String solution_part : this.solutionPartIds.keySet()) {

			writer.write(".AddSolutionPart(");

			writer.write(StringEscape.escapeToDecodeInJavaScript(solution_part));

			writer.write(", [");

			for (int id : this.solutionPartIds.get(solution_part)) {

				writer.write(StringEscape.obfuscateInt(id) + ", ");
			}

			writer.write("])");
		}

		/**
		 * lack qualities
		 */

		for (String quality_name : this.lackingQualityIds.keySet()) {

			writer.write(".AddLackPart(");

			writer.write(StringEscape.escapeToDecodeInJavaScript(quality_name));

			writer.write(", [");

			for (int id : this.lackingQualityIds.get(quality_name)) {

				writer.write(StringEscape.obfuscateInt(id) + ", ");
			}

			writer.write("])");
		}

		/**
		 * correct assertions
		 */
		writer.write(".AddCorrect([");

		for (AssertionInterface a : this.correctAssertions) {
			int id = this.assertionDomain.fromAssertion(a);

			if (id > 0) {
				writer.write(StringEscape.obfuscateInt(id) + ", ");
			} else {

				/**
				 * if the id < 0, then the assertion is not part of the
				 * assertion domain, so it may be a helper assertion that is
				 * added via implicit.
				 * 
				 * For instance
				 * 
				 * bulb A is a bulb
				 * 
				 * is not a part of the assertion domain, yet "is a bulb" is
				 * used as a set predicate to make inference rules easier.
				 */

				System.err.println("Warning: Correct, yet not in domain: "
						+ a.toString());
			}
		}

		writer.write("])");

		/**
		 * justified assertions
		 */

		writer.write(".AddJustified([");

		for (AssertionInterface a : this.justifiedSubsetOfCorrectAssertions) {
			int id = this.assertionDomain.fromAssertion(a);

			if (id > 0) {
				writer.write(StringEscape.obfuscateInt(id) + ", ");
			} else {

				/**
				 * if the id < 0, then the assertion is not part of the
				 * assertion domain, so it may be a helper assertion that is
				 * added via implicit.
				 * 
				 * These warnings should not occur!!
				 */

				System.err
				.println("WARNING: Correct and justified, yet not in domain: "
						+ a.toString());
			}
		}

		writer.write("])");

		/**
		 * write the justification depths for the correct assertions, that are
		 * not justified themselves
		 */

		writer.write(".SetJustificationDepths([");

		for (Integer assertion_id : this.justificationDepths.keySet()) {
			writer.write(StringEscape.obfuscateInt(assertion_id) + ", "
					+ this.justificationDepths.get(assertion_id) + ", ");
		}

		writer.write("])");

		for (ArrayList<Integer> solution : this.sampleSolutionIds) {
			/**
			 * we want to keep the order of the sample solutions, so we do not
			 * iterate over this.modifiedJustificationsDepths.values() here.
			 */
			Map<Integer, Integer> modifiedDepths = this.modifiedJustificationDepths
					.get(solution);

			writer.write(".AddModifiedJustificationDepths([");

			for (Integer assertion_id : modifiedDepths.keySet()) {
				writer.write(StringEscape.obfuscateInt(assertion_id) + ", "
						+ modifiedDepths.get(assertion_id) + ", ");
			}

			writer.write("])");

		}

		/**
		 * write the rest of the stuff
		 */

		writer.write(", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(pointsTag) + "\", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(conclusionsTag)
				+ "\"");
		writer.write(")");

		/**
		 * add processed options from the efml file
		 */

		writer.write(machineOptions);

		/**
		 * if there are justifications in the to-do, write them down
		 */

		if (this.todoJustifyIds.isEmpty() == false) {
			writer.write(".JustifyToDo([");
			for (Integer id : this.todoJustifyIds) {
				writer.write(StringEscape.obfuscateInt(id) + ", ");
			}
			writer.write("])");
		}

		/**
		 * if there are any sample solutions, write them down
		 */

		if (this.sampleSolutionIds.isEmpty() == false) {
			writer.write(".SampleSolutions([");
			for (ArrayList<Integer> solution : this.sampleSolutionIds) {
				writer.write(" [");
				for (Integer id : solution) {
					writer.write(StringEscape.obfuscateInt(id) + ", ");
				}
				writer.write(" ], ");
			}
			writer.write("])");
		}

		/**
		 * also, get the ids for the implicit assertions
		 */
		if (this.implicitIds.isEmpty() == false) {
			writer.write(".ImplicitPoints([");
			for (Integer id : this.implicitIds) {
				writer.write(StringEscape.obfuscateInt(id) + ", ");
			}
			writer.write("])");
		}

		writer.write(".WriteHtml();");
		writer.write("</script>");
	}

	/**
	 * 
	 * This routine processes the inference xml, that has been embedded into the
	 * efml file
	 * 
	 * @param xml
	 *            inference xml that was embedded in the inference tag
	 * 
	 * @return Error reports :) (May be embedded in the generated HTML file)
	 */

	public String processXmlData(ArrayList<EmbeddedInferenceXmlTag> xml) {
		StringBuffer errors = new StringBuffer();

		/**
		 * First, we need to translate the inference xml data in order to
		 * process them
		 * 
		 * The assertionDomain is not used by the inference machine back end, so
		 * we have to keep track of this here.
		 * 
		 * For the inference stuff, we want to recycle code from the
		 * InferenceMachine class, so we need to translate the
		 * EmbeddedInferenceXmlTag interface objects to XmlTag objects
		 */

		try {

			OUTER: for (EmbeddedInferenceXmlTag t : xml) {
				// System.err.println("TAG: " + t.getTagClass());
				if (t.getTagClass().equalsIgnoreCase("domain")) {
					// System.err.println("DOMAIN TAG");
					/**
					 * the domain tags contain the information about how to
					 * produce the assertion domain by point-wise concatenation
					 * of string sets
					 */

					if (t.hasChildren() == false) {
						continue OUTER;
					}

					// System.err.println("FACTORS");

					ArrayList<ArrayList<String>> factors = new ArrayList<ArrayList<String>>();

					for (EmbeddedInferenceXmlTag f : t.getChildren()) {
						ArrayList<String> factor = new ArrayList<String>();
						if (f.getTagClass().equalsIgnoreCase("q")) {
							/**
							 * add constant factor as singleton set
							 */
							factor.add(f.getStringContent());
							// System.err.println("Q");
						} else if (f.getTagClass().equalsIgnoreCase("factor")) {
							// System.err.println("FACTOR");
							if (f.hasChildren() == false) {
								continue OUTER;
							}
							// System.err.println("NONEMPTY");
							for (EmbeddedInferenceXmlTag q : f.getChildren()) {
								factor.add(q.getStringContent());
							}
						} else {
							if (f.getTagClass().equalsIgnoreCase("#PCDATA") == false) {
								System.err
								.println("Unrecognized domain subtag: "
										+ f.getTagClass());
							}
							continue;
						}

						factors.add(factor);
					}

					/**
					 * update the assertionDomain object: add the new generating
					 * product
					 */
					// System.err.println("ADD PRODUCT");
					this.assertionDomain.addStringProduct(factors);
				} else {
					/**
					 * this tag is handled by the inference xml back end
					 */
					this.inferenceRoot.addChild(new XmlTag(t));
				}

			}
		} catch (Exception e) {

			/**
			 * an error occurred, now format it nicely in HTML so that the user
			 * can spot it and take actions
			 */

			errors.append("<div class=\"compilererror\">");
			errors.append("<h3 class=\"compilererror\">Inference Compiler Error</h3><br/>");
			errors.append(StringEscape.escapeToHtml(e.getClass().getName())
					+ ": ");
			errors.append("<em>"
					+ StringEscape.escapeToHtml(e.getMessage())
					+ "</em><br/>");
			errors.append("<h3 class=\"compilererror\">Stack Trace</h3><br/>");
			errors.append("<table class=\"stacktrace\">");

			StackTraceElement[] stackTrace = e.getStackTrace();

			for (StackTraceElement element : stackTrace) {
				errors.append("<tr><td class=\"stacktracefile\">");
				errors.append(StringEscape.escapeToHtml(element
						.getFileName()));
				errors.append("</td><td class=\"stacktraceline\">");
				errors.append(StringEscape.escapeToHtml(""
						+ element.getLineNumber()));
				errors.append("</td><td class=\"stacktrace\">");
				errors.append(StringEscape.escapeToHtml(element
						.getClassName() + "." + element.getMethodName()));
				errors.append("</td></tr>");
			}

			errors.append("</table>");

			errors.append("</div>");
		}

		/**
		 * transfer data to intermediate structures
		 */


		this.implicitAssertions.addAll(this.inferenceRoot
				.getImplicitAssertions());
		this.correctAssertionBase.addAll(this.inferenceRoot
				.getExpertAssertions());

		Map<String, InferenceMap> updated_rules = this.inferenceRoot
				.getInferenceMapsByName();
		for (String key : updated_rules.keySet()) {
			this.inferenceRules.put(key, updated_rules.get(key));
		}

		updated_rules = this.inferenceRoot.getTrivialInferenceMapsByName();
		for (String key : updated_rules.keySet()) {
			this.trivialSubsetOfInferenceRules.put(key, updated_rules.get(key));
		}

		this.trivialAssertionFilters.addAll(this.inferenceRoot
				.getTrivialityFilters());
		this.invalidAssertionFilters.addAll(this.inferenceRoot
				.getInvalidityFilters());
		this.justifiedAssertionFilters.addAll(this.inferenceRoot
				.getJustifiedFilters());
		this.concludingAssertionFilters.addAll(this.inferenceRoot
				.getConclusionFilters());

		/**
		 * add the sample solutions, as we need them to modify the justification
		 * depths
		 */

		for (ArrayList<String> solution : this.inferenceRoot
				.getSampleSolutions()) {
			ArrayList<Integer> solutionIds = new ArrayList<Integer>();

			for (String part : solution) {
				int id = this.assertionDomain.fromString(part);
				if (id < 0) {
					System.err
					.println("ERROR!! Sample Solution Point not in assertion domain: "
							+ part);
				} else {
					solutionIds.add(id);
				}
			}

			this.sampleSolutionIds.add(solutionIds);

			/**
			 * furthermore, we create a new structure that will hold the
			 * rectified justification depth scores needed to direct a given
			 * solution towards the sample solution.
			 */

			this.modifiedJustificationDepths.put(solutionIds,
					new HashMap<Integer, Integer>());
		}

		/***
		 * Second, we need to build the inference hyper graph, the correct
		 * assertion set and the trivial assertion set
		 */

		/*******
		 * infer correct assertions
		 *******
		 */

		/**
		 * AssertionEquivalenceClasses take care of the inference history of
		 * assertions
		 * 
		 * Adding AssertionInferface sets via addNewAssertions will create
		 * interfacing EquivalentAssertion objects, which can be accessed with
		 * .getClasses()
		 * 
		 */
		AssertionEquivalenceClasses implicitClasses = new AssertionEquivalenceClasses(
				this.sampleSolutionIds, this.assertionDomain);
		implicitClasses.addNewAssertions(this.implicitAssertions);

		AssertionEquivalenceClasses correctBaseClasses = new AssertionEquivalenceClasses(
				this.sampleSolutionIds, this.assertionDomain);
		correctBaseClasses.addNewAssertions(this.correctAssertionBase);

		InferableAssertions inferCorrectAssertions = new InferableAssertions(
				implicitClasses.getClasses(), correctBaseClasses.getClasses(),
				this.inferenceRules.values(), this.invalidAssertionFilters,
				this.trivialAssertionFilters, this.sampleSolutionIds,
				this.assertionDomain);

		InferableAssertions.State success = inferCorrectAssertions
				.closeValid(new ExcessLimit(this.excessTimeLimit));

		if (success != InferableAssertions.State.closed) {
			errors.append("<div class=\"compilererror\">");
			errors.append("<h3 class=\"compilererror\">Inference Compiler Error</h3><br/>");

			if (success == InferableAssertions.State.invalid) {
				errors.append("<em>It was possible to infer invalid"
						+ " assertions using the expert assertions"
						+ " and inference rules given!</em><br/>");

				errors.append("<h3 class=\"compilererror\">Inferred Invalid Assertions</h3>");
				errors.append("<table class=\"invalidassertions\">");

				for (AssertionInterface a : inferCorrectAssertions.getInvalid()) {
					errors.append("<tr><td class=\"domainid\">");
					errors.append(this.assertionDomain.fromAssertion(a));
					errors.append("</td><td>");
					errors.append(StringEscape.escapeToHtml(a.toString()));
					errors.append("</td></tr>");
				}
				errors.append("</table>");

				errors.append("<h3 class=\"compilererror\">Other Inferred Assertions</h3>");
				errors.append("<table class=\"inferredassertions\">");

				for (AssertionInterface a : inferCorrectAssertions.getValid()
						.getClasses()) {
					errors.append("<tr><td class=\"domainid\">");
					errors.append(this.assertionDomain.fromAssertion(a));
					errors.append("</td><td>");
					errors.append(StringEscape.escapeToHtml(a.toString()));
					errors.append("</td></tr>");
				}
				errors.append("</table>");
			} else if (success == State.excess) {
				errors.append("<em>It was not possible to find all"
						+ " correct assertions within " + this.excessTimeLimit
						+ " seconds.</em><br/>");

				errors.append("<h3 class=\"compilererror\">Other Inferred Assertions</h3>");
				errors.append("<table class=\"inferredassertions\">");

				for (AssertionInterface a : inferCorrectAssertions.getValid()
						.getClasses()) {
					errors.append("<tr><td class=\"domainid\">");
					errors.append(this.assertionDomain.fromAssertion(a));
					errors.append("</td><td>");
					errors.append(StringEscape.escapeToHtml(a.toString()));
					errors.append("</td></tr>");
				}
				errors.append("</table>");
			} else {
				errors.append("<em>closeValid returned " + success.toString()
						+ "</em>");
			}

			errors.append("</em><br/>");
			errors.append("</div>");
		}

		/**
		 * copy the correct assertions and their ancestor relations
		 */

		this.correctAssertions.addAll(inferCorrectAssertions.getValid()
				.getClasses());

		/**
		 * filter out the justified correct assertions
		 */

		for (ConstrainedAssertionFilter f : this.justifiedAssertionFilters) {
			this.justifiedSubsetOfCorrectAssertions.addAll(f
					.filter(this.correctAssertions));
		}

		/**********
		 * create the hyper edges accordingly
		 **********/

		/**
		 * update the justification levels
		 */

		inferCorrectAssertions.updateJustification(
				this.justifiedAssertionFilters,
				inferCorrectAssertions.getValid(), true);

		/**
		 * calculate the ancestor sets
		 */

		ExcessLimit time_limit = new ExcessLimit(this.excessTimeLimit);

		inferCorrectAssertions.calculateAncestors(time_limit);

		if (time_limit.exceeded()) {
			errors.append("<div class=\"compilererror\">");
			errors.append("<h1>Inference Compiler Error</h1><br/>");

			errors.append("It was not possible to calculate all "
					+ " assertion ancestors within " + this.excessTimeLimit
					+ " seconds.");

			errors.append("</em><br/>");
			errors.append("</div>");
		}

		/**
		 * calculate the pre-images of the assertions
		 */

		time_limit = new ExcessLimit(this.excessTimeLimit);

		inferCorrectAssertions.calculateRuleAncestors(time_limit);

		if (time_limit.exceeded()) {
			errors.append("<div class=\"compilererror\">");
			errors.append("<h1>Inference Compiler Error</h1><br/>");

			errors.append("It was not possible to calculate all "
					+ " assertion pre-image sets within "
					+ this.excessTimeLimit + " seconds.");

			errors.append("</em><br/>");
			errors.append("</div>");
		}

		/**
		 * copy the data to inferenceHyperGraph
		 */

		Set<DirectedHyperEdge> singletonTargetEdges = new HashSet<DirectedHyperEdge>();

		for (AssertionInterface a : inferCorrectAssertions.getInferred()) {
			AnnotableDisjunctiveNormalForm<EquivalentAssertions> premises = inferCorrectAssertions
					.getPreimages(a);
			int id_a = this.assertionDomain.fromAssertion(a);

			if (id_a >= 0) {
				/**
				 * a belongs to the assertionDomain
				 */

				for (Set<EquivalentAssertions> premise : premises
						.getNontrivialPart()) {
					DirectedHyperEdge edge = new DirectedHyperEdge(id_a);

					boolean premise_contains_outside_domain = false;

					for (AssertionInterface p : premise) {
						int id_p = this.assertionDomain.fromAssertion(p);

						if (id_p >= 0) {
							/**
							 * if p is in the assertionDomain as well, it must
							 * be specified to infer a
							 */
							edge.addPremise(id_p);
						} else {

							if (this.implicitAssertions.contains(p) == false) {
								System.err
								.println("WARNING: Ignoring inference involving premise not in domain: "
										+ p.toString());
								premise_contains_outside_domain = true;
							}
						}
					}

					if (premise_contains_outside_domain == false) {
						/**
						 * this premise is nontrivial
						 */

						edge.setTriviality(false);

						singletonTargetEdges.add(edge);
					}
					/**
					 * else: we need an assertion as premise that is not part of
					 * the assertion domain, so this conclusion may not be drawn
					 * from the assertion domain anyway
					 */
				}

				/**
				 * DEBUG CODE
				 */
				// System.err.println(a + " INFERRED BY ");
				// System.err.println(premises.getAnnotatedTerm());
				// System.err.println("TRIVIAL PREMISES: "
				// + premises.getTrivialPart());
				/**
				 * /DEBUGCODE
				 */

				for (Set<EquivalentAssertions> premise : premises
						.getTrivialPart()) {
					DirectedHyperEdge edge = new DirectedHyperEdge(id_a);

					for (AssertionInterface p : premise) {
						int id_p = this.assertionDomain.fromAssertion(p);

						if (id_p >= 0) {
							/**
							 * if p is in the assertionDomain as well, it must
							 * be specified to infer a
							 */
							edge.addPremise(id_p);
						}
					}

					/**
					 * this premise is trivial
					 */

					edge.setTriviality(true);

					singletonTargetEdges.add(edge);
				}

			}
		}

		/**
		 * now, we constructed a hyper graph with singleton edge targets.
		 * 
		 * But: it is more nice to have one edge per premise set
		 */

		Map<DirectedHyperEdgePremise, Set<Integer>> conclusionsByPremise = new HashMap<DirectedHyperEdgePremise, Set<Integer>>();

		for (DirectedHyperEdge e : singletonTargetEdges) {
			DirectedHyperEdgePremise p = e.getPremise();
			if (conclusionsByPremise.containsKey(p) == false) {
				conclusionsByPremise.put(p, new TreeSet<Integer>());
			}

			conclusionsByPremise.get(p).addAll(e.getConclusions());
		}

		for (DirectedHyperEdgePremise p : conclusionsByPremise.keySet()) {

			this.inferenceHyperGraph.add(new DirectedHyperEdge(p.getPremise(),
					conclusionsByPremise.get(p), p.isTrivial()));
		}

		/**
		 * copy the justification depth for unjustified yet justifiable
		 * assertions
		 */

		for (AssertionInterface a : inferCorrectAssertions.getValid()
				.getClasses()) {

			inferCorrectAssertions.getValid().modifiedJustification(a,
					this.modifiedJustificationDepths);

			int justification_level = inferCorrectAssertions.getValid()
					.justification(a);
			if (justification_level == EquivalentAssertions.notJustified) {
				/**
				 * this assertion is correct, yet unjustifiable. This may
				 * indicate incomplete or erroneous inference xml data. So we
				 * warn.
				 */

				System.err.println("WARNING: " + a.toString()
						+ " is correct yet unjustifiable!");

			} else if (justification_level != 0) {
				this.justificationDepths.put(
						this.assertionDomain.fromAssertion(a),
						justification_level);
			}
		}

		/**
		 * we should also keep track of the trivially correct assertions from
		 * the domain
		 */

		SubjectPredicateObjectMatchers parser = this.inferenceRoot.getParsers();

		for (String a : this.assertionDomain.getCaseCorrectStrings()) {
			Set<AssertionInterface> as = new HashSet<AssertionInterface>(
					parser.match(a));

			for (ConstrainedAssertionFilter f : this.trivialAssertionFilters) {
				if (f.filter(as).isEmpty() == false) {
					this.trivialAssertionIds.add(this.assertionDomain
							.fromString(a));
				}
			}
		}

		/**
		 * we should keep track of the concluding assertions from the domain,
		 * too
		 */

		for (String a : this.assertionDomain.getCaseCorrectStrings()) {
			Set<AssertionInterface> as = new HashSet<AssertionInterface>(
					parser.match(a));

			int assertion_id = this.assertionDomain.fromString(a);

			for (ConstrainedAssertionFilter f : this.concludingAssertionFilters) {
				if (f.filter(as).isEmpty() == false) {

					this.concludingAssertionIds.add(assertion_id);

					/**
					 * also, keep the solution parts for the concluding ids
					 */

					for (ConstrainedAssertionFilter part_filter : this.inferenceRoot
							.getPartFilters().keySet()) {

						if (part_filter.filter(as).isEmpty() == false) {
							String solution_part = this.inferenceRoot
									.getPartFilters().get(part_filter);

							if (this.solutionPartIds.containsKey(solution_part) == false) {
								this.solutionPartIds.put(solution_part,
										new HashSet<Integer>());
							}

							this.solutionPartIds.get(solution_part).add(
									assertion_id);

						}

					}

					/**
					 * we found a match for the given id, no need to check the
					 * other conclusion filters anymore
					 */
					break;
				}
			}
		}

		/**
		 * store the information qualities of the correct assertions
		 */

		for (AssertionInterface ai : this.correctAssertions) {
			Set<AssertionInterface> as = new HashSet<AssertionInterface>();
			as.add(ai);

			int assertion_id = this.assertionDomain.fromAssertion(ai);


			for (ConstrainedAssertionFilter quality_filter : this.inferenceRoot
					.getQualityFilters().keySet()) {
				if (quality_filter.filter(as).isEmpty() == false) {
					String quality_name = this.inferenceRoot
							.getQualityFilters().get(quality_filter);

					if (this.lackingQualityIds.containsKey(quality_name) == false) {
						this.lackingQualityIds.put(quality_name,
								new HashSet<Integer>());
					}

					this.lackingQualityIds.get(quality_name).add(assertion_id);
				}

			}
		}

		/**
		 * add the todo justification ids
		 */

		for (String todo : this.inferenceRoot.getJustificationTodos()) {
			int id = this.assertionDomain.fromString(todo);
			if (id < 0) {
				System.err
				.println("ERROR!! Justification To-Do not in assertion domain: "
						+ todo);
			} else {
				this.todoJustifyIds.add(id);
			}
		}


		/**
		 * keep track of the implicit assertions
		 */

		for (AssertionInterface a : this.implicitAssertions) {
			int id = this.assertionDomain.fromAssertion(a);
			if (id < 0) {
				System.err.println("IMPLICIT NOT IN DOMAIN: " + a.toString());
			} else {
				this.implicitIds.add(id);
			}
		}

		return errors.toString();
	}
}
