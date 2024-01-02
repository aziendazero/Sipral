package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.Lifestyle;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("LifestyleAction")
@Scope(ScopeType.CONVERSATION)
public class LifestyleAction extends BaseAction<Lifestyle, Long> {

	private static final long serialVersionUID = 1061228977L;

	public static LifestyleAction instance() {
		return (LifestyleAction) Component.getInstance(LifestyleAction.class, ScopeType.CONVERSATION);
	}

	public void injectArgomentiLegge81(){
		Lifestyle ls = getEntity();
		if(ls != null){
			FunctionsBean function = FunctionsBean.instance();
			List codesList = ls.getArgomentiLegge81();
			try {
				Contexts.getConversationContext().set("CodeValueList", function.propertyAsList(codesList, "id"));
			} catch (Exception e) {
				Contexts.getConversationContext().remove("CodeValueList");
			} 
		}else{
			Contexts.getConversationContext().remove("CodeValueList");
		}
	}
	
	public void setArgomentiLegge81(List<String> codeIds){
		Lifestyle ls = getEntity();
		if(ls == null)
			return;

			if(codeIds!=null && !codeIds.isEmpty()){
				List<CodeValueLaw> l81List = new ArrayList<CodeValueLaw>();
				for(String id : codeIds){
					CodeValueLaw cv = ca.get(CodeValueLaw.class, id);
					l81List.add(cv);
				}
				ls.setArgomentiLegge81(l81List);
			}else{
				ls.setArgomentiLegge81(null);
			}
	}

}