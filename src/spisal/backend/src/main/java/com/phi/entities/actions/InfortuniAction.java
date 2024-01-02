package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.DestinatariSpisal;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.notifications.InfMortaliMessage;
import com.phi.parameters.ParameterManager;
import com.phi.util.GenericWsClient;

@BypassInterceptors
@Name("InfortuniAction")
@Scope(ScopeType.CONVERSATION)
public class InfortuniAction extends BaseAction<Infortuni, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7581062297254566641L;

	private static final Logger log = Logger.getLogger(InfortuniAction.class);

	public static InfortuniAction instance() {
		return (InfortuniAction) Component.getInstance(InfortuniAction.class, ScopeType.CONVERSATION);
	}

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFormeByTree() throws PersistenceException {
		this.setByTree("forma");
	}

	public void setRischioByTree() throws PersistenceException {
		this.setByTree("condizioniDiRischio");
	}

	public void setAgenteMaterialeByTree() throws PersistenceException {
		this.setByTree("agenteMateriale");
	}

	public void setComportamentoByTree() throws PersistenceException {
		this.setByTree("comportamento");
	}

	public void setByTree(String attribute) throws PersistenceException {
		String type = "";
		String id = getId();

		List<?> res = ca.executeHQL("select cv from CodeValue cv where id = '" + this.id + "'");

		if (res != null && !res.isEmpty() && res.get(0) != null)
			type = ((CodeValuePhi)res.get(0)).getType();

		if ((id != null) && ( type.equals("C") || type.equals("S") )) {
			try {
				if (id.endsWith("_V0"))
					id = id.substring(0, id.length()-3);

				this.setCodeValueOid(attribute, id);

			} catch (PhiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void copyAteco(PersoneGiuridiche p, Sedi s) throws PhiException{
		Infortuni inf = getEntity();
		AttivitaIstatAction aAction = AttivitaIstatAction.instance();
		inf.setComparto(aAction.getImportantAteco(p, s));
	}

	public void presetNotePrognosi(Infortuni inf){
		if(inf.getNoteprognosi()==null || inf.getNoteprognosi().isEmpty()){
			inf.setNoteprognosi("Inabilità temporanea assoluta");
		}
	}

	public Infortuni copy(Protocollo protocollo){	
		try{
			if (protocollo==null)
				return null;

			Infortuni infSegnalazione = protocollo.getInfortunio();
			if (infSegnalazione==null)
				return null;

			Infortuni infPratica = new Infortuni();
			InfortuniExt infExt = new InfortuniExt();

			//infPratica.setProcpratiche(getEntity());
			infPratica.setInfortuniExt(infExt);

			infPratica.setData(infSegnalazione.getData());
			infPratica.setGravita(infSegnalazione.getGravita());
			infPratica.setSedeLesione(infSegnalazione.getSedeLesione());
			infPratica.setNaturaLesione(infSegnalazione.getNaturaLesione());
			infPratica.setDiagnosips(infSegnalazione.getDiagnosips());
			infPratica.setPrgr(infSegnalazione.getPrgr());
			infPratica.setNoteprognosi(infSegnalazione.getNoteprognosi());

			if(infPratica.getNoteprognosi()==null || infPratica.getNoteprognosi().isEmpty()){
				infPratica.setNoteprognosi("Inabilità temporanea assoluta");
			}

			infPratica.setGgPrognosi1(infSegnalazione.getGgPrognosi1());
			infPratica.setGgPrognosi2(infSegnalazione.getGgPrognosi2());

			infPratica.setNotificationData(protocollo.getData());
			infPratica.setApplicant(protocollo.getApplicant());

			infPratica.setPerson(protocollo.getRiferimentoUtente());
			//infPratica.setProtocollo(infSegnalazione.getProtocollo());

			//NonPrevisto, Ditta, Cantiere, Altro
			CodeValue ubicazione = protocollo.getUbicazione();
			if (ubicazione!=null){
				String code = ubicazione.getCode();
				if (code != null && code != ""){
					if (code.equals("Ditta")) {
						CodeValue pl = infSegnalazione.getPlace();
						if (pl!=null){
							String place = pl.getCode();
							if (place!=null && place!=""){
								if (place.equals("OwnCompany") || place.equals("Company")){
									infPratica.setPlace(pl);
									if (place.equals("OwnCompany")){
										infPratica.setPersoneGiuridiche(protocollo.getUbicazioneDitta());
										infPratica.setSedi(protocollo.getUbicazioneSede());
									}

									infPratica.setPersoneGiuridicheExt(protocollo.getUbicazioneDitta());
									infPratica.setSediExt(protocollo.getUbicazioneSede());
								}
							}
						}
					} else if (code.equals("Cantiere")) {
						Vocabularies vocabularies = VocabulariesImpl.instance();
						CodeValue pl = vocabularies.getCodeValueCsDomainCode("PHIDIC", "Place", "Yard");
						infPratica.setPlace(pl);
						//http://support.insielmercato.it/mantis/view.php?id=32590
						//http://support.insielmercato.it/mantis/view.php?id=32590
						if(protocollo.getProcpratiche()!=null && protocollo.getProcpratiche().getPraticheRiferimenti()!=null 
								&& protocollo.getProcpratiche().getPraticheRiferimenti().getUbicazioneCantiere()!=null){
							//è già un clone: è stato fatto in sede di ProcpraticheAction.copyRiferimenti
							infPratica.setCantiere(protocollo.getProcpratiche().getPraticheRiferimenti().getUbicazioneCantiere()); 
						} else if(protocollo.getUbicazioneCantiere()!=null){
							Cantiere cantiere = CantiereAction.instance().copy(protocollo.getUbicazioneCantiere());
							ca.create(cantiere);//siamo in superstate
							infPratica.setCantiere(cantiere);
						}

					} else if (code.equals("Altro")){
						Vocabularies vocabularies = VocabulariesImpl.instance();
						CodeValue pl = vocabularies.getCodeValueCsDomainCode("PHIDIC", "Place", "Other");
						infPratica.setPlace(pl);

						infPratica.getInfortuniExt().setEntita((CodeValuePhi)protocollo.getUbicazioneEntita());
						infPratica.getInfortuniExt().setImo(protocollo.getUbicazioneIMO());
						infPratica.getInfortuniExt().setTarga(protocollo.getUbicazioneTarga());
					}
				}
			}

			infPratica.getInfortuniExt().setSpecificazione(protocollo.getUbicazioneSpec());

			if(protocollo.getUbicazioneAddr()!=null)
				infPratica.getInfortuniExt().setAddr(protocollo.getUbicazioneAddr().cloneAd());

			infPratica.getInfortuniExt().setLatitudine(protocollo.getUbicazioneX());
			infPratica.getInfortuniExt().setLongitudine(protocollo.getUbicazioneY());

			infPratica.getInfortuniExt().setRifDenominazione(protocollo.getRiferimentoDenominazione());
			infPratica.getInfortuniExt().setRifNote(protocollo.getRiferimentoNote());

			return infPratica;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public List<Infortuni> filterOnlyMortali(List<Infortuni> notFilteredList){
		List<Infortuni> filteredList = new ArrayList<Infortuni>();

		if(notFilteredList==null || notFilteredList.isEmpty())
			return filteredList;

		for(Infortuni inf : notFilteredList){
			if(((inf.getGravita()!=null && "01".equals(inf.getGravita().getCode())) ||
					(inf.getInfortuniExt()!=null && inf.getInfortuniExt().getGravitaFinale()!=null && "01".equals(inf.getInfortuniExt().getGravitaFinale().getCode()))) &&
					Boolean.TRUE.equals(inf.getInfortuniExt().getNotificaDecesso())){
				filteredList.add(inf);
			}
		}

		return filteredList;
	}

	@SuppressWarnings("unchecked")
	public boolean checkPreNotifications(){
		boolean isValid = true;
		Infortuni infortuni = getEntity();

		if (infortuni==null)
			return isValid;
		//Data infortunio
		if (infortuni.getData()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data infortunio obbligatorio.", "Data infortunio obbligatorio.");
			isValid = false;
		} 
		//Data del decesso
		if (infortuni.getDeceasedTime()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data del decesso obbligatoria.", "Data del decesso obbligatoria.");
			isValid = false;
		}
		//Ruolo
		if (infortuni.getRuolo()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Ruolo obbligatorio.", "Ruolo obbligatorio.");
			isValid = false;
		}
		//Codice ATECO azienda
		if (infortuni.getComparto()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Attività ditta obbligatoria.", "Attività ditta obbligatoria.");
			isValid = false;
		}
		//Forma
		if (infortuni.getForma()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Forma obbligatoria.", "Forma obbligatoria.");
			isValid = false;
		}
		//Agente materiale
		if (infortuni.getAgenteMateriale()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Agente materiale obbligatorio.", "Agente materiale obbligatorio.");
			isValid = false;
		}

		Person infortunato = infortuni.getPerson();
		if (infortunato==null) {
			FacesErrorUtils.addErrorMessage("commError", "Infortunato obbligatorio.", "Infortunato obbligatorio.");
			isValid = false;
		}
		//Data di nascita
		if (infortunato!=null && infortunato.getBirthTime()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data di nascita infortunato obbligatoria.", "Data di nascita infortunato obbligatoria.");
			isValid = false;
		}
		//Cittadinanza
		if (infortunato!=null && infortunato.getCitizen()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Cittadinanza infortunato obbligatoria.", "Cittadinanza obbligatoria.");
			isValid = false;
		}

		InfortuniExt infExt = infortuni.getInfortuniExt();
		if (infExt==null) {
			//FacesErrorUtils.addErrorMessage("commError", "Rispondere alla domanda C3.", "Rispondere alla domanda C3.");
			isValid = false;
		}
		if (infExt!=null && infExt.getAddr()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Luogo dell'infortunio obbligatorio.", "Luogo dell'infortunio obbligatorio.");
			isValid = false;
		}
		//Comune del luogo infortunio
		if (infExt!=null && infExt.getAddr()!=null && (infExt.getAddr().getCty()==null || infExt.getAddr().getCty().isEmpty())) {
			FacesErrorUtils.addErrorMessage("commError", "Comune luogo dell'infortunio obbligatorio.", "Comune luogo dell'infortunio obbligatorio.");
			isValid = false;
		}

		PersoneGiuridiche dittaInfortunato = infortuni.getPersoneGiuridiche();
		// Ditta (azienda in cui lavora l'infortunato)
		if (dittaInfortunato==null) {
			FacesErrorUtils.addErrorMessage("commError", "Ditta obbligatoria.", "Ditta obbligatoria.");
			isValid = false;
		}

		return isValid;

	}

	@SuppressWarnings("finally")
	public boolean sendNotification(){
		Infortuni toCopy = getEntity();

		//Clonazione infortunio con tutti i dati per eventuali usi futuri
		Infortuni copy = new Infortuni();

		CodeValue agenteChildren = toCopy.getAgenteChildren();
		if (agenteChildren instanceof HibernateProxy) {
			agenteChildren = (CodeValue)((HibernateProxy)agenteChildren).getHibernateLazyInitializer().getImplementation();
		}
		copy.setAgenteChildren(agenteChildren);

		CodeValuePhi agenteMAteriale = toCopy.getAgenteMateriale();
		if (agenteMAteriale instanceof HibernateProxy) {
			agenteMAteriale = (CodeValuePhi)((HibernateProxy)agenteMAteriale).getHibernateLazyInitializer().getImplementation();
		}
		copy.setAgenteMateriale(agenteMAteriale);

		CodeValue agenteParent = toCopy.getAgenteParent();
		if (agenteParent instanceof HibernateProxy) {
			agenteParent = (CodeValue)((HibernateProxy)agenteParent).getHibernateLazyInitializer().getImplementation();
		}
		copy.setAgenteParent(agenteParent);

		CodeValue anzianita = toCopy.getAnzianita();
		if (anzianita instanceof HibernateProxy) {
			anzianita = (CodeValue)((HibernateProxy)anzianita).getHibernateLazyInitializer().getImplementation();
		}
		copy.setAnzianita(anzianita);

		CodeValue applicant = toCopy.getApplicant();
		if (applicant instanceof HibernateProxy) {
			applicant = (CodeValue)((HibernateProxy)applicant).getHibernateLazyInitializer().getImplementation();
		}
		copy.setApplicant(applicant);

		CodeValuePhi azioneIntrapresa = toCopy.getAzioneIntrapresa();
		if (azioneIntrapresa instanceof HibernateProxy) {
			azioneIntrapresa = (CodeValuePhi)((HibernateProxy)azioneIntrapresa).getHibernateLazyInitializer().getImplementation();
		}
		copy.setAzioneIntrapresa(azioneIntrapresa);
		copy.setCantiere(toCopy.getCantiere());

		CodeValueAteco comparto = toCopy.getComparto();
		if (comparto instanceof HibernateProxy) {
			comparto = (CodeValueAteco)((HibernateProxy)comparto).getHibernateLazyInitializer().getImplementation();
		}
		copy.setComparto(comparto);

		CodeValuePhi comportamento = toCopy.getComportamento();
		if (comportamento instanceof HibernateProxy) {
			comportamento = (CodeValuePhi)((HibernateProxy)comportamento).getHibernateLazyInitializer().getImplementation();
		}
		copy.setComportamento(comportamento);
		copy.setCompSpec(toCopy.getCompSpec());

		CodeValuePhi condRischio = toCopy.getCondizioniDiRischio();
		if (condRischio instanceof HibernateProxy) {
			condRischio = (CodeValuePhi)((HibernateProxy)condRischio).getHibernateLazyInitializer().getImplementation();
		}
		copy.setCondizioniDiRischio(condRischio);
		copy.setData(toCopy.getData());
		copy.setDataAssunzione(toCopy.getDataAssunzione());

		CodeValue deceasedCode = toCopy.getDeceasedCode();
		if (deceasedCode instanceof HibernateProxy) {
			deceasedCode = (CodeValue)((HibernateProxy)deceasedCode).getHibernateLazyInitializer().getImplementation();
		}
		copy.setDeceasedCode(deceasedCode);
		copy.setDeceasedNote(toCopy.getDeceasedNote());

		CodeValueCity cityDeceased = null;
		if(toCopy.getDeceasedPlace()!=null){
			cityDeceased = toCopy.getDeceasedPlace().getCode();
			if (cityDeceased instanceof HibernateProxy) {
				cityDeceased = (CodeValueCity)((HibernateProxy)cityDeceased).getHibernateLazyInitializer().getImplementation();
			}
			copy.setDeceasedPlace(toCopy.getDeceasedPlace().cloneAd());
			copy.getDeceasedPlace().setCode(cityDeceased);
		}

		copy.setDeceasedTime(toCopy.getDeceasedTime());
		copy.setDiagnosips(toCopy.getDiagnosips());
		copy.setDinamica(toCopy.getDinamica());
		copy.setDisability(toCopy.getDisability());
		copy.setEvitabilita(toCopy.getEvitabilita());

		CodeValuePhi forma = toCopy.getForma();
		if (forma instanceof HibernateProxy) {
			forma = (CodeValuePhi)((HibernateProxy)forma).getHibernateLazyInitializer().getImplementation();
		}
		copy.setForma(forma);
		copy.setGgPrognosi1(toCopy.getGgPrognosi1());
		copy.setGgPrognosi2(toCopy.getGgPrognosi2());
		copy.setGgPrognosi3(toCopy.getGgPrognosi3());
		copy.setGgPrognosi4(toCopy.getGgPrognosi4());
		copy.setGgPrognosi5(toCopy.getGgPrognosi5());
		copy.setGgPrognosiTot(toCopy.getGgPrognosiTot());

		CodeValue gravita = toCopy.getGravita();
		if (gravita instanceof HibernateProxy) {
			gravita = (CodeValue)((HibernateProxy)gravita).getHibernateLazyInitializer().getImplementation();
		}
		copy.setGravita(gravita);
		copy.setHospitalized(toCopy.getHospitalized());
		copy.setInchiesta(toCopy.getInchiesta());
		//copy.setInfortuniExt(toCopy.getInfortuniExt());
		copy.setInternalId(toCopy.getInternalId());

		CodeValue mansione = toCopy.getMansione();
		if (mansione instanceof HibernateProxy) {
			mansione = (CodeValue)((HibernateProxy)mansione).getHibernateLazyInitializer().getImplementation();
		}
		copy.setMansione(mansione);

		CodeValue naturaLesione = toCopy.getNaturaLesione();
		if (naturaLesione instanceof HibernateProxy) {
			naturaLesione = (CodeValue)((HibernateProxy)naturaLesione).getHibernateLazyInitializer().getImplementation();
		}
		copy.setNaturaLesione(naturaLesione);
		copy.setNoteprognosi(toCopy.getNoteprognosi());
		copy.setNotificationData(toCopy.getNotificationData());
		copy.setOrdinalHour(toCopy.getOrdinalHour());
		copy.setOtherDescription(toCopy.getOtherDescription());
		copy.setPat(toCopy.getPat());
		//copy.setPerson(toCopy.getPerson());

		PersoneGiuridiche dittaInf = toCopy.getPersoneGiuridiche();

		if(dittaInf!=null){
			PersoneGiuridiche dittaInfCopy = new PersoneGiuridiche();

			List<AttivitaIstat> dittaAttivita = dittaInf.getAttivitaIstat();
			List<AttivitaIstat> dittaCopyAttivita = new ArrayList<AttivitaIstat>();
			for(AttivitaIstat attivitaOrig : dittaAttivita){
				AttivitaIstat aiCopy = new AttivitaIstat();
				aiCopy.setAttivita(attivitaOrig.getAttivita());
				aiCopy.setCfonte(attivitaOrig.getCfonte());
				aiCopy.setCode(attivitaOrig.getCode());
				aiCopy.setDataInizioAttivita(attivitaOrig.getDataInizioAttivita());
				aiCopy.setDescrizione(attivitaOrig.getDescrizione());
				aiCopy.setFonte(attivitaOrig.getFonte());
				
				CodeValue importanza = attivitaOrig.getImportanza();
				if (importanza instanceof HibernateProxy) {
					importanza = (CodeValue)((HibernateProxy)importanza).getHibernateLazyInitializer().getImplementation();
				}
				aiCopy.setImportanza(importanza);
				
				dittaCopyAttivita.add(aiCopy);
			}
			dittaInfCopy.setAttivitaIstat(dittaCopyAttivita);

			dittaInfCopy.setCodiceDitta(dittaInf.getCodiceDitta());
			dittaInfCopy.setCodiceFiscale(dittaInf.getCodiceFiscale());
			dittaInfCopy.setDataCancellazioneRI(dittaInf.getDataCancellazioneRI());
			dittaInfCopy.setDataCostituzione(dittaInf.getDataCostituzione());
			dittaInfCopy.setDataIscrizioneRI(dittaInf.getDataIscrizioneRI());
			dittaInfCopy.setDataTermine(dittaInf.getDataTermine());
			dittaInfCopy.setDenominazione(dittaInf.getDenominazione());
			dittaInfCopy.setEnte(dittaInf.getEnte());
			CodeValue formaGiurid = dittaInf.getFormaGiuridica();
			if (formaGiurid instanceof HibernateProxy) {
				formaGiurid = (CodeValue)((HibernateProxy)formaGiurid).getHibernateLazyInitializer().getImplementation();
			}
			dittaInfCopy.setFormaGiuridica(formaGiurid);
			dittaInfCopy.setInternalId(dittaInf.getInternalId());
			dittaInfCopy.setNumeroRI(dittaInf.getNumeroRI());
			dittaInfCopy.setPatritaIva(dittaInf.getPatritaIva());

			copy.setPersoneGiuridiche(dittaInfCopy);
		}

		PersoneGiuridiche dittaExt = toCopy.getPersoneGiuridicheExt();
		if(dittaExt!=null){
			PersoneGiuridiche dittaExtCopy = new PersoneGiuridiche();

			List<AttivitaIstat> dittaAttivita = dittaExt.getAttivitaIstat();
			List<AttivitaIstat> dittaCopyAttivita = new ArrayList<AttivitaIstat>();
			for(AttivitaIstat attivitaOrig : dittaAttivita){
				AttivitaIstat aiCopy = new AttivitaIstat();
				aiCopy.setAttivita(attivitaOrig.getAttivita());
				aiCopy.setCfonte(attivitaOrig.getCfonte());
				aiCopy.setCode(attivitaOrig.getCode());
				aiCopy.setDataInizioAttivita(attivitaOrig.getDataInizioAttivita());
				aiCopy.setDescrizione(attivitaOrig.getDescrizione());
				aiCopy.setFonte(attivitaOrig.getFonte());
				
				CodeValue importanza = attivitaOrig.getImportanza();
				if (importanza instanceof HibernateProxy) {
					importanza = (CodeValue)((HibernateProxy)importanza).getHibernateLazyInitializer().getImplementation();
				}
				aiCopy.setImportanza(importanza);
				
				dittaCopyAttivita.add(aiCopy);
			}
			dittaExtCopy.setAttivitaIstat(dittaCopyAttivita);

			dittaExtCopy.setCodiceDitta(dittaExt.getCodiceDitta());
			dittaExtCopy.setCodiceFiscale(dittaExt.getCodiceFiscale());
			dittaExtCopy.setDataCancellazioneRI(dittaExt.getDataCancellazioneRI());
			dittaExtCopy.setDataCostituzione(dittaExt.getDataCostituzione());
			dittaExtCopy.setDataIscrizioneRI(dittaExt.getDataIscrizioneRI());
			dittaExtCopy.setDataTermine(dittaExt.getDataTermine());
			dittaExtCopy.setDenominazione(dittaExt.getDenominazione());
			dittaExtCopy.setEnte(dittaExt.getEnte());
			CodeValue formaGiuridExt = dittaExt.getFormaGiuridica();
			if (formaGiuridExt instanceof HibernateProxy) {
				formaGiuridExt = (CodeValue)((HibernateProxy)formaGiuridExt).getHibernateLazyInitializer().getImplementation();
			}
			dittaExtCopy.setFormaGiuridica(formaGiuridExt);
			dittaExtCopy.setInternalId(dittaExt.getInternalId());
			dittaExtCopy.setNumeroRI(dittaExt.getNumeroRI());
			dittaExtCopy.setPatritaIva(dittaExt.getPatritaIva());

			copy.setPersoneGiuridicheExt(dittaExtCopy);
		}

		CodeValue place = toCopy.getPlace();
		if (place instanceof HibernateProxy) {
			place = (CodeValue)((HibernateProxy)place).getHibernateLazyInitializer().getImplementation();
		}
		copy.setPlace(place);
		copy.setPrgr(toCopy.getPrgr());
		copy.setProcpratiche(null);
		copy.setProtocollo(null);

		CodeValue qualifica = toCopy.getQualifica();
		if (qualifica instanceof HibernateProxy) {
			qualifica = (CodeValue)((HibernateProxy)qualifica).getHibernateLazyInitializer().getImplementation();
		}
		copy.setQualifica(qualifica);

		CodeValue ruolo = toCopy.getRuolo();
		if (ruolo instanceof HibernateProxy) {
			ruolo = (CodeValue)((HibernateProxy)ruolo).getHibernateLazyInitializer().getImplementation();
		}
		copy.setRuolo(ruolo);

		CodeValue sedeLesione = toCopy.getSedeLesione();
		if (sedeLesione instanceof HibernateProxy) {
			sedeLesione = (CodeValue)((HibernateProxy)sedeLesione).getHibernateLazyInitializer().getImplementation();
		}
		copy.setSedeLesione(sedeLesione);
		Sedi sedi = null;
		if(toCopy.getSedi()!=null){
			sedi = toCopy.getSedi();
			if (sedi instanceof HibernateProxy) {
				sedi = (Sedi)((HibernateProxy)sedi).getHibernateLazyInitializer().getImplementation();
			}

			SediAction sediAction = SediAction.instance();

			Sedi sedeCopy = sediAction.copy(sedi);

			CodeValueCity comuneSedi = null;
			if(sedeCopy.getAddr()!=null){
				comuneSedi = sedeCopy.getAddr().getCode();
				if (comuneSedi instanceof HibernateProxy) {
					comuneSedi = (CodeValueCity)((HibernateProxy)comuneSedi).getHibernateLazyInitializer().getImplementation();
				}
				sedeCopy.getAddr().setCode(comuneSedi);
			}

			PersoneGiuridiche ditta = sedi.getPersonaGiuridica();

			PersoneGiuridiche dittaCopy = new PersoneGiuridiche();

			List<AttivitaIstat> dittaAttivita = ditta.getAttivitaIstat();
			List<AttivitaIstat> dittaCopyAttivita = new ArrayList<AttivitaIstat>();
			for(AttivitaIstat attivitaOrig : dittaAttivita){
				AttivitaIstat aiCopy = new AttivitaIstat();
				aiCopy.setAttivita(attivitaOrig.getAttivita());
				aiCopy.setCfonte(attivitaOrig.getCfonte());
				aiCopy.setCode(attivitaOrig.getCode());
				aiCopy.setDataInizioAttivita(attivitaOrig.getDataInizioAttivita());
				aiCopy.setDescrizione(attivitaOrig.getDescrizione());
				aiCopy.setFonte(attivitaOrig.getFonte());
				
				CodeValue importanza = attivitaOrig.getImportanza();
				if (importanza instanceof HibernateProxy) {
					importanza = (CodeValue)((HibernateProxy)importanza).getHibernateLazyInitializer().getImplementation();
				}
				aiCopy.setImportanza(importanza);
				
				dittaCopyAttivita.add(aiCopy);
			}
			dittaCopy.setAttivitaIstat(dittaCopyAttivita);
			
			dittaCopy.setCodiceDitta(ditta.getCodiceDitta());
			dittaCopy.setCodiceFiscale(ditta.getCodiceFiscale());
			dittaCopy.setDataCancellazioneRI(ditta.getDataCancellazioneRI());
			dittaCopy.setDataCostituzione(ditta.getDataCostituzione());
			dittaCopy.setDataIscrizioneRI(ditta.getDataIscrizioneRI());
			dittaCopy.setDataTermine(ditta.getDataTermine());
			dittaCopy.setDenominazione(ditta.getDenominazione());
			dittaCopy.setEnte(ditta.getEnte());

			CodeValue formaGiuridica = ditta.getFormaGiuridica();
			if (formaGiuridica instanceof HibernateProxy) {
				formaGiuridica = (CodeValue)((HibernateProxy)formaGiuridica).getHibernateLazyInitializer().getImplementation();
			}
			dittaCopy.setFormaGiuridica(formaGiuridica);
			dittaCopy.setInternalId(ditta.getInternalId());
			dittaCopy.setNumeroRI(ditta.getNumeroRI());
			dittaCopy.setPatritaIva(ditta.getPatritaIva());

			sedeCopy.setPersonaGiuridica(dittaCopy);
			copy.setSedi(sedeCopy);
		}

		Sedi sediExt = null;
		if(toCopy.getSediExt()!=null){
			sediExt = toCopy.getSediExt();
			if (sediExt instanceof HibernateProxy) {
				sediExt = (Sedi)((HibernateProxy)sediExt).getHibernateLazyInitializer().getImplementation();
			}

			SediAction sediAction = SediAction.instance();

			Sedi sedeCopy = sediAction.copy(sediExt);

			CodeValueCity comuneSedi = null;
			if(sedeCopy.getAddr()!=null){
				comuneSedi = sedeCopy.getAddr().getCode();
				if (comuneSedi instanceof HibernateProxy) {
					comuneSedi = (CodeValueCity)((HibernateProxy)comuneSedi).getHibernateLazyInitializer().getImplementation();
				}
				sedeCopy.getAddr().setCode(comuneSedi);
			}

			PersoneGiuridiche ditta = sediExt.getPersonaGiuridica();

			PersoneGiuridiche dittaCopy = new PersoneGiuridiche();

			List<AttivitaIstat> dittaAttivita = ditta.getAttivitaIstat();
			List<AttivitaIstat> dittaCopyAttivita = new ArrayList<AttivitaIstat>();
			for(AttivitaIstat attivitaOrig : dittaAttivita){
				AttivitaIstat aiCopy = new AttivitaIstat();
				aiCopy.setAttivita(attivitaOrig.getAttivita());
				aiCopy.setCfonte(attivitaOrig.getCfonte());
				aiCopy.setCode(attivitaOrig.getCode());
				aiCopy.setDataInizioAttivita(attivitaOrig.getDataInizioAttivita());
				aiCopy.setDescrizione(attivitaOrig.getDescrizione());
				aiCopy.setFonte(attivitaOrig.getFonte());
				
				CodeValue importanza = attivitaOrig.getImportanza();
				if (importanza instanceof HibernateProxy) {
					importanza = (CodeValue)((HibernateProxy)importanza).getHibernateLazyInitializer().getImplementation();
				}
				aiCopy.setImportanza(importanza);
				
				dittaCopyAttivita.add(aiCopy);
			}
			dittaCopy.setAttivitaIstat(dittaCopyAttivita);

			dittaCopy.setAttivitaIstat(ditta.getAttivitaIstat());
			dittaCopy.setCodiceDitta(ditta.getCodiceDitta());
			dittaCopy.setCodiceFiscale(ditta.getCodiceFiscale());
			dittaCopy.setDataCancellazioneRI(ditta.getDataCancellazioneRI());
			dittaCopy.setDataCostituzione(ditta.getDataCostituzione());
			dittaCopy.setDataIscrizioneRI(ditta.getDataIscrizioneRI());
			dittaCopy.setDataTermine(ditta.getDataTermine());
			dittaCopy.setDenominazione(ditta.getDenominazione());
			dittaCopy.setEnte(ditta.getEnte());

			CodeValue formaGiuridica = ditta.getFormaGiuridica();
			if (formaGiuridica instanceof HibernateProxy) {
				formaGiuridica = (CodeValue)((HibernateProxy)formaGiuridica).getHibernateLazyInitializer().getImplementation();
			}
			dittaCopy.setFormaGiuridica(formaGiuridica);
			dittaCopy.setInternalId(ditta.getInternalId());
			dittaCopy.setNumeroRI(ditta.getNumeroRI());
			dittaCopy.setPatritaIva(ditta.getPatritaIva());

			sedeCopy.setPersonaGiuridica(dittaCopy);
			copy.setSediExt(sedeCopy);
		}

		CodeValue tipoContratto = toCopy.getTipoContratto();
		if (tipoContratto instanceof HibernateProxy) {
			tipoContratto = (CodeValue)((HibernateProxy)tipoContratto).getHibernateLazyInitializer().getImplementation();
		}
		copy.setTipoContratto(tipoContratto);

		//Clonazione infortunato: solo dati essenziali
		Person infortunato = toCopy.getPerson();

		Person newPerson = new Person();

		newPerson.setBirthTime(infortunato.getBirthTime());
		CodeValueCountry citizen = infortunato.getCitizen();
		if (citizen instanceof HibernateProxy) {
			citizen = (CodeValueCountry)((HibernateProxy)citizen).getHibernateLazyInitializer().getImplementation();
		}
		newPerson.setCitizen(citizen);

		CodeValuePhi gender = infortunato.getGenderCode();
		if (gender instanceof HibernateProxy) {
			gender = (CodeValuePhi)((HibernateProxy)gender).getHibernateLazyInitializer().getImplementation();
		}
		newPerson.setGenderCode(gender);

		copy.setPerson(newPerson);

		InfortuniExt infExt = toCopy.getInfortuniExt();

		InfortuniExt infExtCopy = new InfortuniExt();

		CodeValueCity comuneExt = null;
		if(infExtCopy.getAddr()!=null){
			comuneExt = infExtCopy.getAddr().getCode();
			if (comuneExt instanceof HibernateProxy) {
				comuneExt = (CodeValueCity)((HibernateProxy)comuneExt).getHibernateLazyInitializer().getImplementation();
			}
			infExtCopy.setAddr(infExt.getAddr().cloneAd());
			infExtCopy.getAddr().setCode(comuneExt);
		}

		infExtCopy.setAgeDeath(infExt.getAgeDeath());

		CodeValuePhi deceasedCodeFinal = infExt.getDeceasedCodeFinal();
		if (deceasedCodeFinal instanceof HibernateProxy) {
			deceasedCodeFinal = (CodeValuePhi)((HibernateProxy)deceasedCodeFinal).getHibernateLazyInitializer().getImplementation();
		}
		infExtCopy.setDeceasedCodeFinal(deceasedCodeFinal);

		infExtCopy.setDeceasedNoteFinal(infExt.getDeceasedNoteFinal());

		CodeValueCity comuneDeceased = null;
		if(infExtCopy.getDeceasedPlaceFinal()!=null){
			comuneDeceased = infExtCopy.getDeceasedPlaceFinal().getCode();
			if (comuneDeceased instanceof HibernateProxy) {
				comuneDeceased = (CodeValueCity)((HibernateProxy)comuneDeceased).getHibernateLazyInitializer().getImplementation();
			}
			infExtCopy.setDeceasedPlaceFinal(infExt.getDeceasedPlaceFinal().cloneAd());
			infExtCopy.getDeceasedPlaceFinal().setCode(comuneDeceased);
		}

		infExtCopy.setDeceasedTimeFinal(infExt.getDeceasedTimeFinal());
		infExtCopy.setDisabilityFinal(infExt.getDisabilityFinal());

		CodeValuePhi entita = infExt.getEntita();
		if (entita instanceof HibernateProxy) {
			entita = (CodeValuePhi)((HibernateProxy)entita).getHibernateLazyInitializer().getImplementation();
		}
		infExtCopy.setEntita(entita);

		CodeValuePhi gravitaFinale = infExt.getGravitaFinale();
		if (gravitaFinale instanceof HibernateProxy) {
			gravitaFinale = (CodeValuePhi)((HibernateProxy)gravitaFinale).getHibernateLazyInitializer().getImplementation();
		}
		infExtCopy.setGravitaFinale(gravitaFinale);

		infExtCopy.setImo(infExt.getImo());

		CodeValuePhi infProf = infExt.getInfortunioProf();
		if (infProf instanceof HibernateProxy) {
			infProf = (CodeValuePhi)((HibernateProxy)infProf).getHibernateLazyInitializer().getImplementation();
		}
		infExtCopy.setInfortunioProf(infProf);
		infExtCopy.setInternalId(infExt.getInternalId());
		infExtCopy.setLatitudine(infExt.getLatitudine());
		infExtCopy.setLongitudine(infExt.getLongitudine());

		CodeValuePhi modInformo = infExt.getModInformo();
		if (modInformo instanceof HibernateProxy) {
			modInformo = (CodeValuePhi)((HibernateProxy)modInformo).getHibernateLazyInitializer().getImplementation();
		}
		infExtCopy.setModInformo(modInformo);

		infExtCopy.setModInformoNote(infExt.getModInformoNote());
		infExtCopy.setNote(infExt.getNote());
		infExtCopy.setNotificaDecesso(infExt.getNotificaDecesso());
		infExtCopy.setRifDenominazione(infExt.getRifDenominazione());
		infExtCopy.setRifNote(infExt.getRifNote());
		infExtCopy.setSpecificazione(infExt.getSpecificazione());
		infExtCopy.setStreetDescription(infExt.getStreetDescription());
		infExtCopy.setTarga(infExt.getTarga());

		copy.setInfortuniExt(infExtCopy);

		InfMortaliMessage notificationMessage = new InfMortaliMessage();
		setNoticationContent(notificationMessage);

		notificationMessage.setInfortuni(copy);

		GenericWsClient test = new GenericWsClient();

		//		HashMap<String, String> parameters = new HashMap<String, String>();
		//		parameters.put("keyStoreType", "JKS");
		//		parameters.put("keyStoreLoc", "C://temp//metadata-keystore//client-t.ssa-to-wstpitre.tn.it.jks");
		//		parameters.put("certificatePassword", "Uvd3cfAkleJ3");

		HttpResponse wsResponse = null;

		boolean ret = false;
		String notificationStatus = "";

		String notifUrl = "";
		ParameterManager pm = ParameterManager.instance();

		try {
			notifUrl = pm.getParameter("p.general.infmortalinotif", "value").toString();

			wsResponse = test.doPost("",notifUrl, null,notificationMessage);

			ret = wsResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

			if(ret){
				notificationStatus = "Notifica inviata via mail";
			}else{
				notificationStatus = "Attenzione! Invio fallito, ripetere l’invio";
			}
		} catch (Exception e) {
			e.printStackTrace();
			notificationStatus = "Attenzione! Invio fallito, ripetere l’invio";
			ret = false;

		}finally{
			infExt.setNotificaStatus(notificationStatus);

			return ret;
		}
	}

	private void setNoticationContent(InfMortaliMessage notifica){
		Context conversationContext = Contexts.getConversationContext();

		IdataModel<DestinatariSpisal> destinatariDm = (IdataModel<DestinatariSpisal>) conversationContext.get("DestinatariSpisalList");
		if(destinatariDm == null)
			return;

		List<DestinatariSpisal> destinatariList = destinatariDm.getList();
		if(destinatariList == null || destinatariList.isEmpty())
			return;

		String destNotifica = "";
		String ccNotifica = "";

		for(DestinatariSpisal destinatario : destinatariList){
			if(notifica.getOggetto()==null || notifica.getOggetto().isEmpty())
				notifica.setOggetto(destinatario.getMessageType().getCurrentTranslation()+" -  INVIO: {$invio}");

			if("01".equals(destinatario.getTipoInvio().getCode())){
				if(destNotifica!=null && !destNotifica.isEmpty()){
					destNotifica += ";";
				}
				destNotifica += destinatario.getPec();
			}

			if("02".equals(destinatario.getTipoInvio().getCode())){
				if(ccNotifica!=null && !ccNotifica.isEmpty()){
					ccNotifica += ";";
				}
				ccNotifica += destinatario.getPec();
			}
		}
		
		notifica.setDestinatari(destNotifica);
		notifica.setCC(ccNotifica);

		Procpratiche pratica = (Procpratiche) conversationContext.get("Procpratiche");
		String numeroPratica = pratica.getNumero();
		String codiceULSS = numeroPratica.substring(0, 6);

		String bodyMessage = "Con la presente, il Servizio SPISAL della AULSS " + codiceULSS + 
				" comunica l’evento:" + "\n\n" + "Numero Pratica: " + numeroPratica + "\n" + 
				"Invio : {$invio}" + "\n\n" + 
				"Firma: Dott. " + pratica.getRdp().getName();

		notifica.setBodyMessage(bodyMessage);
	}
}