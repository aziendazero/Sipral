package com.phi.cs.error;

public class ErrorConstants {

//	public final static String PHI_ERROR_PAGE = "/common/jsp/fc_error.seam";
//	public final static String PHI_ERROR_OBJECT_NAME = "PHIError";
	
	public static final String GENERIC_ERR_CODE="1";
	public static final String GENERIC_ERR_INTERNAL_MSG="Generic exception";

	//PersistenceException the exception with a DB in the Adapter DB layer
	public static final String PERSISTENCE_GENERIC_ERR_CODE="100";
	public static final String PERSISTENCE_GENERIC_ERR_INTERNAL_MSG="DB interaction problem";
	public static final String PERSISTENCE_SAVE_ERR_CODE="101";
	public static final String PERSISTENCE_SAVE_ERR_INTERNAL_MSG="Can't save the object because is a DUPLICATE";
	public static final String PERSISTENCE_FLUSH_ERR_CODE="102";
	public static final String PERSISTENCE_FLUSH_ERR_INTERNAL_MSG="Problem during DB syncronization";
	public static final String PERSISTENCE_QUERY_TREE_NAVIGATION_ERR_CODE="105";
	public static final String PERSISTENCE_QUERY_TREE_NAVIGATION_ERR_INTERNAL_MSG="Error during query tree navigation";
	public static final String PERSISTENCE_SQLFIXEDQUERY_ERR_CODE="106";
	public static final String PERSISTENCE_SQLFIXEDQUERY_ERR_INTERNAL_MSG="Error during acces to sqlFixedQuery.properties";
	public static final String PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE="107";
	public static final String PERSISTENCE_QUERY_TREE_CREATION_GENERIC_ERR_INTERNAL_MSG="Error during creation of the query tree";
	public static final String PERSISTENCE_QUERY_TREE_CREATION_WHERECLAUSE_ERR_INTERNAL_MSG="Error during creation of current node's WHERE clause";
	public static final String PERSISTENCE_QUERY_TREE_CREATION_FROMCLAUSE_ERR_INTERNAL_MSG="Error during creation of current node's FROM clause";
	public static final String PERSISTENCE_QUERY_TREE_CREATION_RIMTYPE_ERR_INTERNAL_MSG="Error during creation of RIM type instance";
	public static final String PERSISTENCE_GET_RIM_PROPERTY_TYPE_ERR_CODE="108";
	public static final String PERSISTENCE_GET_RIM_PROPERTY_TYPE_ERR_INTERNAL_MSG="Error during instantiation of RIM type, wrong data format";
	public static final String PERSISTENCE_RIM_PROPERTY_TYPE_NOT_FOUND_ERR_CODE="109";
	public static final String PERSISTENCE_RIM_PROPERTY_TYPE_NOT_FOUND_ERR_INTERNAL_MSG="Current RIM Object doesn't own such a property";
	public static final String PERSISTENCE_RIM_OBJECT_INITIALIZATION_ERR_INTERNAL_MSG ="Can't find RIM property matching current restrictions";
	public static final String PERSISTENCE_RIM_OBJECT_NO_METHOD_ERR_INTERNAL_MSG ="Can't find getter or setter for current property";
	public static final String PERSISTENCE_RIM_OBJECT_NO_METADATA_ERR_CODE="110";
	public static final String PERSISTENCE_RIM_OBJECT_NO_METADATA_ERR_INTERNAL_MSG ="Current RIM object has no metadata";
	public static final String PERSISTENCE_RIM_OBJECT_NOT_FOUND_ERR_INTERNAL_MSG ="MIF file not found \\ wrong name";
	public static final String PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_CODE="130";
	public static final String PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG="Error during item selection in listbox, checkbox, etc";
	
	public static final String PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_CODE="150";
	public static final String PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_MSG="Integrity constraint violation";
	
	public static final String PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_DELETE_CODE="151";
	public static final String PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_DELETE_MSG="Impossible delete dataBase element";
	
