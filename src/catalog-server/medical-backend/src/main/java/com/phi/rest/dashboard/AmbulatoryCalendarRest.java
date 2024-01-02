package com.phi.rest.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.hibernate.SQLQuery;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Locale;

import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.entities.actions.AppointmentAction;
import com.phi.entities.actions.PatientAction;
import com.phi.entities.actions.ServiceDeliveryLocationAction;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InitRefreshDatamodel;
import com.phi.rest.datamodel.ListDatamodel;
import com.phi.security.UserBean;
import com.phi.utils.ReplaceListaggBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AmbulatoryCalendarRest")
@Path("/ambulatorycalendar")
public class AmbulatoryCalendarRest extends BaseDashboardRest {

	private static final long serialVersionUID = 207233526900297558L;

	private static String query_sdl = 	"SELECT " +
										" sdl.parent.name.giv," +
										" sdl.name.giv" +
										" FROM " +
										" ServiceDeliveryLocation sdl" +
										" WHERE " +
										" sdl.internalId = :selectedAgendaId";
	
	private static String query_appointment = 	"SELECT " +
												" appointment.defaultDate, " +
												" procedureLevelCode.score," +
												" sc.langIt, " + 
												" patient.name.fam, " +
												" patient.name.giv, " +
												" patient.birthTime, " +
												" appointmentGrouperLocation.code," +
												" appointmentGrouperAgeType.code," +
												" procedureCode.langIt, " +
												" appointment.text.string," +
												" appointmentGrouper.phoneNumber," +
												" patient.telecom.hp," +
												" patient.telecom.mc," +
												" patient.telecom.ec" +
												" FROM " +
												" Appointment appointment " +
												" LEFT JOIN appointment.patient patient" +
												" JOIN appointment.statusCode sc" +
												" JOIN appointment.procedure procedure" +
												" JOIN procedure.classCode procedureCode" +
												" JOIN procedure.levelCode procedureLevelCode" +
												" LEFT JOIN appointment.appointmentGrouper appointmentGrouper " +
												" LEFT JOIN appointmentGrouper.location appointmentGrouperLocation " +
												" LEFT JOIN appointmentGrouper.ageType appointmentGrouperAgeType" +
												" WHERE  " +
												" appointment.insertCompleted = true" +
												" AND appointment.statusCode.code IS NOT 'cancelled'" +
												" AND appointment.defaultDate >= :startDate " +
												" AND appointment.defaultDate < :endDate " +
												" AND appointment.serviceDeliveryLocation.internalId = :selectedAgendaId" +
												" ORDER BY appointment.defaultDate";
	
	private static String query_agende = "SELECT" +
									" sdl.internalId as internalId," +
									" sdl.name.giv as name," +
									" sdl.parent.internalId as parentId," +
									" sdl.parent.name.giv as parentName," +
									" sdl.parent.code.code as parentCode" + 
									" FROM" +
									" ServiceDeliveryLocation sdl " +
									" WHERE sdl.code.code = 'UD' ";
	
	private static String query_worklist = "SELECT" +
			" sdl.name.giv," +
			" patient.name.fam," +
			" patient.name.giv," +
			" patient.birthTime," +
			" appointment.defaultDate," +
			" appointment.text.string," +
			" appointment.statusCode.code," +
			" proc.text" +
			" FROM" +
			" Appointment appointment" +
			" JOIN appointment.serviceDeliveryLocation sdl" +
			" LEFT JOIN appointment.patient patient" +
			" LEFT JOIN appointment.performedProcedure proc " +
			" WITH proc.isActive=true " +
			" WHERE" +
			" appointment.isActive=true" +
			" AND appointment.statusCode.code IS NOT 'cancelled' " +
			" AND appointment.statusCode.code IS NOT 'nullified' " +
			" AND appointment.defaultDate>=:startDate" +
			" AND appointment.defaultDate<=:endDate" +
			" ORDER BY appointment.serviceDeliveryLocation.internalId, appointment.defaultDate";
	
