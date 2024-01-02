package com.phi.entities.actions;

import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import com.phi.cs.view.banner.Banner;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.act.Allergy;
import com.phi.events.PhiEvent;

@BypassInterceptors
@Name("AllergyAction")
@Scope(ScopeType.CONVERSATION)
public class AllergyAction extends BaseAction<Allergy, Long> {


	private static final long serialVersionUID = 5187230971001999697L;

	public static AllergyAction instance() {
		return (AllergyAction) Component.getInstance(AllergyAction.class, ScopeType.CONVERSATION);
	}

	private static final String QRY_ALLERGIES = "SELECT a.isNoAllergy, a.allergyGeneric, drug.displayName FROM Allergy a " +
			"LEFT JOIN a.drugAllergenCode drug " +
			"WHERE a.isActive = true AND a.confirmed = true AND a.patient.internalId = :patientId";
	
	/**
	 * Called when Patient is injected
	 * 
	 * @param patientId Patient.internalId
	 */	
	@Observer(PatientAction.eventInjectIntoSession)
	public void patientInjected(Long patientId) {

		Banner banner = Banner.instance();
		
		banner.put("genericAllergy", null);
		banner.put("drugAllergy", null);
		banner.put("allergyToolTip", null);
		banner.put("allergyIcon", null);
		
		FunctionsBean fb = FunctionsBean.instance();
		StringBuffer genericSB = new StringBuffer();
		StringBuffer medicineSB = new StringBuffer();
		
		Query qry = ca.createQuery(QRY_ALLERGIES);
		qry.setParameter("patientId", patientId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = qry.getResultList();
				
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());
				
		// patient has at least one active and confirmed allergy to a medicine
		boolean hasPharmaAllergy = false;
		// patient has declared that has no allergies
		boolean hasDeclaredNoAllergy = false;
		
		
		for (Object[] allergy : list ) {
			Boolean noAllergy = (Boolean)allergy[0];
			String allergyGeneric = (String)allergy[1];
			String drugAllergenCode = (String)allergy[2];
			
			if (noAllergy) {
				hasDeclaredNoAllergy = true;
			}
			
			if (allergyGeneric != null && !allergyGeneric.equals("")) {
				genericSB.append(allergyGeneric).append(". ");
			}
			
			if (drugAllergenCode != null) {
				medicineSB.append(drugAllergenCode).append(". ");
				hasPharmaAllergy = true;
			}
		}

		if (list.isEmpty()) { //No allergy registered
			banner.put("genericAllergy", null);
			banner.put("drugAllergy", null);
			banner.put("allergyToolTip", "Nessuna allergia registrata");
			banner.put("allergyIcon", "images/noAllergy_icon.gif");
			
		} else { 
			
			if (hasDeclaredNoAllergy) { //No allergy declared
		
				banner.put("allergyToolTip", fb.getStaticTranslation("Label_NoAllergy"));
				
				banner.put("allergyIcon", "images/noAllergy_icon.gif");
			
			} else {// Allergy declared Tooltip_Drug_Allergy

			if (!genericSB.toString().equals("") && !medicineSB.toString().equals("")) {
				banner.put("allergyToolTip", fb.getStaticTranslation("Tooltip_Generic_Allergy") + ": " + genericSB.toString() +   fb.getStaticTranslation("Tooltip_Drug_Allergy") + ": " + medicineSB.toString());
				banner.put("genericAllergy", genericSB.toString());
				banner.put("drugAllergy", medicineSB.toString());
			} else if(genericSB.toString().equals("") &&  !medicineSB.toString().equals("")){
				banner.put("allergyToolTip", fb.getStaticTranslation("Tooltip_Drug_Allergy") + ": " + medicineSB.toString());
				banner.put("drugAllergy", medicineSB.toString());
			} else { 
				banner.put("allergyToolTip",  fb.getStaticTranslation("Tooltip_Generic_Allergy") + ": " + genericSB.toString());
				banner.put("genericAllergy", genericSB.toString());
			}
			
				if (hasPharmaAllergy) {
					banner.put("allergyIcon", "images/allergy_icon.gif");
				} else {
					banner.put("allergyIcon", "images/allergy_gen_icon.gif");
				}
			}
		}
	}
	
	/**
	 * Called when Patient is ejected
	 */	
	@Observer(PatientAction.eventEjectFromSession)
	public void patientEjected(/*Long patientId*/) {
		
		Banner banner = Banner.instance();
		
		banner.remove("allergyToolTip");
		banner.remove("allergyIcon");

	}

	public static String ExportList(List<Allergy> AllergyList) {
		String ret = "";
		if (AllergyList != null && AllergyList.size() >0 ){
			for (Allergy allergy : AllergyList) {
				if(allergy!=null) {
					if(allergy.getIsActive() && allergy.getConfirmed()){
						if (allergy.getAllergyGeneric() != null && !allergy.getAllergyGeneric().equals("")) {
							ret += allergy.getAllergyGeneric() + ",";
						}
						if (allergy.getDrugAllergenCode()!=null) {
							ret += allergy.getDrugAllergenCode() + ",";
						}
						if (allergy.getIsNoAllergy() != null && allergy.getIsNoAllergy() == true){
							ret += allergy.getNote();
						}
					}	
				}	
			}
		}
		if (!ret.isEmpty() && ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}
	
	public void saveNoAllergyAsNote(){
		Allergy allergy = getEntity();
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();
		if (allergy.getNote() != null){
			allergy.setNote(((String)app.evaluateExpressionGet(fc, "${static.Label_NoAllergy}", String.class)).concat(". ").concat(allergy.getNote()));	
		}
		else allergy.setNote(((String)app.evaluateExpressionGet(fc, "${static.Label_NoAllergy}", String.class)).concat(". "));	
	}

}