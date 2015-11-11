package com.xml.proj1;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DisplayBySAX {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		创建SAX解析器工厂
		SAXParserFactory factory = SAXParserFactory.newInstance();
//		创建新的SAX解析器实例
		SAXParser parse = factory.newSAXParser();
		parse.parse("ipo.xml", new SAXHandler());
		
		
	}

}
