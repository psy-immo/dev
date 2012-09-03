/**
 * crosscompat.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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
 * some old versions of IE do not support "  ax ".trim();
 */

if (typeof String.prototype.trim !== 'function') {
  String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, ''); 
  };
}

/**
 * browser detection tweak
 */

Prototype.Browser.IE6 = Prototype.Browser.IE && parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5)) == 6;
Prototype.Browser.IE7 = Prototype.Browser.IE && parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5)) == 7;
Prototype.Browser.IE8 = Prototype.Browser.IE && parseInt(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE")+5)) == 8;
Prototype.Browser.IE9 = Prototype.Browser.IE && !Prototype.Browser.IE6 && !Prototype.Browser.IE7 && !Prototype.Browser.IE8;

/**
 * deselect all text
 */

if (Prototype.Browser.IE6 || Prototype.Browser.IE7 || Prototype.Browser.IE8) {
	DeselectAllText = function() {
		document.selection.empty();
	};
} else {
	DeselectAllText = function() {
		window.getSelection().removeAllRanges();
	};
}

/**
 * return browser information string
 */

function BrowserName() {
	var b = Prototype.Browser;
	
	if (b.IE6)
		return "IE6";
	if (b.IE7)
		return "IE7";
	if (b.IE8)
		return "IE8";
	if (b.IE9)
		return "IE9";
	if (b.IE)
		return "IE?";
	if (b.Gecko)
		return "Gecko";
	if (b.MobileSafari)
		return "MobileSafari";
	if (b.Opera)
		return "Opera";
	if (b.WebKit)
		return "WebKit";
	
	return "UnknownBrowser";
};