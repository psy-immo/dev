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

	this.isFive = (navigator.appVersion.indexOf("MSIE 4") == -1) ? 1 : 0;

	/**
	 * @returns true, if token has been taken off
	 */
	this.TakeOff = function(token, source, respawn) {
		if (this.denyTakeOff == true) {
			return false;
		}
		this.token = token;
		this.respawn = respawn;
		this.source = source;
		this.dontGiveBack = false;

		/**
		 * legacy code
		 */
		this.flight = 2;
		var formatted_token = '<FONT face="Arial" size="3" color="#FFD600"><I><B>'
				+ token + '</B></I></FONT>';
		if (document.layers) {
			var doc = document.lplane.document;
			doc.open();
			doc.clear();
			doc.write('<TABLE border=0 cellpadding=3><TR><TD>'
					+ formatted_token + '</TD></TR></TABLE>');
			doc.close();
			document.lplane.visibility = "show";
			window.captureEvents(Event.MOUSEMOVE);
			window.onmousemove = this.lMovePlane;
			window.captureEvents(Event.MOUSEDOWN);
			window.onmousedown = this.OnFlight;
		} else if (document.all) {
			document.all.plane1.style.zIndex = 3;
			document.all.plane2.innerHTML = formatted_token;

			if (source.style) {
				this.sourceColor = source.style.backgroundColor;
				source.style.backgroundColor = "#FFD600";
			}

			document.all.frame.style.cursor = "crosshair";
		} else // DOM
		{
			var pl = document.getElementById("plane");
			pl.style.zIndex = 3;
			pl.innerHTML = formatted_token;

			if (source.style) {
				this.sourceColor = source.style.backgroundColor;
				source.style.backgroundColor = "#FFD600";
			}

			document.getElementById("body").style.cursor = "crosshair";
			window.onmousemove = this.lMovePlane;
		}

		return true;
	};

	/**
	 * legacy code
	 */
	this.MovePlane = function() {
		var pl = document.all.plane1.style;
		if (!document.all) {
			pl.left = window.event.clientX;
			pl.top = window.event.clientY;
		} else if (this.isFive == 1) {
			pl.left = window.event.x + document.body.scrollLeft + 1;
			pl.top = window.event.y + document.body.scrollTop + 1;
		} else {
			pl.left = window.event.x + 1;
			pl.top = window.event.y + 1;
		}
	};

	/**
	 * legacy code
	 */
	this.lMovePlane = function(ev) {

		var pl = document.layers ? document.lplane : document
				.getElementById("plane").style;

		pl.left = (ev.pageX + 1) + "px";
		pl.top = (ev.pageY + 1) + "px";
	};

	/**
	 * legacy code
	 */

	this.OnFlight = function() {
		if (this.flight == 2)
			this.flight = 1;
		else if (this.flight == 1)
			this.CrashDown();
	};

	/**
	 * function that handles CrashDown by clicking somewhere
	 */
	this.CrashDown = function() {
		this.LegacyCrashDown();
		if (this.dontGiveBack == false) {

			if (this.source.GiveBackToken) {
				this.source.GiveBackToken(this.token);
			}
		}

	};

	/**
	 * legacy code
	 */
	this.LegacyCrashDown = function() {
		this.flight = 0;
		if (document.layers) {
			document.lplane.visibility = "hide";
			window.onmousemove = 0;
			window.releaseEvents(Event.MOUSEMOVE);
			window.onmousedown = 0;
			window.releaseEvents(Event.MOUSEDOWN);
		} else {
			var pl = document.all ? document.all.plane1 : document
					.getElementById("plane");
			pl.style.left = -220 + "px";
			pl.style.top = -220 + "px";
			if (this.source.style)
				this.source.style.backgroundColor = this.sourceColor;
			if (document.all)
				document.all.frame.style.cursor = "";
			else {
				document.getElementById("body").style.cursor = "";
				window.onmousemove = 0;
			}
		}

	};
	
	/**
	 * denies further take-offs
	 */
	this.DenyTakeOff = function() {
		this.denyTakeOff = true;
	};
	
	/**
	 * writes the HTML code that provides the layers used for flights
	 */
	this.WriteHtml = function() {
		
		document.write("<layer name=\"lplane\" top=\"4\" left=\"4\" visibility=\"hide\" bgcolor=\"#DE6B00\"></layer><div id=\"plane\" style=\"position:absolute; top: -220px; left:-220; padding:3px;background-color:#DE6B00;\" onmousemove=\"if ( document.all ) myHover.MovePlane()\"></div><div id=\"plane1\" style=\"position:absolute; top: -220px; left:-220; padding:0;\" onmousemove=\"if ( document.all ) myHover.MovePlane()\"><table style=\"padding:3px;background-color:#DE6B00;\"><tr><td id=\"plane2\">");		
	};

}

/**
 * generate the myHover object
 */
myHover = new Hover();