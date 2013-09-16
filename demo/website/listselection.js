/**
 * listselection.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

var listselectionIdCounter = 0;
var listselectionArray = [];

/**
 * creates a drop down object, that represents a drop down list or - since it
 * uses the same html tags - a list box
 */
function ListSelection(name, tags, label, token) {
	this.id = listselectionIdCounter++;
	
	/**
	 * allow addition td-classes
	 */
	
	this.tdClasses = "";

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
	 * store the current selection, -1 => nothing selected
	 */
	this.selection = -1;

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
	 *
	 * adds some classes that are inherited by td, use it for alignment etc...
	 * 
	 * @returns this 
	 */
	
	this.TdClasses = function(classlist) {
		this.tdClasses = classlist;
		
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
		var idstring = "listselection" + this.id;

		document.write("<table class=\"listselection\" id=\"" + idstring
				+ "\" style=\" ");

		if (this.width) {
			document.write("width: " + this.width + "; ");
		}
		if (this.height) {
			document.write("height: " + this.height + "; ");
		}

		document.write("\">");

		for ( var int = 0; int < this.contentLabels.length; int++) {
			document.write("<tr class=\"listselection\" id=\"" + idstring + "["
					+ int + "]\" " + " onclick=\"listselectionArray[" + this.id
					+ "].OnClick(" + int + ");\"" + ">");
			document.write("<td class=\""+this.tdClasses+"\">");
			document.write(this.contentValues[int]);
			document.write("</td></tr>");
		}

		document.write("</table>");

	};

	/**
	 * this handler is called, whenever a row of the list selection is clicked
	 */
	this.OnClick = function(which) {
		this.SetValue(which);

		/**
		 * log the interaction
		 */

		myLogger.Log(this.name + " <- " + this.token);
	};

	/**
	 * this function is called when the drop down box changes its contents
	 */
	this.OnChange = function() {
		var html_element = $("listselection" + this.id);

		if (this.islistbox) {
			if (html_element.selectedIndex >= 0) {

				html_element.addClassName("listselectionNonempty");
				this.token = this.contentValues[html_element.selectedIndex - 1];
			} else {
				html_element.removeClassName("listselectionNonempty");

				this.token = this.emptyToken;
			}

		} else {

			if (html_element.selectedIndex > 0) {

				html_element.addClassName("listselectionNonempty");
				this.token = this.contentValues[html_element.selectedIndex - 1];
			} else {
				html_element.removeClassName("listselectionNonempty");

				this.token = this.emptyToken;
			}
		}

		/**
		 * also demark the goodness of the listselection
		 */

		html_element.removeClassName("listselectionMarkedGood");

	};

	/**
	 * this function marks the current drop down green
	 */
	this.MarkAsGood = function() {
		var html_element = $("listselection" + this.id);
		html_element.addClassName("listselectionMarkedGood");
	};

	/**
	 * this function demarks the current drop down
	 */
	this.MarkNeutral = function() {
		var html_element = $("listselection" + this.id);
		html_element.removeClassName("listselectionMarkedGood");
	};

	/**
	 * return the current contents of the drop down as string
	 */
	this.GetValue = function() {
		return this.selection;
	};

	/**
	 * restore the drop down state from string
	 */

	this.SetValue = function(contents) {
		this.selection = parseInt(contents);

		var html_element = $("listselection" + this.id);
		html_element.selectedIndex = contents;

		if (this.selection >= 0) {
			html_element.addClassName("listselectionNonEmpty");

			this.token = this.contentValues[this.selection];

		} else {

			html_element.removeClassName("listselectionNonEmpty");

			this.token = this.emptyToken;
		}

		for ( var int = 0; int < this.contentLabels.length; int++) {
			var line_element = $("listselection" + this.id + "[" + int + "]");

			if (int == this.selection) {
				line_element.addClassName("listselectionSelected");
			} else {
				line_element.removeClassName("listselectionSelected");
			}
		}

		/**
		 * also demark the goodness of the listselection
		 */

		html_element.removeClassName("listselectionMarkedGood");

	};

	listselectionArray[this.id] = this;
	myTags.Add(this, this.tags);

	myStorage.RegisterField(this, "listselectionArray[" + this.id + "]");
}
