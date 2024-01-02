package com.phi.entities.actions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.CessioneImp;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.SchedaGeneratori;
import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;
import com.phi.entities.baseEntity.SchedaRecipientiIndiv;
import com.phi.entities.baseEntity.SchedaTubazioniIndiv;
import com.phi.entities.baseEntity.SchedaVasi;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.events.PhiEvent;
import com.phi.parameters.ParameterManager;

@BypassInterceptors
@Name("ImpiantoAction")
@Scope(ScopeType.CONVERSATION)
public class ImpiantoAction extends BaseAction<Impianto, Long> {

	private static final long serialVersionUID = 510813406L;
	private static final Logger log = Logger.getLogger(ImpiantoAction.class);


	public static ImpiantoAction instance() {
		return (ImpiantoAction) Component.getInstance(ImpiantoAction.class, ScopeType.CONVERSATION);
	}

	public void inject(Object baseEntityOrMap) {
		Context conversation = Contexts.getConversationContext();

		if (baseEntityOrMap instanceof BaseEntity)
			super.inject(baseEntityOrMap);

		else if (baseEntityOrMap instanceof Map) {

			Map map 			= (Map)baseEntityOrMap;
			CodeValuePhi code	= (CodeValuePhi)map.get("code");

			Long internalId = (Long)map.get("internalId");
			String type     = code.getCode();

			if (type!=null){
				if("01".equals(type))
					entity = ca.load((new ImpPress()).getClass(), internalId);
				else if("02".equals(type))
					entity = ca.load((new ImpRisc()).getClass(), internalId);
				else if("03".equals(type))
					entity = ca.load((new ImpMonta()).getClass(), internalId);
				else if("04".equals(type))
					entity = ca.load((new ImpSoll()).getClass(), internalId);
				else if("05".equals(type))
					entity = ca.load((new ImpTerra()).getClass(), internalId);
			}

			Contexts.getEventContext().remove(getConversationName());
			conversation.set(conversationName, entity);
		} 
	}

