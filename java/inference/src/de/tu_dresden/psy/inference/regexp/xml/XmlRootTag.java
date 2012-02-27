/**
 * XmlRootTag.java, (c) 2012, Immanuel Albrecht; Dresden University of
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

package de.tu_dresden.psy.inference.regexp.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.tu_dresden.psy.inference.Assertion.AssertionPart;
import de.tu_dresden.psy.inference.AssertionInterface;
import de.tu_dresden.psy.inference.InferenceMap;
import de.tu_dresden.psy.inference.InferenceMaps;
import de.tu_dresden.psy.inference.regexp.NonEmptyIntersectionChecker;
import de.tu_dresden.psy.inference.regexp.RegExpInferenceMap;
import de.tu_dresden.psy.regexp.KRegExp;
import de.tu_dresden.psy.regexp.SplittedStringRelation;
import de.tu_dresden.psy.regexp.SplittedStringRelation.MapSplitting;
import de.tu_dresden.psy.regexp.StringRelationJoin;

import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatcher;
import de.tu_dresden.psy.regexp.SubjectPredicateObjectMatchers;

/**
 * implements a virtual root tag for xml style notation of regexp inference
 * rules, that does all processing of xml data
 * 
 * @author albrecht
 * 
 */
public class XmlRootTag extends XmlTag {

	private Set<InferenceMap> rules;
	private Set<SubjectPredicateObjectMatcher> parsers;
	private Set<String> assertions;

	public XmlRootTag() {
		rules = new HashSet<InferenceMap>();
		parsers = new HashSet<SubjectPredicateObjectMatcher>();
		assertions = new HashSet<String>();
	}

	/**
	 * 
	 * @return a combined inference map of all given inference rules
	 */

	public InferenceMaps getMaps() {
		return new InferenceMaps(rules);
	}

	/**
	 * 
	 * @return a combined parser
	 */

	public SubjectPredicateObjectMatchers getParsers() {
		return new SubjectPredicateObjectMatchers(parsers);
	}

	public Set<AssertionInterface> getGivenAssertions() {
		Set<AssertionInterface> given = new HashSet<AssertionInterface>();
		SubjectPredicateObjectMatchers matcher = getParsers();

		for (String assertion : assertions) {
			given.addAll(matcher.match(assertion));
		}

		return given;
	}

	/**
	 * process a &lt;assert>-tag
	 * 
	 * @param child
	 */

	private void processAssert(XmlTag child) {
		assertions.add(child.contents);
	}

	/**
	 * process a &lt;rule>-tag
	 * 
	 * @param child
	 */

	private void processRule(XmlTag child) {
		RegExpInferenceMap rule = new RegExpInferenceMap(
				child.getAttributeOrDefault("name", "#" + rules.size()));

		Map<String, Integer> premise_id = new HashMap<String, Integer>();
		int current_premise = 0;

		for (XmlTag tag : child.children) {
			if (tag.tagName.equals("PREMISE")) {
				processPremise(tag, rule);
				String id = tag.attributes.get("id");

				if (id != null)
					premise_id.put(id, current_premise);

				current_premise++;
			}
		}
		
		for (XmlTag tag : child.children) {
		    if (tag.tagName.equals("CONSTRAINT")) {
				processConstraint(tag, rule, premise_id);
			} else if (tag.tagName.equals("INFER")) {
				processInfer(tag, rule, premise_id);
			}
		}

		rules.add(rule);
	}

	/**
	 * process a &lt;infer>-tag within a &lt;rule>-tag
	 * 
	 * @param child
	 * @param rule
	 * @param premise_id
	 */

	private void processInfer(XmlTag child, RegExpInferenceMap rule,
			Map<String, Integer> premise_id) {
		RegExpInferenceMap.AdvancedPremiseCombinator conclusion = new RegExpInferenceMap.AdvancedPremiseCombinator(
				rule);

		for (XmlTag tag : child.children) {
			AssertionPart part = null;
			if (tag.tagName.equals("SUBJECT")) {
				part = AssertionPart.subject;
			} else if (tag.tagName.equals("PREDICATE")) {
				part = AssertionPart.predicate;
			} else if (tag.tagName.equals("OBJECT")) {
				part = AssertionPart.object;
			}

			if (part != null) {
				if (tag.attributes.containsKey("id")
						&& tag.attributes.containsKey("source")) {
					AssertionPart source_part = null;
					
					if (tag.attributes.get("source").equals("subject")) {
						source_part = AssertionPart.subject;
					} else if (tag.attributes.get("source").equals("predicate")) {
						source_part = AssertionPart.predicate;
					} else if (tag.attributes.get("source").equals("object")) {
						source_part = AssertionPart.object;
					}

					if (source_part != null) {
						/**
						 * the children of the tag form a relation
						 */

						StringRelationJoin relation = null;

						if (tag.children.isEmpty() == false) {
							relation = processRho(tag);
						}

						conclusion.addPart(part, relation,
								premise_id.get(tag.attributes.get("id")),
								source_part);
					}

				} else {
					conclusion.addConstantPart(part, tag.contents);
				}
			}
		}
		
		rule.addConclusion(conclusion);
		
	}

