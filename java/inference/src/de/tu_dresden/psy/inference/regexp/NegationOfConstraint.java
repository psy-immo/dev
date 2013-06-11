/**
 * NegationOfConstraint.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp;

import java.util.Vector;

import de.tu_dresden.psy.inference.AssertionInterface;

/**
 * implements a constraint interface that negates the result of a given
 * constraint interface
 * 
 * @author immo
 * 
 */

public class NegationOfConstraint implements ConstraintInterface {

	private ConstraintInterface constraint;

	/**
	 * 
	 * @param c
	 *            ConstraintInterface whose .check() function is negated
	 */

	public NegationOfConstraint(ConstraintInterface c) {
		this.constraint = c;
	}

	@Override
	public boolean check(Vector<AssertionInterface> premises) {

		return !this.constraint.check(premises);
	}

}
