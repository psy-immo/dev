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
 * try to detect the server side php script base location
 */

if (typeof logletBaseURL == 'undefined') {
	var scripts = document.getElementsByTagName('script');
	logletBaseURL = scripts[scripts.length - 1].src + "\n";
	logletBaseURL = logletBaseURL.replace("loglet.js\n", "");
};

/**
 * load the server communication applet, if not present
 */

if (!$('loglet')) {
	document.write("<applet id=\"loglet\" " + "name=\"loglet\"" + " archive=\""
			+ logletBaseURL + "loglet.jar\" "
			+ "code=\"de.tu_dresden.psy.util.Loglet\" "
			+ "MAYSCRIPT style=\"width: 1px; height: 1px\"></applet>");
}

/**
 * check whether we are in preview mode
 */

if (window.location.toString().toQueryParams()["preview"]) {
	efmlPreview = true;

	/**
	 * change setup for preview mode
	 */

	studyId = "preview";
	subjectId = "preview";

} else {
	efmlPreview = false;
}

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
 * query server for stored value (java applet version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function getServerApplet(id, name, serverurl) {
	var applet = document.getElementById("loglet");

	if (!applet) {
		return "!! applet element not found";
	}

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
 * query server for stored value (javascript version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function getServerJS(id, name, serverurl, throwIfUnavailable) {
	var request;

	/**
	 * thanks to cssSandpaper getXMLHttpRequest :)
	 */

	if (window.XMLHttpRequest) {

		request = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (dummy) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
	} else {
		if (throwIfUnavailable) {
			throw "!! XMLHttpRequest not supported by browser";
		}
		return "!! XMLHttpRequest not supported by browser";
	}

	/**
	 * encode the post data
	 */

	var data = "";

	data += encodeURIComponent("id") + "=" + encodeURIComponent(id);
	data += "&" + encodeURIComponent("varname") + "="
			+ encodeURIComponent(name);
	data += "&" + encodeURIComponent("value") + "=" + encodeURIComponent("");

	try {
		request.open("POST", serverurl, false);

		/**
		 * is there a bug that requires
		 * 
		 * request.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000
		 * 00:00:00 GMT");
		 * 
		 * here??
		 */

		request.send(data);
	} catch (ex) {
		var error = "" + ex + "";
		if (throwIfUnavailable) {
			throw "!! ERROR requesting " + serverurl + " with " + data + ":\n"
					+ error;
		}
		return "!! ERROR requesting " + serverurl + " with " + data + ":\n"
				+ error;
	}

	/**
	 * skip the http header
	 */

	var val = request.responseText
			.substr(request.responseText.indexOf("\n", 0) + 1);

	if (request.statusText != "OK")
		return "!! error in request: " + request.statusText + " .. " + val;

	var unescaped = unescapeSome(val);

	return unescaped;
}

/**
 * query server for stored value (choose which version to work with)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function getServer(id, name, serverurl) {
	var result = "";
	try {
		/** try JS first, and throw if unavailable */
		result = getServerJS(id, name, serverurl, true);
	} catch (unavailable) {
		result = getServerApplet(id, name, serverurl);
	}

	return result;
}

/**
 * save data to local file
 * 
 * @param contents
 *            file contents
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
 * @param contents
 *            new contents of the clipboard
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
 * @returns contents contents of the clipboard
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
 * @returns contents contents of the file
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
 * query server to change stored value (java applet version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

/**
 * query server to change stored value (java applet version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function setServerApplet(id, name, value, serverurl) {
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
 * query server to change stored value (java applet version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function setServerJS(id, name, value, serverurl, throwIfUnavailable) {
	var request;

	/**
	 * thanks to cssSandpaper getXMLHttpRequest :)
	 */

	if (window.XMLHttpRequest) {

		request = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			request = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (dummy) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}
	} else {
		if (throwIfUnavailable) {
			throw "!! XMLHttpRequest not supported by browser";
		}
		return "!! XMLHttpRequest not supported by browser";
	}

	/**
	 * encode the post data
	 */

	var data = "";

	data += encodeURIComponent("id") + "=" + encodeURIComponent(id);
	data += "&" + encodeURIComponent("varname") + "="
			+ encodeURIComponent(name);
	data += "&" + encodeURIComponent("value") + "=" + encodeURIComponent(value);

	try {
		request.open("POST", serverurl, false);

		/**
		 * is there a bug that requires
		 * 
		 * request.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000
		 * 00:00:00 GMT");
		 * 
		 * here??
		 */

		request.send(data);
	} catch (ex) {
		var error = "" + ex + "";
		if (throwIfUnavailable) {
			throw "!! ERROR requesting " + serverurl + " with " + data + ":\n"
					+ error;
		}
		return "!! ERROR requesting " + serverurl + " with " + data + ":\n"
				+ error;
	}

	/**
	 * skip the http header
	 */

	var val = request.responseText
			.substr(request.responseText.indexOf("\n", 0) + 1);

	if (request.statusText != "OK")
		return "!! error in request: " + request.statusText + " .. " + val;

	var unescaped = unescapeSome(val);

	return unescaped;
}

/**
 * query server to change stored value (automatic version)
 * 
 * @param id
 * @param name
 * @param serverurl
 */

function setServer(id, name, value, serverurl) {
	var result = "";
	
	try {
		result = setServerJS(id, name, value, serverurl, true);
	}
	catch (unavailable) {
		result = setServerApplet(id, name, value, serverurl);
	}
	
	return result;
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
 * store a named variable on server, if the to be stored values differ from the
 * local server cache value
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
 * check whether the server storage works currently (using the javascript
 * XMLHttpRequest version)
 * 
 * @returns true, if storage works
 * 
 */

function doesJSOperate() {
	if (logletBaseURL) {
		try {
			if (getServerJS(logId, docId + "+" + name, logletBaseURL
					+ "operates.php") == "okay") {
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
 * check whether the server storage works currently (using the java applet
 * version)
 * 
 * @returns true, if storage works
 * 
 */

function doesAppletOperate() {
	if (logletBaseURL) {
		try {
			if (getServerApplet(logId, docId + "+" + name, logletBaseURL
					+ "operates.php") == "okay") {
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
 * check whether the server storage works currently
 * 
 * @returns true, if storage works
 * 
 */

function doesOperate() {
	if (doesJSOperate()) {
		return true;
	}
	return doesAppletOperate();
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