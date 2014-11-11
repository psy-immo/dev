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


/**
 * use this file in place of servercom.js in order to supress server communication
 */

console.log("Server communication disabled.");

/**
 * this variable controls, whether asynchronous requests are allowed.
 * (this may be useful to make sure all requests get through when 
 *  unloading the page)
 */

comAllowAsync = true;

function comAsyncMode(allow) {
	comAllowAsync = allow;
}

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
	var fake = {};
	
	fake.responseText = "";
	fake.response = null;
	
	return fake;
}

/**
 * 
 * @param id
 * @returns true, if the id is working with the com interface
 */

function comTest(id) {
	return false;
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
	return null;
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
	return null;
}

/**
 * 
 * @param id      user id
 * @param doc     document id
 * @param token   token id
 * @returns  true, if there is something stored for the given ids
 */

function comInStorage(id, doc, token) {
	return false;
}

/**
 * 
 * @param id      user id
 * @param doc     document id
 * @param token   token id
 * @returns  the state string that was previously stored
 */

function comLoad(id, doc, token) {
	return "";
}