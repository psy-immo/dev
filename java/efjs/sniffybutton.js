/**
 * sniffybutton.js, (c) 2011, Immanuel Albrecht; Dresden University of
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

mySniffyButtonIncludeApplet = true;

myWaitForInstructions = function() {
	for ( var int = 0; int < myInstructionsButtons.length; int++) {
		if (myInstructionsButtons[int].read == false) {
			alert(myInstructionsButtons[int].inform);
			return false;
		}
	}
	return true;
};

mySniffyButtonAction = function() {
	
	if (myWaitForInstructions() != true) {
		return;
	}
	
	var applet = document.getElementById("launchSniffy");
	if (applet) {
		myLogger.Log("Launching Sniffy: " + applet.doLaunchSniffy());

		if (mySniffyButton) {
			mySniffyButton.launched = true;
		}
	} else {
		myLogger.Log("ERROR! sniffyLauncher applet not found!");
	}
};

myInstructionsButtons = [];

myInstructionsButtonAction = function(id) {
	window.open(myInstructionsButtons[id].url);
	myInstructionsButtons[id].read = true;
	myLogger.Log("Viewing instructions: "+myInstructionsButtons[id].url);
};

myWaitForSniffy = function() {

	if (mySniffyButton) {
		if (mySniffyButton.launched == true) {
			return true;
		}
	}
	
	var applet = document.getElementById("launchSniffy");
	if (applet) {
		if (applet.hasLaunchedSniffy()) {
			return true;
		} else {
			if (mySniffyButton) {
				alert(mySniffyButton.inform);
			}
			return false;
		}
	} else {
		myLogger.Log("ERROR! sniffyLauncher applet not found!");
	}
};

/**
 * create a new instructions button that will open a new window with instructions
 * @param url
 * @param text
 * @param inform   message when sniffy button is pressed without reading this instruction
 * @returns
 */

function InstructionsButton(url, text, inform) {

	if (text === undefined) {
		text = "View Instructions";
	}
	
	if (inform === undefined) {
		inform = "Please read all instructions first!";
	}

	this.read = false;
	this.url = url;
	this.id = myInstructionsButtons.length;
	this.inform = inform;

	this.WriteHtml = function() {
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" value=\"" + text
						+ "\" onclick=\"myInstructionsButtonAction(" + this.id
						+ ")\"/>");
		document.write("</form>");
	};

	/**
	 * return the current read state
	 */
	this.GetValue = function() {
		if (this.read) {
			return "read";
		} else {
			return "";
		}
	};

	/**
	 * restore the current read state
	 */
	this.SetValue = function(contents) {
		if (contents == "read") {
			this.read = true;
		} else {
			this.read = false;
		}
	};

	myInstructionsButtons[this.id] = this;

	myStorage.RegisterField(this,"myInstructionsButtons["+this.id+"]");

	return this;
};

/**
 * generate a launch sniffy button
 * @param text
 * @param inform
 * @returns
 */

function SniffyButton(text, inform) {

	if (text === undefined) {
		text = "Launch Sniffy";
	}
	
	if (inform === undefined) {
		inform = "Please use Sniffy first!";
	}

	this.WriteHtml = function() {
		if (mySniffyButtonIncludeApplet) {
			mySniffyButtonIncludeApplet = false;
			document.write("<applet id=\"launchSniffy\" "
					+ "name=\"launchSniffy\""
					+ " archive=\"launcherApplet.jar\" "
					+ "code=\"de.tu_dresden.psy.util.LauncherApplet\" "
					+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");
		}
		document.write("<form onsubmit=\"return false;\">");
		document.write("<input type=\"button\" value=\"" + text
				+ "\" onclick=\"mySniffyButtonAction()\"/>");
		document.write("</form>");
	};
	
	this.inform = inform;

	this.launched = false;

	/**
	 * return the current sniffy state
	 */
	this.GetValue = function() {
		if (this.launched) {
			return "launched";
		} else {
			return "";
		}
	};

	/**
	 * restore the current sniffy state
	 */
	this.SetValue = function(contents) {
		if (contents == "launched") {
			this.launched = true;
		} else {
			this.launched = false;
		}
	};
	
	mySniffyButton = this;

	myStorage.RegisterField(this,"mySniffyButton");

	return this;
};
