package com.phi.rest.dashboard;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.hibernate.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.TransferPrivacy;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.events.PhiEvent;
import com.phi.rest.action.ErogationStatusFixer;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;


/**
 * DrugAdministrator Dashboard rest methods init and refresh
 * 
 * Example:
 * http://localhost:8080/PHI_CI/resource/rest/nurseactivities/init/encounterId=18;therapyId=35/1
 * 
 * Da implementare: union x paginazione ok
 * ordinamento x paziente, data e paginazione
 * molteplicita di encounerId e therapy id x vista reparto: verra passato un sdlId: OK
 * se encounterId == null -> non fare query su attivit�: ok
 * 
 * verificare come implementare: filtri su stato dinamico (Da erogare e Mancate)
 * 
 * 
 * @author alex.zupan
 */
@Restrict("#{identity.isLoggedIn(false)}")
@Name("NurseActivityRest")
@Path("/nurseactivities")
public class NurseActivityRest extends BaseDashboardRest {

	private static final long serialVersionUID = -3923196164942169452L;
	/*
	// Disabled future (now < planned - 60m)
	public static final String DUE_FUTURE_DISABLED 	= "DUE_FUTURE_DISABLED";
	// Enabled early (planned - 60m < now < planned - 30m) 
	public static final String DUE_FUTURE_ENABLED 	= "DUE_FUTURE_ENABLED";
	// Enabled right (planned - 30m < now < planned + 30m)
	public static final String DUE 					= "DUE";
	// Enabled delay (planned + 30m < now < planned + 60m)
	public static final String OVERDUE 				= "OVERDUE";
	// Disabled passed (now > planned + 60m) (super)
	public static final String MISSED = "MISSED";

	// Given in range (planned - 60m / planned - 30m)
	public static final String EROGATED_EARLY 			= "EROGATED_EARLY";
	// Given in range (planned - 30m / planned + 30m)		
	public static final String EROGATED_AS_PLANNED 		= "EROGATED_AS_PLANNED";
	// Given in range (planned + 30m / planned + 60m)
	public static final String EROGATED_LATE 			= "EROGATED_LATE";
	// Given with exception (super)
	public static final String EXCEPTION 						= "EXCEPTION";
	public static final String EXCEPTION_WRONG_DRUG 			= "EXCEPTION_WRONG_DRUG";
	public static final String EXCEPTION_WRONG_DOSE 			= "EXCEPTION_WRONG_DOSE";
	public static final String EXCEPTION_OTHER_REASON			= "EXCEPTION_OTHER_REASON";			
	// Not Given (super)
	public static final String UNSUCCESSFUL 					= "UNSUCCESSFUL";		
	public static final String UNSUCCESSFUL_PATIENT_REFUSED 	= "UNSUCCESSFUL_PATIENT_REFUSED";
	public static final String UNSUCCESSFUL_PATIENT_ABSENT 		= "UNSUCCESSFUL_PATIENT_ABSENT";
	public static final String UNSUCCESSFUL_DRUG_UNAVAILABLE 	= "UNSUCCESSFUL_DRUG_UNAVAILABLE";
	public static final String UNSUCCESSFUL_NIL_BY_MOUTH 		= "UNSUCCESSFUL_NIL_BY_MOUTH";
	public static final String UNSUCCESSFUL_OTHER_REASON		= "UNSUCCESSFUL_OTHER_REASON";

	// Planned (super)
	public static final String PLANNED = "PLANNED";		
	// Cancelled (super)
	public static final String CANCELLED = "CANCELLED";
	// Done (super)
	public static final String DONE = "DONE";
	*/		
	
	public void setReadPageSize(int readPageSize) {
		this.readPageSize = readPageSize;
	}
	
	/*
	** QUERIES for dashboard (patient view and ward view), r_erogationPatient and r_erogationWard
	*/
	
	private static final String EXECUTION_SELECT_SQL = 
			" SELECT" +
			" 'E' as type," +
			" execution.internal_id as internalId," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" execution.planned_date as plannedDate," +
			" execution.planned_time as plannedTime," +
			" execution.executionDate_time_low as erogationDate," +
			" NULL as erogationStopDate," +
			" execution.execution_time as executionTime," +
			" author.internal_id as author," +
			" CASE" +
			"    WHEN execution.cancellation_date IS NOT NULL THEN UPPER(cancellationAuthor.name_fam) || ' ' || cancellationAuthor.name_giv || ' [' || cancellationAuthorCode.lang_it || ' (' || cancellationAuthorCode.code || ')] - ' || TO_CHAR(execution.cancellation_date, 'DD/MM/YYYY hh24:MI') || ' - ' || execution.cancellation_note" +
			" END as cancellationDetails," +
			" execution.cancellation_date as cancellationDate," +
			" execution.cancellation_note as cancellationNote," +
			" activity.internal_id as parent_internalId," +
			" activity.creation_date as parent_creationDate," +
			" activityCode.code as parent_code," +
			" responsibleRole.code as parent_responsibleRole," +
			" activity.effective_Date_high as endDate," +
			" CASE" +
			"     WHEN (objective.internal_id IS NULL) AND (diagnosis.internal_id IS NULL) THEN activityCode.lang_it" +
			"     WHEN (objective.internal_id IS NOT NULL) AND (diagnosisCode.lang_it IS NOT NULL) THEN activityCode.lang_it || '\n' || objective.text_string||' ('||diagnosis.prog_number||' - '||diagnosisCode.lang_it||')'" +
			"     WHEN (objective.internal_id IS NOT NULL) AND (diagnosisCode.lang_it IS NULL) THEN activityCode.lang_it || '\n' || objective.text_string||' ('||diagnosis.prog_number||' - '||diagnosis.title_diag||')'" +
			"     WHEN (objective.internal_id IS NULL) AND (diagnosisCode.lang_it IS NOT NULL) THEN activityCode.lang_it||'\n'||diagnosis.prog_number||' - '||diagnosisCode.lang_it" +
			"     WHEN (objective.internal_id IS NULL) AND (diagnosisCode.lang_it IS NULL) THEN activityCode.lang_it||'\n'||diagnosis.prog_number||' - '||diagnosis.title_diag" +
			" END as description," +
			" CASE" +
			"	  WHEN (activity.extemporaneous = 0) THEN activity.note" +
			"     ELSE NULL" +
			" END as note," +
			" upper(patient.name_fam) ||' '|| patient.name_giv as patient," +
			" encounter.internal_id as encounter_internalid," +
			" encounter.room_string as encounter_room," +
			" LENGTH(encounter.room_string) as encounter_roomLength," +
			" encounter.bed_string as encounter_bed," +
			" LENGTH(encounter.bed_string) as encounter_bedLength," +
			" objective.internal_id as objectiveid," +
			" NULL as route," + //Campi fittizzi necessari x union
			" NULL as quantity," +
			" 0 as continuous";
	
	private static final String EXECUTION_SELECT_REPORT_SQL = 
			"," +
			" CASE" +
			"	WHEN (execution.executionDate_time_low IS NOT NULL) AND (execution.note IS NOT NULL) THEN UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(execution.executionDate_time_low, 'DD/MM/YYYY hh24:MI') || ' - ' || execution.note" +
			"	WHEN (execution.executionDate_time_low IS NOT NULL) AND (execution.note IS NULL) THEN UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(execution.executionDate_time_low, 'DD/MM/YYYY hh24:MI')" +
			" END as details," +
			" CASE" +
			" 	WHEN (statusCode.code = 'DONE') AND (objective.internal_id IS NOT NULL) AND (objective.cancellation_date IS NOT NULL) THEN '(' || objectiveReached.lang_it || ' label_and_closed_on ' || TO_CHAR(objective.cancellation_date, 'DD/MM/YYYY hh24:MI') || ')'" +
			"	WHEN (statusCode.code = 'DONE') AND (objective.internal_id IS NOT NULL) AND (objective.cancellation_date IS NULL) THEN '(' || objectiveReached.lang_it || ')'" +
			" END as objectiveDetails";			
	
	private static final String EXECUTION_JOIN_SQL =
			" FROM lep_execution execution" +
			" LEFT JOIN code_value statusCode on execution.status_code=statusCode.id" +
			" LEFT JOIN code_value statusDetailsCode on execution.status_details_code=statusDetailsCode.id" +
			" LEFT JOIN employee author on execution.author_id=author.internal_id" +
			" LEFT JOIN employee cancellationAuthor on execution.cancelled_by_id=cancellationAuthor.internal_id" +
			" LEFT JOIN code_value_role cancellationAuthorCode on execution.cancelledByRole=cancellationAuthorCode.id" +
			" LEFT JOIN lep_activity activity on execution.lepact_id=activity.internal_id" +
			" LEFT JOIN code_value_nanda activityCode on activity.nandaLep=activityCode.id" +
			" LEFT JOIN code_value_role responsibleRole on activity.responsible_role=responsibleRole.id" +
			" LEFT JOIN nanda diagnosis on activity.nanda_id=diagnosis.internal_id" +
			" LEFT JOIN code_value_nanda diagnosisCode on diagnosis.nandaDiag=diagnosisCode.id" +
			" LEFT JOIN objective_nanda objective on activity.objective_id=objective.internal_id" +
			" LEFT JOIN code_value_nanda objectiveCode on objective.objLep=objectiveCode.id" +
			" LEFT JOIN patient_encounter encounter on activity.patenc=encounter.internal_id" +
			" LEFT JOIN patient patient on encounter.patient_id=patient.internal_id" +
			" LEFT JOIN code_value_role supportRole on activity.support_role=supportRole.id";
	
	private static final String EXECUTION_JOIN_REPORT_SQL =			
			" LEFT JOIN code_value_role authorCode on execution.authorRole=authorCode.id" +
			" LEFT JOIN code_value objectiveReached on objective.obj_reached=objectiveReached.id";
	
	private static final String EXECUTION_WHERE_SQL =
			" WHERE encounter.internal_id IN (:encounterId)" +
			" AND statusCode.code <> 'CANCELLED'";	
	
	private static final String ADMINISTRATION_SELECT_SQL = 
			" SELECT" +
			" 'S' as type," +
			" subAdmin.internal_id as internalId," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" subAdmin.planned_date as plannedDate," +
			" NULL as plannedtime," +
			" subAdmin.administratedDate_time_low as erogationDate," +
			" subAdmin.administratedDate_time_high as erogationStopDate," +
			" NULL as executiontime," +
			" author.internal_id as author," +
			" CASE" +
			" 	WHEN subAdmin.cancellation_date IS NOT NULL THEN UPPER(cancellationAuthor.name_fam) || ' ' || cancellationAuthor.name_giv || ' [' || cancellationAuthorCode.lang_it || ' (' || cancellationAuthorCode.code || ')] - ' || TO_CHAR(subAdmin.cancellation_date, 'DD/MM/YYYY hh24:MI') || ' - ' || subAdmin.cancellation_note" +
			" END as cancellationDetails," +
			" subAdmin.cancellation_date as cancellationDate," +
			" subAdmin.cancellation_note as cancellationNote," +
			" prescription.internal_id as parent_internalId," +
			" prescription.creation_date as parent_creationDate," +
			" prescriptionCode.code as parent_code," +
			" NULL as parent_responsiblerole," +
			" CASE" +
			"   WHEN prescription.validity_period_time_high IS NOT NULL THEN prescription.validity_period_time_high" +
			"   ELSE prescription.period_time_high" +
			" END as endDate," +
			/*
			" CASE" +
			"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND prescriptionInfusionTypeCode.lang_it IS NULL) THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND prescriptionInfusionTypeCode.lang_it IS NOT NULL) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			" 	ELSE prescriptionInfusionTypeCode.lang_it" +
			" END as description," +
			*/
			" CASE" +
			"	WHEN prescriptionCode.code='PHARMA' THEN LISTAGG (medicine.name_giv, '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
	  		"  	WHEN (prescriptionCode.code ='INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND (prescriptionInfusionTypeCode.lang_it IS NULL)) THEN LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
	  		"  	WHEN (prescriptionCode.code ='INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND (prescriptionInfusionTypeCode.lang_it IS NOT NULL)) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"  	ELSE prescriptionInfusionTypeCode.lang_it" +
			" END as description," +
			" prescription.note as note," +
			" UPPER(patient.name_fam) || ' ' ||  patient.name_giv as patient," + 
			" encounter.internal_id as encounter_internalid," +
			" encounter.room_string as encounter_room," +
			" LENGTH(encounter.room_string) as encounter_roomLength," +
			" encounter.bed_string as encounter_bed," +
			" LENGTH(encounter.bed_string) as encounter_bedLength," +
			" NULL as objectiveid," + //Campo fittizzio necessario x union
			" prescriptionRouteCode.lang_it as route," +
			" CASE " +
			"  	WHEN prescriptionCode.code='PHARMA' THEN subAdmin.planned_quantity_value || ' [' || subAdmin.planned_quantity_unit  || ']'" +
			"  	WHEN prescriptionCode.code='INFU' THEN prescription.quantity_value || ' [' || prescription.quantity_unit || '] label_at ' || prescription.infusion_speed_value || ' [' || prescription.infusion_speed_unit  || ']'" +
			" END as quantity," +
			" prescription.continuous as continuous";
	
	private static final String ADMINISTRATION_SELECT_REPORT_SQL = 
			"," +
			" CASE" +
			" 	WHEN (subAdmin.administratedDate_time_low IS NOT NULL) AND (subAdmin.text_string IS NOT NULL) THEN UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(subAdmin.administratedDate_time_low, 'DD/MM/YYYY hh24:MI') || ' - ' || subAdmin.text_string" +
			" 	WHEN (subAdmin.administratedDate_time_low IS NOT NULL) AND (subAdmin.text_string IS NULL) THEN UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(subAdmin.administratedDate_time_low, 'DD/MM/YYYY hh24:MI')" +
			" END as details," +
			" NULL as objectiveDetails";
			
	private static final String ADMINISTRATION_JOIN_SQL = 
			" FROM substanceadmin subAdmin" +
			" LEFT JOIN code_value statusCode on subAdmin.status_code=statusCode.id" +
			" LEFT JOIN code_value statusDetailsCode on subAdmin.status_details_code=statusDetailsCode.id" +
			" LEFT JOIN employee author on subAdmin.author_id=author.internal_id" +
			" LEFT JOIN employee cancellationAuthor on subAdmin.cancelled_by_id=cancellationAuthor.internal_id" +
			" LEFT JOIN code_value_role cancellationAuthorCode on subAdmin.cancelledByRole=cancellationAuthorCode.id" +
			" LEFT JOIN prescription prescription on subAdmin.prescription=prescription.internal_id" +
			" LEFT JOIN code_value prescriptionCode on prescription.code=prescriptionCode.id" +
			" LEFT JOIN code_value prescriptionRouteCode on prescription.route_code=prescriptionRouteCode.id" +
			" LEFT JOIN prescription_medicine prescriptionMedicine on prescription.internal_id=prescriptionMedicine.prescription_id" +
			" LEFT JOIN medicine medicine on prescriptionMedicine.medicine_id=medicine.internal_id" +
			" LEFT JOIN code_value prescriptionInfusionTypeCode on prescription.infusion_Type_Code=prescriptionInfusionTypeCode.id" +
		 	" LEFT JOIN therapy therapy on prescription.therapy=therapy.internal_id" +
			" LEFT JOIN patient_encounter encounter on therapy.patient_encounter=encounter.internal_id" +
			" LEFT JOIN patient patient on encounter.patient_id=patient.internal_id" +
			" LEFT JOIN code_value_codifa eqGroup ON medicine.eq_group=eqGroup.id";
	
