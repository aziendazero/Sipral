package com.phi.rest.action;

import org.hibernate.Criteria;

import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;

public class RestAction extends BaseAction<BaseEntity, Long> {

	private static final long serialVersionUID = 6877737459283459984L;

	public RestAction(Class entityClass) {
		this.entityClass = entityClass;
		conversationName = entityClass.getSimpleName();
	}

	public Criteria getEntityCriteria() {
		if (entityCriteria == null)
			ca.createCriteria(entityClass);
		return entityCriteria;
	}

}