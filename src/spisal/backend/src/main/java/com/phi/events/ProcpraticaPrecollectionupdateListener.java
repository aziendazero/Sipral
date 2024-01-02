package com.phi.events;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentBag;
import org.hibernate.collection.PersistentList;
import org.hibernate.event.PreCollectionUpdateEvent;
import org.hibernate.event.PreCollectionUpdateEventListener;

import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.ProcpraticheEvent;
import com.phi.entities.baseEntity.TagFascicolo;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.security.UserBean;

public class ProcpraticaPrecollectionupdateListener implements PreCollectionUpdateEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7065985336093686975L;
	//private static final Logger log = Logger.getLogger(ProcpraticaPreupdateListener.class);

	
	@Override
	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
		//String entity = event.getAffectedOwnerEntityName();
		Object object = event.getAffectedOwnerOrNull();
		Procpratiche procpratiche = null;
		
		// log aggiunte di tagFascicolo
		HashMap<Long,String> tagMapNew = new HashMap<Long,String>();
		HashMap<Long,String> tagMapOld = new HashMap<Long,String>();

		if(object!=null && object instanceof Procpratiche){
			
			procpratiche = (Procpratiche) object;
		
			List<TagFascicolo> listaVecchia = (List<TagFascicolo>)((PersistentBag)((Procpratiche)event.getAffectedOwnerOrNull()).getTagFascicolo()).getStoredSnapshot();
			List<TagFascicolo> listaNuova = (List<TagFascicolo>) ((Procpratiche)event.getAffectedOwnerOrNull()).getTagFascicolo();
			if(listaVecchia!=null && listaNuova!=null){
				for(TagFascicolo tf: listaVecchia)
					tagMapOld.put(tf.getInternalId(),tf.getFascicolo());

				for(TagFascicolo tf: listaNuova)
					tagMapNew.put(tf.getInternalId(),tf.getFascicolo());

				List<TagFascicolo> subtract = ((List<TagFascicolo>) CollectionUtils.subtract(listaNuova, listaVecchia));

				for(TagFascicolo tag:subtract){
					ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
					procpraticheEvent.setProcpratiche(procpratiche);
					procpraticheEvent.setInserimentoManuale(false);
					procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
					procpraticheEvent.setTesto("Associato fascicolo '" +tag.getFascicolo() +"'.");
					procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.filed_V0"));
					Transaction txn = event.getSession().beginTransaction();
					event.getSession().save(procpraticheEvent);
					txn.commit();
				}
			}
			
			// log assegnazione operatori
			
			List<Operatore> listaOperatori = ((Procpratiche)event.getAffectedOwnerOrNull()).getOperatori();
			List<Operatore> listaVecchiaOperatori = (List<Operatore>)((PersistentList)((Procpratiche)event.getAffectedOwnerOrNull()).getOperatori()).getStoredSnapshot();
			
			if (listaOperatori!=null && listaVecchiaOperatori!=null){
				List<Operatore> aggiunte = (List<Operatore>) CollectionUtils.subtract(listaOperatori, listaVecchiaOperatori);
				List<Operatore> rimozioni = (List<Operatore>) CollectionUtils.subtract(listaVecchiaOperatori,listaOperatori);
			
				if (aggiunte!=null && aggiunte!=null)
					for(Operatore op:aggiunte){
						ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
						procpraticheEvent.setProcpratiche(procpratiche);
						procpraticheEvent.setInserimentoManuale(false);
						procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
						procpraticheEvent.setTesto("Assegnato operatore " +op.getName() +".");
						procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.aop_V0"));
						Transaction txn = event.getSession().beginTransaction();
						event.getSession().save(procpraticheEvent);
						txn.commit();
				}
			
				if (rimozioni!=null && rimozioni!=null)
					for(Operatore op:rimozioni){
						ProcpraticheEvent procpraticheEvent = new ProcpraticheEvent();
						procpraticheEvent.setProcpratiche(procpratiche);
						procpraticheEvent.setInserimentoManuale(false);
						procpraticheEvent.setEmployee((Employee)event.getSession().get(Employee.class,  UserBean.instance().getEmployeeId()));
						procpraticheEvent.setTesto("Rimosso operatore " +op.getName() +".");
						procpraticheEvent.setTitolo((CodeValuePhi) event.getSession().get(CodeValuePhi.class, "phidic.spisal.pratiche.eventi.aop_V0"));
						Transaction txn = event.getSession().beginTransaction();
						event.getSession().save(procpraticheEvent);
						txn.commit();
					}
			}
		}
	}
	
}