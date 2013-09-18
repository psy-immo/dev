/**
 * freetext.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var freetextIdCounter = 0;
var freetextArray = [];

/**
 * creates a free text field object
 */
function Freetext(name, tags, label, embeddedMode) {
	this.id = freetextIdCounter++;

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

	if (label === undefined) {
		this.label = "";
	} else {
		this.label = label;
	}

	this.token = this.label;

	this.respawn = null;
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
	 * @param fn  function that is called on update
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
	 * write the HTML code that will be used for displaying the drop down
	 */
	this.WriteHtml = function() {
		var idstring = "freetext" + this.id;

		document.write("<form onsubmit=\"return false;\" class=\"freetext\">");

		document.write("<input type=\"text\" name=\"" + idstring + "\" id=\""
				+ idstring + "\" " + "value=\"" + this.token + "\""
				+ "class=\"freetext");

		if (this.token)
			document.write(" freetextNonempty");

		document.write("\" style=\"");
		if (this.width) {
			document.write("width: " + this.width + "; ");
		}
		if (this.height) {
			document.write("height: " + this.height + "; ");
		}

		document.write("\" " + "onchange=\"freetextArray[" + this.id
				+ "].OnChange();\" " + "/>");
		document.write("</form>");
	};

	/**
	 * this function is called when the input field changed its contents
	 */
	this.OnChange = function() {
		var element = document.getElementById("freetext" + this.id);

		var old = this.token;

		this.token = element.value;

		if (old != this.token) {
			myLogger.Log(this.name + " <- " + this.token);

			/**
			 * also update the coloring
			 */

			this.MarkNeutral();
			

			/**
			 * notify subscribers about the update
			 */
			
			for ( var int = 0; int < this.subscribers.length; int++) {
				var notificator = this.subscribers[int];
				notificator();
			}
		}
	};

	/**
	 * this function is called on each myStorage.StoreIn run
	 */
	this.Tick = function() {
		var element = document.getElementById("freetext" + this.id);
		var value = element.value;
		if (this.token != value)
			this.OnChange();
	};

	/**
	 * this function marks the current drop down green
	 */
	this.MarkAsGood = function() {
		var element = $("freetext" + this.id);
		element.addClassName("freetextMarkedGood");
	};

	/**
	 * this function demarks the current drop down
	 */
	this.MarkNeutral = function() {
		var element = $("freetext" + this.id);
		if (this.token) {
			element.addClassName("freetextNonempty");
		} else {
			element.removeClassName("freetextNonempty");
		}
		
		element.removeClassName("freetextMarkedGood");
	};

	/**
	 * return the current contents of the input field as string
	 */
	this.GetValue = function() {

		return this.token;
	};

	/**
	 * restore the input field from string
	 */

	this.SetValue = function(contents) {
		var element = document.getElementById("freetext" + this.id);

		if (contents === undefined) {
			contents = "";
		}

		element.value = contents;
		this.token = contents;
		this.old = this.token;

		/**
		 * also update the coloring
		 */

		this.MarkNeutral();
		

		/**
		 * notify subscribers about the update
		 */
		
		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}
	};

	freetextArray[this.id] = this;

	/**
	 * if we use this control as part of another control we may want to be able
	 * to override the save method and tag stuff
	 */
	if (!embeddedMode) {
		myTags.Add(this, this.tags);

		myStorage.RegisterField(this, "freetextArray[" + this.id + "]");
		myStorage.RequestTicks(this);
	}

	return this;
}
