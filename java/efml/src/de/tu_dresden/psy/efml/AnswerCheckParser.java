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
		this.error = checkSyntaxTree(0, this.tokens.length());
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
	 * checks the syntax of a part, for internal usage only!
	 * 
	 * @param first
	 *            first element of the part to check
	 * @param last
	 *            element after the last element of the part that is to check
	 * @return null, if no syntax errors are found, otherwise error description
	 */
	private String checkSyntaxTree(int first, int last) {

		/**
		 * the empty string is a valid term
		 */
		if (first >= last) {

			jsCode += "return true;";

			return null;
		}

		String error_description = "token utterly unrecognized";
		int where = tokens.getEndIndex(first);

		/**
		 * get the local depth of the term
		 */
		int local_depth = levels.get(first);
		for (int c = first; c < last; ++c) {
			if (levels.get(c) < local_depth) {
				local_depth = levels.get(c);
			}
		}

		/**
		 * create intermediate sub term structure
		 */

		ArrayList<Integer> subterm_start = new ArrayList<Integer>();
		ArrayList<Integer> subterm_end = new ArrayList<Integer>();
		ArrayList<Boolean> subterm_rootlevel = new ArrayList<Boolean>();

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

		/**
		 * check the subterm/token structure
		 */

		if (subterm_rootlevel.get(0) == false) {
			/**
			 * term begins with subterm, could be n-ary "and", or n-ary "or"
			 * term
			 */

			String middle_token = tokens.getValue(subterm_start.get(1));
			boolean operator_known = false;

			if (middle_token.equalsIgnoreCase("and") == true) {
				/**
				 * n-ary "and" operator
				 */

				operator_known = true;

			} else if (middle_token.equalsIgnoreCase("or") == true) {
				/**
				 * n-ary "or" operator
				 */

				operator_known = true;
			}

			/**
			 * check for alternating structure: (subterm token)+ subterm
			 */

			if (operator_known) {

				for (int i = 0; i < subterm_rootlevel.size(); ++i) {
					if (i % 2 == 0) {
						if (subterm_rootlevel.get(i) != false) {
							error_description = "Token should be in parenthesis";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						}
					} else {

						if (subterm_rootlevel.get(i) != true) {
							/**
							 * this code will never be reached according to the
							 * upper subterm by level algorithm,
							 * 
							 * we will keep it anyway
							 */
							error_description = "Two terms without connecting token";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						} else if (middle_token.equalsIgnoreCase(tokens
								.getValue(subterm_start.get(i))) != true) {
							error_description = "Connective '" + middle_token
									+ "' may not be mixed with '"
									+ tokens.getValue(subterm_start.get(i))
									+ "'";
							where = tokens.getEndIndex(subterm_start.get(i));
							break;
						}
					}
				}

				if (subterm_rootlevel.size() % 2 == 0) {
					error_description = "Term connective without right hand term";
					where = tokens.getEndIndex(subterm_start.get(subterm_start
							.size() - 1));

				} else {
					/**
					 * and/or n-ary term well-formed, if subterms are
					 * well-formed
					 */
					if (middle_token.equalsIgnoreCase("and") == true) {
						startAndSequence();

					} else if (middle_token.equalsIgnoreCase("or") == true) {
						startOrSequence();
						
					}

					for (int i = 0; i < subterm_rootlevel.size(); i += 2) {
						if (i>0) {
							if (middle_token.equalsIgnoreCase("and") == true) {
								betweenAndSequence();

							} else if (middle_token.equalsIgnoreCase("or") == true) {
								betweenOrSequence();
							}		
						}
						
						String error = checkSyntaxTree(subterm_start.get(i),
								subterm_end.get(i));
						if (error != null)
							return error;
					}
					if (middle_token.equalsIgnoreCase("and") == true) {
						endAndSequence();

					} else if (middle_token.equalsIgnoreCase("or") == true) {
						endOrSequence();
					}					

					return null;
				}
			} else {
				error_description = "Term connective unknown";
				where = tokens.getEndIndex(subterm_start.get(1));
			}

		} else {
			/**
			 * term begins with a token
			 */
			if (tokens.isQuoted(first) == true) {
				/**
				 * term begins with string literal
				 */
				error_description = "A term may not start with a string literal";
				where = tokens.getEndIndex(first);

			} else {
				String first_token = tokens.getValue(first);

				if (first_token.equalsIgnoreCase("(") == true) {
					if ((tokens.isQuoted(last - 1) == false)
							&& (tokens.getValue(last - 1).equalsIgnoreCase(")"))) {
						/**
						 * term in parenthesis is valid, if the enclosed subterm
						 * is valid
						 */
						return checkSyntaxTree(first + 1, last - 1);
					} else {
						error_description = "Closing parenthesis is missing";
						where = tokens.getEndIndex(last - 1);

					}
				} else if ((first_token.equalsIgnoreCase("or") == true)
						|| (first_token.equalsIgnoreCase("and") == true)) {
					error_description = "Term connective without left hand term";
					where = tokens.getEndIndex(first);

				} else if (first_token.equalsIgnoreCase(")") == true) {
					error_description = "Two terms in parenthesis without term connective";
					where = tokens.getEndIndex(first);
				} else if (first_token.equalsIgnoreCase("tag") == true) {
					/**
					 * tag VARNAME : RHS
					 * 
					 * or
					 * 
					 * tag VARNAME in ("tag"|TAGVARIABLE)(,("tag"|TAGVARIABLE)*)
					 * : RHS
					 */

					if (subterm_rootlevel.size() < 2) {
						error_description = "missing variable name after 'tag'";
						where = tokens.getEndIndex(first);
					} else {
						if (subterm_rootlevel.get(1) == true) {
							if (tokens.isQuoted(subterm_start.get(1)) == true) {
								error_description = "String literals cannot be variable names";
								where = tokens
										.getEndIndex(subterm_start.get(1));
							} else {
								String variable = tokens.getValue(
										subterm_start.get(1)).toLowerCase();
								if (boundVariables.contains(variable) == true) {
									error_description = "Variable symbol '"
											+ variable + "' is already taken ";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								} else if (isForbidden(variable) == true) {
									error_description = "'"
											+ variable
											+ "' connot be used as variable symbol";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								} else {
									if (subterm_rootlevel.size() < 3) {
										error_description = "':' or 'in' missing";
										where = tokens
												.getEndIndex(subterm_start
														.get(1));
									} else if (subterm_rootlevel.get(2) == true) {
										String colon_or_in = tokens
												.getValue(subterm_start.get(2));
										if (tokens.isQuoted(subterm_start
												.get(2)) == false) {
											if (colon_or_in
													.equalsIgnoreCase(":") == true) {
												/**
												 * tag VARNAME : RHS
												 */
												boundVariables.push(variable);
												variableType.put(variable,
														VAR_TAG);

												variableValue.put(variable, "");

												openTagVariable(variable);

												/**
												 * check RHS term
												 */

												String error = checkSyntaxTree(
														subterm_start.get(2) + 1,
														last);

												closeTagVariable(variable);

												boundVariables.pop();
												variableType.remove(variable);
												variableValue.remove(variable);

												return error;
											} else if (colon_or_in
													.equalsIgnoreCase("in")) {
												/**
												 * check for colon
												 */
												int colon_term = 3;
												boolean colon_found = false;

												String value = "";

												while (colon_found != true) {
													if (subterm_rootlevel
															.size() <= colon_term) {
														error_description = "':' after 'tag "
																+ variable
																+ " in [...]' missing";
														where = tokens
																.getEndIndex(last - 1);
														break;
													}

													if (subterm_rootlevel
															.get(colon_term) != true) {
														error_description = "Terms cannot be tags";
														where = tokens
																.getEndIndex(subterm_start
																		.get(colon_term));
														break;
													}

													String tag_token = tokens
															.getValue(
																	subterm_start
																			.get(colon_term))
															.toLowerCase();

													if (tokens
															.isQuoted(subterm_start
																	.get(colon_term)) == true) {
														/**
														 * tag given by string
														 * literal
														 */

														if (value.isEmpty() == false) {
															value += ", ";
														}
														value += "\""
																+ StringEscape
																		.escapeToJavaScript(tag_token)
																+ "\"";
													} else {
														if (tag_token
																.equalsIgnoreCase(":") == true) {
															colon_found = true;
															break;
														}
														if (boundVariables
																.contains(tag_token)) {
															if (variableType
																	.get(tag_token) == VAR_TAG) {
																if (value
																		.isEmpty() == false) {
																	value += ", ";
																}
																value += "tVar"
																		+ tag_token;
															} else {
																error_description = "'"
																		+ tag_token
																		+ "' is a variable, but not a token variable";
																where = tokens
																		.getEndIndex(subterm_start
																				.get(colon_term));
																break;
															}
														} else {
															error_description = "'"
																	+ tag_token
																	+ "' is neither a bound token variable nor a string literal";
															where = tokens
																	.getEndIndex(subterm_start
																			.get(colon_term));
															break;
														}
													}

													colon_term++;
												}

												if (colon_found) {
													boundVariables
															.push(variable);
													variableType.put(variable,
															VAR_TAG);
													variableValue.put(variable,
															value);

													openTagVariable(variable);

													/**
													 * check RHS term
													 */

													String error = checkSyntaxTree(
															subterm_start
																	.get(colon_term) + 1,
															last);

													closeTagVariable(variable);

													boundVariables.pop();
													variableType
															.remove(variable);
													variableValue
															.remove(variable);

													return error;
												}

											} else {
												error_description = "':' or 'in' expected";
												where = tokens
														.getEndIndex(subterm_start
																.get(2));
											}
										} else {
											error_description = "':' or 'in' expected, quoted string found";
											where = tokens
													.getEndIndex(subterm_start
															.get(2));
										}
									} else {
										error_description = "':' or 'in' expected, term in parenthesis found";
										where = tokens
												.getEndIndex(subterm_start
														.get(2));
									}
								}
							}
						} else {
							error_description = "Terms cannot be variable names";
							where = tokens.getEndIndex(subterm_start.get(1));
						}
					}

				} else if (first_token.equalsIgnoreCase("field") == true) {
					/**
					 * field VARNAME : RHS
					 * 
					 * or
					 * 
					 * field VARNAME with
					 * ("tag"|TAGVARIABLE)(,("tag"|TAGVARIABLE)*) : RHS
					 */

					if (subterm_rootlevel.size() < 2) {
						error_description = "missing variable name after 'tag'";
						where = tokens.getEndIndex(first);
					} else {
						if (subterm_rootlevel.get(1) == true) {
							if (tokens.isQuoted(subterm_start.get(1)) == true) {
								error_description = "String literals cannot be variable names";
								where = tokens
										.getEndIndex(subterm_start.get(1));
							} else {
								String variable = tokens.getValue(
										subterm_start.get(1)).toLowerCase();
								if (boundVariables.contains(variable) == true) {
									error_description = "Variable symbol '"
											+ variable + "' is already taken ";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								} else if (isForbidden(variable) == true) {
									error_description = "'"
											+ variable
											+ "' connot be used as variable symbol";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								} else {
									if (subterm_rootlevel.size() < 3) {
										error_description = "':' or 'with' missing";
										where = tokens
												.getEndIndex(subterm_start
														.get(1));
									} else if (subterm_rootlevel.get(2) == true) {
										String colon_or_in = tokens
												.getValue(subterm_start.get(2));
										if (tokens.isQuoted(subterm_start
												.get(2)) == false) {
											if (colon_or_in
													.equalsIgnoreCase(":") == true) {
												/**
												 * field VARNAME : RHS
												 */
												boundVariables.push(variable);
												variableType.put(variable,
														VAR_FIELD);
												variableValue
														.put(variable,
																attributes
																		.getAcceptTagsCommas());

												openFieldVariable(variable);

												/**
												 * check RHS term
												 */

												String error = checkSyntaxTree(
														subterm_start.get(2) + 1,
														last);

												closeFieldVariable(variable);

												boundVariables.pop();
												variableType.remove(variable);
												variableValue.remove(variable);

												return error;
											} else if (colon_or_in
													.equalsIgnoreCase("with")) {
												/**
												 * check for colon
												 */
												int colon_term = 3;
												boolean colon_found = false;

												String value = attributes
														.getAcceptTagsCommas();

												while (colon_found != true) {
													if (subterm_rootlevel
															.size() <= colon_term) {
														error_description = "':' after 'field "
																+ variable
																+ " with [...]' missing";
														where = tokens
																.getEndIndex(last - 1);
														break;
													}

													if (subterm_rootlevel
															.get(colon_term) != true) {
														error_description = "Terms cannot be tags";
														where = tokens
																.getEndIndex(subterm_start
																		.get(colon_term));
														break;
													}

													String tag_token = tokens
															.getValue(
																	subterm_start
																			.get(colon_term))
															.toLowerCase();

													if (tokens
															.isQuoted(subterm_start
																	.get(colon_term)) == true) {
														/**
														 * tag given by string
														 * literal
														 */
														if (value.isEmpty() == false) {
															value += ", ";
														}
														value += "\""
																+ StringEscape
																		.escapeToJavaScript(tag_token)
																+ "\"";
													} else {
														if (tag_token
																.equalsIgnoreCase(":") == true) {
															colon_found = true;
															break;
														}
														if (boundVariables
																.contains(tag_token)) {
															if (variableType
																	.get(tag_token) == VAR_TAG) {
																if (value
																		.isEmpty() == false) {
																	value += ", ";
																}
																value += "tVar"
																		+ tag_token;
															} else {
																error_description = "'"
																		+ tag_token
																		+ "' is a variable, but not a token variable";
																where = tokens
																		.getEndIndex(subterm_start
																				.get(colon_term));
																break;
															}
														} else {
															error_description = "'"
																	+ tag_token
																	+ "' is neither a bound token variable nor a string literal";
															where = tokens
																	.getEndIndex(subterm_start
																			.get(colon_term));
															break;
														}
													}

													colon_term++;
												}

												if (colon_found) {
													boundVariables
															.push(variable);
													variableType.put(variable,
															VAR_FIELD);
													variableValue.put(variable,
															value);

													openFieldVariable(variable);

													/**
													 * check RHS term
													 */

													String error = checkSyntaxTree(
															subterm_start
																	.get(colon_term) + 1,
															last);

													closeFieldVariable(variable);

													boundVariables.pop();
													variableType
															.remove(variable);
													variableValue
															.remove(variable);

													return error;
												}

											} else {
												error_description = "':' or 'with' expected";
												where = tokens
														.getEndIndex(subterm_start
																.get(2));
											}
										} else {
											error_description = "':' or 'with' expected, quoted string found";
											where = tokens
													.getEndIndex(subterm_start
															.get(2));
										}
									} else {
										error_description = "':' or 'with' expected, term in parenthesis found";
										where = tokens
												.getEndIndex(subterm_start
														.get(2));
									}
								}
							}
						} else {
							error_description = "Terms cannot be variable names";
							where = tokens.getEndIndex(subterm_start.get(1));
						}
					}

				} else if (boundVariables.contains(first_token.toLowerCase())) {
					/**
					 * term starts with a variable symbol
					 */
					String variable = first_token.toLowerCase();
					if (variableType.get(variable) == VAR_FIELD) {

						if (subterm_rootlevel.size() != 3) {
							error_description = "Field comparision must be of the form FIELD = FIELD or FIELD = \"STRING\"";
							where = tokens.getEndIndex(first);
						} else {
							if (subterm_rootlevel.get(1) != true) {
								error_description = "'=' expected, term in parenthesis found";
								where = tokens
										.getEndIndex(subterm_start.get(1));
							} else {
								if (tokens.isQuoted(subterm_start.get(1)) == true) {
									error_description = "'=' expected, string literal found";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								} else if (tokens
										.getValue(subterm_start.get(1)).equals(
												"=")) {
									if (subterm_rootlevel.get(2) != true) {
										error_description = "String literal or field variable needed";
										where = tokens
												.getEndIndex(subterm_start
														.get(2));
									} else {
										if (tokens.isQuoted(subterm_start
												.get(2)) == true) {
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
																	.getValue(subterm_start
																			.get(2)))
													+ ") {\n goodParts.push(fVar"
													+ variable
													+ ");\n return true; } else {\n return false;\n}"
													+ "\n} else {\n return false;\n }";

											return null;
										} else {
											String comparison_variable = tokens
													.getValue(
															subterm_start
																	.get(2))
													.toLowerCase();
											if (boundVariables
													.contains(comparison_variable) == true) {
												if (variableType
														.get(comparison_variable) == VAR_FIELD) {
													/**
													 * FIELD = FIELD
													 */
													
													jsCode += " if (fVar"
															+ variable
															+ ".token) {\n"
															+ "\nif (fVar"
															+ variable
															+ ".token == "
															+ "fVar" + comparison_variable + ".token"
															+ ") {\n goodParts.push(fVar"
															+ variable
															+ ");\n return true; } else {\n return false;\n}"
															+ "\n} else {\n return false;\n }";
													
													return null;
												} else {
													error_description = "'"
															+ comparison_variable
															+ "' is variable, but not a field variable";
													where = tokens
															.getEndIndex(subterm_start
																	.get(2));
												}
											} else {
												error_description = "Field variable or string literal needed";
												where = tokens
														.getEndIndex(subterm_start
																.get(2));
											}
										}
									}
								} else {
									error_description = "'=' expected";
									where = tokens.getEndIndex(subterm_start
											.get(1));
								}
							}
						}

					} else {
						error_description = "'" + first_token
								+ "' is a variable, but not a field variable";
						where = tokens.getEndIndex(first);
					}
				}
			}
		}

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
						+ inputTerm.substring(end_index + 1, end_index + 17)
						+ "[...]";
			} else
				message += " " + inputTerm.substring(end_index + 1);
		}
		return message;
	}

}
