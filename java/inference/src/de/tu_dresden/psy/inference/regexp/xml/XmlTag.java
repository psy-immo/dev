/**
 * XmlTag.java, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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
package de.tu_dresden.psy.inference.regexp.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;

import de.tu_dresden.psy.inference.compiler.EmbeddedInferenceXmlTag;

/**
 * implements a general XML tag
 * 
 * @author albrecht
 * 
 */

public class XmlTag {
	protected String tagName;
	protected String contents;
	protected Map<String, String> attributes;
	protected Vector<XmlTag> children;

	/**
	 * create a XmlTag object from the tags name and attributes
	 * 
	 * @param tagName
	 * @param atts
	 */
	public XmlTag(String tagName, Attributes atts) {
		this.tagName = tagName.toUpperCase();
		this.attributes = new HashMap<String, String>();

		for (int i = 0; i < atts.getLength(); ++i) {
			this.attributes.put(atts.getLocalName(i).toLowerCase(), atts.getValue(i).toLowerCase());
		}

		this.contents = "";

		this.children = new Vector<XmlTag>();
	}

	/**
	 * 
	 * create a XmlTag object from a given tag that was embedded in some efml
	 * file
	 * 
	 * @param tag
	 *            EmbeddedInferenceXmlTag
	 * 
	 * 
	 */

	public XmlTag(EmbeddedInferenceXmlTag tag) {
		this.tagName = tag.getTagClass().toUpperCase();
		this.attributes = tag.getAttributes();

		this.contents = "";
		this.children = new Vector<XmlTag>();

		if (tag.hasChildren()) {
			for (EmbeddedInferenceXmlTag child : tag.getChildren()) {
				if (child.getTagClass().equalsIgnoreCase("#PCDATA")) {
					this.contents += child.getStringContent();
				} else {
					this.children.add(new XmlTag(child));
				}
			}
		}

	}

	/**
	 * return attribute or default
	 */

	protected String getAttributeOrDefault(String attributeName,
			String defaultValue) {
		String value = this.attributes.get(attributeName);
		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	/**
	 * protected constructor used by the implementation of the root element
	 */
	protected XmlTag() {
		this.tagName = "";
		this.attributes = new HashMap<String, String>();

		this.contents = "";
		this.children = new Vector<XmlTag>();
	}

	/**
	 * concatenate new character data to current contents
	 * 
	 * @param data
	 */
	public void addContent(String data) {
		this.contents += data;
	}

	/**
	 * add a child tag to this tag
	 * 
	 * @param child
	 */
	public void addChild(XmlTag child) throws Exception {
		this.children.add(child);
	}

}
