package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.*;
import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapImpSoll;

public class ImpSollImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(ImpSollImporter.class.getName());

	private static ImpSollImporter instance = null;

	public static ImpSollImporter getInstance() {
		if (instance == null) {
			instance = new ImpSollImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		ImpSoll impSoll = getMapped(row[1].getContents(), row[3].getContents(), row[4].getContents(), row[27].getContents(), row[26].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (impSoll == null) {
			impSoll = new ImpSoll();
			impSoll.setCreatedBy(this.getClass().getSimpleName() + ulss);
			impSoll.setCreationDate(new Date());

			impSoll.setSigla(row[1].getContents());
			impSoll.setMatricola(row[3].getContents());
			impSoll.setAnno(row[4].getContents());
			impSoll.setNote(row[5].getContents());
			impSoll.setCode(getCodeValue(row[6].getContents()));
			if(row[7].getContents()!=null && !row[7].getContents().trim().isEmpty()){
				impSoll.setSedi(getSedi(row[6].getContents()));
			}
			if(row[8].getContents()!=null && !row[8].getContents().trim().isEmpty()){
				impSoll.setSedeInstallazione(getSediInstallazione(row[8]
					.getContents()));

				if(impSoll.getSedeInstallazione()==null) {
					log.error("Unable to find Sede Installazione with idexcel: "+row[8].getContents());
				}
			}
			if(impSoll.getSedi()==null && impSoll.getSedeInstallazione()!=null) {
				impSoll.setSedi(impSoll.getSedeInstallazione().getSede());
				if(impSoll.getSedi()!=null) {
					impSoll.setIndirizzoSped(impSoll.getSedi().getIndirizzoSpedPrinc());
				}
			}

//			if(row[8].getContents()!=null && !row[8].getContents().trim().isEmpty()){
//				impSoll.setIndirizzoSped(getIndirizzoSpedPrinc(row[8].getContents()));
//			}
			impSoll.setSubTypeSoll(getCodeValue(row[2].getContents()));
			impSoll.setSubType(getCodeValue(row[10].getContents()));
			impSoll.setStatoImpianto(getCodeValue(row[11].getContents()));
//			if(row[12]!=null && row[12].getContents()!=null){
//				impSoll.setDataVariazioneStato(sdf.parse(row[12].getContents()));
//			}
			if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
				try{
					impSoll.setPortata(Double.parseDouble(row[12].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing Portata for impSoll #"+
							row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
							"-"+row[26].getContents());
				}
			}
			
			impSoll.setNumeroFabbrica(row[13].getContents());
			impSoll.setTipoFabb(row[14].getContents());
			impSoll.setCostruttore(row[15].getContents());
			if(row[16]!=null && !row[16].getContents().trim().isEmpty()){
				try {
					impSoll.setDataCollaudo(sdf.parse(row[16].getContents()));
				}catch(ParseException e) {
					log.error("Error parsing data collaudo for impSoll #"+
							row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
							"-"+row[26].getContents());
				}
			}
			impSoll.setCompetenza(getCodeValue(row[17].getContents()));
			impSoll.setVelocitaMax(row[18].getContents());
			if(row[19]!=null && !row[19].getContents().trim().isEmpty()){
				try{
					impSoll.setCostruzione(Integer.parseInt(row[19].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing anno costruzione for impSoll #"+
							row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
							"-"+row[26].getContents());
				}
			}
			
			
			impSoll.setMarcaturaCe(controlBoolean(row[20].getContents().trim()));
			impSoll.setCostrRadioc(row[21].getContents());
			impSoll.setNumRadioc(row[22].getContents());
			if(row[23]!=null && !row[23].getContents().trim().isEmpty()){
				try {
					impSoll.setDataInstRadioc(sdf.parse(row[23].getContents()));
				}catch(ParseException e) {
					log.error("Error parsing data installazione radioc for impSoll #"+
							row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
							"-"+row[26].getContents());
				}
			}

			impSoll.setEnteVerificatore(getCodeValue(row[24].getContents()));
			if(row[25]!=null && !row[25].getContents().trim().isEmpty()){
				try {
					impSoll.setDataAssegnazione(sdf.parse(row[25].getContents()));
				}catch(ParseException e) {
					log.error("Error parsing data assegnazione for impSoll #"+
							row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
							"-"+row[26].getContents());
				}

			}
			
			saveOnTarget(impSoll);
			saveMapping(row[1].getContents(), row[3].getContents(), row[4]
					.getContents(), row[27].getContents(), row[26].getContents(), impSoll);
		} else {
			log.info("Already imported ImpSoll with id: "
					+ row[1].getContents()+"-"+row[3].getContents()+"-"+row[4].getContents()+"-"+row[27].getContents()+
					"-"+row[26].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public ImpSoll getMapped(String sigla, String matr, String anno, String subtype, String dep) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
//		String hqlMapping = "SELECT m FROM MapImpSoll m WHERE m.idexcel = :id";
//		Query qMapping = targetEm.createQuery(hqlMapping);
//		qMapping.setParameter("id", idexcel);
		
		String hqlMapping = "SELECT m FROM MapImpSoll m WHERE m.sigla = :sigla " +
				"AND m.matricola = :matr AND m.anno = :annno AND m.subtype = :sub AND m.dipartimento = :dep";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("sigla", sigla);
		qMapping.setParameter("matr", matr);
		qMapping.setParameter("annno", anno);
		qMapping.setParameter("sub", subtype);
		qMapping.setParameter("dep", dep);

		List<MapImpSoll> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapImpSoll map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM ImpSoll e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<ImpSoll> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}
	
	private Boolean controlBoolean(String data) {
		if (data != null) {
			if (("1").equals(data) || "SI".equals(data))
				return true;
			else if(("0").equals(data) || "NO".equals(data))
				return false;
		} 
		
		return null;
	}
	
	private void saveMapping(String sigla, String matr, String anno, String dep, String subtype, ImpSoll target) {
		MapImpSoll map = new MapImpSoll();
//		map.setIdexcel(sourceId);
//		map.setIdphi(target.getInternalId());
//		map.setCopiedBy(target.getCreatedBy());
//		map.setCopyDate(new Date());

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
				+ sigla+"-"+matr+"-"+subtype+"-"+anno+"-"+dep + ". " + "ImpSoll id: "
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

	public void updateVerifIndicator(Long impSollId){
		ImpSoll impSoll = targetEm.find(ImpSoll.class, impSollId);

		if (impSoll!=null && impSoll.getVerificaImp()==null && impSoll.getVerificaImp().size()<=0) {
			impSoll.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpSoll with internalId "+impSoll.getInternalId());

			saveOnTarget(impSoll);
			return;
		}

		int actualVerifNum = 0;
		for(VerificaImp ver : impSoll.getVerificaImp()){
			if(Boolean.TRUE.equals(ver.getPre())){
				continue;
			}
			actualVerifNum++;
		}

		if(actualVerifNum == 0){
			impSoll.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpSoll with internalId "+impSoll.getInternalId());
		}else{
			impSoll.setVerificheLong(1L);
			//log.info("Set verificheLong at 1 on ImpSoll with internalId "+impSoll.getInternalId());
		}

		saveOnTarget(impSoll);
		return;

	}


}
