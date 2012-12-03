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

myEfmlAppletIncluded = false;
myEfmlPreviewButtonId = 0;
myEfmlPreviewButtons = [];

/**
 * include the global applet
 */

function myEfmlAppletInclude() {
	if (myEfmlAppletIncluded == false) {

		document.write("<applet id=\"efmlApplet\" " + "name=\"efmlApplet\""
				+ " archive=\"" + logletBaseURL + "efmlApplet.jar\" "
				+ "code=\"de.tu_dresden.psy.efml.editor.EditorApplet\" "
				+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");

		myEfmlAppletIncluded = true;
	}
}

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
		myEfmlAppletInclude();
		document.write("<form target=\"_blank\" action=\"" + logletBaseURL
				+ "bounce.php\" method=\"post\" id=\"efmlInputForm" + this.id
				+ "\" onsubmit=\"javascript:myEfmlPreviewButtons[" + this.id
				+ "].UpdateContents()\">");
		document.write("<input type=\"hidden\" name=\"doc\" id=\"efmlDoc"
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
		target.value = escapeSome(data);
	};

	/**
	 * submits the bouncer form
	 */

	this.Bounce = function(data) {
		var form = document.getElementById("efmlInputForm" + this.id);

		/**
		 * NOTE: most popup blockers will fail this !!
		 */
		form.submit();
	};

	/**
	 * this function is called when the Preview & Compile button have been hit
	 * 
	 * @returns false, if compilation failed
	 */

	this.UpdateContents = function() {
		
		
		/**
		 * update / save user input values
		 */
		
		myStorage.AutoUpdateAndStore();
		

		/**
		 * ..and now preview & compile
		 */
		
		var applet = document.getElementById("efmlApplet");

		/**
		 * grab efml data
		 */
		
		var efml_parts = myTags.AllTagsBut(this.acceptTags, this.rejectTags);

		var efml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+				"<efml>\n";

		for ( var int = 0; int < efml_parts.length; int++) {
			var part = efml_parts[int];

			efml += part.token + "\n";
		}
		
		efml += "</efml>";

		myLogger.Log("EfmlPreviewButton" + this.id + " compile:\n" + efml);

		/**
		 * compile
		 */
		
		try {
			this.lastError = applet.compileEfml(bugfixParam(efml));
		} catch (err) {
			this.lastError = "Error invoking compilation applet: "+err;
		}

		if (this.lastError) {
			myLogger.Log("EfmlPreviewButton" + this.id + " error:\n"
					+ this.lastError);
			return false;
		} else {
			myLogger.Log("EfmlPreviewButton" + this.id + " bounces.");
		}

		/**
		 * set bounce contents
		 */

		this.SetContents(applet.getHtml());

		return true;
	};

	myEfmlPreviewButtons[this.id] = this;

	return this;
}
