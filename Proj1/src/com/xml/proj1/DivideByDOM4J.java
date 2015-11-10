package com.xml.proj1;

 
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
public class DivideByDOM4J {
	private static int tag = 0; 
	 private static Element purchaseOrder_ibm = null; 
	 private static Element purchaseOrder_abc1 = null; 
	 private static Element purchaseOrder_abc2 = null; 
	public static void main(String[] args)throws Exception
	{
		//创建一个DocumentFactory对象
		DocumentFactory factory=new DocumentFactory();
		//创建一个Document对象
		Document doc_IBM=factory.createDocument();
		//创建一个Document对象
		Document doc_ABC=factory.createDocument();
		//使用SAXReader来解析XML
		SAXReader reader=new SAXReader();
    Document doc=reader.read(new File("ipo.xml"));
       Element root=doc.getRootElement();
       List PurList=root.elements();
       for(int i=0;i<PurList.size();i++){
    	   Element element =(Element)PurList.get(i);
    	   Attribute comp_name=(Attribute)element.attributes().get(0);
    	   String str=comp_name.getValue();
	    		  if(str.equals("IBM")){
	    			  purchaseOrder_ibm=element;
	    			  arrangeElement(purchaseOrder_ibm);
	    	          creatIBM(doc_IBM,purchaseOrder_ibm);
	    		  }   
                 if(str.equals("ABC")){
                	if(tag<1){
                		purchaseOrder_abc1=element;
                		arrangeElement(purchaseOrder_abc1);
                		tag++;
                	} 
                	else{
                		purchaseOrder_abc2=element;
                		arrangeElement(purchaseOrder_abc2);
                		creatABC(doc_ABC,purchaseOrder_abc1,purchaseOrder_abc2);
                	}
                 }
       }
       OutputFormat format=new OutputFormat("  ",true,"UTF-8");
       format.setTrimText(true);//清空原有的换行和缩进
       FileWriter fw=new FileWriter("IBM.xml");
       XMLWriter writer=new XMLWriter(fw,format);
       writer.write(doc_IBM);
       fw.close();
       OutputFormat format1=new OutputFormat("  ",true,"UTF-8");
       format1.setTrimText(true);//清空原有的换行和缩进
       FileWriter fw1=new FileWriter("ABC.xml");
       XMLWriter writer1=new XMLWriter(fw1,format);
       writer1.write(doc_ABC);
       fw1.close();
       } 
	private static void creatABC(Document doc, Element purchaseOrder_abc12,
			Element purchaseOrder_abc22) {
		
		Element element=doc.addElement("purchaseOrders").addElement("ABC_COMP");
		element.add((Element)purchaseOrder_abc1.clone());
	    element.add((Element)purchaseOrder_abc2.clone());
		
	}


	private static void creatIBM(Document doc, Element purchaseOrder_ibm) throws Exception{
            Element element=doc.addElement("purchaseOrders").addElement("IBM_COMP");
		    element.add((Element)purchaseOrder_ibm.clone());
           
            
            
	}

	private static void arrangeElement(Element purchaseOrder_ibm)throws Exception {
		
		List attList=purchaseOrder_ibm.attributes();
		//遍历所有的属性
		for(int i=0;i<attList.size();i++){
			Attribute att=(Attribute)attList.get(i);
			purchaseOrder_ibm.remove(att);
		}
			Element shipto=(Element)purchaseOrder_ibm.elements().get(0);
			Attribute export =(Attribute)shipto.attributes().get(0);
			String export_code=export.getValue();
			Attribute type =(Attribute)shipto.attributes().get(1);
			String Type=type.getValue();
			Element billto=(Element)purchaseOrder_ibm.elements().get(1);
			Attribute btype=(Attribute)billto.attributes().get(0);
			String billtpe=btype.getValue();
			Element items=(Element)purchaseOrder_ibm.elements().get(2);
			List itemList=items.elements();
			int i=0;
			Map<Integer,String>map=new HashMap<Integer,String>();
			for(i=0;i<itemList.size();i++){
				Element item=(Element)itemList.get(i);
				Attribute itemAttri=(Attribute)item.attributes().get(0);
				String partNum=itemAttri.getValue();
				map.put(i, partNum);
				//删除
				item.remove(itemAttri);
			}
			//删除ship的属性
			shipto.remove(export);
			shipto.remove(type);
			//删除billto的属性
			billto.remove(btype);
			//创建节点
			Element shuxing=DocumentHelper.createElement("export_code");
			shuxing.setText(export_code);
			List<Element> shipList=shipto.elements();
			shipList.add(0,shuxing);
			Element shuxing1=DocumentHelper.createElement("type");
			shuxing1.setText(Type);
			shipList.add(1, shuxing1);
			Element shuxing2=DocumentHelper.createElement("type");
			shuxing2.setText(billtpe);
			List<Element> billList=billto.elements();
			billList.add(0,shuxing2);
			for(int j=0;j<itemList.size();j++){
				Element item=(Element)itemList.get(j);
				Element shuxing3=DocumentHelper.createElement("partNum");
				shuxing3.setText(map.get(j));
				List<Element> itList=item.elements();
				itList.add(0, shuxing3);
	}
	}
}


