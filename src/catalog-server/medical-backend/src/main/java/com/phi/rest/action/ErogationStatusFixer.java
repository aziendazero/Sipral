package com.phi.rest.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.phi.security.UserBean;

public class ErogationStatusFixer {
	
	// Disabled future (now < planned - 60m)
	public static final String DUE_FUTURE_DISABLED 	= "DUE_FUTURE_DISABLED";
	// Enabled early (planned - 60m < now < planned - 30m) 
	public static final String DUE_FUTURE_ENABLED 	= "DUE_FUTURE_ENABLED";
	// Enabled right (planned - 30m < now < planned + 30m)
	public static final String DUE 					= "DUE";
	// Enabled delay (planned + 30m < now < planned + 60m)
	public static final String OVERDUE 				= "OVERDUE";
	// Disabled passed (now > planned + 60m) (super)
	public static final String MISSED = "MISSED";

	// Given in range (planned - 60m / planned - 30m)
	public static final String EROGATED_EARLY 			= "EROGATED_EARLY";
	// Given in range (planned - 30m / planned + 30m)		
	public static final String EROGATED_AS_PLANNED 		= "EROGATED_AS_PLANNED";
	// Given in range (planned + 30m / planned + 60m)
	public static final String EROGATED_LATE 			= "EROGATED_LATE";
	// Given with exception (super)
	public static final String EXCEPTION 						= "EXCEPTION";
	public static final String EXCEPTION_WRONG_DRUG 			= "EXCEPTION_WRONG_DRUG";
	public static final String EXCEPTION_WRONG_DOSE 			= "EXCEPTION_WRONG_DOSE";
	public static final String EXCEPTION_OTHER_REASON			= "EXCEPTION_OTHER_REASON";			
	// Not Given (super)
	public static final String UNSUCCESSFUL 					= "UNSUCCESSFUL";		
	public static final String UNSUCCESSFUL_PATIENT_REFUSED 	= "UNSUCCESSFUL_PATIENT_REFUSED";
	public static final String UNSUCCESSFUL_PATIENT_ABSENT 		= "UNSUCCESSFUL_PATIENT_ABSENT";
	public static final String UNSUCCESSFUL_DRUG_UNAVAILABLE 	= "UNSUCCESSFUL_DRUG_UNAVAILABLE";
	public static final String UNSUCCESSFUL_NIL_BY_MOUTH 		= "UNSUCCESSFUL_NIL_BY_MOUTH";
	public static final String UNSUCCESSFUL_OTHER_REASON		= "UNSUCCESSFUL_OTHER_REASON";

	// Planned (super)
	public static final String PLANNED = "PLANNED";		
	// Cancelled (super)
	public static final String CANCELLED = "CANCELLED";
	// Done (super)
	public static final String DONE = "DONE";
		
	public static final int MINUTE_IN_MILLISECONDS = 1000*60;
	
	public static final int ADMINISTRATION_LOW_GAP 		= 60;
	public static final int ADMINISTRATION_EARLY_GAP	= 30;
	public static final int ADMINISTRATION_LATE_GAP		= 30;
	public static final int ADMINISTRATION_HIGH_GAP 	= 60;
	
