package com.phi.db.arpav.importer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;

public class ArpavImporter extends EntityManagerUtilities {

	private static final Logger thislog = Logger.getLogger(ArpavImporter.class.getName());
	private Workbook wbExcel;
	private Sheet sheet;

	public ArpavImporter(){
		thislog.info("Starting Importer");

	}

	public static void main (String[] args) throws ParseException {

		String operation = "";
		String xlsFileName = null;
		Integer startRow = 1;
		if(args!=null){

			boolean nextIsUlss = false;
			boolean nextIsDist = false;
			for(String arg : args){
				if("-emp".equals(arg) && operation.isEmpty()){
					operation="emp";//-emp Employee_Tracciato.xls
				}else if("-ditta".equals(arg) && operation.isEmpty()){
					operation="ditta";//-ditta PersoneGiuridiche_Tracciato.xls
				}else if("-operatore".equals(arg) && operation.isEmpty()){
					operation="operatore";//-operatore operatore_Tracciato.xls
				}else if("-indirizzoSped".equals(arg) && operation.isEmpty()){
					operation="indirizzoSped";//-indirizzoSped indirizzoSped_Tracciato.xls
				}else if("-updateIndirizzoSped".equals(arg) && operation.isEmpty()){
					operation="updateIndirizzoSped";//-indirizzoSped indirizzoSped_Tracciato.xls
				}else if("-sediInstallazione".equals(arg) && operation.isEmpty()){
					operation="sediInstallazione";//-sediInstallazione sediInstallazione_Tracciato.xls
				}else if("-sediAddebito".equals(arg) && operation.isEmpty()){
					operation="sediAddebito";//-sediAddebito SediAddebito_Tracciato.xls
				}else if("-impMonta".equals(arg) && operation.isEmpty()){
					operation="impMonta";//-impMonta impMonta_Tracciato.xls
				}else if("-impPress".equals(arg) && operation.isEmpty()){
					operation="impPress";//-impPress impPress_Tracciato.xls
				}else if("-impRisc".equals(arg) && operation.isEmpty()){
					operation="impRisc";//-impRisc impRisc_Tracciato.xls
				}else if("-impSoll".equals(arg) && operation.isEmpty()){
					operation="impSoll";//-impSoll impSoll_Tracciato.xls
				}else if("-impTerra".equals(arg) && operation.isEmpty()){
					operation="impTerra";//-impTerra impTerra_Tracciato.xls
				}else if("-addebito".equals(arg) && operation.isEmpty()){
					operation="addebito";//-addebito addebito_Tracciato.xls
				}else if("-verificaImp".equals(arg) && operation.isEmpty()){
					operation="verificaImp";//-verificaImp verificaImp_Tracciato.xls
				}else if("-fattura".equals(arg) && operation.isEmpty()){
					operation="fattura";
				}else if("-ulss".equals(arg) && operation.isEmpty()){
					nextIsUlss = true;
				}else if("-pre".equals(arg) && !operation.isEmpty()){
					operation = operation + "Pre";
				}else if(nextIsUlss){
					EntityManagerUtilities.ulss=arg;
					nextIsUlss=false;
				}else if("-dist".equals(arg) && operation.isEmpty()){
					nextIsDist = true;
				}else if(nextIsDist){
					EntityManagerUtilities.distretto=arg;
					nextIsDist=false;
				}else if("-eclipse".equals(arg)) {
					usingEclipse = true;
				}else if(arg != null && arg.contains("xls")) {
					xlsFileName = arg;
				}else {
					try {
						startRow = Integer.parseInt(arg);
					} catch (NumberFormatException e) {
						startRow = 1;
					}
				}
			}
		}

		if (operation.isEmpty()) {
			log.error("missing operation, use switch like -maps, -prat ... see manual.odt");
		}

		ArpavImporter importer = new ArpavImporter();

		//TODO: aggiungere lettura file della directory al posto del contatore k
		for(int k=1; k<2; k++) {

			String xlsNumberedFileName = "";

			if(xlsFileName!=null && xlsFileName.contains("xx")){
				xlsNumberedFileName = xlsFileName.replace("xx", "0"+k);
			}else{
				xlsNumberedFileName = xlsFileName;
			}

			importer.loadXlsFile(xlsNumberedFileName);
			if(importer.sheet==null) {
				if(operation == "verifica") {
					List<Long> toFixVerifs = readVerifIds();
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();

					targetEm.getTransaction().begin();
					verificaImpImporter.bonificaNextVerifDates(toFixVerifs);
					if (targetEm.getTransaction().isActive()) {
						targetEm.flush();
						targetEm.getTransaction().commit();
						targetEm.clear();
					}
					importer.closeResource();
				}else if(operation == "verificaImp"){
					List<String> impPressSigle = readImpPressSigla();
					ImpPressImporter impPressImporter = ImpPressImporter.getInstance();
					targetEm.getTransaction().begin();

					int q = 0;
					for(String sigla : impPressSigle) {
						List<Long> impPressList = readOriginalImpPress(sigla);

						for (Long impPress : impPressList) {
							log.info("Processing ImpPress with sigla "+impPress);
							impPressImporter.updateVerifIndicator(impPress);
							q++;

							if (q % 1000 == 0) {
								targetEm.flush();
								targetEm.getTransaction().commit();
								targetEm.clear();
								targetEm.getTransaction().begin();
							}
						}
					}

					List<String> impRiscSigle = readImpRiscSigla();
					ImpRiscImporter impRiscImporter = ImpRiscImporter.getInstance();
					for(String sigla : impRiscSigle) {
						List<Long> impRiscList = readOriginalImpRisc(sigla);

						for (Long impRisc : impRiscList) {
							log.info("Processing ImpRisc with sigla "+impRisc);
							impRiscImporter.updateVerifIndicator(impRisc);
							q++;

							if (q % 1000 == 0) {
								targetEm.flush();
								targetEm.getTransaction().commit();
								targetEm.clear();
								targetEm.getTransaction().begin();
							}
						}
					}

					List<String> impMontaSigle = readImpMontaSigla();
					ImpMontaImporter impMontaImporter = ImpMontaImporter.getInstance();
					for(String sigla : impMontaSigle) {
						List<Long> impMontaList = readOriginalImpMonta(sigla);

						for (Long impMonta : impMontaList) {
							log.info("Processing ImpMonta with sigla "+impMonta);
							impMontaImporter.updateVerifIndicator(impMonta);
							q++;

							if (q % 1000 == 0) {
								targetEm.flush();
								targetEm.getTransaction().commit();
								targetEm.clear();
								targetEm.getTransaction().begin();
							}
						}
					}

					List<String> impSollSigle = readImpSollSigla();
					ImpSollImporter impSollImporter = ImpSollImporter.getInstance();
					for(String sigla : impSollSigle) {
						List<Long> impSollList = readOriginalImpSoll(sigla);

						for (Long impSoll : impSollList) {
							log.info("Processing ImpSoll with sigla "+impSoll);
							impSollImporter.updateVerifIndicator(impSoll);
							q++;

							if (q % 1000 == 0) {
								targetEm.flush();
								targetEm.getTransaction().commit();
								targetEm.clear();
								targetEm.getTransaction().begin();
							}
						}
					}

					List<String> impTerraSigle = readImpTerraSigla();
					ImpTerraImporter impTerraImporter = ImpTerraImporter.getInstance();
					for(String sigla : impTerraSigle) {
						List<Long> impTerraList = readOriginalImpTerra(sigla);

						for (Long impTerra : impTerraList) {
							log.info("Processing ImpTerra with sigla "+impTerra);
							impTerraImporter.updateVerifIndicator(impTerra);
							q++;

							if (q % 1000 == 0) {
								targetEm.flush();
								targetEm.getTransaction().commit();
								targetEm.clear();
								targetEm.getTransaction().begin();
							}
						}
					}

					if (targetEm.getTransaction().isActive()){
						targetEm.flush();
						targetEm.getTransaction().commit();
						targetEm.clear();
					}
				}else if(operation == "sediInstallazione"){
					List<Long> siList = readSediInstallazione();
					SediInstallazioneImporter sediInstallazioneImporter = SediInstallazioneImporter.getInstance();
					targetEm.getTransaction().begin();

					int q = 0;
					for (Long sedeInstallazione : siList) {
						sediInstallazioneImporter.setDeletable(sedeInstallazione);
						q++;

						if (q % 1000 == 0) {
							targetEm.flush();
							targetEm.getTransaction().commit();
							targetEm.clear();
							targetEm.getTransaction().begin();
						}
					}

					if (targetEm.getTransaction().isActive()){
						targetEm.flush();
						targetEm.getTransaction().commit();
						targetEm.clear();
					}

				}
				log.info("Processing end.");
				return;
			}
			boolean rolledBack = false;
			int i = startRow;
			int j = 0;
			targetEm.getTransaction().begin();


			while (i < importer.sheet.getRows()) {
				Cell[] row = importer.sheet.getRow(i);
				if (row[0].getContents() == null || row[0].getContents().isEmpty()) {
					log.info("Breaking import by an empty row");
					break;
				}
				if ("emp".equals(operation)) {
					EmployeeImporter empImporter = EmployeeImporter.getInstance();
					empImporter.importRow(row);

				} else if ("ditta".equals(operation)) {
					PersoneGiuridicheImporter orgImporter = PersoneGiuridicheImporter.getInstance();
					orgImporter.importRow(row);

				} else if ("operatore".equals(operation)) {
					OperatoreImporter operatoreImporter = OperatoreImporter.getInstance();
					//operatoreImporter.importRow(row);
					operatoreImporter.importMapRow(row);
				}
				//			else if("indirizzoSped".equals(operation)) {
				//				IndirizzoSpedImporter indirizzoSpedImporter = IndirizzoSpedImporter.getInstance();
				//				indirizzoSpedImporter.importRow(row);
				//
				//			}

				else if ("sediInstallazione".equals(operation)) {
					SediInstallazioneImporter sediInstallazioneImporter = SediInstallazioneImporter.getInstance();
					sediInstallazioneImporter.importRow(row);

				} else if ("sediAddebito".equals(operation)) {
					SediAddebitoImporter sediAddeditoImporter = SediAddebitoImporter.getInstance();
					sediAddeditoImporter.importRow(row);

				} else if ("updateIndirizzoSped".equals(operation)) {
					SediAddebitoImporter sediAddeditoImporter = SediAddebitoImporter.getInstance();
					//sediAddeditoImporter.updateIndirizzoPrincipale(row);

				} else if ("impMonta".equals(operation)) {
					ImpMontaImporter impMontaImporter = ImpMontaImporter.getInstance();
					impMontaImporter.importRow(row);

				} else if ("impPress".equals(operation)) {
					ImpPressImporter impPressImporter = ImpPressImporter.getInstance();
					impPressImporter.importRow(row);


				} else if ("impRisc".equals(operation)) {
					ImpRiscImporter impRiscImporter = ImpRiscImporter.getInstance();
					impRiscImporter.importRow(row);


				} else if ("impSoll".equals(operation)) {
					ImpSollImporter impSollImporter = ImpSollImporter.getInstance();
					impSollImporter.importRow(row);

				} else if ("impTerra".equals(operation)) {
					ImpTerraImporter impTerraImporter = ImpTerraImporter.getInstance();
					impTerraImporter.importRow(row);

				} else if ("addebito".equals(operation)) {
					AddebitoImporter addebitoImporter = AddebitoImporter.getInstance();
					addebitoImporter.importRow(row);

				} else if ("impPressPre".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					verificaImpImporter.importPreVerificaPress(row);
				} else if ("impRiscPre".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					verificaImpImporter.importPreVerificaRisc(row);
				} else if ("impMontaPre".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					verificaImpImporter.importPreVerificaMonta(row);
				} else if ("impSollPre".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					verificaImpImporter.importPreVerificaSoll(row);
				} else if ("impTerraPre".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					verificaImpImporter.importPreVerificaTerra(row);
				} else if ("verificaImp".equals(operation)) {
					VerificaImpImporter verificaImpImporter = VerificaImpImporter.getInstance();
					try {
						verificaImpImporter.importRow(row);
					} catch (PersistenceException pe) {
						if (targetEm.getTransaction().isActive())
							targetEm.getTransaction().rollback();
						targetEm.clear();
						pe.printStackTrace();


						rolledBack = true;
						j = i;
						if (i % 1000 == 0) {
							i -= 999;
						} else {
							i = i - (i % 1000) + 1;
						}
						targetEm.getTransaction().begin();
					}
				} else if ("fattura".equals(operation)) {
					FatturaImporter fatturaImporter = FatturaImporter.getInstance();
					try {
						fatturaImporter.importRow(row);
					} catch (PersistenceException pe) {
						if (targetEm.getTransaction().isActive())
							targetEm.getTransaction().rollback();
						targetEm.clear();
						pe.printStackTrace();


						rolledBack = true;
						j = i;
						if (i % 1000 == 0) {
							i -= 999;
						} else {
							i = i - (i % 1000) + 1;
						}
						targetEm.getTransaction().begin();
					}
				}


				if (rolledBack && i == (j - 1)) {
					targetEm.flush();
					targetEm.getTransaction().commit();
					targetEm.clear();
					targetEm.getTransaction().begin();
					rolledBack = false;
				}

				if (i % 1000 == 0) {
					targetEm.flush();
					targetEm.getTransaction().commit();
					targetEm.clear();
					targetEm.getTransaction().begin();
				}


				i++;
			}
			if (targetEm.getTransaction().isActive()){
				targetEm.flush();
				targetEm.getTransaction().commit();
				targetEm.clear();
			}
		}
		/*
		targetEm.flush();
		targetEm.getTransaction().commit();
		targetEm.clear();
*/
		if(importer.wbExcel!=null)
			importer.wbExcel.close();

		importer.closeResource();
	}

