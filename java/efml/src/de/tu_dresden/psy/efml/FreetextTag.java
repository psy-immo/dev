/**
 * FreetextTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;freetext>-tag for having a drop down box
 * 
 * @author immanuel
 * 
 */

public class FreetextTag implements AnyTag, NestedTag {

	private EfmlTagsAttribute attributes;
	private String label;
	

	public FreetextTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.label = "";

	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new javascript dropdown object with name, tags, label, token
		 */

		createNew(writer,"");

		/**
		 * finally let javascript create the html code
		 */

		writer.write(".WriteHtml();");
	}
	
	@Override
	public void createNew(Writer writer, String identificationToken ) throws IOException {
		/**
		 * create new javascript freetext object with name, tags, label, token
		 */

		writer.write(" new Freetext(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", identificationToken)) + "\", ");
		writer.write(attributes.getTags() + ", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(label) + "\", ");

		writer.write("\"" +StringEscape.escapeToJavaScript(attributes
				.getValueOrDefault("value", "")) + "\")");


		/**
		 * set the background colors
		 * 
		 */

		if ((attributes.getValueOrDefault("color", null) != null)
				|| (attributes.getValueOrDefault("filled", null) != null)) {
			String empty = attributes.getValueOrDefault("color", "#CCCCCC");
			String filled = attributes.getValueOrDefault("filled", "#CCCCFF");

			writer.write(".Color(\"" + StringEscape.escapeToJavaScript(empty)
					+ "\", \"" + StringEscape.escapeToJavaScript(filled)
					+ "\")");
		}

		/**
		 * set the size parameter
		 * 
		 * width, height (note: give CSS sizes, e.g. 200px)
		 */

		if ((attributes.getValueOrDefault("width", null) != null)
				|| (attributes.getValueOrDefault("height", null) != null)) {
			String width = attributes.getValueOrDefault("width", "");
			String height = attributes.getValueOrDefault("height", "");

			writer.write(".Size(\"" + StringEscape.escapeToJavaScript(width)
					+ "\", \"" + StringEscape.escapeToJavaScript(height)
					+ "\")");
		}
		

	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write(" </script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.label += ((PlainContent) innerTag).getPlainContent().trim();
		} else
			throw new OperationNotSupportedException(
					"<freetext> cannot enclose "
							+ innerTag.getClass().toString());
	}

}
