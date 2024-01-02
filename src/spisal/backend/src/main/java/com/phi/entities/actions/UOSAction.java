package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("UOSAction")
@Scope(ScopeType.CONVERSATION)
public class UOSAction extends BaseAction<ServiceDeliveryLocation, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7361414695480374202L;
	
    public static UOSAction instance() {
        return (UOSAction) Component.getInstance(UOSAction.class, ScopeType.CONVERSATION);
    }

	public UOSAction() {
		super();
		conversationName = "UOS";
	}
public List<ServiceDeliveryLocation> filterOnlyActive(List<ServiceDeliveryLocation> lst){
		
		List<ServiceDeliveryLocation> out = new ArrayList<ServiceDeliveryLocation>();

		if(lst!=null && !lst.isEmpty()){
			for(ServiceDeliveryLocation cnt : lst){
				if(cnt.getIsActive()==true){
					if(cnt.getParent().getIsActive()==true){
					out.add(cnt);
					}
				}
			}
		}
		
		return out;
	}
}