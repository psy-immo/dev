/**
 * Main.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

package de.tu_dresden.psy.inference.example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import de.tu_dresden.psy.inference.*;

/**
 * main method for this example
 * 
 * @author immanuel
 *
 */
public class Main {
	
	public static void main(String[] args) {
		Set<InferenceMap> mapset = new HashSet<InferenceMap>();
		mapset.add(new Phi1to2());
		mapset.add(new Phi2_3to2());
		mapset.add(new Phi2Combine());
		mapset.add(new Phi2Neg());
		mapset.add(new Phi3());
		InferenceMaps maps = new InferenceMaps(mapset);
		
		
		String[] premises = {"bulb A·is connected in parallel with·bulbchain BC",
				"bulb B·is serial connected with·bulb C",
				"the voltage of bulbchain BC·is bigger than·the voltage of bulb B",
				"a bigger voltage·means·a bigger current",
				"a bigger voltage·means·a bigger luminosity",
				"the current through bulb A·is bigger than·the current through bulbchain BC"};
		
		Set<AssertionInterface> valid = new HashSet<AssertionInterface>();
		for (int i=0;i<premises.length;++i) {
			valid.add(new Assertion(premises[i]));
		}
		
		int step=0;
		int size=0;
		
		while (step < 4) {
			size = valid.size();
			
			System.out.println("  ++++ Step "+step+" ++++\n\n");
			if (step > 0)
				valid.addAll(InferredAssertion.nonTrivial(maps.inferNew(valid)));
			
			TreeSet<String> ordered = new TreeSet<String>();
			for (Iterator<AssertionInterface> it = valid.iterator();it.hasNext();){
				AssertionInterface a = it.next();
				
				ordered.add(a.toString());
			}
			
			for (Iterator<String> it=ordered.iterator();it.hasNext();) {
				String s = it.next();
				System.out.println(s);
			}
			
			step++;
			System.out.println("\n   +++ premise assertions "+size+" -> "+valid.size());
		}
		
		
	}

}