	public static final String PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE="160";
	public static final String PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_MSG="Dictionary domain not found";

	
	public static final String PERSISTENCE_LOAD_MIF_ERR_CODE="111";
	public static final String PERSISTENCE_LOAD_MIF_ERR_INTERNAL_MSG="LoaderException in JavaSig when the system was trying to load a MIF";
	
	public static final String APPLICATION_GENERIC_ERR_CODE="201";
	public static final String APPLICATION_GENERIC_ERR_INTERNAL_MSG="Application generic problem";
	public static final String APPLICATION_CREATION_OBJECT_ERR_CODE="202";
	public static final String APPLICATION_CREATION_OBJECT_ERR_INTERNAL_MSG="Errors during creation of rim object";
	public static final String APPLICATION_READ_OBJECT_ERR_CODE="203";
	public static final String APPLICATION_READ_OBJECT_ERR_INTERNAL_MSG="Errors during read of rim object";
	public static final String APPLICATION_DELETE_OBJECT_ERR_CODE="204";
	public static final String APPLICATION_DELETE_OBJECT_ERR_INTERNAL_MSG="Errors during delete of rim object";
	public static final String APPLICATION_UPDATE_OBJECT_ERR_CODE="205";
	public static final String APPLICATION_UPDATE_OBJECT_ERR_INTERNAL_MSG="Errors during update of rim object";
	public static final String APPLICATION_ATTRIBUTE_JAVA2RIM_ERR_CODE="206";
	public static final String APPLICATION_ATTRIBUTE_JAVA2RIM_ERR_INTERNAL_MSG="Error: ";
	public static final String APPLICATION_ATTRIBUTE_TEL_ERR__CODE="2071";
	public static final String APPLICATION_ATTRIBUTE_TEL_ERR_INTERNAL_MSG="Only digits allowed for a TEL datatype";
	public static final String APPLICATION_ATTRIBUTE_TS_ERR__CODE="2072";
	public static final String APPLICATION_ATTRIBUTE_TS_ERR_INTERNAL_MSG="Expected pattern is: ";
	
	
	public static final String APPLICATION_GETTING_ATTRIBUTE_ERR_CODE="208";
	public static final String APPLICATION_GETTING_ATTRIBUTE_ERR_INTERNAL_MSG="Attribute not found";
	public static final String APPLICATION_GETTING_METHOD_ERR_CODE="209";
	public static final String APPLICATION_GETTING_METHOD_ERR_INTERNAL_MSG="Method not found";
	
	public static final String APPLICATION_CATALOG_ADAPTER_LOAD_ERR_CODE="211";
	public static final String APPLICATION_CATALOG_ADAPTER_LOAD_ERR_INTERNAL_MSG="Problem with the Adapter Loading Step";

	public static final String APPLICATION_ATTRIBUTE_PATTERN_ERR_CODE="212";
	public static final String APPLICATION_ATTRIBUTE_PATTERN_ERR_INTERNAL_MSG="Pattern is null!";
	
	public static final String APPLICATION_OBJECT_LINK_ERR_CODE="250";
	public static final String APPLICATION_OBJECT_LINK_ERR_MSG="Error linking objects";
	public static final String APPLICATION_OBJECT_UNLINK_ERR_CODE="251";
	public static final String APPLICATION_OBJECT_UNLINK_ERR_MSG="Error unlinking objects";

	public static final String INTEGRETY_ATTRIBUTE_TYPE_ERR_CODE="301";
	public static final String INTEGRETY_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG="Error in type resolving";
	
	public static final String CONGRUENCE_GENERIC_ERR_CODE="401";
	public static final String CONGRUENCE_GENERIC_ERR_INTERNAL_MSG="Error in data congruence";
	
