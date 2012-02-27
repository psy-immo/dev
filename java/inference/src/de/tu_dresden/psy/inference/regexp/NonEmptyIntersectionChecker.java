package de.tu_dresden.psy.inference.regexp;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import de.tu_dresden.psy.inference.Assertion;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.Assertion.AssertionPart;
import de.tu_dresden.psy.regexp.StringRelationInterface;

/**
 * implements a constraint that relates parts of the premises and checks,
 * whether the yielded intersection is non empty
 * 
 * @author albrecht
 * 
 */
public class NonEmptyIntersectionChecker implements
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
				intersection.add(value);
			} else {
				intersection.addAll(rel.allMaps(value));
				if (intersection.isEmpty())
					return false;
			}
		} else
			return false;

		for (i = 1; i < index.size(); ++i) {

			idx = index.get(i);
			rel = phi.get(i);
			pt = part.get(i);

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
		}

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