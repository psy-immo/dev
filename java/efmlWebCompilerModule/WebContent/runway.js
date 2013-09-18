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
	this.token = token;
	this.respawn = null;
	this.width = "";
	this.height = "";
	this.colorEmpty = "runwayEmpty";
	this.colorFilled = "runwayNonempty";
	this.colorGood = "#CCFFCC";
	this.markedgood = false;
	this.accept = accept;
	this.reject = reject;
	this.doRespawn = null;
	this.stayFilled = false;
	this.noTakeOff = false;
	
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
	 * this function unsets/sets the flag that prevents take off from the runway
	 * 
	 * @returns this
	 */
	this.SetTakeOff = function(allowed) {
		this.noTakeOff = !allowed;

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
		document.write("<span id=\"runway" + this.id + "\" class=\"runway");

		

		if (this.token) {
			document.write(" "+this.colorFilled);
		}  else {
			document.write(" "+this.colorEmpty);
		}
		
		document.write("\" style=\" ");
		if (this.width) {
			document.write("width:" + this.width + "; ");
		}
		if (this.height) {
			document.write("height:" + this.height + "; ");
		}
		document.write("\">");

		if (this.token) {
			document.write(this.token);
		}
		document.write("</span>");

		/**
		 * ignore default click handlers
		 */

		addMouseClickHook("runway" + this.id, 0, function(id) {
			return function() {
				runwayArray[id].OnClick();
			};
		}(this.id));

	};

	/**
	 * this function sets the objects token
	 */
	this.SetToken = function(token) {

		this.token = token;
		var html_object = $("runway" + this.id);
		if (token) {
			html_object.addClassName(this.colorFilled);
			html_object.removeClassName(this.colorEmpty);
			html_object.innerHTML = token;
		} else {
			html_object.innerHTML = "&nbsp;";
			html_object.removeClassName(this.colorFilled);
			html_object.addClassName(this.colorEmpty);
		};
		

		/**
		 * notify subscribers about the update
		 */
		
		for ( var int = 0; int < this.subscribers.length; int++) {
			var notificator = this.subscribers[int];
			notificator();
		}
	};

	/**
	 * this function marks the current run way green
	 */
	this.MarkAsGood = function() {
		var html_object = $("runway" + this.id);
		html_object.addClassName("runwayMarkedGood");
		this.markedgood = true;
	};

	/**
	 * this function demarks the current run way
	 */
	this.MarkNeutral = function() {
		var html_object = $("runway" + this.id);
		if (this.token) {
			html_object.addClassName(this.colorFilled);
			html_object.removeClassName(this.colorEmpty);
		} else {
			html_object.removeClassName(this.colorFilled);
			html_object.addClassName(this.colorEmpty);
		}
		html_object.removeClassName("runwayMarkedGood");
		this.markedgood = false;
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
				myHover.CrashDown();

				return;
			}

			var log_data = "";
			if (myHover.source.name) {
				log_data += myHover.source.name;
			}
			log_data += " -> " + this.name + ": " + myHover.token;

			/**
			 * check for correct token type
			 */
			if (myHover.GetType() != "text") {
				myHover.CrashDown();

				myLogger.Log(log_data + " rejected");
				return;
			}

			/**
			 * check for acceptance tags
			 */
			if (this.accept) {
				if (myHover.source.tags) {
					for ( var i = 0; i < this.accept.length; i++) {
						if (myHover.source.tags.indexOf(this.accept[i]) < 0) {
							myHover.CrashDown();
							myLogger.Log(log_data + " rejected");
							return;
						}

					}
				} else {
					myHover.CrashDown();
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
							myHover.CrashDown();
							myLogger.Log(log_data + " rejected");
							return;
						}
					}
				}
			}

			/**
			 * the event handlers will be bubbling or capturing, depends on
			 * browser, so handle it twice, this is the capturing part
			 * this is done by myHover.CrashDown now
			 */
			/*if (myHover.source.TakeAway) {
				myHover.source.TakeAway();
			}*/

			/**
			 * remove the plane
			 */

			myHover.CrashDown(true);

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
			if (true != this.noTakeOff) {
				if (myHover.TakeOff(this.token, this, this.respawn)) {
					var log_data = "";
					if (this.name) {
						log_data += this.name;
					}
					log_data += " take off: " + myHover.token;
					myLogger.Log(log_data);

					if (this.stayFilled != true) {
						this.SetToken(null);
					}
				}
			}
		}

	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {
		this.SetToken(token);

		var log_data = "";
		if (this.name) {
			log_data += this.name;
		}
		log_data += " token returns: " + token;
		myLogger.Log(log_data);
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

	/**
	 * return the current contents of the run way as string
	 */
	this.GetValue = function() {
		var value = "N";
		if (this.markedgood)
			value = "G";

		if (myHover.GetSourceIfFlying() === this) {
			return value + myHover.token;
		}

		if (this.token) {
			return value + this.token;
		}
		return value + "";
	};

	/**
	 * restore the run way state from string
	 */

	this.SetValue = function(contents) {
		if (contents) {
			this.SetToken(contents.substr(1));
			if (contents.charAt(0) == "G")
				this.MarkAsGood();
			else
				this.MarkNeutral();
		} else
			this.SetToken(contents);
	};

	runwayArray[this.id] = this;
	myTags.Add(this, this.tags);

	myStorage.RegisterField(this, "runwayArray[" + this.id + "]");
}

/**
 * this function fixes the bug where runways are moving down when filled the
 * first time
 */
function RunwayDisplayBugfix() {
	for ( var int = 0; int < runwayArray.length; int++) {
		var runway = runwayArray[int];
		var value = runway.GetValue();
		runway.SetValue("Ngxl");
		runway.SetValue(value);
	}
}