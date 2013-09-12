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
	 * 
	 * @returns this
	 */

	this.AddString = function(s) {
		var unified = s.toUpperCase().trim();
		if (!(unified in this.to_id)) {
			this.to_id[unified] = this.count;
			this.from_id[this.count] = s;
			this.count += 1;
		}

		return this;
	};

	/**
	 * add a Cartesian concatenation of sets of strings to the id database
	 * 
	 * @param vfactor
	 *            vector of factor sets
	 * 
	 * @returns this
	 */
	this.AddStringProduct = function(vfactor) {
		if (vfactor.length < 1) {
			return this;
		}

		var sigma = [];

		for ( var int = 0; int < vfactor.length; int++) {
			var factor = vfactor[int];
			if (factor.length < 1) {
				return this;
			}
			sigma.push(0);
		}
		
		var stop_criterion = vfactor[0].length;
		
		while (sigma[0] < stop_criterion) {
			/**
			 * add the string corresponding to sigma
			 */
			
			var concat = "";
			
			for (var i = 0; i < sigma.length; ++i) {
				concat += vfactor[i][sigma[i]];
			}
			
			this.AddString(concat);
			
			/**
			 * next permutation
			 */

			sigma[sigma.length - 1] += 1;
			for (var i = sigma.length - 1; i > 0; --i) {
				if (sigma[i] >= vfactor[i].length) {
					sigma[i] = 0;
					sigma[i - 1] += 1;
				} else {
					break;
				}
			}
			
		}

		return this;
	};

	/**
	 * s input string
	 * 
	 * @returns the id of the given string, or -1 if the string is unknown
	 */

	this.ToId = function(s) {
		var unified = s.toUpperCase().trim();
		if (unified in this.to_id)
			return this.to_id[unified];
		return -1;
	};
	
	/**
	 * vs  array of input strings
	 * 
	 * @returns  an array that contains the ids of the given strings, or -1 for unknown strings
	 */

	this.ToIds = function(vs) {
		var rs = [];
		for ( var int = 0; int < vs.length; int++) {
			var s = vs[int];
			rs.push(this.ToId(s));
		}
		return rs;
	};

	/**
	 * id
	 * 
	 * @returns the string that corresponds to the given id
	 */

	this.FromId = function(id) {
		if (id in this.from_id)
			return this.from_id[id];
		return "!!UNKNOWN STRING!!";

	};
	
	/**
	 * vid   array of ids
	 * 
	 * @returns  an array that contains the strings corresponding to the ids
	 */

	this.FromIds = function(vid) {
		var rs = [];
		for ( var int = 0; int < vid.length; int++) {
			var id = vid[int];
			rs.push(this.FromId(id));
		}
		return rs;
	};

	
	return this;
}