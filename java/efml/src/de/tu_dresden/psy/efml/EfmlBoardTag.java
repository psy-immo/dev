/**
 * EfmlBoardTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;efmlboard>-tag for visual editing of efml data
 * 
 * @author albrecht
 * 
 */

public class EfmlBoardTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	private String label;

	public EfmlBoardTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.label = "";
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		writer.write("new EfmlBoard(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", "")) + "\", ");

		writer.write(attributes.getTags() + ", ");

		writer.write(attributes.getAcceptTags() + ", ");
		writer.write(attributes.getRejectTags() + ")");
		
		/**
		 * dimensions
		 */

		attributes.writeIfValueGiven(writer, ".Width(\"", "width", "\")");
		attributes.writeIfValueGiven(writer, ".Height(\"", "height", "\")");

		writer.write(".WriteHtml();");

	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {

		// TODO: make efml tags includable !!

		if (innerTag.getClass() == PlainContent.class) {
			this.label += ((PlainContent) innerTag).getPlainContent();
		} else
			throw new OperationNotSupportedException(
					"<efmlboard> cannot enclose "
							+ innerTag.getClass().toString());

	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<efmlboard");
		attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.label));
		representation.append("</efmlboard>");

		return representation.toString();
	}

}
