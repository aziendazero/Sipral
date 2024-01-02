package com.phi.entities.actions;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Dosage;
import com.phi.entities.act.SubstanceAdministration;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.baseEntity.PrescriptionMedicineGeneric;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.PQ;
import com.phi.events.PhiEvent;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("PrescriptionAction")
@Scope(ScopeType.CONVERSATION)
public class PrescriptionAction extends BaseAction<Prescription, Long> {

	private static final long serialVersionUID = -181281741838L;
	private static final Logger log = Logger.getLogger(PrescriptionAction.class);
	
	public static PrescriptionAction instance() {
		return (PrescriptionAction) Component.getInstance(PrescriptionAction.class, ScopeType.CONVERSATION);
	}
	
	//Unused, if useful, re implement with dosage from PrescriptionMedicine
	
//	public String frequency(Prescription prescription) {
//		// calculate frequency based on Dosage List
//		if (prescription == null)
//			return "";
//		
//		if (prescription.getNeedsBased()) {
//			return "Al bisogno";
//		}
//		
//		List<Dosage> dosageList = prescription.getDosage();
//		if (dosageList == null || dosageList.isEmpty())
//			return "NA";
//		String ret="";
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//		for (Dosage dos : dosageList) {
//			if (dos == null)
//				continue;
//			String quantity="";
//			String weekDay="";
//			if (dos.getQuantity() != null && dos.getQuantity().getValue() != null)
//				quantity = ""+dos.getQuantity().getValue().intValue();
//			if (dos.getWeekDayCode() != null && dos.getWeekDayCode().getCurrentTranslation() != null) {
//				weekDay = dos.getWeekDayCode().getCurrentTranslation();
//				weekDay = weekDay.substring(0,3);
//			}
//			if (quantity.isEmpty() || weekDay.isEmpty())
//				continue;
//			ret+=quantity+ " "+weekDay;
//			if (dos.getDaytime() != null) {
//				ret+=" "+sdf.format(	dos.getDaytime());
//			}
//			else if (dos.getDaymomentCode() != null) {
//				ret+=" "+dos.getDaymomentCode().getCurrentTranslation();
//			}
//			ret+="; ";
//			
//			
//		}
//		
//		return ret;
//	}
	

	public void infusionSpeedChanged(ValueChangeEvent event) {
		
	}

	public void infusionDurationChanged(ValueChangeEvent event) {
		
	}
	
	public void quantityChanged(ValueChangeEvent event) {
		
	}
	
