package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.AgendaConf;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("AgendaConfAction")
@Scope(ScopeType.CONVERSATION)
public class AgendaConfAction extends BaseAction<AgendaConf, Long> {

	private static final long serialVersionUID = 1275468540L;

	public static AgendaConfAction instance() {
		return (AgendaConfAction) Component.getInstance(AgendaConfAction.class, ScopeType.CONVERSATION);
	}

	public void readUos() throws PhiException {
		
		ServiceDeliveryLocationAction sdfa = ServiceDeliveryLocationAction.instance();
		sdfa.equal.put("code.code", "UOS");
		sdfa.equal.put("isActive", true);
		sdfa.equal.put("parent.isActive", true);
		List<ServiceDeliveryLocation> linee = sdfa.list();
		getTemporary().put("linee", linee);

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		
		for (ServiceDeliveryLocation sdl : linee) {
			String parentName = sdl.getParent().getName().getGiv();
			String name = sdl.getName().getGiv();
			SelectItem selItem = new SelectItem(sdl, name+ " ("+parentName+")");
			selectItems.add(selItem);
		}
		
		getTemporary().put("lineeSelectItems", selectItems);
		
	}
	
	public List<SelectItem> slots() {
		Integer [] slots= {30,60,90,120 };
		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		for (Integer i : slots) {
			SelectItem selItem = new SelectItem(i, i+"'");
			selectItems.add(selItem);
		}

		return selectItems;
	}
	
	
	
	
}