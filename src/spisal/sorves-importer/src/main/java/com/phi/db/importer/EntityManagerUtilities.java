package com.phi.db.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.sorves.entities.Comuni;
import com.sorves.mappings.CodeValueMapping;



/**
 * EntityManagerUtilities with double em:
 * one for source db, one for target db
 * @author 510087
 *
 */
@SuppressWarnings({"unchecked"})
public abstract class EntityManagerUtilities {
	public static final Logger log = Logger.getLogger(EntityManagerUtilities.class.getName());
	public static boolean usingEclipse = false; 

	public Query queryComuni;
	public Query queryCode;
	public Query queryCodeDomain;
	public Query queryCodeSystem;
	public Query queryCountry;
	public Query queryParameter;
	public Query queryLaw;
	public Query queryAteco;
	public Query queryStatus;
	public Query queryOrg;
	public Query queryUlss;
	public Query queryLinea;
	
	protected static EntityManagerFactory sourceEmf;
	protected static EntityManagerFactory targetEmf;
	
	public static EntityManager sourceEm;	// SOURCE DB
	public static EntityManager targetEm;	// TARGET TB

	public static enum supportedDb { mySql, oracle, mySql2 };	
	
	public static boolean commit = true;
	public static boolean disableAur = false;
	public static String ulss = "";	//050112 = ULSS 12
	public static String distretto = "";	//ME = Mestre, DC = Dolo-Chioggia

	//Seleted db:
	protected supportedDb sourceDb = supportedDb.mySql2; 
	protected supportedDb targetDb = supportedDb.mySql; 

	public EntityManagerUtilities() {
		super();
		if(sourceEm==null || !sourceEm.isOpen() || targetEm==null || !targetEm.isOpen()){
			setupEntityManagerFactory();
		}
		
		queryComuni = targetEm.createQuery("SELECT cv FROM CodeValueCity cv WHERE cv.oid = :oid");
		queryCode = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid");
		queryCodeDomain = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.code = :code AND cv.oid like :parentOid");
		queryCodeSystem= targetEm.createQuery("SELECT cs FROM CodeSystem cs WHERE cs.name = :name");
		queryCountry = targetEm.createQuery("SELECT cv FROM CodeValueCountry cv WHERE cv.oid = :oid");
		queryParameter = targetEm.createQuery("SELECT cv FROM CodeValueParameter cv WHERE cv.oid = :oid");
		queryLaw = targetEm.createQuery("SELECT cv FROM CodeValueLaw cv WHERE cv.oid = :oid");
		queryAteco = targetEm.createQuery("SELECT cv FROM CodeValueAteco cv WHERE cv.code = :code");
		queryStatus = targetEm.createQuery("SELECT cv FROM CodeValueStatus cv WHERE cv.oid = :oid");
		queryOrg = targetEm.createQuery("SELECT o FROM Organization o WHERE o.id = :code");
		queryUlss = targetEm.createQuery("SELECT s FROM ServiceDeliveryLocation s JOIN s.organization org "
				+ "WHERE s.isActive = 1 AND org.id = :id");
		queryLinea = targetEm.createQuery("SELECT linea FROM ServiceDeliveryLocation linea "
				+ "JOIN linea.parent spisal "
				+ "JOIN spisal.parent ulss "
				+ "JOIN linea.area area "
				+ "JOIN ulss.organization org "
				+ "JOIN spisal.id idSpisal "
				+ "WHERE linea.isActive = 1 AND area.oid = :oid "
				+ "AND org.id = :id AND idSpisal.root = 'HBS' and idSpisal.extension = :idDist");
	}

	public void saveOnSource(Object entity){
		if(commit && entity!=null){
			sourceEm.getTransaction().begin();
			
			sourceEm.persist(entity);
			sourceEm.flush();
		
			sourceEm.getTransaction().commit();
		}
	}
	
	public void saveOnTarget(Object entity){
		if(commit && entity!=null){
			targetEm.getTransaction().begin();
			
			targetEm.persist(entity);
			targetEm.flush();
		
			targetEm.getTransaction().commit();
		}
	}
	
	public void closeResource() {
		if(sourceEm!=null && sourceEm.isOpen()){
			sourceEm.close();
		}
		
		if(targetEm!=null && targetEm.isOpen()){
			targetEm.close();
		}
	}
	
	public CodeValueCity getComune(Comuni com){
		if(com!=null && com.getIstat()!=null){
			DecimalFormat df = new DecimalFormat("000000");
			String istat = df.format(Integer.parseInt(com.getIstat().trim()));
			return getComune(istat);
		}
		
		return null;
	}
	
	public CodeValueCity getComune(String codiceIstat){
		if(codiceIstat!=null){
			String oid = "comuni."+codiceIstat;
			
			queryComuni.setParameter("oid", oid);
			List<CodeValueCity> list = queryComuni.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find Mapped Comune for - codiceIstat:"+codiceIstat+" ulss:"+ulss);
			}
		}
		
