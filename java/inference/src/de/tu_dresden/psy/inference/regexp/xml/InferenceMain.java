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

import de.tu_dresden.psy.inference.machine.InferenceMachine;

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
				if (i + 1 == args.length) {
					System.err
							.println("--xml needs file name as next argument");
				} else {
					++i;
					System.out.print(machine.addXmlFile(args[i]));
				}
			}
			if (argument.equalsIgnoreCase("--xml-url")) {
				if (i + 1 == args.length) {
					System.err
							.println("--xml-url needs file name as next argument");
				} else {
					++i;
					System.out.print(machine.addXmlUrl(args[i]));
				}
			}
			if (argument.equalsIgnoreCase("--expert")
					|| argument.equalsIgnoreCase("--check")) {
				System.out.print("Calculating expert valid assertions: ");
				System.out.println(machine.closeExpertAssertions());
			}
			if (argument.equalsIgnoreCase("--student")
					|| argument.equalsIgnoreCase("--check")) {
				System.out.print("Calculating student valid assertions: ");
				System.out.println(machine.closeStudentAssertions());
			}
			if (argument.equalsIgnoreCase("--correct")) {
				System.out.println("Correct conclusions:");
				System.out.println(machine.getCorrectStudentConclusions());
			}
			if (argument.equalsIgnoreCase("--incorrect")) {
				System.out.println("Incorrect conclusions:");
				System.out.println(machine.getIncorrectStudentConclusions());
			}
			if (argument.equalsIgnoreCase("--inferable")) {
				System.out.println("Inferable conclusions:");
				System.out.println(machine.getInferableStudentConclusions());
			}
			if (argument.equalsIgnoreCase("--non-inferable")) {
				System.out.println("Non-inferable conclusions:");
				System.out.println(machine.getNonInferableStudentConclusions());
			}
			if (argument.equalsIgnoreCase("--justification")
					|| argument.equalsIgnoreCase("--check")) {
				machine.updateExpertJustification();
				machine.updateStudentJustification();
			}
			if (argument.equalsIgnoreCase("--expert-report")) {
				System.out.println("Expert-Report:");
				System.out.println(machine.getExpertReport());
			}
			if (argument.equalsIgnoreCase("--student-report")) {
				System.out.println("Student-Report:");
				System.out.println(machine.getStudentReport());
			}
			if (argument.equalsIgnoreCase("--report")) {
				System.out.println("Answer-Report:");
				System.out.println(machine.getReport());
			}
			if (argument.equalsIgnoreCase("--ancestors")
					|| argument.equalsIgnoreCase("--check")) {
				System.out.print("Calculating ancestor sets: ");
				System.out.println(machine.calculateAncestors());
			}
			if (argument.equalsIgnoreCase("--count")) {
				System.out.println("Number of Justifications:\n");
				System.out.println(machine.countJustifications());
			}

		}
	}
}
