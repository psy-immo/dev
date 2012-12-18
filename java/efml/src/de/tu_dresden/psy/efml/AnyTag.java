/**
 * AnyTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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

package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

import javax.naming.OperationNotSupportedException;


/**
 * interface providing opening and closing operation for tags
 * @author immanuel
 *
 */
public interface AnyTag {
	/**
	 * write the opening sequence of the tag
	 * @param writer   Output writer
	 * @throws IOException
	 */
	public void open(Writer writer) throws IOException;
	
	/**
	 * write the closing sequence of the tag
	 * @param writer   Output writer
	 * @throws IOException
	 */
	public void close(Writer writer) throws IOException;
	
	/**
	 * enclose a tag within this tag, if supported
	 * @param innerTag   tag to enclose
	 */
	public void encloseTag(AnyTag innerTag) throws OperationNotSupportedException;
	
	/**
	 * gives an efml representation of the tag
	 */
	
	public String getEfml();
}
