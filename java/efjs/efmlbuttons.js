/**
 * efmlbuttons.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

efmlButtonCounter = 0;
efmlButtonArray = [];

/**
 * creates an EfmlButton object
 */

function EfmlButton(contents, parent, fn) {

	this.id = efmlButtonCounter++;

	this.width = null;
	this.height = null;

	this.parent = parent;

	/**
	 * returns html code that represents this board
	 */

	this.GetHtmlCode = function() {
		var html = "<div id=\"efmlButton" + this.id + "\" ";

		html += " style=\"";
		html += " display: inline-block;";
		html += " background: #EEEEEE;";
		html += " border-style: solid; ";
		html += " padding: 2px;";
		html += " border-color: #000000;";

		if (this.width)
			html += " width: " + this.width + ";";
		if (this.height)
			html += " height: " + this.height + ";";
		html += "\"";
		html += ">";

		html += contents;

		html += "</div>";
		return html;
	};

	/**
	 * writes the efml board objects html code
	 */

	this.WriteHtml = function() {
		document.write(this.GetHtmlCode());

		this.RegisterMouse();
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
	 * this function sets the parameter
	 * 
	 * @returns this
	 */
	this.Width = function(width) {
		this.width = width;

		return this;
	};

	/**
	 * this function sets the parameter
	 * 
	 * @returns this
	 */
	this.Height = function(height) {
		this.height = height;

		return this;
	};

	/**
	 * @returns the corresponding html element
	 */

	this.GetElement = function() {
		return document.getElementById("efmlButton" + this.id);
	};

	/**
	 * this function registers the mouse hooks for this object
	 */

	this.RegisterMouse = function() {

		addMouseClickHook("efmlButton" + this.id, 0, function(me) {
			return function() {
				me.OnClick();
			};
		}(this));

	};

	/**
	 * this function removes the mouse hooks for this object
	 */

	this.UnregisterMouse = function() {
		var elt = this.GetElement();

		clearMouseClickHooks("efmlButton" + this.id);
		clearMouseClickHooks(elt);

		clearMouseDownHooks("efmlButton" + this.id);
		clearMouseDownHooks(elt);

		clearMouseUpHooks("efmlButton" + this.id);
		clearMouseUpHooks(elt);
	};

	if (fn)
		this.OnClick = fn;
	else
		/**
		 * empty default click handler
		 */
		this.OnClick = function() {
			alert("Pressed!!");
		};

	efmlButtonArray[this.id] = this;
	return this;
};
