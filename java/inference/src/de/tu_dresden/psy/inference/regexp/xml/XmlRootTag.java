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
import de.tu_dresden.psy.inference.regexp.ConstrainedAssertionFilter;
import de.tu_dresden.psy.inference.regexp.NegationOfConstraint;
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

	/**
	 * all given inference rules
	 */
	private Set<InferenceMap> rules;

	/**
	 * given trivial inference rules
	 */
	private Set<InferenceMap> trivialRules;

	/**
	 * all given parsers
	 */
	private Set<SubjectPredicateObjectMatcher> parsers;
	/**
	 * all given student assertions
	 */
	private Set<String> assertions;
	/**
	 * all filters for invalid assertions (self-contradictory etc.)
	 */
	private Set<ConstrainedAssertionFilter> invalid;
	/**
	 * all filters for trivial assertions (e.g. A means A)
	 */
	private Set<ConstrainedAssertionFilter> trivial;

	/**
	 * all filters for concluding assertions
	 */
	private Set<ConstrainedAssertionFilter> concludingFilter;

	/**
	 * all filters for assertions, that do not need further justification
	 */
	private Set<ConstrainedAssertionFilter> justified;
	/**
	 * all implicit assertions (e.g. assertions that do not need to be given
	 * explicitly)
	 */
	private Set<String> implicit;
	/**
	 * all given expert assertions (all assertions needed to generate all valid
	 * assertions in context)
	 */
	private Set<String> expert;

	/**
	 * all given conclusions
	 */
	private Set<String> conclusionAssertionsGiven;
	/**
	 * quality filters for lacking assertions in rationales (e.g. assertions
	 * that do not need to be given explicitly)
	 */
	private Map<String, ConstrainedAssertionFilter> lackQualities;

	/**
	 * filters that identify different parts of the (compound) solution
	 */
	private Map<ConstrainedAssertionFilter, String> solutionParts;

	public XmlRootTag() {
		this.rules = new HashSet<InferenceMap>();
		this.trivialRules = new HashSet<InferenceMap>();
		this.parsers = new HashSet<SubjectPredicateObjectMatcher>();
		this.assertions = new HashSet<String>();
		this.invalid = new HashSet<ConstrainedAssertionFilter>();
		this.trivial = new HashSet<ConstrainedAssertionFilter>();
		this.justified = new HashSet<ConstrainedAssertionFilter>();
		this.implicit = new HashSet<String>();
		this.expert = new HashSet<String>();
		this.conclusionAssertionsGiven = new HashSet<String>();

		this.concludingFilter = new HashSet<ConstrainedAssertionFilter>();
		this.lackQualities = new HashMap<String, ConstrainedAssertionFilter>();
		this.solutionParts = new HashMap<ConstrainedAssertionFilter, String>();
	}

	/**
	 * 
	 * @return all inference maps by their name
	 */
	public Map<String, InferenceMap> getInferenceMapsByName() {
		Map<String, InferenceMap> by_name = new HashMap<String, InferenceMap>();

		for (InferenceMap map : this.rules) {
			if (by_name.containsKey(map.ruleName())) {
				int c = 1;
				while (by_name.containsKey(map.ruleName() + " (" + c + ")")) {
					++c;
				}
				by_name.put(map.ruleName() + " (" + c + ")", map);
			} else {
				by_name.put(map.ruleName(), map);
			}
		}

		return by_name;
	}

	/**
	 * 
	 * @return trivial inference maps by their name
	 * 
	 *         this is a subset of the inference rules returned by
	 *         getInferenceMapsByName
	 */
	public Map<String, InferenceMap> getTrivialInferenceMapsByName() {
		Map<String, InferenceMap> by_name = new HashMap<String, InferenceMap>();

		for (InferenceMap map : this.trivialRules) {
			if (by_name.containsKey(map.ruleName())) {
				int c = 1;
				while (by_name.containsKey(map.ruleName() + " (" + c + ")")) {
					++c;
				}
				by_name.put(map.ruleName() + " (" + c + ")", map);
			} else {
				by_name.put(map.ruleName(), map);
			}
		}

		return by_name;
	}

	/**
	 * 
	 * @return a set of filters that filter out invalid assertions
	 */

	public Set<ConstrainedAssertionFilter> getInvalidityFilters() {
		return this.invalid;
	}

	/**
	 * 
	 * @return a set of filters that filter out trivial assertions
	 */

	public Set<ConstrainedAssertionFilter> getTrivialityFilters() {
		return this.trivial;
	}

	/**
	 * 
	 * @return a set of filters that filter out trivial assertions
	 */

	public Set<ConstrainedAssertionFilter> getJustifiedFilters() {
		return this.justified;
	}

	/**
	 * 
	 * @return a set of filters that filter out assertions that are considered
	 *         to be conclusions
	 */

	public Set<ConstrainedAssertionFilter> getConclusionFilters() {
		return this.concludingFilter;
	}

	/**
	 * 
	 * @return a set of qualities
	 */

	public Map<String, ConstrainedAssertionFilter> getQualityFilters() {
		return this.lackQualities;
	}

	/**
	 * 
	 * @return filters for solution parts
	 */

	public Map<ConstrainedAssertionFilter, String> getPartFilters() {
		return this.solutionParts;
	}

	/**
	 * 
	 * @return a combined inference map of all given inference rules
	 */

	public InferenceMaps getMaps() {
		return new InferenceMaps(this.rules);
	}

	/**
	 * 
	 * @return a combined inference map of all given trivial inference rules
	 */

	public InferenceMaps getTrivialMaps() {
		return new InferenceMaps(this.trivialRules);
	}

	/**
	 * 
	 * @return a combined parser
	 */

	public SubjectPredicateObjectMatchers getParsers() {
		return new SubjectPredicateObjectMatchers(this.parsers);
	}

	/**
	 * 
	 * @return all assertions given in the xml document
	 */
	public Set<AssertionInterface> getGivenAssertions() {
		Set<AssertionInterface> given = new HashSet<AssertionInterface>();
		SubjectPredicateObjectMatchers matcher = this.getParsers();

		for (String assertion : this.assertions) {
			given.addAll(matcher.match(assertion));
		}

		return given;
	}

	/**
	 * 
	 * @return all assertions given in the xml document
	 */
	public Set<AssertionInterface> getGivenConclusions() {
		Set<AssertionInterface> given = new HashSet<AssertionInterface>();
		SubjectPredicateObjectMatchers matcher = this.getParsers();

		for (String assertion : this.conclusionAssertionsGiven) {
			given.addAll(matcher.match(assertion));
		}

		return given;
	}

	/**
	 * 
	 * @return all expert assertions given in the xml document
	 */
	public Set<AssertionInterface> getExpertAssertions() {
		Set<AssertionInterface> given = new HashSet<AssertionInterface>();
		SubjectPredicateObjectMatchers matcher = this.getParsers();

		for (String assertion : this.expert) {
			given.addAll(matcher.match(assertion));
		}

		return given;
	}

	/**
	 * 
	 * @return all expert assertions given in the xml document
	 */
	public Set<AssertionInterface> getImplicitAssertions() {
		Set<AssertionInterface> given = new HashSet<AssertionInterface>();
		SubjectPredicateObjectMatchers matcher = this.getParsers();

		for (String assertion : this.implicit) {
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
		this.assertions.add(child.contents);
	}

	/**
	 * process a &lt;implicit>-tag
	 * 
	 * @param child
	 */

	private void processImplicit(XmlTag child) {
		this.implicit.add(child.contents);
	}

	/**
	 * process a &lt;conclusion>-tag
	 * 
	 * @param child
	 */

	private void processConclusion(XmlTag child) {
		this.conclusionAssertionsGiven.add(child.contents);
	}

	/**
	 * process a &lt;expert>-tag
	 * 
	 * @param child
	 */

	private void processExpert(XmlTag child) {
		this.expert.add(child.contents);
	}

	/**
	 * process a &lt;trivial>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processTrivial(XmlTag child) throws Exception {
		this.trivial.add(this.processConstraintFilter(child));
	}

	/**
	 * process a &lt;invalid>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processInvalid(XmlTag child) throws Exception {
		this.invalid.add(this.processConstraintFilter(child));
	}

	/**
	 * process a &lt;justified>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processJustified(XmlTag child) throws Exception {
		this.justified.add(this.processConstraintFilter(child));
	}

	/**
	 * process a &lt;conclusions>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processConclusions(XmlTag child) throws Exception {
		this.concludingFilter.add(this.processConstraintFilter(child));
	}

	/**
	 * process a &lt;quality>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processQuality(XmlTag child) throws Exception {
		String name = child.getAttributeOrDefault("name", "Q"
				+ this.lackQualities.size());
		this.lackQualities.put(name, this.processConstraintFilter(child));
	}

	/**
	 * process a &lt;solves>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processSolves(XmlTag child) throws Exception {
		String name = child.getAttributeOrDefault("name", "P"
				+ this.solutionParts.size());
		this.solutionParts.put(this.processConstraintFilter(child), name);
	}

	/**
	 * process a constraint for &lt;trivial>- and &lt;invalid>-tags
	 * 
	 * @param child
	 * @return constraint described by child
	 * @throws Exception
	 */

	private NonEmptyIntersectionChecker processConstraint(XmlTag child)
			throws Exception {
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

			if (part != null) {
				if (t.children.isEmpty() == true) {
					checker.addCheckPart(0, part);
				} else {
					/**
					 * the children of t form a relation
					 */

					StringRelationJoin relation = this.processRho(t);

					checker.addCheckPart(0, part, relation);

				}
			}
		}

		return checker;
	}

	/**
	 * process a &lt;trivial>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private ConstrainedAssertionFilter processConstraintFilter(XmlTag child)
			throws Exception {
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

		if (subject.isEmpty()) {
			subject = ".*";
		}

		if (object.isEmpty()) {
			object = ".*";
		}

		if (predicate.isEmpty()) {
			predicate = ".*";
		}

		ConstrainedAssertionFilter filter = new ConstrainedAssertionFilter(
				subject, predicate, object);

		for (XmlTag t : child.children) {
			if (t.tagName.equals("CONSTRAINT")) {
				filter.addConstraint(this.processConstraint(t));
			}
		}

		return filter;
	}

	/**
	 * process a &lt;rule>-tag
	 * 
	 * @param child
	 * @throws Exception
	 */

	private void processRule(XmlTag child) throws Exception {
		RegExpInferenceMap rule = new RegExpInferenceMap(
				child.getAttributeOrDefault("name", "#" + this.rules.size()));

		boolean is_trivial = "trivial".equalsIgnoreCase(child
				.getAttributeOrDefault("type", "non-trivial"));

		Map<String, Integer> premise_id = new HashMap<String, Integer>();
		int current_premise = 0;

		for (XmlTag tag : child.children) {
			if (tag.tagName.equals("PREMISE")) {
				this.processPremise(tag, rule);
				String id = tag.attributes.get("id");

				if (id != null) {
					premise_id.put(id, current_premise);
				}

				current_premise++;
			}
		}

		for (XmlTag tag : child.children) {
			if (tag.tagName.equals("CONSTRAINT")) {
				this.processConstraint(tag, rule, premise_id);
			} else if (tag.tagName.equals("INFER")) {
				this.processInfer(tag, rule, premise_id);
			}
		}

		this.rules.add(rule);

		if (is_trivial) {
			this.trivialRules.add(rule);
		}
	}

	/**
	 * process a &lt;infer>-tag within a &lt;rule>-tag
	 * 
	 * @param child
	 * @param rule
	 * @param premise_id
	 * @throws Exception
	 */

	private void processInfer(XmlTag child, RegExpInferenceMap rule,
			Map<String, Integer> premise_id) throws Exception {
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
							relation = this.processRho(tag);
						}

						if (premise_id.containsKey((tag.attributes.get("id"))) == false) {
							throw new Exception(
									"Inference rule refers to unknown premise \""
											+ (tag.attributes.get("id"))
											+ "\".");
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

		if (subject.isEmpty()) {
			subject = ".*";
		}

		if (object.isEmpty()) {
			object = ".*";
		}

		if (predicate.isEmpty()) {
			predicate = ".*";
		}

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

		if (subject.isEmpty()) {
			subject = ".*";
		}

		if (object.isEmpty()) {
			object = ".*";
		}

		if (predicate.isEmpty()) {
			predicate = ".*";
		}

		this.parsers.add(new SubjectPredicateObjectMatcher(subject, predicate,
				object));
	}

	/**
	 * process a &lt;constraint>-tag within a &lt;rule>-tag
	 * 
	 * @param child
	 * @param rule
	 * @param premise_id
	 * @throws Exception
	 */

	private void processConstraint(XmlTag child, RegExpInferenceMap rule,
			Map<String, Integer> premise_id) throws Exception {

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

					if (premise_id.containsKey((t.attributes.get("id"))) == false) {
						throw new Exception(
								"Constraint refers to unknown premise \""
										+ (t.attributes.get("id")) + "\".");
					}

					checker.addCheckPart(
							premise_id.get(t.attributes.get("id")), part);
				} else {
					/**
					 * the children of t form a relation
					 */

					StringRelationJoin relation = this.processRho(t);

					if (premise_id.containsKey((t.attributes.get("id"))) == false) {
						throw new Exception(
								"Constraint refers to unknown premise \""
										+ (t.attributes.get("id")) + "\".");
					}

					checker.addCheckPart(
							premise_id.get(t.attributes.get("id")), part,
							relation);

				}
			}
		}

		/**
		 * now we check whether the type is equals or differs, where equals is
		 * default and checks for non-empty intersection; and differs checks for
		 * empty intersection
		 */

		if (child.getAttributeOrDefault("type", "equals").equalsIgnoreCase(
				"equals")) {
			rule.addConstraint(checker);
		} else {
			rule.addConstraint(new NegationOfConstraint(checker));
		}


	}

	/**
	 * process a string join relation from &lt;rho>-tags
	 * 
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	private StringRelationJoin processRho(XmlTag parent) throws Exception {
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

							if (ids.containsKey((outtags.attributes.get("id"))) == false) {
								throw new Exception(
										"Rho output rule refers to unknown input part \""
												+ (outtags.attributes.get("id"))
												+ "\".");
							}

							output.add(new SplittedStringRelation.ProjectionMap(
									ids.get(outtags.attributes.get("id"))));
						} else if (outtags.contents.isEmpty() == false) {
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
	public void addChild(XmlTag child) throws Exception {
		if (child.tagName.equals("RULE")) {
			this.processRule(child);
		} else if (child.tagName.equals("PARSE")) {
			this.processParse(child);
		} else if (child.tagName.equals("ASSERT")) {
			this.processAssert(child);
		} else if (child.tagName.equals("TRIVIAL")) {
			this.processTrivial(child);
		} else if (child.tagName.equals("INVALID")) {
			this.processInvalid(child);
		} else if (child.tagName.equals("IMPLICIT")) {
			this.processImplicit(child);
		} else if (child.tagName.equals("EXPERT")) {
			this.processExpert(child);
		} else if (child.tagName.equals("CONCLUSION")) {
			this.processConclusion(child);
		} else if (child.tagName.equals("CONCLUSIONS")) {
			this.processConclusions(child);
		} else if (child.tagName.equals("JUSTIFIED")) {
			this.processJustified(child);
		} else if (child.tagName.equals("QUALITY")) {
			this.processQuality(child);
		} else if (child.tagName.equals("SOLVES")) {
			this.processSolves(child);
		}

		/**
		 * ignore unknown tags
		 */
	}

	@Override
	public String toString() {

		return this.rules.size() + " rule(s), " + this.parsers.size()
				+ " parser(s), " + this.assertions.size() + " assertion(s), "
				+ this.trivial.size() + " triviality filter(s), "
				+ this.invalid.size() + " invalidity filter(s), "
				+ this.implicit.size() + " implicit assertion(s), "
				+ this.expert.size() + " expert assertion(s), "
				+ this.justified.size() + " justified-assertion filter(s)";
	}

}
