package com.phi.entities.actions;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
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

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;

import com.phi.cs.datamodel.PagedDataModel;
import com.phi.entities.baseEntity.Fattura;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.VerificheDocument;
import com.phi.entities.baseEntity.VerificheTecDocument;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;

@BypassInterceptors
@Name("VerificheDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class VerificheDocumentAction extends BaseAction<VerificheDocument, Long> {

	private static final long serialVersionUID = 70273076L;
	private static final Logger log = Logger.getLogger(VerificaImpAction.class); 

	private int firstColumnOfData = 0; //first column of excel datagrid
	private int firstRowOfData = 2; //first row of excel datagrid

	public static VerificheDocumentAction instance() {
		return (VerificheDocumentAction) Component.getInstance(VerificheDocumentAction.class, ScopeType.CONVERSATION);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void generateFile(VerificheDocument vdoc, PagedDataModel<VerificaImp> verifList){
		ByteArrayOutputStream output = null;

		try {
			if (verifList==null || verifList.getFullList()==null)
				return;

			output = new ByteArrayOutputStream();
			WritableWorkbook wb = Workbook.createWorkbook(output);
			WritableSheet sheet = wb.createSheet("First Sheet", 0);

			//RIGA INTESTAZIONI
			String[] header = {"eff_verifica", "anno Matricola", "cod.att. Matricola", "numero Matricola", 
					"provincia Matricola", "esito_verifica", "tipo_verifica", "sospensione", 
					"data_rilascio", "data_pvp", "dl_rag_sociale", "dl_indirizzo", 
					"dl_comune", "dl_cod_fisc", "dl_part_iva", "attr_gruppo", 
					"tipo_attr", "tipo_attr_GVR", "tipo_verifica_GVR", "Sogg_Messa_Servizio_GVR", 
					"ID Specifica", "modello_attr", "num_fabbrica", "marcatura_CE", 
					"num_id_ON", "sv_rag_sociale", "sv_nome_tecn", "sv_cognome_tecn", 
					"sv_CF_tecn", "tariffa_app", "tariffa_regolare", "contributo_sogg_tit", 
					"attr_indirizzo", "attr_comune", "Cap di ubicazione dell'apparecchio", 
					"Provincia di ubicazione dell'apparecchio", "Regione di ubicazione dell'apparecchio", 
					"fattura", "verbale", "cs_ragionesociale"};

			writeCellsFromArray(header, sheet, 0);

			//RIGA FILTRI
			String filtersRow = "";
			VerificaImpAction via = VerificaImpAction.instance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if(via.getGreaterEqual().get("data")!=null){
				filtersRow += "[Data dal] " + sdf.format((Date)via.getGreaterEqual().get("data")) + " ";
			}

			if(via.getLessEqual().get("data")!=null){
				filtersRow += "[Data al] " + sdf.format((Date)via.getLessEqual().get("data")) + " ";
			}

			if(via.getTemporary().get("selectedARPAV")!=null){
				filtersRow += "[Sede Arpav] " + verifList.getList().get(0).getServiceDeliveryLocation().getName().getGiv() + " ";
			}

			String impTypeString = "";
			ImpiantoAction ia = ImpiantoAction.instance();
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

			if(Boolean.TRUE.equals(ia.getTemporary().get("01")) && !Boolean.TRUE.equals(ia.getTemporary().get("02")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("03")) && !Boolean.TRUE.equals(ia.getTemporary().get("04")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("05"))){
				filtersRow += "[Tipo di prova] ";
				if(via.getTemporary().get("idraulica")!=null){
					filtersRow += "Integrità ";
				}
				if(via.getTemporary().get("interna")!=null){
					filtersRow += "Interna ";
				}
				if(via.getTemporary().get("esercizio")!=null){
					filtersRow += "Esercizio ";
				}
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

			if(via.getTemporary().get("denominazioneSI")!=null && !((String)via.getTemporary().get("denominazioneSI")).isEmpty()){
				filtersRow += "[Sede di istallazione] " + (String)via.getTemporary().get("denominazioneSI") + " ";
			}
			if(via.getTemporary().get("denominazioneIS")!=null && !((String)via.getTemporary().get("denominazioneIS")).isEmpty()){
				filtersRow += "[Descrizione spedizione] " + (String)via.getTemporary().get("denominazioneIS") + " ";
			}
			if(via.getTemporary().get("denominazioneSA")!=null && !((String)via.getTemporary().get("denominazioneSA")).isEmpty()){
				filtersRow += "[Sede di addebito] " + (String)via.getTemporary().get("denominazioneSA") + " ";
			}
			if(via.getLike().get("operatore.name.fam")!=null && !((String)via.getLike().get("operatore.name.fam")).isEmpty()){
				filtersRow += "[Tecnico: Cognome] " + (String)via.getLike().get("operatore.name.fam") + " ";
			}
			if(via.getLike().get("operatore.name.giv")!=null && !((String)via.getLike().get("operatore.name.giv")).isEmpty()){
				filtersRow += "[Tecnico: Nome] " + (String)via.getLike().get("operatore.name.giv") + " ";
			}
			if(via.getEqual().get("statusCode")!=null){
				filtersRow += "[Stato verifica] " + ((CodeValuePhi)via.getEqual().get("statusCode")).getCurrentTranslation() + " ";
			}
			if(via.getEqual().get("esito")!=null){
				filtersRow += "[Esito verifica] " + ((CodeValuePhi)via.getEqual().get("esito")).getCurrentTranslation() + " ";
			}
			if(via.getEqual().get("fattura.anno")!=null && !((String)via.getEqual().get("fattura.anno")).isEmpty()){
				filtersRow += "[Anno Doc] " + (String)via.getEqual().get("fattura.anno") + " ";
			}
			if(via.getEqual().get("numeroDoc")!=null && !((String)via.getEqual().get("numeroDoc")).isEmpty()){
				filtersRow += "[Numero Doc] " + (String)via.getEqual().get("numeroDoc") + " ";
			}
			if(via.getLike().get("impSearchCollector.sigla")!=null && !((String)via.getLike().get("impSearchCollector.sigla")).isEmpty()){
				filtersRow += "[Sigla] " + (String)via.getLike().get("impSearchCollector.sigla") + " ";
			}
			if(via.getLike().get("impSearchCollector.matricola")!=null && !((String)via.getLike().get("impSearchCollector.matricola")).isEmpty()){
				filtersRow += "[Matricola] " + (String)via.getLike().get("impSearchCollector.matricola") + " ";
			}
			if(via.getLike().get("impSearchCollector.anno")!=null && !((String)via.getLike().get("impSearchCollector.anno")).isEmpty()){
				filtersRow += "[Anno] " + (String)via.getLike().get("impSearchCollector.anno") + " ";
			}
			if(via.getLike().get("impSearchCollector.numeroFabbrica")!=null && !((String)via.getLike().get("impSearchCollector.numeroFabbrica")).isEmpty()){
				filtersRow += "[Numero fabbrica] " + (String)via.getLike().get("impSearchCollector.numeroFabbrica") + " ";
			}

			sheet.addCell(new Label(firstColumnOfData,firstRowOfData-1,filtersRow));
			//RIGHE VERIFICHE
			List<VerificaImp> verifiche = (List<VerificaImp>)verifList.getFullList();

			for(VerificaImp ver : verifiche){
				CodeValuePhi verifStatus = ver.getStatusCode();
				if(verifStatus == null)
					continue;

				if(!"phidic.arpav.ver.stato.verified".equals(verifStatus.getOid()) && !"phidic.arpav.ver.stato.completed".equals(verifStatus.getOid()))
					continue;

				firstColumnOfData = 0;	

				//eff_verifica
				firstColumnOfData++;

				ImpSearchCollector verifImpSearchColl = ver.getImpSearchCollector();

				//anno Matricola
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getAnno()));
				firstColumnOfData++;

				CodeValuePhi impTypeCv = verifImpSearchColl.getCode();
				String impType = impTypeCv.getOid();

				//cod.att. Matricola
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getTipoApparecchio().getCode()+" - "+impCopy.getTipoApparecchio().getCurrentTranslation()));
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getDescrizImpianto().getCode()+" - "+impCopy.getDescrizImpianto().getCurrentTranslation()));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getSubTypeSoll().getCode()+" - "+verifImpSearchColl.getSubTypeSoll().getCurrentTranslation()));
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getSubTypeTerra().getCode()+" - "+verifImpSearchColl.getSubTypeTerra().getCurrentTranslation()));
				}
				firstColumnOfData++;

				//numero Matricola
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getMatricola()));
				firstColumnOfData++;

				//provincia Matricola
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getSigla()));
				firstColumnOfData++;

				//esito_verifica
				CodeValuePhi esitoCv = ver.getEsito();
				if(esitoCv != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,esitoCv.getCurrentTranslation()));
				}
				firstColumnOfData++;

				//tipo_verifica
				CodeValuePhi tipoVerCv = ver.getTipo();
				if(tipoVerCv != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoVerCv.getCurrentTranslation()));
				}
				firstColumnOfData++;

				//sospensione
				firstColumnOfData++;

				//data_rilascio
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getData())));
				firstColumnOfData++;

				//data_pvp
				if(ver.getNextVerifDate1() != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate1())));
				}else if(ver.getNextVerifDate2() != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate2())));
				}else if(ver.getNextVerifDate3() != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate3())));
				}
				firstColumnOfData++;

				Sedi sedeAddebitoCopy = null;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){
					ImpTerra impCopy = ver.getImpTerraCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}

				//dl_rag_sociale
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getDenominazioneUnitaLocale()));
				firstColumnOfData++;

				AD addrAddebito = sedeAddebitoCopy.getAddr();
				//dl_indirizzo
				if(addrAddebito != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrAddebito.getStr()+" "+addrAddebito.getBnr()));
				}
				firstColumnOfData++;

				//dl_comune
				if(addrAddebito != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrAddebito.getCty()));
				}
				firstColumnOfData++;

				PersoneGiuridiche ditta = sedeAddebitoCopy.getPersonaGiuridica();
				//dl_cod_fisc
				if(ditta != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getPersonaGiuridica().getCodiceFiscale()));
				}
				firstColumnOfData++;

				//dl_part_iva
				if(ditta != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getPersonaGiuridica().getPatritaIva()));
				}
				firstColumnOfData++;

				//attr_gruppo
				firstColumnOfData++;
				//tipo_attr
				firstColumnOfData++;
				//tipo_attr_GVR
				firstColumnOfData++;

				//tipo_verifica_GVR
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					String tipoProva = "";
					if(Boolean.TRUE.equals(ver.getIdraulica())){
						tipoProva += "Integrità ";
					}
					if(Boolean.TRUE.equals(ver.getInterna())){
						tipoProva += "Interna ";
					}
					if(Boolean.TRUE.equals(ver.getEsercizio())){
						tipoProva += "Esercizio ";
					}
				}
				firstColumnOfData++;

				//Sogg_Messa_Servizio_GVR
				firstColumnOfData++;
				//ID Specifica
				firstColumnOfData++;

				//modello_attr
				if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getTipoFabb()));
				}
				firstColumnOfData++;

				//num_fabbrica
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}				
				firstColumnOfData++;

				//marcatura_CE
				if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,(Boolean.TRUE.equals(impCopy.getMarcaturaCe())?"SI":"NO")));
				}
				firstColumnOfData++;

				//num_id_ON
				firstColumnOfData++;
				//sv_rag_sociale
				firstColumnOfData++;

				EN tecnicoName = null;
				String[] tecnicoNameParts = null;
				if(ver.getOperatore()!=null && ver.getOperatore().getName()!=null){
					tecnicoName = ver.getOperatore().getName();
					tecnicoNameParts = tecnicoName.getFam().split("[_\\s]");
				}

				//sv_nome_tecn
				if(tecnicoName!=null){
					if((tecnicoName.getGiv()==null || (tecnicoName.getGiv()!=null && tecnicoName.getGiv().trim().isEmpty())) && 
							tecnicoNameParts!=null && tecnicoNameParts.length>=3){
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoNameParts[2]));
					}else{
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoName.getGiv()));
					}
				}
				firstColumnOfData++;

				//sv_cognome_tecn
				if(tecnicoName!=null){
					if((tecnicoName.getGiv()==null || (tecnicoName.getGiv()!=null && tecnicoName.getGiv().trim().isEmpty())) && 
							tecnicoNameParts!=null && tecnicoNameParts.length>=2){
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoNameParts[1]));
					}else{
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoName.getFam()));
					}
				}
				firstColumnOfData++;

				//sv_CF_tecn
				firstColumnOfData++;

				//tariffa_app
				if(ver.getImporto()!=null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,String.valueOf(ver.getImporto())));
				}
				firstColumnOfData++;

				//tariffa_regolare
				firstColumnOfData++;
				//contributo_sogg_tit
				firstColumnOfData++;

				SediInstallazione sedeinstallazioneCopy = null;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){
					ImpTerra impCopy = ver.getImpTerraCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}


				//attr_indirizzo

				AD addrInstallazione = sedeinstallazioneCopy.getAddr();
				//attr_indirizzo
				if(addrInstallazione != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getStr()+" "+addrInstallazione.getBnr()));
				}
				firstColumnOfData++;

				//attr_comune
				if(addrInstallazione != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getCty()));
				}
				firstColumnOfData++;

				//Cap di ubicazione dell' apparecchio
				if(addrInstallazione != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getZip()));
				}
				firstColumnOfData++;

				//Provincia di ubicazione dell' apparecchio
				if(addrInstallazione != null){
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getCpa()));
				}
				firstColumnOfData++;

				//Regione di ubicazione dell' apparecchio
				sheet.addCell(new Label(firstColumnOfData,firstRowOfData,"VENETO"));
				firstColumnOfData++;

				//fattura
				firstColumnOfData++;
				//verbale
				firstColumnOfData++;

				//cs_ragionesociale    
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}
				firstColumnOfData++;

				firstRowOfData++;

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

	public String generateFileName(){
		String fileName = "Estrazione_DATI_INAIL_";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		fileName += sdf.format(new Date());

		VerificaImpAction via = VerificaImpAction.instance();

		if(via.getGreaterEqual().get("data")!=null){
			fileName += "_DA_" + sdf.format((Date)via.getGreaterEqual().get("data")) + " ";
		}
		if(via.getLessEqual().get("data")!=null){
			fileName += "_A_" + sdf.format((Date)via.getLessEqual().get("data")) + " ";
		}

		fileName += ".xls";

		return fileName;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public void generateMultiFile(VerificheDocument vdoc, VerificheTecDocument vtecdoc, PagedDataModel<VerificaImp> verifList){
		ByteArrayOutputStream output = null;
		ByteArrayOutputStream outputtec = null;
			
//			SimpleDateFormat sdfLog = new SimpleDateFormat("HH:mm:ss.SS");
//			log.info("Start export time: " + sdfLog.format(new Date()));

		try {
			if (verifList==null || verifList.getFullList()==null)
				return;

			output = new ByteArrayOutputStream();
			outputtec = new ByteArrayOutputStream();
			WritableWorkbook wb = Workbook.createWorkbook(output);
			WritableWorkbook wbtec = Workbook.createWorkbook(outputtec);
			WritableSheet sheet = wb.createSheet("First Sheet", 0);
			WritableSheet sheettec = wbtec.createSheet("First Sheet", 0);

//			log.info("Create worksheets time: " + sdfLog.format(new Date()));

			//RIGA INTESTAZIONI
			String[] header = {"eff_verifica", "anno Matricola", "cod.att. Matricola", "numero Matricola", 
					"provincia Matricola", "esito_verifica", "tipo_verifica", "sospensione", 
					"data_rilascio", "data_pvp", "dl_rag_sociale", "dl_indirizzo", 
					"dl_comune", "dl_cod_fisc", "dl_part_iva", "attr_gruppo", 
					"tipo_attr", "tipo_attr_GVR", "tipo_verifica_GVR", "Sogg_Messa_Servizio_GVR", 
					"ID Specifica", "modello_attr", "num_fabbrica", "marcatura_CE", 
					"num_id_ON", "sv_rag_sociale", "sv_nome_tecn", "sv_cognome_tecn", 
					"sv_CF_tecn", "tariffa_app", "tariffa_regolare", "contributo_sogg_tit", 
					"attr_indirizzo", "attr_comune", "Cap di ubicazione dell'apparecchio", 
					"Provincia di ubicazione dell'apparecchio", "Regione di ubicazione dell'apparecchio", 
					"fattura", "verbale", "cs_ragionesociale"};
			String[] headertec = {"TECNICO", "DATA VERIFICA", "SEDE DI ADDEBITO","INDIRIZZO INSTALLAZIONE","COMUNE INSTALLAZIONE","TIPO APPARECCHIO/IMPIANTO",
					"DATI IMPIANTO","SOTTOTIPO","TIPOLOGIA VERIFICA","TIPO PROVA","TIPO SOPRALLUOGO","ESENTE FATTURA",
					"ESITO VERIFICA","NOTE INSERITE NELLA SCHEDA VERIFICA","IMPORTO VERIFICA","GRUPPO","N. DOC.","DATA ELABORAZIONE","CODICE CONTABILITÀ"};

			writeCellsFromArray(header, sheet, 0);
			writeCellsFromArray(headertec, sheettec, 0);

			//RIGA FILTRI
			String filtersRow = "";
			VerificaImpAction via = VerificaImpAction.instance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if(via.getGreaterEqual().get("data")!=null){
				filtersRow += "[Data dal] " + sdf.format((Date)via.getGreaterEqual().get("data")) + " ";
			}

			if(via.getLessEqual().get("data")!=null){
				filtersRow += "[Data al] " + sdf.format((Date)via.getLessEqual().get("data")) + " ";
			}

			if(via.getTemporary().get("selectedARPAV")!=null){
				filtersRow += "[Sede Arpav] " + verifList.getList().get(0).getServiceDeliveryLocation().getName().getGiv() + " ";
			}

			String impTypeString = "";
			ImpiantoAction ia = ImpiantoAction.instance();
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

			if(Boolean.TRUE.equals(ia.getTemporary().get("01")) && !Boolean.TRUE.equals(ia.getTemporary().get("02")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("03")) && !Boolean.TRUE.equals(ia.getTemporary().get("04")) &&
					!Boolean.TRUE.equals(ia.getTemporary().get("05"))){
				filtersRow += "[Tipo di prova] ";
				if(via.getTemporary().get("idraulica")!=null){
					filtersRow += "Integrità ";
				}
				if(via.getTemporary().get("interna")!=null){
					filtersRow += "Interna ";
				}
				if(via.getTemporary().get("esercizio")!=null){
					filtersRow += "Esercizio ";
				}
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

			if(via.getTemporary().get("denominazioneSI")!=null && !((String)via.getTemporary().get("denominazioneSI")).isEmpty()){
				filtersRow += "[Sede di istallazione] " + (String)via.getTemporary().get("denominazioneSI") + " ";
			}
			if(via.getTemporary().get("denominazioneIS")!=null && !((String)via.getTemporary().get("denominazioneIS")).isEmpty()){
				filtersRow += "[Descrizione spedizione] " + (String)via.getTemporary().get("denominazioneIS") + " ";
			}
			if(via.getTemporary().get("denominazioneSA")!=null && !((String)via.getTemporary().get("denominazioneSA")).isEmpty()){
				filtersRow += "[Sede di addebito] " + (String)via.getTemporary().get("denominazioneSA") + " ";
			}
			if(via.getLike().get("operatore.name.fam")!=null && !((String)via.getLike().get("operatore.name.fam")).isEmpty()){
				filtersRow += "[Tecnico: Cognome] " + (String)via.getLike().get("operatore.name.fam") + " ";
			}
			if(via.getLike().get("operatore.name.giv")!=null && !((String)via.getLike().get("operatore.name.giv")).isEmpty()){
				filtersRow += "[Tecnico: Nome] " + (String)via.getLike().get("operatore.name.giv") + " ";
			}
			if(via.getEqual().get("statusCode")!=null){
				filtersRow += "[Stato verifica] " + ((CodeValuePhi)via.getEqual().get("statusCode")).getCurrentTranslation() + " ";
			}
			if(via.getEqual().get("esito")!=null){
				filtersRow += "[Esito verifica] " + ((CodeValuePhi)via.getEqual().get("esito")).getCurrentTranslation() + " ";
			}
			if(via.getEqual().get("fattura.anno")!=null && !((String)via.getEqual().get("fattura.anno")).isEmpty()){
				filtersRow += "[Anno Doc] " + (String)via.getEqual().get("fattura.anno") + " ";
			}
			if(via.getEqual().get("numeroDoc")!=null && !((String)via.getEqual().get("numeroDoc")).isEmpty()){
				filtersRow += "[Numero Doc] " + (String)via.getEqual().get("numeroDoc") + " ";
			}
			if(via.getLike().get("impSearchCollector.sigla")!=null && !((String)via.getLike().get("impSearchCollector.sigla")).isEmpty()){
				filtersRow += "[Sigla] " + (String)via.getLike().get("impSearchCollector.sigla") + " ";
			}
			if(via.getLike().get("impSearchCollector.matricola")!=null && !((String)via.getLike().get("impSearchCollector.matricola")).isEmpty()){
				filtersRow += "[Matricola] " + (String)via.getLike().get("impSearchCollector.matricola") + " ";
			}
			if(via.getLike().get("impSearchCollector.anno")!=null && !((String)via.getLike().get("impSearchCollector.anno")).isEmpty()){
				filtersRow += "[Anno] " + (String)via.getLike().get("impSearchCollector.anno") + " ";
			}
			if(via.getLike().get("impSearchCollector.numeroFabbrica")!=null && !((String)via.getLike().get("impSearchCollector.numeroFabbrica")).isEmpty()){
				filtersRow += "[Numero fabbrica] " + (String)via.getLike().get("impSearchCollector.numeroFabbrica") + " ";
			}

			firstColumnOfData = 0;
			firstRowOfData = 2;
			sheet.addCell(new Label(firstColumnOfData,firstRowOfData-1,filtersRow));
			sheettec.addCell(new Label(0,1,filtersRow));

//			log.info("Fill header time: " + sdfLog.format(new Date()));

			//RIGHE VERIFICHE
			List<VerificaImp> verifiche = (List<VerificaImp>)verifList.getFullList();

			int firstColumnOfDataTec = 0;
			int firstRowOfDataTec = 2;
			
			for(VerificaImp ver : verifiche){
				CodeValuePhi verifStatus = ver.getStatusCode();
				if(verifStatus == null)
					continue;

				boolean isInail = "phidic.arpav.ver.stato.verified".equals(verifStatus.getOid()) || "phidic.arpav.ver.stato.completed".equals(verifStatus.getOid());
				
//				if(!"phidic.arpav.ver.stato.verified".equals(verifStatus.getOid()) && !"phidic.arpav.ver.stato.completed".equals(verifStatus.getOid()))
//					continue;

				firstColumnOfData = 0;	

				//eff_verifica
				firstColumnOfData++;

				ImpSearchCollector verifImpSearchColl = ver.getImpSearchCollector();

				//anno Matricola
				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getAnno()));
				firstColumnOfData++;

				CodeValuePhi impTypeCv = verifImpSearchColl.getCode();
				String impType = impTypeCv.getOid();

				firstColumnOfDataTec = 5;
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,impTypeCv.getCurrentTranslation()));
				
				//cod.att. Matricola
				firstColumnOfDataTec = 7;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					
					String cellContent = impCopy.getTipoApparecchio()==null?"":impCopy.getTipoApparecchio().getCode()+" - "+impCopy.getTipoApparecchio().getCurrentTranslation();
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,cellContent));

					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,cellContent));
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();

					String cellContent = impCopy.getDescrizImpianto()==null?"":impCopy.getDescrizImpianto().getCode()+" - "+impCopy.getDescrizImpianto().getCurrentTranslation();
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,cellContent));

					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,cellContent));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){

					String cellContent = verifImpSearchColl.getSubTypeSoll()==null?"":verifImpSearchColl.getSubTypeSoll().getCode()+" - "+verifImpSearchColl.getSubTypeSoll().getCurrentTranslation();
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,cellContent));

					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,cellContent));
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){

					String cellContent = verifImpSearchColl.getSubTypeTerra()==null?"":verifImpSearchColl.getSubTypeTerra().getCode()+" - "+verifImpSearchColl.getSubTypeTerra().getCurrentTranslation();
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,cellContent));

					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,cellContent));
				}
				firstColumnOfData++;

				//numero Matricola
				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getMatricola()));
				firstColumnOfData++;

				//provincia Matricola
				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,verifImpSearchColl.getSigla()));
				firstColumnOfData++;

				firstColumnOfDataTec = 6;
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,verifImpSearchColl.getSigla() + "-" +
						verifImpSearchColl.getMatricola() + "/" + verifImpSearchColl.getAnno()));

				//esito_verifica
				firstColumnOfDataTec = 12;
				CodeValuePhi esitoCv = ver.getEsito();
				if(esitoCv != null){
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,esitoCv.getCurrentTranslation()));
					
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,esitoCv.getCurrentTranslation()));
				}
				firstColumnOfData++;

				//note verifica
				firstColumnOfDataTec = 13;
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,ver.getNote()));
				
				//tipo_verifica
				firstColumnOfDataTec = 8;
				CodeValuePhi tipoVerCv = ver.getTipo();
				if(tipoVerCv != null){
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoVerCv.getCurrentTranslation()));

					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,tipoVerCv.getCurrentTranslation()));
				}
				firstColumnOfData++;

				//tipo sopralluogo (solo estrazione tecnico)
				firstColumnOfDataTec = 10;
				if(tipoVerCv!=null && "05".equals(tipoVerCv.getCode()) && ver.getSopralluogo()!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,ver.getSopralluogo().getCurrentTranslation()));
				}
				
				//sospensione
				firstColumnOfData++;

				//data_rilascio
				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getData())));
				firstColumnOfData++;

				firstColumnOfDataTec=1;
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,sdf.format(ver.getData())));
				
				//data_pvp
				if(ver.getNextVerifDate1() != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate1())));
				}else if(ver.getNextVerifDate2() != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate2())));
				}else if(ver.getNextVerifDate3() != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sdf.format(ver.getNextVerifDate3())));
				}
				firstColumnOfData++;

				Sedi sedeAddebitoCopy = null;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){
					ImpTerra impCopy = ver.getImpTerraCpy();
					sedeAddebitoCopy = impCopy.getSedi();
				}

				//dl_rag_sociale

				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getDenominazioneUnitaLocale()));
				firstColumnOfData++;

				firstColumnOfDataTec = 2;
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,sedeAddebitoCopy.getDenominazioneUnitaLocale()));

				AD addrAddebito = sedeAddebitoCopy.getAddr();
				//dl_indirizzo
				if(addrAddebito != null){
					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrAddebito.getStr()+" "+addrAddebito.getBnr()));
				}
				firstColumnOfData++;

				//dl_comune
				if(addrAddebito != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrAddebito.getCty()));
				}
				firstColumnOfData++;

				PersoneGiuridiche ditta = sedeAddebitoCopy.getPersonaGiuridica();
				//dl_cod_fisc
				if(ditta != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getPersonaGiuridica().getCodiceFiscale()));
				}
				firstColumnOfData++;

				//dl_part_iva
				if(ditta != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sedeAddebitoCopy.getPersonaGiuridica().getPatritaIva()));
				}
				firstColumnOfData++;

				//attr_gruppo
				firstColumnOfData++;
				//tipo_attr
				firstColumnOfData++;
				//tipo_attr_GVR
				firstColumnOfData++;

				//tipo_verifica_GVR
				firstColumnOfDataTec = 9;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					String tipoProva = "";
					if(Boolean.TRUE.equals(ver.getIdraulica())){
						tipoProva += "Integrità ";
					}
					if(Boolean.TRUE.equals(ver.getInterna())){
						tipoProva += "Interna ";
					}
					if(Boolean.TRUE.equals(ver.getEsercizio())){
						tipoProva += "Esercizio ";
					}
					
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,tipoProva));
				}
				firstColumnOfData++;

				//Sogg_Messa_Servizio_GVR
				firstColumnOfData++;
				//ID Specifica
				firstColumnOfData++;

				//modello_attr
				if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getTipoFabb()));
				}
				firstColumnOfData++;

				//num_fabbrica
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getNumeroFabbrica()));
				}				
				firstColumnOfData++;

				//marcatura_CE
				if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,(Boolean.TRUE.equals(impCopy.getMarcaturaCe())?"SI":"NO")));
				}
				firstColumnOfData++;

				//num_id_ON
				firstColumnOfData++;
				//sv_rag_sociale
				firstColumnOfData++;

				EN tecnicoName = null;
				String[] tecnicoNameParts = null;
				if(ver.getOperatore()!=null && ver.getOperatore().getName()!=null){
					tecnicoName = ver.getOperatore().getName();
					tecnicoNameParts = tecnicoName.getFam().split("[_\\s]");
				}

				//sv_nome_tecn
				if(tecnicoName!=null){
					if((tecnicoName.getGiv()==null || (tecnicoName.getGiv()!=null && tecnicoName.getGiv().trim().isEmpty())) && 
							tecnicoNameParts!=null && tecnicoNameParts.length>=3){

						if(isInail)
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoNameParts[2]));
					}else{

						if(isInail)
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoName.getGiv()));
					}
				}
				firstColumnOfData++;

				//sv_cognome_tecn
				firstColumnOfDataTec = 0;
				if(tecnicoName!=null){
					if((tecnicoName.getGiv()==null || (tecnicoName.getGiv()!=null && tecnicoName.getGiv().trim().isEmpty())) && 
							tecnicoNameParts!=null && tecnicoNameParts.length>=2){

						if(isInail)
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoNameParts[1]));

						sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,tecnicoNameParts[2] + " " +tecnicoNameParts[1]));
					}else{

						if(isInail)
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoName.getFam()));

						sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, tecnicoName.getGiv() + " " + tecnicoName.getFam()));
					}
				}
				firstColumnOfData++;

				//sv_CF_tecn
				firstColumnOfData++;

				//tariffa_app
				firstColumnOfDataTec = 14;
				if(ver.getImporto()!=null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,String.valueOf(ver.getImporto())));
					
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,String.valueOf(ver.getImporto())));
				}
				firstColumnOfData++;

				//tariffa_regolare
				firstColumnOfData++;
				//contributo_sogg_tit
				firstColumnOfData++;

				SediInstallazione sedeinstallazioneCopy = null;
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}else if("phidic.arpav.imp.imptype.05".equals(impType)){
					ImpTerra impCopy = ver.getImpTerraCpy();
					sedeinstallazioneCopy = impCopy.getSedeInstallazione();
				}


				//attr_indirizzo

				AD addrInstallazione = sedeinstallazioneCopy.getAddr();
				//attr_indirizzo
				if(addrInstallazione != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getStr()+" "+addrInstallazione.getBnr()));
				}
				firstColumnOfData++;

				//attr_comune
				if(addrInstallazione != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getCty()));
				}
				firstColumnOfData++;
				
				//Cap di ubicazione dell' apparecchio
				if(addrInstallazione != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getZip()));
				}
				firstColumnOfData++;

				//Provincia di ubicazione dell' apparecchio
				if(addrInstallazione != null){

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,addrInstallazione.getCpa()));
				}
				firstColumnOfData++;

				firstColumnOfDataTec = 3;
				String columnInstallazione = sedeinstallazioneCopy.getDenominazione() + " " +
						(addrInstallazione == null ? "" : addrInstallazione.getStr()) + " " +
						(addrInstallazione == null ? "" : addrInstallazione.getBnr());
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,columnInstallazione));

				firstColumnOfDataTec = 4;
				String columnComuneInst = (addrInstallazione == null ? "" : addrInstallazione.getCty()) + " (" +
						(addrInstallazione == null ? "" : addrInstallazione.getCpa()) + ")";
				sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec,columnComuneInst));

				//Regione di ubicazione dell' apparecchio

				if(isInail)
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,"VENETO"));
				firstColumnOfData++;

				//fattura
				firstColumnOfData++;
				//verbale
				firstColumnOfData++;

				//cs_ragionesociale    
				if("phidic.arpav.imp.imptype.01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}else if("phidic.arpav.imp.imptype.03".equals(impType)){
					ImpMonta impCopy = ver.getImpMontaCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}else if("phidic.arpav.imp.imptype.04".equals(impType)){
					ImpSoll impCopy = ver.getImpSollCpy();

					if(isInail)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impCopy.getCostruttore()));
				}
				firstColumnOfData++;
				
				//esente fattura
				firstColumnOfDataTec = 11;
				if(ver.getEsente()!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, ver.getEsente().getCurrentTranslation()));
				}

				//gruppo (solo estrazione tecnico)
				firstColumnOfDataTec = 15;
				Fattura f = ver.getFattura();
				if(f!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, String.valueOf(f.getGruppo())));
				}
				
				//numero doc (solo estrazione tecnico)
				firstColumnOfDataTec = 16;
				if(f!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, String.valueOf(ver.getNumeroDoc())));
				}

				//data elaborazione (solo estrazione tecnico)
				firstColumnOfDataTec = 17;
				if(f!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, sdf.format(f.getCreationDate())));
				}

				//codice contabilità (solo estrazione tecnico)
				firstColumnOfDataTec = 18;
				if(sedeAddebitoCopy!=null){
					sheettec.addCell(new Label(firstColumnOfDataTec,firstRowOfDataTec, sedeAddebitoCopy.getCodContabilita()));
				}

				if(isInail)
					firstRowOfData++;

				firstRowOfDataTec++;
				
