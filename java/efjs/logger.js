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
	this.t0 = new Date().getTime();
	this.events = [];
	
	this.Log = function(item) {
		this.events[this.events.length] = [new Date().getTime() - this.t0, item];
	};
}

/**
 * generate the myLooger object
 */
myLogger = new Logger();