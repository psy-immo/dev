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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;


/**
 * class that will store tag information when parsing EFML files
 * 
 * @author immanuel
 * 
 */

public class EfmlTagsAttribute {
	private String name;
	private Attributes attribs;
	private EfmlTagsAttribute parent;

	private Set<String> tags;
	private Set<String> accept;
	private Set<String> reject;

	public EfmlTagsAttribute(String qName, Attributes attribs, EfmlTagsAttribute parent) {
		this.name = qName;
		this.attribs = attribs;
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
			String tagsValue = this.attribs.getValue("tags");
			if (null != tagsValue) {
				String[] tag_array = tagsValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.tags.add(tag_array[i].trim());
				}
			}
			
			String acceptValue = this.attribs.getValue("atags");
			if (null != tagsValue) {
				String[] tag_array = tagsValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.accept.add(tag_array[i].trim());
				}
			}
			
			String rejectValue = this.attribs.getValue("rtags");
			if (null != tagsValue) {
				String[] tag_array = tagsValue.split(",");
				for (int i = 0; i < tag_array.length; ++i) {
					this.reject.add(tag_array[i].trim());
				}
			}
		}
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
	
	/**
	 * 
	 * @return the current accept tag set, as javascript-array
	 */
	
	public String getAcceptTags() {
		String array = "[";
		for (Iterator<String> it=accept.iterator(); it.hasNext();) {
			array += "\""+StringEscape.escapeToJavaScript(it.next())+"\"";
			if (it.hasNext()) array += ",";
		}
		return array + "]";
	}
	

	/**
	 * 
	 * @return the current reject tag set, as javascript-array
	 */
	
	public String getRejectTags() {
		String array = "[";
		for (Iterator<String> it=reject.iterator(); it.hasNext();) {
			array += "\""+StringEscape.escapeToJavaScript(it.next())+"\"";
			if (it.hasNext()) array += ",";
		}
		return array + "]";
	}

	public final String getName() {
		return name;
	}

	public final Attributes getAttribs() {
		return attribs;
	}

	public final EfmlTagsAttribute getParent() {
		return parent;
	}

};