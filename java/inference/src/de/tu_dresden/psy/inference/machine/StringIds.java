/**
 * StringIds.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * implements the conversion of strings to table ids
 * 
 * @author immo
 * 
 */

public class StringIds {

	/**
	 * the building instructions for the assertion domain components
	 */
	private ArrayList<ArrayList<ArrayList<String>>> assertionDomain;

	/**
	 * the string vs. id bijection
	 */
	private Map<String, Integer> toId;
	private Map<Integer, String> toString;

	public StringIds() {
		this.assertionDomain = new ArrayList<ArrayList<ArrayList<String>>>();

		this.toId = new HashMap<String, Integer>();
		this.toString = new HashMap<Integer, String>();
	}

	/**
	 * 
	 * implements the behaviour from stringids.js: var unified =
	 * s.toUpperCase().trim();
	 * 
	 * @param x
	 *            to be unified
	 * @return a unified version of the string
	 */

	public static String unifyString(String x) {
		return x.toUpperCase().trim();
	}

	/**
	 * add a Cartesian concatenation of sets of strings to the assertion domain
	 * 
	 * @param factorwise
	 *            an array of factors
	 * 
	 *            factorwise = [ Factor1, Factor2, ... ]
	 * 
	 *            where each Factori = [Element1i, Element2i, ...]
	 * 
	 *            then we add the following concatenated strings:
	 * 
	 *            Element1i . Elemente2j . ... . ElementXz
	 * 
	 *            which is the pointwise concatenation of the factor sets
	 * 
	 */
	public void addStringProduct(ArrayList<ArrayList<String>> factorwise) {

		/**
		 * we make a deep copy of the factor-product
		 */

		ArrayList<ArrayList<String>> deep_copy = (ArrayList<ArrayList<String>>) factorwise
				.clone();

		this.assertionDomain.add(deep_copy);

		/**
		 * furthermore, we add the concatenated strings to the database
		 */
	}

}
