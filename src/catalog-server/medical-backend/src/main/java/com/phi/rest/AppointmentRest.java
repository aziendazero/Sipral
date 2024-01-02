package com.phi.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Procedure;
import com.phi.entities.actions.AppointmentAction;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.entities.baseEntity.ProcedureRequest;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.events.PhiEvent;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AppointmentRest")
@Path("/appointments")
public class AppointmentRest extends BaseRest<Appointment> implements Serializable {

	private static final long serialVersionUID = -5016385326214921028L;
	private static final Logger log = Logger.getLogger(AppointmentRest.class);
	private static final int resultPerPage = 20;
	
	private static final Pattern pattern=Pattern.compile("\\d+");
	
	
	/**
	 * Query for ambulatory appointments used by Ambulatory Portal dashboard
	 */
	private static final String SQL_AMBULATORY_SELECT = 
			" SELECT "+
			" app.text_string as text_string," +
			" app.internal_id as internalId," +
			" app.defaultDate as defaultDate," +
			" appStatusCode.code as statusCode_Code," +
			" pat.internal_id as patient_InternalId," +
			" pat.birth_time as patient_BirthTime," +
			" pat.name_fam as patient_name_fam," +
			" pat.name_giv as patient_name_giv," +
			" pat.fiscal_code as patient_FiscalCode," +
			" LISTAGG (multiProcedureCode.display_name || '@@' || p.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name) As procedurerequest_procedure, "+
			" procCode.lang_it as proc_code_trans," +
			" procClassCode.code as proc_classcode," +
			" app.serviceDeliveryLocation as sdl_internalId," +
			" agenda.name_giv	as sdl_name_giv," +
			" agenda.Parent_SDL as sdl_parent_internalId," +
			" ambulatory.name_giv as sdl_parent_name_giv," +
			" ambulatoryArea.code as sdl_parent_area,"+
			" appGrouper.internal_id as appointmentGrouper_internalId," +
			" appGrouperSC.code as appointmentGrouper_statusCode," +
			" enc.internal_id as encounter_internalId," +
			" app.external_id_root as externalIdRoot,"+
			" app.external_id as externalId";
	
	private static final String SQL_AMBULATORY_SELECT_INTACT =			
			", sdlFrom.name_giv as fromsdl_name";
	
	private static final String SQL_AMBULATORY_JOIN =			
			" FROM" +
			" service_delivery_location agenda," +
			" service_delivery_location ambulatory" +
			" LEFT OUTER JOIN code_value ambulatoryArea ON ambulatory.area = ambulatoryArea.id," +
			" appointment app" +
			" INNER JOIN code_value appStatusCode ON app.status_code=appStatusCode.id" +
			" LEFT OUTER JOIN procedure_db proc ON app.procedure_id=proc.internal_id" +
			" LEFT OUTER JOIN code_value procCode ON proc.code=procCode.id" +
			" LEFT OUTER JOIN code_value procClassCode ON proc.class_code=procClassCode.id" +
			" INNER JOIN patient pat ON app.patient_id=pat.internal_id" +
			" LEFT OUTER JOIN appointment_grouper appGrouper ON app.appointmentGrouper_id=appGrouper.internal_id" +
			" LEFT OUTER JOIN code_value appGrouperSC ON appGrouper.status_code=appGrouperSC.id" +
			" LEFT OUTER JOIN patient_encounter enc ON app.patient_encounter=enc.internal_id" +
			" LEFT OUTER JOIN procedure_request request ON app.internal_id=request.appointment_id" +
			" LEFT OUTER JOIN procedure_db p ON (request.internal_id=p.procreq_id OR p.appointment_id=app.internal_id)" +
			" LEFT OUTER JOIN code_value procStatusCode ON p.status_code=procStatusCode.id" +
			" LEFT OUTER JOIN code_value_icd9 multiProcedureCode ON (p.code_icd9=multiProcedureCode.id AND procStatusCode.code IN ('new', 'completed', 'nullified', 'cancelled'))";
	
	private static final String SQL_AMBULATORY_JOIN_INTACT =
			" LEFT OUTER JOIN intactivity intAct ON app.internal_id=intAct.appointment_id" +
			" LEFT OUTER JOIN service_delivery_location sdlFrom ON intAct.from_sdl_id=sdlFrom.internal_id";
	
	private static final String SQL_AMBULATORY_WHERE =
			" WHERE" +
			" app.serviceDeliveryLocation = agenda.internal_id" +
			" AND agenda.Parent_SDL = ambulatory.internal_id" +
			" AND app.insert_completed = 1" +
			" AND app.serviceDeliveryLocation IN (:sdlIds)";
	
	private static final String SQL_AMBULATORY_FILTER_CUP = 
			" AND ambulatory.int_act_supported = 0";
		
	private static final String SQL_AMBULATORY_FILTER_CONSULENCE = 
			" AND ambulatory.int_act_supported = 1";
	
	private static final String SQL_AMBULATORY_FILTER_DATE_STATUS_EXTENDED = 
			" AND (" +
			"	(" +
			"		app.defaultDate >= :startDateExtended" +
			"		AND app.defaultDate < :startDate" +
			"		AND appStatusCode.code IN ('planned','arrived','completed')" +
			"	) OR (" +
			"		app.defaultDate>= :startDate" +
			"		AND app.defaultDate<:endDate" +
			"		AND appStatusCode.id IN (:statusCodes)" +
			"	)" +
			" )";
	
	private static final String SQL_AMBULATORY_FILTER_DATE_STATUS_SIMPLE = 
			" AND app.defaultDate >= :startDate" +
			" AND app.defaultDate < :endDate" +
			" AND appStatusCode.id IN (:statusCodes)";
			
	private static final String SQL_AMBULATORY_GROUPBY = 
			" GROUP BY" +
			" app.text_string," +
			" app.internal_id," +
			" app.defaultDate," + 
			" appStatusCode.code," +
			" pat.internal_id," +
			" pat.birth_time," +
			" pat.name_fam," +
			" pat.name_giv," +
			" pat.fiscal_code," +
			" procCode.lang_it," +
			" procClassCode.code," +
			" app.serviceDeliveryLocation," +
			" agenda.name_giv," +
			" agenda.Parent_SDL," +
			" ambulatory.name_giv," +
			" ambulatoryArea.code,"+
			" appGrouper.internal_id," +
			" appGrouperSC.code," +
			" enc.internal_id," +
			" app.external_id_root,"+
			" app.external_id";	
	
	private static final String SQL_AMBULATORY_GROUPBY_INTACT = 
			", sdlFrom.name_giv";
			
	private static final String SQL_AMBULATORY_ORDERBY = 
			" ORDER BY app.defaultDate";

