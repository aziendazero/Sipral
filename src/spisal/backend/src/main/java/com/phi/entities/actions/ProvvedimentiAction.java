package com.phi.entities.actions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Disposizioni;
import com.phi.entities.baseEntity.Gruppi;
import com.phi.entities.baseEntity.Iter758;
import com.phi.entities.baseEntity.Miglioramenti;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.SospensioneEx14;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.II4ServiceDeliveryLocation;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("ProvvedimentiAction")
@Scope(ScopeType.CONVERSATION)
public class ProvvedimentiAction extends BaseAction<Provvedimenti, Long> {

	private static final long serialVersionUID = 559233166L;
	private static final Logger log = Logger.getLogger(ProvvedimentiAction.class);

	public static ProvvedimentiAction instance() {
		return (ProvvedimentiAction) Component.getInstance(ProvvedimentiAction.class, ScopeType.CONVERSATION);
	}

	public void fixSoggInSolidoCode(Provvedimenti provv){
		if(provv==null)
			return;

		Soggetto soggettoInSolido = provv.getSoggettoInSolido();

		if(soggettoInSolido==null)
			return;

		if(soggettoInSolido.getUtente()==null && soggettoInSolido.getDitta()==null)
			return;

		if(soggettoInSolido.getUtente()!=null){
			if(soggettoInSolido.getCode()==null){
				this.setSoggInSolidoUtenteCode(soggettoInSolido);
				return;
			}

			if(!("Utente".equals(soggettoInSolido.getCode().getCode())))
				this.setSoggInSolidoUtenteCode(soggettoInSolido);
		}

		if(soggettoInSolido.getDitta()!=null){
			if(soggettoInSolido.getCode()==null){
				this.setSoggInSolidoDittaCode(soggettoInSolido);
				return;
			}

			if(!("Ditta".equals(soggettoInSolido.getCode().getCode())))
				this.setSoggInSolidoDittaCode(soggettoInSolido);
		}
	}

