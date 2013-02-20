/**
 * efmlboard.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

efmlBoardCounter = 0;
efmlBoardArray = [];

/**
 * creates an EfmlBoard object that can take efml code objects as children...
 */

function EfmlBoard(name, tags, accept, reject, embeddedMode) {
	this.id = efmlBoardCounter++;

	this.html_created = false;

	/**
	 * store the currently hovered object
	 */
	this.hoverIndex = -1;
	this.hoverSelected = false;
	this.hoverCursorIndex = -1;

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

	this.acceptedTypes = [ "text", "EfmlTag", "" ];

	this.width = "600px";
	this.height = "800px";

	this.accept = accept;
	this.reject = reject;

	this.buttons = [ new EfmlButton("Copy", function(me) {
		return function() {
			me.Copy();
		};
	}(this)), new EfmlButton("Paste", function(me) {
		return function() {
			me.Paste();
		};
	}(this)), new EfmlButton("Cut", function(me) {
		return function() {
			me.Cut();
		};
	}(this)), new EfmlButton("Export", function(me) {
		return function() {
			me.Export();
		};
	}(this)), new EfmlButton("Import", function(me) {
		return function() {
			me.Import();
		};
	}(this)) ];

	this.contents = [];
	this.content_id = 0;

	/**
	 * we interpret the cursor position to point at the gap before the array
	 * element that is referenced by its integer value
	 */
	this.cursor = 0;
	this.selected = [];

	/**
	 * sets the contents
	 */

	this.SetContents = function(data) {
		this.contents = [];
		this.selected = [];

		var elements = data.split("\n");

		for ( var int = 0; int < elements.length; int++) {
			var content_block = elements[int];

			if (content_block) {

				this.contents.push(NewEfmlTag(content_block, this.name
						+ ".contents[" + int + "]", this.tags, this.accept,
						this.reject));
				this.selected.push(false);

			}
		}

		this.cursor = this.contents.length;

		this.token = this.GetEfml();

		this.content_id = this.contents.length;

		return this;
	};

	/**
	 * insert contents
	 * 
	 * @param where
	 *            index where the new content is inserted in the contents array
	 * @param what
	 *            newline-separated efml tag descriptions
	 * @returns number of inserted elements
	 */

	this.InsertContents = function(where, what) {

		/** generate new objects */
		var new_contents = [];
		var elements = what.split("\n");

		for ( var int = 0; int < elements.length; int++) {
			var data = elements[int];
			if (data) {
				new_contents.push(NewEfmlTag(data, this.name + ".contents["
						+ this.content_id + "]", this.tags, this.accept,
						this.reject));
				this.content_id += 1;
			}
		}

		/** add new objects at the appropriate position */

		var this_contents = [];
		var this_selected = [];

		for ( var int2 = 0; int2 < this.contents.length + 1; int2++) {
			if (int2 == where) {
				for ( var int3 = 0; int3 < new_contents.length; int3++) {
					this_contents.push(new_contents[int3]);
					this_selected.push(false);
				}
			}

			if (int2 < this.contents.length) {
				this_contents.push(this.contents[int2]);
				this_selected.push(this.selected[int2]);
			}
		}

		/** update cursor */

		if (this.cursor >= where) {
			this.cursor += new_contents.length;
		}

		/** unregister mouse handlers */

		this.UnregisterMouse();

		/** remove old html content representation */

		var html_contents = document.getElementById("efmlBoardContents"
				+ this.id);
		var html_parent = html_contents.parentNode;
		html_parent.removeChild(html_contents);

		/** use new contents now */

		this.contents = this_contents;
		this.selected = this_selected;

		/** add new html content representation */

		var html = this.GetContentsHtmlCode();

		var container = document.createElement('div');

		container.innerHTML = html;

		html_parent.appendChild(container.firstChild);

		/** register mouse handlers */

		this.RegisterMouse();

		/** update efml token */

		this.token = this.GetEfml();

		/** log */

		var log_data = this.name + ": InsertContents: " + where + " through "
				+ (where + new_contents.length - 1) + ": " + escapeBTNR(what);

		myLogger.Log(log_data);

		return new_contents.length;
	};

	/**
	 * updates the display of the selection status
	 */

	this.UpdateSelectionHighlighting = function() {
		if (this.html_created) {
			for ( var int = 0; int < this.selected.length; int++) {
				var is_selected = this.selected[int];

				var marker_element = document
						.getElementById("efmlBoardContents" + this.id + "["
								+ int + "].selected");

				if (is_selected)
					marker_element.style.background = "#88FFDD";
				else
					marker_element.style.background = "#338866";

				marker_element = document.getElementById("efmlBoardContents"
						+ this.id + "[" + int + "].cut");

				if (is_selected)
					marker_element.style.background = "#88FFDD";
				else
					marker_element.style.background = "#338866";

				marker_element = document.getElementById("efmlBoardContents"
						+ this.id + "[" + int + "].takeoff");

				if (is_selected)
					marker_element.style.background = "#88FFDD";
				else
					marker_element.style.background = "#338866";

			}
		}
	};

	/**
	 * updates the display of the cursor position
	 */

	this.UpdateCursorHighlighting = function() {
		if (this.html_created) {
			for ( var int = 0; int < this.selected.length + 1; int++) {
				var is_selected = int == this.cursor;

				var marker_element = document
						.getElementById("efmlBoardContents" + this.id + ".Gap["
								+ int + "]");

				if (is_selected)
					marker_element.style.background = "#88FFDD";
				else
					marker_element.style.background = "#338866";
			}
		}
	};

	/**
	 * returns the html code for the contents
	 */

	this.GetContentsHtmlCode = function() {
		var html = "";

		html += "<table id=\"efmlBoardContents" + this.id + "\"";
		html += "style=\"";
		html += " width: 100%;";
		html += "\">";

		html += "<tr id=\"efmlBoardContents" + this.id
				+ ".Gap[0]\" style=\"height: 4px;";
		if (this.cursor == 0)
			html += " background: #88FFDD;";
		else
			html += " background: #338866;";
		html += "\">";
		html += "<td style=\"width: 20px;\">";
		html += "</td>";
		html += "<td style=\"width: 20px;\">";
		html += "</td>";
		html += "<td style=\"width: 20px;\">";
		html += "</td>";
		html += "<td>";
		html += "</td>";
		html += "</tr>";

		for ( var int = 0; int < this.contents.length; int++) {
			var object = this.contents[int];
			var is_selected = this.selected[int];

			html += "<tr id=\"efmlBoardContents" + this.id + "[" + int + "]\">";

			/** the object marker/selector */
			html += "<td id=\"efmlBoardContents" + this.id + "[" + int
					+ "].selected\" style=\"";
			html += "  width: 20px;";
			if (is_selected)
				html += " background: #88FFDD;";
			else
				html += " background: #338866;";
			html += "\">";

			html += "</td>";

			/** the object delete button */
			html += "<td id=\"efmlBoardContents" + this.id + "[" + int
					+ "].cut\" style=\"";
			html += "  width: 20px;";
			if (is_selected)
				html += " background: #88FFDD;";
			else
				html += " background: #338866;";
			html += "\">";
			html += "X";
			html += "</td>";

			/** the object take off runway */
			html += "<td id=\"efmlBoardContents" + this.id + "[" + int
					+ "].takeoff\" style=\"";
			html += "  width: 20px;";
			if (is_selected)
				html += " background: #88FFDD;";
			else
				html += " background: #338866;";
			html += "\">";
			html += "Up";
			html += "</td>";

			/** the object itself */

			html += "<td>";

			html += object.GetHtmlCode();

			html += "</td>";
			html += "</tr>";

			html += "<tr id=\"efmlBoardContents" + this.id + ".Gap["
					+ (int + 1) + "]\" style=\"height: 4px;";
			if (this.cursor == int + 1)
				html += " background: #88FFDD;";
			else
				html += " background: #338866;";
			html += "\">";
			html += "<td style=\"width: 20px;\">";
			html += "</td>";
			html += "<td style=\"width: 20px;\">";
			html += "</td>";
			html += "<td style=\"width: 20px;\">";
			html += "</td>";
			html += "<td>";
			html += "</td>";
			html += "</tr>";
		}

		html += "</table>";

		return html;
	};

	/**
	 * returns html code that represents this board
	 */

	this.GetHtmlCode = function() {
		var html = "<div id=\"efmlBoard" + this.id + "\" ";

		html += " style=\"";
		html += " display: inline-block;";
		html += " background: #338866;";
		if (this.width)
			html += " width: " + this.width + ";";
		if (this.height)
			html += " height: " + this.height + ";";
		html += "\"";
		html += ">";

		/**
		 * control elements
		 */

		for ( var int = 0; int < this.buttons.length; int++) {
			var button = this.buttons[int];
			html += button.GetHtmlCode() + " ";
		}

		html += "<br />";

		html += this.GetContentsHtmlCode();

		html += "</div>";
		return html;
	};

	/**
	 * writes the efml board objects html code
	 */

	this.WriteHtml = function() {
		document.write(this.GetHtmlCode());

		this.RegisterMouse();

		this.html_created = true;
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
		return document.getElementById("efmlBoard" + this.id);
	};

	/**
	 * this function registers the mouse hooks for this object
	 */

	this.RegisterMouse = function() {

		for ( var int = 0; int < this.buttons.length; int++) {
			var button = this.buttons[int];
			button.RegisterMouse();
		}

		for ( var int = 0; int < this.contents.length; int++) {
			var object = this.contents[int];

			object.RegisterMouse();
		}

		this.RegisterCursorHooks();
		this.RegisterSelectionHooks();

		addMouseClickHook("efmlBoard" + this.id, 0, function(me) {
			return function() {
				me.HandleEmptyWorkspaceClick();
			};
		}(this));
	};

	/** default click handler */

	this.HandleEmptyWorkspaceClick = function() {
		if (myHover.flight) {
			this.HandleLanding(this.contents.length);
			return;
		}

		this.HandleCursor(this.contents.length);

	};

	/***************************************************************************
	 * someone requested landing...
	 */

	this.HandleLanding = function(index) {
		if (myHover.flight) {
			var log_data = this.name + ": land plane ";
			/**
			 * check for correct token type
			 */
			var type = myHover.GetType();
			if (this.acceptedTypes.indexOf(type) < 0) {
				myHover.CrashDown();

				myLogger.Log(log_data + " rejected (wrong type: "+type+")");
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
							myLogger.Log(log_data + " rejected (missing tags)");
							return;
						}
					}
				} else {
					myHover.CrashDown();
					myLogger.Log(log_data + " rejected (missing tags)");
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
							myLogger.Log(log_data + " rejected (due to tag)");
							return;
						}
					}
				}
			}

			token = myHover.token;

			myHover.CrashDown(true);

			myLogger.Log(log_data + " accepted");

			this.InsertContents(index, token);
			
			this.UpdateCursorHighlighting();
			this.UpdateSelectionHighlighting();

		}
		
	};

	/***************************************************************************
	 * someone clicked on some selection area
	 */

	this.HandleSelection = function(index) {
		if (myHover.flight) {
			this.HandleLanding(index);
			return;
		}

		if (mouseEvent.ctrlKey) {
			this.selected[index] = !this.selected[index];

		} else
			for ( var int = 0; int < this.selected.length; int++) {
				this.selected[int] = int == index;
			}

		this.UpdateSelectionHighlighting();

		this.cursor = index;
		this.UpdateCursorHighlighting();
	};

	/***************************************************************************
	 * someone clicked on the cut area
	 */

	this.HandleCut = function(index) {

		if (myHover.flight) {
			this.HandleLanding(index);
			return;
		}

		if (mouseEvent.ctrlKey) {
			/**
			 * this is convenient behaviour... :)
			 */
			this.selected[index] = !this.selected[index];

			this.UpdateSelectionHighlighting();

			this.cursor = index;
			this.UpdateCursorHighlighting();
		} else {

			/**
			 * store copy in clip board
			 * 
			 */

			var clip_data = this.contents[index].GetDescription();

			/**
			 * cut out this element
			 */

			var log_data = this.name + ": Cut object: " + index;

			/** filter non-selected content objects */
			var contents = [];
			var selected = [];

			for ( var int = 0; int < this.contents.length; int++) {
				if (index != int) {
					contents.push(this.contents[int]);
					selected.push(this.selected[int]);
				} else {
					if (int < this.cursor) {
						this.cursor -= 1;
					}
				}
			}

			/** unregister mouse handlers */

			this.UnregisterMouse();

			/** remove old html content representation */

			var html_contents = document.getElementById("efmlBoardContents"
					+ this.id);
			var html_parent = html_contents.parentNode;
			html_parent.removeChild(html_contents);

			/** use new contents now */

			this.contents = contents;
			this.selected = selected;

			/** add new html content representation */

			var html = this.GetContentsHtmlCode();

			var container = document.createElement('div');

			container.innerHTML = html;

			html_parent.appendChild(container.firstChild);

			/** register mouse handlers */

			this.RegisterMouse();

			/** update efml token */

			this.token = this.GetEfml();

			/** log */

			myLogger.Log(log_data);

			setClipboardContents(clip_data);
		}

	};

	/***************************************************************************
	 * the hovered token has been accepted by another object
	 */
	this.TakeAway = function() {
		this.hoverIndex = -1;
	};

	/***************************************************************************
	 * the hovered token of this origin returned
	 */

	this.GiveBackToken = function(token) {
		/**
		 * re-insert the hovered object
		 */
		if (this.hoverIndex >= 0) {

			var log_data = this.name + ": Token returns: " + token;

			/** add token back */
			var contents = [];
			var selected = [];

			var contents_length = this.contents.length;

			for ( var int = 0; int < contents_length; int++) {
				if (int == this.hoverIndex) {
					var box = NewEfmlTag(token, this.name + ".contents["
							+ this.content_id + "]", this.tags, this.accept,
							this.reject);

					this.content_id += 1;

					contents.push(box);
					selected.push(this.hoverSelected);
				}

				contents.push(this.contents[int]);
				selected.push(this.selected[int]);
			}

			if (contents_length <= this.hoverIndex) {
				var box = NewEfmlTag(token, this.name + ".contents["
						+ this.content_id + "]", this.tags, this.accept,
						this.reject);

				this.content_id += 1;

				contents.push(box);
				selected.push(this.hoverSelected);
			}

			/**
			 * reset hover index
			 */

			this.hoverIndex = -1;
			
			/**
			 * restore old cursor position
			 */
			
			this.cursor = this.hoverCursorIndex;

			/** unregister mouse handlers */

			this.UnregisterMouse();

			/** remove old html content representation */

			var html_contents = document.getElementById("efmlBoardContents"
					+ this.id);
			var html_parent = html_contents.parentNode;
			html_parent.removeChild(html_contents);

			/** use new contents now */

			this.contents = contents;
			this.selected = selected;

			/** add new html content representation */

			var html = this.GetContentsHtmlCode();

			var container = document.createElement('div');

			container.innerHTML = html;

			html_parent.appendChild(container.firstChild);

			/** register mouse handlers */

			this.RegisterMouse();

			/** update efml token */

			this.token = this.GetEfml();

			/** log */

			myLogger.Log(log_data);

		}

	};

	/***************************************************************************
	 * someone clicked on the take off area
	 */

	this.HandleTakeOff = function(index) {
		if (myHover.flight) {
			this.HandleLanding(index);
			return;
		}

		if (mouseEvent.ctrlKey) {
			/**
			 * this is convenient behaviour... :)
			 */
			this.selected[index] = !this.selected[index];

			this.UpdateSelectionHighlighting();

			this.cursor = index;
			this.UpdateCursorHighlighting();
		} else {
			/**
			 * take off this element
			 */

			var token = this.contents[index].GetDescription();
			var plane = this.contents[index].GetPlaneHtmlCode();

			this.hoverIndex = index;
			this.hoverSelected = this.selected[index];
			this.hoverCursorIndex = this.cursor;

			if (myHover.TakeOff(token, this, null, plane, "EfmlTag")) {
				/**
				 * cut out this element
				 */

				var log_data = this.name + ": Take off object: " + index;

				/** filter non-selected content objects */
				var contents = [];
				var selected = [];

				for ( var int = 0; int < this.contents.length; int++) {
					if (index != int) {
						contents.push(this.contents[int]);
						selected.push(this.selected[int]);
					} else {
						if (int < this.cursor) {
							this.cursor -= 1;
						}
					}
				}

				/** unregister mouse handlers */

				this.UnregisterMouse();

				/** remove old html content representation */

				var html_contents = document.getElementById("efmlBoardContents"
						+ this.id);
				var html_parent = html_contents.parentNode;
				html_parent.removeChild(html_contents);

				/** use new contents now */

				this.contents = contents;
				this.selected = selected;

				/** add new html content representation */

				var html = this.GetContentsHtmlCode();

				var container = document.createElement('div');

				container.innerHTML = html;

				html_parent.appendChild(container.firstChild);

				/** register mouse handlers */

				this.RegisterMouse();

				/** update efml token */

				this.token = this.GetEfml();

				/** log */

				myLogger.Log(log_data);
				
				this.UpdateCursorHighlighting();
				this.UpdateSelectionHighlighting();
			}

		}

	};

	/***************************************************************************
	 * someone clicked on some cursor gap
	 */

	this.HandleCursor = function(before) {
		this.cursor = before;

		this.UpdateCursorHighlighting();

	};

	/** this function registers the cursor mouse hooks */

	this.RegisterCursorHooks = function() {

		for ( var int = 0; int < this.contents.length + 1; int++) {
			addMouseClickHook("efmlBoardContents" + this.id + ".Gap[" + int
					+ "]", 0, function(me, number) {
				return function() {
					me.HandleCursor(number);
				};
			}(this, int));
		}

	};

	/** this function registers the selection mouse hooks */

	this.RegisterSelectionHooks = function() {

		for ( var int = 0; int < this.contents.length; int++) {
			addMouseClickHook("efmlBoardContents" + this.id + "[" + int
					+ "].selected", 0, function(me, number) {
				return function() {
					me.HandleSelection(number);
				};
			}(this, int));
			addMouseClickHook("efmlBoardContents" + this.id + "[" + int
					+ "].takeoff", 0, function(me, number) {
				return function() {
					me.HandleTakeOff(number);
				};
			}(this, int));
			addMouseClickHook("efmlBoardContents" + this.id + "[" + int
					+ "].cut", 0, function(me, number) {
				return function() {
					me.HandleCut(number);
				};
			}(this, int));
		}

	};

	/** this function unregisters the cursor mouse hooks */

	this.UnregisterCursorHooks = function() {

		for ( var int = 0; int < this.contents.length + 1; int++) {
			clearMouseClickHooks("efmlBoardContents" + this.id + ".Gap[" + int
					+ "]");
		}

	};

	/** this function unregisters the selection mouse hooks */

	this.UnregisterSelectionHooks = function() {

		for ( var int = 0; int < this.contents.length; int++) {
			clearMouseClickHooks("efmlBoardContents" + this.id + "[" + int
					+ "].selected");
			clearMouseClickHooks("efmlBoardContents" + this.id + "[" + int
					+ "].cut");
			clearMouseClickHooks("efmlBoardContents" + this.id + "[" + int
					+ "].takeoff");
		}

	};

	/**
	 * this function removes the mouse hooks for this object
	 */

	this.UnregisterMouse = function() {

		for ( var int = 0; int < this.buttons.length; int++) {
			var button = this.buttons[int];
			button.UnregisterMouse();
		}

		for ( var int = 0; int < this.contents.length; int++) {
			var object = this.contents[int];

			object.UnregisterMouse();
		}

		this.UnregisterCursorHooks();
		this.UnregisterSelectionHooks();

		var elt = this.GetElement();

		clearMouseClickHooks("efmlBoard" + this.id);
		clearMouseClickHooks(elt);

		clearMouseDownHooks("efmlBoard" + this.id);
		clearMouseDownHooks(elt);

		clearMouseUpHooks("efmlBoard" + this.id);
		clearMouseUpHooks(elt);
	};

	/**
	 * copy selected elements to clip board
	 */

	this.Copy = function() {
		var data = "";

		for ( var int = 0; int < this.contents.length; int++) {
			var block = this.contents[int];

			if (this.selected[int]) {

				if (data)
					data += "\n";

				data += block.GetDescription();
			}
		}

		setClipboardContents(data);
	};

	/**
	 * cut selected elements to clip board
	 */

	this.Cut = function() {

		var log_data = this.name + ": Cut objects: ";

		this.Copy();

		/** filter non-selected content objects */
		var contents = [];
		var selected = [];

		for ( var int = 0; int < this.contents.length; int++) {
			if (!this.selected[int]) {
				contents.push(this.contents[int]);
				selected.push(false);
			} else {
				log_data += " " + int;
				if (int < this.cursor) {
					this.cursor -= 1;
				}
			}
		}

		/** unregister mouse handlers */

		this.UnregisterMouse();

		/** remove old html content representation */

		var html_contents = document.getElementById("efmlBoardContents"
				+ this.id);
		var html_parent = html_contents.parentNode;
		html_parent.removeChild(html_contents);

		/** use new contents now */

		this.contents = contents;
		this.selected = selected;

		/** add new html content representation */

		var html = this.GetContentsHtmlCode();

		var container = document.createElement('div');

		container.innerHTML = html;

		html_parent.appendChild(container.firstChild);

		/** register mouse handlers */

		this.RegisterMouse();

		/** update efml token */

		this.token = this.GetEfml();

		/** log */

		myLogger.Log(log_data);

	};

	/**
	 * paste clip board elements at cursor position
	 */

	this.Paste = function() {
		var data = getClipboardContents();

		this.InsertContents(this.cursor, data);
	};

	/**
	 * import elements (from file)
	 */

	this.Import = function() {
		// TODO
	};

	/**
	 * export selected elements (to file)
	 */

	this.Export = function() {
		// TODO
	};

	/**
	 * @returns efml code of this tag
	 */

	this.GetEfml = function() {
		var data = "";

		for ( var int = 0; int < this.contents.length; int++) {
			var block = this.contents[int];

			if (data)
				data += "\n";

			data += block.GetEfml();
		}

		return data;
	};

	/**
	 * @returns string that can be used to factor another instance of this
	 *          object
	 */

	this.GetDescription = function() {
		var data = "";

		if (this.hoverIndex < 0) {

			for ( var int = 0; int < this.contents.length; int++) {
				var block = this.contents[int];

				if (data)
					data += "\n";

				data += block.GetDescription();
			}
		} else {
			/**
			 * take the hovered object into account
			 */
			for ( var int = 0; int < this.contents.length; int++) {
				var block = this.contents[int];

				if (data)
					data += "\n";

				if (int == this.hoverIndex) {
					data += myHover.token + "\n";
				}

				data += block.GetDescription();
			}

			if (this.hoverIndex >= this.contents.length) {
				data += "\n" + myHover.token;
			}
		}

		return "EfmlBoard " + escapeBTNR(data);
	};

	/**
	 * return the current contents as string
	 */
	this.GetValue = function() {
		return this.GetDescription().substr(10);
	};

	/**
	 * restore from string
	 */

	this.SetValue = function(contents) {

		/** unregister mouse handlers */

		this.UnregisterMouse();

		/** remove old html content representation */

		var html_contents = document.getElementById("efmlBoardContents"
				+ this.id);
		var html_parent = html_contents.parentNode;
		html_parent.removeChild(html_contents);

		/** use new contents now */

		var description = unescapeBTNR(contents);
		this.SetContents(description);

		/** add new html content representation */

		var html = this.GetContentsHtmlCode();

		var container = document.createElement('div');

		container.innerHTML = html;

		html_parent.appendChild(container.firstChild);

		/** register mouse handlers */

		this.RegisterMouse();

	};

	/**
	 * this function is called on each myStorage.StoreIn run
	 */
	this.Tick = function() {
		this.token = this.GetEfml();
		this.UpdateVolatile();
	};

	efmlBoardArray[this.id] = this;

	/**
	 * if we use this control as part of another control we may want to be able
	 * to override the save method and tag stuff
	 */

	if (!embeddedMode) {
		myTags.Add(this, this.tags);

		myStorage.RegisterField(this, "efmlBoardArray[" + this.id + "]");

	}

	return this;
}