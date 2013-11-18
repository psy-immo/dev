/**
 * AnswerTag.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import javax.naming.OperationNotSupportedException;

/**
 * handles &lt;answer>...&lt;/answer> tag, that can be used to create an answer
 * button
 * 
 * @author immanuel
 * 
 */

public class AnswerTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String buttonText;
	private ArrayList<HintTag> hints;
	private ArrayList<CheckTag> checks;
	private ArrayList<WaitForTag> waitfors;
	private String goodText;

	public AnswerTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		this.buttonText = "";
		this.goodText = null;
		this.hints = new ArrayList<HintTag>();
		this.checks = new ArrayList<CheckTag>();
		this.waitfors = new ArrayList<WaitForTag>();
	}

	/**
	 * 
	 * @return java script code, that will test the solution according to the
	 *         contents of the &lt;answer>-tag
	 */

	private String generateTestSolutionCode() {
		StringBuffer javascript = new StringBuffer();

		javascript.append("function() {\n");

		/**
		 * first, mark all parts neutral
		 */

		javascript.append("Exists(myTags.AllTagsBut("
				+ this.attributes.getAcceptTags() + ","
				+ this.attributes.getRejectTags() + "),");
		javascript.append("function(c) { \n");
		javascript
		.append("if ( typeof c.MarkNeutral != \"undefined\" ) c.MarkNeutral();\n");
		javascript.append("return false;\n});\n\n");

		javascript.append("var good_count = 0;\n");

		for (CheckTag check : this.checks) {
			javascript.append("if ( (" + check.getJavaScriptTestFunction()
					+ ")()) good_count ++;\n");
		}

		javascript.append("return good_count == " + this.checks.size() + ";");
		javascript.append("\n}");

		return javascript.toString();
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

		writer.write("new Answer(");
		/**
		 * write solution test code
		 */

		writer.write(this.generateTestSolutionCode());
		writer.write(")");

		/**
		 * write button text
		 */
		if (this.buttonText.length() > 0) {
			writer.write(".Text(\""
					+ StringEscape.escapeToJavaScript(this.buttonText) + "\")");
		}

		/**
		 * write feedback texts
		 */

		if ((this.goodText != null) || (this.hints.size() > 0)) {
			writer.write(".Feedback(");
			if (this.goodText != null) {
				writer.write(StringEscape
						.escapeToDecodeInJavaScript(this.goodText));
			} else {
				writer.write("\"\"");
			}

			writer.write(",");

			if (this.hints.size() > 0) {
				writer.write("["
						+ StringEscape.escapeToDecodeInJavaScript(this.hints
								.get(0).getHint()));
				for (int i = 1; i < this.hints.size(); ++i) {
					writer.write(","
							+ StringEscape
							.escapeToDecodeInJavaScript(this.hints.get(
									i).getHint()));
				}
				writer.write("]");

			} else {
				writer.write("null");
			}

			writer.write(")");
		}

		/**
		 * add waitfor-checks
		 */

		Iterator<WaitForTag> it_waitfor;
		for (it_waitfor = this.waitfors.iterator(); it_waitfor.hasNext();) {
			WaitForTag waitfor = it_waitfor.next();

			writer.write(".WaitFor(" + waitfor.getJavaScriptFunction() + ")");
		}

		/**
		 * show/hide autospans
		 */

		String show_auto = this.attributes.getValueOrDefault("showautospan",
				null);

		if (show_auto != null) {
			writer.append(".ShowAfterRectification(\""
					+ StringEscape.escapeToJavaScript(show_auto) + "\")");
		}

		String hide_auto = this.attributes.getValueOrDefault("hideautospan",
				null);

		if (hide_auto != null) {
			writer.append(".HideAfterRectification(\""
					+ StringEscape.escapeToJavaScript(hide_auto) + "\")");
		}

		/**
		 * try counter object
		 */

		String counter = this.attributes.getValueOrDefault("counter", null);

		if (counter != null) {
			writer.append(".Counter(\""
					+ StringEscape.escapeToJavaScript(counter) + "\")");
		}

		/**
		 * feedback display name
		 */
		String feedback_target = this.attributes.getValueOrDefault("feedback",
				null);

		if (feedback_target != null) {
			writer.append(".Feedback(\""
					+ StringEscape.escapeToJavaScript(feedback_target) + "\")");
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
		} else if (innerTag.getClass() == HintTag.class) {
			this.hints.add((HintTag) innerTag);
		} else if (innerTag.getClass() == CheckTag.class) {
			this.checks.add((CheckTag) innerTag);
		} else if (innerTag.getClass() == WaitForTag.class) {
			this.waitfors.add((WaitForTag) innerTag);
		} else if (innerTag.getClass() == CorrectTag.class) {
			this.goodText = ((CorrectTag) innerTag).getFeedback();
		} else {
			throw new OperationNotSupportedException("<answer> cannot enclose "
					+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<answer");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		representation.append(StringEscape.escapeToXml(this.buttonText));

		for (AnyTag child : this.waitfors) {
			representation.append(child.getEfml());
		}
		for (AnyTag child : this.checks) {
			representation.append(child.getEfml());
		}
		for (AnyTag child : this.hints) {
			representation.append(child.getEfml());
		}
		if (this.goodText != null) {
			representation.append("<correct>" + this.goodText + "</correct>");
		}

		representation.append("</answer>");

		return representation.toString();
	}

}