	private void setSoggInSolidoUtenteCode(Soggetto soggettoInSolido){
		Vocabularies vocabularies = VocabulariesImpl.instance();
		try {
			soggettoInSolido.setCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));
		} catch (PersistenceException e) {

		} catch (DictionaryException e) {

		}			
	}

	private void setSoggInSolidoDittaCode(Soggetto soggettoInSolido){
		Vocabularies vocabularies = VocabulariesImpl.instance();
		try {
			soggettoInSolido.setCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Ditta"));
		} catch (PersistenceException e) {

		} catch (DictionaryException e) {

		}			
	}	
	/* Formato: CodiceUSL_Anno_Progressivo */
	public void setNumero(ServiceDeliveryLocation uoc){
		try{			
			String numero = "";
			String separatore = "_";			
			Provvedimenti provv = getEntity();

			if(provv==null)
				return;

			if(provv.getNumero()!=null && !"".equals(provv.getNumero()))
				return;

			CodeValuePhi provvType = provv.getType();
			if(provvType==null)
				return;
			
			Integer nextValue = null;
			if("phidic.spisal.pratiche.pev.pevtype.ex14".equals(provvType.getOid())){
				nextValue = this.evaluateNextValueSosp(uoc);
			}else{
				nextValue = this.evaluateNextValue(uoc);
			}
			

			if (nextValue != null) {
				if (uoc != null){
					Organization usl = uoc.getParent().getOrganization();

					//Setta CodiceUSL
					if (usl == null)
						numero = "????" + separatore;	
					else {
						String id = usl.getId();

						if ((id != null) && (id != ""))
							numero = id + separatore;

						else numero = "????" + separatore;	
					}

					//Setta CodiceUOC (Distretto)
					//					II4ServiceDeliveryLocation id = (II4ServiceDeliveryLocation)uoc.getId("HBS");
					//					
					//					if (id == null)
					//						numero += "??" + separatore;
					//					else {
					//						String code = id.getExtension();
					//
					//						if ((code != null) && (code != ""))
					//							numero += code + separatore;
					//
					//						else numero += "??" + separatore;
					//					} 

				}

				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				DecimalFormat df = new DecimalFormat("00000");
				numero += cal.get(Calendar.YEAR) + separatore + df.format(nextValue);;

				provv.setNumero(numero);
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Provvedimenti copy(Provvedimenti toCopy){
		try{
			Provvedimenti newProvv = new Provvedimenti();
			newProvv.setData(new Date());

			Vocabularies voc = VocabulariesImpl.instance();
			CodeValue statusCode = voc.getCodeValueCsDomainCode("PHIDIC", "Status", "active");

			newProvv.setStatusCode(statusCode);
			GenericAdapterLocalInterface ga = GenericAdapter.instance();

			newProvv.setSoggetto(toCopy.getSoggetto());
			newProvv.setAttivita(toCopy.getAttivita());
			newProvv.setProcpratiche(toCopy.getProcpratiche());
			newProvv.setCarica(toCopy.getCarica());

			// I00076224 
			newProvv.setSoggettoInSolido(toCopy.getSoggettoInSolido());

			newProvv.setCountry(toCopy.getCountry());
			newProvv.setDocumentNumber(toCopy.getDocumentNumber());
			newProvv.setDocumentType(toCopy.getDocumentType());
			newProvv.setEsito(toCopy.getEsito());
			newProvv.setNote(toCopy.getNote());

			newProvv.setReleasedBy(toCopy.getReleasedBy());
			newProvv.setReleasedDate(toCopy.getReleasedDate());

			newProvv.setType(toCopy.getType());
			newProvv.setViolationType(toCopy.getViolationType());

			if(toCopy.getCity()!=null)
				newProvv.setCity(toCopy.getCity().cloneAd());

			if(toCopy.getProvince()!=null)
				newProvv.setProvince(toCopy.getProvince().cloneAd());

			if(toCopy.getDisposizioni()!=null && !toCopy.getDisposizioni().isEmpty()){
				for(Disposizioni d : toCopy.getDisposizioni()){
					Disposizioni newDisp = copyDisposizioni(d);
					if(newDisp!=null){

						newDisp.setProvvedimenti(newProvv);
						newProvv.addDisposizioni(newDisp);

						//TODO:persist newDisp...non serve->flush in cascade
					}
				}

			}

			if(toCopy.getIter758()!=null && !toCopy.getIter758().isEmpty()){
				for(Iter758 iter : toCopy.getIter758()){
					Iter758 newIter = copyIter(iter);
					if(newIter!=null){

						newIter.setProvvedimento(newProvv);
						newProvv.addIter758(newIter);

						//TODO:persist iter...non serve->flush in cascade
					}
				}
			}

			if(toCopy.getMiglioramenti()!=null && !toCopy.getMiglioramenti().isEmpty()){
				for(Miglioramenti mig : toCopy.getMiglioramenti()){
					Miglioramenti newMig = copyMig(mig);
					if(newMig!=null){
						if(mig.getArticolo()!=null){
							Articoli migArt = copyArt(mig.getArticolo());

							Articoli mergedArt = (Articoli)ga.updateObject(migArt);
							ca.evict(migArt);
							mergedArt=ca.get(mergedArt.getClass(), mergedArt.getInternalId());
							newMig.setArticolo(migArt);
							migArt.addMiglioramenti(newMig);

						}

						newMig.setAttivita(toCopy.getAttivita());
						newMig.setProvvedimento(newProvv);
						newProvv.addMiglioramenti(newMig);
					}
				}
			}

			if(toCopy.getArticoli()!=null && !toCopy.getArticoli().isEmpty()){
				List<Gruppi> newGruppi = new ArrayList<Gruppi>();
				for(Articoli art : toCopy.getArticoli()){
					Articoli newArt = copyArt(art);
					if(newArt!=null){

						newArt.setProvvedimento(newProvv);
						newProvv.addArticoli(newArt);

						if(art.getGruppo()!=null){
							Gruppi group = findGroupWithName(newGruppi,art.getGruppo().getName());
							if(group!=null){
								newArt.setGruppo(group);
							}else{
								group = copyGroup(art.getGruppo());
								Gruppi mergedGroup = (Gruppi)ga.updateObject(group);
								ca.evict(group);
								group=ca.get(mergedGroup.getClass(), mergedGroup.getInternalId());
							}

							newArt.setGruppo(group);
							group.addArticoli(newArt);
						}
					}
				}
			}

			return newProvv;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private static String EVALUATE_NUMBER = "select max(pv.numero) from Provvedimenti pv " +
			"join pv.type pvtype " +
			"where pv.procpratiche.uoc.parent.internalId =:internalID and pv.numero like :annoInCorso " + 	
			"and pvtype.oid != 'phidic.spisal.pratiche.pev.pevtype.ex14'";

	private Integer evaluateNextValue(ServiceDeliveryLocation uoc){
		try{
			if (uoc==null)
				return null;

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);

			//Date date = cal.getTime();
			int annoInCorso = cal.get(Calendar.YEAR);
			String annoInCorsoLike = "%_" + String.valueOf(annoInCorso) + "_%";

			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", uoc.getParent().getInternalId());
			//parameters.put("minDate", date);
			parameters.put("annoInCorso", annoInCorsoLike);

			@SuppressWarnings("unchecked")
			List<String> ppList = (List<String>) ca.executeHQLwithParameters(EVALUATE_NUMBER, parameters);

			Integer ret = 1;
			String numero = ppList.get(0);

			if (numero != null) {
				String[] parts = numero.split("_");
				if (parts.length==3)
					ret = Integer.parseInt(parts[parts.length-1])+1;
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}
	/* 
	 * Se tutti gli articoli o gruppi di articoli hanno Esito =	 Ottemperato oppure 
	 * 															 Ottemperato con modi diversi oppure 
	 *                                                           Ammissione al pagamento senza prescrizione 
	 * --> Esito = Ottemperato
	 *                                                            
	 * Se tutti gli articoli o gruppi di articoli hanno Esito = Inottemperato 
	 * --> Esito = Non ottemperato
	 * 
	 * Altrimenti
	 * --> Esito = Parzialmente ottemperato
	 * 
	 */
	public void setEsito(List<Articoli> artList){
		try {

			if (artList == null || artList.size() <= 0) 
				return;

			Vocabularies vocabularies = VocabulariesImpl.instance();
			Provvedimenti provvedimento = getEntity();

			CodeValue complied = null;
			CodeValue notComplied = null;

			for (Articoli articolo : artList) {
				if (articolo.getEsito() == null)
					return;

				if (articolo.getEsito().getCode().equals("notComplayed"))
					notComplied = vocabularies.getCodeValueCsDomainCode("PHIDIC", "result", "notComplied");

				else if (articolo.getEsito().getCode().equals("compliedPayed") 
						|| articolo.getEsito().getCode().equals("complayedDifferent") 
						|| articolo.getEsito().getCode().equals("compliedNoPrescription"))

					complied = vocabularies.getCodeValueCsDomainCode("PHIDIC", "result", "complied");
			}

			if (notComplied != null && complied == null) 
				provvedimento.setEsito((CodeValuePhi)notComplied);

			else if (notComplied!=null && complied!=null)
				provvedimento.setEsito((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "result", "partiallyComplied"));

			else if (notComplied==null && complied!=null) 
				provvedimento.setEsito((CodeValuePhi)complied);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Disposizioni copyDisposizioni(Disposizioni oldDisp){
		if(oldDisp!=null){
			Disposizioni newDisp = new Disposizioni();
			newDisp.setDataEmissione(oldDisp.getDataEmissione());
			newDisp.setDataScadenza(oldDisp.getDataScadenza());
			newDisp.setDataVerifica(oldDisp.getDataVerifica());
			newDisp.setEsito(oldDisp.getEsito());
			newDisp.setGiorni(oldDisp.getGiorni());
			newDisp.setRiferimentoNormativo(oldDisp.getRiferimentoNormativo());


			return newDisp;
		}
		return null;
	}

	private Iter758 copyIter(Iter758 oldIter){

		if(oldIter!=null){
			Iter758 newIter = new Iter758();

			newIter.setLegale(oldIter.getLegale());
			newIter.setLegaleStr(oldIter.getLegaleStr());
			newIter.setMagistrato(oldIter.getMagistrato());
			newIter.setMagistratoStr(oldIter.getMagistratoStr());
			newIter.setNumeroFascicolo(oldIter.getNumeroFascicolo());
			newIter.setProcura(oldIter.getProcura());
			newIter.setProcuraStr(oldIter.getProcuraStr());

			return newIter;
		}
		return null;
	}

	private Miglioramenti copyMig(Miglioramenti oldMig){
		if(oldMig!=null){
			Miglioramenti newMig = new Miglioramenti();
			newMig.setCodeLegge81(oldMig.getCodeLegge81());
			newMig.setDataEmissione(oldMig.getDataEmissione());
			newMig.setDataScadenza(oldMig.getDataScadenza());
			newMig.setDataVerifica(oldMig.getDataVerifica());
			newMig.setEsito(oldMig.getEsito());
			newMig.setGiorni(oldMig.getGiorni());
			newMig.setMiglioramento(oldMig.getMiglioramento());
			newMig.setNote(oldMig.getNote());

			return newMig;
		}
		return null;
	}

	private Articoli copyArt(Articoli oldArt){
		if(oldArt!=null){
			Articoli newArt = new Articoli();
			newArt.setCode(oldArt.getCode());
			newArt.setEsito(oldArt.getEsito());
			newArt.setGiorniPrescrizione(oldArt.getGiorniPrescrizione());
			newArt.setIsNew(oldArt.getIsNew());
			newArt.setNote(oldArt.getNote());
			newArt.setPrescrizione(oldArt.getPrescrizione());
			newArt.setScadenzaArticolo(oldArt.getScadenzaArticolo());
			newArt.setViolazione(oldArt.getViolazione());

			return newArt;
		}
		return null;
	}

	private Gruppi copyGroup(Gruppi oldGroup){
		if(oldGroup!=null){
			Gruppi newGroup = new Gruppi();
			newGroup.setDataDellaVerifica(oldGroup.getDataDellaVerifica());
			newGroup.setDataNotifica(oldGroup.getDataNotifica());
			newGroup.setGiorniIniziali(oldGroup.getGiorniIniziali());
			newGroup.setName(oldGroup.getName());
			newGroup.setPrimaProroga(oldGroup.getPrimaProroga());
			newGroup.setSecondaProroga(oldGroup.getSecondaProroga());
			newGroup.setSpeseNotifica(oldGroup.getSpeseNotifica());
			newGroup.setNotificaPagamento(oldGroup.getNotificaPagamento());
			newGroup.setAmmissionePagamento(oldGroup.getAmmissionePagamento());
			newGroup.setDataPagamento(oldGroup.getDataPagamento());
			newGroup.setImportoVersato(oldGroup.getImportoVersato());
			newGroup.setDettagli(oldGroup.getDettagli());

			return newGroup;

		}
		return null;

	}

	private Gruppi findGroupWithName(List<Gruppi> lst,String name){
		if(name!=null && lst!=null && !lst.isEmpty()){
			for(Gruppi g : lst){
				if(name.equals(g.getName()))
					return g;
			}

		}
		return null;	
	}

	public boolean checkProvvedimento() {
		boolean isValid = true;
		Provvedimenti provv = getEntity();
		String tipo = "";

		//Essendoci un blocco applicativo non dovrebbe mai verificarsi
		if (provv.getType()==null || provv.getType().getCode()==null){
			FacesErrorUtils.addErrorMessage("commError", "Tipo provvedimento obbligatorio.", "Tipo provvedimento obbligatorio.");
			isValid = false;

		} else tipo = provv.getType().getCode();

		if (provv.getData()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data del provvedimento obbligatoria.", "Data del provvedimento obbligatoria.");
			isValid = false;
		}

		if (provv.getDataNotifica()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Data notifica obbligatoria.", "Data notifica obbligatoria.");
			isValid = false;
		}

		if (provv.getStatusCode()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Stato del provvedimento obbligatorio.", "Stato del provvedimento obbligatorio.");
			isValid = false;
		}

		if (provv.getEsito()==null) {
			FacesErrorUtils.addErrorMessage("commError", "Esito del provvedimento obbligatorio.", "Esito del provvedimento obbligatorio.");
			isValid = false;
		}

		if (tipo.equals("758")) {
			Iter758 iter758 = null;
			if (provv.getIter758().size()>0)
				iter758 = provv.getIter758().get(0);

			if (iter758==null) {
				FacesErrorUtils.addErrorMessage("commError", "Iter 758 assente.", "Iter 758 assente.");
				isValid = false;
			}

			List<Articoli> articoli = provv.getArticoli();
			if (articoli==null || articoli.size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un articolo.", "Inserire almeno un articolo.");
				isValid = false;
			}

			//Soggetto del provvedimento - se è un utente obbligatori dati anagrafici e ruolo
			Soggetto soggetto = provv.getSoggetto();
			if (soggetto!=null && soggetto.getCode()!=null && "Utente".equals(soggetto.getCode().getCode())) 
				if (soggetto.getRuolo()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Ruolo soggetto del provvedimento obbligatorio.", "Ruolo soggetto del provvedimento obbligatorio.");
					isValid = false;
				}

			//Controllo gruppi articoli
			HashMap<String, Boolean> grpErrors = new HashMap<String, Boolean>(2);

			//HashMap<String, Boolean> grpNamesErrors = new HashMap<String, Boolean>(2);
			//HashMap<String, Boolean> artEsitoErrors = new HashMap<String, Boolean>(2);

			ArticoliAction articoliAction = new ArticoliAction();

			if (articoli!=null && articoli.size()>0) {
				for (Articoli articolo : articoli) {

					String esito = "";
					if (articolo.getEsito()!=null)
						esito = articolo.getEsito().getCode();

					Gruppi grp = articolo.getGruppo();
					if (grp != null){
						String nomeGruppo = grp.getName();

						if (esito != null && !"".equals(esito) && nomeGruppo != null && !"".equals(nomeGruppo)){

							/*	Se l'esito per il gruppo non è ancora stato controllato, lo setta valido e lo controlla
								Altrimenti, sia che sia valido, sia che sia non valido, non fa nulla */
							if (grpErrors.get(nomeGruppo + "_" + esito)==null) {
								grpErrors.put(nomeGruppo + "_" + esito, false);

								//Se ottemperato
								if ("compliedPayed".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getAmmissionePagamento()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (articoliAction.getImportoTotale(articoli, grp.getSpeseNotifica(), nomeGruppo, "compliedPayed")==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataPagamento()==null && !grp.getPagamentoNonEffettuato())
										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se ottemperato con modi diversi	
								} else if ("complayedDifferent".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getOttemperanzaModiDiversi()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getOttemperanzaModiDiversiPM()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se inottemperato	
								} else if ("notComplayed".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getComunicazioneInottemperanza()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getComunicazioneInottemperanzaPM()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se Ammissione al pagamento senza prescrizione
								} else if ("compliedNoPrescription".equals(esito)) {
									if (grp.getAmmissionePagamentoNP()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (articoliAction.getImportoTotale(articoli, grp.getSpeseNotificaNP(), nomeGruppo, "compliedNoPrescription")==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									if (grp.getDataPagamentoNP()==null && !(grp.getPagNonEffettuatoNP()))
										grpErrors.put(nomeGruppo + "_" + esito, true);					
								}
							}
						}
					}
				}
			}

			//Controllo gli esiti dei vari gruppi 
			for (String nomeGruppo_esito : grpErrors.keySet()) { 
				//Se per la coppia [nomeGruppo, esito] è stato rilevato un errore
				if (grpErrors.get(nomeGruppo_esito)){
					String[] nomeGruppoEsito = nomeGruppo_esito.split("_");
					String msg = "Gruppo " + nomeGruppoEsito[0] + " - ";

					if ("compliedPayed".equals(nomeGruppoEsito[1]))
						msg += "Ammissione al pagamento: dati obbligatori non settati.";
					else if ("complayedDifferent".equals(nomeGruppoEsito[1]))
						msg += "Ottemperanza con modi diversi: dati obbligatori non settati.";
					else if ("notComplayed".equals(nomeGruppoEsito[1]))
						msg += "Inottemperanza: dati obbligatori non settati.";
					else if ("compliedNoPrescription".equals(nomeGruppoEsito[1]))
						msg += "Ammissione al pagamento senza prescrizione: dati obbligatori non settati.";

					FacesErrorUtils.addErrorMessage("commError", msg, msg);
					isValid = false;
				}
			}

		} else if (tipo.equals("Disp")) {

			Disposizioni disp = null;

			if (provv.getDisposizioni().size()>0)
				disp = provv.getDisposizioni().get(0);

			if (disp==null) {
				FacesErrorUtils.addErrorMessage("commError", "Disposizione assente.", "Disposizione assente.");
				isValid = false;
			}

			if (provv.getEsito()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Esito obbligatorio.", "Esito obbligatorio.");
				isValid = false;
			}

			//Soggetto del provvedimento - se è un utente obbligatori dati anagrafici e ruolo
			Soggetto soggetto = provv.getSoggetto();
			if (soggetto!=null && soggetto.getCode()!=null && "Utente".equals(soggetto.getCode().getCode())) 
				if (soggetto.getRuolo()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Ruolo soggetto del provvedimento obbligatorio.", "Ruolo soggetto del provvedimento obbligatorio.");
					isValid = false;
				}

			if (disp!=null) {
				if (disp.getRiferimentiNormativi()==null || disp.getRiferimentiNormativi().size()<1){
					FacesErrorUtils.addErrorMessage("commError", "Riferimenti normativi obbligatori.", "Riferimenti normativi obbligatori.");
					isValid = false;
				}

				if (disp.getDataEmissione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Data emissione obbligatoria.", "Data emissione obbligatoria.");
					isValid = false;
				}

				if (disp.getGiorni()==null){
					FacesErrorUtils.addErrorMessage("commError", "Giorni obbligatori.", "Giorni obbligatori.");
					isValid = false;
				}

				if (disp.getDataVerifica()==null){
					FacesErrorUtils.addErrorMessage("commError", "Data verifica obbligatoria.", "Data verifica obbligatoria.");
					isValid = false;
				}

			}

		} else if (tipo.equals("301bis")) {

			List<Articoli> articoli = provv.getArticoli();
			if (articoli==null || articoli.size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un articolo.", "Inserire almeno un articolo.");
				isValid = false;
			}

			//Controllo gruppi articoli
			HashMap<String, Boolean> grpErrors = new HashMap<String, Boolean>(2);
			ArticoliAction articoliAction = new ArticoliAction();

			if (articoli!=null && articoli.size()>0) {
				for (Articoli articolo : articoli) {

					String esito = "";
					if (articolo.getEsito()!=null)
						esito = articolo.getEsito().getCode();

					Gruppi grp = articolo.getGruppo();
					if (grp != null){
						String nomeGruppo = grp.getName();

						if (esito != null && !"".equals(esito) && nomeGruppo != null && !"".equals(nomeGruppo)){

							/*	Se l'esito per il gruppo non è ancora stato controllato, lo setta valido e lo controlla
								Altrimenti, sia che sia valido, sia che sia non valido, non fa nulla */
							if (grpErrors.get(nomeGruppo + "_" + esito)==null) {
								grpErrors.put(nomeGruppo + "_" + esito, false);

								//Se ottemperato
								if ("compliedPayed".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getAmmissionePagamento()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (articoliAction.getImportoTotale(articoli, grp.getSpeseNotifica(), nomeGruppo, "compliedPayed")==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataPagamento()==null && !grp.getPagamentoNonEffettuato())

										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se ottemperato con modi diversi	
									//								} else if ("complayedDifferent".equals(esito)) {
									//									if (grp.getGiorniIniziali()==null)
									//										grpErrors.put(nomeGruppo + "_" + esito, true);
									//									
									//									else if (grp.getDataDellaVerifica()==null)
									//										grpErrors.put(nomeGruppo + "_" + esito, true);
									//									
									//									else if (grp.getOttemperanzaModiDiversi()==null)
									//										grpErrors.put(nomeGruppo + "_" + esito, true);
									//									
									//									else if (grp.getOttemperanzaModiDiversiPM()==null)
									//										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se inottemperato	
								} else if ("notComplayed".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getComunicazioneInottemperanza()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getImportoInottemperanza()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									//Se Ammissione al pagamento senza prescrizione
								} else if ("compliedNoPrescription".equals(esito)) {
									if (grp.getGiorniIniziali()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getDataDellaVerifica()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (grp.getAmmissionePagamentoNP()==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									else if (articoliAction.getImportoTotale(articoli, grp.getSpeseNotificaNP(), nomeGruppo, "compliedNoPrescription")==null)
										grpErrors.put(nomeGruppo + "_" + esito, true);

									if (grp.getDataPagamentoNP()==null && !(grp.getPagNonEffettuatoNP()))
										grpErrors.put(nomeGruppo + "_" + esito, true);					
								}
							}
						}
					}
				}
			}

			//Controllo gli esiti dei vari gruppi 
			for (String nomeGruppo_esito : grpErrors.keySet()) { 
				//Se per la coppia [nomeGruppo, esito] è stato rilevato un errore
				if (grpErrors.get(nomeGruppo_esito)){
					String[] nomeGruppoEsito = nomeGruppo_esito.split("_");
					String msg = "Gruppo " + nomeGruppoEsito[0] + " - ";

					if ("compliedPayed".equals(nomeGruppoEsito[1]))
						msg += "Ammissione al pagamento: dati obbligatori non settati.";
					else if ("complayedDifferent".equals(nomeGruppoEsito[1]))
						msg += "Ottemperanza con modi diversi: dati obbligatori non settati.";
					else if ("notComplayed".equals(nomeGruppoEsito[1]))
						msg += "Inottemperanza: dati obbligatori non settati.";
					else if ("compliedNoPrescription".equals(nomeGruppoEsito[1]))
						msg += "Ammissione al pagamento senza prescrizione: dati obbligatori non settati.";

					FacesErrorUtils.addErrorMessage("commError", msg, msg);
					isValid = false;
				}
			}
		}else if(tipo.equals("ex14")){
			SospensioneEx14 sosp = null;
			if(provv.getSospensioneEx14()!=null && !provv.getSospensioneEx14().isEmpty()){
				sosp = provv.getSospensioneEx14().get(0);
			}

			if (sosp==null) {
				FacesErrorUtils.addErrorMessage("commError", "Sospensione assente.", "Sospensione assente.");
				isValid = false;
			}

			List<Articoli> articoli = provv.getArticoli();
			if (articoli==null || articoli.size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un articolo.", "Inserire almeno un articolo.");
				isValid = false;
			}

			Gruppi unicoGruppo = articoli.get(0).getGruppo();
			CodeValuePhi modPagamento = sosp.getModPagamento();

			if(modPagamento!=null){
				if(!Boolean.TRUE.equals(unicoGruppo.getPagamentoNonEffettuato()) && unicoGruppo.getDataPagamento()==null){
					FacesErrorUtils.addErrorMessage("commError", "Data pagamento obbligatoria.", "Data pagamento obbligatoria.");
					isValid = false;
				}

				if(!Boolean.TRUE.equals(unicoGruppo.getPagamentoNonEffettuato()) && unicoGruppo.getImportoVersato()==null){
					FacesErrorUtils.addErrorMessage("commError", "Importo versato obbligatorio.", "Importo versato obbligatorio.");
					isValid = false;
				}

				if(modPagamento.getCode().equals("dil")){
					if(!Boolean.TRUE.equals(unicoGruppo.getPagamentoNonEffettuato()) && unicoGruppo.getDataPagamentoNP()==null){
						FacesErrorUtils.addErrorMessage("commError", "Data pagamento residuo obbligatoria.", "Data pagamento residuo obbligatoria.");
						isValid = false;
					}

					if(!Boolean.TRUE.equals(unicoGruppo.getPagamentoNonEffettuato()) && unicoGruppo.getImportoVersatoNP()==null){
						FacesErrorUtils.addErrorMessage("commError", "Importo residuo versato obbligatorio.", "Importo residuo versato obbligatorio.");
						isValid = false;
					}
				}
			}
		} 

		return isValid;
	}

	public void increaseProvvedimentiCounter(){

		/*Provvedimenti prov = getEntity();
		//deve funzionare solo per le ditte soggetto di un provvedimento e solo se il provvedimento viene aggiunto la prima volta
		if(prov.getInternalId()==0 && prov.getSoggetto() != null && prov.getSoggetto().getCode().getCode().equals("Ditta") && prov.getSoggetto().getDitta() != null){
			PersoneGiuridiche pg = prov.getSoggetto().getDitta();
			pg.setNumProvvedimenti(pg.getNumProvvedimenti()+1);
		}*/

	}

	public void decreaseProvvedimentiCounter(){

		/*Provvedimenti prov = getEntity();
		//deve funzionare solo per le ditte soggetto di un provvedimento
		if(prov.getSoggetto() != null && prov.getSoggetto().getCode().getCode().equals("Ditta") && prov.getSoggetto().getDitta() != null){
			PersoneGiuridiche pg = prov.getSoggetto().getDitta();
			pg.setNumProvvedimenti(pg.getNumProvvedimenti()-1);
		}*/
	}

	public boolean checkIfSospensione() {
		boolean isValid = true;
		Provvedimenti provv = getEntity();
		String tipo = "";

		//Essendoci un blocco applicativo non dovrebbe mai verificarsi
		if (provv.getType()==null || provv.getType().getCode()==null){
			FacesErrorUtils.addErrorMessage("commError", "Tipo provvedimento obbligatorio.", "Tipo provvedimento obbligatorio.");
			isValid = false;

		} else tipo = provv.getType().getCode();


		if(tipo.equals("ex14")){
			if(provv.getCarica()==null){
				FacesErrorUtils.addErrorMessage("commError", "Destinatario del provvedimento obbligatorio.", "Destinatario del provvedimento obbligatorio.");
				isValid = false;
			}

			SospensioneEx14Action sospAction = SospensioneEx14Action.instance();
			if(!Boolean.TRUE.equals(sospAction.getTemporary().get("A")) && !Boolean.TRUE.equals(sospAction.getTemporary().get("B")) &&
					!Boolean.TRUE.equals(sospAction.getTemporary().get("C"))){
				FacesErrorUtils.addErrorMessage("commError", "Sezione Decorrenza ed efficacia: scegliere un'opzione.", "Sezione Decorrenza ed efficacia: scegliere un'opzione.");
				isValid = false;
			}
		} 

		return isValid;
	}

	public void injectArticoliProvvedimentoSosp(List<Articoli> articoli) throws PhiException{
		if(articoli == null)
			return;

		List<HashMap<String, Object>> articoliProvvList = new ArrayList<HashMap<String, Object>>();

		for(Articoli art : articoli){
			if("Legge81Fattispecie.fattisp.03".equals(art.getCode().getOid()) ||
					"Legge81Fattispecie.fattisp.06".equals(art.getCode().getOid())){
				HashMap<String, Object> articoli36 = new HashMap<String, Object>();
				articoli36.put("articoli", true);
				articoliProvvList.add(articoli36);
				break;
			}
		}

		for(Articoli art : articoli){
			if(!"Legge81Fattispecie.fattisp.03".equals(art.getCode().getOid()) &&
					!"Legge81Fattispecie.fattisp.06".equals(art.getCode().getOid())){
				HashMap<String, Object> otherArticolo = new HashMap<String, Object>();
				otherArticolo.put("otherArticolo", true);
				articoliProvvList.add(otherArticolo);
				break;
			}
		}

		injectList(articoliProvvList, "ArticoliProvvList");
	}

	public boolean isUlssEnabledToTabSosp(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter tabSospVisible = ca.get(CodeValueParameter.class, "p.home.procpratiche.tabsosp");

		if (tabSospVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(tabSospVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}


	private static String EVALUATE_NUMBER_SOSP = "select max(pv.numero) from Provvedimenti pv " +
			"join pv.type pvtype " +
			"where pv.procpratiche.uoc.parent.internalId =:internalID and pv.numero like :annoInCorso " +
			"and pvtype.oid = 'phidic.spisal.pratiche.pev.pevtype.ex14'";

	private Integer evaluateNextValueSosp(ServiceDeliveryLocation uoc){
		try{
			if (uoc==null)
				return null;

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);

			//Date date = cal.getTime();
			int annoInCorso = cal.get(Calendar.YEAR);
			String annoInCorsoLike = "%_" + String.valueOf(annoInCorso) + "_%";

			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", uoc.getParent().getInternalId());
			//parameters.put("minDate", date);
			parameters.put("annoInCorso", annoInCorsoLike);

			@SuppressWarnings("unchecked")
			List<String> ppList = (List<String>) ca.executeHQLwithParameters(EVALUATE_NUMBER_SOSP, parameters);

			Integer ret = 1;
			String numero = ppList.get(0);

			if (numero != null) {
				String[] parts = numero.split("_");
				if (parts.length==3)
					ret = Integer.parseInt(parts[parts.length-1])+1;
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}

}




