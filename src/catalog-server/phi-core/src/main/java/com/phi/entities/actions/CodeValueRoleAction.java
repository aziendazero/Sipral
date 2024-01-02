package com.phi.entities.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.baseEntity.ProcSecurity;
import com.phi.entities.baseEntity.ProcSecurityRole;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("CodeValueRoleAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueRoleAction extends CodeValueBaseAction<CodeValueRole>{

	private static final long serialVersionUID = 1094712915777700240L;
	private static final Logger log = Logger.getLogger(CodeValueRoleAction.class);
	
	public static CodeValueRoleAction instance() {
		return (CodeValueRoleAction) Component.getInstance(CodeValueRoleAction.class, ScopeType.CONVERSATION);
	}
	
	/**
	 * Clone and associate it to all processes of original role
	 * @param original CodeValueRole to clone
	 * @param newName new name
	 * @return cloned CodeValueRole with new name
	 */
	public CodeValueRole coloneRole(CodeValueRole original, String newName) {
		
		CodeValueRole clone = new CodeValueRole();
		
		try {
			//Clone
			
			Date now = new Date();
			
			String capitalizedName = WordUtils.capitalizeFully(newName);
			char c[] = capitalizedName.toCharArray();
			c[0] = Character.toLowerCase(c[0]);
			capitalizedName = new String(c);
			capitalizedName = capitalizedName.replace(" ", "");
			
			clone.setCreator(UserBean.instance().getUsername());
			clone.setChangeReason("Clone of role " + original.getOid());

			clone.setCode(newName.toLowerCase());
			clone.setDisplayName(WordUtils.capitalize(newName));
			
			clone.setCodeSystem(original.getCodeSystem());
			clone.setId(original.getParent().getOid() +  "." + capitalizedName);
			clone.setOid(original.getParent().getOid() +  "." + capitalizedName);
			clone.setParent(original.getParent());
			
			clone.setValidFrom(now);
			clone.setRevisedDate(now);

			clone.setStatus(original.getStatus());
			clone.setType(original.getType());
			
			clone.setVersion(original.getVersion());
			
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			
			ca.create(clone);
			ca.flushSession();
			
			//Associate cloned role to all processes of original role
			
			List<ProcSecurity> procSecurities = findProcSecurityByRole(original);
			
			for (ProcSecurity procSecuritiy : procSecurities) {
				ProcSecurityRole procSecurityRole = new ProcSecurityRole();
				procSecurityRole.setRole(clone);
				procSecuritiy.addProcSecurityRole(procSecurityRole);
			}
			
			//Associate cloned role to all sdlocs of original role
			
			for (ServiceDeliveryLocation sdl : original.getEnabledServiceDeliveryLocations()) {
				clone.addEnabledServiceDeliveryLocations(sdl);
			}
			
			ca.flushSession();
			
		} catch (Exception e) {
			log.error("Error cloning role " + original.getOid(), e);
		}
		
		return clone;
	}
	
	/**
	 * Delete a CodeValueRole if are no EmployeeRoles connected to it
	 * Remove associations to ProcSecurity
	 * @param role to be deleted
	 * @return true if delete was successful
	 */
	public boolean deleteRole(CodeValueRole role) {
		
		try {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			
			//Check if role already used by Employee
			String hql = "SELECT e.username FROM Employee e JOIN e.employeeRole er JOIN er.code roleCode WHERE roleCode.id = :roleId";
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("roleId", role.getId());
			
			@SuppressWarnings("unchecked")
			List<String> usernames = ca.executeHQLwithParameters(hql, pars);
			
			if (!usernames.isEmpty()) {
				getTemporary().put("roleUsedBy", usernames);
				return false;
			} else {
				List<ProcSecurity> procSecurities = findProcSecurityByRole(role);
				
				for (ProcSecurity procSecuritiy : procSecurities) {
					for (Iterator<ProcSecurityRole> iterator = procSecuritiy.getProcSecurityRole().iterator(); iterator.hasNext();) {
						ProcSecurityRole procSecuritiyRole = iterator.next();
						if (procSecuritiyRole.getRole().equals(role)) {
							iterator.remove();
							ca.delete(procSecuritiyRole);
						}
					}
				}
				
				List<CodeValueParameter> applicationParameters = findApplicationParameterByRole(role);
				
				for (CodeValueParameter par : applicationParameters) {
					par.setRole(null);
					ca.create(par);
				}
				
				ca.delete(role);
				
				ca.flushSession();
				
				getTemporary().remove("roleUsedBy");
				return true;
			}
			
			
		} catch (Exception e) {
			log.error("Error deleting role " + role.getOid(), e);
		}
		return false;
	}
	
	/**
	 * Find ProcSecurity by role
	 * @param role
	 * @return ProcSecurity enabled by role
	 * @throws Exception
	 */
	private List<ProcSecurity> findProcSecurityByRole(CodeValueRole role) throws Exception {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String hql = "SELECT ps FROM ProcSecurity ps JOIN ps.procSecurityRole psr WHERE psr.role.id = :originalId";
		
		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("originalId", role.getId());
		
		@SuppressWarnings("unchecked")
		List<ProcSecurity> procSecurities = ca.executeHQLwithParameters(hql, pars);
		
		return procSecurities;
	}
	
	/**
	 * Find CodeValueParameter by role
	 * @param role
	 * @return CodeValueParameter enabled by role
	 * @throws Exception
	 */
	private List<CodeValueParameter> findApplicationParameterByRole(CodeValueRole role) throws Exception {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String hql = "SELECT par FROM CodeValueParameter par WHERE par.role.id = :originalId";
		
		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("originalId", role.getId());
		
		@SuppressWarnings("unchecked")
		List<CodeValueParameter> applicationParameters = ca.executeHQLwithParameters(hql, pars);
		
		return applicationParameters;
	}

}