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

	//this.isFive = (navigator.appVersion.indexOf("MSIE 4") == -1) ? 1 : 0;

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
			formatted_token = '<FONT face="Arial" size="3" color="#FFD6AA"><B>'
				+ token.replace(/\n/g,"<br/>") + '</B></FONT>';
		}

		/**
		 * legacy code
		 */
		this.flight = 2;
		
//		if (document.layers) {
//			var doc = document.myhoverlplane.document;
//			doc.open();
//			doc.clear();
//			doc.write('<TABLE border=0 cellpadding=3><TR><TD>'
//					+ formatted_token + '</TD></TR></TABLE>');
//			doc.close();
//			document.myhoverlplane.visibility = "show";
//			window.captureEvents(Event.MOUSEMOVE);
//			window.onmousemove = this.lMovePlane;
//			window.captureEvents(Event.MOUSEDOWN);
//			window.onmousedown = this.OnFlight;
//		} else if (document.all) {
//			document.all.myhoverplane1.style.zIndex = 3;
//			document.all.myhoverplane2.innerHTML = formatted_token;
//
//			if (source.style) {
//				this.sourceColor = source.style.backgroundColor;
//				source.style.backgroundColor = "#FFD600";
//			}
//
//			document.all.myhoverframe.style.cursor = "crosshair";
//		} else // DOM
		{
			var pl = document.getElementById("myhoverplane");
			pl.style.zIndex = 100;
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

//	/**
//	 * legacy code
//	 */
//	this.MovePlane = function() {
//		var pl = document.all.myhoverplane1.style;
//		if (!document.all) {
//			pl.left = window.event.clientX;
//			pl.top = window.event.clientY;
//		} else if (this.isFive == 1) {
//			pl.left = window.event.x + document.body.scrollLeft + 1;
//			pl.top = window.event.y + document.body.scrollTop + 1;
//		} else {
//			pl.left = window.event.x + 1;
//			pl.top = window.event.y + 1;
//		}
//	};

	/**
	 * legacy code
	 */
	this.lMovePlane = function(ev) {

//		var pl = document.layers ? document.myhoverlplane : document
//				.getElementById("myhoverplane").style;

		var pl = document.getElementById("myhoverplane").style;
		
		var posx = 0;
		var posy = 0;
		var e = ev;
		
		if (!ev) 
			e = window.event;
		
		if (e.pageX || e.pageY) 	{
			posx = e.pageX;
			posy = e.pageY;
		}
		else if (e.clientX || e.clientY) 	{
			posx = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
			posy = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
		}

		pl.left = (posx + 1) + "px";
		pl.top = (posy + 1) + "px";
		
		console.log(ev);
		lastEv = ev;
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
		if (this.flight > 0) {
			this.LegacyCrashDown();
		}

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
//		if (document.layers) {
//			document.myhoverlplane.visibility = "hide";
//			window.onmousemove = 0;
//			window.releaseEvents(Event.MOUSEMOVE);
//			window.onmousedown = 0;
//			window.releaseEvents(Event.MOUSEDOWN);
//		} else {
//			var pl = document.all ? document.all.myhoverplane1 : document
//					.getElementById("myhoverplane");
		
			var pl = document.getElementById("myhoverplane");
	
			pl.style.left = -220 + "px";
			pl.style.top = -220 + "px";
			if (this.source.style)
				this.source.style.backgroundColor = this.sourceColor;
//			if (document.all)
//				document.all.myhoverframe.style.cursor = "";
//			else {
				document.getElementById("body").style.cursor = "";
				window.onmousemove = 0;
//			}
//		}

	};

	/**
	 * denies further take-offs
	 */
	this.DenyTakeOff = function() {
		this.denyTakeOff = true;
	};
	
	/**
	 * returns the source of the token, if there is a token hovering around. Otherwise, returns null
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
		
		document
		.write("<div id=\"myhoverplane\" style=\"position:absolute; z-index: 100;"
				+ " top: -220px; left:-220; padding:3px;background-color:#DE6B00;\""
				+ " onmousemove=\"if ( document.all ) myHover.MovePlane()\"></div>");

//		document
//				.write("<layer name=\"myhoverlplane\" top=\"4\" left=\"4\" "
//						+ "visibility=\"hide\" bgcolor=\"#DE6B00\"></layer>"
//						+ "<div id=\"myhoverplane\" style=\"position:absolute;"
//						+ " top: -220px; left:-220; padding:3px;background-color:#DE6B00;\""
//						+ " onmousemove=\"if ( document.all ) myHover.MovePlane()\"></div>"
//						+ "<div id=\"myhoverplane1\" style=\"position:absolute; top: -220px;"
//						+ " left:-220; padding:0;\" onmousemove=\"if ( document.all ) myHover.MovePlane()\">"
//						+ "<table style=\"padding:3px;background-color:#DE6B00;\"><tr>"
//						+ "<td id=\"myhoverplane2\">"
//						+ "</td></tr></table></div>");
	};

//	/**
//	 * code for interoperability with ef editor output files
//	 */
//	this.EfInterOp = function() {
//		if (typeof MovePlane == "function") {
//			ancientMovePlane = MovePlane;
//			MovePlane = function() {
//				myHover.MovePlane();
//				ancientMovePlane();
//			};
//		}
//		if (typeof OnFlight == "function") {
//			ancientOnFlight = OnFlight;
//			OnFlight = function() {
//				myHover.OnFlight();
//				ancientOnFlight();
//			};
//		}
//	};

}

/**
 * generate the myHover object
 */
myHover = new Hover();