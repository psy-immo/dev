/**
 * cssgraphics.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

graphicsIds = 0;
graphicsArray = [];

/**
 * return a fresh id
 */

function freshId() {
	return "graphicElement" + graphicsIds++;
};

/**
 * 
 * 
 * 
 * line primitive
 * 
 * 
 * 
 * 
 */

/**
 * return css styled html code for drawing a line (by using the borders of an
 * space-empty div layer)
 */

function htmlLineDiv(left, top, right, bottom, color, style, id, extension,
		content) {
	if (color == undefined) {
		color = "#000000";
	}

	if (id == undefined) {
		id = freshId();
	}

	if (extension == undefined) {
		extension = "";
	}

	if (content == undefined) {
		content = "";
	}

	var width = Math.sqrt((right - left) * (right - left) + (bottom - top)
			* (bottom - top));
	var angle = Math.atan2(bottom - top, right - left) * 180.0 / Math.PI;

	var html = "<div id=\"" + id + "\" style=\"";

	html += "position: absolute;";
	html += "background: " + color + ";";
	html += "height: 4px;";
	html += "width: " + width + "px;";
	html += "z-index: 2;";

	html += "left: " + left + "px;";
	html += "top: " + top + "px;";

	html += "transform-origin: 0% 50%;";
	html += "-webkit-transform-origin: 0% 50%;";
	html += "-moz-transform-origin: 0% 50%;";
	html += "-ms-transform-origin: 0% 50%;";
	html += "-o-transform-origin: 0% 50%;";

	html += "transform: rotate(" + angle + "deg);";
	html += "-webkit-transform: rotate(" + angle + "deg);";
	html += "-moz-transform: rotate(" + angle + "deg);";
	html += "-ms-transform: rotate(" + angle + "deg);";
	html += "-o-transform: rotate(" + angle + "deg);";

	html += style;

	html += "\" " + extension + ">" + content + "</div>";
	return html;
};

function removeCss(regexp, css) {
	var new_css = "";
	var old = css.split(";");
	for ( var int = 0; int < old.length; ++int) {
		var line = old[int];
		if (!line.match(regexp)) {
			if (line.trim())
				new_css += line + ";";
		}
	}
	return new_css;
}

/**
 * creates a line object
 */
function Line(left, top, right, bottom, color, style, extension) {
	this.id = graphicsIds++;
	this.left = left;
	this.right = right;
	this.top = top;
	this.bottom = bottom;

	this.color = "#888888";
	this.style = "";
	this.extension = "";

	if (color)
		this.color = color;
	if (style)
		this.style = style;
	if (extension)
		this.extension = extension;

	/**
	 * write html code that creates the line object
	 */

	this.WriteHtml = function() {
		document.write(htmlLineDiv(this.left, this.top, this.right,
				this.bottom, this.color, this.style, "grLine" + this.id,
				this.extension));
	};

	/**
	 * remove the graphical object
	 */

	this.Remove = function() {
		var elt = document.getElementById("grLine" + this.id);
		elt.parentNode.removeChild(elt);
		graphicsArray[this.id] = undefined;

		clearMouseClickHooks("grLine" + this.id);
		clearMouseClickHooks(elt);
	};

	/**
	 * add the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "mainframe";
		}
		var html = htmlLineDiv(this.left, this.top, this.right, this.bottom,
				this.color, this.style, "grLine" + this.id, this.extension);

		var parent = document.getElementById(id);

		var container = document.createElement('div');

		container.innerHTML = html;

		parent.appendChild(container.firstChild);
	};

	/**
	 * get the associated html element
	 */

	this.GetElement = function() {
		return document.getElementById("grLine" + this.id);
	};

	/**
	 * change the line coordinates
	 */

	this.SetCoords = function(left, top, right, bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;

		var elt = document.getElementById("grLine" + this.id);
		var parent = elt.parentNode;

		parent.removeChild(elt);

		var html = htmlLineDiv(this.left, this.top, this.right, this.bottom,
				this.color, this.style, "grLine" + this.id, this.extension);

		var container = document.createElement('div');

		container.innerHTML = html;

		var newelt = container.firstChild;

		parent.appendChild(newelt);

		mouseTargetReplace(elt, newelt);
	};

	graphicsArray[this.id] = this;

	return this;
};

/**
 * 
 * 
 * 
 * container primitive
 * 
 * 
 * 
 * 
 */

/**
 * return css styled html code for a (overlaying) div container primitive
 */

