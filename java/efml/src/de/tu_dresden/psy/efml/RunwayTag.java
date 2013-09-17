/**
 * RunwayTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;runway>-tag modelling an airfield that sends and takes
 * "text" as planes via mouse interaction
 * 
 * @author immanuel
 * 
 */

public class RunwayTag implements NestedTag {

	private EfmlTagsAttribute attributes;
	private String token;

	public RunwayTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.token = "";
	}

	@Override
	public void open(Writer writer) throws IOException {

		/**
		 * in stand-alone mode, we need to open a javascript tag first
		 */
		writer.write("<script type=\"text/javascript\">");

		this.createNew(writer, "");

		/**
		 * finally, call the javascript code to render the html elements
		 */

		writer.write(".WriteHtml();");

		/**
		 * we close the script tag again
		 */
		writer.write(" </script>");
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * do nothing
		 */
	}

	@Override
	public void createNew(Writer writer, String identificationToken)
			throws IOException {

		/**
		 * create new javascript Runway object with name, tags, token, accept,
		 * reject
		 */

		writer.write(" new Runway(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("name", identificationToken))
						+ "\", ");
		writer.write(this.attributes.getTags() + ", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(this.token)
				+ "\", ");

		writer.write(this.attributes.getAcceptTags() + ", ");
		writer.write(this.attributes.getRejectTags() + ")");

		/**
		 * content attribute will change behavior,
		 * 
		 * respawn if contents disappear from screen, they will respawn in this
		 * run way refill contents will be refilled instantly after take off
		 */

		String content = this.attributes.getValueOrDefault("content", "")
				.trim();

		if (content.equalsIgnoreCase("RESPAWN")) {
			writer.write(".Respawn()");

		} else if (content.equalsIgnoreCase("REFILLING")) {
			writer.write(".Refilling()");
		}

		/**
		 * set the run way CSS classes for empty and filled runways
		 */

		if ((this.attributes.getValueOrDefault("empty", null) != null)
				|| (this.attributes.getValueOrDefault("filled", null) != null)) {
			String empty = this.attributes.getValueOrDefault("empty",
					"runwayEmpty");
			String filled = this.attributes.getValueOrDefault("filled",
					"runwayFilled");

			writer.write(".Color(\"" + StringEscape.escapeToJavaScript(empty)
					+ "\", \"" + StringEscape.escapeToJavaScript(filled)
					+ "\")");
		}

		/**
		 * set the size parameter for the run way
		 * 
		 * width, height (note: give CSS sizes, e.g. 200px)
		 */

		if ((this.attributes.getValueOrDefault("width", null) != null)
				|| (this.attributes.getValueOrDefault("height", null) != null)) {
			String width = this.attributes.getValueOrDefault("width", "200px");
			String height = this.attributes.getValueOrDefault("height", "20px");

			writer.write(".Size(\"" + StringEscape.escapeToJavaScript(width)
					+ "\", \"" + StringEscape.escapeToJavaScript(height)
					+ "\")");
		}
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getContent();
		} else {
			throw new OperationNotSupportedException("<runway> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<runway");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(this.token);
		representation.append("</runway>");

		return representation.toString();
	}

}
