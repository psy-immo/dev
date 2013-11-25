/**
 * CallTag.java, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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
 * implements the &lt;call>...&lt;/call> tag for remotebuttons
 * 
 * @author immanuel
 * 
 */

public class CallTag implements AnyTag {

	private String token;

	private EfmlTagsAttribute attributes;

	public CallTag(EfmlTagsAttribute attributes) {
		this.token = "";
		this.attributes = attributes;
	}

	/**
	 * 
	 * @return java script function as code, that calls the slave
	 */

	public String getJsCode() {
		String type = this.attributes.getValueOrDefault("type", "");
		if (type.equalsIgnoreCase("inference")) {
			return "function(){var x = myInferenceMachineNames[\""
					+ StringEscape.escapeToJavaScript(this.token.trim())
					+ "\"]; x.StartMachine(); return x.done;}";
		}

		if (type.equalsIgnoreCase("answer")) {
			return "function(){var x = answerNames[\""
					+ StringEscape.escapeToJavaScript(this.token.trim())
					+ "\"]; x.OnClick(); return x.done;}";
		}

		return "function(){}";
	};

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
			this.token += ((PlainContent) innerTag).getContent();
		} else {
			throw new OperationNotSupportedException("<call> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<call");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.token));
		representation.append("</call>");

		return representation.toString();
	}

}
