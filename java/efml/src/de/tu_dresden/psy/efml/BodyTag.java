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
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

/**
 * provides the body tag in the html file
 * 
 * @author immanuel
 * 
 */

public class BodyTag implements AnyTag {

	private HeadTag headTag;

	private ArrayList<AnyTag> innerTags;
	
	/**
	 * keep track of current autoSpan ids
	 */
	
	private int autoSpanId;

	/**
	 * document id
	 */

	private String idDoc;

	/**
	 * resource language id
	 */

	private String idLanguage;

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

	/**
	 * text that is shown when the current subject id is not working
	 */

	private String subjectNotWorking;

	/**
	 * include efml applet
	 * */

	private boolean include_efml_applet;

	public BodyTag(HeadTag head) {
		this.innerTags = new ArrayList<AnyTag>();

		this.idDoc = UUID.randomUUID().toString();
		this.idLanguage = null;
		this.idStudy = UUID.randomUUID().toString();
		this.logletUrl = null;
		this.scriptUrl = "";
		this.subjectChange = null;
		this.subjectInfo = null;
		this.subjectPrompt = null;
		this.subjectNotWorking = null;

		this.include_efml_applet = false;

		this.headTag = head;
		
		this.autoSpanId = 0;
	}
	
	/**
	 * 
	 * @return the next autoSpanId
	 */
	
	public int nextAutoSpanId() {
		return this.autoSpanId ++;
	}

	/**
	 * write the code needed to include the needed java applets
	 * 
	 * @param writer
	 * @param baseUrl
	 *            base Url for scripts
	 * @throws IOException
	 */
	public void writeAllApplets(Writer writer, String baseUrl)
			throws IOException {
		/**
		 * add loglet applet
		 */

		writer.write("  <applet id=\"loglet\" name=\"loglet\" archive=\""
				+ baseUrl + "loglet.jar\""
				+ " code=\"de.tu_dresden.psy.util.Loglet\" mayscript=\"\" "
				+ "style=\"width: 1px; height: 1px\"></applet>\n");

		if (this.include_efml_applet) {
			writer.write("  <applet id=\"efmlApplet\" name=\"efmlApplet\" archive=\""
					+ baseUrl
					+ "efmlApplet.jar\" "
					+ "code=\"de.tu_dresden.psy.efml.editor.EditorApplet\" mayscript=\"\""
					+ " style=\"width: 1px; height: 1px\"></applet>\n");
		}

	};

	/**
	 * 
	 * @param id
	 *            new study id
	 */

	public void setStudy(String id) {
		this.idStudy = id;
	}

	/**
	 * 
	 * @param prompt
	 *            subject id prompt
	 */

	public void setPrompt(String prompt) {
		this.subjectPrompt = prompt;
	}

	/**
	 * 
	 * @param msg
	 *            subject not working message
	 */

	public void setNotWorkingMessage(String msg) {
		this.subjectNotWorking = msg;
	}

	/**
	 * 
	 * @param info
	 *            subject id info
	 */

	public void setInfo(String info) {
		this.subjectInfo = info;
	}

	/**
	 * 
	 * @param text
	 *            subject id change button text
	 */

	public void setChange(String text) {
		this.subjectChange = text;
	}

	/**
	 * 
	 * @param id
	 *            new document id
	 */

	public void setDocument(String id) {
		this.idDoc = id;
	}

	/**
	 * 
	 * @param id
	 *            new language id
	 */

	public void setLanguage(String id) {
		this.idLanguage = id.trim().toUpperCase();
	}

	/**
	 * 
	 * @param url
	 *            new php base url
	 */

	public void setPhp(String url) {
		this.logletUrl = url;
	}

	/**
	 * 
	 * @param url
	 *            new js base url
	 */

	public void setJs(String url) {
		this.scriptUrl = url;

		/**
		 * give the url as default base if there is no css file url specified
		 * explicitly
		 */

		this.headTag.setCssUrlDefaultBase(url);
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<body id=\"body\">\n");

		this.writeAllApplets(writer, this.scriptUrl);

		/**
		 * write the identification strings & base url & ...
		 * 
		 */

		writer.write("	<script type=\"text/javascript\">\n" + "  docId = \""
				+ StringEscape.escapeToJavaScript(this.idDoc) + "\";\n"
				+ "  studyId = \""
				+ StringEscape.escapeToJavaScript(this.idStudy) + "\";\n");

		if (this.logletUrl != null) {
			writer.write("  logletBaseURL = \""
					+ StringEscape.escapeToJavaScript(this.logletUrl) + "\";\n");
		}

		if (this.subjectPrompt != null) {
			writer.write("  subjectIdPrompt = \""
					+ StringEscape.escapeToJavaScript(this.subjectPrompt)
					+ "\";\n");
		}

		if (this.subjectInfo != null) {
			writer.write("  subjectIdInfo = \""
					+ StringEscape.escapeToJavaScript(this.subjectInfo)
					+ "\";\n");
		}

		if (this.subjectNotWorking != null) {
			writer.write("  serverNotWorking = \""
					+ StringEscape.escapeToJavaScript(this.subjectNotWorking)
					+ "\";\n");
		}

		if (this.subjectChange != null) {
			writer.write("  subjectIdChange = \""
					+ StringEscape.escapeToJavaScript(this.subjectChange)
					+ "\";\n");
		}

		if (this.idLanguage != null) {
			writer.write("  language = \""
					+ StringEscape.escapeToJavaScript(this.idLanguage)
					+ "\";\n");
		}

		writer.write("  </script>\n");

		HtmlTag.writeAllIncludes(writer, this.scriptUrl);

		/**
		 * all content will be displayed in the "mainframe"
		 */

		writer.write("<div id=\"mainframe\" >");

		/**
		 * print the current subject id token and change button
		 */

		writer.write("<script type=\"text/javascript\">printChangeSubjectButton();</script>");

		this.writeInnerTags(writer);

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
		for (AnyTag innerTag : this.innerTags) {
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
				+ StringEscape.escapeToJavaScript(this.idDoc) + "\");"
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
		if (innerTag instanceof TitleTag) {
			this.headTag.encloseTag(innerTag);
		} else {
			this.innerTags.add(innerTag);
		}
	}

	/**
	 * require to include efml applet
	 */

	public void requireEfml() {
		this.include_efml_applet = true;
	}

	@Override
	public String getEfml() {
		/**
		 * there is no efml representation of the body tag!
		 */
		return null;
	}

	/**
	 * set the css url in the document head
	 * 
	 * @param token
	 *            url to the css style sheet that will be used by the generated
	 *            html files
	 */

	public void setCss(String token) {

		this.headTag.setCssUrl(token);

	}

}