	private static final String ADMINISTRATION_JOIN_REPORT_SQL =			
			" LEFT JOIN code_value_role authorCode on subAdmin.authorRole=authorCode.id";			
		
	private static final String ADMINISTRATION_WHERE_SQL = 
			" WHERE therapy.internal_id in (:therapyId)" +
			" AND statusCode.code <> 'CANCELLED'" +
			" AND prescription.is_active = 1";
	
	private static final String ADMINISTRATION_GROUPBY_SQL = 
			" GROUP BY" +
			" subAdmin.internal_id," +
			" statusCode.code," +
			" statusDetailsCode.code," +
			" subAdmin.planned_date," +
			" subAdmin.planned_quantity_value," +
			" subAdmin.planned_quantity_unit," +
			" subAdmin.administratedDate_time_low," +
			" subAdmin.administratedDate_time_high," +
			" author.internal_id," +
			" cancellationAuthor.name_fam," +
			" cancellationAuthor.name_giv," +
			" cancellationAuthorCode.lang_it," +
			" cancellationAuthorCode.code," +
			" subAdmin.cancellation_date," +
			" subAdmin.cancellation_note," +
			" prescription.internal_id," +
			" prescription.creation_date," +
			" prescriptionCode.code," +
			" prescription.validity_period_time_high," +
			" prescription.period_time_high," +
			" prescription.note," +
			" prescriptionRouteCode.lang_it," +
			" prescriptionInfusionTypeCode.lang_it," +
			" prescription.quantity_value," +
			" prescription.quantity_unit," +
			" prescription.infusion_speed_value," +
			" prescription.infusion_speed_unit," +
			" patient.name_fam," +
			" patient.name_giv," +
			" encounter.internal_id," +
			" encounter.room_string," +
			" encounter.bed_string," +
			" prescription.continuous," +
			" prescription.needsbased";
	
	private static final String ADMINISTRATION_GROUPBY_REPORT_SQL =
			"," +
			" author.name_fam," +
			" author.name_giv," +
			" authorCode.lang_it," +
			" authorCode.code," +
			" subAdmin.text_string";
	
	
	/*
	** QUERIES for r_erogationCalendar
	*/
	
	private static final String CALENDAR_REPORT_ACTIVITY_SELECT_SQL =
			" SELECT" +
			" 'A' as type," +
			" activity.internal_id as internalId," +
			" lepCode.lang_it as description," +
			" NULL as code," + // Campo fittizio per UNION
			" LISTAGG(dosage.day_interval || '@' || TO_CHAR(dosage.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY dosage.daytime) as posology," +
			" NULL as velocity," + // Campo fittizio per UNION
			" NULL as route," + // Campo fittizio per UNION
			" activity.time_spent as plannedTime," + 
			" CASE" +
			"   WHEN activity.effective_date_low > activity.validity_period_time_low OR activity.validity_period_time_low IS NULL THEN activity.effective_date_low" +
			"   ELSE activity.validity_period_time_low" +
			" END as startDate," +
			" CASE " +
			"   WHEN activity.effective_date_high < activity.validity_period_time_high OR activity.validity_period_time_high IS NULL THEN activity.effective_date_high" +
			"   ELSE activity.validity_period_time_high" +
			" END as endDate," +
			" SUBSTR(author.name_giv, 1, 1) || '. ' || author.name_fam || ' (' || authorCode.abbreviation_it || ')' as author," +
			" CASE" +
			" 	WHEN deleteAuthor.internal_id IS NOT NULL THEN SUBSTR(deleteAuthor.name_giv, 1, 1) || '. ' || deleteAuthor.name_fam || ' (' || deleteAuthorCode.abbreviation_it || ')'" +
			"   ELSE NULL" +
			" END as cancelledBy," +			
			" activity.note as note," +
			" NULL as extemporaneous," +
			" NULL as urgent," +
			" NULL as continuous," +
			" NULL as needsbased" + // Campo fittizio per UNION
			" FROM" +
			" lep_activity activity" +
			" LEFT JOIN employee author on activity.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode on activity.authorRole = authorCode.id" +
			" LEFT JOIN employee deleteAuthor on activity.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN code_value_role deleteAuthorCode on activity.cancelledByRole = deleteAuthorCode.id" +
			" LEFT JOIN code_value_nanda lepCode on activity.nandaLep = lepCode.id" +
			" LEFT JOIN dosage dosage on activity.internal_id = dosage.lep_activity" +
			" LEFT JOIN code_value_role responsibleRole on activity.responsible_role = responsibleRole.id" +
			" LEFT JOIN code_value_role supportRole on activity.support_role = supportRole.id" +
			" LEFT JOIN patient_encounter encounter on activity.patenc = encounter.internal_id" +
			" LEFT JOIN patient patient on encounter.patient_id = patient.internal_id" +
			" LEFT JOIN service_delivery_location sdloc on encounter.assignedsdl = sdloc.internal_id" +
			" WHERE encounter.internal_id = :encounterId";

	private static final String CALENDAR_REPORT_ACTIVITY_GROUPBY_SQL =
			" GROUP BY" +
			" activity.internal_id," +
			" lepCode.lang_it," +
			" dosage.day_interval," +
			" activity.time_spent," +
			" activity.effective_date_low," +
			" activity.effective_date_high," +
			" activity.validity_period_time_low," +
			" activity.validity_period_time_high," +
			" author.name_fam," +
			" author.name_giv," +
			" authorCode.abbreviation_it," +
			" deleteAuthor.internal_id," +
			" deleteAuthor.name_fam," +
			" deleteAuthor.name_giv," +
			" deleteAuthorCode.abbreviation_it," +		
			" activity.note";
	
	private static final String CALENDAR_REPORT_PRESCRIPTION_SELECT_SQL =
			" SELECT" +
			" 'P' as type," +
			" prescription.internal_id as internalId," +
			/*
			" CASE" +
			"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND prescriptionInfusionTypeCode.lang_it IS NULL) THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND prescriptionInfusionTypeCode.lang_it IS NOT NULL) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			" 	ELSE prescriptionInfusionTypeCode.lang_it" +
			" END as description," +
			*/
			" CASE" +
			"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG (medicine.name_giv, '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND prescriptionInfusionTypeCode.lang_it IS NULL) THEN LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND prescriptionInfusionTypeCode.lang_it IS NOT NULL) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			" 	ELSE prescriptionInfusionTypeCode.lang_it" +
			" END as description," +
			" prescriptionCode.code as code," +
			" LISTAGG (prescriptionMedicine.dosage, ',') WITHIN GROUP (ORDER BY prescriptionMedicine.dosage) as posology," +
			" CASE" +
			"   WHEN prescriptionCode.code = 'INFU' THEN prescription.quantity_value || '[' || prescription.quantity_unit || ']@' || prescription.infusion_speed_value || '[' || prescription.infusion_speed_unit  || ']'" +
			" END as velocity," +
			" prescriptionRouteCode.lang_it as route," +
			" NULL as plannedTime," + // Campo fittizio per UNION			
			" CASE" +
			"   WHEN prescription.period_time_low > prescription.validity_period_time_low OR prescription.validity_period_time_low IS NULL THEN prescription.period_time_low" +
			"   ELSE prescription.validity_period_time_low" +
			" END as startDate," +
			" CASE " +
			"   WHEN prescription.period_time_high < prescription.validity_period_time_high OR prescription.validity_period_time_high IS NULL THEN prescription.period_time_high" +
			"   ELSE prescription.validity_period_time_high" +
			" END as endDate," +
			" SUBSTR(author.name_giv, 1, 1) || '. ' || author.name_fam || ' (' || authorCode.abbreviation_it || ')' as author," +			
			" CASE" +
			" 	WHEN deleteAuthor.internal_id IS NOT NULL THEN SUBSTR(deleteAuthor.name_giv, 1, 1) || '. ' || deleteAuthor.name_fam || ' (' || deleteAuthorCode.abbreviation_it || ')'" +
			"   ELSE NULL" +
			" END as cancelledBy," +
			" prescription.note as note," +
			" prescription.extemporaneous as extemporaneous," +
			" prescription.urgent as urgent," +
			" prescription.continuous as continuous," +
			" prescription.needsbased as needsbased" +
			" FROM" +
			" prescription prescription" +
			" LEFT JOIN employee author ON prescription.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode ON prescription.authorRole = authorCode.id" +
			" LEFT JOIN employee deleteAuthor ON prescription.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN code_value_role deleteAuthorCode ON prescription.cancelledByRole = deleteAuthorCode.id" +
			" LEFT JOIN code_value prescriptionCode ON prescription.code = prescriptionCode.id" +
			" LEFT JOIN code_value prescriptionRouteCode ON prescription.route_code = prescriptionRouteCode.id" +
			" LEFT JOIN code_value prescriptionInfusionTypeCode ON prescription.infusion_Type_Code = prescriptionInfusionTypeCode.id" +
			" LEFT JOIN prescription_medicine prescriptionMedicine ON prescription.internal_id = prescriptionMedicine.prescription_id" +
			" LEFT JOIN medicine medicine ON prescriptionMedicine.medicine_id = medicine.internal_id" +
			" LEFT JOIN code_value_codifa eqGroup ON medicine.eq_group=eqGroup.id" +
			" WHERE prescription.therapy = :therapyId";
			
	private static final String CALENDAR_REPORT_PRESCRIPTION_GROUPBY_SQL =
			" GROUP BY" +
			" prescription.internal_id," +
			" prescriptionCode.code," +
			" medicine.name_giv," +
			" prescriptionMedicine.dosage," +
			" prescriptionInfusionTypeCode.lang_it," +
			" prescription.quantity_value," +
			" prescription.quantity_unit," +
			" prescription.infusion_speed_value," +
			" prescription.infusion_speed_unit," +
			" prescriptionRouteCode.lang_it," +
			" prescription.period_time_low," +
			" prescription.period_time_high," +
			" prescription.validity_period_time_low," +
			" prescription.validity_period_time_high," +
			" author.name_fam," +
			" author.name_giv," +
			" authorCode.abbreviation_it," +
			" deleteAuthor.internal_id," +
			" deleteAuthor.name_fam," +
			" deleteAuthor.name_giv," +
			" deleteAuthorCode.abbreviation_it," +
			" prescription.note," +
			" prescription.extemporaneous," +
			" prescription.urgent," +
			" prescription.continuous," +
			" prescription.needsbased";
	
	private static final String CALENDAR_REPORT_EXECUTION_SQL =
			" SELECT" +
			" 'E' as type," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" execution.planned_date as plannedDate," +
			" execution.execution_time as erogationTime," +
			" execution.executionDate_time_low as erogationDate," +
			" execution.cancellation_date as cancellationDate," +
			" author.internal_id as author," +
			" CASE" +
			"    WHEN author.internal_id IS NOT NULL THEN SUBSTR(author.name_giv, 1, 1) || '. ' || author.name_fam || ' (' || authorCode.abbreviation_it || ') - ' || TO_CHAR(execution.executionDate_time_low, 'DD/MM/YYYY hh24:MI') || ' - ' || execution.note" +
			" END as erogationDetails," +						
			" CASE" +
			"    WHEN execution.cancellation_date IS NOT NULL THEN SUBSTR(cancellationAuthor.name_giv, 1, 1) || '. ' || cancellationAuthor.name_fam || ' (' || cancellationAuthorCode.abbreviation_it || ') - ' || TO_CHAR(execution.cancellation_date, 'DD/MM/YYYY hh24:MI') || ' - ' || execution.cancellation_note" +
			" END as cancellationDetails," +			
			" activity.internal_id as parentId," +
			" activity.effective_date_high as endDate" +
			" FROM" +
			" lep_execution execution" +
			" LEFT JOIN code_value statusCode ON execution.status_code = statusCode.id" +
			" LEFT JOIN code_value statusDetailsCode ON execution.status_details_code = statusDetailsCode.id" +
			" LEFT JOIN employee author ON execution.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode ON execution.authorRole = authorCode.id" +
			" LEFT JOIN employee cancellationAuthor ON execution.cancelled_by_id = cancellationAuthor.internal_id" +
			" LEFT JOIN code_value_role cancellationAuthorCode ON execution.cancelledByRole = cancellationAuthorCode.id" +
			" LEFT JOIN lep_activity activity ON execution.lepact_id = activity.internal_id" +
			" WHERE activity.internal_id IN (:activityIds)" +
			" AND ((execution.planned_date IS NULL AND execution.executionDate_time_low >= :plannedDateFrom) OR execution.planned_date >= :plannedDateFrom)" +
			" AND ((execution.planned_date IS NULL AND execution.executionDate_time_low <= :plannedDateTo) OR execution.planned_date <= :plannedDateTo)"; 
		
	private static final String CALENDAR_REPORT_ADMINISTRATION_SQL =
			" SELECT" +
			" 'A' as type," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" administration.planned_date as plannedDate," +
			" null as erogationTime," +
			" administration.administratedDate_time_low as erogationDate," +
			" administration.cancellation_date as cancellationDate," +
			" author.internal_id as author," +
			" CASE" +
			"    WHEN author.internal_id IS NOT NULL THEN SUBSTR(author.name_giv, 1, 1) || '. ' || author.name_fam || ' (' || authorCode.abbreviation_it || ') - ' || TO_CHAR(administration.administratedDate_time_low, 'DD/MM/YYYY hh24:MI') || ' - ' || administration.text_string" +
			" END as erogationDetails," +						
			" CASE" +
			"    WHEN administration.cancellation_date IS NOT NULL THEN SUBSTR(cancellationAuthor.name_giv, 1, 1) || '. ' || cancellationAuthor.name_fam || ' (' || cancellationAuthorCode.abbreviation_it || ') - ' || TO_CHAR(administration.cancellation_date, 'DD/MM/YYYY hh24:MI') || ' - ' || administration.cancellation_note" +
			" END as cancellationDetails," +
			" prescription.internal_id as parentId," +
			" prescription.period_time_high as endDate" +
			" FROM" +
			" substanceadmin administration" +
			" LEFT JOIN code_value statusCode ON administration.status_code = statusCode.id" +
			" LEFT JOIN code_value statusDetailsCode ON administration.status_details_code = statusDetailsCode.id" +
			" LEFT JOIN employee author ON administration.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode ON administration.authorRole = authorCode.id" +
			" LEFT JOIN employee cancellationAuthor ON administration.cancelled_by_id = cancellationAuthor.internal_id" +
			" LEFT JOIN code_value_role cancellationAuthorCode ON administration.cancelledByRole = cancellationAuthorCode.id" +
			" LEFT JOIN prescription prescription ON administration.prescription = prescription.internal_id" +
			" WHERE prescription.internal_id IN (:prescriptionIds)" +
			" AND ((administration.planned_date IS NULL AND administration.administratedDate_time_low >= :plannedDateFrom) OR administration.planned_date >= :plannedDateFrom)" +
			" AND ((administration.planned_date IS NULL AND administration.administratedDate_time_low <= :plannedDateTo) OR administration.planned_date <= :plannedDateTo)"; 
	
	
	/*
	** QUERIES for r_cart
	*/
	
