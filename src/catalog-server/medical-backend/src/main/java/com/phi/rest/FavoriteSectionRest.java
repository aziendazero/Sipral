package com.phi.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.FavoriteSection;
import com.phi.entities.FavoriteTab;
import com.phi.events.PhiEvent;
import com.phi.rest.action.RestAction;
import com.phi.security.UserBean;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("FavoriteSectionRest")
@Path("/favoritesections")
public class FavoriteSectionRest extends BaseRest<FavoriteSection> implements Serializable {

	private static final long serialVersionUID = 4238292466327461852L;
	
	
	/*
	 * 	FIXME - Custom create method
	 * 	Quando creo una nuova section, devo aggiornare la posizione delle altre sections.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			
			if (methodOverride == null) {
				// CREATE
				FavoriteSection sectionToCreate = mapper.readValue(jSonEntity, FavoriteSection.class);
				
				// Update sort order
				RestAction favoriteSectionAction = new RestAction(FavoriteSection.class);
        		
				favoriteSectionAction.getGreaterEqual().put("sortOrder", sectionToCreate.getSortOrder());
				favoriteSectionAction.getEqual().put("isActive", true);
				favoriteSectionAction.getEqual().put("tab.internalId", sectionToCreate.getTab().getInternalId());
				favoriteSectionAction.getEqual().put("columnIndex", sectionToCreate.getColumnIndex());
								
        		List<FavoriteSection> sectionsToUpdate = favoriteSectionAction.getEntityCriteria().list();
        		
        		if (sectionsToUpdate != null) {

					for (int i = 0; i < sectionsToUpdate.size(); i++) { 						
						FavoriteSection section = sectionsToUpdate.get(i);						
						section.setSortOrder(section.getSortOrder() + 1);
						ca.update(section);						
					}
        		}
        		
        		// Create section
				FavoriteSection createdSection = (FavoriteSection) ca.create(sectionToCreate);
				
				ca.flushSession();
				
				String jsonEntity = mapper.writeValueAsString(createdSection);
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + createdSection.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, createdSection);
				
				return Response.ok(jsonEntity).build(); //FIXME change to created
			
			} else {
				
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					//UPDATE				
					return update(jSonEntity);
				} else if (methodOverride.equalsIgnoreCase("DELETE")) {
					//DELETE
					Long id = Long.parseLong(jSonEntity);
					return delete(id);
				} else {
					log.error("Error x-method-override contains unknown method: " + methodOverride);
					throw new IllegalArgumentException("Error x-method-override contains unknown method: " + methodOverride);
				}				
			}
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error create entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error create entity " + jSonEntity).build();
		}
	}
	
	
	/*
	 * 	FIXME - Custom update method
	 * 	Quando aggiorno una section, devo aggiornare la posizione delle altre sections.
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(String jSonEntity) {
		try {		
			
			FavoriteSection sectionToUpdate = mapper.readValue(jSonEntity, FavoriteSection.class);
			
			// Update sort order
			RestAction favoriteSectionAction = new RestAction(FavoriteSection.class);
			
			favoriteSectionAction.getSelect().add("sortOrder");
			favoriteSectionAction.getEqual().put("internalId", sectionToUpdate.getInternalId());
			
			int oldSortOrder = (Integer) ((HashMap<String, Object>) favoriteSectionAction.getEntityCriteria().list().get(0)).get("sortOrder");
			int newSortOrder = (Integer) sectionToUpdate.getSortOrder();
						
			favoriteSectionAction.cleanRestrictions();
			
			if (newSortOrder < oldSortOrder) {
				
				favoriteSectionAction.getGreaterEqual().put("sortOrder", newSortOrder);
				favoriteSectionAction.getLess().put("sortOrder", oldSortOrder);
				favoriteSectionAction.getEqual().put("isActive", true);
				favoriteSectionAction.getEqual().put("tab.internalId", sectionToUpdate.getTab().getInternalId());
				favoriteSectionAction.getEqual().put("columnIndex", sectionToUpdate.getColumnIndex());
	    		        		
	    		List<FavoriteSection> sectionsToUpdate = favoriteSectionAction.getEntityCriteria().list();
	    		
	    		if (sectionsToUpdate != null) {

					for (int i = 0; i < sectionsToUpdate.size(); i++) { 						
						FavoriteSection section = sectionsToUpdate.get(i);						
						section.setSortOrder(section.getSortOrder() + 1);
						ca.update(section);						
					}
	    		}
	    		
			} else if (newSortOrder > oldSortOrder) {
								
				favoriteSectionAction.getGreater().put("sortOrder", oldSortOrder);
				favoriteSectionAction.getLessEqual().put("sortOrder", newSortOrder);
				favoriteSectionAction.getEqual().put("isActive", true);
				favoriteSectionAction.getEqual().put("tab.internalId", sectionToUpdate.getTab().getInternalId());
				favoriteSectionAction.getEqual().put("columnIndex", sectionToUpdate.getColumnIndex());
	    		        		
	    		List<FavoriteSection> sectionsToUpdate = favoriteSectionAction.getEntityCriteria().list();
	    		
	    		if (sectionsToUpdate != null) {

					for (int i = 0; i < sectionsToUpdate.size(); i++) { 						
						FavoriteSection section = sectionsToUpdate.get(i);						
						section.setSortOrder(section.getSortOrder() - 1);
						ca.update(section);						
					}
	    		}	    			    		
			}  
			
			// Update section
			FavoriteSection updatedSection = (FavoriteSection)ca.update(sectionToUpdate);	
			
			ca.flushSession();
			
			String jsonEntity = mapper.writeValueAsString(updatedSection);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, updatedSection);
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
	/*
	 * 	FIXME - Custom delete method
	 * 	Quando cancello una section, devo aggiornare la posizione delle altre sections.
	 * 	Inoltre devo unlinkare la section dal tab (detached entity exception).
	 */	
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id) {		
		try {		
			
			FavoriteSection sectionToDelete = ca.get(FavoriteSection.class, id);
				
			// Update sort order
			RestAction favoriteSectionAction = new RestAction(FavoriteSection.class);
			
			favoriteSectionAction.getGreater().put("sortOrder", sectionToDelete.getSortOrder());
			favoriteSectionAction.getEqual().put("isActive", true);
			favoriteSectionAction.getEqual().put("tab.internalId", sectionToDelete.getTab().getInternalId());
			favoriteSectionAction.getEqual().put("columnIndex", sectionToDelete.getColumnIndex());
						
    		List<FavoriteSection> sectionsToUpdate = favoriteSectionAction.getEntityCriteria().list();
    		
    		if (sectionsToUpdate != null) {

				for (int i = 0; i < sectionsToUpdate.size(); i++) { 
					
					FavoriteSection section = sectionsToUpdate.get(i);
					
					section.setSortOrder(section.getSortOrder() - 1);
					ca.update(section);						
				}
    		}
    		    		
    		// Unlink section from tab			
			FavoriteTab tabToUnlink = ca.get(FavoriteTab.class, sectionToDelete.getTab().getInternalId());
			tabToUnlink.removeSection(sectionToDelete);
			    		
    		// Delete section    		  		
    		ca.delete(sectionToDelete);
			
			ca.flushSession();
			
			Events.instance().raiseEvent(PhiEvent.DELETE, sectionToDelete);
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error delete entity by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error delete entity by id: " + id).build();
		}
	}
	
}
