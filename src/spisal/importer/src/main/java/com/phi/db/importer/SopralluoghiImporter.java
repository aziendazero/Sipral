package com.phi.db.importer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Misure;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.Sopralluoghi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.prevnet.entities.Ditteedilizia;
import com.prevnet.entities.Misureambientali;
import com.prevnet.entities.Operatorisopralluoghi;
import com.prevnet.entities.Personapresentesop;
import com.prevnet.entities.Sitsopralluoghi;
import com.prevnet.entities.Sopralluoghidip;
import com.prevnet.mappings.MapSopralluoghi;

@SuppressWarnings({"unchecked"})
public class SopralluoghiImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(SopralluoghiImporter.class.getName());

	private static SopralluoghiImporter instance = null;

	public void aggiornaSopralluoghi(Sopralluoghidip source) {
		if(checkMapping(source.getIdsopralluoghidip())){
			Attivita target = getMapped(source.getIdsopralluoghidip());
			//modifica del 8/11/2017
			String note = "";
			if(source.getNote()!=null)
				note+=source.getNote();

			note = note.substring(0,note.length()<=4000?note.length():4000);
			target.setNote(note);

			//I0072968 
			if(source.getEsito()!=null) {
				String esitoSop = ("Esito: " + source.getEsito().getUtcodice() + " - " + source.getEsito().getDescrizione() + ".\r\n");
				String noteTemp = (target.getSopralluogo().getNote());
				String noteSop = (esitoSop + noteTemp);
				target.getSopralluogo().setNote(noteSop);
			}

			Query q = targetEm.createNativeQuery("select count(*) from z_attivita z join revinfo r on r.id = z.rev where z.internal_id = :id and r.username<>'phi-esb'");
			BigInteger count = (BigInteger) q.setParameter("id", target.getInternalId()).getSingleResult();
			if(count==null || count.intValue()<=0) {
				saveOnTarget(target);
				thislog.warn("Aggiornato sopralluogo id: "+source.getIdsopralluoghidip());
			}
		}
	}

	public static SopralluoghiImporter getInstance() {
		if(instance == null) {
			instance = new SopralluoghiImporter();
		}
		return instance;
	}

	public static void main (String[] args) {

		SopralluoghiImporter importer = new SopralluoghiImporter();
		importer.deleteImportedData(ulss);
		List<Sopralluoghidip> sopralluoghi = importer.readSopralluoghi();
		if(sopralluoghi!=null){
			for(Sopralluoghidip sop : sopralluoghi){
				importer.importSopralluogo(sop);
			}
		}
		importer.closeResource();
	}

	public void aggiornaSopralluogo(Sopralluoghidip source){
		if(source.getMisureambientalis()!=null && checkMapping(source.getIdsopralluoghidip())){
			Attivita rilevazione = getMapped(source.getIdsopralluoghidip());

			//RILEVAZIONI AMBIENTALI
			if(source.getMisureambientalis()!=null && !source.getMisureambientalis().isEmpty()) {
				CodeValue tipoSopralluogo = getCodeValue("phidic.spisal.pratiche.activities.activitytypes.rilevazione");
				rilevazione.setCode(tipoSopralluogo);
				for(Misureambientali m : source.getMisureambientalis()) {
					CodeValue tipo = getMappedCode(m.getRilevazioneAmb(), "", ulss);//postfix="" cosi non creo voci in fase di import
					if(tipo!=null) {
						Misure misura = new Misure();
						misura.setCreatedBy(this.getClass().getSimpleName()+ulss);
						misura.setCreationDate(new Date());

						if("Sampling".equals(tipo.getParent().getDisplayName())) {
							misura.setType(getCodeValue("phidic.spisal.pratiche.activities.detectiontype.campionamento"));
							misura.setCampionamentoCv(tipo);
						}else if("Measure".equals(tipo.getParent().getDisplayName())) {
							misura.setType(getCodeValue("phidic.spisal.pratiche.activities.detectiontype.misurazione"));
							misura.setMisurazioneCv(tipo);
						}

						String note = "";
						if(m.getNote()!=null)
							note+=m.getNote();
						if(m.getEsitoRilevAmb()!=null) {
							note+=("\r\nESITO RILEVAZIONE: "+m.getEsitoRilevAmb().getDescrizione());
						}
						if(note!=null){
							note = note.substring(0,note.length()<=255?note.length():255);
							misura.setValue(note);
						}
						misura.setAttivita(rilevazione);
						saveOnTarget(misura);
						rilevazione.addMisure(misura);
					}
				}
			}
		}
	}

	public Attivita importSopralluogo(Sopralluoghidip source){
		if(!checkMapping(source.getIdsopralluoghidip())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			CantieriImporter cantImp = CantieriImporter.getInstance();

			Attivita target = new Attivita();

			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(source.getDataregistrazione());

			//LUOGO..
			//DITTA
			if("0".equals(source.getTipoanagrafica())){
				target.setLuogo(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
				Sedi sede = null;

				if(source.getUbicDitta()!=null){
					sede = ditteImp.importDittaCompleta(source.getUbicDitta());
				}else if(source.getDitta()!=null){
					sede = ditteImp.importDittaCompleta(source.getDitta());
				}else if(source.getAnagrafica()!=null){
					sede = ditteImp.importDittaCompleta(source.getAnagrafica());
				}

				if(sede!=null){
					target.setLuogoSede(sede);
					target.setLuogoDitta(sede.getPersonaGiuridica());
					if(sede.getAddr()!=null)
						target.setAddr(sede.getAddr().cloneAd());
				}

				//CANTIERE
			}else if("3".equals(source.getTipoanagrafica())){
				target.setLuogo(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
				Cantiere cant = null;
				if(source.getCantiere()!=null){
					cant = cantImp.importCantiere(source.getCantiere());
				}else if(source.getAnagrafica()!=null){
					cant = cantImp.importCantiere(source.getAnagrafica());
				}

				if(cant!=null){
					target.setLuogoCantiere(cant);
					if(cant.getAddr()!=null)
						target.setAddr(cant.getAddr().cloneAd());
				}

				//PERSONA FISICA
			}else if("4".equals(source.getTipoanagrafica())){
				target.setLuogo(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
				Person pers = null;
				if(source.getAnagrafica()!=null){
					pers = persImp.importPerson(source.getAnagrafica());
				}

				if(pers!=null){
					target.setLuogoUtente(pers);
					if(pers.getAddr()!=null)
						target.setAddr(pers.getAddr().cloneAd());
				}
			}

			if(target.getAddr()==null)
				target.setAddr(new AD());

			AD addr = target.getAddr();
			if(source.getUbicLocalita()!=null){
				addr.setAdl(source.getUbicLocalita().getDescrlocalita());
			}

			manageAddrComune(addr, source.getUbicComune());

			if("050103".equals(ulss) || "050104".equals(ulss)){
				addr.setBnr(null);
				addr.setStr(source.getNotelocalizzato());
			}

			if(source.getNumsopralluogo()!=null)
				target.setNumero(source.getNumsopralluogo().longValue());

			if(source.getDatasopralluogo()!=null){
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(source.getDatasopralluogo());

				if(source.getOravisita()!=null){
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(source.getOravisita());

					cal1.set(Calendar.HOUR, cal2.get(Calendar.HOUR));
					cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
				}

				target.setDataInizio(cal1.getTime());
				if(source.getOrafinevisita()!=null){
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(source.getOrafinevisita());

					cal1.set(Calendar.HOUR, cal2.get(Calendar.HOUR));
					cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
				}

				target.setDataFine(cal1.getTime());
			}

			if(source.getTipoEmergenza()!=null){
				CodeValue tipoSopralluogo = getMappedCode(source.getTipoEmergenza(), "a", ulss);

				if(tipoSopralluogo!=null && (source.getMisureambientalis()==null || source.getMisureambientalis().isEmpty())){
					target.setCode(tipoSopralluogo);
					if("sopralluogo".equals(tipoSopralluogo.getCode())){
						Sopralluoghi sop = importSopData(source);
						saveOnTarget(sop);
						target.setSopralluogo(sop);
					}
				}else if(source.getMisureambientalis()!=null && !source.getMisureambientalis().isEmpty()) {
					tipoSopralluogo = getCodeValue("phidic.spisal.pratiche.activities.activitytypes.rilevazione");
					target.setCode(tipoSopralluogo);
				}
			}

			//modifica del 8/11/2017
			String note = "";
			if(source.getTipoEmergenza()!=null && source.getTipoEmergenza().getDescrizione()!=null)
				note+=(source.getTipoEmergenza().getDescrizione()+"\r\n");
			if(source.getNote()!=null)
				note+=source.getNote();

			note = note.substring(0,note.length()<=4000?note.length():4000);
			target.setNote(note);


			saveOnTarget(target);

			//RILEVAZIONI AMBIENTALI
			if(source.getMisureambientalis()!=null && !source.getMisureambientalis().isEmpty()) {
				for(Misureambientali m : source.getMisureambientalis()) {
					CodeValue tipo = getMappedCode(m.getRilevazioneAmb(), "", ulss);//postfix="" cosi non creo voci in fase di import
					if(tipo!=null) {
						Misure misura = new Misure();
						misura.setCreatedBy(this.getClass().getSimpleName()+ulss);
						misura.setCreationDate(new Date());

						if("Sampling".equals(tipo.getParent().getDisplayName())) {
							misura.setType(getCodeValue("phidic.spisal.pratiche.activities.detectiontype.campionamento"));
							misura.setCampionamentoCv(tipo);
						}else if("Measure".equals(tipo.getParent().getDisplayName())) {
							misura.setType(getCodeValue("phidic.spisal.pratiche.activities.detectiontype.misurazione"));
							misura.setMisurazioneCv(tipo);
						}

						if(m.getNote()!=null)
							note+=("\r\n"+m.getNote());
						if(m.getEsitoRilevAmb()!=null) {
							note+=("\r\nESITO RILEVAZIONE: "+m.getEsitoRilevAmb().getDescrizione());
						}
						if(note!=null){
							note = note.substring(0,note.length()<=255?note.length():255);
							misura.setValue(note);
						}
						misura.setAttivita(target);
						saveOnTarget(misura);
						target.addMisure(misura);
					}
				}
			}

			//OPERATORI
			if(source.getOperatorisopralluoghis()!=null){
				OperatoriImporter opImp = OperatoriImporter.getInstance();
				List<Partecipanti> theList = new ArrayList<Partecipanti>();
				for(Operatorisopralluoghi os : source.getOperatorisopralluoghis()){
					if(os.getOperatori()!=null){
						Operatore op = opImp.importOperatore(os.getOperatori());
						if(op!=null && !theList.contains(op)){
							Partecipanti p = new Partecipanti();
							p.setCreatedBy(this.getClass().getSimpleName()+ulss);
							p.setCreationDate(source.getDataregistrazione());
							p.setOperatore(op);
							p.setAttivita(target);
							saveOnTarget(p);
							theList.add(p);
						}
					}
				}
				target.setPartecipanti(theList);
				saveOnTarget(target);

			}

			List<Soggetto> soggetti = new ArrayList<Soggetto>();

			//SOGGETTI
			if(source.getPersonapresentesops()!=null){

				for(Personapresentesop pp : source.getPersonapresentesops()){
					if(pp.getPersona()!=null){
						Person pers = persImp.importPerson(pp.getPersona());
						if(!contains(soggetti,pers)){
							Soggetto s = new Soggetto();
							s.setCreatedBy(this.getClass().getSimpleName()+ulss);
							s.setCreationDate(source.getDataregistrazione());
							s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

							s.setRuolo(getMappedCode(pp.getRuolo(), ulss));

							s.setUtente(pers);
							s.setAttivita(target);

							saveOnTarget(s);
							soggetti.add(s);
						}
					}
				}
			}

			if(source.getPersonaPresente()!=null){
				Person pers = persImp.importPerson(source.getPersonaPresente());
				if(!contains(soggetti,pers)){
					Soggetto s = new Soggetto();
					s.setCreatedBy(this.getClass().getSimpleName()+ulss);
					s.setCreationDate(source.getDataregistrazione());
					s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

					s.setRuolo(getMappedCode(source.getPersonaPresenteRuolo(), ulss));

					s.setUtente(pers);
					s.setAttivita(target);

					saveOnTarget(s);
					soggetti.add(s);
				}
			}

			if(source.getSitsopralluoghis()!=null && !source.getSitsopralluoghis().isEmpty()) {
				for(Sitsopralluoghi sit : source.getSitsopralluoghis()) {
					Person pers = persImp.importPerson(sit.getUtenti());
					if(!contains(soggetti,pers)){
						Soggetto s = new Soggetto();
						s.setCreatedBy(this.getClass().getSimpleName()+ulss);
						s.setCreationDate(source.getDataregistrazione());
						s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

						s.setRuolo(getMappedCode(sit.getRuolo(), ulss));

						s.setUtente(pers);
						s.setAttivita(target);

						saveOnTarget(s);
						soggetti.add(s);
					}
				}
			}

			if(source.getDitteedilizias()!=null){
				try{
					for(Ditteedilizia d : source.getDitteedilizias()){
						if(d.getDitte()!=null){
							Sedi sede = ditteImp.importDittaCompleta(d.getDitte());
							if(sede!=null){
								if(!contains(soggetti, sede)){
									Soggetto s = new Soggetto();
									s.setCreatedBy(this.getClass().getSimpleName()+ulss);
									s.setCreationDate(source.getDataregistrazione());
									s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));

									s.setSede(sede);
									s.setDitta(sede.getPersonaGiuridica());
									s.setAttivita(target);

									saveOnTarget(s);
									soggetti.add(s);
								}
							}
						}
					}
				}catch(EntityNotFoundException e){
					log.error(e.getMessage());
				}
			}

			if(source.getVisionato()!=null && source.getTipovisionato()!=null){
				Soggetto s = null;
				//DITTA
				if("0".equals(source.getTipovisionato())){
					Sedi sede = ditteImp.importDittaCompleta(source.getVisionato());
					if(sede!=null){
						if(!contains(soggetti, sede)){
							s = new Soggetto();
							s.setCreatedBy(this.getClass().getSimpleName()+ulss);
							s.setCreationDate(source.getDataregistrazione());
							s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));

							s.setSede(sede);
							s.setDitta(sede.getPersonaGiuridica());
							s.setAttivita(target);

							saveOnTarget(s);
							soggetti.add(s);
						}
					}

					//CANTIERE
				}else if("3".equals(source.getTipovisionato())){
					Cantiere cant = cantImp.importCantiere(source.getVisionato());
					if(cant!=null){
						if(!contains(soggetti, cant)){
							s = new Soggetto();
							s.setCreatedBy(this.getClass().getSimpleName()+ulss);
							s.setCreationDate(source.getDataregistrazione());
							s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));

							s.setCantiere(cant);
							s.setAttivita(target);

							saveOnTarget(s);
							soggetti.add(s);
						}
					}

					//PERSONA FISICA
				}else if("4".equals(source.getTipovisionato())){
					Person pers = persImp.importPerson(source.getVisionato());
					if(pers!=null){
						if(!contains(soggetti, pers)){
							s = new Soggetto();
							s.setCreatedBy(this.getClass().getSimpleName()+ulss);
							s.setCreationDate(source.getDataregistrazione());
							s.setCode(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));

							s.setUtente(pers);
							s.setAttivita(target);

							saveOnTarget(s);
							soggetti.add(s);
						}
					}
				}
			}

			target.setSoggetto(soggetti);
			saveMapping(source, target);
		}

		return getMapped(source.getIdsopralluoghidip());
	}

	public List<Sopralluoghidip> readSopralluoghi() {
		List<Sopralluoghidip> fascicoli = new ArrayList<Sopralluoghidip>();

		String hqlSopralluoghi = "SELECT f FROM Sopralluoghidip f";
		Query qSopralluoghi = sourceEm.createQuery(hqlSopralluoghi);
		fascicoli = qSopralluoghi.getResultList();
		return fascicoli;
	}

	private Sopralluoghi importSopData(Sopralluoghidip source){
		Sopralluoghi sop = new Sopralluoghi();

		String descrizioneLuoghi = source.getNotelocalizzato();
		if(descrizioneLuoghi!=null){
			descrizioneLuoghi = descrizioneLuoghi.substring(0,descrizioneLuoghi.length()<=4000?descrizioneLuoghi.length():4000);
			sop.setDescrizioneLuoghi(descrizioneLuoghi);
		}


		String attivitaSvolta = source.getNoteattivitasvolta();
		if(attivitaSvolta!=null){
			attivitaSvolta = attivitaSvolta.substring(0,attivitaSvolta.length()<=4000?attivitaSvolta.length():4000);
			sop.setAttivitaSvolta(attivitaSvolta);
		}


		sop.setEsito(getMappedCode(source.getEsito(), ulss));

		sop.setTipoSopralluogo(getMappedCode(source.getTipoEmergenza(), ulss));

		String note = "";

		//I0072968 
		if(source.getEsito()!=null) {
			note+=("Esito: " + source.getEsito().getUtcodice() + " - " + source.getEsito().getDescrizione() + ".\r\n");
		}

		if("1".equals(source.getVerbaleredatto()))
			note+="VERBALE REDATTO.\r\n";

		if(source.getTotpersonepresenti()!=null)
			note+=("TOT PERSONE PRESENTI: "+source.getTotpersonepresenti()+".\r\n");

		if(source.getNote()!=null)
			note+=source.getNote();

		int length = note.length();
		note = note.substring(0,length<=4000?length:4000);
		sop.setNote(note);

		if("1".equals(source.getOptinfrrilev()))
			sop.setViolazioni(true);

		String considerazioni = "";
		if(source.getMotivo()!=null && source.getMotivo().getDescrizione()!=null)
			considerazioni+=("MOTIVO: "+source.getMotivo().getDescrizione()+"\r\n");

		if("1".equals(source.getChkdestnonpresente()))
			considerazioni+="DESTINATARIO PRESENTE.\r\n";
		else
			considerazioni+="DESTINATARIO NON PRESENTE.\r\n";

		if("1".equals(source.getChkpsicurezzapresente()))
			considerazioni+="PIANO SICUREZZA PRESENTE.\r\n";
		else
			considerazioni+="PIANO SICUREZZA NON PRESENTE.\r\n";

		if("1".equals(source.getChkpsicurezzavalutato()))
			considerazioni+="PIANO SICUREZZA VALUTATO.\r\n";
		else
			considerazioni+="PIANO SICUREZZA NON VALUTATO.\r\n";

		if(source.getValPianoSicurezza()!=null)
			considerazioni+=("VAL.PIANO SICUREZZA: "+source.getValPianoSicurezza().getDescrizione()+"\r\n");

		if("0".equals(source.getNominacoordinatori()))
			considerazioni+="NOMINA COORDINATORI EFFETTUATA (OBBLIGATORIA).\r\n";
		else if("1".equals(source.getNominacoordinatori()))
			considerazioni+="NOMINA COORDINATORI NON EFFETTUATA (OBBLIGATORIA).\r\n";
		else if("2".equals(source.getNominacoordinatori()))
			considerazioni+="NOMINA COORDINATORI NON OBBLIGATORIA.\r\n";

		int lengthC = considerazioni.length();
		considerazioni = considerazioni.substring(0,lengthC<=4000?lengthC:4000);
		sop.setConsiderazioni(considerazioni);


		if(source.getRiepilogosop()!=null)
			sop.setConclusioni("RIEPILOGO: "+source.getRiepilogosop());

		return sop;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Attivita getMapped(long id){
		MapSopralluoghi map = sourceEm.find(MapSopralluoghi.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapSopralluoghi m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapSopralluoghi> list = qMapping.getResultList();
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
		MapSopralluoghi m = sourceEm.find(MapSopralluoghi.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapSopralluoghi m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapSopralluoghi> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapSopralluoghi map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	private void saveMapping(Sopralluoghidip source, Attivita target){
		MapSopralluoghi map = new MapSopralluoghi();
		map.setIdprevnet(source.getIdsopralluoghidip());
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
		String hqlMap = "SELECT mf.idphi FROM MapSopralluoghi mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlMap+="WHERE mf.ulss = :ulss";

		Query qMap = sourceEm.createQuery(hqlMap);
		if(ulss!=null && !ulss.isEmpty())
			qMap.setParameter("ulss", ulss);

		List<Long> allIdAttivita = qMap.getResultList();
		List<Long> idAttivita = new ArrayList<Long>();
		while(allIdAttivita!=null && !allIdAttivita.isEmpty()){
			if(allIdAttivita.size()>1000){
				idAttivita.clear();
				idAttivita.addAll(allIdAttivita.subList(0, 1000));
				allIdAttivita.removeAll(idAttivita);
			}else{
				idAttivita.clear();
				idAttivita.addAll(allIdAttivita);
				allIdAttivita.removeAll(idAttivita);
			}
			if(commit){
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

				String qSopralluoghi = "SELECT SOPRALLUOGO_ID FROM attivita WHERE INTERNAL_ID IN (:ids)";
				Query qSop = targetEm.createNativeQuery(qSopralluoghi);
				qSop.setParameter("ids", idAttivita);
				List<BigInteger> idSopralluoghi = qSop.getResultList();				

				//ELIMINO LE ATTIVITA
				String deleteAttivita = "DELETE FROM Attivita e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelAttivita = targetEm.createQuery(deleteAttivita);
				qDelAttivita.setParameter("ids", idAttivita);
				qDelAttivita.executeUpdate();
				targetEm.getTransaction().commit();

				//ELIMINO I DATI DI DETTAGLIO SOPRALLUOGHI
				if(idSopralluoghi!=null && !idSopralluoghi.isEmpty()){
					List<Long> list = new ArrayList<Long>();
					for(BigInteger b : idSopralluoghi){
						if(b!=null)
							list.add(b.longValue());
					}
					String deleteSopralluoghi = "DELETE FROM Sopralluoghi s WHERE s.internalId IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelSopralluoghi = targetEm.createQuery(deleteSopralluoghi);
					qDelSopralluoghi.setParameter("ids", list);
					qDelSopralluoghi.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_SOPRALLUOGHI WHERE ulss = :ulss";
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
}
