/**
 * InferenceMachine.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp.xml;

import java.applet.Applet;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;

/**
 * 
 * implements an java applet interface for the provided inference facilities
 * 
 * @author immanuel
 * 
 */

public class InferenceMachine extends Applet {
	
	/**
	 * serialization
	 */
	private static final long serialVersionUID = 8101906904403439152L;
	
	/**
	 * machine state variables
	 */
	private Set<AssertionInterface> implicit, expert, student;
	private Map<String, InferenceMap> inferenceMaps;
	private Set<ConstrainedAssertionFilter> trivial,invalid,justified;
	
	/**
	 * class for keeping track of inferred assertions
	 * @author immanuel
	 *
	 */
	
	public static class InferrableAssertions {
		
		public static enum State {
			/**
			 * there are no more inferrable assertions using the given rules,
			 * and none of them are invalid
			 */
			closed, 
			/**
			 * some invalid assertions could be inferred
			 */
			invalid, 
			/**
			 * inference did not stop within some given limit
			 */
			excess};
			
		private Set<AssertionInterface> givenAssertions;
		private Set<AssertionInterface> validAssertions;
		private Set<InferenceMap> usedRules;
		private Set<ConstrainedAssertionFilter> invalid,trivial;
		private State state;
	};
	
	/**
	 * keep inference states
	 */
	
	InferrableAssertions expertValid, studentValid;
	
	/**
	 * reset the state of the machine
	 */
	
	public void resetState() {
		implicit = new HashSet<AssertionInterface>();
		expert = new HashSet<AssertionInterface>();
		student = new HashSet<AssertionInterface>();
		inferenceMaps = new HashMap<String, InferenceMap>();
		trivial = new HashSet<ConstrainedAssertionFilter>();
		invalid = new HashSet<ConstrainedAssertionFilter>();
		justified = new HashSet<ConstrainedAssertionFilter>();
		expertValid = null;
		studentValid = null;
	}
	
	public InferenceMachine() {
		resetState();
	}
	
	/**
	 * add data from inference xml file to the current machine state	
	 * @param root
	 */
	private void addXml(XmlRootTag root) {
		implicit.addAll(root.getImplicitAssertions());
		expert.addAll(root.getExpertAssertions());
		student.addAll(root.getGivenAssertions());
		
		Map<String, InferenceMap> updated_rules = root.getInferenceMapsByName();
		
		for (String key : updated_rules.keySet()) {
			inferenceMaps.put(key, updated_rules.get(key));
		}
		
		trivial.addAll(root.getTrivialityFilters());
		invalid.addAll(root.getInvalidityFilters());
		justified.addAll(root.getJustifiedFilters());
	}

	/**
	 * add inference xml data from a local file
	 * @param location
	 * @return Error Message
	 */
	public String addXmlFile(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			handler.readStream(new FileInputStream(location));
			
			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}

	/**
	 * add inference xml data from an url
	 * @param location
	 * @return Error message
	 */
	public String addXmlUrl(String location) {

		try {
			XmlHandler handler = new XmlHandler();
			URL url = new URL(location);
			handler.readStream(url.openStream());
			
			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding " + location + "\n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}
	
	/**
	 * add inference xml data given as string without surrounding &lt;root>-tag
	 * @param rootContents
	 * @return Error message
	 */
	
	public String addXmlString(String rootContents) {

		try {
			XmlHandler handler = new XmlHandler();
			
			handler.readString(rootContents);
			
			addXml(handler.getRoot());
		} catch (Exception e) {
			String error = "Error adding xml string \n\n";

			error += " " + e.getMessage() + "\n";
			error += "  " + e.toString() + "\n";

			for (int i = 0; i < e.getStackTrace().length; i++) {
				StackTraceElement t = e.getStackTrace()[i];
				error += "   (" + i + ") " + t.getClassName() + "."
						+ t.getMethodName() + "  line " + t.getLineNumber()
						+ " in " + t.getFileName() + "\n";
			}

			return error;
		}

		return "";
	}
}
