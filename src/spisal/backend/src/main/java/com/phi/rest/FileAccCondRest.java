package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.FileAccCond;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("FileAccCondRest")
@Path("/fileaccconds")
public class FileAccCondRest extends BaseRest<FileAccCond> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1634492040095292998L;

		
}