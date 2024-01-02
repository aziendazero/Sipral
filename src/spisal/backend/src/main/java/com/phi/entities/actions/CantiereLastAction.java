package com.phi.entities.actions;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Cantiere;

@BypassInterceptors
@Name("CantiereLastAction")
@Scope(ScopeType.CONVERSATION)
public class CantiereLastAction extends BaseAction<Cantiere, Long> {

	private static final long serialVersionUID = -2956258496450725327L;
	private static final Logger log = Logger.getLogger(CantiereLastAction.class);

	public static CantiereLastAction instance() {
		return (CantiereLastAction) Component.getInstance(CantiereLastAction.class, ScopeType.CONVERSATION);
	}

	public CantiereLastAction() {
		super();
		conversationName = "CantiereLast";
	}

	public void cleanImported(List<Cantiere> cantiereLastList){
		Iterator<Cantiere> iterator = cantiereLastList.iterator();
		
		while(iterator.hasNext()){
			if(((Cantiere)iterator.next()).getCreatedBy().matches("^.*Importer.*$")){
				iterator.remove();
			}
		}
	}
}