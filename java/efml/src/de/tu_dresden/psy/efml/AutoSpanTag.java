/**
 * AutoSpanTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
import java.util.Vector;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;autospan>-tag that creates a span that is displayed
 * optionally object
 * 
 * @author immanuel
 * 
 */

public class AutoSpanTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	private Vector<AnyTag> innerTags;

	public AutoSpanTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.innerTags = new Vector<AnyTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new auto span
		 */

		writer.write(" new AutoSpan(");

		writer.write(this.attributes.getValueOrDefault("value", "0") + ", ");
		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("class", "")) + "\",");
		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("name", "")) + "\"");

		writer.write(").WriteHtml();");
		writer.write(" </script>");

		/**
		 * create the contents inside the autospan <span> tag
		 */

		for (AnyTag t : this.innerTags) {
			t.open(writer);
			t.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {

		/**
		 * close the autospan <span> tag gracefully (just writing </span> would
		 * break xml conformity of the resulting document)
		 */
		writer.write("<script type=\"text/javascript\">");
		writer.write("document.write(\"</span>\");");
		writer.write(" </script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		this.innerTags.add(innerTag);
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<autospan");
		this.attributes.writeXmlAttributes(representation);
		representation.append(" />");

		return representation.toString();
	}

}
