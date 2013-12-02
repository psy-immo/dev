/**
 * counter.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var counterIdCounter = 0;
var counterArray = [];
var counterNames = {};

function Counter(name, value) {
	this.id = counterIdCounter ++;
	
	if (value)
		this.value = parseInt(value);
	else
		this.value = 0;
	
	if (name)
		this.name = name;
	else
		this.name = "counter"+this.id;
	
	
	
	this.WriteHtml = function() {
		document.write("<span id=\"counterDisplay"+this.id+"\">");
		document.write(""+this.value);
		document.write("</span>");		
	};
	
	this.UpdateContents = function() {
		var element = $("counterDisplay"+this.id);
		element.innerHTML = ""+this.value;
	};
	
	this.SetValue = function(value) {
		this.value = value;
		this.UpdateContents();
	};
	
	this.GetValue = function() {
		return this.value;
	};
	
	this.SetContents = function(value) {
		this.value = parseInt(value);
		return this;
	};
	
	
	counterArray.push(this);
	counterNames[this.name] = this;
	
	myStorage.RegisterField(this, "counterArray[" + this.id + "]");
}
