/**
 * FeedbackDisplayTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;feedbackdisplay>-tag that creates a feedback display
 * object
 * 
 * @author immanuel
 * 
 */

public class FeedbackDisplayTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	public FeedbackDisplayTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new timer object
		 */

		writer.write(" new FeedbackDisplay(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("name", "")) + "\"");

		writer.write(").WriteHtml();");
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write(" </script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {

		} else {
			throw new OperationNotSupportedException("<timer> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<feedbackdisplay");
		this.attributes.writeXmlAttributes(representation);
		representation.append(" />");

		return representation.toString();
	}

}
