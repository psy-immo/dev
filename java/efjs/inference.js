/**
 * inference.js, (c) 2012, Immanuel Albrecht; Dresden University of
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


myInferenceButtons = [];
myInferenceId = 0;


/**
 * generate a check inference button
 * 
 * @param atags accept tags
 * @param rtags reject tags
 * 
 * @returns
 */

function InferenceButton(atags, rtags) {
	
	this.id = myInferenceId++;
	this.errorCount = 0;
	
	this.acceptTags = atags;
	this.rejectTags = rtags;
	
	/**
	 * unlike the answer button, the feedback text is given by the applet
	 */
	this.currentFeedback = "";
	this.text = "Check your answer";
	
	this.waitfor = [];
	
	this.WriteHtml = function() {
		if (inferenceIncludeApplet) {
			inferenceIncludeApplet = false;
			document.write("<applet id=\"inferenceApplet"+this.id+"\" "
					+ "name=\"inferenceApplet"+this.id+"\""
					+ " archive=\"inferenceApplet.jar\" "
					+ "code=\"de.tu_dresden.psy.util.LauncherApplet\" "
					+ "MAYSCRIPT style=\"width: 1px; height: 1px; float:right;\"></applet>");
		}
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" value=\"" + text
				+ "\" onclick=\"myInferenceButtons[" + this.id + "].OnClick()\"/>");
		document
		.write("<br /><table border=0 cellpadding=0 cellspacing=0><tr><td id=\"InferenceHint"
				+ this.id
				+ "\" style=\"height: 20px; width: 100%\"></td></tr></table>");		
		document.write("</form>");
	};
	
	/**
	 * We have to wait for the page to finish loading the applet instance before we may feed it with the relevant information
	 */
	this.FeedApplet = function() {
		var applet = document.getElementById("inferenceApplet"+this.id);		
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

		var applet = document.getElementById("inferenceApplet"+this.id);
	};
	
	/**
	 * this function sets the contents of the hint area
	 */
	this.SetHint = function(contents) {
		var td = document.getElementById("InferenceHint" + this.id);
		td.innerHTML = contents;
	};
	
	/**
	 * return the current state
	 */
	this.GetValue = function() {
		return ""+this.errorCount+ "\n" + escapeBTNR(this.currentFeedback);
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var data = ("" + contents).split("\n");
		if (data.length == 2) {		
			this.errorCount = parseInt(data[0]);
			
			this.currentFeedback = unescapeBTNR(data[1]);

			this.SetHint(this.currentFeedback);
			
		}
	};
	
	myStorage.RegisterField(this,"myInferenceButton["+this.id+"]");
	myInferenceButtons[this.id] = this;

	return this;
};

/**
 * this function takes care of giving the loaded applets the information they need to operate
 */

function FeedInferenceApplets() {
	for ( var int = 0; int < myInferenceButton.length; int++) {
		var btn = myInferenceButton[int];
		btn.FeedApplet();
	}
}
