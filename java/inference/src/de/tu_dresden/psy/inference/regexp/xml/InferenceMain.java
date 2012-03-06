/**
 * InferenceMain.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

/**
 * implements main command line tool for inference argumentation checking
 * 
 * @author immanuel
 * 
 */
public class InferenceMain {
	public static void main(String[] args) {
		
		InferenceMachine machine = new InferenceMachine(); 
		
		/**
		 * check command line arguments
		 */
		
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			
			if (argument.equalsIgnoreCase("--xml")) {
				if (i+1==args.length) {
					System.err.println("--xml needs file name as next argument");
				} else
				{
					++i;
					System.out.print(machine.addXmlFile(args[i]));
				}
			} else if (argument.equalsIgnoreCase("--xml-url")) {
				if (i+1==args.length) {
					System.err.println("--xml-url needs file name as next argument");
				} else
				{
					++i;
					System.out.print(machine.addXmlUrl(args[i]));
				}
			} else if (argument.equalsIgnoreCase("--expert")) {
				System.out.println(machine.closeExpertAssertions());
			} else if (argument.equalsIgnoreCase("--student")) {
				System.out.println(machine.closeStudentAssertions());
			} else if (argument.equalsIgnoreCase("--correct")) {
				System.out.println("Correct conclusions:");
				System.out.println(machine.getCorrectStudentConclusions());
			} else if (argument.equalsIgnoreCase("--incorrect")) {
				System.out.println("Incorrect conclusions:");
				System.out.println(machine.getIncorrectStudentConclusions());
			} else if (argument.equalsIgnoreCase("--inferable")) {
				System.out.println("Inferable conclusions:");
				System.out.println(machine.getInferableStudentConclusions());
			} else if (argument.equalsIgnoreCase("--non-inferable")) {
				System.out.println("Non-inferable conclusions:");
				System.out.println(machine.getNonInferableStudentConclusions());
			} else if (argument.equalsIgnoreCase("--justification")) {
				machine.updateExpertJustification();
				machine.updateStudentJustification();
			} else if (argument.equalsIgnoreCase("--expert-report")) {
				System.out.println("Expert-Report:");
				System.out.println(machine.getExpertReport());
			} else if (argument.equalsIgnoreCase("--student-report")) {
				System.out.println("Student-Report:");
				System.out.println(machine.getStudentReport());
			} else if (argument.equalsIgnoreCase("--report")) {
				System.out.println("Answer-Report:");
				System.out.println(machine.getReport());
			}
		}
	}
}
