package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;inference>-tag
 * 
 * @author albrecht
 * 
 */

public class InferenceTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	/**
	 * contains the inference xml data that is embedded with the inference tag
	 */
	private ArrayList<InferenceXmlTag> xmlMachineData;


	public InferenceTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		xmlMachineData = new ArrayList<InferenceXmlTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {

		/**
		 * get inner data string
		 */

		StringWriter xmlData = new StringWriter();

		for (InferenceXmlTag tag : xmlMachineData) {
			tag.open(xmlData);
			tag.close(xmlData);
		}

		/**
		 * write script code
		 */

		writer.write("<script type=\"text/javascript\">");

		writer.write("new InferenceButton(");

		/**
		 * write tags
		 */

		writer.write(attributes.getAcceptTags() + ", ");
		writer.write(attributes.getRejectTags() + ", ");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"pointstag", "points")) + "\", ");
		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"conclusionstag", "conclusions")) + "\"");

		writer.write(").Feed(");

		/**
		 * write content data
		 */

		writer.write(StringEscape.escapeToDecodableInJavaScript(xmlData
				.toString()));

		writer.write(")");
		/**
		 * let java script create the html contents
		 */
		writer.write(".WriteHtml();");

		writer.write("</script>");

	}

	@Override
	public void close(Writer writer) throws IOException {
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			/**
			 * we just ignore PCDATA content (should only consist of whitespaces
			 * anyway)
			 */
		} else if (innerTag.getClass() == InferenceXmlTag.class) {
			xmlMachineData.add((InferenceXmlTag) innerTag);
		} else

			throw new OperationNotSupportedException("<answer> cannot enclose "
					+ innerTag.getClass().toString());

	}

}
