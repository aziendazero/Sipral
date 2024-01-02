package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.*;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapImpTerra;

public class ImpTerraImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(ImpTerraImporter.class.getName());

	private static ImpTerraImporter instance = null;

	public static ImpTerraImporter getInstance() {
		if (instance == null) {
			instance = new ImpTerraImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		String subTypeImp = getCodeValue(row[11].getContents()).getCurrentTranslation();
		int lengthSubType = subTypeImp.length();
		subTypeImp = subTypeImp.substring(lengthSubType-1, lengthSubType);

		ImpTerra impTerra = getMapped(row[1].getContents(), row[2].getContents(), row[3].getContents(), 
				subTypeImp, row[34].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (impTerra == null) {
			impTerra = new ImpTerra();
			impTerra.setCreatedBy(this.getClass().getSimpleName() + ulss);
			impTerra.setCreationDate(new Date());

			impTerra.setSigla(row[1].getContents());
			impTerra.setMatricola(row[2].getContents());
			impTerra.setAnno(row[3].getContents());
			impTerra.setNote(row[4].getContents());
			impTerra.setCode(getCodeValue(row[33].getContents()));
			if(row[8].getContents()!=null && !row[8].getContents().trim().isEmpty()){
				impTerra.setSedi(getSedi(row[8].getContents()));
			}
			if(row[9].getContents()!=null && !row[9].getContents().trim().isEmpty()){

				impTerra.setSedeInstallazione(getSediInstallazione(row[9]
						.getContents()));

				if(impTerra.getSedeInstallazione()==null) {
					log.error("Unable to find Sede Installazione with idexcel: "+row[9].getContents());
				}
			}
			if(impTerra.getSedi()==null && impTerra.getSedeInstallazione()!=null) {
				impTerra.setSedi(impTerra.getSedeInstallazione().getSede());
				if(impTerra.getSedi()!=null) {
					impTerra.setIndirizzoSped(impTerra.getSedi().getIndirizzoSpedPrinc());
				}
			}

			//			impTerra.setIndirizzoSped(getIndirizzoSpedPrinc(row[11]
			//					.getContents()));
			impTerra.setSubTypeTerra(getCodeValue(row[11].getContents()));
			//impTerra.setStruttAutopCode(getCodeValue(row[13].getContents()));

			impTerra.setTipologia(getCodeValue(row[19].getContents()));
			impTerra.setTipologiaTesto(row[20].getContents());

			impTerra.setAste(row[21].getContents());
			if(impTerra.getAste()!=null && !impTerra.getAste().isEmpty() && impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.41"));
			}

			impTerra.setSuperf01(row[22].getContents());
			impTerra.setSuperf02(row[23].getContents());
			impTerra.setSuperf03(row[24].getContents());
			if(((impTerra.getSuperf01()!=null && !impTerra.getSuperf01().isEmpty()) ||
					(impTerra.getSuperf02()!=null && !impTerra.getSuperf02().isEmpty()) ||
					(impTerra.getSuperf03()!=null && !impTerra.getSuperf03().isEmpty()))
					&& impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.42"));
			}

			impTerra.setRaggruppati01(row[25].getContents());
			if(impTerra.getRaggruppati01()!=null && !impTerra.getRaggruppati01().isEmpty() && impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.43"));
			}

			impTerra.setRaggruppati02(row[26].getContents());
			if(impTerra.getRaggruppati02()!=null && !impTerra.getRaggruppati02().isEmpty() && impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.44"));
			}

			impTerra.setDisperdenti(row[27].getContents());
			if(impTerra.getDisperdenti()!=null && !impTerra.getDisperdenti().isEmpty() && impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.46"));
			}

			if(row[5].getContents()!=null && !row[5].getContents().trim().isEmpty()){
				impTerra.setStruttAutopNum(Integer.parseInt(row[5].getContents()));
			}
			if(impTerra.getStruttAutopNum()!=null && impTerra.getTipologia()==null) {
				impTerra.setTipologia(getCodeValue("arpav.imp.terra.tipologiaie.48"));
			}

			impTerra.setSubTypeB(getCodeValue(row[12].getContents()));
			if(row[6].getContents()!=null && !row[6].getContents().isEmpty()){
				try{
					impTerra.setPot(Double.parseDouble(row[6].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing pot for ImpTerra #"+
							row[1].getContents() + "-" + row[2].getContents() +
							"-" + row[3].getContents() + "-" + subTypeImp +
							"-" + row[34].getContents());
				}
			}
			impTerra.setDispersori(row[30].getContents());
			if(row[7].getContents()!=null && !row[7].getContents().trim().isEmpty()){
				impTerra.setCabineNum(Integer.parseInt(row[7].getContents()));
			}

			impTerra.setCabineCode(getCodeValue(row[13].getContents()));
			if(impTerra.getCabineCode()==null && impTerra.getCabineNum()!=null) {
				impTerra.setCabineCode(getCodeValue("generic.YN.Y"));
			}else if(impTerra.getCabineCode()==null) {
				impTerra.setCabineCode(getCodeValue("generic.YN.N"));
			}
			//impTerra.setImpAutoprod(null);

			impTerra.setAreaPeric(row[31].getContents());
			impTerra.setCi0(getCodeValue(row[14].getContents()));
			impTerra.setCi1(getCodeValue(row[15].getContents()));
			impTerra.setCi2(getCodeValue(row[16].getContents()));
			impTerra.setCi3(getCodeValue(row[17].getContents()));

			impTerra.setStatoImpianto(getCodeValue(row[18].getContents()));

			//			impTerra.setTipologiaTesto(row[21].getContents());
			//			impTerra.setParafulmini(row[21].getContents());
			//impTerra.setIsolanti01(row[26].getContents());
			//impTerra.setIsolanti02(row[28].getContents());
			//impTerra.setMetalliche(row[30].getContents());
			//impTerra.setSerbatoi(row[31].getContents());
			//impTerra.setCantieri(row[33].getContents());

			if(row[28].getContents()!=null && !row[28].getContents().trim().isEmpty()){	
				impTerra.setDataCollaudo(sdf.parse(row[28].getContents()));
			}

			impTerra.setImpColl(row[29].getContents());
			impTerra.setCompetenza(getCodeValue(row[32].getContents()));

			impTerra.setEnteVerificatore(getCodeValue(row[41].getContents()));
			if(row[42].getContents()!=null && !row[42].getContents().trim().isEmpty()){
				impTerra.setDataAssegnazione(sdf.parse(row[42].getContents()));
			}

			saveOnTarget(impTerra);
			saveMapping(row[1].getContents(), row[2].getContents(), row[3]
					.getContents(), subTypeImp, row[34].getContents(), impTerra);
		} else {
			log.info("Already imported ImpTerra with complex id: "
					+ row[1].getContents() + "-" + row[2].getContents() +
					"-" + row[3].getContents() + "-" + subTypeImp +
					"-" + row[34].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public ImpTerra getMapped(String sigla, String matr, String anno, String subtype, String dep) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapImpTerra m WHERE m.sigla = :sigla " +
				"AND m.matricola = :matr AND m.anno = :annno AND m.subtype = :sub AND m.dipartimento = :dep";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("sigla", sigla);
		qMapping.setParameter("matr", matr);
		qMapping.setParameter("annno", anno);
		qMapping.setParameter("sub", subtype);
		qMapping.setParameter("dep", dep);
		List<MapImpTerra> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapImpTerra map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM ImpTerra e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<ImpTerra> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sigla, String matr, String anno,  String subtype, String dep, ImpTerra target) {
		MapImpTerra map = new MapImpTerra();
		map.setIdexcel(sigla+"-"+matr+"-"+subtype+"-"+anno+"-"+dep);
		map.setSigla(sigla);
		map.setMatricola(matr);
		map.setAnno(anno);
		map.setSubtype(subtype);
		map.setDipartimento(dep);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		/*thislog.info("New imported object. Source id: "
				+ sigla+"-"+matr+"-"+subtype+"-"+anno+"-"+dep + ". " + "ImpTerra id: "
				+ target.getInternalId() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());*/
	}

	//	private IndirizzoSped getIndirizzoSpedPrinc(String id) {
	//		IndirizzoSpedImporter indSpedImp = IndirizzoSpedImporter.getInstance();
	//		return indSpedImp.getMapped(id);
	//	}

	private Sedi getSedi(String id) {
		SediAddebitoImporter sediAddImp = SediAddebitoImporter.getInstance();
		return sediAddImp.getMapped(id);
	}

	private SediInstallazione getSediInstallazione(String id) {
		SediInstallazioneImporter sedInstImp = SediInstallazioneImporter.getInstance();
		SediInstallazione mapped = sedInstImp.getMappedLike(id);
		if(mapped == null) {
			thislog.info("Unable to find mapped SediInstallazione with id: "+id);
		}
		return sedInstImp.getMappedLike(id);
	}

	public void updateVerifIndicator(Long impTerraId){
		ImpTerra impTerra = targetEm.find(ImpTerra.class, impTerraId);

		if (impTerra!=null && impTerra.getVerificaImp()==null && impTerra.getVerificaImp().size()<=0) {
			impTerra.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpTerra with internalId "+impTerra.getInternalId());

			saveOnTarget(impTerra);
			return;
		}

		int actualVerifNum = 0;
		for(VerificaImp ver : impTerra.getVerificaImp()){
			if(Boolean.TRUE.equals(ver.getPre())){
				continue;
			}
			actualVerifNum++;
		}

		if(actualVerifNum == 0){
			impTerra.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpTerra with internalId "+impTerra.getInternalId());
		}else{
			impTerra.setVerificheLong(1L);
			//log.info("Set verificheLong at 1 on ImpTerra with internalId "+impTerra.getInternalId());
		}

		saveOnTarget(impTerra);
		return;

	}


}
