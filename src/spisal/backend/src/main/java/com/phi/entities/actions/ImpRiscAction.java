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
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.SchedaGeneratori;
import com.phi.entities.baseEntity.SchedaVasi;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ImpRiscAction")
@Scope(ScopeType.CONVERSATION)
public class ImpRiscAction extends BaseAction<ImpRisc, Long> {

	private static final long serialVersionUID = 2383429L;
	private static final Logger log = Logger.getLogger(ImpRiscAction.class); 

	public static ImpRiscAction instance() {
		return (ImpRiscAction) Component.getInstance(ImpRiscAction.class, ScopeType.CONVERSATION);
	}

	public List<CessioneImp> storicoCessioni(ImpRisc imp){
		// Ricerca completa cessioni anche annullate
		if(imp == null)
			return null;

		try {
			String hqlCessioni = "SELECT cess FROM CessioneImp cess WHERE cess.impRisc.internalId = :impId";

			Query qCessioni = ca.createQuery(hqlCessioni);
			qCessioni.setParameter("impId", imp.getInternalId());

			List<CessioneImp> storico = (List<CessioneImp>)qCessioni.getResultList();

			return storico;
		} catch (Exception e) {
			return null;
		}
	}

	public void move(ImpRisc imp, CessioneImp cessione){
		try{

			//Setta la nuova sede di installazione nell'impianto
			imp.setSedeInstallazione(cessione.getSediInstallazione());

			//Setta la nuova sede di addebito nell'impianto
			//imp.setSedeAddebito(cessione.getSediAddebito());
			imp.setSedi(cessione.getSedi());

			//Setta il nuovo indirizzo di spedizione nell'impianto
			imp.setIndirizzoSped(cessione.getIndirizzoSped());

			//Associa la cessione all'impianto
			cessione.setImpRisc(imp);
			//imp.getCessioneImp().add(cessione);
			imp.addCessioneImp(cessione);

			ca.create(imp);
			//ca.flushSession();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double setSuperf(){

		Double pot = getPot("glob");

		Double sup = 0.0;

		if(pot!=null)
			sup = (pot*860)/10000;

		return sup;
	}

	public ImpRisc copy(ImpRisc toCopy, CessioneImp cessione){
		try{
			ImpRisc copy = new ImpRisc();
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

			copy.setDataAssegnazione(toCopy.getDataAssegnazione());
			copy.setDataAutocert1(toCopy.getDataAutocert1());
			copy.setDataAutocert2(toCopy.getDataAutocert2());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataUltimaModifica(toCopy.getDataUltimaModifica());
			copy.setDataVariazioneStato(toCopy.getDataVariazioneStato());
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setDescrizImpianto(toCopy.getDescrizImpianto());
			copy.setDescrizLocali(toCopy.getDescrizLocali());
			copy.setDestImp(toCopy.getDestImp());

			copy.setEnteVerificatore(toCopy.getEnteVerificatore());
			copy.setGiorniAA(toCopy.getGiorniAA());
			copy.setImpianto(toCopy.getImpianto());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());

			copy.setManutentore(toCopy.getManutentore());
			copy.setMatricola(toCopy.getMatricola());
			copy.setNextVerifDate1(toCopy.getNextVerifDate1());
			copy.setNextVerifDate2(toCopy.getNextVerifDate2());
			copy.setNextVerifDate3(toCopy.getNextVerifDate3());
			copy.setNote(toCopy.getNote());
			copy.setNumGen(toCopy.getNumGen());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());

			copy.setOreGG(toCopy.getOreGG());
			copy.setPotGlob(toCopy.getPotGlob());
			copy.setPotGlobNom(toCopy.getPotGlobNom());
			copy.setProtNumero(toCopy.getProtNumero());

			copy.setSezione(toCopy.getSezione());
			copy.setSigla(toCopy.getSigla());
			copy.setStatoImpianto(toCopy.getStatoImpianto());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setSuperficieRisc(toCopy.getSuperficieRisc());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());
			//copy.setTipoProva(toCopy.getTipoProva());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setVasiAperti(toCopy.getVasiAperti());
			copy.setVasiChiusi(toCopy.getVasiChiusi());

