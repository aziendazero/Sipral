package com.phi.rest.dashboard;

import java.math.BigDecimal;
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
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.TransferPrivacy;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.baseEntity.PrescriptionDischarge;
import com.phi.entities.baseEntity.PrescriptionDischargeMedicine;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;
import com.phi.entities.role.Employee;
import com.phi.events.PhiEvent;
import com.phi.rest.action.ErogationStatusFixer;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("DrugPrescriberRest")
@Path("/drugprescriber")
public class DrugPrescriberRest extends BaseDashboardRest { 

	private static final long serialVersionUID = -6725051242506315057L;

	private static final String PRESCRIPTION_SELECT_SQL = 
					" SELECT" +
					" prescription.internal_id as internalId," +
					" prescription.root_prescription_id as rootId," +
					" CASE" +
					"   WHEN prescription.modified = 1 THEN (	SELECT" +
					"											max(futurePrescription.internal_id)" +
					"											FROM prescription futurePrescription" +
					"											WHERE (prescription.root_prescription_id IS NOT NULL AND futurePrescription.root_prescription_id = prescription.root_prescription_id)" +
					"											OR (prescription.root_prescription_id IS NULL AND futurePrescription.root_prescription_id = prescription.internal_id)" +
					"											AND futurePrescription.is_active = 1)" +
					"	ELSE null" +
					" END as futureId," +
					" prescription.modified as modified," +
					" prescription.extemporaneous as extemporaneous," +
					" prescription.urgent as urgent," +
					" prescription.continuous as continuous," +
					" prescription.needsbased as needsbased," +
					" prescriptionCode.code as code," +
					" CASE" +
					"   WHEN statusCode.code = 'new' THEN 'new'" +
					"   WHEN statusCode.code = 'active' AND (prescription.validity_period_time_high > :sysdate OR" +
					"                                        (prescription.period_time_high > :sysdate AND prescription.validity_period_time_high IS NULL) OR" +
					"                                        (prescription.period_time_high IS NULL AND prescription.validity_period_time_high IS NULL))" +
					"                                THEN 'active'" +
					"   ELSE 'completed'" +
					" END as status," +
					" CASE" + 
					"   WHEN prescription.period_time_low > prescription.validity_period_time_low OR prescription.validity_period_time_low IS NULL THEN prescription.period_time_low" +
					"   ELSE prescription.validity_period_time_low" +
					" END as startDate," +
					" CASE" +
					"   WHEN prescription.validity_period_time_high IS NOT NULL THEN prescription.validity_period_time_high" +
					"   ELSE prescription.period_time_high" +
					" END as endDate," +
					" prescription.note as note," +
					" prescriptionRouteCode.lang_it as route," +
					" CASE" +
					"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG (medicine.name_giv, '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			//		"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1) AND prescriptionInfusionTypeCode.lang_it IS NULL) THEN LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			//		"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND prescriptionInfusionTypeCode.lang_it IS NULL) THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1) AND prescriptionInfusionTypeCode.lang_it IS NOT NULL) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG (medicine.name_giv || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
			//		"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND prescriptionInfusionTypeCode.lang_it IS NOT NULL) THEN prescriptionInfusionTypeCode.lang_it || '\n' || LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || REPLACE(SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1),'.',','), '\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					" 	ELSE prescriptionInfusionTypeCode.lang_it" +
					" END as description," +
					" LISTAGG(prescriptionMedicine.dosage, ',') WITHIN GROUP(ORDER BY prescriptionMedicine.dosage) as posology," +
					" CASE" +
					"   WHEN prescriptionCode.code = 'INFU' THEN prescription.quantity_value || '[' || prescription.quantity_unit || ']@' || prescription.infusion_speed_value || '[' || prescription.infusion_speed_unit  || ']'" +
					" END as velocity," +
					" templatedosage.title as templateString";
	
	private static final String PRESCRIPTION_JOIN_SQL = 
					" FROM" +
					" prescription prescription " +
					" LEFT JOIN code_value statusCode ON prescription.status_code=statusCode.id" +
					" LEFT JOIN code_value prescriptionCode ON prescription.code=prescriptionCode.id" +
					" LEFT JOIN code_value prescriptionRouteCode ON prescription.route_code=prescriptionRouteCode.id" +
					" LEFT JOIN code_value prescriptionInfusionTypeCode ON prescription.infusion_Type_Code=prescriptionInfusionTypeCode.id" +
					" LEFT JOIN prescription_medicine prescriptionMedicine ON prescription.internal_id=prescriptionMedicine.prescription_id" +
					" LEFT JOIN medicine medicine ON prescriptionMedicine.medicine_id=medicine.internal_id" +
					" LEFT JOIN templatedosage templateDosage ON prescriptionMedicine.template_dosage_key=templateDosage.key and templateDosage.is_active=1" +
					" LEFT JOIN code_value_codifa eqGroup ON medicine.eq_group=eqGroup.id";
	
	private static final String PRESCRIPTION_WHERE_SQL = 
					" WHERE prescription.therapy = :therapyId" +
					" AND prescription.is_active = 1" +
					" AND ((prescription.root_prescription_id IS NULL AND prescription.modified = 0) OR" +
					"      (prescription.root_prescription_id IS NOT NULL AND prescription.modified = 0 AND prescription.period_time_low < :sysdate) OR" +
					"      (prescription.modified = 1 AND statusCode.code = 'active' AND (prescription.validity_period_time_high > :sysdate OR" +
					"                                                                     (prescription.period_time_high > :sysdate AND prescription.validity_period_time_high IS NULL) OR" +
					"                                                                     (prescription.period_time_high IS NULL AND prescription.validity_period_time_high IS NULL))))";
	
	private static final String PRESCRIPTION_GROUPBY_SQL =
					" GROUP BY" +
					" prescription.internal_id," +
					" prescription.root_prescription_id," +
					" statusCode.code," +
					" prescriptionCode.code," +
					" prescription.period_time_low," +
					" prescription.period_time_high," +
					" prescription.validity_period_time_low," +
					" prescription.validity_period_time_high," +
					" prescription.note," +
					" prescriptionRouteCode.lang_it," +
					" prescriptionInfusionTypeCode.lang_it," +
					" prescription.quantity_value," +
					" prescription.quantity_unit," +
					" prescription.infusion_speed_value," +
					" prescription.infusion_speed_unit," +
					" templatedosage.title," +
					" prescription.extemporaneous," +
					" prescription.needsbased," +
					" prescription.urgent," +
					" prescription.continuous," +
					" prescription.modified";

	private static final String PRESCRIPTION_DISCHARGE_SELECT_SQL = 
					" SELECT" +
					" prescription.internal_id as internalId," +
					" medicine.internal_id as medicineId," +
					" prescription.needsbased as needsbased," +
					" prescriptionCode.code as code," +
					" statusCode.code as status," +
					" CASE" +
					"    WHEN prescription.duration IS NOT NULL THEN TO_CHAR(prescription.duration) || ' label_day/s'" +
					"    WHEN prescription.duration IS NULL THEN 'label_No_duration_defined'" +
					" END as duration," +
					" prescription.note as note," +
					" prescriptionRouteCode.lang_it as route," +
					" cvc.display_name as pharmaForm," +
					" cv.code as aifaCode," + 
					" cv.lang_it as aifaTranslationIt," +
					" prescriptionMedicine.therapeutic_plan as terapeuticPlan," +
					" CASE" +
		//			"    WHEN prescriptionMedicine.show_medicine_name=0 THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN substance.name_giv ELSE eqGroup.lang_it END),'\n') WITHIN GROUP (ORDER BY substance.name_giv)" + 
					"    WHEN prescriptionMedicine.show_medicine_name=0 THEN LISTAGG (substance.name_giv,'\n') WITHIN GROUP (ORDER BY substance.name_giv)" + //solo principio attivo
		//			"    WHEN prescriptionMedicine.show_medicine_name=1 THEN LISTAGG (medicine.name_giv,'\n') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"    WHEN prescriptionMedicine.show_medicine_name=1 THEN LISTAGG (medicine.name_giv || ' (' || substance.name_giv || ')','\n') WITHIN GROUP (ORDER BY medicine.name_giv)" + //Nome commerciale + principio attivo
					" END as description," +
					" LISTAGG (prescriptionMedicine.dosage , ',') WITHIN GROUP (ORDER BY prescriptionMedicine.dosage) as posology," +
					" CASE" +
					"    WHEN prescriptionCode.code='INFU' THEN prescription.quantity_value || '[' || prescription.quantity_unit || ']@' || prescription.infusion_speed_value || '[' || prescription.infusion_speed_unit  || ']' " +
					" END as velocity, " +
					" templatedosage.title as templateString," +
					" prescriptionMedicine.chronic_patient as chronic," +
					" prescriptionMedicine.irreplaceable as irreplaceable," +
					" prescriptionMedicine.aifaNote as aifaNote";
	
	private static final String PRESCRIPTION_DISCHARGE_JOIN_SQL = 
					" FROM" +
					" prescription_dschrg prescription" +
					" LEFT JOIN code_value statusCode ON prescription.status_code=statusCode.id"  +
					" LEFT JOIN code_value prescriptionCode ON prescription.code=prescriptionCode.id" +
					" LEFT JOIN code_value prescriptionRouteCode ON prescription.route_code=prescriptionRouteCode.id" +
					" LEFT JOIN code_value prescriptionInfusionTypeCode ON prescription.infusion_Type_Code=prescriptionInfusionTypeCode.id" +
					" LEFT JOIN prscrpt_dsch_medicine prescriptionMedicine ON prescription.internal_id=prescriptionMedicine.prescription_id" +
					" LEFT JOIN medicine medicine ON prescriptionMedicine.medicine_id=medicine.internal_id" +
					" LEFT JOIN medicine_substance m_s ON medicine.internal_id=m_s.medicine_id" +
					" LEFT JOIN code_value_codifa cvc ON medicine.pharm_form=cvc.id" +
					" LEFT JOIN code_value cv ON prescriptionMedicine.aifaNote=cv.id" +
					" LEFT JOIN substance substance ON m_s.substance_id=substance.internal_id" +
					" LEFT JOIN templatedosage templateDosage ON prescriptionMedicine.template_dosage_key=templateDosage.key AND templateDosage.is_active = 1" +
					" LEFT JOIN code_value_codifa eqGroup ON medicine.eq_group=eqGroup.id";
	
	private static final String PRESCRIPTION_DISCHARGE_WHERE_SQL = 
					" WHERE prescription.therapy=:therapyId";

	private static final String PRESCRIPTION_DISCHARGE_GROUPBY_SQL =
					" GROUP BY" +
					" prescription.internal_id," +
					" prescription.needsbased," +
					" medicine.internal_id," +
					" statusCode.code," +
					" prescriptionCode.code," +
					" prescription.duration," +
					" prescription.note," +
					" prescriptionRouteCode.lang_it," +
					" prescriptionInfusionTypeCode.lang_it," +
					" prescription.quantity_value," +
					" prescription.quantity_unit," +
					" prescription.infusion_speed_value," +
					" prescription.infusion_speed_unit," +
					" templatedosage.title," +
					" prescriptionMedicine.show_medicine_name," +
					" prescriptionMedicine.chronic_patient," +
					" prescriptionMedicine.irreplaceable," +
					" prescriptionMedicine.aifaNote," +
					" cvc.display_name," +
					" cv.code," +
					" cv.lang_it," +
					" prescriptionMedicine.therapeutic_plan";
	
	private static final String PRESCRIPTION_REPORT_SELECT_SUB_SQL =
					" SELECT * FROM (";
	
	private static final String PRESCRIPTION_REPORT_JOIN_SUB_SQL =
					")subquery" +
					" WHERE subquery.startDate < :end" +
					" AND (subquery.endDate IS NULL or subquery.endDate >= :start)" +
					" ORDER BY subquery.startDate";

	private static final String PRESCRIPTION_REPORT_SELECT_SQL =
					" SELECT" +
					" prescription.internal_id as internalId," +
					" prescription.extemporaneous as extemporaneous," +
					" prescription.urgent as urgent," +
					" prescription.continuous as continuous," +
					" prescription.needsbased as needsbased," +
					" prescriptionCode.code as code," +
					" CASE" +
					"   WHEN prescription.period_time_low > prescription.validity_period_time_low OR prescription.validity_period_time_low IS NULL" +
					"   THEN prescription.period_time_low" +
					"   ELSE prescription.validity_period_time_low" +
					" END as startDate," +
					" CASE" +
					"   WHEN prescription.period_time_high < prescription.validity_period_time_high OR prescription.validity_period_time_high IS NULL" +
					"   THEN prescription.period_time_high" +
					"   ELSE prescription.validity_period_time_high" +
					" END as endDate," +
					" prescription.note as note," +
					" prescriptionRouteCode.lang_it as route," +

					" CASE" +
					"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG (medicine.name_giv, ' ') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND (prescriptionInfusionTypeCode.lang_it IS NULL)) THEN LISTAGG (medicine.name_giv || ' ' || SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1) , ' + ') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 AND (prescriptionInfusionTypeCode.lang_it IS NOT NULL)) THEN prescriptionInfusionTypeCode.lang_it || ' + ' || LISTAGG (medicine.name_giv ||' ' || SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1), ' + ') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					" 	ELSE prescriptionInfusionTypeCode.lang_it" +
					" END as description," +
					/*
					" CASE" +
					"   WHEN prescriptionCode.code = 'PHARMA' THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END), ' ') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND (prescriptionInfusionTypeCode.lang_it IS NULL)) THEN LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) || ' ' || SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1) , ' + ') WITHIN GROUP (ORDER BY medicine.name_giv)" +
					"   WHEN (prescriptionCode.code = 'INFU' AND (INSTR(LISTAGG(prescriptionMedicine.dosage, '') WITHIN GROUP(ORDER BY medicine.name_giv), '=') > 0 OR prescription.continuous = 1 OR prescription.needsbased = 1) AND (prescriptionInfusionTypeCode.lang_it IS NOT NULL)) THEN prescriptionInfusionTypeCode.lang_it || ' + ' || LISTAGG ((CASE WHEN eqGroup.lang_it IS NULL THEN medicine.name_giv ELSE eqGroup.lang_it END) ||' ' || SUBSTR(prescriptionMedicine.dosage, INSTR(prescriptionMedicine.dosage, '=', -1) +1), ' + ') WITHIN GROUP (ORDER BY medicine.name_giv)" +					
					" 	ELSE prescriptionInfusionTypeCode.lang_it" +
					" END as description," +
					*/
					" LISTAGG (prescriptionMedicine.dosage, ',') WITHIN GROUP (ORDER BY prescriptionMedicine.dosage) as posology," +
					" CASE" +
					"   WHEN prescriptionCode.code='INFU' THEN prescription.quantity_value || '[' || prescription.quantity_unit || ']@' || prescription.infusion_speed_value || '[' || prescription.infusion_speed_unit  || ']'" +
					" END as velocity," +
					" templatedosage.title as templateString," +
					" author.name_fam AS authorSurname," +
					" author.name_giv AS authorName," +
					" deleteAuthor.name_fam AS delAuthorSurname," +
					" deleteAuthor.name_giv AS delAuthorName";
	
	private static final String PRESCRIPTION_REPORT_JOIN_SQL =
					" FROM" +
					" prescription prescription" +
					" LEFT JOIN code_value statusCode ON prescription.status_code=statusCode.id" +
					" LEFT JOIN code_value prescriptionCode ON prescription.code=prescriptionCode.id" +
					" LEFT JOIN code_value prescriptionRouteCode ON prescription.route_code=prescriptionRouteCode.id" +
					" LEFT JOIN code_value prescriptionInfusionTypeCode ON prescription.infusion_Type_Code=prescriptionInfusionTypeCode.id" +
					" LEFT JOIN prescription_medicine prescriptionMedicine ON prescription.internal_id=prescriptionMedicine.prescription_id" +
					" LEFT JOIN medicine medicine ON prescriptionMedicine.medicine_id=medicine.internal_id" +
					" LEFT JOIN templatedosage templateDosage ON prescriptionMedicine.template_dosage_key=templateDosage.key and templateDosage.is_active = 1" +
					" LEFT JOIN employee author ON prescription.author_id = author.internal_id" +
					" LEFT JOIN employee deleteAuthor ON prescription.cancelled_by_id = deleteAuthor.internal_id" +
					" LEFT JOIN code_value_codifa eqGroup ON medicine.eq_group=eqGroup.id";
	
	private static final String PRESCRIPTION_REPORT_WHERE_SQL =
					" WHERE prescription.therapy = :therapyId" +
					" AND prescription.is_active = 1" +
					" AND statusCode.code != 'new'";
	
	private static final String PRESCRIPTION_REPORT_GROUPBY_SQL =
					" GROUP BY" +
					" prescription.internal_id," +
					" prescription.root_prescription_id," +
					" statusCode.code," +
					" prescriptionCode.code," +
					" prescription.period_time_low," +
					" prescription.period_time_high," +
					" prescription.validity_period_time_low," +
					" prescription.validity_period_time_high," +
					" prescription.note," +
					" prescriptionRouteCode.lang_it," +
					" prescriptionInfusionTypeCode.lang_it," +
					" prescription.quantity_value," +
					" prescription.quantity_unit," +
					" prescription.infusion_speed_value," +
					" prescription.infusion_speed_unit," +
					" templatedosage.title," +
					" prescription.extemporaneous, " +
					" prescription.urgent, " +
					" prescription.continuous," +
					" prescription.needsbased," +
					" prescription.modified," +
					" author.name_fam," +
					" author.name_giv," +
					" deleteAuthor.name_fam," +
					" deleteAuthor.name_giv";

	/*
	private static final String sqlAdministrationReport = "select  " +
			"statusCode.code as status, " +
			"case when substanceadmin.planned_date is null then substanceadmin.administrateddate_time_low else substanceadmin.planned_date end AS plannedDate, " +
			"case when statusDetailsCode.lang_it is not null then statusDetailsCode.lang_it || ': ' else '' end || substanceadmin.text_string  AS note, " +
			"author.name_fam as authorSurname, " +
			"author.name_giv as authorName," +
			"substanceadmin.prescription as prescriptionId " +
			"from substanceadmin " +
			"left join employee author on substanceadmin.author_id = author.internal_id " +
			"left join code_value statusCode on substanceadmin.status_code=statusCode.id " +
			"LEFT JOIN code_value statusDetailsCode ON substanceadmin.status_details_code = statusDetailsCode.id " +
			"where " +
			"substanceadmin.prescription IN (:prescriptionIds) " +
			"AND ((substanceadmin.planned_date < :end AND substanceadmin.planned_date >= :start) OR (planned_date is null AND substanceadmin.administrateddate_time_low < :end AND substanceadmin.administrateddate_time_low >= :start)) " +
			"order by substanceadmin.prescription, substanceadmin.planned_date";	
	*/
	
	private static final String sqlAdministrationReport = "SELECT * FROM (( " +
			" SELECT " +
			" statusCode.code AS status, " +
			" CASE WHEN substanceadmin.planned_date is null then substanceadmin.administrateddate_time_low else substanceadmin.planned_date end AS plannedDate, " +
			" CASE WHEN statusDetailsCode.lang_it is not null then statusDetailsCode.lang_it || ': ' else '' end || substanceadmin.text_string  AS note, " +
			" author.name_fam AS authorSurname, " +
			" author.name_giv AS authorName," +
			" substanceadmin.prescription AS prescriptionId " +
			" FROM substanceadmin " +
			" LEFT JOIN employee author on substanceadmin.author_id = author.internal_id " +
			" LEFT JOIN code_value statusCode on substanceadmin.status_code=statusCode.id " +
			" LEFT JOIN code_value statusDetailsCode ON substanceadmin.status_details_code = statusDetailsCode.id " +
			" WHERE " +
			" substanceadmin.prescription IN (:prescriptionIds) " +
			" AND ((substanceadmin.planned_date < :end AND substanceadmin.planned_date >= :start) OR (planned_date is null AND substanceadmin.administrateddate_time_low < :end AND substanceadmin.administrateddate_time_low >= :start)) " +
			
			" ) UNION ( " + 
			
			" SELECT " + //query aggiunta per lo stop delle infusioni continue
			" statusCode.code AS status, " +
			" administrateddate_time_high AS plannedDate, " +
			" 'STOP' || CASE WHEN statusDetailsCode.lang_it is not null then statusDetailsCode.lang_it || ': ' else '' end || substanceadmin.text_string  AS note, " +
			" author.name_fam AS authorSurname, " +
			" author.name_giv AS authorName, " +
			" substanceadmin.prescription AS prescriptionId " +
			" FROM substanceadmin " +
			" LEFT JOIN employee author on substanceadmin.stop_author_id = author.internal_id " +
			" LEFT JOIN code_value statusCode on substanceadmin.status_code=statusCode.id " +
			" LEFT JOIN code_value statusDetailsCode ON substanceadmin.status_details_code = statusDetailsCode.id " +
			" WHERE " +
			" substanceadmin.prescription IN (:prescriptionIds) " +
			" AND (substanceadmin.administrateddate_time_high < :end AND substanceadmin.administrateddate_time_high >= :start) " +
			" AND substanceadmin.stop_author_id IS NOT NULL " +
			
			" )) ORDER BY prescriptionId, plannedDate "; 
	
	
	private static final String ADMINISTRATION_HISTORY_SELECT_HQL =
			" SELECT" +
			" 'A' as type," +
			" statusCode.code as statusCode," +
			" statusDetailsCode.code as statusDetailsCode," +
			" administration.plannedDate as plannedDate," +
			" administration.administratedDate.low as erogationDate," +
			" CASE" +
			"	WHEN (prescriptionCode.code = 'INFU' AND administration.administratedDate.low IS NOT NULL AND prescription.infusionDuration.unit = 'min')" +
			"		THEN (administration.administratedDate.low + prescription.infusionDuration.value/1440)" +
			"	WHEN (prescriptionCode.code = 'INFU' AND administration.administratedDate.low IS NOT NULL AND prescription.infusionDuration.unit = 'h')" +
			"		THEN (administration.administratedDate.low + prescription.infusionDuration.value/24)" +
			"	ELSE NULL" +
			" END as infusionEndDate," +
			" CASE" +			
			" 	WHEN administration.administratedDate.low IS NOT NULL THEN (UPPER(author.name.fam) || ' ' || author.name.giv || ' [' || authorCode.langIt || ' (' || authorCode.code || ')]')" +
			" END as author," +
			" administration.text.string as note," +
			" prescription.period.high as endDate";
	
	private static final String ADMINISTRATION_HISTORY_JOIN_HQL =
			" FROM SubstanceAdministration administration" +
			" LEFT JOIN administration.statusCode statusCode" +
			" LEFT JOIN administration.statusDetailsCode statusDetailsCode" +			
			" LEFT JOIN administration.author author" +
			" LEFT JOIN administration.authorRole authorCode" +
			" LEFT JOIN administration.prescription prescription" +
			" LEFT JOIN prescription.code prescriptionCode" +
			" LEFT JOIN prescription.rootPrescription rootPrescription";
	
	private static final String ADMINISTRATION_HISTORY_WHERE_HQL =
			" WHERE (prescription.internalId = :prescriptionId" +
			" OR rootPrescription.internalId = :prescriptionId)";
	
	private static final String ADMINISTRATION_HISTORY_ORDERBY_HQL =
			" ORDER BY administration.plannedDate";
	
	private static final String ALLERGY_COUNT_SQL = "select " +
			" count (allergy.internal_id) as allergy" +
			" from therapy" +
			" join patient_encounter pe on therapy.patient_encounter = pe.internal_id" +
			" join patient on pe.patient_id = patient.internal_id" +
			" left join allergy on allergy.patient_id = patient.internal_id" +
			" where therapy.internal_id = :therapyId" +
			" and allergy.confirmed = 1 and allergy.is_active = 1";
	
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {

		Response response = initRefresh(restrictions, page);
		
		log.info("[cid="+Conversation.instance().getId()+"] DrugPrescriber Init");
		log.debug("[cid="+Conversation.instance().getId()+"] DrugPrescriber Init details - restrictions: " + restrictions + " (page " + page + ")");
		
		return response;
	}

	@GET
	@Path("refresh/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		
		Response response = initRefresh(restrictions, page);
		
		log.info("[cid="+Conversation.instance().getId()+"] DrugPrescriber Refresh");
		log.debug("[cid="+Conversation.instance().getId()+"] DrugPrescriber Refresh details - restrictions: " + restrictions + " (page " + page + ")");
		
		return response;
	}

	private Response initRefresh(PathSegment restrictions, int page) {
		try {

			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			List<Map<String, Object>> results = read(restrictionMap, page, false);

			String readUrl = BASE_REST_URL + "drugprescriber/init/" + restrictions + "/"; //FIXMEEEEEEEEEEEEEEEEEEEEE

			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); //FIXME: readPageSize: sql + union per order by
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			res.setMain(dm);
			
			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> allergy = countAllergy(restrictionMap);
			additional.put("Allergy", allergy);
			res.setAdditional(additional);
			
			String json = mapper.writeValueAsString(res);

			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in DrugPrescriber Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing drug prescriber data").build();
		}
	}
	
	@GET
	@Path("printReport/{restrictions}")
	public Response printReport(@PathParam("restrictions") PathSegment restrictions) {
		try {							

			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			if (restrictionMap.get("prescriptionDischarge") != null) {
				List<Map<String, Object>> results = read(restrictionMap, 0, true);	
				IdataModel<Map<String, Object>> dm = new PhiDataModel<Map<String,Object>>(results, "Prescription");
				Contexts.getConversationContext().set("PrescriptionList", dm);
			} else {
				readReportPrescriptions(restrictionMap);
			}
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error printReport", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error printReport").build();
		}
	}

	public void readReportPrescriptions(MultivaluedMap<String, String> restrictionMap) throws Exception {
		Long therapyId = null;
		Date start = null;
		Date end = null;
		if (restrictionMap != null) {
			if (restrictionMap.get("therapyId") != null) {
				therapyId = Long.parseLong(restrictionMap.get("therapyId").get(0));
			}

			if (restrictionMap.get("startDate") != null) {
				start = decodeISO8601((String)restrictionMap.get("startDate").get(0));
			}

			if (restrictionMap.get("endDate") != null) {
				end = decodeISO8601((String)restrictionMap.get("endDate").get(0));
			}
		}

		readReportPrescriptions(therapyId, start, end);
	}

	public void readReportPrescriptions(Long therId, Date startDate, Date endDate) throws Exception {
		// Get prescriptions
		String currentLang = Locale.instance().getLanguage();
		
		String sqlReport = PRESCRIPTION_REPORT_SELECT_SUB_SQL + PRESCRIPTION_REPORT_SELECT_SQL + PRESCRIPTION_REPORT_JOIN_SQL + PRESCRIPTION_REPORT_WHERE_SQL;
		
		// Add privacy restrictions
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			sqlReport += " AND prescription.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
		}
		
		sqlReport += PRESCRIPTION_REPORT_GROUPBY_SQL + PRESCRIPTION_REPORT_JOIN_SUB_SQL;

		if (!currentLang.equals("it")) {
			sqlReport = sqlReport.replace("lang_it", "lang_" + currentLang);
		}		
		
		// Replace LISTAGG
		sqlReport = ReplaceListaggBean.replaceListagg(sqlReport);
		
		SQLQuery qryReport = ca.createHibernateNativeQuery(sqlReport);

		// Parameters init
		Long therapyId = therId;
		Calendar cal = Calendar.getInstance();
		Date start = null;
		if (startDate != null) {
			cal.setTime(startDate);
			start = truncateDate(cal);
		}
		Date end = null;
		if (endDate != null) {
			cal.setTime(endDate);
			cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
			end = truncateDate(cal);
		}

		// TODO: DA RIVEDERE UN PO' MEGLIO... HA SENSO QUESTA COSA?
		if (therapyId != null) {
			qryReport.setParameter("therapyId", therapyId);
		}
		if (start != null) {
			qryReport.setParameter("start", start);
		}
		if (end != null) {
			qryReport.setParameter("end", end);
		}

		// Add privacy parameters
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			qryReport.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
			qryReport.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
		}

		qryReport.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
		List<Map<String, Object>> results = qryReport.list();

		for (Map<String, Object> res : results) {
			fixFrequency(res, true,false);
		}				

		Contexts.getConversationContext().set("PrescriptionList", results);

		//Set date columns
		Calendar columnDate = Calendar.getInstance();
		columnDate.setTime(start);

		//pages
		List<Integer> pageIndexes = new ArrayList<Integer>();
		Map<Integer, List<Date>> columnsPages = new LinkedHashMap<Integer,List<Date>>();
		List<Date> columns = new ArrayList<Date>();;

		Integer pageIndex = 0;
		Integer index = 0;

		while (columnDate.getTime().before(end)) {
			columns.add(columnDate.getTime());
			columnDate.set(Calendar.DATE, columnDate.get(Calendar.DATE)+1);
			index++;
			if(index == 4){
				//save page
				pageIndexes.add(pageIndex);
				columnsPages.put(pageIndex,columns); 
				pageIndex++;
				//reinitialize
				columns = new ArrayList<Date>();
				index = 0;
			}
		}

		if(index > 0){//save last page
			pageIndexes.add(pageIndex);
			columnsPages.put(pageIndex,columns);
		}

		Contexts.getConversationContext().set("PageIndexes", pageIndexes);
		Contexts.getConversationContext().set("ColumnsPages", columnsPages);

		if (results.size() > 0) {
			// Get administrations
			String sqlReportAdministration = sqlAdministrationReport;

			if (!currentLang.equals("it")) {
				sqlReportAdministration = sqlReportAdministration.replace("lang_it", "lang_" + currentLang);
			}		

			SQLQuery qryReportAdministration = ca.createHibernateNativeQuery(sqlReportAdministration);

			List<Long> prescriptionIds = new ArrayList<Long>();

			for (Map<String, Object> res : results) {
				prescriptionIds.add(Long.parseLong(res.get("internalid").toString())); //Convert BigDecimal to Long
			}
			qryReportAdministration.setParameterList("prescriptionIds", prescriptionIds);

			if (start!= null) {
				qryReportAdministration.setParameter("start", start);
			}
			if (end!= null) {
				qryReportAdministration.setParameter("end", end);
			}

			qryReportAdministration.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			results = qryReportAdministration.list();

			//arrange administrations by prescription and date 
			Map<Integer,Map<String,Map<String, List<Map<String, Object>>>>> administrationsPages = new LinkedHashMap<Integer,Map<String,Map<String, List<Map<String, Object>>>>>();
			Map<String,Map<String, List<Map<String, Object>>>> administrationList = new LinkedHashMap<String,Map<String, List<Map<String, Object>>>>();
			Map<String,List<Map<String, Object>>> row = new LinkedHashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> cell = new ArrayList<Map<String, Object>>();

			Calendar checkingDate = Calendar.getInstance();
			for (Integer pageNumber : pageIndexes) {//scroll pages
				for (long prescriptionId : prescriptionIds){//scroll rows
					columns = columnsPages.get(pageNumber);			
					for (Date date : columns) {//scroll columns
						for (Map<String, Object> administration : results) {
							if (prescriptionId == Long.parseLong(administration.get("prescriptionid").toString())) {
								if ((Date)administration.get("planneddate")!=null) {
									checkingDate.setTime((Date)administration.get("planneddate"));
									if (truncateDate(checkingDate).equals(date)) {//found
										cell.add(administration);
									}
								}
							}
						}
						//add cell
						row.put(String.valueOf(date.getTime()), cell);
						cell = new ArrayList<Map<String, Object>>();
					}
					administrationList.put(String.valueOf(prescriptionId), row);
					row = new LinkedHashMap<String, List<Map<String, Object>>>();
				}
				administrationsPages.put(pageNumber,administrationList);
				administrationList = new LinkedHashMap<String,Map<String, List<Map<String, Object>>>>();
			}
			Contexts.getConversationContext().set("AdministrationsPages", administrationsPages);
		} else {
			Contexts.getConversationContext().set("AdministrationsPages", null);
		}

	}

	private Date truncateDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	
	/**
	 * Executes sqlPrescription or sqlPrescriptionDischarge queries
	 * @param restrictionMap
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> read(Map<String, List<String>> restrictionMap, int page, boolean report) {

		Long therapyId = null;

		if (restrictionMap.get("therapyId") != null) {
			therapyId = Long.parseLong(restrictionMap.get("therapyId").get(0));
		} else {
			//return Response.status(Response.Status.BAD_REQUEST).entity("therapyId required!").build();
			throw new IllegalArgumentException("therapyId required!");
		}


		String sql = "";

		if (restrictionMap.get("prescriptionDischarge") != null) {
			sql = PRESCRIPTION_DISCHARGE_SELECT_SQL + PRESCRIPTION_DISCHARGE_JOIN_SQL + PRESCRIPTION_DISCHARGE_WHERE_SQL;
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				sql += " AND prescription.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			sql += PRESCRIPTION_DISCHARGE_GROUPBY_SQL;
			
		} else {
			sql = PRESCRIPTION_SELECT_SQL + PRESCRIPTION_JOIN_SQL + PRESCRIPTION_WHERE_SQL;
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				sql += " AND prescription.creation_date BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			sql += PRESCRIPTION_GROUPBY_SQL;
		}

		String currentLang = Locale.instance().getLanguage();

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
		
		// Replace LISTAGG
		sql = ReplaceListaggBean.replaceListagg(sql);

		SQLQuery qry = ca.createHibernateNativeQuery(sql);

		qry.setParameter("therapyId", therapyId);

		if (sql.contains(":sysdate")) {
			qry.setParameter("sysdate", new Date());
		}
		
		// Add privacy parameters
		if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
			qry.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
			qry.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
		}

		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
		List<Map<String, Object>> results = qry.list();

		if (restrictionMap.get("dischargeLetter") != null){

			for (Map<String, Object> res : results) {
				fixFrequency(res, report, true);
			}
		} else {
			for (Map<String, Object> res : results) {
				fixFrequency(res, report, false);
			}
		}

		return results;
	}

	/**
	 * Copy Prescriptions to PrescriptionDischarges
	 * 
	 * @param prescriptionIds id of Prescriptions to copy
	 * @return
	 */
	@POST
	@Path("/prescriptiondischarge")
	//@Produces(MediaType.APPLICATION_JSON)
	public Response copyPrescriptionsToPrescriptionDischarges(@FormParam("id") List<Long> prescriptionIds,  @DefaultValue("false") @FormParam("showMedicineName") Boolean showMedicineName) {
		try {

			Employee e = UserBean.instance().getCurrentEmployee();

			for (Long id : prescriptionIds) {
				Prescription p = ca.get(Prescription.class, id);

				//Copy
				PrescriptionDischarge pd = new PrescriptionDischarge();
				pd.setModified(false);
				pd.setNeedsBased(p.getNeedsBased());
				pd.setAuthor(e);
				pd.setCode(p.getCode());				
				pd.setDuration(1);
				pd.setNote(p.getNote());
				for (PrescriptionMedicineGeneric pm : p.getPrescriptionMedicine()) {
					PrescriptionDischargeMedicine pdm = new PrescriptionDischargeMedicine();
					pdm.setDosage(pm.getDosage());
					pdm.setMedicine(pm.getMedicine());
					if(showMedicineName) {
						pdm.setShowMedicineName(true);
					}
					pd.addPrescriptionMedicine(pdm);
				}
				pd.setRouteCode(p.getRouteCode());
				pd.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC","StatusCodes","active")); // Always active
				pd.setTherapy(p.getTherapy());

				ca.create(pd);
				
				Events.instance().raiseEvent(PhiEvent.CREATE, pd);				
			}

			ca.flushSession();

			return Response.ok().build();

		} catch (PhiException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error coping Prescriptions to PrescriptionDischarges, ids: " + prescriptionIds, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error coping Prescriptions to PrescriptionDischarges, ids: " + prescriptionIds).build();
		}
	}
	
	@GET
	@Path("administrationHistory/{prescriptionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response administrationHistory(@PathParam("prescriptionId") long prescriptionId){
		try {
			String administrationHistoryHql = ADMINISTRATION_HISTORY_SELECT_HQL + ADMINISTRATION_HISTORY_JOIN_HQL + ADMINISTRATION_HISTORY_WHERE_HQL;
			
			// Add privacy restrictions
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				administrationHistoryHql += " AND execution.plannedDate BETWEEN :datePrivacyForm AND :datePrivacyTo";
			}
			
			administrationHistoryHql += ADMINISTRATION_HISTORY_ORDERBY_HQL;
						
			Date now = new Date();
			
			Query qryAdministrationHistory = ca.createHibernateQuery(administrationHistoryHql);
			
			qryAdministrationHistory.setParameter("prescriptionId", prescriptionId);
			
			// Add privacy parameters
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy")) && TransferPrivacy.instance().getFilterDate() != null) {
				qryAdministrationHistory.setParameter("datePrivacyForm", TransferPrivacy.instance().getFilterDate().getLow());
				qryAdministrationHistory.setParameter("datePrivacyTo", TransferPrivacy.instance().getFilterDate().getHigh());
			}
			
			qryAdministrationHistory.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Map<String, Object>> results = qryAdministrationHistory.list();
			
			results = ErogationStatusFixer.fixErogations(results, true);
			
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error DrugPrescriberRest administrationHistory", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error DrugPrescriberRest administrationHistory").build();
		}
	}
	
	private List<Map<String, Object>> countAllergy(Map<String, List<String>> restrictionMap) {
		Long therapyId = null;

		if (restrictionMap.get("therapyId") != null) {
			therapyId = Long.parseLong(restrictionMap.get("therapyId").get(0));
		} else {
			//return Response.status(Response.Status.BAD_REQUEST).entity("therapyId required!").build();
			throw new IllegalArgumentException("therapyId required!");
		}
		
		SQLQuery qryAllergy = ca.createHibernateNativeQuery(ALLERGY_COUNT_SQL);
		qryAllergy.setParameter("therapyId", therapyId);
		qryAllergy.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
		List<Map<String, Object>> results = qryAllergy.list();
		return results;
	}
	
	//FIXME: merge with NurseActivityRest.fixFrequency(Map<String, Object> prescription, boolean report)
	public static void fixFrequency(Map<String, Object> prescription, boolean report, boolean dischargeLetter) {

		// PHARMA or INFU
		String code = (String)prescription.get("code");
		// PHARMA: 1@08:00=2[CPS]|1@18:00=3[CPS]
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

		String morningDose = "";
		String afternoonDose = "";
		String eveningDose = "";

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
				
				//posology = posology.replace(".", ",");
			}
			
			if (truez.equals(prescription.get("needsbased"))) {
				quantity = velocity + " " + fb.getStaticTranslation("needsbased"); 
			} else if (truez.equals(prescription.get("continuous"))) {
				quantity = velocity + " " + fb.getStaticTranslation("continuous");
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

		} else if ("PHARMA".equals(code)) {

			if (truez.equals(prescription.get("needsbased"))) {

				// Add spaces before '['
				quantity = posology.replace("[", " [") + " " + fb.getStaticTranslation("needsbased"); 

			} else if (truez.equals(prescription.get("extemporaneous"))) {

				dose = posology.substring(posology.indexOf("=") + 1);					
				// Add spaces before '['
				quantity = dose.replace("[", " [") + " " + fb.getStaticTranslation("extemporaneous");
			
			} else if (truez.equals(prescription.get("urgent"))) {

				dose = posology.substring(posology.indexOf("=") + 1);					
				// Add spaces before '['
				quantity = dose.replace("[", " [") + " " + fb.getStaticTranslation("urgent");

			} else {

				String[] dosagesString = posology.split("\\|");

				ArrayList<Object> dosages = new ArrayList<Object>();
				Map<String, String> dosage = new HashMap<String, String>();

				String previousDose = "";
				boolean differentDoses = false;

				for (int i = 0; i < dosagesString.length; i++) {

					String dosageString = dosagesString[i];						

					dosage = new HashMap<String, String>();

					//if dischargeLetter and if the format of the string is not @dayGAp@time=dose
					//create a fake prescription for the morning
					if(dischargeLetter && dosageString.indexOf("@")==-1){
						dosage.put("time","8:00");
						dosage.put("dose",dosageString.replace("[", " ["));
					}
					else{
						if (dosageString.indexOf("@")>=0) { //avoid error 
							dosage.put("dayGap", dosageString.substring(0, dosageString.indexOf("@")));
						}
						if (dosageString.indexOf("=")>=0) { //avoid error 
							dosage.put("time", dosageString.substring(dosageString.indexOf("@") + 1, dosageString.indexOf("=")));
							dosage.put("dose", dosageString.substring(dosageString.indexOf("=") + 1));
						}
					}
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

					if(dischargeLetter){

						/* TIME INTERVAL: 
						 * 6:00 - 10:59 -> morningDose
						 * 11:00 - 14:59 -> afternoonDose
						 * 15:00 .... -> eveningDose
						 *  
						 */
						for (int i = 0; i < dosages.size(); i++)
						{
							Map<String, String> currentDosage = (Map<String, String>) dosages.get(i);
							if(Integer.parseInt(currentDosage.get("time").replace(":", "")) > 599 && Integer.parseInt(currentDosage.get("time").replace(":", "")) < 1100 )
								morningDose += currentDosage.get("dose").replace("[", " [") + ", ";// + fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
							else if(Integer.parseInt(currentDosage.get("time").replace(":", "")) > 1100 && Integer.parseInt(currentDosage.get("time").replace(":", "")) < 1600 )
								afternoonDose += currentDosage.get("dose").replace("[", " [") + ", ";// + fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
							else {
								eveningDose += currentDosage.get("dose").replace("[", " [") + ", "; // + fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
							}

						}

						// Remove last comma
						if(!morningDose.equals(""))
							morningDose = morningDose.substring(0, morningDose.length() - 2);
						if(!afternoonDose.equals(""))
							afternoonDose = afternoonDose.substring(0, afternoonDose.length() - 2);
						if(!eveningDose.equals(""))
							eveningDose = eveningDose.substring(0, eveningDose.length() - 2);

						prescription.put("morningDose",morningDose);
						prescription.put("afternoonDose",afternoonDose);
						prescription.put("eveningDose",eveningDose);
					}

					else {

						Map<String, String> firstDosage = (Map<String, String>) dosages.get(0); 	
						if (firstDosage.get("dayGap") != null) {
							quantity = fb.getStaticTranslation("Every") + " " + firstDosage.get("dayGap") + " " + fb.getStaticTranslation("day/s") + " ";
						} else {
							quantity = "-";
						}
						
						for (int i = 0; i < dosages.size(); i++)
						{
							Map<String, String> currentDosage = (Map<String, String>) dosages.get(i);
							// Add spaces before '['
							if (currentDosage.get("dose") != null) { 
								quantity += currentDosage.get("dose").replace("[", " [") + " " + fb.getStaticTranslation("at_hour") + " " + currentDosage.get("time") + ", ";
							}
						}

						// Remove last comma
						if (quantity.length()>2) {
							quantity = quantity.substring(0, quantity.length() - 2);
						}
					}

				}
			}				
		}

		prescription.put("quantity", quantity.replace(".", ",").replace(" ,", " 0,"));
	}		

}
