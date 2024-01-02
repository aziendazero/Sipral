package com.phi.db.importer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.*;
import com.prevnet.entities.*;
import org.apache.log4j.Logger;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.prevnet.mappings.MapPratiche;

@SuppressWarnings({"unchecked","unused"})
public class PraticheImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(PraticheImporter.class.getName());
	private Query qRisk;
	
	private static PraticheImporter instance = null;
	
	public static PraticheImporter getInstance() {
		if(instance == null) {
			instance = new PraticheImporter();
		}
		return instance;
	}
	
	public PraticheImporter() {
		super();
		qRisk = targetEm.createQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.id LIKE 'phidic.spisal.frisk.agmal.%' AND cv.code = :code AND cv.type = 'S' ");
	}

	public static void main(String[] args) {
		PraticheImporter importer = new PraticheImporter();
		importer.deleteImportedData(ulss);
		List<Pratiche> pratiche = importer.readPratiche();
		if(pratiche!=null){
			for(Pratiche pratica : pratiche){
				importer.importPratica(pratica);
			}
		}
		importer.closeResource();

	}
	
	public void aggiornaRicorsi(Pratiche source) {
		if(checkMapping(source.getIdprocpratiche())){
			Procpratiche target = getMapped(source.getIdprocpratiche());
			
			/*
			 * se la pratica è stata toccata ha degli eventi perciò non la aggiorniamo
			 */
			Query qEvent = targetEm.createQuery("SELECT ev FROM ProcpraticheEvent ev WHERE ev.procpratiche = :pr AND ev.inserimentoManuale is not null");
			qEvent.setParameter("pr", target);
			List<ProcpraticheEvent> eventList = qEvent.getResultList();
			if(eventList.size() > 0) {
				return;
			}
			
			MedicinaLavoro mdl = target.getMedicinaLavoro();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			if(source.getRicorsigiudizimedicis()!=null && !source.getRicorsigiudizimedicis().isEmpty()) {
				
				// Set anagrafiche
				PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
				PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
				
				Ricorsigiudizimedici r = source.getRicorsigiudizimedicis().get(0);
				mdl.setPatient(persImp.importPerson(r.getLavoratore()));
				
				Sedi sede = ditteImp.importDittaCompleta(r.getDitta());
				mdl.setSedeAttuale(sede);
				if(sede!=null) {
					mdl.setDittaAttuale(sede.getPersonaGiuridica());
				}
				
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
				List<Protocollo> protocolloList = target.getProtocollo();
				Protocollo master = null;

				if (protocolloList != null && !protocolloList.isEmpty()) { 
					for (Protocollo protocollo : protocolloList) {
						if (((Protocollo)protocollo).getIsMaster()){
							master=protocollo;
						}
					}
				}
				
				String motivazione="";
				
				if(master!=null) {
					if(r.getDataaccertamento()!=null) {
						master.setDataGiudizio(r.getDataaccertamento());
					}
					
					if(r.getDatanotifica()!=null) {
						master.setDataNotificaGiudizio(r.getDatanotifica());
					}
				}else {
					if(r.getDataaccertamento()!=null) {
						motivazione+=("DATA GIUDIZIO: "+ sdf.format(r.getDataaccertamento())+ "\n");
					}
					
					if(r.getDatanotifica()!=null) {
						motivazione+=("DATA NOTIFICA: " + sdf.format( r.getDatanotifica())+ "\n");
					}
				}
				
				//String motivoConData = null;
				if(r.getDataricorso()!=null) {
					
					motivazione += ("DATA RICORSO: " + sdf.format(r.getDataricorso()) + "\n");
				}
				
				if(r.getMotivazione()!=null && !"".equals(r.getMotivazione())) {

					motivazione += r.getMotivazione();
				}
				
				if(motivazione!=null && !"".equals(motivazione)) {
					mdl.setMotivoRicorso(motivazione);
				}
				
				if(r.getDatavisita()!=null) {
					mdl.setDataAccertamento(r.getDatavisita());
				}
				
				if(r.getNoteesito()!=null && !"".equals(r.getNoteesito())) {
					mdl.setCommissione(r.getNoteesito());
				}
			}
			
			saveOnTarget(target);
			
		}
	}
	
	public Procpratiche importPratica(Pratiche source){
		if(!checkMapping(source.getIdprocpratiche())){
			Procpratiche target = new Procpratiche();
			
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(source.getTimestampinsmod());
			
			ServiceDeliveryLocation asl = findUlss(ulss);
			ServiceDeliveryLocation spisal = null;
			ServiceDeliveryLocation lineaLavoro = null;
			
			String workingLine = null;
			if(source.getIdprocedura()!=null){
				if(source.getTipoPratica()!=null)
					workingLine = getWorkingLine(source.getIdprocedura().intValue(), source.getTipoPratica());
				else
					workingLine = getWorkingLine(source.getIdprocedura().intValue(), null);
			}
			
			lineaLavoro = findLineaDiLavoro(ulss, distretto, workingLine);
			if(lineaLavoro==null){
				return null;
			}
			target.setServiceDeliveryLocation(lineaLavoro);

			if(lineaLavoro.getParent()!=null){
				spisal=lineaLavoro.getParent();
			}
			target.setUoc(spisal);
			
			String note = null;
			if(source.getNote()!=null){
				int length = source.getNote().length();
				note = source.getNote().substring(0,length<=4000?length:4000);
			}
				
			target.setNote(note);
			
			if("1".equals(source.getNascondi())){
				target.setIsActive(false);
			}
			
			/*
			 * 	0=aperta
			 * 	1=chiusa
			 * 	2=sospesa
			 * 	3=annullata
			 */
			if("0".equals(source.getCompleta()) && !source.getIdprocedura().equals(BigDecimal.valueOf(6))){
				target.setStatusCode(getStatus("status.generic.active"));
			}else if("1".equals(source.getCompleta()) || source.getIdprocedura().equals(BigDecimal.valueOf(6))){
				target.setStatusCode(getStatus("status.generic.completed"));
			}else if("2".equals(source.getCompleta())){
				target.setStatusCode(getStatus("status.generic.suspended"));
			}else if("3".equals(source.getCompleta())){
				target.setStatusCode(getStatus("status.generic.nullified"));
			}
			
			target.setData(source.getData());
			target.setCompletedDate(source.getDatacompleta());
			
			String riferimento = null;
			if(source.getRiferimento()!=null){
				int length = source.getRiferimento().length();
				riferimento = source.getRiferimento().substring(0,length<=1000?length:1000);
			}
				
			target.setRiferimento(riferimento);
			
			String wlCode = "??";
			if(lineaLavoro.getArea()!=null)
				wlCode = lineaLavoro.getArea().getAbbreviation();
			if (wlCode.equals("AML") && source.getIdprocedura().equals(BigDecimal.valueOf(101))) {
				wlCode = "AML_RA";
			}
			if(source.getNumero()!=null){
				DecimalFormat df = new DecimalFormat("00000");
				String nr = df.format(source.getNumero());
				
				if(source.getAnno()!=null){
					String numero = ulss+"_"+source.getAnno().toPlainString()+"_"+wlCode+"_"+nr;
					target.setNumero(numero);
					target.setNrPratica(source.getNumero().intValue());
				}else {
					String numero = ulss+"_????_"+wlCode+"_"+nr;
					target.setNumero(numero);
					target.setNrPratica(source.getNumero().intValue());
				}
			}
			
			//OPERATORI
			List<Assegnazionepratiche> assegnazioni = source.getAssegnazionepratiches();
			if(assegnazioni!=null){
				if(target.getOperatori()==null)
					target.setOperatori(new ArrayList<Operatore>());
				
				for(Assegnazionepratiche a : assegnazioni){
					if(a.getOperatori()!=null){
						Operatore o = getOperatore(a.getOperatori());
						if(o!=null){
							if(!target.getOperatori().contains(o))
								target.getOperatori().add(o);
							
							if("1".equals(a.getRdp()))
								target.setRdp(o.getEmployee());
							else if("3".equals(a.getRdp()))
								target.setRfp(o.getEmployee());
							else if("2".equals(a.getRdp()))
								target.setAuthor(o.getEmployee());
							
							if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
								if(target.getUpg()==null)
									target.setUpg(new ArrayList<Operatore>());
								
								if(!target.getUpg().contains(o))
									target.getUpg().add(o);
							}
						}
					}
				}
			}
			
			PrevnetNotes prevNotes = new PrevnetNotes();
			prevNotes.setCreatedBy(this.getClass().getSimpleName()+ulss);
			prevNotes.setCreationDate(source.getTimestampinsmod());
			addToPrevnetNotes(prevNotes, source);
			
			saveOnTarget(prevNotes);
			target.setPrevnetNotes(prevNotes);
			
			saveOnTarget(target);
			
			importProtocollo(source, target);
			
			if(source.getFascicolo()!=null){
				TagFascicolo tag = getFascicolo(source.getFascicolo());
				if(tag!=null){
					queryFascicolo.setParameter("tagId", tag.getInternalId());
					queryFascicolo.setParameter("pratId", target.getInternalId());
					queryFascicolo.executeUpdate();
				}
			}
			
			//TIPO PRATICA
			if(source.getTipoPratica()!=null){
				TagFascicolo tag = getFascicolo(source.getTipoPratica());
				if(tag!=null){
					queryFascicolo.setParameter("tagId", tag.getInternalId());
					queryFascicolo.setParameter("pratId", target.getInternalId());
					queryFascicolo.executeUpdate();
				}
			}
			
			//PROGETTO
			if(source.getProgetto()!=null){
				TagFascicolo tag = getFascicolo(source.getProgetto());
				if(tag!=null){
					queryFascicolo.setParameter("tagId", tag.getInternalId());
					queryFascicolo.setParameter("pratId", target.getInternalId());
					queryFascicolo.executeUpdate();
				}
			}
			
			if(source.getEventis()!=null){
				
				for(Eventi e : source.getEventis()){
					ProcpraticheEvent event = new ProcpraticheEvent();
					event.setCreatedBy(this.getClass().getSimpleName()+ulss);
					event.setCreationDate(e.getData());
					
					event.setProcpratiche(target);
					event.setTesto(e.getDescrizione());
					
					saveOnTarget(event);
				}				
			}
			
			//SOPRALLUOGHI
			String noteSop = "";
			StringBuffer noteProvv = new StringBuffer("");
			if(source.getSopralluoghidips()!=null){
				SopralluoghiImporter sopImp = SopralluoghiImporter.getInstance();
				ProvvedimentiImporter provImp = ProvvedimentiImporter.getInstance();
				for(Sopralluoghidip s : source.getSopralluoghidips()){
					Attivita sop = sopImp.importSopralluogo(s);
					//targetEm.refresh(sop);
					sop.setProcpratiche(target);
					target.addAttivita(sop);
					saveOnTarget(sop);
					
					if(s.getProvvedimenti()!=null){
						for(ProvvedimentiPrevnet sourceProv : s.getProvvedimenti()){
							CodeValue type = getMappedCode(sourceProv.getTipologia(), ulss);
							
							if(type!=null && "Disp".equals(type.getCode())){
								provImp.importMiglioramento(sourceProv, sop);
							}else if(type!=null && ("758".equals(type.getCode()) || "301bis".equals(type.getCode()))){
								provImp.importProvvedimento(sourceProv, sop, noteProvv);

							}
							
							if(sourceProv.getOperatoriprovvedimentis()!=null && !sourceProv.getOperatoriprovvedimentis().isEmpty()){
								if(target.getOperatori()==null)
									target.setOperatori(new ArrayList<Operatore>());
								
								for(Operatoriprovvedimenti opProv : sourceProv.getOperatoriprovvedimentis()){
									if(opProv.getOperatore()!=null){
										Operatore o = getOperatore(opProv.getOperatore());
										if(o!=null){
											if(!target.getOperatori().contains(o))
												target.getOperatori().add(o);
																						
											if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
												if(target.getUpg()==null)
													target.setUpg(new ArrayList<Operatore>());
												
												if(!target.getUpg().contains(o))
													target.getUpg().add(o);
											}
										}
									}
								}
							}
						}
					}

					// I00090719 note tecniche punto 1: aggiungo l'informazione sull'infrazione
					if(s.getOptinfrrilev()!=null && !"".equals(s.getOptinfrrilev())) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
						if("0".equals(s.getOptinfrrilev())) {
							noteSop+=("Attività "+sop.getNumero()+"/"+(sop.getDataInizio()!=null?sdf.format(sop.getDataInizio()):"")+": Nessuna Infrazione\r\n");
						}else if("1".equals(s.getOptinfrrilev())) {
							noteSop+=("Attività "+sop.getNumero()+"/"+(sop.getDataInizio()!=null?sdf.format(sop.getDataInizio()):"")+": Infrazione Rilevata\r\n");
						}
					}
					
				}

				//ATTI
				if("050120".equals(ulss) || "050121".equals(ulss) || "050122".equals(ulss)){
					AttiImporter attiImp = AttiImporter.getInstance();
					if(source.getAttis()!=null){
						for(Atti atti : source.getAttis()){
							Atto atto = attiImp.importAtti(atti);
							atto.setProcpratiche(target);
							saveOnTarget(atto);
						}
					}
				}
				String pNote = prevNotes.getNote();
				if(pNote==null)
					pNote="";
				
				pNote+=noteSop;
				if(noteProvv.length()>0){
					pNote+=noteProvv.toString();
				}
				prevNotes.setNote(pNote);
			}
			
			//targetEm.refresh(target); // reload attivita elementari, altrimenti getCantiereFromSopralluogo non trova niente
			
			//LINEE DI LAVORO
			if(workingLine!=null){
				if(workingLine.endsWith("workddisease") && source.getMalattieprofessionalis()!=null && !source.getMalattieprofessionalis().isEmpty()){
					importMalProf(source, target);
					
				}else if(workingLine.endsWith("workaccident") && source.getInfortuniPrevnets()!=null && !source.getInfortuniPrevnets().isEmpty()){
					importInfortuni(source, target);

				}else if(workingLine.endsWith("technicaladvice")){
					importNips(source, target);

				}else if(workingLine.endsWith("supervision")){
					importVigilanza(source, target);

				}else if(workingLine.endsWith("training") || workingLine.endsWith("information") 
					 || workingLine.endsWith("lifestyle") || workingLine.endsWith("counseling")){
					
					importInformation(source, target);
				}else if (workingLine.endsWith("workmedicine")) {
					importMdl(source,target);
				}
			}
			
			//ULTIMO AGGIORNAMENTO DEI RIFERIMENTI
			
			
			saveMapping(source, target);
		}
		return getMapped(source.getIdprocpratiche());
	}
	
	private void importMalProf(Pratiche source, Procpratiche target){
		Malattieprofessionali sMal = source.getMalattieprofessionalis().get(0);
		if(sMal!=null){
			MalProfImporter malImp = MalProfImporter.getInstance();
			MalattiaProfessionale tMal = malImp.importMalProf(sMal, target);
			//targetEm.refresh(target);//aggiorno la lista infortuni e inchieste...
			
			saveOnTarget(target);

			//OPERATORI
			if(target.getOperatori()==null)
				target.setOperatori(new ArrayList<Operatore>());
			
			Operatore o1 = getOperatore(sMal.getOperatore());
			if(o1!=null){
				if(!target.getOperatori().contains(o1))
					target.getOperatori().add(o1);
			}
			
			//MALPROF UPG
			List<Malattieproupg> operatoriUpg = sMal.getMalattieproupgs();
			if(operatoriUpg!=null){				
				for(Malattieproupg a : operatoriUpg){
					if(a.getOperatore()!=null){
						Operatore o = getOperatore(a.getOperatore());
						if(o!=null){
							if(!target.getOperatori().contains(o))
								target.getOperatori().add(o);
																		
							if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
								if(target.getUpg()==null)
									target.setUpg(new ArrayList<Operatore>());
								
								if(!target.getUpg().contains(o))
									target.getUpg().add(o);
							}
						}
					}
				}
			}
		}
	}
	
	private void importInfortuni(Pratiche source, Procpratiche target){
		PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
		for(InfortuniPrevnet sInf : source.getInfortuniPrevnets()){
			if(sInf!=null){
				InfortuniImporter infImp = InfortuniImporter.getInstance();
				infImp.importInfortunio(sInf, target);
				//targetEm.refresh(target);//aggiorno la lista infortuni e inchieste...
				
				//INFORTUNI TESTIMONI -> SIT DI DURATA 0
				if(sInf.getInfortunitestimonis()!=null && !sInf.getInfortunitestimonis().isEmpty()){
					for(Infortunitestimoni teste : sInf.getInfortunitestimonis()){
						if(teste.getUtenti()!=null){
							Attivita sit = new Attivita();
							sit.setCreatedBy(this.getClass().getSimpleName()+ulss);
							sit.setCreationDate(target.getCreationDate());
							sit.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.sit"));
							sit.setProcpratiche(target);
							sit.setDataInizio(target.getCreationDate());
							sit.setDataFine(sit.getDataInizio());
							saveOnTarget(sit);
							
							Person pers = persImp.importPerson(teste.getUtenti());
							Soggetto testimone = new Soggetto();
							testimone.setCreatedBy(this.getClass().getSimpleName()+ulss);
							testimone.setCreationDate(target.getCreationDate());
							testimone.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
							testimone.setUtente(pers);
							testimone.setAttivita(sit);
							saveOnTarget(testimone);
							
							if(teste.getTestimonianza()!=null){
								AcquisizioneInformazioni info = new AcquisizioneInformazioni();
								info.setCreatedBy(this.getClass().getSimpleName()+ulss);
								info.setCreationDate(target.getCreationDate());
								String note = null;
								if(teste.getTestimonianza()!=null){
									int length = teste.getTestimonianza().length();
									note = teste.getTestimonianza().substring(0,length<=4000?length:4000);
								}
								info.setOggetto(note);
								saveOnTarget(info);
								
								sit.setAcquisizioneInformazioni(info);
								testimone.setAcquisizioneInformazioni(info);
								saveOnTarget(sit);
								saveOnTarget(testimone);
							}
						}
					}
				}
				
				
				//OPERATORI
				if(target.getOperatori()==null)
					target.setOperatori(new ArrayList<Operatore>());
				
				Operatore o1 = getOperatore(sInf.getOperatore());
				if(o1!=null){
					if(!target.getOperatori().contains(o1))
						target.getOperatori().add(o1);
				}
				
				//INFORTUNI UPG
				List<Infortuniupg> operatoriUpg = sInf.getInfortuniupgs();
				if(operatoriUpg!=null){
					
					for(Infortuniupg a : operatoriUpg){
						if(a.getOperatori()!=null){
							Operatore o = getOperatore(a.getOperatori());
							if(o!=null){
								if(!target.getOperatori().contains(o))
									target.getOperatori().add(o);
																			
								if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
									if(target.getUpg()==null)
										target.setUpg(new ArrayList<Operatore>());
									
									if(!target.getUpg().contains(o))
										target.getUpg().add(o);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void importProtocollo(Pratiche source, Procpratiche target){
		if(source.getRichiestaregistraziones()!=null && !source.getRichiestaregistraziones().isEmpty()){
			ProtocolloImporter protImp = ProtocolloImporter.getInstance();
			Protocollo prot = protImp.importProtocollo(source.getRichiestaregistraziones().get(0), target);

			if (target.getProtocollo() == null) {
				target.setProtocollo(new ArrayList<Protocollo>());
			}
			if(!target.getProtocollo().contains(prot)) {
				target.getProtocollo().add(prot);
			}
			
			saveOnTarget(target);
		}
	}
	
	private void importVigilanza(Pratiche source, Procpratiche target){
		VigilanzaImporter vigImp = VigilanzaImporter.getInstance();
		Vigilanza vigilanza = vigImp.importVigilanza(source, target);
		target.setVigilanza(vigilanza);
		
		saveOnTarget(target);
	}
	
	private void importInformation(Pratiche source, Procpratiche target){
		InformationImporter infoImp = InformationImporter.getInstance();
		Object information = infoImp.importInformation(source, target);
		//targetEm.refresh(target);
		
		saveOnTarget(target);
	}
	
	private void importMdl(Pratiche source, Procpratiche target){
		List<Visitespecialistiche> sourceVisite = source.getVisitespecialistiches();
		BigDecimal subtype = source.getIdprocedura();
		VisitespecialImporter vsimp = VisitespecialImporter.getInstance();
		
		if (sourceVisite == null || sourceVisite.isEmpty()) {
			
			vsimp.importRicorsoAvverso(source,target,subtype);
			return;
		}
		
		
		
		
		for (Visitespecialistiche vis : sourceVisite) {
			
			Attivita visitaMedica = vsimp.importVisitaspecialistiche(vis, target, subtype);

			//targetEm.refresh(visitaMedica);
			visitaMedica.setProcpratiche(target);
			target.addAttivita(visitaMedica);
			saveOnTarget(visitaMedica);
		}
		
	}
	
	private void importNips(Pratiche source, Procpratiche target){
		Parerigen parerigen = null;
		if(source.getParerigens()!=null && !source.getParerigens().isEmpty())
			parerigen = source.getParerigens().get(0);
		
		Lavoratricimadre lav = null;
		if(source.getLavoratricimadres()!=null && !source.getLavoratricimadres().isEmpty())
			lav = source.getLavoratricimadres().get(0);

		Autorizzazionideroga aut = null;
		if(source.getAutorizzazioniderogas()!=null && !source.getAutorizzazioniderogas().isEmpty())
			aut = source.getAutorizzazioniderogas().get(0);

		NipsImporter nipsImp = NipsImporter.getInstance();
		if(parerigen!=null){
			ParereTecnico par = nipsImp.importParere(parerigen, target);
			//targetEm.refresh(target);
			
			saveOnTarget(target);
		}else if(lav!=null){
			
			ParereTecnico par = nipsImp.importLavoratrice(lav, target);
			//targetEm.refresh(target);
			
			saveOnTarget(target);
		}else if(aut!=null){
			ParereTecnico par = nipsImp.importAutorizzazione(aut, target);
			//targetEm.refresh(target);

			saveOnTarget(target);
		}
		
		
		if(source.getParerigenericis()!=null && !source.getParerigenericis().isEmpty()){
			Parerigenerici parerigenici = source.getParerigenericis().get(0);
			Operatore op = getOperatore(parerigenici.getOperatore());
			if(op!=null){
				if(!target.getOperatori().contains(op))
					target.getOperatori().add(op);
															
				if(op.getEmployee()!=null && Boolean.TRUE.equals(op.getEmployee().getUpg())){
					if(target.getUpg()==null)
						target.setUpg(new ArrayList<Operatore>());
					
					if(!target.getUpg().contains(op))
						target.getUpg().add(op);
				}
			}
			
			//ALTRI OPERATORI
			List<Parerigenericialtriop> operatori = parerigenici.getParerigenericialtriops();
			if(operatori!=null){
				if(target.getOperatori()==null)
					target.setOperatori(new ArrayList<Operatore>());
				
				for(Parerigenericialtriop a : operatori){
					if(a.getOperatore()!=null){
						Operatore o = getOperatore(a.getOperatore());
						if(o!=null){
							if(!target.getOperatori().contains(o))
								target.getOperatori().add(o);
																		
							if(o.getEmployee()!=null && Boolean.TRUE.equals(o.getEmployee().getUpg())){
								if(target.getUpg()==null)
									target.setUpg(new ArrayList<Operatore>());
								
								if(!target.getUpg().contains(o))
									target.getUpg().add(o);
							}
						}
					}
				}
			}
		}		
	}
	
	private void updateRiferimenti(Pratiche source, Procpratiche target){
		
		PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
		PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
		CantieriImporter cantImp = CantieriImporter.getInstance();
		
		
		PraticheRiferimenti refs = target.getPraticheRiferimenti();
		if(refs==null){
			refs = new PraticheRiferimenti();
			refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
			refs.setCreationDate(new Date());
		}
		if(source.getRichiestaregistraziones()!=null && !source.getRichiestaregistraziones().isEmpty()){
			Richiestaregistrazione req = source.getRichiestaregistraziones().get(0);
			
			//RICHIEDENTE
			if(req.getIdtipoanrichied()!=null && refs.getRichiedente()==null){
				switch (req.getIdtipoanrichied().intValue()) {
				case 0:
					refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.refssource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getRichiedenteAnag());
					if(sede!=null){
						refs.setRichiedenteSede(sede);
						refs.setRichiedenteDitta(sede.getPersonaGiuridica());
					}
					
					break;
				case 3:
					//NON E' PREVISTO UN CANTIERE COME RICHIEDENTE
					
					break;
				case 4:
					refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.refssource.utente"));
					Person pers = persImp.importPerson(req.getRichiedenteAnag());
					if(pers!=null){
						refs.setRichiedenteUtente(pers);
					}
					break;
				default:
					break;
				}
			}
			
			//RIFERITO A
			if(req.getIdtipoanagrafica()!=null && refs.getRiferimento()==null){
				switch (req.getIdtipoanagrafica().intValue()) {
				case 0:
					refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.refssource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getRiferitoAnag());
					if(sede!=null){
						refs.setRiferimentoSede(sede);
						refs.setRiferimentoDitta(sede.getPersonaGiuridica());
					}
					break;
				case 3:	
					refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.refssource.cantiere"));
					Cantiere cant = cantImp.importCantiere(req.getRiferitoAnag());
					if(cant!=null){
						refs.setRiferimentoCantiere(cant);
					}
					
					break;
				case 4:
					refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.refssource.utente"));
					Person pers = persImp.importPerson(req.getRiferitoAnag());
					if(pers!=null){
						refs.setRiferimentoUtente(pers);
					}
					break;
				default:
					break;
				}
			}
			
			//UBICAZIONE
			if(req.getIdtipoanubicaz()!=null && refs.getUbicazione()==null){
				switch (req.getIdtipoanubicaz().intValue()) {
				case 0:
					refs.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.refssource.ditta"));
					Sedi sede = ditteImp.importDittaCompleta(req.getUbicazioneAnag());
					if(sede!=null){
						refs.setUbicazioneSede(sede);
						refs.setUbicazioneDitta(sede.getPersonaGiuridica());
						if(sede.getAddr()!=null)
							refs.setUbicazioneAddr(sede.getAddr().cloneAd());
					}
					break;
				case 3:
					refs.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.refssource.cantiere"));	
					Cantiere cant = cantImp.importCantiere(req.getUbicazioneAnag());
					if(cant!=null){
						refs.setUbicazioneCantiere(cant);
						if(cant.getAddr()!=null)
							refs.setUbicazioneAddr(cant.getAddr().cloneAd());
					}
					break;
				case 4:
					refs.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.refssource.utente"));
					Person pers = persImp.importPerson(req.getUbicazioneAnag());
					if(pers!=null){
						refs.setUbicazioneUtente(pers);
						if(pers.getAddr()!=null)
							refs.setUbicazioneAddr(pers.getAddr().cloneAd());
					}
					break;
				default:
					break;
				}
			}		
		}
			
		refs.addProcpratiche(target);
		target.setPraticheRiferimenti(refs);
		saveOnTarget(refs);
		
	}
	
	public List<Pratiche> readPratiche() {
		List<Pratiche> pratiche = new ArrayList<Pratiche>();
		
		String hqlPratiche = "SELECT f FROM Pratiche f where idprocedura = 7 and idprocpratiche > 34000";
		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		pratiche = qPratiche.getResultList();
		return pratiche;
	}
	
	private TagFascicolo getFascicolo(Object fascicolo){
		FascicoliImporter imp = FascicoliImporter.getInstance();
		if(fascicolo instanceof FascicoliPrevnet)
			return imp.importFascicolo((FascicoliPrevnet)fascicolo);
		else if(fascicolo instanceof Tabelle)
			return imp.importFascicolo((Tabelle)fascicolo);
		else 
			return null;
	}
		
	private Operatore getOperatore(Operatori source){
		OperatoriImporter opImp = OperatoriImporter.getInstance();
		if(source!=null)
			return opImp.importOperatore(source);
		else
			return null;
		
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapPratiche m = sourceEm.find(MapPratiche.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapPratiche m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPratiche> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPratiche map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	private void saveMapping(Pratiche source, Procpratiche target){
		MapPratiche map = new MapPratiche();
		map.setIdprevnet(source.getIdprocpratiche());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);
		
		saveOnSource(map);
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate() + "   "+(target.getNumero() != null ? target.getNumero() : "") );
	}
	
	@Override
	protected void deleteImportedData(String ulss) {
		
		String hqlPratiche = "SELECT mf.idphi FROM MapPratiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPratiche+="WHERE mf.ulss = :ulss";
		
		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		if(ulss!=null && !ulss.isEmpty())
			qPratiche.setParameter("ulss", ulss);
		
		String hqlVigilanze = "SELECT v.internalId FROM Vigilanza v JOIN v.procpratiche p WHERE p.internalId IN (:ids)";
		Query qVigilanze = targetEm.createQuery(hqlVigilanze);
		
		List<Long> allIdPrat = qPratiche.getResultList();
		List<Long> idPrat = new ArrayList<Long>();
		while(allIdPrat!=null && !allIdPrat.isEmpty()){
			if(allIdPrat.size()>1000){
				idPrat.clear();
				idPrat.addAll(allIdPrat.subList(0, 1000));
				allIdPrat.removeAll(idPrat);
			}else{
				idPrat.clear();
				idPrat.addAll(allIdPrat);
				allIdPrat.removeAll(idPrat);
			}
			if(commit){
				
				//SGANCIO I SIT
				deleteSits(idPrat);
				
				//SGANCIO I FASCICOLI
				String updatePrat = "DELETE FROM tag_fascicolo_procpratiche WHERE PROCPRATICHE_INTERNAL_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdPrat = targetEm.createNativeQuery(updatePrat);
				qUpdPrat.setParameter("ids", idPrat);
				qUpdPrat.executeUpdate();
				targetEm.getTransaction().commit();
							
				//SGANCIO GLI EVENTI
				String deleteEventi = "DELETE FROM procpratiche_event WHERE PROCPRATICHE_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelEv = targetEm.createNativeQuery(deleteEventi);
				qDelEv.setParameter("ids", idPrat);
				qDelEv.executeUpdate();
				targetEm.getTransaction().commit();
				
				//SGANCIO GLI UPG
				String deleteUpg = "DELETE FROM procpratiche_upg WHERE PROCPRATICHE_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelUpg = targetEm.createNativeQuery(deleteUpg);
				qDelUpg.setParameter("ids", idPrat);
				qDelUpg.executeUpdate();
				targetEm.getTransaction().commit();
				
				//SGANCIO GLI OPERATORI
				String deleteOperatori = "DELETE FROM pratica_operatori WHERE PRATICA_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelOp = targetEm.createNativeQuery(deleteOperatori);
				qDelOp.setParameter("ids", idPrat);
				qDelOp.executeUpdate();
				targetEm.getTransaction().commit();
				
				//SGANCIO LE INCHIESTE
				String deleteInchieste = "DELETE FROM inchiesta_infortunio WHERE procpratiche_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelInchieste = targetEm.createNativeQuery(deleteInchieste);
				qDelInchieste.setParameter("ids", idPrat);
				qDelInchieste.executeUpdate();
				targetEm.getTransaction().commit();
								
				SopralluoghiImporter sopImp = SopralluoghiImporter.getInstance();
				sopImp.deleteImportedData(ulss);
				
				String deletePrat = "DELETE FROM Procpratiche e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelPrat = targetEm.createQuery(deletePrat);
				qDelPrat.setParameter("ids", idPrat);
				qDelPrat.executeUpdate();
				targetEm.getTransaction().commit();
				
				
				//DELETE VIGILANZE
				qVigilanze.setParameter("ids", idPrat);
				List<Long> allIdVig = qVigilanze.getResultList();
				if(allIdVig!=null && !allIdVig.isEmpty()){

					List<Long> idVig = new ArrayList<Long>();
					while(allIdVig!=null && !allIdVig.isEmpty()){
						if(allIdVig.size()>1000){
							idVig.clear();
							idVig.addAll(allIdVig.subList(0, 1000));
							allIdVig.removeAll(idVig);
						}else{
							idVig.clear();
							idVig.addAll(allIdVig);
							allIdVig.removeAll(idVig);
						}

						//SGANCIO I RUOLI
						String delPersRuoloHql = "DELETE FROM persona_ruolo WHERE vigilanza_id IN (:ids)";
						targetEm.getTransaction().begin();
						Query qDelPersRuolo = targetEm.createNativeQuery(delPersRuoloHql);
						qDelPersRuolo.setParameter("ids", idVig);
						qDelPersRuolo.executeUpdate();
						targetEm.getTransaction().commit();

						//SGANCIO PERSONEGIURIDICHESEDE	
						String delPersGiuSedeHql = "DELETE FROM persona_giuridica_sede WHERE vigilanza_id IN (:ids)";
						targetEm.getTransaction().begin();
						Query qDelPersGiuSede = targetEm.createNativeQuery(delPersGiuSedeHql);
						qDelPersGiuSede.setParameter("ids", idVig);
						qDelPersGiuSede.executeUpdate();
						targetEm.getTransaction().commit();

						//SGANCIO OPERAIAMIANTO
						String delOperaiHql = "DELETE FROM operaio_amianto WHERE vigilanza_id IN (:ids)";
						targetEm.getTransaction().begin();
						Query qDelOperai = targetEm.createNativeQuery(delOperaiHql);
						qDelOperai.setParameter("ids", idVig);
						qDelOperai.executeUpdate();
						targetEm.getTransaction().commit();

						//ELIMINO LE VIGILANZE
						String delVigilanzaHql = "DELETE FROM vigilanza WHERE internal_id IN (:ids)";
						targetEm.getTransaction().begin();
						Query qDelVigilanza = targetEm.createNativeQuery(delVigilanzaHql);
						qDelVigilanza.setParameter("ids", idVig);
						qDelVigilanza.executeUpdate();
						targetEm.getTransaction().commit();
					}			
				}
				
				//ELIMINO I RIFERIMENTI
				deleteRiferimenti(idPrat);
			}
		}
		
		if(commit){
			String deleteRiferimenti = "DELETE FROM pratiche_riferimenti";
			targetEm.getTransaction().begin();
			Query qDelRefs = targetEm.createNativeQuery(deleteRiferimenti);
			qDelRefs.executeUpdate();
			targetEm.getTransaction().commit();
			
			String deleteNotes = "DELETE FROM prevnet_notes";
			targetEm.getTransaction().begin();
			Query qDelNotes = targetEm.createNativeQuery(deleteNotes);
			qDelNotes.executeUpdate();
			targetEm.getTransaction().commit();
			
			String update = "DELETE FROM MAPPING_PRATICHE WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}
	
	private void deleteRiferimenti(List<Long> idPrat){
		//SGANCIO I RIFERIMENTI (SE PRESENTI)
		String hqlRefs = "SELECT refs.internalId FROM Procpratiche p JOIN p.praticheRiferimenti refs WHERE p.internalId IN (:ids)";			
		Query qRefs = targetEm.createQuery(hqlRefs);
		qRefs.setParameter("ids", idPrat);
		
		List<Long> idRefs = qRefs.getResultList();
		if(idRefs!=null && !idRefs.isEmpty()){
			if(commit){
				String deleteRiferimenti = "DELETE FROM PraticheRiferimenti e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelRiferimenti = targetEm.createQuery(deleteRiferimenti);
				qDelRiferimenti.setParameter("ids", idRefs);
				qDelRiferimenti.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		
	}
	
	private void deleteSits(List<Long> idPrat){
		//SGANCIO I SIT (SE PRESENTI)
		String hqlSit = "SELECT a.internalId FROM Attivita a JOIN a.procpratiche p JOIN a.code c "
				+ "WHERE c.code = 'sit' AND p.internalId IN (:ids)";			
		Query qSit = targetEm.createQuery(hqlSit);
		qSit.setParameter("ids", idPrat);
		
		List<Long> idAttivita = qSit.getResultList();
		if(idAttivita!=null && !idAttivita.isEmpty()){
			if(commit){
				//CERCO LE ACQUISIZIONI
				String hqlAcq = "SELECT acq.internalId FROM AcquisizioneInformazioni acq JOIN acq.attivita a WHERE a.internalId IN (:ids)";			
				Query qAcq = targetEm.createQuery(hqlAcq);
				qAcq.setParameter("ids", idAttivita);
				List<Long> idAcquisizioni = qAcq.getResultList();
				
				
				//SGANCIO I SOGGETTI
				String deleteSoggetto = "DELETE FROM soggetto WHERE ATTIVITA_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdPrat = targetEm.createNativeQuery(deleteSoggetto);
				qUpdPrat.setParameter("ids", idAttivita);
				qUpdPrat.executeUpdate();
				targetEm.getTransaction().commit();
				
				//SGANCIO GLI OPERATORI
				String deleteOperatori = "DELETE FROM attivita_operatori WHERE ATTIVITA_ID IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelEv = targetEm.createNativeQuery(deleteOperatori);
				qDelEv.setParameter("ids", idAttivita);
				qDelEv.executeUpdate();
				targetEm.getTransaction().commit();
				
				//ELIMINO LE ATTIVITA
				String deleteAttivita = "DELETE FROM Attivita e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelAttivita = targetEm.createQuery(deleteAttivita);
				qDelAttivita.setParameter("ids", idAttivita);
				qDelAttivita.executeUpdate();
				targetEm.getTransaction().commit();
				
				//ELIMINO LE ACQUISIZIONI
				if(idAcquisizioni!=null && !idAcquisizioni.isEmpty()){
					String deleteAcq = "DELETE FROM acquisizione_informazioni WHERE internal_id IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelAcq = targetEm.createNativeQuery(deleteAcq);
					qDelAcq.setParameter("ids", idAcquisizioni);
					qDelAcq.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}
	}
	
	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Procpratiche getMapped(long id){
		MapPratiche map = sourceEm.find(MapPratiche.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapPratiche m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapPratiche> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Procpratiche c = targetEm.find(Procpratiche.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qPrat = targetEm.createQuery("SELECT p FROM Procpratiche p WHERE p.internalId = :id");
			qPrat.setParameter("id", map.getIdphi());
			List<Procpratiche> lp = qPrat.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Pratiche prat){
		if(prat==null || prevNotes==null)
			return;
		
		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI PRATICA\r\n");
		
		if(prat.getTipoPratica()!=null){
			constructedNote.append("Tipo: "+prat.getTipoPratica().getDescrizione()+"\r\n");
		}
		
		if(prat.getAmbito()!=null){
			constructedNote.append("Ambito: "+prat.getAmbito().getDescrizione()+"\r\n");
		}
		
		if(prat.getAsl()!=null){
			constructedNote.append("ASL: "+prat.getAsl().getDescrizione()+"\r\n");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(prat.getDatainizioprocedimento()!=null){
			constructedNote.append("Data inizio procedimento: "+sdf.format(prat.getDatainizioprocedimento())+"\r\n");
		}
		
		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";
		
		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}
}
