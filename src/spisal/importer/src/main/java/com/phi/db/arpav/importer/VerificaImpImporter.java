package com.phi.db.arpav.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;

import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;

import com.phi.db.arpav.mapping.MapVerificaImp;
import com.phi.entities.actions.IndirizzoSpedAction;
import com.phi.entities.baseEntity.Fattura;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;

public class VerificaImpImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(VerificaImpImporter.class.getName());

	private static VerificaImpImporter instance = null;

	public static VerificaImpImporter getInstance() {
		if (instance == null) {
			instance = new VerificaImpImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");

		if(row[1].getContents()==null || row[1].getContents().trim().isEmpty()){
			log.error("Unable to get mapping for VerificaImp at row " + (row[1].getRow()+1));
			return;
		}
		String dateMapping = null;
		try{
			dateMapping = sdfMapping.format(sdf.parse(row[1].getContents()));
		}catch(ParseException e){
			log.error("Unable to get Date for VerificaImp at row " + (row[1].getRow()+1));
			return;
		}

		if(row[49].getContents()==null || row[49].getContents().trim().isEmpty()){
			log.error("Unable to get impType for VerificaImp at row " + (row[49].getRow()+1));
			return;
		}
		String impType = row[49].getContents();

		if(row[52].getContents()==null || row[52].getContents().trim().isEmpty()){
			log.error("Unable to get sigla for VerificaImp at row " + (row[52].getRow()+1));
			return;
		}
		String sigla = row[52].getContents();

		if(row[50].getContents()==null || row[50].getContents().trim().isEmpty()){
			log.error("Unable to get matricola for VerificaImp at row " + (row[50].getRow()+1));
			return;
		}
		String matricola = row[50].getContents();

		if(row[53].getContents()==null || row[53].getContents().trim().isEmpty()){
			log.error("Unable to get sigla for VerificaImp at row " + (row[53].getRow()+1));
			return;
		}
		String anno = row[53].getContents();

		String tipo = "x";
		if(row[51].getContents()!=null && !row[51].getContents().trim().isEmpty()){
			tipo = row[51].getContents();
		}

		String dep = row[60].getContents();

		String mappedId = dateMapping+"-"+impType+"-"+sigla+"-"+matricola+"-"+tipo+"-"+anno+"-"+dep;
		VerificaImp verificaimp = getMapped(mappedId);
		if (verificaimp == null) {
			verificaimp = new VerificaImp();
			verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
			verificaimp.setCreationDate(new Date());

			if(row[1].getContents()!=null && !row[1].getContents().trim().isEmpty()){
				verificaimp.setData(sdf.parse(row[1].getContents()));
			}
			verificaimp.setLuogo(row[2].getContents());
			if(row[3].getContents()!=null && !row[3].getContents().trim().isEmpty()) {
				try{
					verificaimp.setImporto(Double.valueOf(row[3].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing Importo for VerificaImp #"+
							mappedId);
				}
			}
			//			if(verificaimp.getImporto()!=null) {
			//				verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
			//			}else {
			//				verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.new"));
			//			}

			verificaimp.setCodiceConto(row[4].getContents());
			verificaimp.setHhServizio(row[5].getContents());
			verificaimp.setMmServizio(row[6].getContents());
			verificaimp.setPrima(controlBoolean(row[7].getContents()));
			//verificaimp.setSopralluogoBl(controlBoolean(row[8].getContents()));
			verificaimp.setTecnicoOut(controlBoolean(row[8].getContents()));
			verificaimp.setIdraulica(controlBoolean(row[9].getContents()));
			verificaimp.setInterna(controlBoolean(row[10].getContents()));
			verificaimp.setEsercizio(controlBoolean(row[11].getContents()));
			if(row[12].getContents()!=null && !row[12].getContents().trim().isEmpty()){
				verificaimp.setNextVerifDate1(sdf.parse(row[12].getContents()));
			}
			if(row[13].getContents()!=null && !row[13].getContents().trim().isEmpty()){
				verificaimp.setNextVerifDate2(sdf.parse(row[13].getContents()));
			}
			if(row[14].getContents()!=null && !row[14].getContents().trim().isEmpty()){
				verificaimp.setNextVerifDate3(sdf.parse(row[14].getContents()));
			}
			verificaimp.setPrescrizioneBl(controlBoolean(row[15].getContents()));
			verificaimp.setStatoImp(getCodeValue(row[16].getContents()));
			if(row[17].getContents()!=null && !row[17].getContents().trim().isEmpty()){
				verificaimp.setDataVar(sdf.parse(row[17].getContents()));
			}
			verificaimp.setGru(controlBoolean(row[18].getContents()));
			verificaimp.setIdrovore(controlBoolean(row[19].getContents()));
			verificaimp.setModAb(row[20].getContents());
			verificaimp.setModB(row[21].getContents());
			verificaimp.setModC(controlBoolean(row[22].getContents()));
			verificaimp.setNote(row[23].getContents());
			if(row[24].getContents()!=null && !row[24].getContents().trim().isEmpty()){
				verificaimp.setDataUltimaModifica(sdf.parse(row[24].getContents()));
			}
			if(row[25].getContents()!=null && !row[25].getContents().trim().isEmpty()){
				verificaimp.setUtenteUltimaModifica(getEmployee(row[25].getContents()));
			}
			verificaimp.setTipoInOut(getCodeValue(row[26].getContents()));

			verificaimp.setImpType(getCodeValue(row[27].getContents()));
			if(!getCodeValue(row[27].getContents()).equals(getCodeValue("arpav.imp.imptype.0"+impType))) {
				log.error("Incoherent impType for VerificaImp #"+
						mappedId);
			}

			verificaimp.setServiceDeliveryLocation(findArpav(row[28].getContents()));



			//Prove impianti a pressione
			/*
			verificaimp.setInterna(controlBoolean(row[36].getContents()));			
			verificaimp.setIdraulica(controlBoolean(row[37].getContents()));
			verificaimp.setEsercizio(controlBoolean(row[38].getContents()));
			 */

			//			verificaimp.setTipo(getCodeValue(row[30].getContents()));
			//			verificaimp.setTipoStr(getCodeValue(row[31].getContents()));
			//			verificaimp.setSopralluogo(getCodeValue(row[30].getContents()));


			if(row[38].getContents()!=null && !row[38].getContents().trim().isEmpty()){
				verificaimp.setOperatore(getMappedOperatore(row[38].getContents()));
				if(verificaimp.getOperatore()==null) {
					log.error("Unable to find Operatore #"+ row[38].getContents());

				}
			}
			verificaimp.setEsito(getCodeValue(row[39].getContents()));
			verificaimp.setEsito02(getCodeValue(row[41].getContents()));
			verificaimp.setPrescrizione(getCodeValue(row[42].getContents()));
			verificaimp.setRegimeFiscale(getCodeValue(row[43].getContents()));
			verificaimp.setAliquota(getCodeValue(row[44].getContents()));
			verificaimp.setFluido(getCodeValue(row[45].getContents()));
			verificaimp.setRischio(getCodeValue(row[46].getContents()));

			Sedi sedeAddebito = null;
			if(row[47].getContents()!=null && !row[47].getContents().trim().isEmpty()){
				sedeAddebito = getSedi(row[47].getContents());
				verificaimp.setSedi(sedeAddebito);
			}

			SediInstallazione sedeInstallazione = null;
			if(row[48].getContents()!=null && !row[48].getContents().trim().isEmpty()){
				sedeInstallazione = getSediInstallazione(row[48].getContents());
				verificaimp.setSediInstallazione(sedeInstallazione);
			}
			if(verificaimp.getSedi()==null && verificaimp.getSediInstallazione()!=null) {
				sedeAddebito = verificaimp.getSediInstallazione().getSede();
				verificaimp.setSedi(sedeAddebito);
			}

			//Aggancio impianto
			if("phidic.arpav.imp.imptype.01".equals(verificaimp.getImpType().getOid())) {
				//copy impPress
				verificaimp.setImpPress(getImpPress(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
				if(verificaimp.getImpPress()==null){
					log.error("Unable to find impPress #"+
							row[52].getContents()+"-"+row[50].getContents()+"-"+row[53].getContents()+
							"-"+dep);
				}else {
					verificaimp.setImpPressCpy(getImpPressCpy(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
					//copy impPress of verificaImp into ImpSearchCollector
					verificaimp.setImpSearchCollector(copyIntoCollector(verificaimp.getImpPress()));

					//					if(sedeInstallazione==null && verificaimp.getImpPress().getSedeInstallazione()!=null) {
					//						sedeInstallazione = copySedeInstallazione(verificaimp.getImpPress().getSedeInstallazione());
					//						if(sedeInstallazione!=null) {
					//							saveOnTarget(sedeInstallazione);
					//							verificaimp.setSediInstallazione(sedeInstallazione);
					//						}
					//					}
					//					if(sedeAddebito==null && verificaimp.getImpPress().getSedi()!=null) {
					//						sedeAddebito = copySede(verificaimp.getImpPress().getSedi());
					//						if(sedeAddebito!=null) {
					//							saveOnTarget(sedeAddebito);
					//							verificaimp.setSedi(sedeAddebito);
					//						}
					//					}

					if(sedeInstallazione==null && verificaimp.getImpPress().getSedeInstallazione()!=null) {
						verificaimp.setSediInstallazione(verificaimp.getImpPress().getSedeInstallazione());
					}
					if(sedeAddebito==null && verificaimp.getImpPress().getSedi()!=null) {
						verificaimp.setSedi(verificaimp.getImpPress().getSedi());
					}

					if(verificaimp.getNextVerifDate1()!=null &&
							(verificaimp.getImpPress().getNextVerifDate1()==null ||
									verificaimp.getNextVerifDate1().after(verificaimp.getImpPress().getNextVerifDate1()))) {
						verificaimp.getImpPress().setNextVerifDate1(verificaimp.getNextVerifDate1());
					}

					if(verificaimp.getNextVerifDate2()!=null &&
							(verificaimp.getImpPress().getNextVerifDate2()==null ||
									verificaimp.getNextVerifDate2().after(verificaimp.getImpPress().getNextVerifDate2()))) {
						verificaimp.getImpPress().setNextVerifDate2(verificaimp.getNextVerifDate2());
					}

					if(verificaimp.getNextVerifDate3()!=null &&
							(verificaimp.getImpPress().getNextVerifDate3()==null ||
									verificaimp.getNextVerifDate3().after(verificaimp.getImpPress().getNextVerifDate3()))) {
						verificaimp.getImpPress().setNextVerifDate3(verificaimp.getNextVerifDate3());
					}
				}
			}else if("phidic.arpav.imp.imptype.02".equals(verificaimp.getImpType().getOid())) {
				//copy impRisc
				verificaimp.setImpRisc(getImpRisc(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
				if(verificaimp.getImpRisc()==null) {
					log.error("Unable to find impRisc #"+
							row[52].getContents()+"-"+row[50].getContents()+"-"+row[53].getContents()+
							"-"+dep);
				}else {
					verificaimp.setImpRiscCpy(getImpRiscCpy(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
					//copy impRisc of verificaImp into ImpSearchCollector
					verificaimp.setImpSearchCollector(copyIntoCollector(verificaimp.getImpRisc()));

					//					if(sedeInstallazione==null && verificaimp.getImpRisc().getSedeInstallazione()!=null) {
					//						sedeInstallazione = copySedeInstallazione(verificaimp.getImpRisc().getSedeInstallazione());
					//						if(sedeInstallazione!=null) {
					//							saveOnTarget(sedeInstallazione);
					//							verificaimp.setSediInstallazione(sedeInstallazione);
					//						}
					//					}
					//					if(sedeAddebito==null && verificaimp.getImpRisc().getSedi()!=null) {
					//						sedeAddebito = copySede(verificaimp.getImpRisc().getSedi());
					//						if(sedeAddebito!=null) {
					//							saveOnTarget(sedeAddebito);
					//							verificaimp.setSedi(sedeAddebito);
					//						}
					//					}

					if(sedeInstallazione==null && verificaimp.getImpRisc().getSedeInstallazione()!=null) {
						verificaimp.setSediInstallazione(verificaimp.getImpRisc().getSedeInstallazione());
					}
					if(sedeAddebito==null && verificaimp.getImpRisc().getSedi()!=null) {
						verificaimp.setSedi(verificaimp.getImpRisc().getSedi());
					}

					if(verificaimp.getNextVerifDate1()!=null &&
							(verificaimp.getImpRisc().getNextVerifDate1()==null ||
									verificaimp.getNextVerifDate1().after(verificaimp.getImpRisc().getNextVerifDate1()))) {
						verificaimp.getImpRisc().setNextVerifDate1(verificaimp.getNextVerifDate1());
					}
				}
			}else if("phidic.arpav.imp.imptype.03".equals(verificaimp.getImpType().getOid())) {
				//copy impMonta
				verificaimp.setImpMonta(getImpMonta(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
				if(verificaimp.getImpMonta()==null) {
					log.error("Unable to find impMonta #"+
							row[52].getContents()+"-"+row[50].getContents()+"-"+row[53].getContents()+
							"-"+dep);
				}else {
					verificaimp.setImpMontaCpy(getImpMontaCpy(row[52].getContents(),row[50].getContents(),row[53].getContents(),dep));
					//copy impMonta of verificaImp into ImpSearchCollector
					verificaimp.setImpSearchCollector(copyIntoCollector(verificaimp.getImpMonta()));

					//					if(sedeInstallazione==null && verificaimp.getImpMonta().getSedeInstallazione()!=null) {
					//						sedeInstallazione = copySedeInstallazione(verificaimp.getImpMonta().getSedeInstallazione());
					//						if(sedeInstallazione!=null) {
					//							saveOnTarget(sedeInstallazione);
					//							verificaimp.setSediInstallazione(sedeInstallazione);
					//						}
					//					}
					//					if(sedeAddebito==null && verificaimp.getImpMonta().getSedi()!=null) {
					//						sedeAddebito = copySede(verificaimp.getImpMonta().getSedi());
					//						if(sedeAddebito!=null) {
					//							saveOnTarget(sedeAddebito);
					//							verificaimp.setSedi(sedeAddebito);
					//						}
					//					}

					if(sedeInstallazione==null && verificaimp.getImpMonta().getSedeInstallazione()!=null) {
						verificaimp.setSediInstallazione(verificaimp.getImpMonta().getSedeInstallazione());
					}
					if(sedeAddebito==null && verificaimp.getImpMonta().getSedi()!=null) {
						verificaimp.setSedi(verificaimp.getImpMonta().getSedi());
					}

					if(verificaimp.getNextVerifDate1()!=null &&
							(verificaimp.getImpMonta().getNextVerifDate1()==null ||
									verificaimp.getNextVerifDate1().after(verificaimp.getImpMonta().getNextVerifDate1()))) {
						verificaimp.getImpMonta().setNextVerifDate1(verificaimp.getNextVerifDate1());
					}
				}
			}else if("phidic.arpav.imp.imptype.04".equals(verificaimp.getImpType().getOid())){
				//copy impSoll
				verificaimp.setImpSoll(getImpSoll(row[52].getContents(),row[50].getContents(),row[53].getContents(),row[51].getContents(),dep));
				if(verificaimp.getImpSoll()==null) {
					log.error("Unable to find impSoll #"+
							row[52].getContents()+"-"+row[50].getContents()+"-"+row[53].getContents()+"-"+row[51].getContents()+
							"-"+dep);
				}else {
					verificaimp.setImpSollCpy(getImpSollCpy(row[52].getContents(),row[50].getContents(),row[53].getContents(),row[51].getContents(),dep));
					//copy impSoll of verificaImp into ImpSearchCollector
					verificaimp.setImpSearchCollector(copyIntoCollector(verificaimp.getImpSoll()));

					//					if(sedeInstallazione==null && verificaimp.getImpSoll().getSedeInstallazione()!=null) {
					//						sedeInstallazione = copySedeInstallazione(verificaimp.getImpSoll().getSedeInstallazione());
					//						if(sedeInstallazione!=null) {
					//							saveOnTarget(sedeInstallazione);
					//							verificaimp.setSediInstallazione(sedeInstallazione);
					//						}
					//					}
					//					if(sedeAddebito==null && verificaimp.getImpSoll().getSedi()!=null) {
					//						sedeAddebito = copySede(verificaimp.getImpSoll().getSedi());
					//						if(sedeAddebito!=null) {
					//							saveOnTarget(sedeAddebito);
					//							verificaimp.setSedi(sedeAddebito);
					//						}
					//					}

					if(sedeInstallazione==null && verificaimp.getImpSoll().getSedeInstallazione()!=null) {
						verificaimp.setSediInstallazione(verificaimp.getImpSoll().getSedeInstallazione());
					}
					if(sedeAddebito==null && verificaimp.getImpSoll().getSedi()!=null) {
						verificaimp.setSedi(verificaimp.getImpSoll().getSedi());
					}

					if(verificaimp.getNextVerifDate1()!=null &&
							(verificaimp.getImpSoll().getNextVerifDate1()==null ||
									verificaimp.getNextVerifDate1().after(verificaimp.getImpSoll().getNextVerifDate1()))) {
						verificaimp.getImpSoll().setNextVerifDate1(verificaimp.getNextVerifDate1());
					}
				}
			}else if("phidic.arpav.imp.imptype.05".equals(verificaimp.getImpType().getOid())) {
				//copy impTerra
				HashMap<String, String> mapSubType = new HashMap<String, String>();
				mapSubType.put("A", "1");
				mapSubType.put("B", "2");
				mapSubType.put("C", "3");

				String numSubType = mapSubType.get(tipo);

				verificaimp.setImpTerra(getImpTerra(row[52].getContents(),row[50].getContents(),row[53].getContents(),numSubType,dep));
				if(verificaimp.getImpTerra()==null) {
					log.error("Unable to find impTerra #"+
							row[52].getContents()+"-"+row[50].getContents()+"-"+row[53].getContents()+"-"+row[51].getContents()+
							"-"+dep);
				}else {
					verificaimp.setImpTerraCpy(getImpTerraCpy(row[52].getContents(),row[50].getContents(),row[53].getContents(),numSubType,dep));
					//copy impTerra of verificaImp into ImpSearchCollector
					verificaimp.setImpSearchCollector(copyIntoCollector(verificaimp.getImpTerra()));

					//					if(sedeInstallazione==null && verificaimp.getImpTerra().getSedeInstallazione()!=null) {
					//						sedeInstallazione = copySedeInstallazione(verificaimp.getImpTerra().getSedeInstallazione());
					//						if(sedeInstallazione!=null) {
					//							saveOnTarget(sedeInstallazione);
					//							verificaimp.setSediInstallazione(sedeInstallazione);
					//						}
					//					}
					//					if(sedeAddebito==null && verificaimp.getImpTerra().getSedi()!=null) {
					//						sedeAddebito = copySede(verificaimp.getImpTerra().getSedi());
					//						if(sedeAddebito!=null) {
					//							saveOnTarget(sedeAddebito);
					//							verificaimp.setSedi(sedeAddebito);
					//						}
					//					}

					if(sedeInstallazione==null && verificaimp.getImpTerra().getSedeInstallazione()!=null) {
						verificaimp.setSediInstallazione(verificaimp.getImpTerra().getSedeInstallazione());
					}
					if(sedeAddebito==null && verificaimp.getImpTerra().getSedi()!=null) {
						verificaimp.setSedi(verificaimp.getImpTerra().getSedi());
					}

					if(verificaimp.getNextVerifDate1()!=null &&
							(verificaimp.getImpTerra().getNextVerifDate1()==null ||
									verificaimp.getNextVerifDate1().after(verificaimp.getImpTerra().getNextVerifDate1()))) {
						verificaimp.getImpTerra().setNextVerifDate1(verificaimp.getNextVerifDate1());
					}
				}
			}else {
				log.error("Unable to find ImpType at row " + (row[1].getRow()+1));
			}

			//Tipo verifica
			if(row[29].getContents()!=null && !row[29].getContents().trim().isEmpty()) {
				verificaimp.setTipo(getCodeValue(row[29].getContents()));
			}

			if("phidic.arpav.ver.tipover.02".equals(verificaimp.getTipo().getOid()) ||
					"phidic.arpav.ver.tipover.05".equals(verificaimp.getTipo().getOid()) ||
					"phidic.arpav.ver.tipover.06".equals(verificaimp.getTipo().getOid())) {
				if(row[30].getContents()!=null && !row[30].getContents().trim().isEmpty()) {
					verificaimp.setTipoStr(getCodeValue(row[30].getContents()));
				}
			}

			if(verificaimp.getTipo()==null) {
				if(row[32].getContents()!=null && !row[32].getContents().trim().isEmpty() && controlBoolean(row[32].getContents())) {
					verificaimp.setTipo(getCodeValue("arpav.ver.tipover.01"));
				}else if(row[33].getContents()!=null && !row[33].getContents().trim().isEmpty()) {
					verificaimp.setTipo(getCodeValue("arpav.ver.tipover.06"));

					String tipoStraordVerif = row[33].getContents();
					if("phidic.arpav.imp.imptype.05".equals(verificaimp.getImpType().getOid()) ||
							("phidic.arpav.imp.imptype.01".equals(verificaimp.getImpType().getOid()) &&
									verificaimp.getImpPressCpy()!=null &&
									verificaimp.getImpPressCpy().getTipoApparecchio()!=null &&
									"phidic.arpav.imp.pressione.tipo.08".equals(verificaimp.getImpPressCpy().getTipoApparecchio().getOid()))) {
						if(tipoStraordVerif.equals("A")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.01"));
						}else if(tipoStraordVerif.equals("B")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.02"));
						}else if(tipoStraordVerif.equals("C")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.03"));
						}else if(tipoStraordVerif.equals("D")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.04"));
						}else if(tipoStraordVerif.equals("E")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.05"));
						}else if(tipoStraordVerif.equals("F")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.06"));
						}else if(tipoStraordVerif.equals("G")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.07"));
						}else if(tipoStraordVerif.equals("H")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.08"));
						}else if(tipoStraordVerif.equals("I")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.09"));
						}else if(tipoStraordVerif.equals("L")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.10"));
						}else if(tipoStraordVerif.equals("S")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.11"));
						}else if(tipoStraordVerif.equals("T")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.12"));
						}else if(tipoStraordVerif.equals("V")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.13"));
						}
					}else if("phidic.arpav.imp.imptype.02".equals(verificaimp.getImpType().getOid()) &&
							!"phidic.arpav.imp.riscaldamento.desc2.04".equals(verificaimp.getImpRiscCpy().getDescrizImpianto().getOid())) {
						if(tipoStraordVerif.equals("C")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.03"));
						}else if(tipoStraordVerif.equals("D")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.02"));
						}else if(tipoStraordVerif.equals("V")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.01"));
						}
					}else if("phidic.arpav.imp.imptype.03".equals(verificaimp.getImpType().getOid())) {
						if(tipoStraordVerif.equals("A")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.03"));
						}else if(tipoStraordVerif.equals("C")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.02"));
						}else if(tipoStraordVerif.equals("S")) {
							verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.01"));
						}
					}else {
						verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.01")); ////TODO:verificare su pspisal-lab
					}
				}else if(row[31].getContents()!=null && !row[31].getContents().trim().isEmpty()) {
					verificaimp.setTipo(getCodeValue("arpav.ver.tipover.05"));

					String tipoSopralluogo = row[31].getContents();
					if(tipoSopralluogo.equals("A")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.01"));
					}else if(tipoSopralluogo.equals("B")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.02"));
					}else if(tipoSopralluogo.equals("C")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.03"));
					}else if(tipoSopralluogo.equals("D")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.04"));
					}else if(tipoSopralluogo.equals("E")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.05"));
					}else if(tipoSopralluogo.equals("F")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.06"));
					}else if(tipoSopralluogo.equals("G")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.07"));
					}else if(tipoSopralluogo.equals("H")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.08"));
					}
				}else if(row[35].getContents()!=null && !row[35].getContents().trim().isEmpty()) {
					verificaimp.setTipo(getCodeValue("arpav.ver.tipover.05"));

					String tipoSopralluogo = row[35].getContents();
					if(tipoSopralluogo.equals("M")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.09"));
					}else if(tipoSopralluogo.equals("N")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.10"));
					}else if(tipoSopralluogo.equals("O")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.11"));
					}else if(tipoSopralluogo.equals("P")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.12"));
					}else if(tipoSopralluogo.equals("Q")) {
						verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.13"));
					}

				}
			}

			//			verificaimp.setStatoImp(getCodeValue(row[45].getContents()));
			//			verificaimp.setTipoStr(getCodeValue(row[46].getContents()));

			//DATI FATTURA
			if(row[64].getContents()!=null && !row[64].getContents().trim().isEmpty()) {
				try{
					verificaimp.setNumeroDoc(Integer.parseInt(row[64].getContents()));
				}catch(NumberFormatException e){
					log.error("Error parsing NumeroDoc for VerificaImp #"+
							mappedId);
				}
			}
			if(row[65].getContents()!=null && !row[65].getContents().trim().isEmpty()) {
				verificaimp.setCausale(getCodeValueCode("phidic.arpav.ver.causale",row[65].getContents()));
			}
			if(row[66].getContents()!=null && !row[66].getContents().trim().isEmpty()) {
				verificaimp.setEsente(getCodeValue(row[66].getContents()));
			}

			/*
			Date notYetFattura = sdf.parse("01/01/2022");
			if(verificaimp.getImporto()!=null || verificaimp.getData().before(notYetFattura)) {
				verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.completed"));
			}else {
				verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
			}*/

			verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.completed"));
			if(row[60].getContents()!=null && !row[60].getContents().trim().isEmpty() &&
					row[61].getContents()!=null && !row[61].getContents().trim().isEmpty() &&
					row[63].getContents()!=null && !row[63].getContents().trim().isEmpty()) {

				FatturaImporter fImp = FatturaImporter.getInstance();
				Fattura fattura = fImp.getMapped(row[61].getContents()+"-"+row[63].getContents()+"-"+row[60].getContents());
				if(fattura==null){
					fattura = fImp.importRow(row);
				}
				if(fattura!=null) {
					List<VerificaImp> verificheFatturate = fattura.getVerificaImp();
					if(verificheFatturate==null){
						verificheFatturate = new ArrayList<VerificaImp>();
					}
					verificaimp.setFattura(fattura);
					verificheFatturate.add(verificaimp);

					fattura.setVerificaImp(verificheFatturate);
				}

			}
			saveOnTarget(verificaimp);
			saveMapping(mappedId, verificaimp);

		} else {
			log.info("Already imported VerificaImp with mapped id: "
					+ mappedId);

		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 *
	 * @param idexcel
	 * @return
	 */
	private VerificaImp getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapVerificaImp m WHERE m.idexcel = :id";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idexcel);
		List<MapVerificaImp> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapVerificaImp map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM VerificaImp e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<VerificaImp> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sourceId, VerificaImp target) {
		MapVerificaImp map = new MapVerificaImp();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		/*thislog.info("New imported object. Source id: "
				+ sourceId + ". " + "VerificaImp id: "

				+ target.getInternalId() + " " + "on date "
				+ target.getCreationDate());*/
	}



	private Sedi getSedi(String id) {
		SediAddebitoImporter sediAddImp = SediAddebitoImporter.getInstance();
		Sedi mapped = sediAddImp.getMapped(id);
		if(mapped == null) {
			thislog.info("Unable to find mapped Sedi with id: "+id);
		}
		return mapped;
	}

	private SediInstallazione getSediInstallazione(String id) {
		SediInstallazioneImporter sediInstImp = SediInstallazioneImporter.getInstance();
		SediInstallazione mapped = sediInstImp.getMappedLike(id);
		if(mapped == null) {
			thislog.info("Unable to find mapped SediInstallazione with id: "+id);
		}
		return mapped;
	}

	private Boolean controlBoolean(String data) {
		if (data != null) {
			if (("1").equals(data) || ("S").equals(data))
				return true;
			else if(("0").equals(data))
				return false;
		}

		return null;
	}


	private Employee getEmployee(String id) {
		EmployeeImporter eImp = EmployeeImporter.getInstance();
		return eImp.getMapped(id);
	}

	private Operatore getOperatore(String id) {
		OperatoreImporter opeImp = OperatoreImporter.getInstance();
		return opeImp.getTarget(id);
	}

	private Operatore getMappedOperatore(String idexcel) {
		OperatoreImporter opeImp = OperatoreImporter.getInstance();
		return opeImp.getMapped(idexcel);
	}
	private ImpTerra getImpTerra(String sigla, String matr, String anno, String subtype, String dep){
		ImpTerraImporter opeImp = ImpTerraImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, subtype, dep);
	}
	private ImpRisc getImpRisc(String sigla, String matr, String anno, String dep){
		ImpRiscImporter opeImp = ImpRiscImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpMonta getImpMonta(String sigla, String matr, String anno, String dep){
		ImpMontaImporter opeImp = ImpMontaImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpPress getImpPress(String sigla, String matr, String anno, String dep){
		ImpPressImporter opeImp = ImpPressImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, dep);
	}
	private ImpSoll getImpSoll(String sigla, String matr, String anno, String subtype, String dep){
		ImpSollImporter opeImp = ImpSollImporter.getInstance();
		return opeImp.getMapped(sigla, matr, anno, subtype, dep);
	}


	private ImpMonta getImpMontaCpy(String sigla, String matr, String anno, String dep) {
		try{
			ImpMontaImporter impMontaImp = ImpMontaImporter.getInstance();
			ImpMonta toCopy = impMontaImp.getMapped(sigla, matr, anno, dep);
			ImpMonta copy = new ImpMonta();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if (toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());

			saveOnTarget(si);
			copy.setSedeInstallazione(si);

			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();

			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());

			ca.create(sa);
			copy.setSedeAddebito(sa);
			 */

			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();

			if(toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());

			saveOnTarget(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();

			if ( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());

			saveOnTarget(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAmministratore(toCopy.getAmministratore());
			copy.setAnno(toCopy.getAnno());
			//copy.setAnnoCostruz(toCopy.getAnnoCostruz());
			copy.setCategoria(toCopy.getCategoria());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setCorsa(toCopy.getCorsa());
			copy.setCostruttore(toCopy.getCostruttore());
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setDestinazione(toCopy.getDestinazione());
			copy.setDistanza(toCopy.getDistanza());
			copy.setFermate(toCopy.getFermate());
			copy.setLicenza(toCopy.getLicenza());
			copy.setManovra(toCopy.getManovra());
			copy.setManutentore(toCopy.getManutentore());
			copy.setMatricola(toCopy.getMatricola());
			copy.setMatrcomune(toCopy.getMatrcomune());
			copy.setMotore(toCopy.getMotore());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setPortata(toCopy.getPortata());
			copy.setSigla(toCopy.getSigla());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setTrazione(toCopy.getTrazione());
			copy.setVelocita(toCopy.getVelocita());

			// I0064822 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276
			copy.setPorte(toCopy.getPorte());
			copy.setNote(toCopy.getNote());

			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}


	private ImpPress getImpPressCpy(String sigla, String matr, String anno, String dep) {
		try{
			ImpPressImporter impPressImp = ImpPressImporter.getInstance();
			ImpPress toCopy = impPressImp.getMapped(sigla, matr, anno, dep);

			ImpPress copy = new ImpPress();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if (toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());

			saveOnTarget(si);
			copy.setSedeInstallazione(si);

			Sedi se = new Sedi();

			if(toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());

			saveOnTarget(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();

			if (toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());

			saveOnTarget(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAnno(toCopy.getAnno());
			copy.setBombole(toCopy.getBombole());
			copy.setCapacita(toCopy.getCapacita());
			//copy.setCaratteristiche(toCopy.getCaratteristiche());
			copy.setCategoriaRischio(toCopy.getCategoriaRischio());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setComodante(toCopy.getComodante());
			copy.setCompetenza(toCopy.getCompetenza());
			copy.setCostruttore(toCopy.getCostruttore());

			copy.setDataCostruzione(toCopy.getDataCostruzione());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setFluido(toCopy.getFluido());
			copy.setMatricola(toCopy.getMatricola());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setPressBar1(toCopy.getPressBar1());
			copy.setPressBar2(toCopy.getPressBar2());
			copy.setProducibilita(toCopy.getProducibilita());

			copy.setSigla(toCopy.getSigla());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());
			copy.setSuperficie(toCopy.getSuperficie());

			copy.setTempS1(toCopy.getTempS1());
			copy.setTempS2(toCopy.getTempS2());
			copy.setTempV1(toCopy.getTempV1());
			copy.setTempV2(toCopy.getTempV2());
			copy.setTipoApparecchio(toCopy.getTipoApparecchio());

			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());

			// I0064820 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276
			/*List<CodeValuePhi> caratteristicheToCopy = toCopy.getCaratteristiche();
		if(caratteristicheToCopy != null){
			List<CodeValuePhi> caratteristicheCopy = new ArrayList<CodeValuePhi>();
			for(CodeValuePhi cv : caratteristicheToCopy){
				caratteristicheCopy.add(cv);
			}
			copy.setCaratteristiche(caratteristicheCopy);

		}*/
			copy.setCaratteristicheSpec(toCopy.getCaratteristicheSpec());

			copy.setSuperficie(toCopy.getSuperficie());
			copy.setTubazioni(toCopy.getTubazioni());
			copy.setNote(toCopy.getNote());

			//this.create();

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}

	private ImpRisc getImpRiscCpy(String sigla, String matr, String anno, String dep) {
		try{
			ImpRiscImporter impRiscImp = ImpRiscImporter.getInstance();
			ImpRisc toCopy = impRiscImp.getMapped(sigla, matr, anno, dep);
			ImpRisc copy = new ImpRisc();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());

			saveOnTarget(si);
			copy.setSedeInstallazione(si);

			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();

			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());

			ca.create(sa);
			copy.setSedeAddebito(sa);
			 */

			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();

			if(toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());

			saveOnTarget(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();

			if( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());

			saveOnTarget(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAnno(toCopy.getAnno());
			copy.setCode(toCopy.getCode());

			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataAutocert1(toCopy.getDataAutocert1());
			copy.setDataAutocert2(toCopy.getDataAutocert2());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setDescrizImpianto(toCopy.getDescrizImpianto());
			copy.setDescrizLocali(toCopy.getDescrizLocali());
			copy.setDestImp(toCopy.getDestImp());

			copy.setGiorniAA(toCopy.getGiorniAA());
			copy.setImpianto(toCopy.getImpianto());
			copy.setManutentore(toCopy.getManutentore());
			copy.setMatricola(toCopy.getMatricola());
			copy.setNumGen(toCopy.getNumGen());
			copy.setOreGG(toCopy.getOreGG());
			copy.setPotGlob(toCopy.getPotGlob());
			copy.setPotGlobNom(toCopy.getPotGlobNom());

			copy.setSigla(toCopy.getSigla());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setVasiAperti(toCopy.getVasiAperti());
			copy.setVasiChiusi(toCopy.getVasiChiusi());

			// I0064821 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276
			copy.setSuperficieRisc(toCopy.getSuperficieRisc());
			copy.setNote(toCopy.getNote());

			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}

	private ImpSoll getImpSollCpy(String sigla, String matr, String anno, String subtype, String dep) {

		try{
			ImpSollImporter impSollImp = ImpSollImporter.getInstance();
			ImpSoll toCopy= impSollImp.getMapped(sigla, matr, anno, subtype, dep);
			ImpSoll copy = new ImpSoll();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());

			saveOnTarget(si);
			copy.setSedeInstallazione(si);

			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();

			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());

			ca.create(sa);
			copy.setSedeAddebito(sa);
			 */

			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();

			if( toCopy.getSedi()!=null)
				se = copySede(toCopy.getSedi());

			saveOnTarget(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			if ( toCopy.getIndirizzoSped()!=null)
				is = copyIndirizzoSped(toCopy.getIndirizzoSped());

			saveOnTarget(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAnno(toCopy.getAnno());
			//copy.setAnnoCostruz(toCopy.getAnnoCostruz());
			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setCompetenza(toCopy.getCompetenza());

			copy.setCostrRadioc(toCopy.getCostrRadioc());
			copy.setCostruttore(toCopy.getCostruttore());

			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setDataInstRadioc(toCopy.getDataInstRadioc());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setMarcCE(toCopy.getMarcCE());
			copy.setMatricola(toCopy.getMatricola());
			copy.setMezzo(toCopy.getMezzo());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setNumRadioc(toCopy.getNumRadioc());

			copy.setPortata(toCopy.getPortata());

			copy.setSigla(toCopy.getSigla());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setTarga(toCopy.getTarga());
			copy.setTelaio(toCopy.getTelaio());

			copy.setTipoFabb(toCopy.getTipoFabb());
			copy.setVelocitaMax(toCopy.getVelocitaMax());

			// I0064823 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276 
			copy.setNote(toCopy.getNote());

			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}

	private ImpTerra getImpTerraCpy(String sigla, String matr, String anno, String subtype, String dep) {

		try{
			ImpTerraImporter impTerraImp = ImpTerraImporter.getInstance();
			ImpTerra toCopy= impTerraImp.getMapped(sigla, matr, anno, subtype, dep);
			ImpTerra copy = new ImpTerra();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			//Copia la sede di installazione nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediInstallazione si = new SediInstallazione();

			if ( toCopy.getSedeInstallazione()!=null)
				si = copySedeInstallazione(toCopy.getSedeInstallazione());

			saveOnTarget(si);
			copy.setSedeInstallazione(si);

			/* I0070276 pezzo sostituito con sede con flag addebito
			//Copia la sede di addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			SediAddebito sa = new SediAddebito();

			if (cessione != null && cessione.getSediAddebitoFrom()!=null)
				sa = SediAddebitoAction.instance().copy(cessione.getSediAddebitoFrom()); 
			else if (cessione == null && toCopy.getSedeAddebito()!=null)
				sa = SediAddebitoAction.instance().copy(toCopy.getSedeAddebito());

			ca.create(sa);
			copy.setSedeAddebito(sa);
			 */

			// I0070276
			// Copia la sede con flag addebito nella fotografia dell'impianto (copy) da agganciare alla verifica
			Sedi se = new Sedi();

			if(toCopy.getSedi()!=null)
				se =copySede(toCopy.getSedi());

			saveOnTarget(se);
			copy.setSedi(se);

			//Copia l'indirizzo di spedizione nella fotografia dell'impianto (im) da agganciare alla verifica
			IndirizzoSped is = new IndirizzoSped();
			if ( toCopy.getIndirizzoSped()!=null)
				is =copyIndirizzoSped(toCopy.getIndirizzoSped());

			saveOnTarget(is);
			copy.setIndirizzoSped(is);

			// Copia l'impianto (toCopy) nella fotografia dell'impianto (im) da agganciare alla verifica
			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setAnno(toCopy.getAnno());
			copy.setAreaPeric(toCopy.getAreaPeric());
			copy.setAste(toCopy.getAste());
			copy.setCabine(toCopy.getCabine());
			//			copy.setCantieri(toCopy.getCantieri());

			copy.setCi0(toCopy.getCi0());
			copy.setCi1(toCopy.getCi1());
			copy.setCi2(toCopy.getCi2());
			copy.setCi3(toCopy.getCi3());

			copy.setCode(toCopy.getCode());
			copy.setCodiceImpianto(toCopy.getCodiceImpianto());
			copy.setDataCollaudo(toCopy.getDataCollaudo());
			copy.setCompetenza(toCopy.getCompetenza());
			copy.setDenominazione(toCopy.getDenominazione());

			copy.setDisperdenti(toCopy.getDisperdenti());
			copy.setDispersori(toCopy.getDispersori());
			copy.setImpColl(toCopy.getImpColl());
			//			copy.setIsolanti01(toCopy.getIsolanti01());
			//			copy.setIsolanti02(toCopy.getIsolanti02());

			copy.setMatricola(toCopy.getMatricola());
			//			copy.setMetalliche(toCopy.getMetalliche());
			copy.setNumeroFabbrica(toCopy.getNumeroFabbrica());
			copy.setPar(toCopy.getPar());
			//			copy.setParafulmini(toCopy.getParafulmini());

			copy.setPot(toCopy.getPot());
			copy.setRaggruppati01(toCopy.getRaggruppati01());
			copy.setRaggruppati02(toCopy.getRaggruppati02());

			//			copy.setSerbatoi(toCopy.getSerbatoi());
			copy.setSigla(toCopy.getSigla());

			//			copy.setStruttAereop(toCopy.getStruttAereop());
			copy.setSubType(toCopy.getSubType());
			copy.setSubTypeB(toCopy.getSubTypeB());
			copy.setSubTypeC(toCopy.getSubTypeC());
			copy.setSubTypeSoll(toCopy.getSubTypeSoll());
			copy.setSubTypeTerra(toCopy.getSubTypeTerra());

			copy.setSuperf01(toCopy.getSuperf01());
			copy.setSuperf02(toCopy.getSuperf03());
			copy.setSuperf02(toCopy.getSuperf03());

			copy.setTipologia(toCopy.getTipologia());
			copy.setTipologiaTesto(toCopy.getTipologiaTesto());
			copy.setType(toCopy.getType());

			// I0064824 
			copy.setStatoImpianto(toCopy.getStatoImpianto());

			// I0070276
			copy.setCabineCode(toCopy.getCabineCode());
			copy.setCabineNum(toCopy.getCabineNum());
			copy.setImpAutoprod(toCopy.getImpAutoprod());
			//			copy.setStruttAutopCode(toCopy.getStruttAutopCode());
			copy.setStruttAutopNum(toCopy.getStruttAutopNum());
			copy.setNote(toCopy.getNote());

			//campi aggiuntivi I00074067
			copy.setProtNumero(toCopy.getProtNumero());
			copy.setUtenteLettera(toCopy.getUtenteLettera());
			copy.setSezione(toCopy.getSezione());
			copy.setLetteraTrasm(toCopy.getLetteraTrasm());
			copy.setArtEsonero(toCopy.getArtEsonero());

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private SediInstallazione copySedeInstallazione(SediInstallazione toCopy){
		try{
			SediInstallazione copy = new SediInstallazione();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setTipoSede(toCopy.getTipoSede());
			copy.setTipologiaSede(toCopy.getTipologiaSede());

			//this.create();

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Sedi copySede(Sedi toCopy){
		try{
			Sedi copy = new Sedi();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setCopy(true);
			copy.setIsActive(false);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setSedeAddebito(toCopy.getSedeAddebito());

			copy.setCig(toCopy.getCig());
			copy.setCodiceUnivoco(toCopy.getCodiceUnivoco());
			copy.setClasseEconomica(toCopy.getClasseEconomica());
			copy.setCodContabilita(toCopy.getCodContabilita());
			copy.setCodiceVia(toCopy.getCodiceVia());
			copy.setDenominazioneUnitaLocale(toCopy.getDenominazioneUnitaLocale());
			copy.setEsenzione(toCopy.getEsenzione());
			copy.setImpSpesa(toCopy.getImpSpesa());
			copy.setNote(toCopy.getNote());
			copy.setPrincipaleAdd(toCopy.getPrincipaleAdd());
			copy.setSedePrincipale(toCopy.getSedePrincipale());
			copy.setSettore(toCopy.getSettore());
			copy.setStato(toCopy.getStato());

			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());

			copy.setTipoAttivita(toCopy.getTipoAttivita());
			copy.setTipoUtente(toCopy.getTipoUtente());
			copy.setZona(toCopy.getZona());

			List<IndirizzoSped> indirizzi = toCopy.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				copy.setIndirizzoSped(new ArrayList<IndirizzoSped>());

				for (IndirizzoSped ind:indirizzi){
					IndirizzoSped icopy = copyIndirizzoSped(ind);
					copy.getIndirizzoSped().add(icopy);
					saveOnTarget(icopy);
				}
			}

			copy.setPersonaGiuridica(toCopy.getPersonaGiuridica());

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private IndirizzoSped copyIndirizzoSped(IndirizzoSped toCopy){
		try{
			IndirizzoSped copy = new IndirizzoSped();
			copy.setCreatedBy(this.getClass().getSimpleName() + ulss);
			copy.setCreationDate(new Date());
			copy.setIsActive(false);
			copy.setCopy(true);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());

			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setPrincipale(toCopy.getPrincipale());
			copy.setStato(toCopy.getStato());

			//this.create();

			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private ImpSearchCollector copyIntoCollector(Impianto imp) {
		try{
			if (imp==null || imp.getCode()==null)
				return null;

			CodeValuePhi codeCv = imp.getCode();
			String code = codeCv.getCode();

			ImpSearchCollector coll = new ImpSearchCollector();
			coll.setCreatedBy(this.getClass().getSimpleName() + ulss);
			coll.setCreationDate(new Date());
			coll.setCode(codeCv);
			coll.setSigla(imp.getSigla());
			coll.setMatricola(imp.getMatricola());
			coll.setAnno(imp.getAnno());

			if (imp.getSedeInstallazione()!=null)
				coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());

			/* I0070276 pezzo sostituito con sede con flag addebito
			if (imp.getSedeAddebito()!=null)
				coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());
			 */

			// I0070276
			if (imp.getSedi()!=null)
				coll.setDenominazioneSA(imp.getSedi().getDenominazioneUnitaLocale());

			if(imp.getIndirizzoSped()!=null)
				coll.setDenominazioneIS(imp.getIndirizzoSped().getDenominazione());

			if (!"".equals(code)){

				//Apparecchi a pressione
				if ("01".equals(code))
					coll.setNumeroFabbrica(((ImpPress)imp).getNumeroFabbrica());

					//Impianti di riscaldamento
					//else if ("02".equals(code))

					//Ascensori e montacarichi
					//else if ("03".equals(code))

					//Apparecchi di sollevamento
				else if ("04".equals(code))
					coll.setSubTypeSoll(((ImpSoll)imp).getSubTypeSoll());

					//Impianti elettrici
				else if ("05".equals(code))
					coll.setSubTypeTerra(((ImpTerra)imp).getSubTypeTerra());
			}

			saveOnTarget(coll);
			return coll;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void importPreVerificaRisc(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

		Date dateUltimaVer = null;
		if(row[124].getContents()!=null && !row[124].getContents().trim().isEmpty()){
			dateUltimaVer = sdf.parse(row[124].getContents());
		}
		if(dateUltimaVer==null)
			return;

		ImpRisc impRisc = getImpRisc(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[123].getContents());
		boolean previousVerif = false;
		if(impRisc.getVerificaImp()!=null) {
			for (VerificaImp ver : impRisc.getVerificaImp()) {
				Calendar calVerifica = Calendar.getInstance();
				calVerifica.setTime(ver.getData());
				calVerifica.set(Calendar.MILLISECOND, 0);
				calVerifica.set(Calendar.SECOND, 0);
				calVerifica.set(Calendar.MINUTE, 0);
				calVerifica.set(Calendar.HOUR, 0);

				if (dateUltimaVer != null) {
					Calendar calPreVerif = Calendar.getInstance();
					calPreVerif.setTime(dateUltimaVer);
					calPreVerif.set(Calendar.MILLISECOND, 0);
					calPreVerif.set(Calendar.SECOND, 0);
					calPreVerif.set(Calendar.MINUTE, 0);
					calPreVerif.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calPreVerif.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

			}
		}
		if(previousVerif)
			return;


		VerificaImp verificaimp = new VerificaImp();
		verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
		verificaimp.setCreationDate(new Date());
		verificaimp.setPre(true);

		verificaimp.setData(dateUltimaVer);
		verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
		verificaimp.setImpType(impRisc.getCode());


		if(row[125].getContents()!=null && !row[125].getContents().trim().isEmpty()){
			verificaimp.setOperatore(getMappedOperatore(row[125].getContents()));
			if(verificaimp.getOperatore()==null) {
				log.error("Unable to find Operatore #"+ row[125].getContents());

			}
		}

		verificaimp.setImpRisc(impRisc);

		if(row[127].getContents()!=null && !row[127].getContents().trim().isEmpty() && controlBoolean(row[127].getContents())) {
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.01"));
		}else if(row[128].getContents()!=null && !row[128].getContents().trim().isEmpty()) {
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.06"));

			String tipoStraordVerif = row[128].getContents();
			if("phidic.arpav.imp.imptype.05".equals(verificaimp.getImpType().getOid()) ||
					("phidic.arpav.imp.imptype.01".equals(verificaimp.getImpType().getOid()) &&
							verificaimp.getImpPress()!=null &&
							verificaimp.getImpPress().getTipoApparecchio()!=null &&
							"phidic.arpav.imp.pressione.tipo.08".equals(verificaimp.getImpPress().getTipoApparecchio().getOid()))) {
				if(tipoStraordVerif.equals("A")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.01"));
				}else if(tipoStraordVerif.equals("B")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.02"));
				}else if(tipoStraordVerif.equals("C")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.03"));
				}else if(tipoStraordVerif.equals("D")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.04"));
				}else if(tipoStraordVerif.equals("E")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.05"));
				}else if(tipoStraordVerif.equals("F")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.06"));
				}else if(tipoStraordVerif.equals("G")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.07"));
				}else if(tipoStraordVerif.equals("H")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.08"));
				}else if(tipoStraordVerif.equals("I")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.09"));
				}else if(tipoStraordVerif.equals("L")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.10"));
				}else if(tipoStraordVerif.equals("S")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.11"));
				}else if(tipoStraordVerif.equals("T")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.12"));
				}else if(tipoStraordVerif.equals("V")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr.13"));
				}
			}else if("phidic.arpav.imp.imptype.02".equals(verificaimp.getImpType().getOid()) &&
					verificaimp.getImpRisc().getDescrizImpianto()!=null &&
					!"phidic.arpav.imp.riscaldamento.desc2.04".equals(verificaimp.getImpRisc().getDescrizImpianto().getOid())) {
				if(tipoStraordVerif.equals("C")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.03"));
				}else if(tipoStraordVerif.equals("D")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.02"));
				}else if(tipoStraordVerif.equals("V")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr02.01"));
				}
			}else if("phidic.arpav.imp.imptype.03".equals(verificaimp.getImpType().getOid())) {
				if(tipoStraordVerif.equals("A")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.03"));
				}else if(tipoStraordVerif.equals("C")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.02"));
				}else if(tipoStraordVerif.equals("S")) {
					verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.01"));
				}
			}else {
				verificaimp.setTipoStr(getCodeValue("arpav.ver.verstr03.01")); ////TODO:verificare su pspisal-lab
			}
		}else if(row[130].getContents()!=null && !row[130].getContents().trim().isEmpty()) {
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.05"));

			String tipoSopralluogo = row[130].getContents();
			if(tipoSopralluogo.equals("A")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.01"));
			}else if(tipoSopralluogo.equals("B")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.02"));
			}else if(tipoSopralluogo.equals("C")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.03"));
			}else if(tipoSopralluogo.equals("D")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.04"));
			}else if(tipoSopralluogo.equals("E")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.05"));
			}else if(tipoSopralluogo.equals("F")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.06"));
			}else if(tipoSopralluogo.equals("G")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.07"));
			}else if(tipoSopralluogo.equals("H")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.08"));
			}
		}else if(row[129].getContents()!=null && !row[129].getContents().trim().isEmpty()) {
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.05"));

			String tipoSopralluogo = row[129].getContents();
			if(tipoSopralluogo.equals("M")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.09"));
			}else if(tipoSopralluogo.equals("N")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.10"));
			}else if(tipoSopralluogo.equals("O")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.11"));
			}else if(tipoSopralluogo.equals("P")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.12"));
			}else if(tipoSopralluogo.equals("Q")) {
				verificaimp.setSopralluogo(getCodeValue("arpav.ver.sopralluogo.13"));
			}
		}

		if(row[131].getContents()!=null && !row[131].getContents().trim().isEmpty()){
			verificaimp.setEsito(getCodeValue(row[131].getContents()));
		}

		Date dateScadenza = null;
		if(row[126].getContents()!=null && !row[126].getContents().trim().isEmpty()){
			dateScadenza = sdf.parse(row[126].getContents());
		}

		if(dateScadenza!=null &&
				(verificaimp.getImpRisc().getNextVerifDate1()==null ||
						dateScadenza.after(verificaimp.getImpRisc().getNextVerifDate1()))) {
			verificaimp.getImpRisc().setNextVerifDate1(dateScadenza);
		}

		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");
		String dateMapping = sdfMapping.format(verificaimp.getData());

		String mappedId = dateMapping+"-"+"2"+"-"+impRisc.getSigla()+"-"+impRisc.getMatricola()+"-"+"x"+"-"+
				impRisc.getAnno()+"-"+row[123].getContents()+"-PRE";

		saveOnTarget(verificaimp);
		saveMapping(mappedId, verificaimp);
	}

	public void importPreVerificaPress(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

		Date dateInterna = null;
		if(row[41].getContents()!=null && !row[41].getContents().trim().isEmpty()){
			dateInterna = sdf.parse(row[41].getContents());
		}
		Date dateIdraulica = null;
		if(row[43].getContents()!=null && !row[43].getContents().trim().isEmpty()){
			dateIdraulica = sdf.parse(row[43].getContents());
		}
		Date dateEsercizio = null;
		if(row[45].getContents()!=null && !row[45].getContents().trim().isEmpty()){
			dateEsercizio = sdf.parse(row[45].getContents());
		}
		Date dateSopralluogo = null;
		if(row[50].getContents()!=null && !row[50].getContents().trim().isEmpty()){
			dateSopralluogo = sdf.parse(row[50].getContents());
		}
		if(dateInterna==null && dateIdraulica==null && dateEsercizio==null && dateSopralluogo==null)
			return;

		ImpPress impPress = getImpPress(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[38].getContents());
		boolean previousVerif = false;
		if(impPress.getVerificaImp()!=null) {
			for (VerificaImp ver : impPress.getVerificaImp()) {
				Calendar calVerifica = Calendar.getInstance();
				calVerifica.setTime(ver.getData());
				calVerifica.set(Calendar.MILLISECOND, 0);
				calVerifica.set(Calendar.SECOND, 0);
				calVerifica.set(Calendar.MINUTE, 0);
				calVerifica.set(Calendar.HOUR, 0);

				if (dateInterna != null) {
					Calendar calInterna = Calendar.getInstance();
					calInterna.setTime(dateInterna);
					calInterna.set(Calendar.MILLISECOND, 0);
					calInterna.set(Calendar.SECOND, 0);
					calInterna.set(Calendar.MINUTE, 0);
					calInterna.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calInterna.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

				if (dateIdraulica != null) {
					Calendar calIdraulica = Calendar.getInstance();
					calIdraulica.setTime(dateIdraulica);
					calIdraulica.set(Calendar.MILLISECOND, 0);
					calIdraulica.set(Calendar.SECOND, 0);
					calIdraulica.set(Calendar.MINUTE, 0);
					calIdraulica.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calIdraulica.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

				if (dateEsercizio != null) {
					Calendar calEsercizio = Calendar.getInstance();
					calEsercizio.setTime(dateEsercizio);
					calEsercizio.set(Calendar.MILLISECOND, 0);
					calEsercizio.set(Calendar.SECOND, 0);
					calEsercizio.set(Calendar.MINUTE, 0);
					calEsercizio.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calEsercizio.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

				if (dateSopralluogo != null) {
					Calendar calSopralluogo = Calendar.getInstance();
					calSopralluogo.setTime(dateSopralluogo);
					calSopralluogo.set(Calendar.MILLISECOND, 0);
					calSopralluogo.set(Calendar.SECOND, 0);
					calSopralluogo.set(Calendar.MINUTE, 0);
					calSopralluogo.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calSopralluogo.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}
			}
		}
		if(previousVerif)
			return;


		VerificaImp verificaimp = new VerificaImp();
		verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
		verificaimp.setCreationDate(new Date());
		verificaimp.setPre(true);

		Date dateScadenza = null;
		if(row[47].getContents()!=null && !row[47].getContents().trim().isEmpty()){
			dateScadenza = sdf.parse(row[47].getContents());
		}

		if(dateInterna!=null) {
			verificaimp.setInterna(true);
			verificaimp.setData(dateInterna);
		}
		if(dateIdraulica!=null) {
			verificaimp.setIdraulica(true);
			if(verificaimp.getData()==null)
				verificaimp.setData(dateIdraulica);
		}
		if(dateEsercizio!=null) {
			verificaimp.setEsercizio(true);
			if(verificaimp.getData()==null)
				verificaimp.setData(dateEsercizio);
		}
		if(verificaimp.getData()==null && dateSopralluogo!=null) {
			verificaimp.setData(dateSopralluogo);
		}

		verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
		verificaimp.setPrescrizioneBl(controlBoolean(row[52].getContents()));
		verificaimp.setImpType(impPress.getCode());


		if(row[51].getContents()!=null && !row[51].getContents().trim().isEmpty()){
			verificaimp.setOperatore(getMappedOperatore(row[51].getContents()));
			if(verificaimp.getOperatore()==null) {
				log.error("Unable to find Operatore #"+ row[51].getContents());

			}
		}
		verificaimp.setImpPress(impPress);

		if(dateInterna!=null && dateScadenza!=null &&
				(verificaimp.getImpPress().getNextVerifDate1()==null ||
						dateScadenza.after(verificaimp.getImpPress().getNextVerifDate1()))) {
			verificaimp.getImpPress().setNextVerifDate1(dateScadenza);
		}

		if(dateIdraulica!=null && dateScadenza!=null &&
				(verificaimp.getImpPress().getNextVerifDate2()==null ||
						dateScadenza.after(verificaimp.getImpPress().getNextVerifDate2()))) {
			verificaimp.getImpPress().setNextVerifDate2(dateScadenza);
		}

		if(dateEsercizio!=null && dateScadenza!=null &&
				(verificaimp.getImpPress().getNextVerifDate3()==null ||
						dateScadenza.after(verificaimp.getImpPress().getNextVerifDate3()))) {
			verificaimp.getImpPress().setNextVerifDate3(dateScadenza);
		}

		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");
		String dateMapping = sdfMapping.format(verificaimp.getData());

		String mappedId = dateMapping+"-"+"1"+"-"+impPress.getSigla()+"-"+impPress.getMatricola()+"-"+"x"+"-"+
				impPress.getAnno()+"-"+row[38].getContents()+"-PRE";

		saveOnTarget(verificaimp);
		saveMapping(mappedId, verificaimp);
	}

	public void importPreVerificaTerra(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

		Date dateUltimaVer = null;
		if(row[36].getContents()!=null && !row[36].getContents().trim().isEmpty()){
			dateUltimaVer = sdf.parse(row[36].getContents());
		}
		if(dateUltimaVer==null)
			return;

		String subTypeImp = getCodeValue(row[11].getContents()).getCode();
		int lengthSubType = subTypeImp.length();
		subTypeImp = subTypeImp.substring(lengthSubType-1, lengthSubType);

		ImpTerra impTerra = getImpTerra(row[1].getContents(), row[2].getContents(), row[3].getContents(),
				subTypeImp, row[34].getContents());
		boolean previousVerif = false;
		if(impTerra.getVerificaImp()!=null) {
			for (VerificaImp ver : impTerra.getVerificaImp()) {
				Calendar calVerifica = Calendar.getInstance();
				calVerifica.setTime(ver.getData());
				calVerifica.set(Calendar.MILLISECOND, 0);
				calVerifica.set(Calendar.SECOND, 0);
				calVerifica.set(Calendar.MINUTE, 0);
				calVerifica.set(Calendar.HOUR, 0);

				if (dateUltimaVer != null) {
					Calendar calPreVerif = Calendar.getInstance();
					calPreVerif.setTime(dateUltimaVer);
					calPreVerif.set(Calendar.MILLISECOND, 0);
					calPreVerif.set(Calendar.SECOND, 0);
					calPreVerif.set(Calendar.MINUTE, 0);
					calPreVerif.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calPreVerif.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

			}
		}
		if(previousVerif)
			return;


		VerificaImp verificaimp = new VerificaImp();
		verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
		verificaimp.setCreationDate(new Date());
		verificaimp.setPre(true);

		Date dateScadenza = null;
		if(row[35].getContents()!=null && !row[35].getContents().trim().isEmpty()){
			dateScadenza = sdf.parse(row[35].getContents());
		}

		verificaimp.setData(dateUltimaVer);
		verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
		verificaimp.setImpType(impTerra.getCode());


		if(row[37].getContents()!=null && !row[37].getContents().trim().isEmpty()){
			verificaimp.setOperatore(getMappedOperatore(row[37].getContents()));
			if(verificaimp.getOperatore()==null) {
				log.error("Unable to find Operatore #"+ row[37].getContents());

			}
		}

		if(row[38].getContents()!=null && "P".equals(row[38].getContents().trim())){
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.01"));
		}

		if(row[39].getContents()!=null && !row[39].getContents().trim().isEmpty()){
			verificaimp.setEsito(getCodeValue(row[39].getContents()));
		}
		if(row[40].getContents()!=null && !row[40].getContents().trim().isEmpty()){
			verificaimp.setPrescrizioneBl(true);
			verificaimp.setPrescrizione(getCodeValue(row[40].getContents()));
		}

		verificaimp.setImpTerra(impTerra);

		if(dateScadenza!=null &&
				(verificaimp.getImpTerra().getNextVerifDate1()==null ||
						dateScadenza.after(verificaimp.getImpTerra().getNextVerifDate1()))) {
			verificaimp.getImpTerra().setNextVerifDate1(dateScadenza);
		}

		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");
		String dateMapping = sdfMapping.format(verificaimp.getData());

		String mappedId = dateMapping+"-"+"4"+"-"+impTerra.getSigla()+"-"+impTerra.getMatricola()+"-"+subTypeImp+"-"+
				impTerra.getAnno()+"-"+row[34].getContents()+"-PRE";

		saveOnTarget(verificaimp);
		saveMapping(mappedId, verificaimp);
	}

	public void importPreVerificaMonta(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

		Date dateUltimaVer = null;
		if(row[37].getContents()!=null && !row[37].getContents().trim().isEmpty()){
			dateUltimaVer = sdf.parse(row[37].getContents());
		}
		if(dateUltimaVer==null)
			return;

		ImpMonta impmonta = getImpMonta(row[1].getContents(),row[2].getContents(),row[3].getContents(),row[35].getContents());
		boolean previousVerif = false;
		if(impmonta.getVerificaImp()!=null) {
			for (VerificaImp ver : impmonta.getVerificaImp()) {
				Calendar calVerifica = Calendar.getInstance();
				calVerifica.setTime(ver.getData());
				calVerifica.set(Calendar.MILLISECOND, 0);
				calVerifica.set(Calendar.SECOND, 0);
				calVerifica.set(Calendar.MINUTE, 0);
				calVerifica.set(Calendar.HOUR, 0);

				if (dateUltimaVer != null) {
					Calendar calPreVerif = Calendar.getInstance();
					calPreVerif.setTime(dateUltimaVer);
					calPreVerif.set(Calendar.MILLISECOND, 0);
					calPreVerif.set(Calendar.SECOND, 0);
					calPreVerif.set(Calendar.MINUTE, 0);
					calPreVerif.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calPreVerif.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

			}
		}
		if(previousVerif)
			return;


		VerificaImp verificaimp = new VerificaImp();
		verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
		verificaimp.setCreationDate(new Date());
		verificaimp.setPre(true);

		Date dateScadenza = null;
		if(row[36].getContents()!=null && !row[36].getContents().trim().isEmpty()){
			dateScadenza = sdf.parse(row[36].getContents());
		}

		verificaimp.setData(dateUltimaVer);
		verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
		verificaimp.setImpType(impmonta.getCode());


		if(row[38].getContents()!=null && !row[38].getContents().trim().isEmpty()){
			verificaimp.setOperatore(getMappedOperatore(row[38].getContents()));
			if(verificaimp.getOperatore()==null) {
				log.error("Unable to find Operatore #"+ row[38].getContents());

			}
		}

		if(row[39].getContents()!=null && "P".equals(row[39].getContents().trim())){
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.01"));
		}

		if(row[40].getContents()!=null && !row[40].getContents().trim().isEmpty()){
			verificaimp.setEsito(getCodeValue(row[40].getContents()));
		}
		if(row[41].getContents()!=null && !row[41].getContents().trim().isEmpty()){
			verificaimp.setPrescrizioneBl(true);
			verificaimp.setPrescrizione(getCodeValue(row[41].getContents()));
		}
		verificaimp.setImpMonta(impmonta);

		if(dateScadenza!=null &&
				(verificaimp.getImpMonta().getNextVerifDate1()==null ||
						dateScadenza.after(verificaimp.getImpMonta().getNextVerifDate1()))) {
			verificaimp.getImpMonta().setNextVerifDate1(dateScadenza);
		}

		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");
		String dateMapping = sdfMapping.format(verificaimp.getData());

		String mappedId = dateMapping+"-"+"3"+"-"+impmonta.getSigla()+"-"+impmonta.getMatricola()+"-"+"x"+"-"+
				impmonta.getAnno()+"-"+row[35].getContents()+"-PRE";

		saveOnTarget(verificaimp);
		saveMapping(mappedId, verificaimp);
	}

	public void importPreVerificaSoll(Cell[] row) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");

		Date dateUltimaVer = null;
		if(row[29].getContents()!=null && !row[29].getContents().trim().isEmpty()){
			dateUltimaVer = sdf.parse(row[29].getContents());
		}
		if(dateUltimaVer==null)
			return;

		ImpSoll impSoll = getImpSoll(row[1].getContents(), row[3].getContents(), row[4].getContents(), row[26].getContents(), row[27].getContents());
		boolean previousVerif = false;
		if(impSoll.getVerificaImp()!=null) {
			for (VerificaImp ver : impSoll.getVerificaImp()) {
				Calendar calVerifica = Calendar.getInstance();
				calVerifica.setTime(ver.getData());
				calVerifica.set(Calendar.MILLISECOND, 0);
				calVerifica.set(Calendar.SECOND, 0);
				calVerifica.set(Calendar.MINUTE, 0);
				calVerifica.set(Calendar.HOUR, 0);

				if (dateUltimaVer != null) {
					Calendar calPreVerif = Calendar.getInstance();
					calPreVerif.setTime(dateUltimaVer);
					calPreVerif.set(Calendar.MILLISECOND, 0);
					calPreVerif.set(Calendar.SECOND, 0);
					calPreVerif.set(Calendar.MINUTE, 0);
					calPreVerif.set(Calendar.HOUR, 0);

					int compareResult = calVerifica.getTime().compareTo(calPreVerif.getTime());
					if (compareResult >= 0) {
						previousVerif = true;
						break;
					}
				}

			}
		}
		if(previousVerif)
			return;


		VerificaImp verificaimp = new VerificaImp();
		verificaimp.setCreatedBy(this.getClass().getSimpleName() + ulss);
		verificaimp.setCreationDate(new Date());
		verificaimp.setPre(true);

		Date dateScadenza = null;
		if(row[28].getContents()!=null && !row[28].getContents().trim().isEmpty()){
			dateScadenza = sdf.parse(row[28].getContents());
		}

		verificaimp.setData(dateUltimaVer);
		verificaimp.setStatusCode(getCodeValue("arpav.ver.stato.verified"));
		verificaimp.setImpType(impSoll.getCode());


		if(row[30].getContents()!=null && !row[30].getContents().trim().isEmpty()){
			verificaimp.setOperatore(getMappedOperatore(row[30].getContents()));
			if(verificaimp.getOperatore()==null) {
				log.error("Unable to find Operatore #"+ row[30].getContents());

			}
		}

		if(row[31].getContents()!=null && "P".equals(row[31].getContents().trim())){
			verificaimp.setTipo(getCodeValue("arpav.ver.tipover.01"));
		}

		if(row[32].getContents()!=null && !row[32].getContents().trim().isEmpty()){
			verificaimp.setEsito(getCodeValue(row[32].getContents()));
		}
		if(row[33].getContents()!=null && !row[33].getContents().trim().isEmpty()){
			verificaimp.setPrescrizioneBl(true);
			verificaimp.setPrescrizione(getCodeValue(row[33].getContents()));
		}
		verificaimp.setImpSoll(impSoll);

		if(dateScadenza!=null &&
				(verificaimp.getImpSoll().getNextVerifDate1()==null ||
						dateScadenza.after(verificaimp.getImpSoll().getNextVerifDate1()))) {
			verificaimp.getImpSoll().setNextVerifDate1(dateScadenza);
		}

		SimpleDateFormat sdfMapping = new SimpleDateFormat("yyyyMMdd");
		String dateMapping = sdfMapping.format(verificaimp.getData());

		String mappedId = dateMapping+"-"+"4"+"-"+impSoll.getSigla()+"-"+impSoll.getMatricola()+"-"+row[26].getContents()+"-"+
				impSoll.getAnno()+"-"+row[27].getContents()+"-PRE";

		saveOnTarget(verificaimp);
		saveMapping(mappedId, verificaimp);
	}

	public void bonificaNextVerifDates(List<Long> verifIds){
		for(Long verifId : verifIds){
			VerificaImp ver = targetEm.find(VerificaImp.class, verifId);

			VerificaImp verificaLast = null;

			if(ver.getImpPress()!=null){
				ImpPress imp = ver.getImpPress();

				List<VerificaImp> verificaImpList = imp.getVerificaImp();
				if (verificaImpList!=null){

					for (VerificaImp vi : verificaImpList){
						if (vi!=null && !Boolean.TRUE.equals(vi.getPre())){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

			}else if(ver.getImpRisc()!=null){
				ImpRisc imp = ver.getImpRisc();

				List<VerificaImp> verificaImpList = imp.getVerificaImp();
				if (verificaImpList!=null){

					for (VerificaImp vi : verificaImpList){
						if (vi!=null && !Boolean.TRUE.equals(vi.getPre())){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

			}else if(ver.getImpMonta()!=null){
				ImpMonta imp = ver.getImpMonta();

				List<VerificaImp> verificaImpList = imp.getVerificaImp();
				if (verificaImpList!=null){

					for (VerificaImp vi : verificaImpList){
						if (vi!=null && !Boolean.TRUE.equals(vi.getPre())){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

			}else if(ver.getImpSoll()!=null){
				ImpSoll imp = ver.getImpSoll();

				List<VerificaImp> verificaImpList = imp.getVerificaImp();
				if (verificaImpList!=null){

					for (VerificaImp vi : verificaImpList){
						if (vi!=null && !Boolean.TRUE.equals(vi.getPre())){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

			}else if(ver.getImpTerra()!=null){
				ImpTerra imp = ver.getImpTerra();

				List<VerificaImp> verificaImpList = imp.getVerificaImp();
				if (verificaImpList!=null){

					for (VerificaImp vi : verificaImpList){
						if (vi!=null && !Boolean.TRUE.equals(vi.getPre())){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

			}

			updateImpianto(verificaLast, ver);
			try {
				saveOnTarget(ver);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void updateImpianto(VerificaImp verificaLast, VerificaImp ver){
		try{

			/* Mi assicuro che la verifica oggetto di manipolazione sia in uno stato diverso da nuovo.
			 * Si tratta di un doppio check perchè questa funzione viene chiamata ogni volta che si valida
			 * una verifica interna ed ogni volta che si salva una verifica esterna (di default in stato validato) */
			if (ver!=null && ver.getStatusCode()!=null && ver.getStatusCode().getCode()!=null && !"new".equals(ver.getStatusCode().getCode())){

				//Se la verifica oggetto di manipolazione è prima verifica (verificaLast è null) OPPURE coincide con l'ultima verifica OPPURE è più recente dell'ultima verifica..
				if (verificaLast==null || verificaLast==ver || verificaLast.getData().before(ver.getData())){
					//..aggiorna gli attributi (nextVerifDate*/statoImpianto/dataVariazioneStato) del relativo impianto
					//.. e della copia storica

					if (ver.getImpPress()!=null){
						if (ver.getNextVerifDate1()!=null)
							ver.getImpPress().setNextVerifDate1(ver.getNextVerifDate1());

						if (ver.getNextVerifDate2()!=null)
							ver.getImpPress().setNextVerifDate2(ver.getNextVerifDate2());

						if (ver.getNextVerifDate3()!=null)
							ver.getImpPress().setNextVerifDate3(ver.getNextVerifDate3());

						if (ver.getStatoImp()!=null)
							ver.getImpPress().setStatoImpianto(ver.getStatoImp());

						if (ver.getDataVar()!=null)
							ver.getImpPress().setDataVariazioneStato(ver.getDataVar());//??

						if(ver.getImpPressCpy()!=null){
							if (ver.getNextVerifDate1()!=null)
								ver.getImpPressCpy().setNextVerifDate1(ver.getNextVerifDate1());

							if (ver.getNextVerifDate2()!=null)
								ver.getImpPressCpy().setNextVerifDate2(ver.getNextVerifDate2());

							if (ver.getNextVerifDate3()!=null)
								ver.getImpPressCpy().setNextVerifDate3(ver.getNextVerifDate3());
						}

					} else if (ver.getImpRisc()!=null){
						if (ver.getNextVerifDate1()!=null)
							ver.getImpRisc().setNextVerifDate1(ver.getNextVerifDate1());

						if (ver.getStatoImp()!=null)
							ver.getImpRisc().setStatoImpianto(ver.getStatoImp());

						if (ver.getDataVar()!=null)
							ver.getImpRisc().setDataVariazioneStato(ver.getDataVar());//??

						if(ver.getImpRiscCpy()!=null){
							if (ver.getNextVerifDate1()!=null)
								ver.getImpRiscCpy().setNextVerifDate1(ver.getNextVerifDate1());
						}
					} else if (ver.getImpMonta()!=null){
						if (ver.getNextVerifDate1()!=null)
							ver.getImpMonta().setNextVerifDate1(ver.getNextVerifDate1());

						if (ver.getStatoImp()!=null)
							ver.getImpMonta().setStatoImpianto(ver.getStatoImp());

						if (ver.getDataVar()!=null)
							ver.getImpMonta().setDataVariazioneStato(ver.getDataVar());//??

						if(ver.getImpMontaCpy()!=null){
							if (ver.getNextVerifDate1()!=null)
								ver.getImpMontaCpy().setNextVerifDate1(ver.getNextVerifDate1());
						}

					} else if (ver.getImpSoll()!=null){
						if (ver.getNextVerifDate1()!=null)
							ver.getImpSoll().setNextVerifDate1(ver.getNextVerifDate1());

						if (ver.getStatoImp()!=null)
							ver.getImpSoll().setStatoImpianto(ver.getStatoImp());

						if (ver.getDataVar()!=null)
							ver.getImpSoll().setDataVariazioneStato(ver.getDataVar());//??

						if(ver.getImpSollCpy()!=null){
							if (ver.getNextVerifDate1()!=null)
								ver.getImpSollCpy().setNextVerifDate1(ver.getNextVerifDate1());
						}

					} else if (ver.getImpTerra()!=null){
						if (ver.getNextVerifDate1()!=null)
							ver.getImpTerra().setNextVerifDate1(ver.getNextVerifDate1());

						if (ver.getStatoImp()!=null)
							ver.getImpTerra().setStatoImpianto(ver.getStatoImp());

						if (ver.getDataVar()!=null)
							ver.getImpTerra().setDataVariazioneStato(ver.getDataVar());//??

						if(ver.getImpTerraCpy()!=null){
							if (ver.getNextVerifDate1()!=null)
								ver.getImpTerraCpy().setNextVerifDate1(ver.getNextVerifDate1());
						}

					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
}
