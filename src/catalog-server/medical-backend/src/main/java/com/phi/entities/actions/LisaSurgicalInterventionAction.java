package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.datamodel.IdataModel;
import com.phi.entities.act.LisaSurgicalIntervention;

@BypassInterceptors
@Name("LisaSurgicalInterventionAction")
@Scope(ScopeType.CONVERSATION)
public class LisaSurgicalInterventionAction extends BaseAction<LisaSurgicalIntervention, Long> {

	private static final long serialVersionUID = 1220419852L;
	
	protected static final String serviceDeliveryLocationId = "serviceDeliveryLocation.internalId";

	public static LisaSurgicalInterventionAction instance() {
		return (LisaSurgicalInterventionAction) Component.getInstance(LisaSurgicalInterventionAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Set an IN criteria type to be used to filter the surgeries interventions.
	 * A particular role in LISA is linked to a certain UORG (means a list of associated SDL)
	 * and to a certain visible UEROGs (means a list of SDL that could be different from UserBean.getSdloc()).
	 * This method purpose is set this criteria as filter in order to retrieve only
	 * the interventions that the role is authenticated for.
	 */
	public void setFilterByVisibility(){

		if(temporary == null || !filterBySdl){
			return;
		}

		List<Long> visibleSdl = (List<Long>)temporary.get("filterIdList");
		if(visibleSdl != null && !visibleSdl.isEmpty()){
			if(entityCriteria == null){
				entityCriteria = ca.createCriteria(entityClass);
				entityCriteria.add(Restrictions.in(serviceDeliveryLocationId, visibleSdl)) ; 
			}
			else{
				entityCriteria.add(Restrictions.in(serviceDeliveryLocationId, visibleSdl)) ; 
			}
			
		}

	}
	
	/**
	 * Get a list of InternalId from a List
	 * @param interventionList
	 * @return
	 */
	public List<Long> getInternalIdList(IdataModel<LisaSurgicalIntervention> dataPagedModel){
		
		List<Long> internalIdList = new ArrayList<Long>();
		if(dataPagedModel == null || dataPagedModel.isEmpty()){
			return internalIdList;
		}
		
		List<LisaSurgicalIntervention> interventionList = (List<LisaSurgicalIntervention>) dataPagedModel.getFullList();
		
		if(interventionList == null || interventionList.isEmpty()){
			return internalIdList;
		}
		
		for(LisaSurgicalIntervention lisa: interventionList){
			internalIdList.add(lisa.getInternalId());
		}
		
		return internalIdList;
	}

	/**
	 * Used to avoid ORACLE limit for IN Restriction
	 */
	@ShowInDesigner(description="Split IN Resctriction in OR")
	public void splitInCriteria(List<Long> listToSplit,int limit, String criteriaName) {

		if(listToSplit == null) {
			return;
		}
		
		if (entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
		}
		
	    List<List<Long>> cdList = new ArrayList<List<Long>>();
	    //if size of list is greater than limit, split it into smaller lists. See List<List<?>> cdList
	    if(listToSplit.size() > limit) {
	        List<Long> tempList = new ArrayList<Long>();
	        Integer counter = 0;
	        for(Integer i = 0; i < listToSplit.size(); i++) {
	            tempList.add(listToSplit.get(i));
	            counter++;
	            if(counter == limit) {
	                counter = 0;
	                cdList.add(tempList);
	                tempList = new ArrayList<Long>();
	            }
	        }

	        if(tempList.size() > 0) {
	            cdList.add(tempList);
	        }
	        Criterion criterion = null;
	        //Iterate the list of lists, add the restriction for smaller list
	        for(List<Long> cds : cdList) {
	            if (criterion == null) {
	                criterion = Restrictions.in(criteriaName, cds);
	            } else {
	                criterion = Restrictions.or(criterion, Restrictions.in(criteriaName, cds));
	            }
	        }
	        entityCriteria.add(criterion);
	    } else {
	    	entityCriteria.add(Restrictions.in(criteriaName, listToSplit));
	    }
	    return;
	}
}