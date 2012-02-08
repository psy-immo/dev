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
import de.tu_dresden.psy.inference.Assertion.AssertionPart;
import de.tu_dresden.psy.inference.regexp.RegExpInferenceMap;
import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatcher;

/**
 * main method for this example
 * 
 * @author immanuel
 * 
 */
public class Main {

	private static void hardcodedExample() {
		Set<InferenceMap> mapset = new HashSet<InferenceMap>();
		mapset.add(new Phi1to2());
		mapset.add(new Phi2_3to2());
		mapset.add(new Phi2Combine());
		mapset.add(new Phi2Neg());
		mapset.add(new Phi3());
		InferenceMaps maps = new InferenceMaps(mapset);

		String[] premises = {
				"bulb A·is connected in parallel with·bulbchain BC",
				"bulb B·is serial connected with·bulb C",
				"the voltage of bulbchain BC·is bigger than·the voltage of bulb B",
				"a bigger voltage·means·a bigger current",
				"a bigger voltage·means·a bigger luminosity",
				"the current through bulb A·is bigger than·the current through bulbchain BC" };

		Set<AssertionInterface> valid = new HashSet<AssertionInterface>();
		for (int i = 0; i < premises.length; ++i) {
			valid.add(new Assertion(premises[i]));
		}

		int step = 0;
		int size = 0;

		while (step < 4) {
			size = valid.size();

			System.out.println("  ++++ Step " + step + " ++++\n\n");
			if (step > 0)
				valid.addAll(InferredAssertion.nonTrivial(maps.inferNew(valid)));

			TreeSet<String> ordered = new TreeSet<String>();
			for (Iterator<AssertionInterface> it = valid.iterator(); it
					.hasNext();) {
				AssertionInterface a = it.next();

				ordered.add(a.toString());
			}

			for (Iterator<String> it = ordered.iterator(); it.hasNext();) {
				String s = it.next();
				System.out.println(s);
			}

			step++;
			System.out.println("\n   +++ premise assertions " + size + " -> "
					+ valid.size());
		}
	}

	private static void regexpExample() {
		Set<InferenceMap> mapset = new HashSet<InferenceMap>();

		/**
		 * fill the map set
		 */

		RegExpInferenceMap phi3 = new RegExpInferenceMap("trans");
		phi3.addPremiseForm(".*", "means", ".*");
		phi3.addPremiseForm(".*", "means", ".*");
		phi3.addPremiseConstraint(0, AssertionPart.object, 1,
				AssertionPart.subject);
		phi3.addConclusion(0, AssertionPart.subject, 0,
				AssertionPart.predicate, 1, AssertionPart.object);

		mapset.add(phi3);

		RegExpInferenceMap phi2neg = new RegExpInferenceMap("neg");
		phi2neg.addPremiseForm(".*", "is (as.*as|(bigger|smaller) than)", ".*");
		phi2neg.addConclusion(0, AssertionPart.object, ".*→»1", 0,
				AssertionPart.predicate,
				"is b.*→is smaller than¶is s.*→is bigger than¶is as.*as→»1", 0,
				AssertionPart.subject, ".*→»1");

		mapset.add(phi2neg);

		RegExpInferenceMap phi2monotone = new RegExpInferenceMap("monotone");
		phi2monotone.addPremiseForm(".*",
				"is ((bigger|smaller) than|as big as)", ".*");
		phi2monotone.addPremiseForm(".*",
				"is ((bigger|smaller) than|as big as)", ".*");

		phi2monotone.addPremiseConstraint(0, AssertionPart.predicate, 1,
				AssertionPart.predicate, "is (b|s).*→»1¶.*→is as big as");

		phi2monotone.addPremiseConstraint(0, AssertionPart.object, 1,
				AssertionPart.subject);

		phi2monotone.addConclusion(0, AssertionPart.subject, 0,
				AssertionPart.predicate, 1, AssertionPart.object);

		mapset.add(phi2monotone);


		RegExpInferenceMap phi1to2s = new RegExpInferenceMap("serial");
		phi1to2s.addPremiseForm(".*", "is serial connected with", ".*");
		phi1to2s.addConclusion(0, AssertionPart.subject,
				".*→the current through ·»1", 0, AssertionPart.predicate,
				".*→is as big as", 0, AssertionPart.object,
				".*→the current through ·»1");
		
		mapset.add(phi1to2s);
		
		RegExpInferenceMap phi1to2p = new RegExpInferenceMap("parallel");
		phi1to2p.addPremiseForm(".*", "is connected in parallel with", ".*");
		phi1to2p.addConclusion(0, AssertionPart.subject,
				".*→the voltage of ·»1", 0, AssertionPart.predicate,
				".*→is as big as", 0, AssertionPart.object,
				".*→the voltage of ·»1");
		
		mapset.add(phi1to2p);
		
		
		RegExpInferenceMap phi2_3to3m1 = new RegExpInferenceMap("combine-monotone");
		phi2_3to3m1.addPremiseForm("the.*(of|through)","is (as big as|(small|bigg)er than)", "the.*(of|through)");
		phi2_3to3m1.addPremiseForm("a smaller .*","means", "a smaller .*");
		
		//TODO.. :)
		
		mapset.add(phi2_3to3m1);

		/**
		 * initial premises
		 */

		String[] premises = {
				"bulb A is connected in parallel with bulbchain BC",
				"bulb B is serial connected with bulb C",
				"the voltage of bulbchain BC is bigger than the voltage of bulb B",
				"a bigger voltage means a bigger current",
				"a bigger voltage means a bigger luminosity",
				"the current through bulb A is bigger than the current through bulbchain BC",
				"my left leg is as crooked as my right leg",
				"X is as big as Y", "Y is smaller than Z",
				"V is bigger than X", "X is as big as X2", "Q means Q2",
				"Q2 means Q3", "Q4 means Q3" };

		/**
		 * chop into subject·predicate·object
		 */

		SubjectPredicateObjectMatcher matchStrings = new SubjectPredicateObjectMatcher(
		// subject starts with non-whitespace
				"\\S.*",
				// predicate is of either form: is ... with, is ... than, is as
				// ... as, means
				"(means|is(\\s.*\\swith|\\s.*\\sthan|\\s+as.*\\sas))",
				// object starts with non-whitespace
				"\\S.*"

		);

		Set<AssertionInterface> valid = new HashSet<AssertionInterface>();
		for (int i = 0; i < premises.length; ++i) {
			valid.addAll(matchStrings.match((premises[i])));
		}

		
		/**
		 * do some inference
		 */
		
		InferenceMaps maps = new InferenceMaps(mapset);

		int step = 0;
		int size = 0;

		while (step <= 100) {
			size = valid.size();

			System.out.println("  ++++ Step " + step + " ++++\n\n");
			if (step > 0)
				valid.addAll(InferredAssertion.nonTrivial(maps.inferNew(valid)));

			TreeSet<String> ordered = new TreeSet<String>();
			for (Iterator<AssertionInterface> it = valid.iterator(); it
					.hasNext();) {
				AssertionInterface a = it.next();

				ordered.add(a.toString());
			}

			for (Iterator<String> it = ordered.iterator(); it.hasNext();) {
				String s = it.next();
				System.out.println(s);
			}

			step++;
			System.out.println("\n   +++ premise assertions " + size + " -> "
					+ valid.size());

			if ((size == valid.size()) && (step > 1))
				break; // nothing new
		}
	}

	public static void main(String[] args) {
		// hardcodedExample();
		regexpExample();
	}

}
