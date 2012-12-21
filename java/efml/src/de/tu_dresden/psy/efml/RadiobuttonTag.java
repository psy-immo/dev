/**
 * RadiobuttonTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;radiobutton>-tag for having a drop down box
 * 
 * @author immanuel
 * 
 */

public class RadiobuttonTag implements AnyTag, NestedTag {

	private EfmlTagsAttribute attributes;
	private String label;
	
	private ArrayList<OptionTag> options;

	public RadiobuttonTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.label = "";
		this.options = new ArrayList<OptionTag>();
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
		 * create new javascript dropdown object with name, tags, label, token
		 */

		writer.write(" new RadioButton(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", identificationToken)) + "\", ");
		writer.write(attributes.getTags() + ", ");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"type", "form")) + "\", ");

		writer.write("\"" +StringEscape.escapeToJavaScript(attributes
				.getValueOrDefault("value", "")) + "\")");

		if (this.label.isEmpty() == false) {
			writer.write(".Label(\""
					+ StringEscape.escapeToJavaScript(this.label) + "\")");
		}


		/**
		 * set the background colors for the drop down box
		 * 
		 * color empty background color filled background color when filled with
		 * token
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
		 * set the size parameter for the drop down
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
		
		/**
		 * now add all drop down box options
		 */
		
		Iterator<OptionTag> it;
		for (it=options.iterator();it.hasNext();){
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
			this.label += ((PlainContent) innerTag).getPlainContent().trim();
		} else if (innerTag.getClass() == OptionTag.class) {
			this.options.add((OptionTag) innerTag);
		} else
			throw new OperationNotSupportedException(
					"<radiobutton> cannot enclose "
							+ innerTag.getClass().toString());
	}
	
	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();
		
		representation.append("<radiobutton");
		attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.label));
		for (AnyTag t: options) {
			representation.append(t.getEfml());
		}
		representation.append("</radiobutton>");
		
		return representation.toString();
	}

}
