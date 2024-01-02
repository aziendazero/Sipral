package com.phi.db.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.hibernate.engine.EntityKey;
import org.hibernate.impl.SessionImpl;
import org.hibernate.proxy.HibernateProxy;

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
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.prevnet.entities.Articolilegge;
import com.prevnet.entities.Comuni;
import com.prevnet.entities.Leggi;
import com.prevnet.entities.Tabelle;
import com.prevnet.mappings.CodeValueMapping;
import com.prevnet.mappings.TableMapping;



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
	public Query queryLawLoose;
	public Query queryLegge;
	public Query queryAteco;
	public Query queryStatus;
	public Query queryOrg;
	public Query queryUlss;
	public Query queryLinea;
	public Query queryAtecoP;
	public Query queryMaxId;
	public Query queryMaxIdLegge;
	public Query queryFascicolo;

	protected static EntityManagerFactory sourceEmf;
	protected static EntityManagerFactory targetEmf;

	public static EntityManager sourceEm;	// SOURCE DB
	public static EntityManager targetEm;	// TARGET TB
	public static EntityManager statelessSourceEm;	// STATELESS TB
	public static EntityManager statelessTargetEm;	// STATELESS TB

	public static enum supportedDb { mySql, oracle };

	public static boolean commit = true;
	public static boolean disableAur = false;
	public static String ulss = "";	//050112 = ULSS 12
	public static String distretto = "";		//ME = Mestre, DC = Dolo-Chioggia
	public static String ufficio = null;	//PER SCALIGERA: 1=VERONA, 24=LEGNAGO, 25=BUSSOLENGO



	//Seleted db:
	protected supportedDb sourceDb = supportedDb.oracle;
	protected supportedDb targetDb = supportedDb.mySql;

	public EntityManagerUtilities() {
		super();
		if(sourceEm==null || !sourceEm.isOpen() || targetEm==null || !targetEm.isOpen()
				|| statelessSourceEm==null || !statelessSourceEm.isOpen() || statelessTargetEm==null || !statelessTargetEm.isOpen()){
			setupEntityManagerFactory();
		}

		queryComuni = targetEm.createQuery("SELECT cv FROM CodeValueCity cv WHERE cv.oid = :oid");
		queryCode = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid");
		queryCodeDomain = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.code = :code AND cv.oid like :parentOid");
		queryCodeSystem= targetEm.createQuery("SELECT cs FROM CodeSystem cs WHERE cs.name = :name");
		queryCountry = targetEm.createQuery("SELECT cv FROM CodeValueCountry cv WHERE cv.oid = :oid");
		queryParameter = targetEm.createQuery("SELECT cv FROM CodeValueParameter cv WHERE cv.oid = :oid");
		queryLaw = targetEm.createQuery("SELECT cv FROM CodeValueLaw cv WHERE cv.oid = :oid");
		queryLawComplete = statelessTargetEm.createQuery("SELECT cv FROM CodeValueLaw cv JOIN cv.parent legge WHERE " +
				"cv.codeSystem.idx = 11 " + // LEGGI PEM
				"AND cv.numero = :numero " +
				"AND (((cv.comma IS NULL OR cv.comma='') AND :comma='') OR cv.comma = :comma) " +
				"AND (((cv.lettera IS NULL OR cv.lettera='') AND :lettera='') OR cv.lettera = :lettera) " +
				"AND (((cv.sanzionatoDa IS NULL OR cv.sanzionatoDa='') AND :sanzionatoDa='') OR cv.sanzionatoDa = :sanzionatoDa) " +
				"AND legge.displayName = :legge " +
				"AND cv.validFrom <= :dataRiferimento " +
				"AND (cv.validTo IS NULL OR cv.validTo >= :dataRiferimento)");
		queryLawLoose = statelessTargetEm.createQuery("SELECT cv FROM CodeValueLaw cv JOIN cv.parent legge WHERE " +
				"cv.codeSystem.idx = 11 " + // LEGGI PEM
				"AND cv.numero = :numero " +
				"AND (((cv.comma IS NULL OR cv.comma='') AND :comma='') OR cv.comma = :comma) " +
				"AND (((cv.lettera IS NULL OR cv.lettera='') AND :lettera='') OR cv.lettera = :lettera) " +
				"AND (((cv.sanzionatoDa IS NULL OR cv.sanzionatoDa='') AND :sanzionatoDa='') OR cv.sanzionatoDa = :sanzionatoDa) " +
				"AND legge.displayName = :legge");
		queryLegge = statelessTargetEm.createQuery("SELECT cv FROM CodeValueLaw cv WHERE " +
				"cv.codeSystem.idx = 11 " +
				"AND cv.type='T' " +
				"AND cv.displayName = :legge");
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

		queryAtecoP = targetEm.createQuery("SELECT ateco FROM AttivitaIstat a  "
				+ "JOIN a.sedi s "
				+ "JOIN a.importanza imp "
				+ "JOIN a.code ateco "
				+ "WHERE a.isActive = 1 AND s.internalId = :sediId AND imp.code = 'P'");

		queryMaxId= statelessTargetEm.createQuery("SELECT MAX(cv.oid) FROM CodeValueLaw cv WHERE " +
				"cv.codeSystem.idx = 11 " +
				"AND cv.type='C'");

		queryMaxIdLegge= statelessTargetEm.createQuery("SELECT MAX(cv.oid) FROM CodeValueLaw cv WHERE " +
				"cv.codeSystem.idx = 11 " +
				"AND cv.type='T' ");

		queryFascicolo= targetEm.createNativeQuery("insert into tag_fascicolo_procpratiche " +
				"select :tagId,:pratId from dual where not exists " +
				"(select t.tagFascicolo_internal_id from tag_fascicolo_procpratiche t " +
				"where t.tagFascicolo_internal_id=:tagId and t.procpratiche_internal_id=:pratId)");
	}

	public void saveOnSource(Object entity){
		if(commit && entity!=null){
			//sourceEm.getTransaction().begin();

			sourceEm.persist(entity);
			//sourceEm.flush();

			//sourceEm.getTransaction().commit();
		}
	}

	public void saveOnTarget(Object entity){
		if(commit && entity!=null){
			//targetEm.getTransaction().begin();

			targetEm.persist(entity);
			//targetEm.flush();

			//targetEm.getTransaction().commit();
		}
	}

	public Object saveOnStatelessTarget(Object entity){
		if(commit && entity!=null){
			if(!statelessTargetEm.getTransaction().isActive())
				statelessTargetEm.getTransaction().begin();
			entity = statelessTargetEm.merge(entity);
			statelessTargetEm.flush();
			statelessTargetEm.getTransaction().commit();
			statelessTargetEm.clear();
			return entity;
		}
		return null;
	}

	public void saveOnStatelessSource(Object entity){
		if(commit && entity!=null){
			if(!statelessSourceEm.getTransaction().isActive())
				statelessSourceEm.getTransaction().begin();
			statelessSourceEm.merge(entity);
			statelessSourceEm.flush();
			statelessSourceEm.getTransaction().commit();
			statelessSourceEm.clear();
		}
	}

	public void closeResource() {
		if(sourceEm!=null && sourceEm.isOpen()){
			sourceEm.close();
		}

		if(targetEm!=null && targetEm.isOpen()){
			targetEm.close();
		}

		if(statelessSourceEm!=null && statelessSourceEm.isOpen()){
			statelessSourceEm.close();
		}

		if(statelessTargetEm!=null && statelessTargetEm.isOpen()){
			statelessTargetEm.close();
		}
	}

	/*
	 * Se postfix è valorizzato, utilizzo un mapping di configurazione (che serve nell'importer per prendere determinate decisioni)
	 * Ad esempio vedi SopralluoghiImporter come gestisce source.getTipoEmergenza()
	 */
	public CodeValue getMappedCode(Tabelle code, String postfix, String ulss){
		try{
			if(code!=null){
				if(code instanceof HibernateProxy)
					code = (((Tabelle)((HibernateProxy)code).getHibernateLazyInitializer().getImplementation()));

				return getMappedCode(code.getUtcodice(),code.getCodicetabella(),code.getDescrizione(),postfix,ulss);
			}
		}catch(Exception e){
			//PREVNET_12: SOGGETTIPROVVEDIMENTO CON IDPROVVEDIMENTO=45: INTEGRITA RELAZIONALE PERSA CON TABELLE (VEDI IDRUOLO 8747)
			//do nothing.
		}


		return null;
	}


	public CodeValue getMappedCode(Tabelle code, String ulss){
		try{
			if(code!=null){
				if(code instanceof HibernateProxy)
					code = (((Tabelle)((HibernateProxy)code).getHibernateLazyInitializer().getImplementation()));

				return getMappedCode(code.getUtcodice(),code.getCodicetabella(),code.getDescrizione(),null,ulss);
			}
		}catch(Exception e){
			//PREVNET_12: SOGGETTIPROVVEDIMENTO CON IDPROVVEDIMENTO=45: INTEGRITA RELAZIONALE PERSA CON TABELLE (VEDI IDRUOLO 8747)
			//do nothing.
		}


		return null;
	}

	private CodeValue getMappedCode(String utcode, BigDecimal codiceTabella, String descrizione, String postfix, String ulss){
		if(utcode==null || utcode.isEmpty() || codiceTabella==null)
			return null;

		try {
			String hqlMap = "SELECT map FROM CodeValueMapping map "
					+ "WHERE map.codiceTabella = :codiceTabella "
					+ "AND map.utcode = :utcode ";

			if(ulss!=null){
				hqlMap += "AND map.ulss = :ulss";
			}

			Query qMap = statelessSourceEm.createQuery(hqlMap);
			if(postfix!=null){
				qMap.setParameter("codiceTabella", codiceTabella.toString()+postfix);
			}else{
				qMap.setParameter("codiceTabella", codiceTabella.toString());
			}

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
					Query qCv = statelessTargetEm.createQuery(hqlCv);
					qCv.setParameter("oid", map.getOid());
					List<CodeValue> listCv = qCv.getResultList();
					if(listCv!=null && !listCv.isEmpty()){
						CodeValue cv = listCv.get(0);
						return cv;
					}
				}
			}else if(postfix==null){//non creare code_values se postfix è valorizzato (mapping di configurazione)


				String hqlDomain = "SELECT map FROM TableMapping map "
						+ "WHERE map.codiceTabella = :codiceTabella ";

				if(ulss!=null){
					hqlDomain += "AND map.ulss = :ulss";
				}

				Query qDomain = statelessSourceEm.createQuery(hqlDomain);
				qDomain.setParameter("codiceTabella", codiceTabella.toString());
				if(ulss!=null){
					qDomain.setParameter("ulss", ulss);
				}
				List<TableMapping> listDomain = qDomain.getResultList();
				if(listDomain!=null && !listDomain.isEmpty()){
					TableMapping firstLine = listDomain.get(0);

					String domainName = firstLine.getDomain();
					String csName = firstLine.getCodeSystem();

					CodeValue cv = getOrCreateCode(csName, domainName, utcode+"imp", descrizione);//imp sta per importer
					CodeValueMapping map = new CodeValueMapping(utcode, cv.getOid(), firstLine.getCvClass(), firstLine.getCodiceTabella(), ulss);
					if(commit){
						saveOnStatelessTarget(cv);
						saveOnStatelessSource(map);
					}

					log.info("Imported NEW CodeValue for - utcode:"+utcode+" codiceTabella:"+codiceTabella+" ulss:"+ulss);
					cv = targetEm.find(CodeValue.class, cv.getId());
					return cv;
				}else {
					log.warn("Unable to find Mapped CodeValue for - utcode:"+utcode+" codiceTabella:"+codiceTabella+" ulss:"+ulss);
				}

			}
		} catch (Exception e) {
			log.error("Error getting CodeValue - utcode:"+utcode+" codiceTabella:"+codiceTabella+" ulss:"+ulss);
		}

		return null;
	}

	public CodeValueCity getComune(Comuni com){
		if(com!=null && com.getCodiceistat()!=null){
			return getComune(com.getCodiceistat());
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
				log.warn("Unable to find CodeValueLaw for oid:"+oid);
			}
		}

		return null;
	}

	public CodeValueLaw getLaw(Articolilegge art, Date dataRiferimento){

		Long leggeId = art.getLeggi().getIdleggi();
		Long artId = art.getIdarticoli();

		String lawId = "LeggiPEM."+leggeId+"."+artId;


		String legge=art.getLeggi().getCodice();
		if(legge!=null && legge.startsWith("I"))
			legge=legge.substring(1, legge.length());
		String numero=art.getNumero();
		String comma=art.getComma();
		String lettera=art.getLettera();
		String sanzionatoDa=art.getSanzionatoda();

		if(legge==null)
			legge="";
		if(numero==null)
			numero="";
		if(comma==null)
			comma="";
		if(lettera==null)
			lettera="";
		if(sanzionatoDa==null)
			sanzionatoDa="";
		if(dataRiferimento==null)
			dataRiferimento=new Date();

		queryLawComplete.setParameter("legge", legge);
		queryLawComplete.setParameter("numero", numero);
		queryLawComplete.setParameter("comma", comma);
		queryLawComplete.setParameter("lettera", lettera);
		queryLawComplete.setParameter("sanzionatoDa", sanzionatoDa);
		queryLawComplete.setParameter("dataRiferimento", dataRiferimento);
		List<CodeValueLaw> list = queryLawComplete.getResultList();

		CodeValueLaw cv = null;

		if(list!=null && !list.isEmpty()) {
			cv = list.get(0);
		}else {
			//non uso la legge ma solo gli estermi dell'articolo e la data di riferimento
			queryLawLoose.setParameter("legge", legge);
			queryLawLoose.setParameter("numero", numero);
			queryLawLoose.setParameter("comma", comma);
			queryLawLoose.setParameter("lettera", lettera);
			queryLawLoose.setParameter("sanzionatoDa", sanzionatoDa);
			list = queryLawLoose.getResultList();
			if(list!=null && !list.isEmpty()) {
				cv = list.get(0);
			}
		}

		if(cv==null){
			log.warn("Creating new articolo: legge:"+legge+" numero:"+numero+" comma:"+comma+" lettera:"+lettera);
			cv = importArticolo(art,legge);
		}

		return cv;
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

	public String getWorkingLine(int idProc, Tabelle specifica){
		CodeValue cv = null;
		switch (idProc) {
			case 3:
				//NIP
				return "phidic.spisal.pratiche.type.technicaladvice";
			case 6:
				//VIGILANZA CANTIERE
				return "phidic.spisal.pratiche.type.supervision";
			case 7:
				//MALATTIE PROFESSIONALI
				return "phidic.spisal.pratiche.type.workddisease";
			case 8:
				//NIP - LAVORATRICI MADRE
				return "phidic.spisal.pratiche.type.technicaladvice";
			case 10:
				//INFORTUNI SUL LAVORO
				return "phidic.spisal.pratiche.type.workaccident";
			case 16:
				//VIGILANZA AMIANTO
				return "phidic.spisal.pratiche.type.supervision";
			case 17:
				//ASSISTENZA
				return "phidic.spisal.pratiche.type.generic";
			case 48:
				//VIGILANZA AMIANTO
				return "phidic.spisal.pratiche.type.supervision";
			case 51:
				//FORMAZIONE,
				//INFORMAZIONE,
				//PROMOZIONE SALUTE E STILI DI VITA,
				//SPORTELLO ASCOLTO E CENTRO BENESSERE ORGANIZZATIVO,
				//ATTIVITA GENERICA

				cv = getMappedCode(specifica, ulss);	//MAPPING 364-TipoPratica
				if(cv!=null)
					return cv.getOid();

			case 53:
				//VIGILANZA CANTIERE
				return "phidic.spisal.pratiche.type.supervision";
			case 54:
				//NIP
				return "phidic.spisal.pratiche.type.technicaladvice";
			case 101:
				//MEDICINA DEL LAVORO - RICORSO AVVERSO
				return "phidic.spisal.pratiche.type.workmedicine";
			case 153:
				//MEDICINA DEL LAVORO
				return "phidic.spisal.pratiche.type.workmedicine";

			case 163:
				//VIGILANZA CANTIERE
				return "phidic.spisal.pratiche.type.generic";
			case 166:
				//ATTIVITA GENERICA
				//INFORMAZIONE
				//SPORTELLO ASCOLTO E CENTRO BENESSERE ORGANIZZATIVO
				//ATTIVITA GENERICA

				cv = getMappedCode(specifica, ulss);	//MAPPING 364-TipoPratica
				if(cv!=null)
					return cv.getOid();

			case 167:
				//COMUNICAZIONE ESPOSTI -> ATTIVITA GENERICA
				return "phidic.spisal.pratiche.type.generic";
			case 170:
				//NIP
				return "phidic.spisal.pratiche.type.technicaladvice";
			case 172:
				//ATTIVITA GENERICA
				return "phidic.spisal.pratiche.type.generic";
			case 173:
				//VIGILANZA DITTA
				return "phidic.spisal.pratiche.type.supervision";
			case 184:
				//ATTIVITA GENERICA
				return "phidic.spisal.pratiche.type.generic";
			case 194:
				//MEDICINA DEL LAVORO
				return "phidic.spisal.pratiche.type.workmedicine";
			default:
				break;
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

			if(statelessSourceEm!=null && statelessSourceEm.isOpen()){
				statelessSourceEm.close();
			}
			statelessSourceEm = sourceEmf.createEntityManager();
			statelessSourceEm.setFlushMode(FlushModeType.COMMIT);

			if(targetEm!=null && targetEm.isOpen()){
				targetEm.close();
			}
			targetEm = targetEmf.createEntityManager();
			targetEm.setFlushMode(FlushModeType.COMMIT);

			if(statelessTargetEm!=null && statelessTargetEm.isOpen()){
				statelessTargetEm.close();
			}
			statelessTargetEm = targetEmf.createEntityManager();
			statelessTargetEm.setFlushMode(FlushModeType.COMMIT);
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
		String defaultSchema = properties.getProperty("hibernate.default_schema");

		configOverrides.put("hibernate.connection.url", connetionUrl);
		configOverrides.put("hibernate.connection.driver_class", connectionDriVerClass);
		configOverrides.put("hibernate.connection.username", user);
		configOverrides.put("hibernate.connection.password", pass);
		configOverrides.put("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
		configOverrides.put("hibernate.hbm2ddl.auto", updateDb);
		configOverrides.put("hibernate.default_schema", defaultSchema);
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
				cv = (CodeValue)statelessTargetEm.find(cvClass, id);
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

	private CodeValueLaw importArticolo(Articolilegge art, String legge){
		CodeValueLaw cv = new CodeValueLaw();

		CodeSystem codeSystem = null;
		queryCodeSystem.setParameter("name", "LeggiPEM");
		List<CodeSystem> csList = queryCodeSystem.getResultList();
		if(csList!=null && !csList.isEmpty())
			codeSystem = csList.get(0);

		//0 genero l'id
		String oid = (String)queryMaxId.getSingleResult();
		if(oid==null || oid.isEmpty())
			return null;

		Integer lastId=null;
		String prefix = "";
		String[] tokens=oid.split("\\.");
		if(tokens.length>1){
			prefix=oid.substring(0, oid.lastIndexOf("."));
			try{
				lastId=Integer.parseInt(tokens[tokens.length-1]);
			}catch (NumberFormatException e){
				//nothing to do..
			}
		}

		if(lastId==null || prefix.isEmpty())
			return null;

		lastId=lastId+1;
		String oidFinale = prefix+"."+lastId;
		String idFinale = oidFinale+"_V0";

		//1 trovo la legge
		queryLegge.setParameter("legge", legge);
		List<CodeValueLaw> leggi = queryLegge.getResultList();

		CodeValueLaw parent = null;
		if(leggi!=null && !leggi.isEmpty()) {
			parent = leggi.get(0);
		}else {
			parent = importLegge(art.getLeggi(), codeSystem);
		}

		if(parent==null)
			return null;

		cv.setId(idFinale);
		cv.setChangeReason("Importer import");
		cv.setCode(ulss+distretto+lastId);
		cv.setCreator("Importer_"+ulss+"_"+distretto);
		cv.setDefaultChild(false);
		cv.setDescription(art.getTitolo());
		String displayName="";
		if(art.getNumero()!=null)
			displayName+=art.getNumero();
		if(art.getComma()!=null)
			displayName+=" - "+art.getComma();
		if(art.getLettera()!=null)
			displayName+=" - "+art.getLettera();

		displayName+=" - "+art.getTitolo();
		cv.setDisplayName(displayName);
		cv.setOid(oidFinale);
		cv.setRevisedDate(new Date());
		cv.setSequenceNumber(0);
		cv.setStatus(2);
		cv.setType("C");
		cv.setValidFrom(new Date());
		cv.setValidTo(new Date());
		cv.setCodeSystem(codeSystem);
		cv.setArresto(art.getArresto());
		if(art.getContravventore()!=null){
			cv.setCodiceContravventore(art.getContravventore().getUtcodice());
			cv.setDescrizioneContravventore(art.getContravventore().getDescrizione());
		}

		cv.setComma(art.getComma());
		cv.setCorpo(art.getCorpo());
		if(art.getImportomax()!=null)
			cv.setImportoMax(art.getImportomax().toPlainString());
		if(art.getImportomin()!=null)
			cv.setImportoMin(art.getImportomin().toPlainString());
		cv.setLettera(art.getLettera());
		//cv.setNotaContravventore();

		String violazioni = null;
		if(art.getNoteprescr()!=null){
			int length = art.getNoteprescr().length();
			violazioni = art.getNoteprescr().substring(0,length<=255?length:255);
		}
		cv.setNotaViolazione(violazioni);

		String prescrizioni = null;
		if(art.getNoteprescr()!=null){
			int length = art.getNoteprescr().length();
			prescrizioni = art.getNoteprescr().substring(0,length<=255?length:255);
		}
		cv.setNotaPrescrizione(prescrizioni);

		cv.setNumero(art.getNumero());
		cv.setSanzionatoDa(art.getSanzionatoda());
		cv.setSanzione(art.getSanzione());
		cv.setParent(parent);

		saveOnStatelessTarget(cv);

		return cv;
	}

	private CodeValueLaw importLegge(Leggi legge, CodeSystem codeSystem){

		CodeValueLaw cv = null;

		if(legge!=null){
			cv=new CodeValueLaw();


			//0 genero l'id
			String oid = (String)queryMaxIdLegge.getSingleResult();
			if(oid==null || oid.isEmpty())
				return null;

			Integer lastId=null;
			String prefix = "";
			String[] tokens=oid.split("\\.");
			if(tokens.length>1){
				prefix=oid.substring(0, oid.lastIndexOf("."));
				try{
					lastId=Integer.parseInt(tokens[tokens.length-1]);
				}catch (NumberFormatException e){
					//nothing to do..
				}
			}

			if(lastId==null || prefix.isEmpty())
				return null;

			lastId=lastId+1;
			String oidFinale = prefix+"."+lastId;
			String idFinale = oidFinale+"_V0";

			cv.setId(idFinale);
			cv.setChangeReason("Importer import");
			cv.setCode(ulss+distretto+lastId);
			cv.setCreator("Importer_"+ulss+"_"+distretto);
			cv.setDefaultChild(false);
			cv.setDescription(legge.getDescrizione());
			cv.setDisplayName(legge.getCodice());
			cv.setOid(oidFinale);
			cv.setRevisedDate(new Date());
			cv.setSequenceNumber(0);
			cv.setStatus(2);
			cv.setType("T");
			cv.setValidFrom(new Date());
			cv.setValidTo(new Date());
			cv.setCodeSystem(codeSystem);

			cv.setNumero(legge.getCodice());
			saveOnStatelessTarget(cv);
		}

		return cv;
	}

	public List findMapping(String className){
		Map<EntityKey, Object> map = ((SessionImpl)sourceEm.getDelegate()).getPersistenceContext().getEntitiesByKey();
		List lst = new ArrayList();
		for(EntityKey str : map.keySet()){
			if(str.getEntityName().equals(className)){
				lst.add(map.get(str));
			}
		}

		return lst;


	}
}