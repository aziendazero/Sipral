package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;

import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.PNCCantiere;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.QuestTabella;
import com.phi.entities.baseEntity.ValutazioneConclusivaMdl;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.actions.BaseAction;
import com.phi.security.UserBean;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ValutazioneConclusivaMdlAction")
@Scope(ScopeType.CONVERSATION)
public class ValutazioneConclusivaMdlAction extends BaseAction<ValutazioneConclusivaMdl, Long> {

	private static final long serialVersionUID = 1623305538L;
	private static final Logger log = Logger.getLogger(ValutazioneConclusivaMdlAction.class);

	public static ValutazioneConclusivaMdlAction instance() {
		return (ValutazioneConclusivaMdlAction) Component.getInstance(ValutazioneConclusivaMdlAction.class, ScopeType.CONVERSATION);
	}
	
	public void initOperatore(ValutazioneConclusivaMdl vc) {
		try{
			if (vc==null)
				return;
			
			Employee rdp = null;
			List<Operatore> operatori = new ArrayList<Operatore>();
			
			Procpratiche procpratiche = ProcpraticheAction.instance().getEntity();
			
			//Recupera RDP (Employee) e la lista degi operatori della pratica
			if (procpratiche!=null){
				if (procpratiche.getRdp()!=null)
					rdp = procpratiche.getRdp();
				
				if (procpratiche.getOperatori()!=null)
					operatori = procpratiche.getOperatori();
			}
			
			//Cicla la lista di operatori alla ricerca dell'RDP (Operatore)
			if (rdp!=null && operatori!=null && operatori.size()>0){
				for (Operatore op : operatori){
					if (op.getIsActive() && op.getEmployee()!=null && op.getEmployee().equals(rdp)){
						//Se lo trova lo setta nelle valutazioni conclusive
						vc.setOperatore(op);
						break;
					}
				}
			}
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public List<QuestTabella> initTabellaPatologie() throws Exception {
		if (getEntity() == null)
			return null;
	
		List<QuestTabella> tabellaPatologie = new ArrayList<QuestTabella>();
	
		UserBean ub = UserBean.instance();
		String creator = ub.username();
		String entityPath = getEntity().getClass().getCanonicalName();
		String entitySeq = String.valueOf(getEntity().getInternalId());
	
		Vocabularies voc = VocabulariesImpl.instance();
	
		List<SelectItem> itemList = voc.getIdValues("PHIDIC:PatologieRicorso");
	
		int i = 0;
	
		for (SelectItem item : itemList) {
			QuestTabella riga = new QuestTabella();
	
			riga.setIsActive(true);
			riga.setCreationDate(new Date());
			riga.setCreatedBy(creator);
	
			riga.setNumProgr(i + 1);
			riga.setEntityPath(entityPath);
			riga.setEntitySeq(entitySeq);
			riga.setQuestion("Ricorso");
	
			riga.setCode01((CodeValuePhi) item.getValue());
	
			tabellaPatologie.add(riga);
			i++;
		}
	
		return tabellaPatologie;
	}

	public void persistTabella(List<QuestTabella> tabella)
			throws PersistenceException {
		if (getEntity() == null || tabella == null)
			return;
	
		String entitySeq = String.valueOf(getEntity().getInternalId());
	
		for (QuestTabella riga : tabella) {
			riga.setEntitySeq(entitySeq);
	
			ca.persist(riga);
		}
	}

	@SuppressWarnings("unchecked")
	public List<QuestTabella> loadTabella(String question) throws PhiException{
		if(getEntity()==null)
			return null;

		ValutazioneConclusivaMdl entity = getEntity();
		if (entity instanceof HibernateProxy) {
			entity = (ValutazioneConclusivaMdl)((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation();
		}

		String entityPath = entity.getClass().getCanonicalName();
		String entitySeq = String.valueOf(entity.getInternalId());
	
		QuestTabellaAction qta = QuestTabellaAction.instance();
	
		qta.cleanRestrictions();
		qta.getEqual().put("isActive", true);
		qta.getEqual().put("entityPath", entityPath);
		qta.getEqual().put("entitySeq", entitySeq);
		qta.getEqual().put("question", question);
	
		qta.getOrderBy().put("numProgr", "ascending");
	
		return qta.select();
	}

}