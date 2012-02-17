/**
 * RegExpInferenceMap.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import de.tu_dresden.psy.inference.Assertion;
import de.tu_dresden.psy.inference.Assertion.AssertionPart;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferredAssertion;
import de.tu_dresden.psy.regexp.StringRelationInterface;
import de.tu_dresden.psy.regexp.StringRelationJoin;

/**
 * implements inference on String-Assertions via regular expressions
 * 
 * @author albrecht
 * 
 */
public class RegExpInferenceMap implements InferenceMap {

	private String name;

	private Vector<AssertionFilter> premiseForms;

	/**
	 * interface for checking whether a given premise-vector is compatible with
	 * the conclusion rules
	 * 
	 * @author albrecht
	 * 
	 */

	public static interface ConstraintInterface {
		/**
		 * 
		 * @param premises
		 * @return true, if the premises are compatible
		 */
		public boolean check(Vector<AssertionInterface> premises);
	}

	/**
	 * implements a check whether given premiseForms are compatible
	 * 
	 * @author albrecht
	 */

	public static class IsCompatibleChecker implements ConstraintInterface {
		private Assertion.AssertionPart leftPart;
		private Assertion.AssertionPart rightPart;
		private int leftIndex;
		private int rightIndex;

		private StringRelationInterface phi;

		@Override
		public boolean check(Vector<AssertionInterface> premises) {
			Object left = Assertion.getAssertionPart(premises.get(leftIndex),
					leftPart);
			Object right = Assertion.getAssertionPart(premises.get(rightIndex),
					rightPart);
			if (phi != null) {
				if (left instanceof String) {
					String x = (String) left;
					return phi.allMaps(x).contains(right);
				} else
					return false;
			} else
				return left.equals(right);
		}

		/**
		 * generate a check whether the left-indexed-part under the relation phi
		 * contains the right-indexed-part
		 * 
		 * @param leftIndex
		 * @param leftPart
		 * @param rightIndex
		 * @param rightPart
		 * @param phi
		 *            a SplittedStringRelation or <b>null</b> to check for
		 *            equality of parts
		 */
		public IsCompatibleChecker(int leftIndex,
				Assertion.AssertionPart leftPart, int rightIndex,
				Assertion.AssertionPart rightPart, StringRelationInterface phi) {
			this.phi = phi;
			this.leftIndex = leftIndex;
			this.leftPart = leftPart;
			this.rightIndex = rightIndex;
			this.rightPart = rightPart;
		}
	}

	public static class AdvancedCompatibleChecker implements
			ConstraintInterface {
		private Assertion.AssertionPart leftPart;
		private Assertion.AssertionPart rightPart;
		private int leftIndex;
		private int rightIndex;

		private StringRelationInterface phi;
		private StringRelationInterface psi;

		@Override
		public boolean check(Vector<AssertionInterface> premises) {
			Object left = Assertion.getAssertionPart(premises.get(leftIndex),
					leftPart);
			Object right = Assertion.getAssertionPart(premises.get(rightIndex),
					rightPart);

			if (left instanceof String) {
				String x = (String) left;
				if (right instanceof String) {
					String y = (String) right;

					Set<String> s = phi.allMaps(x);

					s.retainAll(psi.allMaps(y));

					return s.isEmpty() == false;
				}
			}

			return false;
		}

		/**
		 * generate a check whether the left-indexed-part under the relation phi
		 * and the right-indexed-part under the relation psi have non-empty
		 * intersection
		 * 
		 * @param leftIndex
		 * @param leftPart
		 * @param phi
		 * @param rightIndex
		 * @param rightPart
		 * @param psi
		 *            a SplittedStringRelation or <b>null</b> to check for
		 *            equality of parts
		 */
		public AdvancedCompatibleChecker(int leftIndex,
				Assertion.AssertionPart leftPart, StringRelationInterface phi,
				int rightIndex, Assertion.AssertionPart rightPart,
				StringRelationInterface psi) {
			this.phi = phi;
			this.leftIndex = leftIndex;
			this.leftPart = leftPart;
			this.rightIndex = rightIndex;
			this.rightPart = rightPart;
			this.psi = psi;
		}
	}

