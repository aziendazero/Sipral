package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Comune;
import com.phi.entities.baseEntity.DestinatariNotifiche;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ComuneAction")
@Scope(ScopeType.CONVERSATION)
public class ComuneAction extends BaseAction<Comune, Long> {

	private static final long serialVersionUID = 146429752L;

	public static ComuneAction instance() {
		return (ComuneAction) Component.getInstance(ComuneAction.class, ScopeType.CONVERSATION);
	}

	public Boolean checkSelected(List<Comune> comuneList) throws Exception {	
		if (comuneList.isEmpty())
			return false;

		for (Comune c : comuneList) {
			if (c.getSel()!=null && c.getSel())
				return true;
		}

		return false;

	}
	
	public Boolean exist(List<Comune> comuneList, Comune newComune) throws Exception {
		CodeValueCity c = newComune.getComune();
		
		if (c!=null)
			return this.exist(comuneList, c);
			
		return false;
	}
	
	public Boolean exist(List<Comune> comuneList, CodeValueCity newComune) throws Exception {	
		if (comuneList.isEmpty())
			return false;

		for (Comune c : comuneList) {
			if (c.getComune().equals(newComune))
				return true;
		}

		return false;

	}
	
	
	public void deleteComuni(List<Comune> comuneList, DestinatariNotifiche destinatariNotifica) throws Exception {	
		if (comuneList.isEmpty())
			return;
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		for (Comune c : comuneList) {
			if (c.getSel()!=null && c.getSel()){
				destinatariNotifica.removeComuni(c);
				c.setDestinatariNotifiche(null);
				ca.delete(c);
				//System.out.println("Delete city: " + c.getComune().getDisplayName());
			}
		}
				
		ca.flushSession();
		getTemporary().remove("select");
	}
	
	public void expireComuni(ArrayList<Comune> comuneList) throws Exception {	
		if (comuneList.isEmpty())
			return;
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		Date endValidity = (Date)getTemporary().get("endValidity");
		
		//if (endValidity!=null){
			for (Comune c : comuneList) {
				if (c.getSel()!=null && c.getSel()){
					c.setFineValidita(endValidity);
					c.setSel(false);
										
					ca.create(c);
					//System.out.println("Expire city: " + c.getComune().getDisplayName());
				}
			}
			
			ca.flushSession();
			getTemporary().remove("endValidity");
			getTemporary().remove("select");
		//}	
		
	}
	
	public void addProvincia(List<Comune> comuneList, DestinatariNotifiche destinatariNotifica) throws Exception {	
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		CodeValueCity cap = (CodeValueCity)getTemporary().get("provincia");
		
		if (cap != null && cap.getParent()!=null && cap.getParent().getChildren()!=null){
			
			Collection<CodeValue> cities = cap.getParent().getChildren();
			Iterator<CodeValue> iterator = cities.iterator();
	        
	        while (iterator.hasNext()) {
	        	CodeValueCity city = (CodeValueCity)iterator.next();
	        	
	        	if (city.getValidTo()==null && !exist(comuneList, city)){
	        		//System.out.println("Create city: " + city.getDisplayName());
		        	
		        	Comune c = new Comune();
		        	c.setComune(city);
		        	c.setDestinatariNotifiche(destinatariNotifica);
		        	ca.create(c);
	        	}
	        }
	        			
			ca.flushSession();
			getTemporary().remove("provincia");
		}
	}
	
	public void addRegione(List<Comune> comuneList, DestinatariNotifiche destinatariNotifica) throws Exception {	
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue regione = voc.getCodeValueOid("comuniistat.05");
		Collection<CodeValue> capoluoghi = regione.getChildren();
		Iterator<CodeValue> itCap = capoluoghi.iterator();
		
		while (itCap.hasNext()) {
			CodeValueCity capoluogo = (CodeValueCity)itCap.next();
			Collection<CodeValue> citta = capoluogo.getChildren();
			Iterator<CodeValue> itCit = citta.iterator();
			while (itCit.hasNext()) {
				
				CodeValueCity city = (CodeValueCity)itCit.next();
	        	
	        	if (city.getValidTo()==null && !exist(comuneList, city)){
	        		//System.out.println("Create city: " + city.getDisplayName());
		        	
		        	Comune c = new Comune();
		        	c.setComune(city);
		        	c.setDestinatariNotifiche(destinatariNotifica);
		        	ca.create(c);
	        	}
				
			}
			
			ca.flushSession();
		}
		
	}
	
	public void selectDeselect(ArrayList<Comune> comuneList) throws Exception {	
		if (comuneList.isEmpty())
			return;

		Boolean select = getTemporary().get("select")==null?false:(Boolean)getTemporary().get("select");
		
		for (Comune c : comuneList){
			c.setSel(select);
			//System.out.println("Select city: " + c.getComune().getDisplayName());
		}
	}

	
}