package com.phi.entities.actions;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("AddebitoAction")
@Scope(ScopeType.CONVERSATION)
public class AddebitoAction extends BaseAction<Addebito, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5987831714647459659L;
	private static final Logger log = Logger.getLogger(AddebitoAction.class); 

	private boolean subProcessReadOnly;


	public static AddebitoAction instance() {
		return (AddebitoAction) Component.getInstance(AddebitoAction.class, ScopeType.CONVERSATION);
	}

	public boolean isSubProcessReadOnly() {
		return subProcessReadOnly;
	}

	public void setSubProcessReadOnly(boolean subProcessReadOnly) {
		this.subProcessReadOnly = subProcessReadOnly;
	}

	public void setSediFullLike(){

		removeExpression("this", "ImpSearchCollector.denominazioneIS");

		HashMap<String, Object> temp = getTemporary();

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		String denominazioneIS = (temp.get("denominazioneIS")!=null)?temp.get("denominazioneIS").toString():"";

		if(findSubCriteria("ImpSearchCollector")==null)
			entityCriteria.createAlias("impSearchCollector", "ImpSearchCollector", Criteria.LEFT_JOIN);

		if (!denominazioneIS.equals("")){
			denominazioneIS = "%" + denominazioneIS + "%";
			entityCriteria.add(Restrictions.like("ImpSearchCollector.denominazioneIS", denominazioneIS).ignoreCase());
		}
	}

	public boolean checkAddebito(){
		Addebito add = getEntity();

		if(add!=null){
			if (add.getServiceDeliveryLocation()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV obbligatoria.", "Sede ARPAV obbligatoria.");
				return false;
			}

			/*
			if (add.getSedeAddebito()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sede Addebito obbligatoria.", "Sede Addebito obbligatoria.");
				return false;
			}*/

			if (add.getSedi()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sede Addebito obbligatoria.", "Sede Addebito obbligatoria.");
				return false;
			}

			if(add.getData()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data obbligatoria.", "Data obbligatoria.");
				return false;
			}

			/* I00086913 - Punto 10 - Obbligatorietà Causale e Causale addizionale */
			if(add.getCausale()==null || add.getCausale().getCode()==null || "".equals(add.getCausale().getCode())) {
				FacesErrorUtils.addErrorMessage("commError", "Campo Causale obbligatorio.", "Campo Causale obbligatorio.");
				return false;
			}

			if((add.getCasualeAdd()==null || add.getCasualeAdd().getCode()==null || "".equals(add.getCasualeAdd().getCode()))) {
				FacesErrorUtils.addErrorMessage("commError", "Campo Causale addizionale obbligatorio.", "Campo Causale addizionale obbligatorio.");
				return false;
			}

			if(add.getImporto()==null || add.getImporto()=="") {
				FacesErrorUtils.addErrorMessage("commError", "Importo obbligatorio.", "Importo obbligatorio.");
				return false;
			}
		}

		return true;

	}

	public String getStato(String statusCode){
		if (statusCode==null || "".equals(statusCode))
			return "";

		if ("new".equals(statusCode))
			return "Inserito";

		if ("verified".equals(statusCode))
			return "Validato";

		if ("completed".equals(statusCode))
			return "Fatturato";

		return "";

	}

	//Sigla-PrimaLetteraSottotipo(se sollevamento o terra)-Matricola-Anno
	public String getCodice(Addebito add){
		String ret = "";
		try{
			if (add==null)
				return ret;

			String sigla = add.getSigla();
			ret+=(sigla==null || "".equals(sigla))?"" : sigla+"-";

			if (add.getImpType()!=null){
				String impType = add.getImpType().getCode();

				if (impType!=null && !"".equals(impType)){
					//Sollevamento
					if ("04".equals(impType)){
						if (add.getSubTypeSoll()!=null){
							String soll = add.getSubTypeSoll().getCurrentTranslation();
							ret+=(soll==null || "".equals(soll))?"" : soll.substring(0,1)+"-";
						}

						//Terra	
					} else if ("05".equals(impType)){
						if (add.getSubTypeTerra()!=null){
							String terra = add.getSubTypeTerra().getCurrentTranslation();
							ret+=(terra==null || "".equals(terra))?"" : terra.substring(0,1)+"-";
						}

					}
				}

			}

			String matr = add.getMatricola();
			ret+=(matr==null || "".equals(matr))?"" : matr+"-";

			String anno = add.getAnno();
			ret+=(anno==null || "".equals(anno))?"" : anno;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

		return ret;

	}

	public void prepareAndDelete(){
		try{
			Addebito add = getEntity();

			if (add==null)
				return;

			add.setImpPress(null);
			add.setImpPressCpy(null);

			add.setImpMonta(null);
			add.setImpMontaCpy(null);

			add.setImpSoll(null);
			add.setImpSollCpy(null);

			add.setImpRisc(null);
			add.setImpRiscCpy(null);

			add.setImpTerra(null);
			add.setImpTerraCpy(null);

			add.setOperatore(null);
			add.setServiceDeliveryLocation(null);

			add.setImpSearchCollector(null);
			add.setPersoneGiuridiche(null);

			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			ca.delete(add);
			ca.flushSession();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	/*public void cleanDates(){
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

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}*/

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

	//Aggiorna il collettore ogni volta che l'addebito viene salvato
	public void updateCollector(Impianto imp) {
		try{
			Addebito add = getEntity();
			ImpSearchCollector coll = add.getImpSearchCollector();

			if(coll!=null && coll.getInternalId()<=0){
				ImpSearchCollectorAction ia = ImpSearchCollectorAction.instance();
				ia.inject(coll);
				ia.create();
				add.setImpSearchCollector(ia.getEntity());
				ia.eject();
			}

			if (add==null || imp==null)
				return;

			if (coll==null){
				coll = new ImpSearchCollector();

			}

			String code="";
			CodeValuePhi codeCv = imp.getCode();
			if  (imp.getCode()!=null){
				coll.setCode(codeCv);
				code = codeCv.getCode();
			}

			coll.setSigla(imp.getSigla());
			coll.setMatricola(imp.getMatricola());
			coll.setAnno(imp.getAnno());

			if (imp.getSedeInstallazione()!=null)
				coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());

			/*
			if (imp.getSedeAddebito()!=null)
				coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());
			 */

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

			ImpSearchCollectorAction ia = ImpSearchCollectorAction.instance();
			ia.inject(coll);
			//ia.create(coll);
			add.setImpSearchCollector(ia.getEntity());
			ia.eject();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setTypes() {
		try {
			((FilterMap)getEqual()).remove("impType.id");
			List<String> types = new ArrayList<String>();

			HashMap<String, Object> temp = ImpiantoAction.instance().getTemporary();
			if (!temp.isEmpty()) {
				Object all = temp.get("allTypes");
				Boolean allTypes = (all!=null && (Boolean)all);

				if (allTypes)
					return;

				//Apparecchi a pressione
				Object press = temp.get("01");
				if (press!=null && (Boolean)press)
					types.add("phidic.arpav.imp.imptype.01_V0");

				//Impianti di riscaldamento
				Object risc = temp.get("02");
				if (risc!=null && (Boolean)risc)
					types.add("phidic.arpav.imp.imptype.02_V0");

				//Ascensori e montacarichi
				Object asc = temp.get("03");
				if (asc!=null && (Boolean)asc)
					types.add("phidic.arpav.imp.imptype.03_V0");

				//Apparecchi di sollevamento
				Object soll = temp.get("04");
				if (soll!=null && (Boolean)soll)
					types.add("phidic.arpav.imp.imptype.04_V0");

				//Impianti elettrici - ex messa a terra
				Object terr = temp.get("05");
				if (terr!=null && (Boolean)terr)
					types.add("phidic.arpav.imp.imptype.05_V0");				
			}

			if (types.size()>0)
				((FilterMap)getEqual()).putOr("impType.id", types.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}


	public void setAnnoMese() {
		try {

			//removeExpression("this", "data");

			HashMap<String, Object> temp = this.getTemporary();
			if (temp == null)
				return;

			String anno = getTemporary().get("annoVerifica")!=null?getTemporary().get("annoVerifica").toString():"";
			String mese = getTemporary().get("meseVerifica")!=null?getTemporary().get("meseVerifica").toString():"";

			Integer annoVerifica = (anno!=null && !anno.isEmpty())?Integer.parseInt(anno):null;
			Integer meseVerifica = (mese!=null && !mese.isEmpty())?Integer.parseInt(mese)-1:null;

			if (annoVerifica==null && meseVerifica==null)
				return;

			Date verificaFrom;
			Date verificaTo;
			Calendar cal = Calendar.getInstance();

			if (meseVerifica!=null && meseVerifica>0 && (annoVerifica == null || annoVerifica==0)){
				cal.setTime(new Date());
				annoVerifica = cal.get(Calendar.YEAR);
			}

			if (meseVerifica==null){
				//Primo giorno dell'anno
				cal.set(Calendar.YEAR, annoVerifica);
				cal.set(Calendar.DAY_OF_YEAR, 1);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR, 0);

				verificaFrom = cal.getTime();

				//Ultimo giorno dell'anno
				cal.set(Calendar.MONTH, 11); //11 = dicembre
				cal.set(Calendar.DAY_OF_MONTH, 31);
				cal.set(Calendar.MILLISECOND, 999);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.HOUR, 23);

				verificaTo = cal.getTime();

			} else {
				//Primo giorno del mese
				cal.set(Calendar.YEAR, annoVerifica);
				cal.set(Calendar.MONDAY, meseVerifica);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR, 0);

				verificaFrom = cal.getTime();

				//Ultimo giorno del mese
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				cal.set(Calendar.MILLISECOND, 999);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.HOUR, 23);

				verificaTo = cal.getTime();			
			}

			entityCriteria.add(Restrictions.ge("data", verificaFrom));
			entityCriteria.add(Restrictions.le("data", verificaTo));

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* public void setSubTypes() {
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
	}*/

	public void updateImporto(){

		try{
			Addebito add = getEntity();
			if (add==null)
				return;

			CodeValuePhi causaleCv = add.getCausale();
			if (causaleCv==null || causaleCv.getAbbreviation()==null || "".equals(causaleCv.getAbbreviation()))
				return;

			String causale = causaleCv.getAbbreviation();
			if (causale.endsWith(".00"))
				causale = causale.replace(".00", "");

			double tariffa = Double.parseDouble(causale);

			if (tariffa<=0.0)
				return;

			String q = add.getQuantita();
			String h = add.getHhServizio();
			String m = add.getMmServizio();

			if ((q==null || "".equals(q)) && (h==null || "".equals(h)) && (m==null || "".equals(m)))
				return;

			double quantita = 0.0; 
			if (q!=null && !"".equals(q))
				quantita = Double.parseDouble(q.replace(",", "."));

			if (quantita>0){
				DecimalFormat df = new DecimalFormat(".##");
				double importo = tariffa*quantita;
				getEntity().setImporto(df.format(importo).replace(",", "."));
				return;
			}

			double hh = 0.0; 
			if (h!=null && !"".equals(h))
				hh = Double.parseDouble(h);

			double mm = 0.0; 
			if (m!=null && !"".equals(m))
				mm = Double.parseDouble(m);

			if (hh>0.0 || mm>0.0){
				DecimalFormat df = new DecimalFormat(".##");
				double importo = (tariffa/60)*(hh*60+mm);
				getEntity().setImporto(df.format(importo).replace(",", "."));
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Integer getAnno(Date data) {
		try {
			if (data==null)
				return null;

			Calendar myCal = new GregorianCalendar();
			myCal.setTime(data);

			Integer anno = myCal.get(Calendar.YEAR);
			//getEntity().setAnnoRif(anno.toString());

			return anno;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Integer getMese(Date data) {
		try {			
			if (data==null)
				return null;

			Calendar myCal = new GregorianCalendar();
			myCal.setTime(data);

			Integer mese = myCal.get(Calendar.MONTH)+1;
			//getEntity().setMeseRif(mese.toString());

			return mese;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setCodiceConto() {
		try {
			//SediInstallazione si = null;
			Sedi sa = null;
			Addebito a = getEntity();

			if (a.getImpMontaCpy()!=null)
				sa = a.getImpMontaCpy().getSedi();
			else if (a.getImpPressCpy()!=null)
				sa = a.getImpPressCpy().getSedi();
			else if (a.getImpRiscCpy()!=null)
				sa = a.getImpRiscCpy().getSedi();
			else if (a.getImpSollCpy()!=null)
				sa = a.getImpSollCpy().getSedi();
			else if (a.getImpTerraCpy()!=null)
				sa = a.getImpTerraCpy().getSedi();	

			//Aliquota IVA
			String aliquota = "";
			CodeValuePhi aliquotaCv = a.getAliquota();
			if(aliquotaCv!=null){
				aliquota = aliquotaCv.getCode();
			}
			
			// Sede addebito esente IVA
			CodeValuePhi esenzioneCv = null;
			if(sa != null){
				esenzioneCv = sa.getEsenzione();
			}

			boolean isFuoriIva = "02".equals(aliquota) || "03".equals(aliquota) ||
					(esenzioneCv != null && "phidic.arpav.sa.esenzione.05".equals(esenzioneCv.getOid()));

			if (sa==null){
				if(isFuoriIva){
					a.setCodiceConto("3210");
				}else{
					a.setCodiceConto("3230");
				}
				return;
			}

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

			if (tipoUtente==null || "".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3210");
				}else{
					a.setCodiceConto("3230");
				}
				return;
			}
			
			if (tipoUtente!=null && "01 05 06".contains(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3218");
				}else{
					a.setCodiceConto("3234");
				}
				return;
			}

			if ("02".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3215");
				}else{
					a.setCodiceConto("3231");
				}
				return;
			}

			if ("03".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3216");
				}else{
					a.setCodiceConto("3232");
				}
				return;
			}

			if ("04".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3217");
				}else{
					a.setCodiceConto("3233");
				}
				return;
			}

			if(isFuoriIva){
				a.setCodiceConto("3210");
			}else{
				a.setCodiceConto("3230");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void fillOtherFieldsFromSedeInst(Addebito add){
		try {
			if(add==null)
				return;

			Impianto imp = ImpCopyAction.instance().getEntity();
			PersoneGiuridiche pg = add.getPersoneGiuridiche();

			if(imp == null || pg==null)
				return;

			List<Sedi> sediList = pg.getSedi();

			if(sediList == null)
				return;

			Sedi sedePrincipaleAdd = null;
			List<Sedi> sediAddebito = new ArrayList<Sedi>();

			//Itero la lista di sedi
			for (Sedi s:sediList){
				if(s.getIsActive()){					
					//Cerco tutte le sedi di addebito
					if (Boolean.TRUE.equals(s.getSedeAddebito())){
						//Cerco e memorizzo la sede principale se è anche sede di addebito
						if (s.getSedePrincipale())
							sedePrincipaleAdd = s;

						sediAddebito.add(s);
					}
				}
			}

			Sedi sa = null;
			IndirizzoSped is = null;

			//Se ho trovato la sede principale, la salvo nella variabile "sa"
			if (sedePrincipaleAdd!=null)
				sa = sedePrincipaleAdd;

			//Altrimenti, se ho trovato solo una sede di addebito, la salvo nella variabile "sede"
			else if (sediAddebito.size()==1)
				sa = sediAddebito.get(0);

			//Se ho trovato la sede principale OPPURE una sola sede di addebito
			if (sa!=null && sa.getIndirizzoSped()!=null) {
				IndirizzoSpedAction.instance().injectList(sa.getIndirizzoSped());

				//Se la sede che sto usando ha un solo indirizzo di spedizione 
				if(sa.getIndirizzoSped().size() == 1) {
					if(sa.getIndirizzoSped().get(0).getIsActive()) {
						//lo associo alla cessione
						is=sa.getIndirizzoSped().get(0);
					}
				} else {
					//cerco e associo (se esiste) l'indirizzo di spedizione principale
					for (IndirizzoSped i:sa.getIndirizzoSped()) {
						if(i.getIsActive() && sa.getIndirizzoSpedPrinc()!=null && sa.getIndirizzoSpedPrinc().getInternalId() == i.getInternalId()) {
							is=i;
						}
					}
				}
			}

			ImpCopyAction.instance().manageCopy(sa, is);
			add.setSedi(sa);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean isAnyAddSel(List<Long> selectedAdds){
		boolean ret = false;

		if(selectedAdds!=null && !selectedAdds.isEmpty()){
			ret = true;
		}
		return ret;

	}

	public void selectAdd(Addebito add){
		String key = "selectedAdds";
		List<Long> selectedAdds = (List<Long>)this.getTemporary().get(key);
		if(selectedAdds == null){
			selectedAdds = new ArrayList<Long>();
		}

		boolean isNew = (add.getIsNew()==null?false:add.getIsNew());
		Long idAdd = add.getInternalId();
		if(isNew && !selectedAdds.contains(idAdd)){
			selectedAdds.add(idAdd);
		}else{
			selectedAdds.remove(idAdd);
		}
		this.getTemporary().put(key, selectedAdds);
	}

	public void selectAdd(List<Addebito> addebiti){
		String key = "selectedAdds";
		List<Long> selectedAdds = (List<Long>)this.getTemporary().get(key);
		//		if(selectedAdds == null){
		//			selectedAdds = new ArrayList<Long>();
		//		}
		for(Addebito add : addebiti){
			boolean isNew = (add.getIsNew()==null?false:add.getIsNew());
			Long idAdd = add.getInternalId();
			if(isNew && !selectedAdds.contains(idAdd)){
				selectedAdds.add(idAdd);
			}else{
				selectedAdds.remove(idAdd);
			}	
		}

		this.getTemporary().put(key, selectedAdds);
	}

	public void initSelectedAddList(){
		String key = "selectedAdds";
		List<Long> selectedAdds = (List<Long>)this.getTemporary().get(key);
		if(selectedAdds == null){
			selectedAdds = new ArrayList<Long>();
		}
		this.getTemporary().put(key, selectedAdds);

	}	
	public boolean checkAddebitoMulti(List<Long> addebiti){
		boolean isValidMulti = true;

		if(addebiti==null || addebiti.isEmpty()){
			return isValidMulti;
		}

		List<Long> addebitiOk = new ArrayList<Long>();
		Iterator<Long> selectedAddsIterator = addebiti.iterator();
		while(selectedAddsIterator.hasNext()){
			Long addId = selectedAddsIterator.next();
			Addebito add = ca.get(Addebito.class, addId);

			// Controlli duplicati e rimozioni non riucite nella lista degli addebiti da validare
			if(add.getStatusCode()!=null && !add.getStatusCode().getOid().equals("phidic.arpav.ver.stato.new")){
				selectedAddsIterator.remove();
				continue;
			}

			add.setIsChecked(true);

			if (add.getServiceDeliveryLocation()==null){
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			if (add.getSedi()==null){
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			if(add.getData()==null) {
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			if(add.getImporto()==null || add.getImporto()=="") {
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			/* I00086913 - Punto 10 - Obbligatorietà Causale e Causale addizionale */
			if(add.getCausale()==null || add.getCausale().getCode()==null || "".equals(add.getCausale().getCode())) {
				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			if((add.getCasualeAdd()==null || add.getCasualeAdd().getCode()==null || "".equals(add.getCasualeAdd().getCode()))) {

				if(isValidMulti){
					isValidMulti = false;
				}
				continue;
			}

			addebitiOk.add(addId);

		}

		this.getTemporary().put("selectedAdds", addebiti);
		this.getTemporary().put("addebitiOk", addebitiOk);
		return isValidMulti;

	}

	public String getErrMsgFromTmp(List<Long> unvalidated){
		if(unvalidated==null)
			return "";

		String errorMsg = "ADDEBITI NON VALIDATI<br/>";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for(Long addId : unvalidated){
			Addebito add = ca.get(Addebito.class, addId);
			errorMsg += " DATA INSERIMENTO: "+ sdf.format(add.getCreationDate()) + 
					(add.getData()!=null ? " DATA: " + sdf.format(add.getData()) : "") + 
					(this.getCodice(add).isEmpty() ? "" : " CODICE: " + this.getCodice(add)) + "<br/>";
		}
		return errorMsg;
	}

	@SuppressWarnings("unchecked")
	public void validateMulti(List<Long> addIds){
		if(addIds==null || addIds.isEmpty()){
			return;
		}
		
		boolean validationOk = true;

		List<Long> selectedAdds = (List<Long>)this.getTemporary().get("selectedAdds");
		
		CodeValuePhi codeValueStatus = null;
		Vocabularies voc = VocabulariesImpl.instance();
		try {
			codeValueStatus = (CodeValuePhi)voc.getCodeValue("PHIDIC", "Stato", "verified", "C");
		} catch (DictionaryException e) {
			log.error("Error persisting loading CodeValue ");
		} catch (PhiException e) {
			log.error("Error persisting loading CodeValue ");
		}

		for(Long aId : addIds){
			Addebito add = ca.get(Addebito.class, aId);

			// Controlli duplicati e rimozioni non riucite nella lista degli addebiti da validare
			if(add.getStatusCode()!=null && !add.getStatusCode().getOid().equals("phidic.arpav.ver.stato.new")){
				selectedAdds.remove(aId);
				continue;
			}

			this.setCodiceConto(add);
			add.setIsChecked(null);

			UserBean ub = UserBean.instance();
			add.setUtenteUltimaModifica(ub.getCurrentEmployee());
			add.setDataUltimaModifica(new Date());

			try {
				add.setStatusCode(codeValueStatus);
				ca.create(add);
			} catch (PhiException e) {
				log.error("Error persisting Addebito #"+add.getInternalId(), e);
			}

			selectedAdds.remove(aId);
		}
	}

	public void setCodiceConto(Addebito a) {
		try {
			//SediInstallazione si = null;
			Sedi sa = null;
			
			if (a.getImpMontaCpy()!=null)
				sa = a.getImpMontaCpy().getSedi();
			else if (a.getImpPressCpy()!=null)
				sa = a.getImpPressCpy().getSedi();
			else if (a.getImpRiscCpy()!=null)
				sa = a.getImpRiscCpy().getSedi();
			else if (a.getImpSollCpy()!=null)
				sa = a.getImpSollCpy().getSedi();
			else if (a.getImpTerraCpy()!=null)
				sa = a.getImpTerraCpy().getSedi();	
			
			//Aliquota IVA
			String aliquota = "";
			CodeValuePhi aliquotaCv = a.getAliquota();
			if(aliquotaCv!=null){
				aliquota = aliquotaCv.getCode();
			}

			boolean isFuoriIva = "02".equals(aliquota) || "03".equals(aliquota);

			if (sa==null){
				if(isFuoriIva){
					a.setCodiceConto("3210");
				}else{
					a.setCodiceConto("3230");
				}
				return;
			}

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

			if (tipoUtente==null || "".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3210");
				}else{
					a.setCodiceConto("3230");
				}
				return;
			}
			
			if (tipoUtente!=null && "01 05 06".contains(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3218");
				}else{
					a.setCodiceConto("3234");
				}
				return;
			}

			if ("02".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3215");
				}else{
					a.setCodiceConto("3231");
				}
				return;
			}

			if ("03".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3216");
				}else{
					a.setCodiceConto("3232");
				}
				return;
			}

			if ("04".equals(tipoUtente)){
				if(isFuoriIva){
					a.setCodiceConto("3217");
				}else{
					a.setCodiceConto("3233");
				}
				return;
			}

			if(isFuoriIva){
				a.setCodiceConto("3210");
			}else{
				a.setCodiceConto("3230");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
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
}