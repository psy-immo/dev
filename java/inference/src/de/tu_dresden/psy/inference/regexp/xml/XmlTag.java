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
			attributes.put(atts.getLocalName(i).toLowerCase(), atts.getValue(i));
		}

		contents = "";

		this.children = new Vector<XmlTag>();
	}

	/**
	 * return attribute or default
	 */

	protected String getAttributeOrDefault(String attributeName,
			String defaultValue) {
		String value = attributes.get(attributeName);
		if (value == null)
			return defaultValue;

		return value;
	}

	/**
	 * protected constructor used by the implementation of the root element
	 */
	protected XmlTag() {
		this.tagName = "";
		this.attributes = new HashMap<String, String>();

		contents = "";
		this.children = new Vector<XmlTag>();
	}

	/**
	 * concatenate new character data to current contents
	 * 
	 * @param data
	 */
	public void addContent(String data) {
		contents += data;
	}

	/**
	 * add a child tag to this tag
	 * 
	 * @param child
	 */
	public void addChild(XmlTag child) {
		children.add(child);
	}

}
