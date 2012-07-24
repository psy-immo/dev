/**
 * mouse.js, (c) 2012, Immanuel Albrecht; Dresden University of
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

mouseMoveHooks = [];
mouseX = 0;
mouseY = 0;



/**
 * capture mouse pointer position / call hooks
 */

function mouseOnMove(ev) {

	var e = ev;

	if (!ev)
		e = window.event;

	if (e.pageX || e.pageY) {
		mouseX = e.pageX;
		mouseY = e.pageY;
	} else if (e.clientX || e.clientY) {
		mouseX = e.clientX + document.body.scrollLeft
				+ document.documentElement.scrollLeft;
		mouseY = e.clientY + document.body.scrollTop
				+ document.documentElement.scrollTop;
	}
	
	for (var int=0;int<mouseMoveHooks.length;++int) {
		var hook = mouseMoveHooks[int];
		hook();
	}
	
	return true;
}


/**
 * install mouse hook
 */ 


/**
 * firefox and chromium would work with this code
 * but IE seems to not work with this one
 */
// document.captureEvents(Event.MOUSEMOVE);
// window.onmousemove = mouseOnMove;

/**
 * setup observe handler using prototype.js
 */

document.observe("mousemove",mouseOnMove);


/**
 * add mouse hook
 * @param  function to be called
 */

function addMouseMoveHook(f) {
	if (mouseMoveHooks.lastIndexOf(f) < 0)
		mouseMoveHooks.push(f);
};

/**
 * remove mouse hook
 * @param  function to be called
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



