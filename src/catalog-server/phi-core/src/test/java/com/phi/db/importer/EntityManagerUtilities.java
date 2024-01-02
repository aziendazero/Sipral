package com.phi.db.importer;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public abstract class EntityManagerUtilities {
	public static final Logger log = Logger.getLogger(EntityManagerUtilities.class.getName());

	public EntityManager em;

	protected static EntityManagerFactory emf;

	public static enum supportedDb { mySql, oracle, sqlServer };


	//Seleted db:
	protected supportedDb db = supportedDb.oracle; 

	/**
	 * Close the old entityManager and create a new one
	 */
	public void setupEm() {

		try {
			if(em!=null && em.isOpen()){
				em.close();
			}
			em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
		} catch (Exception e) {
			log.error("ERROR BUILDING ENTITY MANAGER!",e);
		}

	}

	protected void setupEntityManagerFactory() {

		try {
			// Read db properties from mySql.build.properties and oracle.build.properties
			Properties properties = new Properties();

			String dbPropertiesFileName = "../" + db + ".build.properties";

			properties.load(new FileInputStream(dbPropertiesFileName));

			String connetionDriVerClass = properties.getProperty("driver-class");
			String connetionUrl = properties.getProperty("connection-url");
			String user = properties.getProperty("user-name");
			String pass = properties.getProperty("password");

			Map<String, Object> configOverrides = new HashMap<String, Object>();
			configOverrides.put("hibernate.connection.url", connetionUrl);
			configOverrides.put("hibernate.connection.driver_class", connetionDriVerClass);
			configOverrides.put("hibernate.connection.username", user);
			configOverrides.put("hibernate.connection.password", pass);
			configOverrides.put("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
			//http://stackoverflow.com/questions/1780341/do-i-need-class-elements-in-persistence-xml
			configOverrides.put("hibernate.archive.autodetection","class");
			configOverrides.put("hibernate.show_sql", "true");
			log.info("Creating connection to: "+ connetionUrl+ " with username: "+user);
			emf = Persistence.createEntityManagerFactory(db.name(), configOverrides);



		} catch (Exception e) {
			System.err.println("ERROR BUILDING ENTITY MANAGER FACTORY!");
			e.printStackTrace();
		}

	}

	protected void setupLog() {
		setupLog(null);
	}
	
	protected void setupLog(Logger l) {
		if (l==null) {
			l=log;
		}
			
		ConsoleAppender console = new ConsoleAppender(); //create appender
		//configure the appender
		String PATTERN = "%d |%p|%c|> %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.INFO);
		console.activateOptions();
		//add appender to any Logger (here is root)
		l.addAppender(console);
	}

	public void closeResource() {
		if(emf!=null)
			emf.close();
	}

	public EntityManagerUtilities() {
		super();
	}

	public EntityManager getEntityManager(){
		return em;
	}

	public void setEntityManager(javax.persistence.EntityManagerFactory emf) {
		EntityManagerUtilities.emf=emf;
	}

	/**
	 * close entityMange, if previously opened
	 */
	protected void closeEm() {
		if(em!=null && em.isOpen()){
			em.close();}
	}
}