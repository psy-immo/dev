/**
 * EfmlToHtmlHandler.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class takes efml (xml) input from sax and generates intermediate
 * structures that can be rendered to html.
 * 
 * @author immanuel
 * 
 */

public class EfmlToHtmlHandler extends DefaultHandler {

	/**
	 * This stores the tags of the head and body part of the html file
	 */
	public ArrayList<AnyHtmlTag> head;
	public ArrayList<AnyHtmlTag> body;
	public EfmlTag root;
	/**
	 * keep track of currently opened tags
	 */
	public Stack<EfmlTag> openTags;

	public EfmlToHtmlHandler() {
		this.head = new ArrayList<AnyHtmlTag>();
		this.body = new ArrayList<AnyHtmlTag>();
		this.openTags = new Stack<EfmlTag>();
		this.root = new EfmlTag("", null, null);

		this.openTags.push(this.root);
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the head tag of the
	 *         html file
	 */

	public Iterator<AnyHtmlTag> headIterator() {
		return this.head.iterator();
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the body tag of the
	 *         html file
	 */
	public Iterator<AnyHtmlTag> bodyIterator() {
		return this.body.iterator();
	}

	/**
	 * element starts, push it on the stack
	 */

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.openTags
				.push(new EfmlTag(qName, attributes, this.openTags.peek()));
	}

	/**
	 * element ends, add tags to html output
	 */

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		EfmlTag xmlTag = this.openTags.pop();

		if (xmlTag.getName() == "title") {
			this.head.add(new TitleTag(xmlTag.getCharacters()));
		} else {
			/**
			 * this tag is not recognized and thus will be treated as unknown
			 * tag
			 */

			this.body.add(new UnknownTag(xmlTag));
		}

	}

	/**
	 * receive data
	 */

	public void characters(char ch[], int start, int length)
			throws SAXException {
		this.openTags.peek().addCharacters(new String(ch, start, length));
	}

}