	private static final String SQL_APPOINTMENT_DETAILS = 
			" SELECT" +
			" app.internal_id as internalId," +
			" pat.name_giv as patient_name_giv," +
			" pat.name_fam as patient_name_fam," +
			" sdlApp.name_giv as agenda_name_giv," +
			" app.defaultDate as defaultDate," +
			" app.text_string as text_string," +
			" app.canc_reason_detail as cancReasonDetails," + 
			" appAuthor.name_fam as author_name_fam," +
			" appAuthor.name_giv as author_name_giv," +
			" LISTAGG (multiProcedureCode.display_name || '@@' || procR.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name) As procedurerequest_procedure, "+
			" procedurecode.lang_it as proc_code_trans," +
			" grouper.internal_id as appGroup_internalId," +
			" groupAuthor.name_fam as appGroup_Author_name_fam," +
			" groupAuthor.name_giv as appGroup_Author_name_giv," +
			" sdl.name_giv as appGroup_Amb_name_giv," +
			" grouper.availability_time as appGroup_AvaTime," +
			" ageType.lang_it as appGroup_ageType_trans," +
			" priority.lang_it as appGroup_priority_trans," +
			" sentBy.lang_it as appGroup_sentBy_trans," +
			" levelCode.lang_it as levelCode_trans," +
			" location.lang_it as appGroup_location_trans," +
			" grouper.title_string as appGroup_title_string," +
			" grouper.text_string as appGroup_text_string," +
			" grouper.phone_number appGroup_phoneNumber," +
			" codeApp.lang_it as appGroup_proc_trans," +
			" patientType.lang_it as appGroup_patientType_trans," +
			" spokenLanguage.lang_it as appGroup_spokLang_trans," +
			" diagnosis.lang_it as appGroup_diagnosis_trans," +
			" pat.birth_time as patient_birthTime," +
			" cancReasonCode.lang_it as cancCode_trans," +
			" app.creation_date as creationDate," +
			" app.step_marker as stepMarker" +
			" FROM appointment app"+
			" LEFT JOIN patient pat ON app.patient_id=pat.internal_id" +
			" LEFT JOIN service_delivery_location sdlApp ON app.servicedeliverylocation=sdlApp.internal_id" + 
			" LEFT JOIN appointment_grouper grouper ON app.appointmentgrouper_id=grouper.internal_id" +
			" LEFT JOIN procedure_db p ON app.procedure_id=p.internal_id" +
			" LEFT JOIN code_value procedureCode ON p.code=procedurecode.id" +
			" LEFT JOIN code_value cancReasonCode ON app.cancreasoncode=cancReasonCode.id" +
			" LEFT JOIN employee appAuthor ON app.author_id=appauthor.internal_id" +
			" LEFT JOIN employee groupAuthor ON grouper.author_id=groupAuthor.internal_id" +
			" LEFT JOIN service_delivery_location sdl ON grouper.servicedeliverylocation=sdl.internal_id" +
			" LEFT JOIN code_value ageType ON grouper.agetype=ageType.id" +
			" LEFT JOIN code_value priority ON grouper.priority=priority.id" +
			" LEFT JOIN code_value sentBy ON grouper.sentby=sentBy.id"+
			" LEFT JOIN code_value levelCode ON grouper.level_code=levelCode.id" +
			" LEFT JOIN code_value codeApp ON grouper.code=codeApp.id" +
			" LEFT JOIN code_value location ON grouper.location=location.id" +
			" LEFT JOIN code_value patientType ON grouper.patienttype=patientType.id" +
			" LEFT JOIN code_value spokenLanguage ON grouper.spokenLanguage=spokenLanguage.id" +
			" LEFT JOIN code_value diagnosis ON grouper.diagnosis=diagnosis.id" +
			" LEFT OUTER JOIN procedure_request request ON app.internal_id=request.appointment_id" +
			" LEFT OUTER JOIN procedure_db procR ON (request.internal_id=procR.procreq_id OR procR.appointment_id=app.internal_id)" +	
			" LEFT OUTER JOIN code_value procStatusCode ON procR.status_code=procStatusCode.id" +
			" LEFT OUTER JOIN code_value_icd9 multiProcedureCode ON (procR.code_icd9=multiProcedureCode.id AND procStatusCode.code in ('new', 'completed', 'nullified', 'cancelled'))" +
			" WHERE app.internal_id = (:appointmentId)" +
			" GROUP BY" +
			" app.internal_id," +
			" pat.name_giv," +
			" pat.name_fam," +
			" sdlApp.name_giv," +
			" app.defaultDate," +
			" app.text_string," +
			" app.canc_reason_detail," +
			" appAuthor.name_fam," +
			" appAuthor.name_giv," +
			" procedurecode.lang_it," +
			" grouper.internal_id," +
			" groupAuthor.name_fam," +
			" groupAuthor.name_giv," +
			" sdl.name_giv," +
			" grouper.availability_time," +
			" ageType.lang_it," +
			" priority.lang_it," +
			" sentBy.lang_it," +
			" levelCode.lang_it," +
			" location.lang_it," +
			" grouper.title_string," +
			" grouper.text_string," +
			" grouper.phone_number," +
			" codeApp.lang_it," +
			" patientType.lang_it," +
			" spokenLanguage.lang_it," +
			" diagnosis.lang_it," +
			" pat.birth_time," +
			" cancReasonCode.lang_it," +
			" app.creation_date," +
			" app.step_marker" +
			" ORDER BY app.defaultDate";
	
	private static String SQL_INTERNAL_ACTIVITY_DETAIL = 
			" SELECT" +
			" sdl.name_giv as applicant," +
			" author.name_giv || ' ' || author.name_fam as author," +
			" encounter_id.extension as visitnumber," +
			" costc.lang_it as costcenter," +
			" intAct.note as anamnesticsuspicion," +
			" intAct.details as ongoingtherapy," +
			" intAct.requester_note as clinicalquestion," +
			" intAct.bedridden," +
			" intAct.examination," +
			" actt.lang_it as activitytype," +
			" LISTAGG (exType.lang_it, ', ') Within Group (ORDER BY exType.lang_it) as examtype" +
			" FROM intactivity intAct" +
			" LEFT OUTER JOIN Employee author ON author.internal_id=intAct.author_id" +
			" LEFT OUTER JOIN Patient_Encounter_id encounter_id ON (encounter_id.pat_enc_id=intAct.patient_encounter_id AND root='XDSPID')" +
			" LEFT OUTER JOIN Service_Delivery_Location sdl ON sdl.internal_id=intAct.from_sdl_id" +
			" LEFT OUTER JOIN code_value costc ON intAct.costcenter=costc.id" +
			" LEFT OUTER JOIN code_value actt ON intAct.activitytype=actt.id" +
			" LEFT OUTER JOIN intAct_CodeValuePhi examintationType ON intAct.internal_id=examintationType.intact_id" +
			" LEFT OUTER JOIN code_value exType ON examintationType.examtype=exType.id" +
			" WHERE intAct.appointment_id = :appointmentId" +
			" GROUP BY" +
			" sdl.name_giv," +
			" author.name_giv," +
			" author.name_fam," +
			" encounter_id.extension," +
			" costc.lang_it," +
			" intAct.note," +
			" intAct.details," +
			" intAct.requester_note," +
			" intAct.bedridden," +
			" intAct.examination," +
			" actt.lang_it";
	
	
	
			
	private static final String sqlAppointmentsForPatientFirstPart = "select "+
			"   app.defaultDate as appdate, "+
			"   appStatusCode.code as status, "+
			"   CASE" +
			"       WHEN LISTAGG (multiProcedureCode.display_name || '@@' || p.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name) is not null " +
			"       THEN LISTAGG (multiProcedureCode.display_name || '@@' || p.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name) "+
			"	    ELSE procCode.lang_it" +
			"   END as procedure, "+
			"   agenda.name_giv	as agenda, "+
			"   ambulatory.name_giv as ambulatory " +
			"from  "+
			"   appointment app "+
			"       inner join code_value appStatusCode on app.status_code=appStatusCode.id  "+
			"       left outer join procedure_db proc on app.procedure_id=proc.internal_id  "+
			"       left outer join code_value procCode on proc.code=procCode.id  "+
			"       inner join patient pat on app.patient_id=pat.internal_id  "+
			"       left outer join procedure_request request on app.internal_id=request.appointment_id  "+
			"       left outer join procedure_db p on (request.internal_id=p.procreq_id or p.appointment_id=app.internal_id)  " +
			"       left outer join code_value procStatusCode on p.status_code=procStatusCode.id " +
			"       left outer join code_value_icd9 multiProcedureCode on ((p.code_icd9=multiProcedureCode.id) and (procStatusCode.code in ('new', 'completed', 'nullified', 'cancelled'))), "+
			"   service_delivery_location agenda, "+
			"   service_delivery_location ambulatory  "+
			"where  "+
			"   app.serviceDeliveryLocation=agenda.internal_id  "+
			"   and agenda.Parent_SDL=ambulatory.internal_id  "+
			"   and app.insert_completed=1 " +
			"   and pat.internal_id = (:patientId) ";
			
