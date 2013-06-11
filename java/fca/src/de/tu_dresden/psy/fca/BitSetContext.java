/**
 * BitSetContext.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
package de.tu_dresden.psy.fca;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author immo
 * 
 *         class that implements a formal context via BitSet classes
 */
public class BitSetContext implements FormalContext {
	private String name;
	private Map<Integer, String> objectToName;
	private Map<Integer, String> attributeToName;

	private Map<String, Integer> objectFromName;
	private Map<String, Integer> attributeFromName;

	private ArrayList<BitSet> incidenceRows;
	private ArrayList<BitSet> incidenceCols;

	private int nbrO, nbrA;

	private void initializeCommon() {
		this.objectToName = new TreeMap<Integer, String>();
		this.attributeToName = new TreeMap<Integer, String>();
		this.objectFromName = new HashMap<String, Integer>();
		this.attributeFromName = new HashMap<String, Integer>();
		this.incidenceRows = new ArrayList<BitSet>();
		this.incidenceCols = new ArrayList<BitSet>();
		this.nbrO = 0;
		this.nbrA = 0;
	}

	private void initializeEmpty(int o_count, int a_count) {
		this.initializeCommon();
		for (int i = 0; i < o_count; i++) {
			this.incidenceRows.add(new BitSet(a_count));
			this.objectToName.put(i, "g" + i);
			this.objectFromName.put("g" + i, i);
		}
		for (int i = 0; i < a_count; i++) {
			this.incidenceCols.add(new BitSet(o_count));
			this.attributeToName.put(i, "m" + i);
			this.attributeFromName.put("m" + i, i);
		}
		this.nbrO = o_count;
		this.nbrA = a_count;
	}

	public enum SpecialContexts {
		Chain, Antichain, Powerset, N5
	};

	public enum FileFormat {
		Burmeister
	}

	public BitSetContext(FileFormat format, InputStreamReader data)
			throws IOException {
		this.BitSetContextFromFile(format, data, "unnamed stream");
	}

	public BitSetContext(FileFormat format, String path)
			throws FileNotFoundException, IOException {
		this.BitSetContextFromFile(format, new InputStreamReader(
				new FileInputStream(path)), path);
	}

