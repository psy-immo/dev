/**
 * EfmlToHtmlHandler.java, (c) 2011, Immanuel Albrecht; Dresden University of
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

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

import javax.naming.OperationNotSupportedException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class takes efml (xml) input from sax and generates intermediate
 * structures that can be rendered to html.
 * 
 * @author immanuel
 * 
 */

public class EfmlToHtmlHandler extends DefaultHandler {

	/**
	 * This stores the tags of the head and body part of the html file
	 */

	private EfmlTagsAttribute root;

	/**
	 * stores the head
	 */

	private HeadTag head;

	/**
	 * stores the body
	 */

	private BodyTag body;

	/**
	 * keep track of currently opened tags
	 */
	private Stack<EfmlTagsAttribute> currentTags;

	private Stack<AnyTag> processingTags;

	/**
	 * interface for constructing other classes
	 */

	static interface TagObjectConstructor {
		/**
		 * 
		 * 
		 * @return a new tag-handling object
		 */
		AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body);
	};

	/**
	 * X() constructor
	 */

	static class SimpleObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		public SimpleObjectConstructor(Class t) {
			this.t = t;
		}

		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.newInstance();
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			}
			return null;
		}

	};

	/**
	 * X(EfmlTagsAttribute attributes) constructor
	 */

	static class AttributeObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { EfmlTagsAttribute.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public AttributeObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(tags);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(EfmlTagsAttribute attributes, BodyTag body) constructor
	 */

	static class AttributeBodyObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { EfmlTagsAttribute.class,
				BodyTag.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public AttributeBodyObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(tags,
						body);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(AnyTag parent) constructor
	 */

	static class ParentObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings({ "rawtypes" })
		private static Class[] parameters = { AnyTag.class };

		@SuppressWarnings("rawtypes")
		public ParentObjectConstructor(Class t) {
			this.t = t;

			try {
				@SuppressWarnings({ "unchecked", "unused" })
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters)
						.newInstance(parent);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * X(BodyTag body) constructor
	 */

	static class BodyObjectConstructor implements TagObjectConstructor {

		/**
		 * create new instances of this class
		 */
		@SuppressWarnings("rawtypes")
		private Class t;

		@SuppressWarnings("rawtypes")
		private static Class[] parameters = { BodyTag.class };

		@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
		public BodyObjectConstructor(Class t) {
			this.t = t;

			try {
				Object cons = t.getConstructor(parameters);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {

				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public AnyTag New(EfmlTagsAttribute tags, AnyTag parent, BodyTag body) {
			try {
				return (AnyTag) t.getConstructor(parameters).newInstance(body);
			} catch (InstantiationException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			}

			return null;
		}

	};

	/**
	 * tag name/handler class list
	 * 
	 */

	private static Object[] tagClasses = { "title",
			new AttributeObjectConstructor(TitleTag.class), "tags",
			new AttributeObjectConstructor(TagsTag.class), "efml",
			new AttributeObjectConstructor(TagsTag.class), "tie",
			new AttributeObjectConstructor(TieTag.class), "tables",
			new AttributeObjectConstructor(TablesTag.class), "r",
			new SimpleObjectConstructor(RTag.class), "c",
			new ParentObjectConstructor(CTag.class), "runway",
			new AttributeObjectConstructor(RunwayTag.class), "answer",
			new AttributeObjectConstructor(AnswerTag.class), "hint",
			new AttributeObjectConstructor(HintTag.class), "correct",
			new SimpleObjectConstructor(CorrectTag.class), "needjustification",
			new SimpleObjectConstructor(NeedJustificationTag.class),
			"incomplete", new SimpleObjectConstructor(IncompleteTag.class),
			"incorrect", new SimpleObjectConstructor(IncorrectTag.class),
			"check", new AttributeObjectConstructor(CheckTag.class),
			"includepreamble",
			new SimpleObjectConstructor(IncludePreambleTag.class),
			"includeaddendum",
			new SimpleObjectConstructor(IncludeAddendumTag.class), "label",
			new SimpleObjectConstructor(LabelTag.class), "unread",
			new SimpleObjectConstructor(UnreadTag.class), "unused",
			new SimpleObjectConstructor(UnusedTag.class), "instructions",
			new SimpleObjectConstructor(InstructionsTag.class), "sniffy",
			new SimpleObjectConstructor(SniffyTag.class), "waitfor",
			new SimpleObjectConstructor(WaitForTag.class), "dropdown",
			new AttributeObjectConstructor(DropdownTag.class), "radiobutton",
			new AttributeObjectConstructor(RadiobuttonTag.class), "checkbox",
			new AttributeObjectConstructor(CheckboxTag.class), "popuphelp",
			new AttributeObjectConstructor(PopupHelpTag.class), "freetext",
			new AttributeObjectConstructor(FreetextTag.class), "multiline",
			new AttributeObjectConstructor(MultilineTag.class), "option",
			new AttributeObjectConstructor(OptionTag.class), "studyid",
			new BodyObjectConstructor(StudyIdTag.class), "documentid",
			new BodyObjectConstructor(DocumentIdTag.class), "jsurl",
			new BodyObjectConstructor(JsUrlTag.class), "phpurl",
			new BodyObjectConstructor(PhpUrlTag.class), "subjectinfo",
			new BodyObjectConstructor(SubjectInfoTag.class), "subjectprompt",
			new BodyObjectConstructor(SubjectPromptTag.class), "subjectchange",
			new BodyObjectConstructor(SubjectChangeTag.class), "plain",
			new SimpleObjectConstructor(PlainTag.class), "template",
			new AttributeObjectConstructor(TemplateTag.class), "airport",
			new AttributeObjectConstructor(AirportTag.class), "feedback",
			new SimpleObjectConstructor(FeedbackTag.class), "required",
			new AttributeObjectConstructor(RequiredTag.class), "parse",
			new AttributeObjectConstructor(InferenceXmlTag.class), "subject",
			new AttributeObjectConstructor(InferenceXmlTag.class), "object",
			new AttributeObjectConstructor(InferenceXmlTag.class), "predicate",
			new AttributeObjectConstructor(InferenceXmlTag.class), "expert",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"conclusion",
			new AttributeObjectConstructor(InferenceXmlTag.class), "premise",
			new AttributeObjectConstructor(InferenceXmlTag.class), "infer",
			new AttributeObjectConstructor(InferenceXmlTag.class), "in",
			new AttributeObjectConstructor(InferenceXmlTag.class), "assert",
			new AttributeObjectConstructor(InferenceXmlTag.class), "implicit",
			new AttributeObjectConstructor(InferenceXmlTag.class), "rule",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"constraint",
			new AttributeObjectConstructor(InferenceXmlTag.class), "rho",
			new AttributeObjectConstructor(InferenceXmlTag.class), "out",
			new AttributeObjectConstructor(InferenceXmlTag.class), "justified",
			new AttributeObjectConstructor(InferenceXmlTag.class), "trivial",
			new AttributeObjectConstructor(InferenceXmlTag.class), "invalid",
			new AttributeObjectConstructor(InferenceXmlTag.class), "quality",
			new AttributeObjectConstructor(InferenceXmlTag.class),
			"conclusions",
			new AttributeObjectConstructor(InferenceXmlTag.class), "solves",
			new AttributeObjectConstructor(InferenceXmlTag.class), "inference",
			new AttributeObjectConstructor(InferenceTag.class), "trashcan",
			new AttributeObjectConstructor(TrashcanTag.class), "boxspace",
			new AttributeObjectConstructor(BoxspaceTag.class), "floatbox",
			new AttributeObjectConstructor(FloatboxTag.class), "arrow",
			new AttributeObjectConstructor(ArrowTag.class), "efmlboard",
			new AttributeObjectConstructor(EfmlBoardTag.class), "efmlpreview",
			new AttributeBodyObjectConstructor(EfmlPreviewButtonTag.class),
			"efmlquote", new AttributeObjectConstructor(EfmlQuoteTag.class) };

	public EfmlToHtmlHandler() {
		this.currentTags = new Stack<EfmlTagsAttribute>();
		this.root = new EfmlTagsAttribute("", null, null);
		this.head = new HeadTag();
		this.body = new BodyTag();
		this.processingTags = new Stack<AnyTag>();

		this.currentTags.push(this.root);
		this.processingTags.push(body);
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the head tag of the
	 *         html file
	 */

	public HeadTag getHead() {
		return head;
	}

	/**
	 * 
	 * @return all Html tags that should be placed inside the body tag of the
	 *         html file
	 */
	public BodyTag getBody() {
		return body;
	}

	/**
	 * element starts, push it on the stack
	 */

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		this.currentTags.push(new EfmlTagsAttribute(qName, attributes,
				this.currentTags.peek()));

		boolean found = false;

		for (int idx = 0; idx < tagClasses.length; idx += 2) {
			String name = (String) tagClasses[idx];
			TagObjectConstructor cons = (TagObjectConstructor) tagClasses[idx + 1];

			if (qName.equals(name)) {

				this.processingTags.push(cons.New(this.currentTags.peek(),
						this.processingTags.peek(), body));
				found = true;
				break;
			}

		}

		if (found == false) {
			/**
			 * the tag is not recognized and thus we use the unknown tag handler
			 */

			this.processingTags.push(new UnknownTag(this.currentTags.peek()));

		}
	}

	/**
	 * element ends, add tags to html output
	 */

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		this.currentTags.pop();
		AnyTag closing_tag = this.processingTags.pop();

		try {
			if (closing_tag instanceof TitleTag) {
				this.head.encloseTag(closing_tag);
			} else
				this.processingTags.peek().encloseTag(closing_tag);
		} catch (OperationNotSupportedException e) {

			e.printStackTrace();
		}
	}

	/**
	 * receive data
	 */

	public void characters(char ch[], int start, int length)
			throws SAXException {
		try {
			this.processingTags.peek().encloseTag(
					new PlainContent(new String(ch, start, length)));
		} catch (OperationNotSupportedException e) {

			e.printStackTrace();
		}
	}

}
