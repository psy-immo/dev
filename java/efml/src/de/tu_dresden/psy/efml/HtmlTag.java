package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

public class HtmlTag implements AnyHtmlTag {

	@Override
	public void Open(Writer writer) throws IOException {
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
	}

	@Override
	public void Close(Writer writer) throws IOException {
		writer.write("</html>");
	}

}
