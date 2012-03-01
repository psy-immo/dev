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

	private boolean isRoot;

	public XmlHandler() {
		root = new XmlRootTag();

		stack = new Stack<XmlTag>();

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (isRoot == true) {
			isRoot = false;

			return;
		}
		

		XmlTag tag = new XmlTag(qName, attributes);

		stack.push(tag);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		XmlTag top = stack.pop();
		if (stack.isEmpty() == false)
			try {
				stack.peek().addChild(top);
			} catch (Exception e) {
				SAXException sax_e = new SAXException(e.getMessage(),e);
				sax_e.setStackTrace(e.getStackTrace());
				throw sax_e;
			}
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

		stack.clear();
		stack.push(root);
		isRoot = true;

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
		String allData = "<root>" + xmlData + "</root>";
		readStream(new ByteArrayInputStream(allData.getBytes("UTF-8")));
	}

	/**
	 * testing routine
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		XmlHandler handler = new XmlHandler();
		try {

			handler.readString("<rule></rule><parse><subject>a+</subject><subject>a+</subject><predicate>a+</predicate><object>a+</object></parse><assert>aaa aaaa aa</assert>");

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
		System.out.println(handler.root.getGivenAssertions());
	}

	public XmlRootTag getRoot() {
		return root;
	}

}