	private static final String MEDICINE_HQL =
			" SELECT" +
			" medicine.name.giv as name," +
			" prescription.routeCode.langIt as route," +
			" SUM(CASE WHEN administration.statusCode.code LIKE 'PLANNED' THEN administration.plannedQuantity.value ELSE 0 END)  || ' [' || administration.plannedQuantity.unit || ']' as toErogate," +
			" SUM(CASE WHEN administration.statusCode.code <> 'PLANNED' THEN administration.plannedQuantity.value ELSE 0 END) || ' [' || administration.plannedQuantity.unit || ']' as erogated" +
			" FROM" +
			" SubstanceAdministration administration" +
			" LEFT JOIN administration.prescription prescription" +
			" LEFT JOIN prescription.prescriptionMedicine prescriptionMedicine" +
			" LEFT JOIN prescriptionMedicine.medicine medicine" +
			" LEFT JOIN prescription.therapy therapy" +
			" LEFT JOIN therapy.patientEncounter encounter" +
			" LEFT JOIN encounter.patient patient" +
			" WHERE prescription.code.code LIKE 'PHARMA'" +
			" AND therapy.internalId IN (:therapyId)" +
			" AND prescription.isActive = true" +
			" AND prescription.needsBased = false"; 
	
	private static final String AS_NEEDED_MEDICINE_HQL =
			" SELECT" +
			" medicine.name.giv as name," +
			" prescription.routeCode.langIt as route," +
			" SUM(CASE WHEN administration.statusCode.code LIKE 'PLANNED' THEN administration.plannedQuantity.value ELSE 0 END)  || ' [' || administration.plannedQuantity.unit || ']' as toErogate," +
			" SUM(CASE WHEN (administration.statusCode.code <> 'PLANNED' AND administration.statusCode.code <> 'CANCELLED') THEN administration.plannedQuantity.value ELSE 0 END) || ' [' || administration.plannedQuantity.unit || ']' as erogated" +
			" FROM" +
			" SubstanceAdministration administration" +
			" LEFT JOIN administration.prescription prescription" +
			" LEFT JOIN prescription.prescriptionMedicine prescriptionMedicine" +
			" LEFT JOIN prescriptionMedicine.medicine medicine" +
			" LEFT JOIN prescription.therapy therapy" +
			" LEFT JOIN therapy.patientEncounter encounter" +
			" LEFT JOIN encounter.patient patient" +
			" WHERE prescription.code.code LIKE 'PHARMA'" +
			" AND therapy.internalId IN (:therapyId)" +
			" AND prescription.isActive = true" +
			" AND prescription.needsBased = true"; 
	
	private static final String SOLUTION_SQL =
			" SELECT" +
			" solution.lang_it as name," +
			" routeCode.lang_it as route," +
			" CAST(SUM(CASE WHEN statusCode.code LIKE 'PLANNED' THEN 1 ELSE 0 END) AS varchar2(30)) as toErogate," +
			" CAST(SUM(CASE WHEN statusCode.code <> 'PLANNED' THEN 1 ELSE 0 END) AS varchar2(30)) as erogated" +
			" FROM" +
			" SubstanceAdmin administration" +
			" LEFT JOIN Prescription prescription ON administration.prescription = prescription.internal_id" +
			" JOIN Code_Value solution ON prescription.infusion_type_code = solution.id" +
			" LEFT JOIN Code_Value routeCode ON prescription.route_code = routeCode.id" +
			" LEFT JOIN Code_Value statusCode ON administration.status_code = statusCode.id" +
			" LEFT JOIN Code_Value prescriptionCode ON prescription.code = prescriptionCode.id" +
			" LEFT JOIN Therapy therapy ON prescription.therapy = therapy.internal_id" +
			" LEFT JOIN Patient_Encounter encounter ON therapy.patient_encounter = encounter.internal_id" +
			" LEFT JOIN Patient patient ON encounter.patient_id = patient.internal_id" +
			" WHERE prescriptionCode.code LIKE 'INFU'" +
			" AND therapy.internal_id IN (:therapyId)" +
			" AND prescription.is_active = 1";			
	
	private static final String COMPONENT_SQL =
			"   SELECT" +
			"   medicine.name_giv as name," +
			"   routeCode.lang_it as route," +
			"   SUM(CASE WHEN statusCode.code LIKE 'PLANNED' THEN CAST(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=') + 1, INSTR(prescriptionMedicine.dosage, '[') - INSTR(prescriptionMedicine.dosage, '=') - 1) AS number) ELSE 0 END) as toErogate," +
			"   SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '['), INSTR(prescriptionMedicine.dosage, ']') - INSTR(prescriptionMedicine.dosage, '[') + 1)  as toErogateUM," +
			"   SUM(CASE WHEN statusCode.code <> 'PLANNED' THEN CAST(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=') + 1, INSTR(prescriptionMedicine.dosage, '[') - INSTR(prescriptionMedicine.dosage, '=') - 1) AS number) ELSE 0 END) as erogated," +
			"   SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '['), INSTR(prescriptionMedicine.dosage, ']') - INSTR(prescriptionMedicine.dosage, '[') + 1)  as erogatedUM" +
			"   FROM" +
			"   SubstanceAdmin administration" +
			"   LEFT JOIN Prescription prescription ON administration.prescription = prescription.internal_id" +
			"   LEFT JOIN Prescription_Medicine prescriptionMedicine ON prescription.internal_id = prescriptionMedicine.prescription_id" +
			"   JOIN Medicine medicine ON medicine.internal_id = prescriptionMedicine.medicine_id" +
			"   LEFT JOIN Code_Value routeCode ON prescription.route_code = routeCode.id" +
			"   LEFT JOIN Code_Value statusCode ON administration.status_code = statusCode.id" +
			"   LEFT JOIN Code_Value prescriptionCode ON prescription.code = prescriptionCode.id" +
			"   LEFT JOIN Therapy therapy ON prescription.therapy = therapy.internal_id" +
			"   LEFT JOIN Patient_Encounter encounter ON therapy.patient_encounter = encounter.internal_id" +
			"   LEFT JOIN Patient patient ON encounter.patient_id = patient.internal_id" +
			"   WHERE prescriptionCode.code LIKE 'INFU'" +
			"   AND therapy.internal_id IN (:therapyId)" +
			"   AND prescription.is_active = 1";	
		
	
	/*
	 *  OTHER QUERIES
	 */	
	
	private static final String WORK_SHIFT_HQL = 
			" SELECT ws.label as label, ws.fromTime as from, ws.toTime as to" +
			" FROM WorkShift ws JOIN ws.serviceDeliveryLocation sdl" +
			" WHERE sdl.internalId = :sdlId" +
			" ORDER BY ws.fromTime";
	
	private static final String AS_NEEDED_PRES_HQL = 
			" SELECT" +
			" CASE" +
			"   WHEN prescription.maximumQuantity24h.value IS NOT null THEN (prescription.maximumQuantity24h.value || ' [' || prescription.maximumQuantity24h.unit || ']')" +
			" END as maximumQuantity," +
			" CASE" +
			"	WHEN  prescription.hoursGap < 10 AND prescription.minsGap < 10 THEN ('0' || prescription.hoursGap || ':' || '0' || prescription.minsGap)" +
			"	WHEN  prescription.hoursGap < 10 AND prescription.minsGap >= 10 THEN ('0' || prescription.hoursGap || ':' || prescription.minsGap)" +
			"	WHEN  prescription.hoursGap >= 10 AND prescription.minsGap < 10 THEN (prescription.hoursGap || ':' || '0' || prescription.minsGap)" +
			"	WHEN  prescription.hoursGap >= 10 AND prescription.minsGap >= 10 THEN (prescription.hoursGap || ':' || prescription.minsGap)" +			
			" END as gap," +
			" CASE" +
			"	WHEN prescription.systolicPressure.value IS NOT null THEN (sysTH.langIt || ' ' || prescription.systolicPressure.value || ' ' || prescription.systolicPressure.unit)" +
			" END as systolicPressure," +
			" CASE" +
			"	WHEN prescription.diastolicPressure.value IS NOT null THEN (diaTH.langIt || ' ' || prescription.diastolicPressure.value || ' ' || prescription.diastolicPressure.unit)" +
			" END as diastolicPressure," +
			" CASE" +
			"	WHEN prescription.temperature.value IS NOT null THEN (tempTH.langIt || ' ' || prescription.temperature.value || ' ' || prescription.temperature.unit)" +
			" END as temperature," +
			" CASE" +
			"	WHEN prescription.glycemia.value IS NOT null  THEN (glyTH.langIt || ' ' || prescription.glycemia.value || ' ' || prescription.glycemia.unit)" +
			" END as glycemia," +
			" CASE" +
			"	WHEN prescription.diuresis.value IS NOT null THEN (diuTH.langIt || ' ' || prescription.diuresis.value || ' ' || prescription.diuresis.unit)" +
			" END as diuresis," +
			" CASE" +
			"	WHEN prescription.pain.value IS NOT null THEN (painTH.langIt || ' ' || prescription.pain.value || ' ' || prescription.pain.unit)" +
			" END as pain," +
			" CASE" +
			"	WHEN prescription.heartRate.value IS NOT null THEN (rateTH.langIt || ' ' || prescription.heartRate.value || ' ' || prescription.heartRate.unit)" +
			" END as heartRate," +
			" CASE" +
			"	WHEN prescription.spo2.value IS NOT null THEN (spo2TH.langIt || ' ' || prescription.spo2.value || ' ' || prescription.spo2.unit)" +
			" END as spo2," +
			" prescription.glySecondaryProtocol as glySecondaryProtocol," +
			" prescription.other as other" +
			" FROM" +
			" Prescription prescription" +
			" LEFT JOIN prescription.systolicPressureThreshold sysTH" +
			" LEFT JOIN prescription.diastolicPressureThreshold diaTH" +
			" LEFT JOIN prescription.temperatureThreshold tempTH" +
			" LEFT JOIN prescription.glycemiaThreshold glyTH" +
			" LEFT JOIN prescription.diuresisThreshold diuTH" +
			" LEFT JOIN prescription.painThreshold painTH" +
			" LEFT JOIN prescription.heartRateThreshold rateTH" +
			" LEFT JOIN prescription.spo2Threshold spo2TH" +
			" WHERE prescription.internalId = :prescriptionId";
	
	private static final String PRESCRIPTION_DETAILS_SQL = 
			" SELECT" +
			" prescription.period_time_low as startDate," +
			" prescription.period_time_high as endDate," +
			" routeCode.lang_it as routeCode_translation," +
			" upper(author.name_fam)||' '||author.name_giv as author," +
			" prescription.extemporaneous as extemporaneous," +
			" prescription.needsbased as needsbased," +
			" prescription.urgent as urgent," +
			" prescription.continuous as continuous," +
			" prescriptionCode.code as code," +
			" LISTAGG (prescriptionMedicine.dosage , ',') WITHIN GROUP (ORDER BY prescriptionMedicine.dosage) as posology," +
			" CASE" +
			"	WHEN prescriptionCode.code='INFU' THEN prescription.quantity_value || '[' || prescription.quantity_unit || ']@' || prescription.infusion_speed_value || '[' || prescription.infusion_speed_unit  || ']'" +
			" END as velocity," +
			" templatedosage.title as templateString" +
			" FROM prescription prescription" +
			" LEFT JOIN code_value routeCode on prescription.route_code=routeCode.id" +
			" LEFT JOIN employee author on prescription.author_id=author.internal_id" +
			" LEFT JOIN code_value prescriptionCode on prescription.code=prescriptionCode.id " +
			" LEFT JOIN prescription_medicine prescriptionMedicine on prescription.internal_id=prescriptionMedicine.prescription_id" +
			" LEFT JOIN templatedosage templateDosage on prescriptionMedicine.template_dosage_key=templateDosage.key and templateDosage.is_active = 1" +
			" WHERE" +
			" prescription.internal_id=:prescriptionId" +
			" GROUP BY" +
			" prescription.period_time_low," +
			" prescription.period_time_high," +
			" routeCode.lang_it," +
			" author.name_fam," +
			" author.name_giv," +
			" prescription.extemporaneous," +
			" prescription.needsbased," +
			" prescription.urgent," +
			" prescription.continuous," +
			" prescriptionCode.code," +
			" prescription.quantity_value," +
			" prescription.quantity_unit," +
			" prescription.infusion_speed_value," +
			" prescription.infusion_speed_unit," +
			" templatedosage.title";
	
	private static final String ACTIVITY_DETAILS_SQL =
			" SELECT details.*, templateDosage.title as frequency FROM (" +
				" SELECT " +
				" activity.effective_Date_low as startDate," +
				" activity.effective_Date_high as endDate," +
				" case" +
				"   when activity.support_number is not null then responsibleRole.lang_it||' ('||activity.support_number||' '||supportRole.lang_it||')'" +
				"   when activity.support_number is null then responsibleRole.lang_it" +
				" end as involvedRoles," +
				" upper(author.name_fam)||' '||author.name_giv as author," +
				" activity.extemporaneous as extemporaneous," +
				" LISTAGG (to_char(dosage.daytime, 'HH24:MI'), ', ') Within GROUP (ORDER BY dosage.daytime) AS daytime," +
				" LISTAGG (CASE WHEN ROWNUM <=1 THEN dosage.day_interval ELSE NULL END, '') Within GROUP (ORDER BY dosage.daytime) AS dayinterval," +
				" sdloc.internal_id || ';A=' || LISTAGG (dosage.day_interval || '@' || to_char(dosage.daytime, 'HH24:MI'), '|') Within GROUP (ORDER BY dosage.daytime) AS templateDosageKey" +
				" FROM lep_activity activity" +
				" LEFT JOIN code_value_role responsibleRole on activity.responsible_role=responsibleRole.id" +
				" LEFT JOIN code_value_role supportRole on activity.support_role=supportRole.id" +
				" LEFT JOIN employee author on activity.author_id=author.internal_id" +
				" LEFT JOIN dosage dosage on activity.internal_id=dosage.lep_activity" +
				" LEFT JOIN patient_encounter pe on pe.internal_id = activity.patenc" +
				" LEFT JOIN service_delivery_location sdloc on sdloc.INTERNAL_ID = pe.assignedsdl" +
				" WHERE activity.internal_id=:activityId" +
				" GROUP BY" +
				" activity.effective_Date_low," +
				" activity.effective_Date_high," +
				" activity.support_number," +
				" responsibleRole.lang_it," +
				" activity.support_number," +
				" supportRole.lang_it," +
				" author.name_fam," +
				" author.name_giv," +
				" activity.extemporaneous," +
				" sdloc.internal_id" +
			" ) details" +
			" LEFT JOIN templatedosage templateDosage on details.templateDosageKey=templateDosage.key and templateDosage.is_active = 1";	

	private static final String ADMINISTRATION_DETAILS_HQL =
			" SELECT" +
			" UPPER(author.name.fam) || ' ' || author.name.giv as author," +
			" administration.availabilityTime as insertionDate," +
			" administration.administratedDate.low as administratedDate," +
			" administration.text.string as note" +
			" FROM" +
			" SubstanceAdministration administration" +
			" LEFT JOIN administration.author author" +
			" WHERE administration.internalId = :adminId";
	
	private static final String EXECUTION_DETAILS_HQL =
			" SELECT" +
			" UPPER(author.name.fam) || ' ' || author.name.giv as author," +
			" execution.availabilityTime as insertionDate," +
			" execution.executionDate.low as executionDate," +
			" execution.note as note," +
			" objectiveStatus.langIt as objectiveStatus," +
			" objective.cancellationDate as objectiveCancellationDate" +
			" FROM" +
			" LEPExecution execution" +
			" LEFT JOIN execution.author author" +
			" LEFT JOIN execution.lepActivity activity" +
			" LEFT JOIN activity.objective objective" +
			" LEFT JOIN objective.objReached objectiveStatus" +
			" WHERE execution.internalId = :exeId";

	
	
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {
			
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}
			
