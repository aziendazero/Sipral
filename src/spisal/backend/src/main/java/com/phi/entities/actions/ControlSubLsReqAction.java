package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.ControlSubLs;
import com.phi.entities.baseEntity.ControlSubLsReq;
import com.phi.entities.baseEntity.Requisito;
import com.phi.entities.baseEntity.Risposta;

@BypassInterceptors
@Name("ControlSubLsReqAction")
@Scope(ScopeType.CONVERSATION)
public class ControlSubLsReqAction extends BaseAction<ControlSubLsReq, Long> {

	private static final long serialVersionUID = 532197799L;

	public static ControlSubLsReqAction instance() {
		return (ControlSubLsReqAction) Component.getInstance(ControlSubLsReqAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Questo metodo copia in una nuova istanza di ControlSubLsReq i dati di ControlSubLs a cui fa riferimento.
	 * Questa rappresenta la copia IN DOMANDA della sottolista, associata all'opportuna lista in domanda.
	 * 
	 * @param fun
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ControlSubLsReq copySubList(ControlSubLs lorig) throws InstantiationException, IllegalAccessException{
		if(lorig == null)
			return null;
		
		ca.refreshIfNeeded(lorig);
		ControlSubLsReq lreq = newEntity();
		
		//LINK CON L'ORIGINALE
		List<ControlSubLsReq> listeInDom = lorig.getControlSubLsReq();
		if(listeInDom==null){
			listeInDom = new ArrayList<ControlSubLsReq>();
			lorig.setControlSubLsReq(listeInDom);
		}
		listeInDom.add(lreq);
		lreq.setControlSubLs(lorig);
		
		//COPIO LE PROPRIETA' DELL'ORIGINALE
		lreq.setDescrCode(lorig.getDescrCode());
		lreq.setDescription(lorig.getDescription());
		lreq.setEndValidity(lorig.getEndValidity());
		lreq.setListid(lorig.getListid());
		lreq.setStartValidity(lorig.getStartValidity());
		lreq.setInCommon(lorig.getInCommon());
		
		//COPIO I REQUISITI COME RISPOSTE E CREO I LINK
		if(lorig.getRequisito()!=null && !lorig.getRequisito().isEmpty()){
			
			Date currentDate = new Date();
			
			RispostaAction respAction = RispostaAction.instance();
			for(Requisito req : lorig.getRequisito()){
				
				//non copiare requisiti non validi alla data attuale
				if((req.getStartValidity()==null || req.getStartValidity().after(currentDate)) || (req.getEndValidity()!=null && req.getEndValidity().before(currentDate)))
					continue;
				
				Risposta resp = respAction.copyRequisito(req);
				if(resp!=null){
					List<Risposta> risposteInDom = lreq.getRisposta();
					if(risposteInDom==null){
						risposteInDom = new ArrayList<Risposta>();
						lreq.setRisposta(risposteInDom);
					}
					risposteInDom.add(resp);
					resp.setControlSubLsReq(lreq);
										
				}
			}
		}
		
		return lreq;
	}
	
	public List<ControlSubLsReq> orderList(List<ControlSubLsReq> list){
		Collections.sort(list, new Comparator<ControlSubLsReq>() {

			@Override
			public int compare(ControlSubLsReq sub1, ControlSubLsReq sub2) {

				int result = 0;

				String p1 = null;
				String p2 = null;

				if (sub1 == null && sub2 == null) {
					return result;
				} else if (sub1 == null) {
					result = -1;
				} else if (sub2 == null) {
					result = 1;
				} else {
					p1 = sub1.getListid();
					p2 = sub2.getListid();

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
}