package com.phi.db.arpav.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class UpdateOrCreateMappingTables {
	
	private static final Logger log = Logger.getLogger(UpdateOrCreateMappingTables.class.getName());
	
	private static EntityManagerFactory mappingEmf;
	private static enum supportedDb { mySql, oracle };
	private static boolean usingEclipse = false;
	
	public static void main (String[] args) throws IOException  {

		if (args != null ) {
			for (String arg : args) {
				if (arg != null && arg.contains("-eclipse")) {
					usingEclipse = true;
				}
			}
		}
		
		try {
			Map<String, Object> configOverrides = getConfigOverrides(supportedDb.mySql);

			mappingEmf = Persistence.createEntityManagerFactory("mappingArpav", configOverrides);
						
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