	private void BitSetContextFromFile(FileFormat format,
			InputStreamReader data, String defaultName) throws IOException {
		if (format == FileFormat.Burmeister) {
			BufferedReader br = new BufferedReader(data);
			String line;
			int lineNbr = 0;
			int nbrO = 0, nbrA = 0;

			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					if (lineNbr != 1) {
						continue;
					}
					/** ignore empty lines except for the name, in this case */
					line = defaultName;
				}

				if (lineNbr == 0) {
					if (line.equals("B") == false) {
						this.initializeEmpty(0, 0);
						this.name = "Error: Data Stream is not Burmeister!";
						return;
					}
				} else if (lineNbr == 1) {
					this.name = line;
				} else if (lineNbr == 2) {
					nbrO = Integer.parseInt(line);
				} else if (lineNbr == 3) {
					nbrA = Integer.parseInt(line);
					this.initializeEmpty(nbrO, nbrA);

				} else if (lineNbr < (4 + nbrO)) {
					this.objectToName.put(lineNbr - 4, line);
					this.objectFromName.put(line, lineNbr - 4);
				} else if (lineNbr < (4 + nbrO + nbrA)) {
					this.attributeToName.put(lineNbr - 4 - nbrO, line);
					this.attributeFromName.put(line, lineNbr - 4 - nbrO);
				} else if (lineNbr < (4 + nbrO + nbrA + nbrO)) {
					int g = lineNbr - 4 - nbrO - nbrA;

					for (int m = 0; m < nbrA; ++m) {

						if (line.subSequence(m, m + 1).equals("X")) {
							this.setCross(g, m);
						}
					}
				}

				lineNbr++;
			}
		}
	}

	public BitSetContext(SpecialContexts kind, int size) {
		if (kind == SpecialContexts.Chain) {
			this.initializeEmpty(size, size);
			for (int i = 0; i < size; ++i) {
				for (int j = i; j < size; ++j) {
					this.setCross(j, i);
				}
			}
			this.name = "C(" + size + ")";
		} else if (kind == SpecialContexts.Antichain) {
			this.initializeEmpty(size, size);
			for (int i = 0; i < size; ++i) {
				this.setCross(i, i);
			}
			this.name = "A(" + size + ")";
		} else if (kind == SpecialContexts.Powerset) {
			this.initializeEmpty(size, size);
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					if (i != j) {
						this.setCross(i, j);
					}
				}
			}
			this.name = "P(" + size + ")";
		} else if (kind == SpecialContexts.N5) {
			this.initializeEmpty(3, 3);
			this.setCross(0, 0);
			this.setCross(1, 1);
			this.setCross(1, 2);
			this.setCross(2, 2);
			this.name = "N5";
		}
	}

	public BitSetContext() {
		this.initializeCommon();
		this.name = "K(0,0,{})";
	}

	public BitSetContext(int o_count, int a_count) {
		this.initializeEmpty(o_count, a_count);
		this.name = "K(" + o_count + "," + a_count + ",{})";

	}

	@Override
	public int attributeByName(String name) throws Exception {
		Integer i = this.attributeFromName.get(name);
		if (i == null) {
			throw new Exception("Unknown Attribute '" + name + "'", this.name);
		}
		return i;
	}

	@Override
	public String attributeName(int a) throws Exception {
		String name = this.attributeToName.get(a);
		if (name == null) {
			throw new Exception("Unknown Attribute " + a, this.name);
		}
		return name;
	}

	@Override
	public int numberOfObjects() throws Exception {
		return this.objectToName.size();
	}

	@Override
	public int numberOfAttributes() throws Exception {
		return this.attributeToName.size();
	}

	@Override
	public String nameOfContext() throws Exception {
		return this.name;
	}

	@Override
	public String objectName(int o) throws Exception {
		String name = this.objectToName.get(o);
		if (name == null) {
			throw new Exception("Unknown Object " + o, this.name);
		}
		return name;
	}

	@Override
	public int objectByName(String name) throws Exception {
		Integer i = this.objectFromName.get(name);
		if (i == null) {
			throw new Exception("Unknown Object '" + name + "'", this.name);
		}
		return i;
	}

	@Override
	public Iterable<Integer> Attributes() throws Exception {
		return this.attributeToName.keySet();
	}

	@Override
	public Iterable<Integer> Objects() throws Exception {
		return this.objectToName.keySet();
	}

	@Override
	public boolean incidenceRelation(int o, int a) throws Exception {
		return this.incidenceRows.get(o).get(a);
	}

	@Override
	public BitSet objectRow(int o) throws Exception {
		return this.incidenceRows.get(o);
	}

	@Override
	public BitSet attributeCol(int a) throws Exception {
		return this.incidenceCols.get(a);
	}

	public void setIncidence(int o, int a, boolean cross) {
		if (cross) {
			this.incidenceRows.get(o).set(a);
			this.incidenceCols.get(a).set(o);
		} else {
			this.incidenceRows.get(o).clear(a);
			this.incidenceCols.get(a).clear(o);
		}
	}

	public void setCross(int o, int a) {
		this.setIncidence(o, a, true);
	}

	public void clearCross(int o, int a) {
		this.setIncidence(o, a, false);
	}

	@Override
	public BitSet commonAttributes(BitSet os) throws Exception {
		BitSet s = new BitSet(this.numberOfAttributes());
		s.set(0, this.numberOfAttributes());

		for (int i = os.nextSetBit(0); i >= 0; i = os.nextSetBit(i + 1)) {
			s.and(this.incidenceRows.get(i));
		}
		return s;
	}

	@Override
	public BitSet commonObjects(BitSet as) throws Exception {
		BitSet s = new BitSet(this.numberOfObjects());
		s.set(0, this.numberOfObjects());
		for (int i = as.nextSetBit(0); i >= 0; i = as.nextSetBit(i + 1)) {
			s.and(this.incidenceCols.get(i));
		}
		return s;
	}

	@Override
	public FormalConcept closeAttributes(BitSet as) throws Exception {
		BitSet os = this.commonObjects(as);
		return new BitSetConcept(os, this.commonAttributes(os), this.nbrO,
				this.nbrA);
	}

	@Override
	public OrderElement closeObjects(BitSet os) throws Exception {
		BitSet as = this.commonAttributes(os);
		return new BitSetConcept(this.commonObjects(as), as, this.nbrO,
				this.nbrA);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("B\n");
		b.append(this.name);
		try {
			b.append("\n" + this.numberOfObjects() + "\n"
					+ this.numberOfAttributes() + "\n");
			b.append("\n");
			for (int i = 0; i < this.numberOfObjects(); ++i) {
				b.append(this.objectName(i));
				b.append("\n");
			}
			for (int i = 0; i < this.numberOfAttributes(); ++i) {
				b.append(this.attributeName(i));
				b.append("\n");
			}
			for (int i = 0; i < this.numberOfObjects(); ++i) {
				BitSet row = this.incidenceRows.get(i);
				for (int j = 0; j < this.numberOfAttributes(); ++j) {
					if (row.get(j)) {
						b.append("X");
					} else {
						b.append(".");
					}
				}
				b.append("\n");
			}
		} catch (Exception e) {
		}
		return b.toString();
	}

	public void RandomizeContext(double crossRate) {
		Random rnd = new Random();
		try {
			for (int a = 0; a < this.numberOfAttributes(); ++a) {
				for (int o = 0; o < this.numberOfObjects(); ++o) {
					this.setIncidence(o, a, rnd.nextDouble() < crossRate);
				}
			}
			this.name = "K(" + this.numberOfObjects() + ","
					+ this.numberOfAttributes() + ",Random<" + crossRate + ")";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public OrderElement topConcept() throws Exception {
		return this.closeObjects(new BitSet());
	}

	@Override
	public FormalConcept bottomConcept() throws Exception {
		return this.closeAttributes(new BitSet());
	}

	/**
	 * Vanilla implementation of nextClosure algorithm
	 * 
	 * @param x
	 *            concept
	 * @return next concept or null
	 * @throws Exception
	 */

	public FormalConcept nextClosureVanilla(FormalConcept x) throws Exception {
		BitSet m = BitSet.valueOf(x.commonAttributes().toByteArray());

		for (int i = this.numberOfAttributes() - 1; i >= 0; --i) {
			if (m.get(i) == false) {
				m.set(i);
				FormalConcept y = this.closeAttributes(m);
				boolean good = true;
				for (int j = 0; j < i; ++j) {
					if (y.commonAttributes().get(j)) {
						if (m.get(j) == false) {
							good = false;
							break;
						}
					}
				}
				if (good) {
					return y;
				}
			}
			m.clear(i);
		}

		return null;
	}

	@Override
	public FormalConcept nextClosure(FormalConcept x) throws Exception {
		BitSet m = BitSet.valueOf(x.commonAttributes().toByteArray());

		for (int i = this.numberOfAttributes() - 1; i >= 0; --i) {
			if (m.get(i) == false) {
				m.set(i);
				FormalConcept y = this.closeAttributes(m);
				boolean good = true;
				for (int j = 0; j < i; ++j) {
					if (y.commonAttributes().get(j)) {
						if (m.get(j) == false) {
							good = false;
							break;
						}
					}
				}
				if (good) {
					return y;
				}
			}
			m.clear(i);
		}

		return null;
	}

	@SuppressWarnings("unused")
	public FormalConcept nextClosureWorseThanVanilla(FormalConcept x)
			throws Exception {
		BitSet m = BitSet.valueOf(x.commonAttributes().toByteArray());
		BitSet g = BitSet.valueOf(x.commonObjects().toByteArray());

		int boundary = this.numberOfObjects();

		for (int i = this.numberOfAttributes() - 1; i >= 0; --i) {
			if (m.get(i) == false) {
				/**
				 * up to here, we have (g,m) to be a concept
				 */

				m.set(i);

				BitSet col_i = this.attributeCol(i);

				boolean shared_j = false;

				ATTRIBUTE_LOOP: for (int j = 0; j < i; ++j) {
					if (m.get(j) == true) {
						continue;
					}

					BitSet col_j = this.attributeCol(j);
					INNER: for (int o = g.nextSetBit(0); o >= 0; o = g
							.nextSetBit(o + 1)) {
						if (col_i.get(o) == false) {
							continue INNER;
						}
						if (col_j.get(o) == false) {
							continue ATTRIBUTE_LOOP;
						}
					}
					shared_j = true;
					break ATTRIBUTE_LOOP;
				}

				if (shared_j == false) {
					g.and(col_i);

					/**
					 * finally, we compute g' (we could just add the common
					 * attributes greater than i here)
					 */

					m.set(0, this.numberOfAttributes());
					for (int o = g.nextSetBit(0); o >= 0; o = g
							.nextSetBit(o + 1)) {
						m.and(this.objectRow(o));
					}

					return new BitSetConcept(g, m, this.nbrO, this.nbrA);
				}

				m.clear(i);
			} else {
				/**
				 * up to here, we have (g,m) to be a concept
				 */

				m.clear(i);

				BitSet col_i = this.attributeCol(i);

				OBJECT_LOOP: for (int o = g.nextClearBit(0); o < boundary; o = g
						.nextClearBit(o + 1)) {
					if (col_i.get(o) == true) {
						continue OBJECT_LOOP;
					}

					BitSet row_o = this.objectRow(o);
					INNER: for (int j = m.nextSetBit(0); j >= 0; j = m
							.nextSetBit(j + 1)) {
						if (row_o.get(j) == false) {
							continue OBJECT_LOOP;
						}
					}

					g.set(o);
				}

			}

		}

		return null;
	}

	public String toCSV() throws Exception {
		StringBuffer b = new StringBuffer();
		for (int j = 0; j < this.numberOfAttributes(); ++j) {
			b.append("," + this.attributeName(j));
		}
		for (int i = 0; i < this.numberOfObjects(); ++i) {
			b.append("\n" + this.objectName(i));
			for (int j = 0; j < this.numberOfAttributes(); ++j) {
				if (this.incidenceRelation(i, j)) {
					b.append(",1");
				} else {
					b.append(",0");
				}
			}
		}
		return b.toString();
	}

	@Override
	public Set<FormalConcept> calculateAllConcepts() throws Exception {
		Set<FormalConcept> lattice = new TreeSet<FormalConcept>();

		for (FormalConcept b = this.bottomConcept(); b != null; b = this
				.nextClosureVanilla(b)) {
			lattice.add(b);
		}

		return lattice;
	}

	@Override
	public Lattice conceptLattice() throws Exception {
		return new BitSetConceptLattice(this);
	}
}
