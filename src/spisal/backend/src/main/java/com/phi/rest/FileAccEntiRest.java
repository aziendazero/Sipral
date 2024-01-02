package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.FileAccEnti;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("FileAccEntiRest")
@Path("/fileaccentis")
public class FileAccEntiRest extends BaseRest<FileAccEnti> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5457368816488050842L;

		
}