	public static final String PROCESS_EXECUTION_ERR_CODE="401";
	public static final String PROCESS_EXECUTION_ERR_INTERNAL_MSG="Error during business process execution";
	public static final String PROCESS_STARTING_ERR_CODE="402";
	public static final String PROCESS_STARTING_ERR_INTERNAL_MSG="Error during business process initialization";
	public static final String PROCESS_DEFINITION_LOAD_ERR_CODE="403";
	public static final String PROCESS_DEFINITION_LOAD_ERR_INTERNAL_MSG="Error during process definitions loading";
	public static final String PROCESS_CACHE_LIST_ELEMENT_CORRUPTED_ERR_INTERNAL_MSG ="RestoredProcessList cached is null";
	public static final String PROCESS_CACHE__ROOTNODE_ELEMENT_CORRUPTED_ERR_INTERNAL_MSG ="Root Node cached for phi solution is null";
	public static final String PROCESS_RESUMING_ERR_INTERNAL_MSG="Error during business process resuming";
	public static final String PROCESS_DEFINITION_SUPER_STATE_ERR_CODE ="404";
	public static final String PROCESS_LACK_ERR_INTERNAL_MSG="Error during business process resuming. Process Definition is no more present.";
	
	public static final String NAVIGATION_GENERIC_ERR_INTERNAL_MSG="Error during page navigation";	
	
	public static final String RULES_GENERIC_ERR_CODE="501";
	public static final String RULES_GENERIC_ERR_INTERNAL_MSG="Error during rule creation";
	public static final String RULES_LOADING_ERR_CODE="502";
	public static final String RULES_LOADING_ERR_INTERNAL_MSG="Error during rule loading";
	
	public static final String SECURITY_AUTHORIZATION_ERR_CODE="601";
	public static final String SECURITY_AUTHORIZATION_ERR_INTERNAL_MSG="Current user doesn't have permission to execute this operation";
	public static final String SECURITY_NOT_LOGGED_IN_ERR_CODE="602";
	public static final String SECURITY_NOT_LOGGED_IN_ERR_INTERNAL_MSG="You need to be logged in to perform this operation";
	public static final String SECURITY_DUPLICATE_USERNAME_ERR_CODE="603";
	public static final String SECURITY_DUPLICATE_USERNAME_ERR_INTERNAL_MSG="Username already registered, please choose another one";
	public static final String SECURITY_USERNAME_NOT_FOUND_ERR_INTERNAL_MSG="Username not registered";
	public static final String SECURITY_USERNAME_WRONG_PASSWORD_ERR_INTERNAL_MSG="Wrong username\\password combination";
	public static final String SECURITY_NOT_AUTHORIZED_ERR_CODE="604";
		