			copy.setSchedaGeneratori(new ArrayList<SchedaGeneratori>());
			copy.setSchedaVasi(new ArrayList<SchedaVasi>());

			List<SchedaGeneratori> generatoriToCopy = toCopy.getSchedaGeneratori();

			if(generatoriToCopy != null && !generatoriToCopy.isEmpty()){
				for(SchedaGeneratori sn : generatoriToCopy){
					SchedaGeneratori newSn = new SchedaGeneratori();
					newSn.setCodiceComb(sn.getCodiceComb());
					newSn.setCodiceCombCv(sn.getCodiceCombCv());
					newSn.setCostruttore(sn.getCostruttore());
					newSn.setNumero(sn.getNumero());
					newSn.setNumeroFabbrica(sn.getNumeroFabbrica());
					newSn.setPotGlob(sn.getPotGlob());
					newSn.setPotGlobNom(sn.getPotGlobNom());
					newSn.setPressMax(sn.getPressMax());
					newSn.setType(sn.getType());

					try {
						ca.persist(newSn);
					} catch (PersistenceException e) {
						log.error("Error creating new SchedaGeneratori", e);
					}
					newSn.setImpRisc(copy);
					copy.addSchedaGeneratori(newSn);
				}			
			}

			List<SchedaVasi> vasiToCopy = toCopy.getSchedaVasi();

			if(vasiToCopy != null && !vasiToCopy.isEmpty()){
				for(SchedaVasi sv : vasiToCopy){
					SchedaVasi newSv = new SchedaVasi();
					newSv.setCapacita(sv.getCapacita());
					newSv.setClasse(sv.getClasse());
					newSv.setCodiceVaso1(sv.getCodiceVaso1());
					newSv.setCodiceVaso2(sv.getCodiceVaso2());
					newSv.setCodiceVaso3(sv.getCodiceVaso3());
					newSv.setNumero(sv.getNumero());
					newSv.setPress(sv.getPress());

					try {
						ca.persist(newSv);
					} catch (PersistenceException e) {
						log.error("Error creating new SchedaVasi", e);
					}
					newSv.setImpRisc(copy);
					copy.addSchedaVasi(newSv);
				}				
			}

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//	public void copyFromSedeInstallazione(SediInstallazione si){
	//		try{
	//			Vocabularies vocabularies = VocabulariesImpl.instance();
	//			ImpRisc ir = getEntity();
	//			
	//			ir.setCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "impType", "02"));
	//
	//		} catch (Exception ex) {
	//			log.error(ex);
	//			throw new RuntimeException(ex);
	//		} 
	//	}

