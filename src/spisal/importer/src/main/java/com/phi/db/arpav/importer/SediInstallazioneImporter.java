package com.phi.db.arpav.importer;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapSediInstallazione;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;

public class SediInstallazioneImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(SediInstallazioneImporter.class.getName());

	private static SediInstallazioneImporter instance = null;

	public static SediInstallazioneImporter getInstance() {
		if (instance == null) {
			instance = new SediInstallazioneImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) {
		SediInstallazione sinst = getMapped(row[0].getContents());
		if (sinst == null) {
			sinst = new SediInstallazione();
			sinst.setCreatedBy(this.getClass().getSimpleName() + ulss);
			sinst.setCreationDate(new Date());
			sinst.setDenominazione(row[1].getContents());
			//			AD addr = new AD();
			//			addr.setStr(row[2].getContents());
			//			addr.setBnr(row[3].getContents());
			//			addr.setCty(row[4].getContents());
			//			addr.setCpa(row[5].getContents());
			//			addr.setZip(row[6].getContents());
			//			addr.setAdl(row[7].getContents());
			//			addr.setCode(getComune(row[10].getContents()));
			sinst.setAddr(buildAD(row[2].getContents(), row[3].getContents(), row[4].getContents(), 
					row[5].getContents(), row[6].getContents(), row[7].getContents(), row[10].getContents()));
			sinst.setNote(row[8].getContents());
			if(row[9].getContents()!=null && !row[9].getContents().trim().isEmpty()){
				sinst.setSede(getSedi(row[9].getContents()));
			}
			sinst.setTipologiaSede(getCodeValue(row[11].getContents()));
			saveOnTarget(sinst);
			saveMapping(row[0].getContents(), sinst);
		} else {
			log.info("Already imported SediInstallazione with id: "
					+ row[0].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public SediInstallazione getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		MapSediInstallazione map = targetEm.find(MapSediInstallazione.class, idexcel);
		if (map != null) {
			// tiro su dal db phi l'entità
			SediInstallazione lp = targetEm.find(SediInstallazione.class, map.getIdphi());
			return lp;
		}
		return null;

	}

	public SediInstallazione getMappedLike(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		MapSediInstallazione map = null;
		if(idexcel!=null){
			Query queryMapLike = targetEm.createQuery("SELECT mp FROM MapSediInstallazione mp WHERE mp.idexcel LIKE :idlike");
			queryMapLike.setParameter("idlike", idexcel+"%");
			List<MapSediInstallazione> list = queryMapLike.getResultList();
			if(list!=null && list.size()==1) {
				map = list.get(0);
			}else {
				log.warn("Unable to find mapped SediInstallazione for idexcel beginning with:"+idexcel);
			}
		}

		if (map != null) {
			// tiro su dal db phi l'entità
			SediInstallazione lp = targetEm.find(SediInstallazione.class, map.getIdphi());
			return lp;
		}
		return null;

	}

	private void saveMapping(String sourceId, SediInstallazione target) {
		MapSediInstallazione map = new MapSediInstallazione();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "Sede installazione id: "
				+ target.getDenominazione() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}


	private Sedi getSedi(String id) {
		SediAddebitoImporter sediAddImp = SediAddebitoImporter.getInstance();
		return sediAddImp.getMapped(id);
	}

	private AD buildAD(String str, String bnr, String cty, String cpa, String zip, String adl, String istatComune) {
		AD addr = new AD();
		addr.setStr(str);
		addr.setBnr(bnr);
		addr.setCty((cty!=null && !cty.isEmpty())?cty:null);
		addr.setCpa((cpa!=null && !cpa.isEmpty())?cpa:null);
		addr.setZip(zip);
		addr.setAdl(adl);
		addr.setCode(getComune(istatComune));

		if(addr.getCty()==null && addr.getCode()!=null) {
			addr.setCty(addr.getCode().getDisplayName());
		}

		if(addr.getCpa()==null && addr.getCode()!=null) {
			addr.setCpa(addr.getCode().getProvince());
		}

		return addr;
	}

	public void setDeletable(Long siId){
		SediInstallazione si = targetEm.find(SediInstallazione.class, siId);

		Query searchImpianti = targetEm.createQuery("SELECT imp.internalId FROM Impianto imp " +
				"INNER JOIN imp.sedeInstallazione si WHERE si.internalId =:siId");
		searchImpianti.setParameter("siId", siId);
		List<Long> list = searchImpianti.getResultList();
		if(list == null || list.isEmpty()){
			si.setDeletable(true);
		}else{
			si.setDeletable(false);
		}
	}
}