	private static final String fromDefaultDateWhereRestriction = " and app.defaultDate>=(:startDate) ";
	private static final String agendaWhereRestriction = " and agenda.internal_id in (:agendaIds)";
	
	private static final String sqlAppointmentsForPatientGroupBy = "group by  "+
			"   app.defaultDate, "+ 
			"   appStatusCode.code," +
			"   procCode.lang_it, "+
			"   agenda.name_giv, "+
			"   ambulatory.name_giv "+
			"order by app.defaultDate";
	
	private static final String sqlAllPatientAppointments = "select " + 
			"     patient.internal_id as patientId, " + 
			"     appointment.internal_id as internalId, " + 
			"     appointment.defaultDate as appDate, " + 
			"     appointmentStatusCode.code as status, " + 
			"     case " + 
			"       when LISTAGG (multiProcedureCode.display_name || '@@' || p.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name) is not null  " + 
			"             then LISTAGG (multiProcedureCode.display_name || '@@' || p.quantity || '@@' || procStatusCode.code , '&&') Within Group (ORDER BY multiProcedureCode.display_name)  " + 
			"             else macroProcedureCode.lang_it " + 
			"     end as procedure, " + 
			"     agenda.name_giv as agenda, " + 
			"     ambulatory.name_giv as ambulatory, " + 
			"     appointment.serviceDeliveryLocation as agendaId, " + 
			"     agenda.Parent_SDL as ambulatoryId, " + 
			"     appGrouper.internal_id as appointmentGrouperId, " +
			"     grouperStatusCode.code as appointmentGrouperCode," + 
			"     encounter.internal_id as encounterId, " +
			"     macroProcedureType.code as macroprocedurecode " + 
			" from appointment appointment  " + 
			" inner join patient patient on appointment.patient_id=patient.internal_id  " + 
			" left outer join procedure_db macroProcedure on appointment.procedure_id=macroProcedure.internal_id  " + 
			" left outer join code_value macroProcedureCode on macroProcedure.code=macroProcedureCode.id  " + 
			" left outer join code_value macroProcedureType on macroProcedure.class_code=macroProcedureType.id  " +
			" left outer join appointment_grouper appGrouper on appointment.appointmentGrouper_id=appGrouper.internal_id  " + 
			" left outer join code_value grouperStatusCode on appGrouper.status_code=grouperStatusCode.id " + 
			" left outer join patient_encounter encounter on appointment.patient_encounter=encounter.internal_id " + 
			" left outer join procedure_request request on appointment.internal_id=request.appointment_id " + 
			" left outer join procedure_db p on (request.internal_id=p.procreq_id or p.appointment_id=appointment.internal_id) " + 
			" left outer join code_value procStatusCode on p.status_code=procStatusCode.id " + 
			" left outer join code_value_icd9 multiProcedureCode on ((p.code_icd9=multiProcedureCode.id) and (procStatusCode.code in ('new', 'completed', 'nullified', 'cancelled'))), " + 
			" code_value appointmentStatusCode, " + 
			" service_delivery_location agenda, " + 
			" service_delivery_location ambulatory  " + 
			" where " + 
			"     appointment.status_code=appointmentStatusCode.id  " + 
			"     and appointment.serviceDeliveryLocation=agenda.internal_id  " + 
			"     and agenda.Parent_SDL=ambulatory.internal_id  " + 
			"     and patient.internal_id= :patientId  " + 
			"     and appointment.insert_completed=1  " + 
			"     and ambulatory.Parent_SDL=( " + 
			"         select auxAmbulatory.Parent_SDL  " + 
			"         from  " + 
			"         service_delivery_location auxAgenda, " + 
			"         service_delivery_location auxAmbulatory  " + 
			"         where " + 
			"         auxAgenda.Parent_SDL=auxAmbulatory.internal_id  " + 
			"         and auxAgenda.internal_id in (:agendaIds) " + 
			"     )  " + 
			" group by " + 
			"   patient.internal_id, " + 
			"   appointment.internal_id, " + 
			"   appointment.defaultDate, " + 
			"   appointmentStatusCode.code, " + 
			"   agenda.name_giv, " + 
			"   ambulatory.name_giv, " + 
			"   appointment.serviceDeliveryLocation, " + 
			"   agenda.Parent_SDL, " + 
			"   appGrouper.internal_id," +
			"   grouperStatusCode.code, " + 
			"   encounter.internal_id, " + 
			"   macroProcedureCode.lang_it," +
			"   macroProcedureType.code " + 
			" order by " + 
			"     appointment.defaultDate"; 	

		private static final String sqlAppointmentByProcedure = "select "+ 
	    		" app.internal_id," + 
	    		" app.defaultDate," + 
	    		" app.status_code," + 
	    		" pat.name_fam," + 
	    		" pat.name_giv," +
	    		" pat.internal_id as patid," +
	    		" pathSeq.internal_id as pathSeqId," +
	    		" pathStep.internal_id as pathStepId," +
	    		" durat.score," + 
	    		" sdl.internal_id as agendaid " + 
	    		" from appointment app" + 
	    		" join procedure_request procReq on procReq.appointment_id = app.internal_id" +
	    		" join procedure_db procDb on procDb.procReq_id = procReq.internal_id" + 
	    		" join code_value durat on procDb.level_code = durat.id" + 
	    		" join service_delivery_location sdl on app.servicedeliverylocation = sdl.internal_id" + 
	    		" join service_delivery_location sdlAmb on sdlamb.internal_id = sdl.parent_sdl" + 
	    		" left join patient pat on pat.internal_id = app.patient_id" + 
	    		" left join pathway_sequence pathSeq on pathSeq.appointment_id = app.internal_id" +
	    		" left join pathway_step pathStep on pathStep.appointment_id = app.internal_id" +
	    		" where sdlAmb.internal_id in (" + 
	    		"  select sdl.internal_id" + 
	    		"  from proceduredef_sdl pr" + 
	    		"  join procedure_definition pd on pr.proceduredef_id = pd.internal_id" + 
	    		"  left join code_value_icd9 cv on pd.code_icd9 = cv.id" + 
	    		"  left join service_delivery_location sdl on sdl.internal_id = pr.servicedeliverylocation_id" + 
	    		"  where  pd.code_icd9 = :id )" + 
	    		" and app.defaultDate>=:startDate  "+
	    		" and app.defaultDate<:endDate  "+
					" order by sdlAmb.name_giv , app.defaultDate ";
		
