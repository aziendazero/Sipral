package com.phi.entities.actions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.TipologiaCantiere;

@BypassInterceptors
@Name("TipologiaCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class TipologiaCantiereAction extends BaseAction<TipologiaCantiere, Long> {

	private static final long serialVersionUID = 594155838L;
	
	private Query q1;
	private HashMap<Long, Boolean> delCache;

	public static TipologiaCantiereAction instance() {
		return (TipologiaCantiereAction) Component.getInstance(TipologiaCantiereAction.class, ScopeType.CONVERSATION);
	}
	
	@Create
	public void init(){
		/*
		 * controllo i tag: non devo poter modificare i dati se la tipologia figura come un tag
		 * CACHE DEM QUERY
		 */
		String qry1 = "SELECT COUNT(tag) FROM TagCantiere tag " +
				"JOIN tag.tipologiaCantiere type " +				
				"WHERE type.internalId = :id AND (tag.isActive = 1)";
		
		q1 = ca.createQuery(qry1);

		delCache = new HashMap<Long, Boolean>();
	}

	/**
	 * Controlla se si può eliminare una tipologia cantiere.
	 * Controllo i tag: non devo poter eliminare o modificare i dati se la tipologia figura come un tag
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Boolean isDeletable(Object type) throws PhiException {

		Long id=null;
		if(type instanceof TipologiaCantiere){
			id = ((TipologiaCantiere)type).getInternalId();
		}else if(type instanceof Map){
			FunctionsBean fun = FunctionsBean.instance();
			id = (Long)fun.resolveMapProperty((Map)type, "internalId");
		}else{
			return false;
		}

		if(delCache.containsKey(id))
			return delCache.get(id);

		Object count = null;

		if(id!=null){
			q1.setParameter("id", id);
			count = q1.getSingleResult();
			if(count instanceof Long && (Long)count > 0){
				delCache.put(id, false);
				return false;
			}
		}
		
		delCache.put(id, true);

		return true;
	}

	public void filterDates(){
		Date fromDate = (Date)getTemporary().get("fromDate");
		Date toDate = (Date)getTemporary().get("toDate");
		
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		LogicalExpression from = null;
		if(fromDate!=null)
			from = Restrictions.or(Restrictions.ge("startValidity", fromDate), Restrictions.ge("endValidity", fromDate));
		
		LogicalExpression to = null;
		if(toDate!=null)
			to = Restrictions.or(Restrictions.le("startValidity", toDate), Restrictions.le("endValidity", toDate));
		
		if(from!=null && to!=null)
			entityCriteria.add(Restrictions.or(from, to));
		else if(from!=null)
			entityCriteria.add(from);
		else if(to!=null)
			entityCriteria.add(to);
	}

}