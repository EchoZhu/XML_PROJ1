package com.xml.proj1;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		int len = atts.getLength();
		if (len > 0) {
			System.out.println("<"+qName+">元素的属性如下：");
			for (int i = 0; i < len; i++) {
				System.out.println(atts.getQName(i)+"--->"+atts.getValue(i));
			}
		}
	}
}
