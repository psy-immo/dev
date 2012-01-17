/**
 * RTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

import javax.naming.OperationNotSupportedException;

/**
 * implementation of the &lt;c/> tag (switches column within &lt;tables> tag)
 * @author immanuel
 *
 */

public class CTag implements AnyTag {
	
	private TablesTag parent;
	
	public CTag(AnyTag parent) {
		if (parent.getClass() != TablesTag.class)
			throw new RuntimeException("<c/>-tag must be direct child of <tables>-tag!");
		this.parent = (TablesTag) parent;
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("</td></tr></table>");
		
		parent.writeTable(writer);
	}

	@Override
	public void close(Writer writer) throws IOException {

	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		throw new OperationNotSupportedException("<c /> takes no contents.");
	}

}
