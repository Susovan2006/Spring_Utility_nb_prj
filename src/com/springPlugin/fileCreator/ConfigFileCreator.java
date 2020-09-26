/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created by Susovan Gumtya 2014
 */
package com.springPlugin.fileCreator;

import java.util.Properties;

import org.w3c.dom.Document;


import java.io.File ;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Susovan
 */
public class ConfigFileCreator {

    private static Properties properties = null;
    public static String rootDirectoryPath;
    static {
        try {
            properties = new Properties();            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }

    public static void addToBeans(String schema, String userName, String password, String url,String driverClassName,String contextFileName,String basePackageName ) {

        try {            
            File xmlFile = new File("src/sample-application-context.xml");            
            FileInputStream fileStream = new FileInputStream(xmlFile);                 
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();             
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();             
            Document xmlDocument = builder.parse(fileStream); 
            XPath xPath =  XPathFactory.newInstance().newXPath();          
            String expression = "/beans/bean[@id='" + schema + "DataSource" + "']";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
            Node beans = xmlDocument.getElementsByTagName("beans").item(0);
            if (nodeList!=null && nodeList.getLength()==0) {               
                Element bean=xmlDocument.createElement("bean");
                bean.setAttribute("id", schema + "DataSource");
                bean.setAttribute("class", "org.springframework.jdbc.datasource.DriverManagerDataSource");
                Element property1 =xmlDocument.createElement("property"); 
                property1.setAttribute("driverClassName", "${db.driver}");
                setProperty("${db.driver}",driverClassName);
                Element property2 =xmlDocument.createElement("property"); 
                property2.setAttribute("url", "${" + schema + ".db.url}");
                setProperty("${" + schema + ".db.url}",url);
                Element property3 =xmlDocument.createElement("property"); 
                property3.setAttribute("username","${" + schema + ".db.userName}");
                setProperty("${" + schema + ".db.userName}",userName);
                Element property4 =xmlDocument.createElement("property"); 
                property4.setAttribute("password","${" + schema + ".db.password}");
                setProperty("${" + schema + ".db.password}",password);
                bean.appendChild(property1);
                bean.appendChild(property2);
                bean.appendChild(property3);
                bean.appendChild(property4);
                beans.appendChild(bean);               
            } 
                
                System.out.println("Base Package:" + basePackageName);
                Element context_component_scan=xmlDocument.createElement("context:component-scan");                
                context_component_scan.setAttribute("base-package", basePackageName);
                beans.appendChild(context_component_scan);
               
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", 2);
		Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");                
		DOMSource source = new DOMSource(xmlDocument);
		StreamResult result = new StreamResult(rootDirectoryPath + "/" + contextFileName + ".xml");
		transformer.transform(source, result);                
                File configfile = new File(rootDirectoryPath + "/" + "config.properties");
                FileOutputStream fileOut = new FileOutputStream(configfile,true);
                properties.store(fileOut, "Application Properties");
                fileOut.close();
            
            

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }

    }
    
    public static String prettyFormat(String input, int indent) {
    try {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer(); 
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);
        return xmlOutput.getWriter().toString();
    } catch (Exception e) {
        throw new RuntimeException(e); // simple exception handling, please review it
    }
}

    
    private static void setProperty(String key, String value){
     if(properties.getProperty(key)!=null)       
     properties.setProperty(key,value);   
    }

    
    public static void main(String args[]){
         ConfigFileCreator.addToBeans("passwordsafe", "", "", "myURL","ms-access","testContext.xml","com.test");
    }
    
    
}
