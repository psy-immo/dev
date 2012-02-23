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
	
	public static enum AssertionPart {
		subject, predicate, object;
	}
	
	public static Object getAssertionPart(AssertionInterface assertion, AssertionPart part) {
		if (part == AssertionPart.subject)
			return assertion.getSubject();
		
		if (part == AssertionPart.predicate)
			return assertion.getPredicate();
		
		return assertion.getObject();
	}

	private boolean old;
	
	@Override
	public boolean isOld() {
		return old;
	}
	
	@Override
	public void markAsOld() {
		old = true;
	}
	
	private Object s, o, p;
	private int hashS, hashO, hashP;
	private int hashCode;

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
		hashS = s.hashCode();
		hashP = p.hashCode();
		hashO = o.hashCode();
		old = false;
		
		
		hashCode = 1;
		hashCode = 31 * hashCode + (hashO);
		hashCode = 31 * hashCode + (hashP);
		hashCode = 31 * hashCode + (hashS);
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
		hashS = s.hashCode();
		hashP = p.hashCode();
		hashO = 0;
		
		hashCode = 1;
		hashCode = 31 * hashCode + (hashO);
		hashCode = 31 * hashCode + (hashP);
		hashCode = 31 * hashCode + (hashS);
		old = false;
	}

	/**
	 * create assertion from string, of the form subject+"·"+predicate or
	 * subject+"·"+predicate+"·"+object
	 * 
	 * @param fromString
	 */

	public Assertion(String fromString) {
		old = false;
		
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
		
		hashS = s.hashCode();
		hashP = p.hashCode();
		hashO = o.hashCode();
		
		hashCode = 1;
		hashCode = 31 * hashCode + (hashO);
		hashCode = 31 * hashCode + (hashP);
		hashCode = 31 * hashCode + (hashS);
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
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
	
		if (getClass() != obj.getClass())
			return false;		
		
		return isEqualTo((Assertion)obj);
	}

	@Override
	public String toString() {

		return s.toString() + "·" + p.toString() + "·" + o.toString();
	}

	@Override
	public boolean isEqualTo(AssertionInterface assertion) {
		if (this == assertion)
			return true;
		if (assertion == null)
			return false;
		
		if (hashP != assertion.getPredicate().hashCode())
			return false;
		if (hashS != assertion.getSubject().hashCode())
			return false;
		if (hashO != assertion.getObject().hashCode())
			return false;
		
		if (o == null) {
			if (assertion.getObject() != null)
				return false;
		} else if (!o.equals(assertion.getObject()))
			return false;
		if (p == null) {
			if (assertion.getPredicate() != null)
				return false;
		} else if (!p.equals(assertion.getPredicate()))
			return false;
		if (s == null) {
			if (assertion.getSubject() != null)
				return false;
		} else if (!s.equals(assertion.getSubject()))
			return false;
		return true;		
	}

	@Override
	public boolean isPremise(AssertionInterface assertion) {
		return false;
	}

}
