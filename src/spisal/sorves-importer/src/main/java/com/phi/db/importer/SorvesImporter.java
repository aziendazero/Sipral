package com.phi.db.importer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.baseEntity.AccertaMdl;
import com.phi.entities.baseEntity.AnamnesisMdl;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.ConclusioniMdl;
import com.phi.entities.baseEntity.FattoreRischio;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.PrestMdl;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.SpisalAddr;
import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.II4ServiceDeliveryLocation;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.sorves.entities.Anagrafica;
import com.sorves.entities.Anamnesifamiliare;
import com.sorves.entities.Anamnesifisiologica;
import com.sorves.entities.Anamnesilavorativa;
import com.sorves.entities.Anamnesiremota;
import com.sorves.entities.Comuni;
import com.sorves.entities.Ecografia;
import com.sorves.entities.Esamealtro;
import com.sorves.entities.Esameistologico;
import com.sorves.entities.Esamilaboratorio;
import com.sorves.entities.Esamilaboratoriocod;
import com.sorves.entities.Esposizione;
import com.sorves.entities.Radiografia;
import com.sorves.entities.Spirometria;
import com.sorves.entities.Tabdiagnosialtroesame;
import com.sorves.entities.Tabdiagnosiecografica;
import com.sorves.entities.Tabdiagnosiistologica;
import com.sorves.entities.Tabdiagnosiradiografica;
import com.sorves.entities.Tabobiettivita;
import com.sorves.entities.Visita;
import com.sorves.mappings.MapPazienti;

