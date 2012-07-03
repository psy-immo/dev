/**
 * PopupHelpTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;popuphelp>-tag for landing several strings in a row
 * 
 * @author albrecht
 * 
 */

public class PopupHelpTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String url;

	public PopupHelpTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.url = "";
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		writer.write("new PopupButton(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", "")) + "\", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(this.url.trim())
				+ "\") ");
		
		attributes.writeIfValueGiven(writer, ".Text(\"", "text", "\")");
		attributes.writeIfValueGiven(writer, ".Style(\"", "style", "\")");

		writer.write(".WriteHtml();");

	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.url += ((PlainContent) innerTag).getPlainContent();
		} else
			throw new OperationNotSupportedException(
					"<popuphelp> cannot enclose "
							+ innerTag.getClass().toString());

	}

}
