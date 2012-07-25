/**
 * BodyTag.java, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

/**
 * provides the body tag in the html file
 * 
 * @author immanuel
 * 
 */

public class BodyTag implements AnyTag {

	private ArrayList<AnyTag> innerTags;

	/**
	 * document id
	 */

	private String idDoc;

	/**
	 * study id
	 */

	private String idStudy;

	/**
	 * loglet url base
	 */

	private String logletUrl;

	/**
	 * script url base
	 */

	private String scriptUrl;

	/**
	 * prompt for subject id token
	 */

	private String subjectPrompt;

	/**
	 * info text for subject id token
	 */

	private String subjectInfo;

	/**
	 * change button text for subject id token
	 */

	private String subjectChange;

	public BodyTag() {
		innerTags = new ArrayList<AnyTag>();

		idDoc = UUID.randomUUID().toString();
		idStudy = UUID.randomUUID().toString();
		logletUrl = null;
		scriptUrl = "";
		subjectChange = null;
		subjectInfo = null;
		subjectPrompt = null;
	}

	/**
	 * 
	 * @param id
	 *            new study id
	 */

	public void setStudy(String id) {
		idStudy = id;
	}

	/**
	 * 
	 * @param prompt
	 *            subject id prompt
	 */

	public void setPrompt(String prompt) {
		subjectPrompt = prompt;
	}

	/**
	 * 
	 * @param info
	 *            subject id info
	 */

	public void setInfo(String info) {
		subjectInfo = info;
	}

	/**
	 * 
	 * @param text
	 *            subject id change button text
	 */

	public void setChange(String text) {
		subjectChange = text;
	}

	/**
	 * 
	 * @param id
	 *            new document id
	 */

	public void setDocument(String id) {
		idDoc = id;
	}

	/**
	 * 
	 * @param url
	 *            new php base url
	 */

	public void setPhp(String url) {
		logletUrl = url;
	}

	/**
	 * 
	 * @param url
	 *            new js base url
	 */

	public void setJs(String url) {
		scriptUrl = url;
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<body id=\"body\">\n");

		/**
		 * write the identification strings & base url & ...
		 * 
		 */

		writer.write("	<script type=\"text/javascript\">\n" + "  docId = \""
				+ StringEscape.escapeToJavaScript(idDoc) + "\";\n"
				+ "  studyId = \"" + StringEscape.escapeToJavaScript(idStudy)
				+ "\";\n");

		if (logletUrl != null) {
			writer.write("  logletBaseURL = \""
					+ StringEscape.escapeToJavaScript(logletUrl) + "\";\n");
		}

		if (subjectPrompt != null) {
			writer.write("  subjectIdPrompt = \""
					+ StringEscape.escapeToJavaScript(subjectPrompt) + "\";\n");
		}

		if (subjectInfo != null) {
			writer.write("  subjectIdInfo = \""
					+ StringEscape.escapeToJavaScript(subjectInfo) + "\";\n");
		}

		if (subjectChange != null) {
			writer.write("  subjectIdChange = \""
					+ StringEscape.escapeToJavaScript(subjectChange) + "\";\n");
		}

		writer.write("  </script>\n");

		HtmlTag.writeAllIncludes(writer, scriptUrl);

		/**
		 * all content will be displayed in the "mainframe"
		 */

		writer.write("<div id=\"mainframe\" >");

		/**
		 * print the current subject id token and change button
		 */

		writer.write("<script type=\"text/javascript\">printChangeSubjectButton();</script>");

		writeInnerTags(writer);

	}

	/**
	 * writes the inner tags of the body tag only
	 * 
	 * @param write
	 * @throws IOException
	 */
	public void writeInnerTags(Writer writer) throws IOException {

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
		writer.write("</div>");

		/**
		 * add code for the hovering feature
		 */
		includeHover(writer);

		/**
		 * initialize session storage handler
		 */

		writer.write("	<script type=\"text/javascript\">\n"
				+ "  myStorage.SetupAutoRestore(sessionStorage,\""
				+ StringEscape.escapeToJavaScript(idDoc) + "\");"
				+ "  </script>\n");

		/**
		 * write initializations that depend on the variables created by the
		 * html layout
		 */

		HtmlTag.writeAllStaticInitializations(writer);

		writer.write("</body>");
	}

	/**
	 * add code for hovering feature
	 * 
	 * @param writer
	 * @throws IOException
	 */
	static public void includeHover(Writer writer) throws IOException {
		writer.write("	<script type=\"text/javascript\">\n"
				+ "		myHover.WriteHtml();\n" + "	</script>");

	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		innerTags.add(innerTag);
	}

}
