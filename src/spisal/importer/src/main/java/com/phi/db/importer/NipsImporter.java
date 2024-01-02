package com.phi.db.importer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.*;
import com.prevnet.entities.Autorizzazionideroga;
import com.prevnet.entities.Richiestaregistrazione;
import com.prevnet.mappings.MapAut;
import org.apache.log4j.Logger;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.prevnet.entities.Lavoratricimadre;
import com.prevnet.entities.Parerigen;
import com.prevnet.mappings.MapLavs;
import com.prevnet.mappings.MapNips;

@SuppressWarnings({"unchecked","unused"})
public class NipsImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(NipsImporter.class.getName());

	private static NipsImporter instance = null;
	
	public static NipsImporter getInstance() {
		if(instance == null) {
			instance = new NipsImporter();
		}
		return instance;
	}
	
	public ParereTecnico importLavoratrice(Lavoratricimadre source, Procpratiche pratica){
		if(!checkMappingLavoratrice(source.getIdlavoratricimadre())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			CantieriImporter cantImp = CantieriImporter.getInstance();
			
			ParereTecnico target = new ParereTecnico();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			target.setProcpratiche(pratica);

			target.setType(getCodeValue("phidic.spisal.segnalazioni.techadvice.type.09"));
			target.setDataParto(source.getDataparto());
			if(source.getParere()!=null){
				String parereText = source.getParere();
				int length = parereText.length();
				parereText = parereText.substring(0,length<=4000?length:4000);
				target.setParereText(parereText);
			}
			
			String richiestaDettaglio = "";
			if(source.getMansione()!=null){
				String mansione = source.getMansione().getDescrizione();
				richiestaDettaglio+="MANSIONE LAVORATRICE\r\n";
				richiestaDettaglio+=(mansione+"\r\n");
			}
			
			if(source.getDescrizionemansione()!=null){
				String descrMansione = source.getDescrizionemansione();
				richiestaDettaglio+="DESCRIZIONE MANSIONE LAVORATRICE\r\n";
				richiestaDettaglio+=(descrMansione+"\r\n");
			}
			
			if(source.getNote()!=null){
				String note = source.getNote();
				richiestaDettaglio+="NOTE\r\n";
				richiestaDettaglio+=(note+"\r\n");
			}

			int length = richiestaDettaglio.length();
			richiestaDettaglio = richiestaDettaglio.substring(0,length<=4000?length:4000);
			
			target.setRichiestaDettaglio(richiestaDettaglio);
			
			if("1".equals(source.getInizioassenzaprec())){
				target.setRichiesta(getCodeValue("phidic.spisal.segnalazioni.techadvice.request.gravidanza.01"));
			}else if("1".equals(source.getInizioassenzasucc())){
				target.setRichiesta(getCodeValue("phidic.spisal.segnalazioni.techadvice.request.gravidanza.02"));
			}
			
			
			saveOnTarget(target);
			saveMappingLavoratrice(source, target);
			
			if(source.getDittaRichiedente()!=null || source.getRichiedente()!=null || source.getUtente()!=null || source.getDitta()!=null){
				PraticheRiferimenti refs = new PraticheRiferimenti();
				refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
				refs.setCreationDate(new Date());
				
				if(source.getDittaRichiedente()!=null){
					Sedi sede = ditteImp.importDittaCompleta(source.getDittaRichiedente());
					if(sede!=null){
						refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setRichiedenteSede(sede);
						refs.setRichiedenteDitta(sede.getPersonaGiuridica());
					}
				}else if(source.getRichiedente()!=null && source.getRichiedente().getTipo()!=null){
					switch (source.getRichiedente().getTipo().intValue()) {
					case 0:
						Sedi sede = ditteImp.importDittaCompleta(source.getRichiedente());
						if(sede!=null){
							refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
							refs.setRichiedenteSede(sede);
							refs.setRichiedenteDitta(sede.getPersonaGiuridica());
						}
						break;
					case 4:
						Person pers = persImp.importPerson(source.getRichiedente());
						if(pers!=null){
							refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
							refs.setRichiedenteUtente(pers);
						}
						break;
					default:
						break;
					}
				}

				if(source.getUtente()!=null){
					Person ric = persImp.importPerson(source.getUtente());
					if(ric!=null){
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
						refs.setRiferimentoUtente(ric);
					}
				}
				
				if(source.getDitta()!=null){
					Sedi sede = ditteImp.importDittaCompleta(source.getDitta());
					if(sede!=null){
						refs.setUbicazione(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setUbicazioneSede(sede);
						refs.setUbicazioneDitta(sede.getPersonaGiuridica());
						
						PersonaGiuridicaSede pss = new PersonaGiuridicaSede();
						pss.setCreatedBy(this.getClass().getSimpleName()+ulss);
						pss.setCreationDate(new Date());
						pss.setType(getCodeValue("phidic.spisal.segnalazioni.techadvice.ditte.03"));//datrice di lavoro
						pss.setSede(sede);
						pss.setPersonaGiuridica(sede.getPersonaGiuridica());
						pss.setParereTecnico(target);
						saveOnTarget(pss);
					}
				}
				
				saveOnTarget(refs);
				pratica.setPraticheRiferimenti(refs);
			}
		}
		
		return getMappedLavoratrice(source.getIdlavoratricimadre());
		
	}
		
	public ParereTecnico importParere(Parerigen source, Procpratiche pratica){
		if(!checkMappingParere(source.getIdparerigen())){
			PersoneFisicheImporter persImp = PersoneFisicheImporter.getInstance();
			PersoneGiuridicheImporter ditteImp = PersoneGiuridicheImporter.getInstance();
			CantieriImporter cantImp = CantieriImporter.getInstance();
			
			ParereTecnico target = new ParereTecnico();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			target.setProcpratiche(pratica);
			
			if(source.getDestinazioneUso()!=null)
				target.setDestinazioneUso(source.getDestinazioneUso().getDescrizione());
			
			target.setParere((CodeValuePhi)getMappedCode(source.getEsito(), ulss));
			target.setParereData(source.getDataemissioneparere());
			
			if(source.getDescrizione()!=null){
				String descrizione = source.getDescrizione();
				int length = descrizione.length();
				descrizione = descrizione.substring(0,length<=4000?length:4000);
				//target.setParereText(descrizione);
				target.setRichiestaDettaglio(descrizione);
			}
			
			if(source.getNumprot()!=null){
				String numprot = source.getNumprot();
				int length = numprot.length();
				numprot = numprot.substring(0,length<=4000?length:4000);
				target.setProtocolloUscita(numprot);
			}
			
			if(source.getNote()!=null){
				String note = source.getNote();
				int length = note.length();
				note = note.substring(0,length<=4000?length:4000);
				//target.setRichiestaDettaglio(note);
				target.setParereText(note);
			}
			
			saveOnTarget(target);
			saveMappingParere(source, target);
			
			if(source.getSoggetto()!=null || source.getDittaSoggetto()!=null || source.getSedeIntervento()!=null){
				PraticheRiferimenti refs = new PraticheRiferimenti();
				refs.setCreatedBy(this.getClass().getSimpleName()+ulss);
				refs.setCreationDate(new Date());
				
				if(source.getDittaSoggetto()!=null){
					Sedi sede = ditteImp.importDittaCompleta(source.getDittaSoggetto());
					if(sede!=null){
						refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setRichiedenteSede(sede);
						refs.setRichiedenteDitta(sede.getPersonaGiuridica());
					}
				}else if(source.getSoggetto()!=null && source.getSoggetto().getTipo()!=null && source.getSoggetto().getTipo().intValue()== 4){
					Person ric = persImp.importPerson(source.getSoggetto());
					if(ric!=null){
						refs.setRichiedente(getCodeValue("phidic.spisal.segnalazioni.targetsource.utente"));
						refs.setRichiedenteUtente(ric);
					}
				}
				
				if(source.getSedeIntervento()!=null){
					Sedi sede = ditteImp.importDittaCompleta(source.getSedeIntervento());
					if(sede!=null){
						refs.setRiferimento(getCodeValue("phidic.spisal.segnalazioni.targetsource.ditta"));
						refs.setRiferimentoSede(sede);
						refs.setRiferimentoDitta(sede.getPersonaGiuridica());
					}
				}
				
				saveOnTarget(refs);
				pratica.setPraticheRiferimenti(refs);
				saveOnTarget(pratica);
			}
			
			if(source.getDittaStudio()!=null){
				PersonaGiuridicaSede pss = new PersonaGiuridicaSede();
				pss.setCreatedBy(this.getClass().getSimpleName()+ulss);
				pss.setCreationDate(new Date());
				Sedi sede = ditteImp.importDittaCompleta(source.getDittaStudio());

				if(sede!=null){
					
					pss.setType(getCodeValue("phidic.spisal.segnalazioni.techadvice.ditte.01"));//studio tecnico
					pss.setSede(sede);
					pss.setPersonaGiuridica(sede.getPersonaGiuridica());
					
				}
				
				pss.setParereTecnico(target);
				saveOnTarget(pss);
			}
		}
		
		return getMappedParere(source.getIdparerigen());
		
	}	

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingParere(long id){
		MapNips m = sourceEm.find(MapNips.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapNips m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapNips> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapNips map = list.get(0);
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
	private ParereTecnico getMappedParere(long id){
		String hqlMapping = "SELECT m FROM MapNips m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapNips> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapNips map = list.get(0);
				Query qNips = targetEm.createQuery("SELECT p FROM ParereTecnico p WHERE p.internalId = :id");
				qNips.setParameter("id", map.getIdphi());
				List<ParereTecnico> lp = qNips.getResultList();
				if(lp!=null && !lp.isEmpty()){
					return lp.get(0);
				}
		}
		return null;
	}
	
	private void saveMappingParere(Parerigen source, ParereTecnico target){
		MapNips map = new MapNips();
		map.setIdprevnet(source.getIdparerigen());
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
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingLavoratrice(long id){
		MapLavs m = sourceEm.find(MapLavs.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapLavs m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapLavs> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapLavs map = list.get(0);
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
	private ParereTecnico getMappedLavoratrice(long id){
		String hqlMapping = "SELECT m FROM MapLavs m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapLavs> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapLavs map = list.get(0);
				Query qLavs = targetEm.createQuery("SELECT p FROM ParereTecnico p WHERE p.internalId = :id");
				qLavs.setParameter("id", map.getIdphi());
				List<ParereTecnico> lp = qLavs.getResultList();
				if(lp!=null && !lp.isEmpty()){
					return lp.get(0);
				}
		}
		return null;
	}
	
	private void saveMappingLavoratrice(Lavoratricimadre source, ParereTecnico target){
		MapLavs map = new MapLavs();
		map.setIdprevnet(source.getIdlavoratricimadre());
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
		String hqlPareri = "SELECT mf.idphi FROM MapNips mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPareri+="WHERE mf.ulss = :ulss";
		
		Query qNips = sourceEm.createQuery(hqlPareri);
		if(ulss!=null && !ulss.isEmpty())
			qNips.setParameter("ulss", ulss);
		
		List<Long> allIdNip = qNips.getResultList();
		List<Long> idNip = new ArrayList<Long>();
		while(allIdNip!=null && !allIdNip.isEmpty()){
			if(allIdNip.size()>1000){
				idNip.clear();
				idNip.addAll(allIdNip.subList(0, 1000));
				allIdNip.removeAll(idNip);
			}else{
				idNip.clear();
				idNip.addAll(allIdNip);
				allIdNip.removeAll(idNip);
			}
			if(commit){
				//SGANCIO I FATTORI DI RISCHIO
				String updateParere = "DELETE FROM persona_giuridica_sede WHERE parere_tecnico_id IN (:ids)";
				targetEm.getTransaction().begin();
				Query qUpdPrat = targetEm.createNativeQuery(updateParere);
				qUpdPrat.setParameter("ids", idNip);
				qUpdPrat.executeUpdate();
				targetEm.getTransaction().commit();
				
				String deleteNip = "DELETE FROM ParereTecnico e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelMal = targetEm.createQuery(deleteNip);
				qDelMal.setParameter("ids", idNip);
				qDelMal.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_NIPS WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}

	public ParereTecnico importAutorizzazione(Autorizzazionideroga source, Procpratiche pratica){
		if(!checkMappingAutorizzazione(source.getIdautorizzazionideroga())){

			ParereTecnico target = new ParereTecnico();
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(new Date());
			target.setProcpratiche(pratica);

			target.setType(getCodeValue("phidic.spisal.segnalazioni.techadvice.type.06"));


			PrevnetNotes prevnetNotes = pratica.getPrevnetNotes();
			addToPrevnetNotes(prevnetNotes, source);

			saveOnTarget(target);
			saveMappingAutorizzazione(source, target);

		}

		return getMappedAutorizzazione(source.getIdautorizzazionideroga());

	}

	private boolean checkMappingAutorizzazione(long id){
		MapAut m = sourceEm.find(MapAut.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapAut m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapAut> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapAut map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Autorizzazionideroga autorizzazionideroga){

		if(prevNotes==null || autorizzazionideroga==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI AUTORIZZAZIONE IN DEROGA\r\n");

		if(autorizzazionideroga.getTabelle1()!=null){
			constructedNote.append("Tipo Autorizzazione: " + autorizzazionideroga.getTabelle1().getDescrizione()+"\r\n");
		}
		if(autorizzazionideroga.getNumero()!=null && !autorizzazionideroga.getNumero().isEmpty()){
			constructedNote.append("Numero Autorizzazione: " + autorizzazionideroga.getNumero()+"\r\n");
		}
		if(autorizzazionideroga.getDataautorizzazione()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			constructedNote.append("Data Autorizzazione: " + sdf.format(autorizzazionideroga.getDataautorizzazione())+"\r\n");
		}
		if(autorizzazionideroga.getDefinitivo()!=null && !autorizzazionideroga.getDefinitivo().isEmpty()){
			constructedNote.append("Durata Autorizzazione: ");
			if("0".equals(autorizzazionideroga.getDefinitivo())){
				constructedNote.append(" Temporanea  ");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				if(autorizzazionideroga.getDatainizio()!=null){
					constructedNote.append("dal: " + sdf.format(autorizzazionideroga.getDatainizio()));
				}
				if(autorizzazionideroga.getDatafine()!=null){
					constructedNote.append(" al: " + sdf.format(autorizzazionideroga.getDatafine()));
				}
			}
			if("1".equals(autorizzazionideroga.getDefinitivo())){
				constructedNote.append("Definitiva");
			}
			constructedNote.append("\r\n");
		}
		if(autorizzazionideroga.getNote()!=null && !autorizzazionideroga.getNote().isEmpty()){
			constructedNote.append("Note: " + autorizzazionideroga.getNote()+"\r\n");
		}

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private void addToPrevnetNotes(PrevnetNotes prevNotes, Richiestaregistrazione req){

		if(prevNotes==null || req==null)
			return;

		StringBuffer constructedNote = new StringBuffer();
		constructedNote.append("DATI AUTORIZZAZIONE IN DEROGA\r\n");

		if(req.getDescrrichiesta()!=null && !req.getDescrrichiesta().isEmpty()) {
			constructedNote.append("Descrizione richiesta: " + req.getDescrrichiesta() + "\r\n");
		}

		String pNote = prevNotes.getNote();
		if(pNote==null)
			pNote="";

		pNote+=constructedNote;
		prevNotes.setNote(pNote);
	}

	private void saveMappingAutorizzazione(Autorizzazionideroga source, ParereTecnico target){
		MapAut map = new MapAut();
		map.setIdprevnet(source.getIdautorizzazionideroga());
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

	private ParereTecnico getMappedAutorizzazione(long id){
		String hqlMapping = "SELECT m FROM MapAut m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapAut> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapAut map = list.get(0);
			Query qAuts = targetEm.createQuery("SELECT p FROM ParereTecnico p WHERE p.internalId = :id");
			qAuts.setParameter("id", map.getIdphi());
			List<ParereTecnico> lp = qAuts.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		return null;
	}

}
