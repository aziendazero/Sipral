package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import com.phi.entities.baseEntity.PNCMessaggi;
import com.phi.entities.actions.BaseAction;
import com.phi.security.UserBean;
 
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCMessaggiAction")
@Scope(ScopeType.CONVERSATION)
public class PNCMessaggiAction extends BaseAction<PNCMessaggi, Long> {

	private static final long serialVersionUID = 590355110L;

	public static PNCMessaggiAction instance() {
		return (PNCMessaggiAction) Component.getInstance(PNCMessaggiAction.class, ScopeType.CONVERSATION);
	}

	public void preCreate(PNCMessaggi msg) throws Exception {	
		if (msg==null || msg.getInternalId()<1)
			return;
		
		msg.setModificationDate(new Date());
		msg.setModifiedBy(UserBean.instance().getUsername());
	}
	
	public void filterDates(){
		entityCriteria = ca.createCriteria(entityClass);
		
		//------------------
		String titolo = null;
		
		if (getTemporary().get("titolo")!=null)
			titolo = getTemporary().get("titolo").toString();
		
		if (titolo==null || "".equals(titolo))
			((FilterMap)getLike()).remove("titolo");
		else 
			((FilterMap)getLike()).put("titolo", titolo);
		//------------------
		
		String filter = null;

		if (getTemporary().get("filter")!=null)
			filter = getTemporary().get("filter").toString();

		if (filter!=null && !"".equals(filter)){
			LogicalExpression from = null;
			LogicalExpression to = null;
			
			Calendar cal = Calendar.getInstance();  
	        cal.setTime(new Date());  
	        cal.set(Calendar.HOUR_OF_DAY, 0);  
	        cal.set(Calendar.MINUTE, 0);  
	        cal.set(Calendar.SECOND, 0);  
	        cal.set(Calendar.MILLISECOND, 0);  
	        
	        Date toDay = cal.getTime(); 
			
			if (filter.equals("T")){
				((FilterMap)getLessEqual()).remove("fineValidita");
			
			} else if (filter.equals("S")){
				((FilterMap)getLess()).put("fineValidita", toDay);
		
			} else if (filter.equals("V")){
				from = Restrictions.or(Restrictions.isNull("inizioValidita"), Restrictions.le("inizioValidita", toDay));
				to   = Restrictions.or(Restrictions.isNull("fineValidita"), Restrictions.ge("fineValidita", toDay));

				entityCriteria.add(Restrictions.and(from, to));
			}
		}
		
	}
	
	public List<SelectItem> getFilters() {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			
			selItem.add(new SelectItem("V", "Validi"));
			selItem.add(new SelectItem("S", "Scaduti"));
			selItem.add(new SelectItem("T", "Tutti"));
			
			return selItem;
	}
	
}