package de.tu_dresden.psy.util;

import java.applet.Applet;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JFileChooser;

public class Loglet extends Applet {

	private static final class GetFileContents implements
	PrivilegedAction<String> {

		public GetFileContents() {
		}

		@Override
		public String run() {
			String result = "";

			try {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					String line = null;
					StringBuilder stringBuilder = new StringBuilder();
					String ls = System.getProperty("line.separator");

					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
						stringBuilder.append(ls);
					}

					result = stringBuilder.toString();

					reader.close();
				}

			} catch (Exception e) {
				result = "!!" + e.getMessage() + "\n";
			}

			return result;
		}

	}

	private static final class GetWebPageData implements
	PrivilegedAction<String> {
		private String id, varname, value, url;

		public GetWebPageData(String id, String varname, String value,
				String url) {
			this.id = id;
			this.varname = varname;
			this.value = value;
			this.url = url;
		}

		@Override
		public String run() {

			String result = "";
			String data = "";
			try {
				// Construct data
				data = URLEncoder.encode("id", "UTF-8") + "="
						+ URLEncoder.encode(this.id, "UTF-8");
				data += "&" + URLEncoder.encode("varname", "UTF-8") + "="
						+ URLEncoder.encode(this.varname, "UTF-8");
				data += "&" + URLEncoder.encode("value", "UTF-8") + "="
						+ URLEncoder.encode(this.value, "UTF-8");

				// Send data
				URL url_obj = new URL(this.url);
				URLConnection conn = url_obj.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));

				String line;

				while ((line = rd.readLine()) != null) {
					if (result.isEmpty() == false) {
						result += "\n";
					}
					result += line;
				}
				wr.close();
				rd.close();
			} catch (Exception e) {
				result = "!!" + e.getMessage() + "\n\n" + data + "\n" + this.url;
			}

			return result;
		}
	}

	private static final class CopyToClipboard implements
	PrivilegedAction<String>, ClipboardOwner {
		private String data;

		public CopyToClipboard(String data) {
			this.data = data;
		}

		@Override
		public String run() {
			String result = "";

			try {
				StringSelection stringSelection = new StringSelection(this.data);
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(stringSelection, this);
			} catch (Exception e) {
				result = "!!" + e.getMessage() + "\n";
			}

			return result;
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}
	}

	private static final class PasteFromClipboard implements
	PrivilegedAction<String>, ClipboardOwner {

		public PasteFromClipboard() {
		}

		@Override
		public String run() {
			String result = "";

			try {

				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				// odd: the Object param of getContents is not currently used
				Transferable contents = clipboard.getContents(null);
				boolean hasTransferableText = (contents != null)
						&& contents
						.isDataFlavorSupported(DataFlavor.stringFlavor);
				if (hasTransferableText) {
					result = (String) contents
							.getTransferData(DataFlavor.stringFlavor);
				}

			} catch (Exception e) {
				result = "!!" + e.getMessage() + "\n";
			}

			return result;
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}
	}

	private static final class SetLocalFileContents implements
	PrivilegedAction<String> {
		private String data;

		public SetLocalFileContents(String data) {
			this.data = data;
		}

		@Override
		public String run() {
			String result = "";

			try {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					PrintStream out = null;

					try {
						out = new PrintStream(new FileOutputStream(file));
						out.print(this.data);
					} finally {
						if (out != null) {
							out.close();
						}
					}
				}

			} catch (Exception e) {
				result = "!!" + e.getMessage() + "\n";
			}

			return result;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1967856937952632546L;

	/**
	 * 
	 * @param data
	 *            new clipboard data
	 * @return error messages
	 */

	public String setClipboardContents(String data) {

		/**
		 * we need this to escape from the JavaScript security context denying
		 * file permissions!
		 */
		return AccessController.doPrivileged(new CopyToClipboard(data));
	}

	/**
	 * opens a file open dialog, opens the file and returns its contents as
	 * string
	 * 
	 * @return file contents, error message or empty string if cancelled
	 */

	public String getLocalFileContents() {

		/**
		 * we need this to escape from the JavaScript security context denying
		 * file permissions!
		 */
		return AccessController.doPrivileged(new GetFileContents());
	}

	/**
	 * 
	 * opens a file open dialog, opens the file and saves the data
	 * 
	 * @param data
	 *            new file contents
	 * @return error messages
	 */

	public String setLocalFileContents(String data) {

		/**
		 * we need this to escape from the JavaScript security context denying
		 * file permissions!
		 */
		return AccessController.doPrivileged(new SetLocalFileContents(data));
	}


	/**
	 * @return clipboard contents or error messages
	 */

	public String getClipboardContents() {

		/**
		 * we need this to escape from the JavaScript security context denying
		 * file permissions!
		 */
		return AccessController.doPrivileged(new PasteFromClipboard());
	}

	/**
	 * 
	 * @param id
	 *            identification token
	 * @param varname
	 *            variable name
	 * @param value
	 *            variable value
	 * @param url
	 *            post url
	 * @return received data
	 */

	public String queryLogger(String id, String varname, String value,
			String url) {

		/**
		 * we need this to escape from the JavaScript security context denying
		 * file permissions!
		 */
		return AccessController.doPrivileged(new GetWebPageData(id, varname,
				value, url));
	}
}
