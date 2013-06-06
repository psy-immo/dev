/**
 * PlainContent.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import de.tu_dresden.psy.inference.compiler.EmbeddedInferenceXmlTag;
/**
 * implements character data as child "tag"
 * @author immanuel
 *
 */

public class PlainContent implements AnyTag, EmbeddedInferenceXmlTag {

	private String content;
	private String unescaped;

	public PlainContent(String unescapedContents) {
		this.content = StringEscape.escapeToHtml(unescapedContents);
		this.unescaped = unescapedContents;
	}

	public final String getContent() {
		return this.content;
	}

	public final String getPlainContent() {
		return this.unescaped;
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write(this.content);
	}

	@Override
	public void close(Writer writer) throws IOException {


	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		throw new OperationNotSupportedException("PlainContent ain't even a tag");
	}

	@Override
	public String getEfml() {

		return StringEscape.escapeToXml(this.unescaped);
	}

	@Override
	public String getTagClass() {

		return "#PCDATA";
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public ArrayList<EmbeddedInferenceXmlTag> getChildren() {
		return null;
	}


}
