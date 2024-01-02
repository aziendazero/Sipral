package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.ControlLs;
import com.phi.entities.baseEntity.ControlLsReq;
import com.phi.entities.baseEntity.ControlSubLs;
import com.phi.entities.baseEntity.ControlSubLsReq;
import com.phi.entities.baseEntity.Oggetto;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ControlLsReqAction")
@Scope(ScopeType.CONVERSATION)
public class ControlLsReqAction extends BaseAction<ControlLsReq, Long> {

	private static final long serialVersionUID = 533236612L;

	public static ControlLsReqAction instance() {
		return (ControlLsReqAction) Component.getInstance(ControlLsReqAction.class, ScopeType.CONVERSATION);
	}

	public void injectLists(Oggetto obj){
		if(obj==null)
			ejectList();

		String qry = "SELECT lst FROM ControlLsReq lst " +
				"INNER JOIN lst.oggetto obj " +
				"WHERE lst.isActive = 1 AND obj.internalId = :id";

		Query q1 = ca.createQuery(qry);
		q1.setParameter("id", obj.getInternalId());
		List<ControlLsReq> result = q1.getResultList();
		List<ControlLsReq> out = new ArrayList<ControlLsReq>();
		if(result!=null && !result.isEmpty()){
			for(ControlLsReq lsOrig : result){
				boolean found = false;
				boolean inCommon = false;
				if(lsOrig.getControlSubLsReq()!=null && !lsOrig.getControlSubLsReq().isEmpty()){
					for(ControlSubLsReq subList : lsOrig.getControlSubLsReq()){
						if(subList!=null && Boolean.TRUE.equals(subList.getInCommon())){
							inCommon = true;
							break;
						}
					}
				}

				if(inCommon){
					for(ControlLsReq lsNew : out){
						if(lsNew.getListid().equals(lsOrig.getListid())){
							if(lsNew.getControlSubLsReq()!=null && !lsNew.getControlSubLsReq().isEmpty()){
								for(ControlSubLsReq subList : lsNew.getControlSubLsReq()){
									if(subList!=null && Boolean.TRUE.equals(subList.getInCommon())){
										found = true;
										break;
									}
								}
							}
						}
					}
				}


				if(!inCommon || (inCommon && !found))
					out.add(lsOrig);
			}
		}

		injectList(out);
	}

	/*public void injectListsForHistory(Procpratiche obj){
		if(obj==null)
			ejectList();

		List<ControlLsReq> result = new ArrayList<ControlLsReq>();

		if(obj.getControlLsReq()!=null)
			result.addAll(obj.getControlLsReq());

		injectList(result);
	}*/

	/**
	 * Questo metodo copia in una nuova istanza di ControlLsReq i dati di ControlLs a cui fa riferimento.
	 * Questa rappresenta la copia IN DOMANDA della lista, associata all'opportuna attività in domanda.
	 * Metodo usato nella copia della sottolista in domanda.
	 * 
	 * @param fun
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ControlLsReq copyList(ControlLs lorig) throws InstantiationException, IllegalAccessException{
		if(lorig == null)
			return null;

		ca.refreshIfNeeded(lorig);
		ControlLsReq lreq = newEntity();

		//LINK CON L'ORIGINALE
		List<ControlLsReq> listeInDom = lorig.getControlLsReq();
		if(listeInDom==null){
			listeInDom = new ArrayList<ControlLsReq>();
			lorig.setControlLsReq(listeInDom);
		}
		listeInDom.add(lreq);
		lreq.setControlLs(lorig);

		//COPIO LE PROPRIETA' DELL'ORIGINALE
		lreq.setDescrCode(lorig.getDescrCode());
		lreq.setDescrSmall(lorig.getDescrSmall());
		lreq.setDescription(lorig.getDescription());
		lreq.setEndValidity(lorig.getEndValidity());
		lreq.setListid(lorig.getListid());
		lreq.setStartValidity(lorig.getStartValidity());
		lreq.setHeader(lorig.getHeader());
		lreq.setFooter(lorig.getFooter());
		if(lorig.getWorkingLine()!=null){
			List<CodeValuePhi> lst = new ArrayList<CodeValuePhi>();
			lst.addAll(lorig.getSupervisionCode());
			lreq.setWorkingLine(lst);
		}
		
		return lreq;
	}

	/**
	 * Questo metodo copia in una nuova istanza di ControlLsReq i dati di ControlLs a cui fa riferimento.
	 * Questa rappresenta la copia IN DOMANDA della lista, associata DIRETTAMENTE ALLA domanda (cioè StructureReq).
	 * 
	 * @param fun
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ControlLsReq copyListDom(ControlLs lorig) throws InstantiationException, IllegalAccessException{
		if(lorig == null)
			return null;

		ControlLsReq lreq = copyList(lorig);
		
		if(lorig.getControlSubLs()!=null && !lorig.getControlSubLs().isEmpty()){
			ControlSubLsReqAction subAction = ControlSubLsReqAction.instance();
			for(ControlSubLs sub : lorig.getControlSubLs()){
				ControlSubLsReq subReq = subAction.copySubList(sub);


				List<ControlSubLsReq> sottoListeInDom = lreq.getControlSubLsReq();
				if(sottoListeInDom==null){
					sottoListeInDom = new ArrayList<ControlSubLsReq>();
					lreq.setControlSubLsReq(sottoListeInDom);
				}
				sottoListeInDom.add(subReq);
				subReq.addControlLsReq(lreq);
			}
		}

		return lreq;
	}

	/**
	 * Questo metodo, al momento del salvataggio della lista di controllo, salva in ogni sottolista che lo prevede,
	 * il totale dei punti accumulati con le risposte SI e SP, per tutte le liste di controllo di una data funzione.
	 * @param list
	 */
	public void setTotalsAndCompiled(ControlLsReq ls){
		if(ls!=null){
			if(ls.getControlSubLsReq()!=null && !ls.getControlSubLsReq().isEmpty()){

				for(ControlSubLsReq sub : ls.getControlSubLsReq()){
					
					sub.setCompiled(true);
				}
			}

			ls.setCompiled(true);
			ls.setSaved(true);
		}
	}

