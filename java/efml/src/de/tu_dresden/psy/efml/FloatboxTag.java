/**
 * FloatboxTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements &lt;floatbox> tag for boxes in box spaces
 * 
 * @author immanuel
 * 
 */

public class FloatboxTag implements AnyTag, NestedTag {

	private EfmlTagsAttribute attributes;

	private String content;
	private String unescaped;

	public FloatboxTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.content = "";
		this.unescaped = "";
	}

	public final String getContent() {
		return content;
	}

	public final String getPlainContent() {
		return unescaped;
	}

	@Override
	public void open(Writer writer) throws IOException {

		createNew(writer, "");

	}

	@Override
	public void close(Writer writer) throws IOException {

	}

	@Override
	public void createNew(Writer writer, String identificationToken)
			throws IOException {

		writer.write(" new FloatBox(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"style", "")) + "\", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(this.content)
				+ "\")");

	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.content += ((PlainContent) innerTag).getContent();
		} else if (innerTag.getClass() == UnknownTag.class) {
			this.content += ((UnknownTag) innerTag).getContents();
		} else
			throw new OperationNotSupportedException(
					"<floatbox> cannot enclose tags");
	}

}
