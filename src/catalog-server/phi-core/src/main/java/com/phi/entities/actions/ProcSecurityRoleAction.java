package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.PHIError;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.ProcSecurity;
import com.phi.entities.baseEntity.ProcSecurityRole;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ProcSecurityRoleAction")
@Scope(ScopeType.CONVERSATION)
public class ProcSecurityRoleAction extends BaseAction<ProcSecurityRole, Long> {

	private static final Logger log = Logger.getLogger(ProcSecurityRoleAction.class);
	private static final long serialVersionUID = 977169201L;

	public static ProcSecurityRoleAction instance() {
		return (ProcSecurityRoleAction) Component.getInstance(ProcSecurityRoleAction.class, ScopeType.CONVERSATION);
	}
	
	private enum Security4Role {enabled, disabled, readOnly};
	
	/**
	 * Domain of values of roleManagementDetails form to enable, disable or readonly o process for a role
	 */
	public List<SelectItem> getSecurity4RoleDomain() {
		List<SelectItem> ret = new ArrayList<SelectItem>();

		ret.add(new SelectItem(Security4Role.enabled.name(), "Abilitato"));
		ret.add(new SelectItem(Security4Role.readOnly.name(), "Sola lettura"));
		ret.add(new SelectItem(Security4Role.disabled.name(), "Disabilitato"));
		return ret;
	}


	private Map<Long, String> security4role;
	
	
	public Map<Long, String> getSecurity4role() {
		return security4role;
	}


	/**
	 * Convert list of ProcSecurity.procSecurityRole to security4role
	 * roleManagementDetails form
	 * @param procSecurities
	 * @param role
	 */
	public void calculateSecurity4Role(List<ProcSecurity> procSecurities, CodeValueRole role) {
		security4role = new HashMap<Long, String>();
		Security4Role value;
		for (ProcSecurity procSecurity : procSecurities) {
			value = Security4Role.disabled;
			for (ProcSecurityRole procSecurityRole : procSecurity.getProcSecurityRole()) {
				if (procSecurityRole.getRole().equals(role)) {
					if (procSecurityRole.getReadonly() == null || procSecurityRole.getReadonly() == false) {
						value = Security4Role.enabled;
					} else {
						value = Security4Role.readOnly;
					}
					break;
				}
			}
			security4role.put(procSecurity.getInternalId(), value.name());
		}
	}
	
	
	/**
	 * Convert security4role to ProcSecurity.procSecurityRole
	 * roleManagementDetails form 
	 * @param procSecurities
	 * @param role
	 * @throws PhiException 
	 */
	public void updateProcSecurity(List<ProcSecurity> procSecurities, CodeValueRole role) throws PhiException {
		try {
			for (ProcSecurity procSecurity : procSecurities) {
				String value = security4role.get(procSecurity.getInternalId());
				
				boolean found = false;
				boolean modified = false;
				for (Iterator<ProcSecurityRole> iterator = procSecurity.getProcSecurityRole().iterator(); iterator.hasNext();) {
					ProcSecurityRole procSecurityRole = iterator.next();
					
					if (procSecurityRole.getRole().equals(role)) {
						if (Security4Role.enabled.name().equals(value)) {
							procSecurityRole.setReadonly(false);
						} else if (Security4Role.readOnly.name().equals(value)) {
							procSecurityRole.setReadonly(true);
						} else if (Security4Role.disabled.name().equals(value)) {
							iterator.remove();
						}
						found = true;
						modified = true;
						break;
					}
				}
				
				if (found == false) {
					if (Security4Role.enabled.name().equals(value)) {
						ProcSecurityRole procSecurityRole = new ProcSecurityRole();
						procSecurityRole.setRole(role);
						procSecurity.addProcSecurityRole(procSecurityRole);
						modified = true;
					} else if (Security4Role.readOnly.name().equals(value)) {
						ProcSecurityRole procSecurityRole = new ProcSecurityRole();
						procSecurityRole.setRole(role);
						procSecurityRole.setReadonly(true);
						procSecurity.addProcSecurityRole(procSecurityRole);
						modified = true;
					} 
	//				else if (Security4Role.disabled.name().equals(value)) {
	//					nothing to do
	//				}
				}
				if (modified) {
					ca.create(procSecurity);
				}
			}
		} catch (PersistenceException e) {
			log.error("Error converting security4role to ProcSecurity.procSecurityRole " + e.getMessage());
			throw new PhiException("Error converting security4role to ProcSecurity.procSecurityRole", e, ErrorConstants.APPLICATION_GENERIC_ERR_CODE);
		}
	}


}