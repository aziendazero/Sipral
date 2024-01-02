package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

import com.phi.entities.baseEntity.AlfrescoDocument;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("AlfrescoDocumentRest")
@Path("/alfrescodocuments")
public class AlfrescoDocumentRest extends BaseRest<AlfrescoDocument> implements Serializable {

	private static final long serialVersionUID = 4873759105743599045L;

}