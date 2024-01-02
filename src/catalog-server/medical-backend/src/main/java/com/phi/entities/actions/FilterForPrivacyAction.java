package com.phi.entities.actions;

import java.io.Serializable;

import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.Name;

import com.phi.cs.repository.RepositoryManager;

@Name("FilterForPrivacyAction")

public class FilterForPrivacyAction<T, ID extends Serializable> extends BaseAction<T, ID> {
	
	private static final long serialVersionUID = 601730486775315824L;
	
	/**
	 * Add a entityCriteria to respect privacy restrictions
	 * @param date
	 */
	public void filterForPrivacy(String date){
		if (Boolean.parseBoolean(RepositoryManager.instance().getSeamProperty("filterForPrivacy"))){			
			if(entityCriteria == null) {
				entityCriteria = ca.createCriteria(entityClass);
			}
			if (TransferPrivacy.instance().getFilterDate()!= null){				
				entityCriteria.add(Restrictions.between(date, TransferPrivacy.instance().getFilterDate().getLow(), TransferPrivacy.instance().getFilterDate().getHigh()));
			}
		}
	}

}