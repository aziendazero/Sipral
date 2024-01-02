package com.phi.events;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.ProcpraticheEvent;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.security.UserBean;

public class ProcpraticaPreinsertListener implements PreInsertEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7040346415439039150L;
	private static final Logger log = Logger.getLogger(ProcpraticaPreupdateListener.class);

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		Object entity = event.getEntity();
		
		
		if ((entity instanceof Provvedimenti))
			checkProvvedimenti(event);
		else if ((entity instanceof Procpratiche)){
			
			Procpratiche procpratiche = (Procpratiche) event.getEntity(); 
			ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
			procpraticheEvent.setProcpratiche(procpratiche);
			procpraticheEvent.setInserimentoManuale(false);

			procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
			
			String testo = "Inserita pratica a partire dalla comunicazione ";
					
			String id = "N.D.";
			
			Protocollo protocollo = null;
			
			if(procpratiche.getProtocollo()!=null && procpratiche.getProtocollo().size()>0){
				protocollo = procpratiche.getProtocollo().get(0);
			
				if(protocollo.getNprotocollo()!=null)
					id = "(n° protocollo: "+protocollo.getNprotocollo().toString()+")";
				else if(protocollo.getNrichiesta()!=null){
					id = "(n° richiesta: "+protocollo.getNrichiesta().toString()+")";
				}
			}
			
			int lengthDesc = testo.length();
			testo=testo.substring(0,lengthDesc<=2000?lengthDesc:2000);
			
			procpraticheEvent.setTesto(testo + " " + id + ".");
			
			procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.new_V0"));
			
			Transaction txn = event.getSession().beginTransaction();
			event.getSession().save(procpraticheEvent);
			txn.commit();
			
		}
		
	
		
		return false;
	}
	

	
	
	private void checkProvvedimenti(PreInsertEvent event) {
		
		//if(!isDirtyPropertyCheck(event,"procpratiche")) return;
		
		Provvedimenti provvedimenti = (Provvedimenti) event.getEntity();
		Procpratiche procpratiche = provvedimenti.getProcpratiche();
		
		if(procpratiche==null) return;
	
		String descrizione = "Assegnato provvedimento senza codice ("+provvedimenti.getNumero()+")";
				
		if(provvedimenti.getType()!=null)
			descrizione = "Assegnato provvedimento "+provvedimenti.getType().getCurrentTranslation()+" ("+provvedimenti.getNumero()+")";
		
		
		if(provvedimenti.getSoggetto()!=null){
			
			if(provvedimenti.getSoggetto().getUtente()!=null){
				descrizione+= " a carico di '"+provvedimenti.getSoggetto().getUtente().getName()+"'.";
			}else if(provvedimenti.getSoggetto().getDitta()!=null){
				descrizione+= " a carico della ditta '"+provvedimenti.getSoggetto().getDitta().getDenominazione()+"'.";
			}else if(provvedimenti.getSoggetto().getDittaUtente()!=null){
				descrizione+= " a carico di/della '"+provvedimenti.getSoggetto().getDittaUtente().getDenominazione()+"'.";
			}else if(provvedimenti.getSoggetto().getCantiere()!=null){
				descrizione+= " a carico del cantiere '"+provvedimenti.getSoggetto().getCantiere().getNaturaOpera()+"'.";
			}
		}
			
		ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
		procpraticheEvent.setProcpratiche(procpratiche);
		procpraticheEvent.setInserimentoManuale(false);
		procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
		
		int lengthDesc = descrizione.length();
		descrizione=descrizione.substring(0,lengthDesc<=2000?lengthDesc:2000);
		
		procpraticheEvent.setTesto(descrizione);
		procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.insprov_V0"));
		
		Transaction txn = event.getSession().beginTransaction();
		event.getSession().save(procpraticheEvent);
		txn.commit();
		
		
	}

	
		


}