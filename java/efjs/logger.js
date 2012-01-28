/**
 * logger.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * create a logger object that can be used to log all input events
 */
function Logger() {
	this.t0 = new Date();
	this.events = [];
	
	/**
	 * Log arbitrary item as string with timestamp relative to page load
	 */
	
	this.Log = function(item) {
		var logstring = ("" + item).replace("\n","<br/>");
		var timestamp = new Date();
		this.events[this.events.length] = timestamp + " = t0 + "+(timestamp - this.t0) + "ms: "+ logstring;
	};
	
	/**
	 * return all log data as string
	 */
	
	this.GetValue = function() {
		var concat = "";
		for ( var int = 0; int < this.events.length; int++) {
			if (int > 0) {
				concat += "\n";
			}
			concat += this.events[int];
		}
		return concat;
	};
	
	/**
	 * add previously logged events
	 */
	
	this.SetValue = function(events) {
		var splitted = ("" + events).split("\n");
		
		this.events = splitted;
		
		this.Log("Site reloaded.");
	};
	
	
	myStorage.RegisterField(this,"Logger");
}

/**
 * generate the myLooger object
 */
myLogger = new Logger();