	private static String sql_init_appoinment = "select appointment.internal_id as internalId," +
			" appointment.servicedeliverylocation as location," +
			" patient.name_fam as patient_surname," +
			" patient.name_giv as patient_name," +
			" patient.internal_id as patient_internalId," +
			" appointment.defaultDate as time," +
			" appointment.external_id as externalId," +
			" statusCode.code as status," +
			" statusCode.id as statusId," +
			" patient.birth_time as patient_birthtime," +
			" appointment.text_string as note," +
			" appointment.color as color," +
			" appointment.duration as duration," +
			" visitType.code as visitType," +
			" visitType.id as visitTypeId," +
			" appointment.patient_encounter as encounterId," +
			" (select listagg (text_string,',') within group (order by creation_date) " +
			" from procedure_db where appointment_id = appointment.internal_id) as procedures" +
			" from appointment, patient, code_value statusCode, code_value visitType " +
			" where appointment.PATIENT_ID = patient.internal_id (+) " +
			" and statuscode.id = appointment.status_code" +
			" and visitType.id (+) = appointment.visittype" +
			" and appointment.is_active = 1" +
			" AND appointment.defaultDate>=:startDate" + 
			" AND appointment.defaultDate<=:endDate";
	
	@Override
	@GET
	@Path("init/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response init(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {

			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			List<Map<String, Object>> results = read(restrictionMap, page, false);

			String readUrl = BASE_REST_URL + "ambulatorycalendar/init/" + restrictions + "/"; 

			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); 
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			res.setMain(dm);

			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			List<Map<String, Object>> agendas = readAgendas();
			
			additional.put("Agendas", agendas);
			
