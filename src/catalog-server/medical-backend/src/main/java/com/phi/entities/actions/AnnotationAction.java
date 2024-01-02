package com.phi.entities.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.act.Annotation;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.role.Patient;

@BypassInterceptors
@Name("AnnotationAction")
@Scope(ScopeType.CONVERSATION)
public class AnnotationAction extends BaseAction<Annotation, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static AnnotationAction instance() {
        return (AnnotationAction) Component.getInstance(AnnotationAction.class, ScopeType.CONVERSATION);
    }
	
	/**
* From PHI CI
*/


	
    private static final String creationDate ="availabilityTime";
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	
	public void filterDate() throws PersistenceException,ParseException  {
		Date date;
		FunctionsBean f=new FunctionsBean(); 
		String lessDate=null;
		HashMap<String, Object> whereParamsGreaterCreationDate=this.getLessEqual();
		Object ob1=whereParamsGreaterCreationDate.get(creationDate);
		
		if(ob1!=null){
			if (ob1 instanceof Date) {
				lessDate=f.formatDate(ob1,DEFAULT_DATE_PATTERN, Locale.getDefault());
				lessDate+=" 23:59";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				date=sdf.parse(lessDate);
				this.addCriteria("le", "availabilityTime", date);
			}
		}		
	}

	/**
	 * Filter by patient or encounter for VCO retrocompatibility
	 * @param patient
	 * @param patientEncounter
	 */
	@ShowInDesigner(description="Filter by patient or encounter for VCO retrocompatibility")
	public void restrictEncounterOrPatient(/*Patient patient, PatientEncounter patientEncounter*/){
		//FIXME: use this:
		//this.entityCriteria.add(Restrictions.or(Restrictions.eq("patient", patient), Restrictions.eq("patientEncounter", patientEncounter)));
		
		Context conversationContext = Contexts.getConversationContext();
		this.entityCriteria.add(Restrictions.or(Restrictions.eq("patient", (Patient)conversationContext.get("Patient")), Restrictions.eq("patientEncounter", (PatientEncounter)conversationContext.get("PatientEncounter"))));

	}

}