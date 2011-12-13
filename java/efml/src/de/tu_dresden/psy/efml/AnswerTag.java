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

import javax.naming.OperationNotSupportedException;

/**
 * handles <answer>...</answer> tag, that can be used to create an answer button
 * 
 * @author immanuel
 * 
 */

public class AnswerTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String buttonText;
	private ArrayList<HintTag> hints;
	private String goodText;

	public AnswerTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		this.buttonText = "";
		this.goodText = null;
		this.hints = new ArrayList<HintTag>();
	}

	/**
	 * 
	 * @return java script code, that will test the solution according to the
	 *         contents of the <answer>-tag
	 */

	private String generateTestSolutionCode() {
		// TODO: code it

		return "function() { return true; }";
	}

	@Override
	public void open(Writer writer) throws IOException {

		writer.write("<script type=\"text/javascript\">");

		writer.write("new Answer(");
		/**
		 * write solution test code
		 */

		writer.write(generateTestSolutionCode());
		writer.write(")");

		/**
		 * write button text
		 */
		if (this.buttonText.length() > 0) {
			writer.write(".Text(\""
					+ StringEscape.escapeToJavaScript(buttonText) + "\")");
		}
		
		/**
		 * write feedback texts
		 */
		
		if ((this.goodText != null) || (this.hints.size()>0)) {
			writer.write(".Feedback(");
			if (this.goodText != null)
				writer.write(StringEscape.escapeToDecodeInJavaScript(this.goodText));
			else writer.write("\"\"");
			
			writer.write(",");
			
			if (this.hints.size() > 0) {
				writer.write("["+StringEscape.escapeToDecodeInJavaScript(this.hints.get(0).getHint()));
				for (int i=1;i<this.hints.size();++i) {
					writer.write(","+StringEscape.escapeToDecodeInJavaScript(this.hints.get(i).getHint()));
				}
				writer.write("]");
				
			} else writer.write("null");
			
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
			this.buttonText += ((PlainContent) innerTag).getContent();
		} else if (innerTag.getClass() == HintTag.class) {
			this.hints.add((HintTag) innerTag);
		} else if (innerTag.getClass() == CorrectTag.class) {
			this.goodText = ((CorrectTag)innerTag).getFeedback();
		} else

			throw new OperationNotSupportedException("<answer> cannot enclose "
					+ innerTag.getClass().toString());
	}

}
