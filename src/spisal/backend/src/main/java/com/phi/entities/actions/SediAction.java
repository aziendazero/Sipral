package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;

@BypassInterceptors
@Name("SediAction")
@Scope(ScopeType.CONVERSATION)
public class SediAction extends BaseAction<Sedi, Long> {

	private static final Logger log = Logger.getLogger(SediAction.class);
	private static String GET_ATECO = "SELECT att.code FROM AttivitaIstat att JOIN att.importanza i " +
			"WHERE att.personeGiuridiche.internalId =:internalID AND i.code = 'P'";//cerco solo l'attività PRINCIPALE
	private static String GET_ONLY_INST = "SELECT se FROM Sedi se WHERE se.personaGiuridica.internalId = :pgId " + 
			"AND se.isActive = true AND se.soloInstImp = true";//cerco la sede di solo installazioni che non compare nell'elenco principale
	/**
	 * 
	 */
	private static final long serialVersionUID = -7036349509273882798L;

	public static SediAction instance() {
		return (SediAction) Component.getInstance(SediAction.class, ScopeType.CONVERSATION);
	}

	public boolean existsOnlyInst(PersoneGiuridiche pg){

		Sedi onlyInst = null;

		Query qOnlyInst = ca.createQuery(GET_ONLY_INST);
		qOnlyInst.setParameter("pgId", pg.getInternalId());

		@SuppressWarnings("unchecked")
		List<Sedi> listOnlyInst = (List<Sedi>) qOnlyInst.getResultList();

		if(listOnlyInst==null || listOnlyInst.isEmpty())
			return false;

		onlyInst = listOnlyInst.get(0);

		if(onlyInst!=null)
			return true;
		
		return false;
	}

	public void addOnlyInst(List<Sedi> list){
		// Altro metodo becero che aggiunge la sede solo installazione
		// per evitare continue read con filtraggi diversi

		if(list==null)
			return;

		PersoneGiuridiche pg = list.get(0).getPersonaGiuridica();
		Sedi onlyInst = null;

		Query qOnlyInst = ca.createQuery(GET_ONLY_INST);
		qOnlyInst.setParameter("pgId", pg.getInternalId());

		@SuppressWarnings("unchecked")
		List<Sedi> listOnlyInst = (List<Sedi>) qOnlyInst.getResultList();

		if(listOnlyInst==null || listOnlyInst.isEmpty())
			return;

		onlyInst = listOnlyInst.get(0);

		if(onlyInst==null)
			return;

		list.add(onlyInst);

	}

	public void injectOnlyInst(PersoneGiuridiche pg){
		// Cerca tra le sedi della ditta attive quella che ha il flag di solo installazione impianti
		List<Sedi> fullList = pg.getSedi();

		Sedi onlyInst = null;
		this.eject();

		Query qOnlyInst = ca.createQuery(GET_ONLY_INST);
		qOnlyInst.setParameter("pgId", pg.getInternalId());

		@SuppressWarnings("unchecked")
		List<Sedi> listOnlyInst = (List<Sedi>) qOnlyInst.getResultList();

		if(listOnlyInst==null || listOnlyInst.isEmpty())
			return;

		onlyInst = listOnlyInst.get(0);

		if(onlyInst==null)
			return;


		this.inject(onlyInst);
	}

	public void switchListAddebito(PhiDataModel<Sedi> begin){
		// Metodo becero che fa coppia con injectOnlyAddebito(PersoneGiuridiche pg)
		// per permettere la coesistenza di due liste di sedi
		// di cui una di sedi ordinarie e una di solo addebito
		if(begin==null)
			return;

		List<Sedi> onlyAddebito = begin.getList();
		ejectList("SediAddebitoList");
		injectList(onlyAddebito);
	}

	public void injectOnlyAddebito(PersoneGiuridiche pg){
		// Metodo becero che fa coppia con switchListAddebito(PhiDataModel<Sedi> begin)
		// e traveste il nome della lista
		// per permettere la coesistenza di due liste di sedi
		// di cui una di sedi ordinarie e una di solo addebito

		List<Sedi> onlyAddebito = null;

		String ONLY_ADDEBITO_FROM_PG = "SELECT se FROM Sedi se " + 
				"WHERE se.personaGiuridica.internalId = :pgId AND se.isActive = true " + 
				"AND se.sedeAddebito = true AND (se.copy = false or se.copy is null)";
		
		Query qOnlyAdd = ca.createQuery(ONLY_ADDEBITO_FROM_PG);
		qOnlyAdd.setParameter("pgId", pg.getInternalId());

		try {
			onlyAddebito = (List<Sedi>) qOnlyAdd.getResultList();
		} catch (NoResultException e) {
			//onlyAddebito = new ArrayList<Sedi>();
		}

		injectList(onlyAddebito, "SediAddebitoList");
	}

