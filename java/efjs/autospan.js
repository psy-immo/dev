/**
 * autospan.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var autoSpanIdCounter = 0;
var autoSpanArray = [];
var autoSpanNames = {};

function AutoSpan(value,  classname, name) {
	this.id = autoSpanIdCounter ++;
	
	/**
	 * initial value
	 */
	
	if (value)
		this.value = 1;
	else
		this.value = 0;
	
	/**
	 * css class name
	 */
	
	if (classname)
		this.classname = classname;
	else
		this.classname="autoSpan";
	
	if (name)
		this.name = name;
	else
		this.name = "autoSpan"+this.id;
	
	this.WriteHtml = function() {
		/**
		 * write only the opening tag
		 */
		document.write("<span id=\"autoSpan"+this.id+"\" class=\""+this.classname+""+this.value+"\">");
		
		/**
		 * the closing tag has to be written out by the efml compiler
		 */
				
	};
		
	this.SetValue = function(value) {
		var element = $("autoSpan"+this.id);
		
		element.removeClassName(this.classname+""+this.value);
		
		this.value = parseInt(value);
		
		element.addClassName(this.classname+""+this.value);
	};
	
	this.GetValue = function() {
		return this.value;
	};
		
	autoSpanArray.push(this);
	autoSpanNames[this.name] = this;
	
	myStorage.RegisterField(this, "autoSpanArray[" + this.id + "]");
}
