package com.echo.dom;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
/**
 * divide IPO.XML file into two XML files,
 * (IBM_COMP.XML and ABC_COMP.XML) 
 * based on the attribute value of “comp_name”
 * 
 * @author zhuyikun
 *
 *  2015-11-7
 */
public class DivideByDOM {
	private static int tag = 0;
	private static Element purchaseOrder_ibm = null;
	private static Element purchaseOrder_abc1 = null;
	private static Element purchaseOrder_abc2 = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//Dom解析器工厂
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		//获取dom解析器
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc_ipo = builder.parse("/Users/zhuyikun/xml/Projects/Project1/ipo.xml");
		Document doc_IBM = builder.newDocument();
		Document doc_ABC = builder.newDocument();
		Element purchaseOrders = doc_ipo.getDocumentElement();
		//获取子节点purchaseOrder
		NodeList nodeList =  purchaseOrders.getElementsByTagName("purchaseOrder");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			//获取每个purchaseOrder的comp_name的属性的值
			Node comp_name = node.getAttributes().getNamedItem("comp_name");
			if (comp_name != null) {
//				//根据comp_name的值找到IBM所在的节点
				if (comp_name.getTextContent().equals("IBM")) {
					//将IBM或者ABC所在的节点强制类型转化为Element类型，方便后续清除属性处理
					purchaseOrder_ibm = (Element) node;
					//将节点属性值转化为子节点
					arangeElement(doc_ipo,purchaseOrder_ibm);
					//创建IBM_COMP.xml
					creatIBM_COMP(doc_IBM,purchaseOrder_ibm);
				}
				//根据comp_name的值找到IBM所在的节点
				if (comp_name.getTextContent().equals("ABC")) {
					//第一次获取到ABC所在的节点
					if (tag<1) {
						//将IBM或者ABC所在的节点强制类型转化为Element类型，方便后续清除属性处理
						purchaseOrder_abc1 = (Element) node;
						//将节点属性值转化为子节点
						arangeElement(doc_ipo,purchaseOrder_abc1);
						tag++;
					}else {
						purchaseOrder_abc2 = (Element) node;
						arangeElement(doc_ipo,purchaseOrder_abc2);
						creatABC_COMP(doc_ABC,purchaseOrder_abc1,purchaseOrder_abc2);
					}
				}
			}
		}

	}
	/**
	 * 将节点属性值转化为子节点
	 * 
	 * @param document
	 * @param purchaseOrder_ibm
	 */
	private static void arangeElement(Document document,Element purchaseOrder) {
		// TODO Auto-generated method stub
		purchaseOrder.getAttributes().removeNamedItem("comp_name");
		//获取shipTo节点的属性值
		String exp = ((Element) purchaseOrder.getElementsByTagName("shipTo").item(0)).getAttributeNode("export-code").getTextContent();
		String type = ((Element) purchaseOrder.getElementsByTagName("shipTo").item(0)).getAttributeNode("type").getTextContent();
		//获取billTo节点的属性值
		String type_billTo = ((Element) purchaseOrder.getElementsByTagName("billTo").item(0)).getAttributeNode("type").getTextContent();
		//获取item节点的属性值
		Element items_abc = (Element) purchaseOrder.getElementsByTagName("Items").item(0);
		int i = 0;
		Map<Integer, String> map = new HashMap<Integer, String>();
		//如果存在item节点
		while(items_abc.getElementsByTagName("item").item(i) != null){
			Element item_abc = (Element) items_abc.getElementsByTagName("item").item(i);
			String partNum = item_abc.getAttributeNode("partNum").getTextContent();
			map.put(i, partNum);
	        //删除item节点的属性值
			item_abc.getAttributes().removeNamedItem("partNum");
			i++;
		}
//		删除shipTo节点的属性值
		purchaseOrder.getElementsByTagName("shipTo").item(0).getAttributes().removeNamedItem("export-code");
		purchaseOrder.getElementsByTagName("shipTo").item(0).getAttributes().removeNamedItem("type");
//		删除billTo节点的属性值
		purchaseOrder.getElementsByTagName("billTo").item(0).getAttributes().removeNamedItem("type");
		
//		创建节点<export-code>1</export-code> 
		Element export_codeeElement = document.createElement("export-code");
		export_codeeElement.appendChild(document.createTextNode(exp));
//		创建节点<type>EU-Address</type>
		Element typeElement = document.createElement("type");
		typeElement.appendChild(document.createTextNode(type));
		
//		创建节点<type>US-Address</type>
		Element typeElement_bill = document.createElement("type");
		typeElement_bill.appendChild(document.createTextNode(type_billTo));
		
//		创建节点<partNum>833-AA</partNum>
//		并插入到每个item节点中
		for (int j = 0; j < i; j++) {
			Element partNum = document.createElement("partNum");
			partNum.appendChild(document.createTextNode(map.get(j)));
			Element Items = (Element) purchaseOrder.getElementsByTagName("Items").item(0);
			Element item_abc = (Element) Items.getElementsByTagName("item").item(j);
			item_abc.insertBefore(partNum, item_abc.getFirstChild());
		}
		
		//将新创建的两个节点添加到shipTo节点之下，注意插入位置
		((Element) purchaseOrder.getElementsByTagName("shipTo").item(0)).insertBefore(export_codeeElement, purchaseOrder.getElementsByTagName("shipTo").item(0).getFirstChild());
		((Element) purchaseOrder.getElementsByTagName("shipTo").item(0)).insertBefore(typeElement, purchaseOrder.getElementsByTagName("shipTo").item(0).getChildNodes().item(1));

		//将新创建的两个节点添加到billTo节点，注意插入位置
		((Element) purchaseOrder.getElementsByTagName("billTo").item(0)).insertBefore(typeElement_bill, purchaseOrder.getElementsByTagName("billTo").item(0).getFirstChild());
	}

	/**
	 * 创建ABC_COMP.xml
	 * @param doc_ABC
	 * @param purchaseOrder
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 * @throws IOException
	 */
	private static void creatABC_COMP(Document doc_ABC,Element purchaseOrder1,Element purchaseOrder2) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IOException {
		// TODO Auto-generated method stub
		//创建IBM.xml文件
		doc_ABC.setXmlVersion("1.0");
		//创建根节点 <purchaseOrders> 
		Element root = doc_ABC.createElement("purchaseOrders");
		//创建子节点<IBM_COMP> 
		Element element_IBM_COMP = doc_ABC.createElement("ABC_COMP");
		//向子节点<IBM_COMP>中添加<purchaseOrder>节点的内容
		//<purchaseOrder>从ipo.xml文件中获得
		//复制doc_ipo中的purchaseOrder节点到element_IBM_COMP的子节点
		element_IBM_COMP.appendChild(doc_ABC.importNode(purchaseOrder1, true));
		element_IBM_COMP.appendChild(doc_ABC.importNode(purchaseOrder2, true));
		root.appendChild(element_IBM_COMP);
		doc_ABC.appendChild(root);
		output(doc_ABC,"/Users/zhuyikun/xml/ABC_COMP.xml");
	}
	/**
	 * 创建IBM_COMP.xml
	 * @param doc_IBM
	 * @param purchaseOrder
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 * @throws IOException
	 */
	private static void creatIBM_COMP(Document doc_IBM,Element purchaseOrder) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IOException {
		// TODO Auto-generated method stub
		//创建IBM.xml文件
		doc_IBM.setXmlVersion("1.0");
		//创建根节点 <purchaseOrders> 
		Element root = doc_IBM.createElement("purchaseOrders");
		//创建子节点<IBM_COMP> 
		Element element_IBM_COMP = doc_IBM.createElement("IBM_COMP");
		//向子节点<IBM_COMP>中添加<purchaseOrder>节点的内容
		//<purchaseOrder>从ipo.xml文件中获得
		//复制doc_ipo中的purchaseOrder节点到element_IBM_COMP的子节点
		element_IBM_COMP.appendChild(doc_IBM.importNode(purchaseOrder, true));
		root.appendChild(element_IBM_COMP);
		doc_IBM.appendChild(root);
		output(doc_IBM,"/Users/zhuyikun/xml/IBM_COMP.xml");
	}
	/**
	 * 创建xml文件中，最后输出部分共同操作
	 * @param doc
	 * @param xmlpath
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 * @throws IOException
	 */
	private static void output(Document doc,String xmlpath) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException, IOException{
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry.getDOMImplementation("LS");
		LSSerializer serializer = domImplementationLS.createLSSerializer();
		serializer.getDomConfig().setParameter("format-pretty-print", true);
		LSOutput output = domImplementationLS.createLSOutput();
		output.setEncoding("UTF-8");
		FileWriter stringOut = new FileWriter(xmlpath);
		output.setCharacterStream(stringOut);
		serializer.write(doc, output);
	}
}
