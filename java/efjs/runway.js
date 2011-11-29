/**
 * runway.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

var runwayIdCounter = 0;
var runwayArray = [];

/**
 * creates a runway object, that may contain token data, one at a time
 */
function Runway() {
	this.id = runwayIdCounter++;

	this.token = null;
	this.width = "200";
	this.height = "20";
	this.colorEmpty = "#CCCCCC";
	this.colorFilled = "#CCCCFF";

	/**
	 * write the HTML code that will be used for displaying the runway
	 */
	this.WriteHtml = function() {
		document.write("<TABLE><TR><TD id=\"runway" + this.id + "\" ");
		if (this.width) {
			document.write("width=\"" + this.width + "\" ");
		}
		if (this.height) {
			document.write("height=\"" + this.height + "\" ");
		}
		if (this.token) {
			document.write("style=\"background-color:" + this.colorFilled
					+ "\" ");
		} else {
			document.write("style=\"background-color:" + this.colorEmpty
					+ "\" ");
		}
		document.write("onClick=\"runwayArray[" + this.id + "].OnClick()\">");
		if (this.token) {
			document.write(this.token);
		}
		document.write("</TD></TR></TABLE>");
	};

	/**
	 * this function sets the objects token
	 */
	this.SetToken = function(token) {
		this.token = token;
		var html_object = document.getElementById("runway" + this.id);
		if (token) {
			html_object.innerHTML = token;
			html_object.style.backgroundColor = this.colorFilled;
		} else {
			html_object.innerHTML = "";
			html_object.style.backgroundColor = this.colorEmpty;
		}
	};

	/**
	 * this function is called, when the run way object is clicked
	 */
	this.OnClick = function() {
		/**
		 * Allow landing
		 */
		if (myHover.flight) {
			/**
			 * the event handlers will be bubbling or capturing, depends on
			 * browser, so handle it twice, this is the capturing part
			 */
			if (myHover.source.TakeAway) {
				myHover.source.TakeAway();
			}
			/**
			 * and the bubbling part
			 */
			myHover.dontGiveBack = true;

			/**
			 * now update the run way
			 */
			this.SetToken(myHover.token);

			return;
		}
		/**
		 * Allow take off
		 */
		if (this.token) {
			myHover.TakeOff(this.token, this);
			this.SetToken(null);
		}
		;
	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {
		this.SetToken(token);
	};

	/**
	 * this function is called, when a token is taken away after a touch down
	 */
	this.TakeAway = function() {
		this.SetToken(null);
	};

	runwayArray[this.id] = this;
}