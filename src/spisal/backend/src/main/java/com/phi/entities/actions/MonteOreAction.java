package com.phi.entities.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.entities.baseEntity.Disp;
import com.phi.entities.baseEntity.MonteOre;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

@BypassInterceptors
@Name("MonteOreAction")
@Scope(ScopeType.CONVERSATION)
public class MonteOreAction extends BaseAction<MonteOre, Long> {

	private static final long serialVersionUID = 469936856L;
	private static final Logger log = Logger.getLogger(MonteOreAction.class); 


	public static MonteOreAction instance() {
		return (MonteOreAction) Component.getInstance(MonteOreAction.class, ScopeType.CONVERSATION);
	}

	public void setHhDisponibili() {
		try {
			MonteOre mo = this.getEntity();

			if (mo.getHhContrattuali()!=null && mo.getHhScomputo()!=null)
				mo.setHhDisponibili(mo.getHhContrattuali()-mo.getHhScomputo());
			
		} catch (Exception ex) {
			log.error(ex);
		}
	}
	
	public String getDispStr(MonteOre mo) {
		String ret = "";
		
		try {
			if (mo==null)
				return "<p><b>N.D.</b></p>";
			
			List<Disp> disp = mo.getDisp();

			if (disp==null || disp.size()<1)
				return "<p><b>" + mo.getHhDisponibili()==null?"N.D.":mo.getHhDisponibili() + "</b></p>";
			
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			for (Disp d:disp)
				ret += "<p>Dal " + sdf.format(d.getDal()) + ": <b>" + d.getHh() + "</b></p><br>";
			
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			return ret;
		}
	}
	
	public Boolean isDispOk(MonteOre mo, Disp dNew) {
		
		try {
			if (mo==null){
				String error = "Monteore non valido.";
				FacesErrorUtils.addErrorMessage("commError", error, error);
				return false;
			}
			
			if (dNew==null || dNew.getDal()==null || dNew.getHh()==null){
				String error = "Data non valida.";
				FacesErrorUtils.addErrorMessage("commError", error, error);
				return false;
			}
			
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(dNew.getDal());
			int year = calendar.get(Calendar.YEAR);
			
			if (year!=mo.getAnno()){
				String error = "Anno non valido.";
				FacesErrorUtils.addErrorMessage("commError", error, error);
				return false;
			}
			
			List<Disp> disp = mo.getDisp();

			if (disp==null || disp.size()<1)
				return true;
			
			Date max = null;
			
			for (Disp d:disp){
				if (max==null || max.compareTo(d.getDal())<=0)
					max=d.getDal();
			}
			
			if (max!=null && dNew.getDal().compareTo(max)<=0){
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf = new SimpleDateFormat("dd/MM/yyyy");
					
				String	error = "Inserire date superiori al " + sdf.format(max);
				FacesErrorUtils.addErrorMessage("commError", error, error);

				return false;
			}
			
			return true;
			
		} catch (Exception ex) {
			log.error(ex);
			return false;
		}
	}
	
	public void linkUnlinkDisponibilita(List<Disp> link, List<Disp> unlink){
		if(entity==null)
			return;
		
		if(entity.getDisp()==null)
			entity.setDisp(new ArrayList<Disp>());
		
		List<Disp> lista = entity.getDisp();
		if(link!=null){			
			for(Disp d : link){
				if(!lista.contains(d)){
					entity.getDisp().add(d);
					d.setMonteOre(entity);
				}

			}
		}
		
		if(unlink!=null){
			for(Disp d : unlink){
				if(lista.contains(d)){
					entity.getDisp().remove(d);
					d.setMonteOre(null);
				}
			}
		}
	}
	
	public void injectCurrentYear(){
		Context conv = Contexts.getConversationContext();
		IdataModel<MonteOre> monteOreList = (IdataModel<MonteOre>)conv.get("MonteOreList");
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int currentYear = c.get(Calendar.YEAR);
		
		int i = 0;
		if(monteOreList != null){
			for(MonteOre mo : monteOreList.getList()){
				if(monteOreList.getList() != null && monteOreList.getList().isEmpty() != true){
					List<MonteOre> entityList = ((IdataModel)monteOreList).getList();
					if(entityList.get(i) != null){
						if(mo.getAnno() == currentYear){
							inject(entityList.get(i));
							temporary.put("currentYear", true);
						}
					}
				}
				i++;
			}
		}
		
	}
	

}