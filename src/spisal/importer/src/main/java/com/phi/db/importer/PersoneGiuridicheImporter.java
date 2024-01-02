package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Person;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.Attivitaditte;
import com.prevnet.entities.Ditte;
import com.prevnet.entities.Rappresentantiditta;
import com.prevnet.mappings.MapOperatori;
import com.prevnet.mappings.MapPersoneFisiche;
import com.prevnet.mappings.MapPersoneGiuridiche;

@SuppressWarnings({"unchecked"})
public class PersoneGiuridicheImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(PersoneGiuridicheImporter.class.getName());
	private Query qDitteRef;
	private Query qDitteNoRef;
	
	private static PersoneGiuridicheImporter instance = null;
	
	public static PersoneGiuridicheImporter getInstance() {
		if(instance == null) {
			instance = new PersoneGiuridicheImporter();
		}
		return instance;
	}

	
	public PersoneGiuridicheImporter() {
		super();
		qDitteRef = sourceEm.createQuery("SELECT d FROM Ditte d "
					+ "JOIN d.anagrafica a "
					+ "LEFT JOIN a.riferimento r "
					+ "WHERE d.idditte = :idDitta OR a.idanagrafiche = :idAnag AND r.idanagrafiche = :idRef");
		qDitteNoRef = sourceEm.createQuery("SELECT d FROM Ditte d "
				+ "JOIN d.anagrafica a "
				+ "LEFT JOIN a.riferimento r "
				+ "WHERE d.idditte = :idDitta OR a.idanagrafiche = :idAnag");
		
	}
	
	public void aggiornaDitta(Ditte source) {
		if(source.getAttivitadittes()!=null && checkMapping(source.getIdditte())){
			Sedi sede = getMapped(source.getIdditte());
			AttivitaImporter attImporter = AttivitaImporter.getInstance();
			
			if(source.getAttivitadittes()!=null) {
				attImporter.importAttivita(source, sede);
			}
		}
	}
	
	/**
	 * Data l'anagrafica in source ritorna la sede corrispondente in target, ma importa TUTTA la ditta (PersoneGiuridiche e Sedi)
	 * @param source
	 * @return
	 */
	public Sedi importDittaCompleta(Anagrafiche source){
		//controllo che sia una ditta
		if(source!=null && source.getTipo()!=null && source.getTipo().intValue()==0){
			Query qDitta = sourceEm.createQuery("SELECT d FROM Ditte d JOIN d.anagrafica a WHERE a.idanagrafiche = :id");
			qDitta.setParameter("id", source.getIdanagrafiche());
			List<Ditte> list = qDitta.getResultList();
			if(list!=null && !list.isEmpty()){
				return importDittaCompleta(list.get(0));
			}
		}
			
		return null;
	}
	
	/**
	 * Data la sede in source ritorna la sede corrispondente in target, ma importa TUTTA la ditta (PersoneGiuridiche e Sedi)
	 * @param source
	 * @return
	 */
	public Sedi importDittaCompleta(Ditte source){
		Sedi imported = null;
		if(source==null || source.getAnagrafica()==null)
			return imported;
		
		if(!checkMapping(source.getIdditte())){
			
			/*
			 * LISTA DI TUTTE LE SEDI DELLA DITTA A CUI APPARTIENE source: LE IMPORTIAMO TUTTE
			 */
			List<Ditte> ditte = null;
			if(source.getAnagrafica().getRiferimento()!=null){
				qDitteRef.setParameter("idAnag", source.getAnagrafica().getIdanagrafiche());
				qDitteRef.setParameter("idRef", source.getAnagrafica().getRiferimento().getIdanagrafiche());
				qDitteRef.setParameter("idDitta", source.getIdditte());
				ditte = qDitteRef.getResultList();
			}else{
				qDitteNoRef.setParameter("idAnag", source.getAnagrafica().getIdanagrafiche());
				qDitteNoRef.setParameter("idDitta", source.getIdditte());
				ditte = qDitteNoRef.getResultList();
			
			}
					
			
			Ditte principale = findPrincipale(ditte);
			PersoneGiuridiche pg = importNewPg(principale);
			
			for(Ditte ditta : ditte){
				if(!checkMapping(ditta.getIdditte())){
					importNewSede(ditta, pg, principale);
				}
			}
			
		/**
		 * SE l'utente è già stato mappato, allora ritorna il corrispondente
		 */
		}else{
			//return getMapped(source.getIdditte());
		}
		
		return getMapped(source.getIdditte());
	}
	
	public Sedi importNewSede(Ditte source, PersoneGiuridiche pg, Ditte principale){
		if(source!=null){
			Sedi sede = new Sedi();
			sede.setPersonaGiuridica(pg);
			pg.addSedi(sede);
			sede.setCreatedBy(this.getClass().getSimpleName()+ulss);
			sede.setCreationDate(source.getTimestampinsmod()==null?new Date():source.getTimestampinsmod());
			if(!"1".equals(source.getSchedacorrente()))
				sede.setIsActive(false);
			
			sede.setNumeroREA(source.getCervednrea());
			if(source.getCervedprogul()!=null){
				try{
					sede.setProgressivoUnitaLocale(Integer.parseInt(source.getCervedprogul()));
				}catch (NumberFormatException ne){
					//continua..
				}
			}
			sede.setProvinciaCCIAA(source.getCciaa());
			
			if(source.getIdditte()==principale.getIdditte())
				sede.setSedePrincipale(true);
			
			if(source.getAnagrafica()!=null){
				Anagrafiche a = source.getAnagrafica();
				if(sede.getAddr()==null)
					sede.setAddr(new AD());
				
				AD addr = sede.getAddr();
				if(a.getLocalita()!=null){
					addr.setAdl(a.getLocalita().getDescrlocalita());
				}
				
				addr.setStr(a.getIndirizzo());	
				manageAddrComune(addr, a.getComuni());
				
				sede.setTipologia(getMappedCode(a.getTipologiaDitta(), ulss));
			}
			sede.setStato((CodeValueCountry)getMappedCode(source.getNazionalita(), ulss));
			
			if(sede.getTelecom()==null)
				sede.setTelecom(new TEL());
			
			TEL tel = sede.getTelecom();
			tel.setAs(source.getTelefono());
			tel.setBad(source.getFax());
			tel.setMail(source.getEmail());
			
			sede.setDataInizioAttivita(source.getInizioattivita());
			sede.setDataCessazione(source.getFineattivita());
			
			sede.setStatoAttivita(getMappedCode(source.getStato(), ulss));
			
			String denominazione = "";
			
			if(source.getDenominazioneesercizio()!=null)
				denominazione = source.getDenominazioneesercizio();
			else if(source.getDenominazioneditta()!=null)
				denominazione = source.getDenominazioneditta();
			else if(source.getAnagrafica()!=null && source.getAnagrafica().getDenominazione()!=null)
				denominazione = source.getAnagrafica().getDenominazione();
			else if(pg!=null)
				denominazione = pg.getDenominazione();
			
			int length = denominazione.length();
			denominazione = denominazione.substring(0,length<=255?length:255);
			sede.setDenominazioneUnitaLocale(denominazione);
			
			saveOnTarget(sede);
			
			RappresentantiImporter rapprImporter = RappresentantiImporter.getInstance();
			
			if(source.getRappresentanti()!=null){
				for(Rappresentantiditta rapp : source.getRappresentanti()){
					rapprImporter.importRappresentante(rapp,sede);
				}
			}
			
			if(source.getTitolare()!=null)
				rapprImporter.importTitolare(sede, source.getTitolare(), "phidic.spisal.company.cariche.ti");//phidic.spisal.pratiche.activities.roles.tit
			if(source.getRapprLegale()!=null)
				rapprImporter.importTitolare(sede, source.getRapprLegale(), "phidic.spisal.company.cariche.ler");//phidic.spisal.pratiche.activities.roles.ler
			if(source.getRls()!=null)
				rapprImporter.importTitolare(sede, source.getRls(), "phidic.spisal.pratiche.activities.roles.rls");
			
			AttivitaImporter attImporter = AttivitaImporter.getInstance();
			
			if(source.getAttivitadittes()!=null) {
				attImporter.importAttivita(source, sede);
			}
			
			if(source.getAttivitadittes()!=null){
				for(Attivitaditte att : source.getAttivitadittes()){
					attImporter.importAttivita(att,sede);
				}
			}
			
			saveMapping(source, sede);
			
		}
		
		
		return null;
	}
	
	public PersoneGiuridiche importNewPg(Ditte principale){
		if(principale!=null){
			
			PersoneGiuridiche p = new PersoneGiuridiche();
			p.setCreatedBy(this.getClass().getSimpleName()+ulss);
			p.setCreationDate(principale.getTimestampinsmod()==null?new Date():principale.getTimestampinsmod());
			
			p.setPatritaIva(principale.getCodfiscpartiva());
			p.setCodiceFiscale(principale.getCodicefiscale());
			//richiesta di A.Scatto del 11/05/2017
			if(p.getCodiceFiscale()==null || p.getCodiceFiscale().isEmpty()){
				p.setCodiceFiscale(p.getPatritaIva());
			}
			if(principale.getAnagrafica().getCodiceImp()!=null) {
				p.setCodiceDitta(String.valueOf(principale.getAnagrafica().getCodiceImp()));
			}
			p.setFormaGiuridica(getMappedCode(principale.getNaturaGiuridica(), ulss));
			
			String denominazione = "";
			if(principale.getDenominazioneditta()!=null)
				denominazione+=principale.getDenominazioneditta();

			if(denominazione.isEmpty() && principale.getAnagrafica()!=null && principale.getAnagrafica().getDenominazione()!=null)
				denominazione = principale.getAnagrafica().getDenominazione();
			
			int length = denominazione.length();
			denominazione = denominazione.substring(0,length<=255?length:255);
			
			p.setDenominazione(denominazione);
			p.setDataCostituzione(principale.getInizioattivita());
			p.setDataTermine(principale.getFineattivita());
			p.setDataIscrizioneRI(principale.getDiadataiscrizioneri());
			
			p.setNumeroRI(principale.getCodicecamerale());
			
			saveOnTarget(p);
			
			return p;
		}
		
		
		return null;
	}
	
	private Ditte findPrincipale(List<Ditte> ditte){
		if(ditte!=null && !ditte.isEmpty()){
			Ditte principale = null;
			
			for(Ditte ditta : ditte){
				if(ditta.getAnagrafica()!=null && ditta.getAnagrafica().getRiferimento()!=null){
					if(ditta.getAnagrafica().getRiferimento().getIdanagrafiche() == ditta.getAnagrafica().getIdanagrafiche()){
						
						return ditta;					
					}
					
				}else if(ditta.getAnagrafica()!=null && ditta.getAnagrafica().getSottocodiceImp()!=null){
					
					if(principale==null){
						principale=ditta;
					}else if(principale.getAnagrafica().getSottocodiceImp()!=null && ditta.getAnagrafica().getSottocodiceImp()!=null){
						if(principale.getAnagrafica().getSottocodiceImp().compareTo(ditta.getAnagrafica().getSottocodiceImp())>0){
							principale=ditta;
						}
					}
				}
			}
			
			if(principale!=null){
				return principale;
				
			}else{
				return ditte.get(0);
			}
		}
		
		return null;
	}
	
	/**
	 * Controlla se l'entità id sia già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapPersoneGiuridiche m = sourceEm.find(MapPersoneGiuridiche.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapPersoneGiuridiche m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPersoneGiuridiche> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPersoneGiuridiche map = list.get(0);
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
	private Sedi getMapped(long id){
		MapPersoneGiuridiche map = sourceEm.find(MapPersoneGiuridiche.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapPersoneGiuridiche m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapPersoneGiuridiche> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Sedi c = targetEm.find(Sedi.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qSede = targetEm.createQuery("SELECT s FROM Sedi s WHERE s.internalId = :id");
			qSede.setParameter("id", map.getIdphi());
			List<Sedi> lp = qSede.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}
	
	private void saveMapping(Ditte source, Sedi target){
		MapPersoneGiuridiche map = new MapPersoneGiuridiche();
		map.setIdprevnet(source.getIdditte());
		map.setIdphi(target.getInternalId());
		if(target.getPersonaGiuridica()!=null)
			map.setIdPg(target.getPersonaGiuridica().getInternalId());
		
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
	 * Cancellare una Sedi+PersoneGiuridiche implica cancellare anche le Cariche, le SediPersone e le AttivitaIstat ad esse connesse.
	 * Non cancelliamo invece le Person importate eventualmente in ambito di import di Cariche, perchè per quello possiamo usare
	 * il PersoneFisicheImporter
	 */
	@Override
	protected void deleteImportedData(String ulss) {
		
		//PRIMA ELIMINO TUTTE LE SEDI IMPORTATE
		
		String hqlSedi = "SELECT mf.idphi FROM MapPersoneGiuridiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlSedi+="WHERE mf.ulss = :ulss";
		
		Query qSedi = sourceEm.createQuery(hqlSedi);
		if(ulss!=null && !ulss.isEmpty())
			qSedi.setParameter("ulss", ulss);
		
		List<Long> allIdSedi = qSedi.getResultList();
		List<Long> idSedi = new ArrayList<Long>();
		while(allIdSedi!=null && !allIdSedi.isEmpty()){
			if(allIdSedi.size()>1000){
				idSedi.clear();
				idSedi.addAll(allIdSedi.subList(0, 1000));
				allIdSedi.removeAll(idSedi);
			}else{
				idSedi.clear();
				idSedi.addAll(allIdSedi);
				allIdSedi.removeAll(idSedi);
			}
			if(commit){				
				String hqlSelAttivita = "SELECT c1.internalId FROM AttivitaIstat c1 JOIN c1.sedi s WHERE s.internalId IN (:ids)";
				Query qSelAttivita = targetEm.createQuery(hqlSelAttivita);
				qSelAttivita.setParameter("ids", idSedi);
				List<Long> idAttivita = qSelAttivita.getResultList();
				
				String hqlSelCariche = "SELECT c1.internalId FROM Cariche c1 JOIN c1.sede s WHERE s.internalId IN (:ids)";
				Query qSelCariche = targetEm.createQuery(hqlSelCariche);
				qSelCariche.setParameter("ids", idSedi);
				List<Long> idCariche = qSelCariche.getResultList();
				List<Long> idSediPersone = null;
				
				if(idCariche!=null && !idCariche.isEmpty()){
					String hqlSediPersone = "SELECT DISTINCT s.internalId FROM SediPersone s JOIN s.cariche c WHERE c.internalId IN (:ids)";
					Query qSediPersone = targetEm.createQuery(hqlSediPersone);
					qSediPersone.setParameter("ids", idCariche);
					idSediPersone = qSediPersone.getResultList();
					
				}

				//PER OGNI SEDE, ELIMINO TUTTE LE ATTIVITAISTAT
				
				targetEm.getTransaction().begin();
				if(idAttivita!=null && !idAttivita.isEmpty()){
					String deleteAttivita = "DELETE FROM AttivitaIstat c WHERE c.internalId IN (:ids)";

					Query qDelAttivita = targetEm.createQuery(deleteAttivita);
					qDelAttivita.setParameter("ids", idAttivita);
					qDelAttivita.executeUpdate();
					
					//PER OGNI ATTIVITAISTAT ELIMINO IL RELATIVO MAPPING
					
					String updateMapAtt = "DELETE FROM MAPPING_ATTIVITA WHERE idphi IN (:ids) AND ulss = :ulss";
					sourceEm.getTransaction().begin();
					Query qMapAtt = sourceEm.createNativeQuery(updateMapAtt);
					qMapAtt.setParameter("ulss", ulss);
					qMapAtt.setParameter("ids", idAttivita);
					qMapAtt.executeUpdate();
					sourceEm.getTransaction().commit();
				}
				
				//PER OGNI SEDE, ELIMINO TUTTE LE CARICHE
				
				if(idCariche!=null && !idCariche.isEmpty()){
					String deleteCariche = "DELETE FROM Cariche c WHERE c.internalId IN (:ids)";

					Query qDelCariche = targetEm.createQuery(deleteCariche);
					qDelCariche.setParameter("ids", idCariche);
					qDelCariche.executeUpdate();
					
					//PER OGNI CARICHE ELIMINO IL RELATIVO MAPPING
					
					String updateMapRappr = "DELETE FROM MAPPING_RAPPRESENTANTI WHERE idphi IN (:ids) AND ulss = :ulss";
					sourceEm.getTransaction().begin();
					Query qMapRappr = sourceEm.createNativeQuery(updateMapRappr);
					qMapRappr.setParameter("ulss", ulss);
					qMapRappr.setParameter("ids", idCariche);
					qMapRappr.executeUpdate();
					sourceEm.getTransaction().commit();
				}
				
				//PER OGNI SEDE, ELIMINO TUTTE LE SEDIPERSONE ASSOCIATE ALLE CARICHE CHE HO ELIMINATO
				
				if(idSediPersone!=null && !idSediPersone.isEmpty()){
					String deleteSediPersone = "DELETE FROM SediPersone s WHERE s.internalId IN (:ids)";

					Query qDelSediPersone = targetEm.createQuery(deleteSediPersone);
					qDelSediPersone.setParameter("ids", idSediPersone);
					qDelSediPersone.executeUpdate();
					
					//SEDIPERSONE NON COMPARE IN NESSUN MAPPING
				}
				
				//INFINE ELIMINO LA SEDE
				
				String deleteSedi = "DELETE FROM Sedi e WHERE e.internalId IN (:ids)";
				
				Query qDelPersons = targetEm.createQuery(deleteSedi);
				qDelPersons.setParameter("ids", idSedi);
				qDelPersons.executeUpdate();

				targetEm.getTransaction().commit();
			}
		}
		
		//POI ELIMINO TUTTE LE PERSONE GIURIDICHE IMPORTATE
		
		String hqlPersGiu = "SELECT mf.idPg FROM MapPersoneGiuridiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPersGiu+="WHERE mf.ulss = :ulss";
		
		Query qPersGiu = sourceEm.createQuery(hqlPersGiu);
		if(ulss!=null && !ulss.isEmpty())
			qPersGiu.setParameter("ulss", ulss);
		
		List<Long> idPersGiu = qPersGiu.getResultList();
		if(idPersGiu!=null && !idPersGiu.isEmpty()){
			if(commit){
				String deletePersGiu = "DELETE FROM PersoneGiuridiche e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelPersons = targetEm.createQuery(deletePersGiu);
				qDelPersons.setParameter("ids", idPersGiu);
				qDelPersons.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		//INFINE IL MAPPING
		
		if(commit){
			
			String updateMapGiu = "DELETE FROM MAPPING_PERSONEGIU WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query qMapGiu = sourceEm.createNativeQuery(updateMapGiu);
			qMapGiu.setParameter("ulss", ulss);
			qMapGiu.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}
}