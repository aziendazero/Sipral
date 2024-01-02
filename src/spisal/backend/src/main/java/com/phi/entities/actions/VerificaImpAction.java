package com.phi.entities.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;
import com.phi.entities.baseEntity.SchedaRecipientiIndiv;
import com.phi.entities.baseEntity.SchedaTubazioniIndiv;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("VerificaImpAction")
@Scope(ScopeType.CONVERSATION)
public class VerificaImpAction extends BaseAction<VerificaImp, Long> {

	private static final long serialVersionUID = 48307896L;
	private static final Logger log = Logger.getLogger(VerificaImpAction.class); 

	private boolean subProcessReadOnly;

	public static VerificaImpAction instance() {
		return (VerificaImpAction) Component.getInstance(VerificaImpAction.class, ScopeType.CONVERSATION);
	}

	public boolean isSubProcessReadOnly() {
		return subProcessReadOnly;
	}

	public void setSubProcessReadOnly(boolean subProcessReadOnly) {
		this.subProcessReadOnly = subProcessReadOnly;
	}

	public void setDefaultTipoProva(){
		VerificaImp v = getEntity();

		if(v == null)
			return;

		if(v.getImpRiscCpy()!=null){
			try {
				List<CodeValuePhi> tipoProva = v.getTipoProva();
				if(tipoProva==null)
					tipoProva = new ArrayList<CodeValuePhi>();

				Vocabularies voc = VocabulariesImpl.instance();
				CodeValuePhi codeValueProva = (CodeValuePhi)voc.getCodeValue("PHIDIC", "TipoProva02", "01", "C");
				tipoProva.add(codeValueProva);
				v.setTipoProva(tipoProva);
			} catch (Exception ex) {
				log.error(ex);
				throw new RuntimeException(ex);
			}
		}
	}

	public void setNextVerifDates(){
		VerificaImp v = getEntity();

		if(v == null)
			return;

		Date currentVerifDate = v.getData();

		if(currentVerifDate == null)
			return;

		Context conversationContext = Contexts.getConversationContext();
		Impianto copy = (Impianto) conversationContext.get("ImpCopy");

		if(copy == null)
			return;

		Calendar cal = Calendar.getInstance();
		cal.setTime(v.getData());

		if(v.getImpPressCpy()!=null){
			cal.add(Calendar.YEAR, 1);

			if(v.getIdraulica()!=null){
				if(v.getIdraulica()==true)
					v.setNextVerifDate1(cal.getTime());
			}

			if(v.getInterna()!=null){
				if(v.getInterna()==true)
					v.setNextVerifDate2(cal.getTime());
			}

			if(v.getEsercizio()!=null){
				if(v.getEsercizio()==true)
					v.setNextVerifDate3(cal.getTime());
			}			

		}else if(v.getImpRiscCpy()!=null){
			cal.add(Calendar.YEAR, 5);
			v.setNextVerifDate1(cal.getTime());

		}else if(v.getImpMontaCpy()!=null){
			cal.add(Calendar.YEAR, 2);
			v.setNextVerifDate1(cal.getTime());

		}else if(v.getImpSollCpy()!=null){
			cal.add(Calendar.YEAR, 1);
			v.setNextVerifDate1(cal.getTime());

		}else if(v.getImpTerraCpy()!=null){
			cal.add(Calendar.YEAR, 2);
			v.setNextVerifDate1(cal.getTime());

		}

	}

	public void setSedeAddebito(Impianto imp){
		if(imp == null)
			return;

		VerificaImp ver = getEntity();

		//Se non è stata già settata una sede di addebito, associa alla verifica la sede di addebito dell'impianto
		/* I0070276 sede addebito sostituita da sede con flag addebito
		if (ver.getSedeAddebito()==null)
			ver.setSedeAddebito(imp.getSedeAddebito());
		 */
		if (ver.getSedi()==null)
			ver.setSedi(imp.getSedi());		
	}

	public void getStatoFromImp(Impianto imp){
		if(imp == null)
			return;

		VerificaImp ver = getEntity();

		String code = imp.getCode().getCode();

		if("01".equals(code)){
			ver.setStatoImp(((ImpPress)imp).getStatoImpianto());

		}else if("02".equals(code)){
			ver.setStatoImp(((ImpRisc)imp).getStatoImpianto());

		}else if("03".equals(code)){
			ver.setStatoImp(((ImpMonta)imp).getStatoImpianto());

		}else if("04".equals(code)){
			ver.setStatoImp(((ImpSoll)imp).getStatoImpianto());

		}else if("05".equals(code)){
			ver.setStatoImp(((ImpTerra)imp).getStatoImpianto());
		}
	}

