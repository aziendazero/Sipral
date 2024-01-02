package com.phi.db.importer;

import com.phi.entities.baseEntity.*;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.prevnet.entities.*;
import com.prevnet.mappings.MapAtti;
import com.prevnet.mappings.MapSopralluoghi;
import org.apache.log4j.Logger;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"unchecked"})
public class AttiImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(AttiImporter.class.getName());

	private static AttiImporter instance = null;

	public static AttiImporter getInstance() {
		if(instance == null) {
			instance = new AttiImporter();
		}
		return instance;
	}

	public Atto importAtti(Atti source){
		if(!checkMapping(source.getIdatti())){
			Atto target = new Atto();

			target.setCreatedBy(this.getClass().getSimpleName()+ulss);
			target.setCreationDate(source.getData());

			target.setDateProt(source.getData());
			if(source.getNprotocollo()!=null && !source.getNprotocollo().isEmpty()) {
				target.setNumeroProt(source.getNprotocollo());
			}

			String sourceNote = source.getNote();

			if (sourceNote != null && sourceNote.length() > 2000) {
				log.error("truncated note of atti "+source.getIdatti() +" too long");
				sourceNote= sourceNote.substring(0, 2000);
			}
			target.setNote(sourceNote);

			if(source.getInpartenza()!=null && !source.getInpartenza().isEmpty()){
				if("0".equals(source.getInpartenza())){
					target.setDirezione(getCodeValue("phidic.spisal.pratiche.atti.attiinout.in"));
				}else if("1".equals(source.getInpartenza())){
					target.setDirezione(getCodeValue("phidic.spisal.pratiche.atti.attiinout.out"));
				}else{
					log.error("Unable to find CodeValue for value: "+source.getInpartenza()+" of Atti id: "+source.getIdatti());
				}
			}

			saveOnTarget(target);
			saveMapping(source, target);
		}

		return getMapped(source.getIdatti());
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Atto getMapped(long id){
		MapAtti map = sourceEm.find(MapAtti.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapAtti m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapAtti> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Atto c = targetEm.find(Atto.class, map.getIdphi());
			if(c!=null){
				return c;
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
		MapAtti m = sourceEm.find(MapAtti.class, id);
		if(m!=null)
			return true;

		String hqlMapping = "SELECT m FROM MapAtti m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapAtti> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapAtti map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	private void saveMapping(Atti source, Atto target){
		MapAtti map = new MapAtti();
		map.setIdprevnet(source.getIdatti());
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

	}
}
