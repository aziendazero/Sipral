package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.Sedi;
//import com.phi.entities.baseEntity.SediAddebito;
import com.phi.entities.dataTypes.AD;

@BypassInterceptors
@Name("IndirizzoSpedAction")
@Scope(ScopeType.CONVERSATION)
public class IndirizzoSpedAction extends BaseAction<IndirizzoSped, Long> {

	private static final long serialVersionUID = 508683972L;
	private static final Logger log = Logger.getLogger(IndirizzoSpedAction.class);

	public static IndirizzoSpedAction instance() {
		return (IndirizzoSpedAction) Component.getInstance(IndirizzoSpedAction.class, ScopeType.CONVERSATION);
	}

	public IndirizzoSped copy(IndirizzoSped toCopy){
		try{
			IndirizzoSped copy = new IndirizzoSped();
			copy.setIsActive(false);
			copy.setCopy(true);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());
			
			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());
			
			copy.setDenominazione(toCopy.getDenominazione());
			copy.setNote(toCopy.getNote());
			copy.setPrincipale(toCopy.getPrincipale());
			copy.setStato(toCopy.getStato());
			
			//this.create();
			
			return copy;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/* I0070276 sede addebito sostituita da sede con flag addebito
	public IndirizzoSped injectFromExistingSa(SediAddebito sa) {
		
		if (sa == null ) { 
			log.error("impossibile creare indirizzo da sede addebito null");
			return null;
		}
		
		IndirizzoSped i = new IndirizzoSped();
		if (sa.getAddr() != null){
			i.setAddr(sa.getAddr().cloneAd());
		}
		if (sa.getTelecom() != null){
			i.setTelecom(sa.getTelecom().cloneTel());
		}
		if (sa.getDenominazione() != null){
			i.setDenominazione(sa.getDenominazione());
		}
		
		i.setStato(sa.getStato());
		
		//inject(i);
		return i;
	}*/
	
	public IndirizzoSped injectFromExistingSede(Sedi sede) {
		
		if (sede == null ) {
			log.error("impossibile creare indirizzo da sede nulla");
			return null;
		}
		
		IndirizzoSped i = new IndirizzoSped();
		if (sede.getAddr() != null){
			i.setAddr(sede.getAddr().cloneAd());
		}
		/*
		 * la sede ha solo una mail che viene salvata in telecom.mail, ma è la PEC
		 * quindi cloneTel non va bene del tutto
		 */
		if (sede.getTelecom()!=null){
			i.setTelecom(sede.getTelecom().cloneTel());
			//i.getTelecom().setMc(i.getTelecom().getMail());
			//i.getTelecom().setMail(null);
		}
		if (sede.getDenominazioneUnitaLocale() != null){
			i.setDenominazione(sede.getDenominazioneUnitaLocale());
		}
		
		i.setStato(sede.getStato());
		
		inject(i);
		return i;
	}
	
	public void injectNewList(IndirizzoSped i) {
		List<IndirizzoSped> list = new ArrayList<IndirizzoSped>();
		list.add(i);
		injectList(list);
	}
	
	public boolean checkExisting(IndirizzoSped ind) {
		
		boolean ret = false;
		
		if (ind == null || ind.getAddr()==null)
			return ret;
		
		//via IndirizzoSped.addr.str
		//numero IndirizzoSped.addr.bnr
		//città IndirizzoSped.addr.cty
		String hqlNameSurname = "SELECT COUNT(ind) FROM IndirizzoSped ind WHERE ind.isActive=1 AND LOWER(ind.addr.str) = :via AND LOWER(ind.addr.bnr) = :numero AND ind.addr.cty = :citta";
		Query q = ca.createQuery(hqlNameSurname);
		
		if(ind.getAddr().getStr()!=null)		
			q.setParameter("via", ind.getAddr().getStr().toLowerCase());
		else
			q.setParameter("via", "");

			
		if(ind.getAddr().getBnr()!=null)		
			q.setParameter("numero", ind.getAddr().getBnr().toLowerCase());
		else
			q.setParameter("numero", "");
	
		if(ind.getAddr().getCty()!=null)		
			q.setParameter("citta", ind.getAddr().getCty());
		else
			q.setParameter("citta", "");
		
		Long count = (Long)q.getSingleResult();
		
		if(count!=null && count>0)
			return true;
		
		return ret;
			
	}
	
