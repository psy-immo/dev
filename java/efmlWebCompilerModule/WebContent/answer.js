/**
 * answer.js, (c) 2011-13, Immanuel Albrecht; Dresden University of Technology,
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
answerNames = {};

/**
 * returns an object that provides the operation of an answer button
 */

function Answer(name,testfn) {
	this.id = answerIdCounter++;
	
	if (name)
	{
		this.name = name;
	} else {
		this.name = "answer"+this.id;
	}
	
	this.feedbackAllGood = getRes("answerCorrect");
	this.errorCount = 0;
	this.feedbackErrors = [ getRes("answerErrors") ];
	
	this.text = getRes("answerButton");
	this.testfn = testfn;
	this.waitfor = [];
	this.uncheckedbadgood = 0;
	
	this.done = false;
	
	/**
	 * store the name of the FeedbackDisplay object, or false if no textual/html
	 * feedback is given
	 */

	this.feedback = false;
	
	/**
	 * contains the name of the counter object to check how many tries are left before rectification
	 */
	this.checkCounter = false;
	
	this.showAutoSpanAfterRect = [];
	this.hideAutoSpanAfterRect = [];
	this.lockAfterRectification = false;
	
	/**
	 * set the name of the feedback target
	 */

	this.FeedbackDisplay = function(target_display) {
		this.feedback = target_display;

		return this;
	};

	
	/**
	 * adds autospans to hide after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.HideAfterRectification = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.hideAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};
	
	/**
	 * adds autospans to show after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.ShowAfterRectification = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.showAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};

	/**
	 * whether to lock the airport after the rectification of the solution
	 */

	this.LockAfterRectification = function(doLockTag) {
		this.lockAfterRectification = doLockTag;

		return this;
	};
	
	/**
	 * set the name of the counter object
	 */
	
	this.Counter = function(name) {
		this.checkCounter = name;
		
		return this;
	};


	/**
	 * write the HTML code that will be used for displaying the answer button
	 */
	this.WriteHtml = function() {
		var idstring = "\"AnswerButton" + this.id + "\"";
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" name=" + idstring + " id="
				+ idstring + " value=\"" + this.text
				+ "\" onclick=\"answerArray[" + this.id + "].OnClick()\"/>");
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
		if (this.feedback) {
			var display = feedbackNames[this.feedback];
			if (display) {
				display.SetValue(contents);
				myLogger.Log("Check answer " + this.id+ ": give feedback: " + contents);
			}
		}
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
		 * check the counter
		 */
		
		var rectify = false;
		
		if (this.checkCounter) {
			var counter = counterNames[this.checkCounter];
			
			/**
			 * set new value
			 */
			
			var value = counter.GetValue();
			value -= 1;
			counter.SetValue(value);
			
			/**
			 * check for rectification
			 */
			
			if (!value) {
				rectify = true;
			}
		}
		
		var solved = false;
		
		/**
		 * check the answer
		 */

		if (this.testfn()) {

			this.SetHint(this.feedbackAllGood);

			myLogger.Log("Check answer " + this.id+ ": good (" + this.errorCount + ")");

			this.uncheckedbadgood = 2;
			
			solved = true;
		} else {
			this.SetHint(this.feedbackErrors[Math.min(
					this.feedbackErrors.length - 1, this.errorCount)]);
			this.errorCount++;

			myLogger.Log("Check answer " + this.id+ ": errors (" + this.errorCount + ")");

			this.uncheckedbadgood = 1;
			
		}
		
		if (solved || rectify) {
			myLogger.Log("Check answer " + this.id+ ": show/hide autospans.");
			
			this.done = true;
			
			/**
			 * magically hide some elements
			 */
			
			for ( var int99 = 0; int99 < this.hideAutoSpanAfterRect.length; int99++) {
				var name = this.hideAutoSpanAfterRect[int99];
				
				var span = autoSpanNames[name];
				
				if (span)
				{
					span.SetValue(0);
				}
				
			}
			
			/**
			 * magically show some elements
			 */
			
			for ( var int99 = 0; int99 < this.showAutoSpanAfterRect.length; int99++) {
				var name = this.showAutoSpanAfterRect[int99];
				
				var span = autoSpanNames[name];
				
				if (span)
				{
					span.SetValue(1);
				}
				
			}
			
			if (this.lockAfterRectification) {
				var targets = myTags.AllTags([this.lockAfterRectification]);
				for ( var int2 = 0; int2 < targets.length; int2++) {
					var target = targets[int2];
					if (target.LockInput)
						target.LockInput();
				}
			}
		}
	};

	/**
	 * return the current state
	 */
	this.GetValue = function() {
		return "" + this.uncheckedbadgood + "," + this.errorCount+","+(this.done?1:0);
	};

	/**
	 * restore the given state
	 */

	this.SetValue = function(contents) {
		var data = ("" + contents).split(",");
		if (data.length == 3) {
			this.uncheckedbadgood = parseInt(data[0]);
			this.errorCount = parseInt(data[1]);
			this.done = parseInt(data[2])!=0;

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
	answerNames[this.name] = this;
};
