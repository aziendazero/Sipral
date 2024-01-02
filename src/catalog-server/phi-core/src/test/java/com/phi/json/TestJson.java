package com.phi.json;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;

/**
 * Read db properties from mySql.build.properties or oracle.build.properties and
 * test json
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author Zupan
 */

public class TestJson {

	private static EntityManager em;
	
	private static EntityManagerFactory emf;
	
	private static enum supportedDb { mySql, oracle };
	
	private static ObjectMapper mapper;
	
	//Seleted db:
	private static supportedDb db = supportedDb.oracle; 

	//user to be created on rim
//	private static String username = "admin";
//	private static String password = "12345";

//	protected static String loginHql = "FROM Patient WHERE genderCode is not null";
	protected static String loginHql = "FROM Patient WHERE internalId = 370";
	
	//CriteriaImpl(com.phi.entities.baseEntity.TherapySimple:this[Subcriteria(patientEncounter:PatientEncounter)][internalId=2901406])
	protected static String  hql = "SELECT t from TherapySimple t WHERE t.patientEncounter = 2901406";
//	protected static String  hql = "SELECT p from PrescriptionSimple p WHERE p.internalId = 134";
	
	private static final Logger log = Logger.getLogger(TestJson.class.getName());
	
	public static void main (String[] args) throws PersistenceException, DictionaryException  {
	
		setupLog();
		
		setupJson();

		try {
//			String parse = "{\"noAllergy\":false,\"language\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.02.20\"},\"birthTime\":\"1977-03-16T23:00:00Z\",\"externalConsent\":false,\"telecom\":{\"hp\":\"0409229427\"},\"internalId\":370,\"birthPlace\":{},\"foreignBirthPlace\":false,\"name\":{\"giv\":\"Test\",\"fam\":\"Test\"},\"genderCode\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.2.10173\"},\"externalId\":\"000000\",\"internalConsent\":false,\"addr\":{\"code\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValueCity\",\"id\":\"2.16.840.1.113883.2.9.4.2.3.04.021.051_V20111001\"},\"cpa\":\"BZ\",\"stb\":\"via roma\",\"zip\":\"39012\",\"cty\":\"Merano\",\"bnr\":\"5\"}}\"";
//			String parse = "{\"noAllergy\":false,\"language\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.02.20\"},\"birthTime\":\"1977-03-16T23:00:00Z\",\"externalConsent\":false,\"telecom\":{\"hp\":\"0409229427\"},\"internalId\":370,\"foreignBirthPlace\":false,\"name\":{\"giv\":\"Test\",\"fam\":\"Test\"},\"genderCode\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.2.10173\"},\"externalId\":\"000000\",\"internalConsent\":false,\"addr\":{\"code\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValueCity\",\"id\":\"2.16.840.1.113883.2.9.4.2.3.04.021.051_V20111001\"},\"cpa\":\"BZ\",\"stb\":\"via roma\",\"zip\":\"39012\",\"cty\":\"Merano\",\"bnr\":\"5\"}}";
//			String parse = "{\"noAllergy\":false,\"language\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.02.20\"},\"birthTime\":\"1977-03-16T23:01:02Z\",\"externalConsent\":false,\"telecom\":{\"hp\":\"0409229427\"},\"internalId\":370,\"foreignBirthPlace\":false,\"name\":{\"giv\":\"Test\",\"fam\":\"Test\"},\"genderCode\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValuePhi\",\"id\":\"2.16.840.1.113883.3.20.100.2.10173\"},\"externalId\":\"000000\",\"internalConsent\":false,\"addr\":{\"code\":{\"entityName\":\"com.phi.entities.dataTypes.CodeValueCity\",\"id\":\"2.16.840.1.113883.2.9.4.2.3.04.021.051_V20111001\"},\"cpa\":\"BZ\",\"stb\":\"via roma\",\"zip\":\"39012\",\"cty\":\"Merano\",\"bnr\":\"5\"}}";
//			
//			Patient pattest = mapper.readValue(parse, Patient.class);
//			
//			System.out.println(pattest);
			setupEm();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		Query q = em.createQuery(loginHql);
		Query q = em.createQuery(hql);
		q.setMaxResults(10);
//		q.setParameter("user", username);
//        q.setParameter("pass", password);
				
		List oldList = q.getResultList();
		
		if (!oldList.isEmpty()) {
			
			try {
				
				String jSon = mapper.writeValueAsString(oldList.get(0));
				
				
				System.out.println(jSon);
				
				//Patient pat = mapper.readValue(employee, Patient.class);
				
				//TherapySimple entity = mapper.readValue(jSon, TherapySimple.class);
				
				
				//System.out.println(entity);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}
	
	public static void setupJson() {
		
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
//		mapper.setDateFormat(df);
		mapper.registerModule(new HibernateModule());
		
	}

	private static void setupEm () {
		
		try {
			// Read db properties from mySql.build.properties and oracle.build.properties
			Properties properties = new Properties();

			String dbPropertiesFileName = db + ".build.properties";
			
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
			
			configOverrides.put("hibernate.show_sql", "true");
			
			log.info("Creating connection to: "+ connetionUrl+ " with username: "+user);
			
			emf = Persistence.createEntityManagerFactory(db.name(), configOverrides);
			
			em = emf.createEntityManager();
			
		} catch (Exception e) {
			System.err.println("ERROR BUILDING ENTITY MANAGER!");
			e.printStackTrace();
		}

	}
	
	private static void setupLog() {
		  ConsoleAppender console = new ConsoleAppender(); //create appender
		  //configure the appender
		  String PATTERN = "%d |%p|%c|> %m%n";
		  console.setLayout(new PatternLayout(PATTERN)); 
		  console.setThreshold(Level.INFO);
		  console.activateOptions();
		  //add appender to any Logger (here is root)
		  log.addAppender(console);
	}
	
}