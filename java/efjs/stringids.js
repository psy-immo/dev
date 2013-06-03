/**
 * stringids.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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
 * this class provides functionality to make ids from strings, i.e. this class
 * maps filled sentence templates to integer ids and vice versa
 */

function StringIds() {
	this.count = 0;
	this.to_id = {};
	this.from_id = {};
	
	/**
	 * add another string to the id database
	 */
	
	this.AddString = function(s) {
		var unified = s.toUpperCase().trim();
		if (!(unified in this.to_id)) {
			this.to_id[unified] = this.count;
			this.from_id[this.count] = s;
			this.count += 1;
		}
	};
	
	/**
	 * s   input string
	 * @returns the id of the given string, or -1 if the string is unknown
	 */
	
	this.ToId = function(s) {
		var unified = s.toUpperCase().trim();
		if (unified in this.to_id)
			return this.to_id[unified];
		return -1;
	};
	
	/**
	 * id 
	 * @returns the string that corresponds to the given id
	 */
	
	this.FromId = function(id) {
		if (id in this.from_id)
			return this.from_id[id];
		return "!!UNKNOWN STRING!!";
		
	};
}