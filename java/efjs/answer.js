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
	this.id = answerIdCounter ++;
	this.feedbackAllGood = "Correct!";
	this.feedbackErrors = "Your solution still contains an error. Correct parts of your solution are lit green.";
	this.text = "Check your answer";
	this.testfn = testfn;
	
	/**
	 * write the HTML code that will be used for displaying the answer button
	 */
	this.WriteHtml = function() {
		var idstring = "\"AnswerButton"+this.id+"\"";		
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" name="+idstring+" id="+idstring+" value=\""+this.text+"\" onclick=\"answerArray["+this.id+"].OnClick()\"/>");
		document.write("<br /><table border=0 cellpadding=0 cellspacing=0><tr><td id=\"AnswerHint"+this.id+"\" style=\"height: 20; width: 100%\"></td></tr></table>");
		document.write("</form>");
	};
	
	/**
	 * this function sets the contents of the hint area
	 */
	this.SetHint = function(contents) {
		var td = document.getElementById("AnswerHint"+this.id);
		td.innerHTML = contents;
	};
	
	/**
	 * this is called whenever the button is clicked
	 */
	this.OnClick = function() {
		myLogger.Log("Check answer");
		if ( this.testfn() ) {
			this.SetHint(this.feedbackAllGood);
		} else {
			this.SetHint(this.feedbackErrors);
		}
	};
	
	answerArray[this.id] = this;
};