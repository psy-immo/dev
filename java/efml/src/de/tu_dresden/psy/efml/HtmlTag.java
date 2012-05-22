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

	/**
	 * write the code needed to include the js containing general code
	 * 
	 * @param writer
	 * @param baseUrl
	 *            base Url for scripts
	 * @throws IOException
	 */
	static public void writeAllIncludes(Writer writer, String baseUrl)
			throws IOException {
		writer.write("	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "loglet.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "storage.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "logger.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "tags.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "logic.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "hover.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "endecoder.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "runway.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "answer.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "sniffybutton.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "dropdown.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "sentencepattern.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "airport.js\"></script>\n"
				+ "	<script type=\"text/javascript\" src=\"" + baseUrl
				+ "inference.js\"></script>\n");
	}

	/**
	 * write the code needed to initialize the used modules
	 * 
	 * @param writer
	 * @throws IOException
	 */
	static public void writeAllStaticInitializations(Writer writer)
			throws IOException {
		
		/**
		 * run runway bugfix
		 */
		
		writer.write("	<script type=\"text/javascript\">\n"
				+ "  RunwayDisplayBugfix();"
				+ "  </script>\n");
		
		/**
		 * feed inference applets
		 */

		writer.write("	<script type=\"text/javascript\">\n"
				+ "  FeedInferenceApplets();" + "  </script>\n");

	}


	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n"
				+ "<html>");
		/**
		 * include all scripts
		 */

		// moved to body tag writeAllIncludes(writer);

		/**
		 * write inner tags
		 */

		for (Iterator<AnyTag> it = innerTags.iterator(); it.hasNext();) {
			AnyTag innerTag = it.next();
			innerTag.open(writer);
			innerTag.close(writer);
		}
	}

	@Override
	public void close(Writer writer) throws IOException {
		/**
		 * write initializations that depend on the variables created by the
		 * html layout
		 */

		writeAllStaticInitializations(writer);


		writer.write("</html>\n");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		innerTags.add(innerTag);
	}

}
