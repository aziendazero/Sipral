package com.phi.rest.dashboard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
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
import com.phi.entities.actions.TransferPrivacy;
import com.phi.events.PhiEvent;
import com.phi.rest.action.ErogationStatusFixer;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;


/**
 * ActivtyPrescriber Dashboard rest methods init and refresh
 * 
 * Example:
 * http://localhost:8080/PHI_CI/resource/rest/nurseactivities/init/encounterId=18;therapyId=35/1
 *  
 * 
 * @author simone.zancanaro
 */
@Restrict("#{identity.isLoggedIn(false)}")
@Name("CarePlanRest")
@Path("/careplan")
public class CarePlanRest extends BaseDashboardRest {
	
	private static final long serialVersionUID = -2092375843841583137L;
	
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
	
	private static final int MINUTE_IN_MILLISECONDS = 1000*60;
		
	private static final int EXECUTION_LOW_GAP 			= 60;
	private static final int EXECUTION_EARLY_GAP 		= 30;
	private static final int EXECUTION_LATE_GAP 		= 30;
	private static final int EXECUTION_HIGH_GAP 		= 60;
	*/

	
	private static final String DIAGNOSES_SELECT_SQL = 
			" SELECT" +
			" diagnosis.internal_id as internalId," +
			" CASE" +
			"   WHEN nandaCode.code IS NOT NULL THEN (diagnosis.prog_number || ' - ' || nandaCode.code || ' - ' || nandaCode.lang_it)" +
			"   WHEN nandaCode.code IS NULL THEN (diagnosis.prog_number || ' - ' || diagnosis.title_diag)" +
			" END as title," +
			" (UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(diagnosis.creation_date, 'DD/MM/YYYY HH24:MI')) as creationDetails," +
			" diagnosis.actNanda_date as startDate," +
			" riskCode.code as riskCode," +
			" typePCP.code as PCPCode," +
			" diagnosis.cancellation_date as cancellationDate," +
			" diagnosis.resources as resources," +
			" CASE" +
			"   WHEN consequenceDiagnosis.code IS NOT NULL and diagnosis.consequence IS NOT NULL THEN (consequenceDiagnosis.code || ' - ' || consequenceDiagnosis.lang_it || '\n' || diagnosis.consequence)" +
			"   WHEN consequenceDiagnosis.code IS NOT NULL and diagnosis.consequence IS NULL THEN (consequenceDiagnosis.code || ' - ' || consequenceDiagnosis.lang_it)" +
			"   WHEN consequenceDiagnosis.code IS NULL and diagnosis.consequence IS NOT NULL THEN (diagnosis.consequence)" +
			" END as consequences," +
			" nandaCode.code as nandaCode," +
			" consequenceDiagnosis.code as consequenceCode";
	
	private static final String DIAGNOSES_SELECT_REPORT_SQL =
			"," +
			" CASE" +
			"   WHEN diagnosis.cancellation_date IS NOT NULL THEN (UPPER(deleteAuthor.name_fam) || ' ' || deleteAuthor.name_giv || ' [' || deleteAuthorCode.lang_it || ' (' || deleteAuthorCode.code || ')] - ' || TO_CHAR(diagnosis.cancellation_date, 'DD/MM/YYYY HH24:MI')) || ' - ' || diagnosis.cancellation_note" +
			" END as deleteDetails";
			
	private static final String DIAGNOSES_JOIN_SQL =
			" FROM" +
			" nanda diagnosis" +
			" LEFT JOIN employee author on diagnosis.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode on diagnosis.authorRole = authorCode.id" +
			" LEFT JOIN code_value_nanda nandaCode on diagnosis.nandaDiag = nandaCode.id" +
			" LEFT JOIN code_value riskCode on diagnosis.riskCode = riskCode.id" +
			" LEFT JOIN code_value typePCP on diagnosis.typePCP = typePCP.id" +
			" LEFT JOIN code_value_nanda consequenceDiagnosis on diagnosis.consequenceDiag = consequenceDiagnosis.id" +
			" LEFT JOIN patient_encounter encounter on diagnosis.patient_encounter = encounter.internal_id";
			
	private static final String DIAGNOSES_JOIN_REPORT_SQL =
			" LEFT JOIN employee deleteAuthor on diagnosis.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN code_value_role deleteAuthorCode on diagnosis.cancelledByRole = deleteAuthorCode.id" ;
			
	private static final String DIAGNOSES_WHERE_SQL =			
			" WHERE encounter.internal_id = :encounterId";
	
	// FIXME: LISTAGG multipli nella query principale
	private static final String ETIOLOGIC_FACTORS_SQL = 
			" SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" CASE" +
			"   WHEN diagnosis.else_nanda_BM IS NOT NULL THEN " +
			"   CASE" +
			"     WHEN LISTAGG (etiologicFactor.lang_it) WITHIN GROUP (ORDER BY etiologicFactor.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || etiologicFactor.lang_it, '\n') WITHIN GROUP (ORDER BY etiologicFactor.lang_it)  || '\n' || diagnosis.else_nanda_BM)" +
			"     WHEN LISTAGG (etiologicFactor.lang_it) WITHIN GROUP (ORDER BY etiologicFactor.lang_it) IS NULL THEN (diagnosis.else_nanda_BM)" +
			"   END" +
			"   WHEN diagnosis.else_nanda_BM IS NULL THEN" +
			"   CASE" +
			"     WHEN LISTAGG (etiologicFactor.lang_it) WITHIN GROUP (ORDER BY etiologicFactor.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || etiologicFactor.lang_it, '\n') WITHIN GROUP (ORDER BY etiologicFactor.lang_it))" +
			"   END" +
			" END as etiologicFactors" +
			" FROM" +
			" nanda diagnosis" +
			" LEFT JOIN nanda_nandaBM nandaEtiologicFactor on diagnosis.internal_id = nandaEtiologicFactor.nanda_id" +
			" LEFT JOIN code_value_nanda etiologicFactor on nandaEtiologicFactor.nandaBM = etiologicFactor.id" +
			" WHERE diagnosis.internal_id IN (:diagnosisIds)" +
			" GROUP BY" +
			" diagnosis.internal_id," +
			" diagnosis.else_nanda_BM";					
	
