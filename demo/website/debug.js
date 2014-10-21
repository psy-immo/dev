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

function Domain(x,nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];	
	var s = machine.stringids;

	if (x) {
		if (typeof(x)=="string")
			return s.ToId(x);
		else
			return s.FromId(x);
	} else return s.count;	
}

function Inferences(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];	
	var h = machine.hypergraph;
	console.log(h.inferences);
}

function PrintAllCorrect(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];
	var h = machine.hypergraph;
	var s = machine.stringids;
	
	var html = "<table class=\"debug\">";
	
	var correct = Object.keys(h.correct);
	for ( var int = 0; int < correct.length; int++) {
		var id = correct[int];
		if (h.IsTrivial(id))
			continue;
		html += "<tr>";
		html += "<td class=\"debugNbr\">";
		
		html += id;
		html += "</td><td class=\"debugTxt\">";
		html += escapeSome(s.FromId(id));
		html += "</td><td class=\"debugFlag\">";
		if (h.IsTrivial(id))
			html += "T";
		html += "</td><td class=\"debugFlag\">";
		if (h.IsJustified(id))
			html += "J";
		html += "</td><td class=\"debugFlag\">";
		if (h.IsConcluding(id))
			html += "C";	
		html += "</td><td class=\"debugFlag\">";
		var lacks = h.IndicatesWhichLacks(id);
		for ( var int2 = 0; int2 < lacks.length; int2++) {
			var array_element = lacks[int2];
			html += escapeSome(array_element+" ");
			
		}
		html += "</td>";
		html += "</tr>";
	}
	
	html += "</table>";
	
	document.write(html);		
}

function PrintAllTrivial(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];
	var h = machine.hypergraph;
	var s = machine.stringids;
	
	var html = "<table class=\"debug\">";
	
	var correct = Object.keys(h.trivial);
	for ( var int = 0; int < correct.length; int++) {
		var id = correct[int];
		if (!h.IsTrivial(id))
			continue;
		html += "<tr>";
		html += "<td class=\"debugNbr\">";
		
		html += id;
		html += "</td><td class=\"debugTxt\">";
		html += escapeSome(s.FromId(id));
		html += "</td><td class=\"debugFlag\">";
		if (h.IsTrivial(id))
			html += "T";
		html += "</td><td class=\"debugFlag\">";
		if (h.IsJustified(id))
			html += "J";
		html += "</td><td class=\"debugFlag\">";
		if (h.IsConcluding(id))
			html += "C";	
		html += "</td><td class=\"debugFlag\">";
		var lacks = h.IndicatesWhichLacks(id);
		for ( var int2 = 0; int2 < lacks.length; int2++) {
			var array_element = lacks[int2];
			html += escapeSome(array_element+" ");
			
		}
		html += "</td>";
		html += "</tr>";
	}
	
	html += "</table>";
	
	document.write(html);		
}

function PrintTrivialInferences(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];
	var h = machine.hypergraph;
	var s = machine.stringids;
	
	var html = "<table class=\"debug\">";
	
	var inferences = h.trivial_inference_ids;
	for ( var int = 0; int < inferences.length; int++) {
		
		html += "<tr>";
		html += "<td class=\"debugNbr\">";
		var id = inferences[int];
		var premises = h.inferences[id].p;
		var conclusions = h.inferences[id].c;
		html += id;
		html += "</td><td class=\"debugTxt\">";
		html += "<table class=\"debug2\">";
		for ( var int2 = 0; int2 < premises.length; int2++) {
			var p = premises[int2];
			html += "<tr><td class=\"debugNbr2\">";
			html += p;
			html += "</td><td class=\"debugTxt\">";
			html += escapeSome(s.FromId(p));
			html += "</td></tr>";
		}
		html += "</table>";
		html += "</td><td class=\"debugTxt\">";
		html += "<table class=\"debug2\">";
		for ( var int2 = 0; int2 < conclusions.length; int2++) {
			var c = conclusions[int2];
			html += "<tr><td class=\"debugNbr3\">";
			html += c;
			html += "</td><td class=\"debugTxt\">";
			html += escapeSome(s.FromId(c));
			html += "</td></tr>";
		}
		html += "</table>";
		
		html += "</td>";
		html += "</tr>";
	}
	
	html += "</table>";
	
	document.write(html);	
}

function PrintNonTrivialInferences(nbr) {
	if (!nbr)
		nbr = 0;
	var machine = myInferenceMachines[nbr];
	var h = machine.hypergraph;
	var s = machine.stringids;
	
	var html = "<table class=\"debug\">";
	
	var trivial_inferences = h.trivial_inference_ids;
	for ( var int = 0; int < h.inferences.length; int++) {
		if (trivial_inferences.indexOf(int)> 0)
			continue;
		html += "<tr>";
		html += "<td class=\"debugNbr\">";
		var id = int;
		var premises = h.inferences[id].p;
		var conclusions = h.inferences[id].c;
		html += id;
		html += "</td><td class=\"debugTxt\">";
		html += "<table class=\"debug2\">";
		for ( var int2 = 0; int2 < premises.length; int2++) {
			var p = premises[int2];
			html += "<tr><td class=\"debugNbr2\">";
			html += p;
			html += "</td><td class=\"debugTxt\">";
			html += escapeSome(s.FromId(p));
			html += "</td></tr>";
		}
		html += "</table>";
		html += "</td><td class=\"debugTxt\">";
		html += "<table class=\"debug2\">";
		for ( var int2 = 0; int2 < conclusions.length; int2++) {
			var c = conclusions[int2];
			html += "<tr><td class=\"debugNbr3\">";
			html += c;
			html += "</td><td class=\"debugTxt\">";
			html += escapeSome(s.FromId(c));
			html += "</td></tr>";
		}
		html += "</table>";
		
		html += "</td>";
		html += "</tr>";
	}
	
	html += "</table>";
	
	document.write(html);	
}

