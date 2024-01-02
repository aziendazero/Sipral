package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.TimeBand;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("TimeBandRest")
@Path("/timebands")
public class TimeBandRest extends BaseRest<TimeBand> implements Serializable {

	private static final long serialVersionUID = 5201975621849082452L;
	
}