	public void setPrincipale( List<IndirizzoSped> lista, IndirizzoSped toBePrincipale) throws PhiException{
	
		if (lista == null || toBePrincipale == null) {
			return;
		}
		
		toBePrincipale.setPrincipale(true);
		create(toBePrincipale);
		
		for (IndirizzoSped i : lista) {
			if (toBePrincipale.getInternalId() == i.getInternalId()) {
				continue;
			}

			if (i.getPrincipale() != null && i.getPrincipale()) {
				i.setPrincipale(false);
				create(i);
			}
			
		}
		
		
	}
	
	public String streetToString(String str, String bnr) {
		AD addr = new AD("", bnr, "", "", "", "", "", "", str, "");
		return addr.streetToString();
	}
	
	public String streetToString(Object o) {
		if (o instanceof HashMap) {
			HashMap<String,String> addr = (HashMap<String,String>) o;
			AD address = new AD("", addr.get("bnr"), "", "", "", "", "", "", addr.get("str"), "");
			return address.streetToString();
		}
		//entityCriteria.setProjection(Projections.projectionList().add(Projections.property("sediAddebito").as("sediAddebitoCount")));
		return ""; 
	}
	
//	//Execute the count of child object
//	//Working only with select Restrictions
//	public void readWithCount(String relationship) {
//		
//		String alias = relationship.substring(0,1).toUpperCase() + relationship.substring(1);
//		Criteria subcriteria = findSubCriteria(alias);
//		if (subcriteria == null) {
//			entityCriteria.createAlias(relationship, alias,Criteria.LEFT_JOIN);
//		}
//		
//		//Reset all projections, using configured select and related 'child' to be counted 
//		ProjectionList pl = Projections.projectionList();
//		pl.add(Projections.alias(Projections.count(alias+".internalId"), relationship+"Count"))
//		  .add(Projections.alias(Projections.groupProperty("this.internalId"), "internalId"));
//		
//		List<String> projectionAliases = Arrays.asList(pl.getAliases());
//		for (String s : select) {
//			if (!projectionAliases.contains(s)){
//				pl.add(Projections.alias(Projections.groupProperty("this."+s), s));
//			}
//		}
//		
//		entityCriteria.setProjection(pl); 
//		entityCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(entityClass));
//		
//		//FIXME: Hibernate resolve query using query alias (AS) of columns like _y1, _y3 in query clause.
//		//these alias are not found in query, because restriction are defined without prefix this. 
//		//to prefix this. to current criterion, can be done only with reflection.
//		//criterion with this. prefix are removed (query can be repeated), only the current 'new' criterion are pre-fixed.
//		//removing this. criterions, or prefix anywhere a this. is not tested overall, not working with distinct, in clause, and other.
//		
//		Iterator<CriterionEntry> criterions = ((CriteriaImpl)entityCriteria).iterateExpressionEntries();
//		while (criterions.hasNext()) {
//			CriterionEntry critEnt = criterions.next();
//			Criterion crit = critEnt.getCriterion();
//			
//	        try {
//				Field field = crit.getClass().getDeclaredField("propertyName");
//				field.setAccessible(true);
//		        String expressionPropertyName = (String)field.get(crit);
//		        if (expressionPropertyName.startsWith("this.")) {
//		        	criterions.remove();
//		        }
//		        else {
//		        	field.set(crit, "this."+expressionPropertyName);
//		        	//System.out.println("criterion TOSTRING: "+crit.toString()+ "  "+ expressionPropertyName + " changed with   this."+ expressionPropertyName);
//		        }
//			        
//			} catch (Exception e) {
//				log.error("error executing counted read:   "+e.getMessage());
//				e.printStackTrace();
//			} 
//	        
//	        
//		}
//		
//		try {
//			read();
//		} catch (PhiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return;
//		
//		
//	}
	
	class IndirizzoSpedCount {
		long internalId;
		long sediCount;
	}
	
}