	public Sedi copy(Sedi toCopy){
		try{
			Sedi copy = new Sedi();
			copy.setCopy(true);
			copy.setIsActive(false);

			if(toCopy.getAddr()!=null)
				copy.setAddr(toCopy.getAddr().cloneAd());

			copy.setSedeAddebito(toCopy.getSedeAddebito());

			copy.setCig(toCopy.getCig());
			copy.setCodiceUnivoco(toCopy.getCodiceUnivoco());
			copy.setClasseEconomica(toCopy.getClasseEconomica());
			copy.setCodContabilita(toCopy.getCodContabilita());
			copy.setCodiceUnivoco(toCopy.getCodiceUnivoco());
			copy.setCodiceVia(toCopy.getCodiceVia());
			copy.setDenominazioneUnitaLocale(toCopy.getDenominazioneUnitaLocale());
			copy.setEsenzione(toCopy.getEsenzione());
			copy.setImpSpesa(toCopy.getImpSpesa());
			copy.setNote(toCopy.getNote());
			copy.setPrincipaleAdd(toCopy.getPrincipaleAdd());
			copy.setSedePrincipale(toCopy.getSedePrincipale());
			copy.setSettore(toCopy.getSettore());
			copy.setStato(toCopy.getStato());

			if(toCopy.getTelecom()!=null)
				copy.setTelecom(toCopy.getTelecom().cloneTel());

			copy.setTipoAttivita(toCopy.getTipoAttivita());
			copy.setTipoUtente(toCopy.getTipoUtente());
			copy.setZona(toCopy.getZona());

			List<IndirizzoSped> indirizzi = toCopy.getIndirizzoSped();
			if (indirizzi!=null && indirizzi.size()>0){
				copy.setIndirizzoSped(new ArrayList<IndirizzoSped>());

				for (IndirizzoSped ind:indirizzi){
					IndirizzoSped icopy = IndirizzoSpedAction.instance().copy(ind);
					copy.getIndirizzoSped().add(icopy);
					ca.create(icopy);
				}
			}


			//I0066939 
			//copy.setPersonaGiuridica(PersoneGiuridicheAction.instance().copyForArpav(toCopy.getPersonaGiuridica()));
			
			//H00111009
			copy.setPersonaGiuridica(toCopy.getPersonaGiuridica());
			
			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public Sedi injectFromExisting (Sedi sede) {

		if (sede == null || sede.getPersonaGiuridica() == null) {
			log.error("impossibile agganciare sedi di addebito senza persona giuridica");
			return null;
		}

		Sedi sa = new Sedi();
		sa.setDenominazioneUnitaLocale(sede.getDenominazioneUnitaLocale());

		if (sede.getAddr()!=null)
			sa.setAddr(sede.getAddr().cloneAd());

		/*
		 * la sede ha solo una mail che viene salvata in telecom.mail, ma è la PEC
		 * quindi cloneTel non va bene del tutto
		 */
		if (sede.getTelecom()!=null){
			sa.setTelecom(sede.getTelecom().cloneTel());

		}

		sa.setStato(sede.getStato());
		sa.setSedeAddebito(true);
		inject(sa);

		//		sa.setPersonaGiuridica(sede.getPersonaGiuridica());
		//		sede.getPersonaGiuridica().addSediAddebito(sa);

		return sa;
	}

	public List<SelectItem> choicesSede(){
		List<SelectItem> out = new ArrayList<SelectItem>();

		SelectItem first = new SelectItem();
		first.setValue(false);
		first.setLabel("Sede ordinaria");
		out.add(first);

		SelectItem second = new SelectItem();
		second.setValue(true);
		second.setLabel("Sede di addebito");
		out.add(second);		

		return out;
	}

	public void updatePrincipaleIfRemoved() {

		List<IndirizzoSped> indirizzi = getEntity().getIndirizzoSped();
		if (indirizzi == null || indirizzi.size() == 0){
			entity.setIndirizzoSpedPrinc(null);
			return;
		}

		IndirizzoSped principale = entity.getIndirizzoSpedPrinc();
		if (indirizzi.contains(principale)){
			return;
		}
		else {
			entity.setIndirizzoSpedPrinc(null);
		}
	}

	public boolean disableCheckPrincipale() throws PhiException {	
		try {
			Sedi s = (Sedi)Contexts.getConversationContext().get("Sedi");

			if (s!=null && s.getSedePrincipale())
				return false;


			if(Contexts.getConversationContext().get("SediList") instanceof PhiDataModel){
				PhiDataModel<Sedi> sedi = (PhiDataModel<Sedi>) Contexts.getConversationContext().get("SediList");

				if (sedi != null) {
					for (Sedi sede : sedi.getList()) {
						if (sede.getSedePrincipale()) {
							return true;
						}
					}
				}
			}

			return false;

		} catch (Exception e) {
			// TODO: handle exception
			throw new PhiException(ErrorConstants.GENERIC_ERR_INTERNAL_MSG, e, ErrorConstants.GENERIC_ERR_INTERNAL_MSG);

		}
	}

	public void injectSedePrincipale(List<Sedi> lst){
		if(lst==null || lst.isEmpty())
			return;

		for(Sedi sede : lst){
			if (sede.getSedePrincipale() && sede.getIsActive()) {
				inject(sede);
			}
		}
	}

	public void linkUnlinkAttivita(List<AttivitaIstat> link, List<AttivitaIstat> unlink) throws PhiException {
		if(entity==null)
			return;

		List<AttivitaIstat> lista = new ArrayList<AttivitaIstat>();

		AttivitaIstatAction attAction = AttivitaIstatAction.instance();

		if(link!=null){			
			for(AttivitaIstat attivita : link){
				attivita.setSedi(entity);
				attAction.inject(attivita);
				attAction.create();
				lista.add(attAction.getEntity());

			}
			attAction.eject();
		}
		if(unlink!=null){
			for(AttivitaIstat att : unlink){
				lista.remove(att);
				attAction.inject(att);
				attAction.delete();
			}
			attAction.eject();
		}
		if(entity!=null && entity.getSedePrincipale()){
			AttivitaIstatAction.instance().getTemporary().put("AttivitaIstatDittaList", new PhiDataModel<AttivitaIstat>(lista, "AttivitaIstat"));
			AttivitaIstatAction.instance().getTemporary().put("ToRemoveAttivitaIstatDittaList", new PhiDataModel<AttivitaIstat>(new ArrayList<AttivitaIstat>(), "AttivitaIstat"));
		}

		PersoneGiuridicheAction.instance().refresh();
		attAction.injectList(lista);
		attAction.injectEmptyList("ToRemoveAttivitaIstatList");
	}

	// I0062094
	public boolean checkAteco(PhiDataModel list){

		List<AttivitaIstat> attList = list.getList();

		boolean anyAteco = false;
		int i=0;

		if(attList!=null && !attList.isEmpty()){
			do{
				AttivitaIstat att = attList.get(i);
				if(att.getCode()!=null){
					anyAteco=true;
				}
				i++;			
			}while(!anyAteco && i<list.size());			
		}


		return anyAteco;
	}

	@SuppressWarnings("unchecked")
	public void injectAddebitoWithExternal(PersoneGiuridiche pg){
		// Metodo becero che fa coppia con switchListAddebito(PhiDataModel<Sedi> begin)
		// e traveste il nome della lista
		// per permettere la coesistenza di due liste di sedi
		// di cui una di sedi ordinarie e una di solo addebito
	
		List<Sedi> onlyAddebito = null;
	
		String ONLY_ADDEBITO_FROM_PG = "SELECT se FROM Sedi se " + 
				"WHERE se.personaGiuridica.internalId = :pgId AND se.isActive = true " + 
				"AND se.sedeAddebito = true AND (se.copy = false or se.copy is null)";
		
		Query qOnlyAdd = ca.createQuery(ONLY_ADDEBITO_FROM_PG);
		qOnlyAdd.setParameter("pgId", pg.getInternalId());
	
		try {
			onlyAddebito = (List<Sedi>) qOnlyAdd.getResultList();
		} catch (NoResultException e) {
			//onlyAddebito = new ArrayList<Sedi>();
		}
		
		String ADDEBITO_EXTERNAL_FROM_PG = "SELECT se FROM Sedi se " + 
				"JOIN se.pgEsterne pg " + 
				"WHERE pg.internalId = :pgId";
		
		Query qAddExternal = ca.createQuery(ADDEBITO_EXTERNAL_FROM_PG);
		qAddExternal.setParameter("pgId", pg.getInternalId());
	
		List<Sedi> esterne = null;
		try {
			esterne = (List<Sedi>) qAddExternal.getResultList();
		} catch (NoResultException e) {
			//onlyAddebito = new ArrayList<Sedi>();
		}
	
		onlyAddebito.addAll(esterne);
		injectList(onlyAddebito, "SediAddebitoList");
	}

	public void mergeAddebitoLists(PhiDataModel<Sedi> sediList, PhiDataModel<Sedi> saeList ){
		if(sediList==null || saeList==null)
			return;
		
		List<Sedi> sediInterneList = sediList.getList();
		List<Sedi> sediEsterneList = saeList.getList();
		
		sediInterneList.addAll(sediEsterneList);
		injectList(sediInterneList);
	}
}