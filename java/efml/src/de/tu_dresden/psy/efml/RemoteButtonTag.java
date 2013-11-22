/**
 * RemoteButtonTag.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
 * handles &lt;remotebutton>...&lt;/remotebutton> tag, that can be used to let
 * several inference machines work at the same time
 * 
 * @author immanuel
 * 
 */

public class RemoteButtonTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String buttonText;
	private ArrayList<CallTag> calls;

	public RemoteButtonTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		this.buttonText = "";
		this.calls = new ArrayList<CallTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {

		/**
		 * trim the button label
		 */
		this.buttonText = this.buttonText.trim();

		/**
		 * write script code
		 */

		writer.write("<script type=\"text/javascript\">");

		writer.write("new RemoteButton(");
		writer.write(")");

		/**
		 * write button text
		 */
		if (this.buttonText.length() > 0) {
			writer.write(".Text(\""
					+ StringEscape.escapeToJavaScript(this.buttonText) + "\")");
		}

		for (CallTag c : this.calls) {
			writer.write(".AddSlaveCall(");
			writer.write(c.getJsCode());
			writer.write(")");
		}


		/**
		 * let java script create the html contents
		 */
		writer.write(".WriteHtml();");

		writer.write("</script>");
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
			this.buttonText += ((PlainContent) innerTag).getPlainContent();
		} else if (innerTag.getClass() == CallTag.class) {
			this.calls.add((CallTag) innerTag);
		}
		else {
			throw new OperationNotSupportedException("<remotebutton> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<remotebutton");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.buttonText));

		for (AnyTag child : this.calls) {
			representation.append(child.getEfml());
		}
		representation.append("</answer>");

		return representation.toString();
	}

}
