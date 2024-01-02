package com.phi.dictionaryManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;


import com.phi.entities.dataTypes.CodeValue;

public class Globals {


	// system dependent New Line character
	public static final String NL = System.getProperty("line.separator");
	
	// system dependent File Separator
	public static final String FS = File.separator;
	
	//public static final int COLUMN_OID_LENGTH = 65;
	public static final int COLUMN_CODEVALUE_CODE_LENGTH = 65;
	public static final int COLUMN_CODEVALUE_DISPLAYNAME_LENGTH = 255;
	//public static final int COLUMN_KEYWORDS_LENGTH = 65;
	public static final int COLUMN_CODEVALUE_DESCRIPTION_LENGTH = 765;
	public static final int COLUMN_CODEVALUE_CHANGEREASON_LENGTH = 255;
	public static final int COLUMN_CODEEXTENSIONS_PROPERTYNAME_LENGTH = 35;
	
	public static final int CODEUSERS_USERNAME_LENGTH = 65;
	public static final int CODEUSERS_PASSWORD_LENGTH = 65;
	
	// LANGUAGES
	public static final int LANG_CODES = 0;
	public static final int LANG_NAMES = 1;
	
	public static final int CODEVALUE_MAX_RESULTS = 10000;
	public static final int CODEVALUE_MAX_BROTHERS = 200;
	
	public static final int CODEVALUE_INTERNAL_DISCRIMINATOR_DEFAULT = 4;
	
	
	//public static final char[] IMPORT_CSV_DELIMITERS = {',',';',':','|'};
	public static final String[] IMPORT_CSV_DELIMITERS = {",",";",":","|","TAB"};
	public static final int IMPORT_CSV_DELIMITER_PIPE = 4;
	
	public static final String CODEVALUE_CODE = "code";
	public static final String CODEVALUE_DISPLAYNAME = "displayName";
	public static final String CODEVALUE_VALIDFROM = "validFrom";
	public static final String CODEVALUE_VALIDTO = "validTo";
	public static final String CODEVALUE_DESCRIPTION = "description";
	public static final String CODEVALUE_KEYWORDS = "keywords";
	// FBMOD ****************************************************************************************
	public static final String CODEVALUE_TYPECODE = "typeCode";
	public static final String CODEVALUE_IDSUFFIX = "idSuffixCode";
	public static final String CODEVALUE_KEYCODE = "keyCodeCode";
	public static final String CODEVALUE_MAPPED_CODE_SYSTEM = "mappedCodeValue";
	public static final String CODEVALUE_PARENTID = "parentID";
	public static final String CODEVALUE_SEQUENCENUMBER = "sequenceNumber";
	public static final String CODEVALUE_STATUS = "status";

	public static final String CODEEQUIVALENT_TARGET = "CODE EQUIVALENT [T]";
	public static final String CODEEQUIVALENT_SOURCE = "CODE EQUIVALENT [S]";
	
	public static final String CODEVALUE_TYPE_TOPLEVEL = "T";
	public static final String CODEVALUE_TYPE_CONCEPTLEAF = "C";
	public static final String CODEVALUE_TYPE_GENERICDOMAIN = "D";
	public static final String CODEVALUE_TYPE_SPECIALIZEDDOMAIN = "S";
	public static final String CODEVALUE_TYPE_ABSTRACTDOMAIN = "A";
//	public static final String CODEVALUE_TYPE_VIRTUAL = "V";
	//public static final String[] CODEVALUE_TYPE_ALL = { CODEVALUE_TYPE_ABSTRACTDOMAIN, CODEVALUE_TYPE_SPECIALIZEDDOMAIN, CODEVALUE_TYPE_ABSTRACTDOMAIN };

	public static final String CODEVALUE_EMPTYCODE = "-";
	
	public static final int CODEVALUE_STATUS_PROPOSED = 0;
	public static final int CODEVALUE_STATUS_ACTIVE = 1;
	public static final int CODEVALUE_STATUS_RETIRED = 2;
	public static final int CODEVALUE_STATUS_DELETED = 3;
	public static final String[] CODEVALUE_STATUS_ALL = { "PROPOSED", "ACTIVE", "RETIRED", "DELETED" };
//	public static enum CV_STATUS{PROPOSED,ACTIVE,RETIRED,DELETED};

	public static final Integer CODEVALUE_EXTENSION_GETTER = 0;
	public static final Integer CODEVALUE_EXTENSION_SETTER = 1;
	
	public static final Integer CODEVALUE_EQUIVALENT_GETTER = 0;
	public static final Integer CODEVALUE_EQUIVALENT_SETTER = 1;
	

	public static final int CODESYSTEM_STATUS_PROPOSED = 0;
	public static final int CODESYSTEM_STATUS_ACTIVE = 1;
	public static final int CODESYSTEM_STATUS_RETIRED = 2;
	public static final int CODESYSTEM_STATUS_DELETED = 3;
//	public static enum CS_STATUS{PROPOSED,ACTIVE,RETIRED,DELETED};

	public static final int COLUMN_CODESYSTEM_DESCRIPTION_LENGTH = 255;

	// FBMOD: CUSTOM DICTIONARY FILE WITH DOTTED OIDs [ie: 6.25 or 11.155 are short root OIDs] **********************************************
	public static final boolean NOT_DOTTED_TO_DOTTED = false;

