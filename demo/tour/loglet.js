/**
 * loglet.js, (c) 2012-13, Immanuel Albrecht; Dresden University of Technology,
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
 * try to detect the server side php script base location
 */


if (typeof logletBaseURL == 'undefined') {
	var scripts = document.getElementsByTagName('script');
	logletBaseURL = scripts[scripts.length - 1].src + "\n";
	logletBaseURL = logletBaseURL.replace("loglet.js\n", "");
};




/**
 * fall back definitions / look ups
 */

if (typeof docId == 'undefined') {
	docId = "";
};

if (typeof studyId == 'undefined') {
	studyId = "";
};

if (typeof subjectIdPrompt == 'undefined') {
	subjectIdPrompt = "Please enter your identification token:";
};

if (typeof subjectIdInfo == 'undefined') {
	subjectIdInfo = "Your identification token is";
};

if (typeof serverNotWorking == 'undefined') {
	serverNotWorking = "Your current identification token is <b>not working</b>! ";
};

if (typeof subjectIdChange == 'undefined') {
	subjectIdChange = "Change";
};

if (typeof subjectId == 'undefined') {
	var keyname = "subject-id-" + studyId;
	var v = null;
	try {
		v = sessionStorage.getItem(keyname);
	} catch (e) {
		// firefox doesn't allow local storage for filesystem files
	}

	if (v !== null) {
		subjectId = v;
	} else {
		v = prompt(subjectIdPrompt, "").toUpperCase();
		if (v == null)
			v = "";
		subjectId = v;
		try {
			sessionStorage.setItem(keyname, subjectId);
		} catch (e) {
			// firefox doesn't allow local storage for filesystem files
		}
	}
};

/**
 * combine study and subject tokens
 */

if (typeof logId == 'undefined') {
	logId = studyId + "+" + subjectId;
};

/**
 * escape & < and >
 * 
 * @param s
 * @returns
 */

function escapeSome(s) {
	return ("" + s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g,
			"&gt;");
}

/**
 * unescape & < and >
 * 
 * @param s
 * @returns
 */

function unescapeSome(s) {
	return ("" + s).replace(/&gt;/g, ">").replace(/&lt;/g, "<").replace(
			/&amp;/g, "&");
}

/**
 * escape \\,\n,\t,\r
 * 
 * @param s
 * @returns
 */

function escapeBTNR(s) {
	return ("" + s).replace(/\\/g, "\\\\").replace(/\n/g, "\\n").replace(/\r/g,
			"\\r").replace(/\t/g, "\\t");
}

/**
 * unescape \\,\n,\t,\r
 * 
 * @param s
 * @returns
 */

function unescapeBTNR(s) {
	var output = "";
	var input = "" + s;
	var escaped = false;
	for ( var int = 0; int < input.length; int++) {
		var c = input[int];
		if (escaped) {
			escaped = false;
			if (c == 'n') {
				output += '\n';
			} else if (c == 't') {
				output += '\t';
			} else if (c == 'r') {
				output += '\r';
			} else
				output += c;
		} else if (c == '\\') {
			escaped = true;
		} else {
			output += c;
		}
	}

	return output;
}

/**
 * 
 * log the string using the global variables
 * 
 * @param string
 * 
 */

function doLog(string) {
	comLog(logId, docId, string);
}


serverDataCache = {};

/**
 * 
 * store a named variable on server
 * 
 * @param name
 * @param value
 * 
 */

function doSet(name, value) {
	comSave(logId, docId, name, value);
	
	var fullid = docId + "+" + name;
	serverDataCache[fullid] = value;
}

/**
 * 
 * store a named variable on server, if the to be stored values differ from the
 * local server cache value
 * 
 * @param name
 * @param value
 * 
 */

function doSetIfDifferent(name, value) {
	var fullid = docId + "+" + name;
	if (fullid in serverDataCache) {
		if (serverDataCache[fullid] == value)
				return;
	}
	comSave(logId, docId, name, value);
	serverDataCache[fullid] = value;
}

/**
 * 
 * retrieve the stored value of a named variable
 * 
 * @param name
 * @returns value
 * 
 */

function doGet(name) {
	return comLoad(logId,docId,name);
}

function didSave(name) {
	return comInStorage(logId,docId,name);
}

function doGetAll() {

	return {};
}

/**
 * 
 * check whether the server storage works currently
 * 
 * @returns true, if storage works
 * 
 */

function doesOperate() {
	return comTest(logId);
}

/**
 * 
 * print the current subject token and change button
 * 
 */

function printChangeSubjectButton() {
	if (doesOperate()) {
		document.write("<div>" + subjectIdInfo + " <b>" + subjectId
				+ "</b>. <a href=\"javascript:changeSubject()\">"
				+ subjectIdChange + "</a></div>");
	} else {
		document.write("<div>" + subjectIdInfo + " <font color=\"#FF0000\"><b>"
				+ subjectId + "</b></font>. " + serverNotWorking
				+ " <a href=\"javascript:changeSubject()\">" + subjectIdChange
				+ "</a></div>");
	}
}

function changeSubject() {
	var keyname = "subject-id-" + studyId;
	sessionStorage.removeItem(keyname);
	window.location.reload();
}

/**
 * routine that restores described values at the correct property's position in
 * the value array
 * 
 * @param description
 *            description string
 * @param props
 *            property name array
 * @param vals
 *            value array
 * @param locks
 *            lock-status array
 */
function descriptionToProperties(description, props, vals, locks) {
	var lines = description.split("\n");
	for ( var int = 0; int < lines.length; int++) {
		var line = lines[int];
		var eqidx = line.indexOf("=");
		if (eqidx > 0) {
			var p = line.substr(0, eqidx - 1).trim();
			var idx = props.indexOf(p);
			if (p.indexOf("!") == 0) {
				idx = props.indexOf(p.substr(1));
				if (idx >= 0) {
					locks[idx] = true;
				}
			}
			var q = line.substr(eqidx + 1);
			vals[idx] = unescapeBTNR(q);
		} else {
			var p = line.trim();
			if (p.indexOf("!") == 0) {
				var idx = props.indexOf(p.substr(1));
				if (idx >= 0) {
					locks[idx] = true;
				}
			}
		}
	}
}

/**
 * routine that stores values in a description
 * 
 * @param props
 *            property name array
 * @param vals
 *            value array
 * @param locks
 *            lock-status array
 * @returns description string
 * 
 */
function propertiesToDescription(props, vals, locks) {
	var description = "";
	for ( var int = 0; int < props.length; int++) {
		if (description)
			description += "\n";
		if (locks[int]) {
			description += "!";
		}
		description += props[int];
		description += "=";
		description += escapeBTNR(vals[int]);
	}
	return description;
}