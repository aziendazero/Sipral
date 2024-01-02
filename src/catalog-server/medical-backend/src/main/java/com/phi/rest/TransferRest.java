package com.phi.rest;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.Transfer;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("TransferRest")
@Path("/transfers")
public class TransferRest extends BaseRest<Transfer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6954455713196918882L;
 
}
