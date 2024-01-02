package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.phi.entities.baseEntity.ConclusioniMdl;
import com.phi.entities.baseEntity.DiagMdl;

import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ConclusioniMdlAction")
@Scope(ScopeType.CONVERSATION)
public class ConclusioniMdlAction extends BaseAction<ConclusioniMdl, Long> {

	private static final long serialVersionUID = 1010809149L;

	public static ConclusioniMdlAction instance() {
		return (ConclusioniMdlAction) Component.getInstance(ConclusioniMdlAction.class, ScopeType.CONVERSATION);
	}
	
	public void initList(){
		DiagMdlAction diagMdlAction = DiagMdlAction.instance();
		diagMdlAction.injectList(new ArrayList<DiagMdl>());
	}
	
	public static Date addDays(Date date, Integer days) {
		if (date==null)
			return null;
		
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        
        return cal.getTime();
    }

}