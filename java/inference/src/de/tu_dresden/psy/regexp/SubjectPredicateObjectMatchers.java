package de.tu_dresden.psy.regexp;

import java.util.HashSet;
import java.util.Set;

import de.tu_dresden.psy.inference.Assertion;

public class SubjectPredicateObjectMatchers extends
		SubjectPredicateObjectMatcher {
	
	private Set<SubjectPredicateObjectMatcher> matchers;
	
	public SubjectPredicateObjectMatchers(Set<SubjectPredicateObjectMatcher> parsers) {
		
		matchers = parsers;
		
	}
	
	@Override
	public Set<Assertion> match(String assertion) {
		Set<Assertion> matches = new HashSet<Assertion>();
		for (SubjectPredicateObjectMatcher matcher : matchers) {
			matches.addAll(matcher.match(assertion));
		}
		return matches;
	}

}
