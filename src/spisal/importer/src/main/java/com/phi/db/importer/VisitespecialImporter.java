package com.phi.db.importer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.baseEntity.AnamnesisMdl;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.ConclusioniMdl;
import com.phi.entities.baseEntity.FattoreRischio;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.PrestMdl;
import com.phi.entities.baseEntity.PrevnetNotes;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.prevnet.entities.Dettaglivisite;
import com.prevnet.entities.Esamiobiettivi;
import com.prevnet.entities.Pratiche;
import com.prevnet.entities.Ricorsigiudizimedici;
import com.prevnet.entities.Visitespecialistiche;
import com.prevnet.mappings.MapSopralluoghi;
import com.prevnet.mappings.MapVisSpec;

@SuppressWarnings({"unchecked"})
public class VisitespecialImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(VisitespecialImporter.class.getName());
	private Query qRisk;

	public VisitespecialImporter() {
		super();
		qRisk = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.id LIKE 'phidic.spisal.frisk.agmal.%' AND cv.code = :code AND cv.type = 'S' ");
	}

	private static VisitespecialImporter instance = null;

	public static VisitespecialImporter getInstance() {
		if(instance == null) {
			instance = new VisitespecialImporter();
		}
		return instance;
	}

	public void importRicorsoAvverso(Pratiche praticaSource, Procpratiche praticaTarget, BigDecimal subtype){


		//		Attivita attivitaTarget = new Attivita();
		MedicinaLavoro mdl = new MedicinaLavoro();

		//		attivitaTarget.setCreatedBy(this.getClass().getSimpleName()+ulss);
		//		attivitaTarget.setCreationDate(praticaSource.getCreationDate());
		//		attivitaTarget.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.visitamedica"));
		//		attivitaTarget.setDataInizio(praticaSource.getCreationDate());
		//		attivitaTarget.setDataFine(praticaSource.getCreationDate());
		//		
		mdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
		mdl.setCreationDate(praticaSource.getData());
		if (subtype.equals(BigDecimal.valueOf(153))) {
			mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
		}
		else if (subtype.equals(BigDecimal.valueOf(101))) {
			mdl.setType(getCodeValue("phidic.spisal.mdl.type.03"));

			if(praticaSource.getRicorsigiudizimedicis()!=null && !praticaSource.getRicorsigiudizimedicis().isEmpty()) {

				// Set anagrafiche
				PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
				PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();

				Ricorsigiudizimedici r = praticaSource.getRicorsigiudizimedicis().get(0);
				mdl.setPatient(persImp.importPerson(r.getLavoratore()));

				Sedi sede = ditteImp.importDittaCompleta(r.getDitta());
				mdl.setSedeAttuale(sede);
				if(sede!=null) {
					mdl.setDittaAttuale(sede.getPersonaGiuridica());
					manageAteco(mdl);
				}

				// Set medico competente
				if(r.getMedico()!=null) {
					MediciImporter medImp = MediciImporter.getInstance();
					Organization org = medImp.findOrganization(ulss);

					Physician med = medImp.importMedicoSpisal(r.getMedico(), org);
					if(med!=null) {
						mdl.setMedico(med);
					}
				}

				// Set date e note
				List<Protocollo> protocolloList = praticaTarget.getProtocollo();
				Protocollo master = null;

				if (protocolloList != null && !protocolloList.isEmpty()) { 
					for (Protocollo protocollo : protocolloList) {
						if (((Protocollo)protocollo).getIsMaster()){
							master=protocollo;
						}
					}
				}

				if(master!=null) {
					if(r.getDataaccertamento()!=null) {
						master.setDataGiudizio(r.getDataaccertamento());
					}

					if(r.getDatanotifica()!=null) {
						master.setDataNotificaGiudizio(r.getDatanotifica());
					}
				}

				if(r.getDatavisita()!=null) {
					mdl.setDataAccertamento(r.getDatavisita());
				}

				String motivoConData = null;
				if(r.getDataricorso()!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					motivoConData = "DATA RICORSO: " + sdf.format(r.getDataricorso()) + "\n";
				}

				if(r.getMotivazione()!=null && !"".equals(r.getMotivazione())) {
					if(motivoConData==null) {
						motivoConData = r.getMotivazione();
					}else {
						motivoConData += r.getMotivazione();
					}
				}

				if(motivoConData!=null && !"".equals(motivoConData)) {
					mdl.setMotivoRicorso(motivoConData);
				}

				// I00094505 nuova costruzione campo commissione
				StringBuffer commissione =  new StringBuffer();
				if(r.getDataaccertamento()!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					commissione.append("Data giudizio medico competente: "+sdf.format(r.getDataaccertamento())+"\r\n");
				}
				if(r.getDatanotifica()!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					commissione.append("Data giudizio notificata al lavoratore: "+sdf.format(r.getDatanotifica())+"\r\n");
				}
				if(r.getDatavisitacollegiale()!=null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					commissione.append("Data visita collegiale: "+sdf.format(r.getDatavisitacollegiale())+"\r\n");
				}
				if(r.getNoteesito()!=null && !"".equals(r.getNoteesito())) {
					//mdl.setCommissione(r.getNoteesito());
					commissione.append(r.getNoteesito());
				}
				int commissioneLength = commissione.length();
				mdl.setCommissione(commissione.substring(0,commissioneLength<=2500?commissioneLength:2500));

				addToPrevnetNotes(praticaTarget.getPrevnetNotes(), r);
			}


		}else if(subtype.equals(BigDecimal.valueOf(194))){
			if(praticaSource.getTipoPratica()!=null) {
				String praticaType = praticaSource.getTipoPratica().getUtcodice();
				if ("87371".equals(praticaType)) {
					// Ex-esposti uranio impoverito
					mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));
					mdl.setTipoExEsposto(getCodeValue("phidic.spisal.mdl.exesp.04"));
				} else if ("RE29".equals(praticaType)) {
					// Ex-esposti uranio amianto
					mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));
					mdl.setTipoExEsposto(getCodeValue("phidic.spisal.mdl.exesp.01"));
				} else if ("RE31".equals(praticaType)) {
					// Visite mediche volontari protezione civile
					mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
					mdl.setLavType(getCodeValue("phidic.spisal.mdl.wtype.04"));
				} else {
					log.error("invalid praticaType: " + praticaType);
					mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
				}
			}else{
				log.error("invalid praticaType: null");
				mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
			}


		} else {
			log.error("invalid subtype: "+subtype);
			mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
		}


		praticaTarget.setMedicinaLavoro(mdl);
		List<Procpratiche> praticheMdl = new ArrayList<Procpratiche>();
		praticheMdl.add(praticaTarget);
		mdl.setProcpratiche(praticheMdl);

		saveOnTarget(mdl);
		if(praticaSource.getRicorsigiudizimedicis()!=null && !praticaSource.getRicorsigiudizimedicis().isEmpty()) {
			Ricorsigiudizimedici r = praticaSource.getRicorsigiudizimedicis().get(0);
			// Set fattori di rischio
			if(r.getRischiogen()!=null){
				String code = r.getRischiogen().getCodice();
				if(code!=null && code.endsWith("."))
					code = code.substring(0, code.length()-1);

				qRisk.setParameter("code", code);
				List<CodeValuePhi> list = qRisk.getResultList();
				if(list!=null && !list.isEmpty()){
					FattoreRischio risk = new FattoreRischio();
					risk.setCreatedBy(this.getClass().getSimpleName()+ulss);
					risk.setCreationDate(new Date());
					risk.setMedicinaLavoro(mdl);
					risk.setCode(list.get(0));
					//risk.setCausa((CodeValuePhi)getMappedCode(r.getNessoGlobale(), ulss));

					saveOnTarget(risk);
				}
			}
		}

		saveOnTarget(praticaTarget);



	}


	public Attivita importVisitaspecialistiche(Visitespecialistiche source, Procpratiche praticaTarget, BigDecimal subtype){

		if(!checkMapping(source.getIdvisitespecialistiche())){

			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			MediciImporter medImp = MediciImporter.getInstance();

			//////////
			/// import Visita Specialista come attività eleementare, MedicinaLavoro, VisistaMdl, VisistaSp 
			//////////

			Attivita attivitaTarget = new Attivita();
			MedicinaLavoro mdl = new MedicinaLavoro();
			VisitaMdl visitaMdl = new VisitaMdl(); 
			VisitaSp visitaSp = new VisitaSp();   //Tutte visiste interne 
			ConclusioniMdl concMdl = new ConclusioniMdl();


			attivitaTarget.setCreatedBy(this.getClass().getSimpleName()+ulss);
			attivitaTarget.setCreationDate(source.getDatavisita());
			attivitaTarget.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.visitamedica"));

			String sourceNote = source.getNote();

			if (source.getIdfattorerischio() != null) {
				sourceNote += "\r\n\r\nFATTORE DI RISCHIO: "+source.getIdfattorerischio();
			}

			if (sourceNote != null && sourceNote.length() > 3995) {
				log.error("truncated note of vissta "+source.getIdvisitespecialistiche() +" too long");
				sourceNote= sourceNote.substring(0, 3995);
			}
			attivitaTarget.setNote(sourceNote);
			attivitaTarget.setDataInizio(source.getDatavisita());
			attivitaTarget.setDataFine(source.getDatavisita());



			////// MDL //////
			//impostando tutti come 1.1 Visite Mediche e accertamenti sanitari
			mdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
			mdl.setCreationDate(source.getDatavisita());
			if (subtype.equals(BigDecimal.valueOf(153))) {
				mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
			}
			else if (subtype.equals(BigDecimal.valueOf(101))) {
				mdl.setType(getCodeValue("phidic.spisal.mdl.type.03"));
			}else if(subtype.equals(BigDecimal.valueOf(194))){
				if(source.getPratica().getTipoPratica()!=null) {
					String praticaType = source.getPratica().getTipoPratica().getUtcodice();
					if ("87371".equals(praticaType)) {
						// Ex-esposti uranio impoverito
						mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));
						mdl.setTipoExEsposto(getCodeValue("phidic.spisal.mdl.exesp.04"));
					} else if ("RE29".equals(praticaType)) {
						// Ex-esposti uranio amianto
						mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));
						mdl.setTipoExEsposto(getCodeValue("phidic.spisal.mdl.exesp.01"));
					} else if ("RE31".equals(praticaType)) {
						// Visite mediche volontari protezione civile
						mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
						mdl.setLavType(getCodeValue("phidic.spisal.mdl.wtype.04"));
					} else {
						log.error("invalid praticaType: " + praticaType);
						mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
					}
				}else{
					log.error("invalid praticaType: null");
					mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
				}

			}
			else {
				log.error("invalid subtype: "+subtype);
				mdl.setType(getCodeValue("phidic.spisal.mdl.type.01"));
			}



			////// VisitaMdL //////
			visitaMdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
			visitaMdl.setCreationDate(source.getDatavisita());
			concMdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
			concMdl.setCreationDate(source.getDatavisita());
			visitaMdl.setCode(getCodeValue("phidic.spisal.mdl.subtype.01"));

			visitaMdl.setConclusioniMdl(concMdl);

			List<VisitaMdl> visiteMdl = new ArrayList<VisitaMdl>();
			visiteMdl.add(visitaMdl);


			visitaMdl.setAttivita(attivitaTarget);
			attivitaTarget.setVisitaMdl(visiteMdl);


			////// VisitaSp //////
			//FIXME: mdl > pratica?
			visitaSp.setCreatedBy(this.getClass().getSimpleName()+ulss);
			visitaSp.setCreationDate(source.getDatavisita());

			visitaMdl.setVisitaSp(visitaSp);
			visitaSp.setVisitaMdl(visiteMdl);
			visitaSp.setVisusOd("/10");
			visitaSp.setVisusOs("/10");

			//Fixme: RFP?
			//visitaSp.setRiferimentoInterno(praticaTarget.getRfp());


			//DITTA
			Sedi sede = null;
			if (source.getDitta() != null) {
				sede = ditteImp.importDittaCompleta(source.getDitta());
			} else if (source.getAnagraficaDitta() != null){
				sede = ditteImp.importDittaCompleta(source.getAnagraficaDitta());
			}

			if (sede!=null){
				mdl.setDittaAttuale(sede.getPersonaGiuridica());
			}

			List<Soggetto> soggetti = new ArrayList<Soggetto>();

			//PERSONA FISICA: Paziente?
			Person pers = null;
			if(source.getAnagraficaUtente()!=null){
				pers = persImp.importPerson(source.getAnagraficaUtente());
			}

			//persisti quanto fatto finira, cascade di VisitaMdl e VisistaSps 
			saveOnTarget(attivitaTarget);
			saveOnTarget(mdl);

			if(pers!=null){
				//sia a MedicinaLavoro che a attivita elementare
				//attivitaTarget.setLuogoUtente(pers);
				mdl.setPatient(pers);
				PraticheRiferimenti refs = praticaTarget.getPraticheRiferimenti();
				if(refs==null){
					refs=new PraticheRiferimenti();
					refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
					refs.setCreationDate(new Date());

				}
				refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
				refs.setRiferimentoUtente(pers);
				saveOnTarget(refs);
				praticaTarget.setPraticheRiferimenti(refs);

				Soggetto s = new Soggetto();
				s.setCreatedBy(this.getClass().getSimpleName()+ulss);
				s.setCreationDate(source.getDatavisita());
				s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

				s.setUtente(pers);
				s.setAttivita(attivitaTarget);

				saveOnTarget(s);  //Bisogna aver salvato l'attività per salvar il soggetto 
				soggetti.add(s);
			}



			//MEDICO CHE FA LA VISITA (--> operatore)
			if(source.getOperatori()!=null){
				List<Partecipanti> theList = new ArrayList<Partecipanti>();

				OperatoriImporter opImp = OperatoriImporter.getInstance();
				Operatore op = opImp.importOperatore(source.getOperatori());
				if(op!=null){

					//attaccare come operatore attività elementare e 
					Partecipanti p = new Partecipanti();
					p.setCreatedBy(this.getClass().getSimpleName()+ulss);
					p.setCreationDate(source.getDatavisita());
					p.setOperatore(op);
					p.setAttivita(attivitaTarget);
					saveOnTarget(p);
					//FIXME: non devo aggiungerlo ai partecipanti dell'attivita
					//theList.add(p);


					//ATTENZIONE FIXME... modifico la pratica
					List<Operatore> operatoriPratica = praticaTarget.getOperatori();

					if (operatoriPratica == null) {
						operatoriPratica = new ArrayList<Operatore>();
					}
					if(!operatoriPratica.contains(op)) {
						operatoriPratica.add(op);
					}
					saveOnTarget(praticaTarget);

				}

				//FIXME: non devo aggiungerlo ai partecipanti dell'attivita
				//attivitaTarget.setPartecipanti(theList);
				//saveOnTarget(attivitaTarget);



			}

			//MEDICO CURANTE (--> soggetti)  FIXME: da decommentare?
			//			if (source.getMedici() != null) {
			//				
			//				Physician med = medImp.importMedicoSpisal(source.getMedici(), medImp.findOrganization(ulss));
			//				if(med!=null){
			//					
			//					Soggetto s = new Soggetto();
			//					s.setCreatedBy(this.getClass().getSimpleName()+ulss);
			//					s.setCreationDate(source.getDatavisita());
			//					s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.medico"));
			//					
			//					s.setUtente(pers);
			//					s.setAttivita(attivitaTarget);
			//					
			//					saveOnTarget(s);
			//					soggetti.add(s);
			//					
			//				}
			//				
			//			}


			List <Attivita> attivitaPratica = praticaTarget.getAttivita();
			if (attivitaPratica == null) {
				attivitaPratica= new ArrayList<Attivita>();
				praticaTarget.setAttivita(attivitaPratica);
			}
			attivitaPratica.add(attivitaTarget);
			attivitaTarget.setProcpratiche(praticaTarget);

			praticaTarget.setMedicinaLavoro(mdl);
			List<Procpratiche> praticheMdl = new ArrayList<Procpratiche>();
			praticheMdl.add(praticaTarget);
			mdl.setProcpratiche(praticheMdl);

			saveOnTarget(mdl);
			saveOnTarget(praticaTarget);

			//add to prevnetnotes if any
			addToPrevnetNotes(praticaTarget.getPrevnetNotes(), source);


			//////////////////////////////////////////////////////////////////////
			/////////////////      ACCERTAMENTI                 //////////////////
			//////////////////////////////////////////////////////////////////////

			List<Dettaglivisite> listAccertamentiSource = source.getDettaglivisites();
			List<Attivita> attivitaChildren = new  ArrayList<Attivita>();

			for (Dettaglivisite dettVis : listAccertamentiSource) {
				if (dettVis.getVisitespecialistiche() == null) {

					continue;
				}

				Attivita attivitaAccert = new Attivita();


				attivitaAccert.setCreatedBy(this.getClass().getSimpleName()+ulss);
				attivitaAccert.setCreationDate(dettVis.getData());
				attivitaAccert.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.accertamento"));

				//if("050118".equals(ulss) || "050119".equals(ulss)) {
				attivitaAccert.setDataInizio(dettVis.getData());
				attivitaAccert.setDataFine(dettVis.getData());
				//}

				String accertSourceNote = dettVis.getNote();
				if (accertSourceNote == null)
					accertSourceNote = "";
				if (source.getIdfattorerischio() != null) {
					accertSourceNote += "\r\n\r\nFATTORE DI RISCHIO: "+source.getIdfattorerischio();
				}
				if (!accertSourceNote.isEmpty()) {

					if (accertSourceNote.length() > 3999) {
						log.error("truncated note of dett. visita "+dettVis.getIddettaglivisite() +" too long");
						accertSourceNote= accertSourceNote.substring(0, 3998);
					}

					attivitaAccert.setNote(accertSourceNote);
				}


				attivitaAccert.setParent(attivitaTarget);
				attivitaChildren.add(attivitaAccert);

				praticaTarget.getAttivita().add(attivitaAccert);
				attivitaAccert.setProcpratiche(praticaTarget);

				AccertaMdl accertMdl = new AccertaMdl(); 
				AccertSp accertSp = new AccertSp();   //Tutti accertamenti interni 


				////// AccertMdL //////
				accertMdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
				accertMdl.setCreationDate(source.getDatavisita());
				accertMdl.setCode(getCodeValue("phidic.spisal.mdl.subtype.01"));

				List<AccertaMdl> accertMdls = new ArrayList<AccertaMdl>();
				accertMdls.add(accertMdl);

				accertMdl.setAttivita(attivitaAccert);
				attivitaAccert.setAccertaMdl(accertMdls);

				////// AccertSp //////
				accertSp.setCreatedBy(this.getClass().getSimpleName()+ulss);
				accertSp.setCreationDate(source.getDatavisita());

				accertMdl.setAccertSp(accertSp);
				accertSp.setAccertaMdl(accertMdls);



				////// PrestaMdl //////
				PrestMdl prestazione = new PrestMdl();
				List<PrestMdl> prestazioni = new ArrayList<PrestMdl>();
				prestazioni.add(prestazione);

				prestazione.setPrest(cvPrestazione(dettVis.getIdprestazionesan()));
				prestazione.setDataReferto(dettVis.getData());

				prestazione.setAccertaMdl(accertMdl);
				accertMdl.setPrestMdl(prestazioni);

				saveOnTarget(prestazione);
				saveOnTarget(accertMdl);
				saveOnTarget(attivitaAccert);

			}

			attivitaTarget.setChildren(attivitaChildren);
			saveOnTarget(attivitaTarget);


			//////////////////////////////////////////////////////////////////////
			/////////////////      Esami obbiettivi             //////////////////
			//////////////////////////////////////////////////////////////////////


			Esamiobiettivi esameObbiettivoSource = source.getEsamiobiettivi();

			if (esameObbiettivoSource.getAltezza() != null)
				visitaSp.setAltezza(esameObbiettivoSource.getAltezza().toString());

			if (esameObbiettivoSource.getPeso() != null) {
				visitaSp.setPeso(esameObbiettivoSource.getPeso().toString());
			}

			if (esameObbiettivoSource.getCostituzioneFisica() != null) {
				String costituzione = esameObbiettivoSource.getCostituzioneFisica().getDescrizione();
				visitaSp.setNote("Costituzione fisica: "+costituzione);
			}


			if (esameObbiettivoSource.getOrtoscopia() != null) {

			}

			if (esameObbiettivoSource.getOsservazioni() != null) {

			}

			saveOnTarget(visitaSp);
			saveOnTarget(praticaTarget);


			//PERSISTERE attivitaTarget

			saveMapping(source, attivitaTarget);
		}

		return getMapped(source.getIdvisitespecialistiche());

	}	


	private Attivita getMapped(long id){
		MapVisSpec map = sourceEm.find(MapVisSpec.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapVisSpec m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapVisSpec> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Attivita c = targetEm.find(Attivita.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qFascicolo = targetEm.createQuery("SELECT tag FROM Attivita tag WHERE tag.internalId = :id");
			qFascicolo.setParameter("id", map.getIdphi());
			List<Attivita> tags = qFascicolo.getResultList();
			if(tags!=null && !tags.isEmpty()){
				return tags.get(0);
			}
		}

		return null;
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapVisSpec m = sourceEm.find(MapVisSpec.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapVisSpec m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapVisSpec> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapVisSpec map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}


	private void saveMapping(Visitespecialistiche source, Attivita target){
		MapVisSpec map = new MapVisSpec();
		map.setIdprevnet(source.getIdvisitespecialistiche());
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
		String hqlPratiche = "SELECT mf.idphi FROM MapVisSpec vs ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPratiche+="WHERE vs.ulss = :ulss";

		Query qVisSpec = sourceEm.createQuery(hqlPratiche);
		if(ulss!=null && !ulss.isEmpty())
			qVisSpec.setParameter("ulss", ulss);

		List<Long> allIdVis = qVisSpec.getResultList();
		List<Long> idVis = new ArrayList<Long>();
		while(allIdVis!=null && !allIdVis.isEmpty()){
			if(allIdVis.size()>1000){
				idVis.clear();
				idVis.addAll(allIdVis.subList(0, 1000));
				allIdVis.removeAll(idVis);
			}else{
				idVis.clear();
				idVis.addAll(allIdVis);
				allIdVis.removeAll(idVis);
			}
			if(commit){
				//				//SGANCIO I FATTORI DI RISCHIO
				//				String updatePrat = "DELETE FROM fattori_rischio WHERE VISITA_SPECIALISTICA_ID IN (:ids)";
				//				targetEm.getTransaction().begin();
				//				Query qUpdPrat = targetEm.createNativeQuery(updatePrat);
				//				qUpdPrat.setParameter("ids", idMal);
				//				qUpdPrat.executeUpdate();
				//				targetEm.getTransaction().commit();
				//				
				//				String updateDm = "DELETE FROM ditte_malattie WHERE MALATTIA_PROFESSIONALE_ID IN (:ids)";
				//				targetEm.getTransaction().begin();
				//				Query qUpdDm = targetEm.createNativeQuery(updateDm);
				//				qUpdDm.setParameter("ids", idMal);
				//				qUpdDm.executeUpdate();
				//				targetEm.getTransaction().commit();
				//				
				//				String deleteMal = "DELETE FROM MalattiaProfessionale e WHERE e.internalId IN (:ids)";
				//				targetEm.getTransaction().begin();
				//				Query qDelMal = targetEm.createQuery(deleteMal);
				//				qDelMal.setParameter("ids", idMal);
				//				qDelMal.executeUpdate();
				//				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_VISSPEC WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}


	private boolean contains(List<Soggetto> lst, Object ent){
		if(ent instanceof Person){
			Person p = (Person)ent;
			for(Soggetto s : lst){
				if(s.getUtente()!=null && s.getUtente().getInternalId()==p.getInternalId())
					return true;
			}
		}else if(ent instanceof Sedi){
			Sedi p = (Sedi)ent;
			for(Soggetto s : lst){
				if(s.getSede()!=null && s.getSede().getInternalId()==p.getInternalId())
					return true;
			}
		}else if(ent instanceof Cantiere){
			Cantiere p = (Cantiere)ent;
			for(Soggetto s : lst){
				if(s.getCantiere()!=null && s.getCantiere().getInternalId()==p.getInternalId())
					return true;
			}
		}
		return false;
	}



	private void addToPrevnetNotes(PrevnetNotes prevNotes, Visitespecialistiche vis){
		if(vis==null || prevNotes==null || vis.getTabelle2()==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		if(vis.getPratica().getRichiestaregistraziones()!=null && !vis.getPratica().getRichiestaregistraziones().isEmpty()){
			constructedNote.append("Descrizione Richiesta: "+vis.getPratica().getRichiestaregistraziones().get(0).getDescrrichiesta()+"\r\n");
		}
		constructedNote.append("DATI VISITA SPECIALISTICA\r\n");

		//		if(vis.getAsl()!=null){
		//			constructedNote.append("ASL Competente: "+vis.getAsl().getDescrizione()+"\r\n");
		//		}
		//		if(vis.getComuneComunicazione()!=null){
		//			constructedNote.append("Comune comunicazione: "+vis.getComuneComunicazione().getDescrizione()+"\r\n");
		//		}

		if(vis.getTabelle2()!=null) {
			constructedNote.append("MOTIVO\r\n");	
			constructedNote.append(vis.getTabelle2().getDescrizione()+"\r\n");	
		}

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private CodeValuePhi cvPrestazione(BigDecimal d) {

		if (d == null) {
			return null;
		}

		String idPrestazione = d.toString();
		if (idPrestazione.equals("10")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.832");
		} else if (idPrestazione.equals("11")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.2047");
		} else if (idPrestazione.equals("12")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.892");
		} else if (idPrestazione.equals("13")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.1773");
		} else if (idPrestazione.equals("15")) {
			//nn sicuro:
			//(PHI: STUDIO DELLA SENSIBILITA' AL COLORE. Test di acuità visiva e di discriminazione cromatica)
			//Prevent: ERGONOMIA-VISIVA
			return getCodeValue("phidic.spisal.pratiche.presmp.2021");
		} else if (idPrestazione.equals("29")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.921");
		} else if (idPrestazione.equals("30")) {
			return getCodeValue("phidic.spisal.pratiche.presmp.1795");
		} 

		log.error("invalid cv prestazione sanitaria: "+d);
		return null;



	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Ricorsigiudizimedici ric){
		if(ric==null || prevNotes==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI RICORSO\r\n");

		if(ric.getDatore()==null) {
			constructedNote.append("Richiedente: Lavoratore\r\n");
		}else {
			constructedNote.append("Richiedente: Datore di lavoro o rappr. legale\r\n");
		}

		//			if(ric.getDatavisitacollegiale()!=null) {
		//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		//				constructedNote.append("Data visita collegiale: "+sdf.format(ric.getDatavisitacollegiale())+"\r\n");
		//			}

		if(ric.getEsito()!=null) {
			constructedNote.append("Esito ricorso: "+ric.getEsito().getDescrizione()+"\r\n");
		}

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private void manageAteco(MedicinaLavoro mdl) {
		if(mdl==null)
			return;

		if(mdl.getSedeAttuale()==null && mdl.getDittaAttuale()==null)
			return;

		Long sediId = -1L;
		Long dittaId = -1L;

		if(mdl.getSedeAttuale()!=null)
			sediId = mdl.getSedeAttuale().getInternalId();

		if(mdl.getDittaAttuale()!=null)
			dittaId = mdl.getDittaAttuale().getInternalId();

		queryAtecoP.setParameter("sediId", sediId);

		List<CodeValueAteco> list = queryAtecoP.getResultList();
		if(list!=null && !list.isEmpty()) {
			CodeValueAteco ateco = list.get(0);
			mdl.setComparto(ateco);
		}else {
			log.warn("Unable to find CodeValueAteco for dittaId: "+dittaId+" sediId:"+sediId);
		}

		return;
	}
}
