/**
 * mouse.js, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

mouseMoveHooks = [];
mouseX = 0;
mouseY = 0;

mouseClickHooks = [];
mouseClickHookPriorities = [];
mouseClickHookTargets = [];
mouseEvent = null;

mouseDownHooks = [];
mouseDownHookPriorities = [];
mouseDownHookTargets = [];

mouseUpHooks = [];
mouseUpHookPriorities = [];
mouseUpHookTargets = [];

/**
 * capture mouse pointer position / call hooks
 */

function mouseOnMove(e) {

	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}

	for ( var int = 0; int < mouseMoveHooks.length; ++int) {
		var hook = mouseMoveHooks[int];
		hook();
	}

	return true;
}

/**
 * capture mouse button down globally
 */

function mouseOnDown(e) {

	mouseEvent = e;

	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}

	var handler = null;
	var priority = -Number.MAX_VALUE;
	mouseHandlerIdx = -1;

	for ( var current = e.target; current != null; current = current.parentNode) {

		for ( var idx = mouseDownHookTargets.indexOf(current); idx >= 0; idx = mouseDownHookTargets
				.indexOf(current, idx + 1)) {
			var p = mouseDownHookPriorities[idx];
			if (p > priority) {
				priority = p;
				handler = mouseDownHooks[idx];
				mouseHandlerIdx = idx;
			}
		}

		var current_id = current.id;

		if (current_id)
			for ( var idx = mouseDownHookTargets.indexOf(current_id); idx >= 0; idx = mouseDownHookTargets
					.indexOf(current_id, idx + 1)) {
				var p = mouseDownHookPriorities[idx];
				if (p > priority) {
					priority = p;
					handler = mouseDownHooks[idx];
					mouseHandlerIdx = idx;
				}
			}
	}

	if (handler != null) {
		handler();
	}

}


/**
 * capture mouse button up globally
 */

function mouseOnUp(e) {

	mouseEvent = e;

	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}

	var handler = null;
	var priority = -Number.MAX_VALUE;
	mouseHandlerIdx = -1;

	for ( var current = e.target; current != null; current = current.parentNode) {

		for ( var idx = mouseUpHookTargets.indexOf(current); idx >= 0; idx = mouseUpHookTargets
				.indexOf(current, idx + 1)) {
			var p = mouseUpHookPriorities[idx];
			if (p > priority) {
				priority = p;
				handler = mouseUpHooks[idx];
				mouseHandlerIdx = idx;
			}
		}

		var current_id = current.id;

		if (current_id)
			for ( var idx = mouseUpHookTargets.indexOf(current_id); idx >= 0; idx = mouseUpHookTargets
					.indexOf(current_id, idx + 1)) {
				var p = mouseUpHookPriorities[idx];
				if (p > priority) {
					priority = p;
					handler = mouseUpHooks[idx];
					mouseHandlerIdx = idx;
				}
			}
	}

	if (handler != null) {
		handler();
	}

}


/**
 * capture mouse clicks globally
 */

function mouseOnClick(e) {

	mouseEvent = e;

	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}

	var handler = null;
	var priority = -Number.MAX_VALUE;
	mouseHandlerIdx = -1;

	for ( var current = e.target; current != null; current = current.parentNode) {

		for ( var idx = mouseClickHookTargets.indexOf(current); idx >= 0; idx = mouseClickHookTargets
				.indexOf(current, idx + 1)) {
			var p = mouseClickHookPriorities[idx];
			if (p > priority) {
				priority = p;
				handler = mouseClickHooks[idx];
				mouseHandlerIdx = idx;
			}
		}

		var current_id = current.id;

		if (current_id)
			for ( var idx = mouseClickHookTargets.indexOf(current_id); idx >= 0; idx = mouseClickHookTargets
					.indexOf(current_id, idx + 1)) {
				var p = mouseClickHookPriorities[idx];
				if (p > priority) {
					priority = p;
					handler = mouseClickHooks[idx];
					mouseHandlerIdx = idx;
				}
			}
	}

	if (handler != null) {
		handler();
	}

}

/**
 * install mouse hook
 */

/**
 * firefox and chromium would work with this code but IE seems to not work with
 * this one
 */
// document.captureEvents(Event.MOUSEMOVE);
// window.onmousemove = mouseOnMove;
/**
 * setup observe handler using prototype.js
 */

document.observe("mousemove", mouseOnMove);
document.observe("click", mouseOnClick);
document.observe("mousedown", mouseOnDown);
document.observe("mouseup", mouseOnUp);

/**
 * add mouse hook
 * 
 * @param target
 *            target object or id string
 * @param priority
 *            higher value gets called
 * @param f
 *            function to be called
 */

function addMouseClickHook(target, priority, f) {
	mouseClickHookTargets.push(target);
	mouseClickHookPriorities.push(priority);
	mouseClickHooks.push(f);
};

/**
 * remove mouse hook
 * 
 * @param function
 *            to be called
 */

