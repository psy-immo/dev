package de.tu_dresden.psy.efml;

/**
 * implements the handling of embedded inference xml data within an
 * &lt;inference>-tag
 * 
 * @author albrecht
 * 
 */

public class InferenceXmlTag extends UnknownTag implements AnyTag {

	public InferenceXmlTag(EfmlTagsAttribute reproduce) {
		super(reproduce);
	}

}
