package com.phi.db.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.amianto.mappings.CodeValueMapping;
import com.amianto.mappings.TableMapping;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class UpdateOrCreateMappingTables {
	
	private static final Logger log = Logger.getLogger(UpdateOrCreateMappingTables.class.getName());
	
	private static EntityManagerFactory mappingEmf;
	private static enum supportedDb { mySql, oracle, mySql2 };
	private static boolean usingEclipse = false;
	
	private static boolean commit = true;
	public static String ulss = "050112";
	public static String distretto = "";	//ME = Mestre, DC = Dolo-Chioggia
	
	public static void main (String[] args) throws IOException  {

		String xlsFileName = null;
		if (args != null ) {
			for (String arg : args) {
				if (arg != null && arg.contains("usingEclipse=")) {
					usingEclipse = (arg.split("=")[1]).equals("true");
				}
				if (arg != null && arg.contains("xls")) {
					xlsFileName = arg;
				}
			}
		}
		
		try {
			Map<String, Object> configOverrides = getConfigOverrides(supportedDb.mySql2);

			mappingEmf = Persistence.createEntityManagerFactory("mapping", configOverrides);
			
			//UNCOMMENT THIS TO RELOAD CODE VALUE MAPPINGS FROM EXCEL
			//loadCodeValueMappings(mappingEmf.createEntityManager(), xlsFileName);
			
			if(mappingEmf.isOpen())
				mappingEmf.close();
		} catch (Exception e) {
			System.err.println("ERROR BUILDING ENTITY MANAGER FACTORY!");
			e.printStackTrace();
		} finally{
			if(mappingEmf.isOpen())
				mappingEmf.close();
		}
	}
	
	private static void loadCodeValueMappings(EntityManager em, String xlsFileName) throws BiffException, IOException{
		if (xlsFileName == null || xlsFileName.isEmpty()) {
			xlsFileName = "Mapping_dizionari.xls";
		}
		InputStream xls;
		//in caso di debug
		if (usingEclipse) {
			log.info("[usingEclipse] reading mapping/"+xlsFileName);
			xls = new FileInputStream("mapping/"+xlsFileName);
		}
		else {//in caso di jar
			log.info("reading "+xlsFileName);
			xls = new FileInputStream(xlsFileName);
		}
		
		Workbook wb = Workbook.getWorkbook(xls);
		Sheet[] fogli = wb.getSheets();

		if (fogli == null || fogli.length==0) {
			return;
		}
		
		if(commit){
			String deleteCvMapping = "DELETE FROM cv_mapping";
			String deleteTableMapping = "DELETE FROM table_mapping";
			
			em.getTransaction().begin();
			
			Query qDeleteCvMapping = em.createNativeQuery(deleteCvMapping);
			qDeleteCvMapping.executeUpdate();
			Query qDeleteTableMapping = em.createNativeQuery(deleteTableMapping);
			qDeleteTableMapping.executeUpdate();
			
			em.getTransaction().commit();
		}

		for(Sheet foglio : fogli){
			if(!"Tabelle-Dictionary".equals(foglio.getName()) && !"Comuni".equals(foglio.getName())){
				if(commit){
					em.getTransaction().begin();
				}
				System.out.println("Processing "+foglio.getName());
				Cell[] testata = foglio.getRow(1);
				if(testata[0]==null || testata[3]==null)
					continue;
				
				String codicetabella = testata[0].getContents();
				String classe = testata[3].getContents();
				
				String csDomain = testata[2].getContents();
				String codeSystem = null;
				String domain = null;
				if(csDomain!=null){
					String[] tokens = csDomain.split(":");
					if(tokens.length==2){
						codeSystem = tokens[0];
						domain = tokens[1];
						
						TableMapping tmap = new TableMapping(codeSystem, domain, classe, codicetabella, ulss);
						if(commit){
							em.persist(tmap);
						}
					}
				}
				
				int i = 3;
				while(i < foglio.getRows()){
					Cell[] row = foglio.getRow(i);
					if(row.length>=3 && row[0]!=null && row[2]!=null && !row[0].getContents().isEmpty() && !row[2].getContents().isEmpty()){
						CodeValueMapping map = new CodeValueMapping(row[0].getContents(), row[2].getContents(), classe, codicetabella, ulss);
						if(commit){
							em.persist(map);
						}
					}
					i++;
				}
				
				if(commit){
					em.flush();
					em.getTransaction().commit();
				}
			}
		}


		wb.close();
		
	}
	
	private static Map<String, Object> getConfigOverrides(supportedDb db) throws FileNotFoundException, IOException{
		Map<String, Object> configOverrides = new HashMap<String, Object>();
		Properties properties = new Properties();

		String dbPropertiesFileName = db + ".build.properties";

		if (usingEclipse) {
			properties.load(UpdateOrCreateMappingTables.class.getResourceAsStream("/"+dbPropertiesFileName));
		}
		else {
			properties.load(new FileInputStream(dbPropertiesFileName));
		}

		String connectionDriVerClass = properties.getProperty("driver-class");
		String connetionUrl = properties.getProperty("connection-url");
		String user = properties.getProperty("user-name");
		String pass = properties.getProperty("password");
		
		configOverrides.put("hibernate.connection.url", connetionUrl);
		configOverrides.put("hibernate.connection.driver_class", connectionDriVerClass);
		configOverrides.put("hibernate.connection.username", user);
		configOverrides.put("hibernate.connection.password", pass);
		configOverrides.put("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
		
		return configOverrides;
	}
}