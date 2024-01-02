package com.phi.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.view.banner.Banner;
import com.phi.entities.role.Patient;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("PatientRest")
@Path("/patients")
public class PatientRest extends BaseRest<Patient> implements Serializable {
	
	private static final long serialVersionUID = 8556346700912237063L;
	
	private static final Logger log = Logger.getLogger(PatientRest.class);
	
	private static final String ENCOUNTER_HISTORY_SQL = 
			" SELECT" +
			" encounter.internal_id as internalId," +
			" patient.internal_id as patientinternalId," +
			" code.code as code," +
			" scheduled.internal_id as scheduledInternalId," +
			" scheduled.name_giv as scheduledName," +
			" assigned.internal_id as assignedInternalId," +
			" assigned.name_giv as assignedName," +	
			" encounter.availability_time as availabilityTime," +
			" encounter.dismission_date as dismissionDate," +
			" encounter.text_string as note" +
			" FROM patient_encounter encounter" +
			" LEFT JOIN patient patient on patient.internal_id = encounter.patient_id" +
			" LEFT JOIN code_value code on code.id = encounter.code" +
			" LEFT JOIN code_value statusCode on statusCode.id = encounter.status_code" +
			" LEFT JOIN service_delivery_location assigned on assigned.internal_id = encounter.assignedSDL" +
			" LEFT JOIN service_delivery_location scheduled on scheduled.internal_id = encounter.scheduledSDL" +
			" WHERE patient.internal_id = :patientInternalId" +
			" AND statusCode.code <> 'nullified'";

	private static final String sqlPatientWithPathway = "select "+  // seleziona pazienti con pathway presenti
			" p.name_giv, p.name_fam, p.internal_id," +
			" p.gender_code , p.birth_time as birthTime," +
			" max(ps.status) as statusPS, max(pt.status)  as statusPT" + 
			" from patient p, PATHWAY_SEQUENCE ps , PATHWAY_STEP pt " + 
			" where p.internal_id = ps.patient_id" + 
			" and ps.internal_id = pt.pathway_sequence_id" +
			" and ps.author = :id" +
			"  GROUP BY p.internal_id, p.name_giv, p.name_fam, p.internal_id, p.gender_code , p.birth_time " +
			" order by p.NAME_FAM, p.NAME_GIV";
	
	private static final String sqlPatientWithPathwayAlert = "select "+  // seleziona pazienti con pathway presenti
			" p.name_giv, p.name_fam, p.internal_id," +
			" p.gender_code , p.birth_time as birthTime," +
			" max(ps.status) as statusPS, max(pt.status)  as statusPT" + 
			" from patient p, PATHWAY_SEQUENCE ps , PATHWAY_STEP pt " + 
			" where p.internal_id = ps.patient_id" + 
			" and ps.internal_id = pt.pathway_sequence_id" +
			" and ps.author = :id" +
			" and (ps.status = 2 or pt.status = 2)" +
			"  GROUP BY p.internal_id, p.name_giv, p.name_fam, p.internal_id, p.gender_code , p.birth_time " +
			" order by p.NAME_FAM, p.NAME_GIV";
	
	private static final String ALLERGY_COUNT_SQL = "select " +
			" count (allergy.internal_id) as allergy" +
			" from patient" +
			" left join allergy on allergy.patient_id = patient.internal_id" +
			" where patient.internal_id = :patientInternalId" +
			" and allergy.confirmed = 1 and allergy.is_active = 1";

