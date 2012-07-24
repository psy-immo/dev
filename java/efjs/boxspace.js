/**
 * boxspace.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var boxspaceIdCounter = 0;
var boxspaceArray = [];


/**
 * creates a workspace box that can be moved in a box workspace
 */

function FloatBox() {
	
	//TODO

	return this;
}

/**
 * creates a box workspace object, that may contain token data, one at a time
 */
function Boxspace(name, tags, accept, reject) {
	this.id = boxspaceIdCounter++;

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
	
	this.respawn = null;
	this.width = "600px";
	this.height = "600px";
	this.colorGround = "#CCCCCC";
	this.colorBoxes = "#CCCCFF";
	this.colorGood = "#CCFFCC";
	
	this.accept = accept;
	this.reject = reject;
	this.doRespawn = null;

	this.noTakeOff = false;
	
	this.contents = [];

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
	 * this function unsets/sets the flag that prevents take off from the boxspace
	 * 
	 * @returns this
	 */
	this.SetTakeOff = function(allowed) {
		this.noTakeOff = !allowed;

		return this;
	};

	/**
	 * this function sets the box workspace to be of respawning type
	 */
	this.Respawn = function(content) {
		this.doRespawn = this.token;
		this.respawn = this;
		return this;
	};

	/**
	 * this function sets the box workspace to be of refilling type
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
	this.Color = function(colorGround, colorBoxes) {
		this.colorGround = colorGround;
		this.colorBoxes = colorBoxes;

		return this;
	};

	/**
	 * write the HTML code that will be used for displaying the box workspace
	 */
	this.WriteHtml = function() {
		document.write("<div id=\"boxspace" + this.id + "\" ");

		document.write(" style=\" display: inline-block; ");

		document.write("background-color:" + this.colorGround + "; ");
		
		if (this.width) {
			document.write("width:" + this.width + "; ");
		}
		if (this.height) {
			document.write("height:" + this.height + "; ");
		}
		
		document.write("overflow: auto; ");
		
		document.write("\"");
		document.write("onClick=\"boxspaceArray[" + this.id + "].OnClick()\">");
				
		document.write("</div>");

	};


	/**
	 * this function marks the current box workspace green
	 */
	this.MarkAsGood = function() {
		
	};

	/**
	 * this function demarks the current box workspace
	 */
	this.MarkNeutral = function() {
		
	};

	/**
	 * this function is called, when the box workspace object is clicked
	 */
	this.OnClick = function() {
		
		//TODO
		
		/**
		 * Allow landing
		 */
		if (myHover.flight) {

			/**
			 * if this box workspace stays filled, do not allow landing
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
			 * now update the box workspace
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


	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {
		//TODO
	};

	/**
	 * this function is called, when a token is taken away after a touch down
	 */
	this.TakeAway = function() {
		//TODO		
	};

	/**
	 * return the current contents of the box workspace as string
	 */
	this.GetValue = function() {
		
		//TODO
		
		var value = "N";
		if (this.markedgood)
			value = "G";
		
		if (myHover.GetSourceIfFlying()===this) {
			return value + myHover.token;
		}

		if (this.token) {
			return value + this.token;
		}
		return value + "";
	};

	/**
	 * restore the box workspace state from string
	 */

	this.SetValue = function(contents) {
		
		//TODO
		
		if (contents) {
			this.SetToken(contents.substr(1));
			if (contents.charAt(0) == "G")
				this.MarkAsGood();
			else
				this.MarkNeutral();
		} else
			this.SetToken(contents);
	};

	boxspaceArray[this.id] = this;
	myTags.Add(this, this.tags);

	myStorage.RegisterField(this, "boxspaceArray[" + this.id + "]");
}
