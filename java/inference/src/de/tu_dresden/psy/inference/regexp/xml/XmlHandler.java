package de.tu_dresden.psy.inference.regexp.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {

	private Stack<XmlTag> stack;

	private XmlRootTag root;

	public XmlHandler() {
		root = new XmlRootTag();

		stack = new Stack<XmlTag>();
		stack.push(root);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		XmlTag tag = new XmlTag(qName, attributes);

		stack.push(tag);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		XmlTag top = stack.pop();
		stack.peek().addChild(top);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		stack.peek().addContent(new String(ch, start, length));
	}

	/**
	 * parse xml rules from input stream
	 * 
	 * @param input
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */

	public void readStream(InputStream input)
			throws ParserConfigurationException, SAXException, IOException {
		/**
		 * I/O handling in UTF-8
		 */

		Reader reader = new InputStreamReader(input, "UTF-8");
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		/**
		 * parse input
		 */

		parser.parse(source, this);
	}

	/**
	 * parse xml rules given as string
	 * 
	 * @param xmlData
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws UnsupportedEncodingException
	 */
	public void readString(String xmlData) throws UnsupportedEncodingException,
			ParserConfigurationException, SAXException, IOException {
		readStream(new ByteArrayInputStream(xmlData.getBytes("UTF-8")));
	}

	/**
	 * testing routine
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		XmlHandler handler = new XmlHandler();
		try {

			handler.readString("<rule></rule>");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		System.out.println(handler.root);
	}

}
