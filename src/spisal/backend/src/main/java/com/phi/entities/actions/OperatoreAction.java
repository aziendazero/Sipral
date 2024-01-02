package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.paging.LazyList;
import com.phi.entities.baseEntity.MonteOre;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("OperatoreAction")
@Scope(ScopeType.CONVERSATION)
public class OperatoreAction extends BaseAction<Operatore, Long> {
	
	private static final long serialVersionUID = 7597173679851444582L;
	
	private static String GET_RDP = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.rdpOf sdl " +
			"WHERE sdl.internalId =:internalID";
	
	private static String GET_RDP_SUPERVISION = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.vigilanzaRdpType t " +
			"WHERE t.serviceDeliveryLocation =:sdlID " +
			"AND t.vigilanzaType =:vType";

	private static String GET_DIRECTOR = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.directorOf sdl " +
			"WHERE sdl.internalId =:internalID";
	
	public static OperatoreAction instance() {
		return (OperatoreAction) Component.getInstance(OperatoreAction.class, ScopeType.CONVERSATION);
	}
	
    private static final Logger log = Logger.getLogger(OperatoreAction.class);

	public void setFromEmployee(Employee employee, String ente){
		try{
			Operatore operatore = getEntity();
			
			operatore.setName(employee.getName());
			operatore.setEmployee(employee);
			
			this.setCodeValue("ente","PHIDIC","Ente", ente);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void setUlss() {
		try {
			getEqual().remove("serviceDeliveryLocation.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.internalId", id);
				}
			}
			
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
	
	public String getDirector(ServiceDeliveryLocation sdl) {
		try{
			String ret = "";
			if (sdl == null)
				return ret;

			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", sdl.getInternalId());

			List<Operatore> operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_DIRECTOR, parameters);
				
			if (operatori!=null && operatori.size()>0){
				Operatore dir = operatori.get(0);
					
				if (dir.getName()!=null)
					ret += dir.getName().getFam() + " " + dir.getName().getGiv();
					
				if (dir.getEmployee()!=null && dir.getEmployee().getFiscalCode()!=null)
					ret += " (CF: " +  dir.getEmployee().getFiscalCode() + ")";
			}
				
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String getRDP(ServiceDeliveryLocation sdl) {
		try{
			String ret = "";
			if (sdl == null)
				return ret;

			//Search RDP
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", sdl.getInternalId());
			List<Operatore> operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_RDP, parameters);
				
			if (operatori!=null && operatori.size()>0){
				Operatore rdp = operatori.get(0);
				
								
				if (rdp.getName()!=null)
					ret += rdp.getName().getFam() + " " + rdp.getName().getGiv();
					
				if (rdp.getEmployee()!=null && rdp.getEmployee().getFiscalCode()!=null)
					ret += " (CF: " +  rdp.getEmployee().getFiscalCode() + ")";
			
			} else {
				//Show director as RDP
				parameters= new HashMap<String, Object>(2);
				parameters.put("internalID", sdl.getParent().getInternalId());
				operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_DIRECTOR, parameters);
					
				if (operatori!=null && operatori.size()>0){
					Operatore dir = operatori.get(0);
						
					if (dir.getName()!=null)
						ret += dir.getName().getFam() + " " + dir.getName().getGiv();
						
					if (dir.getEmployee()!=null && dir.getEmployee().getFiscalCode()!=null)
						ret += " (CF: " +  dir.getEmployee().getFiscalCode() + ")";
				}
			}
			
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getRDPSupervision(ServiceDeliveryLocation sdl, CodeValuePhi type) {
		try{
			String ret = "";
			if (sdl == null)
				return ret;

			//Search RDP
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("sdlID", sdl);
			parameters.put("vType", type);
			List<Operatore> operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_RDP_SUPERVISION, parameters);
				
			if (operatori!=null && operatori.size()>0){
				Operatore rdp = operatori.get(0);
				
								
				if (rdp.getName()!=null)
					ret += rdp.getName().getFam() + " " + rdp.getName().getGiv();
					
				if (rdp.getEmployee()!=null && rdp.getEmployee().getFiscalCode()!=null)
					ret += " (CF: " +  rdp.getEmployee().getFiscalCode() + ")";
			
			} else {
				//Show director as RDP
				parameters= new HashMap<String, Object>(2);
				parameters.put("internalID", sdl.getParent().getInternalId());
				operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_DIRECTOR, parameters);
					
				if (operatori!=null && operatori.size()>0){
					Operatore dir = operatori.get(0);
						
					if (dir.getName()!=null)
						ret += dir.getName().getFam() + " " + dir.getName().getGiv();
						
					if (dir.getEmployee()!=null && dir.getEmployee().getFiscalCode()!=null)
						ret += " (CF: " +  dir.getEmployee().getFiscalCode() + ")";
				}
			}
			
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public void filterActive() {
		Boolean addNotActive=false;
		Object addNonActive_ = this.getTemporary().get("addNotActive");

		if (addNonActive_!=null && (Boolean)addNonActive_)
			addNotActive=true;
		
		if(addNotActive){
			((FilterMap)getEqual()).remove("isActive");
		}else{
			((FilterMap)getEqual()).put("isActive", true);
		}
	}	
	
	public void linkUnlinkMonteOre(List<MonteOre> link, List<MonteOre> unlink){
		if(entity==null)
			return;
		
		if(entity.getMonteOre()==null)
			entity.setMonteOre(new ArrayList<MonteOre>());
		
		List<MonteOre> lista = entity.getMonteOre();
		if(link!=null){			
			for(MonteOre mo : link){
				if(!lista.contains(mo)){
					entity.getMonteOre().add(mo);
					mo.setOperatore(entity);
				}

			}
		}
		
		if(unlink!=null){
			for(MonteOre mo : unlink){
				if(lista.contains(mo)){
					entity.getMonteOre().remove(mo);
					mo.setOperatore(null);
				}
			}
		}
	}
	
	public void setMonteOreMulti(MonteOre mo, LazyList operatori){
		try {
			if(mo==null || mo.getAnno()==null || operatori==null || operatori.size()<1)
				return;
			
			String tmp = "";
			Integer anno = mo.getAnno();
			
			for (Map operatore : (List<Map>)operatori) {
				if (operatore.get("isNew")!=null && (Boolean)operatore.get("isNew")){
					Operatore op = (Operatore)ca.get(Operatore.class, (Long)operatore.get("internalId"));
					
					Boolean toCreate = true;
					
					if(op.getMonteOre()==null)
						op.setMonteOre(new ArrayList<MonteOre>());
					else {
						Boolean exit = false;
						
						for (MonteOre m : op.getMonteOre()) {
							if (!exit && m!=null && m.getAnno()!=null && m.getAnno().equals(anno)){
								tmp += "<p><b>" + op.getName() + "</b> (" + op.getCode() + ")</p><br>";
								toCreate=false;
								exit = true;
							}
						}
					}
					
					if (toCreate){
						MonteOre moNew = new MonteOre();
						
						moNew.setAnno(mo.getAnno());
						moNew.setHhContrattuali(mo.getHhContrattuali());
						moNew.setHhScomputo(mo.getHhScomputo());
						moNew.setMotivoScomputo(mo.getMotivoScomputo());
						
						op.getMonteOre().add(moNew);
						moNew.setOperatore(op);
						
						this.create(op);
					}
				}
			}
			
			if (!"".equals(tmp))
				this.temporary.put("createMultiInfo", tmp);
			else 
				this.temporary.remove("createMultiInfo");
				
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
	/* Per ogni operatore, copia il monte (op.getMonteOre()) più prossimo all'anno indicato (mo.getAnno()) */
	public void copyMonteOreMulti(MonteOre mo, LazyList operatori){
		try {
			
			if(mo==null || mo.getAnno()==null || operatori==null || operatori.size()<1)
				return;
			
			String tmp = "";
			Integer anno = mo.getAnno();
						
			for (Map operatore : (List<Map>)operatori) {
				
				//Se l'operatore è selezionato
				if (operatore.get("isNew")!=null && (Boolean)operatore.get("isNew")){
					Operatore op = (Operatore)ca.get(Operatore.class, (Long)operatore.get("internalId"));
					
					MonteOre nearestLeft = null;
					MonteOre nearestRight = null;
					
					if(op.getMonteOre()==null || op.getMonteOre().size()<1)
						tmp += "<p><b>" + op.getName() + "</b> (" + op.getCode() + ") - Nessun monte ore da copiare</p><br>";

					else if (op.getMonteOre().size()==1){
						if (op.getMonteOre().get(0).getAnno().equals(anno))
							tmp += "<p><b>" + op.getName() + "</b> (" + op.getCode() + ") - Monte ore già presente per l'anno indicato</p><br>";
						else
							nearestLeft = op.getMonteOre().get(0);
					} else {
						/* Trova il MonteOre più prossimo all'anno indicato. A parità di distanza, copia l'estremo inferiore.
						 * Es.:	Devo copiare il monte ore per il 2019. 
						 * 		Ho già il monte ore per gli anni 2017, 2018, 2020.
						 * 		Tra 2018 e 2020 copio il 2018  */ 
						
						String forTmp="";
						Integer gap = null;
						
						for (MonteOre m : op.getMonteOre()) {
							if (m.getAnno().equals(anno))
								forTmp += "<p><b>" + op.getName() + "</b> (" + op.getCode() + ") - Monte ore già presente per l'anno indicato</p><br>";
								
							else {//Trova gli estremi
								Integer newGap = Math.abs(m.getAnno()-anno);
								
								if (gap==null || newGap<=gap){
									gap = newGap;
									
									if (m.getAnno()<anno)
										nearestLeft  = m;
									else
										nearestRight = m;
								}		
							}
						}
						
						if (!"".equals(forTmp)){
							tmp += forTmp;
							nearestLeft = null;
							nearestRight= null;
						}
					}
					
					MonteOre toCopy = null;
					
					if (nearestLeft!=null ||nearestRight!=null){
						if (nearestLeft==null)
							toCopy = nearestRight;
						else if (nearestRight==null)
							toCopy = nearestLeft;
						
						else {
							if (Math.abs(nearestLeft.getAnno()-anno)<=Math.abs(nearestRight.getAnno()-anno))
								toCopy = nearestLeft;
							else 
								toCopy = nearestRight;
						}
						
						MonteOre moNew = new MonteOre();
						
						moNew.setAnno(anno);
						moNew.setHhContrattuali(toCopy.getHhContrattuali());
						moNew.setHhScomputo(toCopy.getHhScomputo());
						moNew.setMotivoScomputo(toCopy.getMotivoScomputo());
						
						op.getMonteOre().add(moNew);
						moNew.setOperatore(op);
						
						this.create(op);
					}
				}
			}

			if (!"".equals(tmp))
				this.temporary.put("copyMultiInfo", tmp);
			else 
				this.temporary.remove("copyMultiInfo");
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
}
