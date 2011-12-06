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
import java.util.Iterator;

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

	private ArrayList<AnyHtmlTag> head;
	private ArrayList<AnyHtmlTag> body;

	public EfmlToHtmlHandler() {
		head = new ArrayList<AnyHtmlTag>();
		body = new ArrayList<AnyHtmlTag>();
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the head tag of the
	 *         html file
	 */

	public Iterator<AnyHtmlTag> headIterator() {
		return head.iterator();
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the body tag of the
	 *         html file
	 */
	public Iterator<AnyHtmlTag> bodyIterator() {
		return body.iterator();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		System.out.println("Start Element :" + qName);
		System.out.println("Attrubibutes :" + attributes.getLength());

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		System.out.println("End Element :" + qName);

	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		System.out.println(new String(ch, start, length));

	}

}
