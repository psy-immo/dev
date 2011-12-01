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
 * creates a run way object, that may contain token data, one at a time
 */
function Runway(name, tags, token, accept, reject) {
	this.id = runwayIdCounter++;
	this.name = name;
	this.tags = tags;
	this.token = token;
	this.respawn = null;
	this.width = "200";
	this.height = "20";
	this.colorEmpty = "#CCCCCC";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";
	this.accept = accept;
	this.reject = reject;
	this.doRespawn = null;
	this.stayFilled = false;

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
	 * this function sets the run way to be of respawning type
	 */
	this.Respawn = function(content) {
		this.doRespawn = this.token;
		this.respawn = this;
		return this;
	};

	/**
	 * this function sets the run way to be of refilling type
	 */
	this.Refilling = function(content) {
		this.stayFilled = true;

		return this;
	};

	/**
	 * this function provides the respawning
	 */
	this.DoRespawn = function() {
		if (this.respawn) {
			this.respawn.DoRespawn();
		}
		this.SetToken(this.doRespawn);
		this.respawn = this;
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
	 * write the HTML code that will be used for displaying the run way
	 */
	this.WriteHtml = function() {
		document.write("<TABLE cellpadding=0 cellspacing=0><TR><TD id=\"runway"
				+ this.id + "\" ");
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
	 * this function marks the current run way green
	 */
	this.MarkAsGood = function() {
		var html_object = document.getElementById("runway" + this.id);
		html_object.style.backgroundColor = this.colorGood;
	};

	/**
	 * this function demarks the current run way
	 */
	this.MarkNeutral = function() {
		var html_object = document.getElementById("runway" + this.id);
		if (this.token) {

			html_object.style.backgroundColor = this.colorFilled;
		} else {

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
			 * if this run way stays filled, do not allow landing
			 */
			if (this.stayFilled) {
				return;
			}

			var log_data = "";
			if (myHover.source.name) {
				log_data += myHover.source.name;
			}
			log_data += " -> " + this.name + ": " + myHover.token;

			/**
			 * check for acceptance tags
			 */
			if (this.accept) {
				if (myHover.source.tags) {
					for ( var i = 0; i < this.accept.length; i++) {
						if (myHover.source.tags.indexOf(this.accept[i]) < 0) {
							myLogger.Log(log_data + " rejected");
							return;
						}

					}
				} else {
					myLogger.Log(log_data + " rejected");
					return;
				}
			}

			/**
			 * check for rejection tags
			 */
			if (this.reject) {
				if (myHover.source.tags) {
					for ( var i = 0; i < this.reject.length; i++) {
						if (myHover.source.tags.indexOf(this.reject[i]) >= 0) {
							myLogger.Log(log_data + " rejected");
							return;
						}
					}
				}
			}

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

			/**
			 * respawn the old contents
			 */

			var respawn = this.respawn;

			this.respawn = myHover.respawn;

			if (respawn) {
				respawn.DoRespawn();
			}

			myLogger.Log(log_data);

			return;
		}
		/**
		 * Allow take off
		 */
		if (this.token) {
			if (myHover.TakeOff(this.token, this, this.respawn))
				if (this.stayFilled != true) {
					this.SetToken(null);
				}
		}

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
		if (this.stayFilled) {
			return;
		}
		this.SetToken(null);
		this.respawn = null;
	};

	runwayArray[this.id] = this;
	myTags.Add(this, this.tags);
}