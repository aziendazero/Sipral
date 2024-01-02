package com.phi.cs.view.bean;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.jboss.seam.web.Locale;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.util.SuggestionComparator;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;

/**
 * Executes queryes to find suggestion for autocomplete widget
 * @author Francesco Bruni
 * @author Alex Zupan
 */
public class InputSuggestionBean {

	private static final Logger log = Logger.getLogger(InputSuggestionBean.class);

	private static final int CITIES = 0; // manages bindings of addr type only
	private static final int DOMAINS = 1; // populates suggestion box with first level domain names
	private static final int DOMAINSRECURSIVE = 2; // recursively populates suggestion box with domain names
	private static final int LEAVES = 3; // populates suggestion box with first level leaves names
	private static final int FULL = -1; // populates suggestion box with all
	
	private static final int CODED_ACTIVE = 1;
	
	private static final String CODE_SYSTEM_QUERY = "SELECT MAX(cs.version), cs.codeValueClass FROM CodeSystem cs WHERE cs.name = :codeSystemName AND cs.status = " + CODED_ACTIVE + " group by cs.codeValueClass";
	
	private static final String CODE_SYSTEM_BY_VERSION_QUERY = "SELECT cs.codeValueClass FROM CodeSystem cs WHERE cs.name = :codeSystemName AND cs.version = :version";
	
	private static final String CODE_VALUES_QUERY = "SELECT cv.id, case when cv.langIt is not null then cv.langIt else cv.displayName end, cv.code FROM CodeValue cv " +
			"JOIN cv.codeSystem AS cs " +
			"WHERE (lower(cv.langIt) like :suggestion " +
			"OR lower(cv.displayName) like :suggestion ";
	private static final String CODE_VALUES_QUERY_CODE = "OR lower(cv.code) like :suggestion "; 
	private static final String QUERY_END = " AND cs.name = :codeSystemName AND cs.version = :version ";
	private static final String CODE_VALUES_QUERY_END = ")" + QUERY_END;
	
	private static final String CODE_VALUE_NAME = CodeValue.class.getSimpleName();
	private static final String CODE_VALUE_CITY_NAME = CodeValueCity.class.getSimpleName();

	protected String locale = Locale.instance().getLanguage();
	
//	protected ResourceBundle bundle = ResourceBundle.getBundle("bundle.format.messages", new java.util.Locale(locale));
	private String datePattern = "dd/MM/yyyy";
	
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	private Integer version = null;
	
	private String domainName;
	private String codeSystemName;

	private Date date = null;

	// current selected code value
	private CodeValue currentCV = null;

	// name of the "parent" widget
	private String chainedWidget = null;

	private CodeValue chainedCV = null;
	
	// content type of the suggestion 
	private int contentType;
	
	private Boolean fullLike = false;

	private String search;

	private List<CodeValue> customList = null;
	private List<Suggestion> results = null;

	private CatalogAdapter catalogAdapter = CatalogPersistenceManagerImpl.instance();
	
	protected Map<Object, InputSuggestionBean> suggestionContainer;
	
	private static final String regexDomain = "[a-zA-Z0-9]*";
	
	public InputSuggestionBean(Map<Object, InputSuggestionBean> suggestionContainer) {
		this.suggestionContainer = suggestionContainer;
	}

	//DATE for BirthPlace widget -> to be fixed!!! PHI 2
	public String getDate() {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		return sdf.format(date);
	}

