package com.phi.rest.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
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
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;

/**
 * ADT Dashboard rest methods init and refresh
 * 
 * Example: read Impatient (IMP) and DayHospital (DH) PatientEncounter in status new, active or held, hospitalized in ward.internalId = 8 
 * http://localhost:8080/PHI_CI/resource/rest/adt/init/CaseTypes=IMP;CaseTypes=DH;StatusCode=new;StatusCode=active;StatusCode=held;Wards=8;/1
 *
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ADTRest")
@Path("/adt")
public class ADTRest extends BaseDashboardRest {

	private static final long serialVersionUID = 5997108853652524016L;
	
	
	private static final String ENCOUNTERS_SELECT_HQL = 
			" SELECT" +
			" encounter.internalId as internalId," +
			" encounter.details as note," +
			" code.code as code," +
			" statusCode.code as statusCode," +
			" patient.internalId as patient_internalId," +
			" UPPER(patient.name.fam) || ' ' || patient.name.giv as patient_name," +
			" patient.birthTime as patient_birthTime," +
			" patient.fiscalCode as patient_fiscalCode," +
			" encounter.availabilityTime as availabilityTime," +
			" encounter.visitNumber.extension as visitNumber," +	
			" encounter.preadmitNumber.extension as preadmitNumber," +
			" location.internalId as location_internalId," +
			" location.name.giv as location_name," +
			" CASE" +
			"	WHEN support IS NOT NULL AND support.internalId != location.internalId THEN support.internalId" +
			"	WHEN support IS NULL OR support.internalId = location.internalId THEN NULL" +
			" END as support_internalId," +
			" CASE" +
			"	WHEN support IS NOT NULL AND support.internalId != location.internalId THEN support.name.giv" +
			"	WHEN support IS NULL OR support.internalId = location.internalId THEN NULL" +
			" END as support_name," +
			" encounter.roomString as locationRoom," + 
			" encounter.bedString as locationBed," +
			" encounter.dischargeDate as dischargeDate," +
			" encounter.complexity as complexity," +
			" encounter.optionTPN as optionTPN," +
			" encounter.lastChecking as checkingAvailabilityTime," +
			" encounter.pain as painValue," +
			" encounter.bradenScore as bradenScore," +
			" encounter.brassScore as brassScore," +
			" encounter.lastClinicalDiary as clinicalDiaryAvailabilityTime," +
			" encounter.nortonScore as nortonScore," +	
			" encounter.fallRisk as fallRisk," +
			" encounter.barcode as barcode," +
			" encounter.nurseReportCode.id as nurseReportCode," +
			" (SELECT nurseReport.confirmed FROM NurseReport nurseReport WHERE nurseReport.patientEncounter.internalId = encounter.internalId) as nrConfirmed";
			
				
	private static final String ENCOUNTERS_JOIN_HQL =
			" FROM PatientEncounter encounter" +
			" JOIN encounter.patient patient" +
			" LEFT JOIN encounter.code code" +
			" LEFT JOIN encounter.statusCode statusCode" +
			" LEFT JOIN encounter.assignedSDL location" +
			" LEFT JOIN encounter.temporarySDL support";	
				
	private static final String ENCOUNTERS_WHERE_HQL =		
			" WHERE 1=1";
	
	private static final String ENCOUNTERS_WHERE_IS_ACTIVE_HQL =		
			" WHERE encounter.isActive = true";
		
	private static final String ORDER_BY_SURNAME_NAME_ASC =
			" ORDER BY patient.name.fam ASC, patient.name.giv ASC, encounter.availabilityTime ASC";
	
	private static final String ORDER_BY_SURNAME_NAME_DESC =
			" ORDER BY patient.name.fam DESC, patient.name.giv DESC, encounter.availabilityTime DESC";
	
	private static final String ORDER_BY_ADMISSION_DATE_ASC =
			" ORDER BY encounter.availabilityTime ASC";
	
	private static final String ORDER_BY_ADMISSION_DATE_DESC =
			" ORDER BY encounter.availabilityTime DESC";
	
	private static final String ORDER_BY_WARD_ROOM_BED_ASC =
			" ORDER BY" +
			" CASE" +
			"   WHEN support IS NOT NULL THEN support.name.giv ELSE location.name.giv" +
			" END ASC," +
			" CASE" +
			"   WHEN encounter.roomString IS NULL THEN 1 ELSE 0" +
			" END," +			
			" LENGTH(encounter.roomString) ASC," +
			" encounter.roomString ASC," +
			" CASE" +
			"   WHEN encounter.bedString IS NULL THEN 1 ELSE 0" +
			" END," +
			" LENGTH(encounter.bedString) ASC," +
			" encounter.bedString ASC," +
			" encounter.availabilityTime ASC";
	
	private static final String ORDER_BY_WARD_ROOM_BED_DESC =
			" ORDER BY" +
			" CASE" +
			"   WHEN support IS NOT NULL THEN support.name.giv ELSE location.name.giv" +
			" END DESC," +
			" CASE" +
			"   WHEN encounter.roomString IS NULL THEN 0 ELSE 1" +
			" END," +			
			" LENGTH(encounter.roomString) DESC," +
			" encounter.roomString DESC," +
			" CASE" +
			"   WHEN encounter.bedString IS NULL THEN 0 ELSE 1" +
			" END," +
			" LENGTH(encounter.bedString) DESC," +
			" encounter.bedString DESC," +
			" encounter.availabilityTime DESC";
	
					
	
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}
						
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
						
			ListDatamodel<Map<String, Object>> dm = readEncounterList(restrictionMap, page);

			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();

			res.setMain(dm);
			
			boolean readNursingReportStatus = true;
			
			if (readNursingReportStatus) {
			
				Vocabularies voc = VocabulariesImpl.instance();
				
				Map<String,List<Map<String,Object>>> dictionaries = new HashMap<String, List<Map<String,Object>>>();
				List<SelectItem> siResults = voc.getIdValues("PHIDIC:NursingReport");			
						
				List<Map<String,Object>> listResult = new ArrayList<Map<String,Object>>();
			
				for (SelectItem si : siResults) {
					Map<String,Object> code = new HashMap<String, Object>();
				
					CodeValue cv = (CodeValue)si.getValue();
					
					@SuppressWarnings("unchecked")
					Class<CodeValue> cvClass = HibernateProxyHelper.getClassWithoutInitializingProxy(cv);
					
					cv = ca.get(cvClass, cv.getId());
					
					code.put("id", cv.getId());
					code.put("code", cv.getCode());										
					code.put("displayName", cv.getDisplayName()); 
					code.put("type", cv.getType());	
					code.put("langIt", cv.getLangIt());
					code.put("langDe", cv.getLangDe());
					code.put("langEn", cv.getLangEn());							
					code.put("entityName", cvClass.getName());
																				
					listResult.add(code);
				}
				
				dictionaries.put("PHIDIC_NursingReport", listResult);
			
				res.setDictionaries(dictionaries);
			}
			
			String json = mapper.writeValueAsString(res);
										
			log.info("[cid="+Conversation.instance().getId()+"] ADT Init (page " + page + ")");
			log.debug("[cid="+Conversation.instance().getId()+"] ADT Init details - restrictions: " + restrictions + " (page " + page + ")");
					
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in ADT Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in adt init").build();
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
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
						
			ListDatamodel<Map<String, Object>> dm = readEncounterList(restrictionMap, page);
			
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			
			res.setMain(dm);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] ADT Refresh (page " + page + ")");
			log.debug("[cid="+Conversation.instance().getId()+"] ADT Refresh details - restrictions: " + restrictions + " (page " + page + ")");
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in ADT Refresh with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error in adt refresh").build();
		}
	}

	@Override
	public Response printReport(PathSegment restrictions) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ListDatamodel<Map<String, Object>> readEncounterList(MultivaluedMap<String, String> restrictionMap, int page){
		
		UserBean ub = (UserBean) Component.getInstance("userBean");
		
		Boolean isActiveFilter = true;
		if (restrictionMap.get("isActiveFilter") != null && "false".equals(restrictionMap.get("isActiveFilter").get(0))) {
			isActiveFilter = false;
		}
		List<String> caseTypes = restrictionMap.get("encounterTypes");
		List<String> statusCodes = new ArrayList<String>();
		
		String encounterHql = ENCOUNTERS_SELECT_HQL;
		
		if (caseTypes.contains("PRE")){
			encounterHql += 
					"," +
					" priority.code as priorityCode," +
					" intervention.langIt as intervention";
		}
		
		encounterHql += ENCOUNTERS_JOIN_HQL;
		
		if (caseTypes.contains("PRE")){
			encounterHql += 
					" LEFT JOIN encounter.priorityCode priority" +
					" LEFT JOIN encounter.intervention intervention";					
		}
		
		if (isActiveFilter) {
			encounterHql += ENCOUNTERS_WHERE_IS_ACTIVE_HQL;
		} else {
			encounterHql += ENCOUNTERS_WHERE_HQL;
		}
				
		String currentLang = Locale.instance().getLanguage();
		
		if (!currentLang.equals("it")) {
			encounterHql = encounterHql.replace("langIt", "lang" + WordUtils.capitalize(currentLang));
		}
				
		if (caseTypes != null){
			encounterHql += " AND code.code IN (:encounterTypes)"; 
		}
				
		if (caseTypes.contains("PRE")) {
			statusCodes = restrictionMap.get("statusCodes");
		} else {
			
			String showHeld = "false";
			if (restrictionMap.containsKey("showheld"))
				showHeld = restrictionMap.get("showheld").get(0);
		
			if (showHeld.equalsIgnoreCase("true"))
				statusCodes.add("held");
			else {
				
				statusCodes = restrictionMap.get("statusCodes");
				statusCodes.remove("held");
			}	
		}
		encounterHql += " AND statusCode.code IN (:statusCodes)"; 
		
		String role = ub.getRole();
		
		if (role.equalsIgnoreCase("2"))
		{
			encounterHql += " AND (encounter.brassScore >= :maxBrassValue" +
							" OR (" +
							"	SELECT COUNT(procedure)" +
							"	FROM Appointment appointment, InternalActivity internalActivity" +
							"	JOIN appointment.performedProcedure procedure" +
							"	JOIN procedure.codeIcd9 procedureCode" +
							"	WHERE appointment.internalId = internalActivity.appointment.internalId" +
							"   AND internalActivity.patientEncounter.internalId = encounter.internalId" +
							"	AND procedureCode.code = 'AP9'" +
							"	AND internalActivity.statusCode.code <> 'cancelled'" +
							" ) > 0" +
							" OR (" +
							"	SELECT COUNT(clinicalPathways)" +
							"	FROM ClinicalPathwaysVCO clinicalPathways, InternalActivity internalActivity" +
							"	JOIN clinicalPathways.patientEncounter encounterAmb" +
							"	JOIN encounterAmb.appointment appointment" +
							"	WHERE appointment.internalId = internalActivity.appointment.internalId" +
							"   AND internalActivity.patientEncounter.internalId = encounter.internalId" +
							"	AND (clinicalPathways.confirmed = true OR clinicalPathways.saveForReservation = true)" +
							" ) > 0)";
		}
		
		List<Long> wards = new ArrayList<Long>(); 
		
		List<String> wardList =  restrictionMap.get("wards");
		if(wardList!=null){
			for (String ward : wardList){
				wards.add(Long.parseLong(ward));
			}
		}
		if (wards.size() > 0){		
			if (restrictionMap.get("assigned") != null && restrictionMap.get("temporary") != null){				
				encounterHql += " AND (support.internalId IN (:wards) OR location.internalId IN (:wards))";				
			} else if (restrictionMap.get("assigned") != null){
				encounterHql += " AND location.internalId IN (:wards)";
			} else if (restrictionMap.get("temporary") != null){
				encounterHql += " AND (support.internalId IN (:wards) OR (location.internalId IN (:wards) AND support.internalId IS NULL)) ";
			}
		}
		
		String patientName = "";
		if (restrictionMap.get("patientName") != null){
			patientName = restrictionMap.get("patientName").get(0);
			if (patientName != null && patientName != "")
				encounterHql += " AND LOWER(patient.name.giv) like LOWER(:patientName)"; 
		}		
		
		String patientSurname = "";
		if (restrictionMap.get("patientSurname") != null){
			patientSurname = restrictionMap.get("patientSurname").get(0);
			if (patientSurname != null && patientSurname != "")
				encounterHql += " AND LOWER(patient.name.fam) like LOWER(:patientSurname)"; 
		}
		
		String nosologic = "";
		if (restrictionMap.get("nosologic")!=null){
			nosologic = restrictionMap.get("nosologic").get(0);
			if (nosologic != null && nosologic != "")
				encounterHql += " AND LOWER(encounter.visitNumber.extension) like LOWER(:nosologic)"; 
		} 
		
		String nurse = "";
		if (restrictionMap.get("nurse") != null){
			nurse = restrictionMap.get("nurse").get(0);
			if (nurse != null && nurse != "")
				encounterHql += " AND LOWER(encounter.referrer.name.fam) like LOWER(:nurse)"; 
		}
		
		String physician = "";
		if (restrictionMap.get("physician") != null){
			physician = restrictionMap.get("physician").get(0);
			if (physician != null && physician != "")
				encounterHql += " AND LOWER(encounter.admitter.name.fam) like LOWER(:physician)"; 
		}
		
		String room = "";
		if (restrictionMap.get("room") != null){
			room = restrictionMap.get("room").get(0);
			if (room != null && room != "")
				encounterHql += " AND LOWER(encounter.roomString) like LOWER(:room)"; 
		}
		
		String bed = "";
		if (restrictionMap.get("bed") != null){
			bed = restrictionMap.get("bed").get(0);
			if (bed != null && bed != "")
				encounterHql += " AND LOWER(encounter.bedString) like LOWER(:bed)"; 
		}


		Date acceptedFrom = null;
		Date acceptedTo = null;
		if (restrictionMap.get("encounterYear") != null){
			String encounterYear = restrictionMap.get("encounterYear").get(0);
			if (encounterYear != null && encounterYear != ""){
				acceptedFrom = createDate(Integer.parseInt(encounterYear), 1, 1, 0, 0, 0);
				acceptedTo = createDate(Integer.parseInt(encounterYear), 12, 31, 23, 59, 59);
				
				encounterHql += " AND encounter.availabilityTime >= :acceptedFrom AND encounter.availabilityTime <= :acceptedTo";	
			}
		}
		
		if (restrictionMap.get("sorting") != null) {
			if (restrictionMap.get("sorting").get(0).equals("SurnameNameASC"))
				encounterHql += ORDER_BY_SURNAME_NAME_ASC;
			if (restrictionMap.get("sorting").get(0).equals("SurnameNameDESC"))
				encounterHql += ORDER_BY_SURNAME_NAME_DESC;
			else if (restrictionMap.get("sorting").get(0).equals("AdmissionDateASC"))
				encounterHql += ORDER_BY_ADMISSION_DATE_ASC;
			else if (restrictionMap.get("sorting").get(0).equals("AdmissionDateDESC"))
				encounterHql += ORDER_BY_ADMISSION_DATE_DESC;
			else if (restrictionMap.get("sorting").get(0).equals("WardRoomBedASC"))
				encounterHql += ORDER_BY_WARD_ROOM_BED_ASC;
			else if (restrictionMap.get("sorting").get(0).equals("WardRoomBedDESC"))
				encounterHql += ORDER_BY_WARD_ROOM_BED_DESC;					 
		}
				
		Query qryEncounter = ca.createHibernateQuery(encounterHql);
		
		if (encounterHql.contains(":statusCodes"))
			qryEncounter.setParameterList("statusCodes", statusCodes);
		
		if (encounterHql.contains(":encounterTypes"))
			qryEncounter.setParameterList("encounterTypes", caseTypes);
		
		if (encounterHql.contains(":wards"))
			qryEncounter.setParameterList("wards", wards);
		
		if (encounterHql.contains(":patientName"))
			qryEncounter.setParameter("patientName", patientName + "%");
		
		if (encounterHql.contains(":patientSurname"))
			qryEncounter.setParameter("patientSurname", patientSurname + "%");
		
		if (encounterHql.contains(":nosologic"))
			qryEncounter.setParameter("nosologic", "%" + nosologic + "%");
		
		if (encounterHql.contains(":nurse"))
			qryEncounter.setParameter("nurse", nurse + "%");
		
		if (encounterHql.contains(":physician"))
			qryEncounter.setParameter("physician", physician + "%");
		
		if (encounterHql.contains(":room"))
			qryEncounter.setParameter("room", room);
		
		if (encounterHql.contains(":bed"))
			qryEncounter.setParameter("bed", bed);
		
		if (encounterHql.contains(":acceptedFrom"))
			qryEncounter.setParameter("acceptedFrom", acceptedFrom);
		
		if (encounterHql.contains(":acceptedTo"))
			qryEncounter.setParameter("acceptedTo", acceptedTo);
		
		if (encounterHql.contains(":maxBrassValue")){
			Integer maxBrassValue = 10;
			qryEncounter.setParameter("maxBrassValue", maxBrassValue, Hibernate.INTEGER);
		}
		
		qryEncounter.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
		qryEncounter.setFirstResult((page - 1) * readPageSize);
		qryEncounter.setMaxResults(readPageSize + 1);
		Long startMs = new Date().getTime();
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resultsExecutions = qryEncounter.list();
		
		log.debug("[cid="+Conversation.instance().getId()+"] Executed dashboard ADT query in "+ (new Date().getTime() - startMs) +" ms." );
		ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(resultsExecutions, getUrl4Pagination(), readPageSize, page);
		
		return dm;
	}
		
	private Date createDate(int year, int month, int day, int hour, int minute, int second){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }
	
	
	
}
