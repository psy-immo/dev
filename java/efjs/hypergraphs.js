/**
 * hypergraphs.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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
 * This file contains routines that are used for abstract inference.
 */

/**
 * creates an abstract inference graph
 */

function InferenceGraph() {
	
	this.inferences = [];
	
	/**
	 * adds an inference hyper-arrow from the premises to the conclusions
	 */
	
	this.AddInference = function(premises, conclusions) {
		var sorted_premises = [];
		var sorted_conclusions = [];
		for ( var int = 0; int < premises.length; int++) {
			var x = premises[int];
			if ((x in sorted_premises) == false)
				sorted_premises.push(x);
		}
		for ( var int2 = 0; int2 < conclusions.length; int2++) {
			var x = conclusions[int];
			if ((x in sorted_conclusions) == false)
				sorted_conclusions.push(x);
		}
		
		sorted_premises.sort();
		sorted_conclusions.sort();
		
		this.inferences.push({"p":sorted_premises, "c":sorted_conclusions});
	};
	
	
	return this;
};
