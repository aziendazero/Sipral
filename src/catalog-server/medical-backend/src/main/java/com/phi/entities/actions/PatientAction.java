package com.phi.entities.actions;



import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.AESencrp;
import com.phi.cs.view.banner.Banner;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.VitalSign;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.role.Patient;
import com.phi.entities.role.PatientCr;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("PatientAction")
@Scope(ScopeType.CONVERSATION)
public class PatientAction extends BaseAction<Patient, Long> {

	private static final long serialVersionUID = -5092382867539591845L;
	private static final Logger log = Logger.getLogger(PatientAction.class);
	
    public static PatientAction instance() {
        return (PatientAction) Component.getInstance(PatientAction.class, ScopeType.CONVERSATION);
    }
    
	// Inject into session event name
	public static final String eventInjectIntoSession = "patient.session.inject";
	
	// Eject from session event name
	public static final String eventEjectFromSession = "patient.session.eject";
   
	
	/**
	 * Clean conversation after Patient inject/eject
	 */
	private void cleanConversation() {
		//Clean conversation
		log.info("[cid="+Conversation.instance().getId()+"] Patient injected/ejected: cleaning conversation");
		Context conv = Contexts.getConversationContext();
		for (String name : conv.getNames()) {
			Object obj = conv.get(name);
			if (obj instanceof BaseEntity 
					&& !(obj instanceof Patient)
					&& !(obj instanceof PatientEncounter)
					&& !(obj instanceof Appointment)
					&& !(obj instanceof AppointmentGrouper)
					&& !(obj instanceof ServiceDeliveryLocation)) {
				conv.remove(name);
				log.info("[cid="+Conversation.instance().getId()+"] Removed: " + obj + " with conversation name: " + name);
			}
		}
	}

	@Override
	public void inject(Object patientOrMap) {
		super.inject(patientOrMap);

		injectInSession(conversationName);
	}
	
	//WARNING: do not use this method, only if really needeed. 
	public void injectWithoutCleanConv(Object patientOrMap) {
		super.inject(patientOrMap);
		injectInSession(conversationName,false);
	}
	
	@Override
	public void injectAndProceed(Object patientOrMap, String mnemonicName) throws PhiException {
		super.injectAndProceed(patientOrMap, mnemonicName);

		injectInSession(conversationName);
	}

	@Override
	public void inject(Patient patientOrMap, String conversationName) {
		super.inject(patientOrMap, conversationName);

		injectInSession(conversationName);
	}

	
	/**
	 * Inject patient also in session
	 * Raise event eventInjectIntoSession, listen to this event into other actions to inject other objects in session
	 * @param conversationName
	 */
//	@RaiseEvent(eventInjectIntoSession)
	private void injectInSession(String conversationName) {
		injectInSession(conversationName,true);
	}
	
