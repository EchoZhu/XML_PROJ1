package divide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class DivideByJdom {
	private static int tag = 0;
	private static Element purchaseOrder_ibm = null;
	private static Element purchaseOrder_abc1 = null;
	private static Element purchaseOrder_abc2 = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//解析
		SAXBuilder builder = new SAXBuilder();
		Document doc_ipo = builder.build(new File("ipo.xml"));
		//创建目标文件
		Document doc_abc = new Document();
		Document doc_ibm = new Document();
		
		Element purchaseOrders = doc_ipo.getRootElement();
		//获取子节点
		List purchaseOrderList = purchaseOrders.getChildren();
		for (Object purchaseOrderObject:purchaseOrderList){
			Element purchaseOrder = (Element)purchaseOrderObject;
			List attList = purchaseOrder.getAttributes();
			for (Object e: attList){
				Attribute attr = (Attribute) e;
				if (attr.getName().equals("comp_name")){
					//获取ABC所在节点
					if(attr.getValue().equals("ABC")){
						if (tag < 1){
							purchaseOrder_abc1 = purchaseOrder;
							//将节点属性转化为子节点
							arrangeElement(purchaseOrder_abc1);
							tag ++;
						}else{
							purchaseOrder_abc2 = purchaseOrder;
							arrangeElement(purchaseOrder_abc2);
							creatABC_COMP(doc_abc,purchaseOrder_abc1,purchaseOrder_abc2);
						}
					}
					//获取到IBM所在节点
					if (attr.getValue().endsWith("IBM")){
						System.out.println("IBM");
						arrangeElement(purchaseOrder);
						createIBM_COMP(doc_ibm,purchaseOrder);
					}
				}
			}
		}
	}
	private static void createIBM_COMP(Document doc,Element purchaseOrder) throws IOException {
		// TODO Auto-generated method stub
		Element rootElement = new Element("purchaseOrders");
		Element IBM_COMP = new Element("IBM_COMP");
		
		//清除purchaseOrder的属性值
		Element purchaseOrderElement = (Element)purchaseOrder.clone();
		purchaseOrderElement.removeAttribute("comp_name");
		
		
		IBM_COMP.addContent(purchaseOrderElement);
		rootElement.addContent(IBM_COMP);
		doc.setRootElement(rootElement);
		
		Format format = Format.getPrettyFormat();
		format.setIndent("    ").setEncoding("UTF-8");
		XMLOutputter xmlOutputter = new XMLOutputter(format);
		FileOutputStream fos = new FileOutputStream("IBM_COMP.xml");
		xmlOutputter.output(doc, fos);
	}
	
	
	private static void creatABC_COMP(Document doc,Element purchaseOrder1,Element purchaseOrder2) throws IOException {
		//TODO
		Element rootElement = new Element("purchaseOrders");
		Element ABC_COMP = new Element("ABC_COMP");
		//清除purchase的属性值
		Element purchasePrder1Element = (Element)purchaseOrder1.clone();
		Element purchasePrder2Element = (Element)purchaseOrder2.clone();
		purchasePrder1Element.removeAttribute("comp_name");
		purchasePrder2Element.removeAttribute("comp_name");
		
		
		ABC_COMP.addContent(0,purchasePrder1Element);
		ABC_COMP.addContent(1,purchasePrder2Element);
		rootElement.addContent(ABC_COMP);
		doc.setRootElement(rootElement);
		
		Format format = Format.getPrettyFormat();
		format.setIndent("    ").setEncoding("UTF-8");
		XMLOutputter xmlOutputter = new XMLOutputter(format);
		FileOutputStream fos = new FileOutputStream("ABC_COMP.xml");
		xmlOutputter.output(doc, fos);
		
	}
	
	
	
	private static void arrangeElement(Element purchaseOrder) {
		// TODO Auto-generated method stub
		//得到purchaseOrder的子节点shipTo和billTo
		Element shipTo = purchaseOrder.getChild("shipTo");
		Element billTo = purchaseOrder.getChild("billTo");
		Element Items = purchaseOrder.getChild("Items");
		//获取属性值
		String exp = shipTo.getAttributeValue("export-code");
		String type_shipTo = shipTo.getAttributeValue("type");
		String type_billTo = shipTo.getAttributeValue("type");
		
//		添加<export-code>3</export-code> 节点
		Element expElement = new Element("export-code");
		expElement.setText(exp);
//		添加<export-code>3</export-code> 节点
		Element typeElement = new Element("type");
		typeElement.setText(type_shipTo);
		
//		添加<export-code>3</export-code> 节点
		Element type_billTo_Element = new Element("type");
		type_billTo_Element.setText(type_billTo);
		
		//添加新节点
		shipTo.addContent(0,expElement);
		shipTo.addContent(1,typeElement);
		billTo.addContent(0,type_billTo_Element);
		
//		最后在清除属性
		shipTo.removeAttribute("export-code");
		shipTo.removeAttribute("type");
		billTo.removeAttribute("type");
		
		List itemsList = Items.getChildren();
		for (Object e:itemsList) {
			Element item = (Element) e;
			Element partNum = new Element("partNum");
			partNum.setText(item.getAttributeValue("partNum"));
			item.addContent(0,partNum);
			//清除属性
			item.removeAttribute("partNum");
		}
	}


}

