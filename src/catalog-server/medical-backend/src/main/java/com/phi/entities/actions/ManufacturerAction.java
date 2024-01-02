package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.ApplicationException;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.Manufacturer;

@BypassInterceptors
@Name("ManufacturerAction")
@Scope(ScopeType.CONVERSATION)

public class ManufacturerAction extends BaseAction<Manufacturer, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static ManufacturerAction instance() {
        return (ManufacturerAction) Component.getInstance(ManufacturerAction.class, ScopeType.CONVERSATION);
    }

    
    
    @SuppressWarnings("unchecked")
	public List<SelectItem> selectionItemtManufacturer () throws PersistenceException, ApplicationException {
    	
    	List<SelectItem> selectItems =  new ArrayList<SelectItem>();
//    	List<Manufacturer> mfs = read().getList();
//    	
//    	for (Manufacturer mf : mfs) {
//			if (mf.getName() != null) {
//				SelectItem selItem = new SelectItem(mf.getId() , mf.getName().getGiv());
//				selectItems.add(selItem);
//			}
//    	}
	
    	List<String[]> results = ca.executeHQL("select manufacturer.internalId, manufacturer.name.giv from Manufacturer manufacturer ");
    	Iterator<String[]> it = results.iterator();
    	
    	while (it.hasNext()) {
    		
    		String[] result = it.next();
    		SelectItem selItem = new SelectItem(result[0] , result[1]);
    		selectItems.add(selItem);
    	}
    	return selectItems;	
		
    }
    
}