/**
 * Annotated.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.forms;

/**
 * implements an annotation for some Atoms type
 * 
 * @author immo
 * 
 * @param <Atoms>
 */

public class Annotated<Atoms> {

	/**
	 * 
	 * enum that lists all possible annotations
	 * 
	 * @author immo
	 * 
	 */
	public static enum Annotations {
		unannotated, trivial
	};

	/**
	 * store the atom
	 */

	private Atoms atom;

	/**
	 * store the annotation
	 */

	private Annotations annotiation;

	/**
	 * unannotated constructor
	 * 
	 * @param atom
	 */

	public Annotated(Atoms atom) {
		this.atom = atom;
		this.annotiation = Annotations.unannotated;
	}

	/**
	 * standard annotation constructor
	 * 
	 * @param atom
	 * @param annotation
	 */

	public Annotated(Atoms atom, Annotations annotation) {
		this.atom = atom;
		this.annotiation = annotation;
	}

	/**
	 * 
	 * @return the atom object
	 */

	public Atoms getAtom() {
		return this.atom;
	}

	/**
	 * 
	 * @return the annotation object
	 */

	public Annotations getAnnotiation() {
		return this.annotiation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.annotiation == null) ? 0 : this.annotiation.hashCode());
		result = (prime * result)
				+ ((this.atom == null) ? 0 : this.atom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Annotated)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		Annotated other = (Annotated) obj;
		if (this.annotiation != other.annotiation) {
			return false;
		}
		if (this.atom == null) {
			if (other.atom != null) {
				return false;
			}
		} else if (!this.atom.equals(other.atom)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (this.annotiation == Annotations.trivial) {
			return this.atom.toString() + "'";
		} else {
			return this.atom.toString();
		}
	}

}