	public static final String VALIDATION_ATTRIBUTE_TYPE_ERR_CODE="701"; 
	public static final String VALIDATION_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG="Validation error for a type, it's expected a single value in a map values";
	public static final String VALIDATION_PQ_TYPE_ERR_CODE="702";
	public static final String VALIDATION_PQ_TYPE_ERR_INTERNAL_MSG="PQ Type Malformed, expected pattern is 'magnitude unit' blank space separated ','";
	public static final String VALIDATION_NIN_TYPE_FORMAT_ERR_CODE="703";
	public static final String VALIDATION_NIN_TYPE_FORMAT_ERR_INTERNAL_MSG="Wrong format for National Identification Number ";
	public static final String VALIDATION_NIN_TYPE_PARITY_CHECK_ERR_CODE="704";
	public static final String VALIDATION_NIN_TYPE_PARITY_CHECK_ERR_INTERNAL_MSG="Inserted code does not exist";
	public static final String VALIDATION_VALUE_ALREADY_PRESENT_ERR_CODE="705";
	public static final String VALIDATION_VALUE_ALREADY_PRESENT_ERR_INTERNAL_MSG="Inserted code is already registered ";
	public static final String VALIDATION_COMPARE_DATE_EQUAL_TO_ERR_CODE="706";
	public static final String VALIDATION_COMPARE_DATE_EQUAL_TO_ERR_INTERNAL_MSG="Ending Date must be equal to Starting Date";
	public static final String VALIDATION_COMPARE_DATE_INEQUAL_TO_ERR_CODE="707";
	public static final String VALIDATION_COMPARE_DATE_INEQUAL_TO_ERR_INTERNAL_MSG="Ending Date must not be equal to Starting Date";
	public static final String VALIDATION_COMPARE_DATE_GREATER_THAN_ERR_CODE="708";
	public static final String VALIDATION_COMPARE_DATE_GREATER_THAN_ERR_INTERNAL_MSG="Ending Date must be greater than Starting Date";
	public static final String VALIDATION_COMPARE_DATE_LESSER_THAN_ERR_CODE="709";
	public static final String VALIDATION_COMPARE_DATE_LESSER_THAN_ERR_INTERNAL_MSG="Ending Date must be lesser than Starting Date";
	public static final String VALIDATION_COMPARE_DATE_GREATER_THAN_OR_EQUAL_TO_ERR_CODE="710";
	public static final String VALIDATION_COMPARE_DATE_GREATER_THAN_OR_EQUAL_TO_ERR_INTERNAL_MSG="Ending Date must be greater than or equal to Starting Date";
	public static final String VALIDATION_COMPARE_DATE_LESSER_THAN_OR_EQUAL_TO_ERR_CODE="711";
	public static final String VALIDATION_COMPARE_DATE_LESSER_THAN_OR_EQUAL_TO_ERR_INTERNAL_MSG="Ending Date must be lesser than or equal to Starting Date";
	public static final String VALIDATION_COMPARE_DATE_NULL_DATE_ERR_CODE="712";
	public static final String VALIDATION_COMPARE_DATE_NULL_DATE__ERR_INTERNAL_MSG="To perform a comparation you must set both dates";
	public static final String VALIDATION_COMPARE_DATE_INVALID_DATE_FORMAT_ERR_CODE="713";
	public static final String VALIDATION_COMPARE_DATE_INVALID_DATE_FORMAT_ERR_INTERNAL_MSG="Invalid date format";
	public static final String VALIDATION_MAIL_INVALID_MAIL_FORMAT_ERR_CODE="714";
	public static final String VALIDATION_MAIL_INVALID_MAIL_FORMAT_ERR_INTERNAL_MSG="Invalid e-mail address format";
	public static final String VALIDATION_NUMBER_INVALID_NUMBER_FORMAT_ERR_CODE="715";
	public static final String VALIDATION_NUMBER_INVALID_NUMBER_FORMAT_ERR_INTERNAL_MSG="Invalid number format";
	public static final String VALIDATION_CHECK_FALSE_ERR_CODE="716";
	public static final String VALIDATION_CHECK_FALSE_ERR_INTERNAL_MSG="Checkbox value must be FALSE";
	public static final String VALIDATION_CHECK_TRUE_ERR_CODE="717";
	public static final String VALIDATION_CHECK_TRUE_ERR_INTERNAL_MSG="Checkbox value must be TRUE";
	public static final String VALIDATION_PHRASE_INVALID_PHRASE_FORMAT_ERR_CODE="718";
	public static final String VALIDATION_PHRASE_INVALID_PHRASE_FORMAT_ERR_INTERNAL_MSG="Invalid phrase format";
	public static final String VALIDATION_LETTER_NUMBER_ONLY_NOSPACE_ERR_CODE="7188";
	public static final String VALIDATION_LETTER_NUMBER_ONLY_NOSPACE_ERR_INTERNAL_MSG="Letters and numbers only allowed, no spaces";
	public static final String VALIDATION_DATE_INVALID_FORMAT_ERR_CODE="719";
	public static final String VALIDATION_DATE_INVALID_FORMAT_ERR_INTERNAL_MSG="Invalid date format";
	public static final String VALIDATION_DATE_INVALID_PATTERN_ERR_CODE="720";
	public static final String VALIDATION_DATE_INVALID_PATTERN_ERR_CODE_B="720b";
	public static final String VALIDATION_DATE_INVALID_PATTERN_ERR_INTERNAL_MSG="Invalid date format";
	public static final String VALIDATION_DATE_INVALID_COMPONENT_ERR_CODE="721";
	public static final String VALIDATION_DATE_INVALID_COMPONENT_ERR_INTERNAL_MSG="Invalid component";
	public static final String VALIDATION_PIVA_TYPE_FORMAT_ERR_CODE="722";
	public static final String VALIDATION_PIVA_TYPE_FORMAT_ERR_INTERNAL_MSG="Wrong format for code";
	public static final String VALIDATION_PIVA_TYPE_PARITY_CHECK_ERR_CODE="723";
	public static final String VALIDATION_PIVA_TYPE_PARITY_CHECK_ERR_INTERNAL_MSG="Inserted code does not exist";
	public static final String VALIDATION_TIME_INVALID_HHMM_FORMAT_ERR_CODE="724";
	public static final String VALIDATION_TIME_INVALID_HHMM_FORMAT_ERR_INTERNAL_MSG="Invalid time hh:mm format";

	
	public static final String VALIDATION_MINOR_OF_TODAY="730";
	public static final String VALIDATION_MINOR_EQUAL_OF_TODAY="731";
	public static final String VALIDATION_MAJOR_OF_TODAY="732";
	public static final String VALIDATION_MAJOR_EQUAL_OF_TODAY="733";


	
	public static final String SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE="801"; 
	public static final String SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG="Schedule management error";
	
