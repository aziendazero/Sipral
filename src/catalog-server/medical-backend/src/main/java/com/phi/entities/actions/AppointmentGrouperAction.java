package com.phi.entities.actions;

import org.apache.log4j.Logger;
import javax.naming.NamingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.LockManager;
import com.phi.cs.lock.Locker;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("AppointmentGrouperAction")
@Scope(ScopeType.CONVERSATION)
public class AppointmentGrouperAction extends BaseAction<AppointmentGrouper, Long> {

	private static final long serialVersionUID = 586478277L;
	private static final Logger log = Logger.getLogger(AppointmentGrouperAction.class);

	public static AppointmentGrouperAction instance() {
		return (AppointmentGrouperAction) Component.getInstance(AppointmentGrouperAction.class, ScopeType.CONVERSATION);

	}

	public static final String COMPLETE="complete";

	/**
	 * Complete the appointment Grouper in order to forbid the insertion of new appointments.
	 * @param appointmentGrouper to be closed
	 * @throws NamingException 
	 * @throws PhiException 
	 */
	public String completeAppointmentGrouper(AppointmentGrouper appointmentGrouper) throws NamingException, PhiException{

		if (appointmentGrouper== null) {
			return null;
		}

		LockManager delegate = Locker.instance();
		String lockedBy = null;

		//CHECK IF APPOINTMENT_GROUPER IS LOCKED 
		if(delegate.isLocked("PHI",Long.toString(appointmentGrouper.getInternalId()))){
			lockedBy = delegate.isLockedBy("PHI",Long.toString(appointmentGrouper.getInternalId()));
		}

		if(lockedBy != null){
			//READ to retrieve name_giv and name_fam from employee with username "locked by"
			EmployeeAction empAction = EmployeeAction.instance();
			List<HashMap> employeeList = new ArrayList<HashMap>();
			empAction.equal.put("username", lockedBy);
			empAction.select.add("name.giv");
			empAction.select.add("name.fam");
			employeeList = (List<HashMap>) empAction.select();
			for(HashMap nameEmployee : employeeList){
				lockedBy = (String)((HashMap)nameEmployee.get("name")).get("fam") +  " " + (String)((HashMap)nameEmployee.get("name")).get("giv");
			}
		} else {
			Vocabularies vocabularies = VocabulariesImpl.instance();
			CodeValue statusCodeAppointmentGrouper = vocabularies.getCodeValueCsDomainCode("PHIDIC", "AppGroupStatus", "completed");
			appointmentGrouper.setStatusCode(statusCodeAppointmentGrouper);
		}
		
		return lockedBy;

	}
	
	public void filterByProcedureCode(String... codes) {
		
		if (entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if (findSubCriteria("procReq") == null) {
			Criteria procReqs = entityCriteria.createCriteria("procedureRequests","procReq", Criteria.LEFT_JOIN);
			Criteria procs = procReqs.createCriteria("procReq.procedure", "procs", Criteria.LEFT_JOIN);
			Criteria icd9Codes = procs.createCriteria("procs.codeIcd9", "code", Criteria.LEFT_JOIN);
			icd9Codes.add(Restrictions.in("code.code", codes));
		}
		
	}
	
	public String manageExternalSDLoc(AppointmentGrouper appGrp) {
		String result = "images/codeGreen.gif";
		UserBean ub = (UserBean) Component.getInstance("userBean");
		List<Long> sdLocs = ub.getSdLocs();
		
		ServiceDeliveryLocation sdLoc = appGrp.getServiceDeliveryLocation();
		boolean isInternal = sdLoc != null && sdLocs.contains(sdLoc.getInternalId());
		List<ServiceDeliveryLocation> extSdLocs = appGrp.getAvailableSDLoc();
		int extSdLocsSize = extSdLocs.size();
		
		if (extSdLocs != null && extSdLocsSize > 0) {
			if (isInternal && extSdLocsSize > 1) {
				result = "images/multi_sede_int.png";
			} else {
				Iterator<ServiceDeliveryLocation> availableSDLocIterator = extSdLocs.iterator();
				while (availableSDLocIterator.hasNext()) {
					ServiceDeliveryLocation availableSDLoc = availableSDLocIterator.next();
					if (sdLocs.contains(availableSDLoc.getInternalId())){
						if (extSdLocs.size() < 2) {
							if (isInternal) {
								if (!sdLoc.equals(availableSDLoc)) {
									result = "images/multi_sede_int.png";
								}
								break;
							}
						}
						result = "images/multi_sede_ext.png";
						break;
					}
				}
			}
		}		
		return result;
	}

	public void filterByEnalbledSDLoc(List<Long> enabledSdlId) {
		filterByEnalbledSDLoc(enabledSdlId, true);
	}
	
	public void filterByEnalbledSDLoc(List<Long> enabledSdlId, Boolean fullSearch) {
		if (entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if (fullSearch == null || !fullSearch)
			entityCriteria.add(Restrictions.in("serviceDeliveryLocation.internalId", enabledSdlId));
		
		else {
			if (findSubCriteria("availSDL") == null) {
				entityCriteria.createAlias("availableSDLoc","availSDL", Criteria.LEFT_JOIN);
			
				entityCriteria.add(
						Restrictions.or(
								Restrictions.in("serviceDeliveryLocation.internalId",enabledSdlId),
								Restrictions.and(Restrictions.in("availSDL.internalId", enabledSdlId), Restrictions.eq("StatusCode.code", "active"))
						));
			}
		}	
	}
	
}