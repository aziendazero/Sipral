package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import com.phi.cs.view.bean.ButtonBean;
import com.phi.entities.baseEntity.Disp;
import com.phi.entities.baseEntity.MonteOre;
import com.phi.entities.baseEntity.Pianificazione;

import com.phi.entities.baseEntity.ProgettoAssociato;
import com.phi.entities.role.Operatore;

import com.phi.entities.actions.BaseAction;
import com.phi.security.UserBean;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.apache.log4j.Logger;

@BypassInterceptors
@Name("PianificazioneAction")
@Scope(ScopeType.CONVERSATION)
public class PianificazioneAction extends BaseAction<Pianificazione, Long> {

	private static final long serialVersionUID = 735652651L;
    private static final Logger log = Logger.getLogger(PianificazioneAction.class);


	public static PianificazioneAction instance() {
		return (PianificazioneAction) Component.getInstance(PianificazioneAction.class, ScopeType.CONVERSATION);
	}

	public void manageChanges(Pianificazione p) {
		try {
			if (p==null)
				return;
			
			if (p.getCreatoDa()==null){
				p.setCreatoDa(UserBean.instance().getCurrentEmployee());
				p.setDataCreazione(new Date());
			} else{
				p.setModificatoDa(UserBean.instance().getCurrentEmployee());
				p.setDataUltimaModifica(new Date());
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void prioritaUpDown(Pianificazione p, ProgettoAssociato progettoAssociato) {
		try {
			if (p==null || progettoAssociato==null)
				return;
			
			List<ProgettoAssociato> paList = p.getProgettiAssociati();
			if (paList==null || paList.size()<1)
				return;

			Boolean up = null;
			String value = "";
			ButtonBean button = ButtonBean.instance();
						
			if (button!=null)
				value = button.getValue();
			
			if ("prioritaUp".equals(value))
				up=true;
			else if ("prioritaDown".equals(value))
				up=false;
			
			if (up==null)
				return;

			Integer minValue = null;
			Integer maxValue = null;
			
			//Trova i valori minimi e massimi di priorità. 
			//L'ordine di priorità è dal basso all'alto (priorita 1>priorità 2)
			for (ProgettoAssociato pa:paList){
				Integer priorita = pa.getPriorita();
				if (minValue==null || priorita<minValue)
					minValue = priorita;//Valore minimo - priorità massima
				
				if (maxValue==null || priorita>maxValue)
					maxValue = priorita;//Valore massimo - priorità minima
			}

			Integer priorita = progettoAssociato.getPriorita();
			//Impossibile aumentare la priorità del progetto di priorità 1 
			if (up && priorita==1 && priorita==minValue)
				return;
			
			//Impossibile ridurre la priorità del progetto di priorità massima (più bassa)
			if (!up && priorita==maxValue)
				return;
			
			int newValue = up?(priorita-1):(priorita+1);
			
			ProgettoAssociato paToUpOrWown = null;
			
			//Trova, se esiste, il progetto con la nuova priorità che si vuole raggiungere
			for (ProgettoAssociato pa:paList){
				if (pa.getPriorita()==newValue)
					paToUpOrWown = pa;
			}
			
			if (paToUpOrWown!=null)
				paToUpOrWown.setPriorita(priorita);//Setta la priorità attuale
			
			progettoAssociato.setPriorita(newValue);//Setta la priorità desiderata
			
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void refreshCarichi(List<ProgettoAssociato> paList) {
		try {
			if (paList==null || paList.size()<1)
				return;
			
			ProgettoAssociatoAction paa = (ProgettoAssociatoAction)Component.getInstance("ProgettoAssociatoAction");
			
			for (ProgettoAssociato pa:paList)
				paa.setCarichi(pa);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setUlss() {
		try {
			getEqual().remove("serviceDeliveryLocation.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.internalId", id);
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void linkProgettoAssociato(ProgettoAssociato pa){
		try{
			if (pa != null) {
				Pianificazione pianificazione = getEntity();
				
				if (pianificazione.getProgettiAssociati() == null)
					pianificazione.setProgettiAssociati(new ArrayList<ProgettoAssociato>());
				
				pa.setPianificazione(pianificazione);
				pianificazione.getProgettiAssociati().add(pa);
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void unlinkProgettoAssociato(ProgettoAssociato pa){
		try{
			if (pa != null) {
				Pianificazione pianificazione = getEntity();
				
				if (pianificazione.getProgettiAssociati() != null && pianificazione.getProgettiAssociati().contains(pa)){
					pianificazione.getProgettiAssociati().remove(pa);
					pa.setPianificazione(null);
					pa.setProgetto(null);
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Calcola le disponibilità totali delle nomine di tutti gli operatori. *  
	 *  Tali disponibilità verranno memorizzate nella HashMap "disponibilitaNomine" nel temporary di PianificazioneAction. **/
	public void updateDisponibilitaNomina(Pianificazione p){
		try{
			if (p == null || p.getAnno()==null || p.getServiceDeliveryLocation()==null)
				return;
				
			this.getTemporary().put("disponibilitaNomine", null);
			HashMap<String,Double> disponibilitaNomine = new HashMap<String, Double>();
			
			//Recupero tutti gli operatori interni ed attivi della ulss associata alla pianificazione
			String qry = "SELECT DISTINCT op FROM Operatore op " +
					"LEFT JOIN op.employee emp " +
					"LEFT JOIN op.serviceDeliveryLocation ulss " +
					"LEFT JOIN op.monteOre mo " +
					"WHERE op.isActive = true AND " +
					"emp IS NOT NULL AND " +
					"ulss.internalId=:ulssId AND " +
					"mo.anno=:anno";
			
			Query qOp = ca.createQuery(qry);
			qOp.setParameter("ulssId", p.getServiceDeliveryLocation().getInternalId());
			qOp.setParameter("anno", p.getAnno());
			
			List<Operatore> lst = qOp.getResultList();
			
			if (lst==null || lst.size()<1)
				return;
			
			//Itera gli operatori per aggiungere alla HashMAp le ore disponibili
			for (Operatore op:lst){
				if (op!=null && op.getCode()!=null){
					String nomina = op.getCode().getCode();
					Double hhDisponibili = 0.0;
					
					/* Call Marchì (02/09/2019) + Call Fedrizzi/Canciani (04/09/2019)
					 * Le ore disponibili vanno ricercate nella lista disponibilità associata al monte ore, confrontando la data di tali disponibilità con la data attuale.
					 * Se la lista delle disponibilità è nulla, si prende il valore dell'attributo hhDisponibili del montre ore */
					for (MonteOre mo:op.getMonteOre()){
						if (mo.getAnno().equals(p.getAnno())){
							List<Disp> dispList = mo.getDisp();
							if (dispList==null || dispList.size()<1)
								hhDisponibili = mo.getHhDisponibili();
							else {
								Date toDay = new Date();
								
								//Prende l'ultima disponibilità con data inferiore alla data attuale
								for (Disp disp:dispList){
									if (toDay.compareTo(disp.getDal())>=0)
										hhDisponibili = disp.getHh();
								}
							}
						}
					}
					
					if (hhDisponibili!=0.0){
						Double val = disponibilitaNomine.get(nomina);
						if (val==null)
							val = hhDisponibili;
						else 
							val += hhDisponibili;
						
						disponibilitaNomine.put(nomina, val);
					}
				}
			}
			
		 /* -- FAKE VALUES ---------------------------------------------------------
			disponibilitaNomine.put("Assistente sanitario", 100.00);
			disponibilitaNomine.put("Direttore del servizio", 100.00);
			disponibilitaNomine.put("Dirigente", 100.00);
			disponibilitaNomine.put("Dirigente chimico", 100.00);
			disponibilitaNomine.put("Dirigente ingegnere", 100.00);
			disponibilitaNomine.put("Dirigente medico", 100.00);
			disponibilitaNomine.put("Informatico", 100.00);
			disponibilitaNomine.put("Medico sumai", 100.00);
			disponibilitaNomine.put("Operatore tecnico specializzato", 100.00);
			disponibilitaNomine.put("Personale amministrativo", 100.00);
			disponibilitaNomine.put("Personale di vigilanza ed ispezione", 100.00);
			disponibilitaNomine.put("Personale infermieristico", 100.00);
			disponibilitaNomine.put("Psicologo del lavoro", 100.00);
			disponibilitaNomine.put("Tecnico alimentarista", 100.00);
			disponibilitaNomine.put("Tecnico della prevenzione", 100.00);
			disponibilitaNomine.put("Tecnico generico", 100.00);
			-- FAKE VALIES --------------------------------------------------------- */
			
			this.getTemporary().put("disponibilitaNomine", disponibilitaNomine);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
}