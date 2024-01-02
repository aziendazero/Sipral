package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.paging.LazyList;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.ControlLsAction;
import com.phi.entities.actions.ControlLsReqAction;
import com.phi.entities.baseEntity.AccessoAtti;
import com.phi.entities.baseEntity.AcquisizioneInformazioni;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Commissioni;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.ControlLs;
import com.phi.entities.baseEntity.ControlLsReq;
import com.phi.entities.baseEntity.Misure;
import com.phi.entities.baseEntity.Oggetto;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.PersonaRuolo;
import com.phi.entities.baseEntity.PersoneCantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.Sopralluoghi;
import com.phi.entities.baseEntity.Vigilanza;
import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("AttivitaAction")
@Scope(ScopeType.CONVERSATION)
public class AttivitaAction extends BaseAction<Attivita, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7843158108875817401L;
	
	public static AttivitaAction instance() {
        return (AttivitaAction) Component.getInstance(AttivitaAction.class, ScopeType.CONVERSATION);
    }
	
    private static final Logger log = Logger.getLogger(AttivitaAction.class);
	
    public boolean isDeletable(Attivita att){
    	if(att!=null && att.getProvvedimenti()!=null){
    		for(Provvedimenti p : att.getProvvedimenti()){
    			if(Boolean.TRUE.equals(p.getIsActive())){
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
	public void copyAssignments(List<Operatore> list, Long idRdp, Long idRfp){
		try{
			Attivita attivita = getEntity();
			if (list != null && list.size() > 0 ) { 
				if (attivita.getPartecipanti() == null)
					attivita.setPartecipanti(new ArrayList<Partecipanti>());
						
				for (Operatore operatore : list) {
					//richiesta del 30/01/17: non importare l'rdp, se presente (ammesso che RDP ed RFP non coincidano)
					if (operatore.getIsActive()) {
						if (operatore.getEmployee()!=null && idRdp!=null && (operatore.getEmployee().getInternalId()!=idRdp || idRfp.equals(idRdp))){
							Partecipanti partecipante = new Partecipanti();
							
							partecipante.setOperatore(operatore);
							partecipante.setAttivita(attivita);
							attivita.getPartecipanti().add(partecipante);
						}
					}
				}
			}
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setAssignments(LazyList list){
		try{
			Attivita attivita = getEntity();
			if (list != null && list.size() > 0 ) {
				if (attivita.getPartecipanti() == null)
					attivita.setPartecipanti(new ArrayList<Partecipanti>());
				
				List<Partecipanti> partecipanti = attivita.getPartecipanti();
				
				boolean contain = false;
				
				for (Map operatore : (List<Map>)list) {
					if (operatore.get("isNew")!=null && (Boolean)operatore.get("isNew")){
						Long internalId = (Long)operatore.get("internalId");
						contain = false;
						for (Partecipanti partecipante : partecipanti)
							if (partecipante.getOperatore().getInternalId()==internalId){
								
								//Era stato rimosso dall'attività e adesso viene riagganciato
								//delete from partecipanti where is_active=false;
								if (!partecipante.getIsActive())
									partecipante.setIsActive(true);

								contain=true;
							}
						
						if (!contain){
							Partecipanti partecipante = new Partecipanti();
							Operatore op = (Operatore)ca.get(Operatore.class, (Long)operatore.get("internalId"));
							
							partecipante.setOperatore(op);
							partecipante.setAttivita(attivita);
							attivita.getPartecipanti().add(partecipante);
							
							//ca.create(partecipante);
						}

						operatore.put("IsNew",false);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public boolean contains(Long operatoreInternalId){
		try{
			Attivita attivita = getEntity();
			if (attivita!=null && attivita.getPartecipanti()!=null){
				List<Partecipanti> partecipanti = attivita.getPartecipanti();
			
				for (Partecipanti partecipante : partecipanti) {
					if (partecipante.getOperatore().getInternalId()==operatoreInternalId && partecipante.getIsActive())
						return true;
				}
			}
			
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/*public void removePartecipante(Partecipanti partecipante){
		try{
			Attivita attivita = getEntity();
			if ((attivita.getPartecipanti()!=null)&&(attivita.getPartecipanti().contains(partecipante)))
				attivita.getPartecipanti().remove(partecipante);
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}*/
	
	public String durata(Attivita attivita){
		try{
			String ret = "";
			if (attivita!=null) {

				Date dataInizio = attivita.getDataInizio();
				Date dataFine = attivita.getDataFine();
				if (dataInizio != null && dataFine != null){
					long diff = dataFine.getTime() - dataInizio.getTime();
				
					long diffInDays = (long) (diff / (1000 * 60 * 60 * 24));
					
					diff = diff - (diffInDays * 1000 * 60 * 60 * 24);
					long diffInHours = diff / (60 * 60 * 1000);
					
					if (diffInDays > 0)
						ret = diffInDays + " giorni";
					
					if (diffInHours > 0) {
						if (ret != "")
							ret += " e ";
						if (diffInHours == 1)
							ret += "1 ora";
						else 
							ret += diffInHours + " ore";
					}
				}
			}
		        
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Attivita copy(Attivita toCopy){
		try{
			Vocabularies vocabularies = VocabulariesImpl.instance();
			Attivita attivita = new Attivita();
			
			//Copia il legame con gli operatori
			List<Partecipanti> partecipanti = toCopy.getPartecipanti();
			if (partecipanti != null && partecipanti.size() > 0 ) {
				attivita.setPartecipanti(new ArrayList<Partecipanti>());				
				
				for (Partecipanti p : partecipanti) {
					if (p.getIsActive()){
						Partecipanti partecipante = new Partecipanti();
						
						partecipante.setOperatore(p.getOperatore());
						partecipante.setAttivita(attivita);
						attivita.getPartecipanti().add(partecipante);
						
						//ca.create(partecipante);
					}
				}
			}
		    
			//Copia i soggetti
			List<Soggetto> listSgg = toCopy.getSoggetto();
			if (listSgg != null && listSgg.size() > 0 ) { 
				attivita.setSoggetto(new ArrayList<Soggetto>());
				SoggettoAction soggettoAction = SoggettoAction.instance();
				Soggetto newSoggetto = new Soggetto();
				
				for (Soggetto soggetto : listSgg) {
					if (soggetto.getIsActive()){
						newSoggetto = soggettoAction.copy(soggetto);
						newSoggetto.setAttivita(attivita);
						
						ca.persist(newSoggetto);
						attivita.getSoggetto().add(newSoggetto);
					}
				}
			}
			
			//Copia gli oggetti
			List<Oggetto> listOgg = toCopy.getOggetti();
			if (listOgg != null && listOgg.size() > 0 ) { 
				attivita.setOggetti(new ArrayList<Oggetto>());
				OggettoAction oggettoAction = OggettoAction.instance();
				Oggetto newOggetto = new Oggetto();
				
				for (Oggetto oggetto : listOgg) {
					if (oggetto.getIsActive()){
						newOggetto = oggettoAction.copy(oggetto);
						newOggetto.setAttivita(attivita);
						
						attivita.getOggetti().add(newOggetto);
					}
				}
			}
			
			attivita.setCode(toCopy.getCode());
			attivita.setStatusCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "active"));							
			attivita.setDataInizio(new Date());
			attivita.setNote(toCopy.getNote());
			attivita.setStatusCode(toCopy.getStatusCode());
			if(toCopy.getAddr()!=null)
				attivita.setAddr(toCopy.getAddr().cloneAd());
			
			attivita.setLuogo(toCopy.getLuogo());
			attivita.setLuogoAltro(toCopy.getLuogoAltro());
			attivita.setLuogoCantiere(toCopy.getLuogoCantiere());
			attivita.setLuogoDitta(toCopy.getLuogoDitta());
			attivita.setLuogoSede(toCopy.getLuogoSede());
			attivita.setLuogoUtente(toCopy.getLuogoUtente());
			
			attivita.setOggettoVerifica(toCopy.getOggettoVerifica());
			attivita.setTipoAddr(toCopy.getTipoAddr());
			attivita.setTypeText(toCopy.getTypeText());
			//AttivitaAction.setNumeroVerbale(Protocollo.serviceDeliveryLocation.parent)
			
			return attivita;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void checkNewSubjects(List<Soggetto> soggettiList){
		try {
			Attivita attivita = getEntity();
			//Se il luogo è già settato, esci
			if (attivita.getLuogo() != null)
				return;
			
			Vocabularies vocabularies = VocabulariesImpl.instance();
			for (Soggetto s : soggettiList){
				CodeValue cv = s.getCode();
				if (s.getCode()!=null){
					String code = cv.getCode();
					
					//Setta il luogo con il primo soggetto di tipo Ditta, Cantiere o Altro
					if (code!=null){
						if (code.equals("Ditta")) {
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
							
							PersoneGiuridiche pg = s.getDitta();
							if (pg!=null)
								attivita.setLuogoDitta(pg);
							
							Sedi sd = s.getSede();
							if (sd!=null){
								attivita.setLuogoSede(sd);
								
								if (sd.getAddr()!=null)
									attivita.setAddr(sd.getAddr().cloneAd());
							}
							
							break;
							
						} else if (code.equals("Cantiere")){
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Cantiere"));
							
							Cantiere c = s.getCantiere();
							if (c!=null)
								attivita.setLuogoCantiere(c);
							
							if (c.getAddr()!=null)
								attivita.setAddr(c.getAddr().cloneAd());
							
							break;
						
						} else if (code.equals("Altro")){
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Altro"));
							
							AD ad = s.getAddr();
							if (ad!=null)
								attivita.setAddr(ad.cloneAd());
													
							break;
							
						}
					}
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void initializeSubjects(Procpratiche pp){
		try{
			//Recupera l'attività
			Attivita attivita = getEntity();
			
			//Recupera dalla pratica la comunicazione master 
			Protocollo pr = null;
			List<Protocollo> protocolloList = pp.getProtocollo();
			
			if (protocolloList!=null){
				if (protocolloList.size()==1)
					pr = protocolloList.get(0);
				else {
					for (Protocollo prMaster : protocolloList){
						if (prMaster.getIsMaster()){
							pr = prMaster;
							break;
						}
					}
				}
			}
			
			//Inizializza con una lista vuola la lista dei soggetti dell'attività
			Vocabularies vocabularies = VocabulariesImpl.instance();
			if (attivita.getSoggetto()==null)
				attivita.setSoggetto(new ArrayList<Soggetto>());
			
			String ubi = null;
			Vigilanza v = pp.getVigilanza();
			
			//Se la comunicazione master è stata trovata
			if (pr != null) {
				
				//Se NON si tratta di una vigilanza cantiere, valorizza il luogo a partire dall’ubicazione specificata sulla comunicazione
				if (v == null || v.getType() == null || !v.getType().equals("Yard")) {
					
					//Recupera la tipologia di ubicazione
					CodeValue ubicazione = pr.getUbicazione();	
					if (ubicazione != null && ubicazione.getCode() != null)
						ubi = ubicazione.getCode();
					
					/* Ubicazione: 
					 *
					 * Non previsto [NonPrevisto]
					 * 
					 * Ditta/Ente [Ditta]
					 * Cantiere [Cantiere]
					 * Altro [Altro]
					 * 
					 * */
					
					//Se l'ubicazione è stata settata ed è prevista
					if (ubi!=null && !ubi.equals("NonPrevisto")) {
						//Se l'ubicazione è una ditta
						if (ubi.equals("Ditta")){
												
							//Setta tipo luogo "Ditta"
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
							
							//Se è stata specificata la ditta, la aggancia
							if (pr.getUbicazioneDitta()!=null)
								attivita.setLuogoDitta(pr.getUbicazioneDitta());
							
							//Se è stata specificata la sede, la aggancia	
							if (pr.getUbicazioneSede()!=null)
								attivita.setLuogoSede(pr.getUbicazioneSede());
								
							/* Copiare indirizzo della sede? Non dovrebbe servire perchè questo indirizzo viene già copiato in luogoAltro dell'ubicazione ed è modificabile */
								
						//Se l'ubicazione è un cantiere									
						} else if (ubi.equals("Cantiere")){
							
							//Setta tipo luogo "Cantiere"
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Cantiere"));
							
							//Se è stato specificato un cantiere, la aggancia
							if (pr.getUbicazioneCantiere()!=null)
								attivita.setLuogoCantiere(pr.getUbicazioneCantiere());																		
						
						//Se l'ubicazione è Altro setta tipo luogo "Altro"
						} else if (ubi.equals("Altro"))
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Altro"));
						
						//Indipendentemente dal tipo di ubicazione, aggancia un nuovo soggetto a luogoAltro e lo valorizza
						//con l'ubicazione della segnalazione
						if (attivita.getLuogoAltro()==null)
							attivita.setLuogoAltro(new Soggetto());
						
						attivita.getLuogoAltro().setRiferimentoEntita(pr.getUbicazioneEntita());
						attivita.getLuogoAltro().setCodiceIMO(pr.getUbicazioneIMO());						
						attivita.getLuogoAltro().setTarga(pr.getUbicazioneTarga());
						
						/* Latitudine, longitudine e "UbicazioneSpec" NON sono attualmente presenti in luogo
						attivita.getLuogoAltro().set...(pr.getUbicazioneSpec());						
						attivita.getLuogoAltro().set...(pr.getUbicazioneX());
						attivita.getLuogoAltro().set...(pr.getUbicazioneY()); */					
						if (pr!=null && pr.getUbicazioneAddr()!=null)						
							attivita.setAddr(pr.getUbicazioneAddr().cloneAd());
						
						if(pr!=null){
							if(pr.getUbicazioneX()!=null) attivita.setLatitudine(pr.getUbicazioneX());
							if(pr.getUbicazioneY()!=null) attivita.setLongitudine(pr.getUbicazioneY());
						}
					}
				}
				
				String rif =null;
				
				//Estrae il codice che indica la tipologia di riferimento
				CodeValue riferimento = pr.getRiferimento();	
				if (riferimento != null && riferimento.getCode() != null)
					rif = riferimento.getCode();
							
				if (rif != null) {
					//Se il riferimento è una ditta e la ditta è stata selezionata
					if (rif.equals("Ditta") && pr.getRiferimentoDitta()!=null){
						
						//Recupera la persona giuridica e se non è già presente tra i soggetti dell'attività, la aggiunge 
						PersoneGiuridiche pg = pr.getRiferimentoDitta();
						if (this.checkSoggetto(attivita.getSoggetto(), pg)){
							Boolean toSetLuogo = false;
							
							Soggetto soggetto = new Soggetto();
							soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));							
							soggetto.setAttivita(attivita);
							soggetto.setDitta(pg);
							soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "azienda"));
							
							//Se non è ancora stato settato il luogo, lo setta di tipo ditta e associa la persona giuridica
							if (attivita.getLuogo() == null){
								attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
								attivita.setLuogoDitta(pg);
								
								toSetLuogo = true;
							}
							
							//Se è stata definita anche la sede, la aggiunge al soggetto 
							if (pr.getRiferimentoSede()!=null){
								Sedi s = pr.getRiferimentoSede();
								soggetto.setSede(s);
								
								//Se ha settato la ditta come luogo dell'attività e c'è anche una sede, la associa al luogo dell'attività
								if (toSetLuogo)
									attivita.setLuogoSede(s);
								
								//Se il riferimento sede ha un indirizzo, lo clona e lo associa all'indirizzo del soggetto
								if (pr.getRiferimentoSede().getAddr()!=null){
									soggetto.setAddr(pr.getRiferimentoSede().getAddr().cloneAd());
									
									//Se ha settato la ditta come luogo, c'è anche la sede e la sede ha un indirizzo, lo clona e lo associa al luogo dell'attività
									if (toSetLuogo){
										attivita.setAddr(pr.getRiferimentoSede().getAddr().cloneAd());
										if(pr.getRiferimentoSede().getLatitudine()!=null)attivita.setLatitudine(pr.getRiferimentoSede().getLatitudine());
										if(pr.getRiferimentoSede().getLongitudine()!=null)attivita.setLongitudine(pr.getRiferimentoSede().getLongitudine());
										
									}

								}
							}
							
							//Aggiunge il soggetto all'attività
							ca.persist(soggetto);
							attivita.getSoggetto().add(soggetto);
						}
					
					//Se il riferimento è un cantiere, il cantiere è stato selezionato e non è stato ancora specificato il luogo	
					} else if (rif.equals("Cantiere") && pr.getRiferimentoCantiere()!=null && attivita.getLuogo() == null) {
						
						//Setta il luogo di tipo Cantiere
						attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Cantiere"));
						
						//Recupera il cantiere e lo associa al luogo
						Cantiere c = pr.getRiferimentoCantiere();
						attivita.setLuogoCantiere(c);
					
						//Se il cantiere ha un indirizzo, lo clona e lo associa al luogo dell'attività
						if (c.getAddr()!=null){
							attivita.setAddr(c.getAddr().cloneAd());
							if(c.getLatitudine()!=null)attivita.setLatitudine(c.getLatitudine());
							if(c.getLongitudine()!=null)attivita.setLongitudine(c.getLongitudine());
						}
						
						/* NON crea e NON aggiunge all'attività un soggetto di tipo cantiere */
						
					} else if (rif.equals("Altro") && attivita.getLuogo() == null) {
						
						//Setta il luogo di tipo Altro
						attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Altro"));
						
						/* NON crea e NON aggiunge all'attività un soggetto di tipo altro */
						
					//Se il riferimento è un utente e l'utente è stato selezionato	
					} else if (rif.equals("Utente") && pr.getRiferimentoUtente()!=null) {
						
						//Recupera la persona e se non è già presente tra i soggetti dell'attività, la aggiunge 
						Person u = pr.getRiferimentoUtente();
						if (this.checkSoggetto(attivita.getSoggetto(), u)){
							String workingLine = "";
							ServiceDeliveryLocation sdl = pp.getServiceDeliveryLocation();
							
							Soggetto soggetto = new Soggetto();
							soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));	
							soggetto.setAttivita(attivita);
							soggetto.setUtente(u);
							
							if (sdl != null && sdl.getArea() != null)
								workingLine = sdl.getArea().getCode();
							
							//Se siamo in una pratica di Medicina del Lavoro - setta ruolo paziente
							if (workingLine!=null && "WORKMEDICINE".equals(workingLine))
								soggetto.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "paziente"));
							
							else
								//Setta ruolo lavoratore
								soggetto.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "lavoratore"));	
														
							ca.persist(soggetto);
							attivita.getSoggetto().add(soggetto);
						}
					}
				}
			}
			
			//Se si tratta di una Vigilanza
			if (v!=null && v.getType()!=null) {
				String type = v.getType().getCode();
				
				//Se vigilanza ditte (Generic) o cantiere (Yard)
				if (type.equals("Generic") || type.equals("Yard")) {
					List<PersonaGiuridicaSede> pgsList = v.getPersonaGiuridicaSede();
					
					if (pgsList != null) {
						for (PersonaGiuridicaSede pgs : pgsList) {
							
							/* Se vigilanza cantiere considera solo le imprese (pgs) con il check "controllate" a true 
							 * 
							 * OPPURE
							 * 
							 * Se vigilanza aziende prende tutte le aziende */
							if ((type.equals("Yard") && pgs!=null && pgs.getChecked()!=null && pgs.getChecked()) || type.equals("Generic")){
								if (pgs.getPersonaGiuridica()!=null && this.checkSoggetto(attivita.getSoggetto(), pgs.getPersonaGiuridica())) {
									Boolean toSetLuogo = false;
									
									Soggetto soggetto = new Soggetto();
									soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));							
									soggetto.setAttivita(attivita);
									soggetto.setDitta(pgs.getPersonaGiuridica());
									
									if (attivita.getLuogo() == null){
										attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
										attivita.setLuogoDitta(pgs.getPersonaGiuridica());
										
										toSetLuogo = true;
									}
							
									if (pgs.getSede()!=null){
										soggetto.setSede(pgs.getSede());
										
										//Associa al luogo la sede
										if (toSetLuogo)
											attivita.setLuogoSede(pgs.getSede());
									
										if(pgs.getSede().getAddr()!=null){
											soggetto.setAddr(pgs.getSede().getAddr().cloneAd());
											
											//Copia l'indirizzo della sede nel luogo dell'attivita (Addr)
											if (toSetLuogo){
												attivita.setAddr(pgs.getSede().getAddr().cloneAd());
												if(pgs.getSede().getLatitudine()!=null)attivita.setLatitudine(pgs.getSede().getLatitudine());
												if(pgs.getSede().getLongitudine()!=null)attivita.setLongitudine(pgs.getSede().getLongitudine());
											}
											
										}
									}
									
									/* Rimappa il ruolo di PersonaGiuridicaSede.tipoDitta [RuoloImpresa]
									 		- Impresa Affidataria 	(RUOLOIMP1)
											- Impresa Esecutrice  	(RUOLOIMP2)
									 		- Lavoratore Autonomo	(RUOLOIMP3)
											- Fornitore				(RUOLOIMP4)
								 			- Partecipe al cantiere	(RUOLOIMP5)
											
										in Soggetto.tipoDitta [CompanyType]
											- Azienda 				(azienda)
											- Fornitore				(fornitore)
											- Impresa affidataria 	(impresaaffidataria)	
											- Impresa appaltatrice 	(impresaappaltatrice)
											- Impresa esecutrice	(impresaesecutrice)
											- Impresa familiare		(impresafamiliare)	*/
									
									CodeValuePhi td = pgs.getTipoDitta();
									if (td != null){
										String tipoDitta = td.getCode();
									
										if (tipoDitta!=null) {
											if ("RUOLOIMP1".equals(tipoDitta))
												soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "impresaaffidataria"));
											
											else if ("RUOLOIMP2".equals(tipoDitta))
												soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "impresaesecutrice"));
											
											else if ("RUOLOIMP3".equals(tipoDitta))
												soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "lavoratoreautonomo"));
											
											else if ("RUOLOIMP4".equals(tipoDitta))
												soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "fornitore"));
											
											else //if ("RUOLOIMP5".equals(tipoDitta))
												soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "azienda"));
										}
									}
	
									ca.persist(soggetto);
									attivita.getSoggetto().add(soggetto);
							}
						}
					}

					List<PersonaRuolo> prList = v.getPersonaRuolo();
					for (PersonaRuolo prl : prList) {
						if (prl.getPerson()!=null && this.checkSoggetto(attivita.getSoggetto(), prl.getPerson())) {
							Soggetto soggetto = new Soggetto();

							soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));	
							soggetto.setAttivita(attivita);
							soggetto.setUtente(prl.getPerson());
							soggetto.setRuolo(prl.getRuolo());
										
							ca.persist(soggetto);
							attivita.getSoggetto().add(soggetto);
						}
					}
				}
				
				if (type.equals("Yard")) {
					
					//Vigilanza cantiere - gestione delle persone fisiche e giuridiche coinvolte
					Cantiere c = v.getCantiere();
					if (c!=null && this.checkSoggetto(attivita.getSoggetto(), c)) {
						
						//Se non è ancora stato settato il luogo, lo setta come cantiere e associa associa il cantiere
						if (attivita.getLuogo() == null){
							attivita.setLuogo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Cantiere"));
							attivita.setLuogoCantiere(c);
							if (c.getAddr()!=null){
								attivita.setAddr(c.getAddr().cloneAd());
								if(c.getLatitudine()!=null)attivita.setLatitudine(c.getLatitudine());
								if(c.getLongitudine()!=null)attivita.setLongitudine(c.getLongitudine());
							}
						}
						
						/* Da mantis 34287 - NON aggiungere il cantiere ai soggetti dell'attività */
						
						//Aggiungere ai soggetti il/i committenti
						List<Committente> commList = c.getCommittente();
						if (commList!=null){
							for (Committente comm : commList) {
								if (comm!=null)
									if (comm.getPerson()!=null  && this.checkSoggetto(attivita.getSoggetto(), comm.getPerson())) {
										Soggetto soggettoCommUtente = new Soggetto();

										soggettoCommUtente.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));	
										soggettoCommUtente.setAttivita(attivita);
										soggettoCommUtente.setUtente(comm.getPerson());
											
										soggettoCommUtente.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "committente"));	
										ca.persist(soggettoCommUtente);
										attivita.getSoggetto().add(soggettoCommUtente);
										
									} else {
										if (comm.getPersoneGiuridiche()!=null && this.checkSoggetto(attivita.getSoggetto(), comm.getPersoneGiuridiche())) {
											Soggetto soggettoCommDitta = new Soggetto();

											soggettoCommDitta.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));							
											soggettoCommDitta.setAttivita(attivita);
											soggettoCommDitta.setDitta(comm.getPersoneGiuridiche());
											
											soggettoCommDitta.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "committente"));	
											ca.persist(soggettoCommDitta);
											attivita.getSoggetto().add(soggettoCommDitta);
										}
									}
							}
						}
						
						List<PersoneCantiere> respList = c.getPersoneCantiere();
						/* Aggiungere ai soggetti dell'attività elementare le persone fisiche associate al cantiere 
						   PersoneCantiere.ruolo [RuoloInCantiere]
								- Responsabile lavori 						 	(RUOLOCANT01)
							  	- Coordinatore lavori durante progettazione 	(RUOLOCANT02)
								- Coordinatore lavori durante realizzazione 	(RUOLOCANT03) 
						
						rimappando i codici in Soggetto.ruolo [Roles]
								- Responsabile lavori(rl)
								- Coordinatore lavori durante progettazione (coordinatoreprogetto)
								- Coordinatore lavori durante realizzazione (coordinatoreesecuzione)
						 */
						
						if (respList!=null){
							for (PersoneCantiere resp : respList) {
								if (resp!=null){
									if (resp.getPerson()!=null  && this.checkSoggetto(attivita.getSoggetto(), resp.getPerson())) {
										Soggetto soggettoResponsabileUtente = new Soggetto();

										soggettoResponsabileUtente.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));	
										soggettoResponsabileUtente.setAttivita(attivita);
										soggettoResponsabileUtente.setUtente(resp.getPerson());
										
										CodeValuePhi rl = resp.getRuolo();
										if (rl != null){
											String role = rl.getCode();
											if (role!=null && role!="") {
												if ("RUOLOCANT01".equals(role))
													soggettoResponsabileUtente.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "rl"));		
													
												else if ("RUOLOCANT02".equals(role))
													soggettoResponsabileUtente.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "coordinatoreprogetto"));		
													
												else if ("RUOLOCANT03".equals(role))
													soggettoResponsabileUtente.setRuolo(vocabularies.getCodeValueCsDomainCode("PHIDIC", "Roles", "coordinatoreesecuzione"));											
											}
										}
										ca.persist(soggettoResponsabileUtente);
										attivita.getSoggetto().add(soggettoResponsabileUtente);
										
									}
								}
							}
						}
					}
				}
				
				/*//Mantis 29994 - non serve visualizzare le imprese
					List<DitteCantiere> dcList = c.getDitteCantiere();
					for (DitteCantiere dc : dcList) {
						if (dc.getPersoneGiuridiche()!=null) {
							Soggetto soggetto = new Soggetto();
							
							soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));							
							soggetto.setAttivita(attivita);
							soggetto.setDitta(dc.getPersoneGiuridiche());

							attivita.getSoggetto().add(soggetto);
						}
					}*/
			
			//Se vigilanza amianto (Asbestos) recupera le aziende e le aggiunge come soggetto
			} else if (type.equals("Asbestos")) {
					List<PersonaGiuridicaSede> pgsList = v.getPersonaGiuridicaSede();
					if (pgsList!=null && pgsList.size()>0){
						PersonaGiuridicaSede pgs = pgsList.get(0);
						if (pgs.getPersonaGiuridica()!=null && this.checkSoggetto(attivita.getSoggetto(), pgs.getPersonaGiuridica())) {
							Soggetto soggetto = new Soggetto();
							
							soggetto.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));							
							soggetto.setAttivita(attivita);
							soggetto.setDitta(pgs.getPersonaGiuridica());
							
							soggetto.setTipoDitta(vocabularies.getCodeValueCsDomainCode("PHIDIC", "CompanyType", "azienda"));
						
							if (pgs.getSede()!=null){
								soggetto.setSede(pgs.getSede());
							
								if (pgs.getSede().getAddr()!=null)
									soggetto.setAddr(pgs.getSede().getAddr().cloneAd());
							}
							ca.persist(soggetto);
							attivita.getSoggetto().add(soggetto);
						}
					}
					
					
				}
				
			} 
					
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	//Return true if obj is not in soggetti jet
	public boolean checkSoggetto(List<Soggetto> soggetti, Object obj){
		try{
			
			if (soggetti==null || obj==null)
				return true;
			
			for (Soggetto sgt : soggetti){
				if (sgt!=null){
					CodeValue cv = sgt.getCode();
					if (cv!=null && cv.getCode()!=null) {
						String code = cv.getCode();
						
						if (code.equals("Ditta") && (obj instanceof PersoneGiuridiche) && sgt.getDitta().equals((PersoneGiuridiche)obj))
							return false;
						else if (code.equals("Cantiere") && (obj instanceof Cantiere) && sgt.getCantiere().equals((Cantiere)obj))
							return false;
						if (code.equals("Utente") && (obj instanceof Person) && sgt.getUtente().equals((Person)obj))
							return false;
					}
				}
			}
			
			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setLuogo(Soggetto sgt){
		try{
			Attivita att = getEntity();
			
			//Se è già stato inserito il luogo, non fare nulla
			if (att==null || att.getLuogo()!=null)
				return;
			
			//Se il soggetto è un utente, non fare nulla
			if (sgt==null || (sgt.getCode()!=null && sgt.getCode().getCode().equals("Utente")))
				return;
			
			else {
				//Setta IL tipo soggetto IN tipo luogo
				att.setLuogo((CodeValuePhi)sgt.getCode());
				
				//Nei casi in cui il soggetto sia una Ditta, un Cantiere o Altro
				//copia le informazioni di interesse in luogo 
				String sgtType = sgt.getCode().getCode();
				
				if (sgtType.equals("Ditta")) {
					if (sgt.getDitta()!=null)
						att.setLuogoDitta(sgt.getDitta());
					
					if (sgt.getSede()!=null) {
						att.setLuogoSede(sgt.getSede());
						
						if (sgt.getSede().getAddr()!=null)
							att.setAddr(sgt.getSede().getAddr().cloneAd());
					}
					
				} else if (sgtType.equals("Cantiere")) {
					if (sgt.getCantiere()!=null){
						att.setLuogoCantiere(sgt.getCantiere());
					
						if (sgt.getCantiere().getAddr()!=null)
							att.setAddr(sgt.getCantiere().getAddr().cloneAd());
							
					}
					
				} else if (sgtType.equals("Altro")) {
					Soggetto luogoAltro = new Soggetto();
					if (sgt.getRiferimentoEntita()!=null)
						luogoAltro.setRiferimentoEntita(sgt.getRiferimentoEntita());
					if (sgt.getTarga()!=null)
						luogoAltro.setTarga(sgt.getTarga());
					
					if (sgt.getCodiceIMO()!=null)
						luogoAltro.setCodiceIMO(sgt.getCodiceIMO());
					
					if (sgt.getAddr()!=null)
						att.setAddr(sgt.getAddr().cloneAd());
					
					att.setLuogoAltro(luogoAltro);
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	/* Medicina del Lavoro - Gestione del luogo per Visite e Accertamenti (attività elementari)*/
	public void setLuogoMdl(String subType){
		try{
			Attivita att = getEntity();
			Procpratiche p = att.getProcpratiche();
			
			/* Indirizzo che potrei dover brasare */
			AD ad = null;
			if (att!=null) 
				ad = att.getAddr();
			
			/* Indirizzo della linea di lavoro (ServiceDeliveryLocation) */
			AD adSdl = null;
			if (p!=null && p.getServiceDeliveryLocation()!=null)
				adSdl = p.getServiceDeliveryLocation().getAddr();
	
			/* Per attività elementari (Visita o Accertamento) interne (subType eq 01) */
			if ("01".equals(subType)) {
				/* se non c'è lo stesso indirizzo della sdl */
				if (ad!=null && adSdl!=null && !ad.toString().equals(adSdl.toString())){
					att.setAddr(p.getServiceDeliveryLocation().getAddr().cloneAd());
				}
			
			/* Per attività elementari (Visita o Accertamento) esterne (subType ne 01) 
			 * o per le quali non è ancora stato specificato il tipo */
			} else { 
				/* Se c'è ancora l'indirizzo della linea di lavoro (ServiceDeliveryLocation) - brasalo di cattiveria */
				if (ad!=null && adSdl!=null && ad.toString().equals(adSdl.toString()))
					att.setAddr(new AD());
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setNumeroVerbale(ServiceDeliveryLocation ulss){
		try{
			if (ulss==null)
				return;//
			
			String evaluateNumber = "select count(a) from Attivita a " +
					"where a.procpratiche.uoc.parent.internalId=:internalID";
					//+ " and a.creationDate  >= :minDate";
			
			/*Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);*/

			//Date date = cal.getTime();
			
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", ulss.getInternalId());
			//parameters.put("minDate", date);
						
			@SuppressWarnings("unchecked")
			List<Long> aList = (List<Long>) ca.executeHQLwithParameters(evaluateNumber, parameters);
			
			Long numero = aList.get(0);
			
			if (numero!=null){
				Attivita attivita = getEntity();
				attivita.setNumero(numero + 1);
			}
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public boolean checkAttivita(String workingLine){
		boolean isValid = true;
		Attivita att = getEntity();
		String tipo = "";
		
		//Essendoci un blocco applicativo non dovrebbe mai verificarsi
		if (att.getCode()==null){
			FacesErrorUtils.addErrorMessage("commError", "Tipo attività obbligatoria.", "Tipo attività obbligatoria.");
			 return isValid = false;
		} 
		
		if (att.getDataInizio()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data inizio obbligatoria.", "Data inizio obbligatoria.");
			isValid = false;
		}
		
		if (att.getPartecipanti()==null || att.getPartecipanti().size()<1) {
			FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un operatore.", "Inserire almeno un operatore.");
			isValid = false;
		} else {
			boolean oneActive = false;
			for (Partecipanti part : att.getPartecipanti()) {
				if (part.getIsActive())
					oneActive = true;
			}
			
			if (!oneActive){
				isValid = false;
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un operatore.", "Inserire almeno un operatore.");
			}
		}
		
		if (att.getCode().getCode()!=null)
			tipo = att.getCode().getCode();
		
		if (tipo.equals("accessoatti")) {
			AccessoAtti aa = att.getAccessoAtti();
			
			if (aa==null) {
				FacesErrorUtils.addErrorMessage("commError", "Accesso agli atti assente.", "Accesso agli atti assente.");
				return isValid = false;
			}
			
			if (aa.getRichiedenteCv()==null && (aa.getRichiedente()==null || aa.getRichiedente()=="")) {
				FacesErrorUtils.addErrorMessage("commError", "Mittente obbligatorio", "Mittente obbligatorio.");
				isValid = false;
			}
			
		} else if (tipo.equals("acquisizionedocumentale")) {
			if (!"WORKMEDICINE".equals(workingLine)){
				AcquisizioneInformazioni aa =att.getAcquisizioneInformazioni();
				if (aa==null || aa.getDocumentiVisionati()==null || aa.getDocumentiVisionati().size()<1){
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un documento.", "Inserire almeno un documento.");
					isValid = false;
				}
/*				if (att.getOggetti()==null || att.getOggetti().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un oggetto.", "Inserire almeno un oggetto.");
					isValid = false;
				}*/
			}

		} else if (tipo.equals("sit")) {
			
			if (!isValidList(att.getSoggetto())) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un soggetto.", "Inserire almeno un soggetto.");
				isValid = false;
			}

		} else if (tipo.equals("commissione")) {
			Commissioni comm = att.getCommissione();
			
			if (comm==null) {
				FacesErrorUtils.addErrorMessage("commError", "Commissione assente.", "Commissione assente.");
				return isValid = false;
			}
			
			if (comm.getTipoCommissione()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Tipologia obbligatoria", "Tipologia obbligatoria.");
				isValid = false;
			}

/*		} else if (tipo.equals("incontri")) {
			if (!"WORKMEDICINE".equals(workingLine)) 
				if (att.getOggetti()==null || att.getOggetti().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un oggetto.", "Inserire almeno un oggetto.");
					isValid = false;
				}	*/

		} else if (tipo.equals("rilevazione")) {
			/*	RilevazioniAmbientali ril = att.getRilevazioneAmbientale();
			if (ril==null) {
				FacesErrorUtils.addErrorMessage("commError", "Rilevazione ambientale assente.", "Rilevazione ambientale assente.");
				return isValid = false;
			} 	*/
			
			if (att.getMisure()==null || att.getMisure().size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno una misura", "Inserire almeno una misura");
				isValid = false;
			} else {
				boolean oneActive = false;
				for (Misure misura : att.getMisure()) {
					if (misura.getIsActive())
						oneActive = true;
				}
				
				if (!oneActive){
					isValid = false;
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno una misura.", "Inserire almeno una misura.");
				}
			}
			
			if (att.getLuogoDitta()==null && att.getLuogoCantiere()==null && att.getLuogoAltro()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un luogo", "Inserire almeno un luogo");
				isValid = false;
			}
			
		} else if (tipo.equals("sequestro")) {
			
			if (att.getLuogoDitta()==null && att.getLuogoCantiere()==null && att.getLuogoAltro()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un luogo", "Inserire almeno un luogo");
				isValid = false;
			}
			
		} else if (tipo.equals("sopralluogo")) {
			if (!"WORKMEDICINE".equals(workingLine)) 			
				if (att.getOggetti()==null || att.getOggetti().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un oggetto", "Inserire almeno un oggetto");
					isValid = false;
				}
			
			if (att.getLuogoDitta()==null && att.getLuogoCantiere()==null && att.getLuogoAltro()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un luogo", "Inserire almeno un luogo");
				isValid = false;
			}
			
			Sopralluoghi sopralluogo = att.getSopralluogo();
			
			if (sopralluogo==null) {
				FacesErrorUtils.addErrorMessage("commError", "Sopralluogo assente.", "Sopralluogo assente.");
				return isValid = false;
			}
			
			if (sopralluogo.getTipoSopralluogo()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Tipo sopralluogo obbligatorio.", "Tipo sopralluogo obbligatorio.");
				isValid = false;
			}
			
			// I0062097
			if("SUPERVISION".equals(workingLine)){
				if(sopralluogo.getMotivoNonSPI()==null){
					
					List<Soggetto> soggetti = att.getSoggetto();
					
					if(!isValidList(soggetti) || !isAnyDitta(soggetti)){
						FacesErrorUtils.addErrorMessage("commError", "Almeno un soggetto deve essere persona giuridica.", "Almeno un soggetto deve essere persona giuridica.");
						isValid = false;						
					}
				}
			}
			
		} else if (tipo.equals("visitaMedica")) {
			VisitaMdl visita = null;
			if (att.getVisitaMdl()!=null && att.getVisitaMdl().size()>0)
				visita = att.getVisitaMdl().get(0);
			
			if (visita==null) {
				FacesErrorUtils.addErrorMessage("commError", "Visita medica assente.", "Visita medica assente.");
				return isValid = false;
			}
			
			if (visita.getCode()!=null && "01".equals(visita.getCode().getCode()) && visita.getVisitaSp()!=null && visita.getVisitaSp().getRiferimentoInterno()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Operatore obbligatorio.", "Operatore obbligatorio.");
				isValid = false;
			}
			
/*			if (visita.getMotivo()==null || visita.getMotivo()=="") {
				FacesErrorUtils.addErrorMessage("commError", "Motivo della visita obbligatorio.", "Motivo della visita obbligatorio.");
				isValid = false;
			}
			
			if (visita.getMedico()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Medico obbligatorio.", "Medico obbligatorio.");
				isValid = false;
			}
			
			if (att.getSoggetto()==null || att.getSoggetto().size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un soggetto.", "Inserire almeno un soggetto.");
				isValid = false;
			}	*/
			
		}

		return isValid;
	}
	
	//I0062097
	public boolean isAnyDitta(List<Soggetto> soggetti){
		int i=0;
		boolean anyDitta = false;
		
		do {
			Soggetto sogg = soggetti.get(i);
			if(sogg!=null && sogg.getIsActive() && sogg.getCode()!=null && "Ditta".equals(sogg.getCode().getCode()))
				anyDitta=true;
			
			i++;
		} while(!anyDitta && i<soggetti.size());

		return anyDitta;
	}
	
	public boolean isValidList(List<Soggetto> soggetti){
		
		if (soggetti==null || soggetti.isEmpty())
			return false;
		
		for (Soggetto soggetto:soggetti){
			if (soggetto!=null && soggetto.getIsActive())
				return true;
		}
		
		return false;
	}
	
	public void copySoggFromProtocollo(Protocollo protocollo) throws PersistenceException, DictionaryException{
		Vocabularies vocabularies = VocabulariesImpl.instance();
		
		/*
		 * H0081779 - Le pratiche importate spesso non hanno Protocollo
		 */
		if(protocollo==null){
			return;
		}
		PersoneGiuridiche ditta = protocollo.getUbicazioneDitta();
		if(ditta==null)
			return;

		Attivita att = getEntity();
		Soggetto sogg = new Soggetto();
		//SoggettoAction soggAct = (SoggettoAction)Component.getInstance(sogg.getClass().getSimpleName()+"Action");
		if(protocollo.getUbicazione()!=null){
			sogg.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", protocollo.getUbicazione().getCode()));
		}
		sogg.setDitta(ditta);

		Sedi sede = protocollo.getUbicazioneSede();
		if(sede != null)
			sogg.setSede(protocollo.getUbicazioneSede());

		/*
		 * H0080636 - soggetti duplicati: se sto per inserire un soggetto già presente salto.
		 */
		if(att.getSoggetto()!=null){
			for(Soggetto s : att.getSoggetto()){
				if(s.getCode()!=null && sogg.getCode()!=null && s.getCode().getCode().equals(sogg.getCode().getCode()) && sogg.getDitta().equals(ditta) 
						|| (sogg.getSede()!=null && sogg.getSede().equals(sede))){
					sogg=null;
					return;
				}
			}
		}
		
		
		sogg.setAttivita(att);

		sogg.setAcquisizioneInformazioni(null); 
		sogg.setAddr(protocollo.getUbicazioneAddr().cloneAd()); 
		sogg.setCantiere(null); 
		sogg.setCodiceIMO(""); 
		sogg.setDittaUtente(null); 
		sogg.setDocumenti(null); 
		sogg.setLifestyle(null); 
		sogg.setMedico(null); 
		sogg.setProvvedimenti(null); 
		sogg.setRiferimentoDenominazione(null); 
		sogg.setRiferimentoEntita(null); 
		sogg.setRiferimentoNote(null); 
		sogg.setRuolo(null); 
		sogg.setSedeUtente(null); 
		sogg.setTarga(null); 
		sogg.setTipoCantiere(null); 
		sogg.setTipoDitta(null); 
		sogg.setUtente(null);

		ca.persist(sogg);
		att.getSoggetto().add(sogg);
	}
	
	public void prepareList(Procpratiche p) throws Exception {
		if(p==null)
			return;

		ControlLsReqAction clReqAction = ControlLsReqAction.instance();
		clReqAction.ejectList();
		
		//search for required check lists..
		ControlLsAction clAction = ControlLsAction.instance();
		clAction.cleanRestrictions();
		clAction.getEqual().put("workingLine.code", p.getServiceDeliveryLocation().getArea().getCode());
		clAction.getEqual().put("isActive", true);
		clAction.getLessEqual().put("startValidity", new Date());
		((FilterMap)clAction.getGreaterEqual()).putOr("endValidity", new Date(), null);
		List<ControlLs> questionariPrevisti = clAction.select();
		
		if(questionariPrevisti!=null && !questionariPrevisti.isEmpty())
			clAction.injectList(questionariPrevisti);
	}
	
	public void prepareReq(ControlLs controlLs) throws Exception {
		ControlLsReqAction clReqAction = ControlLsReqAction.instance();
		ControlLsReq controlLsReq = clReqAction.copyListDom(controlLs);
			
		if(controlLsReq!=null){
			Attivita a = getEntity();
			a.setControlLsReq(controlLsReq);
			
			List<Attivita> aList = new ArrayList<Attivita>();
			aList.add(a);
				
			controlLsReq.setAttivita(aList);
			
			clReqAction.inject(controlLsReq);
		}
	}

	
}