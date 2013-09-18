/**
 * dropdown.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var dropdownIdCounter = 0;
var dropdownArray = [];

/**
 * creates a drop down object, that represents a drop down list or - since it
 * uses the same html tags - a list box
 */
function Dropdown(name, tags, label, token) {
	this.id = dropdownIdCounter++;

	/**
	 * default to dropdown mode
	 */
	this.islistbox = false;

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
	if (token === undefined) {
		this.token = null;
	} else {
		this.token = token;
	}

	this.emptyToken = this.token;

	if (label === undefined) {
		this.label = "";
	} else {
		this.label = label;
	}

	this.width = "";
	this.height = "";
	this.colorEmpty = "#CCCCAA";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";

	this.contentLabels = [];
	this.contentValues = [];
	
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
	 * adds another option to the drop down box
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
	 * this function sets up the list box mode
	 * 
	 * @returns this
	 */

	this.Listbox = function() {
		this.islistbox = true;

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
	 * write the HTML code that will be used for displaying the drop down
	 */
	this.WriteHtml = function() {
		var idstring = "dropdown" + this.id;

		document.write("<form onsubmit=\"return false;\" class=\"dropdown\">");

		if (this.islistbox) {
			document.write("<div class=\"norightscroll\" style=\"");

			if (this.width) {
				document.write("width: " + this.width + "; ");
			}
			if (this.height) {
				document.write("height: " + this.height + "; ");
			}

			document.write("\">");
		}

		document.write("<select name=\"" + idstring + "\" id=\"" + idstring
				+ "\" " + "class=\"dropdown\" style=\"");
		if (!this.islistbox) {
			if (this.width) {
				document.write("width: " + this.width + "; ");
			}
			if (this.height) {
				document.write("height: " + this.height + "; ");
			}
		}
		document.write("\" " + "onchange=\"dropdownArray[" + this.id
				+ "].OnChange();\" ");

		if (this.islistbox) {
			document.write(" size=\"" + this.contentLabels.length + "\"");
		}

		document.write(">");

		if (!this.islistbox) {
			document.write("<option class=\"dropdownoption\" value=\""
					+ this.token + "\">" + this.label + "</option>");
		}

		for ( var int = 0; int < this.contentLabels.length; int++) {
			if (this.islistbox) {
				document.write("<option class=\"listboxoption\" value=\""
						+ this.contentValues[int] + "\">");
			} else {
				document.write("<option class=\"dropdownoption\" value=\""
						+ this.contentValues[int] + "\">");
			}
			document.write(this.contentLabels[int]);
			document.write("</option>");
		}

		document.write("</select>");

		if (this.islistbox) {
			document.write("</div>");
		}

		document.write("</form>");
	};

	/**
	 * this function is called when the drop down box changes its contents
	 */
	this.OnChange = function() {
		var html_element = $("dropdown" + this.id);

		if (this.islistbox) {
			if (html_element.selectedIndex >= 0) {

				html_element.addClassName("dropdownNonempty");
				this.token = this.contentValues[html_element.selectedIndex - 1];
			} else {
				html_element.removeClassName("dropdownNonempty");

				this.token = this.emptyToken;
			}

		} else {

			if (html_element.selectedIndex > 0) {

				html_element.addClassName("dropdownNonempty");
				this.token = this.contentValues[html_element.selectedIndex - 1];
			} else {
				html_element.removeClassName("dropdownNonempty");

				this.token = this.emptyToken;
			}
		}

		/**
		 * also demark the goodness of the dropdown
		 */

		html_element.removeClassName("dropdownMarkedGood");

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
	 * this function marks the current drop down green
	 */
	this.MarkAsGood = function() {
		var html_element = $("dropdown" + this.id);
		html_element.addClassName("dropdownMarkedGood");
	};

	/**
	 * this function demarks the current drop down
	 */
	this.MarkNeutral = function() {
		var html_element = $("dropdown" + this.id);
		html_element.removeClassName("dropdownMarkedGood");
	};

	/**
	 * return the current contents of the drop down as string
	 */
	this.GetValue = function() {
		var element = document.getElementById("dropdown" + this.id);
		return element.selectedIndex;
	};

	/**
	 * restore the drop down state from string
	 */

	this.SetValue = function(contents) {
		var html_element = $("dropdown" + this.id);
		html_element.selectedIndex = contents;

		if (html_element.selectedIndex > 0) {

			html_element.addClassName("dropdownNonempty");
			this.token = this.contentValues[html_element.selectedIndex - 1];
		} else {
			html_element.removeClassName("dropdownNonempty");

			this.token = this.emptyToken;
		}

		/**
		 * also demark the goodness of the dropdown
		 */

		html_element.removeClassName("dropdownMarkedGood");
		
		/**
		 * notify subscribers about the update
		 */
		
		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}
	};

	dropdownArray[this.id] = this;
	myTags.Add(this, this.tags);

	myStorage.RegisterField(this, "dropdownArray[" + this.id + "]");
}
