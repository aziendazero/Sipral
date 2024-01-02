package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Dosage;
import com.phi.entities.act.LEPActivity;
import com.phi.entities.act.LEPExecution;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.IVL;
import com.phi.events.PhiEvent;



@BypassInterceptors
@Name("LEPActivityAction")
@Scope(ScopeType.CONVERSATION)
public class LEPActivityAction extends BaseAction<LEPActivity, Long> {

	public static LEPActivityAction instance() {
		return (LEPActivityAction) Component.getInstance(LEPActivityAction.class, ScopeType.CONVERSATION);
	}

	private static final long serialVersionUID = 1145778343L;
	private static final Logger log = Logger.getLogger(LEPActivityAction.class);


	public void InitDate() throws PersistenceException  {

		LEPActivity currentLep = getEntity();
		IVL<Date> ValidDate = new IVL<Date>();
		ValidDate.setLow(new Date());

		currentLep.setEffectiveDate(ValidDate);

	}
	
	public void InitValidDate() throws PersistenceException  {

		LEPActivity currentLep = getEntity();
		IVL<Date> ValidDate = new IVL<Date>();
		ValidDate.setLow(new Date());

		currentLep.setValidityPeriod(ValidDate);

	}
	
	public void FinValidDate() throws PersistenceException  {

		LEPActivity currentLep = getEntity();
		IVL<Date> ValidDate = new IVL<Date>();
		ValidDate.setHigh(new Date());

		currentLep.setValidityPeriod(ValidDate);

	}

	public List<CodeValue> getLepRel(CodeValue nandaCode,CodeValue nandaConsequence)  {
		List<CodeValue> lepRel = new ArrayList<CodeValue>();
		List<CodeValue> lepRelnanda = new ArrayList<CodeValue>();
		List<CodeValue> lepRelcons = new ArrayList<CodeValue>();
		if (nandaConsequence ==null  && nandaCode==null )
			return lepRel;
		if ( nandaCode==null )
			return lepRel;


		HashMap<String, Object> parameters= new HashMap<String, Object>();
		if (nandaConsequence ==null){
			try {
				parameters.put("id", nandaCode.getId());
				parameters.put("code", "K630%");

				lepRelnanda = (List<CodeValue>)ca.executeHQLwithParameters(NandaAction.GET_CODE_EQUIVALENT_FOR_NANDA, parameters);
				
			} catch (PersistenceException e) {
				log.error("Error getting ServiceDeliveryLocations!", e);
			}


		}
		else {
			try {
				parameters.put("id", nandaCode.getId());
				parameters.put("code", "K630%");

				lepRelnanda = (List<CodeValue>)ca.executeHQLwithParameters(NandaAction.GET_CODE_EQUIVALENT_FOR_NANDA, parameters);

				parameters.clear();
				parameters.put("id", nandaConsequence.getId());
				parameters.put("code", "K630%");
				lepRelcons = (List<CodeValue>)ca.executeHQLwithParameters(NandaAction.GET_CODE_EQUIVALENT_FOR_NANDA, parameters);
			} catch (PersistenceException e) {
				log.error("Error getting ServiceDeliveryLocations!", e);
			}
			lepRelnanda.addAll(lepRelcons);
		}




		HashSet<CodeValue> hs = new HashSet<CodeValue>(lepRelnanda.size());
		hs.addAll(lepRelnanda);
		lepRel.clear();
		lepRel.addAll(hs);



		Collections.sort(lepRel,
				new Comparator<CodeValue>()
				{
			public int compare(CodeValue f1, CodeValue f2)
			{
				return f1.toString().compareTo(f2.toString());
			}        
				});

		return lepRelnanda;
	}
		
	
	/*
	 * 
	 * VALIDATION functions
	 * 
	 */	
	
	public void validateActivity(LEPActivity activity) throws PhiException {
		validateActivity(activity, true);
	}
	
