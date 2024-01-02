package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.Requisito;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("RequisitoAction")
@Scope(ScopeType.CONVERSATION)
public class RequisitoAction extends BaseAction<Requisito, Long> {

	private static final long serialVersionUID = 1834443471L;
	
	private Query q1;
	private HashMap<Long, Boolean> delCache;

	@Create
	public void init(){
		
		String qry1 = "SELECT COUNT(req) FROM Requisito req " +
				"INNER JOIN req.risposta risp " +		//controllo le risposte al requisito
				"WHERE req.internalId = :id AND risp.isActive = 1";


		q1 = ca.createQuery(qry1);

		delCache = new HashMap<Long, Boolean>();
	}

	public static RequisitoAction instance() {
		return (RequisitoAction) Component.getInstance(RequisitoAction.class, ScopeType.CONVERSATION);
	}

	public List<Requisito> orderList(List<Requisito> list){
		Collections.sort(list, new Comparator<Requisito>() {

			@Override
			public int compare(Requisito r1, Requisito r2) {

				int result = 0;

				Integer p1 = null;
				Integer p2 = null;

				if (r1 == null && r2 == null) {
					return result;
				} else if (r1 == null) {
					result = -1;
				} else if (r2 == null) {
					result = 1;
				} else {
					p1 = r1.getOrdering();
					p2 = r2.getOrdering();

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

	public Integer getRequisitoColSpan(Requisito req){
		Integer num = 1;
		if(req!=null && req.getValAdmitted()!=null && req.getValAdmitted().size()>0){
			num = 12/req.getValAdmitted().size();
		}
		return num;
	}

	/**
	 * Checks if it is possible to delete a Requisito:
	 * it is possible to delete a Requisito
	 * when there are no further data attached besides 
	 * This method is used to disable delete button in f_control_list_search form
	 * 
	 * @return
	 */
	public Boolean isDeletable(Object obj) throws PhiException {

		Requisito req = null;
		Long id;
		if(obj instanceof Requisito){
			req = (Requisito)obj;
			id = req.getInternalId();
		} else if(obj instanceof Map){
			id = (Long)((Map)obj).get("internalId");
			//req = (Requisito)ca.get(entityClass, id);
		
		}else{
			return false;
		}
		

		if(delCache.containsKey(id))
			return delCache.get(id);

		Object count = null;

		//controllo le liste in domanda
		q1.setParameter("id", id);
		count = q1.getSingleResult();
		if(count instanceof Long && (Long)count > 0){
			delCache.put(id, false);
			return false;
		}

		delCache.put(id, true);
		return true;
	}

	public String getValAdmitted(Requisito req){
		if(req!=null && req.getValAdmitted()!=null && !req.getValAdmitted().isEmpty()){
			String rtn = "";
			for(CodeValuePhi cv : req.getValAdmitted()){
				rtn+=cv.getCode()+" ";
			}
			
			return rtn.substring(0, rtn.length()-1);
		}else{
			return "";
		}
	}

	/**
	 * Ritorna gli indicatori del requisito - usato nella reportistica e storico
	 * @param req
	 * @return
	 */
	public String getIndicators(Requisito req){
		String rtn = "";
		FunctionsBean fn = FunctionsBean.instance();
		if(req!=null){
			rtn += fn.getStaticTranslation("Requisito_var") + ": " + getYesNo(req.getReqVar()) + ", ";
			rtn += fn.getStaticTranslation("Requisito_vis") + ": " + getYesNo(req.getReqVis()) + ", ";
		}

		return rtn;
	}

	public String getYesNo(Boolean b){
		FunctionsBean fn = FunctionsBean.instance();
		if(b)
			return fn.getStaticTranslation("LabelGenericYes");
		else
			return fn.getStaticTranslation("LabelGenericNo");
	}


	
	public Integer getColumnNumber(Requisito req, Boolean extendedYes){
		int num = 0;
		
		if(req!=null && req.getValAdmitted()!=null){
			num = req.getValAdmitted().size();
			if(Boolean.TRUE.equals(extendedYes)){
				num++;
			}
		}
		
		return num>0?num:1;
	}
	
	public Integer getColspan(CodeValue cv, Boolean extendedYes){
		int num = 1;
		
		if(cv!=null && "SI".equals(cv.getCode()) && Boolean.TRUE.equals(extendedYes)){
			num++;
		}
		
		return num;
	}

	public List<SelectItem> rispSingolaMultipla() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		SelectItem yes = new SelectItem(new Boolean(true), (String)app.evaluateExpressionGet(fc, "Selezione singola", String.class));
		SelectItem no = new SelectItem(new Boolean(false), (String)app.evaluateExpressionGet(fc, "Scelta tra opzioni", String.class));
		
		options.add(yes);
		options.add(no);
		
		return options;
	}
}