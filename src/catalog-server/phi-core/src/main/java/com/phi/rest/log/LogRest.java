package com.phi.rest.log;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;

/**
 * Writes server log messages 
 * 
 * @author alex.zupan
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("LogRest")
@Path("/logs")
public class LogRest {

	private static final Logger log = Logger.getLogger(LogRest.class);

	@POST
	@Path("/error")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response logError(String logz) {
		try {
			
			log.error("[cid="+Conversation.instance().getId()+"] " + logz );

			return Response.ok().build();
			
		} catch (Exception e) {
			log.error("Error writing log: " + log, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error writing log: " + log + e.getMessage()).build();
		}
	}
	
	@POST
	@Path("/info")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response logInfo(String logz) {
		try {
			
			log.info("[cid="+Conversation.instance().getId()+"] " + logz );

			return Response.ok().build();
			
		} catch (Exception e) {
			log.error("Error writing log: " + log, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error writing log: " + log + e.getMessage()).build();
		}
	}
}