	private void injectInSession(String conversationName, boolean cleanConv) {
		
		if (entity == null || entity.getInternalId() == 0)
			return;
		
		Banner banner = Banner.instance();
		banner.addEntity(conversationName, entity);
		
//		Calculate age and put into banner
		
		if (entity.getBirthTime() != null) {

			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			
			Calendar birthDay = Calendar.getInstance();
			birthDay.setTime(entity.getBirthTime());
			
			int age = -1;
				
			age = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
			
			if ((birthDay.get(Calendar.MONTH) >  now.get(Calendar.MONTH)) 
			|| ((birthDay.get(Calendar.MONTH) == now.get(Calendar.MONTH)) && (birthDay.get(Calendar.DATE) > now.get(Calendar.DATE))) ) {

				age--;
			}
	
			banner.put("patientAge", age);
		}
		
		//Check encounter:
		PatientEncounter pe = (PatientEncounter)banner.getEntity("PatientEncounter");
		
		if (pe != null && pe.getPatient() != null && !entity.equals(pe.getPatient())) { // if injected encounter isn't for current patient, eject encounter
			PatientEncounterAction pea = PatientEncounterAction.instance();
			pea.eject();
		}
		
		//Check appointment:
		Appointment app = (Appointment)banner.getEntity("Appointment");
		
		if (app != null && app.getPatient() != null && !entity.equals(app.getPatient())) { // if injected appointment isn't for current patient, eject appointment
			AppointmentAction aa = AppointmentAction.instance();
			aa.eject();
		}
		
		if (cleanConv == true)
			cleanConversation();
		
		Events.instance().raiseEvent(eventInjectIntoSession, entity.getInternalId());
		log.info("[cid="+Conversation.instance().getId()+"] Object injected: " + entityClass.getSimpleName() + ", id: " + entity.getInternalId());
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
	 * Eject patient also from session
	 * Raise event eventEjectFromSession, listen to this event into other actions to eject other objects from session
	 * @param conversationName
	 */
//	@RaiseEvent(eventEjectFromSession)
	private void ejectFromSession(String conversationName) {
		
//		Clean banner
		Banner banner = Banner.instance();
		banner.removeEntity(conversationName);
		banner.put("managementClass", "patientheader-pat");
		banner.put("managementTypeAMB", false);
		banner.remove("RecoveryDays");
		banner.remove("managementType");
		banner.remove("managementTypeToolTip");
		banner.remove("patientAge");
		
		//Eject also encounter:
		PatientEncounterAction pea = PatientEncounterAction.instance();
		pea.eject();
		
		//Eject also appointment:
		AppointmentAction app = AppointmentAction.instance();
		app.eject();
		
		cleanConversation();
		
		Events.instance().raiseEvent(eventEjectFromSession);
		log.info("[cid="+Conversation.instance().getId()+"] Object ejected: Patient");
	}
	
	
//	/**
//	 * Get Patient from conversation, if null search in session
//	 * If patient is found in session refresh it and put into current conversation
//	 */
//	@Override
//	public Patient getEntity() {
//		Patient patFromConversation = super.getEntity();
//		if (patFromConversation == null) {
//			Patient patFromSession = (Patient)Contexts.getSessionContext().get(conversationName);
//			if (patFromSession != null) {
//				//inject into current conversation refreshed object from session
//				inject(ca.load(entityClass, patFromSession.getInternalId()));
//			}
//		}
//		return entity;
//	}
	
	
	public PatientEncounter getCurrentImpEnc() {
		Criteria patEncounterCriteria = ca.createCriteria(PatientEncounter.class);
		
		patEncounterCriteria.add(Restrictions.eq("this.patient", getEntity()));
		patEncounterCriteria.add(Restrictions.eq("this.isActive", true));
		patEncounterCriteria.add(Restrictions.isNotNull("this.availabilityTime"));
		patEncounterCriteria.createCriteria("code").add(Restrictions.eq("code", "IMP"));
		patEncounterCriteria.createCriteria("statusCode").add(Restrictions.eq("code", "active"));
		
		patEncounterCriteria.setMaxResults(1);
		List<PatientEncounter> listpatenc=patEncounterCriteria.list();
		PatientEncounter patEncounter;
		if (listpatenc == null || listpatenc.isEmpty()){
			patEncounter= null;}
		else{
			patEncounter=listpatenc.get(0);
		}
		
		return patEncounter;
	}
	
	public String getLastWGT() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		VitalSign vs = getLastVitalSign("weight");
		if (vs != null) {
			PQ pq = vs.getHeight();
			if (pq != null && pq.getValue() != null)
				return pq.getValue().toString();
		}
		return "";
	}

