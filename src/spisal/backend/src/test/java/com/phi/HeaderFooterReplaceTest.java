package com.phi;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.phi.odt.OdtEngine;

public class HeaderFooterReplaceTest {
	
    // Run once, e.g. Database connection, connection pool
    @BeforeClass
    public static void runOnceBeforeClass() {
//        System.out.println("@BeforeClass - runOnceBeforeClass");
    }

    // Run once, e.g close connection, cleanup
    @AfterClass
    public static void runOnceAfterClass() {
//        System.out.println("@AfterClass - runOnceAfterClass");
    }
    
	@Test
	public void test() {
		//fail("Not yet implemented");
		try {
			File documentFile = new File("src/test/resources/emptyDoc-styles.xml");
			//File modelFile = new File("src/test/resources/model-styles.xml");
			File modelFile = new File("src/test/resources/model-noTbl-styles.xml");
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			Document document = docBuilder.parse(documentFile);
			Document model = docBuilder.parse(modelFile);
			
			byte[] styles = OdtEngine.replaceHeaderFooter(model, document);
			
			System.out.println(new String(styles));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
