package com.phi.entities.actions;


import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.baseEntity.AgendaConf;
import com.phi.entities.baseEntity.AppointmentSp;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("AppointmentSpAction")
@Scope(ScopeType.CONVERSATION)
public class AppointmentSpAction extends BaseAction<AppointmentSp, Long> {

	private static final long serialVersionUID = 1301786748L;
	private static final Logger log = Logger.getLogger(AppointmentSpAction.class);

	public static AppointmentSpAction instance() {
		return (AppointmentSpAction) Component.getInstance(AppointmentSpAction.class, ScopeType.CONVERSATION);
	}


	public String getCalendarParameter() {
		
		Context conv = Contexts.getConversationContext();
		
		String ope = (String)getTemporary().get("appointmentFilter");
		if (ope == null || ope.isEmpty()) {
			return "";
		}
		
		if (ope.equals("byoperator")){
			long id = getIdEmployeeVisitaOrAcc();
			return "'"+ope+"',"+id;
		}
		
		if (ope.equals("byagenda")){
			AgendaConf agenda = (AgendaConf)conv.get("AgendaConf");
			if (agenda == null){
				return "";
			}
			return "'"+ope+"',"+agenda.getInternalId();
		}
		
		if (ope.equals("byagendaandope")){
			AgendaConf agenda = (AgendaConf)conv.get("AgendaConf");
			if (agenda == null){
				return "";
			}
			long id = getIdEmployeeVisitaOrAcc();
			
			return "'"+ope+"',"+agenda.getInternalId()+","+id;
		}
		return "";
	}
	
	private Long getIdEmployeeVisitaOrAcc (){

		Context conv = Contexts.getConversationContext();
		
		VisitaSp visita = (VisitaSp)conv.get("VisitaSp");
		if (visita != null){
			long id = visita.getRiferimentoInterno().getInternalId();
			return id;
		}
		
		AccertSp acce = (AccertSp)conv.get("AccertSp");
		if (acce != null){
			long id = acce.getRiferimentoInterno().getInternalId();
			return id;
		}
		
		log.error("missing assigned to VisitaSp or AccertSp, used current logged employee");
		long id = UserBean.instance().getCurrentEmployee().getInternalId();
		return id;
	}
	
	public void linkPaziente() {

		Context conv = Contexts.getConversationContext();
		Object l = conv.get("SoggettoList");
		if (l instanceof PhiDataModel){
			List<Soggetto> soggetti = (List<Soggetto>)((PhiDataModel)l).getList();
			if (soggetti != null) {
				for (Soggetto s : soggetti) {
					if (s.getRuolo() !=null && s.getRuolo().getCode().equals("paziente")){
						getEntity().setSoggetto(s);
						return;
					}
				}
			}
			
		}
			
	}
}