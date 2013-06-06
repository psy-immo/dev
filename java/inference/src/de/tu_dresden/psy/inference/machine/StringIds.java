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
	/** stores unified string as key, its id as value */
	private Map<String, Integer> toId;
	/** stores id as key, its pre-unified version as value */
	private Map<Integer, String> toCaseCorrectString;
	/**
	 * the next fresh id
	 */
	private int currentId;

	public StringIds() {
		this.assertionDomain = new ArrayList<ArrayList<ArrayList<String>>>();

		this.toId = new HashMap<String, Integer>();
		this.toCaseCorrectString = new HashMap<Integer, String>();

		this.currentId = 0;
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

		if (factorwise.isEmpty()) {
			/** empty product */
			return;
		}
		for (ArrayList<String> factor : factorwise) {
			if (factor.isEmpty()) {
				/** empty product */
				return;
			}
		}

		/**
		 * we make a deep copy of the factor-product
		 */

		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> deep_copy = (ArrayList<ArrayList<String>>) factorwise
		.clone();

		this.assertionDomain.add(deep_copy);

		/**
		 * furthermore, we add the concatenated strings to the database
		 */

		/** permutation vector */
		int[] sigma = new int[deep_copy.size()];

		int stop_criterion = deep_copy.get(0).size();

		while (sigma[0] < stop_criterion) {
			/**
			 * add the string corresponding to sigma
			 */

			String concat = "";

			for (int i = 0; i < sigma.length; ++i) {
				concat += deep_copy.get(i).get(sigma[i]);
			}

			this.addString(concat);

			/**
			 * next permutation
			 */

			sigma[sigma.length - 1] += 1;
			for (int i = sigma.length - 1; i > 0; --i) {
				if (sigma[i] >= deep_copy.get(i).size()) {
					sigma[i] = 0;
					sigma[i - 1] += 1;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * Adds a string to the string id mapping, using a new id number if
	 * necessary.
	 * 
	 * @param s
	 */

	public void addString(String s) {
		String unified = unifyString(s);

		if (this.toId.containsKey(unified) == false) {
			this.toId.put(unified, this.currentId);
			this.toCaseCorrectString.put(this.currentId, s);
			this.currentId += 1;
		}
	}

	/**
	 * 
	 * @param id
	 * @return the (correct cased) string that corresponds to the given id
	 */

	public String fromId(int id) {
		return this.toCaseCorrectString.get(id);
	}

	/**
	 * 
	 * @param s
	 * @return the id that corresponds to unifyString(s)
	 */

	public int fromString(String s) {
		String unified = StringIds.unifyString(s);

		return this.toId.get(unified);
	}

	/**
	 * 
	 * Test routines
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StringIds ids = new StringIds();
		ArrayList<ArrayList<String>> dbl_list = new ArrayList<ArrayList<String>>();

		dbl_list.add(new ArrayList<String>());

		dbl_list.get(0).add("A");
		dbl_list.get(0).add("B");
		dbl_list.get(0).add("C");

		dbl_list.add(new ArrayList<String>());

		dbl_list.get(1).add("A");
		dbl_list.get(1).add("B");
		dbl_list.get(1).add("C");

		dbl_list.add(new ArrayList<String>());

		dbl_list.get(2).add("A");
		dbl_list.get(2).add("B");
		dbl_list.get(2).add("C");

		ids.addStringProduct(dbl_list);

		for (int i = 0; i <= (3 * 3 * 3); ++i) {
			System.out.print(i);
			System.out.print(" = ");
			System.out.println(ids.fromId(i));
		}

	}
}