	public List<ControlLsReq> orderList(List<ControlLsReq> list){
		Collections.sort(list, new Comparator<ControlLsReq>() {

			@Override
			public int compare(ControlLsReq ls1, ControlLsReq ls2) {

				int result = 0;

				String p1 = null;
				String p2 = null;

				if (ls1 == null && ls2 == null) {
					return result;
				} else if (ls1 == null) {
					result = -1;
				} else if (ls2 == null) {
					result = 1;
				} else {
					p1 = ls1.getListid();
					p2 = ls2.getListid();

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
	 * Usato in InsertModifyAllegatoB per injettare una lista con un singolo elemento (per futuro setTotalsAndCompiled e checkAllLists)
	 * @param listInDom
	 */
	public void injectAsList(ControlLsReq listInDom){
		if(listInDom==null)
			injectEmptyList("ControlLsReqList");
		
		List<ControlLsReq> list = new ArrayList<ControlLsReq>();
		list.add(listInDom);
		injectList(list);
		
	}

	public boolean checkSaved(List<ControlLsReq> list){
		
		if(list!=null && !list.isEmpty()){
			for(ControlLsReq ls : list){
				if(!Boolean.TRUE.equals(ls.getSaved()))
					return false;
			}
			return true; //all lists have been saved
		}
		
		return false;
	}

	/**
	 * Unlinka tutte le liste, sottoliste, risposte e tipi allegato dall'oggetto in input
	 * @param str
	 * @throws PhiException 
	 *
	public void unLinkLists(Procpratiche obj){

		if(obj instanceof Procpratiche){
			Procpratiche ogg = (Procpratiche)obj;

			if(ogg.getControlLsReq()!=null && !ogg.getControlLsReq().isEmpty()){
				for(ControlLsReq lst : ogg.getControlLsReq()){
					if(lst.getControlLs()!=null)
						lst.getControlLs().removeControlLsReq(lst);

					lst.setControlLs(null);

					if(lst.getControlSubLsReq()!=null && !lst.getControlSubLsReq().isEmpty()){
						List<ControlSubLsReq> subListTmp = new ArrayList<ControlSubLsReq>();
						subListTmp.addAll(lst.getControlSubLsReq());
						for(ControlSubLsReq sub : subListTmp){

							if(sub.getControlSubLs()!=null)
								sub.getControlSubLs().removeControlSubLsReq(sub);

							sub.setControlSubLs(null);

							if(sub.getRisposta()!=null && !sub.getRisposta().isEmpty()){
								for(Risposta resp : sub.getRisposta()){
									if(resp.getRequisito()!=null)
										resp.getRequisito().removeRisposta(resp);

									resp.setRequisito(null);
								}
							}
						}
					}
				}
			}	
		}
	}*/
}