/**
 * remotebutton.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var remoteButtonIdCounter = 0;
var remoteButtonArray = [];

/**
 * returns an object that provides the operation of an remoteButton button
 */

function RemoteButton() {
	this.id = remoteButtonIdCounter++;
	
	this.text = getRes("remoteButtonText");
	this.executeFns = [];
	
	this.showAutoSpanAfterRect = [];
	this.hideAutoSpanAfterRect = [];
	
	
    /**
	 * write the HTML code that will be used for displaying the remoteButton button
	 */
	this.WriteHtml = function() {
		var idstring = "\"RemoteButtonButton" + this.id + "\"";
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" name=" + idstring + " id="
				+ idstring + " value=\"" + this.text
				+ "\" onclick=\"remoteButtonArray[" + this.id + "].OnClick()\"/>");
		document.write("</form>");
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
	 * this is called whenever the button is clicked
	 */
	this.OnClick = function() {
		
		myLogger.Log("remoteButton["+this.id+"] clicked.");
		
		var all_done = true;
		
		for ( var int = 0; int < this.executeFns.length; int++) {
			var fn = this.executeFns[int];
			var retval = fn();
			if (typeof retval == "undefined") {
				
			} else
			{
				if (!retval)
					all_done = false;
			}
		}
		
		if (all_done) {
			myLogger.Log("Remote button " + this.id+ ": show/hide autospans.");
			
			/**
			 * magically hide some elements
			 */
			
			for ( var int99 = 0; int99 < this.hideAutoSpanAfterRect.length; int99++) {
				var name = this.hideAutoSpanAfterRect[int99];
				
				var span = autoSpanNames[name];
				
				if (span)
				{
					span.SetValue(0);
				}
				
			}
			
			/**
			 * magically show some elements
			 */
			
			for ( var int99 = 0; int99 < this.showAutoSpanAfterRect.length; int99++) {
				var name = this.showAutoSpanAfterRect[int99];
				
				var span = autoSpanNames[name];
				
				if (span)
				{
					span.SetValue(1);
				}	
			}			
		}
	};
	
	/**
	 * adds autospans to hide after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.HideAfter = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.hideAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};
	
	/**
	 * adds autospans to show after rectification
	 * 
	 * @param namelist     a string with , separated names for autospan objects
	 * 
	 * @returns this
	 */
	
	this.ShowAfter = function(namelist) {
		var split = namelist.split(",");
		for ( var int = 0; int < split.length; int++) {
			var x = split[int];
			this.showAutoSpanAfterRect.push(x);
		}
		
		
		return this;
	};


	/**
	 *  adds a slave call
	 */
	
	this.AddSlaveCall = function(fn) {
		this.executeFns.push(fn);
		return this;
	};

	remoteButtonArray[this.id] = this;
};
