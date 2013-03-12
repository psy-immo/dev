/**
 * LayoutPositionVector.java, (c) 2013, Immanuel Albrecht; Dresden University of
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
package de.tu_dresden.psy.fca;

/**
 * 
 * @author immo
 * 
 *         this class handles position vectors for layout purposes
 * 
 */

public class LayoutPositionVector {
	private int rank;
	private int rankX;

	public LayoutPositionVector() {
		this.rank = 0;
		this.rankX = 0;
	}

	/**
	 * 
	 * @param target_rank
	 * @return true, if rank moved up
	 */

	public boolean rankUpTo(int target_rank) {
		if (target_rank > this.rank) {
			this.rank = target_rank;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param target_rank
	 * @return true, if rank moved up
	 */

	public void setRankX(int x) {
		this.rankX = x;
	}

	/**
	 * 
	 * @return the "column" position of the layout element in the corresponding
	 *         rank
	 */

	public int RankX() {
		return this.rankX;
	}

	/**
	 * 
	 * @return the rank of the layout element
	 */

	public int Rank() {
		return this.rank;
	}

	@Override
	public String toString() {
		return "rank=" + this.rank + " X=" + this.rankX;
	}

}