		private static final String sqlAppointmentByAmbulatory= "select "+ 
	    		" app.internal_id," + 
	    		" app.defaultDate," + 
	    		" app.external_id as externalId"+
	    		" statusCode.id as statusCode_id," + 
	    		" statusCode.code as statusCode_code," + 
	    		" statusCode.display_name as statusCode_displayName," + 
	    		//" app.status_code," + 
	    		" pat.name_fam," + 
	    		" pat.name_giv," +
	    		" pat.internal_id as patid," +
	    		" pathSeq.internal_id as pathSeqId," +
	    		" pathStep.internal_id as pathStepId," +
	    		" app.duration," + 
	    		//" durat.score," + 
	    		" sdl.internal_id as agendaid " + 
	    		" from appointment app" + 
	    		//" join procedure_request procReq on procReq.appointment_id = app.internal_id" +
	    		//" join procedure_db procDb on procDb.procReq_id = procReq.internal_id" + 
	    		//" join code_value durat on procDb.level_code = durat.id" + 
	    		" join service_delivery_location sdl on app.servicedeliverylocation = sdl.internal_id" + 
	    		" left join patient pat on pat.internal_id = app.patient_id" + 
	    		" left join pathway_sequence pathSeq on pathSeq.appointment_id = app.internal_id" +
	    		" left join pathway_step pathStep on pathStep.appointment_id = app.internal_id" +
	    		" left join code_value statusCode on statusCode.id = app.status_code" +
	    		" where sdl.internal_id in (:id)" + 
	    		" and app.defaultDate>=:startDate  "+
	    		" and app.defaultDate<:endDate  "+
					" order by sdl.name_giv , app.defaultDate ";
		
		private static final String sqlAmbulatoryByProcedure = "select "+ 
			" sdlAmb.name_giv as Ambulatorio," + 
			" sdl.name_giv as Agenda," +
			" sdl.internal_id" + 
			" from service_delivery_location sdl " +
			" join service_delivery_location sdlAmb on sdlamb.internal_id = sdl.parent_sdl" + 
			" where sdl.parent_sdl in (" + 
			"  select sdl.internal_id" + 
			"  from proceduredef_sdl pr" + 
			"  join procedure_definition pd on pr.proceduredef_id = pd.internal_id" + 
			"  left join code_value_icd9 cv on pd.code_icd9 = cv.id" +
			"  left join service_delivery_location sdl on sdl.internal_id = pr.servicedeliverylocation_id" + 
			"  where  pd.code_icd9 = :id )" + 
			" order by Ambulatorio, Agenda ";

		private static final String sqlAmbulatoryBySequence = "select "+ 
				" sdl.name_giv as Agenda," +
				" sdl.internal_id" + 
				" from operation_sequence opSeq " +
				" JOIN OpSeq_Sdloc opseqSdloc on opseqSdloc.OpSeq_id=opSeq.internal_id" +
				" JOIN service_delivery_location sdl on sdl.internal_id= opseqSdloc.Sdloc_id " +
				" WHERE opSeq.internal_id= :id " +
				" order by Agenda ";
		
		
		//Sdl for PAS (ambulatory) union Rooms for CRIS
		private static final String sqlAmbulatoryByStep = "select * from " +
				"((select sdl.name_giv as Agenda, sdl.internal_id as 'internal_id',null as 'startTime',null as 'endTime' "+
				"from operation_step opStep "+
				 "JOIN OpStep_sdloc opsStepSdloc on opsStepSdloc.OpStep_id=opStep.internal_id "+
				 "JOIN service_delivery_location sdl on sdl.internal_id= opsStepSdloc.sdloc_id "+
				 "WHERE opStep.internal_id= :id) "+				  
				"UNION "+
				 "( select distinct(th.theatre_name) as 'Agenda', " +
				 "th.service_delivery_location_id as  'internal_id', " +
				 "start_time as 'startTime', " +
				 "end_time as 'endTime' " + 
			 	"from theatre_session th " +
			 	"join service_delivery_location sid on sid.internal_id=th.service_delivery_location_id " +
			 	"join code_value codeType on codeType.id=sid.code "+
			 	"where specialtyCode in "+
			 	"(select opSeq.specCode from operation_step opStep "+
			 	"join operation_sequence opSeq on opSeq.internal_id=opStep.operation_sequence_id "+
			 	"where opStep.internal_id=:id and opStep.requires_session=1) AND codeType.code ='ROOM' AND th.macro_procedure = case when codeType.code ='ROOM' then (" +
			 	"SELECT icd9.code " +
			 	"FROM operation_step opStep " +
			 	"JOIN code_value_icd9 icd9 ON icd9.id= opSTep.codeIcd9 where opStep.internal_id=:id AND opStep.requires_session=1) " +
			 	"else null end )) " +
			 	"compound order by Agenda";
		
		private static final String sqlProcedureCounter = 	"SELECT count(*) as counter FROM appointment " +
															" LEFT OUTER JOIN procedure_request request ON appointment.internal_id=request.appointment_id" +
															" JOIN procedure_db p ON (request.internal_id=p.procreq_id OR p.appointment_id=appointment.internal_id OR p.internal_id=appointment.procedure_id)" +
															" WHERE" +
															" appointment.internal_id = :appointmentId";
		
		
		/**
		 * Used by Ambulatory Portal dashboard to show Appointment
		 * 
		 * @param dateStr filters Appointment.defaultDate
		 * @param sdlIds filters Appointment.serviceDeliveryLocation.internalId
		 * @param retrieveIntAct retrieve or not informations about InternalActivity
		 * @param statusCodes filters Appointment.statusCode.code
		 * @param page number of page, page size defined in: resultPerPage
		 * @return
		 * @throws PersistenceException
		 */
		@GET
		@Path("/ambulatory/{page}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getAmbulatoryAppointments(@QueryParam("date") String dateStr, @DefaultValue("1") @QueryParam("days") int days, @QueryParam("intact") Boolean retrieveIntAct, @QueryParam("type-filter") String typeFilter, @QueryParam("id") List<Long> sdlIds,  @QueryParam("status") List<String> statusCodes, @DefaultValue("0") @PathParam("page") int page) throws PersistenceException {
			try {

				String currentLang = Locale.instance().getLanguage();

				String sqlAmbulatory = SQL_AMBULATORY_SELECT;
				
				if (retrieveIntAct) {
					sqlAmbulatory += SQL_AMBULATORY_SELECT_INTACT;
				}
				
				sqlAmbulatory += SQL_AMBULATORY_JOIN;
				
				if (retrieveIntAct) {
					sqlAmbulatory += SQL_AMBULATORY_JOIN_INTACT;
				}
				
				sqlAmbulatory += SQL_AMBULATORY_WHERE;
				
				if ("cup".equals(typeFilter)) {
					sqlAmbulatory += SQL_AMBULATORY_FILTER_CUP;
				} else if ("consulence".equals(typeFilter)) {
					sqlAmbulatory += SQL_AMBULATORY_FILTER_CONSULENCE;
				}
				
				if (days > 1) {
					sqlAmbulatory += SQL_AMBULATORY_FILTER_DATE_STATUS_EXTENDED; 
				} else {
					sqlAmbulatory += SQL_AMBULATORY_FILTER_DATE_STATUS_SIMPLE;
				}
				
				sqlAmbulatory += SQL_AMBULATORY_GROUPBY; 
				
				if (retrieveIntAct) {
					sqlAmbulatory += SQL_AMBULATORY_GROUPBY_INTACT;
				}
				
				sqlAmbulatory += SQL_AMBULATORY_ORDERBY; 
				
				if (!currentLang.equals("it")) {
					sqlAmbulatory = sqlAmbulatory.replace("lang_it", "lang_" + currentLang);
				}
				
				// Replace LISTAGG
				sqlAmbulatory = ReplaceListaggBean.replaceListagg(sqlAmbulatory);

				SQLQuery qryAmbulatory = ca.createHibernateNativeQuery(sqlAmbulatory);
				
				Calendar cal = Calendar.getInstance();	
				
				if (days > 1) {
					cal.setTime(decodeISO8601(dateStr));
					cal.add(Calendar.DATE, 1 - days);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					qryAmbulatory.setParameter("startDateExtended", cal.getTime());			
				}
				
				// From Date at 00:00
				cal.setTime(decodeISO8601(dateStr));
				cal.set(Calendar.HOUR_OF_DAY,0);
				cal.set(Calendar.MINUTE,0);
				cal.set(Calendar.SECOND,0);
				cal.set(Calendar.MILLISECOND,0);
				qryAmbulatory.setParameter("startDate", cal.getTime());
				// To Date at 24:00 
				cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
				qryAmbulatory.setParameter("endDate", cal.getTime());

				qryAmbulatory.setParameterList("sdlIds", sdlIds);
				qryAmbulatory.setParameterList("statusCodes", statusCodes);

				qryAmbulatory.setMaxResults(resultPerPage);
				qryAmbulatory.setFirstResult(page*resultPerPage);

				qryAmbulatory.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				List<?> results = qryAmbulatory.list();

				String json = mapper.writeValueAsString(results);
				
				Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
				
				return Response.ok(json).build();

			} catch (Exception e) {
				log.error("Error getting ambulatory appointments for date: " + dateStr +", sdl List : " + sdlIds + ", statusCode List: " + statusCodes + ", page: " + page , e);
				return Response.ok("Error getting ambulatory appointments for date: " + dateStr +", sdl List : " + sdlIds + ", statusCode List: " + statusCodes + ", page: " + page).build();
			}
		}
		
