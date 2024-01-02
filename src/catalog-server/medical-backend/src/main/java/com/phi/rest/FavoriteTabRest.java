package com.phi.rest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.FavoriteSection;
import com.phi.entities.FavoriteTab;
import com.phi.events.PhiEvent;
import com.phi.rest.action.RestAction;
import com.phi.rest.datamodel.ListDatamodel;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("FavoriteTabRest")
@Path("/favoritetabs")
public class FavoriteTabRest extends BaseRest<FavoriteTab> implements Serializable {

	private static final long serialVersionUID = -3746791978685561933L;
	
	
	/*
	 *  Custom read method:	It is necessary to deproxy some properties. 	
	 */	
	@GET
	@Path("{restrictions}/{page}")
	@Produces(APPLICATION_JSON_UTF8)
	public Response get(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page, @javax.ws.rs.core.Context ServletContext servletContext) throws UnsupportedEncodingException {
		
		Map<String,List<String>> restrictionMap = decodeResctrictions(restrictions);
		
		try {
			
			if (page <= 0)  {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}

			RestAction favoriteTabAction = new RestAction(FavoriteTab.class);
			
			restrictions2action(favoriteTabAction, restrictionMap);
					            
            List<FavoriteTab> tabs = favoriteTabAction.getEntityCriteria().list();
                                    
        	for (int i = 0; i < tabs.size(); i++) {

        		FavoriteTab tab = tabs.get(i);        		
        		ca.refreshIfNeeded(tab);      
        		loadProxy(tab, "subTypeCode");
        		loadProxy(tab, "section");
        		
        		List<FavoriteSection> sections = tab.getSection();
        		
        		for (int j = 0; j < sections.size(); j++) {
        			
        			FavoriteSection section = sections.get(j);
        			ca.refreshIfNeeded(section);        		
            		loadProxy(section, "profile");        			
        		}
        	}        		
                        
            String readUrl = BASE_REST_URL + entityClass.getSimpleName().toLowerCase() + "s/" + restrictions + "/";

            ListDatamodel<FavoriteTab> dm = new ListDatamodel<FavoriteTab>(tabs, readUrl, readPageSize, page);
            		
			String result = mapper.writeValueAsString(dm);
			
			Events.instance().raiseEvent(PhiEvent.READ, entityClass.getSimpleName());
			
			return Response.ok(result).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get entities " + entityClass.getSimpleName() + " by restrictions: " + restrictionMap, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get entities " + entityClass.getSimpleName() + " by restrictions: " + restrictionMap).build();
		}	
	}
		
	
	/*
	 * 	FIXME - Custom create method
	 * 	Quando creo un nuovo tab, devo aggiornare la posizione degli altri tabs.
	 */
	@POST
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	@Transactional(TransactionPropagationType.REQUIRED)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			
			if (methodOverride == null) {
				// CREATE
				FavoriteTab tabToCreate = mapper.readValue(jSonEntity, FavoriteTab.class);
								
				// Update sort order
				RestAction favoriteTabAction = new RestAction(FavoriteTab.class);
				
				favoriteTabAction.getGreaterEqual().put("sortOrder", tabToCreate.getSortOrder());
				favoriteTabAction.getEqual().put("isActive", true);
				favoriteTabAction.getEqual().put("serviceDeliveryLocation.internalId", tabToCreate.getServiceDeliveryLocation().getInternalId());
				favoriteTabAction.getEqual().put("typeCode.code", tabToCreate.getTypeCode().getCode());				
				if (tabToCreate.getSubTypeCode() != null) {
					favoriteTabAction.getEqual().put("subTypeCode.code", tabToCreate.getSubTypeCode().getCode());
				}
				
				List<FavoriteTab> tabsToUpdate = favoriteTabAction.getEntityCriteria().list();
				
				if (tabsToUpdate != null) {
					
					for (int i = 0; i < tabsToUpdate.size(); i++) {						
						FavoriteTab tab = tabsToUpdate.get(i);						
						tab.setSortOrder(tab.getSortOrder() + 1);						
						ca.update(tab);						
					}
				}
				
				// Create tab
				FavoriteTab createdTab = (FavoriteTab) ca.create(tabToCreate);
				
				ca.flushSession();
												
				String jsonEntity = mapper.writeValueAsString(createdTab);
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + createdTab.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, createdTab);
				
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
	 * 	Quando aggiorno un tab, devo aggiornare la posizione degli altri tabs.
	 */
	@PUT
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	@Transactional(TransactionPropagationType.REQUIRED)
	public Response update(String jSonEntity) {
		try {		
						
			FavoriteTab tabToUpdate = mapper.readValue(jSonEntity, FavoriteTab.class);
			
			// Update sort order			
			RestAction favoriteTabAction = new RestAction(FavoriteTab.class);
			
			favoriteTabAction.getSelect().add("sortOrder");
			favoriteTabAction.getEqual().put("internalId", tabToUpdate.getInternalId());
			
			int oldSortOrder = (Integer) ((HashMap<String, Object>) favoriteTabAction.getEntityCriteria().list().get(0)).get("sortOrder");
			int newSortOrder = (Integer) tabToUpdate.getSortOrder();
						
			favoriteTabAction.cleanRestrictions();
									
			if (newSortOrder < oldSortOrder) {	
				
				favoriteTabAction.getGreaterEqual().put("sortOrder", newSortOrder);
				favoriteTabAction.getLess().put("sortOrder", oldSortOrder);
				favoriteTabAction.getEqual().put("isActive", true);
				favoriteTabAction.getEqual().put("serviceDeliveryLocation.internalId", tabToUpdate.getServiceDeliveryLocation().getInternalId());
				favoriteTabAction.getEqual().put("typeCode.code", tabToUpdate.getTypeCode().getCode());				
				if (tabToUpdate.getSubTypeCode() != null) {
					favoriteTabAction.getEqual().put("subTypeCode.code", tabToUpdate.getSubTypeCode().getCode());
				}
				
				List<FavoriteTab> tabsToUpdate = favoriteTabAction.getEntityCriteria().list();
				
				if (tabsToUpdate != null) {
					
					for (int i = 0; i < tabsToUpdate.size(); i++) {						
						FavoriteTab tab = tabsToUpdate.get(i);						
						tab.setSortOrder(tab.getSortOrder() + 1);						
						ca.update(tab);						
					}
				}
								   		
			} else if (newSortOrder > oldSortOrder) {
								
				favoriteTabAction.getGreater().put("sortOrder", oldSortOrder);
				favoriteTabAction.getLessEqual().put("sortOrder", newSortOrder);
				favoriteTabAction.getEqual().put("isActive", true);
				favoriteTabAction.getEqual().put("serviceDeliveryLocation.internalId", tabToUpdate.getServiceDeliveryLocation().getInternalId());
				favoriteTabAction.getEqual().put("typeCode.code", tabToUpdate.getTypeCode().getCode());				
				if (tabToUpdate.getSubTypeCode() != null) {
					favoriteTabAction.getEqual().put("subTypeCode.code", tabToUpdate.getSubTypeCode().getCode());
				}
				
				List<FavoriteTab> tabsToUpdate = favoriteTabAction.getEntityCriteria().list();
				
				if (tabsToUpdate != null) {
					
					for (int i = 0; i < tabsToUpdate.size(); i++) {						
						FavoriteTab tab = tabsToUpdate.get(i);						
						tab.setSortOrder(tab.getSortOrder() - 1);						
						ca.update(tab);						
					}
				}
			} 
			
			// Update tab
			FavoriteTab updatedTab = (FavoriteTab) ca.update(tabToUpdate);	
			
			ca.flushSession();
									
			String jsonEntity = mapper.writeValueAsString(tabToUpdate);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, updatedTab);
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
	
	/*
	 * 	FIXME - Custom delete method
	 * 	Quando cancello un tab, devo aggiornare la posizione degli altri tabs.
	 */
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id) {			
		try {		
				
			FavoriteTab tabToDelete = ca.get(FavoriteTab.class, id);
			
			// Update sort order
			RestAction favoriteTabAction = new RestAction(FavoriteTab.class);
								
			favoriteTabAction.getGreater().put("sortOrder", tabToDelete.getSortOrder());
			favoriteTabAction.getEqual().put("isActive", true);
			favoriteTabAction.getEqual().put("serviceDeliveryLocation.internalId", tabToDelete.getServiceDeliveryLocation().getInternalId());
			favoriteTabAction.getEqual().put("typeCode.code", tabToDelete.getTypeCode().getCode());				
			if (tabToDelete.getSubTypeCode() != null) {
				favoriteTabAction.getEqual().put("subTypeCode.code", tabToDelete.getSubTypeCode().getCode());
			}
			
			List<FavoriteTab> tabsToUpdate = favoriteTabAction.getEntityCriteria().list();
			
			if (tabsToUpdate != null) {
				
				for (int i = 0; i < tabsToUpdate.size(); i++) {						
					FavoriteTab tab = tabsToUpdate.get(i);						
					tab.setSortOrder(tab.getSortOrder() - 1);						
					ca.update(tab);						
				}
			}
			
			// Delete tab
			ca.delete(tabToDelete);
			
			ca.flushSession();
			
			Events.instance().raiseEvent(PhiEvent.DELETE, tabToDelete);
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error delete entity by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error delete entity by id: " + id).build();
		}
	}
	
		
}