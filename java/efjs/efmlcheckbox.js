/**
 * efmlcheckbox.js, (c) 2013, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
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

function EfmlCheckBox(name, description, tags, accept, reject) {
	EfmlTagConstructor(this);

	this.tags = tags;

	this.name = name;

	/**
	 * the child objects properties and default values
	 */

	this.properties = [ "name", "tags", "label", "checked", "unchecked",
			"defaultstatus" ];
	this.values = [ name, "", "label", "X", "", "unchecked" ];

	/**
	 * these properties go directly to the tag attributes, if they differ from
	 * defaults
	 */
	this.attributes = [ "name", "tags", "checked", "unchecked", "defaultstatus" ];
	this.defaults = [ "", "", "X", "", "unchecked" ];

	/**
	 * whether the property is locked and may not be edited anymore
	 */

	this.locked = [];
	for ( var int = 0; int < this.properties.length; ++int) {
		this.locked.push(false);
	}

	/**
	 * read the description and update the values
	 */

	descriptionToProperties(unescapeBTNR(description), this.properties,
			this.values, this.locked);

	/**
	 * @param property
	 * @returns value
	 */
	this.Get = function(property) {
		var idx = this.properties.indexOf(property);
		if (idx < 0)
			return "!!PROPERTY NOT FOUND!!";
		return this.values[idx];
	};

	/**
	 * @param property
	 * @returns true, if the property has the default value
	 */
	this.IsDefault = function(property) {
		var idx = this.properties.indexOf(property);
		if (idx < 0)
			return true;
		return this.values[idx] == this.defaults[idx];
	};

	/**
	 * @param property
	 * @returns whether the property is locked
	 */
	this.Locked = function(property) {
		var idx = this.properties.indexOf(property);
		if (idx < 0)
			return true;
		return this.locked[idx];
	};

	/**
	 * @returns efml code of this tag
	 */

	this.GetEfml = function() {
		var efmlcode = "<checkbox";
		for ( var int = 0; int < this.attributes.length; int++) {
			var p = this.attributes[int];
			var v = this.Get(p);
			if (v != this.defaults[int]) {
				efmlcode += " " + p + "=\"" + v + "\"";
			}
		}

		efmlcode += ">";
		efmlcode += escapeSome(this.Get("label"));
		efmlcode += "</checkbox>";
		return efmlcode;
	};

	/**
	 * @returns string that can be used to factor another instance of this
	 *          object
	 */

	this.GetDescription = function() {
		return "EfmlCheckBox "
				+ escapeBTNR(propertiesToDescription(this.properties,
						this.values, this.locked));
	};

	/**
	 * @returns control html code
	 */

	this.GetHtmlCode = function() {
		var html = "";

		html += "<div style=\"";
		// html += " display: inline-block;";
		html += " background: #FFFFFF;";
		html += " font-family: 'Courier New', Courier, monospace;";
		html += " color: #0000FF;";
		html += "\" id=\"efmlCheckBox" + this.id + "\">";
		html += "<span style=\"color: #222222;";
		html += " background: #DDDDDD;";
		html += " font-family: 'Times New Roman', Times, serif;";
		html += " font-size: 70%;";
		html += "\">";

		html += "checkbox";

		if (this.Locked("checked")) {
			html += " [x]='" + this.Get("checked") + "'";
		} else {
			html += " [x]='"
					+ this.Get("checked")
					+ "' <span style=\" cursor:default;background:#DDDDFF; color: #3333FF\" id=\"efmlCheckBox"
					+ this.id + ".edit[x]\">edit</span>";
		}

		if (this.Locked("unchecked")) {
			html += " [&nbsp;]='" + this.Get("unchecked") + "'";
		} else {
			html += " [&nbsp;]='"
					+ this.Get("unchecked")
					+ "' <span style=\" cursor:default;background:#DDDDFF; color: #3333FF\" id=\"efmlCheckBox"
					+ this.id + ".edit[]\">edit</span>";
		}

		html += "</span>";
		html += "<br/>";

		if (this.Locked("defaultstatus")) {

			if (this.Get("defaultstatus") == "checked") {
				html += "[x] ";
			} else {
				html += "[&nbsp;] ";
			}

		} else {

		}

		if (this.Locked("label")) {

		} else {
			html += escapeSome(this.Get("label"));
		}

		html += "</div>";

		return html;
	};

	/**
	 * @returns plane html code, i.e. the code for the object that is show when
	 *          dragging this tag around with the hover feature
	 */

	this.GetPlaneHtmlCode = function() {
		var html = "<div style=\"";
		// html += " display: inline-block;";
		html += " background: #FFFFFF;";
		html += " font-family: 'Courier New', Courier, monospace;";
		html += " color: #0000FF;";
		html += " width: 400px";
		html += "\">";
		html += "<span style=\"color: #222222;";
		html += " background: #DDDDDD;";
		html += " font-family: 'Times New Roman', Times, serif;";
		html += " font-size: 70%;";
		html += "\">";

		html += "checkbox";

		html += "</span>";
		html += "<br/>";

		if (this.Get("defaultstatus") == "checked") {
			html += "[x] ";
		} else {
			html += "[&nbsp;] ";
		}

		html += escapeSome(this.Get("label"));

		html += "</div>";

		return html;
	};
	
	/**
	 * this function handles editing of the checked attribute
	 */
	this.EditChecked = function(){
		alert("a");
	};
	
	/**
	 * this function handles editing of the unchecked attribute
	 */
	this.EditUnchecked = function(){
		
	};
	
	/**
	 * this function updates the displayed html object, if found,
	 * to match the current check box state
	 */
	
	this.UpdateHtml = function(){
		var elt =  document.getElementById("efmlCheckBox"+ this.id);
		
		if (elt) {
			var parent = elt.parentNode;
			parent.removeChild(elt);
			
			var container = document.createElement('div');

			container.innerHTML = this.GetHtmlCode();

			parent.appendChild(container.firstChild);
		}
		
	};

	/**
	 * this function registers the mouse hooks for this object
	 */

	this.RegisterMouse = function() {
		addMouseClickHook("efmlCheckBox" + this.id + ".edit[x]", 0,
				function(me) {
					return function() {
						me.EditChecked();
					};
				}(this));
		addMouseClickHook("efmlCheckBox" + this.id + ".edit[]", 0,
				function(me) {
					return function() {
						me.EditUnchecked();
					};
				}(this));

	};

	/**
	 * this function removes the mouse hooks for this object
	 */

	this.UnregisterMouse = function() {
		clearMouseClickHooks("efmlCheckBox" + this.id + ".edit[]");
		clearMouseClickHooks("efmlCheckBox" + this.id + ".edit[x]");
	};

	return this;
};
