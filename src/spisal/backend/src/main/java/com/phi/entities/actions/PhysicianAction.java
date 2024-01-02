package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.Physician;

@BypassInterceptors
@Name("PhysicianAction")
@Scope(ScopeType.CONVERSATION)
public class PhysicianAction extends BaseAction<Physician, Long> {

	private static final long serialVersionUID = -5856716131543953549L;

	public static PhysicianAction instance() {
        return (PhysicianAction) Component.getInstance(PhysicianAction.class, ScopeType.CONVERSATION);
    }
	
	//same as ProcolloAction.setUlss
	public void setUlss() {
		try {
			//togliere il fitro prima!!!
			getEqual().remove("organization.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("organization.internalId", id);
				}
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} 
	}
	
	public void filterValid(){
		Boolean valid = (Boolean)getTemporary().get("filterValid");
	
		if(Boolean.TRUE.equals(valid)){
			Date currentDate = new Date();
//			((FilterMap)getLessEqual()).putOr("validity.low", currentDate,null);
			((FilterMap)getGreaterEqual()).putOr("validity.high", currentDate,null);
		}else{
//			getLessEqual().remove("validity.low");
//			getGreaterEqual().remove("validity.low");
			getGreaterEqual().remove("validity.high");
		}
	}
	
	public void filterExpired(){
		Boolean expired = (Boolean)getTemporary().get("filterExpired");
		getLessEqual().remove("validity.high");
		if(Boolean.TRUE.equals(expired)){
			if(getGreaterEqual().get("validity.high")!=null){
				getGreaterEqual().remove("validity.high");
			}else{
				Date currentDate = new Date();
				getLessEqual().put("validity.high", currentDate);
			}
		}
	}

	public boolean hasOrg(Long orgId, List<SelectItem> orgList){
		if(orgList!=null && orgId!=null){
			for(SelectItem s : orgList){
				if(orgId.equals(s.getValue()))
					return true;
			}
		}
		
		return false;
	}

	public void filterOwnOrgs(List<SelectItem> orgList){
		if(orgList!=null){
			List<Long> ids = new ArrayList<Long>();
			for(SelectItem s : orgList){
				ids.add((Long)s.getValue());
			}
			OrganizationAction orgAction = OrganizationAction.instance();
			orgAction.getIn().remove("internalId");
			if(!ids.isEmpty()){
				orgAction.getIn().put("internalId", ids);
			}
		}
	}
	
	public boolean checkDuplicateName(Physician p){
		
		boolean result = false;
		
		if(p.getInternalId()==0){
			
			String qryDuplicateCheck = "SELECT COUNT(p) FROM Physician p " +
				"WHERE name_fam = :fam and name_giv = :giv";
			
			Query duplicateCheck = ca.createQuery(qryDuplicateCheck);
			duplicateCheck.setParameter("fam", p.getName().getFam().trim());
			duplicateCheck.setParameter("giv", p.getName().getGiv().trim());
			
			Long countP = (Long)duplicateCheck.getSingleResult();
			if(countP!=null && countP>0){
				result=true;
			}
			
		}
		
		return result;
		
	}
	
	
	public boolean checkDuplicateRegionalCode(Physician p){
		
		boolean result = false;
		
		if(p.getInternalId()==0 && p.getRegionalCode()!=null){
			
			String qryDuplicateCheck = "SELECT COUNT(p) FROM Physician p " +
				"WHERE regional_code = :rc";
			
			Query duplicateCheck = ca.createQuery(qryDuplicateCheck);
			duplicateCheck.setParameter("rc", p.getRegionalCode().trim());
			
			
			Long countP = (Long)duplicateCheck.getSingleResult();
			if(countP!=null && countP>0){
				result=true;
			}
			
			
		}
		
		return result;
		
	}
	
}