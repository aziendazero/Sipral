package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.AcquisizioneInformazioni;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.role.Operatore;
import com.prevnet.entities.Interventiattivitasvolta;
import com.prevnet.entities.Operatori;
import com.prevnet.entities.Riunionioperative;
import com.prevnet.entities.Riunionioperativeoperatori;
import com.prevnet.mappings.MapInterventi;
import com.prevnet.mappings.MapProvvedimenti;
import com.prevnet.mappings.MapRiunioni;

@SuppressWarnings({"unchecked"})
public class RiunioniImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(RiunioniImporter.class.getName());

	private static RiunioniImporter instance = null;
	
	public static RiunioniImporter getInstance() {
		if(instance == null) {
			instance = new RiunioniImporter();
		}
		return instance;
	}
	
	public Attivita importIntervento(Interventiattivitasvolta source){
		if(!checkMappingInterventi(source.getIdattivitasvolta())){//CAMBIARE MAPPING!!!!
			
			Attivita target = new Attivita();
			
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			if(source.getDataattivita()!=null)
				target.setCreationDate(source.getDataattivita());
			else
				target.setCreationDate(new Date());
			
			target.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.incontri"));
			target.setDataInizio(source.getDataattivita());
						
			if(source.getNote()!=null){
				String note = source.getNote();
				int length = note.length();
				note = note.substring(0,length<=4000?length:4000);
				target.setNote(note);
			}
			
			saveOnTarget(target);
			
			//OPERATORI
			if(source.getOperatoris()!=null){
				OperatoriImporter opImp = OperatoriImporter.getInstance();
				List<Operatore> theList = new ArrayList<Operatore>();
				for(Operatori o : source.getOperatoris()){
					Operatore op = opImp.importOperatore(o);
					if(!theList.contains(op)){
						theList.add(op);
					}
				}
				target.setOperatori(theList);
				saveOnTarget(target);
				
			}
			
			saveMappingInterventi(source, target);
		}
		
		return getMappedInterventi(source.getIdattivitasvolta());
	}
	
	public Attivita importRiunione(Riunionioperative source){
		if(!checkMappingRiunioni(source.getIdriunionioperative())){
			
			Attivita target = new Attivita();
			
			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			if(source.getData()!=null)
				target.setCreationDate(source.getData());
			else
				target.setCreationDate(new Date());
			
			target.setCode(getCodeValue("phidic.spisal.pratiche.activities.activitytypes.incontri"));
			target.setDataInizio(source.getData());
			

			AcquisizioneInformazioni infos = new AcquisizioneInformazioni();
			infos.setCreatedBy(this.getClass().getSimpleName()+ulss);
			infos.setCreationDate(new Date());
			
			if(source.getArgomento()!=null)
				infos.setOggetto(source.getArgomento().getDescrizione());
			
			String conclusioni = "";
			if(source.getTipologia()!=null)
				conclusioni+=("TIPOLOGIA: "+source.getTipologia()+".\r\n");
			if(source.getDurata()!=null)
				conclusioni+=("DURATA: "+source.getDurata().toPlainString()+".\r\n");
			if(source.getDescrizione()!=null)
				conclusioni+=(source.getDescrizione()+".\r\n");
					
			int length = conclusioni.length();
			conclusioni = conclusioni.substring(0,length<=4000?length:4000);
			infos.setConclusioni(conclusioni);
			
			saveOnTarget(infos);
			
			target.setAcquisizioneInformazioni(infos);
			
			saveOnTarget(target);
			
			//OPERATORI
			if(source.getRiunionioperativeoperatoris()!=null){
				OperatoriImporter opImp = OperatoriImporter.getInstance();
				List<Operatore> theList = new ArrayList<Operatore>();
				for(Riunionioperativeoperatori os : source.getRiunionioperativeoperatoris()){
					if(os.getOperatori()!=null){
						Operatore op = opImp.importOperatore(os.getOperatori());
						if(!theList.contains(op)){
							theList.add(op);
						}
					}
				}
				target.setOperatori(theList);
				saveOnTarget(target);
				
			}
			
			saveMappingRiunioni(source, target);
		}
		
		return getMappedRiunioni(source.getIdriunionioperative());
	}

	
	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Attivita getMappedRiunioni(long id){
		String hqlMapping = "SELECT m FROM MapRiunioni m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapRiunioni> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapRiunioni map = list.get(0);
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
	private boolean checkMappingRiunioni(long id){
		MapRiunioni m = sourceEm.find(MapRiunioni.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapRiunioni m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapRiunioni> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapRiunioni map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	private void saveMappingRiunioni(Riunionioperative source, Attivita target){
		MapRiunioni map = new MapRiunioni();
		map.setIdprevnet(source.getIdriunionioperative());
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
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Attivita getMappedInterventi(long id){
		String hqlMapping = "SELECT m FROM MapInterventi m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapInterventi> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapInterventi map = list.get(0);
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
	private boolean checkMappingInterventi(long id){
		MapInterventi m = sourceEm.find(MapInterventi.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapInterventi m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapInterventi> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapInterventi map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	private void saveMappingInterventi(Interventiattivitasvolta source, Attivita target){
		MapInterventi map = new MapInterventi();
		map.setIdprevnet(source.getIdattivitasvolta());
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
		String hqlMap = "SELECT mf.idphi FROM MapRiunioni mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlMap+="WHERE mf.ulss = :ulss";
		
		Query qRiunioni = sourceEm.createQuery(hqlMap);
		if(ulss!=null && !ulss.isEmpty())
			qRiunioni.setParameter("ulss", ulss);
		
		List<Long> idAttivita = qRiunioni.getResultList();
		if(idAttivita!=null && !idAttivita.isEmpty()){
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
				
				String hqlInfos = "SELECT ACQUISIZIONE_INFORMAZIONI_ID FROM attivita WHERE INTERNAL_ID IN (:ids)";
				Query qInfos = targetEm.createNativeQuery(hqlInfos);
				qInfos.setParameter("ids", idAttivita);
				List<Long> idInfos = qInfos.getResultList();				
				
				//ELIMINO LE ATTIVITA
				String deleteAttivita = "DELETE FROM Attivita e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelAttivita = targetEm.createQuery(deleteAttivita);
				qDelAttivita.setParameter("ids", idAttivita);
				qDelAttivita.executeUpdate();
				targetEm.getTransaction().commit();
				
				//ELIMINO I DATI DI DETTAGLIO SOPRALLUOGHI
				if(idInfos!=null && !idInfos.isEmpty()){
					String delInfos = "DELETE FROM AcquisizioneInformazioni s WHERE s.internalId IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelInfos = targetEm.createQuery(delInfos);
					qDelInfos.setParameter("ids", idInfos);
					qDelInfos.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}
		
		hqlMap = "SELECT mf.idphi FROM MapInterventi mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlMap+="WHERE mf.ulss = :ulss";
		
		qRiunioni = sourceEm.createQuery(hqlMap);
		if(ulss!=null && !ulss.isEmpty())
			qRiunioni.setParameter("ulss", ulss);
		
		idAttivita = qRiunioni.getResultList();
		if(idAttivita!=null && !idAttivita.isEmpty()){
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
				
				String hqlInfos = "SELECT ACQUISIZIONE_INFORMAZIONI_ID FROM attivita WHERE INTERNAL_ID IN (:ids)";
				Query qInfos = targetEm.createNativeQuery(hqlInfos);
				qInfos.setParameter("ids", idAttivita);
				List<Long> idInfos = qInfos.getResultList();				
				
				//ELIMINO LE ATTIVITA
				String deleteAttivita = "DELETE FROM Attivita e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelAttivita = targetEm.createQuery(deleteAttivita);
				qDelAttivita.setParameter("ids", idAttivita);
				qDelAttivita.executeUpdate();
				targetEm.getTransaction().commit();
				
				//ELIMINO I DATI DI DETTAGLIO SOPRALLUOGHI
				if(idInfos!=null && !idInfos.isEmpty()){
					String delInfos = "DELETE FROM AcquisizioneInformazioni s WHERE s.internalId IN (:ids)";
					targetEm.getTransaction().begin();
					Query qDelInfos = targetEm.createQuery(delInfos);
					qDelInfos.setParameter("ids", idInfos);
					qDelInfos.executeUpdate();
					targetEm.getTransaction().commit();
				}
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_RIUNIONI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
			
			update = "DELETE FROM MAPPING_INTERVENTI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}
}