	public void loadXlsFile(String xlsFileName) {
		if (xlsFileName == null || xlsFileName.isEmpty()) {
			log.info("NO XLS FILE NAME PROVIDED. QUITTING.");
			return;
		}
		InputStream xls;
		try {
			//in caso di debug
			if (usingEclipse) {
				log.info("[usingEclipse] reading db/"+xlsFileName);
				xls = new FileInputStream("db/"+xlsFileName);
			}
			else {//in caso di jar
				log.info("reading "+xlsFileName);
				xls = new FileInputStream(xlsFileName);
			}

			wbExcel = Workbook.getWorkbook(xls);
			sheet = wbExcel.getSheet(0);

			if (sheet == null) {
				thislog.error("Error getting sheet");
				return;
			}

		}catch (Exception e) {
			log.info("Exception while reading xls file:"+ xlsFileName);
		}
	}

	public static List<Long> readVerifIds(){
		Query postImportVerifs = targetEm.createQuery("SELECT v.internalId FROM VerificaImp v WHERE v.createdBy !='VerificaImpImporterARPAV' " +
				"AND (v.statusCode='phidic.arpav.ver.stato.verified_V0' OR v.statusCode='phidic.arpav.ver.stato.completed_V0')");
		List<Long> postImportVers = postImportVerifs.getResultList();

		return postImportVers;
	}