	/**
	 * implements a constraint that relates parts of the premises and checks,
	 * whether the yielded intersection is non empty
	 * 
	 * @author albrecht
	 * 
	 */
	public static class NonEmptyIntersectionChecker implements
			ConstraintInterface {
		private Vector<AssertionPart> part;
		private Vector<Integer> index;
		private Vector<StringRelationInterface> phi;

		@Override
		public boolean check(Vector<AssertionInterface> premises) {
			if (index.isEmpty())
				return true;

			Set<String> intersection = new HashSet<String>();

			int i = 0;

			int idx = index.get(i);
			StringRelationInterface rel = phi.get(i);
			AssertionPart pt = part.get(i);

			if (Assertion.getAssertionPart(premises.get(idx), pt) instanceof String) {
				String value = (String) Assertion.getAssertionPart(
						premises.get(idx), pt);

				if (rel == null) {
					if (intersection.contains(value)) {
						intersection.clear();
						intersection.add(value);
					} else
						return false;
				} else {
					intersection.retainAll(rel.allMaps(value));
					if (intersection.isEmpty())
						return false;
				}
			} else
				return false;

			return true;
		}

		/**
		 * add another premise part to be met with the intersection under a
		 * given relation
		 * 
		 * @param idx
		 * @param part
		 * @param relation
		 */
		public void addCheckPart(int idx, AssertionPart part,
				StringRelationInterface relation) {
			this.part.add(part);
			this.index.add(idx);
			this.phi.add(relation);
		}

		/**
		 * add another premise part to be in the intersection
		 * 
		 * @param idx
		 * @param part
		 */
		public void addCheckPart(int idx, AssertionPart part) {
			this.part.add(part);
			this.index.add(idx);
			this.phi.add(null);
		}

		/**
		 * generate a check whether the left-indexed-part under the relation phi
		 * and the right-indexed-part under the relation psi have non-empty
		 * intersection
		 * 
		 * @param leftIndex
		 * @param leftPart
		 * @param phi
		 * @param rightIndex
		 * @param rightPart
		 * @param psi
		 *            a SplittedStringRelation or <b>null</b> to check for
		 *            equality of parts
		 */
		public NonEmptyIntersectionChecker() {
			this.phi = new Vector<StringRelationInterface>();
			this.index = new Vector<Integer>();
			this.part = new Vector<Assertion.AssertionPart>();
		}
	}

	private Set<ConstraintInterface> checkPremises;

	private static class Morpher {
		StringRelationInterface phi;
		int index;
		Assertion.AssertionPart part;

		public Morpher(StringRelationInterface phi, int index,
				AssertionPart part) {
			
			this.phi = phi;
			this.index = index;
			this.part = part;
		}

		/**
		 * do not initialize MorpherConstant
		 */
		protected Morpher() {

		}

		Set<String> getMorphed(Vector<AssertionInterface> premises) {
			String input = (String) Assertion.getAssertionPart(
					premises.get(index), part);
			if (phi != null)
				return phi.allMaps(input);
			else {
				Set<String> result = new HashSet<String>();
				result.add(input);
				return result;
			}
		}
	}
	
	private static class MorpherConstant extends Morpher {
		Set<String> value;
		
		public MorpherConstant(String constant) {
			value = new HashSet<String>();
			value.add(constant);
		}
		
		@Override
		Set<String> getMorphed(Vector<AssertionInterface> premises) {
			return value;
		}
	}

	public static interface PremiseCombinatorInterface {
		/**
		 * take a vector of given premises and recombine them
		 * 
		 * @param premises
		 * @return recombined premises
		 */
		public Set<AssertionInterface> combine(
				Vector<AssertionInterface> premises);
	}

	/**
	 * implements the recombination of valid and checked premises into new
	 * assertions
	 * 
	 * @author albrecht
	 * 
	 */
	public static class PremiseCombinator implements PremiseCombinatorInterface {

