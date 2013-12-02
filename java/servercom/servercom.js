/**
 * servercom.js, (c) 2011-13, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
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

if (typeof comURL == 'undefined') {
	var script = document.getElementById('servercom_js');
	comURL = script.src + "\n";
	comURL = comURL.replace("servercom.js\n", "com.php");
};

console.log("comURL = " + comURL);

/**
 * 
 * @param id
 *            id that is transmitted
 * @param q
 *            q that is transmitted
 * @param d
 *            d that is transmitted
 * 
 * @returns request object
 */

function getComRequest(id, q, d, x, y, synchron) {
	var request = new XMLHttpRequest();

	var data = "id=" + encodeURIComponent(id) + "&q=" + encodeURIComponent(q)
			+ "&d=" + encodeURIComponent(d) + "&x=" + encodeURIComponent(x)
			+ "&y=" + encodeURIComponent(y);

	try {

		request.open("POST", comURL, (synchron ? false : true));
		request.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");

		request.send(data);
	} catch (ex) {
		comError = ex;
		comData = data;
	}

	return request;
}

/**
 * 
 * @param id
 * @returns true, if the id is working with the com interface
 */

function comTest(id) {
	return getComRequest(id, "test", "", "", "", true).responseText == "good";
}

/**

 * adds a line to the log, returns the request
 * 
 * @param id   user id
 * @param doc  document id
 * @param log  string that is appended to the log
 * 
 * @returns  an asynchronous request
 */


function comLog(id, doc, log) {
	return getComRequest(id, "log", doc, log,"", false);
}

/**
 * saves the state of some object on the server
 * 
 * @param id       user id
 * @param doc      document id
 * @param token    token id
 * @param state    state string to be stored
 * @returns  an asynchronous request
 */

function comSave(id, doc, token, state) {
	return getComRequest(id, "save", doc, token, encodeURI(state), false);
}

/**
 * 
 * @param id      user id
 * @param doc     document id
 * @param token   token id
 * @returns  true, if there is something stored for the given ids
 */

function comInStorage(id, doc, token) {
	return getComRequest(id, "avail", doc, token, "", true).responseText == "yes";
}

/**
 * 
 * @param id      user id
 * @param doc     document id
 * @param token   token id
 * @returns  the state string that was previously stored
 */

function comLoad(id, doc, token) {
	return decodeURI(getComRequest(id, "load", doc, token, "", true).responseText);
}