	// FIXME: LISTAGG multipli nella query principale
	private static final String SYMPTOMS_SQL = 
			" SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" CASE" +
			"   WHEN diagnosis.nanda_BFstr IS NOT NULL THEN " +
			"   CASE" +
			"     WHEN LISTAGG (symptom.lang_it) WITHIN GROUP (ORDER BY symptom.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || symptom.lang_it, '\n') WITHIN GROUP (ORDER BY symptom.lang_it)  || '\n' || diagnosis.nanda_BFstr)" +
			"     WHEN LISTAGG (symptom.lang_it) WITHIN GROUP (ORDER BY symptom.lang_it) IS NULL THEN (diagnosis.nanda_BFstr)" +
			"   END" +
			"   WHEN diagnosis.nanda_BFstr IS NULL THEN" +
			"   CASE" +
			"     WHEN LISTAGG (symptom.lang_it) WITHIN GROUP (ORDER BY symptom.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || symptom.lang_it, '\n') WITHIN GROUP (ORDER BY symptom.lang_it))" +
			"   END" +
			" END as symptoms" +
			" FROM" +
			" nanda diagnosis" +
			" LEFT JOIN nanda_nandaBFsgn nandaSymptom on diagnosis.internal_id = nandaSymptom.nanda_id" +
			" LEFT JOIN code_value_nanda symptom on nandaSymptom.nandaBFsign = symptom.id" +
			" WHERE diagnosis.internal_id IN (:diagnosisIds)" +
			" GROUP BY" +
			" diagnosis.internal_id," +
			" diagnosis.nanda_BFstr";
				
	// FIXME: LISTAGG multipli nella query principale
	private static final String RISK_FACTORS_SQL = 
			" SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" CASE" +
			"   WHEN diagnosis.else_nanda_RF IS NOT NULL THEN " +
			"   CASE" +
			"     WHEN LISTAGG (riskFactor.lang_it) WITHIN GROUP (ORDER BY riskFactor.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || riskFactor.lang_it, '\n') WITHIN GROUP (ORDER BY riskFactor.lang_it)  || '\n' || diagnosis.else_nanda_RF)" +
			"     WHEN LISTAGG (riskFactor.lang_it) WITHIN GROUP (ORDER BY riskFactor.lang_it) IS NULL THEN (diagnosis.else_nanda_RF)" +
			"   END" +
			"   WHEN diagnosis.else_nanda_RF IS NULL THEN" +
			"   CASE" +
			"     WHEN LISTAGG (riskFactor.lang_it) WITHIN GROUP (ORDER BY riskFactor.lang_it) IS NOT NULL THEN (LISTAGG ('- ' || riskFactor.lang_it, '\n') WITHIN GROUP (ORDER BY riskFactor.lang_it))" +
			"   END" +
			" END as riskFactors" +
			" FROM" +
			" nanda diagnosis" +
			" LEFT JOIN nanda_nandaRF nandaRiskFactor on diagnosis.internal_id = nandaRiskFactor.nanda_id" +
			" LEFT JOIN code_value_nanda riskFactor on nandaRiskFactor.nandaRF = riskFactor.id" +
			" WHERE diagnosis.internal_id IN (:diagnosisIds)" +
			" GROUP BY" +
			" diagnosis.internal_id," +
			" diagnosis.else_nanda_RF";			
	
	private static final String TEMPLATE_DOSAGE_SELECT_SQL =
			" SELECT" +
			" details.*," +
			" templateDosage.title as frequency" +
			" FROM (";
			
	private static final String TEMPLATE_DOSAGE_JOIN_SQL =
			")" +
			" details" +
			" LEFT JOIN templatedosage templateDosage on details.templateDosageKey = templateDosage.key AND templateDosage.is_active = 1";
		
	private static final String OBJECTIVES_SELECT_SQL =			 
			" SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" objective.internal_id as internalId," +
			" activity.internal_id as activityId," +
			" objective.text_string as title," +
			" (UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(objective.creation_date, 'DD/MM/YYYY HH24:MI')) as creationDetails," +
			" activity.effective_date_low as startDate," +
			" activity.effective_date_high as endDate," +
			" statusCode.code as status," +
			" objective.cancellation_date as cancellationDate," +
			" CASE" +
			"	WHEN supportRole.code IS NOT NULL THEN (responsibleRole.lang_it || ' (' || activity.support_number || ' ' || supportRole.lang_it || ')')" +
			" 	WHEN supportRole.code IS NULL THEN (responsibleRole.lang_it)" +
			" END as involvedRoles," +
			" LISTAGG (TO_CHAR(dosage.daytime, 'HH24:MI'), ', ') WITHIN GROUP (ORDER BY dosage.daytime) as daytime," +
			" SUBSTR (LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', 1, INSTR(LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', '|') - 1) as dayinterval," +
			" sdloc.internal_id || ';A=' || LISTAGG (dosage.day_interval || '@' || TO_CHAR(dosage.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY dosage.daytime) as templateDosageKey," +
			" activity.time_spent as plannedTime, " +
			" objective.note as note";				
	
	private static final String OBJECTIVES_SELECT_REPORT_SQL =
			"," +
			" CASE" +
			"	WHEN objective.cancellation_date IS NOT NULL THEN (UPPER(deleteAuthor.name_fam) || ' ' || deleteAuthor.name_giv || ' [' || deleteAuthorCode.lang_it || ' (' || deleteAuthorCode.code || ')] - ' || TO_CHAR(objective.cancellation_date, 'DD/MM/YYYY HH24:MI')) || ' - ' || objective.cancellation_note" +
			" END as deleteDetails";
			
