/**
 * DoubleMatrix.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

/**
 * 
 * @author immo
 * 
 *         implements a double matrix of size MxN
 * 
 */

public class DoubleMatrix implements Comparable<DoubleMatrix> {
	private int M, N;
	private double[] m;

	/**
	 * initalize empty matrix
	 * 
	 * @param m
	 *            number of rows
	 * @param n
	 *            number of columns
	 */
	private void initialize(int m, int n) {
		this.M = m;
		this.N = n;
		this.m = new double[this.M * this.N];
	}

	/**
	 * create zero matrix
	 * 
	 * @param Rows
	 * @param Columns
	 */

	public DoubleMatrix(int Rows, int Columns) {
		this.initialize(Rows, Columns);
	}

	enum SpecialMatrix {
		Zero, Block, Unity;
	}

	/**
	 * create a matrix of given kind
	 * 
	 * @param Rows
	 * @param Columns
	 * @param kind
	 */

	public DoubleMatrix(int Rows, int Columns, SpecialMatrix kind) {
		this.initialize(Rows, Columns);
		if (kind == SpecialMatrix.Block) {
			for (int i = 0; i < (this.M * this.N); ++i) {
				this.m[i] = 1.;
			}
		} else if (kind == SpecialMatrix.Unity) {
			for (int i = 0; i < Math.min(this.M, this.N); ++i) {
				this.set(i, i, 1.);
			}
		}
	}

	/**
	 * 
	 * @param x
	 *            0..rows, 1..columns
	 * @return dimension of matrix
	 */

	public int dim(int x) {
		if (x == 0) {
			return this.M;
		}
		if (x == 1) {
			return this.N;
		}
		return -1;
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @return m[row,column]
	 */

	public double get(int row, int column) {
		return this.m[(row * this.N) + column];
	}

	/**
	 * 
	 * sets m[row,column]
	 * 
	 * @param row
	 * @param column
	 * @param x
	 */

	public void set(int row, int column, double x) {
		this.m[(row * this.N) + column] = x;
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
		result = (prime * result) + this.M;
		result = (prime * result) + this.N;
		result = (prime * result) + Arrays.hashCode(this.m);
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
		DoubleMatrix other = (DoubleMatrix) obj;
		if (this.M != other.M) {
			return false;
		}
		if (this.N != other.N) {
			return false;
		}
		if (!Arrays.equals(this.m, other.m)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(DoubleMatrix o) {
		if (this.M != o.M) {
			if (this.M < o.M) {
				return -1;
			}
			return 1;
		}
		if (this.N != o.N) {
			if (this.N < o.N) {
				return -1;
			}
			return 1;
		}
		for (int i = 0; i < (this.M * this.N); ++i) {
			if (this.m[i] < o.m[i]) {
				return -1;
			}
			if (this.m[i] > o.m[i]) {
				return 1;
			}
		}
		return 0;
	}

}
