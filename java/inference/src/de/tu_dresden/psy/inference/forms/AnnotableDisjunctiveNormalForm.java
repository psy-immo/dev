/**
 * DisjunctiveNormalForm.java, (c) 2012, Immanuel Albrecht; Dresden University
 * of Technology, Professur für die Psychologie des Lernen und Lehrens
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

package de.tu_dresden.psy.inference.forms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * implements a disjunctive normal form of Atoms, i.e. a term of the form
 * 
 * (x1_1 ^ x1_2 ^ .. ^ x1_n1) v ( ... ) ... v (xm_1 ^ xm_2 ^ .. ^ x1_nm)
 * 
 * where every atom may be annotated as trivial. (We write a prime ' to signal
 * this case.)
 * 
 * A term like
 * 
 * (m' ^ n) v (p ^ q) v (r' ^ s')
 * 
 * may be read in context of conclusion left-hand-sides
 * 
 * (m' ^ n) v (p ^ q) v (r' ^ s') => A
 * 
 * means, that if p and q is stated, A follows using rules that are not marked
 * trivial; and that if r and s is stated, A follows using rules that are marked
 * trivial; and that if m and n is stated, A follows using both marked and
 * unmarked rules
 * 
 * 
 * @author albrecht
 * 
 */

public class AnnotableDisjunctiveNormalForm<Atoms> {

	/**
	 * store the annotated Atoms
	 */

	private Set<Set<Atoms>> termForm;


	/**
	 * create empty form
	 */
	public AnnotableDisjunctiveNormalForm() {
		this.termForm = new HashSet<Set<Atoms>>();
	}

	/**
	 * create a form consisting of a single atom
	 * 
	 * @param a
	 *            atom
	 */

	public AnnotableDisjunctiveNormalForm(Atoms a) {
		this.termForm = new HashSet<Set<Atoms>>();
		Set<Atoms> conj = new HashSet<Atoms>();
		conj.add(a);
		this.termForm.add(conj);
	}

	/**
	 * create a form consisting of a single conjunction
	 * 
	 * @param initialConjunction
	 */

	public AnnotableDisjunctiveNormalForm(
			Collection<? extends Atoms> initialConjunction) {
		this.termForm = new HashSet<Set<Atoms>>();
		Set<Atoms> conj = new HashSet<Atoms>();
		conj.addAll(initialConjunction);
		this.termForm.add(conj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.termForm == null) ? 0 : this.termForm.hashCode());
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
		@SuppressWarnings("rawtypes")
		AnnotableDisjunctiveNormalForm other = (AnnotableDisjunctiveNormalForm) obj;
		if (this.termForm == null) {
			if (other.termForm != null) {
				return false;
			}
		} else if (!this.termForm.equals(other.termForm)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * replace this DNF with the join (e.g. the disjunction) of this DNF with
	 * the other DNF
	 * 
	 * @param other
	 */

	public void join(AnnotableDisjunctiveNormalForm<Atoms> other) {
		this.termForm.addAll(other.termForm);
	}

	/**
	 * 
	 * replace this DNF with the meet (e.g. the conjunction) of this DNF with
	 * the other DNF
	 * 
	 * @param other
	 */

	public void meet(AnnotableDisjunctiveNormalForm<Atoms> other) {
		Set<Set<Atoms>> new_term_form = new HashSet<Set<Atoms>>();

		for (Set<Atoms> left : this.termForm) {
			for (Set<Atoms> right : other.termForm) {
				Set<Atoms> conjunction = new HashSet<Atoms>();
				conjunction.addAll(left);
				conjunction.addAll(right);
				new_term_form.add(conjunction);
			}
		}

		this.termForm = new_term_form;
	}

	/**
	 * for each conjunction that contains the atom, the meet of all other
	 * conjunction literals and the replacement is joined to this DNF.
	 * 
	 * @param atom
	 * @param replacement
	 */

	public void replaceJoin(Atoms atom,
			AnnotableDisjunctiveNormalForm<Atoms> replacement) {
		Set<Set<Atoms>> new_forms = new HashSet<Set<Atoms>>();

		for (Set<Atoms> conjunction : this.termForm) {
			if (conjunction.contains(atom)) {
				AnnotableDisjunctiveNormalForm<Atoms> part = new AnnotableDisjunctiveNormalForm<Atoms>(
						conjunction);
				part.termForm.iterator().next().remove(atom);
				part.meet(replacement);
				new_forms.addAll(part.termForm);
			}
		}

		this.termForm.addAll(new_forms);
	}

	/**
	 * 
	 * @return Set containing each conjunction represented as Set of Atoms
	 */

	public Set<Set<Atoms>> getTerm() {
		return this.termForm;
	}

	@Override
	public String toString() {
		StringBuffer presentation = new StringBuffer();
		for (Set<Atoms> conjs : this.termForm) {
			if (presentation.length() == 0) {
				presentation.append("     (");
			} else {
				presentation.append(" or (");
			}

			boolean first = true;

			for (Atoms a : conjs) {
				if (first) {
					first = false;
					presentation.append("   ");
				} else {
					presentation.append("   and   ");
				}

				presentation.append(a.toString());
				presentation.append("\n");
			}

			presentation.append(")");
		}

		return presentation.toString();
	}
}
