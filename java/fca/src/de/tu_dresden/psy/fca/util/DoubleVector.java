/**
 * DoubleVector.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import java.util.Arrays;
import java.util.BitSet;

/**
 * 
 * @author immo
 * 
 *         implements a double vector of size k
 * 
 */

public class DoubleVector implements Comparable<DoubleVector> {

	private int k;

	/**
	 * vector value
	 */

	public double[] x;

	/**
	 * zero vector
	 * 
	 * @param dim
	 */
	public DoubleVector(int dim) {
		this.k = dim;
		this.x = new double[dim];
	}

	/**
	 * given vector
	 * 
	 * @param dim
	 * @param initial
	 */
	public DoubleVector(int dim, double[] initial) {
		this.k = dim;
		this.x = new double[dim];
		for (int i = 0; i < this.k; ++i) {
			this.x[i] = initial[i];
		}
	}

	/**
	 * given vector
	 * 
	 * @param dim
	 * @param initial
	 */
	public DoubleVector(int dim, BitSet initial) {
		this.k = dim;
		this.x = new double[dim];
		for (int i = 0; i < this.k; ++i) {
			if (initial.get(i)) {
				this.x[i] = 1.;
			}
		}
	}

	/**
	 * e-th unit vector
	 * 
	 * @param dim
	 * @param e
	 */

	public DoubleVector(int dim, int e) {
		this.k = dim;
		this.x = new double[dim];
		this.x[e] = 1.;
	}

	/**
	 * 
	 * @return dimension of the vector
	 */

	public int dim() {
		return this.k;
	}

	@Override
	public int compareTo(DoubleVector o) {
		if (o.k != this.k) {
			if (this.k < o.k) {
				return -1;
			}

			return 1;
		}

		for (int i = 0; i < this.k; ++i) {
			if (this.x[i] < o.x[i]) {
				return -1;
			}
			if (this.x[i] > o.x[i]) {
				return 1;
			}
		}
		return 0;
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
		result = (prime * result) + this.k;
		result = (prime * result) + Arrays.hashCode(this.x);
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
		DoubleVector other = (DoubleVector) obj;
		if (this.k != other.k) {
			return false;
		}
		if (!Arrays.equals(this.x, other.x)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[");

		for (int c = 0; c < this.k; ++c) {
			b.append(this.x[c] + "\t");
		}
		b.append("]\n");

		return b.toString();
	}
}
