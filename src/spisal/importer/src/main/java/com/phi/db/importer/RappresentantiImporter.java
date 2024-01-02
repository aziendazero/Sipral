package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.Cariche;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SpisalAddr;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.Rappresentantiditta;
import com.prevnet.entities.Utenti;
import com.prevnet.mappings.MapAttivita;
import com.prevnet.mappings.MapMiglioramenti;
import com.prevnet.mappings.MapRappresentanti;

@SuppressWarnings({"unchecked"})
public class RappresentantiImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(RappresentantiImporter.class.getName());
	
	private static RappresentantiImporter instance = null;
	
	public static RappresentantiImporter getInstance() {
		if(instance == null) {
			instance = new RappresentantiImporter();
		}
		return instance;
	}
	
	public Cariche importTitolare(Sedi sede, Object utente, String caricaOid){
		PersoneFisicheImporter personeImporter = PersoneFisicheImporter.getInstance();
		Person person = null;
		if(utente instanceof Utenti)
			person = personeImporter.importPerson((Utenti)utente);
		else if(utente instanceof Anagrafiche)
			person = personeImporter.importPerson((Anagrafiche)utente);
		else
			return null;
		
		Cariche car = new Cariche();
		
		car.setCreatedBy(this.getClass().getSimpleName()+ulss);
		car.setCreationDate(new Date());
		
		
		SediPersone dipendente = findDipendente(sede.getPersonaGiuridica(), utente);
		if(dipendente==null)
			dipendente = importDipendente(person);
		
		car.setSede(sede);
		car.setSediPersone(dipendente);
		
		car.setCarica(getCodeValue(caricaOid));
			
		saveOnTarget(car);
		
		saveMapping(utente, car);
		return car;
	}
	
	/**
	 * Importa il rappresentante e lo aggancia alla sede
	 * @param source
	 * @param sede
	 */
	public void importRappresentante(Rappresentantiditta source, Sedi sede){
		PersoneFisicheImporter personeImporter = PersoneFisicheImporter.getInstance();
		if(!checkMapping(source.getIdrappresentantiditta())){
			Cariche car = new Cariche();
			
			car.setCreatedBy(this.getClass().getSimpleName()+ulss);
			car.setCreationDate(new Date());
					
			Utenti utente = source.getUtente();
			Person person = personeImporter.importPerson(utente);
			
			if(sede!=null){
				SediPersone dipendente = findDipendente(sede.getPersonaGiuridica(), utente);
				if(dipendente==null)
					dipendente = importDipendente(person);
				
				car.setSede(sede);
				car.setSediPersone(dipendente);
			}
			
			car.setCarica(getMappedCode(source.getRuolo(), "a", ulss));
				
			saveOnTarget(car);			
			saveMapping(source, car);
		}
	}
	
	private SediPersone importDipendente(Person personaFisica){
		if(personaFisica==null)
			return null;
		
		SediPersone dipendente = new SediPersone();
		dipendente.setCreatedBy(this.getClass().getSimpleName()+ulss);
		dipendente.setCreationDate(new Date());
		
		if(personaFisica.getAddr()!=null)
			dipendente.setAddr(personaFisica.getAddr().cloneAd());

		if(personaFisica.getBirthPlace()!=null)
			dipendente.setBirthPlace(personaFisica.getBirthPlace().cloneAd());
		
		if(personaFisica.getDomicileAddr()!=null)
			dipendente.setDomicileAddr(personaFisica.getDomicileAddr().cloneAd());
		
		if(personaFisica.getName()!=null)
			dipendente.setName(personaFisica.getName().cloneEN());
		
		if(personaFisica.getTelecom()!=null)
			dipendente.setTelecom(personaFisica.getTelecom().cloneTel());

		if(personaFisica.getCsDate()!=null)
			dipendente.setCsDate(personaFisica.getCsDate().cloneIVL());
		
		if(personaFisica.getEniDate()!=null)
			dipendente.setEniDate(personaFisica.getEniDate().cloneIVL());
		
		if(personaFisica.getStpDate()!=null)
			dipendente.setStpDate(personaFisica.getStpDate().cloneIVL());
		
		if(personaFisica.getTeamDate()!=null)
			dipendente.setTeamDate(personaFisica.getTeamDate().cloneIVL());
		
		if(personaFisica.getAlternativeAddr()!=null){
			SpisalAddr lavAlternativeAddr = personaFisica.getAlternativeAddr().cloneAddr();
			dipendente.setAlternativeAddr(lavAlternativeAddr);
		}
			
		if(personaFisica.getCurrentOrg()!=null)
			dipendente.setCurrentOrg(personaFisica.getCurrentOrg());
		
		if(personaFisica.getOriginalOrg()!=null)
			dipendente.setOriginalOrg(personaFisica.getOriginalOrg());
		
		dipendente.setBirthTime(personaFisica.getBirthTime());
		dipendente.setCategory(personaFisica.getCategory());
		dipendente.setCitizen(personaFisica.getCitizen());
		dipendente.setCode(personaFisica.getCode());
		dipendente.setCountryOfAddr(personaFisica.getCountryOfAddr());
		dipendente.setCountryOfBirth(personaFisica.getCountryOfBirth());
		dipendente.setCountryOfDomicile(personaFisica.getCountryOfDomicile());
		dipendente.setCs(personaFisica.getCs());
		dipendente.setCsRegion(personaFisica.getCsRegion());
		dipendente.setDeathDate(personaFisica.getDeathDate());
		dipendente.setDeathIndicator(personaFisica.getDeathIndicator());
		dipendente.setEni(personaFisica.getEni());
		dipendente.setFiscalCode(personaFisica.getFiscalCode());
		dipendente.setGenderCode(personaFisica.getGenderCode());
		dipendente.setHL7MsgDate(personaFisica.getHL7MsgDate());
		dipendente.setMaritalStatusCode(personaFisica.getMaritalStatusCode());
		dipendente.setMpi(personaFisica.getMpi());
		dipendente.setReliability(personaFisica.getReliability());
		dipendente.setStp(personaFisica.getStp());
		dipendente.setTeamCode(personaFisica.getTeamCode());
		dipendente.setTeamIdent(personaFisica.getTeamIdent());
		dipendente.setTeamInst(personaFisica.getTeamInst());
		dipendente.setTeamPers(personaFisica.getTeamPers());
		dipendente.setToUpdate(personaFisica.getToUpdate());
		
		saveOnTarget(dipendente);
		
		dipendente.setPerson(personaFisica);
		
		return dipendente;
	}
	
	private SediPersone findDipendente(PersoneGiuridiche pg, Object utente){
		if(pg==null || utente==null)
			return null;
		
		String hqlMapping = "SELECT m FROM MapRappresentanti m WHERE m.pgId = :pgId AND m.utenteId = :utenteId";
		
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("pgId", pg.getInternalId());
		if(utente instanceof Utenti)
			qMapping.setParameter("utenteId", ((Utenti)utente).getIdanagrafica());
		else if(utente instanceof Anagrafiche)
			qMapping.setParameter("utenteId", ((Anagrafiche)utente).getIdanagrafiche());
		
		List<MapRappresentanti> list = qMapping.getResultList();
		
		if(list!=null && !list.isEmpty()){
			MapRappresentanti map = list.get(0);
			String hqlDipendenti = "SELECT s FROM SediPersone s WHERE s.internalId = :id";
			Query qDipendenti = targetEm.createQuery(hqlDipendenti);
			qDipendenti.setParameter("id", map.getDipId());
			List<SediPersone> dipendenti = qDipendenti.getResultList();
			if(dipendenti!=null && !dipendenti.isEmpty()){
				return dipendenti.get(0);
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
		List<MapRappresentanti> maps = findMapping(MapRappresentanti.class.getName());
		for(MapRappresentanti m : maps){
			if(m.getIdprevnet()==id)
				return true;
		}
		
		String hqlMapping = "SELECT m FROM MapRappresentanti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapRappresentanti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapRappresentanti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	private void saveMapping(Object source, Cariche target){
		MapRappresentanti map = new MapRappresentanti();
		
		if(source instanceof Rappresentantiditta){
			map.setIdprevnet(((Rappresentantiditta)source).getIdrappresentantiditta());
			
			if(((Rappresentantiditta)source).getUtente()!=null)
				map.setUtenteId(((Rappresentantiditta)source).getUtente().getIdanagrafica());
		}else if(source instanceof Utenti){
			map.setUtenteId(((Utenti)source).getIdanagrafica());
		}
			
		
		map.setIdphi(target.getInternalId());
		if(target.getSede()!=null && target.getSede().getPersonaGiuridica()!=null)
			map.setPgId(target.getSede().getPersonaGiuridica().getInternalId());
		
		if(target.getSediPersone()!=null)
			map.setDipId(target.getSediPersone().getInternalId());
		

		
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);
		
		saveOnSource(map);
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}
	
	protected void deleteImportedData(String ulss){
		
		String hqlRappresentanti = "SELECT mf.idphi FROM MapRappresentanti mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlRappresentanti+="WHERE mf.ulss = :ulss";
		
		Query qRappresentanti = sourceEm.createQuery(hqlRappresentanti);
		if(ulss!=null && !ulss.isEmpty())
			qRappresentanti.setParameter("ulss", ulss);
		
		List<Long> allIdCariche = qRappresentanti.getResultList();
		List<Long> idCariche = new ArrayList<Long>();
		while(allIdCariche!=null && !allIdCariche.isEmpty()){
			if(allIdCariche.size()>1000){
				idCariche.clear();
				idCariche.addAll(allIdCariche.subList(0, 1000));
				allIdCariche.removeAll(idCariche);
			}else{
				idCariche.clear();
				idCariche.addAll(allIdCariche);
				allIdCariche.removeAll(idCariche);
			}
			String hqlSediPersone = "SELECT DISTINCT s.internalId FROM SediPersone s JOIN s.cariche c WHERE c.internalId IN (:ids)";
			Query qSediPersone = targetEm.createQuery(hqlSediPersone);
			qSediPersone.setParameter("ids", idCariche);
			List<Long> idSediPersone = qSediPersone.getResultList();
			
			if(commit){
				String deleteCariche = "DELETE FROM Cariche e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				
				Query qDelCariche = targetEm.createQuery(deleteCariche);
				qDelCariche.setParameter("ids", idCariche);
				qDelCariche.executeUpdate();
				
				targetEm.getTransaction().commit();
				
				if(idSediPersone!=null && !idSediPersone.isEmpty()){
					String deleteSediPersone = "DELETE FROM SediPersone s WHERE s.internalId IN (:ids)";
					targetEm.getTransaction().begin();
					
					Query qDelSediPersone = targetEm.createQuery(deleteSediPersone);
					qDelSediPersone.setParameter("ids", idSediPersone);
					qDelSediPersone.executeUpdate();
					
					targetEm.getTransaction().commit();
				}
			}
		}
				
		if(commit){
			String update = "DELETE FROM MAPPING_RAPPRESENTANTI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}

}