	public static final String WEB_SERVICE_ERR_CODE="901"; 
	public static final String WEB_SERVICE_ERR_INTERNAL_MSG="Error calling web service";

	public static final String APPLICATION_STATE_MACHINE_ERR_CODE="1001"; 
	public static final String APPLICATION_STATE_MACHINE_ERR_INTERNAL_MSG="Operation not allowed, check the state of your data and application";

	public static final String APPLICATION_STATE_MACHINE_RMIMLINK_ERR_CODE="1002"; 
	public static final String APPLICATION_STATE_MACHINE_RMIMLINK_ERR_INTERNAL_MSG="Operation not allowed, some problems arised during RMIM LINK management.";
	
	public static final String VALIDATION_LATITUDE_OUT_OF_RANGE_ERR_CODE="1100";
	public static final String VALIDATION_LATITUDE_OUT_OF_RANGE_ERR_INTERNAL_MSG="Latitude must be in the -90 / +90 range";
	public static final String VALIDATION_LONGITUDE_OUT_OF_RANGE_ERR_CODE="1101";
	public static final String VALIDATION_LONGITUDE_OUT_OF_RANGE_ERR_INTERNAL_MSG="Longitude must be in the -180 / +180 range";
	
	public static final String VALIDATION_PRESSURE_FORMAT_ERR_CODE="1702";
	public static final String VALIDATION_PRESSURE_FORMAT_ERR_INTERNAL_MSG="Invalid value. Insert number in this format: 12345.12";
	
	public static final String VALIDATION_TEMPERATURE_FORMAT_ERR_CODE="1703";
	public static final String VALIDATION_TEMPERATURE_FORMAT_ERR_INTERNAL_MSG="Invalid value. Insert number in this format: 123.12";
	
	public static final String VALIDATION_CAPACITY_FORMAT_ERR_CODE="1704";
	public static final String VALIDATION_CAPACITY_FORMAT_ERR_INTERNAL_MSG="Invalid value. Insert number in this format: 123456789.12";
	
	public static final String VALIDATION_POTENCY_FORMAT_ERR_CODE="1705";
	public static final String VALIDATION_POTENCY_FORMAT_ERR_INTERNAL_MSG="Invalid value. Insert number in this format: 123456.12";

	public static final String VALIDATION_ALFRESCO_DUPLICATE_NAME_ERR_CODE="2000";
	public static final String VALIDATION_ALFRESCO_DUPLICATE_NAME_INTERNAL_MSG="Invalid name: a document with the same name already exists.";
	
}
