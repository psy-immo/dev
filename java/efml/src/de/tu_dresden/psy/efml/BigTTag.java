/**
 * BigTTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

/**
 * implements the <T> tag, which puts a left-floating table around its contents
 * @author immanuel
 *
 */

public class BigTTag implements AnyTag {
	
	private ArrayList<AnyTag> innerTags;
	
	
	public BigTTag() {
	
		innerTags = new ArrayList<AnyTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		
		writer.write("<table style=\"float:left; white-space:nowrap;\" cellpadding=0 cellspacing=0 border=0><tr><td>");
		
		/**
		 * write inner tags
		 */
		
		for (Iterator<AnyTag> it=innerTags.iterator();it.hasNext();)
		{
			AnyTag innerTag = it.next();
			innerTag.open(writer);
			innerTag.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("&nbsp;</td></tr></table>");

	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		innerTags.add(innerTag);
	}

}
