/**
 * solution.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

var answerIdCounter = 0;
var answerArray = [];

/**
 * returns an object that provides the operation of an answer button
 */

function Answer(testfn) {
	this.id = answerIdCounter++;
	this.feedbackAllGood = "Correct!";
	this.errorCount = 0;
	this.feedbackErrors = [ "Your solution still contains an error. Correct parts of your solution are lit green." ];
	this.text = "Check your answer";
	this.testfn = testfn;
	this.waitfor = [];
	this.uncheckedbadgood = 0;

	/**
	 * write the HTML code that will be used for displaying the answer button
	 */
	this.WriteHtml = function() {
		var idstring = "\"AnswerButton" + this.id + "\"";
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" name=" + idstring + " id="
				+ idstring + " value=\"" + this.text
				+ "\" onclick=\"answerArray[" + this.id + "].OnClick()\"/>");
		document
				.write("<br /><table border=0 cellpadding=0 cellspacing=0><tr><td id=\"AnswerHint"
						+ this.id
						+ "\" style=\"height: 20px; width: 100%\"></td></tr></table>");
		document.write("</form>");
	};

	/**
	 * this function sets the text of the component
	 * 
	 * @returns this
	 */
	this.Text = function(text) {
		this.text = text;
		return this;
	};

	/**
	 * this function adds a waitfor-function of the component, which is called
	 * whenever the answer button is clicked, and which has to return true in
	 * order to check the answer. The given function is responsible to alert the
	 * user that the answer will not be checked.
	 * 
	 * @returns this
	 */

	this.WaitFor = function(waitforfn) {
		this.waitfor[this.waitfor.length] = waitforfn;

		return this;
	};

	/**
	 * this function sets the texts for the feedback
	 * 
	 * @returns this
	 */
	this.Feedback = function(good, bad_list) {
		if (good)
			this.feedbackAllGood = good;
		if (bad_list)
			this.feedbackErrors = bad_list;
		return this;
	};

	/**
	 * this function sets the contents of the hint area
	 */
	this.SetHint = function(contents) {
		var td = document.getElementById("AnswerHint" + this.id);
		td.innerHTML = contents;
	};

	/**
	 * this is called whenever the button is clicked
	 */
	this.OnClick = function() {

		/**
		 * check, whether giving a solution is allowed
		 */
		for ( var int = 0; int < this.waitfor.length; int++) {
			if (this.waitfor[int]() != true) {
				myLogger.Log("Check answer " + this.id+ ": check refused by " + int + ".");
				return;
			}
		}
		
		/**
		 * update / save user input values
		 */
		
		myStorage.AutoUpdateAndStore();
		
		/**
		 * check the answer
		 */

		if (this.testfn()) {

			this.SetHint(this.feedbackAllGood);

			myLogger.Log("Check answer " + this.id+ ": good (" + this.errorCount + ")");

			this.uncheckedbadgood = 2;
		} else {
			this.SetHint(this.feedbackErrors[Math.min(
					this.feedbackErrors.length - 1, this.errorCount)]);
			this.errorCount++;

			myLogger.Log("Check answer " + this.id+ ": errors (" + this.errorCount + ")");

			this.uncheckedbadgood = 1;
		}
	};

	/**
	 * return the current state
	 */
	this.GetValue = function() {
		return "" + this.uncheckedbadgood + "," + this.errorCount;
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var data = ("" + contents).split(",");
		if (data.length == 2) {
			this.uncheckedbadgood = parseInt(data[0]);
			this.errorCount = parseInt(data[1]);

			if (this.uncheckedbadgood == 2) {
				this.SetHint(this.feedbackAllGood);
			} else if (this.uncheckedbadgood == 1) {
				this.SetHint(this.feedbackErrors[Math.min(
						this.feedbackErrors.length - 1, this.errorCount - 1)]);
			}
		}
	};

	answerArray[this.id] = this;
	myStorage.RegisterField(this, "answerArray[" + this.id + "]");
};
