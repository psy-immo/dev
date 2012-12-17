/**
 * CompilerServlet.java, (c) 2012, Immanuel Albrecht; Dresden University of
 * Technology, Professur für die Psychologie des Lernen und Lehrens
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

package de.tu_dresden.psy.efml.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.tu_dresden.psy.efml.StringEscape;
import de.tu_dresden.psy.efml.editor.StringCompiler;

public class CompilerServlet extends HttpServlet {

	/** generated serial id */
	private static final long serialVersionUID = -7743450395527035392L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		String efml = request.getParameter("efml");

		if (efml == null) {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
					+ "Transitional//EN\">\n" + "<HTML>\n"
					+ "<HEAD><TITLE>EFML compilation error</TITLE></HEAD>\n"
					+ "<BODY>\n"
					+ "<H1>There was no EFML data submitted!</H1>\n"
					+ "</BODY></HTML>");

			return;
		}

		StringCompiler compiler = new StringCompiler();

		String errors = compiler.compileEfml(efml);

		if (errors.isEmpty()) {
			out.println(compiler.getHtml());
		} else {
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 "
					+ "Transitional//EN\">\n" + "<HTML>\n"
					+ "<HEAD><TITLE></TITLE></HEAD>\n" + "<BODY>\n"
					+ "<H1>EFML compilation errors</H1>\n<pre>" + errors
					+ "</pre>" + "<br/>" + "<H1>Compiled EFML data</H1><pre>"
					+ StringEscape.escapeToHtml(compiler.getEfml()) + "</pre>"
					+ "<br/>" + "<H1>Submitted EFML data</H1><pre>"
					+ StringEscape.escapeToHtml(efml) + "</pre></BODY></HTML>");
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
