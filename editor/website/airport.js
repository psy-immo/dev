/**
 * airport.js, (c) 2012-13, Immanuel Albrecht; Dresden University of Technology,
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

var airportIdCounter = 0;
var airportArray = [];

/**
 * creates an object, that may contain token data, one at a time, for a given
 * airport line
 */

function AirportRunway(airport, index, tags) {
	this.airport = airport;
	this.index = index;
	this.tags = tags;

	this.token = null;

	this.SetToken = function(token) {
		this.token = token;
	};

	this.Delete = function() {
		myTags.Remove(this);
	};

	this.MarkNeutral = function() {
		this.airport.marked[this.index] = "N";
		this.airport.UpdateContents();
	};

	this.MarkAsOkay = function() {
		this.airport.marked[this.index] = "O";
		this.airport.UpdateContents();
	};

	this.MarkAsBad = function() {
		this.airport.marked[this.index] = "B";
		this.airport.UpdateContents();
	};

	this.MarkAsGood = function() {
		this.airport.marked[this.index] = "G";
		this.airport.UpdateContents();
	};

	myTags.Add(this, this.tags);
	myTags.Add(this, [ "i" + this.index ], true);
}

/**
 * creates a airport object, that is a sequence of runways
 */
function Airport(name, tags, accept, reject) {
	this.id = airportIdCounter++;

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
	this.token = null;
	this.width = null;
	this.height = null;
	this.colorEmpty = "#CCCCCC";
	this.colorFilled = "#CCCCFF";
	this.colorGood = "#CCFFCC";
	this.colorOkay = "#FFEE00";
	this.colorBad = "#EE6666";
	this.colorAround = "#E8E8E8";
	this.markedgood = false;
	this.accept = accept;
	this.reject = reject;
	this.noTakeOff = false;
	this.content = [];
	this.marked = [];
	this.respawn = [];
	this.returnIndex = -1;
	this.returnMark = "N";
	this.returnRespawn = null;
	this.tokenObjects = [];

	/**
	 * sets the contents of the airport
	 */

	this.UpdateContents = function() {

		var element = document.getElementById("airport" + this.id);
		var contents = "<table class=\"airport\" style=\"";

		contents += "\">";

		var count = 0;

		for ( var int = 0; int < this.content.length; ++int) {

			/**
			 * padding
			 */

			contents += "<tr class=\"airportGap\" style=\"";

			contents += "\" ";
			contents += "onClick=\"airportArray[" + this.id + "].OnClickRow("
					+ count + ")\"><td>";
			contents += "</td><td></td></tr>";

			count++;

			/**
			 * line
			 */

			contents += "<tr class=\"";
			if (this.marked[int] == "G") {
				contents += "airportGood";
			} else if (this.marked[int] == "O") {
				contents += "airportOkay";
			} else if (this.marked[int] == "B") {
				contents += "airportBad";
			} else if (this.content[int])
				contents += "airportNonempty";
			else
				contents += "airportEmpty";

			contents += "\"";
			contents += " style=\"";
			contents += "\" >";

			contents += "<td class=\"airportButtonDel\" style=\"";
			contents += " \" onClick=\"airportArray[" + this.id
					+ "].OnDeleteRow(" + count + ")\">";
			contents += "<img class=\"airportTrashcan\" src=\""
					+ logletBaseURL
					+ "TRASH_FULL-CC-Attribution-Noncommercial-NoDerivate-3.0-Gordon-Irving--www.gordonirvingdesign.com.png\"/>";
			contents += "</td>";
			contents += "<td onClick=\"airportArray[" + this.id
					+ "].OnClickRow(" + count + ")\">";
			contents += this.content[int];

			contents += "</td></tr>";

			count++;

		}

		/**
		 * padding
		 */

		contents += "<tr class=\"airportGap\" style=\"";
		contents += "\" ";

		contents += "onClick=\"airportArray[" + this.id + "].OnClickRow("
				+ count + ")\"><td>&nbsp;";
		contents += "</td><td>&nbsp;</td></tr>";

		count++;

		contents += "</table>";

		element.innerHTML = contents;

		/**
		 * create objects for tag hive
		 */

		if (this.tokenObjects.length < this.content.length) {
			for ( var int = this.tokenObjects.length; int < this.content.length; ++int) {
				this.tokenObjects.push(new AirportRunway(this, int, this.tags));
			}
		}

		if (this.tokenObjects.length > this.content.length) {
			var diff = this.tokenObjects.length - this.content.length;
			for ( var int = 0; int < diff; ++int) {
				this.tokenObjects.pop().Delete();
			}
		}

		/**
		 * copy data
		 */

		for ( var int = 0; int < this.content.length; ++int) {
			this.tokenObjects[int].token = this.content[int];
		}
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
	 * this function unsets/sets the flag that prevents take off from the
	 * airport
	 * 
	 * @returns this
	 */
	this.SetTakeOff = function(allowed) {
		this.noTakeOff = !allowed;

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
	 * write the HTML code that will be used for displaying the airport
	 */
	this.WriteHtml = function() {
		document.write("<span id=\"airport" + this.id + "\" ");
		document.write(" class=\"airport\" ");

		document.write(" style=\" ");

		if (this.width) {
			document.write("width:" + this.width + "; ");
		}
		if (this.height) {
			document.write("height:" + this.height + "; ");
		}
		document.write("\">");

		/**
		 * content
		 */

		var contents = "<table class=\"airport\" style=\"  ";

		contents += "\">";

		/**
		 * padding
		 */

		contents += "<tr class=\"airportEmpty\" style=\"";

		contents += "\" ";

		contents += "onClick=\"airportArray[" + this.id
				+ "].OnClickRow(0)\"><td>&nbsp;";
		contents += "</td><td>&nbsp;</td></tr>";

		contents += "</table>";

		document.write(contents);

		// document.write("onClick=\"airportArray[" + this.id +
		// "].OnClick()\">");

		if (this.token) {
			document.write(this.token);
		}
		document.write("</span>");

		/**
		 * ignore default click handlers
		 */

		addMouseClickHook("airport" + this.id, 0, null);

	};

	/**
	 * this function sets the objects token
	 */
	this.SetToken = function(token) {

		console.log("set token");

	};

	this.MarkAsGood = function() {
		/**
		 * we ignore this
		 */
	};

	/**
	 * this function demarks the current airport
	 */
	this.MarkNeutral = function() {
		this.marked = this.content.map(function(x) {
			return "N";
		});

		this.UpdateContents();
	};

	/**
	 * this function is called, when a row is clicked
	 */
	this.OnClickRow = function(row) {
		var index = Math.ceil((row - 1) / 2);

		if (myHover.flight) {
			var log_data = "";
			if (myHover.source.name) {
				log_data += myHover.source.name;
			}
			log_data += " -> " + this.name + " [" + index + "]: "
					+ myHover.token;

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
			 */
			/**
			 * old
			 * 
			 * 
			 * if (myHover.source.TakeAway) { myHover.source.TakeAway(); }
			 */

			/**
			 * now crash down the plane
			 */

			myHover.CrashDown(true);

			/**
			 * now update the airport
			 */

			var new_content = [];
			var new_marked = [];
			var new_respawn = [];

			var old_int = 0;

			for ( var int = 0; int < this.content.length + 1; ++int) {
				if (int != index) {
					new_content.push(this.content[old_int]);
					new_marked.push(this.marked[old_int]);
					new_respawn.push(this.respawn[old_int]);
					old_int++;
				} else {
					new_content.push(myHover.token);
					new_marked.push("N");
					new_respawn.push(myHover.respawn);
				}
			}

			this.marked = new_marked;
			this.content = new_content;
			this.respawn = new_respawn;

			this.UpdateContents();

			myLogger.Log(log_data);

			return;
		}

		/**
		 * Allow take off
		 */

		if (index < this.content.length) {
			if (true == this.noTakeOff)
				return;

			var new_content = [];
			var new_marked = [];
			var new_respawn = [];

			for ( var int = 0; int < this.content.length; ++int) {
				if (int != index) {
					new_content.push(this.content[int]);
					new_marked.push(this.marked[int]);
					new_respawn.push(this.respawn[int]);
				} else {

					this.returnMark = this.marked[int];
					this.returnRespawn = this.respawn[int];

					if (myHover.TakeOff(this.content[int], this,
							this.respawn[int])) {
						var log_data = "";
						if (this.name) {
							log_data += this.name;
						}
						log_data += "[" + index + "] take off: "
								+ myHover.token;
						myLogger.Log(log_data);
					} else {
						/**
						 * Take off denied
						 */
						return;
					}
				}
			}

			this.marked = new_marked;
			this.content = new_content;
			this.respawn = new_respawn;
			this.returnIndex = index;

			this.UpdateContents();

		}

	};

	/**
	 * this function is called, when the delete button is clicked
	 */
	this.OnDeleteRow = function(row) {

		/**
		 * Allow landing, ignore delete button
		 */

		if (myHover.flight) {
			this.OnClickRow(row);
			return;
		}

		var index = (row - 1) / 2;
		var new_content = [];
		var new_marked = [];
		var new_respawn = [];

		for ( var int = 0; int < this.content.length; ++int) {
			if (int != index) {
				new_content.push(this.content[int]);
				new_marked.push(this.marked[int]);
				new_respawn.push(this.respawn[int]);
			} else {
				if (this.respawn[int])
					this.respawn[int].DoRespawn();
			}
		}

		this.marked = new_marked;
		this.content = new_content;
		this.respawn = new_respawn;

		this.UpdateContents();
	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {

		var index = this.returnIndex;

		if (index < 0)
			return;

		this.returnIndex = -1;

		var new_content = [];
		var new_marked = [];
		var new_respawn = [];

		var old_int = 0;

		for ( var int = 0; int < this.content.length + 1; ++int) {
			if (int != index) {
				new_content.push(this.content[old_int]);
				new_marked.push(this.marked[old_int]);
				new_respawn.push(this.respawn[old_int]);
				old_int++;
			} else {
				new_content.push(token);
				new_marked.push(this.returnMark);
				new_respawn.push(this.returnRespawn);
			}
		}

		this.marked = new_marked;
		this.content = new_content;
		this.respawn = new_respawn;

		this.UpdateContents();

		var log_data = "";
		if (this.name) {
			log_data += this.name;
		}
		log_data += "[" + index + "] token returns: " + token;
		myLogger.Log(log_data);
	};

	/**
	 * this function is called, when a token is taken away after a touch down
	 */
	this.TakeAway = function() {
		this.returnIndex = -1;
	};

	/**
	 * return the current contents of the airport as string
	 */
	this.GetValue = function() {
		var value = "";
		for ( var int = 0; int < this.marked.length; ++int) {
			if (int > 0) {
				value += "\n";
			}
			value += this.marked[int] + "\t" + escapeBTNR(this.content[int]);

		}

		return escapeBTNR(value);
	};

	/**
	 * restore the airport state from string
	 */

	this.SetValue = function(contents) {
		this.marked = [];
		this.content = [];

		if (contents) {
			var data = unescapeBTNR(contents).split("\n");

			for ( var int = 0; int < data.length; ++int) {
				var pair = data[int].split("\t");
				this.marked.push(pair[0]);
				this.content.push(unescapeBTNR(pair[1]));
			}
		}

		this.UpdateContents();
	};

	airportArray[this.id] = this;

	myStorage.RegisterField(this, "airportArray[" + this.id + "]");

}
