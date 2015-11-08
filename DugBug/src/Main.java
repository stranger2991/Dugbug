import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
public class Main {
	Map< String , String> results;
	public enum fix { SQL,RestEndpoint,Script,Manual };
	public Main()
	{
		results = new HashMap<>();
		results.put("sql1","True" );
		results.put("sql2","1" );
	}
	
	public String getResult(String query)
	{
		return results.get(query);
	}
	
	public void processConfig(Element root)
	{
		//base case stop recursing here
		if(root.getTagName().equals("fix"))
		{
			NodeList n = root.getChildNodes();
			
			//loop and find then first element node 
			//then find the appropriate fix
			for(int i= 0;i< n.getLength();i++)
			{
				if(n.item(i).getNodeType() == Node.ELEMENT_NODE)
				{	
					Element f = (Element) n.item(i);
					
					if(f.getAttribute("value").equals(fix.SQL.toString()))
					{
						System.out.println("Executing SQL");
					}
					else if(f.getAttribute("value").equals(fix.SQL.toString()))
					{
						System.out.println("Send Rest request ");
					}
					else if(f.getAttribute("value").equals(fix.Script.toString()))
					{
						System.out.println("Executing SQL");
					}
					else
					{
						System.out.println("Manual intervention ");
					} 
					return ;
				}	
			}
		}	
		System.out.println("root "+ root.getAttribute("value"));		
		//execute  extracted SQL
		// 
		NodeList n = root.getChildNodes();
		for(int i =0 ;i < n.getLength();i++)
		{
			if(n.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element output = (Element)n.item(i);
				
				//get result and compare to all possible output values
				if(output.getAttribute("value").equals(getResult(root.getAttribute("value"))))
				{
					System.out.println("output value "+ output.getAttribute("value"));
					
					NodeList output_childs = output.getChildNodes();
										
					for (int j = 0; j < output_childs.getLength(); j++) 
					{	
						//recurse through the first element childnode
						if(output_childs.item(j).getNodeType() == Node.ELEMENT_NODE)
						{
							processConfig((Element)output_childs.item(j));
							break;
						}
						
					}
					
				}
				 
			}
			break;
		}
	}
	
	public void processConfigUtil()
	{
		Map< String , String> results = new HashMap<>();
		
		File fXmlFile = new File("src/issue.xml");
		DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
		DocumentBuilder dbuilder;
		org.w3c.dom.Document d;
		try {
			dbuilder = db.newDocumentBuilder();
			d=dbuilder.parse(fXmlFile);
			d.getDocumentElement().normalize();
			
			Element root= d.getDocumentElement();
			
			NodeList firstchild = root.getChildNodes();
			for(int i =0 ;i < firstchild.getLength();i++)
			{
				if(firstchild.item(i).getNodeType() == Node.ELEMENT_NODE)
				{	
					System.out.println(firstchild.item(i).getNodeType());
					processConfig((Element)firstchild.item(i));
				}
			}	
			//System.out.println(firstchild.getLength());
			//processConfig((Element)firstchild.item(0));
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	public static void main(String[] args) 
	{
		Main m = new Main();
		m.processConfigUtil();
	}

}
