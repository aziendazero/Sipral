package com.phi.db.importer;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.SessionImpl;

import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.prevnet.entities.Attivitaditte;
import com.prevnet.entities.Ditte;
import com.prevnet.mappings.MapAttivita;
import com.prevnet.mappings.MapPersoneFisiche;

@SuppressWarnings({"unchecked"})
public class AttivitaImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(AttivitaImporter.class.getName());
	
	private static AttivitaImporter instance = null;
	
	public static AttivitaImporter getInstance() {
		if(instance == null) {
			instance = new AttivitaImporter();
		}
		return instance;
	}
	
	/**
	 * Importa l'attivita e la aggancia alla sede
	 * @param source
	 * @param sede
	 */
	public void importAttivita(Ditte source, Sedi sede){		
		if(source==null || source.getAttivitaPrevnet()==null)
			return;
		
		if(!checkMapping(source.getAttivitaPrevnet().getIdattivita(), source.getIdditte())){
			AttivitaIstat att = new AttivitaIstat();
			
			att.setCreatedBy(this.getClass().getSimpleName()+ulss);
			att.setCreationDate(new Date());
			String code = source.getAttivitaPrevnet().getCodice();
			if(code!=null && code.contains("(2007)")){
				// Vengono accettati solo codici che contengono 2007, il resto sono dati non validi
				CodeValueAteco ateco = getAteco(code.replaceAll(" +\\(2007\\).*", ""));
				att.setCode(ateco);
			}
			att.setSedi(sede);
			att.setImportanza(getCodeValue("phidic.spisal.company.attivita.imp.P"));
			
			if(sede.getSedePrincipale()){
				att.setPersoneGiuridiche(sede.getPersonaGiuridica());
			}
			
			saveOnTarget(att);			
			saveMapping(source.getAttivitaPrevnet().getIdattivita(), source.getIdditte(), att.getInternalId(), att.getCreatedBy());
		}
	}
	
	/**
	 * Importa l'attivita e la aggancia alla sede
	 * @param source
	 * @param sede
	 */
	public void importAttivita(Attivitaditte source, Sedi sede){		
		if(source.getAttivitaPrevnet()==null || source.getDitta()==null)
			return;
		
		if(!checkMapping(source.getAttivitaPrevnet().getIdattivita(), source.getDitta().getIdditte())){
			AttivitaIstat att = new AttivitaIstat();
			
			att.setCreatedBy(this.getClass().getSimpleName()+ulss);
			att.setCreationDate(new Date());
			String code = source.getAttivitaPrevnet().getCodice();
			if(code!=null && code.contains("(2007)")){
				// Vengono accettati solo codici che contengono 2007, il resto sono dati non validi
				CodeValueAteco ateco = getAteco(code.replaceAll(" +\\(2007\\).*", ""));
				att.setCode(ateco);
			}
			att.setSedi(sede);
			if(source.getContatore()!=null && source.getContatore().intValue()==1){
				att.setImportanza(getCodeValue("phidic.spisal.company.attivita.imp.P"));
				
			}else{
				att.setImportanza(getCodeValue("phidic.spisal.company.attivita.imp.S"));
			}
			att.setDataInizioAttivita(source.getDatainizio());
			if(sede.getSedePrincipale()){
				att.setPersoneGiuridiche(sede.getPersonaGiuridica());
			}
			
			saveOnTarget(att);
			
			saveMapping(source, att);
		}
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param idAttivita
	 * @return
	 */
	private boolean checkMapping(long idAttivita, long idDitta){
		List<MapAttivita> maps = findMapping(MapAttivita.class.getName());
		for(MapAttivita m : maps){
			if(m.getIdprevnet()==idAttivita && m.getIdDitta()==idDitta)
				return true;
		}
		String hqlMapping = "SELECT m FROM MapAttivita m WHERE m.idprevnet = :id AND m.idDitta = :idDitta";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idAttivita);
		qMapping.setParameter("idDitta", idDitta);
		List<MapAttivita> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapAttivita map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	private void saveMapping(Attivitaditte source, AttivitaIstat target){
		if(source.getAttivitaPrevnet()==null || source.getDitta()==null)
			return;
		
		saveMapping(source.getAttivitaPrevnet().getIdattivita(), source.getDitta().getIdditte(), target.getInternalId(), target.getCreatedBy());
	}
	
	private void saveMapping(long idattivita, long idditte, long internalId, String createdBy){

		MapAttivita map = new MapAttivita();
		map.setIdprevnet(idattivita);
		map.setIdDitta(idditte);
		map.setIdphi(internalId);
		map.setCopiedBy(createdBy);
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
		String hqlAttivita = "SELECT mf.idphi FROM MapAttivita mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlAttivita+="WHERE mf.ulss = :ulss";
		
		Query qAttivita = sourceEm.createQuery(hqlAttivita);
		if(ulss!=null && !ulss.isEmpty())
			qAttivita.setParameter("ulss", ulss);
		
		List<Long> idAttivita = qAttivita.getResultList();
		if(idAttivita!=null && !idAttivita.isEmpty()){
			if(commit){
				String deleteAttivita = "DELETE FROM AttivitaIstat e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelAttivita = targetEm.createQuery(deleteAttivita);
				qDelAttivita.setParameter("ids", idAttivita);
				qDelAttivita.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_ATTIVITA WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}

}
