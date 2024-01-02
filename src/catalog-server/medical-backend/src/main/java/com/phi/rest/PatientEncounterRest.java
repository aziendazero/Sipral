package com.phi.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.act.PatientEncounter;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InjectDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;

/**
 * Returns all PatientEncounters where assignedSDL or temporarySDL IN UserBean.instance().getSdLocs()
 * Same logic of ADTRest
 * Used by PHI_CI.war\common\js\mobile\psion-mobile.js
 * 
 * @author Alex Zupan
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("PatientEncounterRest")
@Path("/patientencounters")
public class PatientEncounterRest extends BaseRest<PatientEncounter> {

	private static final long serialVersionUID = 332862988817186188L;
	
	private static final String allEncountersHql = 
			" SELECT" +
			" pe.internalId as internalId," +
			" pe.barcode as barcode," +
			" patient.internalId AS patient_internalId," +
			" patient.name.fam AS patient_name_fam," +
			" patient.name.giv AS patient_name_giv" +
			" FROM PatientEncounter pe" +
			" JOIN pe.statusCode statusCode" +
			" JOIN pe.patient patient" +
			" WHERE statusCode.code IN ('new', 'active','held')";			
	private static final String nameGivHql = " AND lower(patient.name.giv) LIKE :nameGiv";
	private static final String nameFamHql = " AND lower(patient.name.fam) LIKE :nameFam";
	private static final String barcodeHql = " AND barcode LIKE :barcode";
	//Here
	private static final String temporarySDLHql = "AND (pe.temporarySDL.internalId IN (:sdlocs) OR (pe.assignedSDL.internalId IN (:sdlocs) AND pe.temporarySDL.internalId IS NULL))";
	//Assigned here
	private static final String assignedSDLHql = " AND pe.assignedSDL.internalId IN (:sdlocs)";
	//All
	private static final String assignedAndTemporarySDLHql = " AND (pe.assignedSDL.internalId IN (:sdlocs) OR pe.temporarySDL.internalId IN (:sdlocs))";
	private static final String orderHql = " ORDER BY patient.name.fam DESC";
	
	private static final String proceduresQuery = 
			" SELECT" +
			" status.code as statuscode," +
			" nvl(codeproc.code,' ') as procedurecode," +
			" proc.text_string as description" +
			" FROM procedure_db proc," +
			" code_value status," +
			" code_value codeproc" +
			" WHERE proc.status_code = status.id" +
			" AND codeproc.id (+) = proc.code" +
			" AND proc.is_active = 1 " +
			" AND proc.pat_enc_id = :encounterId ";
	
	private static final String historyQuery = 
			" SELECT" +
			" encounter.internal_id as encounter_internalid," +
			" nvl(app.defaultDate, encounter.availability_time) as eventdate," +
			" encsdl.name_giv as encounter_sdl," +
			" enccode.code as encounter_code," +
			" encounter.patient_id as encounter_patient," +
			" nvl(appsdl.name_giv, encsdl.name_giv) as appointment_sdl" +
			" FROM patient_encounter encounter," +
			" appointment app," +
			" service_delivery_location encsdl," +
			" service_delivery_location appsdl," +
			" code_value enccode" +
			" WHERE encounter.internal_id = app.patient_encounter (+)" +
			" AND encounter.assignedsdl = encsdl.internal_id" +
			" AND app.servicedeliverylocation = appsdl.internal_id (+)" +
			" AND enccode.id = encounter.code" +
			" AND encounter.patient_id = (SELECT e1.patient_id FROM patient_encounter e1 WHERE e1.internal_id = :encounterId)" +
			" ORDER BY nvl(app.defaultDate, encounter.availability_time) desc";
	
	private static final String lastClinicalDiaryHql =
			" SELECT" +
			" MAX(note.availabilityTime) as lastDate" +
			" FROM ClinicalDiary note" +
			" WHERE note.isActive = true" +
			" AND note.patientEncounter.internalId = :encounterId";
	
	
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@QueryParam("patient.name.giv") String nameGiv, @QueryParam("patient.name.fam") String nameFam, @QueryParam("barcode") String barcode, @DefaultValue("1") @QueryParam("page") int page) {

		try {

			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}

			String hql = allEncountersHql;
			
			if (nameGiv != null && nameGiv != "") {
				hql += nameGivHql;
			} 
			
			if (nameFam != null && nameFam != "") {
				hql += nameFamHql;
			} 
			
			if (barcode != null && barcode != "") {
				hql += barcodeHql;
			} 
			
			UserBean ub = UserBean.instance();
			
			if (ub.getRole().equals("1")) { //AMMINISTRATORE 1
				hql += assignedAndTemporarySDLHql;
			} else if (ub.getRole().equals("11")) { //MEDICO 11 
				hql += assignedSDLHql;
			} else {
				hql += temporarySDLHql;
			}
			
			hql += orderHql;
			
			Query qry = ca.createHibernateQuery(hql);
			
			if (nameGiv != null && nameGiv != "") {
				qry.setParameter("nameGiv", nameGiv.toLowerCase() + "%");
			} 
			
			if (nameFam != null && nameFam != "") {
				qry.setParameter("nameFam", nameFam.toLowerCase() + "%");
			} 
			
			if (barcode != null && barcode != "") {
				qry.setParameter("barcode", barcode + "%");
			} 
 
			qry.setParameterList("sdlocs", ub.getSdLocs());
				
			qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
						
            qry.setFirstResult((page - 1) * readPageSize);
            qry.setMaxResults(readPageSize + 1);
			
			List<Map<String, Object>> results = qry.list();
			
			String readUrl = BASE_REST_URL + "patientencounters/all/?";
			
			if (nameGiv != null && nameGiv != "") {
				readUrl += "patient.name.giv=" + nameGiv + "&";
			} 
			
			if (nameFam != null && nameFam != "") {
				readUrl += "patient.name.fam=" + nameFam + "&";
			} 
			
			if (barcode != null && barcode != "") {
				readUrl += "barcode=" + barcode + "&";
			} 
			
			readUrl += "page=";
			
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, readPageSize, page); 
			
			String json = mapper.writeValueAsString(dm);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();
						
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error getAll by patientName: " + nameGiv + " patientSurname: " + nameFam, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getAll by patientName: " + nameGiv + " patientSurname: " + nameFam).build();
		}	
	}
	
	
	/**
	 * Get entity by id, put in conversation
     * If id == 0 eject from conversation
     * 
	 * @param id
	 * @return json representation
	 * @throws Exception 
	 */	
	@Override
	@GET
	@Path("/{restrictions}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inject(@PathParam("restrictions") PathSegment restrictions) {
		long id = 0L;
		try {
			InjectDatamodel<PatientEncounter> res = new InjectDatamodel<PatientEncounter>();
			// Inject and set entity (if id <= 0 entity is ejected and passed null)
			id = Long.parseLong(restrictions.getPath());
			inject(id);
			
			// Deproxy required children
			MultivaluedMap<String, String> restrictionMap = restrictions.getMatrixParameters();
			List<String> loadList = restrictionMap.get("load");
			
			if (loadList != null) {
				ca.refreshIfNeeded(entity);
				for (String load : loadList) {
					loadProxy(entity, load);				
				}
			}			
			res.setEntity(entity);
			
			List<String> additionalList = restrictionMap.get("additional");
			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			
			// If filter for privacy, send RenderPrivacy, DatePrivacyFrom and DatePrivacyTo to dashboard 
			if ("true".equals(RepositoryManager.instance().getSeamProperty("filterForPrivacy"))) {
				List<Map<String, Object>> privacyList = new ArrayList<Map<String,Object>>();
				Map<String, Object> privacy = new HashMap<String, Object>();		
				
				privacy.put("renderPrivacy", Contexts.getSessionContext().get("RenderPrivacy"));
				privacy.put("datePrivacyFrom", Contexts.getSessionContext().get("DatePrivacyFrom"));
				privacy.put("datePrivacyTo", Contexts.getSessionContext().get("DatePrivacyTo"));
				
				privacyList.add(privacy);					
				additional.put("privacy", privacyList);
			}
			
			//TODO: Check if useful value = URLDecoder.decode(value, "utf-8");
			if (additionalList != null && !additionalList.isEmpty()){
												
				if (additionalList.contains("procedures")) {
					// Procedure list
					List<Map<String, Object>> procedureList = readProcedures(id);				
					additional.put("procedures", procedureList);
				}

				if (additionalList.contains("history")) {
					// Patient history
					List<Map<String, Object>> historyList = readHistory(id);
					additional.put("history", historyList);
				}	
			}
			
			res.setAdditional(additional);
			String json = mapper.writeValueAsString(res);
			
			Events.instance().raiseEvent(PhiEvent.INJECT, entity);
				
			return Response.ok(json).build();
			
		} catch (JsonProcessingException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		} catch (PhiException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();		
		}		
	}
		
	@POST
	@Path("/updateLastClinicalDiary/{encounterId : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLastClinicalDiary(@PathParam("encounterId") long encounterId) throws PersistenceException {
		
		PatientEncounter encounter = ca.get(PatientEncounter.class, encounterId);

		// Read active clinical diary notes
		String hql = lastClinicalDiaryHql;
		Query qry = ca.createHibernateQuery(hql);
		qry.setParameter("encounterId", encounterId);
		Date lastDate = (Date) qry.uniqueResult();
		
		// Set lastClinicalDiary
		encounter.setLastClinicalDiary(lastDate);
		
		ca.update(encounter);		
		ca.flushSession();
		
		return Response.ok().build();
	}	
		
	private List<Map<String, Object>> readHistory(long encounterId) {
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		Query qry = ca.createHibernateNativeQuery(historyQuery);
		
		qry.setParameter("encounterId", encounterId);
		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
		results = qry.list();
		
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		
		return results;
	}

	private List<Map<String, Object>> readProcedures(long encounterId) {

		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
			
		Query qry = ca.createHibernateNativeQuery(proceduresQuery);
		
		qry.setParameter("encounterId", encounterId);
		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
		results = qry.list();
		
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		
		return results;
	}
}