	public static final String DUPLICATED_CODE_POSTFIX = "-1";
	
	public static final String TEMPORARY_CODE_SYSTEM = "TEMPORARYCODESYSTEM";
	public static final String TEMPORARY_CODE_SYSTEM_VERSION = "0";
	public static final Integer TEMPORARY_CODE_SYSTEM_ID = (TEMPORARY_CODE_SYSTEM + " " +TEMPORARY_CODE_SYSTEM_VERSION).hashCode();
	public static final String TEMPORARY_UPDATE_CODE_SYSTEM = "TEMPORARYCOMPARED";
	public static final String TEMPORARY_UPDATE_CODE_SYSTEM_VERSION = "0";
	public static final Integer TEMPORARY_UPDATE_CODE_SYSTEM_ID = (TEMPORARY_UPDATE_CODE_SYSTEM + " " +TEMPORARY_UPDATE_CODE_SYSTEM_VERSION).hashCode();
	//public static final String CODEEQUIVALENT_STATUS_ACTIVE = "ACTIVE";
	
	public static final String DUMMY_DESCRIPTION = "Dummy root Code Value, useful for linking full Code System to a binding";

	// EXPORT CONSTANTS
	public static final String EXPORT_EXTENSION = "EXTENSION[";
	public static final String EXPORT_EXTENSIONEND = "]";
	public static final String EXPORT_RELATION = "RELATION[";
	public static final String EXPORT_RELATIONEND = "]";
	public static final String EXPORT_CET = "CODE EQUIVALENT [T]";
	public static final String EXPORT_CES = "CODE EQUIVALENT [S]";
	public static final String EXPORT_MAPPEDCODESYSTEM = "MAPPED CODE SYSTEM";
	public static final String EXPORT_ELEMENTTYPE = "ELEMENT TYPE";
	public static final String EXPORT_IDSUFFIX = "ID SUFFIX";
	public static final String EXPORT_CODE = "CODE";
	public static final String EXPORT_NAME = "NAME";
	public static final String EXPORT_DESCRIPTION = "DESCRIPTION";
	public static final String EXPORT_STATUS = "STATUS";
	public static final String EXPORT_TRANSLATION = "TRANSLATION[";
	public static final String EXPORT_TRANSLATIONEND = "]";
	public static final String EXPORT_VALIDFROM = "VALID FROM";
	public static final String EXPORT_VALIDTO = "VALID TO";
	public static final String EXPORT_DATEFORMAT = "dd/MM/yyyy HH:mm";
	public static final String EXPORT_SEQUENCENUMBER = "SEQUENCE NUMBER";
	
	// DB CONFIGURATION
	public static final String DB_CONF_HBM2DDL_AUTO_CREATE = "create";
	public static final String DB_CONF_HBM2DDL_AUTO_UPDATE = "update";
	public static final String DB_CONF_HBM2DDL_AUTO_DISABLED = "disabled";
	public static final String DB_CONF_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

	public static final String DB_CONNECTIONS = "db.connections.xml";
	public static final String DB_LOCAL = "local";
	public static final String DB_COMMON_NODE = "commons";
	public static final String DB_CONNECTION_NODE = "connection";
	public static final String DB_DEFAULT_CONN = "rimpdm15";
	public static final String DB_DRIVER_CLASS = "hibernate.connection.driver_class";
	public static final String DB_DIALECT_CLASS = "hibernate.dialect";
	public static final String DB_CONNECTION_URL = "hibernate.connection.url";
	public static final String DB_CONNECTION_USERNAME = "hibernate.connection.username";
	public static final String DB_CONNECTION_PASSWORD = "hibernate.connection.password";
	public static final String DB_FORMAT_SQL = "hibernate.format_sql";
	public static final String DB_SHOW_SQL = "hibernate.show_sql";
	public static final String DB_USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
	public static final String DB_CACHE_PROVIDER_CLASS = "org.hibernate.cache.HashtableCacheProvider";

	public static final String DOT_EXT_PROPERTIES = ".properties";
	
	public static final String PROPS_CODEVALUEMODEL = "CODEVALUEMODEL";
	public static final String PROPS_CODESYSTEMID = "CODESYSTEMID";
	public static final String PROPS_CODESYSTEMNAME = "CODESYSTEMNAME";
	public static final String PROPS_CODESYSTEMDISPLAYNAME = "CODESYSTEMDISPLAYNAME";
	public static final String PROPS_VERSION = "VERSION";
	public static final String PROPS_LANGUAGE = "LANGUAGE";
	public static final String PROPS_DESCRIPTION = "DESCRIPTION";
	public static final String PROPS_KEYWORDS = "KEYWORDS";
	public static final String PROPS_AUTHORITYNAME = "AUTHORITYNAME";
	public static final String PROPS_AUTHORITYDESCRIPTION = "AUTHORITYDESCRIPTION";
	public static final String PROPS_AUTHORITYCONTACTINFORMATION = "AUTHORITYCONTACTINFORMATION";
	public static final String PROPS_VALIDFROM = "VALIDFROM";
	public static final String PROPS_VALIDTO = "VALIDTO";
	
	public static enum Extended {

		SCORE, CUSTOM;

		@Override
		public String toString() {
			switch (this) {
			case SCORE:
				return "SCORE";
			case CUSTOM:
				return "...";
			default:
				return "";
			}
		}
	} 
	
	


}
