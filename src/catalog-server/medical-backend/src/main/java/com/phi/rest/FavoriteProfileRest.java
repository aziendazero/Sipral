package com.phi.rest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.FavoriteProfile;
import com.phi.entities.FavoriteSection;
import com.phi.entities.act.LEPActivity;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;
import com.phi.events.PhiEvent;
import com.phi.rest.action.RestAction;
import com.phi.rest.datamodel.ListDatamodel;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("FavoriteProfileRest")
@Path("/favoriteprofiles")
public class FavoriteProfileRest extends BaseRest<FavoriteProfile> implements Serializable {

	private static final long serialVersionUID = 8509860670483193738L;
	
	
	/*
	 * 	Custom read method: It is necessary to deproxy some properties.
	 */		
	@GET
	@Path("{restrictions}/{page}")
	@Produces(APPLICATION_JSON_UTF8)
	public Response get(@PathParam("restrictions") PathSegment restrictions, @DefaultValue("1") @PathParam("page") int page, @javax.ws.rs.core.Context ServletContext servletContext) throws UnsupportedEncodingException {
		
		Map<String,List<String>> restrictionMap = decodeResctrictions(restrictions);
		
		try {
			
			if (page <= 0) {
				return Response.status(Response.Status.BAD_REQUEST).entity("Page starts from 1").build();
			}

			RestAction favoriteProfileAction = new RestAction(FavoriteProfile.class);
			
			restrictions2action(favoriteProfileAction, restrictionMap);
								            
            List<FavoriteProfile> profiles = favoriteProfileAction.getEntityCriteria().list();
            
            FavoriteProfile profile = profiles.get(0);
            
            loadProxy(profile, "activity.dosage");
            loadProxy(profile, "nandaLep");
            loadProxy(profile, "responsibleRole");
            loadProxy(profile, "supportRole");
                       
            String readUrl = BASE_REST_URL + entityClass.getSimpleName().toLowerCase() + "s/" + restrictions + "/"; //FIXME

            ListDatamodel<FavoriteProfile> dm = new ListDatamodel<FavoriteProfile>(profiles, readUrl, readPageSize, page);
            		
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
	 * 	Quando creo un nuovo profile, devo linkare activities o prescriptions al nuovo profile (altrimenti il link non viene salvato sul DB). 
	 * 	In caso di prescriptions devo anche linkare le prescriptionMedicine alle prescriptions (altrimenti il link non viene salvato sul DB).
	 *  Inoltre c'è un nuovo methodOverride (UPDATE_SORTORDER).
	 */
	@POST
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response create(String jSonEntity, @HeaderParam("x-method-override") String methodOverride) {
		try {
			
			if (methodOverride == null) {
				// CREATE
				FavoriteProfile profileToCreate = mapper.readValue(jSonEntity, FavoriteProfile.class);
				
				if (profileToCreate.getActivity() != null ) {
					// Link activity to favorite profile 
					for (LEPActivity activity : profileToCreate.getActivity()) {
						
						activity.setProfile(profileToCreate);						
					}				
				} else if (profileToCreate.getPrescription() != null) {
					// Link prescription to favorite profile and prescriptioMedicine to prescription
					for (Prescription prescription : profileToCreate.getPrescription()) {
						
						prescription.setProfile(profileToCreate);
						
						for (PrescriptionMedicineGeneric prescriptionMedicine : prescription.getPrescriptionMedicine())	{
							
							prescriptionMedicine.setPrescription(prescription);
						}
					}	
				}
				       		
        		// Create profile
				FavoriteProfile createdProfile = (FavoriteProfile) ca.create(profileToCreate);
				
				ca.flushSession();
				
				String jsonEntity = mapper.writeValueAsString(createdProfile);
				
				log.info("[cid="+Conversation.instance().getId()+"] Object created: " + entityClass + ", id: " + createdProfile.getInternalId());
				
				Events.instance().raiseEvent(PhiEvent.CREATE, createdProfile);
				
				return Response.ok(jsonEntity).build(); //FIXME change to created
			
			} else {
				
				if (methodOverride.equalsIgnoreCase("UPDATE")) {
					//UPDATE				
					return update(jSonEntity);
				}else if (methodOverride.equalsIgnoreCase("UPDATE_SORTORDER")) {
					//UPDATE SORTORDER					
					return updateSortOrder(jSonEntity);
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
	 * 	Quando aggiorno un profile, devo recuperare le vecchie activites o prescriptions, unlinkarle dal vecchio profile e cancellarle (detached entity exception).
	 * 	Poi devo linkare activities o prescriptions al profile (altrimenti il link non viene salvato sul DB). 
	 * 	Nel caso di prescriptions, dopo aver creato le nuove prescription, devo aggiornare le prescriptionMedicine associate, altrimenti i dosaggi non vengono salvati.
	 */
	@PUT
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response update(String jSonEntity) {
		try {		
			
			FavoriteProfile profileToUpdate = mapper.readValue(jSonEntity, FavoriteProfile.class);
						
			RestAction favoriteProfileAction = new RestAction(FavoriteProfile.class);
							
			// Update profile activities or prescriptions
			favoriteProfileAction.getEqual().put("internalId", profileToUpdate.getInternalId());
				
			FavoriteProfile oldProfile = (FavoriteProfile) favoriteProfileAction.getEntityCriteria().list().get(0);
			
			List<LEPActivity> oldActivities = new ArrayList<LEPActivity>();

			List<LEPActivity> newActivities = new ArrayList<LEPActivity>();
			
			List<Prescription> newPrescriptions = new ArrayList<Prescription>();
			List<Prescription> oldPrescriptions = new ArrayList<Prescription>();
				
			if (oldProfile.getActivity() != null && profileToUpdate.getActivity() != null) {
								
				oldActivities.addAll(oldProfile.getActivity());
				newActivities.addAll(profileToUpdate.getActivity());
							
				//Remove all and delete old activities
				for (int i = 0; i < oldActivities.size(); i++) { 
					LEPActivity oldActivity = oldActivities.get(i);
					oldProfile.removeActivity(oldActivity);
					ca.delete(oldActivity);
				}
				
				//Add all
				for (int i = 0; i < newActivities.size(); i++) { 				
					LEPActivity newActivity = newActivities.get(i);
					oldProfile.addActivity(newActivity);							
				}
			} else if (oldProfile.getPrescription() != null && profileToUpdate.getPrescription() != null) {
								
				oldPrescriptions.addAll(oldProfile.getPrescription());
				newPrescriptions.addAll(profileToUpdate.getPrescription());
										
				//Remove all and delete old prescriptions
				for (int i = 0; i < oldPrescriptions.size(); i++) { 
					Prescription oldPrescription = oldPrescriptions.get(i);
					oldProfile.removePrescription(oldPrescription);										
					ca.delete(oldPrescription);
				}
				
				//Add all
				for (int i = 0; i < newPrescriptions.size(); i++) { 				
					Prescription newPrescription = newPrescriptions.get(i);
					oldProfile.addPrescription(newPrescription);
				}					
			}
						
			//Set title
			oldProfile.setTitle(profileToUpdate.getTitle());
			
			profileToUpdate = oldProfile;
				
			// Update profile
			FavoriteProfile updatedProfile = (FavoriteProfile) ca.update(profileToUpdate);
			
			ca.flushSession();
									
			if (newPrescriptions.size() > 0)
			{
				for (int i = 0; i < newPrescriptions.size(); i ++){
					
					Prescription prescriptionToUpdate = newPrescriptions.get(i);
					Prescription updatedPrescription = updatedProfile.getPrescription().get(i);
					
					if (prescriptionToUpdate.getPrescriptionMedicine() != null){
						
						// Copy dosage list from original entity to updated entity: since dosages aren't mapped, merge will not update them
						for (int z = 0; z < prescriptionToUpdate.getPrescriptionMedicine().size(); z++) {
							
							PrescriptionMedicineGeneric prescriptionMedicineToUpdate = prescriptionToUpdate.getPrescriptionMedicine().get(z);
							PrescriptionMedicineGeneric updatedPrescriptionMedicine = updatedPrescription.getPrescriptionMedicine().get(z);
							
							updatedPrescriptionMedicine.setDosage(prescriptionMedicineToUpdate.getDosage());
							
							updatedPrescriptionMedicine.onPrePersist();
							
							ca.update(updatedPrescriptionMedicine);
						}
					}
				}
			}			
									
			ca.flushSession();
			
			String jsonEntity = mapper.writeValueAsString(updatedProfile);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, updatedProfile);
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
		
	@PUT
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON, APPLICATION_JSON_UTF8})
	@Produces(APPLICATION_JSON_UTF8)
	public Response updateSortOrder(String jSonEntity) {
		try {		
			
			FavoriteProfile profileToUpdate = mapper.readValue(jSonEntity, FavoriteProfile.class);
					
			RestAction favoriteProfileAction = new RestAction(FavoriteProfile.class);
			
			favoriteProfileAction.getSelect().add("section.internalId");
			favoriteProfileAction.getEqual().put("internalId", profileToUpdate.getInternalId());
			
			long oldSectionId = (Long) ((HashMap<String, Object>) ((HashMap<String, Object>) favoriteProfileAction.getEntityCriteria().list().get(0)).get("section")).get("internalId");
			long newSectionId = (Long) profileToUpdate.getSection().getInternalId();
			
			favoriteProfileAction.cleanRestrictions();
			
			favoriteProfileAction.getSelect().add("sortOrder");
			favoriteProfileAction.getEqual().put("internalId", profileToUpdate.getInternalId());
			
			int oldSortOrder = (Integer) ((HashMap<String, Object>) favoriteProfileAction.getEntityCriteria().list().get(0)).get("sortOrder");
			int newSortOrder = (Integer) profileToUpdate.getSortOrder();
						
			favoriteProfileAction.cleanRestrictions();
			
			FavoriteProfile updatedProfile = new FavoriteProfile();
			
			if (oldSectionId == newSectionId) {				
			
				if (newSortOrder < oldSortOrder) {
					
					// Update sort order
					favoriteProfileAction.getGreaterEqual().put("sortOrder", newSortOrder);
					favoriteProfileAction.getLess().put("sortOrder", oldSortOrder);
					favoriteProfileAction.getEqual().put("isActive", true);
					favoriteProfileAction.getEqual().put("section.internalId", newSectionId);
		    		        		
		    		List<FavoriteProfile> profilesToUpdate = favoriteProfileAction.getEntityCriteria().list();
		    		
		    		if (profilesToUpdate != null) {
	
						for (int i = 0; i < profilesToUpdate.size(); i++) { 						
							FavoriteProfile profile = profilesToUpdate.get(i);						
							profile.setSortOrder(profile.getSortOrder() + 1);
							ca.update(profile);						
						}
		    		}
		    		
		    		// Update profile
					updatedProfile = (FavoriteProfile) ca.update(profileToUpdate);
		    		
				} else if (newSortOrder > oldSortOrder) {
									
					// Update sort order
					favoriteProfileAction.getGreater().put("sortOrder", oldSortOrder);
					favoriteProfileAction.getLessEqual().put("sortOrder", newSortOrder);
					favoriteProfileAction.getEqual().put("isActive", true);
					favoriteProfileAction.getEqual().put("section.internalId", newSectionId);
					
					List<FavoriteProfile> profilesToUpdate = favoriteProfileAction.getEntityCriteria().list();
		    		
		    		if (profilesToUpdate != null) {
	
						for (int i = 0; i < profilesToUpdate.size(); i++) { 						
							FavoriteProfile profile = profilesToUpdate.get(i);						
							profile.setSortOrder(profile.getSortOrder() - 1);
							ca.update(profile);						
						}
		    		}
		    		
		    		// Update profile
					updatedProfile = (FavoriteProfile) ca.update(profileToUpdate);
				}
			} else if (oldSectionId != newSectionId) {
				
				List<FavoriteProfile> profilesToUpdate;
				
				// Update sort order			
				favoriteProfileAction.getGreater().put("sortOrder", oldSortOrder);
				favoriteProfileAction.getEqual().put("isActive", true);
				favoriteProfileAction.getEqual().put("section.internalId", oldSectionId);
							
	    		profilesToUpdate = favoriteProfileAction.getEntityCriteria().list();
	    		
	    		if (profilesToUpdate != null) {

					for (int i = 0; i < profilesToUpdate.size(); i++) { 
						
						FavoriteProfile profile = profilesToUpdate.get(i);
						
						profile.setSortOrder(profile.getSortOrder() - 1);
						ca.update(profile);						
					}
	    		}
	    		
	    		favoriteProfileAction.cleanRestrictions();
	    		
	    		// Update sort order
				favoriteProfileAction.getGreaterEqual().put("sortOrder", newSortOrder);
				favoriteProfileAction.getEqual().put("isActive", true);
				favoriteProfileAction.getEqual().put("section.internalId", newSectionId);
	    		        		
	    		profilesToUpdate = favoriteProfileAction.getEntityCriteria().list();
	    		
	    		if (profilesToUpdate != null) {

					for (int i = 0; i < profilesToUpdate.size(); i++) { 						
						FavoriteProfile profile = profilesToUpdate.get(i);						
						profile.setSortOrder(profile.getSortOrder() + 1);
						ca.update(profile);						
					}
	    		}
			}
			
			// Update profile
			updatedProfile = (FavoriteProfile) ca.update(profileToUpdate);	
	    								
			ca.flushSession();
			
			String jsonEntity = mapper.writeValueAsString(updatedProfile);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, updatedProfile);
			
			return Response.ok(jsonEntity).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
	
	/*
	 * 	FIXME - Custom delete method
	 * 	Quando cancello un profile, devo aggiornare la posizione degli altri profiles.
	 * 	Inoltre devo unlinkare il profile dalla section (detached entity exception).
	 */	
	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long id) {		
		try {		
			
			FavoriteProfile profileToDelete = ca.get(FavoriteProfile.class, id);
			
			RestAction favoriteProfileAction = new RestAction(FavoriteProfile.class);
									
			// Update sort order			
			favoriteProfileAction.getGreater().put("sortOrder", profileToDelete.getSortOrder());
			favoriteProfileAction.getEqual().put("isActive", true);
			favoriteProfileAction.getEqual().put("section.internalId", profileToDelete.getSection().getInternalId());
						
    		List<FavoriteProfile> profilesToUpdate = favoriteProfileAction.getEntityCriteria().list();
    		
    		if (profilesToUpdate != null) {

				for (int i = 0; i < profilesToUpdate.size(); i++) { 
					
					FavoriteProfile profile = profilesToUpdate.get(i);
					
					profile.setSortOrder(profile.getSortOrder() - 1);
					ca.update(profile);						
				}
    		}
    		    		
    		// Unlink profile from section			
			FavoriteSection sectionToUnlink = ca.get(FavoriteSection.class, profileToDelete.getSection().getInternalId());
			sectionToUnlink.removeProfile(profileToDelete);
			    		
    		// Delete profile    		  		
    		ca.delete(profileToDelete);
    		
			ca.flushSession();
			
			Events.instance().raiseEvent(PhiEvent.DELETE, profileToDelete);
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error delete entity by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error delete entity by id: " + id).build();
		}
	}	
		
}
