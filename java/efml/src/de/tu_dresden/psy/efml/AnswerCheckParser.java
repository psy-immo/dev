/**
 * AnswerCheckParser.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.efml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * parses terms from &lt;answer>&lt;check>-tags
 * 
 * @author immanuel
 * 
 */
public class AnswerCheckParser {

	/**
	 * tokenized version of the string
	 */

	private AnswerCheckTokenizer tokens;

	/**
	 * parenthesis level of each token
	 */

	private ArrayList<Integer> levels;

	/**
	 * original input data
	 */
	private String inputTerm;

	/**
	 * currently bound variable names
	 */
	private LinkedList<String> boundVariables;

	/**
	 * variable binding types
	 */
	private Map<String, Integer> variableType;

	/**
	 * variable bound values
	 */

	private Map<String, String> variableValue;

	/**
	 * tokens forbidden for variable names
	 */

	static final String[] forbiddenTokens = new String[] { "tag", "in",
			"field", "with", "and", "or" };

	/**
	 * Variable types:
	 * 
	 */

	private final int VAR_FIELD = 0;
	private final int VAR_TAG = 1;

	private boolean checked;
	private String error;

	/**
	 * contains generated javaScript code
	 */
	private String jsCode;

	private EfmlTagsAttribute attributes;

	public AnswerCheckParser(String term, EfmlTagsAttribute attributes) {

		this.attributes = attributes;

		this.checked = false;

		this.inputTerm = term;

		/**
		 * run tokenizer on the term
		 */
		this.tokens = new AnswerCheckTokenizer(term);

		/**
		 * levelize the term, i.e. ( -> level up, ) -> level down
		 */

		int current_level = 0;

		levels = new ArrayList<Integer>();

		for (Iterator<AnswerCheckTokenizer.Token> it = tokens.iterator(); it
				.hasNext();) {
			AnswerCheckTokenizer.Token token = it.next();
			if (token.quoted == false) {
				if (token.value.equalsIgnoreCase("(")) {
					current_level += 1;
				}
			}

			levels.add(current_level);

			if (token.quoted == false) {
				if (token.value.equalsIgnoreCase(")")) {
					current_level -= 1;
				}
			}
		}
	}

	/**
	 * checks, whether a token may not be used as variable symbol
	 * 
	 * @param token
	 * @return true, if the token cannot be used as variable symbol
	 */

	public boolean isForbidden(String token) {
		for (int i = 0; i < forbiddenTokens.length; ++i)
			if (token.equalsIgnoreCase(forbiddenTokens[i]) == true)
				return true;
		/**
		 * test whether it is alphanumeric
		 */

		for (int c = 0; c < token.length(); ++c) {
			if (Character.isLetterOrDigit(token.charAt(c)) == false)
				return true;
		}

		return Character.isLetter(token.charAt(0)) != true;
	}

	/**
	 * 
	 * @return corresponing javascript code
	 */
	public String getJsCode() {
		if (checked == false) {
			checkSyntaxAndParse();
		}
		if (error != null) {
			/**
			 * disable this check...
			 */
			return "function(){\nreturn true;\n}";
		} else {
			return "function(){\n var goodParts = [];\n var result = (function(){\n"
					+ this.jsCode
					+ "\n})();"
					+ "if (result) {\n"
					+ " for (var i=0;i<goodParts.length;++i) {\n"
					+ " if (goodParts[i].MarkAsGood) {\n"
					+ "goodParts[i].MarkAsGood();\n}\n"
					+ " }\n}\nreturn result;}";
		}
	}

