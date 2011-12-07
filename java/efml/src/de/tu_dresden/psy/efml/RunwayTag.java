package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

import javax.naming.OperationNotSupportedException;

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
		 * create new javascript Runway object with name, tags, token, accept, reject 
		 */
		
		writer.write(" new Runway(");
		
		writer.write("\""+ StringEscape.escapeToJavaScript(attributes.getValueOrDefault("name", ""))+"\", ");
		writer.write(attributes.getTags()+", ");
		
		writer.write("\""+StringEscape.escapeToJavaScript(token)+"\", ");
		
		writer.write(attributes.getAcceptTags()+", ");
		writer.write(attributes.getRejectTags()+")");

		
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
			this.token += ((PlainContent)innerTag).getContent();
		} else
		throw new OperationNotSupportedException("<runway> cannot enclose " + innerTag.getClass().toString());
	}

}
