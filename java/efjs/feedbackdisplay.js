/**
 * feedbackdisplay.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var feedbackIdCounter = 0;
var feedbackArray = [];
var feedbackNames = {};

function FeedbackDisplay(name) {
	this.id = feedbackIdCounter ++;
	
	if (name)
		this.name = name;
	else
		this.name = "feedback"+this.id;
	
	this.contents = "";
	
	
	this.WriteHtml = function() {
		document.write("<span id=\"feedbackDisplay"+this.id+"\">");
		document.write(this.contents);
		document.write("</span>");		
	};
	
	this.UpdateContents = function() {
		var element = $("feedbackDisplay"+this.id);
		element.innerHTML = this.contents;
        element.scrollIntoView();
	};
	
	this.SetValue = function(contents) {
		this.contents = contents;
		this.UpdateContents();
	};
	
	this.GetValue = function() {
		return this.contents;
	};
	
	this.SetContents = function(contents) {
		this.contents = contents;
		
		return this;
	};
	
	
	feedbackArray.push(this);
	feedbackNames[this.name] = this;
	
	myStorage.RegisterField(this, "feedbackArray[" + this.id + "]");
}
