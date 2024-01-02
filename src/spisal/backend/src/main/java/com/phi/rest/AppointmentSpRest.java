package com.phi.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.AgendaConfAction;
import com.phi.entities.actions.AppointmentSpAction;
import com.phi.entities.actions.EmployeeAction;
import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.baseEntity.AgendaConf;
import com.phi.entities.baseEntity.AppointmentSp;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AppointmentSpRest")
@Path("/appointmentsps")
public class AppointmentSpRest extends BaseRest<AppointmentSp>{

	protected static final Logger log = Logger.getLogger(AppointmentSpRest.class);
	
	@GET
	@Path("byagenda/{agendaConfId}/{from}/{to}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response byAgenda(	@PathParam("agendaConfId") long agendaConfId,
								@PathParam("from") String from,
								@PathParam("to") String to) {
	
		String json = "";
		if (from == null || from.isEmpty() || from.length() != 10 ||
			to == null || to.isEmpty() || to.length() != 10) {
			log.error("invaldid date range: "+ from+" / "+ to);
			return Response.ok(json).build();
		}
		
		Date fromIncluded = parteInput(from);
		Date toExcluded = parteInput(to);
		if (fromIncluded == null || toExcluded == null) {
			return Response.ok(json).build();
		}
		
		if (fromIncluded.after(toExcluded)){
			log.error("invaldid date range FROM: "+ from+" TO: "+ to + "(FROM>TO)");
			return Response.ok(json).build();
		}
		
		
		AgendaConfAction aca = AgendaConfAction.instance();
		AgendaConf agenda = null;
		
		try {
			agenda = aca.read(agendaConfId);
		} catch (PhiException e1) {
			log.error("wrong agendaId: "+agendaConfId);
			return Response.ok(json).build();
			
		}
		ServiceDeliveryLocation linea = agenda.getServiceDeliveryLocation();
		
		List<Long> sdl = UserBean.instance().getSdLocs();
		if (linea != null && !sdl.contains(linea.getInternalId())){
			log.error("trying to access to agendenda connetted to unallowed working line. agendaConfId: "+agendaConfId+" enabled sdl: "+sdl);
			return Response.ok(json).build();
		}
		
		AppointmentSpAction aspa  = AppointmentSpAction.instance();
		aspa.getGreaterEqual().put("data", fromIncluded );
		aspa.getLess().put("data", toExcluded );
		aspa.getEqual().put("agendaConf", agenda);
		
		try {
			List<AppointmentSp> lista = aspa.list();
			json = listToJson(lista);
			
		} catch (PhiException e) {
			log.error("error reading appointmentSp for agenda "+agendaConfId+" from "+from+" to"+to);
			e.printStackTrace();
		}
		
		return Response.ok(json).build();
	}
	

	//from/to format  dd-MM-yyyy
	//from included, to excluded
	@GET
	@Path("byoperator/{employeeId}/{from}/{to}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response byOperator(	@PathParam("employeeId") long employeeId,
								@PathParam("from") String from,
								@PathParam("to") String to) {
	
		//Employee current = UserBean.instance().getCurrentEmployee();
		Employee emp;
		String json = "";
		
		if (employeeId == 0) {
			log.error("invaldid employeeId: "+employeeId);
			return Response.ok(json).build();
		}
		
		try {
			emp = EmployeeAction.instance().read(employeeId);
		} catch (PhiException e1) {
			log.error("invaldid employeeId: "+employeeId);
			return Response.ok(json).build();
		}
		
		AppointmentSpAction aspa  = AppointmentSpAction.instance();
		aspa.getEqual().put("employee", emp);

		if (from == null || from.isEmpty() || from.length() != 10 ||
			to == null || to.isEmpty() || to.length() != 10) {
			log.error("invaldid date range: "+ from+" / "+ to);
			return Response.ok(json).build();
		}
		
		Date fromIncluded = parteInput(from);
		Date toExcluded = parteInput(to);
		if (fromIncluded == null || toExcluded == null) {
			return Response.ok(json).build();
		}
		
		if (fromIncluded.after(toExcluded)){
			log.error("invaldid date range FROM: "+ from+" TO: "+ to + "(FROM>TO)");
			return Response.ok(json).build();
		}
		
		aspa.getGreaterEqual().put("data", fromIncluded );
		aspa.getLess().put("data", toExcluded );
		
		try {
			List<AppointmentSp> lista = aspa.list();
			json = listToJson(lista);
			
		} catch (PhiException e) {
			log.error("error reading appointmentSp: "+from+" month"+to);
			e.printStackTrace();
		}
		
		return Response.ok(json).build();
	}
	
	
	//from/to format  dd-MM-yyyy
		//from included, to excluded
		@GET
		@Path("byagendaandope/{agendaConfId}/{employeeId}/{from}/{to}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response byagendaandope( @PathParam("agendaConfId") long agendaConfId,	
									@PathParam("employeeId") long employeeId,
									@PathParam("from") String from,
									@PathParam("to") String to) {
		
			
			
			AgendaConfAction aca = AgendaConfAction.instance(); 
			AgendaConf agenda = null;
			String json = "";
			
			try {
				agenda = aca.read(agendaConfId);
			} catch (PhiException e1) {
				log.error("wrong agendaId: "+agendaConfId);
				return Response.ok(json).build();
				
			}
			ServiceDeliveryLocation linea = agenda.getServiceDeliveryLocation();
			
			List<Long> sdl = UserBean.instance().getSdLocs();
			if (!sdl.contains(linea.getInternalId())){
				log.error("trying to access to agendenda connetted to unallowed working line. agendaConfId: "+agendaConfId+" enabled sdl: "+sdl);
				return Response.ok(json).build();
			}
			
			
			Employee emp;
			
			
			if (employeeId == 0) {
				log.error("invaldid employeeId: "+employeeId);
				return Response.ok(json).build();
			}
			
			try {
				emp = EmployeeAction.instance().read(employeeId);
			} catch (PhiException e1) {
				log.error("invaldid employeeId: "+employeeId);
				return Response.ok(json).build();
			}
			
			AppointmentSpAction aspa  = AppointmentSpAction.instance();
			aspa.getEqual().put("agendaConf", agenda);
			aspa.getEqual().put("employee", emp);

			if (from == null || from.isEmpty() || from.length() != 10 ||
				to == null || to.isEmpty() || to.length() != 10) {
				log.error("invaldid date range: "+ from+" / "+ to);
				return Response.ok(json).build();
			}
			
			Date fromIncluded = parteInput(from);
			Date toExcluded = parteInput(to);
			if (fromIncluded == null || toExcluded == null) {
				return Response.ok(json).build();
			}
			
			if (fromIncluded.after(toExcluded)){
				log.error("invaldid date range FROM: "+ from+" TO: "+ to + "(FROM>TO)");
				return Response.ok(json).build();
			}
			
			aspa.getGreaterEqual().put("data", fromIncluded );
			aspa.getLess().put("data", toExcluded );
			
			try {
				List<AppointmentSp> lista = aspa.list();
				json = listToJson(lista);
				
			} catch (PhiException e) {
				log.error("error reading appointmentSp: "+from+" month"+to);
				e.printStackTrace();
			}
			
			return Response.ok(json).build();
		}
		
		
		
	
	
	
//	
//	@POST
//	@Path("/injectnew")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.TEXT_PLAIN)
//	public Response injectNewEntity(String jsonEntity) {
//		
//			
//			AppointmentSpAction asa = AppointmentSpAction.instance();
//			if (jsonEntity == null || jsonEntity.isEmpty()){
//				try {
//					asa.inject(asa.newEntity());
//				} catch (Exception e) {
//					log.error("error injecting new Entity");
//					e.printStackTrace();
//					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("invalid injection").build();
//				} 
//			}
//			else {
//				try {
//					AppointmentSp entity = mapper.readValue(jsonEntity, AppointmentSp.class);
//					asa.inject(entity);
//					
//				} catch (Exception e) {
//					log.error("error parsing injected Entity");
//					e.printStackTrace();
//					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("invalid injected entity").build();
//				} 
//			}
//			
//			
//			return Response.ok().build(); 
//	}
//	

