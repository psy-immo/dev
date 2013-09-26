/**
 * debug.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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
 * log all correct assertion from the given machine
 * 
 * @param nbr  machine number
 */

function AllCorrect(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];
	var h = machine.hypergraph;
	var s = machine.stringids;
	
	var correct = Object.keys(h.correct);
	for ( var int = 0; int < correct.length; int++) {
		var id = correct[int];
		console.log(id+" "+s.FromId(id));
	}
}