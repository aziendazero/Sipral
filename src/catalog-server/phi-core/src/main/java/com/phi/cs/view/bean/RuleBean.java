package com.phi.cs.view.bean;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;

import com.phi.cs.exception.PhiException;
import com.phi.cs.view.banner.Banner;

/**
 * This bean is used to allow rim objects and attributes to be inserted in businesss rules.
 * It is called by rules in this way: ruleBean.isRight("PatientEncounter.priorityCode.code","UR")
 * method RuleBean.isRight(...) resolves expression language el
 * and checked against the given value (in the above example that would be "UR" returning true or false
 * 
 * @author mfrossi
 *
 */
@BypassInterceptors
@Name("ruleBean")
@Scope(ScopeType.EVENT)
public class RuleBean implements Serializable {

	private static final long serialVersionUID = 2634023088486384569L;
//	private final static Logger log = Logger.getLogger(RuleBean.class);

	public RuleBean(){
		//conversationContext  = Contexts.getConversationContext();
	}
	
//	@Create
//	public void init() {
//		if (log.isDebugEnabled()) log.debug("RuleBean init ..");
//	}
	/**
	 * Get value from RIM datagraph and compare it with the value expected</br>
	 * 
	 * @param el expression language
	 * @param value value to compare
	 * 
	 * @return true or false if the value expected is equal to the real one
	 * 
	 * @throws PhiException
	 */
	
	/**
	 * 

	 * @return
	 * @throws PhiException
	 */
	public Boolean isRight(String el, Object value) throws PhiException{
        if (el == null) {
            return null;
        }

        Expressions.ValueExpression<Object> vEl = Expressions.instance().createValueExpression(el);
        
        Object valueEl = vEl.getValue();
        
        if (value == valueEl) {
        	return true;
        } else {
        	return false;
        }
	}
	
	public static Boolean isInConv(String input) throws PhiException{
		
		Object entity = Contexts.getConversationContext().get(input);
		
		if (entity == null) {
			entity = Banner.instance().getEntity(input);
		}
		
		if (entity != null) {
        	return true;
        } else {
        	return false;
        }
	}
	
	
}
