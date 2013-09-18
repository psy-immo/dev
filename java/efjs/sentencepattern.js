/**
 * sentencepattern.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

var sentencePatternIdCounter = 0;
var sentencePatternArray = [];

/**
 * creates a sentence pattern object, that may contain token data, one at a time
 */
function SentencePattern(name, tags, generators, nonempty) {
	this.id = sentencePatternIdCounter++;

	/**
	 * Provide automatic name generation: use provided tags
	 */

	if ((name === undefined) || (name === "") || (name === false)) {
		this.name = "";
		for ( var i = 0; i < tags.length; ++i) {
			this.name += tags[i];
		}
	} else {
		this.name = name;
	}

	this.tags = tags;
	this.token = null;
	this.generators = generators;
	this.width = "";
	this.height = "";
	this.colorFilled = "#EEEEEE";
	this.colorBox = "#DDDDDD";
	this.noTakeOff = false;
	/**
	 * if nonempty evaluates to true, then we may not take off until all parts
	 * of the sentence box are filled.
	 */
	this.nonempty = nonempty;

	/**
	 * this function sets the bounding parameters
	 * 
	 * @returns this
	 */
	this.Size = function(width, height) {
		this.width = width;
		this.height = height;

		return this;
	};

	/**
	 * this function unsets/sets the flag that prevents take off from the
	 * sentencePattern
	 * 
	 * @returns this
	 */
	this.SetTakeOff = function(allowed) {
		this.noTakeOff = !allowed;

		return this;
	};

	/**
	 * this function sets the color parameters
	 * 
	 * @returns this
	 */
	this.Color = function(colorFilled) {
		this.colorFilled = colorFilled;

		return this;
	};

	/**
	 * this function sets the color parameters
	 * 
	 * @returns this
	 */
	this.ColorBox = function(color) {
		this.colorBox = color;

		return this;
	};
	
	/**
	 * @returns true, if the sentence pattern may be dragged off
	 */
	
	this.MayTakeoff = function() {
		var may_takeoff = true;
		for ( var int = 0; int < this.generators.length; ++int) {
			var part = this.generators[int];
			if (typeof part == "string") {
			} else if (typeof part == "object") {
				if (part.token) {
				} else {
					if (this.nonempty) {
						may_takeoff = false;
					}						
				}
			}
		}
		
		return may_takeoff;
	};

	/**
	 * write the HTML code that will be used for displaying the run way
	 */
	this.WriteHtml = function() {
		document.write("<span id=\"sentencePatternBox" + this.id + "\" ");

		document.write(" class=\"sentencepatternBox");
		
		if (this.MayTakeoff()) {
			document.write(" sentencePatternMayTakeoff");
		}
		
		document.write("\" ");
		document.write(" >");

		document.write("<span class=\"sentencepattern\" id=\"sentencePattern"
				+ this.id + "\" ");

		document.write(" style=\" ");
		document.write("\" ");

		document.write("onClick=\"sentencePatternArray[" + this.id
				+ "].OnClick()\">");

		document.write("<img class=\"sentencepatterntakeoff\" src=\""
				+ logletBaseURL
				+ "plane-freeware-jonas-hellwig--softicons.com.png\">");
		document.write("</span>");

		for ( var int = 0; int < this.generators.length; ++int) {
			var part = this.generators[int];
			if (typeof part == "string") {
				document.write(part);
			} else if (typeof part == "object") {
				part.WriteHtml();

				/**
				 * if the child object allows update subscription, then
				 * subscribe
				 */

				if (part.SubscribeUpdates) {
					part.SubscribeUpdates(function(me) {
						return function() {
							me.UpdatedContents();
						};
					}(this));
				}
			}
		}

		document.write("</span>");

		/**
		 * ignore default click handlers
		 */

		addMouseClickHook("sentencePattern" + this.id, 0, null);

	};
	

	/**
	 * callback if a child object has updated its data
	 */

	this.UpdatedContents = function() {
		var html_container = $("sentencePatternBox" + this.id);
		if (this.MayTakeoff()) {
			html_container.addClassName("sentencePatternMayTakeoff");
		} else {
			html_container.removeClassName("sentencePatternMayTakeoff");
		}
	};

	/**
	 * this function sets the objects token
	 */
	this.SetToken = function(token) {
	};

	/**
	 * this function returns the currently generated token
	 * 
	 */
	this.GetCurrentToken = function() {
		var token = "";

		for ( var int = 0; int < this.generators.length; ++int) {
			var part = this.generators[int];
			if (typeof part == "string") {
				token = token + part;
			} else if (typeof part == "object") {
				if (part.token) {
					token = token + part.token;
				} else {
					if (this.nonempty)
						return;
				}
			}
		}
		return token;
	};

	/**
	 * this function is called, when the run way object is clicked
	 */
	this.OnClick = function() {
		/**
		 * Handle landing
		 */
		if (myHover.flight) {

			/**
			 * this run way stays filled, do not allow landing
			 */
			return;
		}

		var current_token = this.GetCurrentToken();
		/**
		 * Allow take off
		 */
		if (current_token != null) {
			if (true != this.noTakeOff) {
				if (myHover.TakeOff(current_token, this, false)) {
					var log_data = "";
					if (this.name) {
						log_data += this.name;
					}
					log_data += " take off: " + myHover.token;
					myLogger.Log(log_data);
				}
			}
		}

	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {
		var log_data = "";
		if (this.name) {
			log_data += this.name;
		}
		log_data += " token returns: " + token;
		myLogger.Log(log_data);
	};

	/**
	 * this function is called, when a token is taken away after a touch down
	 */
	this.TakeAway = function() {
		/**
		 * no intervention needed
		 */
	};

	sentencePatternArray[this.id] = this;
	/**
	 * this is not for making checked answers, so we don't register in the tags
	 * hive
	 */
	// myTags.Add(this, this.tags);
}
