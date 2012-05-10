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
 * creates a drop down object, that represents a drop down list
 */
function Dropdown(name, tags, label, token) {
	this.id = dropdownIdCounter++;

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
	if (token === undefined) {
		this.token = "";
	} else {
		this.token = token;
	}
	
	if (label === undefined) {
		this.label = "";
	} else {
		this.label = label;
	}
	
	if (this.token == "") {
		this.token = this.label;
	}
	
	this.respawn = null;
	this.width = "";
	this.height = "";
	this.colorEmpty = "#CCCCAA";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";
	
	this.contentLabels = [];
	this.contentValues = [];
	
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
		var idstring = "dropdown"+this.id;
		
		document.write("<form onsubmit=\"return false;\" style=\"display: inline-block;\">");
		
		document.write("<select name=\""+idstring+ "\" id=\""+idstring+"\" " 
				+ "style=\"display: inline-block; ");
		if (this.width) {
			document.write("width: "+this.width+"; ");
		}
		if (this.height){
			document.write("height: "+this.height+"; ");
		}
		document.write( "background-color: "+this.colorEmpty+";\" "		
				+ "onchange=\"dropdownArray["+this.id+"].OnChange();\" "
				+ ">");
		document.write("<option value=\""+this.token+"\">"+this.label+"</option>");
		
		for ( var int = 0; int < this.contentLabels.length; int++) {
			document.write("<option value=\""+this.contentValues[int]+"\">");
			document.write(this.contentLabels[int]);
			document.write("</option>");
		}
		
		document.write("</select>");	
		
		document.write("</form>");
	};
	
	/**
	 * this function is called when the drop down box changes its contents
	 */
	this.OnChange = function() {
		var element = document.getElementById("dropdown"+this.id);
		this.token = element.value;
		
		if (element.selectedIndex > 0) {
			element.style.backgroundColor = this.colorFilled;
		} else {

			element.style.backgroundColor = this.colorEmpty;
		}
		
		myLogger.Log(this.name + " <- " + this.token);
	};

	/**
	 * this function marks the current drop down green
	 */
	this.MarkAsGood = function() {
		var html_object = document.getElementById("dropdown" + this.id);
		html_object.style.backgroundColor = this.colorGood;
	};

	/**
	 * this function demarks the current drop down
	 */
	this.MarkNeutral = function() {
		var element = document.getElementById("dropdown" + this.id);
		if (element.selectedIndex > 0) {
			element.style.backgroundColor = this.colorFilled;
		} else {
			element.style.backgroundColor = this.colorEmpty;
		}
	};
	
	/**
	 * return the current contents of the drop down as string
	 */
	this.GetValue = function() {
		var element = document.getElementById("dropdown"+this.id);
		return element.selectedIndex;
	};
	
	/**
	 * restore the drop down state from string
	 */
	
	this.SetValue = function(contents) {
		var element = document.getElementById("dropdown"+this.id);
		element.selectedIndex = contents;
		this.token = element.value;
			
		if (element.selectedIndex > 0) {
			element.style.backgroundColor = this.colorFilled;
		} else {
			element.style.backgroundColor = this.colorEmpty;
		}
	};

	dropdownArray[this.id] = this;
	myTags.Add(this, this.tags);
	
	myStorage.RegisterField(this,"dropdownArray["+this.id+"]");
}

