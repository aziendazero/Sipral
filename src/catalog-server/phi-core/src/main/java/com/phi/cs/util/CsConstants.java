package com.phi.cs.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all useful Constants
 *
 */
public class CsConstants {
	public static final String LIST_SUFFIX = "_LIST";
	public static final String PRE_MULTISELECT_SUFFIX = "MULTISELECT_";
	
	public static final String HISTORY_PREFIX = "HistoryOf";
	
	public static final String C_CATALOG_ADAPTER_PACKAGE = "com.phi.cs.catalog.adapter.";
	public static final String C_CATALOG_ADAPTER_CLASS_SUFFIX = "CatalogAdapter";

	public static final String CATALOG_ADPATER_WEB_CONF_PAR_NAME = "CatalogAdapter";
	public static final String CATALOG_ADPATER_ACTIVE_CONV_PAR_NAME = "ActiveCatalogAdapter";
	
	public static final String HISTORY_ENABLED = "repository.history";
	
	public static final String HISTORY_AUDIT_ENABLED = "repository.AuditHistory";
	
	public static final String HISTORY_AUDIT_DATA = "repository.AuditHistoryData";
	
	public static final String RESUMING_ENABLED = "repository.resuming";
	
	public final static String MIF_CODE_SEPARTOR="_";
	public final static String NOT_AVAILABLE="NA";
	public final static String NULL_STR="NULL";
	public final static String COMMA =",";
	public final static String EMPTY_SPACE =" ";
	public final static String SLASH ="/";
	
	//String used as key for variable name in Jboss Application Context to store customer name, solution name and catalog server ear path.
	public final static String CUSTOMER = "CUSTOMER";
	public final static String SOLUTION_NAME = "SolutionName";
	public final static String CATALOG_SERVER_EAR_PATH = "CatalogServerEarPath";
	
//	public final static String PHI_DIC="PHI_DIC";
	public final static String encryptPatientData="encryptPatientData";
	public final static String MD5_passwords="MD5_passwords";
	public final static String use_LDAP_auth="use_LDAP_auth";
	
//	public final static String PROCESS_AUTHORIZATION_FROM="PROCESS_AUTHORIZATION_FROM";
//	public final static String RULES_FROM="RULES_FROM";
	
	public final static String HOME_LBL="home";
	
	public final static String READ_OP="READ";
	public final static String WRITE_OP="WRITE";
	
	public final static String PDF_FOLDER="ABSOLTUE_PATH_TO_PDF_FOLDER";
	
	public final static String securityAuthenticationLDAP = "SECURITY_AUTHENTICATION";
	public final static String initialContextFactoryLDAP = "INITIAL_CONTEXT_FACTORY";
	public final static String domainLDAP = "LDAP_DOMAIN";
	public final static String searchBaseLDAP = "LDAP_SEARCH_BASE";
	public final static String hostLDAP = "LDAP_HOST";
	public final static String portLDAP = "LDAP_PORT";
	
	
}