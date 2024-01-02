package com.phi.db.importer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.baseEntity.PersoneCantiere;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.TagCantiere;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.role.Person;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.Cantieri;
import com.prevnet.entities.Committenticantiere;
import com.prevnet.entities.Ditte;
import com.prevnet.entities.Impresecantiere;
import com.prevnet.entities.Utenti;
import com.prevnet.mappings.MapCantieri;

@SuppressWarnings({"unchecked"})
public class CantieriImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(CantieriImporter.class.getName());

	private static CantieriImporter instance = null;
	
	public static CantieriImporter getInstance() {
		if(instance == null) {
			instance = new CantieriImporter();
		}
		return instance;
	}

	public Cantiere importCantiere(Cantieri source){
		if(!checkMapping(source.getIdcantieri())){
			importNewCantiere(source); 

			/**
			 * SE il cantiere è già stato mappato, allora ritorna il corrispondente
			 */
		}else{
			//return getMapped(source.getIdcantieri());
		}

		return getMapped(source.getIdcantieri());
	}
	
	/**
	 * Data l'anagrafica in source ritorna il cantiere corrispondente in target
	 * @param source
	 * @return
	 */
	public Cantiere importCantiere(Anagrafiche source){
		//controllo che sia una cantiere
		if(source!=null && source.getTipo()!=null && source.getTipo().intValue()==3){
			Query qCantiere = sourceEm.createQuery("SELECT c FROM Cantieri c WHERE c.anagrafica.idanagrafiche = :id");
			qCantiere.setParameter("id", source.getIdanagrafiche());
			List<Cantieri> list = qCantiere.getResultList();
			if(list!=null && !list.isEmpty()){
				return importCantiere(list.get(0));
			}
		}
			
		return null;
	}

	public Cantiere importNewCantiere(Cantieri source){
		Cantiere target = new Cantiere();
		target.setNotifica(false);
		target.setCreatedBy(this.getClass().getSimpleName()+ulss);
		target.setCreationDate(source.getTimestampinsmod());

		// INDIRIZZO E DENOMINAZIONE

		if(source.getAnagrafica()!=null){
			Anagrafiche a = source.getAnagrafica();
			//target.setName(a.getDenominazione());
			if(target.getAddr()==null)
				target.setAddr(new AD());

			AD addr = target.getAddr();
			addr.setStr(a.getIndirizzo());
			if(a.getLocalita()!=null)
				addr.setAdl(a.getLocalita().getDescrlocalita());

			manageAddrComune(addr, a.getComuni());
			
			target.setId(a.getCodice());
		}

		//ALTRE PROPRIETA VARIE

		//target.setNaturaOpera((CodeValuePhi)getMappedCode(source.getNaturaOpera(), ulss));
		String natura = null;
		if(source.getNaturaOpera()!=null && source.getNaturaOpera().getDescrizione()!=null){
			int length = source.getNaturaOpera().getDescrizione().length();
			natura = source.getNaturaOpera().getDescrizione().substring(0,length<=1000?length:1000);
		}else if(source.getCategoriaOpera()!=null && source.getCategoriaOpera().getDescrizione()!=null){
			int length = source.getCategoriaOpera().getDescrizione().length();
			natura = source.getCategoriaOpera().getDescrizione().substring(0,length<=1000?length:1000);
		}else if(source.getAnagrafica()!=null && source.getAnagrafica().getDenominazione()!=null){
			int length = source.getAnagrafica().getDenominazione().length();
			natura = source.getAnagrafica().getDenominazione().substring(0,length<=1000?length:1000);
		}
		target.setNaturaOpera(natura);
		
		target.setDataComunicazione(source.getDatanotifica());
		target.setInizioLavori(source.getDatainizio());
		if(source.getDuratalavori()!=null)
			target.setDurataLavori(source.getDuratalavori().intValue());

		target.setFineLavori(source.getDatafine());
		if(source.getNumlavoratori()!=null)
			target.setMaxWorkers(source.getNumlavoratori().intValue());

		if(source.getNumlavoratoriauto()!=null)
			target.setNumeroAutonomi(source.getNumlavoratoriauto().intValue());

		if(source.getNumimprese()!=null)
			target.setNumeroImprese(source.getNumimprese().intValue());

		if(source.getAmmontlavori()!=null)
			target.setCost(source.getAmmontlavori().doubleValue());

		saveOnTarget(target);
		
		//ASSOCIAZIONI
		importCommittenti(source,target);

		importPersoneCantiere(source, target);

		importDitteCantiere(source, target);
		
		importEntita(source, target);
		
		saveOnTarget(target);
		saveMapping(source, target);
		
		return target;
	}

	private void importEntita(Cantieri source, Cantiere target) {
		// I00090719 importazione cantieri.entita per note pregresso
		List<TagCantiere> list = new ArrayList<TagCantiere>();

		BigDecimal entita = source.getEntita();

		if(entita!=null) {
			TagCantiere note = new TagCantiere();
			
			note.setCreatedBy(this.getClass().getSimpleName()+ulss);
			note.setCreationDate(source.getTimestampinsmod());
			note.setIsActive(false);
			note.setStartValidity(new Date());
			note.setEndValidity(new Date());
			note.setCantiere(target);

			note.setNotes("Entità: "+entita.intValue());
			
			saveOnTarget(note);
			
			list.add(note);
			target.setTagCantiere(list);			
		}
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapCantieri m = sourceEm.find(MapCantieri.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapCantieri m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapCantieri> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapCantieri map = list.get(0);
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
	private Cantiere getMapped(long id){
		MapCantieri map = sourceEm.find(MapCantieri.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapCantieri m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapCantieri> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Cantiere c = targetEm.find(Cantiere.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qCantiere = targetEm.createQuery("SELECT p FROM Cantiere p WHERE p.internalId = :id");
			qCantiere.setParameter("id", map.getIdphi());
			List<Cantiere> lp = qCantiere.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}

	private void importDitteCantiere(Cantieri source, Cantiere target){
		PersoneGiuridicheImporter ditteImporter = PersoneGiuridicheImporter.getInstance();
		List<DitteCantiere> list = new ArrayList<DitteCantiere>();

		//AFFIDATARIA (=CAPOCOMMESSA)
		if(source.getDittaCapoCom()!=null){
			DitteCantiere d = new DitteCantiere();
			d.setCreatedBy(this.getClass().getSimpleName()+ulss);
			d.setCreationDate(source.getTimestampinsmod());

			Sedi sede = ditteImporter.importDittaCompleta(source.getDittaCapoCom());
			if(sede!=null && sede.getPersonaGiuridica()!=null){
				d.setPersoneGiuridiche(sede.getPersonaGiuridica());
				d.setCantiere(target);
				d.setRuolo(getCodeValue("phidic.spisal.cantieri.ruoloimp.1"));
				
				saveOnTarget(d);
				
				list.add(d);
			}
		}

		//DITTA ESECUTRICE
		if(source.getDittaEsecLavori()!=null){
			DitteCantiere d = new DitteCantiere();
			d.setCreatedBy(this.getClass().getSimpleName()+ulss);
			d.setCreationDate(source.getTimestampinsmod());

			Sedi sede = ditteImporter.importDittaCompleta(source.getDittaEsecLavori());
			if(sede!=null && sede.getPersonaGiuridica()!=null){
				d.setPersoneGiuridiche(sede.getPersonaGiuridica());
				d.setCantiere(target);
				d.setRuolo(getCodeValue("phidic.spisal.cantieri.ruoloimp.2"));
				
				saveOnTarget(d);
				
				list.add(d);
			}
		}

		if(source.getDitteCantiere()!=null){
			for(Impresecantiere imp : source.getDitteCantiere()){
				DitteCantiere d = new DitteCantiere();
				d.setCreatedBy(this.getClass().getSimpleName()+ulss);
				d.setCreationDate(source.getTimestampinsmod());

				Sedi sede = ditteImporter.importDittaCompleta(imp.getImpresa());
				if(sede!=null && sede.getPersonaGiuridica()!=null){
					d.setPersoneGiuridiche(sede.getPersonaGiuridica());
					d.setCantiere(target);
					if("1".equals(imp.getEseclavori())){
						d.setRuolo(getCodeValue("phidic.spisal.cantieri.ruoloimp.2"));	//DITTA ESECUTRICE
					}else{
						d.setRuolo(getCodeValue("phidic.spisal.cantieri.ruoloimp.5"));	//DITTA PARTECIPE
					}

					saveOnTarget(d);
					
					list.add(d);
				}
			}
		}

		target.setDitteCantiere(list);
	}

	private void importPersoneCantiere(Cantieri source, Cantiere target){
		PersoneFisicheImporter personeImporter = PersoneFisicheImporter.getInstance();
		List<PersoneCantiere> list = new ArrayList<PersoneCantiere>();

		//RESPONSABILE LAVORI
		if(source.getResponsabileLavori()!=null){
			PersoneCantiere p = new PersoneCantiere();
			p.setCreatedBy(this.getClass().getSimpleName()+ulss);
			p.setCreationDate(source.getTimestampinsmod());

			Person persona = personeImporter.importPerson(source.getResponsabileLavori());
			if(persona!=null){
				p.setPerson(persona);
				p.setCantiere(target);
				p.setRuolo(getCodeValue("phidic.spisal.cantieri.ruolo.01"));
				
				saveOnTarget(p);
				
				list.add(p);
			}
		}

		//COORDINATORE DEL PROGETTO
		if(source.getCoordinatoreProgetto()!=null){
			PersoneCantiere p = new PersoneCantiere();
			p.setCreatedBy(this.getClass().getSimpleName()+ulss);
			p.setCreationDate(source.getTimestampinsmod());

			Person persona = personeImporter.importPerson(source.getCoordinatoreProgetto());
			if(persona!=null){
				p.setPerson(persona);
				p.setCantiere(target);
				p.setRuolo(getCodeValue("phidic.spisal.cantieri.ruolo.02"));

				saveOnTarget(p);

				list.add(p);
			}
		}

		//COORDINATORE ESECUZIONE
		if(source.getCoordinatoreEsecuzione()!=null){
			PersoneCantiere p = new PersoneCantiere();
			p.setCreatedBy(this.getClass().getSimpleName()+ulss);
			p.setCreationDate(source.getTimestampinsmod());

			Person persona = personeImporter.importPerson(source.getCoordinatoreEsecuzione());
			if(persona!=null){
				p.setPerson(persona);
				p.setCantiere(target);
				p.setRuolo(getCodeValue("phidic.spisal.cantieri.ruolo.03"));

				saveOnTarget(p);
				
				list.add(p);
			}
		}

		target.setPersoneCantiere(list);
	}

	private void importCommittenti(Cantieri source, Cantiere target){
		
		List<Committente> list = new ArrayList<Committente>();
		
		//COMMITTENTI
		
		//Questo non sembra servire, dato che è sempre incluso in source.committenti...
		/*Committente primoCommittente = importCommittente(source, target, source.getAnagCommittente());
		if(primoCommittente!=null)
			list.add(primoCommittente);
		 */
		if(source.getCommittenti()!=null){
			for(Committenticantiere comcantiere : source.getCommittenti()){
					if(comcantiere.getAnagrafiche()!=null){
						Committente committenteSecondario = importCommittente(source, target, comcantiere.getAnagrafiche());
						if(committenteSecondario!=null)
							list.add(committenteSecondario);
				}
			}
		}
		target.setCommittente(list);
	}

	private Committente importCommittente(Cantieri source, Cantiere target, Anagrafiche anagCommittente){
		PersoneFisicheImporter personeImporter = PersoneFisicheImporter.getInstance();
		PersoneGiuridicheImporter ditteImporter = PersoneGiuridicheImporter.getInstance();
		
		//COMMITTENTE
		if(anagCommittente!=null && anagCommittente.getTipo()!=null){
			Committente comm = new Committente();
			comm.setCreatedBy(this.getClass().getSimpleName()+ulss);
			comm.setCreationDate(source.getTimestampinsmod());
			Sedi sede = null;
			switch (anagCommittente.getTipo().intValue()) {

			//DITTA
			case 0:
				if(source.getDittaCommittente()!=null && source.getDittaCommittente().getAnagrafica().equals(anagCommittente)){
					Ditte ditta = source.getDittaCommittente();
					sede = ditteImporter.importDittaCompleta(ditta);

				}else{
					Query q = sourceEm.createQuery("SELECT d FROM Ditte d JOIN d.anagrafica a WHERE a.idanagrafiche = :id");
					q.setParameter("id", anagCommittente.getIdanagrafiche());
					List<Ditte> list = q.getResultList();
					if(list!=null && !list.isEmpty()){
						Ditte ditta = list.get(0);
						sede = ditteImporter.importDittaCompleta(ditta);
					}
				}
				if(sede!=null && sede.getPersonaGiuridica()!=null){
					comm.setPersoneGiuridiche(sede.getPersonaGiuridica());
					comm.setCantiere(target);
				}

				break;

				//CANTIERE -> GUARDO IL RIFERIMENTO
			case 3:
				if(anagCommittente.getRiferimento()!=null){
					Query q = sourceEm.createQuery("SELECT d FROM Ditte d JOIN d.anagrafica a WHERE a.idanagrafiche = :id");
					q.setParameter("id", anagCommittente.getRiferimento().getIdanagrafiche());
					List<Ditte> list = q.getResultList();
					if(list!=null && !list.isEmpty()){
						Ditte ditta = list.get(0);
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
				Query q = sourceEm.createQuery("SELECT u FROM Utenti u WHERE u.idanagrafica = :id");
				q.setParameter("id", anagCommittente.getIdanagrafiche());
				List<Utenti> list = q.getResultList();
				if(list!=null && !list.isEmpty()){
					Utenti utente = list.get(0);
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

	private void saveMapping(Cantieri source, Cantiere target){
		MapCantieri map = new MapCantieri();
		map.setIdprevnet(source.getIdcantieri());
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
		String hqlCantieri = "SELECT mf.idphi FROM MapCantieri mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlCantieri+="WHERE mf.ulss = :ulss";

		Query qCantieri = sourceEm.createQuery(hqlCantieri);
		if(ulss!=null && !ulss.isEmpty())
			qCantieri.setParameter("ulss", ulss);

		List<Long> allIdCantieri = qCantieri.getResultList();
		List<Long> idCantieri = new ArrayList<Long>();
		while(allIdCantieri!=null && !allIdCantieri.isEmpty()){
			if(allIdCantieri.size()>1000){
				idCantieri.clear();
				idCantieri.addAll(allIdCantieri.subList(0, 1000));
				allIdCantieri.removeAll(idCantieri);
			}else{
				idCantieri.clear();
				idCantieri.addAll(allIdCantieri);
				allIdCantieri.removeAll(idCantieri);
			}
			if(commit){
				String hqlSelCommittente = "SELECT c1.internalId FROM Committente c1 JOIN c1.cantiere s WHERE s.internalId IN (:ids)";
				Query qSelCommittente = targetEm.createQuery(hqlSelCommittente);
				qSelCommittente.setParameter("ids", idCantieri);
				List<Long> idCommittente = qSelCommittente.getResultList();

				String hqlSelDitteCantiere = "SELECT c1.internalId FROM DitteCantiere c1 JOIN c1.cantiere s WHERE s.internalId IN (:ids)";
				Query qSelDitteCantiere = targetEm.createQuery(hqlSelDitteCantiere);
				qSelDitteCantiere.setParameter("ids", idCantieri);
				List<Long> idDitteCantiere = qSelDitteCantiere.getResultList();

				String hqlPersoneCantiere = "SELECT s.internalId FROM PersoneCantiere s JOIN s.cantiere c WHERE c.internalId IN (:ids)";
				Query qPersoneCantiere = targetEm.createQuery(hqlPersoneCantiere);
				qPersoneCantiere.setParameter("ids", idCantieri);
				List<Long> idPersoneCantiere = qPersoneCantiere.getResultList();

				//PER OGNI CANTIERE, ELIMINO TUTTI I COMMITTENTI

				targetEm.getTransaction().begin();
				if(idCommittente!=null && !idCommittente.isEmpty()){
					String deleteCommittente = "DELETE FROM Committente c WHERE c.internalId IN (:ids)";

					Query qDelCommittente = targetEm.createQuery(deleteCommittente);
					qDelCommittente.setParameter("ids", idCommittente);
					qDelCommittente.executeUpdate();

				}

				//PER OGNI CANTIERE, ELIMINO TUTTE LE DITTE CANTIERE

				if(idDitteCantiere!=null && !idDitteCantiere.isEmpty()){
					String deleteDitteCantiere = "DELETE FROM DitteCantiere c WHERE c.internalId IN (:ids)";

					Query qDelDitteCantiere = targetEm.createQuery(deleteDitteCantiere);
					qDelDitteCantiere.setParameter("ids", idDitteCantiere);
					qDelDitteCantiere.executeUpdate();

				}

				//PER OGNI CANTIERE, ELIMINO TUTTE LE PERSONE CANTIERE

				if(idPersoneCantiere!=null && !idPersoneCantiere.isEmpty()){
					String deletePersoneCantiere = "DELETE FROM PersoneCantiere s WHERE s.internalId IN (:ids)";

					Query qDelPersoneCantiere = targetEm.createQuery(deletePersoneCantiere);
					qDelPersoneCantiere.setParameter("ids", idPersoneCantiere);
					qDelPersoneCantiere.executeUpdate();

				}

				String deleteCant = "DELETE FROM Cantiere e WHERE e.internalId IN (:ids)";
				Query qDelCant = targetEm.createQuery(deleteCant);
				qDelCant.setParameter("ids", idCantieri);
				qDelCant.executeUpdate();
				targetEm.getTransaction().commit();


				String update = "DELETE FROM MAPPING_CANTIERI WHERE ulss = :ulss";
				sourceEm.getTransaction().begin();
				Query q = sourceEm.createNativeQuery(update);
				q.setParameter("ulss", ulss);
				q.executeUpdate();
				sourceEm.getTransaction().commit();
			}
		}
	}
}
