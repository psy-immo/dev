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
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
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

	private DefaultExecuteResultHandler result;

	private PipedOutputStream to_conexp;
	private InputStream from_conexp;
	private InputStream error_conexp;
	private InputStream stream_to_conexp;
	private PipedOutputStream stream_error_conexp;
	private PipedOutputStream stream_from_conexp;

	private String output_buffer;
	private byte[] b;

	/**
	 * send a command and newline to conexp-clj
	 * 
	 * @param input
	 *            command
	 */

	public void sendCommands(String input) {
		try {
			this.to_conexp.write((input + "\n").getBytes());
			this.to_conexp.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return a line of conexp output, or null if none is available
	 */

	public String readOutput() {
		while (this.output_buffer.indexOf("\n") < 0) {
			try {
				if (this.from_conexp.available() == 0) {
					return null;
				}

				this.from_conexp.read(this.b);

				this.output_buffer += new String(this.b);

			} catch (IOException e) {
				return null;
			}
		}
		String line = this.output_buffer.substring(0,
				this.output_buffer.indexOf("\n"));
		this.output_buffer = this.output_buffer.substring(this.output_buffer
				.indexOf("\n") + 1);

		return line;
	}

	/**
	 * 
	 * @return a line of conexp output, or null if none is available
	 */

	public String waitOutput() {
		while (this.output_buffer.indexOf("\n") < 0) {
			try {

				while (this.from_conexp.available() == 0) {
					System.out.println(this.output_buffer);
					Thread.sleep(10);

				}

				this.from_conexp.read(this.b);

				this.output_buffer += new String(this.b);

			} catch (IOException | InterruptedException e) {
				return null;
			}
		}
		String line = this.output_buffer.substring(0,
				this.output_buffer.indexOf("\n"));
		this.output_buffer = this.output_buffer.substring(this.output_buffer
				.indexOf("\n") + 1);

		return line;
	}

	/**
	 * close conexp input and wait for the process to finish
	 */

	public void waitForResults() {
		try {
			this.to_conexp.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		try {
			this.result.waitFor();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	public ConexpCljBridge() {

		this.b = new byte[1];

		/**
		 * build the command line (see conexp-clj/bin/conexp-clj)
		 */

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

		/**
		 * open the pipes
		 */

		this.to_conexp = new PipedOutputStream();
		try {
			this.stream_to_conexp = new PipedInputStream(this.to_conexp, 2048);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		this.stream_error_conexp = new PipedOutputStream();
		this.stream_from_conexp = new PipedOutputStream();

		try {
			this.from_conexp = new PipedInputStream(this.stream_from_conexp,
					2048);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.error_conexp = new PipedInputStream(this.stream_error_conexp,
					2048);
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		/**
		 * setup apache commons exec
		 */

		this.result = new DefaultExecuteResultHandler();

		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		executor.setStreamHandler(new PumpStreamHandler(
				this.stream_from_conexp, this.stream_error_conexp,
				this.stream_to_conexp));

		/**
		 * run in non-blocking mode
		 */

		try {
			executor.execute(conexp_cmd, this.result);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.output_buffer = "";

	}
}
