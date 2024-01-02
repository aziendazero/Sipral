package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapPersoneGiuridicheArpav;
import com.phi.db.arpav.mapping.MapSediAddebito;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;

public class PersoneGiuridicheImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(PersoneGiuridicheImporter.class.getName());

	private static PersoneGiuridicheImporter instance = null;

	public static PersoneGiuridicheImporter getInstance() {
		if (instance == null) {
			instance = new PersoneGiuridicheImporter();
		}
		return instance;
	}

	
	public void importRow(Cell[] row) throws ParseException {
		PersoneGiuridiche pg = getMapped(row[0].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (pg == null) {
			pg = new PersoneGiuridiche();
			pg.setCreatedBy(this.getClass().getSimpleName() + ulss);
			pg.setCreationDate(new Date());
			
			if(row[1].getContents()==null || row[1].getContents().trim().isEmpty()) {
				pg.setCodiceFiscale("-");
			}else {
				pg.setCodiceFiscale(row[1].getContents());
			}
			
			if(row[2].getContents()!=null && !row[2].getContents().trim().isEmpty()){
					pg.setDataCostituzione(sdf.parse(row[2].getContents()));
			}			
			if(row[3].getContents()!=null && !row[3].getContents().trim().isEmpty()){
					pg.setDataIscrizioneRI(sdf.parse(row[3].getContents()));
			}
			if(row[4].getContents()!=null && !row[4].getContents().trim().isEmpty()){
					pg.setDataTermine(sdf.parse(row[4].getContents()));
			}
			pg.setDenominazione(row[5].getContents());
			pg.setNumeroRI(row[6].getContents());
			pg.setPatritaIva(row[7].getContents());
			pg.setFormaGiuridica(getCodeValue(row[8].getContents()));

			pg.setCodiceDitta(row[9].getContents());
			pg.setApp("ARPAV");

			saveOnTarget(pg);
			saveMapping(row[0].getContents(), pg);
		} else {
			log.info("Already imported PersoneGiuridiche with id: "
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
	public PersoneGiuridiche getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
			MapPersoneGiuridicheArpav map = targetEm.find(MapPersoneGiuridicheArpav.class, idexcel);

			if (map != null) {
			// tiro su dal db phi l'entità
				PersoneGiuridiche lp = targetEm.find(PersoneGiuridiche.class, map.getIdphi());
				return lp;
		}
		return null;
	}

	private void saveMapping(String sourceId, PersoneGiuridiche target) {
		MapPersoneGiuridicheArpav map = new MapPersoneGiuridicheArpav();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "PersoneGiuridiche piva: "
				+ target.getPatritaIva() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}
}
