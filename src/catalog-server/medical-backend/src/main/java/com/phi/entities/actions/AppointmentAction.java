package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.LockManager;
import com.phi.cs.lock.Locker;
import com.phi.cs.paging.LazyList;
import com.phi.cs.view.banner.Banner;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.Procedure;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.entities.baseEntity.ProcedureRequest;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.Patient;

@BypassInterceptors
@Name("AppointmentAction")
@Scope(ScopeType.CONVERSATION)
public class AppointmentAction extends BaseAction<Appointment, Long> {

	private static final long serialVersionUID = 586502657L;
	private static final Logger log = Logger.getLogger(Appointment.class);
	public static final String ARRIVED="arrived";
	public static final String CANCEL="cancel";
	public static final String CANCEL_AND_REACTIVATE="cancelReactivate";
	public static final String COMPLETE="complete";
	public static final String COMPLETED_PROCEDURE="completed";
	public static final String CONFIRM="confirm";
	public static final String CONFIRM_AND_COMPLETE="confirmComplete";
	public static final String NEW="new";
	public static final String NULLIFIED_PROCEDURE="nullified";
	public static final String NULLIFY="nullify";
	public static final String REPORT="report";

	public static AppointmentAction instance() {
		return (AppointmentAction) Component.getInstance(AppointmentAction.class, ScopeType.CONVERSATION);
	}
	
	// Inject into session event name
	public static final String eventInjectIntoSession = "appointment.session.inject";
	
	// Eject from session event name
	public static final String eventEjectFromSession = "appointment.session.eject";
	
	@Override
	public void inject(Object patientOrMap) {
		super.inject(patientOrMap);

		injectInSession(conversationName);
	}
	
	@Override
	public void injectAndProceed(Object appOrMap, String mnemonicName) throws PhiException {
		super.injectAndProceed(appOrMap, mnemonicName);
		injectInSession(conversationName);
	}

	@Override
	public void inject(Appointment appOrMap, String conversationName) {
		super.inject(appOrMap, conversationName);
		injectInSession(conversationName);
	}
	
	// AppointmentAction.inject() ejects PatientEncounter, we need to override this when creating InternalActivity, because we don't want the PatientEncounter to be ejected, and
	// there is no risk of having two instances of PatientEncounter in conversation.
	public void injectInConversation(Appointment appOrMap) {
		super.inject(appOrMap);
	}
	
