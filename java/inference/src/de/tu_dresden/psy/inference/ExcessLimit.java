/**
 * ExcessLimit.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference;

import java.util.Date;

/**
 * implements methods to break operation after a given time limit has been hit
 * 
 * @author albrecht
 * 
 */

public class ExcessLimit {

	private Date time;

	private boolean hitLimit;

	/**
	 * create a new excess limit
	 * 
	 * @param limitInSecs
	 */
	public ExcessLimit(float limitInSecs) {
		time = new Date();
		time.setTime(time.getTime() + (long) (1000.f * limitInSecs));
		hitLimit = false;
	}

	/**
	 * check time limit
	 * 
	 * @return true, if there is still time left
	 */

	public boolean continueTask() {
		if (time.before(new Date())) {
			hitLimit = true;
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return true, if the limit was exceeded
	 */
	public boolean exceeded() {
		return hitLimit;
	}
}
