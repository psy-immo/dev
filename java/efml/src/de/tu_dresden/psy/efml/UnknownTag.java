/**
 * UnknownTag.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

/**
 * this class represents an unknown tag which will just be reproduced as it was
 * in the efml in the output html body
 * 
 * @author immanuel
 * 
 */
public class UnknownTag implements AnyTag {

	private String opening;
	private String standalone;
	private String closing;

	private ArrayList<AnyTag> innerTags;

	public UnknownTag(EfmlTagsAttribute reproduce) {

		opening = "<" + reproduce.getName();

		Map<String,String> attribs = reproduce.getAttribs();
		for (Iterator<String> it_attribs = attribs.keySet().iterator(); it_attribs.hasNext();) {
			String attributeName = it_attribs.next();
			/**
			 * the tags attribute belongs to the EFML data structure layer and
			 * is not going to show in the html
			 */
			if ((attributeName != "tags")&&(attributeName != "atags")&&(attributeName != "rtags")) {
				opening += " " + attributeName + "=\""
						+ StringEscape.escapeToHtml(attribs.get(attributeName)) + "\"";
			}
		}

		standalone = opening + " />";

		opening += ">";

		innerTags = new ArrayList<AnyTag>();

		closing = "</" + reproduce.getName() + ">";

	}

	@Override
	public void open(Writer writer) throws IOException {

		if (innerTags.isEmpty()) {
			/**
			 * no inner contents, use <... /> variant
			 */
			writer.write(standalone);
			return;
		}

		/**
		 * with inner contents
		 */

		writer.write(opening);

		/**
		 * write inner tags
		 */

		for (Iterator<AnyTag> it = innerTags.iterator(); it.hasNext();) {
			AnyTag innerTag = it.next();
			innerTag.open(writer);
			innerTag.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * no need to close standalone tag
		 */
		if (!innerTags.isEmpty()) {
			writer.write(closing);
		}
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		innerTags.add(innerTag);
	}

}