	/**
	 * Inject appointment also in session
	 * Raise event eventInjectIntoSession, listen to this event into other actions to inject other objects in session
	 * @param conversationName
	 */
	private void injectInSession(String conversationName) {
		
		if (entity == null || entity.getInternalId() == 0)
			return;

		Banner banner = Banner.instance();
		banner.addEntity(conversationName, entity);
		
		banner.put("managementTypeAMB", true);
			
		PatientAction pa 				= PatientAction.instance();
		PatientEncounterAction pea 		= PatientEncounterAction.instance();
			
		Patient patientInConv 			= (Patient)Contexts.getConversationContext().get("Patient");
		PatientEncounter encInConv		= (PatientEncounter)Contexts.getConversationContext().get("PatientEncounter");
			
		if (entity.getPatient()!=null && !entity.getPatient().equals(patientInConv)) 
			//if patient is null don't eject the patient
			pa.inject(entity.getPatient());
			
		if (entity.getPatientEncounter()==null){
			if (encInConv!=null) 
				//Eject Encounter - Could come from ward
				pea.eject();

			//Set banner image and tooltip for managementType when we inject an appointment, 
			//but the relative encounter doesn't exist yet
			FunctionsBean fb = FunctionsBean.instance();
			banner.put("managementClass", "patientheader-amb");
			banner.put("managementType", "images/amb.png");
			banner.put("managementTypeToolTip", fb.getStaticTranslation("Label_managementTypeAMB"));
			
		} else if (!entity.getPatientEncounter().equals(encInConv))
			//Inject the right encounter
			pea.inject(entity.getPatientEncounter());	
		
		Events.instance().raiseEvent(eventInjectIntoSession, entity.getInternalId());
		log.info("[cid="+Conversation.instance().getId()+"] Object injected: Appointment, id: " + entity.getInternalId());
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
	 * Eject appointment from session
	 * Raise event eventEjectFromSession, listen to this event into other actions to eject other objects from session
	 * @param conversationName
	 */
//	@RaiseEvent(eventEjectFromSession)
	private void ejectFromSession(String conversationName) {
		Banner banner = Banner.instance();
		banner.removeEntity(conversationName);
		Events.instance().raiseEvent(eventEjectFromSession);
		log.info("[cid="+Conversation.instance().getId()+"] Object ejected: Appointment");
	}
	
	
	/**
	 * This method has been defined to retrieve some informations during the printing of 
	 * a particular report PDF and it is used when these informations are not available in conversation in that moment.
	 *  
	 * It is retrieving a certain field of a certain number of Appointments with a statusCode (contained in statusCodeListSting)
	 * for a given AppointmentGrouper sorted as requested in the parameter "orderBy". 
	 * The filter on the SDL will be deactivated.
	 * during the read operation and restored before close the procedure. 
	 * @param numberAppointment -> number of Appointment to read
	 * @param appointmentGrouper -> the given appointmentGrouper
	 * @param attributeToRetrieve -> the attribute to be selected
	 * @param orderByRead -> the order of sorting (ascending,descending). 
	 * @param statusCodeListSting -> the list of the statusCode
	 * @return the LazyList returned from the read , Null if some error occurred
	 */
	public LazyList<HashMap> getSortedAttributeFromAppointment(int numberOfAppointment, AppointmentGrouper appointmentGrouper,String attributeToRetrieve, String orderByRead, String... statusCodeListSting) throws PhiException{

		if(numberOfAppointment == 0 || appointmentGrouper == null || statusCodeListSting == null
				|| attributeToRetrieve == null || orderByRead == null){
			log.warn("The parameters cannot be null or 'zero'! The read will not be executed. " +
					"Watch out the parameters: Number of Appointment =" + numberOfAppointment 
					+ ", AppointmentGrouper = " + appointmentGrouper + ", status Code List =" + statusCodeListSting
					+ ", attribute to select =" + attributeToRetrieve + ", orderBy =" + orderByRead );
			return null;
		}

		if(!orderByRead.equals("ascending") && !orderByRead.equals("descending")){
			log.warn("The orderBy parameter is not containing a valid value. The read will not be executed");
			return null;
		}

		LazyList<HashMap> appointmentList = new LazyList<HashMap>();
		int tmpReadPageSize = 0;

		ArrayList<String> statusCodeList = new ArrayList<String>(Arrays.asList(statusCodeListSting));

		if(entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
		}

		if(this.getReadPageSize()!=null)
			tmpReadPageSize = this.getReadPageSize();
		this.setFilterBySdl(false);
		this.setReadPageSize(numberOfAppointment);
		this.addCriteria("eq", "statusCode.code", statusCodeList);
		this.equal.put("appointmentGrouper", appointmentGrouper);
		this.select.add(attributeToRetrieve);
		this.orderBy.put(attributeToRetrieve,orderByRead);
		appointmentList = (LazyList<HashMap>) this.select();
		this.setReadPageSize(tmpReadPageSize);
		this.setFilterBySdl(true);

		return appointmentList;
	}

	/**
	 * This method is useful when due to some operation on the Appointment
	 * either from process either from dashboard, it is required to update the
	 * status of all the entities which are linked.
	 * 
	 * @param appointment
	 * @param statusCode
	 *            the new status code for the entity appointment
	 * @throws PhiException
	 */
	
	public boolean changeStatusCodeCascade(Appointment appointment, String statusCode, CodeValue patientEncounterCode) throws PhiException {

		if (appointment == null) {
			log.warn("No appointment in conversation");
			return false;
		}

		if (statusCode == null || statusCode.isEmpty()) {
			log.warn("No statusCode has been defined");
			return false;
		}

		Vocabularies vocabularies = VocabulariesImpl.instance();
		CodeValue statusCodeProcedure = null;
		CodeValue statusCodeEncounter = null;
		CodeValue statusCodeAppointment = null;
		CodeValue statusCodeAppointmentGrouper = null;

		AppointmentGrouper appointmentGrouper = appointment.getAppointmentGrouper();
		PatientEncounter patientEncounter = appointment.getPatientEncounter();

		// COMPLETE THE APPOINTMENT
		/*
		 * Erogazione [DASHBOARD (Eroga)] LISTA.statusCode =
		 * PHIDIC:AppGroupStatus:active APPUNTAMENTO.statusCode =
		 * PHIDIC:AppointmentStatus:completed CASO.statusCode =
		 * PHIDIC:EncounterStatus:completed
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:completed
		 */

		if (statusCode.equals(COMPLETE)) {
			if (appointment.getStatusCode().getCode().equals("reported")
					|| appointment.getStatusCode().getCode().equals("cancelled")) {
				return false;
			}

			// create patientEncounter if null for appointment linked to a
			// Patient
			if (appointment.getPatient() != null && patientEncounter == null) {
				PatientEncounterAction actionPatientEncounter = new PatientEncounterAction();
				patientEncounter = actionPatientEncounter.createNewPatientEncounter(appointment, vocabularies.getCodeValueCsDomainCode("PHIDIC","EncounterKind","AMB"), patientEncounterCode);
				appointment.setPatientEncounter(patientEncounter);
			}

			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "completed");
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "completed");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "completed");

