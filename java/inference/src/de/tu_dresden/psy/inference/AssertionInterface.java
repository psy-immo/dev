/**
 * AssertionInterface.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference;

/**
 * 
 * provides means to check assertions for their distinct parts
 * 
 * @author albrecht
 * 
 */

public interface AssertionInterface {

	/**
	 * @return the subject of the sentence
	 */
	public Object getSubject();

	/**
	 * 
	 * @return the object of the sentence, or <b>null</b>
	 */
	public Object getObject();

	/**
	 * @return the predicate of the sentence
	 */
	public Object getPredicate();
	
	/**
	 * @param assertion
	 * @return true, if <b>this</b> equals assertion
	 */
	public boolean isEqualTo(AssertionInterface assertion);
	
	/**
	 * @param assertion
	 * @return true, if <b>this</b> has been inferred by using assertion as premise
	 */
	public boolean isPremise(AssertionInterface assertion);
	
	/**
	 * 
	 * @return true, if the Assertion is marked old
	 */
	public boolean isOld();
	
	/**
	 * mark an Assertion as old
	 */
	public void markAsOld();
}
