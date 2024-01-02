package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Contact;

@BypassInterceptors
@Name("ContactAction")
@Scope(ScopeType.CONVERSATION)
public class ContactAction extends BaseAction<Contact, Long> {

	private static final long serialVersionUID = 2061644090L;

	public static ContactAction instance() {
		return (ContactAction) Component.getInstance(ContactAction.class, ScopeType.CONVERSATION);
	}

	public List<Contact> filterOnlyReference(List<Contact> lst){
		
		List<Contact> out = new ArrayList<Contact>();

		if(lst!=null && !lst.isEmpty()){
			for(Contact cnt : lst){
				if(cnt.getPersonReference()==true){
					out.add(cnt);
				}
			}
		}
		
		return out;
	}

}