package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapImpPress;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.role.Operatore;

public class ImpPressImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(ImpPressImporter.class.getName());

	private static ImpPressImporter instance = null;

	public static ImpPressImporter getInstance() {
		if (instance == null) {
			instance = new ImpPressImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		ImpPress impPress = getMapped(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[38].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (impPress == null) {
			impPress = new ImpPress();
			impPress.setCreatedBy(this.getClass().getSimpleName() + ulss);
			impPress.setCreationDate(new Date());

			impPress.setSigla(row[1].getContents());
			impPress.setMatricola(row[2].getContents());
			impPress.setAnno(row[3].getContents());
			if(row[4].getContents()!=null && !row[4].getContents().trim().isEmpty()){
				try{
					impPress.setPressBar1(Double.parseDouble(row[4].getContents().replace(",", ".")));
				}catch(NumberFormatException e){
					log.error("Error parsing pressbar1 for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			if(row[5].getContents()!=null && !row[5].getContents().trim().isEmpty()){
				try{
					impPress.setPressBar2(Double.parseDouble(row[5].getContents().replace(",", ".")));
				}catch(NumberFormatException e){
					log.error("Error parsing pressbar2 for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			if(row[6].getContents()!=null && !row[6].getContents().trim().isEmpty()){
				try{
					impPress.setCapacita(Double.parseDouble(row[6].getContents().replace(",", ".")));
				}catch(NumberFormatException e){
					log.error("Error parsing capacita for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			if(row[7].getContents()!=null && !row[7].getContents().trim().isEmpty()){
				try{
					impPress.setSuperficie(Double.parseDouble(row[7].getContents().replace(",", ".")));
				}catch(NumberFormatException e){
					log.error("Error parsing superficie for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			impPress.setTempS1(getCodeValue(row[8].getContents()));
			impPress.setTempV1(row[9].getContents());
			impPress.setTempS2(getCodeValue(row[10].getContents()));
			impPress.setTempV2(row[11].getContents());
			impPress.setFluido(getCodeValue(row[12].getContents().replace("ver.tipofluidi", "imp.pressione.fluidi")));
			impPress.setCategoriaRischio(getCodeValue(row[13].getContents().replace("ver.catrischio", "imp.pressione.categorierischi")));
			if(row[14].getContents()!=null && !row[14].getContents().trim().isEmpty()){
				try{
					impPress.setProducibilita(Double.parseDouble(row[14].getContents().replace(",", ".")));
				}catch(NumberFormatException e){
					log.error("Error parsing producibilita for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			impPress.setCode(getCodeValue(row[15].getContents()));
			if(row[16].getContents()!=null && !row[16].getContents().trim().isEmpty()){
				impPress.setSedi(getSedi(row[16].getContents()));
			}
			if(row[17].getContents()!=null && !row[17].getContents().trim().isEmpty()){
				impPress.setSedeInstallazione(getSediInstallazione(row[17]
						.getContents()));

				if(impPress.getSedeInstallazione()==null) {
					log.error("Unable to find Sede Installazione with idexcel: "+row[17].getContents());
				}
			}
			if(impPress.getSedi()==null && impPress.getSedeInstallazione()!=null) {
				impPress.setSedi(impPress.getSedeInstallazione().getSede());
				if(impPress.getSedi()!=null) {
					impPress.setIndirizzoSped(impPress.getSedi().getIndirizzoSpedPrinc());
				}
			}

			//			if(row[18].getContents()!=null && !row[18].getContents().trim().isEmpty()){
			//				impPress.setIndirizzoSped(getIndirizzoSpedPrinc(row[18]
			//						.getContents()));
			//			}

			impPress.setTipoApparecchio(getCodeValue(row[19].getContents()));
			impPress.setStatoImpianto(getCodeValue(row[20].getContents()));

			impPress.setCompetenza(getCodeValue(row[21].getContents()));
			impPress.setEsonero(getCodeValue(row[22].getContents()));
			if(row[23].getContents()!=null && !row[23].getContents().trim().isEmpty()){
				impPress.setDataEsonero(sdf.parse(row[23].getContents()));
			}
			if(row[24].getContents()!=null && !row[24].getContents().trim().isEmpty()){
				try{
					impPress.setArtEsonero(Integer.parseInt(row[24].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing esonero for ImpPress #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
				}
			}
			impPress.setCaratteristicheSpec(getCodeValue(row[25].getContents()));
			impPress.setNumeroFabbrica(row[26].getContents());
			if(row[27].getContents()!=null && !row[27].getContents().trim().isEmpty()){
				impPress.setDataCostruzione(sdf.parse(row[27].getContents()));
			}
			impPress.setCostruttore(row[28].getContents());
			impPress.setComodante(getCodeValue(row[29].getContents()));
			impPress.setSezione(row[30].getContents());
			if(row[31].getContents()!=null && !row[31].getContents().trim().isEmpty()){
				impPress.setDataVariazioneStato(sdf.parse(row[31].getContents()));
			}
			impPress.setNote(row[32].getContents());
			impPress.setEnteVerificatore(getCodeValue(row[33].getContents()));
			if(row[34].getContents()!=null && !row[34].getContents().trim().isEmpty()){
				impPress.setDataAssegnazione(sdf.parse(row[34].getContents()));
			}
			if(row[35].getContents()!=null && !row[35].getContents().trim().isEmpty()){
				impPress.setLetteraTrasm(sdf.parse(row[35].getContents()));
			}
			impPress.setProtNumero(row[36].getContents());
			impPress.setUtenteLettera(getCodeValue(row[37].getContents()));







			saveOnTarget(impPress);
			saveMapping(row[1].getContents(), row[2].getContents(), row[3].getContents(), row[38].getContents(), impPress);

		} else {
			log.info("Already imported ImpPress with id: "
					+ row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[38].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 *
	 * @param idexcel
	 * @return
	 */
	public ImpPress getMapped(String sigla, String matr, String anno, String dep) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		//		String hqlMapping = "SELECT m FROM MapImpPress m WHERE m.idexcel = :id";
		//		Query qMapping = targetEm.createQuery(hqlMapping);
		//		qMapping.setParameter("id", idexcel);
		String hqlMapping = "SELECT m FROM MapImpPress m WHERE m.sigla = :sigla " +
				"AND m.matricola = :matr AND m.anno = :annno AND m.dipartimento = :dep";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("sigla", sigla);
		qMapping.setParameter("matr", matr);
		qMapping.setParameter("annno", anno);
		qMapping.setParameter("dep", dep);

		List<MapImpPress> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapImpPress map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM ImpPress e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<ImpPress> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sigla, String matr, String anno, String dep, ImpPress target) {
		MapImpPress map = new MapImpPress();
		//		map.setIdexcel(sourceId);
		//		map.setIdphi(target.getInternalId());
		//		map.setCopiedBy(target.getCreatedBy());
		//		map.setCopyDate(new Date());

		map.setIdexcel(sigla+"-"+matr+"-"+anno+"-"+dep);
		map.setSigla(sigla);
		map.setMatricola(matr);
		map.setAnno(anno);
		map.setDipartimento(dep);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		saveOnTarget(map);
		saveOnTarget(map);

		/*thislog.info("New imported object. Source id: "
				+ sigla+"-"+matr+"-"+anno+"-"+dep + ". " + "ImpPress id: "
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
		return sedInstImp.getMappedLike(id);
	}

	public void updateVerifIndicator(Long impPressId){
		ImpPress impPress = targetEm.find(ImpPress.class, impPressId);

		if (impPress!=null && impPress.getVerificaImp()==null && impPress.getVerificaImp().size()<=0) {
			impPress.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpPress with internalId "+impPress.getInternalId());

			saveOnTarget(impPress);
			return;
		}

		int actualVerifNum = 0;
		for(VerificaImp ver : impPress.getVerificaImp()){
			if(Boolean.TRUE.equals(ver.getPre())){
				continue;
			}
			actualVerifNum++;
		}

		if(actualVerifNum == 0){
			impPress.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpPress with internalId "+impPress.getInternalId());
		}else{
			impPress.setVerificheLong(1L);
			//log.info("Set verificheLong at 1 on ImpPress with internalId "+impPress.getInternalId());
		}

		saveOnTarget(impPress);
		return;

	}
}
