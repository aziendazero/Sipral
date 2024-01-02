package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.act.Procedure;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProcedureRest")
@Path("/procedures")
public class ProcedureRest extends BaseRest<Procedure> {

	private static final long serialVersionUID = 6008704624491715302L;

}
