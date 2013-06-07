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


/**
 * 
 * class that implements facilities for inference machine compilation, i.e. to
 * create the data needed for the efjs website for qualitative reasoning tasks
 * 
 * @author immo
 * 
 */

public class InferenceCompiler {

	private StringIds assertionDomain;

	public InferenceCompiler() {
		this.resetInferenceCompiler();
	}

	/**
	 * reset the class object to the default initial state
	 */

	private void resetInferenceCompiler() {
		this.assertionDomain = new StringIds();
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

		OUTER:for (EmbeddedInferenceXmlTag t : xml) {
			/**
			 * the domain tags contain the information about how to produce the
			 * assertion domain by point-wise concatenation of string sets
			 */
			if (t.getTagClass().equalsIgnoreCase("domain")) {
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
			}
		}

		errors.append(this.assertionDomain.getJSCode());

		return errors.toString();
	}

}
