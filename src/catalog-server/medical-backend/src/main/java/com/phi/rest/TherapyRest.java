package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.act.Therapy;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("TherapyRest")
@Path("/therapies")
public class TherapyRest extends BaseRest<Therapy> {

	private static final long serialVersionUID = -5982009433656394933L;

}