package com.phi.rest.dictionary;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Locale;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.view.bean.InputSuggestionBean;
import com.phi.cs.view.bean.Suggestion;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.json.HibernateModule;


/**
 * Manages code values from rest.
 * 
 * Examples:
 * GET /codevalues/10										: get codevalues with id 10
 * GET /codevalues/domain/codeSystem=PHIDIC&domain=Gender	: get codevalues of code system phidic of domain gender
 * 
 * @author alex.zupan
 */
@Restrict("#{identity.isLoggedIn(false)}")
@Name("DictionaryManagerRest")
@Path("/codevalues")
public class DictionaryManagerRest {

	private static final Logger log = Logger.getLogger(DictionaryManagerRest.class);
	
	private static final String APPLICATION_JSON = "application/json; charset=utf-8";
	
	private CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
	private Vocabularies voc = VocabulariesImpl.instance();
		
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	private static final String SEARCH_HQL =
			" SELECT" +
			" cv.id as id" +
			" FROM CodeValue cv" +
			" WHERE cv.oid LIKE :rootOid" +
			" AND cv.type = 'C'";
			

	
	/**
	 * Get a codeValue by id
     * 
	 * @param id
	 * @return
	 */	
	@GET
	@Path("/{id}")
	@Produces(APPLICATION_JSON)
	public Response get(@PathParam("id") String id) {
		try {
						
			CodeValue entity = ca.get(CodeValue.class, id);
			
			if (entity instanceof HibernateProxy ) {
				entity = (CodeValue)((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
			}
			//FLEX doesen't support reading of status. Only 200 is a valid status.
//			if (entity == null) {
//				return Response.status(Response.Status.NOT_FOUND).entity("Entity " + CodeValue.class.getSimpleName() + " not found for id: " + id).build();
//			}

			String json = mapper.writeValueAsString(entity);

			return Response.ok(json).build();


		} catch (Exception e) {
			log.error("Error get codeValue by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + CodeValue.class.getSimpleName() + " by id: " + id).build();
		}
	}
	
	
	/**
	 * Get codeValues by id codeSystem and domain d=CodeSystem:Domain:code1,code2,code3
	 * @param parameters
	 * @return
	 */
	@GET
	@Path("/domain/{parameters}")
	@Produces(APPLICATION_JSON)
	public Response get(@PathParam("parameters") PathSegment parameters) {		
		//domain=CodeSystem:Domain:code1,code2,code3
		try {
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(parameters);
			
			Map<String, List<Map<String,Object>>> fixedResults = new HashMap<String, List<Map<String,Object>>>();
			
			List<String> codeSystemAndDomains = restrictionMap.get("domain");
			Boolean lazy = Boolean.parseBoolean(restrictionMap.get("lazy").get(0));
			
			for (String codeSystemAndDomain : codeSystemAndDomains) {
				
				String codeSystem;
				String domain;
				
				if (codeSystemAndDomain.contains(":")) {
					codeSystem = codeSystemAndDomain.substring(0,codeSystemAndDomain.indexOf(":"));
					domain  = codeSystemAndDomain.substring(codeSystemAndDomain.indexOf(":") + 1,codeSystemAndDomain.length());
				} else { //if doesent contain : load all domain
					codeSystem = codeSystemAndDomain;
					domain  = "";
				}
				
				String fixedDomain = domain;
				if (fixedDomain.contains(":")) {
					fixedDomain = fixedDomain.substring(0,fixedDomain.indexOf(":"));
				}
				
				List<SelectItem> siResults;
				
				if (!lazy) {
					if (domain.contains(":")) {
						siResults = voc.selectCodeValues(codeSystem, domain);
					} else {
						siResults = voc.getIdValues(codeSystem + ":" + domain);
					}
				} else {
					if (domain.contains(":")) {
						// FIXME
						siResults = voc.selectCodeValues(codeSystem, domain);
					} else {
						siResults = voc.getLazyIdValues(codeSystem + ":" + domain);
					}
				}
								
				List<Map<String,Object>> listResult = new ArrayList<Map<String,Object>>();
				
				for (SelectItem si : siResults) {
					Map<String,Object> code = new HashMap<String, Object>();
					
					CodeValue cv = (CodeValue)si.getValue();					
					Class<CodeValue> cvClass = HibernateProxyHelper.getClassWithoutInitializingProxy(cv);
					cv = ca.get(cvClass, cv.getId());
					
					code.put("id", cv.getId());
					code.put("code", cv.getCode());										
					code.put("displayName", cv.getDisplayName()); 
					code.put("type", cv.getType());	
					code.put("currentTranslation", cv.getCurrentTranslation()); // Angular
					code.put("langIt", cv.getLangIt()); // Flex
					code.put("langDe", cv.getLangDe()); // Flex
					code.put("langEn", cv.getLangEn()); // Flex						
					code.put("entityName", cvClass.getName());
																				
					listResult.add(code);
				}
										
				if (domain == "") {
					fixedResults.put(codeSystem, listResult);
				} else {
					fixedResults.put(codeSystem + "_" + fixedDomain, listResult);
				}
			}
						
			String json = mapper.writeValueAsString(fixedResults);

			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error get entities " + CodeValue.class.getSimpleName() + " by: " + parameters, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get entities " + CodeValue.class.getSimpleName()  + " by: " + parameters).build();
		}
	}
	
	/**
	 * Get CodeValues for a suggestion box
	 */
	@GET
	@Path("/suggest/{codeSystemName}/{domainName}/{search}")
	@Produces(APPLICATION_JSON)
	public Response suggest(@PathParam("codeSystemName") String codeSystemName,@PathParam("domainName") String domainName,@PathParam("search") String search) {
		try {
			
			InputSuggestionBean suggestion = new InputSuggestionBean(null);
			
			suggestion.setContentType(3);
			
			suggestion.setCodeSystem(codeSystemName);
			if (!domainName.equals("null")) {
				suggestion.setDomain(domainName);
			}
			suggestion.setSearch(search);
			
			List<Suggestion> suggested = suggestion.autocomplete();
			
			List<Map<String,Object>> listResult = new ArrayList<Map<String,Object>>();
			
			for (Suggestion sugg : suggested) {
				Map<String,Object> code = new HashMap<String, Object>();
				
				int indexOf = sugg.getId().indexOf("-");
				
				code.put("id", sugg.getId().substring(indexOf + 1, sugg.getId().length()));
				code.put("entityName", "com.phi.entities.dataTypes." + sugg.getId().substring(0, indexOf));
				code.put("displayName", sugg.getLabel());
				code.put("code", sugg.getCode());
				listResult.add(code);
			}
			
			String json = mapper.writeValueAsString(listResult);
	
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error suggest " + codeSystemName + ", " + domainName + " : " + search, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error suggest " + codeSystemName + ", " + domainName + " : " + search).build();
		}
	}
	
	/**
	 * Get codeValues by id codeSystem and domain d=CodeSystem:Domain:code1,code2,code3
	 * @param parameters
	 * @return
	 */
	@GET
	@Path("/search/{parameters}")
	@Produces(APPLICATION_JSON)
	public Response search(@PathParam("parameters") PathSegment parameters) {	
		try {
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(parameters);
			
			String currentLang = Locale.instance().getLanguage();
			
			String codeSystem = restrictionMap.get("codeSystem").get(0);
			String rootDomain = restrictionMap.get("rootDomain").get(0);
			
			CodeValue rootCode = voc.getDomain(codeSystem, rootDomain);
			
			String rootOid = rootCode.getOid();
			String code = null;
			if (restrictionMap.get("code") != null) {
				code = restrictionMap.get("code").get(0);
			}
			String name = null;
			if (restrictionMap.get("name") != null) {
				name = restrictionMap.get("name").get(0);
			}
			
			String searchHql = SEARCH_HQL;
			
			if (code != null && name != null)
				searchHql += " AND (UPPER(cv.code) LIKE :code OR UPPER(cv.lang" + WordUtils.capitalize(currentLang) + ") LIKE :name)";
			else if (code != null)
				searchHql += " AND UPPER(cv.code) LIKE :code";
			else if (name != null)
				searchHql += " AND UPPER(cv.lang" + WordUtils.capitalize(currentLang) + ") LIKE :name";
			
			searchHql += " ORDER BY cv.lang" + WordUtils.capitalize(currentLang);
			
			Query searchQry = ca.createHibernateQuery(searchHql);
			
			searchQry.setParameter("rootOid", rootOid + ".%");
			if (code != null)
				searchQry.setParameter("code", "%" + code.toUpperCase() + "%");
			if (name != null)
				searchQry.setParameter("name", "%" + name.toUpperCase() + "%");
			
			searchQry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
						
			List<Map<String,Object>> listResult = (List<Map<String,Object>>) searchQry.list();
			
			for (Map<String,Object> codeValue : listResult) {
				
				CodeValue cv = ca.get(CodeValue.class, (String) codeValue.get("id"));	
				if (cv instanceof HibernateProxy) { //deproxy
					cv = (CodeValue)(((HibernateProxy)cv).getHibernateLazyInitializer().getImplementation());
				}
				Class cvClass = cv.getClass();
			
				codeValue.put("code", cv.getCode());
				codeValue.put("displayName", cv.getDisplayName());
				codeValue.put("type", cv.getType());
				codeValue.put("currentTranslation", cv.getCurrentTranslation()); // Angular
				codeValue.put("langIt", cv.getLangIt()); // Flex
				codeValue.put("langDe", cv.getLangDe());
				codeValue.put("langEn", cv.getLangEn());	
				
				codeValue.put("entityName", cvClass.getName());
			}
			
			String json = mapper.writeValueAsString(listResult);

			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error search code values " + CodeValue.class.getSimpleName() + " by: " + parameters, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error search code values " + CodeValue.class.getSimpleName()  + " by: " + parameters).build();
		}
	}
	
	
	/**
	 * 
	 *  UTILITY functions
	 *  
	 */
		
	// Function to decode the restrictions and add path parameter
	protected MultivaluedMap<String, String> decodeResctrictions(PathSegment restrictions) throws UnsupportedEncodingException{
		MultivaluedMap<String, String> restrictionMap = restrictions.getMatrixParameters();

		// Add path to restrictionMap
		String path = restrictions.getPath();
	
		String key = path;
		String value = "";
		if (path.contains("=")) {
			String[] keyValue = path.split("=");
			key = keyValue[0];
			value =	keyValue[1];
		}

		if (restrictionMap.containsKey(key)) {
			restrictionMap.get(key).add(value);
		} else {
			List<String> valueLst = new ArrayList<String>();
			valueLst.add(value);
			restrictionMap.put(key, valueLst);
		}			

		// Decode restrictionMap
		for (List<String> valueList : restrictionMap.values()) {
			for (int i = 0; i < valueList.size(); i++) {
				value = valueList.get(i);
				value = URLDecoder.decode(value, "utf-8");
				valueList.set(i, value);
			}		
		}
		
		return restrictionMap;
	}

}