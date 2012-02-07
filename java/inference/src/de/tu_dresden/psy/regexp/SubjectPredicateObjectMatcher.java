/**
 * SubjectPredicateObjectMatcher.java, (c) 2012, Immanuel Albrecht; Dresden
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

package de.tu_dresden.psy.regexp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.tu_dresden.psy.inference.*;

/**
 * 
 * implements a matcher for SPO sentences
 * 
 * @author albrecht
 * 
 */

public class SubjectPredicateObjectMatcher {

	private KRegExp matcher;
	
	public static String delimiterPattern = "(\\s+|\\s*·\\s*)";

	/**
	 * construct a String -> Assertion (of Strings) matcher for sentences of the
	 * form [WHITES?](SUBJECT)[DELIMITERS](PREDICATE)[DELIMITERS](OBJECT)[WHITES?]
	 * 
	 * @param subjectPattern
	 *            subject regular expression
	 * @param predicatePattern
	 *            predicate regular expression
	 * @param objectPattern
	 *            object regular expression
	 */

	public SubjectPredicateObjectMatcher(String subjectPattern,
			String predicatePattern, String objectPattern) {
		this.matcher = new KRegExp(new String[] { subjectPattern, delimiterPattern,
				predicatePattern, delimiterPattern, objectPattern });
	}

	/**
	 * make Assertion objects from String 
	 * @param assertion  assertion as string
	 * @return  set of Assertion objects that may represent this string according to this pattern
	 */
	
	public Set<Assertion> match(String assertion) {
		String trimmed = assertion.trim();
		Set<Assertion> results = new HashSet<Assertion>();
		
		Set<int[]> boundaries = matcher.matchChain(trimmed);
		
		for (Iterator<int[]> it = boundaries.iterator(); it.hasNext();) {
			int[] bounds = it.next();
			
			String subject = trimmed.substring(0,bounds[1]);
			String predicate = trimmed.substring(bounds[2],bounds[3]);
			String object = trimmed.substring(bounds[4]);
			
			results.add(new Assertion(subject, predicate, object));
		}
		
		return results;
	}
	
	/**
	 * test routine
	 * @param args
	 */
	
	public static void main(String[] args) {
		SubjectPredicateObjectMatcher spom = new SubjectPredicateObjectMatcher("Ab*p*\\s*p*", "p*\\s*p*", "Ob*");
		System.out.println(spom.match("Abbp pp pp Ob"));
	}
}