	public String getLastHGT() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		VitalSign vs = getLastVitalSign("height");
		if (vs != null) {
			PQ pq = vs.getHeight();
			if (pq != null && pq.getValue() != null)
				return pq.getValue().toString();
		}
		return "";
	}
	
	
	 
	
	private VitalSign getLastVitalSign (String attribute) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		if (attribute == null || attribute.isEmpty())
			return null;

		//VitalSignAction vsa = new VitalSignAction();
		
		VitalSign newest = null;
		Date newestDate = null;
		Patient pat = getEntity();
		if (pat == null)
			return null;
		List<VitalSign> vitalSigns =  pat.getVitalSign();
		if (vitalSigns != null && !vitalSigns.isEmpty()) {
			for (VitalSign vitalSign : vitalSigns) {
				PQ measure = (PQ)PropertyUtils.getProperty(vitalSign, attribute);
				
				if (measure != null && measure.getValue() != null) {
					Date d = vitalSign.getAvailabilityTime();
					if (d==null)
						continue;
					if (newest == null || newestDate == null  ||   d.after(newestDate)  ) {
						newest = vitalSign;
						newestDate = d;
					}
				}
			}
		}
		
		return newest;
	}
	
	//example of returned element to be placed in a table cell with line breaks
	public String getPatEnc(Patient pat) {
		if (pat == null)
			return "";

		Criteria patientCriteria = ca.createCriteria(PatientEncounter.class);
		ProjectionList slotProjection = Projections.projectionList();
		patientCriteria.setProjection(slotProjection);
		slotProjection.add(Projections.property("availabilityTime"), "availabilityTime");
	
		patientCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(PatientEncounter.class));
		patientCriteria.add(Restrictions.eq("this.patient", pat));
		List<Map<String, Object>> listpatenc=patientCriteria.list();
		if (listpatenc == null)
			 return "";
		String ret="";
		for (Map<String, Object> patenc : listpatenc) {
			ret+=patenc.get("AvailabilityTime")+" <br/>";
		}
		return ret;
	}
	
	/**
	 * Metodo che controlla se esiste già un paziente come quello 
	 * che sta per essere inserito.
	 * Se ne trova uno o pi�, mette una lista in conversation
	 */
	public boolean checkDuplicate(Patient patient) {
				
		if (patient != null){
			//recupero cognome, nome, data nascita, sesso inseriti da interfaccia
			
			try {
				String patientSurname = patient.getName().getFam();				
				
				String patientName = patient.getName().getGiv();
				
				Date patientBirthDate = patient.getBirthTime();
											
				String patientGender = patient.getGenderCode().getCode();
				
				//cerco se esiste già uno o più pazienti simili
				HashMap<String,Object> pars = new HashMap<String,Object>();
				 
				pars.put("patientSurname",patientSurname);
				pars.put("patientName",patientName);
				pars.put("patientBirthDate", patientBirthDate);
				pars.put("patientGender", patientGender);

				List<Object> list = (List<Object>)ca.executeHQLwithParameters(
						"SELECT PatientImpl1" +
						"		FROM Patient PatientImpl1" +
						"        WHERE SOUNDEX(PatientImpl1.name.fam) = SOUNDEX(:patientSurname)" +
						"        AND SOUNDEX(PatientImpl1.name.giv) = SOUNDEX(:patientName)" +
						"        AND PatientImpl1.birthTime = :patientBirthDate " +
						"        AND PatientImpl1.genderCode.code = :patientGender " 
				 ,pars);
				
				if( list.size()>0){
					PatientAction pa = (PatientAction) Component.getInstance("PatientAction");
					pa.injectList(list, "OldPatientList");
					return true;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}			
			
		}
		
		return false;
	}
	
	public void mergePatients(Long masterId, Long slaveId, String userName) throws Exception {
//		merge("begin pkg_phi_utilities.patient_merge(:master_id, :slave_id, :user_name, :buffer_out); end;", masterId, slaveId, userName);
	}
	
	@Override
	public void create() throws PhiException {
		if(RepositoryManager.instance().isUsing_EncryptPatientData()) {
			encryptData();
		}else{
			super.create();
		}
	}
	
	private void encryptData() throws PhiException{
		AESencrp encryptor = AESencrp.instance();
		PatientCr patCr = null;
		//pure create
		if(entity.getInternalId()==0){
			patCr = new PatientCr();
		}else if(entity.getInternalId()>0){
			patCr = ca.get(PatientCr.class, entity.getInternalId());
		}
		
		if(patCr!=null){
			patCr.setAdditionalId(entity.getAdditionalId());
			patCr.setBirthTime(entity.getBirthTime());
			patCr.setCitizen(entity.getCitizen());
			patCr.setCountryOfBirth(entity.getCountryOfBirth());
			patCr.setCurrentOrg(entity.getCurrentOrg());
			patCr.setCurrentOrgCode(entity.getCurrentOrgCode());
			patCr.setDate1(entity.getDate1());
			patCr.setDate2(entity.getDate2());
			patCr.setDate3(entity.getDate3());
			patCr.setDeceasedTime(entity.getDeceasedTime());
			patCr.setDistrict(entity.getDistrict());
			patCr.setDoc(entity.getDoc());
			patCr.setDoctor(entity.getDoctor());
			patCr.setEducationLevelCode(entity.getEducationLevelCode());
			patCr.setExternalConsent(entity.getExternalConsent());
			patCr.setExternalId(entity.getExternalId());
			patCr.setForeignBirthPlace(entity.getForeignBirthPlace());
			patCr.setG2Anag(entity.getG2Anag());
			patCr.setGenderCode(entity.getGenderCode());
			patCr.setGenericExemption(entity.getGenericExemption());
			patCr.setHealthCardId(entity.getHealthCardId());
			patCr.setImported(entity.getImported());
			patCr.setInternalConsent(entity.getInternalConsent());
			patCr.setJobTitle(entity.getJobTitle());
			patCr.setLanguage(entity.getLanguage());
			patCr.setLivingArrangementCode(entity.getLivingArrangementCode());
			patCr.setMaritalStatusCode(entity.getMaritalStatusCode());
			patCr.setNoAllergy(entity.getNoAllergy());
			patCr.setNote1(entity.getNote1());
			patCr.setNote2(entity.getNote2());
			patCr.setNote3(entity.getNote3());
			patCr.setOriginalOrg(entity.getOriginalOrg());
			patCr.setReligiousAffiliationCode(entity.getReligiousAffiliationCode());
			patCr.setStatusCode(entity.getStatusCode());
			patCr.setStp(entity.getStp());
			patCr.setTeamId(entity.getTeamId());
			
			if(entity.getAddr()!=null)
				patCr.setAddr(entity.getAddr().cloneAd());
			if(entity.getBirthPlace()!=null)
				patCr.setBirthPlace(entity.getBirthPlace().cloneAd());
			if(entity.getDomicileAddr()!=null)
				patCr.setDomicileAddr(entity.getDomicileAddr().cloneAd());
			if(entity.getTelecom()!=null)
				patCr.setTelecom(entity.getTelecom().cloneTel());
			
			//finally we encrypt patient data
			if(entity.getName()!=null){
				String fam = entity.getName().getFam();
				String giv = entity.getName().getGiv();
				String cf = entity.getFiscalCode();
				patCr.setName(entity.getName().cloneEN());
				
				try {
					patCr.getName().setFam(encryptor.encryptHex(fam));
					patCr.getName().setGiv(encryptor.encryptHex(giv));
					patCr.setFiscalCodeCr(encryptor.encryptHex(cf));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			PatientCrAction pcr = PatientCrAction.instance();
			pcr.inject(patCr);
			pcr.create();
		}
	}
}