		return null;
	}
	
	public CodeValuePhi getCodeValue(String oid){
		if(oid!=null){
			queryCode.setParameter("oid", oid);
			List<CodeValuePhi> list = queryCode.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValue for oid:"+oid);
			}
		}
		
		return null;
	}
	
	public CodeValuePhi getCodeValueCode(String parentOid, String code){
		if(code!=null){
			queryCodeDomain.setParameter("parentOid", parentOid+"%");
			queryCodeDomain.setParameter("code", code);
			List<CodeValuePhi> list = queryCodeDomain.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValue for parentOid:"+parentOid+", code:"+code);
			}
		}
		
		return null;
	}
	
	public CodeValueCountry getCountry(String oid){
		if(oid!=null){
			queryCountry.setParameter("oid", oid);
			List<CodeValueCountry> list = queryCountry.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValueCountry for oid:"+oid);
			}
		}
		
		return null;
	}
	
	public CodeValueParameter getParameter(String oid){
		if(oid!=null){
			queryParameter.setParameter("oid", oid);
			List<CodeValueParameter> list = queryParameter.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValueParameter for oid:"+oid);
			}
		}
		
		return null;
	}
	
	public CodeValueLaw getLaw(String oid){
		if(oid!=null){
			queryLaw.setParameter("oid", oid);
			List<CodeValueLaw> list = queryLaw.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValueCountry for oid:"+oid);
			}
		}
		
		return null;
	}
	
	public CodeValueAteco getAteco(String code){
		if(code!=null){
			queryAteco.setParameter("code", code);
			List<CodeValueAteco> list = queryAteco.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValueAteco for code:"+code);
			}
		}
		
		return null;
	}
	
	public CodeValueStatus getStatus(String oid){
		if(oid!=null){
			queryStatus.setParameter("oid", oid);
			List<CodeValueStatus> list = queryStatus.getResultList();
			if(list!=null && !list.isEmpty()) {
				return list.get(0);
			}else {
				log.warn("Unable to find CodeValueStatus for oid:"+oid);
			}
		}
		
		return null;
	}
	
	public Organization findOrganization(String code){
		
		if(code!=null && !code.isEmpty()){
			queryOrg.setParameter("code", code);
			List<Organization> list = queryOrg.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find Organization for code:"+code);
			}
			
		}
		
		return null;
	}
	
	public ServiceDeliveryLocation findUlss(String id){
		
		if(id!=null && !id.isEmpty()){
			queryUlss.setParameter("id", id);
			List<ServiceDeliveryLocation> list = queryUlss.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find Ulss for id:"+id);
			}
			
		}
		
		return null;
	}
	
	public ServiceDeliveryLocation findLineaDiLavoro(String idUlss, String idDist, String oid){
		if(idUlss!=null && oid!=null){
			queryLinea.setParameter("id", idUlss);
			queryLinea.setParameter("oid", oid);
			queryLinea.setParameter("idDist", idDist);
			List<ServiceDeliveryLocation> list = queryLinea.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find LineaDiLavoro for - idUlss:"+idUlss+" - idDist:"+idDist+" oid:"+oid);
			}
		}
		
		return null;
	}
		
	public CodeValue getMappedCode(String utcode, String codiceTabella, String ulss){
		if(utcode==null || utcode.isEmpty() || codiceTabella==null)
			return null;
		
		try {
			String hqlMap = "SELECT map FROM CodeValueMapping map "
					+ "WHERE map.codiceTabella = :codiceTabella "
					+ "AND map.utcode = :utcode ";
			
			if(ulss!=null){
				hqlMap += "AND map.ulss = :ulss";
			}
			
			Query qMap = sourceEm.createQuery(hqlMap);
			qMap.setParameter("codiceTabella", codiceTabella.toString());
			qMap.setParameter("utcode", utcode);
			if(ulss!=null){
				qMap.setParameter("ulss", ulss);
			}
			List<CodeValueMapping> listMap = qMap.getResultList();
			if(listMap!=null && !listMap.isEmpty()){
				CodeValueMapping map = listMap.get(0);
				if(map!=null && map.getCvClass()!=null && !map.getCvClass().isEmpty() 
						&& map.getOid()!=null && !map.getOid().isEmpty()){
					
					String hqlCv = "SELECT cv FROM " +map.getCvClass()+ " cv WHERE cv.oid = :oid";
					Query qCv = targetEm.createQuery(hqlCv);
					qCv.setParameter("oid", map.getOid());
					List<CodeValue> listCv = qCv.getResultList();
					if(listCv!=null && !listCv.isEmpty()){
						CodeValue cv = listCv.get(0);
						return cv;
					}
				}
			}
		} catch (Exception e) {
			log.error("Error getting CodeValue - utcode:"+utcode+" codiceTabella:"+codiceTabella+" ulss:"+ulss);
		}
				
		return null;
	}
	
	protected void setupEntityManagerFactory() {

		try {

			Map<String, Object> sourceOverrides = getConfigOverrides(sourceDb);
			sourceEmf = Persistence.createEntityManagerFactory(sourceDb.name(), sourceOverrides);

			Map<String, Object> targetOverrides = getConfigOverrides(targetDb);
			targetEmf = Persistence.createEntityManagerFactory(targetDb.name(), targetOverrides);

			setupEm();
		} catch (Exception e) {
			System.err.println("ERROR BUILDING ENTITY MANAGER FACTORY!");
			e.printStackTrace();
		}

	}
	
	/**
	 * Close the old entityManager and create a new one
	 */
	private void setupEm() {

		try {
			if(sourceEm!=null && sourceEm.isOpen()){
				sourceEm.close();
			}
			sourceEm = sourceEmf.createEntityManager();
			sourceEm.setFlushMode(FlushModeType.COMMIT);
			if(targetEm!=null && targetEm.isOpen()){
				targetEm.close();
			}
			targetEm = targetEmf.createEntityManager();
			targetEm.setFlushMode(FlushModeType.COMMIT);
		} catch (Exception e) {
			log.error("ERROR BUILDING ENTITY MANAGER!",e);
		}
	}
	
	private Map<String, Object> getConfigOverrides(supportedDb db) throws FileNotFoundException, IOException{
		Map<String, Object> configOverrides = new HashMap<String, Object>();
		Properties properties = new Properties();

		String dbPropertiesFileName = db + ".build.properties";

		
		if (usingEclipse) {
			properties.load(new FileInputStream("src/main/resources/"+dbPropertiesFileName));
		}
		else {
			properties.load(new FileInputStream(dbPropertiesFileName));
		}

		String connectionDriVerClass = properties.getProperty("driver-class");
		String connetionUrl = properties.getProperty("connection-url");
		String user = properties.getProperty("user-name");
		String pass = properties.getProperty("password");
		String updateDb = properties.getProperty("hibernate.hbm2ddl.auto");

		
		configOverrides.put("hibernate.connection.url", connetionUrl);
		configOverrides.put("hibernate.connection.driver_class", connectionDriVerClass);
		configOverrides.put("hibernate.connection.username", user);
		configOverrides.put("hibernate.connection.password", pass);
		configOverrides.put("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
		configOverrides.put("hibernate.hbm2ddl.auto", updateDb);
		//http://stackoverflow.com/questions/1780341/do-i-need-class-elements-in-persistence-xml
		configOverrides.put("hibernate.archive.autodetection","class");
		log.info("Creating connection to: "+ connetionUrl+ " with username: "+user);
		
		return configOverrides;
	}

	public void manageAddrComune(AD addr, Comuni com){
		if(addr==null)
			return;
		
		addr.setCode(getComune(com));
		if(addr.getCode()!=null){
			addr.setCpa(addr.getCode().getProvince());
			addr.setZip(addr.getCode().getZip());
			addr.setCty(addr.getCode().getCurrentTranslation());
		}else if(com!=null){
			if(com.getProvincia()!=null)
				addr.setCpa(com.getProvincia());
			
			addr.setZip(com.getCap());
			addr.setCty(com.getDescrizione());
		}
	}
	
	protected abstract void deleteImportedData(String ulss);

	private CodeValue getOrCreateCode(String csName, String domainName, String code, String description) {
		if(csName==null || domainName==null || code==null || description==null)
			return null;
		
		code = code.replaceAll("\\W", "").toLowerCase();

		CodeValue cv = null;
		try{
			Vocabularies voc = new VocabulariesImpl(targetEm);
			
			CodeValue domain = voc.getDomain(csName, domainName);
			CodeSystem codeSystem = null;
			queryCodeSystem.setParameter("name", csName);
			List<CodeSystem> csList = queryCodeSystem.getResultList();
			if(csList!=null && !csList.isEmpty())
				codeSystem = csList.get(0);
			
			if(domain!=null && codeSystem!=null){
				Class cvClass = Class.forName("com.phi.entities.dataTypes." + codeSystem.getCodeValueClass());
				String id = domain.getOid() + "." + code + "_V0";
				cv = (CodeValue)targetEm.find(cvClass, id);
				if(cv!=null){
					return cv;
				}
				cv = (CodeValue)cvClass.newInstance();
				
				cv.setId(id);
				cv.setOid(domain.getOid() + "." + code);
				cv.setCreator("GenericImporter");
				cv.setDefaultChild(false);
				cv.setSequenceNumber(0);
				cv.setType("C");
				cv.setValidFrom(new Date());
				cv.setValidTo(new Date());
				cv.setVersion(codeSystem.getVersion());
				cv.setCodeSystem(codeSystem);
				cv.setParent(domain);
				cv.setStatus(2);	//RETIRED
				cv.setCode(code);
				cv.setDisplayName(code);
				cv.setLangIt(description);
				cv.setDescription(description);
				cv.setChangeReason("Added importing prevnet data - ULSS "+ulss);
				cv.setRevisedDate(new Date());
			}			
		}catch (Exception e){
			log.error("Error on getOrCreateCode for code: "+ code);
		}


		return cv;
	}
}