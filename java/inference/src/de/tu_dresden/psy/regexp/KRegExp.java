/**
 * KRegExp.java, (c) 2012, Immanuel Albrecht; Dresden University of Technology,
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

package de.tu_dresden.psy.regexp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * implements k-regular-expressions-in-a-row pattern matching
 * 
 * @author albrecht
 * 
 */

public class KRegExp implements StringSplitter {

	private Pattern[] patterns;

	private Vector<Vector<Integer>> boundaries;

	private int[] bounds;

	/**
	 * default constructor
	 */

	public KRegExp() {
		this.patterns = new Pattern[0];
		this.boundaries = null;
		this.bounds = null;
	}

	/**
	 * construct chain matcher
	 * 
	 * @param regexps
	 *            pattern chain
	 */

	public KRegExp(String[] regexps) {
		this.setPattern(regexps);
	}
	/**
	 * construct a chain matcher
	 * @param delimitedRegexps  series of regular expressions delimited by '·'
	 */
	public KRegExp(String delimitedRegexps) {
		this.setPattern(delimitedRegexps.split("·"));		
	}

	/**
	 * 
	 * compile patterns given
	 * 
	 * @param regexps
	 *            pattern chain
	 */
	public void setPattern(String[] regexps) {
		this.patterns = new Pattern[regexps.length];
		for (int i = 0; i < regexps.length; i++) {
			this.patterns[i] = Pattern.compile(regexps[i]);
		}
		this.boundaries = null;
		this.bounds = null;
	}

	/**
	 * reset the boundaries member variable
	 * 
	 * @param stringSize
	 */
	private void resetBoundariesAndBounds(int stringSize) {

		if (boundaries == null) {

			boundaries = new Vector<Vector<Integer>>(patterns.length + 1);
			boundaries.setSize(patterns.length + 1);

			for (int i = 0; i < patterns.length + 1; ++i) {
				boundaries.set(i, new Vector<Integer>());
			}

			boundaries.get(0).add(0);
			boundaries.get(patterns.length).add(stringSize);

			bounds = new int[patterns.length + 1];
			bounds[0] = 0;
			bounds[patterns.length] = 0;

		} else {
			for (int i = 1; i < patterns.length; ++i) {
				boundaries.get(i).removeAllElements();
			}
			boundaries.get(patterns.length).set(0, stringSize);
		}

	}

	/**
	 * tries to adjust all indices after leftIndex to meet the monotonicity
	 * criteria
	 * 
	 * @param leftIndex
	 * @return false, if monotonicity cannot be met
	 */

	private boolean adjustRightBounds(int leftIndex) {
		if (leftIndex >= patterns.length)
			return true;

		int index = leftIndex + 1;

		bounds[index] = 0;

		Vector<Integer> possibleBounds = boundaries.get(index);
		int maxBound = possibleBounds.size();

		int lessOrEqual = boundaries.get(leftIndex).get(bounds[leftIndex]);

		while (possibleBounds.get(bounds[index]) < lessOrEqual) {
			bounds[index]++;

			if (bounds[index] == maxBound) {
				/**
				 * monotonicity cannot be met
				 */
				return false;
			}
		}
		/**
		 * now adjust the following indices
		 */

		return adjustRightBounds(index);
	}

	/**
	 * calculates the next possible chopping according to lexocographic-order
	 * 
	 * @return false, is there is no next chopping
	 */

	private boolean getNextBounds() {
		for (int increment = patterns.length - 1; increment > 0; --increment) {
			bounds[increment] += 1;

			if (bounds[increment] >= boundaries.get(increment).size()) {
				/**
				 * may not increment this index because of vector size
				 */
				continue;
			}

			if (adjustRightBounds(increment) == false) {
				/**
				 * may not increment this index because of unmeetable
				 * monotonicity
				 */
				continue;
			}

			/**
			 * just incremented this index and adjusted right bounds, we are
			 * done for now
			 */

			return true;
		}

		return false;

	}

	private static class intPair {
		int a, b;

		public intPair(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + a;
			result = prime * result + b;
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
			intPair other = (intPair) obj;
			if (a != other.a)
				return false;
			if (b != other.b)
				return false;
			return true;
		}

		

	}

