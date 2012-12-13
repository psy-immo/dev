/**
 * trashcan.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

var trashcanIdCounter = 0;
var trashcanArray = [];

/**
 * creates a trash can object, that deletes the hovered token
 */
function Trashcan(name, tags, accept, reject) {
	this.id = trashcanIdCounter++;

	/**
	 * Provide automatic name generation: use provided tags
	 */

	if ((name === undefined) || (name === "") || (name === false)) {
		this.name = "";
		for ( var i = 0; i < tags.length; ++i) {
			this.name += tags[i];
		}
		;
	} else {

		this.name = name;
	}

	this.tags = tags;

	this.respawn = null;
	this.width = "50px";
	this.height = "50px";
	this.colorGround = "#888888";
	this.colorBoxes = "#CC0000";
	this.colorGood = "#CCFFCC";
	
	this.label = "Re- cycle";

	this.accept = accept;
	this.reject = reject;

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
	 * write the HTML code that will be used for displaying the trash can
	 */
	this.WriteHtml = function() {
		document.write("<div id=\"trashcan" + this.id + "\" ");

		document.write(" style=\" display: inline-block; ");

		document.write("background-color:" + this.colorGround + "; ");
		document.write("color:" + this.colorBoxes + "; ");

		if (this.width) {
			document.write("width:" + this.width + "; ");
		}
		if (this.height) {
			document.write("height:" + this.height + "; ");
		}


		document.write("\">");


		document.write("Re- cycle !");
		document.write("</div>");

		/**
		 * ignore default click handlers, install own handlers
		 * 
		 * with: * another lovely example of java script closure
		 */

		addMouseClickHook("trashcan" + this.id, 0, function(id) {
			return function() {
				trashcanArray[id].OnClick();
			};
		}(this.id));

	};

	/**
	 * this function is called, when the box workspace object is clicked
	 */
	this.OnClick = function() {

		/**
		 * Allow landing
		 */
		if (myHover.flight) {

			var log_data = "";
			if (myHover.source.name) {
				log_data += myHover.source.name;
			}
			log_data += " -> " + this.name + ": " + myHover.token;

			var plane_type = myHover.GetType();

			/**
			 * check for correct token type
			 */

			if ((plane_type != "text") && (plane_type != "box")) {
				myHover.CrashDown();

				myLogger.Log(log_data + " rejected (wrong type:" + plane_type
						+ ")");
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

			if (myHover.source.TakeAway) {
				myHover.source.TakeAway();
			}

			/**
			 * remove the plane
			 */

			myHover.CrashDown(true);

			/**
			 * that's it
			 */

			myLogger.Log(log_data);

			return;
		}

	};

	trashcanArray[this.id] = this;

}