	/**
	 * checks the syntax
	 * 
	 * @return null, if no syntax errors are found, otherwise error description
	 */
	public String checkSyntaxAndParse() {
		if (this.checked == true) {
			return this.error;
		}

		this.boundVariables = new LinkedList<String>();
		this.variableType = new HashMap<String, Integer>();
		this.variableValue = new HashMap<String, String>();
		this.jsCode = "\n";
		this.error = enterTerm(0, this.tokens.length());
		this.checked = true;

		return error;
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void openTagVariable(String variable) {
		if (variableValue.get(variable).isEmpty() == true) {
			jsCode += "return Exists(myTags.tags, function(tVar" + variable
					+ "){\n";
		} else {
			jsCode += "return Exists([" + variableValue.get(variable)
					+ "], function(tVar" + variable + "){\n";
		}
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void closeTagVariable(String variable) {
		jsCode += "});";
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void openFieldVariable(String variable) {

		jsCode += "return Exists(myTags.AllTagsBut(["
				+ variableValue.get(variable) + "],"
				+ attributes.getRejectTags() + "), function(fVar" + variable
				+ "){\n";
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void closeFieldVariable(String variable) {
		jsCode += "});";
	}

	/**
	 * generate javaScript code
	 */

	private void startAndSequence() {
		jsCode += "var previous_good_count = goodParts.length;\n"
				+ "var failed = false;";
		jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void betweenAndSequence() {
		jsCode += "})()) {\n" + "} else {\n" + " failed = true;\n" + "}\n";
		jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void endAndSequence() {
		jsCode += "})()) {\n" + "} else {\n" + " failed = true;\n" + "}\n";

		jsCode += " if (failed) {\n"
				+ "  while (goodParts.length > previous_good_count) goodParts.pop();\n"
				+ "  return null;\n" + "} else {\n" + "  return true;\n" + "}";
	}

	/**
	 * generate javaScript code
	 */

	private void startOrSequence() {
		jsCode += "var previous_good_count = goodParts.length;\n"
				+ "var succeeded = false;";
		jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void betweenOrSequence() {
		jsCode += "})()) {\n succeeded=true;" + "}\n";
		jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void endOrSequence() {
		jsCode += "})()) {\n succeeded=true;" + "}\n";
		jsCode += " return succeeded;";
	}

	/**
	 * implements shared structures for parsing terms
	 * 
	 * @author albrecht
	 * 
	 */
	private class SubtermStructure {
		/**
		 * current error description
		 */
		public String error_description;
		/**
		 * current error position
		 */
		public int where;

		/**
		 * local term depth
		 */
		public int local_depth;

		public ArrayList<Integer> subterm_start;
		public ArrayList<Integer> subterm_end;
		public ArrayList<Boolean> subterm_rootlevel;

		public int first, last;

		/**
		 * construct the subterm structure
		 * 
		 * @param termFirst
		 *            first element of subterm
		 * @param termLast
		 *            element after the last element of the subterm
		 */

		public SubtermStructure(int termFirst, int termLast) {
			this.first = termFirst;
			this.last = termLast;

			error_description = "token utterly unrecognized";
			where = tokens.getEndIndex(first);

			/**
			 * get the local depth of the term
			 */
			local_depth = levels.get(first);
			for (int c = first; c < last; ++c) {
				if (levels.get(c) < local_depth) {
					local_depth = levels.get(c);
				}
			}

			/**
			 * create intermediate sub term structure
			 */

			subterm_start = new ArrayList<Integer>();
			subterm_end = new ArrayList<Integer>();
			subterm_rootlevel = new ArrayList<Boolean>();

			int start = first;
			boolean rootlevel = levels.get(first) == local_depth;

			for (int end = first + 1; end < last; ++end) {
				if ((levels.get(end) == local_depth) || (rootlevel)) {
					subterm_start.add(start);
					subterm_end.add(end);
					subterm_rootlevel.add(rootlevel);

					rootlevel = levels.get(end) == local_depth;
					start = end;
				}
			}

			subterm_start.add(start);
			subterm_end.add(last);
			subterm_rootlevel.add(rootlevel);

			for (int c = 0; c < subterm_rootlevel.size(); ++c) {
				if (subterm_rootlevel.get(c) == true) {
					where = tokens.getEndIndex(subterm_start.get(c));
					break;
				}
			}
		}

		public String getErrorMessage() {
			/**
			 * format error message
			 */

			String message = "Syntax-Error: " + error_description
					+ " just before the " + (2 + where) + "th character: ";

			int end_index = where;

			if (where > 16) {
				message += "[...]";
			} else
				where = 16;

			int start_index = where - 16;

			message += inputTerm.substring(start_index, end_index + 1);

			message += " <--HERE--|";

			if (end_index + 1 < inputTerm.length()) {

				if (inputTerm.length() - end_index - 1 > 16) {
					message += " "
							+ inputTerm
									.substring(end_index + 1, end_index + 17)
							+ "[...]";
				} else
					message += " " + inputTerm.substring(end_index + 1);
			}
			return message;
		}

		/**
		 * convenient way to return error message
		 * 
		 * @param error
		 *            error description
		 * @param where
		 *            position
		 * @return this.getErrorMessage()
		 */
		public String errorFound(String error, int where) {
			this.error_description = error;
			this.where = where;

			return getErrorMessage();
		}
	};

	/**
	 * 
	 * @param term
	 *            subterm of n-ary and structure
	 * @return error message or null
	 */
	private String enterAnd(SubtermStructure term) {
		for (int i = 0; i < term.subterm_rootlevel.size(); ++i) {
			if (i % 2 == 0) {
				if (term.subterm_rootlevel.get(i) != false) {

					return term.errorFound("Token should be in parenthesis",
							tokens.getEndIndex(term.subterm_start.get(i)));
				}
			} else {

				if (term.subterm_rootlevel.get(i) != true) {
					/**
					 * this code will never be reached according to the upper
					 * subterm by level algorithm,
					 * 
					 * we will keep it anyway
					 */
					return term.errorFound(
							"Two terms without connecting token",
							tokens.getEndIndex(term.subterm_start.get(i)));

				} else if ("and".equalsIgnoreCase(tokens
						.getValue(term.subterm_start.get(i))) != true) {
					return term
							.errorFound(
									"Connective '"
											+ "and"
											+ "' may not be mixed with '"
											+ tokens.getValue(term.subterm_start
													.get(i)) + "'", tokens
											.getEndIndex(term.subterm_start
													.get(i)));

				}
			}
		}

		if (term.subterm_rootlevel.size() % 2 == 0) {
			return term.errorFound("Term connective without right hand term",
					tokens.getEndIndex(term.subterm_start
							.get(term.subterm_start.size() - 1)));

		} else {
			/**
			 * and/or n-ary term well-formed, if subterms are well-formed
			 */
			startAndSequence();

			for (int i = 0; i < term.subterm_rootlevel.size(); i += 2) {
				if (i > 0) {

					betweenAndSequence();

				}

				String error = enterTerm(term.subterm_start.get(i),
						term.subterm_end.get(i));

				if (error != null)
					return error;
			}

			endAndSequence();

			return null;
		}
	}

	/**
	 * 
	 * @param term
	 *            subterm of n-ary or structure
	 * @return error message or null
	 */
	private String enterOr(SubtermStructure term) {
		for (int i = 0; i < term.subterm_rootlevel.size(); ++i) {
			if (i % 2 == 0) {
				if (term.subterm_rootlevel.get(i) != false) {
					return term.errorFound("Token should be in parenthesis",
							tokens.getEndIndex(term.subterm_start.get(i)));

				}
			} else {

				if (term.subterm_rootlevel.get(i) != true) {
					/**
					 * this code will never be reached according to the upper
					 * subterm by level algorithm,
					 * 
					 * we will keep it anyway
					 */
					return term.errorFound(
							"Two terms without connecting token",
							tokens.getEndIndex(term.subterm_start.get(i)));
				} else if ("or".equalsIgnoreCase(tokens
						.getValue(term.subterm_start.get(i))) != true) {
					return term
							.errorFound(
									"Connective '"
											+ "or"
											+ "' may not be mixed with '"
											+ tokens.getValue(term.subterm_start
													.get(i)) + "'", tokens
											.getEndIndex(term.subterm_start
													.get(i)));

				}
			}
		}

		if (term.subterm_rootlevel.size() % 2 == 0) {
			return term.errorFound("Term connective without right hand term",
					tokens.getEndIndex(term.subterm_start
							.get(term.subterm_start.size() - 1)));

		} else {
			/**
			 * and/or n-ary term well-formed, if subterms are well-formed
			 */

			startOrSequence();

			for (int i = 0; i < term.subterm_rootlevel.size(); i += 2) {
				if (i > 0) {

					betweenOrSequence();

				}

				String error = enterTerm(term.subterm_start.get(i),
						term.subterm_end.get(i));
				if (error != null)
					return error;
			}

			endOrSequence();

			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param term
	 *            subterm starting with '('
	 * @return error message or null
	 */

	private String enterSubterm(SubtermStructure term) {
		if ((tokens.isQuoted(term.last - 1) == false)
				&& (tokens.getValue(term.last - 1).equalsIgnoreCase(")"))) {
			/**
			 * term in parenthesis is valid, if the enclosed subterm is valid
			 */
			return enterTerm(term.first + 1, term.last - 1);
		} else {
			return term.errorFound("Closing parenthesis is missing",
					tokens.getEndIndex(term.last - 1));

		}
	}

	private String enterExistsTag(SubtermStructure term) {
		/**
		 * tag VARNAME : RHS
		 * 
		 * or
		 * 
		 * tag VARNAME in ("tag"|TAGVARIABLE)(,("tag"|TAGVARIABLE)*) : RHS
		 */

		if (term.subterm_rootlevel.size() < 2) {
			return term.errorFound("missing variable name after 'tag'",
					tokens.getEndIndex(term.first));
		} else {
			if (term.subterm_rootlevel.get(1) == true) {
				if (tokens.isQuoted(term.subterm_start.get(1)) == true) {
					return term.errorFound(
							"String literals cannot be variable names",
							tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					String variable = tokens
							.getValue(term.subterm_start.get(1)).toLowerCase();
					if (boundVariables.contains(variable) == true) {
						return term.errorFound("Variable symbol '" + variable
								+ "' is already taken ",
								tokens.getEndIndex(term.subterm_start.get(1)));
					} else if (isForbidden(variable) == true) {
						return term.errorFound("'" + variable
								+ "' connot be used as variable symbol",
								tokens.getEndIndex(term.subterm_start.get(1)));
					} else {
						if (term.subterm_rootlevel.size() < 3) {
							return term.errorFound("':' or 'in' missing",
									tokens.getEndIndex(term.subterm_start
											.get(1)));
						} else if (term.subterm_rootlevel.get(2) == true) {
							String colon_or_in = tokens
									.getValue(term.subterm_start.get(2));
							if (tokens.isQuoted(term.subterm_start.get(2)) == false) {
								if (colon_or_in.equalsIgnoreCase(":") == true) {
									/**
									 * tag VARNAME : RHS
									 */
									boundVariables.push(variable);
									variableType.put(variable, VAR_TAG);

									variableValue.put(variable, "");

									openTagVariable(variable);

									/**
									 * check RHS term
									 */

									String error = enterTerm(
											term.subterm_start.get(2) + 1,
											term.last);

									closeTagVariable(variable);

									boundVariables.pop();
									variableType.remove(variable);
									variableValue.remove(variable);

									return error;
								} else if (colon_or_in.equalsIgnoreCase("in")) {
									/**
									 * check for colon
									 */
									int colon_term = 3;
									boolean colon_found = false;

									String value = "";

									while (colon_found != true) {
										if (term.subterm_rootlevel.size() <= colon_term) {
											return term
													.errorFound(
															"':' after 'tag "
																	+ variable
																	+ " in [...]' missing",
															tokens.getEndIndex(term.last - 1));

										}

										if (term.subterm_rootlevel
												.get(colon_term) != true) {
											return term
													.errorFound(
															"Terms cannot be tags",
															tokens.getEndIndex(term.subterm_start
																	.get(colon_term)));

										}

										String tag_token = tokens.getValue(
												term.subterm_start
														.get(colon_term))
												.toLowerCase();

										if (tokens.isQuoted(term.subterm_start
												.get(colon_term)) == true) {
											/**
											 * tag given by string literal
											 */

											if (value.isEmpty() == false) {
												value += ", ";
											}
											value += "\""
													+ StringEscape
															.escapeToJavaScript(tag_token)
													+ "\"";
										} else {
											if (tag_token.equalsIgnoreCase(":") == true) {
												colon_found = true;
												break;
											}
											if (boundVariables
													.contains(tag_token)) {
												if (variableType.get(tag_token) == VAR_TAG) {
													if (value.isEmpty() == false) {
														value += ", ";
													}
													value += "tVar" + tag_token;
												} else {
													term.error_description = "'"
															+ tag_token
															+ "' is a variable, but not a token variable";
													term.where = tokens
															.getEndIndex(term.subterm_start
																	.get(colon_term));
													break;
												}
											} else {
												return term
														.errorFound(
																"'"
																		+ tag_token
																		+ "' is neither a bound token variable nor a string literal",
																tokens.getEndIndex(term.subterm_start
																		.get(colon_term)));

											}
										}

										colon_term++;
									}

									if (colon_found) {
										boundVariables.push(variable);
										variableType.put(variable, VAR_TAG);
										variableValue.put(variable, value);

										openTagVariable(variable);

										/**
										 * check RHS term
										 */

										String error = enterTerm(
												term.subterm_start
														.get(colon_term) + 1,
												term.last);

										closeTagVariable(variable);

										boundVariables.pop();
										variableType.remove(variable);
										variableValue.remove(variable);

										return error;
									}

								} else {
									return term
											.errorFound(
													"':' or 'in' expected",
													tokens.getEndIndex(term.subterm_start
															.get(2)));
								}
							} else {
								return term
										.errorFound(
												"':' or 'in' expected, quoted string found",
												tokens.getEndIndex(term.subterm_start
														.get(2)));
							}
						} else {
							return term
									.errorFound(
											"':' or 'in' expected, term in parenthesis found",
											tokens.getEndIndex(term.subterm_start
													.get(2)));
						}
					}
				}
			} else {
				return term.errorFound("Terms cannot be variable names",
						tokens.getEndIndex(term.subterm_start.get(1)));
			}
		}

		return term.getErrorMessage(); /*
										 * SHOULD BE NEVER REACHED, STOP JAVA
										 * COMPLAINTS
										 */
	}

	private String enterExistsField(SubtermStructure term) {
		/**
		 * field VARNAME : RHS
		 * 
		 * or
		 * 
		 * field VARNAME with ("tag"|TAGVARIABLE)(,("tag"|TAGVARIABLE)*) : RHS
		 */

		if (term.subterm_rootlevel.size() < 2) {
			return term.errorFound("missing variable name after 'tag'",
					tokens.getEndIndex(term.first));
		} else {
			if (term.subterm_rootlevel.get(1) == true) {
				if (tokens.isQuoted(term.subterm_start.get(1)) == true) {
					return term.errorFound(
							"String literals cannot be variable names",
							tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					String variable = tokens
							.getValue(term.subterm_start.get(1)).toLowerCase();
					if (boundVariables.contains(variable) == true) {
						return term.errorFound("Variable symbol '" + variable
								+ "' is already taken ",
								tokens.getEndIndex(term.subterm_start.get(1)));
					} else if (isForbidden(variable) == true) {
						return term.errorFound("'" + variable
								+ "' connot be used as variable symbol",
								tokens.getEndIndex(term.subterm_start.get(1)));
					} else {
						if (term.subterm_rootlevel.size() < 3) {
							return term.errorFound("':' or 'with' missing",
									tokens.getEndIndex(term.subterm_start
											.get(1)));
						} else if (term.subterm_rootlevel.get(2) == true) {
							String colon_or_in = tokens
									.getValue(term.subterm_start.get(2));
							if (tokens.isQuoted(term.subterm_start.get(2)) == false) {
								if (colon_or_in.equalsIgnoreCase(":") == true) {
									/**
									 * field VARNAME : RHS
									 */
									boundVariables.push(variable);
									variableType.put(variable, VAR_FIELD);
									variableValue.put(variable,
											attributes.getAcceptTagsCommas());

									openFieldVariable(variable);

									/**
									 * check RHS term
									 */

									String error = enterTerm(
											term.subterm_start.get(2) + 1,
											term.last);

									closeFieldVariable(variable);

									boundVariables.pop();
									variableType.remove(variable);
									variableValue.remove(variable);

									return error;
								} else if (colon_or_in.equalsIgnoreCase("with")) {
									/**
									 * check for colon
									 */
									int colon_term = 3;
									boolean colon_found = false;

									String value = attributes
											.getAcceptTagsCommas();

									while (colon_found != true) {
										if (term.subterm_rootlevel.size() <= colon_term) {
											return term
													.errorFound(
															"':' after 'field "
																	+ variable
																	+ " with [...]' missing",
															tokens.getEndIndex(term.last - 1));

										}

										if (term.subterm_rootlevel
												.get(colon_term) != true) {

											return term
													.errorFound(
															"Terms cannot be tags",
															tokens.getEndIndex(term.subterm_start
																	.get(colon_term)));

										}

										String tag_token = tokens.getValue(
												term.subterm_start
														.get(colon_term))
												.toLowerCase();

										if (tokens.isQuoted(term.subterm_start
												.get(colon_term)) == true) {
											/**
											 * tag given by string literal
											 */
											if (value.isEmpty() == false) {
												value += ", ";
											}
											value += "\""
													+ StringEscape
															.escapeToJavaScript(tag_token)
													+ "\"";
										} else {
											if (tag_token.equalsIgnoreCase(":") == true) {
												colon_found = true;
												break;
											}
											if (boundVariables
													.contains(tag_token)) {
												if (variableType.get(tag_token) == VAR_TAG) {
													if (value.isEmpty() == false) {
														value += ", ";
													}
													value += "tVar" + tag_token;
												} else {
													return term
															.errorFound(
																	"'"
																			+ tag_token
																			+ "' is a variable, but not a token variable",
																	tokens.getEndIndex(term.subterm_start
																			.get(colon_term)));

												}
											} else {

												return term
														.errorFound(
																"'"
																		+ tag_token
																		+ "' is neither a bound token variable nor a string literal",
																tokens.getEndIndex(term.subterm_start
																		.get(colon_term)));

											}
										}

										colon_term++;
									}

									if (colon_found) {
										boundVariables.push(variable);
										variableType.put(variable, VAR_FIELD);
										variableValue.put(variable, value);

										openFieldVariable(variable);

										/**
										 * check RHS term
										 */

										String error = enterTerm(
												term.subterm_start
														.get(colon_term) + 1,
												term.last);

										closeFieldVariable(variable);

										boundVariables.pop();
										variableType.remove(variable);
										variableValue.remove(variable);

										return error;
									}

								} else {
									return term
											.errorFound(
													"':' or 'with' expected",
													tokens.getEndIndex(term.subterm_start
															.get(2)));
								}
							} else {
								return term
										.errorFound(
												"':' or 'with' expected, quoted string found",
												tokens.getEndIndex(term.subterm_start
														.get(2)));
							}
						} else {
							return term
									.errorFound(
											"':' or 'with' expected, term in parenthesis found",
											tokens.getEndIndex(term.subterm_start
													.get(2)));
						}
					}
				}
			} else {
				return term.errorFound("Terms cannot be variable names",
						tokens.getEndIndex(term.subterm_start.get(1)));
			}
		}

		return term.getErrorMessage(); /*
										 * SHOULD BE NEVER REACHED, STOP JAVA
										 * COMPLAINTS
										 */
	}

	/**
	 * 
	 * @param term
	 *            subterm of VARIABLE = ... style
	 * @return error message or null
	 */
	private String enterCheckValue(SubtermStructure term) {

		String first_token = tokens.getValue(term.first);

		/**
		 * term starts with a variable symbol
		 */
		String variable = first_token.toLowerCase();
		if (variableType.get(variable) == VAR_FIELD) {

			if (term.subterm_rootlevel.size() != 3) {
				return term
						.errorFound(
								"Field comparision must be of the form FIELD = FIELD or FIELD = \"STRING\"",
								tokens.getEndIndex(term.first));
			} else {
				if (term.subterm_rootlevel.get(1) != true) {
					return term.errorFound(
							"'=' expected, term in parenthesis found",
							tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					if (tokens.isQuoted(term.subterm_start.get(1)) == true) {
						return term.errorFound(
								"'=' expected, string literal found",
								tokens.getEndIndex(term.subterm_start.get(1)));
					} else if (tokens.getValue(term.subterm_start.get(1))
							.equals("=")) {
						if (term.subterm_rootlevel.get(2) != true) {
							return term.errorFound(
									"String literal or field variable needed",
									tokens.getEndIndex(term.subterm_start
											.get(2)));
						} else {
							if (tokens.isQuoted(term.subterm_start.get(2)) == true) {
								/**
								 * FIELD = "STRING"
								 */

								jsCode += " if (fVar"
										+ variable
										+ ".token) {\n"
										+ "\nif (fVar"
										+ variable
										+ ".token == "
										+ StringEscape
												.escapeToDecodeInJavaScript(tokens
														.getValue(term.subterm_start
																.get(2)))
										+ ") {\n goodParts.push(fVar"
										+ variable
										+ ");\n return true; } else {\n return false;\n}"
										+ "\n} else {\n return false;\n }";

								return null;
							} else {
								String comparison_variable = tokens.getValue(
										term.subterm_start.get(2))
										.toLowerCase();
								if (boundVariables
										.contains(comparison_variable) == true) {
									if (variableType.get(comparison_variable) == VAR_FIELD) {
										/**
										 * FIELD = FIELD
										 */

										jsCode += " if (fVar"
												+ variable
												+ ".token) {\n"
												+ "\nif (fVar"
												+ variable
												+ ".token == "
												+ "fVar"
												+ comparison_variable
												+ ".token"
												+ ") {\n goodParts.push(fVar"
												+ variable
												+ ");\n return true; } else {\n return false;\n}"
												+ "\n} else {\n return false;\n }";

										return null;
									} else {
										return term
												.errorFound(
														"'"
																+ comparison_variable
																+ "' is variable, but not a field variable",
														tokens.getEndIndex(term.subterm_start
																.get(2)));
									}
								} else {
									return term
											.errorFound(
													"Field variable or string literal needed",
													tokens.getEndIndex(term.subterm_start
															.get(2)));
								}
							}
						}
					} else {
						return term.errorFound("'=' expected",
								tokens.getEndIndex(term.subterm_start.get(1)));
					}
				}
			}

		} else {
			return term.errorFound("'" + first_token
					+ "' is a variable, but not a field variable",
					tokens.getEndIndex(term.first));
		}

		
	}

	/**
	 * checks the syntax of a part, for internal usage only!
	 * 
	 * @param first
	 *            first element of the part to check
	 * @param last
	 *            element after the last element of the part that is to check
	 * @return null, if no syntax errors are found, otherwise error description
	 */
	private String enterTerm(int term_first, int term_last) {

		/**
		 * the empty string is a valid term
		 */
		if (term_first >= term_last) {

			jsCode += "return true;";

			return null;
		}

		/**
		 * compute the subterm structure
		 */

		SubtermStructure term = new SubtermStructure(term_first, term_last);

		/**
		 * check the subterm/token structure
		 */

		if (term.subterm_rootlevel.get(0) == false) {
			/**
			 * term begins with subterm, could be n-ary "and", or n-ary "or"
			 * term
			 */

			String middle_token = tokens.getValue(term.subterm_start.get(1));

			if (middle_token.equalsIgnoreCase("and") == true) {
				/**
				 * n-ary "and" operator
				 */

				return enterAnd(term);

			} else if (middle_token.equalsIgnoreCase("or") == true) {
				/**
				 * n-ary "or" operator
				 */

				return enterOr(term);
			} else
				return term.errorFound("Term connective unknown",
						tokens.getEndIndex(term.subterm_start.get(1)));

		} else {
			/**
			 * term begins with a token
			 */
			if (tokens.isQuoted(term.first) == true) {
				/**
				 * term begins with string literal
				 */
				return term.errorFound(
						"A term may not start with a string literal",
						tokens.getEndIndex(term.first));

			} else {
				String first_token = tokens.getValue(term.first);

				if (first_token.equalsIgnoreCase("(") == true) {
					return enterSubterm(term);
				} else if ((first_token.equalsIgnoreCase("or") == true)
						|| (first_token.equalsIgnoreCase("and") == true)) {
					return term.errorFound(
							"Term connective without left hand term",
							tokens.getEndIndex(term.first));

				} else if (first_token.equalsIgnoreCase(")") == true) {
					return term.errorFound(
							"Two terms in parenthesis without term connective",
							tokens.getEndIndex(term.first));
				} else if (first_token.equalsIgnoreCase("tag") == true) {
					return enterExistsTag(term);
				} else if (first_token.equalsIgnoreCase("field") == true) {
					return enterExistsField(term);
				} else if (boundVariables.contains(first_token.toLowerCase())) {
					return enterCheckValue(term);
				}
			}
		}

		return term.getErrorMessage();
	}

}
