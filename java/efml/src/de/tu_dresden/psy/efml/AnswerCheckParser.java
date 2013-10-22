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

		this.levels = new ArrayList<Integer>();

		for (Iterator<AnswerCheckTokenizer.Token> it = this.tokens.iterator(); it
				.hasNext();) {
			AnswerCheckTokenizer.Token token = it.next();
			if (token.quoted == false) {
				if (token.value.equalsIgnoreCase("(")) {
					current_level += 1;
				}
			}

			this.levels.add(current_level);

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
		for (int i = 0; i < forbiddenTokens.length; ++i) {
			if (token.equalsIgnoreCase(forbiddenTokens[i]) == true) {
				return true;
				/**
				 * test whether it is alphanumeric
				 */
			}
		}

		for (int c = 0; c < token.length(); ++c) {
			if (Character.isLetterOrDigit(token.charAt(c)) == false) {
				return true;
			}
		}

		return Character.isLetter(token.charAt(0)) != true;
	}

	/**
	 * 
	 * @return corresponing javascript code
	 */
	public String getJsCode() {
		if (this.checked == false) {
			this.checkSyntaxAndParse();
		}
		if (this.error != null) {
			/**
			 * disable this check...
			 */
			return "function(){\nreturn true;\n}";
		} else {

			if (this.attributes.getValueOrDefault("type", "neutralgood")
					.equalsIgnoreCase("neutralgood")) {

				return "function(){\n var goodParts = [];"
						+ "\n var consideredParts = [];"
						+ "\n var result = (function(){\n"
						+ this.jsCode
						+ "\n})();"
						+ "if (result) {\n"
						+ " for (var i=0;i<goodParts.length;++i) {\n"
						+ " if (typeof goodParts[i].MarkAsGood != \"undefined\") {\n"
						+ "goodParts[i].MarkAsGood();\n}\n"
						+ " }\n}\nreturn result;}";
			} else {
				return "function(){\n var goodParts = [];"
						+ "\n var consideredParts = [];"
						+ "\n var quantifiedParts = [];"
						+ "\n var result = (function(){\n"
						+ this.jsCode
						+ "\n})();"
						+ "if (result) {\n"
						+ " for (var i=0;i<goodParts.length;++i) {\n"
						+ " if (typeof goodParts[i].MarkAsGood != \"undefined\") {\n"
						+ "goodParts[i].MarkAsGood();\n}\n"
						+ " }\n} else {\n"
						+ " for (var i=0;i<quantifiedParts.length;++i) {\n"
						+ "   if (typeof quantifiedParts[i].MarkAsBad != \"undefined\") {\n"
						+ "      quantifiedParts[i].MarkAsBad();\n}\n"
						+ "   }\n}"
						+"\nreturn result;}";
			}
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
		this.error = this.enterTerm(0, this.tokens.length());
		this.checked = true;

		return this.error;
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void openTagVariable(String variable) {
		if (this.variableValue.get(variable).isEmpty() == true) {
			this.jsCode += "return Exists(myTags.tags, function(tVar"
					+ variable + "){\n";
		} else {
			this.jsCode += "return Exists([" + this.variableValue.get(variable)
					+ "], function(tVar" + variable + "){\n";
		}
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void closeTagVariable(String variable) {
		this.jsCode += "});";
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void openFieldVariable(String variable) {
		if (this.attributes.getValueOrDefault("type", "neutralgood")
				.equalsIgnoreCase("neutralgood")) {
			this.jsCode += "return Exists(myTags.AllTagsBut(["
					+ this.variableValue.get(variable) + "],"
					+ this.attributes.getRejectTags() + "), function(fVar"
					+ variable + "){\n";
		} else {
			this.jsCode += "return ExistsTrack(myTags.AllTagsBut(["
					+ this.variableValue.get(variable) + "],"
					+ this.attributes.getRejectTags()
					+ "), quantifiedParts, function(fVar" + variable + "){\n";
		}
	}

	/**
	 * generate javaScript code
	 * 
	 * @param variable
	 */
	private void closeFieldVariable(String variable) {
		this.jsCode += "});";
	}

	/**
	 * generate javaScript code
	 */

	private void startAndSequence() {
		this.jsCode += "var previous_good_count = goodParts.length;\n"
				+ "var failed = false;";
		this.jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void betweenAndSequence() {
		this.jsCode += "})()) {\n" + "} else {\n" + " failed = true;\n" + "}\n";
		this.jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void endAndSequence() {
		this.jsCode += "})()) {\n" + "} else {\n" + " failed = true;\n" + "}\n";

		this.jsCode += " if (failed) {\n"
				+ "  while (goodParts.length > previous_good_count) { var considered = goodParts.pop(); "
				+ "     if (consideredParts.indexOf(considered) < 0) consideredParts.push(considered);"
				+ " }\n" + "  return null;\n" + "} else {\n"
				+ "  return true;\n" + "}";
	}

	/**
	 * generate javaScript code
	 */

	private void startOrSequence() {
		this.jsCode += "var previous_good_count = goodParts.length;\n"
				+ "var succeeded = false;";
		this.jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void betweenOrSequence() {
		this.jsCode += "})()) {\n succeeded=true;" + "}\n";
		this.jsCode += "if ((function(){\n";
	}

	/**
	 * generate javaScript code
	 */

	private void endOrSequence() {
		this.jsCode += "})()) {\n succeeded=true;" + "}\n";
		this.jsCode += " return succeeded;";
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

			this.error_description = "token utterly unrecognized";
			this.where = AnswerCheckParser.this.tokens.getEndIndex(this.first);

			/**
			 * get the local depth of the term
			 */
			this.local_depth = AnswerCheckParser.this.levels.get(this.first);
			for (int c = this.first; c < this.last; ++c) {
				if (AnswerCheckParser.this.levels.get(c) < this.local_depth) {
					this.local_depth = AnswerCheckParser.this.levels.get(c);
				}
			}

			/**
			 * create intermediate sub term structure
			 */

			this.subterm_start = new ArrayList<Integer>();
			this.subterm_end = new ArrayList<Integer>();
			this.subterm_rootlevel = new ArrayList<Boolean>();

			int start = this.first;
			boolean rootlevel = AnswerCheckParser.this.levels.get(this.first) == this.local_depth;

			for (int end = this.first + 1; end < this.last; ++end) {
				if ((AnswerCheckParser.this.levels.get(end) == this.local_depth)
						|| (rootlevel)) {
					this.subterm_start.add(start);
					this.subterm_end.add(end);
					this.subterm_rootlevel.add(rootlevel);

					rootlevel = AnswerCheckParser.this.levels.get(end) == this.local_depth;
					start = end;
				}
			}

			this.subterm_start.add(start);
			this.subterm_end.add(this.last);
			this.subterm_rootlevel.add(rootlevel);

			for (int c = 0; c < this.subterm_rootlevel.size(); ++c) {
				if (this.subterm_rootlevel.get(c) == true) {
					this.where = AnswerCheckParser.this.tokens
							.getEndIndex(this.subterm_start.get(c));
					break;
				}
			}
		}

		public String getErrorMessage() {
			/**
			 * format error message
			 */

			String message = "Syntax-Error: " + this.error_description
					+ " just before the " + (2 + this.where) + "th character: ";

			int end_index = this.where;

			if (this.where > 16) {
				message += "[...]";
			} else {
				this.where = 16;
			}

			int start_index = this.where - 16;

			message += AnswerCheckParser.this.inputTerm.substring(start_index,
					end_index + 1);

			message += " <--HERE--|";

			if ((end_index + 1) < AnswerCheckParser.this.inputTerm.length()) {

				if ((AnswerCheckParser.this.inputTerm.length() - end_index - 1) > 16) {
					message += " "
							+ AnswerCheckParser.this.inputTerm.substring(
									end_index + 1, end_index + 17) + "[...]";
				} else {
					message += " "
							+ AnswerCheckParser.this.inputTerm
							.substring(end_index + 1);
				}
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

			return this.getErrorMessage();
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
			if ((i % 2) == 0) {
				if (term.subterm_rootlevel.get(i) != false) {

					return term.errorFound("Token should be in parenthesis",
							this.tokens.getEndIndex(term.subterm_start.get(i)));
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
							this.tokens.getEndIndex(term.subterm_start.get(i)));

				} else if ("and".equalsIgnoreCase(this.tokens
						.getValue(term.subterm_start.get(i))) != true) {
					return term.errorFound(
							"Connective '"
									+ "and"
									+ "' may not be mixed with '"
									+ this.tokens.getValue(term.subterm_start
											.get(i)) + "'",
											this.tokens.getEndIndex(term.subterm_start.get(i)));

				}
			}
		}

		if ((term.subterm_rootlevel.size() % 2) == 0) {
			return term.errorFound("Term connective without right hand term",
					this.tokens.getEndIndex(term.subterm_start
							.get(term.subterm_start.size() - 1)));

		} else {
			/**
			 * and/or n-ary term well-formed, if subterms are well-formed
			 */
			this.startAndSequence();

			for (int i = 0; i < term.subterm_rootlevel.size(); i += 2) {
				if (i > 0) {

					this.betweenAndSequence();

				}

				String error = this.enterTerm(term.subterm_start.get(i),
						term.subterm_end.get(i));

				if (error != null) {
					return error;
				}
			}

			this.endAndSequence();

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
			if ((i % 2) == 0) {
				if (term.subterm_rootlevel.get(i) != false) {
					return term.errorFound("Token should be in parenthesis",
							this.tokens.getEndIndex(term.subterm_start.get(i)));

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
							this.tokens.getEndIndex(term.subterm_start.get(i)));
				} else if ("or".equalsIgnoreCase(this.tokens
						.getValue(term.subterm_start.get(i))) != true) {
					return term.errorFound(
							"Connective '"
									+ "or"
									+ "' may not be mixed with '"
									+ this.tokens.getValue(term.subterm_start
											.get(i)) + "'",
											this.tokens.getEndIndex(term.subterm_start.get(i)));

				}
			}
		}

		if ((term.subterm_rootlevel.size() % 2) == 0) {
			return term.errorFound("Term connective without right hand term",
					this.tokens.getEndIndex(term.subterm_start
							.get(term.subterm_start.size() - 1)));

		} else {
			/**
			 * and/or n-ary term well-formed, if subterms are well-formed
			 */

			this.startOrSequence();

			for (int i = 0; i < term.subterm_rootlevel.size(); i += 2) {
				if (i > 0) {

					this.betweenOrSequence();

				}

				String error = this.enterTerm(term.subterm_start.get(i),
						term.subterm_end.get(i));
				if (error != null) {
					return error;
				}
			}

			this.endOrSequence();

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
		if ((this.tokens.isQuoted(term.last - 1) == false)
				&& (this.tokens.getValue(term.last - 1).equalsIgnoreCase(")"))) {
			/**
			 * term in parenthesis is valid, if the enclosed subterm is valid
			 */
			return this.enterTerm(term.first + 1, term.last - 1);
		} else {
			return term.errorFound("Closing parenthesis is missing",
					this.tokens.getEndIndex(term.last - 1));

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
					this.tokens.getEndIndex(term.first));
		} else {
			if (term.subterm_rootlevel.get(1) == true) {
				if (this.tokens.isQuoted(term.subterm_start.get(1)) == true) {
					return term.errorFound(
							"String literals cannot be variable names",
							this.tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					String variable = this.tokens.getValue(
							term.subterm_start.get(1)).toLowerCase();
					if (this.boundVariables.contains(variable) == true) {
						return term.errorFound("Variable symbol '" + variable
								+ "' is already taken ", this.tokens
								.getEndIndex(term.subterm_start.get(1)));
					} else if (this.isForbidden(variable) == true) {
						return term
								.errorFound(
										"'"
												+ variable
												+ "' connot be used as variable symbol",
												this.tokens
												.getEndIndex(term.subterm_start
														.get(1)));
					} else {
						if (term.subterm_rootlevel.size() < 3) {
							return term.errorFound("':' or 'in' missing",
									this.tokens.getEndIndex(term.subterm_start
											.get(1)));
						} else if (term.subterm_rootlevel.get(2) == true) {
							String colon_or_in = this.tokens
									.getValue(term.subterm_start.get(2));
							if (this.tokens.isQuoted(term.subterm_start.get(2)) == false) {
								if (colon_or_in.equalsIgnoreCase(":") == true) {
									/**
									 * tag VARNAME : RHS
									 */
									this.boundVariables.push(variable);
									this.variableType.put(variable,
											this.VAR_TAG);

									this.variableValue.put(variable, "");

									this.openTagVariable(variable);

									/**
									 * check RHS term
									 */

									String error = this.enterTerm(
											term.subterm_start.get(2) + 1,
											term.last);

									this.closeTagVariable(variable);

									this.boundVariables.pop();
									this.variableType.remove(variable);
									this.variableValue.remove(variable);

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
																	this.tokens
																	.getEndIndex(term.last - 1));

										}

										if (term.subterm_rootlevel
												.get(colon_term) != true) {
											return term
													.errorFound(
															"Terms cannot be tags",
															this.tokens
															.getEndIndex(term.subterm_start
																	.get(colon_term)));

										}

										String tag_token = this.tokens
												.getValue(
														term.subterm_start
														.get(colon_term))
														.toLowerCase();

										if (this.tokens
												.isQuoted(term.subterm_start
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
											if (this.boundVariables
													.contains(tag_token)) {
												if (this.variableType
														.get(tag_token) == this.VAR_TAG) {
													if (value.isEmpty() == false) {
														value += ", ";
													}
													value += "tVar" + tag_token;
												} else {
													term.error_description = "'"
															+ tag_token
															+ "' is a variable, but not a token variable";
													term.where = this.tokens
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
																		this.tokens
																		.getEndIndex(term.subterm_start
																				.get(colon_term)));

											}
										}

										colon_term++;
									}

									if (colon_found) {
										this.boundVariables.push(variable);
										this.variableType.put(variable,
												this.VAR_TAG);
										this.variableValue.put(variable, value);

										this.openTagVariable(variable);

										/**
										 * check RHS term
										 */

										String error = this.enterTerm(
												term.subterm_start
												.get(colon_term) + 1,
												term.last);

										this.closeTagVariable(variable);

										this.boundVariables.pop();
										this.variableType.remove(variable);
										this.variableValue.remove(variable);

										return error;
									}

								} else {
									return term
											.errorFound(
													"':' or 'in' expected",
													this.tokens
													.getEndIndex(term.subterm_start
															.get(2)));
								}
							} else {
								return term
										.errorFound(
												"':' or 'in' expected, quoted string found",
												this.tokens
												.getEndIndex(term.subterm_start
														.get(2)));
							}
						} else {
							return term
									.errorFound(
											"':' or 'in' expected, term in parenthesis found",
											this.tokens
											.getEndIndex(term.subterm_start
													.get(2)));
						}
					}
				}
			} else {
				return term.errorFound("Terms cannot be variable names",
						this.tokens.getEndIndex(term.subterm_start.get(1)));
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
					this.tokens.getEndIndex(term.first));
		} else {
			if (term.subterm_rootlevel.get(1) == true) {
				if (this.tokens.isQuoted(term.subterm_start.get(1)) == true) {
					return term.errorFound(
							"String literals cannot be variable names",
							this.tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					String variable = this.tokens.getValue(
							term.subterm_start.get(1)).toLowerCase();
					if (this.boundVariables.contains(variable) == true) {
						return term.errorFound("Variable symbol '" + variable
								+ "' is already taken ", this.tokens
								.getEndIndex(term.subterm_start.get(1)));
					} else if (this.isForbidden(variable) == true) {
						return term
								.errorFound(
										"'"
												+ variable
												+ "' connot be used as variable symbol",
												this.tokens
												.getEndIndex(term.subterm_start
														.get(1)));
					} else {
						if (term.subterm_rootlevel.size() < 3) {
							return term.errorFound("':' or 'with' missing",
									this.tokens.getEndIndex(term.subterm_start
											.get(1)));
						} else if (term.subterm_rootlevel.get(2) == true) {
							String colon_or_in = this.tokens
									.getValue(term.subterm_start.get(2));
							if (this.tokens.isQuoted(term.subterm_start.get(2)) == false) {
								if (colon_or_in.equalsIgnoreCase(":") == true) {
									/**
									 * field VARNAME : RHS
									 */
									this.boundVariables.push(variable);
									this.variableType.put(variable,
											this.VAR_FIELD);
									this.variableValue.put(variable,
											this.attributes
											.getAcceptTagsCommas());

									this.openFieldVariable(variable);

									/**
									 * check RHS term
									 */

									String error = this.enterTerm(
											term.subterm_start.get(2) + 1,
											term.last);

									this.closeFieldVariable(variable);

									this.boundVariables.pop();
									this.variableType.remove(variable);
									this.variableValue.remove(variable);

									return error;
								} else if (colon_or_in.equalsIgnoreCase("with")) {
									/**
									 * check for colon
									 */
									int colon_term = 3;
									boolean colon_found = false;

									String value = this.attributes
											.getAcceptTagsCommas();

									while (colon_found != true) {
										if (term.subterm_rootlevel.size() <= colon_term) {
											return term
													.errorFound(
															"':' after 'field "
																	+ variable
																	+ " with [...]' missing",
																	this.tokens
																	.getEndIndex(term.last - 1));

										}

										if (term.subterm_rootlevel
												.get(colon_term) != true) {

											return term
													.errorFound(
															"Terms cannot be tags",
															this.tokens
															.getEndIndex(term.subterm_start
																	.get(colon_term)));

										}

										String tag_token = this.tokens
												.getValue(
														term.subterm_start
														.get(colon_term))
														.toLowerCase();

										if (this.tokens
												.isQuoted(term.subterm_start
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
											if (this.boundVariables
													.contains(tag_token)) {
												if (this.variableType
														.get(tag_token) == this.VAR_TAG) {
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
																			this.tokens
																			.getEndIndex(term.subterm_start
																					.get(colon_term)));

												}
											} else {

												return term
														.errorFound(
																"'"
																		+ tag_token
																		+ "' is neither a bound token variable nor a string literal",
																		this.tokens
																		.getEndIndex(term.subterm_start
																				.get(colon_term)));

											}
										}

										colon_term++;
									}

									if (colon_found) {
										this.boundVariables.push(variable);
										this.variableType.put(variable,
												this.VAR_FIELD);
										this.variableValue.put(variable, value);

										this.openFieldVariable(variable);

										/**
										 * check RHS term
										 */

										String error = this.enterTerm(
												term.subterm_start
												.get(colon_term) + 1,
												term.last);

										this.closeFieldVariable(variable);

										this.boundVariables.pop();
										this.variableType.remove(variable);
										this.variableValue.remove(variable);

										return error;
									}

								} else {
									return term
											.errorFound(
													"':' or 'with' expected",
													this.tokens
													.getEndIndex(term.subterm_start
															.get(2)));
								}
							} else {
								return term
										.errorFound(
												"':' or 'with' expected, quoted string found",
												this.tokens
												.getEndIndex(term.subterm_start
														.get(2)));
							}
						} else {
							return term
									.errorFound(
											"':' or 'with' expected, term in parenthesis found",
											this.tokens
											.getEndIndex(term.subterm_start
													.get(2)));
						}
					}
				}
			} else {
				return term.errorFound("Terms cannot be variable names",
						this.tokens.getEndIndex(term.subterm_start.get(1)));
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

		String first_token = this.tokens.getValue(term.first);

		/**
		 * term starts with a variable symbol
		 */
		String variable = first_token.toLowerCase();
		if (this.variableType.get(variable) == this.VAR_FIELD) {

			if (term.subterm_rootlevel.size() != 3) {
				return term
						.errorFound(
								"Field comparision must be of the form FIELD = FIELD or FIELD = \"STRING\"",
								this.tokens.getEndIndex(term.first));
			} else {
				if (term.subterm_rootlevel.get(1) != true) {
					return term.errorFound(
							"'=' expected, term in parenthesis found",
							this.tokens.getEndIndex(term.subterm_start.get(1)));
				} else {
					if (this.tokens.isQuoted(term.subterm_start.get(1)) == true) {
						return term.errorFound(
								"'=' expected, string literal found",
								this.tokens.getEndIndex(term.subterm_start
										.get(1)));
					} else if (this.tokens.getValue(term.subterm_start.get(1))
							.equals("=")) {
						if (term.subterm_rootlevel.get(2) != true) {
							return term.errorFound(
									"String literal or field variable needed",
									this.tokens.getEndIndex(term.subterm_start
											.get(2)));
						} else {
							if (this.tokens.isQuoted(term.subterm_start.get(2)) == true) {
								/**
								 * FIELD = "STRING"
								 */

								this.jsCode += " if (fVar"
										+ variable
										+ ".token) {\n"
										+ "\nif (fVar"
										+ variable
										+ ".token == "
										+ StringEscape
										.escapeToDecodeInJavaScript(this.tokens
												.getValue(term.subterm_start
														.get(2)))
														+ ") {\n goodParts.push(fVar"
														+ variable
														+ ");\n return true; } else {\n return false;\n}"
														+ "\n} else {\n return false;\n }";

								return null;
							} else {
								String comparison_variable = this.tokens
										.getValue(term.subterm_start.get(2))
										.toLowerCase();
								if (this.boundVariables
										.contains(comparison_variable) == true) {
									if (this.variableType
											.get(comparison_variable) == this.VAR_FIELD) {
										/**
										 * FIELD = FIELD
										 */

										this.jsCode += " if (fVar"
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
																this.tokens
																.getEndIndex(term.subterm_start
																		.get(2)));
									}
								} else {
									return term
											.errorFound(
													"Field variable or string literal needed",
													this.tokens
													.getEndIndex(term.subterm_start
															.get(2)));
								}
							}
						}
					} else {
						return term.errorFound("'=' expected", this.tokens
								.getEndIndex(term.subterm_start.get(1)));
					}
				}
			}

		} else {
			return term.errorFound("'" + first_token
					+ "' is a variable, but not a field variable",
					this.tokens.getEndIndex(term.first));
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

			this.jsCode += "return true;";

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

			String middle_token = this.tokens.getValue(term.subterm_start
					.get(1));

			if (middle_token.equalsIgnoreCase("and") == true) {
				/**
				 * n-ary "and" operator
				 */

				return this.enterAnd(term);

			} else if (middle_token.equalsIgnoreCase("or") == true) {
				/**
				 * n-ary "or" operator
				 */

				return this.enterOr(term);
			} else {
				return term.errorFound("Term connective unknown",
						this.tokens.getEndIndex(term.subterm_start.get(1)));
			}

		} else {
			/**
			 * term begins with a token
			 */
			if (this.tokens.isQuoted(term.first) == true) {
				/**
				 * term begins with string literal
				 */
				return term.errorFound(
						"A term may not start with a string literal",
						this.tokens.getEndIndex(term.first));

			} else {
				String first_token = this.tokens.getValue(term.first);

				if (first_token.equalsIgnoreCase("(") == true) {
					return this.enterSubterm(term);
				} else if ((first_token.equalsIgnoreCase("or") == true)
						|| (first_token.equalsIgnoreCase("and") == true)) {
					return term.errorFound(
							"Term connective without left hand term",
							this.tokens.getEndIndex(term.first));

				} else if (first_token.equalsIgnoreCase(")") == true) {
					return term.errorFound(
							"Two terms in parenthesis without term connective",
							this.tokens.getEndIndex(term.first));
				} else if (first_token.equalsIgnoreCase("tag") == true) {
					return this.enterExistsTag(term);
				} else if (first_token.equalsIgnoreCase("field") == true) {
					return this.enterExistsField(term);
				} else if (this.boundVariables.contains(first_token
						.toLowerCase())) {
					return this.enterCheckValue(term);
				}
			}
		}

		return term.getErrorMessage();
	}

}
