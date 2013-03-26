/**
 * BitSetAscendingHasseNeighbors.java, (c) 2013, Immanuel Albrecht; Dresden
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
package de.tu_dresden.psy.fca;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.tu_dresden.psy.fca.util.BitSetMatrix;
import de.tu_dresden.psy.fca.util.ComparableBitSet;
import de.tu_dresden.psy.fca.util.Permutation;

/**
 * 
 * @author immo
 * 
 *         class that sorts a set of ordered elements such that the upper
 *         neighbor relation may be seen
 * 
 */

public class BitSetAscendingHasseNeighbors {

	private Map<Integer, OrderElement> numberToElement;
	private Map<OrderElement, Integer> elementToNumber;
	private Map<Integer, Integer> numberToMinRank;
	private Map<Integer, Integer> numberToMaxRank;
	static OrderZero bottom = new OrderZero();

	/**
	 * note that upperNeighbors.get(0) is the bit set of all elements that have
	 * no lower neighbors, i.e. that are minimal wrt. the poset
	 */
	private ArrayList<BitSet> upperNeighbors;

	/**
	 * 
	 * @return size of the poset that is the basis of this structure
	 */

	public int size() {
		return this.numberToElement.size() - 1;
	}

	/**
	 * calculates the numberToMinRank and numberToMaxRank sets
	 */

	private void calcRanks() {
		this.numberToMaxRank = new TreeMap<Integer, Integer>();
		this.numberToMinRank = new TreeMap<Integer, Integer>();

		BitSet next_step = new BitSet();
		next_step.set(0);
		int rank = 0;

		while (next_step.isEmpty() == false) {
			BitSet next_step2 = new BitSet();

			for (int n = next_step.nextSetBit(0); n >= 0; n = next_step
					.nextSetBit(n + 1)) {
				if (this.numberToMinRank.containsKey(n) == false) {
					this.numberToMinRank.put(n, rank);
				}
				this.numberToMaxRank.put(n, rank);
				next_step2.or(this.upperNeighbors.get(n));
			}

			next_step = next_step2;
			++rank;
		}
	}

	private BitSetAscendingHasseNeighbors(
			Map<Integer, OrderElement> numberToElement,
			Map<OrderElement, Integer> elementToNumber,
			ArrayList<BitSet> upperNeighbors) {
		this.numberToElement = numberToElement;
		this.elementToNumber = elementToNumber;
		this.upperNeighbors = upperNeighbors;

		/**
		 * calculate ranks
		 */

		this.calcRanks();
	}

	public BitSetAscendingHasseNeighbors(Set<OrderElement> poset) {
		this.numberToElement = new TreeMap<Integer, OrderElement>();
		this.elementToNumber = new TreeMap<OrderElement, Integer>();
		this.numberToElement.put(0, bottom);
		this.elementToNumber.put(bottom, 0);
		int element_count = poset.size();
		this.upperNeighbors = new ArrayList<BitSet>(element_count + 1);
		this.upperNeighbors.add(new BitSet(element_count + 1));
		this.upperNeighbors.get(0).set(1, element_count + 1);
		int id = 1;
		for (OrderElement p : poset) {
			this.numberToElement.put(id, p);
			this.elementToNumber.put(p, id);
			this.upperNeighbors.add(new BitSet(element_count + 1));
			id++;
		}

		BitSet continueMoving = new BitSet(element_count + 1);

		continueMoving.set(0);

		while (continueMoving.isEmpty() == false) {
			for (int base = continueMoving.nextSetBit(0); base >= 0; base = continueMoving
					.nextSetBit(base + 1)) {
				continueMoving.clear(base);
				if (this.moveUp(base)) {
					continueMoving.or(this.upperNeighbors.get(base));
				}
			}
		}

		/**
		 * calculate ranks
		 */

		this.calcRanks();
	}

	/**
	 * 
	 * compares all elements that are bigger than base pairwise, and moves up
	 * those elements that are bigger than the compared elements
	 * 
	 * @param base
	 *            element
	 * @return whether we moved elements up
	 */

