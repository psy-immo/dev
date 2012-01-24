/**
 * tags.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * returns an object that manages tags of other objects
 */
function Tags() {
	this.taglists = [];
	this.tags = [];
	this.objs = [];

	/**
	 * this adds the object to the respective tag lists
	 */
	this.Add = function(obj, tags) {

		for ( var int = 0; int < tags.length; int++) {
			var tag = tags[int];

			if (tag in this.taglists) {
				this.taglists[tag].push(obj);
			} else {
				this.taglists[tag] = [ obj ];
				this.tags.push(tag);
			}

		}

		this.objs.push(obj);
	};

	/**
	 * this return all objects bearing all of the tags
	 */
	this.AllTags = function(tags) {
		var objs = this.objs;
		for ( var int = 0; int < tags.length; int++) {
			var tag = tags[int];
			if (tag in this.taglists) {
				var taglist = this.taglists[tag];
				objs = objs.filter(function(x) {
					return taglist.indexOf(x) >= 0;
				});
			} else {
				return [];
			}
		}
		return objs;
	};

	/**
	 * this return all objects bearing all of the tags, not having any of the
	 * reject tags
	 */
	this.AllTagsBut = function(tags, rejects) {
		var objs = this.objs;
		for ( var int = 0; int < tags.length; int++) {
			var tag = tags[int];
			if (tag in this.taglists) {
				var taglist = this.taglists[tag];
				objs = objs.filter(function(x) {
					return taglist.indexOf(x) >= 0;
				});
			} else {
				return [];
			}
		}
		for ( var int = 0; int < rejects.length; int++) {
			var tag = rejects[int];
			if (tag in this.taglists) {
				var taglist = this.taglists[tag];
				objs = objs.filter(function(x) {
					return taglist.indexOf(x) < 0;
				});
			}
		}
		return objs;
	};

};

/**
 * create the myTags object
 */
myTags = new Tags();