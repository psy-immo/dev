package de.tu_dresden.psy.efml;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import de.tu_dresden.psy.inference.compiler.EmbeddedInferenceXmlTag;

/**
 * implements the handling of embedded inference xml data within an
 * &lt;inference>-tag
 * 
 * @author albrecht
 * 
 */

public class InferenceXmlTag extends UnknownTag implements AnyTag,
EmbeddedInferenceXmlTag {

	private ArrayList<EmbeddedInferenceXmlTag> enclosedInferenceXml;
	private String tagClass;

	public InferenceXmlTag(EfmlTagsAttribute reproduce) {
		super(reproduce);

		this.enclosedInferenceXml = new ArrayList<EmbeddedInferenceXmlTag>();
		this.tagClass = reproduce.getName();
	}

	@Override
	public boolean hasChildren() {

		return this.enclosedInferenceXml.isEmpty() == false;
	}

	@Override
	public ArrayList<EmbeddedInferenceXmlTag> getChildren() {

		return this.enclosedInferenceXml;
	}

	@Override
	public String getTagClass() {

		return this.tagClass;
	}

	@Override
	public void encloseTag(AnyTag innerTag)
			throws OperationNotSupportedException {
		super.encloseTag(innerTag);

		/**
		 * add the inference xml children to the inference xml child list
		 */

		if (innerTag instanceof EmbeddedInferenceXmlTag) {
			EmbeddedInferenceXmlTag inferenceXml = (EmbeddedInferenceXmlTag) innerTag;
			this.enclosedInferenceXml.add(inferenceXml);
		}
	}

}
