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

		String[] javascript_includes = {
		/**
		 * cssSandpaper
		 */
		"EventHelpers.js", "cssQuery-p.js", "jcoglan.com.sylvester.js",
				"cssSandpaper.js",
				/**
				 * jQuery
				 */
				"jquery-1.8.0.min.js",
				/**
				 * prototypejs
				 */
				"prototype.js",
				/**
				 * efjs scripts
				 */
				"crosscompat.js", "mouse.js", "cssgraphics.js", "loglet.js",
				"storage.js", "logger.js", "tags.js", "logic.js", "hover.js",
				"endecoder.js", "runway.js", "answer.js", "sniffybutton.js",
				"dropdown.js", "checkbox.js", "radiobutton.js",
				"popupbutton.js", "freetext.js", "boxspace.js", "trashcan.js",
				"sentencepattern.js", "airport.js", "inference.js", "efml.js",
				"efmlbuttons.js", "efmlboard.js", "efmltag.js", "efmlquote.js",
				"efmlfactory.js" };

		for (int i = 0; i < javascript_includes.length; i++) {
			writer.write("	<script type=\"text/javascript\" src=\"" + baseUrl
					+ javascript_includes[i] + "\"></script>\n");
		}

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
				+ "  RunwayDisplayBugfix();" + "  </script>\n");

		/**
		 * add initial arrows to boxspace
		 */

		writer.write("	<script type=\"text/javascript\">\n"
				+ "  BoxspaceUpdateAllArrows();" + "  </script>\n");

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
		 * static initializations have been moved to body tags close method
		 */

		writer.write("</html>\n");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		innerTags.add(innerTag);
	}

}
