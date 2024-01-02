package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.baseEntity.Aulss;
import com.phi.entities.baseEntity.Comune;
import com.phi.entities.baseEntity.DestinatariNotifiche;
import com.phi.entities.baseEntity.DestinatariSpisal;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.actions.BaseAction;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AulssAction")
@Scope(ScopeType.CONVERSATION)
public class AulssAction extends BaseAction<Aulss, Long> {

	private static final long serialVersionUID = 636024331L;

	public static AulssAction instance() {
		return (AulssAction) Component.getInstance(AulssAction.class, ScopeType.CONVERSATION);
	}

	public Boolean checkSelected(List<Aulss> aulssList) throws Exception {	
		if (aulssList.isEmpty())
			return false;

		for (Aulss c : aulssList) {
			if (c.getSel()!=null && c.getSel())
				return true;
		}

		return false;

	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getUlss() {

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String countULSS = "SELECT sdl FROM ServiceDeliveryLocation sdl " +
				"WHERE sdl.code.code = 'ULSS'";
		org.hibernate.Query qry = ca.createHibernateQuery(countULSS);

		List<ServiceDeliveryLocation> lst = qry.list();


		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		for (ServiceDeliveryLocation row : lst) {
			selectItems.add(new SelectItem(row.getInternalId(), row.getName().getGiv()));
		}

		return selectItems;
	}

	public Boolean exist(List<Aulss> aulssList, Aulss newAulss) throws Exception {
		ServiceDeliveryLocation sdl = newAulss.getServiceDeliveryLocation();

		if (sdl!=null)
			return this.exist(aulssList, sdl);

		return false;
	}

	public Boolean exist(List<Aulss> aulssList, ServiceDeliveryLocation sdl) throws Exception {	
		if (aulssList.isEmpty())
			return false;

		for (Aulss c : aulssList) {
			if (c.getServiceDeliveryLocation().equals(sdl))
				return true;
		}

		return false;

	}

	public void addUlss(List<Aulss> aulssList, DestinatariSpisal destinatariSpisal) throws Exception {	

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, (Long)getTemporary().get("ulss"));

		if (sdl != null && !exist(aulssList, sdl)){
			Aulss c = new Aulss();
			c.setServiceDeliveryLocation(sdl);
			c.setDestinatariSpisal(destinatariSpisal);
			ca.create(c);

			ca.flushSession();
			getTemporary().remove("ulss");
		}
	}

	public void selectDeselect(ArrayList<Aulss> aulssList) throws Exception {	
		if (aulssList.isEmpty())
			return;

		Boolean select = getTemporary().get("select")==null?false:(Boolean)getTemporary().get("select");

		for (Aulss c : aulssList){
			c.setSel(select);
		}
	}

	public void deleteUlss(List<Aulss> aulssList, DestinatariSpisal destinatariSipral) throws Exception {	
		if (aulssList.isEmpty())
			return;

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		for (Aulss c : aulssList) {
			if (c.getSel()!=null && c.getSel()){
				destinatariSipral.removeAulss(c);
				c.setDestinatariSpisal(null);
				ca.delete(c);
			}
		}

		ca.flushSession();
		getTemporary().remove("select");
		getTemporary().remove("delCascade");
	}

	public void expireAulss(ArrayList<Aulss> aulssList) throws Exception {	
		if (aulssList.isEmpty())
			return;

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		Date endValidity = (Date)getTemporary().get("endValidity");

		for (Aulss c : aulssList) {
			if (c.getSel()!=null && c.getSel()){
				c.setFineValidita(endValidity);
				c.setSel(false);

				ca.create(c);
			}
		}

		ca.flushSession();
		getTemporary().remove("endValidity");
		getTemporary().remove("select");

	}

	public void deleteCascade(List<Aulss> aulssList, DestinatariSpisal destinatariSipral) throws Exception {	
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		if(aulssList!=null && !aulssList.isEmpty()){
			for (Aulss c : aulssList) {
				destinatariSipral.removeAulss(c);
				c.setDestinatariSpisal(null);
				ca.delete(c);
			}
		}
		ca.delete(destinatariSipral);

		ca.flushSession();
	}

}