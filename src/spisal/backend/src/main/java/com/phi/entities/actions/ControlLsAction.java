package com.phi.entities.actions;

import java.util.ArrayList;
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
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.ControlLs;
import com.phi.entities.baseEntity.ControlSubLs;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ControlLsAction")
@Scope(ScopeType.CONVERSATION)
public class ControlLsAction extends BaseAction<ControlLs, Long> {

	private static final long serialVersionUID = 1834043399L;

	private Query q1;
	private HashMap<Long, Boolean> delCache;

	@Create
	public void init(){

		String qry1 = "SELECT COUNT(lst) FROM ControlLs lst " +
				"LEFT JOIN lst.controlLsReq lreq " +		//controllo le liste in domanda
				"WHERE lst.internalId = :id AND lreq.isActive = 1 ";

		q1 = ca.createQuery(qry1);

		delCache = new HashMap<Long, Boolean>();
	}

	public static ControlLsAction instance() {
		return (ControlLsAction) Component.getInstance(ControlLsAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Perform delete of ControlLs on cascade
	 * Setta isActive=false per la ControlLs e tutte le sottoliste e i requisiti associati.
	 * Per scrupolo vengono lasciati i link.
	 * @param clist
	 */
	public void deleteOnCascade(ControlLs clist){
		if(clist==null)
			return;

		clist.setIsActive(false);

		if(clist.getControlSubLs()!=null && !clist.getControlSubLs().isEmpty()){
			ControlSubLsAction slistAction = ControlSubLsAction.instance();

			for(ControlSubLs slist : clist.getControlSubLs()){
				slistAction.deleteOnCascade(slist);
			}
		}
	}

	/**
	 * Checks if it is possible to delete a ControlLs:
	 * it is possible to delete a ControlLs
	 * when there are no further data attached besides 
	 * This method is used to disable delete button in f_control_list_search form
	 * 
	 * @return
	 */
	public Boolean isDeletable(Object obj) throws PhiException {

		ControlLs clist = null;

		Long id;
		if(obj instanceof ControlLs){
			clist = (ControlLs)obj;
			id = clist.getInternalId();
		} else if(obj instanceof Map){
			id = (Long)((Map)obj).get("internalId");
			clist = (ControlLs)ca.get(entityClass, id);

		}else{
			return false;
		}

		if(delCache.containsKey(id))
			return delCache.get(id);

		Object count = null;

		//controllo le liste in domanda e i tipi istanza
		q1.setParameter("id", id);
		count = q1.getSingleResult();
		if(count instanceof Long && (Long)count > 0){
			delCache.put(id, false);
			return false;
		}

		//controllo le sottoliste di controllo - se sono tutte eliminabili posso procedere con l'eliminazione in cascata
		if(clist.getControlSubLs()!=null && !clist.getControlSubLs().isEmpty()){

			ControlSubLsAction slistAction = ControlSubLsAction.instance();

			for(ControlSubLs slist : clist.getControlSubLs()){
				if(!slistAction.isDeletable(slist)){
					delCache.put(id, false);
					return false;

				}
			}
		}

		delCache.put(id, true);
		return true;
	}
	
	/**
	 * Checks if it is possible to unlink a ControlLs from a TipoIstanza:
	 * it is possible to delete a ControlLs
	 * 
	 * @return
	 */
	public Boolean isUnlinkable(Object obj) throws PhiException {

		return isDeletable(obj);
	}

	public void injectSupervisionCode(String code){
		ControlLs ls = getEntity();
		if(code!=null && "SUPERVISION".equals(code) && ls!=null){
			FunctionsBean function = FunctionsBean.instance();
			List codesList = ls.getSupervisionCode();
			try {
				Contexts.getConversationContext().set("CodeValueList", function.propertyAsList(codesList, "id"));
			} catch (Exception e) {
				Contexts.getConversationContext().remove("CodeValueList");
			} 
		}else{
			Contexts.getConversationContext().remove("CodeValueList");
		}
	}
	
	public void setSupervisionCode(List<String> codeIds){
		ControlLs controlLs = getEntity();
		if(controlLs==null)
			return;
		
		CodeValue area = (CodeValue)getTemporary().get("area");
		if(area!=null && "SUPERVISION".equals(area.getCode())){
			if(codeIds!=null && !codeIds.isEmpty()){
				List<CodeValuePhi> supCodes = new ArrayList<CodeValuePhi>();
				for(String id : codeIds){ 
					CodeValuePhi cv = ca.get(CodeValuePhi.class, id);
					supCodes.add(cv);
				}
				controlLs.setSupervisionCode(supCodes);
			}else{
				controlLs.setSupervisionCode(null);
			}
		}
	}
}