		/**
		 * Used by Ambulatory Portal dashboard to show Appointment details
		 * 
		 * @param retrieveIntAct retrieve or not informations about InternalActivity
		 * @param id filters Appointment.internalId
		 * @return
		 * @throws PersistenceException
		 */
		@GET
		@Path("/details/{id : \\d+}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getAppointmentsDetails(@QueryParam("intact") Boolean retrieveIntAct, @PathParam("id") long id) throws PersistenceException {
			try {

				String currentLang = Locale.instance().getLanguage();
				List<?> detailsResults = null;
				List<?> internalActivityResults = null;

				String sqlAppointment = SQL_APPOINTMENT_DETAILS;

				if (!currentLang.equals("it")) {
					sqlAppointment = sqlAppointment.replace("lang_it", "lang_" + currentLang);
				}
				
				// Replace LISTAGG
				sqlAppointment = ReplaceListaggBean.replaceListagg(sqlAppointment);

				SQLQuery qryAppointment = ca.createHibernateNativeQuery(sqlAppointment);

				qryAppointment.setParameter("appointmentId", id);

				qryAppointment.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				detailsResults = qryAppointment.list();
					
				if (retrieveIntAct) {
					String sqlIntActivity = SQL_INTERNAL_ACTIVITY_DETAIL;
	
					if (!currentLang.equals("it")) {
						sqlIntActivity = sqlIntActivity.replace("lang_it", "lang_" + currentLang);
					}
					
					// Replace LISTAGG
					sqlIntActivity = ReplaceListaggBean.replaceListagg(sqlIntActivity);
	
					SQLQuery qryIntActivity = ca.createHibernateNativeQuery(sqlIntActivity);
	
					qryIntActivity.setParameter("appointmentId", id);
	
					qryIntActivity.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
					internalActivityResults = qryIntActivity.list();
				}
				
				Map<String, List<?>> results = new HashMap<String, List<?>>();
				
				results.put("appointment", detailsResults);
				results.put("internalActivity", internalActivityResults);
				
				String json = mapper.writeValueAsString(results);
				
				Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
				
				return Response.ok(json).build();

			} catch (Exception e) {
				log.error("Error getting appointment details, appointmentId :" + id, e);
				return Response.ok("Error getting appointment details, appointmentId :" + id).build();
			}
		}

		
		/**
		 * Used by Ambulatory Portal and Agenda dashboard to change the status of an appointment
		 * 
		 * @param id Appointment.internalId
		 * @return a Response including Appointment.internal Id if it ok, a Response with an error message otherwise
		 */
		@POST
		@Path("/deleteAll/{id : \\d+}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteAll(@PathParam("id") long id, @FormParam("cancellationNote") String cancellationNote) {
			try {
				AppointmentAction actionAppointment = new AppointmentAction(); 
				Appointment appointment = (Appointment)ca.get(entityClass, id);
				ca.refreshIfNeeded(appointment);
				AppointmentGrouper ag = appointment.getAppointmentGrouper();
				ca.refreshIfNeeded(ag);
				List<Appointment> appointmentList = ag.getAppointment();
				List<Long> ids = new ArrayList<Long>();
				if(appointmentList!=null) {
					for (Appointment appointmentToCancel : appointmentList) {
						actionAppointment.cancelAppointment(appointmentToCancel, null, cancellationNote, false);
						ids.add(appointmentToCancel.getInternalId());
						ca.create(appointmentToCancel);
						
						Events.instance().raiseEvent(PhiEvent.CREATE, appointmentToCancel);					
					}
					ca.flushSession();
				}
				String json = mapper.writeValueAsString(ids);
				return Response.ok(json).build();
			} catch (Exception e) {
				log.error("Error deleting multiple appointments", e);
				return Response.ok("Error deleting multiple appointments, appointmentId: " + id + ", cancellationNote: " + cancellationNote).build();
			}
		}	
		
		
		
	
	/**
	 * 
	 * Used by dashboard to:
	 * 1) Ambulatory Calendar: get future appointment for given patient (id), filtered on given agenda id list (agendaId), starting from date
	 * 2) Ambulatory Portal: get all patient appointment filtered on agenda.parent.parent
	 *  
	 * @param id (patient id)
	 * @param date (if is null, gets all appointments, else start from date)
	 * @param agendaIds (authenticated agenda filter)
	 * @return
	 */	
	@GET
	@Path("/patients")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPatientAppointments(@QueryParam("id") long id, @QueryParam("date") String date, @QueryParam("agendaId") List<Long>agendaIds) {
		try {
			String currentLang = Locale.instance().getLanguage();
			String sqlPatient;			
			SQLQuery qry = null;		

			if (date!=null) {//get future appointment
				
				date = date.replace(" ", "+"); //FIXME: urlencoding for + 
				sqlPatient = sqlAppointmentsForPatientFirstPart;
				
				sqlPatient += fromDefaultDateWhereRestriction;
			
				if (agendaIds.size()>0) {
					sqlPatient += agendaWhereRestriction;
				}
				
				sqlPatient += sqlAppointmentsForPatientGroupBy;
				
				if (!currentLang.equals("it")) {
					sqlPatient = sqlPatient.replace("lang_it", "lang_" + currentLang);
				}				
				
				// Replace LISTAGG
				sqlPatient = ReplaceListaggBean.replaceListagg(sqlPatient);
				
				qry = ca.createHibernateNativeQuery(sqlPatient);
	
				qry.setParameter("patientId", id); 
				qry.setParameter("startDate", decodeISO8601(date));	
				
				if (agendaIds.size()>0) {
					qry.setParameterList("agendaIds", agendaIds);	
				}
			} else {
				sqlPatient = sqlAllPatientAppointments;
				
				// Replace LISTAGG
				sqlPatient = ReplaceListaggBean.replaceListagg(sqlPatient);
								
				qry = ca.createHibernateNativeQuery(sqlPatient);
				
				qry.setParameter("patientId", id); 
				if (agendaIds.size()>0) {
					qry.setParameterList("agendaIds", agendaIds);	
				}
			}
			
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			List<?> results = qry.list();

			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error getting appointment list for patient id: " + id, e);
		//	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting appointment details").build();
			return Response.ok("Error getting appointment list for patient id: " + id).build();
		}
	}
	
	
	

	

