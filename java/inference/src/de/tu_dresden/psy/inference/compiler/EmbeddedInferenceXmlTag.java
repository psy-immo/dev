/**
 * EmbeddedInferenceXmlTag.java, (c) 2013, Immanuel Albrecht; Dresden University
 * of Technology, Professur für die Psychologie des Lernen und Lehrens
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
 * @author immo
 * 
 *         this interface is for sending embedded inference xml from the efml
 *         compiler to the inference compiler
 * 
 */

public interface EmbeddedInferenceXmlTag {

	/**
	 * 
	 * @return the class name of the tag, i.e. <q>a</q> has the class name "q",
	 *         plain data has the class name "#PCDATA"
	 */

	public String getTagClass();

	/**
	 * 
	 * @return true, if there are children. In that case, getChildren may not
	 *         return null.
	 */

	public boolean hasChildren();

	/**
	 * 
	 * @return all child tags, i.e. <tag><child1>...<childK></tag> <b> MAY
	 *         RETURN ZERO </b> if there are no child tags
	 */

	public ArrayList<EmbeddedInferenceXmlTag> getChildren();
}
