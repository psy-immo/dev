/**
 * DirectedHyperEdge.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.compiler;

import java.util.Set;
import java.util.TreeSet;

/**
 * implements a directed hyper edge:
 * 
 * For a graph with a vertex set V, a hyper edge is a subset of V, whereas a
 * directed hyper edge consists of two disjoint subsets of V: the premise and
 * the conclusion
 * 
 * 
 * @author immo
 * 
 */
public class DirectedHyperEdge {

	/**
	 * premise or source of the hyper edge
	 */
	private Set<Integer> premise;

	/**
	 * conclusion or target of the hyper edge
	 */
	private Set<Integer> conclusion;

	/**
	 * flag this inference rule as trivial
	 */

	private boolean isTrivialRule;

	/**
	 * common initialization code for various constructors
	 */

	private void initialize() {
		this.premise = new TreeSet<Integer>();
		this.conclusion = new TreeSet<Integer>();
		this.isTrivialRule = false;
	}

	/**
	 * 
	 * @return true, if this rule is considered to be trivial; e.g. A equals B
	 *         --> B equals A
	 */

	public boolean isTrivial() {
		return this.isTrivialRule;
	}

	/**
	 * sets the triviality parameter of this rule
	 * 
	 * @param isTrivial
	 */

	public void setTriviality(boolean isTrivial) {
		this.isTrivialRule = isTrivial;
	}

	/**
	 * constructs the empty hyper edge
	 */
	public DirectedHyperEdge() {
		this.initialize();
	}

	/**
	 * empty premise, sigleton target
	 * 
	 * @param target
	 */
	public DirectedHyperEdge(int target) {
		this.initialize();
		this.conclusion.add(target);
	}

	public DirectedHyperEdge(Set<Integer> source, Set<Integer> target) {
		this.initialize();
		this.conclusion.addAll(target);
		this.premise.addAll(source);
	}

	/**
	 * add a vertex to the premise set
	 * 
	 * @param v
	 */

	public void addPremise(int v) {
		this.premise.add(v);
	}

	/**
	 * add a vertex to the premise set
	 * 
	 * @param v
	 */

	public void addConclusion(int v) {
		this.conclusion.add(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.conclusion == null) ? 0 : this.conclusion.hashCode());
		result = (prime * result)
				+ ((this.premise == null) ? 0 : this.premise.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		DirectedHyperEdge other = (DirectedHyperEdge) obj;
		if (this.conclusion == null) {
			if (other.conclusion != null) {
				return false;
			}
		} else if (!this.conclusion.equals(other.conclusion)) {
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

	public DirectedHyperEdgePremise getPremise() {
		return new DirectedHyperEdgePremise(this.premise, this.isTrivialRule);
	}

	public Set<Integer> getConclusions() {
		return this.conclusion;
	}

}
