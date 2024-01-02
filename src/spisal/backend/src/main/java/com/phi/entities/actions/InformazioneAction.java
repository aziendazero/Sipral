package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.Informazione;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("InformazioneAction")
@Scope(ScopeType.CONVERSATION)
public class InformazioneAction extends BaseAction<Informazione, Long> {

	private static final long serialVersionUID = 516608401L;

	public static InformazioneAction instance() {
		return (InformazioneAction) Component.getInstance(InformazioneAction.class, ScopeType.CONVERSATION);
	}
	
	public void injectArgomentiLegge81(){
		Informazione fr = getEntity();
		if(fr != null){
			FunctionsBean function = FunctionsBean.instance();
			List codesList = fr.getArgomentiLegge81();
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
		Informazione fr = getEntity();
		if(fr == null)
			return;

			if(codeIds!=null && !codeIds.isEmpty()){
				List<CodeValueLaw> l81List = new ArrayList<CodeValueLaw>();
				for(String id : codeIds){
					CodeValueLaw cv = ca.get(CodeValueLaw.class, id);
					l81List.add(cv);
				}
				fr.setArgomentiLegge81(l81List);
			}else{
				fr.setArgomentiLegge81(null);
			}
	}



}