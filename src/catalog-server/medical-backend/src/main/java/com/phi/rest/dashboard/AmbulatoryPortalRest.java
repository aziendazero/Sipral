package com.phi.rest.dashboard;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.hibernate.SQLQuery;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.exception.PersistenceException;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AmbulatoryPortalRest")
@Path("/ambulatoryportal")
public class AmbulatoryPortalRest extends BaseDashboardRest {

	private static final long serialVersionUID = -3319755470201361416L;

	private static final int resultPerPage = 20;
	
	/**
	 * Query for ambulatory appointments used by Ambulatory Portal dashboard
	 */
	private static final String sqlAmbulatory = "select "+
			"	app.text_string as text_string, " +
			" 	app.internal_id as internalId, "+
			"	app.defaultDate as defaultDate, "+
			"	appStatusCode.code as statusCode_Code, "+
			"	pat.internal_id as patient_InternalId, "+
			"	pat.birth_time as patient_BirthTime, "+
			"	pat.name_fam as patient_name_fam, "+
			"	pat.name_giv as patient_name_giv, "+
			"	pat.fiscal_code as patient_FiscalCode, "+
			"	LISTAGG (multiProcedureCode.display_name, ', ') Within Group (ORDER BY multiProcedureCode.display_name) As procedurerequest_procedure, "+
			"	procCode.lang_it as proc_code_translation, "+
			"   procClassCode.code as proc_classcode, "+
			"	app.serviceDeliveryLocation as sdl_internalId, "+
			"	agenda.name_giv	as sdl_name_giv, "+
			"	agenda.Parent_SDL as sdl_parent_internalId, "+
			"	ambulatory.name_giv as sdl_parent_name_giv, "+
			"	appGrouper.internal_id as appointmentGrouper_internalId, "+
			"	appGrouperSC.code as appointmentGrouper_statusCode, "+
			"	enc.internal_id as encounter_internalId "+
			"from  "+
			"	appointment app "+
			"		inner join code_value appStatusCode on app.status_code=appStatusCode.id  "+
			"		left outer join procedure_db proc on app.procedure_id=proc.internal_id  "+
			"		left outer join code_value procCode on proc.code=procCode.id  "+
			"		left outer join code_value procClassCode on proc.class_code=procClassCode.id  "+
			"		inner join patient pat on app.patient_id=pat.internal_id  "+
			"		left outer join appointment_grouper appGrouper on app.appointmentGrouper_id=appGrouper.internal_id  "+
			"		left outer join code_value appGrouperSC on appGrouper.status_code=appGrouperSC.id  "+
			"		left outer join patient_encounter enc on app.patient_encounter=enc.internal_id  "+
			"		left outer join procedure_request request on app.internal_id=request.appointment_id  "+
			"		left outer join procedure_db p on request.internal_id=p.procreq_id  " +
			"		left outer join code_value procStatusCode on p.status_code=procStatusCode.id " +
			"		left outer join code_value_icd9 multiProcedureCode on ((p.code_icd9=multiProcedureCode.id) and (procStatusCode.code in ('new', 'completed'))), "+
			"	service_delivery_location agenda, "+
			"	service_delivery_location ambulatory  "+
			"where  "+
			"	app.serviceDeliveryLocation=agenda.internal_id  "+
			"	and agenda.Parent_SDL=ambulatory.internal_id  "+
			"	and app.insert_completed=1  "+
			"	and app.defaultDate>=:startDate  "+
			"	and app.defaultDate<:endDate  "+
			"	and app.serviceDeliveryLocation in (:sdlIds)  "+
			"	and appStatusCode.id in (:statusCodes)  " +
			"group by  "+
			"	app.text_string, " +
			"   app.internal_id, "+
			"	app.defaultDate, "+ 
			"	appStatusCode.code, "+
			"	pat.internal_id, "+
			"	pat.birth_time, "+
			"	pat.name_fam, "+
			"	pat.name_giv, "+
			"	pat.fiscal_code, "+
			"	procCode.lang_it, "+
			"	procClassCode.code, "+
			"	app.serviceDeliveryLocation, "+
			"	agenda.name_giv, "+
			"	agenda.Parent_SDL, "+
			"	ambulatory.name_giv, "+
			"	appGrouper.internal_id," +
			"	appGrouperSC.code, "+
			"	enc.internal_id  "+
			"order by app.defaultDate";
	
