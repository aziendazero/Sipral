package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Vigilanza;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("VigilanzaAction")
@Scope(ScopeType.CONVERSATION)
public class VigilanzaAction extends BaseAction<Vigilanza, Long> {

	private static final long serialVersionUID = 1465437212732550786L;
	
    public static VigilanzaAction instance() {
        return (VigilanzaAction) Component.getInstance(VigilanzaAction.class, ScopeType.CONVERSATION);
    }
    
    private static final Logger log = Logger.getLogger(VigilanzaAction.class);
	
    private static String SUBSTANCE_COUNTER = "SELECT COUNT(s) FROM Sostanze s " +
			"JOIN s.schedaEsposti se JOIN se.personeGiuridiche pg " +
			"WHERE pg.internalId=:internalID AND s.isActive = 1 AND se.isActive = 1";

	public boolean substances(DitteCantiere dc){
		try{
			if (dc == null || dc.getPersoneGiuridiche()==null)
				return false;
					
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", dc.getPersoneGiuridiche().getInternalId());
			
			@SuppressWarnings("unchecked")
			List<Long> substanceCounter = (List<Long>) ca.executeHQLwithParameters(SUBSTANCE_COUNTER, parameters);
			
			long counter =((Long)substanceCounter.get(0));
			
			if (counter>0)
				return true;
			
			return false;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}
	
	public void checkProtocollo(Protocollo protocollo, DettagliBonifiche dettagliBonifiche){
		try{
			//Controlla che la comunicazione oggetto di modifica sia la comunicazione principale
			if (protocollo==null || !protocollo.getIsMaster())
				return;
			
			//Controlla che la comunicazione sia di tipo SUPERVISION (Vigilanza Spisal)
			ServiceDeliveryLocation uos = protocollo.getUos();
			if (uos == null)
				return;
					
			CodeValue area = uos.getArea();
			if (area == null || !"SUPERVISION".equals(area.getCode()))
				return;
			
			//Controlla che la comunicazione oggetto di modifica sia in stato assegnata (completed)
			if (protocollo.getStatusCode()==null || protocollo.getStatusCode().getCode()==null || !"completed".equals(protocollo.getStatusCode().getCode()))
				return;
			
			//Aggiorna i riferimenti
			this.copyFrom(protocollo, dettagliBonifiche);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Copy data from Protocollo and DettagliBonifiche to Vigilanza 
	 * only if workline is SUPERVISION -> Verifica amianto
	 * vedi: http://support.insielmercato.it/mantis/view.php?id=29994 -> Vigilanza_18_10_2016.docx 
	 * @param protocollo
	 * @param dettagliBonifiche
	 * @throws PhiException 
	 */
	public void copyFrom(Protocollo protocollo, DettagliBonifiche dettagliBonifiche) throws PhiException {
		
		try {
			if (protocollo == null || protocollo.getUos() == null  || protocollo.getUos().getArea() == null || protocollo.getCode() == null)
				return;
			
			//Linea di lavoro Vigilanza spisal
			if ("SUPERVISION".equals(protocollo.getUos().getArea().getCode())) {
				
				if (getEntity() == null){
					
					if (protocollo.getProcpratiche()==null)
						return;
					
					if (protocollo.getProcpratiche().getVigilanza()==null) {
						inject(newEntity());
						create();
						protocollo.getProcpratiche().setVigilanza(entity);
						entity.setProcpratiche(protocollo.getProcpratiche());
					} else 
						inject(protocollo.getProcpratiche().getVigilanza());
				}
				
				Cantiere cloneCondiviso=null;

				//Ditta lavori: Copia mittente della comunicazione
				if (protocollo.getRichiedente() != null) {
					if ("Ditta".equals(protocollo.getRichiedente().getCode())) {
						PersonaGiuridicaSede personaGiuridicaSede = new PersonaGiuridicaSede();
						personaGiuridicaSede.setPersonaGiuridica(protocollo.getRichiedenteDitta());
						personaGiuridicaSede.setSede(protocollo.getRichiedenteSede());
						copyAteco(personaGiuridicaSede.getPersonaGiuridica(), personaGiuridicaSede.getSede());
						//ca.create(personaGiuridicaSede);

						entity.addPersonaGiuridicaSede(personaGiuridicaSede);
					}
				}
				
				if (protocollo.getRiferimento() != null) {
					if ("Ditta".equals(protocollo.getRiferimento().getCode())) {
						PersonaGiuridicaSede personaGiuridicaSede = new PersonaGiuridicaSede();
						personaGiuridicaSede.setPersonaGiuridica(protocollo.getRiferimentoDitta());
						personaGiuridicaSede.setSede(protocollo.getRiferimentoSede());
						copyAteco(personaGiuridicaSede.getPersonaGiuridica(), personaGiuridicaSede.getSede());
						//ca.create(personaGiuridicaSede);
						
						entity.addPersonaGiuridicaSede(personaGiuridicaSede);
					}
					if ("Cantiere".equals(protocollo.getRiferimento().getCode())) {
						/* Mantis 32590 */
						if(protocollo.getProcpratiche()!=null && protocollo.getProcpratiche().getPraticheRiferimenti()!=null 
								&& protocollo.getProcpratiche().getPraticheRiferimenti().getRiferimentoCantiere()!=null){
							//è già un clone: è stato fatto in sede di ProcpraticheAction.copyRiferimenti
							entity.setCantiere(protocollo.getProcpratiche().getPraticheRiferimenti().getRiferimentoCantiere()); 
						}else if(protocollo.getRiferimentoCantiere()!=null){
							if(protocollo.getUbicazioneCantiere()!=null && 
									protocollo.getUbicazioneCantiere().getInternalId()==protocollo.getRiferimentoCantiere().getInternalId()){
								
								cloneCondiviso=CantiereAction.instance().copy(protocollo.getRiferimentoCantiere());
								ca.create(cloneCondiviso);//siamo in superstate
								entity.setCantiere(cloneCondiviso);
							}else{
								Cantiere cantiere = CantiereAction.instance().copy(protocollo.getRiferimentoCantiere());
								
								ca.create(cantiere);//siamo in superstate
								entity.setCantiere(cantiere);
							}
						}
					}
				}
				
				if (protocollo.getUbicazione() != null) {
					if ("Ditta".equals(protocollo.getUbicazione().getCode())) {
						//Copio ubicazione ditta x pratiche Vigilanza aziende
						entity.setSitoBonificaDitta(protocollo.getUbicazioneDitta());
						entity.setSitoBonificaSede(protocollo.getUbicazioneSede());
					}
	
					//Sito della bonifica: Copia ubicazione dalla comuniczione
					if ("Cantiere".equals(protocollo.getUbicazione().getCode())) {
						//http://support.insielmercato.it/mantis/view.php?id=32590
						if(protocollo.getProcpratiche()!=null && protocollo.getProcpratiche().getPraticheRiferimenti()!=null 
								&& protocollo.getProcpratiche().getPraticheRiferimenti().getUbicazioneCantiere()!=null){
							//è già un clone: è stato fatto in sede di ProcpraticheAction.copyRiferimenti
							entity.setCantiere(protocollo.getProcpratiche().getPraticheRiferimenti().getUbicazioneCantiere()); 
						}else if(protocollo.getUbicazioneCantiere()!=null){
							if(cloneCondiviso!=null){
								
								entity.setCantiere(cloneCondiviso);
							}else{
								Cantiere cantiere = CantiereAction.instance().copy(protocollo.getUbicazioneCantiere());
								entity.setCantiere(cantiere);
							}
							
						}
					}
					
					if ("Altro".equals(protocollo.getUbicazione().getCode())) {
						entity.setEntita((CodeValuePhi)protocollo.getUbicazioneEntita());
						entity.setImo(protocollo.getUbicazioneIMO());
						entity.setTarga(protocollo.getUbicazioneTarga());
						entity.setSpecificazione(protocollo.getUbicazioneSpec());
						
						if (protocollo.getUbicazioneAddr()!=null)
							entity.setAddr(protocollo.getUbicazioneAddr().cloneAd());
						
						entity.setLatitudine(protocollo.getUbicazioneX());
						entity.setLongitudine(protocollo.getUbicazioneY());
					}
				}

				
				if ("5".equals(protocollo.getCode().getCode()) && dettagliBonifiche!=null) {
					//Notifiche e piani di lavoro per bonifiche amianto

					setCodeValue("type", "PHIDIC", "types", "Asbestos");
					
					//Committente
					entity.setCommittente(dettagliBonifiche.getCommittente());
					
					entity.setTipoSegnalazione(dettagliBonifiche.getTipoSegnalazione());
					entity.setIscrizioneAlbo(dettagliBonifiche.getIscrizioneAlbo());

					//Piano di lavoro
					if (dettagliBonifiche.getTipoMatrice() != null) {
						if ("1".equals(dettagliBonifiche.getTipoMatrice().getCode())) {
							entity.setFriabile(true);
						} else if ("2".equals(dettagliBonifiche.getTipoMatrice().getCode())) {
							entity.setCompatto(true);
						}
					}
					
					entity.setTipoIntervento(dettagliBonifiche.getTipoBonifica());
					entity.setTipoConfinamento(dettagliBonifiche.getTipoConfinamento());
					
					entity.setPresuntoInizioLavori(dettagliBonifiche.getInizioLavori());
					
					if (dettagliBonifiche.getDurata() != null) {
						entity.setDurataLavori(dettagliBonifiche.getDurata().toString());
					}
					
					/* if (dettagliBonifiche.getTipoSegnalazione() != null) {
						if ("1".equals(dettagliBonifiche.getTipoSegnalazione().getCode())) {
							//art 256
							if (dettagliBonifiche.getKg() != null) {
								entity.setBonificatiQ(dettagliBonifiche.getKg().toString());
								setCodeValue("bonificatiUm", "PHIDIC", "UM", "Kg");
							}
						} else {
							//art 250
							if (dettagliBonifiche.getMq() != null) {
								entity.setBonificatiQ(dettagliBonifiche.getMq().toString());
								setCodeValue("bonificatiUm", "PHIDIC", "UM", "mq");
							}
						}
					} */
					
					entity.setBonificatiKg(dettagliBonifiche.getKg());
					entity.setBonificatiMq(dettagliBonifiche.getMq());
					
					entity.setNumLavoratori(dettagliBonifiche.getNlav());
				}
				
			}
			
		} catch (Exception e) {
			throw new PhiException("Error coping data from Protocollo and DettagliBonifiche to Vigilanza", 
					e, ErrorConstants.GENERIC_ERR_CODE);
		}
	}
	
	public void calculateEffectiveDuration() {
		entity = getEntity();
		if (entity.getEffettivoFineLavori() != null && entity.getEffettivoInizioLavori() != null) {
			Long durata = (entity.getEffettivoFineLavori().getTime() - entity.getEffettivoInizioLavori().getTime()) / (1000 * 60 * 60 * 24);
			entity.setEffettivoDurataLavori(durata.intValue()+1	 + "");
		}
	}
	
	public void setReason(List<String> codeIds){
		getEntity();
		if(entity == null)
			return;

			if(codeIds!=null && !codeIds.isEmpty()){
				List<CodeValuePhi> reason = new ArrayList<CodeValuePhi>();
				for(String id : codeIds){
					CodeValuePhi cv = ca.get(CodeValuePhi.class, id);
					reason.add(cv);
				}
				entity.setReason(reason);
			} else {
				entity.setReason(null);
			}
	}
	
	@SuppressWarnings("unchecked")
	public void copyAteco(PersoneGiuridiche p, Sedi s) throws PhiException{
		Vigilanza vig = getEntity();
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		vig.setComparto(aAction.getImportantAteco(p, s));
	}
	
	/* Verifica che le ditte del cantiere (List<DitteCantiere>) siano le stesse ditte 
	 * presenti in vigilanza (Vigilanza.personaGiuridicaSede) */ 
	public void checkDitteCantiere(Cantiere cant) {
		Vigilanza vig = getEntity();
		
		//Return se non è una vigilanza cantiere		
		if (vig.getType()==null || !"Yard".equals(vig.getType().getCode()))
			return;
	
		//Return se la pratica non è in stato active		
		Procpratiche p = vig.getProcpratiche();
		if (p == null || p.getStatusCode()==null || !"active".equals(p.getStatusCode().getCode()))
			return;
		
		if (vig.getCantiere()==null && cant ==null){
			vig.setPersonaGiuridicaSede(null);
			return;
		}
		
		if (vig.getCantiere()!=null)
			cant = vig.getCantiere();
		
		//Lista delle ditte del cantiere (dcl)
		List<DitteCantiere> dcl = new ArrayList<DitteCantiere>();
		if (cant.getDitteCantiere()!=null)
			dcl = cant.getDitteCantiere();
		
		//Lista delle ditte della vigilanza (dvl)
		List<PersonaGiuridicaSede> dvl = new ArrayList<PersonaGiuridicaSede>();
		if (vig.getPersonaGiuridicaSede()!=null)
			dvl = vig.getPersonaGiuridicaSede();
		
		try{
			//Se il cantiere non ha ditte, elimina le eventuali ditte associate alla vigilanza (dati sporchi)
			if (dcl.size()==0 && dvl.size()>0) {
				vig.setPersonaGiuridicaSede(null);
				
			} else {//Altrimenti
				
				//Crea una lista contenente le ditte della vigilanza che non figurano tra le ditte del cantiere (dvToRemList)
				List<PersonaGiuridicaSede> dvToRemList = new ArrayList<PersonaGiuridicaSede>();

				for (PersonaGiuridicaSede dv : dvl){
					boolean inCantiere = false;
					if (dcl!=null){
						for (DitteCantiere dc : dcl){
							if (dv!=null && dc!=null && dv.getPersonaGiuridica()!=null && dc.getPersoneGiuridiche()!=null){
								if (dv.getPersonaGiuridica().getInternalId() == dc.getPersoneGiuridiche().getInternalId()){
									dv.setTipoDitta(dc.getRuolo());
									inCantiere = true;
								}
							}
						}
							
						if (!inCantiere)
							dvToRemList.add(dv);
					}
				}
				
				//Elimina le ditte della vigilanza che non figurano tra le ditte del cantiere
				for (PersonaGiuridicaSede dvToRem : dvToRemList){
					vig.getPersonaGiuridicaSede().remove(dvToRem);
				}
				
				//Crea una lista (dvToAddList) contenente le ditte del cantiere che non figurano tra le ditte della vigilanza 
				List<PersonaGiuridicaSede> dvToAddList = new ArrayList<PersonaGiuridicaSede>();
				
				for (DitteCantiere dc : dcl) {
					boolean inVigilanza = false;
					if (dvl!=null){
						for (PersonaGiuridicaSede dv : dvl){
							if (dv!=null && dc!=null && dv.getPersonaGiuridica()!=null && dc.getPersoneGiuridiche()!=null){
								if (dv.getPersonaGiuridica().getInternalId() == dc.getPersoneGiuridiche().getInternalId()){
									inVigilanza = true;
								}
							}
						}
					
						//Se la ditta del cantiere non è stata trovata tra le ditte della vigilanza
						if (!inVigilanza){
							//La crea e la aggiunge a dvToAddList
							PersonaGiuridicaSede dv = new PersonaGiuridicaSede();
							dv.setPersonaGiuridica(dc.getPersoneGiuridiche());
							dv.setTipoDitta(dc.getRuolo());
							dv.setVigilanza(vig);
							dvToAddList.add(dv);
							ca.create(dv);//siamo in un superstate
						}
					}
				}
				
				//Aggiunge le ditte del cantiere che non figurano tra le ditte della vigilanza
				for (PersonaGiuridicaSede dvToAdd : dvToAddList){
					vig.getPersonaGiuridicaSede().add(dvToAdd);
				}
				
				//this.create();
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void cleanSitoBonifica() throws PhiException {
		Vigilanza vig = getEntity();
		if (vig !=null) {
			if (vig.getCantiere()!=null){
				CantiereAction cantiereAction = CantiereAction.instance();	
				cantiereAction.eject();
				
				vig.setCantiere(null);
			}
			
			vig.setSitoBonificaDitta(null);
			vig.setSitoBonificaSede(null);
		}
	}
	
	/* public void createPersoneGiuridicheSede(List<DitteCantiere> dcl) {
		try {
			Vigilanza vig = getEntity();
			PersonaGiuridicaSedeAction pgsAction = PersonaGiuridicaSedeAction.instance();
			List<PersonaGiuridicaSede> dvToAddList = new ArrayList<PersonaGiuridicaSede>();
			
			//Crea una lista (dvToAddList) contenente le ditte del cantiere 
			if (dcl!=null){
				for (DitteCantiere dc : dcl){
					PersonaGiuridicaSede dv = new PersonaGiuridicaSede();
					dv.setPersonaGiuridica(dc.getPersoneGiuridiche());
					dv.setTipoDitta(dc.getRuolo());
					dvToAddList.add(dv);
				}
			}
					
			//Aggiunge le ditte del cantiere
			for (PersonaGiuridicaSede dvToAdd : dvToAddList){
				dvToAdd.setVigilanza(vig);
				vig.addPersonaGiuridicaSede(dvToAdd);
						
				pgsAction.inject(dvToAdd);
				pgsAction.create();
			}
			
			//pgsAction.ejectList();
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void deletePersoneGiuridicheSede() {
		Vigilanza vig = getEntity();
		List<PersonaGiuridicaSede> dvList = vig.getPersonaGiuridicaSede();
		
		PersonaGiuridicaSedeAction pgsAction = PersonaGiuridicaSedeAction.instance();

		try{
			
			//Elimina tutte le ditte presenti in vigilanza
			for (PersonaGiuridicaSede dv : dvList){
				dv.setVigilanza(null);
				pgsAction.inject(dv);
				pgsAction.create();
			}
			
			vig.setPersonaGiuridicaSede(null);
			
			pgsAction.eject();
			pgsAction.ejectList();
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	} */
	
}