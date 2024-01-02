package com.phi.events;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Oggetto;
import com.phi.entities.baseEntity.Partecipanti;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.ProcpraticheEvent;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

public class ProcpraticaPreupdateListener implements PreUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7040346415439039150L;
	private static final Logger log = Logger.getLogger(ProcpraticaPreupdateListener.class);

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		Object entity = event.getEntity();
		
		if ((entity instanceof Procpratiche)){
			checkServiceDeliveryLocation(event);
			checkStatusCode(event);
		
		} else if ((entity instanceof Protocollo)){
			checkRiferimenti(event);
			checkAssegnazionePratica(event); 
		
		} else if ((entity instanceof Oggetto)){
			checkOggetti(event);
						
		} else if ((entity instanceof Attivita)){
			checkAttivita(event);
						
		}
		
		return false;
	}
	
	private void checkAttivita(PreUpdateEvent event) {
		
		Attivita attivita = (Attivita) event.getEntity();
		Procpratiche procpratiche = attivita.getProcpratiche();
		
		if(procpratiche==null) return;
		
		Object[] oldStateValues = event.getOldState(); 
		//Object[] newStateValues = event.getState();
		String[] propertyNames = event.getPersister().getPropertyNames(); 

		int index = -1;
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equalsIgnoreCase("procpratiche")){
				index = i;
			}
		}
		
		if(index>-1 && oldStateValues[index]!=null) 
			return; // associazione comunicazione a pratica esistente. non fare niente

		String descrizione = "Tipologia '"+attivita.getCode().getCurrentTranslation()+"'";
				
		if(attivita.getPartecipanti() != null){
			descrizione+=", partecipanti '";
			
			for(Partecipanti p: attivita.getPartecipanti()){
				descrizione += p.getOperatore().getName()+",";	
			}
			
			if(descrizione.endsWith(",")) descrizione=descrizione.substring(0, descrizione.length()-1);
			descrizione+="'";
		}
		
		ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
		procpraticheEvent.setProcpratiche(procpratiche);
		procpraticheEvent.setInserimentoManuale(false);
		procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
		
		int lengthDesc = descrizione.length();
		descrizione=descrizione.substring(0,lengthDesc<=2000?lengthDesc:2000);
		
		procpraticheEvent.setTesto(descrizione);
		procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.atc_V0"));
																							  			
		Transaction txn = event.getSession().beginTransaction();
		event.getSession().save(procpraticheEvent);
		txn.commit();
	}
	
	private void checkAssegnazionePratica(PreUpdateEvent event){
		
		//if(!isDirtyPropertyCheck(event,"procpratiche")) return;
		Protocollo protocollo = (Protocollo) event.getEntity();
		Procpratiche procpratiche = protocollo.getProcpratiche();
		
		if(procpratiche==null) 
			return;
		
		Object[] oldStateValues = event.getOldState(); 
		//Object[] newStateValues = event.getState();
		String[] propertyNames = event.getPersister().getPropertyNames(); 

		int index = -1;
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equalsIgnoreCase("procpratiche")) {
				index = i;
			}
		}
		
		if(index==-1) 
			return;
		
		if(oldStateValues==null || oldStateValues.length<index || oldStateValues[index]==null){

			ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
			procpraticheEvent.setProcpratiche(procpratiche);
		
			procpraticheEvent.setInserimentoManuale(false);
			procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
		
			String id = "N.D.";
			
			if(protocollo.getNprotocollo()!=null)
				id = "(n° protocollo: "+protocollo.getNprotocollo().toString()+")";
			else if(protocollo.getNrichiesta()!=null)
				id = "(n° richiesta: "+protocollo.getNrichiesta().toString()+")";
						
			procpraticheEvent.setTesto("Comunicazione "+ id +" agganciata alla pratica.");
			procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.addcom_V0"));

			Transaction txn = event.getSession().beginTransaction();
			event.getSession().save(procpraticheEvent);
			txn.commit();
		}
	}
	
	private void checkStatusCode(PreUpdateEvent event){
		Procpratiche procpratiche = (Procpratiche) event.getEntity();
		if(!isDirtyPropertyCheck(event,"statusCode") || procpratiche.getStatusCode()==null)
			return;
	
		ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
		procpraticheEvent.setProcpratiche(procpratiche);
		procpraticheEvent.setInserimentoManuale(false);

		procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
		String descrizione = "Nuovo stato pratica: " + procpratiche.getStatusCode().getCurrentTranslation();
		
		if(procpratiche.getCancelReason()!=null && procpratiche.getCancelReason().length()>0)
			descrizione+=" (motivo cancellazione:"+procpratiche.getCancelReason()+")";
		
		if(procpratiche.getSuspendedReason()!=null && procpratiche.getSuspendedReason().length()>0)
			descrizione+=" (motivo sospensione:"+procpratiche.getSuspendedReason()+")";
		
		int lengthDesc = descrizione.length();
		descrizione=descrizione.substring(0,lengthDesc<=2000?lengthDesc:2000);
		
		procpraticheEvent.setTesto(descrizione);
		procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.stch_V0"));
		
		Transaction txn = event.getSession().beginTransaction();
		event.getSession().save(procpraticheEvent);
		txn.commit();
	}
	
	private void checkOggetti(PreUpdateEvent event) {
		
		if(!isDirtyPropertyCheck(event,"procpratiche")) 
			return;
		
		Oggetto oggetto = (Oggetto) event.getEntity();
		Procpratiche procpratiche = oggetto.getProcpratiche();
		
		if(procpratiche==null || oggetto==null || oggetto.getCodeLegge81()==null) 
			return;
		
		String descrizione = "Assegnato oggetto: " + oggetto.getCodeLegge81().getCurrentTranslation() + ".";
		
		ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
		procpraticheEvent.setProcpratiche(procpratiche);
		procpraticheEvent.setInserimentoManuale(false);
		procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));

		int lengthDesc = descrizione.length();
		descrizione=descrizione.substring(0,lengthDesc<=2000?lengthDesc:2000);
		
		procpraticheEvent.setTesto(descrizione);
		procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.addobj_V0"));
		
		Transaction txn = event.getSession().beginTransaction();
		event.getSession().save(procpraticheEvent);
		
		txn.commit();
	}
	
	
	private void checkRiferimenti(PreUpdateEvent event) {
		Protocollo protocollo = (Protocollo) event.getEntity();
		Object[] ob = event.getOldState(); 
		String[] propertyNames = event.getPersister().getPropertyNames(); 

		Procpratiche procpratiche = protocollo.getProcpratiche();
		if(procpratiche==null) return;
		
		int index = -1;
		for (int i = 0; i < propertyNames.length; i++) 
			if (propertyNames[i].equalsIgnoreCase("procpratiche"))
				index = i;
			
		if(index>-1 && ob[index]==null) 
			return; // associazione comunicazione a pratica esistente. non fare niente

		//richiedente
		if(isDirtyPropertyCheck(event,"richiedenteInterno")
		|| isDirtyPropertyCheck(event,"richiedenteMedico")
		|| isDirtyPropertyCheck(event,"richiedenteDitta")		
		|| isDirtyPropertyCheck(event,"richiedenteUtente")
		
		|| isDirtyPropertyCheck(event,"riferimentoDitta")
		|| isDirtyPropertyCheck(event,"riferimentoUtente")
		|| isDirtyPropertyCheck(event,"riferimentoCantiere")
		
		|| isDirtyPropertyCheck(event,"ubicazioneDitta")
		|| isDirtyPropertyCheck(event,"ubicazioneCantiere")
		|| isDirtyPropertyCheck(event,"ubicazioneAddr")) {
				
			log.warn("riferimenti CHANGED");
			
			String descrizione = "";
			
			if(protocollo.getRichiedente()!=null){
				String cvCode = protocollo.getRichiedente().getCode();
				descrizione+="Richiedente (";
			
				if(cvCode.equalsIgnoreCase("Interno")){
					Employee e = protocollo.getRichiedenteInterno();
					
					if(e!=null && e.getName()!=null) 
						descrizione += e.getName();
					else
						descrizione += "N.D.";
					
					descrizione += " ,interno), ";
								
				} else if(cvCode.equalsIgnoreCase("Utente")){
					Person p = protocollo.getRichiedenteUtente();
					if(p!=null && p.getName()!=null) 
						descrizione += p.getName();
					else
						descrizione += "N.D.";
					descrizione += ", utente), ";
								
				} else if(cvCode.equalsIgnoreCase("Ditta")){
					PersoneGiuridiche pg = protocollo.getRichiedenteDitta();
					if(pg!=null && pg.getDenominazione()!=null) 
						descrizione += protocollo.getRichiedenteDitta().getDenominazione();
					else
						descrizione += "N.D.";
					descrizione += ", ditta), ";
								
				} else if(cvCode.equalsIgnoreCase("Medico")){			
					Physician ph = protocollo.getRichiedenteMedico();
					if(ph!=null && ph.getName()!=null) 
						descrizione += protocollo.getRichiedenteMedico().getName();
					else
						descrizione += "N.D.";
					descrizione += ", medico), ";
								
				} else if(cvCode.equalsIgnoreCase("Anonimo"))
					descrizione += " anonimo), ";
			}
			
			// riferito
			if(protocollo.getRiferimento()!=null){
				String cvCode = protocollo.getRiferimento().getCode();
				descrizione += "riferito (";
				
				if(cvCode.equalsIgnoreCase("Cantiere")){
					Cantiere c = protocollo.getRiferimentoCantiere();
					if(c!=null && c.getNaturaOpera()!=null) 
						descrizione += c.getNaturaOpera();
					else
						descrizione += "N.D.";
					descrizione += ", cantiere), ";
								
				} else if(cvCode.equalsIgnoreCase("Utente")){
					Person p = protocollo.getRiferimentoUtente();
					if(p!=null && p.getName()!=null) 
						descrizione += p.getName();
					else
						descrizione += "N.D.";
					descrizione += ", utente), ";
								
				} else if(cvCode.equalsIgnoreCase("Ditta")){
					PersoneGiuridiche pg = protocollo.getRiferimentoDitta();
					if(pg!=null && pg.getDenominazione()!=null) 
						descrizione += pg.getDenominazione();
					else
						descrizione += "N.D.";
					descrizione += ", ditta/ente), ";
								
				} else if(cvCode.equalsIgnoreCase("Altro")){			
					descrizione += "altro), ";
				}							
			}
			
			//ubicazione
			if(protocollo.getUbicazione()!=null){
				String cvCode = protocollo.getUbicazione().getCode();
				descrizione += "ubicazione (";
				
				if(cvCode.equalsIgnoreCase("Cantiere")){
					Cantiere c = protocollo.getUbicazioneCantiere();
					
					if(c!=null && c.getNaturaOpera()!=null) 
						descrizione += c.getNaturaOpera();
					else
						descrizione += "N.D.";
					descrizione += ", cantiere).";
								
				} else if(cvCode.equalsIgnoreCase("Altro")){
					AD a = protocollo.getUbicazioneAddr();
					
					descrizione += "altro).";
								
				} else if(cvCode.equalsIgnoreCase("Ditta")){
					PersoneGiuridiche pg = protocollo.getUbicazioneDitta();
					if(pg!=null && pg.getDenominazione()!=null) 
						descrizione += pg.getDenominazione();
					else
						descrizione += "N.D.";
					descrizione += ", ditta/ente).";
								
				} else if(cvCode.equalsIgnoreCase("NonPrevisto")){			
					descrizione += " non previsto).";
				}							
			}
			
			ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
			procpraticheEvent.setProcpratiche(procpratiche);
	
			procpraticheEvent.setInserimentoManuale(false);
			procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
			
			int lengthDesc = descrizione.length();
			descrizione=descrizione.substring(0,lengthDesc<=2000?lengthDesc:2000);
			
			procpraticheEvent.setTesto(descrizione);
			procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.vrif_V0"));
			Transaction txn = event.getSession().beginTransaction();
			event.getSession().save(procpraticheEvent);
			txn.commit();
		}
		
	}
	
	private void checkServiceDeliveryLocation(PreUpdateEvent event) {
		

		Procpratiche procpratiche = (Procpratiche) event.getEntity();
		Object[] oldStateValues = event.getOldState(); 
		String[] propertyNames = event.getPersister().getPropertyNames(); 

		int index = -1;
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equalsIgnoreCase("serviceDeliveryLocation")) {
				index = i;
			}
		}
		
		if(index==-1) 
			return;
		
		if(!isDirtyPropertyCheck(event,"serviceDeliveryLocation")) 
			return;
		
		ServiceDeliveryLocation sdl = (ServiceDeliveryLocation) oldStateValues[index];

		ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
		procpraticheEvent.setProcpratiche(procpratiche);
		procpraticheEvent.setInserimentoManuale(false);
		procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
			
		String testo = "Linea di lavoro cambiata da '";
			
		if(sdl!=null && sdl.getArea()!=null && sdl.getArea().getCurrentTranslation()!=null){
				testo+= sdl.getArea().getCurrentTranslation();
		} else
			testo+="N.D.";
			
			
		ServiceDeliveryLocation newSdl = procpratiche.getServiceDeliveryLocation();
		if(newSdl!=null && newSdl.getArea()!=null && newSdl.getArea().getCurrentTranslation()!=null)
			testo+="' a '" + newSdl.getArea().getCurrentTranslation();
		else
			testo+="' a 'N.D.";
		
			
		testo += "'.";
			
		int lengthDesc = testo.length();
		testo=testo.substring(0,lengthDesc<=2000?lengthDesc:2000);
		
		procpraticheEvent.setTesto(testo);

		procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.swl_V0"));
			
		Transaction txn = event.getSession().beginTransaction();
		event.getSession().save(procpraticheEvent);
		txn.commit();
	}

	private boolean isDirtyPropertyCheck(PreUpdateEvent event, String propertyName){
		
		Object[] oldStateValues = event.getOldState(); 
		Object[] newStateValues = event.getState();
		String[] propertyNames = event.getPersister().getPropertyNames(); 

		int index = -1;
		for (int i = 0; i < propertyNames.length; i++) {
			if (propertyNames[i].equalsIgnoreCase(propertyName)) 
				index = i;
		}
		
		if(index==-1) 
			return false;
		
		if(oldStateValues==null || oldStateValues.length<index || oldStateValues[index]==null) 
			return true;
		
		if(oldStateValues[index].equals(newStateValues[index])) 
			return false;
		
		return true;	
	}

}