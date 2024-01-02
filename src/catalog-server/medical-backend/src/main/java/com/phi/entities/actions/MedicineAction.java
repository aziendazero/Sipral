package com.phi.entities.actions;

import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Medicine;

@BypassInterceptors
@Name("MedicineAction")
@Scope(ScopeType.CONVERSATION)
public class MedicineAction extends BaseAction<Medicine, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static MedicineAction instance() {
        return (MedicineAction) Component.getInstance(MedicineAction.class, ScopeType.CONVERSATION);
    }
	
    public boolean isPrescrivible(Date expirationDate){
		if(expirationDate==null)
			return true;
		
		Date now = new Date();
		return now.before(expirationDate);
	}

}