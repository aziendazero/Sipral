package com.phi.cs.vocabulary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.parameters.ParameterManager;
import com.phi.security.UserBean;

/**
 * Manages dictionary values
 * @author Alex Zupan
 */

@Stateless
@BypassInterceptors
@Name("vocabularies")
public class VocabulariesImpl implements Serializable, Vocabularies {

	private static final long serialVersionUID = -6176906022585185710L;
	private static final Logger log = Logger.getLogger(VocabulariesImpl.class);

	protected EntityManager em;

	private static final String SEPARATOR = "_";
	private static final String VOCAB_EQ ="EQ";

	private static final int CV_CV = 0;
	//	private static final int CV_OID = 0;
	//	private static final int CV_CODE = 1;
	//	private static final int CV_CODESYSTEM_ID = 2;
	//	private static final int CV_TYPE = 3;
	private static final int CV_CE_CODE = 1;

	public static final int CODED_PROPOSED = 0;
	public static final int CODED_ACTIVE = 1;
	public static final int CODED_RETIRED = 2;
	public static final int CODED_DELETED = 3;
	public static final String[] CODED_ALLSTATUS = { "PROPOSED", "ACTIVE", "RETIRED", "DELETED" };

	//	private static final String PHI_DIC = "2.16.840.1.113883.3.20";


	private static WeakHashMap<String, Object[]> cacheDomainIds = null;
	private static WeakHashMap<String, CodeValue> cacheDomain = null;
	private boolean useExternalEM = false;
	private boolean useCache = false;

	private ThreadLocal<WeakHashMap<String, List<SelectItem>>> cacheDomains = null;

