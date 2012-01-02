package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;runway>-tag modelling an airfield that sends and takes
 * "text" as planes via mouse interaction
 * 
 * @author immanuel
 * 
 */

public class RunwayTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String token;

	public RunwayTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.token = "";
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new javascript Runway object with name, tags, token, accept,
		 * reject
		 */

		writer.write(" new Runway(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", "")) + "\", ");
		writer.write(attributes.getTags() + ", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(token) + "\", ");

		writer.write(attributes.getAcceptTags() + ", ");
		writer.write(attributes.getRejectTags() + ")");

		/**
		 * content attribute will change behavior,
		 * 
		 * respawn if contents disappear from screen, they will respawn in this
		 * run way refill contents will be refilled instantly after take off
		 */

		String content = attributes.getValueOrDefault("content", "").trim();

		if (content.equalsIgnoreCase("RESPAWN")) {
			writer.write(".Respawn()");

		} else if (content.equalsIgnoreCase("REFILLING")) {
			writer.write(".Refilling()");
		}

		/**
		 * set the background colors for the run way
		 * 
		 * color empty background color filled background color when filled with
		 * token
		 */

		if ((attributes.getValueOrDefault("color", null) != null)
				|| (attributes.getValueOrDefault("filled", null) != null)) {
			String empty = attributes.getValueOrDefault("color", "#CCCCCC");
			String filled = attributes.getValueOrDefault("filled", "#CCCCFF");

			writer.write(".Color(\"" + StringEscape.escapeToJavaScript(empty)
					+ "\", \"" + StringEscape.escapeToJavaScript(filled)
					+ "\")");
		}

		/**
		 * set the size parameter for the run way
		 * 
		 * width, height (note: give CSS sizes, e.g. 200px)
		 */

		if ((attributes.getValueOrDefault("width", null) != null)
				|| (attributes.getValueOrDefault("height", null) != null)) {
			String width = attributes.getValueOrDefault("width", "200px");
			String height = attributes.getValueOrDefault("height", "20px");

			writer.write(".Size(\"" + StringEscape.escapeToJavaScript(width)
					+ "\", \"" + StringEscape.escapeToJavaScript(height)
					+ "\")");
		}

		/**
		 * finally let javascript create the html code
		 */

		writer.write(".WriteHtml();");
	}

	@Override
	public void close(Writer writer) throws IOException {
		writer.write(" </script>");
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		if (innerTag.getClass() == PlainContent.class) {
			this.token += ((PlainContent) innerTag).getContent();
		} else
			throw new OperationNotSupportedException("<runway> cannot enclose "
					+ innerTag.getClass().toString());
	}

}
