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

import java.util.ArrayList;

import de.tu_dresden.psy.efml.StringEscape;
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
	 * keep track of the assertion domain
	 */

	private StringIds assertionDomain;

	/**
	 * keep track of the inference xml data structure
	 */

	private XmlRootTag inferenceRoot;

	public InferenceCompiler() {
		this.resetInferenceCompiler();
	}

	/**
	 * reset the class object to the default initial state
	 */

	private void resetInferenceCompiler() {
		this.assertionDomain = new StringIds();
		this.inferenceRoot = new XmlRootTag();
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

		try {

			/**
			 * First, we need to translate the inference xml data in order to
			 * process them
			 * 
			 * The assertionDomain is not used by the inference machine back
			 * end, so we have to keep track of this here.
			 * 
			 * For the inference stuff, we want to recycle code from the
			 * InferenceMachine class, so we need to translate the
			 * EmbeddedInferenceXmlTag interface objects to XmlTag objects
			 */

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
			 * an error occured, now format it nicely in HTML so that the user
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

		System.out.print("IROOT= " + this.inferenceRoot.getExpertAssertions());

		return errors.toString();
	}

}
