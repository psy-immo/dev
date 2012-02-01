package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

/**
 * implements the &lt;dropdown>-tag for having a drop down box
 * 
 * @author immanuel
 * 
 */

public class DropdownTag implements AnyTag {

	private EfmlTagsAttribute attributes;
	private String label;
	
	private ArrayList<OptionTag> options;

	public DropdownTag(EfmlTagsAttribute efmlAttributes) {
		this.attributes = efmlAttributes;
		this.label = "";
		this.options = new ArrayList<OptionTag>();
	}

	@Override
	public void open(Writer writer) throws IOException {
		writer.write("<script type=\"text/javascript\">");

		/**
		 * create new javascript dropdown object with name, tags, label, token
		 */

		writer.write(" new Dropdown(");

		writer.write("\""
				+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault(
						"name", "")) + "\", ");
		writer.write(attributes.getTags() + ", ");

		writer.write("\"" + StringEscape.escapeToJavaScript(label) + "\", ");

		writer.write("\"" +StringEscape.escapeToJavaScript(attributes
				.getValueOrDefault("value", "")) + "\")");


		/**
		 * set the background colors for the drop down box
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
		 * now add all drop down box options
		 */
		
		Iterator<OptionTag> it;
		for (it=options.iterator();it.hasNext();){
			writer.write(it.next().getJsContent());
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
			this.label += ((PlainContent) innerTag).getPlainContent();
		} else if (innerTag.getClass() == OptionTag.class) {
			this.options.add((OptionTag) innerTag);
		} else
			throw new OperationNotSupportedException(
					"<dropdown> cannot enclose "
							+ innerTag.getClass().toString());
	}

}