			res.setAdditional(additional);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] AmbulatoryCalendar Init");
			log.debug("[cid="+Conversation.instance().getId()+"] AmbulatoryCalendar Init details - restrictions: " + restrictions + " (page " + page + ")");
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in AmbulatoryCalendar Init with restrictions: " + restrictions + " (page " + page + ")", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error initializing appointment data").build();
		}
	}

	

	@Override
	@GET
	@Path("refresh/{restrictions}/{page}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page) {
		try {

			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);

			List<Map<String, Object>> results = read(restrictionMap, page, false);

			String readUrl = BASE_REST_URL + "ambulatorycalendar/refresh/" + restrictions + "/"; 

			ListDatamodel<Map<String, Object>> dm = new ListDatamodel<Map<String, Object>>(results, readUrl, null, page); 
			InitRefreshDatamodel<Map<String, Object>> res = new InitRefreshDatamodel<Map<String, Object>>();
			res.setMain(dm);
			
			String json = mapper.writeValueAsString(res);
			
			log.info("[cid="+Conversation.instance().getId()+"] AmbulatoryCalendar Refresh");
			log.debug("[cid="+Conversation.instance().getId()+"] AmbulatoryCalendar Refresh details - restrictions: " + restrictions + " (page " + page + ")");
			
			Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
			
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error in AmbulatoryCalendar Refresh with restrictions: " + restrictions + " (page " + page + ")", e);		
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error refreshing appointment data").build();
		}
	}
	
	@GET
	@Path("startNewAppointment/{restrictions}")
	public Response startNewAppointment(@PathParam("restrictions") PathSegment restrictions) {
		
		try {
			PatientAction pa = PatientAction.instance();
			pa.eject();
			
			MultivaluedMap<String, String> restrictionMap = decodeResctrictions(restrictions);
			restrictionMap.get("defaultDate");
			restrictionMap.get("serviceDeliveryLocation.internalId");
			
			AppointmentAction aa = AppointmentAction.instance();
			Appointment app = aa.newEntity();
			aa.inject(app);
			app.setDefaultDate(decodeISO8601((String)restrictionMap.get("defaultDate").get(0)));
			
			ServiceDeliveryLocationAction sda = ServiceDeliveryLocationAction.instance();
			ServiceDeliveryLocation agenda =  sda.read(Long.parseLong(restrictionMap.get("serviceDeliveryLocation.internalId").get(0)));
			app.setServiceDeliveryLocation(agenda);
			
			aa.setCodeValue("statusCode", "PHIDIC", "AppointmentStatus", "awaiting");
			aa.setCodeValue("sourceCode", "PHIDIC", "SentBy", "OWN");
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error starting new appointment process. Restrictions: " + restrictions.toString(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error starting new appointment process. Restrictions: " + restrictions.toString()).build();
		}
		
		return Response.ok().build();
	}

	@Override
	@GET
	@Path("printReport/{restrictions}")
	public Response printReport(@PathParam("restrictions") PathSegment restrictions) {
		try {
			MultivaluedMap<String, String> restrictionMap;

			restrictionMap = decodeResctrictions(restrictions);
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			List<Object[]> result =  null;
			String currentLang = Locale.instance().getLanguage();

			if (restrictionMap.get("worklist") != null) {
				
				Date reportingDate = null;
				if (restrictionMap.get("reportingDate") != null) {
					reportingDate = decodeISO8601((String)restrictionMap.get("reportingDate").get(0));
				}
					
				pars.put("startDate", new Date(reportingDate.getTime()));
				pars.put("endDate", new Date(reportingDate.getTime()+24*60*60*1000));
				
				String query = query_worklist;
		
				if (!currentLang.equals("it")) {
					query = query.replace("langIt", "lang" + currentLang.substring(0, 1).toUpperCase() + currentLang.substring(1,2).toLowerCase());
				} 
				
				result = (List<Object[]>) ca.executeHQLwithParameters(query, pars);
				
				if (result.size()==0)
				{
					//set void appointment collection
					Contexts.getConversationContext().set("Appointments", null); 
					Contexts.getConversationContext().set("AppointmentsDate", null);
				}
				else
				{
					//map data result in a collection					
					Iterator<Object[]> objIterator = result.iterator();
					
					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					
					List<String[]> therapies = new ArrayList<String[]>();
					
					while (objIterator.hasNext())
					{
						Object[] appointment = objIterator.next();
						
						String[] stringArray = new String[2];
						String appStr = appointment[0]+"     ";
						
						Date appDate = (Date)appointment[4];
						String appDateString = dateTimeFormat.format(appDate);
						appStr+=appDateString;
						
						if (appointment[1] != null){  //un appuntamento generico non ha pazienti
							appStr+="      "+appointment[1].toString().toUpperCase()+" "+appointment[2];
						}
						
						if (appointment[5] != null){
							appStr+="\n       Note: " +appointment[5];
						}
						stringArray[0] = appStr;
						if (appointment[7] != null){
							stringArray[1] = appointment[7].toString();
						}

						therapies.add(stringArray);
						
					}
					
					Contexts.getConversationContext().set("Appointments", therapies); 
					Contexts.getConversationContext().set("AppointmentsDate", format.format(reportingDate));
				}

			} else {
			
				String query = query_sdl;

				Long reportingUdId = null;

				if (restrictionMap.get("reportingUdId") != null) {
					reportingUdId = Long.parseLong(restrictionMap.get("reportingUdId").get(0));
					pars.put("selectedAgendaId", reportingUdId);

					result = (List<Object[]>) ca.executeHQLwithParameters(query, pars);

					Contexts.getConversationContext().set("AmbulatoryName", (String) result.get(0)[0]); 
					Contexts.getConversationContext().set("AgendaName", (String) result.get(0)[1]); 
				}

				Date reportingDate = null;
				if (restrictionMap.get("reportingDate") != null) {
					reportingDate = decodeISO8601((String)restrictionMap.get("reportingDate").get(0));
				}

				//			pars = new HashMap<String, Object>();
				pars.put("startDate", new Date(reportingDate.getTime()));
				pars.put("endDate", new Date(reportingDate.getTime()+24*60*60*1000));

				query = query_appointment;

				if (!currentLang.equals("it")) {
					query = query.replace("langIt", "lang" + currentLang.substring(0, 1).toUpperCase() + currentLang.substring(1,2).toLowerCase());
				} 

				result = (List<Object[]>) ca.executeHQLwithParameters(query, pars);

				if (result.size()==0)
				{
					//set void appointment collection
					Contexts.getConversationContext().set("Appointments", null); 
				}
				else
				{
					//map data result in a collection

					Iterator<Object[]> objIterator = result.iterator();

					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
					SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yyyy)");

					while (objIterator.hasNext())
					{
						Object[] appointment = objIterator.next();
						//format appointment date

						Date startDate = (Date)(appointment[0]);
						appointment[0] = dateTimeFormat.format(startDate);

						//add duration
						appointment[0] = (String)appointment[0] + " - " + timeFormat.format(startDate.getTime() + (Integer)appointment[1] * 60000);

						//replace null with ''
						if (appointment[3]==null)
							appointment[3]="";

						if (appointment[4]==null)
							appointment[4]="";

						//format patient birthdate
						if (appointment[5]!=null)
						{
							appointment[5] = format.format(appointment[5]);
						}

						//adding "/" separator
						if (appointment[6]!=null && (appointment[7]!=null || appointment[8]!=null))
						{
							appointment[6]=appointment[6].toString()+'/';
						}

						if (appointment[7]!=null && appointment[8]!=null)
						{
							appointment[7]=appointment[7].toString()+'/';
						}

						if (appointment[10]==null) {
							appointment[10]="";
						} else {
							appointment[10] = appointment[10].toString() + " ";
						}

						if (appointment[11]==null) {
							appointment[11]="";
						} else {
							appointment[11] = appointment[11].toString() + " ";
						}

						if (appointment[12]==null) {
							appointment[12]="";
						} else {
							appointment[12] = appointment[12].toString() + " ";
						}

						if (appointment[13]==null) {
							appointment[13]="";
						} else {
							appointment[13] = appointment[13].toString() + " ";
						}

					}

					Contexts.getConversationContext().set("Appointments", result); 
				}

			}

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error reading appointments for report. Restrictions: " + restrictions.toString(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading appointments for report. Restrictions: " + restrictions.toString()).build();
		}
		
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
		
		return Response.ok().build();

	}
	
	public List<Map<String, Object>> read(Map<String, List<String>> restrictionMap, int page, boolean report) throws Exception {

		String sqlAppointment = sql_init_appoinment;	
		
		if (restrictionMap.get("statusCode") != null){
			sqlAppointment += " AND statusCode.code != '" + (String)restrictionMap.get("statusCode").get(0) + "' ";
		}
		
		if (restrictionMap.get("agenda") != null){
			sqlAppointment += " AND appointment.serviceDeliveryLocation IN ("+ (String)restrictionMap.get("agenda").get(0) +") ";
		} else {
			return null;
		}
		
		sqlAppointment += " ORDER BY appointment.internal_id ";
		
		// Replace LISTAGG
		sqlAppointment = ReplaceListaggBean.replaceListagg(sqlAppointment);
		
		SQLQuery qry = ca.createHibernateNativeQuery(sqlAppointment);

		Date start = null;
		if (restrictionMap.get("startDate") != null){
			start = decodeISO8601((String)restrictionMap.get("startDate").get(0));								
			qry.setParameter("startDate", start);						
		}
		Date end = null;
		if (restrictionMap.get("endDate") != null){
			end = decodeISO8601((String)restrictionMap.get("endDate").get(0));
			qry.setParameter("endDate",end);
		}
				
		qry.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
		List<Map<String, Object>> results = qry.list();
		
		return results;
		
	}
	
	private List<Map<String, Object>> readAgendas() {
		UserBean userBean = (UserBean)Component.getInstance("userBean");
		
		String listSdl ="";
		
		for (Long sdlId : userBean.getSdLocs()){
			listSdl += sdlId + ",";
		}
		if (listSdl.length() > 1){
			listSdl=listSdl.substring(0,listSdl.length()-1);
		}
		
		String sqlAgende = query_agende;
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		if (!listSdl.equals("")){
			sqlAgende += " AND sdl.internalId IN (" + listSdl + ") ";
			sqlAgende += " ORDER BY sdl.parent.internalId ";
			
			Query qryAgende = ca.createHibernateQuery(sqlAgende);
			qryAgende.setResultTransformer(new SqlAliasToEntityMapResultTransformer() );
			results = qryAgende.list();
		}	
		
		return results;
	}

}