/**
 * BitSetMatrix.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;

/**
 * 
 * @author immo
 * 
 *         class that implements a n×m-matrix with codomain {0,1}
 * 
 */

public class BitSetMatrix implements Comparable<BitSetMatrix> {
	private int rows, columns;
	private BitSet m;

	private void initialize(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.m = new BitSet(rows * columns);
	}

	/**
	 * creates a zero bit-set matrix
	 * 
	 * @param rows
	 * @param columns
	 */

	public BitSetMatrix(int rows, int columns) {
		this.initialize(rows, columns);
	}

	/**
	 * creates a filled BitSet Matrix
	 * 
	 * @param rows
	 * @param columns
	 * @param row_vectors
	 *            iterator over the row vectors
	 * @param column_offset
	 *            offset to start with in the row vectors
	 */

	public BitSetMatrix(int rows, int columns, Iterator<BitSet> row_vectors,
			int column_offset) {
		this.initialize(rows, columns);
		for (int row = 0; row < rows; ++row) {
			BitSet v = row_vectors.next();

			for (int c = v.nextSetBit(0); (c >= 0)
					&& (c < (columns + column_offset)); c = v.nextSetBit(c + 1)) {
				if (c < column_offset) {
					continue;
				}
				int column = c - column_offset;
				this.m.set(column + (row * this.columns));
			}
		}
	}

	/**
	 * private copy constructor
	 * 
	 * @param o BitSetMatrix that is copied
	 */

	private BitSetMatrix(BitSetMatrix o) {
		this.rows = o.rows;
		this.columns = o.columns;
		this.m = (BitSet) o.m.clone();
	}

	/**
	 * 
	 * @return a new copy of this BitSetMatrix
	 */

	public BitSetMatrix Copy() {
		return new BitSetMatrix(this);
	}


	/**
	 * creates a bit-set matrix that is filled with initial content
	 * 
	 * @param rows
	 * @param columns
	 * @param values
	 *            content of the matrix as consecutive row vectors (i.e.
	 *            values.get(column+row*columns) will determine the value for
	 *            m[row,column]
	 */

	public BitSetMatrix(int rows, int columns, BitSet values) {
		this.initialize(rows, columns);
		this.m.or(values);
	}

	/**
	 * 
	 * @param row
	 * @param column
	 * @return the value of the matrix cell
	 */

	public boolean get(int row, int column) {
		return this.m.get(column + (row * this.columns));
	}

	/**
	 * fill the matrix with random values
	 * 
	 * @param rate
	 *            rate of 1's
	 */

	public void RandomizeMatrix(double rate) {
		Random rnd = new Random();
		for (int i = 0; i < (this.rows * this.columns); ++i) {
			if (rnd.nextDouble() < rate) {
				this.m.set(i);
			} else {
				this.m.clear(i);
			}
		}
	}

	/**
	 * 
	 * sets the value of the matrix cell
	 * 
	 * @param row
	 * @param column
	 * @param value
	 * 
	 */

	public void set(int row, int column, boolean value) {
		if (value) {
			this.m.set(column + (row * this.columns));
		} else {
			this.m.clear(column + (row * this.columns));
		}
	}

	/**
	 * moves the contents of row A to row B and vice versa
	 * 
	 * @param rowA
	 * @param rowB
	 */

	public void swapRows(int rowA, int rowB) {
		BitSet A = this.m.get(rowA * this.columns, (rowA * this.columns)
				+ this.columns);
		BitSet BxorA = this.m.get(rowB * this.columns, (rowB * this.columns)
				+ this.columns);

		BxorA.xor(A);

		/**
		 * B xor A is the bit vector that contains the columns that differ,
		 * hence must be flipped
		 */

		for (int i = BxorA.nextSetBit(0); i >= 0; i = BxorA.nextSetBit(i + 1)) {
			int j = BxorA.nextClearBit(i);
			this.m.flip(i + (rowA * this.columns), j + (rowA * this.columns));
			this.m.flip(i + (rowB * this.columns), j + (rowB * this.columns));
			i = j;
		}
	}

	/**
	 * moves the contents of column A to column B and vice versa
	 * 
	 * @param colA
	 * @param colB
	 */

	public void swapColumns(int colA, int colB) {
		for (int r = 0; r < this.rows; ++r) {
			if (this.m.get(colA + (r * this.columns)) != this.m.get(colB
					+ (r * this.columns))) {
				this.m.flip(colA + (r * this.columns));
				this.m.flip(colB + (r * this.columns));
			}
		}
	}

	@Override
	public int compareTo(BitSetMatrix o) {

		/**
		 * xor with this.compareTo(this) will delete the information!
		 */

		if (this == o) {
			return 0;
		}

		/**
		 * smaller dimension vector -> smaller matrix
		 */
		if (this.rows < o.rows) {
			return -1;
		}
		if (this.rows > o.rows) {
			return 1;
		}
		if (this.columns < o.columns) {
			return -1;
		}
		if (this.columns > o.columns) {
			return 1;
		}

		/**
		 * find first index where matrices are different
		 */

		this.m.xor(o.m);
		int idx = this.m.nextSetBit(0);
		this.m.xor(o.m);

		/**
		 * matrix with 0 in first differing position is smaller
		 */

		if (idx >= 0) {
			if (this.m.get(idx)) {
				return 1;
			}
			return -1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.columns;
		result = (prime * result) + ((this.m == null) ? 0 : this.m.hashCode());
		result = (prime * result) + this.rows;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof BitSetMatrix) {
			BitSetMatrix other = (BitSetMatrix) obj;
			return this.compareTo(other)==0;
		}
		return false;
	}


	@Override
	public String toString() {
		if ((this.rows<=0) || (this.columns<=0)) {
			return "[]("+this.rows+","+this.columns+")";
		}

		StringBuffer s = new StringBuffer();

		for (int r = 0; r < this.rows; ++r) {
			s.append("[");
			for (int c = 0; c < this.columns; ++c) {
				if (this.m.get(c + (r * this.columns))) {
					s.append("1");
				} else {
					s.append(".");
				}
			}
			s.append("]\n");
		}

		return s.toString();
	}

	/**
	 * 
	 * @param i
	 * @return the vector consisting of m(0..i-1,i)+m(i,i)+m(i,i+1..columns)
	 */

	public ComparableBitSet LVector(int i) {
		ComparableBitSet vector = new ComparableBitSet();
		for (int j = 0; j < this.columns; ++j) {
			if (j <= i) {
				if (this.get(j, i)) {
					vector.set(j);
				}
			} else {
				if (this.get(i, j)) {
					vector.set(j);
				}
			}
		}
		return vector;
	}

	/**
	 * swaps rows and columns according to the given permutation
	 * 
	 * @param p
	 */

	public void multiSwap(Permutation p) {
		BitSet new_m = new BitSet(this.rows * this.columns);

		for (int r = 0; r < this.rows; ++r) {
			for (int c = 0; c < this.columns; ++c) {
				if (this.m.get(c + (r * this.columns))) {
					new_m.set(p.Forward(c) + (p.Forward(r) * this.columns));
				}
			}
		}

		this.m = new_m;
	}
}