@SuppressWarnings({"unchecked"})
public class SorvesImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(SorvesImporter.class.getName());
	private Query queryPerson;
	private DecimalFormat df = new DecimalFormat("000000");
	private static SorvesImporter instance = null;

	public static SorvesImporter getInstance() {
		if(instance == null) {
			instance = new SorvesImporter();
		}
		return instance;
	}

	public SorvesImporter() {
		super();

		queryPerson = targetEm.createQuery("SELECT p FROM Person p LEFT JOIN p.birthPlace.code bp WHERE "
				+ "p.name.giv = :nome AND "
				+ "p.name.fam = :cognome AND "
				+ "(p.birthTime = :dataNascita OR p.birthTime IS NULL) AND "
				+ "(bp.code = :istatNascita OR bp.code IS NULL) AND "
				+ "(p.fiscalCode = :cf OR p.fiscalCode IS NULL) AND "
				+ "p.isActive = :isActive "
				+ "ORDER BY p.creationDate DESC ");
	}

	public Person importPerson(Anagrafica source){
		Person paziente = null;
		if(!checkMapping(source.getId())){
			/*
			 * qui non interrogo aur: importo tutte le anagrafiche come snapshot storiche 
			 */
			paziente = importNewPerson(source, new Person());

			/*
			 * poi controllo se in anagrafica c'è almeno una persona con gli stessi dati: se si aggiorno gli isActive di modo da
			 * avere sempre e solo un record attivo (l'ultimo)
			 */
			Person anagrafica = checkAnagrafica(source);
			if(anagrafica!=null) {
				setIsActiveOnHistoricVersions(paziente);
			}

			/**
			 * SE l'utente è già stato mappato, allora ritorna il corrispondente
			 */
		}else{			
			paziente = getMapped(source.getId());
		}

		if(!checkMapCommPrat(source.getId())) {
			Date dataIniziale = new Date();
			if(source.getVisita()!=null && !source.getVisita().isEmpty()) {
				for(Visita v : source.getVisita()) {
					if(v.getDataprel().before(dataIniziale)) {
						dataIniziale=v.getDataprel();
					}
				}
			}

			Protocollo comunicazione = new Protocollo();
			comunicazione.setCreatedBy(this.getClass().getSimpleName()+ulss);
			comunicazione.setCreationDate(new Date());

			ServiceDeliveryLocation asl = findUlss(ulss);
			ServiceDeliveryLocation spisal = null;
			ServiceDeliveryLocation lineaLavoro = null;

			//lineadi lavoro Medicina del lavoro
			String workingLine = "phidic.spisal.pratiche.type.workmedicine";
			lineaLavoro = findLineaDiLavoro(ulss, distretto, workingLine);
			if(lineaLavoro==null){
				return paziente;
			}
			comunicazione.setUos(lineaLavoro);
			if(lineaLavoro.getParent()!=null){
				spisal=lineaLavoro.getParent();
			}
			comunicazione.setServiceDeliveryLocation(spisal);

			//comunicazione di tipo visita ex esposti
			CodeValue tipoCom = getCodeValue("phidic.spisal.segnalazioni.complextype.14.3");
			comunicazione.setCode(tipoCom);

			//paziente in riferito a
			comunicazione.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
			comunicazione.setRiferimentoUtente(paziente);

			comunicazione.setData(dataIniziale);

			comunicazione.setStatusCode(getStatus("status.generic.completed")); //STATO ASSEGNATA
			comunicazione.setIsMaster(true);//COMUNICAZIONE PRINCIPALE

			comunicazione.setNote("Comunicazione importata da SORVES.");

			//salvo la comunicazione
			saveOnTarget(comunicazione);

			Procpratiche pratica = new Procpratiche();
			pratica.setCreatedBy(this.getClass().getSimpleName()+ulss);
			pratica.setCreationDate(new Date());
			pratica.setUoc(spisal);
			pratica.setServiceDeliveryLocation(lineaLavoro);

			pratica.setData(dataIniziale);

			pratica.setStatusCode(getStatus("status.generic.completed"));//pratica chiusa
			pratica.setNote("Pratica importata da SORVES.");
			setNumero(source, pratica);

			Operatore op = getOrCreateSorvesOperatore(asl);
			pratica.setOperatori(new ArrayList<Operatore>());
			pratica.getOperatori().add(op);
			pratica.setProtocollo(new ArrayList<Protocollo>());
			pratica.getProtocollo().add(comunicazione);

			//salvo la comunicazione
			saveOnTarget(pratica);

			comunicazione.setProcpratiche(pratica);
			updateMapping(source, comunicazione, pratica);

			managePratica(pratica, paziente, source);

			manageAttivita(pratica, paziente, source);
		}

		return paziente;
	}

	private void managePratica(Procpratiche pratica, Person paziente, Anagrafica source) {
		MedicinaLavoro mdl = new MedicinaLavoro();
		mdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
		mdl.setCreationDate(new Date());

		mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));//2 Sorveglianza Sanitaria Ex Esposti
		mdl.setPatient(paziente);

		String aFisiologicaFamiliare="";
		if(source.getAnamnesifisiologica()!=null && !source.getAnamnesifisiologica().isEmpty()) {
			aFisiologicaFamiliare+=("ANAMNESI FISIOLOGICA");
			for(Anamnesifisiologica a : source.getAnamnesifisiologica()) {
				aFisiologicaFamiliare+=("\r\n"+a.getDescrizione());
			}
			aFisiologicaFamiliare+="\r\n";
		}
		if(source.getAnamnesifamiliare()!=null && !source.getAnamnesifamiliare().isEmpty()) {
			aFisiologicaFamiliare+=("ANAMNESI FAMILIARE");
			for(Anamnesifamiliare a : source.getAnamnesifamiliare()) {
				aFisiologicaFamiliare+=("\r\n"+a.getDescrizione());
			}
		}
		int lengthFis = aFisiologicaFamiliare.length();
		aFisiologicaFamiliare = aFisiologicaFamiliare.substring(0,lengthFis<=2500?lengthFis:2500);
		mdl.setAnamnesiFisiologica(aFisiologicaFamiliare);

		String aRemota="";
		if(source.getAnamnesiremota()!=null && !source.getAnamnesiremota().isEmpty()) {
			for(Anamnesiremota a : source.getAnamnesiremota()) {
				aRemota+=(a.getDescrizione()+"\r\n");
			}
		}
		int lengthRem = aRemota.length();
		aRemota = aRemota.substring(0,lengthRem<=2500?lengthRem:2500);
		mdl.setAnamnesiPatologica(aRemota);
		if(source.getStatolavorativo()!=null) {
			switch (Long.valueOf(source.getStatolavorativo().getId()).intValue()) {
			case 2:
				mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.pen"));
				break;
			case 3:
				mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.lav"));
				break;
			case 4:
				mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.lav"));
				break;
			case 5:
				mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.lav"));
				break;
			case 6:
				mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.dis"));
				break;
			default:
				break;
			}
		}

		saveOnTarget(mdl);
		pratica.setMedicinaLavoro(mdl);
		saveOnTarget(pratica);

		//PRATICHE RIFERIMENTI
		PraticheRiferimenti refs = pratica.getPraticheRiferimenti();
		if(refs==null){
			refs=new PraticheRiferimenti();
			refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
			refs.setCreationDate(new Date());

		}
		refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
		refs.setRiferimentoUtente(paziente);
		saveOnTarget(refs);
		pratica.setPraticheRiferimenti(refs);
		
		//ANAMNESI LAVORATIVE
		if(source.getAnamnesilavorativa()!=null && !source.getAnamnesilavorativa().isEmpty()) {
			for(Anamnesilavorativa a : source.getAnamnesilavorativa()) {
				AnamnesisMdl amdl = new AnamnesisMdl();
				amdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
				amdl.setCreationDate(new Date());
				amdl.setStartValidity(null);
				amdl.setEndValidity(null);

				Calendar start = Calendar.getInstance();
				start.set(Calendar.MONTH,0);
				start.set(Calendar.DAY_OF_YEAR, 1);
				start.set(Calendar.HOUR,0);
				start.set(Calendar.MINUTE,0);
				start.set(Calendar.MILLISECOND,0);
				Calendar end = Calendar.getInstance();
				end.set(Calendar.MONTH,0);
				end.set(Calendar.DAY_OF_YEAR, 1);
				end.set(Calendar.HOUR,0);
				end.set(Calendar.MINUTE,0);
				end.set(Calendar.MILLISECOND,0);
				
				if(a.getDescrizione()!=null) {
					String descrizione = a.getDescrizione();
					int lenghtDes = descrizione.length();
					descrizione = descrizione.substring(0,lenghtDes<=4000?lenghtDes:4000);
					amdl.setNote(descrizione);
				}
					

				Double expCVM = 0D;
				Double expAMI = 0D;
				saveOnTarget(amdl);

				if(a.getEsposizioni()!=null && !a.getEsposizioni().isEmpty()) {
					for(Esposizione e : a.getEsposizioni()) {
						FattoreRischio f = new FattoreRischio();
						f.setCreatedBy(this.getClass().getSimpleName()+ulss);
						f.setCreationDate(new Date());

						String codice = e.getEsposizionecod().getId()+"";
						if(codice.length()==1)
							codice="00"+codice;
						else if(codice.length()==2)
							codice="0"+codice;
						
						if(e.getAnnoini()!=null) {
							f.setYearStart(e.getAnnoini().intValue());
							start.set(Calendar.YEAR,f.getYearStart());
							if(amdl.getStartValidity()==null) {
								amdl.setStartValidity(start.getTime());
								if(amdl.getEndValidity()==null)
									amdl.setEndValidity(start.getTime());
							}else if(amdl.getStartValidity().after(start.getTime())){
								amdl.setStartValidity(start.getTime());
							}
						}
						if(e.getAnnofin()!=null) {
							f.setYearStop(e.getAnnofin().intValue());
							end.set(Calendar.YEAR,f.getYearStop());
							if(amdl.getEndValidity().before(end.getTime())){
								amdl.setEndValidity(end.getTime());
							}
						}

						f.setType(getCodeValue("phidic.spisal.segnalazioni.workdisease.frtype.1"));//LAV
						if(e.getEsposizionecod()!=null && e.getEsposizionecod().getTabcancerogeni()!=null) {
							Long tipo = e.getEsposizionecod().getTabcancerogeni().getId();
							if(Long.valueOf(1).equals(tipo)) {
								f.setExpType(getCodeValue("phidic.spisal.mdl.exptype.cvm"));//CVM
								f.setCoefficient(getCodeValue("phidic.spisal.mdl.exptype.cvm."+codice));//reparto/mansione
								f.setUnitaMisura(getCodeValue("phidic.spisal.pratiche.um.ppm"));//unita
								if(f.getCoefficient()!=null) {
									Query qMult = targetEm.createQuery("SELECT c.multiplier FROM ExpositionCoefficients c WHERE c.isActive=1 AND c.code='"+f.getCoefficient().getId()+"' AND c.multiplier IS NOT NULL");
									List<Double> mults = qMult.getResultList();
									if(mults!=null && !mults.isEmpty())
										f.setMultiplier(mults.get(0));
								}


								if(f!=null && f.getYearStart()!=null && f.getYearStop()!=null && f.getMultiplier()!=null && f.getExpType()!=null){
									int difference = f.getYearStop()-f.getYearStart()+1;
									Double intensity = difference*f.getMultiplier();
									f.setIntensityQuant(intensity);
									expCVM+=f.getIntensityQuant();
								}
							}else if(Long.valueOf(2).equals(tipo)) {
								f.setExpType(getCodeValue("phidic.spisal.mdl.exptype.ami"));//ASBESTO
								if(e.getEsposizionecod()!=null)
									f.setNote("SOTTOGRUPPO "+e.getEsposizionecod().getSottogruppo());
								//non si può valorizzare altro qui
							}else if(Long.valueOf(3).equals(tipo)) {
								f.setCode(getCodeValue("phidic.spisal.frisk.agmal.i.1.44"));//AMMINE AROMATICHE
							}else if(Long.valueOf(26).equals(tipo)) {
								f.setCode(getCodeValue("phidic.spisal.frisk.agmal.i.1.15"));//URANIO IMPOVERITO
							}
						}
						
						f.setAnamnesisMdl(amdl);
						saveOnTarget(f);
						amdl.addFattoreRischio(f);
					}
				}
				
				amdl.setExpCVM(expCVM);
				amdl.setMedicinaLavoro(mdl);
				saveOnTarget(amdl);
				mdl.addAnamnesisMdl(amdl);
			}

		}
	}

	private void manageAttivita(Procpratiche pratica, Person paziente, Anagrafica source) {
		//visita medica c/o SPISAL
		if(source.getVisita()!=null && !source.getVisita().isEmpty()) {

			for(Visita v : source.getVisita()) {
				Attivita a = new Attivita();
				a.setCreatedBy(this.getClass().getSimpleName()+ulss);
				a.setCreationDate(new Date());
				a.setDataInizio(v.getDataprel());
				a.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.visitamedica"));
				a.setStatusCode(getCodeValue("phidic.spisal.pratiche.activities.status.completed")); //STATO CONCLUSA
				a.setSoggetto(new ArrayList<Soggetto>());
				a.setProcpratiche(pratica);
				saveOnTarget(a);
				pratica.addAttivita(a);

				Soggetto s = new Soggetto();
				s.setCreatedBy(this.getClass().getSimpleName()+ulss);
				s.setCreationDate(new Date());
				s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
				s.setRuolo(getCodeValue("phidic.spisal.pratiche.activities.roles.paziente"));

				s.setUtente(paziente);
				s.setAttivita(a);
				saveOnTarget(s);
				a.addSoggetto(s);


				VisitaMdl vmdl = new VisitaMdl();
				vmdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
				vmdl.setCreationDate(new Date());
				vmdl.setCode(getCodeValue("phidic.spisal.mdl.subtype.01"));
				vmdl.setAttivita(a);
				saveOnTarget(vmdl);
				a.addVisitaMdl(vmdl);

				VisitaSp vsp = new VisitaSp();
				vsp.setCreatedBy(this.getClass().getSimpleName()+ulss);
				vsp.setCreationDate(new Date());
				String anamnesi = "";
				if(v.getNoteanamnesi()!=null && !v.getNoteanamnesi().isEmpty())
					anamnesi+=(v.getNoteanamnesi()+"\r\n");
				if(v.getNoteabvoluttuarie()!=null && !v.getNoteabvoluttuarie().isEmpty()) {
					anamnesi+=(v.getNoteabvoluttuarie());
				}
				int lenghtAn = anamnesi.length();
				anamnesi = anamnesi.substring(0,lenghtAn<=2000?lenghtAn:2000);
				vsp.setAnamnesiProssima(anamnesi);


				String esame = "";
				if(v.getNoteesameobiettivo()!=null && !v.getNoteesameobiettivo().isEmpty())
					esame+=(v.getNoteesameobiettivo()+"\r\n");

				int lenghtEs = esame.length();
				esame = esame.substring(0,lenghtEs<=255?lenghtEs:255);
				vsp.setNote(esame);

				if(v.getAltezza()!=null)
					vsp.setAltezza(v.getAltezza().toPlainString());

				if(v.getPeso()!=null)
					vsp.setPeso(v.getPeso().toPlainString());

				if(v.getPamax()!=null)
					vsp.setPaMax(v.getPamax().toPlainString());

				if(v.getPamin()!=null)
					vsp.setPaMin(v.getPamin().toPlainString());

				if(v.getFreqcard()!=null)
					vsp.setHr(v.getFreqcard().toPlainString());

				saveOnTarget(vsp);
				vmdl.setVisitaSp(vsp);
				vsp.addVisitaMdl(vmdl);
				saveOnTarget(vmdl);

				if(v.getTabfumo()!=null && (v.getTabfumo().getId()==1 || v.getTabfumo().getId()==5)) {
					//sigaretta
					
					String note = "";
					if(v.getNoteabvoluttuarie()!=null)
						note=v.getNoteabvoluttuarie();
					int lenghtNote = note.length();
					note = note.substring(0,lenghtNote<=255?lenghtNote:255);
					
					boolean foundSimilar = false;
					if(pratica.getMedicinaLavoro()!=null && pratica.getMedicinaLavoro().getFattoreRischio()!=null) {
						for(FattoreRischio pratFatt : pratica.getMedicinaLavoro().getFattoreRischio()) {
							if(pratFatt.getExt()!=null && "phidic.spisal.segnalazioni.workdisease.frext.06".equals(pratFatt.getExt().getOid()) && note.equals(pratFatt.getNote())) {
								foundSimilar=true;
								break;
							}
						}
					}

					if(!foundSimilar) {
						FattoreRischio f = new FattoreRischio();
						f.setCreatedBy(this.getClass().getSimpleName()+ulss);
						f.setCreationDate(new Date());


						f.setType(getCodeValue("phidic.spisal.segnalazioni.workdisease.frtype.2"));//EXTRA
						f.setExt(getCodeValue("phidic.spisal.segnalazioni.workdisease.frext.06"));//SIGARETTA
						if(v.getTabfumo().getId()==1 && v.getFumoqta()!=null)
							f.setSigarette(v.getFumoqta().intValue());
						else if(v.getTabfumo().getId()==5)
							f.setSigarette(0);

						f.setNote(note);
						f.setStartValidity(v.getDataprel());
						if(pratica.getMedicinaLavoro()!=null && pratica.getMedicinaLavoro().getFattoreRischio()!=null) {
							for(FattoreRischio pratFatt : pratica.getMedicinaLavoro().getFattoreRischio()) {
								if(pratFatt.getExt()!=null && "phidic.spisal.segnalazioni.workdisease.frext.06".equals(pratFatt.getExt().getOid()) && pratFatt.getEndValidity()==null) {
									pratFatt.setEndValidity(v.getDataprel());
									break;
								}
							}
						}
						
						f.setMedicinaLavoro(pratica.getMedicinaLavoro());
						saveOnTarget(f);
						pratica.getMedicinaLavoro().addFattoreRischio(f);
					}
				}	

				if(v.getAlcolqta()!=null && v.getAlcolqta().intValue()>0) {
					//alcol
					
					String note = "";
					if(v.getNoteabvoluttuarie()!=null)
						note=v.getNoteabvoluttuarie();
					
					note+="\r\nQUANTITA': "+v.getAlcolqta().toPlainString();
					int lenghtNote = note.length();
					note = note.substring(0,lenghtNote<=255?lenghtNote:255);
					
					boolean foundSimilar = false;
					if(pratica.getMedicinaLavoro()!=null && pratica.getMedicinaLavoro().getFattoreRischio()!=null) {
						for(FattoreRischio pratFatt : pratica.getMedicinaLavoro().getFattoreRischio()) {
							if(pratFatt.getExt()!=null && "phidic.spisal.segnalazioni.workdisease.frext.03".equals(pratFatt.getExt().getOid()) && note.equals(pratFatt.getNote())) {
								foundSimilar=true;
								break;
							}
						}
					}

					if(!foundSimilar) {
						FattoreRischio f = new FattoreRischio();
						f.setCreatedBy(this.getClass().getSimpleName()+ulss);
						f.setCreationDate(new Date());


						f.setType(getCodeValue("phidic.spisal.segnalazioni.workdisease.frtype.2"));//EXTRA
						f.setExt(getCodeValue("phidic.spisal.segnalazioni.workdisease.frext.03"));//ALCOL					

						f.setNote(note);
						f.setStartValidity(v.getDataprel());
						if(pratica.getMedicinaLavoro()!=null && pratica.getMedicinaLavoro().getFattoreRischio()!=null) {
							for(FattoreRischio pratFatt : pratica.getMedicinaLavoro().getFattoreRischio()) {
								if(pratFatt.getExt()!=null && "phidic.spisal.segnalazioni.workdisease.frext.03".equals(pratFatt.getExt().getOid()) && pratFatt.getEndValidity()==null) {
									pratFatt.setEndValidity(v.getDataprel());
									break;
								}
							}
						}
						
						f.setMedicinaLavoro(pratica.getMedicinaLavoro());
						saveOnTarget(f);
						pratica.getMedicinaLavoro().addFattoreRischio(f);
					}
				}	
				
				//Conclusioni
				if(v.getTabobiettivita()!=null && !v.getTabobiettivita().isEmpty()) {
					ConclusioniMdl c = new ConclusioniMdl();
					c.setCreatedBy(this.getClass().getSimpleName()+ulss);
					c.setCreationDate(new Date());
					String descrizione = "";
					for(Tabobiettivita tab : v.getTabobiettivita()) {
						if(tab.getDescrizione()!=null) {
							descrizione+=tab.getDescrizione();
						}
						if(tab.getSnomed()!=null) {
							descrizione+=(" (SNOMED: "+tab.getSnomed()+")");
						}
						descrizione+="\r\n";
						
					}
					int lenghtDesc = descrizione.length();
					descrizione = descrizione.substring(0,lenghtDesc<=255?lenghtDesc:255);
					c.setDiagnosiTxt(descrizione);
					
					saveOnTarget(c);
					c.addVisitaMdl(vmdl);
					vmdl.setConclusioniMdl(c);
					saveOnTarget(vmdl);
				}
			}
		}
		//Accertamento ecografia
		if(source.getEcografia()!=null && !source.getEcografia().isEmpty()) {
			for(Ecografia eco : source.getEcografia()) {
				CodeValue icd9=null;
				if(eco.getTipo()!=null) {
					icd9 = getMappedCode(eco.getTipo().getId()+"", "tabesameecografico", ulss);
				}

				String descrizione="";
				if(eco.getTipo()!=null)
					descrizione+=("TIPO ECOGRAFIA: "+eco.getTipo().getDescrizione()+"\r\n");
				if(eco.getTabdiagnosiecografica()!=null) {
					descrizione+="DIAGNOSI: ";
					for(Tabdiagnosiecografica d : eco.getTabdiagnosiecografica()) {
						descrizione+=d.getDescrizione()+"\r\n";
					}
				}
				
				if(eco.getDescrizione()!=null)
					descrizione+=("NOTE: "+eco.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);
				
				createAccertamento(pratica, eco.getDataprel(), paziente, icd9, descrizione);
			}
		}
		//Esame altro
		if(source.getEsamealtro()!=null && !source.getEsamealtro().isEmpty()) {
			for(Esamealtro altro : source.getEsamealtro()) {
				CodeValue icd9=null;
				if(altro.getTipo()!=null) {
					icd9 = getMappedCode(altro.getTipo().getId()+"", "tabesamealtroesame", ulss);
				}

				String descrizione="";
				if(altro.getTipo()!=null)
					descrizione+=("TIPO ESAME: "+altro.getTipo().getDescrizione()+"\r\n");
				if(altro.getTabdiagnosialtroesame()!=null) {
					descrizione+="DIAGNOSI: ";
					for(Tabdiagnosialtroesame d : altro.getTabdiagnosialtroesame()) {
						descrizione+=d.getDescrizione()+"\r\n";
					}
				}
				
				if(altro.getDescrizione()!=null)
					descrizione+=("NOTE: "+altro.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);
				
				createAccertamento(pratica, altro.getDataprel(), paziente, icd9, descrizione);

			}
		}
		//Esame istologico
		if(source.getEsameistologico()!=null && !source.getEsameistologico().isEmpty()) {
			for(Esameistologico isto : source.getEsameistologico()) {
				CodeValue icd9=null;
				if(isto.getTipo()!=null) {
					icd9 = getMappedCode(isto.getTipo().getId()+"", "tabesameistologico", ulss);
				}

				String descrizione="";
				if(isto.getTipo()!=null)
					descrizione+=("TIPO ESAME: "+isto.getTipo().getDescrizione()+"\r\n");
				if(isto.getTabdiagnosiistologica()!=null) {
					descrizione+="DIAGNOSI: ";
					for(Tabdiagnosiistologica d : isto.getTabdiagnosiistologica()) {
						descrizione+=d.getDescrizione()+"\r\n";
					}
				}
				
				if(isto.getDescrizione()!=null)
					descrizione+=("NOTE: "+isto.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);
				
				createAccertamento(pratica, isto.getDataprel(), paziente, icd9, descrizione);
			}
		}
		//Esame radiologico
		if(source.getRadiografia()!=null && !source.getRadiografia().isEmpty()) {
			for(Radiografia radio : source.getRadiografia()) {
				CodeValue icd9=null;
				if(radio.getTipo()!=null) {
					icd9 = getMappedCode(radio.getTipo().getId()+"", "tabesameradiografico", ulss);
				}
				
				String descrizione="";
				if(radio.getTipo()!=null)
					descrizione+=("TIPO RADIOGRAFIA: "+radio.getTipo().getDescrizione()+"\r\n");
				if(radio.getTabdiagnosiradiografica()!=null) {
					descrizione+="DIAGNOSI: ";
					for(Tabdiagnosiradiografica d : radio.getTabdiagnosiradiografica()) {
						descrizione+=d.getDescrizione()+"\r\n";
					}
				}
				if(radio.getDescrizione()!=null)
					descrizione+=("NOTE: "+radio.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);

				createAccertamento(pratica, radio.getDataprel(), paziente, icd9, descrizione);
			}
		}
		//Esame laboratorio
		if(source.getEsamilaboratorio()!=null && !source.getEsamilaboratorio().isEmpty()) {
			for(Esamilaboratorio lab : source.getEsamilaboratorio()) {
				CodeValue icd9=null;
				if(lab.getTipo()!=null) {
					icd9 = getMappedCode(lab.getTipo().getId()+"", "tabesamelaboratorio", ulss);
				}

				String descrizione="";
				if(lab.getTipo()!=null)
					descrizione+=("TIPO ESAME: "+lab.getTipo().getDescrizione()+"\r\n");
				if(lab.getEsamilaboratoriocod()!=null) {
					
					for(Esamilaboratoriocod ris : lab.getEsamilaboratoriocod()) {
						if(ris.getTabesami()!=null) {
							descrizione+=("ESAME: "+ris.getTabesami().getDescrizione()+
											" - RISULTATO: "+ris.getRisultato()+
											" - ESITO: "+ris.getEsito()+
											" - UNITA: "+ris.getTabesami().getUnita()+"\r\n");
						}
					}
				}
				if(lab.getDescrizione()!=null)
					descrizione+=("NOTE: "+lab.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);

				createAccertamento(pratica, lab.getDataprel(), paziente, icd9, descrizione);

			}
		}
		//Spirometria
		if(source.getSpirometria()!=null && !source.getSpirometria().isEmpty()) {
			for(Spirometria spiro : source.getSpirometria()) {
				CodeValue icd9=null;
				icd9 = getCodeValue("phidic.spisal.pratiche.presmp.892");
				String descrizione="";
				if(spiro.getTipo()!=null)
					descrizione+=("ESITO: "+spiro.getTipo().getDescrizione()+"\r\n");
				if(spiro.getDescrizione()!=null)
					descrizione+=("NOTE: "+spiro.getDescrizione());
				
				int lenghtDesc = descrizione.length();
				descrizione = descrizione.substring(0,lenghtDesc<=4000?lenghtDesc:4000);
				
				createAccertamento(pratica, spiro.getDataprel(), paziente, icd9, descrizione);
			}
		}
	}

	private void createAccertamento(Procpratiche pratica, Date dataprel, Person paziente, CodeValue icd9, String descrizione) {
		Attivita a = new Attivita();
		a.setCreatedBy(this.getClass().getSimpleName()+ulss);
		a.setCreationDate(new Date());
		a.setDataInizio(dataprel);
		a.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.accertamento"));
		a.setStatusCode(getCodeValue("phidic.spisal.pratiche.activities.status.completed")); //STATO CONCLUSA
		if(descrizione!=null) {
			int lenghtDes = descrizione.length();
			descrizione = descrizione.substring(0,lenghtDes<=4000?lenghtDes:4000);
			a.setNote(descrizione);
		}
		
		
		a.setSoggetto(new ArrayList<Soggetto>());
		a.setProcpratiche(pratica);
		saveOnTarget(a);
		pratica.addAttivita(a);

		Soggetto s = new Soggetto();
		s.setCreatedBy(this.getClass().getSimpleName()+ulss);
		s.setCreationDate(new Date());
		s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
		s.setRuolo(getCodeValue("phidic.spisal.pratiche.activities.roles.paziente"));

		s.setUtente(paziente);
		s.setAttivita(a);
		saveOnTarget(s);
		a.addSoggetto(s);


		AccertaMdl amdl = new AccertaMdl();
		amdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
		amdl.setCreationDate(new Date());
		amdl.setCode(getCodeValue("phidic.spisal.mdl.accsubtype.01"));
		amdl.setAttivita(a);
		saveOnTarget(amdl);
		a.addAccertaMdl(amdl);

		AccertSp asp = new AccertSp();
		asp.setCreatedBy(this.getClass().getSimpleName()+ulss);
		asp.setCreationDate(new Date());

		saveOnTarget(asp);
		amdl.setAccertSp(asp);
		asp.addAccertaMdl(amdl);
		saveOnTarget(amdl);

		if(icd9!=null) {
			PrestMdl prest = new PrestMdl();
			prest.setCreatedBy(this.getClass().getSimpleName()+ulss);
			prest.setCreationDate(new Date());
			prest.setPrest((CodeValuePhi) icd9);
			prest.setDataReferto(a.getDataInizio());
			prest.setOperatore(getOrCreateSorvesOperatore(pratica.getUoc().getParent()));

			prest.setAccertaMdl(amdl);
			saveOnTarget(prest);
			amdl.addPrestMdl(prest);
		}
	}

	private Person importNewPerson(Anagrafica source, Person target){
		target.setCreatedBy(this.getClass().getSimpleName()+ulss);
		target.setCreationDate(source.getDatanascita()==null?new Date():source.getDatanascita());


		target.setFiscalCode(source.getCodicefiscale());
		target.setCs(source.getTesserasanitaria());

		if(source.getStatovita()!=null && source.getStatovita().intValue()==0){
			target.setDeathIndicator(true);
			target.setDeathDate(source.getDatadecesso());
		}

		if(target.getName()==null)
			target.setName(new EN());

		target.getName().setFam(source.getCognome());
		target.getName().setGiv(source.getNome());
		target.setGenderCode(getCodeValue("phidic.generic.gender."+source.getSesso()));

		if(target.getTelecom()==null)
			target.setTelecom(new TEL());


		//in sorves salvano il nr cellulare nel campo fax
		if(source.getFax()!=null)
			target.getTelecom().setHp(source.getFax().trim());

		//telefono domicilio
		if(source.getTelefono()!=null)
			target.getTelecom().setMc(source.getTelefono().trim());

		target.getTelecom().setMail(source.getEmail());

		if(target.getAddr()==null)
			target.setAddr(new AD());

		AD residenza = target.getAddr();

		if(source.getFrazione()!=null)
			residenza.setAdl(source.getFrazione());

		String indirizzo = "";
		if(source.getTipovia()!=null)
			indirizzo+=(source.getTipovia()+" ");

		indirizzo+=source.getIndirizzo();
		residenza.setStr(indirizzo);
		residenza.setBnr(source.getCivico());
		residenza.setCode(getComune(source.getComuneresidenza()));
		if(residenza.getCode()!=null){
			residenza.setCpa(residenza.getCode().getProvince());
			residenza.setZip(residenza.getCode().getZip());
			residenza.setCty(residenza.getCode().getCurrentTranslation());
		}else if(source.getComuneresidenza()!=null){
			Comuni com = source.getComuneresidenza();
			if(com.getProvincia()!=null)
				residenza.setCpa(com.getProvincia());

			residenza.setZip(com.getCap());
			residenza.setCty(com.getDescrizione());
		}

		//copio nel contatto spisal la residenza
		if(target.getAlternativeAddr()==null)
			target.setAlternativeAddr(new SpisalAddr());

		target.getAlternativeAddr().setAddr(residenza.cloneAd());

		target.setBirthTime(source.getDatanascita());
		if(target.getBirthPlace()==null)
			target.setBirthPlace(new AD());

		manageAddrComune(target.getBirthPlace(), source.getComunenascita());		

		if(target.getDomicileAddr()==null)
			target.setDomicileAddr(new AD());

		if(source.getComuneresidenza()!=null) {
			String codusl = source.getComuneresidenza().getCodusl().trim();
			String codreg =  source.getComuneresidenza().getCodreg().trim();
			if(codusl.length()==1)
				codusl="10"+codusl;
			else if(codusl.length()==2)
				codusl="1"+codusl;
			if(codreg.length()==1)
				codreg="00"+codreg;
			else if(codreg.length()==2)
				codreg="0"+codreg;

			String org = codreg+codusl;
			target.setCurrentOrg(findOrganization(org));
		}



		saveOnTarget(target);
		saveMapping(source, target);

		return target;
	}

	/**
	 * Data una Person di esempio,
	 * cerco tutte le altre Person che rappresentano la stessa Person di esempio (stesso nome,cognome,cf,data e luogo di nascita) 
	 * ma storiche (isActive = 0): assieme a example formano lo storico della Person e solo la più recente avrà isActive = true
	 * @param example
	 */
	private void setIsActiveOnHistoricVersions(Person example){
		if(example==null || example.getName()==null)
			return;

		String code = null;
		if(example.getBirthPlace()!=null && example.getBirthPlace().getCode()!=null){
			code = example.getBirthPlace().getCode().getCode();
		}

		queryPerson.setParameter("nome", example.getName().getGiv());
		queryPerson.setParameter("cognome", example.getName().getFam());
		queryPerson.setParameter("dataNascita", example.getBirthTime());
		queryPerson.setParameter("istatNascita", code);
		queryPerson.setParameter("cf", example.getFiscalCode());
		queryPerson.setParameter("isActive", false);

		List<Person> list = queryPerson.getResultList();

		if(list!=null && !list.isEmpty()){
			Person last = list.get(0);
			if(last.getCreationDate().after(example.getCreationDate())){
				last.setIsActive(true);
				example.setIsActive(false);

				if(commit){
					targetEm.getTransaction().begin();
					targetEm.persist(last);
					targetEm.persist(example);
					targetEm.flush();
					targetEm.getTransaction().commit();
				}
			}
		}
	}

	/**
	 * Controlla se l'entità sia già stata inserita in Anagrafica locale. Se sì ritorna la riga corrispondente.
	 * @param source
	 * @return
	 */
	private Person checkAnagrafica(Anagrafica source){
		if(source==null || source.getCodicefiscale()==null)
			return null;

		String istat = null;
		if(source.getComunenascita()!=null){
			istat = source.getComunenascita().getIstat().trim();
			istat = df.format(Integer.parseInt(istat));
		}

		queryPerson.setParameter("nome", source.getNome());
		queryPerson.setParameter("cognome", source.getCognome());
		queryPerson.setParameter("dataNascita", source.getDatanascita());
		queryPerson.setParameter("istatNascita", istat);
		queryPerson.setParameter("cf", source.getCodicefiscale());
		queryPerson.setParameter("isActive", true);

		List<Person> list = queryPerson.getResultList();
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}

		return null;
	}

	/* Formato: CodiceUSL_Anno_AbbreviazioneLineaDiLavoro_Progressivo */
	private void setNumero(Anagrafica source, Procpratiche procpratiche){
		try{			
			
			DecimalFormat df = new DecimalFormat("00000");
			String nr = df.format(source.getId());
			
			String wlCode = "AML_SRV";
			
			if(procpratiche.getData()!=null){
				Date date = procpratiche.getData();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				String numero = ulss+"_"+cal.get(Calendar.YEAR)+"_"+wlCode+"_"+nr;
				procpratiche.setNumero(numero);
			}else {
				String numero = ulss+"_????_"+wlCode+"_"+nr;
				procpratiche.setNumero(numero);
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Controlla se l'entità id sia già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		String hqlMapping = "SELECT m FROM MapPazienti m WHERE m.idsorves = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPazienti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPazienti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdsorves()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	/**
	 * Controlla se l'entità id sia già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapCommPrat(long id){
		String hqlMapping = "SELECT m FROM MapPazienti m WHERE m.idsorves = :id AND m.commId IS NOT NULL AND m.praticaId IS NOT NULL";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPazienti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPazienti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdsorves()+". "+
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
	private Person getMapped(long id){
		String hqlMapping = "SELECT m FROM MapPazienti m WHERE m.idsorves = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPazienti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPazienti map = list.get(0);
			Query qPerson = targetEm.createQuery("SELECT p FROM Person p WHERE p.internalId = :id");
			qPerson.setParameter("id", map.getIdphi());
			List<Person> lp = qPerson.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(Anagrafica source, Person target){
		MapPazienti map = new MapPazienti();
		map.setIdsorves(source.getId());
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);

		saveOnSource(map);

		thislog.info("New imported object. Source id: "+map.getIdsorves()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}

	private void updateMapping(Anagrafica source, Protocollo comunicazione, Procpratiche pratica){
		String hqlMapping = "SELECT m FROM MapPazienti m WHERE m.idsorves = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", source.getId());
		List<MapPazienti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPazienti map = list.get(0);
			map.setCommId(comunicazione.getInternalId());
			map.setPraticaId(pratica.getInternalId());
			saveOnSource(map);
		}
	}

	@Override
	protected void deleteImportedData(String ulss) {
		// TODO Auto-generated method stub

	}

	private Integer evaluateNextValue(Procpratiche procpratiche, ServiceDeliveryLocation uos){
		try{
			if (uos==null)
				return null;

			String number = "050_%";
			String evaluateNumber = "select max(numero) from Procpratiche pp " +
					"where pp.serviceDeliveryLocation.internalId =:internalID and pp.statusCode is not null and pp.data  >= :minDate and pp.data <= :maxDate and pp.numero like :number";

			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(procpratiche.getData());
			cal1.set(Calendar.MONTH,0);
			cal1.set(Calendar.DAY_OF_YEAR, 1);
			cal1.set(Calendar.HOUR,0);
			cal1.set(Calendar.MINUTE,0);
			cal1.set(Calendar.MILLISECOND,0);

			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(procpratiche.getData());
			cal2.set(Calendar.MONTH,11);
			cal2.set(Calendar.DAY_OF_MONTH, 31);
			cal2.set(Calendar.HOUR,23);
			cal2.set(Calendar.MINUTE,59);
			cal2.set(Calendar.MILLISECOND,99);

			Date minDate = cal1.getTime();
			Date maxDate = cal2.getTime();
			Query q = targetEm.createQuery(evaluateNumber);
			q.setParameter("internalID", uos.getInternalId());
			q.setParameter("minDate", minDate);
			q.setParameter("maxDate", maxDate);
			q.setParameter("number", number);

			List<String> ppList = (List<String>) q.getResultList();

			Integer ret = 1;
			String numero = ppList.get(0);

			if (numero != null) {
				String[] parts = numero.split("_");
				if (parts.length>=4)
					ret = Integer.parseInt(parts[parts.length-1])+1;
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}

	private Operatore getOrCreateSorvesOperatore(ServiceDeliveryLocation asl) {
		Operatore op = null;
		Query q = targetEm.createQuery("SELECT op FROM Operatore op WHERE "
				+ "op.code = 'phidic.spisal.pratiche.nomina.amministrativo_V0' AND "
				+ "op.name.giv='COMPILATORE' AND op.name.fam='SORVES'");

		List<Operatore> opList = q.getResultList();
		if(opList!=null && !opList.isEmpty()) {
			op = opList.get(0);

		}else {
			op = new Operatore();

			op.setCreatedBy(this.getClass().getSimpleName()+ulss);
			op.setCreationDate(new Date());

			if(op.getName()==null)
				op.setName(new EN());

			op.getName().setGiv("COMPILATORE");
			op.getName().setFam("SORVES");

			op.setCode(getCodeValue("phidic.spisal.pratiche.nomina.amministrativo"));//PERSONALE AMMINISTRATIVO

			op.setServiceDeliveryLocation(asl);
			op.setEnte(getCodeValue("phidic.spisal.pratiche.ente.spisal"));//SPISAL

			saveOnTarget(op);
		}


		return op;
	}
}
