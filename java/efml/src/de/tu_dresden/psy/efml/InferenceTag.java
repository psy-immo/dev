package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import de.tu_dresden.psy.inference.compiler.EmbeddedInferenceXmlTag;
import de.tu_dresden.psy.inference.compiler.InferenceCompiler;

/**
 * implements the &lt;inference>-tag
 * 
 * @author albrecht
 * 
 */

public class InferenceTag implements AnyTag {

	private EfmlTagsAttribute attributes;

	private static boolean useInferenceApplet = false;
	private static boolean useInferenceCompiler = true;

	/**
	 * contains the inference xml data that is embedded with the inference tag
	 */
	private ArrayList<EmbeddedInferenceXmlTag> xmlMachineData;

	private ArrayList<FeedbackTag> feedbackData;

	public InferenceTag(EfmlTagsAttribute attributes) {
		this.attributes = attributes;
		this.xmlMachineData = new ArrayList<EmbeddedInferenceXmlTag>();
		this.feedbackData = new ArrayList<FeedbackTag>();

	}

	@Override
	public void open(Writer writer) throws IOException {

		/****
		 * NEW CODE: use generated hypergraphs and javascript instead of feeding
		 * xml code into an applet
		 */
		if (useInferenceCompiler) {
			/**
			 * compiler object that calculates the inference graphs etc.
			 */
			InferenceCompiler compiler = new InferenceCompiler();

			/**
			 * actually compile
			 */

			String compiler_errors = compiler
					.processXmlData(this.xmlMachineData);

			/**
			 * write the compiler errors in the html document :)
			 */

			writer.write(compiler_errors);

			/**
			 * extend the inference machine code with efml document specific
			 * stuff
			 */

			StringBuffer more_machine_options = new StringBuffer();

			String rectify_airport = this.attributes.getValueOrDefault(
					"rectify", null);

			if (rectify_airport != null) {
				more_machine_options.append(".Rectify(\""
						+ StringEscape.escapeToJavaScript(rectify_airport)
						+ "\")");
			}

			String rectify_timer = this.attributes.getValueOrDefault(
					"rectifytimer", null);

			if (rectify_timer != null) {
				more_machine_options.append(".RectifyTimer(\""
						+ StringEscape.escapeToJavaScript(rectify_timer)
						+ "\")");
			}

			String rectify_counter = this.attributes.getValueOrDefault(
					"rectifycounter", null);

			if (rectify_counter != null) {
				more_machine_options.append(".RectifyCounter(\""
						+ StringEscape.escapeToJavaScript(rectify_counter)
						+ "\")");
			}

			String feedback_target = this.attributes.getValueOrDefault(
					"feedback", null);

			if (feedback_target != null) {
				more_machine_options.append(".Feedback(\""
						+ StringEscape.escapeToJavaScript(feedback_target)
						+ "\")");
			}

			String show_auto = this.attributes.getValueOrDefault(
					"showautospan", null);

			if (show_auto != null) {
				more_machine_options.append(".ShowAfterRectification(\""
						+ StringEscape.escapeToJavaScript(show_auto) + "\")");
			}

			String hide_auto = this.attributes.getValueOrDefault(
					"hideautospan", null);

			if (hide_auto != null) {
				more_machine_options.append(".HideAfterRectification(\""
						+ StringEscape.escapeToJavaScript(hide_auto) + "\")");
			}

			if (this.attributes.getValueOrDefault("lockairport", "on")
					.equalsIgnoreCase("ON") == false) {
				more_machine_options.append(".LockAfterRectification(false)");
			}

			for (FeedbackTag feedback : this.feedbackData) {
				for (InferenceSolutionRequirementTag require : feedback
						.getRequires()) {
					more_machine_options.append(".Requirement("
							+ require.getRequirementJavaScriptCheckFunction()
							+ ")");

				}

				for (HintTag hint : feedback.getHints()) {
					writer.write(".AddHint(");
					writer.write(StringEscape.escapeToDecodeInJavaScript(hint
							.getLack()));
					writer.write(",");
					writer.write(StringEscape
							.escapeToDecodableInJavaScript(hint.getHint()));
					writer.write(")");
				}

			}

			/**
			 * set the button text
			 */
			if (this.attributes.getValueOrDefault("button", null) != null) {
				more_machine_options.append(".Text(\""
						+ StringEscape.escapeToJavaScript(this.attributes
								.getValueOrDefault("button", null)) + "\")");
			}

			/**
			 * write the inference machine html code
			 */

			compiler.writeInferenceMachineCode(writer, this.attributes
					.getValueOrDefault("name", ""), this.attributes
					.getAcceptTags(), this.attributes.getRejectTags(),
					this.attributes.getValueOrDefault("pointstag", "points"),
					this.attributes.getValueOrDefault("conclusionstag",
							"conclusions"), more_machine_options.toString());

		}

		/**** OLD & OBSOLETE CODE */
		if (useInferenceApplet) {
			System.err.println("The INFERNCE APPLET IS NO LONGER SUPPORTED!");
			writer.write("<b>The INFERENCE APPLET IS NO LONGER SUPPORTED!</b>");
		}

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
			this.xmlMachineData.add((InferenceXmlTag) innerTag);
		} else if (innerTag.getClass() == FeedbackTag.class) {
			this.feedbackData.add((FeedbackTag) innerTag);
		} else {
			throw new OperationNotSupportedException(
					"<inference> cannot enclose "
							+ innerTag.getClass().toString());
		}

	}

	@Override
	public String getEfml() {
		StringBuffer representation = new StringBuffer();

		representation.append("<inference");
		this.attributes.writeXmlAttributes(representation);
		representation.append(">");
		for (EmbeddedInferenceXmlTag t : this.xmlMachineData) {
			AnyTag backconversion = (AnyTag) t;
			representation.append(backconversion.getEfml());
		}
		for (AnyTag t : this.feedbackData) {
			representation.append(t.getEfml());
		}
		representation.append("</inference>");

		return representation.toString();
	}

}
