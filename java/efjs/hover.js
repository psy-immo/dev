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
	this.tokenType = null;

	this.hover = null;

	/**
	 * @returns true, if token has been taken off
	 */
	this.TakeOff = function(token, source, respawn, planeHtml, tokenType) {
		if (this.denyTakeOff == true) {
			return false;
		}
		this.token = token;
		if (tokenType)
			this.tokenType = tokenType;
		else
			this.tokenType = "text";
		
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

		pl.left = (mouseX + 5) + "px";
		pl.top = (mouseY + 3) + "px";
	};

	
	/**
	 * function that handles CrashDown by clicking somewhere
	 */
	this.CrashDown = function(dontGiveBack) {
		if (this.flight > 0) {
			this.LegacyCrashDown();
		}

		if ((this.dontGiveBack == false)&&(!dontGiveBack)) {
			if (this.source) {
				if (this.source.GiveBackToken) {
					this.source.GiveBackToken(this.token);
				}
			}
		}
		
		this.tokenType = null;

	};
	
	/**
	 * returns the type of the current token
	 */
	
	this.GetType = function(){
		return this.tokenType;
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
		/** nothing more to do here ... */
	};

}

/**
 * generate the myHover object
 */
myHover = new Hover();
addMouseClickHook(document, 0, function() {
	myHover.CrashDown();
});

/* The Hover interface allows data to be taken up and put down in the
 * document. There are two objects governing this process, the source
 * and the target.
 * 
 * The OnClick handler routine of a take-off capable object checks for
 * other flying objects by checking myHover.flight :
 *    If there is some object flying, it may be removed by calling
 *      myHover.CrashDown() to cancel the operation or 
 *      myHover.CrashDown(true) to take away the flying object.
 * 
 *
 * 
 */
