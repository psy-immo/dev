/**
 * EfmlQuoteTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;efmlquote>-tag
 * 
 * @author immo
 * 
 */

public class EfmlQuoteTag implements EfmlBoardComponentTag {

	private EfmlTagsAttribute attributes;

	private ArrayList<AnyTag> contents;

	public EfmlQuoteTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;

		this.contents = new ArrayList<AnyTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		/**
		 * should never be called since this would be incorrect efml
		 */
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * should never be called since this would be incorrect efml
		 */
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		this.contents.add(innerTag);
		if (innerTag instanceof GlobalModifier) {
			GlobalModifier modifier = (GlobalModifier) innerTag;
			modifier.RequestDeferring();
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<efmlquote");
		attributes.writeXmlAttributes(representation);
		representation.append(">");
		for (AnyTag a : this.contents) {
			representation.append(a.getEfml());
		}

		representation.append("</efmlquote>");

		return representation.toString();
	}

	@Override
	public String getEfjsEfmlNewRepresentation() {
		StringBuffer representation = new StringBuffer();

		representation.append("EfmlQuote ");
		for (AnyTag a : this.contents) {
			representation.append(StringEscape.escapeToJavaScript(a.getEfml()));
		}

		return representation.toString();
	}

}