	private static final String OBJECTIVES_JOIN_SQL =
			" FROM" +
			" objective_nanda objective" +
			" LEFT JOIN nanda diagnosis on objective.nanda_id = diagnosis.internal_id" +
			" LEFT JOIN lep_activity activity on objective.internal_id = activity.objective_id" +
			" LEFT JOIN employee author on objective.author_id = author.internal_id" +
			" LEFT JOIN code_value_role authorCode on objective.authorRole = authorCode.id" +
			" LEFT JOIN code_value statusCode on objective.obj_reached = statusCode.id" +
			" LEFT JOIN code_value_role responsibleRole on activity.responsible_role = responsibleRole.id" +
			" LEFT JOIN code_value_role supportRole on activity.support_role = supportRole.id" +
			" LEFT JOIN dosage dosage on activity.internal_id = dosage.lep_activity" +
			" LEFT JOIN patient_encounter encounter on encounter.internal_id = objective.patient_encounter" +
			" LEFT JOIN service_delivery_location sdloc on sdloc.internal_id = encounter.assignedsdl";

	private static final String OBJECTIVES_JOIN_REPORT_SQL =
			" LEFT JOIN employee deleteAuthor on objective.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN code_value_role deleteAuthorCode on objective.cancelledByRole = deleteAuthorCode.id" ;
	
	private static final String OBJECTIVES_WHERE_SQL =
			" WHERE diagnosis.internal_id IN (:diagnosisIds)";
	
	private static final String OBJECTIVES_GROUPBY_SQL =
			" GROUP BY" +
			" diagnosis.internal_id," +
			" objective.internal_id," +
			" activity.internal_id," +
			" objective.text_string," +
			" author.name_fam," +
			" author.name_giv," +
			" authorCode.lang_it," +
			" authorCode.code," +
			" objective.creation_date," +
			" activity.effective_date_low," +
			" activity.effective_date_high," +
			" statusCode.code," +
			" objective.cancellation_date," +
			" supportRole.code," +
			" responsibleRole.lang_it," +
			" activity.support_number, " +
			" supportRole.lang_it," +
			" sdloc.internal_id," +
			" activity.time_spent, " +
			" objective.note";
			
	private static final String OBJECTIVES_GROUPBY_REPORT_SQL =
			"," +
			" deleteAuthor.name_fam," +
			" deleteAuthor.name_giv," +
			" deleteAuthorCode.lang_it," +
			" deleteAuthorCode.code," +
			" objective.cancellation_note";
					
	private static final String ACTIVITIES_SELECT_SQL = 
			"SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" activity.internal_id as internalId," +
			" lepCode.lang_it as title," +
			" (UPPER(author.name_fam) || ' ' || author.name_giv || ' [' || authorCode.lang_it || ' (' || authorCode.code || ')] - ' || TO_CHAR(activity.creation_date, 'DD/MM/YYYY HH24:MI')) as creationDetails," +
			" activity.effective_date_low as startDate," +
			" activity.effective_date_high as endDate," +
			" activity.validity_period_time_high as invalidationDate," +
			" CASE" +
			"   WHEN supportRole.code IS NOT NULL THEN (responsibleRole.lang_it || ' (' || activity.support_number || ' ' || supportRole.lang_it || ')')" +
			"   WHEN supportRole.code IS NULL THEN (responsibleRole.lang_it)" +
			" END as involvedRoles," +
			" LISTAGG (TO_CHAR(dosage.daytime, 'HH24:MI'), ', ') WITHIN GROUP (ORDER BY dosage.daytime) as daytime," +
			" SUBSTR (LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', 1, INSTR(LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', '|') - 1) as dayinterval," +
			" sdloc.internal_id || ';A=' || LISTAGG (dosage.day_interval || '@' || TO_CHAR(dosage.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY dosage.daytime) as templateDosageKey," +
			" activity.time_spent as plannedTime," +
			" activity.note as note";
	
	private static final String ACTIVITIES_SELECT_REPORT_SQL = 
			"," +
			" CASE" +
			"	WHEN activity.validity_period_time_high IS NOT NULL THEN (UPPER(deleteAuthor.name_fam) || ' ' || deleteAuthor.name_giv || ' [' || deleteAuthorCode.lang_it || ' (' || deleteAuthorCode.code || ')] - ' || TO_CHAR(activity.validity_period_time_high, 'DD/MM/YYYY HH24:MI')) || ' - ' || activity.cancellation_note" +
			" END as deleteDetails";
			
	private static final String ACTIVITIES_JOIN_SQL = 
			" FROM" +
			" lep_activity activity" +
			" LEFT JOIN nanda diagnosis on activity.nanda_id = diagnosis.internal_id" +
			" LEFT JOIN employee author on activity.author_id = author.internal_id" +
			" LEFT JOIN code_value_nanda lepCode on activity.nandaLep = lepCode.id" +
			" LEFT JOIN code_value_role authorCode on activity.authorRole = authorCode.id" +
			" LEFT JOIN code_value_role responsibleRole on activity.responsible_role = responsibleRole.id" +
			" LEFT JOIN code_value_role supportRole on activity.support_role = supportRole.id" +
			" LEFT JOIN dosage dosage on activity.internal_id = dosage.lep_activity" +
			" LEFT JOIN patient_encounter encounter on encounter.internal_id = activity.patenc" +
			" LEFT JOIN service_delivery_location sdloc on sdloc.internal_id = encounter.assignedsdl" +
			" LEFT JOIN objective_nanda objective on activity.objective_id = objective.internal_id";
				
	private static final String ACTIVITIES_JOIN_REPORT_SQL = 
			" LEFT JOIN employee deleteAuthor on activity.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN code_value_role deleteAuthorCode on activity.cancelledByRole = deleteAuthorCode.id" ;
	
	private static final String ACTIVITIES_WHERE_SQL =
			" WHERE diagnosis.internal_id IN (:diagnosisIds)" +
			" AND objective.internal_id IS NULL";
	
	private static final String ACTIVITIES_GROUPBY_SQL =
			" GROUP BY" +
			" diagnosis.internal_id," +
			" activity.internal_id," +
			" lepCode.lang_it," +
			" author.name_fam," +
			" author.name_giv," +
			" authorCode.lang_it," +
			" authorCode.code," +
			" activity.creation_date," +
			" activity.effective_date_low," +
			" activity.effective_date_high," +
			" activity.validity_period_time_high," +
			" supportRole.code," +
			" responsibleRole.lang_it," +
			" activity.support_number," +
			" supportRole.lang_it," +
			" sdloc.internal_id," +
			" activity.time_spent," +
			" activity.note";
	
