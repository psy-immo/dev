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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

	private void initializeCommon() {
		this.objectToName = new TreeMap<Integer, String>();
		this.attributeToName = new TreeMap<Integer, String>();
		this.objectFromName = new HashMap<String, Integer>();
		this.attributeFromName = new HashMap<String, Integer>();
		this.incidenceRows = new ArrayList<BitSet>();
		this.incidenceCols = new ArrayList<BitSet>();
	}

	private void initializeEmpty(int o_count, int a_count) {
		initializeCommon();
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
	}

	public BitSetContext() {
		initializeCommon();
		this.name = "K(0,0,{})";
	}

	public BitSetContext(int o_count, int a_count) {
		initializeEmpty(o_count, a_count);
		this.name = "K(" + o_count + "," + a_count + ",{})";

	}

	@Override
	public int attributeByName(String name) throws Exception {
		Integer i = this.attributeFromName.get(name);
		if (i == null)
			throw new Exception("Unknown Attribute '" + name + "'", this.name);
		return i;
	}

	@Override
	public String attributeName(int a) throws Exception {
		String name = this.attributeToName.get(a);
		if (name == null)
			throw new Exception("Unknown Attribute " + a, this.name);
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
		return name;
	}

	@Override
	public String objectName(int o) throws Exception {
		String name = this.objectToName.get(o);
		if (name == null)
			throw new Exception("Unknown Object " + o, this.name);
		return name;
	}

	@Override
	public int objectByName(String name) throws Exception {
		Integer i = this.objectFromName.get(name);
		if (i == null)
			throw new Exception("Unknown Object '" + name + "'", this.name);
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
		return incidenceRows.get(o).get(a);
	}

	@Override
	public BitSet objectRow(int o) throws Exception {
		return incidenceRows.get(o);
	}

	@Override
	public BitSet attributeCol(int a) throws Exception {
		return incidenceCols.get(a);
	}

	public void setIncidence(int o, int a, boolean cross) {
		if (cross) {
			incidenceRows.get(o).set(a);
			incidenceCols.get(a).set(o);
		} else {
			incidenceRows.get(o).clear(a);
			incidenceCols.get(a).clear(o);
		}
	}

	public void setCross(int o, int a) {
		setIncidence(o, a, true);
	}

	public void clearCross(int o, int a) {
		setIncidence(o, a, false);
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
		BitSet os = commonObjects(as);
		return new BitSetConcept(os, commonAttributes(os));
	}

	@Override
	public FormalConcept closeObjects(BitSet os) throws Exception {
		BitSet as = commonAttributes(os);
		return new BitSetConcept(commonObjects(as), as);
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("B\n");
		b.append(name);
		try {
			b.append("\n" + numberOfObjects() + "\n" + numberOfAttributes()
					+ "\n");
			for (int i = 0; i < numberOfObjects(); ++i) {
				b.append(this.objectName(i));
				b.append("\n");
			}
			for (int i = 0; i < numberOfAttributes(); ++i) {
				b.append(this.attributeName(i));
				b.append("\n");
			}
			for (int i = 0; i < numberOfObjects(); ++i) {
				BitSet row = incidenceRows.get(i);
				for (int j = 0; j < numberOfAttributes(); ++j) {
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
					setIncidence(o, a, rnd.nextDouble() < crossRate);
				}
			}
			this.name = "K(" + this.numberOfObjects() + ","
					+ this.numberOfAttributes() + ",Random<" + crossRate + ")";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public FormalConcept topConcept() throws Exception {
		return closeObjects(new BitSet());
	}
	
	@Override
	public FormalConcept bottomConcept() throws Exception {
		return closeAttributes(new BitSet());
	}
	
	@Override
	public FormalConcept nextClosure(FormalConcept x) throws Exception {
		BitSet m = BitSet.valueOf(x.commonAttributes().toByteArray());
		
		for(int i = this.numberOfAttributes()-1;i>=0;--i)
		{
			if (m.get(i) == false) {
				m.set(i);
				FormalConcept y = this.closeAttributes(m);
				boolean good = true;
				for (int j=0;j<i;++j) {
					if (y.commonAttributes().get(j))
						if (m.get(j)==false)
						{
							good = false;
							break;
						}
				}
				if (good)
					return y;
			}
			m.clear(i);
		}
		
		return null;
	}
	
	public String toCSV() throws Exception {
		StringBuffer b = new StringBuffer();
		for (int j = 0; j<this.numberOfAttributes();++j) {
			b.append(","+this.attributeName(j));
		}
		for (int i=0;i<this.numberOfObjects();++i)
		{
			b.append("\n"+this.objectName(i));
			for (int j = 0; j<this.numberOfAttributes();++j) {
				if (this.incidenceRelation(i, j))
					b.append(",1");
				else
					b.append(",0");
			}
		}
		return b.toString();
	}
	
}
