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

	this.cursor = 0.5;
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

			this.contents.push(NewEfmlTag(content_block, this.name + "[" + int
					+ "]", this.tags, this.accept, this.reject));
			this.selected.push(false);
		}

		this.cursor = this.contents.length - 0.5;

		this.token = this.GetEfml();

		return this;
	};

	/**
	 * returns the html code for the contents
	 */

	this.GetContentsHtmlCode = function() {
		var html = "";

		html += "<table id=\"efmlBoardContents" + this.id + "\"";
		html += "style=\"";
		html += "\">";

		for ( var int = 0; int < this.contents.length; int++) {
			var object = this.contents[int];

			html += "<tr id=\"efmlBoardContents" + this.id + "[" + int + "]\">";
			html += "<td>";

			html += object.GetHtmlCode();

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
	};

	/**
	 * cut selected elements to clip board
	 */

	this.Cut = function() {
		this.Copy();

		// TODO
	};

	/**
	 * paste clip board elements at cursor position
	 */

	this.Paste = function() {
		// TODO
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

		for ( var int = 0; int < this.contents.length; int++) {
			var block = this.contents[int];

			if (data)
				data += "\n";

			data += block.GetDescription();
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
		var description = unescapeBTNR(contents);

		this.SetContents(description);
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

		/**
		 * we might embed freetext or multiline controls and thus request auto
		 * save ticks
		 */
		myStorage.RequestTicks(this);
	}

	return this;
}