	public void initListe(){
		try{
			ImpRisc impRisc = getEntity();

			if (impRisc.getVerificaImp()==null)
				impRisc.setVerificaImp(new ArrayList<VerificaImp>());

			SchedaVasiAction sva = SchedaVasiAction.instance();
			SchedaGeneratoriAction sga = SchedaGeneratoriAction.instance();

			if (impRisc.getSchedaVasi()==null)
				impRisc.setSchedaVasi(new ArrayList<SchedaVasi>());

			if (impRisc.getSchedaGeneratori()==null)
				impRisc.setSchedaGeneratori(new ArrayList<SchedaGeneratori>());

			sga.injectList(impRisc.getSchedaGeneratori());	
			sva.injectList(impRisc.getSchedaVasi());

			//this.injectList(impRisc.getVerificaImp());
			//ca.create(impRisc);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Double getPot(String type){
		try{
			ImpRisc ir = getEntity();

			if (ir==null || type==null || "".equals(type))
				return 0.0;

			Double p = getPot(type, ir);

			return p;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Double getPot(String type, ImpRisc ir){
		try{
			if (ir==null || type==null || "".equals(type))
				return 0.0;

			List<SchedaGeneratori> sgList = ir.getSchedaGeneratori();

			if (sgList==null || sgList.size()<1)
				return 0.0;

			Double potGlob=0.0;

			for (SchedaGeneratori sg : sgList) {

				Double ps = null;

				if ("glob".equals(type))
					ps=sg.getPotGlob();
				else if ("nom".equals(type))
					ps=sg.getPotGlobNom();

				if (ps!=null)
					potGlob+=ps;
			}

			if (potGlob==null)
				return 0.0;

			return potGlob;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void updateSuperf(ImpRisc imp){
		try {
			if (imp==null)
				return;

			Double pot = getPot("glob", imp);

			Double superficieRisc = pot*860/10000;
			imp.setSuperficieRisc(superficieRisc);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void updatePot(){
		try{
			ImpRisc ir = getEntity();
			if (ir==null)
				return;

			Double potGlob = getPot("glob");
			if (potGlob!=null)
				ir.setPotGlob(potGlob); 

			Double potGlobNom = getPot("nom");
			if (potGlobNom!=null)
				ir.setPotGlobNom(potGlobNom);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean checkImpRisc(ImpRisc imp){
		boolean isValid = true;
		//ImpRisc imp = getEntity();

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
				FacesErrorUtils.addErrorMessage("commError", "IMPIANTO GIA' PRESENTE IN ANAGRAFICA.", "IMPIANTO GIA' PRESENTE IN ANAGRAFICA.");
				return false;				
			}

			if (imp.getInternalId()==0)
				return isValid;

			if (imp.getImpianto()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Impianto obbligatorio.", "Impianto obbligatorio.");
				isValid = false;
			}

			if (imp.getDescrizImpianto()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Destinazione impianto obbligatoria.", "Destinazione impianto obbligatoria.");
				isValid = false;
			}

			if (imp.getDescrizLocali()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Destinazione locali obbligatoria.", "Destinazione locali obbligatoria.");
				isValid = false;
			}

			if (imp.getVasiAperti()==null || "".equals(imp.getVasiAperti())) {
				FacesErrorUtils.addErrorMessage("commError", "Vasi aperti obbligatori.", "Vasi aperti obbligatori.");
				isValid = false;
			}

			if (imp.getVasiChiusi()==null || "".equals(imp.getVasiChiusi())) {
				FacesErrorUtils.addErrorMessage("commError", "Vasi chiusi obbligatori.", "Vasi chiusi obbligatori.");
				isValid = false;
			}

			if (imp.getNumGen()==null || "".equals(imp.getNumGen())) {
				FacesErrorUtils.addErrorMessage("commError", "Numero generatori obbligatorio.", "Numero generatori obbligatorio.");
				isValid = false;
			}

			if (imp.getStatoImpianto()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Stato impianto obbligatorio.", "Stato impianto obbligatorio.");
				isValid = false;
			}
			if(!imp.getCopy()){
				Serializable id = ca.getIdentifier(entity);
				try {
					List<ImpRisc> history = ca.getHistoryof(entityClass, id, new String[]{"dataVariazioneStato"});
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
						for(ImpRisc h : history){
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

			if(imp.getSchedaGeneratori()!=null && !imp.getCopy()){
				if (imp.getSchedaGeneratori().isEmpty()) {
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un generatore.", "Inserire almeno un generatore.");
					isValid = false;
				}				
			}

			/**/
			if (imp.getSuperficieRisc()==null || "".equals(imp.getSuperficieRisc())) {
				FacesErrorUtils.addErrorMessage("commError", "Superficie riscaldata obbligatoria.", "Superficie riscaldata obbligatoria.");
				isValid = false;
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

	public boolean isDuplicate(ImpRisc imp) {

		if(imp.getCopy())
			return false;

		String hqlImp = "SELECT COUNT(i) FROM ImpRisc i WHERE i.isActive = 1 AND " +
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

	public boolean isNumFabbricaDuplicate(ImpRisc imp){
		if(imp.getNumeroFabbrica()!=null && !("".equals(imp.getNumeroFabbrica()))){
			String hqlImp = "SELECT COUNT(i) FROM ImpRisc i WHERE i.isActive = 1 AND " +
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
			
			List<ImpRisc> lst = select();

			if (lst instanceof LazyList){
				IdataModel<ImpRisc> dm = new PagedDataModel<ImpRisc>((LazyList)lst, "Impianto");
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

	public void updateImpiantoFromCopy(ImpRisc imp, ImpRisc cpy){
		imp.setDataUltimaModifica(new Date());

		imp.setDataAutocert1(cpy.getDataAutocert1());
		imp.setDataAutocert2(cpy.getDataAutocert2());
		imp.setDataCollaudo(cpy.getDataCollaudo());
		imp.setDescrizImpianto(cpy.getDescrizImpianto());
		imp.setDescrizLocali(cpy.getDescrizLocali());
		imp.setGiorniAA(cpy.getGiorniAA());
		imp.setImpianto(cpy.getImpianto());
		imp.setLetteraTrasm(cpy.getLetteraTrasm());
		imp.setManutentore(cpy.getManutentore());
		imp.setNote(cpy.getNote());
		imp.setNumGen(cpy.getNumGen());
		imp.setOreGG(cpy.getOreGG());
		imp.setPotGlob(cpy.getPotGlob());
		imp.setPotGlobNom(cpy.getPotGlobNom());
		imp.setProtNumero(cpy.getProtNumero());
		imp.setSuperficieRisc(cpy.getSuperficieRisc());
		imp.setUtenteLettera(cpy.getUtenteLettera());
		imp.setVasiAperti(cpy.getVasiAperti());
		imp.setVasiChiusi(cpy.getVasiChiusi());

		//TODO: SCHEDE GENERATORI
		for(SchedaGeneratori s : imp.getSchedaGeneratori()) {
			s.setImpRisc(null);
			try {
				ca.delete(s);
			} catch (PersistenceException e) {
				log.error("Error deleting SchedaGeneratori #"+s.getInternalId(), e);
			}
		}

		for(SchedaVasi s : imp.getSchedaVasi()) {
			s.setImpRisc(null);
			try {
				ca.delete(s);
			} catch (PersistenceException e) {
				log.error("Error deleting SchedaVasi #"+s.getInternalId(), e);
			}
		}

		imp.setSchedaGeneratori(new ArrayList<SchedaGeneratori>());
		imp.setSchedaVasi(new ArrayList<SchedaVasi>());

		List<SchedaGeneratori> generatoriToCopy = cpy.getSchedaGeneratori();

		if(generatoriToCopy != null && !generatoriToCopy.isEmpty()){
			for(SchedaGeneratori sn : generatoriToCopy){
				SchedaGeneratori newSn = new SchedaGeneratori();
				newSn.setCodiceComb(sn.getCodiceComb());
				newSn.setCodiceCombCv(sn.getCodiceCombCv());
				newSn.setCostruttore(sn.getCostruttore());
				newSn.setNumero(sn.getNumero());
				newSn.setNumeroFabbrica(sn.getNumeroFabbrica());
				newSn.setPotGlob(sn.getPotGlob());
				newSn.setPotGlobNom(sn.getPotGlobNom());
				newSn.setPressMax(sn.getPressMax());
				newSn.setType(sn.getType());

				newSn.setImpRisc(imp);
				imp.addSchedaGeneratori(newSn);
				try {
					ca.create(newSn);
				} catch (PersistenceException e) {
					log.error("Error creating new SchedaGeneratori", e);
				}
			}			
		}

		List<SchedaVasi> vasiToCopy = cpy.getSchedaVasi();

		if(vasiToCopy != null && !vasiToCopy.isEmpty()){
			for(SchedaVasi sv : vasiToCopy){
				SchedaVasi newSv = new SchedaVasi();
				newSv.setCapacita(sv.getCapacita());
				newSv.setClasse(sv.getClasse());
				newSv.setCodiceVaso1(sv.getCodiceVaso1());
				newSv.setCodiceVaso2(sv.getCodiceVaso2());
				newSv.setCodiceVaso3(sv.getCodiceVaso3());
				newSv.setNumero(sv.getNumero());
				newSv.setPress(sv.getPress());

				newSv.setImpRisc(imp);
				imp.addSchedaVasi(newSv);
				try {
					ca.create(newSv);
				} catch (PersistenceException e) {
					log.error("Error creating new SchedaVasi", e);
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