	/**
	 * Used by Ambulatory Portal and Agenda dashboard to change the status of an appointment
	 * 
	 * @param id Appointment.internalId
	 * @return a Response including Appointment.internal Id if it ok, a Response with an error message otherwise
	 */
	@POST
	@Path("/status/{id : \\d+}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStatus(@PathParam("id") long id, @FormParam("action") String action, @FormParam("reasonCancCodeId") String reasonCancCodeId,@FormParam("cancellationNote") String cancellationNote,@FormParam("encounterCodeId") String encounterCodeId) {
		AppointmentAction actionAppointment = new AppointmentAction(); 
		HashMap<String, Object> response = new HashMap<String, Object>();
		Appointment appointment = (Appointment)ca.get(entityClass, id);
		ca.refreshIfNeeded(appointment);
		
		FunctionsBean fb = FunctionsBean.instance();
		
		if(appointment == null){
			response.put("appointmentId", id);
			response.put("message", fb.getStaticTranslation("APPOINTMENT_NOT_FOUND_ERROR"));
			response.put("status", "error");
		}
		if(!action.equals(AppointmentAction.NULLIFY) && !action.equals(AppointmentAction.COMPLETE) && !action.equals(AppointmentAction.CONFIRM) && !action.equals(AppointmentAction.CONFIRM_AND_COMPLETE) && !action.equals(AppointmentAction.CANCEL) && !action.equals(AppointmentAction.CANCEL_AND_REACTIVATE) && !action.equals(AppointmentAction.ARRIVED)) {
			response.put("appointmentId", id);
			response.put("message", fb.getStaticTranslation("STATUS_CODE_NOT_VALID_ERROR"));
			response.put("status", "error");
		}
		try {
			if (action.equals(AppointmentAction.ARRIVED)){
				CodeValue patientEncounterCode = null;
				String codeAgenda = appointment.getServiceDeliveryLocation().getParent().getCode().getCode();
				if (codeAgenda.equals("WARD")){
					patientEncounterCode = VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC","EncounterKind","DH");
				} else {
					patientEncounterCode = VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC","EncounterKind","AMB");
				}
				if(actionAppointment.changeStatusCodeCascade(appointment, action,patientEncounterCode)){
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("CHANGE_STATUS_OK_MESSAGE"));
					response.put("status", "ok");
				}
				else{
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("STATE_MACHINE_NOT_VALID_ERROR"));
					response.put("status", "error");
				}
			}
			else if (action.equals(AppointmentAction.CANCEL)){
				CodeValue reasonCancCode = (CodeValue)ca.get(CodeValuePhi.class, reasonCancCodeId);
				if(reasonCancCode == null){
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("CANCELLATION_REASON_CODE_NOT_VALID_ERROR"));
					response.put("status", "error");
				}
								