	private static final String QRY_BY_NAME_AND_CODE_SYSTEM = "SELECT code.oid, codSys.version, codSys.codeValueClass FROM CodeValue code JOIN code.codeSystem codSys " +
			"WHERE code.status = "+CODED_ACTIVE+" AND code.type <> 'C' " +
			"AND code.displayName =:display AND codSys.name=:name " +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :name AND cssub.status = "+CODED_ACTIVE+") ";

	private static final String QRY_BY_CODE_SYSTEM = "SELECT codeSystem.id, codeSystem.version, codeSystem.codeValueClass FROM CodeSystem codeSystem " +
			"WHERE codeSystem.name = :name " +
			"AND codeSystem.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :name AND cssub.status = "+CODED_ACTIVE+") ";

	private static final String QRY_CHILDS = "SELECT code.id, case when code.langIt is not null then code.langIt else code.displayName end, " +
			"code.validTo, code.status FROM CodeValue code "; 

	private static final String QRY_TRANSCODED_CHILDS = "SELECT code.id, case when code.langIt is not null then code.langIt else code.displayName end, " +
			"code.validTo, code.status, codeEquivalent.code FROM CodeValue code " +
			"LEFT JOIN code.codeEquivalentsForCodeValueTarget codeEquivalents " +
			"LEFT JOIN codeEquivalents.codeValueByCodeValueSource codeEquivalent " +
			"LEFT JOIN codeEquivalent.codeSystem codeSystem WITH codeSystem.name = :transcodingDomain "; 

	private static final String QRY_EQUIVALENT_CHILDS = "SELECT code.id, case when code.langIt is not null then code.langIt else code.displayName end, " +
			"code.validTo, code.status, codeEquivalent.code, codeEquivalent.displayName FROM CodeValue code " +
			"LEFT JOIN code.codeEquivalentsForCodeValueTarget codeEquivalents " +
			"LEFT JOIN codeEquivalents.codeValueByCodeValueSource codeEquivalent "; 

	private static final String QRY_ADDITIONAL_VALUES = "SELECT code FROM CodeValue code WHERE code.id in "; //FIXME

	private static final String QRY_WHERE_TYPE = " code.type = :type AND ";
	private static final String QRY_WHERE_PARENT = " code.id LIKE :parentId AND ";
	private static final String QRY_WHERE_CHILD = " code.id NOT LIKE :childId AND ";
	private static final String QRY_WHERE_CODE_STATUS_ACTIVE = "code.status="+CODED_ACTIVE;
	private static final String QRY_ORDER = "ORDER BY code.defaultChild, code.sequenceNumber, code.langIt, code.displayName";
	private static final String QRY_ORDER_OID = "ORDER BY code.oid";

	private static final String QRY_SINGLE_CV ="SELECT cv FROM CodeValue cv WHERE cv.code = :code AND cv.id LIKE :parentId";
	private static final String QRY_SINGLE_CV_FOR_PROPERTY ="SELECT cv FROM CodeValue cv WHERE cv.type = 'C' AND cv.id LIKE :parentId";
	private static final String QRY_TYPE_CV = " AND cv.type = :type";
	private static final String QRY_MULTIPLE_CV ="SELECT cv FROM CodeValue cv WHERE cv.type = 'C' AND cv.id LIKE :parentId AND cv.code in (:listCode)";
	private static final String QRY_MULTIPLE_CV_IN_DOMAIN ="SELECT cv FROM CodeValue cv WHERE cv.type = 'C' AND cv.status = "+CODED_ACTIVE+" AND cv.id LIKE :parentId";

	private static final String QRY_MULTIPLE_CV_FOR_SELECT_ITEM = "SELECT cv.id, CASE WHEN cv.langIt IS NOT NULL THEN cv.langIt ELSE cv.displayName END, cv.defaultChild, cv.sequenceNumber " +
			"FROM CodeValue cv WHERE cv.type = 'C' AND cv.id LIKE :parentId AND cv.code in (:listCode) ORDER BY cv.defaultChild, cv.sequenceNumber, cv.langIt, cv.displayName";

	private static final String QRY_MULTIPLE_CV_FOR_SELECT_ITEM_NO_DOMAIN = "SELECT cv.id, CASE WHEN cv.langIt IS NOT NULL THEN cv.langIt ELSE cv.displayName END, cv.defaultChild, cv.sequenceNumber " +
			"FROM CodeValue cv WHERE cv.type = 'C' AND cv.codeSystem.name = :codeSystemName AND cv.code in (:listCode) ORDER BY cv.defaultChild, cv.sequenceNumber, cv.langIt, cv.displayName";

	private static final String QRY_SINGLE_DOMAIN = "SELECT code FROM CodeValue code JOIN code.codeSystem codSys " +
			"WHERE code.status = 1 AND code.type <> 'C' " +
			"AND code.displayName = :domainName AND codSys.name= :codeSystemName " +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName AND cssub.status = 1) ";

	/* new */
	//	private static final String QRY_CODE_VALUE_GIVEN_ID = "select cv from CodeValue cv where cv.id = :id ";

	private static final String QRY_LAST_CODE_VALUE_GIVEN_OID = "select cv from CodeValue cv where cv.oid = :oid " +
			"and cv.version = (select max(cv.version) from CodeValue cv where cv.oid = :oid)";
	//	
	//	private static final String QRY_LAST_CODE_SYSTEM_ID_GIVEN_NAME ="SELECT cs.idx FROM CodeSystem cs " +
	//			"WHERE cs.name =:codeSystemName " +
	//			"AND cs.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName) ";
	//	
	//	private static final String QRY_LAST_CODE_SYSTEM_GIVEN_NAME ="SELECT cs FROM CodeSystem cs " +
	//			"WHERE cs.name =:codeSystemName " +
	//			"AND cs.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName) ";
	//	
	//	private static final String QRY_OID_DOMAIN_GIVEN_IDCS_DOMAINNAME="SELECT cv.oid FROM CodeValue cv " +
	//			"WHERE  (cv.type = 'T' or cv.type = 'S') and cv.codeSystem.idx = :idCodeSystem and cv.displayName= :domainName ";
	//	
	//	private static final String QRY_CODE_VALUE_GIVEN_IDCS_OIDDOMAIN_CODE = "select cv from CodeValue cv where " +
	//			"cv.type = 'C' and cv.codeSystem.idx = :idCodeSystem and cv.oid like :oidDomain and cv.code = :code";
	//	
	//	private static final String QRY_TOPDOMAIN_LIST_GIVEN_CSNAME = "select cv from CodeValue cv where " +
	//			"cv.codeSystem.name = :codeSystemName and cv.parent = null " +
	//			"order by cv.displayName"; 
	//	
	//	//usable only for codeSystem where the code is unique in the entire Code System.
	//	private static final String QRY_CODE_VALUE_GIVEN_IDCS_CODE = "select cv from CodeValue cv where " +
	//				"cv.type = 'C' and cv.codeSystem.idx = :idCodeSystem and cv.code = :code";

	//private String currentLang;

	//Code Value types:
	public static String TOP_LEVEL = "T";
	public static String SPECIALIZED_DOMAIN = "S";
	public static String ABSTRACT_DOMAIN = "A";
	public static String CONCEPT_LEAF = "C";


	public VocabulariesImpl() {
		//		currentLang = Locale.instance().getLanguage();

		//Global id cache
		if(useCache){
			if (cacheDomainIds == null){
				cacheDomainIds = new WeakHashMap<String, Object[]>(1000);
			}

			if (cacheDomain == null){
				cacheDomain = new WeakHashMap<String, CodeValue>(100);
			}

			//Thread local domain cache
			if (cacheDomains == null) {
				cacheDomains = new ThreadLocal<WeakHashMap<String, List<SelectItem>>>();
				cacheDomains.set(new WeakHashMap<String, List<SelectItem>>());
				//			cacheDomains = new WeakHashMap<String, List<SelectItem>>();
			}
		}
	}

	public void cleanCaches() {

		if (cacheDomainIds != null) {
			cacheDomainIds.clear();
		}
		if (cacheDomain != null) {
			cacheDomain.clear();
		}
		if (cacheDomains != null && cacheDomains.get()!=null) {
				cacheDomains.get().clear();
		}
	}

	public static Vocabularies instance() {
		return (Vocabularies) Component.getInstance(VocabulariesImpl.class, ScopeType.STATELESS);
	}

	public VocabulariesImpl (EntityManager em) {
		this();
		this.em = em;
		useExternalEM=true;
	}

	//public List<SelectItem> getIdValues(String vocabName, String moreValue) throws Exception {
	//	return getValues(vocabName, "C", -1, new String[]{moreValue}, false);
	//}

	//	public List<SelectItem> getIdValues(String vocabName, String[] moreValues) throws Exception {
	//    	return getValues(vocabName, "C", -1, moreValues);
	//	}

	public List<SelectItem> getIdValues(String vocabName) throws Exception {
		return getValues(vocabName, "C", -1, null, false);
	}
	public List<SelectItem> getIdValues(String vocabName, boolean retired) throws Exception {
		return getValues(vocabName, "C", -1, null, false, retired, false);
	}
	public List<SelectItem> getIdValues(String vocabName, boolean retired, boolean deleted) throws Exception {
		return getValues(vocabName, "C", -1, null, false, retired, deleted);
	}

	public List<SelectItem> getLazyIdValues(String vocabName) throws Exception {
		return getValues(vocabName, "", -1, null, true);
	}

	//	public List<SelectItem> getIdValues(String vocabName, int version) throws Exception {
	//    	return getValues(vocabName, "C", version, null);
	//	}

	public List<SelectItem> getSpecializedIdValues(String vocabName) throws Exception {
		return getValues(vocabName, "S", -1, null, false);
	}

	//	public List<SelectItem> getSpecializedIdValues(String vocabName, int version) throws Exception {
	//		return getValues(vocabName, "S", version, null);
	//	}

	public List<SelectItem> getAbstractIdValues(String vocabName) throws Exception {
		return getValues(vocabName, "A", -1, null, false);
	}

	private List<SelectItem> getValues(String vocabName, String type, int version, String [] moreValues, boolean lazy) throws DictionaryException {
		return getValues(vocabName, type, version, moreValues, lazy, false, false);
	}


	/** 
	 * @param vocabName - Dictionary name
	 * @param selectingId - tells if we want to create a combo with code or id as value
	 * @param type - coded value type [C,S,A,T]
	 * @param version - Code System version to get. If this value is -1 queries will be performed using max available version
	 * @param moreValues -  additional list element to be inserted in the returning list (for example old catalog values not more selectable, but need to be displayed as current values)
	 * @param lazy - boolean to retrieve a code tree lazily
	 * @return a SelectItem list usable with comboBoxes, listBoxes and other coded value dependant widgets
	 * @throws Exception
	 */
	private List<SelectItem> getValues(String vocabName, String type, int version, String [] moreValues, boolean lazy, boolean retired, boolean deleted) throws DictionaryException {

		//		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);

		List<SelectItem> values = new ArrayList<SelectItem>();

		try {

			//Three temporary data structure to store id, labels and mapping between them from current dictionary,
			//They will be compared with last value added as moreValues, to avoid duplicate entry in the list.

			List<String> temp_ids = new ArrayList<String>();
			List<String> temp_labels = new ArrayList<String>();
			//HashMap<String,String> temp_mapping = new HashMap<String,String>();

			String transcodingDomain = "";
			boolean transcoded = false;
			boolean equivalent = false;
			boolean lookForDeleted = deleted;
			boolean lookForRetired = retired;
			
			

			String originalVocDomain = vocabName;
			if (vocabName==null) {
				return values;
			}

			int separatorIndex = vocabName.indexOf(SEPARATOR);
			if (separatorIndex > -1 && separatorIndex < vocabName.indexOf(":")) {
				transcodingDomain = vocabName.substring(vocabName.indexOf(SEPARATOR) + 1);
				vocabName = vocabName.substring(0, vocabName.indexOf(SEPARATOR));
				if (transcodingDomain.startsWith("[") && transcodingDomain.contains("]")) {
					char c = transcodingDomain.charAt(1);
					switch (c) {
					case 'D':
						lookForDeleted = true;
						break;
					case 'R':
						lookForRetired = true;
						break;
					case 'A':
						lookForDeleted = true;
						lookForRetired = true;
						break;
					default:
						break;
					}
					transcodingDomain = transcodingDomain.substring(transcodingDomain.indexOf("]")+1);
				}
				if (transcodingDomain.equals(VOCAB_EQ))
					equivalent = true;
				//FIXME:ADDED a checks about some possible wrong name derived from HL7 voc domain that start with x_ or _
				else if (!transcodingDomain.isEmpty() && !vocabName.equals("x")){
					transcoded = true;
				} else if (!lookForDeleted && !lookForRetired){
					transcodingDomain="";
					vocabName=originalVocDomain;
				}
			}
			
			Boolean histo = (Boolean)Contexts.getConversationContext().get("history");
			if(histo==null){
				histo = (Boolean) ParameterManager.instance().getParameter("p.general.enablecvhistory", "value");
			}
			if(Boolean.TRUE.equals(histo)){
				lookForDeleted = true;
				lookForRetired = true;
			}

			String parentId = null;
			String codeValueClass = null;

			String[] codeSystemAndDomainName = vocabName.split(":");

			String codeSystemName = codeSystemAndDomainName[0];
			String domainName = null;
			if (codeSystemAndDomainName.length == 2) {
				domainName = codeSystemAndDomainName[1];
			}

			Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, domainName);

			if (csAndVersion == null) {
				return values;
			}

			parentId = csAndVersion[0].toString();

			if (version == -1 && csAndVersion[1] != null) {
				version = (Integer)csAndVersion[1];
			}

			codeValueClass = csAndVersion[2].toString();

			if (useCache && cacheDomains.get().containsKey(vocabName+":"+type+":"+version) ) {
				values = cacheDomains.get().get(vocabName+":"+type+":"+version);
			} else {

				String csName = "";
				String where_qry_version = "code.version = ";
				if (vocabName.contains(".")) {
					where_qry_version += (version > -1 ? version : "(SELECT MAX(cv.version) FROM CodeValue as cv WHERE cv.codeSystem.status = "+CODED_ACTIVE+" AND cv.id");
					csName = vocabName;
				} else {
					where_qry_version += (version > -1 ? version : "(SELECT MAX(cs.version) FROM CodeSystem as cs WHERE cs.status = "+CODED_ACTIVE+" AND cs.name");
					if (vocabName.contains(":"))
						csName = vocabName.split(":")[0];
					else
						csName = "HL7";
				}
				where_qry_version += (version == -1 ? " = :codeSystemName)" : "") + " AND ";

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date todayDate = new Date();
				//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String today = sdf.format(todayDate);
				
				Query qry;

				String query = "";
				if (equivalent) {
					query = QRY_EQUIVALENT_CHILDS;
				} else if (transcoded) {
					query = QRY_TRANSCODED_CHILDS;
				} else {
					query = QRY_CHILDS;
				}

				String currentLang = Locale.instance().getLanguage();

				Class<?> cvClass = CodeValue.class;

				if (!cvClass.getSimpleName().equals(codeValueClass)) {
					query = query.replace("CodeValue", codeValueClass);
					cvClass = Class.forName("com.phi.entities.dataTypes." + codeValueClass);
				}

				query += " WHERE ";
				if (!lazy){
					query += QRY_WHERE_TYPE + QRY_WHERE_PARENT;
				} else {
					query += QRY_WHERE_PARENT + QRY_WHERE_CHILD;
				}

				query += where_qry_version;	

				query += (lookForDeleted || lookForRetired ? "(" : "") + QRY_WHERE_CODE_STATUS_ACTIVE + (lookForRetired ? " OR code.status="+CODED_RETIRED+" OR code.validTo<'"+today+"' ":"") + (lookForDeleted ? " OR code.status="+CODED_DELETED+" ":"") + (lookForDeleted || lookForRetired ? ") " : "")+ " ";
				if (!lazy){
					query += QRY_ORDER;
				} else {
					query += QRY_ORDER_OID;
				}

				if (!currentLang.equals("it")) {
					query = query.replace("code.langIt", "code.lang" + WordUtils.capitalize(currentLang));
				}

				CatalogAdapter ca = null;
				if(!useExternalEM){
					ca = CatalogPersistenceManagerImpl.instance();
					qry = ca.createQuery(query);
				}else{
					qry = em.createQuery(query);
				}
				

				qry.setParameter("parentId", parentId + ".%");
				

				if (!lazy) {
					qry.setParameter("type", type);
				}
				if (lazy) {
					qry.setParameter("childId", parentId + ".%.%");
				}			
				if (version == -1) {
					qry.setParameter("codeSystemName", csName);
				}
				if (transcoded) {
					qry.setParameter("transcodingDomain", transcodingDomain);
				}
				
				qry.setHint("org.hibernate.cacheable", Boolean.TRUE);
				qry.setHint("org.hibernate.cacheRegion", "VocabulariesCache");

				List<Object[]> qryResults = qry.getResultList();

				if (qryResults.isEmpty()){
					log.error("ERR: VOCABULARY DOMAIN " + vocabName + " REQUIRED DOES NOT EXIST. ORIGINAL VOC DOMAIN NAME: "+ originalVocDomain + ", TRASCONDING VOC DOMAIN NAME:"+ transcodingDomain);
					if(!useExternalEM)
						FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, vocabName);

					return values;
				}

				for (Object[] result : qryResults) {

					String id = (String)result[0];
					String label = (String)result[1];
					Date validToDate = (Date)result[2];
					Integer status = (Integer)result[3];

					String prefix = status != CODED_ACTIVE ? "["+CODED_ALLSTATUS[status].substring(0, 1)+"] " : (validToDate != null && validToDate.before(todayDate) ? "[R] " : "");
					if(Boolean.TRUE.equals(histo)){
						prefix = "";
					}
					
					SelectItem selItem = null;
					if(!useExternalEM){
						selItem = new SelectItem(ca.load(cvClass, id), prefix + label);
					}else{
						selItem = new SelectItem(em.find(cvClass, id), prefix + label);
					}
					if(status!=CODED_ACTIVE || (validToDate != null && validToDate.before(todayDate))){
						selItem.setDisabled(true);
					}
					if (transcoded) {
						String transcodedValue = (String)result[4];
						if (transcodedValue != null) {
							selItem.setLabel(selItem.getLabel() + " (" + transcodedValue + ")");
						}
					}
					values.add(selItem);

					//store values.
					temp_ids.add(id);
					temp_labels.add(label);
					//temp_mapping.put(cv[CV_ID].toString(),selItem.getLabel());

				}

				if (moreValues != null && moreValues.length > 0) {
					//add to List<SelectItem> values, the values contained in String[] moreValues, except for already inserted code.
					String listIdForQuery="("; 
					for (String moreValue : moreValues)  
						if (moreValue != null && !moreValue.equals("") && !temp_ids.contains(moreValue)) 
							listIdForQuery+="'"+moreValue+"', ";

					listIdForQuery+=")";
					listIdForQuery = listIdForQuery.replace(", )", ")"); //remove last ','

					if (!listIdForQuery.equals("()")) {
						query = QRY_ADDITIONAL_VALUES + listIdForQuery;
						if(!useExternalEM){
							qry = ca.createQuery(query);
						}else{
							qry = em.createQuery(query);
						}
						qry.setParameter("lang", currentLang);
						
						qry.setHint("org.hibernate.cacheable", Boolean.TRUE);
						qry.setHint("org.hibernate.cacheRegion", "VocabulariesCache");
						
						qryResults = qry.getResultList();

						for (Object result : qryResults) {
							CodeValue cv = null;
							Object[] res = new Object[2];
							if (result instanceof CodeValue) {
								cv = (CodeValue)result;	
							} else {
								res = (Object[])result;
								cv = (CodeValue)res[CV_CV];
							}

							Date validToDate = cv.getValidTo();
							Integer status = cv.getStatus();
							String prefix = status != CODED_ACTIVE ? "["+CODED_ALLSTATUS[status].substring(0, 1)+"] " : (validToDate != null && validToDate.before(todayDate) ? "[R] " : "");

							SelectItem selItem = new SelectItem(cv.getId(), prefix + cv.getDisplayName());
							if(status!=CODED_ACTIVE || (validToDate != null && validToDate.before(todayDate))){
								selItem.setDisabled(true);
							}
							if (!equivalent && cv.getTranslation(currentLang) != null)
								selItem.setLabel(cv.getTranslation(currentLang));
							if (transcoded && res[CV_CE_CODE] != null)
								selItem.setLabel(selItem.getLabel() + " (" + res[CV_CE_CODE].toString() + ")");

							//add the selectItem to values only if the label found for moreValue is not already present in values.
							if (!temp_labels.contains(selItem.getLabel())) 
								values.add(selItem);
						}
						//SelectItem selItem = new SelectItem(cv[CV_ID].toString(), prefix + cv[CV_DISPLAY_NAME].toString());
					}
				}

				if(useCache)
					cacheDomains.get().put(vocabName+":"+type+":"+version, values);

			}
		} catch(DictionaryException e) {
			throw e;
		} catch(Exception e) {
			log.error("Error finding " + vocabName, e);
			if(!useExternalEM)
				FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Error finding " + vocabName);
		}

		return values;
	}

	private Object[] getCodeSystemAndVersionFromCache(String codeSystemName, String domainName) throws DictionaryException {
		Object[] csAndVersion = null;

		String cacheKey = codeSystemName + domainName;

		if (useCache && cacheDomainIds.containsKey(cacheKey) ) {
			csAndVersion = cacheDomainIds.get(cacheKey);
		} else {

			csAndVersion = findIdAndVersion(codeSystemName, domainName);
			if (csAndVersion == null) {
				log.error("ERR: VOCABULARY DOMAIN " + domainName + " of code system " + codeSystemName + " REQUIRED DOES NOT EXIST.");
				if(!useExternalEM)
					FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, cacheKey);
				return null;
			}
		}
		if(useCache)
			cacheDomainIds.put(cacheKey, csAndVersion);

		return csAndVersion;
	}

	@SuppressWarnings("unchecked")
	private Object[] findIdAndVersion(String codeSystemName, String domainName) throws DictionaryException {
		Query q;

		CatalogAdapter ca = null;
		if (!useExternalEM) {
			ca = CatalogPersistenceManagerImpl.instance();
		}

		if (domainName == null || domainName.isEmpty()) {
			q = useExternalEM ? em.createQuery(QRY_BY_CODE_SYSTEM) : ca.createQuery(QRY_BY_CODE_SYSTEM);
			q.setParameter("name", codeSystemName);
		} else {
			String qry = QRY_BY_NAME_AND_CODE_SYSTEM;
			if ("PHIDIC".equals(codeSystemName)) { //FIXMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
				qry = qry.replace("CodeValue", "CodeValuePhi");
			}  
			q = useExternalEM ? em.createQuery(qry) : ca.createQuery(qry);
			q.setParameter("name", codeSystemName);
			q.setParameter("display", domainName);
		}
		
		q.setHint("org.hibernate.cacheable", Boolean.TRUE);
		q.setHint("org.hibernate.cacheRegion", "VocabulariesCache");

		List<Object[]> results = q.getResultList();

		if (results.isEmpty()) {
			return null;
		}

		if (results.size() > 1) {
			log.error("Duplicate domain " + codeSystemName);
			throw new DictionaryException("Duplicate domain " + codeSystemName);
		}

		return results.get(0);

	}


	public List<SelectItem> entityToSelectItem (Collection entities, String el4label) throws PhiException {
		return entityToSelectItem(entities, el4label, false);
	}

	public List<SelectItem> entityToSelectItem (Collection entities, String el4label, boolean alphaSort) throws PhiException {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();

		try {
			if (entities != null && !entities.isEmpty()) {

				String labelStr = null;

				if (BaseEntity.getDerivedClasses().containsValue(entities.iterator().next().getClass()) || 
						CodeValue.getDerivedClasses().containsValue(entities.iterator().next().getClass())) {
					for (Object entity : entities) {
						Object label = PropertyUtils.getProperty(entity, el4label);
						labelStr = (label != null ? label.toString() : entity.toString());

						SelectItem selItem = new SelectItem(entity, labelStr);
						selectItems.add(selItem);
					}
				} else {

					CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

					for (Map<String, Object> map : (Collection<Map>)entities) {

						String entityName = (String)map.get("entityClass");
						Serializable id = (Serializable)map.get("internalId");

						Object label = null;

						if (el4label.contains(".")) {
							String[] els = el4label.split("\\.");
							Map<String, Object> innerMap = map;
							for (int z = 0; z<els.length-1; z++) {
								innerMap = (Map)innerMap.get(els[z]);
							}
							if (innerMap != null) {
								label = innerMap.get(els[els.length-1]);
							}
						} else {
							label = map.get(el4label);
						}
						labelStr = (label != null ? label.toString() : map.toString());

						SelectItem selItem = new SelectItem(ca.load(entityName, id), labelStr);
						selectItems.add(selItem);
					}
				}
			}

			if (alphaSort) {
				Collections.sort(selectItems, new Comparator<SelectItem>() {
					public int compare(SelectItem item1, SelectItem item2) {
						if (item1 == null && item2 == null)
							return 0;
						else if (item1 == null)
							return -1;
						else if (item2 == null)
							return 1;

						String label1 = item1.getLabel();
						String label2 = item2.getLabel();

						if (label1 == null && label2 == null)
							return 0;
						else if (label1 == null)
							return -1;
						else if (label2 == null)
							return 1;

						return (label1.compareToIgnoreCase(label2));
					}
				});
			}
		} catch (Exception e) {
			throw new PhiException("Error converting collection of entities or collection of maps to selectitem", e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_CODE);
		}

		return selectItems;
	}

	public List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues , boolean addCodeToLabel) {
		return attributeToSelectItem ( codeValues, null, addCodeToLabel );
	}

	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues) {
		return attributeToSelectItem ( codeValues, null, false);
	}

	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues, String defaultCode) {
		return attributeToSelectItem ( codeValues, defaultCode, false );
	}

	public  List<SelectItem> attributeToSelectItem ( Collection<CodeValue> codeValues, String defaultCode, boolean addCodeToLabel ) {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();

		if (codeValues == null )
			return selectItems;

		if (defaultCode != null && !defaultCode.isEmpty() ) {
			CodeValue defaultCV = getFromList (codeValues,  defaultCode);
			if (defaultCV != null) {
				String label = defaultCV.getCurrentTranslation();
				if (addCodeToLabel) {
					label = "["+defaultCV.getCode()+"] "+label;
				}
				SelectItem selItem = new SelectItem(defaultCV , label);
				selectItems.add(selItem);
			}
		}

		for (CodeValue cv : codeValues) {
			if (cv != null && (defaultCode == null || !cv.getCode().equals(defaultCode))) {
				String label = cv.getCurrentTranslation();
				if (addCodeToLabel) {
					label = "["+cv.getCode()+"] "+label;
				}
				SelectItem selItem = new SelectItem(cv , label);
				selectItems.add(selItem);
			}
		}
		return selectItems;
	}

	//	public  List<SelectItem> attributeToSelectItem (Collection<CodeValue>... codeValues) {
	//		List<CodeValue> codeValuesTotal =  new ArrayList<CodeValue>();
	//		
	//		for (Collection<CodeValue> cvs : codeValues) {
	//			codeValuesTotal.addAll(cvs);
	//		}
	//		
	//		return attributeToSelectItem(codeValuesTotal);
	//	}

	public List<SelectItem> stringListToSelectItem ( List<String> strings) {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		if (strings == null || strings.isEmpty()) {
			return selectItems;
		}

		for (String s: strings) {
			SelectItem selItem = new SelectItem(s, s);
			selectItems.add(selItem);
		}
		return selectItems;
	}

	/**
	 * Please use a method in custom action, this method will be removed.
	 */

	@Deprecated
	public boolean containsCode (Collection<CodeValue> codeValueList, String code) {

		if (codeValueList == null || code == null || code.isEmpty())
			return false;
		//List<CodeValue> codeValues = new ArrayList<CodeValue>(codeValueList);

		for (CodeValue cv : codeValueList) {
			if (cv != null && cv.getCode() != null && cv.getCode().equals(code))
				return true;
		}

		return false;
	}


	private CodeValue getFromList (Collection<CodeValue> codeValueList, String code) {

		if (codeValueList == null || code == null || code.isEmpty())
			return null;

		for (CodeValue cv : codeValueList) {
			if (cv != null && cv.getCode() != null && cv.getCode().equals(code))
				return cv;
		}

		return null;
	}







	/*
	 * return a CodeValue from dictionary, gived its oid.
	 * return null if code is not found.
	 * warn: it return also cancelled code, or not more valid in term of validity date
	 */
	public CodeValue getCodeValueOid(String oid) throws PersistenceException{

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("oid", oid);
		List<CodeValue> cvs = (List<CodeValue> )executeQuery(QRY_LAST_CODE_VALUE_GIVEN_OID,pars);

		if (cvs== null || cvs.size() != 1 || cvs.get(0) == null){
			log.error("Error reading code value for oid "+oid+": returned null or multiple result");
			//			throw new IllegalArgumentException("Error reading code value for oid " + oid + ": returned null or multiple result");
			//			FacesMessages.instance().add("Error reading code value for oid "+oid+": returned null or multiple result");
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "OID: " + oid + " not found or multiple result");
			return null;
		}

		CodeValue cv = cvs.get(0);
		if (cv.getStatus() != CODED_ACTIVE) {
			log.warn("requested code with oid "+oid+" code "+cv.getCode()+ " and display name "+ cv.getDisplayName() + " found but is not active!");
		}

		return cv;
	}

	/**
	 * Returns leaf code values
	 */
	public CodeValue getCodeValueCsDomainCode(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException {
		return getCodeValue(codeSystemName, domainName, code, CONCEPT_LEAF);
	}

	public CodeValue getCodeValueCsDomainCodeDisableErrors(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException {
		return getCodeValue(codeSystemName, domainName, code, CONCEPT_LEAF, true);
	}

	/**
	 * Returns specialized domains code values
	 */
	public CodeValue getSpecializedCodeValue(String codeSystemName, String domainName, String code) throws PersistenceException, DictionaryException {
		return getCodeValue(codeSystemName, domainName, code, SPECIALIZED_DOMAIN);
	}

	/**
	 * Returns a CodeValue given:
	 * 		codeSystemName (property displayName of CodeSystem) 
	 * 		domainName (property displayName of CodeValue representing domain)
	 * 		code (property code of CodeValue representing code)
	 * return null if code is not found.
	 * If domainName is null it searches in whole code system.
	 * example1: codeSystemName = "PHIDIC"  displayName="EmployeeFunction"      code="1"   (AMMINISTRATORE)
	 * example2: codeSystemName = "HL7"     displayName="AdministrativeGender"  code="M"   (Male)
	 * warn: it returns also cancelled and retired codes
	 */
	public CodeValue getCodeValue(String codeSystemName, String domainName, String code, String type) throws PersistenceException, DictionaryException {
		return getCodeValue(codeSystemName, domainName, code, type, false);
	}
	public CodeValue getCodeValue(String codeSystemName, String domainName, String code, String type, boolean disableErrors) throws PersistenceException, DictionaryException {
		
		if (useExternalEM) {
			((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);
		}

		if (codeSystemName == null || codeSystemName.isEmpty() || code == null || code.isEmpty()) {
			if(!useExternalEM && !disableErrors){
				FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Unable to search code without valid tuple (CodeSystem, Domain, Code). One element is null: Code system:"+codeSystemName+", Domain:"+domainName+ ", Code:"+ code);}
			return null;
		}

		String cacheKey = domainName+":"+type+":"+code;
		Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, domainName);

		if (csAndVersion == null ) {
			if (!disableErrors) {
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Unable to find codeSystem: " + codeSystemName + " domain: " + domainName);
			}
			return null;
		}

		if (useCache && cacheDomain.containsKey(cacheKey)) {
			return cacheDomain.get(cacheKey);
		}

		String q = QRY_SINGLE_CV;
		Class<?> cvClass = CodeValue.class;
		String codeValueClass = csAndVersion[2].toString();

		if (!cvClass.getSimpleName().equals(codeValueClass)) {
			q = q.replace("CodeValue", codeValueClass);
		}

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("parentId", csAndVersion[0] + ".%");
		pars.put("code", code);
		if (type != null && !type.isEmpty()) {
			q += QRY_TYPE_CV;
			pars.put("type", type);
		}
		List<CodeValue> codeValueList = (List<CodeValue>)executeQuery(q,pars);

		if (codeValueList== null || codeValueList.size() != 1 || codeValueList.get(0) == null){
			if (!disableErrors) {
			log.error("error reading code "+code+ " in domain "+domainName +" of code system "+codeSystemName+" : returned null caused by null or multiple result");
			}
			if(!useExternalEM && !disableErrors) {
				FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Domain "+domainName + ", Code system "+codeSystemName + " code: " + code + " : not found or multiple result");
			}
			return null;
		}

		CodeValue cv = codeValueList.get(0);
		if (cv.getStatus() != CODED_ACTIVE)
			log.warn("requested code with code" +code+" in domain "+domainName+ " of code system "+codeSystemName+" : found but is not active!");

		if (useCache) {
			cacheDomain.put(cacheKey, cv);
		}
		
		return cv;	
	}

	/**
	 * return a list of CodeValue coming from one code system, and multiple domain. 
	 * The method get codes, indipendetly if they are active or not, retired, or if they have code equivalent.
	 * 
	 * @param codeSystemName:  e.g. PHIDIC, is the name of the code system
	 * @param domainNameCodes is an unlimited list of string, represeenting a list of Domains and codes.
	 * 			each domain display name must be followed by column and a list of code comma separated.
	 * 			e.g.:   "EmployeeFunction:1,2,3" ,  "PHI_HOSPITAL_BREAKDOWN_STRUCTURE:Bed,Room"
	 * @return
	 * 	return a list 
	 * @throws PersistenceException 
	 * @throws DictionaryException 
	 * @throws ClassNotFoundException 
	 */

	public List<CodeValue> getCodeValues (String codeSystemName, String... domainNameCodes) throws PersistenceException, DictionaryException {

		//		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);

		if (codeSystemName == null || codeSystemName.isEmpty() || domainNameCodes== null || domainNameCodes.length < 1 ) {
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Unable to search code without valid input. One element is null: Code system:"+codeSystemName+", InputString:" + domainNameCodes);
			return null;
		}

		HashMap<String,List<String>> requestedDomainCode = parseInput(domainNameCodes);

		List<CodeValue> codeValueList = new ArrayList<CodeValue>();
		List<CodeValue> tmpCodeValueList = new ArrayList<CodeValue>();
		Set<String> requestedDomainKeySet = requestedDomainCode.keySet();

		for (String domainName : requestedDomainKeySet) {

			List<String> codes = requestedDomainCode.get(domainName);

			if (codes.size() == 1) { 
				//single code for this domain, use lighter query
				String code = codes.get(0);
				CodeValue singleCv = getCodeValueCsDomainCode(codeSystemName, domainName, code);
				if (singleCv != null)
					codeValueList.add(singleCv);
			} else {  
				//real list of codes
				Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, domainName);
				if(csAndVersion==null) {
					log.error("error reading list of code "+domainNameCodes+" of code system "+codeSystemName+" : returned empty result.");
					FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Domain:codes "+domainNameCodes + ", Code system "+codeSystemName+" : not found or multiple result");
					return codeValueList;
				}
				
				String codeValueClass = csAndVersion[2].toString();
				Class cvClass = CodeValue.class;
				String q;
				if (codes.isEmpty()) {
					q = QRY_MULTIPLE_CV_IN_DOMAIN;
				} else {
					q = QRY_MULTIPLE_CV;
				}

				if (!cvClass.getSimpleName().equals(codeValueClass)) {
					q = q.replace("CodeValue", codeValueClass);
				}

				HashMap<String, Object> pars = new HashMap<String, Object>();

				pars.put("parentId", csAndVersion[0] + ".%");
				if (!codes.isEmpty()) {
					pars.put("listCode", codes);
				}
				tmpCodeValueList = (List<CodeValue> )executeQuery(q,pars);

				if (tmpCodeValueList != null && !tmpCodeValueList.isEmpty())
					codeValueList.addAll(tmpCodeValueList);
			}
		}  //for cycle on domains

		if (codeValueList== null || codeValueList.isEmpty() ){
			log.error("error reading list of code "+domainNameCodes+" of code system "+codeSystemName+" : returned empty result.");
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Domain:codes "+domainNameCodes + ", Code system "+codeSystemName+" : not found or multiple result");
			return codeValueList;
		}

		for (CodeValue cv : codeValueList) {
			if (cv.getStatus() != CODED_ACTIVE)
				log.warn("requested code with code" +cv.getCode()+" in domain "+cv.getParent().getDisplayName()+ " of code system "+codeSystemName+" : found but is not active!");
		}

		return codeValueList;
	}

	/**
	 *get a list of select items, given a list of domain and codes of one code system.
	 * The mehtod doesn't look at active/unactive codes, code equivalent or code status, simply build a select item list
	 * from given codes.
	 */
	public List<SelectItem> selectCodeValues (String codeSystemName, String... domainNameCodes) throws DictionaryException {
		boolean getLeavesOnly = true;
		// JSF PROBLEM: THE ONLY WAY AT THE MOMENT. CALLING THIS METHOD BY EL
		if (domainNameCodes.length > 1 && "false".equals(domainNameCodes[0])) {
			getLeavesOnly = false;
			domainNameCodes = Arrays.copyOfRange(domainNameCodes, 1, domainNameCodes.length);
		}
		return selectCodeValues(getLeavesOnly, codeSystemName, domainNameCodes);
	}

	public List<SelectItem> selectCodeValuesWithDomains (String codeSystemName, String... domainNameCodes) throws DictionaryException {
		return selectCodeValues(false, codeSystemName, domainNameCodes);
	}

	public List<SelectItem> selectCodeValues (Boolean getLeavesOnly, String codeSystemName, String... domainNameCodes) throws DictionaryException {

		if (codeSystemName == null || codeSystemName.isEmpty() || domainNameCodes== null || domainNameCodes.length < 1 ) {
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Unable to build select item list without valid input. One element is null: Code system:"+codeSystemName+", InputString:" + domainNameCodes);
			return null;
		}

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		//		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);

		HashMap<String,List<String>> requestedDomainCode = parseInput(domainNameCodes);
		Set<String> requestedDomainKeySet = requestedDomainCode.keySet();

		for (String domainName : requestedDomainKeySet) {
			List<String> codes = requestedDomainCode.get(domainName);

			Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, domainName);

			if (csAndVersion == null || csAndVersion.length < 1){
				log.error("No element found for codesystem: "+codeSystemName+ " and " + domainName);
				FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, codeSystemName);
				continue;
			}

			String codeValueClass = csAndVersion[2].toString();
			Class cvClass = CodeValue.class;
			String q = QRY_MULTIPLE_CV_FOR_SELECT_ITEM;
			if (!getLeavesOnly) {
				q = q.replace("WHERE cv.type = 'C' AND ", "WHERE ");
			}

			if (!cvClass.getSimpleName().equals(codeValueClass)) {
				q = q.replace("CodeValue", codeValueClass);
				try {
					cvClass = Class.forName("com.phi.entities.dataTypes." + codeValueClass);
				} catch (ClassNotFoundException e) {
					log.error("unable to find CodeValue class extension: "+codeValueClass+ " skipped codes for domain "+domainName + " of codeSystem "+codeSystemName);
					continue;
				}
			}

			String currentLang = Locale.instance().getLanguage();

			if (!currentLang.equals("it")) {
				q = q.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
			}

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			Query query = ca.createQuery(q); 
			query.setParameter("parentId", csAndVersion[0] + ".%");
			query.setParameter("listCode", codes);
			
			query.setHint("org.hibernate.cacheable", Boolean.TRUE);
			query.setHint("org.hibernate.cacheRegion", "VocabulariesCache");
			
			List<Object[]> result = query.getResultList();

			if (result == null || result.isEmpty() ){
				log.error("Error getting codes "+codes+" of domain " + domainName + " from code system" +codeSystemName+ " no codes/domain/codesystem found."  );
				FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, domainName);
				continue;
			}
			for (Object[] res : result) {
				String id = (String)res[0];
				String label = (String)res[1];
				SelectItem selItem = new SelectItem(ca.load(cvClass, id), label);
				selectItems.add(selItem);
			}

		} //cycle on requested domains

		if (selectItems.isEmpty()){
			log.error("No element found for codesystem: "+codeSystemName+ " and "+domainNameCodes);
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, codeSystemName);
		}

		return selectItems;
	}

	/**
	 * Method used by getCodeValues and selectCodeValues. 
	 * It is used to parse the input list of string like "domain:code1,code1,code3"
	 * It returns an hashmap String,List<String> with as key the domain name, and as value the list of requested code. 
	 */
	private HashMap<String,List<String>> parseInput(String[] domainNameCodes) {
		HashMap<String,List<String>> requestedDomainCode = new HashMap<String, List<String>>();
		for (String domainCode : domainNameCodes) {
			if (domainCode == null || domainCode.isEmpty()){
				log.warn("requested invalid domain[:codes] string:"+domainCode);
				continue;
			}
			if (domainCode.contains(":")) {
				String[] domainCodeSplit =domainCode.split(":");
				if (domainCodeSplit.length != 2) {
					log.warn("requested invalid domain:codes string:"+domainCode);
					continue;
				}

				String domain=domainCodeSplit[0];
				String codesString=domainCodeSplit[1];
				List<String> codes = new ArrayList<String>();

				String[] codesArray =  codesString.split(",");
				for (String code : codesArray) {
					if (code == null || code.length() <1){
						log.warn("requested invalid domain:codes string:"+domainCode);
						continue;
					}
					codes.add(code);
				}


				if (codes.isEmpty()) {
					log.warn("requested invalid domain:codes string:"+domainCode);
				}

				if (requestedDomainCode.containsKey(domain)){
					List<String> existingCode= requestedDomainCode.get(domain);
					existingCode.addAll(codes);
					requestedDomainCode.put(domain,existingCode);
				}else{
					requestedDomainCode.put(domain, codes);
				}
			} else {
				if (!requestedDomainCode.containsKey(domainCode)) {
					requestedDomainCode.put(domainCode, new ArrayList<String>());
				}
			}

		} 
		return requestedDomainCode;
	}

	/**
	 * Returns a semicolon separated list of CodeValue IDs, with class type as prefix
	 * 
	 * @param codeSystemName:  e.g. PHIDIC, is the name of the code system
	 * @param domainAndCodes is an unlimited list of string, represeenting a list of Domains and codes.
	 * 			each domain display name must be followed by column and a list of code comma separated.
	 * 			e.g.:   "EmployeeFunction:1,2,3" ,  "PHI_HOSPITAL_BREAKDOWN_STRUCTURE:Bed,Room"
	 */
	public String getCodeValueExtendedId(String codeSystemName, String... domainAndCodes) throws PersistenceException, DictionaryException {
		String result = "";
		for (CodeValue cv : getCodeValues(codeSystemName, domainAndCodes)) {
			String cvClass = HibernateProxyHelper.getClassWithoutInitializingProxy(cv).getSimpleName();
			result += (result.isEmpty() ? "" : ";") + cvClass + "-" + cv.getId();
		}
		return result;
	}


	public List<SelectItem> distinctLabelSelectItem(List<SelectItem> sl) {
		List<String> labels = new ArrayList<String>();
		Iterator<SelectItem> it = sl.iterator();
		while (it.hasNext()) {
			SelectItem sel = it.next();
			if (labels.contains(sel.getLabel()))
				it.remove();
			else
				labels.add(sel.getLabel());
		}
		return sl;
	}


	public CodeValue getDomain (String codeSystemName, String domainName) throws PersistenceException, DictionaryException  {

		if (codeSystemName == null || codeSystemName.isEmpty() || domainName == null || domainName.isEmpty()) {
			return null;
		}

		String cacheKey = codeSystemName+domainName;
		if (useCache && cacheDomain.containsKey(cacheKey)) {
			return cacheDomain.get(cacheKey);
		}

		Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, domainName);
		String codeValueClass = csAndVersion[2].toString();
		Class cvClass = CodeValue.class;

		String q = QRY_SINGLE_DOMAIN;

		if (!cvClass.getSimpleName().equals(codeValueClass)) {
			q = q.replace("CodeValue", codeValueClass);
		}

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("codeSystemName", codeSystemName);
		pars.put("domainName", domainName);
		List<CodeValue> res = (List<CodeValue>)executeQuery(q,pars);

		if (res == null || res.isEmpty()) {
			log.error("Domain "+domainName+ " not found in CodeSystem "+codeSystemName);
			return null;
		}
		else  {
			CodeValue cv = res.get(0);
			if(useCache)
				cacheDomain.put(cacheKey,cv);

			return cv;
		}
	}



	private Object executeQuery (String q, HashMap<String,Object> pars) throws PersistenceException {

		Object cvs = null;

		if (useExternalEM) {
			Query qry = em.createQuery(q);
			for (String k : pars.keySet()) {
				qry.setParameter(k, pars.get(k));
			}
			
			qry.setHint("org.hibernate.cacheable", Boolean.TRUE);
			qry.setHint("org.hibernate.cacheRegion", "VocabulariesCache");
			
			cvs = qry.getResultList();
		}
		//Uses Catalog adapter entity manager instead of this.em to guarantee same persistence context as other rim entities
		else {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance(); 
			cvs = ca.executeHQLwithParameters(q, pars );
		}

		//object returned should be cast to List<CodeValue>, or List<CodeValueXXX> or List<Object[]> in case of select
		return cvs;

	}

	public List<SelectItem> selectCodeValuesWithoutDomain (Boolean getLeavesOnly, String codeSystemName, String... codes) throws DictionaryException {

		if (codeSystemName == null || codeSystemName.isEmpty() || codes== null || codes.length < 1 ) {
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "Unable to build select item list without valid input");
			return null;
		}

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		//		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);


		Object[] csAndVersion = getCodeSystemAndVersionFromCache(codeSystemName, "");

		if (csAndVersion == null || csAndVersion.length < 1){
			log.error("No element found for codesystem: "+codeSystemName);
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, codeSystemName);
			return selectItems;
		}

		String codeValueClass = csAndVersion[2].toString();
		Class cvClass = CodeValue.class;
		String q = QRY_MULTIPLE_CV_FOR_SELECT_ITEM_NO_DOMAIN;
		if (!getLeavesOnly) {
			q = q.replace("WHERE cv.type = 'C' AND ", "WHERE ");
		}

		if (!cvClass.getSimpleName().equals(codeValueClass)) {
			q = q.replace("CodeValue", codeValueClass);
			try {
				cvClass = Class.forName("com.phi.entities.dataTypes." + codeValueClass);
			} catch (ClassNotFoundException e) {
				log.error("unable to find CodeValue class extension: "+codeValueClass+ " skipped codes for codeSystem "+codeSystemName);
				return selectItems;
			}
		}

		String currentLang = Locale.instance().getLanguage();

		if (!currentLang.equals("it")) {
			q = q.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
		}

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		Query query = ca.createQuery(q); 
		query.setParameter("codeSystemName", codeSystemName);
		query.setParameter("listCode", Arrays.asList(codes));

		query.setHint("org.hibernate.cacheable", Boolean.TRUE);
		query.setHint("org.hibernate.cacheRegion", "VocabulariesCache");

		List<Object[]> result = query.getResultList();

		if (result == null || result.isEmpty() ){
			log.error("Error getting codes "+codes+" from code system" +codeSystemName+ " no codes/domain/codesystem found."  );
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, codeSystemName);
			return selectItems;
		}
		for (Object[] res : result) {
			String id = (String)res[0];
			String label = (String)res[1];
			SelectItem selItem = new SelectItem(ca.load(cvClass, id), label);
			selectItems.add(selItem);
		}

		if (selectItems.isEmpty()){
			log.error("No element found for codesystem: "+codeSystemName);
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, codeSystemName);
		}

		return selectItems;
	}

	public CodeValue getCodeValueByProperty(String codeSystemName,
			String domainName, String propertyName, String propertyValue)
			throws PersistenceException, DictionaryException {

		if (useExternalEM) {
			((Session) em.getDelegate()).setFlushMode(FlushMode.MANUAL);
		}

		if (codeSystemName == null || codeSystemName.isEmpty() || propertyValue == null
				|| propertyValue.isEmpty()) {
			if (!useExternalEM) {
				FacesErrorUtils
						.addErrorMessage(
								ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE,
								"Unable to search code without valid tuple (CodeSystem, Domain, DisplayName). One element is null: Code system:"
										+ codeSystemName
										+ ", Domain:"
										+ domainName + ", DisplayName:" + propertyValue);
			}
			return null;
		}

		String cacheKey = domainName + ":C:" + propertyValue;
		Object[] csAndVersion = getCodeSystemAndVersionFromCache(
				codeSystemName, domainName);

		if (csAndVersion == null) {			
				FacesErrorUtils
						.addErrorMessage(
								ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE,
								"Unable to find codeSystem: " + codeSystemName
										+ " domain: " + domainName);			
			return null;
		}

		if (useCache && cacheDomain.containsKey(cacheKey)) {
			return cacheDomain.get(cacheKey);
		}

		String q = QRY_SINGLE_CV_FOR_PROPERTY;
		Class<?> cvClass = CodeValue.class;
		String codeValueClass = csAndVersion[2].toString();

		if (!cvClass.getSimpleName().equals(codeValueClass)) {
			q = q.replace("CodeValue", codeValueClass);
		}

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("parentId", csAndVersion[0] + ".%");
		
		if (propertyName != null && !propertyName.isEmpty()) {
			if ("displayName".equals(propertyName)){
				q += " AND cv.displayName = :displayName ";
				pars.put("displayName", propertyValue);
			} else if ("description".equals(propertyName)){
				q += " AND cv.description = :description ";
				pars.put("description", propertyValue);
			} else if ("keywords".equals(propertyName)){
				q += " AND cv.keywords = :keywords ";
				pars.put("keywords", propertyValue);
			}
		}
		List<CodeValue> codeValueList = (List<CodeValue>) executeQuery(q, pars);

		if (codeValueList == null || codeValueList.size() != 1
				|| codeValueList.get(0) == null) {

				log.error("error reading code in domain "
						+ domainName + " of code system " + codeSystemName
						+ " : returned null caused by null or multiple result");
			
			if (!useExternalEM ) {
				FacesErrorUtils
						.addErrorMessage(
								ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE,
								"Domain " + domainName + ", Code system "
										+ codeSystemName + " code: " + propertyValue
										+ " : not found or multiple result");
			}
			return null;
		}

		CodeValue cv = codeValueList.get(0);
		if (cv.getStatus() != CODED_ACTIVE)
			log.warn("requested code with code" + propertyValue + " in domain "
					+ domainName + " of code system " + codeSystemName
					+ " : found but is not active!");

		if (useCache) {
			cacheDomain.put(cacheKey, cv);
		}

		return cv;
	}
}