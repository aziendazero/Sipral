package com.phi.rest;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.entities.baseEntity.Protocollo;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("ProtocolloRest")
@Path("/protocollos")
public class ProtocolloRest extends BaseRest<Protocollo> implements Serializable {

	private static final long serialVersionUID = 1697113559603086172L;
	
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public String get() throws Exception {
		try {
			IdataModel<Protocollo> listFromConversation = (IdataModel<Protocollo>)Contexts.getConversationContext().get("ProtocolloList");
			
			String json = "";
			if (listFromConversation instanceof PhiDataModel) {
				json = mapper.writeValueAsString(listFromConversation.getList());
			} else if (listFromConversation instanceof PagedDataModel) {
				json = mapper.writeValueAsString(((PagedDataModel) listFromConversation).getEntities().getWrappedData());
			}
			
			return json;

		} catch (Exception e) {
			log.error("Error get ProtocolloList", e);
			throw e;
		}
	}
	
}