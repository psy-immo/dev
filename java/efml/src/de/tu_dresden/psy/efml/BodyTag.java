/**
 * BodyTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

/**
 * provides the body tag in the html file
 * 
 * @author immanuel
 * 
 */

public class BodyTag implements AnyHtmlTag {

	@Override
	public void Open(Writer writer) throws IOException {
		writer.write("<body id=\"body\">");

		/**
		 * all content will be displayed in the main "frame"
		 */

		writer.write("<div id=\"frame\" \n"
				+ "		onmousemove=\"if ( document.all && myHover.flight != 0 ) myHover.MovePlane();\"\n"
				+ "		onclick=\"myHover.OnFlight();\">");
	}

	@Override
	public void Close(Writer writer) throws IOException {
		writer.write("</div>");

		/**
		 * add code for the hovering feature
		 */

		writer.write("	<script type=\"text/javascript\">\n"
				+ "		myHover.WriteHtml();\n" + "	</script>");

		writer.write("</body>");
	}

}