	/**
	 * match chain-pattern, return results
	 * 
	 * 
	 * @param chain
	 *            candidate to match
	 * @return set of arrays with start indices for a valid chain match
	 */
	public Set<int[]> match(String chain) {

		int chain_length = chain.length();

		resetBoundariesAndBounds(chain_length);

		Set<int[]> results = new HashSet<int[]>();

		/**
		 * find sub-patterns
		 */

		for (int i = 1; i < patterns.length; ++i) {
			Pattern pattern = patterns[i];

			Matcher matcher = pattern.matcher(chain);

			int start = 0;

			while (matcher.find(start) == true) {
				int pos = matcher.start();

				boundaries.get(i).add(pos);

				start = pos + 1;
				if (start >= chain_length)
					break;
			}

			if (start == 0) {
				/**
				 * no match
				 */

				return results;
			}
		}

		/**
		 * find all possible string choppings
		 */

		if (adjustRightBounds(0) == false) {
			/**
			 * no possible chopping
			 */

			return results;
		}

		boolean do_again = true;

		Vector<int[]> possible_bounds = new Vector<int[]>();

		while (do_again) {
			possible_bounds.add(bounds.clone());

			do_again = getNextBounds();
		}

		/**
		 * check for matching
		 */

		@SuppressWarnings("unchecked")
		Map<intPair, Boolean>[] map_cache = new HashMap[patterns.length];

		for (int i = 0; i < patterns.length; ++i) {
			map_cache[i] = new HashMap<intPair, Boolean>();

		}

		for (Iterator<int[]> it = possible_bounds.iterator(); it.hasNext();) {
			int[] bounds = it.next();
			for (int i = 0; i < patterns.length; ++i) {
				intPair p = new intPair(bounds[i], bounds[i + 1]);

				if (map_cache[i].containsKey(p) == false) {
					map_cache[i].put(
							p,
							patterns[i].matcher(
									chain.substring(
											boundaries.get(i).get(bounds[i]),
											boundaries.get(i + 1).get(
													bounds[i + 1]))).matches());
				}
			}
		}

		/**
		 * fill the result set
		 */
		for (Iterator<int[]> it = possible_bounds.iterator(); it.hasNext();) {
			int[] bounds = it.next();
			boolean good = true;
			for (int i = 0; i < patterns.length; ++i) {
				intPair p = new intPair(bounds[i], bounds[i + 1]);
				if (map_cache[i].get(p) == false) {
					good = false;
					break;
				}
			}
			if (good) {
				int[] result = new int[patterns.length];
				for (int i = 0; i < patterns.length; ++i) {
					result[i] = boundaries.get(i).get(bounds[i]);
				}

				results.add(result);
			}
		}

		/**
		 * now bounds contains the lexicographic-order-first possible chopping
		 */

		return results;
	}

	/**
	 * use chain pattern match to split given String into its matched parts
	 * 
	 * @param chain   candidate to split
	 * @return  possible splittings
	 */
	public Set<String[]> split(String chain) {
		Set<int[]> boundaries = match(chain);
		
		Set<String[]> results = new HashSet<String[]>();
		
		for (Iterator<int[]> it = boundaries.iterator(); it.hasNext();) {
			int[] bounds =  it.next();
			
			String[] parts = new String[bounds.length];
			for (int i=0;i<bounds.length-1;++i) {
				parts[i] = chain.substring(bounds[i],bounds[i+1]);
			}
			if (bounds.length > 0) {
				parts[bounds.length-1] = chain.substring(bounds[bounds.length-1]);
			}
			
			results.add(parts);
		}
		
		return results;
	}
	
	/**
	 * testing routine
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		KRegExp kregexp = new KRegExp(new String[] { "ab*", "b+c*b*", "c*" });
		Set<int[]> matches = kregexp
				.match("abbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
//		Iterator<int[]> it = matches.iterator();
//		while (it.hasNext()) {
//			int[] bounds = it.next();
//			System.out.print("[");
//			for (int i = 0; i < bounds.length; ++i)
//				System.out.print(bounds[i] + ",");
//			System.out.println("]");
//		}
		System.out.println("Total: " + matches.size());

	}

}
