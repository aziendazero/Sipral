package com.phi.entities.actions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.ImpiantiDocument;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.ScadenzaTipoCom;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parameters.ParameterManager;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("ProtocolloAction")
@Scope(ScopeType.CONVERSATION)
public class ProtocolloAction extends BaseAction<Protocollo, Long> {

	private static final long serialVersionUID = -3493327414507855797L;

	public static ProtocolloAction instance() {
		return (ProtocolloAction) Component.getInstance(ProtocolloAction.class, ScopeType.CONVERSATION);
	}

	private static final Logger log = Logger.getLogger(ProtocolloAction.class);
	private UserBean ub = (UserBean) Component.getInstance("userBean");

	private static String UOSList = "SELECT sdl FROM ServiceDeliveryLocation sdl " +
			"WHERE sdl.code.code = 'UOS' AND sdl.internalId IN (:sdLocs)";

	private static String ScadenzeList = "SELECT stc FROM ScadenzaTipoCom stc " +
			"WHERE stc.ulss.internalId = :sdlId AND stc.code.code = :typeCode order by stc.creationDate desc";

	public void filterBy() {
		try {
			Object ric = getTemporary().get("richiedente");
			Object rif = getTemporary().get("riferimento");
			Object ubi = getTemporary().get("ubicazione");

			if (ric != null) {
				String richiedente = ((CodeValue)ric).getCode();

				if (richiedente != null) 
					((FilterMap)getEqual()).put("richiedente.code", richiedente);
			} else 
				((FilterMap)getEqual()).remove("richiedente.code");

			if (rif != null) {
				String riferimento = ((CodeValue)rif).getCode();

				if (riferimento != null) 
					((FilterMap)getEqual()).put("riferimento.code", riferimento);
			} else 
				((FilterMap)getEqual()).remove("riferimento.code");

			if (ubi != null) {
				String ubicazione = ((CodeValue)ubi).getCode();

				if (ubicazione != null) 
					((FilterMap)getEqual()).put("ubicazione.code", ubicazione);
			} else 
				((FilterMap)getEqual()).remove("ubicazione.code");

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	} 

	public void setStates() {
		try {
			List<String> status = new ArrayList<String>();


			Object allstatesTmp = this.getTemporary().get("allstates");
			Boolean allstates = (allstatesTmp!=null && (Boolean)allstatesTmp);

			// Add three elements.
			//status.add("status.generic.new_V0");
			status.add("status.generic.held_V0");
			status.add("status.generic.active_V0");

			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				Object new_ = temp.get("new");
				if ((new_!=null && (Boolean)new_) || allstates)
					status.add("status.generic.new_V0");

				Object completed = temp.get("completed");
				if ((completed!=null && (Boolean)completed) || allstates)
					status.add("status.generic.completed_V0");

				Object cancelled = temp.get("cancelled");
				if ((cancelled!=null && (Boolean)cancelled) || allstates)
					status.add("status.generic.cancelled_V0");

				Object nullified = temp.get("nullified");
				if ((nullified!=null && (Boolean)nullified) || allstates)
					status.add("status.generic.nullified_V0");

				Object terminated = temp.get("terminated");
				if ((terminated!=null && (Boolean)terminated) || allstates)
					status.add("status.generic.terminated_V0");

				Object obsolete = temp.get("obsolete");
				if ((obsolete!=null && (Boolean)obsolete) || allstates)
					status.add("status.generic.obsolete_V0");
			}

			((FilterMap)getEqual()).putOr("statusCode.id", status.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}

	public void resetStates() {

		Object allstatesTmp = this.getTemporary().get("allstates");
		Boolean allstates = (allstatesTmp!=null && (Boolean)allstatesTmp);
		if(allstates){
			this.getTemporary().remove("new");
			this.getTemporary().remove("completed");
			this.getTemporary().remove("cancelled");
			this.getTemporary().remove("nullified");
			this.getTemporary().remove("terminated");
			this.getTemporary().remove("obsolete");
		}



	}


	public void initializeComplexTypes() {
		Context conv = Contexts.getConversationContext();		
		conv.set("ComplexTypesList", new ArrayList<String>());
		getTemporary().put("complexTypes", new ArrayList<CodeValuePhi>());

	}

	public void readComplexTypes(List<CodeValuePhi> codeList){
		Context conv = Contexts.getConversationContext();
		List<String> codeIds = new ArrayList<String>();
		if(codeList!=null && !codeList.isEmpty()){
			for(CodeValuePhi cv : codeList){
				if(!codeIds.contains(cv.getId()))
					codeIds.add(cv.getId());
			}
		}

		conv.set("ComplexTypesList", codeIds);
	}

	public void setComplexTypesFilter(List<String> codeIds) {
		if(codeIds!=null && !codeIds.isEmpty()){
			List<CodeValuePhi> complexCodes = new ArrayList<CodeValuePhi>();
			for(String id : codeIds){ 
				CodeValuePhi cv = ca.get(CodeValuePhi.class, id);
				complexCodes.add(cv);
			}
			getTemporary().put("complexTypes", complexCodes);
		}else{
			getTemporary().put("complexTypes", new ArrayList<CodeValuePhi>());
		}		
	}

	public String printComplexTypes(List<CodeValuePhi> codeList){

		String rtn ="";
		if(codeList!=null && !codeList.isEmpty()){
			for(CodeValuePhi cv : codeList){
				if(cv.getChildren()==null || cv.getChildren().isEmpty()){//solo le foglie
					rtn+=cv.getCurrentTranslation();
					rtn+=", ";
				}
			}

			if(rtn.length()>=2){
				rtn=rtn.substring(0, rtn.length()-2);
			}
		}
		return rtn;
	}

	public void setCodes() {
		try {
			List<String> codes = new ArrayList<String>();
			HashMap<String, Object> temp = this.getTemporary();

			if (!temp.isEmpty()){
				@SuppressWarnings("unchecked")
				List<CodeValuePhi> complexTypes = (List<CodeValuePhi>)temp.get("complexTypes");

				Iterator<CodeValuePhi> complexTypesIterator = null;

				if (complexTypes != null) {
					complexTypesIterator = complexTypes.iterator();

					while (complexTypesIterator.hasNext()) {
						CodeValuePhi complexType = complexTypesIterator.next();

						if (complexType != null)
							codes.add(complexType.getId());
					}
				}
			}

			if (codes.size()>0)
				((FilterMap)getEqual()).putOr("code.id", codes.toArray()); 

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void setWorkingLine(String filter) {
		try {

			if (this.getEqual() != null ) {
				Object area = this.getEqual().get(filter);//uos.area in segnalazioni.mmgp

				if (area!=null ) {
					String code = ((CodeValue)area).getCode();
					if (code!=null && !"".equals(code))
						this.getTemporary().put("workingLine", code);
					else 
						this.getTemporary().remove("workingLine");
				} else {

					//Set workingLine for users able to act in a single line
					List<Long> sdLocs = ub.getSdLocs();

					if (sdLocs != null && sdLocs.size()>0) {
						Query qry = ca.createHibernateQuery(UOSList);
						qry.setParameterList("sdLocs", sdLocs);

						@SuppressWarnings("unchecked")
						List<ServiceDeliveryLocation> UOSList = (List<ServiceDeliveryLocation>)qry.list();

						if (UOSList.size() == 1) {
							String workingLine = UOSList.get(0).getArea().getCode();
							this.getTemporary().put("workingLine", workingLine);

						}
					} else 
						this.getTemporary().remove("workingLine");
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public AD copyAddr(AD addr){
		try {
			AD addrToReturn = new AD(addr.getAdl(), addr.getBnr(),
					addr.getCen(), addr.getCnt(), addr.getCpa(), addr.getCty(),
					addr.getSta(), addr.getStb(), addr.getStr(), addr.getZip());

			return addrToReturn;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public String getRiferimenti(String riferimenti){
		try{
			Protocollo protocollo = getEntity();

			if (riferimenti==null || protocollo==null)
				return "";

			if (riferimenti.equals("richiedente")){
				//Ditta, Utente, Interno, Medico, Anonimo
				CodeValue richiedente = protocollo.getRichiedente();

				if (richiedente != null){
					String code = richiedente.getCode();
					if (code != null){
						if (code.equals("Anonimo"))
							return " (" + richiedente.getCurrentTranslation() + ")";

						if (code.equals("Medico")){
							String ret = "";
							Physician medico = protocollo.getRichiedenteMedico();
							if (medico!=null){
								ret += " " + medico.getName().getFam() + " " + medico.getName().getGiv();
							}
							return ret;
						}

						if (code.equals("Ditta")) {
							String ret = "";

							PersoneGiuridiche pg = protocollo.getRichiedenteDitta();
							if (pg!=null && pg.getDenominazione()!=null){
								ret += " " + pg.getDenominazione();

								Sedi sede = protocollo.getRichiedenteSede();
								if (sede!=null && sede.getDenominazioneUnitaLocale()!=null)
									ret += " sede: " + sede.getDenominazioneUnitaLocale();
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}

						if (code.equals("Interno")) {
							String ret = "";

							Employee interno = protocollo.getRichiedenteInterno();
							if (interno!=null){
								ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
								//+ " CF: " + interno.getFiscalCode();
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}

						if (code.equals("Utente")) {
							String ret = "";

							Person person = protocollo.getRichiedenteUtente();
							if (person!=null){
								ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
								//+ " CF: " + person.getFiscalCode();
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}

					}
				}
			}

			if (riferimenti.equals("riferimento")){
				//NonPrevisto, Ditta, Interno, Utente, Cantiere
				CodeValue riferimento = protocollo.getRiferimento();

				if (riferimento != null){
					String code = riferimento.getCode();
					if (code != null){
						if (code.equals("NonPrevisto"))
							return " (" + riferimento.getCurrentTranslation() + ")";

						if (code.equals("Altro")){
							String ret = "";
							if (protocollo.getRiferimentoEntita()!=null){
								String altro = this.getAltro(protocollo.getRiferimentoEntita().getCurrentTranslation(), protocollo.getRiferimentoIMO(), protocollo.getRiferimentoTarga());
								ret = altro.substring(2);
							}
							return ret;
						}

						if (code.equals("Cantiere")) {
							String ret = "";

							Cantiere c = protocollo.getRiferimentoCantiere();
							if (c!=null && c.getAddr()!=null){
								ret += " " + c.getAddr();

							}

							return ret + " (" + riferimento.getCurrentTranslation() + ")";
						}

						if (code.equals("Ditta")) {
							String ret = "";

							PersoneGiuridiche pg = protocollo.getRiferimentoDitta();
							if (pg!=null && pg.getDenominazione()!=null){
								ret += " " + pg.getDenominazione();

								Sedi sede = protocollo.getRiferimentoSede();
								if (sede!=null && sede.getDenominazioneUnitaLocale()!=null)
									ret += " sede: " + sede.getDenominazioneUnitaLocale();
							}

							return ret + " (" + riferimento.getCurrentTranslation() + ")";
						}

						if (code.equals("Interno")) {
							String ret = "";

							Employee interno = protocollo.getRiferimentoInterno();
							if (interno!=null){
								ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
								//+ " CF: " + interno.getFiscalCode();
							}

							return ret + " (" + riferimento.getCurrentTranslation() + ")";
						}

						if (code.equals("Utente")) {
							String ret = "";

							Person person = protocollo.getRiferimentoUtente();
							if (person!=null){
								ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
								//+ " CF: " + person.getFiscalCode();
							}

							return ret + " (" + riferimento.getCurrentTranslation() + ")";
						}

					}
				}
			}

			if (riferimenti.equals("ubicazione")){
				//NonPrevisto, Ditta, Cantiere, Altro
				CodeValue ubicazione = protocollo.getUbicazione();

				if (ubicazione != null){
					String code = ubicazione.getCode();
					if (code != null){
						if (code.equals("NonPrevisto"))
							return " (" + ubicazione.getCurrentTranslation() + ")";
					}
				}

				String ret = "";
				AD ad = protocollo.getUbicazioneAddr();

				if (ad!=null){
					if (ad.getCty() !=null && ad.getCty()!=""){
						ret += ad.getCty();

						if (ad.getStr()!= null && ad.getStr()!= ""){
							ret += " - Via " + ad.getStr();

							if (ad.getBnr()!= null && ad.getBnr()!= "")
								ret += " - N° " + ad.getBnr();
						}
					}
				}

				return ret;
			}

			return "";

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void setUlss() {
		try {
			getEqual().remove("serviceDeliveryLocation.parent.internalId");
			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");

				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.parent.internalId", id);
				}
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void setUos() {
		try {
			getEqual().remove("uos.internalId");
			List<Long> uosIds = new ArrayList<Long>();

			//Get workingLine
			List<Long> sdLocs = ub.getSdLocs();

			if (sdLocs != null && sdLocs.size()>0) {
				Query qry = ca.createHibernateQuery(UOSList);
				qry.setParameterList("sdLocs", sdLocs);

				@SuppressWarnings("unchecked")
				List<ServiceDeliveryLocation> UOSList = (List<ServiceDeliveryLocation>)qry.list();
				if (UOSList!=null){
					for(ServiceDeliveryLocation sdl:UOSList){
						uosIds.add(sdl.getInternalId());
					}
				}
			}

			uosIds.add(null);

			if (uosIds != null) 
				((FilterMap)getEqual()).putOr("uos.internalId", uosIds.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void injectMaster(List<Protocollo> lst){
		if(lst==null || lst.isEmpty()) {
			eject();
			return;
		}

		for(Protocollo prot : lst){
			if(prot.getIsMaster()){
				inject(prot);
				break;
			}
		}
	}

	public boolean hasDitteAssociate(Protocollo p){

		boolean result = false;

		CodeValue riferimento = p.getRiferimento();

		if (riferimento != null){
			String code = riferimento.getCode();
			if (code != null){
				if (code.equals("Ditta")){

					PersoneGiuridiche pg = p.getRichiedenteDitta();
					if (pg!=null && pg.getDenominazione()!=null){
						result = true;
					}

				}
			}
		}

		CodeValue richiedente = p.getRichiedente();

		if (richiedente != null){
			String code = richiedente.getCode();
			if (code != null){

				if (code.equals("Ditta")) {

					PersoneGiuridiche pg = p.getRichiedenteDitta();
					if (pg!=null && pg.getDenominazione()!=null){
						result = true;
					}
				}
			}
		}

		return result;

	}

	public void filterCommittente(){
		String committente = (String)getTemporary().get("committente");

		if(committente==null || committente.isEmpty())
			return;

		/*
		 * IL COMMITTENTE PUO' ESSERE UNA PERSONA FISICA O GIURIDICA
		 */
		removeExpression("this", "CommittentePerson.name.giv");
		removeExpression("this", "CommittentePerson.name.fam");
		removeExpression("this", "CommittentePersoneGiuridiche.denominazione");
		removeSubCriteria(entityCriteria, "CommittentePerson");
		removeSubCriteria(entityCriteria, "CommittentePersoneGiuridiche");
		removeSubCriteria(entityCriteria, "Committente");
		removeSubCriteria(entityCriteria, "RiferimentoCantiere");

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		if(findSubCriteria("RiferimentoCantiere")==null)
			entityCriteria.createAlias("riferimentoCantiere", "RiferimentoCantiere", Criteria.INNER_JOIN);

		if(findSubCriteria("Committente")==null)
			entityCriteria.createAlias("RiferimentoCantiere.committente", "Committente", Criteria.INNER_JOIN);

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


	/**
	 * Filter Protocollo by Protocollo.riferimentoCantiere.personeCantiere.person.name.fam or giv 
	 * 
	 * Filter by Responsabile or Coordinatori  durante progettazione or Coordinatori durante realizzazione since they are in the same table
	 * 
	 * Used by: /MOD_home/CORE/FORMS/segnalazioni.mmgp
	 */
	private String filterByRespOrCoord;

	public String getFilterByRespOrCoord() {
		return filterByRespOrCoord;
	}

	public void setFilterByRespOrCoord(String filterByRespOrCoord) {
		this.filterByRespOrCoord = filterByRespOrCoord;

		removeExpression("this", "RiferimentoCantierePersoneCantierePerson.name.giv");
		removeExpression("this", "RiferimentoCantierePersoneCantierePerson.name.fam");
		removeSubCriteria(entityCriteria, "RiferimentoCantierePersoneCantierePerson");
		removeSubCriteria(entityCriteria, "RiferimentoCantierePersoneCantiere");
		// removeSubCriteria(entityCriteria, "RiferimentoCantiere");

		if(filterByRespOrCoord==null || filterByRespOrCoord.isEmpty())
			return;

		if(entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
		}

		Criteria vCrit = findSubCriteria("RiferimentoCantiere");
		if(vCrit == null) {
			vCrit = entityCriteria.createCriteria("riferimentoCantiere", "RiferimentoCantiere", Criteria.LEFT_JOIN);
		}

		Criteria vcCrit = findSubCriteria("RiferimentoCantierePersoneCantiere");
		if(vcCrit == null) {
			vcCrit = vCrit.createCriteria("personeCantiere", "RiferimentoCantierePersoneCantiere", Criteria.LEFT_JOIN);
		}

		Criteria vccCrit = findSubCriteria("RiferimentoCantierePersoneCantierePerson");
		if(vccCrit == null) {
			vccCrit = vcCrit.createCriteria("person", "RiferimentoCantierePersoneCantierePerson", Criteria.LEFT_JOIN);
		}

		filterByRespOrCoord = "%" + filterByRespOrCoord + "%";

		LogicalExpression givOrFam =  Restrictions.or(
				Restrictions.like("RiferimentoCantierePersoneCantierePerson.name.giv", filterByRespOrCoord).ignoreCase(), 
				Restrictions.like("RiferimentoCantierePersoneCantierePerson.name.fam", filterByRespOrCoord).ignoreCase());

		entityCriteria.add(givOrFam);
	}



	public String getAltro(String riferimentoEntita, String riferimentoIMO, String riferimentoTarga){
		try{
			String ret = "A:";
			if (riferimentoEntita==null)
				return ret;

			ret += riferimentoEntita;

			if (riferimentoEntita.equals("Mezzo automobilistico") && riferimentoTarga!=null && riferimentoTarga!="")
				ret += " (" + riferimentoTarga + ")";
			else if (riferimentoEntita.equals("Natante") && riferimentoIMO!=null && riferimentoIMO!="")
				ret += " (" + riferimentoIMO + ")";

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public List<SelectItem> getRadioList(String riferimento, String tipoSegnalazione) throws DictionaryException{
		List<SelectItem> out = new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();

		if("richiedente".equals(riferimento)){
			if("5".equals(tipoSegnalazione)){
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Utente");
			} else //DEFAULT
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Utente,Interno,Medico,Anonimo");

		} else if("riferimento".equals(riferimento)) {

			//Medicina del Lavoro - il riferimento prevede solo il campo Utente
			Protocollo protocollo = getEntity();
			if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null && protocollo.getUos().getArea().getCode().equals("WORKMEDICINE"))

				return voc.selectCodeValues("PHIDIC", "TargetSource:Utente");

			else if("4".equals(tipoSegnalazione)) {
				return voc.selectCodeValues("PHIDIC", "TargetSource:Cantiere");
			} else if("5".equals(tipoSegnalazione)) {
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Utente");
			} else if("13.3".equals(tipoSegnalazione)){
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta");
			} else 	//DEFAULT
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Cantiere,Utente,Altro");


		} else if("ubicazione".equals(riferimento)){
			//DEFAULT
			if("5".equals(tipoSegnalazione)){
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Cantiere,Altro");
			} else 
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Cantiere,Altro,NonPrevisto");
		}else if("committenteBonifica".equals(riferimento)){
			if("5".equals(tipoSegnalazione)){
				return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Utente");
			}
		}

		return out;
	}

	public void injectWorkingLine(Protocollo prot, CodeValuePhi code){
		if(prot!=null && code!=null && prot.getServiceDeliveryLocation()!=null){
			Collection<CodeValuePhi> relations = code.getRelationsPhi();
			if(relations!=null && !relations.isEmpty()){
				CodeValuePhi workingLine = relations.iterator().next();
				String hql = "SELECT uos FROM ServiceDeliveryLocation uos JOIN uos.parent uoc JOIN uos.area area " +
						"WHERE area.oid = :oid AND uoc.internalId = :uocId";

				javax.persistence.Query q = ca.createQuery(hql);
				q.setParameter("oid", workingLine.getOid());
				q.setParameter("uocId", prot.getServiceDeliveryLocation().getInternalId());

				List<ServiceDeliveryLocation> lst = q.getResultList();
				if(lst!=null && !lst.isEmpty()){
					UOSAction.instance().inject(lst.get(0));
				}else{
					UOSAction.instance().eject();
				}
			}
		}
	}

	public void cleanWorkingLine() {
		try {
			Protocollo protocollo = getEntity();
			if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null 
					&& protocollo.getUos().getArea().getCode().equals("WORKACCIDENT")){

				Infortuni infortunio = protocollo.getInfortunio();
				if(infortunio!=null){
					infortunio.setProtocollo(null); //piu efficiente
					ca.delete(infortunio);
					protocollo.setInfortunio(null);

					ca.flushSession();
				}

				InfortuniAction.instance().eject();

			} else if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null
					&& protocollo.getUos().getArea().getCode().equals("WORKDISEASE")){

				MalattiaProfessionale malProf = protocollo.getMalattiaProfessionale();
				if(malProf!=null){
					malProf.setProtocollo(null); //piu efficiente
					ca.delete(malProf);
					protocollo.setMalattiaProfessionale(null);

					ca.flushSession();
				}
				MalattiaProfessionaleAction.instance().eject();

			} else if (protocollo!=null && protocollo.getUos()!=null && protocollo.getUos().getArea()!=null
					&& protocollo.getUos().getArea().getCode().equals("SUPERVISION")){
				List<DettagliBonifiche> dettagli = protocollo.getDettagliBonifiche();
				DettagliBonificheAction dettagliAction = DettagliBonificheAction.instance();
				if(dettagli!=null){
					for (DettagliBonifiche dettaglio : dettagli){
						dettaglio.setProtocollo(null);
						ca.delete(dettaglio);
					}
					protocollo.setDettagliBonifiche(null);

					ca.flushSession();
				}				
				dettagliAction.eject();
				dettagliAction.ejectList();

			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	/**
	 * If Protocollo is already linked to cantiere, return cantiere otherwise return a new clone
	 * @param cantiere
	 * @return 
	 * @throws PhiException 
	 */
	public Cantiere getCloneOrRevision(Cantiere cantiere) throws PhiException {
		getEntity();

		if (cantiere == null) {
			log.error("Unable to clone: cantiere is null!");
			return null;
		}

		Cantiere originale = cantiere;
		if (cantiere.getOriginal() != null) {
			originale = cantiere.getOriginal();
		}
		if (entity.getRiferimentoCantiere() != null) {
			if (originale.equals(entity.getRiferimentoCantiere().getOriginal())) {
				return entity.getRiferimentoCantiere();
			}
		}
		if (entity.getUbicazioneCantiere() != null) {
			if (originale.equals(entity.getUbicazioneCantiere().getOriginal())) {
				return entity.getUbicazioneCantiere();
			}
		}
		CantiereAction cAction = CantiereAction.instance();
		Cantiere clone = cAction.copy(cantiere);

		cAction.inject(clone);
		cAction.create();

		return clone;
	}


	/**
	 * Same as:
	 * 
	 * not empty Sedi ? Protocollo.setUbicazioneAddr(ProtocolloAction.copyAddr(Sedi.addr)) : (not empty Cantiere ? Protocollo.setUbicazioneAddr(ProtocolloAction.copyAddr(Cantiere.addr)) : '')
	 * (not empty PersoneGiuridiche and empty Sedi) ? Protocollo.setUbicazioneAddr(ProtocolloAction.copyAddr(PersoneGiuridicheAction.getAddrSedePrincipale(PersoneGiuridiche))) : ''
	 * 
	 * but sometimes:
	 * Not a Valid Method Expression: ${(not empty PersoneGiuridiche and empty Sedi) ? Protocollo.setUbicazioneAddr(ProtocolloAction.copyAddr(PersoneGiuridicheAction.getAddrSedePrincipale(PersoneGiuridiche))) : ''}
	 * see: http://support.insielmercato.it/mantis/view.php?id=33371
	 * @param sedi
	 * @param cantiere
	 * @param personeGiuridiche
	 */
	public void setUbicazioneAddr(Sedi sedi, Cantiere cantiere, PersoneGiuridiche personeGiuridiche ) {
		getEntity();

		if (sedi != null && sedi.getAddr() != null) {
			entity.setUbicazioneAddr(sedi.getAddr().cloneAd());
		} else if (cantiere != null && cantiere.getAddr() != null) {
			entity.setUbicazioneAddr(cantiere.getAddr().cloneAd());
		} else if (personeGiuridiche != null) {
			AD addrSedePrincipale = PersoneGiuridicheAction.instance().getAddrSedePrincipale(personeGiuridiche);
			if (addrSedePrincipale != null) {
				entity.setUbicazioneAddr(addrSedePrincipale.cloneAd());
			}
		}
	}


	/*public int setAnnoProtocolloFromDate(Date d){
		getEntity();
		int result = 0;

		if (d!=null && entity.getAnnoProtocollo() == null) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			result = cal.get(Calendar.YEAR);

			entity.setAnnoProtocollo(new BigDecimal(result));
		}
		return result;
	}*/

	public void clearUbicazioni(){
		getEntity();

		entity.setUbicazioneDitta(null);
		entity.setUbicazioneSede(null);
		entity.setUbicazioneUtente(null);
		entity.setUbicazioneCantiere(null);
		entity.setUbicazioneEntita(null);
		entity.setUbicazioneIMO(null);
		entity.setUbicazioneTarga(null);
		entity.setUbicazioneSpec(null);
		entity.setUbicazioneAddr(null);
		entity.setUbicazioneX(null);
		entity.setUbicazioneY(null);
		entity.setUbicazioneLocalita(null);
	}

	public void revertStatus(Protocollo prot) throws PersistenceException, DictionaryException{
		getTemporary().put("stateMessage", null);
		if(prot!=null && prot.getStatusCode()!=null && !"new".equals(prot.getStatusCode().getCode())){
			String code = prot.getStatusCode().getCode();
			Vocabularies voc = VocabulariesImpl.instance();
			String message = "";

			if("nullified".equals(code) || "held".equals(code)){
				prot.setStatusCode(voc.getCodeValue("STATUS", "GENERIC", "new", "C"));
				message+=("La Comunicazione è ritornata allo stato: "+prot.getStatusCode().getCurrentTranslation());
			}else if("terminated".equals(code) || "active".equals(code)){
				prot.setStatusCode(voc.getCodeValue("STATUS", "GENERIC", "held", "C"));
				message+=("La Comunicazione è ritornata allo stato: "+prot.getStatusCode().getCurrentTranslation());
			}else if("obsolete".equals(code)){
				prot.setStatusCode(voc.getCodeValue("STATUS", "GENERIC", "terminated", "C"));
				message+=("La Comunicazione è ritornata allo stato: "+prot.getStatusCode().getCurrentTranslation());
			}else if("completed".equals(code)){
				if(prot.getIsMaster()){
					message+="Impossibile procedere trattandosi di una Comunicazione Master.";
				}else{
					prot.setStatusCode(voc.getCodeValue("STATUS", "GENERIC", "active", "C"));
					if(prot.getProcpratiche()!=null){
						List<Protocollo> protList = prot.getProcpratiche().getProtocollo();
						if(protList!=null)
							protList.remove(prot);
					}

					prot.setProcpratiche(null);
					message+=("La Comunicazione è ritornata allo stato: "+prot.getStatusCode().getCurrentTranslation());
				}
			}
			getTemporary().put("stateMessage", message);
		}
	}

	public boolean checkProtocollo(){
		try{
			boolean isValid = true;
			Protocollo prot = getEntity();

			/* I00089832 - Se l'assegnazione della pratica avviene in regime di auto-assegnazione (condizione parametrizzata sul ruolo) 
			 * devo verificare che all'utente loggato corrisponda un operatore Spisal da auto-assegnare alla pratica */

			//Controllo se siamo in regime di auto-assegnazione
			ParameterManager pm = ParameterManager.instance();
			Object value = pm.getParameter("p.home.gestione_pratica.autoassegnazione", "value");

			boolean autoassegnazione = (value!=null && value.toString()!=null && "true".equals(value.toString())?true:false);

			//Se siamo in regime di auto-assegnazione, devo controllare che all'utente loggato corrisponda un operatore Spisal 
			//nella stessa ulss in cui andrò a creare la pratica.
			if (autoassegnazione) {

				String GET_OP_ASSOCIATI = 	"SELECT op FROM Operatore op " +
						"LEFT JOIN op.employee emp " +
						"LEFT JOIN op.serviceDeliveryLocation sdl " +
						"WHERE op.isActive = 1 AND emp.internalId = :empInternalId and sdl.internalId =:sdlInternalId";

				Employee emp = UserBean.instance().getCurrentEmployee();
				ServiceDeliveryLocation ulss = prot.getServiceDeliveryLocation().getParent();

				HashMap<String, Object> parameters = new HashMap<String, Object>(2);
				List<Operatore> operatori = null;

				parameters.put("empInternalId", emp.getInternalId());
				parameters.put("sdlInternalId", ulss.getInternalId());

				operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_OP_ASSOCIATI, parameters);

				//Se non trovo un operatore Spisal non posso proseguire nella creazione della nuova pratica
				if (operatori==null || operatori.size()<1) {
					String error = "Nessun Operatore SIPRAL associato all'utente corrente (Username: " + ub.getUsername() + ") - Contattare un amministratore di sistema.";

					FacesErrorUtils.addErrorMessage("commError", error, error);
					isValid = false;
					//altrimenti injetto l'operatore che verrà successivamente associato (a livello di processo) alla pratica che sto creando
				} else {
					OperatoreAction opAction = OperatoreAction.instance();
					opAction.inject(operatori.get(0));
				}
			}
			/* ---------- I00089832 ---------- */

			if(prot!=null){
				if (prot.getCode()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipo comunicazione obbligatorio.", "Tipo comunicazione obbligatorio.");
					isValid = false;
				} 

				if (prot.getServiceDeliveryLocation()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Distretto obbligatorio.", "Distretto obbligatorio.");
					isValid = false;
				} 	

				if (prot.getUos()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Linea di lavoro obbligatoria.", "Linea di lavoro obbligatoria.");
					isValid = false;
				} 

				if (prot.getOggetto()==null || prot.getOggetto()=="") {
					FacesErrorUtils.addErrorMessage("commError", "Oggetto obbligatorio.", "Oggetto obbligatorio.");
					isValid = false;
				}

				if (prot.getData()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Data inserimento obbligatoria.", "Data inserimento obbligatoria.");
					isValid = false;
				}

				if (prot.getCode()!=null && "6".equals(prot.getCode().getCode()) && prot.getDataGiudizio()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Data giudizio medico competente obbligatoria.", "Data giudizio medico competente obbligatoria.");
					isValid = false;
				}

				if ("14.3".equals(prot.getCode().getCode()) && prot.getTipoExEsposto()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipo ex-esposto obbligatorio.", "Tipo ex-esposto obbligatorio.");
					isValid = false;
				}

				// I0062108
				isValid = checkForAsbestos(isValid, prot);
			}

			return isValid;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean checkForAsbestos(boolean isValid, Protocollo prot) {
		DettagliBonifiche currentDett = DettagliBonificheAction.instance().getEntity();
		if ("5".equals(prot.getCode().getCode()) && currentDett != null) {

			if(currentDett.getTipoSegnalazione() == null){
				FacesErrorUtils.addErrorMessage("commError", "Notifica o piano di lavoro obbligatorio.", "Notifica o piano di lavoro obbligatorio.");
				isValid = false;	
			}

			if(currentDett.getTipoMatrice() == null){
				FacesErrorUtils.addErrorMessage("commError", "Tipo matrice obbligatorio.", "Tipo matrice obbligatorio.");
				isValid = false;					
			}
		}
		if ("5".equals(prot.getCode().getCode()) && currentDett == null) {

			currentDett = prot.getDettagliBonifiche().get(0);

			if(currentDett.getTipoSegnalazione() == null){
				FacesErrorUtils.addErrorMessage("commError", "Notifica o piano di lavoro obbligatorio.", "Notifica o piano di lavoro obbligatorio.");
				isValid = false;	
			}

			if(currentDett.getTipoMatrice() == null){
				FacesErrorUtils.addErrorMessage("commError", "Tipo matrice obbligatorio.", "Tipo matrice obbligatorio.");
				isValid = false;					
			}
		}
		return isValid;
	}

	public boolean check(){
		try {
			boolean isValid = true;
			Protocollo prot = getEntity();

			if(prot!=null){

				if (prot.getServiceDeliveryLocation()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Distretto obbligatorio.", "Distretto obbligatorio.");
					isValid = false;
				} 	

				if (prot.getCode()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipo comunicazione obbligatorio.", "Tipo comunicazione obbligatorio.");
					isValid = false;
				}

				if (prot.getUos()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Linea di lavoro obbligatoria.", "Linea di lavoro obbligatoria.");
					isValid = false;
				} 
			}

			return isValid;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setDataScadenza(){  

		try{
			Protocollo prot = getEntity(); 

			if(prot!=null) {
				Date dataProtocollo = prot.getDataProtocollo();
				Date dataArrivo = prot.getDataASL();
				Date dataIns = prot.getData();

				Date scadenzaRef = dataIns;
				if (dataProtocollo!=null){
					scadenzaRef = dataProtocollo;
				}
				else if (dataArrivo != null){
					scadenzaRef = dataArrivo;
				}
				else if (dataIns != null){
					scadenzaRef = dataIns;
				}
				else {
					scadenzaRef = new Date();
				}

				int stcScore = 0;
				Integer score =0;
				if (prot.getCode()!=null) {
					String typeCode = prot.getCode().getCode();
					score = ((CodeValuePhi)prot.getCode()).getScore();
					ServiceDeliveryLocation sdl = prot.getServiceDeliveryLocation();
					if (sdl != null) {
						long sdlId = sdl.getParent().getInternalId();
						stcScore = getStcScore(sdlId, typeCode);
					}
				}

				//calcolo della data di scadenza a partire da quella di riferimento.
				Calendar cal = Calendar.getInstance();
				cal.setTime(scadenzaRef);

				//aggiusta la scadenza secondo quanto previsto da regione/struttura
				if (stcScore>0){
					cal.add(Calendar.DATE, stcScore); 
				} else if (score!=null && score>0) {
					cal.add(Calendar.DATE, score); 
				} 

				prot.setDataScadenza(cal.getTime());

				if (dataProtocollo!=null){
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(dataProtocollo);

					//Set Anno protocollo 
					prot.setAnnoProtocollo(new BigDecimal(cal1.get(Calendar.YEAR)));
				}

			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	private int getStcScore (long sdlId, String typeCode){


		Query qry = ca.createHibernateQuery(ScadenzeList);

		qry.setParameter("sdlId", sdlId);
		qry.setParameter("typeCode", typeCode);

		@SuppressWarnings("unchecked")
		List<ScadenzaTipoCom> stcList = (List<ScadenzaTipoCom>)qry.list();

		int stcScore = 0;

		if (stcList.size()>0){
			stcScore = stcList.get(0).getScore();
		}

		return stcScore;
	}

	public Date nDaysForward(Integer n){
		if (n==null || n<1){
			n = 10;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, n); 
		Date daysInFuture = cal.getTime();
		return daysInFuture;
	}

	public void filterExpired() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());

			//Otto giorni fa
			cal.add(Calendar.DATE, -8);
			Date  d = cal.getTime();

			Date filterDate = (Date)getTemporary().get("lessEqualInserimento");
			if (filterDate == null || filterDate.after(d)) {
				//Data di inserimento <= 8 giorni fa (escludendo le comunicazioni senza data inserimento)
				((FilterMap)getLessEqual()).put("data", d);
			}
			else {
				((FilterMap)getLessEqual()).put("data", filterDate);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}


	public void cleanFiltersMittente(){

		getLike().remove("richiedenteUtente.name.fam");
		getLike().remove("richiedenteUtente.name.giv");
		getLike().remove("richiedenteUtente.birthTime");
		getLike().remove("richiedenteUtente.fiscalCode");
		getLike().remove("richiedenteUtente.birthPlace.cty");

		getLike().remove("richiedenteMedico.name.fam");
		getLike().remove("richiedenteMedico.name.giv");
		getLike().remove("richiedenteMedico.regionalCode");

		getLike().remove("richiedenteInterno.name.fam");
		getLike().remove("richiedenteInterno.name.giv");
		getLike().remove("richiedenteInterno.code.code");

		getLike().remove("richiedenteDitta.patritaIva");
		getLike().remove("richiedenteDitta.codiceFiscale");
		getLike().remove("richiedenteDitta.denominazione");



	}

	public void cleanFiltersRiferito(){

		getLike().remove("riferimentoDitta.patritaIva");
		getLike().remove("riferimentoDitta.codiceFiscale");
		getLike().remove("riferimentoDitta.denominazione");

		getLike().remove("riferimentoUtente.name.fam");
		getLike().remove("riferimentoUtente.name.giv");
		getLike().remove("riferimentoUtente.birthTime");
		getLike().remove("riferimentoUtente.fiscalCode");
		getLike().remove("richiedenteUtente.birthPlace.cty");

		getLike().remove("riferimentoCantiere.naturaOpera");
		getLike().remove("riferimentoCantiere.addr.str");

		getLike().remove("riferimentoDenominazione");
		getLike().remove("riferimentoUbicazione");

	}

	public void cleanFiltersUbicazione(){

		getLike().remove("ubicazioneDitta.patritaIva");
		getLike().remove("ubicazioneDitta.codiceFiscale");
		getLike().remove("ubicazioneDitta.denominazione");

		getLike().remove("ubicazioneCantiere.naturaOpera");
		getLike().remove("ubicazioneCantiere.addr.str");

		getLike().remove("ubicazioneAddr.str");
		getLike().remove("ubicazioneX");
		getLike().remove("ubicazioneY");

	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getAllowedAreas(){
		List<SelectItem> areas = new ArrayList<SelectItem>();
		UserBean ub = UserBean.instance();

		if(ub.getSdLocs()!=null){
			List<CodeValuePhi> cvArea = ((javax.persistence.Query)ca.createQuery("SELECT DISTINCT area FROM ServiceDeliveryLocation s " +
					"JOIN s.area area JOIN s.code code " +
					"WHERE code.code = 'UOS' AND s.internalId in (:ids)"))
					.setParameter("ids", ub.getSdLocs())
					.getResultList();

			if(cvArea!=null){
				for(CodeValuePhi area : cvArea){
					areas.add(new SelectItem(area, area.getCurrentTranslation()));
				}
			}
		}
		return areas;
	}


	@SuppressWarnings("unchecked")
	public void setAllowedWorkingLines(){
		List<String> allowedCodes = new ArrayList<String>(); 
		allowedCodes.add("phidic.spisal.segnalazioni.complextype.17.2_V0");//altro è onnipresente e non mappato
		List<String> allowedDomains = new ArrayList<String>(); 
		allowedDomains.add("phidic.spisal.segnalazioni.complextype_V0");//root
		allowedDomains.add("phidic.spisal.segnalazioni.complextype.17_V0");//Altre richieste

		UserBean ub = UserBean.instance();
		Long distId = null;
		if(entity!=null && entity.getServiceDeliveryLocation()!=null){
			distId = entity.getServiceDeliveryLocation().getInternalId();
		}

		if(ub.getSdLocs()!=null){

			List<CodeValuePhi> wls = ((javax.persistence.Query)ca.createQuery("SELECT DISTINCT wl FROM ServiceDeliveryLocation s " +
					"JOIN s.area area JOIN s.code code JOIN area.inverseRelationsPhi wl " +
					"WHERE code.code = 'UOS' AND s.internalId in (:ids) AND (:distretto IS NULL OR s.parent.internalId = :distretto)"))
					.setParameter("ids", ub.getSdLocs())
					.setParameter("distretto", distId)
					.getResultList();

			if(wls!=null){
				for(CodeValuePhi wl : wls){
					if(!allowedCodes.contains(wl.getId())){
						allowedCodes.add(wl.getId());

						if(wl.getParent()!=null && !allowedCodes.contains(wl.getParent().getId())){
							allowedDomains.add(wl.getParent().getId());
						}
					}
				}
			}
		}

		CodeValueAction.instance().getTemporary().put("allowedDomains", allowedDomains);
		CodeValueAction.instance().getTemporary().put("allowedCodes", allowedCodes);
	}

	public void filterPnc(){
		Boolean fromPnc = (Boolean)getTemporary().get("fromPnc");

		removeExpression("this", "not RiferimentoCantiere.idPnc");

		if(fromPnc==null || Boolean.FALSE.equals(fromPnc))
			return;

		if(findSubCriteria("RiferimentoCantiere")==null)
			entityCriteria.createAlias("riferimentoCantiere", "RiferimentoCantiere", Criteria.INNER_JOIN);

		entityCriteria.add(Restrictions.not(Restrictions.eq("RiferimentoCantiere.idPnc", "")));
		entityCriteria.add(Restrictions.not(Restrictions.isNull("RiferimentoCantiere.idPnc")));

	}

	public Long countResults(){
		List<Integer> multiresult = entityCriteria.setFirstResult(0).setProjection(Projections.rowCount()).list();
		Long total = 0L;
		if(multiresult!=null){
			for(Integer n : multiresult){
				if(n!=null){
					total += n;
				}
			}
		}

		entityCriteria.setProjection(entityProjections);
		if(resultTranformer!=null){
			entityCriteria.setResultTransformer(resultTranformer);
		}else{
			entityCriteria.setResultTransformer(entityCriteria.ROOT_ENTITY);
		}
		return total;
	}

	public void generateCoordinateExportFile(PagedDataModel protocolloList){
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) protocolloList.getFullList();

		ImpiantiDocument exportCsv = new ImpiantiDocument();
		StringBuffer sb = new StringBuffer();

		FunctionsBean fb = FunctionsBean.instance();

		for(HashMap<String, Object> map : list){

			HashMap<String, Object> riferimentoCantiere = (HashMap<String, Object>) map.get("riferimentoCantiere");
			String lat = (String) riferimentoCantiere.get("latitudine");
			String lng = (String) riferimentoCantiere.get("longitudine");
			String ubicazioneX = (String) map.get("ubicazioneX");
			String ubicazioneY = (String) map.get("ubicazioneY");
			
			if (riferimentoCantiere != null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()){
				if(map.get("nprotocollo") != null)
					sb.append(map.get("nprotocollo"));

				if(map.get("data") != null)
					sb.append("/").append(fb.formatDate(map.get("data"), "yyyy"));

				sb.append(";").append(lat).append(";").append(lng).append(";");
				sb.append("\r\n");
				
			} else if(ubicazioneX!=null && !ubicazioneX.isEmpty() && ubicazioneY!=null && !ubicazioneY.isEmpty()) {
				if(map.get("nprotocollo") != null)
					sb.append(map.get("nprotocollo"));

				if(map.get("data") != null)
					sb.append("/").append(fb.formatDate(map.get("data"), "yyyy"));

				sb.append(";").append(ubicazioneX).append(";").append(ubicazioneY).append(";");
				sb.append("\r\n");
			}
		}

		exportCsv.setFilename("COMUNICAZIONI_" + fb.formatDate(new Date(), "yyyyMMdd") + ".csv");
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb.toString().getBytes());

		ImpiantiDocumentAction ida = ImpiantiDocumentAction.instance();
		ida.inject(exportCsv);
	}
}