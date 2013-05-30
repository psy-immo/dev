/**
 * popupbutton.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

popupButtonId = 0;

popupButtonArray = [];

/**
 * create a new popup button
 */

function PopupButton(name, target) {
	this.id = popupButtonId++;
	
	if ((name === undefined) || (name === "") || (name === false)) {
		this.name = "Help";
	} else {
		this.name = name;
	}
	
	this.text = this.name;
	this.style = "height=300,width=400";
	this.target = target;
	
	
	/**
	 * write the HTML code that will be used for displaying the answer button
	 */
	this.WriteHtml = function() {
		var idstring = "\"PopupButton" + this.id + "\"";
		document.write("<form onsubmit=\"return false;\" class=\"popupbutton\">");
		document.write("<input class=\"popupbutton\" type=\"button\" name=" + idstring + " id="
				+ idstring + " value=\"" + this.text
				+ "\" onclick=\"popupButtonArray[" + this.id + "].OnClick()\"/>");
		document.write("</form>");
	};
	

	/**
	 * this is called whenever the button is clicked
	 */
	this.OnClick = function() {
		var wnd = window.open(this.target,this.name,this.style);
		if (window.focus)
			wnd.focus();
		
		myLogger.Log("Popup "+this.name+": "+this.target);
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
	 * this function sets the style of the popup window
	 * 
	 * @returns this
	 */
	this.Style = function(style) {
		this.style = style;
		return this;
	};


	
	popupButtonArray[this.id] = this;
	
	return this;
}