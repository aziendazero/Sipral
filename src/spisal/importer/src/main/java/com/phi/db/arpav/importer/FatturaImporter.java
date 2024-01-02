package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapFattura;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.Fattura;
import com.phi.entities.baseEntity.VerificaImp;

import jxl.Cell;

public class FatturaImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(FatturaImporter.class.getName());

	private static FatturaImporter instance = null;

	public static FatturaImporter getInstance() {
		if (instance == null) {
			instance = new FatturaImporter();
		}
		return instance;
	}

	public Fattura importRow(Cell[] row) throws ParseException {
		if(row[61].getContents()==null || row[61].getContents().trim().isEmpty()){
			log.error("Unable to get Gruppo for Fattura at row " + (row[61].getRow()+1));
			return null;
		}
		String gruppo = row[61].getContents();

		if(row[63].getContents()==null || row[63].getContents().trim().isEmpty()){
			log.error("Unable to get Anno for Fattura at row " + (row[63].getRow()+1));
			return null;
		}
		String anno = row[63].getContents();

		if(row[60].getContents()==null || row[60].getContents().trim().isEmpty()){
			log.error("Unable to get dep for Fattura at row " + (row[60].getRow()+1));
			return null;
		}
		String dep = row[60].getContents();

		String mappedId = gruppo+"-"+anno+"-"+dep;
		Fattura fattura = getMapped(mappedId);
		if (fattura == null) {
			fattura = new Fattura();
			fattura.setCreatedBy(this.getClass().getSimpleName() + ulss);
			fattura.setIsActive(false);

			SimpleDateFormat sdfFattura = new SimpleDateFormat("dd/MM/yyyy");
			Date dateElab = null;
			if(row[67].getContents()!=null && !row[67].getContents().trim().isEmpty()){
				dateElab = sdfFattura.parse(row[67].getContents());
			}else{
				dateElab = new Date();
			}
			fattura.setCreationDate(dateElab);

			//fattura.setAddebito(new ArrayList<Addebito>());
			if(row[63].getContents()!=null && !row[63].getContents().trim().isEmpty()) {
				try{
					fattura.setAnno(Integer.parseInt(row[63].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing Anno for Fattura #"+
							mappedId);
				}
			}

			fattura.setArchiviata(true);
//			fattura.setAuth();
//			fattura.setContent();
//			fattura.setContentType();

//			fattura.setDataRif

//			fattura.setDescr
//			fattura.setFilename
//			fattura.setFilesize

			if(row[61].getContents()!=null && !row[61].getContents().trim().isEmpty()) {
				try{
					fattura.setGruppo(Integer.parseInt(row[61].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing Gruppo for Fattura #"+
							mappedId);
				}
			}

//			fattura.setImponibile
//			fattura.setMeseAl
//			fattura.setMeseDal
//			fattura.setOperatore
//			fattura.setPersonaGiuridica
//			fattura.setPersonaGiuridicaAdd

			fattura.setServiceDeliveryLocation(findArpav(row[28].getContents()));

//			fattura.setStatusCode

			if(row[62].getContents()!=null && !row[62].getContents().trim().isEmpty()) {
				HashMap<String, String> tipoDoc = new HashMap<String, String>();
				tipoDoc.put("F","02");
				tipoDoc.put("A","03");

				fattura.setTipologiaDocumento(getCodeValueCode("phidic.arpav.fat.tipodoc",tipoDoc.get(row[62].getContents())));
			}


//			fattura.setTipologiaFattura

			//fattura.setVerificaImp(new ArrayList<VerificaImp>());

			saveOnTarget(fattura);
			saveMapping(mappedId, fattura);

		} /*else {
			log.info("Already imported Fattura with mapped id: "
					+ mappedId);
		}*/

		return fattura;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public Fattura getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapFattura m WHERE m.idexcel = :id";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idexcel);
		List<MapFattura> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapFattura map = list.get(0);

			Fattura mapped= targetEm.find(Fattura.class, map.getIdphi());
			
			// tiro su dal db phi l'entità
//			Query qEmp = targetEm
//					.createQuery("SELECT e FROM Fattura e WHERE e.internalId = :id");
//			qEmp.setParameter("id", map.getIdphi());
//			List<Fattura> lp = qEmp.getResultList();
//			if (lp != null && !lp.isEmpty()) {
//				return lp.get(0);
//			}
			
			return mapped;
		}
		return null;
	}

	private void saveMapping(String sourceId, Fattura target) {
		MapFattura map = new MapFattura();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ sourceId + ". " + "Fattura id: "

				+ target.getInternalId() + " " + "on date "
				+ target.getCreationDate());
	}

}
