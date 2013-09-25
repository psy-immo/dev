/**
 * Union.java, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

package de.tu_dresden.psy.util;

/**
 * implements a union object, i.e. an object of either type A or type B
 * 
 * @author immo
 * 
 * @param <A>
 *            any type
 * @param <B>
 *            any type
 */

public class Union<A, B> {

	private boolean isTypeA;
	private A a;
	private B b;

	public boolean isA() {
		return this.isTypeA;
	}

	public boolean isB() {
		return this.isTypeA == false;
	}

	public A getA() {
		return this.a;
	}

	public B getB() {
		return this.b;
	}

	public Union() {
		this.a = null;
		this.b = null;
		this.isTypeA = false;
	}

	/**
	 * make this object a representant of type A
	 * 
	 * @param a
	 * @return this
	 */

	public Union<A, B> setA(A a) {
		this.isTypeA = true;
		this.a = a;
		this.b = null;

		return this;
	}

	/**
	 * make this object a representant of type B
	 * 
	 * @param b
	 * @return this
	 */

	public Union<A, B> setB(B b) {
		this.isTypeA = false;
		this.a = null;
		this.b = b;

		return this;
	}

}
