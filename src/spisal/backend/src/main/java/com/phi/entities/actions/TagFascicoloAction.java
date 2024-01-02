package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.collection.PersistentBag;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Risposta;
import com.phi.entities.baseEntity.TagFascicolo;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parameters.ParameterManager;
import com.phi.security.SpisalUserAction;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("TagFascicoloAction")
@Scope(ScopeType.CONVERSATION)
public class TagFascicoloAction<T> extends BaseAction<TagFascicolo, Long> {

	private static final long serialVersionUID = 2101013802L;
	
	private static final Logger log = Logger.getLogger(TagFascicoloAction.class);

	public static TagFascicoloAction instance() {
		return (TagFascicoloAction) Component.getInstance(TagFascicoloAction.class, ScopeType.CONVERSATION);
	}

	public void filterDates(){
		Date fromDate = (Date)getTemporary().get("fromDate");
		Date toDate = (Date)getTemporary().get("toDate");
		Boolean nonattivi = (Boolean)getTemporary().get("nonattivi");
		
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);
		
		LogicalExpression from = null;
		LogicalExpression to = null;
		
		if(!nonattivi){
			if(fromDate!=null){
				from = Restrictions.or(Restrictions.ge("startValidity", fromDate), Restrictions.ge("endValidity", fromDate));
			}
			
			if(toDate!=null)
				to = Restrictions.or(Restrictions.le("startValidity", toDate), Restrictions.le("endValidity", toDate));
			
			if(from!=null && to!=null)
				entityCriteria.add(Restrictions.or(from, to));
			else if(from!=null)
				entityCriteria.add(from);
			else if(to!=null)
				entityCriteria.add(to);
			
		} else if(fromDate!=null)
				entityCriteria.add(Restrictions.ge("startValidity", fromDate));
	}
	
	public List<TagFascicolo> removeFromList(List<TagFascicolo> sourceElements, Object tagsToRemove){
		
		if (sourceElements==null || sourceElements.size()<1 || tagsToRemove==null)
			return sourceElements;
			
		List<TagFascicolo> resultList = new ArrayList<TagFascicolo>();
		PersistentBag removeElements = (PersistentBag)tagsToRemove;
		
		Iterator it2 = sourceElements.iterator();
	  	
		while(it2.hasNext()) {
			TagFascicolo tag = (TagFascicolo)it2.next();
	  		
			if (tag!=null){
	  			Long internalId = (Long) tag.getInternalId();
	  			boolean mustBeRemoved = false;
	  			
	  			Iterator it = removeElements.iterator();
	  			
	  			while(it.hasNext()){
	  				TagFascicolo tf = (TagFascicolo) it.next();
	  				
	  				if(internalId==tf.getInternalId())
	  					mustBeRemoved = true;
	  			}
	    	  
	  			if(!mustBeRemoved)
	  				resultList.add(tag);
	  		  }
		}
		
		return resultList;
	}
	
	public void unlinkall(){
		TagFascicolo tag = getEntity();
		
		List<Procpratiche> listaPratiche = tag.getProcpratiche(); 
		if(listaPratiche!=null){
			
			for(Procpratiche pp:listaPratiche)
				pp.setTagFascicolo(null);
			
			tag.setProcpratiche(null);
		}
	}
	
	public void filterByEndValidity(){
		Boolean nonAttivi = (Boolean)getTemporary().get("nonattivi");
		TagFascicoloAction tagFascicoliAction = TagFascicoloAction.instance();
		
		if (nonAttivi==null || !nonAttivi)
			((FilterMap)tagFascicoliAction.getGreaterEqual()).putOr("endValidity", new Date(), null);
		
		else 		
			((FilterMap)tagFascicoliAction.getGreaterEqual()).remove("endValidity");

	}
	
	public void addDistretto(ServiceDeliveryLocation dist){
		if(getEntity().getDistretti()==null)
			getEntity().setDistretti(new ArrayList<ServiceDeliveryLocation>());
		
		getEntity().getDistretti().add(dist);
	}
	
	public void removeDistretto(ServiceDeliveryLocation dist){ //linea is not correctly injected :\
		DistrettiAction da = DistrettiAction.instance(); 
		ServiceDeliveryLocation sdl = da.getEntity();
		
		if(getEntity().getDistretti().contains(sdl))
			getEntity().getDistretti().remove(sdl);
	}
	
	public void addLinea(ServiceDeliveryLocation linea){
		if(getEntity().getLinee()==null)
			getEntity().setLinee(new ArrayList<ServiceDeliveryLocation>());
		
		getEntity().getLinee().add(linea);
	}
	
	public void removeLinea(ServiceDeliveryLocation linea){  //linea is not correctly injected :\
		LineeAction la = LineeAction.instance();
		ServiceDeliveryLocation sdl = la.getEntity();
		
		if(getEntity().getLinee().contains(sdl))
			getEntity().getLinee().remove(sdl);
	}
	
	public void addUlss(ServiceDeliveryLocation ulss){
		if(getEntity().getUlss()==null)
			getEntity().setUlss(new ArrayList<ServiceDeliveryLocation>());
		
		getEntity().getUlss().add(ulss);
	}
	
	public void removeUlss(ServiceDeliveryLocation ulss){ //ulss is not correctly injected :\
		UlssAction ua = UlssAction.instance();
		ServiceDeliveryLocation sdl = ua.getEntity();
		
		if(getEntity().getUlss().contains(sdl))
			getEntity().getUlss().remove(sdl);
	}
	
	public boolean checkErrors(){
		boolean result = true;
		String errorMessage = "";
		getTemporary().remove("errorMessage");
		
		TagFascicolo tf = getEntity();
		if(tf==null) return result;
		
		CodeValuePhi tagType = tf.getTagType();
				
		if(tagType==null) 
			return result;
		
		String type = tagType.getCode(); 

		//se il pulsante per selezionare i distretti è visibile ma non è stato aggiunto nessun distretto
		if (type.equals("gen") || type.equals("distr")) {
			if((tf.getDistretti()==null || tf.getDistretti().isEmpty()) ){
				errorMessage="E' necessario impostare almeno un distretto. ";
				getTemporary().put("errorMessage", errorMessage);
				return false;
			}
			
		} else if (type.equals("ulss")){
			if((tf.getUlss()==null || tf.getUlss().isEmpty()) ){
				errorMessage="E' necessario impostare almeno una ULSS. ";
				getTemporary().put("errorMessage", errorMessage);
				return false;
			}
			
		} else if (type.equals("distr")){
			
		}
		

		//controllo univocità nome, ma solo con lo stesso tagType
		//		String query_nomi = "SELECT fascicolo FROM TagFascicolo tf WHERE tf.tagType = (:tagType) and tf.fascicolo = (:name) and tf.internalId != (:id)";		
		//		Query query = ca.createHibernateQuery(query_nomi);
		//		query.setParameter("tagType", tagType);
		//		query.setParameter("name", tf.getFascicolo());
		//		query.setParameter("id", tf.getInternalId());

		//		@SuppressWarnings("unchecked")
		//		List<TagFascicolo> TFList = (List<TagFascicolo>)query.list();
		//
		//		if(!TFList.isEmpty()){
		//			errorMessage+="Esiste già un "+ tagType.getCurrentTranslation().toLowerCase() +" con lo stesso nome.";
		//			getTemporary().put("errorMessage", errorMessage);
		//			return false;	
		//		}

		// H00102183
		String query_nomi = "SELECT tf.fascicolo FROM TagFascicolo tf ";

		if (type.equals("gen") || type.equals("distr")) {
			query_nomi += "JOIN tf.distretti distretti WHERE distretti.internalId IN (:distretti) and ";
		}else if (type.equals("ulss")) {
			query_nomi += "JOIN tf.ulss ulss WHERE ulss.internalId IN (:ulss) and ";
		}else if (type.equals("reg")) {
			query_nomi += "WHERE ";
		}

		query_nomi += "tf.tagType = (:tagType) and tf.fascicolo = (:name) and ";

		Date endVal = tf.getEndValidity();
		if (endVal == null) {
			query_nomi += "(tf.endValidity is null or tf.endValidity >= (:startValidity)) and ";
		} else {
			query_nomi += "((tf.endValidity is null and tf.startValidity <= (:endValidity)) or " + 
					"(tf.startValidity <= (:endValidity) and tf.endValidity >= (:startValidity))) and ";
		}

		query_nomi += "tf.internalId != (:id)";

		Query query = ca.createHibernateQuery(query_nomi);
		query.setParameter("tagType", tagType);
		query.setParameter("name", tf.getFascicolo());
		query.setParameter("startValidity", tf.getStartValidity());
		if(endVal != null)
			query.setParameter("endValidity", endVal);
		query.setParameter("id", tf.getInternalId());

		if (type.equals("gen") || type.equals("distr")) {
			List<ServiceDeliveryLocation> distretti = tf.getDistretti();
			List<Long> distrIds = new ArrayList<Long>();
			for(ServiceDeliveryLocation sdl : distretti){
				distrIds.add(sdl.getInternalId());
			}
			query.setParameterList("distretti", distrIds);
		}else if (type.equals("ulss")) {
			List<ServiceDeliveryLocation> ulss = tf.getUlss();
			List<Long> ulssIds = new ArrayList<Long>();
			for(ServiceDeliveryLocation sdl : ulss){
				ulssIds.add(sdl.getInternalId());
			}
			query.setParameterList("ulss", ulssIds);
		}

		@SuppressWarnings("unchecked")
		List<String> TFList = (List<String>)query.list();

		if(!TFList.isEmpty()){
			errorMessage+="Esiste già un "+ tagType.getCurrentTranslation().toLowerCase() +" con lo stesso nome e periodo di validità sovrapponibile.";
			getTemporary().put("errorMessage", errorMessage);
			return false;	
		}

		return true;

	}
	
	public void setUlss() {

		try {
			getEqual().remove("distretti.internalId");
			getIn().remove("distretti.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("distretti.internalId", id);
				}else{
					UserBean ub = (UserBean) Component.getInstance("userBean");
					List<Long> sdLocs = ub.getSdLocs();
					((FilterMap)getIn()).put("distretti.internalId", sdLocs);
				}
			}
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} 
		
	}
	
	public String getUlssListAsString(){ 

		String result = "";
		int inc=0;
		if(getEntity()!=null && getEntity().getUlss()!=null){
		
			for(ServiceDeliveryLocation ulss : getEntity().getUlss()){
				if(inc>0)result+=", ";
				inc++;
				if(ulss.getName()!=null){
					result+=ulss.getName().getGiv();
				}else{
					result+="N.D."; 
				}
			}
		}
		return result;
	}
	
	public String getDistrettiListAsString(){ 

		String result = "";
		int inc=0;
		if(getEntity()!=null && getEntity().getDistretti()!=null){
		
			for(ServiceDeliveryLocation distretto : getEntity().getDistretti()){
				if(inc>0)result+=" - ";
				inc++;
				if(distretto.getName()!=null){
					result+=distretto.getName().getGiv();
				}else{
					result+="N.D."; 
				}
			}
		}
		return result;
	}
	
	public String getLineeListAsString(){

		String result = "";
		int inc=0;
		
		if(getEntity()!=null && getEntity().getLinee()!=null){
		
			for(ServiceDeliveryLocation linea : getEntity().getLinee()){
				
				if(inc>0)result+=" - ";
				inc++;
				if(linea.getName()!=null){//id HBS
					result += linea.getName().getGiv();
					
					if (linea.getParent()!=null && linea.getParent().getId("HBS")!=null){
						String code = linea.getParent().getId("HBS").getExtension();
						
						if (code!=null && !"".equals(code))
							result += " ("+code+")"; 
					}
				} else
					result += "N.D."; 
			}
		}
		
		return result;
	}
	
	//linee di lavoro selezionabili in base a distretti o ulss scelte, a seconda del tipo di fascicolo.
	public void addFilterForUos() {
		TagFascicolo tf = getEntity();
		if (tf == null)
			return;
		
		CodeValuePhi tagType = tf.getTagType();
		if(tagType==null) 
			return;
		
		String type = tagType.getCode();
		if (tagType == null)
			return;
		
		ServiceDeliveryLocationAction sdla = ServiceDeliveryLocationAction.instance();

		//se il pulsante per selezionare i distretti è visibile ma non è stato aggiunto nessun distretto
		if (type.equals("distr") || type.equals("gen")) {
			List<Long> filterId = new ArrayList<Long>();
			
			if(tf.getDistretti()!=null && !tf.getDistretti().isEmpty()){
				for(ServiceDeliveryLocation p : tf.getDistretti()){
					filterId.add(p.getInternalId());
				}
			}
			sdla.getIn().put("parent.internalId", filterId);
		}
		
		if (type.equals("ulss")) {
			List<Long> filterId = new ArrayList<Long>();
			
			if(tf.getUlss()!=null && !tf.getUlss().isEmpty()){
				for(ServiceDeliveryLocation p : tf.getUlss()){
					filterId.add(p.getInternalId());
				}
			}
			
			sdla.getIn().put("parent.parent.internalId", filterId);
		}

	}

	public List<Long> getDistrettiInternalIdList(){
				 
		List<Long> result = new ArrayList<Long>();
		
		if(getEntity()!=null && getEntity().getDistretti()!=null && !getEntity().getDistretti().isEmpty()){
			for(ServiceDeliveryLocation p : getEntity().getDistretti()){
				result.add(p.getInternalId());
			}
		}
		
		if(getEntity()!=null && getEntity().getUlss()!=null && !getEntity().getUlss().isEmpty()){
			for(ServiceDeliveryLocation p : getEntity().getUlss()){
				result.add(p.getInternalId());
			}
		}

		return result;
	}
	
	public void setDefaultTagType(){
		
		if(getEntity()==null) return;
		
		Vocabularies voc = VocabulariesImpl.instance();
		CodeValuePhi tagType = null;
		
		ParameterManager pm = ParameterManager.instance();

		try {
			String[] codeArr = {"gen", "distr", "ulss", "reg"};
			boolean disabledByparam = false;
			int iterator = 0;
			while(iterator<codeArr.length && tagType==null){
				String fullParamPath = "p.fascicolazione.editFascicolo.tipoFascicolo." + codeArr[iterator];
				String readOnly = 	pm.getParameter(fullParamPath, "readOnly").toString();
				disabledByparam = (readOnly!=null && "true".equals(readOnly))?true:false;
				if(!disabledByparam){
					tagType = (CodeValuePhi)voc.getCodeValue("PHIDIC", "tagType", codeArr[iterator], "C");
				}
				iterator++;
			}
			getEntity().setTagType(tagType);
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (DictionaryException e) {
			e.printStackTrace();
		} catch (PhiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
		UserBean ub = UserBean.instance();
		
		try {
			if(sua!=null && sua.getUocEntities()!=null && sua.getUocEntities().size()==1 && ub.getRole()!="admin"){
				List<ServiceDeliveryLocation> distretti = new ArrayList<ServiceDeliveryLocation>();
				ServiceDeliveryLocation baseDist = (ServiceDeliveryLocation) sua.getUocEntities().get(0).getValue();
				distretti.add(baseDist);
				getEntity().setDistretti(distretti);
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}	
	}
	
	public void readFascicoli(){
		try {
			CodeValuePhi  tagType	= (CodeValuePhi) getTemporary().get("tagType");
			String  fascicolo		= (String) getTemporary().get("fascicolo");
			
			Date from	= (Date)getTemporary().get("from");
			Date to 	= (Date)getTemporary().get("to");
			
			ServiceDeliveryLocation  distretto 	= (ServiceDeliveryLocation)getTemporary().get("distretto");
			ServiceDeliveryLocation  ldl		= (ServiceDeliveryLocation) getTemporary().get("ldl");

			Boolean nonAttivi	= (Boolean)getTemporary().get("nonAttivi")==null?false:(Boolean)getTemporary().get("nonAttivi");
			

			String qry = 	"SELECT DISTINCT tag FROM TagFascicolo tag " + 
					 		"LEFT JOIN tag.distretti dstr " +
					 		"LEFT JOIN tag.linee ldl " +
					 		"LEFT JOIN tag.ulss ulss " +
					 		"LEFT JOIN tag.tagType tagType " +
					 		"WHERE ";
			
			/* Se tipo: Fascicolo (gen) 	
			 * relazioni possibili:
			 * Fascicolo 0:1 Distretto
			 * Fascicolo 0:n ldl
			 * --    
			 * Se tipo: Progetto distrettuale (distr) 
			 * relazioni possibili:
			 * Fascicolo 0:n Distretto
			 * Fascicolo 0:n ldl
			 
			 * Visibile solo se l'utente è abilitato sul distretto E (nessuna ldl associata al fascicolo OPPURE sono abilitato ad almeno una di quelle ldl */
			qry += "( (tagType.code IN ('gen', 'distr') AND dstr.internalId IN (:sdlocs) AND (ldl.internalId IS NULL OR ldl.internalId IN (:sdlocs))) OR ";
			
			/* Se tipo: Progetto ULSS (ulss) 	
			 * relazioni possibili:
			 * Fascicolo 0:n ulss
			 * Fascicolo 0:n ldl
			 
			 * Visibile solo se l'utente è abilitato sulla ulss E (nessuna ldl associata al fascicolo OPPURE sono abilitato ad almeno una di quelle ldl */
			qry += "(tagType.code = 'ulss' AND ulss.internalId IN (:sdlocs) AND (ldl.internalId IS NULL OR ldl.internalId IN (:sdlocs))) OR ";			

			/* Se tipo: Progetto Regionale (reg) 	
			 * relazioni possibili:
			 * Fascicolo 0:n ldl
			 
			 * Visibile solo se nessuna ldl associata al fascicolo OPPURE sono abilitato ad almeno una di quelle ldl */
			qry += "(tagType.code = 'reg' AND ldl.internalId IS NULL OR ldl.internalId IN (:sdlocs)) ) AND ";
			
			//Filtra per tipo fascicolo OPPURE esclude i fascicoli privi di tipo (dati sporchi)
			if (tagType!=null && tagType.getCode()!=null && !"".equals(tagType.getCode()))
				qry += "tagType.code ='" + tagType.getCode() + "' AND ";
			else 
				qry += "tagType.code IS NOT NULL AND ";
			
			//Filtra per nome fascicolo OPPURE esclude i fascicoli senza nome (dati sporchi)
			if (fascicolo!=null && !"".equals(fascicolo))
				qry += "tag.fascicolo like '%" + fascicolo + "%' AND ";
			else 
				qry += "tag.fascicolo IS NOT NULL AND tag.fascicolo !='' AND ";
			
			//Filtra sull'intervallo
			if(from != null)
				qry += "tag.startValidity IS NOT NULL AND tag.startValidity >= :from AND ";
			
			if(to != null)
				qry += "tag.startValidity IS NOT NULL AND tag.startValidity <= :to AND ";
			
			
			//Visualizza solo quelli attivi
			if (!nonAttivi)
				qry += "(tag.endValidity IS NULL OR tag.endValidity > :oggi) AND ";
			
			//Filtra per distretto selezionato
			if (distretto!=null){
				Long idDistretto = distretto.getInternalId();
				qry += "dstr.internalId = " + idDistretto + " AND ";
			}
			
			//Filtra per ldl selezionata
			if (ldl!=null){
				Long idLdl = ldl.getInternalId();
				qry += "ldl.internalId = " + idLdl + " AND ";
			}
			
			qry += "tag.isActive = true ";
			
			//Ordina per data di inizio validità
			qry += "order by tag.startValidity desc";
			
			/* Ripulisce coda
			if (qry.endsWith(" WHERE "))
				qry = qry.substring(0, qry.length() - 7);
			else if (qry.endsWith(" AND "))
				qry = qry.substring(0, qry.length() - 5);
			else if (qry.endsWith(" OR "))
				qry = qry.substring(0, qry.length() - 4); */
			
			javax.persistence.Query tags = ca.createQuery(qry);
			
			if (qry.contains(":from"))
				tags.setParameter("from", from);
			
			if (qry.contains(":to"))
				tags.setParameter("to", to);
			
			if (!nonAttivi){
				Date oggi = new Date();
				tags.setParameter("oggi", oggi);
			}
				
			
			UserBean ub = UserBean.instance();
			tags.setParameter("sdlocs",  ub.getSdLocs());
			
			@SuppressWarnings("unchecked")
			List<TagFascicolo> tagsList = tags.getResultList();
			
			if(tagsList!=null && !tagsList.isEmpty())
				this.injectList(tagsList);
			else {
				this.eject();
				this.ejectList();
			}	

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	public void readFascicoli4Suggestion(){
 		Object fas = getTemporary().get("fascicolo");
		getTemporary().put("fascicolo",getLike().get("fascicolo"));
		readFascicoli();
		IdataModel<TagFascicolo> im = (IdataModel<TagFascicolo>) Contexts.getConversationContext().get("TagFascicoloList");
		List<Map<String, Object>> lista = new ArrayList<Map<String,Object>>();
		if(im!=null && im.getList()!=null){
			for(TagFascicolo t : im.getList()){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("internalId", t.getInternalId());
				m.put("fascicolo", t.getFascicolo());
				m.put("entityClass", entityClass.getName());
				lista.add(m);
			}
		}
		getTemporary().put("fascicolo",fas);
		injectList(lista);
	}
	
	public List<SelectItem> getParameterizedList() throws Exception{
		List<SelectItem> out = new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();
		
		out = voc.getIdValues("PHIDIC:tagtype");
		
		ParameterManager pm = ParameterManager.instance();
		TagFascicolo tf = getEntity();
		String currentCode = "";
		if(tf.getTagType()!=null){
			currentCode = tf.getTagType().getCode();
		}
		
		String readOnly = "";
		
		for(SelectItem si : out){
			String code = ((CodeValuePhi)si.getValue()).getCode();
			String fullParamPath = "p.fascicolazione.editFascicolo.tipoFascicolo." + code;
			Object param = pm.getParameter(fullParamPath, "readOnly");
			if(param == null){
				continue;
			}
			if(param != null){
				readOnly = 	param.toString();
			}
			boolean disabledByparam = (readOnly!=null && "true".equals(readOnly))?true:false;
			
			boolean disableByHierarchy = false;
			if("gen".equals(currentCode) && !(code.equals("gen"))){
				disableByHierarchy = true;
			}
			if("distr".equals(currentCode) && code.equals("gen")){
				disableByHierarchy = true;
			}
			if("ulss".equals(currentCode) && (code.equals("gen") || code.equals("distr"))){
				disableByHierarchy = true;
			}
			if("reg".equals(currentCode) && (code.equals("gen") || code.equals("distr") || code.equals("ulss"))){
				disableByHierarchy = true;
			}
			
			boolean disabled = false;
			if(disabledByparam || disableByHierarchy){
				disabled = true;
			}
			si.setDisabled(disabled);
		}
		
		return out;
	}

	public void refreshAndLink(Procpratiche prat, List<TagFascicolo> lst) {

		if (lst == null) 
			return;

		for (TagFascicolo tag : lst) {
			if(tag.getInternalId()>0L){
				ca.refreshIfNeeded(tag);
			}
		}
		
		
		if(prat!=null){
			for (TagFascicolo tag : lst) {
				prat.addTagFascicolo(tag);
				tag.addProcpratiche(prat);
			}
		}
	}
}