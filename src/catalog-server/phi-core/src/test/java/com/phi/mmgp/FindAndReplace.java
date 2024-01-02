package com.phi.mmgp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Replace content of JollyWidgets
 * 
 * In:
 * <object type="application/x-java-applet" archive="SignedShowDocumentApplet.jar" code="ShowDocument.class" width="100%" Height="100%">
 * <param name="cid" value="#{conversation.id}"/>
 * <param name="reportOwner" value="PatientReport"/>
 * <param name="docType" value="InformativaPrivacyQuestQual"/>
 * <param name="lang" value="it;de;en"/>
 * </object>
 * 
 * Out:
 * <a href="resource/rest/javawebstart/showdocument/InformativaPrivacyQuestQual?lang=it&amp;cid=#{conversation.id}">IT</a>
 * <a href="resource/rest/javawebstart/showdocument/InformativaPrivacyQuestQual?lang=de&amp;cid=#{conversation.id}">DE</a>
 * <a href="resource/rest/javawebstart/showdocument/InformativaPrivacyQuestQual?lang=en&amp;cid=#{conversation.id}">EN</a>
 * 
 * @author Alex
 */
public class FindAndReplace {
	
	private static String inPath = System.getProperty("user.dir")+File.separator+File.separator + "../../phi-klinik/webapp/src/main/modules/";

	private static String inputFileExtension = "mmgp";
	
	//xpath find jollyWidget nodes
	private static String xpathStr = "//*[starts-with(local-name(), 'jollyWidget')]";
	
	//regex find applet props SignedShowDocumentApplet
	private static String pattern = "<.*reportOwner\" value=\"([^\"]+).*docType\" value=\"([^\"]+).*lang\" value=\"([^\"]+).*>";
	private static Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
	
    public static void main (String args[]) throws IOException {
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(xpathStr);

		    File inputFile = new File(inPath);
		    if (!inputFile.exists()) {
		    	System.out.println("There is no input folder for the project. Skipping.");
		        return;
		    }
		
		    Iterator<File> iterator = FileUtils.iterateFiles(inputFile, new String[] { inputFileExtension }, true);
		
		    FileInputStream fis = null;
		    
		    while (iterator.hasNext()) {
		    	File file = iterator.next();
		    	fis = new FileInputStream(file);
		    	Document mmgpDom = docBuilder.parse(fis);
		    	
		    	NodeList jollyWidgets = (NodeList) expr.evaluate(mmgpDom, XPathConstants.NODESET);
				
		    	if (jollyWidgets.getLength() > 0) {
		    		System.out.println(">>>>>" + file.getName());
		    	}
		    	
		    	for (int i = 0; i<jollyWidgets.getLength(); i++) {
		    		Node jwNode = jollyWidgets.item(i);
		    		Node jwCodeAttribute = jwNode.getAttributes().getNamedItem("customCode");
		    		String jwCode = jwCodeAttribute.getNodeValue();
		    		
		    		
		    		if (jwCode != null && jwCode.contains("SignedShowDocumentApplet")) {
		    			
		    			System.out.println(jwCode);
		    			
			            Matcher m = p.matcher(jwCode);
			            if (m.matches()) {
			            	
			            	System.out.println("File getName() " + file.getName());
			            	
			            	String reportOwner = m.group(1);
			            	String docType = m.group(2);
			            	String[] lang = m.group(3).split(";");
			            	
			            	StringBuffer sb = new StringBuffer();
			            	
			            	for (String curLang : lang) {
			            		sb.append("<a href=\"resource/rest/javawebstart/showdocument/");
			            		sb.append(docType);
			            		sb.append("?lang=");
			            		sb.append(curLang);
			            		sb.append("&amp;reportOwner=");
			            		sb.append(reportOwner);
			            		sb.append("&amp;cid=#{conversation.id}\" class=\"lnk2report\">");
			            		sb.append(curLang.toUpperCase());
			            		sb.append("</a>\n");
			            	}
			            	

			            	jwCodeAttribute.setNodeValue(sb.toString());
			            	
					    	byte[] fixedMmgp = nodeToByteArray(mmgpDom);

					        String relativePath = inputFile.toURI().relativize(file.toURI()).getPath();
					
					        //relativePath = relativePath.replace(".mmgp", "-noApplet.mmgp");
					
					        File outFile = new File(inPath + relativePath);
					        FileUtils.writeByteArrayToFile(outFile, fixedMmgp);

			            }
		    		}
		    	}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }
    
	/**
	 * Convert node to byteArray
	 * @param node to convert
	 * @return byte[]
	 */
	private static byte[] nodeToByteArray(Node node) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(node);
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	transformer.transform(source, new StreamResult(bos));
    	return bos.toByteArray();
	}
}
