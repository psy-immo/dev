/**
 * endecoder.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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
 * this function encodes a string, such that it cannot be read by looking at sources :)
 */
function encodeString(s){
	var previous = Math.round(Math.random()*255);
	var codes = [s.length, previous];
	
	for ( var int = 0; int < s.length; int++) {
		var array_element = s.charCodeAt(int);
		codes[int+2] = (array_element ^ 173)^previous;
		previous = array_element;
	};
	for (var int = s.length; int < 128; int++) {
		codes[int+2] = ((Math.round(Math.random()*255)) ^ 173 ) ^previous;
		previous = codes[int];
	}
	codes[0] = codes[128]^codes[0];
	return codes;
};

function decodeString(codes){
	var len = codes[128]^codes[0];
	var s = "";
	var previous = codes[1];
	for (var int = 0; int < len; int++){
		var array_elt = (codes[int+2]^previous^173);
		s += String.fromCharCode(array_elt);
		previous = array_elt;		
	}
	return s;
}