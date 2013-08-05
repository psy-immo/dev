/**
 * FeedbackTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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
 * implements the &lt;feedback> tag for &lt;inference>
 * 
 * @author immanuel
 * 
 */

public class FeedbackTag implements AnyTag {

	private ArrayList<InferenceSolutionRequirementTag> requires;
	private CorrectTag correct;
	private ArrayList<HintTag> hints;
	private NeedJustificationTag needjustification;
	private IncompleteTag incomplete;
	private IncorrectTag incorrect;

	public FeedbackTag() {
		this.requires = new ArrayList<InferenceSolutionRequirementTag>();
		this.hints = new ArrayList<HintTag>();
		this.correct = null;
		this.needjustification = null;
		this.incomplete = null;
		this.incorrect = null;
	}

	/**
	 * @return the requires
	 */
	public ArrayList<InferenceSolutionRequirementTag> getRequires() {
		return this.requires;
	}

	/**
	 * @return the correct
	 */
	public CorrectTag getCorrect() {
		return this.correct;
	}

	/**
	 * @return the incorrect
	 */
	public IncorrectTag getIncorrect() {
		return this.incorrect;
	}

	/**
	 * @return the incomplete
	 */
	public IncompleteTag getIncomplete() {
		return this.incomplete;
	}

	/**
	 * @return the hints
	 */
	public ArrayList<HintTag> getHints() {
		return this.hints;
	}

	/**
	 * @return the needjustification
	 */
	public NeedJustificationTag getNeedjustification() {
		return this.needjustification;
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
		if (innerTag.getClass() == PlainContent.class) {
			return;
		}

		if (innerTag.getClass() == CorrectTag.class) {
			this.correct = (CorrectTag) innerTag;
		} else if (innerTag.getClass() == NeedJustificationTag.class) {
			this.needjustification = (NeedJustificationTag) innerTag;
		} else if (innerTag.getClass() == HintTag.class) {
			this.hints.add((HintTag) innerTag);
		} else if (innerTag instanceof InferenceSolutionRequirementTag) {
			this.requires.add((InferenceSolutionRequirementTag) innerTag);
		} else if (innerTag.getClass() == IncompleteTag.class) {
			this.incomplete = (IncompleteTag) innerTag;
		} else if (innerTag.getClass() == IncorrectTag.class) {
			this.incorrect = (IncorrectTag) innerTag;
		} else {
			throw new OperationNotSupportedException(
					"<feedback> cannot enclose "
							+ innerTag.getClass().toString());
		}
	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<feedback>");
		if (this.correct != null) {
			representation.append(this.correct.getEfml());
		}
		if (this.incorrect != null) {
			representation.append(this.incorrect.getEfml());
		}
		if (this.incomplete != null) {
			representation.append(this.incomplete.getEfml());
		}
		if (this.needjustification != null) {
			representation.append(this.needjustification.getEfml());
		}
		for (AnyTag t : this.hints) {
			representation.append(t.getEfml());
		}
		for (AnyTag t : this.requires) {
			representation.append(t.getEfml());
		}
		representation.append("</feedback>");

		return representation.toString();
	}

}
