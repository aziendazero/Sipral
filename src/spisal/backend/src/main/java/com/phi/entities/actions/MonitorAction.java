package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.Monitor;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.Pianificazione;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Progetto;
import com.phi.entities.baseEntity.ProgettoAssociato;
import com.phi.entities.role.Operatore;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("MonitorAction")
@Scope(ScopeType.CONVERSATION)
public class MonitorAction extends BaseAction<Monitor, Long> {

	private static final long serialVersionUID = 1824187676L;
	private static final Logger log = Logger.getLogger(MonitorAction.class);


	public static MonitorAction instance() {
		return (MonitorAction) Component.getInstance(MonitorAction.class, ScopeType.CONVERSATION);
	}
	
	/** Restituisce la lista di progetti associati alla pianificazione (pianificazione.mmgp - Tab Monitoraggio carichi) **/
	public List<SelectItem> getListaProgetti() throws PersistenceException {
		try {
			
			IdataModel<ProgettoAssociato> paList = (IdataModel<ProgettoAssociato>) Contexts.getConversationContext().get("ProgettoAssociatoList");
			List<SelectItem> selItems = new ArrayList<SelectItem>();
			
			if (paList==null || paList.size()<1)
				return selItems;
			
			List<Object> paList4tmp = new ArrayList<Object>();
			ProgettoAssociatoAction paa = (ProgettoAssociatoAction)Component.getInstance("ProgettoAssociatoAction");

			for (ProgettoAssociato pa : paList.getList()) {
				//Solo per i progetti associati in stato validato (completed)
				if (pa!=null && pa.getStatusCode()!=null && "completed".equals(pa.getStatusCode().getCode())){
					String nomeProgetto = null;
					if (pa.getProgetto()!=null)
						nomeProgetto = pa.getProgetto().getNome() + " - " + paa.getLineaDiLavoroStr(pa, false) + " - Priorità: " + pa.getPriorita();
					
					if (nomeProgetto!=null && !"".equals(nomeProgetto)){
						SelectItem si = new SelectItem(pa.getInternalId(), nomeProgetto);
						selItems.add(si);
						
						paList4tmp.add(pa.getInternalId());
					}
				}
			}
			
			if (!this.getTemporary().containsKey("selectedProgAssoc"))
				this.getTemporary().put("selectedProgAssoc", paList4tmp);
			
			return selItems;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Estrae la lista (senza duplicati) dei partecipanti (associati ai progetti selezionati in pianificazione.mmgp - Tab Monitoraggio carichi)
	 *  Crea la lista di monitoraggio (oggetti Monitor -metodo createList) e la injetta in conversation **/
	public void check() throws PersistenceException {
		try {
			HashMap<String, Object> temp = this.getTemporary();
			
			if (temp==null || !this.getTemporary().containsKey("selectedProgAssoc"))
				return;
			
			//Accede agli id dei Progetti Associati dai quali estrarre i partecipanti
			List<String> paListStr = (List<String>)this.getTemporary().get("selectedProgAssoc");
			List<Long> internalIds = new ArrayList<Long>();
			
			if (paListStr==null || paListStr.size()<1)
				return;
			else 
				for (String id:paListStr)
					internalIds.add(Long.parseLong(id));
			
			if (internalIds==null || internalIds.size()<1)
				return;
			
			String qryPart = "SELECT DISTINCT p FROM Partecipanti p " +
					"LEFT JOIN p.progettoAssociato pa " +
					"LEFT JOIN p.operatore op " +
					"LEFT JOIN op.employee emp " +
					"WHERE pa.internalId IN (:internalIds) ORDER BY emp.name.fam ASC";

			Query qPart = ca.createQuery(qryPart);
			qPart.setParameter("internalIds", internalIds);
			
			@SuppressWarnings("unchecked")
			List<Partecipanti> partList = qPart.getResultList();
			
			this.injectList(this.createList(partList));
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Preseleziona con l'anno della pianificazione le date dell'intervallo di monitoraggio (pianificazione.mmgp - Tab Monitoraggio carichi) **/
	public void preActions(Pianificazione p) throws PersistenceException {
		try {
			if (p==null || p.getAnno()==null)
				return;
			
			HashMap<String, Object> temp = this.getTemporary();
			Integer anno = p.getAnno();
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.DAY_OF_YEAR, 1);    
			Date lowerBound = cal.getTime();

			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.MONTH, 11); //11 = Dicembre
			cal.set(Calendar.DAY_OF_MONTH, 31);
			Date upperBound = cal.getTime();
			
			temp.put("upperBound", upperBound);
			temp.put("lowerBound", lowerBound);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Riceve in input la la lista degli operatori coinvolti nei progetti selezionati e crea i relativi oggetti Monitor.
	 *  Ogni oggetto monitor è legato all'operatore e alla lista delle sue partecipazioni ai Progetti Associati alla pianificazione **/ 
	public List<Monitor> createList(List<Partecipanti> partList) throws PersistenceException {
		try {

			List<Monitor> monitorList = new ArrayList<Monitor>();
			
			if (partList==null || partList.size()<1)
				return monitorList;
			
			/* Itera le partecipazioni per creare la lista di monitoraggio con le sole informazioni relative alla Pianificazione (infoProgetti, praticheAttese e caricoAtteso).
			 * Se l'operatore associato alla partecipazione è già presente in un Monitor (stesso operatore con partecipazione su più progetti) aggiorna il Monitor e restituisce null */
			for (Partecipanti part:partList){
				if (part!=null){
					
					Monitor m = this.checkPartecipante(monitorList, part);
					
					if (m!=null)
						monitorList.add(m);
				}
			}

			/* Dopo aver creato la lista di oggetti Monitor e recuperato le informazioni dalla pianificazione
			 * calcolo lo stato di avanzamento delle pratiche */
			
			//Recupero agli id dei Progetti Associati selezionati (pianificazione.mmgp - Tab Monitoraggio carichi) --------//
			List<String> paListStr = (List<String>)this.getTemporary().get("selectedProgAssoc");
			List<Long> paInternalIds = new ArrayList<Long>();
			
			if (paListStr==null || paListStr.size()<1)
				return monitorList;
			else 
				for (String id:paListStr)
					paInternalIds.add(Long.parseLong(id));
			//-------------------------------------------------------------------------------------------------------------//
			
			IdataModel<ProgettoAssociato> paList = (IdataModel<ProgettoAssociato>) Contexts.getConversationContext().get("ProgettoAssociatoList");
			if (paList==null || paList.size()<1)
				return monitorList;
			
			//Per i soli Progetti Associati selezionati, aggiorno lo stato di avanzamento (praticheAperte, praticheChiuse e percentualePraticheChiuse)
			for (ProgettoAssociato pa:paList.getList()){
				if (paInternalIds.contains(pa.getInternalId()))
					this.checkPratiche(pa, monitorList);
			}
			
			//Itero la lista di Monitor per calcolare caricoAperto, caricoChiuso e % caricoChiuso 
			for (Monitor m:monitorList){
				for (Partecipanti part:m.getPartecipazioni()){
					
					//FIXME: Il carico chiuso complessivo (dell'operatore associato all'oggetto Monitor) è la somma dei carichi chiusi sulle varie partecipazioni?
					Double caricoChiuso = m.getCaricoChiuso();
					m.setCaricoChiuso((caricoChiuso!=null?caricoChiuso:0.0) + this.caricoChiuso(m, part));
					
					//FIXME: La % di carico chiuso complessivo (dell'operatore associato all'oggetto Monitor) è la somma delle % dei carichi chiusi sulle varie partecipazioni?
					Double caricoChiusoPerc = m.getCaricoChiusoPerc();
					m.setCaricoChiusoPerc((caricoChiusoPerc!=null?caricoChiusoPerc:0.0) + this.caricoChiusoPerc(m, part));
					
					//FIXME: Il carico aperto complessivo (dell'operatore associato all'oggetto Monitor) è la somma dei carichi aperti sulle varie partecipazioni?
					Double caricoAperto = m.getCaricoAperto();
					m.setCaricoAperto((caricoAperto!=null?caricoAperto:0.0) + this.caricoAperto(m, part));
				}
			}
			
			//Calcolo medie e totali
			Monitor totali = new Monitor();
			totali.setTotale(true);
			
			Monitor medie  = new Monitor();
			medie.setMedia(true);
			
			Integer num = monitorList.size();
			
			if (num!=null && num>0){
				for (Monitor m:monitorList){
					totali.setPraticheAttese((totali.getPraticheAttese()!=null?totali.getPraticheAttese():0) + m.getPraticheAttese());
					totali.setPraticheChiuse((totali.getPraticheChiuse()!=null?totali.getPraticheChiuse():0) + m.getPraticheChiuse());
					//totali.setPraticheChiusePerc((totali.getPraticheChiusePerc()!=null?totali.getPraticheChiusePerc():0) + m.getPraticheChiusePerc());
					totali.setCaricoAtteso((totali.getCaricoAtteso()!=null?totali.getCaricoAtteso():0) + m.getCaricoAtteso());
					totali.setCaricoChiuso((totali.getCaricoChiuso()!=null?totali.getCaricoChiuso():0) + m.getCaricoChiuso());
					//totali.setCaricoChiusoPerc((totali.getCaricoChiusoPerc()!=null?totali.getCaricoChiusoPerc():0) + m.getCaricoChiusoPerc());
					totali.setPraticheAperte((totali.getPraticheAperte()!=null?totali.getPraticheAperte():0) + m.getPraticheAperte());
					totali.setCaricoAperto((totali.getCaricoAperto()!=null?totali.getCaricoAperto():0) + m.getCaricoAperto());
				}
				
				medie.setPraticheAttese(totali.getPraticheAttese()/num);
				medie.setPraticheChiuse(totali.getPraticheChiuse()/num);
				//medie.setPraticheChiusePerc(totali.getPraticheChiusePerc()/num);
				medie.setCaricoAtteso(totali.getCaricoAtteso()/num);
				medie.setCaricoChiuso(totali.getCaricoChiuso()/num);
				//medie.setCaricoChiusoPerc(totali.getCaricoChiusoPerc()/num);
				medie.setPraticheAperte(totali.getPraticheAperte()/num);
				medie.setCaricoAperto(totali.getCaricoAperto()/num);
			}
			
			//% Totale Pratiche Chiuse = 100*Totale Pratiche Chiuse/Totale Pratiche Aperte
			Double totalePraticheAperte = totali.getPraticheAperte();
			if (totalePraticheAperte!=null && totalePraticheAperte>0)
				totali.setPraticheChiusePerc(((totali.getPraticheChiuse()!=null?totali.getPraticheChiuse():0.0)/totalePraticheAperte)*100);

			//% Totale Carico Chiuso = 100*Totale Carico Chiuso/Totale Carico Aperto
			Double totaleCaricoAperto = totali.getCaricoAperto();
			if (totaleCaricoAperto!=null && totaleCaricoAperto>0)
				totali.setCaricoChiusoPerc(((totali.getCaricoChiuso()!=null?totali.getCaricoChiuso():0.0)/totaleCaricoAperto)*100);
			
			monitorList.add(totali);
			monitorList.add(medie);
			
			//Media Scostamento = Carico aperto - Media carico aperto
			for (Monitor m:monitorList)
				m.setMediaScostamento(m.getCaricoAperto() - medie.getCaricoAperto());
			
			//Cerca un eventuale filtro sulle qualifiche/nomine
			String qualifica = null;
			if (this.getTemporary().get("qualifica")!=null)
				qualifica = this.getTemporary().get("qualifica").toString();
			
			if (qualifica==null || "".equals(qualifica))
				return monitorList;
			
			else {
				List<Monitor> filteredMonitorList = new ArrayList<Monitor>();
				
				for (Monitor mo:monitorList){
					Operatore op = mo.getOperatore();
					if (op!=null && op.getCode()!=null){
						String code = op.getCode().getCode();
						
						if (code!=null && code.equals(qualifica))
							filteredMonitorList.add(mo);
					}
				}
				
				return filteredMonitorList;
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/** Controlla che il partecipante non sia già in monitorList. Se non lo trova, crea e restituisce l'oggetto da aggiungere alla lista, 
	 *  altrimenti aggiorna l'oggetto in monitorList e restituisce null **/
	public Monitor checkPartecipante(List<Monitor> monitorList, Partecipanti part) throws PersistenceException {
		try {
			
			for (Monitor m:monitorList){
				//Se trovo che l'operatore è già nella lista di monitoraggio, aggiorno l'oggetto monitoraggio
				if (m.getOperatore().equals(part.getOperatore())){
					//Aggiungo la partecipazione
					m.getPartecipazioni().add(part);
					
					//Aggiungo dettagli all'attributo infoProgetti
					this.addInfoPartecipazione(m, part);
					
					//Aggiungo il contributo della partecipazione all'attributo praticheAttese
					this.addPraticheAttese(m, part);
					
					//Aggiungo il contributo della partecipazione all'attributo caricoAtteso
					this.addCaricoAtteso(m, part);

					return null;
				}
			}
			
			//Altrimenti creo un nuovo oggetto Monitor che verrà aggiunto (vedi chiamante) alla lista di monitoraggio
			Monitor m = new Monitor();
			m.setOperatore(part.getOperatore());
			
			if (m.getPartecipazioni()==null)
				m.setPartecipazioni(new ArrayList<Partecipanti>());
			
			m.getPartecipazioni().add(part);
			
			this.addInfoPartecipazione(m, part);
			this.addPraticheAttese(m, part);
			this.addCaricoAtteso(m, part);
	
			return m;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void addInfoPartecipazione(Monitor m, Partecipanti part) throws PersistenceException {
		try {
			ProgettoAssociatoAction paa = (ProgettoAssociatoAction)Component.getInstance("ProgettoAssociatoAction");
			ProgettoAssociato pa = part.getProgettoAssociato();
			Progetto p = pa.getProgetto();
			
			String info = "";
			if (m.getInfoProgetti()!=null)
				info = m.getInfoProgetti();
			
			if ("".equals(info) || !info.endsWith("</b><br>"))
				info += "<b>" + p.getNome() + " - " + paa.getLineaDiLavoroStr(pa, false) + " - Priorità: " + pa.getPriorita() + "</b><br>";
			else 
				info += p.getNome() + " - " + paa.getLineaDiLavoroStr(pa, false)  + " - Priorità: " + pa.getPriorita() + "<br>";
			
			m.setInfoProgetti(info);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void addPraticheAttese(Monitor m, Partecipanti part) throws PersistenceException {
		try {			
			Double praticheAttese = 0.0;
			if (m.getPraticheAttese()!=null)
				praticheAttese = m.getPraticheAttese();
			
			if (part.getNumeroPraticheMod()!=null && part.getNumeroPraticheMod()>0)
				praticheAttese += part.getNumeroPraticheMod();
			else if (part.getNumeroPratiche()!=null && part.getNumeroPratiche()>0)
				praticheAttese += part.getNumeroPratiche();
		
			m.setPraticheAttese(praticheAttese);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	private void addCaricoAtteso(Monitor m, Partecipanti part) throws PersistenceException {
		try {			
			Double caricoAtteso = 0.0;
			if (m.getCaricoAtteso()!=null)
				caricoAtteso = m.getCaricoAtteso();
			
			//Recupero il carico atteso dalla partecipazione
			if (part.getCaricoAtteso()!=null && part.getCaricoAtteso()>0)
				caricoAtteso += part.getCaricoAtteso();
		
			m.setCaricoAtteso(caricoAtteso);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
		
	/** Calcola il carico aperto come costoNomina*numeroPraticheAperte **/
	private Double caricoAperto(Monitor m, Partecipanti part) throws PersistenceException {
		try {
			Double numeroPraticheAperte = m.getPraticheAperte();
			Double costoNomina = part.getCostoNomina();
			
			if (numeroPraticheAperte!=null && costoNomina!=null)
				 return numeroPraticheAperte*costoNomina;
			
			return 0.0;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Calcola il carico chiuso come costoNomina*numeroPraticheChiuse **/
	private Double caricoChiuso(Monitor m, Partecipanti part) throws PersistenceException {
		try {
			Double numeroPraticheChiuse = m.getPraticheChiuse();
			Double costoNomina = part.getCostoNomina();
			
			if (numeroPraticheChiuse!=null && costoNomina!=null)
				return numeroPraticheChiuse*costoNomina;
			
			return 0.0;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Calcola il carico chiuso percentuale come (caricoChiuso/caricoAtteso)*100 **/
	private Double caricoChiusoPerc(Monitor m, Partecipanti part) throws PersistenceException {
		try {
			Double caricoAtteso = m.getCaricoAtteso();
			Double caricoChiuso = m.getCaricoChiuso();
			
			if (caricoAtteso!=null && caricoAtteso!=0.0 && caricoChiuso!=null)
				return 100*caricoChiuso/caricoAtteso;
			
			return 0.0;
			
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/** Dato un progetto Associato, estrae la lista delle pratiche relative ad Ulss, LdL (+ eventuale sottotipo) che coinvolgono gli operatori sotto monitoraggio.
	 *  In base allo stato pratica, aggiorna gli attributi praticheAperte, praticheChiuse e percentualePraticheChiusela degli oggetti Monitor **/
	private void checkPratiche(ProgettoAssociato pa, List<Monitor> monitorList) throws PersistenceException {
		try {
			
			if (pa==null || pa.getProgetto()==null || monitorList==null || monitorList.size()<1)
				return;
			
			/** Tutte le pratiche devono essere associate ad un Distretto (Procpratiche.uoc), figlio della ulss della pianificazione in oggetto **/
			Long ulssId = pa.getPianificazione().getServiceDeliveryLocation().getInternalId();

			/** Recupero gli internalId degli operatori coinvolti (Procpratiche.operatori) **/
			List<Long> opInternalIds = new ArrayList<Long>();
			for (Monitor m:monitorList){
				if (m!=null){
					//Estraggo l'internalId dell'operatore coinvolto
					if (m.getOperatore()!=null)
						opInternalIds.add(m.getOperatore().getInternalId());
				}				
			}
			
			/** Estraggo Linea di lavoro ed eventuale sottoripo. Tutte le pratiche devono essere associate ad una 
			 *  linea di lavoro (Procpratiche.serviceDeliveryLocation.area.code) uguale alla linea di lavoro specificata nel Progetto Associato 
			 *  
			 *  Per la linea di lavoro Vigilanza dovrà coincidere anche il sottotipo (Procpratiche.vigilanza.type.code) 
			 *  Per la linea di lavoro Medicina del lavoro dovrà coincidere anche il sottotipo (Procpratiche.medicinaLavoro.type.code) **/
			String lineaDiLavoro = pa.getLineaDiLavoro()!=null?pa.getLineaDiLavoro().getCode():"";
			String subVigilanza = pa.getSubVigilanza()!=null?pa.getSubVigilanza().getCode():"";
			String subMdl = pa.getSubMdl()!=null?pa.getSubMdl().getCode():"";

			String qry = "SELECT DISTINCT prt FROM Procpratiche prt " + 
					"LEFT JOIN prt.uoc uoc " +
					"LEFT JOIN uoc.parent ulss " +
					"LEFT JOIN prt.serviceDeliveryLocation sdl " +
					"LEFT JOIN prt.operatori op ";
			 
			if ("SUPERVISION".equals(lineaDiLavoro))
				qry += "LEFT JOIN prt.vigilanza vig ";
			else if ("WORKMEDICINE".equals(lineaDiLavoro))
				qry += "LEFT JOIN prt.medicinaLavoro mdl ";
			
			qry += " WHERE ulss.internalId = :ulssId AND " +
					"sdl.area.code = :lineaDiLavoro AND " +
					"op.internalId IN (:opInternalIds) AND ";
			
			if ("SUPERVISION".equals(lineaDiLavoro))
				qry += "vig.type.code = :subVigilanza AND ";
			else if ("WORKMEDICINE".equals(lineaDiLavoro))
				qry += "mdl.type.code = :subMdl AND ";
			
			/** Estraggo tutte le pratiche con l'eccezione di quelle annullate
			 * 	Aperte 		- active
			 * 	Validate 	- verified
			 * 	Suspended 	- suspended
			 * 	Chiuse 		- completed
			 * 	Annullate 	- nullified
			 * 	Archiviate 	- held **/
			qry += "prt.statusCode.code IN ('active', 'verified', 'suspended', 'completed', 'held') AND " +
				   "prt.data IS NOT NULL AND prt.data <= :upperBound AND prt.data >= :lowerBound"; 

			Query queryPratiche = ca.createQuery(qry);
			
			queryPratiche.setParameter("ulssId", ulssId);
			queryPratiche.setParameter("lineaDiLavoro", lineaDiLavoro);
			queryPratiche.setParameter("opInternalIds", opInternalIds);
			
			if ("SUPERVISION".equals(lineaDiLavoro))
				queryPratiche.setParameter("subVigilanza", subVigilanza);
			else if ("WORKMEDICINE".equals(lineaDiLavoro))
				queryPratiche.setParameter("subMdl", subMdl);
			
			HashMap<String, Object> temp = this.getTemporary();
			Date lowerBound = (Date)temp.get("lowerBound");
			Date upperBound = (Date)temp.get("upperBound");
			
			queryPratiche.setParameter("upperBound", upperBound);
			queryPratiche.setParameter("lowerBound", lowerBound);

			@SuppressWarnings("unchecked")
			List<Procpratiche> prtLst = queryPratiche.getResultList();
			
			if (prtLst==null || prtLst.size()<1)
				return;
			
			//Itero la lista di Monitor per aggiornare lo stato di avanzamento (pratiche aperte, pratiche chiuse e % pratiche chiuse)
			for (Monitor m:monitorList){
				for (Procpratiche prt:prtLst){
					/* Se l'opetarore del monitoraggio attuale è coinvolto nella gestione della pratica, aggiorno lo stato di avanzamento in base allo stato pratica
					 * I00080334 (Punti 13.a e 13.b) - L'operatore è da considerarsi coinvolto nella pratica solo se è assegnato come RFP */
					if ( prt.getOperatori().contains(m.getOperatore()) && prt.getRfp()!=null && prt.getRfp().equals(m.getOperatore().getEmployee()) ){
						//I00080334 (Punto 13.a) - Pratiche chiuse (completed)
						if ("completed".equals(prt.getStatusCode().getCode())) {
							
							Double praticheChiuse = m.getPraticheChiuse();
							if (praticheChiuse==null)
								praticheChiuse=1.0;
							else 
								praticheChiuse++;
							
							m.setPraticheChiuse(praticheChiuse);
							
						} else {
							//I00080334 (Punto 13.b) - Pratiche aperte (non completed e non nullified)
							Double praticheAperte = m.getPraticheAperte();
							if (praticheAperte==null)
								praticheAperte=1.0;
							else 
								praticheAperte++;
							
							m.setPraticheAperte(praticheAperte);
						}
					
						//Aggiorno la % pratiche chiuse (100*praticheChiuse/praticheAttese)
						if (m.getPraticheAttese()!=null && m.getPraticheChiuse()!=null && m.getPraticheAttese()>0)
							m.setPraticheChiusePerc(100*m.getPraticheChiuse()/m.getPraticheAttese());	
					}
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getInfoMonitor() throws PersistenceException {
		try {			
			String info = "<p>" +
					"<b>Pratiche attese</b>   - Valore calcolato sul progetto.<br>" +
					"<b>Pratiche chiuse</b>   - Numero di pratiche chiuse, nell'intervallo considerato e per la linea di lavoro specificata nel progetto, in cui" +
					"l'operatore è assegnato come RFP.<br>" +
					
					"<b>% Pratiche chiuse</b> - 100 * Pratiche chiuse/Pratiche attese.<br>" +
					"<b>Carico atteso</b>     - Costo nomina * Numero pratiche attese (progetto).<br>" +
					"<b>Carico chiuso</b>     - Costo nomina * Numero pratiche chiuse.<br>" +
					"<b>% Carico chiuso</b>   - 100 * Carico chiuso/Carico atteso.<br>" +
					"<b>Pratiche aperte</b>   - Numero di pratiche aperte, nell'intervallo considerato e per la linea di lavoro specificata nel progetto, in cui" +
					"l'operatore assegnato come RFP<br>" +
					
					"<b>Carico aperto</b>     - Costo nomina * Numero pratiche aperte.<br>" +
					"<b>Media scostamento</b> - Carico aperto - Media carico aperto.<br>";
			
			return info;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
}

