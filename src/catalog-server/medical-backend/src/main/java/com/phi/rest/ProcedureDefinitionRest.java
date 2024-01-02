package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.act.ProcedureDefinition;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProcedureDefinitionRest")
@Path("/proceduredefinitions")
public class ProcedureDefinitionRest extends BaseRest<ProcedureDefinition> {

	private static final long serialVersionUID = 8208409192026753988L;

}
