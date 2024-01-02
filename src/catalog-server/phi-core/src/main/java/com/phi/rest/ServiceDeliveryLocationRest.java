package com.phi.rest;

import java.io.Serializable;
import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.role.ServiceDeliveryLocation;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ServiceDeliveryLocationRest")
@Path("/servicedeliverylocations")
public class ServiceDeliveryLocationRest extends BaseRest<ServiceDeliveryLocation> implements Serializable {
	
	private static final long serialVersionUID = 6892599915773382137L;
		
}