	public static List<Long> readOriginalImpPress(String sigla){
		Query originalImpianti = targetEm.createQuery("SELECT imp.internalId FROM ImpPress imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) AND imp.sigla = :sigla");
		originalImpianti.setParameter("sigla", sigla);
		List<Long> impiantiList = originalImpianti.getResultList();

		return impiantiList;
	}

	public static List<Long> readOriginalImpRisc(String sigla){
		Query originalImpianti = targetEm.createQuery("SELECT imp.internalId FROM ImpRisc imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) AND imp.sigla = :sigla");
		originalImpianti.setParameter("sigla", sigla);
		List<Long> impiantiList = originalImpianti.getResultList();

		return impiantiList;
	}

	public static List<Long> readOriginalImpMonta(String sigla){
		Query originalImpianti = targetEm.createQuery("SELECT imp.internalId FROM ImpMonta imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) AND imp.sigla = :sigla");
		originalImpianti.setParameter("sigla", sigla);
		List<Long> impiantiList = originalImpianti.getResultList();

		return impiantiList;
	}

	public static List<Long> readOriginalImpSoll(String sigla){
		Query originalImpianti = targetEm.createQuery("SELECT imp.internalId FROM ImpSoll imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) AND imp.sigla = :sigla");
		originalImpianti.setParameter("sigla", sigla);
		List<Long> impiantiList = originalImpianti.getResultList();

		return impiantiList;
	}

