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

public class EfmlTag {
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

	public final String getName() {
		return name;
	}

	public final Attributes getAttribs() {
		return attribs;
	}

	public final EfmlTag getParent() {
		return parent;
	}

};