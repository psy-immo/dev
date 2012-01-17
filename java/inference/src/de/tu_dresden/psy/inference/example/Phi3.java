/**
 * Phi3.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import de.tu_dresden.psy.inference.*;

public class Phi3 implements InferenceMap {

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises) {
		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();

		Map<String, Set<AssertionInterface>> by_subject = new HashMap<String, Set<AssertionInterface>>();
		Map<String, Set<AssertionInterface>> by_object = new HashMap<String, Set<AssertionInterface>>();

		Iterator<AssertionInterface> it;
		for (it = validPremises.iterator(); it.hasNext();) {
			AssertionInterface a = it.next();

			if ((a.getPredicate() instanceof String)
					&& (a.getSubject() instanceof String)
					&& (a.getObject() instanceof String)) {
				String predicate = (String) a.getPredicate();
				String subject = (String) a.getSubject();
				String object = (String) a.getObject();

				boolean filter = true;

				String s1 = null;
				String o1 = null;

				if (subject.startsWith("a smaller ")) {
					s1 = subject.substring(10);
				} else if (subject.startsWith("a bigger ")) {
					s1 = subject.substring(9);
				} else
					filter = false;

				if (object.startsWith("a smaller ")) {
					o1 = object.substring(10);
				} else if (object.startsWith("a bigger ")) {
					o1 = object.substring(9);
				} else
					filter = false;

				if ((predicate.equals("means") == true) && (filter == true)) {
					if (by_subject.containsKey(s1)) {
						by_subject.get(s1).add(a);
					} else {
						Set<AssertionInterface> s = new HashSet<AssertionInterface>();
						s.add(a);
						by_subject.put(s1, s);
					}

					if (by_object.containsKey(o1)) {
						by_object.get(o1).add(a);
					} else {
						Set<AssertionInterface> s = new HashSet<AssertionInterface>();
						s.add(a);
						by_object.put(o1, s);
					}
				}
			}
		}

		Iterator<String> iy;
		for (iy = by_object.keySet().iterator(); iy.hasNext();) {
			String y = iy.next();
			if (by_subject.containsKey(y)) {
				Iterator<AssertionInterface> ixy, iyz;
				for (ixy = by_object.get(y).iterator(); ixy.hasNext();) {
					AssertionInterface xy = ixy.next();
					for (iyz = by_subject.get(y).iterator(); iyz.hasNext();) {
						
						AssertionInterface yz = iyz.next();

						String x = (String) xy.getSubject();
						String y1 = (String) xy.getObject();
						String y2 = (String) yz.getSubject();
						String z = (String) yz.getObject();

						boolean monotone_xy = (x.startsWith("a smaller") == y1
								.startsWith("a smaller"));
						boolean monotone_yz = (y1.startsWith("a smaller") == z
								.startsWith("a smaller"));
						
						boolean monotone_xz = monotone_xy == monotone_yz;
						
						if (x.startsWith("a smaller")) {
							x = x.substring(10);
						} else x = x.substring(9); //x.startsWith("a bigger")
						
						if (z.startsWith("a smaller")) {
							z = z.substring(10);
						} else z = z.substring(9); //x.startsWith("a bigger")
						
						if (monotone_xz == true) {
							inferred.add(new Assertion("a smaller "+x, "means", "a smaller "+z));
							inferred.add(new Assertion("a bigger "+x, "means", "a bigger "+z));
						} else {
							inferred.add(new Assertion("a bigger "+x, "means", "a smaller "+z));
							inferred.add(new Assertion("a smaller "+x, "means", "a bigger "+z));
						}
					}
				}
			}
		}

		return inferred;
	}
	
	public static void main(String[] args) {
		Phi3 phi3 = new Phi3();
		
		Set<AssertionInterface> premises = new HashSet<AssertionInterface>();
		
		premises.add(new Assertion("a smaller A", "means", "a bigger B"));
		premises.add(new Assertion("a bigger B", "means", "a bigger C"));
		premises.add(new Assertion("a bigger B", "means", "a smaller D"));
		premises.add(new Assertion("a bigger A", "means", "a bigger E"));
		premises.add(new Assertion("a bigger E", "means", "a bigger B"));
		
		Set<AssertionInterface> level1 = phi3.inferNew(premises);
		
		System.out.println("first degree: ");
		
		for (Iterator<AssertionInterface> it = level1.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
		
		level1.addAll(premises);
		
		Set<AssertionInterface> level2 = phi3.inferNew(level1);
		
		System.out.println("first+second degree: ");
		
		for (Iterator<AssertionInterface> it = level2.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
		
		level1.addAll(level2);
		
		level2 = phi3.inferNew(level1);
		
		System.out.println("first+second+third degree: ");
		
		for (Iterator<AssertionInterface> it = level2.iterator();it.hasNext();) {
			System.out.println(it.next());
		}
	}

}
