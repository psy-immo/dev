package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

/**
 * implements the main html tag
 * 
 * @author immanuel
 *
 */

public class HtmlTag implements AnyTag {
	
	private ArrayList<AnyTag> innerTags;
	
	public HtmlTag() {
		innerTags = new ArrayList<AnyTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				+ "<html>");
		/**
		 * include all scripts
		 */

		writer.write("	<script type=\"text/javascript\" src=\"logger.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"tags.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"logic.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"hover.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"endecoder.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"runway.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"answer.js\"></script>");
		
		/**
		 * write inner tags
		 */
		
		for (Iterator<AnyTag> it=innerTags.iterator();it.hasNext();)
		{
			AnyTag innerTag = it.next();
			innerTag.open(writer);
			innerTag.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write("</html>\n");
	}
	
	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
			innerTags.add(innerTag);
	}

}
