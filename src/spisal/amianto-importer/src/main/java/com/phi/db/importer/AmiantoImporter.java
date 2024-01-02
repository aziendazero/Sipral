package com.phi.db.importer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.amianto.entities.Anagrafica;
import com.amianto.entities.Dlco;
import com.amianto.entities.EsameIstologico;
import com.amianto.entities.EsameSpirometrico;
import com.amianto.entities.EsposizioneProfessionale;
import com.amianto.entities.IndagineMalattiaProfessionale;
import com.amianto.entities.NoduloPolmonare;
import com.amianto.entities.PrimaVisita;
import com.amianto.entities.RadiografiaTorace;
import com.amianto.entities.Tac;
import com.amianto.entities.Visita;
import com.amianto.entities.VisitaGenerica;
import com.amianto.entities.VisitaSpecialistica;
import com.amianto.mappings.MapPazienti;
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

@SuppressWarnings({"unchecked"})
public class AmiantoImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(AmiantoImporter.class.getName());
	private Query queryPerson;
	private static AmiantoImporter instance = null;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public static AmiantoImporter getInstance() {
		if(instance == null) {
			instance = new AmiantoImporter();
		}
		return instance;
	}

	public AmiantoImporter() {
		super();

		queryPerson = targetEm.createQuery("SELECT p FROM Person p LEFT JOIN p.birthPlace.code bp WHERE "
				+ "p.name.giv = :nome AND "
				+ "p.name.fam = :cognome AND "
				+ "(p.birthTime = :dataNascita OR p.birthTime IS NULL) AND "
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
			if(source.getDataCompilazioneQuestionario()!=null)
				dataIniziale=source.getDataCompilazioneQuestionario();

			if(source.getVisitaMedica()!=null && source.getVisitaMedica().getData()!=null) {
				if(source.getVisitaMedica().getData().before(dataIniziale)) {
					dataIniziale=source.getVisitaMedica().getData();
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

			comunicazione.setNote("Comunicazione importata da SORVES-AMIANTO.");
			if(source.getTipo()!=null) {
				if(source.getTipo().getId()==1) {
					comunicazione.setApplicant(getCodeValue("phidic.spisal.segnalazioni.applicant.sindacati"));
				}else if(source.getTipo().getId()==2) {
					comunicazione.setApplicant(getCodeValue("phidic.spisal.segnalazioni.applicant.esposto"));
					comunicazione.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
					comunicazione.setRichiedenteUtente(paziente);
				}else if(source.getTipo().getId()==3) {
					comunicazione.setApplicant(getCodeValue("phidic.spisal.segnalazioni.applicant.spisal"));
				}
			}


			//salvo la comunicazione
			saveOnTarget(comunicazione);

			Procpratiche pratica = new Procpratiche();
			pratica.setCreatedBy(this.getClass().getSimpleName()+ulss);
			pratica.setCreationDate(new Date());
			pratica.setUoc(spisal);
			pratica.setServiceDeliveryLocation(lineaLavoro);

			pratica.setData(dataIniziale);

			pratica.setStatusCode(getStatus("status.generic.completed"));//pratica chiusa
			String note = "Pratica importata da SORVES-AMIANTO.";
			if(source.getConsegnaEsenzione()!=null) {
				note+=("\r\nDATA CONSEGNA ESENZIONE TICKET: "+sdf.format(source.getConsegnaEsenzione()));
			}
			pratica.setNote(note);
			setNumero(source, pratica);

			Operatore op = getOrCreateAmiantoOperatore(asl, source.getOperatoreSanitario());
			if(op!=null) {
				pratica.setOperatori(new ArrayList<Operatore>());
				pratica.getOperatori().add(op);
			}

			pratica.setProtocollo(new ArrayList<Protocollo>());
			pratica.getProtocollo().add(comunicazione);

			//salvo la comunicazione
			saveOnTarget(pratica);

			comunicazione.setProcpratiche(pratica);
			updateMapping(source, comunicazione, pratica);

			managePratica(pratica, paziente, source);

			//manageAttivita(pratica, paziente, source);
		}

		return paziente;
	}

	private Person importNewPerson(Anagrafica source, Person target){
		target.setCreatedBy(this.getClass().getSimpleName()+ulss);
		target.setCreationDate(source.getDataNascita()==null?new Date():source.getDataNascita());


		target.setFiscalCode(source.getCodiceFiscale());
		target.setCs(source.getTesseraSanitaria());

		if(source.getDataMorte()!=null){
			target.setDeathIndicator(true);
			target.setDeathDate(source.getDataMorte());
		}

		if(target.getName()==null)
			target.setName(new EN());

		String nome = "";
		String cognome = "";
		String cn = source.getCognomeENome();
		//approccio stupido, ma da una select distinct funzionerà sempre
		if(cn!=null) {
			if(cn.startsWith("D'") || cn.startsWith("DA ") || cn.startsWith("DAL ") || cn.startsWith("DALL'") ||
					cn.startsWith("DALLA ") || cn.startsWith("DE ") || cn.startsWith("DEI ") || cn.startsWith("DEL ") ||
					cn.startsWith("DELLA ") || cn.startsWith("DI ") || cn.startsWith("FACCHIN - MORETTO") || cn.startsWith("GRIZ BITTADOR ") ||
					cn.startsWith("LA ") || cn.startsWith("MAZZA BALESTRIERI ") || cn.startsWith("POLO DEL VECCHIO ") || 
					cn.startsWith("SALGARO VACCARO ") || cn.startsWith("SANTON RALLO ")) {
				cognome=cn.substring(0, cn.lastIndexOf(" "));
				nome=cn.substring(cn.lastIndexOf(" ")+1);
			}else {
				cognome=cn.substring(0, cn.indexOf(" "));
				nome=cn.substring(cn.indexOf(" ")+1);
			}
		}

		target.getName().setFam(cognome);
		target.getName().setGiv(nome);
		if(Integer.valueOf(1).equals(source.getSesso())) {
			target.setGenderCode(getCodeValue("phidic.generic.gender.M"));
		}else if(Integer.valueOf(12).equals(source.getSesso())) {
			target.setGenderCode(getCodeValue("phidic.generic.gender.F"));
		}		

		if(target.getTelecom()==null)
			target.setTelecom(new TEL());

		//telefono domicilio
		if(source.getTelefono()!=null)
			target.getTelecom().setMc(source.getTelefono().trim());

		if(target.getAddr()==null)
			target.setAddr(new AD());

		AD residenza = target.getAddr();
		TEL telefoni = target.getTelecom();

		String indirizzo=source.getIndirizzo();
		residenza.setStr(indirizzo);
		residenza.setZip(source.getCitta());
		residenza.setCty(source.getCap());

		//copio nel contatto spisal la residenza
		if(target.getAlternativeAddr()==null)
			target.setAlternativeAddr(new SpisalAddr());

		target.getAlternativeAddr().setAddr(residenza.cloneAd());
		target.getAlternativeAddr().setTelecom(telefoni.cloneTel());

		target.setBirthTime(source.getDataNascita());

		if(target.getDomicileAddr()==null)
			target.setDomicileAddr(new AD());

		target.setCurrentOrg(findOrganization(ulss));//solo comuni del veneziano

		saveOnTarget(target);
		saveMapping(source, target);

		return target;
	}

	public void fixPratica(Procpratiche pratica, Person paziente, Anagrafica source) {
		MedicinaLavoro mdl = pratica.getMedicinaLavoro();

		//FATTORI DI RISCHIO EXTRA LAVORATIVI
		manageFattoriRischioExtra(source, mdl);

		//PRIMA VISITA E SUCCESSIVI FOLLOWUP
		manageVisite(source, mdl, pratica);

		//ANAMNESI PATOLOGICA REMOTA
		if(source.getIndagineMalattiaProfessionale()!=null && !source.getIndagineMalattiaProfessionale().isEmpty()) {
			for(IndagineMalattiaProfessionale i : source.getIndagineMalattiaProfessionale()) {
				if(i.getMp()!=null) {
					String anamnesiRemota = mdl.getAnamnesiPatologica();
					anamnesiRemota+=("\r\nM.P. "+i.getMp());
					if(i.getDataSegnalazione()!=null) {
						anamnesiRemota+=(" segnalata il "+i.getDataSegnalazione());
					}
					if(i.getStrutturaCheSegnala()!=null) {
						anamnesiRemota+=(" da "+i.getStrutturaCheSegnala());
					}
					int lengthRem = anamnesiRemota.length();
					anamnesiRemota = anamnesiRemota.substring(0,lengthRem<=2500?lengthRem:2500);
					mdl.setAnamnesiPatologica(anamnesiRemota);

					if(i.getIcd()!=null) {
						String conclusioni = pratica.getConclusioni();
						if(conclusioni==null) {
							conclusioni = "";
						}else if(!conclusioni.isEmpty()) {
							conclusioni+="\r\n";
						}
						if(i.getNuovaDiagnosiDiSorveglianzaSanitaria()==1) {
							conclusioni+=("NUOVA DIAGNOSI: "+i.getIcd());
						}else {
							conclusioni+=("DIAGNOSI: "+i.getIcd());
						}
						pratica.setConclusioni(conclusioni);
					}

					saveOnTarget(mdl);
				}
			}
		}

		//NODULO POLMONARE
		if(source.getNoduloPolmonare()!=null && !source.getNoduloPolmonare().isEmpty()) {
			for(NoduloPolmonare n : source.getNoduloPolmonare()) {
				if(n.getDiametroMin_max()!=null) {
					String anamnesiRemota = mdl.getAnamnesiPatologica();
					anamnesiRemota+=("\r\nNodulo polmonare: diametro min-max: "+n.getDiametroMin_max());
					if(!Integer.valueOf(0).equals(n.getVolumeTotalecm3())) {
						anamnesiRemota+=(" volume totale (cm3): "+n.getVolumeTotalecm3());
					}
					if(n.getCalcifico()!=null) {
						anamnesiRemota+=(" calcifico: "+n.getCalcifico());
					}
					if(n.getDensita()!=null) {
						anamnesiRemota+=(" densità: "+n.getDensita());
					}
					if(n.getForma()!=null) {
						anamnesiRemota+=(" forma: "+n.getForma());
					}
					if(n.getSuperficie()!=null) {
						anamnesiRemota+=(" superficie: "+n.getSuperficie());
					}
					if(n.getGravita()!=null) {
						anamnesiRemota+=(" gravità: "+n.getGravita());
					}
					if(n.getSede()!=null) {
						anamnesiRemota+=(" sede: "+n.getSede());
					}
					if(n.getIncremento()!=null) {
						anamnesiRemota+=(" incremento: "+n.getIncremento());
					}
					if(n.getData()!=null) {
						anamnesiRemota+=(" data: "+sdf.format(n.getData()));
					}

					int lengthRem = anamnesiRemota.length();
					anamnesiRemota = anamnesiRemota.substring(0,lengthRem<=2500?lengthRem:2500);
					mdl.setAnamnesiPatologica(anamnesiRemota);

					saveOnTarget(mdl);
				}
			}
		}

		//ACCERTAMENTI
		manageAccertamenti(source, mdl, pratica);
	}

	public void fixAnamnesi(Procpratiche pratica, Person paziente, Anagrafica source) {
		MedicinaLavoro mdl = pratica.getMedicinaLavoro();
		//ANAMNESI LAVORATIVA - ESPOSIZIONI
		manageAnamnesisMdl(source, mdl);
		//FATTORI DI RISCHIO EXTRA LAVORATIVI
		manageFattoriRischioExtra(source, mdl);
	}
	
	private void managePratica(Procpratiche pratica, Person paziente, Anagrafica source) {
		MedicinaLavoro mdl = new MedicinaLavoro();
		mdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
		mdl.setCreationDate(new Date());

		mdl.setType(getCodeValue("phidic.spisal.mdl.type.02"));//2 Sorveglianza Sanitaria Ex Esposti
		mdl.setPatient(paziente);

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
		
		//ANAMNESI LAVORATIVA - ESPOSIZIONI
		manageAnamnesisMdl(source, mdl);

		//FATTORI DI RISCHIO EXTRA LAVORATIVI
		manageFattoriRischioExtra(source, mdl);

		//PRIMA VISITA E SUCCESSIVI FOLLOWUP
		manageVisite(source, mdl, pratica);

		//ANAMNESI PATOLOGICA REMOTA
		if(source.getIndagineMalattiaProfessionale()!=null && !source.getIndagineMalattiaProfessionale().isEmpty()) {
			for(IndagineMalattiaProfessionale i : source.getIndagineMalattiaProfessionale()) {
				if(i.getMp()!=null) {
					String anamnesiRemota = mdl.getAnamnesiPatologica();
					anamnesiRemota+=("\r\nM.P. "+i.getMp());
					if(i.getDataSegnalazione()!=null) {
						anamnesiRemota+=(" segnalata il "+i.getDataSegnalazione());
					}
					if(i.getStrutturaCheSegnala()!=null) {
						anamnesiRemota+=(" da "+i.getStrutturaCheSegnala());
					}
					int lengthRem = anamnesiRemota.length();
					anamnesiRemota = anamnesiRemota.substring(0,lengthRem<=2500?lengthRem:2500);
					mdl.setAnamnesiPatologica(anamnesiRemota);

					if(i.getIcd()!=null) {
						String conclusioni = pratica.getConclusioni();
						if(conclusioni==null) {
							conclusioni = "";
						}else if(!conclusioni.isEmpty()) {
							conclusioni+="\r\n";
						}
						if(i.getNuovaDiagnosiDiSorveglianzaSanitaria()==1) {
							conclusioni+=("NUOVA DIAGNOSI: "+i.getIcd());
						}else {
							conclusioni+=("DIAGNOSI: "+i.getIcd());
						}
						pratica.setConclusioni(conclusioni);
					}

					saveOnTarget(mdl);
				}
			}
		}

		//NODULO POLMONARE
		if(source.getNoduloPolmonare()!=null && !source.getNoduloPolmonare().isEmpty()) {
			for(NoduloPolmonare n : source.getNoduloPolmonare()) {
				if(n.getDiametroMin_max()!=null) {
					String anamnesiRemota = mdl.getAnamnesiPatologica();
					anamnesiRemota+=("\r\nNodulo polmonare: diametro min-max: "+n.getDiametroMin_max());
					if(!Integer.valueOf(0).equals(n.getVolumeTotalecm3())) {
						anamnesiRemota+=(" volume totale (cm3): "+n.getVolumeTotalecm3());
					}
					if(n.getCalcifico()!=null) {
						anamnesiRemota+=(" calcifico: "+n.getCalcifico());
					}
					if(n.getDensita()!=null) {
						anamnesiRemota+=(" densità: "+n.getDensita());
					}
					if(n.getForma()!=null) {
						anamnesiRemota+=(" forma: "+n.getForma());
					}
					if(n.getSuperficie()!=null) {
						anamnesiRemota+=(" superficie: "+n.getSuperficie());
					}
					if(n.getGravita()!=null) {
						anamnesiRemota+=(" gravità: "+n.getGravita());
					}
					if(n.getSede()!=null) {
						anamnesiRemota+=(" sede: "+n.getSede());
					}
					if(n.getIncremento()!=null) {
						anamnesiRemota+=(" incremento: "+n.getIncremento());
					}
					if(n.getData()!=null) {
						anamnesiRemota+=(" data: "+sdf.format(n.getData()));
					}

					int lengthRem = anamnesiRemota.length();
					anamnesiRemota = anamnesiRemota.substring(0,lengthRem<=2500?lengthRem:2500);
					mdl.setAnamnesiPatologica(anamnesiRemota);

					saveOnTarget(mdl);
				}
			}
		}

		//ACCERTAMENTI
		manageAccertamenti(source, mdl, pratica);
	}

	private void manageVisite(Anagrafica source, MedicinaLavoro mdl, Procpratiche pratica) {
		Attivita padre = null;
		if(source.getVisitaMedica()!=null && source.getVisitaMedica().getDataVisita()!=null) {
			padre = manageVisitaGenerica(source.getVisitaMedica(), mdl, pratica);
		}
		if(source.getPrimoControllo()!=null && source.getPrimoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getPrimoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getSecondoControllo()!=null && source.getSecondoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getSecondoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getTerzoControllo()!=null && source.getTerzoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getTerzoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getQuartoControllo()!=null && source.getQuartoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getQuartoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getQuintoControllo()!=null && source.getQuintoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getQuintoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getSestoControllo()!=null && source.getSestoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getSestoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getSettimoControllo()!=null && source.getSettimoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getSettimoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(source.getOttavoControllo()!=null && source.getOttavoControllo().getDataVisita()!=null) {
			Attivita figlia = manageVisitaGenerica(source.getOttavoControllo(), mdl, pratica);
			if(padre!=null) {
				figlia.setParent(padre);
				padre.addChildren(figlia);
			}
		}
		if(padre!=null) {
			saveOnTarget(padre);
		}

		if(source.getProssimoControllo()!=null && source.getProssimoControllo().getDataProssimoControllo()!=null
				&& padre!=null && padre.getChildren()!=null && !padre.getChildren().isEmpty()) {

			Attivita ultima = padre.getChildren().get(padre.getChildren().size()-1);
			if(ultima.getVisitaMdl()!=null && ultima.getVisitaMdl().size()>0) {
				VisitaMdl visita = ultima.getVisitaMdl().get(0);
				if(visita.getConclusioniMdl()!=null) {
					visita.getConclusioniMdl().setControllo(source.getProssimoControllo().getDataProssimoControllo());
				}
			}
		}

		//VISITE GENERICHE
		if(source.getVisita()!=null && !source.getVisita().isEmpty()) {
			for(Visita v : source.getVisita()) {
				Attivita a = new Attivita();
				a.setCreatedBy(this.getClass().getSimpleName()+ulss);
				a.setCreationDate(new Date());
				a.setDataInizio(v.getDataVisita());
				a.setDataFine(v.getDataConclusione());

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

				s.setUtente(mdl.getPatient());
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

				//ESAME OBIETTIVO
				vsp.setNote(v.getEsameObiettivo());

				//ANAMNESI PATOLOGICA PROSSIMA
				if(v.getRaccordoAnamnestico()!=null) {
					String anamnesi = v.getRaccordoAnamnestico();

					int lenghtAn = anamnesi.length();
					anamnesi = anamnesi.substring(0,lenghtAn<=2000?lenghtAn:2000);
					vsp.setMalattie(anamnesi);
				}

				saveOnTarget(vsp);
				vmdl.setVisitaSp(vsp);
				vsp.addVisitaMdl(vmdl);
				saveOnTarget(vmdl);

				String refertoFinale = "";
				//CONCLUSIONI
				if(v.getConclusioni()!=null) {
					refertoFinale+=("CONCLUSIONI: "+v.getConclusioni()+"\r\n");
					ConclusioniMdl conc = new ConclusioniMdl();
					conc.setCreatedBy(this.getClass().getSimpleName()+ulss);
					conc.setCreationDate(new Date());
					conc.setConclusioneVisita(a.getDataFine());

					int lengthRef = refertoFinale.length();
					refertoFinale = refertoFinale.substring(0,lengthRef<=255?lengthRef:255);
					conc.setDiagnosiTxt(refertoFinale);

					saveOnTarget(conc);
					vmdl.setConclusioniMdl(conc);
					conc.addVisitaMdl(vmdl);
					saveOnTarget(vmdl);
				}
			}
		}

		//VISITE SPECIALISTICHE
		if(source.getVisitaSpecialistica()!=null && !source.getVisitaSpecialistica().isEmpty()) {
			for(VisitaSpecialistica v : source.getVisitaSpecialistica()) {
				Attivita a = new Attivita();
				a.setCreatedBy(this.getClass().getSimpleName()+ulss);
				a.setCreationDate(new Date());
				a.setDataInizio(v.getData());

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

				s.setUtente(mdl.getPatient());
				s.setAttivita(a);
				saveOnTarget(s);
				a.addSoggetto(s);


				VisitaMdl vmdl = new VisitaMdl();
				vmdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
				vmdl.setCreationDate(new Date());
				vmdl.setCode(getCodeValue("phidic.spisal.mdl.subtype.02"));// c/o esterni
				vmdl.setAttivita(a);
				saveOnTarget(vmdl);
				a.addVisitaMdl(vmdl);

				VisitaSp vsp = new VisitaSp();
				vsp.setCreatedBy(this.getClass().getSimpleName()+ulss);
				vsp.setCreationDate(new Date());

				vsp.setAccertamenti(new ArrayList<CodeValuePhi>());

				if(v.getTipoSpecialista()!=null) {
					if(v.getTipoSpecialista().getId()==2) {
						CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.03");
						if(!vsp.getAccertamenti().contains(cv))
							vsp.getAccertamenti().add(cv);

					}else if(v.getTipoSpecialista().getId()==4) {
						CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.01");
						if(!vsp.getAccertamenti().contains(cv))
							vsp.getAccertamenti().add(cv);

					}else if(v.getTipoSpecialista().getId()==5) {
						CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.08");
						if(!vsp.getAccertamenti().contains(cv))
							vsp.getAccertamenti().add(cv);

					}else {
						CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.08");
						if(!vsp.getAccertamenti().contains(cv))
							vsp.getAccertamenti().add(cv);
					}					
				}


				saveOnTarget(vsp);
				vmdl.setVisitaSp(vsp);
				vsp.addVisitaMdl(vmdl);
				saveOnTarget(vmdl);

				String refertoFinale = "";
				//CONCLUSIONI
				if(v.getReferto()!=null) {
					refertoFinale+=("CONCLUSIONI: "+v.getReferto()+"\r\n");
					ConclusioniMdl conc = new ConclusioniMdl();
					conc.setCreatedBy(this.getClass().getSimpleName()+ulss);
					conc.setCreationDate(new Date());
					conc.setConclusioneVisita(a.getDataFine());

					int lengthRef = refertoFinale.length();
					refertoFinale = refertoFinale.substring(0,lengthRef<=255?lengthRef:255);
					conc.setDiagnosiTxt(refertoFinale);

					saveOnTarget(conc);
					vmdl.setConclusioniMdl(conc);
					conc.addVisitaMdl(vmdl);
					saveOnTarget(vmdl);
				}
			}
		}
	}

	private void manageAccertamenti(Anagrafica source, MedicinaLavoro mdl, Procpratiche pratica) {
		//ESAME SPIROMETRICO
		if(source.getEsameSpirometrico()!=null && !source.getEsameSpirometrico().isEmpty()) {
			for(EsameSpirometrico e : source.getEsameSpirometrico()) {
				CodeValue icd9 = getCodeValue("phidic.spisal.pratiche.presmp.892");//semplice
				String descrizione = "";
				if(e.getReferto()!=null)
					descrizione+=("REFERTO: "+e.getReferto()+". \r\n");
				if(!Integer.valueOf(0).equals(e.getFev1()))
					descrizione+=("FEV1 = "+e.getFev1()+"\r\n");
				if(!Integer.valueOf(0).equals(e.getVc()))
					descrizione+=("VC = "+e.getVc()+"\r\n");
				if(!Integer.valueOf(0).equals(e.getFev1_vc()))
					descrizione+=("FEV1/VC = "+e.getFev1_vc()+"\r\n");

				if(icd9!=null) {
					createAccertamento(pratica, e.getData(), mdl.getPatient(), icd9, descrizione);
				}
			}
		}
		//DLCO
		if(source.getDlco()!=null && !source.getDlco().isEmpty()) {
			for(Dlco e : source.getDlco()) {
				CodeValue icd9 = getCodeValue("phidic.spisal.pratiche.presmp.900");//globale
				String descrizione = "";
				if(e.getReferto()!=null)
					descrizione+=("REFERTO DLCO: "+e.getReferto()+". \r\n");

				if(icd9!=null) {
					createAccertamento(pratica, e.getData(), mdl.getPatient(), icd9, descrizione);
				}
			}
		}
		//RADIOGRAFIA TORACE
		if(source.getRadiografiaTorace()!=null && !source.getRadiografiaTorace().isEmpty()) {
			for(RadiografiaTorace e : source.getRadiografiaTorace()) {
				CodeValue icd9 = getCodeValue("phidic.spisal.pratiche.presmp.558");//RX DEL TORACE
				String descrizione = "";
				if(e.getReferto()!=null)
					descrizione+=("REFERTO: "+e.getReferto()+". \r\n");

				if(icd9!=null) {
					createAccertamento(pratica, e.getData(), mdl.getPatient(), icd9, descrizione);
				}
			}
		}
		//TAC
		if(source.getTac()!=null && !source.getTac().isEmpty()) {
			for(Tac e : source.getTac()) {
				CodeValue icd9 = getCodeValue("phidic.spisal.pratiche.presmp.549");//TAC TORACE
				if(e.getTipologia()!=null && e.getTipologia().getId()==2) {
					icd9 = getCodeValue("phidic.spisal.pratiche.presmp.550");//TAC MDC
				}
				String descrizione = "";
				if(e.getReferto()!=null)
					descrizione+=("REFERTO: "+e.getReferto()+". \r\n");

				if(icd9!=null) {
					createAccertamento(pratica, e.getData(), mdl.getPatient(), icd9, descrizione);
				}
			}
		}
		//ESAME ISTOLOGICO
		if(source.getEsameIstologico()!=null && !source.getEsameIstologico().isEmpty()) {
			for(EsameIstologico e : source.getEsameIstologico()) {
				CodeValue icd9 = getCodeValue("phidic.spisal.pratiche.presmp.1722");//ES. CITOLOGICO DI ESPETTORATO
				if(e.getTipologia()!=null && "6".equals(e.getTipologia().getId())) {
					icd9 = getCodeValue("phidic.spisal.pratiche.presmp.210");//BRONCOSCOPIA
				}
				String descrizione = "";
				if(e.getReferto()!=null)
					descrizione+=("REFERTO: "+e.getReferto()+". \r\n");

				if(icd9!=null) {
					createAccertamento(pratica, e.getData(), mdl.getPatient(), icd9, descrizione);
				}
			}
		}

	}

	private Attivita manageVisitaGenerica(VisitaGenerica vm, MedicinaLavoro mdl, Procpratiche pratica) {
		PrimaVisita pm = null;
		if(vm instanceof PrimaVisita) {
			pm = (PrimaVisita) vm;
		}
		String anamnesiRemota = mdl.getAnamnesiPatologica();
		Attivita a = new Attivita();
		a.setCreatedBy(this.getClass().getSimpleName()+ulss);
		a.setCreationDate(new Date());
		a.setDataInizio(vm.getDataVisita());

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

		s.setUtente(mdl.getPatient());
		s.setAttivita(a);
		saveOnTarget(s);
		a.addSoggetto(s);


		VisitaMdl vmdl = new VisitaMdl();
		vmdl.setCreatedBy(this.getClass().getSimpleName()+ulss);
		vmdl.setCreationDate(new Date());
		if(pm!=null)
			vmdl.setPrimaVisita(true);
		vmdl.setCode(getCodeValue("phidic.spisal.mdl.subtype.01"));
		vmdl.setAttivita(a);
		saveOnTarget(vmdl);
		a.addVisitaMdl(vmdl);

		VisitaSp vsp = new VisitaSp();
		vsp.setCreatedBy(this.getClass().getSimpleName()+ulss);
		vsp.setCreationDate(new Date());

		//ESAME OBIETTIVO
		vsp.setNote(vm.getEsameObiettivo());
		if(pm!=null) {
			vsp.setPeso(pm.getPeso());
			if(pm.getAltezza()!=null)
				vsp.setAltezza(pm.getAltezza().replace(",",""));
		}


		//ANAMNESI PATOLOGICA REMOTA
		String data = "";
		String datiAnamnestici = "";
		String malattieInfettive = "";
		if(a.getDataInizio()!=null) {
			data += (sdf.format(a.getDataInizio())+": ");
		}
		if(vm.getDatiAnamnesticiSalienti()!=null) {
			datiAnamnestici+=(vm.getDatiAnamnesticiSalienti()+". ");
		}
		if(pm!=null && pm.getMalattieInfettiveUltimi5Anni()!=null) {
			malattieInfettive+=pm.getMalattieInfettiveUltimi5Anni();
			if(pm.getFebbre()!=null) {
				malattieInfettive+=("(Febbre: "+pm.getFebbre()+").");
			}
		}
		anamnesiRemota+=(data+datiAnamnestici+malattieInfettive+"\r\n");

		//ANAMNESI PATOLOGICA PROSSIMA
		if(vm.getVisitaMedica()!=null) {
			String anamnesi = vm.getVisitaMedica();

			int lenghtAn = anamnesi.length();
			anamnesi = anamnesi.substring(0,lenghtAn<=2000?lenghtAn:2000);
			vsp.setAnamnesiProssima(anamnesi);
		}

		//SPIROMETRIA
		if(vm.getDataSpirometria()!=null) {
			CodeValue icd9=null;
			if(pm!=null && pm.getDlco()!=null) {
				icd9 = getCodeValue("phidic.spisal.pratiche.presmp.900");//globale
			}else {
				icd9 = getCodeValue("phidic.spisal.pratiche.presmp.892");//semplice
			}
			String descrizione = "";
			if(vm.getSpirometria()!=null)
				descrizione+=(vm.getSpirometria()+". \r\n");
			if(!Integer.valueOf(0).equals(vm.getCvpc()))
				descrizione+=("CV % = "+vm.getCvpc()+"\r\n");
			if(!Integer.valueOf(0).equals(vm.getVemspc()))
				descrizione+=("VEMS % = "+vm.getVemspc()+"\r\n");
			if(!Integer.valueOf(0).equals(vm.getVems_cvpc()))
				descrizione+=("VEMS/CV % = "+vm.getVems_cvpc()+"\r\n");

			if(pm!=null && pm.getDlco()!=null)
				descrizione+=("DLCO: "+pm.getDlco());

			if(icd9!=null) {
				createAccertamento(pratica, vm.getDataSpirometria(), mdl.getPatient(), icd9, descrizione);
			}
		}

		//RADIOTERAPIA
		if(pm!=null && !Integer.valueOf(0).equals(pm.getRadioterapia())) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.1876");//radioterapia

			String descrizione = "";
			if(pm.getMotivoRadioterapia()!=null)
				descrizione+=("MOTIVO: "+pm.getMotivoRadioterapia()+". \r\n");
			if(pm.getSedeRadioterapia()!=null)
				descrizione+=("SEDE: "+pm.getSedeRadioterapia()+"\r\n");
			if(pm.getPeriodoRadioterapia()!=null)
				descrizione+=("PERIODO: "+pm.getPeriodoRadioterapia()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//PET
		if(vm.getPet()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.1859");//PET-CT

			String descrizione = "";
			if(vm.getPet()!=null)
				descrizione+=("DATA: "+vm.getPet()+". \r\n");//la data è formattata im svariati modi:impossibile estrarla come data precisa
			if(vm.getRefertoPet()!=null)
				descrizione+=("	REFERTO: "+vm.getRefertoPet()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//INDAGINE RADIOLOGICA
		if(vm.getIndagineRadiologica()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.549");//TC DEL TORACE

			String descrizione = "";
			descrizione+=("1A INDAGINE RADIOLOGICA: "+sdf.format(vm.getIndagineRadiologica())+"\r\n");
			if(vm.getReferto()!=null)
				descrizione+=("	REFERTO: "+vm.getReferto()+"\r\n");
			if(vm.getLesione()!=null)
				descrizione+=("	LESIONE: "+vm.getLesione()+"\r\n");
			if(vm.getDiametro()!=null)
				descrizione+=("	DIAMETRO: "+vm.getDiametro()+"\r\n");
			if(vm.getEvoluzione()!=null) {
				descrizione+=("	EVOLUZIONE: "+vm.getEvoluzione()+"\r\n");
			}

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//PRIMA VISITA PNEUMOLOGICA
		if(vm.getVisitaPneumologica()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.960");//PRIMA VISITA PNEUMOLOGICA

			String descrizione = "";
			descrizione+=("VISITA PNEUMOLOGICA: "+vm.getDataVisitaPneumologica()+"\r\n");
			if(vm.getVisitaPneumologica()!=null)
				descrizione+=("	REFERTO: "+vm.getVisitaPneumologica()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//BIOPSIA
		if(vm.getBiopsia()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.211");//BRONCOSCOPIA CON PRELIEVO BRONCHIALE

			String descrizione = "";
			descrizione+=("BIOPSIA: "+vm.getBiopsia()+"\r\n");
			if(vm.getRefertoBiopsia()!=null)
				descrizione+=("	REFERTO: "+vm.getRefertoBiopsia()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//VISITA CHIRURGICA
		if(vm.getDataVisitaChirurgica()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.946");//PRIMA VISITA CHIRURGICA GENERALE

			String descrizione = "";
			descrizione+=("VISITA CHIRURGICA: "+vm.getDataVisitaChirurgica()+"\r\n");
			if(vm.getVisitaChirurgica()!=null)
				descrizione+=("	REFERTO: "+vm.getVisitaChirurgica()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//PRELIEVO
		if(vm.getDataPrelievo()!=null) {
			CodeValue icd9=getCodeValue("phidic.spisal.pratiche.presmp.1773");//PRELIEVO DI SANGUE VENOSO

			String descrizione = "";
			descrizione+=("PRELIEVO: "+vm.getDataPrelievo()+"\r\n");

			if(icd9!=null) {
				createAccertamento(pratica, null, mdl.getPatient(), icd9, descrizione);
			}
		}

		//ACCERTAMENTI RICHIESTI ED ESEGUITI
		String refertoFinale = "";
		vsp.setAccertamenti(new ArrayList<CodeValuePhi>());

		//ALTRA VISITA
		if(vm.getDataAltraVisita()!=null) {
			CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.08");//altro
			if(!vsp.getAccertamenti().contains(cv))
				vsp.getAccertamenti().add(cv);
			refertoFinale+=("ALTRA VISITA: "+vm.getDataAltraVisita()+"\r\n");
			if(vm.getVisitaChirurgica()!=null)
				refertoFinale+=("	REFERTO: "+vm.getVisita()+"\r\n");
		}

		//RICOVERO
		if(vm.getDataRicovero()!=null) {
			CodeValuePhi cv = getCodeValue("phidic.spisal.mdl.investigations.08");//altro
			if(!vsp.getAccertamenti().contains(cv))
				vsp.getAccertamenti().add(cv);
			refertoFinale+=("RICOVERO: "+vm.getDataRicovero()+"\r\n");
			if(vm.getRepartoRicovero()!=null)
				refertoFinale+=("	REPARTO: "+vm.getRepartoRicovero()+"\r\n");
			if(vm.getDiagnosiDimissione()!=null)
				refertoFinale+=("	DIAGNOSI: "+vm.getDiagnosiDimissione()+"\r\n");
		}

		saveOnTarget(vsp);
		vmdl.setVisitaSp(vsp);
		vsp.addVisitaMdl(vmdl);
		saveOnTarget(vmdl);

		//CONCLUSIONI
		if(vm.getConclusioni()!=null)
			refertoFinale+=("CONCLUSIONI: "+vm.getConclusioni()+"\r\n");

		ConclusioniMdl conc = new ConclusioniMdl();
		conc.setCreatedBy(this.getClass().getSimpleName()+ulss);
		conc.setCreationDate(new Date());
		conc.setConclusioneVisita(a.getDataFine());

		int lengthRef = refertoFinale.length();
		refertoFinale = refertoFinale.substring(0,lengthRef<=255?lengthRef:255);
		conc.setDiagnosiTxt(refertoFinale);

		int lengthRem = anamnesiRemota.length();
		anamnesiRemota = anamnesiRemota.substring(0,lengthRem<=2500?lengthRem:2500);
		mdl.setAnamnesiPatologica(anamnesiRemota);

		saveOnTarget(conc);
		vmdl.setConclusioniMdl(conc);
		conc.addVisitaMdl(vmdl);
		saveOnTarget(vmdl);

		return a;
	}

	private void manageFattoriRischioExtra(Anagrafica source, MedicinaLavoro mdl) {
		if(source.getAbitudineAlFumo()!=null && !Integer.valueOf(0).equals(source.getAbitudineAlFumo().getNSigaretteGiorno())
				&& !Integer.valueOf(0).equals(source.getAbitudineAlFumo().getEtaInizioFumo()) && !Integer.valueOf(0).equals(source.getAbitudineAlFumo().getEtaFineFumo())) {
			FattoreRischio f = new FattoreRischio();
			f.setCreatedBy(this.getClass().getSimpleName()+ulss);
			f.setCreationDate(new Date());


			f.setType(getCodeValue("phidic.spisal.segnalazioni.workdisease.frtype.2"));//EXTRA
			f.setExt(getCodeValue("phidic.spisal.segnalazioni.workdisease.frext.06"));//SIGARETTA
			f.setSigarette(source.getAbitudineAlFumo().getNSigaretteGiorno());

			if(source.getAbitudineAlFumo().getDataFineFumo()!=null)
				f.setNote("Data fine fumo: "+source.getAbitudineAlFumo().getDataFineFumo());

			Calendar cal = Calendar.getInstance();

			if(source.getAbitudineAlFumo().getEtaInizioFumo()!=null) {
				cal.setTime(source.getDataNascita());
				cal.add(Calendar.YEAR, source.getAbitudineAlFumo().getEtaInizioFumo());
				f.setStartValidity(cal.getTime());
			}
			if(source.getAbitudineAlFumo().getEtaFineFumo()!=null) {
				cal.setTime(source.getDataNascita());
				cal.add(Calendar.YEAR, source.getAbitudineAlFumo().getEtaFineFumo());
				f.setEndValidity(cal.getTime());
			}


			if(f!=null && f.getSigarette()!=null && f.getStartValidity()!=null && f.getEndValidity()!=null){
				long difference = f.getEndValidity().getTime()-f.getStartValidity().getTime();
				long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);

				Double sigRisk = ((double)(days*f.getSigarette()))/20/365;

				double toRet = Math.round(sigRisk * 10);
				toRet = toRet/10;
				f.setSigRisk(toRet);
			}

			f.setMedicinaLavoro(mdl);
			saveOnTarget(f);
		}
	}

	private void manageAnamnesisMdl(Anagrafica source, MedicinaLavoro mdl) {
		//sono tutti pensionati
		mdl.setCondProf(getCodeValue("phidic.spisal.pratiche.condprof.pen"));

		if(source.getEsposizioneProfessionale()!=null) {

			EsposizioneProfessionale e = source.getEsposizioneProfessionale();

			if(e.getAnnoInizio1()!=null && e.getPrimaEsposizione()!=null) {
				try {
					String ditta = e.getPrimaEsposizione();
					String reparto = e.getAttivita1();
					String mansione = e.getMansione1();
					Double multiplier = Double.parseDouble(e.getIntensita1().replace(" ", "").replace(",","."));
					Double frequenza = Double.parseDouble(e.getFrequenza1().replace(" ", "").replace(",","."));
					Integer annoInizio = Integer.parseInt(e.getAnnoInizio1().replace(" ",""));
					Integer annoFine = Integer.parseInt(e.getAnnoFine1().replace(" ",""));
					
					AnamnesisMdl anamnesi = new AnamnesisMdl();
					anamnesi.setCreatedBy(this.getClass().getSimpleName()+ulss);
					anamnesi.setCreationDate(new Date());
					anamnesi.setStartValidity(null);
					anamnesi.setEndValidity(null);
					saveOnTarget(anamnesi);
					anamnesi = manageFattoriRischioLav(multiplier, frequenza, annoInizio, annoFine, ditta, reparto, mansione, anamnesi);
					if(e.getUltimoAnnoDiEsposizione()!=null) {
						String note = anamnesi.getNote();
						if(note==null || note.isEmpty())
							note=("Ultimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));
						else
							note+=("\r\nUltimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));

						anamnesi.setNote(note);

					}
					anamnesi.setMedicinaLavoro(mdl);
					saveOnTarget(anamnesi);
					mdl.addAnamnesisMdl(anamnesi);
				}catch (NumberFormatException ex) {
					//do nothing
				}
			}
			if(e.getAnnoInizio2()!=null && e.getSecondaEsposizione()!=null) {
				try {
					String ditta = e.getSecondaEsposizione();
					String reparto = e.getAttivita2();
					String mansione = e.getMansione2();
					Double multiplier = Double.parseDouble(e.getIntensita2().replace(" ", "").replace(",","."));
					Double frequenza = Double.parseDouble(e.getFrequenza2().replace(" ", "").replace(",","."));
					Integer annoInizio = Integer.parseInt(e.getAnnoInizio2().replace(" ",""));
					Integer annoFine = Integer.parseInt(e.getAnnoFine2().replace(" ",""));
					
					AnamnesisMdl anamnesi = new AnamnesisMdl();
					anamnesi.setCreatedBy(this.getClass().getSimpleName()+ulss);
					anamnesi.setCreationDate(new Date());
					anamnesi.setStartValidity(null);
					anamnesi.setEndValidity(null);
					saveOnTarget(anamnesi);
					anamnesi = manageFattoriRischioLav(multiplier, frequenza, annoInizio, annoFine, ditta, reparto, mansione, anamnesi);
					if(e.getUltimoAnnoDiEsposizione()!=null) {
						String note = anamnesi.getNote();
						if(note==null || note.isEmpty())
							note=("Ultimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));
						else
							note+=("\r\nUltimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));

						anamnesi.setNote(note);

					}
					anamnesi.setMedicinaLavoro(mdl);
					saveOnTarget(anamnesi);
					mdl.addAnamnesisMdl(anamnesi);
				}catch (NumberFormatException ex) {
					//do nothing
				}
			}
			if(e.getAnnoInizio3()!=null && e.getTerzaEsposizione()!=null) {
				try {
					String ditta = e.getTerzaEsposizione();
					String reparto = e.getAttivita3();
					String mansione = e.getMansione3();
					Double multiplier = Double.parseDouble(e.getIntensita3().replace(" ", "").replace(",","."));
					Double frequenza = Double.parseDouble(e.getFrequenza3().replace(" ", "").replace(",","."));
					Integer annoInizio = Integer.parseInt(e.getAnnoInizio3().replace(" ",""));
					Integer annoFine = Integer.parseInt(e.getAnnoFine3().replace(" ",""));
					
					AnamnesisMdl anamnesi = new AnamnesisMdl();
					anamnesi.setCreatedBy(this.getClass().getSimpleName()+ulss);
					anamnesi.setCreationDate(new Date());
					anamnesi.setStartValidity(null);
					anamnesi.setEndValidity(null);
					saveOnTarget(anamnesi);
					anamnesi = manageFattoriRischioLav(multiplier, frequenza, annoInizio, annoFine, ditta, reparto, mansione, anamnesi);
					if(e.getUltimoAnnoDiEsposizione()!=null) {
						String note = anamnesi.getNote();
						if(note==null || note.isEmpty())
							note=("Ultimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));
						else
							note+=("\r\nUltimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));

						anamnesi.setNote(note);

					}
					anamnesi.setMedicinaLavoro(mdl);
					saveOnTarget(anamnesi);
					mdl.addAnamnesisMdl(anamnesi);
				}catch (NumberFormatException ex) {
					//do nothing
				}
			}
			if(e.getAnnoInizio4()!=null && e.getQuartaEsposizione()!=null) {
				try {
					String ditta = e.getQuartaEsposizione();
					String reparto = e.getAttivita4();
					String mansione = e.getMansione4();
					Double multiplier = Double.parseDouble(e.getIntensita4().replace(" ", "").replace(",","."));
					Double frequenza = Double.parseDouble(e.getFrequenza4().replace(" ", "").replace(",","."));
					Integer annoInizio = Integer.parseInt(e.getAnnoInizio4().replace(" ",""));
					Integer annoFine = Integer.parseInt(e.getAnnoFine4().replace(" ",""));
					
					AnamnesisMdl anamnesi = new AnamnesisMdl();
					anamnesi.setCreatedBy(this.getClass().getSimpleName()+ulss);
					anamnesi.setCreationDate(new Date());
					anamnesi.setStartValidity(null);
					anamnesi.setEndValidity(null);
					saveOnTarget(anamnesi);
					anamnesi = manageFattoriRischioLav(multiplier, frequenza, annoInizio, annoFine, ditta, reparto, mansione, anamnesi);
					if(e.getUltimoAnnoDiEsposizione()!=null) {
						String note = anamnesi.getNote();
						if(note==null || note.isEmpty())
							note=("Ultimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));
						else
							note+=("\r\nUltimo anno di esposizione: "+sdf.format(e.getUltimoAnnoDiEsposizione()));

						anamnesi.setNote(note);

					}
					anamnesi.setMedicinaLavoro(mdl);
					saveOnTarget(anamnesi);
					mdl.addAnamnesisMdl(anamnesi);
				}catch (NumberFormatException ex) {
					//do nothing
				}
			}
		}
	}

	private AnamnesisMdl manageFattoriRischioLav(Double multiplier, Double frequenza, Integer annoInizio, Integer annoFine, 
			String ditta, String reparto, String mansione, AnamnesisMdl anamnesi) {
		if(multiplier==null || frequenza==null || annoInizio==null || annoFine==null || anamnesi==null)
			return anamnesi;

		if(multiplier<0.00135) {
			multiplier = 0D;
		}else if(multiplier<0.0135) {
			multiplier = 0.00135D;
		}else if(multiplier<0.135) {
			multiplier = 0.0135D;
		}else if(multiplier<1.35) {
			multiplier = 0.135D;
		}else if(multiplier<13.5) {
			multiplier = 1.35D;
		}else if(multiplier<135) {
			multiplier = 13.5D;
		}else if(multiplier<1350) {
			multiplier = 135D;
		}

		Double expAMI = anamnesi.getExpAmi();
		
		FattoreRischio f = new FattoreRischio();
		f.setCreatedBy(this.getClass().getSimpleName()+ulss);
		f.setCreationDate(new Date());

		f.setType(getCodeValue("phidic.spisal.segnalazioni.workdisease.frtype.1"));//LAV
		f.setExpType(getCodeValue("phidic.spisal.mdl.exptype.ami"));//ASBESTO
		f.setUnitaMisura(getCodeValue("phidic.spisal.pratiche.um.ff/cc"));//unita
		f.setFrequenza(frequenza);

		if(multiplier!=null) {
			Query qMult = targetEm.createQuery("SELECT c.code FROM ExpositionCoefficients c WHERE c.isActive=1 AND c.multiplier="+multiplier);
			List<CodeValuePhi> codes = qMult.getResultList();
			if(codes!=null && !codes.isEmpty()) {
				f.setCoefficient(codes.get(0));
				f.setMultiplier(multiplier);
			}else {
				f.setNote("Intensità: "+multiplier);
			}
		}

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

		f.setYearStart(annoInizio);
		start.set(Calendar.YEAR,f.getYearStart());
		if(anamnesi.getStartValidity()==null) {
			anamnesi.setStartValidity(start.getTime());
			if(anamnesi.getEndValidity()==null)
				anamnesi.setEndValidity(start.getTime());
		}else if(anamnesi.getStartValidity().after(start.getTime())){
			anamnesi.setStartValidity(start.getTime());
		}

		f.setYearStop(annoFine);
		end.set(Calendar.YEAR,f.getYearStop());
		if(anamnesi.getEndValidity().before(end.getTime())){
			anamnesi.setEndValidity(end.getTime());
		}

		if(f!=null && f.getYearStart()!=null && f.getYearStop()!=null && f.getMultiplier()!=null && f.getExpType()!=null){
			int difference = f.getYearStop()-f.getYearStart()+1;
			Double intensity = difference*f.getMultiplier()*f.getFrequenza();
			f.setIntensityQuant(intensity);
			if(expAMI==null)
				expAMI=0D;
			
			expAMI+=f.getIntensityQuant();
		}

		anamnesi.setNote("Ditta: "+ditta);
		anamnesi.setReparto(reparto);
		anamnesi.setMansione(mansione);
		f.setAnamnesisMdl(anamnesi);
		saveOnTarget(f);
		anamnesi.addFattoreRischio(f);

		anamnesi.setExpAmi(expAMI);

		return anamnesi;
	}

	private void createAccertamento(Procpratiche pratica, Date dataprel, Person paziente, CodeValue icd9, String descrizione) {
		Attivita a = new Attivita();
		a.setCreatedBy(this.getClass().getSimpleName()+ulss);
		a.setCreationDate(new Date());
		a.setDataInizio(dataprel);
		a.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.accertamento"));
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

		PrestMdl prest = new PrestMdl();
		prest.setCreatedBy(this.getClass().getSimpleName()+ulss);
		prest.setCreationDate(new Date());
		prest.setPrest((CodeValuePhi) icd9);
		int lenghtDesc = descrizione.length();
		descrizione = descrizione.substring(0,lenghtDesc<=255?lenghtDesc:255);
		prest.setEsitoTxt(descrizione);
		if(pratica.getOperatori()!=null && !pratica.getOperatori().isEmpty())
			prest.setOperatore(pratica.getOperatori().get(0));

		prest.setAccertaMdl(amdl);
		saveOnTarget(prest);
		amdl.addPrestMdl(prest);
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
		if(source==null || source.getCodiceFiscale()==null)
			return null;

		String nome = "";
		String cognome = "";
		String cn = source.getCognomeENome();
		//approccio stupido, ma da una select distinct funzionerà sempre
		if(cn!=null) {
			if(cn.startsWith("D'") || cn.startsWith("DA ") || cn.startsWith("DAL ") || cn.startsWith("DALL'") ||
					cn.startsWith("DALLA ") || cn.startsWith("DE ") || cn.startsWith("DEI ") || cn.startsWith("DEL ") ||
					cn.startsWith("DELLA ") || cn.startsWith("DI ") || cn.startsWith("FACCHIN - MORETTO") || cn.startsWith("GRIZ BITTADOR ") ||
					cn.startsWith("LA ") || cn.startsWith("MAZZA BALESTRIERI ") || cn.startsWith("POLO DEL VECCHIO ") || 
					cn.startsWith("SALGARO VACCARO ") || cn.startsWith("SANTON RALLO ")) {
				cognome=cn.substring(0, cn.lastIndexOf(" "));
				nome=cn.substring(cn.lastIndexOf(" ")+1);
			}else {
				cognome=cn.substring(0, cn.indexOf(" "));
				nome=cn.substring(cn.indexOf(" ")+1);
			}
		}
		queryPerson.setParameter("nome", nome);
		queryPerson.setParameter("cognome", cognome);
		queryPerson.setParameter("dataNascita", source.getDataNascita());
		queryPerson.setParameter("cf", source.getCodiceFiscale());
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

			String wlCode = "AML_AM";

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
	private boolean checkMapping(int id){
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
	private boolean checkMapCommPrat(int id){
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

	public Procpratiche getMappedPratica(int id) {
		String hqlMapping = "SELECT m FROM MapPazienti m WHERE m.idsorves = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPazienti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPazienti map = list.get(0);
			Query qPrat = targetEm.createQuery("SELECT p FROM Procpratiche p WHERE p.internalId = :id");
			qPrat.setParameter("id", map.getPraticaId());
			List<Procpratiche> lp = qPrat.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}

		return null;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Person getMapped(int id){
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

	private Operatore getOrCreateAmiantoOperatore(ServiceDeliveryLocation asl, String cognomeNome) {
		Operatore op = null;
		String nome = "";
		String cognome = "";

		//approccio stupido, ma da una select distinct funzionerà sempre
		if(cognomeNome!=null && !cognomeNome.isEmpty()) {
			cognome=cognomeNome.split(" ")[0];
			nome=cognomeNome.split(" ")[1];
		}else {
			return op;
		}

		Query q = targetEm.createQuery("SELECT op FROM Operatore op WHERE "
				+ "op.code = 'phidic.spisal.pratiche.nomina.amministrativo_V0' AND "
				+ "op.name.giv='"+nome+"' AND op.name.fam='"+cognome+"'");		

		List<Operatore> opList = q.getResultList();
		if(opList!=null && !opList.isEmpty()) {
			op = opList.get(0);

		}else {
			op = new Operatore();

			op.setCreatedBy(this.getClass().getSimpleName()+ulss);
			op.setCreationDate(new Date());

			if(op.getName()==null)
				op.setName(new EN());

			op.getName().setGiv(nome);
			op.getName().setFam(cognome);

			op.setCode(getCodeValue("phidic.spisal.pratiche.nomina.amministrativo"));//PERSONALE AMMINISTRATIVO

			op.setServiceDeliveryLocation(asl);
			op.setEnte(getCodeValue("phidic.spisal.pratiche.ente.spisal"));//SPISAL

			saveOnTarget(op);
		}


		return op;
	}
}