	private static final String ACTIVITIES_GROUPBY_REPORT_SQL =
			"," +
			" deleteAuthor.name_fam," +
			" deleteAuthor.name_giv," +
			" deleteAuthorCode.lang_it," +
			" deleteAuthorCode.code," +
			" activity.cancellation_note";	
		
	private static final String ALL_ACTIVITIES_SELECT_SQL =
			" SELECT" +
			" diagnosis.internal_id as diagnosisId," +
			" objective.internal_id as objectiveId," +
			" activity.internal_id as internalId," +
			" activity.creation_date as creationDate," +
			" CASE" +
			"	WHEN activity.validity_period_time_high IS NULL and (activity.effective_date_high > :sysdate or activity.effective_date_high IS NULL) THEN 'active'" +
			"	WHEN activity.validity_period_time_high IS NULL and activity.effective_date_high < :sysdate THEN 'completed'" +
			"	WHEN activity.validity_period_time_high IS NOT NULL THEN 'cancelled'" +
			" END as status," +
			" lepCode.lang_it as title," +			
			" CASE" +
			"   WHEN objective.internal_id IS NOT NULL and nandaCode.code IS NOT NULL THEN objective.text_string || ' (' || diagnosis.prog_number || ' - ' || nandaCode.code || ' - ' || nandaCode.lang_it || ')'" +
			"   WHEN objective.internal_id IS NOT NULL and nandaCode.code IS NULL THEN objective.text_string || ' (' || diagnosis.prog_number || ' - ' || diagnosis.title_diag || ')'" +
			"   WHEN diagnosis.internal_id IS NOT NULL and nandaCode.code IS NOT NULL THEN diagnosis.prog_number || ' - ' || nandaCode.code || ' - ' || nandaCode.lang_it" +
			"   WHEN diagnosis.internal_id IS NOT NULL and nandaCode.code IS NULL THEN diagnosis.prog_number || ' - ' || diagnosis.title_diag" +
			" END as subtitle," +
			" lepCode.code as code," +
			" UPPER(author.name_fam) || ' ' || author.name_giv as author," +			
			" activity.effective_date_low as startDate," +
			" activity.effective_date_high as endDate," +
			" activity.validity_period_time_high as invalidationDate," +
			" activity.extemporaneous as extemporaneous," +
			" activity.modified as modified," +
			" CASE" +
			" 	WHEN deleteAuthor.internal_id IS NOT NULL THEN (UPPER(deleteAuthor.name_fam) || ' ' || deleteAuthor.name_giv)" +
			" END as cancelledBy," +
			" LISTAGG (TO_CHAR(dosage.daytime, 'HH24:MI'), ', ') WITHIN GROUP (ORDER BY dosage.daytime) as daytime," +
			" SUBSTR (LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', 1, INSTR(LISTAGG (dosage.day_interval, '|') WITHIN GROUP (ORDER BY dosage.daytime) || '|', '|') - 1) as dayinterval," +
			" sdloc.internal_id || ';A=' || LISTAGG (dosage.day_interval || '@' || TO_CHAR(dosage.daytime, 'HH24:MI'), '|') WITHIN GROUP (ORDER BY dosage.daytime) as templateDosageKey," +
			" activity.time_spent as plannedTime," +
			" activity.note as note";
	
	private static final String ALL_ACTIVITIES_JOIN_SQL =
			" FROM" +
			" lep_activity activity" +
			" LEFT JOIN nanda diagnosis on activity.nanda_id = diagnosis.internal_id" +
			" LEFT JOIN objective_nanda objective on activity.objective_id = objective.internal_id" +
			" LEFT JOIN code_value_nanda lepCode on activity.nandaLep = lepCode.id" +
			" LEFT JOIN code_value_nanda nandaCode on diagnosis.nandaDiag = nandaCode.id" +
			" LEFT JOIN employee author on activity.author_id = author.internal_id" +
			" LEFT JOIN employee deleteAuthor on activity.cancelled_by_id = deleteAuthor.internal_id" +
			" LEFT JOIN dosage dosage on activity.internal_id = dosage.lep_activity" +
			" LEFT JOIN patient_encounter encounter on encounter.internal_id = activity.patenc" +
			" LEFT JOIN service_delivery_location sdloc on sdloc.internal_id = encounter.assignedsdl";
	
	private static final String ALL_ACTIVITIES_WHERE_SQL =
			" WHERE encounter.internal_id IN (:encounterId)" +
			" AND activity.extemporaneous = 0";
	
	private static final String ALL_ACTIVITIES_GROUPBY_SQL =
			" GROUP BY" +
			" diagnosis.internal_id," +
			" objective.internal_id," +
			" activity.internal_id," +
			" lepCode.lang_it," +
			" lepCode.code," +
			" objective.text_string," +
			" diagnosis.prog_number," +
			" nandaCode.code," +
			" nandaCode.lang_it," +
			" diagnosis.title_diag," +
			" author.name_fam," +
			" author.name_giv," +
			" activity.effective_date_low," +
			" activity.effective_date_high," +
			" activity.validity_period_time_high," +
			" activity.extemporaneous," +
			" activity.modified," +
			" deleteAuthor.internal_id," +
			" deleteAuthor.name_fam," +
			" deleteAuthor.name_giv," +
			" sdloc.internal_id," +
			" activity.time_spent," +
			" activity.note," +
			" activity.creation_date";				
	
	private static final String DIAGNOSES_COUNT_HQL = 
			" SELECT" +
			" COUNT(*) as count" +
			" FROM" +
			" Nanda diagnosis" + 
			" LEFT JOIN diagnosis.patientEncounter encounter" +
			" WHERE encounter.internalId = :encounterId";
		
