/**
 * HintTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * implements the &lt;hint>...&lt;/hint> tag for answers
 * and &lt;feedback>
 * @author immanuel
 *
 */

public class HintTag implements AnyTag {
	
	private String token;
	
	private EfmlTagsAttribute attributes;
	
	public HintTag(EfmlTagsAttribute attributes) {
		this.token = "";
		this.attributes = attributes;
	}
	
	/**
	 * 
	 * @return value of lack attribute that this hint is meant for
	 */
	
	public String getLack() {
		return this.attributes.getValueOrDefault("lack", "");
	}
	
	/**
	 * 
	 * @return the hints html content
	 */
	
	public String getHint() {
		return this.token;
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
			this.token += ((PlainContent) innerTag).getContent();
		} else if (innerTag.getClass() == UnknownTag.class) {
			this.token += ((UnknownTag) innerTag).getContents();
		} else
			throw new OperationNotSupportedException("<hint> cannot enclose "
					+ innerTag.getClass().toString());
	}
	
	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<hint");
		attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.token));
		representation.append("</hint>");

		return representation.toString();
	}

}
