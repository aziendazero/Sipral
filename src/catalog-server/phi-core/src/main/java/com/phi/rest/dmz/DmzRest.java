package com.phi.rest.dmz;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;

/**
 * Demilitarized zone Rest services
 * 
 * @author Alex
 */

@Path("/dmz")
public class DmzRest {

	private static final Logger log = Logger.getLogger(DmzRest.class);
	
	/**
	 * Check db connection
	 * @return http status 200 ok if is ok, else 500.
	 */	
	@GET
	@Path("/ping")
	public Response get() {
		try {
			
			CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
			
			boolean works = ca.checkDb();
			
			if (works) {
				return Response.ok().build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error checking db connection").build();
			}
			
			
		} catch (Exception e) {
			log.error("Error checking db connection", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error checking db connection").build();
		}
	}
}
