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
	 * common initialization code for various constructors
	 */

	private void initialize() {
		this.premise = new TreeSet<Integer>();
		this.conclusion = new TreeSet<Integer>();
	}

	/**
	 * constructs the empty hyper edge
	 */
	public DirectedHyperEdge() {
		this.initialize();
	}

}
