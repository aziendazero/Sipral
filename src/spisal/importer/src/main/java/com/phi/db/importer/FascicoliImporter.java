package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.TagFascicolo;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.prevnet.entities.FascicoliPrevnet;
import com.prevnet.entities.Tabelle;
import com.prevnet.mappings.MapAttivita;
import com.prevnet.mappings.MapEsposti;
import com.prevnet.mappings.MapFascicoli;

@SuppressWarnings({"unchecked"})
public class FascicoliImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(FascicoliImporter.class.getName());

	private static FascicoliImporter instance = null;

	public static FascicoliImporter getInstance() {
		if(instance == null) {
			instance = new FascicoliImporter();
		}
		return instance;
	}

	public static void main (String[] args) {

		FascicoliImporter importer = new FascicoliImporter();
		importer.deleteImportedData(ulss);
		List<FascicoliPrevnet> fascicoli = importer.readFascicoli();
		if(fascicoli!=null){
			for(FascicoliPrevnet fascicolo : fascicoli){
				importer.importFascicolo(fascicolo);
			}
		}
		importer.closeResource();
	}

	public TagFascicolo importFascicolo(FascicoliPrevnet fascicolo){
		if(!checkMapping(fascicolo.getIdfascicoli())){
			TagFascicolo tag = new TagFascicolo();

			tag.setCreatedBy(this.getClass().getSimpleName()+ulss);
			tag.setCreationDate(new Date());
			if(distretto==null || distretto.isEmpty()){
				tag.setTagType(getCodeValue("phidic.spisal.pratiche.tagtype.ulss"));//tag type ulss
			}else {
				tag.setTagType(getCodeValue("phidic.spisal.pratiche.tagtype.distr"));//tag type distretto
			}
			
			if(fascicolo.getDescrizione()!=null){
				String fas = fascicolo.getDescrizione();
				int length = fas.length();
				fas = fas.substring(0,length<=255?length:255);
				tag.setFascicolo(fas);
			}

			//tag.setIsActive("1".equals(fascicolo.getStato()));


			if(fascicolo.getAnno()!=null){
				int year = fascicolo.getAnno().intValue();

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0L);
				cal.set(Calendar.YEAR, year);

				tag.setStartValidity(cal.getTime());

				cal.set(Calendar.HOUR,0);
				cal.add(Calendar.YEAR, 1);
				cal.add(Calendar.MILLISECOND, -1);
				tag.setEndValidity(cal.getTime());
			}

			//aggancio la UOC (Spisal), non la ULSS
			if(distretto!=null && !distretto.isEmpty()){
				ServiceDeliveryLocation loc = findUlss(ulss);
				if(loc!=null && loc.getChildren()!=null && !loc.getChildren().isEmpty()){
					for(ServiceDeliveryLocation s : loc.getChildren()) {
						if(s!=null && s.getId("HBS")!=null && distretto!=null && distretto.equals(s.getId("HBS").getExtension())) {
							if(tag.getDistretti()==null) {
								tag.setDistretti(new ArrayList<ServiceDeliveryLocation>());
							}
							tag.getDistretti().add(s);
							break;
						}
					}
				}
			}

			saveOnTarget(tag);			
			saveMapping(fascicolo, tag);
		}

		return getMapped(fascicolo.getIdfascicoli());
	}

	public TagFascicolo importFascicolo(Tabelle fascicolo){
		if(!checkMappingTabella(fascicolo.getIdtabelle())){
			TagFascicolo tag = new TagFascicolo();

			tag.setCreatedBy(this.getClass().getSimpleName()+ulss);
			tag.setCreationDate(new Date());
			tag.setTagType(getCodeValue("phidic.spisal.pratiche.tagtype.gen"));//tag type generico
			
			if(fascicolo.getDescrizione()!=null){
				String fas = fascicolo.getDescrizione();
				int length = fas.length();
				fas = fas.substring(0,length<=255?length:255);
				tag.setFascicolo(fas);
			}
			
			if(fascicolo.getNote()!=null){
				String notes = fascicolo.getNote();
				int length = notes.length();
				notes = notes.substring(0,length<=2000?length:2000);
				tag.setNotes(notes);
			}

			if(fascicolo.getDatainizio()!=null){
				tag.setStartValidity(fascicolo.getDatainizio());
			}

			if(fascicolo.getDatafine()!=null){
				tag.setEndValidity(fascicolo.getDatafine());
			}

			//aggancio la UOC (Spisal), non la ULSS
			ServiceDeliveryLocation loc = findUlss(ulss);
			if(loc!=null && loc.getChildren()!=null && !loc.getChildren().isEmpty()){
				for(ServiceDeliveryLocation s : loc.getChildren()) {
					if(s!=null && s.getId("HBS")!=null && distretto!=null && distretto.equals(s.getId("HBS").getExtension())) {
						if(tag.getDistretti()==null) {
							tag.setDistretti(new ArrayList<ServiceDeliveryLocation>());
						}
						tag.getDistretti().add(s);
						break;
					}
				}
			}


			saveOnTarget(tag);
			saveMappingTabelle(fascicolo, tag);
		}

		return getMapped(fascicolo.getIdtabelle());
	}

	public List<FascicoliPrevnet> readFascicoli() {
		List<FascicoliPrevnet> fascicoli = new ArrayList<FascicoliPrevnet>();

		String hqlFascicoli = "SELECT f FROM FascicoliPrevnet f";
		Query qFascicoli = sourceEm.createQuery(hqlFascicoli);
		fascicoli = qFascicoli.getResultList();
		return fascicoli;
	}
	
	public List<Tabelle> readFascicoliTabelle() {
		List<Tabelle> fascicoli = new ArrayList<Tabelle>();

		String hqlFascicoli = "SELECT t FROM Tabelle t WHERE t.codicetabella IN (364,677)";
		Query qFascicoli = sourceEm.createQuery(hqlFascicoli);
		fascicoli = qFascicoli.getResultList();
		return fascicoli;
	}

	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapFascicoli m = sourceEm.find(MapFascicoli.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapFascicoli m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapFascicoli> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapFascicoli map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMappingTabella(long id){
		List<MapFascicoli> maps = findMapping(MapFascicoli.class.getName());
		for(MapFascicoli m : maps){
			if(m.getIdprevnet()==id && m.getTabella()==true)
				return true;
		}
		String hqlMapping = "SELECT m FROM MapFascicoli m WHERE m.idprevnet = :id and m.tabella = 1";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapFascicoli> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapFascicoli map = list.get(0);
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
	private TagFascicolo getMapped(long id){
		MapFascicoli map = sourceEm.find(MapFascicoli.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapFascicoli m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapFascicoli> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			TagFascicolo c = targetEm.find(TagFascicolo.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qFascicolo = targetEm.createQuery("SELECT tag FROM TagFascicolo tag WHERE tag.internalId = :id");
			qFascicolo.setParameter("id", map.getIdphi());
			List<TagFascicolo> tags = qFascicolo.getResultList();
			if(tags!=null && !tags.isEmpty()){
				return tags.get(0);
			}
		}
		
		return null;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private TagFascicolo getMappedTabella(long id){
		String hqlMapping = "SELECT m FROM MapFascicoli m WHERE m.idprevnet = :id AND m.tabella = 1";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapFascicoli> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapFascicoli map = list.get(0);
			Query qFascicolo = targetEm.createQuery("SELECT tag FROM TagFascicolo tag WHERE tag.internalId = :id");
			qFascicolo.setParameter("id", map.getIdphi());
			List<TagFascicolo> tags = qFascicolo.getResultList();
			if(tags!=null && !tags.isEmpty()){
				return tags.get(0);
			}
		}
		return null;
	}

	private void saveMapping(FascicoliPrevnet source, TagFascicolo target){
		MapFascicoli map = new MapFascicoli();
		map.setIdprevnet(source.getIdfascicoli());
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

	private void saveMappingTabelle(Tabelle source, TagFascicolo target){
		MapFascicoli map = new MapFascicoli();
		map.setIdprevnet(source.getIdtabelle());
		map.setTabella(true);
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
	protected void deleteImportedData(String ulss){

		String hqlFascicoli = "SELECT mf.idphi FROM MapFascicoli mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlFascicoli+="WHERE mf.ulss = :ulss";

		Query qFascicoli = sourceEm.createQuery(hqlFascicoli);
		if(ulss!=null && !ulss.isEmpty())
			qFascicoli.setParameter("ulss", ulss);

		List<Long> idFasc = qFascicoli.getResultList();
		if(idFasc!=null && !idFasc.isEmpty()){
			if(commit){
				String deleteFasc = "DELETE FROM TagFascicolo e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelFasc = targetEm.createQuery(deleteFasc);
				qDelFasc.setParameter("ids", idFasc);
				qDelFasc.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_FASCICOLI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}
}
