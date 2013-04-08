/**
 * HasseDiagram.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.fca.layout;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.tu_dresden.psy.fca.FormalConcept;
import de.tu_dresden.psy.fca.OrderElement;
import de.tu_dresden.psy.fca.util.DoubleMatrix;
import de.tu_dresden.psy.fca.util.DoubleMatrix.SpecialMatrix;
import de.tu_dresden.psy.fca.util.DoubleVector;

/**
 * 
 * @author immo
 * 
 *         objects of this class represent a (k+1)-dimensional HASSE diagram
 * 
 */

public class HasseDiagram {

	private int k;
	private int N;
	private Map<Integer, OrderElement> fromNumber;
	private Map<OrderElement, Integer> toNumber;
	private ArrayList<DoubleVector> vectors;
	private DoubleMatrix layout;
	private Neighborhood neighborhood;

	public HasseDiagram(Set<OrderElement> poset) {
		int x = 0;
		this.fromNumber = new TreeMap<Integer, OrderElement>();
		this.toNumber = new TreeMap<OrderElement, Integer>();
		for (OrderElement e : poset) {
			this.fromNumber.put(x, e);
			this.toNumber.put(e, x);
			++x;
		}

		this.neighborhood = new Neighborhood(poset);

		/**
		 * check whether we already have formal concepts
		 */

		boolean fcs = true;
		int nbrO = 0, nbrA = 0;
		for (OrderElement e : poset) {
			if (!(e instanceof FormalConcept)) {
				fcs = false;
				break;
			} else {
				FormalConcept c = (FormalConcept) e;
				if (nbrO < c.contextObjectCount()) {
					nbrO = c.contextObjectCount();
				}
				if (nbrA < c.contextAttributeCount()) {
					nbrA = c.contextAttributeCount();
				}
			}
		}

		this.vectors = new ArrayList<DoubleVector>();

		if (fcs) {
			/**
			 * use the original context
			 */

			if (nbrA < nbrO) {
				/**
				 * use inverse attribute vectors
				 */

				this.N = nbrA;
				for (int i = 0; i < x; ++i) {
					BitSet b = new BitSet(nbrA);
					b.set(0, nbrA);
					FormalConcept c = (FormalConcept) this.fromNumber.get(i);
					b.xor(c.commonAttributes());
					this.vectors.add(new DoubleVector(nbrA, b));
				}
			} else {
				/**
				 * use object vectors
				 */

				this.N = nbrO;
				for (int i = 0; i < x; ++i) {
					BitSet b = new BitSet(nbrO);
					FormalConcept c = (FormalConcept) this.fromNumber.get(i);
					b.or(c.commonObjects());
					this.vectors.add(new DoubleVector(nbrO, b));
				}
			}
		} else {
			/**
			 * use the standard context of the poset
			 */

			this.N = x;
			for (int i = 0; i < x; ++i) {
				BitSet b = new BitSet(x);
				for (int j = 0; j < x; ++j) {
					if ((this.fromNumber.get(j).cmp(this.fromNumber.get(i)) & OrderElement.LessEq) > 0) {
						b.set(j);
					}
				}
				this.vectors.add(new DoubleVector(x, b));
			}
		}

		this.k = 1;
		this.layout = new DoubleMatrix(this.k + 1, this.N,
				SpecialMatrix.HasseDefault);
	}

	public int getDimension() {
		return this.k + 1;
	}

	/**
	 * 
	 * @return the current hasse diagram as svg embedded in an html container
	 */

	public String toHtml() {
		return "<!DOCTYPE html>	<html><body>" + this.toSVG() + "</body></html>";
	}

	/**
	 * 
	 * @return SVG code for the current hasse diagram
	 */

	public String toSVG() {
		StringBuffer b = new StringBuffer();

		b.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">");

		double padding_x = 50;
		double padding_y = 50;

		double ymax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double xmin = Double.POSITIVE_INFINITY;
		int count[] = new int[0 + 1]; // DUMMY CODE
		double x[] = new double[this.vectors.size()];
		double y[] = new double[this.vectors.size()];

		for (int i = 0; i < this.vectors.size(); ++i) {

			DoubleVector v = this.vectors.get(i);
			DoubleVector r = this.layout.rMult(v);

			x[i] = (count[0]++) * 20; // DUMMY CODE
			y[i] = 0 * 50; // DUMMY CODE

			/**
			 * dummy positioning !
			 */
			// x[i] = (count[this.neighbors.maxRank(this.fromNumber.get(i))]++)
			// * 20;
			// y[i] = this.neighbors.maxRank(this.fromNumber.get(i)) * 50;

			if (x[i] > xmax) {
				xmax = x[i];
			}
			if (xmin > x[i]) {
				xmin = x[i];
			}
			if (y[i] > ymax) {
				ymax = y[i];
			}
			if (ymin > y[i]) {
				ymin = y[i];
			}
		}

		for (int i = 0; i < this.vectors.size(); ++i) {
			for (OrderElement q : this.neighborhood.UpperNeighbors(this.fromNumber
					.get(i))) {
				int j = this.toNumber.get(q);

				b.append("<line x1=\"" + (padding_x + x[i]) + "\" y1=\""
						+ ((padding_y + ymax) - y[i]) + "\" x2=\""
						+ (padding_x + x[j]) + "\" y2=\""
						+ ((padding_y + ymax) - y[j])
						+ "\" style=\"stroke:rgb(64,64,64);stroke-width:2\" />");
			}
		}

		for (int i = 0; i < this.vectors.size(); ++i) {

			b.append("<circle cx=\"" + (padding_x + x[i]) + "\" cy=\""
					+ ((padding_y + ymax) - y[i]) + "\" r=\"5\"/>");

		}

		b.append("</svg>");

		return b.toString();
	}
}
