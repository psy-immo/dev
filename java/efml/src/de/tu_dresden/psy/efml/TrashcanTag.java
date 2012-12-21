/**
 * TrashcanTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;trashcan>-tag for deleting the landing token
 * 
 * @author albrecht
 * 
 */

public class TrashcanTag implements AnyTag {

	private EfmlTagsAttribute attributes;


	public TrashcanTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;

	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		writer.write("new Trashcan(");

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

		/**
		 * colors
		 */

		if ((attributes.getValueOrDefault("color", null) != null)
				|| (attributes.getValueOrDefault("filled", null) != null)) {
			String empty = attributes.getValueOrDefault("color", "#CCCCCC");
			String filled = attributes.getValueOrDefault("filled", "#CCCCFF");

			writer.write(".Color(\"" + StringEscape.escapeToJavaScript(empty)
					+ "\", \"" + StringEscape.escapeToJavaScript(filled)
					+ "\")");
		}

		writer.write(".WriteHtml();");

	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {

			throw new OperationNotSupportedException(
				"<trashcan> cannot enclose tags ("
						+ innerTag.getClass().toString() + ")");

	}
	
	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();
		
		representation.append("<trashcan");
		attributes.writeXmlAttributes(representation);
		representation.append("/>");
		
		return representation.toString();
	}


}
