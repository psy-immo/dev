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
		
		
		
		try {

			InputStream inputStream = null;
			OutputStream outputStream = System.out;

			switch (args.length) {
			case 2:
				outputStream = new FileOutputStream(new File(args[1]));
			case 1:
				if (args[0].equals("-"))
					inputStream = System.in;
				else
					inputStream = new FileInputStream(new File(args[0]));

				transformStream(inputStream, outputStream);

				break;

			default:
				System.out
						.println("Usage: java -jar emfl2html.jar INPUT [OUTPUT]\n"
								+ "   where INPUT is either a path to the input file or '-' for\n"
								+ "   standard input.\n\n"
								+ "   Output will be written to the optional parameter OUTPUT or\n"
								+ "   in case of absence to the standard output.");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
