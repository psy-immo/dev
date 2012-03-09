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
 * implements a disjunctive normal form of Atoms
 * 
 * @author albrecht
 * 
 */

public class DisjunctiveNormalForm<Atoms> {

	private Set<Set<Atoms>> termForm;

	/**
	 * create empty form
	 */
	public DisjunctiveNormalForm() {
		termForm = new HashSet<Set<Atoms>>();
	}

	/**
	 * create a form consisting of a single atom
	 * 
	 * @param a
	 *            atom
	 */

	public DisjunctiveNormalForm(Atoms a) {
		termForm = new HashSet<Set<Atoms>>();
		Set<Atoms> conj = new HashSet<Atoms>();
		conj.add(a);
		termForm.add(conj);
	}

	/**
	 * create a form consisting of a single conjunction
	 * 
	 * @param initialConjunction
	 */

	public DisjunctiveNormalForm(Collection<? extends Atoms> initialConjunction) {
		termForm = new HashSet<Set<Atoms>>();
		Set<Atoms> conj = new HashSet<Atoms>();
		conj.addAll(initialConjunction);
		termForm.add(conj);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((termForm == null) ? 0 : termForm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		DisjunctiveNormalForm other = (DisjunctiveNormalForm) obj;
		if (termForm == null) {
			if (other.termForm != null)
				return false;
		} else if (!termForm.equals(other.termForm))
			return false;
		return true;
	}

	/**
	 * 
	 * replace this DNF with the join (e.g. the disjunction) of this DNF with
	 * the other DNF
	 * 
	 * @param other
	 */

	public void join(DisjunctiveNormalForm<Atoms> other) {
		termForm.addAll(other.termForm);
	}

	/**
	 * 
	 * replace this DNF with the meet (e.g. the conjunction) of this DNF with
	 * the other DNF
	 * 
	 * @param other
	 */

	public void meet(DisjunctiveNormalForm<Atoms> other) {
		Set<Set<Atoms>> new_term_form = new HashSet<Set<Atoms>>();

		for (Set<Atoms> left : termForm) {
			for (Set<Atoms> right : other.termForm) {
				Set<Atoms> conjunction = new HashSet<Atoms>();
				conjunction.addAll(left);
				conjunction.addAll(right);
				new_term_form.add(conjunction);
			}
		}

		termForm = new_term_form;
	}

	/**
	 * for each conjunction that contains the atom, the meet of all other
	 * conjunction literals and the replacement is joined to this DNF.
	 * 
	 * @param atom
	 * @param replacement
	 */

	public void replaceJoin(Atoms atom, DisjunctiveNormalForm<Atoms> replacement) {
		Set<Set<Atoms>> new_forms = new HashSet<Set<Atoms>>();

		for (Set<Atoms> conjunction : termForm) {
			if (conjunction.contains(atom)) {
				DisjunctiveNormalForm<Atoms> part = new DisjunctiveNormalForm<Atoms>(
						conjunction);
				part.termForm.iterator().next().remove(atom);
				part.meet(replacement);
				new_forms.addAll(part.termForm);
			}
		}

		termForm.addAll(new_forms);
	}

	/**
	 * 
	 * @return Set containing each conjunction represented as Set of Atoms
	 */

	public Set<Set<Atoms>> getTerm() {
		return termForm;
	}

	@Override
	public String toString() {
		StringBuffer presentation = new StringBuffer();
		for (Set<Atoms> conjs : termForm) {
			if (presentation.length() == 0) {
				presentation.append("     (");
			} else
				presentation.append(" or (");

			boolean first = true;

			for (Atoms a : conjs) {
				if (first) {
					first = false;
					presentation.append("   ");
				} else
					presentation.append("   and   ");

				presentation.append(a.toString());
				presentation.append("\n");
			}

			presentation.append(")");
		}

		return presentation.toString();
	}
}