function htmlContainerDiv(left, top, right, bottom, layer, style, id,
		extension, content) {

	if (layer == undefined) {
		layer = 3;
	}

	if (id == undefined) {
		id = freshId();
	}

	if (extension == undefined) {
		extension = "";
	}

	if (content == undefined) {
		content = "";
	}

	if (right < left) {
		var tri = right;
		right = left;
		left = tri;
	}

	if (bottom < top) {
		var tri = bottom;
		bottom = top;
		top = tri;
	}

	var html = "<div id=\"" + id + "\" style=\"";

	html += "position: absolute;";

	html += "z-index: " + layer + ";";

	html += "left: " + left + "px;";
	html += "top: " + top + "px;";

	var width = (right - left);
	var height = (bottom - top);

	html += "width: " + width + "px;";
	html += "height: " + height + "px;";
	html += "min-width: " + width + "px;";
	html += "min-height: " + height + "px;";
	html += "max-width: " + width + "px;";
	html += "max-height: " + height + "px;";

	html += style;

	html += "\" " + extension + ">" + content + "</div>";
	return html;
};

function Container(left, top, right, bottom, style, extension, layer, content) {

	this.id = graphicsIds++;
	this.left = left;
	this.right = right;
	this.top = top;
	this.bottom = bottom;

	this.layer = 3;
	this.style = "";
	this.extension = "";
	this.content = "";

	if (layer)
		this.layer = layer;
	if (style)
		this.style = style;
	if (extension)
		this.extension = extension;
	if (content)
		this.content = content;

	/**
	 * write html code that creates the line object
	 */

	this.WriteHtml = function() {
		document.write(htmlContainerDiv(this.left, this.top, this.right,
				this.bottom, this.layer, this.style, "grDiv" + this.id,
				this.extension, this.content));
	};

	/**
	 * remove the graphical object
	 */

	this.Remove = function() {
		var elt = document.getElementById("grDiv" + this.id);
		elt.parentNode.removeChild(elt);
		graphicsArray[this.id] = undefined;

		clearMouseClickHooks("grDiv" + this.id);
		clearMouseClickHooks(elt);
	};

	/**
	 * remove the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "mainframe";
		}
		var html = htmlContainerDiv(this.left, this.top, this.right,
				this.bottom, this.layer, this.style, "grDiv" + this.id,
				this.extension, this.content);

		var parent = document.getElementById(id);

		var container = document.createElement('div');

		container.innerHTML = html;

		parent.appendChild(container.firstChild);
	};

	/**
	 * get the associated html element
	 */

	this.GetElement = function() {
		return document.getElementById("grDiv" + this.id);
	};

	graphicsArray[this.id] = this;

	return this;
};

/**
 * 
 * 
 * 
 * hover primitive
 * 
 * 
 * 
 * 
 */

function htmlHoverDiv(left, top, style, id, extension, content) {

	if (id == undefined) {
		id = freshId();
	}

	if (extension == undefined) {
		extension = "";
	}

	if (content == undefined) {
		content = "";
	}

	var html = "<div id=\"" + id + "\" style=\"";

	html += "position: absolute;";

	html += "z-index: 100;";

	html += "left: " + left + "px;";
	html += "top: " + top + "px;";

	html += "color: #EEEE44;";
	html += "background: #000000;";
	html += "opacity: 0.6;";

	html += style;

	html += "\" " + extension + ">" + content + "</div>";
	return html;
};

function HoverContainer(left, top, content) {

	this.id = graphicsIds++;
	this.left = left;
	this.top = top;

	this.style = "";
	this.extension = "";
	this.content = "";

	if (content)
		this.content = content;

	/**
	 * write html code that creates the line object
	 */

	this.WriteHtml = function() {
		document.write(htmlHoverDiv(this.left, this.top, this.style, "grHover"
				+ this.id, this.extension, this.content));
	};

	/**
	 * remove the graphical object
	 */

	this.Remove = function() {
		var elt = document.getElementById("grHover" + this.id);
		elt.parentNode.removeChild(elt);
		graphicsArray[this.id] = undefined;

		clearMouseClickHooks("grHover" + this.id);
		clearMouseClickHooks(elt);
	};

	/**
	 * remove the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "mainframe";
		}
		var html = htmlHoverDiv(this.left, this.top, this.style, "grHover"
				+ this.id, this.extension, this.content);

		var parent = document.getElementById(id);

		var container = document.createElement('div');

		container.innerHTML = html;

		parent.appendChild(container.firstChild);
	};

	/**
	 * get the associated html element
	 */

	this.GetElement = function() {
		return document.getElementById("grHover" + this.id);
	};

	graphicsArray[this.id] = this;

	return this;
};

/**
 * 
 * 
 * 
 * 
 * arrow primitive
 * 
 * 
 * 
 * 
 * 
 * 
 */

/**
 * creates an arrow object
 */
