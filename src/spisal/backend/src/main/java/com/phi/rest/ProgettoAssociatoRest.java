package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;

import com.phi.entities.baseEntity.ProgettoAssociato;
import com.phi.util.ProgettoAssociatoCounter;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProgettoAssociatoRest")
@Path("/progettoassociato")
public class ProgettoAssociatoRest extends BaseRest<ProgettoAssociato> implements Serializable {

	/**
	 * http://localhost:8080/spisal/resource/rest/progettoassociato/counter 
	 */
	private static final long serialVersionUID = 2532302012320508498L;
	
	@GET
	@Path("/counter")
	@Produces(MediaType.TEXT_PLAIN)
	public Response get() {

		ResponseBuilder responseBuilder = null;

		try {
			Object data = "0";
			ProgettoAssociatoCounter pac = (ProgettoAssociatoCounter)Component.getInstance("ProgettoAssociatoCounter");

			if (pac != null) {
				Object obj =pac.getCounter();
				
				if (obj!=null)
					data = obj;
				
				responseBuilder = Response.ok(data);				
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error getting count " + e.getMessage(), e);
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getting count " + e.getMessage());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}
		return responseBuilder.build();
	}
			
}