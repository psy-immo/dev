/**
 * HeadTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

/**
 * provides the head tag
 * 
 * @author immanuel
 * 
 */

public class HeadTag implements AnyTag {

	private ArrayList<AnyTag> innerTags;

	/**
	 * css url
	 */

	private String cssLink;

	/**
	 * css url default base
	 */

	private String cssDefaultBase;

	public HeadTag() {
		this.innerTags = new ArrayList<AnyTag>();
		this.cssLink = null;
		this.cssDefaultBase = "";
	}

	/**
	 * 
	 * @param newUrl
	 *            new url of the used style sheet file
	 */

	public void setCssUrl(String newUrl) {
		this.cssLink = newUrl;
	}

	/**
	 * 
	 * @param newBase
	 *            new base if css url is not set explicitly
	 */

	public void setCssUrlDefaultBase(String newBase) {
		this.cssDefaultBase = newBase;
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<head>");
		/**
		 * write UTF-8 meta data information
		 */

		writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");

		/**
		 * write CSS style sheet link
		 */

		if (this.cssLink != null) {

			if (this.cssLink.toLowerCase().endsWith(".css")) {
				writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
						+ this.cssLink.substring(0, this.cssLink.length() - 4)
						+ "-print"
						+ this.cssLink.substring(this.cssLink.length() - 4)
						+ "\" media=\"print\" />");

				writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
						+ this.cssLink + "\" media=\"screen\" />");

			} else {
				writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
						+ this.cssLink + "\" media=\"print\" />");
				writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
						+ this.cssLink + "\" media=\"screen\" />");

			}

		} else {

			writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
					+ this.cssDefaultBase
					+ "efjs-print.css\" media=\"print\" />");

			writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""
					+ this.cssDefaultBase + "efjs.css\" media=\"screen\" />");

		}

		/**
		 * write inner tags
		 */

		for (AnyTag innerTag : this.innerTags) {
			innerTag.open(writer);
			innerTag.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</head>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		this.innerTags.add(innerTag);
	}

	@Override
	public String getEfml() {
		/**
		 * the head tag has no efml representation
		 */
		return null;
	}

}
