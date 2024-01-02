package com.phi.db.importer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.CommittenteVigilanza;
import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.PrevnetNotes;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.TagCantiere;
import com.phi.entities.baseEntity.Vigilanza;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.Cantieri;
import com.prevnet.entities.Comuni;
import com.prevnet.entities.Ditte;
import com.prevnet.entities.InfortuniPrevnet;
import com.prevnet.entities.Parerigenerici;
import com.prevnet.entities.Pianidilavoroamianto;
import com.prevnet.entities.Pratiche;
import com.prevnet.entities.Utenti;

@SuppressWarnings({"unchecked"})
public class VigilanzaImporter extends EntityManagerUtilities {

	private static VigilanzaImporter instance = null;

	public static VigilanzaImporter getInstance() {
		if(instance == null) {
			instance = new VigilanzaImporter();
		}
		return instance;
	}

	public Vigilanza importVigilanza(Pratiche source, Procpratiche target){
		if(target.getVigilanza()==null){
			Vigilanza vig = new Vigilanza();
			vig.setCreatedBy(this.getClass().getSimpleName()+ulss);
			vig.setCreationDate(new Date());

			vig.setData(target.getData());
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			CantieriImporter cantImp = CantieriImporter.getInstance();

			saveOnTarget(vig); //for PersonaGiuridicaSede.vigilanza missing cascade
			if(source!=null && source.getIdprocedura()!=null){
				switch (source.getIdprocedura().intValue()) {
				case 6:
					Cantiere importedCant = null;
					//importiamo la 6 come NOTIFICA CANTIERE E PRATICA DI ATTIVITA GENERICA => NO: RESTA VIGILANZA
					if(source.getCantieris()!=null && !source.getCantieris().isEmpty()){
						for(Cantieri cantiere : source.getCantieris()){
							importedCant = cantImp.importCantiere(cantiere);
							if(importedCant!=null){
								importedCant.setNotifica(true);
								PraticheRiferimenti refs = target.getPraticheRiferimenti();
								if(refs==null){
									refs=new PraticheRiferimenti();
									refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
									refs.setCreationDate(new Date());

								}
								refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
								refs.setRiferimentoCantiere(importedCant);
								saveOnTarget(refs);
								target.setPraticheRiferimenti(refs);
								break;
							}
						}

						Protocollo prot = null;
						if(target.getProtocollo()!=null && !target.getProtocollo().isEmpty()){
							prot=target.getProtocollo().get(0);
						}else{
							prot=new Protocollo();
						}

						prot.setCreatedBy(this.getClass().getSimpleName()+ulss);
						prot.setCreationDate(new Date());
						prot.setProcpratiche(target);

						prot.setServiceDeliveryLocation(target.getUoc());
						prot.setUos(target.getServiceDeliveryLocation());
						prot.setCode(getCodeValue("phidic.spisal.segnalazioni.complextype.4"));
						prot.setData(target.getData());
						prot.setDataASL(target.getDataAssegnazione());
						prot.setStatusCode(getStatus("status.generic.completed")); //STATO ASSEGNATA
						prot.setIsMaster(true);//COMUNICAZIONE PRINCIPALE
						prot.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
						if(target.getPraticheRiferimenti()!=null && target.getPraticheRiferimenti().getRiferimentoCantiere()!=null){
							prot.setRiferimentoCantiere(target.getPraticheRiferimenti().getRiferimentoCantiere());
						}

						saveOnTarget(prot);
						if(target.getProtocollo()==null)
							target.setProtocollo(new ArrayList<Protocollo>());

						if(!target.getProtocollo().contains(prot))
							target.getProtocollo().add(prot);

						//================== da qui è come per la 53 ==================//

						vig.setType(getCodeValue("phidic.spisal.segnalazioni.supervision.types.yard"));
						target.setNumero(target.getNumero().replace("VI_", "VI_CA_"));

						//source.getCantieris() è sempre vuoto per la pratica di tipo 53
						vig.setCantiere(getCantiereFromSopralluogo(target.getAttivita()));
						//se non riesco a importarlo dal sopralluogo lo setto dalla notifica cantiere??
						/*if(vig.getCantiere()==null)
							vig.setCantiere(importedCant);
						 */
						if(vig.getCantiere()!=null){
							//DITTE CONTROLLATE: PRESE DA CANTIERE DEL SOPRALLUOGO
							if(vig.getCantiere().getDitteCantiere()!=null){
								for(DitteCantiere dc : vig.getCantiere().getDitteCantiere()){

									PersoneGiuridiche sedeCant = dc.getPersoneGiuridiche();
									if(sedeCant!=null){
										PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
										pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
										pgs.setCreationDate(new Date());
										pgs.setTipoDitta(dc.getRuolo());
										pgs.setVigilanza(vig);
										pgs.setPersonaGiuridica(sedeCant);
										vig.addPersonaGiuridicaSede(pgs);
										pgs.setChecked(true);
										saveOnTarget(pgs);
									}
								}
							}

							addToPrevnetNotes(target.getPrevnetNotes(), vig.getCantiere());
						}

					}

					PrevnetNotes pnCantiere = target.getPrevnetNotes();
					addToPrevnetNotesParere(pnCantiere, source);
					saveOnTarget(pnCantiere);

					break;
				case 16:
					vig.setType(getCodeValue("phidic.spisal.segnalazioni.supervision.types.generic"));
					target.setNumero(target.getNumero().replace("VI_", "VI_AZ_"));
					if(source.getTipoPratica()!=null){
						CodeValuePhi cv = (CodeValuePhi) getMappedCode(source.getTipoPratica(), "a", ulss);
						List<CodeValuePhi> reasons = new ArrayList<CodeValuePhi>();
						if(cv!=null)
							reasons.add(cv);

						vig.setReason(reasons);
					}

					Sedi sede1 = getDittaFromSopralluogo(target.getAttivita());
					if(sede1!=null){
						PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
						pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
						pgs.setCreationDate(new Date());

						pgs.setVigilanza(vig);
						pgs.setSede(sede1);
						pgs.setPersonaGiuridica(sede1.getPersonaGiuridica());
						vig.addPersonaGiuridicaSede(pgs);
						saveOnTarget(pgs);

						PraticheRiferimenti refs = target.getPraticheRiferimenti();
						if(refs==null){
							refs=new PraticheRiferimenti();
							refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
							refs.setCreationDate(new Date());

						}
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setRiferimentoSede(sede1);
						refs.setRiferimentoDitta(sede1.getPersonaGiuridica());
						saveOnTarget(refs);
						target.setPraticheRiferimenti(refs);
					}

					PrevnetNotes pnDitta = target.getPrevnetNotes();
					addToPrevnetNotesParere(pnDitta, source);
					saveOnTarget(pnDitta);

					break;
				case 48:
					vig.setType(getCodeValue("phidic.spisal.segnalazioni.supervision.types.asbestos"));
					target.setNumero(target.getNumero().replace("VI_", "VI_AM_"));

					vig.setTipoSegnalazione((CodeValuePhi)getMappedCode(source.getTipoPratica(), ulss));//NOTIFICA EX ART 250 o PIANI DI LAVORO EX ART 256

					if(source.getPianidilavoroamiantos()!=null && !source.getPianidilavoroamiantos().isEmpty()){
						Pianidilavoroamianto piano = source.getPianidilavoroamiantos().get(0);

						if(piano.getCantiere()!=null){
							Cantiere cant = cantImp.importCantiere(piano.getCantiere());
							vig.setCantiere(cant);
						}

						if(piano.getDatainizio()!=null){
							vig.setPresuntoInizioLavori(piano.getDatainizio());
							Calendar cal = Calendar.getInstance();
							cal.setTime(piano.getDatainizio());
							if(piano.getDuratagg()!=null){
								cal.add(Calendar.DAY_OF_YEAR, piano.getDuratagg().intValue());
								vig.setEffettivoFineLavori(cal.getTime());
								vig.setEffettivoDurataLavori(piano.getDuratagg().toPlainString());
							}
						}else if(piano.getDuratagg()!=null){
							vig.setEffettivoDurataLavori(piano.getDuratagg().toPlainString());
						}

						//COMMITTENTE OGGETTO
						if(piano.getDittaCommittente()!=null && piano.getTipoanagcommit()!=null){
							switch (piano.getTipoanagcommit().intValue()) {
							case 0:
								Sedi dittaCommittente = ditteImp.importDittaCompleta(piano.getDittaCommittente());
								if(dittaCommittente!=null){
									CommittenteVigilanza commVig =  new CommittenteVigilanza();
									commVig.setCreatedBy(this.getClass().getSimpleName()+ulss);
									commVig.setCreationDate(new Date());
									commVig.setCommittente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
									commVig.setCommittenteDitta(dittaCommittente.getPersonaGiuridica());
									commVig.setCommittenteSede(dittaCommittente);
									commVig.setVigilanza(vig);
									saveOnTarget(commVig);
								}
								break;
							case 4:
								Person personCommittente = persImp.importPerson(piano.getDittaCommittente());
								if(personCommittente!=null){
									CommittenteVigilanza commVig =  new CommittenteVigilanza();
									commVig.setCreatedBy(this.getClass().getSimpleName()+ulss);
									commVig.setCreationDate(new Date());
									commVig.setCommittente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
									commVig.setCommittenteUtente(personCommittente);
									commVig.setVigilanza(vig);
									saveOnTarget(commVig);
								}
								break;
							default:
								break;
							}
						}

						//COMMMITTENTE TESTUALE
						/*if(piano.getDittaCommittente()!=null && piano.getTipoanagcommit()!=null){
							switch (piano.getTipoanagcommit().intValue()) {
							case 0:
								Sedi dittaCommittente = ditteImp.importDittaCompleta(piano.getDittaCommittente());
								if(dittaCommittente!=null)
									vig.setCommittente(dittaCommittente.getDenominazioneUnitaLocale());
								break;
							case 4:
								Person personCommittente = persImp.importPerson(piano.getDittaCommittente());
								if(personCommittente!=null && personCommittente.getName()!=null && personCommittente.getName().getGiv()!=null && personCommittente.getName().getFam()!=null)
									vig.setCommittente(personCommittente.getName().getGiv()+" "+personCommittente.getName().getFam());

								break;
							default:
								break;
							}
						}*/

						if(piano.getDescrlavoro()!=null){
							String descrizione = piano.getDescrlavoro();
							int length = descrizione.length();
							descrizione = descrizione.substring(0,length<=4000?length:4000);
							vig.setDescrizione(descrizione);
						}

						if(piano.getQuantita()!=null && piano.getUnitam()!=null){
							CodeValue um = getMappedCode(piano.getUnitam(), ulss);
							if(um!=null && "Kg".equals(um.getCode())){
								vig.setBonificatiKgEffettivi(piano.getQuantita().doubleValue());
							}else if(um!=null && "mq".equals(um.getCode())){
								vig.setBonificatiMqEffettivi(piano.getQuantita().doubleValue());
							}
						}

						if(piano.getNumlavoratori()!=null){
							vig.setNumLavoratori(piano.getNumlavoratori().intValue());
						}

						if("0".equals(piano.getTipoamianto())){
							vig.setFriabile(true);
						}else if("1".equals(piano.getTipoamianto())){
							vig.setCompatto(true);
						}

						vig.setTipoIntervento((CodeValuePhi)getMappedCode(piano.getTipoLavoro(), ulss));
						vig.setTipoConfinamento((CodeValuePhi)getMappedCode(piano.getTipoEdificio(), ulss));

						//importato in Altro->Altro...sempre che Protocollo.ubicazione.code = 'altro'...
						if(piano.getUbicazionelavoro()!=null) {
							vig.setEntita(getCodeValue("phidic.spisal.segnalazioni.entity.altro"));


							vig.setUbicazione(piano.getUbicazionelavoro());
							if(piano.getUbicazioneComune()!=null) {
								AD addr = new AD();
								manageAddrComune(addr, piano.getUbicazioneComune());
								vig.setAddr(addr);
							}							
						}

						//questo andrebbe importato in Altro->Altro ma sono N per piano di lavoro..
						/*if(piano.getUbicazioniamiantos()!=null && !piano.getUbicazioniamiantos().isEmpty()){
							Ubicazioniamianto ubic = piano.getUbicazioniamiantos().get(0);
						}*/

						// I00073535 
						//if("050118".equals(ulss) || "050119".equals(ulss)) {
						PrevnetNotes pn = target.getPrevnetNotes();
						addToPrevnetNotes(pn, piano);
						saveOnTarget(pn);
						//}
					}

					//ultimo tentativo: se non c'è un piano di lavoro cerco tra i sopralluoghi il primo con un cantiere
					if(vig.getCantiere()==null){
						vig.setCantiere(getCantiereFromSopralluogo(target.getAttivita()));
					}

					if(vig.getCantiere()!=null){
						if(source.getPianidilavoroamiantos()!=null && !source.getPianidilavoroamiantos().isEmpty()){
							Pianidilavoroamianto piano = source.getPianidilavoroamiantos().get(0);
							if(piano.getDittaCommittente()!=null && piano.getTipoanagcommit()!=null){
								boolean found = false;
								switch (piano.getTipoanagcommit().intValue()) {
								case 0:
									Sedi dittaCommittente = ditteImp.importDittaCompleta(piano.getDittaCommittente());

									if(dittaCommittente!=null && dittaCommittente.getPersonaGiuridica()!=null 
											&& vig.getCantiere().getCommittente()!=null){

										for(Committente comm : vig.getCantiere().getCommittente()){
											if(comm.getPersoneGiuridiche()!=null && comm.getPersoneGiuridiche().getInternalId()==
													dittaCommittente.getPersonaGiuridica().getInternalId()){
												found=true;
												break;
											}
										}
									}
									if(!found){
										Committente comm = importCommittente(vig.getCantiere(), piano.getDittaCommittente());
									}
									break;

								case 4:
									Person personCommittente = persImp.importPerson(piano.getDittaCommittente());
									if(personCommittente!=null && vig.getCantiere().getCommittente()!=null){
										for(Committente comm : vig.getCantiere().getCommittente()){
											if(comm.getPerson()!=null && comm.getPerson().getInternalId()==
													personCommittente.getInternalId()){
												found=true;
												break;
											}
										}
									}
									if(!found){
										Committente comm = importCommittente(vig.getCantiere(), piano.getDittaCommittente());
									}
									break;

								default:
									break;
								}								
							}
						}
						//targetEm.refresh(vig.getCantiere());


						PraticheRiferimenti refs = target.getPraticheRiferimenti();
						if(refs==null){
							refs=new PraticheRiferimenti();
							refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
							refs.setCreationDate(new Date());

						}
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
						refs.setRiferimentoCantiere(vig.getCantiere());
						refs.addProcpratiche(target);
						target.setPraticheRiferimenti(refs);
						saveOnTarget(refs);

						//CONCORDATO TELEFONICAMENTE CON SCATTO IL 28/02/2016
						if(target.getProtocollo()!=null && !target.getProtocollo().isEmpty()){
							Protocollo prot = target.getProtocollo().get(0);
							if(prot.getRiferimentoCantiere()==null){
								prot.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
								prot.setRiferimentoCantiere(vig.getCantiere());
								saveOnTarget(prot);
							}
						}
					}

					/*if(source.getRestamiantos()!=null && !source.getRestamiantos().isEmpty()){
						Restamianto rest = source.getRestamiantos().get(0);

					}*/

					PrevnetNotes pnAsbestos = target.getPrevnetNotes();
					addToPrevnetNotesParere(pnAsbestos, source);
					saveOnTarget(pnAsbestos);

					break;
				case 53:
					vig.setType(getCodeValue("phidic.spisal.segnalazioni.supervision.types.yard"));
					target.setNumero(target.getNumero().replace("VI_", "VI_CA_"));

					//source.getCantieris() è sempre vuoto per la pratica di tipo 53
					vig.setCantiere(getCantiereFromSopralluogo(target.getAttivita()));
					if(vig.getCantiere()!=null){
						PraticheRiferimenti refs = target.getPraticheRiferimenti();
						if(refs==null){
							refs=new PraticheRiferimenti();
							refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
							refs.setCreationDate(new Date());

						}
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
						refs.setRiferimentoCantiere(vig.getCantiere());
						saveOnTarget(refs);
						target.setPraticheRiferimenti(refs);

						//CONCORDATO TELEFONICAMENTE CON SCATTO IL 28/02/2016
						if(target.getProtocollo()!=null && !target.getProtocollo().isEmpty()){
							Protocollo prot = target.getProtocollo().get(0);
							if(prot.getRiferimentoCantiere()==null){
								prot.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.cantiere"));
								prot.setRiferimentoCantiere(vig.getCantiere());
								saveOnTarget(prot);
							}
						}

						//DITTE CONTROLLATE: PRESE DA CANTIERE DEL SOPRALLUOGO
						if(vig.getCantiere().getDitteCantiere()!=null){
							for(DitteCantiere dc : vig.getCantiere().getDitteCantiere()){

								PersoneGiuridiche sedeCant = dc.getPersoneGiuridiche();
								if(sedeCant!=null){
									PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
									pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
									pgs.setCreationDate(new Date());
									pgs.setTipoDitta(dc.getRuolo());
									pgs.setVigilanza(vig);
									pgs.setPersonaGiuridica(sedeCant);
									vig.addPersonaGiuridicaSede(pgs);
									pgs.setChecked(true);
									saveOnTarget(pgs);
								}
							}
						}

						addToPrevnetNotes(target.getPrevnetNotes(), vig.getCantiere());
					}

					PrevnetNotes pnCantiere2 = target.getPrevnetNotes();
					addToPrevnetNotesParere(pnCantiere2, source);
					saveOnTarget(pnCantiere2);

					break;
				case 173:
					vig.setType(getCodeValue("phidic.spisal.segnalazioni.supervision.types.generic"));
					target.setNumero(target.getNumero().replace("VI_", "VI_AZ_"));

					Sedi sede2 = getDittaFromSopralluogo(target.getAttivita());
					if(sede2!=null){
						PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
						pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
						pgs.setCreationDate(new Date());

						pgs.setVigilanza(vig);
						pgs.setSede(sede2);
						pgs.setPersonaGiuridica(sede2.getPersonaGiuridica());
						vig.addPersonaGiuridicaSede(pgs);
						saveOnTarget(pgs);

						PraticheRiferimenti refs = target.getPraticheRiferimenti();
						if(refs==null){
							refs=new PraticheRiferimenti();
							refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
							refs.setCreationDate(new Date());

						}
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setRiferimentoSede(sede2);
						refs.setRiferimentoDitta(sede2.getPersonaGiuridica());
						saveOnTarget(refs);
						target.setPraticheRiferimenti(refs);
					}

					PrevnetNotes pnDitta2 = target.getPrevnetNotes();
					addToPrevnetNotesParere(pnDitta2, source);
					saveOnTarget(pnDitta2);

					break;
				default:
					break;
				}
			}

			if(vig!=null) {
				vig.setProcpratiche(target);
				saveOnTarget(vig);

				//DITTA LAVORI
				if(source.getDitta()!=null){
					Sedi sede = ditteImp.importDittaCompleta(source.getDitta());
					if(sede!=null){
						PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
						pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
						pgs.setCreationDate(new Date());

						pgs.setVigilanza(vig);
						pgs.setSede(sede);
						pgs.setPersonaGiuridica(sede.getPersonaGiuridica());
						vig.addPersonaGiuridicaSede(pgs);
						saveOnTarget(pgs);
					}
				}else if(source.getPianidilavoroamiantos()!=null && !source.getPianidilavoroamiantos().isEmpty()){
					Pianidilavoroamianto piano = source.getPianidilavoroamiantos().get(0);
					//DITTA LAVORO AMIANTO
					if(piano.getSchedaDittaLavoro()!=null){
						Sedi sede = ditteImp.importDittaCompleta(piano.getSchedaDittaLavoro());
						if(sede!=null){
							PersonaGiuridicaSede pgs = new PersonaGiuridicaSede();
							pgs.setCreatedBy(this.getClass().getSimpleName()+ulss);
							pgs.setCreationDate(new Date());

							pgs.setVigilanza(vig);
							pgs.setSede(sede);
							pgs.setPersonaGiuridica(sede.getPersonaGiuridica());
							vig.addPersonaGiuridicaSede(pgs);
							saveOnTarget(pgs);
						}
					}					
				}
				//targetEm.refresh(vig);
				manageAteco(vig);
				saveOnTarget(vig);
				target.setVigilanza(vig);
			}


			return target.getVigilanza();
		}else{
			return target.getVigilanza();
		}
	}

