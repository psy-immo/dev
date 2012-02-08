/**
 * StringRelationJoin.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.regexp;

import java.util.HashSet;
import java.util.Set;

/**
 * implements the relative join of string operations
 * 
 * @author albrecht
 *
 */

public class StringRelationJoin implements StringRelationInterface {

	private Set<StringRelationInterface> relations;
	
	/**
	 * create an empty join-object
	 */
	public StringRelationJoin() {
		this.relations = new HashSet<StringRelationInterface>();
	}
	
	/**
	 * create a joined object from a ¶-delimited SplittedStringRelation
	 * @param delimited  delimited string relation, form:
	 *  [Input ·-delimited k-RegExp]→[Output Function]¶...¶[Input ·-delimited k-RegExp]→[Output Function]
	 */
	public StringRelationJoin(String delimited) {
		this.relations = new HashSet<StringRelationInterface>();
		for (String relation : delimited.split("¶")) {
			this.relations.add(new SplittedStringRelation(relation));
		}
			
	}
	
	/**
	 * add another string relation
	 * @param relation
	 */
	public void join(StringRelationInterface relation) {
		this.relations.add(relation);
	}

	@Override
	public Set<String> allMaps(String s) {
		Set<String> results = new HashSet<String>();
		for (StringRelationInterface relation : relations) {
			results.addAll(relation.allMaps(s));
		}
		return results;
	}

}
