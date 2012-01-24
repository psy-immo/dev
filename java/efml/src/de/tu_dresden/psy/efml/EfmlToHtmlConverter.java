/**
 * EfmlToHtmlConverter.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.efml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * provides an interface that generates HTML files for the browser from EFML
 * files
 * 
 * @author immanuel
 * 
 */

public class EfmlToHtmlConverter {

	/**
	 * Transform an EFML-File into an HTML-File
	 * 
	 * @param input
	 *            Stream that reads the EFML file (UTF-8)
	 * @param output
	 *            Stream that takes the HTML file (UTF-8)
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */

	public static void transformStream(InputStream input, OutputStream output)
			throws ParserConfigurationException, SAXException, IOException {

		/**
		 * I/O handling in UTF-8
		 */

		Reader reader = new InputStreamReader(input, "UTF-8");
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");

		Writer writer = new OutputStreamWriter(output, "UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		EfmlToHtmlHandler handler = new EfmlToHtmlHandler();

		/**
		 * parse input
		 */

		parser.parse(source, handler);

		/**
		 * write output
		 */

		HtmlTag html = new HtmlTag();

		/**
		 * add head
		 */

		HeadTag head = handler.getHead();

		try {
			html.encloseTag(head);
		} catch (OperationNotSupportedException e) {
			/** unreachable */
		}

		/**
		 * add body
		 */

		BodyTag body = handler.getBody();

		try {
			html.encloseTag(body);
		} catch (OperationNotSupportedException e) {
			/** unreachable */
		}

		/**
		 * write main html tag and save file
		 */

		html.open(writer);

		html.close(writer);

		writer.close();

	};

	/**
	 * command line program
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		boolean give_help = false;

		boolean relax = false;

		try {

			InputStream inputStream = null;
			OutputStream outputStream = System.out;

			switch (args.length) {
			case 3:
				if (args[0].startsWith("--") == false) {
					give_help = true;
					break;
				} else {
					outputStream = new FileOutputStream(new File(args[2]));
				}

			case 2:
				if (args[0].startsWith("--") == false)
					outputStream = new FileOutputStream(new File(args[1]));
				else {
					if (args[1].equals("-"))
						inputStream = System.in;
					else
						inputStream = new FileInputStream(new File(args[1]));
				}

			case 1:
				if (args[0].startsWith("--") == false) {
					if (args[0].equals("-"))
						inputStream = System.in;
					else
						inputStream = new FileInputStream(new File(args[0]));
				} else {
					if (args[0].equalsIgnoreCase("--relax") == false) {
						give_help = true;
						break;
					}
					relax = true;
				}

				if (relax == false) {
					transformStream(inputStream, outputStream);
				} else {
					relaxedTransformStream(inputStream, outputStream);
				}

				break;

			default:
				give_help = true;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (give_help == true) {
			System.out
					.println("Usage: java -jar emfl2html.jar [--relax] INPUT [OUTPUT]\n"
							+ "   where INPUT is either a path to the input file or '-' for\n"
							+ "   standard input.\n\n"
							+ "   Output will be written to the optional parameter OUTPUT or\n"
							+ "   in case of absence to the standard output.\n"
							+ "   The optional parameter --relax will force efml2html to ignore\n"
							+ "   encountered errors as far as possible and ignore all data not\n"
							+ "   enclosed within <efml>..</efml> tags (which may not nest)\n\n");
		}

	}

	/**
	 * Transform an EFML-fused (HTML) file into an HTML-File (h4><0round for old
	 * ef editor output files)
	 * 
	 * @param input
	 *            Stream that reads the EFML file (UTF-8)
	 * @param output
	 *            Stream that takes the HTML file (UTF-8)
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */

	public static void relaxedTransformStream(InputStream input,
			OutputStream output) throws ParserConfigurationException,
			SAXException, IOException {

		/**
		 * I/O handling in UTF-8
		 */

		Reader reader = new InputStreamReader(input, "UTF-8");
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");

		StringBuffer all_input = new StringBuffer();

		char[] c_buf = new char[1024];
		int amount = 0;

		while (amount >= 0) {
			amount = source.getCharacterStream().read(c_buf);
			if (amount >= 0) {
				all_input.append(c_buf, 0, amount);
			}
		}

		String contents = all_input.toString();
		String lowercase_contents = contents.toLowerCase();

		/**
		 * do some nasty fixing for ef editor files
		 */

		if (contents.indexOf("<includehover") < 0) {
			/**
			 * add <includehover/> just before </body>
			 */
			int idx = lowercase_contents.indexOf("</body");
			if (idx >= 0) {
				contents = contents.substring(0, idx)
						+ "<efml><includehover/></efml>"
						+ contents.substring(idx);
				lowercase_contents = contents.toLowerCase();
			}
		}
		
		if (contents.indexOf("<includepreamble")<0) {
			/**
			 * add <includepreamble/> just after <html>..
			 */
			int idx = lowercase_contents.indexOf("<html>");
			if (idx >= 0) {
				contents = contents.substring(0, idx+6)
						+ "<efml><includepreamble/></efml>"
						+ contents.substring(idx+6);
				lowercase_contents = contents.toLowerCase();
			}
		}
		
		if (contents.indexOf("<includeaddendum") < 0) {
			/**
			 * add <includehover/> just before </body>
			 */
			int idx = lowercase_contents.indexOf("</body");
			if (idx >= 0) {
				contents = contents.substring(0, idx)
						+ "<efml><includeaddendum/></efml>"
						+ contents.substring(idx);
				lowercase_contents = contents.toLowerCase();
			}
		}

		Writer writer = new OutputStreamWriter(output, "UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		/**
		 * chop up the file
		 */

		int current_index = 0;
		int efml_index = contents.indexOf("<efml>", current_index);

		while (efml_index >= 0) {
			/**
			 * copy the current non-efml part
			 */

			writer.write(contents.substring(current_index, efml_index));

			int end_index = contents.indexOf("</efml>", efml_index)
					+ "</efml>".length();

			if (end_index < "</efml>".length())
				end_index = contents.length() + "</efml>".length();

			String efml_part = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<efml>"
					+ contents.substring(efml_index + "<efml>".length(),
							end_index - "</efml>".length()) + "</efml>";

			/**
			 * process the EFML code
			 */

			EfmlToHtmlHandler handler = new EfmlToHtmlHandler();

			parser.parse(new ByteArrayInputStream(efml_part.getBytes("UTF-8")),
					handler);

			handler.getBody().writeInnerTags(writer);

			/**
			 * continue after EFML part
			 */

			current_index = end_index;
			efml_index = contents.indexOf("<efml>", current_index);
		}

		/**
		 * copy the last part of the file
		 */

		writer.write(contents.substring(current_index));

		/**
		 * close file
		 */

		writer.close();

	}

}