function delMouseClickHook(f) {
	var idx = mouseClickHooks.lastIndexOf(f);
	if (idx >= 0) {
		var last = mouseClickHooks.pop();
		if (idx < mouseClickHooks.length) {
			mouseClickHooks[idx] = last;
		}

		last = mouseClickHookPriorities.pop();
		if (idx < mouseClickHookPriorities.length) {
			mouseClickHookPriorities[idx] = last;
		}

		last = mouseClickHookTargets.pop();
		if (idx < mouseClickHookTargets.length) {
			mouseClickHookTargets[idx] = last;
		}
	}
};

/**
 * remove mouse hook
 * 
 * @param target
 *            to remove all click hooks
 */

function clearMouseClickHooks(target) {
	for ( var idx = mouseClickHooks.indexOf(target); idx >= 0; idx = mouseClickHooks
			.indexOf(target, idx + 1)) {
		var last = mouseClickHooks.pop();
		if (idx < mouseClickHooks.length) {
			mouseClickHooks[idx] = last;
		}

		last = mouseClickHookPriorities.pop();
		if (idx < mouseClickHookPriorities.length) {
			mouseClickHookPriorities[idx] = last;
		}

		last = mouseClickHookTargets.pop();
		if (idx < mouseClickHookTargets.length) {
			mouseClickHookTargets[idx] = last;
		}
	}
};


/**
 * add mouse hook
 * 
 * @param target
 *            target object or id string
 * @param priority
 *            higher value gets called
 * @param f
 *            function to be called
 */

function addMouseUpHook(target, priority, f) {
	mouseUpHookTargets.push(target);
	mouseUpHookPriorities.push(priority);
	mouseUpHooks.push(f);
};

/**
 * remove mouse hook
 * 
 * @param function
 *            to be called
 */

function delMouseUpHook(f) {
	var idx = mouseUpHooks.lastIndexOf(f);
	if (idx >= 0) {
		var last = mouseUpHooks.pop();
		if (idx < mouseUpHooks.length) {
			mouseUpHooks[idx] = last;
		}

		last = mouseUpHookPriorities.pop();
		if (idx < mouseUpHookPriorities.length) {
			mouseUpHookPriorities[idx] = last;
		}

		last = mouseUpHookTargets.pop();
		if (idx < mouseUpHookTargets.length) {
			mouseUpHookTargets[idx] = last;
		}
	}
};

/**
 * remove mouse hook
 * 
 * @param target
 *            to remove all click hooks
 */

function clearMouseUpHooks(target) {
	for ( var idx = mouseUpHooks.indexOf(target); idx >= 0; idx = mouseUpHooks
			.indexOf(target, idx + 1)) {
		var last = mouseUpHooks.pop();
		if (idx < mouseUpHooks.length) {
			mouseUpHooks[idx] = last;
		}

		last = mouseUpHookPriorities.pop();
		if (idx < mouseUpHookPriorities.length) {
			mouseUpHookPriorities[idx] = last;
		}

		last = mouseUpHookTargets.pop();
		if (idx < mouseUpHookTargets.length) {
			mouseUpHookTargets[idx] = last;
		}
	}
};


/**
 * add mouse hook
 * 
 * @param target
 *            target object or id string
 * @param priority
 *            higher value gets called
 * @param f
 *            function to be called
 */

function addMouseDownHook(target, priority, f) {
	mouseDownHookTargets.push(target);
	mouseDownHookPriorities.push(priority);
	mouseDownHooks.push(f);
};

/**
 * remove mouse hook
 * 
 * @param function
 *            to be called
 */

function delMouseDownHook(f) {
	var idx = mouseDownHooks.lastIndexOf(f);
	if (idx >= 0) {
		var last = mouseDownHooks.pop();
		if (idx < mouseDownHooks.length) {
			mouseDownHooks[idx] = last;
		}

		last = mouseDownHookPriorities.pop();
		if (idx < mouseDownHookPriorities.length) {
			mouseDownHookPriorities[idx] = last;
		}

		last = mouseDownHookTargets.pop();
		if (idx < mouseDownHookTargets.length) {
			mouseDownHookTargets[idx] = last;
		}
	}
};

/**
 * remove mouse hook
 * 
 * @param target
 *            to remove all click hooks
 */

function clearMouseDownHooks(target) {
	for ( var idx = mouseDownHooks.indexOf(target); idx >= 0; idx = mouseDownHooks
			.indexOf(target, idx + 1)) {
		var last = mouseDownHooks.pop();
		if (idx < mouseDownHooks.length) {
			mouseDownHooks[idx] = last;
		}

		last = mouseDownHookPriorities.pop();
		if (idx < mouseDownHookPriorities.length) {
			mouseDownHookPriorities[idx] = last;
		}

		last = mouseDownHookTargets.pop();
		if (idx < mouseDownHookTargets.length) {
			mouseDownHookTargets[idx] = last;
		}
	}
};

/**
 * add mouse hook
 * 
 * @param function
 *            to be called
 */

function addMouseMoveHook(f) {
	if (mouseMoveHooks.lastIndexOf(f) < 0)
		mouseMoveHooks.push(f);
};

/**
 * remove mouse hook
 * 
 * @param function
 *            to be called
 */

function delMouseMoveHook(f) {
	var idx = mouseMoveHooks.lastIndexOf(f);
	if (idx >= 0) {
		var last = mouseMoveHooks.pop();
		if (idx < mouseMoveHooks.length) {
			mouseMoveHooks[idx] = last;
		}
	}
};
