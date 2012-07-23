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
 * return css styled html code for drawing a line (by using the borders of an space-empty div layer)
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
	
	html += "left: "+left+"px;";
	html += "top: "+top+"px;";

	html += "transform-origin: 0% 50%;";
	html += "-webkit-transform-origin: 0% 50%;";
	html += "-moz-transform-origin: 0% 50%;";

	html += "transform: rotate(" + angle + "deg);";
	html += "-webkit-transform: rotate(" + angle + "deg);";
	html += "-moz-transform: rotate(" + angle + "deg);";

	html += style;

	html += "\" " + extension + ">" + content + "</div>";
	return html;
};

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
	};

	/**
	 * remove the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "frame";
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
		return document.getElementById("grLine"+this.id);
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

function htmlContainerDiv(left, top, right, bottom, layer, style, id, extension,
		content) {
	
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
	
	html += "z-index: "+layer+";";	
	
	html += "left: "+left+"px;";
	html += "top: "+top+"px;";
	
	var width = (right-left);
	var height = (bottom-top);
	
	html += "width: "+width+"px;";
	html += "height: "+height+"px;";
	html += "min-width: "+width+"px;";
	html += "min-height: "+height+"px;";
	html += "max-width: "+width+"px;";
	html += "max-height: "+height+"px;";
	
	html += style;
	
	html += "\" " + extension + ">"+content+"</div>";
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
				this.extension,this.content));
	};

	/**
	 * remove the graphical object
	 */

	this.Remove = function() {
		var elt = document.getElementById("grDiv" + this.id);
		elt.parentNode.removeChild(elt);
		graphicsArray[this.id] = undefined;
	};

	/**
	 * remove the graphical object
	 */

	this.AddChild = function(id) {
		if (id == undefined) {
			id = "frame";
		}
		var html = htmlContainerDiv(this.left, this.top, this.right,
				this.bottom, this.layer, this.style, "grDiv" + this.id,
				this.extension,this.content);

		var parent = document.getElementById(id);
		
		var container = document.createElement('div');
	
		container.innerHTML = html;

		parent.appendChild(container.firstChild);
	};
	
	/**
	 * get the associated html element
	 */
	
	this.GetElement = function() {
		return document.getElementById("grDiv"+this.id);
	};

	graphicsArray[this.id] = this;
	
	return this;
};