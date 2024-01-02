package com.phi.entities.actions;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.ControlLsReq;
import com.phi.entities.baseEntity.Fattura;
import com.phi.entities.baseEntity.FileAccCond;
import com.phi.entities.baseEntity.FileAccDitte;
import com.phi.entities.baseEntity.FileAccEnti;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.actions.BaseAction;
import com.phi.security.SpisalUserAction;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("FatturaAction")
@Scope(ScopeType.CONVERSATION)
public class FatturaAction extends BaseAction<Fattura, Long> {

	private static final long serialVersionUID = -3458786017795344834L;
	private static final Logger log = Logger.getLogger(FatturaAction.class); 


	public static FatturaAction instance() {
		return (FatturaAction) Component.getInstance(FatturaAction.class, ScopeType.CONVERSATION);
	}

	public boolean deleteLast() {
		try {

			SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");

			List<SelectItem> arpavList = sua.getArpav();
			if (arpavList==null || arpavList.size()!=1){
				FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV non nota.", "Sede ARPAV non nota.");
				return false;
			}

			String idStr = arpavList.get(0).getValue().toString();

			if (idStr==null || "".equals(idStr)){
				FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV non nota.", "Sede ARPAV non nota.");
				return false;
			}

			Long id = Long.parseLong(idStr);

			if (id==null || id<=0){
				FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV non nota.", "Sede ARPAV non nota.");
				return false;
			}

			String evaluateGruppo = "SELECT fattura FROM Fattura fattura " + 
					"LEFT JOIN fattura.serviceDeliveryLocation arpav " +
					"WHERE fattura.isActive = true AND arpav.internalId = :arpavId order by fattura.creationDate desc";

			HashMap<String, Object> parameters= new HashMap<String, Object>(1);
			parameters.put("arpavId", id);

			@SuppressWarnings("unchecked")
			List<Fattura> fatture = (List<Fattura>) ca.executeHQLwithParameters(evaluateGruppo, parameters);

			//Non sono presenti fatture
			if(fatture==null || fatture.isEmpty()){
				FacesErrorUtils.addErrorMessage("commError", "Impossibile individuare l'ultima fattura per la sede ARPAV specificata.", "Impossibile individuare l'ultima fattura per la sede ARPAV specificata.");
				return false;
			}
			else {
				Fattura fattura = fatture.get(0);				

				if (fattura.getVerificaImp()!=null && fattura.getVerificaImp().size()>0)
					this.resetVerifiche(fattura.getVerificaImp());
				else if (fattura.getAddebito()!=null && fattura.getAddebito().size()>0)
					this.resetAddebiti(fattura.getAddebito());

				fattura.setIsActive(false);
				this.inject(fattura);
			}

			return true;

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

	public void addArchiviate() {
		try {	
			getEqual().remove("archiviata");
			Boolean addArc = false;

			List<Boolean> statoArc = new ArrayList<Boolean>();
			statoArc.add(false);
			statoArc.add(null);

			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				addArc = temp.get("archiviate")!=null?(Boolean)temp.get("archiviate"):false;

				if (addArc)
					statoArc.add(true);
			}

			((FilterMap)getEqual()).putOr("archiviata", statoArc.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean checkArpav() {
		try {
			Fattura f = this.getEntity();
			if (f.getServiceDeliveryLocation()==null){
				FacesErrorUtils.addErrorMessage("commError", "Sede ARPAV obbligatoria.", "Sede ARPAV obbligatoria.");
				return false;
			}

			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void checkDati() {
		try {
			VerificaImpAction va = VerificaImpAction.instance();
			va.ejectList();

			AddebitoAction ad = AddebitoAction.instance();
			ad.ejectList();

			getTemporary().remove("check");
			Fattura f = this.getEntity();

			/* 01 - Apparecchi a pressione
			 * 02 - Impianti di riscaldamento
			 * 03 - Ascensori e montacarichi
			 * 04 - Apparecchi di sollevamento
			 * 05 - Impianti elettrici
			 * 06 - Fatturazione unificata (solo tecnici interni)
			 * 07 - Fatturazione unificata (solo tecnici esterni)
			 * 08 - Fatturazione unificata (completa)
			 * 09 - Addebiti vari */
			CodeValuePhi tipologiaFatturaCv = f.getTipologiaFattura();
			String  tipologiaFattura = null;
			if (tipologiaFatturaCv != null)
				tipologiaFattura = tipologiaFatturaCv.getCode();

			if (tipologiaFattura!=null) {
				//Fatturazione - Solo verifiche
				if (!"00".equals(tipologiaFattura))
					this.checkDatiVerifiche(tipologiaFattura);

				//Fatturazione - Solo addebiti
				if ("00".equals(tipologiaFattura))
					this.checkDatiAddebiti(tipologiaFattura);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private void checkDatiVerifiche(String tipologiaFattura) {
		try {
			VerificaImpAction va = VerificaImpAction.instance();

			Fattura f = this.getEntity();

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();
			Operatore operatore = f.getOperatore();
			//Ditta proprietaria legata alla sede di installazione
			PersoneGiuridiche pgInst = f.getPersonaGiuridica();
			//Ditta legata alla sede di addebito, anche esterna
			PersoneGiuridiche pgAdd = f.getPersonaGiuridicaAdd();

			int anno    = f.getAnno();
			int meseAl  = f.getMeseAl();
			int meseDal = f.getMeseDal();

			Calendar calTo = Calendar.getInstance();

			calTo.set(Calendar.YEAR, anno);
			calTo.set(Calendar.MONTH, meseAl-1);
			calTo.set(Calendar.DATE, calTo.getActualMaximum(Calendar.DATE));
			Date to   = calTo.getTime();

			calTo.set(Calendar.MONTH, meseDal-1);
			calTo.set(Calendar.DAY_OF_MONTH, 0);
			calTo.set(Calendar.HOUR_OF_DAY, 0);
			calTo.set(Calendar.MINUTE, 0);
			calTo.set(Calendar.SECOND, 0);
			calTo.set(Calendar.MILLISECOND, 0);
			calTo.set(Calendar.DATE, calTo.getActualMinimum(Calendar.DATE));
			Date from = calTo.getTime();

			/* I0070276 sede addebito sostituita da sede con flag addebito
			String qry = "SELECT DISTINCT v FROM VerificaImp v " + 
					 	 "LEFT JOIN v.operatore op " +
					 	 "LEFT JOIN v.sedeAddebito sa " +
					 	 "LEFT JOIN sa.personaGiuridica pg " +
					 	 "LEFT JOIN v.serviceDeliveryLocation arpav ";
			 */
			String qry = "SELECT DISTINCT v FROM VerificaImp v " + 
					"LEFT JOIN v.operatore op ";	    

			if(pgAdd != null){
				qry += "LEFT JOIN v.sedi se ";
				qry += "LEFT JOIN se.personaGiuridica pg ";
			}

			if(pgInst != null){
				qry += "LEFT JOIN v.sediInstallazione si ";
				qry += "LEFT JOIN si.sede se ";
				qry += "LEFT JOIN se.personaGiuridica pg ";
			}

			qry += "LEFT JOIN v.serviceDeliveryLocation arpav ";

			/* 01 - Apparecchi a pressione
			 * 02 - Impianti di riscaldamento
			 * 03 - Ascensori e montacarichi
			 * 04 - Apparecchi di sollevamento
			 * 05 - Impianti elettrici
			 * 06 - Fatturazione unificata (solo tecnici interni)
			 * 07 - Fatturazione unificata (solo tecnici esterni)
			 * 08 - Fatturazione unificata (completa)
			 * 09 - Addebiti vari */
			if ("01".equals(tipologiaFattura))
				qry += "LEFT JOIN v.impPress ip ";
			else if ("02".equals(tipologiaFattura))
				qry += "LEFT JOIN v.impRisc ir ";
			else if ("03".equals(tipologiaFattura))
				qry += "LEFT JOIN v.impMonta im ";
			else if ("04".equals(tipologiaFattura))
				qry += "LEFT JOIN v.impSoll iso ";
			else if ("05".equals(tipologiaFattura))
				qry += "LEFT JOIN v.impTerra it ";

			//Solo verifiche attiva, in stato verificato escludendo le verifiche esterne
			qry += "WHERE v.isActive = true AND v.statusCode.code != 'completed' AND (v.tipoInOut IS NULL OR v.tipoInOut.code != '02') ";

			//06 - Fatturazione unificata (solo tecnici interni)
			if ("06".equals(tipologiaFattura))
				qry += "AND op IS NOT NULL AND (v.tecnicoOut IS NULL OR v.tecnicoOut = false) ";

			//07 - Fatturazione unificata (solo tecnici esterni)
			else if ("07".equals(tipologiaFattura))
				qry += "AND op IS NOT NULL AND v.tecnicoOut = true ";

			//08 - Fatturazione unificata (completa) --> non è necessario aggiungere condizioni

			if (arpav!=null)
				qry += "AND arpav.internalId = :arpavId ";

			if (pgAdd!=null)
				qry += "AND pg.internalId = :pgAddId ";

			if (pgInst!=null)
				qry += "AND pg.internalId = :pgInstId ";

			if (operatore!=null)
				qry += "AND op.internalId = :opId ";

			if(to != null || from != null)
				qry += "AND v.data IS NOT NULL ";
			if(from != null)
				qry += "AND v.data >= :from ";
			if(to != null)
				qry += "AND v.data <= :to ";

			if (tipologiaFattura!=null) {
				if ("01".equals(tipologiaFattura))
					qry += "AND ip IS NOT NULL ";
				else if ("02".equals(tipologiaFattura))
					qry += "AND ir IS NOT NULL ";
				else if ("03".equals(tipologiaFattura))
					qry += "AND im IS NOT NULL ";
				else if ("04".equals(tipologiaFattura))
					qry += "AND iso IS NOT NULL ";
				else if ("05".equals(tipologiaFattura))
					qry += "AND it IS NOT NULL ";
			}

			qry += "order by v.data desc";

			javax.persistence.Query verifiche = ca.createQuery(qry);

			if (qry.contains(":opId"))
				verifiche.setParameter("opId", operatore.getInternalId());

			if (qry.contains(":pgAddId"))
				verifiche.setParameter("pgAddId", pgAdd.getInternalId());

			if (qry.contains(":pgInstId"))
				verifiche.setParameter("pgInstId", pgInst.getInternalId());

			if (qry.contains(":arpavId"))
				verifiche.setParameter("arpavId", arpav.getInternalId());

			if (qry.contains(":from"))
				verifiche.setParameter("from", from);

			if (qry.contains(":to"))
				verifiche.setParameter("to", to);

			@SuppressWarnings("unchecked")
			List<VerificaImp> verificheList = verifiche.getResultList();

			//Non sono presenti verifiche
			if(verificheList==null || verificheList.isEmpty()){
				getTemporary().put("check", false);
				return;
			}

			boolean _verified = false;
			boolean _new = false;

			for (VerificaImp v:verificheList){

				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					//C'è almeno una verifica Validata
					if ("verified".equals(status.getCode()))
						_verified = true;

					else //C'è almeno una verifica non validata (inserita)
						if ("new".equals(status.getCode()))
							_new = true;
				}
			}

			//Tutte le verifiche sono validate
			if (!_new && _verified){
				va.injectList(verificheList);
				getTemporary().put("check", true);
				return;	
			}

			//Sono presenti verifiche non validate (inserite)
			if (_new && _verified){
				va.injectList(verificheList);
				getTemporary().put("check", false);
				return;	
			} 

			//Sono presenti solo verifiche non validate (inserite)
			if (_new && !_verified)
				getTemporary().put("check", false);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private void checkDatiAddebiti(String tipologiaFattura) {
		try {
			AddebitoAction ad = AddebitoAction.instance();
			Fattura f = this.getEntity();

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();
			Operatore operatore = f.getOperatore();
			PersoneGiuridiche pgInst = f.getPersonaGiuridica();
			//Ditta legata alla sede di installazione quando presente
			PersoneGiuridiche pgAdd = f.getPersonaGiuridicaAdd();

			int anno    = f.getAnno();
			int meseAl  = f.getMeseAl();
			int meseDal = f.getMeseDal();

			Calendar calTo = Calendar.getInstance();

			calTo.set(Calendar.YEAR, anno);
			calTo.set(Calendar.MONTH, meseAl-1);
			calTo.set(Calendar.DATE, calTo.getActualMaximum(Calendar.DATE));
			Date to   = calTo.getTime();

			calTo.set(Calendar.MONTH, meseDal-1);
			calTo.set(Calendar.DAY_OF_MONTH, 0);
			calTo.set(Calendar.HOUR_OF_DAY, 0);
			calTo.set(Calendar.MINUTE, 0);
			calTo.set(Calendar.SECOND, 0);
			calTo.set(Calendar.MILLISECOND, 0);
			calTo.set(Calendar.DATE, calTo.getActualMinimum(Calendar.DATE));
			Date from = calTo.getTime();

			String qry = "SELECT DISTINCT a FROM Addebito a " + 
					"LEFT JOIN a.operatore op ";

			if(pgInst != null){
				qry += "LEFT JOIN a.personeGiuridiche pgInst ";
			}

			if(pgAdd != null){
				qry += "LEFT JOIN a.sedi se ";
				qry += "LEFT JOIN se.personaGiuridica pgAdd ";
			}

			qry += "LEFT JOIN a.serviceDeliveryLocation arpav ";

			qry += "WHERE a.isActive = true AND a.statusCode.code != 'completed' ";

			if (arpav!=null)
				qry += "AND arpav.internalId = :arpavId ";

			if (operatore!=null)
				qry += "AND op.internalId = :opId ";

			if (pgAdd!=null)
				qry += "AND pgAdd.internalId = :pgAddId ";

			if (pgInst!=null)
				qry += "AND pgInst.internalId = :pgInstId ";

			if(to != null || from != null)
				qry += "AND a.data IS NOT NULL ";
			if(from != null)
				qry += "AND a.data >= :from ";
			if(to != null)
				qry += "AND a.data <= :to ";

			qry += "order by a.data desc";

			javax.persistence.Query addebiti = ca.createQuery(qry);

			if (qry.contains(":opId"))
				addebiti.setParameter("opId", operatore.getInternalId());

			if (qry.contains(":pgAddId"))
				addebiti.setParameter("pgAddId", pgAdd.getInternalId());

			if (qry.contains(":pgInstId"))
				addebiti.setParameter("pgInstId", pgInst.getInternalId());

			if (qry.contains(":arpavId"))
				addebiti.setParameter("arpavId", arpav.getInternalId());

			if (qry.contains(":from"))
				addebiti.setParameter("from", from);

			if (qry.contains(":to"))
				addebiti.setParameter("to", to);

			@SuppressWarnings("unchecked")
			List<Addebito> addebitiList = addebiti.getResultList();

			//Non sono presenti verifiche
			if(addebitiList==null || addebitiList.isEmpty()){
				getTemporary().put("check", false);
				return;
			}

			boolean _verified = false;
			boolean _new = false;

			for (Addebito a:addebitiList){

				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					//C'è almeno un addebito Validato
					if ("verified".equals(status.getCode()))
						_verified = true;

					else //C'è almeno un addebito non validato (inserito)
						if ("new".equals(status.getCode()))
							_new = true;
				}
			}

			//Tutti gli addebiti sono validati
			if (!_new && _verified){
				ad.injectList(addebitiList);
				getTemporary().put("check", true);
				return;	
			}

			//Sono presenti addebiti non validati (inseriti)
			if (_new && _verified){
				ad.injectList(addebitiList);
				getTemporary().put("check", false);
				return;	
			} 

			//Sono presenti solo addebiti non validati (inseriti)
			if (_new && !_verified)
				getTemporary().put("check", false);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void estraiDati(List<VerificaImp> verificheList, List<Addebito> addebitiList) {
		try {
			if (verificheList!=null && verificheList.size()>0)
				this.estraiDatiVerifiche(verificheList);

			else if (addebitiList!=null && addebitiList.size()>0)
				this.estraiDatiAddebiti(addebitiList);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	} 

	private void estraiDatiVerifiche(List<VerificaImp> verificheList) {
		try {

			Fattura f = this.getEntity();
			Vocabularies vocabularies = VocabulariesImpl.instance();

			/* CALCOLA GRUPPO */		
			Integer gruppo = this.calcolaGruppo(f);
			if (gruppo==null){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			} else {
				f.setGruppo(gruppo);
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "completed"));
			}

			/* GESTIONE VERIFICHE */
			boolean mngVer = false;

			if (verificheList==null || verificheList.size()<1){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			} else
				mngVer = this.manageVerifiche(f, verificheList);

			if (!mngVer){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			}

			/* GESTIONE FILE */

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			f.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + ".txt");
			f.setContentType("text/plain");

			String content = getFileContentVer(f, verificheList);

			if (content==null || "".equals(content)){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));

				/* Riporta le verifiche completed in stato verified, le slega dalla fattura e annulla gli attributi NumeroDoc e Fattura */
				this.resetVerifiche(verificheList);	
				FacesErrorUtils.addErrorMessage("commError", "File content: null.", "File content: null.");

				return;
			}

			f.setContent(content.getBytes());

			// Preparazione liste per file accessori
			ArrayList<String> ditteArr = new ArrayList<String>();
			ArrayList<String> condArr = new ArrayList<String>();
			ArrayList<String> entiArr = new ArrayList<String>();

			for (VerificaImp v : verificheList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsente(v);


					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {
						/* I0070276 
						SediAddebito sa = getSedeAddebitoCpy(v);
						String tipoSA = "";

						if(sa != null && sa.getTipoUtente() != null){
							tipoSA = sa.getTipoUtente().getCode();
						}

						if("05".equals(tipoSA)){
							String rigaDitte = getRigaDitteVer(f, v, sa);
							ditteArr.add(rigaDitte);
						}

						if("01".equals(tipoSA)){
							String rigaCond = getRigaCondVer(f, v, sa);
							condArr.add(rigaCond);
						}

						if("02".equals(tipoSA) || "03".equals(tipoSA) || "04".equals(tipoSA)){
							String rigaEnti = getRigaEntiVer(f, v, sa);
							entiArr.add(rigaEnti);
						}*/	

						Sedi se = getSedeCpy(v);
						String tipoSA = "";

						if(se != null && se.getTipoUtente() != null){
							tipoSA = se.getTipoUtente().getCode();
						}

						if("05".equals(tipoSA)){
							String rigaDitte = getRigaDitteVer(f, v, se);
							ditteArr.add(rigaDitte);
						}

						if("01".equals(tipoSA)){
							String rigaCond = getRigaDitteVer(f, v, se);
							condArr.add(rigaCond);
						}

						if("02".equals(tipoSA) || "03".equals(tipoSA) || "04".equals(tipoSA)){
							String rigaEnti = getRigaDitteVer(f, v, se);
							entiArr.add(rigaEnti);
						}	
					}
				}
			}

			// Gestione file accessorio ditte
			if(ditteArr != null && ditteArr.size() > 0){
				FileAccDitteAction fada = FileAccDitteAction.instance();
				FileAccDitte fad = fada.getEntity();

				String ditteContent = getHeaderDitte();

				fad.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_DITTE.txt");

				Collections.sort(ditteArr);
				for(String s : ditteArr){
					ditteContent += s;
				}

				ditteContent += "TOTALE POSIZIONI GENERATE: " + ditteArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//ditteContent = ditteContent.replace(";", "");

				fad.setCsv(true);
				fad.setContent(ditteContent.getBytes());

				temporary.put("ditteSave", true);
			}

			// Gestione file accessorio condomini
			if(condArr != null && condArr.size() > 0){
				FileAccCondAction faca = FileAccCondAction.instance();
				FileAccCond fac = faca.getEntity();

				String condContent = getHeaderDitte();

				fac.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_CONDOMINI.txt");

				Collections.sort(condArr);
				for(String s : condArr){
					condContent += s;
				}

				condContent += "TOTALE POSIZIONI GENERATE: " + condArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//condContent = condContent.replace(";", "");

				fac.setCsv(true);
				fac.setContent(condContent.getBytes());

				temporary.put("condSave", true);
			}

			// Gestione file accessorio enti pubblici
			if(entiArr != null && entiArr.size() > 0){
				FileAccEntiAction faea = FileAccEntiAction.instance();
				FileAccEnti fae = faea.getEntity();

				String entiContent = getHeaderDitte();

				fae.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_ENTI_PUBBLICI.txt");

				Collections.sort(entiArr);
				for(String s : entiArr){
					entiContent += s;
				}

				entiContent += "TOTALE POSIZIONI GENERATE: " + entiArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//entiContent = entiContent.replace(";", "");

				fae.setCsv(true);
				fae.setContent(entiContent.getBytes());

				temporary.put("entiSave", true);
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private String getHeaderEnti() {
		String ret = padWith("RAGIONE_SOCIALE_INSTALLAZIONE", true, ' ', 30) + ";";

		ret += padWith("DESCRIZIONE_LOCALITA_INST", true, ' ', 30) + ";";
		ret += padWith("CODICE_FISCALE", true, ' ', 30) + ";";
		ret += padWith("RAGIONE_SOCIALE_ADDEBITO", true, ' ', 120) + ";";
		ret += padWith("INDIRIZZO_ADDEBITO", true, ' ', 120) + ";";
		ret += padWith("DESCRIZIONE_LOCALITA_ADD", true, ' ', 30) + ";";
		ret += padWith("ANNO", true, ' ', 10) + ";";
		ret += padWith("NUMERO", true, ' ', 10) + ";";

		ret += padWith("CODICE_UNIVOCO", true, ' ', 15) + ";";

		ret += padWith("NOTE_ADDEBITO", true, ' ', 600) + ";";

		return ret + "\n";
	}

	private String getHeaderCond() {
		String ret = padWith("RAGIONE_SOCIALE_INSTALLAZIONE", true, ' ', 30) + ";";

		ret += padWith("INDIRIZZO_INSTALLAZIONE", true, ' ', 30) + ";";
		ret += padWith("DESCRIZIONE_LOCALITA_INST", true, ' ', 30) + ";";
		ret += padWith("CODICE_FISCALE", true, ' ', 30) + ";";
		ret += padWith("INDIRIZZO_ADDEBITO", true, ' ', 30) + ";";
		ret += padWith("DESCRIZIONE_LOCALITA_ADD", true, ' ', 30) + ";";
		ret += padWith("ANNO", true, ' ', 10) + ";";
		ret += padWith("NUMERO", true, ' ', 10) + ";";

		ret += padWith("CODICE_UNIVOCO", true, ' ', 15) + ";";

		ret += padWith("NOTE_ADDEBITO", true, ' ', 600) + ";";

		return ret + "\n";
	}

	private String getHeaderDitte() {
		String ret = padWith("RAGIONE_SOCIALE_INSTALLAZIONE", true, ' ', 30) + ";";

		ret += padWith("INDIRIZZO_INSTALLAZIONE", true, ' ', 30) + ";";
		ret += padWith("DESCRIZIONE_LOCALITA_INST", true, ' ', 30) + ";";
		ret += padWith("CODICE_FISCALE", true, ' ', 30) + ";";
		ret += padWith("RAGIONE_SOCIALE_ADDEBITO", true, ' ', 120) + ";";
		ret += padWith("INDIRIZZO_ADDEBITO", true, ' ', 120) + ";";
		ret += padWith("DESCRIZIONE_LOCALITA_ADD", true, ' ', 30) + ";";
		ret += padWith("ANNO", true, ' ', 10) + ";";
		ret += padWith("NUMERO", true, ' ', 10) + ";";

		ret += padWith("CODICE_UNIVOCO", true, ' ', 15) + ";";

		ret += padWith("NOTE_ADDEBITO", true, ' ', 600) + ";";
		ret += padWith("COD_CONTABIL", true, ' ', 30) + ";";

		return ret + "\n";
	}

	private void estraiDatiAddebiti(List<Addebito> addebitiList) {
		try {
			Fattura f = this.getEntity();
			Vocabularies vocabularies = VocabulariesImpl.instance();

			/* CALCOLA GRUPPO */		
			Integer gruppo = this.calcolaGruppo(f);
			if (gruppo==null){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			} else {
				f.setGruppo(gruppo);
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "completed"));
			}	

			/* GESTIONE ADDEBITI */
			boolean mngAdd = false;

			if (addebitiList==null || addebitiList.size()<1){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			} else
				mngAdd = this.manageAddebiti(f, addebitiList);

			if (!mngAdd){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));
				return;
			}

			/* GESTIONE FILE */

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			f.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + ".txt");
			f.setContentType("text/plain");

			String content = getFileContentAdd(f, addebitiList);

			if (content==null || "".equals(content)){
				f.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Status", "nullified"));

				/* Riporta gli addebiti completed in stato verified, li slega dalla fattura e annulla gli attributi NumeroDoc e Fattura */
				this.resetAddebiti(addebitiList);
				FacesErrorUtils.addErrorMessage("commError", "File content: null.", "File content: null.");

				return;
			}

			f.setContent(content.getBytes());

			// Preparazione liste per file accessori
			ArrayList<String> ditteArr = new ArrayList<String>();
			ArrayList<String> condArr = new ArrayList<String>();
			ArrayList<String> entiArr = new ArrayList<String>();

			for (Addebito a : addebitiList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsenteAdd(a);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {
						/* I0070276 
						SediAddebito sa = getSedeAddebitoCpyAdd(a);
						String tipoSA = "";

						if(sa != null && sa.getTipoUtente() != null){
							tipoSA = sa.getTipoUtente().getCode();
						}

						if("05".equals(tipoSA)){
							String rigaDitte = getRigaDitteAdd(f, a, sa);
							ditteArr.add(rigaDitte);
						}

						if("01".equals(tipoSA)){
							String rigaCond = getRigaCondAdd(f, a, sa);
							condArr.add(rigaCond);
						}

						if("02".equals(tipoSA) || "03".equals(tipoSA) || "04".equals(tipoSA)){
							String rigaEnti = getRigaEntiAdd(f, a, sa);
							entiArr.add(rigaEnti);
						}*/

						Sedi se = getSedeCpyAdd(a);
						String tipoSA = "";

						if(se != null && se.getTipoUtente() != null){
							tipoSA = se.getTipoUtente().getCode();
						}

						if("05".equals(tipoSA)){
							String rigaDitte = getRigaDitteAdd(f, a, se);
							ditteArr.add(rigaDitte);
						}

						if("01".equals(tipoSA)){
							String rigaCond = getRigaDitteAdd(f, a, se);
							condArr.add(rigaCond);
						}

						if("02".equals(tipoSA) || "03".equals(tipoSA) || "04".equals(tipoSA)){
							String rigaEnti = getRigaDitteAdd(f, a, se);
							entiArr.add(rigaEnti);
						}	

					}
				}
			}

			// Gestione file accessorio ditte
			if(ditteArr != null && ditteArr.size() > 0){
				FileAccDitteAction fada = FileAccDitteAction.instance();
				FileAccDitte fad = fada.getEntity();

				String ditteContent = getHeaderDitte();

				fad.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_DITTE.txt");

				Collections.sort(ditteArr);
				for(String s : ditteArr){
					ditteContent += s;
				}

				ditteContent += "TOTALE POSIZIONI GENERATE: " + ditteArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//ditteContent = ditteContent.replace(";", "");

				fad.setCsv(true);
				fad.setContent(ditteContent.getBytes());

				temporary.put("ditteSave", true);
			}

			// Gestione file accessorio condomini
			if(condArr != null && condArr.size() > 0){
				FileAccCondAction faca = FileAccCondAction.instance();
				FileAccCond fac = faca.getEntity();

				String condContent = getHeaderDitte();

				fac.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_CONDOMINI.txt");

				Collections.sort(condArr);
				for(String s : condArr){
					condContent += s;
				}

				condContent += "TOTALE POSIZIONI GENERATE: " + condArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//condContent = condContent.replace(";", "");

				fac.setCsv(true);
				fac.setContent(condContent.getBytes());

				temporary.put("condSave", true);
			}

			// Gestione file accessorio enti pubblici
			if(entiArr != null && entiArr.size() > 0){
				FileAccEntiAction faea = FileAccEntiAction.instance();
				FileAccEnti fae = faea.getEntity();

				String entiContent = getHeaderDitte();

				fae.setFilename((codiceArpav!=null && !"".equals(codiceArpav)?codiceArpav:"??") + gruppo + "_ENTI_PUBBLICI.txt");

				Collections.sort(entiArr);
				for(String s : entiArr){
					entiContent += s;
				}

				entiContent += "TOTALE POSIZIONI GENERATE: " + entiArr.size() + "\n";
				//Non si rimuovono più i separatori perché serve scaricarlo anche in .csv
				//entiContent = entiContent.replace(";", "");

				fae.setCsv(true);
				fae.setContent(entiContent.getBytes());

				temporary.put("entiSave", true);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Integer calcolaGruppo(Fattura f) {
		try {
			//			if (f.getAnno()==null)
			//				return null;
			//
			//			String annoStr = f.getAnno().toString();

			String annoStr = this.getAnnoFatturazione();

			if (annoStr==null || annoStr.length()!=4 || Integer.parseInt(annoStr)<1)
				return null;

			String anno = annoStr.substring(2);//2018 -> 18

			int gruppoStart = Integer.parseInt(anno + "001");//18 -> 18001
			if (gruppoStart<1)
				return null;

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();

			if (arpav==null)
				return null;

			String evaluateGruppo = "SELECT fattura.gruppo FROM Fattura fattura " + 
					"LEFT JOIN fattura.serviceDeliveryLocation arpav " +
					"WHERE fattura.isActive = true AND fattura.statusCode.code = 'completed' AND " +
					"arpav.internalId = :arpavId AND fattura.gruppo >= " + gruppoStart + " order by fattura.gruppo desc";

			HashMap<String, Object> parameters= new HashMap<String, Object>(1);
			parameters.put("arpavId", arpav.getInternalId());

			@SuppressWarnings("unchecked")
			List<Integer> numList = (List<Integer>) ca.executeHQLwithParameters(evaluateGruppo, parameters);

			//Non sono presenti fatture
			if(numList==null || numList.isEmpty())
				return gruppoStart;//2018 -> 18001
			else {

				Integer gruppo = numList.get(0) + 1;
				return gruppo;
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Calcola o crea un progressivo per anno e sede ARPAV */ 
	private Integer numeroDocVerifica(Fattura f, VerificaImp v) {
		try {
			//
			//			if (f.getAnno()==null)
			//				return null;
			//
			//			String annoStr = f.getAnno().toString();

			String annoStr = this.getAnnoFatturazione();

			if (annoStr==null || annoStr.length()!=4 || Integer.parseInt(annoStr)<1)
				return null;

			String anno = annoStr.substring(2);//2018 -> 18

			int gruppoStart = Integer.parseInt(anno + "001");// 18 -> 18001
			if (gruppoStart<1)
				return null;

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();
			if (arpav==null)
				return null;

			/* Non uso la copia, ma la sede di addebito originale associata alla verifica
			 * perchè rappresente il collettore che mette a fattor comune tutte le verifiche di una stessa sede di addebito */
			//SediAddebito sa = v.getSedeAddebito();//this.getSedeAddebitoCpy(v);
			//if (sa==null)
			//	return null;

			String evaluateVerifiche = "SELECT ver.numeroDoc FROM VerificaImp ver " + 
					"LEFT JOIN ver.serviceDeliveryLocation arpav " +
					"LEFT JOIN ver.fattura fattura " +
					//"LEFT JOIN ver.sedeAddebito sa " +
					"WHERE ver.isActive = true AND ver.statusCode.code = 'completed' " +
					//"AND sa.internalId = :saId " +
					"AND arpav.internalId = :arpavId AND fattura.isActive = true AND fattura.gruppo like '" + anno + "%' order by ver.numeroDoc desc";

			HashMap<String, Object> parameters = new HashMap<String, Object>(1);
			parameters.put("arpavId", arpav.getInternalId());
			//parameters1.put("saId", sa.getInternalId());

			@SuppressWarnings("unchecked")
			List<Integer> numListVer = (List<Integer>) ca.executeHQLwithParameters(evaluateVerifiche, parameters);

			Integer numDoc = 0;

			//Non sono presenti verifiche
			if(numListVer==null || numListVer.isEmpty())
				numDoc = 1;
			else
				numDoc = numListVer.get(0) + 1;

			return numDoc;	

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Calcola o crea un progressivo per anno e sede ARPAV */
	private Integer numeroDocAddebito(Fattura f, Addebito a) {
		try {
			//			if (f.getAnno()==null)
			//				return null;
			//
			//			String annoStr = f.getAnno().toString();

			String annoStr = this.getAnnoFatturazione();

			if (annoStr==null || annoStr.length()!=4 || Integer.parseInt(annoStr)<1)
				return null;

			String anno = annoStr.substring(2);//2018 -> 18

			int gruppoStart = Integer.parseInt(anno + "001");// 18 -> 18001
			if (gruppoStart<1)
				return null;

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();
			if (arpav==null)
				return null;

			/* Non uso la copia, ma la sede di addebito originale associata all'addebito
			 * perchè rappresente il collettore che mette a fattor comune tutti gli addebiti di una stessa sede di addebito */
			//SediAddebito sa = a.getSedeAddebito();//this.getSedeAddebitoCpy(v);
			//if (sa==null)
			//	return null;

			String evaluateVerifiche = "SELECT a.numeroDoc FROM Addebito a " + 
					"LEFT JOIN a.serviceDeliveryLocation arpav " +
					"LEFT JOIN a.fattura fattura " +
					//"LEFT JOIN ver.sedeAddebito sa " +
					"WHERE a.isActive = true AND a.statusCode.code = 'completed' " +
					//"AND sa.internalId = :saId " +
					"AND arpav.internalId = :arpavId AND fattura.isActive = true AND fattura.gruppo like '" + anno + "%' order by a.numeroDoc desc";

			HashMap<String, Object> parameters = new HashMap<String, Object>(1);
			parameters.put("arpavId", arpav.getInternalId());
			//parameters1.put("saId", sa.getInternalId());

			@SuppressWarnings("unchecked")
			List<Integer> numListVer = (List<Integer>) ca.executeHQLwithParameters(evaluateVerifiche, parameters);

			Integer numDoc = 0;

			//Non sono presenti verifiche
			if(numListVer==null || numListVer.isEmpty())
				numDoc = 1;
			else
				numDoc = numListVer.get(0) + 1;

			return numDoc;	

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	} 

	private void resetVerifiche(List<VerificaImp> verificheList) {
		try {
			if (verificheList==null || verificheList.size()<1)
				return;

			//Gestisce le verifiche validata
			Vocabularies vocabularies = VocabulariesImpl.instance();
			for (VerificaImp v:verificheList){
				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					//Se c'è almeno una verifica validata
					if ("completed".equals(status.getCode())){
						v.setFattura(null);
						v.setNumeroDoc(null);

						v.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "verified"));
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private void resetAddebiti(List<Addebito> addebitiList) {
		try {
			if (addebitiList==null || addebitiList.size()<1)
				return;

			//Gestisce le verifiche validata
			Vocabularies vocabularies = VocabulariesImpl.instance();
			for (Addebito a:addebitiList){
				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					//Se c'è almeno un addebito validato
					if ("completed".equals(status.getCode())){
						a.setFattura(null);
						a.setNumeroDoc(null);

						a.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "verified"));
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private boolean manageVerifiche(Fattura f, List<VerificaImp> verificheList) {
		try {
			boolean ret = false;

			/* Non può verificarsi
			if (verificheList==null || verificheList.size()<1)
				return ret; */

			//Controlla che ci sia almeno una verifica validata
			for (VerificaImp v:verificheList){
				/* I0070276 
				if (v.getSedeAddebito()!=null){//Non dovrei poter validarla
					CodeValuePhi status = v.getStatusCode();

					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

							//Se c'è almeno una verifica validata
							if ("verified".equals(status.getCode()))
								ret = true;
					}
				}*/

				if (v.getSedi()!=null){//Non dovrei poter validarla
					CodeValuePhi status = v.getStatusCode();

					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

						//Se c'è almeno una verifica validata
						if ("verified".equals(status.getCode()))
							ret = true;
					}
				}
			}

			if (!ret)
				return ret;

			//Definisco una Map per memorizzare i numeri documento movimentati 
			HashMap<Long, Integer> numeriDocs= new HashMap<Long, Integer>();
			Integer max = 0;

			Vocabularies vocabularies = VocabulariesImpl.instance();

			//Gestisce le verifiche validata
			for (VerificaImp v:verificheList){

				//				if (v.getSedeAddebito()!=null){//Non dovrei poter validarla
				//					CodeValuePhi status = v.getStatusCode();
				//					
				//					if (status!=null){
				//						if (status instanceof HibernateProxy)
				//							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();
				//						
				//						//Se c'è almeno una verifica validata
				//						if ("verified".equals(status.getCode())){
				//							Integer numeroDoc = 0;
				//							
				//							
				//							/* Recupera la sede di addebito originale (non la copia) che rappresente il collettore
				//							 * che mette a fattor comune tutte le verifiche di una stessa sede di addebito */
				//							SediAddebito sa = v.getSedeAddebito();//this.getSedeAddebitoCpy(v);
				//							
				//							if (numeriDocs.isEmpty()){
				//								numeroDoc = this.numeroDocVerifica(f, v);
				//								max = numeroDoc;
				//								numeriDocs.put(sa.getInternalId(), numeroDoc);
				//							} else {
				//								//Se c'è già un numero doc per quella sede di addebito, lo usa senza incrementare max
				//								if (numeriDocs.containsKey(sa.getInternalId()))
				//									numeroDoc = numeriDocs.get(sa.getInternalId());
				//								
				//								else {
				//									//Incrementa max e lo usa
				//									numeroDoc = ++max;
				//									numeriDocs.put(sa.getInternalId(), numeroDoc);
				//								}
				//							}
				//							
				//							v.setNumeroDoc(numeroDoc);
				//							v.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "completed"));
				//							
				//							v.setFattura(f);
				//						}
				//					}
				//				}

				if (v.getSedi()!=null){//Non dovrei poter validarla
					CodeValuePhi status = v.getStatusCode();

					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

						//Se c'è almeno una verifica validata e non esente
						if ("verified".equals(status.getCode()) && !isEsente(v)){
							Integer numeroDoc = 0;


							/* Recupera la sede di addebito originale (non la copia) che rappresente il collettore
							 * che mette a fattor comune tutte le verifiche di una stessa sede di addebito */
							Sedi se = v.getSedi();//this.getSedeAddebitoCpy(v);

							if (numeriDocs.isEmpty()){
								numeroDoc = this.numeroDocUnificato(f, v, null);
								max = numeroDoc;
								numeriDocs.put(se.getInternalId(), numeroDoc);
							} else {
								//Se c'è già un numero doc per quella sede di addebito, lo usa senza incrementare max
								if (numeriDocs.containsKey(se.getInternalId()))
									numeroDoc = numeriDocs.get(se.getInternalId());

								else {
									//Incrementa max e lo usa
									numeroDoc = ++max;
									numeriDocs.put(se.getInternalId(), numeroDoc);
								}
							}

							v.setNumeroDoc(numeroDoc);
							v.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "completed"));

							v.setFattura(f);
						}else if("verified".equals(status.getCode()) && isEsente(v)){
							// Verifica esente: mi limito ad impostarne lo stato per evitare che venga svalidata
							v.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "completed"));
						}
					}
				}
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private boolean manageAddebiti(Fattura f, List<Addebito> addebitiList) {
		try {
			boolean ret = false;

			/* Non può verificarsi
			if (addebitiList==null || addebitiList.size()<1)
				return ret; */

			//Controlla che ci sia almeno un addebito validato
			for (Addebito a:addebitiList){

				CodeValuePhi status = a.getStatusCode();
				/* I0070276 sede addebito sostituita da sede con flag addebito
				if (a.getSedeAddebito()!=null){//Non dovrei poter validarlo
					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

							//Se c'è almeno un addebito validato
							if ("verified".equals(status.getCode()))
								ret = true;
					}
				}*/
				if (a.getSedi()!=null){//Non dovrei poter validarlo
					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

						//Se c'è almeno un addebito validato
						if ("verified".equals(status.getCode()))
							ret = true;
					}
				}
			}

			if (!ret)
				return ret;

			//Definisco una Map per memorizzare i numeri documento movimentati 
			HashMap<Long, Integer> numeriDocs= new HashMap<Long, Integer>();
			Integer max = 0;

			Vocabularies vocabularies = VocabulariesImpl.instance();

			//Gestisce gli addebiti validati(sostituito da I0070276 )
			//			for (Addebito a:addebitiList){
			//				if (a.getSedeAddebito()!=null){//Non dovrei poter validarlo
			//					CodeValuePhi status = a.getStatusCode();
			//					
			//					if (status!=null){
			//						if (status instanceof HibernateProxy)
			//							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();
			//						
			//						//Se c'è almeno un addebito validato
			//						if ("verified".equals(status.getCode())){
			//							Integer numeroDoc = 0;
			//							
			//							
			//							/* Recupera la sede di addebito originale (non la copia) che rappresente il collettore
			//							 * che mette a fattor comune tutti gli addebiti di una stessa sede di addebito */
			//							SediAddebito sa = a.getSedeAddebito();//this.getSedeAddebitoCpy(v);
			//							
			//							if (numeriDocs.isEmpty()){
			//								numeroDoc = this.numeroDocAddebito(f, a);
			//								max = numeroDoc;
			//								numeriDocs.put(sa.getInternalId(), numeroDoc);
			//							} else {
			//								//Se c'è già un numero doc per quella sede di addebito, lo usa senza incrementare max
			//								if (numeriDocs.containsKey(sa.getInternalId()))
			//									numeroDoc = numeriDocs.get(sa.getInternalId());
			//								
			//								else {
			//									//Incrementa max e lo usa
			//									numeroDoc = ++max;
			//									numeriDocs.put(sa.getInternalId(), numeroDoc);
			//								}
			//							}
			//							
			//							a.setNumeroDoc(numeroDoc);
			//							a.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "completed"));
			//							
			//							a.setFattura(f);
			//						}
			//					}
			//				}
			//			}
			//Gestisce gli addebiti validati(nuovo con sede con flag addebito)
			for (Addebito a:addebitiList){
				if (a.getSedi()!=null){//Non dovrei poter validarlo
					CodeValuePhi status = a.getStatusCode();

					if (status!=null){
						if (status instanceof HibernateProxy)
							status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

						//Se c'è almeno un addebito validato
						if ("verified".equals(status.getCode())){
							Integer numeroDoc = 0;


							/* Recupera la sede di addebito originale (non la copia) che rappresente il collettore
							 * che mette a fattor comune tutti gli addebiti di una stessa sede di addebito */
							Sedi se = a.getSedi();//this.getSedeAddebitoCpy(v);

							if (numeriDocs.isEmpty()){
								numeroDoc = this.numeroDocUnificato(f, null, a);
								max = numeroDoc;
								numeriDocs.put(se.getInternalId(), numeroDoc);
							} else {
								//Se c'è già un numero doc per quella sede di addebito, lo usa senza incrementare max
								if (numeriDocs.containsKey(se.getInternalId()))
									numeroDoc = numeriDocs.get(se.getInternalId());

								else {
									//Incrementa max e lo usa
									numeroDoc = ++max;
									numeriDocs.put(se.getInternalId(), numeroDoc);
								}
							}

							a.setNumeroDoc(numeroDoc);
							a.setStatusCode((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "Stato", "completed"));

							a.setFattura(f);
						}
					}
				}
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Integer getDocumentiDal(Fattura f) {
		try {
			Integer min = 0;

			if (f==null)
				return min;

			List<VerificaImp> verificheList = f.getVerificaImp();
			if (verificheList!=null && verificheList.size()>0){				
				//Calcola minimo
				for (VerificaImp v:verificheList){
					Integer numeroDoc = v.getNumeroDoc();

					if (min==0 || numeroDoc < min)
						min = numeroDoc;
				}
			}

			List<Addebito> addebitiList = f.getAddebito();
			if (addebitiList!=null && addebitiList.size()>0){				
				//Calcola minimo
				for (Addebito a:addebitiList){
					Integer numeroDoc = a.getNumeroDoc();

					if (min==0 || numeroDoc < min)
						min = numeroDoc;
				}
			}

			return min;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Integer getDocumentiAl(Fattura f) {
		try {
			Integer max = 0;

			if (f==null)
				return max;

			List<VerificaImp> verificheList = f.getVerificaImp();
			if (verificheList!=null && verificheList.size()>0){				
				//Calcola massimo
				for (VerificaImp v:verificheList){
					Integer numeroDoc = v.getNumeroDoc();

					if (max==0 || numeroDoc > max)
						max = numeroDoc;
				}
			}

			List<Addebito> addebitiList = f.getAddebito();
			if (addebitiList!=null && addebitiList.size()>0){				
				//Calcola minimo
				for (Addebito a:addebitiList){
					Integer numeroDoc = a.getNumeroDoc();

					if (max==0 || numeroDoc > max)
						max = numeroDoc;
				}
			}

			return max;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Integer getTotaleDocumenti(Fattura f) {
		// N.B..: questo metodo resituisce il numero di verifiche o di addebiti
		// associati alla fattura. Il totale documenti, cioè di fatture inviate
		// è inferiore perché se una sede di addebito compare più volte nella
		// stessa  entità Fattura la fattura effettiva sarà unica 
		// con più verifiche addebitate
		try {
			Integer tot = 0;

			if (f==null)
				return tot;

			List<VerificaImp> verificheList = f.getVerificaImp();
			if (verificheList!=null && verificheList.size()>0){
				tot = verificheList.size();
			}

			List<Addebito> addebitiList = f.getAddebito();
			if (addebitiList!=null && addebitiList.size() > tot)
				tot = addebitiList.size(); 

			return tot;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Integer getDocumentiGenerabili(Fattura f){
		return getDocumentiAl(f) - getDocumentiDal(f) + 1;
	}

	private String getFileContentVer(Fattura f, List<VerificaImp> verificheList) {
		try {

			if (f==null || verificheList==null || verificheList.size()<1)
				return null;

			Map<Integer, String> headerLines = getHeaderLines(f, verificheList);
			Map<Integer, ArrayList<String>> detailLines = getDetailLines(f, verificheList);
			Map<Integer, ArrayList<String>> infoLines = getInfoLines(f, verificheList);

			return this.getFileContent(headerLines, detailLines, infoLines);

		} catch (Exception ex) {
			log.error(ex);
			return null;
			//throw new RuntimeException(ex);
		}
	}

	private String getFileContentAdd(Fattura f, List<Addebito> addebitiList) {
		try {

			if (f==null || addebitiList==null || addebitiList.size()<1)
				return null;

			Map<Integer, String> headerLines = getHeaderLinesAdd(f, addebitiList);
			Map<Integer, ArrayList<String>> detailLines = getDetailLinesAdd(f, addebitiList);
			Map<Integer, ArrayList<String>> infoLines = getInfoLinesAdd(f, addebitiList);

			return this.getFileContent(headerLines, detailLines, infoLines);

		} catch (Exception ex) {
			log.error(ex);
			return null;
			//throw new RuntimeException(ex);
		}
	}

	private String getFileContent(Map<Integer, String> headerLines, Map<Integer, ArrayList<String>> detailLines, Map<Integer, ArrayList<String>> infoLines) {
		try {

			if (headerLines==null || headerLines.size()==0)
				return null;

			if (detailLines==null || detailLines.size()==0)
				return null;

			if (infoLines==null || infoLines.size()==0)
				return null;

			String header 	= "";
			String detail 	= "";
			String info		= "";

			DecimalFormat df = new DecimalFormat("0000000000.00");

			DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			df.setDecimalFormatSymbols(dfs);

			/* Itero le linee di testata e le linee di dettaglio per calcolare la somma degli importi per ogni numeroDoc.
			 * Tale valore deve essere sostituite, in formato 13.2 - padLeft - '0', alla stringa TOTIMPONIBILE della relativa linea di testata 
			 * 
			 * Anche l'importo delle singole linne di dettaglio deve essere convertito nel formato 13.2 - padLeft - '0' */
			Double imponibile =0.0;
			Double esenteIva =0.0;
			for (Integer keyHeader : headerLines.keySet()){ 
				Double imponibileLinea = 0.0;
				Double esenteIvaLinea = 0.0;

				//Itero le linee di dettaglio
				for (Integer keyDetail : detailLines.keySet()){
					if (keyHeader==keyDetail){
						ArrayList<String> lines = detailLines.get(keyDetail);
						for (int i=0;i<lines.size();i++){

							String line = lines.get(i);
							String[] cells = line.split(";");

							//Recupero l'imponibile della linea di dettaglio e lo sommo al totale imponibile
							String cell12 = cells[12];
							Double imp = Double.parseDouble(cell12);

							imponibileLinea += imp;

							//Converto l'imponibile della linea di dettaglio nel formato richiesto
							//Da tracciato, il formato è in formato 13.2 - padLeft - '0'. Usiamo 14 per compensare il replace del carattere "."
							String newImp = this.padWith(df.format(imp), false, '0', 14).replace(".", "");
							cells[12] = newImp;

							//Recupero l'importo fuori campo IVA della linea di dettaglio e lo sommo al totale esente IVA
							String cell30 = cells[30];
							if(!"0000000000000".equals(cell30)){
								Double fcIva = Double.parseDouble(cell30);
								esenteIvaLinea += fcIva;
								//imponibileLinea -= fcIva;

								//Converto l'importo fuori campo IVA in una sequenza di zeri
								//Da tracciato, il campo va sempre valorizzato così
								String newFcIva = this.padWith("", true, '0', 13);
								cells[30] = newFcIva;
							}

							//Recupero l'imponibile art. 8 della linea di dettaglio e lo sommo al totale esente IVA (solo addebiti)
							String cell31 = cells[31];
							if(!"0000000000000".equals(cell31)){
								Double impArt8 = Double.parseDouble(cell31);
								esenteIvaLinea += impArt8;
								//imponibileLinea -= impArt8;

								//Converto l'imponibile art. 8 in una sequenza di zeri
								//Da tracciato, il campo va sempre valorizzato così
								String newImpArt8 = this.padWith("", true, '0', 13);
								cells[31] = newImpArt8;
							}

							//Ricreo e setto la linea di dettaglio
							String newLine = "";
							for (String cell:cells)
								newLine += cell + ";";

							lines.set(i, newLine);
							detailLines.put(keyDetail, lines);
						}		        		
					}
				}

				//Da tracciato, il formato è in formato 13.2 - padLeft - '0'. Usiamo 14 per compensare il replace del carattere "."
				headerLines.put(keyHeader, (headerLines.get(keyHeader)).replace("TOTIMPONIBILE", this.padWith(df.format(imponibileLinea), false, '0', 14).replace(".", "")));
				imponibile += imponibileLinea;
				esenteIva += esenteIvaLinea;
			}

			/** * Setta il totale imponibile * **/
			getEntity().setImponibile(imponibile);
			/** ****************************** **/

			getEntity().setEsenteIva(esenteIva);

			/* Costruisce il blocco header */
			Set<Integer> headerKeys = headerLines.keySet();
			for(Integer key : headerKeys)
				header += headerLines.get(key) + "\n";

			/* Costruisce il blocco detail */
			Set<Integer> detailKeys = detailLines.keySet();
			for(Integer key : detailKeys){
				ArrayList<String> dl = detailLines.get(key);
				for (String line:dl)
					detail += line + "\n";
			}

			/* Costruisce il blocco info */
			Set<Integer> infoKeys = infoLines.keySet();
			for(Integer key : infoKeys){

				ArrayList<String> il = infoLines.get(key);
				for (String line:il)
					info += line + "\n";
			}

			/* Aggrega e restituisce il contenuto del file */
			String content = header + detail + info;

			/* L'eliminazione del carattere di separazione viene fatto durante il download - Vedi: FattureRest.java @GET */
			//content = content.replace(";", "");

			return content;

		} catch (Exception ex) {
			log.error(ex);
			return null;
			//throw new RuntimeException(ex);
		}
	}

	private Map<Integer, String> getHeaderLines(Fattura f, List<VerificaImp> verificheList) {	
		try {

			//Contiene tutte le linee di testata associate ad un dato NumeroDoc
			Map<Integer, String> headerLines = new TreeMap<Integer, String>();

			//Anno
			String anno = f.getAnno().toString();
			String annoDoc = this.getAnnoFatturazione();

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (VerificaImp v : verificheList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsente(v);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (v.getNumeroDoc()!=null)
							numeroDoc = v.getNumeroDoc();

						//Solo se in headerLines non è già presente la linea di testata relativa al NumeroDoc, ne crea una e la aggiunge
						if (headerLines.isEmpty() || !headerLines.containsKey(numeroDoc)) {

							//T - Testata
							String newLine = "T" + ";";

							//Anno - 4 caratteri
							newLine += annoDoc + ";";

							//Numero doc - 6 caratteri
							newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

							//Dipartimento - 2 caratteri
							newLine += padWith(codiceArpav, false, '0', 2) + ";";

							//Codice - 6 caratteri - non serve
							newLine += "000000" + ";";

							//Sottocodice - 6 caratteri - non serve
							newLine += "000000" + ";";

							//Sottocodice addebito - 6 caratteri - non serve
							newLine += "000000" + ";";

							/* I0070276 sede addebito sostituita da sede con flag addebito
							//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
							SediAddebito sa = this.getSedeAddebitoCpy(v);

							PersoneGiuridiche pg = null;

							String regioneSociale = "";
							String indirizzo = "";				
							String località = "";
							String cap = "";
							String prov = "";

							String codContabilita = "";
							String tel = "";
							String fax = "";

							if (sa!=null){
								regioneSociale = sa.getDenominazione();
								pg = sa.getPersonaGiuridica();
							}

							//Regione sociale - 80 caratteri
							newLine += padWith(regioneSociale, true, ' ', 80) + ";";

							//Dati sede di addebito
							if (sa!=null){
								codContabilita = sa.getCodContabilita();

								if (sa.getAddr()!=null){
									String via = sa.getAddr().getStr();
									String numero = sa.getAddr().getBnr();
									indirizzo = (via!=null?via:"") + " " + numero;

									località = sa.getAddr().getCty();
									cap = sa.getAddr().getZip();
									prov = sa.getAddr().getCpa();
								}

								if (sa.getTelecom()!=null){
									tel = sa.getTelecom().getAs();
									fax = sa.getTelecom().getBad();		
								}
							}
							//I0070276 sede addebito sostituita da sede con flag addebito */

							Sedi se = this.getSedeCpy(v);

							PersoneGiuridiche pg = null;

							String regioneSociale = "";
							String indirizzo = "";				
							String località = "";
							String cap = "";
							String prov = "";

							String codContabilita = "";
							String tel = "";
							String fax = "";

							String cf= "";
							String piva = "";

							if (se!=null){
								regioneSociale = se.getDenominazioneUnitaLocale();
								pg = se.getPersonaGiuridica();
							}

							//Regione sociale - 80 caratteri
							newLine += padWith(regioneSociale, true, ' ', 80) + ";";

							//Dati sede di addebito
							if (se!=null){
								codContabilita = se.getCodContabilita();

								if (se.getAddr()!=null){
									String via = se.getAddr().getStr();
									String numero = se.getAddr().getBnr();
									indirizzo = (via!=null?via:"") + " " + numero;

									località = se.getAddr().getCty();
									cap = se.getAddr().getZip();
									prov = se.getAddr().getCpa();
								}

								if (se.getTelecom()!=null){
									tel = se.getTelecom().getAs();
									fax = se.getTelecom().getBad();		
								}
							}

							//Indirizzo (Via + Numero) - 35 caratteri 				
							newLine += padWith(indirizzo, true, ' ', 35) + ";";
							//Località - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";	
							//CAP - 5
							newLine += padWith(cap, true, ' ', 5) + ";";
							//Provincia - 3
							newLine += padWith(prov, true, ' ', 3) + ";";



							if (pg != null){
								cf = pg.getCodiceFiscale();
								piva = pg.getPatritaIva();
							}

							//Codice fiscale Persona Giuridica - 16 caratteri
							newLine += padWith(cf, true, ' ', 16) + ";";

							//Partita IVA Persona Giuridica - 11 caratteri
							newLine += padWith(piva, true, ' ', 11) + ";";

							//Codice contabilità - 12 caratteri
							newLine += padWith(codContabilita, true, ' ', 12) + ";";			

							//Tel. (as) - 13
							newLine += padWith(tel, true, ' ', 13) + ";";

							//Fax (bad) - 13
							newLine += padWith(fax, true, ' ', 13) + ";";

							//Data della generazione del flusso - 8 caratteri
							String dataElab = "";
							Date creationDate = f.getCreationDate();
							if (creationDate!=null){
								SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
								dataElab = df.format(creationDate);
							}

							newLine += padWith(dataElab, true, ' ', 8) + ";";

							//Imponibile - 13.2 caratteri - Viene calcolato come somma degli importi di tutte le verifiche associate a questa riga di testata
							String imponibile = "TOTIMPONIBILE";
							newLine += padWith(imponibile, true, '0', 13) + ";";

							//Quota esente IVA - 13.2 caratteri - Da capire come calcolarlo - per il momento passiamo 13 zeri
							//String qeiva = "QUOTESENTEIVA";
							String qeiva = "";
							newLine += padWith(qeiva, true, '0', 13) + ";";

							//Tipo movimento - 1 carattere
							String tipoMovimento = "F";
							newLine += padWith(tipoMovimento, true, ' ', 1) + ";";

							//Gruppo elab. - 5 caratteri
							String gruppo = "";
							if (f.getGruppo()!=null)
								gruppo = f.getGruppo().toString();

							newLine += padWith(gruppo, true, ' ', 5) + ";";

							//Tipo doc elab - 5 caratteri
							String tipoDocElab = "F";
							newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

							//Anno elab - 4 caratteri
							newLine += padWith(anno, true, ' ', 4) + ";";

							//Tipo appar elab - 1 carattere - Campo tipo fattura?
							String tipoApparElab = "";
							CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
							if (tipologiaFattura!=null){
								tipoApparElab = tipologiaFattura.getCode();
								tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
							}

							newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

							//Dal mese elab - 2 caratteri
							String dalMese = f.getMeseDal()!=null?f.getMeseDal().toString():"";					    	
							newLine += padWith(dalMese, false, '0', 2) + ";";

							//Al mese elab - 2 caratteri
							String alMese = f.getMeseAl()!=null?f.getMeseAl().toString():"";					    	
							newLine += padWith(alMese, false, '0', 2) + ";";

							//Comune della sede di addebito (località) - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";

							//Imponibile4 - 13.2 caratteri
							String imp4 = "";
							newLine += padWith(imp4, true, '0', 13) + ";";

							//Imponibile10 - 13.2 caratteri
							String imp10 = "";
							newLine += padWith(imp10, true, '0', 13) + ";";

							//Imponibile20 - 13.2 caratteri
							String imp20 = "";
							newLine += padWith(imp20, true, '0', 13) + ";";

							//ImponibileFcIva - 13.2 caratteri
							String impFcIva = "";
							newLine += padWith(impFcIva, true, '0', 13) + ";";

							//ImponibileArt8 - 13.2 caratteri
							String impArt8 = "";
							newLine += padWith(impArt8, true, '0', 13) + ";";

							//Iva 4% - 13.2
							String iva4 = "";
							newLine += padWith(iva4, true, '0', 13) + ";";

							//Iva 10% - 13.2 caratteri
							String iva10 = "";
							newLine += padWith(iva10, true, '0', 13) + ";";

							//Iva 20% - 13.2 caratteri
							String iva20 = "";
							newLine += padWith(iva20, true, '0', 13) + ";";

							//Bollo - 13.2 caratteri
							String bollo = "";
							newLine += padWith(bollo, true, '0', 13) + ";";

							/* Intestatario della fattura - da mail 28/08: Corrisponde alla sede di addebito
							 * ma la sede addebito è stata sostituita da sede con flag addebito */

							//Intestatario - 35 caratteri
							newLine += padWith(regioneSociale, true, ' ', 35) + ";";

							//Indirizzo intestatario - 35 caratteri
							newLine += padWith(indirizzo, true, ' ', 35) + ";";							

							//Comune intestatario - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";	

							//Provincia intestatario - 2 caratteri
							newLine += padWith(prov, true, ' ', 2) + ";";	

							//CAP intestatario - 5 caratteri
							newLine += padWith(cap, true, ' ', 5) + ";";	

							//Codice fiscale intestatario - 16 caratteri
							newLine += padWith(cf, true, ' ', 16) + ";";	

							//Partita IVA intestatario - 11 caratteri
							newLine += padWith(piva, true, ' ', 11) + ";";	

							//Aggiunge la nuova linea di testata in headerLines
							headerLines.put(numeroDoc, newLine);
						}
					}
				}
			}

			return headerLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
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

	private Map<Integer, ArrayList<String>> getDetailLines(Fattura f, List<VerificaImp> verificheList) {
		try {

			//Contiene tutte le linee di dettaglio associate ad un dato NumeroDoc
			Map<Integer, ArrayList<String>> datailsLines = new TreeMap<Integer, ArrayList<String>>();

			//Anno
			String anno = f.getAnno().toString();
			String annoDoc = this.getAnnoFatturazione();

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (VerificaImp v : verificheList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsente(v);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (v.getNumeroDoc()!=null)
							numeroDoc = v.getNumeroDoc();

						//D - Dettaglio
						String newLine = "D" + ";";

						//Anno - 4 caratteri
						newLine += annoDoc + ";";

						//Numero doc - 6 caratteri
						newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

						//Dipartimento - 2 caratteri
						newLine += padWith(codiceArpav, false, '0', 2) + ";";

						//Codice - 6 caratteri - non serve
						newLine += "000000" + ";";

						//Sottocodice - 6 caratteri - non serve
						newLine += "000000" + ";";

						//Installazione - indirizzo della sede di installazione - 41 caratteri
						String indirizzoSi = "";
						SediInstallazione si = getSedeInstallazioneCpy(v);
						if (si!=null && si.getAddr()!=null)
							indirizzoSi = (si.getAddr().getStr()!=null ? si.getAddr().getStr() + " " : "") +
							(si.getAddr().getBnr()!=null ? si.getAddr().getBnr() + " ": "");
						//(si.getAddr().getCty()!=null ? "- " + si.getAddr().getCty() + " ": "") +
						//(si.getAddr().getCpa()!=null ? "(" + si.getAddr().getCpa() + ") ": "");

						newLine += padWith(indirizzoSi, true, ' ', 41) + ";";

						//Matricola - SIGLA/TIPO/MATRICOLA/ANNO - 14 caratteri
						String matricola = getMatricola(v);
						newLine += padWith(matricola, true, ' ', 14) + ";";

						//Causale
						CodeValuePhi causaleCv = v.getCausale();
						String causale = "";
						String desrCausale = "";

						if (causaleCv!=null){
							//DisplayName
							if (causaleCv.getDisplayName()!=null)
								causale = causaleCv.getDisplayName();

							//Current translation
							if (causaleCv.getCurrentTranslation()!=null){
								String separator = "-";
								String[] splitTransl = causaleCv.getCurrentTranslation().split(separator);
								desrCausale = separator + splitTransl[1];
							}
						}

						//Causale - 2 caratteri 
						//String causale = "";
						newLine += padWith(causale, true, ' ', 2) + ";";

						//Descrizione causale - 30 caratteri 
						//String desrCausale = "";
						newLine += padWith(desrCausale, true, ' ', 30) + ";";

						//Causale addizionale - 4 caratteri - Solo addebiti
						String causaleAdd = "";
						newLine += padWith(causaleAdd, true, ' ', 4) + ";";

						//Descrizione causale addizionale - 40 caratteri - Solo addebiti
						String desrCausaleAdd = "";
						newLine += padWith(desrCausaleAdd, true, ' ', 40) + ";";

						//Importo - lasciarlo in formato Double to String
						newLine += v.getImporto()!=null?v.getImporto() + ";":"0.0" + ";";

						HashMap<String, String> tipoImpianto = getTipo(v);

						//Tipo 
						newLine += tipoImpianto.get("tipo") + ";";

						//Sottotipo 
						newLine += tipoImpianto.get("sottoTipo") + ";";

						//Data della verifica - 8 caratteri
						String dataVerifica = "";
						Date data = v.getData();
						if (data!=null){
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
							dataVerifica = df.format(data);
						}

						newLine += padWith(dataVerifica, true, ' ', 8) + ";";

						//Ore - hh,mm - 5 caratteri
						String hhServizio = (v.getHhServizio()!=null && !"".equals(v.getHhServizio())?v.getHhServizio():"00");
						String mmServizio = (v.getMmServizio()!=null && !"".equals(v.getMmServizio())?v.getMmServizio():"00");

						if (hhServizio.length()==1)
							hhServizio = "0" + hhServizio;

						if (mmServizio.length()==1)
							mmServizio = "0" + mmServizio;

						String ore = hhServizio + "," + mmServizio;
						newLine += padWith(ore, true, ' ', 5) + ";";

						//Gruppo elab. - 5 caratteri
						String gruppo = "";
						if (f.getGruppo()!=null)
							gruppo = f.getGruppo().toString();

						newLine += padWith(gruppo, true, ' ', 5) + ";";

						//Tipo doc elab - 5 caratteri
						String tipoDocElab = "F";
						newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

						//Anno elab - 4 caratteri
						newLine += padWith(anno, true, ' ', 4) + ";";

						//Tipo appar elab - 1 carattere - Campo tipo fattura?
						String tipoApparElab = "";
						CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
						if (tipologiaFattura!=null){
							tipoApparElab = tipologiaFattura.getCode();
							tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
						}

						newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

						//Dal mese elab - 2 caratteri
						String dalMese = f.getMeseDal()!=null?f.getMeseDal().toString():"";					    	
						newLine += padWith(dalMese, false, '0', 2) + ";";

						//Al mese elab - 2 caratteri
						String alMese = f.getMeseAl()!=null?f.getMeseAl().toString():"";					    	
						newLine += padWith(alMese, false, '0', 2) + ";";

						String regioneSociale = "";
						String località = "";

						/* I0070276 
						//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
						SediAddebito sa = this.getSedeAddebitoCpy(v);//v.getSedeAddebito();

						if (sa!=null)
							regioneSociale = sa.getDenominazione();
						 */
						//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
						Sedi se = this.getSedeCpy(v);//v.getSedeAddebito();
						Impianto impCpy = this.getImpiantoCpy(v);

						/*
						if (sa!=null)
							regioneSociale = sa.getDenominazione();						

						//Regione sociale - 80 caratteri
						newLine += padWith(regioneSociale, true, ' ', 80) + ";";

						//Comune - 30 caratteri
						if (sa!=null && sa.getAddr()!=null)
							località = sa.getAddr().getCty();
						 */

						// Mail 25/11 inserire la ragione sociale installazione e non addebito
						//if (se!=null)
						//	regioneSociale = se.getDenominazioneUnitaLocale();						
						if(si!=null)
							regioneSociale = si.getDenominazione();

						//Regione sociale - 80 caratteri
						newLine += padWith(regioneSociale, true, ' ', 80) + ";";

						/* Comune - 30 caratteri
						 * Mail 22/05 Canciani - mostrare il comune della sede di istallazione dell'impianto (copia) associato alla verifica */						
						if (impCpy!=null && impCpy.getSedeInstallazione()!=null && impCpy.getSedeInstallazione().getAddr()!=null)
							località = impCpy.getSedeInstallazione().getAddr().getCty();						

						newLine += padWith(località, true, ' ', 30) + ";";

						//Descrizione sottotipo - 30 caratteri
						newLine += padWith(tipoImpianto.get("descrSottoTipo"), true, ' ', 30) + ";";

						//Descrizione tecnico - 30 caratteri
						String tecnico = "";
						Operatore op = v.getOperatore();
						if (op!=null)
							tecnico = op.getName().toString();

						newLine += padWith(tecnico, true, ' ', 30) + ";";

						//Imponibile4 - 13.2 caratteri
						String imp4 = "";
						newLine += padWith(imp4, true, '0', 13) + ";";

						//Imponibile10 - 13.2 caratteri
						String imp10 = "";
						newLine += padWith(imp10, true, '0', 13) + ";";

						//Imponibile20 - 13.2 caratteri
						String imp20 = "";
						newLine += padWith(imp20, true, '0', 13) + ";";

						//Esenzione sede addebito
						CodeValuePhi cvEsenzione = se.getEsenzione();
						String esenzioneSede = null;
						if(cvEsenzione != null){
							esenzioneSede = cvEsenzione.getOid();
						}

						//ImponibileFcIva - 13.2 caratteri
						String impFcIva = "";
						if("phidic.arpav.sa.esenzione.05".equals(esenzioneSede)){
							newLine += v.getImporto()!=null?v.getImporto() + ";":"0.0" + ";";
						}else{
							newLine += padWith(impFcIva, true, '0', 13) + ";";
						}

						//ImponibileArt8 - 13.2 caratteri
						String impArt8 = "";
						newLine += padWith(impArt8, true, '0', 13) + ";";

						//H00313293: rimossi flag condominio e codice impegno spesa
						//Flag condominio - 1 carattere
						//						String condominio = "";
						//						newLine += padWith(condominio, true, ' ', 1) + ";";

						//Codice impegno spesa - 20 caratteri
						//						String codImpSpesa = "";
						//						newLine += padWith(codImpSpesa, true, ' ', 20) + ";";

						//Codice conto - 20 caratteri
						String codiceConto = v.getCodiceConto();
						newLine += padWith(codiceConto, true, ' ', 20) + ";";

						if (datailsLines.containsKey(numeroDoc))
							datailsLines.get(numeroDoc).add(newLine);
						else {
							ArrayList<String> lines = new ArrayList<String>();
							lines.add(newLine);

							datailsLines.put(numeroDoc, lines);
						}
					}
				}
			}

			return datailsLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Map<Integer, ArrayList<String>> getInfoLines(Fattura f, List<VerificaImp> verificheList) {
		try {

			//Contiene tutte le linee di informazione associate ad un dato NumeroDoc
			Map<Integer, ArrayList<String>> infoLines = new TreeMap<Integer, ArrayList<String>>();

			//Anno
			String anno = f.getAnno().toString();
			String annoDoc = this.getAnnoFatturazione();

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (VerificaImp v : verificheList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = v.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsente(v);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (v.getNumeroDoc()!=null)
							numeroDoc = v.getNumeroDoc();

						//Tipo rec - V
						String newLine = "V" + ";";

						//Anno - 4 caratteri
						newLine += annoDoc + ";";

						//Numero doc - 6 caratteri
						newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

						//Dipartimento - 2 caratteri
						newLine += padWith(codiceArpav, false, '0', 2) + ";";

						//Matricola - SIGLA/TIPO/MATRICOLA/ANNO - 14 caratteri
						String matricola = getMatricola(v);
						newLine += padWith(matricola, true, ' ', 14) + ";";

						//Data della verifica - 8 caratteri
						String dataVerifica = "";
						Date data = v.getData();
						if (data!=null){
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
							dataVerifica = df.format(data);
						}

						newLine += padWith(dataVerifica, true, ' ', 8) + ";";

						//Sequenza - 3 caratteri (Valore fisso: 001)
						String sequenza = "001";
						newLine += padWith(sequenza, false, '0', 3) + ";";

						//Informazione - 80 caratteri
						String informazione = this.getInformazione(v);
						newLine += padWith(informazione, true, ' ', 80) + ";";

						//Gruppo elab. - 5 caratteri
						String gruppo = "";
						if (f.getGruppo()!=null)
							gruppo = f.getGruppo().toString();

						newLine += padWith(gruppo, true, ' ', 5) + ";";

						//Tipo doc elab - 5 caratteri
						String tipoDocElab = "F";
						newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

						//Anno elab - 4 caratteri
						newLine += padWith(anno, true, ' ', 4) + ";";

						//Tipo appar elab - 1 carattere - Campo tipo fattura?
						String tipoApparElab = "";
						CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
						if (tipologiaFattura!=null){
							tipoApparElab = tipologiaFattura.getCode();
							tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
						}

						newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

						//Dal mese elab - 2 caratteri
						String dalMese = f.getMeseDal()!=null?f.getMeseDal().toString():"";					    	
						newLine += padWith(dalMese, false, '0', 2) + ";";

						//Al mese elab - 2 caratteri
						String alMese = f.getMeseAl()!=null?f.getMeseAl().toString():"";
						newLine += padWith(alMese, false, '0', 2) + ";";

						if (infoLines.containsKey(numeroDoc))
							infoLines.get(numeroDoc).add(newLine);
						else {
							ArrayList<String> lines = new ArrayList<String>();
							lines.add(newLine);

							infoLines.put(numeroDoc, lines);

						}
					}
				}
			}

			return infoLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/*          ADDEBITI          */

	private Map<Integer, String> getHeaderLinesAdd(Fattura f, List<Addebito> addebitiList) {	
		try {

			//Contiene tutte le linee di testata associate ad un dato NumeroDoc
			Map<Integer, String> headerLines = new TreeMap<Integer, String>();

			//Anno
			String anno = f.getAnno()!=null?f.getAnno().toString():"";
			String annoDoc = this.getAnnoFatturazione();

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (Addebito a : addebitiList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsenteAdd(a);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (a.getNumeroDoc()!=null)
							numeroDoc = a.getNumeroDoc();

						//Solo se in headerLines non è già presente la linea di testata relativa al NumeroDoc, ne crea una e la aggiunge
						if (headerLines.isEmpty() || !headerLines.containsKey(numeroDoc)) {

							//T - Testata
							String newLine = "T" + ";";

							//Anno - 4 caratteri
							newLine += annoDoc + ";";

							//Numero doc - 6 caratteri
							newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

							//Dipartimento - 2 caratteri
							newLine += padWith(codiceArpav, false, '0', 2) + ";";

							//Codice - 6 caratteri - non serve
							newLine += "000000" + ";";

							//Sottocodice - 6 caratteri - non serve
							newLine += "000000" + ";";

							//Sottocodice addebito - 6 caratteri - non serve
							newLine += "000000" + ";";

							/* I0070276 
							//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
							SediAddebito sa = this.getSedeAddebitoCpyAdd(a);//v.getSedeAddebito();

							PersoneGiuridiche pg = null;

							String regioneSociale = "";
							String indirizzo = "";				
							String località = "";
							String cap = "";
							String prov = "";

							String codContabilita = "";
							String tel = "";
							String fax = "";

							if (sa!=null){
								regioneSociale = sa.getDenominazione();
								pg = sa.getPersonaGiuridica();
							}

							//Regione sociale - 80 caratteri
							newLine += padWith(regioneSociale, true, ' ', 80) + ";";

							//Dati sede di addebito
							if (sa!=null){
								codContabilita = sa.getCodContabilita();

								if (sa.getAddr()!=null){
									String via = sa.getAddr().getStr();
									String numero = sa.getAddr().getBnr();
									indirizzo = (via!=null?via:"") + " " + numero;

									località = sa.getAddr().getCty();
									cap = sa.getAddr().getZip();
									prov = sa.getAddr().getCpa();
								}

								if (sa.getTelecom()!=null){
									tel = sa.getTelecom().getAs();
									fax = sa.getTelecom().getBad();		
								}
							}*/

							//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
							Sedi se = this.getSedeCpyAdd(a);//v.getSedeAddebito();

							PersoneGiuridiche pg = null;

							String regioneSociale = "";
							String indirizzo = "";				
							String località = "";
							String cap = "";
							String prov = "";

							String codContabilita = "";
							String tel = "";
							String fax = "";

							String cf= "";
							String piva = "";

							if (se!=null){
								regioneSociale = se.getDenominazioneUnitaLocale();
								pg = se.getPersonaGiuridica();
							}

							//Regione sociale - 80 caratteri
							newLine += padWith(regioneSociale, true, ' ', 80) + ";";

							//Dati sede di addebito
							if (se!=null){
								codContabilita = se.getCodContabilita();

								if (se.getAddr()!=null){
									String via = se.getAddr().getStr();
									String numero = se.getAddr().getBnr();
									indirizzo = (via!=null?via:"") + " " + numero;

									località = se.getAddr().getCty();
									cap = se.getAddr().getZip();
									prov = se.getAddr().getCpa();
								}

								if (se.getTelecom()!=null){
									tel = se.getTelecom().getAs();
									fax = se.getTelecom().getBad();		
								}
							}

							//Indirizzo (Via + Numero) - 35 caratteri 				
							newLine += padWith(indirizzo, true, ' ', 35) + ";";				
							//Località - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";	
							//CAP - 5
							newLine += padWith(cap, true, ' ', 5) + ";";
							//Provincia - 3
							newLine += padWith(prov, true, ' ', 3) + ";";

							if (pg != null){
								cf = pg.getCodiceFiscale();
								piva = pg.getPatritaIva();
							}

							//Codice fiscale Persona Giuridica - 16 caratteri
							newLine += padWith(cf, true, ' ', 16) + ";";

							//Partita IVA Persona Giuridica - 11 caratteri
							newLine += padWith(piva, true, ' ', 11) + ";";

							//Codice contabilità - 12 caratteri
							newLine += padWith(codContabilita, true, ' ', 12) + ";";			
							//Tel. (as) - 13
							newLine += padWith(tel, true, ' ', 13) + ";";
							//Fax (bad) - 13
							newLine += padWith(fax, true, ' ', 13) + ";";

							//Data della generazione del flusso - 8 caratteri
							String dataElab = "";
							Date creationDate = f.getCreationDate();
							if (creationDate!=null){
								SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
								dataElab = df.format(creationDate);
							}

							newLine += padWith(dataElab, true, ' ', 8) + ";";

							//Imponibile - 13.2 caratteri - Viene calcolato come somma degli importi di tutte le verifiche associate a questa riga di testata
							String imponibile = "TOTIMPONIBILE";
							newLine += padWith(imponibile, true, '0', 13) + ";";

							//Quota esente IVA - 13.2 caratteri - Da capire come calcolarlo - per il momento passiamo 13 zeri
							//String qeiva = "QUOTESENTEIVA";
							String qeiva = "";
							newLine += padWith(qeiva, true, '0', 13) + ";";

							//Tipo movimento - 1 carattere
							String tipoMovimento = "F";
							newLine += padWith(tipoMovimento, true, ' ', 1) + ";";

							//Gruppo elab. - 5 caratteri
							String gruppo = "";
							if (f.getGruppo()!=null)
								gruppo = f.getGruppo().toString();

							newLine += padWith(gruppo, true, ' ', 5) + ";";

							//Tipo doc elab - 5 caratteri
							String tipoDocElab = "F";
							newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

							//Anno elab - 4 caratteri
							newLine += padWith(anno, true, ' ', 4) + ";";

							//Tipo appar elab - 1 carattere - Campo tipo fattura?
							String tipoApparElab = "";
							CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
							if (tipologiaFattura!=null){
								tipoApparElab = tipologiaFattura.getCode();
								tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
							}

							newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

							//Dal mese elab - 2 caratteri
							String dalMese = f.getMeseDal().toString();					    	
							newLine += padWith(dalMese, false, '0', 2) + ";";

							//Al mese elab - 2 caratteri
							String alMese = f.getMeseAl()!=null?f.getMeseAl().toString():"";					    	
							newLine += padWith(alMese, false, '0', 2) + ";";

							//Comune della sede di addebito (località) - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";

							//Imponibile4 - 13.2 caratteri
							String imp4 = "";
							newLine += padWith(imp4, true, '0', 13) + ";";

							//Imponibile10 - 13.2 caratteri
							String imp10 = "";
							newLine += padWith(imp10, true, '0', 13) + ";";

							//Imponibile20 - 13.2 caratteri
							String imp20 = "";
							newLine += padWith(imp20, true, '0', 13) + ";";

							//ImponibileFcIva - 13.2 caratteri
							String impFcIva = "";
							newLine += padWith(impFcIva, true, '0', 13) + ";";

							//ImponibileArt8 - 13.2 caratteri
							String impArt8 = "";
							newLine += padWith(impArt8, true, '0', 13) + ";";

							//Iva 4% - 13.2
							String iva4 = "";
							newLine += padWith(iva4, true, '0', 13) + ";";

							//Iva 10% - 13.2 caratteri
							String iva10 = "";
							newLine += padWith(iva10, true, '0', 13) + ";";

							//Iva 20% - 13.2 caratteri
							String iva20 = "";
							newLine += padWith(iva20, true, '0', 13) + ";";

							//Bollo - 13.2 caratteri
							String bollo = "";
							newLine += padWith(bollo, true, '0', 13) + ";";

							/* Intestatario della fattura - da mail 28/08: Corrisponde alla sede di addebito
							 * ma la sede addebito è stata sostituita da sede con flag addebito */

							//Intestatario - 35 caratteri
							newLine += padWith(regioneSociale, true, ' ', 35) + ";";

							//Indirizzo intestatario - 35 caratteri
							newLine += padWith(indirizzo, true, ' ', 35) + ";";							

							//Comune intestatario - 30 caratteri
							newLine += padWith(località, true, ' ', 30) + ";";	

							//Provincia intestatario - 2 caratteri
							newLine += padWith(prov, true, ' ', 2) + ";";	

							//CAP intestatario - 5 caratteri
							newLine += padWith(cap, true, ' ', 5) + ";";	

							//Codice fiscale intestatario - 16 caratteri
							newLine += padWith(cf, true, ' ', 16) + ";";	

							//Partita IVA intestatario - 11 caratteri
							newLine += padWith(piva, true, ' ', 11) + ";";	

							//Aggiunge la nuova linea di testata in headerLines
							headerLines.put(numeroDoc, newLine);
						}
					}
				}
			}

			return headerLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Sedi getSedeCpyAdd(Addebito a) {
		try {
			Impianto impCpy = getImpiantoCpyAdd(a);

			if (impCpy!=null)
				return impCpy.getSedi();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Map<Integer, ArrayList<String>> getDetailLinesAdd(Fattura f, List<Addebito> addebitiList) {
		try {

			//Contiene tutte le linee di dettaglio associate ad un dato NumeroDoc
			Map<Integer, ArrayList<String>> datailsLines = new TreeMap<Integer, ArrayList<String>>();

			//Anno
			String anno = f.getAnno()!=null?f.getAnno().toString():"";
			String annoDoc = this.getAnnoFatturazione();


			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (Addebito a : addebitiList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsenteAdd(a);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (a.getNumeroDoc()!=null)
							numeroDoc = a.getNumeroDoc();

						//D - Dettaglio
						String newLine = "D" + ";";

						//Anno - 4 caratteri
						newLine += annoDoc + ";";

						//Numero doc - 6 caratteri
						newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

						//Dipartimento - 2 caratteri
						newLine += padWith(codiceArpav, false, '0', 2) + ";";

						//Codice - 6 caratteri - non serve
						newLine += "000000" + ";";

						//Sottocodice - 6 caratteri - non serve
						newLine += "000000" + ";";

						//Installazione - indirizzo della sede di installazione - 41 caratteri
						String indirizzoSi = "";
						SediInstallazione si = getSedeInstallazioneCpyAdd(a);
						if (si!=null && si.getAddr()!=null)
							indirizzoSi = (si.getAddr().getStr()!=null ? si.getAddr().getStr() + " " : "") +
							(si.getAddr().getBnr()!=null ? si.getAddr().getBnr() + " ": "") +
							(si.getAddr().getCty()!=null ? "- " + si.getAddr().getCty() + " ": "") +
							(si.getAddr().getCpa()!=null ? "(" + si.getAddr().getCpa() + ") ": "");

						newLine += padWith(indirizzoSi, true, ' ', 41) + ";";

						//Matricola - SIGLA/TIPO/MATRICOLA/ANNO - 14 caratteri
						String matricola = getMatricolaAdd(a);
						newLine += padWith(matricola, true, ' ', 14) + ";";

						//Causale
						CodeValuePhi causaleCv = a.getCausale();
						String causale = "";
						String desrCausale = "";

						if (causaleCv!=null){
							//DisplayName
							if (causaleCv.getDisplayName()!=null)
								causale = causaleCv.getDisplayName();

							//Current translation
							if (causaleCv.getCurrentTranslation()!=null){
								String separator = "-";
								String[] splitTransl = causaleCv.getCurrentTranslation().split(separator);
								desrCausale = separator + " " + splitTransl[1];
							}
						}

						//Causale - 2 caratteri 
						newLine += padWith(causale, true, ' ', 2) + ";";

						//Descrizione causale - 30 caratteri 
						newLine += padWith(desrCausale, true, ' ', 30) + ";";

						//Causale addizionale
						CodeValuePhi causaleAddCv = a.getCasualeAdd();
						String causaleAdd = "";
						String desrCausaleAdd = "";

						if (causaleAddCv!=null){
							//DisplayName
							if (causaleAddCv.getDisplayName()!=null)
								causaleAdd = causaleAddCv.getDisplayName();

							//Current translation
							if (causaleAddCv.getCurrentTranslation()!=null)
								desrCausaleAdd = causaleAddCv.getCurrentTranslation();
						}

						//Causale addizionale - 4 caratteri - Solo addebiti
						newLine += padWith(causaleAdd, true, ' ', 4) + ";";

						//Descrizione causale addizionale - 40 caratteri - Solo addebiti
						if ("".equals(desrCausaleAdd) && a.getCausaleAddTxt()!=null)
							desrCausaleAdd = a.getCausaleAddTxt();

						newLine += padWith(desrCausaleAdd, true, ' ', 40) + ";";

						//Importo - lasciarlo in formato Double to String
						newLine += a.getImporto()!=null?a.getImporto() + ";":"0.0" + ";";

						HashMap<String, String> tipoImpianto = getTipoAdd(a);

						//Tipo 
						newLine += tipoImpianto.get("tipo") + ";";

						//Sottotipo 
						newLine += tipoImpianto.get("sottoTipo") + ";";

						//Data della verifica - 8 caratteri
						String dataVerifica = "";
						Date data = a.getData();
						if (data!=null){
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
							dataVerifica = df.format(data);
						}

						newLine += padWith(dataVerifica, true, ' ', 8) + ";";

						//Ore - hh,mm - 5 caratteri
						String hhServizio = (a.getHhServizio()!=null && !"".equals(a.getHhServizio())?a.getHhServizio():"00");
						String mmServizio = (a.getMmServizio()!=null && !"".equals(a.getMmServizio())?a.getMmServizio():"00");

						if (hhServizio.length()==1)
							hhServizio = "0" + hhServizio;

						if (mmServizio.length()==1)
							mmServizio = "0" + mmServizio;

						String ore = hhServizio + "," + mmServizio;
						newLine += padWith(ore, true, ' ', 5) + ";";

						//Gruppo elab. - 5 caratteri
						String gruppo = "";
						if (f.getGruppo()!=null)
							gruppo = f.getGruppo().toString();

						newLine += padWith(gruppo, true, ' ', 5) + ";";

						//Tipo doc elab - 5 caratteri
						String tipoDocElab = "F";
						newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

						//Anno elab - 4 caratteri
						newLine += padWith(anno, true, ' ', 4) + ";";

						//Tipo appar elab - 1 carattere - Campo tipo fattura?
						String tipoApparElab = "";
						CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
						if (tipologiaFattura!=null){
							tipoApparElab = tipologiaFattura.getCode();
							tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
						}

						newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

						//Dal mese elab - 2 caratteri
						String dalMese = f.getMeseDal().toString();					    	
						newLine += padWith(dalMese, false, '0', 2) + ";";

						//Al mese elab - 2 caratteri
						String alMese = f.getMeseAl().toString();
						newLine += padWith(alMese, false, '0', 2) + ";";

						String regioneSociale = "";
						String località = "";

						/* I0070276 
						//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
						SediAddebito sa = this.getSedeAddebitoCpyAdd(a);//v.getSedeAddebito();

						if (sa!=null)
							regioneSociale = sa.getDenominazione();

						//Regione sociale - 80 caratteri
						newLine += padWith(regioneSociale, true, ' ', 80) + ";";

						//Comune - 30 caratteri
						if (sa!=null && sa.getAddr()!=null)
							località = sa.getAddr().getCty();
						 */

						//Recupera dati dalla sede di addebito. In questo caso si usa la copia perchè l'utente potrebbe aver cambiato i dati della sede
						Sedi se = this.getSedeCpyAdd(a);//v.getSedeAddebito();

						// Mail 25/11 inserire la ragione sociale installazione e non addebito
						//						if (se!=null)
						//							regioneSociale = se.getDenominazioneUnitaLocale();
						if(si!=null)
							regioneSociale = si.getDenominazione();


						//Regione sociale - 80 caratteri
						newLine += padWith(regioneSociale, true, ' ', 80) + ";";

						//Comune - 30 caratteri
						//						if (se!=null && se.getAddr()!=null)
						//							località = se.getAddr().getCty();						
						if (si!=null && si.getAddr()!=null)
							località = si.getAddr().getCty();						

						newLine += padWith(località, true, ' ', 30) + ";";	

						//Descrizione sottotipo - 30 caratteri
						newLine += padWith(tipoImpianto.get("descrSottoTipo"), true, ' ', 30) + ";";

						//Descrizione tecnico - 30 caratteri
						String tecnico = "";
						Operatore op = a.getOperatore();
						if (op!=null)
							tecnico = op.getName().toString();

						newLine += padWith(tecnico, true, ' ', 30) + ";";

						//Imponibile4 - 13.2 caratteri
						String imp4 = "";
						newLine += padWith(imp4, true, '0', 13) + ";";

						//Imponibile10 - 13.2 caratteri
						String imp10 = "";
						newLine += padWith(imp10, true, '0', 13) + ";";

						//Imponibile20 - 13.2 caratteri
						String imp20 = "";
						newLine += padWith(imp20, true, '0', 13) + ";";

						//Aliquota IVA
						String aliquota = "";
						CodeValuePhi aliquotaCv = a.getAliquota();
						if(aliquotaCv!=null){
							aliquota = aliquotaCv.getCode();
						}

						//Esenzione sede addebito
						CodeValuePhi cvEsenzione = se.getEsenzione();
						String esenzioneSede = null;
						if(cvEsenzione != null){
							esenzioneSede = cvEsenzione.getOid();
						}

						//ImponibileFcIva - 13.2 caratteri
						String impFcIva = "";
						if("02".equals(aliquota) || "phidic.arpav.sa.esenzione.05".equals(esenzioneSede)){
							newLine += a.getImporto()!=null?a.getImporto() + ";":"0.0" + ";";
						}else{
							newLine += padWith(impFcIva, true, '0', 13) + ";";
						}

						//ImponibileArt8 - 13.2 caratteri
						String impArt8 = "";
						if("03".equals(aliquota)){
							newLine += a.getImporto()!=null?a.getImporto() + ";":"0.0" + ";";
						}else{
							newLine += padWith(impArt8, true, '0', 13) + ";";
						}

						//H00313293: rimossi flag condominio e codice impegno spesa
						//Flag condominio - 1 carattere
						//						String condominio = "";
						//						newLine += padWith(condominio, true, ' ', 1) + ";";

						//Codice impegno spesa - 20 caratteri
						//						String codImpSpesa = "";
						//						newLine += padWith(codImpSpesa, true, ' ', 20) + ";";

						//Codice conto - 20 caratteri
						String codiceConto = a.getCodiceConto();
						newLine += padWith(codiceConto, true, ' ', 20) + ";";

						if (datailsLines.containsKey(numeroDoc))
							datailsLines.get(numeroDoc).add(newLine);
						else {
							ArrayList<String> lines = new ArrayList<String>();
							lines.add(newLine);

							datailsLines.put(numeroDoc, lines);
						}
					}
				}
			}

			return datailsLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Map<Integer, ArrayList<String>> getInfoLinesAdd(Fattura f, List<Addebito> addebitiList) {
		try {

			//Contiene tutte le linee di informazione associate ad un dato NumeroDoc
			Map<Integer, ArrayList<String>> infoLines = new TreeMap<Integer, ArrayList<String>>();

			//Anno
			String anno = f.getAnno().toString();
			String annoDoc = this.getAnnoFatturazione();

			//Codice ARPAV
			String codiceArpav = null;

			II id = f.getServiceDeliveryLocation().getId("HBS");
			if (id!=null)
				codiceArpav= f.getServiceDeliveryLocation().getId("HBS").getExtension();

			for (Addebito a : addebitiList){
				/* Nel processo di fatturazione devono essere inserite solo le verifiche in stato validato (completed)
				 * che non siano esenti fattura (attributo sulla verifica) o che non siano collegate a ditte esenti fattura (attributo sulla ditta) */
				CodeValuePhi status = a.getStatusCode();

				if (status!=null){
					if (status instanceof HibernateProxy)
						status = (CodeValuePhi)((HibernateProxy)status).getHibernateLazyInitializer().getImplementation();

					Boolean esente = isEsenteAdd(a);

					//Se la verifica è in stato validata e non è esente
					if ("completed".equals(status.getCode()) && !esente) {

						//Numero doc
						Integer numeroDoc = 0;
						if (a.getNumeroDoc()!=null)
							numeroDoc = a.getNumeroDoc();

						//Tipo rec - V
						String newLine = "V" + ";";

						//Anno - 4 caratteri
						newLine += annoDoc + ";";

						//Numero doc - 6 caratteri
						newLine += padWith(numeroDoc.toString(), false, '0', 6) + ";";

						//Dipartimento - 2 caratteri
						newLine += padWith(codiceArpav, false, '0', 2) + ";";

						//Matricola - SIGLA/TIPO/MATRICOLA/ANNO - 14 caratteri
						String matricola = getMatricolaAdd(a);
						newLine += padWith(matricola, true, ' ', 14) + ";";

						//Data della verifica - 8 caratteri
						String dataVerifica = "";
						Date data = a.getData();
						if (data!=null){
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
							dataVerifica = df.format(data);
						}

						newLine += padWith(dataVerifica, true, ' ', 8) + ";";

						//Sequenza - 3 caratteri (Valore fisso: 001)
						String sequenza = "001";
						newLine += padWith(sequenza, false, '0', 3) + ";";

						//Informazione - 80 caratteri
						String informazione = this.getInformazioneAdd(a);
						newLine += padWith(informazione, true, ' ', 80) + ";";

						//Gruppo elab. - 5 caratteri
						String gruppo = "";
						if (f.getGruppo()!=null)
							gruppo = f.getGruppo().toString();

						newLine += padWith(gruppo, true, ' ', 5) + ";";

						//Tipo doc elab - 5 caratteri
						String tipoDocElab = "F";
						newLine += padWith(tipoDocElab, true, ' ', 5) + ";";

						//Anno elab - 4 caratteri
						newLine += padWith(anno, true, ' ', 4) + ";";

						//Tipo appar elab - 1 carattere - Campo tipo fattura?
						String tipoApparElab = "";
						CodeValuePhi tipologiaFattura = f.getTipologiaFattura();
						if (tipologiaFattura!=null){
							tipoApparElab = tipologiaFattura.getCode();
							tipoApparElab = tipoApparElab.substring(1, tipoApparElab.length());
						}

						newLine += padWith(tipoApparElab, true, ' ', 1) + ";";

						//Dal mese elab - 2 caratteri
						String dalMese = f.getMeseDal().toString();
						newLine += padWith(dalMese, false, '0', 2) + ";";

						//Al mese elab - 2 caratteri
						String alMese = f.getMeseAl().toString();
						newLine += padWith(alMese, false, '0', 2) + ";";

						if (infoLines.containsKey(numeroDoc))
							infoLines.get(numeroDoc).add(newLine);
						else {
							ArrayList<String> lines = new ArrayList<String>();
							lines.add(newLine);

							infoLines.put(numeroDoc, lines);
						}
					}
				}
			}

			return infoLines;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Nel processo di fatturazione devono essere inserite solo le verifiche che non siano esenti fattura (attributo sulla verifica) 
	 * o che non siano collegate a ditte esenti fattura (attributo sulla sede di addebito) */
	private Boolean isEsente(VerificaImp v){
		try {

			/* Controllo la verifica */
			String esenteV = null;
			CodeValuePhi esenteCvV = v.getEsente();

			if(esenteCvV!=null)
				esenteV = esenteCvV.getCode();

			//La verifica è esente fattura
			if (esenteV!=null && "02".equals(esenteV))
				return true;

			/* Controllo la sede di addebito - escludo solo 04
			 * 01 - Nessuna esenzione
			 * 02 - Esente mora e bollo (o IVA)
			 * 03 - Esente mora
			 * 04 - Esente da emissione fattura
			 * 05 - Esente IVA esportatori abituali
			 * 
			 *  Recupera dati dalla sede di addebito. In questo caso usa la copia perchè l'utente può aver cambiato 
			 *  le informazioni legate alle esenzioni */

			/* I0070276 
			SediAddebito sa = this.getSedeAddebitoCpy(v);//v.getSedeAddebito();
			String esenteSa = null;

			if (sa!=null && sa.getEsenzione()!=null)
				esenteSa = sa.getEsenzione().getCode();
			 */

			Sedi se = this.getSedeCpy(v);//v.getSedeAddebito();
			String esenteSa = null;

			List<String> oidCodeEsente = new ArrayList<String>();
			oidCodeEsente.add("phidic.arpav.sa.esenzione.04");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.06");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.07");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.08");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.09");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.10");

			if (se!=null && se.getEsenzione()!=null)
				esenteSa = se.getEsenzione().getOid();

			if (esenteSa!=null && oidCodeEsente.contains(esenteSa))
				return true;

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private Boolean isEsenteAdd(Addebito a){
		try {

			/* Controllo la verifica */
			String esenteV = null;
			CodeValuePhi esenteCvV = a.getEsente();

			if(esenteCvV!=null)
				esenteV = esenteCvV.getCode();

			//La verifica è esente fattura
			if (esenteV!=null && "02".equals(esenteV))
				return true;

			/* Controllo la sede di addebito - escludo solo 04
			 * 01 - Nessuna esenzione
			 * 02 - Esente mora e bollo (o IVA)
			 * 03 - Esente mora
			 * 04 - Esente da emissione fattura
			 * 05 - Esente IVA esportatori abituali
			 * 
			 *  Recupera dati dalla sede di addebito. In questo caso usa la copia perchè l'utente può aver cambiato 
			 *  le informazioni legate alle esenzioni */

			/* I0070276 
			SediAddebito sa = this.getSedeAddebitoCpyAdd(a);//v.getSedeAddebito();
			String esenteSa = null;

			if (sa!=null && sa.getEsenzione()!=null)
				esenteSa = sa.getEsenzione().getCode();
			 */
			Sedi se = this.getSedeCpyAdd(a);//v.getSedeAddebito();
			String esenteSa = null;

			List<String> oidCodeEsente = new ArrayList<String>();
			oidCodeEsente.add("phidic.arpav.sa.esenzione.04");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.06");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.07");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.08");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.09");
			oidCodeEsente.add("phidic.arpav.sa.esenzione.10");

			if (se!=null && se.getEsenzione()!=null)
				esenteSa = se.getEsenzione().getOid();

			if (esenteSa!=null && oidCodeEsente.contains(esenteSa))
				return true;

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private String padWith(String toPad, boolean padRigth, char padWith, int finalLength) {
		try {

			String ret = "";

			if (toPad==null)
				toPad="";

			toPad = toPad.replaceAll("\r\n|\r|\n", " ");

			int size = toPad.length();

			if (size>finalLength)
				return toPad.substring(0, finalLength);

			if (size==finalLength)
				return toPad;

			String padder = "";
			int n = finalLength - size;

			for (int i=0;i<n;i++)
				padder += padWith;

			if (padRigth)
				ret = toPad + padder;
			else 
				ret = padder + toPad;

			return ret.toUpperCase();

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

	private Impianto getImpiantoCpyAdd(Addebito a) {
		try {
			if (a.getImpPressCpy() != null)
				return a.getImpPressCpy();

			if (a.getImpRiscCpy() != null)
				return a.getImpRiscCpy();

			if (a.getImpMontaCpy() != null)
				return a.getImpMontaCpy();

			if (a.getImpSollCpy() != null)
				return a.getImpSollCpy();

			if (a.getImpTerraCpy() != null)
				return a.getImpTerraCpy();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private SediInstallazione getSedeInstallazioneCpy(VerificaImp v) {
		try {
			Impianto imp = getImpiantoCpy(v);

			if (imp!=null)
				return imp.getSedeInstallazione();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private SediInstallazione getSedeInstallazioneCpyAdd(Addebito a) {
		try {
			Impianto imp = getImpiantoCpyAdd(a);

			if (imp!=null)
				return imp.getSedeInstallazione();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* I0070276
	private SediAddebito getSedeAddebitoCpy(VerificaImp v) {
		try {
			Impianto impCpy = getImpiantoCpy(v);

			if (impCpy!=null)
				return impCpy.getSedeAddebito();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}*/

	/* I0070276 
	private SediAddebito getSedeAddebitoCpyAdd(Addebito a) {
		try {
			Impianto impCpy = getImpiantoCpyAdd(a);

			if (impCpy!=null)
				return impCpy.getSedeAddebito();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}*/

	private IndirizzoSped getIndirizzoSpedCpy(VerificaImp v) {
		try {
			Impianto imp = getImpiantoCpy(v);

			if (imp!=null)
				return imp.getIndirizzoSped();

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private String getMatricola(VerificaImp v) {
		try {
			String matricola = "";
			Impianto imp = getImpiantoCpy(v);

			if (imp!=null){
				String sigla = imp.getSigla();
				matricola += sigla!=null?sigla+"/":"/";

				if (imp.getCode()!=null){
					if("04".equals(imp.getCode().getCode())){
						/* Sollevamento:
						 * 01 - D - Scale aeree ad incl. variabile
						 * 02 - E - Ponti sviluppabili su carro
						 * 03 - F - Ponti sospesi
						 * 04 - G - Argani per ponti sospesi
						 * 05 - H - Idroestrattori
						 * 06 - I - Gru
						 * 07 - L - Argani e paranchi */
						CodeValuePhi subTypeSollCv = imp.getSubTypeSoll();
						if (subTypeSollCv!=null && !"".equals(subTypeSollCv.getCode())){
							String subTypeSoll = subTypeSollCv.getCode();
							if ("01".equals(subTypeSoll))
								matricola += "D/";
							else if ("02".equals(subTypeSoll))
								matricola += "E/";
							else if ("03".equals(subTypeSoll))
								matricola += "F/";
							else if ("04".equals(subTypeSoll))
								matricola += "G/";
							else if ("05".equals(subTypeSoll))
								matricola += "H/";
							else if ("06".equals(subTypeSoll))
								matricola += "I/";
							else if ("07".equals(subTypeSoll))
								matricola += "L/";

						} else 
							matricola += "/";
					} else if("05".equals(imp.getCode().getCode())){
						/* Terra:
						 * 01 - A - Inst. di prot. scariche atm.
						 * 02 - B - Impianti di messa a terra
						 * 03 - C - Ins. elett. In luoghi peric. */

						CodeValuePhi subTypeTerraCv = imp.getSubTypeTerra();
						if (subTypeTerraCv!=null && !"".equals(subTypeTerraCv.getCode())){
							String subTypeTerra = subTypeTerraCv.getCode();
							if ("01".equals(subTypeTerra))
								matricola += "A/";
							else if ("02".equals(subTypeTerra))
								matricola += "B/";
							else if ("03".equals(subTypeTerra))
								matricola += "C/";
						} else 
							matricola += "/";
					}					
				}

				String matr = imp.getMatricola();
				matricola += matr!=null?matr+"/":"/";

				String anno = imp.getAnno();
				matricola += anno!=null?anno:"";
			}

			return matricola;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Diversamente dal caso delle verifiche, la matricola degli addebiti è registrata direttamente nell'addebito */
	private String getMatricolaAdd(Addebito a) {
		try {
			String matr = "";

			if (a==null)
				return "";

			String sigla = a.getSigla();//2
			String matricola = a.getMatricola();//6
			String anno = a.getAnno();//4

			matr += (sigla!=null && !"".equals(sigla)?sigla:"  ") + "/";
			matr += (matricola!=null && !"".equals(matricola)?matricola:"      ") + "/";
			matr += (anno!=null && !"".equals(anno)?anno:"    ");

			return matr;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private HashMap<String, String> getTipo(VerificaImp v) {
		try {
			HashMap<String, String> tipoImpianto = new HashMap<String, String>();

			Impianto imp = getImpiantoCpy(v);

			if (imp!=null && imp.getCode()!=null){

				//Pressione
				if ("01".equals(imp.getCode().getCode())){
					CodeValuePhi subTypePress2Cv = ((ImpPress)imp).getTipoApparecchio();
					String subTypePress2Code = "";
					String subTypePress2 = "";

					if (subTypePress2Cv!=null){
						//if (subTypePress2Cv.getCurrentTranslation().contains(" - "))
						//	subTypePress2 = subTypePress2Cv.getCurrentTranslation().split(" - ")[1];
						//else
						subTypePress2 = subTypePress2Cv.getCurrentTranslation();
						subTypePress2Code = subTypePress2Cv.getCode().replaceFirst("^0", "");
						if(subTypePress2Code.length()==1){
							subTypePress2Code = "P" + subTypePress2Code;
						}
					}

					tipoImpianto.put("tipo", subTypePress2Code);
					tipoImpianto.put("sottoTipo", "  ");

					/* In descrSottoTipo originariamente recuperava la descrizione (senza il codice) del campo tipo apparecchio nell'anagrafica impianti.
					 * Es.: "27 - BRACCIO FIX/GIR SU AUTOC <= 3T"  --> "BRACCIO FIX/GIR SU AUTOC <= 3T" 
					 * 
					 * Mail 28/08 ci chiedono di lasciare anche il codice */

					tipoImpianto.put("descrSottoTipo", subTypePress2);

					//Riscaldamento
				} else if ("02".equals(imp.getCode().getCode())){
					tipoImpianto.put("tipo", "R ");
					tipoImpianto.put("sottoTipo", "  ");

					ImpRisc impRisc = (ImpRisc)imp;

					String descImp = "";
					CodeValuePhi descImpCv = impRisc.getDescrizImpianto();
					if (descImpCv!=null)
						descImp = descImpCv.getCurrentTranslation();

					tipoImpianto.put("descrSottoTipo", descImp);

					//Ascensori e montacarichi
				} else if ("03".equals(imp.getCode().getCode())){
					CodeValuePhi categoriaCv = ((ImpMonta)imp).getCategoria();
					if (categoriaCv!=null && !"".equals(categoriaCv.getCode())){
						String categoria = categoriaCv.getCode();
						/* Categoria:
						 * 01 - A Ascensore adibito al trasporto di persone
						 * 02 - B Ascensore per il trasporto di cose accompagnate da persone
						 * 03 - C Montacarichi per trasporto di cose con cabina destinate alle persone per il solo carico e scarico
						 * 04 - D Montacarichi per trasporto di cose, con cabina non accessibile a persone e di portata superiore a Kg 25
						 * 05 - E Europa */
						if ("01".equals(categoria)){
							tipoImpianto.put("tipo", "KA");
							//tipoImpianto.put("sottoTipo", "A");
						} else if ("02".equals(categoria)){
							tipoImpianto.put("tipo", "KB");
							//tipoImpianto.put("sottoTipo", "B");
						}  else if ("03".equals(categoria)){
							tipoImpianto.put("tipo", "KC");
							//tipoImpianto.put("sottoTipo", "C");
						} else if ("04".equals(categoria)){
							tipoImpianto.put("tipo", "KD");
							//tipoImpianto.put("sottoTipo", "D");
						} else if ("05".equals(categoria)){
							tipoImpianto.put("tipo", "KE");
							//tipoImpianto.put("sottoTipo", "E");
						}
						tipoImpianto.put("sottoTipo", "  ");
						tipoImpianto.put("descrSottoTipo", categoriaCv.getCurrentTranslation());

					}

					//Sollevamento
				} else if("04".equals(imp.getCode().getCode())) {
					/* Sollevamento - Campo "tipo" (Recupera la lettera):
					 * 01 - D - Scale aeree ad incl. variabile 	-> D
					 * 02 - E - Ponti sviluppabili su carro		-> E
					 * 03 - F - Ponti sospesi					-> F
					 * 04 - G - Argani per ponti sospesi		-> G
					 * 05 - H - Idroestrattori					-> H
					 * 06 - I - Gru								-> I
					 * 07 - L - Argani e paranchi 				-> L */
					CodeValuePhi subTypeSollCv = imp.getSubTypeSoll();

					if (subTypeSollCv!=null && !"".equals(subTypeSollCv.getCode())) {
						String subTypeSoll = subTypeSollCv.getCode();
						if ("01".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "D ");
							//tipoImpianto.put("sottoTipo", "D");
						} else if ("02".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "E ");
							//tipoImpianto.put("sottoTipo", "E");
						} else if ("03".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "F ");
							//tipoImpianto.put("sottoTipo", "F");
						} else if ("04".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "G ");
							//tipoImpianto.put("sottoTipo", "G");
						} else if ("05".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "H ");
							//tipoImpianto.put("sottoTipo", "H");
						} else if ("06".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "I ");
							//tipoImpianto.put("sottoTipo", "I");
						} else if ("07".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "L ");
							//tipoImpianto.put("sottoTipo", "L");
						}

						/* In descrSottoTipo originariamente recuperava la descrizione (senza il codice) del campo tipo apparecchio nell'anagrafica impianti.
						 * Es.: "27 - BRACCIO FIX/GIR SU AUTOC <= 3T"  --> "BRACCIO FIX/GIR SU AUTOC <= 3T" 
						 * 
						 * Mail 28/08 ci chiedono di lasciare anche il codice */
						String subTypeSoll2 = "";
						String subTypeSoll2Code = "";
						CodeValuePhi subTypeSoll2Cv = ((ImpSoll)imp).getSubType();

						if (subTypeSoll2Cv!=null){
							//if (subTypeSoll2Cv.getCurrentTranslation().contains(" - "))
							//	subTypeSoll2 = subTypeSoll2Cv.getCurrentTranslation().split(" - ")[1];
							//else
							subTypeSoll2 = subTypeSoll2Cv.getCurrentTranslation();
							subTypeSoll2Code = subTypeSoll2Cv.getCode();
							if(subTypeSoll2Code.startsWith("0")){
								subTypeSoll2Code = subTypeSoll2Code.substring(1)+" ";
							}
						}
						tipoImpianto.put("sottoTipo", subTypeSoll2Code);		
						tipoImpianto.put("descrSottoTipo", subTypeSoll2);
					}

					//Impianti elettrici	
				} else if("05".equals(imp.getCode().getCode())){
					/* Terra:
					 * 01 - A - Inst. di prot. scariche atm.
					 * 02 - B - Impianti di messa a terra
					 * 03 - C - Ins. elett. In luoghi peric. */
					CodeValuePhi subTypeTerraCv = imp.getSubTypeTerra();
					if (subTypeTerraCv!=null && !"".equals(subTypeTerraCv.getCode())){
						String subTypeTerra = subTypeTerraCv.getCode();
						if ("01".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "A ");
							tipoImpianto.put("sottoTipo", "  ");
							tipoImpianto.put("descrSottoTipo", "");
						} else if ("02".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "B ");

							CodeValuePhi subTypeBCv = ((ImpTerra)imp).getSubTypeB();
							if (subTypeBCv!=null && !"".equals(subTypeBCv.getCode())){

								tipoImpianto.put("sottoTipo", String.valueOf(subTypeBCv.getScore()));

								String[] subTypeB = subTypeBCv.getCurrentTranslation().split("-");
								if (subTypeB.length==2){
									//tipoImpianto.put("sottoTipo", subTypeB[0]);
									tipoImpianto.put("descrSottoTipo", subTypeB[1]);
								}
							} else {
								tipoImpianto.put("sottoTipo", "");
								tipoImpianto.put("descrSottoTipo", "");
							}

						} else if ("03".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "C ");
							tipoImpianto.put("sottoTipo", "  ");
							tipoImpianto.put("descrSottoTipo", "");
						}
					}
				}					
			}

			return tipoImpianto;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* Diversamente dal caso delle verifiche, il tipo impianto è registrata direttamente nell'addebito */
	private HashMap<String, String> getTipoAdd(Addebito a) {
		try {
			HashMap<String, String> tipoImpianto = new HashMap<String, String>();

			CodeValuePhi impTypeCv = a.getImpType();

			if (impTypeCv!=null && impTypeCv.getCode()!=null){

				String impType = impTypeCv.getCode();

				//Pressione
				if ("01".equals(impType)){
					tipoImpianto.put("tipo", "P ");
					tipoImpianto.put("sottoTipo", "8 ");
					tipoImpianto.put("descrSottoTipo", "");

					//Riscaldamento
				} else if ("02".equals(impType)){
					tipoImpianto.put("tipo", "R ");
					tipoImpianto.put("sottoTipo", "  ");

					String descImp = "";
					ImpRisc imp = a.getImpRisc();

					CodeValuePhi descImpCv = (imp!=null?imp.getDescrizImpianto():null);
					if (descImpCv!=null)
						descImp = descImpCv.getCurrentTranslation();

					//Ascensori e montacarichi
				} else if ("03".equals(impType)){

					ImpMonta imp = a.getImpMonta();

					CodeValuePhi categoriaCv = (imp!=null?imp.getCategoria():null);

					if (categoriaCv!=null && !"".equals(categoriaCv.getCode())){
						String categoria = categoriaCv.getCode();
						/* Categoria:
						 * 01 - A Ascensore adibito al trasporto di persone
						 * 02 - B Ascensore per il trasporto di cose accompagnate da persone
						 * 03 - C Montacarichi per trasporto di cose con cabina destinate alle persone per il solo carico e scarico
						 * 04 - D Montacarichi per trasporto di cose, con cabina non accessibile a persone e di portata superiore a Kg 25
						 * 05 - E Europa */
						if ("01".equals(categoria)){
							tipoImpianto.put("tipo", "KA");
							tipoImpianto.put("sottoTipo", "A ");
						} else if ("02".equals(categoria)){
							tipoImpianto.put("tipo", "KA");
							tipoImpianto.put("sottoTipo", "B ");
						}  else if ("03".equals(categoria)){
							tipoImpianto.put("tipo", "KB");
							tipoImpianto.put("sottoTipo", "C ");
						} else if ("04".equals(categoria)){
							tipoImpianto.put("tipo", "KB");
							tipoImpianto.put("sottoTipo", "D ");
						} else if ("05".equals(categoria)){
							tipoImpianto.put("tipo", "KB");
							tipoImpianto.put("sottoTipo", "E ");
						}

						tipoImpianto.put("descrSottoTipo", categoriaCv.getCurrentTranslation());

					}

					//Sollevamento
				} else if("04".equals(impType)) {
					/* Sollevamento:
					 * 01 - D - Scale aeree ad incl. variabile
					 * 02 - E - Ponti sviluppabili su carro
					 * 03 - F - Ponti sospesi
					 * 04 - G - Argani per ponti sospesi
					 * 05 - H - Idroestrattori
					 * 06 - I - Gru
					 * 07 - L - Argani e paranchi */
					CodeValuePhi subTypeSollCv = a.getSubTypeSoll();

					if (subTypeSollCv!=null && !"".equals(subTypeSollCv.getCode())) {
						String subTypeSoll = subTypeSollCv.getCode();
						if ("01".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "D ");
							tipoImpianto.put("sottoTipo", "D ");
						} else if ("02".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "E ");
							tipoImpianto.put("sottoTipo", "E ");
						} else if ("03".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "F ");
							tipoImpianto.put("sottoTipo", "F ");
						} else if ("04".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "G ");
							tipoImpianto.put("sottoTipo", "G ");
						} else if ("05".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "H ");
							tipoImpianto.put("sottoTipo", "H ");
						} else if ("06".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "I ");
							tipoImpianto.put("sottoTipo", "I ");
						} else if ("07".equals(subTypeSoll)) {
							tipoImpianto.put("tipo", "L ");
							tipoImpianto.put("sottoTipo", "L ");
						}

						tipoImpianto.put("descrSottoTipo", subTypeSollCv.getCurrentTranslation());
					}

					//Impianti elettrici	
				} else if("05".equals(impType)){
					/* Terra:
					 * 01 - A - Inst. di prot. scariche atm.
					 * 02 - B - Impianti di messa a terra
					 * 03 - C - Ins. elett. In luoghi peric. */
					CodeValuePhi subTypeTerraCv = a.getSubTypeTerra();

					if (subTypeTerraCv!=null && !"".equals(subTypeTerraCv.getCode())){
						String subTypeTerra = subTypeTerraCv.getCode();
						if ("01".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "A ");
							tipoImpianto.put("sottoTipo", "  ");
							tipoImpianto.put("descrSottoTipo", "");
						} else if ("02".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "B ");
							ImpTerra imp = a.getImpTerra();

							CodeValuePhi subTypeBCv = (imp!=null?imp.getSubTypeB():null);
							if (subTypeBCv!=null && !"".equals(subTypeBCv.getCode())){
								String[] subTypeB = subTypeBCv.getCode().split("-");
								if (subTypeB.length==2){
									tipoImpianto.put("sottoTipo", subTypeB[0]);
									tipoImpianto.put("descrSottoTipo", subTypeB[1]);
								}
							}

							tipoImpianto.put("sottoTipo", "  ");
							tipoImpianto.put("descrSottoTipo", "");

						} else if ("03".equals(subTypeTerra)) {
							tipoImpianto.put("tipo", "C ");
							tipoImpianto.put("sottoTipo", "  ");
							tipoImpianto.put("descrSottoTipo", "");
						}
					}
				}					
			}else{
				tipoImpianto.put("tipo", "  ");
				tipoImpianto.put("sottoTipo", "  ");
			}

			return tipoImpianto;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private String getInformazione(VerificaImp v) {
		try {
			String info = "";
			Impianto imp = getImpiantoCpy(v);

			if (imp==null || imp.getCode()==null)
				return info;

			//Pressione
			if ("01".equals(imp.getCode().getCode())){
				ImpPress ip = ((ImpPress)imp);
				//info += "Tipo apparecchio " + (ip.getTipoApparecchio()!=null?ip.getTipoApparecchio().getCurrentTranslation()+" ":" ");

				info += "Pressione " + (ip.getPressBar1()!=null?ip.getPressBar1():"") + " Pressione max " + 
						(ip.getPressBar2()!=null?ip.getPressBar2():"") + " Capacità " + (ip.getCapacita()!=null?ip.getCapacita():"");

				//Riscaldamento
			} else if ("02".equals(imp.getCode().getCode())){
				ImpRisc ir = ((ImpRisc)imp);
				ImpRiscAction ira = ImpRiscAction.instance();
				Double potGlb = ira.getPot("glob", ir);

				info += "Potenza globale in Kw" + (potGlb!=null?potGlb:"");

				//Ascensori e montacarichi
			} else if ("03".equals(imp.getCode().getCode())){
				ImpMonta im = ((ImpMonta)imp);

				info += "Fermate " + (im.getFermate()!=null?im.getFermate():"") + 
						((im.getMatrcomune()!=null && !im.getMatrcomune().isEmpty())?" Matricola comune " + im.getMatrcomune():"");

				//Sollevamento
			} else if("04".equals(imp.getCode().getCode())) {
				ImpSoll is = ((ImpSoll)imp);

				info += "Portata " + (is.getPortata()!=null?is.getPortata():"");
				/*info += "Portata " + (is.getPortata()!=null?is.getPortata():"" + " Sottotipo ");

				CodeValuePhi subTypeSollCv = imp.getSubTypeSoll();
				if (subTypeSollCv!=null) 
					info += subTypeSollCv.getCurrentTranslation();*/

				//Impianti elettrici	
			} else if("05".equals(imp.getCode().getCode())){
				ImpTerra it = ((ImpTerra)imp);

				CodeValuePhi subTypeTerraCv = it.getSubTypeTerra();
				String subTypeTerraCode = "";
				if(subTypeTerraCv!=null){
					subTypeTerraCode = subTypeTerraCv.getCode();
				}

				if("01".equals(subTypeTerraCode)){
					CodeValuePhi tipologiaCv = it.getTipologia();
					String tipologiaCode = "";
					if(tipologiaCv!=null){
						tipologiaCode = tipologiaCv.getCode();
					}

					if("41".equals(tipologiaCode)){

						info += "Parafulmini ad asta " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") +
								" Aste " + (it.getAste()!=null?it.getAste():"");

					}else if("42".equals(tipologiaCode)){

						ImpTerraAction ia = ImpTerraAction.instance();
						info += "Parafulmini a gabbia " + "Superficie totale " + ia.getSupTot(it.getSuperf01(), it.getSuperf02(), it.getSuperf03());

					}else if("43".equals(tipologiaCode)){

						info += "Strutt. metalliche fuori terra " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Raggruppamenti " + (it.getRaggruppati01()!=null?it.getRaggruppati01():"");

					}else if("44".equals(tipologiaCode)){

						info += "Recipenti, app. met. fuori terra " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Raggruppamenti " + (it.getRaggruppati02()!=null?it.getRaggruppati02():"");

					}else if("45".equals(tipologiaCode)){
						info += "Complessi costit. unica strutt." + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"");

					}else if("46".equals(tipologiaCode)){

						info += "Serbatoi metallici interrati " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Disperdenti " + (it.getDisperdenti()!=null?it.getDisperdenti():"");

					}else if("47".equals(tipologiaCode)){
						info += "Str. qualsiasi di cant. edili " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"");

					}else if("48".equals(tipologiaCode)){
						info += "Strutture autoprotette " + (it.getStruttAutopNum()!=null?it.getStruttAutopNum():"");
					}
				}else if("02".equals(subTypeTerraCode)){
					info += ((it.getCabineCode()!=null && "YES".equals(it.getCabineCode().getCode()))?"Cabine " + it.getCabineNum() + " ":"") + 
							"Potenza totale in Kw " + (it.getPot()!=null?it.getPot()+" ":"") + 
							/*" Dispersori " + (it.getDispersori()!=null?it.getDispersori():"") +*/ 
							((it.getImpAutoprod()!=null && "YES".equals(it.getImpAutoprod().getCode()))?"Autoproduzione " + it.getImpAutoprod().getCurrentTranslation():"");

				}else if("03".equals(subTypeTerraCode)){}
			}					

			return info;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private String getInformazioneAdd(Addebito a) {
		try {
			String info = "";
			Impianto imp = getImpiantoCpyAdd(a);

			if (imp==null || imp.getCode()==null)
				return info;

			//Pressione
			if ("01".equals(imp.getCode().getCode())){
				ImpPress ip = ((ImpPress)imp);
				//info += "Tipo apparecchio " + (ip.getTipoApparecchio()!=null?ip.getTipoApparecchio().getCurrentTranslation()+" ":" ");

				info += "Pressione " + (ip.getPressBar1()!=null?ip.getPressBar1():"") + " Pressione max " + 
						(ip.getPressBar2()!=null?ip.getPressBar2():"") + " Capacità " + (ip.getCapacita()!=null?ip.getCapacita():"");

				//Riscaldamento
			} else if ("02".equals(imp.getCode().getCode())){
				ImpRisc ir = ((ImpRisc)imp);
				ImpRiscAction ira = ImpRiscAction.instance();
				Double potGlb = ira.getPot("glob", ir);

				info += "Potenza globale " + (potGlb!=null?potGlb:"");

				//Ascensori e montacarichi
			} else if ("03".equals(imp.getCode().getCode())){
				ImpMonta im = ((ImpMonta)imp);

				info += "Fermate " + (im.getFermate()!=null?im.getFermate():"") + 
						((im.getMatrcomune()!=null && !im.getMatrcomune().isEmpty())?" Matricola comune " + im.getMatrcomune():"");

				//Sollevamento
			} else if("04".equals(imp.getCode().getCode())) {
				ImpSoll is = ((ImpSoll)imp);

				info += "Portata " + (is.getPortata()!=null?is.getPortata():"");
				/*info += "Portata " + (is.getPortata()!=null?is.getPortata():"" + " Sottotipo ");

				CodeValuePhi subTypeSollCv = imp.getSubTypeSoll();
				if (subTypeSollCv!=null) 
					info += subTypeSollCv.getCurrentTranslation();*/

				//Impianti elettrici	
			} else if("05".equals(imp.getCode().getCode())){
				ImpTerra it = ((ImpTerra)imp);

				CodeValuePhi subTypeTerraCv = it.getSubTypeTerra();
				String subTypeTerraCode = "";
				if(subTypeTerraCv!=null){
					subTypeTerraCode = subTypeTerraCv.getCode();
				}

				if("01".equals(subTypeTerraCode)){
					CodeValuePhi tipologiaCv = it.getTipologia();
					String tipologiaCode = "";
					if(tipologiaCv!=null){
						tipologiaCode = tipologiaCv.getCode();
					}

					if("41".equals(tipologiaCode)){

						info += "Parafulmini ad asta " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") +
								" Aste " + (it.getAste()!=null?it.getAste():"");

					}else if("42".equals(tipologiaCode)){

						ImpTerraAction ia = ImpTerraAction.instance();
						info += "Parafulmini a gabbia " + "Superficie totale " + ia.getSupTot(it.getSuperf01(), it.getSuperf02(), it.getSuperf03());

					}else if("43".equals(tipologiaCode)){

						info += "Strutt. metalliche fuori terra " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Raggruppamenti " + (it.getRaggruppati01()!=null?it.getRaggruppati01():"");

					}else if("44".equals(tipologiaCode)){

						info += "Recipenti, app. met. fuori terra " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Raggruppamenti " + (it.getRaggruppati02()!=null?it.getRaggruppati02():"");

					}else if("45".equals(tipologiaCode)){
						info += "Complessi costit. unica strutt." + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"");

					}else if("46".equals(tipologiaCode)){

						info += "Serbatoi metallici interrati " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"") + 
								" Disperdenti " + (it.getDisperdenti()!=null?it.getDisperdenti():"");

					}else if("47".equals(tipologiaCode)){
						info += "Str. qualsiasi di cant. edili " + (it.getTipologiaTesto()!=null?it.getTipologiaTesto():"");

					}else if("48".equals(tipologiaCode)){
						info += "Strutture autoprotette " + (it.getStruttAutopNum()!=null?it.getStruttAutopNum():"");
					}
				}else if("02".equals(subTypeTerraCode)){
					info += ((it.getCabineCode()!=null && "YES".equals(it.getCabineCode().getCode()))?"Cabine " + it.getCabineNum() + " ":"") + 
							"Potenza totale in Kw " + (it.getPot()!=null?it.getPot()+" ":"") + 
							/*" Dispersori " + (it.getDispersori()!=null?it.getDispersori():"") +*/ 
							((it.getImpAutoprod()!=null && "YES".equals(it.getImpAutoprod().getCode()))?"Autoproduzione " + it.getImpAutoprod().getCurrentTranslation():"");

				}else if("03".equals(subTypeTerraCode)){}
			}					

			return info;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public String getImporti(Fattura f, String toGet) {
		try {
			if (f==null || toGet==null || "".equals("toGet"))
				return "";

			Double imponibile = f.getImponibile();

			if (imponibile==null)
				return "";

			if ("imponibile".equals(toGet))
				return new BigDecimal(imponibile).setScale(2 , BigDecimal.ROUND_HALF_UP) + " €"; 			

			//Imposta 21%
			Double esenteIva = f.getEsenteIva();
			if(esenteIva==null)
				esenteIva = 0.0;
			
			Double imposta = (imponibile-esenteIva)*21/100;

			if ("imposta".equals(toGet))
				return new BigDecimal(imposta).setScale(2 , BigDecimal.ROUND_HALF_UP) + " €";

			if ("totale".equals(toGet)){
				Double totale = imponibile + imposta;

				return new BigDecimal(totale).setScale(2 , BigDecimal.ROUND_HALF_UP) + " €";
			}			

			return "";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private String getRigaDitteVer(Fattura f, VerificaImp v, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";


		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}
		newLine += padWith( (pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";


		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((se.getDenominazioneUnitaLocale()!=null ? se.getDenominazioneUnitaLocale() : ""), true, ' ', 120) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 120) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = v.getNumeroDoc();
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + ";";

		//COD_CONTABIL
		String codCont = se.getCodContabilita();
		newLine += padWith(((codCont!=null && !("".equals(codCont))) ? codCont : ""), true, ' ', 30) + "\n";

		return newLine;
	}

	/* I0070276
	private String getRigaCondVer(Fattura f, VerificaImp v, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocVerifica(f, v);
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 150) + "\n";

		return newLine;
	}*/

	/* I0070276
	private String getRigaEntiVer(Fattura f, VerificaImp v, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}

		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((sa.getDenominazione()!=null ? sa.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocVerifica(f, v);
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 150) + "\n";

		return newLine;
	}*/

	/* I0070276
	private String getRigaDitteAdd(Fattura f, Addebito a, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);
		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((sa.getDenominazione()!=null ? sa.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocAddebito(f, a);
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "") + ";";

		newLine += padWith(toPad, true, ' ', 150);

		//COD_CONTABIL
		String codCont = sa.getCodContabilita();
		newLine += padWith(((codCont!=null && !("".equals(codCont))) ? codCont : ""), true, ' ', 30) + "\n";

		return newLine;
	}*/

	/* I0070276
	private String getRigaCondAdd(Fattura f, Addebito a, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocAddebito(f, a);
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 150) + "\n";

		return newLine;
	}*/

	/* I0070276
	private String getRigaEntiAdd(Fattura f, Addebito a, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);

		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((sa.getDenominazione()!=null ? sa.getDenominazione() : ""), true, ' ', 30) + ";";


		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocAddebito(f, a);
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 150) + "\n";

		return newLine;
	}*/

	private String getPIvaOrCfFromPersonaGiuridica(PersoneGiuridiche pg){
		String out = pg.getPatritaIva();
		if(out==null || "".equals(out)){
			out = (pg.getCodiceFiscale()!=null ? pg.getCodiceFiscale() : "");
		}
		return out;
	}

	/* I0070276
	private String getRigaDitteVer(Fattura f, VerificaImp v, SediAddebito sa) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";


		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(sa != null){
			pg = sa.getPersonaGiuridica();
		}
		newLine += padWith( (pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";


		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((sa.getDenominazione()!=null ? sa.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((sa.getAddr())!=null ?  sa.getAddr().getStr() + " " + sa.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((sa.getAddr())!=null ? sa.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		Integer anno = f.getAnno();
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = numeroDocVerifica(f, v);
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		//NOTE_ADDEBITO
		String cig = (sa.getCig()!=null ? sa.getCig().trim() : "");
		TEL tel = sa.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			mail = (tel.getMc()!=null ? tel.getMc().trim() : "");
			pec = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (sa.getNote()!=null ? sa.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "") + ";";

		newLine += padWith(toPad, true, ' ', 150);

		//COD_CONTABIL
		String codCont = sa.getCodContabilita();
		newLine += padWith(((codCont!=null && !("".equals(codCont))) ? codCont : ""), true, ' ', 30) + "\n";

		return newLine;
	}*/

	private String getRigaCondVer(Fattura f, VerificaImp v, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = v.getNumeroDoc();
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + "\n";

		return newLine;
	}

	private String getRigaEntiVer(Fattura f, VerificaImp v, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpy(v);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}

		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((se.getDenominazioneUnitaLocale()!=null ? se.getDenominazioneUnitaLocale() : ""), true, ' ', 120) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 120) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = v.getNumeroDoc();
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + "\n";

		return newLine;
	}

	private String getRigaDitteAdd(Fattura f, Addebito a, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);
		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((se.getDenominazioneUnitaLocale()!=null ? se.getDenominazioneUnitaLocale() : ""), true, ' ', 120) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 120) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = a.getNumeroDoc();
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + ";";

		//COD_CONTABIL
		String codCont = se.getCodContabilita();
		newLine += padWith(((codCont!=null && !("".equals(codCont))) ? codCont : ""), true, ' ', 30) + "\n";

		return newLine;
	}

	private String getRigaCondAdd(Fattura f, Addebito a, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);
		String newLine = padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//INDIRIZZO_INSTALLAZIONE
		newLine += padWith(((si.getAddr())!=null ?  si.getAddr().getStr() + " " + si.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = a.getNumeroDoc();
		} catch (Exception e) {
		}
		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + "\n";

		return newLine;
	}

	private String getRigaEntiAdd(Fattura f, Addebito a, Sedi se) {

		//RAGIONE_SOCIALE_INSTALLAZIONE
		SediInstallazione si = getSedeInstallazioneCpyAdd(a);

		String newLine =  padWith((si.getDenominazione()!=null ? si.getDenominazione() : ""), true, ' ', 30) + ";";

		//DESCRIZIONE_LOCALITA_INST
		newLine += padWith(((si.getAddr())!=null ? si.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//CODICE_FISCALE
		PersoneGiuridiche pg = null;
		if(se != null){
			pg = se.getPersonaGiuridica();
		}
		newLine += padWith((pg != null ? getPIvaOrCfFromPersonaGiuridica(pg) : ""), true, ' ', 30) + ";";

		//RAGIONE_SOCIALE_ADDEBITO
		newLine += padWith((se.getDenominazioneUnitaLocale()!=null ? se.getDenominazioneUnitaLocale() : ""), true, ' ', 120) + ";";


		//INDIRIZZO_ADDEBITO
		newLine += padWith(((se.getAddr())!=null ?  se.getAddr().getStr() + " " + se.getAddr().getBnr() : ""), true, ' ', 120) + ";";

		//DESCRIZIONE_LOCALITA_ADD
		newLine += padWith(((se.getAddr())!=null ? se.getAddr().getCty() : ""), true, ' ', 30) + ";";

		//ANNO
		//Integer anno = f.getAnno();
		Integer anno = Integer.valueOf(this.getAnnoFatturazione());
		newLine += padWith((anno != null ? anno.toString() : ""), true, ' ', 10) + ";";

		//NUMERO
		Integer n = null;
		try {
			n = a.getNumeroDoc();
		} catch (Exception e) {
		}

		newLine += padWith((n != null ? n.toString() : ""), true, ' ', 10) + ";";

		// CODICE UNIVOCO
		String codUniv = se.getCodiceUnivoco();
		newLine += padWith(((codUniv!=null && !("".equals(codUniv))) ? codUniv : ""), true, ' ', 15) + ";";

		//NOTE_ADDEBITO
		String cig = (se.getCig()!=null ? se.getCig().trim() : "");
		TEL tel = se.getTelecom();
		String mail = "";
		String pec = "";
		if(tel != null){
			pec = (tel.getMc()!=null ? tel.getMc().trim() : "");
			mail = (tel.getMail()!=null ? tel.getMail().trim() : "");
		}
		String note = (se.getNote()!=null ? se.getNote().trim() : "");

		String toPad = ((cig!=null && !("".equals(cig))) ? "CIG: " + cig + " " : "") + 
				(!("".equals(mail)) ? "E-MAIL: " + mail + " " : "") +
				(!("".equals(pec)) ? "PEC: " + pec + " " : "") + 
				((note!=null && !("".equals(note))) ? note : "");

		newLine += padWith(toPad, true, ' ', 600) + "\n";

		return newLine;
	}

	private String getAnnoFatturazione(){
		String out = "";

		Date today = new Date();
		Calendar cal = Calendar.getInstance();

		return String.valueOf(cal.get(Calendar.YEAR));

	}

	/* Calcola o crea un progressivo per anno e sede ARPAV */ 
	private Integer numeroDocUnificato(Fattura f, VerificaImp v, Addebito a) {
		try {

			String annoStr = this.getAnnoFatturazione();

			if (annoStr==null || annoStr.length()!=4 || Integer.parseInt(annoStr)<1)
				return null;

			String anno = annoStr.substring(2);//2018 -> 18

			int gruppoStart = Integer.parseInt(anno + "001");// 18 -> 18001
			if (gruppoStart<1)
				return null;

			ServiceDeliveryLocation arpav = f.getServiceDeliveryLocation();
			if (arpav==null)
				return null;

			String evaluateVerifiche = "SELECT ver.numeroDoc FROM VerificaImp ver " + 
					"LEFT JOIN ver.serviceDeliveryLocation arpav " +
					"LEFT JOIN ver.fattura fattura " +
					"WHERE ver.isActive = true AND ver.statusCode.code = 'completed' " +
					"AND arpav.internalId = :arpavId AND fattura.isActive = true AND fattura.gruppo like '" + anno + "%' order by ver.numeroDoc desc";

			HashMap<String, Object> parametersVer = new HashMap<String, Object>(1);
			parametersVer.put("arpavId", arpav.getInternalId());

			@SuppressWarnings("unchecked")
			List<Integer> numListVer = (List<Integer>) ca.executeHQLwithParameters(evaluateVerifiche, parametersVer);

			String evaluateAddebiti = "SELECT a.numeroDoc FROM Addebito a " + 
					"LEFT JOIN a.serviceDeliveryLocation arpav " +
					"LEFT JOIN a.fattura fattura " +
					"WHERE a.isActive = true AND a.statusCode.code = 'completed' " +
					"AND arpav.internalId = :arpavId AND fattura.isActive = true AND fattura.gruppo like '" + anno + "%' order by a.numeroDoc desc";

			HashMap<String, Object> parametersAdd = new HashMap<String, Object>(1);
			parametersAdd.put("arpavId", arpav.getInternalId());

			@SuppressWarnings("unchecked")
			List<Integer> numListAdd = (List<Integer>) ca.executeHQLwithParameters(evaluateAddebiti, parametersAdd);

			Integer numDoc = 0;

			//Non sono presenti verifiche
			if((numListVer==null || numListVer.isEmpty()) && (numListAdd==null || numListAdd.isEmpty())){
				numDoc = 1;
			}
			else{
				Integer numDocVer = -1;
				Integer numDocAdd = -1;

				if(numListVer!=null && !numListVer.isEmpty()){
					numDocVer = numListVer.get(0) + 1;
				}

				if(numListAdd!=null && ! numListAdd.isEmpty()){
					numDocAdd = numListAdd.get(0) + 1;
				}

				if(numDocAdd > numDocVer){
					numDoc = numDocAdd;
				} else if(numDocVer > numDocAdd){
					numDoc = numDocVer;
				}
			}

			return numDoc;	

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/** Pulizia Fatture:
	 *  update verifica_imp set statusCode='phidic.arpav.ver.stato.verified_V0' where statusCode='phidic.arpav.ver.stato.completed_V0';
	 *  update verifica_imp set statusCode='phidic.arpav.ver.stato.verified_V0' where fattura_id is not null;
	 *  update verifica_imp set fattura_id =null where fattura_id is not null;
	 *  update verifica_imp set numero_doc=null where numero_doc is not null;
	 *  
	 *  update addebito set statusCode='phidic.arpav.ver.stato.verified_V0' where fattura_id is not null;
	 *  update addebito set fattura_id =null where fattura_id is not null;
	 *  update addebito set statusCode='phidic.arpav.ver.stato.verified_V0' where statusCode='phidic.arpav.ver.stato.completed_V0';
	 *  update addebito set numero_doc=null where numero_doc is not null;
	 *  
	 *  delete from file_acc_ditte;
	 *  delete from file_acc_cond;
	 *  delete from file_acc_enti;
	 *  delete from fattura; */

}