	/**
	 * process a &lt;premise>-tag within a &lt;rule>-tag
	 * 
	 * @param child
	 * @param rule
	 */

	private void processPremise(XmlTag child, RegExpInferenceMap rule) {
		String subject = "";
		String predicate = "";
		String object = "";

		for (XmlTag t : child.children) {
			if (t.tagName.equals("SUBJECT")) {
				subject += "(" + t.contents + ")";
			} else if (t.tagName.equals("PREDICATE")) {
				predicate += "(" + t.contents + ")";
			} else if (t.tagName.equals("OBJECT")) {
				object += "(" + t.contents + ")";
			}
		}

		if (subject.isEmpty())
			subject = ".*";

		if (object.isEmpty())
			object = ".*";

		if (predicate.isEmpty())
			predicate = ".*";

		rule.addPremiseForm(subject, predicate, object);
	}

	/**
	 * process a &lt;parse>-tag
	 * 
	 * @param child
	 */

	private void processParse(XmlTag child) {
		String subject = "";
		String predicate = "";
		String object = "";

		for (XmlTag t : child.children) {
			if (t.tagName.equals("SUBJECT")) {
				subject += "(" + t.contents + ")";
			} else if (t.tagName.equals("PREDICATE")) {
				predicate += "(" + t.contents + ")";
			} else if (t.tagName.equals("OBJECT")) {
				object += "(" + t.contents + ")";
			}
		}

		if (subject.isEmpty())
			subject = ".*";

		if (object.isEmpty())
			object = ".*";

		if (predicate.isEmpty())
			predicate = ".*";

		parsers.add(new SubjectPredicateObjectMatcher(subject, predicate,
				object));
	}

	/**
	 * process a &lt;constraint>-tag within a &lt;rule>-tag
	 * 
	 * @param child
	 * @param rule
	 * @param premise_id
	 */

	private void processConstraint(XmlTag child, RegExpInferenceMap rule,
			Map<String, Integer> premise_id) {

		NonEmptyIntersectionChecker checker = new NonEmptyIntersectionChecker();

		for (XmlTag t : child.children) {
			AssertionPart part = null;
			if (t.tagName.equals("SUBJECT")) {
				part = AssertionPart.subject;
			} else if (t.tagName.equals("PREDICATE")) {
				part = AssertionPart.predicate;
			} else if (t.tagName.equals("OBJECT")) {
				part = AssertionPart.object;
			}

			if ((part != null) && (t.attributes.containsKey("id"))) {
				if (t.children.isEmpty() == true) {
					checker.addCheckPart(
							premise_id.get(t.attributes.get("id")), part);
				} else {
					/**
					 * the children of t form a relation
					 */

					StringRelationJoin relation = processRho(t);

					checker.addCheckPart(
							premise_id.get(t.attributes.get("id")), part,
							relation);

				}
			}
		}
		
		rule.addConstraint(checker);
	}

	/**
	 * process a string join relation from &lt;rho>-tags
	 * 
	 * @param parent
	 * @return
	 */
	private StringRelationJoin processRho(XmlTag parent) {
		StringRelationJoin result = new StringRelationJoin();

		for (XmlTag rho : parent.children) {
			if (rho.tagName.equals("RHO")) {
				SplittedStringRelation relation = new SplittedStringRelation();

				Map<String, Integer> ids = new HashMap<String, Integer>();
				int input_part = 0;
				Vector<String> input_regexps = new Vector<String>();

				Vector<MapSplitting> output = new Vector<SplittedStringRelation.MapSplitting>();

				/**
				 * process <IN> tags
				 */

				for (XmlTag intags : rho.children) {
					if (intags.tagName.equals("IN")) {
						if (intags.attributes.containsKey("id")) {
							ids.put(intags.attributes.get("id"), input_part);
						}
						input_part++;
						input_regexps.add(intags.contents);
					}
				}

				/**
				 * process <OUT> tags
				 */

				for (XmlTag outtags : rho.children) {
					if (outtags.tagName.equals("OUT")) {
						if (outtags.attributes.containsKey("id")) {
							output.add(new SplittedStringRelation.ProjectionMap(
									ids.get(outtags.attributes.get("id"))));
						} else
						if (outtags.contents.isEmpty() == false) {
							output.add(new SplittedStringRelation.ConstantMap(
									outtags.contents));
						}
					}
				}

				relation.addInput(new KRegExp(input_regexps));
				relation.addOutput(output);
				result.join(relation);
			}
		}

		return result;
	}

	@Override
	public void addChild(XmlTag child) {
		if (child.tagName.equals("RULE")) {
			processRule(child);
		} else if (child.tagName.equals("PARSE")) {
			processParse(child);
		}
		else if (child.tagName.equals("ASSERT")) {
			processAssert(child);
		}

		/**
		 * ignore unknown tags
		 */
	}

	@Override
	public String toString() {

		return rules.size() + " rule(s), " + parsers.size() + " parser(s), "
				+ assertions.size() + " assertion(s)";
	}

}
