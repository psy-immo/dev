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
var floatboxIdCounter = 0;
var floatboxArray = [];

/**
 * creates a workspace box that can be moved in a box workspace
 */

function FloatBox(style, content) {
	this.id = floatboxIdCounter++;

	this.parent = null;

	if (style)
		this.style = style;
	else
		this.style = "";
	if (content)
		this.content = content;
	else
		this.content = "";

	this.allowTakeOff = true;
	this.takeOffType = "box";

	this.keepElement = null;

	this.name = "";
	this.tags = [];

	/**
	 * set the id of the parent element
	 * 
	 * @returns this
	 */

	this.SetParentId = function(id) {
		this.parent = id;
		return this;
	};

	/**
	 * return the parent box space id
	 */

	this.GetParentId = function() {
		return this.parent;
	};

	/**
	 * sets the take off allowance and type
	 * 
	 * @returns this
	 */
	this.SetTakeOff = function(allow, type) {
		if (type)
			this.takeOffType = type;
		this.allowTakeOff = allow ? true : false;
	};

	/**
	 * @returns the html code for the floatbox
	 */

	this.GetHtmlCode = function() {
		var html = "<div id=\"floatbox" + this.id + "\" ";

		html += " style=\" display: inline-block; ";
		html += " position: absolute; ";
		html += this.style;
		html += "\" >";
		html += this.content;
		html += "</div>";

		return html;
	};

	/**
	 * write the HTML code that will be used for displaying the box workspace
	 */
	this.WriteHtml = function() {
		document.write(this.GetHtmlCode());

		/**
		 * lovely example of java script closure: the outer function call with
		 * this.id binds the id for the inner function on return, and the double
		 * abstraction binds the this variable to the correct object when
		 * OnClick is called. (return floatboxArray[id].OnClick would not bind
		 * the this variable)
		 */

		addMouseClickHook("floatbox" + this.id, 0, function(id) {
			return function() {
				floatboxArray[id].OnClick();
			};
		}(this.id));
	};

	/**
	 * create and add the div object
	 */

	this.CreateDiv = function(id) {
		if (id == undefined) {
			id = "mainframe";
		}
		var html = this.GetHtmlCode();

		var parent = document.getElementById(id);

		var container = document.createElement('div');

		container.innerHTML = html;

		parent.appendChild(container.firstChild);
	};

	/**
	 * add the div object to the parent and register hooks
	 */
	this.Create = function(parent) {
		this.CreateDiv(parent);

		addMouseClickHook("floatbox" + this.id, 0, function(id) {
			return function() {
				floatboxArray[id].OnClick();
			};
		}(this.id));
	};

	/**
	 * called when the box is clicked at
	 */

	this.OnClick = function() {
		if (myHover.flight) {
			boxspaceArray[this.parent].OnClick();
			return;
		}

		if (this.allowTakeOff) {
			/**
			 * take off the box
			 */
			this.keepElement = document.getElementById("floatbox" + this.id);
			this.RemoveDiv();

			this.name = boxspaceArray[this.parent].name;
			this.tags = boxspaceArray[this.parent].tags;

			myHover.TakeOff(this.content, this, null, null, this.takeOffType);
		}

	};

	/**
	 * get the contents of the box
	 */

	this.GetContent = function() {
		return this.content;
	};

	/**
	 * get the style of the box
	 */

	this.GetStyle = function() {
		var elt = document.getElementById("floatbox" + this.id);
		if (elt) {
			return removeCss(/^\s*(left|top)\s*/, this.style) + "left: "
					+ elt.style.left + ";" + "top: " + elt.style.top + ";";
		}
		return this.style;
	};

	/**
	 * get the type of the box
	 */
	this.GetBoxType = function() {
		return "FloatBox";
	};

	/**
	 * remove the div from the document
	 */
	this.RemoveDiv = function() {
		var elt = document.getElementById("floatbox" + this.id);
		if (elt)
			elt.parentNode.removeChild(elt);
	};

	/**
	 * give the div element
	 */

	this.GetElement = function() {
		return document.getElementById("floatbox" + this.id);
	};

	/**
	 * also remove the java script object that represents this box
	 */
	this.Remove = function() {
		var elt = document.getElementById("floatbox" + this.id);

		clearMouseClickHooks("floatbox" + this.id);
		clearMouseClickHooks(elt);

		this.RemoveDiv();

		floatboxArray[this.id] = undefined;
	};

	/**
	 * this function is called, when a token is given back after a take off
	 */
	this.GiveBackToken = function(token) {
		var parent = document.getElementById("boxspace" + this.parent);
		parent.appendChild(this.keepElement);
		this.keepElement = null;
	};

	/**
	 * this function is called, when a token is taken away after a touch down
	 */
	this.TakeAway = function() {
		clearMouseClickHooks("floatbox" + this.id);
		clearMouseClickHooks(this.keepElement);
		floatboxArray[this.id] = undefined;
		this.keepElement = null;
		boxspaceArray[this.parent].DelBox(this);
	};

	floatboxArray[this.id] = this;
	return this;
}