	@Override
	protected void deleteImportedData(String ulss) {
		String hqlPratiche = "SELECT mf.idphi FROM MapPratiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPratiche+="WHERE mf.ulss = :ulss";

		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		if(ulss!=null && !ulss.isEmpty())
			qPratiche.setParameter("ulss", ulss);

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
				String hqlVigilanze = "SELECT v.internalId FROM Vigilanza v JOIN v.procpratiche p WHERE p.internalId IN (:ids)";
				Query qVigilanze = targetEm.createQuery(hqlVigilanze);
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
			}
		}
	}

	private Cantiere getCantiereFromSopralluogo(List<Attivita> sopralluoghi){
		if(sopralluoghi==null){
			return null;
		}
		for(Attivita a : sopralluoghi){
			if(a.getCode()!=null && "sopralluogo".equals(a.getCode().getCode()) && a.getSoggetto()!=null && !a.getSoggetto().isEmpty()){
				for(Soggetto s : a.getSoggetto()){
					if(s.getCantiere()!=null)
						return s.getCantiere();
				}
			}
		}

		for(Attivita a : sopralluoghi){
			if(a.getSoggetto()!=null && !a.getSoggetto().isEmpty()){
				for(Soggetto s : a.getSoggetto()){
					if(s.getCantiere()!=null)
						return s.getCantiere();
				}
			}
		}

		for(Attivita a : sopralluoghi){
			if(a.getCode()!=null && "sopralluogo".equals(a.getCode().getCode()) && a.getLuogoCantiere()!=null){
				return a.getLuogoCantiere();
			}
		}

		for(Attivita a : sopralluoghi){
			if(a.getCode()!=null && a.getLuogoCantiere()!=null){
				return a.getLuogoCantiere();
			}
		}

		return null;
	}

	private Sedi getDittaFromSopralluogo(List<Attivita> sopralluoghi){
		if(sopralluoghi==null){
			return null;
		}
		if(sopralluoghi!=null){
			for(Attivita a : sopralluoghi){
				if(a.getCode()!=null && "sopralluogo".equals(a.getCode().getCode()) && a.getSoggetto()!=null && !a.getSoggetto().isEmpty()){
					for(Soggetto s : a.getSoggetto()){
						if(s.getSede()!=null)
							return s.getSede();
					}
				}
			}

			for(Attivita a : sopralluoghi){
				if(a.getSoggetto()!=null && !a.getSoggetto().isEmpty()){
					for(Soggetto s : a.getSoggetto()){
						if(s.getSede()!=null)
							return s.getSede();
					}
				}
			}
		}

		for(Attivita a : sopralluoghi){
			if(a.getCode()!=null && "sopralluogo".equals(a.getCode().getCode()) && a.getLuogoSede()!=null){
				return a.getLuogoSede();
			}
		}

		for(Attivita a : sopralluoghi){
			if(a.getCode()!=null && a.getLuogoSede()!=null){
				return a.getLuogoSede();
			}
		}

		return null;
	}

	private Committente importCommittente(Cantiere target, Anagrafiche anagCommittente){
		PersoneFisicheImporter personeImporter = PersoneFisicheImporter.getInstance();
		PersoneGiuridicheImporter ditteImporter = PersoneGiuridicheImporter.getInstance();

		//COMMITTENTE
		if(anagCommittente!=null && anagCommittente.getTipo()!=null){
			Committente comm = new Committente();
			comm.setCreatedBy(this.getClass().getSimpleName()+ulss);
			comm.setCreationDate(target.getCreationDate());
			Sedi sede = null;
			switch (anagCommittente.getTipo().intValue()) {

			//DITTA
			case 0:

				Query q1 = sourceEm.createQuery("SELECT d FROM Ditte d JOIN d.anagrafica a WHERE a.idanagrafiche = :id");
				q1.setParameter("id", anagCommittente.getIdanagrafiche());
				List<Ditte> list1 = q1.getResultList();
				if(list1!=null && !list1.isEmpty()){
					Ditte ditta = list1.get(0);
					sede = ditteImporter.importDittaCompleta(ditta);
				}
				if(sede!=null && sede.getPersonaGiuridica()!=null){
					comm.setPersoneGiuridiche(sede.getPersonaGiuridica());
					comm.setCantiere(target);
				}

				break;

				//CANTIERE -> GUARDO IL RIFERIMENTO
			case 3:
				if(anagCommittente.getRiferimento()!=null){
					Query q2 = sourceEm.createQuery("SELECT d FROM Ditte d JOIN d.anagrafica a WHERE a.idanagrafiche = :id");
					q2.setParameter("id", anagCommittente.getRiferimento().getIdanagrafiche());
					List<Ditte> list2 = q2.getResultList();
					if(list2!=null && !list2.isEmpty()){
						Ditte ditta = list2.get(0);
						sede = ditteImporter.importDittaCompleta(ditta);
					}
				}
				if(sede!=null && sede.getPersonaGiuridica()!=null){
					comm.setPersoneGiuridiche(sede.getPersonaGiuridica());
					comm.setCantiere(target);
				}
				break;

				//PERSONA FISICA
			case 4:
				Query q3 = sourceEm.createQuery("SELECT u FROM Utenti u WHERE u.idanagrafica = :id");
				q3.setParameter("id", anagCommittente.getIdanagrafiche());
				List<Utenti> list3 = q3.getResultList();
				if(list3!=null && !list3.isEmpty()){
					Utenti utente = list3.get(0);
					Person persona = personeImporter.importPerson(utente);
					if(persona!=null){
						comm.setPerson(persona);
						comm.setCantiere(target);
					}
				}

				break;

			default:
				break;
			}


			saveOnTarget(comm);

			return comm;
		}

		return null;
	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Pianidilavoroamianto piano){
		// I00073535 costruzione note per bypassare automatismi

		if(prevNotes==null || piano==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI BONIFICA\r\n");

		if(piano.getTipoLavoro()!=null){
			constructedNote.append( "Tipo lavoro: " + piano.getTipoLavoro().getDescrizione() + "\r\n");
		}

		if(piano.getUbicazionelavoro()!=null && !"".equals(piano.getUbicazionelavoro())) {
			constructedNote.append( "Ubicazione lavoro: " + piano.getUbicazionelavoro() + "\r\n");
		}

		Anagrafiche trasportatore = piano.getTrasportatore();
		if(trasportatore!=null) {
			constructedNote.append("Trasportatore: " + trasportatore.getDenominazione() + " " + trasportatore.getIndirizzo());
			Comuni comune = trasportatore.getComuni();
			if(comune!=null)
				constructedNote.append(" "+comune.getDescrizione());
			constructedNote.append("\r\n");
		}

		Anagrafiche discarica = piano.getDiscarica();
		if(discarica!=null) {
			constructedNote.append("Discarica: " + discarica.getDenominazione() + " " + discarica.getIndirizzo());
			Comuni comune = discarica.getComuni();
			if(comune!=null)
				constructedNote.append(" "+comune.getDescrizione());
			constructedNote.append("\r\n");
		}

		Date inizioLavori = piano.getDatainizio();
		if(inizioLavori!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Inizio lavori: " + sdf.format(inizioLavori) + "\r\n");
		}

		BigDecimal durataLavori = piano.getDuratagg();
		if(durataLavori!=null) {
			constructedNote.append("Durata lavori: " + durataLavori.intValue() + "\r\n");
		}

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private void manageAteco(Vigilanza vig) {
		if(vig==null)
			return;

		if(vig.getPersonaGiuridicaSede()==null || vig.getPersonaGiuridicaSede().isEmpty())
			return;

		PersonaGiuridicaSede pgs = vig.getPersonaGiuridicaSede().get(0);

		if(pgs.getSede()==null && pgs.getPersonaGiuridica()==null)
			return;

		Long sediId = -1L;
		Long dittaId = -1L;

		if(pgs.getSede()!=null)
			sediId = pgs.getSede().getInternalId();

		if(pgs.getPersonaGiuridica()!=null)
			dittaId = pgs.getPersonaGiuridica().getInternalId();

		queryAtecoP.setParameter("sediId", sediId);

		List<CodeValueAteco> list = queryAtecoP.getResultList();
		if(list!=null && !list.isEmpty()) {
			CodeValueAteco ateco = list.get(0);
			vig.setComparto(ateco);
		}else {
			log.warn("Unable to find CodeValueAteco for dittaId: "+dittaId+" sediId:"+sediId);
		}

		return;
	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Cantiere cantiere){
		// I00090719 note cantiere

		if(prevNotes==null || cantiere==null)
			return;

		if(cantiere.getTagCantiere()==null || cantiere.getTagCantiere().isEmpty())
			return;

		String entita = null;
		for(TagCantiere nota : cantiere.getTagCantiere()) {
			if(nota.getIsActive()==true)
				continue;

			if(nota.getNotes().contains("Entità")) {
				entita = nota.getNotes();
			}
		}

		if(entita==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI CANTIERE\r\n");
		constructedNote.append(entita+"\r\n");

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private void addToPrevnetNotesParere(PrevnetNotes prevNotes, Pratiche source){
		// I00090719 note tecniche punto 2 

		if(prevNotes==null || source==null)
			return;

		if(source.getParerigenericis()==null || source.getParerigenericis().isEmpty())
			return;

		Parerigenerici parerigenici = source.getParerigenericis().get(0);
		String parereEmesso = ("Parere emesso: "+parerigenici.getParere().getDescrizione()+"\r\n");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dataEmissioneParere = ("Data emissione parere: "+sdf.format(parerigenici.getData())+"\r\n");
		String operatoreEmissione = ("Operatore emissione: "+parerigenici.getOperatore().getNome()+" "+parerigenici.getOperatore().getCognome()+"\r\n");

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI PARERE\r\n");
		constructedNote.append(parereEmesso);
		constructedNote.append(dataEmissioneParere);
		constructedNote.append(operatoreEmissione);

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}
}