function Arrow(left, top, right, bottom, color, style, extension) {
	this.id = graphicsIds++;
	this.left = left;
	this.right = right;
	this.top = top;
	this.bottom = bottom;

	var angle = Math.atan2(bottom - top, right - left);
	var tail = 15;
	var rotate = 0.3;

	this.arrowX1 = this.right - Math.cos(angle + rotate) * tail;
	this.arrowY1 = this.bottom - Math.sin(angle + rotate) * tail;
	this.arrowX2 = this.right - Math.cos(angle - rotate) * tail;
	this.arrowY2 = this.bottom - Math.sin(angle - rotate) * tail;

	this.color = "#888888";
	this.style = "";
	this.extension = "";

	if (color)
		this.color = color;
	if (style)
		this.style = style;
	if (extension)
		this.extension = extension;

	/**
	 * write html code that creates the line object
	 */

	this.WriteHtml = function() {
		document.write(htmlLineDiv(this.left, this.top, this.right,
				this.bottom, this.color, this.style, "grArrow0." + this.id,
				this.extension));
		document.write(htmlLineDiv(this.arrowX1, this.arrowY1, this.right,
				this.bottom, this.color, this.style, "grArrow1." + this.id,
				this.extension));
		document.write(htmlLineDiv(this.arrowX2, this.arrowY2, this.right,
				this.bottom, this.color, this.style, "grArrow2." + this.id,
				this.extension));
	};

	/**
	 * remove the graphical object
	 */

	this.Remove = function() {
		{
			var elt = document.getElementById("grArrow0." + this.id);
			elt.parentNode.removeChild(elt);
			graphicsArray[this.id] = undefined;

			clearMouseClickHooks("grArrow0." + this.id);
			clearMouseClickHooks(elt);
		}
		{
			var elt = document.getElementById("grArrow1." + this.id);
			elt.parentNode.removeChild(elt);
			graphicsArray[this.id] = undefined;

			clearMouseClickHooks("grArrow1." + this.id);
			clearMouseClickHooks(elt);
		}
		{
			var elt = document.getElementById("grArrow2." + this.id);
			elt.parentNode.removeChild(elt);
			graphicsArray[this.id] = undefined;

			clearMouseClickHooks("grArrow2." + this.id);
			clearMouseClickHooks(elt);
		}
	};

	/**
	 * add the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "mainframe";
		}

		var parent = document.getElementById(id);

		{
			var html = htmlLineDiv(this.left, this.top, this.right,
					this.bottom, this.color, this.style, "grArrow0." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			parent.appendChild(container.firstChild);
		}

		{
			var html = htmlLineDiv(this.arrowX1, this.arrowY1, this.right,
					this.bottom, this.color, this.style, "grArrow1." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			parent.appendChild(container.firstChild);
		}

		{
			var html = htmlLineDiv(this.arrowX2, this.arrowY2, this.right,
					this.bottom, this.color, this.style, "grArrow2." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			parent.appendChild(container.firstChild);
		}
	};

	/**
	 * get the associated html element
	 */

	this.GetElement = function() {
		return document.getElementById("grArrow0." + this.id);
	};

	/**
	 * change the line coordinates
	 */

	this.SetCoords = function(left, top, right, bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;

		var angle = Math.atan2(bottom - top, right - left);
		var tail = 15;
		var rotate = 0.3;

		this.arrowX1 = this.right - Math.cos(angle + rotate) * tail;
		this.arrowY1 = this.bottom - Math.sin(angle + rotate) * tail;
		this.arrowX2 = this.right - Math.cos(angle - rotate) * tail;
		this.arrowY2 = this.bottom - Math.sin(angle - rotate) * tail;

		{

			var elt = document.getElementById("grArrow0." + this.id);
			var parent = elt.parentNode;

			parent.removeChild(elt);

			var html = htmlLineDiv(this.left, this.top, this.right,
					this.bottom, this.color, this.style, "grArrow0." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			var newelt = container.firstChild;

			parent.appendChild(newelt);

			mouseTargetReplace(elt, newelt);

		}

		{

			var elt = document.getElementById("grArrow1." + this.id);
			var parent = elt.parentNode;

			parent.removeChild(elt);

			var html = htmlLineDiv(this.arrowX1, this.arrowY1, this.right,
					this.bottom, this.color, this.style, "grArrow1." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			var newelt = container.firstChild;

			parent.appendChild(newelt);

			mouseTargetReplace(elt, newelt);

		}

		{

			var elt = document.getElementById("grArrow2." + this.id);
			var parent = elt.parentNode;

			parent.removeChild(elt);

			var html = htmlLineDiv(this.arrowX2, this.arrowY2, this.right,
					this.bottom, this.color, this.style, "grArrow2." + this.id,
					this.extension);

			var container = document.createElement('div');

			container.innerHTML = html;

			var newelt = container.firstChild;

			parent.appendChild(newelt);

			mouseTargetReplace(elt, newelt);

		}
	};

	graphicsArray[this.id] = this;

	return this;
};

