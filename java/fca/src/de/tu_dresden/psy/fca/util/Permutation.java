/**
 * Permutation.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.fca.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author immo
 * 
 *         each instance is a permutation on the set of natural numbers
 * 
 */

public class Permutation {
	private Map<Integer, Integer> sigma, inverse;

	/**
	 * creates a new identity permutation object
	 */
	public Permutation() {
		this.sigma = new TreeMap<Integer, Integer>();
		this.inverse = new TreeMap<Integer, Integer>();
	}

	/**
	 * creates a random permutation between the numbers 0,...,N-1
	 * 
	 * @param N
	 */

	public Permutation(int N) {
		Random rnd = new Random();
		BitSet left_to_use = new BitSet(N);
		left_to_use.set(0, N);

		this.sigma = new TreeMap<Integer, Integer>();
		this.inverse = new TreeMap<Integer, Integer>();

		for (int n = 0; n < N; ++n) {
			int N_left = left_to_use.cardinality();
			int which = rnd.nextInt(N_left);
			int sigma_n = left_to_use.nextSetBit(0);
			for (int i = 0; i < which; ++i) {
				sigma_n = left_to_use.nextSetBit(sigma_n + 1);
			}

			if (sigma_n < 0) {
				throw new RuntimeException("Assertion: sigma_n < 0!");
			}

			left_to_use.clear(sigma_n);

			this.sigma.put(n, sigma_n);
			this.inverse.put(sigma_n, n);
		}
	}

	/**
	 * creates a new permutation such that the image of index i is represented
	 * by the (i-1)th iterator value
	 * 
	 * @param images
	 */

	public Permutation(Iterator<Integer> images) {
		this.sigma = new TreeMap<Integer, Integer>();
		this.inverse = new TreeMap<Integer, Integer>();

		int x = 0;
		while (images.hasNext()) {
			int sigma_x = images.next();
			if (this.inverse.containsKey(sigma_x)) {
				throw new RuntimeException(
						"Permutation-image list contains double image: "
								+ sigma_x);
			}
			this.sigma.put(x, sigma_x);
			this.inverse.put(sigma_x, x);
			++x;
		}
	}

	/**
	 * create a permutation that reorders according to a reorder image
	 * 
	 * @param new_order
	 * @param start
	 * @param exclusiveEnd
	 */

	public Permutation(ArrayList<Integer> new_order, int start, int exclusiveEnd) {
		this.sigma = new TreeMap<Integer, Integer>();
		this.inverse = new TreeMap<Integer, Integer>();

		for (int i = start; i < exclusiveEnd; ++i) {
			this.sigma.put(i, new_order.get(i));
			this.inverse.put(new_order.get(i), i);
		}
	}

	/**
	 * 
	 * @param x
	 * @return sigma(x)
	 */
	public int Forward(int x) {
		if (this.sigma.containsKey(x)) {
			return this.sigma.get(x);
		}
		return x;
	}

	/**
	 * 
	 * @param x
	 * @return sigma^-1(x)
	 */
	public int Backward(int x) {
		if (this.inverse.containsKey(x)) {
			return this.inverse.get(x);
		}
		return x;
	}

	/**
	 * 
	 * @return the inverse of this permutation
	 */

	public Permutation Inverse() {
		Permutation inv = new Permutation();
		for (Integer n: this.sigma.keySet()) {
			inv.inverse.put(n, this.sigma.get(n));
			inv.sigma.put(this.sigma.get(n), n);
		}
		return inv;
	}

	/**
	 * change this permutation by right side multiplying (x y)
	 * 
	 * @param x
	 * @param y
	 */

	public void rightSwap(int x, int y) {
		int inv_x = this.Backward(x);
		int inv_y = this.Backward(y);
		this.sigma.put(inv_x, y);
		this.sigma.put(inv_y, x);
		this.inverse.put(x, inv_y);
		this.inverse.put(y, inv_x);
	}

	/**
	 * change this permutation by right side multiplying (x y)
	 * 
	 * @param x
	 * @param y
	 */

	public void leftSwap(int x, int y) {
		int sigma_x = this.Forward(x);
		int sigma_y = this.Forward(y);
		this.sigma.put(x, sigma_y);
		this.sigma.put(y, sigma_x);
		this.inverse.put(sigma_x, y);
		this.inverse.put(sigma_y, x);
	}

	/**
	 * 
	 * returns a new permutation that is this object right side multiplied with
	 * right
	 * 
	 * @param right
	 */

	public Permutation andThen(Permutation right) {
		Permutation product = new Permutation();
		Set<Integer> keys = new TreeSet<Integer>(this.sigma.keySet());

		keys.addAll(right.sigma.keySet());

		for (Integer i : keys) {
			int sigma_i = right.Forward(this.Forward(i));
			if (i != sigma_i) {
				product.sigma.put(i, sigma_i);
				product.inverse.put(i, sigma_i);
			}
		}
		return product;
	}

	/**
	 * 
	 * returns a new permutation that is this object left side multiplied with
	 * left
	 * 
	 * @param left
	 */

	public Permutation after(Permutation left) {
		Permutation product = new Permutation();
		Set<Integer> keys = new TreeSet<Integer>(this.sigma.keySet());

		keys.addAll(left.sigma.keySet());

		for (Integer i : keys) {
			int sigma_i = this.Forward(left.Forward(i));
			if (i != sigma_i) {
				product.sigma.put(i, sigma_i);
				product.inverse.put(i, sigma_i);
			}
		}
		return product;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();

		int count = 1;

		for (Integer n : this.sigma.keySet()) {
			s.append("σ(" + n + ")=" + this.sigma.get(n));
			if ((count % 5) == 0) {
				s.append("\n");
			} else {
				s.append(", \t");
			}
			++count;
		}

		return s.toString();
	}
}