	public String printAddr(Map addr) {
		try {
			String ret = "";

			if (addr==null)
				return ret;

			String str = addr.get("str")!=null?addr.get("str").toString():null;
			String bnr = addr.get("brn")!=null?addr.get("bnr").toString():null;
			String cty = addr.get("cty")!=null?addr.get("cty").toString():null;
			String cpa = addr.get("cpa")!=null?addr.get("cpa").toString():null;

			if (str != null && !str.isEmpty()) 
				ret += str + " ";

			if (bnr != null && !bnr.isEmpty())
				ret += bnr + ", ";

			if (cty != null && !cty.isEmpty())
				ret += cty + " ";

			if (cpa != null && !cpa.isEmpty())
				ret += "(" + cpa + ") ";

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean isSedeLinked(Sedi sede){

		if(sede==null)
			return false;

		Impianto imp = this.getEntity();
		if(imp==null){
			String type = this.getSelectedType();

			if("01".equals(type)){
				imp = ImpPressAction.instance().getEntity();
			}else if("02".equals(type)){
				imp = ImpRiscAction.instance().getEntity();
			}else if("03".equals(type)){
				imp = ImpMontaAction.instance().getEntity();
			}else if("04".equals(type)){
				imp = ImpSollAction.instance().getEntity();
			}else if("05".equals(type)){
				imp = ImpTerraAction.instance().getEntity();
			}else{
				return false;
			}
		}

		if(imp.getSedi()!=null){
			if((imp.getSedi()).equals(sede)){
				return true;			
			}else{
				return false;	
			}
		}

		Context conversationContext = Contexts.getConversationContext();
		List<Sedi> sediAddList = (List<Sedi>) ((IdataModel<Sedi>) conversationContext.get("SediList")).getFullList();
		if(sediAddList!=null){
			if(sediAddList.size()==1 && sede.equals(sediAddList.get(0))){
				return true;
			}
		}	

		return false;
	}

	public boolean isIndirizzoSpedLinked(IndirizzoSped is){

		if(is==null)
			return false;

		Impianto imp = this.getEntity();
		if(imp==null){
			String type = this.getSelectedType();

			if("01".equals(type)){
				imp = ImpPressAction.instance().getEntity();
			}else if("02".equals(type)){
				imp = ImpRiscAction.instance().getEntity();
			}else if("03".equals(type)){
				imp = ImpMontaAction.instance().getEntity();
			}else if("04".equals(type)){
				imp = ImpSollAction.instance().getEntity();
			}else if("05".equals(type)){
				imp = ImpTerraAction.instance().getEntity();
			}else{
				return false;
			}
		}

		if(imp.getIndirizzoSped()!=null){
			if((imp.getIndirizzoSped()).equals(is)){
				return true;			
			}else{
				return false;	
			}
		}

		return false;
	}

	public boolean isSaved(){
		boolean saved = false;

		String type = this.getSelectedType();

		if("01".equals(type)){
			ImpPress impPress = ImpPressAction.instance().getEntity();
			if(impPress==null)
				return saved;

			if(impPress.getInternalId() > 0){
				saved = true;
			}
		}else if("02".equals(type)){
			ImpRisc impRisc = ImpRiscAction.instance().getEntity();
			if(impRisc==null)
				return saved;

			if(impRisc.getInternalId() > 0){
				saved = true;
			}
		}else if("03".equals(type)){
			ImpMonta impMonta = ImpMontaAction.instance().getEntity();
			if(impMonta==null)
				return saved;

			if(impMonta.getInternalId() > 0){
				saved = true;
			}
		}else if("04".equals(type)){
			ImpSoll impSoll = ImpSollAction.instance().getEntity();
			if(impSoll==null)
				return saved;

			if(impSoll.getInternalId() > 0){
				saved = true;
			}
		}else if("05".equals(type)){
			ImpTerra impTerra = ImpTerraAction.instance().getEntity();
			if(impTerra==null)
				return saved;

			if(impTerra.getInternalId() > 0){
				saved = true;
			}
		}

		return saved;
	}

	public void toggleIsNewAll(PhiDataModel<Impianto> phiList){

		Boolean selAll = (Boolean) (temporary.get("selectAll")==null ? false : temporary.get("selectAll"));

		if(selAll == false){
			selAll = true;
		}else{
			selAll = false;
		}
		temporary.put("selectAll", selAll);

		List<Impianto> impList = phiList.getList();

		for(Impianto imp : impList){
			imp.setIsNew(selAll);
		}

	}

	public Date getDataAcquisizione(Impianto imp, PersoneGiuridiche pg){

		if(imp == null || pg == null)
			return null;

		if(imp.getInternalId() <=0 || pg.getInternalId() <=0)
			return null;

		String q = "SELECT MAX(c.dataCessione) FROM CessioneImp c WHERE c.isActive = 1 AND ";
		//select max(data_cessione) from cessione_imp where imp_terra_id=81 and persona_giuridica_id=352;

		String code = imp.getCode().getCode();
		if("01".equals(code)){
			q += "c.impPress = ";
		}else if("02".equals(code)){
			q += "c.impRisc = ";
		}else if("03".equals(code)){
			q += "c.impMonta = ";
		}else if("04".equals(code)){
			q += "c.impSoll = ";
		}else if("05".equals(code)){
			q += "c.impTerra = ";
		}

		q += ":imp AND c.personaGiuridica = :pg";
		Query qDate = ca.createQuery(q);
		qDate.setParameter("imp", imp);
		qDate.setParameter("pg", pg);

		Date dateAcq = null;
		try {
			dateAcq = (Date) qDate.getSingleResult();
		} catch (NoResultException e) {
			//dateAcq = null;
		}

		return dateAcq;
	}

	public void altRead() throws PersistenceException{

		Object[] mainFields = {like.get("sigla"),
				like.get("matricola"),
				like.get("anno")};

		boolean anyMain = false;
		int iMain=0;
		while(iMain<mainFields.length && !anyMain){
			if(mainFields[iMain]!=null)
				anyMain=true;
			iMain++;
		}

		Set<Entry<String, Object>> otherLikeEntries = like.entrySet();
		Iterator<Map.Entry<String, Object>>	likeIterator = otherLikeEntries.iterator();
		String likeQ = "";

		while(likeIterator.hasNext()){
			Map.Entry<String, Object> currentEntry = likeIterator.next();
			String likeK = currentEntry.getKey();
			Object likeV = currentEntry.getValue();

			if(likeK.equals("sigla") || likeK.equals("matricola") || likeK.equals("anno")){
				continue;
			}
			String likeStr = "";
			if(likeV != null)
				likeStr = (String) likeV;
			if(!(likeStr.equals(""))){
				likeQ += " AND i." + likeK + " LIKE '%" + likeStr + "%'";
			}
		}

		Set<Entry<String, Object>> eqEntries = equal.entrySet();
		Iterator<Map.Entry<String, Object>>	eqIterator = eqEntries.iterator();
		String eqQ = "";

		while(eqIterator.hasNext()){
			Map.Entry<String, Object> currentEntry = eqIterator.next();
			String eqK = currentEntry.getKey();
			Object eqV = currentEntry.getValue();

			if(eqK.equals("isActive") || eqK.equals("code.id")){
				continue;
			}
			String eqStr = "";
			if(eqV != null && eqV instanceof String)
				eqStr = (String) eqV;
			if(eqV != null && eqV instanceof CodeValuePhi)
				eqStr = ((CodeValuePhi) eqV).getId();
			if(eqV != null && eqV instanceof CodeValueCity)
				eqStr = ((CodeValueCity) eqV).getId();
			if(!(eqStr.equals(""))){
				eqQ += " AND i." + eqK + " = '" + eqStr + "'";
			}
		}

		String q = "SELECT i FROM Impianto i WHERE i.isActive = 1 AND i.copy != 1 ";

		if(anyMain){

			q += "AND (";

			if(like.get("sigla")!=null){
				q += "i.sigla LIKE '%" + like.get("sigla") + "%' ";
			}

			if(like.get("matricola")!=null){
				String op = "";
				if(like.get("sigla")!=null){
					op = "OR";
				}
				q += op + " i.matricola LIKE '%" + like.get("matricola") + "%' ";
			}

			if(like.get("anno")!=null){
				String op = "";
				if(like.get("sigla")!=null || like.get("matricola")!=null){
					op = "OR";
				}
				q += op + " i.anno LIKE '%" + like.get("anno") + "%' ";
			}			

			q += ") ";

		}

		q += likeQ;
		q += eqQ;
		q += " ORDER BY i.denominazione DESC ";

		Query qImp = ca.createQuery(q);
		//qImp.setParameter("isActive", true);

		@SuppressWarnings("unchecked")
		List<Impianto> impList = qImp.getResultList();

		injectList(impList, "ImpiantoList");
	}

	public List<SelectItem> getOptions(){
		List<SelectItem> out = new ArrayList<SelectItem>();

		String[] items = {"Esatta", "Contiene"};

		for(String s : items){
			SelectItem newSelIt = new SelectItem();
			newSelIt.setValue(s);
			newSelIt.setDescription(s);
			newSelIt.setLabel(s);

			out.add(newSelIt);
		}
		return out;
	}

	public String getSelectedType(){
		try{
			/* Se non siamo in creazione di un nuovo impianto, ma in modifica, recupera il code	*/
			if (this.getTemporary().get("impType")==null) {
				Impianto imp = getEntity();
				CodeValuePhi cv = null;

				if (imp!=null && imp.getCode()!=null)
					cv = getEntity().getCode();

				if (cv!=null)
					return cv.getCode();
				else
					return "exit";
			}

			String type = ((CodeValue)this.getTemporary().get("impType")).getCode();

			if (type==null || "".equals(type))
				return "exit";

			return type;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void resetTypes() {
		try {

			Object all = this.getTemporary().get("allTypes");
			Boolean allTypes = (all!=null && (Boolean)all);

			if(allTypes){
				this.getTemporary().remove("01");
				this.getTemporary().remove("02");
				this.getTemporary().remove("03");
				this.getTemporary().remove("04");
				this.getTemporary().remove("05");

				//I0064820 
				VerificaImpAction verifAct = VerificaImpAction.instance();
				verifAct.resetTestTypes();
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void setTemporary() {
		try {

			//Seleziona il check "tutti" - richiamando resetTypes() deseleziona gli eventuali sottotipi
			this.getTemporary().put("allTypes", true);
			this.resetTypes();

			this.getTemporary().put("esattaContiene", "Esatta");

			ImpTerraAction ita = ImpTerraAction.instance();
			HashMap<String, Object> impTerraTemp = ita.getTemporary();

			impTerraTemp.put("A", true);
			impTerraTemp.put("B", true);
			impTerraTemp.put("C", true);

			ImpSollAction isa = ImpSollAction.instance();
			HashMap<String, Object> impSollTemp = isa.getTemporary();

			impSollTemp.put("D", true);
			impSollTemp.put("E", true);
			impSollTemp.put("F", true);
			impSollTemp.put("G", true);
			impSollTemp.put("H", true);
			impSollTemp.put("I", true);
			impSollTemp.put("L", true);

			//			Negli apparecchi a pressione, "Caratteristiche speciali" non è un campo obbligatorio. 
			//			Nei filtri di ricerca i check NON saranno presettati  
			//			ImpPressAction ipa = ImpPressAction.instance();
			//			HashMap<String, Object> impPressTemp = ipa.getTemporary();
			//			
			//			impPressTemp.put("AP00", true);
			//			impPressTemp.put("AP01", true);
			//			impPressTemp.put("AP02", true);
			//			impPressTemp.put("AP03", true);
			//			impPressTemp.put("AP04", true);
			//			impPressTemp.put("AP05", true);
			//			impPressTemp.put("AP06", true);
			//			impPressTemp.put("AP07", true);
			//			impPressTemp.put("AP08", true);
			//			impPressTemp.put("AP09", true);
			//			impPressTemp.put("AP10", true);
			//			impPressTemp.put("AP11", true);
			//			impPressTemp.put("AP12", true);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void resetAllBut(String type) {
		try {
			if (type!=null && !"".equals(type)){

				HashMap<String, Object> temp = this.getTemporary();
				boolean checked = temp.get(type)==null?false:(Boolean)temp.get(type);

				//Se ho deselezionato un check, qualsiasi esso sia, seleziono "allTypes"
				if (!checked)
					this.getTemporary().put("allTypes", true);

				else {
					//Deseleziono tutti tranne il check selezionato
					String[] types = {"allTypes", "01", "02", "03", "04", "05"};

					for (String t:types)
						if (!t.equals(type))
							this.getTemporary().remove(t);
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	private void setAllTypes() {
		try {
			List<String> types = new ArrayList<String>();

			//Apparecchi a pressione
			types.add("phidic.arpav.imp.imptype.01_V0");

			//Impianti di riscaldamento
			types.add("phidic.arpav.imp.imptype.02_V0");

			//Ascensori e montacarichi
			types.add("phidic.arpav.imp.imptype.03_V0");

			//Apparecchi di sollevamento
			types.add("phidic.arpav.imp.imptype.04_V0");

			//Impianti elettrici - ex messa a terra
			types.add("phidic.arpav.imp.imptype.05_V0");	

			((FilterMap)getEqual()).putOr("code.id", types.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}	

	/* Utilizzato in ricerca verifiche (verificheSearch.mmgp) */
	public void resetAllType() {
		try {

			HashMap<String, Object> temp = this.getTemporary();

			if (!temp.isEmpty()) {
				Boolean press	= temp.get("01")==null?false:(Boolean)temp.get("01");
				Boolean risc 	= temp.get("02")==null?false:(Boolean)temp.get("02");
				Boolean asc 	= temp.get("03")==null?false:(Boolean)temp.get("03");
				Boolean soll 	= temp.get("04")==null?false:(Boolean)temp.get("04");
				Boolean terr 	= temp.get("05")==null?false:(Boolean)temp.get("05");

				//Se tutti i check sono selezionati - seleziona il check tutti e deseleziona gli altri
				if (press && risc && asc && soll && terr){
					this.getTemporary().put("allTypes", true);
					this.getTemporary().remove("01");
					this.getTemporary().remove("02");
					this.getTemporary().remove("03");
					this.getTemporary().remove("04");
					this.getTemporary().remove("05");

					//Se tutti i check sono deselezionati - seleziona il check tutti
				} else if (!press && !risc && !asc && !soll && !terr)
					this.getTemporary().put("allTypes", true);

				//Se solo alcuni check sono selezionati - deseleziona il check tutti
				else
					this.getTemporary().put("allTypes", false);
			}

			//I0064820 
			VerificaImpAction verifAct = VerificaImpAction.instance();
			verifAct.resetTestTypes();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/* Utilizzato in verificheInScadenza.jpdl.xml */
	public void setTypes() {
		try {
			List<String> types = new ArrayList<String>();

			//Fake code
			types.add("phidic.arpav.imp.imptype.00_V0");

			HashMap<String, Object> temp = this.getTemporary();
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

			((FilterMap)getEqual()).putOr("code.id", types.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	/* Utilizzato in verificheInScadenza.jpdl.xml */
	public void setSubTypes() {
		try {

			HashMap<String, Object> temp = this.getTemporary();
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
						((FilterMap)getEqual()).putOr("subTypeSoll.id", subTypes.toArray());

				} else //Se sono stati selezionati soli impianti elettrici (ex Terra)
					if (!press && !risc && !asc && !soll && terr){

						if (temp.get("A")==null?false:(Boolean)temp.get("A"))
							subTypes.add("phidic.arpav.imp.terra.type01.01_V0");

						if (temp.get("B")==null?false:(Boolean)temp.get("B"))
							subTypes.add("phidic.arpav.imp.terra.type01.02_V0");

						if (temp.get("C")==null?false:(Boolean)temp.get("C"))
							subTypes.add("phidic.arpav.imp.terra.type01.03_V0");

						if (subTypes.size()>0)
							((FilterMap)getEqual()).putOr("subTypeTerra.id", subTypes.toArray());
					}
			}

			if (subTypes.size()==0) {
				((FilterMap)getEqual()).remove("subTypeSoll.id");
				((FilterMap)getEqual()).remove("subTypeTerra.id");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}


	public void clearLinks(Impianto imp){
		try{
			if (imp==null || imp.getCode()==null)
				return;

			String type = imp.getCode().getCode();

			//Impianto a pressione
			if ("01".equals(type))	{
				ImpPress impPress = (ImpPress)imp;
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

				List<SchedaGeneratoriIndiv> schedeGen = impPress.getSchedaGeneratoriIndiv();
				if (schedeGen!=null && schedeGen.size()>0){
					for (SchedaGeneratoriIndiv sg:schedeGen){
						sg.setImpPress(null);
						ca.delete(sg);
					}
				}

				List<SchedaRecipientiIndiv> schedeRec = impPress.getSchedaRecipientiIndiv();
				if (schedeRec!=null && schedeRec.size()>0){
					for (SchedaRecipientiIndiv sr:schedeRec){
						sr.setImpPress(null);
						ca.delete(sr);
					}
				}

				List<SchedaTubazioniIndiv> schedeTub = impPress.getSchedaTubazioniIndiv();
				if (schedeTub!=null && schedeTub.size()>0){
					for (SchedaTubazioniIndiv st:schedeTub){
						st.setImpPress(null);
						ca.delete(st);
					}
				}

			} else if ("02".equals(type)) {
				//Impianto di riscaldamento
				ImpRisc impRisc = (ImpRisc)imp;
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

				List<SchedaGeneratori> schedeGen = impRisc.getSchedaGeneratori();
				if (schedeGen!=null && schedeGen.size()>0){
					for (SchedaGeneratori sg:schedeGen){
						sg.setImpRisc(null);
						ca.delete(sg);
					}
				}

				List<SchedaVasi> schedeVas = impRisc.getSchedaVasi();
				if (schedeVas!=null && schedeVas.size()>0){
					for (SchedaVasi sv:schedeVas){
						sv.setImpRisc(null);
						ca.delete(sv);
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Boolean isDeletable(){
		try{
			Boolean ret = true;
			Boolean isUserAllowed = false;
			Impianto imp = getEntity();

			if (imp==null || imp.getCode()==null)
				return false;

			ParameterManager pm = ParameterManager.instance();
			Object param = pm.getParameter("p.general.deleteImpianto", "value");

			if(param == null)
				return false;

			if(param != null){
				String p = param.toString();

				if (p!=null)
					isUserAllowed = ("true".equalsIgnoreCase(p) ? true : false);
			}

			if(!isUserAllowed)
				return isUserAllowed;

			String type = imp.getCode().getCode();

			if ("01".equals(type)) {
				//Pressione
				ImpPress impPress = (ImpPress)imp;
				if (impPress!=null && impPress.getVerificaImp()!=null && impPress.getVerificaImp().size()>0)
					ret= false;

				if(ret){
					ImpPressAction ia = ImpPressAction.instance();
					List<CessioneImp> storico = ia.storicoCessioni(impPress);
					if(storico==null || !storico.isEmpty())
						ret=false;
				}
			} else if ("02".equals(type)) {
				//Riscaldamento
				ImpRisc impRisc = (ImpRisc)imp;
				if (impRisc!=null && impRisc.getVerificaImp()!=null && impRisc.getVerificaImp().size()>0)
					ret= false;

				if(ret){
					ImpRiscAction ir = ImpRiscAction.instance();
					List<CessioneImp> storico = ir.storicoCessioni(impRisc);
					if(storico==null || !storico.isEmpty())
						ret=false;
				}
			} else if ("03".equals(type)) {
				//Montacarichi
				ImpMonta impMonta = (ImpMonta)imp;
				if (impMonta!=null && impMonta.getVerificaImp()!=null && impMonta.getVerificaImp().size()>0)
					ret= false;

				if(ret){
					ImpMontaAction im = ImpMontaAction.instance();
					List<CessioneImp> storico = im.storicoCessioni(impMonta);
					if(storico==null || !storico.isEmpty())
						ret=false;
				}
			} else if ("04".equals(type)) {
				//Sollevamento
				ImpSoll impSoll = (ImpSoll)imp;
				if (impSoll!=null && impSoll.getVerificaImp()!=null && impSoll.getVerificaImp().size()>0)
					ret= false;

				if(ret){
					ImpSollAction is = ImpSollAction.instance();
					List<CessioneImp> storico = is.storicoCessioni(impSoll);
					if(storico==null || !storico.isEmpty())
						ret=false;
				}
			} else if ("05".equals(type)) {
				//Elettrico 
				ImpTerra impTerra = (ImpTerra)imp;
				if (impTerra!=null && impTerra.getVerificaImp()!=null && impTerra.getVerificaImp().size()>0)
					ret= false;

				if(ret){
					ImpTerraAction it = ImpTerraAction.instance();
					List<CessioneImp> storico = it.storicoCessioni(impTerra);
					if(storico==null || !storico.isEmpty())
						ret=false;
				}
			}
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
			return false;
		} 
	}

	//Aggiorna il collettore ogni volta che la verifica viene salvata
	public void updateCollectors(Impianto imp) {
		try{
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


			if (verifiche!=null){
				for (VerificaImp ver:verifiche){

					if(Boolean.TRUE.equals(ver.getPre()))
						continue;

					ImpSearchCollector coll = ver.getImpSearchCollector();

					if (coll==null){
						coll = new ImpSearchCollector();
						ca.create(coll);
						ver.setImpSearchCollector(coll);
					}

					CodeValuePhi codeCv = imp.getCode();
					String code = codeCv.getCode();

					coll.setCode(codeCv);
					coll.setSigla(imp.getSigla());
					coll.setMatricola(imp.getMatricola());
					coll.setAnno(imp.getAnno());

					if (imp.getSedeInstallazione()!=null)
						coll.setDenominazioneSI(imp.getSedeInstallazione().getDenominazione());

					/* I0070276
					if (imp.getSedeAddebito()!=null)
						coll.setDenominazioneSA(imp.getSedeAddebito().getDenominazione());*/

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
				}
			}

			return;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void filterExpired() {
		try {

			removeExpression("this", "nextVerifDate1");
			removeExpression("this", "nextVerifDate2");
			removeExpression("this", "nextVerifDate3");

			HashMap<String, Object> temp = this.getTemporary();

			//includiScadute serve solo a cancellare scadenzaFrom...
			Boolean includiScadute = Boolean.TRUE.equals(temp.get("includiScadute"));
			Date scadenzaFrom = (Date) temp.get("scadenzaFrom");
			Date scadenzaTo = (Date) temp.get("scadenzaTo");
			if(includiScadute)
				scadenzaFrom = null;

			if(scadenzaTo != null){
				if(scadenzaFrom == null) {
					LogicalExpression datesTo = Restrictions.or(
							Restrictions.le("nextVerifDate1", scadenzaTo), 
							Restrictions.or(
									Restrictions.le("nextVerifDate2", scadenzaTo),
									Restrictions.le("nextVerifDate3", scadenzaTo)));

					entityCriteria.add(datesTo);
				}else if(scadenzaFrom != null) {
					LogicalExpression datesFromTo = Restrictions.or(
							Restrictions.between("nextVerifDate1", scadenzaFrom, scadenzaTo), 
							Restrictions.or(
									Restrictions.between("nextVerifDate2", scadenzaFrom, scadenzaTo),
									Restrictions.between("nextVerifDate3", scadenzaFrom, scadenzaTo)));

					entityCriteria.add(datesFromTo);
				}
			}else if (scadenzaFrom != null) {
				LogicalExpression datesFrom = 	Restrictions.or(
						Restrictions.ge("nextVerifDate1", scadenzaFrom), 
						Restrictions.or(
								Restrictions.ge("nextVerifDate2", scadenzaFrom),
								Restrictions.ge("nextVerifDate3", scadenzaFrom)));

				entityCriteria.add(datesFrom);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public String nextVerifDate(Impianto imp, String num) {
		try {
			String ret = "";
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if (imp==null || num==null || "".equals(num))
				return ret;

			CodeValuePhi code = imp.getCode();
			if (code==null)
				return ret;

			if ("01".equals(code.getCode())){
				if ("1".equals(num)){
					ret += "Idr.: ";
					if (imp.getNextVerifDate1()!=null)
						ret += sdf.format(imp.getNextVerifDate1());
				} else if ("2".equals(num)){
					ret += "Int.: ";
					if (imp.getNextVerifDate2()!=null)
						ret += sdf.format(imp.getNextVerifDate2());
				} else if ("3".equals(num)){
					ret += "Esr.: ";
					if (imp.getNextVerifDate3()!=null)
						ret += sdf.format(imp.getNextVerifDate3());
				}

			} else if (imp.getNextVerifDate1()!=null)
				ret += sdf.format(imp.getNextVerifDate1());

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Boolean expired(Impianto imp, String num) {
		try {
			if (imp==null || num==null || "".equals(num))
				return false;

			Date nextVerifDate = null;

			if ("1".equals(num))
				nextVerifDate = imp.getNextVerifDate1();
			else if ("2".equals(num))
				nextVerifDate = imp.getNextVerifDate2();
			else if ("3".equals(num))
				nextVerifDate = imp.getNextVerifDate3();

			if (nextVerifDate!=null && nextVerifDate.before(new Date()))
				return true;
			else 
				return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public Boolean toRead(Addebito addebito) {
		try {
			if (addebito==null)
				return false;

			if ( ((FilterMap)getEqual()).containsKey("code"))
				((FilterMap)getEqual()).remove("code");

			if ( ((FilterMap)getEqual()).containsKey("sigla"))
				((FilterMap)getEqual()).remove("sigla");

			if ( ((FilterMap)getEqual()).containsKey("matricola"))
				((FilterMap)getEqual()).remove("matricola");

			if ( ((FilterMap)getEqual()).containsKey("anno"))
				((FilterMap)getEqual()).remove("anno");

			String sigla = addebito.getSigla();
			CodeValuePhi impType = addebito.getImpType();
			String matricola = addebito.getMatricola();
			String anno = addebito.getAnno();

			if ((impType==null || impType.getCode()==null || "".equals(impType.getCode())) && (sigla==null || "".equals(sigla)) && (matricola==null || "".equals(matricola)) && (anno==null || "".equals(anno)) )
				return false;

			if (impType!=null && impType.getCode()!=null && !"".equals(impType.getCode()))
				((FilterMap)getEqual()).put("code.code", impType.getCode());

			if (sigla!=null && !"".equals(sigla))
				((FilterMap)getEqual()).put("sigla", sigla);

			if (matricola!=null && !"".equals(matricola))
				((FilterMap)getEqual()).put("matricola", matricola);

			if (anno!=null && !"".equals(anno))
				((FilterMap)getEqual()).put("anno", anno);

			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public ArrayList<Impianto> getSelected(ArrayList<Impianto> impianti) {
		try{
			ArrayList<Impianto> selected = new ArrayList<Impianto>();

			for (Impianto i:impianti){
				if (i.getIsNew()!=null && i.getIsNew()){
					i.setIsNew(false);
					selected.add(i);
				}
			}

			return selected;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void cessioneMulti(IdataModel<Impianto> idm, CessioneImp cess) {
		try{	
			List<Impianto> impianti = new ArrayList<Impianto>();
			if(idm==null){
				return;
			}else if(idm.getList()!=null){
				impianti=idm.getList();
			}
			for (Impianto i:impianti){
				if (i!=null && i.getCode()!=null && !"".equals(i.getCode())){
					String code = i.getCode().getCode();

					CessioneImp newCess = new CessioneImp();
					newCess.setUtente(cess.getUtente());
					newCess.setDataCessione(cess.getDataCessione());
					newCess.setNote(cess.getNote());

					newCess.setSediInstallazioneFrom(i.getSedeInstallazione());
					newCess.setSediInstallazione(cess.getSediInstallazione());

					/* I0070276
					 * newCess.setSediAddebitoFrom(i.getSedeAddebito());
					 * newCess.setSediAddebito(cess.getSediAddebito()); */

					newCess.setSediFrom(i.getSedi());
					newCess.setSedi(cess.getSedi());

					newCess.setIndirizzoSpedFrom(i.getIndirizzoSped());
					newCess.setIndirizzoSped(cess.getIndirizzoSped());

					newCess.setPersonaGiuridicaFrom(i.getSedeInstallazione().getSede().getPersonaGiuridica());
					newCess.setPersonaGiuridica(cess.getPersonaGiuridica());

					//Apparecchi a pressione
					if ("01".equals(code))
						ImpPressAction.instance().move((ImpPress)i, newCess);

					//Impianti di riscaldamento
					else if ("02".equals(code))
						ImpRiscAction.instance().move((ImpRisc)i, newCess);

					//Ascensori e montacarichi
					else if ("03".equals(code))
						ImpMontaAction.instance().move((ImpMonta)i, newCess);

					//Apparecchi di sollevamento	
					else if ("04".equals(code))
						ImpSollAction.instance().move((ImpSoll)i, newCess);

					//Impianti elettrici	
					else if ("05".equals(code))
						ImpTerraAction.instance().move((ImpTerra)i, newCess);
				}
			}

			ca.flushSession();

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void isDuplicate(Impianto imp){
		try {
			boolean duplicated = false;
			String type = this.getSelectedType();

			if(imp==null){
				//Se siamo in creazione è stato fatto il newEntity() del tipo specifico

				if("01".equals(type)){
					imp = ImpPressAction.instance().getEntity();
				}else if("02".equals(type)){
					imp = ImpRiscAction.instance().getEntity();
				}else if("03".equals(type)){
					imp = ImpMontaAction.instance().getEntity();
				}else if("04".equals(type)){
					imp = ImpSollAction.instance().getEntity();
				}else if("05".equals(type)){
					imp = ImpTerraAction.instance().getEntity();
				}
			}

			if (imp instanceof ImpPress)
				duplicated = ImpPressAction.instance().isDuplicate((ImpPress)imp);
			else if (imp instanceof ImpRisc)
				duplicated = ImpRiscAction.instance().isDuplicate((ImpRisc)imp);
			else if (imp instanceof ImpMonta)
				duplicated = ImpMontaAction.instance().isDuplicate((ImpMonta)imp);
			else if (imp instanceof ImpSoll)
				duplicated = ImpSollAction.instance().isDuplicate((ImpSoll)imp);
			else if (imp instanceof ImpTerra)
				duplicated = ImpTerraAction.instance().isDuplicate((ImpTerra)imp);

			if(duplicated)
				temporary.put("check", "E' stato trovato un impianto identificato con gli stessi valori. Impossibile validare i dati inseriti");
			else 
				temporary.put("check", "Impianto univoco.");
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean isNew(Impianto imp){
		if (imp==null || imp.getInternalId()==0)
			return true;

		return false;
	}

	/**
	 * Le modifiche potranno essere applicate solo e solamente se non esistono Verifiche o Addebiti vari in stato fatturato o validato. 
	 * 
	 * Nel caso in cui il sistema verificasse la presenza di Verifiche o Addebiti legati all'impianto fatturati, uscirà un avviso di questo tipo: 
	 * "Attenzione! A quest'impianto sono collegate Verifiche/Addebiti in stato FATTURATO. Impossibile procedere con le modifiche."
	 * 
	 * Nel caso in cui invece le Verifiche o gli Addebiti siano validati, il sistema riporterà all'utente: 
	 * "Attenzione! A quest'impianto sono collegate Verifiche/Addebiti in stato VALIDATO. Procedere con la svalidazione per proseguire con le modifiche." **/
	public boolean checkVerifiche(Impianto imp){
		try {
			temporary.remove("checkVerificheAddebiti");

			String qry = "SELECT v FROM VerificaImp v ";

			if (imp instanceof ImpPress)
				qry += "JOIN v.impPress imp ";
			else if (imp instanceof ImpRisc)
				qry += "JOIN v.impRisc imp ";
			else if (imp instanceof ImpMonta)
				qry += "JOIN v.impMonta imp ";
			else if (imp instanceof ImpSoll)
				qry += "JOIN v.impSoll imp ";
			else if (imp instanceof ImpTerra)
				qry += "JOIN v.impTerra imp ";

			qry += "LEFT JOIN v.statusCode statusCode " +
					"WHERE v.isActive = 1 AND " +
					"imp.internalId = :id AND " +
					"statusCode.code IN ('verified','completed')";

			Query qVer = ca.createQuery(qry);
			qVer.setParameter("id", imp.getInternalId());

			List<VerificaImp> vList = qVer.getResultList();	

			if(vList==null || vList.size()==0)
				return false;

			else {
				for (VerificaImp v: vList) {
					if (v.getStatusCode().getCode().equals("completed")){
						temporary.put("checkVerificheAddebiti", "A quest'impianto sono collegate Verifiche/Addebiti in stato FATTURATO.<br>Impossibile procedere con le modifiche.");
						return true;	
					}
				}
			}

			temporary.put("checkVerificheAddebiti", "A quest'impianto sono collegate Verifiche/Addebiti in stato VALIDATO.<br>Procedere con la svalidazione per proseguire con le modifiche.");
			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean checkAddebiti(Impianto imp){
		try {
			temporary.remove("checkVerificheAddebiti");

			String qry = "SELECT a FROM Addebito a ";

			if (imp instanceof ImpPress)
				qry += "LEFT JOIN a.impPress imp ";
			else if (imp instanceof ImpRisc)
				qry += "LEFT JOIN a.impRisc imp ";
			else if (imp instanceof ImpMonta)
				qry += "LEFT JOIN a.impMonta imp ";
			else if (imp instanceof ImpSoll)
				qry += "LEFT JOIN a.impSoll imp ";
			else if (imp instanceof ImpTerra)
				qry += "LEFT JOIN a.impTerra imp ";

			qry += "LEFT JOIN a.statusCode statusCode " +
					"WHERE a.isActive = 1 AND " +
					"imp.internalId = :id AND " +
					"statusCode.code IN ('verified','completed')";

			Query qVer = ca.createQuery(qry);
			qVer.setParameter("id", imp.getInternalId());

			List<Addebito> aList = qVer.getResultList();	

			if(aList==null || aList.size()==0)
				return false;

			else {
				for (Addebito a: aList) {
					if (a.getStatusCode().getCode().equals("completed")){
						temporary.put("checkVerificheAddebiti", "A quest'impianto sono collegate Verifiche/Addebiti in stato FATTURATO.<br>Impossibile procedere con le modifiche.");
						return true;	
					}
				}
			}

			temporary.put("checkVerificheAddebiti", "A quest'impianto sono collegate Verifiche/Addebiti in stato VALIDATO.<br>Procedere con la svalidazione per proseguire con le modifiche.");
			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/** Per i soli Impianti elettrici (05) le modifica sul Tipo prevede che vengano 
	 *  CANCELLATI tutti i dati specifici relativi al tipo precedentemente impostato. **/
	public void cleanType(Impianto imp){
		try {

			//Impianti elettrici (05)
			if (imp instanceof ImpTerra && imp.getSubTypeTerra()!=null){
				String type = imp.getSubTypeTerra().getCode();
				ImpTerra impTerra = ImpTerraAction.instance().getEntity();

				if (type==null)
					type="";

				//Se non è del sottotipo A (01)
				if (!"01".equals(type)){
					//					impTerra.setParafulmini("");
					impTerra.setAste("");
					//					impTerra.setIsolanti01("");
					impTerra.setRaggruppati01("");
					//					impTerra.setIsolanti02("");
					impTerra.setRaggruppati02("");
					//					impTerra.setMetalliche("");
					//					impTerra.setSerbatoi("");
					impTerra.setDisperdenti("");
					//					impTerra.setCantieri("");
					//impTerra.setStruttAereop("");
					//					impTerra.setStruttAutopCode(null);
					impTerra.setStruttAutopNum(null);
					impTerra.setSuperf01("");
					impTerra.setSuperf02("");
					impTerra.setSuperf03("");

					impTerra.setTipologia(null);
					impTerra.setTipologiaTesto("");
				}

				//Se non è del sottotipo B (02)
				if (!"02".equals(type)){
					impTerra.setSubTypeB(null);
					impTerra.setPot(null);
					impTerra.setDisperdenti("");
					impTerra.setCabine("");
					impTerra.setCabineCode(null);
					impTerra.setImpAutoprod(null);
					impTerra.setCabineNum(null);
				}

				//Se non è del sottotipo C (03)
				if (!"03".equals(type)){
					impTerra.setAreaPeric("");
					impTerra.setCi0(null);
					impTerra.setCi1(null);
					impTerra.setCi2(null);
					impTerra.setCi3(null);
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	private void filterBy() {
		try {
			HashMap<String, Object> thisLike = (FilterMap)this.getLike();
			HashMap<String, Object> thisEqual = (FilterMap)this.getEqual();

			HashMap<String, Object> impTemp = this.getTemporary();

			removeExpression("this", "sigla");
			removeExpression("this", "matricola");
			removeExpression("this", "anno");

			thisLike.remove("indirizzoSped.denominazione");
			thisLike.remove("indirizzoSped.addr.str");
			thisEqual.remove("enteVerificatore");

			//Sigla - matricola - anno
			String sigla = "";
			String matricola = "";
			String anno = "";
			String corrispondenza = "";

			if (impTemp.get("sigla")!=null)
				sigla = impTemp.get("sigla").toString();

			if (impTemp.get("matricola")!=null)
				matricola = impTemp.get("matricola").toString();

			if (impTemp.get("anno")!=null)
				anno = impTemp.get("anno").toString();

			if (impTemp.get("esattaContiene")!=null)
				corrispondenza = impTemp.get("esattaContiene").toString();			

			if ("".equals(corrispondenza) || "Esatta".equals(corrispondenza)){
				if (!"".equals(sigla))
					thisEqual.put("sigla", sigla);

				if (!"".equals(matricola))
					thisEqual.put("matricola", matricola);

				if (!"".equals(anno))
					thisEqual.put("anno", anno);
			} else if (!"".equals(sigla) || !"".equals(matricola) || !"".equals(anno)){

				/* filtro con criteriaquery - mette in or i paramentri di ricerca */
				if (!"".equals(sigla) && !sigla.contains("%"))
					sigla = "%" + sigla + "%";

				if (!"".equals(matricola) && !matricola.contains("%"))
					matricola = "%" + matricola + "%";

				if (!"".equals(anno) && !anno.contains("%"))
					anno = "%" + anno + "%";

				LogicalExpression logicalDates = Restrictions.or(Restrictions.like("sigla", sigla).ignoreCase(),
						Restrictions.or(Restrictions.like("matricola", matricola).ignoreCase(),
								Restrictions.like("anno", anno).ignoreCase())	);

				entityCriteria.add(logicalDates);
			}

			//Descrizione spedizione - Indirizzo di spedizione - Ente verificator
			String denominazioneIS = "";

			if (impTemp.get("denominazioneIS")!=null)
				denominazioneIS = impTemp.get("denominazioneIS").toString();

			if(!"".equals(denominazioneIS)){
				if(!denominazioneIS.startsWith("%"))
					denominazioneIS = "%" + denominazioneIS;

				if(!denominazioneIS.endsWith("%"))
					denominazioneIS = denominazioneIS + "%";

				thisLike.put("indirizzoSped.denominazione", denominazioneIS);
			}

			String addrStrIS = "";
			if (impTemp.get("addrStrIS")!=null)
				addrStrIS = impTemp.get("addrStrIS").toString();

			if(!"".equals(addrStrIS))
				thisLike.put("indirizzoSped.addr.str", addrStrIS);

			CodeValue enteVerificatore = null;
			if (impTemp.get("enteVerificatore")!=null)
				enteVerificatore = (CodeValue)impTemp.get("enteVerificatore");

			if(enteVerificatore!=null)
				thisEqual.put("enteVerificatore", enteVerificatore);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void readImpianti() throws PhiException {
		try {
			HashMap<String, Object> temp = this.getTemporary();

			//Se sono stati selezionate tutte le tipologie di impianto
			if (temp.get("allTypes")==null?false:(Boolean)temp.get("allTypes")) {		

				//Usato nella generazione del file csv (ImpiantiDocumentAction) 
				temp.put("singleType", false);

				this.setFullLike(true);

				this.setReadPageSize(150);
				//this.setReadPageSize(20);

				this.getEqual().put("isActive",true);
				this.getNotEqual().put("copy",true);
				this.getOrderBy().put("denominazione","descending");

				this.setAllTypes();
				this.filterBy();
				this.filterWithoutVerifNew();

				this.getTemporary().put("rowCount", this.countResults());
				super.read();


			} else {
				//Usato nella generazione del file csv (ImpiantiDocumentAction) 
				temp.put("singleType", true);

				//Apparecchi a pressione
				if (temp.get("01")==null?false:(Boolean)temp.get("01")){

					ImpPressAction ipa = ImpPressAction.instance();
					ipa.readAsImpianti();

					//Impianti di riscaldamento	
				} else if (temp.get("02")==null?false:(Boolean)temp.get("02")){

					ImpRiscAction ira = ImpRiscAction.instance();
					ira.readAsImpianti();

					//Ascensori e montacarichi	
				} else if (temp.get("03")==null?false:(Boolean)temp.get("03")){

					ImpMontaAction ima = ImpMontaAction.instance();
					ima.readAsImpianti();

					//Apparecchi di sollevamento
				} else if (temp.get("04")==null?false:(Boolean)temp.get("04")){

					ImpSollAction isa = ImpSollAction.instance();
					isa.readAsImpianti();

					//Impianti elettrici
				} else if (temp.get("05")==null?false:(Boolean)temp.get("05")){

					ImpTerraAction ita = ImpTerraAction.instance();
					ita.readAsImpianti();

				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void getStatusHistory() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		List<Impianto> history = getStatusHistoryList(entity);
		if(history==null) {
			ejectList(getConversationName() + historyListSuffix);
			return;
		}

		IdataModel<Impianto> dm = new PhiDataModel<Impianto>(history, conversationName);

		Contexts.getConversationContext().set(getConversationName() + historyListSuffix, dm);

		Events.instance().raiseEvent(PhiEvent.HISTORY, getEntity());
	}

	@SuppressWarnings("unchecked")
	public List<Impianto> getStatusHistoryList(Impianto imp) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String[] attributesChanged = new String[]{"statoImpianto", "dataVariazioneStato"};
		long t = System.currentTimeMillis();
		if (getEntity() == null) {
			return null;
		}

		Serializable id = ca.getIdentifier(imp);
		String entityName = entityClass.getName();
		List<Impianto> history = (List<Impianto>) ca.getHistoryof(HibernateProxyHelper.getClassWithoutInitializingProxy(imp), id, attributesChanged);

		ListIterator<Impianto> iter = history.listIterator();
		while(iter.hasNext()){
			CodeValue statoImpianto = iter.next().getStatoImpianto();
			if(statoImpianto==null || !statoImpianto.getId().startsWith("phidic.arpav.imp.stato")){
				iter.remove();
			}
		}
		//ordine discendente
		Collections.reverse(history);

		log.debug("[cid="+Conversation.instance().getId()+"] Readed History"+
				" for "+ entityName + " internalId "+ id +
				" with " + history.size()+" results in " +(System.currentTimeMillis()-t) + " ms " +
				(attributesChanged == null || attributesChanged.length == 0 ? "" : attributesChanged));

		return history;
	}

	@SuppressWarnings("unchecked")
	public Boolean checkStatoImpianto() {
		getTemporary().remove("warningMsg");
		boolean before=false, after = false;


		if(entity!=null && entity.getStatoImpianto()!=null && entity.getDataVariazioneStato()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String message = "";	
			FunctionsBean fn = FunctionsBean.instance();
			String joinClause = "";
			if(entity instanceof ImpTerra){
				joinClause="join v.impTerra imp ";
			}else if(entity instanceof ImpSoll){
				joinClause="join v.impSoll imp ";
			}else if(entity instanceof ImpRisc){
				joinClause="join v.impRisc imp ";
			}else if(entity instanceof ImpPress){
				joinClause="join v.impPress imp ";
			}else if(entity instanceof ImpMonta){
				joinClause="join v.impMonta imp ";
			}
			Query qVerifiche = ca.createQuery("select distinct v.internalId, v.statoImp, v.data from VerificaImp v " +
					"join v.statusCode status " +
					"join v.tipoInOut tipoInOut " +
					joinClause +
					"where v.statoImp <> :statoImp and status.code in ('new','verified') " +
					"and imp.internalId=:impId and tipoInOut.code='01' " + // tipoInOut.code='01' => VERIFICATORE INTERNO
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

		return !(before||after); //ok
	}

	/**
	 * Returns a list containing all documents of impianto, verifica and addebito
	 * @param impiantoDocuments
	 * @param verificaList
	 * @param addebitoList
	 * @return
	 */
	public List<AlfrescoDocument> getAllDocuments(List<AlfrescoDocument> impiantoDocuments, List<VerificaImp> verificaList, List<Addebito> addebitoList) {
		List<AlfrescoDocument> results = new ArrayList<AlfrescoDocument>();

		results.addAll(impiantoDocuments);

		if (verificaList != null) {
			for (VerificaImp verifica : verificaList) {
				results.addAll(verifica.getDocumenti());
			}
		}

		if (addebitoList != null) {
			for (Addebito addebito : addebitoList) {
				results.addAll(addebito.getDocumenti());
			}
		}

		return results;
	}

	public void fillOtherFieldsFromSedeInst(PersoneGiuridiche pg, String type){
		try {
			Impianto imp = null;

			if("01".equals(type)){
				imp = ImpPressAction.instance().getEntity();
			}else if("02".equals(type)){
				imp = ImpRiscAction.instance().getEntity();
			}else if("03".equals(type)){
				imp = ImpMontaAction.instance().getEntity();
			}else if("04".equals(type)){
				imp = ImpSollAction.instance().getEntity();
			}else if("05".equals(type)){
				imp = ImpTerraAction.instance().getEntity();
			}else if("copy".equals(type)){
				imp = ImpCopyAction.instance().getEntity();
			}


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
				if(!"copy".equals(type)) {
					IndirizzoSpedAction.instance().injectList(sa.getIndirizzoSped());
				}

				//Se la sede che sto usando ha un solo indirizzo di spedizione 
				if(sa.getIndirizzoSped().size() == 1) {
					if(sa.getIndirizzoSped().get(0).getIsActive()){
						//lo associo alla cessione
						is=sa.getIndirizzoSped().get(0);
					}
				}else {
					//cerco e associo (se esiste) l'indirizzo di spedizione principale
					for (IndirizzoSped i:sa.getIndirizzoSped()) {
						if(i.getIsActive() && sa.getIndirizzoSpedPrinc()!=null && sa.getIndirizzoSpedPrinc().getInternalId() == i.getInternalId()) {
							is=i;
						}
					}
				}
			}
			if("copy".equals(type)) {
				ImpCopyAction.instance().manageCopy(sa, is);
			}else{
				imp.setSedi(sa);
				imp.setIndirizzoSped(is);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void updateImpiantoFromCopy(Impianto imp, Impianto cpy, VerificaImp ver){
		if (imp==null || imp.getCode()==null || cpy==null || cpy.getCode()==null)
			return;

		if (imp instanceof HibernateProxy)
			imp = (Impianto) ((HibernateProxy)imp).getHibernateLazyInitializer().getImplementation();

		if (cpy instanceof HibernateProxy)
			cpy = (Impianto) ((HibernateProxy)cpy).getHibernateLazyInitializer().getImplementation();

		if(!imp.getCode().getCode().equals(cpy.getCode().getCode()) || !imp.getClass().equals(cpy.getClass()))
			return;

		String code = imp.getCode().getCode();

		if ("".equals(code)) {
			return;

			//Apparecchi a pressione
		} else if ("01".equals(code)) {
			ImpPressAction.instance().updateImpiantoFromCopy((ImpPress)imp, (ImpPress)cpy);
			ImpPressAction.instance().inject(imp);
			this.inject(imp);
			ver.setImpPress((ImpPress) imp);
			//Impianti di riscaldamento
		} else if ("02".equals(code)) {
			ImpRiscAction.instance().updateImpiantoFromCopy((ImpRisc)imp, (ImpRisc)cpy);
			ImpRiscAction.instance().inject(imp);
			this.inject(imp);
			ver.setImpRisc((ImpRisc) imp);
			//Ascensori e montacarichi
		} else if ("03".equals(code)) {
			ImpMontaAction.instance().updateImpiantoFromCopy((ImpMonta)imp, (ImpMonta)cpy);
			ImpMontaAction.instance().inject(imp);
			this.inject(imp);
			ver.setImpMonta((ImpMonta) imp);
			//Apparecchi di sollevamento	
		} else if ("04".equals(code)) {
			ImpSollAction.instance().updateImpiantoFromCopy((ImpSoll)imp, (ImpSoll)cpy);
			ImpSollAction.instance().inject(imp);
			this.inject(imp);
			ver.setImpSoll((ImpSoll) imp);
			//Impianti elettrici	
		} else if ("05".equals(code)) {
			ImpTerraAction.instance().updateImpiantoFromCopy((ImpTerra)imp, (ImpTerra)cpy);
			ImpTerraAction.instance().inject(imp);
			this.inject(imp);
			ver.setImpTerra((ImpTerra) imp);
		}

	}

	private void filterWithoutVerif(){
		removeExpression("this", "org.hibernate.criterion.ExistsSubqueryExpression");

		if(!Boolean.TRUE.equals(this.getTemporary().get("noVerifiche")))
			return;

		DetachedCriteria impSearchCollectorCriteria1 = DetachedCriteria.forClass(ImpSearchCollector.class,"impSearch1");

		impSearchCollectorCriteria1.add(Restrictions.sqlRestriction("impSearch1_.sigla = this_.sigla"));
		impSearchCollectorCriteria1.add(Restrictions.sqlRestriction("impSearch1_.matricola = this_.matricola"));
		impSearchCollectorCriteria1.add(Restrictions.sqlRestriction("impSearch1_.anno = this_.anno"));
		impSearchCollectorCriteria1.add(Restrictions.sqlRestriction("impSearch1_.code = this_.code"));

		impSearchCollectorCriteria1.setProjection(Projections.projectionList()
				.add(Projections.property("impSearch1.sigla"))
				.add(Projections.property("impSearch1.matricola"))
				.add(Projections.property("impSearch1.anno"))
				.add(Projections.property("impSearch1.code"))
				);

		DetachedCriteria impSearchCollectorCriteria2 = DetachedCriteria.forClass(ImpSearchCollector.class,"impSearch2");
		impSearchCollectorCriteria2.createAlias("verificaImp", "verificaImp", Criteria.LEFT_JOIN);

		impSearchCollectorCriteria2.add(Restrictions.sqlRestriction("impSearch2_.sigla = this_.sigla"));
		impSearchCollectorCriteria2.add(Restrictions.sqlRestriction("impSearch2_.matricola = this_.matricola"));
		impSearchCollectorCriteria2.add(Restrictions.sqlRestriction("impSearch2_.anno = this_.anno"));
		impSearchCollectorCriteria2.add(Restrictions.sqlRestriction("impSearch2_.code = this_.code"));
		impSearchCollectorCriteria2.add(Restrictions.isNotEmpty("verificaImp"));

		impSearchCollectorCriteria2.setProjection(Projections.projectionList()
				.add(Projections.property("impSearch2.sigla"))
				.add(Projections.property("impSearch2.matricola"))
				.add(Projections.property("impSearch2.anno"))
				.add(Projections.property("impSearch2.code"))
				);

		entityCriteria.add(Restrictions.or(Subqueries.notExists(impSearchCollectorCriteria1),
				Subqueries.notExists(impSearchCollectorCriteria2)));

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

	public boolean hasVerifiche(Impianto imp){
		try{
			boolean ret = true;
			//Impianto imp = getEntity();
	
			if (imp==null || imp.getCode()==null)
				return false;
	
			String type = imp.getCode().getCode();
	
			if ("01".equals(type)) {
				//Pressione
				ImpPress impPress = (ImpPress)imp;
				if (impPress!=null && impPress.getVerificaImp()==null && impPress.getVerificaImp().size()<=0)
					ret= false;
	
				if(ret){
					int actualVerifNum = 0;
					for(VerificaImp ver : impPress.getVerificaImp()){
						if(Boolean.TRUE.equals(ver.getPre())){
							continue;
						}
						actualVerifNum++;
					}
					if(actualVerifNum == 0)
						ret = false;
				}
			} else if ("02".equals(type)) {
				//Riscaldamento
				ImpRisc impRisc = (ImpRisc)imp;
				if (impRisc!=null && impRisc.getVerificaImp()==null && impRisc.getVerificaImp().size()<=0)
					ret= false;
	
				if(ret){
					int actualVerifNum = 0;
					for(VerificaImp ver : impRisc.getVerificaImp()){
						if(Boolean.TRUE.equals(ver.getPre())){
							continue;
						}
						actualVerifNum++;
					}
					if(actualVerifNum == 0)
						ret = false;
				}
			} else if ("03".equals(type)) {
				//Montacarichi
				ImpMonta impMonta = (ImpMonta)imp;
				if (impMonta!=null && impMonta.getVerificaImp()==null && impMonta.getVerificaImp().size()<=0)
					ret= false;
	
				if(ret){
					int actualVerifNum = 0;
					for(VerificaImp ver : impMonta.getVerificaImp()){
						if(Boolean.TRUE.equals(ver.getPre())){
							continue;
						}
						actualVerifNum++;
					}
					if(actualVerifNum == 0)
						ret = false;
				}
			} else if ("04".equals(type)) {
				//Sollevamento
				ImpSoll impSoll = (ImpSoll)imp;
				if (impSoll!=null && impSoll.getVerificaImp()==null && impSoll.getVerificaImp().size()<=0)
					ret= false;
	
				if(ret){
					int actualVerifNum = 0;
					for(VerificaImp ver : impSoll.getVerificaImp()){
						if(Boolean.TRUE.equals(ver.getPre())){
							continue;
						}
						actualVerifNum++;
					}
					if(actualVerifNum == 0)
						ret = false;
				}
			} else if ("05".equals(type)) {
				//Elettrico 
				ImpTerra impTerra = (ImpTerra)imp;
				if (impTerra!=null && impTerra.getVerificaImp()==null && impTerra.getVerificaImp().size()<=0)
					ret= false;
	
				if(ret){
					int actualVerifNum = 0;
					for(VerificaImp ver : impTerra.getVerificaImp()){
						if(Boolean.TRUE.equals(ver.getPre())){
							continue;
						}
						actualVerifNum++;
					}
					if(actualVerifNum == 0)
						ret = false;
				}
			}
			return ret;
	
		} catch (Exception ex) {
			log.error(ex);
			//throw new RuntimeException(ex);
			return false;
		} 
	}

	private void filterWithoutVerifNew() {
		try {
	
			this.getEqual().remove("verificheLong");

			if(!Boolean.TRUE.equals(this.getTemporary().get("noVerifiche")))
				return;
	
			this.getEqual().put("verificheLong", 0L);
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

}