	private static final int EXECUTION_LOW_GAP 			= 90;
	private static final int EXECUTION_EARLY_GAP 		= 30;
	private static final int EXECUTION_LATE_GAP 		= 30;
	private static final int EXECUTION_HIGH_GAP 		= 90;
	
	
	public static List<Map<String, Object>> fixErogations(List<Map<String, Object>> erogationsToFix, boolean viewOnly) {
		
		List<Map<String, Object>> fixedErogations = new ArrayList<Map<String,Object>>();
			
		long userId = UserBean.instance().getCurrentEmployee().getInternalId();
		String userRoleCode =  UserBean.instance().getRole();
		Date now = new Date();
		
		int lowGap; 
		int lateGap; 
		int earlyGap; 
		int highGap;
		
		for (Map<String, Object> erogation : erogationsToFix) {
		
			if (erogation.get("quantity") != null) {
				String quantity = erogation.get("quantity").toString().replace(".", ",");
			
				if (quantity.startsWith(","))
					quantity = quantity.replaceFirst(",", "0,");
				
				erogation.put("quantity", quantity.replace(" ,", " 0,"));
			}
						
			String type = erogation.get("type").toString();
			
			if (type.equals("E")) {
				lowGap = EXECUTION_LOW_GAP;
				lateGap = EXECUTION_LATE_GAP;
				earlyGap = EXECUTION_EARLY_GAP;
				highGap = EXECUTION_HIGH_GAP;
			} else { //S
				lowGap = ADMINISTRATION_LOW_GAP;
				lateGap = ADMINISTRATION_LATE_GAP;
				earlyGap = ADMINISTRATION_EARLY_GAP;
				highGap = ADMINISTRATION_HIGH_GAP;
			}
		
			String statusCode = (String)erogation.remove("statuscode");
			String statusDetailsCode = (String)erogation.remove("statusdetailscode");
			
			Date plannedDate = (Date)erogation.get("planneddate");
			Date erogationDate = (Date)erogation.get("erogationdate");
			Date endDate = (Date)erogation.get("enddate");
			
			if (!viewOnly) {
				erogation.put("erogable", false);
				erogation.put("erogated", false);
				erogation.put("erogablefast", false);
				erogation.put("removable", false);
			}
			
			if (PLANNED.equals(statusCode)) {
				
				if (plannedDate == null) {//is as needed
					
					if (endDate == null || endDate.after(now)) {
						erogation.put("status", DUE);	
						if (!viewOnly) {
							erogation.put("erogable", true);
						}
					} else {						
						erogation.put("status", CANCELLED);
					}
					
				} else if (plannedDate.getTime() + highGap *  MINUTE_IN_MILLISECONDS < now.getTime()) {
					
					erogation.put("status", MISSED);
					if (!viewOnly) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() + lateGap * MINUTE_IN_MILLISECONDS < now.getTime()) {
					
					erogation.put("status", OVERDUE);
					if (!viewOnly) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS <= now.getTime()) {
					
					erogation.put("status", DUE);
					if (!viewOnly) {
						erogation.put("erogable", true);
						erogation.put("erogablefast", true);
					}
					
				} else if (plannedDate.getTime() - lowGap * MINUTE_IN_MILLISECONDS <= now.getTime()) {
					
					erogation.put("status", DUE_FUTURE_ENABLED);
					if (!viewOnly) {
						erogation.put("erogable", true);
					}
					
				} else if (plannedDate.getTime() - lowGap * MINUTE_IN_MILLISECONDS > now.getTime()) {
					
					erogation.put("status", DUE_FUTURE_DISABLED);
					
				}
	
			} else if (DONE.equals(statusCode)) {
				
				if (plannedDate == null) {//is as needed
					erogation.put("status", EROGATED_AS_PLANNED);
				} else if (plannedDate.getTime() + lateGap * MINUTE_IN_MILLISECONDS < erogationDate.getTime()) {
					erogation.put("status", EROGATED_LATE);
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS <= erogationDate.getTime()) {
					erogation.put("status", EROGATED_AS_PLANNED);
				} else if (plannedDate.getTime() - earlyGap * MINUTE_IN_MILLISECONDS > erogationDate.getTime()) {
					erogation.put("status", EROGATED_EARLY);
				}
				
				if (!viewOnly) {
					erogation.put("erogated", true);
				}
								
			} else if (EXCEPTION.equals(statusCode)) {
				
				if ("WDRU".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_WRONG_DRUG);
				} else if ("WDOSE".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_WRONG_DOSE);
				} else if ("OCRE".equals(statusDetailsCode)) {
					erogation.put("status", EXCEPTION_OTHER_REASON);
				} else {
					erogation.put("status", EXCEPTION);
				}
				
				if (!viewOnly) {
					erogation.put("erogated", true);
				}
				
			} else if (UNSUCCESSFUL.equals(statusCode)) {
				
				if ("DUNA".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_DRUG_UNAVAILABLE);
				} else if ("NBMP".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_NIL_BY_MOUTH);
				} else if ("PABS".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_PATIENT_ABSENT);
				} else if ("PREF".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_PATIENT_REFUSED);
				} else if ("OCRE".equals(statusDetailsCode)) {
					erogation.put("status", UNSUCCESSFUL_OTHER_REASON);
				} else {
					erogation.put("status", UNSUCCESSFUL);
				}
				
				if (!viewOnly) {
					erogation.put("erogated", true);
				}
				
			} else if (MISSED.equals(statusCode)) {
				
				erogation.put("status", MISSED);
				
				if (!viewOnly) {
					erogation.put("erogable", true);
				}
				
			} else if (CANCELLED.equals(statusCode)) {
				
				erogation.put("status", CANCELLED);				
			}
			
			if (!viewOnly) {
				Boolean erogated = (Boolean)erogation.get("erogated");
				
				Long authorId = null;
				if (erogation.get("author") != null) {
					authorId = Long.parseLong(erogation.get("author").toString());
				}
				
				if (plannedDate == null) {//is as needed
				
					if (erogated && authorId == userId && erogation.get("cancellationdate") == null) {
						erogation.put("removable", true);
					}
				} else {
					if (erogated && authorId == userId && erogation.get("cancellationdate") == null) {
						erogation.put("removable", true);
					}
				}
				
				if (erogation.get("continuous") != null && Integer.parseInt(erogation.get("continuous").toString()) == 1) {//is continuous
					if (erogation.get("erogationdate")!=null && erogation.get("erogationstopdate")==null) {
						erogation.put("removable", true);
						erogation.put("erogable", true);
						erogation.put("erogablefast", true);
					}					
				}
				
				if (type.equals("E")) {
									
					Map<String, Object> activity = (Map<String, Object>) erogation.get("parent");
					
					Number objective = (Number)erogation.get("objectiveid");
					
					// O.S.S. = 12: only executions where is responsible are erogable/erogablefast/removable
					if (!userRoleCode.equals(activity.get("responsiblerole")) && userRoleCode.equals("12")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
					
					// MEDICO = 11: executions are not erogable/erogablefast/removable
					// SEGRETARIO DI REPARTO (SOLA LETTURA) = 28: executions are not erogable/erogablefast/removable
					// INFERMIERE SOLA LETTURA = 31: executions are not erogable/erogablefast/removable
					if (userRoleCode.equals("11") || userRoleCode.equals("28") || userRoleCode.equals("31")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
					
					activity.remove("responsiblerole");			
					
					// Evaluation of an objective is not erogablefast
					if (objective != null) {
						erogation.put("erogablefast", false);
					}
				}				
				else
				{
					// SEGRETARIO DI REPARTO (SOLA LETTURA) = 28: administrations are not erogable/erogablefast/removable
					// INFERMIERE SOLA LETTURA = 31: administrations are not erogable/erogablefast/removable					
					if (userRoleCode.equals("28") || userRoleCode.equals("31")) {
						erogation.put("erogable", false);
						erogation.put("erogablefast", false);
						erogation.put("removable", false);
					}
				}
			}
			
			fixedErogations.add(erogation);
		}
		
		return fixedErogations;
	}

}