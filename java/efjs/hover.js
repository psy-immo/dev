/**
 * hover.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

/** TODO: customize this to be able to use several hover objects in a single file 
 *          & make it easier to attach to html objects */

/**
 * creates a Hover object which provides functionality of letting objects fly
 * under the mouse cursor
 */
function Hover() {
	this.flight = false;
	this.token = null;
	this.source = null;
	this.denyTakeOff = false;
	this.respawn = null;
	this.dontGiveBack = false;

	this.hover = null;

	/**
	 * @returns true, if token has been taken off
	 */
	this.TakeOff = function(token, source, respawn, planeHtml) {
		if (this.denyTakeOff == true) {
			return false;
		}
		this.token = token;
		this.respawn = respawn;
		this.source = source;
		this.dontGiveBack = false;

		var formatted_token = "";

		if (planeHtml) {
			formatted_token = planeHtml;
		} else {
			formatted_token = token.replace(/\n/g, "<br/>");
		}

		/**
		 * legacy code
		 */
		this.flight = 2;

		/*
		 * { var pl = document.getElementById("myhoverplane"); pl.style.zIndex =
		 * 100; pl.innerHTML = formatted_token; }
		 */

		this.hover = new HoverContainer(mouseX + 1, mouseY + 1, formatted_token);
		this.hover.AddChild();

		document.getElementById("body").style.cursor = "move";
		addMouseMoveHook(this.lMovePlane);

		return true;
	};

	this.lMovePlane = function() {
		var pl = myHover.hover.GetElement().style;

		pl.left = (mouseX + 1) + "px";
		pl.top = (mouseY + 1) + "px";
	};

	
	/**
	 * function that handles CrashDown by clicking somewhere
	 */
	this.CrashDown = function(dontGiveBack) {
		if (this.flight > 0) {
			this.LegacyCrashDown();
		}
		
		if (dontGiveBack)
			return;
		
		if (this.dontGiveBack == false) {
			if (this.source) {
				if (this.source.GiveBackToken) {
					this.source.GiveBackToken(this.token);
				}
			}
		}

	};

	/**
	 * legacy code
	 */
	this.LegacyCrashDown = function() {
		this.flight = 0;

		document.getElementById("body").style.cursor = "";
		delMouseMoveHook(this.lMovePlane);

		this.hover.Remove();

	};

	/**
	 * denies further take-offs
	 */
	this.DenyTakeOff = function() {
		this.denyTakeOff = true;
	};

	/**
	 * returns the source of the token, if there is a token hovering around.
	 * Otherwise, returns null
	 */
	this.GetSourceIfFlying = function() {
		if (this.flight > 0) {
			return this.source;
		}
		return null;
	};

	/**
	 * writes the HTML code that provides the layers used for flights
	 */
	this.WriteHtml = function() {

//		document
//				.write("<div id=\"myhoverplane\" style=\"position:absolute; z-index: 100;"
//						+ " top: -220px; left:-220; padding:3px;background-color:#DE6B00;\""
//						+ " onmousemove=\"if ( document.all ) myHover.MovePlane()\"></div>");

	};

}

/**
 * generate the myHover object
 */
myHover = new Hover();
addMouseClickHook(document, 0, function() {
	myHover.CrashDown();
});
