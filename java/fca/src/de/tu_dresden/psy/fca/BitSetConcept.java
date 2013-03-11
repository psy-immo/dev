package de.tu_dresden.psy.fca;

import java.util.BitSet;


public class BitSetConcept implements FormalConcept {
	
	private BitSet os,as;
	
	public BitSetConcept(BitSet os, BitSet as) {
		this.os = os;
		this.as = as;
	}

	@Override
	public BitSet commonObjects() {
		return this.os;
	}

	@Override
	public BitSet commonAttributes() {
		return this.as;
	}
	
	@Override
	public String toString() {
		return "("+os+","+as+")";
	}
	
	@Override
	public int compareTo(FormalConcept o) {
		BitSet attribs = o.commonAttributes();
		
		int boundary = Math.max(as.length(), attribs.length());
		
		for (int i=0; i<boundary;++i) {
			if (as.get(i)==true) {
				if (attribs.get(i)==false)
					return -1;
			} else if (attribs.get(i)==true)
				return 1;
		}		
		return 0;
	}
		
	
	@Override
	public int cmp(FormalConcept r) {
		BitSet objs = r.commonObjects();
		boolean less= false;
		boolean more= false;
		
		int boundary = Math.max(os.length(), objs.length());
		
		for (int i=0; i<boundary;++i) {
			if (os.get(i)) {
				if (objs.get(i)==false)
					more = true;
			} else if (objs.get(i))
				less = true;
			if (more && less)
				return FormalConcept.incomparable;
		}
			
		if (less)
			return FormalConcept.lessObjects;
		if (more)
			return FormalConcept.moreObjects;
		return FormalConcept.equalConcept;
	}
		

}
