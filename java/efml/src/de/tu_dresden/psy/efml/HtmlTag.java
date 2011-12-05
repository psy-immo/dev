package de.tu_dresden.psy.efml;

import java.io.IOException;
import java.io.Writer;

public class HtmlTag implements AnyHtmlTag {

	@Override
	public void Open(Writer writer) throws IOException {
		writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" + 
				"<html>");
	}

	@Override
	public void Close(Writer writer) throws IOException {
		writer.write("</html>"); 
	}

}
