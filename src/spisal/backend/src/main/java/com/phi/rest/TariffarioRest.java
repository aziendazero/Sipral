package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.Tariffario;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("TariffarioRest")
@Path("/tariffarios")
public class TariffarioRest extends BaseRest<Tariffario> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5322188853588836778L;
		
}