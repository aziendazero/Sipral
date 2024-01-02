package com.phi.db.importer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.DitteMalattie;
import com.phi.entities.baseEntity.FattoreRischio;
import com.phi.entities.baseEntity.InchiestaInfortunio;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.PrevnetNotes;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.prevnet.entities.Inchiestemalatpro;
import com.prevnet.entities.Malattieprofessionali;
import com.prevnet.mappings.MapInfortuni;
import com.prevnet.mappings.MapMalprof;

@SuppressWarnings({"unchecked"})
public class MalProfImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(MalProfImporter.class.getName());
	private Query qRisk;
	
	private static MalProfImporter instance = null;
	
	public static MalProfImporter getInstance() {
		if(instance == null) {
			instance = new MalProfImporter();
		}
		return instance;
	}
	
	public MalProfImporter() {
		super();		
		qRisk = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.id LIKE 'phidic.spisal.frisk.agmal.%' AND cv.code = :code AND cv.type = 'S' ");
	}
	
	public MalattiaProfessionale importMalProf(Malattieprofessionali source, Procpratiche pratica){
		int length=0;
		if(!checkMapping(source.getIdmalattieprofessionali())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			MediciImporter medImp = MediciImporter.getInstance();
			
			MalattiaProfessionale target = new MalattiaProfessionale();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			pratica.setMalattiaProfessionale(target);
			
			if(source.getLavoratore()!=null){
				Person lav = persImp.importPerson(source.getLavoratore());
				if(lav!=null){
					target.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
					target.setRiferimentoUtente(lav);
				}
			}
			
			if(source.getSchedaDitta()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getSchedaDitta());
				if(sede!=null){
					target.setAttualeSede(sede);
					target.setAttualeDitta(sede.getPersonaGiuridica());
				}
			}
			
			if(source.getMedici()!=null){
				Physician med = medImp.importMedicoSpisal(source.getMedici(), medImp.findOrganization(ulss));
				if(med!=null){
					target.setCertMed(med);
				}
			}
			
			if(source.getCodiceIcd()!=null){
				CodeValue icd9 = getMappedCode(source.getCodiceIcd(), ulss);
				if(icd9 instanceof CodeValueIcd9)
					target.setDiagCode((CodeValueIcd9)icd9);
			}
			
			if(source.getMalattiaContratta()!=null){
				CodeValue mpInail = getMappedCode(source.getMalattiaContratta(), ulss);
				if(mpInail instanceof CodeValuePhi)
					target.setMpInail((CodeValuePhi)mpInail);
			}
			manageAteco(target);
			target.setData(source.getDatacomunicazione());
			target.setCertDate(source.getDatacertificato());
			
			if("1".equals(source.getRiconoscimentoinail())){
				target.setRicInail(getCodeValue("phidic.spisal.segnalazioni.workdisease.ricinail.1"));
			}else if("2".equals(source.getRiconoscimentoinail())){
				target.setRicInail(getCodeValue("phidic.spisal.segnalazioni.workdisease.ricinail.2"));
			}else if("4".equals(source.getRiconoscimentoinail())){
				target.setRicInail(getCodeValue("phidic.spisal.segnalazioni.workdisease.ricinail.3"));
			}else if("5".equals(source.getRiconoscimentoinail())){
				target.setRicInail(getCodeValue("phidic.spisal.segnalazioni.workdisease.ricinail.4"));
			}
			
			target.setRicInailData(source.getDatariconoscimentoinail());
			
			target.setDiagText(source.getDiagnosi());
			
			if(source.getEnte()!=null){
				if(source.getEnte().getTipo()!=null && source.getEnte().getTipo().intValue() == 0){
					
					Sedi sede = ditteImp.importDittaCompleta(source.getEnte());
					if(sede!=null){
						target.setRichiedenteSede(sede);
						target.setRichiedenteDitta(sede.getPersonaGiuridica());
						target.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
					}

					
				}else if(source.getEnte().getTipo()!=null && source.getEnte().getTipo().intValue() == 4){
					Person ric = persImp.importPerson(source.getEnte());
					if(ric!=null){
						target.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
						target.setRichiedenteUtente(ric);
					}
				}
			}
			
			target.setMansioneAttribuita((CodeValuePhi)getMappedCode(source.getMansione(), ulss));
			target.setGravita((CodeValuePhi)getMappedCode(source.getGravita(), ulss));
			target.setCondProf((CodeValuePhi)getMappedCode(source.getCondProf(), ulss));
			
			CodeValue yesno = getMappedCode(source.getQualDiagnosi(), ulss);
			//certezza diagnosi:[A]	AFFIDABILE
			if(yesno!=null && "YES".equals(yesno.getCode())){
				target.setCertezzaDiag(true);
			
			//certezza diagnosi:[B]	DUBBIA
			}else if(yesno!=null && "NO".equals(yesno.getCode())){
				target.setCertezzaDiag(false);
			}
			
			String esame = "";
			if(source.getEsamiobiettivi()!=null){
				
				if(source.getEsamiobiettivi().getAltezza()!=null){
					esame+="ALTEZZA: "+source.getEsamiobiettivi().getAltezza().toPlainString()+"\r\n";
				}
				if(source.getEsamiobiettivi().getPeso()!=null){
					esame+="PESO: "+source.getEsamiobiettivi().getPeso().toPlainString()+"\r\n";
				}
				if(source.getEsamiobiettivi().getEta()!=null){
					esame+="ETA: "+source.getEsamiobiettivi().getEta().toPlainString()+"\r\n";
				}
			}
			if(source.getAltrifattori()!=null){
				esame+="ALTRI FATTORI: "+source.getAltrifattori();
			}
			
			target.setAnamnesi(esame);
			target.setDeathDate(source.getDatadecesso());
			
			if("1".equals(source.getLuogodecesso())){
				target.setDeathPlace(getCodeValue("phidic.spisal.segnalazioni.workdisease.lm.1"));//ospedale
			}else if("2".equals(source.getLuogodecesso())){
				target.setDeathPlace(getCodeValue("phidic.spisal.segnalazioni.workdisease.lm.2"));//luogo di lavoro
			}else if(target.getDeathDate()!=null){
				target.setDeathPlace(getCodeValue("phidic.spisal.segnalazioni.workdisease.lm.3"));//altro
			}
			if(source.getComuneDecesso()!=null){
				target.setDeathText("Comune del decesso del lavoratore: "+source.getComuneDecesso().getDescrizione());
			}
			addToPrevnetNotes(pratica.getPrevnetNotes(), source);
			
			saveOnTarget(target);
			
			if(source.getDittaPrincRespons()!=null){
				Sedi sede = ditteImp.importDittaCompleta(source.getDittaPrincRespons());
				if(sede!=null){
					DitteMalattie dm = new DitteMalattie();
					dm.setCreationDate(new Date());
					dm.setCreatedBy(this.getClass().getSimpleName()+ulss);
					dm.setMalattiaProfessionale(target);
					dm.setSedi(sede);
					dm.setPersoneGiuridiche(sede.getPersonaGiuridica());
					dm.setPrincipale(true);
					saveOnTarget(dm);
				}
			}
			
			if(source.getFattoririschio()!=null){
				String code = source.getFattoririschio().getCodice();
				if(code!=null && code.endsWith("."))
					code = code.substring(0, code.length()-1);
				
				qRisk.setParameter("code", code);
				List<CodeValuePhi> list = qRisk.getResultList();
				if(list!=null && !list.isEmpty()){
					FattoreRischio risk = new FattoreRischio();
					risk.setCreatedBy(this.getClass().getSimpleName()+ulss);
					risk.setCreationDate(new Date());
					risk.setMalattiaProfessionale(target);
					risk.setCode(list.get(0));
					risk.setCausa((CodeValuePhi)getMappedCode(source.getNessoGlobale(), ulss));
					
					saveOnTarget(risk);
				}
			}
			
			saveMapping(source, target);
			
			if("1".equals(source.getInchiestaindagine())){
				target.setInchiesta(true);
			
				InchiestaInfortunio in = new InchiestaInfortunio();
				in.setCreatedBy(this.getClass().getSimpleName()+ulss);
				in.setCreationDate(new Date());
				in.setProcpratiche(pratica);
							
				in.setDataInizio(source.getDatainizioindagine());
				in.setDataFine(source.getDatafineindagine());

				if(source.getInchiestemalatpros()!=null && !source.getInchiestemalatpros().isEmpty()){
					//dovrebbe essercene sempre e solo 1...
					Inchiestemalatpro im = source.getInchiestemalatpros().get(0);
					
					in.setDataAffidamento(im.getDataaffidamentoinc());
					if(im.getProcura()!=null){
						in.setProcura(getCodeValue("phidic.spisal.procure."+im.getProcura().getCodice()));
					}
					
					in.setNumeroFascicolo(im.getNumfascicolo());
					in.setEsitoInchiesta(getMappedCode(im.getEsitoInchiesta(), ulss));
					in.setDataFine(im.getDataesitoinc());
					
					if("1".equals(im.getViolazionisino()))
						in.setReati(true);
					
					String considerazioni = im.getConsiderazioni();
					if(considerazioni!=null){
						length = considerazioni.length();
						considerazioni = considerazioni.substring(0,length<=2500?length:2500);
						in.setConsiderazioni(considerazioni);
					}

					String conclusioni = im.getConclusioni();
					if(conclusioni!=null){
						length = conclusioni.length();
						conclusioni = conclusioni.substring(0,length<=2500?length:2500);
						in.setConclusioni(conclusioni);
					}
					if("1".equals(im.getTipoinchiesta())){
						//semplice
					}else if("2".equals(im.getTipoinchiesta())){
						//complessa
					}
					
					
					
					String note = "";
					if(im.getNoteinchiesta()!=null){
						note+=("NOTE: "+im.getNoteinchiesta()+"\r\n");
					}
					if(im.getStorialavorativa()!=null){
						note+=("STORIA LAVORATIVA: "+im.getNoteinchiesta()+"\r\n");
					}
					if(im.getStoriaclinica()!=null){
						note+=("STORIA CLINICA: "+im.getNoteinchiesta()+"\r\n");
					}
					if("1".equals(im.getChknessocasualita())){
						note+=("VIOLAZIONE CORRELATA ALL'EVENTO: SI\r\n");
					}else{
						note+=("VIOLAZIONE CORRELATA ALL'EVENTO: NO\r\n");
					}
					if("1".equals(im.getChklesionicolpose())){
						note+=("di cui per lesioni colpose art 589-590 CP");
					}

					length = note.length();
					note = note.substring(0,length<=2500?length:2500);
					in.setNote(note);

					addToPrevnetNotes(pratica.getPrevnetNotes(), im);
				}
				
				saveOnTarget(in);
			}
		}
		
		return getMapped(source.getIdmalattieprofessionali());
		
	}	
	
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapMalprof m = sourceEm.find(MapMalprof.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapMalprof m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapMalprof> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapMalprof map = list.get(0);
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
	private MalattiaProfessionale getMapped(long id){
		MapMalprof map = sourceEm.find(MapMalprof.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapMalprof m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapMalprof> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			MalattiaProfessionale c = targetEm.find(MalattiaProfessionale.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qMalprof = targetEm.createQuery("SELECT m FROM MalattiaProfessionale m WHERE m.internalId = :id");
			qMalprof.setParameter("id", map.getIdphi());
			List<MalattiaProfessionale> lp = qMalprof.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}
	
	private void saveMapping(Malattieprofessionali source, MalattiaProfessionale target){
		MapMalprof map = new MapMalprof();
		map.setIdprevnet(source.getIdmalattieprofessionali());
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
		String hqlPratiche = "SELECT mf.idphi FROM MapMalprof mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPratiche+="WHERE mf.ulss = :ulss";
		
		Query qMalprof = sourceEm.createQuery(hqlPratiche);
		if(ulss!=null && !ulss.isEmpty())
			qMalprof.setParameter("ulss", ulss);
		
		List<Long> allIdMal = qMalprof.getResultList();
		List<Long> idMal = new ArrayList<Long>();
		while(allIdMal!=null && !allIdMal.isEmpty()){
			if(allIdMal.size()>1000){
				idMal.clear();
				idMal.addAll(allIdMal.subList(0, 1000));
				allIdMal.removeAll(idMal);
			}else{
				idMal.clear();
				idMal.addAll(allIdMal);
				allIdMal.removeAll(idMal);
			}
			if(commit){
				//SGANCIO I FATTORI DI RISCHIO
				String updatePrat = "DELETE FROM fattori_rischio WHERE MALATTIA_PROFESSIONALE_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdPrat = targetEm.createNativeQuery(updatePrat);
				qUpdPrat.setParameter("ids", idMal);
				qUpdPrat.executeUpdate();
				targetEm.getTransaction().commit();
				
				String updateDm = "DELETE FROM ditte_malattie WHERE MALATTIA_PROFESSIONALE_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdDm = targetEm.createNativeQuery(updateDm);
				qUpdDm.setParameter("ids", idMal);
				qUpdDm.executeUpdate();
				targetEm.getTransaction().commit();
				
				String deleteMal = "DELETE FROM MalattiaProfessionale e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelMal = targetEm.createQuery(deleteMal);
				qDelMal.setParameter("ids", idMal);
				qDelMal.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_MALPROF WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Inchiestemalatpro im){
		if(im==null || prevNotes==null)
			return;
		
		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI INCHIESTA MALATTIA PROFESSIONALE\r\n");
		if("1".equals(im.getChkarchivsede())){
			constructedNote.append("Archiviazione in Sede: SI\r\n");
		}else{
			constructedNote.append("Archiviazione in Sede: NO\r\n");
		}
		if(im.getMotivoArchiv()!=null){
			constructedNote.append("Motivo Archiviazione: "+im.getMotivoArchiv().getDescrizione()+"\r\n");
		}
		if("1".equals(im.getChkinoltropm())){
			constructedNote.append("Inoltro Relazione al PM: SI\r\n");
		}else{
			constructedNote.append("Inoltro Relazione al PM: NO\r\n");
		}
		if("1".equals(im.getChkverbale758())){
			constructedNote.append("Verbale 758 redatto (per infortunio): SI\r\n");
		}else{
			constructedNote.append("Verbale 758 redatto (per infortunio): NO\r\n");
		}
		if("1".equals(im.getTipoinchiesta())){
			constructedNote.append("Tipo inchiesta: SEMPLICE\r\n");
		}else if("2".equals(im.getTipoinchiesta())){
			constructedNote.append("Tipo inchiesta: COMPLESSA\r\n");
		}

		if("050120".equals(ulss) || "050121".equals(ulss) || "050122".equals(ulss)){
			if ("1".equals(im.getViolazionisino())) {
				constructedNote.append("Emergono o non emergono violazioni: NO\r\n");
			} else {
				constructedNote.append("Emergono o non emergono violazioni: SI\r\n");
			}
		}else {
			if ("1".equals(im.getViolazionisino())) {
				constructedNote.append("Emergono o non emergono violazioni: SI\r\n");
			} else {
				constructedNote.append("Emergono o non emergono violazioni: NO\r\n");
			}
		}

		if(im.getEsitoInchiesta()!=null) {
			constructedNote.append("Descrizione esito: "+im.getEsitoInchiesta().getDescrizione()+"\r\n");
		}
		
		if(im.getDataesitoinc()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Data esito: "+sdf.format(im.getDataesitoinc())+"\r\n");
		}
		
		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";
		
		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}
	
	private void addToPrevnetNotes(PrevnetNotes prevNotes, Malattieprofessionali mal){
		if(mal==null || prevNotes==null)
			return;
		
		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI MALATTIA PROFESSIONALE\r\n");
		
		if(mal.getAsl()!=null){
			constructedNote.append("ASL Competente: "+mal.getAsl().getDescrizione()+"\r\n");
		}
		if(mal.getComuneComunicazione()!=null){
			constructedNote.append("Comune comunicazione: "+mal.getComuneComunicazione().getDescrizione()+"\r\n");
		}
		if(mal.getCondProf()!=null){
			constructedNote.append("Condizione professionale del lavoratore: "+mal.getCondProf().getDescrizione()+"\r\n");
		}
		if("1".equals(mal.getInchiestaindagine())){
			constructedNote.append("Inchiesta effettuata: SI"+"\r\n");

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			if(mal.getDatainizioindagine()!=null){
				constructedNote.append("Data inizio indagine: "+sdf.format(mal.getDatainizioindagine())+"\r\n");
			}
			if(mal.getDatafineindagine()!=null){
				constructedNote.append("Data fine indagine:"+sdf.format(mal.getDatafineindagine())+"\r\n");
			}
		}
		if(mal.getNessoGlobale()!=null){
			constructedNote.append("Nesso globale: "+mal.getNessoGlobale().getDescrizione()+"\r\n");
		}
		if(mal.getNotifica()!=null){
			constructedNote.append("Notifica: "+mal.getNotifica().getDescrizione()+"\r\n");
		}
		if(mal.getNote()!=null){
			//constructedNote.append("\r\nNOTE\r\n");
			constructedNote.append("\r\nNote: "+mal.getNote()+"\r\n");
		}
		if("1".equals(mal.getEsitoindagine())){
			constructedNote.append("Esito: CNR\r\n");
		}else if("2".equals(mal.getEsitoindagine())){
			constructedNote.append("Esito: Altro esito\r\n");
		}
		
		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";
		
		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}
	
	private void manageAteco(MalattiaProfessionale malProf) {
		if(malProf==null)
			return;
		
		if(malProf.getAttualeSede()==null && malProf.getAttualeDitta()==null)
			return;
		
		Long sediId = -1L;
		Long dittaId = -1L;
		
		if(malProf.getAttualeSede()!=null)
			sediId = malProf.getAttualeSede().getInternalId();
		
		if(malProf.getAttualeDitta()!=null)
			dittaId = malProf.getAttualeDitta().getInternalId();
		
		queryAtecoP.setParameter("sediId", sediId);
		
		List<CodeValueAteco> list = queryAtecoP.getResultList();
		if(list!=null && !list.isEmpty()) {
			CodeValueAteco ateco = list.get(0);
			malProf.setComparto(ateco);
		}else {
			log.warn("Unable to find CodeValueAteco for dittaId: "+dittaId+" sediId:"+sediId);
		}
		
		return;
	}
}
