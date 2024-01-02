package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.baseEntity.PersoneCantiere;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.TagCantiere;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCity;

@BypassInterceptors
@Name("CantiereAction")
@Scope(ScopeType.CONVERSATION)
public class CantiereAction extends BaseAction<Cantiere, Long> {

	private static final long serialVersionUID = 829947839L;

	private static final Logger log = Logger.getLogger(CantiereAction.class);

	public static CantiereAction instance() {
		return (CantiereAction) Component.getInstance(CantiereAction.class, ScopeType.CONVERSATION);
	}

	public Cantiere copy(Cantiere toCopy){
		try{
			Cantiere cantiere = new Cantiere();
			//ProtocolloAction protAction = ProtocolloAction.instance();

			if(toCopy.getAddr()!=null)
				cantiere.setAddr(toCopy.getAddr().cloneAd());

			cantiere.setCost(toCopy.getCost());
			cantiere.setDataComunicazione(toCopy.getDataComunicazione());
			cantiere.setDurataLavori(toCopy.getDurataLavori());
			cantiere.setFineLavori(toCopy.getFineLavori());	
			cantiere.setInizioLavori(toCopy.getInizioLavori());
			cantiere.setLatitudine(toCopy.getLatitudine());
			cantiere.setLongitudine(toCopy.getLongitudine());
			cantiere.setMail(toCopy.getMail());
			cantiere.setMaxWorkers(toCopy.getMaxWorkers());
			//cantiere.setName(toCopy.getName());
			cantiere.setNaturaOpera(toCopy.getNaturaOpera());
			cantiere.setId(toCopy.getId());

			if(toCopy.getIdPnc()!=null && !toCopy.getIdPnc().isEmpty()){
				cantiere.setNotifica(true);
			}else{
				cantiere.setNotifica(false);
			}
			cantiere.setTitoloIV(toCopy.getTitoloIV());

			cantiere.setNumeroAutonomi(toCopy.getNumeroAutonomi());
			cantiere.setNumeroImprese(toCopy.getNumeroImprese());
			cantiere.setTipologiaNotifica(toCopy.getTipologiaNotifica());
			cantiere.setTipologiaOther(toCopy.getTipologiaOther());
			cantiere.setTipo(toCopy.getTipo());

			if (toCopy.getOriginal() == null) {
				cantiere.setOriginal(toCopy); //toCopy is original since getOriginal returns null
			}else {
				cantiere.setOriginal(toCopy.getOriginal()); //toCopy is a clone, original is in getOriginal
			}

			cantiere.setLastVersion(false);
			ca.create(cantiere);

			//Copia il/i committente/i
			List<Committente> committenti = toCopy.getCommittente();
			if (committenti != null && committenti.size() > 0) {
				cantiere.setCommittente(new ArrayList<Committente>());				
				CommittenteAction ca = new CommittenteAction();

				for (Committente c : committenti) {
					Committente newCommittente = ca.copy(c);
					newCommittente.setCantiere(cantiere);

					cantiere.getCommittente().add(newCommittente);
				}
			}

			//Copia le persone coinvolte nel cantiere
			List<PersoneCantiere> personeCantiere = toCopy.getPersoneCantiere();
			if (personeCantiere != null && personeCantiere.size() > 0) {
				cantiere.setPersoneCantiere(new ArrayList<PersoneCantiere>());
				PersoneCantiereAction pca = new PersoneCantiereAction();

				for (PersoneCantiere pc : personeCantiere) {
					PersoneCantiere newPersoneCantiere = pca.copy(pc);
					newPersoneCantiere.setCantiere(cantiere);

					cantiere.getPersoneCantiere().add(newPersoneCantiere);
				}
			}

			//Copia le ditte coinvolte nel cantiere
			List<DitteCantiere> ditteCantiere = toCopy.getDitteCantiere();
			if (ditteCantiere != null && ditteCantiere.size() > 0 ) {
				cantiere.setDitteCantiere(new ArrayList<DitteCantiere>());				
				DitteCantiereAction dca = new DitteCantiereAction();

				for (DitteCantiere dc : ditteCantiere) {
					DitteCantiere newDitteCantiere = dca.copy(dc);
					newDitteCantiere.setCantiere(cantiere);

					cantiere.getDitteCantiere().add(newDitteCantiere);
				}
			}

			//Copia i tag del cantiere
			List<TagCantiere> tagCantiere = toCopy.getTagCantiere();
			if (tagCantiere != null && tagCantiere.size() > 0 ) {
				cantiere.setTagCantiere(new ArrayList<TagCantiere>());				
				TagCantiereAction tca = new TagCantiereAction();

				for (TagCantiere tc : tagCantiere) {
					TagCantiere newTagCantiere = tca.copy(tc);
					newTagCantiere.setCantiere(cantiere);

					cantiere.getTagCantiere().add(newTagCantiere);
				}
			}


			return cantiere;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void closeAllPreviousVersionsOf(Cantiere lastVersion) throws PersistenceException{
		if(lastVersion!=null && ca.contains(lastVersion) && lastVersion.getOriginal()!=null && ca.contains(lastVersion.getOriginal())){
			Query q = ca.createQuery("SELECT c.internalId FROM Cantiere c JOIN c.original o WHERE o.internalId = :oid AND c.lastVersion = 1 AND c.internalId <> :cid");

			q.setParameter("oid", lastVersion.getOriginal().getInternalId());
			q.setParameter("cid", lastVersion.getInternalId());
			List<Long> cantieriId = q.getResultList();
			if(cantieriId!=null){
				for(Long id : cantieriId){
					Cantiere old = ca.get(Cantiere.class, id);
					if(old!=null){
						old.setLastVersion(false);
						ca.create(old);
					}
				}
			}
		}
	}

	/*@Create
	public void init(){
		qPersone = ca.createQuery(QRY_PERSONE);
	}*/

	public boolean getFilterBySdl() {
		return this.filterBySdl;
	}

	@Override
	public void setFilterBySdl(boolean filterBySdl) {
		this.filterBySdl = filterBySdl;
	}

	public void filterCommittente(){
		String committente = (String)getTemporary().get("committente");

		/*
		 * IL COMMITTENTE PUO' ESSERE UNA PERSONA FISICA O GIURIDICA
		 */
		removeExpression("this", "CommittentePerson.name.giv");
		removeExpression("this", "CommittentePerson.name.fam");
		removeExpression("this", "CommittentePersoneGiuridiche.denominazione");
		removeSubCriteria(entityCriteria, "CommittentePerson");
		removeSubCriteria(entityCriteria, "CommittentePersoneGiuridiche");
		removeSubCriteria(entityCriteria, "Committente");

		if(committente==null || committente.isEmpty())
			return;

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if(findSubCriteria("Committente")==null)
			entityCriteria.createAlias("committente", "Committente", Criteria.INNER_JOIN);

		if(findSubCriteria("CommittentePersoneGiuridiche")==null)
			entityCriteria.createAlias("Committente.personeGiuridiche", "CommittentePersoneGiuridiche", Criteria.LEFT_JOIN);

		if(findSubCriteria("CommittentePerson")==null)
			entityCriteria.createAlias("Committente.person", "CommittentePerson", Criteria.LEFT_JOIN);

		committente = "%" + committente + "%";

		LogicalExpression givOrFam =  Restrictions.or(
				Restrictions.or(
						Restrictions.like("CommittentePerson.name.giv", committente).ignoreCase(), 
						Restrictions.like("CommittentePerson.name.fam", committente).ignoreCase()),
						Restrictions.like("CommittentePersoneGiuridiche.denominazione", committente).ignoreCase());

		entityCriteria.add(givOrFam);
	}

	public void filterResponsabile(){
		String responsabile = (String)getTemporary().get("responsabile");

		getEqual().put("personeCantiere.ruolo.code", null);
		removeExpression("this", "PersoneCantierePerson.name.giv");
		removeExpression("this", "PersoneCantierePerson.name.fam");
		removeSubCriteria(entityCriteria, "PersoneCantierePerson");
		removeSubCriteria(entityCriteria, "PersoneCantiere");

		if(responsabile==null || responsabile.isEmpty())
			return;

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if(findSubCriteria("PersoneCantiere")==null)
			entityCriteria.createAlias("personeCantiere", "PersoneCantiere", Criteria.INNER_JOIN);

		if(findSubCriteria("PersoneCantierePerson")==null)
			entityCriteria.createAlias("PersoneCantiere.person", "PersoneCantierePerson", Criteria.INNER_JOIN);

		responsabile = "%" + responsabile + "%";

		//CERCO, TRA LE PERSONE DEL CANTIERE QUELLI CON RUOLO RUOLOCANT01=RESPONSABILE LAVORI
		getEqual().put("personeCantiere.ruolo.code", "RUOLOCANT01");
		LogicalExpression givOrFam = Restrictions.or(Restrictions.like("PersoneCantierePerson.name.giv", responsabile).ignoreCase(), 
				Restrictions.like("PersoneCantierePerson.name.fam", responsabile).ignoreCase());

		entityCriteria.add(givOrFam);
	}

	public boolean checkCantiere(){
		boolean isValid = true;
		Cantiere cant = getEntity();
		Context conv = Contexts.getConversationContext();

		if(cant!=null){

			if(cant.getNotifica()){

				String cantType = "";
				if(cant.getTipologiaNotifica()!=null && cant.getTipologiaNotifica().getCode()!=null){
					cantType = cant.getTipologiaNotifica().getCode();
				}

				//c'è almeno un responsabile lavori?
				if(getPersonCount(cant.getInternalId(), "RUOLOCANT01")==0){
					FacesErrorUtils.addErrorMessage("respError", "Responsabile dei lavori obbligatorio.", "Responsabile dei lavori obbligatorio.");
					isValid = false;
				}
				//c'è almeno un coordinatore progettazione per i tipi notifica 2,3,4?
				if(getPersonCount(cant.getInternalId(), "RUOLOCANT02")==0 && "TYPECANT02;TYPECANT03;TYPECANT04;".contains(cantType)){
					FacesErrorUtils.addErrorMessage("progError", "Coordinatore lavori durante progettazione obbligatorio.", "Coordinatore lavori durante progettazione obbligatorio.");
					isValid = false;
				}
				//c'è almeno un coordinatore realizzazione per il tipo notifica 4?
				if(getPersonCount(cant.getInternalId(), "RUOLOCANT03")==0 && "TYPECANT04".equals(cantType)){
					FacesErrorUtils.addErrorMessage("realError", "Coordinatore lavori durante realizzazione obbligatorio.", "Coordinatore lavori durante realizzazione obbligatorio.");
					isValid = false;
				}

				IdataModel<DitteCantiere> ditte = (IdataModel<DitteCantiere>)conv.get("DitteCantiereList");
				if(ditte.size()==0){
					FacesErrorUtils.addErrorMessage("ditteError", "Elenco imprese obbligatorio.", "Elenco imprese obbligatorio.");
					isValid = false;
				}
			}

			//I0062155 - Committente obbligatorio solo per cantieri in Titolo IV
			if (cant.getTitoloIV()==null || cant.getTitoloIV()){
				IdataModel<Committente> comm = (IdataModel<Committente>)conv.get("CommittenteList");
				if(comm==null || comm.size()==0){
					FacesErrorUtils.addErrorMessage("commError", "Committente obbligatorio.", "Committente obbligatorio.");
					isValid = false;
				} /* //I0050418 Punto 1  
				else{
					boolean almenoUnaPersonaFisica = false;
					for(Committente c : comm.getList()){
						if(c.getPerson()!=null){
							almenoUnaPersonaFisica=true;
							break;
						}
					}

					if(!almenoUnaPersonaFisica){
						FacesErrorUtils.addErrorMessage("commError", "Committente: almeno una persona fisica obbligatoria.", "Committente: almeno una persona fisica obbligatoria");
						isValid = false;
					}
				}*/
			}
		}

		if(cant.getDataComunicazione()!=null && cant.getInizioLavori()!=null && cant.getInizioLavori().before(cant.getDataComunicazione())){
			FacesErrorUtils.addErrorMessage("i:MonthCalendar_1464014408258_id", "Data comunicazione non valida.", "Data comunicazione maggiore della data presunta inizio lavori.");
			isValid = false;
		}




		return isValid;
	}

	public String printCommittenti(List<Committente> lst){
		String rtn = "";

		if(lst!=null && !lst.isEmpty()){
			for(Committente comm : lst){
				if(comm.getPerson()!=null && comm.getPerson().getName()!=null)
					rtn += (comm.getPerson().getName().getGiv() + " " + comm.getPerson().getName().getFam() +", ");
				else if(comm.getPersoneGiuridiche()!=null && comm.getPersoneGiuridiche().getDenominazione()!=null)
					rtn += (comm.getPersoneGiuridiche().getDenominazione() +", ");
			}

			if(rtn.length()>2)
				rtn = rtn.substring(0, rtn.length()-2);
		}

		return rtn;
	}

	public String printResponsabili(List<PersoneCantiere> lst){
		String rtn = "";

		if(lst!=null && !lst.isEmpty()){
			for(PersoneCantiere pers : lst){
				if(pers.getRuolo()!=null && "RUOLOCANT01".equals(pers.getRuolo().getCode()) 
						&& pers.getPerson()!=null && pers.getPerson().getName()!=null)
					rtn += (pers.getPerson().getName().getGiv() + " " + pers.getPerson().getName().getFam() +", ");
			}

			if(!rtn.isEmpty())
				rtn = rtn.substring(0, rtn.length()-2);
		}

		return rtn;
	}

	private Long getPersonCount(Long cantId, String ruolo){
		/*qPersone.setParameter("cantId", cantId);
		qPersone.setParameter("ruolo", ruolo);
		Long count = (Long)qPersone.getSingleResult();
		if(count!=null && count>0)
			return count;
		else 
			return 0L;*/ 

		Long rtn = 0L;
		Context conv = Contexts.getConversationContext();
		if("RUOLOCANT01".equals(ruolo)){
			IdataModel<PersoneCantiere> lst = (IdataModel<PersoneCantiere>)conv.get("PersoneCantiere1List");
			rtn = Integer.valueOf(lst.size()).longValue();
		}else if("RUOLOCANT02".equals(ruolo)){
			IdataModel<PersoneCantiere> lst = (IdataModel<PersoneCantiere>)conv.get("PersoneCantiere2List");
			rtn = Integer.valueOf(lst.size()).longValue();
		}else if("RUOLOCANT03".equals(ruolo)){
			IdataModel<PersoneCantiere> lst = (IdataModel<PersoneCantiere>)conv.get("PersoneCantiere3List");
			rtn = Integer.valueOf(lst.size()).longValue();
		}
		return rtn;
	}

	public void linkUnlinkCommittente(List<Committente> link, List<Committente> unlink){
		if(entity==null)
			return;

		if(entity.getCommittente()==null)
			entity.setCommittente(new ArrayList<Committente>());

		if(link!=null){			
			for(Committente comm : link){
				entity.getCommittente().add(comm);
				comm.setCantiere(entity);
			}
		}
		if(unlink!=null){
			for(Committente comm : unlink){
				entity.getCommittente().remove(comm);
				comm.setCantiere(null);
			}
		}
	}

	public void linkUnlinkPersoneCantiere(List<PersoneCantiere> link, List<PersoneCantiere> unlink){
		if(entity==null)
			return;

		if(entity.getPersoneCantiere()==null)
			entity.setPersoneCantiere(new ArrayList<PersoneCantiere>());

		if(link!=null){			
			for(PersoneCantiere pers : link){
				entity.getPersoneCantiere().add(pers);
				pers.setCantiere(entity);
			}
		}
		if(unlink!=null){
			for(PersoneCantiere pers : unlink){
				entity.getPersoneCantiere().remove(pers);
				pers.setCantiere(null);
			}
		}
	}

	public void linkUnlinkDitteCantiere(List<DitteCantiere> link, List<DitteCantiere> unlink){
		if(entity==null)
			return;

		if(entity.getDitteCantiere()==null)
			entity.setDitteCantiere(new ArrayList<DitteCantiere>());

		if(link!=null){			
			for(DitteCantiere ditta : link){
				entity.getDitteCantiere().add(ditta);
				ditta.setCantiere(entity);
			}
		}
		if(unlink!=null){
			for(DitteCantiere ditta : unlink){
				entity.getDitteCantiere().remove(ditta);
				ditta.setCantiere(null);
			}
		}
	}

	public void linkUnlinkTagCantiere(List<TagCantiere> link, List<TagCantiere> unlink){
		if(entity==null)
			return;

		if(entity.getTagCantiere()==null)
			entity.setTagCantiere(new ArrayList<TagCantiere>());

		List<TagCantiere> lista = entity.getTagCantiere();
		if(link!=null){			
			for(TagCantiere tag : link){
				if(!lista.contains(tag)){
					entity.getTagCantiere().add(tag);
					tag.setCantiere(entity);
				}

			}
		}
		if(unlink!=null){
			for(TagCantiere tag : unlink){
				if(lista.contains(tag)){
					entity.getTagCantiere().remove(tag);
					tag.setCantiere(null);
				}
			}
		}
	}

	public void checkPratiche(Long cantiereId){
		if(cantiereId!=null && cantiereId>0){
			String qryPratiche = "SELECT DISTINCT p FROM Procpratiche p " +
					"LEFT JOIN p.cantiere c1 " +
					"LEFT JOIN p.infortuni i LEFT JOIN i.cantiere c2 " +
					"LEFT JOIN p.vigilanza v LEFT JOIN v.cantiere c3 " +
					"LEFT JOIN p.attivita a LEFT JOIN a.luogoCantiere c4 " +
					"LEFT JOIN a.soggetto s LEFT JOIN s.cantiere c5 " +
					"LEFT JOIN p.protocollo pr " +
					"LEFT JOIN pr.riferimentoCantiere c6 " +
					"LEFT JOIN pr.ubicazioneCantiere c7 " +
					"WHERE c1.internalId = :id OR c2.internalId = :id OR c3.internalId = :id " +
					"OR c4.internalId = :id OR c5.internalId = :id OR c6.internalId = :id";

			Query qPratiche = ca.createQuery(qryPratiche);
			qPratiche.setParameter("id", cantiereId);
			List<Procpratiche> lst = qPratiche.getResultList();
			if(lst!=null && !lst.isEmpty()){
				ProcpraticheAction pAction = ProcpraticheAction.instance();
				pAction.injectList(lst);
				getTemporary().put("hasPratiche",true);
			}
		}
	}

	public boolean checkDuplicates(Cantiere cant){

		boolean result = false; 

		if(cant.getInternalId()==0){

			String qryDuplicateAddressCheck = "SELECT COUNT(c) FROM Cantiere c " +
					"WHERE addr.str = :str and addr.bnr = :bnr and addr.cpa = :cpa and addr.zip = :zip and addr.code = :code and lastVersion = 1";

			Query duplicateAddressCheck = ca.createQuery(qryDuplicateAddressCheck);
			duplicateAddressCheck.setParameter("str", cant.getAddr().getStr().trim());
			duplicateAddressCheck.setParameter("bnr", cant.getAddr().getBnr().trim());
			duplicateAddressCheck.setParameter("cpa", cant.getAddr().getCpa().trim());
			duplicateAddressCheck.setParameter("zip", cant.getAddr().getZip().trim());
			duplicateAddressCheck.setParameter("code", cant.getAddr().getCode());


			Long countP = (Long)duplicateAddressCheck.getSingleResult();
			if(countP!=null && countP>0){
				result=true;
			}

		}
		return result;
	}

	public void filterNotifica() {
		try {
			HashMap<String, Object> temp = this.getTemporary();
			Boolean si = false;
			Boolean no = false;

			if (!temp.isEmpty()){
				Object notificaSi = temp.get("notificaSi");
				if (notificaSi!=null && (Boolean)notificaSi)
					si = true;

				Object notificaNo = temp.get("notificaNo");
				if (notificaNo!=null && (Boolean)notificaNo)
					no = true;
			}

			// il filtro idPnc prevale sugli altri
			if(this.getEqual().get("idPnc")==null || ((String) this.getEqual().get("idPnc")).isEmpty()){
				if (si ^ no) {
					if (si)
						((FilterMap)getEqual()).put("notifica", true);
					else 
						((FilterMap)getEqual()).put("notifica", false);

				} else 
					((FilterMap)getEqual()).remove("notifica");
			}else{
				((FilterMap)getEqual()).remove("notifica");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void filterTitoloIV() {
		try {
			((FilterMap)getEqual()).remove("titoloIV");

			HashMap<String, Object> temp = this.getTemporary();
			Boolean si = false;
			Boolean no = false;

			if (!temp.isEmpty()){
				Object titoloIVSi = temp.get("titoloIVSi");
				if (titoloIVSi!=null && (Boolean)titoloIVSi)
					si = true;

				Object titoloIVNo = temp.get("titoloIVNo");
				if (titoloIVNo!=null && (Boolean)titoloIVNo)
					no = true;
			}
			
			// il filtro idPnc prevale sugli altri
			if(this.getEqual().get("idPnc")==null || ((String) this.getEqual().get("idPnc")).isEmpty()){
				if (!(si && no)) {
					if (si)
						((FilterMap)getEqual()).putOr("titoloIV", true, null);
					else if (no)
						((FilterMap)getEqual()).put("titoloIV", false);
				}
			}else{
				((FilterMap)getEqual()).remove("titoloIV");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String computeClass(Cantiere cantiere){
		String rtn = "";

		if(cantiere.getIdPnc()==null || cantiere.getIdPnc().isEmpty())
			return rtn;

		rtn = "fa icona fa-2x";

		if(Boolean.TRUE.equals(cantiere.getPncForced())){
			rtn = rtn.replace("icona", "fa-question-circle");
			return rtn;
		}
		if(cantiere.getOriginal()==null){
			rtn = rtn.replace("icona", "fa-display-medical");
			return rtn;
		}

		rtn = rtn.replace("icona", "fa-display-medical-arrow-down");
		return rtn;
	}

	public boolean hasChildren(Cantiere cantiere) throws PhiException{
		if(cantiere==null)
			return false;

		// Il cantiere è stato creato in Sipral ed è l'ultima versione: non occorre cercare discendenti
		if(/*(cantiere.getIdPnc()==null || cantiere.getIdPnc().isEmpty()) && */(Boolean.TRUE.equals(cantiere.getLastVersion())))
			return false;

		cleanRestrictions();
		getEqual().put("isActive", true);
		getEqual().put("id", cantiere.getId());

		//		if(cantiere.getOriginal()!=null)
		//			getEqual().put("original.internalId", cantiere.getOriginal().getInternalId());

		if(cantiere.getIdPnc()==null || cantiere.getIdPnc().isEmpty())
			getEqual().put("lastVersion", true);

		List<Cantiere> childrenList = select();
		Iterator<Cantiere> childrenIterator = childrenList.iterator();
		// Tra i cantieri all'ultima versione c'è il cantiere stesso; rimosso dai discendenti
		// oppure
		// il cantiere all'ultima versione e il cantiere di partenza hanno lo stesso genitore: vuol dire che il
		// cantiere di partenza è un clone del cantiere all'ultima versione
		// e non ci sono versioni più aggiornate
		// oppure
		// il cantiere all'ultima versione non ha un genitore ed è invece genitore del cantiere di partenza:
		// vuol dire che non ci sono versioni più aggiornate
		while(childrenIterator.hasNext()){
			Cantiere child = (Cantiere)childrenIterator.next();
			if(child.getInternalId()==cantiere.getInternalId() || 
					(child.getOriginal()!=null && child.getOriginal().equals(cantiere.getOriginal())) ||
					(child.getOriginal()==null && child.equals(cantiere.getOriginal())))
				childrenIterator.remove();
		}
		if(childrenList!=null && !childrenList.isEmpty())
			return true;

		return false;
	}

	public void filterPnc(){
		Boolean fromPnc = (Boolean)getTemporary().get("fromPnc");

		removeExpression("this", "not idPnc");

		// il filtro idPnc prevale sugli altri
		if(this.getEqual().get("idPnc")!=null && !((String) this.getEqual().get("idPnc")).isEmpty()){
			((FilterMap)getEqual()).remove("lastVersion");
			return;
		}
		
		if(fromPnc==null || Boolean.FALSE.equals(fromPnc))
			return;

		((FilterMap)getEqual()).remove("notifica");

		entityCriteria.add(Restrictions.not(Restrictions.eq("idPnc", "")));
		entityCriteria.add(Restrictions.not(Restrictions.isNull("idPnc")));

	}

	public AD fixAddressPNC(AD addr) throws PersistenceException, DictionaryException {
		if(addr.getCode()!=null){
			// il comune da PNC arriva da COMUNIISTAT mentre in Sipral i comuni sono del cs Comuni
			addr.setCode((CodeValueCity) VocabulariesImpl.instance().getCodeValueCsDomainCode("Comuni", null, addr.getCode().getCode()));
			if(addr.getCpa()==null)
				addr.setCpa(addr.getCode().getProvince());

			if(addr.getCty()==null)
				addr.setCty(addr.getCode().getLangIt());

			if(addr.getZip()==null)
				addr.setZip(addr.getCode().getZip());
		}

		return addr;
	}
	
	public Cantiere cloneUntilEditable(Cantiere toCopy) throws PersistenceException{
		//Clona ripetutamente il cantiere fino ad ottenere un cantiere non notificato quindi modificabile
		
		Cantiere copy = null;
		
		do{
			toCopy.setLastVersion(false);
			
			copy = copy(toCopy);
			
			copy.setLastVersion(true);
			
			ca.persist(copy);
			
			closeAllPreviousVersionsOf(copy);
			
			toCopy = copy;
		}while(copy.getNotifica());
		
		//ca.flushSession();
		
		return copy;
	}
}