/**
 * efml.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

/**
 * load the efml compiler applet
 */

myEfmlPreviewButtonId = 0;
myEfmlPreviewButtons = [];

/**
 * 
 * @param text
 * @returns
 */

function EfmlPreviewButton(atags, rtags, text) {

	this.id = myEfmlPreviewButtonId++;

	this.acceptTags = atags;
	this.rejectTags = rtags;

	if (text) {
		this.text = text;
	} else {
		this.text = "Compile &amp; Preview";
	}

	this.WriteHtml = function() {
		document.write("<form target=\"_previewframe" + this.id
				+ "\" action=\"" + logletBaseURL
				+ "compile?preview=1\" method=\"post\" id=\"efmlInputForm"
				+ this.id + "\" onsubmit=\"javascript:myEfmlPreviewButtons["
				+ this.id + "].UpdateContents()\">");
		document.write("<input type=\"hidden\" name=\"efml\" id=\"efmlDoc"
				+ this.id + "\" />");
		document.write("<input type=\"submit\" value=\"" + this.text + "\"/>");
		document.write("</form>");

	};

	/**
	 * set the contents that will be sent to the html body reflecting bounce
	 * script
	 */

	this.SetContents = function(data) {
		var target = document.getElementById("efmlDoc" + this.id);
		target.value = data;
	};

	/**
	 * submits the compile form
	 */

	this.Bounce = function(data) {
		var form = document.getElementById("efmlInputForm" + this.id);

		/**
		 * NOTE: most pop up blockers will fail this !!
		 */
		form.submit();
	};

	/**
	 * this function is called when the Preview & Compile button have been hit
	 * 
	 * @returns false, if compilation failed
	 */

	this.UpdateContents = function() {

		myLogger.Log("EfmlPreviewButton" + this.id + " click");

		/**
		 * update / save user input values
		 */

		myStorage.AutoUpdateAndStore();

		myLogger.Log("EfmlPreviewButton" + this.id + " store");

		/**
		 * grab efml data
		 */

		myLogger.Log("EfmlPreviewButton" + this.id + " tags");

		var efml_parts = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		myLogger.Log("EfmlPreviewButton" + this.id + " gathering");

		var efml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<efml>\n";

		for ( var int = 0; int < efml_parts.length; int++) {
			var part = efml_parts[int];

			if (part.token)
				efml += part.token + "\n";
		}

		efml += "</efml>";

		myLogger.Log("EfmlPreviewButton" + this.id + " compile preview:\n"
				+ efml);

		/**
		 * set efml data accordingly
		 */

		this.SetContents(efml);

		/**
		 * compliation of preview code is done by servlet on tomcat7
		 */

		return true;
	};

	myEfmlPreviewButtons[this.id] = this;

	return this;
}

/**
 * 
 *  Fixed Efml Code Objects 
 * 
 */

myFixedEfmlCounter = 0;
myFixedEfmlArray = [];

function EfmlFixedQuote(tags, efml) {

	this.id = myFixedEfmlCounter++;

	this.tags = tags;

	this.token = efml;

	/**
	 * we keep the function, but it wont do anything.
	 */
	
	this.WriteHtml = function() {		
	};
	
	myTags.Add(this, this.tags);

	myFixedEfmlArray[this.id] = this;

	return this;
}