		private Morpher subject, predicate, object;

		private InferenceMap parentRule;

		protected PremiseCombinator() {
		}

		public PremiseCombinator(InferenceMap parentRule, int idxS,
				AssertionPart partS, StringRelationInterface phiS, int idxP,
				AssertionPart partP, StringRelationInterface phiP, int idxO,
				AssertionPart partO, StringRelationInterface phiO) {
			this.parentRule = parentRule;
			this.subject = new Morpher(phiS, idxS, partS);

			this.predicate = new Morpher(phiP, idxP, partP);

			this.object = new Morpher(phiO, idxO, partO);

		}

		@Override
		public Set<AssertionInterface> combine(
				Vector<AssertionInterface> premises) {
			Set<AssertionInterface> results = new HashSet<AssertionInterface>();

			Set<String> subjects = subject.getMorphed(premises);
			Set<String> predicates = predicate.getMorphed(premises);
			Set<String> objects = object.getMorphed(premises);

			for (String s : subjects) {
				for (String p : predicates) {
					for (String o : objects) {
						results.add(new InferredAssertion(parentRule,
								new Assertion(s, p, o), premises));
					}
				}
			}

			return results;
		}
	}

	/**
	 * 
	 * implements an advanced recombination method
	 * 
	 * @author albrecht
	 * 
	 */

	public static class AdvancedPremiseCombinator implements
			PremiseCombinatorInterface {

		private Vector<Morpher> subject;
		private Vector<Morpher> predicate;
		private Vector<Morpher> object;

		private InferenceMap parentRule;

		public AdvancedPremiseCombinator(InferenceMap parent) {
			parentRule = parent;
			subject = new Vector<RegExpInferenceMap.Morpher>();
			predicate = new Vector<RegExpInferenceMap.Morpher>();
			object = new Vector<RegExpInferenceMap.Morpher>();

		}

		public void addSubjectPart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			subject.add(new Morpher(phi, idx, part));
		}

