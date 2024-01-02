package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.banner.Banner;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Diagnosis;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.Procedure;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.IIEmbeddable;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("PatientEncounterAction")
@Scope(ScopeType.CONVERSATION)
public class PatientEncounterAction extends FilterForPrivacyAction<PatientEncounter, Long> {

	private static final long serialVersionUID = -9184321127445629428L;
	
	private static final Logger log = Logger.getLogger(PatientEncounterAction.class);
	
    public static PatientEncounterAction instance() {
        return (PatientEncounterAction) Component.getInstance(PatientEncounterAction.class, ScopeType.CONVERSATION);
    }
	
	// Inject into session event name
	public static final String eventInjectIntoSession = "patientEncounter.session.inject";
	
	// Eject from session event name
	public static final String eventEjectFromSession = "patientEncounter.session.eject";
	

	private static final String fallRiskHql =
			" SELECT" +
			" COUNT(nanda)" +
			" FROM Nanda nanda" +
			" LEFT JOIN nanda.nandaDiag nandaCode" +
			" WHERE nandaCode.code = '00155'" +
			" AND nanda.cancellationDate IS NULL" +
			" AND nanda.patientEncounter.internalId = :encounterId";
   

	@Override
	public void inject(Object patientOrMap) {
		super.inject(patientOrMap);

		injectInSession(conversationName);
	}
	
	@Override
	public void injectAndProceed(Object patEncOrMap, String mnemonicName) throws PhiException {
		super.injectAndProceed(patEncOrMap, mnemonicName);

		injectInSession(conversationName);
	}

	@Override
	public void inject(PatientEncounter patEncOrMap, String conversationName) {
		super.inject(patEncOrMap, conversationName);

		injectInSession(conversationName);
	}
	
	/**
	 * Inject encounter also in session
	 * Raise event eventInjectIntoSession, listen to this event into other actions to inject other objects in session
	 * @param conversationName
	 */	
	private void injectInSession(String conversationName) {
		
		if (entity == null || entity.getInternalId() == 0)
			return;
		
		Banner banner = Banner.instance();
		banner.addEntity(conversationName, entity);
		long days = -1;
		
		if (entity.getAvailabilityTime() != null) {
			Calendar cal = Calendar.getInstance();
			Calendar admissionDate = Calendar.getInstance();
			admissionDate.setTime(entity.getAvailabilityTime());
				
			long now = cal.getTimeInMillis();
			long availabilityDate = entity.getAvailabilityTime().getTime();
			if (now > availabilityDate)
				days = (now - availabilityDate) / 86400000;
			
			banner.put("RecoveryDays", days);
		
		} else
			banner.put("RecoveryDays", "N.A.");
			
		//Set banner image and tooltip for management type (Ambulatory or Ward)
		if (entity.getCode()!=null && entity.getCode().getCode()!=null){
			String code = entity.getCode().getCode();
				
			if ((code!=null)&&(!code.equals(""))){
				FunctionsBean fb = FunctionsBean.instance();

				if(code.equals("AMB")) {
					banner.put("managementClass", "patientheader-amb");
					banner.put("managementTypeAMB", true);
					banner.put("managementType", "images/amb.png");
					banner.put("managementTypeToolTip", fb.getStaticTranslation("Label_managementTypeAMB"));
				} else {
					banner.put("managementClass", "patientheader-ward");
					banner.put("managementTypeAMB", false);
					banner.put("managementType", "images/ward.png");
					banner.put("managementTypeToolTip", fb.getStaticTranslation("Label_managementTypeWARD"));
				}
			}
		}
			
		//When a PatientEncounter is injected, the corresponding patient is injected to maintain 
		//consistency in conversation context. When a patient is injected, the conversation context is cleaned up 
		//from any other entity except for PatientEncounter, Appointment, AppointmentGrouper and ServiceDeliveryLocation 
		PatientAction pa = PatientAction.instance();
		if (entity.getPatient() != null)
			pa.inject(entity.getPatient());
		else {
			//If Encounter's patient is null and conversation contains a patient do not eject it, 
			//to permit encounter creation and linking. Write a warning.
			if (pa.getEntity() != null)
				log.warn("Conversation already contains a Patient (id: " + pa.getEntity().getInternalId() + ") and injected PatientEncounter has null patient.");
		}					
		
		Events.instance().raiseEvent(eventInjectIntoSession, entity.getInternalId());
		
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")))
			TransferPrivacy.instance().buildEnabledDateIntervals(entity.getInternalId());				
		
		log.info("[cid="+Conversation.instance().getId()+"] Object injected: PatientEncounter, id: " + entity.getInternalId());
	}

	@Override
	public void eject() {
		super.eject();
		
		ejectFromSession(conversationName);
	}
	
	@Override
    public void delete() throws PhiException {
    	super.delete();
    	
    	ejectFromSession(conversationName);
    }
	
	/**
	 * Eject encounter from session
	 * Raise event eventEjectFromSession, listen to this event into other actions to eject other objects from session
	 * @param conversationName
	 */
	private void ejectFromSession(String conversationName) {
		
		Banner banner = Banner.instance();
		banner.removeEntity(conversationName);
		
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy"))) {
			TransferPrivacy.instance().cleanEnabledDateIntervals();				
		}
		