//				log.info("Fill line time: " + sdfLog.format(new Date()));

			}

			wb.write();
			wb.close();
			output.flush();
			output.close();
			vdoc.setContent(output.toByteArray());

			wbtec.write();
			wbtec.close();
			outputtec.flush();
			outputtec.close();
			vtecdoc.setContent(outputtec.toByteArray());

//			log.info("Complete time: " + sdfLog.format(new Date()));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
		public void generateMultiFileFake(VerificheDocument vdoc, VerificheTecDocument vtecdoc, PagedDataModel<VerificaImp> verifList){
			ByteArrayOutputStream output = null;
			ByteArrayOutputStream outputtec = null;
	
			try {
				if (verifList==null || verifList.getFullList()==null)
					return;
	
				output = new ByteArrayOutputStream();
				outputtec = new ByteArrayOutputStream();
				WritableWorkbook wb = Workbook.createWorkbook(output);
				WritableWorkbook wbtec = Workbook.createWorkbook(outputtec);
				WritableSheet sheet = wb.createSheet("First Sheet", 0);
				WritableSheet sheettec = wbtec.createSheet("First Sheet", 0);
	
				//RIGA INTESTAZIONI
				String[] header = {"eff_verifica", "anno Matricola", "cod.att. Matricola", "numero Matricola", 
						"provincia Matricola", "esito_verifica", "tipo_verifica", "sospensione", 
						"data_rilascio", "data_pvp", "dl_rag_sociale", "dl_indirizzo", 
						"dl_comune", "dl_cod_fisc", "dl_part_iva", "attr_gruppo", 
						"tipo_attr", "tipo_attr_GVR", "tipo_verifica_GVR", "Sogg_Messa_Servizio_GVR", 
						"ID Specifica", "modello_attr", "num_fabbrica", "marcatura_CE", 
						"num_id_ON", "sv_rag_sociale", "sv_nome_tecn", "sv_cognome_tecn", 
						"sv_CF_tecn", "tariffa_app", "tariffa_regolare", "contributo_sogg_tit", 
						"attr_indirizzo", "attr_comune", "Cap di ubicazione dell'apparecchio", 
						"Provincia di ubicazione dell'apparecchio", "Regione di ubicazione dell'apparecchio", 
						"fattura", "verbale", "cs_ragionesociale"};
				String[] headertec = {"TECNICO", "DATA VERIFICA", "SEDE DI ADDEBITO","INDIRIZZO INSTALLAZIONE","COMUNE INSTALLAZIONE","TIPO APPARECCHIO/IMPIANTO",
						"DATI IMPIANTO","SOTTOTIPO","TIPOLOGIA VERIFICA","TIPO PROVA","TIPO SOPRALLUOGO","ESENTE FATTURA",
						"ESITO VERIFICA","NOTE INSERITE NELLA SCHEDA VERIFICA","IMPORTO VERIFICA","GRUPPO","N. DOC.","DATA ELABORAZIONE","CODICE CONTABILITÀ"};
	
				writeCellsFromArray(header, sheet, 0);
				writeCellsFromArray(headertec, sheettec, 0);

				for(int i=1; i<=13000; i++){
					writeCellsFromArray(header, sheet, i);
				}
				
				wb.write();
				wb.close();
				output.flush();
				output.close();
				vdoc.setContent(output.toByteArray());
	
				wbtec.write();
				wbtec.close();
				outputtec.flush();
				outputtec.close();
				vtecdoc.setContent(outputtec.toByteArray());
	
	
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}