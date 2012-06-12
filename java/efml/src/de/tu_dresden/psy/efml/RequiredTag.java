/**
 * RequiredTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;required> subtag for &lt;feedback>
 * @author immanuel
 *
 */

public class RequiredTag implements AnyTag {
	
	private EfmlTagsAttribute attributes;
	
	public RequiredTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;	
	}
	
	/**
	 * 
	 * @return true, if this tag requires a certain number of correct parts
	 */
	
	boolean requiresCount() {
		return this.attributes.hasAttribute("count");
	}
	
	/**
	 * 
	 * @return required count of different correct solution parts
	 */
	
	String getCount() {
		return this.attributes.getValueOrDefault("count", "0");
	}
	

	/**
	 * 
	 * @return true, if this tag requires a correct part identified by a name
	 */
	
	boolean requiresPart() {
		return this.attributes.hasAttribute("name");
	}
	
	/**
	 * 
	 * @return required part's name
	 */
	
	String getPart() {
		return this.attributes.getValueOrDefault("name", "");
	}

	@Override
	public void open(Writer writer) throws IOException {
	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class)
			return;
		
		throw new OperationNotSupportedException("<required> cannot enclose "
				+ innerTag.getClass().toString());
	}

}
