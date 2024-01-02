package com.phi.entities.actions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;

import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.paging.LazyList;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.ImpiantiDocument;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("ImpiantiDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class ImpiantiDocumentAction extends BaseAction<ImpiantiDocument, Long> {

	private static final long serialVersionUID = 2012479433L;
	private static final Logger log = Logger.getLogger(ImpiantoAction.class);

	private int firstColumnOfData = 0; //first column of excel datagrid
	private int firstRowOfData = 1; //first row of excel datagrid

	public static ImpiantiDocumentAction instance() {
		return (ImpiantiDocumentAction) Component.getInstance(ImpiantiDocumentAction.class, ScopeType.CONVERSATION);
	}

	public void generateFile(ImpiantiDocument idoc, PagedDataModel<Impianto> impList){
		try {
			if (impList==null || impList.getFullList()==null)
				return;

			Date currDate = new Date();
			List<Date> nextVerifDateList = new ArrayList<Date>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VerificaImpAction vAction = VerificaImpAction.instance();
			List<VerificaImp> lastVerList = new ArrayList<VerificaImp>();
			VerificaImp lastVer = null;


			String impType = "";
			List<Map> impianti = (List<Map>)impList.getFullList();

			ImpiantoAction ia = ImpiantoAction.instance();
			HashMap<String, Object> temp = ia.getTemporary();

			Boolean singleType = temp.get("singleType")==null?false:(Boolean)ia.getTemporary().get("singleType");
			String stringContent = "SIGLA;TIPO;MATRICOLA;ANNO;TIPOLOGIA_IMPIANTO;SEDE_INST_DENOMINAZIONE;SEDE_INST_INDIRIZZO;SEDE_ADD_DENOMINAZIONE;SEDE_ADD_INDIRIZZO;DATA_ULTIMA_VERIFICA;TECNICO_ULTIMA_VERIFICA;DATA_SCADENZA";

			//Se nel processo anagraficaImpianti è stata effettuata una ricerca multipla
			if (!singleType)
				stringContent += "\n";
			else {

				//Apparecchi a pressione
				if (temp.get("01")==null?false:(Boolean)temp.get("01")){
					impType = "01";
					stringContent += ";TIPO APPARECCHIO;CARATTERISTICHE SPECIALI;COMODANTE;COSTRUTTORE;NUMERO FABBRICA;PRESSIONE BAR 1^;CAPACITÀ (LT);SUPERFICIE MQ;PRODUCIBILITA T/H;STATO IMPIANTO;NOTE\n";

					//Impianti di riscaldamento
				} else if (temp.get("02")==null?false:(Boolean)temp.get("02")){
					impType = "02";
					stringContent += ";IMPIANTO;DESTINAZIONE LOCALI;POT. GLOBALE KWATT;DESTINAZIONE IMPIANTO;MANUTENTORE;STATO IMPIANTO;NOTE\n";

					//Ascensori e montacarichi
				} else if (temp.get("03")==null?false:(Boolean)temp.get("03")){
					impType = "03";
					stringContent += ";MATRICOLA COMUNE;STATO IMPIANTO;ANNO COSTRUZIONE;DESTINAZIONE;CATEGORIA;NUMERO FABBRICA;PORTATA (KG);COSTRUTTORE;MANUTENTORE;DISTANZA;FERMATE;UTENTE LETTERA;AMMINISTRATORE;NOTE\n";

					//Apparecchi di sollevamento
				} else if (temp.get("04")==null?false:(Boolean)temp.get("04")){
					impType = "04";
					stringContent += ";TIPO APPARECCHIO DI SOLLEVAMENTO;SOTTOTIPO;ANNO COSTRUZIONE;COSTRUTTORE;COSTRUTTORE RADIOCOMANDO;TARGA;PORTATA (KG);NUMERO FABBRICA;TIPO FABBRICA;NUMERO RADIOCOMANDO;TELAIO;STATO IMPIANTO;NOTE\n";

					//Impianti elettrici
				} else if (temp.get("05")==null?false:(Boolean)temp.get("05")){
					impType = "05";
					stringContent += ";TIPO IMPIANTO ELETTRICO;STRUTTURE AUTOPROTETTE;SOTTOTIPO B;POTENZA (KWATT);STATO IMPIANTO;NOTE\n";
				}			
			}

			for(@SuppressWarnings("rawtypes") Map map : impianti){
				Impianto imp = null;
				String type =null;

				Long internalId = (Long)map.get("internalId");

				CodeValuePhi code = (CodeValuePhi)map.get("code");
				if (code!=null)
					type = code.getCode();

				//PER ULTIMA VERIFICA
				vAction.cleanRestrictions();
				vAction.isNull.put("statusCode", false);
				vAction.notEqual.put("statusCode.code", "new");
				vAction.notEqual.put("pre", true);
				vAction.orderBy.put("data", "descending");

				if (type!=null){
					if("01".equals(type)){
						imp = ca.get(ImpPress.class, internalId);
						vAction.equal.put("impPress.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("02".equals(type)){
						imp = ca.get(ImpRisc.class, internalId);
						vAction.equal.put("impRisc.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("03".equals(type)){
						imp = ca.get(ImpMonta.class, internalId);
						vAction.equal.put("impMonta.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("04".equals(type)){
						imp = ca.get(ImpSoll.class, internalId);
						vAction.equal.put("impSoll.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("05".equals(type)){
						imp = ca.get(ImpTerra.class, internalId);
						vAction.equal.put("impTerra.internalId", internalId); //PER ULTIMA VERIFICA
					}
				}

				if(imp != null) {
					//SIGLA
					stringContent += (imp.getSigla()!=null ? this.clean(imp.getSigla()) : "") + ";";

					//TIPO
					if(imp.getSubTypeSoll()!=null)
						stringContent += this.clean(imp.getSubTypeSoll().getCurrentTranslation()) + ";";
					else if(imp.getSubTypeTerra()!=null)
						stringContent += this.clean(imp.getSubTypeTerra().getCurrentTranslation()) + ";";
					else
						stringContent += ";";

					//MATRICOLA
					stringContent += (imp.getMatricola()!=null ? this.clean(imp.getMatricola()) : "") + ";";

					//ANNO
					stringContent += (imp.getAnno()!=null ? this.clean(imp.getAnno()) : "") + ";";

					//TIPOLOGIA_IMPIANTO
					stringContent += (imp.getCode()!=null ? this.clean(imp.getCode().getCurrentTranslation()) : "") + ";";

					//SEDE_INST_DENOMINAZIONE
					SediInstallazione si = imp.getSedeInstallazione();
					stringContent += (si!=null ? this.clean(si.getDenominazione()) : "") + ";";

					//SEDE_INST_INDIRIZZO
					AD addrSI = null;
					if(si!=null){
						addrSI = si.getAddr();
					}
					stringContent += (addrSI!=null ? this.clean(addrSI.toString()) : "") + ";";

					//SEDE_ADD_DENOMINAZIONE
					Sedi se = imp.getSedi();
					stringContent += (se!=null ? this.clean(se.getDenominazioneUnitaLocale()) : "") + ";";

					//SEDE_ADD_INDIRIZZO
					AD addrSA = null;
					if(se!=null)
						addrSA = se.getAddr();

					stringContent += (addrSA!=null ? this.clean(addrSA.toString()) : "") + ";";

					//ULTIMA VERIFICA
					if(!Boolean.TRUE.equals((Boolean)temp.get("noVerifiche"))){
						lastVerList = vAction.select();
					}
					if(lastVerList!=null && !lastVerList.isEmpty()) {
						lastVer = lastVerList.get(0);
					}else {
						lastVer = null;
					}

					if(lastVer!=null){
						//DATA_ULTIMA_VERIFICA
						stringContent += (lastVer.getData()!=null ? this.clean(sdf.format(lastVer.getData())) : "") + ";";

						//TECNICO_ULTIMA_VERIFICA
						stringContent += (lastVer.getOperatore()!=null ? this.clean(lastVer.getOperatore().getName().toString()) : "") + ";";

					}else {
						stringContent += ";";
						stringContent += ";";
					}

					//DATA_SCADENZA
					nextVerifDateList.clear();
					if(imp.getNextVerifDate1()!=null)
						nextVerifDateList.add(imp.getNextVerifDate1());

					if(imp.getNextVerifDate2()!=null)
						nextVerifDateList.add(imp.getNextVerifDate2());

					if(imp.getNextVerifDate3()!=null)
						nextVerifDateList.add(imp.getNextVerifDate3());

					if(nextVerifDateList!=null && !nextVerifDateList.isEmpty()){
						//DATA_ULTIMA_VERIFICA
						Collections.sort(nextVerifDateList);
						/*Date minDate = null;
						for(Date d : nextVerifDateList){
							//se la data è minore di oggi, prendo la massima minore di oggi
							if(d.before(currDate)){
								minDate = d;
							}else if(minDate!=null){
								break;
							}else{
								minDate = d;
								break;
							}
						}*/
						Date minDate = Collections.min(nextVerifDateList);
						stringContent += (minDate!=null ? this.clean(sdf.format(minDate)) : "");

					} else
						stringContent += "";

					/* Gestione campi specifici */
					if (!singleType)
						stringContent += "\n";
					else {
						stringContent += ";";

						//Apparecchi a pressione
						if("01".equals(impType)){
							ImpPress ip = (ImpPress)imp;

							//TIPO APPARECCHIO
							stringContent += (ip.getTipoApparecchio()!=null ? this.clean(ip.getTipoApparecchio().getCurrentTranslation()) : "") + ";";

							//CARATTERISTICHE SPECIALI
							stringContent += (ip.getCaratteristicheSpec()!=null ? this.clean(ip.getCaratteristicheSpec().getCurrentTranslation()) : "") + ";";

							//COMODANTE
							stringContent += (ip.getComodante()!=null ? this.clean(ip.getComodante().getCurrentTranslation()) : "") + ";";

							//COSTRUTTORE
							stringContent += this.clean(ip.getCostruttore()) + ";";

							//NUMERO FABBRICA
							stringContent += this.clean(ip.getNumeroFabbrica()) + ";";

							//PRESSIONE BAR 1^
							stringContent += (ip.getPressBar1()!=null ? this.clean(ip.getPressBar1().toString()) : "") + ";";

							//CAPACITÀ (LT)
							stringContent += (ip.getCapacita()!=null ? this.clean(ip.getCapacita().toString()) : "") + ";";


							//SUPERFICIE MQ
							stringContent += (ip.getSuperficie()!=null ? this.clean(ip.getSuperficie().toString()) : "") + ";";

							//PRODUCIBILITA T/H
							stringContent += (ip.getProducibilita()!=null ? this.clean(ip.getProducibilita().toString()) : "") + ";";

							//STATO IMPIANTO
							stringContent += (ip.getStatoImpianto()!=null ? this.clean(ip.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(ip.getNote()) + "\n";

							//Impianti di riscaldamento		
						} else if("02".equals(impType)){
							ImpRisc ir = (ImpRisc)imp;

							//IMPIANTO
							stringContent += (ir.getImpianto()!=null ? this.clean(ir.getImpianto().getCurrentTranslation()) : "") + ";";

							//DESTINAZIONE LOCALI
							stringContent += (ir.getDescrizLocali()!=null ? this.clean(ir.getDescrizLocali().getCurrentTranslation()) : "") + ";";

							//POT. GLOBALE KWATT
							stringContent += (ir.getPotGlob()!=null ? this.clean(ir.getPotGlob().toString()) : "") + ";";

							//DESTINAZIONE IMPIANTO
							stringContent += (ir.getDescrizImpianto()!=null ? this.clean(ir.getDescrizImpianto().getCurrentTranslation()) : "") + ";";

							//MANUTENTORE
							stringContent += (ir.getManutentore()!=null ? this.clean(ir.getManutentore().getCurrentTranslation()) : "") + ";";

							//STATO IMPIANTO
							stringContent += (ir.getStatoImpianto()!=null ? this.clean(ir.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(ir.getNote()) + "\n";

							//Ascensori e montacarichi
						} else if("03".equals(impType)){
							ImpMonta im = (ImpMonta)imp;

							//MATRICOLA COMUNE
							stringContent += this.clean(im.getMatrcomune()) + ";";

							//STATO IMPIANTO
							stringContent += (im.getStatoImpianto()!=null ? this.clean(im.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//ANNO COSTRUZIONE
							stringContent += (im.getCostruzione()!=null ? this.clean(im.getCostruzione().toString()) : "") + ";";


							//DESTINAZIONE
							stringContent += (im.getDestinazione()!=null ? this.clean(im.getDestinazione().getCurrentTranslation()) : "") + ";";

							//CATEGORIA
							stringContent += (im.getCategoria()!=null ? this.clean(im.getCategoria().getCurrentTranslation()) : "") + ";";

							//NUMERO FABBRICA
							stringContent += this.clean(im.getNumeroFabbrica()) + ";";

							//PORTATA (KG)
							stringContent += (im.getPortata()!=null ? this.clean(im.getPortata().toString()) : "") + ";";


							//COSTRUTTORE
							stringContent += this.clean(im.getCostruttore()) + ";";

							//MANUTENTORE
							stringContent += (im.getManutentore()!=null ? this.clean(im.getManutentore().getCurrentTranslation()) : "") + ";";

							//DISTANZA
							stringContent += (im.getDistanza()!=null ? this.clean(im.getDistanza().getCurrentTranslation()) : "") + ";";

							//FERMATE
							stringContent += (im.getFermate()!=null ? this.clean(im.getFermate().toString()) : "") + ";";


							//UTENTE LETTERA
							stringContent += (im.getUtenteLettera()!=null ? this.clean(im.getUtenteLettera().getCurrentTranslation()) : "") + ";";

							//AMMINISTRATORE
							stringContent += (im.getAmministratore()!=null ? this.clean(im.getAmministratore().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(im.getNote()) + "\n";

							//Apparecchi di sollevamento	
						} else if("04".equals(impType)){
							ImpSoll is = (ImpSoll)imp;

							//TIPO APPARECCHIO DI SOLLEVAMENTO
							stringContent += (is.getSubTypeSoll()!=null ? this.clean(is.getSubTypeSoll().getCurrentTranslation()) : "") + ";";

							//SOTTOTIPO
							stringContent += (is.getSubType()!=null ? this.clean(is.getSubType().getCurrentTranslation()) : "") + ";";

							//ANNO COSTRUZIONE
							stringContent += (is.getCostruzione()!=null ? this.clean(is.getCostruzione().toString()) : "") + ";";

							//COSTRUTTORE
							stringContent += this.clean(is.getCostruttore()) + ";";

							//COSTRUTTORE RADIOCOMANDO
							stringContent += this.clean(is.getCostrRadioc()) + ";";

							//TARGA
							stringContent += this.clean(is.getTarga()) + ";";

							//PORTATA (KG)
							stringContent += (is.getPortata()!=null ? this.clean(is.getPortata().toString()) : "") + ";";


							//NUMERO FABBRICA
							stringContent += this.clean(is.getNumeroFabbrica()) + ";";

							//TIPO FABBRICA
							stringContent += this.clean(is.getTipoFabb()) + ";";

							//NUMERO RADIOCOMANDO
							stringContent += this.clean(is.getNumRadioc()) + ";";

							//TELAIO
							stringContent += this.clean(is.getTelaio()) + ";";

							//STATO IMPIANTO
							stringContent += (is.getStatoImpianto()!=null ? this.clean(is.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(is.getNote()) + "\n";

							//Impianti elettrici	
						} else if("05".equals(impType)){
							ImpTerra it = (ImpTerra)imp;

							//TIPO IMPIANTO ELETTRICO
							stringContent += (it.getSubTypeTerra()!=null ? this.clean(it.getSubTypeTerra().getCurrentTranslation()) : "") + ";";

							//STRUTTURE AUTOPROTETTE
							stringContent += (it.getTipologia()!=null ? ("48".equals(it.getTipologia().getCode())?"Si":"No") : "") + ";";

							//SOTTOTIPO B
							stringContent += (it.getSubTypeB()!=null ? this.clean(it.getSubTypeB().getCurrentTranslation()) : "") + ";";

							//POTENZA (KWATT)
							stringContent += (it.getPot()!=null ? this.clean(it.getPot().toString()) : "") + ";";


							//STATO IMPIANTO
							stringContent += (it.getStatoImpianto()!=null ? this.clean(it.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(it.getNote()) + "\n";
						}
					}
				}
			}

			idoc.setContent(stringContent.getBytes());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public String clean(String toClean){
		try{
			if (toClean==null)
				return "";

			toClean = toClean.replaceAll(";", " - ");
			toClean = toClean.replaceAll("\r\n|\r|\n", " ");

			return toClean;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void generateFileFromPaging(ImpiantiDocument idoc, PagedDataModel<Impianto> impList){
		try {
			if (impList==null || impList.getFullList()==null)
				return;

			Date currDate = new Date();
			List<Date> nextVerifDateList = new ArrayList<Date>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VerificaImpAction vAction = VerificaImpAction.instance();
			List<VerificaImp> lastVerList = new ArrayList<VerificaImp>();
			VerificaImp lastVer = null;


			String impType = "";
			LazyList impianti = (LazyList)impList.getList();

			ImpiantoAction ia = ImpiantoAction.instance();
			HashMap<String, Object> temp = ia.getTemporary();

			Boolean singleType = temp.get("singleType")==null?false:(Boolean)ia.getTemporary().get("singleType");
			String stringContent = "SIGLA;TIPO;MATRICOLA;ANNO;TIPOLOGIA_IMPIANTO;SEDE_INST_DENOMINAZIONE;SEDE_INST_INDIRIZZO;SEDE_ADD_DENOMINAZIONE;SEDE_ADD_INDIRIZZO;DATA_ULTIMA_VERIFICA;TECNICO_ULTIMA_VERIFICA;DATA_SCADENZA";

			//Se nel processo anagraficaImpianti è stata effettuata una ricerca multipla
			if (!singleType)
				stringContent += "\n";
			else {

				//Apparecchi a pressione
				if (temp.get("01")==null?false:(Boolean)temp.get("01")){
					impType = "01";
					stringContent += ";TIPO APPARECCHIO;CARATTERISTICHE SPECIALI;COMODANTE;COSTRUTTORE;NUMERO FABBRICA;PRESSIONE BAR 1^;CAPACITÀ (LT);SUPERFICIE MQ;PRODUCIBILITA T/H;STATO IMPIANTO;NOTE\n";

					//Impianti di riscaldamento
				} else if (temp.get("02")==null?false:(Boolean)temp.get("02")){
					impType = "02";
					stringContent += ";IMPIANTO;DESTINAZIONE LOCALI;POT. GLOBALE KWATT;DESTINAZIONE IMPIANTO;MANUTENTORE;STATO IMPIANTO;NOTE\n";

					//Ascensori e montacarichi
				} else if (temp.get("03")==null?false:(Boolean)temp.get("03")){
					impType = "03";
					stringContent += ";MATRICOLA COMUNE;STATO IMPIANTO;ANNO COSTRUZIONE;DESTINAZIONE;CATEGORIA;NUMERO FABBRICA;PORTATA (KG);COSTRUTTORE;MANUTENTORE;DISTANZA;FERMATE;UTENTE LETTERA;AMMINISTRATORE;NOTE\n";

					//Apparecchi di sollevamento
				} else if (temp.get("04")==null?false:(Boolean)temp.get("04")){
					impType = "04";
					stringContent += ";TIPO APPARECCHIO DI SOLLEVAMENTO;SOTTOTIPO;ANNO COSTRUZIONE;COSTRUTTORE;COSTRUTTORE RADIOCOMANDO;TARGA;PORTATA (KG);NUMERO FABBRICA;TIPO FABBRICA;NUMERO RADIOCOMANDO;TELAIO;STATO IMPIANTO;NOTE\n";

					//Impianti elettrici
				} else if (temp.get("05")==null?false:(Boolean)temp.get("05")){
					impType = "05";
					stringContent += ";TIPO IMPIANTO ELETTRICO;STRUTTURE AUTOPROTETTE;SOTTOTIPO B;POTENZA (KWATT);STATO IMPIANTO;NOTE\n";
				}			
			}

			//			int impIndex = 0;
			//			int currentIndex = impList.getCurrentPage();
			//			impList.first();

			for(Object impiantoRow : impianti){
				Map map = (Map)impiantoRow;

				//				try {
				//					map = (Map) impList.get(impIndex);
				//				} catch (IndexOutOfBoundsException iex1) {
				//					
				//					try {
				//						impList.next();
				//						map = (Map) impList.get(impIndex);
				//					} catch (IndexOutOfBoundsException iex2) {
				//						break;
				//					}
				//				}


				Impianto imp = null;
				String type =null;

				Long internalId = (Long)map.get("internalId");

				CodeValuePhi code = (CodeValuePhi)map.get("code");
				if (code!=null)
					type = code.getCode();

				//PER ULTIMA VERIFICA
				vAction.cleanRestrictions();
				vAction.isNull.put("statusCode", false);
				vAction.notEqual.put("statusCode.code", "new");
				vAction.notEqual.put("pre", true);
				vAction.orderBy.put("data", "descending");

				if (type!=null){
					if("01".equals(type)){
						imp = ca.get(ImpPress.class, internalId);
						vAction.equal.put("impPress.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("02".equals(type)){
						imp = ca.get(ImpRisc.class, internalId);
						vAction.equal.put("impRisc.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("03".equals(type)){
						imp = ca.get(ImpMonta.class, internalId);
						vAction.equal.put("impMonta.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("04".equals(type)){
						imp = ca.get(ImpSoll.class, internalId);
						vAction.equal.put("impSoll.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("05".equals(type)){
						imp = ca.get(ImpTerra.class, internalId);
						vAction.equal.put("impTerra.internalId", internalId); //PER ULTIMA VERIFICA
					}
				}

				if(imp != null) {
					//SIGLA
					stringContent += (imp.getSigla()!=null ? this.clean(imp.getSigla()) : "") + ";";

					//TIPO
					if(imp.getSubTypeSoll()!=null)
						stringContent += this.clean(imp.getSubTypeSoll().getCurrentTranslation()) + ";";
					else if(imp.getSubTypeTerra()!=null)
						stringContent += this.clean(imp.getSubTypeTerra().getCurrentTranslation()) + ";";
					else
						stringContent += ";";

					//MATRICOLA
					stringContent += (imp.getMatricola()!=null ? this.clean(imp.getMatricola()) : "") + ";";

					//ANNO
					stringContent += (imp.getAnno()!=null ? this.clean(imp.getAnno()) : "") + ";";

					//TIPOLOGIA_IMPIANTO
					stringContent += (imp.getCode()!=null ? this.clean(imp.getCode().getCurrentTranslation()) : "") + ";";

					//SEDE_INST_DENOMINAZIONE
					SediInstallazione si = imp.getSedeInstallazione();
					stringContent += (si!=null ? this.clean(si.getDenominazione()) : "") + ";";

					//SEDE_INST_INDIRIZZO
					AD addrSI = null;
					if(si!=null){
						addrSI = si.getAddr();
					}
					stringContent += (addrSI!=null ? this.clean(addrSI.toString()) : "") + ";";

					//SEDE_ADD_DENOMINAZIONE
					Sedi se = imp.getSedi();
					stringContent += (se!=null ? this.clean(se.getDenominazioneUnitaLocale()) : "") + ";";

					//SEDE_ADD_INDIRIZZO
					AD addrSA = null;
					if(se!=null)
						addrSA = se.getAddr();

					stringContent += (addrSA!=null ? this.clean(addrSA.toString()) : "") + ";";

					//ULTIMA VERIFICA
					lastVerList = vAction.select();
					if(lastVerList!=null && !lastVerList.isEmpty()) {
						lastVer = lastVerList.get(0);
					}else {
						lastVer = null;
					}

					if(lastVer!=null){
						//DATA_ULTIMA_VERIFICA
						stringContent += (lastVer.getData()!=null ? this.clean(sdf.format(lastVer.getData())) : "") + ";";

						//TECNICO_ULTIMA_VERIFICA
						stringContent += (lastVer.getOperatore()!=null ? this.clean(lastVer.getOperatore().getName().toString()) : "") + ";";

					}else {
						stringContent += ";";
						stringContent += ";";
					}

					//DATA_SCADENZA
					nextVerifDateList.clear();
					if(imp.getNextVerifDate1()!=null)
						nextVerifDateList.add(imp.getNextVerifDate1());

					if(imp.getNextVerifDate2()!=null)
						nextVerifDateList.add(imp.getNextVerifDate2());

					if(imp.getNextVerifDate3()!=null)
						nextVerifDateList.add(imp.getNextVerifDate3());

					if(nextVerifDateList!=null && !nextVerifDateList.isEmpty()){
						//DATA_ULTIMA_VERIFICA
						Collections.sort(nextVerifDateList);
						/*Date minDate = null;
					for(Date d : nextVerifDateList){
						//se la data è minore di oggi, prendo la massima minore di oggi
						if(d.before(currDate)){
							minDate = d;
						}else if(minDate!=null){
							break;
						}else{
							minDate = d;
							break;
						}
					}*/
						Date minDate = Collections.min(nextVerifDateList);
						stringContent += (minDate!=null ? this.clean(sdf.format(minDate)) : "");

					} else
						stringContent += "";

					/* Gestione campi specifici */
					if (!singleType)
						stringContent += "\n";
					else {
						stringContent += ";";

						//Apparecchi a pressione
						if("01".equals(impType)){
							ImpPress ip = (ImpPress)imp;

							//TIPO APPARECCHIO
							stringContent += (ip.getTipoApparecchio()!=null ? this.clean(ip.getTipoApparecchio().getCurrentTranslation()) : "") + ";";

							//CARATTERISTICHE SPECIALI
							stringContent += (ip.getCaratteristicheSpec()!=null ? this.clean(ip.getCaratteristicheSpec().getCurrentTranslation()) : "") + ";";

							//COMODANTE
							stringContent += (ip.getComodante()!=null ? this.clean(ip.getComodante().getCurrentTranslation()) : "") + ";";

							//COSTRUTTORE
							stringContent += this.clean(ip.getCostruttore()) + ";";

							//NUMERO FABBRICA
							stringContent += this.clean(ip.getNumeroFabbrica()) + ";";

							//PRESSIONE BAR 1^
							stringContent += (ip.getPressBar1()!=null ? this.clean(ip.getPressBar1().toString()) : "") + ";";

							//CAPACITÀ (LT)
							stringContent += (ip.getCapacita()!=null ? this.clean(ip.getCapacita().toString()) : "") + ";";


							//SUPERFICIE MQ
							stringContent += (ip.getSuperficie()!=null ? this.clean(ip.getSuperficie().toString()) : "") + ";";

							//PRODUCIBILITA T/H
							stringContent += (ip.getProducibilita()!=null ? this.clean(ip.getProducibilita().toString()) : "") + ";";

							//STATO IMPIANTO
							stringContent += (ip.getStatoImpianto()!=null ? this.clean(ip.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(ip.getNote()) + "\n";

							//Impianti di riscaldamento		
						} else if("02".equals(impType)){
							ImpRisc ir = (ImpRisc)imp;

							//IMPIANTO
							stringContent += (ir.getImpianto()!=null ? this.clean(ir.getImpianto().getCurrentTranslation()) : "") + ";";

							//DESTINAZIONE LOCALI
							stringContent += (ir.getDescrizLocali()!=null ? this.clean(ir.getDescrizLocali().getCurrentTranslation()) : "") + ";";

							//POT. GLOBALE KWATT
							stringContent += (ir.getPotGlob()!=null ? this.clean(ir.getPotGlob().toString()) : "") + ";";

							//DESTINAZIONE IMPIANTO
							stringContent += (ir.getDescrizImpianto()!=null ? this.clean(ir.getDescrizImpianto().getCurrentTranslation()) : "") + ";";

							//MANUTENTORE
							stringContent += (ir.getManutentore()!=null ? this.clean(ir.getManutentore().getCurrentTranslation()) : "") + ";";

							//STATO IMPIANTO
							stringContent += (ir.getStatoImpianto()!=null ? this.clean(ir.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(ir.getNote()) + "\n";

							//Ascensori e montacarichi
						} else if("03".equals(impType)){
							ImpMonta im = (ImpMonta)imp;

							//MATRICOLA COMUNE
							stringContent += this.clean(im.getMatrcomune()) + ";";

							//STATO IMPIANTO
							stringContent += (im.getStatoImpianto()!=null ? this.clean(im.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//ANNO COSTRUZIONE
							stringContent += (im.getCostruzione()!=null ? this.clean(im.getCostruzione().toString()) : "") + ";";


							//DESTINAZIONE
							stringContent += (im.getDestinazione()!=null ? this.clean(im.getDestinazione().getCurrentTranslation()) : "") + ";";

							//CATEGORIA
							stringContent += (im.getCategoria()!=null ? this.clean(im.getCategoria().getCurrentTranslation()) : "") + ";";

							//NUMERO FABBRICA
							stringContent += this.clean(im.getNumeroFabbrica()) + ";";

							//PORTATA (KG)
							stringContent += (im.getPortata()!=null ? this.clean(im.getPortata().toString()) : "") + ";";


							//COSTRUTTORE
							stringContent += this.clean(im.getCostruttore()) + ";";

							//MANUTENTORE
							stringContent += (im.getManutentore()!=null ? this.clean(im.getManutentore().getCurrentTranslation()) : "") + ";";

							//DISTANZA
							stringContent += (im.getDistanza()!=null ? this.clean(im.getDistanza().getCurrentTranslation()) : "") + ";";

							//FERMATE
							stringContent += (im.getFermate()!=null ? this.clean(im.getFermate().toString()) : "") + ";";


							//UTENTE LETTERA
							stringContent += (im.getUtenteLettera()!=null ? this.clean(im.getUtenteLettera().getCurrentTranslation()) : "") + ";";

							//AMMINISTRATORE
							stringContent += (im.getAmministratore()!=null ? this.clean(im.getAmministratore().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(im.getNote()) + "\n";

							//Apparecchi di sollevamento	
						} else if("04".equals(impType)){
							ImpSoll is = (ImpSoll)imp;

							//TIPO APPARECCHIO DI SOLLEVAMENTO
							stringContent += (is.getSubTypeSoll()!=null ? this.clean(is.getSubTypeSoll().getCurrentTranslation()) : "") + ";";

							//SOTTOTIPO
							stringContent += (is.getSubType()!=null ? this.clean(is.getSubType().getCurrentTranslation()) : "") + ";";

							//ANNO COSTRUZIONE
							stringContent += (is.getCostruzione()!=null ? this.clean(is.getCostruzione().toString()) : "") + ";";

							//COSTRUTTORE
							stringContent += this.clean(is.getCostruttore()) + ";";

							//COSTRUTTORE RADIOCOMANDO
							stringContent += this.clean(is.getCostrRadioc()) + ";";

							//TARGA
							stringContent += this.clean(is.getTarga()) + ";";

							//PORTATA (KG)
							stringContent += (is.getPortata()!=null ? this.clean(is.getPortata().toString()) : "") + ";";


							//NUMERO FABBRICA
							stringContent += this.clean(is.getNumeroFabbrica()) + ";";

							//TIPO FABBRICA
							stringContent += this.clean(is.getTipoFabb()) + ";";

							//NUMERO RADIOCOMANDO
							stringContent += this.clean(is.getNumRadioc()) + ";";

							//TELAIO
							stringContent += this.clean(is.getTelaio()) + ";";

							//STATO IMPIANTO
							stringContent += (is.getStatoImpianto()!=null ? this.clean(is.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(is.getNote()) + "\n";

							//Impianti elettrici	
						} else if("05".equals(impType)){
							ImpTerra it = (ImpTerra)imp;

							//TIPO IMPIANTO ELETTRICO
							stringContent += (it.getSubTypeTerra()!=null ? this.clean(it.getSubTypeTerra().getCurrentTranslation()) : "") + ";";

							//STRUTTURE AUTOPROTETTE
							stringContent += (it.getTipologia()!=null ? ("48".equals(it.getTipologia().getCode())?"Si":"No") : "") + ";";

							//SOTTOTIPO B
							stringContent += (it.getSubTypeB()!=null ? this.clean(it.getSubTypeB().getCurrentTranslation()) : "") + ";";

							//POTENZA (KWATT)
							stringContent += (it.getPot()!=null ? this.clean(it.getPot().toString()) : "") + ";";


							//STATO IMPIANTO
							stringContent += (it.getStatoImpianto()!=null ? this.clean(it.getStatoImpianto().getCurrentTranslation()) : "") + ";";

							//NOTE
							stringContent += this.clean(it.getNote()) + "\n";
						}
					}
				}

				// importantissimo
				//impIndex++;
			}

			//impList.goToPage(currentIndex);

			idoc.setContent(stringContent.getBytes());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
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

	private void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex) throws RowsExceededException, WriteException {
		writeCellsFromArray(rowCells, sheet, rowIndex, false);
	}


	
	public void generateXlsFile(ImpiantiDocument idoc, PagedDataModel<Impianto> impList){
		ByteArrayOutputStream output = null;

		try {
			if (impList==null || impList.getFullList()==null)
				return;
			
			output = new ByteArrayOutputStream();
			WritableWorkbook wb = Workbook.createWorkbook(output);
			WritableSheet sheet = wb.createSheet("First Sheet", 0);

			Date currDate = new Date();
			List<Date> nextVerifDateList = new ArrayList<Date>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VerificaImpAction vAction = VerificaImpAction.instance();
			List<VerificaImp> lastVerList = new ArrayList<VerificaImp>();
			VerificaImp lastVer = null;


			String impType = "";
			List<Map> impianti = (List<Map>)impList.getFullList();

			ImpiantoAction ia = ImpiantoAction.instance();
			HashMap<String, Object> temp = ia.getTemporary();

			Boolean singleType = temp.get("singleType")==null?false:(Boolean)ia.getTemporary().get("singleType");
			String[] header = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA"};
			String[] header01 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO APPARECCHIO","CARATTERISTICHE SPECIALI","COMODANTE","COSTRUTTORE","NUMERO FABBRICA","PRESSIONE BAR 1^","CAPACITÀ (LT)","SUPERFICIE MQ","PRODUCIBILITA T/H","STATO IMPIANTO","NOTE"};
			String[] header02 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"IMPIANTO","DESTINAZIONE LOCALI","POT. GLOBALE KWATT","DESTINAZIONE IMPIANTO","MANUTENTORE","STATO IMPIANTO","NOTE"};
			String[] header03 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"MATRICOLA COMUNE","STATO IMPIANTO","ANNO COSTRUZIONE","DESTINAZIONE","CATEGORIA","NUMERO FABBRICA","PORTATA (KG)","COSTRUTTORE","MANUTENTORE","DISTANZA","FERMATE","UTENTE LETTERA","AMMINISTRATORE","NOTE"};
			String[] header04 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO APPARECCHIO DI SOLLEVAMENTO","SOTTOTIPO","ANNO COSTRUZIONE","COSTRUTTORE","COSTRUTTORE RADIOCOMANDO","TARGA","PORTATA (KG)","NUMERO FABBRICA","TIPO FABBRICA","NUMERO RADIOCOMANDO","TELAIO","STATO IMPIANTO","NOTE"};
			String[] header05 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO IMPIANTO ELETTRICO","STRUTTURE AUTOPROTETTE","SOTTOTIPO B","POTENZA (KWATT)","STATO IMPIANTO","NOTE"};


			//Se nel processo anagraficaImpianti è stata effettuata una ricerca multipla
			if (!singleType){
				writeCellsFromArray(header, sheet, 0);
			}
			else {

				//Apparecchi a pressione
				if (temp.get("01")==null?false:(Boolean)temp.get("01")){
					impType = "01";
					writeCellsFromArray(header01, sheet, 0);

					//Impianti di riscaldamento
				} else if (temp.get("02")==null?false:(Boolean)temp.get("02")){
					impType = "02";
					writeCellsFromArray(header02, sheet, 0);

					//Ascensori e montacarichi
				} else if (temp.get("03")==null?false:(Boolean)temp.get("03")){
					impType = "03";
					writeCellsFromArray(header03, sheet, 0);

					//Apparecchi di sollevamento
				} else if (temp.get("04")==null?false:(Boolean)temp.get("04")){
					impType = "04";
					writeCellsFromArray(header04, sheet, 0);

					//Impianti elettrici
				} else if (temp.get("05")==null?false:(Boolean)temp.get("05")){
					impType = "05";
					writeCellsFromArray(header05, sheet, 0);
				}			
			}
			
			firstRowOfData = 1;

			for(@SuppressWarnings("rawtypes") Map map : impianti){
				Impianto imp = null;
				String type =null;

				Long internalId = (Long)map.get("internalId");

				CodeValuePhi code = (CodeValuePhi)map.get("code");
				if (code!=null)
					type = code.getCode();

				//PER ULTIMA VERIFICA
				vAction.cleanRestrictions();
				vAction.isNull.put("statusCode", false);
				vAction.notEqual.put("statusCode.code", "new");
				vAction.notEqual.put("pre", true);
				vAction.orderBy.put("data", "descending");

				if (type!=null){
					if("01".equals(type)){
						imp = ca.get(ImpPress.class, internalId);
						vAction.equal.put("impPress.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("02".equals(type)){
						imp = ca.get(ImpRisc.class, internalId);
						vAction.equal.put("impRisc.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("03".equals(type)){
						imp = ca.get(ImpMonta.class, internalId);
						vAction.equal.put("impMonta.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("04".equals(type)){
						imp = ca.get(ImpSoll.class, internalId);
						vAction.equal.put("impSoll.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("05".equals(type)){
						imp = ca.get(ImpTerra.class, internalId);
						vAction.equal.put("impTerra.internalId", internalId); //PER ULTIMA VERIFICA
					}
				}

				if(imp != null) {
					firstColumnOfData = 0;	

					//SIGLA
					String sigla = (imp.getSigla()!=null ? this.clean(imp.getSigla()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sigla));
					firstColumnOfData++;

					//TIPO
					if(imp.getSubTypeSoll()!=null)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,this.clean(imp.getSubTypeSoll().getCurrentTranslation())));
					else if(imp.getSubTypeTerra()!=null)
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,this.clean(imp.getSubTypeTerra().getCurrentTranslation())));
					firstColumnOfData++;

					//MATRICOLA
					String matricola = (imp.getMatricola()!=null ? this.clean(imp.getMatricola()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,matricola));
					firstColumnOfData++;

					//ANNO
					String anno = (imp.getAnno()!=null ? this.clean(imp.getAnno()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,anno));
					firstColumnOfData++;

					//TIPOLOGIA_IMPIANTO
					String tipologiaImp = (imp.getCode()!=null ? this.clean(imp.getCode().getCurrentTranslation()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipologiaImp));
					firstColumnOfData++;

					//SEDE_INST_DENOMINAZIONE
					SediInstallazione si = imp.getSedeInstallazione();
					String denominazioneSI = (si!=null ? this.clean(si.getDenominazione()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,denominazioneSI));
					firstColumnOfData++;

					//SEDE_INST_INDIRIZZO
					AD addrSI = null;
					if(si!=null){
						addrSI = si.getAddr();
					}
					String indirizzoSI = (addrSI!=null ? this.clean(addrSI.toString()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,indirizzoSI));
					firstColumnOfData++;

					//SEDE_ADD_DENOMINAZIONE
					Sedi se = imp.getSedi();
					String denominazioneSA = (se!=null ? this.clean(se.getDenominazioneUnitaLocale()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,denominazioneSA));
					firstColumnOfData++;

					//SEDE_ADD_INDIRIZZO
					AD addrSA = null;
					if(se!=null)
						addrSA = se.getAddr();

					String indirizzoSA = (addrSA!=null ? this.clean(addrSA.toString()) : "");
					sheet.addCell(new Label(firstColumnOfData,firstRowOfData,indirizzoSA));
					firstColumnOfData++;

					//ULTIMA VERIFICA
					if(!Boolean.TRUE.equals((Boolean)temp.get("noVerifiche"))){
						lastVerList = vAction.select();
					}
					if(lastVerList!=null && !lastVerList.isEmpty()) {
						lastVer = lastVerList.get(0);
					}else {
						lastVer = null;
					}

					if(lastVer!=null){
						//DATA_ULTIMA_VERIFICA
						String dataLastVer = (lastVer.getData()!=null ? this.clean(sdf.format(lastVer.getData())) : "");
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,dataLastVer));
						firstColumnOfData++;

						//TECNICO_ULTIMA_VERIFICA
						String tecnicoLastVer = (lastVer.getOperatore()!=null ? this.clean(lastVer.getOperatore().getName().toString()) : "");
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tecnicoLastVer));
						firstColumnOfData++;

					}else {
						firstColumnOfData++;
						firstColumnOfData++;
					}

					//DATA_SCADENZA
					nextVerifDateList.clear();
					if(imp.getNextVerifDate1()!=null)
						nextVerifDateList.add(imp.getNextVerifDate1());

					if(imp.getNextVerifDate2()!=null)
						nextVerifDateList.add(imp.getNextVerifDate2());

					if(imp.getNextVerifDate3()!=null)
						nextVerifDateList.add(imp.getNextVerifDate3());

					if(nextVerifDateList!=null && !nextVerifDateList.isEmpty()){
						//DATA_ULTIMA_VERIFICA
						Collections.sort(nextVerifDateList);
						/*Date minDate = null;
						for(Date d : nextVerifDateList){
							//se la data è minore di oggi, prendo la massima minore di oggi
							if(d.before(currDate)){
								minDate = d;
							}else if(minDate!=null){
								break;
							}else{
								minDate = d;
								break;
							}
						}*/
	
						Date minDate = Collections.min(nextVerifDateList);
						String dataScad = (minDate!=null ? this.clean(sdf.format(minDate)) : "");
						sheet.addCell(new Label(firstColumnOfData,firstRowOfData,dataScad));
						firstColumnOfData++;

					} else
						firstColumnOfData++;

					/* Gestione campi specifici */
					if (!singleType)
						firstColumnOfData++;
					else {

						//Apparecchi a pressione
						if("01".equals(impType)){
							ImpPress ip = (ImpPress)imp;

							//TIPO APPARECCHIO
							String tipoApp = (ip.getTipoApparecchio()!=null ? this.clean(ip.getTipoApparecchio().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoApp));
							firstColumnOfData++;

							//CARATTERISTICHE SPECIALI
							String caratteristiche = (ip.getCaratteristicheSpec()!=null ? this.clean(ip.getCaratteristicheSpec().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,caratteristiche));
							firstColumnOfData++;

							//COMODANTE
							String comodante = (ip.getComodante()!=null ? this.clean(ip.getComodante().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,comodante));
							firstColumnOfData++;

							//COSTRUTTORE
							String costruttore = this.clean(ip.getCostruttore());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,costruttore));
							firstColumnOfData++;

							//NUMERO FABBRICA
							String numFabbrica = this.clean(ip.getNumeroFabbrica());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,numFabbrica));
							firstColumnOfData++;

							//PRESSIONE BAR 1^
							String pressione1 = (ip.getPressBar1()!=null ? this.clean(ip.getPressBar1().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,pressione1));
							firstColumnOfData++;

							//CAPACITÀ (LT)
							String capacita = (ip.getCapacita()!=null ? this.clean(ip.getCapacita().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,capacita));
							firstColumnOfData++;


							//SUPERFICIE MQ
							String superf = (ip.getSuperficie()!=null ? this.clean(ip.getSuperficie().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,superf));
							firstColumnOfData++;

							//PRODUCIBILITA T/H
							String producibiita = (ip.getProducibilita()!=null ? this.clean(ip.getProducibilita().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,producibiita));
							firstColumnOfData++;

							//STATO IMPIANTO
							String statoImp = (ip.getStatoImpianto()!=null ? this.clean(ip.getStatoImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,statoImp));
							firstColumnOfData++;

							//NOTE
							String note = this.clean(ip.getNote());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,note));
							firstColumnOfData++;

							//Impianti di riscaldamento		
						} else if("02".equals(impType)){
							ImpRisc ir = (ImpRisc)imp;

							//IMPIANTO
							String impianto = (ir.getImpianto()!=null ? this.clean(ir.getImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,impianto));
							firstColumnOfData++;

							//DESTINAZIONE LOCALI
							String destinazione = (ir.getDescrizLocali()!=null ? this.clean(ir.getDescrizLocali().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,destinazione));
							firstColumnOfData++;

							//POT. GLOBALE KWATT
							String potGlob = (ir.getPotGlob()!=null ? this.clean(ir.getPotGlob().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,potGlob));
							firstColumnOfData++;

							//DESTINAZIONE IMPIANTO
							String destinazImp = (ir.getDescrizImpianto()!=null ? this.clean(ir.getDescrizImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,destinazImp));
							firstColumnOfData++;

							//MANUTENTORE
							String manutentore = (ir.getManutentore()!=null ? this.clean(ir.getManutentore().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,manutentore));
							firstColumnOfData++;

							//STATO IMPIANTO
							String statoImp = (ir.getStatoImpianto()!=null ? this.clean(ir.getStatoImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,statoImp));
							firstColumnOfData++;

							//NOTE
							String note = this.clean(ir.getNote());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,note));
							firstColumnOfData++;

							//Ascensori e montacarichi
						} else if("03".equals(impType)){
							ImpMonta im = (ImpMonta)imp;

							//MATRICOLA COMUNE
							String matrComune = this.clean(im.getMatrcomune());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,matrComune));
							firstColumnOfData++;

							//STATO IMPIANTO
							String statoImp = (im.getStatoImpianto()!=null ? this.clean(im.getStatoImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,statoImp));
							firstColumnOfData++;

							//ANNO COSTRUZIONE
							String annoCostr = (im.getCostruzione()!=null ? this.clean(im.getCostruzione().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,annoCostr));
							firstColumnOfData++;


							//DESTINAZIONE
							String destinazione = (im.getDestinazione()!=null ? this.clean(im.getDestinazione().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,destinazione));
							firstColumnOfData++;

							//CATEGORIA
							String categoria = (im.getCategoria()!=null ? this.clean(im.getCategoria().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,categoria));
							firstColumnOfData++;

							//NUMERO FABBRICA
							String numeroFabb = this.clean(im.getNumeroFabbrica());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,numeroFabb));
							firstColumnOfData++;

							//PORTATA (KG)
							String portata = (im.getPortata()!=null ? this.clean(im.getPortata().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,portata));
							firstColumnOfData++;


							//COSTRUTTORE
							String costruttore = this.clean(im.getCostruttore());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,costruttore));
							firstColumnOfData++;

							//MANUTENTORE
							String manutentore = (im.getManutentore()!=null ? this.clean(im.getManutentore().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,manutentore));
							firstColumnOfData++;

							//DISTANZA
							String distanza = (im.getDistanza()!=null ? this.clean(im.getDistanza().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,distanza));
							firstColumnOfData++;

							//FERMATE
							String fermate = (im.getFermate()!=null ? this.clean(im.getFermate().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,fermate));
							firstColumnOfData++;


							//UTENTE LETTERA
							String utente = (im.getUtenteLettera()!=null ? this.clean(im.getUtenteLettera().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,utente));
							firstColumnOfData++;

							//AMMINISTRATORE
							String amministratore = (im.getAmministratore()!=null ? this.clean(im.getAmministratore().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,amministratore));
							firstColumnOfData++;

							//NOTE
							String note = this.clean(im.getNote());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,note));
							firstColumnOfData++;

							//Apparecchi di sollevamento	
						} else if("04".equals(impType)){
							ImpSoll is = (ImpSoll)imp;

							//TIPO APPARECCHIO DI SOLLEVAMENTO
							String subTypeSoll = (is.getSubTypeSoll()!=null ? this.clean(is.getSubTypeSoll().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,subTypeSoll));
							firstColumnOfData++;

							//SOTTOTIPO
							String subType = (is.getSubType()!=null ? this.clean(is.getSubType().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,subType));
							firstColumnOfData++;

							//ANNO COSTRUZIONE
							String annoCostr = (is.getCostruzione()!=null ? this.clean(is.getCostruzione().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,annoCostr));
							firstColumnOfData++;

							//COSTRUTTORE
							String costruttore = this.clean(is.getCostruttore());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,costruttore));
							firstColumnOfData++;

							//COSTRUTTORE RADIOCOMANDO
							String costrRadio = this.clean(is.getCostrRadioc());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,costrRadio));
							firstColumnOfData++;

							//TARGA
							String targa = this.clean(is.getTarga());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,targa));
							firstColumnOfData++;

							//PORTATA (KG)
							String portata = (is.getPortata()!=null ? this.clean(is.getPortata().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,portata));
							firstColumnOfData++;


							//NUMERO FABBRICA
							String numeroFabb = this.clean(is.getNumeroFabbrica());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,numeroFabb));
							firstColumnOfData++;

							//TIPO FABBRICA
							String tipoFabb = this.clean(is.getTipoFabb());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,tipoFabb));
							firstColumnOfData++;

							//NUMERO RADIOCOMANDO
							String numeroRadio = this.clean(is.getNumRadioc());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,numeroRadio));
							firstColumnOfData++;

							//TELAIO
							String telaio = this.clean(is.getTelaio());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,telaio));
							firstColumnOfData++;

							//STATO IMPIANTO
							String statoImp = (is.getStatoImpianto()!=null ? this.clean(is.getStatoImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,statoImp));
							firstColumnOfData++;

							//NOTE
							String note = this.clean(is.getNote());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,note));
							firstColumnOfData++;

							//Impianti elettrici	
						} else if("05".equals(impType)){
							ImpTerra it = (ImpTerra)imp;

							//TIPO IMPIANTO ELETTRICO
							String subTypeTerra = (it.getSubTypeTerra()!=null ? this.clean(it.getSubTypeTerra().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,subTypeTerra));
							firstColumnOfData++;

							//STRUTTURE AUTOPROTETTE
							String struttureAutop = (it.getTipologia()!=null ? ("48".equals(it.getTipologia().getCode())?"Si":"No") : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,struttureAutop));
							firstColumnOfData++;

							//SOTTOTIPO B
							String sotttipoB = (it.getSubTypeB()!=null ? this.clean(it.getSubTypeB().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,sotttipoB));
							firstColumnOfData++;

							//POTENZA (KWATT)
							String potenza = (it.getPot()!=null ? this.clean(it.getPot().toString()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,potenza));
							firstColumnOfData++;


							//STATO IMPIANTO
							String statoImp = (it.getStatoImpianto()!=null ? this.clean(it.getStatoImpianto().getCurrentTranslation()) : "");
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,statoImp));
							firstColumnOfData++;

							//NOTE
							String note = this.clean(it.getNote());
							sheet.addCell(new Label(firstColumnOfData,firstRowOfData,note));
							firstColumnOfData++;
						}
					}

					firstRowOfData++;

				}
			}

			wb.write();
			wb.close();
			output.flush();
			output.close();
			idoc.setContent(output.toByteArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	
	/*
	public void generateXlsxFile(ImpiantiDocument idoc, PagedDataModel<Impianto> impList) throws FileNotFoundException{
		ByteArrayOutputStream output = null;
		FileOutputStream out = null;
		
		try {
			if (impList==null || impList.getFullList()==null)
				return;
	
			out = new FileOutputStream(
				      new File("C:/TEMP/createworkbook.xlsx"));
			
			//output = new ByteArrayOutputStream();
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("First Sheet");
	
			Date currDate = new Date();
			List<Date> nextVerifDateList = new ArrayList<Date>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			VerificaImpAction vAction = VerificaImpAction.instance();
			List<VerificaImp> lastVerList = new ArrayList<VerificaImp>();
			VerificaImp lastVer = null;
	
	
			String impType = "";
			List<Map> impianti = (List<Map>)impList.getFullList();
	
			ImpiantoAction ia = ImpiantoAction.instance();
			HashMap<String, Object> temp = ia.getTemporary();
	
			Boolean singleType = temp.get("singleType")==null?false:(Boolean)ia.getTemporary().get("singleType");
			String[] header = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA"};
			String[] header01 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO APPARECCHIO","CARATTERISTICHE SPECIALI","COMODANTE","COSTRUTTORE","NUMERO FABBRICA","PRESSIONE BAR 1^","CAPACITÀ (LT)","SUPERFICIE MQ","PRODUCIBILITA T/H","STATO IMPIANTO","NOTE"};
			String[] header02 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"IMPIANTO","DESTINAZIONE LOCALI","POT. GLOBALE KWATT","DESTINAZIONE IMPIANTO","MANUTENTORE","STATO IMPIANTO","NOTE"};
			String[] header03 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"MATRICOLA COMUNE","STATO IMPIANTO","ANNO COSTRUZIONE","DESTINAZIONE","CATEGORIA","NUMERO FABBRICA","PORTATA (KG)","COSTRUTTORE","MANUTENTORE","DISTANZA","FERMATE","UTENTE LETTERA","AMMINISTRATORE","NOTE"};
			String[] header04 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO APPARECCHIO DI SOLLEVAMENTO","SOTTOTIPO","ANNO COSTRUZIONE","COSTRUTTORE","COSTRUTTORE RADIOCOMANDO","TARGA","PORTATA (KG)","NUMERO FABBRICA","TIPO FABBRICA","NUMERO RADIOCOMANDO","TELAIO","STATO IMPIANTO","NOTE"};
			String[] header05 = {"SIGLA","TIPO","MATRICOLA","ANNO","TIPOLOGIA_IMPIANTO","SEDE_INST_DENOMINAZIONE","SEDE_INST_INDIRIZZO","SEDE_ADD_DENOMINAZIONE","SEDE_ADD_INDIRIZZO","DATA_ULTIMA_VERIFICA","TECNICO_ULTIMA_VERIFICA","DATA_SCADENZA",
					"TIPO IMPIANTO ELETTRICO","STRUTTURE AUTOPROTETTE","SOTTOTIPO B","POTENZA (KWATT)","STATO IMPIANTO","NOTE"};
			
			//Se nel processo anagraficaImpianti è stata effettuata una ricerca multipla
			if (!singleType)
				writexlsxCellsFromArray(header, sheet, 0);
			else {
	
				//Apparecchi a pressione
				if (temp.get("01")==null?false:(Boolean)temp.get("01")){
					impType = "01";
					writexlsxCellsFromArray(header01, sheet, 0);
	
					//Impianti di riscaldamento
				} else if (temp.get("02")==null?false:(Boolean)temp.get("02")){
					impType = "02";
					writexlsxCellsFromArray(header02, sheet, 0);
	
					//Ascensori e montacarichi
				} else if (temp.get("03")==null?false:(Boolean)temp.get("03")){
					impType = "03";
					writexlsxCellsFromArray(header03, sheet, 0);
	
					//Apparecchi di sollevamento
				} else if (temp.get("04")==null?false:(Boolean)temp.get("04")){
					impType = "04";
					writexlsxCellsFromArray(header04, sheet, 0);
	
					//Impianti elettrici
				} else if (temp.get("05")==null?false:(Boolean)temp.get("05")){
					impType = "05";
					writexlsxCellsFromArray(header05, sheet, 0);
				}			
			}
	
			for(@SuppressWarnings("rawtypes") Map map : impianti){
				Impianto imp = null;
				String type =null;
	
				Long internalId = (Long)map.get("internalId");
	
				CodeValuePhi code = (CodeValuePhi)map.get("code");
				if (code!=null)
					type = code.getCode();
	
				//PER ULTIMA VERIFICA
				vAction.cleanRestrictions();
				vAction.isNull.put("statusCode", false);
				vAction.notEqual.put("statusCode.code", "new");
				vAction.notEqual.put("pre", true);
				vAction.orderBy.put("data", "descending");
	
				if (type!=null){
					if("01".equals(type)){
						imp = ca.get(ImpPress.class, internalId);
						vAction.equal.put("impPress.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("02".equals(type)){
						imp = ca.get(ImpRisc.class, internalId);
						vAction.equal.put("impRisc.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("03".equals(type)){
						imp = ca.get(ImpMonta.class, internalId);
						vAction.equal.put("impMonta.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("04".equals(type)){
						imp = ca.get(ImpSoll.class, internalId);
						vAction.equal.put("impSoll.internalId", internalId); //PER ULTIMA VERIFICA
					}else if("05".equals(type)){
						imp = ca.get(ImpTerra.class, internalId);
						vAction.equal.put("impTerra.internalId", internalId); //PER ULTIMA VERIFICA
					}
				}
	
				if(imp != null) {
					XSSFRow row = sheet.createRow(firstRowOfData);
					//XSSFCell xlsxCell = null;
					firstColumnOfData = 0;	
	
					List<String> cellsValues = new ArrayList<String>();
					//SIGLA
					String sigla = (imp.getSigla()!=null ? this.clean(imp.getSigla()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellValue(null);
		            //xlsxCell.setCellValue(sigla);
					cellsValues.add(sigla);
					firstColumnOfData++;
	
					//log.info("cella: "+//xlsxCell.getStringCellValue());
					//TIPO
					if(imp.getSubTypeSoll()!=null){
						//xlsxCell = row.createCell(firstColumnOfData);
			            //xlsxCell.setCellType(null);
			            //xlsxCell.setCellValue(this.clean(imp.getSubTypeSoll().getCurrentTranslation()));
					}else if(imp.getSubTypeTerra()!=null){
						//xlsxCell = row.createCell(firstColumnOfData);
			            //xlsxCell.setCellType(null);
			            //xlsxCell.setCellValue(this.clean(imp.getSubTypeTerra().getCurrentTranslation()));
					}
					firstColumnOfData++;
	
					//MATRICOLA
					String matricola = (imp.getMatricola()!=null ? this.clean(imp.getMatricola()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(matricola);
					cellsValues.add(matricola);
					firstColumnOfData++;
	
					//ANNO
					String anno = (imp.getAnno()!=null ? this.clean(imp.getAnno()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(anno);
					cellsValues.add(anno);
					firstColumnOfData++;
	
					//TIPOLOGIA_IMPIANTO
					String tipologiaImp = (imp.getCode()!=null ? this.clean(imp.getCode().getCurrentTranslation()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(tipologiaImp);
					cellsValues.add(tipologiaImp);
					firstColumnOfData++;
	
					//SEDE_INST_DENOMINAZIONE
					SediInstallazione si = imp.getSedeInstallazione();
					String denominazioneSI = (si!=null ? this.clean(si.getDenominazione()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(denominazioneSI);
					cellsValues.add(denominazioneSI);
					firstColumnOfData++;
	
					//SEDE_INST_INDIRIZZO
					AD addrSI = null;
					if(si!=null){
						addrSI = si.getAddr();
					}
					String indirizzoSI = (addrSI!=null ? this.clean(addrSI.toString()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(indirizzoSI);
					cellsValues.add(indirizzoSI);
					firstColumnOfData++;
	
					//SEDE_ADD_DENOMINAZIONE
					Sedi se = imp.getSedi();
					String denominazioneSA = (se!=null ? this.clean(se.getDenominazioneUnitaLocale()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(denominazioneSA);
					cellsValues.add(denominazioneSA);
					firstColumnOfData++;
	
					//SEDE_ADD_INDIRIZZO
					AD addrSA = null;
					if(se!=null)
						addrSA = se.getAddr();
	
					String indirizzoSA = (addrSA!=null ? this.clean(addrSA.toString()) : "");
					//xlsxCell = row.createCell(firstColumnOfData);
		            //xlsxCell.setCellType(null);
		            //xlsxCell.setCellValue(indirizzoSA);
					cellsValues.add(indirizzoSA);
					firstColumnOfData++;
	
					//ULTIMA VERIFICA
					if(!Boolean.TRUE.equals((Boolean)temp.get("noVerifiche"))){
						lastVerList = vAction.select();
					}
					if(lastVerList!=null && !lastVerList.isEmpty()) {
						lastVer = lastVerList.get(0);
					}else {
						lastVer = null;
					}
	
					if(lastVer!=null){
						//DATA_ULTIMA_VERIFICA
						String dataLastVer = (lastVer.getData()!=null ? this.clean(sdf.format(lastVer.getData())) : "");
						//xlsxCell = row.createCell(firstColumnOfData);
			            //xlsxCell.setCellType(null);
			            //xlsxCell.setCellValue(dataLastVer);
						firstColumnOfData++;
	
						//TECNICO_ULTIMA_VERIFICA
						String tecnicoLastVer = (lastVer.getOperatore()!=null ? this.clean(lastVer.getOperatore().getName().toString()) : "");
						//xlsxCell = row.createCell(firstColumnOfData);
			            //xlsxCell.setCellType(null);
			            //xlsxCell.setCellValue(tecnicoLastVer);
						firstColumnOfData++;
	
					}else {
						firstColumnOfData++;
						firstColumnOfData++;
					}
	
					//DATA_SCADENZA
					nextVerifDateList.clear();
					if(imp.getNextVerifDate1()!=null)
						nextVerifDateList.add(imp.getNextVerifDate1());
	
					if(imp.getNextVerifDate2()!=null)
						nextVerifDateList.add(imp.getNextVerifDate2());
	
					if(imp.getNextVerifDate3()!=null)
						nextVerifDateList.add(imp.getNextVerifDate3());
	
					if(nextVerifDateList!=null && !nextVerifDateList.isEmpty()){
						//DATA_ULTIMA_VERIFICA
						Collections.sort(nextVerifDateList);
						/*Date minDate = null;
						for(Date d : nextVerifDateList){
							//se la data è minore di oggi, prendo la massima minore di oggi
							if(d.before(currDate)){
								minDate = d;
							}else if(minDate!=null){
								break;
							}else{
								minDate = d;
								break;
							}
						}*/
	/*
						Date minDate = Collections.min(nextVerifDateList);
						String dataScad = (minDate!=null ? this.clean(sdf.format(minDate)) : "");
						//xlsxCell = row.createCell(firstColumnOfData);
			            //xlsxCell.setCellType(null);
			            //xlsxCell.setCellValue(dataScad);
						firstColumnOfData++;
	
					} else
						firstColumnOfData++;
	
					/* Gestione campi specifici *//*
					if (!singleType)
						firstColumnOfData++;
					else {
	
						//Apparecchi a pressione
						if("01".equals(impType)){
							ImpPress ip = (ImpPress)imp;
	
							//TIPO APPARECCHIO
							String tipoApp = (ip.getTipoApparecchio()!=null ? this.clean(ip.getTipoApparecchio().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(tipoApp);
							firstColumnOfData++;
	
							//CARATTERISTICHE SPECIALI
							String caratteristiche = (ip.getCaratteristicheSpec()!=null ? this.clean(ip.getCaratteristicheSpec().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(caratteristiche);
							firstColumnOfData++;
	
							//COMODANTE
							String comodante = (ip.getComodante()!=null ? this.clean(ip.getComodante().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(comodante);
							firstColumnOfData++;
	
							//COSTRUTTORE
							String costruttore = this.clean(ip.getCostruttore());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(costruttore);
							firstColumnOfData++;
	
							//NUMERO FABBRICA
							String numFabbrica = this.clean(ip.getNumeroFabbrica());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(numFabbrica);
							firstColumnOfData++;
	
							//PRESSIONE BAR 1^
							String pressione1 = (ip.getPressBar1()!=null ? this.clean(ip.getPressBar1().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(pressione1);
							firstColumnOfData++;
	
							//CAPACITÀ (LT)
							String capacita = (ip.getCapacita()!=null ? this.clean(ip.getCapacita().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(capacita);
							firstColumnOfData++;
	
	
							//SUPERFICIE MQ
							String superf = (ip.getSuperficie()!=null ? this.clean(ip.getSuperficie().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(superf);
							firstColumnOfData++;
	
							//PRODUCIBILITA T/H
							String producibiita = (ip.getProducibilita()!=null ? this.clean(ip.getProducibilita().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(producibiita);
							firstColumnOfData++;
	
							//STATO IMPIANTO
							String statoImp = (ip.getStatoImpianto()!=null ? this.clean(ip.getStatoImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(statoImp);
							firstColumnOfData++;
	
							//NOTE
							String note = this.clean(ip.getNote());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(note);
							firstColumnOfData++;
	
							//Impianti di riscaldamento		
						} else if("02".equals(impType)){
							ImpRisc ir = (ImpRisc)imp;
	
							//IMPIANTO
							String impianto = (ir.getImpianto()!=null ? this.clean(ir.getImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(impianto);
							firstColumnOfData++;
	
							//DESTINAZIONE LOCALI
							String destinazione = (ir.getDescrizLocali()!=null ? this.clean(ir.getDescrizLocali().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(destinazione);
							firstColumnOfData++;
	
							//POT. GLOBALE KWATT
							String potGlob = (ir.getPotGlob()!=null ? this.clean(ir.getPotGlob().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(potGlob);
							firstColumnOfData++;
	
							//DESTINAZIONE IMPIANTO
							String destinazImp = (ir.getDescrizImpianto()!=null ? this.clean(ir.getDescrizImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(destinazImp);
							firstColumnOfData++;
	
							//MANUTENTORE
							String manutentore = (ir.getManutentore()!=null ? this.clean(ir.getManutentore().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(manutentore);
							firstColumnOfData++;
	
							//STATO IMPIANTO
							String statoImp = (ir.getStatoImpianto()!=null ? this.clean(ir.getStatoImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(statoImp);
							firstColumnOfData++;
	
							//NOTE
							String note = this.clean(ir.getNote());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(note);
							firstColumnOfData++;
	
							//Ascensori e montacarichi
						} else if("03".equals(impType)){
							ImpMonta im = (ImpMonta)imp;
	
							//MATRICOLA COMUNE
							String matrComune = this.clean(im.getMatrcomune());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(matrComune);
							firstColumnOfData++;
	
							//STATO IMPIANTO
							String statoImp = (im.getStatoImpianto()!=null ? this.clean(im.getStatoImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(statoImp);
							firstColumnOfData++;
	
							//ANNO COSTRUZIONE
							String annoCostr = (im.getCostruzione()!=null ? this.clean(im.getCostruzione().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(annoCostr);
							firstColumnOfData++;
	
	
							//DESTINAZIONE
							String destinazione = (im.getDestinazione()!=null ? this.clean(im.getDestinazione().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(destinazione);
							firstColumnOfData++;
	
							//CATEGORIA
							String categoria = (im.getCategoria()!=null ? this.clean(im.getCategoria().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(categoria);
							firstColumnOfData++;
	
							//NUMERO FABBRICA
							String numeroFabb = this.clean(im.getNumeroFabbrica());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(numeroFabb);
							firstColumnOfData++;
	
							//PORTATA (KG)
							String portata = (im.getPortata()!=null ? this.clean(im.getPortata().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(portata);
							firstColumnOfData++;
	
	
							//COSTRUTTORE
							String costruttore = this.clean(im.getCostruttore());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(costruttore);
							firstColumnOfData++;
	
							//MANUTENTORE
							String manutentore = (im.getManutentore()!=null ? this.clean(im.getManutentore().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(manutentore);
							firstColumnOfData++;
	
							//DISTANZA
							String distanza = (im.getDistanza()!=null ? this.clean(im.getDistanza().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(distanza);
							firstColumnOfData++;
	
							//FERMATE
							String fermate = (im.getFermate()!=null ? this.clean(im.getFermate().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(fermate);
							firstColumnOfData++;
	
	
							//UTENTE LETTERA
							String utente = (im.getUtenteLettera()!=null ? this.clean(im.getUtenteLettera().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(utente);
							firstColumnOfData++;
	
							//AMMINISTRATORE
							String amministratore = (im.getAmministratore()!=null ? this.clean(im.getAmministratore().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(amministratore);
							firstColumnOfData++;
	
							//NOTE
							String note = this.clean(im.getNote());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(note);
							firstColumnOfData++;
	
							//Apparecchi di sollevamento	
						} else if("04".equals(impType)){
							ImpSoll is = (ImpSoll)imp;
	
							//TIPO APPARECCHIO DI SOLLEVAMENTO
							String subTypeSoll = (is.getSubTypeSoll()!=null ? this.clean(is.getSubTypeSoll().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(subTypeSoll);
							firstColumnOfData++;
	
							//SOTTOTIPO
							String subType = (is.getSubType()!=null ? this.clean(is.getSubType().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(subType);
							firstColumnOfData++;
	
							//ANNO COSTRUZIONE
							String annoCostr = (is.getCostruzione()!=null ? this.clean(is.getCostruzione().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(annoCostr);
							firstColumnOfData++;
	
							//COSTRUTTORE
							String costruttore = this.clean(is.getCostruttore());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(costruttore);
							firstColumnOfData++;
	
							//COSTRUTTORE RADIOCOMANDO
							String costrRadio = this.clean(is.getCostrRadioc());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(costrRadio);
							firstColumnOfData++;
	
							//TARGA
							String targa = this.clean(is.getTarga());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(targa);
							firstColumnOfData++;
	
							//PORTATA (KG)
							String portata = (is.getPortata()!=null ? this.clean(is.getPortata().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(portata);
							firstColumnOfData++;
	
	
							//NUMERO FABBRICA
							String numeroFabb = this.clean(is.getNumeroFabbrica());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(numeroFabb);
							firstColumnOfData++;
	
							//TIPO FABBRICA
							String tipoFabb = this.clean(is.getTipoFabb());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(tipoFabb);
							firstColumnOfData++;
	
							//NUMERO RADIOCOMANDO
							String numeroRadio = this.clean(is.getNumRadioc());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(numeroRadio);
							firstColumnOfData++;
	
							//TELAIO
							String telaio = this.clean(is.getTelaio());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(telaio);
							firstColumnOfData++;
	
							//STATO IMPIANTO
							String statoImp = (is.getStatoImpianto()!=null ? this.clean(is.getStatoImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(statoImp);
							firstColumnOfData++;
	
							//NOTE
							String note = this.clean(is.getNote());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(note);
							firstColumnOfData++;
	
							//Impianti elettrici	
						} else if("05".equals(impType)){
							ImpTerra it = (ImpTerra)imp;
	
							//TIPO IMPIANTO ELETTRICO
							String subTypeTerra = (it.getSubTypeTerra()!=null ? this.clean(it.getSubTypeTerra().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(subTypeTerra);
							firstColumnOfData++;
	
							//STRUTTURE AUTOPROTETTE
							String struttureAutop = (it.getTipologia()!=null ? ("48".equals(it.getTipologia().getCode())?"Si":"No") : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(struttureAutop);
							firstColumnOfData++;
	
							//SOTTOTIPO B
							String sotttipoB = (it.getSubTypeB()!=null ? this.clean(it.getSubTypeB().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(sotttipoB);
							firstColumnOfData++;
	
							//POTENZA (KWATT)
							String potenza = (it.getPot()!=null ? this.clean(it.getPot().toString()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(potenza);
							firstColumnOfData++;
	
	
							//STATO IMPIANTO
							String statoImp = (it.getStatoImpianto()!=null ? this.clean(it.getStatoImpianto().getCurrentTranslation()) : "");
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(statoImp);
							firstColumnOfData++;
	
							//NOTE
							String note = this.clean(it.getNote());
							//xlsxCell = row.createCell(firstColumnOfData);
				            //xlsxCell.setCellType(null);
				            //xlsxCell.setCellValue(note);
							firstColumnOfData++;
						}
					}
					
					writexlsxCellsFromArray(cellsValues.toArray(), sheet, firstRowOfData);
					log.info("riga: "+firstRowOfData);
					firstRowOfData++;
	
				}
			}

			//wb.write(output);
			wb.write(out);
			//wb.close();
			//output.flush();
			//output.close();
			out.close();
			idoc.setContent(out.toString().getBytes());
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private void writexlsxCellsFromArray(Object[] rowCells, XSSFSheet sheet, int rowIndex, boolean hasIdData) throws RowsExceededException, WriteException {
		if (rowCells != null) {
			XSSFRow row = sheet.createRow(rowIndex);

			// loop on all row objects but id column
			for (int colIndex=0;(colIndex+(hasIdData ? 1 : 0))<rowCells.length;colIndex++) {
				Object cell = rowCells[colIndex+(hasIdData ? 1 : 0)];
				XSSFCell xlsxCell = row.createCell(colIndex);
	            xlsxCell.setCellType(XSSFCell.CELL_TYPE_STRING);
	            xlsxCell.setCellValue((String)cell);
			}
		}
	}

	private void writexlsxCellsFromArray(Object[] rowCells, XSSFSheet sheet, int rowIndex) throws RowsExceededException, WriteException {
		writexlsxCellsFromArray(rowCells, sheet, rowIndex, false);
	}
*/
}