package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.ControlLsReq;
import com.phi.entities.baseEntity.ControlSubLsReq;
import com.phi.entities.baseEntity.Requisito;
import com.phi.entities.baseEntity.Risposta;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("RispostaAction")
@Scope(ScopeType.CONVERSATION)
public class RispostaAction extends BaseAction<Risposta, Long> {

	private static final long serialVersionUID = 531430141L;

	private Query q4, q6;

	@Create
	public void init(){

		String qry4 = "SELECT resp FROM Risposta resp " +
				"INNER JOIN resp.controlSubLsReq subList " +		//prendo la sottolista
				"INNER JOIN subList.controlLsReq list " +			//prendo la lista
				"WHERE list.internalId = :id AND resp.isActive = 1 " +
				"ORDER BY subList.listid ASC, resp.ordering ASC";

		//da riscrivere
		String qry6 = "SELECT COUNT(resp) FROM Risposta resp " +
				"INNER JOIN resp.documenti doc " +
				"WHERE resp.internalId = :id and resp.isActive = 1 and doc.isActive = 1";
		
		q4 = ca.createQuery(qry4);
		q6 = ca.createQuery(qry6);
	}

	public static RispostaAction instance() {
		return (RispostaAction) Component.getInstance(RispostaAction.class, ScopeType.CONVERSATION);
	}

	public boolean hasRispostaOfType(String type, ControlLsReq list){
		boolean rtn = false;
		if(list!=null && type!=null && !type.isEmpty()){
			if(list.getControlSubLsReq()!=null && !list.getControlSubLsReq().isEmpty()){
				for(ControlSubLsReq subList : list.getControlSubLsReq()){
					if(subList.getRisposta()!=null && !subList.getRisposta().isEmpty()){
						for(Risposta resp : subList.getRisposta()){
							if(resp.getValue()!=null && resp.getReqVis() && type.equals(resp.getValue().getCode())){
								return true;
							}
						}
					}
				}
			}

		}
		return rtn;
	}

	public boolean controlRisposte(List<Risposta> list){
		boolean rtn = true;
		if(list==null || list.isEmpty()){
			return rtn;
		}

		FacesContext fContext = FacesContext.getCurrentInstance();
		for(Risposta resp : list){
			if(resp.getPointsFlag() && resp.getTotal()!=null && resp.getPoints()!=null){
				if(resp.getTotal() > resp.getPoints()){

					addMessage(fContext, "Requisito "+ resp.getDescrCode() +"-Punteggio assegnato maggiore di "+resp.getPoints());
					rtn = false;
				}
			}
		}

		return rtn;	
	}

	public void setPoints(Risposta resp){
		if(resp==null || resp.getValue()==null){
			return;

		}else if(resp.getPointsFlag()){
			String code = resp.getValue().getCode();

			if("SI".equals(code)){
				resp.setTotal(resp.getPoints());
			}else {
				resp.setTotal(0);
			}
		}
	}

	public List<Risposta> orderList(List<Risposta> list){
		Collections.sort(list, new Comparator<Risposta>() {

			@Override
			public int compare(Risposta r1, Risposta r2) {

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

	/**
	 * Prepara la lista delle possibili risposte: i valori mostrati sono i "code" diretti (SI, SP, NO, NA)
	 * @param codeValues
	 * @return
	 */
	public List<SelectItem> attributeToSelectItem(Collection<CodeValue> codeValues) {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();

		if (codeValues == null )
			return selectItems;

		for (CodeValue cv : codeValues) {
			if (cv != null) {
				String label = cv.getLangIt();
				SelectItem selItem = new SelectItem(cv , label);
				selectItems.add(selItem);
			}
		}
		return selectItems;
	}

	/**
	 * Effettua il refresh delle risposte inserite
	 * @param lst
	 */
	public void refreshList(List<Risposta> lst) {

		if (lst == null) 
			return;

		for (Risposta resp : lst) {
			if(resp.getInternalId()>0L){
				ca.refreshIfNeeded(resp);
			}
		}
	}

	/**
	 * Injetta nel corretto ordine la lista dei requisiti da compilare per una data attività
	 * @param act
	 */
	public void injectRisposte(ControlLsReq act) {
		if (act!=null)
			injectList(getRisposte(act));
	}


	/**
	 * Injetta nel corretto ordine la lista dei requisiti da compilare per una data attività
	 * @param act
	 */
	public List<Risposta> getRisposte(ControlLsReq act) {
		ControlLsReqAction lsAction = ControlLsReqAction.instance();
		ControlSubLsReqAction subAction = ControlSubLsReqAction.instance();

		if(act.getInternalId()<=0){
			List<Risposta> out = new ArrayList<Risposta>();
			List<ControlSubLsReq> subList = ((ControlLsReq) act).getControlSubLsReq();
			if(subList!=null && !subList.isEmpty()){
				for(ControlSubLsReq sub : subAction.orderList(subList)){
					out.addAll(orderList(sub.getRisposta()));
				}
			}
			return out;
		}else{
			q4.setParameter("id", act.getInternalId());
			return q4.getResultList();
		}
	}

	/**
	 * Questo metodo copia in una nuova istanza di Risposta i dati di Requisito a cui fa riferimento.
	 * Questa rappresenta la copia IN DOMANDA del requisito, associata all'opportuna sottolista in domanda.
	 * Metodo usato nella copia della sottolista in domanda.
	 * 
	 * @param fun
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Risposta copyRequisito(Requisito req) throws InstantiationException, IllegalAccessException{
		if(req == null)
			return null;

		ca.refreshIfNeeded(req);
		Risposta risp = newEntity();

		//LINK CON L'ORIGINALE
		List<Risposta> risposte = req.getRisposta();
		risposte.add(risp);
		risp.setRequisito(req);

		//COPIO LE PROPRIETA' DELL'ORIGINALE
		risp.setCampoNoteObb(req.getCampoNoteObb());
		risp.setRispostaObb(req.getRispostaObb());
		risp.setRispostaType(req.getRispostaType());
		risp.setDescrCode(req.getDescrCode());
		risp.setDescription(req.getDescription());
		risp.setDescrPerf(req.getDescrPerf());
		risp.setEndValidity(req.getEndValidity());
		risp.setOrdering(req.getOrdering());
		risp.setParent(req.getParent());
		risp.setPoints(req.getPoints());
		risp.setPointsFlag(req.getPointsFlag());
		risp.setReqid(req.getReqid());
		risp.setReqVar(req.getReqVar());
		risp.setReqVis(req.getReqVis());
		risp.setStartValidity(req.getStartValidity());

		risp.setValDomain(req.getValDomain());
		List<CodeValuePhi> valAdmitted = new ArrayList<CodeValuePhi>();
		valAdmitted.addAll(req.getValAdmitted());
		risp.setValAdmitted(valAdmitted);
		
		List<CodeValuePhi> valNotesObb = new ArrayList<CodeValuePhi>();
		valNotesObb.addAll(req.getValNotesObb());
		risp.setValNotesObb(valNotesObb);


		return risp;
	}

	public Long getDocCount(Long internalId){
		if(internalId==null)
			return null;

		q6.setParameter("id", internalId);			
		Object count = q6.getSingleResult();

		if(count instanceof Long && (Long)count > 0)
			return (Long)count;
		else
			return null;
	}

	private void addMessage(FacesContext fContext, String message){
		FacesMessage msg = new FacesMessage(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		fContext.addMessage(null, msg);

	}
}