		public void addObjectPart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			object.add(new Morpher(phi, idx, part));
		}

		public void addPredicatePart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			predicate.add(new Morpher(phi, idx, part));
		}
		
		public void addPart(AssertionPart targetPart, StringRelationInterface phi, int idx,
				AssertionPart sourcePart) {
			if (targetPart == AssertionPart.subject) {
				addSubjectPart(phi, idx, sourcePart);
			} else if (targetPart == AssertionPart.predicate) {
				addPredicatePart(phi, idx, sourcePart);
			} else if (targetPart == AssertionPart.object) {
				addObjectPart(phi, idx, sourcePart);
			}
		}
		
		public void addConstantPart(AssertionPart targetPart, String value) {
			if (targetPart == AssertionPart.subject) {
				subject.add(new MorpherConstant(value));
			} else if (targetPart == AssertionPart.predicate) {
				predicate.add(new MorpherConstant(value));
			} else if (targetPart == AssertionPart.object) {
				object.add(new MorpherConstant(value));
			}
		}

		@Override
		public Set<AssertionInterface> combine(
				Vector<AssertionInterface> premises) {
			Set<AssertionInterface> results = new HashSet<AssertionInterface>();

			Set<String> subjects = new HashSet<String>();
			Set<String> predicates = new HashSet<String>();
			Set<String> objects = new HashSet<String>();
			Set<String> concats = new HashSet<String>();

			subjects.add("");
			objects.add("");
			predicates.add("");

			for (int i = 0; i < subject.size(); i++) {
				concats.clear();

				for (String s1 : subjects) {
					for (String s2 : subject.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				subjects.clear();
				subjects.addAll(concats);
			}

			for (int i = 0; i < object.size(); i++) {
				concats.clear();

				for (String s1 : objects) {
					for (String s2 : object.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				objects.clear();
				objects.addAll(concats);
			}

			for (int i = 0; i < predicate.size(); i++) {
				concats.clear();

				for (String s1 : predicates) {
					for (String s2 : predicate.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				predicates.clear();
				predicates.addAll(concats);
			}

			for (String s : subjects) {
				for (String p : predicates) {
					for (String o : objects) {
						results.add(new InferredAssertion(parentRule,
								new Assertion(s, p, o), premises));
					}
				}
			}

			return results;
		}

	}

	/**
	 * implements cross-product recombination of premise forms
	 * 
	 * @author albrecht
	 */
	public static class CrossProductRecombinator implements
			Iterable<Vector<AssertionInterface>> {

		private Vector<Vector<AssertionInterface>> factors;

		public CrossProductRecombinator(
				Vector<Vector<AssertionInterface>> factors) {
			this.factors = factors;
		}

		public static class CrossProductIterator implements
				Iterator<Vector<AssertionInterface>> {

			private Vector<Integer> currentIndices;

			private int factorCount;

			private Vector<Vector<AssertionInterface>> factors;

			private Vector<AssertionInterface> currentElement;
			private Vector<AssertionInterface> nextElement;

			private boolean nextIndex() {
				for (int index = factorCount - 1; index >= 0; --index) {
					int new_value = currentIndices.get(index) + 1;
					if (new_value >= factors.get(index).size()) {
						currentIndices.set(index, 0);
					} else {
						currentIndices.set(index, new_value);
						return true;
					}
				}
				return false;
			}

			private void retrieveNextElement() {
				if (nextIndex() == false) {
					this.nextElement = null;
				} else {
					for (int i = 0; i < factorCount; ++i) {
						this.nextElement.set(i,
								factors.get(i).get(currentIndices.get(i)));
					}
				}
			}

			public CrossProductIterator(
					Vector<Vector<AssertionInterface>> factors) {
				this.factors = factors;
				this.factorCount = factors.size();
				boolean noMore = false;
				currentIndices = new Vector<Integer>(factorCount);
				for (int i = 0; i < factorCount; ++i) {
					currentIndices.add(0);
					if (factors.get(i).isEmpty() == true) {
						noMore = true;
					}
				}
				if (noMore == true) {
					this.currentElement = null;
				} else {
					this.currentElement = new Vector<AssertionInterface>();
					this.nextElement = new Vector<AssertionInterface>();

					for (int i = 0; i < factorCount; ++i) {
						this.currentElement.add(factors.get(i).get(0));
						this.nextElement.add(factors.get(i).get(0));
					}
				}
			}

			@Override
			public boolean hasNext() {

				return currentElement != null;
			}

			@Override
			public Vector<AssertionInterface> next() {
				retrieveNextElement();

				Vector<AssertionInterface> swap = this.currentElement;
				this.currentElement = this.nextElement;
				this.nextElement = swap;

				return swap;
			}

			@Override
			public void remove() {
				throw new RuntimeException(
						"Cannot remove elements from cross product");
			}
		};

		@Override
		public Iterator<Vector<AssertionInterface>> iterator() {
			return new CrossProductIterator(factors);
		}
	}

	/**
	 * interface that returns a new iterable of assertion vectors for a given
	 * vector of premise that have given forms
	 * 
	 * @author albrecht
	 * 
	 */
	public static interface RecombinatorInterface {
		Iterable<Vector<AssertionInterface>> getProduct(
				Vector<Vector<AssertionInterface>> premises);
	}

	/**
	 * 
	 * use CrossProductRecombinator with RecombinatorInterface
	 * 
	 * @author albrecht
	 * 
	 */

	public static class CrossProduct implements RecombinatorInterface {
		@Override
		public Iterable<Vector<AssertionInterface>> getProduct(
				Vector<Vector<AssertionInterface>> premises) {
			return new CrossProductRecombinator(premises);
		}
	};

	private Set<PremiseCombinatorInterface> conclusions;

	private RecombinatorInterface mergePremises;

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises) {

		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();
		Vector<Vector<AssertionInterface>> premises = new Vector<Vector<AssertionInterface>>();

		for (AssertionFilter filter : premiseForms) {
			premises.add(new Vector<AssertionInterface>(filter
					.filter(validPremises)));
		}

		for (Vector<AssertionInterface> premiseVector : mergePremises
				.getProduct(premises)) {
			boolean passed = true;
			for (ConstraintInterface check : checkPremises) {
				if (check.check(premiseVector) == false) {
					passed = false;
					break;
				}
			}
			if (passed) {
				for (PremiseCombinatorInterface combine : conclusions) {
					inferred.addAll(combine.combine(premiseVector));
				}
			}
		}

		return inferred;
	}

	@Override
	public String ruleName() {
		return this.name;
	}

	/**
	 * create an empty RegExp-Inference Map
	 * 
	 * @param name
	 *            attached rule name
	 */

	public RegExpInferenceMap(String name) {
		this.mergePremises = new CrossProduct();
		this.name = name;
		this.conclusions = new HashSet<RegExpInferenceMap.PremiseCombinatorInterface>();
		this.premiseForms = new Vector<AssertionFilter>();
		this.checkPremises = new HashSet<RegExpInferenceMap.ConstraintInterface>();
	}

	public void addPremiseForm(String subjectPattern, String predicatePattern,
			String objectPattern) {
		premiseForms.add(new AssertionFilter(subjectPattern, predicatePattern,
				objectPattern));
	}

	/**
	 * check for equality of assertions' parts
	 * 
	 * @param leftIndex
	 * @param leftPart
	 * @param rightIndex
	 * @param rightPart
	 */

	public void addPremiseConstraint(int leftIndex,
			Assertion.AssertionPart leftPart, int rightIndex,
			Assertion.AssertionPart rightPart) {
		checkPremises.add(new IsCompatibleChecker(leftIndex, leftPart,
				rightIndex, rightPart, null));
	}

	public void addConstraint(ConstraintInterface constraint) {

		checkPremises.add(constraint);

	}

	/**
	 * check whether the left part under the rule contains the right part
	 * 
	 * @param leftIndex
	 * @param leftPart
	 * @param rightIndex
	 * @param rightPart
	 * @param delimitedRule
	 *            delimited rule, see StringRelationJoin
	 */
	public void addPremiseConstraint(int leftIndex,
			Assertion.AssertionPart leftPart, int rightIndex,
			Assertion.AssertionPart rightPart, String delimitedRule) {
		checkPremises.add(new IsCompatibleChecker(leftIndex, leftPart,
				rightIndex, rightPart, new StringRelationJoin(delimitedRule)));
	}

	/**
	 * adds a new conclusion derivation rule with morphing subject, predicate
	 * and object from parts of the premises
	 * 
	 * @param idxS
	 * @param partS
	 * @param phiS
	 * @param idxP
	 * @param partP
	 * @param phiP
	 * @param idxO
	 * @param partO
	 * @param phiO
	 */

	public void addConclusion(int idxS, AssertionPart partS, String phiS,
			int idxP, AssertionPart partP, String phiP, int idxO,
			AssertionPart partO, String phiO) {
		conclusions.add(new PremiseCombinator(this, idxS, partS,
				new StringRelationJoin(phiS), idxP, partP,
				new StringRelationJoin(phiP), idxO, partO,
				new StringRelationJoin(phiO)));
	}

	/**
	 * adds a new conclusion derivation rule by copying subject, predicate and
	 * object from parts of the premises
	 * 
	 * @param idxS
	 * @param partS
	 * @param idxP
	 * @param partP
	 * @param idxO
	 * @param partO
	 */

	public void addConclusion(int idxS, AssertionPart partS, int idxP,
			AssertionPart partP, int idxO, AssertionPart partO) {
		conclusions.add(new PremiseCombinator(this, idxS, partS, null, idxP,
				partP, null, idxO, partO, null));
	}

	/**
	 * adds a new conclusion
	 * 
	 * @param combinator
	 */

	public void addPremiseCombinator(PremiseCombinatorInterface combinator) {
		conclusions.add(combinator);
	}

}
