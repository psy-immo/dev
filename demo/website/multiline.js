/**
 * multiline.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var multilineIdCounter = 0;
var multilineArray = [];

/**
 * creates a free text field object
 */
function Multiline(name, tags, label) {
	this.id = multilineIdCounter++;

	/**
	 * Provide automatic name generation: use provided tags
	 */

	if ((name === undefined) || (name === "") || (name === false)) {
		this.name = "";
		for (var i = 0; i < tags.length; ++i) {
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
		var idstring = "multiline"+this.id;
		
		document.write("<form onsubmit=\"return false;\" style=\"display: inline-block;\">");
		
		document.write("<textarea name=\""+idstring+ "\" id=\""+idstring+"\" "
				+ "style=\"display: inline-block; ");
		if (this.width) {
			document.write("width: "+this.width+"; ");
		}
		if (this.height){
			document.write("height: "+this.height+"; ");
		}
		
		
		
		var color = this.colorEmpty;
		
		if (this.token) {
			color = this.colorFilled;
		}
		
		document.write( "background-color: "+color+";\" "		
				+ "onchange=\"multilineArray["+this.id+"].OnChange();\" "
				+ "/>");
		
		document.write(this.token);
		document.write("</textarea>");
		document.write("</form>");
	};
	
	/**
	 * this function is called when the input field changed its contents
	 */
	this.OnChange = function() {
		var element = document.getElementById("multiline"+this.id);
		
		var old = this.token;
		
		this.token = element.value;
		
		if (old != this.token) {
			myLogger.Log(this.name + " <- " + this.token);
			
			/**
			 * also update the coloring
			 */
			
			this.MarkNeutral();
		}
	};
	
	/**
	 * this function is called on each myStorage.StoreIn run
	 */
	this.Tick = function() {
		var element = document.getElementById("multiline"+this.id);
		var value = element.value;
		if (this.token != value)
			this.OnChange();
	};

	/**
	 * this function marks the current drop down green
	 */
	this.MarkAsGood = function() {
		var html_object = document.getElementById("multiline" + this.id);
		html_object.style.backgroundColor = this.colorGood;
	};

	/**
	 * this function demarks the current drop down
	 */
	this.MarkNeutral = function() {
		var element = document.getElementById("multiline" + this.id);
		if (this.token) {
			element.style.backgroundColor = this.colorFilled;
		} else {
			element.style.backgroundColor = this.colorEmpty;
		}
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
		var element = document.getElementById("multiline"+this.id);
		
		if (contents === undefined) {
			contents = "";
		}
		
		element.value = contents;		
		this.token = contents;
		
		/**
		 * also update the coloring
		 */
		
		this.MarkNeutral();
	};

	multilineArray[this.id] = this;
	myTags.Add(this, this.tags);
	
	myStorage.RegisterField(this,"multilineArray["+this.id+"]");
	myStorage.RequestTicks(this);
	
	return this;
}

