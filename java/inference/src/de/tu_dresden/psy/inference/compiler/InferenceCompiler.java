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

import de.tu_dresden.psy.efml.StringEscape;
import de.tu_dresden.psy.inference.AssertionEquivalenceClasses;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;
import de.tu_dresden.psy.inference.regexp.xml.InferableAssertions;
import de.tu_dresden.psy.inference.regexp.xml.InferableAssertions.State;
import de.tu_dresden.psy.inference.regexp.xml.XmlRootTag;
import de.tu_dresden.psy.inference.regexp.xml.XmlTag;

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

	private static final float defaultExcessTimeLimit = 45;

	/**
	 * keep track of the assertion domain
	 */

	private StringIds assertionDomain;

	/**
	 * implicit assertions are added to the given assertions any time
	 */

	private Set<AssertionInterface> implicitAssertions;

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
	private Set<ConstrainedAssertionFilter> conclusiveAssertionFilters;

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
		this.conclusiveAssertionFilters = new HashSet<ConstrainedAssertionFilter>();
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
	 * @throws IOException
	 */

	public void writeInferenceMachineCode(Writer writer,
			String jsAcceptTagsArray, String jsRejectTagsArray,
			String pointsTag, String conclusionsTag) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		writer.write("new InferenceMachine(");

		writer.write(jsAcceptTagsArray + ", ");
		writer.write(jsRejectTagsArray + ", ");

		writer.write(this.assertionDomain.getJSCode() + ", ");

		/**
		 * Write the inference hyper graph object that contains all data on the
		 * assertion domain that is needed to check student solutions and
		 * provide feedback
		 */

		writer.write("new InferenceGraph()");

		/**
		 * TODO: inference rules
		 */

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
		 * write the rest of the stuff
		 */

		writer.write(", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(pointsTag) + "\", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(conclusionsTag)
				+ "\"");
		writer.write(")");

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
				if (t.getTagClass().equalsIgnoreCase("domain")) {
					/**
					 * the domain tags contain the information about how to
					 * produce the assertion domain by point-wise concatenation
					 * of string sets
					 */

					if (t.hasChildren() == false) {
						continue OUTER;
					}

					ArrayList<ArrayList<String>> factors = new ArrayList<ArrayList<String>>();

					for (EmbeddedInferenceXmlTag f : t.getChildren()) {
						ArrayList<String> factor = new ArrayList<String>();
						if (f.getTagClass().equalsIgnoreCase("q")) {
							/**
							 * add constant factor as singleton set
							 */
							factor.add(f.getStringContent());
						} else if (f.getTagClass().equalsIgnoreCase("factor")) {
							if (f.hasChildren() == false) {
								continue OUTER;
							}

							for (EmbeddedInferenceXmlTag q : f.getChildren()) {
								factor.add(q.getStringContent());
							}
						}

						factors.add(factor);
					}

					/**
					 * update the assertionDomain object: add the new generating
					 * product
					 */

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
			errors.append("<h1>Inference Compiler Error</h1><br/>");
			errors.append(StringEscape.escapeToHtml(e.getClass().getName())
					+ ": ");
			errors.append("<em>"
					+ StringEscape.escapeToHtml(e.getLocalizedMessage())
					+ "</em><br/>");
			errors.append("<h2>Stack Trace</h2><br/>");
			errors.append("<table class=\"stacktrace\">");

			StackTraceElement[] stackTrace = e.getStackTrace();

			for (int i = 0; i < stackTrace.length; ++i) {
				errors.append("<tr><td class=\"stacktracefile\">");
				errors.append(StringEscape.escapeToHtml(stackTrace[i]
						.getFileName()));
				errors.append("</td><td class=\"stacktraceline\">");
				errors.append(StringEscape.escapeToHtml(""
						+ stackTrace[i].getLineNumber()));
				errors.append("</td><td class=\"stacktrace\">");
				errors.append(StringEscape.escapeToHtml(stackTrace[i]
						.getClassName() + "." + stackTrace[i].getMethodName()));
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
		this.conclusiveAssertionFilters.addAll(this.inferenceRoot
				.getConclusionFilters());

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
		AssertionEquivalenceClasses implicitClasses = new AssertionEquivalenceClasses();
		implicitClasses.addNewAssertions(this.implicitAssertions);

		AssertionEquivalenceClasses correctBaseClasses = new AssertionEquivalenceClasses();
		correctBaseClasses.addNewAssertions(this.correctAssertionBase);

		InferableAssertions inferCorrectAssertions = new InferableAssertions(
				implicitClasses.getClasses(), correctBaseClasses.getClasses(),
				this.inferenceRules.values(), this.invalidAssertionFilters,
				this.trivialAssertionFilters);

		InferableAssertions.State success = inferCorrectAssertions
				.closeValid(new ExcessLimit(this.excessTimeLimit));

		if (success != InferableAssertions.State.closed) {
			errors.append("<div class=\"compilererror\">");
			errors.append("<h1>Inference Compiler Error</h1><br/>");

			if (success == InferableAssertions.State.invalid) {
				errors.append("It was possible to infer invalid"
						+ " assertions using the expert assertions"
						+ " and inference rules given!");
			} else if (success == State.excess) {
				errors.append("It was not possible to find all"
						+ " correct assertions within " + this.excessTimeLimit
						+ " seconds.");
			} else {
				errors.append("closeValid returned " + success.toString());
			}

			errors.append("</em><br/>");
			errors.append("</div>");
		}

		/**
		 * copy the correct assertions and their ancestor relations
		 */

		this.correctAssertions.addAll(inferCorrectAssertions.getInferred());

		/**
		 * filter out the justified correct assertions
		 */

		for (ConstrainedAssertionFilter f : this.justifiedAssertionFilters) {
			this.justifiedSubsetOfCorrectAssertions.addAll(f
					.filter(this.correctAssertions));
		}

		// TODO: save ancestor relations (i.e. inference hyper graph edges)

		return errors.toString();
	}
}