	private boolean moveUp(int base) {
		BitSet R = this.upperNeighbors.get(base);
		boolean changed = false;

		OUTER_LOOP: for (int p = R.nextSetBit(0); p >= 0; p = R
				.nextSetBit(p + 1)) {

			OrderElement P = this.numberToElement.get(p);
			for (int q = R.nextSetBit(p + 1); q >= 0; q = R.nextSetBit(q + 1)) {
				OrderElement Q = this.numberToElement.get(q);
				int result = P.cmp(Q);

				if ((result & OrderElement.lessThan) > 0) {
					/*
					 * base < P < Q, thus (not base <. Q)
					 */
					R.clear(q);

					/*
					 * find all O < Q, and set O <. Q
					 */

					for (int o = R.nextSetBit(0); o >= 0; o = R
							.nextSetBit(o + 1)) {
						OrderElement O = this.numberToElement.get(o);
						if ((O.cmp(Q) & OrderElement.lessThan) > 0) {
							this.upperNeighbors.get(o).set(q);
						}
					}
					changed = true;
				} else if ((result & OrderElement.greaterThan) > 0) {
					/*
					 * base < Q < P, thus (not base <. P)
					 */
					R.clear(p);

					/*
					 * find all O < P, and set O <. P
					 */

					for (int o = R.nextSetBit(0); o >= 0; o = R
							.nextSetBit(o + 1)) {
						OrderElement O = this.numberToElement.get(o);
						if ((O.cmp(P) & OrderElement.lessThan) > 0) {
							this.upperNeighbors.get(o).set(p);
						}
					}

					changed = true;
					continue OUTER_LOOP;
				}
			}
		}

		return changed;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < this.upperNeighbors.size(); ++i) {
			BitSet uppers = this.upperNeighbors.get(i);
			if (uppers.isEmpty() == false) {

				out.append(this.numberToElement.get(i));
				out.append(" <. ");

				boolean first = true;
				for (int o = uppers.nextSetBit(0); o >= 0; o = uppers
						.nextSetBit(o + 1)) {
					if (first) {
						first = false;
					} else {
						out.append(", ");
					}
					out.append(this.numberToElement.get(o));

				}
				out.append("\n");
			}
		}

