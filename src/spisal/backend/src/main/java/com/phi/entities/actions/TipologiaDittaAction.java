package com.phi.entities.actions;

import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;

import com.phi.entities.baseEntity.TipologiaDitta;

@BypassInterceptors
@Name("TipologiaDittaAction")
@Scope(ScopeType.CONVERSATION)
public class TipologiaDittaAction extends BaseAction<TipologiaDitta, Long> {

	private static final long serialVersionUID = 683047351L;
	private Query q1;
	
	public static TipologiaDittaAction instance() {
		return (TipologiaDittaAction) Component.getInstance(TipologiaDittaAction.class, ScopeType.CONVERSATION);
	}
	
	@Create
	public void init(){

		String qry1 = "SELECT COUNT(tag) FROM TagDitta tag " +
				"JOIN tag.tipologiaDitta type " +		//se c'è almeno un tag non posso eliminare
				"WHERE type.internalId = :id ";

		q1 = ca.createQuery(qry1);
	}

	public Boolean isDeletable(Object obj) throws PhiException {

		TipologiaDitta type = null;

		Long id;
		if(obj instanceof TipologiaDitta){
			type = (TipologiaDitta)obj;
			id = type.getInternalId();
		} else if(obj instanceof Map){
			id = (Long)((Map)obj).get("internalId");

		}else{
			return false;
		}

		Object count = null;

		//controllo le liste in domanda e i tipi istanza
		q1.setParameter("id", id);
		count = q1.getSingleResult();
		if(count instanceof Long && (Long)count > 0){
			
			return false;
		}

		
		return true;
	}
}