	private Date parteInput(String s){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date d = sdf.parse(s);
			return d;
		} catch (ParseException e) {
			log.error("error parsing"+s);
			return null;
		}
	}
	
	private String listToJson (List<AppointmentSp> lista)  {
		
//		Context conv = Contexts.getConversationContext();
//		AppointmentSp convApp = (AppointmentSp)conv.get("AppointmentSp");
//		long id = convApp == null? 0 : convApp.getInternalId();
		
		List<HashMap<String,Object>> results = new ArrayList<HashMap<String,Object>>();
		String json ="";
		
		for (AppointmentSp a : lista){
			HashMap<String, Object> result = appToMap(a);
//			if (id == a.getInternalId()){
//				result.put("editable", true);
//			}
			results.add(result);
		}
		
		try {
			json = mapper.writeValueAsString(results);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("error converting AppointmentSp List");
		}
		
		return json;
	}
	
	
	
	private HashMap<String,Object> appToMap (AppointmentSp a){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		long id = a.getInternalId();
		result.put("id", id);
		
		Date start = a.getData();
		Date end = a.getEnd();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-ddTHH:mm:ss");
//		result.put("start", sdf.format(start));
//		result.put("end", sdf.format(end));
		
		result.put("start", start);
		result.put("end", end);
		
		
		if (a.getEmployee() != null) {
			String operatore = a.getEmployee().getName().getFam() + " " + a.getEmployee().getName().getGiv();
			result.put("title", operatore);
		}
		
		if (a.getAgendaConf() != null){
			String agenda = a.getAgendaConf().getNome();
			result.put("agenda", agenda);
		}
		
		if (a.getSoggetto() != null){
			Person p = a.getSoggetto().getUtente();
			String paziente = p.getName().getFam() + " " + p.getName().getGiv();
			result.put("paziente", paziente);
		}
		
		Attivita att = null;
		
		if (a.getVisitaSp() != null){
			VisitaSp vis = a.getVisitaSp();
		
			long visitaId = vis.getInternalId();
			result.put("visita", visitaId);
			
			if (vis.getVisitaMdl() != null && !vis.getVisitaMdl().isEmpty() && vis.getVisitaMdl().get(0) != null) {
				att = vis.getVisitaMdl().get(0).getAttivita();
			}
		}
		
		if (a.getAccertSp() != null) {
			AccertSp acc = a.getAccertSp();
			
			long accId = acc.getInternalId();
			result.put("accertamento", accId);
			 
			if (acc.getAccertaMdl() != null && !acc.getAccertaMdl().isEmpty() && acc.getAccertaMdl().get(0) != null) {
				att = acc.getAccertaMdl().get(0).getAttivita();
			}
		}
		
		
		if (att != null ) {
			long attId = att.getInternalId();
			result.put("attivita", attId);
			
			if (att.getProcpratiche() != null) {
				Procpratiche pratica = att.getProcpratiche();
				long praticaId = pratica.getInternalId();
				String prot = pratica.getNumero();
				result.put("pratica", praticaId);
				result.put("prot", prot);
			}
		}
		
		
		
		return result;
	}
}
