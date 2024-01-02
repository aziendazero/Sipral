package com.phi.entities.actions;

import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.entities.baseEntity.QuestTabella;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("QuestTabellaAction")
@Scope(ScopeType.CONVERSATION)
public class QuestTabellaAction extends BaseAction<QuestTabella, Long> {

	private static final long serialVersionUID = 588824175L;

	public static QuestTabellaAction instance() {
		return (QuestTabellaAction) Component.getInstance(QuestTabellaAction.class, ScopeType.CONVERSATION);
	}

	public void rinumeraRighe(QuestTabella riga){
		String question = riga.getQuestion();
		
		Context conversationContext = Contexts.getConversationContext();
		IdataModel<QuestTabella> conversationList = (IdataModel<QuestTabella>) conversationContext.get("Tabella"+question+"List");
		List<QuestTabella> tabella = conversationList.getList();
		tabella.remove(riga);
		
		for(QuestTabella row : tabella){
			if(row.getNumProgr() < riga.getNumProgr())
				continue;
			
			row.setNumProgr(row.getNumProgr()-1);
		}
	}
	
	public void setPropertyNullRiga(QuestTabella riga, String... properties){
		
		inject(riga);
		
		for(String property : properties){
			setPropertyNull(property, false);
		}
		
		eject();
	}
	
	public List<SelectItem> reverseSelectItem(List<SelectItem> itemsList){
		Collections.reverse(itemsList);
		return itemsList;
	}

}