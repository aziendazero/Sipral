package com.phi.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;

import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.PrescriptionAction;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.baseEntity.PrescriptionMedicine;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;
import com.phi.events.PhiEvent;
import com.phi.rest.action.RestAction;


@Restrict("#{identity.isLoggedIn(false)}")
@Name("PrescriptionRest")
@Path("/prescriptions")
public class PrescriptionRest extends BaseRest<Prescription> implements Serializable {

	private static final long serialVersionUID = 2967601316293249405L;

	public static final String VALIDATE="validate";
	public static final String INVALIDATE="invalidate";
	public static final String INVALIDATE_AND_MODIFY="invalidate_and_modify";
		
	/*
	 * 	FIXME - Custom update method
	 * 	Quando aggiorno una prescription, devo recuperare le vecchie prescriptionMedicine, unlinkarle dalla vecchia prescriptions e cancellarle (detached entity exception).
	 * 	Dopo aver aggiornato la prescription, devo aggiornare le prescriptionMedicine associate, altrimenti i dosaggi non vengono salvati.
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(String jSonEntity) {
		try {
			Prescription prescriptionToUpdate = mapper.readValue(jSonEntity, entityClass);
			
			PrescriptionAction.instance().refresh();
						
			RestAction prescriptionAction = new RestAction(Prescription.class);
			
			// Update prescriptionMedicines
			prescriptionAction.getEqual().put("internalId", prescriptionToUpdate.getInternalId());				
			Prescription oldPrescription = (Prescription) prescriptionAction.getEntityCriteria().list().get(0);
			
			List<PrescriptionMedicineGeneric> newPrescriptionMedicines = new ArrayList<PrescriptionMedicineGeneric>();
			List<PrescriptionMedicineGeneric> oldPrescriptionMedicines = new ArrayList<PrescriptionMedicineGeneric>();
						
			if (oldPrescription.getPrescriptionMedicine() != null) {
				
				oldPrescriptionMedicines.addAll(oldPrescription.getPrescriptionMedicine());
				newPrescriptionMedicines.addAll(prescriptionToUpdate.getPrescriptionMedicine());
										
				//Remove all and delete old prescriptionMedicines
				for (int i = 0; i < oldPrescriptionMedicines.size(); i++) { 
					PrescriptionMedicine oldPrescriptionMedicine = (PrescriptionMedicine) oldPrescriptionMedicines.get(i);
					oldPrescription.removePrescriptionMedicine(oldPrescriptionMedicine);										
					ca.delete(oldPrescriptionMedicine);
				}
			}			
						
			Prescription updatedPrescription = (Prescription)ca.update(prescriptionToUpdate);
						
			ca.flushSession();		
						
			if (newPrescriptionMedicines != null){
						
				// Copy dosage list from original entity to updated entity: since dosages aren't mapped, merge will not update them
				for (int z = 0; z < newPrescriptionMedicines.size(); z++) {
							
					PrescriptionMedicineGeneric prescriptionMedicineToUpdate = newPrescriptionMedicines.get(z);
					PrescriptionMedicineGeneric updatedPrescriptionMedicine = updatedPrescription.getPrescriptionMedicine().get(z);
								
					updatedPrescriptionMedicine.setDosage(prescriptionMedicineToUpdate.getDosage());
								
					updatedPrescriptionMedicine.onPrePersist();
								
					ca.update(updatedPrescriptionMedicine);
				}
			}
			
			ca.flushSession();
							
			String json = mapper.writeValueAsString(updatedPrescription);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, updatedPrescription);
			
			return Response.ok(json).build();
			
		} catch (Exception e) {
			log.error("Error update entity " + jSonEntity, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error update entity " + jSonEntity).build();
		}
	}
	
		
	@PUT
	@POST
	@Path("/status/{id : \\d+}/{changeDate}") //support digit only, without regEx this path matches also patient/read;name.fam=ad;name.giv=ad -> get() method
	public Response updateStatus(@PathParam("id") long id, @PathParam("changeDate") String changeDate, @FormParam("action") String action) {
		
		Prescription prescription = ca.get(entityClass, id);
		
		if(!(action.equals(VALIDATE) || action.equals(INVALIDATE) || action.equals(INVALIDATE_AND_MODIFY))) {
			//FIXME change to not valid
			return Response.ok("The action specified :" + action + "is not valid").build(); 
		}
		
		if(prescription == null) {
			//FIXME change to not found
			return Response.ok("Prescription by Id :" + id + " not found").build(); 
		}
		
		Date updateDate = new Date();
		
		try {
			updateDate = decodeISO8601(changeDate);
		} catch (Exception e) {
			log.error("Error PrescriptionRest updateStatus", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error PrescriptionRest updateStatus").build();
		}
				 		
		PrescriptionAction prescriptionAction = new PrescriptionAction(); 
		try {
			if (action.equals(VALIDATE)) {
				prescriptionAction.validatePrescription(prescription, updateDate);
			} else if (action.equals(INVALIDATE)) {
				prescriptionAction.invalidatePrescription(prescription, updateDate, false);
			} else if (action.equals(INVALIDATE_AND_MODIFY)) {
				prescriptionAction.invalidatePrescription(prescription, updateDate, true);
			}
		} catch (PhiException e) {
			log.error("Error updating status of prescription with id: " + id + " and action: " + action, e);
			return Response.ok("{\"error\":\"Error updating status of prescription with id: " + id + " and action: " + action + "\"}").build();
		}
		
		return Response.ok(prescription.getInternalId()).build(); //FIXME change to created
	}
}
