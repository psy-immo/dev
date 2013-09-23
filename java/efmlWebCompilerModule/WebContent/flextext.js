/**
 * flextext.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var flextextIdCounter = 0;
var flextextArray = [];

/**
 * creates a flexible text object, which may change its token depending on other
 * tokens of a sentence pattern, left or right side.
 * 
 */
function FlexText(name, tags, tokenDefault, embeddedMode) {
	this.id = flextextIdCounter++;

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
	if (tokenDefault === undefined) {
		this.tokenDefault = "";
	} else {
		this.tokenDefault = tokenDefault;
	}

	this.token = this.tokenDefault;

	/**
	 * store the type of the pattern: -1 = left 1 = right 0 = left + right
	 */

	this.patternType = [];

	/**
	 * store a matcher to check the other tokens
	 */

	this.patternMatcher = [];

	/**
	 * store the token that magically appears if matched
	 */

	this.patternToken = [];

	/**
	 * add a match for the left-tokens
	 * 
	 * @param regexp
	 *            regular expression object
	 * @param token
	 *            token that appears if matched
	 * 
	 * @returns this
	 */
	this.CheckLeft = function(regexp, token) {

		this.patternType.push(-1);
		this.patternMatcher.push(regexp);
		this.patternToken.push(token);

		return this;
	};

	/**
	 * add a match for the right-tokens
	 * 
	 * @param regexp
	 *            regular expression object
	 * @param token
	 *            token that appears if matched
	 * 
	 * @returns this
	 */
	this.CheckRight = function(regexp, token) {

		this.patternType.push(1);
		this.patternMatcher.push(regexp);
		this.patternToken.push(token);

		return this;
	};

	/**
	 * add a match for the left- and right-tokens
	 * 
	 * @param regexp
	 *            regular expression object
	 * @param token
	 *            token that appears if matched
	 * 
	 * @returns this
	 */
	this.CheckBoth = function(regexp, token) {

		this.patternType.push(0);
		this.patternMatcher.push(regexp);
		this.patternToken.push(token);

		return this;
	};

	this.width = "";
	this.height = "";
	this.colorEmpty = "#CCCCAA";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";

	/**
	 * store subscription functions
	 */

	this.subscribers = [];

	/**
	 * add a function that is called everytime the contents change.
	 * 
	 * @param fn
	 *            function that is called on update
	 */

	this.SubscribeUpdates = function(fn) {
		this.subscribers.push(fn);
	};

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
	 * this function sets the color parameters
	 * 
	 * @returns this
	 */
	this.Color = function(colorEmpty, colorFilled) {
		this.colorEmpty = colorEmpty;
		this.colorFilled = colorFilled;

		return this;
	};

	/**
	 * write the HTML code that will be used for displaying the check box
	 */
	this.WriteHtml = function() {
		var idstring = "flextext" + this.id;

		document.write("<span id=\"" + idstring
				+ "\" class=\"flextext\" style=\"");

		if (this.width) {
			document.write("width: " + this.width + "; ");
		}
		if (this.height) {
			document.write("height: " + this.height + "; ");
		}

		document.write("\">");

		if (this.token) {
			document.write((""+this.token).replace(/ /g,"&nbsp;"));
		} else
			document.write("&nbsp;");

		document.write("</span>");

	};

	/**
	 * @param generators
	 *            a list of text generating objects
	 */

	this.FeedbackChanges = function(generators) {
		var new_token = this.tokenDefault;
		var left = "";
		var right = "";
		var got_me = false;

		for ( var int = 0; int < generators.length; int++) {
			var part = generators[int];
			if (typeof part == "string") {
				if (got_me) {
					right += part;
				} else {
					left += part;
				}
			} else if (typeof part == "object") {
				if (part.token) {
					if (got_me) {
						right += part.token;
					} else {
						if (part == this) {
							got_me = true;
						} else {
							left += part.token;
						}
					}

				}
			}
		}

		for ( var int2 = 0; int2 < this.patternType.length; int2++) {
			var type = this.patternType[int2];
			var rexp = this.patternMatcher[int2];

			if (type == -1) {
				if (rexp.test(left)) {
					new_token = this.patternToken[int2];
					break;
				}
			} else if (type == 1) {
				if (rexp.test(right)) {
					new_token = this.patternToken[int2];
					break;
				}
			} else {
				if (rexp.test(left + " " + right)) {
					new_token = this.patternToken[int2];
					break;
				}
			}
		}

		if (this.token != new_token) {
			/**
			 * this routine also calls the subscribers and updates the html
			 * object
			 */
			this.SetValue(new_token);
		}
	};

	/**
	 * this function marks the current check box green
	 */
	this.MarkAsGood = function() {
		var html_object = $("flextextForm" + this.id);
		html_object.addClassName("flextextMarkedGood");
	};

	/**
	 * this function demarks the current check box
	 */
	this.MarkNeutral = function() {
		var html_object = $("flextextForm" + this.id);
		html_object.removeClassName("flextextMarkedGood");

	};

	/**
	 * return the current contents of the check box as string
	 */
	this.GetValue = function() {

		return this.token;
	};

	/**
	 * restore the check box state from string
	 */

	this.SetValue = function(contents) {
		this.token = contents;

		var html_element = $("flextext" + this.id);
		if (html_element)
			if (this.token) {
				html_element.innerHTML = (""+this.token).replace(/ /g,"&nbsp;");
			} else {
				html_element.innerHTML = "&nbsp;";
			}

		/**
		 * notify subscribers about the update
		 */

		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}

	};

	flextextArray[this.id] = this;

	/**
	 * if we use this control as part of another control we may want to be able
	 * to override the save method and tag stuff
	 */
	if (!embeddedMode) {
		myTags.Add(this, this.tags);

		myStorage.RegisterField(this, "flextextArray[" + this.id + "]");
	}

	return this;
}
