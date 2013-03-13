/**
 * ConexpCljBridge.java, (c) 2013, Immanuel Albrecht; Dresden University of
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

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.launcher.Launcher;

/**
 * 
 * @author immo
 * 
 *         implements a bridge to conexp-clj, see:
 *         https://github.com/exot/conexp-clj and
 *         http://www.math.tu-dresden.de/~borch/
 * 
 */

public class ConexpCljBridge {

	public ConexpCljBridge() {

		String java_bin = Launcher.getJavaCommand();

		CommandLine conexp_cmd = new CommandLine(java_bin);
		conexp_cmd.addArgument("-server");
		conexp_cmd.addArgument("-cp");
		conexp_cmd
		.addArgument("./conexp-clj/lib/conexp-clj-0.0.7-alpha-SNAPSHOT-standalone.jar");
		conexp_cmd.addArgument("clojure.main");
		conexp_cmd.addArgument("-e");
		conexp_cmd.addArgument("");
		conexp_cmd.addArgument("./conexp-clj/lib/conexp-clj.clj");
		conexp_cmd.addArgument("--gui");

		System.out.println(conexp_cmd);

		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
		executor.setWatchdog(watchdog);
		try {
			int exitValue = executor.execute(conexp_cmd);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
