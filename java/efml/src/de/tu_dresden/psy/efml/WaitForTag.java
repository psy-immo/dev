/**
 * WaitForTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * implements the &lt;waitfor>...&lt;/waitfor> tag for answers
 * 
 * @author immanuel
 * 
 */

public class WaitForTag implements AnyTag {

	private String token;

	public WaitForTag() {
		this.token = "";
	}

	public String getJavaScriptFunction() {
		String check = this.token.trim().toLowerCase();
		if (check.equals("sniffy")) {
			return "myWaitForSniffy";
		} else if (check.equals("instructions")) {
			return "myWaitForInstructions";
		}

		/**
		 * this is neither <waitfor>sniffy</waitfor> nor
		 * <waitfor>instructions</waitfor>, thus might be java script
		 */

		System.err.println("Warning: <waitfor>" + this.token
				+ "</waitfor> not recognized, java script content assumed.");

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
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getPlainContent();
		} else
			throw new OperationNotSupportedException(
					"<waitfor> cannot enclose "
							+ innerTag.getClass().toString());
	}
	
	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();
		
		representation.append("<waitfor>");
		representation.append(StringEscape.escapeToXml(this.token));
		representation.append("</waitfor>");
		
		return representation.toString();
	}

}
