package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Esposti;
import com.phi.entities.baseEntity.EventoAccidentale;
import com.phi.entities.baseEntity.LavorazioniCorrelate;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.SchedaEsposti;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Sostanze;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueInail;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

@BypassInterceptors
@Name("SchedaEspostiAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaEspostiAction extends BaseAction<SchedaEsposti, Long> {

	private static final Logger log = Logger.getLogger(SchedaEspostiAction.class);
	private static final long serialVersionUID = 1379655813L;

	public static SchedaEspostiAction instance() {
		return (SchedaEspostiAction) Component.getInstance(SchedaEspostiAction.class, ScopeType.CONVERSATION);
	}
	
	public void filterInail(){
		String inailOpt = (String)getTemporary().get("inailOpt");
		CodeValueInail filter = CodeValueInailAction.instance().getEntity();
			
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		//clean entityCriteria
		removeExpression("this", "LavorazioniCorrelate.inail");
		removeExpression("this", "inail");
		if(findSubCriteria("LavorazioniCorrelate")==null)
			entityCriteria.createAlias("lavorazioniCorrelate", "LavorazioniCorrelate", Criteria.LEFT_JOIN);
		
		if(filter==null)
			return;
		
		if(inailOpt==null){			
			LogicalExpression inail = Restrictions.or(Restrictions.eq("inail",filter), Restrictions.eq("LavorazioniCorrelate.inail",filter));
			entityCriteria.add(inail);
		}else if("Prevalente".equals(inailOpt)){
			entityCriteria.add(Restrictions.eq("inail",filter));
		}else if("Correlata".equals(inailOpt)){
			entityCriteria.add(Restrictions.eq("LavorazioniCorrelate.inail",filter));
		}	
	}
	
	public void filterLavUnica(){
		String lavOpt = (String)getTemporary().get("lavOpt");
		CodeValueAteco filter = CodeValueAtecoAction.instance().getEntity();
			
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		//clean entityCriteria
		removeExpression("this", "LavorazioniCorrelate.lavUnica");
		removeExpression("this", "lavUnica");
		if(findSubCriteria("LavorazioniCorrelate")==null)
			entityCriteria.createAlias("lavorazioniCorrelate", "LavorazioniCorrelate", Criteria.LEFT_JOIN);
		
		if(filter==null)
			return;
		
		if(lavOpt==null){
			LogicalExpression lavUnica = Restrictions.or(Restrictions.eq("lavUnica",filter), Restrictions.eq("LavorazioniCorrelate.lavUnica",filter));
			entityCriteria.add(lavUnica);
		}else if("Prevalente".equals(lavOpt)){
			entityCriteria.add(Restrictions.eq("lavUnica",filter));
		}else if("Correlata".equals(lavOpt)){
			entityCriteria.add(Restrictions.eq("LavorazioniCorrelate.lavUnica",filter));
		}	
	}
	
	public void filterTotale(){
		String totOpt = (String)getTemporary().get("totOpt");
		Integer filter = (Integer)getTemporary().get("totale");
			
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		removeExpression("this", "tot_uomini + tot_donne");
		removeExpression("this", "totUomini");
		removeExpression("this", "totDonne");
		
		if(filter==null)
			return;
		
		if(totOpt==null){
			entityCriteria.add(Restrictions.sqlRestriction("tot_uomini + tot_donne >= ?", filter, Hibernate.INTEGER));
		}else if("M".equals(totOpt)){
			entityCriteria.add(Restrictions.ge("totUomini",filter));
		}else if("F".equals(totOpt)){
			entityCriteria.add(Restrictions.ge("totDonne",filter));
		}	
	}
	
	public void filterEsposti(){
		String espOpt = (String)getTemporary().get("espOpt");
		Integer filter = (Integer)getTemporary().get("esposti");
			
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		removeExpression("this", "esp_uomini + esp_donne");
		removeExpression("this", "espUomini");
		removeExpression("this", "espDonne");
		
		
		if(filter==null)
			return;
		
		if(espOpt==null){
			entityCriteria.add(Restrictions.sqlRestriction("esp_uomini + esp_donne >= ?", filter, Hibernate.INTEGER));
		}else if("M".equals(espOpt)){
			entityCriteria.add(Restrictions.ge("espUomini",filter));
		}else if("F".equals(espOpt)){
			entityCriteria.add(Restrictions.ge("espDonne",filter));
		}	
	}

	public void filterCas(){
		CodeValuePhi sostanza1 = (CodeValuePhi)getTemporary().get("sostanze1");
		CodeValuePhi sostanza2 = (CodeValuePhi)getTemporary().get("sostanze2");
		
		//clean entityCriteria
		getEqual().remove("sostanze.sostanza");
		
		if(sostanza1!=null && sostanza2!=null){
			((FilterMap)getEqual()).putOr("sostanze.sostanza", sostanza1, sostanza2);
		}else if(sostanza1!=null){
			getEqual().put("sostanze.sostanza", sostanza1);
		}else if(sostanza2!=null){
			getEqual().put("sostanze.sostanza", sostanza2);
		}	
	}
	
	public void filterGruppo34(){
		CodeValuePhi bio1 = (CodeValuePhi)getTemporary().get("bio1");
		CodeValuePhi bio2 = (CodeValuePhi)getTemporary().get("bio2");
					
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		//clean entityCriteria
		removeExpression("this", "Sostanze.bio");
		removeExpression("this", "Sostanze.gruppo3");
		if(findSubCriteria("Sostanze")==null)
			entityCriteria.createAlias("sostanze", "Sostanze", Criteria.LEFT_JOIN);
		
		
		if(bio1!=null && bio2!=null){
			LogicalExpression sub1 = Restrictions.and(Restrictions.eq("Sostanze.bio",bio1), Restrictions.eq("Sostanze.gruppo3",true));
			LogicalExpression sub2 = Restrictions.and(Restrictions.eq("Sostanze.bio",bio2), Restrictions.eq("Sostanze.gruppo3",false));
			entityCriteria.add(Restrictions.or(sub1, sub2));
		}else if(bio1!=null){
			LogicalExpression sub1 = Restrictions.and(Restrictions.eq("Sostanze.bio",bio1), Restrictions.eq("Sostanze.gruppo3",true));
			entityCriteria.add(sub1);
		}else if(bio2!=null){
			LogicalExpression sub2 = Restrictions.and(Restrictions.eq("Sostanze.bio",bio2), Restrictions.eq("Sostanze.gruppo3",false));
			entityCriteria.add(sub2);
		}	
	}
	
	public void filterTipologia(){
		CodeValuePhi tipologia = (CodeValuePhi)getEqual().get("tipologia");
		
		//clean entityCriteria
		getEqual().remove("tipologia.code");
		
		if(tipologia==null){
			((FilterMap)getEqual()).putOr("tipologia.code", "SUBTYPE1", "SUBTYPE2");
		}
	}
	
	public void linkUnlinkSostanze(List<Sostanze> link, List<Sostanze> unlink){
		if(entity==null)
			return;
		
		entity.setSostanze(new ArrayList<Sostanze>());
		
		if(link!=null){			
			for(Sostanze sost : link){
				entity.getSostanze().add(sost);
				sost.setSchedaEsposti(entity);
			}
		}
		if(unlink!=null){
			for(Sostanze sost : unlink){
				entity.getSostanze().remove(sost);
				sost.setSchedaEsposti(null);
			}
		}
	}
	
	public void linkUnlinkLavorazioni(List<LavorazioniCorrelate> link, List<LavorazioniCorrelate> unlink){
		if(entity==null)
			return;
		
		entity.setLavorazioniCorrelate(new ArrayList<LavorazioniCorrelate>());
		
		if(link!=null){
			for(LavorazioniCorrelate lav : link){
				entity.getLavorazioniCorrelate().add(lav);
				lav.setSchedaEsposti(entity);
			}
		}
		if(unlink!=null){
			for(LavorazioniCorrelate lav : unlink){
				entity.getLavorazioniCorrelate().remove(lav);
				lav.setSchedaEsposti(null);
			}
		}
	}
	
	public void linkUnlinkEventi(List<EventoAccidentale> link, List<EventoAccidentale> unlink, Person person){
		if(entity==null)
			return;
		
		/*
		 * In questo caso non parto facendo tabula rasa perchè sto modificando gli eventi di un singolo dipendente
		 */
		if(entity.getEventoAccidentale()==null){
			entity.setEventoAccidentale(new ArrayList<EventoAccidentale>());
		}
		
		if(link!=null){			
			for(EventoAccidentale eventi : link){
				entity.addEventoAccidentale(eventi);
				eventi.setSchedaEsposti(entity);
				eventi.setPerson(person);
			}
		}
		if(unlink!=null){
			for(EventoAccidentale eventi : unlink){
				entity.getEventoAccidentale().remove(eventi);
				eventi.setPerson(null);
			}
		}
	}
	
	public void linkUnlinkEsposti(List<Esposti> link, List<Esposti> unlink, Person person){
		if(entity==null)
			return;
		
		/*
		 * In questo caso non parto facendo tabula rasa perchè sto modificando gli esposti di un singolo dipendente
		 */
		if(entity.getEsposti()==null){
			entity.setEsposti(new ArrayList<Esposti>());
		}
		
		if(link!=null){			
			for(Esposti esposti : link){
				entity.addEsposti(esposti);
				esposti.setSchedaEsposti(entity);
				esposti.setPerson(person);				
			}
		}
		if(unlink!=null){
			for(Esposti esposti : unlink){
				entity.getEsposti().remove(esposti);
				esposti.setPerson(null);
			}
		}
	}
	
	public void linkUnlinkEsposti(List<Esposti> link, List<Esposti> unlink){
		if(entity==null)
			return;
		
		entity.setEsposti(new ArrayList<Esposti>());
		
		if(link!=null){			
			for(Esposti esposti : link){
				entity.getEsposti().add(esposti);
				esposti.setSchedaEsposti(entity);
			}
		}
		if(unlink!=null){
			for(Esposti esposti : unlink){
				entity.getEsposti().remove(esposti);
				esposti.setSchedaEsposti(null);
			}
		}
	}

	/**
	 * PER PARIX: MODIFICARE LA QUERY HQL SULLA BASE NON DI ditta.internalId e sede.internalId, MA IDENTIFICATIVI CHE
	 * RESTANO COSTANTI ALL'INTERNO DI PARIX
	 * @param ditta
	 * @param sede
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void checkAndClone(PersoneGiuridiche ditta, Sedi sede, boolean biologico) throws InstantiationException, IllegalAccessException{
		Long sedeId = null;
		
		if(ditta==null)
			inject(newEntity());
		
		if(sede!=null)
			sedeId = sede.getInternalId();
		
		/*
		 * Per PARIX la chiave univoca delle ditte è il codice fiscale.
		 * Invece, non vi è modo di identificare univocamente un'unità locale appartenente ad una sede secondaria (CCIAA+N_REA).
		 * L'unico identificativo utilizzabile sarebbe il progressivo dell'unità locale, ma non è detto che l'integrazione
		 * ritorni le stesse unità locali con lo stesso progressivo sembra l'indice all'interno della lista.
		 * In tal caso potremmo usare la terna CCIAA+N_REA+N_PROGRESSIVO
		 */
		String hql = "SELECT scheda FROM SchedaEsposti scheda " +
				"JOIN scheda.personeGiuridiche ditta " +
				"JOIN scheda.tipologia tipologia " +
				"WHERE ditta.codiceFiscale = :dittaCf " +
				"AND scheda.endValidity IS NULL AND scheda.isActive = 1 " +
				"AND tipologia.code IN (:types) ORDER BY scheda.dataCompilazione DESC ";
		
		Query q = ca.createQuery(hql);
		q.setParameter("dittaCf", ditta.getCodiceFiscale());
		List<String> types = new ArrayList<String>();
		if(biologico){
			types.add("SUBTYPE3");
			q.setParameter("types", types);
			
		}else{
			types.add("SUBTYPE1");
			types.add("SUBTYPE2");
			q.setParameter("types", types);
		}
		
		List<SchedaEsposti> result = q.getResultList();
		if(result.size()>1){
			log.error("SchedaEspostiAction.checkAndClone returned more than one open row for latest SchedaEsposti.");
		}else if(result.size()==1){
			SchedaEsposti latest = result.get(0);
			inject(copy(latest));
		}else{
			inject(newEntity());
		}
	}
	
	public SchedaEsposti copy(SchedaEsposti oldScheda){
		if(oldScheda==null)
			return null;
		
		SchedaEsposti newScheda = new SchedaEsposti();
		
		if(oldScheda.getCancerogeno()!=null){
			List<CodeValuePhi> newCancerogeno = new ArrayList<CodeValuePhi>();
			newCancerogeno.addAll(oldScheda.getCancerogeno());
			newScheda.setCancerogeno(newCancerogeno);
		}
		
		//la data compilazione non si copia
		newScheda.setEspDonne(oldScheda.getEspDonne());
		newScheda.setEspUomini(oldScheda.getEspUomini());
		newScheda.setInail(oldScheda.getInail());
		newScheda.setLavUnica(oldScheda.getLavUnica());
		newScheda.setNotInAll5(oldScheda.getNotInAll5());
		newScheda.setTipologia(oldScheda.getTipologia());
		newScheda.setTotAmm(oldScheda.getTotAmm());
		newScheda.setTotDonne(oldScheda.getTotDonne());
		newScheda.setTotProd(oldScheda.getTotProd());
		newScheda.setTotUomini(oldScheda.getTotUomini());
		
		SostanzeAction sostAction = SostanzeAction.instance();
		LavorazioniCorrelateAction lavAction = LavorazioniCorrelateAction.instance();
		EspostiAction espAction = EspostiAction.instance();
		
		List<Sostanze> newSostanze = new ArrayList<Sostanze>();
		List<LavorazioniCorrelate> newLavorazioni = new ArrayList<LavorazioniCorrelate>();
		List<Esposti> newEsposti = new ArrayList<Esposti>();
			
		if(oldScheda.getSostanze() != null){
			for(Sostanze sost : oldScheda.getSostanze()){
				newSostanze.add(sostAction.copy(sost));
			}
		}
		
		if(oldScheda.getLavorazioniCorrelate() != null){
			for(LavorazioniCorrelate lav : oldScheda.getLavorazioniCorrelate()){
				newLavorazioni.add(lavAction.copy(lav));
			}
		}
		
		//non serve linkare le precedenti: c'è la gestione ad hoc
		
		if(oldScheda.getEsposti() != null){
			for(Esposti esp : oldScheda.getEsposti()){
				Esposti newEsp = espAction.copy(esp);
				if(newEsp!=null){
					newEsposti.add(newEsp);
					newEsp.setSchedaEsposti(newScheda);
				}			
			}
		}
		
		//INIT LISTS
		if(newScheda.getTipologia()!=null && "SUBTYPE3".equals(newScheda.getTipologia().getCode())){ //biologico
			List<Sostanze> gruppo3 = new ArrayList<Sostanze>();
			List<Sostanze> gruppo4 = new ArrayList<Sostanze>();
			for(Sostanze s : newSostanze){
				if(Boolean.TRUE.equals(s.getGruppo3())){
					gruppo3.add(s);
				
				}else if(Boolean.FALSE.equals(s.getGruppo3())){
					gruppo4.add(s);
				}
			}
			
			if(!gruppo3.isEmpty()){
				getTemporary().put("gruppo3",true);
			}else{
				getTemporary().put("gruppo3",false);
			}
			
			if(!gruppo4.isEmpty()){
				getTemporary().put("gruppo4",true);
			}else{
				getTemporary().put("gruppo4",false);
			}
			sostAction.injectList(gruppo3,"Sostanze1List");
			sostAction.injectList(gruppo4,"Sostanze2List");
		}else{
			sostAction.injectList(newSostanze);
		}
		
		lavAction.injectList(newLavorazioni);
		espAction.injectList(newEsposti);
		
		getTemporary().put("oldScheda",oldScheda);
		return newScheda;
	}
	
	/**
	 * Anche in modifica devo caricare l'ultima scheda chiusa, perchè potrei cambiare la data di compilazione,
	 * e sarebbe meglio non lasciare buchi.
	 */
	public void loadOldScheda() {
		
		SchedaEsposti currentScheda = getEntity();
		if(currentScheda == null)
			return;
		
		Long sedeId = null;
		Long dittaId = null;
		
		if(currentScheda.getPersoneGiuridiche()!=null)
			dittaId = currentScheda.getPersoneGiuridiche().getInternalId();
		else 
			return;	//non è possibile che la scheda non sia associata alla ditta
		
		if(currentScheda.getSedi()!=null)
			sedeId = currentScheda.getSedi().getInternalId();
		
		/*
		 * TODO: usare ditta.internalId e sede.internalId è limitante perchè a tendere avrò più versioni
		 * con Parix: quindi sarebbe da usare una proprietà che si conserva tra le versioni, tipo codice fiscale + partita iva
		 * 
		 * VA FATTO IN 2 TEMPI: PRIMA CERCO LA DITTA SU PARIX SULLA BASE DEL CODICE FISCALE.
		 * SE LA TROVO, CHIUDO TUTTE LE PRECEDENTI, CREO LA NUOVA, CERCO L'ULTIMA SCHEDA ESPOSTI SU DITTA CON
		 * MEDESIMO CODICE FISCALE, COPIO E AGGANCIO LA NUOVA DITTA.
		 * 
		 * USANDO PARIX DIVENTA IMPOSSIBILE GESTIRE LE SCHEDE A LIVELLO DI SEDE, PERCHE NON ESISTE UN IDENTIFICATIVO UNIVOCO PER UNA SEDE
		 * USIAMO LA DENOMINAZIONE?
		 */
		String hql = "SELECT scheda FROM SchedaEsposti scheda " +
				"JOIN scheda.personeGiuridiche ditta " +
				"LEFT JOIN scheda.sedi sede " +
				"WHERE ditta.internalId = :dittaId AND (:sedeId IS NULL OR sede.internalId = :sedeId) " +
				"AND scheda.endValidity IS NOT NULL AND scheda.isActive = 1 ORDER BY scheda.dataCompilazione DESC ";
		
		Query q = ca.createQuery(hql);
		q.setParameter("dittaId", dittaId);
		q.setParameter("sedeId", sedeId);
		
		List<SchedaEsposti> result = q.getResultList();
		if(result.size()>0){
			SchedaEsposti oldScheda = result.get(0);
			getTemporary().put("oldScheda",oldScheda);
		}
	}
	
	public void closePreviousScheda() throws PersistenceException{
		SchedaEsposti newScheda = getEntity();
		SchedaEsposti oldScheda = (SchedaEsposti)getTemporary().get("oldScheda");
		if(newScheda==null || oldScheda==null)
			return;
		
		Calendar cal = Calendar.getInstance(Locale.getDefault());		
		cal.setTime(newScheda.getDataCompilazione());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.add(Calendar.DATE, -1);
		
		oldScheda.setEndValidity(cal.getTime());
		ca.create(oldScheda);
		//non rimuovo da temporary la oldScheda, perchè potrei modificare la data di compilazione e salvare nuovamente
	}
	
	public void openPreviousScheda() throws PersistenceException{
		loadOldScheda();
		SchedaEsposti oldScheda = (SchedaEsposti)getTemporary().get("oldScheda");
		if(oldScheda==null)
			return;
		
		oldScheda.setEndValidity(null);
		ca.create(oldScheda);
		getTemporary().remove("oldScheda");
	}
	
	public List<SelectItem> getFattoriRischio() throws PersistenceException, DictionaryException{
		List<SelectItem> lst = new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue ncas = voc.getCodeValue("PHIDIC", "FattoriRischio", "ncas", "S");
		CodeValue nall = voc.getCodeValue("PHIDIC", "FattoriRischio", "nall", "S");
		
		if(ncas!=null)
			lst.add(new SelectItem(ncas, ncas.getCurrentTranslation()));
		if(nall!=null)
			lst.add(new SelectItem(nall, nall.getCurrentTranslation()));
		
		return lst;
	}
	
	public void resetFilters() throws PersistenceException, DictionaryException{
		String autocomplete = "sostanze.sostanza";
		if(autocomplete!=null && !autocomplete.isEmpty()){
			getAutocomplete().get(autocomplete).setContentType(4);
			getAutocomplete().get(autocomplete).setCodeSystem("PHIDIC");
			getAutocomplete().get(autocomplete).setFullLike(true);

			getTemporary().remove("sostanze1");
			getTemporary().remove("sostanze2");
			
			if(getTemporary().get("tipologia") instanceof CodeValue){
				CodeValue agente = (CodeValue)getTemporary().get("tipologia");
				getAutocomplete().get(autocomplete).setDomain(agente.getDisplayName());
			}
		}
	}
	
	private Date currentDate() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}
}