	private static final String EXECUTION_HISTORY_SELECT_HQL =
			" SELECT" +
			" 'E' as type," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" execution.plannedDate as plannedDate," +
			" execution.plannedTime as plannedTime," +
			" execution.executionDate.low as erogationDate," +
			" execution.executionTime as erogationTime," +
			" CASE" +			
			" 	WHEN execution.executionDate.low IS NOT NULL THEN (UPPER(author.name.fam) || ' ' || author.name.giv || ' [' || authorCode.langIt || ' (' || authorCode.code || ')]')" +
			" END as author," +
			" execution.note as note," +
			" activity.effectiveDate.high as endDate";
	
	private static final String EXECUTION_HISTORY_JOIN_HQL =
			" FROM LEPExecution execution" +
			" LEFT JOIN execution.statusCode statusCode" +
			" LEFT JOIN execution.statusDetailsCode statusDetailsCode" +			
			" LEFT JOIN execution.author author" +
			" LEFT JOIN execution.authorRole authorCode" +
			" LEFT JOIN execution.lepActivity activity";
	
	private static final String EXECUTION_HISTORY_WHERE_HQL =
			" WHERE activity.internalId = :activityId";
	
	private static final String EXECUTION_HISTORY_ORDERBY_HQL =
			" ORDER BY execution.plannedDate";
			
		
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page){
		try {							
			
			String currentLang = Locale.instance().getLanguage();
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			
			List<Map<String, Object>> results = readCarePlan(restrictionMap, currentLang, false);									
			// FIXME
			String readUrl = BASE_REST_URL + "careplan/init/" + restrictions + "/";
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page);			
			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			
			res.setMain(dm);
						
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] CarePlan Init");
			log.debug("[cid="+Conversation.instance().getId()+"] CarePlan Init details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in CarePlan Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error CarePlanRest init").build();
		}
	}
	
	@GET
	@Path("refresh/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {
			
			String currentLang = Locale.instance().getLanguage();

			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			
			List<Map<String, Object>> results = null;
			
			String refreshType = restrictionMap.remove("refreshType").get(0);
					
			if (refreshType.equals("carePlan")) {
				// query careplan
				results = readCarePlan(restrictionMap, currentLang, false);
			} else if (refreshType.equals("activities")) {
				// query activities
				results = readActivities(restrictionMap, currentLang);
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("Error CarePlanRest refresh").build();
			}
			
			// FIXME
			String readUrl = BASE_REST_URL + "careplan/refresh/" + restrictions + "/";
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); 
			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			
			res.setMain(dm);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] CarePlan Refresh");
			log.debug("[cid="+Conversation.instance().getId()+"] CarePlan Refresh details - restrictions: " + restrictions + " (page " + page + ")");
						
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in CarePlan Refresh with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error CarePlanRest refresh").build();
		}		
	}
	
	@GET
	@Path("printReport/{restrictions}")
	public Response printReport(@PathParam("restrictions") PathSegment restrictions) {
		try {		
			
			String currentLang = Locale.instance().getLanguage();
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			
			boolean enableLEPTime = false;
			
			if (restrictionMap.get("enableLEPTime") != null) {
				enableLEPTime = restrictionMap.remove("enableLEPTime").get(0).equals("true");
			}
			
			Contexts.getConversationContext().set("EnableLEPTime", enableLEPTime);
						
			List<Map<String, Object>> results = readCarePlan(restrictionMap, currentLang, true);	
			
			Contexts.getConversationContext().set("DiagnosisList", results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok().build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error CarePlanRest printReport", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error CarePlanRest printReport").build();
		}
	}
	
	private List<Map<String, Object>> readCarePlan(MultivaluedMap<String, String> restrictionMap, String currentLang, boolean report) {
		
		FunctionsBean fb = FunctionsBean.instance();
		
		boolean showNotActive = false;
		if (restrictionMap.get("showNotActive") != null){
			showNotActive = Boolean.parseBoolean(restrictionMap.get("showNotActive").get(0));
		}
		
		String diagnosesSql = "";
		
		if (!report){
			diagnosesSql = DIAGNOSES_SELECT_SQL + DIAGNOSES_JOIN_SQL + DIAGNOSES_WHERE_SQL;
		} else {
			diagnosesSql = DIAGNOSES_SELECT_SQL + DIAGNOSES_SELECT_REPORT_SQL + DIAGNOSES_JOIN_SQL + DIAGNOSES_JOIN_REPORT_SQL + DIAGNOSES_WHERE_SQL;
		}
		
		if (!showNotActive){
			diagnosesSql += " AND diagnosis.cancellation_date IS NULL";
		}
		
		// Add privacy restrictions
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			diagnosesSql += " AND diagnosis.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
		}
				
		diagnosesSql += " ORDER BY diagnosis.prog_number";
				
		if (!currentLang.equals("it")) {
			diagnosesSql = diagnosesSql.replace("lang_it", "lang_" + currentLang);
		}
		
		Query qryDiagnoses = ca.createHibernateNativeQuery(diagnosesSql);
		
		if (restrictionMap.get("encounterId") != null) {
			qryDiagnoses.setParameterList("encounterId", restrictionMap.get("encounterId"));
		}
		
		// Add privacy parameters
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			qryDiagnoses.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
			qryDiagnoses.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
		}
					
		qryDiagnoses.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
		List<Map<String, Object>> resultsDiagnoses = qryDiagnoses.list();
		
		List<Long> diagnosisNotAtRiskIds = new ArrayList<Long>(); 
		List<Long> diagnosisAtRiskIds = new ArrayList<Long>(); 
		List<Long> diagnosisIds = new ArrayList<Long>(); 
		
		for (Map<String, Object> diagnosis : resultsDiagnoses)
		{
			if ("NOTATRISK".equals(diagnosis.get("riskcode"))){
				
				diagnosisNotAtRiskIds.add(((BigDecimal)diagnosis.get("internalid")).longValue());
				diagnosisIds.add(((BigDecimal)diagnosis.get("internalid")).longValue());
				
			} else if ("ATRISK".equals(diagnosis.get("riskcode"))){
				
				diagnosisAtRiskIds.add(((BigDecimal)diagnosis.get("internalid")).longValue());
				diagnosisIds.add(((BigDecimal)diagnosis.get("internalid")).longValue());
			}
		}
		
		if (!diagnosisNotAtRiskIds.isEmpty()) {
			
			String etiologicFactorsSql = ETIOLOGIC_FACTORS_SQL;				
			String symptomsSql = SYMPTOMS_SQL;
			
			if (!currentLang.equals("it")) {
				etiologicFactorsSql = etiologicFactorsSql.replace("lang_it", "lang_" + currentLang);
				symptomsSql = symptomsSql.replace("lang_it", "lang_" + currentLang);
			}
				
			// Replace LISTAGG
			etiologicFactorsSql = ReplaceListaggBean.replaceListagg(etiologicFactorsSql);
			symptomsSql = ReplaceListaggBean.replaceListagg(symptomsSql);
			
			Query qryEtiologicFactors = ca.createHibernateNativeQuery(etiologicFactorsSql);
			Query qrySymptoms = ca.createHibernateNativeQuery(symptomsSql);
			
			qryEtiologicFactors.setParameterList("diagnosisIds", diagnosisNotAtRiskIds);
			qrySymptoms.setParameterList("diagnosisIds", diagnosisNotAtRiskIds);
										
			qryEtiologicFactors.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			qrySymptoms.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Map<String, Object>> resultsEtiologicFactors = qryEtiologicFactors.list();
			List<Map<String, Object>> resultsSymptoms = qrySymptoms.list();
			
			for (Map<String, Object> etiologicFactors : resultsEtiologicFactors){
				
				for (Map<String, Object> diagnosis : resultsDiagnoses){
					
					if (etiologicFactors.get("diagnosisid").equals((BigDecimal)diagnosis.get("internalid"))){
						
						diagnosis.put("etiologicfactors", etiologicFactors.get("etiologicfactors"));
						break;
					}
				}										
			}	
			
			for (Map<String, Object> symptoms : resultsSymptoms){
				
				for (Map<String, Object> diagnosis : resultsDiagnoses){
					
					if (symptoms.get("diagnosisid").equals((BigDecimal)diagnosis.get("internalid"))){
						
						diagnosis.put("symptoms", symptoms.get("symptoms"));
						break;
					}
				}										
			}
		}
		
		if (!diagnosisAtRiskIds.isEmpty()) {
			
			String riskFactorsSql = RISK_FACTORS_SQL;
			
			if (!currentLang.equals("it")) {
				riskFactorsSql = riskFactorsSql.replace("lang_it", "lang_" + currentLang);
			}
			
			// Replace LISTAGG
			riskFactorsSql = ReplaceListaggBean.replaceListagg(riskFactorsSql);
			
			Query qryRiskFactors = ca.createHibernateNativeQuery(riskFactorsSql);
			
			qryRiskFactors.setParameterList("diagnosisIds", diagnosisAtRiskIds);
										
			qryRiskFactors.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Map<String, Object>> resultsRiskFactors = qryRiskFactors.list();
			
			for (Map<String, Object> riskFactors : resultsRiskFactors){
				
				for (Map<String, Object> diagnosis : resultsDiagnoses){
					
					if (riskFactors.get("diagnosisid").equals((BigDecimal)diagnosis.get("internalid"))){
						
						diagnosis.put("riskfactors", riskFactors.get("riskfactors"));
						break;
					}
				}										
			}	
		}
		
		if (!diagnosisIds.isEmpty()) {
			
			String objectivesSql = TEMPLATE_DOSAGE_SELECT_SQL;
			
			if (!report){
				objectivesSql += OBJECTIVES_SELECT_SQL + OBJECTIVES_JOIN_SQL + OBJECTIVES_WHERE_SQL;
			} else {
				objectivesSql += OBJECTIVES_SELECT_SQL + OBJECTIVES_SELECT_REPORT_SQL + OBJECTIVES_JOIN_SQL + OBJECTIVES_JOIN_REPORT_SQL + OBJECTIVES_WHERE_SQL;
			}
			
			if (!showNotActive){
				objectivesSql += " AND objective.cancellation_date IS NULL AND (activity.effective_date_high IS NULL OR activity.effective_date_high > :sysdate)";
			}
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				objectivesSql += " AND objective.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			if (!report){
				objectivesSql += OBJECTIVES_GROUPBY_SQL;
			} else {
				objectivesSql += OBJECTIVES_GROUPBY_SQL + OBJECTIVES_GROUPBY_REPORT_SQL;
			}
			
			objectivesSql += " ORDER BY objective.creation_date" + TEMPLATE_DOSAGE_JOIN_SQL;
						
			String activitiesSql = TEMPLATE_DOSAGE_SELECT_SQL;
			
			if (!report){
				activitiesSql += ACTIVITIES_SELECT_SQL + ACTIVITIES_JOIN_SQL + ACTIVITIES_WHERE_SQL;
			} else {
				activitiesSql += ACTIVITIES_SELECT_SQL + ACTIVITIES_SELECT_REPORT_SQL + ACTIVITIES_JOIN_SQL + ACTIVITIES_JOIN_REPORT_SQL + ACTIVITIES_WHERE_SQL;
			}
			
			if (!showNotActive){
				activitiesSql += " AND activity.validity_period_time_high IS NULL AND (activity.effective_date_high IS NULL OR activity.effective_date_high > :sysdate)";
			}
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				activitiesSql += " AND activity.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			if (!report){
				activitiesSql += ACTIVITIES_GROUPBY_SQL;
			} else {
				activitiesSql += ACTIVITIES_GROUPBY_SQL + ACTIVITIES_GROUPBY_REPORT_SQL;
			}
			
			activitiesSql += " ORDER BY activity.creation_date" + TEMPLATE_DOSAGE_JOIN_SQL;
						
			if (!currentLang.equals("it")) {
				objectivesSql = objectivesSql.replace("lang_it", "lang_" + currentLang);
				activitiesSql = activitiesSql.replace("lang_it", "lang_" + currentLang);
			}
			
			// Replace LISTAGG
			objectivesSql = ReplaceListaggBean.replaceListagg(objectivesSql);
			activitiesSql = ReplaceListaggBean.replaceListagg(activitiesSql);
			
			Query qryObjectives = ca.createHibernateNativeQuery(objectivesSql);
			Query qryActivities = ca.createHibernateNativeQuery(activitiesSql);
			
			qryObjectives.setParameterList("diagnosisIds", diagnosisIds);
			qryActivities.setParameterList("diagnosisIds", diagnosisIds);
			
			if (objectivesSql.contains(":sysdate")) {
				qryObjectives.setParameter("sysdate", new Date());
			}
			
			if (activitiesSql.contains(":sysdate")) {
				qryActivities.setParameter("sysdate", new Date());
			}	
			
			// Add privacy parameters
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				qryObjectives.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
				qryObjectives.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
				qryActivities.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
				qryActivities.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
			}
										
			qryObjectives.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			qryActivities.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Map<String, Object>> resultsObjectives = qryObjectives.list();
			List<Map<String, Object>> resultsActivities = qryActivities.list();
			
			for (Map<String, Object> objective : resultsObjectives){
				
				for (Map<String, Object> diagnosis : resultsDiagnoses){
					
					if (objective.get("diagnosisid").equals((BigDecimal)diagnosis.get("internalid"))){
						
						if (diagnosis.get("objectives") == null){
							
							List<Map<String, Object>> objectives = new ArrayList<Map<String,Object>>();
							
							diagnosis.put("objectives", objectives);
						}
						
						objective.remove("diagnosisid");
						
						String dayinterval = (String)objective.remove("dayinterval");
						String daytime = (String)objective.remove("daytime");
						
						if (objective.get("frequency") == null) {
														
							objective.put("frequency", fb.getStaticTranslation("Every") + " " + dayinterval + " " + fb.getStaticTranslation("day/s") + " " + fb.getStaticTranslation("at_hour") + " " + daytime);
						}
						
						((List<Map<String, Object>>)diagnosis.get("objectives")).add(objective);
						
						break;
					}
				}										
			}	
			
			for (Map<String, Object> activity : resultsActivities){
				
				for (Map<String, Object> diagnosis : resultsDiagnoses){
					
					if (activity.get("diagnosisid").equals((BigDecimal)diagnosis.get("internalid"))){
						
						if (diagnosis.get("activities") == null){
							
							List<Map<String, Object>> activities = new ArrayList<Map<String,Object>>();
							
							diagnosis.put("activities", activities);
						}
						
						activity.remove("diagnosisid");
						
						String dayinterval = (String)activity.remove("dayinterval");
						String daytime = (String)activity.remove("daytime");
						
						if (activity.get("frequency") == null) {
															
							activity.put("frequency", fb.getStaticTranslation("Every") + " " + dayinterval + " " + fb.getStaticTranslation("day/s") + " " + fb.getStaticTranslation("at_hour") + " " + daytime);
						}
						
						((List<Map<String, Object>>)diagnosis.get("activities")).add(activity);
						
						break;
					}
				}											
			}
			return resultsDiagnoses;
		}
		return null;
	}
	
	private List<Map<String, Object>> readActivities(MultivaluedMap<String, String> restrictionMap, String currentLang) {
		
		String allActivitiesSql = TEMPLATE_DOSAGE_SELECT_SQL + ALL_ACTIVITIES_SELECT_SQL + ALL_ACTIVITIES_JOIN_SQL + ALL_ACTIVITIES_WHERE_SQL;
		
		// Add privacy restrictions
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			allActivitiesSql += " AND activity.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
		}
		
		allActivitiesSql += ALL_ACTIVITIES_GROUPBY_SQL + TEMPLATE_DOSAGE_JOIN_SQL + " ORDER BY status, creationDate";
		
		if (!currentLang.equals("it")) {
			allActivitiesSql = allActivitiesSql.replace("lang_it", "lang_" + currentLang);
		}
		
		// Replace LISTAGG
		allActivitiesSql = ReplaceListaggBean.replaceListagg(allActivitiesSql);
		
		Query qryAllActivities = ca.createHibernateNativeQuery(allActivitiesSql);
		
		if (restrictionMap.get("encounterId") != null) {
			qryAllActivities.setParameterList("encounterId", restrictionMap.get("encounterId"));
		}
		
		qryAllActivities.setParameter("sysdate", new Date());		
		
		// Add privacy parameters
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			qryAllActivities.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
			qryAllActivities.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
		}
					
		qryAllActivities.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
				
		List<Map<String, Object>> resultsAllActivities = qryAllActivities.list();
		
		FunctionsBean fb = FunctionsBean.instance();
		
		for (Map<String, Object> activity : resultsAllActivities){
									
			String dayinterval = (String)activity.remove("dayinterval");
			String daytime = (String)activity.remove("daytime");
			
			BigDecimal truez = new BigDecimal(1);
							
			if (activity.get("frequency") == null) {
				
				if (truez.equals(activity.get("extemporaneous"))){
					activity.put("frequency", fb.getStaticTranslation("Extemporaneous"));
				} else {												
					activity.put("frequency", fb.getStaticTranslation("Every") + " " + dayinterval + " " + fb.getStaticTranslation("day/s") + " " + fb.getStaticTranslation("at_hour") + " " + daytime);
				}
			}
			
			if (daytime != null && dayinterval != null) {
				List<Object> dosages = new ArrayList<Object>();
				
				String[] times = daytime.split(", ");
				
				for (String time : times){
					Map<String, Object> dosage = new HashMap<String, Object>();
					dosage.put("dayinterval", dayinterval);
					
					String[] hm = time.split(":");
					
					Calendar date =  Calendar.getInstance();
					date.set(1970, 0, 1, Integer.parseInt(hm[0]), Integer.parseInt(hm[1]), 0);
					date.set(Calendar.MILLISECOND, 0);
									
					dosage.put("time", date);
					dosages.add(dosage);
				}
				
				activity.put("dosages", dosages);
			}
			
			if (activity.get("subtitle") == null)
			{
				activity.put("subtitle", fb.getStaticTranslation("No_related_diagnosis"));
			}
			
		}
		return resultsAllActivities;			
	}
	
	@GET
	@Path("diagnosesCount/{encounterId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response diagnosesCount(@PathParam("encounterId") long encounterId){
		try {			
			String diagnosesCountHql = DIAGNOSES_COUNT_HQL;
	        			
			Query qryDiagnosesCount = ca.createHibernateQuery(diagnosesCountHql);
			
			qryDiagnosesCount.setParameter("encounterId", encounterId);
			
			qryDiagnosesCount.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
			Map<String, Object> result = (Map<String, Object>) qryDiagnosesCount.uniqueResult();
			
			String json = mapper.writeValueAsString(result);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error CarePlanRest diagnosesCount", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error CarePlanRest diagnosesCount").build();
		}
	}	
		
	@GET
	@Path("executionHistory/{activityId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response executionHistory(@PathParam("activityId") long activityId){
		try {
			String executionHistoryHql = EXECUTION_HISTORY_SELECT_HQL + EXECUTION_HISTORY_JOIN_HQL + EXECUTION_HISTORY_WHERE_HQL;
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				executionHistoryHql += " AND execution.plannedDate BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			executionHistoryHql += EXECUTION_HISTORY_ORDERBY_HQL;
			
			Query qryExecutionHistory = ca.createHibernateQuery(executionHistoryHql);
			
			qryExecutionHistory.setParameter("activityId", activityId);
			
			// Add privacy parameters
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				qryExecutionHistory.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
				qryExecutionHistory.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
			}
			
			qryExecutionHistory.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Map<String, Object>> results = qryExecutionHistory.list();
			
			//fixExecutions(results, now);
			results = ErogationStatusFixer.fixErogations(results, true);
			
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error CarePlanRest executionHistory", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error CarePlanRest executionHistory").build();
		}
	}
	
	/*
	// FIXME: unire con metodo fixErogations in NurseActivityRest.java
	private void fixExecutions(List<Map<String, Object>> executions, Date now) {
		
		int lowGap = EXECUTION_LOW_GAP; 
		int lateGap = EXECUTION_LATE_GAP; 
		int earlyGap = EXECUTION_EARLY_GAP; 
		int highGap = EXECUTION_HIGH_GAP;
		
		for (Map<String, Object> execution : executions) {			
			
			String statusCode = (String)execution.remove("statuscode");
			String statusDetailsCode = (String)execution.remove("statusdetailscode");
			
			Date plannedDate = (Date)execution.get("planneddate");
			Date erogationDate = (Date)execution.get("erogationdate");
			
			if (PLANNED.equals(statusCode)) {
				if (plannedDate.getTime() < now.getTime() - lowGap * MINUTE_IN_MILLISECONDS) {
					
					execution.put("status", MISSED);					
					
				} else if (plannedDate.getTime() < now.getTime() - lateGap * MINUTE_IN_MILLISECONDS) {
					
					execution.put("status", OVERDUE);				
					
				} else if (plannedDate.getTime() < now.getTime() + earlyGap * MINUTE_IN_MILLISECONDS) {
					
					execution.put("status", DUE);
										
				} else if (plannedDate.getTime() < now.getTime() + highGap * MINUTE_IN_MILLISECONDS) {
					
					execution.put("status", DUE_FUTURE_ENABLED);
										
				} else if (plannedDate.getTime() > now.getTime() + highGap * MINUTE_IN_MILLISECONDS) {
					
					execution.put("status", DUE_FUTURE_DISABLED);
					
				}
				
			} else if (DONE.equals(statusCode)) {
			
				if (plannedDate == null) {//is as needed
					execution.put("status", EROGATED_AS_PLANNED);
				} else if (erogationDate.getTime() < plannedDate.getTime() - lateGap * MINUTE_IN_MILLISECONDS) {
					execution.put("status", EROGATED_EARLY);
				} else if (erogationDate.getTime() < plannedDate.getTime() + earlyGap * MINUTE_IN_MILLISECONDS) {
					execution.put("status", EROGATED_AS_PLANNED);
				} else if (erogationDate.getTime() > plannedDate.getTime() + earlyGap * MINUTE_IN_MILLISECONDS) {
					execution.put("status", EROGATED_LATE);
				}				
				
			} else if (EXCEPTION.equals(statusCode)) {
				
				if ("WDRU".equals(statusDetailsCode)) {
					execution.put("status", EXCEPTION_WRONG_DRUG);
				} else if ("WDOSE".equals(statusDetailsCode)) {
					execution.put("status", EXCEPTION_WRONG_DOSE);
				} else if ("OCRE".equals(statusDetailsCode)) {
					execution.put("status", EXCEPTION_OTHER_REASON);
				} else {
					execution.put("status", EXCEPTION);
				}
								
			} else if (UNSUCCESSFUL.equals(statusCode)) {
				
				if ("DUNA".equals(statusDetailsCode)) {
					execution.put("status", UNSUCCESSFUL_DRUG_UNAVAILABLE);
				} else if ("NBMP".equals(statusDetailsCode)) {
					execution.put("status", UNSUCCESSFUL_NIL_BY_MOUTH);
				} else if ("PABS".equals(statusDetailsCode)) {
					execution.put("status", UNSUCCESSFUL_PATIENT_ABSENT);
				} else if ("PREF".equals(statusDetailsCode)) {
					execution.put("status", UNSUCCESSFUL_PATIENT_REFUSED);
				} else if ("OCRE".equals(statusDetailsCode)) {
					execution.put("status", UNSUCCESSFUL_OTHER_REASON);
				} else {
					execution.put("status", UNSUCCESSFUL);
				}
								
			} else if (MISSED.equals(statusCode)) {
				
				execution.put("status", MISSED);
								
			} else if (CANCELLED.equals(statusCode)) {
				
				execution.put("status", CANCELLED);
				
			}
		}
	}
	*/
		
}