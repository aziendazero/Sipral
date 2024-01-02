package com.phi.entities.actions;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.phi.cs.datamodel.PagedDataModel;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.VerificheScadDocument;
import com.phi.entities.baseEntity.VerificheTecDocument;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Operatore;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;

@BypassInterceptors
@Name("VerificheScadDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class VerificheScadDocumentAction extends BaseAction<VerificheScadDocument, Long> {

	private static final long serialVersionUID = 287692101L;

	private int firstColumnOfData = 0; //first column of excel datagrid
	private int firstRowOfData = 2; //first row of excel datagrid
	private int firstRowOfDataPress = 1;
	private int firstRowOfDataRisc = 1;
	private int firstRowOfDataMonta = 1;
	private int firstRowOfDataSoll = 1;
	private int firstRowOfDataTerra = 1;

	public static VerificheScadDocumentAction instance() {
		return (VerificheScadDocumentAction) Component.getInstance(VerificheScadDocumentAction.class, ScopeType.CONVERSATION);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void generateFile(VerificheScadDocument vdoc, PagedDataModel<Impianto> impList){
		ByteArrayOutputStream output = null;

		try {
			if (impList==null || impList.getFullList()==null)
				return;

			output = new ByteArrayOutputStream();
			WritableWorkbook wb = Workbook.createWorkbook(output);
			WritableSheet sheetTutti = wb.createSheet("SET DATI TUTTI", 0);
			WritableSheet sheetTerra = wb.createSheet("IMP. ELETTRICI", 0);
			WritableSheet sheetSoll = wb.createSheet("SOLLEVAMENTO", 0);
			WritableSheet sheetMonta = wb.createSheet("ASCENSORI", 0);
			WritableSheet sheetRisc = wb.createSheet("RISCALDAMENTO", 0);
			WritableSheet sheetPress = wb.createSheet("PRESSIONE", 0);

			//RIGA INTESTAZIONI
			String[] headerPress = {"SIGLA", "TIPO", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO", 
					"CARATTERISTICHE SPECIALI", "COMODANTE", "COSTRUTTORE", "NUMERO FABBRICA", 
					"PRESSIONE BAR 1^", "CAPACITÀ (LT)", "SUPERFICIE MQ", "PRODUCIBILITA T/H", 
					"ESONERO", "STATO IMPIANTO", "NOTE"};

			writeCellsFromArray(headerPress, sheetPress, 0);

			String[] headerRisc = {"SIGLA", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO"};

			writeCellsFromArray(headerRisc, sheetRisc, 0);

			String[] headerMonta = {"SIGLA", "CATEGORIA", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO"};

			writeCellsFromArray(headerMonta, sheetMonta, 0);

			String[] headerSoll = {"SIGLA", "TIPO", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO"};

			writeCellsFromArray(headerSoll, sheetSoll, 0);

			String[] headerTerra = {"SIGLA", "TIPO", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO"};

			writeCellsFromArray(headerTerra, sheetTerra, 0);

			String[] headerTutti = {"SIGLA", "TIPO", "MATRICOLA", "ANNO", "TIPOLOGIA_IMPIANTO", 
					"SEDE_INST_DENOMINAZIONE", "SEDE_INST_INDIRIZZO", "SEDE_ADD_DENOMINAZIONE", 
					"SEDE_ADD_INDIRIZZO", "TIPO VERIFICA", "TIPO PROVA", "DATA_ULTIMA_VERIFICA", 
					"TECNICO_ULTIMA_VERIFICA", "DATA_SCADENZA", "TIPO APPARECCHIO"};

			writeCellsFromArray(headerTutti, sheetTutti, 0);

			//RIGA FILTRI
			String filtersRow = "";
			VerificaImpAction via = VerificaImpAction.instance();
			ImpiantoAction ia = ImpiantoAction.instance();
			
			if(Boolean.TRUE.equals(ia.getTemporary().get("includiScadute"))){
				filtersRow += "[Includi scadute] ";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if(ia.getTemporary().get("scadenzaFrom")!=null){
				filtersRow += "[Scadenza dal] " + sdf.format((Date)ia.getTemporary().get("scadenzaFrom")) + " ";
			}
			if(ia.getTemporary().get("scadenzaTo")!=null){
				filtersRow += "[Scadenza al] " + sdf.format((Date)ia.getTemporary().get("scadenzaTo")) + " ";
			}
			
			if(ia.getLike().get("sigla")!=null && !((String)ia.getLike().get("sigla")).isEmpty()){
				filtersRow += "[Sigla] " + (String)ia.getLike().get("sigla") + " ";
			}
			if(ia.getLike().get("matricola")!=null && !((String)ia.getLike().get("matricola")).isEmpty()){
				filtersRow += "[Matricola] " + (String)ia.getLike().get("matricola") + " ";
			}
			if(ia.getLike().get("anno")!=null && !((String)ia.getLike().get("anno")).isEmpty()){
				filtersRow += "[Anno] " + (String)ia.getLike().get("anno") + " ";
			}
			if(ia.getLike().get("sedeInstallazione.denominazione")!=null && !((String)ia.getLike().get("sedeInstallazione.denominazione")).isEmpty()){
				filtersRow += "[Sede di installazione] " + (String)ia.getLike().get("sedeInstallazione.denominazione") + " ";
			}
			if(ia.getLike().get("sedi.denominazioneUnitaLocale")!=null && !((String)ia.getLike().get("sedi.denominazioneUnitaLocale")).isEmpty()){
				filtersRow += "[Sede di addebito] " + (String)ia.getLike().get("sedi.denominazioneUnitaLocale") + " ";
			}
			if(ia.getLike().get("indirizzoSped.addr.str")!=null && !((String)ia.getLike().get("indirizzoSped.addr.str")).isEmpty()){
				filtersRow += "[Indirizzo di spedizione] " + (String)ia.getLike().get("indirizzoSped.addr.str") + " ";
			}
			if(ia.getEqual().get("sedeInstallazione.addr.code")!=null){
				filtersRow += "[Comune sede installazione] " + ((CodeValueCity)ia.getEqual().get("sedeInstallazione.addr.code")).getCurrentTranslation() + " ";
			}
			if(ia.getEqual().get("sedeInstallazione.addr.code")==null &&
					ia.getEqual().get("sedeInstallazione.addr.cty")!=null && !((String)ia.getEqual().get("sedeInstallazione.addr.cty")).isEmpty()){
				filtersRow += "[Comune sede installazione] " + (String)ia.getEqual().get("sedeInstallazione.addr.cty") + " ";
			}
			if(ia.getEqual().get("sedeInstallazione.addr.code")==null &&
					ia.getEqual().get("sedeInstallazione.addr.cpa")!=null && !((String)ia.getEqual().get("sedeInstallazione.addr.cpa")).isEmpty()){
				filtersRow += "[Provincia sede installazione] " + (String)ia.getEqual().get("sedeInstallazione.addr.cpa") + " ";
			}
			if(ia.getEqual().get("sedeInstallazione.addr.code")==null &&
					ia.getEqual().get("sedeInstallazione.addr.zip")!=null && !((String)ia.getEqual().get("sedeInstallazione.addr.zip")).isEmpty()){
				filtersRow += "[CAP sede installazione] " + (String)ia.getEqual().get("sedeInstallazione.addr.zip") + " ";
			}
			if(ia.getEqual().get("sedi.addr.code")!=null){
				filtersRow += "[Comune sede addebito] " + ((CodeValueCity)ia.getEqual().get("sedi.addr.code")).getCurrentTranslation() + " ";
			}
			if(ia.getEqual().get("sedi.addr.code")==null &&
					ia.getEqual().get("sedi.addr.cty")!=null && !((String)ia.getEqual().get("sedi.addr.cty")).isEmpty()){
				filtersRow += "[Comune sede addebito] " + (String)ia.getEqual().get("sedi.addr.cty") + " ";
			}
			if(ia.getEqual().get("sedi.addr.code")==null &&
					ia.getEqual().get("sedi.addr.cpa")!=null && !((String)ia.getEqual().get("sedi.addr.cpa")).isEmpty()){
				filtersRow += "[Provincia sede addebito] " + (String)ia.getEqual().get("sedi.addr.cpa") + " ";
			}
			if(ia.getEqual().get("sedi.addr.code")==null &&
					ia.getEqual().get("sedi.addr.zip")!=null && !((String)ia.getEqual().get("sedi.addr.zip")).isEmpty()){
				filtersRow += "[CAP sede addebito] " + (String)ia.getEqual().get("sedi.addr.zip") + " ";
			}

			String impTypeString = "";
			if(Boolean.TRUE.equals(ia.getTemporary().get("allTypes"))){
				impTypeString += "Tutti ";
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("01"))){
				impTypeString += "Apparecchi a pressione ";
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("02"))){
				impTypeString += "Impianti di riscaldamento ";
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("03"))){
				impTypeString += "Ascensori e montacarichi ";
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("04"))){
				impTypeString += "Apparecchi di sollevamento ";
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("05"))){
				impTypeString += "Impianti elettrici ";
			}
			filtersRow += "[Tipo Apparecchio/Impianto] " + impTypeString + " ";
			
			if(ia.getEqual().get("enteVerificatore")!=null){
				filtersRow += "[Ente verificatore] " + ((CodeValuePhi)ia.getEqual().get("enteVerificatore")).getCurrentTranslation() + " ";
			}

			if(Boolean.TRUE.equals(ia.getTemporary().get("04")) && !Boolean.TRUE.equals(ia.getTemporary().get("01")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("02")) && !Boolean.TRUE.equals(ia.getTemporary().get("03")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("05"))){
				filtersRow += "[Tipo apparecchio di sollevamento] ";
				if(ia.getTemporary().get("D")!=null){
					filtersRow += "D - Scale aeree ad incl. variabile ";
				}
				if(ia.getTemporary().get("E")!=null){
					filtersRow += "E - Ponti sviluppabili su carro ";
				}
				if(ia.getTemporary().get("F")!=null){
					filtersRow += "F - Ponti sospesi ";
				}
				if(ia.getTemporary().get("G")!=null){
					filtersRow += "G - Argani per ponti sospesi ";
				}
				if(ia.getTemporary().get("H")!=null){
					filtersRow += "H - Idroestrattori ";
				}
				if(ia.getTemporary().get("I")!=null){
					filtersRow += "I - Gru ";
				}
				if(ia.getTemporary().get("L")!=null){
					filtersRow += "L - Argani e paranchi ";
				}
			}

			if(Boolean.TRUE.equals(ia.getTemporary().get("05")) && !Boolean.TRUE.equals(ia.getTemporary().get("01")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("02")) && !Boolean.TRUE.equals(ia.getTemporary().get("03")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("04"))){
				filtersRow += "[Tipo impianto elettrico] ";
				if(ia.getTemporary().get("A")!=null){
					filtersRow += "A - Inst. di prot. scariche atm ";
				}
				if(ia.getTemporary().get("B")!=null){
					filtersRow += "B - Impianti di messa a terra ";
				}
				if(ia.getTemporary().get("C")!=null){
					filtersRow += "C - Ins. elett. In luoghi peric. ";
				}
			}

			if(Boolean.TRUE.equals(ia.getTemporary().get("allTypes"))){
				sheetTutti.addCell(new Label(0,1,filtersRow));
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("01"))){
				sheetPress.addCell(new Label(0,1,filtersRow));
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("02"))){
				sheetRisc.addCell(new Label(0,1,filtersRow));
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("03"))){
				sheetMonta.addCell(new Label(0,1,filtersRow));
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("04"))){
				sheetSoll.addCell(new Label(0,1,filtersRow));
			}
			if(Boolean.TRUE.equals(ia.getTemporary().get("05"))){
				sheetTerra.addCell(new Label(0,1,filtersRow));
			}

			//RIGHE IMPIANTI
			firstRowOfData = 2;
			firstRowOfDataPress = 2;
			firstRowOfDataRisc = 2;
			firstRowOfDataMonta = 2;
			firstRowOfDataSoll = 2;
			firstRowOfDataTerra = 2;

			List<Impianto> impInScadenza = (List<Impianto>)impList.getFullList();
			VerificaImpAction va = VerificaImpAction.instance();

			for(Impianto imp : impInScadenza){

				if(imp == null)
					continue;

				List<VerificaImp> verifiche=new ArrayList<VerificaImp>();

				if (imp instanceof ImpPress)
					verifiche = ((ImpPress)imp).getVerificaImp();
				else if (imp instanceof ImpRisc)
					verifiche = ((ImpRisc)imp).getVerificaImp();
				else if (imp instanceof ImpMonta)
					verifiche = ((ImpMonta)imp).getVerificaImp();
				else if (imp instanceof ImpSoll)
					verifiche = ((ImpSoll)imp).getVerificaImp();
				else if (imp instanceof ImpTerra)
					verifiche = ((ImpTerra)imp).getVerificaImp();

				VerificaImp verificaLast = null;
				if (verifiche!=null){

					for (VerificaImp vi : verifiche){
						if (vi!=null){
							if (vi.getData()!=null && vi.getStatusCode()!=null && !"new".equals(vi.getStatusCode().getCode())){
								//if (vi.getData()!=null){
								if ((verificaLast==null) || (verificaLast.getData().before(vi.getData())))
									verificaLast=vi;
							}
						}
					}
				}

				if(Boolean.TRUE.equals(ia.getTemporary().get("allTypes"))){
					writeSheetTutti(sheetTutti, ia, imp, verificaLast);
				}
				if(Boolean.TRUE.equals(ia.getTemporary().get("01"))){
					writeSheetPress(sheetPress, ia, imp, verificaLast);
				}
				if(Boolean.TRUE.equals(ia.getTemporary().get("02"))){
					writeSheetRisc(sheetRisc, ia, imp, verificaLast);
				}
				if(Boolean.TRUE.equals(ia.getTemporary().get("03"))){
					writeSheetMonta(sheetMonta, ia, imp, verificaLast);
				}
				if(Boolean.TRUE.equals(ia.getTemporary().get("04"))){
					writeSheetSoll(sheetSoll, ia, imp, verificaLast);
				}
				if(Boolean.TRUE.equals(ia.getTemporary().get("05"))){
					writeSheetTerra(sheetTerra, ia, imp, verificaLast);
				}

				firstColumnOfData++;

			}

			wb.write();
			wb.close();
			output.flush();
			output.close();
			vdoc.setContent(output.toByteArray());



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex) throws RowsExceededException, WriteException {
		writeCellsFromArray(rowCells, sheet, rowIndex, false);
	}

	private void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex, boolean hasIdData) throws RowsExceededException, WriteException {
		if (rowCells != null) {
			// loop on all row objects but id column
			for (int colIndex=0;(colIndex+(hasIdData ? 1 : 0))<rowCells.length;colIndex++) {
				Object cell = rowCells[colIndex+(hasIdData ? 1 : 0)];
				if (cell instanceof CodeValue){
					// for code values get translation if exists. If not use display name. Get CV code as extra data
					CodeValue cv = (CodeValue) cell;
					String label = cv.getDisplayName();
					String translation = cv.getTranslation(Locale.instance().getLanguage());
					if (translation != null)
						label = translation;
					sheet.addCell(new Label(colIndex,rowIndex,label + " ["+cv.getCode()+"]"));
				} else if (cell instanceof Date) {
					// for TimeStamps get date...
					Date date = (Date)cell;
					Date fixedDate = null;
					if (date != null) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(date);
						// ... and fix it considering current timezone
						fixedDate = new Date(date.getTime()+(cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)));
					}
					sheet.addCell(fixedDate != null ? new DateTime(colIndex,rowIndex,fixedDate) : new Label(colIndex,rowIndex,""));
				} else {
					// in other cases get string value or use empty string for null objects
					sheet.addCell(new Label(colIndex,rowIndex,cell != null ? cell.toString() : ""));
				}
			}
		}
	}

	private void writeSheetMonta(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {

		if(!(imp instanceof ImpMonta))
			return;

		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,imp.getSigla()));
		firstColumnOfData++;

		//TIPO
		String tipoImp = "";
		CodeValuePhi categoria = ((ImpMonta) imp).getCategoria();
		if(categoria!=null){
			tipoImp = categoria.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tipoImp));
		firstColumnOfData++;

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		if(verificaLast!=null){
			String tipoSopralluogo = "";
			CodeValuePhi tipoSopralluogoCv = verificaLast.getSopralluogo();
			if(Boolean.TRUE.equals(verificaLast.getSopralluogoBl()) && tipoSopralluogoCv!=null){
				tipoSopralluogo = tipoSopralluogoCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tipoSopralluogo));
		}
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = ia.nextVerifDate(imp, "1");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,scadenza));
		}
		firstColumnOfData++;

		//TIPO APPARECCHIO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataMonta,tipoImp));
		firstColumnOfData++;


		firstRowOfDataMonta++;

	}

	private void writeSheetPress(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {

		if(!(imp instanceof ImpPress))
			return;

		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,imp.getSigla()));
		firstColumnOfData++;

		//TIPO
		String tipoImp = "";
		CodeValuePhi tipoImpPress = ((ImpPress) imp).getTipoApparecchio();
		if(tipoImpPress!=null){
			tipoImp = tipoImpPress.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tipoImp));
		firstColumnOfData++;

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		if(verificaLast!=null){
			VerificaImpAction va = VerificaImpAction.instance();
			String tipoProva = va.getTipoProva(verificaLast);
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tipoProva));
		}
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = "";
			if(imp.getNextVerifDate1()!=null){
				scadenza += ia.nextVerifDate(imp, "1");
			}
			if(imp.getNextVerifDate2()!=null){
				if(imp.getNextVerifDate1()!=null)
					scadenza += ", ";

				scadenza += ia.nextVerifDate(imp, "2");
			}
			if(imp.getNextVerifDate3()!=null){
				if(imp.getNextVerifDate1()!=null || imp.getNextVerifDate2()!=null)
					scadenza += ", ";

				scadenza += ia.nextVerifDate(imp, "3");
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,scadenza));
		}
		firstColumnOfData++;

		ImpPress impPress = (ImpPress) imp;

		//TIPO APPARECCHIO
		String tipoApparecchio = "";
		CodeValuePhi tipoAppCv = impPress.getTipoApparecchio();
		if(tipoAppCv!=null){
			tipoApparecchio = tipoAppCv.getCurrentTranslation();
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,tipoApparecchio));
		}
		firstColumnOfData++;

		//CARATTERISTICHE SPECIALI
		String caratteristiche = "";
		CodeValuePhi caratteristicheCv = impPress.getCaratteristicheSpec();
		if(caratteristicheCv!=null){
			caratteristiche = caratteristicheCv.getCurrentTranslation();
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,caratteristiche));
		}
		firstColumnOfData++;

		//COMODANTE
		String comodante = "";
		CodeValuePhi comodanteCv = impPress.getComodante();
		if(comodanteCv!=null){
			comodante = comodanteCv.getCurrentTranslation();
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,comodante));
		}
		firstColumnOfData++;

		//COSTRUTTORE
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,impPress.getCostruttore()));
		firstColumnOfData++;

		//NUMERO FABBRICA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,impPress.getNumeroFabbrica()));
		firstColumnOfData++;

		//PRESSIONE BAR 1^
		if(impPress.getPressBar1()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,String.valueOf(impPress.getPressBar1()).replace(".", ",")));
		}
		firstColumnOfData++;

		//CAPACITÃ€ (LT)
		if(impPress.getCapacita()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,String.valueOf(impPress.getCapacita()).replace(".", ",")));
		}
		firstColumnOfData++;

		//SUPERFICIE MQ
		if(impPress.getSuperficie()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,String.valueOf(impPress.getSuperficie()).replace(".", ",")));
		}
		firstColumnOfData++;

		//PRODUCIBILITA T/H
		if(impPress.getProducibilita()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,String.valueOf(impPress.getProducibilita()).replace(".", ",")));
		}
		firstColumnOfData++;

		//ESONERO
		String esonero = "";
		CodeValuePhi esoneroCv = impPress.getEsonero();
		if(esoneroCv!=null){
			esonero = esoneroCv.getCurrentTranslation();
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,esonero));
		}
		firstColumnOfData++;

		//STATO IMPIANTO
		String statoImp = "";
		CodeValuePhi statoCv = impPress.getStatoImpianto();
		if(statoCv!=null){
			statoImp = statoCv.getCurrentTranslation();
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,statoImp));
		}
		firstColumnOfData++;

		//NOTE
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataPress,impPress.getNote()));
		firstColumnOfData++;

		firstRowOfDataPress++;

	}

	private void writeSheetRisc(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {

		if(!(imp instanceof ImpRisc))
			return;

		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,imp.getSigla()));
		firstColumnOfData++;

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		if(verificaLast!=null){
			VerificaImpAction va = VerificaImpAction.instance();
			String tipoProva = va.getTipoProva(verificaLast);
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,tipoProva));
		}
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = ia.nextVerifDate(imp, "1");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,scadenza));
		}
		firstColumnOfData++;

		//TIPO APPARECCHIO
		String destImpianto = "";
		CodeValuePhi destImpiantoCv = ((ImpRisc) imp).getDescrizImpianto();
		if(destImpiantoCv!=null){
			destImpianto = destImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataRisc,destImpianto));
		firstColumnOfData++;


		firstRowOfDataRisc++;

	}

	private void writeSheetSoll(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {

		if(!(imp instanceof ImpSoll))
			return;

		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,imp.getSigla()));
		firstColumnOfData++;

		//TIPO
		String tipoImp = "";
		CodeValuePhi tipoImpSoll = imp.getSubTypeSoll();
		if(tipoImpSoll!=null){
			tipoImp = tipoImpSoll.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,tipoImp));
		firstColumnOfData++;

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = ia.nextVerifDate(imp, "1");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,scadenza));
		}
		firstColumnOfData++;

		//TIPO APPARECCHIO
		String sottoTipoImp = "";
		CodeValuePhi sottoTipoCv = ((ImpSoll) imp).getSubType();
		if(sottoTipoCv!=null){
			sottoTipoImp = sottoTipoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataSoll,sottoTipoImp));
		firstColumnOfData++;


		firstRowOfDataSoll++;

	}

	private void writeSheetTerra(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {

		if(!(imp instanceof ImpTerra))
			return;

		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,imp.getSigla()));
		firstColumnOfData++;

		//TIPO
		String tipoImp = "";
		CodeValuePhi tipoImpTerra = imp.getSubTypeTerra();
		if(tipoImpTerra!=null){
			tipoImp = tipoImpTerra.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,tipoImp));
		firstColumnOfData++;

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = ia.nextVerifDate(imp, "1");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,scadenza));
		}
		firstColumnOfData++;

		//TIPO APPARECCHIO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfDataTerra,tipoImp));
		firstColumnOfData++;


		firstRowOfDataTerra++;

	}

	private void writeSheetTutti(WritableSheet sheet, ImpiantoAction ia,
			Impianto imp, VerificaImp verificaLast) throws WriteException, RowsExceededException {
		firstColumnOfData = 0;	

		//SIGLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfData,imp.getSigla()));
		firstColumnOfData++;

		//TIPO
		String tipoImp = "";
		if(imp instanceof ImpPress){
			CodeValuePhi tipoImpPress = ((ImpPress) imp).getTipoApparecchio();
			if(tipoImpPress!=null){
				tipoImp = tipoImpPress.getCurrentTranslation();
			}
		}else if(imp instanceof ImpMonta){
			CodeValuePhi categoria = ((ImpMonta) imp).getCategoria();
			if(categoria!=null){
				tipoImp = categoria.getCurrentTranslation();
			}
		}else if(imp instanceof ImpSoll){
			CodeValuePhi tipoImpSoll = imp.getSubTypeSoll();
			if(tipoImpSoll!=null){
				tipoImp = tipoImpSoll.getCurrentTranslation();
			}
		}else if(imp instanceof ImpTerra){
			CodeValuePhi tipoImpTerra = imp.getSubTypeTerra();
			if(tipoImpTerra!=null){
				tipoImp = tipoImpTerra.getCurrentTranslation();
			}
		}
		if(!Boolean.TRUE.equals(ia.getTemporary().get("02"))){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoImp));
			firstColumnOfData++;
		}

		//MATRICOLA
		sheet.addCell(new Label(firstColumnOfData,firstRowOfData,imp.getMatricola()));
		firstColumnOfData++;

		//ANNO
		sheet.addCell(new Label(firstColumnOfData,firstRowOfData,imp.getAnno()));
		firstColumnOfData++;

		//TIPOLOGIA_IMPIANTO
		String tipoImpianto = "";
		CodeValuePhi tipoImpiantoCv = imp.getCode();
		if(tipoImpiantoCv!=null){
			tipoImpianto = tipoImpiantoCv.getCurrentTranslation();
		}
		sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoImpianto));
		firstColumnOfData++;

		//SEDE_INST_DENOMINAZIONE
		SediInstallazione sedeInst = imp.getSedeInstallazione();
		if(sedeInst!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeInst.getDenominazione()));
		}
		firstColumnOfData++;

		//SEDE_INST_INDIRIZZO
		if(sedeInst!=null && sedeInst.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeInst.getAddr().toString()));
		}
		firstColumnOfData++;

		//SEDE_ADD_DENOMINAZIONE
		Sedi sedeAdd = imp.getSedi();
		if(sedeAdd!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAdd.getDenominazioneUnitaLocale()));
		}
		firstColumnOfData++;

		//SEDE_ADD_INDIRIZZO
		if(sedeAdd!=null && sedeAdd.getAddr()!=null){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAdd.getAddr().toString()));
		}
		firstColumnOfData++;

		//TIPO VERIFICA
		if(verificaLast!=null){
			String tipoVer = "";
			CodeValuePhi tipoVerCv = verificaLast.getTipo();
			if(tipoVerCv!=null){
				tipoVer = tipoVerCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoVer));
		}
		firstColumnOfData++;

		//TIPO PROVA
		if(verificaLast!=null){
			if(imp instanceof ImpPress){
				VerificaImpAction va = VerificaImpAction.instance();
				String tipoProva = va.getTipoProva(verificaLast);
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoProva));
			}else if(imp instanceof ImpMonta){
				String tipoSopralluogo = "";
				CodeValuePhi tipoSopralluogoCv = verificaLast.getSopralluogo();
				if(Boolean.TRUE.equals(verificaLast.getSopralluogoBl()) && tipoSopralluogoCv!=null){
					tipoSopralluogo = tipoSopralluogoCv.getCurrentTranslation();
				}
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoSopralluogo));
			}
		}
		firstColumnOfData++;

		//DATA_ULTIMA_VERIFICA
		if(verificaLast!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(verificaLast.getData())));
		}
		firstColumnOfData++;

		//TECNICO_ULTIMA_VERIFICA
		if(verificaLast!=null){
			String tecnicoName = "";
			Operatore operatore = verificaLast.getOperatore();
			if(operatore!=null && operatore.getName()!=null){
				tecnicoName = operatore.getName().toString();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoName));
		}
		firstColumnOfData++;

		//DATA_SCADENZA
		if(verificaLast!=null){
			String scadenza = "";
			if(imp instanceof ImpPress){
				if(imp.getNextVerifDate1()!=null){
					scadenza += ia.nextVerifDate(imp, "1");
				}
				if(imp.getNextVerifDate2()!=null){
					if(imp.getNextVerifDate1()!=null)
						scadenza += ", ";

					scadenza += ia.nextVerifDate(imp, "2");
				}
				if(imp.getNextVerifDate3()!=null){
					if(imp.getNextVerifDate1()!=null || imp.getNextVerifDate2()!=null)
						scadenza += ", ";

					scadenza += ia.nextVerifDate(imp, "3");
				}
			}else{
				scadenza = ia.nextVerifDate(imp, "1");
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,scadenza));
		}
		firstColumnOfData++;

		//TIPO APPARECCHIO
		if(imp instanceof ImpPress){
			String tipoApparecchio = "";
			CodeValuePhi tipoImpPress = ((ImpPress) imp).getTipoApparecchio();
			if(tipoImpPress!=null){
				tipoApparecchio = tipoImpPress.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoApparecchio));
		}else if(imp instanceof ImpRisc){
			String destImpianto = "";
			CodeValuePhi destImpiantoCv = ((ImpRisc) imp).getDescrizImpianto();
			if(destImpiantoCv!=null){
				destImpianto = destImpiantoCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,destImpianto));
		}else if(imp instanceof ImpMonta){
			String cat = "";
			CodeValuePhi categoria = ((ImpMonta) imp).getCategoria();
			if(categoria!=null){
				cat = categoria.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,cat));
		}else if(imp instanceof ImpSoll){
			String sottoTipoImp = "";
			CodeValuePhi sottoTipoCv = ((ImpSoll) imp).getSubType();
			if(sottoTipoCv!=null){
				sottoTipoImp = sottoTipoCv.getCurrentTranslation();
			}
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sottoTipoImp));
		}else if(imp instanceof ImpTerra){
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoImp));
		}
		firstColumnOfData++;

		firstRowOfData++;

	}


}