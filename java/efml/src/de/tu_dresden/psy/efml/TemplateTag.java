/**
 * TemplateTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;template>-tag for sentence pattern take-offs
 * 
 * @author albrecht
 * 
 */

public class TemplateTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String label;

	private ArrayList<NestedTag> parts;

	public TemplateTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.label = "";
		this.parts = new ArrayList<NestedTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create java script object
		 */

		writer.write("new SentencePattern(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", "")) + "\", ");

		writer.write(attributes.getTags() + ", ");

		/**
		 * write template array of nested objects
		 */

		boolean first = true;

		writer.write("[");

		for (NestedTag part : parts) {
			if (first == false) {
				writer.write(", ");
			}

			part.createNew(writer);

			first = false;
		}
		writer.write("]");

		if (attributes.getValueOrDefault("nonempty", "disallow")
				.equalsIgnoreCase(
				"disallow")) {
			writer.write(", true");
		}
		writer.write(")");

		writer.write(".WriteHtml();");


	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.label += ((PlainContent) innerTag).getPlainContent();
		} else if (innerTag instanceof NestedTag) {
			this.parts.add((NestedTag) innerTag);
		} else
			throw new OperationNotSupportedException(
					"<template> cannot enclose "
							+ innerTag.getClass().toString());

	}

}