			if (appointmentGrouper != null && !appointmentGrouper.getStatusCode().getCode().equals("completed")) {
				statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus", "active");
			}

		}

		// REPORT THE APPOINTMENT
		/*
		 * Accettazione e Erogazione [Da processo Refertazione GENERALE]
		 * LISTA.statusCode = PHIDIC:AppGroupStatus:active
		 * APPUNTAMENTO.statusCode = PHIDIC:AppointmentStatus:reported
		 * CASO.statusCode = PHIDIC:EncounterStatus:completed
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:completed
		 */

		else if (statusCode.equals(REPORT)) {
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "completed");
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "completed");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "reported");

			if (appointmentGrouper != null && !appointmentGrouper.getStatusCode().getCode().equals("completed")) {
				statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus","active");
			}
		}

		// CANCEL THE APPOINTMENT
		/*
		 * Cancellazione [DASHBOARD (Cancella)] APPUNTAMENTO.statusCode =
		 * PHIDIC:AppointmentStatus:cancelled CASO.statusCode =
		 * PHIDIC:EncounterStatus:cancelled
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:nullified
		 */
		else if (statusCode.equals(CANCEL)) {
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "cancelled");
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "cancelled");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "cancelled");

			/*
			 * if the appointment is the first of an appointmentGrouper the
			 * status will change into "new" if(appointmentGrouper != null &&
			 * appointmentGrouper.getStatusCode().getCode().equals("planned" ))
			 * { statusCodeAppointmentGrouper =
			 * vocabularies.getCodeValueCsDomainCode
			 * ("PHIDIC","AppGroupStatus","new"); }
			 */
		}

		// CANCEL THE APPOINTMENT AND REACTIVATE THE APPOINTMENGROUPER
		/*
		 * Rimettere in lista di attesa [DASHBOARD (Rimetti in lista di attesa)]
		 * LISTA.statusCode = PHIDIC:AppGroupStatus:new APPUNTAMENTO.statusCode
		 * = PHIDIC:AppointmentStatus:cancelled CASO.statusCode =
		 * PHIDIC:EncounterStatus:cancelled
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:nullified
		 */

		else if (statusCode.equals(CANCEL_AND_REACTIVATE)) {
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "nullified");
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "cancelled");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "cancelled");

			// if the appointment is the first of an appointmentGrouper the
			// status will change into "new"
			if (appointmentGrouper != null && appointmentGrouper.getStatusCode().getCode().equals("planned")) {
				statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus", "new");
			}
		}

		// CONFIRM THE APPOINTMENT
		/*
		 * Accettazione Paziente [DASHBOARD (Conferma arrivo)] LISTA.statusCode
		 * = PHIDIC:AppGroupStatus:active APPUNTAMENTO.statusCode =
		 * PHIDIC:AppointmentStatus:arrived CASO.statusCode =
		 * PHIDIC:EncounterStatus:new
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:new
		 */
		else if (statusCode.equals(CONFIRM)) {
			if (appointment.getStatusCode().getCode().equals("completed") 
					|| appointment.getStatusCode().getCode().equals("reported") 
					|| appointment.getStatusCode().getCode().equals("cancelled")) {
				return false;
			}

			// create patientEncounter if null for appointment linked to a
			// Patient
			if (appointment.getPatient() != null && patientEncounter == null) {
				PatientEncounterAction actionPatientEncounter = new PatientEncounterAction();
				patientEncounter = actionPatientEncounter.createNewPatientEncounter(appointment, patientEncounterCode);
				appointment.setPatientEncounter(patientEncounter);
			}

			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "new");
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "arrived");
			// set the appointmentGrouper status Code to Active

			if (appointmentGrouper != null
					&& !appointmentGrouper.getStatusCode().getCode().equals("completed")) {
				statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus", "active");
			}
		}

		// CONFIRM AND COMPLETE THE APPOINTMENT
		/*
		 * Accettazione e Erogazione [DASHBOARD (Eroga)] LISTA.statusCode =
		 * PHIDIC:AppGroupStatus:active APPUNTAMENTO.statusCode =
		 * PHIDIC:AppointmentStatus:completed CASO.statusCode =
		 * PHIDIC:EncounterStatus:completed
		 * APPOINTMENT.procedureRequest.Procedure.statusCode =
		 * PHIDIC:StatusCodes:completed
		 */

		else if (statusCode.equals(CONFIRM_AND_COMPLETE)) {
			if (appointment.getStatusCode().getCode().equals("completed")
					|| appointment.getStatusCode().getCode().equals("reported")
					|| appointment.getStatusCode().getCode().equals("cancelled")) {
				return false;
			}

			// create patientEncounter if null
			if (patientEncounter == null) {
				PatientEncounterAction actionPatientEncounter = new PatientEncounterAction();
				patientEncounter = actionPatientEncounter.createNewPatientEncounter(appointment,patientEncounterCode);
				appointment.setPatientEncounter(patientEncounter);
			}

			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "completed");
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "completed");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "completed");

			if (appointmentGrouper != null
					&& !appointmentGrouper.getStatusCode().getCode().equals("completed")) {
				statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus", "active");
			}

		}
		// PATIENT ARRIVED
		/*
		 * CASO.statusCode = PHIDIC:EncounterStatus:new
		 */
		else if (statusCode.equals(ARRIVED)) {
			if (appointment.getStatusCode().getCode().equals("completed")
					|| appointment.getStatusCode().getCode().equals("reported")
					|| appointment.getStatusCode().getCode().equals("cancelled")) {
				return false;
			}

			// create patientEncounter if null for appointment linked to a
			// Patient
			if (appointment.getPatient() != null && patientEncounter == null) {
				PatientEncounterAction actionPatientEncounter = new PatientEncounterAction();
				patientEncounter = actionPatientEncounter.createNewPatientEncounter(appointment, patientEncounterCode);
				appointment.setPatientEncounter(patientEncounter);
			}

			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "EncounterStatus", "new");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "arrived");

		} else if (statusCode.equals(NULLIFY)) {
			statusCodeProcedure = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "nullified");
			statusCodeEncounter = vocabularies.getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "nullified");
			statusCodeAppointment = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "nullified");
		}

		// CHANGE THE STATUSCODE OF THE PROCEDURES
		if (statusCode.equals(COMPLETE) 
				|| statusCode.equals(REPORT)
				|| statusCode.equals(CONFIRM_AND_COMPLETE)) {

			if (appointment.getStatusCode().getCode().equals("cancelled")) {
				return false;
			}

			List<Procedure> performedProcedure = appointment.getPerformedProcedure();
			if (performedProcedure != null) {
				for (Procedure proc : performedProcedure) {
					if (!proc.getStatusCode().getCode().equals(NULLIFIED_PROCEDURE)) {
						proc.setStatusCode(statusCodeProcedure);
					}
				}
			}

			// get the procedure Appointment.procedure and update the
			// statusCode
			Procedure procedureAppointment = appointment.getProcedure();
			if (procedureAppointment != null) {
				procedureAppointment.setStatusCode(statusCodeProcedure);
			}

			// go through the ProcedureRequest list to search for the
			// procedures
			List<ProcedureRequest> listProcedureRequest = appointment.getProcedureRequest();

			if (listProcedureRequest != null) {

				List<Procedure> listProcedure = null;
				for (ProcedureRequest procReqTmp : listProcedureRequest) {

					listProcedure = procReqTmp.getProcedure();
					if (listProcedure != null) {
						for (Procedure proc : listProcedure) {
							if (!proc.getStatusCode().getCode().equals(NULLIFIED_PROCEDURE)) {
								proc.setStatusCode(statusCodeProcedure);
							}
						}
					}
				}
			}
		}

		// update the statusCode of all entities
		appointment.setStatusCode(statusCodeAppointment);

		if (patientEncounter != null && statusCodeEncounter != null) {
			appointment.getPatientEncounter().setStatusCode(statusCodeEncounter);
		}
		// update the AppointmentGrouper statusCode only if has been changed
		if (appointmentGrouper != null && statusCodeAppointmentGrouper != null) {
			appointment.getAppointmentGrouper().setStatusCode(statusCodeAppointmentGrouper);
		}

		return true;
		// }
	}

	/**
	 * Cancel the selected appointment
	 * @param appointment the entity
	 * @throws DictionaryException
	 * @throws PhiException
	 * @throws NamingException 
	 */
	public String cancelAppointment(Appointment appointment, CodeValue reasonCancCode, String cancellationNote, boolean reactive) throws DictionaryException, PhiException, NamingException{

		LockManager delegate = Locker.instance();
		String lockedBy = null;
		
		//CHECK IF APPOINTMENT BELONGS TO AN APPOINTMENT_GROUPER LOCKED 
		if(appointment.getAppointmentGrouper()!=null){
			if(delegate.isLocked("PHI",Long.toString(appointment.getAppointmentGrouper().getInternalId()))){
				lockedBy = delegate.isLockedBy("PHI",Long.toString(appointment.getAppointmentGrouper().getInternalId()));
			}
		}
		
		if(lockedBy == null){
			//CHECK IF ANY USER IS REPORTING THE APPOINTMENT
			List<HashMap> reportList = new ArrayList<HashMap>();
			
			ReportAction repAction = (ReportAction) Component.getInstance(ReportAction.class, ScopeType.CONVERSATION);
	
			repAction.equal.put("appointment", appointment);
			repAction.equal.put("isActive", true);
			repAction.select.add("internalId");
			reportList = (List<HashMap>) repAction.select();
			for(HashMap hashTmp : reportList){
				if(delegate.isLocked("PHI",Long.toString((Long)hashTmp.get("internalId")))){
					lockedBy = delegate.isLockedBy("PHI",Long.toString((Long)hashTmp.get("internalId")));
				}
			}
		}

		if(lockedBy != null){
			//READ to retrieve name_giv and name_fam from employee with username "locked by"
			EmployeeAction empAction = EmployeeAction.instance();
			List<HashMap> employeeList = new ArrayList<HashMap>();
			empAction.equal.put("username", lockedBy);
			empAction.select.add("name.giv");
			empAction.select.add("name.fam");
			employeeList = (List<HashMap>) empAction.select();
			for(HashMap nameEmployee : employeeList){
				lockedBy = (String)((HashMap)nameEmployee.get("name")).get("fam") +  " " + (String)((HashMap)nameEmployee.get("name")).get("giv");
			}
			
			return lockedBy;
		}

		if(reasonCancCode != null) {
			appointment.setCancReasonCode(reasonCancCode);
		}
		if(!cancellationNote.equals("")) {
			appointment.setCancReasonDetail(cancellationNote);
		}
		
		if (!reactive)
			changeStatusCodeCascade(appointment, CANCEL, null);
		else
			changeStatusCodeCascade(appointment, CANCEL_AND_REACTIVATE, null);
		return null;
	}

	
	private String selectedAppointmentColor=null;
	
	public String getSelectedAppointmentColor() {
		return selectedAppointmentColor;
	}

	public void setSelectedAppointmentColor(String selectedAppointmentColor) {
		this.selectedAppointmentColor = selectedAppointmentColor;
	}

	public String getHexColor() {
		getEntity();
		if (entity != null && entity.getColor() != null) {
			return Integer.toHexString(entity.getColor());
		}
		else 
			return "";
	}
	
	public void setColorAttribute() {
		
		  try {
			  
			  getEntity();
			  if (selectedAppointmentColor != null){
				  if (selectedAppointmentColor.indexOf("#")>=0){
					  selectedAppointmentColor = selectedAppointmentColor.substring(1);
				  }
				  int codiceColore = Integer.parseInt(selectedAppointmentColor, 16);
				  entity.setColor(codiceColore);
			  }			

		  } catch (Exception e) {
			  log.error("Error during set appointment's color ",e);
		  }
		
	}

	public String calculateProcedureString(Appointment app) {
		return this.calculateProcedureString(app, false);
	}
	public String calculateProcedureString(Appointment app,boolean addDescription) {
		if (app == null || app.getProcedureRequest() == null || app.getProcedureRequest().isEmpty()) {
			return "";
		}
		
		String ret = "";
		for (ProcedureRequest req : app.getProcedureRequest()) {
			if (req == null || req.getProcedure() == null || req.getProcedure().isEmpty())
				continue;
			
			for (Procedure p : req.getProcedure()) {
				if (p!= null && p.getCodeIcd9()!=null)
					if(addDescription){
						ret+=p.getCodeIcd9().getDisplayName()+" ("+p.getCodeIcd9().getCurrentTranslation()+"),";
					}
					else{
						ret+=p.getCodeIcd9().getDisplayName()+",";
					}
			}
		}
		if (ret.endsWith(","))
			ret= ret.substring(0,ret.length()-1);
		return ret;
	}
	public String calculatePerformedProcedureString(Appointment app) {
		//return calculatePerformedProcedureString(app, false, true);
		return calculatePerformedProcedureString(app, false);
	}
	
	//public String calculatePerformedProcedureString(Appointment app, boolean addCode, boolean addCicleNumber) {
	public String calculatePerformedProcedureString(Appointment app, boolean addCode) {
		String ret = "";

		if (app != null) {
			List<Procedure> performedProcs = app.getPerformedProcedure();
			if(performedProcs != null && !performedProcs.isEmpty()) {
				for (Procedure performedProc : performedProcs) {
					if (performedProc == null) continue;
					CodeValue cv = performedProc.getCodeIcd9();
					if (cv != null) {
						if (!ret.isEmpty()) {
							ret += ", ";
						}
						if (addCode)
							ret += "["+cv.getCode()+"] ";

						ret += cv.getDisplayName();
						
						//if ((addCicleNumber)&&(performedProc.getPlacerField1()!=null)&&(!performedProc.getPlacerField1().equals("")))
						//	ret += " ("+performedProc.getPlacerField1()+")";

					}
				}
			}
		}
		
		return ret;
	}


	/**
	 * this methods is useful in order to refresh all the entities linked to an appointment before saving a Report
	 * @throws PhiException 
	 */
	public void refreshAppointmentCascade() throws PhiException{

		//REFRESH APPOINTMENT
		Appointment app = getEntity();

		if (app != null) { 
			// GET PATIENT ENCOUNTER
			PatientEncounter pe = app.getPatientEncounter();

			// REFRESH APPOINTMENT
			ca.refreshIfNeeded(app); 

			//REFRESH APPOINTMENT GROUPER
			AppointmentGrouper appGroup = app.getAppointmentGrouper();
			if(appGroup != null){
				ca.refreshIfNeeded(appGroup);
			}

			//REFRESH PROCEDURE
			Procedure proc = app.getProcedure();
			if(proc != null){
				ca.refreshIfNeeded(proc);
			}

			//REFRESH PROCEDURE REQUEST AND LINKED PROCEDURES

			//go through the ProcedureRequest list to search for the procedures
			List<ProcedureRequest> listProcedureRequest = app.getProcedureRequest();

			if(listProcedureRequest != null){

				List<Procedure> listProcedure = null;
				for(ProcedureRequest currentProcReq : listProcedureRequest){

					listProcedure = currentProcReq.getProcedure();
					if(listProcedure != null) {
						for (Procedure currentProc : listProcedure) {
							ca.refreshIfNeeded(currentProc);
						}
					}
					ca.refreshIfNeeded(currentProcReq);
				} 
			}
			
			//REFRESH PATIENT ENCOUNTER
			if(pe != null){
				if (pe.getInternalId() < 1) {
					pe.addAppointment(app);
				} else {
					ca.refreshIfNeeded(pe);
				}
			}

		}
	}
}
