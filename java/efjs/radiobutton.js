/**
 * radiobutton.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

var radiobuttonIdCounter = 0;
var radiobuttonArray = [];

/**
 * creates a radio button object, that represents a radio button list
 */
function RadioButton(name, tags, type, token, embeddedMode) {
	this.id = radiobuttonIdCounter++;

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

	this.type = type;
	if (!(this.type))
		this.type = "form";

	this.tags = tags;
	if (token === undefined) {
		this.token = null;
	} else {
		this.token = token;
	}

	this.emptyToken = this.token;

	this.width = "";
	this.height = "";
	this.colorEmpty = "#CCCCAA";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";

	this.contentLabels = [];
	this.contentValues = [];

	this.selected = -1;

	this.label = null;
	
	/**
	 * store subscription functions
	 */
	
	this.subscribers = [];
	
	/**
	 * add a function that is called everytime the contents change.
	 * 
	 * @param fn  function that is called on update
	 */
	
	this.SubscribeUpdates = function(fn) {
		this.subscribers.push(fn);
	};


	/**
	 * adds another option to the radio button box
	 * 
	 * @returns this
	 */
	this.Option = function(label, value) {
		if ((value === undefined) || (value === "") || (value === false)) {
			this.contentValues[this.contentValues.length] = label;
		} else {
			this.contentValues[this.contentValues.length] = value;
		}
		this.contentLabels[this.contentLabels.length] = label;

		return this;
	};

	/**
	 * 
	 * adds a global text to be displayed before the radio box
	 * 
	 * @returns this*
	 */

	this.Label = function(text) {
		this.label = text;
		return this;
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
	 * write the HTML code that will be used for displaying the radio button
	 */
	this.WriteHtml = function() {
		var idstring = "radiobutton" + this.id;

		if (this.type == "form") {
			document
					.write("<form id=\""
							+ idstring
							+ "\" onsubmit=\"return false;\" class=\"radiobutton\" style=\"");
		} else if (this.type == "tr") {
			document.write("<tr id=\"" + idstring
					+ "\" class=\"radiobutton\" style=\"");
		}

		if (this.width) {
			document.write("width: " + this.width + "; ");
		}
		if (this.height) {
			document.write("height: " + this.height + "; ");
		}
		document.write("\">");

		if (this.label) {
			if (this.type == "form") {
				document.write(this.label + "<br />");
			} else {
				document.write("<td>" + this.label + "</td>");
			}
		}

		for ( var int = 0; int < this.contentLabels.length; int++) {
			if (this.type == "tr") {
				document
						.write("<td class=\"radiobutton\"><form onsubmit=\"return false;\" class=\"radiobuttonNested\">");
			}

			document.write("<span onclick=\"radiobuttonArray[" + this.id
					+ "].OnClick(" + int + ")\">");
			document.write("<input class=\"radiobutton\" type=\"radio\" name=\"" + idstring
					+ "group\" id=\"" + idstring + "_" + int + "\""
					+ " value=\"" + this.contentValues[int] + "\"/>");
			document.write(this.contentLabels[int]);
			document.write("</span>");
			if (this.type == "form") {
				document.write("<br/>");
			} else if (this.type == "tr") {
				if (this.type == "tr") {
					document.write("</form></td>");
				}
			}
		}
		if (this.type == "tr") {
			document.write("</tr>");
		}
	};

	/**
	 * this function is called when the radio button box changes its contents
	 */
	this.OnClick = function(nbr) {

		this.token = this.contentValues[nbr];

		if (this.type == "form") {

			if (this.selected >= 0) {
				var old = document.getElementById("radiobutton" + this.id + "_"
						+ this.selected);
				old.checked = false;
			}
		} else if (this.type == "tr") {
			for ( var int = 0; int < this.contentValues.length; ++int) {
				var old = document.getElementById("radiobutton" + this.id + "_"
						+ int);
				old.checked = false;
			}
		}

		if (nbr != this.selected) {

			this.selected = nbr;

			this.MarkNeutral();
		}

		var selection = document.getElementById("radiobutton" + this.id + "_"
				+ this.selected);
		selection.checked = true;

		myLogger.Log(this.name + " <- " + this.token);
		

		/**
		 * notify subscribers about the update
		 */
		
		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}
	};

	/**
	 * this function marks the current radio button green
	 */
	this.MarkAsGood = function() {
		var html_object = $("radiobutton" + this.id);
		html_object.addClassName("radiobuttonMarkedGood");
	};

	/**
	 * this function demarks the current radio button
	 */
	this.MarkNeutral = function() {
		var element = $("radiobutton" + this.id);
		if (this.selected >= 0) {
			element.addClassName("radiobuttonNonempty");
		} else {
			element.removeClassName("radiobuttonNonempty");
		}
		
		element.removeClassName("radiobuttonMarkedGood");
	};

	/**
	 * return the current contents of the radio button as string
	 */
	this.GetValue = function() {
		return this.selected;
	};

	/**
	 * restore the radio button state from string
	 */

	this.SetValue = function(contents) {
		var selection = parseInt(contents);

		for ( var int = 0; int < this.contentLabels.length; int++) {
			var btn = document.getElementById("radiobutton" + this.id + "_"
					+ int);
			btn.checked = false;
		}

		this.selected = -1;
		this.token = this.emptyToken;

		if (selection >= 0) {
			this.OnClick(selection);
		}
		

		/**
		 * notify subscribers about the update
		 */
		
		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}
	};

	radiobuttonArray[this.id] = this;
	
	/**
	 * if we use this control as part of another control we may want to be able
	 * to override the save method and tag stuff
	 */
	if (!embeddedMode) {
		myTags.Add(this, this.tags);

		myStorage.RegisterField(this, "radiobuttonArray[" + this.id + "]");
	}
	
	return this;
}
