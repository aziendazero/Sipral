package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapImpMonta;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;

public class ImpMontaImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(ImpMontaImporter.class.getName());

	private static ImpMontaImporter instance = null;

	public static ImpMontaImporter getInstance() {
		if (instance == null) {
			instance = new ImpMontaImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		ImpMonta impmonta = getMapped(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[35].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (impmonta == null) {
			impmonta = new ImpMonta();
			impmonta.setCreatedBy(this.getClass().getSimpleName() + ulss);
			impmonta.setCreationDate(new Date());

			impmonta.setSigla(row[1].getContents());
			impmonta.setMatricola(row[2].getContents());
			impmonta.setAnno(row[3].getContents());
			impmonta.setNote(row[4].getContents());
			impmonta.setCostruttore(row[5].getContents());
			if(row[6].getContents()!=null && !row[6].getContents().trim().isEmpty()){
				try{
					impmonta.setPortata(Double.parseDouble(row[6].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing portata for ImpMonta #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[35].getContents());
				}
			}
			if(row[7].getContents()!=null && !row[7].getContents().trim().isEmpty()){
				try{
					impmonta.setFermate(Integer.parseInt(row[7].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing fermate for ImpMonta #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[35].getContents());
				}
			}
			
			impmonta.setCode(getCodeValue(row[8].getContents()));
			if(row[9].getContents()!=null && !row[9].getContents().trim().isEmpty()){
				impmonta.setSedi(getSedi(row[9].getContents()));
			}
			if(row[10].getContents()!=null && !row[10].getContents().trim().isEmpty()){
				impmonta.setSedeInstallazione(getSediInstallazione(row[10]
						.getContents()));
				
				if(impmonta.getSedeInstallazione()==null) {
					log.error("Unable to find Sede Installazione with idexcel: "+row[10].getContents());
				}
			}
			if(impmonta.getSedi()==null && impmonta.getSedeInstallazione()!=null) {
				impmonta.setSedi(impmonta.getSedeInstallazione().getSede());
				if(impmonta.getSedi()!=null) {
					impmonta.setIndirizzoSped(impmonta.getSedi().getIndirizzoSpedPrinc());
				}
			}

//			if(row[11].getContents()!=null && !row[11].getContents().trim().isEmpty()){
//				impmonta.setIndirizzoSped(getIndirizzoSpedPrinc(row[11]
//						.getContents()));
//			}
			impmonta.setCategoria(getCodeValue(row[12].getContents()));
			impmonta.setStatoImpianto(getCodeValue(row[13].getContents()));
			impmonta.setManutentore(getCodeValue(row[14].getContents()));
			impmonta.setDestinazione(getCodeValue(row[15].getContents()));
			
			
			impmonta.setDistanza(getCodeValue(row[16].getContents()));
			impmonta.setNumeroFabbrica(row[17].getContents());
			impmonta.setCorsa(getCodeValue(row[18].getContents()));
			impmonta.setVelocita(getCodeValue(row[19].getContents()));
			impmonta.setTrazione(getCodeValue(row[20].getContents()));
			impmonta.setMotore(getCodeValue(row[21].getContents()));
			impmonta.setManovra(getCodeValue(row[22].getContents()));
			impmonta.setPorte(getCodeValue(row[23].getContents()));	
			if(row[24].getContents()!=null && !row[24].getContents().trim().isEmpty()){
				impmonta.setDataCollaudo(sdf.parse(row[24].getContents()));
			}
			if(row[25].getContents()!=null && !row[25].getContents().trim().isEmpty()){
				impmonta.setLicenza(sdf.parse(row[25].getContents()));
			}
			if(row[26].getContents()!=null && !row[26].getContents().trim().isEmpty()){
				impmonta.setDataVariazioneStato(sdf.parse(row[26].getContents()));
			}
			if(row[27].getContents()!=null && !row[27].getContents().trim().isEmpty()){
				impmonta.setLetteraTrasm(sdf.parse(row[27].getContents()));
			}
			impmonta.setProtNumero(row[28].getContents());
			impmonta.setUtenteLettera(getCodeValue(row[29].getContents()));
			impmonta.setEnteVerificatore(getCodeValue(row[30].getContents()));
			if(row[31].getContents()!=null && !row[31].getContents().trim().isEmpty()){
				impmonta.setDataAssegnazione(sdf.parse(row[31].getContents()));
			}
			impmonta.setMatrcomune(row[32].getContents());
			impmonta.setAmministratore(getCodeValue(row[33].getContents()));
			if(row[34].getContents()!=null && !row[34].getContents().trim().isEmpty()){
				try{
					impmonta.setCostruzione(Integer.parseInt(row[34].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing costruzione for ImpMonta #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[35].getContents());
				}
			}
			
			
			
			saveOnTarget(impmonta);
			saveMapping(row[1].getContents(), row[2].getContents(), row[3].getContents(), row[35].getContents(), impmonta);
		} else {
			log.info("Already imported ImpMonta with id: "
					+ row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[35].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public ImpMonta getMapped(String sigla, String matr, String anno, String dep) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
//		String hqlMapping = "SELECT m FROM MapImpMonta m WHERE m.idexcel = :id";
//		Query qMapping = targetEm.createQuery(hqlMapping);
//		qMapping.setParameter("id", idexcel);

		String hqlMapping = "SELECT m FROM MapImpMonta m WHERE m.sigla = :sigla " +
				"AND m.matricola = :matr AND m.anno = :annno AND m.dipartimento = :dep";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("sigla", sigla);
		qMapping.setParameter("matr", matr);
		qMapping.setParameter("annno", anno);
		qMapping.setParameter("dep", dep);
		
		List<MapImpMonta> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapImpMonta map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM ImpMonta e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<ImpMonta> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sigla, String matr, String anno, String dep, ImpMonta target) {
		MapImpMonta map = new MapImpMonta();
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

		/*thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "ImpMonta id: "
				+ target.getSigla() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());*/
	}
//
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

	public void updateVerifIndicator(Long impMontaId){
		ImpMonta impMonta = targetEm.find(ImpMonta.class, impMontaId);

		if (impMonta!=null && impMonta.getVerificaImp()==null && impMonta.getVerificaImp().size()<=0) {
			impMonta.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpMonta with internalId "+impMonta.getInternalId());

			saveOnTarget(impMonta);
			return;
		}

		int actualVerifNum = 0;
		for(VerificaImp ver : impMonta.getVerificaImp()){
			if(Boolean.TRUE.equals(ver.getPre())){
				continue;
			}
			actualVerifNum++;
		}

		if(actualVerifNum == 0){
			impMonta.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpMonta with internalId "+impMonta.getInternalId());
		}else{
			impMonta.setVerificheLong(1L);
			//log.info("Set verificheLong at 1 on ImpMonta with internalId "+impMonta.getInternalId());
		}

		saveOnTarget(impMonta);
		return;

	}

}
