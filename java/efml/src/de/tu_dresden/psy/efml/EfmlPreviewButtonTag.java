/**
 * EfmlCompilePreviewTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;efmlpreview>-tag for interactive efml previews
 * 
 * @author immanuel
 * 
 */

public class EfmlPreviewButtonTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String token;

	public EfmlPreviewButtonTag(EfmlTagsAttribute efmlAttributes, BodyTag body) {
		this.attributes = efmlAttributes;
		this.token = "";
		body.requireEfml();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new javascript Runway object with name, tags, token, accept,
		 * reject
		 */

		writer.write(" new EfmlPreviewButton(");


		writer.write(attributes.getAcceptTags() + ", ");
		writer.write(attributes.getRejectTags() + ", ");


		writer.write("\"" + StringEscape.escapeToJavaScript(token) + "\")");

		/**
		 * finally let javascript create the html code
		 */

		writer.write(".WriteHtml();");
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write(" </script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getContent();
		} else
			throw new OperationNotSupportedException(
					"<efmlpreview> cannot enclose "
					+ innerTag.getClass().toString());
	}

}
