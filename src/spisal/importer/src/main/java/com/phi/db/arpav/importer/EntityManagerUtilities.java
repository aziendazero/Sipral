package com.phi.db.arpav.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.prevnet.entities.Comuni;



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
	public Query queryLawComplete;
	public Query queryAteco;
	public Query queryStatus;
	public Query queryOrg;
	public Query querySede;
	public Query queryUlss;
	public Query queryArpav;
	public Query queryAtecoP;
	public Query queryPersonaGiuridica;
	public Query queryIndirizzoSped;
	protected static EntityManagerFactory targetEmf;
	
	public static EntityManager targetEm;	// TARGET TB

	public static enum supportedDb { mySql, oracle };	
	
	public static boolean commit = true;

	public static String ulss = "ARPAV";	//050112 = ULSS 12
	public static String distretto = "";	//ME = Mestre, DC = Dolo-Chioggia
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	//Seleted db:
	
	protected supportedDb targetDb = supportedDb.mySql; 

	public EntityManagerUtilities() {
		super();
		if(targetEm==null || !targetEm.isOpen()){
			setupEntityManagerFactory();
		}
		
		queryComuni = targetEm.createQuery("SELECT cv FROM CodeValueCity cv WHERE cv.oid = :oid");
		queryCode = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid");
		queryCodeDomain = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.code = :code AND cv.oid like :parentOid");
		queryCodeSystem= targetEm.createQuery("SELECT cs FROM CodeSystem cs WHERE cs.name = :name");
		queryCountry = targetEm.createQuery("SELECT cv FROM CodeValueCountry cv WHERE cv.oid = :oid");
		queryParameter = targetEm.createQuery("SELECT cv FROM CodeValueParameter cv WHERE cv.oid = :oid");
		queryLaw = targetEm.createQuery("SELECT cv FROM CodeValueLaw cv WHERE cv.oid = :oid");
		queryLawComplete = targetEm.createQuery("SELECT cv FROM CodeValueLaw cv JOIN cv.parent legge WHERE cv.numero = :numero AND (((cv.comma IS NULL OR cv.comma='') AND :comma='') OR cv.comma = :comma) AND (((cv.lettera IS NULL OR cv.lettera='') AND :lettera='') OR cv.lettera = :lettera) AND legge.displayName = :legge");
		queryAteco = targetEm.createQuery("SELECT cv FROM CodeValueAteco cv WHERE cv.code = :code");
		queryStatus = targetEm.createQuery("SELECT cv FROM CodeValueStatus cv WHERE cv.oid = :oid");
		queryOrg = targetEm.createQuery("SELECT o FROM Organization o WHERE o.id = :code");
		queryUlss = targetEm.createQuery("SELECT s FROM ServiceDeliveryLocation s JOIN s.organization org "
				+ "WHERE s.isActive = 1 AND org.id = :id");
		queryArpav = targetEm.createQuery("SELECT s FROM ServiceDeliveryLocation s WHERE s.internalId = :id");
		//queryArpav = targetEm.createQuery("SELECT s FROM ServiceDeliveryLocation s WHERE s.addr.cpa = :dep");
		querySede = targetEm.createQuery("SELECT o FROM Sedi o WHERE o.internalId = :id");
		queryPersonaGiuridica = targetEm.createQuery("SELECT o FROM PersoneGiuridiche o WHERE o.internalId = :id");
		queryIndirizzoSped=  targetEm.createQuery("SELECT o FROM IndirizzoSped o WHERE o.internalId = :id");
		
	
		queryAtecoP = targetEm.createQuery("SELECT ateco FROM AttivitaIstat a  "
				+ "LEFT JOIN a.sedi s "
				+ "LEFT JOIN a.personeGiuridiche p "
				+ "JOIN a.importanza imp "
				+ "JOIN a.code ateco "
				+ "WHERE a.isActive = 1 AND (s.internalId = :sediId OR p.internalId = :dittaId) AND imp.code = 'P'");
	}

	public void saveOnTarget(Object entity){
		if(commit && entity!=null){			
			targetEm.persist(entity);
		}
	}
	
	public void closeResource() {
		if(targetEm!=null && targetEm.isOpen()){
			targetEm.close();
		}
	}
	
	public CodeValueCity getComune(Comuni com){
		if(com!=null && com.getCodiceistat()!=null){
			return getComune(com.getCodiceistat());
		}
		
		return null;
	}
	
	public CodeValueCity getComune(String codiceIstat){
		if(codiceIstat!=null && !codiceIstat.trim().isEmpty()){
			String oid = "comuni."+codiceIstat; //codice istat a 6 cifre es comuni.010225
			
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
		if(oid!=null && !oid.trim().isEmpty()){
			queryCode.setParameter("oid", "phidic."+oid);
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
		if(oid!=null && !oid.trim().isEmpty()){
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
	
	public CodeValueLaw getLaw(String legge, String numero, String comma, String lettera){
		if(legge==null)
			legge="";
		if(numero==null)
			numero="";
		if(comma==null)
			comma="";
		if(lettera==null)
			lettera="";
		queryLawComplete.setParameter("legge", legge);
		queryLawComplete.setParameter("numero", numero);
		queryLawComplete.setParameter("comma", comma);
		queryLawComplete.setParameter("lettera", lettera);
		List<CodeValueLaw> list = queryLawComplete.getResultList();
		if(list!=null && !list.isEmpty()) {
			return list.get(0);
		}else {
			log.warn("Unable to find CodeValueLaw: legge:"+legge+" numero:"+numero+" comma:"+comma+" lettera:"+lettera);
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
	public Sedi findSede(String id){
		
		if(id!=null && !id.isEmpty()){
			querySede.setParameter("id", id);
			List<Sedi> list = querySede.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find Sede for id:"+id);
			}
			
		}
		
		return null;
	}
	public PersoneGiuridiche findPersonaGiuridica(String id){
		
		if(id!=null && !id.isEmpty()){
			queryPersonaGiuridica.setParameter("id", id);
			List<PersoneGiuridiche> list = queryPersonaGiuridica.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find PersonaGiuridica for id:"+id);
			}
			
		}
		
		return null;
	}
public IndirizzoSped findIndirizzoSpedPrinc(String id){
		
		if(id!=null && !id.isEmpty()){
			queryIndirizzoSped.setParameter("id", id);
			List<IndirizzoSped> list = queryIndirizzoSped.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find IndirizzoSped for id:"+id);
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
	
	public ServiceDeliveryLocation findArpav(String id){
		HashMap<String, Long> arpavMap = new HashMap<String, Long>();
		arpavMap.put("1", 226L);
		arpavMap.put("2", 224L);
		arpavMap.put("3", 225L);
		arpavMap.put("4", 227L);
		arpavMap.put("5", 223L);
		arpavMap.put("6", 228L);
		arpavMap.put("7", 229L);
		
		if(id!=null && !id.isEmpty()){
			queryArpav.setParameter("id", arpavMap.get(id));
			//queryArpav.setParameter("dep", id);
			List<ServiceDeliveryLocation> list = queryArpav.getResultList();
			if(list!=null && !list.isEmpty()){
				return list.get(0);
			}else {
				log.warn("Unable to find Ulss for id:"+id);
			}
			
		}
		
		return null;
	}
		
	protected void setupEntityManagerFactory() {

		try {
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
				addr.setCpa(com.getProvincia().getDescrizione());
			
			addr.setZip(com.getCapdelcomune());
			addr.setCty(com.getDescrizione());
		}
	}
	
}