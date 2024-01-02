package com.phi.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.hibernate.collection.PersistentBag;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.entities.baseEntity.Medicine;
import com.phi.entities.baseEntity.PrescriptionDischarge;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.events.PhiEvent;
import com.phi.rest.datamodel.InjectDatamodel;
import com.phi.security.UserBean;

@Restrict("#{identity.isLoggedIn(false)}")
@Name("PrescriptionDischargeRest")
@Path("/prescriptiondischarges")
public class PrescriptionDischargeRest extends BaseRest<PrescriptionDischarge> {

	private static final long serialVersionUID = 104516076093898844L;

	/**
	 * Get entity by id, put in conversation
     * If id == 0 eject from conversation
     * 
	 * @param id
	 * @return json representation
	 */
	
	@Override
	@GET
	@Path("/{restrictions}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inject(@PathParam("restrictions") PathSegment restrictions) {
		long id = 0L;
		try {
			InjectDatamodel<PrescriptionDischarge> res = new InjectDatamodel<PrescriptionDischarge>();
			//inject and set entity (if id<=0 entity is ejected and passed null)
			id = Long.parseLong(restrictions.getPath());
			inject(id);	
				
			//custom hardcoded deproxy
			for (PrescriptionMedicineGeneric pm : entity.getPrescriptionMedicine()) {
				if (pm.getMedicine() instanceof HibernateProxy) { //de proxy
					pm.setMedicine(((Medicine)((HibernateProxy)pm.getMedicine()).getHibernateLazyInitializer().getImplementation()));
				}
				
				List<CodeValuePhi> aifaNotes = pm.getMedicine().getAifaNote();
				
				if (aifaNotes instanceof PersistentBag) { //de proxy
					((PersistentBag)aifaNotes).forceInitialization();
				}
				
				for (int z = 0; z<aifaNotes.size(); z++) {
					if (aifaNotes.get(z) instanceof HibernateProxy) {
						aifaNotes.set(z, ((CodeValuePhi)((HibernateProxy)aifaNotes.get(z)).getHibernateLazyInitializer().getImplementation()));
					}
				}
				
			}
			
			if (entity.getCode() instanceof HibernateProxy) { //de proxy
				entity.setCode(((CodeValue)((HibernateProxy)entity.getCode()).getHibernateLazyInitializer().getImplementation()));
			}
			
			if (entity.getStatusCode() instanceof HibernateProxy) { //de proxy
				entity.setStatusCode(((CodeValue)((HibernateProxy)entity.getStatusCode()).getHibernateLazyInitializer().getImplementation()));
			}
			
			if (entity.getRouteCode() instanceof HibernateProxy) { //de proxy
				entity.setRouteCode(((CodeValue)((HibernateProxy)entity.getRouteCode()).getHibernateLazyInitializer().getImplementation()));
			}
			
			res.setEntity(entity);
			
			//set additional collections
			Map<String, List<Map<String, Object>>> additional = new HashMap<String, List<Map<String, Object>>>();
			res.setAdditional(additional);
			
			String json = mapper.writeValueAsString(res);
			
			Events.instance().raiseEvent(PhiEvent.INJECT, entity);
			
			return Response.ok(json).build();
			
		} catch (NumberFormatException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id + ". Id is not a valid number", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id + ". Id is not a valid number").build();
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error get " + entityClass.getSimpleName() + " by id: " + id, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error get " + entityClass.getSimpleName() + " by id: " + id).build();
		}

	}
	
	/**
	 * Update an entity
	 * 
	 * It may get invoked by the @POST equivalent if the "x-method-override" header is configured for "UPDATE"
	 * 
	 * @param jSonEntity
	 */
	
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(String jSonEntity) {
		try {
			PrescriptionDischarge detachedEntity = mapper.readValue(jSonEntity, entityClass);
			
			entity = (PrescriptionDischarge)ca.update(detachedEntity);
			
			//Copy dosage list from detached entity to managed entity, since dosages aren't mapped, merge will not update them
			for (int z = 0; z<detachedEntity.getPrescriptionMedicine().size(); z++) {
				entity.getPrescriptionMedicine().get(z).setDosage(detachedEntity.getPrescriptionMedicine().get(z).getDosage());
			}
			
			ca.flushSession();
			
			String json = mapper.writeValueAsString(entity);
						
			Events.instance().raiseEvent(PhiEvent.CREATE, entity);
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}

}