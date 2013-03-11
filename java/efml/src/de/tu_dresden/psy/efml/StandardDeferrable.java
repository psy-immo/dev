/**
 * StandardDeferrable.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.efml;

/**
 * class that implements standard behavior of the Deferrable interface
 * 
 * @author immo
 * 
 */

public class StandardDeferrable implements Deferrable {

	private boolean deferred;

	public StandardDeferrable() {
		this.deferred = false;
	}

	@Override
	public boolean isDeferred() {
		return this.deferred;
	}

	@Override
	public void RequestDeferring() {
		this.deferred = true;
	}

}
