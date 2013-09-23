/**
 * AutomaticTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;automatic>-tag for having a magically changing text
 * element inside a &lt;template>-box
 * 
 * @author immanuel
 * 
 */

public class AutomaticTag implements AnyTag, NestedTag {

	private EfmlTagsAttribute attributes;
	private String defaultToken;

	private ArrayList<RegexpTag> patterns;

	public AutomaticTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.defaultToken = "";
		this.patterns = new ArrayList<RegexpTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new javascript dropdown object with name, tags, label, token
		 */

		this.createNew(writer, "");

		/**
		 * finally let javascript create the html code
		 */

		writer.write(".WriteHtml();");
	}

	@Override
	public void createNew(Writer writer, String identificationToken)
			throws IOException {
		/**
		 * create new javascript dropdown object with name, tags, label, token
		 */

		writer.write(" new FlexText(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("name", identificationToken))
						+ "\", ");
		writer.write(this.attributes.getTags() + ", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(this.defaultToken)
				+ "\", ");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("value", "")) + "\")");


		/**
		 * set the size parameter for the drop down
		 * 
		 * width, height (note: give CSS sizes, e.g. 200px)
		 */

		if ((this.attributes.getValueOrDefault("width", null) != null)
				|| (this.attributes.getValueOrDefault("height", null) != null)) {
			String width = this.attributes.getValueOrDefault("width", "");
			String height = this.attributes.getValueOrDefault("height", "");

			writer.write(".Size(\"" + StringEscape.escapeToJavaScript(width)
					+ "\", \"" + StringEscape.escapeToJavaScript(height)
					+ "\")");
		}



		/**
		 * now add all matcher patterns
		 */

		Iterator<RegexpTag> it;
		for (it = this.patterns.iterator(); it.hasNext();) {
			writer.write(it.next().getJsContent());
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
		} else if (innerTag.getClass() == DefaultTag.class) {
			DefaultTag d = (DefaultTag) innerTag;

			this.defaultToken += d.getPlainContent();
		} else if (innerTag.getClass() == RegexpTag.class) {
			this.patterns.add((RegexpTag) innerTag);
		} else {
			throw new OperationNotSupportedException(
					"<automatic> cannot enclose "
							+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<automatic");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append("<default>");
		representation.append(StringEscape.escapeToXml(this.defaultToken));
		representation.append("</default>");
		for (AnyTag child : this.patterns) {
			representation.append(child.getEfml());
		}
		representation.append("</automatic>");

		return representation.toString();
	}

}
