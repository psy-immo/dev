/**
 * AllRequirementsTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;allrequirements> subtag for &lt;feedback>
 * 
 * @author immanuel
 * 
 */

public class AllRequirementsTag implements InferenceSolutionRequirementTag {

	private ArrayList<InferenceSolutionRequirementTag> requirements;

	public AllRequirementsTag(EfmlTagsAttribute attributes) {
		this.requirements = new ArrayList<InferenceSolutionRequirementTag>();
	}


	@Override
	public void open(Writer writer) throws IOException {
	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			return;
		}
		if (innerTag instanceof InferenceSolutionRequirementTag) {
			InferenceSolutionRequirementTag requirement = (InferenceSolutionRequirementTag) innerTag;

			this.requirements.add(requirement);
		} else {
			throw new OperationNotSupportedException(
					"<allrequirements> cannot enclose "
							+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<allrequirements");
		representation.append(">");
		for (InferenceSolutionRequirementTag requirement : this.requirements) {
			representation.append(requirement.getEfml());
		}
		representation.append("</allrequirements>");

		return representation.toString();
	}

	@Override
	public String getRequirementJavaScriptCheckFunction() {
		StringBuffer fn = new StringBuffer();
		fn.append("function(parts) { return true ");
		for (InferenceSolutionRequirementTag requirement : this.requirements) {
			fn.append("&& ((");
			fn.append(requirement.getRequirementJavaScriptCheckFunction());
			fn.append(")(parts))");
		}
		fn.append(";}");
		return fn.toString();
	}

}
