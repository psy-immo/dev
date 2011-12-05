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

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

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
		Reader reader = new InputStreamReader(input, "UTF-8");
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");
		
		Writer writer = new OutputStreamWriter(output, "UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		DefaultHandler handler = new DefaultHandler() {

			boolean bfname = false;
			boolean blname = false;
			boolean bnname = false;
			boolean bsalary = false;

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {

				System.out.println("Start Element :" + qName);
				System.out.println("Attrubibutes :" + attributes.getLength());

				if (qName.equalsIgnoreCase("FIRSTNAME")) {
					bfname = true;
				}

				if (qName.equalsIgnoreCase("LASTNAME")) {
					blname = true;
				}

				if (qName.equalsIgnoreCase("NICKNAME")) {
					bnname = true;
				}

				if (qName.equalsIgnoreCase("SALARY")) {
					bsalary = true;
				}

			}

			public void endElement(String uri, String localName, String qName)
					throws SAXException {

				System.out.println("End Element :" + qName);

			}

			public void characters(char ch[], int start, int length)
					throws SAXException {

				System.out.println(new String(ch, start, length));

				if (bfname) {
					System.out.println("First Name : "
							+ new String(ch, start, length));
					bfname = false;
				}

				if (blname) {
					System.out.println("Last Name : "
							+ new String(ch, start, length));
					blname = false;
				}

				if (bnname) {
					System.out.println("Nick Name : "
							+ new String(ch, start, length));
					bnname = false;
				}

				if (bsalary) {
					System.out.println("Salary : "
							+ new String(ch, start, length));
					bsalary = false;
				}

			}

		};

		HtmlTag html = new HtmlTag();
		
		html.Open(writer);
		
		parser.parse(source, handler);
		
		html.Close(writer);
		
		writer.close();
		
	};

	public static void main(String[] args) {
		try {
			File file = new File("/tmp/input.xml");

			InputStream inputStream = new FileInputStream(file);

			File out = new File("/tmp/output.xml");
			OutputStream outputStream = new FileOutputStream(out);

			transformStream(inputStream, outputStream);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
