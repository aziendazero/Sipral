package com.phi.db.arpav.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapSediAddebito;
import com.phi.db.arpav.mapping.MapSediInstallazione;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.parix.json.detail.PersonaGiuridica;

public class SediAddebitoImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(SediAddebitoImporter.class.getName());

	private static SediAddebitoImporter instance = null;

	public static SediAddebitoImporter getInstance() {
		if (instance == null) {
			instance = new SediAddebitoImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) {
		Sedi saddebito = getMapped(row[0].getContents());
		if (saddebito == null) {
			saddebito = new Sedi();
			saddebito.setCreatedBy(this.getClass().getSimpleName() + ulss);
			saddebito.setCreationDate(new Date());
			saddebito.setSedeAddebito(true);
			saddebito.setDenominazioneUnitaLocale(row[1].getContents());

			//			AD addr = new AD();
			//			addr.setStr(row[2].getContents());
			//			addr.setBnr(row[3].getContents());
			//			addr.setCty(row[4].getContents());
			//			addr.setCpa(row[5].getContents());
			//			addr.setZip(row[6].getContents());
			//			addr.setAdl(row[7].getContents());
			//			addr.setCode(getComune(row[15].getContents()));
			saddebito.setAddr(buildAD(row[2].getContents(), row[3].getContents(), row[4].getContents(), 
					row[5].getContents(), row[6].getContents(), row[7].getContents(), row[15].getContents()));
			saddebito.setCodContabilita(row[8].getContents());
			saddebito.setImpSpesa(row[9].getContents());
			saddebito.setCig(row[10].getContents());

			TEL tel = new TEL();
			tel.setMail(row[11].getContents());
			tel.setAs(row[22].getContents());
			tel.setBad(row[23].getContents());

			saddebito.setTelecom(tel);

			saddebito.setNote(row[12].getContents());
			if(row[13].getContents()!=null && !row[13].getContents().trim().isEmpty()){
				saddebito.setPersonaGiuridica(getPersonaGiuridica(row[13].getContents())); // per ogni sede di addebito esiste una ditta con lo stesso id
			}
			saddebito.setStato(getCountry(row[16].getContents()));
			saddebito.setClasseEconomica(getCodeValue(row[17].getContents()));
			saddebito.setTipoAttivita(getCodeValue(row[18].getContents()));
			saddebito.setSettore(getCodeValue(row[19].getContents()));
			saddebito.setTipoUtente(getCodeValue(row[20].getContents()));
			saddebito.setEsenzione(getCodeValue(row[21].getContents()));
			saddebito.setSedePrincipale(Boolean.TRUE.equals(controlBoolean(row[24].getContents()))); //boolean
			if(row[25].getContents()!=null && !row[25].getContents().trim().isEmpty()){
				saddebito.setProgressivoUnitaLocale(Integer.parseInt(row[25]
						.getContents()));}

			//save sede addebito
			saveOnTarget(saddebito);
			saveMapping(row[0].getContents(), saddebito);
			//add and create new IndirizzoSped in automatic and copy entities inside
			IndirizzoSped insp = new IndirizzoSped();
			insp.setCreatedBy(this.getClass().getSimpleName() + ulss);
			insp.setCreationDate(new Date());
			insp.setDenominazione(row[1].getContents());
			//			AD addr2 = new AD();
			//			addr2.setStr(row[2].getContents());
			//			addr2.setBnr(row[3].getContents());
			//			addr2.setCty(row[4].getContents());
			//			addr2.setCpa(row[5].getContents());
			//			addr2.setZip(row[6].getContents());
			//			addr2.setAdl(row[7].getContents());
			//			addr2.setCode(getComune(row[15].getContents()));
			insp.setAddr(buildAD(row[2].getContents(), row[3].getContents(), row[4].getContents(), 
					row[5].getContents(), row[6].getContents(), row[7].getContents(), row[15].getContents()));
			TEL tel2 = new TEL();
			tel2.setMail(row[11].getContents());
			tel.setAs(row[22].getContents());
			tel.setBad(row[23].getContents());

			insp.setTelecom(tel2);
			//	insp.setNote(row[10].getContents());
			//	if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
			//			Sedi o = getSedi(row[12].getContents());
			if (saddebito != null) {
				if (insp.getSedi() == null)
					insp.setSedi(new ArrayList<Sedi>());

				if (!insp.getSedi().contains(saddebito))
					insp.getSedi().add(saddebito);

			}

			//save again IndirizzoSped
			saveOnTarget(insp);

			//set IndirizzoSped in sedeAddebitoPrinc and sedeAddebitoList
			saddebito.setIndirizzoSpedPrinc(insp);

			if(saddebito.getIndirizzoSped()==null)
				saddebito.setIndirizzoSped(new ArrayList<IndirizzoSped>());
			if (!saddebito.getIndirizzoSped().contains(insp))
				saddebito.getIndirizzoSped().add(insp);

			//save again sedeaddebito
			saveOnTarget(saddebito);

		} else {
			log.info("Already imported SediAddebito/sedi with id: "
					+ row[0].getContents());

			/*
			if(saddebito.getEsenzione() == null) {
				saddebito.setEsenzione(getCodeValue(row[21].getContents()));
				saveOnTarget(saddebito);
				log.info("Updated SediAddebito/sedi with id: " + row[0].getContents());
			}*/
		}
	}

	//	public void updateIndirizzoPrincipale(Cell[] row) {
	//		Sedi saddebito = getMapped(row[0].getContents());
	//		saddebito.setIndirizzoSpedPrinc(getIndirizzoSpedPrinc(row[14]
	//				.getContents()));
	//		thislog.info("update  indirizzoSedePrincipale del sede addebito:"+saddebito.getDenominazioneUnitaLocale());
	//		saveOnTarget(saddebito);
	//	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public Sedi getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		MapSediAddebito map = targetEm.find(MapSediAddebito.class, idexcel);
		if (map != null) {
			// tiro su dal db phi l'entità
			Sedi lp = targetEm.find(Sedi.class, map.getIdphi());
			return lp;
		}
		return null;
	}

	private void saveMapping(String sourceId, Sedi target) {
		MapSediAddebito map = new MapSediAddebito();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "Sede addebito id: "
				+ target.getDenominazioneUnitaLocale() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}

	//	private IndirizzoSped getIndirizzoSpedPrinc(String id) {
	//		IndirizzoSpedImporter indSpedImp = IndirizzoSpedImporter.getInstance();
	//		return indSpedImp.getMapped(id);
	//	}

	private PersoneGiuridiche getPersonaGiuridica(String id) {
		PersoneGiuridicheImporter persGiudImp = PersoneGiuridicheImporter.getInstance();
		return persGiudImp.getMapped(id);
	}

	private Boolean controlBoolean(String data) {
		if (data != null) {
			if ("1".equals(data) || "TRUE".equals(data))
				return true;
		} else
			return false;

		return null;
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
}
