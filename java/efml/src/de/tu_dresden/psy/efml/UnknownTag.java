/**
 * UnknownTag.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import org.xml.sax.Attributes;

/**
 * this class represents an unknown tag which will just be reproduced as it was in the efml in the output html body
 * @author immanuel
 *
 */
public class UnknownTag implements AnyHtmlTag {
	
	private String opening;
	private String contents;
	private String closing;

	public UnknownTag(EfmlTag reproduce) {
		
		opening = "<"+reproduce.getName();
		
		final Attributes attribs = reproduce.getAttribs();
		for (int i=0;i<attribs.getLength(); ++i) {
			opening += " " + attribs.getQName(i) + "=\"" + StringEscape.escapeToHtml(attribs.getValue(i)) + "\"";
		}
		opening += ">";
		
		contents = StringEscape.escapeToHtml(reproduce.getCharacters());
		
		closing = "</"+reproduce.getName()+">";
		
	}
	
	@Override
	public void open(Writer writer) throws IOException {
		writer.write(opening);
		writer.write(contents);
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write(closing);
	}

}
