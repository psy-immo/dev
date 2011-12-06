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

	private ArrayList<AnyHtmlTag> head;
	private ArrayList<AnyHtmlTag> body;

	/**
	 * class that will store tag information
	 * 
	 * @author immanuel
	 * 
	 */

	private class EfmlTag {
		private String name;
		private Attributes attribs;
		private EfmlTag parent;

		private Set<String> tags;
		private StringBuffer characters;

		public EfmlTag(String qName, Attributes attribs, EfmlTag parent) {
			this.name = qName;
			this.attribs = attribs;
			this.parent = parent;

			if (parent != null) {
				this.tags = new HashSet<String>(parent.tags);
			} else {
				this.tags = new HashSet<String>();
			}
			this.characters = new StringBuffer();

			if (null != attribs) {
				String tagsValue = this.attribs.getValue("tags");
				if (null != tagsValue) {
					String[] tag_array = tagsValue.split(",");
					for (int i = 0; i < tag_array.length; ++i) {
						this.tags.add(tag_array[i].trim());
					}
				}
			}
		}

		public String getCharacters() {
			return characters.toString();
		}

		public void addCharacters(String s) {
			characters.append(s);
		}
		
		/**
		 * 
		 * @return the current tag set, as javascript-array
		 */
		
		public String getTags() {
			String array = "[";
			for (Iterator<String> it=tags.iterator(); it.hasNext();) {
				array += "\""+StringEscape.escapeToJavaScript(it.next())+"\"";
				if (it.hasNext()) array += ",";
			}
			return array + "]";
		}

		public String getName() {
			return name;
		}

		public Attributes getAttribs() {
			return attribs;
		}

		public EfmlTag getParent() {
			return parent;
		}

	};

	EfmlTag root;

	/**
	 * keep track of currently opened tags
	 */

	private Stack<EfmlTag> openTags;

	public EfmlToHtmlHandler() {
		head = new ArrayList<AnyHtmlTag>();
		body = new ArrayList<AnyHtmlTag>();
		openTags = new Stack<EfmlTag>();
		root = new EfmlTag("", null, null);

		openTags.push(root);
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
		System.out.println("Local name :" + localName);
		System.out.println("Attributes :" + attributes.getLength());

		openTags.push(new EfmlTag(qName, attributes, openTags.peek()));
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		System.out.println("tags: " + openTags.peek().getTags());
		openTags.pop();
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		System.out.println("Chars:" + new String(ch, start, length));
		openTags.peek().addCharacters(new String(ch, start, length));

	}

}