	public void validatePrescription(Prescription prescription, Date validationDate) throws PhiException {
		try {
			Vocabularies vocabularies = VocabulariesImpl.instance();
			CodeValue activeCode = vocabularies.getCodeValueCsDomainCode("PHIDIC","StatusCodes","active");
			prescription.setStatusCode(activeCode);
			IVL<Date> validityPeriod = new IVL<Date>();
			validityPeriod.setLow(validationDate);
			if (prescription.getExtemporaneous() || prescription.getUrgent()) {
				validityPeriod.setHigh(new Date(validationDate.getTime() + 60000)); //1 minute	
			}
			prescription.setValidityPeriod(validityPeriod);
			generateAdministrations(prescription);
			ca.update(prescription);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, prescription);
			
			ca.flushSession();
		} catch (Exception e) {
			log.error("Error validation prescription with internalId " + prescription.getInternalId(), e);
			throw(new PhiException("Error validation for prescription with internalId " + prescription.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}
	
	public void invalidatePrescription(Prescription prescription, Date invalidationDate, boolean modify) throws PhiException {
		try {	
			Date oldInvalidationDate = null;
			
			IVL<Date> validityPeriod = prescription.getValidityPeriod(); 
			if (validityPeriod == null) {
				validityPeriod = new IVL<Date>();
				// FIXME: why???
				validityPeriod.setLow(invalidationDate);
			} else {
				oldInvalidationDate = validityPeriod.getHigh();
			}
			
			validityPeriod.setHigh(invalidationDate);
			prescription.setValidityPeriod(validityPeriod);
			prescription.setModified(modify);
			prescription.setCancelledBy(UserBean.instance().getCurrentEmployee());
			// Delete all future administrations
			if (prescription.getSubstanceAdministration() != null && prescription.getSubstanceAdministration().size() > 0) {
				Iterator<SubstanceAdministration> iterator = prescription.getSubstanceAdministration().iterator();
				while (iterator.hasNext()) {
					SubstanceAdministration substanceAdministration = iterator.next();
					if (substanceAdministration.getStatusCode().getCode().equals("PLANNED")) {						
						if (substanceAdministration.getPlannedDate() == null || (substanceAdministration.getPlannedDate() != null && substanceAdministration.getPlannedDate().after(invalidationDate))) {
							iterator.remove();
							ca.delete(substanceAdministration);
							
							Events.instance().raiseEvent(PhiEvent.DELETE, substanceAdministration);							
						}
					}
				}
			}
			// If reinvalidation and old invalidation date < new invalidation date 
			// then it is necessary to recreate the administrations from the old invalidation date and the new one
			if (oldInvalidationDate != null && oldInvalidationDate.before(invalidationDate)) {
				
				// If old invalidation date < start of next generateAdministrations
				// then it is necessary to create the administrations between old invalidation date and min(new invalidation date, end of previous generateAdministrations) 
				
				Calendar oldInvalidation = Calendar.getInstance();
				oldInvalidation.setTime(oldInvalidationDate);
				
				Calendar nextStart = Calendar.getInstance();
				nextStart.setTime(new Date());				
				nextStart.set(Calendar.DATE, nextStart.get(Calendar.DATE) + 3);
				nextStart.set(Calendar.HOUR_OF_DAY, 0);
				nextStart.set(Calendar.MINUTE, 0);
				nextStart.set(Calendar.SECOND, 0);
				nextStart.set(Calendar.MILLISECOND, 0);
				
				if (oldInvalidation.before(nextStart))
				{
					// Calculate prescriptionRealStartDate: is the bigger of startDate and validationDate
					Date startDate = prescription.getPeriod().getLow(); 				// Can't be null
					Date validationDate = prescription.getValidityPeriod().getLow(); 	// Can't be null
					Calendar prescriptionRealStartDate = Calendar.getInstance();
					
					if (startDate.after(validationDate)) {
						prescriptionRealStartDate.setTime(startDate);
					} else {
						prescriptionRealStartDate.setTime(validationDate);
					}
					
					Calendar realStart = oldInvalidation;
					
					Calendar invalidation = Calendar.getInstance();
					invalidation.setTime(invalidationDate);
					
					Calendar previousEnd = Calendar.getInstance();
					previousEnd.setTime(new Date());					
					previousEnd.set(Calendar.DATE, previousEnd.get(Calendar.DATE) + 2);
					previousEnd.set(Calendar.HOUR_OF_DAY, 23);
					previousEnd.set(Calendar.MINUTE, 59);
					previousEnd.set(Calendar.SECOND, 0);
					previousEnd.set(Calendar.MILLISECOND, 0);	
					
					Calendar realEnd = Calendar.getInstance();
					
					if (previousEnd.before(invalidation) || previousEnd.equals(invalidation))
						realEnd = previousEnd;
					else
						realEnd = invalidation;
					
					// Generate Administrations between realStart and realEnd
					generateAdministrationsFromTo(prescription, prescriptionRealStartDate, realStart, realEnd);					
				}
			}
			
			ca.update(prescription);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, prescription);
			
			ca.flushSession();
						
		} catch (Exception e) {
			log.error("Error validation prescription with internalId " + prescription.getInternalId(), e);
			throw(new PhiException("Error validation for prescription with internalId " + prescription.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}
	
	/**
	 * Generates substance administrations of injected prescription
	 * @return number of generated substance administrations
	 * @throws PhiException
	 */
	public Integer generateAdministrations(Prescription prescription) throws PhiException {
				
		if (prescription == null) {
			return 0;
		}
		
		if (!prescription.getStatusCode().getCode().equals("active")) {
			return 0;
		}
		
		try {
			
			//Calculate prescriptionRealStartDate: is the bigger of startDate and validationDate
			Date startDate = prescription.getPeriod().getLow(); 				// Can't be null
			Date validationDate = prescription.getValidityPeriod().getLow(); 	// Can't be null
			Calendar prescriptionRealStartDate = Calendar.getInstance();
			
			if (startDate.after(validationDate)) {
				prescriptionRealStartDate.setTime(startDate);
			} else {
				prescriptionRealStartDate.setTime(validationDate);
			}
			
			//Calculate prescriptionRealEndDate: is the invalidationDate if present or endDate if present or null if both not present
			Calendar prescriptionRealEndDate = Calendar.getInstance();
			if (prescription.getValidityPeriod() != null && prescription.getValidityPeriod().getHigh() != null)
				prescriptionRealEndDate.setTime(prescription.getValidityPeriod().getHigh());
			else if (prescription.getPeriod().getHigh() != null)
				prescriptionRealEndDate.setTime(prescription.getPeriod().getHigh());
			else
				prescriptionRealEndDate = null;
						
			// Now find out start date to calculate administrations:
			// if prescription doesn't have administrations, generate all from prescriptionRealStartDate (after there is a check end > start)
			// else generate only for the 2nd day in the future (start = dd+2/mm/yyyy 00:00)
			// Ex: today is 01/01/2014 --> start = 03/01/2014 00:00
			Calendar start = Calendar.getInstance();
			start.setTime(new Date());
			start.set(Calendar.DATE, start.get(Calendar.DATE) + 2);
			start.set(Calendar.HOUR_OF_DAY, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			
			if (prescription.getSubstanceAdministration() == null || prescription.getSubstanceAdministration().size() == 0) {
				start.setTime(prescriptionRealStartDate.getTime());				
			}
			
			//Now find out end date to calculate administrations:
			//generate all to min(prescriptionRealEndDate, end = dd+2/mm/yyyy 23:59) (after there is a check end > start)
			//Ex: today is 01/01/2014 --> end = 03/01/2014 23:59			
			Calendar end = Calendar.getInstance();
			end.setTime(new Date());
			end.set(Calendar.DATE, end.get(Calendar.DATE) + 2);
			end.set(Calendar.HOUR_OF_DAY, 23);
			end.set(Calendar.MINUTE, 59);
			end.set(Calendar.SECOND, 0);
			end.set(Calendar.MILLISECOND, 0);				
			
			// If end date is before end limit, get end date
			if (prescriptionRealEndDate != null && prescriptionRealEndDate.before(end)) { 
				end.setTime(prescriptionRealEndDate.getTime());
			}
			
			return generateAdministrationsFromTo(prescription, prescriptionRealStartDate, start, end);
			
		} catch (Exception e) {
			log.error("Error generating administrations for prescription with internalId " + prescription.getInternalId(), e);
			throw(new PhiException("Error generating administrations for prescription with internalId " + prescription.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}
		
		
	public Integer generateAdministrationsFromTo(Prescription prescription, Calendar realStartDate, Calendar start, Calendar end) throws PhiException {	
		try {
			
			Integer generatedAdministration = 0;
			
			String prescriptionType = prescription.getCode().getCode();
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			CodeValue plannedCode = vocabularies.getCodeValueCsDomainCode("PHIDIC","AdministrationStatus","PLANNED");
						
			//Set seconds and milliseconds to 0
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			end.set(Calendar.SECOND, 0);
			end.set(Calendar.MILLISECOND, 0);
			
			//NEEDS BASED
			if (prescription.getNeedsBased()) { 	//if needs based create only 1 administration, if not already exists a planned one
				boolean doit = true;
				if (prescription.getSubstanceAdministration() != null && prescription.getSubstanceAdministration().size()>0) {					
					for (SubstanceAdministration admin : prescription.getSubstanceAdministration()) {
						if (admin.getStatusCode().getCode().equals("PLANNED")) { 	//if already exists a planned administration don't do it
							doit = false;
							break;
						}
					}
				}
				if (doit) {
					PQ quantity;
					if (prescriptionType.equals("PHARMA")) {
						quantity = prescription.getPrescriptionMedicine().get(0).getDosage().get(0).getQuantity();
					} else {
						quantity = null;
					}
					createAdministration(plannedCode, null, quantity, prescription);
					generatedAdministration++;
					ca.flushSession();
				}
				return generatedAdministration;
			}
			
			//EXTEMPORANEOUS
			if (prescription.getExtemporaneous()) { 	//if extemporaneous create only 1 administration, if not already exists
				if (prescription.getSubstanceAdministration() == null || prescription.getSubstanceAdministration().size()==0) {
					PQ quantity;
					if (prescriptionType.equals("PHARMA")) {
						quantity = prescription.getPrescriptionMedicine().get(0).getDosage().get(0).getQuantity();
					} else {
						quantity = null;
					}
					createAdministration(plannedCode, realStartDate.getTime(), quantity, prescription);
					generatedAdministration++;
					ca.flushSession();
				}
				return generatedAdministration;
			}
			
			//URGENT
			if (prescription.getUrgent()) { 	//if urgent create only 1 administration, if not already exists
				if (prescription.getSubstanceAdministration() == null || prescription.getSubstanceAdministration().size()==0) {
					PQ quantity;
					if (prescriptionType.equals("PHARMA")) {
						quantity = prescription.getPrescriptionMedicine().get(0).getDosage().get(0).getQuantity();
					} else {
						quantity = null;
					}
					UserBean ub = UserBean.instance();
					//createAdministrationAndComplete(realStartDate.getTime(), quantity, prescription, ub.getCurrentSystemUser());
					createAdministrationAndComplete(prescription.getPeriod().getLow(), quantity, prescription, ub.getCurrentSystemUser());
					generatedAdministration++;
					CodeValue activeCode = vocabularies.getCodeValueCsDomainCode("PHIDIC","StatusCodes","completed");
					prescription.setStatusCode(activeCode);
					ca.flushSession();
				}
				return generatedAdministration;
			}
			
			if (prescription.getContinuous()) {
				if (prescription.getSubstanceAdministration() == null || prescription.getSubstanceAdministration().size()==0) {
					PQ quantity;
					quantity = null;
					
					createAdministration(plannedCode, realStartDate.getTime(), quantity, prescription);
					generatedAdministration++;
					ca.flushSession();
				}
				return generatedAdministration;
			}
			
			//PLANNED
			if (end.getTimeInMillis() > start.getTimeInMillis()) {	//start before end
				Calendar administrationDateCandidate = Calendar.getInstance();				
				
				//For infusions useful only the first one: in that case we don't need quantities (different for each prescriptionMedicine) 
				//but only times (equal for each prescriptionMedicine).
				//For pharmas there is only one prescriptionMedicine, so take the first
				PrescriptionMedicineGeneric prescriptionMedicine = prescription.getPrescriptionMedicine().get(0);
				
				Integer dayInterval;
				PQ quantity;		
				Calendar dosageTime = Calendar.getInstance();
				
				for (Dosage dosage : prescriptionMedicine.getDosage()) {
					//For each dosage get  dayInterval, quantity, dosageTime
					//FIXME valido per infusioni (con e senza farmaci), prescrizioni ecc ecc?
					dayInterval = dosage.getDayInterval(); 
					if (prescriptionType.equals("PHARMA")){
						quantity = dosage.getQuantity();
					} else {
						quantity = null;
					}
					
					dosageTime.setTime(dosage.getDaytime());
					
					administrationDateCandidate.set(realStartDate.get(Calendar.YEAR), 
													realStartDate.get(Calendar.MONTH), 
													realStartDate.get(Calendar.DATE), 
													dosageTime.get(Calendar.HOUR_OF_DAY), 
													dosageTime.get(Calendar.MINUTE),
													0);
					administrationDateCandidate.set(Calendar.MILLISECOND, 0);
					
					while (administrationDateCandidate.before(end) || administrationDateCandidate.equals(end)) {				
						if (administrationDateCandidate.after(start) || administrationDateCandidate.equals(start)) {
							
							boolean toCreate = true;
							
							if (administrationDateCandidate != null && prescription.getSubstanceAdministration() != null) {								
								for (SubstanceAdministration administration : prescription.getSubstanceAdministration()) {
									if (administration.getPlannedDate().equals(administrationDateCandidate.getTime())) {
										toCreate = false;
										break;
									}
								}
							}
							
							//Check also in RootPrescription if administration already exsist 
							if (administrationDateCandidate != null && prescription.getRootPrescription() != null && prescription.getRootPrescription() instanceof Prescription) {
								List<SubstanceAdministration> sbstdmnlst = ((Prescription)prescription.getRootPrescription()).getSubstanceAdministration();
								for (SubstanceAdministration administration : sbstdmnlst) {
									if (administration.getPlannedDate().equals(administrationDateCandidate.getTime())) {
										toCreate = false;
										break;
									}
								}
							}
							
							if (toCreate) {
								//Generate administration						
								createAdministration(plannedCode, administrationDateCandidate.getTime(), quantity, prescription);
								generatedAdministration++;
								
								if (generatedAdministration % 10 == 0) {
									//FIXME: don't use ca
									ca.flushSession();
								}								
							}
						}
						administrationDateCandidate.set(Calendar.DATE,administrationDateCandidate.get(Calendar.DATE) + dayInterval); 
					}
				}
			}
			ca.flushSession();
			return generatedAdministration;
		} catch (Exception e) {
			log.error("Error generating administrations for prescription with internalId " + prescription.getInternalId(), e);
			throw(new PhiException("Error generating administrations for prescription with internalId " + prescription.getInternalId(), e, ErrorConstants.GENERIC_ERR_CODE));
		}
	}
	
	private SubstanceAdministration createAdministrationAndComplete(Date plannedDate, PQ plannedQuantity, Prescription prescription, EmployeeRole authorRole) throws PersistenceException, DictionaryException {
		Vocabularies vocabularies = VocabulariesImpl.instance();
		CodeValue doneCode = vocabularies.getCodeValueCsDomainCode("PHIDIC","AdministrationStatus","DONE");
		SubstanceAdministration adm = createAdministration(doneCode,  plannedDate,  plannedQuantity, prescription);
		adm.setAuthor(authorRole.getEmployee());
		adm.setAuthorRole(authorRole.getCode());
		IVL<Date> date = new IVL<Date>();
		date.setLow(adm.getPlannedDate());
		date.setHigh(adm.getPlannedDate());
		adm.setAdministratedDate(date);
		adm.setAdministratedQuantity(adm.getAdministratedQuantity());
		ca.create(adm);
		return adm;
	}
	
	private SubstanceAdministration createAdministration(CodeValue statusCode, Date plannedDate, PQ plannedQuantity, Prescription prescription) throws PersistenceException {
		SubstanceAdministration administration = new SubstanceAdministration();
		administration.setStatusCode(statusCode);
		if (plannedDate != null) {
			administration.setPlannedDate(plannedDate);
		}
		if (plannedQuantity != null) {
			administration.setPlannedQuantity(plannedQuantity);
		}
		administration.setPrescription(prescription);
		prescription.addSubstanceAdministration(administration);
		
		ca.create(administration);
		
		Events.instance().raiseEvent(PhiEvent.CREATE, administration);		
		
		return administration;
	}
}
	
	
	