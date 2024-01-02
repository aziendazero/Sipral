package com.phi.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("spisalUserAction")
@Scope(ScopeType.CONVERSATION)
public class SpisalUserAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5843970326108193637L;
	private static final Logger log = Logger.getLogger(SpisalUserAction.class);
	
	private UserBean ub = (UserBean) Component.getInstance("userBean");
	protected CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
	
	private static String count = "SELECT COUNT(sdl) FROM ServiceDeliveryLocation sdl " +
			"WHERE sdl.code.code = :code AND sdl.internalId IN (:sdLocs)";
	
	private static String ulss = "SELECT sdl.internalId, sdl.name.giv FROM ServiceDeliveryLocation sdl " + 
			"WHERE sdl.code.code = 'ULSS' AND sdl.internalId IN (:sdLocs)";
	
	private static String arpav = "SELECT sdl.internalId, sdl.name.giv FROM ServiceDeliveryLocation sdl " + 
			"WHERE sdl.code.code = 'ARPAV' AND sdl.internalId IN (:sdLocs)";
	
	private static String ulssEntities = "SELECT sdl FROM ServiceDeliveryLocation sdl " + 
			"WHERE sdl.code.code = 'ULSS' AND sdl.internalId IN (:sdLocs)";

	private static String uos = "SELECT sdl FROM ServiceDeliveryLocation sdl " + 
			"WHERE sdl.code.code = 'UOS' AND sdl.internalId IN (:sdLocs)";
	
	private static String uoc = "SELECT sdl FROM ServiceDeliveryLocation sdl " + 
			"WHERE sdl.code.code = 'UOC' AND sdl.internalId IN (:sdLocs)";
	
	private static String organizations = "SELECT o.internalId, o.name.giv FROM ServiceDeliveryLocation sdl " +
			"JOIN sdl.organization o " + 
			"WHERE sdl.code.code = 'ULSS' AND sdl.internalId IN (:sdLocs)";
	
	private ServiceDeliveryLocation lastSelectedUlss=null;
	private ServiceDeliveryLocation lastSelectedUoc=null;
	private ServiceDeliveryLocation lastSelectedUos=null;
	
	
	public boolean show(String code) throws PersistenceException{
		try {
			boolean ret = true;
			List<Long> sdLocs = ub.getSdLocs();
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(count);
				
				qry.setParameter("code", code);
				qry.setParameterList("sdLocs", sdLocs);
				List<Long> count = (List<Long>)qry.list();
				
				if (count.get(0) < 2)
					ret = false;
			}
			
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public List<SelectItem> getUlss() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(ulss);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<Object[]> res = qry.list();
				
				for (Object[] ulss : res) {
					selItem.add(new SelectItem(ulss[0], ulss[1].toString()));
				}
			}
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public List<SelectItem> getUoc() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(uoc);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				for (ServiceDeliveryLocation distretto : res) {
					selItem.add(new SelectItem(distretto.getInternalId(), distretto.getName().getGiv()));
				}
			}
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	
	public List<SelectItem> getUocEntities() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(uoc);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				for (ServiceDeliveryLocation distretto : res) {
					selItem.add(new SelectItem(distretto, distretto.getName().getGiv()));
				}
			}
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public List<ServiceDeliveryLocation> getDistretti() throws PersistenceException {
		try {
			List<ServiceDeliveryLocation> res = new ArrayList<ServiceDeliveryLocation>();
			
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(uoc);
				qry.setParameterList("sdLocs", sdLocs);
				
				res = qry.list();
				
			}
			
			return res;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	
	public List<SelectItem> getArpav() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(arpav);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<Object[]> res = qry.list();
				
				for (Object[] arpav : res) {
					selItem.add(new SelectItem(arpav[0], arpav[1].toString()));
				}
			}
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public boolean isArpav() throws PersistenceException {
		try {
			List<SelectItem> selItem = this.getArpav();
			if (selItem==null || selItem.size()<1)
				return false;
			
			return true;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public List<SelectItem> getUlssEntities() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(ulssEntities);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				for (ServiceDeliveryLocation ulss : res) { 
					if(ulss.getChildren()!=null && !ulss.getChildren().isEmpty() && ulss.getChildren().get(0)!=null){
						try{
							selItem.add(new SelectItem(ulss.getChildren().get(0), ulss.getChildren().get(0).getName().getGiv()));
						}catch(Exception e){
							log.error("SpisalUserAction.java getUlssEntities ERROR: a sdLoc without useful data has been read.");
						}
					}
				}
			}
			
			lastSelectedUlss=null;
			lastSelectedUoc=null;
			lastSelectedUos=null;
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public ServiceDeliveryLocation getFirstEnabledUlss(){
		
		try {
			
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(ulssEntities);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				
				if(res.size()>0)
					return res.get(0);
				else
					return null;
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
		return null; 
		
	}
	
	
	public List<SelectItem> getEnabledUOS() throws PersistenceException {
		try {
			
			
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(uos);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				
				
				for (ServiceDeliveryLocation uos : res) {
					selItem.add(new SelectItem(uos, uos.getName().getGiv()));
				}
			}
			
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public ServiceDeliveryLocation getFirstEnabledUos(){
		
		try {
			
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(uos);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<ServiceDeliveryLocation> res = qry.list();
				
				
				if(res.size()>0)
					return res.get(0);
				else
					return null;
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
		return null; 
		
	}
	
	public List<SelectItem> getUosFromSelectedUoc(ServiceDeliveryLocation selectedUoc) throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			
			if(lastSelectedUoc==null) {
				//prima selezione uos dopo aver selezionato una uoc
				lastSelectedUoc=selectedUoc;
			}else{
				//dalla seconda in poi
				selectedUoc=lastSelectedUoc;
			}
			
			lastSelectedUos=null;
			
			
			if(selectedUoc==null){
				return selItem;
			}
				
			List<ServiceDeliveryLocation> res = selectedUoc.getChildren();
			
			for (ServiceDeliveryLocation uos : res) {
				selItem.add(new SelectItem(uos, uos.getName().getGiv()));
			}

			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	public List<SelectItem> getOrganizations() throws PersistenceException {
		try {
			List<SelectItem> selItem = new ArrayList<SelectItem>();
			List<Long> sdLocs = ub.getSdLocs();			
			
			if (sdLocs != null && sdLocs.size()>1) {
				Query qry = ca.createHibernateQuery(organizations);
				qry.setParameterList("sdLocs", sdLocs);
				
				List<Object[]> res = qry.list();
				
				for (Object[] ulss : res) {
					selItem.add(new SelectItem(ulss[0], ulss[1].toString()));
				}
			}
			
			return selItem;
		
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
}
