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

	private ArrayList<FeedbackTag> feedbackData;

	public InferenceTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		xmlMachineData = new ArrayList<InferenceXmlTag>();
		feedbackData = new ArrayList<FeedbackTag>();
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
		 * write feedback data
		 */

		for (FeedbackTag feedback : feedbackData) {
			if (feedback.getCorrect() != null) {
				writer.write(".SetCorrect(");
				writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
						.getCorrect().getFeedback())));
				writer.write(")");
			}

			if (feedback.getIncorrect() != null) {
				writer.write(".SetIncorrect(");
				writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
						.getIncorrect().getFeedback())));
				writer.write(")");
			}

			if (feedback.getIncomplete() != null) {
				writer.write(".SetIncomplete(");
				writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
						.getIncomplete().getFeedback())));
				writer.write(")");
			}

			if (feedback.getNeedjustification() != null) {
				writer.write(".SetInjustified(");
				writer.write(StringEscape.escapeToDecodeInJavaScript((feedback
						.getNeedjustification().getFeedback())));
				writer.write(")");
			}

			for (HintTag hint : feedback.getHints()) {
				writer.write(".AddHint(");
				writer.write(StringEscape.escapeToDecodeInJavaScript(hint
						.getLack()));
				writer.write(",");
				writer.write(StringEscape.escapeToDecodableInJavaScript(hint
						.getHint()));
				writer.write(")");
			}

			for (RequiredTag require : feedback.getRequires()) {
				if (require.requiresCount()) {
					writer.write(".RequireCount(" + require.getCount() + ")");
				}
				if (require.requiresPart()) {
					writer.write(".RequirePart("
							+ StringEscape.escapeToDecodeInJavaScript(require
									.getPart()) + ")");
				}
			}
		}

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
		} else if (innerTag.getClass() == FeedbackTag.class) {
			feedbackData.add((FeedbackTag) innerTag);
		} else

			throw new OperationNotSupportedException(
					"<inference> cannot enclose "
							+ innerTag.getClass().toString());

	}

}
