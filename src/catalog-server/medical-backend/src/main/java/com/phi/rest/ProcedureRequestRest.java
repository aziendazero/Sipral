package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.ProcedureRequest;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProcedureRequestRest")
@Path("/procedurerequests")
public class ProcedureRequestRest extends BaseRest<ProcedureRequest> {

	private static final long serialVersionUID = 9052153283672643559L;

}
