/**
 * OptionTag.java, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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
 * implements the &lt;option>...&lt;/option> tag
 * 
 * @author immanuel
 * 
 */

public class OptionTag implements AnyTag {

	private String token;
	private EfmlTagsAttribute attributes;

	public OptionTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.token = "";
	}

	public String getJsContent() {
		String javaScript = ".Option(\""
				+ StringEscape.escapeToJavaScript(this.token) + "\"";
		if (attributes.getValueOrDefault("value", null) != null) {
			javaScript += ",\""
					+ StringEscape.escapeToJavaScript(attributes
							.getValueOrDefault("value", "")) + "\"";
		}
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
			this.token += ((PlainContent) innerTag).getPlainContent();
		} else
			throw new OperationNotSupportedException("<option> cannot enclose "
					+ innerTag.getClass().toString());
	}

}
