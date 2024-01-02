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
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ImpTerraAction")
@Scope(ScopeType.CONVERSATION)
public class ImpTerraAction extends BaseAction<ImpTerra, Long> {

	private static final long serialVersionUID = 2431884L;
    private static final Logger log = Logger.getLogger(ImpTerraAction.class); 

	public static ImpTerraAction instance() {
		return (ImpTerraAction) Component.getInstance(ImpTerraAction.class, ScopeType.CONVERSATION);
	}

	public List<CessioneImp> storicoCessioni(ImpTerra imp){
		// Ricerca completa cessioni anche annullate
		if(imp == null)
			return null;
		
		try {
			String hqlCessioni = "SELECT cess FROM CessioneImp cess WHERE cess.impTerra.internalId = :impId";

			Query qCessioni = ca.createQuery(hqlCessioni);
			qCessioni.setParameter("impId", imp.getInternalId());

			List<CessioneImp> storico = (List<CessioneImp>)qCessioni.getResultList();

			return storico;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void move(ImpTerra imp, CessioneImp cessione){
		try{
			
			//Setta la nuova sede di installazione nell'impianto
			imp.setSedeInstallazione(cessione.getSediInstallazione());
			
			//Setta la nuova sede di addebito nell'impianto
			//imp.setSedeAddebito(cessione.getSediAddebito());
			imp.setSedi(cessione.getSedi());
			
			//Setta il nuovo indirizzo di spedizione nell'impianto
			imp.setIndirizzoSped(cessione.getIndirizzoSped());
			
			//Associa la cessione all'impianto
			cessione.setImpTerra(imp);
			//imp.getCessioneImp().add(cessione);
			imp.addCessioneImp(cessione);

			ca.create(imp);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean checkImpTerra(ImpTerra imp){
		boolean isValid = true;
		//ImpTerra imp = getEntity();
		
		if(imp!=null && imp.getCreatedBy()!=null && !imp.getCreatedBy().contains("Importer")){
			if (imp.getSigla()==null || "".equals(imp.getSigla())) {
				FacesErrorUtils.addErrorMessage("commError", "Sigla apparecchio obbligatoria.", "Sigla apparecchio obbligatoria.");
				isValid = false;
			}
			
			if (imp.getSubTypeTerra()==null) {
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

			if(imp.getSubTypeTerra()!=null){
				
				if ("01".equals(imp.getSubTypeTerra().getCode())){
					
					if (imp.getTipologia()!=null && "48".equals(imp.getTipologia().getCode()) && imp.getStruttAutopNum()==null) {
						FacesErrorUtils.addErrorMessage("commError", "Numero strutture autoprotette obbligatorio.", "Numero strutture autoprotette obbligatorio.");
						isValid = false;
					}
				}
				
				if ("02".equals(imp.getSubTypeTerra().getCode())){
					
					if (imp.getSubTypeB()==null) {
						FacesErrorUtils.addErrorMessage("commError", "Sottotipo impianto B obbligatorio.", "Sottotipo impianto B obbligatorio.");
						isValid = false;
					}

					if (imp.getPot()==null || "".equals(imp.getPot())) {
						FacesErrorUtils.addErrorMessage("commError", "Potenza impianto obbligatoria.", "Potenza impianto obbligatoria.");
						isValid = false;
					}
					
					if (imp.getCabineCode()==null) {
						FacesErrorUtils.addErrorMessage("commError", "Cabine obbligatorio.", "Cabine obbligatorio.");
						isValid = false;
					}else if("YES".equals(imp.getCabineCode().getCode()) && imp.getCabineNum()==null){
						FacesErrorUtils.addErrorMessage("commError", "Numero cabine obbligatorio.", "Numero cabine obbligatorio.");
						isValid = false;
					}
				}

				if ("03".equals(imp.getSubTypeTerra().getCode()) && imp.getCi0()==null && imp.getCi1()==null && imp.getCi2()==null && imp.getCi3()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Compilare almeno un campo CI.", "Compilare almeno un campo CI.");
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
					List<ImpTerra> history = ca.getHistoryof(entityClass, id, new String[]{"dataVariazioneStato"});
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
						for(ImpTerra h : history){
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

			if(imp.getSubTypeB()!=null){
				
				String code = imp.getSubTypeB().getCode();
				
				if ("01".equals(code) || "02".equals(code)) {
					
					Double powerDbl = imp.getPot();
					
					if (powerDbl!=null){
						if("01".equals(code) && powerDbl>1000){
							FacesErrorUtils.addErrorMessage("commError", "La potenza deve essere minore o uguale a 1000 kW.", "La potenza deve essere minore o uguale a 1000 kW.");
							isValid = false;
						}
						
						if("02".equals(code) && powerDbl<=1000){
							FacesErrorUtils.addErrorMessage("commError", "La potenza deve essere maggiore di 1000 kW.", "La potenza deve essere maggiore di 1000 kW.");
							isValid = false;						
						}
					}
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
	
	public ImpTerra copy(ImpTerra toCopy, CessioneImp cessione){
		try{
			ImpTerra copy = new ImpTerra();
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
			copy.setAreaPeric(toCopy.getAreaPeric());
			copy.setArtEsonero(toCopy.getArtEsonero());
			copy.setAste(toCopy.getAste());

			copy.setCabine(toCopy.getCabine());
			copy.setCabineCode(toCopy.getCabineCode());
			copy.setCabineNum(toCopy.getCabineNum());
//			copy.setCantieri(toCopy.getCantieri());
			copy.setCi0(toCopy.getCi0());
			copy.setCi1(toCopy.getCi1());
			copy.setCi2(toCopy.getCi2());
			copy.setCi3(toCopy.getCi3());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setCompetenza(toCopy.getCompetenza());
			
			copy.setDataAssegnazione(toCopy.getDataAssegnazione());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataUltimaModifica(toCopy.getDataUltimaModifica());
			copy.setDataVariazioneStato(toCopy.getDataVariazioneStato());
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setDisperdenti(toCopy.getDisperdenti());
			copy.setDispersori(toCopy.getDispersori());

			copy.setEnteVerificatore(toCopy.getEnteVerificatore());

			copy.setImpAutoprod(toCopy.getImpAutoprod());
			copy.setImpColl(toCopy.getImpColl());
//			copy.setIsolanti01(toCopy.getIsolanti01());
//			copy.setIsolanti02(toCopy.getIsolanti02());

			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setMatricola(toCopy.getMatricola());
//			copy.setMetalliche(toCopy.getMetalliche());
			
			copy.setNextVerifDate1(toCopy.getNextVerifDate1());
			copy.setNextVerifDate2(toCopy.getNextVerifDate2());
			copy.setNextVerifDate3(toCopy.getNextVerifDate3());
			copy.setNote(toCopy.getNote());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			
			copy.setPar(toCopy.getPar());
//			copy.setParafulmini(toCopy.getParafulmini());
			copy.setPot(toCopy.getPot());
			copy.setProtNumero(toCopy.getProtNumero());

			copy.setRaggruppati01(toCopy.getRaggruppati01());
			copy.setRaggruppati02(toCopy.getRaggruppati02());
			
//			copy.setSerbatoi(toCopy.getSerbatoi());
			copy.setSezione(toCopy.getSezione());
			copy.setSigla(toCopy.getSigla());
			copy.setStatoImpianto(toCopy.getStatoImpianto());
//			copy.setStruttAereop(toCopy.getStruttAereop());
//			copy.setStruttAutopCode(toCopy.getStruttAutopCode());
			copy.setStruttAutopNum(toCopy.getStruttAutopNum());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeB(toCopy.getSubTypeB());
			copy.setSubTypeC(toCopy.getSubTypeC());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setSuperf01(toCopy.getSuperf01());
			copy.setSuperf02(toCopy.getSuperf02());
			copy.setSuperf03(toCopy.getSuperf03());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());
			copy.setTipologia(toCopy.getTipologia());
			copy.setTipologiaTesto(toCopy.getTipologiaTesto());
			copy.setType(toCopy.getType());
			
			copy.setUtenteLettera(toCopy.getUtenteLettera());

			return copy;			
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
//	public void copyFromSedeInstallazione(SediInstallazione si){
//		try{
//			Vocabularies vocabularies = VocabulariesImpl.instance();
//			ImpTerra it = getEntity();
//			
//			it.setCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "impType", "05"));
//
//		} catch (Exception ex) {
//			log.error(ex);
//			throw new RuntimeException(ex);
//		} 
//	}
	
	public String getSupTot(String superf01, String superf02, String superf03){
		try{
			Double somma=0.0;
						
			if (superf01!=null && !superf01.isEmpty())
				somma += Double.parseDouble(superf01.replace(",", "."));
			
			if (superf02!=null && !superf02.isEmpty())
				somma += Double.parseDouble(superf02.replace(",", "."));

			if (superf03!=null && !superf03.isEmpty())
				somma += Double.parseDouble(superf03.replace(",", "."));
			
			if (somma>0)
				return somma.toString();
			
			else return "";
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void initListe(){
		try{
			ImpTerra impTerra = getEntity();
			
			if (impTerra.getVerificaImp()==null)
				impTerra.setVerificaImp(new ArrayList<VerificaImp>());
			
			//this.injectList(impTerra.getVerificaImp());
			//ca.create(impTerra);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean isDuplicate(ImpTerra imp) {
		
		if(imp.getCopy())
			return false;
		
		String hqlImp = "SELECT COUNT(i) FROM ImpTerra i " +
				"WHERE i.isActive = 1 AND i.internalId != :id AND i.sigla = :sigla AND i.matricola = :matricola " +
				"AND i.anno = :anno AND i.subTypeTerra.code = :code";
		
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
		
		if(imp.getSubTypeTerra()!=null && imp.getSubTypeTerra().getCode()!=null)
			qImp.setParameter("code", imp.getSubTypeTerra().getCode());
		else
			qImp.setParameter("code", "");

		Long count = (Long)qImp.getSingleResult();
		
		if(count!=null && count>0)
			return true;
		
		return false;
	}
	
	public boolean isNumFabbricaDuplicate(ImpTerra imp){
		if(imp.getNumeroFabbrica()!=null && !("".equals(imp.getNumeroFabbrica()))){
			String hqlImp = "SELECT COUNT(i) FROM ImpTerra i WHERE i.isActive = 1 AND " +
					"i.internalId != :id AND i.numeroFabbrica = :num";

			Query qImp = ca.createQuery(hqlImp);
			qImp.setParameter("id", imp.getInternalId());
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
	
	private void setSubFilters() {
		try {

			HashMap<String, Object> temp = this.getTemporary();
			List<String> subTypes = new ArrayList<String>();

			if (temp.get("A")==null?false:(Boolean)temp.get("A"))
				subTypes.add("phidic.arpav.imp.terra.type01.01_V0");

			if (temp.get("B")==null?false:(Boolean)temp.get("B"))
				subTypes.add("phidic.arpav.imp.terra.type01.02_V0");

			if (temp.get("C")==null?false:(Boolean)temp.get("C"))
				subTypes.add("phidic.arpav.imp.terra.type01.03_V0");

			if (subTypes.size()>0)
				((FilterMap)getEqual()).putOr("subTypeTerra.id", subTypes.toArray());
			else 
				((FilterMap)getEqual()).remove("subTypeTerra.id");
			
			/* Pulizia filtri specifici per la tipologia */
			if (!subTypes.contains("phidic.arpav.imp.terra.type01.01_V0"))
				((FilterMap)getEqual()).remove("tipologia");
			
			if (!subTypes.contains("phidic.arpav.imp.terra.type01.02_V0")){
				((FilterMap)getEqual()).remove("subTypeB");
				((FilterMap)getGreaterEqual()).remove("pot");
				((FilterMap)getLessEqual()).remove("pot");
			}

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
			
			List<ImpTerra> lst = select();
			
			if (lst instanceof LazyList){
				IdataModel<ImpTerra> dm = new PagedDataModel<ImpTerra>((LazyList)lst, "Impianto");
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
	
	public void updateImpiantoFromCopy(ImpTerra imp, ImpTerra cpy){
		imp.setDataUltimaModifica(new Date());
		
		imp.setAreaPeric(cpy.getAreaPeric());
		imp.setAste(cpy.getAste());
		imp.setCabine(cpy.getCabine());
		imp.setCabineCode(cpy.getCabineCode());
		imp.setCabineNum(cpy.getCabineNum());
//		imp.setCantieri(cpy.getCantieri());
		imp.setCi0(cpy.getCi0());
		imp.setCi1(cpy.getCi1());
		imp.setCi2(cpy.getCi2());
		imp.setCi3(cpy.getCi3());
		imp.setCompetenza(cpy.getCompetenza());
		imp.setDataCollaudo(cpy.getDataCollaudo());
		imp.setDisperdenti(cpy.getDisperdenti());
		imp.setDispersori(cpy.getDispersori());
		imp.setImpAutoprod(cpy.getImpAutoprod());
		imp.setImpColl(cpy.getImpColl());
//		imp.setIsolanti01(cpy.getIsolanti01());
//		imp.setIsolanti02(cpy.getIsolanti02());
		imp.setLetteraTrasm(cpy.getLetteraTrasm());
//		imp.setMetalliche(cpy.getMetalliche());
		imp.setNote(cpy.getNote());
//		imp.setParafulmini(cpy.getParafulmini());
		imp.setPot(cpy.getPot());
		imp.setProtNumero(cpy.getProtNumero());
		imp.setRaggruppati01(cpy.getRaggruppati01());
		imp.setRaggruppati02(cpy.getRaggruppati02());
//		imp.setSerbatoi(cpy.getSerbatoi());
//		imp.setStruttAereop(cpy.getStruttAereop());
//		imp.setStruttAutopCode(cpy.getStruttAutopCode());
		imp.setStruttAutopNum(cpy.getStruttAutopNum());
		imp.setSubTypeB(cpy.getSubTypeB());
		imp.setSuperf01(cpy.getSuperf01());
		imp.setSuperf02(cpy.getSuperf02());
		imp.setSuperf03(cpy.getSuperf03());
		imp.setTipologia(cpy.getTipologia());
		imp.setTipologiaTesto(cpy.getTipologiaTesto());
		imp.setUtenteLettera(cpy.getUtenteLettera());
	}
	
	public void cleanTipologiaRelatedFields(ImpTerra imp){
		if(imp == null)
			return;
		
		imp.setTipologiaTesto(null);
		imp.setAste(null);
		imp.setSuperf01(null);
		imp.setSuperf02(null);
		imp.setSuperf03(null);
		imp.setRaggruppati01(null);
		imp.setRaggruppati02(null);
		imp.setDisperdenti(null);
		imp.setStruttAutopNum(null);
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