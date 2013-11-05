/**
 * ResourceLanguageTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
 * 
 * 
 * implements the &lt;resourcelanguage>...&lt;/resourcelanguage> tag
 * 
 * 
 * @author albrecht
 * 
 */

public class ResourceLanguageTag extends StandardDeferrable implements AnyTag,
GlobalModifier {

	/**
	 * store current token
	 */
	private String token;

	/**
	 * use body tag to set id variable
	 */
	private BodyTag body;

	public ResourceLanguageTag(BodyTag body) {
		this.body = body;
		this.token = "";
	}

	@Override
	public void open(Writer writer) throws IOException {
	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getPlainContent();

		} else {
			throw new OperationNotSupportedException("<resourcelanguage> cannot enclose "
					+ innerTag.getClass().toString());
		}

	}

	@Override
	public void DoAction() {
		this.body.setLanguage(this.token);

	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<resourcelanguage>");
		representation.append(StringEscape.escapeToXml(this.token));
		representation.append("</resourcelanguage>");

		return representation.toString();
	}


}
