/**
 * CorrectTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * implements the &lt;correct>...&lt;/correct> tag for answers
 * @author immanuel
 *
 */

public class CorrectTag implements AnyTag {
	
	private String token;
	
	public CorrectTag() {
		this.token = "";
	}
	
	public String getFeedback() {
		return this.token;
	}

	@Override
	public void open(Writer writer) throws IOException {
		/**
		 * noop
		 */
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * noop
		 */
	}

	@Override
	public String getEfml() {
		/**
		 * handled by enclosing tag
		 */
		return null;
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getContent();
		}  else if (innerTag.getClass() == UnknownTag.class) {
			this.token += ((UnknownTag) innerTag).getContents();
		} else
			throw new OperationNotSupportedException("<correct> cannot enclose "
					+ innerTag.getClass().toString());
	}

}
