package com.phi.entities.actions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.entities.baseEntity.CessioneImp;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;
import com.phi.entities.baseEntity.SchedaRecipientiIndiv;
import com.phi.entities.baseEntity.SchedaTubazioniIndiv;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ImpPressAction")
@Scope(ScopeType.CONVERSATION)
public class ImpPressAction extends BaseAction<ImpPress, Long> {

	private static final long serialVersionUID = 2367979L;
	private static final Logger log = Logger.getLogger(ImpPressAction.class); 

	public static ImpPressAction instance() {
		return (ImpPressAction) Component.getInstance(ImpPressAction.class, ScopeType.CONVERSATION);
	}

	public List<CessioneImp> storicoCessioni(ImpPress imp){
		// Ricerca completa cessioni anche annullate
		if(imp == null)
			return null;

		try {
			String hqlCessioni = "SELECT cess FROM CessioneImp cess WHERE cess.impPress.internalId = :impId";

			Query qCessioni = ca.createQuery(hqlCessioni);
			qCessioni.setParameter("impId", imp.getInternalId());

			List<CessioneImp> storico = (List<CessioneImp>)qCessioni.getResultList();

			return storico;
		} catch (Exception e) {
			return null;
		}
	}

	public void move(ImpPress imp, CessioneImp cessione){
		try{

			//Setta la nuova sede di installazione nell'impianto
			imp.setSedeInstallazione(cessione.getSediInstallazione());

			//Setta la nuova sede di addebito nell'impianto
			//imp.setSedeAddebito(cessione.getSediAddebito());
			imp.setSedi(cessione.getSedi());

			//Setta il nuovo indirizzo di spedizione nell'impianto
			imp.setIndirizzoSped(cessione.getIndirizzoSped());

			//Associa la cessione all'impianto
			cessione.setImpPress(imp);
			//imp.getCessioneImp().add(cessione);
			imp.addCessioneImp(cessione);

			ca.create(imp);
			//ca.flushSession();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public ImpPress copy(ImpPress toCopy, CessioneImp cessione){
		try{
			ImpPress copy = new ImpPress();
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if (cessione != null && cessione.getSediInstallazioneFrom()!=null)
				si = SediInstallazioneAction.instance().copy(cessione.getSediInstallazioneFrom()); 
			else if (cessione == null && toCopy.getSedeInstallazione()!=null)
				si = SediInstallazioneAction.instance().copy(toCopy.getSedeInstallazione());

			ca.create(si);
			copy.setSedeInstallazione(si);

			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();

			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());

			ca.create(sa);
			copy.setSedeAddebito(sa); */

			/* I0070276 - Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica */
			Sedi se = new Sedi();

			if(cessione != null && cessione.getSediFrom()!=null)
				se = SediAction.instance().copy(cessione.getSediFrom());
			else if(cessione == null && toCopy.getSedi()!=null)
				se = SediAction.instance().copy(toCopy.getSedi());

			ca.create(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();

			if (cessione != null && cessione.getIndirizzoSpedFrom()!=null)
				is = IndirizzoSpedAction.instance().copy(cessione.getIndirizzoSpedFrom()); 
			else if (cessione == null && toCopy.getIndirizzoSped()!=null)
				is = IndirizzoSpedAction.instance().copy(toCopy.getIndirizzoSped());

			ca.create(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAnno(toCopy.getAnno());
			copy.setArtEsonero(toCopy.getArtEsonero());

			copy.setBombole(toCopy.getBombole());

			copy.setCapacita(toCopy.getCapacita());
			copy.setCaratteristicheSpec(toCopy.getCaratteristicheSpec());
			copy.setCategoriaRischio(toCopy.getCategoriaRischio());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setComodante(toCopy.getComodante());
			copy.setCompetenza(toCopy.getCompetenza());
			copy.setCostruttore(toCopy.getCostruttore());

			copy.setDataAssegnazione(toCopy.getDataAssegnazione());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataCostruzione(toCopy.getDataCostruzione());
			copy.setDataEsonero(toCopy.getDataEsonero());
			copy.setDataUltimaModifica(toCopy.getDataUltimaModifica());
			copy.setDataVariazioneStato(toCopy.getDataVariazioneStato());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setEnteVerificatore(toCopy.getEnteVerificatore());
			copy.setEsonero(toCopy.getEsonero());
			copy.setFluido(toCopy.getFluido());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setMatricola(toCopy.getMatricola());

			copy.setNextVerifDate1(toCopy.getNextVerifDate1());
			copy.setNextVerifDate2(toCopy.getNextVerifDate2());
			copy.setNextVerifDate3(toCopy.getNextVerifDate3());
			copy.setNote(toCopy.getNote());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());

			copy.setParteInsiemiSoggetti(toCopy.getParteInsiemiSoggetti());
			copy.setPressBar1(toCopy.getPressBar1());
			copy.setPressBar2(toCopy.getPressBar2());
			copy.setProducibilita(toCopy.getProducibilita());
			copy.setProtNumero(toCopy.getProtNumero());

			copy.setSezione(toCopy.getSezione());
			copy.setSigla(toCopy.getSigla());
			copy.setStatoImpianto(toCopy.getStatoImpianto());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setSuperficie(toCopy.getSuperficie());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());
			copy.setTempS1(toCopy.getTempS1());
			copy.setTempS2(toCopy.getTempS2());
			copy.setTempV1(toCopy.getTempV1());
			copy.setTempV2(toCopy.getTempV2());
			copy.setTipoApparecchio(toCopy.getTipoApparecchio());
			//copy.setTipoProva(toCopy.getTipoProva());
			copy.setTubazioni(toCopy.getTubazioni());

			copy.setUtenteLettera(toCopy.getUtenteLettera());


			copy.setSchedaGeneratoriIndiv(new ArrayList<SchedaGeneratoriIndiv>());
			copy.setSchedaRecipientiIndiv(new ArrayList<SchedaRecipientiIndiv>());
			copy.setSchedaTubazioniIndiv(new ArrayList<SchedaTubazioniIndiv>());

			List<SchedaGeneratoriIndiv> generatoriToCopy = toCopy.getSchedaGeneratoriIndiv();

			if(generatoriToCopy != null && !generatoriToCopy.isEmpty()){
				for(SchedaGeneratoriIndiv sg : generatoriToCopy){
					SchedaGeneratoriIndiv newSg = new SchedaGeneratoriIndiv();
					newSg.setNumeroFabbrica(sg.getNumeroFabbrica());
					newSg.setCostruttore(sg.getCostruttore());
					newSg.setPressBar1(sg.getPressBar1());
					newSg.setCapacita(sg.getCapacita());
					newSg.setSuperficie(sg.getSuperficie());
					newSg.setProducibilita(sg.getProducibilita());
					newSg.setNumero(sg.getNumero());

					try {
						ca.persist(newSg);
					} catch (PersistenceException e) {
						log.error("Error creating new SchedaGeneratoriIndiv", e);
					}
					newSg.setImpPress(copy);
					copy.addSchedaGeneratoriIndiv(newSg);
				}		
			}

			List<SchedaRecipientiIndiv> recipientiToCopy = toCopy.getSchedaRecipientiIndiv();

			if(recipientiToCopy != null && !recipientiToCopy.isEmpty()){
				for(SchedaRecipientiIndiv sr : recipientiToCopy){
					SchedaRecipientiIndiv newSr = new SchedaRecipientiIndiv();
					newSr.setNumeroFabbrica(sr.getNumeroFabbrica());
					newSr.setCostruttore(sr.getCostruttore());
					newSr.setPressBar1(sr.getPressBar1());
					newSr.setCapacita(sr.getCapacita());
					newSr.setNumero(sr.getNumero());

					try {
						ca.persist(newSr);
					} catch (PersistenceException e) {
						log.error("Error creating new SchedaRecipientiIndiv", e);
					}
					newSr.setImpPress(copy);
					copy.addSchedaRecipientiIndiv(newSr);
				}			
			}

			List<SchedaTubazioniIndiv> tubazioniToCopy = toCopy.getSchedaTubazioniIndiv();

			if(tubazioniToCopy != null && !tubazioniToCopy.isEmpty()){
				for(SchedaTubazioniIndiv st : tubazioniToCopy){
					SchedaTubazioniIndiv newSt = new SchedaTubazioniIndiv();
					newSt.setNumeroFabbrica(st.getNumeroFabbrica());
					newSt.setCostruttore(st.getCostruttore());
					newSt.setNumero(st.getNumero());

					try {
						ca.persist(newSt);
					} catch (PersistenceException e) {
						log.error("Error creating new SchedaTubazioniIndiv", e);
					}
					newSt.setImpPress(copy);
					copy.addSchedaTubazioniIndiv(newSt);
				}				
			}

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public ImpPress copy(SediInstallazione siToCopy){
		try{
			ImpPress copy = new ImpPress();
			copy.setCopy(true);
			copy.setIsActive(false);


			//Copia la sede di installazione
			SediInstallazione si = new SediInstallazione();
			if (siToCopy!=null){
				si = SediInstallazioneAction.instance().copy(siToCopy);
				si.setSede(siToCopy.getSede());
			}
			ca.create(si);
			copy.setSedeInstallazione(si);

			/* I0070276 sede addebito sostituita da sede con flag addebito
			//Istanzia la sede di addebito
			SediAddebito sa = new SediAddebito();
			ca.create(sa);

			copy.setSedeAddebito(sa); */

			Sedi se = new Sedi();
			ca.create(se);

			copy.setSedi(se);

			//Istanzia l'indirizzo di spedizione
			IndirizzoSped is = new IndirizzoSped();
			ca.create(is);

			copy.setIndirizzoSped(is);

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void initListe(){
		try{
			ImpPress impPress = getEntity();

			if (impPress.getVerificaImp()==null)
				impPress.setVerificaImp(new ArrayList<VerificaImp>());

			SchedaGeneratoriIndivAction sga = SchedaGeneratoriIndivAction.instance();
			SchedaRecipientiIndivAction sra = SchedaRecipientiIndivAction.instance();
			SchedaTubazioniIndivAction sta = SchedaTubazioniIndivAction.instance();

			if(impPress.getSchedaGeneratoriIndiv() == null)
				impPress.setSchedaGeneratoriIndiv(new ArrayList<SchedaGeneratoriIndiv>());

			if(impPress.getSchedaRecipientiIndiv() == null)
				impPress.setSchedaRecipientiIndiv(new ArrayList<SchedaRecipientiIndiv>());

			if(impPress.getSchedaTubazioniIndiv() == null)
				impPress.setSchedaTubazioniIndiv(new ArrayList<SchedaTubazioniIndiv>());

			sga.injectList(impPress.getSchedaGeneratoriIndiv());
			sra.injectList(impPress.getSchedaRecipientiIndiv());
			sta.injectList(impPress.getSchedaTubazioniIndiv());

			//this.injectList(impPress.getVerificaImp());
			//ca.create(impPress);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setTubazioniInCaratteristicheSpeciali(){
		// I0070561 setta tubazioni anche per nuovo tipo apparecchio 11 Tubazioni

		try{
			ImpPress impPress = getEntity();

			if (impPress==null)
				return;

			CodeValuePhi tipoApp = impPress.getTipoApparecchio();
			if(tipoApp==null)
				return;

			String tipoAppCode = tipoApp.getCode();
			if (tipoAppCode==null || tipoAppCode.isEmpty())
				return;

			if(!"04".equals(tipoAppCode) && !"11".equals(tipoAppCode))
				return;

			//Se 04 - Linea attrezz.re a pressione - Setta tubazioni in caratteristiche speciali;
			if (impPress.getCaratteristicheSpec()==null)
				getEntity().setCaratteristicheSpec(ca.get(CodeValuePhi.class, "phidic.arpav.imp.pressione.caratteristiche.12_V0"));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean isTubazioniRequired(){
		try{
			ImpPress impPress = getEntity();

			if (impPress==null)
				return false;

			if (impPress.getCaratteristicheSpec()==null)
				return false;

			String id = impPress.getCaratteristicheSpec().getId();

			if (id!=null && "phidic.arpav.imp.pressione.caratteristiche.12_V0".equals(id))
				return true;

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean checkImpPress(ImpPress imp){
		boolean isValid = true;
		//ImpPress imp = getEntity();

		if(imp!=null && imp.getCreatedBy()!=null && !imp.getCreatedBy().contains("Importer")){
			if (imp.getSigla()==null || "".equals(imp.getSigla())) {
				FacesErrorUtils.addErrorMessage("commError", "Sigla apparecchio obbligatoria.", "Sigla apparecchio obbligatoria.");
				isValid = false;
			}

			if (imp.getMatricola()==null || "".equals(imp.getMatricola())) {
				FacesErrorUtils.addErrorMessage("commError", "Matricola obbligatoria.", "Matricola obbligatoria.");
				isValid = false;
			}

			if (imp.getAnno()==null || "".equals(imp.getAnno())) {
				FacesErrorUtils.addErrorMessage("commError", "Anno obbligatorio.", "Anno obbligatorio.");
				isValid = false;
			}

			if(isDuplicate(imp)){
				FacesErrorUtils.addErrorMessage("commError", "Impianto già presente in anagrafica.", "Impianto già presente in anagrafica.");
				return false;				
			}

			if (imp.getInternalId()==0)
				return isValid;

			if (imp.getTipoApparecchio()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Tipo apparecchio obbligatorio.", "Tipo apparecchio obbligatorio.");
				isValid = false;
			}

			if(imp.getTipoApparecchio() != null){
				if("01".equals(imp.getTipoApparecchio().getCode()) || "02".equals(imp.getTipoApparecchio().getCode())){
					if("".equals(imp.getSuperficie()) || imp.getSuperficie()==null){
						FacesErrorUtils.addErrorMessage("commError", "Superficie Mq obbligatoria.", "Superficie Mq obbligatoria.");
						isValid = false;						
					}
					if("".equals(imp.getProducibilita()) || imp.getProducibilita()==null){
						FacesErrorUtils.addErrorMessage("commError", "Producibilità obbligatoria.", "Producibilità obbligatoria.");
						isValid = false;	
					}
				}

				if(!"11".equals(imp.getTipoApparecchio().getCode()) && !"12".equals(imp.getTipoApparecchio().getCode())){
					if (imp.getPressBar1()==null || "".equals(imp.getPressBar1())) {
						FacesErrorUtils.addErrorMessage("commError", "Pressione 1 obbligatoria.", "Pressione 1 obbligatoria.");
						isValid = false;
					}

					if (imp.getCapacita()==null || "".equals(imp.getCapacita())) {
						FacesErrorUtils.addErrorMessage("commError", "Capacità obbligatoria.", "Capacità obbligatoria.");
						isValid = false;
					}				
				}

				if(imp.getStatoImpianto()==null){
					FacesErrorUtils.addErrorMessage("commError", "Stato impianto obbligatorio.", "Stato impianto obbligatorio.");
					isValid = false;				
				}
				if(!imp.getCopy()){
					Serializable id = ca.getIdentifier(entity);
					try {
						List<ImpPress> history = ca.getHistoryof(entityClass, id, new String[]{"dataVariazioneStato"});
						Collections.reverse(history);
						if(imp.getDataVariazioneStato()==null){
							//la data variazione è obbligatoria...
							//1.se lo stato impianto non è regolarmente attivo
							if(imp.getStatoImpianto()!=null && !"00".equals(imp.getStatoImpianto().getCode())){
								FacesErrorUtils.addErrorMessage("commError", "Data variazione stato impianto obbligatoria.", "Data variazione stato impianto obbligatoria.");
								isValid = false;	
								//2.se l'impianto torna ad essere attivo dopo un period di di inattività => se l'ultima revisione ha data
							}else if(!history.isEmpty() && history.get(0).getDataVariazioneStato()!=null) {
								FacesErrorUtils.addErrorMessage("commError", "Data variazione stato impianto obbligatoria.", "Data variazione stato impianto obbligatoria.");
								isValid = false;
							}
						}else {
							for(ImpPress h : history){
								if(h.getDataVariazioneStato()!=null && imp.getDataVariazioneStato().before(h.getDataVariazioneStato())){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
									FacesErrorUtils.addErrorMessage("commError", 
											"Data variazione stato impianto minore di una precedente data variazione stato: inserire un valore maggiore o uguale a "+sdf.format(h.getDataVariazioneStato())+".", 
											"Data variazione stato impianto minore di una precedente data variazione stato: inserire un valore maggiore o uguale a "+sdf.format(h.getDataVariazioneStato())+".");
									isValid = false;
									break;
								}
							}
						}
					} catch (Exception e) {
						log.error("Error getting history filtered by dataVariazioneStato for ImpTerra#"+id);
					}
				}
			}

			if(imp.getEsonero() != null){
				String codeEsonero = imp.getEsonero().getCode();
				if(("01".equals(codeEsonero) || "02".equals(codeEsonero) || "03".equals(codeEsonero) || "04".equals(codeEsonero)) && imp.getDataEsonero()==null){
					FacesErrorUtils.addErrorMessage("commError", "Data esonero obbligatoria.", "Data esonero obbligatoria.");
					isValid = false;						
				}

			}

			// H00101044
			if(imp.getSedeInstallazione() != null){
				if(imp.getSedi() == null){
					FacesErrorUtils.addErrorMessage("commError", "Attenzione! Selezionare la sede di addebito.", "Attenzione! Selezionare la sede di addebito.");
					isValid = false;					
				}
			}

		}

		return isValid;
	}

	public boolean isDuplicate(ImpPress imp) {

		if(imp.getCopy())
			return false;

		String hqlImp = "SELECT COUNT(i) FROM ImpPress i WHERE i.isActive = 1 AND " +
				"i.internalId != :id AND i.sigla = :sigla AND i.matricola = :matricola AND i.anno = :anno";

		if(imp.getCreatedBy()!=null && imp.getCreatedBy().contains("Importer"))
			hqlImp += " AND i.createdBy NOT LIKE '%Importer%'";

		Query qImp = ca.createQuery(hqlImp);
		qImp.setParameter("id", imp.getInternalId());
		if(imp.getSigla()!=null)
			qImp.setParameter("sigla", imp.getSigla());
		else
			qImp.setParameter("sigla", "");
		if(imp.getMatricola()!=null)
			qImp.setParameter("matricola", imp.getMatricola());
		else
			qImp.setParameter("matricola", "");
		if(imp.getAnno()!=null)
			qImp.setParameter("anno", imp.getAnno());
		else
			qImp.setParameter("anno", "");

		Long count = (Long)qImp.getSingleResult();
		if(count!=null && count>0){
			return true;
		}
		return false;
	}

	public boolean isNumFabbricaDuplicate(ImpPress imp){
		if(imp == null)
			return false;

		ImpPress impOrig=imp;
		if(imp.getCopy()) {
			if(entity==null){
				return false;
			}else {
				impOrig=entity;
			}
		}

		if(imp.getNumeroFabbrica()!=null && !("".equals(imp.getNumeroFabbrica()))){
			String hqlImp = "SELECT COUNT(i) FROM ImpPress i WHERE i.isActive = 1 AND " +
					"i.internalId != :id AND i.numeroFabbrica = :num AND i.copy = 0";

			Query qImp = ca.createQuery(hqlImp);
			qImp.setParameter("id", impOrig.getInternalId());
			qImp.setParameter("num", imp.getNumeroFabbrica());

			Long count = (Long)qImp.getSingleResult();
			if(count!=null && count>0){
				return true;
			}
		}
		return false;
	}

	public void updateCopy(ImpPress copy, CessioneImp cessione) {
		try {

			if(cessione == null)
				return;

			//Aggiorna la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = copy.getSedeInstallazione();

			if (cessione != null && cessione.getSediInstallazioneFrom()!=null){
				si = SediInstallazioneAction.instance().copy(cessione.getSediInstallazioneFrom());
				ca.create(si);
				copy.setSedeInstallazione(si);
			}

			// Aggiorna la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = copy.getSedi();

			if(cessione != null && cessione.getSediFrom()!=null){
				se = SediAction.instance().copy(cessione.getSediFrom());
				ca.create(se);
				copy.setSedi(se);
			}
			copy.setSedi(se);

			//Aggiorna l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = copy.getIndirizzoSped();

			if (cessione != null && cessione.getIndirizzoSpedFrom()!=null){
				is = IndirizzoSpedAction.instance().copy(cessione.getIndirizzoSpedFrom()); 
				ca.create(is);
				copy.setIndirizzoSped(is);
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);		
		}
	}

	private void filterBy() {
		try {
			HashMap<String, Object> thisLike = (FilterMap)this.getLike();
			HashMap<String, Object> thisEqual = (FilterMap)this.getEqual();

			ImpiantoAction impAction = ImpiantoAction.instance();

			HashMap<String, Object> impTemp = impAction.getTemporary();
			HashMap<String, Object> impLike = (FilterMap)impAction.getLike();
			HashMap<String, Object> impEqual = (FilterMap)impAction.getEqual();

			removeExpression("this", "sigla");
			removeExpression("this", "matricola");
			removeExpression("this", "anno");

			thisLike.remove("indirizzoSped.denominazione");
			thisLike.remove("indirizzoSped.addr.str");
			thisEqual.remove("enteVerificatore");
			thisLike.remove("note");

			thisLike.remove("sedeInstallazione.denominazione");
			thisLike.remove("sedeInstallazione.addr.str");
			thisEqual.remove("sedeInstallazione.addr.code");

			thisLike.remove("sedi.denominazioneUnitaLocale");
			thisLike.remove("sedi.addr.str");
			thisEqual.remove("sedi.addr.code");

			//Sigla - matricola - anno / corrispondenza
			String sigla = "";
			String matricola = "";
			String anno = "";
			String corrispondenza = "";

			if (impTemp.get("sigla")!=null)
				sigla = impTemp.get("sigla").toString();

			if (impTemp.get("matricola")!=null)
				matricola = impTemp.get("matricola").toString();

			if (impTemp.get("anno")!=null)
				anno = impTemp.get("anno").toString();

			if (impTemp.get("esattaContiene")!=null)
				corrispondenza = impTemp.get("esattaContiene").toString();			

			if ("".equals(corrispondenza) || "Esatta".equals(corrispondenza)){
				if (!"".equals(sigla))
					thisEqual.put("sigla", sigla);

				if (!"".equals(matricola))
					thisEqual.put("matricola", matricola);

				if (!"".equals(anno))
					thisEqual.put("anno", anno);

			} else if (!"".equals(sigla) || !"".equals(matricola) || !"".equals(anno)){

				/* filtro con criteriaquery - mette in or i paramentri di ricerca */
				if (!"".equals(sigla) && !sigla.contains("%"))
					sigla = "%" + sigla + "%";

				if (!"".equals(matricola) && !matricola.contains("%"))
					matricola = "%" + matricola + "%";

				if (!"".equals(anno) && !anno.contains("%"))
					anno = "%" + anno + "%";

				LogicalExpression logicalDates = Restrictions.or(Restrictions.like("sigla", sigla).ignoreCase(),
						Restrictions.or(Restrictions.like("matricola", matricola).ignoreCase(),
								Restrictions.like("anno", anno).ignoreCase())	);

				entityCriteria.add(logicalDates);
			}

			//Sede di istallazione
			String denominazioneSI = null;
			if (impLike.get("sedeInstallazione.denominazione")!=null)
				denominazioneSI = impLike.get("sedeInstallazione.denominazione").toString();

			if (denominazioneSI!=null && !"".equals(denominazioneSI))
				thisLike.put("sedeInstallazione.denominazione", denominazioneSI);

			String addrStrSI = null;
			if (impLike.get("sedeInstallazione.addr.str")!=null)
				addrStrSI = impLike.get("sedeInstallazione.addr.str").toString();

			if (addrStrSI!=null && !"".equals(addrStrSI))
				thisLike.put("sedeInstallazione.addr.str", addrStrSI);

			CodeValue addrCodeSI = null;
			if (impEqual.get("sedeInstallazione.addr.code")!=null)
				addrCodeSI = (CodeValue)impEqual.get("sedeInstallazione.addr.code");

			if (addrCodeSI!=null)
				thisEqual.put("sedeInstallazione.addr.code", addrCodeSI);

			//Sede d'addebito
			String denominazioneSA = null;
			if (impLike.get("sedi.denominazioneUnitaLocale")!=null)
				denominazioneSA = impLike.get("sedi.denominazioneUnitaLocale").toString();

			if (denominazioneSA!=null && !"".equals(denominazioneSA))
				thisLike.put("sedi.denominazioneUnitaLocale", denominazioneSA);

			String addrStrSA = null;
			if (impLike.get("sedi.addr.str")!=null)
				addrStrSA = impLike.get("sedi.addr.str").toString();

			if (addrStrSA!=null && !"".equals(addrStrSA))
				thisLike.put("sedi.addr.str", addrStrSA);

			CodeValue addrCodeSA = null;
			if (impEqual.get("sedi.addr.code")!=null)
				addrCodeSA = (CodeValue)impEqual.get("sedi.addr.code");

			if (addrCodeSA!=null && !"".equals(addrCodeSA))
				thisEqual.put("sedi.addr.code", addrCodeSA);

			//Descrizione spedizione - Indirizzo di spedizione - Ente verificator
			String denominazioneIS = "";

			if (impTemp.get("denominazioneIS")!=null)
				denominazioneIS = impTemp.get("denominazioneIS").toString();

			if(!"".equals(denominazioneIS)){
				if(!denominazioneIS.startsWith("%"))
					denominazioneIS = "%" + denominazioneIS;

				if(!denominazioneIS.endsWith("%"))
					denominazioneIS = denominazioneIS + "%";

				thisLike.put("indirizzoSped.denominazione", denominazioneIS);
			}

			String addrStrIS = "";
			if (impTemp.get("addrStrIS")!=null)
				addrStrIS = impTemp.get("addrStrIS").toString();

			if(!"".equals(addrStrIS))
				thisLike.put("indirizzoSped.addr.str", addrStrIS);

			CodeValue enteVerificatore = null;
			if (impTemp.get("enteVerificatore")!=null)
				enteVerificatore = (CodeValue)impTemp.get("enteVerificatore");

			if(enteVerificatore!=null)
				thisEqual.put("enteVerificatore", enteVerificatore);

			String note = null;
			if (impLike.get("note")!=null)
				note = impLike.get("note").toString();

			if(note!=null)
				thisLike.put("note", note);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void setSubFilters() {
		try {

			HashMap<String, Object> temp = this.getTemporary();
			List<String> caratteristiche = new ArrayList<String>();

			if (temp.get("AP00")==null?false:(Boolean)temp.get("AP00"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.00_V0");

			if (temp.get("AP01")==null?false:(Boolean)temp.get("AP01"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.01_V0");

			if (temp.get("AP02")==null?false:(Boolean)temp.get("AP02"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.02_V0");

			if (temp.get("AP03")==null?false:(Boolean)temp.get("AP03"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.03_V0");

			if (temp.get("AP04")==null?false:(Boolean)temp.get("AP04"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.04_V0");

			if (temp.get("AP05")==null?false:(Boolean)temp.get("AP05"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.05_V0");

			if (temp.get("AP06")==null?false:(Boolean)temp.get("AP06"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.06_V0");

			if (temp.get("AP07")==null?false:(Boolean)temp.get("AP07"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.07_V0");

			if (temp.get("AP08")==null?false:(Boolean)temp.get("AP08"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.08_V0");

			if (temp.get("AP09")==null?false:(Boolean)temp.get("AP09"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.09_V0");

			if (temp.get("AP10")==null?false:(Boolean)temp.get("AP10"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.10_V0");

			if (temp.get("AP11")==null?false:(Boolean)temp.get("AP11"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.11_V0");

			if (temp.get("AP12")==null?false:(Boolean)temp.get("AP12"))
				caratteristiche.add("phidic.arpav.imp.pressione.caratteristiche.12_V0");

			if (caratteristiche.size()>0)
				((FilterMap)getEqual()).putOr("caratteristicheSpec.id", caratteristiche.toArray());
			else 
				((FilterMap)getEqual()).remove("caratteristicheSpec.id");

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void readAsImpianti() throws PhiException {
		try {

			this.setFullLike(true);

			this.setReadPageSize(150);
			//this.setReadPageSize(20);

			this.getEqual().put("isActive",true);
			this.getNotEqual().put("copy",true);
			this.getOrderBy().put("denominazione","descending");

			this.filterBy();
			this.setSubFilters();
			this.filterExpired();
			this.copyFiltersFromSuper();
			this.filterWithoutVerifNew();
			
			ImpiantoAction.instance().getTemporary().put("rowCount", this.countResults());

			List<ImpPress> lst = select();

			if (lst instanceof LazyList){
				IdataModel<ImpPress> dm = new PagedDataModel<ImpPress>((LazyList)lst, "Impianto");
				Contexts.getConversationContext().set("ImpiantoList", dm);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void filterExpired() {
		try {

			removeExpression("this", "nextVerifDate1");
			removeExpression("this", "nextVerifDate2");
			removeExpression("this", "nextVerifDate3");

			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();

			//includiScadute serve solo a cancellare scadenzaFrom...
			Boolean includiScadute = Boolean.TRUE.equals(temp.get("includiScadute"));
			Date scadenzaFrom = (Date) temp.get("scadenzaFrom");
			Date scadenzaTo = (Date) temp.get("scadenzaTo");
			if(includiScadute)
				scadenzaFrom = null;

			if(scadenzaTo != null){
				if(scadenzaFrom == null) {
					LogicalExpression datesTo = Restrictions.or(
							Restrictions.le("nextVerifDate1", scadenzaTo), 
							Restrictions.or(
									Restrictions.le("nextVerifDate2", scadenzaTo),
									Restrictions.le("nextVerifDate3", scadenzaTo)));

					entityCriteria.add(datesTo);
				}else if(scadenzaFrom != null) {
					LogicalExpression datesFromTo = Restrictions.or(
							Restrictions.between("nextVerifDate1", scadenzaFrom, scadenzaTo), 
							Restrictions.or(
									Restrictions.between("nextVerifDate2", scadenzaFrom, scadenzaTo),
									Restrictions.between("nextVerifDate3", scadenzaFrom, scadenzaTo)));

					entityCriteria.add(datesFromTo);
				}
			}else if (scadenzaFrom != null) {
				LogicalExpression datesFrom = 	Restrictions.or(
						Restrictions.ge("nextVerifDate1", scadenzaFrom), 
						Restrictions.or(
								Restrictions.ge("nextVerifDate2", scadenzaFrom),
								Restrictions.ge("nextVerifDate3", scadenzaFrom)));

				entityCriteria.add(datesFrom);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void updateImpiantoFromCopy(ImpPress imp, ImpPress cpy) {
		imp.setDataUltimaModifica(new Date());

		imp.setArtEsonero(cpy.getArtEsonero());
		imp.setBombole(cpy.getBombole());
		imp.setCapacita(cpy.getCapacita());
		imp.setCaratteristicheSpec(cpy.getCaratteristicheSpec());
		imp.setCategoriaRischio(cpy.getCategoriaRischio());
		imp.setComodante(cpy.getComodante());
		imp.setCompetenza(cpy.getCompetenza());
		imp.setCostruttore(cpy.getCostruttore());
		imp.setDataCostruzione(cpy.getDataCostruzione());
		imp.setDataEsonero(cpy.getDataEsonero());
		imp.setEsonero(cpy.getEsonero());
		imp.setFluido(cpy.getFluido());
		imp.setLetteraTrasm(cpy.getLetteraTrasm());
		imp.setNote(cpy.getNote());
		imp.setNumeroFabbrica(cpy.getNumeroFabbrica());
		imp.setPressBar1(cpy.getPressBar1());
		imp.setPressBar2(cpy.getPressBar2());
		imp.setProducibilita(cpy.getProducibilita());
		imp.setProtNumero(cpy.getProtNumero());
		imp.setSezione(cpy.getSezione());
		imp.setSuperficie(cpy.getSuperficie());
		imp.setTempS1(cpy.getTempS1());
		imp.setTempS2(cpy.getTempS2());
		imp.setTempV1(cpy.getTempV1());
		imp.setTempV2(cpy.getTempV2());
		imp.setTipoApparecchio(cpy.getTipoApparecchio());
		imp.setTubazioni(cpy.getTubazioni());
		imp.setUtenteLettera(cpy.getUtenteLettera());
		// solo per ImpPress è nella sez. Ultima verifica
		// imp.setDataCollaudo(cpy.getDataCollaudo()); 

		//TODO: SCHEDE GENERATORI
		for(SchedaGeneratoriIndiv s : imp.getSchedaGeneratoriIndiv()) {
			s.setImpPress(null);
			try {
				ca.delete(s);
			} catch (PersistenceException e) {
				log.error("Error deleting SchedaGeneratoriIndiv #"+s.getInternalId(), e);
			}
		}

		for(SchedaRecipientiIndiv s : imp.getSchedaRecipientiIndiv()) {
			s.setImpPress(null);
			try {
				ca.delete(s);
			} catch (PersistenceException e) {
				log.error("Error deleting SchedaRecipientiIndiv #"+s.getInternalId(), e);
			}
		}

		for(SchedaTubazioniIndiv s : imp.getSchedaTubazioniIndiv()) {
			s.setImpPress(null);
			try {
				ca.delete(s);
			} catch (PersistenceException e) {
				log.error("Error deleting SchedaTubazioniIndiv #"+s.getInternalId(), e);
			}
		}

		imp.setSchedaGeneratoriIndiv(new ArrayList<SchedaGeneratoriIndiv>());
		imp.setSchedaRecipientiIndiv(new ArrayList<SchedaRecipientiIndiv>());
		imp.setSchedaTubazioniIndiv(new ArrayList<SchedaTubazioniIndiv>());

		List<SchedaGeneratoriIndiv> generatoriToCopy = cpy.getSchedaGeneratoriIndiv();

		if(generatoriToCopy != null && !generatoriToCopy.isEmpty()){
			for(SchedaGeneratoriIndiv sg : generatoriToCopy){
				SchedaGeneratoriIndiv newSg = new SchedaGeneratoriIndiv();
				newSg.setNumeroFabbrica(sg.getNumeroFabbrica());
				newSg.setCostruttore(sg.getCostruttore());
				newSg.setPressBar1(sg.getPressBar1());
				newSg.setCapacita(sg.getCapacita());
				newSg.setSuperficie(sg.getSuperficie());
				newSg.setProducibilita(sg.getProducibilita());
				newSg.setNumero(sg.getNumero());

				newSg.setImpPress(imp);
				imp.addSchedaGeneratoriIndiv(newSg);
				try {
					ca.create(newSg);
				} catch (PersistenceException e) {
					log.error("Error creating new SchedaGeneratoriIndiv", e);
				}
			}		
		}

		List<SchedaRecipientiIndiv> recipientiToCopy = cpy.getSchedaRecipientiIndiv();

		if(recipientiToCopy != null && !recipientiToCopy.isEmpty()){
			for(SchedaRecipientiIndiv sr : recipientiToCopy){
				SchedaRecipientiIndiv newSr = new SchedaRecipientiIndiv();
				newSr.setNumeroFabbrica(sr.getNumeroFabbrica());
				newSr.setCostruttore(sr.getCostruttore());
				newSr.setPressBar1(sr.getPressBar1());
				newSr.setCapacita(sr.getCapacita());
				newSr.setNumero(sr.getNumero());

				newSr.setImpPress(imp);
				imp.addSchedaRecipientiIndiv(newSr);
				try {
					ca.create(newSr);
				} catch (PersistenceException e) {
					log.error("Error creating new SchedaRecipientiIndiv", e);
				}
			}			
		}

		List<SchedaTubazioniIndiv> tubazioniToCopy = cpy.getSchedaTubazioniIndiv();

		if(tubazioniToCopy != null && !tubazioniToCopy.isEmpty()){
			for(SchedaTubazioniIndiv st : tubazioniToCopy){
				SchedaTubazioniIndiv newSt = new SchedaTubazioniIndiv();
				newSt.setNumeroFabbrica(st.getNumeroFabbrica());
				newSt.setCostruttore(st.getCostruttore());
				newSt.setNumero(st.getNumero());

				newSt.setImpPress(imp);
				imp.addSchedaTubazioniIndiv(newSt);
				try {
					ca.create(newSt);
				} catch (PersistenceException e) {
					log.error("Error creating new SchedaTubazioniIndiv", e);
				}
			}				
		}
	}

	public void copyFiltersFromSuper(){
		ImpiantoAction ia = ImpiantoAction.instance();
		HashMap <String, Object> superLikeFilters = ia.getLike();
		HashMap <String, Object> superEqualFilters = ia.getEqual();
		
		if(superLikeFilters.get("sedeInstallazione.denominazione")!=null && ! ((String)superLikeFilters.get("sedeInstallazione.denominazione")).isEmpty()){
			this.getLike().put("sedeInstallazione.denominazione", superLikeFilters.get("sedeInstallazione.denominazione"));
		}
		
		if(superLikeFilters.get("sedeInstallazione.addr.str")!=null && ! ((String)superLikeFilters.get("sedeInstallazione.addr.str")).isEmpty()){
			this.getLike().put("sedeInstallazione.addr.str", superLikeFilters.get("sedeInstallazione.addr.str"));
		}
		
		if(superEqualFilters.get("sedeInstallazione.addr.cpa")!=null && ! ((String)superEqualFilters.get("sedeInstallazione.addr.cpa")).isEmpty()){
			this.getEqual().put("sedeInstallazione.addr.cpa", superEqualFilters.get("sedeInstallazione.addr.cpa"));
		}
		
		if(superEqualFilters.get("sedeInstallazione.addr.zip")!=null && ! ((String)superEqualFilters.get("sedeInstallazione.addr.zip")).isEmpty()){
			this.getEqual().put("sedeInstallazione.addr.zip", superEqualFilters.get("sedeInstallazione.addr.zip"));
		}
		
		if(superEqualFilters.get("sedeInstallazione.addr.cty")!=null && ! ((String)superEqualFilters.get("sedeInstallazione.addr.cty")).isEmpty()){
			this.getEqual().put("sedeInstallazione.addr.cty", superEqualFilters.get("sedeInstallazione.addr.cty"));
		}
		
		if(superEqualFilters.get("sedeInstallazione.addr.code")!=null){
			this.getEqual().put("sedeInstallazione.addr.code", superEqualFilters.get("sedeInstallazione.addr.code"));
		}
		
		if(superLikeFilters.get("sedi.denominazioneUnitaLocale")!=null && ! ((String)superLikeFilters.get("sedi.denominazioneUnitaLocale")).isEmpty()){
			this.getLike().put("sedi.denominazioneUnitaLocale", superLikeFilters.get("sedi.denominazioneUnitaLocale"));
		}
		
		if(superLikeFilters.get("sedi.addr.str")!=null && ! ((String)superLikeFilters.get("sedi.addr.str")).isEmpty()){
			this.getLike().put("sedi.addr.str", superLikeFilters.get("sedi.addr.str"));
		}
		
		if(superEqualFilters.get("sedi.addr.cpa")!=null && ! ((String)superEqualFilters.get("sedi.addr.cpa")).isEmpty()){
			this.getEqual().put("sedi.addr.cpa", superEqualFilters.get("sedi.addr.cpa"));
		}
		
		if(superEqualFilters.get("sedi.addr.zip")!=null && ! ((String)superEqualFilters.get("sedi.addr.zip")).isEmpty()){
			this.getEqual().put("sedi.addr.zip", superEqualFilters.get("sedi.addr.zip"));
		}
		
		if(superEqualFilters.get("sedi.addr.cty")!=null && ! ((String)superEqualFilters.get("sedi.addr.cty")).isEmpty()){
			this.getEqual().put("sedi.addr.cty", superEqualFilters.get("sedi.addr.cty"));
		}
		
		if(superEqualFilters.get("sedi.addr.code")!=null){
			this.getEqual().put("sedi.addr.code", superEqualFilters.get("sedi.addr.code"));
		}
		
		if(superLikeFilters.get("indirizzoSped.denominazione")!=null && ! ((String)superLikeFilters.get("indirizzoSped.denominazione")).isEmpty()){
			this.getLike().put("indirizzoSped.denominazione", superLikeFilters.get("indirizzoSped.denominazione"));
		}
		
		if(superLikeFilters.get("indirizzoSped.denominazione")!=null && ! ((String)superLikeFilters.get("indirizzoSped.denominazione")).isEmpty()){
			this.getLike().put("indirizzoSped.denominazione", superLikeFilters.get("indirizzoSped.denominazione"));
		}
		
		if(superEqualFilters.get("enteVerificatore")!=null && ! ((String)superEqualFilters.get("enteVerificatore")).isEmpty()){
			this.getEqual().put("enteVerificatore", superEqualFilters.get("enteVerificatore"));
		}
		
	}

	private void filterWithoutVerif() {
		try {

			removeExpression("this", "verificaImp");

			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();

			Boolean senzaVerifiche = Boolean.TRUE.equals(temp.get("noVerifiche"));

			if(!senzaVerifiche)
				return;

			if(findSubCriteria("verificaImp")==null)
				entityCriteria.createAlias("verificaImp", "verificaImp", Criteria.LEFT_JOIN);

			entityCriteria.add(Restrictions.isEmpty("verificaImp"));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public Long countResults(){
		List<Integer> multiresult = entityCriteria.setFirstResult(0).setProjection(Projections.rowCount()).list();
		Long total = 0L;
		if(multiresult!=null){
			for(Integer n : multiresult){
				if(n!=null){
					total += n;
				}
			}
		}
	
		entityCriteria.setProjection(entityProjections);
		entityCriteria.setResultTransformer(resultTranformer);
		return total;
	}

	private void filterWithoutVerifNew() {
		try {
	
			this.getEqual().remove("verificheLong");
	
			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();
	
			Boolean senzaVerifiche = Boolean.TRUE.equals(temp.get("noVerifiche"));
	
			if(!senzaVerifiche)
				return;
	
			this.getEqual().put("verificheLong", 0L);
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

}