	public static List<Long> readOriginalImpTerra(String sigla){
		Query originalImpianti = targetEm.createQuery("SELECT imp.internalId FROM ImpTerra imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) AND imp.sigla = :sigla");
		originalImpianti.setParameter("sigla", sigla);
		List<Long> impiantiList = originalImpianti.getResultList();

		return impiantiList;
	}

	public static List<String> readImpPressSigla(){
		Query qSigleImpianti = targetEm.createQuery("SELECT DISTINCT imp.sigla FROM ImpPress imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) ");
		List<String> sigleList = qSigleImpianti.getResultList();

		return sigleList;
	}

	public static List<String> readImpRiscSigla(){
		Query qSigleImpianti = targetEm.createQuery("SELECT DISTINCT imp.sigla FROM ImpRisc imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) ");
		List<String> sigleList = qSigleImpianti.getResultList();

		return sigleList;
	}

	public static List<String> readImpMontaSigla(){
		Query qSigleImpianti = targetEm.createQuery("SELECT DISTINCT imp.sigla FROM ImpMonta imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) ");
		List<String> sigleList = qSigleImpianti.getResultList();

		return sigleList;
	}

	public static List<String> readImpSollSigla(){
		Query qSigleImpianti = targetEm.createQuery("SELECT DISTINCT imp.sigla FROM ImpSoll imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) ");
		List<String> sigleList = qSigleImpianti.getResultList();

		return sigleList;
	}

	public static List<String> readImpTerraSigla(){
		Query qSigleImpianti = targetEm.createQuery("SELECT DISTINCT imp.sigla FROM ImpTerra imp " +
				"WHERE (imp.copy IS NULL OR imp.copy = false) ");
		List<String> sigleList = qSigleImpianti.getResultList();

		return sigleList;
	}

	public static List<Long> readSediInstallazione(){
		Query qSediInstallazione = targetEm.createQuery("SELECT si.internalId FROM SediInstallazione si " +
				"WHERE (si.copy IS NULL OR si.copy = false) AND si.sede IS NOT NULL");
		List<Long> siList = qSediInstallazione.getResultList();

		return siList;
	}

}