			String currentLang = Locale.instance().getLanguage();
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			List<Map<String, Object>> results = readAdministrationsAndExecutions(restrictionMap, page, false);
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, getUrl4Pagination(), readPageSize, page);			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();			
			res.setMain(dm);

			Long sdlId = null; 			
			if (restrictions.getMatrixParameters().get("sdlocId") != null) {
				sdlId = Long.parseLong(restrictions.getMatrixParameters().get("sdlocId").get(0));
			}
						
			if (sdlId != null) {
				
				String workShiftHql = WORK_SHIFT_HQL;
				
				if (!currentLang.equals("it")) {
					workShiftHql = workShiftHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				}
				
				Query qryWorkShift = ca.createHibernateQuery(workShiftHql);
				
				qryWorkShift.setParameter("sdlId", sdlId);
	
				qryWorkShift.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
				List<Map<String, Object>> resultsWorkShift = qryWorkShift.list();
			
				res.setAdditional(new HashMap<String, List<Map<String,Object>>>());
				res.getAdditional().put("workShift", resultsWorkShift);
			}
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] NurseActivity Init");
			log.debug("[cid="+Conversation.instance().getId()+"] NurseActivity Init details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in NurseActivity Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest init").build();
		}
	}
	
	@GET
	@Path("refresh/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {
			
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}
			
			String currentLang = Locale.instance().getLanguage();
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			List<Map<String, Object>> results = readAdministrationsAndExecutions(restrictionMap, page, false);
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, getUrl4Pagination(), readPageSize, page);			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();			
			res.setMain(dm);

			Long sdlId = null; 
			if (restrictions.getMatrixParameters().get("sdlocId") != null) {
				sdlId = Long.parseLong(restrictions.getMatrixParameters().get("sdlocId").get(0));
			}
			
			boolean sdlChanged = false;			
			if (restrictions.getMatrixParameters().get("sdlChanged") != null) {
				sdlChanged = true;
			}
			
			if (sdlId != null && sdlChanged) {
				
				String workShiftHql = WORK_SHIFT_HQL;
				
				if (!currentLang.equals("it")) {
					workShiftHql = workShiftHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				}
				
				Query qryWorkShift = ca.createHibernateQuery(workShiftHql);
				
				qryWorkShift.setParameter("sdlId", sdlId);
	
				qryWorkShift.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
				
				List<Map<String, Object>> resultsWorkShift = qryWorkShift.list();
				
				res.setAdditional(new HashMap<String, List<Map<String,Object>>>());
				res.getAdditional().put("workShift", resultsWorkShift);
			}
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] NurseActivity Refresh");
			log.debug("[cid="+Conversation.instance().getId()+"] NurseActivity Refresh details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in NurseActivity Refresh with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest refresh").build();
		}
	}
	
	@GET
	@Path("printReport/{restrictions}")
	public Response printReport(@PathParam("restrictions") PathSegment restrictions) {
		try {	
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			
			boolean calendarReport = restrictionMap.remove("calendarReport").get(0).equals("true");
			boolean cartReport = restrictionMap.remove("cartReport").get(0).equals("true");
			boolean enableLEPTime = false;
			
			if (restrictionMap.get("enableLEPTime") != null) {
				enableLEPTime = restrictionMap.remove("enableLEPTime").get(0).equals("true");
			}
			
			Contexts.getConversationContext().set("EnableLEPTime", enableLEPTime);
						
			if (!calendarReport && !cartReport) {
				readAdministrationsAndExecutions(restrictionMap, -1, true);
			} else if (calendarReport) {				
				readCalendar(restrictionMap);				
			} else if (cartReport) {				
				readCart(restrictionMap);				
			}
			
			HashMap<String, String> filters = new HashMap<String, String>();
			
			DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");						 
						
			if (restrictionMap.get("shift") != null) {
				filters.put("shift", restrictionMap.get("shift").get(0));
			} else {
				filters.put("shift", "");
			}
			
			if (restrictionMap.get("plannedDateFrom") != null) {
				String plannedDateFromStr = restrictionMap.get("plannedDateFrom").get(0);
				Date plannedDateFrom = decodeISO8601(plannedDateFromStr);				
				filters.put("plannedDateFrom", dateTimeFormat.format(plannedDateFrom));
			} else {
				filters.put("plannedDateFrom", "n.d.");
			}				

			if (restrictionMap.get("plannedDateTo") != null) {
				String plannedDateToStr = restrictionMap.get("plannedDateTo").get(0);
				Date plannedDateTo = decodeISO8601(plannedDateToStr);							
				filters.put("plannedDateTo", dateTimeFormat.format(plannedDateTo));				
			} else {
				filters.put("plannedDateTo", "n.d.");
			}
			
			if (restrictionMap.get("routeCode.code") != null) {
				filters.put("route", restrictionMap.get("routeCode.code").get(0));
			} else {
				filters.put("route", FunctionsBean.instance().getStaticTranslation("all"));
			}
			
			String typeString = null;
			
			if (restrictionMap.get("onlyExecutions") != null) {
				typeString = FunctionsBean.instance().getStaticTranslation("EXE");
			} else  if (restrictionMap.get("onlySubministrations") != null) {
				if (restrictionMap.get("prescriptionCode.code") != null) {
					typeString = FunctionsBean.instance().getStaticTranslation(restrictionMap.get("prescriptionCode.code").get(0));					
				} else {
					typeString = FunctionsBean.instance().getStaticTranslation("PHARMA") + ", " + FunctionsBean.instance().getStaticTranslation("INFU");
				}			
			} else {
				typeString = FunctionsBean.instance().getStaticTranslation("EXE") + ", ";
				if (restrictionMap.get("prescriptionCode.code") != null) {
					typeString += FunctionsBean.instance().getStaticTranslation(restrictionMap.get("prescriptionCode.code").get(0));					
				} else {
					typeString += FunctionsBean.instance().getStaticTranslation("PHARMA") + ", " + FunctionsBean.instance().getStaticTranslation("INFU");
				}
			}
						
			if (typeString != null) {
				filters.put("type", typeString);				
			} else {
				filters.put("type", "-");
			}
							
			// FIXME: filtro stati
			String statusString = null;
			
			if (statusString != null) {
				filters.put("status", statusString);				
			} else {
				filters.put("status", FunctionsBean.instance().getStaticTranslation("all"));
			}
			
			if (restrictionMap.get("responsibleCode.code") != null) {
				CodeValue responsibleCode = VocabulariesImpl.instance().getCodeValueCsDomainCode("ROLES", "EmployeeFunction", restrictionMap.get("responsibleCode.code").get(0));
				filters.put("responsible", responsibleCode.getCurrentTranslation());
			} else {
				filters.put("responsible", FunctionsBean.instance().getStaticTranslation("all"));
			}
						
			// Only ward view
			if (restrictionMap.get("sdlocId") != null) {
												
				String nameString = null;
				
				if (restrictionMap.get("patient.name.fam") != null) {
					nameString = "'" + restrictionMap.get("patient.name.fam").get(0) + "', ";
				} else {
					nameString = "n.d., ";
				}
				
				if (restrictionMap.get("patient.name.giv") != null) {
					nameString += "'" + restrictionMap.get("patient.name.giv").get(0) + "'";
				} else {
					nameString += "n.d.";
				}
				
				filters.put("name", nameString);				
												
				// FIXME: filtro gruppo letti
				if (restrictionMap.get("bedGroup") != null) {
					filters.put("bedGroup", restrictionMap.get("bedGroup").get(0));
				} else {
					filters.put("bedGroup", "n.d.");
				}	
				
				if (restrictionMap.get("room") != null) {
					filters.put("room", restrictionMap.get("room").get(0));
				} else {
					filters.put("room", "n.d.");
				}
				
				if (restrictionMap.get("bed") != null) {
					filters.put("bed", restrictionMap.get("bed").get(0));
				} else {
					filters.put("bed", "n.d.");
				}
			}				
			
			Contexts.getConversationContext().set("Filters", filters);
									
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
						
			return Response.ok().build();
	
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest printReport", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest printReport").build();
		}
	}
	
	// Function to read erogations (dashboard, r_erogationPatient, r_erogationWard)
	private List<Map<String, Object>> readAdministrationsAndExecutions(MultivaluedMap<String, String> restrictionMap, int page, boolean report) throws Exception {
		// converts restrictions multivalued map to hashmap
		HashMap<String, String> restrictionsMap = new HashMap<String, String>();
		for (String key : restrictionMap.keySet()) {
			restrictionsMap.put(key, restrictionMap.get(key).get(0));
		}
		return readAdministrationsAndExecutions(restrictionsMap, page, report, false, null);
	}
	
	// Function to read erogations (dashboard, patient dashboard, r_erogationPatient, r_erogationWard)
	private List<Map<String, Object>> readAdministrationsAndExecutions(HashMap<String, String> restrictionsMap, int page, boolean report, boolean patientDashboard, String administrationType) throws Exception {

		List<Long> encounterIds = new ArrayList<Long>(); 
		List<Long> therapyIds = new ArrayList<Long>();
				
		String userRoleCode =  UserBean.instance().getRole();
		String currentLang = Locale.instance().getLanguage();		
				
		Long sdlocId = null;
		
		if (restrictionsMap.get("sdlocId") != null && restrictionsMap.get("encounterId") == null && restrictionsMap.get("therapyId") == null) {
			sdlocId = Long.parseLong(restrictionsMap.get("sdlocId"));
			
			boolean executions = true;
			boolean subministrations = true;
			
			if (restrictionsMap.get("onlyExecutions") != null) {
				subministrations = false;
			}
			
			if (restrictionsMap.get("onlySubministrations") != null) {
				executions = false;
			}
						
			String ENC_AND_THERAPY_IDS_HQL = "";
			
			if ("10".equals(userRoleCode) || "12".equals(userRoleCode) || "21".equals(userRoleCode) || "28".equals(userRoleCode)) {			
				ENC_AND_THERAPY_IDS_HQL = "SELECT enc.internalId, therapy.internalId FROM PatientEncounter enc LEFT JOIN enc.therapy therapy LEFT JOIN enc.temporarySDL temporary LEFT JOIN enc.assignedSDL assigned WHERE ((temporary.internalId IS NULL AND assigned.internalId = :sdlocId) OR temporary.internalId = :sdlocId) AND enc.statusCode.code = 'active'";
			} else {
				ENC_AND_THERAPY_IDS_HQL = "SELECT enc.internalId, therapy.internalId FROM PatientEncounter enc LEFT JOIN enc.therapy therapy LEFT JOIN enc.assignedSDL assigned WHERE assigned.internalId = :sdlocId AND enc.statusCode.code = 'active'";
			}
			
			Query qryEncTherIds = ca.createHibernateQuery(ENC_AND_THERAPY_IDS_HQL);
			
			qryEncTherIds.setParameter("sdlocId", sdlocId);
			
			List<Long[]> encAndTherIds = qryEncTherIds.list();
			
			for (Object[] encAndTherId : encAndTherIds) {
				if (executions) {
					encounterIds.add((Long)encAndTherId[0]);
				}
				if (subministrations && encAndTherId[1] != null) {
					therapyIds.add((Long)encAndTherId[1]);
				}
			}			
		}
		
		// Parsing restrictions 
		if (restrictionsMap.get("encounterId") != null) {
			encounterIds.add(Long.parseLong(restrictionsMap.get("encounterId")));
		}

		if (restrictionsMap.get("therapyId") != null) {
			therapyIds.add(Long.parseLong(restrictionsMap.get("therapyId")));
		}
				
		String patientNameFam = ""; 
		if (restrictionsMap.get("patient.name.fam") != null) {
			patientNameFam = restrictionsMap.get("patient.name.fam");
		}
		
		String patientNameGiv = ""; 
		if (restrictionsMap.get("patient.name.giv") != null) {
			patientNameGiv = restrictionsMap.get("patient.name.giv");
		}
		
		String room = ""; 
		if (restrictionsMap.get("room") != null) {
			room = restrictionsMap.get("room");
		}
		
		String bed = "";
		if (restrictionsMap.get("bed") != null) {
			bed = restrictionsMap.get("bed");
		}
		
		String responsibleCode = "";
		if (restrictionsMap.get("responsibleCode.code") != null) {
			responsibleCode = restrictionsMap.get("responsibleCode.code");
		}
		
		String routeCodeCode = "";
		if (restrictionsMap.get("routeCode.code") != null) {
			routeCodeCode = restrictionsMap.get("routeCode.code");			
		}
		
		String prescriptionCodeCode = "";
		if (restrictionsMap.get("prescriptionCode.code") != null) {
			prescriptionCodeCode = restrictionsMap.get("prescriptionCode.code");			
		}
		
		Date plannedDateFrom = null;
		if (restrictionsMap.get("plannedDateFrom") != null) {
			String plannedDateFromStr = restrictionsMap.get("plannedDateFrom");
			plannedDateFrom = decodeISO8601(plannedDateFromStr);			
		}
		// Add privacy restrictions
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			if (plannedDateFrom == null || plannedDateFrom.before(TransferPrivacy.instance().getFilterDate().getLow())) {
				plannedDateFrom = TransferPrivacy.instance().getFilterDate().getLow();
			}
		}
		
		Date plannedDateTo = null;
		if (restrictionsMap.get("plannedDateTo") != null) {
			String plannedDateToStr = restrictionsMap.get("plannedDateTo");
			plannedDateTo = decodeISO8601(plannedDateToStr);			
		}
		
		// Add privacy restrictions
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			if (plannedDateTo == null || plannedDateTo.after(TransferPrivacy.instance().getFilterDate().getHigh())) {
				plannedDateTo = TransferPrivacy.instance().getFilterDate().getHigh();
			}
		}
			
		// Build query
		String sql = null;
		
		List<Map<String, Object>> results = null;
		
		//EXECUTIONS
		if (!encounterIds.isEmpty()) {

			sql = EXECUTION_SELECT_SQL;
			
			if (report){
				sql += EXECUTION_SELECT_REPORT_SQL;
			}
			
			sql += EXECUTION_JOIN_SQL;
			
			if (report){
				sql += EXECUTION_JOIN_REPORT_SQL;
			}
			
			sql += EXECUTION_WHERE_SQL;
			
			if (patientNameFam != null && patientNameFam != "") {
				sql += " AND LOWER(patient.name_fam) like LOWER(:patientNameFam)";
			}
			
			if (patientNameGiv != null && patientNameGiv != "") {
				sql += " AND LOWER(patient.name_giv) like LOWER(:patientNameGiv)";
			}
			
			if (room != null && room != "") {
				sql += " AND LOWER(encounter.room_string) like LOWER(:room)";
			}
			
			if (bed != null && bed != "") {
				sql += " AND LOWER(encounter.bed_string) like LOWER(:bed)";
			}
			
			if (responsibleCode != null && responsibleCode != "") {
				sql += " AND responsibleRole.code = :responsibleCode";
			}
						
			// O.S.S. = 12: only executions where is responsible or support are visible
			if (userRoleCode.equals("12")) {
				sql+=" AND (responsibleRole.code = :userCode OR supportRole.code = :userCode)";
			}
			
			if (plannedDateFrom != null) {
				sql+=" AND execution.planned_date >= :plannedDateFrom";
			}
			
			if (plannedDateTo != null) {
				sql+=" AND execution.planned_date <= :plannedDateTo";
			}		
			
			
		}		
		
		Date missedTime = null;
		
		//ADMINISTRATIONS
		//O.S.S. = 12: administrations are not visible
		if (!therapyIds.isEmpty() && !userRoleCode.equals("12")) {

			if (sql == null) {
				sql = ADMINISTRATION_SELECT_SQL;
			} else {
				sql+= " UNION ALL " + ADMINISTRATION_SELECT_SQL;
			}
			
			if (report){
				sql += ADMINISTRATION_SELECT_REPORT_SQL;
			}
			
			sql += ADMINISTRATION_JOIN_SQL;
			
			if (report){
				sql += ADMINISTRATION_JOIN_REPORT_SQL;
			}
									
			sql += ADMINISTRATION_WHERE_SQL;
			
			if (patientNameFam != null && patientNameFam != "") {
				sql += " AND LOWER(patient.name_fam) like LOWER(:patientNameFam)";
			}
			
			if (patientNameGiv != null && patientNameGiv != "") {
				sql += " AND LOWER(patient.name_giv) like LOWER(:patientNameGiv)";
			}
			
			if (room != null && room != "") {
				sql += " AND LOWER(encounter.room_string) like LOWER(:room)";
			}
			
			if (bed != null && bed != "") {
				sql += " AND LOWER(encounter.bed_string) like LOWER(:bed)";
			}

			if (routeCodeCode != null && routeCodeCode != "") {
				sql += " AND prescriptionRouteCode.code = :routeCodeCode";
			}

			if (prescriptionCodeCode != null && prescriptionCodeCode != "") {
				sql += " AND prescriptionCode.code = :prescriptionCodeCode";
			}
			
			if (plannedDateFrom != null) {
				sql+=	" AND ((subAdmin.planned_date IS NULL AND subAdmin.administratedDate_time_low >= :plannedDateFrom)" +
						" OR (subAdmin.planned_date IS NULL AND subAdmin.administratedDate_time_low IS NULL AND (prescription.period_time_high IS NULL OR prescription.period_time_high > :sysdate) AND (prescription.validity_period_time_high IS NULL OR prescription.validity_period_time_high > :sysdate) AND :sysdate >= :plannedDateFrom)" +
						" OR (subAdmin.planned_date >= :plannedDateFrom))";
			}
			
			if (plannedDateTo != null) {
				sql+=	" AND ((subAdmin.planned_date IS NULL AND subAdmin.administratedDate_time_low <= :plannedDateTo)" +
						" OR (subAdmin.planned_date IS NULL AND subAdmin.administratedDate_time_low IS NULL AND prescription.period_time_low < :sysdate AND prescription.validity_period_time_low < :sysdate AND :sysdate <= :plannedDateTo)" +
						" OR (subAdmin.planned_date <= :plannedDateTo))";		
			}		
			
			
			if (patientDashboard) {
				long nowTime = new Date().getTime();
				missedTime = new Date(nowTime - ErogationStatusFixer.ADMINISTRATION_HIGH_GAP * ErogationStatusFixer.MINUTE_IN_MILLISECONDS);
				if (ErogationStatusFixer.MISSED.equals(administrationType)) {
					sql += 	" AND subAdmin.planned_date < :missedTime" +
							" AND statusCode.code = :statusCode";
				} else if (ErogationStatusFixer.DUE.equals(administrationType)) {
					sql += 	" AND subAdmin.planned_date >= :missedTime" +
							" AND statusCode.code = :statusCode";
				}
			}
			
			sql += ADMINISTRATION_GROUPBY_SQL;
			
			if (report){
				sql += ADMINISTRATION_GROUPBY_REPORT_SQL;
			}
		}
		
		if (sql != null) {
			
			if (!currentLang.equals("it")) {
				sql = sql.replace("lang_it", "lang_" + currentLang);
			}
			
			Pattern labelPattern = Pattern.compile("label_[^'\\s]*");
			
			Matcher matcher = labelPattern.matcher(sql);
			
			while (matcher.find()) {
				String label = matcher.group();
				String key = label.substring(6);
				String translation = FunctionsBean.instance().getStaticTranslation(key);
				sql = sql.replace(label, translation);
				matcher.reset(sql);
			}
			
			
			if (sdlocId != null) {
				sql += 	" ORDER BY" +						
						" encounter_roomLength ASC NULLS LAST," +
						" encounter_room ASC," +
						" encounter_bedLength ASC NULLS LAST," +
						" encounter_bed ASC," +
						" patient ASC," +
						" continuous DESC," +
						" plannedDate ASC NULLS FIRST," +
						" erogationDate ASC NULLS FIRST";
			} else if (patientDashboard) {
				if (ErogationStatusFixer.MISSED.equals(administrationType)) {
					sql += 	" ORDER BY" +
							" plannedDate DESC NULLS LAST";
				} else if (ErogationStatusFixer.DUE.equals(administrationType)) {
					sql += 	" ORDER BY" +
							" plannedDate ASC NULLS LAST";
				}
			} else {
				sql += 	" ORDER BY" +
						" continuous DESC," +
						" plannedDate ASC NULLS FIRST," +
						" erogationDate ASC NULLS FIRST";
			}
			
			// Replace LISTAGG
			sql = ReplaceListaggBean.replaceListagg(sql);
			
			Query qry = ca.createHibernateNativeQuery(sql);
			
			// Setting query parameters			
			if (sql.contains(":sysdate"))
				qry.setParameter("sysdate", new Date());
			
			if (sql.contains(":encounterId"))
				qry.setParameterList("encounterId", encounterIds);
			
			if (sql.contains(":therapyId"))
				qry.setParameterList("therapyId", therapyIds);
			
			if (sql.contains(":patientNameFam"))
				qry.setParameter("patientNameFam", patientNameFam + "%");
			
			if (sql.contains(":patientNameGiv"))
				qry.setParameter("patientNameGiv", patientNameGiv + "%");
			
			if (sql.contains(":room"))
				qry.setParameter("room", room);
			
			if (sql.contains(":bed"))
				qry.setParameter("bed", bed);
			
			if (sql.contains(":responsibleCode"))
				qry.setParameter("responsibleCode", responsibleCode);
			
			if (sql.contains(":userCode"))
				qry.setParameter("userCode", userRoleCode);
			
			if (sql.contains(":routeCodeCode"))
				qry.setParameter("routeCodeCode", routeCodeCode);
			
			if (sql.contains(":prescriptionCodeCode"))
				qry.setParameter("prescriptionCodeCode", prescriptionCodeCode);			

			if (sql.contains(":plannedDateFrom"))
				qry.setParameter("plannedDateFrom", plannedDateFrom);
			
			if (sql.contains(":plannedDateTo"))
				qry.setParameter("plannedDateTo", plannedDateTo);	

			if (sql.contains(":missedTime"))
				qry.setParameter("missedTime", missedTime);

			if (sql.contains(":statusCode"))
				qry.setParameter("statusCode", ErogationStatusFixer.PLANNED);

			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
			
			if (!report){
				qry.setFirstResult((page - 1) * readPageSize);
				qry.setMaxResults(readPageSize + 1);
			}

			results = qry.list();

			//fixErogations(results, report); 
			results = ErogationStatusFixer.fixErogations(results, report);
			
			/*
			if (patientDashboard) {
				splitAdministrationsAndExecutionsForPatientDashboard(results);
			} else
			*/
			// If report, inject results in conversation
			if (report) {
				Contexts.getConversationContext().set("ErogationList", results);
			} 
			
			return results;
		}
		
		return null;
	}

	// Function to read activities, prescriptions and erogation (r_erogationCalendar)	
	private void readCalendar(MultivaluedMap<String, String> restrictionMap) throws Exception {
				
		long encounterId = -1;
		long therapyId = -1;
		
		String userRoleCode =  UserBean.instance().getRole();
		String currentLang = Locale.instance().getLanguage();
				
		// Parsing restrictions
		if (restrictionMap.get("encounterId") != null) {
			encounterId = Long.parseLong(restrictionMap.get("encounterId").get(0));
		}

		if (restrictionMap.get("therapyId") != null) {
			therapyId = Long.parseLong(restrictionMap.get("therapyId").get(0));
		}
				
		String responsibleCode = "";
		if (restrictionMap.get("responsibleCode.code") != null) {
			responsibleCode = restrictionMap.get("responsibleCode.code").get(0);
		}
		
		String routeCodeCode = "";
		if (restrictionMap.get("routeCode.code") != null) {
			routeCodeCode = restrictionMap.get("routeCode.code").get(0);			
		}
		
		String prescriptionCodeCode = "";
		if (restrictionMap.get("prescriptionCode.code") != null) {
			prescriptionCodeCode = restrictionMap.get("prescriptionCode.code").get(0);			
		}
		
		Date plannedDateFrom = null;
		if (restrictionMap.get("plannedDateFrom") != null) {
			String plannedDateFromStr = restrictionMap.get("plannedDateFrom").get(0);
			plannedDateFrom = decodeISO8601(plannedDateFromStr);			
		}
		
		Date plannedDateTo = null;
		if (restrictionMap.get("plannedDateTo") != null) {
			String plannedDateToStr = restrictionMap.get("plannedDateTo").get(0);
			plannedDateTo = decodeISO8601(plannedDateToStr);			
		}
		
		// Build query for activities and prescriptions
		String sql = null;
				
		List<Map<String, Object>> results = null;
		
		// ACTIVITIES
		if (encounterId > -1) {
			
			sql = CALENDAR_REPORT_ACTIVITY_SELECT_SQL;
								
			if (responsibleCode != null && responsibleCode != "") {
				sql += " AND responsibleRole.code = :responsibleCode";
			}
						
			// O.S.S. = 12: only executions where is responsible or support are visible
			if ("12".equals(userRoleCode)) {
				sql+=" AND (responsibleRole.code = :userCode OR supportRole.code = :userCode)";
			}
			
			if (plannedDateFrom != null) {
				sql+=	" AND (activity.effective_date_high IS NULL OR activity.effective_date_high >= :plannedDateFrom)" +
						" AND (activity.validity_period_time_high IS NULL OR activity.validity_period_time_high >= :plannedDateFrom)";
			}
			
			if (plannedDateTo != null) {
				sql+=	" AND activity.effective_date_low <= :plannedDateTo" +
						" AND (activity.validity_period_time_low IS NULL OR activity.validity_period_time_low <= :plannedDateTo)";
			}	
			
			sql += CALENDAR_REPORT_ACTIVITY_GROUPBY_SQL;			
		}
		
		// PRESCRIPTIONS
		//O.S.S. = 12: prescriptions are not visible
		if (therapyId > -1 && !("12".equals(userRoleCode))) {
				
			if (sql == null) {
				sql = CALENDAR_REPORT_PRESCRIPTION_SELECT_SQL;
			} else {
				sql += " UNION ALL" + CALENDAR_REPORT_PRESCRIPTION_SELECT_SQL;
			}
			
			if (routeCodeCode != null && routeCodeCode != "") {
				sql += " AND prescriptionRouteCode.code = :routeCodeCode";
			}

			if (prescriptionCodeCode != null && prescriptionCodeCode != "") {
				sql += " AND prescriptionCode.code = :prescriptionCodeCode";
			}
			
			if (plannedDateFrom != null) {
				sql+=	" AND (prescription.period_time_high IS NULL OR prescription.period_time_high >= :plannedDateFrom)" +
						" AND (prescription.validity_period_time_high IS NULL OR prescription.validity_period_time_high >= :plannedDateFrom)";
			}
			
			if (plannedDateTo != null) {
				sql+=	" AND prescription.period_time_low <= :plannedDateTo" +
						" AND (prescription.validity_period_time_low IS NULL OR prescription.validity_period_time_low <= :plannedDateTo)";
			}		
			
			sql += CALENDAR_REPORT_PRESCRIPTION_GROUPBY_SQL;
		}		
		
		if (!currentLang.equals("it")) {
			sql = sql.replace("lang_it", "lang_" + currentLang);
			sql = sql.replace("abbreviation_it", "abbreviation_" + currentLang);
		}
		
		// Replace LISTAGG
		sql = ReplaceListaggBean.replaceListagg(sql);
		
		Query qry = ca.createHibernateNativeQuery(sql);
		
		// Setting query parameters		
		if (sql.contains(":sysdate"))
			qry.setParameter("sysdate", new Date());
		
		if (sql.contains(":encounterId"))
			qry.setParameter("encounterId", encounterId);
		
		if (sql.contains(":therapyId"))
			qry.setParameter("therapyId", therapyId);
				
		if (sql.contains(":responsibleCode"))
			qry.setParameter("responsibleCode", responsibleCode);
		
		if (sql.contains(":userCode"))
			qry.setParameter("userCode", userRoleCode);
		
		if (sql.contains(":routeCodeCode"))
			qry.setParameter("routeCodeCode", routeCodeCode);
		
		if (sql.contains(":prescriptionCodeCode"))
			qry.setParameter("prescriptionCodeCode", prescriptionCodeCode);			

		if (sql.contains(":plannedDateFrom"))
			qry.setParameter("plannedDateFrom", plannedDateFrom);
		
		if (sql.contains(":plannedDateTo"))
			qry.setParameter("plannedDateTo", plannedDateTo);	
		
		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
		
		results = qry.list();
		
		List<Long> activityIds = new ArrayList<Long>();
		List<Long> prescriptionIds = new ArrayList<Long>();	
		List<Long> activityOrPescriptionIds = new ArrayList<Long>();
		
		// Fix activity and prescription frequency
		for (Map<String, Object> result : results) {
		
			if ("A".equals(result.get("type").toString())) {
				
				fixActivityFrequency(result, true);					
								
				activityIds.add(Long.parseLong(result.get("internalid").toString()));	
				activityOrPescriptionIds.add(Long.parseLong(result.get("internalid").toString()));
				
			} else if ("P".equals(result.get("type").toString())) {
				
				fixPrescriptionFrequency(result, true);
				
				prescriptionIds.add(Long.parseLong(result.get("internalid").toString()));	
				activityOrPescriptionIds.add(Long.parseLong(result.get("internalid").toString()));
			}		
		}
		
		// Build query for executions and administrations
		String sqlErogation = null;	
		
		List<Map<String, Object>> resultsErogation = null;
		
		// EXECUTIONS
		if (activityIds.size() > 0) {
			
			sqlErogation = CALENDAR_REPORT_EXECUTION_SQL;
			
		}
		
		// ADMINISTRATIONS
		if (prescriptionIds.size() > 0) {
			
			if (sqlErogation == null) {
				sqlErogation = CALENDAR_REPORT_ADMINISTRATION_SQL;
			} else {
				sqlErogation += " UNION ALL" + CALENDAR_REPORT_ADMINISTRATION_SQL;
			}
		}
		
		if (sqlErogation != null) {
		
			if (!currentLang.equals("it")) {
				sqlErogation = sqlErogation.replace("lang_it", "lang_" + currentLang);
				sqlErogation = sqlErogation.replace("abbreviation_it", "abbreviation_" + currentLang);
			}
			
			Query qryErogation = ca.createHibernateNativeQuery(sqlErogation);
			
			// Setting query parameters			
			if (sqlErogation.contains(":activityIds"))
				qryErogation.setParameterList("activityIds", activityIds);
			
			if (sqlErogation.contains(":prescriptionIds"))
				qryErogation.setParameterList("prescriptionIds", prescriptionIds);
	
			qryErogation.setParameter("plannedDateFrom", plannedDateFrom);
			qryErogation.setParameter("plannedDateTo", plannedDateTo);
			
			qryErogation.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			resultsErogation = qryErogation.list();
			
			//fixErogations(resultsErogation, true);			
			resultsErogation = ErogationStatusFixer.fixErogations(resultsErogation, true);
		}
		
		// Set date columns
		Calendar columnDate = Calendar.getInstance();
		columnDate.setTime(plannedDateFrom);

		// Pages
		List<Integer> pageIndexes = new ArrayList<Integer>();
		Map<Integer, List<Date>> columnsPages = new LinkedHashMap<Integer,List<Date>>();
		List<Date> columns = new ArrayList<Date>();

		Integer pageIndex = 0;
		Integer index = 0;

		while (columnDate.getTime().before(plannedDateTo)) {
			columns.add(columnDate.getTime());
			columnDate.set(Calendar.DATE, columnDate.get(Calendar.DATE) + 1);
			index++;
			if(index == 4){
				// Save page
				pageIndexes.add(pageIndex);
				columnsPages.put(pageIndex, columns); 
				pageIndex++;
				// Reinitialize
				columns = new ArrayList<Date>();
				index = 0;
			}
		}

		if (index > 0){ // Save last page
			pageIndexes.add(pageIndex);
			columnsPages.put(pageIndex,columns);
		}
		
		List<Integer> pagesToRemove = new ArrayList<Integer>();
			
		// Show activity/prescription only if active in the showed page
		Map<Integer, List<Map<String, Object>>> activitiesOrPrescriptionsPages = new LinkedHashMap<Integer, List<Map<String, Object>>>();
		List<Map<String, Object>> activityOrPrescriptionList = new ArrayList<Map<String, Object>>();		
		
		for (Integer pageNumber : pageIndexes) { // Scroll pages	
			
			List<Date> columnsInPage = columnsPages.get(pageNumber); // Get start date and end date for each page
			
			Calendar start = Calendar.getInstance();
			start.setTime(columnsInPage.get(0));
			start.set(Calendar.HOUR, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			
			Calendar end = Calendar.getInstance();
			end.setTime(columnsInPage.get(columnsInPage.size() - 1));
			end.set(Calendar.HOUR, 23);
			end.set(Calendar.MINUTE, 59);
			end.set(Calendar.SECOND, 59);
			end.set(Calendar.MILLISECOND, 0);
			
			for (Map<String, Object> activityOrPrescription : results) {				
				if ((activityOrPrescription.get("enddate") == null || ((Date) activityOrPrescription.get("enddate")).getTime() >= start.getTimeInMillis()) && (((Date) activityOrPrescription.get("startdate")).getTime() <= end.getTimeInMillis())) {
					activityOrPrescriptionList.add(activityOrPrescription);
				}
			}	
			
			if (activityOrPrescriptionList.size() > 0) {
				activitiesOrPrescriptionsPages.put(pageNumber, activityOrPrescriptionList);
			} else {
				pagesToRemove.add(pageNumber);
			}
			
			activityOrPrescriptionList = new ArrayList<Map<String, Object>>();
		}
		
		// Remove unused pages
		for (Integer pageToRemove : pagesToRemove) {
			pageIndexes.remove(pageToRemove);
			columnsPages.remove(pageToRemove);
		}
		
		if (resultsErogation != null) {
			// Arrange erogations by activity/prescription and date 
			Map<Integer,Map<String,Map<String, List<Map<String, Object>>>>> erogationsPages = new LinkedHashMap<Integer,Map<String,Map<String, List<Map<String, Object>>>>>();
			Map<String,Map<String, List<Map<String, Object>>>> erogationList = new LinkedHashMap<String,Map<String, List<Map<String, Object>>>>();
			Map<String,List<Map<String, Object>>> row = new LinkedHashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> cell = new ArrayList<Map<String, Object>>();
				
			Calendar checkingDate = Calendar.getInstance();
			for (Integer pageNumber : pageIndexes) { // Scroll pages
				for (long activityOrPrescriptionId : activityOrPescriptionIds){ // Scroll rows
					columns = columnsPages.get(pageNumber);			
					for (Date date : columns) { // Scroll columns
						for (Map<String, Object> erogation : resultsErogation) {
							if (activityOrPrescriptionId == Long.parseLong(erogation.get("parentid").toString())) {
								if ((Date)erogation.get("planneddate") != null) {
									checkingDate.setTime((Date)erogation.get("planneddate"));
									checkingDate.set(Calendar.HOUR_OF_DAY,0);
									checkingDate.set(Calendar.MINUTE,0);
									checkingDate.set(Calendar.SECOND,0);
									checkingDate.set(Calendar.MILLISECOND,0);
									if (checkingDate.getTime().equals(date)) {//found
										cell.add(erogation);
									}
								}
							}
						}
						// Add cell
						row.put(String.valueOf(date.getTime()), cell);
						cell = new ArrayList<Map<String, Object>>();
					}
					erogationList.put(String.valueOf(activityOrPrescriptionId), row);
					row = new LinkedHashMap<String, List<Map<String, Object>>>();
				}
				erogationsPages.put(pageNumber,erogationList);
				erogationList = new LinkedHashMap<String,Map<String, List<Map<String, Object>>>>();
			}
			Contexts.getConversationContext().set("ErogationsPages", erogationsPages);
			
		} else {
			Contexts.getConversationContext().set("ErogationsPages", null);
		}
		
		Contexts.getConversationContext().set("PageIndexes", pageIndexes);
		Contexts.getConversationContext().set("ColumnsPages", columnsPages);
		Contexts.getConversationContext().set("ActivitiesOrPrescriptionsPages", activitiesOrPrescriptionsPages);		
	}
	
	// Function to read erogations (r_cart)	
	private void readCart(MultivaluedMap<String, String> restrictionMap) throws Exception {
					
		String userRoleCode =  UserBean.instance().getRole();
		String currentLang = Locale.instance().getLanguage();		
		
		Long sdlocId = null;		
		sdlocId = Long.parseLong(restrictionMap.get("sdlocId").get(0));
		
		String THERAPY_IDS_HQL = "";
			
		if ("10".equals(userRoleCode) || "12".equals(userRoleCode) || "21".equals(userRoleCode)) {			
			THERAPY_IDS_HQL = "SELECT therapy.internalId FROM PatientEncounter enc JOIN enc.therapy therapy LEFT JOIN enc.temporarySDL temporary LEFT JOIN enc.assignedSDL assigned WHERE ((temporary.internalId IS NULL AND assigned.internalId = :sdlocId) OR temporary.internalId = :sdlocId) AND enc.statusCode.code = 'active'";
		} else {
			THERAPY_IDS_HQL = "SELECT therapy.internalId FROM PatientEncounter enc JOIN enc.therapy therapy LEFT JOIN enc.assignedSDL assigned WHERE assigned.internalId = :sdlocId AND enc.statusCode.code = 'active'";
		}
			
		Query qryTherIds = ca.createHibernateQuery(THERAPY_IDS_HQL);
			
		qryTherIds.setParameter("sdlocId", sdlocId);
			
		List<Long[]> therapyIds = qryTherIds.list();	
				
		// Parsing restrictions 
		String patientNameFam = ""; 
		if (restrictionMap.get("patient.name.fam") != null) {
			patientNameFam = restrictionMap.get("patient.name.fam").get(0);
		}
		
		String patientNameGiv = ""; 
		if (restrictionMap.get("patient.name.giv") != null) {
			patientNameGiv = restrictionMap.get("patient.name.giv").get(0);
		}
		
		String room = ""; 
		if (restrictionMap.get("room") != null) {
			room = restrictionMap.get("room").get(0);
		}
		
		String bed = "";
		if (restrictionMap.get("bed") != null) {
			bed = restrictionMap.get("bed").get(0);
		}
				
		String routeCodeCode = "";
		if (restrictionMap.get("routeCode.code") != null) {
			routeCodeCode = restrictionMap.get("routeCode.code").get(0);			
		}
		
		String prescriptionCodeCode = "";
		if (restrictionMap.get("prescriptionCode.code") != null) {
			prescriptionCodeCode = restrictionMap.get("prescriptionCode.code").get(0);			
		}
		
		Date plannedDateFrom = null;
		if (restrictionMap.get("plannedDateFrom") != null) {
			String plannedDateFromStr = restrictionMap.get("plannedDateFrom").get(0);
			plannedDateFrom = decodeISO8601(plannedDateFromStr);			
		}
		
		Date plannedDateTo = null;
		if (restrictionMap.get("plannedDateTo") != null) {
			String plannedDateToStr = restrictionMap.get("plannedDateTo").get(0);
			plannedDateTo = decodeISO8601(plannedDateToStr);			
		}
		
		// Build queries
		if (!therapyIds.isEmpty()) {
			
			if (prescriptionCodeCode == null || prescriptionCodeCode == "" || prescriptionCodeCode.equals("PHARMA")){
				
				String medicineHql = MEDICINE_HQL;
				
				if (patientNameFam != null && patientNameFam != "") {
					medicineHql += " AND LOWER(patient.name.fam) like LOWER(:patientNameFam)";
				}
				
				if (patientNameGiv != null && patientNameGiv != "") {
					medicineHql += " AND LOWER(patient.name.giv) like LOWER(:patientNameGiv)";
				}
				
				if (room != null && room != "") {
					medicineHql += " AND LOWER(encounter.roomString) like LOWER(:room)";
				}
				
				if (bed != null && bed != "") {
					medicineHql += " AND LOWER(encounter.bedString) like LOWER(:bed)";
				}
	
				if (routeCodeCode != null && routeCodeCode != "") {
					medicineHql += " AND prescription.routeCode.code = :routeCodeCode";
				}
	
				if (plannedDateFrom != null) {
					medicineHql +=	" AND ((administration.plannedDate IS NULL AND administration.administratedDate.low >= :plannedDateFrom)" +
									" OR(administration.plannedDate IS NULL AND administration.administratedDate.low IS NULL AND (prescription.period.high IS NULL OR prescription.period.high > :sysdate) AND (prescription.validityPeriod.high IS NULL OR prescription.validityPeriod.high > :sysdate) AND :sysdate >= :plannedDateFrom)" +
									" OR(administration.plannedDate >= :plannedDateFrom))";
				}
				
				if (plannedDateTo != null) {
					medicineHql+=	" AND ((administration.plannedDate IS NULL AND administration.administratedDate.low <= :plannedDateTo)" +
									" OR (administration.plannedDate IS NULL AND administration.administratedDate.low IS NULL AND prescription.period.low < :sysdate AND prescription.validityPeriod.low < :sysdate AND :sysdate <= :plannedDateTo)" +
									" OR (administration.plannedDate <= :plannedDateTo))";		
				}		
				
				medicineHql += " GROUP BY medicine.name.giv, prescription.routeCode.code, prescription.routeCode.langIt, administration.plannedQuantity.unit, prescription.needsBased";
				medicineHql += " ORDER BY prescription.routeCode.code, medicine.name.giv";
								
				if (!currentLang.equals("it")) {
					medicineHql = medicineHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				}
				
				Query medicineQry = ca.createHibernateQuery(medicineHql);
				
				// Setting query parameters
				if (medicineHql.contains(":sysdate"))
					medicineQry.setParameter("sysdate", new Date());
								
				if (medicineHql.contains(":therapyId"))
					medicineQry.setParameterList("therapyId", therapyIds);
				
				if (medicineHql.contains(":patientNameFam"))
					medicineQry.setParameter("patientNameFam", patientNameFam + "%");
				
				if (medicineHql.contains(":patientNameGiv"))
					medicineQry.setParameter("patientNameGiv", patientNameGiv + "%");
				
				if (medicineHql.contains(":room"))
					medicineQry.setParameter("room", room);
				
				if (medicineHql.contains(":bed"))
					medicineQry.setParameter("bed", bed);
				
				if (medicineHql.contains(":routeCodeCode"))
					medicineQry.setParameter("routeCodeCode", routeCodeCode);
				
				if (medicineHql.contains(":plannedDateFrom"))
					medicineQry.setParameter("plannedDateFrom", plannedDateFrom);
				
				if (medicineHql.contains(":plannedDateTo"))
					medicineQry.setParameter("plannedDateTo", plannedDateTo);	
				
				medicineQry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
				List<Map<String, String>> medicineList = medicineQry.list();
				
				String asNeededMedicineHql = AS_NEEDED_MEDICINE_HQL;
				
				if (patientNameFam != null && patientNameFam != "") {
					asNeededMedicineHql += " AND LOWER(patient.name.fam) like LOWER(:patientNameFam)";
				}
				
				if (patientNameGiv != null && patientNameGiv != "") {
					asNeededMedicineHql += " AND LOWER(patient.name.giv) like LOWER(:patientNameGiv)";
				}
				
				if (room != null && room != "") {
					asNeededMedicineHql += " AND LOWER(encounter.roomString) like LOWER(:room)";
				}
				
				if (bed != null && bed != "") {
					asNeededMedicineHql += " AND LOWER(encounter.bedString) like LOWER(:bed)";
				}
	
				if (routeCodeCode != null && routeCodeCode != "") {
					asNeededMedicineHql += " AND prescription.routeCode.code = :routeCodeCode";
				}
	
				if (plannedDateFrom != null) {
					asNeededMedicineHql +=	" AND ((administration.plannedDate IS NULL AND administration.administratedDate.low >= :plannedDateFrom)" +
											" OR(administration.plannedDate IS NULL AND administration.administratedDate.low IS NULL AND (prescription.period.high IS NULL OR prescription.period.high > :sysdate) AND (prescription.validityPeriod.high IS NULL OR prescription.validityPeriod.high > :sysdate) AND :sysdate >= :plannedDateFrom)" +
											" OR(administration.plannedDate >= :plannedDateFrom))";
				}
				
				if (plannedDateTo != null) {
					asNeededMedicineHql +=	" AND ((administration.plannedDate IS NULL AND administration.administratedDate.low <= :plannedDateTo)" +
											" OR (administration.plannedDate IS NULL AND administration.administratedDate.low IS NULL AND prescription.period.low < :sysdate AND prescription.validityPeriod.low < :sysdate AND :sysdate <= :plannedDateTo)" +
											" OR (administration.plannedDate <= :plannedDateTo))";		
				}		
				
				asNeededMedicineHql += " GROUP BY medicine.name.giv, prescription.routeCode.code, prescription.routeCode.langIt, administration.plannedQuantity.unit, prescription.needsBased";
				asNeededMedicineHql += " ORDER BY prescription.routeCode.code, medicine.name.giv";
				
				if (!currentLang.equals("it")) {
					asNeededMedicineHql = asNeededMedicineHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
				}
				
				Query asNeededMedicineQry = ca.createHibernateQuery(asNeededMedicineHql);
				
				// Setting query parameters
				if (asNeededMedicineHql.contains(":sysdate"))
					asNeededMedicineQry.setParameter("sysdate", new Date());
								
				if (asNeededMedicineHql.contains(":therapyId"))
					asNeededMedicineQry.setParameterList("therapyId", therapyIds);
				
				if (asNeededMedicineHql.contains(":patientNameFam"))
					asNeededMedicineQry.setParameter("patientNameFam", patientNameFam + "%");
				
				if (asNeededMedicineHql.contains(":patientNameGiv"))
					asNeededMedicineQry.setParameter("patientNameGiv", patientNameGiv + "%");
				
				if (asNeededMedicineHql.contains(":room"))
					asNeededMedicineQry.setParameter("room", room);
				
				if (asNeededMedicineHql.contains(":bed"))
					asNeededMedicineQry.setParameter("bed", bed);
				
				if (asNeededMedicineHql.contains(":routeCodeCode"))
					asNeededMedicineQry.setParameter("routeCodeCode", routeCodeCode);
				
				if (asNeededMedicineHql.contains(":plannedDateFrom"))
					asNeededMedicineQry.setParameter("plannedDateFrom", plannedDateFrom);
				
				if (asNeededMedicineHql.contains(":plannedDateTo"))
					asNeededMedicineQry.setParameter("plannedDateTo", plannedDateTo);	
				
				asNeededMedicineQry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
				List<Map<String, String>> asNeededMedicineList = asNeededMedicineQry.list();
				
				Contexts.getConversationContext().set("MedicineList", medicineList);
				Contexts.getConversationContext().set("AsNeededMedicineList", asNeededMedicineList);					
			} else {
				Contexts.getConversationContext().set("MedicineList", null);
				Contexts.getConversationContext().set("AsNeededMedicineList", null);
			}
			
			if (prescriptionCodeCode == null || prescriptionCodeCode == "" || prescriptionCodeCode.equals("INFU")){
				
				String infusionSql = SOLUTION_SQL;
				
				if (patientNameFam != null && patientNameFam != "") {
					infusionSql += " AND LOWER(patient.name_fam) like LOWER(:patientNameFam)";
				}
				
				if (patientNameGiv != null && patientNameGiv != "") {
					infusionSql += " AND LOWER(patient.name_giv) like LOWER(:patientNameGiv)";
				}
				
				if (room != null && room != "") {
					infusionSql += " AND LOWER(encounter.room_string) like LOWER(:room)";
				}
				
				if (bed != null && bed != "") {
					infusionSql += " AND LOWER(encounter.bed_string) like LOWER(:bed)";
				}
	
				if (routeCodeCode != null && routeCodeCode != "") {
					infusionSql += " AND routeCode.code = :routeCodeCode";
				}
	
				if (plannedDateFrom != null) {
					infusionSql +=	" AND ((administration.planned_date IS NULL AND administration.administrateddate_time_low >= :plannedDateFrom)" +
									" OR(administration.planned_date IS NULL AND administration.administrateddate_time_low IS NULL AND (prescription.period_time_high IS NULL OR prescription.period_time_high > :sysdate) AND (prescription.period_time_high IS NULL OR prescription.validity_period_time_high > :sysdate) AND :sysdate >= :plannedDateFrom)" +
									" OR(administration.planned_date >= :plannedDateFrom))";
				}
				
				if (plannedDateTo != null) {
					infusionSql+=	" AND ((administration.planned_date IS NULL AND administration.administrateddate_time_low <= :plannedDateTo)" +
									" OR (administration.planned_date IS NULL AND administration.administrateddate_time_low IS NULL AND prescription.period_time_low < :sysdate AND prescription.validity_period_time_low < :sysdate AND :sysdate <= :plannedDateTo)" +
									" OR (administration.planned_date <= :plannedDateTo))";		
				}		
								
				infusionSql += " GROUP BY solution.lang_it, routeCode.lang_it, prescriptionCode.code";
				
				infusionSql += " UNION ALL";
				
				infusionSql += 	" SELECT" +
								" component.name as name," +
								" component.route as route," +
								" SUM(component.toErogate) || ' ' || component.toErogateUM as toErogate," +
								" SUM(component.erogated) || ' ' || component.erogatedUM as erogated" +
								" FROM (";
				
				infusionSql += COMPONENT_SQL;
				
				if (patientNameFam != null && patientNameFam != "") {
					infusionSql += " AND LOWER(patient.name_fam) like LOWER(:patientNameFam)";
				}
				
				if (patientNameGiv != null && patientNameGiv != "") {
					infusionSql += " AND LOWER(patient.name_giv) like LOWER(:patientNameGiv)";
				}
				
				if (room != null && room != "") {
					infusionSql += " AND LOWER(encounter.room_string) like LOWER(:room)";
				}
				
				if (bed != null && bed != "") {
					infusionSql += " AND LOWER(encounter.bed_string) like LOWER(:bed)";
				}
	
				if (routeCodeCode != null && routeCodeCode != "") {
					infusionSql += " AND routeCode.code = :routeCodeCode";
				}
	
				if (plannedDateFrom != null) {
					infusionSql +=	" AND ((administration.planned_date IS NULL AND administration.administrateddate_time_low >= :plannedDateFrom)" +
									" OR(administration.planned_date IS NULL AND administration.administrateddate_time_low IS NULL AND (prescription.period_time_high IS NULL OR prescription.period_time_high > :sysdate) AND (prescription.period_time_high IS NULL OR prescription.validity_period_time_high > :sysdate) AND :sysdate >= :plannedDateFrom)" +
									" OR(administration.planned_date >= :plannedDateFrom))";
				}
				
				if (plannedDateTo != null) {
					infusionSql+=	" AND ((administration.planned_date IS NULL AND administration.administrateddate_time_low <= :plannedDateTo)" +
									" OR (administration.planned_date IS NULL AND administration.administrateddate_time_low IS NULL AND prescription.period_time_low < :sysdate AND prescription.validity_period_time_low < :sysdate AND :sysdate <= :plannedDateTo)" +
									" OR (administration.planned_date <= :plannedDateTo))";		
				}	
				
				infusionSql += " GROUP BY medicine.name_giv, routeCode.lang_it, prescriptionMedicine.dosage, prescriptionCode.code";
				
				infusionSql += 	" ) component" +
								" GROUP BY component.name, component.route, component.toErogateUM, component.erogatedUM" +
								" ORDER BY route, name";
				
				if (!currentLang.equals("it")) {
					infusionSql = infusionSql.replace("lang_it", "lang_" + currentLang);
				}
								
				Query infusionQry = ca.createHibernateNativeQuery(infusionSql);
				
				// Setting query parameters
				if (infusionSql.contains(":sysdate"))
					infusionQry.setParameter("sysdate", new Date());
								
				if (infusionSql.contains(":therapyId"))
					infusionQry.setParameterList("therapyId", therapyIds);
				
				if (infusionSql.contains(":patientNameFam"))
					infusionQry.setParameter("patientNameFam", patientNameFam + "%");
				
				if (infusionSql.contains(":patientNameGiv"))
					infusionQry.setParameter("patientNameGiv", patientNameGiv + "%");
				
				if (infusionSql.contains(":room"))
					infusionQry.setParameter("room", room);
				
				if (infusionSql.contains(":bed"))
					infusionQry.setParameter("bed", bed);
				
				if (infusionSql.contains(":routeCodeCode"))
					infusionQry.setParameter("routeCodeCode", routeCodeCode);
				
				if (infusionSql.contains(":plannedDateFrom"))
					infusionQry.setParameter("plannedDateFrom", plannedDateFrom);
				
				if (infusionSql.contains(":plannedDateTo"))
					infusionQry.setParameter("plannedDateTo", plannedDateTo);	
				
				infusionQry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
				List<Map<String, String>> infusionList = infusionQry.list();
									
				Contexts.getConversationContext().set("InfusionList", infusionList);
			} else {
				Contexts.getConversationContext().set("InfusionList", null);				
			}
		}		
	}

			
	@GET
	@Path("asNeededDetails/{prescriptionId}/{encounterId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response asNeededPresDetails(@PathParam("prescriptionId") long prescriptionId, @PathParam("encounterId") long encounterId){
		try {			
			String asNeededPresHql = AS_NEEDED_PRES_HQL;
			
			Query qryAsNeededPres = ca.createHibernateQuery(asNeededPresHql);
			
			qryAsNeededPres.setParameter("prescriptionId", prescriptionId);
			
			qryAsNeededPres.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> resultThreshold = (Map<String, Object>) qryAsNeededPres.uniqueResult();
			
			Map<String, Object> lastVitalSigns = new HashMap<String, Object>();
			
			List<Map<String, Object>> resultVitalSigns = new ArrayList<Map<String, Object>>();
			
			String vitalSignsHql = "";
			/*
					" SELECT" +
					" vitalSign.evaluationDate as evaluationDate,";
			 */
			String vitalSignsWhere = " AND (";
			
			if (resultThreshold.get("systolicpressure") == null){
				resultThreshold.remove("systolicpressure");
			} else {
				vitalSignsHql +=
					" vitalSign.systolic.value as systolicPressure,";
				vitalSignsWhere += " vitalSign.systolic.value IS NOT null OR";
				lastVitalSigns.put("systolicpressure", null);
			}
			if (resultThreshold.get("diastolicpressure") == null){
				resultThreshold.remove("diastolicpressure");
			} else {
				vitalSignsHql +=
					" vitalSign.diastolic.value as diastolicPressure,";
				vitalSignsWhere += " vitalSign.diastolic.value IS NOT null OR";
				lastVitalSigns.put("diastolicpressure", null);
			}
			if (resultThreshold.get("temperature") == null){
				resultThreshold.remove("temperature");
			} else  {
				vitalSignsHql +=
					" vitalSign.bodyTemperature.value as temperature,";
				vitalSignsWhere += " vitalSign.bodyTemperature.value IS NOT null OR";
				lastVitalSigns.put("temperature", null);
			}
			if (resultThreshold.get("glycemia") == null){
				resultThreshold.remove("glycemia");
			} else {
				vitalSignsHql +=
					" vitalSign.glycemia.value as glycemia,";
				vitalSignsWhere += " vitalSign.glycemia.value IS NOT null OR";
				lastVitalSigns.put("glycemia", null);
			}
			if (resultThreshold.get("diuresis") == null){
				resultThreshold.remove("diuresis");
			} else {
				vitalSignsHql +=
					" vitalSign.urineStick.value as diuresis,";
				vitalSignsWhere += " vitalSign.urineStick.value IS NOT null OR";
				lastVitalSigns.put("diuresis", null);
			}
			if (resultThreshold.get("pain") == null){
				resultThreshold.remove("pain");
			} else {
				vitalSignsHql +=
					" vitalSign.pain.value as pain,";
				vitalSignsWhere += " vitalSign.pain.value IS NOT null OR";
				lastVitalSigns.put("pain", null);
			}
			if (resultThreshold.get("heartrate") == null){
				resultThreshold.remove("heartrate");
			} else {
				vitalSignsHql +=
					" vitalSign.cardiacFrequency.value as heartRate,";
				vitalSignsWhere += " vitalSign.cardiacFrequency.value IS NOT null OR";
				lastVitalSigns.put("heartrate", null);
			}
			if (resultThreshold.get("spo2") == null){
				resultThreshold.remove("spo2");
			} else {
				vitalSignsHql +=
					" vitalSign.o2Saturation.value as spo2,";
				vitalSignsWhere += " vitalSign.o2Saturation.value IS NOT null OR";
				lastVitalSigns.put("spo2", null);
			}
			
			if (vitalSignsHql!="") {
				vitalSignsHql = " SELECT" +
					 		 	" vitalSign.evaluationDate as evaluationDate," +
					 		 	vitalSignsHql;
					
				//  Remove last comma
				vitalSignsHql = vitalSignsHql.substring(0, vitalSignsHql.length() - 1);	
				
				// Remove last OR
				vitalSignsWhere = vitalSignsWhere.substring(0, vitalSignsWhere.length() - 3);
				vitalSignsWhere += ")";
				
				vitalSignsHql +=
						" FROM " +
						" VitalSign vitalSign" +
						" WHERE vitalSign.patientEncounter.internalId = :encounterId" +
						" AND vitalSign.isActive = true" +
						" AND vitalSign.confirmed = true" +
						vitalSignsWhere +
						" ORDER BY vitalSign.evaluationDate DESC";
									
				Query qryVitalSigns = ca.createHibernateQuery(vitalSignsHql);
				
				qryVitalSigns.setParameter("encounterId", encounterId);
				
				qryVitalSigns.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
							
				resultVitalSigns = qryVitalSigns.list();
			}
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			result.put("thresholds", resultThreshold);
			
			result.put("vitalsigns", getLastVitalSigns(resultVitalSigns, lastVitalSigns));
						
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest asNeededPresDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest asNeededPresDetails").build();
		}
	}
	
	
	@GET
	@Path("prescriptionDetails/{prescriptionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response prescriptionDetails(@PathParam("prescriptionId") long prescriptionId){
		try {			
			String prescriptionDetailsSql = PRESCRIPTION_DETAILS_SQL;
	        
			String currentLang = Locale.instance().getLanguage();
			
			if (!currentLang.equals("it")) {
				prescriptionDetailsSql = prescriptionDetailsSql.replace("lang_it", "lang_" + currentLang);
			}
			
			// Replace LISTAGG
			prescriptionDetailsSql = ReplaceListaggBean.replaceListagg(prescriptionDetailsSql);
			
			Query qryPrescriptionDetails = ca.createHibernateNativeQuery(prescriptionDetailsSql);
			
			qryPrescriptionDetails.setParameter("prescriptionId", prescriptionId);
			
			qryPrescriptionDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryPrescriptionDetails.uniqueResult();
			
			fixPrescriptionFrequency(result, false);
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest prescriptionDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest prescriptionDetails").build();
		}
	}
	
	
	@GET
	@Path("activityDetails/{activityId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activityDetails(@PathParam("activityId") long activityId){
		try {			

			String currentLang = Locale.instance().getLanguage();
			
			String prescriptionDetailsSql = ACTIVITY_DETAILS_SQL;
			
			if (!currentLang.equals("it")) {
				prescriptionDetailsSql = prescriptionDetailsSql.replace("lang_it", "lang_" + currentLang);
			}
			
			// Replace LISTAGG
			prescriptionDetailsSql = ReplaceListaggBean.replaceListagg(prescriptionDetailsSql);
			
			Query qryActivityDetails = ca.createHibernateNativeQuery(prescriptionDetailsSql);
			
			qryActivityDetails.setParameter("activityId", activityId);
			
			qryActivityDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryActivityDetails.uniqueResult();
			
			FunctionsBean fb = FunctionsBean.instance();
			
			BigDecimal truez = new BigDecimal(1);
						
			if (result.get("frequency") == null) {
				if (truez.equals(result.get("extemporaneous"))){
					result.put("frequency", fb.getStaticTranslation("Extemporaneous"));
				} else {				
					String dayinterval = (String)result.remove("dayinterval");
					String daytime = (String)result.remove("daytime");	
					
					result.put("frequency", fb.getStaticTranslation("Every") + " " + dayinterval + " " + fb.getStaticTranslation("day/s") + " " + fb.getStaticTranslation("at_hour") + " " + daytime);
				}
			}

			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest activityDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest activityDetails").build();
		}
	}
	
	
	@GET
	@Path("administrationDetails/{administrationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response administrationDetails(@PathParam("administrationId") long administrationId){
		try {			

			Query qryAdministrationDetails = ca.createHibernateQuery(ADMINISTRATION_DETAILS_HQL);
			
			qryAdministrationDetails.setParameter("adminId", administrationId);
			
			qryAdministrationDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryAdministrationDetails.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest administrationDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest administrationDetails").build();
		}
	}
	
	
	@GET
	@Path("executionDetails/{executionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response executionDetails(@PathParam("executionId") long executionId){
		try {			
			String currentLang = Locale.instance().getLanguage();
			
			String executionDetailsHql = EXECUTION_DETAILS_HQL;
						
			if (!currentLang.equals("it")) {
				executionDetailsHql = executionDetailsHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
			}
			
			Query qryExecutionDetails = ca.createHibernateQuery(executionDetailsHql);
			
			qryExecutionDetails.setParameter("exeId", executionId);
			
			qryExecutionDetails.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryExecutionDetails.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error NurseActivityRest executionDetails", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error NurseActivityRest executionDetails").build();
		}
	}
	
	
	
	/*
	 * UTILITY FUNCTIONS
	 
	
	// Function to fix the status and the possible actions of erogations
	private void fixErogations(List<Map<String, Object>> erogations, boolean report) {
			
		long userId = UserBean.instance().getCurrentEmployee().getInternalId();
		String userRoleCode =  UserBean.instance().getRole();
		Date now = new Date();
		
		int lowGap; 
		int lateGap; 
		int earlyGap; 
		int highGap;
		
		for (Map<String, Object> erogation : erogations) {
			
			if (erogation.get("quantity") != null) {
				String quantity = erogation.get("quantity").toString().replace(".", ",");
			
				if (quantity.startsWith(","))
					quantity = quantity.replaceFirst(",", "0,");
				
				erogation.put("quantity", quantity.replace(" ,", " 0,"));
			}

			Character type = (Character)erogation.get("type");
			
			if (type.toString().equals("E")) {
				lowGap = EXECUTION_LOW_GAP;
				lateGap = EXECUTION_LATE_GAP;
				earlyGap = EXECUTION_EARLY_GAP;
				highGap = EXECUTION_HIGH_GAP;
			} else { //S
				lowGap = ADMINISTRATION_LOW_GAP;
				lateGap = ADMINISTRATION_LATE_GAP;
				earlyGap = ADMINISTRATION_EARLY_GAP;
				highGap = ADMINISTRATION_HIGH_GAP;
			}
		
			String statusCode = (String)erogation.remove("statuscode");
			String statusDetailsCode = (String)erogation.remove("statusdetailscode");
			
			Date plannedDate = (Date)erogation.get("planneddate");
			Date erogationDate = (Date)erogation.get("erogationdate");
			Date endDate = (Date)erogation.get("enddate");
			
			if (!report) {
				erogation.put("erogable", false);
				erogation.put("erogated", false);
				erogation.put("erogablefast", false);
				erogation.put("removable", false);
			}
			
			if (PLANNED.equals(statusCode)) {
				
				if (plannedDate == null) {//is as needed
					
					// Check if prescription is still active
					// This check should be unuseful because is already made from the query, 
					// that returns the planned asNeeded administration only for active prescriptions
					if (endDate == null || endDate.after(now)) {
						erogation.put("status", DUE);	
						if (!report) {
							erogation.put("erogable", true);
						}
					} else {						
						erogation.put("status", CANCELLED);
					}
					
				} else if (plannedDate.getTime() + highGap *  MINUTE_IN_MILLISECONDS < now.getTime()) {
					
					erogation.put("status", MISSED);
					if (!report) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() + lateGap * MINUTE_IN_MILLISECONDS < now.getTime()) {
					
					erogation.put("status", OVERDUE);
					if (!report) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS <= now.getTime()) {
					
					erogation.put("status", DUE);
					if (!report) {
						erogation.put("erogable", true);
						erogation.put("erogablefast", true);
					}
					
				} else if (plannedDate.getTime() - lowGap * MINUTE_IN_MILLISECONDS <= now.getTime()) {
					
					erogation.put("status", DUE_FUTURE_ENABLED);
					if (!report) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() - lowGap * MINUTE_IN_MILLISECONDS > now.getTime()) {
					
					erogation.put("status", DUE_FUTURE_DISABLED);
					
				}
	
			} else if (DONE.equals(statusCode)) {
				
				if (plannedDate == null) {//is as needed
					erogation.put("status", EROGATED_AS_PLANNED);
				} else if (plannedDate.getTime() + lateGap * MINUTE_IN_MILLISECONDS < erogationDate.getTime()) {
					erogation.put("status", EROGATED_LATE);
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS <= erogationDate.getTime()) {
					erogation.put("status", EROGATED_AS_PLANNED);
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS > erogationDate.getTime()) {
					erogation.put("status", EROGATED_EARLY);
				}
				
				if (!report) {
					erogation.put("erogated", true);
				}
								
			} else if (EXCEPTION.equals(statusCode)) {
				
				if ("WDRU".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_WRONG_DRUG);
				} else if ("WDOSE".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_WRONG_DOSE);
				} else if ("OCRE".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_OTHER_REASON);
				} else {
					erogation.put("status", EXCEPTION);
				}
				
				if (!report) {
					erogation.put("erogated", true);
				}
				
			} else if (UNSUCCESSFUL.equals(statusCode)) {
				
				if ("DUNA".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_DRUG_UNAVAILABLE);
				} else if ("NBMP".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_NIL_BY_MOUTH);
				} else if ("PABS".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_PATIENT_ABSENT);
				} else if ("PREF".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_PATIENT_REFUSED);
				} else if ("OCRE".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_OTHER_REASON);
				} else {
					erogation.put("status", UNSUCCESSFUL);
				}
				
				if (!report) {
					erogation.put("erogated", true);
				}
				
			} else if (MISSED.equals(statusCode)) {
				
				erogation.put("status", MISSED);
				
				if (!report) {
					erogation.put("erogable", true);
				}
				
			} else if (CANCELLED.equals(statusCode)) { 	// This part should be unuseful because is already made from the query, 
				
				erogation.put("status", CANCELLED);				
			}
					
			//Date pre = new Date(now.getTime() - lowGap * MINUTE_IN_MILLISECONDS);
			//Date post = new Date(now.getTime() + highGap * MINUTE_IN_MILLISECONDS);
			if (!report) {
				Boolean erogated = (Boolean)erogation.get("erogated");
				
				Long authorId = null;
				if (erogation.get("author") != null) {
					authorId = Long.parseLong(erogation.get("author").toString());
				}
				
				if (plannedDate == null) {//is as needed
				
					if (erogated && authorId == userId && erogation.get("cancellationdate") == null) {
						erogation.put("removable", true);
					}
				} else {
					if (erogated && authorId == userId && erogation.get("cancellationdate") == null) {
						erogation.put("removable", true);
					}
				}
					
				if (type.toString().equals("E")) {
									
					Map<String, Object> activity = (Map<String, Object>) erogation.get("parent");
					
					Number objective = (Number)erogation.get("objectiveid");
					
					//O.S.S. = 12: only executions where is responsible are erogable/erogablefast/removable
					if (!userRoleCode.equals(activity.get("responsiblerole")) && userRoleCode.equals("12")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
					
					//MEDICO = 11: executions are not erogable/erogablefast/removable
					//SEGRETARIO DI REPARTO = 28: executions are not erogable/erogablefast/removable
					if (userRoleCode.equals("11") || userRoleCode.equals("28")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
					
					activity.remove("responsiblerole");			
					
					// Evaluation of an objective is not erogablefast
					if (objective != null) {
						erogation.put("erogablefast", false);
					}
				}				
				else
				{
					// SEGRETARIO DI REPARTO = 28: administrations are not erogable/erogablefast/removable
					if (userRoleCode.equals("28")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
				}
			}
		}
	}
	*/
	
	// Function to fix the activity frequency
	private void fixActivityFrequency(Map<String, Object> activity, boolean report) {
		
		// repeated: 1@08:00|1@18:00
		// extemporaneous: 1@08:00
		// asNeeded: not used yet
		String posology = (String)activity.remove("posology");
		// null
		String velocity = (String)activity.remove("velocity");
		
		// This string will contain the resulting frequency String
		String quantity = ""; 
		
		BigDecimal truez = new BigDecimal(1); 			
		FunctionsBean fb = FunctionsBean.instance();
		
		if (truez.equals(activity.get("needsbased"))) {
		
			quantity = fb.getStaticTranslation("needsbased"); 
			
		} else if (truez.equals(activity.get("extemporaneous"))) {
						
			quantity = fb.getStaticTranslation("extemporaneous");
			
		} else {

			String[] dosagesString = posology.split("\\|");
			
			ArrayList<Object> dosages = new ArrayList<Object>();
			Map<String, String> dosage = new HashMap<String, String>();
						
			for (int i = 0; i < dosagesString.length; i++) {
				
				String dosageString = dosagesString[i];						
				
				dosage = new HashMap<String, String>();
				dosage.put("dayGap", dosageString.substring(0, dosageString.indexOf("@")));
				dosage.put("time", dosageString.substring(dosageString.indexOf("@") + 1));				
				
				dosages.add(dosage);							
			}
							
			Map<String, String> firstDosage = (Map<String, String>) dosages.get(0); 						
			
			quantity = fb.getStaticTranslation("Every") + " " + firstDosage.get("dayGap") + " " + fb.getStaticTranslation("day/s") + " ";
				
			for (int i = 0; i < dosages.size(); i++)
			{
				Map<String, String> currentDosage = (Map<String, String>) dosages.get(i);
				// Add spaces before '['
				quantity += fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
			}
				
			// Remove last comma
			quantity = quantity.substring(0, quantity.length() - 2);
		}		
		activity.put("quantity", quantity.replace(".", ",").replace(" ,", " 0,"));		
	}
	
	//FIXME: merge with DrugPrescriberRest.fixFrequency(Map<String, Object> prescription, boolean report)
	// Function to fix prescription frequency
	private void fixPrescriptionFrequency(Map<String, Object> prescription, boolean report) {
		
		// PHARMA or INFU
		String code = (String)prescription.get("code");
		// PHARMA repeated: 1@08:00=2[CPS]|1@18:00=3[CPS]
		// PHARMA extemporaneous: 1@08:00=2[CPS]
		// PHARMA asNeeded: 12[CPS]
		// INFU with components: 1@08:00=2[FL]|1@18:00=2[FL],1@08:00=3[CP]|1@18:00=3[CP],1@08:00=7[FL]|1@18:00=7[FL],... 
		// INFU without components: 1@08:00|1@18:00
		String posology = (String)prescription.remove("posology");
		// PHARMA: null
		// INFU: 50[ml]@25[ml/h]
		String velocity = (String)prescription.remove("velocity");
		// otto diciotto or null
		String templateString = (String)prescription.remove("templatestring");
		
		// This string will contain the resulting frequency String
		String quantity = ""; 
		
		String dayGap = "";
		String time = "";
		String dose = "";
				
		BigDecimal truez = new BigDecimal(1); 			
		FunctionsBean fb = FunctionsBean.instance();
							
		if ("INFU".equals(code)) {
			
			if (velocity != null) {
				velocity = velocity.replace("@", " " + fb.getStaticTranslation("at") + " ") + " ";
				// Add spaces before '['
				velocity = velocity.replace("[", " [");
			}
			if (posology != null) {
				// Remove redondant
				if (posology.contains(",")) {
					posology = posology.substring(0, posology.indexOf(","));
				}
			}			
			if (truez.equals(prescription.get("needsbased"))) {
				quantity = velocity + " " + fb.getStaticTranslation("needsbased"); 
				prescription.put("frequency", fb.getStaticTranslation("needsbased"));
			} else if (truez.equals(prescription.get("continuous"))) {
				quantity = velocity + " " + fb.getStaticTranslation("continuous");
				prescription.put("frequency", fb.getStaticTranslation("continuous"));
			} else {
			String[] timesString = posology.split("\\|");
			ArrayList<String> times = new ArrayList<String>();
			
			for (int i = 0; i < timesString.length; i++) {	
				
				time = timesString[i];
									
				dayGap = time.substring(0, time.indexOf("@"));
				
				time = time.substring(time.indexOf("@") + 1);
				
				// Infusion with components 
				if (time.indexOf("=") != -1) {
					time = time.substring(0, time.indexOf("="));
					
				}				
				// Now in times there are String similar to 08:00, 12:00, ...		
				
				times.add(time);
			}
			
			if (!report && templateString != null) {
				
				quantity = velocity + " " + templateString;
			
			} else {
				
				quantity = fb.getStaticTranslation("Every") + " " + dayGap + " " + fb.getStaticTranslation("day/s") + " ";
				quantity += velocity + " " + fb.getStaticTranslation("at_hour") + " ";
				
				for (int i = 0; i < times.size(); i++) {
					quantity += times.get(i) + ", ";
				}
				
				// Remove last comma
				quantity = quantity.substring(0, quantity.length() - 2);
				}
			}
			
		}else if ("PHARMA".equals(code)) {
			
			if (truez.equals(prescription.get("needsbased"))) {

				// Add spaces before '['
				quantity = posology.replace("[", " [") + " " + fb.getStaticTranslation("needsbased"); 
				prescription.put("frequency", fb.getStaticTranslation("needsbased"));
			} else if (truez.equals(prescription.get("extemporaneous"))) {
				
				dose = posology.substring(posology.indexOf("=") + 1);					
				// Add spaces before '['
				quantity = dose.replace("[", " [") + " " + fb.getStaticTranslation("extemporaneous");
				prescription.put("frequency", fb.getStaticTranslation("extemporaneous"));
				
			} else if (truez.equals(prescription.get("urgent"))) {

				dose = posology.substring(posology.indexOf("=") + 1);					
				// Add spaces before '['
				quantity = dose.replace("[", " [") + " " + fb.getStaticTranslation("urgent");
				prescription.put("frequency", fb.getStaticTranslation("urgent"));
				
			} else {
				
				String[] dosagesString = posology.split("\\|");
				
				ArrayList<Object> dosages = new ArrayList<Object>();
				Map<String, String> dosage = new HashMap<String, String>();
				
				String previousDose = "";
				boolean differentDoses = false;
				
				for (int i = 0; i < dosagesString.length; i++) {
					
					String dosageString = dosagesString[i];						
					
					dosage = new HashMap<String, String>();

					dosage.put("dayGap", dosageString.substring(0, dosageString.indexOf("@")));
					dosage.put("time", dosageString.substring(dosageString.indexOf("@") + 1, dosageString.indexOf("=")));
					dosage.put("dose", dosageString.substring(dosageString.indexOf("=") + 1));
					
					dosages.add(dosage);
					
					if (!previousDose.equals("") && !previousDose.equals(dosage.get("dose"))){
						differentDoses = true;
					}
					
					previousDose = dosage.get("dose");					
				}
				
				if (!report && templateString != null && !differentDoses) {
					
					Map<String, String> firstDosage = (Map<String, String>) dosages.get(0); 						
					// Add spaces before '['
					quantity = firstDosage.get("dose").replace("[", " [")  + " " + templateString;
				
				} else {
					
					Map<String, String> firstDosage = (Map<String, String>) dosages.get(0); 						
					quantity = fb.getStaticTranslation("Every") + " " + firstDosage.get("dayGap") + " " + fb.getStaticTranslation("day/s") + " ";
					
					for (int i = 0; i < dosages.size(); i++)
					{
						Map<String, String> currentDosage = (Map<String, String>) dosages.get(i);
						// Add spaces before '['
						quantity += currentDosage.get("dose").replace("[", " [") + " " + fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
					}
					
					// Remove last comma
					quantity = quantity.substring(0, quantity.length() - 2);
					
				}
			}				
		}
		
		prescription.put("quantity", quantity.replace(".", ",").replace(" ,", " 0,"));

	}	
	
	// Function to get the last vital signs
	private Map<String, Object> getLastVitalSigns(List<Map<String, Object>> vitalSigns, Map<String, Object> lastVitalSigns){
		
		int currentLength = 0;
				
		for (Map<String, Object> vitalSign : vitalSigns) {			
			for (String key : vitalSign.keySet()){					
				if (!key.equals("evaluationdate") && vitalSign.get(key) != null && !"".equals(vitalSign.get(key)) && lastVitalSigns.get(key) == null){					
					Map<String, Object> lastVitalSign = new HashMap<String, Object>();
					lastVitalSign.put("value", vitalSign.get(key));
					lastVitalSign.put("evaluationdate", vitalSign.get("evaluationdate"));
					lastVitalSigns.put(key, lastVitalSign);
					currentLength++;
					if (currentLength == lastVitalSigns.size())
					{
						return lastVitalSigns;
					}
				}				
			}
		}	
		
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		
		return lastVitalSigns;
	}

	public List<Map<String, Object>> readMissedAdministrations(HashMap<String, String> restrictionsMap, int page) throws Exception {
		return readAdministrationsAndExecutions(restrictionsMap, page, false, true, ErogationStatusFixer.MISSED);
	}
	
	public List<Map<String, Object>> readDueAdministrations(HashMap<String, String> restrictionsMap, int page) throws Exception {
		return readAdministrationsAndExecutions(restrictionsMap, page, false, true, ErogationStatusFixer.DUE);
	}
	
}
