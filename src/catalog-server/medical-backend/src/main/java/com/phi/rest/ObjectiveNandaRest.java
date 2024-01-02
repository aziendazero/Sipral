package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.act.LEPActivity;
import com.phi.entities.act.ObjectiveNanda;
import com.phi.entities.actions.LEPActivityAction;
import com.phi.events.PhiEvent;
import com.phi.security.UserBean;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("ObjectiveNandaRest")
@Path("/objectivenandas")
public class ObjectiveNandaRest extends BaseRest<ObjectiveNanda> implements Serializable {

	private static final long serialVersionUID = -6541171124327299634L;
	
	
	/*
	 * 	FIXME - Custom create method
	 * 	Quando creo un nuovo objective, devo linkare l'activity associata (altrimenti il link non viene salvato sul DB).
	 */	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			if (methodOverride != null) {
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					return update(jSonEntity);
				} else if (methodOverride.equalsIgnoreCase("DELETE")) {
					Long id = Long.parseLong(jSonEntity);
					return delete(id);
				} else {
					log.error("Error x-method-override contains unknown method: " + methodOverride);
					throw new IllegalArgumentException("Error x-method-override contains unknown method: " + methodOverride);
				}
			} else { // CREATE
				ObjectiveNanda objective = mapper.readValue(jSonEntity, entityClass);
				LEPActivity activity = objective.getActivity();
				
				activity.setObjective(objective);				
				
				ca.create(objective);
				ca.flushSession();
								
				LEPActivityAction.instance().validateActivity(activity);
				
				String jsonId = mapper.writeValueAsString(objective.getInternalId());
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + objective.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, objective);
				
				return Response.ok(jsonId).build(); //FIXME change to created
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
		
}
