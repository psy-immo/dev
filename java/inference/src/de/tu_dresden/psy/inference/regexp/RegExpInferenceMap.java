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
import de.tu_dresden.psy.inference.ExcessLimit;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferredAssertion;
import de.tu_dresden.psy.regexp.StringRelationInterface;
import de.tu_dresden.psy.regexp.StringRelationJoin;

/**
 * implements inference on String-Assertions via regular expressions
 * 
 * (the name parameter is the only information used to check for equality!)
 * 
 * @author albrecht
 * 
 */
public class RegExpInferenceMap implements InferenceMap {

	/**
	 * WATCH OUT!
	 * 
	 * we will compare rules only based on this name and not on the actual rule
	 * !!!
	 * 
	 */
	private String name;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		RegExpInferenceMap other = (RegExpInferenceMap) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	private Vector<AssertionFilter> premiseForms;

	/**
	 * store, whether this rule has been marked trivial
	 */
	private boolean isTrivialRule;

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
			Object left = Assertion.getAssertionPart(premises.get(this.leftIndex),
					this.leftPart);
			Object right = Assertion.getAssertionPart(premises.get(this.rightIndex),
					this.rightPart);
			if (this.phi != null) {
				if (left instanceof String) {
					String x = (String) left;
					return this.phi.allMaps(x).contains(right);
				} else {
					return false;
				}
			} else {
				return left.equals(right);
			}
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
			Object left = Assertion.getAssertionPart(premises.get(this.leftIndex),
					this.leftPart);
			Object right = Assertion.getAssertionPart(premises.get(this.rightIndex),
					this.rightPart);

