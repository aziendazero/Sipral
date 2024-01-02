package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.act.VitalSign;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("VitalSignRest")
@Path("/vitalsigns")
public class VitalSignRest extends BaseRest<VitalSign> {

	private static final long serialVersionUID = -8128328517985590795L;

}