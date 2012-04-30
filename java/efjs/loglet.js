/**
 * loglet.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

document.write("<applet id=\"loglet\" "
		+ "name=\"loglet\""
		+ " archive=\"loglet.jar\" "
		+ "code=\"de.tu_dresden.psy.util.Loglet\" "
		+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");

/**
 * query server for stored value
 *  
 * @param id
 * @param name
 * @param serverurl
 */

function getServer(id, name, serverurl) {
	var applet = document.getElementById("loglet");
	
	/**
	 * java script to java interaction is buggy so add string termination zeros....
	 */
	
	return applet.queryLogger(""+id+"\0",""+name+"\0","\0",""+serverurl+"\0");	
}

/**
 * query server for stored value
 *  
 * @param id
 * @param name
 * @param serverurl
 */

function setServer(id, name, value, serverurl) {
	var applet = document.getElementById("loglet");
	
	/**
	 * java script to java interaction is buggy so add string termination zeros....
	 */
	
	return applet.queryLogger(""+id+"\0",""+name+"\0",""+value+"\0",""+serverurl+"\0");	
}