			if (left instanceof String) {
				String x = (String) left;
				if (right instanceof String) {
					String y = (String) right;

					Set<String> s = this.phi.allMaps(x);

					s.retainAll(this.psi.allMaps(y));

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
					premises.get(this.index), this.part);
			if (this.phi != null) {
				return this.phi.allMaps(input);
			} else {
				Set<String> result = new HashSet<String>();
				result.add(input);
				return result;
			}
		}
	}

	private static class MorpherConstant extends Morpher {
		Set<String> value;

		public MorpherConstant(String constant) {
			this.value = new HashSet<String>();
			this.value.add(constant);
		}

		@Override
		Set<String> getMorphed(Vector<AssertionInterface> premises) {
			return this.value;
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

			Set<String> subjects = this.subject.getMorphed(premises);
			Set<String> predicates = this.predicate.getMorphed(premises);
			Set<String> objects = this.object.getMorphed(premises);

			for (String s : subjects) {
				for (String p : predicates) {
					for (String o : objects) {
						results.add(new InferredAssertion(this.parentRule,
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
			this.parentRule = parent;
			this.subject = new Vector<RegExpInferenceMap.Morpher>();
			this.predicate = new Vector<RegExpInferenceMap.Morpher>();
			this.object = new Vector<RegExpInferenceMap.Morpher>();

		}

		public void addSubjectPart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			this.subject.add(new Morpher(phi, idx, part));
		}

		public void addObjectPart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			this.object.add(new Morpher(phi, idx, part));
		}

		public void addPredicatePart(StringRelationInterface phi, int idx,
				AssertionPart part) {
			this.predicate.add(new Morpher(phi, idx, part));
		}

		public void addPart(AssertionPart targetPart,
				StringRelationInterface phi, int idx, AssertionPart sourcePart) {
			if (targetPart == AssertionPart.subject) {
				this.addSubjectPart(phi, idx, sourcePart);
			} else if (targetPart == AssertionPart.predicate) {
				this.addPredicatePart(phi, idx, sourcePart);
			} else if (targetPart == AssertionPart.object) {
				this.addObjectPart(phi, idx, sourcePart);
			}
		}

		public void addConstantPart(AssertionPart targetPart, String value) {
			if (targetPart == AssertionPart.subject) {
				this.subject.add(new MorpherConstant(value));
			} else if (targetPart == AssertionPart.predicate) {
				this.predicate.add(new MorpherConstant(value));
			} else if (targetPart == AssertionPart.object) {
				this.object.add(new MorpherConstant(value));
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

			for (int i = 0; i < this.subject.size(); i++) {
				concats.clear();

				for (String s1 : subjects) {
					for (String s2 : this.subject.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				subjects.clear();
				subjects.addAll(concats);
			}

			for (int i = 0; i < this.object.size(); i++) {
				concats.clear();

				for (String s1 : objects) {
					for (String s2 : this.object.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				objects.clear();
				objects.addAll(concats);
			}

			for (int i = 0; i < this.predicate.size(); i++) {
				concats.clear();

				for (String s1 : predicates) {
					for (String s2 : this.predicate.get(i).getMorphed(premises)) {
						concats.add(s1 + s2);
					}
				}

				predicates.clear();
				predicates.addAll(concats);
			}

			for (String s : subjects) {
				for (String p : predicates) {
					for (String o : objects) {
						results.add(new InferredAssertion(this.parentRule,
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
				for (int index = this.factorCount - 1; index >= 0; --index) {
					int new_value = this.currentIndices.get(index) + 1;
					if (new_value >= this.factors.get(index).size()) {
						this.currentIndices.set(index, 0);
					} else {
						this.currentIndices.set(index, new_value);
						return true;
					}
				}
				return false;
			}

			private void retrieveNextElement() {
				if (this.nextIndex() == false) {
					this.nextElement = null;
				} else {
					for (int i = 0; i < this.factorCount; ++i) {
						this.nextElement.set(i,
								this.factors.get(i).get(this.currentIndices.get(i)));
					}
				}
			}

			public CrossProductIterator(
					Vector<Vector<AssertionInterface>> factors) {
				this.factors = factors;
				this.factorCount = factors.size();
				boolean noMore = false;
				this.currentIndices = new Vector<Integer>(this.factorCount);
				for (int i = 0; i < this.factorCount; ++i) {
					this.currentIndices.add(0);
					if (factors.get(i).isEmpty() == true) {
						noMore = true;
					}
				}
				if (noMore == true) {
					this.currentElement = null;
				} else {
					this.currentElement = new Vector<AssertionInterface>();
					this.nextElement = new Vector<AssertionInterface>();

					for (int i = 0; i < this.factorCount; ++i) {
						this.currentElement.add(factors.get(i).get(0));
						this.nextElement.add(factors.get(i).get(0));
					}
				}
			}

			@Override
			public boolean hasNext() {

				return this.currentElement != null;
			}

			@Override
			public Vector<AssertionInterface> next() {
				this.retrieveNextElement();

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
			return new CrossProductIterator(this.factors);
		}
	}

	/**
	 * implements cross-product recombination of premise forms, where at least
	 * one form is marked to be new
	 * 
	 * @author albrecht
	 */
	public static class CrossProductNewRecombinator implements
	Iterable<Vector<AssertionInterface>> {

		private Vector<Vector<AssertionInterface>> factors;

		public CrossProductNewRecombinator(
				Vector<Vector<AssertionInterface>> factors) {
			this.factors = factors;
		}

		public static class CrossProductIfNewIterator implements
		Iterator<Vector<AssertionInterface>> {

			private Vector<Integer> currentIndices;
			private Vector<Boolean> isNewAssertionLeftOfIndex;

			private int factorCount;

			private Vector<Vector<AssertionInterface>> factors;

			private Vector<AssertionInterface> currentElement;
			private Vector<AssertionInterface> nextElement;

			private boolean nextIndex() {
				boolean do_it_again = true;
				while (do_it_again) {
					do_it_again = false;
					for (int index = this.factorCount - 1; index >= 0; --index) {
						int new_value = this.currentIndices.get(index) + 1;
						if (new_value >= this.factors.get(index).size()) {
							this.currentIndices.set(index, 0);
						} else {
							this.currentIndices.set(index, new_value);

							boolean have_new = this.isNewAssertionLeftOfIndex
									.get(index);
							for (int i = index; i < this.factorCount; ++i) {
								if (have_new == false) {
									if (this.factors.get(i)
											.get(this.currentIndices.get(i)).isOld() == false) {
										have_new = true;
									}
								}
								this.isNewAssertionLeftOfIndex.set(i + 1, have_new);
							}

							if (have_new) {
								// System.out.println("Is new: " +
								// factors.get(0).get(currentIndices.get(0)));
								return true;
							}

							// System.out.println("Not new: " +
							// factors.get(0).get(currentIndices.get(0)));

							/**
							 * here we have a new combination, but it has no new
							 * assertions in it, so skip!
							 */

							do_it_again = true;
							break;
						}
					}
				}
				return false;
			}

			private void retrieveNextElement() {
				if (this.nextIndex() == false) {
					this.nextElement = null;
				} else {
					for (int i = 0; i < this.factorCount; ++i) {
						this.nextElement.set(i,
								this.factors.get(i).get(this.currentIndices.get(i)));
					}
				}
			}

			public CrossProductIfNewIterator(
					Vector<Vector<AssertionInterface>> factors) {
				this.factors = factors;
				this.factorCount = factors.size();
				boolean noMore = false;

				this.currentIndices = new Vector<Integer>(this.factorCount);

				for (int i = 0; i < this.factorCount; ++i) {
					this.currentIndices.add(0);

					if (factors.get(i).isEmpty() == true) {
						noMore = true;
					}
				}

				if (noMore) {
					this.currentElement = null;
					return;
				}

				this.isNewAssertionLeftOfIndex = new Vector<Boolean>(this.factorCount + 1);
				this.isNewAssertionLeftOfIndex.add(false);

				boolean haveNewAssertion = false;

				for (int i = 0; i < this.factorCount; ++i) {
					if (haveNewAssertion == false) {
						if (factors.get(i).get(0).isOld() == false) {
							haveNewAssertion = true;
						}
					}
					this.isNewAssertionLeftOfIndex.add(haveNewAssertion);
				}

				/**
				 * get next index that is also a new combination, in order to do
				 * that, set the current index to (0,..,0,-1).
				 */

				this.currentIndices.set(this.factorCount - 1, -1);

				if (this.nextIndex() == false) {
					this.currentElement = null;
				} else {
					this.currentElement = new Vector<AssertionInterface>();
					this.nextElement = new Vector<AssertionInterface>();

					for (int i = 0; i < this.factorCount; ++i) {
						this.currentElement.add(factors.get(i).get(
								this.currentIndices.get(i)));
						this.nextElement.add(factors.get(i).get(0));
					}
				}
			}

			@Override
			public boolean hasNext() {

				return this.currentElement != null;
			}

			@Override
			public Vector<AssertionInterface> next() {
				this.retrieveNextElement();

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
			return new CrossProductIfNewIterator(this.factors);
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

	/**
	 * 
	 * use CrossProductNewRecombinator with RecombinatorInterface
	 * 
	 * @author albrecht
	 * 
	 */

	public static class CrossProductIfNew implements RecombinatorInterface {
		@Override
		public Iterable<Vector<AssertionInterface>> getProduct(
				Vector<Vector<AssertionInterface>> premises) {
			return new CrossProductNewRecombinator(premises);
		}
	};

	private Set<PremiseCombinatorInterface> conclusions;

	private RecombinatorInterface mergePremises;

	@Override
	public Set<AssertionInterface> inferNew(
			Set<AssertionInterface> validPremises, ExcessLimit limit) {

		Set<AssertionInterface> inferred = new HashSet<AssertionInterface>();
		Vector<Vector<AssertionInterface>> premises = new Vector<Vector<AssertionInterface>>();

		for (AssertionFilter filter : this.premiseForms) {
			premises.add(new Vector<AssertionInterface>(filter
					.filter(validPremises)));
		}

		int count = 0;

		for (Vector<AssertionInterface> premiseVector : this.mergePremises
				.getProduct(premises)) {
			if (count == 100) {
				count = 0;
				if (limit.continueTask() == false) {
					break;
				}

			} else {
				++count;
			}

			boolean passed = true;
			for (ConstraintInterface check : this.checkPremises) {
				if (check.check(premiseVector) == false) {
					passed = false;
					break;
				}
			}
			if (passed) {
				for (PremiseCombinatorInterface combine : this.conclusions) {
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

	public RegExpInferenceMap(String name, boolean trivial) {
		this.mergePremises = new CrossProductIfNew();
		this.name = name;
		this.conclusions = new HashSet<RegExpInferenceMap.PremiseCombinatorInterface>();
		this.premiseForms = new Vector<AssertionFilter>();
		this.checkPremises = new HashSet<ConstraintInterface>();
		this.isTrivialRule = trivial;
	}

	public void addPremiseForm(String subjectPattern, String predicatePattern,
			String objectPattern) {
		this.premiseForms.add(new AssertionFilter(subjectPattern, predicatePattern,
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
		this.checkPremises.add(new IsCompatibleChecker(leftIndex, leftPart,
				rightIndex, rightPart, null));
	}

	public void addConstraint(ConstraintInterface constraint) {

		this.checkPremises.add(constraint);

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
		this.checkPremises.add(new IsCompatibleChecker(leftIndex, leftPart,
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
		this.conclusions.add(new PremiseCombinator(this, idxS, partS,
				new StringRelationJoin(phiS), idxP, partP,
				new StringRelationJoin(phiP), idxO, partO,
				new StringRelationJoin(phiO)));
	}

	/**
	 * adds a new conclusion
	 * 
	 * @param conclusion
	 */
	public void addConclusion(PremiseCombinatorInterface conclusion) {
		this.conclusions.add(conclusion);
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
		this.conclusions.add(new PremiseCombinator(this, idxS, partS, null, idxP,
				partP, null, idxO, partO, null));
	}

	/**
	 * adds a new conclusion
	 * 
	 * @param combinator
	 */

	public void addPremiseCombinator(PremiseCombinatorInterface combinator) {
		this.conclusions.add(combinator);
	}

	@Override
	public boolean isTrivial() {
		return this.isTrivialRule;
	}

}
