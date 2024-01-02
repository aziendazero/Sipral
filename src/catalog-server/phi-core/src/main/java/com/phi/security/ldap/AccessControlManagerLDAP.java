package com.phi.security.ldap;

import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.jboss.seam.core.Conversation;
import com.phi.cs.repository.RepositoryManager;
//import java.util.Enumeration;
//import javax.naming.NameNotFoundException;
//import javax.naming.NamingException;
//import javax.naming.SizeLimitExceededException;
//import javax.naming.directory.Attribute;
//import javax.naming.directory.Attributes;
//import javax.naming.directory.SearchControls;
//import javax.naming.directory.SearchResult;
//import org.jboss.seam.annotations.FlushModeType;
//import com.phi.security.AccessControlAction;

public class AccessControlManagerLDAP {
	
	private static final Logger log = Logger.getLogger(AccessControlManagerLDAP.class);
	
	public int validateUser(String userName, String userPassword) {
		DirContext ctx = null;
		boolean auth = false;
		int ret=-1;
		
		RepositoryManager rm = RepositoryManager.instance();
			
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			
			//Needed for the Bind (User Authorized to Query the LDAP server)
			env.put(Context.SECURITY_AUTHENTICATION, rm.getSecurityAuthenticationLDAP());
			env.put(Context.INITIAL_CONTEXT_FACTORY, rm.getinItialContextFactoryLDAP());
			
			env.put(Context.PROVIDER_URL, "ldap://" + rm.getHostLDAP() + ":" + rm.getPortLDAP());
			
			env.put(Context.SECURITY_PRINCIPAL, 	userName + rm.getDomainLDAP());
			env.put(Context.SECURITY_CREDENTIALS, 	userPassword);
			
			//env.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
			
			ctx = new InitialLdapContext(env, null);
			//printUserInfo(ctx, userName);
			
			auth = true;
			ret = 0;

		} catch (AuthenticationException e) {
			/** LDAP: error code 49
			 *  525 	username is invalid
			 *  52e 	username is valid but password/credential is invalid
			 *  530 	not permitted to logon at this time
			 *  531 	not permitted to logon at this workstation
			 *  532 	password expired 			
			 *  533 	account disabled
			 *  701 	account expired
			 *  773 	user must reset password
			 *  775 	user account locked */

			String errorMSG = e.getMessage();
			
			//LDAP wrong username or password 
			if (errorMSG.contains("data 52")) 
				ret = 1;
			
			//LDAP correct user/password but there's a problem in account 
			//(e.g. expired or any other LDAP account/server problem) 
			else ret = -1;
			
		} catch (Exception e) {
			log.error("LDAP - Generic arror validating user: " + userName, e);
			ret = -1;
		
		} finally {
			Conversation conversation = Conversation.instance();

			if (log.isDebugEnabled()) {
				log.debug("Authenticated: " + userName);
			} 
			
			MDC.put("username", userName);
			
			log.info("[cid="+conversation.getId()+"] Authentication via LDAP - Username: "+userName+" | Valid: " + auth);
			
			if (ctx != null) {
				try {
					ctx.close();
				} catch (final Exception e) {
					log.error("LDAP - Generic error closing context ", e);
				}
			}
		}
		
		return ret;
		
	}
	
	/* DON'T REMOVE
	 * private static void printUserInfo(DirContext ctx, String userName) {
		try {
			String filter = "(&(objectClass=user),(sAMAccountName=" + userName + "))";
			Enumeration searchResults = ctx.search(LDAP_SEARCH_BASE, filter, getSimpleSearchControls());
			
			int entryNo = 0;
			while (searchResults.hasMoreElements()) {
				entryNo++;
				System.out.println("Entry n. " + entryNo);
				SearchResult entry = (SearchResult) searchResults.nextElement();
				Attributes attrSet = entry.getAttributes();
				for (Enumeration i = attrSet.getIDs(); i.hasMoreElements();) {
					String attrName = (String) i.nextElement();
					Attribute attr = attrSet.get(attrName);
					Object attrValue = attr.get(0);
					if (attrValue instanceof byte[]) {
						byte[] objectSid = (byte[]) attrValue;
						System.out.print(attrName + "=byte[" + objectSid.length	+ "]=");
						
						for (int ib = 0; ib < objectSid.length; ib++) {
							System.out.print(toHex(objectSid[ib]) + " ");
						}
						System.out.println();
					} else {
						System.out.println(attrName + "=" + attrValue);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}*/
	
	/*private static String toHex(byte b) {
		final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		int value;
		if (b < 0) {
			value = 256 + b;
		} else {
			value = b;
		}
		int digit1 = value / 16;
		int digit2 = value - (digit1 * 16);
		StringBuffer sb = new StringBuffer();
		sb.append(digits[digit1]);
		sb.append(digits[digit2]);
		return sb.toString();
	}
	
	private static SearchControls getSimpleSearchControls() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		return searchControls;
	}
	*/
}

