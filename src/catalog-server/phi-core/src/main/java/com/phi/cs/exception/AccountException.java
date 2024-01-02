package com.phi.cs.exception;

import java.util.Locale;
import java.util.ResourceBundle;


public class AccountException extends Exception {
	
	private static final long serialVersionUID = 5183670696234491264L;

	public static String WRONG_ACCOUNT_MESSAGE = "WRONG_ACCOUNT_MESSAGE";
	public static String NOT_YET_ACTIVE_ACCOUNT_MESSAGE = "NOT_YET_ACTIVE_ACCOUNT_MESSAGE";
	public static String EXPIRED_ACCOUNT_MESSAGE = "EXPIRED_ACCOUNT_MESSAGE";
	public static String DISABLED_ACCOUNT_MESSAGE = "DISABLED_ACCOUNT_MESSAGE";
	public static String CALL_ASSISTANCE_MESSAGE = "CALL_ASSISTANCE_MESSAGE";
	
	public static String PASSWORD_OLD_INCORRECT = "PASSWORD_OLD_INCORRECT";
	public static String PASSWORD_OLD_NEW_EQUAL = "PASSWORD_OLD_NEW_EQUAL";
	public static String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
	
	public static String LDAP_ACCOUNT_DOESNT_EXIST = "LDAP_ACCOUNT_DOESNT_EXIST";
	public static String LDAP_GENERIC_ERROR = "LDAP_GENERIC_ERROR";
	public static String LDAP_OK_NO_PHI_ACCOUNT = "LDAP_OK_NO_PHI_ACCOUNT";
	public static String CAS_OK_NO_PHI_ACCOUNT = LDAP_OK_NO_PHI_ACCOUNT;

	
	public AccountException(String message) {
		super(ResourceBundle.getBundle("bundle.error.messages", new Locale(org.jboss.seam.core.Locale.instance().getLanguage())).getString(message));
	}
	
}