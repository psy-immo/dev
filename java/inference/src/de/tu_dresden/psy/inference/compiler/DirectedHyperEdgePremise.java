/**
 * DirectedHyperEdgePremise.java, (c) 2013, Immanuel Albrecht; Dresden
 * University of Technology, Professur für die Psychologie des Lernen und
 * Lehrens
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

package de.tu_dresden.psy.inference.compiler;

import java.util.Set;

/**
 * implements a premise of a directed hyper edge
 * 
 * @author immo
 * 
 */

public class DirectedHyperEdgePremise {

	/**
	 * premise or source of the hyper edge
	 */
	private Set<Integer> premise;

	/**
	 * is this a trivial rule, in this case we should not merge the conclusions
	 * with non-trivial rules, so we keep track of that fact, too.
	 */
	private boolean isTrivial;

	/**
	 * 
	 * @param premise
	 */

	public DirectedHyperEdgePremise(Set<Integer> premise, boolean trivial) {
		this.premise = premise;
		this.isTrivial = trivial;
	}

	/**
	 * 
	 * @return premise
	 */

	public Set<Integer> getPremise() {
		return this.premise;
	}

	/**
	 * 
	 * @return true, if this premise consists of assertions marked trivial
	 */
	public boolean isTrivial() {
		return this.isTrivial;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (this.isTrivial ? 1231 : 1237);
		result = (prime * result)
				+ ((this.premise == null) ? 0 : this.premise.hashCode());
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
		if (!(obj instanceof DirectedHyperEdgePremise)) {
			return false;
		}
		DirectedHyperEdgePremise other = (DirectedHyperEdgePremise) obj;
		if (this.isTrivial != other.isTrivial) {
			return false;
		}
		if (this.premise == null) {
			if (other.premise != null) {
				return false;
			}
		} else if (!this.premise.equals(other.premise)) {
			return false;
		}
		return true;
	}


}
