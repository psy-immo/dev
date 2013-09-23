/**
 * RegexpTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;regexp>...&lt;/regexp> tag
 * 
 * @author immanuel
 * 
 */

public class RegexpTag implements AnyTag {

	private String regularExpression;
	private EfmlTagsAttribute attributes;

	public RegexpTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.regularExpression = "";
	}

	public String getJsContent() {

		String javaScript = ".Check";
		String where = this.attributes.getValueOrDefault("direction", "right");

		if (where.equalsIgnoreCase("LEFT")) {
			javaScript += "Left";
		} else if (where.equalsIgnoreCase("BOTH")) {
			javaScript += "Both";
		} else {
			javaScript += "Right";
		}
		javaScript += "(";
		if (this.regularExpression.startsWith("/")) {
			javaScript += this.regularExpression + ",";
		} else {
			javaScript += "/";
			javaScript += this.regularExpression;
			javaScript += "/, ";
		}

		javaScript += "\""
				+ StringEscape.escapeToJavaScript(this.attributes
						.getValueOrDefault("value", "")) + "\"";

		return javaScript + ")";
	}

	@Override
	public void open(Writer writer) throws IOException {
		/**
		 * noop
		 */
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * noop
		 */
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.regularExpression += ((PlainContent) innerTag).getPlainContent();
		} else {
			throw new OperationNotSupportedException("<regexp> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<regexp");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.regularExpression));
		representation.append("</regexp>");

		return representation.toString();
	}

}
