/**
 * EfmlTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

package de.tu_dresden.psy.efml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;

/**
 * class that will store tag information when parsing EFML files
 * 
 * @author immanuel
 * 
 */

public class EfmlTagsAttribute {
	private String name;
	private Map<String, String> attribs;
	private EfmlTagsAttribute parent;

	private Set<String> tags;
	private Set<String> accept;
	private Set<String> reject;
	
	/**
	 * create EfmlTagsAttribute for new tag
	 * 
	 * @param qName    name of the tag
	 * @param attribs  xml parser attributes object
	 * @param parent   the EfmlTagsAttributes object of the parent node
	 */

	public EfmlTagsAttribute(String qName, Attributes attribs,
			EfmlTagsAttribute parent) {
		this.name = qName;
		this.attribs = new HashMap<String, String>();
		this.parent = parent;

		if (parent != null) {
			this.tags = new HashSet<String>(parent.tags);
			this.accept = new HashSet<String>(parent.accept);
			this.reject = new HashSet<String>(parent.reject);
		} else {
			this.tags = new HashSet<String>();
			this.accept = new HashSet<String>();
			this.reject = new HashSet<String>();
		}

		if (null != attribs) {
			String tagsValue = attribs.getValue("tags");
			if (null != tagsValue) {
				String[] tag_array = tagsValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.tags.add(tag_array[i].trim());
				}
			}

			String acceptValue = attribs.getValue("atags");
			if (null != acceptValue) {
				String[] tag_array = acceptValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.accept.add(tag_array[i].trim());
				}
			}

			String rejectValue = attribs.getValue("rtags");
			if (null != rejectValue) {
				String[] tag_array = rejectValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.reject.add(tag_array[i].trim());
				}
			}

			/**
			 * 
			 * keep the attributes of the tag, storing the Attributes object
			 * doesn't work for later
			 */

			for (int i = 0; i < attribs.getLength(); ++i) {
				this.attribs.put(attribs.getQName(i), attribs.getValue(i));
			}
		}
	}

	/**
	 * 
	 * @return the current tag set, as javascript-array
	 */

	public String getTags() {
		String array = "[";
		for (Iterator<String> it = tags.iterator(); it.hasNext();) {
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
			if (it.hasNext())
				array += ",";
		}
		return array + "]";
	}

	/**
	 * 
	 * @return the current tag set, as javascript-array-part (without the []),
	 *        no trailing ','
	 */

	public String getTagsCommas() {
		String array = "";
		for (Iterator<String> it = tags.iterator(); it.hasNext();) {
			if (array.isEmpty()==false)
				array +=", ";
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
		
		}
		return array;
	}

	/**
	 * 
	 * @return the current accept tag set, as javascript-array
	 */

	public String getAcceptTags() {
		String array = "[";
		for (Iterator<String> it = accept.iterator(); it.hasNext();) {
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
			if (it.hasNext())
				array += ",";
		}
		return array + "]";
	}

	/**
	 * 
	 * @return the current accept tag set, as javascript-array-part (without the
	 *         []), no trailing ','
	 */

	public String getAcceptTagsCommas() {
		String array = "";
		for (Iterator<String> it = accept.iterator(); it.hasNext();) {
			if (array.isEmpty()==false)
				array += ",";
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
			
		}
		return array;
	}

	/**
	 * 
	 * @return the current reject tag set, as javascript-array
	 */

	public String getRejectTags() {
		String array = "[";
		for (Iterator<String> it = reject.iterator(); it.hasNext();) {
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
			if (it.hasNext())
				array += ",";
		}
		return array + "]";
	}

	/**
	 * 
	 * @return the current reject tag set, as javascript-array-part (without the
	 *         []), no trailing ','
	 */

	public String getRejectTagsCommas() {
		String array = "";
		for (Iterator<String> it = reject.iterator(); it.hasNext();) {
			if (array.isEmpty()==false)
				array += ",";
			array += "\"" + StringEscape.escapeToJavaScript(it.next()) + "\"";
			
		}
		return array;
	}
	
	/**
	 * 
	 * @return name of tag corresponding to this attributes
	 */

	public final String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return the objects attributes map
	 */

	public final Map<String, String> getAttribs() {
		return attribs;
	}

	/**
	 * checks the xml attributes
	 * 
	 * @param attributeName
	 *            attribute to be checked
	 * @param defaultValue
	 *            value returned if attribute is not defined
	 * @return value of the attribute or default
	 */

	public String getValueOrDefault(String attributeName, String defaultValue) {
		if (attribs.containsKey(attributeName))
			return attribs.get(attributeName);

		return defaultValue;
	}
	
	/**
	 * 
	 * @return attributes object of the parent node
	 */

	public final EfmlTagsAttribute getParent() {
		return parent;
	}

};