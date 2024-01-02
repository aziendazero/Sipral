package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.FileAccDitte;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("FileAccDitteRest")
@Path("/fileaccdittes")
public class FileAccDitteRest extends BaseRest<FileAccDitte> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3471463251400946154L;



		
}