		return out.toString();
	}

	/**
	 * 
	 * @return dot file contents that generate the current Hasse diagram
	 */
	public String asDotCode(boolean labels) {
		StringBuffer out = new StringBuffer();

		out.append("digraph HasseDiagramm {\n");
		out.append("rankdir=BT;\n");

		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}
			String label = this.numberToElement.get(n).toString();

			out.append("node" + n);
			out.append("[");
			if (labels) {

				// out.append("shape=circle,");
				out.append("label=\"" + label + "\"");

			} else {
				out.append("label=\"\",shape=point,color=red,width=.1,height=.1");
			}
			out.append("];\n");
		}
		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}
			BitSet upper = this.upperNeighbors.get(n);
			for (int o = upper.nextSetBit(0); o >= 0; o = upper
					.nextSetBit(o + 1)) {
				out.append("node" + n + " -> node" + o + "[dir=none];\n");
			}
		}

		out.append("}");

		return out.toString();
	}

	/**
	 * 
	 * @return the set of minimal elements
	 */

	public Set<OrderElement> Minimal() {
		Set<OrderElement> s = new TreeSet<OrderElement>();
		BitSet x = this.upperNeighbors.get(0);
		for (int o = x.nextSetBit(0); o >= 0; o = x.nextSetBit(o + 1)) {
			s.add(this.numberToElement.get(o));
		}
		return s;
	}

	/**
	 * 
	 * @param p
	 * @return minimal rank of element p
	 */

	public int minRank(OrderElement p) {
		return this.numberToMinRank.get(this.elementToNumber.get(p));
	}

	/**
	 * 
	 * @param p
	 * @return maximal rank of element p
	 */

	public int maxRank(OrderElement p) {
		return this.numberToMaxRank.get(this.elementToNumber.get(p));
	}

	/**
	 * 
	 * @return the maximum rank
	 */

	public int highestRank() {
		int x = 0;
		for (Integer v : this.numberToMaxRank.values()) {
			if (x < v) {
				x = v;
			}
		}
		return x;
	}

	/**
	 * 
	 * @param p
	 *            element
	 * @return all upper neighbors of p, i.e. all q with p <. q
	 */

	public Set<OrderElement> UpperNeighbors(OrderElement p) {
		Set<OrderElement> s = new TreeSet<OrderElement>();
		BitSet x = this.upperNeighbors.get(this.elementToNumber.get(p));
		for (int o = x.nextSetBit(0); o >= 0; o = x.nextSetBit(o + 1)) {
			s.add(this.numberToElement.get(o));
		}
		return s;
	}

	/**
	 * change the order of element numbers by given permutation
	 * 
	 * @param p
	 *            Permutation such that p.Forward(0)=0
	 * @return a copy of this object, but with different element numbering; or
	 *         null if p.Forward(0) != 0
	 */

	public BitSetAscendingHasseNeighbors Reorder(Permutation p) {
		if (p.Forward(0) != 0) {
			return null;
		}

		int n_elements = this.elementToNumber.size();
		Map<Integer, OrderElement> numberToElement;
		Map<OrderElement, Integer> elementToNumber;

		ArrayList<BitSet> upperNeighbors;
		numberToElement = new TreeMap<Integer, OrderElement>();
		elementToNumber = new TreeMap<OrderElement, Integer>();

		numberToElement.put(0, bottom);
		elementToNumber.put(bottom, 0);

		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}

			OrderElement e = this.numberToElement.get(n);
			int new_n = p.Forward(n);

			numberToElement.put(new_n, e);
			elementToNumber.put(e, new_n);
		}

		upperNeighbors = new ArrayList<BitSet>();
		for (int i = 0; i < n_elements; ++i) {
			int old_i = i;
			if (i != 0) {
				old_i = p.Backward(i);
			}
			BitSet old_u = this.upperNeighbors.get(old_i);
			BitSet u = new BitSet(n_elements);
			for (int j = 0; j < n_elements; ++j) {
				int old_j = j;
				if (j != 0) {
					old_j = p.Backward(j);
				}
				if (old_u.get(old_j)) {
					u.set(j);
				}
			}
			upperNeighbors.add(u);
		}

		return new BitSetAscendingHasseNeighbors(numberToElement,
				elementToNumber, upperNeighbors);
	}

	/**
	 * change the order of element numbers by random permutation
	 * 
	 * @return a copy of this object, but with different element numbering
	 */

	public BitSetAscendingHasseNeighbors Shake() {
		int n_elements = this.elementToNumber.size();
		Permutation p = new Permutation(n_elements - 1);
		Map<Integer, OrderElement> numberToElement;
		Map<OrderElement, Integer> elementToNumber;

		ArrayList<BitSet> upperNeighbors;
		numberToElement = new TreeMap<Integer, OrderElement>();
		elementToNumber = new TreeMap<OrderElement, Integer>();

		numberToElement.put(0, bottom);
		elementToNumber.put(bottom, 0);

		for (Integer n : this.numberToElement.keySet()) {
			if (n == 0) {
				continue;
			}

			OrderElement e = this.numberToElement.get(n);
			int new_n = p.Forward(n - 1) + 1;

			numberToElement.put(new_n, e);
			elementToNumber.put(e, new_n);
		}

		upperNeighbors = new ArrayList<BitSet>();
		for (int i = 0; i < n_elements; ++i) {
			int old_i = i;
			if (i != 0) {
				old_i = p.Backward(i - 1) + 1;
			}
			BitSet old_u = this.upperNeighbors.get(old_i);
			BitSet u = new BitSet(n_elements);
			for (int j = 0; j < n_elements; ++j) {
				int old_j = j;
				if (j != 0) {
					old_j = p.Backward(j - 1) + 1;
				}
				if (old_u.get(old_j)) {
					u.set(j);
				}
			}
			upperNeighbors.add(u);
		}

		return new BitSetAscendingHasseNeighbors(numberToElement,
				elementToNumber, upperNeighbors);
	}

	/**
	 * 
	 * @return the adjacency matrix of this object without the bottom element
	 */

	public BitSetMatrix AdjacencyMatrix() {
		Iterator<BitSet> i = this.upperNeighbors.iterator();
		i.next();
		int n_elements = this.elementToNumber.size();
		return new BitSetMatrix(n_elements - 1, n_elements - 1, i, 1);
	}

	/**
	 * 
	 * @author immo
	 * 
	 *         helper class for quick-sorting arrays of permutation images
	 * 
	 */

	static private class QSortElementList {
		private BitSetAscendingHasseNeighbors parent;
		static Random rnd = new Random();

		public QSortElementList(BitSetAscendingHasseNeighbors parent) {
			this.parent = parent;
		}

		private static final void swap(ArrayList<Integer> x, int p, int q) {
			int s = x.get(p);
			x.set(p, x.get(q));
			x.set(q, s);
			/*
			 * System.out.println("swap " + p + "<>" + q + "  ("+x.get(q)+"," +
			 * this.parent.numberToMinRank.get(x.get(q)) + ") vs ("+x.get(p)+","
			 * + this.parent.numberToMinRank.get(x.get(p)) + ")");
			 */
		}

		public void QSortArray(ArrayList<Integer> x, int left, int right) {
			if (left < right) {

				// System.out.println("[" + left + "," + right + "]");

				int pivotIndex = left + rnd.nextInt((right - left) + 1);
				int pivot = x.get(pivotIndex);
				int min_rank = this.parent.numberToMinRank.get(pivot);
				int max_rank = this.parent.numberToMaxRank.get(pivot);

				swap(x, pivotIndex, right);

				pivotIndex = left;

				for (int i = left; i < right; ++i) {
					int e = x.get(i);
					int e_min = this.parent.numberToMinRank.get(e);
					if ((e_min < min_rank)
							|| ((e_min == min_rank) && (this.parent.numberToMaxRank
									.get(e) < max_rank))) {
						swap(x, pivotIndex, i);
						pivotIndex++;
					}
				}

				swap(x, pivotIndex, right);

				this.QSortArray(x, left, pivotIndex - 1);
				this.QSortArray(x, pivotIndex + 1, right);
			}
		}
	}

	/**
	 * 
	 * @return a pre-normalized version of this object with regard to the
	 *         poset's isomorphism class
	 */
	public BitSetAscendingHasseNeighbors PreNormalize() {
		int n_elements = this.elementToNumber.size();

		ArrayList<Integer> new_order = new ArrayList<Integer>(n_elements);
		for (int i = 0; i < n_elements; ++i) {
			new_order.add(i);
		}

		QSortElementList sorter = new QSortElementList(this);

		sorter.QSortArray(new_order, 1, n_elements - 1);

		// System.out.println(new_order);
		// ArrayList<Integer> ranks = new ArrayList<>();
		//
		// for (int i = 0; i < n_elements; ++i) {
		// ranks.add(this.numberToMinRank.get(new_order.get(i)));
		// }
		//
		// System.out.println(ranks);
		//
		// ranks = new ArrayList<>();
		//
		// for (int i = 0; i < n_elements; ++i) {
		// ranks.add(this.numberToMaxRank.get(new_order.get(i)));
		// }

		// System.out.println(ranks);

		Permutation reorder = new Permutation(new_order.iterator()).Inverse();

		// System.out.println(reorder);

		return this.Reorder(reorder);
	};

	/**
	 * this function will identify some objects that are isomorphic, but not
	 * all, and return a representation that has the minimal found adjacency
	 * matrix. Since this is not unique per isomorphism class, we call is pseudo
	 * normalized form
	 * 
	 * @return a psedo normalized version of this object
	 */

	public BitSetAscendingHasseNeighbors PseudoNormalize() {

		BitSetAscendingHasseNeighbors form = this.PreNormalize();
		int N = form.numberToElement.size();
		Map<BitSetMatrix, BitSetAscendingHasseNeighbors> unique_forms = new TreeMap<BitSetMatrix, BitSetAscendingHasseNeighbors>();

		for (int iteration = 0; iteration < 1000; ++iteration) {

			BitSetMatrix adjacency = form.AdjacencyMatrix();

			if (unique_forms.containsKey(adjacency)) {
				// System.out.println("Cycle after " + iteration +
				// " iterations.");
				/**
				 * we have completed a cycle (and thus found one)
				 */
				return unique_forms
						.get(unique_forms.keySet().iterator().next());
			} else {
				unique_forms.put(adjacency, form);
			}

			adjacency = form.AdjacencyMatrix();

			ArrayList<Integer> new_order = new ArrayList<Integer>(N);
			for (int i = 0; i < N; ++i) {
				new_order.add(i);
			}

			int group_start = 0;
			int min_rank = 0;
			int max_rank = 0;

			TreeMap<ComparableBitSet, Set<Integer>> order_level = new TreeMap<>();

			for (int i = 1; i < (N + 1); ++i) {
				if ((i == N) || (form.numberToMinRank.get(i) != min_rank)
						|| (form.numberToMaxRank.get(i) != max_rank)) {
					int size = i - group_start;

					if (size > 1) {

						/**
						 * minimize the order
						 */
						int x = group_start;

						for (ComparableBitSet c : order_level.keySet()) {
							// System.out.println(i + " " + order_level.get(c));
							for (Integer j : order_level.get(c)) {
								new_order.set(x, j);
								++x;
							}
						}
						/**
						 * rearrange the adjacency matrix
						 */
						Permutation level_swap = new Permutation(new_order,
								group_start, group_start + size);
						// System.out.println(level_swap);

						adjacency.multiSwap(level_swap);
					}
					if (i == N) {
						break;
					}

					group_start = i;
					min_rank = form.numberToMinRank.get(i);
					max_rank = form.numberToMaxRank.get(i);

					order_level.clear();
				}

				ComparableBitSet L = adjacency.LVector(i);
				// System.out.println(i + " " + L);
				if (order_level.containsKey(L) == false) {
					order_level.put(L, new TreeSet<Integer>());
				}
				order_level.get(L).add(i);

			}

			Permutation to_minimum = new Permutation(new_order.iterator());

			form = form.Reorder(to_minimum);
		}

		// System.out.println("No cycle!");

		return form;
	}

}