	public String getTipoProva(VerificaImp ver){
		String ret="";

		if (ver==null)
			return ret;

		try {
			//Impianti a pressione
			if (ver.getImpPress()!=null){
				if (ver.getIdraulica()!=null && ver.getIdraulica())
					ret+="Integrità ";
				if (ver.getInterna()!= null && ver.getInterna())
					ret+="Interna ";
				if (ver.getEsercizio()!=null && ver.getEsercizio())
					ret+="Esercizio";

				//Impianti a di riscaldamento
			} else if (ver.getImpRisc()!=null && ver.getTipoProva()!=null){
				ret+=ver.getTipoProva().toString(); 
				ret=ret.replace("[", "");
				ret=ret.replace("]", "");
				ret=ret.replace(", ", " "); 
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

		return ret;

	}

	public void cleanTemporaryDateLastNext(){
		Vocabularies voc = VocabulariesImpl.instance();
		List<CodeValue> tipiProva = new ArrayList<CodeValue>();

		try {
			tipiProva = voc.getCodeValues("PHIDIC", "TipoProva");
			List<CodeValue> tipiProva2 = voc.getCodeValues("PHIDIC", "TipoProva02");
			tipiProva.addAll(tipiProva2);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		HashMap<String, Object> tmp = getTemporary();
		for(CodeValue cv : tipiProva) {
			if (tmp.containsKey("last"+cv.getDisplayName())){
				tmp.remove("last"+cv.getDisplayName());
			}
			if (tmp.containsKey("next"+cv.getDisplayName())){
				tmp.remove("next"+cv.getDisplayName());
			}
			if (tmp.containsKey("nextDate")){
				tmp.remove("nextDate");
			}
		}
	}

	public String getImpiantoCode(VerificaImp v) {
		if (v.getImpPress() != null)
			return "01";

		if (v.getImpRisc() != null)
			return "02";

		if (v.getImpMonta() != null)
			return "03";

		if (v.getImpSoll() != null)
			return "04";

		if (v.getImpTerra() != null)
			return "05";

		return "";

	}

	public void injectLastPerType(){ 
		cleanTemporaryDateLastNext();

		Context conv = Contexts.getConversationContext(); 
		List<VerificaImp> listVerifiche = new ArrayList<VerificaImp>();


		if (conv.get("VerificaImpList") != null){
			listVerifiche = ((PhiDataModel) conv.get("VerificaImpList")).getList();
		}

		//calcola date ultime prove: prendo la più recente di ogni tipo, 
		//cosiderando che una veriifca può esser per più tipi di prova
		//saltando quelle in stato new.

		HashMap<String, Date> lastVerifiche = new HashMap<String, Date>();
		for (VerificaImp v : listVerifiche) {
			if (v.getStatusCode().getCode().equals("new")) {
				continue;
			}

			List<CodeValuePhi> tipiProva = v.getTipoProva();
			Date dataProva = v.getData();
			if (dataProva!=null){
				if (dataProva!=null && tipiProva != null && !tipiProva.isEmpty()) {
					for (CodeValuePhi tipo : tipiProva) {
						String tipoProva = tipo.getDisplayName();
						if (!lastVerifiche.containsKey(tipoProva)){
							lastVerifiche.put(tipoProva,dataProva);
							continue;
						}
						if (lastVerifiche.get(tipoProva).before(dataProva)){
							lastVerifiche.put(tipoProva, dataProva);
							continue;
						}
					}
				}
				else {
					//Verifica senza specifica tipo prova
					String codeImpainto  = getImpiantoCode(v);

					if (!lastVerifiche.containsKey(codeImpainto)){
						lastVerifiche.put(codeImpainto,dataProva);
					}
					else if (lastVerifiche.get(codeImpainto).before(dataProva)){
						lastVerifiche.put(codeImpainto, dataProva);

					}
				}
			}
		}

		Calendar c = Calendar.getInstance();
		List<CodeValue> tipiProve = new ArrayList<CodeValue>();
		List<CodeValue> tipiProve2 = new ArrayList<CodeValue>();
		Vocabularies voc = VocabulariesImpl.instance();
		Integer anni03 =null, anni04 = null, anni05 = null;

		try {
			tipiProve = voc.getCodeValues("PHIDIC", "TipoProva");
			tipiProve2 = voc.getCodeValues("PHIDIC", "TipoProva02");
			anni03 = ((CodeValuePhi)voc.getCodeValueOid("phidic.arpav.imp.imptype.03")).getScore();
			anni04 = ((CodeValuePhi)voc.getCodeValueOid("phidic.arpav.imp.imptype.04")).getScore();
			anni05 = ((CodeValuePhi)voc.getCodeValueOid("phidic.arpav.imp.imptype.05")).getScore();
			tipiProve.addAll(tipiProve2);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//anni di differenza per ciascun tipo di verifica.
		HashMap<String, Integer> anni = new HashMap<String, Integer>();
		for (CodeValue cv : tipiProve) {
			anni.put(cv.getDisplayName(), ((CodeValuePhi)cv).getScore());
		}

		//set default per chi non usa il tipo
		anni.put("03", anni03 != null ? anni03 : 2); //montacarichi
		anni.put("04", anni04 != null ? anni04 : 2); //sollevamento
		anni.put("05", anni04 != null ? anni05 : 2);  //elettrici

		//calcolo la verifica necessara più vicina, tra quelle calcolate come prossime verifiche.
		Date minNextDate = null;
		for (String tipiProveOImpianto : lastVerifiche.keySet()){
			int n =1;
			if (anni.containsKey(tipiProveOImpianto) && anni.get(tipiProveOImpianto)!= null){
				n = anni.get(tipiProveOImpianto);
			}

			getTemporary().put("last"+tipiProveOImpianto, lastVerifiche.get(tipiProveOImpianto));
			c.setTime(lastVerifiche.get(tipiProveOImpianto));
			c.add(Calendar.YEAR, n);
			getTemporary().put("next"+tipiProveOImpianto, c.getTime());
			if (minNextDate == null || c.getTime().before(minNextDate)){
				minNextDate = c.getTime();
			}
		}

		getTemporary().put("nextDate", minNextDate);
	}

	public boolean checkVerifica(String impType){
		VerificaImp ver = getEntity();

		if(ver!=null && impType!=null && !"".equals(impType)){

			/* Effettua il controllo sul campo Ore/Minuti per le sole verifiche interne */
			String tipoInOut = null;
			CodeValuePhi tipoInOutCv = ver.getTipoInOut();

			if (tipoInOutCv!=null)
				tipoInOut = tipoInOutCv.getCode();

			if ("01".equals(tipoInOut)){
				boolean isValid = checkOreMinuti(impType, ver);

				//Se non viene settata la sede ARPAV non consente la validazione 
				if (ver.getServiceDeliveryLocation()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV obbligatoria.", "Sede ARPAV obbligatoria.");
					isValid = false;
				}

				if (!isValid)
					return false;
			}


			/* I0070276 sede addebito sostituita da sede con flag addebito
			//Controlla che nella verifica sia settata la sede di addebito
			if (ver.getSedeAddebito()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sede Addebito obbligatoria.", "Sede Addebito obbligatoria.");
				return false;
			}
			 */
			if (ver.getSedi()==null || ver.getSedi().getSedeAddebito()==null || ver.getSedi().getSedeAddebito()==false){
				FacesErrorUtils.addErrorMessage("commError", "Sede Addebito obbligatoria.", "Sede Addebito obbligatoria.");
				return false;
			}

			//Controlla che nell'impianto sia settata la sede di istallazione 
			/*if("01".equals(impType)){
				ImpPress ip = ver.getImpPress();

				if (ip!=null && ip.getSedeInstallazione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede di istallazione obbligatoria.", "Sede di istallazione obbligatoria.");
					return false;
				}
			} else if("02".equals(impType)){
				ImpRisc ir = ver.getImpRisc();

				if (ir!=null && ir.getSedeInstallazione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede di istallazione obbligatoria.", "Sede di istallazione obbligatoria.");
					return false;
				}
			} else if("03".equals(impType)){
				ImpMonta im = ver.getImpMonta();

				if (im!=null && im.getSedeInstallazione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede di istallazione obbligatoria.", "Sede di istallazione obbligatoria.");
					return false;
				}
			} else if("04".equals(impType)){
				ImpSoll is = ver.getImpSoll();

				if (is!=null && is.getSedeInstallazione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede di istallazione obbligatoria.", "Sede di istallazione obbligatoria.");
					return false;
				}
			} else if("05".equals(impType)){
				ImpTerra it = ver.getImpTerra();

				if (it!=null && it.getSedeInstallazione()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede di istallazione obbligatoria.", "Sede di istallazione obbligatoria.");
					return false;
				}				
			}*/

			//Se l'impianto è in stato domolito non fa nessun controllo sulla data prossima verifica
			if (ver.getStatoImp()!=null && "08".equals(ver.getStatoImp().getCode()))
				return true;

			//Escludendo gli impianti a pressione, se non è stata inserita la data prossima verifica non consente la validazione 
			if(!"01".equals(impType) && ver.getNextVerifDate1()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data prossima verifica obbligatoria.", "Data prossima verifica obbligatoria.");
				return false;
			} 

			//Per gli impianti a pressione, se non è stata inserita almeno una data prossima verifica non consente la validazione
			if ("01".equals(impType) && ver.getNextVerifDate1()==null) {
				if (ver.getNextVerifDate2()==null && ver.getNextVerifDate3()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Almeno una data prossima verifica obbligatoria.", "Almeno una data prossima verifica obbligatoria.");
					return false;
				}
			}
		}

		return true;
	}

	/* Nelle verifiche esterne, solo per quelle associate ad impianti a pressione, 
	 * deve scattare il controllo sull'obbligatorietà di almeno una data ultima verifica */
	public boolean checkVerificaEsterna(String impType){
		VerificaImp ver = getEntity();

		if(ver!=null && impType!=null && !"".equals(impType)){

			//Se non è una verifica esterna
			if (ver.getTipoInOut()==null || !"02".equals(ver.getTipoInOut().getCode()))
				return true;

			//Se non è un impianto a pressione
			if (!"01".equals(impType))
				return true;

			//Per gli impianti a pressione, se non è stata inserita almeno una data prossima verifica non consente la validazione
			if (ver.getNextVerifDate1()==null) {
				if (ver.getNextVerifDate2()==null && ver.getNextVerifDate3()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Almeno una data prossima verifica obbligatoria.", "Almeno una data prossima verifica obbligatoria.");
					return false;
				}
			}
		}

		return true;
	}

	/* Aggiorna gli attributi dell'impianto se la verifica oggetto di manipolazione è l'ultima interna validata 
	 * oppure se è l'ultima verifica esterna.
	 * 
	 * Questa funzione viene chiamata ogni volta che si valida una verifica interna ed ogni volta che si salva 
	 * una verifica esterna (di default in stato validato) 
	 *  
	 * Il parametro di input (verificaLast) viene messo in conversation ogni volta che accede ad una verifica 
	 * dall'anagrafica verifiche ed ogni volta che si accede ad un impianto - vedi VerificaLastAction */
	public void updateImpianto(VerificaImp verificaLast){
		this.updateImpianto(verificaLast, null);
	}

	public void prepareAndDelete(){
		try{
			VerificaImp ver = getEntity();

			if (ver==null)
				return;

			ver.setImpPress(null);
			ver.setImpPressCpy(null);

			ver.setImpMonta(null);
			ver.setImpMontaCpy(null);

			ver.setImpSoll(null);
			ver.setImpSollCpy(null);

			ver.setImpRisc(null);
			ver.setImpRiscCpy(null);

			ver.setImpTerra(null);
			ver.setImpTerraCpy(null);

			ver.setOperatore(null);
			ver.setServiceDeliveryLocation(null);

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			ca.delete(ver);
			ca.flushSession();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void cleanDates(){
		try{
			VerificaImp ver = getEntity();

			if (ver==null)
				return;

			if (ver.getIdraulica()!=null && !ver.getIdraulica())
				ver.setNextVerifDate1(null);

			if (ver.getInterna()!=null && !ver.getInterna())
				ver.setNextVerifDate2(null);

			if (ver.getEsercizio()!=null && !ver.getEsercizio())
				ver.setNextVerifDate3(null);

			setNextVerifDates();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setArpav() {
		try {
			getEqual().remove("serviceDeliveryLocation.internalId");
			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				Object arpav_id = temp.get("selectedARPAV");

				if (arpav_id != null) {
					Long id = Long.parseLong(arpav_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.internalId", id);
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//Aggiorna il collettore ogni volta che la verifica viene salvata
	public void updateCollector(Impianto imp) {
		try{
			VerificaImp ver = getEntity();
			ImpSearchCollector coll = ver.getImpSearchCollector();

			if (ver==null || imp==null || imp.getCode()==null)
				return;

			if (coll==null){
				coll = new ImpSearchCollector();
				ca.create(coll);
				ver.setImpSearchCollector(coll);
			}

			if (coll.getInternalId()==0)
				return;

			CodeValuePhi codeCv = imp.getCode();
			String code = codeCv.getCode();

			coll.setCode(codeCv);
			coll.setSigla(imp.getSigla());
			coll.setMatricola(imp.getMatricola());
			coll.setAnno(imp.getAnno());

			if (imp.getSedeInstallazione()!=null)
				coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());
			else 
				coll.setDenominazioneSI("");

			/* I0070276 sede addebito sostituita da sede con flag addebito
			if (imp.getSedeAddebito()!=null)
				coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());
			 */
			if (imp.getSedi()!=null)
				coll.setDenominazioneSA(imp.getSedi().getDenominazioneUnitaLocale());
			else 
				coll.setDenominazioneSA("");

			if(imp.getIndirizzoSped()!=null)
				coll.setDenominazioneIS(imp.getIndirizzoSped().getDenominazione());
			else 
				coll.setDenominazioneIS("");

			if (!"".equals(code)){

				//Apparecchi a pressione
				if ("01".equals(code))
					coll.setNumeroFabbrica(((ImpPress)imp).getNumeroFabbrica());
				else 
					coll.setNumeroFabbrica("");

				//Impianti di riscaldamento
				//else if ("02".equals(code))

				//Ascensori e montacarichi
				//else if ("03".equals(code))

				//Apparecchi di sollevamento	
				if ("04".equals(code))
					coll.setSubTypeSoll(((ImpSoll)imp).getSubTypeSoll());
				else 
					coll.setSubTypeSoll(null);

				//Impianti elettrici	
				if ("05".equals(code))
					coll.setSubTypeTerra(((ImpTerra)imp).getSubTypeTerra());
				else
					coll.setSubTypeTerra(null);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setTypes() {
		try {
			List<String> types = new ArrayList<String>();

			//Fake code
			types.add("phidic.arpav.imp.imptype.00_V0");

			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();
			if (!temp.isEmpty()) {
				Object all = temp.get("allTypes");
				Boolean allTypes = (all!=null && (Boolean)all);

				//Apparecchi a pressione
				Object press = temp.get("01");
				if ((press!=null && (Boolean)press) || allTypes)
					types.add("phidic.arpav.imp.imptype.01_V0");

				//Impianti di riscaldamento
				Object risc = temp.get("02");
				if ((risc!=null && (Boolean)risc) || allTypes)
					types.add("phidic.arpav.imp.imptype.02_V0");

				//Ascensori e montacarichi
				Object asc = temp.get("03");
				if ((asc!=null && (Boolean)asc) || allTypes)
					types.add("phidic.arpav.imp.imptype.03_V0");

				//Apparecchi di sollevamento
				Object soll = temp.get("04");
				if ((soll!=null && (Boolean)soll) || allTypes)
					types.add("phidic.arpav.imp.imptype.04_V0");

				//Impianti elettrici - ex messa a terra
				Object terr = temp.get("05");
				if ((terr!=null && (Boolean)terr) || allTypes)
					types.add("phidic.arpav.imp.imptype.05_V0");				
			}

			((FilterMap)getEqual()).putOr("impSearchCollector.code.id", types.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setSubTypes() {
		try {

			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();
			List<String> subTypes = new ArrayList<String>();

			if (!temp.isEmpty()) {
				Boolean press	= temp.get("01")==null?false:(Boolean)temp.get("01");
				Boolean risc 	= temp.get("02")==null?false:(Boolean)temp.get("02");
				Boolean asc 	= temp.get("03")==null?false:(Boolean)temp.get("03");
				Boolean soll 	= temp.get("04")==null?false:(Boolean)temp.get("04");
				Boolean terr 	= temp.get("05")==null?false:(Boolean)temp.get("05");

				//Se sono stati selezionati solo apparecchi di sollevamento
				if (!press && !risc && !asc && soll && !terr){

					if (temp.get("D")==null?false:(Boolean)temp.get("D"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.01_V0");

					if (temp.get("E")==null?false:(Boolean)temp.get("E"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.02_V0");

					if (temp.get("F")==null?false:(Boolean)temp.get("F"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.03_V0");

					if (temp.get("G")==null?false:(Boolean)temp.get("G"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.04_V0");

					if (temp.get("H")==null?false:(Boolean)temp.get("H"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.05_V0");

					if (temp.get("I")==null?false:(Boolean)temp.get("I"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.06_V0");

					if (temp.get("L")==null?false:(Boolean)temp.get("L"))
						subTypes.add("phidic.arpav.imp.sollevamento.type1.07_V0");

					if (subTypes.size()>0)
						((FilterMap)getEqual()).putOr("impSearchCollector.subTypeSoll.id", subTypes.toArray());

				} else //Se sono stati selezionati soli impianti elettrici (ex Terra)
					if (!press && !risc && !asc && !soll && terr){

						if (temp.get("A")==null?false:(Boolean)temp.get("A"))
							subTypes.add("phidic.arpav.imp.terra.type01.01_V0");

						if (temp.get("B")==null?false:(Boolean)temp.get("B"))
							subTypes.add("phidic.arpav.imp.terra.type01.02_V0");

						if (temp.get("C")==null?false:(Boolean)temp.get("C"))
							subTypes.add("phidic.arpav.imp.terra.type01.03_V0");

						if (subTypes.size()>0)
							((FilterMap)getEqual()).putOr("impSearchCollector.subTypeTerra.id", subTypes.toArray());
					}
			}

			if (subTypes.size()==0) {
				((FilterMap)getEqual()).remove("impSearchCollector.subTypeSoll.id");
				((FilterMap)getEqual()).remove("impSearchCollector.subTypeTerra.id");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setTestTypes(){
		HashMap<String, Object> tempImp = ImpiantoAction.instance().getTemporary();
		HashMap<String, Object> tempVer = getTemporary();

		//tempVer.remove("idraulica");
		//tempVer.remove("interna");
		//tempVer.remove("esercizio");
		//equal.remove("idraulica");
		//equal.remove("interna");
		//equal.remove("esercizio");

		Boolean press	= tempImp.get("01")==null?false:(Boolean)tempImp.get("01");
		Boolean risc 	= tempImp.get("02")==null?false:(Boolean)tempImp.get("02");
		Boolean asc 	= tempImp.get("03")==null?false:(Boolean)tempImp.get("03");
		Boolean soll 	= tempImp.get("04")==null?false:(Boolean)tempImp.get("04");
		Boolean terr 	= tempImp.get("05")==null?false:(Boolean)tempImp.get("05");

		if(press && !risc && !asc && !soll && !terr){

			Boolean idr	    = tempVer.get("idraulica")==null?false:(Boolean)tempVer.get("idraulica");
			Boolean intn	= tempVer.get("interna")==null?false:(Boolean)tempVer.get("interna");
			Boolean eser	= tempVer.get("esercizio")==null?false:(Boolean)tempVer.get("esercizio");

			if(!idr && !intn && !eser){
				equal.remove("idraulica");
				equal.remove("interna");
				equal.remove("esercizio");
			}else{
				equal.put("idraulica",idr);			
				equal.put("interna",intn);
				equal.put("esercizio",eser);	
			}
		}
	}

	public void setSediFullLike(){

		removeExpression("this", "ImpSearchCollector.denominazioneSI");
		removeExpression("this", "ImpSearchCollector.denominazioneSA");
		removeExpression("this", "ImpSearchCollector.denominazioneIS");

		HashMap<String, Object> temp = getTemporary();

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		String denominazioneSI = (temp.get("denominazioneSI")!=null)?temp.get("denominazioneSI").toString():"";
		String denominazioneSA = (temp.get("denominazioneSA")!=null)?temp.get("denominazioneSA").toString():"";
		String denominazioneIS = (temp.get("denominazioneIS")!=null)?temp.get("denominazioneIS").toString():"";

		if(findSubCriteria("ImpSearchCollector")==null)
			entityCriteria.createAlias("impSearchCollector", "ImpSearchCollector", Criteria.LEFT_JOIN);

		if (!denominazioneSI.equals("")){
			denominazioneSI = "%" + denominazioneSI + "%";
			entityCriteria.add(Restrictions.like("ImpSearchCollector.denominazioneSI", denominazioneSI).ignoreCase());
		}

		if (!denominazioneSA.equals("")){
			denominazioneSA = "%" + denominazioneSA + "%";
			entityCriteria.add(Restrictions.like("ImpSearchCollector.denominazioneSA", denominazioneSA).ignoreCase());
		}

		if (!denominazioneIS.equals("")){
			denominazioneIS = "%" + denominazioneIS + "%";
			entityCriteria.add(Restrictions.like("ImpSearchCollector.denominazioneIS", denominazioneIS).ignoreCase());
		}
	}

	public void resetTestTypes(){
		HashMap<String, Object> tempVer = getTemporary();

		tempVer.remove("idraulica");
		tempVer.remove("interna");
		tempVer.remove("esercizio");

		equal.remove("idraulica");
		equal.remove("interna");
		equal.remove("esercizio");		
	}

	public void setCodiceConto(VerificaImp v) {
		try {
			String code = this.getImpiantoCode(v); 			
			String codiceConto = "";

			if ("".equals(code))
				return;

			Impianto imp =null;

			// Esenzione: il codice conto è null
			if(this.isEsente(v)){
				v.setCodiceConto(null);
				return;
			}

			//Verifica su impianto di pressione
			if ("01".equals(code)) {
				imp = v.getImpPressCpy();

				if (imp!=null){
					Sedi sa = imp.getSedi();
					CodeValuePhi esenzioneCv = null;
					if(sa != null){
						esenzioneCv = sa.getEsenzione();
					}
					if(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid())){
					// Sede addebito esente IVA
						codiceConto = this.codiceConto1(sa);
					}else{

						String tipoApparecchio = null;

						CodeValuePhi tipoApparecchioCv = ((ImpPress)imp).getTipoApparecchio();

						if (tipoApparecchioCv!=null)
							tipoApparecchio = tipoApparecchioCv.getCode();

						if (tipoApparecchio!=null && "08".equals(tipoApparecchio))
							codiceConto = this.codiceConto1(sa);
						else 
							codiceConto = this.codiceConto2(sa);
					}
				}

				//Verifica su impianto di riscaldamento
			} else if ("02".equals(code)) {
				imp = v.getImpRiscCpy();

				if (imp!=null){
					Sedi sa = imp.getSedi();
					CodeValuePhi esenzioneCv = null;
					if(sa != null){
						esenzioneCv = sa.getEsenzione();
					}
					if(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid())){
					// Sede addebito esente IVA
						codiceConto = this.codiceConto1(sa);
					}else{
						String descrizImpianto = null;

						/* descrizImpianto:
						 * 01 Riscaldamento ambienti
						 * 02 Acqua calda per servizi
						 * 03 Riscaldamento e servizi
						 * 04 Acqua calda per fini produttivi */
						CodeValuePhi descrizImpiantoCv = ((ImpRisc)imp).getDescrizImpianto();

						if (descrizImpiantoCv!=null)
							descrizImpianto = descrizImpiantoCv.getCode();

						if (descrizImpianto!=null && "01 02 03".contains(descrizImpianto))
							codiceConto = this.codiceConto1(sa);
						else 
							codiceConto = this.codiceConto2(sa);
					}
				}

				//Verifica su ascensori e montacarichi
			} else if ("03".equals(code)) {
				imp = v.getImpMontaCpy();

				if (imp!=null){
					Sedi sa = imp.getSedi();
					CodeValuePhi esenzioneCv = null;
					if(sa != null){
						esenzioneCv = sa.getEsenzione();
					}
					if(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid())){
					// Sede addebito esente IVA
						codiceConto = this.codiceConto1(sa);
					}else{					
						codiceConto = this.codiceConto2(sa);
					}
				}

				//Verifica su impianto di sollevamento	
			} else if ("04".equals(code)) {
				imp = v.getImpSollCpy();

				if (imp!=null){
					Sedi sa = imp.getSedi();
					CodeValuePhi esenzioneCv = null;
					if(sa != null){
						esenzioneCv = sa.getEsenzione();
					}
					if(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid())){
					// Sede addebito esente IVA
						codiceConto = this.codiceConto1(sa);
					}else{
						codiceConto = this.codiceConto2(sa);
					}
				}

				//Verifica su impianto elettrico
			} else if ("05".equals(code)) {
				imp = v.getImpTerraCpy();

				if (imp!=null){
					Sedi sa = imp.getSedi();
					CodeValuePhi esenzioneCv = null;
					if(sa != null){
						esenzioneCv = sa.getEsenzione();
					}
					if(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid())){
					// Sede addebito esente IVA
						codiceConto = this.codiceConto1(sa);
					}else{
						String tipoTerra = null;

						//Tipo c e prima verifica

						/* 01 - A - Inst. di prot. scariche atm.
						 * 02 - B - Impianti di messa a terra
						 * 03 - C - Ins. elett. In luoghi peric. */
						CodeValuePhi tipoTerraCv = imp.getSubTypeTerra();

						if (tipoTerraCv!=null){
							tipoTerra = tipoTerraCv.getCode();

							if (tipoTerra!=null && "03".equals(tipoTerra) && (v.getPrima()!= null? v.getPrima():false))
								codiceConto = this.codiceConto2(sa);
							else 
								codiceConto = this.codiceConto2(sa);
						} else
							codiceConto = this.codiceConto2(sa);
					}
				}
			}

			v.setCodiceConto(codiceConto);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setCausale(VerificaImp v) {
		try {
			/* Da rivedere dopo aver uniformato il discorso su verifica straordinaria */

			String code = null;
			/* code - tipoImpianto: 
			 * 01 - Pressione
			 * 02 - Riscaldamento
			 * 03 - Ascensori e montacarichi
			 * 04 - Apparecchi di sollevamento 
			 * 05 - Impianti elettrici */
			code = this.getImpiantoCode(v); 			

			if ("".equals(code))
				return;

			// Esenzione: la causale è null
			if(this.isEsente(v)){
				v.setCausale(null);
				return;
			}

			String tipoAppPress = null;
			if("01".equals(code)){
				ImpPress imp = v.getImpPress();
				CodeValuePhi tipoAppCv = imp.getTipoApparecchio();
				if(tipoAppCv!=null){
					tipoAppPress = tipoAppCv.getCode();
				}
			}

			String tipoImpRisc = null;
			if("02".equals(code)){
				ImpRisc imp = v.getImpRisc();
				CodeValuePhi destinazioneCv = imp.getDescrizImpianto();
				if(destinazioneCv!=null){
					tipoImpRisc = destinazioneCv.getCode();
				}
			}

			String tipoVerifica = null;
			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto 
			 * 05 - Sopralluogo 
			 * 06 - Straordinaria per accertamenti
			 * 07 - Straordinaria per modifiche costruttive*/
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(code))
				return;

			Vocabularies voc = VocabulariesImpl.instance();
			CodeValuePhi causale = null;
			String causaleOid = "phidic.arpav.ver.causale.";
			//Se tipoVerifica: Visita a vuoto -> Causale: 86 - Verifica a vuoto
			if ("04".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.86");
				causaleOid += "86";

			//Se tipoVerifica: Sopralluogo -> Causale: 26 - Sopralluoghi e varie
			else if ("05".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.26");
				causaleOid += "26";

			//Se tipoVerifica: Periodica
			else if ("01".equals(tipoVerifica)){
				//Se tipoImpianto: Pressione -> 36 - Quota ann. e prove period.reg.
				if ("01".equals(code))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.36");
					causaleOid += "36";
				else 
					//Se tipoImpianto <> Pressione -> 23 - Verifica periodica
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.23");
					causaleOid += "23";
			}

			//Se tipoVerifica: Straordinaria -> Causale 24 VERIFICA STRAORDINARIA
			else if("02".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.24");
				causaleOid += "24";

			//Se tipoVerifica: Straordinaria per accertamenti
			else if ("06".equals(tipoVerifica)){
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.25");
				causaleOid += "25";
			}

			//modifiche costruttive solo ascensori
			else if ("07".equals(tipoVerifica)){
				if ("03".equals(code))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.04");
					causaleOid += "04";
			}

			if(("01".equals(code) && "08".equals(tipoAppPress)) || ("02".equals(code) && "01 02 03".contains(tipoImpRisc)) ||
					"03 05".contains(code)){
				causaleOid += "R";
			}

			causale = (CodeValuePhi)voc.getCodeValueOid(causaleOid);

			if (causale!=null)
				v.setCausale(causale);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private String codiceConto1(SediInstallazione si) {
		try {

			if (si==null)
				return "3210";

			/* 01 - Condominio
			   02 - Ente del SSN
			   03 - Enti pubblici della regione
			   04 - Enti pubblici fuori regione
			   05 - Privati
			   06 - Altri */
			String tipoUtente = null;

			CodeValuePhi tipoUtenteCv = si.getTipoSede();
			if (tipoUtenteCv!=null)
				tipoUtente = tipoUtenteCv.getCode();

			if (tipoUtente==null || "".equals(tipoUtente))
				return "3210";
			else if (tipoUtente != null && "01 05 06".contains(tipoUtente))
				return "3218";
			else if ("02".equals(tipoUtente))
				return "3215";
			else if ("03".equals(tipoUtente))
				return "3216";
			else if ("04".equals(tipoUtente))
				return "3217";

			return "3210";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private String codiceConto2(SediInstallazione si) {
		try {
			if (si==null)
				return "3230";

			/* 01 - Condominio
			   02 - Ente del SSN
			   03 - Enti pubblici della regione
			   04 - Enti pubblici fuori regione
			   05 - Privati
			   06 - Altri */
			String tipoUtente = null;

			CodeValuePhi tipoUtenteCv = si.getTipoSede();
			if (tipoUtenteCv!=null)
				tipoUtente = tipoUtenteCv.getCode();

			if (tipoUtente==null || "".equals(tipoUtente))
				return "3230";
			else if (tipoUtente!=null && "01 05 06".contains(tipoUtente))
				return "3234";
			else if ("02".equals(tipoUtente))
				return "3231";
			else if ("03".equals(tipoUtente))
				return "3232";
			else if ("04".equals(tipoUtente))
				return "3233";

			return "3230";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/* Se l'importo è stato settato manualmente, recupera e converte il valore settato nel temporary
	 * per settalo nell'attributo importo */
	public Boolean setImportoManuale(VerificaImp v) {
		try {
			Boolean manuale = v.getImpManuale()!=null?v.getImpManuale():false;
			if (manuale){				
				HashMap<String, Object> tmp = getTemporary();
				if (tmp.containsKey("impTemp") && tmp.get("impTemp")!=null){

					String impTemp = tmp.get("impTemp").toString();

					if (impTemp!=null){
						v.setImporto(Double.parseDouble(impTemp));
						return true;
					}
				}
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean setImporto(VerificaImp v) {
		try {
			Boolean manuale = v.getImpManuale()!=null?v.getImpManuale():false;

			if (manuale)
				return this.setImportoManuale(v);

			Double importo = null;

			/* ECCEZIONE - Se si tratta di un sopralluogo la tariffazione è oraria 
			Boolean sopralluogo = false; 

			String tipoVerifica = null;			
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null){
				tipoVerifica = tipoVerificaCv.getCode();

				if (tipoVerifica!=null && tipoVerifica.equals("05"))
					sopralluogo = true;
			}

			if (sopralluogo) {
				Double tariffaOraria = null;

				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria, ma bisogna accedere al foglio giusto in relazione al tipo impianto
				if (min>0){
					Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 4);

					if (sheet==null){
						FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
						return false;
					}

					Double val = getCellValue(sheet.getRow(3).getCell(3));

					if (val!=null)
						tariffaOraria = val*min/60;
				} else {
					FacesErrorUtils.addErrorMessage("commError", "Impossibile calcolare la tariffa oraria.", "Impossibile calcolare la tariffa oraria.");
					return false;
				}

				v.setImporto(tariffaOraria);
				return true;
			}
			/* FINE ECCEZIONE */

			String code = this.getImpiantoCode(v); 
			boolean ret = false;

			if (code==null || "".equals(code)){
				FacesErrorUtils.addErrorMessage("commError", "Tipologia impianto non nota.", "Tipologia impianto non nota.");
				return ret;
			}

			//Verifica su impianto di pressione
			if ("01".equals(code)) {
				importo = this.getImportoImpPress(v);

				if (importo!=null){
					v.setImporto(importo);
					ret = true;
				}

				//Verifica su impianto di riscaldamento
			} else if ("02".equals(code)) {
				importo = this.getImportoImpRisc(v);

				if (importo!=null){
					v.setImporto(importo);
					ret = true;
				}

				//Verifica su ascensori e montacarichi
			} else if ("03".equals(code)) {
				importo = this.getImportoImpMonta(v);

				if (importo!=null){
					v.setImporto(importo);
					ret = true;
				}

				//Verifica su impianto di sollevamento	
			} else if ("04".equals(code)) {
				importo = this.getImportoImpSoll(v);

				if (importo!=null){
					v.setImporto(importo);
					ret = true;
				}

				//Verifica su impianto elettrico
			} else if ("05".equals(code)) {
				importo = this.getImportoImpTerra(v);

				if (importo!=null){
					v.setImporto(importo);
					ret = true;
				}
			}

			// Vista la complessità della validazione in caso di esenzione l'importo viene azzerato solo qui
			if(this.isEsente(v)){
				//				BigDecimal bigDecimal = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
				//				String decimalToString = bigDecimal.toPlainString();
				//				v.setImporto(Double.parseDouble(decimalToString));
				v.setImporto(Double.valueOf(0.0D));
				ret = true;
			}

			/* Questo messaggio di errore viene mostrato ogni volta che non è stato possibile calcolare l'importo. */
			if (!ret)
				FacesErrorUtils.addErrorMessage("commError", "Impossibile calcolare l'importo. Verificare le caratteristiche della verifica e dell'impianto.", "Impossibile calcolare l'importo. Verificare le caratteristiche della verifica e dell'impianto.");

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpPress(VerificaImp v) {
		try {
			ImpPress i = v.getImpPressCpy();
			if (i==null)
				return null;

			String tipoVerifica = null;			
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)) {
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}

			String tipoApparecchio = null;

			CodeValuePhi tipoApparecchioCv = i.getTipoApparecchio();

			if (tipoApparecchioCv!=null)
				tipoApparecchio = tipoApparecchioCv.getCode();

			if (tipoApparecchio==null || "".equals(tipoApparecchio)) {
				FacesErrorUtils.addErrorMessage("commError", "Tipo apparecchio non noto.", "Tipo apparecchio non noto.");
				return null;
			}

			/* ECCEZIONE: Se Tipo apparecchio = 08 - REC. PRESS. AMBIENTE VITA --> Foglio 5 (PRESSIONE AMBIENTE VITA) */
			if ("08".equals(tipoApparecchio)){
				return getImportoRecPressAmbienteVita(v, i, tipoVerifica);

				//Per tutti gli altri impianti a pressione	
			} else {

				/* Per tutti i tipi apparecchio != 08 - REC. PRESS. AMBIENTE VITA 
				 * 
				 * Se Tipo verifica:
				 * 
				 * 	01 - Periodica
				 *      Tipo apparecchio:
				 * 		08 - REC. PRESS. AMBIENTE VITA - ECCEZIONE >>---> Vedi sopra
				 * 		
				 * 		Tipo apparecchio:
				 * 		05 - FORNO
				 * 		01 - GENERATORE A VAPORE FISSO
				 * 		02 - GENERATORE A VAPORE SEMIFISSO
				 * 		09 - GENERATORE DI ACETILENE
				 * 		03 - GENERATORE RISCALD. ELETTRICO
				 * 		06 - LOCOMOBILE
				 *		
				 *			SUPERFICIE mq <= Sperficie limite (300) --> Foglio 1 (PRESSIONE sup < X)
				 *   		SUPERFICIE mq >  Sperficie limite (300) --> Foglio 2 (PRESSIONE sup > X)
				 *		
				 *		Tipo apparecchio:
				 *		04 - LINEA ATTR.RE A PRESSIONE --> Foglio 14 (PRESSIONE LINEA ATTREZZ.)
				 *    	
				 *    	Tipo apparecchio:
				 * 		07 - RECIPIENTE PER VAPORE --> Foglio 3 (PRESSIONE REC)
				 * 		10 - REC. PRESS. AMBIENTE LAVORO 
				 * 		
				 * 		Tipo apparecchio:
				 * 		11 - TUBAZIONI --> Foglio 14 (PRESSIONE LINEA ATTREZZ.)
				 * 		
				 *      			
				 * 	02 - Straordinaria  	--> Foglio 4 (VUOTO e STRAORD LAVORO)
				 * 	04 - Visita a vuoto 	--> Foglio 4 (VUOTO e STRAORD LAVORO)
				 *
				 * 	03 - Constatazione --> Eliminato
				 */

				//Tipo verifica: Periodica
				if ("01".equals(tipoVerifica))
					return getImportoImpPress01(v, i, tipoApparecchio, true);

				//Tipo verifica: Straordinaria o Sopralluogo o Visita a vuoto
				else if ("02".equals(tipoVerifica) || "04".equals(tipoVerifica) || "05".equals(tipoVerifica))
					return getImportoImpPress020405(v, i, tipoApparecchio, tipoVerifica);

				//Tipo verifica: Straordinaria per accertamenti
				else if ("06".equals(tipoVerifica))
					return getImportoImpPress06(v, i, tipoApparecchio, tipoVerifica);

				else
					FacesErrorUtils.addErrorMessage("commError", "Regole non note per il tipo verifica selezionato.", "Regole non note per il tipo verifica selezionato.");
			}

			FacesErrorUtils.addErrorMessage("commError", "Regole non note per questa tipologia di apparecchio.", "Regole non note per questa tipologia di apparecchio.");
			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpPress06(VerificaImp v, ImpPress i, String tipoApparecchio, String tipoVerifica) {

		if(tipoApparecchio!=null && "01 02 03 05 06 07 09 10 11".contains(tipoApparecchio)){
			Double tariffaOraria = null;
			Double min = getMinutiServizio(v);

			//Calcolo la tariffa oraria
			if (min>0){
				Double t = 92.90;
				tariffaOraria = t*min/60;
			}

			return tariffaOraria;

		}

		return null;
	}

	//Tipo verifica: periodica (01)
	private Double getImportoImpPress01(VerificaImp v, ImpPress i, String tipoApparecchio, Boolean printError) {
		try {
			Sheet sheet = null;
			Boolean primaVerifica = v.getPrima()!= null? v.getPrima():false;
			Boolean esercizio = v.getEsercizio()!= null? v.getEsercizio():false;
			Boolean idraulica = v.getIdraulica()!= null? v.getIdraulica():false;
			Boolean interna   = v.getInterna()!= null? v.getInterna():false;

			Boolean parteInsiemi = false;

			if (i.getParteInsiemiSoggetti()!=null){// NO YES
				if (i.getParteInsiemiSoggetti().getCode()!=null){
					String code = i.getParteInsiemiSoggetti().getCode();
					if ("YES".equals(code))
						parteInsiemi = true;			
				}
			}

			Double val = null;

			if (tipoApparecchio!=null && "01 02 03 05 06 09".contains(tipoApparecchio)){
				Double superficie = i.getSuperficie();
				if (superficie==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Superficie impianto non nota.", "Superficie impianto non nota.");

					return null;
				}

				if (superficie<=300) {
					sheet = TariffarioAction.instance().getSheet(v.getData(), 1);

					if (sheet==null){
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

						return null;
					}

					if (!primaVerifica){
						if ((esercizio || interna) && !idraulica){
							if (superficie>197)
								//F5
								val = getCellValue(sheet.getRow(4).getCell(5)); 

							else if (superficie>113)
								//F4
								val = getCellValue(sheet.getRow(3).getCell(5));

							else /*if (superficie<=113)*/
								//F3
								val = getCellValue(sheet.getRow(2).getCell(5));

						} else if (esercizio || interna || idraulica){
							if (superficie>197)
								//G5
								val = getCellValue(sheet.getRow(4).getCell(6));
							else if (superficie>113)
								//G4
								val = getCellValue(sheet.getRow(3).getCell(6));
							else /*if (superficie<=113)*/
								//G3
								val = getCellValue(sheet.getRow(2).getCell(6));
						}else if(!esercizio && !interna && !idraulica){
							//Nessuna prova -> Si comporta come sopralluogo (05)
							val = getImportoImpPress020405(v, i, tipoApparecchio, "05");
						} else
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.");


					} else if (primaVerifica){

						if (superficie>197){
							if (parteInsiemi)
								//H5
								val = getCellValue(sheet.getRow(4).getCell(7));
							else 
								//I5
								val = getCellValue(sheet.getRow(4).getCell(8));

						} else if (superficie>113) {
							if (parteInsiemi)
								//H4
								val = getCellValue(sheet.getRow(3).getCell(7));
							else 
								//I4
								val = getCellValue(sheet.getRow(3).getCell(8));

						} else /*if (superficie<=113)*/ { 

							if (parteInsiemi)
								//H3
								val = getCellValue(sheet.getRow(2).getCell(7));
							else 
								//I3
								val = getCellValue(sheet.getRow(2).getCell(8));
						}
					}


				} else if (superficie>300) {
					sheet = TariffarioAction.instance().getSheet(v.getData(), 2);

					if (sheet==null){
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

						return null;
					}

					Double producibilita = i.getProducibilita();
					if (producibilita==null){
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

						return null;
					}

					if (!primaVerifica){
						if ((esercizio || interna) && !idraulica){
							if (producibilita<=12)
								//D4
								val = getCellValue(sheet.getRow(3).getCell(3)); 
							else if  (producibilita<=22)
								//D5
								val = getCellValue(sheet.getRow(4).getCell(3));
							else if  (producibilita<=37)
								//D6
								val = getCellValue(sheet.getRow(5).getCell(3));						
							else if  (producibilita<=60)
								//D7
								val = getCellValue(sheet.getRow(6).getCell(3));
							else if  (producibilita<=90)
								//D8
								val = getCellValue(sheet.getRow(7).getCell(3));
							else if  (producibilita<=132)
								//D9
								val = getCellValue(sheet.getRow(8).getCell(3));
							else if  (producibilita<=186)
								//D10
								val = getCellValue(sheet.getRow(9).getCell(3));
							else if  (producibilita<=255)
								//D11
								val = getCellValue(sheet.getRow(10).getCell(3));
							else if  (producibilita<=342)
								//D12
								val = getCellValue(sheet.getRow(11).getCell(3));
							else if  (producibilita<=448)
								//D13
								val = getCellValue(sheet.getRow(12).getCell(3));
							else if  (producibilita<=579)
								//D14
								val = getCellValue(sheet.getRow(13).getCell(3));
							else if  (producibilita<=735)
								//D15
								val = getCellValue(sheet.getRow(14).getCell(3));
							else if  (producibilita<=921)
								//D16
								val = getCellValue(sheet.getRow(15).getCell(3));
							else if  (producibilita<=1141)
								//D17
								val = getCellValue(sheet.getRow(16).getCell(3));
							else if  (producibilita<=1397)
								//D18
								val = getCellValue(sheet.getRow(17).getCell(3));
							else if  (producibilita>1397)
								//D19
								val = getCellValue(sheet.getRow(18).getCell(3));

						} else if (esercizio || interna || idraulica){

							if (producibilita<=12)
								//E4
								val = getCellValue(sheet.getRow(3).getCell(4)); 
							else if  (producibilita<=22)
								//E5
								val = getCellValue(sheet.getRow(4).getCell(4));
							else if  (producibilita<=37)
								//E6
								val = getCellValue(sheet.getRow(5).getCell(4));						
							else if  (producibilita<=60)
								//E7
								val = getCellValue(sheet.getRow(6).getCell(4));
							else if  (producibilita<=90)
								//E8
								val = getCellValue(sheet.getRow(7).getCell(4));
							else if  (producibilita<=132)
								//E9
								val = getCellValue(sheet.getRow(8).getCell(4));
							else if  (producibilita<=186)
								//E10
								val = getCellValue(sheet.getRow(9).getCell(4));
							else if  (producibilita<=255)
								//E11
								val = getCellValue(sheet.getRow(10).getCell(4));
							else if  (producibilita<=342)
								//E12
								val = getCellValue(sheet.getRow(11).getCell(4));
							else if  (producibilita<=448)
								//E13
								val = getCellValue(sheet.getRow(12).getCell(4));
							else if  (producibilita<=579)
								//E14
								val = getCellValue(sheet.getRow(13).getCell(4));
							else if  (producibilita<=735)
								//E15
								val = getCellValue(sheet.getRow(14).getCell(4));
							else if  (producibilita<=921)
								//E16
								val = getCellValue(sheet.getRow(15).getCell(4));
							else if  (producibilita<=1141)
								//E17
								val = getCellValue(sheet.getRow(16).getCell(4));
							else if  (producibilita<=1397)
								//E18
								val = getCellValue(sheet.getRow(17).getCell(4));
							else if  (producibilita>1397)
								//E19
								val = getCellValue(sheet.getRow(18).getCell(4));
						}else if(!esercizio && !interna && !idraulica){
							//Nessuna prova -> Si comporta come sopralluogo (05)
							val = getImportoImpPress020405(v, i, tipoApparecchio, "05");
						}  else
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.");

					} else if (primaVerifica){

						if (producibilita<=12) {
							if (parteInsiemi)
								//F4
								val = getCellValue(sheet.getRow(3).getCell(5));
							else 
								//G4
								val = getCellValue(sheet.getRow(3).getCell(6));
						} else if  (producibilita<=22) {
							if (parteInsiemi)
								//F5
								val = getCellValue(sheet.getRow(4).getCell(5));
							else 
								//G5
								val = getCellValue(sheet.getRow(4).getCell(6));
						} else if  (producibilita<=37) {
							if (parteInsiemi)
								//F6
								val = getCellValue(sheet.getRow(5).getCell(5));
							else 
								//G6
								val = getCellValue(sheet.getRow(5).getCell(6));					
						} else if  (producibilita<=60) {
							if (parteInsiemi)
								//F7
								val = getCellValue(sheet.getRow(6).getCell(5));
							else 
								//G7
								val = getCellValue(sheet.getRow(6).getCell(6));
						} else if  (producibilita<=90) {
							if (parteInsiemi)
								//F8
								val = getCellValue(sheet.getRow(7).getCell(5));
							else 
								//G8
								val = getCellValue(sheet.getRow(7).getCell(6));
						} else if  (producibilita<=132) {
							if (parteInsiemi)
								//F9
								val = getCellValue(sheet.getRow(8).getCell(5));
							else 
								//G9
								val = getCellValue(sheet.getRow(8).getCell(6));
						} else if  (producibilita<=186) {
							if (parteInsiemi)
								//F10
								val = getCellValue(sheet.getRow(9).getCell(5));
							else 
								//G10
								val = getCellValue(sheet.getRow(9).getCell(6));
						} else if  (producibilita<=255) {
							if (parteInsiemi)
								//F11
								val = getCellValue(sheet.getRow(10).getCell(5));
							else 
								//G11
								val = getCellValue(sheet.getRow(10).getCell(6));
						} else if  (producibilita<=342) {
							if (parteInsiemi)
								//F12
								val = getCellValue(sheet.getRow(11).getCell(5));
							else 
								//G12
								val = getCellValue(sheet.getRow(11).getCell(6));
						} else if  (producibilita<=448) {
							if (parteInsiemi)
								//F13
								val = getCellValue(sheet.getRow(12).getCell(5));
							else 
								//G13
								val = getCellValue(sheet.getRow(12).getCell(6));
						} else if  (producibilita<=579) {
							if (parteInsiemi)
								//F14
								val = getCellValue(sheet.getRow(13).getCell(5));
							else 
								//G14
								val = getCellValue(sheet.getRow(13).getCell(6));
						} else if  (producibilita<=735) {
							if (parteInsiemi)
								//F15
								val = getCellValue(sheet.getRow(14).getCell(5));
							else 
								//G15
								val = getCellValue(sheet.getRow(14).getCell(6));
						} else if  (producibilita<=921) {
							if (parteInsiemi)
								//F16
								val = getCellValue(sheet.getRow(15).getCell(5));
							else 
								//G16
								val = getCellValue(sheet.getRow(15).getCell(6));
						} else if  (producibilita<=1141) {
							if (parteInsiemi)
								//F17
								val = getCellValue(sheet.getRow(16).getCell(5));
							else 
								//G17
								val = getCellValue(sheet.getRow(16).getCell(6));
						} else if  (producibilita<=1397) {
							if (parteInsiemi)
								//F18
								val = getCellValue(sheet.getRow(17).getCell(5));
							else 
								//G18
								val = getCellValue(sheet.getRow(17).getCell(6));
						} else if  (producibilita>1397) {
							if (parteInsiemi)
								//F19
								val = getCellValue(sheet.getRow(18).getCell(5));
							else 
								//G19
								val = getCellValue(sheet.getRow(18).getCell(6));
						}
					}					
				}

			} else if (tipoApparecchio!=null && "04".equals(tipoApparecchio)) {
				//04 - LINEA ATTR.RE A PRESSIONE --> Foglio 14 (PRESSIONE LINEA ATTREZZ.)
				sheet = TariffarioAction.instance().getSheet(v.getData(), 14);

				if (sheet==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

					return null;
				}

				String tub = i.getTubazioni();
				if (tub==null || "".equals(tub)){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Numero tubazioni impianto non noto.", "Numero tubazioni impianto non noto.");

					return null;
				}

				Double tubazioni = Double.parseDouble(tub.replace(",", "."));
				if (tubazioni==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Numero tubazioni impianto non noto.", "Numero tubazioni impianto non noto.");

					return null;
				}

				//PRIMA VERIFICA = NO
				if (!primaVerifica) {
					//(prova ESERCIZIO=SI o PROVA INTERNA=SI) e prova IDRAULICA=SI
					if (((esercizio || interna) && idraulica) || idraulica) {
						//C4
						val = getCellValue(sheet.getRow(3).getCell(2));
					}
					//(prova ESERCIZIO=SI o PROVA INTERNA=SI) e prova IDRAULICA=NO
					else if ((esercizio || interna) && !idraulica) {
						//B3
						val = getCellValue(sheet.getRow(2).getCell(1));
					} else if(!esercizio && !interna && !idraulica){
						//Nessuna prova -> Si comporta come sopralluogo (05)
						val = getImportoImpPress020405(v, i, tipoApparecchio, "05");
					} else {
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.");

						return null;
					}

					//PRIMA VERIFICA = SI
				} else 
					//D5
					val = getCellValue(sheet.getRow(4).getCell(3));						

				if (val!=null)
					val = val*tubazioni;

			} else if (tipoApparecchio!=null && "07 10".contains(tipoApparecchio)) {
				/* 07 - RECIPIENTE PER VAPORE --> Foglio 16 (APP PRESSIONE 07 e 10) 
				 * 10 - REC. PRESS. AMBIENTE LAVORO*/
				sheet = TariffarioAction.instance().getSheet(v.getData(), 15);

				if (sheet==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

					return null;
				}

				Double press1 = i.getPressBar1();
				if (press1==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

					return null;
				}

				Double capacita = i.getCapacita();
				if (capacita==null || "".equals(capacita)){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non nota.", "Capacità impianto non nota.");

					return null;
				}

				//Pressione per capacità
				Double pXc = press1*capacita;

				// Verifica periodica successiva alla prima -> Colonne B e C
				if (!primaVerifica){
					if ((esercizio || interna) && !idraulica){
						if  (pXc<=1000)
							//B4
							val = getCellValue(sheet.getRow(3).getCell(1));						
						else if  (pXc<=8000)
							//B5
							val = getCellValue(sheet.getRow(4).getCell(1));						
						else if  (pXc<=27000)
							//B6
							val = getCellValue(sheet.getRow(5).getCell(1));						
						else if  (pXc<=125000)
							//B7
							val = getCellValue(sheet.getRow(6).getCell(1));
						else if  (pXc<=343000)
							//B8
							val = getCellValue(sheet.getRow(7).getCell(1));
						else if  (pXc<=729000)
							//B9
							val = getCellValue(sheet.getRow(8).getCell(1));
						else if  (pXc<=1331000)
							//B10
							val = getCellValue(sheet.getRow(9).getCell(1));
						else if  (pXc<=2197000)
							//B11
							val = getCellValue(sheet.getRow(10).getCell(1));
						else if  (pXc<=3375000)
							//B12
							val = getCellValue(sheet.getRow(11).getCell(1));
						else if  (pXc<=4913000)
							//B13
							val = getCellValue(sheet.getRow(12).getCell(1));
						else if  (pXc<=5832000)
							//B14
							val = getCellValue(sheet.getRow(13).getCell(1));
						else if  (pXc>5832000)
							//B15
							val = getCellValue(sheet.getRow(14).getCell(1));

					}else if (esercizio || interna || idraulica){
						if  (pXc<=1000)
							//C4
							val = getCellValue(sheet.getRow(3).getCell(2));						
						else if  (pXc<=8000)
							//C5
							val = getCellValue(sheet.getRow(4).getCell(2));						
						else if  (pXc<=27000)
							//C6
							val = getCellValue(sheet.getRow(5).getCell(2));						
						else if  (pXc<=125000)
							//C7
							val = getCellValue(sheet.getRow(6).getCell(2));
						else if  (pXc<=343000)
							//C8
							val = getCellValue(sheet.getRow(7).getCell(2));
						else if  (pXc<=729000)
							//C9
							val = getCellValue(sheet.getRow(8).getCell(2));
						else if  (pXc<=1331000)
							//C10
							val = getCellValue(sheet.getRow(9).getCell(2));
						else if  (pXc<=2197000)
							//C11
							val = getCellValue(sheet.getRow(10).getCell(2));
						else if  (pXc<=3375000)
							//C12
							val = getCellValue(sheet.getRow(11).getCell(2));
						else if  (pXc<=4913000)
							//C13
							val = getCellValue(sheet.getRow(12).getCell(2));
						else if  (pXc<=5832000)
							//C14
							val = getCellValue(sheet.getRow(13).getCell(2));
						else if  (pXc>5832000)
							//C15
							val = getCellValue(sheet.getRow(14).getCell(2));

					}else if(!esercizio && !interna && !idraulica){
						//Nessuna prova -> Si comporta come sopralluogo (05)
						val = getImportoImpPress020405(v, i, tipoApparecchio, "05");
					} else
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.");
				} else if (primaVerifica){
					// Prima verifica periodica -> Colonne D ed E	

					if  (pXc<=1000){
						if (parteInsiemi)
							//D4
							val = getCellValue(sheet.getRow(3).getCell(3));	
						else						
							val = getCellValue(sheet.getRow(3).getCell(4));	
					}else if  (pXc<=8000){
						if (parteInsiemi)
							//D5
							val = getCellValue(sheet.getRow(4).getCell(3));						
						else						
							val = getCellValue(sheet.getRow(4).getCell(4));						
					}else if  (pXc<=27000){
						if (parteInsiemi)
							//D6
							val = getCellValue(sheet.getRow(5).getCell(3));						
						else						
							val = getCellValue(sheet.getRow(5).getCell(4));						
					}else if  (pXc<=125000){
						if (parteInsiemi)
							//D7
							val = getCellValue(sheet.getRow(6).getCell(3));
						else						
							val = getCellValue(sheet.getRow(6).getCell(4));
					}else if  (pXc<=343000){
						if (parteInsiemi)
							//D8
							val = getCellValue(sheet.getRow(7).getCell(3));
						else						
							val = getCellValue(sheet.getRow(7).getCell(4));
					}else if  (pXc<=729000){
						if (parteInsiemi)
							//D9
							val = getCellValue(sheet.getRow(8).getCell(3));
						else						
							val = getCellValue(sheet.getRow(8).getCell(4));
					}else if  (pXc<=1331000){
						if (parteInsiemi)
							//D10
							val = getCellValue(sheet.getRow(9).getCell(3));
						else						
							val = getCellValue(sheet.getRow(9).getCell(4));
					}else if  (pXc<=2197000){
						if (parteInsiemi)
							//D11
							val = getCellValue(sheet.getRow(10).getCell(3));
						else						
							val = getCellValue(sheet.getRow(10).getCell(4));
					}else if  (pXc<=3375000){
						if (parteInsiemi)
							//D12
							val = getCellValue(sheet.getRow(11).getCell(3));
						else						
							val = getCellValue(sheet.getRow(11).getCell(4));
					}else if  (pXc<=4913000){
						if (parteInsiemi)
							//D13
							val = getCellValue(sheet.getRow(12).getCell(3));
						else						
							val = getCellValue(sheet.getRow(12).getCell(4));
					}else if  (pXc<=5832000){
						if (parteInsiemi)
							//D14
							val = getCellValue(sheet.getRow(13).getCell(3));
						else						
							val = getCellValue(sheet.getRow(13).getCell(4));
					}else if  (pXc>5832000){
						if (parteInsiemi)
							//D15
							val = getCellValue(sheet.getRow(14).getCell(3));
						else						
							val = getCellValue(sheet.getRow(14).getCell(4));
					}					
				}


				return val;

			} else if (tipoApparecchio!=null && "11".equals(tipoApparecchio)){
				//11 - LINEA TUBAZIONI --> Foglio 14 (PRESSIONE LINEA ATTREZZ.)
				sheet = TariffarioAction.instance().getSheet(v.getData(), 14);

				if (sheet==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

					return null;
				}

				String tub = i.getTubazioni();
				if (tub==null || "".equals(tub)){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Numero tubazioni impianto non noto.", "Numero tubazioni impianto non noto.");

					return null;
				}

				Double tubazioni = Double.parseDouble(tub.replace(",", "."));
				if (tubazioni==null){
					if (printError)
						FacesErrorUtils.addErrorMessage("commError", "Numero tubazioni impianto non noto.", "Numero tubazioni impianto non noto.");

					return null;
				}

				//PRIMA VERIFICA = SI
				if(primaVerifica){
					//D5
					val = getCellValue(sheet.getRow(4).getCell(3));

					//PRIMA VERIFICA = NO
				}else{
					//(prova ESERCIZIO=SI) or (prova INTERNA=SI) and (INTEGRITA’=SI)] or [ (prova ESERCIZIO=SI) and (prova INTERNA=SI) and (INTEGRITA’=SI)]
					if (idraulica && (esercizio || interna)) {
						//C4
						val = getCellValue(sheet.getRow(3).getCell(2));
					}
					//[(prova ESERCIZIO=SI) or (prova INTERNA=SI) and (INTEGRITA’=NO)] or [(ESERCIZIO=SI) and prova (INTRENA=SI) and (INTEGRITA’=NO)]
					else if (!idraulica && (esercizio || interna)) {
						//B3
						val = getCellValue(sheet.getRow(2).getCell(1));
					} else {
						if (printError)
							FacesErrorUtils.addErrorMessage("commError", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.");

						return null;
					}					
				}

				if (val!=null)
					val = val*tubazioni;

			} else if (tipoApparecchio!=null && "12".equals(tipoApparecchio)){
				//12 - SISTEMI INDIVISIBILI

				Double subTot = 0D;

				if(primaVerifica){

					// SCHEDA GENERATORI
					List<SchedaGeneratoriIndiv>  generatori = i.getSchedaGeneratoriIndiv();

					if(generatori != null && generatori.size() > 0){
						// --> Foglio 1 (PRESSIONE sup<X) superficie <= superficie limite 300mq
						sheet = TariffarioAction.instance().getSheet(v.getData(), 1);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						for(SchedaGeneratoriIndiv sgi : generatori){
							String sup = sgi.getSuperficie();
							Double superficie = Double.parseDouble(sup.replace(",", "."));
							if(superficie > 300)
								continue;

							if (superficie>197){
								if (parteInsiemi)
									//H5
									val = getCellValue(sheet.getRow(4).getCell(7));
								else 
									//I5
									val = getCellValue(sheet.getRow(4).getCell(8));

							} else if (superficie>113) {
								if (parteInsiemi)
									//H4
									val = getCellValue(sheet.getRow(3).getCell(7));
								else 
									//I4
									val = getCellValue(sheet.getRow(3).getCell(8));

							} else /*if (superficie<=113)*/ { 

								if (parteInsiemi)
									//H3
									val = getCellValue(sheet.getRow(2).getCell(7));
								else 
									//I3
									val = getCellValue(sheet.getRow(2).getCell(8));
							}

							subTot += val;
						}

						// --> Foglio 2 (PRESSIONE sup>X) superficie > superficie limite 300mq
						sheet = TariffarioAction.instance().getSheet(v.getData(), 2);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						for(SchedaGeneratoriIndiv sgi : generatori){
							String sup = sgi.getSuperficie();
							Double superficie = Double.parseDouble(sup.replace(",", "."));
							if(superficie <= 300)
								continue;

							String prod = sgi.getProducibilita();
							if (prod==null || "".equals(prod)){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

								return null;
							}

							Double producibilita = Double.parseDouble(prod.replace(",", "."));
							if (producibilita==null){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

								return null;
							}

							if (producibilita<=12) {
								if (parteInsiemi)
									//F4
									val = getCellValue(sheet.getRow(3).getCell(5));
								else 
									//G4
									val = getCellValue(sheet.getRow(3).getCell(6));
							} else if  (producibilita<=22) {
								if (parteInsiemi)
									//F5
									val = getCellValue(sheet.getRow(4).getCell(5));
								else 
									//G5
									val = getCellValue(sheet.getRow(4).getCell(6));
							} else if  (producibilita<=37) {
								if (parteInsiemi)
									//F6
									val = getCellValue(sheet.getRow(5).getCell(5));
								else 
									//G6
									val = getCellValue(sheet.getRow(5).getCell(6));					
							} else if  (producibilita<=60) {
								if (parteInsiemi)
									//F7
									val = getCellValue(sheet.getRow(6).getCell(5));
								else 
									//G7
									val = getCellValue(sheet.getRow(6).getCell(6));
							} else if  (producibilita<=90) {
								if (parteInsiemi)
									//F8
									val = getCellValue(sheet.getRow(7).getCell(5));
								else 
									//G8
									val = getCellValue(sheet.getRow(7).getCell(6));
							} else if  (producibilita<=132) {
								if (parteInsiemi)
									//F9
									val = getCellValue(sheet.getRow(8).getCell(5));
								else 
									//G9
									val = getCellValue(sheet.getRow(8).getCell(6));
							} else if  (producibilita<=186) {
								if (parteInsiemi)
									//F10
									val = getCellValue(sheet.getRow(9).getCell(5));
								else 
									//G10
									val = getCellValue(sheet.getRow(9).getCell(6));
							} else if  (producibilita<=255) {
								if (parteInsiemi)
									//F11
									val = getCellValue(sheet.getRow(10).getCell(5));
								else 
									//G11
									val = getCellValue(sheet.getRow(10).getCell(6));
							} else if  (producibilita<=342) {
								if (parteInsiemi)
									//F12
									val = getCellValue(sheet.getRow(11).getCell(5));
								else 
									//G12
									val = getCellValue(sheet.getRow(11).getCell(6));
							} else if  (producibilita<=448) {
								if (parteInsiemi)
									//F13
									val = getCellValue(sheet.getRow(12).getCell(5));
								else 
									//G13
									val = getCellValue(sheet.getRow(12).getCell(6));
							} else if  (producibilita<=579) {
								if (parteInsiemi)
									//F14
									val = getCellValue(sheet.getRow(13).getCell(5));
								else 
									//G14
									val = getCellValue(sheet.getRow(13).getCell(6));
							} else if  (producibilita<=735) {
								if (parteInsiemi)
									//F15
									val = getCellValue(sheet.getRow(14).getCell(5));
								else 
									//G15
									val = getCellValue(sheet.getRow(14).getCell(6));
							} else if  (producibilita<=921) {
								if (parteInsiemi)
									//F16
									val = getCellValue(sheet.getRow(15).getCell(5));
								else 
									//G16
									val = getCellValue(sheet.getRow(15).getCell(6));
							} else if  (producibilita<=1141) {
								if (parteInsiemi)
									//F17
									val = getCellValue(sheet.getRow(16).getCell(5));
								else 
									//G17
									val = getCellValue(sheet.getRow(16).getCell(6));
							} else if  (producibilita<=1397) {
								if (parteInsiemi)
									//F18
									val = getCellValue(sheet.getRow(17).getCell(5));
								else 
									//G18
									val = getCellValue(sheet.getRow(17).getCell(6));
							} else if  (producibilita>1397) {
								if (parteInsiemi)
									//F19
									val = getCellValue(sheet.getRow(18).getCell(5));
								else 
									//G19
									val = getCellValue(sheet.getRow(18).getCell(6));
							}

							subTot += val;

						}
					}

					// SCHEDA RECIPIENTI
					List<SchedaRecipientiIndiv> recipienti = i.getSchedaRecipientiIndiv();

					if(recipienti != null & recipienti.size() > 0){
						// --> Foglio 16 (APP PRESSIONE 07 e 10) 
						sheet = TariffarioAction.instance().getSheet(v.getData(), 15);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						for(SchedaRecipientiIndiv sri : recipienti){

							String p1 = sri.getPressBar1();
							if (p1==null || "".equals(p1)){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

								return null;
							}

							Double press1 = Double.parseDouble(p1.replace(",", "."));
							if (press1==null){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

								return null;
							}

							String c = sri.getCapacita();
							if (c==null || "".equals(c)){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non nota.", "Capacità impianto non nota.");

								return null;
							}

							Double cap = Double.parseDouble(c.replace(",", "."));
							if (cap==null){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non nota.", "Capacità impianto non nota.");

								return null;
							}

							//Pressione per capacità
							Double pXc = press1*cap;

							// Prima verifica periodica -> Colonne D ed E	
							if  (pXc<=1000){
								if (parteInsiemi)
									//D4
									val = getCellValue(sheet.getRow(3).getCell(3));	
								else						
									val = getCellValue(sheet.getRow(3).getCell(4));	
							}else if  (pXc<=8000){
								if (parteInsiemi)
									//D5
									val = getCellValue(sheet.getRow(4).getCell(3));						
								else						
									val = getCellValue(sheet.getRow(4).getCell(4));						
							}else if  (pXc<=27000){
								if (parteInsiemi)
									//D6
									val = getCellValue(sheet.getRow(5).getCell(3));						
								else						
									val = getCellValue(sheet.getRow(5).getCell(4));						
							}else if  (pXc<=125000){
								if (parteInsiemi)
									//D7
									val = getCellValue(sheet.getRow(6).getCell(3));
								else						
									val = getCellValue(sheet.getRow(6).getCell(4));
							}else if  (pXc<=343000){
								if (parteInsiemi)
									//D8
									val = getCellValue(sheet.getRow(7).getCell(3));
								else						
									val = getCellValue(sheet.getRow(7).getCell(4));
							}else if  (pXc<=729000){
								if (parteInsiemi)
									//D9
									val = getCellValue(sheet.getRow(8).getCell(3));
								else						
									val = getCellValue(sheet.getRow(8).getCell(4));
							}else if  (pXc<=1331000){
								if (parteInsiemi)
									//D10
									val = getCellValue(sheet.getRow(9).getCell(3));
								else						
									val = getCellValue(sheet.getRow(9).getCell(4));
							}else if  (pXc<=2197000){
								if (parteInsiemi)
									//D11
									val = getCellValue(sheet.getRow(10).getCell(3));
								else						
									val = getCellValue(sheet.getRow(10).getCell(4));
							}else if  (pXc<=3375000){
								if (parteInsiemi)
									//D12
									val = getCellValue(sheet.getRow(11).getCell(3));
								else						
									val = getCellValue(sheet.getRow(11).getCell(4));
							}else if  (pXc<=4913000){
								if (parteInsiemi)
									//D13
									val = getCellValue(sheet.getRow(12).getCell(3));
								else						
									val = getCellValue(sheet.getRow(12).getCell(4));
							}else if  (pXc<=5832000){
								if (parteInsiemi)
									//D14
									val = getCellValue(sheet.getRow(13).getCell(3));
								else						
									val = getCellValue(sheet.getRow(13).getCell(4));
							}else if  (pXc>5832000){
								if (parteInsiemi)
									//D15
									val = getCellValue(sheet.getRow(14).getCell(3));
								else						
									val = getCellValue(sheet.getRow(14).getCell(4));
							}

							subTot = subTot + val;
						}
					}

					// SCHEDA TUBAZIONI
					List<SchedaTubazioniIndiv> tubazioni = i.getSchedaTubazioniIndiv();
					if(tubazioni != null && tubazioni.size() > 0){
						// Foglio 14 (PRESSIONE LINEA ATTREZZ.)
						sheet = TariffarioAction.instance().getSheet(v.getData(), 14);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						//TUTTI I TIPI DI PROVA - D5
						val = getCellValue(sheet.getRow(4).getCell(3));

						subTot = subTot + val * tubazioni.size();
					}

					/*
					if(subTot > 0D){
						subTot = subTot - 0.2*subTot;
						val = subTot;
					}*/
				} else {
					//Double subTot = 0D;
					// SCHEDA GENERATORI
					List<SchedaGeneratoriIndiv>  generatori = i.getSchedaGeneratoriIndiv();

					if(generatori != null && generatori.size() > 0){
						// --> Foglio 1 (PRESSIONE sup<X) superficie <= superficie limite 300mq
						sheet = TariffarioAction.instance().getSheet(v.getData(), 1);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						if((esercizio || interna) && !idraulica){
							// Tipo verifica=Periodica e PRIMA VERIFICA=NO  e (prova ESERCIZIO=SI o PROVA INTERNA=SI) e  prova IDRAULICA =NO
							for(SchedaGeneratoriIndiv sgi : generatori){
								String sup = sgi.getSuperficie();
								Double superficie = Double.parseDouble(sup.replace(",", "."));
								if(superficie > 300)
									continue;

								if(superficie <= 113){
									val = getCellValue(sheet.getRow(2).getCell(5));
								}else if(superficie > 113 && superficie <= 197){
									val = getCellValue(sheet.getRow(3).getCell(5));
								} else{
									val = getCellValue(sheet.getRow(4).getCell(5));
								}

								subTot += val;
							}

						} else if(esercizio || interna || idraulica){
							// Tipo verifica=Periodica e PRIMA VERIFICA=NO e (prova ESERCIZIO=SI o PROVA INTERNA=SI o prova IDRAULICA =SI)
							for(SchedaGeneratoriIndiv sgi : generatori){
								String sup = sgi.getSuperficie();
								Double superficie = Double.parseDouble(sup.replace(",", "."));
								if(superficie > 300)
									continue;

								if(superficie <= 113){
									val = getCellValue(sheet.getRow(2).getCell(6));
								}else if(superficie > 113 && superficie <= 197){
									val = getCellValue(sheet.getRow(3).getCell(6));
								} else{
									val = getCellValue(sheet.getRow(4).getCell(6));
								}

								subTot += val;
							}

						} else {
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.");

							return null;
						}

						// --> Foglio 2 (PRESSIONE sup>X) superficie > superficie limite 300mq
						sheet = TariffarioAction.instance().getSheet(v.getData(), 2);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						if((esercizio || interna) && !idraulica){
							// Tipo verifica=Periodica e PRIMA VERIFICA=NO  e (prova ESERCIZIO=SI o PROVA INTERNA=SI) e  prova IDRAULICA=NO
							for(SchedaGeneratoriIndiv sgi : generatori){
								String sup = sgi.getSuperficie();
								Double superficie = Double.parseDouble(sup.replace(",", "."));
								if(superficie <= 300)
									continue;

								String prod = sgi.getProducibilita();
								if (prod==null || "".equals(prod)){
									if (printError)
										FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

									return null;
								}

								Double producibilita = Double.parseDouble(prod.replace(",", "."));
								if (producibilita==null){
									if (printError)
										FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

									return null;
								}

								if (producibilita<=12)
									//D4
									val = getCellValue(sheet.getRow(3).getCell(3)); 
								else if  (producibilita<=22)
									//D5
									val = getCellValue(sheet.getRow(4).getCell(3));
								else if  (producibilita<=37)
									//D6
									val = getCellValue(sheet.getRow(5).getCell(3));						
								else if  (producibilita<=60)
									//D7
									val = getCellValue(sheet.getRow(6).getCell(3));
								else if  (producibilita<=90)
									//D8
									val = getCellValue(sheet.getRow(7).getCell(3));
								else if  (producibilita<=132)
									//D9
									val = getCellValue(sheet.getRow(8).getCell(3));
								else if  (producibilita<=186)
									//D10
									val = getCellValue(sheet.getRow(9).getCell(3));
								else if  (producibilita<=255)
									//D11
									val = getCellValue(sheet.getRow(10).getCell(3));
								else if  (producibilita<=342)
									//D12
									val = getCellValue(sheet.getRow(11).getCell(3));
								else if  (producibilita<=448)
									//D13
									val = getCellValue(sheet.getRow(12).getCell(3));
								else if  (producibilita<=579)
									//D14
									val = getCellValue(sheet.getRow(13).getCell(3));
								else if  (producibilita<=735)
									//D15
									val = getCellValue(sheet.getRow(14).getCell(3));
								else if  (producibilita<=921)
									//D16
									val = getCellValue(sheet.getRow(15).getCell(3));
								else if  (producibilita<=1141)
									//D17
									val = getCellValue(sheet.getRow(16).getCell(3));
								else if  (producibilita<=1397)
									//D18
									val = getCellValue(sheet.getRow(17).getCell(3));
								else if  (producibilita>1397)
									//D19
									val = getCellValue(sheet.getRow(18).getCell(3));

								subTot += val;
							}

						} else if(esercizio || interna || idraulica){
							// Tipo verifica=Periodica e PRIMA VERIFICA=NO e (prova ESERCIZIO=SI o PROVA INTERNA=SI o prova IDRAULICA =SI)
							for(SchedaGeneratoriIndiv sgi : generatori){
								String sup = sgi.getSuperficie();
								Double superficie = Double.parseDouble(sup.replace(",", "."));
								if(superficie <= 300)
									continue;


								String prod = sgi.getProducibilita();
								if (prod==null || "".equals(prod)){
									if (printError)
										FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

									return null;
								}

								Double producibilita = Double.parseDouble(prod.replace(",", "."));
								if (producibilita==null){
									if (printError)
										FacesErrorUtils.addErrorMessage("commError", "Producibilità impianto non nota.", "Producibilità impianto non nota.");

									return null;
								}

								if (producibilita<=12)
									//E4
									val = getCellValue(sheet.getRow(3).getCell(4)); 
								else if  (producibilita<=22)
									//E5
									val = getCellValue(sheet.getRow(4).getCell(4));
								else if  (producibilita<=37)
									//E6
									val = getCellValue(sheet.getRow(5).getCell(4));						
								else if  (producibilita<=60)
									//E7
									val = getCellValue(sheet.getRow(6).getCell(4));
								else if  (producibilita<=90)
									//E8
									val = getCellValue(sheet.getRow(7).getCell(4));
								else if  (producibilita<=132)
									//E9
									val = getCellValue(sheet.getRow(8).getCell(4));
								else if  (producibilita<=186)
									//E10
									val = getCellValue(sheet.getRow(9).getCell(4));
								else if  (producibilita<=255)
									//E11
									val = getCellValue(sheet.getRow(10).getCell(4));
								else if  (producibilita<=342)
									//E12
									val = getCellValue(sheet.getRow(11).getCell(4));
								else if  (producibilita<=448)
									//E13
									val = getCellValue(sheet.getRow(12).getCell(4));
								else if  (producibilita<=579)
									//E14
									val = getCellValue(sheet.getRow(13).getCell(4));
								else if  (producibilita<=735)
									//E15
									val = getCellValue(sheet.getRow(14).getCell(4));
								else if  (producibilita<=921)
									//E16
									val = getCellValue(sheet.getRow(15).getCell(4));
								else if  (producibilita<=1141)
									//E17
									val = getCellValue(sheet.getRow(16).getCell(4));
								else if  (producibilita<=1397)
									//E18
									val = getCellValue(sheet.getRow(17).getCell(4));
								else if  (producibilita>1397)
									//E19
									val = getCellValue(sheet.getRow(18).getCell(4));

								subTot += val;
							}

						} else {
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.");

							return null;
						}

					}


					// SCHEDA RECIPIENTI
					List<SchedaRecipientiIndiv> recipienti = i.getSchedaRecipientiIndiv();

					if(recipienti != null & recipienti.size() > 0){
						// --> Foglio 16 (APP PRESSIONE 07 e 10) 
						sheet = TariffarioAction.instance().getSheet(v.getData(), 15);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						for(SchedaRecipientiIndiv sri : recipienti){

							String p1 = sri.getPressBar1();
							if (p1==null || "".equals(p1)){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

								return null;
							}

							Double press1 = Double.parseDouble(p1.replace(",", "."));
							if (press1==null){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

								return null;
							}

							String c = sri.getCapacita();
							if (c==null || "".equals(c)){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non nota.", "Capacità impianto non nota.");

								return null;
							}

							Double cap = Double.parseDouble(c.replace(",", "."));
							if (cap==null){
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non nota.", "Capacità impianto non nota.");

								return null;
							}

							//Pressione per capacità
							Double pXc = press1*cap;

							// Verifica periodica successiva alla prima -> Colonne B e C

							if ((esercizio || interna) && !idraulica){
								if  (pXc<=1000)
									//B4
									val = getCellValue(sheet.getRow(3).getCell(1));						
								else if  (pXc<=8000)
									//B5
									val = getCellValue(sheet.getRow(4).getCell(1));						
								else if  (pXc<=27000)
									//B6
									val = getCellValue(sheet.getRow(5).getCell(1));						
								else if  (pXc<=125000)
									//B7
									val = getCellValue(sheet.getRow(6).getCell(1));
								else if  (pXc<=343000)
									//B8
									val = getCellValue(sheet.getRow(7).getCell(1));
								else if  (pXc<=729000)
									//B9
									val = getCellValue(sheet.getRow(8).getCell(1));
								else if  (pXc<=1331000)
									//B10
									val = getCellValue(sheet.getRow(9).getCell(1));
								else if  (pXc<=2197000)
									//B11
									val = getCellValue(sheet.getRow(10).getCell(1));
								else if  (pXc<=3375000)
									//B12
									val = getCellValue(sheet.getRow(11).getCell(1));
								else if  (pXc<=4913000)
									//B13
									val = getCellValue(sheet.getRow(12).getCell(1));
								else if  (pXc<=5832000)
									//B14
									val = getCellValue(sheet.getRow(13).getCell(1));
								else if  (pXc>5832000)
									//B15
									val = getCellValue(sheet.getRow(14).getCell(1));

							}else if (esercizio || interna || idraulica){
								if  (pXc<=1000)
									//C4
									val = getCellValue(sheet.getRow(3).getCell(2));						
								else if  (pXc<=8000)
									//C5
									val = getCellValue(sheet.getRow(4).getCell(2));						
								else if  (pXc<=27000)
									//C6
									val = getCellValue(sheet.getRow(5).getCell(2));						
								else if  (pXc<=125000)
									//C7
									val = getCellValue(sheet.getRow(6).getCell(2));
								else if  (pXc<=343000)
									//C8
									val = getCellValue(sheet.getRow(7).getCell(2));
								else if  (pXc<=729000)
									//C9
									val = getCellValue(sheet.getRow(8).getCell(2));
								else if  (pXc<=1331000)
									//C10
									val = getCellValue(sheet.getRow(9).getCell(2));
								else if  (pXc<=2197000)
									//C11
									val = getCellValue(sheet.getRow(10).getCell(2));
								else if  (pXc<=3375000)
									//C12
									val = getCellValue(sheet.getRow(11).getCell(2));
								else if  (pXc<=4913000)
									//C13
									val = getCellValue(sheet.getRow(12).getCell(2));
								else if  (pXc<=5832000)
									//C14
									val = getCellValue(sheet.getRow(13).getCell(2));
								else if  (pXc>5832000)
									//C15
									val = getCellValue(sheet.getRow(14).getCell(2));

							}else if(!esercizio && !interna && !idraulica){
								//Nessuna prova -> Si comporta come sopralluogo (05)
								val = getImportoImpPress020405(v, i, tipoApparecchio, "05");
							} else
								if (printError)
									FacesErrorUtils.addErrorMessage("commError", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.", "Verifiche successive alla prima: regole non note per il Tipo prova specificato.");

							subTot += val;
						}

					}

					// SCHEDA TUBAZIONI
					List<SchedaTubazioniIndiv> tubazioni = i.getSchedaTubazioniIndiv();
					if(tubazioni != null && tubazioni.size() > 0){
						// Foglio 14 (PRESSIONE LINEA ATTREZZ.)
						sheet = TariffarioAction.instance().getSheet(v.getData(), 14);

						if (sheet==null){
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");

							return null;
						}

						//(prova ESERCIZIO=SI) or (prova INTERNA=SI) and (INTEGRITA’=SI)] or [ (prova ESERCIZIO=SI) and (prova INTERNA=SI) and (INTEGRITA’=SI)]
						if (idraulica && (esercizio || interna)) {
							//C4
							val = getCellValue(sheet.getRow(3).getCell(2));
						}
						//[(prova ESERCIZIO=SI) or (prova INTERNA=SI) and (INTEGRITA’=NO)] or [(ESERCIZIO=SI) and prova (INTRENA=SI) and (INTEGRITA’=NO)]
						else if (!idraulica && (esercizio || interna)) {
							//B3
							val = getCellValue(sheet.getRow(2).getCell(1));
						} else {
							if (printError)
								FacesErrorUtils.addErrorMessage("commError", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.", "Verifica successiva alla prima - Regole non note per il tipo prova specificato.");

							return null;
						}					

						subTot = subTot + val * tubazioni.size();
					}
				}

				if(subTot > 0D){
					subTot = subTot - 0.2*subTot;
					val = subTot;
				}	
			}

			return val;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//Tipo verifica: Straordinaria (02) o Visita a vuoto (04)
	private Double getImportoImpPress0204(VerificaImp v, ImpPress i, String tipoApparecchio, String tipoVerifica) {
		try {
			Double tariffaOraria = null;
			Double tariffaApparecchio = null;

			Double min = getMinutiServizio(v);

			//Calcolo la tariffa oraria
			if (min>0){
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 4);

				if (sheet==null){
					FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
					return null;
				}

				Double val = getCellValue(sheet.getRow(3).getCell(3));

				if (val!=null)
					tariffaOraria = val*min/60;
			} else {
				FacesErrorUtils.addErrorMessage("commError", "Impossibile calcolare la tariffa oraria.", "Impossibile calcolare la tariffa oraria.");
				return null;
			}

			//Se tipoVerifica = Straordinaria (02)
			if ("02".equals(tipoVerifica))
				return tariffaOraria;

			//Se tipoVerifica = Visita a vuoto (04) calcolo la tariffa per Tipo verifica: periodica (01)
			if ("04".equals(tipoVerifica)){

				/* Eccezione: Per impianti a pressione di tipo 10 - REC. PRESS. AMBIENTI LAVORO 
				 * la tariffazione è sempre oraria - non è quindi necessario calcolare la tariffaApparecchio */
				if (tipoApparecchio!=null && !"10".equals(tipoApparecchio))
					tariffaApparecchio = getImportoImpPress01(v, i, tipoApparecchio, false);

				if (tariffaOraria==null)
					return tariffaApparecchio;

				else if (tariffaApparecchio==null)
					return tariffaOraria;

				else if (tariffaApparecchio < tariffaOraria)
					return tariffaApparecchio;
			}

			return tariffaOraria;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	//Tipo verifica: Straordinaria (02) o Visita a vuoto (04) o Sopralluogo (05)
	private Double getImportoImpPress020405(VerificaImp v, ImpPress i, String tipoApparecchio, String tipoVerifica) {
		try {
			Double tariffaOraria = null;
			Double tariffaApparecchio = null;

			Double min = getMinutiServizio(v);

			//Calcolo la tariffa oraria
			if (min>0){
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 4);

				if (sheet==null){
					FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
					return null;
				}

				Double val = null;

				//Straordinaria (02)
				if ("02".equals(tipoVerifica))
					val = getCellValue(sheet.getRow(3).getCell(3));
				else //Visita a vuoto (04) o Sopralluogo (05)
					val = getCellValue(sheet.getRow(4).getCell(3));

				if (val!=null)
					tariffaOraria = val*min/60;
			} else {
				FacesErrorUtils.addErrorMessage("commError", "Impossibile calcolare la tariffa oraria.", "Impossibile calcolare la tariffa oraria.");
				return null;
			}

			//Se tipoVerifica = Sopralluogo (05) o Straordinaria (02)
			if ("02".equals(tipoVerifica) || "05".equals(tipoVerifica))
				return tariffaOraria;

			//Se tipoVerifica = Visita a vuoto (04) calcolo la tariffa per Tipo verifica: periodica (01)
			if ("04".equals(tipoVerifica)){

				/* Eccezione: Per impianti a pressione di tipo 10 - REC. PRESS. AMBIENTI LAVORO 
				 * la tariffazione è sempre oraria - non è quindi necessario calcolare la tariffaApparecchio */
				if (tipoApparecchio!=null && !"10".equals(tipoApparecchio))
					tariffaApparecchio = getImportoImpPress01(v, i, tipoApparecchio, false);

				if (tariffaOraria==null)
					return tariffaApparecchio;

				else if (tariffaApparecchio==null)
					return tariffaOraria;

				else if (tariffaApparecchio < tariffaOraria)
					return tariffaApparecchio;
			}

			return tariffaOraria;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	/* Calcola il numero di minuti dedicati alla verifica */
	private Double getMinutiServizio(VerificaImp v) {
		try {
			Double min = 0.0;

			String hhServizio = v.getHhServizio();
			if (hhServizio!=null && !"".equals(hhServizio)){
				Double hh = Double.parseDouble(hhServizio);
				if (hh>0)
					min += hh*60;
			}

			String mmServizio = v.getMmServizio();
			if (mmServizio!=null && !"".equals(mmServizio)){
				Double mm = Double.parseDouble(mmServizio);
				if (mm>0)
					min += mm;
			}

			return min;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpRisc(VerificaImp v) {
		try {
			Double val = null;

			String tipoVerifica = null;
			String descrizImpianto = null;
			ImpRisc impRisc = v.getImpRiscCpy();

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto 
			 * 05 - Sopralluogo
			 * 06 - Straordinaria per accertamenti*/
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}


			/* descrizImpianto:
			 * 01 Riscaldamento ambienti
			 * 02 Acqua calda per servizi
			 * 03 Riscaldamento e servizi
			 * 04 Acqua calda per fini produttivi */
			CodeValuePhi descrizImpiantoCv = impRisc.getDescrizImpianto();

			if (descrizImpiantoCv!=null)
				descrizImpianto = descrizImpiantoCv.getCode();

			if (descrizImpianto==null || "".equals(descrizImpianto)){
				FacesErrorUtils.addErrorMessage("commError", "Descrizione impianto non nota.", "Descrizione impianto non nota.");
				return null;
			}

			//Riscaldamento lavoro --> Foglio 6 (RISCALDAMENTO LAVORO) 
			if ("04".equals(descrizImpianto)){

				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 6);
				if (sheet==null){
					FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");					
					return null;
				}

				//Tipo verifica = PERIODICA
				if ("01".equals(tipoVerifica)){
					boolean primaVerifica = v.getPrima()!=null?v.getPrima():false;

					if (primaVerifica)
						val = getCellValue(sheet.getRow(2).getCell(2));
					else 
						val = getCellValue(sheet.getRow(2).getCell(1));

					//Tipo verifica = STRAORDINARIO (02) o VISITA A VUOTO (04) o SOPRALLUOGO (05) o STRAORD. PER ACC.
				} else if ("02".equals(tipoVerifica) || "04".equals(tipoVerifica) || "05".equals(tipoVerifica) || "06".equals(tipoVerifica)){

					Double min = getMinutiServizio(v);
					if (min>0){
						val = "02 06".contains(tipoVerifica)?getCellValue(sheet.getRow(3).getCell(3)):getCellValue(sheet.getRow(3).getCell(4));

						if (val!=null)
							val = val*min/60;
					}
				}

				//Riscaldamento ambienti vita --> Foglio 7 (RISCALDAMENTO AMBIENTE VITA) 	
			} else if ("01 02 03".contains(descrizImpianto)) {
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 7);

				if (sheet==null){
					FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
					return null;
				}

				Double superfice = impRisc.getSuperficieRisc();
				if (superfice==null){
					FacesErrorUtils.addErrorMessage("commError", "Superficie impianto non nota.", "Superficie impianto non nota.");
					return null;
				}

				/* tipoVerifica: 
				 * 01 - Periodica
				 * 02 - Straordinaria
				 * 03 - Constatazione (eliminato)
				 * 04 - Visita a vuoto 
				 * 05 - Sopralluogo 
				 * 06 - Straordinaria per accertamenti*/

				//Sopralluogo o straord. per acc.
				if ("05 06".contains(tipoVerifica)){

					Double tariffaOraria = null;
					Double min = getMinutiServizio(v);

					//Calcolo la tariffa oraria
					if (min>0){

						val =  getCellValue(sheet.getRow(5).getCell(5));

						if (val!=null)
							tariffaOraria = val*min/60;
					}

					return tariffaOraria;
				}
				else if (superfice<=51){
					//Periodica
					if ("01".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(3).getCell(2));
					//Straordinaria
					else if ("02".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(3).getCell(3));
					//Visita a vuoto
					else if ("04".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(3).getCell(4));

				} else if (superfice<=300){
					//Periodica
					if ("01".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(4).getCell(2));
					//Straordinaria
					else if ("02".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(4).getCell(3));
					//Visita a vuoto
					else if ("04".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(4).getCell(4));

				} else if (superfice>300){
					//Periodica
					if ("01".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(2).getCell(2));
					//Straordinaria
					else if ("02".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(2).getCell(3));
					//Visita a vuoto
					else if ("04".equals(tipoVerifica))
						val = getCellValue(sheet.getRow(2).getCell(4));
				}
			}

			return val;

		} catch (Exception ex) {
			log.error(ex);
			return null;
			//throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpMonta(VerificaImp v) {
		try {
			Double val = null;

			String tipoVerifica = null;
			ImpMonta impMonta = v.getImpMontaCpy();
			String categoria = null;

			/* 01 A Ascensore adibito al trasporto di persone
			   02 B Ascensore per il trasporto di cose accompagnate da persone
			   03 C Montacarichi per trasporto di cose con cabina destinate alle persone per il solo carico e scarico
			   04 D Montacarichi per trasporto di cose, con cabina non accessibile a persone e di portata superiore a Kg 25
			   05 E Europa*/
			CodeValuePhi categoriaCv = impMonta.getCategoria();

			if (categoriaCv!=null)
				categoria = categoriaCv.getCode();

			if (categoria==null || "".equals(categoria)){
				FacesErrorUtils.addErrorMessage("commError", "Categoria impianto non nota.", "Categoria impianto non nota.");
				return null;
			}

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto */
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}

			//Ascensori e montacarichi --> Foglio 8 (ASCENSORI E MONTACARICHI) 	
			Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 8);
			if (sheet==null){
				FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
				return null;
			}

			//Periodica e straordinaria normale
			if ("01 02".contains(tipoVerifica)){
				Integer fermate = impMonta.getFermate();

				if (fermate==null){
					FacesErrorUtils.addErrorMessage("commError", "Numero fermate non noto.", "Numero fermate non noto.");
					return null;
				}

				//Per le categorie A, B e C
				if (/*categoria!=null && */"01 02 03".contains(categoria)){
					if (fermate<=5)
						val = getCellValue(sheet.getRow(2).getCell(3));
					else {
						val = getCellValue(sheet.getRow(2).getCell(3));
						Double valPlus = getCellValue(sheet.getRow(3).getCell(3));
						fermate = fermate - 5;

						val += valPlus*fermate;
					}

					//Per la categoria D	
				} else if ("04".equals(categoria)){
					if (fermate<=4)
						val = getCellValue(sheet.getRow(4).getCell(3));
					else {
						val = getCellValue(sheet.getRow(4).getCell(3));
						Double valPlus = getCellValue(sheet.getRow(5).getCell(3));
						fermate = fermate - 4;

						val += valPlus*fermate;
					}
				} else {
					FacesErrorUtils.addErrorMessage("commError", "Regola non nota per la Categoria impianto selezioanta.", "Regola non nota per la Categoria impianto selezionata.");
					return null;
				}

				//Straordinaria	- Modificato per I00089813 incorporando i sottotipi straordinaria in tipo verifica
			}
			//			else if ("02".equals(tipoVerifica)){
			//
			//				Integer fermate = impMonta.getFermate();
			//
			//				if (fermate==null || "".equals(fermate)){
			//					FacesErrorUtils.addErrorMessage("commError", "Numero fermate non noto.", "Numero fermate non noto.");
			//					return null;
			//				}
			//
			//				//Per le categorie A, B e C
			//				if (/*categoria!=null && */"01 02 03".contains(categoria)){
			//					if (fermate<=5)
			//						val = getCellValue(sheet.getRow(2).getCell(3));
			//					else {
			//						val = getCellValue(sheet.getRow(2).getCell(3));
			//						Double valPlus = getCellValue(sheet.getRow(3).getCell(3));
			//						fermate = fermate - 5;
			//
			//						val += valPlus*fermate;
			//					}
			//
			//					//Per la categoria D	
			//				} else if ("04".equals(categoria)){
			//					if (fermate<=4)
			//						val = getCellValue(sheet.getRow(4).getCell(3));
			//					else {
			//						val = getCellValue(sheet.getRow(4).getCell(3));
			//						Double valPlus = getCellValue(sheet.getRow(5).getCell(3));
			//						fermate = fermate - 4;
			//
			//						val += valPlus*fermate;
			//					}
			//				} else {
			//					FacesErrorUtils.addErrorMessage("commError", "Regola non nota per la Categoria impianto selezioanta.", "Regola non nota per la Categoria impianto selezionata.");
			//					return null;
			//				}


			//Visita a vuoto	
			//			} 
			else if ("04".equals(tipoVerifica))
				val = getCellValue(sheet.getRow(9).getCell(3));

			//Sopralluogo	
			else if ("05".equals(tipoVerifica)){
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					val = getCellValue(sheet.getRow(10).getCell(3));
					val = val*min/60;
				}

			}

			//06: Straordinaria per acceramenti (I00081134)
			else if ("06".equals(tipoVerifica)){

				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					val = getCellValue(sheet.getRow(7).getCell(3));

					if (val!=null)
						val = val*min/60;
				}
			}

			//07: Modifiche costruttive (ARPAV)
			else if ("07".equals(tipoVerifica)){
				val = getCellValue(sheet.getRow(6).getCell(3));
			}
			return val;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpSoll(VerificaImp v) {
		try {
			Double val = null;
			String tipoVerifica = null;
			ImpSoll impSoll = v.getImpSollCpy();

			//Apparecchi di sollevamento --> Foglio 10 (SOLLEVAMENTO) 	
			Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 10);
			if (sheet==null){
				FacesErrorUtils.addErrorMessage("commError", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).", "Errore nell'acquisizione del tariffario (tariffario o foglio non trovato).");
				return null;
			}

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria (non usato per questi impianti)
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto
			 * 05 - Sopralluogo 
			 * 06 - Straordinaria per accertamenti*/

			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}


			//tipoVerifica: 01 - Periodica
			if ("01".equals(tipoVerifica)){
				val = this.getImportoImpSoll01(v, impSoll, sheet, true);

				//tipoVerifica: 06 - straordinaria per accertamenti - tariffazione a minuti
			} else if ("06".equals(tipoVerifica)){
				Double min = getMinutiServizio(v);
				if (min>0){
					val = getCellValue(sheet.getRow(50).getCell(5));

					if (val!=null)
						val = val*min/60;
				}

				//tipoVerifica: 04 - Visita a vuoto 
			} else if ("04".equals(tipoVerifica)){
				/* Si applica l'importo minimo tra la tariffa della verifica dell'attrezzatura
				 * e l'importo corrispondente alla tariffa oraria per il tempo impiegato (compresi i trasferimenti) */

				// 26/10/20 da Martina su richiesta Saracini solo tariffa oraria
				Double valAttrezzatura = this.getImportoImpSoll01(v, impSoll, sheet, false);

				Double valMinuti = 0.0;
				Double min = getMinutiServizio(v);

				if (min>0){
					valMinuti = getCellValue(sheet.getRow(50).getCell(5));

					if (valMinuti!=null)
						valMinuti = valMinuti*min/60;
				}

				//				if (valAttrezzatura==null)
				val = valMinuti;
				//				else if (valMinuti==null)
				//					val = valAttrezzatura;
				//				else if (valMinuti<=valAttrezzatura)
				//					val = valMinuti;
				//				else val = valAttrezzatura;

				//tipoVerifica: 05 - Sopralluogo
			} else if ("05".equals(tipoVerifica)){
				Double min = getMinutiServizio(v);
				if(min>0){
					val = getCellValue(sheet.getRow(51).getCell(5));
					val = val*min/60;
				}
			}
			return val;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpSoll01(VerificaImp v, ImpSoll impSoll, Sheet sheet, Boolean printError) {
		try {

			String sottotipoImp = null;
			Boolean primaVerifica = v.getPrima()!= null? v.getPrima():false;
			Boolean gruSmontata = v.getGru()!=null?v.getGru():false;

			CodeValuePhi sottotipoImpCv = impSoll.getSubType();

			if (sottotipoImpCv!=null)
				sottotipoImp = sottotipoImpCv.getCode();

			if (sottotipoImp==null || "".equals(sottotipoImp)){
				FacesErrorUtils.addErrorMessage("commError", "Sotto-tipo impianto non noto.", "Sotto-tipo impianto non noto.");
				return null;
			}

			if ("05".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(2).getCell(7)):getCellValue(sheet.getRow(2).getCell(6));
			} else if ("06".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(3).getCell(7)):getCellValue(sheet.getRow(3).getCell(6));
			} else if ("12".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(4).getCell(7)):getCellValue(sheet.getRow(4).getCell(6));
			} else if ("13".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(5).getCell(7)):getCellValue(sheet.getRow(5).getCell(6));
			} else if ("14".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(6).getCell(7)):getCellValue(sheet.getRow(6).getCell(6));
			} else if ("15".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(7).getCell(7)):getCellValue(sheet.getRow(7).getCell(6));
			} else if ("16".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(8).getCell(7)):getCellValue(sheet.getRow(8).getCell(6));
			} else if ("18".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(9).getCell(7)):getCellValue(sheet.getRow(9).getCell(6));
			} else if ("19".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(10).getCell(7)):getCellValue(sheet.getRow(10).getCell(6));
			} else if ("20".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(11).getCell(7)):getCellValue(sheet.getRow(11).getCell(6));
			} else if ("21".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(12).getCell(7)):getCellValue(sheet.getRow(12).getCell(6));
			} else if ("22".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(13).getCell(7)):getCellValue(sheet.getRow(13).getCell(6));
			} else if ("23".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(14).getCell(7)):getCellValue(sheet.getRow(14).getCell(6));
			} else if ("24".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(15).getCell(7)):getCellValue(sheet.getRow(15).getCell(6));
			} else if ("25".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(16).getCell(7)):getCellValue(sheet.getRow(16).getCell(6));
			} else if ("26".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(17).getCell(7)):getCellValue(sheet.getRow(17).getCell(6));
			} else if ("80".equals(sottotipoImp)){
				if (gruSmontata)
					return primaVerifica?getCellValue(sheet.getRow(44).getCell(7)):getCellValue(sheet.getRow(44).getCell(6));
					else
						return primaVerifica?getCellValue(sheet.getRow(43).getCell(7)):getCellValue(sheet.getRow(43).getCell(6));
			} else if ("81".equals(sottotipoImp)){
				if (gruSmontata)
					return primaVerifica?getCellValue(sheet.getRow(46).getCell(7)):getCellValue(sheet.getRow(46).getCell(6));
					else
						return primaVerifica?getCellValue(sheet.getRow(45).getCell(7)):getCellValue(sheet.getRow(45).getCell(6));
			} else if ("82".equals(sottotipoImp)){
				if (gruSmontata)
					return primaVerifica?getCellValue(sheet.getRow(48).getCell(7)):getCellValue(sheet.getRow(48).getCell(6));
					else
						return primaVerifica?getCellValue(sheet.getRow(47).getCell(7)):getCellValue(sheet.getRow(47).getCell(6));
			} else if ("27".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(18).getCell(7)):getCellValue(sheet.getRow(18).getCell(6));
			} else if ("28".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(19).getCell(7)):getCellValue(sheet.getRow(19).getCell(6));
			} else if ("29".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(20).getCell(7)):getCellValue(sheet.getRow(20).getCell(6));
			} else if ("30".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(21).getCell(7)):getCellValue(sheet.getRow(21).getCell(6));
			} else if ("31".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(22).getCell(7)):getCellValue(sheet.getRow(22).getCell(6));
			} else if ("33".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(23).getCell(7)):getCellValue(sheet.getRow(23).getCell(6));
			} else if ("34".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(24).getCell(7)):getCellValue(sheet.getRow(24).getCell(6));
			} else if ("60".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(25).getCell(7)):getCellValue(sheet.getRow(25).getCell(6));
			} else if ("61".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(26).getCell(7)):getCellValue(sheet.getRow(26).getCell(6));
			} else if ("62".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(27).getCell(7)):getCellValue(sheet.getRow(27).getCell(6));
			} else if ("63".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(28).getCell(7)):getCellValue(sheet.getRow(28).getCell(6));
			} else if ("64".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(29).getCell(7)):getCellValue(sheet.getRow(29).getCell(6));
			} else if ("65".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(30).getCell(7)):getCellValue(sheet.getRow(30).getCell(6));
			} else if ("66".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(31).getCell(7)):getCellValue(sheet.getRow(31).getCell(6));
			} else if ("67".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(32).getCell(7)):getCellValue(sheet.getRow(32).getCell(6));
			} else if ("68".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(33).getCell(7)):getCellValue(sheet.getRow(33).getCell(6));
			} else if ("69".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(34).getCell(7)):getCellValue(sheet.getRow(34).getCell(6));
			} else if ("70".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(35).getCell(7)):getCellValue(sheet.getRow(35).getCell(6));
			} else if ("71".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(36).getCell(7)):getCellValue(sheet.getRow(36).getCell(6));
			} else if ("72".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(37).getCell(7)):getCellValue(sheet.getRow(37).getCell(6));
			} else if ("73".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(38).getCell(7)):getCellValue(sheet.getRow(38).getCell(6));
			} else if ("74".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(39).getCell(7)):getCellValue(sheet.getRow(39).getCell(6));
			} else if ("75".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(40).getCell(7)):getCellValue(sheet.getRow(40).getCell(6));
			} else if ("76".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(41).getCell(7)):getCellValue(sheet.getRow(41).getCell(6));
			} else if ("77".equals(sottotipoImp)){
				return primaVerifica?getCellValue(sheet.getRow(42).getCell(7)):getCellValue(sheet.getRow(42).getCell(6));
			}

			//I00081134
			if (printError)
				FacesErrorUtils.addErrorMessage("commError", "Regole non note per il sotto-tipo impianto specificato.", "Regole non note per il sotto-tipo impianto specificato.");

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}	

	private Double getImportoImpTerra(VerificaImp v) {
		try {
			Double val = null;
			String tipoTerra = null;

			ImpTerra impTerra = v.getImpTerraCpy();

			/* 01 - A - Inst. di prot. scariche atm.
			 * 02 - B - Impianti di messa a terra
			 * 03 - C - Ins. elett. In luoghi peric. */
			CodeValuePhi tipoTerraCv = impTerra.getSubTypeTerra();

			if (tipoTerraCv!=null)
				tipoTerra = tipoTerraCv.getCode();

			if (tipoTerra==null || "".equals(tipoTerra)){
				FacesErrorUtils.addErrorMessage("commError", "Sotto-tipo impianto non noto.", "Sotto-tipo impianto non noto.");
				return null;
			}

			if ("01".equals(tipoTerra)){
				//Impianti di messa a terra di tipo A --> Foglio 11 (IMPIANTI MESSA A TERRA A) 	
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 11);
				val = this.getImportoImpTerraA(v, impTerra, sheet);

			} else if ("02".equals(tipoTerra)){
				//Impianti di messa a terra di tipo B --> Foglio 12 (IMPIANTI MESSA A TERRA B) 	
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 12);
				val = this.getImportoImpTerraB(v, impTerra, sheet);

			} else if ("03".equals(tipoTerra)){
				//Impianti di messa a terra di tipo C --> Foglio 13 (IMPIANTI MESSA A TERRA C) 	
				Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 13);
				val = this.getImportoImpTerraC(v, impTerra, sheet);
			}

			return val;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpTerraA(VerificaImp v, ImpTerra impTerra, Sheet sheet) {
		try {
			String tipoVerifica = null;
			//			String struttAutop = null;
			String tipologia = null;

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto 
			 */
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}

			//tipoVerifica = Visita a vuoto 
			if ("04".equals(tipoVerifica))
				return getCellValue(sheet.getRow(3).getCell(3));

			//tipoVerifica = Sopralluogo - D5*ore
			if ("05".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val = getCellValue(sheet.getRow(4).getCell(3));
					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//tipoVerifica = Straordinaria per accertamenti - B2*ore
			if ("06".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val = getCellValue(sheet.getRow(1).getCell(1));
					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//			CodeValuePhi struttAutopCv = impTerra.getStruttAutopCode();
			//
			//			if (struttAutopCv!=null)
			//				struttAutop = struttAutopCv.getCode();
			//
			//			if (struttAutop==null || "".equals(struttAutop)){
			//				FacesErrorUtils.addErrorMessage("commError", "Strutture autoprotette impianto - campo non specificato.", "Strutture autoprotette impianto - campo non specificato.");
			//				return null;
			//			}

			//Se tipologia = '48 strutture autoprotette'

			CodeValuePhi tipologiaCv = impTerra.getTipologia();

			if(tipologiaCv!=null){
				tipologia = tipologiaCv.getCode();
			}

			if ("48".equals(tipologia)){
				/* tipoVerifica = Periodica o straordinaria
				 * La tariffa va moltiplicare per il numero di strutture */
				if (tipoVerifica!=null && "01 02".contains(tipoVerifica)){
					Double val = getCellValue(sheet.getRow(2).getCell(2));
					Integer numeroStrutture = impTerra.getStruttAutopNum();

					if (val!=null && numeroStrutture!=null)
						return val*numeroStrutture;
					else 
						return null;				
				}
			}else {

				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					Double val = getCellValue(sheet.getRow(1).getCell(1));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;

			}

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getImportoImpTerraB(VerificaImp v, ImpTerra impTerra, Sheet sheet) {
		try {
			String tipoVerifica = null;

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto
			 * 05 - Sopralluogo
			 * 06 - Straordinaria per accertamenti 
			 */
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}

			String impAutoprod = null;
			CodeValuePhi impAutoprodCv = impTerra.getImpAutoprod();

			if (impAutoprodCv!=null)
				impAutoprod = impAutoprodCv.getCode();

			if (impAutoprod==null || "".equals(impAutoprod)){
				FacesErrorUtils.addErrorMessage("commError", "Impianto di autoproduzione - campo non specificato.", "Impianto di autoproduzione - campo non specificato.");
				return null;
			}

			Double pot = impTerra.getPot();

			Integer numCabine = impTerra.getCabineNum();
			if (numCabine==null)
				numCabine=0;

			//tipoVerifica = Visita a vuoto --> I22
			if ("04".equals(tipoVerifica))
				return getCellValue(sheet.getRow(21).getCell(8));

			//tipoVerifica = Straordinaria per accertamenti --> F21*ore
			if ("06".equals(tipoVerifica)){

				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(20).getCell(5));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Se idovore = true e tipoVerifica = Periodica Straordinaria Sopralluogo --> H20*ore
			Boolean idrovore = v.getIdrovore()!= null? v.getIdrovore():false;
			if (idrovore && "01 02 05".contains(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(19).getCell(7));
					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//tipoVerifica = Sopralluogo --> J23
			if ("05".equals(tipoVerifica)){

				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(22).getCell(9));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Se impianto di autoproduzione = SI
			if ("YES".equals(impAutoprod)){

				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					Double val = getCellValue(sheet.getRow(18).getCell(2));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;

			} else //Se impianto di autoproduzione = NO, tipoVerifica = Periodica e potenza <1000 --> D2 -> D15 + (n° cabine * E18)
				if ("NO".equals(impAutoprod) && "01".equals(tipoVerifica) && pot <= 1000){

					Double val = null;

					if (pot<=10)
						val = getCellValue(sheet.getRow(1).getCell(3));
					else if (pot<=15)
						val = getCellValue(sheet.getRow(2).getCell(3));
					else if (pot<=20)
						val = getCellValue(sheet.getRow(3).getCell(3));
					else if (pot<=25)
						val = getCellValue(sheet.getRow(4).getCell(3));
					else if (pot<=50)
						val = getCellValue(sheet.getRow(5).getCell(3));
					else if (pot<=100)
						val = getCellValue(sheet.getRow(6).getCell(3));
					else if (pot<=150)
						val = getCellValue(sheet.getRow(7).getCell(3));
					else if (pot<=200)
						val = getCellValue(sheet.getRow(8).getCell(3));
					else if (pot<=250)
						val = getCellValue(sheet.getRow(9).getCell(3));
					else if (pot<=300)
						val = getCellValue(sheet.getRow(10).getCell(3));
					else if (pot<=415)
						val = getCellValue(sheet.getRow(11).getCell(3));
					else if (pot<=630)
						val = getCellValue(sheet.getRow(12).getCell(3));
					else if (pot<=800)
						val = getCellValue(sheet.getRow(13).getCell(3));
					else if (pot<=1000)
						val = getCellValue(sheet.getRow(14).getCell(3));

					if (numCabine>0)
						val += numCabine*getCellValue(sheet.getRow(17).getCell(4));

					return val;
				}

			//Se tipo verifica straordinaria --> F21 (sostituito da 06 - Straordinaria per accertamenti)
			//			if ("02".equals(tipoVerifica)){				
			//				Double tariffaOraria = null;
			//				Double min = getMinutiServizio(v);
			//
			//				//Calcolo la tariffa oraria
			//				if (min>0){
			//					Double val = getCellValue(sheet.getRow(20).getCell(5));
			//
			//					if (val!=null)
			//						tariffaOraria = val*min/60;
			//				}
			//
			//				return tariffaOraria;
			//			}

			//Se tipo verifica periodica e potenza>1000 --> tariffazione oraria con minimo G16
			if ("01".equals(tipoVerifica) && pot>1000) {
				Double g16 = getCellValue(sheet.getRow(15).getCell(6));

				Double tariffaOraria=0.0;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					Double val = getCellValue(sheet.getRow(16).getCell(6));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				if (tariffaOraria!=null && g16!=null){
					if (tariffaOraria>g16)
						return tariffaOraria;
					else 
						return g16;
				}

				return null;
			}


			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private Double getCellValue(Cell cell) {
		try {
			Double val = null;

			if (cell != null) {
				CellType cellType = cell.getCellTypeEnum();

				if (cellType == CellType.STRING)
					val = Double.parseDouble(cell.getStringCellValue());

				else if (cellType == CellType.NUMERIC){
					val = cell.getNumericCellValue();
					BigDecimal bigDecimal = new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP);
					String decimalToString = bigDecimal.toPlainString();
					val = Double.parseDouble(decimalToString);
				}
				else //CellType.BLANK | CellType.BOOLEAN | CellType.FORMULA etc.
					val = null;	        		
			}

			return val;

		} catch (Exception e) {
			log.error(e);
			//throw new RuntimeException(e);

			return null;
		}
	}

	private Double getImportoImpTerraC(VerificaImp v, ImpTerra impTerra, Sheet sheet) {
		try {			
			String tipoVerifica = null;

			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto 
			 * 05 - Sopralluogo
			 * 06 - Straordinaria per accertamenti
			 */
			CodeValuePhi tipoVerificaCv = v.getTipo();

			if (tipoVerificaCv!=null)
				tipoVerifica = tipoVerificaCv.getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica)){
				FacesErrorUtils.addErrorMessage("commError", "Tipo verifica non noto.", "Tipo verifica non noto.");
				return null;
			}

			Boolean primaVerifica = v.getPrima()!= null? v.getPrima():false;

			//tipoVerifica =  Sopralluogo  --> G6
			if ("05".equals(tipoVerifica))
				return getCellValue(sheet.getRow(5).getCell(6));

			//tipoVerifica =  Straordinaria per accertamenti --> F5*ore
			if ("06".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(4).getCell(5));
					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Prima verifica = NO e tipoVerifica != Visita a vuoto --> B2*ore
			if (!primaVerifica && !"04".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(1).getCell(1));
					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Prima verifica = NO e tipoVerifica =  Visita a vuoto  --> C3
			if (!primaVerifica && "04".equals(tipoVerifica))
				return getCellValue(sheet.getRow(2).getCell(2));

			//Prima verifica = SI e tipoVerifica != Visita a vuoto --> D4*ore
			if (primaVerifica && !"04".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					Double val =  getCellValue(sheet.getRow(3).getCell(3));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Prima verifica = SI e tipoVerifica =  Visita a vuoto  --> E3
			if (primaVerifica && "04".equals(tipoVerifica))
				return getCellValue(sheet.getRow(4).getCell(2));

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/* ECCEZIONE - Se Tipo apparecchio = 08 - REC. PRESS. AMBIENTE VITA --> Foglio 5 (PRESSIONE AMBIENTE VITA) */
	private Double getImportoRecPressAmbienteVita(VerificaImp v, ImpPress i, String tipoVerifica) {
		try {
			Sheet sheet = TariffarioAction.instance().getSheet(v.getData(), 5);
			Double val = null;

			Double fattoreMoltiplicativo = 1.0;

			//Sopralluogo --> H15*ore minuti
			if (tipoVerifica!=null && "05".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){

					val = getCellValue(sheet.getRow(14).getCell(7));

					if (val!=null)
						tariffaOraria = val*min/60;
				}

				return tariffaOraria;
			}

			//Visita a vuoto --> G14
			if (tipoVerifica!=null && "04".equals(tipoVerifica))
				val = getCellValue(sheet.getRow(13).getCell(6));

			//Periodica o Straordinaria - Calcolate allo stesso modo
			else if (tipoVerifica!=null && "01 02".contains(tipoVerifica)){

				Double press1 = i.getPressBar1();
				if (press1==null){
					FacesErrorUtils.addErrorMessage("commError", "Pressione impianto non nota.", "Pressione impianto non nota.");

					return null;
				}

				Double capacita = i.getCapacita();
				if (capacita==null){
					FacesErrorUtils.addErrorMessage("commError", "Capacità impianto non specificata.", "Capacità impianto non specificata.");
					return null;
				}

				//Pressione per capacità
				Double pXc = press1*capacita;

				/* 	00 Non esonerato
					01 Escluso
					02 Incompleto
					03 Parziale (GPL)
					04 Totale  */
				String esonero = null;			
				CodeValuePhi esoneroCv = i.getEsonero();

				if (esoneroCv!=null)
					esonero = esoneroCv.getCode();

				if (esonero != null && "03".equals(esonero)) {
					fattoreMoltiplicativo = 2.5;
				}

				//Colonna C per Periodica (01) ed E per Straordinaria (02)
				int cell = 2;
				if("02".equals(tipoVerifica))
					cell = 4;

				if (pXc<=27000)
					val = getCellValue(sheet.getRow(3).getCell(cell));

				else if (pXc<=125000)
					val = getCellValue(sheet.getRow(4).getCell(cell));

				else if (pXc<=343000)
					val = getCellValue(sheet.getRow(5).getCell(cell));

				else if (pXc<=729000)
					val = getCellValue(sheet.getRow(6).getCell(cell));

				else if (pXc<=1331000)
					val = getCellValue(sheet.getRow(7).getCell(cell));

				else if (pXc<=2197000)
					val = getCellValue(sheet.getRow(8).getCell(cell));

				else if (pXc<=3375000)
					val = getCellValue(sheet.getRow(9).getCell(cell));

				else if (pXc<=4913000)
					val = getCellValue(sheet.getRow(10).getCell(cell));

				else if (pXc<=5832000)
					val = getCellValue(sheet.getRow(11).getCell(cell));

				else if (pXc>5832000)
					val = getCellValue(sheet.getRow(12).getCell(cell));

			}
			//Straordinaria per accertamenti
			else if(tipoVerifica!=null && "06".equals(tipoVerifica)){
				Double tariffaOraria = null;
				Double min = getMinutiServizio(v);

				//Calcolo la tariffa oraria
				if (min>0){
					Double t = 64.45;
					tariffaOraria = t*min/60;
				}

				return tariffaOraria;

			}

			return val*fattoreMoltiplicativo;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/** Stampa l'elenco delle verifiche interne con la stessa data della nuova verifica (ver) che si sta creando **/
	public void checkVerifiche(VerificaImp ver, List<VerificaImp> verList){
		String errorMsg = "";

		if(ver==null || ver.getData()==null || verList==null || verList.size()<1){
			this.getTemporary().put("errorMsg", errorMsg);
			return;
		}

		int cont = 0;
		for (VerificaImp v:verList){
			CodeValuePhi tipoInOut = v.getTipoInOut();
			if (tipoInOut instanceof HibernateProxy) {
				tipoInOut = (CodeValuePhi)((HibernateProxy)tipoInOut).getHibernateLazyInitializer().getImplementation();
			}

			//Se la verifica è interna
			if (tipoInOut!=null && tipoInOut.getCode()!=null && "01".equals(tipoInOut.getCode())){
				if (v.getData()!=null && ver.getData()!=null && ver.getData().compareTo(v.getData())==0){
					cont++;
					errorMsg += "<b>-</b> Tipo verifica: " + (v.getTipo()!=null?v.getTipo().getCurrentTranslation():"?") +
							" eseguita dal Tecnico " + (v.getOperatore()!=null?v.getOperatore().getName():"?") + "<br>";
				}
			}
		}

		if (cont>0){
			if (cont==1)
				errorMsg = "<b>Attenzione!</b> Per il giorno indicato è già presente la seguente verifica interna:<br> " + errorMsg;
			else 				
				errorMsg = "<b>Attenzione!</b> Per il giorno indicato sono già presenti le seguenti verifiche interne:<br>" + errorMsg;
		}

		this.getTemporary().put("errorMsg", errorMsg);

	}

	public boolean checkVerificheMulti(List<Long> verifiche){
		boolean isValidMulti = true;

		if(verifiche==null || verifiche.isEmpty()){
			return isValidMulti;
		}

		List<Long> verificheOk = new ArrayList<Long>();
		Iterator<Long> selectedVersIterator = verifiche.iterator();
		while(selectedVersIterator.hasNext()){
			Long verId = selectedVersIterator.next();
			VerificaImp ver = ca.get(VerificaImp.class, verId);

			// Controlli duplicati e rimozioni non riucite nella lista deglle verifiche da validare
			if(ver.getStatusCode()!=null && !ver.getStatusCode().getOid().equals("phidic.arpav.ver.stato.new")){
				selectedVersIterator.remove();
				continue;
			}

			ver.setIsChecked(true);

			/* Effettua il controllo sul campo Ore/Minuti per le sole verifiche interne */
			String tipoInOut = null;
			CodeValuePhi tipoInOutCv = ver.getTipoInOut();

			if (tipoInOutCv!=null)
				tipoInOut = tipoInOutCv.getCode();

			if ("01".equals(tipoInOut)){
				boolean isValidOreMinuti  = checkOreMinutiMulti(ver);
				if (!isValidOreMinuti){
					if(isValidMulti){
						isValidMulti = false;
					}
					continue;
				}
				//Se non viene settata la sede ARPAV non consente la validazione 
				if (ver.getServiceDeliveryLocation()==null){
					if(isValidMulti){
						isValidMulti = false;
					}
					continue;
				}
			}

			//Controlla che nella verifica sia settata la sede di addebito
			if (ver.getSedi()==null || ver.getSedi().getSedeAddebito()==null || ver.getSedi().getSedeAddebito()==false){
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}


			//Se l'impianto è in stato domolito non fa nessun controllo sulla data prossima verifica
			if (ver.getStatoImp()!=null && "08".equals(ver.getStatoImp().getCode())){
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			//Escludendo gli impianti a pressione, se non è stata inserita la data prossima verifica non consente la validazione 
			if(ver.getImpPressCpy()==null && ver.getNextVerifDate1()==null) {
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			} 

			//Per gli impianti a pressione, se non è stata inserita almeno una data prossima verifica non consente la validazione
			if (ver.getImpPressCpy()!=null && ver.getNextVerifDate1()==null) {
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			verificheOk.add(verId);
		}

		this.getTemporary().put("selectedVers", verifiche);
		this.getTemporary().put("verificheOk", verificheOk);
		return isValidMulti;
	}

	public String getErrMsgFromTmp(List<Long> unvalidated){
		if(unvalidated==null)
			return "";

		String errorMsg = "VERIFICHE NON VALIDATE<br/>";

		for(Long verId : unvalidated){
			VerificaImp ver = ca.get(VerificaImp.class, verId);
			errorMsg += "NUMERO: " + verId + " - IMPIANTO: " + ver.getImpSearchCollector().getSigla() + 
					"&#47;" + ver.getImpSearchCollector().getMatricola() + "&#47;" + ver.getImpSearchCollector().getAnno() + "<br/>";
		}
		return errorMsg;
	}

	@SuppressWarnings("unchecked")
	public void validateMulti(List<Long> verIds){
		if(verIds==null || verIds.isEmpty()){
			return;
		}

		boolean validationOk = true;
		List<Long> selectedVers = (List<Long>)this.getTemporary().get("selectedVers");

		CodeValuePhi codeValueStatus = null;
//		HashMap<String, CodeValuePhi> causali = new HashMap<String, CodeValuePhi>();	
		Vocabularies voc = VocabulariesImpl.instance();
		try {
			codeValueStatus = (CodeValuePhi)voc.getCodeValue("PHIDIC", "Stato", "verified", "C");

//			String[] causaleOids = {"04","04R","23","23R","24","24R","25","25R","26","26R","36","36R","86","86R"};
//			for(String causaleOid: causaleOids){
//				causaleOid = "phidic.arpav.ver.causale." + causaleOid;
//				causali.put(causaleOid, (CodeValuePhi)voc.getCodeValueOid(causaleOid));
//			}
		} catch (DictionaryException e) {
			log.error("Error persisting loading CodeValue ");
		} catch (PhiException e) {
			log.error("Error persisting loading CodeValue ");
		}

		for(Long vId : verIds){
			VerificaImp ver = ca.get(VerificaImp.class, vId);

			// Controlli duplicati e rimozioni non riucite nella lista degli addebiti da validare
			if(ver.getStatusCode()!=null && !ver.getStatusCode().getOid().equals("phidic.arpav.ver.stato.new")){
				selectedVers.remove(vId);
				continue;
			}

			String qCount = "SELECT COUNT(*) FROM DataBaseLog l";
			org.hibernate.Query queryCount = ca.createHibernateQuery(qCount);
			Long insertedRows = (Long) queryCount.uniqueResult();
			String qWrite = null;
			if(insertedRows==0L){
				qWrite = "INSERT INTO db_log (created_by, is_active, message_txt, object_class, object_identifier) " +
						"VALUES('" + this.getClass().getSimpleName() +
						"', 1, '', '" + ver.getClass().getSimpleName() + 
						"', '" + vId + "')";
			}else{
				qWrite = "UPDATE db_log " + 
						"SET object_class='" + ver.getClass().getSimpleName() +
						"', object_identifier='" + vId + "' WHERE internal_id=1";

			}
			try {
				GenericAdapterLocalInterface ga=GenericAdapter.instance();
				ga.executeUpdateNative(qWrite,null);
			} catch (PersistenceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			SimpleDateFormat sdfLog = new SimpleDateFormat("HH:mm:ss.SS");
//			log.info("Update log time: " + sdfLog.format(new Date()));

			boolean importoOk = this.setImporto(ver);
			if(!importoOk){
				if(validationOk){
					validationOk = false;
				}
				continue;
			}

//			log.info("Setting importo time: " + sdfLog.format(new Date()));

			this.setCodiceConto(ver);
			//this.getTemporary().put("impTemp", ver.getImporto());


//			String code = null;
			/* code - tipoImpianto: 
			 * 01 - Pressione
			 * 02 - Riscaldamento
			 * 03 - Ascensori e montacarichi
			 * 04 - Apparecchi di sollevamento 
			 * 05 - Impianti elettrici */
//			code = this.getImpiantoCode(ver); 			

			//			if ("".equals(code))
			//				return;

			// Esenzione: la causale è null
//			if(this.isEsente(ver)){
//				ver.setCausale(null);
//			}
//
//			String tipoAppPress = null;
//			if("01".equals(code)){
//				ImpPress imp = ver.getImpPress();
//				CodeValuePhi tipoAppCv = imp.getTipoApparecchio();
//				if(tipoAppCv!=null){
//					tipoAppPress = tipoAppCv.getCode();
//				}
//			}
//
//			String tipoImpRisc = null;
//			if("02".equals(code)){
//				ImpRisc imp = ver.getImpRisc();
//				CodeValuePhi destinazioneCv = imp.getDescrizImpianto();
//				if(destinazioneCv!=null){
//					tipoImpRisc = destinazioneCv.getCode();
//				}
//			}
//
//			String tipoVerifica = null;
			/* tipoVerifica: 
			 * 01 - Periodica
			 * 02 - Straordinaria
			 * 03 - Constatazione (eliminato)
			 * 04 - Visita a vuoto 
			 * 05 - Sopralluogo 
			 * 06 - Straordinaria per accertamenti
			 * 07 - Straordinaria per modifiche costruttive*/
//			CodeValuePhi tipoVerificaCv = ver.getTipo();
//
//			if (tipoVerificaCv!=null)
//				tipoVerifica = tipoVerificaCv.getCode();
//
//			if (tipoVerifica==null || "".equals(code))
//				return;
//
//			CodeValuePhi causale = null;
//			String causaleOid = "";
			//Se tipoVerifica: Visita a vuoto -> Causale: 86 - Verifica a vuoto
//			if ("04".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.86");
//				causaleOid = "86";

			//Se tipoVerifica: Sopralluogo -> Causale: 26 - Sopralluoghi e varie
//			else if ("05".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.26");
//				causaleOid = "26";

			//Se tipoVerifica: Periodica
//			else if ("01".equals(tipoVerifica)){
				//Se tipoImpianto: Pressione -> 36 - Quota ann. e prove period.reg.
//				if ("01".equals(code))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.36");
//					causaleOid = "36";
//				else 
					//Se tipoImpianto <> Pressione -> 23 - Verifica periodica
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.23");
//					causaleOid = "23";
//			}

			//Se tipoVerifica: Straordinaria -> Causale 24 VERIFICA STRAORDINARIA
//			else if("02".equals(tipoVerifica))
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.24");
//				causaleOid = "24";

			//Se tipoVerifica: Straordinaria per accertamenti
//			else if ("06".equals(tipoVerifica)){
				//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.25");
//				causaleOid = "25";
//			}

			//modifiche costruttive solo ascensori
//			else if ("07".equals(tipoVerifica)){
//				if ("03".equals(code))
//					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.04");
//					causaleOid = "04";
//			}
//
//			if(("01".equals(code) && "08".equals(tipoAppPress)) || ("02".equals(code) && "01 02 03".contains(tipoImpRisc)) ||
//					"03 05".contains(code)){
//				causaleOid += "R";
//			}
//
//			causale = causali.get(causaleOid);
//
//			if (causale!=null)
//				ver.setCausale(causale);

			this.setCausale(ver);
			
			if (ver.getSedi()==null){
				if(ver.getImpPress()!=null){
					ver.setSedi(ver.getImpPress().getSedi());
				}else if(ver.getImpRisc()!=null){
					ver.setSedi(ver.getImpRisc().getSedi());
				}else if(ver.getImpMonta()!=null){
					ver.setSedi(ver.getImpMonta().getSedi());
				}else if(ver.getImpSoll()!=null){
					ver.setSedi(ver.getImpSoll().getSedi());
				}else if(ver.getImpTerra()!=null){
					ver.setSedi(ver.getImpTerra().getSedi());
				}
			}

			VerificaImp verificaLast = null;

			ImpiantoAction ia = ImpiantoAction.instance();
			if(ver.getImpPress()!=null){
				ImpPress imp = ver.getImpPress();
				ImpPressAction.instance().updateImpiantoFromCopy(imp, ver.getImpPressCpy());

				//				ImpPress imp = ca.get(ImpPress.class, ver.getImpPress().getInternalId());
				//				ver.setImpPress(imp);

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
				ImpRiscAction.instance().updateImpiantoFromCopy(imp, ver.getImpRiscCpy());

				//				ImpRisc imp = ca.get(ImpRisc.class, ver.getImpRisc().getInternalId());
				//				ver.setImpRisc(imp);

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
				ImpMontaAction.instance().updateImpiantoFromCopy(imp, ver.getImpMontaCpy());

				//				ImpMonta imp = ca.get(ImpMonta.class, ver.getImpMonta().getInternalId());
				//				ver.setImpMonta(imp);

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
				ImpSollAction.instance().updateImpiantoFromCopy(imp, ver.getImpSollCpy());

				//				ImpSoll imp = ca.get(ImpSoll.class, ver.getImpSoll().getInternalId());
				//				ver.setImpSoll(imp);

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
				ImpTerraAction.instance().updateImpiantoFromCopy(imp, ver.getImpTerraCpy());

				//				ImpTerra imp = ca.get(ImpTerra.class, ver.getImpTerra().getInternalId());
				//				ver.setImpTerra(imp);

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
			ver.setIsChecked(null);

			ver.setStatusCode(codeValueStatus);
			try {
				ca.create(ver);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			log.info("Saving time: " + sdfLog.format(new Date()));

			this.updateImpianto(verificaLast);
			selectedVers.remove(vId);

		}
	}

	/* Ai fini del salvataggio della verifica devono essere valorizzati sede ARPAV e, per quelle di tipo interno, il tecnico della verifica.
	 * Deve essere opportunamente valorizzato il campo Ore/Minuti
	 * Per evitarte duplicazioni non deve essere possibile inserire (per lo stesso impianto) due verifiche interne dello stesso tipo, con la 
	 * stessa data e con lo stesso tecnico */
	public boolean checkVerificaPreSave(String impType, VerificaImp ver, List<VerificaImp> verList){
		boolean isValid = true;

		if(ver!=null && impType!=null && !"".equals(impType)){

			/* Effettua il controllo sul campo Ore/Minuti per le sole verifiche interne */
			String tipoInOut = null;
			CodeValuePhi tipoInOutCv = ver.getTipoInOut();

			if (tipoInOutCv!=null)
				tipoInOut = tipoInOutCv.getCode();

			if ("01".equals(tipoInOut)){
				isValid = checkOreMinuti(impType, ver);

				if(ver.getServiceDeliveryLocation()==null){
					FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV obbligatoria.", "Sede ARPAV obbligatoria.");
					isValid = false;				
				}

				if(ver.getOperatore()==null){
					FacesErrorUtils.addErrorMessage("commError", "Tecnico della verifica obbligatorio.", "Tecnico della verifica obbligatorio.");
					isValid = false;
				}

				if("01".equals(impType)){
					ImpPress impCopy = ver.getImpPressCpy();
					String tipoApparecchio = null;
					if(impCopy!=null){
						CodeValuePhi tipoApparecchioCv = impCopy.getTipoApparecchio();
						if(tipoApparecchioCv!=null){
							tipoApparecchio = tipoApparecchioCv.getCode();
						}
					}

					if("08".equals(tipoApparecchio)){
						CodeValuePhi tipoVerificaCv = ver.getTipo();
						if(tipoVerificaCv!=null){
							String tipoVerifica = tipoVerificaCv.getCode();
							if("06".equals(tipoVerifica) && ver.getTipoStr()==null){
								FacesErrorUtils.addErrorMessage("commError", "Tipo verifica straordinaria per accertamenti obbligatorio.", "Tipo verifica straordinaria per accertamenti obbligatorio.");
								isValid = false;
							}
						}
					}
				}

				if("02".equals(impType)){
					ImpRisc impCopy = ver.getImpRiscCpy();
					String destinazImp = null;
					if(impCopy!=null){
						CodeValuePhi destinazImpCv = impCopy.getDescrizImpianto();
						if(destinazImpCv!=null){
							destinazImp = destinazImpCv.getCode();
						}
					}

					if("01 02 03".contains(destinazImp)){
						CodeValuePhi tipoVerificaCv = ver.getTipo();
						if(tipoVerificaCv!=null){
							String tipoVerifica = tipoVerificaCv.getCode();
							if("06".equals(tipoVerifica) && ver.getTipoStr()==null){
								FacesErrorUtils.addErrorMessage("commError", "Tipo verifica straordinaria per accertamenti obbligatorio.", "Tipo verifica straordinaria per accertamenti obbligatorio.");
								isValid = false;
							}
						}
					}
				}

				if("05".equals(impType)){
					CodeValuePhi tipoVerificaCv = ver.getTipo();
					if(tipoVerificaCv!=null){
						String tipoVerifica = tipoVerificaCv.getCode();
						if("06".equals(tipoVerifica) && ver.getTipoStr()==null){
							FacesErrorUtils.addErrorMessage("commError", "Tipo verifica straordinaria per accertamenti obbligatorio.", "Tipo verifica straordinaria per accertamenti obbligatorio.");
							isValid = false;
						}
					}
				}

				/** Controllo di non duplicazione - per le sole verifiche interne **/
				for (VerificaImp v:verList){
					if (!ver.equals(v)){
						CodeValuePhi inOut = v.getTipoInOut();
						if (inOut instanceof HibernateProxy) {
							inOut = (CodeValuePhi)((HibernateProxy)inOut).getHibernateLazyInitializer().getImplementation();
						}

						//Se la verifica è interna
						if (inOut!=null && inOut.getCode()!=null && "01".equals(inOut.getCode())){
							//Se hanno la stessa data
							if (v.getData()!=null && ver.getData()!=null && ver.getData().compareTo(v.getData())==0){
								//Se sono dello stesso tipo
								if (v.getTipo()!=null && ver.getTipo()!=null && ver.getTipo().equals(v.getTipo())) {
									//Ss hanno lo stesso tecnico
									if (v.getOperatore()!=null && ver.getOperatore()!=null && ver.getOperatore().equals(v.getOperatore())){
										FacesErrorUtils.addErrorMessage("commError", "Esiste già una verifica interna con le medesime caratteristiche di Data, Tipo e Operatore. Impossibile procedere con il salvataggio.", "Esiste già una verifica interna con le medesime caratteristiche di Data, Tipo e Operatore. Impossibile procedere con il salvataggio.");
										isValid = false;
									}
								}
							}
						}
					}
				}
			}
		}

		if (isValid)
			this.getTemporary().remove("errorMsg");

		return isValid;	
	}

	/* Funzione chiamata in fase di salvataggio della verifica per valutare l'obbligatorietà del campo ore/minuti */ 
	private boolean checkOreMinuti(String impType, VerificaImp ver) {
		try{
			/* impType: 	(01) APPARECCHI A PRESSIONE 						*
			 * 				(02) IMPIANTI DI RISCALDAMENTO 						*
			 * 				(03) ASCENSORI E MONTACARICHI						*
			 * 				(04) APPARECCHI DI SOLLEVAMENTO						*
			 * 				(05) IMPIANTI ELETTRICI (tipo A, tipo b, tipo c) 	*
			 *  																*
			 * tipoVerifica:(01) PERIODICA										*
			 * 				(02) STRAORDINARIA									*
			 * 				(04) VISITA A VUOTO									*
			 * 				(05) SOPRALLUOGO 									
			 *              (06) STRAORDINARIA PER ACCERTAMENTI*/

			boolean isValid = true;
			String error = "Ore o Minuti Servizio: campo obbligatorio.";

			String tipoVerifica = null;
			if (ver.getTipo()!=null)
				tipoVerifica = ver.getTipo().getCode();

			//Non dovrebbe poter verificarsi essendo il campo a selezione obbligatoria
			if (tipoVerifica==null || "".equals(tipoVerifica))
				return isValid;

			//Controllo se il campo ore/minuti è valorizzato
			boolean oreMinutiValorizzato = false;

			String ore = ver.getHhServizio();
			String min = ver.getMmServizio();

			if ((ore!=null && !"".equals(ore)) || (min!=null && !"".equals(min)))
				oreMinutiValorizzato = true;

			String tipoVerificaStraordinaria = null;
			if (ver.getTipoStr()!=null)
				tipoVerificaStraordinaria = ver.getTipoStr().getCode();

			//APPARECCHI A PRESSIONE 
			if("01".equals(impType)){

				ImpPress imp = ver.getImpPressCpy();

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true;

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio per qualsiasi tipoApparecchio tranne nel caso di 08 - REC. PRESS. AMBIENTE VITA 
				if ("02 04".contains(tipoVerifica)){
					String tipoApparecchio = null;
					CodeValuePhi tipoApparecchioCv = imp.getTipoApparecchio();

					if (tipoApparecchioCv!=null){
						tipoApparecchio = tipoApparecchioCv.getCode();
						if (tipoApparecchio!=null && "08".equals(tipoApparecchio))//bbb
							return isValid = true;
					}

					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}

					//return isValid = true;
				}

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//IMPIANTI DI RISCALDAMENTO
			} else if("02".equals(impType)){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true; 

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio solo se DESTINAZIONE IMPIANTO = ACQUA CALDA PER FINI PRODUTTIVI
				if ("02 04".contains(tipoVerifica)){

					/* descrizImpianto:
					 * 01 Riscaldamento ambienti
					 * 02 Acqua calda per servizi
					 * 03 Riscaldamento e servizi
					 * 04 Acqua calda per fini produttivi */
					String destinazioneImpianto = null;
					ImpRisc imp = ver.getImpRiscCpy();

					if (imp!=null && imp.getDescrizImpianto()!=null)
						destinazioneImpianto = imp.getDescrizImpianto().getCode();

					if (destinazioneImpianto!=null && "04".equals(destinazioneImpianto)){
						if (!oreMinutiValorizzato){
							FacesErrorUtils.addErrorMessage("commError", error, error);
							return isValid = false;	
						}
					}

					//return isValid = true;
				}

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}

					//return isValid = true;	
				}

				//ASCENSORI E MONTACARICHI	
			} else if("03".equals(impType)){

				//Verifica PERIODICA - Presente obbligatorio
				if ("01".equals(tipoVerifica)){
					//					if (!oreMinutiValorizzato){
					//						FacesErrorUtils.addErrorMessage("commError", error, error);
					//						return isValid = false;	
					//					}

					return isValid = true;	
				}

				//Verifica STRAORDINARIA
				if ("02".equals(tipoVerifica)){
					//					if (tipoVerificaStraordinaria!=null && "03".equals(tipoVerificaStraordinaria)){
					//						if (!oreMinutiValorizzato){
					//							FacesErrorUtils.addErrorMessage("commError", error, error);
					//							return isValid = false;	
					//						}
					//					}

					return isValid = true;	
				}

				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica))
					return isValid = true; 

				//SOPRALLUOGO - Presente obbligatorio per qualsiasi tipoVerificaSopralluogo
				if ("05".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//Verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//APPARECCHI DI SOLLEVAMENTO
			} else if("04".equals(impType)){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true; 

				//Verifica STRAORDINARIA - Presente obbligatorio indipendentemente dal tipoVerificaStraordinaria. 
				//Per gli apparecchi di sollevamento è selezionabile solo la voce 03 (A: STRAORDINARIA PER ACCERTAMENTI)
				if ("02".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;
					}
				}

				//VISITA A VUOTO - Presente obbligatorio
				if ("04".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					} 
				}

				//SOPRALLUOGO - Presente obbligatorio per qualsiasi tipoVerificaSopralluogo
				if ("05".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//Verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					} 
				}

				/* IMPIANTI ELETTRICI (01 - Tipo A, 02 - Tipo B, 03 - Tipo C)  - indipendentemente dal tipo il comportamento sembra essere lo stesso.
				 * C'è una sola eccezione per impianti di Tipo A - in questo caso, se il campo "48 Strutture autoprotette" = YES, nelle verifiche 
				 * periodiche il campo ore/minuti non è obbligatorio. */
			} else if("05".equals(impType)){

				ImpTerra imp = ver.getImpTerraCpy();

				String tipoImpiantoElettrico = null;
				CodeValuePhi tie = imp.getSubTypeTerra();

				if (tie!=null)
					tipoImpiantoElettrico = tie.getCode();

				if (tipoImpiantoElettrico==null || "".equals(tipoImpiantoElettrico))
					return isValid = true;

				//Verifica PERIODICA - Presente obbligatorio tranne nel caso di impianti di Tipo A e Tipoligia = 48 Strutture autoprotette
				// Presente non obbligatorio per impianti di tipo B SOTTOTIPO 51 SENZA AUTOPRODUZIONE
				if ("01".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){

						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && "48".equals(tipologia))
								return isValid = true; 
						}
					}	
					//Se Tipo B
					if("02".equals(tipoImpiantoElettrico)){
						if(imp.getSubTypeB()!=null){
							String sottotipoB = imp.getSubTypeB().getCode();
							String autoProd = null;
							if(imp.getImpAutoprod()!=null){
								autoProd = imp.getImpAutoprod().getCode();
							}
							if(sottotipoB!=null && "01".equals(sottotipoB) && autoProd!=null && "NO".equals(autoProd)){
								return isValid = true; 
							}
						}
					}

					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}

					return isValid = true;
				}

				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica))
					return isValid = true;

				//Straordinaria - Presente obbligatorio per tipo A <> tipologia 48
				if ("02".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){
						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && !"48".equals(tipologia) && !oreMinutiValorizzato){
								FacesErrorUtils.addErrorMessage("commError", error, error);
								return isValid = false;	
							}
						}
					}
					return isValid = true;
				}

				//SOPRALLUOGO e STRAORDINARIA PER ACCERTAMENTI- Presente obbligatorio
				if ("05 06".contains(tipoVerifica)){
					if (!oreMinutiValorizzato){
						FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}
			}

			return isValid;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Funzione chiamata dai jollyWidget di scheda_verifiche_*.mmgp per mostrare il layout required sul componente di input */
	public boolean oreMinutiServizioRequired(String impType, VerificaImp ver) {
		try {

			if (ver == null || impType==null || "".equals(impType))
				return false;

			/* impType: 	(01) APPARECCHI A PRESSIONE 						*
			 * 				(02) IMPIANTI DI RISCALDAMENTO 						*
			 * 				(03) ASCENSORI E MONTACARICHI						*
			 * 				(04) APPARECCHI DI SOLLEVAMENTO						*
			 * 				(05) IMPIANTI ELETTRICI (tipo A, tipo b, tipo c) 	*
			 *  																*
			 * tipoVerifica:(01) PERIODICA										*
			 * 				(02) STRAORDINARIA									*
			 * 				(04) VISITA A VUOTO									*
			 * 				(05) SOPRALLUOGO 									
			 *              (06) STRAORDINARIA PER ACCERTAMENTI
			 *              (07) STRAORDINARIA PER MODIFICHE COSTRUITTIVE*/
			String tipoVerifica = null;
			if (ver.getTipo()!=null)
				tipoVerifica = ver.getTipo().getCode();

			if (tipoVerifica==null || "".equals(tipoVerifica))
				return false;

			String tipoVerificaStraordinaria = null;
			if (ver.getTipoStr()!=null)
				tipoVerificaStraordinaria = ver.getTipoStr().getCode();

			//APPARECCHI A PRESSIONE 
			if("01".equals(impType)){
				ImpPress imp = ver.getImpPressCpy();

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return false; 

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio per qualsiasi tipoApparecchio tranne nel caso di 08 - REC. PRESS. AMBIENTE VITA 
				if ("02 04".contains(tipoVerifica)){
					String tipoApparecchio = null;
					CodeValuePhi tipoApparecchioCv = imp.getTipoApparecchio();

					if (tipoApparecchioCv!=null){
						tipoApparecchio = tipoApparecchioCv.getCode();
						if (tipoApparecchio!=null && "08".equals(tipoApparecchio))
							return false;
					}

					return true;
				}

				// verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica))					
					return true;

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica))					
					return true;

				//IMPIANTI DI RISCALDAMENTO
			} else if("02".equals(impType)){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return false;

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio solo se DESTINAZIONE IMPIANTO = ACQUA CALDA PER FINI PRODUTTIVI
				if ("02 04".contains(tipoVerifica)){

					/* descrizImpianto:
					 * 01 Riscaldamento ambienti
					 * 02 Acqua calda per servizi
					 * 03 Riscaldamento e servizi
					 * 04 Acqua calda per fini produttivi */
					String destinazioneImpianto = null;
					ImpRisc imp = ver.getImpRiscCpy();

					if (imp!=null && imp.getDescrizImpianto()!=null)
						destinazioneImpianto = ((ImpRisc)imp).getDescrizImpianto().getCode();

					if (destinazioneImpianto!=null && "04".equals(destinazioneImpianto))
						return true;

					return false;
				}

				// verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica))					
					return true;

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica))					
					return true;

				//ASCENSORI E MONTACARICHI	
			} else if("03".equals(impType)){

				//Verifica PERIODICA - Presente obbligatorio
				if ("01".equals(tipoVerifica))
					return false; 

				//Verifica STRAORDINARIA 
				if ("02".equals(tipoVerifica)){
					//					if (tipoVerificaStraordinaria!=null && "03".equals(tipoVerificaStraordinaria))
					//						return true;

					return false;
				}

				//Verifica STRAORDINARIA PER ACCERTAMENTI
				if ("06".equals(tipoVerifica))
					return true;

				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica))
					return false;

				//SOPRALLUOGO - Presente obbligatorio per qualsiasi tipoVerificaSopralluogo
				if ("05".equals(tipoVerifica))
					return true;

				//APPARECCHI DI SOLLEVAMENTO
			} else if("04".equals(impType)){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return false; 

				//Verifica STRAORDINARIA - Presente obbligatorio indipendentemente dal tipoVerificaStraordinaria. 
				//Per gli apparecchi di sollevamento è selezionabile solo la voce 03 (A: STRAORDINARIA PER ACCERTAMENTI)
				if ("02".equals(tipoVerifica))
					return true;

				//VISITA A VUOTO - Presente obbligatorio
				if ("04".equals(tipoVerifica))
					return true;


				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica))
					return true;

				//Verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica))
					return true;

				/* IMPIANTI ELETTRICI (01 - Tipo A, 02 - Tipo B, 03 - Tipo C)  - indipendentemente dal tipo il comportamento sembra essere lo stesso.
				 * C'è una sola eccezione per impianti di Tipo A - in questo caso, se 48 Strutture autoprotette = YES, nelle verifiche 
				 * periodiche il campo ore/minuti non è obbligatorio. */
			} else if("05".equals(impType)){

				ImpTerra imp = ver.getImpTerraCpy();

				String tipoImpiantoElettrico = null;
				CodeValuePhi tie = imp.getSubTypeTerra();

				if (tie!=null)
					tipoImpiantoElettrico = tie.getCode();

				if (tipoImpiantoElettrico==null || "".equals(tipoImpiantoElettrico))
					return false;

				//Verifica PERIODICA - Presente obbligatorio tranne nel caso di impianti di Tipo A e 48 Strutture autoprotette = YES
				// Presente non obbligatorio per impianti di tipo B SOTTOTIPO 51 SENZA AUTOPRODUZIONE
				if ("01".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){
						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && "48".equals(tipologia))
								return false; 
						}
					}
					//Se Tipo B
					if("02".equals(tipoImpiantoElettrico)){
						if(imp.getSubTypeB()!=null){
							String sottotipoB = imp.getSubTypeB().getCode();
							String autoProd = null;
							if(imp.getImpAutoprod()!=null){
								autoProd = imp.getImpAutoprod().getCode();
							}
							if(sottotipoB!=null && "01".equals(sottotipoB) && autoProd!=null && "NO".equals(autoProd)){
								return false;
							}
						}
					}
					return true;
				}

				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica)){
					return false;
				}

				//Straordinaria - Presente obbligatorio per tipo A <> tipologia 48
				if ("02".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){
						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && "48".equals(tipologia))
								return false; 
						}
					}
					return true;
				}

				//SOPRALLUOGO e STRAORDINARIA PER ACCERTAMENTI- Presente obbligatorio
				if ("05 06".contains(tipoVerifica))
					return true;
			}

			//Non dovrebbe mai verificarsi
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void clearNoteImporto(Object importo){
		if(entity!=null && (importo==null || ((String)importo).isEmpty())) {
			entity.setNoteImporto(null);
		}
	}


	public void recallStatoImpianto(VerificaImp verifica, Impianto imp) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(verifica!=null && verifica.getData()!=null && imp!=null){
			ImpiantoAction impAction = ImpiantoAction.instance();
			List<Impianto> lista = impAction.getStatusHistoryList(imp);
			if(lista==null || lista.isEmpty())
				return;
			/*
			 * La storia è ordinata per revisioni decrescenti. Siccome non è mai possibile inserire data variazione stato minore 
			 * della data variazione stato della versione precedente, la lista è anche ordinata per data variazione stato decrescente
			 * Le prime 2 valorizzazioni sono per il caso in cui in anagrafica non sia mai stata valorizzata la data variazione stato
			 */
			verifica.setStatoImp(lista.get(0).getStatoImpianto());
			verifica.setDataVar(lista.get(0).getDataVariazioneStato());
			if(lista!=null && !lista.isEmpty()){
				for(Impianto i : lista){
					if(i.getDataVariazioneStato()!=null && i.getDataVariazioneStato().before(verifica.getData())){
						verifica.setStatoImp(i.getStatoImpianto());
						verifica.setDataVar(i.getDataVariazioneStato());
						break;
					}
				}
			}
		}
	}

	public void updateStatoImpianto(VerificaImp verifica, Impianto imp) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(verifica!=null && verifica.getData()!=null && imp!=null){
			if(imp.getStatoImpianto()!=null && verifica.getStatoImp()!=null && !imp.getStatoImpianto().getCode().equals(verifica.getStatoImp().getCode())){
				if(imp.getDataVariazioneStato()!=null && verifica.getDataVar()!=null && verifica.getDataVar().after(imp.getDataVariazioneStato())){
					imp.setStatoImpianto(verifica.getStatoImp());
					imp.setDataVariazioneStato(verifica.getDataVar());
				}else if(imp.getDataVariazioneStato()==null){
					imp.setStatoImpianto(verifica.getStatoImp());
					imp.setDataVariazioneStato(verifica.getDataVar());
				}
			}
		}
	}

	public Boolean checkStatoImpianto(Impianto imp) {
		getTemporary().remove("warningMsg");
		boolean isValid = true;
		if(entity.getDataVar()==null){
			if(entity.getStatoImp()!=null && !"00".equals(entity.getStatoImp().getCode())){
				FacesErrorUtils.addErrorMessage("commError", "Data variazione stato impianto obbligatoria.", "Data variazione stato impianto obbligatoria.");
				isValid = false;	
				//2.se l'impianto torna ad essere attivo dopo un period di di inattività => se l'ultima revisione ha data
				/*
				 * Concordato con Saracini/Binotto: l'unica situazione in cui si può registrare una verifica con data variazione stato null
				 * è quello in cui anche l'impianto in anagrafica, in quel momento, ha data variazione stato null
				 */
			}else if(imp.getDataVariazioneStato()!=null) {
				FacesErrorUtils.addErrorMessage("commError", "Data variazione stato impianto obbligatoria.", "Data variazione stato impianto obbligatoria.");
				isValid = false;
			}
		}
		return isValid;

		/*boolean before=false, after = false;
		if(entity!=null && entity.getStatoImpianto()!=null && entity.getDataVariazioneStato()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String message = "";	
			FunctionsBean fn = FunctionsBean.instance();
			Query qVerifiche = ca.createQuery("select distinct v.internalId, v.statoImp, v.data from VerificaImp v " +
					"join v.statusCode status " +
					"left join v.impMonta monta " +
					"left join v.impPress press " +
					"left join v.impRisc risc " +
					"left join v.impSoll soll " +
					"left join v.impTerra terra " +
					"where v.statoImp <> :statoImp and status.code in ('new','verified') " +
					"and (monta.internalId=:impId or press.internalId=:impId or risc.internalId=:impId or soll.internalId=:impId or terra.internalId=:impId) " +
					"order by v.dataVar");

			qVerifiche.setParameter("statoImp", entity.getStatoImpianto()).setParameter("impId", entity.getInternalId());

			List<Object[]> lst = qVerifiche.getResultList();

			if(lst!=null && lst.size()>0){
				for(Object[] ver : lst){
					if(!before && ver[1]!=null && ver[2]!=null && entity.getDataVariazioneStato().before((Date) ver[2])){
						message += fn.getTranslation("warnImp01")+ " ("+ ((CodeValue)ver[1]).getCurrentTranslation() + " in data " +
								sdf.format((Date) ver[2])+ ") "+ fn.getTranslation("aCapo");
						before=true;
					}
					if(!after && ver[1]!=null && ver[2]!=null && entity.getDataVariazioneStato().after((Date) ver[2])){
						message += fn.getTranslation("warnImp02")+ " ("+ ((CodeValue)ver[1]).getCurrentTranslation() + " in data " +
								sdf.format((Date) ver[2])+ ") "+ fn.getTranslation("aCapo");
						after=true;
					}
				}

				getTemporary().put("warningMsg",message);
			}
		}
		return !(before||after); //ok*/


	}

	public String printSI(VerificaImp v){
		String out = "";
		SediInstallazione si = null;
		if(v!=null){
			String impType = getImpiantoCode(v);

			if("01".equals(impType) && v.getImpPressCpy()!=null) {
				si = v.getImpPressCpy().getSedeInstallazione();
			}else if("02".equals(impType) && v.getImpRiscCpy()!=null) {
				si = v.getImpRiscCpy().getSedeInstallazione();
			}else if("03".equals(impType) && v.getImpMontaCpy()!=null) {
				si = v.getImpMontaCpy().getSedeInstallazione();
			}else if("04".equals(impType) && v.getImpSollCpy()!=null) {
				si = v.getImpSollCpy().getSedeInstallazione();
			}else if("05".equals(impType) && v.getImpTerraCpy()!=null) {
				si = v.getImpTerraCpy().getSedeInstallazione();
			}
		}

		if(si!=null) {
			if(si.getDenominazione()!=null)
				out += (si.getDenominazione() + " - ");

			if(si.getAddr()!=null)
				out += si.getAddr().toString();

			if(si.getTipologiaSede()!=null)
				out += (" ("+si.getTipologiaSede().getDisplayName()+")");
		}

		return out;
	}

	public boolean isAnyVerifSel(List<Long> selectedVer){
		boolean ret = false;
		if(selectedVer!=null && !selectedVer.isEmpty()){
			ret = true;
		}
		return ret;

	}

	/* Funzione chiamata in fase di salvataggio della verifica per valutare l'obbligatorietà del campo ore/minuti */ 
	private boolean checkOreMinutiMulti(VerificaImp ver) {
		try{
			/* impType: 	(01) APPARECCHI A PRESSIONE 						*
			 * 				(02) IMPIANTI DI RISCALDAMENTO 						*
			 * 				(03) ASCENSORI E MONTACARICHI						*
			 * 				(04) APPARECCHI DI SOLLEVAMENTO						*
			 * 				(05) IMPIANTI ELETTRICI (tipo A, tipo b, tipo c) 	*
			 *  																*
			 * tipoVerifica:(01) PERIODICA										*
			 * 				(02) STRAORDINARIA									*
			 * 				(04) VISITA A VUOTO									*
			 * 				(05) SOPRALLUOGO 									
			 *              (06) STRAORDINARIA PER ACCERTAMENTI*/

			boolean isValid = true;
			String error = "Ore o Minuti Servizio: campo obbligatorio.";

			String tipoVerifica = null;
			if (ver.getTipo()!=null)
				tipoVerifica = ver.getTipo().getCode();

			//Non dovrebbe poter verificarsi essendo il campo a selezione obbligatoria
			if (tipoVerifica==null || "".equals(tipoVerifica))
				return isValid;

			//Controllo se il campo ore/minuti è valorizzato
			boolean oreMinutiValorizzato = false;

			String ore = ver.getHhServizio();
			String min = ver.getMmServizio();

			if ((ore!=null && !"".equals(ore)) || (min!=null && !"".equals(min)))
				oreMinutiValorizzato = true;

			String tipoVerificaStraordinaria = null;
			if (ver.getTipoStr()!=null)
				tipoVerificaStraordinaria = ver.getTipoStr().getCode();

			//APPARECCHI A PRESSIONE 
			if(ver.getImpPressCpy()!=null){

				ImpPress imp = ver.getImpPressCpy();

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true;

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio per qualsiasi tipoApparecchio tranne nel caso di 08 - REC. PRESS. AMBIENTE VITA 
				if ("02 04".contains(tipoVerifica)){
					String tipoApparecchio = null;
					CodeValuePhi tipoApparecchioCv = imp.getTipoApparecchio();

					if (tipoApparecchioCv!=null){
						tipoApparecchio = tipoApparecchioCv.getCode();
						if (tipoApparecchio!=null && "08".equals(tipoApparecchio))//bbb
							return isValid = true;
					}

					if (!oreMinutiValorizzato){
						return isValid = false;	
					}

					//return isValid = true;
				}

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						return isValid = false;	
					}
				}
				//STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						return isValid = false;	
					}
				}

				//IMPIANTI DI RISCALDAMENTO
			} else if(ver.getImpRiscCpy()!=null){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true; 

				//Verifica STRAORDINARIA e VISITA A VUOTO - Presente obbligatorio solo se DESTINAZIONE IMPIANTO = ACQUA CALDA PER FINI PRODUTTIVI
				if ("02 04".contains(tipoVerifica)){

					/* descrizImpianto:
					 * 01 Riscaldamento ambienti
					 * 02 Acqua calda per servizi
					 * 03 Riscaldamento e servizi
					 * 04 Acqua calda per fini produttivi */
					String destinazioneImpianto = null;
					ImpRisc imp = ver.getImpRiscCpy();

					if (imp!=null && imp.getDescrizImpianto()!=null)
						destinazioneImpianto = imp.getDescrizImpianto().getCode();

					if (destinazioneImpianto!=null && "04".equals(destinazioneImpianto)){
						if (!oreMinutiValorizzato){
							//FacesErrorUtils.addErrorMessage("commError", error, error);
							return isValid = false;	
						}
					}

					//return isValid = true;
				}

				//SOPRALLUOGO - Presente obbligatorio
				if ("05".equals(tipoVerifica)){

					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}

					//return isValid = true;	
				}

				//ASCENSORI E MONTACARICHI	
			} else if(ver.getImpMontaCpy()!=null){

				//Verifica PERIODICA - Presente obbligatorio
				if ("01".equals(tipoVerifica)){
					//					if (!oreMinutiValorizzato){
					//						//FacesErrorUtils.addErrorMessage("commError", error, error);
					//						return isValid = false;	
					//					}
					return isValid = true;
				}

				//Verifica STRAORDINARIA
				if ("02".equals(tipoVerifica)){
					return isValid = true; 
				}

				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica))
					return isValid = true; 

				//SOPRALLUOGO - Presente obbligatorio per qualsiasi tipoVerificaSopralluogo
				if ("05".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						return isValid = false;	
					}
				}

				//Verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						return isValid = false;	
					}
				}

				//APPARECCHI DI SOLLEVAMENTO
			} else if(ver.getImpSollCpy()!=null){

				//Verifica PERIODICA - Presente non obbligatorio
				if ("01".equals(tipoVerifica))
					return isValid = true; 

				//Verifica STRAORDINARIA - Presente obbligatorio indipendentemente dal tipoVerificaStraordinaria. 
				//Per gli apparecchi di sollevamento è selezionabile solo la voce 03 (A: STRAORDINARIA PER ACCERTAMENTI)
				if ("02".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;
					}
				}

				//VISITA A VUOTO - Presente obbligatorio
				if ("04".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					} 
				}

				//SOPRALLUOGO - Presente obbligatorio per qualsiasi tipoVerificaSopralluogo
				if ("05".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}
				}

				//Verifica STRAORDINARIA PER ACCERTAMENTI - Presente obbligatorio
				if ("06".equals(tipoVerifica)){
					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					} 
				}

				/* IMPIANTI ELETTRICI (01 - Tipo A, 02 - Tipo B, 03 - Tipo C)  - indipendentemente dal tipo il comportamento sembra essere lo stesso.
				 * C'è una sola eccezione per impianti di Tipo A - in questo caso, se il campo "48 Strutture autoprotette" = YES, nelle verifiche 
				 * periodiche il campo ore/minuti non è obbligatorio. */
			} else if(ver.getImpTerraCpy()!=null){

				ImpTerra imp = ver.getImpTerraCpy();

				String tipoImpiantoElettrico = null;
				CodeValuePhi tie = imp.getSubTypeTerra();

				if (tie!=null)
					tipoImpiantoElettrico = tie.getCode();

				if (tipoImpiantoElettrico==null || "".equals(tipoImpiantoElettrico))
					return isValid = true;

				//Verifica PERIODICA - Presente obbligatorio tranne nel caso di impianti di Tipo A e 48 Strutture autoprotette = YES
				// Presente non obbligatorio per impianti di tipo B SOTTOTIPO 51 SENZA AUTOPRODUZIONE
				if ("01".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){

						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && "48".equals(tipologia))
								return isValid = true; 
						}
					}	
					//Se Tipo B
					if("02".equals(tipoImpiantoElettrico)){
						if(imp.getSubTypeB()!=null){
							String sottotipoB = imp.getSubTypeB().getCode();
							String autoProd = null;
							if(imp.getImpAutoprod()!=null){
								autoProd = imp.getImpAutoprod().getCode();
							}
							if(sottotipoB!=null && "01".equals(sottotipoB) && autoProd!=null && "NO".equals(autoProd)){
								return isValid = true; 
							}
						}
					}

					if (!oreMinutiValorizzato){
						//FacesErrorUtils.addErrorMessage("commError", error, error);
						return isValid = false;	
					}

					return isValid = true;
				}


				//VISITA A VUOTO - Presente non obbligatorio
				if ("04".equals(tipoVerifica))
					return isValid = true;

				//Straordinaria - Presente obbligatorio per tipo A <> tipologia 48
				if ("02".equals(tipoVerifica)){

					//Se Tipo A
					if ("01".equals(tipoImpiantoElettrico)){
						if (imp.getTipologia()!=null){
							String tipologia = imp.getTipologia().getCode();

							if (tipologia!=null && !"48".equals(tipologia) && !oreMinutiValorizzato){
								return isValid = false;	
							}
						}
					}
					return isValid = true;
				}

				//SOPRALLUOGO e STRAORDINARIA PER ACCERTAMENTI- Presente obbligatorio
				if ("05 06".contains(tipoVerifica)){
					if (!oreMinutiValorizzato){
						return isValid = false;	
					}
				}
			}

			return isValid;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void selectVer(VerificaImp ver){
		String key = "selectedVers";
		List<Long> selectedVer = (List<Long>)this.getTemporary().get(key);
		if(selectedVer == null){
			selectedVer = new ArrayList<Long>();
		}

		boolean isSel = (ver.getIsSel()==null?false:ver.getIsSel());
		Long idVer = ver.getInternalId();
		if(isSel && !selectedVer.contains(idVer)){
			selectedVer.add(idVer);
		}else{
			selectedVer.remove(idVer);
		}
		this.getTemporary().put(key, selectedVer);
	}

	public void initSelectedVerifList(){
		String key = "selectedVers";
		List<Long> selectedVer = (List<Long>)this.getTemporary().get(key);
		if(selectedVer == null){
			selectedVer = new ArrayList<Long>();
		}
		this.getTemporary().put(key, selectedVer);

	}

	private String codiceConto1(Sedi sa) {
		try {

			if (sa==null)
				return "3210";

			/* 01 - Condominio
			   02 - Ente del SSN
			   03 - Enti pubblici della regione
			   04 - Enti pubblici fuori regione
			   05 - Privati
			   06 - Altri */
			String tipoUtente = null;

			CodeValuePhi tipoUtenteCv = sa.getTipoUtente();
			if (tipoUtenteCv!=null)
				tipoUtente = tipoUtenteCv.getCode();

			if (tipoUtente==null || "".equals(tipoUtente))
				return "3210";
			else if (tipoUtente != null && "01 05 06".contains(tipoUtente))
				return "3218";
			else if ("02".equals(tipoUtente))
				return "3215";
			else if ("03".equals(tipoUtente))
				return "3216";
			else if ("04".equals(tipoUtente))
				return "3217";

			return "3210";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private String codiceConto2(Sedi sa) {
		try {
			if (sa==null)
				return "3230";

			/* 01 - Condominio
			   02 - Ente del SSN
			   03 - Enti pubblici della regione
			   04 - Enti pubblici fuori regione
			   05 - Privati
			   06 - Altri */
			String tipoUtente = null;

			CodeValuePhi tipoUtenteCv = sa.getTipoUtente();
			if (tipoUtenteCv!=null)
				tipoUtente = tipoUtenteCv.getCode();

			if (tipoUtente==null || "".equals(tipoUtente))
				return "3230";
			else if (tipoUtente!=null && "01 05 06".contains(tipoUtente))
				return "3234";
			else if ("02".equals(tipoUtente))
				return "3231";
			else if ("03".equals(tipoUtente))
				return "3232";
			else if ("04".equals(tipoUtente))
				return "3233";

			return "3230";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}


	public List<VerificaImp> readIncludedPre() throws PhiException{
		removeExpression("this", "pre");

		return this.select();

	}

	private boolean isEsente(VerificaImp v){
		boolean esente = false;

		List<String> oidCodeEsente = new ArrayList<String>();
		oidCodeEsente.add("phidic.arpav.sa.esenzione.04");
		oidCodeEsente.add("phidic.arpav.sa.esenzione.06");
		oidCodeEsente.add("phidic.arpav.sa.esenzione.07");
		oidCodeEsente.add("phidic.arpav.sa.esenzione.08");
		oidCodeEsente.add("phidic.arpav.sa.esenzione.09");
		oidCodeEsente.add("phidic.arpav.sa.esenzione.10");

		Sedi sedeAddebito = getSedeCpy(v);
		CodeValuePhi esenzioneCv = sedeAddebito.getEsenzione();
		CodeValuePhi esenzioneVerif = v.getEsente();
		if((esenzioneCv!=null && oidCodeEsente.contains(esenzioneCv.getOid())) ||
				(esenzioneVerif!=null && "02".equals(esenzioneVerif.getCode()))){
			esente = true;
		}

		return esente;
	}


	private Sedi getSedeCpy(VerificaImp v) {
		try {
			Impianto impCpy = getImpiantoCpy(v);

			if (impCpy!=null)
				return impCpy.getSedi();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Impianto getImpiantoCpy(VerificaImp v) {
		try {
			if (v.getImpPressCpy() != null)
				return v.getImpPressCpy();

			if (v.getImpRiscCpy() != null)
				return v.getImpRiscCpy();

			if (v.getImpMontaCpy() != null)
				return v.getImpMontaCpy();

			if (v.getImpSollCpy() != null)
				return v.getImpSollCpy();

			if (v.getImpTerraCpy() != null)
				return v.getImpTerraCpy();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public List<VerificaImp> getSisters(VerificaImp v){
		if(v==null)
			return null;

		List<VerificaImp> verList = null;
		if (v.getImpPress() != null)
			verList = v.getImpPress().getVerificaImp();

		if (v.getImpRisc() != null)
			verList = v.getImpRisc().getVerificaImp();

		if (v.getImpMonta() != null)
			verList = v.getImpMonta().getVerificaImp();

		if (v.getImpSoll() != null)
			verList = v.getImpSoll().getVerificaImp();

		if (v.getImpTerra() != null)
			verList = v.getImpTerra().getVerificaImp();

		if(verList==null)
			verList = new ArrayList<VerificaImp>();

		return verList;
	}

	public void setAnnoDoc(){
		HashMap<String, Object> tempVer = getTemporary();

		Integer annoWrap = (Integer) tempVer.get("fattura.anno");
		if(annoWrap==null){
			lessEqual.remove("fattura.creationDate");
			greaterEqual.remove("fattura.creationDate");
			return;
		}

		int anno    = annoWrap.intValue();

		Calendar calTo = Calendar.getInstance();

		calTo.set(Calendar.YEAR, anno);
		calTo.set(Calendar.MONTH, 11);
		calTo.set(Calendar.DATE, calTo.getActualMaximum(Calendar.DATE));
		Date to   = calTo.getTime();

		calTo.set(Calendar.MONTH, 0);
		calTo.set(Calendar.DAY_OF_MONTH, 0);
		calTo.set(Calendar.HOUR_OF_DAY, 0);
		calTo.set(Calendar.MINUTE, 0);
		calTo.set(Calendar.SECOND, 0);
		calTo.set(Calendar.MILLISECOND, 0);
		calTo.set(Calendar.DATE, calTo.getActualMinimum(Calendar.DATE));
		Date from = calTo.getTime();

		lessEqual.put("fattura.creationDate", to);
		greaterEqual.put("fattura.creationDate", from);


	}

	public Long countResults(){
		List<Integer> multiresult = entityCriteria.setFirstResult(0).setProjection(Projections.rowCount()).list();
		Long total = 0L;
		if(multiresult!=null){
			for(Integer n : multiresult){
				if(n!=null){
					total += n;
				}
			}
		}

		entityCriteria.setProjection(entityProjections);
		if(resultTranformer!=null){
			entityCriteria.setResultTransformer(resultTranformer);
		}else{
			entityCriteria.setResultTransformer(entityCriteria.ROOT_ENTITY);
		}
		return total;
	}

	@SuppressWarnings("unchecked")
		public void validateMultiRefactored(List<Long> verIds){
			if(verIds==null || verIds.isEmpty()){
				return;
			}
	
			boolean validationOk = true;
			List<Long> selectedVers = (List<Long>)this.getTemporary().get("selectedVers");
	
			CodeValuePhi codeValueStatus = null;
			HashMap<String, CodeValuePhi> causali = (HashMap<String, CodeValuePhi>)this.getTemporary().get("causali");	
			Vocabularies voc = VocabulariesImpl.instance();
			try {
				codeValueStatus = (CodeValuePhi)voc.getCodeValue("PHIDIC", "Stato", "verified", "C");
	
	//			String[] causaleOids = {"04","04R","23","23R","24","24R","25","25R","26","26R","36","36R","86","86R"};
	//			for(String causaleOid: causaleOids){
	//				causaleOid = "phidic.arpav.ver.causale." + causaleOid;
	//				causali.put(causaleOid, (CodeValuePhi)voc.getCodeValueOid(causaleOid));
	//			}
			} catch (DictionaryException e) {
				log.error("Error persisting loading CodeValue ");
			} catch (PhiException e) {
				log.error("Error persisting loading CodeValue ");
			}
	
			for(Long vId : verIds){
				VerificaImp ver = ca.get(VerificaImp.class, vId);

				// Controlli duplicati e rimozioni non riucite nella lista degli addebiti da validare
				if(ver.getStatusCode()!=null && !ver.getStatusCode().getOid().equals("phidic.arpav.ver.stato.new")){
					selectedVers.remove(vId);
					continue;
				}
	
				String qCount = "SELECT COUNT(*) FROM DataBaseLog l";
				org.hibernate.Query queryCount = ca.createHibernateQuery(qCount);
				Long insertedRows = (Long) queryCount.uniqueResult();
				String qWrite = null;
				if(insertedRows==0L){
					qWrite = "INSERT INTO db_log (created_by, is_active, message_txt, object_class, object_identifier) " +
							"VALUES('" + this.getClass().getSimpleName() +
							"', 1, '', '" + ver.getClass().getSimpleName() + 
							"', '" + vId + "')";
				}else{
					qWrite = "UPDATE db_log " + 
							"SET object_class='" + ver.getClass().getSimpleName() +
							"', object_identifier='" + vId + "' WHERE internal_id=1";
	
				}
				try {
					GenericAdapterLocalInterface ga=GenericAdapter.instance();
					ga.executeUpdateNative(qWrite,null);
				} catch (PersistenceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
	//			SimpleDateFormat sdfLog = new SimpleDateFormat("HH:mm:ss.SS");
	//			log.info("Update log time: " + sdfLog.format(new Date()));
	
				boolean importoOk = this.setImporto(ver);
				if(!importoOk){
					if(validationOk){
						validationOk = false;
					}
					continue;
				}
	
	//			log.info("Setting importo time: " + sdfLog.format(new Date()));
	
				this.setCodiceConto(ver);
				//this.getTemporary().put("impTemp", ver.getImporto());
	
	
				String code = null;
				/* code - tipoImpianto: 
				 * 01 - Pressione
				 * 02 - Riscaldamento
				 * 03 - Ascensori e montacarichi
				 * 04 - Apparecchi di sollevamento 
				 * 05 - Impianti elettrici */
				code = this.getImpiantoCode(ver); 			
	
				//			if ("".equals(code))
				//				return;
	
				// Esenzione: la causale è null
				if(this.isEsente(ver)){
					ver.setCausale(null);
				}
	
				String tipoAppPress = null;
				if("01".equals(code)){
					ImpPress imp = ver.getImpPress();
					CodeValuePhi tipoAppCv = imp.getTipoApparecchio();
					if(tipoAppCv!=null){
						tipoAppPress = tipoAppCv.getCode();
					}
				}
	
				String tipoImpRisc = null;
				if("02".equals(code)){
					ImpRisc imp = ver.getImpRisc();
					CodeValuePhi destinazioneCv = imp.getDescrizImpianto();
					if(destinazioneCv!=null){
						tipoImpRisc = destinazioneCv.getCode();
					}
				}
	
				String tipoVerifica = null;
				/* tipoVerifica: 
				 * 01 - Periodica
				 * 02 - Straordinaria
				 * 03 - Constatazione (eliminato)
				 * 04 - Visita a vuoto 
				 * 05 - Sopralluogo 
				 * 06 - Straordinaria per accertamenti
				 * 07 - Straordinaria per modifiche costruttive*/
				CodeValuePhi tipoVerificaCv = ver.getTipo();
	
				if (tipoVerificaCv!=null)
					tipoVerifica = tipoVerificaCv.getCode();
	
				if (tipoVerifica==null || "".equals(code))
					return;
	
				CodeValuePhi causale = null;
				String causaleOid = "";
				//Se tipoVerifica: Visita a vuoto -> Causale: 86 - Verifica a vuoto
				if ("04".equals(tipoVerifica))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.86");
					causaleOid = "86";
	
				//Se tipoVerifica: Sopralluogo -> Causale: 26 - Sopralluoghi e varie
				else if ("05".equals(tipoVerifica))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.26");
					causaleOid = "26";
	
				//Se tipoVerifica: Periodica
				else if ("01".equals(tipoVerifica)){
					//Se tipoImpianto: Pressione -> 36 - Quota ann. e prove period.reg.
					if ("01".equals(code))
						//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.36");
						causaleOid = "36";
					else 
						//Se tipoImpianto <> Pressione -> 23 - Verifica periodica
						//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.23");
						causaleOid = "23";
				}
	
				//Se tipoVerifica: Straordinaria -> Causale 24 VERIFICA STRAORDINARIA
				else if("02".equals(tipoVerifica))
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.24");
					causaleOid = "24";
	
				//Se tipoVerifica: Straordinaria per accertamenti
				else if ("06".equals(tipoVerifica)){
					//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.25");
					causaleOid = "25";
				}
	
				//modifiche costruttive solo ascensori
				else if ("07".equals(tipoVerifica)){
					if ("03".equals(code))
						//causale = (CodeValuePhi)voc.getCodeValueOid("phidic.arpav.ver.causale.04");
						causaleOid = "04";
				}
	
				if(("01".equals(code) && "08".equals(tipoAppPress)) || ("02".equals(code) && "01 02 03".contains(tipoImpRisc)) ||
						"03 05".contains(code)){
					causaleOid += "R";
				}
	
				causale = causali.get(causaleOid);
	
				if (causale!=null)
					ver.setCausale(causale);
				
				if (ver.getSedi()==null){
					if(ver.getImpPress()!=null){
						ver.setSedi(ver.getImpPress().getSedi());
					}else if(ver.getImpRisc()!=null){
						ver.setSedi(ver.getImpRisc().getSedi());
					}else if(ver.getImpMonta()!=null){
						ver.setSedi(ver.getImpMonta().getSedi());
					}else if(ver.getImpSoll()!=null){
						ver.setSedi(ver.getImpSoll().getSedi());
					}else if(ver.getImpTerra()!=null){
						ver.setSedi(ver.getImpTerra().getSedi());
					}
				}
	
				VerificaImp verificaLast = null;
	
				ImpiantoAction ia = ImpiantoAction.instance();
				if(ver.getImpPress()!=null){
					ImpPress imp = ver.getImpPress();
					ImpPressAction.instance().updateImpiantoFromCopy(imp, ver.getImpPressCpy());
	
					//				ImpPress imp = ca.get(ImpPress.class, ver.getImpPress().getInternalId());
					//				ver.setImpPress(imp);
	
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
					ImpRiscAction.instance().updateImpiantoFromCopy(imp, ver.getImpRiscCpy());
	
					//				ImpRisc imp = ca.get(ImpRisc.class, ver.getImpRisc().getInternalId());
					//				ver.setImpRisc(imp);
	
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
					ImpMontaAction.instance().updateImpiantoFromCopy(imp, ver.getImpMontaCpy());
	
					//				ImpMonta imp = ca.get(ImpMonta.class, ver.getImpMonta().getInternalId());
					//				ver.setImpMonta(imp);
	
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
					ImpSollAction.instance().updateImpiantoFromCopy(imp, ver.getImpSollCpy());
	
					//				ImpSoll imp = ca.get(ImpSoll.class, ver.getImpSoll().getInternalId());
					//				ver.setImpSoll(imp);
	
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
					ImpTerraAction.instance().updateImpiantoFromCopy(imp, ver.getImpTerraCpy());
	
					//				ImpTerra imp = ca.get(ImpTerra.class, ver.getImpTerra().getInternalId());
					//				ver.setImpTerra(imp);
	
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
				ver.setIsChecked(null);
				
				UserBean ub = UserBean.instance();
				ver.setStatusCode(codeValueStatus);
				ver.setUtenteUltimaModifica(ub.getCurrentEmployee());
				ver.setDataUltimaModifica(new Date());
				
				try {
					ca.create(ver);
				} catch (PersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	//			log.info("Saving time: " + sdfLog.format(new Date()));
	
				this.updateImpianto(verificaLast, ver);
				selectedVers.remove(vId);
	
			}
		}
	
	public boolean putCausaleListInTmp(){
		String[] causaleOids = {"04","04R","23","23R","24","24R","25","25R","26","26R","36","36R","86","86R"};
		HashMap<String, CodeValuePhi> causali = new HashMap<String, CodeValuePhi>();

		for(String causaleOid: causaleOids){
			Query qGetCv = ca.createHibernateQuery("SELECT cv FROM CodeValuePhi cv WHERE cv.oid = :oid ORDER by cv.version DESC");
			qGetCv.setParameter("oid", "phidic.arpav.ver.causale." + causaleOid);
			List<CodeValuePhi> cvList = (List<CodeValuePhi>) qGetCv.list();
			causali.put(causaleOid, cvList.get(0));
		}
		this.getTemporary().put("causali",causali);

		return true;

	}

	/* Aggiorna gli attributi dell'impianto se la verifica oggetto di manipolazione è l'ultima interna validata 
	 * oppure se è l'ultima verifica esterna.
	 * 
	 * Questa funzione viene chiamata ogni volta che si valida una verifica interna ed ogni volta che si salva 
	 * una verifica esterna (di default in stato validato) 
	 *  
	 * Il parametro di input (verificaLast) viene messo in conversation ogni volta che accede ad una verifica 
	 * dall'anagrafica verifiche ed ogni volta che si accede ad un impianto - vedi VerificaLastAction */
	public void updateImpianto(VerificaImp verificaLast, VerificaImp ver){
		try{
			
			if(ver == null)
				ver = getEntity();
	
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

	public String getImpTypeVerifEsterna(String impType){
		if(impType == null || impType.isEmpty())
			return "";
		
		HashMap<String, String> labelsMap = new HashMap<String, String>();
		labelsMap.put("01", "Apparecchio a pressione");
		labelsMap.put("02", "Impianto di riscaldamento");
		labelsMap.put("03", "Ascensore/montacarichi");
		labelsMap.put("04", "Apparecchio di sollevamento");
		labelsMap.put("05", "Impianto elettrico");
		
		return labelsMap.get(impType);
	}

	public void setSedeArpav(VerificaImp ver) throws PhiException{
		if(ver == null)
			return;
		
		ServiceDeliveryLocationAction sdlAction = ServiceDeliveryLocationAction.instance();
		sdlAction.getEqual().put("code.code", "ARPAV");
		sdlAction.getEqual().put("isActive", true);
		
		List<ServiceDeliveryLocation> sdlList = sdlAction.list();
		
		if(sdlList != null && sdlList.size() == 1){
			ver.setServiceDeliveryLocation(sdlList.get(0));
		}
	}
	
	public String getTipoProva(HashMap<String, Object> ver){
		String ret="";

		if (ver==null)
			return ret;

		
		try {
			//Impianti a pressione
			if (((HashMap<String, Object>) ver.get("impPress"))!=null &&
					((HashMap<String, Object>) ver.get("impPress")).get("internalId")!=null){
				if (ver.get("idraulica")!=null && Boolean.TRUE.equals(ver.get("idraulica")))
					ret+="Integrità ";
				if (ver.get("interna")!=null && Boolean.TRUE.equals(ver.get("interna")))
					ret+="Interna ";
				if (ver.get("esercizio")!=null && Boolean.TRUE.equals(ver.get("esercizio")))
					ret+="Esercizio";

				//Impianti a di riscaldamento
			} else if (((HashMap<String, Object>) ver.get("impRisc"))!=null &&
					((HashMap<String, Object>) ver.get("impRisc")).get("internalId")!=null && 
					((HashMap<String, Object>) ver.get("tipoProva"))!=null){
				ret+=((HashMap<String, Object>) ver.get("tipoProva")).toString(); 
				ret=ret.replace("[", "");
				ret=ret.replace("]", "");
				ret=ret.replace(", ", " "); 
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

		return ret;

	}

}