	@Override
	public Response init(PathSegment restrictions, int page) {
		try {
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
	
			String dateStr = restrictionMap.get("dateStr").get(0);
			List<String> sdlIds = restrictionMap.get("sdlIds");
			List<String> statusCodes = restrictionMap.get("statusCodes");

			List<Map<String, Object>> results = getAmbulatoryAppointments(dateStr, sdlIds, statusCodes, page);
	
			String readUrl = BASE_REST_URL + "ambulatoryportal/init/" + restrictions + "/"; 
	
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); 
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			res.setMain(dm);
	/*
			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> agendas = readAgendas();
			
			additional.put("Agendas", agendas);
			
			res.setAdditional(additional);
	*/		
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] AmbulatoryPortal Init");
			log.debug("[cid="+Conversation.instance().getId()+"] AmbulatoryPortal Init details - restrictions: " + restrictions + " (page " + page + ")");
			
			return Response.ok(json).build();
		
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in AmbulatoryPortal Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing appointment data").build();
		}
	}

	@Override
	public Response refresh(PathSegment restrictions, int page) {
		try {
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
	
			String dateStr = restrictionMap.get("dateStr").get(0);
			List<String> sdlIds = restrictionMap.get("sdlIds");
			List<String> statusCodes = restrictionMap.get("statusCodes");

			List<Map<String, Object>> results = getAmbulatoryAppointments(dateStr, sdlIds, statusCodes, page);
	
			String readUrl = BASE_REST_URL + "ambulatoryportal/refresh/" + restrictions + "/"; 
	
			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); 
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			res.setMain(dm);
		
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] AmbulatoryPortal Refresh");
			log.debug("[cid="+Conversation.instance().getId()+"] AmbulatoryPortal Refresh details - restrictions: " + restrictions + " (page " + page + ")");
			
			return Response.ok(json).build();
		
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in AmbulatoryPortal Refresh with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing appointment data").build();
		}
	}

	/**
	 * Used by Ambulatory Portal dashboard to show Appointment
	 * 
	 * @param dateStr filters Appointment.defaultDate
	 * @param sdlIds filters Appointment.serviceDeliveryLocation.internalId
	 * @param statusCodes filters Appointment.statusCode.code
	 * @param page number of page, page size defined in: resultPerPage
	 * @return
	 * @throws PersistenceException
	 */
	private List<Map<String, Object>> getAmbulatoryAppointments(String dateStr, List<String> sdlIds, List<String> statusCodes, int page) throws Exception {

		String currentLang = Locale.instance().getLanguage();

		String sqlAmbulatory = this.sqlAmbulatory;

		if (!currentLang.equals("it")) {
			sqlAmbulatory = sqlAmbulatory.replace("lang_it", "lang_" + currentLang);
		}
		
		// Replace LISTAGG
		sqlAmbulatory = ReplaceListaggBean.replaceListagg(sqlAmbulatory);

		SQLQuery qry = ca.createHibernateNativeQuery(sqlAmbulatory);

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

		qry.setParameterList("sdlIds", sdlIds);
		qry.setParameterList("statusCodes", statusCodes);

		qry.setMaxResults(resultPerPage);
		qry.setFirstResult(page*resultPerPage);

		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
		List<Map<String, Object>> results = qry.list();

		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		
		return results;
	}
	
	@Override
	public Response printReport(PathSegment restrictions) {
		// TODO Auto-generated method stub
		return null;
	}

}
