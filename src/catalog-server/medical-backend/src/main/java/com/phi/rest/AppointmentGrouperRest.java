package com.phi.rest;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.actions.AppointmentGrouperAction;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.events.PhiEvent;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AppointmentGrouperRest")
@Path("/appointmentgroupers")
public class AppointmentGrouperRest extends BaseRest<AppointmentGrouper> implements Serializable {

	private static final long serialVersionUID = 6406998699954162149L;
	private static final Logger log = Logger.getLogger(AppointmentGrouperRest.class);

	/**
	 * Used by Ambulatory Portal dashboard to complete the waitingList
	 * 
	 * @param id Appointment
	 * @return
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@POST
	@Path("/status/{id : \\d+}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
	@Produces(MediaType.APPLICATION_JSON)
	public Response completeAppointmentGrouper(@PathParam("id") long id,@FormParam("action") String action) {

		AppointmentGrouperAction appGroupAction = AppointmentGrouperAction.instance();
		HashMap<String, Object> response = new HashMap<String, Object>();
		FunctionsBean fb = FunctionsBean.instance();

		if(!action.equals(AppointmentGrouperAction.COMPLETE)) {
			response.put("appointmentGrouperId", id);
			response.put("message", fb.getStaticTranslation("STATUS_CODE_NOT_VALID_ERROR"));
			response.put("status", "error");
		}

		AppointmentGrouper appointmentGrouper = (AppointmentGrouper)ca.get(AppointmentGrouper.class, id);
		if(appointmentGrouper == null){
			log.error("Error completing appointmentGrouper");
			return Response.ok("Error completing appointment grouper for appointmentGrouperId: " + id + "appointmentGrouper not found").build();
		}
		try {

			String completeAppointmentGrouper;

			if((completeAppointmentGrouper=appGroupAction.completeAppointmentGrouper(appointmentGrouper))!=null){
				//CREATE RESPONSE NEGATIVE
				response.put("appointmentGrouperId", id);
				response.put("message", fb.getStaticTranslation("APPOINTMENT_GROUPER_HANDLED_ERROR") + " " + completeAppointmentGrouper);
				response.put("status", "error");

			} else {

				ca.create(appointmentGrouper);
				
//				Events.instance().raiseEvent(PhiEvent.CREATE, entity);
				Events.instance().raiseEvent(PhiEvent.CREATE, appointmentGrouper);
				
				ca.flushSession();

				response.put("appointmentGrouperId", id);
				response.put("message", fb.getStaticTranslation("CHANGE_STATUS_OK_MESSAGE"));
				response.put("status", "ok");
			}

			String jsonEntity = mapper.writeValueAsString(response);

			return Response.ok(jsonEntity).build();

		} catch (PhiException e) {
			log.error("Error completing appointmentGrouper", e);
			return Response.ok("Error completing appointment grouper for appointmentId: " + id).build();
			//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error completing appointment grouper").build();
		} catch (Exception e) {
			log.error("Error completing appointmentGrouper", e);
			return Response.ok("Error completing appointment grouper for appointmentId: " + id).build();
		}
	}

}


