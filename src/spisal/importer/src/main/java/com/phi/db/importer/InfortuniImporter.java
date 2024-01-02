package com.phi.db.importer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.InchiestaInfortunio;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.PrevnetNotes;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.TagFascicolo;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.InfortuniPrevnet;
import com.prevnet.entities.Infortunirelazioni;
import com.prevnet.entities.Prognosi;
import com.prevnet.mappings.MapEsposti;
import com.prevnet.mappings.MapFascicoli;
import com.prevnet.mappings.MapInfortuni;

@SuppressWarnings({"unchecked"})
public class InfortuniImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(InfortuniImporter.class.getName());
	
	private static InfortuniImporter instance = null;
	
	public static InfortuniImporter getInstance() {
		if(instance == null) {
			instance = new InfortuniImporter();
		}
		return instance;
	}
	
	public void aggiornaInfPolesana(InfortuniPrevnet source) {
		if(checkMapping(source.getIdinfortuni())){
			Infortuni target = getMapped(source.getIdinfortuni());
			
			if(source.getDatainf()!=null)
				target.setData(source.getDatainf());
			if(source.getDatasegnalazione()!=null)
				target.setNotificationData(source.getDatasegnalazione());
			
			target.setApplicant(getMappedCode(source.getSegnalazione(), ulss));
			
			Query q = targetEm.createNativeQuery("select count(*) from z_infortuni z join revinfo r on r.id = z.rev where z.internal_id = :id and r.username<>'phi-esb'");
			BigInteger count = (BigInteger) q.setParameter("id", target.getInternalId()).getSingleResult();
			if(count==null || count.intValue()<=0) {
				saveOnTarget(target);
				thislog.warn("Aggiornato infortunio id: "+source.getIdinfortuni());
			}
		}
	}
	
	public void aggiornaInfortuni(InfortuniPrevnet source) {
		if(checkMapping(source.getIdinfortuni())){
			Infortuni target = getMapped(source.getIdinfortuni());
			//managePrognosi(source, target);
			manageAteco(target);
			Query q = targetEm.createNativeQuery("select count(*) from z_infortuni z join revinfo r on r.id = z.rev where z.internal_id = :id and r.username<>'phi-esb'");
			BigInteger count = (BigInteger) q.setParameter("id", target.getInternalId()).getSingleResult();
			if(count==null || count.intValue()<=0) {
				saveOnTarget(target);
				thislog.warn("Aggiornato infortunio id: "+source.getIdinfortuni());
			}
		}
	}
	
	public Infortuni importInfortunio(InfortuniPrevnet source, Procpratiche pratica){
		if(!checkMapping(source.getIdinfortuni())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			
			Infortuni target = new Infortuni();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			target.setProcpratiche(pratica);
			
			if(pratica.getInfortuni()==null)
				pratica.setInfortuni(new ArrayList<Infortuni>());
			
			pratica.getInfortuni().add(target);
			
			target.setData(source.getDatainf());
			if(source.getUtenteInf()!=null){
				Person infortunato = persImp.importPerson(source.getUtenteInf());
				target.setPerson(infortunato);
				//RICHIESTA DI CHIOGGIA
				PraticheRiferimenti refs = pratica.getPraticheRiferimenti();
				if(refs==null){
					refs=new PraticheRiferimenti();
					refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
					refs.setCreationDate(new Date());

				}
				refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
				refs.setRiferimentoUtente(infortunato);
				saveOnTarget(refs);
				pratica.setPraticheRiferimenti(refs);
			}
			
			if(source.getDitta()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDitta());
				if(sede!=null){
					target.setSedi(sede);
					target.setPersoneGiuridiche(sede.getPersonaGiuridica());
				}				
			}else if(source.getAzienda()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getAzienda());
				if(sede!=null){
					target.setSedi(sede);
					target.setPersoneGiuridiche(sede.getPersonaGiuridica());
				}	
			}
			//INFORTUNI EXT
			importInfortuniExt(source, target);
			
			//LUOGO INFORTUNIO
			manageLuogoInfortunio(source, target);
			
			//GIORNI DI PROGNOSI
			managePrognosi(source, target);
			
			//ATECO
			manageAteco(target);
			
			target.setApplicant(getMappedCode(source.getSegnalazione(), ulss));
			target.setNoteprognosi(source.getNoteprognosi());			
			target.setGravita(getMappedCode(source.getEsitoInfortunio(), ulss));		
			target.getInfortuniExt().setGravitaFinale((CodeValuePhi)target.getGravita());
			target.setNaturaLesione(getMappedCode(source.getInailTipo(), ulss));			
			target.setSedeLesione(getMappedCode(source.getInailSede(), ulss));			
			target.setAzioneIntrapresa((CodeValuePhi)getMappedCode(source.getAzioneIntrapresa(), ulss));
			
			if(source.getPercentualeinvalidita()!=null){
				try {
					target.setDisability(Integer.parseInt(source.getPercentualeinvalidita()));
				} catch (NumberFormatException e) {
					thislog.warn("Unable to parse percentuale invalidità - source infortuni id: "+source.getIdinfortuni());
				}
			}
			
			String diagnosiPs = (source.getDiagnosips()!=null?source.getDiagnosips().trim():"");
			int diagnosiLength = diagnosiPs.length();
			diagnosiPs = diagnosiPs.substring(0,diagnosiLength<=2500?diagnosiLength:2500);
			target.setDiagnosips(diagnosiPs);
			
			target.setMansione(getMappedCode(source.getMansioneInf(), ulss));			
			target.setTipoContratto(getMappedCode(source.getRapportoLav(), ulss));			
			target.setNotificationData(source.getDatasegnalazione());
			target.setQualifica(getMappedCode(source.getQualificaLav(), ulss));
			target.setDataAssunzione(source.getDataassunzione());
			
			if(source.getAgenteMateriale()!=null){
				String code = source.getAgenteMateriale().getCodice();
				String oid = "phidic.spisal.segnalazioni.workaccident.agentimateriali";
				int i=0;
				for(char c : code.toCharArray()){
					if(i==0){
						oid+=".";
					}
					oid+=c;
					i++;
					if(i==2){
						i=0;
					}
				}
				target.setAgenteMateriale((CodeValuePhi)getCodeValue(oid));
			}
					
			target.setDeceasedTime(source.getDataoradecesso());
			
			if(target.getDeceasedPlace()==null)
				target.setDeceasedPlace(new AD());
			
			manageAddrComune(target.getDeceasedPlace(), source.getLuogoDecesso());
			
			if("1".equals(source.getChkprognosiriservata())){
				target.setPrgr(true);
			}
			if(source.getDescrevento()!=null){
				String dinamica = source.getDescrevento();
				int length = dinamica.length();
				dinamica = dinamica.substring(0,length<=4000?length:4000);
				target.setDinamica(dinamica);
			}

			
			target.setForma((CodeValuePhi)getMappedCode(source.getForma(), ulss));
			if(source.getAgenteMateriale()!=null){
				String code = source.getAgenteMateriale().getCodice();
				CodeValuePhi cv = getCodeValueCode("phidic.spisal.segnalazioni.workaccident.agentimateriali", code);
				if(cv!=null){
					target.setAgenteMateriale(cv);
				}
			}
			
			target.setCondizioniDiRischio((CodeValuePhi)getMappedCode(source.getCondRischio(), ulss));
			target.setComportamento((CodeValuePhi)getMappedCode(source.getComportamento(), ulss));
			
			
			
			List<CodeValuePhi> compSpec = new ArrayList<CodeValuePhi>();
			
			if("1".equals(source.getChkinfocarente()))
				compSpec.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.compspec.01"));
			
			if("1".equals(source.getChkinsufvigil()))
				compSpec.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.compspec.02"));
			
			if("1".equals(source.getChkcolpalav()))
				compSpec.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.compspec.03"));
			
			if("1".equals(source.getChkresponabterzi()))
				compSpec.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.compspec.04"));
			
			target.setCompSpec(compSpec);
			
			
			List<CodeValuePhi> evitabilita = new ArrayList<CodeValuePhi>();
			
			if("1".equals(source.getChkmisuretecn()))
				evitabilita.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.evitabilita.01"));
			
			if("1".equals(source.getChkmisureprocedurali()))
				evitabilita.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.evitabilita.02"));
			
			if("1".equals(source.getChkistruzformaz()))
				evitabilita.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.evitabilita.03"));
			
			if("1".equals(source.getChkvigilanza()))
				evitabilita.add(getCodeValue("phidic.spisal.segnalazioni.workaccident.evitabilita.04"));
			
			target.setEvitabilita(evitabilita);
									
			String notePratica = pratica.getNote();
			if(source.getNote()!=null){
				notePratica+=("\r\n"+source.getNote());
				pratica.setNote(notePratica);
				
				saveOnTarget(pratica);
			}
			
			if("1".equals(source.getChkinchiesta())){
				target.setInchiesta(true);
			
				InchiestaInfortunio in = new InchiestaInfortunio();
				in.setCreatedBy(this.getClass().getSimpleName()+ulss);
				in.setCreationDate(new Date());
				in.setProcpratiche(pratica);
				
				if(pratica.getInchiestaInfortunio()==null)
					pratica.setInchiestaInfortunio(new ArrayList<InchiestaInfortunio>());
				
				pratica.getInchiestaInfortunio().add(in);
				
				in.setDataInizio(source.getDatainizioinchiesta());
				in.setDataFine(source.getDatafineinchiesta());
				in.setDataAffidamento(source.getDataaffidamentoinc());
				in.setDataSupplemento(source.getDatasupplementoinc());
				in.setNumeroFascicolo(source.getNumfascicolo());
				in.setEsitoInchiesta(getMappedCode(source.getEsitoInchiesta(), ulss));
				if(source.getEsitoInchiesta()!=null){
					//MAPPING COMUNICATO DA POMA  L'1/3					
					pratica.setEsitoPratica((CodeValuePhi) getMappedCode(source.getEsitoInchiesta(), "a", ulss));
				}
				
				if(source.getProcura()!=null){
					in.setProcura(getCodeValue("phidic.spisal.procure."+source.getProcura().getCodice()));
				}
				
				if(source.getTipoinchiesta()!=null){
					in.setTipoIntervento(getCodeValue("phidic.spisal.segnalazioni.workaccident.tipoint."+source.getTipoinchiesta()));
				}
				
				if(source.getInfortunirelazioni()!=null){
					Infortunirelazioni rel = source.getInfortunirelazioni();
					
					if(rel.getElencoallegati()!=null){
						String allegati = rel.getElencoallegati();
						int length = allegati.length();
						allegati = allegati.substring(0,length<=2500?length:2500);
						in.setAllegati(allegati);
					}
					
					if("1".equals(rel.getOptviolazioni()))
						in.setReati(true);
					
					if(rel.getInfomotivoviolazioni()!=null){
						String noteInfo = rel.getInfomotivoviolazioni();
						int length = noteInfo.length();
						noteInfo = noteInfo.substring(0,length<=2500?length:2500);
						in.setNote(noteInfo);
					}
					
					if(rel.getDescrizioneluoghi()!=null){
						String descLuoghi = rel.getDescrizioneluoghi();
						int length = descLuoghi.length();
						descLuoghi = descLuoghi.substring(0,length<=2500?length:2500);
						in.setDescrizioneLuoghi(descLuoghi);
					}
					
					if(rel.getAttivitauo()!=null){
						String attivitaPg = rel.getAttivitauo();
						int length = attivitaPg.length();
						attivitaPg = attivitaPg.substring(0,length<=2500?length:2500);
						in.setAttivitaPg(attivitaPg);
					}
					
					if(rel.getDescrizioneevento()!=null){
						String descEvento = rel.getDescrizioneevento();
						int length = descEvento.length();
						descEvento = descEvento.substring(0,length<=2500?length:2500);
						in.setDescrizioneEvento(descEvento);
						
						String tempDinamica = target.getDinamica() == null ? "" : target.getDinamica();
						tempDinamica += "\r\n" + rel.getDescrizioneevento();
						int tempLength = tempDinamica.length();
						tempDinamica = tempDinamica.substring(0,tempLength<=4000?tempLength:4000);
						
						target.setDinamica(tempDinamica);
					}
					
					in.setConsiderazioni(rel.getConsiderazioni());
					
					if(rel.getConsiderazioni()!=null){
						String considerazioni = rel.getConsiderazioni();
						int length = considerazioni.length();
						considerazioni = considerazioni.substring(0,length<=2500?length:2500);
						in.setConsiderazioni(considerazioni);
						
						pratica.setConclusioni(considerazioni);
					}
					
					if(rel.getConclusioni()!=null){
						String conclusioni = rel.getConclusioni();
						int length = conclusioni.length();
						conclusioni = conclusioni.substring(0,length<=2500?length:2500);
						in.setConclusioni(conclusioni);
						
						String tempConclusioni = pratica.getConclusioni() == null ? "" : pratica.getConclusioni();
						tempConclusioni += "\r\n" + rel.getConclusioni();
						int tempLength = tempConclusioni.length();
						tempConclusioni = tempConclusioni.substring(0,tempLength<=2500?tempLength:2500);

						pratica.setConclusioni(tempConclusioni);
					}				
				}
				
				saveOnTarget(in);
			}
				
			addToPrevnetNotes(pratica.getPrevnetNotes(), source);
			saveOnTarget(target);
			saveMapping(source, target);
			
			
			
		}
		
		return getMapped(source.getIdinfortuni());
		
	}	
	
	private void manageAteco(Infortuni inf) {
		if(inf==null)
			return;
		
		if(inf.getSedi()==null && inf.getPersoneGiuridiche()==null)
			return;
		
		Long sediId = -1L;
		Long dittaId = -1L;
		
		if(inf.getSedi()!=null)
			sediId = inf.getSedi().getInternalId();
		
		if(inf.getPersoneGiuridiche()!=null)
			dittaId = inf.getPersoneGiuridiche().getInternalId();
		
		queryAtecoP.setParameter("sediId", sediId);
		
		List<CodeValueAteco> list = queryAtecoP.getResultList();
		if(list!=null && !list.isEmpty()) {
			CodeValueAteco ateco = list.get(0);
			inf.setComparto(ateco);
		}else {
			log.warn("Unable to find CodeValueAteco for dittaId: "+dittaId+" sediId:"+sediId);
		}
		
		return;
	}
	
	private void importInfortuniExt(InfortuniPrevnet source, Infortuni target){
		InfortuniExt infExt = new InfortuniExt();
		infExt.setCreatedBy(this.getClass().getSimpleName()+ulss);
		infExt.setCreationDate(new Date());
		//NOTE
		String note = "";
		if(source.getRepartoarealuogoinf()!=null){
			note+=("REPARTO/AREA: "+source.getRepartoarealuogoinf()+"\r\n");
		}
		if(source.getAttSvoltaDuranteInf()!=null){
			note+=("ATTIVITA' SVOLTA: "+source.getAttSvoltaDuranteInf().getDescrizione()+"\r\n");
		}
		if(source.getDescrinf()!=null){
			note+=("DESCRIZIONE INFORTUNIO: "+source.getDescrinf()+"\r\n");
		}
		if(source.getNoteinchiesta()!=null){
			note+=("NOTE INCHIESTA: "+source.getNoteinchiesta()+"\r\n");
		}
		
		int noteLength = note.length();
		note = note.substring(0,noteLength<=4000?noteLength:4000);
		infExt.setNote(note);
		
		if(source.getIndirizzoinf()!=null || source.getLuogoInf()!=null || source.getEsitoitem()!=null){				
			
			if(infExt.getAddr()==null)
				infExt.setAddr(new AD());
			
			AD addr = infExt.getAddr();
			
			if(source.getIndirizzoinf()!=null){
				addr.setStr(source.getIndirizzoinf());
			}
			if(source.getLuogoInf()!=null){
				manageAddrComune(addr, source.getLuogoInf());
			}
			if("1".equals(source.getEsitoitem().toPlainString())){
				infExt.setGravitaFinale((CodeValuePhi)getCodeValue("phidic.spisal.segnalazioni.workaccident.esitoitem.01"));//DECESSO
			}
			

		}
		saveOnTarget(infExt);
		target.setInfortuniExt(infExt);
	}
	
	private void managePrognosi(InfortuniPrevnet source, Infortuni target){
		if(source==null || target==null)
			return;
		
		if(source.getPrognosis()!=null && !source.getPrognosis().isEmpty()){
			List<Prognosi> pList = source.getPrognosis();
			int pLength = pList.size();
			int totale = 0;
			if(pLength>0 && pList.get(0).getGiorni()!=null){
				target.setGgPrognosi1(pList.get(0).getGiorni().intValue());
				
			}
			
			//modifica del 25/10/17 @Dolo
			for(Prognosi p : source.getPrognosis()) {
				if(p.getGiorni()!=null) {
					totale+=p.getGiorni().intValue();
				}
			}
			target.setGgPrognosi2(totale);
			
			
		}else if(source.getPrognosiiniziale()!=null){
			target.setGgPrognosi1(source.getPrognosiiniziale().intValue());
			if(source.getPrognositot()!=null){
				target.setGgPrognosi2(source.getPrognositot().intValue()-target.getGgPrognosi1());
				target.setGgPrognosiTot(source.getPrognositot().intValue());
			}
		}
		
	}
	
	private void manageLuogoInfortunio(InfortuniPrevnet source, Infortuni target){
		if(source==null || target==null)
			return;
		
		PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
		CantieriImporter cantieriImp = CantieriImporter.getInstance();
		
		
		if("1".equals(source.getTipoluogoinf())){
			//AZIENDA PROPRIA
			target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.owncompany"));
			if(source.getDittaLuogoInf()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDittaLuogoInf());
				if(sede!=null){
					target.setSediExt(sede);
					target.setPersoneGiuridicheExt(sede.getPersonaGiuridica());
				}
			}else if(source.getAnagrLuogoInf()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getAnagrLuogoInf());
				if(sede!=null){
					target.setSedi(sede);
					target.setPersoneGiuridiche(sede.getPersonaGiuridica());
				}
			}
		}else if("2".equals(source.getTipoluogoinf())){
			//ALTRA AZIENDA
			target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.company"));
			if(source.getDittaLuogoInf()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDittaLuogoInf());
				if(sede!=null){
					target.setSediExt(sede);
					target.setPersoneGiuridicheExt(sede.getPersonaGiuridica());
				}
			}else if(source.getAnagrLuogoInf()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getAnagrLuogoInf());
				if(sede!=null){
					target.setSediExt(sede);
					target.setPersoneGiuridicheExt(sede.getPersonaGiuridica());
				}
			}
		}else if("3".equals(source.getTipoluogoinf())){
			//CANTIERE
			target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.yard"));
			if(source.getAnagrLuogoInf()!=null){
				Cantiere cant = cantieriImp.importCantiere(source.getAnagrLuogoInf());
				target.setCantiere(cant);
			}
			
		}else if("4".equals(source.getTipoluogoinf())){
			//STRADA
			target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.street"));
			if(source.getStradaaltro()!=null){
				String strdesc = source.getStradaaltro();
				int length = strdesc.length();
				strdesc = strdesc.substring(0,length<=2500?length:2500);
				if(target.getInfortuniExt()!=null)
					target.getInfortuniExt().setStreetDescription(strdesc);
			}
			
		}else if("5".equals(source.getTipoluogoinf())){
			//ALTRO LUOGO
			target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.other"));
			if(source.getStradaaltro()!=null){
				String strdesc = source.getStradaaltro();
				int length = strdesc.length();
				strdesc = strdesc.substring(0,length<=255?length:255);
				target.setOtherDescription(strdesc);
			}
			
		}else{
			//SE NON CONOSCO IL TIPO LUOGO DEVO INDOVINARE..
			if(source.getAnagrLuogoInf()!=null){
				Anagrafiche a = source.getAnagrLuogoInf();
				if("0".equals(a.getTipo())){
					//ALTRA AZIENDA
					target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.company"));
					Sedi sede = ditteImp.importDittaCompleta(a);
					if(sede!=null){
						target.setSediExt(sede);
						target.setPersoneGiuridicheExt(sede.getPersonaGiuridica());
					}
				}else if("3".equals(a.getTipo())){
					//CANTIERE
					target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.yard"));
					Cantiere cant = cantieriImp.importCantiere(a);
					target.setCantiere(cant);
				}
			}
			
			if(source.getStradaaltro()!=null){
				//ALTRO LUOGO
				target.setPlace(getCodeValue("phidic.spisal.segnalazioni.place.other"));
				if(source.getStradaaltro()!=null){
					String strdesc = source.getStradaaltro();
					int length = strdesc.length();
					strdesc = strdesc.substring(0,length<=255?length:255);
					target.setOtherDescription(strdesc);
				}
			}				
		}
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapInfortuni m = sourceEm.find(MapInfortuni.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapInfortuni m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapInfortuni> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapInfortuni map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Infortuni getMapped(long id){
		MapInfortuni map = sourceEm.find(MapInfortuni.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapInfortuni m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapInfortuni> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Infortuni c = targetEm.find(Infortuni.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qInfortuni = targetEm.createQuery("SELECT m FROM Infortuni m WHERE m.internalId = :id");
			qInfortuni.setParameter("id", map.getIdphi());
			List<Infortuni> lp = qInfortuni.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}

		return null;
	}
	
	private void saveMapping(InfortuniPrevnet source, Infortuni target){
		MapInfortuni map = new MapInfortuni();
		map.setIdprevnet(source.getIdinfortuni());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);
		
		saveOnSource(map);
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}

	@Override
	protected void deleteImportedData(String ulss) {
		String hqlInfortuni = "SELECT mf.idphi FROM MapInfortuni mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlInfortuni+="WHERE mf.ulss = :ulss";
		
		Query qInfortuni = sourceEm.createQuery(hqlInfortuni);
		if(ulss!=null && !ulss.isEmpty())
			qInfortuni.setParameter("ulss", ulss);
		
		List<Long> allIdInf = qInfortuni.getResultList();
		List<Long> idInf = new ArrayList<Long>();
		while(allIdInf!=null && !allIdInf.isEmpty()){
			if(allIdInf.size()>1000){
				idInf.clear();
				idInf.addAll(allIdInf.subList(0, 1000));
				allIdInf.removeAll(idInf);
			}else{
				idInf.clear();
				idInf.addAll(allIdInf);
				allIdInf.removeAll(idInf);
			}
			if(commit){		
				String deleteCompSpec = "DELETE FROM comp_spec WHERE infortuni_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelCompSpec = targetEm.createNativeQuery(deleteCompSpec);
				qDelCompSpec.setParameter("ids", idInf);
				qDelCompSpec.executeUpdate();
				targetEm.getTransaction().commit();
				
				String deleteEvitabilita = "DELETE FROM evitabilita WHERE infortuni_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelEvitabilita = targetEm.createNativeQuery(deleteEvitabilita);
				qDelEvitabilita.setParameter("ids", idInf);
				qDelEvitabilita.executeUpdate();
				targetEm.getTransaction().commit();
				
				String deleteInf = "DELETE FROM Infortuni e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelInf = targetEm.createQuery(deleteInf);
				qDelInf.setParameter("ids", idInf);
				qDelInf.executeUpdate();
				targetEm.getTransaction().commit();
				
				String findInfExt = "SELECT ext.internalId FROM Infortuni i JOIN i.infortuniExt ext WHERE i.internalId IN (:ids)";
				Query qExt = targetEm.createQuery(findInfExt);
				qExt.setParameter("ids", idInf);
				List<Long> idExt = qExt.getResultList();
				
				if(idExt!=null && !idExt.isEmpty()){
					String deleteInfExt = "DELETE FROM infortuni_ext WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelInfExt = targetEm.createNativeQuery(deleteInfExt);
					qDelInfExt.setParameter("ids", idExt);
					qDelInfExt.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_INFORTUNI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, InfortuniPrevnet inf){
		if(inf==null || prevNotes==null)
			return;
		
		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI INFORTUNIO\r\n");
		
		if("1".equals(inf.getChkarchivsede()))
			constructedNote.append("Archiviazione in Sede: SI\r\n");
		else
			constructedNote.append("Archiviazione in Sede: NO\r\n");
		
		if("1".equals(inf.getChkdecessolavoro()))
			constructedNote.append("Decesso avvenuto sul luogo di lavoro: SI\r\n");
		
		if("1".equals(inf.getChkdecessoospedale()))
			constructedNote.append("Decesso avvenuto in ospedale: SI\r\n");
		
		if("1".equals(inf.getChkinoltropm()))
			constructedNote.append("Inoltro Relazione al PM: SI\r\n");
		else
			constructedNote.append("Inoltro Relazione al PM: NON SPECIFICATO\r\n");
		
		if("1".equals(inf.getChklesionicolpose()))
			constructedNote.append("Infortunio per lesioni colpose art 589-590 CP: SI\r\n");
		else
			constructedNote.append("Infortunio per lesioni colpose art 589-590 CP: NO\r\n");
		
		if("1".equals(inf.getChkricovero()))
			constructedNote.append("Ricovero: SI\r\n");
		else
			constructedNote.append("Ricovero: NO\r\n");
		
		if("1".equals(inf.getChkverbale758()))
			constructedNote.append("Verbale 758 redatto (per infortunio): SI\r\n");
		else
			constructedNote.append("Verbale 758 redatto (per infortunio): NO\r\n");

		if(inf.getAzioneIntrapresa()!=null) {
			constructedNote.append("Azione intrapresa: "+inf.getAzioneIntrapresa().getDescrizione()+"\r\n");
		}
		
		if(inf.getNoteinchiesta()!=null && !"".equals(inf.getNoteinchiesta())) {
			constructedNote.append("Note: "+inf.getNoteinchiesta()+"\r\n");
		}

		if("1".equals(inf.getTipoinchiesta())) {
			constructedNote.append("Tipo inchiesta: "+"Semplice"+"\r\n");
		}else if("2".equals(inf.getTipoinchiesta())) {
			constructedNote.append("Tipo inchiesta: "+"Complessa"+"\r\n");
		}else if("3".equals(inf.getTipoinchiesta())) {
			constructedNote.append("Tipo inchiesta: "+"Semplice tramite intervista"+"\r\n");
		}
		
		if(inf.getEsitoInchiesta()!=null){
			constructedNote.append("Esito inchiesta: "+inf.getEsitoInchiesta().getDescrizione()+"\r\n");
		}
		
		if(inf.getDatainizioinchiesta()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Data inizio inchiesta: "+sdf.format(inf.getDatainizioinchiesta())+"\r\n");
		}
		
		if(inf.getDatafineinchiesta()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Data fine inchiesta: "+sdf.format(inf.getDatafineinchiesta())+"\r\n");
		}
		
		if(inf.getDataesitoinc()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Data esito inchiesta: "+sdf.format(inf.getDataesitoinc())+"\r\n");
		}
		
		if("1".equals(inf.getUlterioriinfortunati())) {
			constructedNote.append("Per questa pratica sono stati registrati più infortunati.\r\n");
		}

		if("1".equals(inf.getChkinchiesta()) && inf.getInfortunirelazioni()!=null){
			Infortunirelazioni rel = inf.getInfortunirelazioni();
			
			if(rel.getElencoallegati()!=null && !"".equals(rel.getElencoallegati())) {
				constructedNote.append("Allegati: "+rel.getElencoallegati().replaceAll("\r\n", "; ")+"\r\n");
			}
			
			if(rel.getDescrizioneluoghi()!=null && !"".equals(rel.getDescrizioneluoghi())) {
				constructedNote.append("Descrizione luoghi: "+rel.getDescrizioneluoghi()+"\r\n");
			}
			
			if(rel.getAttivitauo()!=null && !"".equals(rel.getAttivitauo())) {
				constructedNote.append("Descrizione dell'U.O.: "+rel.getAttivitauo()+"\r\n");
			}
		}
		
		/* I0072968 rimesso a posto */
		if(inf.getSegnalazione()!=null){
			constructedNote.append("Origine/Fonte dell'informazione: "+inf.getSegnalazione().getDescrizione()+"\r\n");
		}
		
		
		if(inf.getMotivoArchiv()!=null){
			constructedNote.append("Motivo archiviazione: "+inf.getMotivoArchiv().getDescrizione()+"\r\n");
		}
		
		if(inf.getOraordinale()!=null){
			constructedNote.append("Ora ordinale dell'orario di lavoro in cui è avvenuto l'infortunio: "+inf.getOraordinale().toPlainString()+"\r\n");
		}
		
		if(inf.getDisposizioni()!=null){
			constructedNote.append("Disposizioni: "+inf.getDisposizioni()+"\r\n");
		}
		
		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";
		
		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}
}
