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

/**
 * load the server communication applet
 */

document.write("<applet id=\"loglet\" " + "name=\"loglet\""
		+ " archive=\"loglet.jar\" "
		+ "code=\"de.tu_dresden.psy.util.Loglet\" "
		+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");

/**
 * loglet server cache object
 */

serverDataCache = {};

/**
 * this is for bugfixing chromium + icedtea java interop bugs
 */

function bugfixParam(param) {
	return "" + param + "\0";
}

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
		//firefox doesn't allow local storage for filesystem files
	}

	if (v !== null) {
		subjectId = v;
	} else {
		v = prompt(subjectIdPrompt,"").toUpperCase();
		if (v == null)
			v = "";
		subjectId = v;
		try {
			sessionStorage.setItem(keyname, subjectId);
		} catch (e) {
			//firefox doesn't allow local storage for filesystem files			
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
	return ("" + s).replace(/\\n/g, "\n").replace(/\\r/g, "\r").replace(/\\t/g,
			"\t").replace(/\\\\/g, "\\");
}

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
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var val = applet.queryLogger(bugfixParam(id), bugfixParam(name),
			bugfixParam(""), bugfixParam(serverurl));
	
	var unescaped = unescapeSome(val); 

	return unescaped;
}

/**
 * save data to local file
 * 
 * @param contents   file contents
 */


function writeLocalFile(contents) {
	var applet = document.getElementById("loglet");

	/**
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var val = applet.setLocalFileContents(bugfixParam(contents));
	return val;
}



/**
 * copy data to clipboard
 * 
 * @param contents   new contents of the clipboard
 */


function setClipboardContents(contents) {
	var applet = document.getElementById("loglet");

	/**
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var val = applet.setClipboardContents(bugfixParam(contents));
	return val;
}


/**
 * copy data from clipboard
 * 
 * @returns contents   contents of the clipboard
 */


function getClipboardContents() {
	var applet = document.getElementById("loglet");

	/**
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var val = applet.getClipboardContents();
	
	return val;
}

/**
 * read data from local file
 * 
 * @returns contents   contents of the file
 */


function readLocalFile() {
	var applet = document.getElementById("loglet");

	/**
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var val = applet.getLocalFileContents();
	
	return val;
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
	 * java script to java interaction is buggy so add string termination
	 * zeros....
	 */

	var result = applet.queryLogger(bugfixParam(id), bugfixParam(name),
			bugfixParam(escapeSome(value)), bugfixParam(serverurl));
	
	return unescapeSome(result);
}

/**
 * 
 * log the string using the global variables
 * 
 * @param string
 * 
 */

function doLog(string) {
	if (logletBaseURL) {
		setServer(logId, docId, string, logletBaseURL + "log.php");
	}
}

/**
 * 
 * store a named variable on server
 * 
 * @param name
 * @param value
 * 
 */

function doSet(name, value) {
	if (logletBaseURL) {
		var fullid = docId + "+" + name;
		var result = setServer(logId, fullid, value, logletBaseURL + "push.php");
		serverDataCache[fullid] = result;	
	}
}

/**
 * 
 * store a named variable on server, if the to be stored values differ from the local server cache value
 * 
 * @param name
 * @param value
 * 
 */

function doSetIfDifferent(name, value) {
	if (logletBaseURL) {
		var fullid = docId + "+" + name;
		if (fullid in serverDataCache) {
				if (serverDataCache[fullid] == value)
					return;  
		} 
		var result = setServer(logId, fullid, value, logletBaseURL + "push.php");
		serverDataCache[fullid] = result;
	}
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
	if (logletBaseURL) {
		var fullid = docId + "+" + name;
		var result = getServer(logId, fullid, logletBaseURL + "pull.php");
		
		return result;
	}
	return "";
}

function doGetAll() {
	var urldecode = function(url) {
		return decodeURIComponent(url.replace(/\+/g, ' '));
	};

	if (logletBaseURL) {
		var raw = getServer(logId, docId + "+", logletBaseURL + "pullall.php")
				.split('\n');
		var entries = {};
		var prefix_length = docId.length + 1;

		for ( var int = 0; int < raw.length; ++int) {
			var line = raw[int].split(' ');
			if (line.length > 1) {
				var fullid = urldecode(line[0]); 
				var key = fullid.substr(prefix_length);
				var result = unescapeSome(urldecode(line[1]));
				entries[key] = result;
				serverDataCache[fullid] = result;
			}
		}
		return entries;
	}
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
	if (logletBaseURL) {
		try {
		if (getServer(logId, docId + "+" + name, logletBaseURL + "operates.php") == "okay") {
			return true;
		}
		} catch (err) {
			return false;
		}
	}
	return false;
}

/**
 * 
 * print the current subject token and change button
 * 
 */

function printChangeSubjectButton() {
	if (myStorage.useLoglet()) {
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
