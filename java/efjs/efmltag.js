/**
 * efmltag.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

efmlTagCounter = 0;
efmlTagArray = [];

/**
 * common efml tag constructor
 */
function EfmlTagConstructor(me) {
	me.id = efmlTagCounter++;

	/**
	 * @returns efml code of this tag
	 */

	me.GetEfml = function() {
		return "";
	};

	/**
	 * @returns string that can be used to factor another instance of this
	 *          object
	 */

	me.GetDescription = function() {
		return "";
	};


	efmlTagArray[me.id] = me;
}