	@Override
	@GET
	@Path("/{restrictions}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inject(@PathParam("restrictions") PathSegment restrictions) {
		try{
			Response resp = super.inject(restrictions);

			Banner banner = Banner.instance();
			banner.addEntity(conversationName, entity);
			
			return resp;
		} catch (Exception e) {
			log.error("Error setting BannerBean from inject with restrictions: "+restrictions, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error setting BannerBean from inject with restrictions: "+restrictions).build();
		}
	}
		
	/**
	 * Used by dashboard ADT to show Historic Encounters for selected Patient.
	 * 
	 * @param patientInternalId filters Encounters for selected Patient
	 * @param customer different behavior for different customers
	 * @param page number of page, page size defined in: resultPerPage
	 * @return
	 * @throws PersistenceException
	 */	
	@GET
	@Path("/encounterHistory/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEncounterHistory(@QueryParam("patientInternalId") Long patientInternalId, @QueryParam("showHistoricEncounters") String showHistoricEncounters, @DefaultValue("0") @PathParam("page") int page) throws PersistenceException {
		try {
			String encounterHistorySql = ENCOUNTER_HISTORY_SQL;
			
			if (showHistoricEncounters.equals("OnlyPassedInSelWards")){
				
				UserBean ub = UserBean.instance();
				String employeeRoleCode = ub.getRole();
				long employeeInternalId = ub.getCurrentEmployee().getInternalId();
								
				encounterHistorySql += 
						" AND" +
						"  (EXISTS" +
						"    (SELECT 1" +
						"     FROM transfer transfer" +
						"     WHERE transfer.patient_encounter = encounter.internal_id" +
						"     AND sdlocto IN" +
						"      (SELECT s.sdloc_id" +
						"       FROM employeerole_servicedelivery s, employee_role r, employee e, code_value_role cv" +
						"       WHERE e.internal_id = " + employeeInternalId +
						"       AND r.code = cv.id" +
						"       AND r.employee_id = e.internal_id" +
						"       AND s.emprole_id = r.internal_id" +
						"       AND cv.id LIKE '2.16.840.1.113883.3.20.11.1.%'" +
						"       AND cv.code = " + employeeRoleCode +
						"      )" +
						"    )" +
						"   OR EXISTS" +
						"    (SELECT s.sdloc_id" +
						"     FROM employeerole_servicedelivery s, employee_role r, employee e , code_value_role cv" +
						"     WHERE e.internal_id = " + employeeInternalId + 
						"     AND r.code = cv.id" +
						"     AND r.employee_id = e.internal_id" +
						"     AND s.emprole_id = r.internal_id" +
						"     AND cv.id LIKE '2.16.840.1.113883.3.20.11.1.%'" +
						"     AND cv.code = " + employeeRoleCode +
						"     AND (s.sdloc_id = assigned.internal_id OR s.sdloc_id = scheduled.internal_id)" +
						"    )" +
						"  )";
			}
			
			encounterHistorySql += " ORDER BY encounter.availability_time DESC, encounter.creation_date DESC";
			
			Query qry = ca.createHibernateNativeQuery(encounterHistorySql);

			qry.setParameter("patientInternalId", patientInternalId);
			
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			List<Object> results = qry.list();
						
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("Error getting patient history", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting patient history").build();
		}
	}

	@GET
	@Path("/bypathway/{id}/{tipo}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "rawtypes" })
	public Response sqlPatientWithPathway(@PathParam("id") Integer id, @PathParam("tipo") Integer tipo, @DefaultValue("1") @PathParam("page") int page) throws PersistenceException {
		try {
			
			//String currentLang = Locale.instance().getLanguage();
			
			String sqlpatWidthPatw = sqlPatientWithPathway;
			if (tipo == 1)
				sqlpatWidthPatw = sqlPatientWithPathwayAlert;
			
			/*			
			if (!currentLang.equals("it")) {
				sqlAmbulatory = sqlAmbulatory.replace("lang_it", "lang_" + currentLang);
			} 
			*/
			
			SQLQuery qry = ca.createHibernateNativeQuery(sqlpatWidthPatw);


			qry.setParameter("id", id);
			
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			
			int firstResult = (page - 1) * readPageSize;
            int maxResults = readPageSize + 1;
            
            qry.setFirstResult(firstResult);
            qry.setMaxResults(maxResults);                      
            
            String readUrl = BASE_REST_URL + "patients/bypathway/" + id + "/"; //FIXMEEEEEEEEEEEEEEEEEEEEE
			
			List<?> results = qry.list();

			ListDatamodel<Patient> dm = new ListDatamodel(results, readUrl, readPageSize, page);
			
			String json = mapper.writeValueAsString(dm);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
	
		} catch (Exception e) {
			log.error("Error getting patient width pathway, with code id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting patient width pathwa, with code id: " + id).build();
		}
	}	

	@GET
	@Path("/allergies")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllergies(@QueryParam("patientInternalId") Long patientInternalId) throws PersistenceException {
		try {
			SQLQuery qryAllergy = ca.createHibernateNativeQuery(ALLERGY_COUNT_SQL);
			qryAllergy.setParameter("patientInternalId", patientInternalId);
			qryAllergy.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
			List<Map<String, Object>> results = qryAllergy.list();
			String json = mapper.writeValueAsString(results);	
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		return Response.ok(json).build();
		} catch (JsonProcessingException e) {
			log.error("Error getting allergies width patient id: " + patientInternalId, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting allergies width patient id: " + patientInternalId).build();
		}
	}
}