package com.phi.ps;

import java.util.ArrayList;
import java.util.List;

import com.phi.entities.baseEntity.ProcSecurityRole;

/**
 * Stores enabled roles and ServicedeliveryLocation idis.
 * Used in ProcessManagerImpl.procSecurity
 */

public class ProcessSecurity {
	
	private List<ProcessSecurityRole> enabledRoles;
//	private List<Long> enabledSdlocs;
	
	public List<ProcessSecurityRole> getEnabledRoles() {
		return enabledRoles;
	}
	public void setEnabledRoles(List<ProcessSecurityRole> enabledRoles) {
		this.enabledRoles = enabledRoles;
	}
//	public List<Long> getEnabledSdlocs() {
//		return enabledSdlocs;
//	}
//	public void setEnabledSdlocs(List<Long> ebabledSdlocs) {
//		this.enabledSdlocs = ebabledSdlocs;
//	}
//	
	public void setProcSecurityRole(List<ProcSecurityRole> procSecurityRoles) {
		if (procSecurityRoles != null && !procSecurityRoles.isEmpty()) {
			enabledRoles = new ArrayList<ProcessSecurityRole>();
			for (ProcSecurityRole psr : procSecurityRoles) {
				ProcessSecurityRole processSecurityRole = new ProcessSecurityRole();
				processSecurityRole.setRole(psr.getRole().getCode());
				if (Boolean.TRUE.equals(psr.getReadonly())) {
					processSecurityRole.setReadonly(true);
				}
				enabledRoles.add(processSecurityRole);
			}
		}
	}

}