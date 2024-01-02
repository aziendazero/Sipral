package com.phi.entities.actions;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.ControlSubLs;
import com.phi.entities.baseEntity.Requisito;

@BypassInterceptors
@Name("ControlSubLsAction")
@Scope(ScopeType.CONVERSATION)
public class ControlSubLsAction extends BaseAction<ControlSubLs, Long> {

	private static final long serialVersionUID = 1833869586L;

	private Query q1;
	private HashMap<Long, Boolean> delCache;

	@Create
	public void init(){

		String qry1 = "SELECT COUNT(lst) FROM ControlSubLs lst " +
				"JOIN lst.controlSubLsReq lreq " +		//controllo le sottoliste in domanda
				"WHERE lst.internalId = :id AND lreq.isActive = 1";

		q1 = ca.createQuery(qry1);
		
		delCache = new HashMap<Long, Boolean>();
	}

	public static ControlSubLsAction instance() {
		return (ControlSubLsAction) Component.getInstance(ControlSubLsAction.class, ScopeType.CONVERSATION);
	}

	public List<ControlSubLs> orderList(List<ControlSubLs> list){
		Collections.sort(list, new Comparator<ControlSubLs>() {

			@Override
			public int compare(ControlSubLs l1, ControlSubLs l2) {

				int result = 0;

				String p1 = null;
				String p2 = null;

				if (l1 == null && l2 == null) {
					return result;
				} else if (l1 == null) {
					result = -1;
				} else if (l2 == null) {
					result = 1;
				} else {
					p1 = l1.getDescrCode();
					p2 = l2.getDescrCode();

					if (p1 == null && p2 == null) {
						result = 0;
					}  else if (p1 == null) {
						result = -1;
					} else if (p2 == null) {
						result = 1;
					}  else {
						result = p1.compareTo(p2);
					}
				}
				return result;
			}
		});

		return list;
	}

	public void deleteOnCascade(ControlSubLs slist){
		if(slist==null)
			return;

		slist.setIsActive(false);

		if(slist.getRequisito()!=null && !slist.getRequisito().isEmpty()){
			for(Requisito req : slist.getRequisito()){
				req.setIsActive(false);
			}
		}
	}

	/**
	 * Checks if it is possible to delete a ControlSubLs:
	 * it is possible to delete a ControlSubLs
	 * when there are no further data attached besides 
	 * This method is used to disable delete button in f_control_sublist_search form
	 * 
	 * @return
	 */
	public Boolean isDeletable(Object obj) throws PhiException {

		ControlSubLs slist = null;
		Long id;
		if(obj instanceof ControlSubLs){
			slist = (ControlSubLs)obj;
			id = slist.getInternalId();
		} else if(obj instanceof Map){
			id = (Long)((Map)obj).get("internalId");
			slist = (ControlSubLs)ca.get(entityClass, id);

		}else{
			return false;
		}


		if(delCache.containsKey(id))
			return delCache.get(id);

		Object count = null;

		//controllo le funzioni strutturali
		q1.setParameter("id", id);
		count = q1.getSingleResult();
		if(count instanceof Long && (Long)count > 0){
			delCache.put(id, false);
			return false;
		}

		//controllo i requisiti
		if(slist.getRequisito()!=null && !slist.getRequisito().isEmpty()){

			RequisitoAction reqAction = RequisitoAction.instance();

			for(Requisito req : slist.getRequisito()){
				if(!reqAction.isDeletable(req)){
					delCache.put(id, false);
					return false;
				}
			}
		}

		delCache.put(id, true);
		return true;
	}

	/**
	 * Checks if it is possible to unlink a ControlSubLs:
	 * it is possible to unlink a ControlSubLs
	 * when there are no further data attached besides 
	 * Similar to isDeletable, but does not look upwards
	 * 
	 * @return
	 */
	public Boolean isUnlinkable(Object obj) throws PhiException {
		return isUnlinkable(obj);
	}
}