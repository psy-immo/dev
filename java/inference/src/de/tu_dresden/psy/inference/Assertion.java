/**
 * Assertion.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

/**
 * provides a generic assertion class
 * 
 * @author albrecht
 */

public class Assertion implements AssertionInterface {

	private Object s, o, p;

	/**
	 * create assertion from subject, predicate and object
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */

	public Assertion(Object subject, Object predicate, Object object) {
		s = subject;
		p = predicate;
		o = object;
	}

	/**
	 * create assertion from subject and predicate with default object
	 * <b>null</b>
	 * 
	 * @param subject
	 * @param predicate
	 */

	public Assertion(Object subject, Object predicate) {
		o = null;
		s = subject;
		p = predicate;
	}

	/**
	 * create assertion from string, of the form subject+"·"+predicate or
	 * subject+"·"+predicate+"·"+object
	 * 
	 * @param fromString
	 */

	public Assertion(String fromString) {
		String[] parts = fromString.split("·");

		if (parts.length == 2) {
			o = null;
			s = parts[0];
			p = parts[1];
		} else if (parts.length == 3) {
			o = parts[2];
			s = parts[0];
			p = parts[1];
		} else

		throw new RuntimeException(
				"Assertion must be either SUBJECT·PREDICATE or SUBJECT·PREDICATE·OBJECT: "+fromString);
	}

	@Override
	public Object getSubject() {

		return s;
	}

	@Override
	public Object getObject() {

		return o;
	}

	@Override
	public Object getPredicate() {

		return p;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assertion other = (Assertion) obj;
		if (o == null) {
			if (other.o != null)
				return false;
		} else if (!o.equals(other.o))
			return false;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return s.toString() + "·" + p.toString() + "·" + o.toString();
	}

}
