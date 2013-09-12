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

import de.tu_dresden.psy.efml.res.EfjsIntegrationResources;
import de.tu_dresden.psy.efml.res.EfjsIntegrationResources.TagObjectConstructor;

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
		this.body = new BodyTag(this.head);
		this.processingTags = new Stack<AnyTag>();

		this.currentTags.push(this.root);
		this.processingTags.push(this.body);
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the head tag of the
	 *         html file
	 */

	public HeadTag getHead() {
		return this.head;
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the body tag of the
	 *         html file
	 */
	public BodyTag getBody() {
		return this.body;
	}

	/**
	 * element starts, push it on the stack
	 */

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.currentTags.push(new EfmlTagsAttribute(qName, attributes,
				this.currentTags.peek()));

		boolean found = false;

		for (int idx = 0; idx < EfjsIntegrationResources.tagClasses.length; idx += 2) {
			String name = (String) EfjsIntegrationResources.tagClasses[idx];
			EfjsIntegrationResources.TagObjectConstructor cons = (TagObjectConstructor) EfjsIntegrationResources.tagClasses[idx + 1];

			if (qName.equals(name)) {

				this.processingTags.push(cons.New(this.currentTags.peek(),
						this.processingTags.peek(), this.body));
				found = true;
				break;
			}

		}

		if (found == false) {
			/**
			 * the tag is not recognized and thus we use the unknown tag handler
			 */

			this.processingTags.push(new UnknownTag(this.currentTags.peek()));

		}
	}

	/**
	 * element ends, add tags to html output
	 */

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.currentTags.pop();
		AnyTag closing_tag = this.processingTags.pop();

		try {
			if (closing_tag instanceof UnknownHeadTag) {
				this.head.encloseTag(closing_tag);
			} else {
				this.processingTags.peek().encloseTag(closing_tag);
			}

			if (closing_tag instanceof GlobalModifier) {
				GlobalModifier modifier = (GlobalModifier) closing_tag;
				if (modifier.isDeferred() == false) {
					modifier.DoAction();
				}
			}
		} catch (OperationNotSupportedException e) {

			e.printStackTrace();
		}
	}

	/**
	 * receive data
	 */

	@Override
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