				String cancelAppointment;
				if((cancelAppointment = actionAppointment.cancelAppointment(appointment, reasonCancCode, cancellationNote, false)) != null){
					//CREATE RESPONSE NEGATIVE

					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("APPOINTMENT_HANDLED_ERROR") + " " + cancelAppointment);
					response.put("status", "error");
				} else {
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("CHANGE_STATUS_OK_MESSAGE"));
					response.put("status", "ok");
				}
			} else if (action.equals(AppointmentAction.CANCEL_AND_REACTIVATE)) {
				CodeValue reasonCancCode = (CodeValue)ca.get(CodeValuePhi.class, reasonCancCodeId);
				if(reasonCancCode == null){
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("CANCELLATION_REASON_CODE_NOT_VALID_ERROR"));
					response.put("status", "error");
				}
				
				for (Appointment appointmentToCancel : appointment.getAppointmentGrouper().getAppointment()) {
					if (appointmentToCancel.getStatusCode().getCode().equals("planned")) {
													
						String cancelAppointment;
						if((cancelAppointment = actionAppointment.cancelAppointment(appointmentToCancel, reasonCancCode, cancellationNote, true)) != null){
							//CREATE RESPONSE NEGATIVE
	
							response.put("appointmentId", id);
							response.put("message", fb.getStaticTranslation("APPOINTMENT_HANDLED_ERROR") + " " + cancelAppointment);
							response.put("status", "error");
							break;
						} else {
							response.put("appointmentId", id);
							response.put("message", fb.getStaticTranslation("CHANGE_STATUS_OK_MESSAGE"));
							response.put("status", "ok");
							ca.create(appointmentToCancel);
							
							Events.instance().raiseEvent(PhiEvent.CREATE, appointmentToCancel);							
						}
					}
				}
								
			} else {
				//SET DEFAULT PATIENT ENCOUNTER KIND VUALE = AMB
				CodeValue patientEncounterCode = null;
				if(encounterCodeId != null){
					patientEncounterCode  = (CodeValue)ca.get(CodeValuePhi.class, encounterCodeId);
				}
				else
					patientEncounterCode = VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC","EncounterKind","AMB");
				if(actionAppointment.changeStatusCodeCascade(appointment, action,patientEncounterCode)){
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("CHANGE_STATUS_OK_MESSAGE"));
					response.put("status", "ok");
				}
				else{
					response.put("appointmentId", id);
					response.put("message", fb.getStaticTranslation("STATE_MACHINE_NOT_VALID_ERROR"));
					response.put("status", "error");
				}
			}
			
			if((response.get("appointmentId")!= null) && ((response.get("status").equals("ok")))){
				ca.create(appointment);
				
				Events.instance().raiseEvent(PhiEvent.CREATE, appointment);
				
				ca.flushSession();
			} 

			String json = mapper.writeValueAsString(response);

			log.info("[cid="+Conversation.instance().getId()+"] Changed status to appointment, id: " + id + ", action: " + action);
			
			return Response.ok(json).build();
			
		} catch (PhiException e) {
			log.error("Error completing appointment", e);			
			return Response.ok("Error change appointment status, appointmentId: " + id + ", reasonCodeId: " + reasonCancCodeId + ", cancellationNote: " + cancellationNote).build();
		} catch (Exception e) {
			log.error("Error completing appointment", e);			
			return Response.ok("Error change appointment status, appointmentId: " + id + ", reasonCodeId: " + reasonCancCodeId + ", cancellationNote: " + cancellationNote).build();
		}
	}
	
	/**
	 * Used by SSK Dashboard to get Appointments By Procedure
	 * @param id key di code_value per la prestazione singola
	 * @return
	 * @throws PersistenceException
	 */
	
	@GET
	@Path("/byprocedure/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAppointmentsByProcedure(@PathParam("id") String id, @QueryParam("date") String dateStr) throws PersistenceException {
		try {
			
			String currentLang = Locale.instance().getLanguage();
			
			String sqlAmbulatory = sqlAppointmentByProcedure;
			
			if (!currentLang.equals("it")) {
				sqlAmbulatory = sqlAmbulatory.replace("lang_it", "lang_" + currentLang);
			}
			
			SQLQuery qry = ca.createHibernateNativeQuery(sqlAmbulatory);

			qry.setParameter("id", id);
			//from date 00:00
			Calendar cal = Calendar.getInstance();
			cal.setTime(decodeISO8601(dateStr));
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			qry.setParameter("startDate", cal.getTime());
			//to date 24:00 
			cal.set(Calendar.DAY_OF_YEAR,cal.get(Calendar.DAY_OF_YEAR)+1);
			qry.setParameter("endDate", cal.getTime());			
//			qry.setParameter("dateMin", startDate);
//			qry.setParameter("dateMax", endDate);
			
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			List<?> results = qry.list();

			String json = mapper.writeValueAsString(results);
			

			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("Error getting appointment by procedure, with code id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting appointment by procedure, with code id: " + id).build();
		}
	}
	
	/**
	 * Used by SSK Dashboard to get Ambulatory By Procedure
	 * @param id
	 * @return
	 * @throws PersistenceException
	 */
	
	@GET
	@Path("/byambulatory/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAmbulatoryByProcedure(@PathParam("id") String id,@QueryParam("queryToUse") String queryToUse,@QueryParam("date") String dateStr) throws PersistenceException {
		try {
			
			String currentLang = Locale.instance().getLanguage();
			String sqlAmbulatory ;
			
			if("sqlAmbulatoryByStep".equals(queryToUse)){
				sqlAmbulatory = sqlAmbulatoryByStep;
			}
			else if("sqlAmbulatoryBySequence".equals(queryToUse)){
				sqlAmbulatory = sqlAmbulatoryBySequence;
			}
			else if("sqlAppointmentByAmbulatory".equals(queryToUse)){
				sqlAmbulatory=sqlAppointmentByAmbulatory;
			}
			else{
				sqlAmbulatory = sqlAmbulatoryByProcedure;
				
			}
			if (!currentLang.equals("it")) {
				sqlAmbulatory = sqlAmbulatory.replace("lang_it", "lang_" + currentLang);
			}
			
			SQLQuery qry = ca.createHibernateNativeQuery(sqlAmbulatory);
			
			Matcher m=pattern.matcher(id);
			if(m.matches()){
				qry.setParameter("id", id);
			}
			else{
				qry.setParameterList("id", id.split(","));
			}
			if (dateStr!=null){
				//from date 00:00
				Calendar cal = Calendar.getInstance();
				cal.setTime(decodeISO8601(dateStr));
				cal.set(Calendar.HOUR_OF_DAY,0);
				cal.set(Calendar.MINUTE,0);
				cal.set(Calendar.SECOND,0);
				cal.set(Calendar.MILLISECOND,0);
				qry.setParameter("startDate", cal.getTime());
				//to date 24:00 
				cal.set(Calendar.DAY_OF_YEAR,cal.get(Calendar.DAY_OF_YEAR)+1);
				qry.setParameter("endDate", cal.getTime());	
			}
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			List<?> results = qry.list();

			String json = mapper.writeValueAsString(results);
			
			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("Error getting ambulatory by procedure, with code id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting ambulatory by procedure, with code id: " + id).build();
		}
	}	
	
	private static final String REPETITION_DAILY = "daily";
	private static final String REPETITION_WORKING_DAYS = "workingDays";
	private static final String REPETITION_WEEKLY = "weekly";
	private static final String REPETITION_MONTHLY = "monthly";
	
	@POST
	@Path("/createRepeated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@FormParam("entity") String jSonEntity, @FormParam("frequency") String frequency, @FormParam("endDate") String jSonEndDate) {
		try {
			Appointment entity = mapper.readValue(jSonEntity, entityClass);	
			
			//calculate list of dates
			Date rootDate = entity.getDefaultDate();
			Date endDate = decodeISO8601(jSonEndDate);
			endDate = addDays(endDate,1);
			
			Calendar cal = Calendar.getInstance();
			List<Date> dateList = new ArrayList<Date>();
			Date newDate = rootDate;
			while(newDate.before(endDate)) {
				dateList.add(newDate);
				if (frequency.equals(REPETITION_DAILY)){
					newDate = addDays(newDate,1);
				} else if (frequency.equals(REPETITION_WORKING_DAYS)) {
					newDate = addDays(newDate,1);
			        cal.setTime(newDate);
					if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
						newDate = addDays(newDate,2);
					} else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
						newDate = addDays(newDate,1);
					}
				} else if (frequency.equals(REPETITION_WEEKLY)) {
					newDate = addDays(newDate,7);
				} else if (frequency.equals(REPETITION_MONTHLY)) {
					newDate = addMonths(newDate,1);
				}
			}
			//create appointment Grouper
			AppointmentGrouper rootAppGrouper = new AppointmentGrouper();
			ca.create(rootAppGrouper);
			ca.flushSession();
			
			//link appointment Grouper in rootAppointment
			entity.setAppointmentGrouper(rootAppGrouper);
			
			Iterator<Date> dateItr = dateList.iterator();  
			List<Long> internalIds = new ArrayList<Long>();
			
			Appointment newAppointment;
			
			while(dateItr.hasNext()) {
				newAppointment = createAppointmentInstance(entity);
				newAppointment.setDefaultDate(dateItr.next());
				ca.create(newAppointment);
				internalIds.add(newAppointment.getInternalId());
			}

			ca.flushSession();

			String jsonId = mapper.writeValueAsString(internalIds);
			
			log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + entity.getInternalId());
			return Response.ok(jsonId).build(); //FIXME change to created
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
	
	/**
	 * Used by Ambulatory Portal to copy Appointment
	 * 
	 * @param id Appointment.internalId
	 * @return
	 * @throws PersistenceException
	 */

	@GET
	@Path("/copy")
	@Produces(MediaType.APPLICATION_JSON)
	public Response copyAppointment(@QueryParam("appointmentId") long appointmentId, @QueryParam("date") String date, @QueryParam("agendaId")  long agendaId) {
		try {
			UserBean ub = UserBean.instance();
			Appointment oldAppointment = (Appointment)ca.get(entityClass, appointmentId);
			ServiceDeliveryLocation sdl = (ServiceDeliveryLocation)ca.get(ServiceDeliveryLocation.class, agendaId);
			
			Appointment newAppointment = new Appointment();
			//newAppointment.setText(oldAppointment.getText());
			newAppointment.setColor(oldAppointment.getColor());
			newAppointment.setCreatedBy(ub.getUsername());
			newAppointment.setAuthor(ub.getCurrentEmployee());
			newAppointment.setDefaultDate(decodeISO8601(date));

			newAppointment.setPatient(oldAppointment.getPatient());
			newAppointment.setCreationDate(new Date());
			newAppointment.setDuration(oldAppointment.getDuration());
			
			newAppointment.setServiceDeliveryLocation(sdl);
			newAppointment.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "AppointmentStatus", "planned"));
			
			AppointmentGrouper ag = oldAppointment.getAppointmentGrouper();
			
			if (ag!=null) {
				if(ag instanceof HibernateProxy)
					ag = (AppointmentGrouper)((HibernateProxy)ag).getHibernateLazyInitializer().getImplementation();
			
				newAppointment.setAppointmentGrouper(ag);
				ca.refresh(ag);
			}
			
			//Main procedure management
			Procedure procedure = new Procedure();
			procedure.setLevelCode(oldAppointment.getProcedure().getLevelCode());
			procedure.setClassCode(oldAppointment.getProcedure().getClassCode());
			procedure.setCode(oldAppointment.getProcedure().getCode());
			procedure.setCreationDate(new Date());
			procedure.setIsActive(true);
			procedure.setVersion(0);
			procedure.setMainProcedure(false);
			newAppointment.setProcedure(procedure);
			
			newAppointment.setPerformedProcedure(new ArrayList<Procedure>());
			 
			//Performed procedures management
			List<Procedure> oldProcList = oldAppointment.getPerformedProcedure();
			
			if(oldProcList != null && oldProcList.size()>0){
				
				if (oldProcList.size()==1){
					String stringToAdd = oldAppointment.getStepMarker();
					if (stringToAdd!=null && stringToAdd!="" && stringToAdd.contains("di")){
						stringToAdd = stringToAdd.substring(stringToAdd.indexOf("di")-1, stringToAdd.length());
					}
					
					String stepMarker = this.evaluateStepMarker(oldAppointment.getAppointmentGrouper().getInternalId(), oldProcList.get(0).getCodeIcd9().getId());
					newAppointment.setStepMarker(stepMarker + stringToAdd);
				}
					
				Iterator<Procedure> itr= oldProcList.iterator();
				while(itr.hasNext()) {
					Procedure newProcedure= new Procedure();
					Procedure oldProcedure = itr.next();
					
					newProcedure.setClassCode(oldProcedure.getClassCode());
					newProcedure.setText(oldProcedure.getText());
					newProcedure.setCodeIcd9(oldProcedure.getCodeIcd9());
					newProcedure.setRegionalCodeIcd9(oldProcedure.getRegionalCodeIcd9());
					newProcedure.setCharge(oldProcedure.getCharge());
					newProcedure.setQuantity(oldProcedure.getQuantity());
					newProcedure.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));

					newProcedure.setDescription(oldProcedure.getDescription());
					newAppointment.getPerformedProcedure().add(newProcedure);
				}
			}
					
			//Procedure request management
			List<ProcedureRequest> oldProcRequestList = oldAppointment.getProcedureRequest();
			
			if(oldProcRequestList != null && oldProcRequestList.size()>0){
				
				List<ProcedureRequest> newProcRequestList = new ArrayList<ProcedureRequest>();
				Iterator<ProcedureRequest> itrPR = oldProcRequestList.iterator();
				
				while(itrPR.hasNext()) {
					ProcedureRequest newProcedureRequest = new ProcedureRequest();
					ProcedureRequest oldProcedureRequest = itrPR.next();
					//copy ProcedureRequest attributes
					newProcedureRequest.setRequestNumber(oldProcedureRequest.getRequestNumber());
					newProcedureRequest.setExemption(oldProcedureRequest.getExemption());
					newProcedureRequest.setCodeExemption(oldProcedureRequest.getCodeExemption());
					newProcedureRequest.setPriority(oldProcedureRequest.getPriority());
					newProcedureRequest.setUrgency(oldProcedureRequest.getUrgency());
					newProcedureRequest.setSsnCode(oldProcedureRequest.getSsnCode());
					
					oldProcList = oldProcedureRequest.getProcedure();
					
					if(oldProcList != null && oldProcList.size()>0){
												
						Iterator<Procedure> itr = oldProcList.iterator();
						while(itr.hasNext()) {
							Procedure newProcedure = new Procedure();
							Procedure oldProcedure = itr.next();
							//copy Procedure attributes
							newProcedure.setClassCode(oldProcedure.getClassCode());
							newProcedure.setText(oldProcedure.getText());
							newProcedure.setCodeIcd9(oldProcedure.getCodeIcd9());
							newProcedure.setRegionalCodeIcd9(oldProcedure.getRegionalCodeIcd9());
							newProcedure.setCharge(oldProcedure.getCharge());
							newProcedure.setQuantity(oldProcedure.getQuantity());
							newProcedure.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));
		
							newProcedure.setDescription(oldProcedure.getDescription());
							newProcedureRequest.addProcedure(newProcedure);
						}
					}
					newProcedureRequest.setAppointment(newAppointment);
					newProcRequestList.add(newProcedureRequest);
				}
				newAppointment.setProcedureRequest(newProcRequestList);
			}
			
			newAppointment.setInsertCompleted(true);
			ca.create(newAppointment);
			ca.flushSession();
			
			String json = mapper.writeValueAsString(newAppointment.getInternalId());
			
			Events.instance().raiseEvent(PhiEvent.CREATE, newAppointment);
			
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error copying appointment, appointmentId: " + appointmentId, e);
			return Response.ok("Error copying appointment, appointmentId: " + appointmentId).build();
		}
	}
	
	@GET
	@Path("/counter/{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProcedureCounter(@PathParam("id") long id) throws PersistenceException {
		try {
			String sqlProcedureCounter = this.sqlProcedureCounter;
			SQLQuery qry = ca.createHibernateNativeQuery(sqlProcedureCounter);

			qry.setParameter("appointmentId", id);

			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			List<?> results = qry.list();

			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error getting appointment details, appointmentId :" + id, e);
			//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting appointment details").build();
			return Response.ok("Error getting appointment details, appointmentId :" + id).build();
		}
	}
	
	protected static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	protected static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months); //minus number would decrement the days
        return cal.getTime();
    }
	
	protected static Appointment createAppointmentInstance(Appointment oldAppointment) {
		Appointment newAppointment = new Appointment();
		newAppointment.setColor(oldAppointment.getColor());
		newAppointment.setCreatedBy(oldAppointment.getCreatedBy());
		newAppointment.setCreationDate(oldAppointment.getCreationDate());
		newAppointment.setDuration(oldAppointment.getDuration());
		newAppointment.setServiceDeliveryLocation(oldAppointment.getServiceDeliveryLocation());
		newAppointment.setAppointmentGrouper(oldAppointment.getAppointmentGrouper());
		newAppointment.setStatusCode(oldAppointment.getStatusCode());
		newAppointment.setText(oldAppointment.getText());
		return newAppointment;
	}
	
	public String evaluateStepMarker(Long appGrpInternalId, String oldProcedureCodeIcd9Id)  throws PersistenceException {
		String sql = "SELECT * FROM procedure_db p" +
				" WHERE p.code_icd9     ='" + oldProcedureCodeIcd9Id +"'" +
				" AND p.appointment_id IN (" +
				" SELECT internal_id" +
				" FROM appointment " +
				" WHERE appointmentgrouper_id = '" + appGrpInternalId + "'" +
				" AND is_active = '1'" +
				" AND status_code NOT IN ('2.16.840.1.113883.3.20.04.05.06_V0','2.16.840.1.113883.3.20.04.05.07_V0'))";
		
		Query qry = ca.createNativeQuery(sql);
		List<Object[]> list = qry.getResultList();
		
		if (list != null && list.size()>0) {
			Integer ret = (list.size() + 1);
			return ret.toString();
			
		}
		
		return "1";
	}
}