	public void setDate(String date) {
		if (date == null) {
			this.date = null;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			this.date = sdf.parse(date,new ParsePosition(0));
		}
		//FIXME PHI 2
//		if (dateBindingExpression != null) {
//			setValue(dateBindingExpression, date, CsConstants.DATEFORMATS.get(locale));
//		}
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setContentType(int contentType) {
		this.contentType = contentType; 
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setDomain(String domainName) {
		this.domainName = domainName;
	}

	public void setCodeSystem(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}

	public void setChainedWidget(String chainedWidget) {
		this.chainedWidget = chainedWidget;
		chainedCV = suggestionContainer.get(chainedWidget).currentCV;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
	public void setCustomList(List<CodeValue> customList) {
		this.customList = customList;
	}

	public void setFullLike( Boolean val) {
		fullLike = val;
	}
	
	public Boolean getFullLike() {
		return fullLike;
	}
	
	/**
	 * looks for object names that start by suggest parameter and return a filtered object list
	 * 
	 * @return an objects list, containing all objects which display name starts with suggest string
	 */
	public List<Suggestion> autocomplete() {
		
		try {
			List<String[]> idAndTranslations = null;
			if (customList == null) {
				idAndTranslations = executeQuery(search);
			} else {
				idAndTranslations = filterList(search);
			}

			results = new ArrayList<Suggestion>();
			
			for (int codeValueIndex=0; codeValueIndex<idAndTranslations.size(); codeValueIndex++) {
				Object[] idTranslationAndCode = idAndTranslations.get(codeValueIndex);

				if (CITIES == contentType) {
					results.add(new Suggestion(codeValueIndex, idTranslationAndCode[0].toString(), idTranslationAndCode[1].toString(), idTranslationAndCode[2].toString(), idTranslationAndCode[3] == null ? "" : idTranslationAndCode[3].toString(), idTranslationAndCode[4] == null ? "" : idTranslationAndCode[4].toString()));
				} else
					results.add(new Suggestion(codeValueIndex, idTranslationAndCode[0].toString(), idTranslationAndCode[1].toString(), idTranslationAndCode[2].toString()));

			}
			// sort alphabetically
			if (customList == null) {
				Collections.sort(results, new SuggestionComparator());
			}

		} catch (PhiException e) {
			FacesErrorUtils.addErrorMessage(ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE, "CodeSystem " + codeSystemName + " not found!");
		}
		return results;
	}

	public List<Suggestion> getResults() {
		return results;
	}

	private List<String[]> filterList(String suggest) {
		List<String[]> result = new ArrayList<String[]>();
		
		for (CodeValue cv : customList) {
			String displayName = cv.getDisplayName();
			if (displayName == null) {
				displayName = "";
			}
			String code = cv.getCode();
			if (code == null) {
				code = "";
			}
			String translation = "";
			String t = cv.getTranslation(locale);
			if (t != null) {
				translation = t;
			}
			
			if(fullLike) {
				if (displayName.toLowerCase().contains(suggest.toLowerCase()) ||
						code.toLowerCase().contains(suggest.toLowerCase()) ||
						translation.toLowerCase().contains(suggest.toLowerCase())) {
					result.add( new String[]{ cv.getId(), (t != null ? t : displayName),cv.getCode() });
				}
			} else {
				if (displayName.toLowerCase().startsWith(suggest.toLowerCase()) ||
						code.toLowerCase().startsWith(suggest.toLowerCase()) ||
						translation.toLowerCase().startsWith(suggest.toLowerCase())) {
					result.add( new String[]{ cv.getId(), (t != null ? t : displayName),cv.getCode() });
				}
			}
		}
		
		return result;
	}
	
	/**
	 * executes main query to get code value list
	 * @throws PhiException 
	 */
	private List<String[]> executeQuery(String suggest) throws PhiException {
//		try {
				if (chainedWidget != null) {
//					String previousDomainName = String.class.cast(queryParameters.get(DOMAINNAME));
					chainedCV = suggestionContainer.get(chainedWidget).currentCV;
					String currentDomainName = null;
					if (chainedCV != null) {
						currentDomainName = chainedCV.getDisplayName();
						if (!isDomainDisplayNameWellFormed(currentDomainName)) {
							currentDomainName = chainedCV.getId();
						}
					} else {
						if (currentCV != null) {
							CodeValue parentCV = currentCV.getParent();
							if (parentCV != null) {
								currentDomainName = parentCV.getDisplayName();
								if (!isDomainDisplayNameWellFormed(currentDomainName)) {
									currentDomainName = parentCV.getId();
								}
							}
						}
					}
					domainName = currentDomainName;
				}
				
				if (domainName != null) {
					domainName = domainName.toLowerCase();
				}

				HashMap<String,Object> qryPars = new HashMap<String, Object>();

				String codeValueClass;
				
				qryPars.put("codeSystemName", codeSystemName);
				
				if (version == null) {
					List maxVersionAndCvClass = catalogAdapter.executeHQLwithParameters(CODE_SYSTEM_QUERY, qryPars);
					
					if (maxVersionAndCvClass == null || maxVersionAndCvClass.isEmpty()) {
						throw new PhiException("CodeSystem " + codeSystemName + " not found!", ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE);
					}
					
					Object[] versionAndCodeValueClass = (Object[])maxVersionAndCvClass.get(0);
					version = (Integer)versionAndCodeValueClass[0];
					qryPars.put("version", version);
					codeValueClass = (String)versionAndCodeValueClass[1];
				} else {
					qryPars.put("version", version);
					List cvClass = catalogAdapter.executeHQLwithParameters(CODE_SYSTEM_BY_VERSION_QUERY, qryPars);

					if (cvClass == null || cvClass.isEmpty()) {
						throw new PhiException("CodeSystem " + codeSystemName + " version: " + version + " not found!", ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE);
					}
					
					codeValueClass = (String)cvClass.get(0);
				}
				
				String query = CODE_VALUES_QUERY + (isShowCode() ? CODE_VALUES_QUERY_CODE : "") + CODE_VALUES_QUERY_END;
				
				if (!locale.equals("it")) {
					query = query.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(locale));
				}
				
				query = query.replace("cv.id", "'" + codeValueClass + "-' || cv.id");
				
				if (!CODE_VALUE_NAME.equals(codeValueClass)) {
					query = query.replace("FROM CodeValue", "FROM " + codeValueClass);
					
					if (CODE_VALUE_CITY_NAME.equals(codeValueClass)) {
						query = query.replace("FROM", ", cv.zip, cv.province FROM");
					}
				}
				
				switch (contentType) {
				case FULL:
					break;
				case DOMAINSRECURSIVE:
					query += "AND cv.type <> 'C' ";
					if (domainName != null) {
						String parentIdLike = getParentDomain(codeValueClass);
						if (parentIdLike == null) {
							return null;
						}
						query += "AND cv.id like '" + parentIdLike + "' ";
					}
					break;
				case DOMAINS:
					query += "AND cv.type <> 'C' ";
					query += (domainName == null ? "AND cv.parent is null ":"AND lower(cv.parent.displayName) = :domainName ");
					break;
				case LEAVES:
					query += "AND cv.type = 'C' ";
					query += (domainName == null ? "" : "AND lower(cv.parent.displayName) = :domainName ");
					break;
				default:
					//CITIES
					query += "AND cv.type = 'C' ";
					if (domainName != null) {
						String parentIdLike = getParentDomain(codeValueClass);
						if (parentIdLike == null) {
							return null;
						}
						query += "AND cv.id like '" + parentIdLike + "' ";
					}
					break;
				}
				// CHECK DATE VALIDITY
				query += "AND (cv.validTo is null OR cv.validTo > :date) ";
				query += "AND (cv.validFrom is null OR cv.validFrom <= :date) ";
				qryPars.put("date", date == null ? new Date() : date);

				query += "ORDER BY lower(cv.displayName)";

				if (domainName != null && (contentType == LEAVES || contentType == DOMAINS))
					qryPars.put("domainName", domainName.toLowerCase());
				
				if(fullLike) {
					qryPars.put("suggestion", "%" + suggest.toLowerCase()+ "%");
				} else {
					qryPars.put("suggestion", suggest.toLowerCase()+ "%");
				}
				
				return catalogAdapter.executePagedHQL(query, qryPars, 0, 50); 
				
//		} catch (Exception e) {
//			log.error("Error retrieving requested domain", e);
//		}
//		return null;
	}

	/**
	 * Checks if a display name is well formed
	 * 
	 * @param domainDisplayName - a domain display name
	 * @return true if given display name is well formed, false elsewhere
	 */
	private boolean isDomainDisplayNameWellFormed(String domainDisplayName) {

		if (Pattern.matches(regexDomain, domainDisplayName)) {
			return true;
		}
		return false;
	}
	
	// SHOW CODE
	private boolean showCode = false;
	
	public boolean isShowCode() {
		return showCode;
	}

	public void setShowCode(boolean showCode) {
		this.showCode = showCode;
	}
	
	private String getParentDomain(String codeValueClass) {
		String parentIdLike = null;
		Query parentQuery = catalogAdapter.createHibernateQuery("SELECT parentDomain.oid||'.%' FROM "+ codeValueClass +" AS parentDomain " +
				"JOIN parentDomain.codeSystem AS cs " +
				"WHERE lower(parentDomain.displayName) = :parentName AND parentDomain.type <> 'C' AND parentDomain.version = :version" + QUERY_END);
		parentQuery.setParameter("parentName", domainName.toLowerCase());
		parentQuery.setParameter("codeSystemName", codeSystemName);
		parentQuery.setParameter("version", version);
		List<?> result = parentQuery.list();
		if (result != null && !result.isEmpty()) {
			parentIdLike = (String)result.get(0);
		}
		return parentIdLike;
	}
}