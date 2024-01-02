package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.*;
import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapImpRisc;

public class ImpRiscImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(ImpRiscImporter.class.getName());

	private static ImpRiscImporter instance = null;

	public static ImpRiscImporter getInstance() {
		if (instance == null) {
			instance = new ImpRiscImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		ImpRisc impRisc = getMapped(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[123].getContents());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (impRisc == null) {
			impRisc = new ImpRisc();
			impRisc.setCreatedBy(this.getClass().getSimpleName() + ulss);
			impRisc.setCreationDate(new Date());

			impRisc.setSigla(row[1].getContents());
			impRisc.setMatricola(row[2].getContents());
			impRisc.setAnno(row[3].getContents());
			impRisc.setNote(row[4].getContents());
			if(row[5].getContents()!=null && !row[5].getContents().trim().isEmpty()){
				try{
					impRisc.setSuperficieRisc(Double.parseDouble(row[5].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing SuperficieRisc for ImpRisc #"+
							row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
				}
			}

			impRisc.setVasiAperti(row[7].getContents());
			impRisc.setVasiChiusi(row[8].getContents());
			impRisc.setNumGen(row[9].getContents());
			impRisc.setCode(getCodeValue(row[10].getContents()));
			if(row[11].getContents()!=null && !row[11].getContents().trim().isEmpty()){
				impRisc.setSedi(getSedi(row[11].getContents()));
			}
			if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
				impRisc.setSedeInstallazione(getSediInstallazione(row[12]
						.getContents()));

				if(impRisc.getSedeInstallazione()==null) {
					log.error("Unable to find Sede Installazione with idexcel: "+row[12].getContents());
				}
			}
			if(impRisc.getSedi()==null && impRisc.getSedeInstallazione()!=null) {
				impRisc.setSedi(impRisc.getSedeInstallazione().getSede());
				if(impRisc.getSedi()!=null) {
					impRisc.setIndirizzoSped(impRisc.getSedi().getIndirizzoSpedPrinc());
				}
			}

			//			if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
			//				impRisc.setIndirizzoSped(getIndirizzoSpedPrinc(row[12]
			//						.getContents()));
			//			}		
			impRisc.setImpianto(getCodeValue(row[14].getContents()));
			impRisc.setDescrizImpianto(getCodeValue(row[15].getContents()));
			impRisc.setDescrizLocali(getCodeValue(row[16].getContents()));
			impRisc.setStatoImpianto(getCodeValue(row[17].getContents()));
			if(row[18].getContents()!=null && !row[18].getContents().trim().isEmpty()){
				impRisc.setDataVariazioneStato(sdf.parse(row[18].getContents()));
			}
			impRisc.setOreGG(row[19].getContents());
			impRisc.setGiorniAA(row[20].getContents());
			if(row[21].getContents()!=null && !row[21].getContents().trim().isEmpty()){
				impRisc.setDataCollaudo(sdf.parse(row[21].getContents()));
			}
			impRisc.setManutentore(getCodeValue(row[22].getContents()));
			if(row[23].getContents()!=null && !row[23].getContents().trim().isEmpty()){
				impRisc.setDataAutocert1(sdf.parse(row[23].getContents()));
			}
			impRisc.setEnteVerificatore(getCodeValue(row[24].getContents()));
			if(row[25].getContents()!=null && !row[25].getContents().trim().isEmpty()){
				impRisc.setDataAssegnazione(sdf.parse(row[25].getContents()));
			}

			List<SchedaGeneratori> genList = null;
			if(row[27].getContents()!=null && !row[27].getContents().trim().isEmpty()){
				genList = new ArrayList<SchedaGeneratori>();

				SchedaGeneratori gen = new SchedaGeneratori();
				gen.setCreatedBy(this.getClass().getSimpleName() + ulss);
				//impRisc.addSchedaGeneratori(gen);
				gen.setImpRisc(impRisc);
				gen.setNumero(1);
				gen.setType(getCodeValue(row[27].getContents()));
				gen.setCostruttore(row[32].getContents());
				gen.setNumeroFabbrica(row[37].getContents());
				gen.setPressMax(row[42].getContents());
				gen.setCodiceCombCv(getCodeValue(row[47].getContents()));
				if(row[52].getContents()!=null && !row[52].getContents().trim().isEmpty()){
					try{
						gen.setPotGlob(Double.parseDouble(row[52].getContents()));
					}catch(NumberFormatException e){
						log.error("Error parsing PotGlob for SchedaGeneratori #"+
								row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
					}
				}
				if(row[57].getContents()!=null && !row[57].getContents().trim().isEmpty()){
					try{
						gen.setPotGlobNom(Double.parseDouble(row[57].getContents()));
					}catch(NumberFormatException e){
						log.error("Error parsing PotGlobNom for SchedaGeneratori #"+
								row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
					}
				}

				genList.add(gen);

				if(row[28].getContents()!=null && !row[28].getContents().trim().isEmpty()){
					SchedaGeneratori gen2 = new SchedaGeneratori();
					gen2.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaGeneratori(gen2);
					gen2.setImpRisc(impRisc);
					gen2.setNumero(2);
					gen2.setType(getCodeValue(row[28].getContents()));
					gen2.setCostruttore(row[33].getContents());
					gen2.setNumeroFabbrica(row[38].getContents());
					gen2.setPressMax(row[43].getContents());
					gen2.setCodiceCombCv(getCodeValue(row[48].getContents()));
					if(row[53].getContents()!=null && !row[53].getContents().trim().isEmpty()){
						try{
							gen2.setPotGlob(Double.parseDouble(row[53].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlob for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					if(row[58].getContents()!=null && !row[58].getContents().trim().isEmpty()){
						try{
							gen2.setPotGlobNom(Double.parseDouble(row[58].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlobNom for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					genList.add(gen2);
				}


				if(row[29].getContents()!=null && !row[29].getContents().trim().isEmpty()){
					SchedaGeneratori gen3 = new SchedaGeneratori();
					gen3.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaGeneratori(gen3);
					gen3.setImpRisc(impRisc);
					gen3.setNumero(3);
					gen3.setType(getCodeValue(row[29].getContents()));
					gen3.setCostruttore(row[34].getContents());
					gen3.setNumeroFabbrica(row[39].getContents());
					gen3.setPressMax(row[44].getContents());
					gen3.setCodiceCombCv(getCodeValue(row[49].getContents()));
					if(row[54].getContents()!=null && !row[54].getContents().trim().isEmpty()){
						try{
							gen3.setPotGlob(Double.parseDouble(row[54].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlob for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					if(row[59].getContents()!=null && !row[59].getContents().trim().isEmpty()){
						try{
							gen3.setPotGlobNom(Double.parseDouble(row[59].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlobNom for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					genList.add(gen3);
				}

				if(row[30].getContents()!=null && !row[30].getContents().trim().isEmpty()){
					SchedaGeneratori gen4 = new SchedaGeneratori();
					gen4.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaGeneratori(gen);
					gen4.setImpRisc(impRisc);
					gen4.setNumero(4);
					gen4.setType(getCodeValue(row[30].getContents()));
					gen4.setCostruttore(row[35].getContents());
					gen4.setNumeroFabbrica(row[40].getContents());
					gen4.setPressMax(row[45].getContents());
					gen4.setCodiceCombCv(getCodeValue(row[50].getContents()));
					if(row[55].getContents()!=null && !row[55].getContents().trim().isEmpty()){
						try{
							gen4.setPotGlob(Double.parseDouble(row[55].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlob for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					if(row[60].getContents()!=null && !row[60].getContents().trim().isEmpty()){
						try{
							gen4.setPotGlobNom(Double.parseDouble(row[60].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlobNom for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					genList.add(gen4);
				}

				if(row[31].getContents()!=null && !row[31].getContents().trim().isEmpty()){
					SchedaGeneratori gen5 = new SchedaGeneratori();
					gen5.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaGeneratori(gen);
					gen5.setImpRisc(impRisc);
					gen5.setNumero(5);
					gen5.setType(getCodeValue(row[31].getContents()));
					gen5.setCostruttore(row[36].getContents());
					gen5.setNumeroFabbrica(row[41].getContents());
					gen5.setPressMax(row[46].getContents());
					gen5.setCodiceCombCv(getCodeValue(row[51].getContents()));
					if(row[56].getContents()!=null && !row[56].getContents().trim().isEmpty()){
						try{
							gen5.setPotGlob(Double.parseDouble(row[56].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlob for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					if(row[61].getContents()!=null && !row[61].getContents().trim().isEmpty()){
						try{
							gen5.setPotGlobNom(Double.parseDouble(row[61].getContents()));
						}catch(NumberFormatException e){
							log.error("Error parsing PotGlobNom for SchedaGeneratori #"+
									row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
						}
					}
					genList.add(gen5);
				}

			}

			impRisc.setSchedaGeneratori(genList);

			List<SchedaVasi> vasiList = null;
			if(row[63].getContents()!=null && !row[63].getContents().trim().isEmpty()){
				vasiList = new ArrayList<SchedaVasi>();

				SchedaVasi vas1 = new SchedaVasi();
				vas1.setCreatedBy(this.getClass().getSimpleName() + ulss);
				//impRisc.addSchedaVasi(vas1);
				vas1.setImpRisc(impRisc);
				vas1.setNumero(1);
				vas1.setCodiceVaso1(row[63].getContents());
				vas1.setCodiceVaso2(row[73].getContents());
				vas1.setCodiceVaso3(row[83].getContents());
				vas1.setClasse(row[93].getContents());
				vas1.setPress(row[103].getContents());
				vas1.setCapacita(row[113].getContents());

				vasiList.add(vas1);

				if(row[64].getContents()!=null && !row[64].getContents().trim().isEmpty()){
					SchedaVasi vas2 = new SchedaVasi();
					vas2.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas2);
					vas2.setImpRisc(impRisc);
					vas2.setNumero(2);
					vas2.setCodiceVaso1(row[64].getContents());
					vas2.setCodiceVaso2(row[74].getContents());
					vas2.setCodiceVaso3(row[84].getContents());
					vas2.setClasse(row[94].getContents());
					vas2.setPress(row[104].getContents());
					vas2.setCapacita(row[114].getContents());

					vasiList.add(vas2);

				}
				if(row[65].getContents()!=null && !row[65].getContents().trim().isEmpty()){
					SchedaVasi vas3 = new SchedaVasi();
					vas3.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas3);
					vas3.setImpRisc(impRisc);
					vas3.setNumero(3);
					vas3.setCodiceVaso1(row[65].getContents());
					vas3.setCodiceVaso2(row[75].getContents());
					vas3.setCodiceVaso3(row[85].getContents());
					vas3.setClasse(row[95].getContents());
					vas3.setPress(row[105].getContents());
					vas3.setCapacita(row[115].getContents());

					vasiList.add(vas3);

				}
				if(row[66].getContents()!=null && !row[66].getContents().trim().isEmpty()){
					SchedaVasi vas4 = new SchedaVasi();
					vas4.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas4);
					vas4.setImpRisc(impRisc);
					vas4.setNumero(4);
					vas4.setCodiceVaso1(row[66].getContents());
					vas4.setCodiceVaso2(row[76].getContents());
					vas4.setCodiceVaso3(row[86].getContents());
					vas4.setClasse(row[96].getContents());
					vas4.setPress(row[106].getContents());
					vas4.setCapacita(row[116].getContents());

					vasiList.add(vas4);

				}
				if(row[67].getContents()!=null && !row[67].getContents().trim().isEmpty()){
					SchedaVasi vas5 = new SchedaVasi();
					vas5.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas5);
					vas5.setImpRisc(impRisc);
					vas5.setNumero(5);
					vas5.setCodiceVaso1(row[67].getContents());
					vas5.setCodiceVaso2(row[77].getContents());
					vas5.setCodiceVaso3(row[87].getContents());
					vas5.setClasse(row[97].getContents());
					vas5.setPress(row[107].getContents());
					vas5.setCapacita(row[117].getContents());

					vasiList.add(vas5);

				}
				if(row[68].getContents()!=null && !row[68].getContents().trim().isEmpty()){
					SchedaVasi vas6 = new SchedaVasi();
					vas6.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas6);
					vas6.setImpRisc(impRisc);
					vas6.setNumero(6);
					vas6.setCodiceVaso1(row[68].getContents());
					vas6.setCodiceVaso2(row[78].getContents());
					vas6.setCodiceVaso3(row[88].getContents());
					vas6.setClasse(row[98].getContents());
					vas6.setPress(row[108].getContents());
					vas6.setCapacita(row[118].getContents());

					vasiList.add(vas6);

				}
				if(row[69].getContents()!=null && !row[69].getContents().trim().isEmpty()){
					SchedaVasi vas7 = new SchedaVasi();
					vas7.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas7);
					vas7.setImpRisc(impRisc);
					vas7.setNumero(7);
					vas7.setCodiceVaso1(row[69].getContents());
					vas7.setCodiceVaso2(row[79].getContents());
					vas7.setCodiceVaso3(row[89].getContents());
					vas7.setClasse(row[99].getContents());
					vas7.setPress(row[109].getContents());
					vas7.setCapacita(row[119].getContents());

					vasiList.add(vas7);

				}
				if(row[70].getContents()!=null && !row[70].getContents().trim().isEmpty()){
					SchedaVasi vas8 = new SchedaVasi();
					vas8.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas8);
					vas8.setImpRisc(impRisc);
					vas8.setNumero(8);
					vas8.setCodiceVaso1(row[70].getContents());
					vas8.setCodiceVaso2(row[80].getContents());
					vas8.setCodiceVaso3(row[90].getContents());
					vas8.setClasse(row[100].getContents());
					vas8.setPress(row[110].getContents());
					vas8.setCapacita(row[120].getContents());

					vasiList.add(vas8);

				}
				if(row[71].getContents()!=null && !row[71].getContents().trim().isEmpty()){
					SchedaVasi vas9 = new SchedaVasi();
					vas9.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas9);
					vas9.setImpRisc(impRisc);
					vas9.setNumero(9);
					vas9.setCodiceVaso1(row[71].getContents());
					vas9.setCodiceVaso2(row[81].getContents());
					vas9.setCodiceVaso3(row[91].getContents());
					vas9.setClasse(row[101].getContents());
					vas9.setPress(row[111].getContents());
					vas9.setCapacita(row[121].getContents());

					vasiList.add(vas9);

				}
				if(row[72].getContents()!=null && !row[72].getContents().trim().isEmpty()){
					SchedaVasi vas10 = new SchedaVasi();
					vas10.setCreatedBy(this.getClass().getSimpleName() + ulss);
					//impRisc.addSchedaVasi(vas10);
					vas10.setImpRisc(impRisc);
					vas10.setNumero(10);
					vas10.setCodiceVaso1(row[72].getContents());
					vas10.setCodiceVaso2(row[82].getContents());
					vas10.setCodiceVaso3(row[92].getContents());
					vas10.setClasse(row[102].getContents());
					vas10.setPress(row[112].getContents());
					vas10.setCapacita(row[122].getContents());

					vasiList.add(vas10);

				}

			}

			impRisc.setSchedaVasi(vasiList);




			saveOnTarget(impRisc);

			saveMapping(row[1].getContents(), row[2].getContents(), row[3].getContents(), row[123].getContents(), impRisc);
		} else {
			log.info("Already imported ImpRisc with id: "
					+ row[1].getContents()+"-"+row[2].getContents()+"-"+row[3].getContents()+"-"+row[123].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public ImpRisc getMapped(String sigla, String matr, String anno, String dep) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		//		String hqlMapping = "SELECT m FROM MapImpRisc m WHERE m.idexcel = :id";
		//		Query qMapping = targetEm.createQuery(hqlMapping);
		//		qMapping.setParameter("id", idexcel);

		String hqlMapping = "SELECT m FROM MapImpRisc m WHERE m.sigla = :sigla " +
				"AND m.matricola = :matr AND m.anno = :annno AND m.dipartimento = :dep";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("sigla", sigla);
		qMapping.setParameter("matr", matr);
		qMapping.setParameter("annno", anno);
		qMapping.setParameter("dep", dep);

		List<MapImpRisc> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapImpRisc map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM ImpRisc e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<ImpRisc> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sigla, String matr, String anno, String dep, ImpRisc target) {
		MapImpRisc map = new MapImpRisc();
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
				+ sigla+"-"+matr+"-"+anno+"-"+dep + ". " + "ImpRisc id: "
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

	public void updateVerifIndicator(Long impRiscId){
		ImpRisc impRisc = targetEm.find(ImpRisc.class, impRiscId);

		if (impRisc!=null && impRisc.getVerificaImp()==null && impRisc.getVerificaImp().size()<=0) {
			impRisc.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpRisc with internalId "+impRisc.getInternalId());

			saveOnTarget(impRisc);
			return;
		}

		int actualVerifNum = 0;
		for(VerificaImp ver : impRisc.getVerificaImp()){
			if(Boolean.TRUE.equals(ver.getPre())){
				continue;
			}
			actualVerifNum++;
		}

		if(actualVerifNum == 0){
			impRisc.setVerificheLong(0L);
			//log.info("Set verificheLong at null on ImpRisc with internalId "+impRisc.getInternalId());
		}else{
			impRisc.setVerificheLong(1L);
			//log.info("Set verificheLong at 1 on ImpRisc with internalId "+impRisc.getInternalId());
		}

		saveOnTarget(impRisc);
		return;

	}


}