	public void validateActivity(LEPActivity activity, boolean isDashboard) throws PhiException {
	
		try {	
			
			// Add validation date
			if (activity.getValidityPeriod() == null || activity.getValidityPeriod().getLow() == null) {
				activity.setValidityPeriod(new IVL<Date>());
				activity.getValidityPeriod().setLow(new Date());
			}
			// Change status to active
			if (activity.getStatusCode() == null)
			{
				activity.setStatusCode((CodeValuePhi)VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC","StatusCodes","active"));
			}
			
			if (isDashboard) {
				ca.update(activity);
				
				Events.instance().raiseEvent(PhiEvent.CREATE, activity);
				
				ca.flushSession();
			}
			
			generateExecutions(activity, false, isDashboard);			
			
		} catch (Exception e) {
			log.error("Error validation activity with internalId " + activity.getInternalId(), e);
			throw(new PhiException("Error validation for prescription with internalId " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}	

	public void revalidateActivity(LEPActivity activity) throws PhiException {
		revalidateActivity(activity, true);
	}
	
	public void revalidateActivity(LEPActivity activity, boolean flushSession) throws PhiException {
		try{
			
			try {
				generateExecutions(activity, true, flushSession);				
			} catch (Exception e) {
				log.error("Error validation activity with internalId " + activity.getInternalId(), e);
				throw(new PhiException("Error revalidation for prescription with internalId " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
			}
			
		} catch (Exception e) {
			log.error("Error updating status of executions of activity with id: " + activity.getInternalId(), e);
			throw(new PhiException("Error updating status of executions of activity with id: " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}

	public void invalidateActivity(LEPActivity activity) throws PhiException {
		invalidateActivity(activity, true);
	}
	
	public void invalidateActivity(LEPActivity activity, boolean isDashboard) throws PhiException {
		try {
			
			List<LEPExecution> executions = activity.getLepExecution();
			
			// Delete all future executions
			if (executions != null && executions.size() > 0)
			{
				// Calculate activityRealEndDate: is the invalidationDate if present or endDate if present
				Calendar activityRealEndDate = Calendar.getInstance();
				if (activity.getValidityPeriod() != null && activity.getValidityPeriod().getHigh() != null)
					activityRealEndDate.setTime(activity.getValidityPeriod().getHigh());
				else if (activity.getEffectiveDate().getHigh() != null)
					activityRealEndDate.setTime(activity.getEffectiveDate().getHigh());				
				
				Iterator<LEPExecution> iterator = executions.iterator();				
				while (iterator.hasNext()) {
					LEPExecution execution = iterator.next();
					if (execution.getStatusCode().getCode().equals("PLANNED")) {
						if (execution.getPlannedDate().after(activityRealEndDate.getTime())) {						
							iterator.remove();
							ca.delete(execution);
							
							Events.instance().raiseEvent(PhiEvent.DELETE, execution);
						}
					}
				}
			}
			
			if (isDashboard) {
				ca.update(activity);
				
				Events.instance().raiseEvent(PhiEvent.CREATE, activity);
				
				ca.flushSession();
			}
						
		} catch (Exception e) {
			log.error("Error invalidation activity with internalId " + activity.getInternalId(), e);
			throw(new PhiException("Error validation for prescription with internalId " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}
	
	/**
	 * Generates executions of injected activity
	 * @return number of generated executions
	 * @throws PhiException
	 */
	public Integer generateExecutions(LEPActivity activity, boolean reopening) throws PhiException {
			return generateExecutions(activity, reopening, true);
	}
	
	public Integer generateExecutions(LEPActivity activity, boolean reopening, boolean isDashboard) throws PhiException {
				
		if (activity == null) {
			return 0;
		}
		
		if (!activity.getStatusCode().getCode().equals("active")) {
			return 0;
		}
		
		try {				
						
			//Calculate activityRealStartDate: is the bigger of startDate and validationDate
			Date startDate = activity.getEffectiveDate().getLow();  		// Can't be null
			Date validationDate = activity.getValidityPeriod().getLow(); 	// Can't be null
			Calendar activityRealStartDate = Calendar.getInstance();
			
			if (startDate.after(validationDate)) {
				activityRealStartDate.setTime(startDate);
			} else {
				activityRealStartDate.setTime(validationDate);
			}
			
			// Calculate activityRealEndDate: is the invalidationDate if present or endDate if present or null if both not present
			Calendar activityRealEndDate = Calendar.getInstance();
			if (activity.getValidityPeriod() != null && activity.getValidityPeriod().getHigh() != null)
				activityRealEndDate.setTime(activity.getValidityPeriod().getHigh());
			else if (activity.getEffectiveDate().getHigh() != null)
				activityRealEndDate.setTime(activity.getEffectiveDate().getHigh());
			else
				activityRealEndDate = null;
						
			// Now find out start date to calculate executions:
			// if activity doesn't have executions, generate all from activityRealStartDate (after there is a check end > start)
			// else generate only for the 2nd day in the future (start = dd+2/mm/yyyy 00:00)
			// Ex: today is 01/01/2014 --> start = 03/01/2014 00:00
			Calendar start = Calendar.getInstance();
			start.setTime(new Date());
			start.set(Calendar.DATE, start.get(Calendar.DATE) + 2);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			
			if (activity.getLepExecution() == null || activity.getLepExecution().size() == 0) {
				start.setTime(activityRealStartDate.getTime());				
			}
			
			if (reopening) {
				start.setTime(new Date());
			}
			
			//Now find out end date to calculate administrations:
			//generate all to min(activityRealEndDate, end = dd+2/mm/yyyy 23:59) (after there is a check end > start)
			//Ex: today is 01/01/2014 --> end = 03/01/2014 23:59			
			
			Calendar end = Calendar.getInstance();
			end.setTime(new Date());
			/* FIXME: remove after checking
			if (activity.getLepExecution() == null || activity.getLepExecution().size() == 0) {
				end.set(Calendar.DATE, end.get(Calendar.DATE) + 2);
			} else {
				end.set(Calendar.DATE, end.get(Calendar.DATE) + 3);
			}
			*/
			end.set(Calendar.DATE, end.get(Calendar.DATE) + 2);
			end.set(Calendar.HOUR_OF_DAY, 23);
			end.set(Calendar.MINUTE, 59);
			end.set(Calendar.SECOND, 0);
			end.set(Calendar.MILLISECOND, 0);			
						
			// If end date is before end limit, get end date
			if (activityRealEndDate != null && activityRealEndDate.before(end)) { 
				end.setTime(activityRealEndDate.getTime());
			}
			
			return generateExecutionsFromTo(activity, activityRealStartDate, start, end, isDashboard);		
			
		} catch (Exception e) {
			log.error("Error generating executions for activity with internalId " + activity.getInternalId(), e);
			throw(new PhiException("Error generating executions for activity with internalId " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}

	
	public Integer generateExecutionsFromTo(LEPActivity activity, Calendar realStartDate, Calendar start, Calendar end) throws PhiException {
		return generateExecutionsFromTo(activity, realStartDate, start, end, true);
	}
	
	public Integer generateExecutionsFromTo(LEPActivity activity, Calendar realStartDate, Calendar start, Calendar end, boolean isDashboard) throws PhiException {
		try {
			
			Integer generatedExecution = 0;
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			CodeValue plannedCode = vocabularies.getCodeValueCsDomainCode("PHIDIC","AdministrationStatus","PLANNED");
			
			// Set seconds and milliseconds to 0
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			end.set(Calendar.SECOND, 0);
			end.set(Calendar.MILLISECOND, 0);
						
			// PLANNED
			if (end.getTimeInMillis() > start.getTimeInMillis()) { 	//start before end
				Calendar executionDateCandidate = Calendar.getInstance();
				
				Integer dayInterval;					
				Calendar dosageTime = Calendar.getInstance();
				
				for (Dosage dosage : activity.getDosage()) {
					// For each dosage get dayInterval, dosageTime
					dayInterval = dosage.getDayInterval();				
					
					dosageTime.setTime(dosage.getDaytime());
					
					executionDateCandidate.set(	realStartDate.get(Calendar.YEAR), 
												realStartDate.get(Calendar.MONTH), 
												realStartDate.get(Calendar.DATE), 
												dosageTime.get(Calendar.HOUR_OF_DAY), 
												dosageTime.get(Calendar.MINUTE),
												0);					
					executionDateCandidate.set(Calendar.MILLISECOND, 0);	
					
					while (executionDateCandidate.before(end) || executionDateCandidate.equals(end)) {				
						if (executionDateCandidate.after(start) || executionDateCandidate.equals(start)) {
							
							boolean toCreate = true;
							
							if (executionDateCandidate != null && activity.getLepExecution() != null) {								
								for (LEPExecution execution : activity.getLepExecution()) {
									if (execution.getPlannedDate().equals(executionDateCandidate.getTime())) {
										toCreate = false;
										break;
									}
								}
							}
							
							if (toCreate) {
								//Generate execution						
								createExecution(plannedCode, executionDateCandidate.getTime(), activity);
								generatedExecution++;
		
								if (isDashboard && generatedExecution % 10 == 0) {
									//FIXME: don't use ca
									ca.flushSession();
								}
							}
						}
						executionDateCandidate.set(Calendar.DATE, executionDateCandidate.get(Calendar.DATE) + dayInterval); 
					}
				}
			}
			if (isDashboard){
				ca.flushSession();
			}
			return generatedExecution;
		} catch (Exception e) {
			log.error("Error generating executions for activity with internalId " + activity.getInternalId(), e);
			throw(new PhiException("Error generating executions for activity with internalId " + activity.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}		
	
	private void createExecution(CodeValue statusCode, Date plannedDate, LEPActivity activity) throws PersistenceException {
		LEPExecution execution = new LEPExecution();
		execution.setStatusCode(statusCode);
		if (plannedDate != null) {
			execution.setPlannedDate(plannedDate);
		}
		execution.setPlannedTime(activity.getTimeSpent());
		execution.setLepActivity(activity);
		ca.create(execution);
		
		Events.instance().raiseEvent(PhiEvent.CREATE, execution);
	}
	
	/**
	 * Sets given IVL property low and high values
	 * 
	 * @param attributeName - IVL attribute name
	 * @param low - IVL lower element
	 * @param high - IVL higher element
	 * @throws PhiException
	 */
	@ShowInDesigner(description="set IVL property")
	public void setIvl(String attributeName, Date low, Date high) throws PhiException {

		if (attributeName == null || attributeName.isEmpty() || low == null && high == null) {
			return;
		}

		IVL<Date> interval = new IVL<Date>();
		interval.setLow(low);
		interval.setHigh(high);
		
		try {
			if (getEntity() != null) {
				PropertyUtils.setProperty(entity, attributeName, interval);
			} else {
				log.error("Error setting IVL: no entity in conversation");
			}
		} catch (Exception e) {
			throw new PhiException("Error setting IVL to " + getConversationName() + "." + attributeName,
					e, ErrorConstants.PERSISTENCE_RIM_PROPERTY_TYPE_NOT_FOUND_ERR_CODE);
		}
	}


}