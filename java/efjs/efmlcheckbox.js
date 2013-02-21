/**
 * efmlcheckbox.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

function EfmlCheckBox(name, description, tags, accept, reject) {
	EfmlTagConstructor(this);

	this.tags = tags;
	
	this.name = name;
	this.description = unescapeBTNR(description);
	
	/**
	 * the child objects properties and default values
	 */
	
	this.properties = ["name","tags","label","tokenChecked","tokenUnchecked","checked"];
	this.values = [name,"","",null,null,false];

	/**
	 * whether the property is locked and may not be edited anymore
	 */
	
	this.locked = [];
	for (var int=0;int<this.properties.length;++int) {
		this.locked.push(false);
	}
	
	/**
	 * read the description and update the values
	 */
	
	descriptionToProperties(description, this.properties, this.values, this.locked);
	

	/**
	 * @returns efml code of this tag
	 */

	this.GetEfml = function() {
		return this.efmlcode;
	};

	/**
	 * @returns string that can be used to factor another instance of this
	 *          object
	 */

	this.GetDescription = function() {
		return "EfmlCheckBox " + escapeBTNR(this.description);
	};

	/**
	 * @returns control html code
	 */

	this.GetHtmlCode = function() {
		var html = "<div style=\"";
		// html += " display: inline-block;";
		html += " background: #FFFFFF;";
		html += " font-family: 'Courier New', Courier, monospace;";
		html += " color: #0000FF;";
		html += "\">";
		html += "<span style=\"color: #222222;";
		html += " background: #DDDDDD;";
		html += " font-family: 'Times New Roman', Times, serif;";
		html += " font-size: 70%;";
		html += "\">";

		html += "checkbox";

		html += "</span>";
		html += "<br/>";

		html += escapeSome(this.efmlcode);

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

		if ((""+this.efmlcode).length > 80)
			html += escapeSome(this.efmlcode.substr(0,79)+"...");
		else
			html += escapeSome(this.efmlcode);

		html += "</div>";

		return html;
	};

	return this;
};
