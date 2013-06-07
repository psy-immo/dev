package de.tu_dresden.psy.efml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	private Map<String, String> attributeValues;


	public InferenceXmlTag(EfmlTagsAttribute reproduce) {
		super(reproduce);

		this.enclosedInferenceXml = new ArrayList<EmbeddedInferenceXmlTag>();
		this.tagClass = reproduce.getName();
		this.attributeValues = new HashMap<String, String>();

		Map<String, String> attribs = reproduce.getAttribs();
		for (String attributeName : attribs.keySet()) {
			/**
			 * the tags attribute belongs to the EFML data structure layer and
			 * is not going to show in the html
			 */
			if ((attributeName != "tags") && (attributeName != "atags")
					&& (attributeName != "rtags")) {
				this.attributeValues.put(attributeName,
						attribs.get(attributeName));
			}
		}

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

	@Override
	public Map<String, String> getAttributes() {
		return this.attributeValues;
	}

	@Override
	public String getStringContent() {
		StringBuffer combine = new StringBuffer();

		for (EmbeddedInferenceXmlTag t : this.enclosedInferenceXml) {
			combine.append(t.getStringContent());
		}

		return combine.toString();
	}
}
