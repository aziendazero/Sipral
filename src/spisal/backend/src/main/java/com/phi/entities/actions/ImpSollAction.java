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
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.entities.baseEntity.CessioneImp;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ImpSollAction")
@Scope(ScopeType.CONVERSATION)
public class ImpSollAction extends BaseAction<ImpSoll, Long> {

	private static final long serialVersionUID = 2412238L;
    private static final Logger log = Logger.getLogger(ImpSollAction.class); 

	public static ImpSollAction instance() {
		return (ImpSollAction) Component.getInstance(ImpSollAction.class, ScopeType.CONVERSATION);
	}

	public List<CessioneImp> storicoCessioni(ImpSoll imp){
		// Ricerca completa cessioni anche annullate
		if(imp == null)
			return null;
		
		try {
			String hqlCessioni = "SELECT cess FROM CessioneImp cess WHERE cess.impSoll.internalId = :impId";

			Query qCessioni = ca.createQuery(hqlCessioni);
			qCessioni.setParameter("impId", imp.getInternalId());

			List<CessioneImp> storico = (List<CessioneImp>)qCessioni.getResultList();

			return storico;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void move(ImpSoll imp, CessioneImp cessione){
		try{
			
			//Setta la nuova sede di installazione nell'impianto
			imp.setSedeInstallazione(cessione.getSediInstallazione());
			
			//Setta la nuova sede di addebito nell'impianto
			//imp.setSedeAddebito(cessione.getSediAddebito());
			imp.setSedi(cessione.getSedi());
			
			//Setta il nuovo indirizzo di spedizione nell'impianto
			imp.setIndirizzoSped(cessione.getIndirizzoSped());
			
			//Associa la cessione all'impianto
			cessione.setImpSoll(imp);
			//imp.getCessioneImp().add(cessione);
			imp.addCessioneImp(cessione);

			ca.create(imp);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public ImpSoll copy(ImpSoll toCopy, CessioneImp cessione){
		try{
			ImpSoll copy = new ImpSoll();
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

			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setCompetenza(toCopy.getCompetenza());
			copy.setCostrRadioc(toCopy.getCostrRadioc());
			copy.setCostruttore(toCopy.getCostruttore());
			copy.setCostruzione(toCopy.getCostruzione());
			
			copy.setDataAssegnazione(toCopy.getDataAssegnazione());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataInstRadioc(toCopy.getDataInstRadioc());
			copy.setDataUltimaModifica(toCopy.getDataUltimaModifica());
			copy.setDataVariazioneStato(toCopy.getDataVariazioneStato());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setEnteVerificatore(toCopy.getEnteVerificatore());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());

			copy.setMarcCE(toCopy.getMarcCE());
			copy.setMarcaturaCe(toCopy.getMarcaturaCe());
			copy.setMatricola(toCopy.getMatricola());
			copy.setMezzo(toCopy.getMezzo());
			copy.setNextVerifDate1(toCopy.getNextVerifDate1());
			copy.setNextVerifDate2(toCopy.getNextVerifDate2());
			copy.setNextVerifDate3(toCopy.getNextVerifDate3());
			copy.setNote(toCopy.getNote());
			copy.setNumRadioc(toCopy.getNumRadioc());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());

			copy.setPortata(toCopy.getPortata());
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setSezione(toCopy.getSezione());
			copy.setSigla(toCopy.getSigla());
			copy.setStatoImpianto(toCopy.getStatoImpianto());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());
			copy.setTipoFabb(toCopy.getTipoFabb());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setVelocitaMax(toCopy.getVelocitaMax());
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

//	public void copyFromSedeInstallazione(SediInstallazione si){
//		try{
//			Vocabularies vocabularies = VocabulariesImpl.instance();
//			ImpSoll is = getEntity();
//			
//			is.setCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "impType", "04"));
//
//		} catch (Exception ex) {
//			log.error(ex);
//			throw new RuntimeException(ex);
//		} 
//	}
	
	public void initListe(){
		try{
			ImpSoll impSoll = getEntity();
			
			if (impSoll.getVerificaImp()==null)
				impSoll.setVerificaImp(new ArrayList<VerificaImp>());
			
			//this.injectList(impSoll.getVerificaImp());
			//ca.create(impSoll);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean checkImpSoll(ImpSoll imp){
		boolean isValid = true;
		//ImpSoll imp = getEntity();
		
		if(imp!=null && imp.getCreatedBy()!=null && !imp.getCreatedBy().contains("Importer")){
			if (imp.getSigla()==null || "".equals(imp.getSigla())) {
				FacesErrorUtils.addErrorMessage("commError", "Sigla apparecchio obbligatoria.", "Sigla apparecchio obbligatoria.");
				isValid = false;
			}
			
			if(imp.getSubTypeSoll()==null){
				FacesErrorUtils.addErrorMessage("commError", "Tipo impianto obbligatorio.", "Tipo impianto obbligatorio.");
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
				FacesErrorUtils.addErrorMessage("commError", "IMPIANTO GIA' PRESENTE IN ANAGRAFICA.", "IMPIANTO GIA' PRESENTE IN ANAGRAFICA.");
				return false;				
			}
			
			if (imp.getInternalId()==0)
				return isValid;
			
			if(imp.getSubType()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sottotipo obbligatorio.", "Sottotipo obbligatorio.");
				isValid = false;				
			}

			
			if(imp.getStatoImpianto()==null){
				FacesErrorUtils.addErrorMessage("commError", "Stato impianto obbligatorio.", "Stato impianto obbligatorio.");
				isValid = false;				
			}
			if(!imp.getCopy()){
				Serializable id = ca.getIdentifier(entity);
				try {
					List<ImpSoll> history = ca.getHistoryof(entityClass, id, new String[]{"dataVariazioneStato"});
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
						for(ImpSoll h : history){
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
	
	public boolean isDuplicate(ImpSoll imp) {
		
		if(imp.getCopy())
			return false;

		String hqlImp = "SELECT COUNT(i) FROM ImpSoll i " +
				"WHERE i.isActive = 1 AND i.internalId != :id AND i.sigla = :sigla AND i.matricola = :matricola " +
				"AND i.anno = :anno AND i.subTypeSoll.code = :code";
		
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
		
		if(imp.getSubTypeSoll()!=null && imp.getSubTypeSoll().getCode()!=null)
			qImp.setParameter("code", imp.getSubTypeSoll().getCode());
		else
			qImp.setParameter("code", "");

		Long count = (Long)qImp.getSingleResult();
		if(count!=null && count>0)
			return true;
		
		return false;
	}

	public boolean isNumFabbricaDuplicate(ImpSoll imp){
		if(imp == null)
			return false;
		
		ImpSoll impOrig=imp;
		if(imp.getCopy()) {
			if(entity==null){
				return false;
			}else {
				impOrig=entity;
			}
		}
		
		if(imp.getNumeroFabbrica()!=null && !("".equals(imp.getNumeroFabbrica()))){
			String hqlImp = "SELECT COUNT(i) FROM ImpSoll i WHERE i.isActive = 1 AND " +
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
			List<String> subTypes = new ArrayList<String>();

			if (temp.get("D")==null?false:(Boolean)temp.get("D"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.01_V0");

			if (temp.get("E")==null?false:(Boolean)temp.get("E"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.02_V0");

			if (temp.get("F")==null?false:(Boolean)temp.get("F"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.03_V0");

			if (temp.get("G")==null?false:(Boolean)temp.get("G"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.04_V0");

			if (temp.get("H")==null?false:(Boolean)temp.get("H"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.05_V0");

			if (temp.get("I")==null?false:(Boolean)temp.get("I"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.06_V0");

			if (temp.get("L")==null?false:(Boolean)temp.get("L"))
				subTypes.add("phidic.arpav.imp.sollevamento.type1.07_V0");

			if (subTypes.size()>0)
				((FilterMap)getEqual()).putOr("subTypeSoll.id", subTypes.toArray());
			else 
				((FilterMap)getEqual()).remove("subTypeSoll.id");

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
			
			List<ImpSoll> lst = select();
			
			if (lst instanceof LazyList){
				IdataModel<ImpSoll> dm = new PagedDataModel<ImpSoll>((LazyList)lst, "Impianto");
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
	
	public void updateImpiantoFromCopy(ImpSoll imp, ImpSoll cpy){
		imp.setDataUltimaModifica(new Date());
		
		imp.setCompetenza(cpy.getCompetenza());
		imp.setCostrRadioc(cpy.getCostrRadioc());
		imp.setCostruttore(cpy.getCostruttore());
		imp.setCostruzione(cpy.getCostruzione());
		imp.setDataCollaudo(cpy.getDataCollaudo());
		imp.setDataInstRadioc(cpy.getDataInstRadioc());
		imp.setLetteraTrasm(cpy.getLetteraTrasm());
		imp.setMarcaturaCe(cpy.getMarcaturaCe());
		imp.setMezzo(cpy.getMezzo());
		imp.setNote(cpy.getNote());
		imp.setNumRadioc(cpy.getNumRadioc());
		imp.setNumeroFabbrica(cpy.getNumeroFabbrica());
		imp.setPortata(cpy.getPortata());
		imp.setProtNumero(cpy.getProtNumero());
		imp.setSubType(cpy.getSubType());
		imp.setTarga(cpy.getTarga());
		imp.setTelaio(cpy.getTelaio());
		imp.setTipoFabb(cpy.getTipoFabb());
		imp.setUtenteLettera(cpy.getUtenteLettera());
		imp.setVelocitaMax(cpy.getVelocitaMax());
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