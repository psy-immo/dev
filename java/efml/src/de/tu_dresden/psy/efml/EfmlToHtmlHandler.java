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

import java.util.Stack;

import javax.naming.OperationNotSupportedException;

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

	private EfmlTagsAttribute root;

	/**
	 * stores the head
	 */

	private HeadTag head;

	/**
	 * stores the body
	 */

	private BodyTag body;

	/**
	 * keep track of currently opened tags
	 */
	private Stack<EfmlTagsAttribute> currentTags;

	private Stack<AnyTag> processingTags;

	public EfmlToHtmlHandler() {
		this.currentTags = new Stack<EfmlTagsAttribute>();
		this.root = new EfmlTagsAttribute("", null, null);
		this.head = new HeadTag();
		this.body = new BodyTag();
		this.processingTags = new Stack<AnyTag>();

		this.currentTags.push(this.root);
		this.processingTags.push(body);
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the head tag of the
	 *         html file
	 */

	public HeadTag getHead() {
		return head;
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the body tag of the
	 *         html file
	 */
	public BodyTag getBody() {
		return body;
	}

	/**
	 * element starts, push it on the stack
	 */

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.currentTags.push(new EfmlTagsAttribute(qName, attributes,
				this.currentTags.peek()));

		if (qName.equals("title")) {
			this.processingTags.push(new TitleTag());
		} else if ((qName.equals("tags")) || (qName.equals("efml"))) {
			this.processingTags.push(new TagsTag());
		} else if (qName.equals("tie")) {
			this.processingTags.push(new TieTag());
		} else if (qName.equals("tables")) {
			this.processingTags.push(new TablesTag(this.currentTags.peek()));
		} else if (qName.equals("r")) {
			this.processingTags.push(new RTag());
		} else if (qName.equals("c")) {
			this.processingTags.push(new CTag(this.processingTags.peek()));
		} else if (qName.equals("runway")) {
			this.processingTags.push(new RunwayTag(this.currentTags.peek()));
		} else if (qName.equals("answer")) {
			this.processingTags.push(new AnswerTag(this.currentTags.peek()));
		} else if (qName.equals("hint")) {
			this.processingTags.push(new HintTag());
		} else if (qName.equals("correct")) {
			this.processingTags.push(new CorrectTag());
		} else if (qName.equals("check")) {
			this.processingTags.push(new CheckTag(this.currentTags.peek()));
		} else if (qName.equals("includepreamble")) {
			this.processingTags.push(new IncludePreambleTag());
		} else if (qName.equals("includehover")) {
			this.processingTags.push(new IncludeHoverTag());
		} else {
			/**
			 * the tag is not recognized and thus we use the unknown tag handler
			 */

			this.processingTags.push(new UnknownTag(this.currentTags.peek()));
		}
	}

	/**
	 * element ends, add tags to html output
	 */

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.currentTags.pop();
		AnyTag closing_tag = this.processingTags.pop();

		try {
			this.processingTags.peek().encloseTag(closing_tag);
		} catch (OperationNotSupportedException e) {

			e.printStackTrace();
		}
	}

	/**
	 * receive data
	 */

	public void characters(char ch[], int start, int length)
			throws SAXException {
		try {
			this.processingTags.peek().encloseTag(
					new PlainContent(new String(ch, start, length)));
		} catch (OperationNotSupportedException e) {

			e.printStackTrace();
		}
	}

}