/**
 * creates a box of given type
 */

function BoxFactory(type, style, content) {
	if (type == "FloatBox") {
		return new FloatBox(style, content);
	}
};

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
		;
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
	 * this function adds a box for the box space
	 * 
	 * use before writing the boxspace (i.e. will not work in dynamic phase)
	 * 
	 * 
	 * @returns this
	 */
	this.AddBox = function(box) {
		box.SetParentId(this.id);
		this.contents.push(box);

		return this;
	};

	/**
	 * this function removes a box from the box space
	 */
	this.DelBox = function(box, dontRemove) {
		if (!dontRemove) {
			box.Remove();
		}
		var idx = this.contents.lastIndexOf(box);
		if (idx >= 0) {
			var last_element = this.contents.pop();
			if (idx < this.contents.length) {
				this.contents[idx] = last_element;
			}
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
	 * this function unsets/sets the flag that prevents take off from the
	 * boxspace
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

		/**
		 * be the reference for children absolute positions
		 */
		document.write("position: relative; ");

		document.write("overflow: auto; ");

		document.write("\">");

		for ( var int = 0; int < this.contents.length; int++) {
			this.contents[int].WriteHtml();
		}

		document.write("</div>");

		/**
		 * ignore default click handlers, install own handlers
		 * 
		 * with: * another lovely example of java script closure
		 */

		addMouseClickHook("boxspace" + this.id, 0, function(id) {
			return function() {
				boxspaceArray[id].OnClick();
			};
		}(this.id));

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
			var plane = myHover.token;

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
			if (plane_type == "text")
				if (myHover.source.TakeAway) {
					myHover.source.TakeAway();
				}

			/**
			 * remove the plane
			 */

			myHover.CrashDown(true);

			var layout = $("boxspace" + this.id).getLayout();
			var scrollme = $("boxspace" + this.id).cumulativeScrollOffset();
			var scrolldaddy = $("boxspace" + this.id).parentNode
					.cumulativeScrollOffset();
			var left = mouseX - layout.get("left") + scrollme["left"]
					- scrolldaddy["left"];
			var top = mouseY - layout.get("top") + scrollme["top"]
					- scrolldaddy["top"];

			/**
			 * now add a float box
			 */
			if (plane_type == "text") {
				var box = new FloatBox("background-color: #EEEEEE;" + "left: "
						+ left + "px;" + "top: " + top + "px;", plane);
				box.SetParentId(this.id);
				box.Create("boxspace" + this.id);
				this.contents.push(box);
			} else if (plane_type == "box") {
				var box = myHover.source;
				if (box.GetParentId() == this.id) {
					/**
					 * own box
					 */
					box.GiveBackToken();
					/**
					 * move box
					 */
					box.GetElement().style.left = left + "px";
					box.GetElement().style.top = top + "px";
				} else {
					/**
					 * different box space
					 */
					var content = box.GetContent();
					var style = box.GetStyle();
					var type = box.GetBoxType();

					box.TakeAway();
					
					box = BoxFactory(type,
							removeCss(/^\s*(left|top)\s*/, style) + "left: "
									+ left + "px;" + "top: " + top + "px;",
							content);

					box.SetParentId(this.id);
					box.Create("boxspace" + this.id);
					this.contents.push(box);
				}

			}

			myLogger.Log(log_data);

			return;
		}

	};

	/**
	 * return the current contents of the box workspace as string
	 */
	this.GetValue = function() {

		var data = "" + this.contents.length;

		for ( var int = 0; int < this.contents.length; int++) {
			var box = this.contents[int];
			data += "\n" + escapeBTNR(box.GetBoxType());
			data += "\n" + escapeBTNR(box.GetStyle());
			data += "\n" + escapeBTNR(box.GetContent());
		}

		return data;
	};

	/**
	 * remove all boxes from workspace (dynamic part)
	 */

	this.RemoveAllBoxes = function() {
		for ( var int = 0; int < this.contents.length; int++) {
			var box = this.contents[int];
			box.Remove();
		}
		this.contents = [];
	};

	/**
	 * restore the box workspace state from string
	 */

	this.SetValue = function(contents) {
		if (!contents)
			return;

		this.RemoveAllBoxes();

		var parts = contents.split("\n");
		var count = parseInt(parts[0]);
		for ( var int = 0; int < count; ++int) {
			var type = unescapeBTNR(parts[1 + 3 * int]);
			var style = unescapeBTNR(parts[2 + 3 * int]);
			var content = unescapeBTNR(parts[3 + 3 * int]);
			var box = BoxFactory(type, style, content);
			box.SetParentId(this.id);
			box.Create("boxspace" + this.id);
			this.contents.push(box);
		}
		;

	};

	boxspaceArray[this.id] = this;
	myTags.Add(this, this.tags);

	myStorage.RegisterField(this, "boxspaceArray[" + this.id + "]");
}