		Events.instance().raiseEvent(eventEjectFromSession);
		
		log.info("[cid="+Conversation.instance().getId()+"] Object ejected: PatientEncounter");
	}
	
	
//	/**
//	 * Get Patient from conversation, if null search in session
//	 * If patient is found in session refresh it and put into current conversation
//	 */
//	@Override
//	public PatientEncounter getEntity() {
//		PatientEncounter patFromConversation = super.getEntity();
//		if (patFromConversation == null) {
//			PatientEncounter patFromSession = (PatientEncounter)Contexts.getSessionContext().get(conversationName);
//			if (patFromSession != null) {
//				//inject into current conversation refreshed object from session
//				inject(ca.load(entityClass, patFromSession.getInternalId()));
//			}
//		}
//		return entity;
//	}
	
    /**
	 * Create a new patientEncounter for a certain patient linked to the currentEmployee and availability time as current Date
	 * @param appointment
	 * @return
	 * @throws PersistenceException
	 * @throws DictionaryException
	 */
	public PatientEncounter createNewPatientEncounter(Appointment appointment) throws PersistenceException, DictionaryException{
		return createNewPatientEncounter(appointment, null);
	}
	
	public PatientEncounter createNewPatientEncounter(Appointment appointment, CodeValue codeEncounter) throws PersistenceException, DictionaryException{
		return createNewPatientEncounter(appointment, codeEncounter, null);
	}
	
	public PatientEncounter createNewPatientEncounter(Appointment appointment, CodeValue codeEncounter, CodeValue statusCodeEncounter) throws PersistenceException, DictionaryException{

		Vocabularies vocabularies = VocabulariesImpl.instance();
		// Get information and create PatientEncounter
		UserBean userBean = UserBean.instance();
		Employee admitter = userBean.getCurrentEmployee();
		ServiceDeliveryLocation admissionSdl = null;
		
		if (codeEncounter==null)
			codeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC","EncounterKind","AMB");
		
		if (statusCodeEncounter == null)
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC","EncounterStatus","new");
	
		PatientEncounter patientEncounter = new PatientEncounter();
		
		if(appointment != null){
			admissionSdl = appointment.getServiceDeliveryLocation();
			patientEncounter.setServiceDeliveryLocation(admissionSdl);
			ServiceDeliveryLocation parent = admissionSdl.getParent();
			if (parent instanceof HibernateProxy) {
				parent = (ServiceDeliveryLocation)((HibernateProxy)parent).getHibernateLazyInitializer().getImplementation();
			}
			patientEncounter.setAssignedSDL(parent);
		}
		
		patientEncounter.setCode(codeEncounter);
		patientEncounter.setStatusCode(statusCodeEncounter);
		patientEncounter.setAvailabilityTime(new Date());
		patientEncounter.setAdmitter(admitter);

		Patient patient = appointment.getPatient();
		if (patient!= null) {
			if (patient instanceof HibernateProxy) {
				patient = (Patient)((HibernateProxy)appointment.getPatient()).getHibernateLazyInitializer().getImplementation();
			}
			patientEncounter.setPatient(patient);
		}
		
		Context app = Contexts.getApplicationContext();
		String currentCustomer = (String)app.get("CUSTOMER");
		if ("DSC".equals(currentCustomer) || "OMEGA".equals(currentCustomer) || "MARIEN".equals(currentCustomer) || "KRONPLATZ".equals(currentCustomer)) {
			patientEncounter.setAssignedSDL(admissionSdl);
			//Specialità
			if (admissionSdl.getParent().getCode().getCode().equals("WARD")){
				CodeValuePhi specialitaEncounter = (CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC","Specialita",admissionSdl.getParent().getSpecialization().getCode());
				patientEncounter.setSpecialityCode(specialitaEncounter);
			}
			List<Procedure> appointmentProcList = (List<Procedure>)appointment.getPerformedProcedure();
			if (appointmentProcList != null &&  !appointmentProcList.isEmpty()) {
				List<Procedure> encProcList = patientEncounter.getDischargeProcedure();
				if (encProcList == null || encProcList.isEmpty()) 
					encProcList = new ArrayList<Procedure>();
				
				encProcList.addAll(appointmentProcList);
				
				for (Procedure p : appointmentProcList) {
					p.setPatientEncounter(patientEncounter);
				}
				
			}
		}
		return patientEncounter;
	}
	
	
	/**
	 * Inject a auditedEntity List (Diagnosis or Procedure) of elements filtered by isActive = true
	 * 
	 * @param list of auditedEntity objects to be filtered
	 * @param name used to put the IdataModel in conversation
	 * @param conversationName used by the ArrayList into the PhiDataModel 
	 * @return the filtered list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List injectActiveList(List<AuditedEntity> list, String name, String conversationName) {

		Context conversation = Contexts.getConversationContext();

		if (list == null || list.isEmpty()) {
			IdataModel<AuditedEntity> dm = new PhiDataModel(new ArrayList<AuditedEntity>(), conversationName);
			Contexts.getConversationContext().set(name, dm);
			return ((IdataModel)conversation.get(name)).getList();
		}

		List activeElement = new ArrayList<AuditedEntity>();
		for(AuditedEntity audit:list){
			if(audit.getIsActive())
				activeElement.add(audit);
		}
		
		if(list.get(0) instanceof Diagnosis){
			activeElement = DiagnosisAction.instance().orderListByScore(activeElement);
		}

		IdataModel<AuditedEntity> dm = new PhiDataModel(activeElement, conversationName);

		conversation.set(name, dm);

		return list;
	}
	
	
    //Methods for updating summary fields of PatientEncounter without increasing version to avoid StealObjectException
    
    private final static String updateLastCheckingHql = "update PatientEncounter set lastChecking = :lastChecking where internalId = :internalId";
    private final static String updatePainHql = "update PatientEncounter set pain = :pain where internalId = :internalId";
    private final static String updateBradenScoreHql = "update PatientEncounter set bradenScore = :bradenScore where internalId = :internalId";
    private final static String updateLastClinicalDiaryHql ="update PatientEncounter set lastClinicalDiary = :lastClinicalDiary where internalId = :internalId";
    private final static String updateNortonScoreHql = "update PatientEncounter set nortonScore = :nortonScore where internalId = :internalId";
    private final static String updateBrassScoreHql = "update PatientEncounter set brassScore = :brassScore where internalId = :internalId";
    private final static String updateNurseReportCodeHql = "update PatientEncounter set nurseReportCode = :nurseReportCode where internalId = :internalId";
    
    public void setLastChecking(Date lastChecking) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("lastChecking", lastChecking);
            
            ca.executeUpdateHql(updateLastCheckingHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field lastChecking", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
    
    public void setPain(Double pain) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("pain", pain);
            
            ca.executeUpdateHql(updatePainHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field pain", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
    
    /**
     * Use this to set null pain from EL
     * null from EL is converted to 0
     * @throws PhiException
     */
    public void cleanPain() throws PhiException {
    	setPain(null);
    }
    
    public void setBradenScore(Integer bradenScore) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("bradenScore", bradenScore);
            
            ca.executeUpdateHql(updateBradenScoreHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field bradenScore", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
    
    /**
     * Use this to set null bradenScore from EL
     * null from EL is converted to 0
     * @throws PhiException
     */
    public void cleanBradenScore() throws PhiException {
    	setBradenScore(null);
    }
    
    public void setLastClinicalDiary(Date lastClinicalDiary) throws PhiException {
    	setLastClinicalDiary(lastClinicalDiary, getEntity().getInternalId());        
    }
    
    public void setLastClinicalDiary(Date lastClinicalDiary, Long encounterId) throws PhiException {
    	try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", encounterId);
            params.put("lastClinicalDiary", lastClinicalDiary);
            
            ca.executeUpdateHql(updateLastClinicalDiaryHql, params);            
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field lastClinicalDiary", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
    }
    
    public void setNortonScore(Integer nortonScore) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("nortonScore", nortonScore);
            
            ca.executeUpdateHql(updateNortonScoreHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field nortonScore", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
    
    /**
     * Use this to set null nortonScore from EL
     * null from EL is converted to 0
     * @throws PhiException
     */
    public void cleanNortonScore() throws PhiException {
    	setNortonScore(null);
    }
    
    public void setBrassScore(Integer brassScore) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("brassScore", brassScore);
            
            ca.executeUpdateHql(updateBrassScoreHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field brassScore", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
    
    /**
     * Use this to set null brassScore from EL
     * null from EL is converted to 0
     * @throws PhiException
     */
    public void cleanBrassScore() throws PhiException {
    	setBrassScore(null);
    }
    
    public void updateFallRisk() throws PersistenceException {
		
		// Read active diagnosis of fall risk
		String hql = fallRiskHql;
		Query qry = ca.createHibernateQuery(hql);
		qry.setParameter("encounterId", entity.getInternalId());
		long count = (Long) qry.uniqueResult();
		boolean fallRisk = count != 0;
		
		// Set fall risk
		entity.setFallRisk(fallRisk);
		
		ca.update(entity);		
		ca.flushSession();
	}

	public void initVisitNumber() {
		PatientEncounter pe = getEntity();
		if (pe != null && pe.getVisitNumber() == null) {
			pe.setVisitNumber(new IIEmbeddable());
		}
	}
	    
    public void setNurseReportCode(CodeValuePhi value) throws PhiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("internalId", getEntity().getInternalId());
            params.put("nurseReportCode", value);
            
            ca.executeUpdateHql(updateNurseReportCodeHql, params);
            
        } catch (PersistenceException e) {
            throw new PhiException("Error updating summary field hasNurseReports", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
        }
        
    }
	
	public void mergeEncounters(Long masterId, Long slaveId, String userName) throws Exception {
//		merge("begin pkg_phi_utilities.patient_encounter_merge(:master_id, :slave_id, :user_name, :buffer_out); end;", masterId, slaveId, userName);
	}

}