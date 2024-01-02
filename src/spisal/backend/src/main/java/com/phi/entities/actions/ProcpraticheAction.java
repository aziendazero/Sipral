package com.phi.entities.actions;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.paging.LazyList;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.BenessereOrg;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.ConclusioniMdl;
import com.phi.entities.baseEntity.DiagMdl;
import com.phi.entities.baseEntity.Disposizioni;
import com.phi.entities.baseEntity.DitteMalattie;
import com.phi.entities.baseEntity.Gruppi;
import com.phi.entities.baseEntity.ImpiantiDocument;
import com.phi.entities.baseEntity.Infortuni;
import com.phi.entities.baseEntity.InfortuniExt;
import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.baseEntity.MedicinaLavoro;
import com.phi.entities.baseEntity.Miglioramenti;
import com.phi.entities.baseEntity.Observer;
import com.phi.entities.baseEntity.ParereTecnico;
import com.phi.entities.baseEntity.PersonaGiuridicaSede;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.PraticheRiferimenti;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.ScadenzaTipoCom;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.Sospensione;
import com.phi.entities.baseEntity.TagFascicolo;
import com.phi.entities.baseEntity.ValutazioneConclusivaMdl;
import com.phi.entities.baseEntity.Vigilanza;
import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.II4ServiceDeliveryLocation;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parameters.ParameterManager;
import com.phi.security.SpisalUserAction;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("ProcpraticheAction")
@Scope(ScopeType.CONVERSATION)
public class ProcpraticheAction extends BaseAction<Procpratiche, Long> {

	private static final long serialVersionUID = -8236609598505145155L;

	public static ProcpraticheAction instance() {
		return (ProcpraticheAction) Component.getInstance(ProcpraticheAction.class, ScopeType.CONVERSATION);
	}

	private static final Logger log = Logger.getLogger(ProcpraticheAction.class);

	private static String ScadenzeList = "SELECT stc FROM ScadenzaTipoCom stc " +
			"WHERE stc.ulss.internalId = :sdlId AND stc.code.code = :typeCode order by stc.creationDate desc";

	/**
	 * NB: per ottimizzare ulteriormente questo metodo si dovrebbe agganciare la comunicazione MASTER alla pratica usando
	 * 		un link DEDICATO, senza dover cosi cercare tra tutte le comunicazioni associate
	 * @param pratObj
	 * @return
	 */
	public String getUtenteDitta(Object pratObj) {
		String ret = "";
		try {

			if(pratObj instanceof Procpratiche){
				Procpratiche prat = (Procpratiche)pratObj;

				List<Protocollo> protocolloList = prat.getProtocollo();

				if (protocolloList != null && protocolloList.size() > 0 ) { 
					for (Protocollo protocollo : protocolloList) {
						if (protocollo.getIsMaster()){
							String code = null;
							CodeValue riferimento = protocollo.getRiferimento();

							if (riferimento != null){
								code = riferimento.getCode();

								if (code!=null && code!=""){
									if (code.equals("Utente") && protocollo.getRiferimentoUtente()!=null && protocollo.getRiferimentoUtente().getName()!=null){
										ret = "U: " + protocollo.getRiferimentoUtente().getName().getFam() + " " + protocollo.getRiferimentoUtente().getName().getGiv();
										Date birthTime = protocollo.getRiferimentoUtente().getBirthTime();
										if (birthTime!=null){
											SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
											ret += " (" + DATE_FORMAT.format(birthTime) + ")";
										}
									}

									else if (code.equals("Ditta") && protocollo.getRiferimentoDitta()!=null)
										ret = "D: " + protocollo.getRiferimentoDitta().getDenominazione();

									else if (code.equals("Interno") && protocollo.getRiferimentoInterno()!=null && protocollo.getRiferimentoInterno().getName()!=null)
										ret = "I: " + protocollo.getRiferimentoInterno().getName().getFam() + " " + protocollo.getRiferimentoInterno().getName().getGiv();

									else if (code.equals("Cantiere") && protocollo.getRiferimentoCantiere()!=null)
										ret = "C: " + protocollo.getRiferimentoCantiere().getAddr();
								}
							}
							break;
						}
					}
				}
			}else if(pratObj instanceof Map){	//read with select...
				FunctionsBean fun = FunctionsBean.instance();
				Long pratId = (Long)fun.resolveMapProperty((Map)pratObj, "internalId");
				String hql = "SELECT refCode.code, ut.name.giv, ut.name.fam, ditta.denominazione, inter.name.giv, inter.name.fam, cant.naturaOpera, cant.addr, ut.birthTime FROM Protocollo prot " +
						"JOIN prot.procpratiche prat " +
						"JOIN prot.riferimento refCode " +
						"LEFT JOIN prot.riferimentoUtente ut " +
						"LEFT JOIN prot.riferimentoDitta ditta " +
						"LEFT JOIN prot.riferimentoInterno inter " +
						"LEFT JOIN prot.riferimentoCantiere cant " +
						"WHERE prat.internalId = :pratId AND prot.isMaster = 1";
				Query q = ca.createQuery(hql);
				q.setParameter("pratId", pratId);
				List<Object[]> result = (List<Object[]>)q.getResultList();
				if(result!=null && !result.isEmpty()){
					Object[] res = result.get(0);

					if("Utente".equals(res[0]) && res[1]!=null && res[2]!=null){
						ret = "U: " + (String)res[2] + " " + (String)res[1];
						if (res[8]!=null){
							SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
							ret += " (" + DATE_FORMAT.format(res[8]) + ")";

						}
					}else if("Ditta".equals(res[0]) && res[3]!=null){
						ret = "D: " + (String)res[3];
					}else if("Interno".equals(res[0]) && res[4]!=null && res[5]!=null){
						ret = "I: " + (String)res[5] + " " + (String)res[4];
					}else if("Cantiere".equals(res[0]) && res[6]!=null && res[7]!=null){
						ret = "C: " + (String)res[6]+ res[7].toString();
					}					
				}

			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		return ret.toUpperCase();
	}

	public String getRoles(Operatore op, Object pratObj) {
		String ret = "";

		try {
			if(pratObj instanceof Procpratiche){
				Employee employee = null;
				Procpratiche prat = (Procpratiche)pratObj;

				UserBean ub = (UserBean) Component.getInstance("userBean");

				ParameterManager pm = ParameterManager.instance();
				String value = "";

				if (pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value")!=null)
					value = pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value").toString();

				Boolean soloPraticheAssegnate = (value!=null && value.equals("true"))?true:false;

				if (soloPraticheAssegnate)
					employee = ub.getCurrentEmployee();

				else if (op != null)
					employee = op.getEmployee();

				List<Operatore> operatori = prat.getOperatori();

				if (operatori != null && operatori.size() > 0 ){
					for (Operatore operatore : operatori) {
						if(operatore == null)
							continue;
						Employee emp = operatore.getEmployee();
						if (emp != null && emp.equals(employee)){
							ret += "OP";	
							break;
						}
					}
				}

				if (ret.equals(""))
					return ret;

				if (employee!=null) {
					if (prat.getRdp() != null && prat.getRdp().equals(employee))
						ret = "RDP";

					if (prat.getRdi() != null && prat.getRdi().equals(employee))
						if (ret.startsWith("O"))
							ret = "RDI";
						else ret += "/RDI";

					if (prat.getRfp() != null && prat.getRfp().equals(employee))
						if (ret.startsWith("O"))
							ret = "RFP";
						else ret += "/RFP";
				}
			} else if(pratObj instanceof Map){	//read with select...
				FunctionsBean fun = FunctionsBean.instance();
				Employee employee = null;
				Long pratId = (Long)fun.resolveMapProperty((Map)pratObj, "internalId");

				UserBean ub = (UserBean) Component.getInstance("userBean");

				ParameterManager pm = ParameterManager.instance();
				String value = "";

				if (pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value")!=null)
					value = pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value").toString();

				Boolean soloPraticheAssegnate = (value!=null && value.equals("true"))?true:false;

				//if (role.equalsIgnoreCase("utente"))
				if (soloPraticheAssegnate)
					employee = ub.getCurrentEmployee();

				else if (op != null)
					employee = op.getEmployee();

				if (employee!=null) {
					String hql1 = "SELECT COUNT(p) FROM Procpratiche p " +
							"JOIN p.operatori o " +
							"JOIN o.employee e " +
							"WHERE e.internalId = :empId AND p.internalId = :pratId";

					Query q1 = ca.createQuery(hql1);
					q1.setParameter("empId", employee.getInternalId());
					q1.setParameter("pratId", pratId);
					Long count = (Long)q1.getSingleResult();
					if(count!=null && count>0){
						ret += "OP";
					}

					if (ret.equals(""))
						return ret;

					String hql2 = "SELECT rdp.internalId, rdi.internalId, rfp.internalId FROM Procpratiche p " +
							"LEFT JOIN p.rdp rdp " +
							"LEFT JOIN p.rdi rdi " +
							"LEFT JOIN p.rfp rfp " +
							"WHERE (rdp.internalId = :empId OR rdi.internalId = :empId OR rfp.internalId = :empId) AND p.internalId = :pratId";

					Query q2 = ca.createQuery(hql2);
					q2.setParameter("empId", employee.getInternalId());
					q2.setParameter("pratId", pratId);
					List<Object[]> result = (List<Object[]>)q2.getResultList(); 
					if(result!=null && !result.isEmpty()){
						Object[] r = result.get(0);
						if (r[0]!=null)
							ret = "RDP";

						if (r[1]!=null)
							if (ret.startsWith("O"))
								ret = "RDI";
							else ret += "/RDI";

						if (r[2]!=null)
							if (ret.startsWith("O"))
								ret = "RFP";
							else ret += "/RFP";
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

		return ret;

	}

	public boolean isCurrentUserOperatore(Procpratiche prat) {
		try {

			if (prat == null)
				return false;

			UserBean ub = (UserBean) Component.getInstance("userBean");
			Employee employee = ub.getCurrentEmployee();

			List<Operatore> operatori = prat.getOperatori();

			if (operatori != null && operatori.size() > 0) {
				for (Operatore operatore : operatori) {
					if (operatore != null && operatore.getIsActive()) {
						Employee emp = operatore.getEmployee();
						if (emp != null && emp.equals(employee))
							return true;
					}
				}
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void filterByOperatore() {
		try {

			Operatore op = (Operatore)getTemporary().get("searchByOp");

			UserBean ub = (UserBean) Component.getInstance("userBean");

			ParameterManager pm = ParameterManager.instance();

			String value = "";

			if (pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value")!=null)
				value = pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value").toString();

			Boolean soloPraticheAssegnate = (value!=null && value.equals("true"))?true:false;

			/* p.home.procpratiche.gestionePraticheAssegnate -> false (default)
			 * p.home.procpratiche.gestionePraticheAssegnate -> true  (per specifici ruoli)
			 * 
			 * Se l'utente si è loggato con un ruolo specifico (gestionePraticheAssegnate -> true) potrà vedere in lettura/scrittura
			 * solo le pratiche alle quali è stato assegnato. Potrà vedere in sola lettura anche le pratiche alle quali non risulta assegnato
			 * solo se è true il temporary showAll
			 * 
			 * Se l'utente si è loggato con un ruolo non specifico (gestionePraticheAssegnate -> false) potrà vedere in lettura/scrittura
			 * tutte le pratiche */			
			if (soloPraticheAssegnate) {
				Boolean showAll = (Boolean)getTemporary().get("showAll");

				if (showAll == null || !showAll)
					((FilterMap)getEqual()).put("operatori.employee", ub.getCurrentEmployee());
				else 
					((FilterMap)getEqual()).remove("operatori.employee");
			}

			//Se è stato introdotto un filtro specifico per Operatore
			else if (op != null)
				((FilterMap)getEqual()).put("operatori.internalId", op.getInternalId());

			else 
				((FilterMap)getEqual()).remove("operatori.internalId");

			/*
			Object ric = getTemporary().get("richiedente");
			Object rif = getTemporary().get("riferimento");
			Object ubi = getTemporary().get("ubicazione");

			if (ric != null) {
				String richiedente = ((CodeValue)ric).getCode();

				if (richiedente != null) 
					((FilterMap)getEqual()).put("praticheRiferimenti.richiedente.code", richiedente);
			} else 
				((FilterMap)getEqual()).remove("praticheRiferimenti.richiedente.code");

			if (rif != null) {
				String riferimento = ((CodeValue)rif).getCode();

				if (riferimento != null) 
					((FilterMap)getEqual()).put("praticheRiferimenti.riferimento.code", riferimento);
			} else 
				((FilterMap)getEqual()).remove("praticheRiferimenti.riferimento.code");

			if (ubi != null) {
				String ubicazione = ((CodeValue)ubi).getCode();

				if (ubicazione != null) 
					((FilterMap)getEqual()).put("praticheRiferimenti.ubicazione.code", ubicazione);
			} else 
				((FilterMap)getEqual()).remove("praticheRiferimenti.ubicazione.code");

			 */
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}


	//	public void filterBy(Operatore op) {
	//		try {
	//
	//			UserBean ub = (UserBean) Component.getInstance("userBean");
	//			
	//			ParameterManager pm = ParameterManager.instance();
	//			
	//			String value = "";
	//			
	//			if (pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value")!=null)
	//				value = pm.getParameter("p.home.procpratiche.gestionePraticheAssegnate", "value").toString();
	//			
	//			Boolean soloPraticheAssegnate = (value!=null && value.equals("true"))?true:false;
	//			
	//			/* p.home.procpratiche.gestionePraticheAssegnate -> false (default)
	//			 * p.home.procpratiche.gestionePraticheAssegnate -> true  (per specifici ruoli)
	//			 * 
	//			 * Se l'utente si è loggato con un ruolo specifico (gestionePraticheAssegnate -> true) potrà vedere in lettura/scrittura
	//			 * solo le pratiche alle quali è stato assegnato. Potrà vedere in sola lettura anche le pratiche alle quali non risulta assegnato
	//			 * solo se è true il temporary showAll
	//			 * 
	//			 * Se l'utente si è loggato con un ruolo non specifico (gestionePraticheAssegnate -> false) potrà vedere in lettura/scrittura
	//			 * tutte le pratiche */			
	//			if (soloPraticheAssegnate) {
	//				Boolean showAll = (Boolean)getTemporary().get("showAll");
	//
	//				if (showAll == null || !showAll)
	//					((FilterMap)getEqual()).put("operatori.employee", ub.getCurrentEmployee());
	//				else 
	//					((FilterMap)getEqual()).remove("operatori.employee");
	//			}
	//
	//			//Se è stato introdotto un filtro specifico per Operatore
	//			else if (op != null)
	//				((FilterMap)getEqual()).put("operatori.internalId", op.getInternalId());
	//			
	//			else 
	//				((FilterMap)getEqual()).remove("operatori.internalId");
	//
	//			Object ric = getTemporary().get("richiedente");
	//			Object rif = getTemporary().get("riferimento");
	//			Object ubi = getTemporary().get("ubicazione");
	//			
	//			if (ric != null) {
	//				String richiedente = ((CodeValue)ric).getCode();
	//				
	//				if (richiedente != null) 
	//					((FilterMap)getEqual()).put("praticheRiferimenti.richiedente.code", richiedente);
	//			} else 
	//				((FilterMap)getEqual()).remove("praticheRiferimenti.richiedente.code");
	//			
	//			if (rif != null) {
	//				String riferimento = ((CodeValue)rif).getCode();
	//				
	//				if (riferimento != null) 
	//					((FilterMap)getEqual()).put("praticheRiferimenti.riferimento.code", riferimento);
	//			} else 
	//				((FilterMap)getEqual()).remove("praticheRiferimenti.riferimento.code");
	//			
	//			if (ubi != null) {
	//				String ubicazione = ((CodeValue)ubi).getCode();
	//				
	//				if (ubicazione != null) 
	//					((FilterMap)getEqual()).put("praticheRiferimenti.ubicazione.code", ubicazione);
	//			} else 
	//				((FilterMap)getEqual()).remove("praticheRiferimenti.ubicazione.code");
	//			
	//
	//		} catch (Exception ex) {
	//			log.error(ex);
	//			throw new RuntimeException(ex);
	//		} 
	//
	//	}

	/*public void filterBy(Operatore op, Person p, PersoneGiuridiche pg, Employee emp, Cantiere c) {
		try {

			UserBean ub = (UserBean) Component.getInstance("userBean");
			String role = ub.getRole();

			//L'utente loggato è un operatore (role: utente)
			if (role.equalsIgnoreCase("utente")) {
				Boolean showAll = (Boolean)getTemporary().get("showAll");

				if (showAll == null || !showAll)
					((FilterMap)getEqual()).put("operatori.employee", ub.getCurrentEmployee());
				else 
					((FilterMap)getEqual()).remove("operatori.employee");
			}

			//Operatore selezionato
			else if (op != null)
				((FilterMap)getEqual()).put("operatori.internalId", op.getInternalId());

			else ((FilterMap)getEqual()).remove("operatori.internalId");

			Object rif = getTemporary().get("riferimento");

			if (rif != null) {
				String riferimento = ((CodeValue)rif).getCode();

				if ((riferimento != null) && ("Altro Anonimo".contains(riferimento))) {
					if (riferimento.equals("Altro"))
						((FilterMap)getEqual()).put("protocollo.riferimento.code", "Altro");

					else if (riferimento.equals("Anonimo"))
						((FilterMap)getEqual()).put("protocollo.richiedente.code", "Anonimo");

					((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.codiceFiscale");
					((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.mpi");
					((FilterMap)getEqual()).remove("protocollo.riferimentoInterno.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.id");				
					PersonAction pa = new PersonAction();
					pa.eject();

					PersoneGiuridicheAction.instance().eject();

					EmployeeAction.instance().eject();


					CantiereAction.instance().eject();

					return;
				}

				if (p != null || pg != null || emp != null || c != null) {
					if (p != null) {

						//Riferito a - Utente
						((FilterMap)getEqual()).put("protocollo.riferimento.code", "Utente");

						// Se l'xmpi è valorizzato, usalo come criterio di matching
						// Altrimenti usa l'internalId 
						String mpi = p.getMpi();
						if (mpi != null && !"".equals(mpi))
							((FilterMap)getEqual()).put("protocollo.riferimentoUtente.mpi", mpi);
						else 
							((FilterMap)getEqual()).put("protocollo.riferimentoUtente.internalId", p.getInternalId());

						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.codiceFiscale");
						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoInterno");
						((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.id");
						((FilterMap)getEqual()).remove("protocollo.riferimentoEntita");

					} else if (pg != null){


						Boolean showBoth = (Boolean)getTemporary().get("showBoth");
						if(showBoth==null) showBoth=false;

						if(!showBoth){
							//Riferito a - Ditta/Ente
							((FilterMap)getEqual()).put("praticheRiferimenti.riferimento.code", "Ditta");
							((FilterMap)getEqual()).put("praticheRiferimenti.riferimentoDitta.internalId", pg.getInternalId());

							((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.mpi");
							((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.internalId");
							((FilterMap)getEqual()).remove("protocollo.riferimentoInterno");
							((FilterMap)getEqual()).remove("protocollo.riferimentoEntita");
							((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.internalId");
							((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.id");

						}else{
							//FIXME: Chiedere a Giacomo
							Criteria protocolloCriteria = findSubCriteria("Protocollo");

							protocolloCriteria = findSubCriteria("Protocollo");	

							if(protocolloCriteria==null){
								protocolloCriteria = this.entityCriteria.createCriteria("protocollo","Protocollo");	
							}

							Criterion criterionRicRif = Restrictions.or(Restrictions.eq("richiedenteDitta", pg), Restrictions.eq("riferimentoDitta", pg));
							protocolloCriteria.add(criterionRicRif);	


						}
					} else if (emp != null){

						//Riferito a - Interno
						((FilterMap)getEqual()).put("protocollo.riferimento.code", "Interno");
						((FilterMap)getEqual()).put("protocollo.riferimentoInterno", emp);

						((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.mpi");
						((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.codiceFiscale");
						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.id");
						((FilterMap)getEqual()).remove("protocollo.riferimentoEntita");


					} else if (c != null){

						//Riferito a - Cantiere
						((FilterMap)getEqual()).put("protocollo.riferimento.code", "Cantiere");

						String id = c.getId();
						if (id != null && !"".equals(id))
							((FilterMap)getEqual()).put("protocollo.riferimentoCantiere.id", id);
						else 
							((FilterMap)getEqual()).put("protocollo.riferimentoCantiere.internalId", c.getInternalId());


						((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.mpi");
						((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoInterno");
						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.codiceFiscale");
						((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.internalId");
						((FilterMap)getEqual()).remove("protocollo.riferimentoEntita");
					}
				} else {
					((FilterMap)getEqual()).remove("protocollo.riferimento.code");
					((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.mpi");
					((FilterMap)getEqual()).remove("protocollo.riferimentoUtente.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.codiceFiscale");
					((FilterMap)getEqual()).remove("protocollo.riferimentoDitta.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.internalId");
					((FilterMap)getEqual()).remove("protocollo.riferimentoCantiere.id");
					((FilterMap)getEqual()).remove("protocollo.riferimentoInterno");
					((FilterMap)getEqual()).remove("protocollo.riferimentoEntita");


				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}*/

	/*public void filterByPerson(Person p) {
		try {

			//Riferito a - Utente
			if (p != null) {
				((FilterMap)getEqual()).put("protocollo.riferimento.code", "Utente");
				((FilterMap)getEqual()).put("protocollo.riferimentoUtente.internalId", p.getInternalId());		
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}

	}*/

	public void setAssignments(LazyList list){
		try{
			Procpratiche procpratiche = getEntity();
			if (list != null && list.size() > 0 ) { 

				for (Map operatore : (List<Map>)list) {
					if (operatore.get("isNew")!=null && (Boolean)operatore.get("isNew")){
						if (procpratiche.getOperatori() == null) {
							procpratiche.setOperatori(new ArrayList<Operatore>());
							procpratiche.setUpg(new ArrayList<Operatore>());
						} else if (procpratiche.getOperatori().size()==0)
							procpratiche.setUpg(new ArrayList<Operatore>());

						if (procpratiche.getUpg() == null)
							procpratiche.setUpg(new ArrayList<Operatore>());

						Operatore op = (Operatore)ca.get(Operatore.class, (Long)operatore.get("internalId"));
						if (!procpratiche.getOperatori().contains(op)){
							procpratiche.getOperatori().add(op);

							if (procpratiche.getDataAssegnazione()==null)
								procpratiche.setDataAssegnazione(new Date());
						}

						if (!procpratiche.getUpg().contains(op)){
							boolean upg = false;
							Employee emp = op.getEmployee();
							if (emp != null)
								if (emp.getUpg()!=null)
									upg = emp.getUpg();

							if (upg)
								procpratiche.getUpg().add(op);
						}

						operatore.put("IsNew", false);
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public boolean isObservedByCurrentEmployee(Object pratObj) {
		try {
			if (pratObj == null)
				return false;

			UserBean ub = (UserBean) Component.getInstance("userBean");
			Employee employee = ub.getCurrentEmployee();

			if(pratObj instanceof Procpratiche) {
				Procpratiche prat = (Procpratiche)pratObj;



				List<Observer> osservatori = prat.getObservedBy();

				if (osservatori != null && osservatori.size() > 0) {
					for (Observer osservatore : osservatori) {
						if (osservatore != null && osservatore.getIsActive()) {
							Employee emp = osservatore.getEmployee();
							if (emp != null && emp.equals(employee))
								return true;
						}
					}
				}

				return false;

			} else if(pratObj instanceof Map){

				FunctionsBean fun = FunctionsBean.instance();
				Long pratId = (Long)fun.resolveMapProperty((Map)pratObj, "internalId");

				String hql = "SELECT obs.internalId FROM Observer obs " +
						"LEFT JOIN obs.employee emp " +
						"LEFT JOIN obs.procpratiche prat " +
						"WHERE obs.isActive = 1 AND prat.internalId = :pratId AND emp.internalId = :empId ";

				Query q = ca.createQuery(hql);
				q.setParameter("pratId", pratId);
				q.setParameter("empId", employee.getInternalId());

				List<Object[]> result = (List<Object[]>)q.getResultList();

				if(result!=null && !result.isEmpty())
					return true;
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void removeOperatore(Operatore operatore){
		try{
			Procpratiche procpratiche = getEntity();
			if ((procpratiche.getOperatori()!=null)&&(procpratiche.getOperatori().contains(operatore)))
				procpratiche.getOperatori().remove(operatore);
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setStates() {
		try {
			List<String> status = new ArrayList<String>();

			// Add three elements.
			status.add("status.generic.new_V0");
			//status.add("status.generic.active_V0");

			HashMap<String, Object> temp = this.getTemporary();
			Object allstatesTmp = temp.get("allstates");
			Boolean allstates = (allstatesTmp!=null && (Boolean)allstatesTmp);


			if (!temp.isEmpty()){
				Object completed = temp.get("completed");
				if ((completed!=null && (Boolean)completed) || allstates)
					status.add("status.generic.completed_V0");
				//Altrimenti elimina eventuali filtri "Data chiusura Dal" - "Al"
				else {
					((FilterMap)getGreaterEqual()).remove("completedDate");
					((FilterMap)getLessEqual()).remove("completedDate");
				}

				Object suspended = temp.get("suspended");
				if ((suspended!=null && (Boolean)suspended) || allstates)
					status.add("status.generic.suspended_V0");

				Object nullified = temp.get("nullified");
				if ((nullified!=null && (Boolean)nullified) || allstates)
					status.add("status.generic.nullified_V0");

				Object held = temp.get("held");
				if ((held!=null && (Boolean)held) || allstates)
					status.add("status.generic.held_V0");

				Object active = temp.get("active");
				if ((active!=null && (Boolean)active) || allstates)
					status.add("status.generic.active_V0");

				Object verified = temp.get("verified");
				if ((verified!=null && (Boolean)verified) || allstates)
					status.add("status.generic.verified_V0");		
			}

			((FilterMap)getEqual()).putOr("statusCode.id", status.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	/* I0063194 (Punto 7) - Se l'utente è contemporaneamente su distretti attivi e non attivi, 
	 * limita la ricerca alle sole pratiche legate a distretti attivi */
	public void checkSign4Uoc() {
		try {
			getEqual().remove("uoc.internalId");

			HashMap<String, Object> temp = this.getTemporary();
			Object showGeneratedTmp = temp.get("showGenerated");
			Boolean showGenerated = (showGeneratedTmp!=null)?(Boolean)showGeneratedTmp:false;

			Object showPartiallySignedTmp = temp.get("showPartiallySigned");
			Boolean showPartiallySigned = (showPartiallySignedTmp!=null)?(Boolean)showPartiallySignedTmp:false;

			//Se l'utente non ha impostato filtri sui documenti, non fa nulla
			if (!showGenerated && !showPartiallySigned)
				return;

			boolean active = false;
			boolean inactive =false;

			SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
			List<ServiceDeliveryLocation> uocList = sua.getDistretti();

			if (uocList==null || uocList.size()<1)
				return;

			List<Long> uocIds = new ArrayList<Long>();
			AlfrescoDocumentAction ada = (AlfrescoDocumentAction) Component.getInstance("AlfrescoDocumentAction");
			for (ServiceDeliveryLocation sdl:uocList){
				if (ada.showSign(sdl)){
					active = true;
					uocIds.add(sdl.getInternalId());
				} else 
					inactive = true;
			}

			//Se l'utente è loggato contemporaneamente su distretti attivi e non attivi
			if (active && inactive)
				//limita la ricerca alle sole pratiche legate a distretti attivi
				((FilterMap)getEqual()).putOr("uoc.internalId", uocIds.toArray());

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void resetStates() {

		Object allstatesTmp = this.getTemporary().get("allstates");
		Boolean allstates = (allstatesTmp!=null && (Boolean)allstatesTmp);
		if(allstates){
			this.getTemporary().remove("active");
			this.getTemporary().remove("verified");
			this.getTemporary().remove("completed");
			this.getTemporary().remove("suspended");
			this.getTemporary().remove("nullified");
			this.getTemporary().remove("held");
		}

	}

	public void resetStatesForRovvedimenti() {
		this.getTemporary().remove("allstates");

		this.getTemporary().put("active", true);
		this.getTemporary().put("verified", true);
		this.getTemporary().put("suspended", true);
		this.getTemporary().put("completed", true);

		this.getTemporary().remove("nullified");
		this.getTemporary().remove("held");
	}

	public void setDocumentiStates(){
		try {
			HashMap<String, Object> temp = this.getTemporary();
			Object showNotSignedTmp = temp.get("showNotSigned");
			Boolean showNotSigned = (showNotSignedTmp!=null && (Boolean)showNotSignedTmp);
			Object showPartiallySignedTmp = temp.get("showPartiallySigned");
			Boolean showPartiallySigned = (showPartiallySignedTmp!=null && (Boolean)showPartiallySignedTmp);
			removeExpression("this", "org.hibernate.criterion.ExistsSubqueryExpression");
			if(showNotSigned || showPartiallySigned){
				DetachedCriteria criteria1 = DetachedCriteria.forClass(AlfrescoDocument.class,"AlfrescoDocument");
				criteria1.add(Restrictions.sqlRestriction("AlfrescoDocument_.Procpratiche_id = this_.internal_id"));
				criteria1.add(Restrictions.sqlRestriction("AlfrescoDocument_.is_active=true"));

				/* I0063194 (Punto 8) -  Utente di sistema loggato su un DISTRETTO ATTIVO, avviato con la firma digitale in data successiva all'impostazione del numero di firme 
				 * necessarie sui template
				 * 
				 * La funzionalità estrarrà solo le pratiche che contengono documenti in stato 'non firmato' o 'parzialmente firmato' creati a partire dalla data di attivazione del parametro 
				 * */
				Date validFrom = null;
				SpisalUserAction sua = (SpisalUserAction) Component.getInstance("spisalUserAction");
				List<ServiceDeliveryLocation> uocList = sua.getDistretti();
				AlfrescoDocumentAction ada = (AlfrescoDocumentAction) Component.getInstance("AlfrescoDocumentAction");

				for (ServiceDeliveryLocation sdl:uocList){
					Date date = ada.getSignValidFrom(sdl);
					if (date!=null)
						validFrom = date;
				}

				if (validFrom!=null)
					criteria1.add(Restrictions.sqlRestriction("AlfrescoDocument_.creation_date >= '" + validFrom + "'"));
				/* ---- I0063194 (Punto 8) ---- */

				if(showNotSigned && !showPartiallySigned){
					criteria1.add(Restrictions.sqlRestriction("AlfrescoDocument_.documentStatus = 'phidic.spisal.documentstatus.generato_V0'"));
				}
				if(!showNotSigned && showPartiallySigned){
					criteria1.add(Restrictions.sqlRestriction("AlfrescoDocument_.documentStatus = 'phidic.spisal.documentstatus.2_V0'"));
				}
				if(showNotSigned && showPartiallySigned){
					criteria1.add(Restrictions.sqlRestriction("(AlfrescoDocument_.documentStatus = 'phidic.spisal.documentstatus.generato_V0' OR AlfrescoDocument_.documentStatus = 'phidic.spisal.documentstatus.2_V0')"));
				}
				criteria1.setProjection(Projections.property("AlfrescoDocument.internalId"));
				entityCriteria.add(Subqueries.exists(criteria1));
			}
		} catch (Exception ex) {
			log.error(ex);
		} 
	}

	private Integer evaluateNextValue(ServiceDeliveryLocation uos, int annoPratica){
		try{
			if(!isWorkingLineOK(uos)){
				return null;
			}

			CodeValueParameter nrSkipDistr = ca.get(CodeValueParameter.class, "p.home.procpratiche.nrskipdistr");

			if (nrSkipDistr==null)
				return null;

			CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(nrSkipDistr, uos.getParent().getParent().getInternalId());
			String value = evaluatedParameter.get("value").toString();

			Boolean skipDistrict = (value!=null && "true".equals(value))?true:false;
			Calendar cal = Calendar.getInstance();

			/* H00491319 - calcolo ed aggiungo ai parametri il primo (minDate)
			 * e l'ultimo (maxDate) giorno dell'anno pratica */
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, annoPratica);
			Date minDate = cal.getTime();

			cal.set(Calendar.MONTH, 11);
			cal.set(Calendar.DAY_OF_MONTH, 31);
			cal.set(Calendar.YEAR, annoPratica);
			Date maxDate = cal.getTime();

			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("wlArea", uos.getArea().getId());
			parameters.put("minDate", minDate);
			parameters.put("maxDate", maxDate);
			parameters.put("ulssId", null);
			parameters.put("wlId", null);
			parameters.put("vType", null);

			if(Boolean.TRUE.equals(skipDistrict)){//filtro solo la ulss
				parameters.put("ulssId", uos.getParent().getParent().getInternalId());
			} else{//filtro la linea di lavoro
				parameters.put("wlId", uos.getInternalId());
			}

			if (uos.getArea()!=null && uos.getArea().getCode()!=null){
				String type = uos.getArea().getCode();

				// Per la linea di lavoro VIGILANZA devo filtrare il sottotipo (aziende, cantieri, amianto)
				if (type.equals("SUPERVISION")) {
					Context conversationContext = Contexts.getConversationContext();
					Vigilanza vigilanza = (Vigilanza)conversationContext.get("Vigilanza");
					if (vigilanza!=null && vigilanza.getType()!=null){
						parameters.put("vType", vigilanza.getType().getId());
					}
				}
			}

			@SuppressWarnings("unchecked")
			List<Integer> ppList = (List<Integer>)ca.executeNamedQuery("Procpratiche.getCurrentNumber", parameters);

			Integer ret = 1;
			Integer numero = ppList.get(0);

			if (numero != null) {
				ret = numero+1;
			}

			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);

		} 
	}

	private boolean isWorkingLineOK(ServiceDeliveryLocation uos){
		//Linea di lavoro
		if (uos==null){
			log.info("uos is null");
			return false;
		}

		if (uos.getArea()==null){
			log.info("uos area is null");
			return false;
		}

		//Distretto
		ServiceDeliveryLocation uoc = uos.getParent();
		String idDistretto = "";

		if (uoc==null){
			log.info("uoc is null");
			return false;

		} else if (uoc.getId("HBS")!=null)
			idDistretto = uoc.getId("HBS").getExtension();

		if (idDistretto==null || "".equals(idDistretto)){
			log.info("uoc.id is null");
			return false;
		}

		//Ulss
		ServiceDeliveryLocation ulss = uoc.getParent();
		if (ulss==null){
			log.info("ulss is null");
			return false;
		}

		Organization org = ulss.getOrganization();
		String idUlss = "";

		if (org==null){
			log.info("uos is null");
			return false;
		} else 
			idUlss = org.getId();

		if (idUlss==null || "".equals(idUlss)){
			log.info("ulss.id is null");
			return false;
		}

		return true;
	}

	/* Formato: CodiceUSL_CodiceUOC_Anno_AbbreviazioneLineaDiLavoro_Progressivo */
	public void setNumero(ServiceDeliveryLocation uos){
		try{
			Procpratiche procpratiche = getEntity();
			String numero = procpratiche.getNumero();

			//H00491319 - Recupero l'anno della pratica
			Calendar dataPratica = Calendar.getInstance();
			dataPratica.setTime(procpratiche.getData());
			int annoPratica = dataPratica.get(Calendar.YEAR);

			if (numero!=null && !"".equals(numero))
				return;

			String separatore = "_";
			String wLine = "";

			Integer nextValue = this.evaluateNextValue(uos, annoPratica);

			if (nextValue != null) {
				if (uos != null){
					CodeValuePhi warkingLine = uos.getArea();

					if (warkingLine != null){
						String warkingLineAbbreviation = warkingLine.getAbbreviation();
						String warkingLineCode = warkingLine.getCode();

						if ((warkingLineAbbreviation != null) && (warkingLineAbbreviation != "")) {

							if (warkingLineCode.equals("SUPERVISION")) {
								Context conversationContext = Contexts.getConversationContext();
								Vigilanza vigilanza = (Vigilanza)conversationContext.get("Vigilanza");

								if (vigilanza != null && vigilanza.getType() != null) {
									String vType = vigilanza.getType().getCode();

									if (vType.equals("Yard"))
										wLine = warkingLineAbbreviation + separatore + "CA";

									else if (vType.equals("Generic"))
										wLine = warkingLineAbbreviation + separatore + "AZ";

									else if (vType.equals("Asbestos"))
										wLine = warkingLineAbbreviation + separatore + "AM";

								}

							} else wLine = warkingLineAbbreviation;

						} else wLine = "??";
					}

					ServiceDeliveryLocation uoc = uos.getParent();

					if (uoc != null) {
						Organization usl = uoc.getParent().getOrganization();

						//Setta CodiceUSL
						if (usl == null)
							numero = "????" + separatore;
						else {
							String id = usl.getId();

							if ((id != null) && (id != ""))
								numero = id + separatore;

							else numero = "????" + separatore;
						} 

						//Setta CodiceUOC (Distretto)
						II4ServiceDeliveryLocation id = (II4ServiceDeliveryLocation)uoc.getId("HBS");

						if (id == null)
							numero += "??" + separatore;
						else {
							String code = id.getExtension();

							if ((code != null) && (code != ""))
								numero += code + separatore;

							else numero += "??" + separatore;
						} 
					}
				}

				numero += annoPratica + separatore;				

				DecimalFormat df = new DecimalFormat("00000");
				numero += wLine + separatore + df.format(nextValue);

				procpratiche.setNumero(numero);
				procpratiche.setNrPratica(nextValue);
			} else 
				log.info("nextValue is null");

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public String getWorkingLine(Protocollo protocollo){
		try{
			String workingLine = null;			
			if (protocollo != null){
				ServiceDeliveryLocation uos = protocollo.getUos();
				if (uos != null){
					CodeValue area = uos.getArea();

					if (area != null)
						workingLine = area.getCode();
				}
			}
			return workingLine;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	/* I0064248 - Questo metodo viene invocato in gestioneSegnalazioni.jpdl.xml (nodo <save pratica>) 
	 * A seguito di modifiche o integrazioni sulla comunicazione (master) già agganciata alla pratica (stato completed) aggiorna i riferimenti presenti 
	 * sulla pratica associata. Non saranno oggetto di allineamenti automatici quei riferimenti già agganciati alle attività elementari e sarà carico 
	 * dell'operatore sanare le situazioni incongruenti.
	 * 
	 * Attualmente il sistema permette di modificare la comunicazione solo attivando, per i ruoli che lo desiderano, il parametro p.home.gestione_segnalazione.modificaSegnalazioneAssegnata. 
	 * La modifica della comunicazione assegnata permette di ottenere una situazione pulita, con i dettagli a cui fa riferimento la pratica perfettamente allineati, per ricerche e conteggi.
	 * 
	 * Non saranno prese in considerazione dall'intervento in esame le comunicazioni oggetto di associazioni secondarie */
	public void checkProtocollo(Protocollo protocollo){
		try{
			//Controlla che la comunicazione oggetto di modifica sia la comunicazione principale
			if (protocollo==null || !protocollo.getIsMaster())
				return;

			//Controlla che la comunicazione oggetto di modifica sia in stato assegnata (completed)
			if (protocollo.getStatusCode()==null || protocollo.getStatusCode().getCode()==null || !"completed".equals(protocollo.getStatusCode().getCode()))
				return;

			/* Aggiorna i riferimenti associando alla pratica un nuovo oggetto PraticheRiferimenti 
			 * 
			 * Questo meccanismo (effettuato per tutte le linee di lavoro) è suffucuente per:
			 * 
			 * ASBESTOSREMOVAL	(Bonifica amianto)
			 * COUNSELING 		(Sportello ascolto e centro benessere organizzativo)
			 * GENERIC			(Attività generica)
			 * INFORMATION		(Informazione)
			 * LIFESTYLE		(Promozione della salute e stili di vita)
			 * TRAINING			(Formazione)
			 * WORKACCIDENTREG	(Vidimazione registro infortuni) */
			this.copyRiferimenti(protocollo);

			/* Per SUPERVISION (Vigilanza Spisal) - Viene Invocato (da processo) il metodo 
			 * VigilanzaAction.checkProtocollo(Protocollo protocollo, DettagliBonifiche dettagliBonifiche) */

			/* Per: 
			 * WORKACCIDENT 	(Infortunio sul lavoro)
			 * TECHNICALADVICE 	(Espressione Pareri Tecnici Formalizzati)	
			 * 
			 * Viene invocato this.copyFromProtocollo(Protocollo protocollo) */
			String workingLine = this.getWorkingLine(protocollo);
			if (workingLine != null && (workingLine.equals("WORKACCIDENT") || workingLine.equals("TECHNICALADVICE")))
				this.copyFromProtocollo(protocollo);

			/* Per WORKDISEASE	(Malattia professionale) - Non dovrebbe servire niente */

			/* Per WORKMEDICINE	(Medicina del lavoro) - Viene Invocato (da processo) il metodo 
			 * MedicinaLavoroAction.checkProtocollo(Protocollo protocollo) */  

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void copyFromProtocollo(Protocollo protocollo){
		try{
			String workingLine = this.getWorkingLine(protocollo);
			if (workingLine != null && workingLine != ""){

				if (workingLine.equals("WORKACCIDENT")){

					/* H00216311 - per evitare duplicazioni o sovrascritture, il legame tra infortunato (creato dal protocollo) 
					 * e pratica viene creato solamente se la lista infortunati nella pratica è vuota */
					Procpratiche p = getEntity();
					if (p!=null && (p.getInfortuni()==null || p.getInfortuni().size()==0)){

						Infortuni infPratica =  InfortuniAction.instance().copy(protocollo);
						if (infPratica!=null){
							infPratica.setProcpratiche(getEntity());
							this.link("infortuni", infPratica);
						}
					}

				} else if (workingLine.equals("TECHNICALADVICE")) {
					if (protocollo.getCode() != null && protocollo.getCode().getCode() != null) {
						String code = protocollo.getCode().getCode();
						if (code.equals("8.1") || code.equals("8.2") || code.equals("8.3")) {
							//Tipo comunicazione Abilitazioni patentini

							ParereTecnicoAction parTecAct = ParereTecnicoAction.instance();
							ParereTecnico parTec = parTecAct.getEntity();
							if (parTec == null) {
								parTec = new ParereTecnico();
								parTecAct.inject(parTec);
								parTec.setProcpratiche(protocollo.getProcpratiche());
								parTecAct.create();
							}

							//Set type to: Patentino per l'abilitazione all'impiego dei gas tossici
							parTecAct.setCodeValue("type", "PHIDIC", "TAType", "TA08");
						}
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void setWorkingLine(String filter) {
		try {

			if (this.getEqual()!=null ) {

				Object area = this.getEqual().get(filter);//uos.area in segnalazioni.mmgp
				if (area instanceof CodeValue ) {
					CodeValue cv = (CodeValue)area;
					if (cv instanceof HibernateProxy)
						cv = (CodeValuePhi)((HibernateProxy)cv).getHibernateLazyInitializer().getImplementation();

					this.getTemporary().put("workingLine", cv.getCode());

				} else { 
					this.getTemporary().remove("workingLine");
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void setUlss() {
		try {
			//togliere il fitro prima!!!
			getEqual().remove("serviceDeliveryLocation.parent.parent.internalId");
			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");

				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.parent.parent.internalId", id);
				}
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void cleanWorkingLine() {
		try {
			Procpratiche pratica = getEntity();
			if (pratica!=null && pratica.getServiceDeliveryLocation().getArea().getCode().equals("WORKACCIDENT")){
				List<Infortuni> infortuni = pratica.getInfortuni();
				InfortuniAction infortuniAction	= new InfortuniAction();

				for (Infortuni infortunio:infortuni){
					infortunio.setProcpratiche(null); //piu efficiente
					ca.delete(infortunio);
				}
				pratica.setInfortuni(null);

				ca.flushSession();
				infortuniAction.eject();
				infortuniAction.ejectList();

			}else if (pratica.getServiceDeliveryLocation().getArea().getCode().equals("TECHNICALADVICE")){
				List<ParereTecnico> pareri = pratica.getParereTecnico();
				ParereTecnicoAction pareriAction = ParereTecnicoAction.instance();

				for (ParereTecnico parere : pareri){
					if(parere!=null && ca.contains(parere)){
						parere.setProcpratiche(null);
						ca.delete(parere);
					}
				}
				pratica.setParereTecnico(null);

				ca.flushSession();
				pareriAction.eject();
				pareriAction.ejectList();

			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void initTypeFilter() {
		getTemporary().put("parereTecnico.type", new CodeValue[]{});
	}

	public String getFascicoli(List<TagFascicolo> listaFascicoli){

		String result = "";

		for(TagFascicolo fascicolo : listaFascicoli){

			if(fascicolo!=null){
				result+=fascicolo.getFascicolo()+" ";
			}else{
				result+="null "; 
			}
		}
		return result;
	}

	public String getFascicoli(Long pratId){

		String ret = "";
		if(pratId!=null){
			String hql = "SELECT t.fascicolo FROM Procpratiche p " +
					"JOIN p.tagFascicolo t " +
					"WHERE p.internalId = :pratId";
			Query q = ca.createQuery(hql);
			q.setParameter("pratId", pratId);
			List<Object> result = (List<Object>)q.getResultList();
			if(result!=null && !result.isEmpty()){
				for(Object o : result){
					if(o!=null){
						ret+=(String)o+", ";
					}
				}
			}
		}

		if(ret.endsWith(", ")) ret=ret.substring(0,ret.length()-2);

		return ret;

	}

	/*
	public void create() throws PhiException{

		log.info("create override");
		super.create(entity);
	}
	 */

	public boolean checkActivities(Procpratiche prat) {
		try {
			if (prat == null)
				return true;

			List<Attivita> attivita = prat.getAttivita();
			if (attivita==null || attivita.size()==0)
				return false;

			for (Attivita att : attivita) {
				if (att.getIsActive()){
					if (att.getStatusCode()==null || att.getStatusCode().getCode().equals("active"))
						return false;
				}
			}

			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public boolean checkProvvedimenti(Procpratiche prat) {
		try {
			if (prat == null)
				return true;

			List<Provvedimenti> provvList = prat.getProvvedimenti();
			if (provvList==null || provvList.size()==0)
				return true;

			for (Provvedimenti provv : provvList) {
				if (provv.getIsActive()){
					if (provv.getStatusCode()==null || provv.getStatusCode().getCode().equals("active"))
						return false;
				}
			}

			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void setMasterTemporary(Object pratObj) {

		//Procpratiche procpratiche = getEntity();
		Vocabularies voc = VocabulariesImpl.instance();
		if(pratObj instanceof Procpratiche){
			Procpratiche prat = (Procpratiche)pratObj;
			List<Protocollo> protocolloList = prat.getProtocollo();

			if (protocolloList != null && protocolloList.size() > 0 ) { 
				for (Protocollo protocollo : protocolloList) {
					if (protocollo.getIsMaster()){
						temporary.put("masterAnnoProtocollo", protocollo.getAnnoProtocollo());
						temporary.put("masterNprotocollo", protocollo.getNprotocollo());
						temporary.put("masterNrichiesta", protocollo.getNrichiesta());
						temporary.put("masterData", protocollo.getData());
					}
				}
			}
		}
	}

	/**
	 * Questo metodo viene chiamato ogni volta che viene creata una nuova pratica a partire da una segnalazione.
	 * Copia i riferimenti della segnalazione nella pratica.
	 * Per i cantieri effettua il clone: tale clone è il CLONE IN PRATICA: lo stesso verrà utilizzato in vigilanza/infortuni ecc.
	 * @param prot
	 * @throws PersistenceException 
	 */
	public void copyRiferimenti(Protocollo prot) throws PersistenceException {
		try {
			Procpratiche prat = getEntity();
			AttivitaIstatAction aAction = AttivitaIstatAction.instance();

			if (prot == null || prat == null)
				return;

			PraticheRiferimenti pr = new PraticheRiferimenti();
			Cantiere cloneCondiviso = null;

			// RICHIEDENTE
			pr.setRichiedente(prot.getRichiedente());
			pr.setRichiedenteDitta(prot.getRichiedenteDitta());
			pr.setRichiedenteInterno(prot.getRichiedenteInterno());
			pr.setRichiedenteMedico(prot.getRichiedenteMedico());
			pr.setRichiedenteSede(prot.getRichiedenteSede());
			pr.setRichiedenteUtente(prot.getRichiedenteUtente());

			pr.setCompartoRichiedente(aAction.getImportantAteco(
					prot.getRichiedenteDitta(),
					prot.getRichiedenteSede()));

			// RIFERITO A
			pr.setRiferimento(prot.getRiferimento());
			// http://support.insielmercato.it/mantis/view.php?id=32590
			if (prot.getRiferimentoCantiere() != null) {
				if (prot.getUbicazioneCantiere() != null
						&& prot.getUbicazioneCantiere().getInternalId() == prot
						.getRiferimentoCantiere().getInternalId()) {

					cloneCondiviso = CantiereAction.instance().copy(
							prot.getRiferimentoCantiere());
					ca.create(cloneCondiviso);// siamo in superstate
					pr.setRiferimentoCantiere(cloneCondiviso);
				} else {
					Cantiere cantiere = CantiereAction.instance().copy(
							prot.getRiferimentoCantiere());
					ca.create(cantiere);// siamo in superstate
					pr.setRiferimentoCantiere(cantiere);
				}
			}

			pr.setRiferimentoDitta(prot.getRiferimentoDitta());

			//increase pratiche counter for companies
			/*if(prot.getRiferimentoDitta()!=null){
				PersoneGiuridiche pg = prot.getRiferimentoDitta();
				if (pg.getNumPratiche()==null)
					pg.setNumPratiche(0);

				pg.setNumPratiche(pg.getNumPratiche()+1);
				ca.create(pg);
			}*/

			pr.setRiferimentoInterno(prot.getRiferimentoInterno());
			pr.setRiferimentoSede(prot.getRiferimentoSede());
			pr.setRiferimentoUtente(prot.getRiferimentoUtente());
			pr.setRiferimentoDenominazione(prot.getRiferimentoDenominazione());
			pr.setRiferimentoEntita(prot.getRiferimentoEntita());
			pr.setRiferimentoIMO(prot.getRiferimentoIMO());
			pr.setRiferimentoNote(prot.getRiferimentoNote());
			pr.setRiferimentoTarga(prot.getRiferimentoTarga());
			pr.setRiferimentoSpec(prot.getRiferimentoSpec());

			pr.setCompartoRiferimento(aAction.getImportantAteco(
					prot.getRiferimentoDitta(),
					prot.getRiferimentoSede()));

			// UBICAZIONE
			pr.setUbicazione(prot.getUbicazione());
			// http://support.insielmercato.it/mantis/view.php?id=32590
			if (prot.getUbicazioneCantiere() != null) {
				if (cloneCondiviso != null) {
					pr.setUbicazioneCantiere(cloneCondiviso);
				} else {
					Cantiere cantiere = CantiereAction.instance().copy(
							prot.getUbicazioneCantiere());
					ca.create(cantiere);// siamo in superstate
					pr.setUbicazioneCantiere(cantiere);
				}
			}

			pr.setUbicazioneDitta(prot.getUbicazioneDitta());
			pr.setUbicazioneSede(prot.getUbicazioneSede());
			pr.setUbicazioneUtente(prot.getUbicazioneUtente());
			pr.setUbicazioneEntita(prot.getUbicazioneEntita());
			pr.setUbicazioneIMO(prot.getUbicazioneIMO());
			pr.setUbicazioneTarga(prot.getUbicazioneTarga());
			pr.setUbicazioneSpec(prot.getUbicazioneSpec());
			pr.setUbicazioneX(prot.getUbicazioneX());
			pr.setUbicazioneY(prot.getUbicazioneY());
			pr.setUbicazioneLocalita(prot.getUbicazioneLocalita());


			if (prot.getUbicazioneAddr() != null) {
				pr.setUbicazioneAddr(prot.getUbicazioneAddr().cloneAd());
			}

			pr.setCompartoUbicazione(aAction.getImportantAteco(
					prot.getUbicazioneDitta(),
					prot.getUbicazioneSede()));

			ca.create(pr);
			// LINK TO PRATICHE
			pr.addProcpratiche(prat);
			prat.setPraticheRiferimenti(pr);

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public boolean contains(Long operatoreInternalId){
		try{
			Procpratiche pratica = getEntity();
			if (pratica!=null && pratica.getOperatori()!=null){
				List<Operatore> operatori = pratica.getOperatori();

				for (Operatore operatore : operatori) {
					if (operatore.getInternalId()==operatoreInternalId && operatore.getIsActive())
						return true;
				}
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}


	public List<SelectItem> getEsitiAttivita()  throws Exception {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		Procpratiche pratica = getEntity();

		if (pratica == null) {
			return null;
		}

		List<Protocollo> protocolloList = pratica.getProtocollo();

		String workingLine = null;

		if (protocolloList != null && protocolloList.size() > 0 ) { 
			for (Object protocollo : protocolloList) {
				if (protocollo instanceof Protocollo) {
					if (((Protocollo)protocollo).getIsMaster()){
						workingLine =  this.getWorkingLine((Protocollo)protocollo);
					}
				} else if (protocollo instanceof Map) {
					if ("true".equals(((Map)protocollo).get("isMaster"))){
						Protocollo prot = ca.get(Protocollo.class, (Long)((Map)protocollo).get("internalId"));
						workingLine =  this.getWorkingLine(prot);
					}
				}
			}
		}

		if (pratica.getServiceDeliveryLocation() != null && pratica.getServiceDeliveryLocation().getArea() != null) {
			if (pratica.getServiceDeliveryLocation().getArea().getCode().equals("WORKDISEASE")) {
				workingLine="WORKDISEASE";
				//getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "WORKDISEASE"));
			} else if (pratica.getServiceDeliveryLocation().getArea().getCode().equals("WORKACCIDENT")) {
				//getEqual().put("type", voc.getCodeValueCsDomainCode("PHIDIC", "ReportType", "WORKACCIDENT"));
				workingLine="WORKACCIDENT";
			}
		}

		if (workingLine != null && workingLine != ""){
			Vocabularies voc = VocabulariesImpl.instance();

			//Work Accident
			if (workingLine.equals("WORKDISEASE")){

				for (int i=1;i<=10;i++) {

					String code = String.format("%02d", i);
					CodeValuePhi cv = (CodeValuePhi)voc.getCodeValue("PHIDIC", "EsitoConclusione", code, "C");
					if (cv  != null) {
						SelectItem selItem = new SelectItem("CodeValuePhi-"+cv.getId() , cv.getTranslation(Locale.instance().getLanguage()));
						selectItems.add(selItem);
					}
				}

			} else if (workingLine.equals("WORKACCIDENT")){
				for (int i=11;i<=21;i++) {
					String code = String.format("%02d", i);
					CodeValuePhi cv = (CodeValuePhi)voc.getCodeValue("PHIDIC", "EsitoConclusione", code, "C");
					if (cv  != null) {
						SelectItem selItem = new SelectItem("CodeValuePhi-"+cv.getId() , cv.getTranslation(Locale.instance().getLanguage()));
						selectItems.add(selItem);
					}
				}
			}

		}
		if (selectItems.size()>1)
			Collections.sort(selectItems, comparator);

		return selectItems;
	}

	Comparator<SelectItem> comparator = new Comparator<SelectItem>() {
		@Override
		public int compare(SelectItem s1, SelectItem s2) {
			return s1.getLabel().compareTo(s2.getLabel());
		}
	};

	public void setActivitiesColor(Procpratiche prat) {
		try {
			if (prat == null){
				this.getTemporary().put("color", "orange");
				return;
			}

			//Se non ci sono attività
			List<Attivita> attivitaLst = prat.getAttivita();
			if (attivitaLst==null || attivitaLst.size()==0)
				this.getTemporary().put("color", "lightblue");

			else {
				int tot = 0; 
				int completed=0; int nullified = 0;

				for (Attivita att : attivitaLst) {
					if (att.getIsActive()){
						tot++;
						if (att.getStatusCode()!=null)
							if (att.getStatusCode().getCode().equals("completed"))
								completed++;
							else if (att.getStatusCode().getCode().equals("nullified"))
								nullified++;
					}
				}

				//Se tutte le attività sono state eliminate -> non ci sono attività
				if(tot==0)
					this.getTemporary().put("color", "lightblue");

				//Se tutte le attività sono fallite -> rosso
				else if (tot>0 && nullified>0 && tot==nullified)
					this.getTemporary().put("color", "red");

				//Se tutte le attività sono concluse -> verde
				else if (tot>0 && completed>0 && tot==completed)
					this.getTemporary().put("color", "green");

				//se c’è almeno una attività in stato non concluso -> giallo 
				else if (completed>0)
					this.getTemporary().put("color", "yellow");

				//In tutti gli altri casi -> arancione
				else 
					this.getTemporary().put("color", "orange");
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	/**
	 * If Procpratiche already linked to cantiere, return cantiere otherwise return a new clone
	 * @param cantiere
	 * @return 
	 */
	public Cantiere getCloneOrRevision(Cantiere cantiere) {
		getEntity();
		for (Cantiere c : entity.getCantiere()) {
			if (cantiere.equals(c.getOriginal())) {
				return c;
			}
		}
		return CantiereAction.instance().copy(cantiere);
	}

	private static String GET_RDP = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.rdpOf sdl " +
			"WHERE sdl.internalId =:internalID";

	private static String GET_DIRECTOR = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.directorOf sdl " +
			"WHERE sdl.internalId =:internalID";

	private static String GET_RDP_SUPERVISION = "SELECT op FROM Operatore op " +
			"LEFT JOIN op.vigilanzaRdpType t " +
			"WHERE t.serviceDeliveryLocation =:sdlID " +
			"AND t.vigilanzaType =:vType";

	public void initOperatori() {
		try{
			Procpratiche procpratiche = getEntity();

			if (procpratiche.getOperatori() == null)
				procpratiche.setOperatori(new ArrayList<Operatore>());

			ServiceDeliveryLocation uos = procpratiche.getServiceDeliveryLocation();
			ServiceDeliveryLocation uoc = procpratiche.getUoc();

			if (uos != null){


				//Get RDP
				HashMap<String, Object> parameters;
				List<Operatore> operatori = null;

				if(!uos.getArea().getCode().equalsIgnoreCase("SUPERVISION")){

					parameters = new HashMap<String, Object>(2);
					parameters.put("internalID", uos.getInternalId());
					operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_RDP, parameters);

				} else {

					parameters = new HashMap<String, Object>(2);

					parameters.put("sdlID", uos);
					parameters.put("vType", VigilanzaAction.instance().getEntity().getType());
					operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_RDP_SUPERVISION, parameters);

				}

				if (operatori!=null && operatori.size()>0){
					Operatore rdp = operatori.get(0);

					procpratiche.getOperatori().add(rdp);
					if (rdp.getEmployee()!=null)
						procpratiche.setRdp(rdp.getEmployee());
				}

			} 

			if (procpratiche.getRdp()==null && uoc != null){
				//Get director
				HashMap<String, Object> parameters= new HashMap<String, Object>(2);
				parameters.put("internalID", uoc.getInternalId());
				List<Operatore> operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_DIRECTOR, parameters);

				if (operatori!=null && operatori.size()>0){
					Operatore director = operatori.get(0);

					procpratiche.getOperatori().add(director);
					if (director.getEmployee()!=null)
						procpratiche.setRdp(director.getEmployee());
				}

			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public Operatore getRDP(ServiceDeliveryLocation sdl) {
		try{

			if (sdl == null)
				return null;

			//Search RDP
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("internalID", sdl.getInternalId());
			List<Operatore> operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_RDP, parameters);

			if (operatori!=null && operatori.size()>0)
				return operatori.get(0);

			else {
				//Get director as RDP
				parameters= new HashMap<String, Object>(2);
				parameters.put("internalID", sdl.getParent().getInternalId());
				operatori = (List<Operatore>) ca.executeHQLwithParameters(GET_DIRECTOR, parameters);

				if (operatori!=null && operatori.size()>0)
					return operatori.get(0);
			}

			return null;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void copyAccidentLocationFromProtocollo(Protocollo protocollo){

		Vocabularies voc = VocabulariesImpl.instance();

		try{
			String workingLine = this.getWorkingLine(protocollo);
			if (workingLine != null && workingLine != ""){

				//Work Accident
				if (workingLine.equals("WORKACCIDENT")){
					InfortuniAction ia	=  InfortuniAction.instance();
					Infortuni infSegnalazione = protocollo.getInfortunio();
					if (infSegnalazione!=null){
						Infortuni infPratica = ia.getEntity();

						InfortuniExt infExt=infPratica.getInfortuniExt();


						//infPratica.setProcpratiche(getEntity());//I0051626 
						infPratica.setProtocollo(infSegnalazione.getProtocollo());

						//NonPrevisto, Ditta, Cantiere, Altro
						CodeValue ubicazione = protocollo.getUbicazione();
						if (ubicazione!=null){
							String code = ubicazione.getCode();
							if (code != null && code != ""){
								if (code.equals("Ditta")) {
									CodeValue pl = protocollo.getUbicazione();
									if (pl!=null){
										String place = pl.getCode();
										if (place!=null && place!=""){
											if (place.equals("Ditta")){
												CodeValue location = voc.getCodeValue("PHIDIC", "Place", "Company", "C");
												infPratica.setPlace(location);
												if (place.equals("OwnCompany")){
													infPratica.setSedi(protocollo.getUbicazioneSede());
												}

												if(infExt!=null){
													infExt.setAddr(protocollo.getUbicazioneAddr());

												}

												infPratica.setPersoneGiuridicheExt(protocollo.getUbicazioneDitta());
												infPratica.setSediExt(protocollo.getUbicazioneSede());


											}
										}
									}
								} else if (code.equals("Cantiere")) {
									Vocabularies vocabularies = VocabulariesImpl.instance();
									CodeValue pl = vocabularies.getCodeValueCsDomainCode("PHIDIC", "Place", "Yard");
									infPratica.setPlace(pl);

									infPratica.setCantiere(protocollo.getUbicazioneCantiere());
								} else if (code.equals("Altro")){
									Vocabularies vocabularies = VocabulariesImpl.instance();
									CodeValue pl = vocabularies.getCodeValueCsDomainCode("PHIDIC", "Place", "Other");
									infPratica.setPlace(pl);

									infPratica.getInfortuniExt().setEntita((CodeValuePhi)protocollo.getUbicazioneEntita());
									infPratica.getInfortuniExt().setImo(protocollo.getUbicazioneIMO());
									infPratica.getInfortuniExt().setTarga(protocollo.getUbicazioneTarga());
								}
							}
						}

						infPratica.getInfortuniExt().setSpecificazione(protocollo.getUbicazioneSpec());
						if(protocollo.getUbicazioneAddr()!=null){
							infPratica.getInfortuniExt().setAddr(protocollo.getUbicazioneAddr().cloneAd());

						}

						infPratica.getInfortuniExt().setLatitudine(protocollo.getUbicazioneX());
						infPratica.getInfortuniExt().setLongitudine(protocollo.getUbicazioneY());

						infPratica.getInfortuniExt().setRifDenominazione(protocollo.getRiferimentoDenominazione());
						infPratica.getInfortuniExt().setRifNote(protocollo.getRiferimentoNote());


						Procpratiche prat = getEntity();
						if(prat.getInfortuni()==null)
							prat.setInfortuni(new ArrayList<Infortuni>());

						prat.getInfortuni().add(infPratica);
						ca.create(infPratica);//.flushSession();

					}

				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void getArchList(String code) throws Exception{
		String benessere = null;
		BenessereOrg b = BenessereOrgAction.instance().getEntity();
		if(b!=null && b.getTipoPratica()!=null)
			benessere = b.getTipoPratica().getCode();

		String finalString = "PHIDIC:ARCH";
		List<SelectItem> lst = new ArrayList<SelectItem>();
		if(code!=null && !code.isEmpty()){
			finalString+=code;
			String id = "phidic.spisal.pratiche.conc."+code.toLowerCase();
			if("COUNSELING".equals(code) && (benessere!=null && !benessere.isEmpty())){
				finalString+=benessere;
				id+=benessere.toLowerCase();
			}else if("COUNSELING".equals(code) && (benessere==null || benessere.isEmpty())){
				getTemporary().put("archList", lst);
				return;
			}
			String hql = "SELECT count(cv) FROM CodeValuePhi cv WHERE cv.type = 'C' AND cv.id like :id";
			id+="%";

			Query q = ca.createQuery(hql);
			q.setParameter("id", id);
			Long count = (Long)q.getSingleResult();
			if(count!=null && count>0 && !"PHIDIC:ARCH".equals(finalString)){
				Vocabularies voc = VocabulariesImpl.instance();
				lst = voc.getIdValues(finalString);

			}
		}
		getTemporary().put("archList", lst);
	}

	public void injectBuonePratiche(Procpratiche p){
		Context conv = Contexts.getConversationContext();
		List<String> buonePratiche = new ArrayList<String>();
		if(p!=null && p.getBuonePratiche()!=null){
			for(CodeValueLaw c : p.getBuonePratiche()){
				buonePratiche.add(c.getId());
			}
		}

		conv.set("BuonePraticheList", buonePratiche);
	}

	public void setBuonePratiche(List<String> codeIds){
		Procpratiche prat = getEntity();
		if(prat==null)
			return;

		if(codeIds!=null && !codeIds.isEmpty()){
			List<CodeValueLaw> lawCodes = new ArrayList<CodeValueLaw>();
			for(String id : codeIds){ 
				CodeValueLaw cv = ca.get(CodeValueLaw.class, id);
				lawCodes.add(cv);
			}
			prat.setBuonePratiche(lawCodes);
		}else{
			prat.setBuonePratiche(null);
		}
	}

	public boolean checkProcpratiche(){
		boolean isValid = true;
		Procpratiche pratica = getEntity();

		if (pratica==null)
			return isValid;

		String workingLine = null;			
		ServiceDeliveryLocation sdl = pratica.getServiceDeliveryLocation();
		//Se non è ancora stata settata la linea di lavoro. Essendoci un blocco applicativo non dovrebbe mai verificarsi
		if (sdl==null || sdl.getArea()==null || sdl.getArea().getCode()==null)
			return isValid;

		workingLine = sdl.getArea().getCode();

		//Linea di lavoro Incidente sul lavoro
		if (workingLine.equals("WORKACCIDENT")){
			List<Infortuni> infortuni = pratica.getInfortuni();

			if (infortuni==null || infortuni.size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Inserire almeno un infortunato.", "Inserire almeno un infortunato.");
				return isValid = false;
			}

			if (pratica.getEsitoPratica()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Atto conclusivo obbligatorio.", "Atto conclusivo obbligatorio.");
				isValid = false;
			}

			for (Infortuni infortunio : infortuni) {

				//Non controllare l'n° infortunato se nell'infortunato n-1 ci sono campi non validi
				if (!isValid)
					return isValid;

				if (infortunio.getApplicant()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Segnalante obbligatorio.", "Segnalante obbligatorio.");
					isValid = false;
				}

				if (infortunio.getData()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Data infortunio obbligatorio.", "Data infortunio obbligatorio.");
					isValid = false;
				}

				if (infortunio.getPerson()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Infortunato obbligatorio.", "Infortunato obbligatorio.");
					isValid = false;
				}

				if (infortunio.getPersoneGiuridiche()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Ditta obbligatoria.", "Ditta obbligatoria.");
					isValid = false;
				}

				if (infortunio.getComparto()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Attività ditta obbligatoria.", "Attività ditta obbligatoria.");
					isValid = false;
				}

				if (infortunio.getRuolo()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Ruolo obbligatorio.", "Ruolo obbligatorio.");
					isValid = false;
				}

				if (infortunio.getPlace()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipologia obbligatoria.", "Tipologia obbligatoria.");
					isValid = false;

				} else {
					CodeValue cv = infortunio.getPlace();

					String code="";
					if (cv!=null)
						code = cv.getCode();

					//Se Company (Altra azienda) 
					if (code.equals("Company") && infortunio.getPersoneGiuridicheExt()==null){
						FacesErrorUtils.addErrorMessage("commError", "Altra azienda obbligatoria.", "Altra azienda obbligatoria.");
						isValid = false;

						//Se OwnCompany (Azienda propria)
					} else if (code.equals("OwnCompany") && infortunio.getPersoneGiuridiche()==null){
						FacesErrorUtils.addErrorMessage("commError", "Azienda propria obbligatoria.", "Azienda propria obbligatoria.");
						isValid = false;
					}
				}

				if (infortunio.getGgPrognosi1()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Giorni iniziali prognosi obbligatori.", "Giorni iniziali prognosi obbligatori.");
					isValid = false;
				}

				if (infortunio.getGgPrognosi2()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Giorni totali invalidità obbligatori.", "Giorni totali inabilità obbligatori.");
					isValid = false;
				}

				if (infortunio.getSedeLesione()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Sede lesione obbligatoria.", "Sede lesione obbligatoria.");
					isValid = false;
				}

				if (infortunio.getNaturaLesione()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Natura lesione obbligatoria.", "Natura lesione obbligatoria.");
					isValid = false;
				}

				/* I0062105
				if (infortunio.getGravita()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Gravità iniziale obbligatoria.", "Gravità iniziale obbligatoria.");
					isValid = false;
				}*/

				if (infortunio.getInfortuniExt()==null || infortunio.getInfortuniExt().getGravitaFinale()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Gravità finale obbligatoria.", "Gravità finale obbligatoria.");
					isValid = false;
				}

				if (infortunio.getTipoContratto()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipologia contratto obbligatoria.", "Tipologia contratto obbligatoria.");
					isValid = false;
				}

				if (infortunio.getQualifica()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Qualifica obbligatoria.", "Qualifica obbligatoria.");
					isValid = false;
				}

				if (infortunio.getMansione()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Mansione obbligatoria.", "Mansione obbligatoria.");
					isValid = false;
				}


				if (infortunio.getForma()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Forma obbligatoria.", "Forma obbligatoria.");
					isValid = false;
				}

				if (infortunio.getAgenteMateriale()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Agente materiale obbligatorio.", "Agente materiale obbligatorio.");
					isValid = false;
				}

				if (infortunio.getCondizioniDiRischio()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Condizioni obbligatorie.", "Condizioni obbligatorie.");
					isValid = false;
				}

				if (infortunio.getComportamento()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Comportamento obbligatorio.", "Comportamento obbligatorio.");
					isValid = false;
				}

				if (infortunio.getCompSpec()==null || infortunio.getCompSpec().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Specificazioni del comportamento obbligatorio.", "Specificazioni del comportamento obbligatorio.");
					isValid = false;
				}

				if (infortunio.getEvitabilita()==null || infortunio.getEvitabilita().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Evitabilità infortunio obbligatoria.", "Evitabilità infortunio obbligatoria.");
					isValid = false;
				}

				//I00119459
				InfortuniExtAction infExtAction = InfortuniExtAction.instance();
				if ((infortunio.getInfortuniExt()==null || infortunio.getInfortuniExt().getInfortunioProf()==null) && 
						infExtAction.isUlssEnabledToInfProf(pratica)) {
					FacesErrorUtils.addErrorMessage("commError", "Infortunio professionale obbligatorio.", "Infortunio professionale obbligatorio.");
					isValid = false;
				}
				if((infortunio.getGravita()!=null && "01".equals(infortunio.getGravita().getCode())) ||
						(infortunio.getInfortuniExt()!=null && infortunio.getInfortuniExt().getGravitaFinale()!=null && 
						"01".equals(infortunio.getInfortuniExt().getGravitaFinale().getCode()))){
					if ((infortunio.getInfortuniExt()==null || infortunio.getInfortuniExt().getModInformo()==null) &&
							infExtAction.isUlssEnabledToInformo(pratica)) {
						FacesErrorUtils.addErrorMessage("commError", "Modulo INFORMO obbligatorio.", "Modulo INFORMO obbligatorio.");
						isValid = false;
					}
					if ((infortunio.getInfortuniExt()==null || infortunio.getInfortuniExt().getModInformoNote()==null || infortunio.getInfortuniExt().getModInformoNote().isEmpty()) &&
							infExtAction.isUlssEnabledToInformo(pratica) && infortunio.getInfortuniExt().getModInformo()!=null &&
							"NO".equals(infortunio.getInfortuniExt().getModInformo().getCode())) {
						FacesErrorUtils.addErrorMessage("commError", "Note modulo INFORMO obbligatorie.", "Note modulo INFORMO obbligatorie.");
						isValid = false;
					}
				}
			}

			//Linea di lavoro Malattia professionale
		} else if (workingLine.equals("WORKDISEASE")) {
			MalattiaProfessionale malattiaProfessionale = pratica.getMalattiaProfessionale();

			if (malattiaProfessionale==null) {
				FacesErrorUtils.addErrorMessage("commError", "Malattia professionale assente.", "Malattia professionale assente.");
				return isValid = false;
			}

			if (pratica.getEsitoPratica()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Atto conclusivo obbligatorio.", "Atto conclusivo obbligatorio.");
				isValid = false;
			}

			if (malattiaProfessionale.getData()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data comunicazione obbligatoria.", "Data comunicazione obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getApplicant()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Segnalante obbligatorio.", "Segnalante obbligatorio.");
				isValid = false;
			}

			if (malattiaProfessionale.getRiferimentoUtente()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Paziente obbligatorio.", "Paziente obbligatorio.");
				isValid = false;
			}

			if (malattiaProfessionale.getCondProf()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Condizione professionale obbligatoria.", "Condizione professionale obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getMansioneAttribuita()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Mansione attribuita obbligatoria.", "Mansione attribuita obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getDitteMalattie()==null || malattiaProfessionale.getDitteMalattie().size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Ditta di attribuzione della malattia obbligatoria.", "Ditta di attribuzione della malattia obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getCertDate()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data primo certificato obbligatoria.", "Data primo certificato obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getCertMed()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Medico primo certificato obbligatorio.", "Medico primo certificato obbligatorio.");
				isValid = false;
			}

			if (malattiaProfessionale.getDiagCode()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Codice malattia obbligatorio.", "Codice malattia obbligatorio.");
				isValid = false;
			}

			if (malattiaProfessionale.getDiagDate()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data diagnosi obbligatoria.", "Data diagnosi obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getGravita()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Gravità finale obbligatoria.", "Gravità finale obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getGravitaFinale()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Gravità finale obbligatoria.", "Gravità finale obbligatoria.");
				isValid = false;
			}

			if (malattiaProfessionale.getFattoreRischio()==null || malattiaProfessionale.getFattoreRischio().size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Fattore di rischio obbligatorio.", "Fattore di rischio obbligatorio.");
				isValid = false;
			}

			//Linea di lavoro Vigilanza SPISAL	
		} else if (workingLine.equals("SUPERVISION")) {
			Vigilanza vigilanza = pratica.getVigilanza();

			if (vigilanza==null) {
				FacesErrorUtils.addErrorMessage("commError", "Vigilanza assente.", "Vigilanza assente.");
				return isValid = false;
			}

			String type = "";
			if (vigilanza.getType()!=null) 
				type = vigilanza.getType().getCode();

			if (type==null || "".equals(type)) {
				FacesErrorUtils.addErrorMessage("commError", "Tipo vigilanza obbligatoria.", "Tipo Vigilanza obbligatoria.");
				return isValid = false;
			}

			if (pratica.getEsitoPratica()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Atto conclusivo obbligatorio.", "Atto conclusivo obbligatorio.");
				isValid = false;
			}

			//Vigilanza aziende
			if ("Generic".equals(type)) {

				if (vigilanza.getPersonaGiuridicaSede()==null || vigilanza.getPersonaGiuridicaSede().size()<1) {
					FacesErrorUtils.addErrorMessage("commError", "Inserire almeno una ditta.", "Inserire almeno una ditta.");
					return isValid = false;
				}

				//Vigilanza amianto
			} else if ("Asbestos".equals(type)) {
				Boolean friabile = vigilanza.getFriabile();
				Boolean compatto = vigilanza.getCompatto();

				//Bisogna specificare se è friabile o compatto
				if ((friabile==null||!friabile)&&(compatto==null||!compatto)) {
					FacesErrorUtils.addErrorMessage("commError", "Friabile/compatto obbligatori.", "Friabile/compatto obbligatori.");
					isValid = false;
				}

				if (vigilanza.getTipoIntervento()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipologia bonifica obbligatoria.", "Tipologia bonifica obbligatoria.");
					isValid = false;
				}

				if (vigilanza.getTipoConfinamento()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Tipo confinamento obbligatorio.", "Tipo confinamento obbligatorio.");
					isValid = false;
				}

				if (vigilanza.getEffettivoInizioLavori()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Effettivo inizio lavori obbligatorio.", "Effettivo inizio lavori obbligatorio.");
					isValid = false;
				}

				if (vigilanza.getEffettivoFineLavori()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Effettiva fine lavori obbligatoria.", "Effettiva fine lavori obbligatoria.");
					isValid = false;
				}

				if (vigilanza.getBonificatiKgEffettivi()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Quantità effettiva bonificata Kg obbligatoria.", "Quantità effettiva bonificata Kg obbligatoria.");
					isValid = false;
				}

				if (vigilanza.getBonificatiMqEffettivi()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Quantità effettiva bonificata Mq obbligatoria.", "Quantità effettiva bonificata Mq obbligatoria.");
					isValid = false;
				}

				// I0062108
				if (vigilanza.getNumLavoratori()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Numero lavoratori impiegati obbligatorio.", "Numero lavoratori impiegati obbligatorio.");
					isValid = false;
				}


			}

			//Linea di lavoro Pareri tecnici		
		} else if (workingLine.equals("TECHNICALADVICE")) {
			List<ParereTecnico> pareri = pratica.getParereTecnico();

			if (pareri==null || pareri.size()<1) {
				FacesErrorUtils.addErrorMessage("commError", "Parere tecnico assente.", "Parere tecnico assente.");
				return isValid = false;
			}

			//Ce ne può essere soltanto uno
			ParereTecnico parere = pareri.get(0);

			if (parere.getType()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Tipologia pratica obbligatoria.", "Tipologia pratica obbligatoria.");
				return isValid = false;
			}

			//Per tutti i tipi diversi da TA08 e TA09
			/* Tolto su intervento 51626
			 * if (!"TA08,TA09".contains(parere.getType().getCode())) {

				//Controlla che ci sia uno studio tecnico
				PersonaGiuridicaSedeAction pgsAction = new PersonaGiuridicaSedeAction();
				String name = pgsAction.getName("DTYPE01", parere.getPersonaGiuridicaSede());

				if (name==null || name=="") {
					FacesErrorUtils.addErrorMessage("commError", "Studio tecnico obbligatorio.", "Studio tecnico obbligatorio.");
					isValid = false;
				}
			}*/

			//Per i tipi TA01 e TA02
			if ("TA01,TA02".contains(parere.getType().getCode())) {
				if (parere.getRichiesta()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Richiesta obbligatoria.", "Richiesta obbligatoria.");
					isValid = false;
				}
			}

			//Per tutti i tipi diversi da TA08
			if (!parere.getType().getCode().equals("TA08")) {
				if (parere.getParere()==null) {
					FacesErrorUtils.addErrorMessage("commError", "Parere obbligatorio.", "Parere obbligatorio.");
					isValid = false;
				}
			}

			/* Tolto su intervento 51626
			 * if (parere.getProtocolloData()==null) {
				FacesErrorUtils.addErrorMessage("commError", "Data di protocollazione obbligatoria.", "Data di protocollazione obbligatoria.");
				isValid = false;
			}*/	
		}

		return isValid;
	}
	public boolean checkPendingDocuments(Procpratiche obj) throws PersistenceException, DictionaryException{
		Boolean hasPendingDocuments=false;
		List<AlfrescoDocument> documenti = obj.getDocumenti();
		Vocabularies voc = VocabulariesImpl.instance();
		for(int i=0;i<documenti.size();i++){
			if(!documenti.get(i).getIsActive()) continue;
			if(documenti.get(i).getSignaturesReqN()==null || documenti.get(i).getSignaturesReqN()==0) continue;
			CodeValuePhi documentStatus = documenti.get(i).getDocumentStatus();
			CodeValuePhi cv = (CodeValuePhi)voc.getCodeValue("PHIDIC", "documentstatus", "5", "C");
			if(documentStatus!=cv)hasPendingDocuments=true;
		}
		return hasPendingDocuments;
	}

	/*public boolean hasListe(Procpratiche obj){

		if(obj!=null && obj.getControlLsReq()!=null && !obj.getControlLsReq().isEmpty()){
			return true;
		}

		return false;
	}

	public boolean hasListeNonCompilate(Procpratiche obj){

		if(obj!=null && obj.getControlLsReq()!=null && !obj.getControlLsReq().isEmpty()){
			for(ControlLsReq ls : obj.getControlLsReq()){
				if(!Boolean.TRUE.equals(ls.getCompiled())){
					return true;
				}
			}
			return false;
		}

		return true;
	}

	public void linkLists(List<ControlLsReq> list) throws PersistenceException{
		Procpratiche p = getEntity();
		if(p==null){
			return;
		}else if(list!=null && !list.isEmpty()){
			for(ControlLsReq ls : list){
				ls.setProcpratiche(p);
				ca.create(ls);
			}
		}
	}*/

	public void setDataScadenza(Protocollo prot) {
		try{
			Procpratiche proc = getEntity();

			if(prot != null && proc != null) {
				Date dataScadenza = prot.getDataScadenza();

				if (dataScadenza!=null) {
					proc.setDataScadenza(dataScadenza);
					return;
				}

				/* Se il campo data protocollo non è valorizzato, come riferimento ai fini del  
				 * calcolo della data di scadenza viene usata la data inserimento */
				Date data = prot.getDataProtocollo();
				if (data==null)
					data = prot.getData();

				if (data!=null){
					Calendar cal = Calendar.getInstance();
					cal.setTime(data);

					if (prot.getCode()!=null) {
						String typeCode = prot.getCode().getCode();
						org.hibernate.Query qry = ca.createHibernateQuery(ScadenzeList);

						long sdlId = prot.getServiceDeliveryLocation().getParent().getInternalId();

						qry.setParameter("sdlId", sdlId);
						qry.setParameter("typeCode", typeCode);

						@SuppressWarnings("unchecked")
						List<ScadenzaTipoCom> stcList = (List<ScadenzaTipoCom>)qry.list();

						int stcScore = 0;

						if (stcList.size()>0)
							stcScore = stcList.get(0).getScore();

						if (stcScore>0){
							cal.add(Calendar.DATE, stcScore); 
							proc.setDataScadenza(cal.getTime());
						} else {
							CodeValuePhi code = (CodeValuePhi)prot.getCode();
							if (code instanceof HibernateProxy)
								code = (CodeValuePhi)((HibernateProxy)code).getHibernateLazyInitializer().getImplementation();

							Integer score = code.getScore();

							if (score!=null && score>0) {
								cal.add(Calendar.DATE, score); 
								proc.setDataScadenza(cal.getTime());
							}
						}
					} 
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void updateDataScadenza(Sospensione sosp) {
		try{
			int days = 0;
			Procpratiche proc = getEntity();

			if(proc != null && proc.getDataScadenza()!=null && sosp != null && sosp.getDataInizio()!=null && sosp.getDataFine()!=null) {
				long ms = sosp.getDataFine().getTime() - sosp.getDataInizio().getTime();
				days = (int)(ms/86400000);	
			}

			if (days>0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(proc.getDataScadenza());
				cal.add(Calendar.DATE, days); 
				proc.setDataScadenza(cal.getTime());
			}


		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void readPraticheInScadenza(){
		try {
			UserBean ub = UserBean.instance();
			String loggedRole = ub.getRoleName();
			Long employeeId = ub.getEmployeeId();

			cleanUpTemporaryInScadenzaCalucated();

			Boolean terminiDiRisposta 	= (Boolean)getTemporary().get("terminiDiRisposta");
			Boolean miglioramenti 		= (Boolean)getTemporary().get("miglioramenti");
			Boolean provvedimenti 		= (Boolean)getTemporary().get("provvedimenti");

			//Controllo necessario perchè il check viene visualizzato solo per linea di lavoro Mdl
			Boolean followUp	 		= (Boolean)getTemporary().get("followUp");
			if (followUp==null)
				followUp=false;

			String  numero		 		= (String) getTemporary().get("number");

			Boolean searchByDate 		= (Boolean)getTemporary().get("searchByDate");
			Boolean includiScadute		= (Boolean)getTemporary().get("includiScadute");
			Date scadenzaFrom 			= (Date)getTemporary().get("scadenzaFrom");
			Date scadenzaTo 			= (Date)getTemporary().get("scadenzaTo");

			if (searchByDate && scadenzaTo==null){
				FacesErrorUtils.addErrorMessage("commError", "Inserire il filtro Scadenza al.", "Inserire il filtro Scadenza al.");
				return;
			}

			Date upperBound = nDaysForward(null,10);
			Date lowerBound = new Date();
			boolean singleDate =true;

			if (searchByDate != null && searchByDate && (includiScadute == null || !includiScadute )){
				singleDate = false;
				lowerBound = (scadenzaFrom != null ? scadenzaFrom : new Date());
				upperBound = scadenzaTo;
			} else if (searchByDate != null && searchByDate && includiScadute != null && includiScadute){
				singleDate = true;
				upperBound = scadenzaTo;
			}

			//Se l'utente non seleziona alcun check pulisco la conversation e non eseguo nessuna query
			if (!terminiDiRisposta && !miglioramenti && !provvedimenti){
				this.eject();
				this.ejectList();
				return;
			} 

			String qry = "SELECT DISTINCT prt FROM Procpratiche prt " + 
					"LEFT JOIN prt.serviceDeliveryLocation sdl " +
					"LEFT JOIN prt.medicinaLavoro mdl " +
					"LEFT JOIN prt.conclusioniMdl cnc " +
					"LEFT JOIN prt.attivita att " +
					"LEFT JOIN att.provvedimenti prv " +
					"LEFT JOIN prv.statusCode prvStatusCode ";

			if ("Operatore".equals(loggedRole))
				qry+= "LEFT JOIN prt.operatori op ";

			qry += "LEFT JOIN prv.disposizioni disp " +
					"LEFT JOIN att.miglioramenti migl " +
					"LEFT JOIN prv.articoli art " +
					"LEFT JOIN art.gruppo grp " +
					"WHERE ";

			//Filtro linea di lavoro
			String ldl = null;

			if (this.getEqual()!=null) {
				Object obj = this.getEqual().get("serviceDeliveryLocation.area");
				if (obj!=null)
					ldl = ((CodeValue)obj).getCode();		

				if (ldl !=null && ldl!="")
					qry += "sdl.area.code = '" + ldl + "' AND ";	
			}

			//Filtro numero pratica
			if (numero!=null && numero!="")
				qry += "lower(prt.numero) like '%" + numero.toLowerCase() + "%' AND ";	

			//Solo le pratiche in linee di lavoro abilitate 
			qry += "sdl.internalId IN (:sdlocs) AND ";

			//Per la linea di lavoro MdL escludi le pratiche in stato SOSPESO e ANNULLATO  
			if ("WORKMEDICINE".equals(ldl))
				qry += "prt.statusCode.code not IN ('suspended', 'nullified') AND ("; 
			else  
				//Per tutte le altre linee di lavoro escludi le pratiche in stato CHIUSO, SOSPESO e ANNULLATO 
				qry += "prt.statusCode.code not IN ('completed', 'suspended', 'nullified') AND ("; 

			//Solo le pratiche il cui esito dei provvedimenti non sia a stato ottemperato
			//qry += " prvEsito is null or prvEsito.code != 'complied' ) AND (";

			//Se l'utente è loggato con ruolo Operatore vedrà solo le pratiche in cui compare come OPERATORE, RDP o RFP
			if ("Operatore".equals(loggedRole))
				qry += "prt.rdp.internalId = " + employeeId + " OR prt.rfp.internalId = " + employeeId + " OR op.employee.internalId = " + employeeId + ") AND (";

			//Se il check followUp non è settato
			if (!followUp) {
				if ("WORKMEDICINE".equals(ldl))
					//Se è stata selezionata la linea Medicina del lavoro, limita la ricerca alle pratiche con sotto-tipo "3 Ricorso avverso giudizio medico competente" 
					qry += "mdl.type.code = '03') AND (";
				else 
					//Escludi le pratiche di tipo Medicina del lavoro
					qry += "sdl.area.code <> 'WORKMEDICINE') AND (";

				//Includi anche le pratiche di Medicina del lavoro con sotto-tipo "3 Ricorso avverso giudizio medico competente"
				//qry += "sdl.area.code <> 'WORKMEDICINE' OR mdl.type.code = '03') AND (";
				//qry += "mdl.internalId is null or mdl.type.code = '03') AND (";

				//Solo le pratiche i cui provvedimenti sono attivi (non rimossi)
				qry += "prv is null or (att.isActive is true AND prv.isActive is true AND prvStatusCode is not null AND prvStatusCode.code = 'active')) AND (";

				//Solo le pratiche i cui miglioramenti sono attivi
				qry += "migl is null or migl.isActive is true) AND (";

				//Solo le pratiche i cui miglioramenti non siano a stato ottemperato
				//qry += " miglEsito is null or miglEsito.code != 'complied' ) AND (";

				//Scadenza legata ai termini di risposta (prt.dataScadenza) <= Sysdate + 10gg
				if (terminiDiRisposta){
					if (singleDate)
						qry += "(prt.dataScadenza IS NOT NULL AND prt.dataScadenza <= :upperBound) OR ";
					else 
						qry += "(prt.dataScadenza IS NOT NULL AND prt.dataScadenza <= :upperBound AND prt.dataScadenza >= :lowerBound) OR ";
				}

				//Scadenze legate ai migliormaneti
				if (miglioramenti){
					//Data Scadenza (Miglioramenti.dataScadenza) <= Sysdate + 10gg se Data verifica (Miglioramenti.dataVerifica) è null   //FIXME AND migl.statusCode.code ?
					if (singleDate)
						qry += "(migl.dataVerifica IS NULL AND migl.dataScadenza IS NOT NULL AND migl.dataScadenza <= :upperBound ) OR ";
					else 
						qry += "(migl.dataVerifica IS NULL AND migl.dataScadenza IS NOT NULL AND migl.dataScadenza <= :upperBound AND  migl.dataScadenza >= :lowerBound ) OR ";
				}

				//Scadenze legate ai provvedimenti
				if (provvedimenti) {

					//Data Scadenza (Disposizioni.dataScadenza) <= Sysdate + 10gg se Data verifica (Disposizioni.dataVerifica) è null */
					if(singleDate)
						qry += 	"((disp.dataVerifica IS NULL AND disp.dataScadenza IS NOT NULL AND disp.dataScadenza <= :upperBound) OR ";
					else 
						qry += 	"((disp.dataVerifica IS NULL AND disp.dataScadenza IS NOT NULL AND disp.dataScadenza <= :upperBound AND disp.dataScadenza >= :lowerBound) OR ";

					//Scadenze legate ai provvedimenti di tipo Iter 758 ed Ex301Bis
					//Scadenza della verifica (scadenzaDellaVerifica) <= Sysdate + 10gg se data verifica (dataDellaVerifica) è null */
					if (singleDate)
						qry += "(grp.dataDellaVerifica IS NULL AND grp.scadenzaDellaVerifica IS NOT NULL AND grp.scadenzaDellaVerifica <= :upperBound) OR ";
					else 
						qry += "(grp.dataDellaVerifica IS NULL AND grp.scadenzaDellaVerifica IS NOT NULL AND grp.scadenzaDellaVerifica <= :upperBound AND grp.scadenzaDellaVerifica >= :lowerBound) OR ";

					//Data scadenza pagamento (dataScadenzaPagamentoNP) <= Sysdate + 10gg se data pagamento (dataPagamentoNP) è null */
					if (singleDate)
						qry += "(grp.dataPagamentoNP IS NULL AND grp.dataScadenzaPagamentoNP IS NOT NULL AND grp.dataScadenzaPagamentoNP <= :upperBound) OR ";
					else 
						qry += "(grp.dataPagamentoNP IS NULL AND grp.dataScadenzaPagamentoNP IS NOT NULL AND grp.dataScadenzaPagamentoNP <= :upperBound AND grp.dataScadenzaPagamentoNP >= :lowerBound) OR ";

					//* ??? * duplicato?
					//Data scadenza pagamento (dataScadenzaPagamentoNP) <= Sysdate + 10gg se data pagamento (dataPagamentoNP) è null */
					//"(grp.dataPagamentoNP IS NULL AND grp.dataScadenzaPagamentoNP IS NOT NULL AND grp.dataScadenzaPagamentoNP <= :upperBound) OR ";
					//

					//Data scadenza comunicazione al PM (dataScadenzaComPMNP) <= Sysdate + 10gg se Data comunicazione effettiva al PM (comunicazionePMNP) è null */
					if (singleDate)
						qry += "(grp.comunicazionePMNP IS NULL AND grp.dataScadenzaComPMNP IS NOT NULL AND grp.dataScadenzaComPMNP <= :upperBound) OR ";
					else
						qry += "(grp.comunicazionePMNP IS NULL AND grp.dataScadenzaComPMNP IS NOT NULL AND grp.dataScadenzaComPMNP <= :upperBound AND grp.dataScadenzaComPMNP >= :lowerBound) OR ";

					//Data scadenza comunicazione dell'inottemperanza al contravventore (scadComInottemperanza) <= Sysdate + 10gg 
					//se Data invio comunicazione al contravventore (comunicazioneInottemperanza) è null */
					if (singleDate)
						qry += "(grp.comunicazioneInottemperanza IS NULL AND grp.scadComInottemperanza IS NOT NULL AND grp.scadComInottemperanza <= :upperBound) OR ";
					else
						qry += "(grp.comunicazioneInottemperanza IS NULL AND grp.scadComInottemperanza IS NOT NULL AND grp.scadComInottemperanza <= :upperBound AND grp.scadComInottemperanza >= :lowerBound) OR ";

					//Data scadenza comunicazione dell'inottemperanza al PM (scadComInottemperanzaPM) <= Sysdate + 10gg 
					//se Data invio comunicazione al PM (comunicazioneInottemperanzaPM) è null */
					if (singleDate)
						qry += "(grp.comunicazioneInottemperanzaPM IS NULL AND grp.scadComInottemperanzaPM IS NOT NULL AND grp.scadComInottemperanzaPM <= :upperBound) OR ";
					else
						qry += "(grp.comunicazioneInottemperanzaPM IS NULL AND grp.scadComInottemperanzaPM IS NOT NULL AND grp.scadComInottemperanzaPM <= :upperBound AND grp.scadComInottemperanzaPM >= :lowerBound) OR ";


					//Data scadenza pagamento (dataScadenzaPagamento) <= Sysdate + 10gg se data pagamento (dataPagamento) è null */
					if (singleDate)
						qry += "(grp.dataPagamento IS NULL AND grp.dataScadenzaPagamento IS NOT NULL AND grp.dataScadenzaPagamento <= :upperBound) OR ";
					else 
						qry += "(grp.dataPagamento IS NULL AND grp.dataScadenzaPagamento IS NOT NULL AND grp.dataScadenzaPagamento <= :upperBound AND grp.dataScadenzaPagamento >= :lowerBound) OR ";

					//Data scadenza comunicazione al PM (scadenzaComPM) <= Sysdate + 10gg se Data comunicazione effettiva al PM (comunicazionePM) è null */
					if (singleDate) 
						qry += "(grp.comunicazionePM IS NULL AND grp.scadenzaComPM IS NOT NULL AND grp.scadenzaComPM <= :upperBound) OR ";
					else 
						qry += "(grp.comunicazionePM IS NULL AND grp.scadenzaComPM IS NOT NULL AND grp.scadenzaComPM <= :upperBound AND grp.scadenzaComPM >= :lowerBound) OR ";

					//Data scadenza comunicazione dell'ottemperanza con modi diversi al contravventore (scadOttModiDiversi) <= Sysdate + 10gg 
					//se Data invio comunicazione al contravventore (ottemperanzaModiDiversi) è null */
					if(singleDate)
						qry += "(grp.ottemperanzaModiDiversi IS NULL AND grp.scadOttModiDiversi IS NOT NULL AND grp.scadOttModiDiversi <= :upperBound) OR ";
					else
						qry += "(grp.ottemperanzaModiDiversi IS NULL AND grp.scadOttModiDiversi IS NOT NULL AND grp.scadOttModiDiversi <= :upperBound AND grp.scadOttModiDiversi >= :lowerBound) OR ";

					//Data scadenza comunicazione dell'ottemperanza con modi diversi al PM (scadOttModiDiversiPM) <= Sysdate + 10gg 
					//se Data invio comunicazione al PM (ottemperanzaModiDiversiPM) è null */
					if  (singleDate)
						qry += "(grp.ottemperanzaModiDiversiPM IS NULL AND grp.scadOttModiDiversiPM IS NOT NULL AND grp.scadOttModiDiversiPM <= :upperBound))";
					else
						qry += "(grp.ottemperanzaModiDiversiPM IS NULL AND grp.scadOttModiDiversiPM IS NOT NULL AND grp.scadOttModiDiversiPM <= :upperBound AND grp.scadOttModiDiversiPM >= :lowerBound))";
				}
			} else {//Limita la ricerca alle pratiche di medicina del lavoro con follow-up in scadenza

				qry += "mdl.internalId IS NOT NULL AND mdl.type.code <> '03') AND (";
				/* Scadenza legata al follow-up di Valutazioni conclusive ed esito (linea di lavoro Medicina del lavoro)
			   	   (cnc.uscitaFollowUp == null && lowerBound =< cnc.controllo <= upperBound */
				if (singleDate)
					qry += "(cnc.uscitaFollowUp IS NULL AND cnc.controllo IS NOT NULL AND cnc.controllo <= :upperBound) OR ";
				else 
					qry += "(cnc.uscitaFollowUp IS NULL AND cnc.controllo IS NOT NULL AND cnc.controllo <= :upperBound AND cnc.controllo >= :lowerBound) OR ";
			}

			if (qry.endsWith(" OR "))
				qry = qry.substring(0, qry.length() - 4) + ")";
			else 
				qry = qry + ")";

			Query queryPraticheInScadenzaCount = ca.createQuery(qry.replace("DISTINCT prt", "COUNT(DISTINCT prt)"));
			queryPraticheInScadenzaCount.setParameter("sdlocs", ub.getSdLocs());
			queryPraticheInScadenzaCount.setParameter("upperBound", upperBound);
			//queryPraticheInScadenza.setParameter("sdl", "WORKMEDICINE");

			if (!singleDate){
				queryPraticheInScadenzaCount.setParameter("lowerBound", lowerBound);
			}

			Query queryPraticheInScadenza = ca.createQuery(qry);
			queryPraticheInScadenza.setParameter("sdlocs", ub.getSdLocs());
			queryPraticheInScadenza.setParameter("upperBound", upperBound);
			//queryPraticheInScadenza.setParameter("sdl", "WORKMEDICINE");

			if (!singleDate){
				queryPraticheInScadenza.setParameter("lowerBound", lowerBound);
			}


			Long countResult = (Long) queryPraticheInScadenzaCount.getSingleResult();
			getTemporary().put("rowCount", countResult);

			@SuppressWarnings("unchecked")
			List<Procpratiche> lst = queryPraticheInScadenza.getResultList();

			if(lst!=null && !lst.isEmpty()){
				ProcpraticheAction pAction = ProcpraticheAction.instance();
				pAction.injectList(lst);
			} else {
				this.eject();
				this.ejectList();
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}


	private void cleanUpTemporaryInScadenzaCalucated() { 
		Iterator it = getTemporary().keySet().iterator();
		while( it.hasNext()) {
			String key = (String)it.next();
			if (key.startsWith("PraticheOcchietti")){
				it.remove();
			}
		}
	}

	public void inScadenza (Procpratiche p ){

		Boolean [] ret = new Boolean[]{false, false, false};    //occhietti [generale, provvedimenti, miglioramenti] 

		if (p== null) {
			return;
		}

		ret [0] = inScadenzaGen(p);
		Boolean[] ret23 = inScadenzaProvvOrMigl(p);
		ret [1] = ret23 [0];
		ret [2] = ret23 [1];

		p.setInScadenzaT(ret [0]);
		p.setInScadenzaP(ret [1]);
		p.setInScadenzaM(ret [2]);
	}

	public Boolean inScadenzaGen (Procpratiche p) {

		Date tenDaysInFuture = nDaysForward(null,10);

		if(p!= null && p.getDataScadenza() != null && p.getDataScadenza().before(tenDaysInFuture)) {
			return true;
		}
		else {
			return false;
		}

	}

	public Boolean[] inScadenzaProvvOrMigl (Procpratiche pratica) {

		Boolean [] ret = new Boolean[]{false,false};  //occhietto [provvedimenti, miglioramenti] 
		if (pratica == null) {
			return ret;
		}

		List<Provvedimenti> provvedimentiAll = pratica.getProvvedimenti();
		List<Attivita> attivita = pratica.getAttivita();

		if(attivita!=null && !attivita.isEmpty()){
			for (Attivita att : attivita){
				if(!att.getIsActive())
					continue;

				if (ret[0] && ret[1]){
					break;  
				}

				List<Provvedimenti> provvedimenti = att.getProvvedimenti();
				provvedimentiAll.removeAll(provvedimenti);

				if (provvedimenti != null && !provvedimenti.isEmpty()){
					for (Provvedimenti p : provvedimenti) {

						boolean scadutoProvv = checkScadenzaProvvedimento(p);
						if (scadutoProvv) {
							ret[0] = true;
							break;  
						}

					} //for provvedimenti
				}

				List<Miglioramenti> miglioramenti = att.getMiglioramenti();
				if (miglioramenti !=null && !miglioramenti.isEmpty()) { 
					for (Miglioramenti m : miglioramenti){

						boolean miglioramentoScaduto = checkScadenzaMiglioramento(m);
						if (miglioramentoScaduto){
							ret[1] = true;
							break;  
						}
					}
				}
			}
		}

		//----------------------------------------------
		//FIXME: bug on link between ProcPratica > Attivita / Attivita > Provvedimenti / ProcPratica > Provvedimenti
		// 			if an expired provision was found, skip this check
		//----------------------------------------------

		if (provvedimentiAll.size() > 0 && !Boolean.TRUE.equals(ret[0])) {
			log.warn("Pratica internalId"+pratica.getInternalId()+"numero:"+ pratica.getNumero()+ " wrong attachment attivita/provvedimenti: attivita>");
			for (Provvedimenti p : provvedimentiAll) {

				boolean scadutoProvv = checkScadenzaProvvedimento(p);
				if (scadutoProvv) {
					ret[0] = true;
					break;  
				}

			} //for provvedimenti

		}
		//----------------------------------------------

		return ret;
	}

	public boolean checkScadenzaMiglioramento (Miglioramenti m) {
		if (m.getDataVerifica() == null && m.getDataScadenza() != null && m.getDataScadenza().before(nDaysForward(null, 10))){
			return true;
		}
		return false;
	}


	public boolean checkScadenzaProvvedimento (Provvedimenti p) {

		if (p.getStatusCode() != null && p.getStatusCode().getCode().equals("active") && p.getIsActive() ) {

			List<Articoli> articoli = p.getArticoli();
			if (articoli != null && !articoli.isEmpty()){

				List<Long> groupChecked = new ArrayList<Long>();

				for (Articoli art : articoli) {
					Gruppi g = art.getGruppo();
					if (g != null && !groupChecked.contains(g.getInternalId())) {
						boolean scaduto = checkScadenzeGroup(g);
						if (scaduto) {
							return true;
						}
					}
				}

			} //if Articoli

			List <Disposizioni> disps = p.getDisposizioni();
			if (disps != null && !disps.isEmpty()){
				for (Disposizioni disp : disps){
					boolean scaduta = checkScadenzaDisp(disp);
					if (scaduta) {
						return true;
					}

				}
			}

		}  //if provvedimento attivo
		return false;
	}

	public boolean checkScadenzaDisp (Disposizioni disp) {
		if (disp == null)
			return false;

		Date tenDaysInFuture = nDaysForward(null, 10);
		if (disp.getDataVerifica() == null && disp.getDataScadenza() != null && disp.getDataScadenza().before(tenDaysInFuture)){
			return true;
		}

		return false;
	}

	public boolean checkScadenzeGroup(Gruppi g) {

		Date lowerBound = nDaysForward(null, 10);

		Date dataDellaVerifica = g.getDataDellaVerifica();
		Date scadenzaDellaVerifica = g.getScadenzaDellaVerifica();

		Date dataPagamentoNP = g.getDataPagamentoNP();
		Date dataScadenzaPagamentoNP = g.getDataScadenzaPagamentoNP();

		Date comunicazionePMNP = g.getComunicazionePMNP();
		Date dataScadenzaComPMNP = g.getDataScadenzaComPMNP();

		Date comunicazioneInottemperanza = g.getComunicazioneInottemperanza();
		Date scadComInottemperanza = g.getScadComInottemperanza();

		Date comunicazioneInottemperanzaPM = g.getComunicazioneInottemperanzaPM();
		Date scadComInottemperanzaPM = g.getScadComInottemperanzaPM();

		Date dataPagamento = g.getDataPagamento();
		Date dataScadenzaPagamento = g.getDataScadenzaPagamento();

		Date comunicazionePM = g.getComunicazionePM();
		Date scadenzaComPM = g.getScadenzaComPM();

		Date ottemperanzaModiDiversi = g.getOttemperanzaModiDiversi();
		Date scadOttModiDiversi = g.getScadOttModiDiversi();

		Date ottemperanzaModiDiversiPM = g.getOttemperanzaModiDiversiPM();
		Date scadOttModiDiversiPM = g.getScadOttModiDiversiPM();


		if (dataDellaVerifica == null && scadenzaDellaVerifica != null && scadenzaDellaVerifica.before(lowerBound)){
			return true;
		}

		if (dataPagamentoNP == null && dataScadenzaPagamentoNP != null && dataScadenzaPagamentoNP.before(lowerBound)){
			return true;
		}

		if (comunicazionePMNP == null && dataScadenzaComPMNP != null && dataScadenzaComPMNP.before(lowerBound)){
			return true;
		}

		if ( comunicazioneInottemperanza == null && scadComInottemperanza != null && scadComInottemperanza.before(lowerBound)){
			return true;
		}

		if ( comunicazioneInottemperanzaPM == null && scadComInottemperanzaPM != null && scadComInottemperanzaPM.before(lowerBound)){
			return true;
		}

		if ( dataPagamento == null && dataScadenzaPagamento != null && dataScadenzaPagamento.before(lowerBound)){
			return true;
		}

		if ( comunicazionePM == null && scadenzaComPM != null && scadenzaComPM.before(lowerBound)){
			return true;
		}

		if ( ottemperanzaModiDiversi == null && scadOttModiDiversi != null && scadOttModiDiversi.before(lowerBound)){
			return true;
		}

		if ( ottemperanzaModiDiversiPM == null && scadOttModiDiversiPM != null && scadOttModiDiversiPM.before(lowerBound)){
			return true;
		}

		return false;
	}


	public Date lowerBownd (){
		return new Date();
	}

	public Date nDaysForward(Date d, Integer n){
		if (d == null){
			d = new Date();
		}
		if (n==null || n<1){
			n = 10;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, n); 
		Date daysInFuture = cal.getTime();
		return daysInFuture;
	}

	public void readForProvvedimenti(PersoneGiuridiche pg){
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String query = "select distinct proc from Procpratiche proc " +
				"join proc.attivita a " +
				"join a.provvedimenti prov " +
				"join prov.soggetto s " +
				"join proc.serviceDeliveryLocation sdloc " +
				"where s.ditta = :ditta " + 
				"and a.isActive = true " + 
				"and prov.isActive = true " +
				"and sdloc.internalId in (:sdlocIds)";

		Query q = ca.createQuery(query);
		q.setParameter("ditta", pg);
		q.setParameter("sdlocIds", UserBean.instance().getSdLocs());
		List<Procpratiche> praticheList = q.getResultList();

		injectList(praticheList);
	}

	public String toSetValutazioneConclusivaMdl(VisitaMdl visita){
		Date conclusioneVisita = null;

		try {
			if (visita!=null && visita.getConclusioniMdl()!=null)
				conclusioneVisita = visita.getConclusioniMdl().getConclusioneVisita();

			//Se si sta salvando una visita priva di "data conclusione visita" - non fare niente
			if (conclusioneVisita==null)
				return "doNothing";

			Procpratiche proc = getEntity();
			ValutazioneConclusivaMdl valutazioneConclusivaMdl = null;
			Date conclusione = null;

			if(proc!=null){
				MedicinaLavoro medicinaLavoro = proc.getMedicinaLavoro();

				/* Se NON si tratta di una pratica di Medicina del Lavoro di tipo:
				 *  01 (Visita Medica e accertamenti)
				 *  02 (Visita di Medicona del Lavoro per iniziativa di Vigilanza)
				 *  04 (Sorveglianza sanitaria Ex esposti) 
				 *  05 (Visite di idoneità al lavoro) 
				 *  
				 *  non fare niente */
				if (medicinaLavoro==null || medicinaLavoro.getType()==null || "03".equals(medicinaLavoro.getType().getCode()))
					return "doNothing";

				//Recupera valutazioneConclusivaMdl dalla pratica
				valutazioneConclusivaMdl = proc.getConclusioniMdl();			
			}

			/* Se nella pratica non è ancora stata definita una valutazioneConclusivaMdl
			   creala e agganciala alla pratica */ 
			if (valutazioneConclusivaMdl==null)
				return "toCreate";

			//Recupera la data conclusione di valutazioneConclusivaMdl (Pratica)
			conclusione = valutazioneConclusivaMdl.getConclusioneVisita();

			//			/* Se la data conclusione di valutazioneConclusivaMdl (Pratica) non è stata impostata
			//			   chiedi all'utente se vuole sovrascrivere le conclusioni della pratica con le conclusioni della visita attuale */
			//			if (conclusione==null)
			//				return "toUpdatePopUp1";
			//
			//			/* Se la data conclusione di valutazioneConclusivaMdl (Pratica) <= della data conclusione della visita attuale
			//			   chiedi all'utente se vuole sovrascrivere le conclusioni della pratica con le conclusioni della visita */
			//			if (conclusione.equals(conclusioneVisita) || conclusione.before(conclusioneVisita))
			//				return "toUpdatePopUp2";
			//
			//			/* Se la data conclusione di valutazioneConclusivaMdl (Pratica) > della data conclusione visita
			//			   non fare niente */
			//			if (conclusione.after(conclusioneVisita))
			//				return "doNothing";
			//			richiesta di Luisa H00....
			if (conclusione==null){
				return "toUpdatePopUp1";
			}else{
				return "toUpdatePopUp2";
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

		//		richiesta di Luisa H00....
		//		return "doNothing";
	}

	/* Crea e setta le valutazioni conclusive della pratica (ValutazioneConclusivaMdl) utilizzando i dati della visita (VisitaMdl) */
	public void setValutazioneConclusivaMdl(String toDo, VisitaMdl visita){
		try {
			if (toDo==null || "".equals(toDo) || visita==null || visita.getConclusioniMdl()==null)
				return;

			ConclusioniMdl conclusioniVisita = visita.getConclusioniMdl();
			if (conclusioniVisita==null)
				return;

			//Crea una nuova valutazione conclusiva (ValutazioneConclusivaMdl) da agganciare alla pratica
			if ("toCreate".equals(toDo) || "toUpdate".equals(toDo)) {
				ValutazioneConclusivaMdl valutazioneConclusivaMdl = new ValutazioneConclusivaMdl();

				valutazioneConclusivaMdl.setDiagMdl(new ArrayList<DiagMdl>());
				valutazioneConclusivaMdl.setIsActive(true);

				//Recupera le diagnosi della visita
				List<DiagMdl> diagList = conclusioniVisita.getDiagMdl();
				if (diagList!=null && diagList.size()>0) {
					DiagMdlAction diagMdlAction = DiagMdlAction.instance(); 

					for(DiagMdl diag : diagList){
						DiagMdl newDiag = diagMdlAction.copy(diag);

						if (newDiag!=null){
							newDiag.setValutazioneConclusivaMdl(valutazioneConclusivaMdl);
							valutazioneConclusivaMdl.getDiagMdl().add(newDiag);
						}
					}
				}

				//Copia i dati comuni alla visita
				valutazioneConclusivaMdl.setDiagnosiTxt(conclusioniVisita.getDiagnosiTxt());
				valutazioneConclusivaMdl.setIdoneitaMdl(conclusioniVisita.getIdoneitaMdl());
				valutazioneConclusivaMdl.setConclusioneVisita(conclusioniVisita.getConclusioneVisita());
				valutazioneConclusivaMdl.setControllo(conclusioniVisita.getControllo());
				valutazioneConclusivaMdl.setGgControlli(conclusioniVisita.getGgControlli());
				valutazioneConclusivaMdl.setUscitaFollowUp(conclusioniVisita.getUscitaFollowUp());
				valutazioneConclusivaMdl.setUscitaFollowUpTxt(conclusioniVisita.getUscitaFollowUpTxt());

				//Aggancia alla pratica la nuova valutazione conclusiva
				this.link("conclusioniMdl", valutazioneConclusivaMdl);
				this.create();
				ValutazioneConclusivaMdlAction.instance().inject(valutazioneConclusivaMdl);
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public void cleanFiltersMittente(){
		//		
		//		getLike().remove("praticheRiferimenti.richiedenteUtente.name.fam");
		//	    getLike().remove("praticheRiferimenti.richiedenteUtente.name.giv");
		//	    getLike().remove("praticheRiferimenti.richiedenteUtente.birthTime");
		//	    getLike().remove("praticheRiferimenti.richiedenteUtente.fiscalCode");
		//	    getLike().remove("praticheRiferimenti.richiedenteUtente.birthPlace.cty");
		//	    
		//	    getLike().remove("praticheRiferimenti.richiedenteMedico.name.fam");
		//	    getLike().remove("praticheRiferimenti.richiedenteMedico.name.giv");
		//	    getLike().remove("praticheRiferimenti.richiedenteMedico.code.code");
		//	    
		//	    getLike().remove("praticheRiferimenti.richiedenteInterno.name.fam");
		//	    getLike().remove("praticheRiferimenti.richiedenteInterno.name.giv");
		//	    getLike().remove("praticheRiferimenti.richiedenteInterno.fiscalCode");
		//	    
		//	    getLike().remove("praticheRiferimenti.richiedenteDitta.patritaIva");
		//	    getLike().remove("praticheRiferimenti.richiedenteDitta.codiceFiscale");
		//	    getLike().remove("praticheRiferimenti.richiedenteDitta.denominazione");
		temporary.remove("mittenteUtenteNameFam");
		temporary.remove("mittenteUtenteNameGiv");
		temporary.remove("mittenteUtenteBirthDate");
		temporary.remove("mittenteUtenteFc");
		temporary.remove("mittenteUtenteCty");

		temporary.remove("mittenteMedicoNameFam");
		temporary.remove("mittenteMedicoNameGiv");
		temporary.remove("mittenteMedicoRc");

		temporary.remove("mittenteUserNameFam");
		temporary.remove("mittenteUserNameGiv");
		temporary.remove("mittenteUserNameCode");

		temporary.remove("mittenteDittaPi");
		temporary.remove("mittenteDittaFc");
		temporary.remove("mittenteDittaNameDenom");  

		//clean  utente
		removeExpression("this", "ProtocolloRichiedente.code");
		removeExpression("this", "ProtocolloMultiRichiedente.code");

		removeExpression("this", "ProtocolloPerson.name.fam");
		removeExpression("this", "ProtocolloMultiPerson.name.fam");
		removeExpression("this", "ProtocolloPerson.name.giv");
		removeExpression("this", "ProtocolloMultiPerson.name.giv");
		removeExpression("this", "ProtocolloPerson.birthTime");
		removeExpression("this", "ProtocolloMultiPerson.birthTime");
		removeExpression("this", "ProtocolloPerson.fiscalCode");
		removeExpression("this", "ProtocolloMultiPerson.fiscalCode");
		removeExpression("this", "ProtocolloPerson.birthPlace.cty");
		removeExpression("this", "ProtocolloMultiPerson.birthPlace.cty");

		//clean  medico
		removeExpression("this", "ProtocolloMedico.name.fam");
		removeExpression("this", "ProtocolloMultiMedico.name.fam");
		removeExpression("this", "ProtocolloMedico.name.giv");
		removeExpression("this", "ProtocolloMultiMedico.name.giv");
		removeExpression("this", "ProtocolloMedico.regionalCode");
		removeExpression("this", "ProtocolloMultiMedico.regionalCode");

		//clean  interno
		removeExpression("this", "ProtocolloInterno.name.fam");
		removeExpression("this", "ProtocolloMultiInterno.name.fam");
		removeExpression("this", "ProtocolloInterno.name.giv");
		removeExpression("this", "ProtocolloMultiInterno.name.giv");
		removeExpression("this", "ProtocolloInterno.fiscalCode");
		removeExpression("this", "ProtocolloMultiInterno.fiscalCode");

		//clean ditta
		removeExpression("this", "ProtocolloPersoneGiuridiche.patritaIva");
		removeExpression("this", "ProtocolloMultiPersoneGiuridiche.patritaIva");
		removeExpression("this", "ProtocolloPersoneGiuridiche.codiceFiscale");
		removeExpression("this", "ProtocolloMultiPersoneGiuridiche.codiceFiscale");
		removeExpression("this", "ProtocolloPersoneGiuridiche.denominazione");
		removeExpression("this", "ProtocolloMultiPersoneGiuridiche.denominazione");

		// clean sub criteria

		removeSubCriteria(entityCriteria, "ProtocolloRichiedente");
		removeSubCriteria(entityCriteria, "ProtocolloPerson");
		removeSubCriteria(entityCriteria, "ProtocolloPersoneGiuridiche");
		removeSubCriteria(entityCriteria, "ProtocolloMedico");
		removeSubCriteria(entityCriteria, "ProtocolloInterno");

		removeSubCriteria(entityCriteria, "ProtocolloMultiRichiedente");
		removeSubCriteria(entityCriteria, "ProtocolloMultiPerson");
		removeSubCriteria(entityCriteria, "ProtocolloMultiPersoneGiuridiche");
		removeSubCriteria(entityCriteria, "ProtocolloMultiMedico");
		removeSubCriteria(entityCriteria, "ProtocolloMultiInterno");


	}

	public void cleanFiltersRiferito(){

		getTemporary().remove("personeAssociazioni.person.birthPlace.cty");
		getTemporary().remove("personeAssociazioni.person.fiscalCode");
		getTemporary().remove("personeAssociazioni.person.birthTime");
		getTemporary().remove("personeAssociazioni.person.name.giv");
		getTemporary().remove("personeAssociazioni.person.name.fam");

		removeExpression("this", "PersoneAssociazioniPerson.birthPlace.cty");
		removeExpression("this", "PersoneAssociazioniPerson.fiscalCode");
		removeExpression("this", "PersoneAssociazioniPerson.birthTime");
		removeExpression("this", "PersoneAssociazioniPerson.name.giv");
		removeExpression("this", "PersoneAssociazioniPerson.name.fam");
		removeExpression("this", "PersoneAssociazioniSediPersone.birthPlace.cty");
		removeExpression("this", "PersoneAssociazioniSediPersone.fiscalCode");
		removeExpression("this", "PersoneAssociazioniSediPersone.birthTime");
		removeExpression("this", "PersoneAssociazioniSediPersone.name.giv");
		removeExpression("this", "PersoneAssociazioniSediPersone.name.fam");

		getLike().remove("ditteAssociazioni.personeGiuridiche.denominazione");
		getLike().remove("ditteAssociazioni.personeGiuridiche.codiceFiscale");
		getLike().remove("ditteAssociazioni.personeGiuridiche.patritaIva");

		getLike().remove("cantieriAssociazioni.cantiere.addr.str");
		getLike().remove("cantieriAssociazioni.cantiere.naturaOpera");

		removeSubCriteria(entityCriteria, "PersoneAssociazioniPerson");
		removeSubCriteria(entityCriteria, "PersoneAssociazioniSediPersone");
		removeSubCriteria(entityCriteria, "PersoneAssociazioni");
	}

	public void cleanFiltersUbicazione(){

		getLike().remove("praticheRiferimenti.ubicazioneDitta.patritaIva");
		getLike().remove("praticheRiferimenti.ubicazioneDitta.codiceFiscale");
		getLike().remove("praticheRiferimenti.ubicazioneDitta.denominazione");

		getLike().remove("praticheRiferimenti.ubicazioneCantiere.naturaOpera");
		getLike().remove("praticheRiferimenti.ubicazioneCantiere.addr.str");

		getLike().remove("praticheRiferimenti.ubicazioneAddr.str");
		getLike().remove("praticheRiferimenti.ubicazioneX");
		getLike().remove("praticheRiferimenti.ubicazioneY");

	}

	public void filterByProt() {
		try {

			String type=null;
			//mittente della richiesta di comunicazione
			if(temporary.get("richiedente")!=null)
				type=(String) ((CodeValue) temporary.get("richiedente")).getCode();

			if (type==null || type.isEmpty() ){
				return;
			}
			// utente
			String u1=null;
			String u2=null;
			Date u3=null;
			String u4=null;
			String u5=null;

			//medico
			String m1=null;
			String m2=null;
			String m3=null;
			//interno
			String i1=null;
			String i2=null;
			String i3=null;
			//ditta
			String d1=null;
			String d2=null;
			String d3=null;
			String d4=null;

			//get every temporary from filter in form
			if (type!=null){
				if ("Utente".equals(type)){
					u1=(String)temporary.get("mittenteUtenteNameFam");
					u2=(String)temporary.get("mittenteUtenteNameGiv");
					u3=(Date)temporary.get("mittenteUtenteBirthDate");
					u4=(String)temporary.get("mittenteUtenteFc");
					u5=(String)temporary.get("mittenteUtenteCty");
				}
				if ("Medico".equals(type)){
					m1=(String)temporary.get("mittenteMedicoNameFam");
					m2=(String)temporary.get("mittenteMedicoNameGiv");
					m3=(String)temporary.get("mittenteMedicoRc");

				}
				if ("Interno".equals(type)){
					i1=(String)temporary.get("mittenteUserNameFam");
					i2=(String)temporary.get("mittenteUserNameGiv");
					i3=(String)temporary.get("mittenteUserNameCode");


				}

				if ("Ditta".equals(type)){
					d1=(String)temporary.get("mittenteDittaPi");
					d2=(String)temporary.get("mittenteDittaFc");
					d3=(String)temporary.get("mittenteDittaNameDenom");
					d4=(String)temporary.get("mittenteDittaCodDitta");

				}

			}

			if(entityCriteria == null) {
				entityCriteria = ca.createCriteria(entityClass);
			}

			/*
			 * mettere tutti remove expretion e remove subCriteria
			 */
			removeExpression("this", "ProtocolloRichiedente.code");
			removeExpression("this", "ProtocolloMultiRichiedente.code");

			removeExpression("this", "ProtocolloPerson.name.fam");
			removeExpression("this", "ProtocolloMultiPerson.name.fam");
			removeExpression("this", "ProtocolloPerson.name.giv");
			removeExpression("this", "ProtocolloMultiPerson.name.giv");
			removeExpression("this", "ProtocolloPerson.birthTime");
			removeExpression("this", "ProtocolloMultiPerson.birthTime");
			removeExpression("this", "ProtocolloPerson.fiscalCode");
			removeExpression("this", "ProtocolloMultiPerson.fiscalCode");
			removeExpression("this", "ProtocolloPerson.birthPlace.cty");
			removeExpression("this", "ProtocolloMultiPerson.birthPlace.cty");

			removeExpression("this", "ProtocolloMedico.name.fam");
			removeExpression("this", "ProtocolloMultiMedico.name.fam");
			removeExpression("this", "ProtocolloMedico.name.giv");
			removeExpression("this", "ProtocolloMultiMedico.name.giv");
			removeExpression("this", "ProtocolloMedico.regionalCode");
			removeExpression("this", "ProtocolloMultiMedico.regionalCode");

			removeExpression("this", "ProtocolloInterno.name.fam");
			removeExpression("this", "ProtocolloMultiInterno.name.fam");
			removeExpression("this", "ProtocolloInterno.name.giv");
			removeExpression("this", "ProtocolloMultiInterno.name.giv");
			removeExpression("this", "ProtocolloInterno.fiscalCode");
			removeExpression("this", "ProtocolloMultiInterno.fiscalCode");

			removeExpression("this", "ProtocolloPersoneGiuridiche.patritaIva");
			removeExpression("this", "ProtocolloMultiPersoneGiuridiche.patritaIva");
			removeExpression("this", "ProtocolloPersoneGiuridiche.codiceFiscale");
			removeExpression("this", "ProtocolloMultiPersoneGiuridiche.codiceFiscale");
			removeExpression("this", "ProtocolloPersoneGiuridiche.denominazione");
			removeExpression("this", "ProtocolloMultiPersoneGiuridiche.denominazione");
			removeExpression("this", "ProtocolloPersoneGiuridiche.codiceDitta");
			removeExpression("this", "ProtocolloMultiPersoneGiuridiche.codiceDitta");

			removeSubCriteria(entityCriteria, "ProtocolloRichiedente");
			removeSubCriteria(entityCriteria, "ProtocolloPerson");
			removeSubCriteria(entityCriteria, "ProtocolloPersoneGiuridiche");
			removeSubCriteria(entityCriteria, "ProtocolloMedico");
			removeSubCriteria(entityCriteria, "ProtocolloInterno");

			removeSubCriteria(entityCriteria, "ProtocolloMultiRichiedente");
			removeSubCriteria(entityCriteria, "ProtocolloMultiPerson");
			removeSubCriteria(entityCriteria, "ProtocolloMultiPersoneGiuridiche");
			removeSubCriteria(entityCriteria, "ProtocolloMultiMedico");
			removeSubCriteria(entityCriteria, "ProtocolloMultiInterno");


			//create criteria for protocollo
			Criteria procProtCrit = findSubCriteria("Protocollo");
			if(procProtCrit == null) {
				procProtCrit =  entityCriteria.createCriteria("protocollo", "Protocollo",Criteria.LEFT_JOIN);
			}
			Criteria procProtRichiedenteCrit = findSubCriteria("ProtocolloRichiedente");
			if(procProtRichiedenteCrit == null) {
				procProtRichiedenteCrit =  procProtCrit.createCriteria("richiedente", "ProtocolloRichiedente",Criteria.LEFT_JOIN);
			}

			//create criteria for protocolloMulti
			Criteria procProtMultiCrit = findSubCriteria("ProtocolloMulti");
			if(procProtMultiCrit == null) {
				procProtMultiCrit =   entityCriteria.createCriteria("protocolloMulti", "ProtocolloMulti",Criteria.LEFT_JOIN);
			}
			Criteria procProtMultiRichiedenteCrit = findSubCriteria("ProtocolloMultiRichiedente");
			if(procProtMultiRichiedenteCrit == null) {
				procProtMultiRichiedenteCrit =  procProtMultiCrit.createCriteria("richiedente", "ProtocolloMultiRichiedente",Criteria.LEFT_JOIN);
			}

			//create criterion for protocollo e protocolloMulti and set them in different lists for last 	LogicalExpression 
			Criterion c0 = null;
			Criterion cm0 = null;
			//criterion for protocollo
			Criterion c1 = null;
			Criterion c2 = null;
			Criterion c3 = null;
			Criterion c4 = null;
			Criterion c5 = null;
			//criterion for protocolloMulti
			Criterion cm1 = null;
			Criterion cm2 = null;
			Criterion cm3 = null;
			Criterion cm4 = null;
			Criterion cm5 = null;
			//multi criterion for protocollo
			Criterion cx1 = null;
			//multi criterion for protocolloMulti
			Criterion cx2 = null;

			if ("Utente".equals(type)){
				//utente
				Criteria procProtPersonCrit = findSubCriteria("ProtocolloPerson");
				if(procProtPersonCrit == null) {
					procProtPersonCrit = procProtCrit.createCriteria("richiedenteUtente", "ProtocolloPerson",Criteria.LEFT_JOIN);
				}
				Criteria procProtMultiPersonCrit = findSubCriteria("ProtocolloMultiPerson");
				if(procProtMultiPersonCrit == null) {
					procProtMultiPersonCrit = procProtMultiCrit.createCriteria("richiedenteUtente", "ProtocolloMultiPerson",Criteria.LEFT_JOIN);
				}

				if((u1==null || u1.trim().isEmpty()) && (u2==null || u2.trim().isEmpty()) && (u3==null) && (u4==null || u4.trim().isEmpty()) && (u5==null || u5.trim().isEmpty())){
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();

				} else{
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();
					if(u1!=null && !u1.trim().isEmpty()){
						u1 = "%" + u1 + "%";

						c1 = Restrictions.like("ProtocolloPerson.name.fam", u1).ignoreCase();
						cm1 = Restrictions.like("ProtocolloMultiPerson.name.fam", u1).ignoreCase();
					}
					if(u2!=null  && !u2.trim().isEmpty()){
						u2 = "%" + u2 + "%";

						c2 = Restrictions.like("ProtocolloPerson.name.giv", u2).ignoreCase();
						cm2 = Restrictions.like("ProtocolloMultiPerson.name.giv", u2).ignoreCase();
					}
					if(u3!=null){

						c3 = Restrictions.eq("ProtocolloPerson.birthTime", u3);
						cm3 = Restrictions.eq("ProtocolloMultiPerson.birthTime", u3);
					}
					if(u4!=null && !u4.trim().isEmpty()){
						u4 = "%" + u4 + "%";

						c4 = Restrictions.like("ProtocolloPerson.fiscalCode", u4).ignoreCase();
						cm4 = Restrictions.like("ProtocolloMultiPerson.fiscalCode", u4).ignoreCase();
					}
					if(u5!=null && !u5.trim().isEmpty()){
						u5 = "%" + u5 + "%";

						c5 = Restrictions.like("ProtocolloPerson.birthPlace.cty", u5).ignoreCase();
						cm5 = Restrictions.like("ProtocolloMultiPerson.birthPlace.cty", u5).ignoreCase();
					}
				}
			}
			if ("Medico".equals(type)){
				//medico
				Criteria procProtPhysicionCrit = findSubCriteria("ProtocolloMedico");
				if(procProtPhysicionCrit == null) {
					procProtPhysicionCrit = procProtCrit.createCriteria("richiedenteMedico", "ProtocolloMedico",Criteria.LEFT_JOIN);
				}
				Criteria procProtMultiPhysicionCrit = findSubCriteria("ProtocolloMultiMedico");
				if(procProtMultiPhysicionCrit == null) {
					procProtMultiPhysicionCrit = procProtMultiCrit.createCriteria("richiedenteMedico", "ProtocolloMultiMedico",Criteria.LEFT_JOIN);
				}

				if((m1==null || m1.trim().isEmpty()) && (m2==null ||m2.trim().isEmpty()) && (m3==null || m3.trim().isEmpty())){
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();

				} else{
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();
					if(m1!=null  && !m1.trim().isEmpty()){
						m1 = "%" + m1 + "%";

						c1 = Restrictions.like("ProtocolloMedico.name.fam", m1).ignoreCase();
						cm1 = Restrictions.like("ProtocolloMultiMedico.name.fam", m1).ignoreCase();
					}
					if(m2!=null && !m2.trim().isEmpty()){
						m2 = "%" + m2 + "%";

						c2 = Restrictions.like("ProtocolloMedico.name.giv", m2).ignoreCase();
						cm2 = Restrictions.like("ProtocolloMultiMedico.name.giv", m2).ignoreCase();
					}
					if(m3!=null  && !m3.trim().isEmpty()){
						m3 = "%" + m3 + "%";

						c3 = Restrictions.like("ProtocolloMedico.regionalCode", m3).ignoreCase();
						cm3 = Restrictions.like("ProtocolloMultiMedico.regionalCode", m3).ignoreCase();
					}
				}
			}
			if ("Interno".equals(type)){
				//interno
				Criteria procProtInternoCrit = findSubCriteria("ProtocolloInterno");
				if(procProtInternoCrit == null) {
					procProtInternoCrit = procProtCrit.createCriteria("richiedenteInterno", "ProtocolloInterno",Criteria.LEFT_JOIN);
				}
				Criteria procProtMultiInternoCrit = findSubCriteria("ProtocolloMultiInterno");
				if(procProtMultiInternoCrit == null) {
					procProtMultiInternoCrit = procProtMultiCrit.createCriteria("richiedenteInterno", "ProtocolloMultiInterno",Criteria.LEFT_JOIN);
				}

				if((i1==null || i1.trim().isEmpty()) && (i2==null ||i2.trim().isEmpty()) && (i3==null || i3.trim().isEmpty())){
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();

				} else{
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();
					if(i1!=null  && !i1.trim().isEmpty()){
						i1 = "%" + i1 + "%";

						c1 = Restrictions.like("ProtocolloInterno.name.fam", i1).ignoreCase();
						cm1 = Restrictions.like("ProtocolloMultiInterno.name.fam", i1).ignoreCase();
					}
					if(i2!=null  && !i2.trim().isEmpty()){
						i2 = "%" + i2 + "%";

						c2 = Restrictions.like("ProtocolloInterno.name.giv", i2).ignoreCase();
						cm2 = Restrictions.like("ProtocolloMultiInterno.name.giv", i2).ignoreCase();
					}
					if(i3!=null && !i3.trim().isEmpty()){

						c3 = Restrictions.like("ProtocolloInterno.fiscalCode", i3).ignoreCase();
						cm3 = Restrictions.like("ProtocolloMultiInterno.fiscalCode", i3).ignoreCase();
					}		
				}
			}


			if ("Ditta".equals(type)){		
				//ditta
				Criteria procProtPersonGiurCrit = findSubCriteria("ProtocolloPersoneGiuridiche");
				if(procProtPersonGiurCrit == null) {
					procProtPersonGiurCrit = procProtCrit.createCriteria("richiedenteDitta", "ProtocolloPersoneGiuridiche",Criteria.LEFT_JOIN);
				}
				Criteria procProtMultiPersonGiurCrit = findSubCriteria("ProtocolloMultiPersoneGiuridiche");
				if(procProtMultiPersonGiurCrit == null) {
					procProtMultiPersonGiurCrit = procProtMultiCrit.createCriteria("richiedenteDitta", "ProtocolloMultiPersoneGiuridiche",Criteria.LEFT_JOIN);
				}

				if((d1==null || d1.trim().isEmpty()) && (d2==null ||d2.trim().isEmpty()) && (d3==null || d3.trim().isEmpty()) && (d4==null ||d4.trim().isEmpty())){
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();

				} else{
					c0 = Restrictions.eq("ProtocolloRichiedente.code", type).ignoreCase();
					cm0 = Restrictions.eq("ProtocolloMultiRichiedente.code", type).ignoreCase();
					if(d1!=null  && !d1.trim().isEmpty()){
						d1 = "%" + d1 + "%";

						c1 = Restrictions.like("ProtocolloPersoneGiuridiche.patritaIva", d1).ignoreCase();
						cm1 = Restrictions.like("ProtocolloMultiPersoneGiuridiche.patritaIva", d1).ignoreCase();
					}
					if(d2!=null  && !d2.trim().isEmpty()){
						d2 = "%" + d2 + "%";

						c2 = Restrictions.like("ProtocolloPersoneGiuridiche.codiceFiscale", d2).ignoreCase();
						cm2 = Restrictions.like("ProtocolloMultiPersoneGiuridiche.codiceFiscale", d2).ignoreCase();
					}
					if(d3!=null && !d3.trim().isEmpty()){
						d3 = "%" + d3 + "%";

						c3 = Restrictions.like("ProtocolloPersoneGiuridiche.denominazione", d3).ignoreCase();
						cm3 = Restrictions.like("ProtocolloMultiPersoneGiuridiche.denominazione", d3).ignoreCase();
					}	
					if(d4!=null && !d4.trim().isEmpty()){
						d4 = "%" + d4 + "%";

						c4 = Restrictions.like("ProtocolloPersoneGiuridiche.codiceDitta", d4).ignoreCase();
						cm4 = Restrictions.like("ProtocolloMultiPersoneGiuridiche.codiceDitta", d4).ignoreCase();
					}	
				}
			}

			List<Criterion> cList1 = new ArrayList<Criterion>();
			List<Criterion> cList2 = new ArrayList<Criterion>();
			//add criterion to list for protocollo
			if(c0!=null){
				cList1.add(c0);
			}	
			if(c1!=null){
				cList1.add(c1);
			}	
			if(c2!=null){
				cList1.add(c2);
			}
			if(c3!=null){
				cList1.add(c3);
			}
			if(c4!=null){
				cList1.add(c4);
			}
			if(c5!=null){
				cList1.add(c5);
			}
			//add criterion to list for protocolloMulti
			if(cm0!=null){
				cList2.add(cm0);
			}	
			if(cm1!=null){
				cList2.add(cm1);
			}	
			if(cm2!=null){
				cList2.add(cm2);
			}
			if(cm3!=null){
				cList2.add(cm3);
			}
			if(cm4!=null){
				cList2.add(cm4);
			}
			if(cm5!=null){
				cList2.add(cm5);
			}
			//make list of multi criterion  for protocollo
			while(cList1.size()>=2){
				cx1 = Restrictions.and(cList1.get(0), cList1.get(1));	
				cList1.remove(0);
				cList1.remove(0);
				cList1.add(cx1);
			}

			//make list of multi criterion  for protocolloMulti
			while(cList2.size()>=2){
				cx2 = Restrictions.and(cList2.get(0), cList2.get(1));	
				cList2.remove(0);
				cList2.remove(0);
				cList2.add(cx2);
			}
			if(cList1.size()>0 && cList2.size()>0){
				//last logical expression
				LogicalExpression l = Restrictions.or(cList1.get(0), cList2.get(0));

				//add it to entityCriteria
				entityCriteria.add(l);
			} else{

				return;
			}
		}catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 

	}

	public void filterByUtente() {
		String type=null;
		//mittente della richiesta di comunicazione
		if(temporary.get("riferimento")!=null)
			type=(String) ((CodeValue) temporary.get("riferimento")).getCode();

		if (type==null || type.isEmpty() ){
			return;
		}

		// utente
		String u1=null;
		String u2=null;
		Date u3=null;
		String u4=null;
		String u5=null;

		if (type!=null){
			if ("Utente".equals(type)){
				u1=(String)getTemporary().get("personeAssociazioni.person.name.fam");
				u2=(String)getTemporary().get("personeAssociazioni.person.name.giv");
				u3=(Date)getTemporary().get("personeAssociazioni.person.birthTime");
				u4=(String)getTemporary().get("personeAssociazioni.person.fiscalCode");
				u5=(String)getTemporary().get("personeAssociazioni.person.birthPlace.cty");
			}else{
				return;
			}
		}

		if(entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
		}

		removeExpression("this", "PersoneAssociazioniPerson.name.fam");
		removeExpression("this", "PersoneAssociazioniPerson.name.giv");
		removeExpression("this", "PersoneAssociazioniPerson.birthTime");
		removeExpression("this", "PersoneAssociazioniPerson.fiscalCode");
		removeExpression("this", "PersoneAssociazioniPerson.birthPlace.cty");

		removeSubCriteria(entityCriteria, "PersoneAssociazioniPerson");
		removeSubCriteria(entityCriteria, "PersoneAssociazioniSediPersone");
		removeSubCriteria(entityCriteria, "PersoneAssociazioni");


		//create criteria for PersoneAssociazioni
		Criteria personeAssociazioniCrit = findSubCriteria("PersoneAssociazioni");
		if(personeAssociazioniCrit == null) {
			personeAssociazioniCrit =  entityCriteria.createCriteria("personeAssociazioni", "PersoneAssociazioni", Criteria.INNER_JOIN);
		}
		Criteria personeAssociazioniPersonCrit = findSubCriteria("PersoneAssociazioniPerson");
		if(personeAssociazioniPersonCrit == null) {
			personeAssociazioniPersonCrit =  personeAssociazioniCrit.createCriteria("person", "PersoneAssociazioniPerson", Criteria.LEFT_JOIN);
		}
		Criteria personeAssociazioniSediPersoneCrit = findSubCriteria("PersoneAssociazioniSediPersone");
		if(personeAssociazioniSediPersoneCrit == null) {
			personeAssociazioniSediPersoneCrit =  personeAssociazioniCrit.createCriteria("sediPersone", "PersoneAssociazioniSediPersone", Criteria.LEFT_JOIN);
		}

		//criterion for person
		Criterion c1 = null;
		Criterion c2 = null;
		Criterion c3 = null;
		Criterion c4 = null;
		Criterion c5 = null;
		//criterion for sedi_persone
		Criterion cm1 = null;
		Criterion cm2 = null;
		Criterion cm3 = null;
		Criterion cm4 = null;
		Criterion cm5 = null;
		//multi criterion for person
		Criterion cx1 = null;
		//multi criterion for sedi_persone
		Criterion cx2 = null;

		if ("Utente".equals(type)){

			if(!((u1==null || u1.trim().isEmpty()) && (u2==null || u2.trim().isEmpty()) && (u3==null) && (u4==null || u4.trim().isEmpty()) && (u5==null || u5.trim().isEmpty()))) {
				if(u1!=null && !u1.trim().isEmpty()){
					u1 = "%" + u1 + "%";

					c1 = Restrictions.like("PersoneAssociazioniPerson.name.fam", u1).ignoreCase();
					cm1 = Restrictions.like("PersoneAssociazioniSediPersone.name.fam", u1).ignoreCase();
				}
				if(u2!=null  && !u2.trim().isEmpty()){
					u2 = "%" + u2 + "%";

					c2 = Restrictions.like("PersoneAssociazioniPerson.name.giv", u2).ignoreCase();
					cm2 = Restrictions.like("PersoneAssociazioniSediPersone.name.giv", u2).ignoreCase();
				}
				if(u3!=null){

					c3 = Restrictions.eq("PersoneAssociazioniPerson.birthTime", u3);
					cm3 = Restrictions.eq("PersoneAssociazioniSediPersone.birthTime", u3);
				}
				if(u4!=null && !u4.trim().isEmpty()){
					u4 = "%" + u4 + "%";

					c4 = Restrictions.like("PersoneAssociazioniPerson.fiscalCode", u4).ignoreCase();
					cm4 = Restrictions.like("PersoneAssociazioniSediPersone.fiscalCode", u4).ignoreCase();
				}
				if(u5!=null && !u5.trim().isEmpty()){
					u5 = "%" + u5 + "%";

					c5 = Restrictions.like("PersoneAssociazioniPerson.birthPlace.cty", u5).ignoreCase();
					cm5 = Restrictions.like("PersoneAssociazioniSediPersone.birthPlace.cty", u5).ignoreCase();
				}
			}
		}

		List<Criterion> cList1 = new ArrayList<Criterion>();
		List<Criterion> cList2 = new ArrayList<Criterion>();
		//add criterion to list for person
		if(c1!=null){
			cList1.add(c1);
		}	
		if(c2!=null){
			cList1.add(c2);
		}
		if(c3!=null){
			cList1.add(c3);
		}
		if(c4!=null){
			cList1.add(c4);
		}
		if(c5!=null){
			cList1.add(c5);
		}
		//add criterion to list for sedi_persone
		if(cm1!=null){
			cList2.add(cm1);
		}	
		if(cm2!=null){
			cList2.add(cm2);
		}
		if(cm3!=null){
			cList2.add(cm3);
		}
		if(cm4!=null){
			cList2.add(cm4);
		}
		if(cm5!=null){
			cList2.add(cm5);
		}
		//make list of multi criterion  for protocollo
		while(cList1.size()>=2){
			cx1 = Restrictions.and(cList1.get(0), cList1.get(1));	
			cList1.remove(0);
			cList1.remove(0);
			cList1.add(cx1);
		}

		//make list of multi criterion  for protocolloMulti
		while(cList2.size()>=2){
			cx2 = Restrictions.and(cList2.get(0), cList2.get(1));	
			cList2.remove(0);
			cList2.remove(0);
			cList2.add(cx2);
		}
		if(cList1.size()>0 && cList2.size()>0){
			//last logical expression
			LogicalExpression l = Restrictions.or(cList1.get(0), cList2.get(0));

			//add it to entityCriteria
			entityCriteria.add(l);
		} else{
			return;
		}
	}

	/**
	 * Filter Procpratiche with vigilanza.cantiere.committente.personeGiuridiche.denominazione 
	 * or vigilanza.cantiere.committente.person.name.fam or giv
	 * 
	 * Used by: vp_pratiche_filters_supervision.mmgp
	 */
	private String filterByVigComm;

	public String getFilterByVigComm() {
		return filterByVigComm;
	}

	public void setFilterByVigComm(String committente) {

		filterByVigComm = committente;

		/*
		 * IL COMMITTENTE PUO' ESSERE UNA PERSONA FISICA O GIURIDICA
		 */
		removeExpression("this", "VigilanzaCantiereCommittentePerson.name.giv");
		removeExpression("this", "VigilanzaCantiereCommittentePerson.name.fam");
		removeExpression("this", "VigilanzaCantiereCommittentePersoneGiuridiche.denominazione");
		removeSubCriteria(entityCriteria, "VigilanzaCantiereCommittentePerson");
		removeSubCriteria(entityCriteria, "VigilanzaCantiereCommittentePersoneGiuridiche");
		removeSubCriteria(entityCriteria, "VigilanzaCantiereCommittente");
		//		removeSubCriteria(entityCriteria, "VigilanzaCantiere");
		//		removeSubCriteria(entityCriteria, "Vigilanza");

		if(committente==null || committente.isEmpty())
			return;

		if(entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
		}

		Criteria vCrit = findSubCriteria("Vigilanza");
		if(vCrit == null) {
			vCrit = entityCriteria.createCriteria("vigilanza", "Vigilanza");
		}

		Criteria vcCrit = findSubCriteria("VigilanzaCantiere");
		if(vcCrit == null) {
			vcCrit = vCrit.createCriteria("cantiere", "VigilanzaCantiere");
		}

		Criteria vccCrit = findSubCriteria("VigilanzaCantiereCommittente");
		if(vccCrit == null) {
			vccCrit = vcCrit.createCriteria("committente", "VigilanzaCantiereCommittente", Criteria.LEFT_JOIN);
		}

		Criteria vccpgCrit = findSubCriteria("VigilanzaCantiereCommittentePersoneGiuridiche");
		if(vccpgCrit == null) {
			vccpgCrit = vccCrit.createCriteria("personeGiuridiche", "VigilanzaCantiereCommittentePersoneGiuridiche", Criteria.LEFT_JOIN);
		}

		if(findSubCriteria("VigilanzaCantiereCommittentePerson") == null) {
			vccCrit.createCriteria("person", "VigilanzaCantiereCommittentePerson", Criteria.LEFT_JOIN);
		}

		committente = "%" + committente + "%";

		LogicalExpression givOrFam =  Restrictions.or(
				Restrictions.or(
						Restrictions.like("VigilanzaCantiereCommittentePerson.name.giv", committente).ignoreCase(), 
						Restrictions.like("VigilanzaCantiereCommittentePerson.name.fam", committente).ignoreCase()),
						Restrictions.like("VigilanzaCantiereCommittentePersoneGiuridiche.denominazione", committente).ignoreCase());

		entityCriteria.add(givOrFam);
	}

	/**
	 * Filter Procpratiche by responsabile
	 * 
	 * Used by: vp_pratiche_filters_supervision.mmgp
	 */
	private String filterByVigResp;


	public String getFilterByVigResp() {
		return filterByVigResp;
	}

	public void setFilterByVigResp(String responsabile) {
		this.filterByVigResp = responsabile;

		// getEqual().put("personeCantiere.ruolo.code", null);  
		removeExpression("this", "VigilanzaCantierePersoneCantierePerson.name.giv");
		removeExpression("this", "VigilanzaCantierePersoneCantierePerson.name.fam");
		removeSubCriteria(entityCriteria, "VigilanzaCantierePersoneCantierePerson");
		removeSubCriteria(entityCriteria, "VigilanzaCantierePersoneCantiere");
		//		removeSubCriteria(entityCriteria, "VigilanzaCantiere");
		//		removeSubCriteria(entityCriteria, "Vigilanza");

		if(responsabile==null || responsabile.isEmpty())
			return;

		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		Criteria vCrit = findSubCriteria("Vigilanza");
		if(vCrit == null) {
			vCrit = entityCriteria.createCriteria("vigilanza", "Vigilanza");
		}

		Criteria vcCrit = findSubCriteria("VigilanzaCantiere");
		if(vcCrit == null) {
			vcCrit = vCrit.createCriteria("cantiere", "VigilanzaCantiere");
		}

		Criteria vcpcCrit = findSubCriteria("VigilanzaCantierePersoneCantiere");
		if(vcpcCrit == null) {
			vcpcCrit = vcCrit.createCriteria("personeCantiere", "VigilanzaCantierePersoneCantiere");
		}

		if(findSubCriteria("VigilanzaCantierePersoneCantierePerson") == null) {
			vcpcCrit.createCriteria("person", "VigilanzaCantierePersoneCantierePerson");
		}

		responsabile = "%" + responsabile + "%";

		//CERCO, TRA LE PERSONE DEL CANTIERE QUELLI CON RUOLO RUOLOCANT01=RESPONSABILE LAVORI
		// getEqual().put("personeCantiere.ruolo.code", "RUOLOCANT01");
		LogicalExpression givOrFam = Restrictions.or(Restrictions.like("VigilanzaCantierePersoneCantierePerson.name.giv", responsabile).ignoreCase(), 
				Restrictions.like("VigilanzaCantierePersoneCantierePerson.name.fam", responsabile).ignoreCase());

		entityCriteria.add(givOrFam);
	}


	/**
	 * Filter by comparto, same as from PersoneGiuridicheAction.filterAteco
	 * Used by: MOD_home/CORE/FORMS/popup/comparto.mmgp
	 */
	private List<CodeValue> filterByComparto;

	private static String hqlCompartoAteco = "SELECT DISTINCT ateco.id FROM CompartoAteco ca JOIN ca.ateco ateco JOIN ca.comparto spec JOIN spec.parent comparto " +
			"WHERE spec.id in (:comparto) OR comparto.id in (:comparto)";

	public List<CodeValue> getFilterByComparto() {
		return filterByComparto;
	}

	public void setFilterByComparto(List<CodeValue> filterByComparto) {
		this.filterByComparto = filterByComparto;

		CodeValue area = (CodeValue)this.equal.get("serviceDeliveryLocation.area");
		String restriction;
		if (area == null) {
			return;
		} else if ("WORKACCIDENT".equals(area.getCode())) {
			restriction = "infortuni.comparto.id";
		} else if ("SUPERVISION".equals(area.getCode())) {
			restriction = "vigilanza.comparto.id";
		} else if ("WORKDISEASE".equals(area.getCode())) {
			restriction = "malattiaProfessionale.comparto.id";
		} else {
			return;
		}

		if(filterByComparto != null && !filterByComparto.isEmpty()){
			List<String> cvIds = new ArrayList<String>();
			for (CodeValue cv: filterByComparto) {
				cvIds.add(cv.getId());
			}

			Query q = ca.createQuery(hqlCompartoAteco);
			q.setParameter("comparto", cvIds);
			List<String> ids = q.getResultList();
			if(ids!=null && !ids.isEmpty()){
				getIn().put(restriction, ids);
			}
		} else {
			getIn().put(restriction, null);
			// bug, previous line removes subcriteria malattiaProfessionale, infortuni or vigilanza
			if ("WORKACCIDENT".equals(area.getCode())) {
				getSelect().add("infortuni.internalId");
			} else if ("SUPERVISION".equals(area.getCode())) {
				getSelect().add("vigilanza.internalId");
			} else if ("WORKDISEASE".equals(area.getCode())) {
				getSelect().add("malattiaProfessionale.internalId");
			}
			this.filterByComparto = null;
		}
	}

	public List<SelectItem> getRadioList() throws DictionaryException {
		List<SelectItem> out = new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();

		return voc.selectCodeValues("PHIDIC", "TargetSource:Ditta,Utente");
	}


	@SuppressWarnings("unchecked")
	public boolean checkDuplicates(Protocollo protocollo){
		try{
			UserBean ub = UserBean.instance();

			String query = "SELECT DISTINCT pp FROM Procpratiche pp " +
					"LEFT JOIN pp.serviceDeliveryLocation sdl " +
					"LEFT JOIN pp.protocollo pr " +
					"LEFT JOIN pr.riferimento refCode " +
					"LEFT JOIN pr.riferimentoUtente ut " +
					"LEFT JOIN pr.riferimentoDitta ditta " +
					"LEFT JOIN pr.riferimentoInterno inter " +
					"LEFT JOIN pr.riferimentoCantiere cant " +
					"WHERE pp.data >= :d AND sdl.internalId IN (:sdlocs) AND (";


			List<Procpratiche> duplicates = new ArrayList<Procpratiche>();

			/* Questo controllo va effettuato solo su PRATICHE che sono state aperte entro n mesi,
			 * dove n è un periodo configurabile attraverso un parametro (es. mesi_controllo).
			 * Iniziamelmente il valore attribuito a questo parametro deve essere 3 */ 
			int n = 3; //Default
			String nStr = null;

			ParameterManager pm = ParameterManager.instance();
			Object mesiControllo = pm.getParameter("p.home.gestione_segnalazione.mesiControllo", "value");

			if (mesiControllo!=null)
				nStr = mesiControllo.toString();

			if (nStr!=null && Integer.parseInt(nStr)>0)
				n = Integer.parseInt(nStr);

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());

			//n mesi fa
			cal.add(Calendar.MONTH, -n);
			Date  d = cal.getTime();

			/* 1) Il numero di protocollo della comunicazione è già presente in una pratica.
			 * NUMERO DI PROTOCOLLO va inteso come insieme di: anno protocollo e numero protocollo
			 * 
			 * OPPURE
			 * 
			 * 2) esiste una pratica con lo stesso "riferito a". Il "riferito a" deve essere dello stesso tipo "utente/ditta/cantiere/etc."  
			 * e corrispondente alla stessa entità (stessa persona, stessa azienda (occhio ai versioning di parix) etc. */
			BigDecimal annoProtocollo = protocollo.getAnnoProtocollo();			
			BigDecimal nprotocollo = protocollo.getNprotocollo();

			//Punto 1
			if (annoProtocollo!=null && nprotocollo!=null)
				query += "(pr.annoProtocollo = :annoProtocollo AND pr.nprotocollo = :nprotocollo)";

			if (query.endsWith(")"))
				query += " OR ";

			//Punto 2    
			String riferimento = null;
			Long utenteInternalId = null;
			String denominazione = null;
			String cantiereId = null;
			Long internoInternalId = null;

			CodeValue rif = protocollo.getRiferimento();
			if (rif!=null){
				riferimento = rif.getCode();



				if (riferimento!=null && !"".equals(riferimento)){
					if ("Utente".equals(riferimento)){
						Person utente = protocollo.getRiferimentoUtente();
						if (utente!=null){
							utenteInternalId = utente.getInternalId();
							query += "(refCode.code='Utente' AND ut.internalId = :utenteInternalId)";
						}
					} else if ("Ditta".equals(riferimento)){
						PersoneGiuridiche ditta = protocollo.getRiferimentoDitta();
						if (ditta!=null){
							denominazione = ditta.getDenominazione();
							query += "(refCode.code='Ditta' AND ditta.denominazione = :denominazione)";
						}

					} else if ("Interno".equals(riferimento)){
						Employee interno = protocollo.getRiferimentoInterno();
						if (interno!=null){
							internoInternalId = interno.getInternalId();
							query += "(refCode.code='Interno' AND inter.internalId = :internoInternalId)";
						}

					} else if ("Cantiere".equals(riferimento)){
						Cantiere cantiere = protocollo.getRiferimentoCantiere();
						if (cantiere!=null){
							cantiereId = cantiere.getId();
							query += "(refCode.code='Cantiere' AND cant.id = :cantiereId)";
						}
					}
				}
			}
			/* Fine Punto 2 */

			if (query.endsWith("("))
				return false;

			query = (query.endsWith(" OR ") ? query.substring(0, query.length()-4) + ") order by pp.data desc" : query + ") order by pp.data desc");

			Query qry = ca.createQuery(query);

			qry.setParameter("d", d);
			qry.setParameter("sdlocs", ub.getSdLocs());

			if (query.contains(":annoProtocollo"))
				qry.setParameter("annoProtocollo", annoProtocollo);

			if (query.contains(":nprotocollo"))
				qry.setParameter("nprotocollo", nprotocollo);

			if (query.contains(":utenteInternalId"))
				qry.setParameter("utenteInternalId", utenteInternalId);

			if (query.contains(":denominazione"))
				qry.setParameter("denominazione", denominazione);

			if (query.contains(":cantiereId"))
				qry.setParameter("cantiereId", cantiereId);

			if (query.contains(":internoInternalId"))
				qry.setParameter("internoInternalId", internoInternalId);

			duplicates = (List<Procpratiche>)qry.getResultList();;

			if (duplicates!=null && duplicates.size()>0){
				this.injectList(duplicates);
				this.injectFirst();
				return true;
			}

			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public String getCurrentUser(){
		UserBean ub = (UserBean) Component.getInstance("userBean");
		EN empName = ub.getCurrentEmployee().getName();

		return empName.getFam() + " " + empName.getGiv();
	}


	/* H0084924 */
	public void resetVigilanzaFilters(){
		CodeValuePhi cv = (CodeValuePhi) equal.get("vigilanza.type");
		String code = cv.getCode();

		//Asbestos
		if(!"Asbestos".equals(code)){
			equal.remove("vigilanza.tipoSegnalazione");
			equal.remove("vigilanza.tipoIntervento");
			//equal.remove("vigilanza.friabile");
			//equal.remove("vigilanza.compatto");
			greaterEqual.remove("vigilanza.bonificatiKgEffettivi");
			lessEqual.remove("vigilanza.bonificatiKgEffettivi");
			greaterEqual.remove("vigilanza.presuntoInizioLavori");
			lessEqual.remove("vigilanza.presuntoInizioLavori");
			greaterEqual.remove("vigilanza.effettivoFineLavori");
			lessEqual.remove("vigilanza.effettivoFineLavori");
			greaterEqual.remove("vigilanza.numLavoratori");
			lessEqual.remove("vigilanza.numLavoratori");
			like.remove("vigilanza.committente");

			temporary.remove("vigilanza.friabile");
			temporary.remove("vigilanza.compatto");
			//this.filterByFriabileCompatto();

		}

		//Generic
		if(!"Generic".equals(code)){
			equal.remove("vigilanza.personaGiuridicaSede.sede.addr.cpa");
			equal.remove("vigilanza.personaGiuridicaSede.sede.addr.zip");
			equal.remove("vigilanza.personaGiuridicaSede.sede.addr.cty");
			equal.remove("vigilanza.personaGiuridicaSede.sede.addr.code");

			getIn().remove("vigilanza.comparto.id");
		}

		//Yard
		if(!"Yard".equals(code)){
			equal.remove("vigilanza.cantiere.tagCantiere.tipologiaCantiere");
			like.remove("vigilanza.cantiere.naturaOpera");
			greaterEqual.remove("protocollo.data");
			lessEqual.remove("protocollo.data");
			greaterEqual.remove("vigilanza.presuntoInizioLavori");
			lessEqual.remove("vigilanza.presuntoInizioLavori");
			greaterEqual.remove("vigilanza.cantiere.maxWorkers");
			lessEqual.remove("vigilanza.cantiere.maxWorkers");
			greaterEqual.remove("vigilanza.cantiere.numeroImprese");
			lessEqual.remove("vigilanza.cantiere.numeroImprese");
			greaterEqual.remove("vigilanza.cantiere.numeroAutonomi");
			lessEqual.remove("vigilanza.cantiere.numeroAutonomi");
			greaterEqual.remove("vigilanza.cantiere.cost");
			lessEqual.remove("vigilanza.cantiere.cost");
			like.remove("vigilanza.cantiere.ditteCantiere.personeGiuridiche.denominazione");

			this.setFilterByVigComm(null);
		}
	}

	/* H0084924 */
	public void filterByFriabileCompatto(){

		((FilterMap)getEqual()).remove("vigilanza.friabile");
		((FilterMap)getEqual()).remove("vigilanza.compatto");

		boolean friabile = ((Boolean) temporary.get("vigilanza.friabile")!=null ? (Boolean) temporary.get("vigilanza.friabile") : false);
		boolean compatto = ((Boolean) temporary.get("vigilanza.compatto")!=null ? (Boolean) temporary.get("vigilanza.compatto") : false);

		if(friabile==true){
			((FilterMap)getEqual()).put("vigilanza.friabile", friabile);
		}

		if(compatto==true){
			((FilterMap)getEqual()).put("vigilanza.compatto", compatto);
		}
	}

	public void cleanFilterProvvedimento(){
		if(entityCriteria == null)
			entityCriteria = ca.createCriteria(entityClass);

		removeExpression("this", "AttivitaProvvedimentiSoggettoUtente.fiscalCode");
		removeExpression("this", "AttivitaProvvedimentiCaricaSediPersone.fiscalCode");

		removeSubCriteria(entityCriteria, "AttivitaProvvedimentiSoggettoUtente");
		removeSubCriteria(entityCriteria, "AttivitaProvvedimentiCaricaSediPersone");

		removeSubCriteria(entityCriteria, "AttivitaProvvedimentiSoggetto");
		removeSubCriteria(entityCriteria, "AttivitaProvvedimentiCarica");

		removeSubCriteria(entityCriteria, "AttivitaProvvedimenti");

		removeSubCriteria(entityCriteria, "Attivita");
	}

	public void filterProvvedimento(String fiscalCode){
		if(fiscalCode!=null){
			if(entityCriteria == null)
				entityCriteria = ca.createCriteria(entityClass);

			this.cleanFilterProvvedimento();

			Criteria aCrit = findSubCriteria("Attivita");
			if(aCrit == null) {
				aCrit = entityCriteria.createCriteria("attivita", "Attivita");
			}

			Criteria pCrit = findSubCriteria("AttivitaProvvedimenti");
			if(pCrit == null) {
				pCrit = aCrit.createCriteria("provvedimenti", "AttivitaProvvedimenti");
			}

			Criteria psCrit = findSubCriteria("AttivitaProvvedimentiSoggetto");
			if(psCrit == null) {
				psCrit = pCrit.createCriteria("soggetto", "AttivitaProvvedimentiSoggetto", Criteria.LEFT_JOIN);
			}

			Criteria pcCrit = findSubCriteria("AttivitaProvvedimentiCarica");
			if(pcCrit == null) {
				pcCrit = pCrit.createCriteria("carica", "AttivitaProvvedimentiCarica", Criteria.LEFT_JOIN);
			}

			Criteria psuCrit = findSubCriteria("AttivitaProvvedimentiSoggettoUtente");
			if(psuCrit == null) {
				psuCrit = psCrit.createCriteria("utente", "AttivitaProvvedimentiSoggettoUtente", Criteria.LEFT_JOIN);
			}

			Criteria pcsCrit = findSubCriteria("AttivitaProvvedimentiCaricaSediPersone");
			if(pcsCrit == null) {
				pcsCrit = pcCrit.createCriteria("sediPersone", "AttivitaProvvedimentiCaricaSediPersone", Criteria.LEFT_JOIN);
			}

			LogicalExpression l = Restrictions.or(Restrictions.eq("AttivitaProvvedimentiSoggettoUtente.fiscalCode", fiscalCode),
					Restrictions.eq("AttivitaProvvedimentiCaricaSediPersone.fiscalCode", fiscalCode));

			//add it to entityCriteria
			entityCriteria.add(l);
		}
	}

	public void filterByInfortuni(){
		Boolean notificaDecessoBl = (Boolean) getTemporary().get("notificaDecesso");

		if(Boolean.TRUE.equals(notificaDecessoBl)){
			getEqual().put("infortuni.infortuniExt.notificaDecesso", true);
		}else{
			getEqual().remove("infortuni.infortuniExt.notificaDecesso");
		}
	}

	public boolean isUlssEnabledToTabAtti(Procpratiche pratica){
		boolean ret = false;

		if(pratica==null)
			return ret;

		ServiceDeliveryLocation distretto = pratica.getUoc();
		CodeValueParameter tabAttiVisible = ca.get(CodeValueParameter.class, "p.home.procpratiche.tabatti");

		if (tabAttiVisible==null)
			return ret;

		CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
		HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(tabAttiVisible, distretto.getParent().getInternalId());
		String value = evaluatedParameter.get("visible").toString();

		ret = Boolean.TRUE.equals(Boolean.parseBoolean(value));
		return ret;
	}

	public boolean isForceDateValid(){
		if(getEntity()==null)
			return true;

		if(getEntity().getCompletedDate()==null)
			return false;

		Calendar cal = Calendar.getInstance();

		cal.setTime(getEntity().getData());
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		if(getEntity().getCompletedDate().before(cal.getTime())){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			FacesErrorUtils.addErrorMessage("commError", "La data di chiusura pratica deve essere maggiore o uguale alla data apertura (" + sdf.format(cal.getTime()) +")", "La data di chiusura pratica deve essere maggiore o uguale alla data apertura (" + sdf.format(cal.getTime()) +")");
			return false;		
		}

		return true;
	}

	public Long countResults(){
		List<Integer> multiresult = entityCriteria.setFirstResult(0).setProjection(Projections.countDistinct("internalId")).list();
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

	public void generateCoordinateExportFile(PagedDataModel procpraticheList){
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) procpraticheList.getFullList();

		ImpiantiDocument exportCsv = new ImpiantiDocument();
		StringBuffer sb = new StringBuffer();

		FunctionsBean fb = FunctionsBean.instance();

		for(HashMap<String, Object> map : list){
			String numeroPratica = (String) map.get("numero");

			HashMap<String, Object> sdl = (HashMap<String, Object>) map.get("serviceDeliveryLocation");
			CodeValuePhi area = (CodeValuePhi) sdl.get("area");
			String areaCode = area.getCode();

			HashMap<String, Object> vigilanza = (HashMap<String, Object>) map.get("vigilanza");

			if ("WORKACCIDENT".equals(areaCode)) {
				Criteria infCrit = ca.createCriteria(Infortuni.class);
				// .setProjection(Projections.projectionList().add(Projections.property("infortuniExt")));
				infCrit.add(Restrictions.eq("procpratiche.internalId", map.get("internalId")));
				List<Infortuni> infortuni = infCrit.list();

				if(infortuni==null)
					continue;

				for (Infortuni infortunio: infortuni) {
					CodeValue place = infortunio.getPlace();
					InfortuniExt infortuniExt = infortunio.getInfortuniExt();
					String lat = infortuniExt.getLatitudine();
					String lng = infortuniExt.getLongitudine();
					if (place!=null && infortuniExt!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(lat).append(";").append(lng).append(";");
						sb.append("\r\n");
					}
				}
			} else if ("SUPERVISION".equals(areaCode) && vigilanza!=null) {
				HashMap<String, Object> type = (HashMap<String, Object>) vigilanza.get("type");
				String typeCode = (String)(type.get("code"));

				String latVigilanza = (String) vigilanza.get("latitudine");
				String lngVigilanza = (String) vigilanza.get("longitudine");

				HashMap<String, Object> cantiere = (HashMap<String, Object>) vigilanza.get("cantiere");
				String latCantiere = (String) cantiere.get("latitudine");
				String lngCantiere = (String) cantiere.get("longitudine");

				if ("Asbestos".equals(typeCode)) {
					HashMap<String, Object> sitoBonificaSede = (HashMap<String, Object>) vigilanza.get("sitoBonificaSede");
					String lat = (String) sitoBonificaSede.get("latitudine");
					String lng = (String) sitoBonificaSede.get("longitudine");
					if (sitoBonificaSede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(lat).append(";").append(lng).append(";");
						sb.append("\r\n");
					} else if (cantiere!=null && latCantiere!=null && !latCantiere.isEmpty() && lngCantiere!=null && !lngCantiere.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(latCantiere).append(";").append(lngCantiere).append(";");
						sb.append("\r\n");
					}  else if (latVigilanza!=null && !latVigilanza.isEmpty() && lngVigilanza!=null && !lngVigilanza.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(latVigilanza).append(";").append(lngVigilanza).append(";");
						sb.append("\r\n");
					}
				} else if ("Yard".equals(typeCode) && latCantiere!=null && !latCantiere.isEmpty() && lngCantiere!=null && !lngCantiere.isEmpty()) {
					sb.append(numeroPratica).append(";");
					sb.append(latCantiere).append(";").append(lngCantiere).append(";");
					sb.append("\r\n");
				} else if ("Generic".equals(typeCode)) {
					Criteria pgCrit = ca.createCriteria(PersonaGiuridicaSede.class);
					pgCrit.add(Restrictions.eq("vigilanza.internalId", vigilanza.get("internalId")));
					List<PersonaGiuridicaSede> pgs = pgCrit.list();

					if(pgs==null)
						continue;

					for (PersonaGiuridicaSede pg: pgs) {
						Sedi sede = pg.getSede();
						String lat = sede!=null?sede.getLatitudine():null;
						String lng = sede!=null?sede.getLongitudine():null;
						if (sede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
							sb.append(numeroPratica).append(";");
							sb.append(lat).append(";").append(lng).append(";");
							sb.append("\r\n");
						}
					}
				}
			} else if ("WORKDISEASE".equals(areaCode)) {
				Map malattiaProfessionale = (Map)map.get("malattiaProfessionale");
				if (malattiaProfessionale != null && malattiaProfessionale.get("internalId") != null) {
					Criteria dmCrit = ca.createCriteria(DitteMalattie.class);
					dmCrit.add(Restrictions.eq("malattiaProfessionale.internalId", malattiaProfessionale.get("internalId")));
					List<DitteMalattie> dms = dmCrit.list();

					if(dms==null)
						continue;

					for (DitteMalattie dm: dms) {
						Sedi sede = dm.getSedi();
						String lat = sede!=null?sede.getLatitudine():null;
						String lng = sede!=null?sede.getLongitudine():null;
						if (sede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
							sb.append(numeroPratica).append(";");
							sb.append(lat).append(";").append(lng).append(";");
							sb.append("\r\n");
						}
					}
				}
			}
		}
		exportCsv.setFilename("PRATICHE_" + fb.formatDate(new Date(), "yyyyMMdd") + ".csv");
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb.toString().getBytes());

		ImpiantiDocumentAction ida = ImpiantiDocumentAction.instance();
		ida.inject(exportCsv);
	}

	public void generateCoordinateExportFileSenzaFullList(PagedDataModel procpraticheList){
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) procpraticheList.getFullList();
	
		ImpiantiDocument exportCsv = new ImpiantiDocument();
		StringBuffer sb = new StringBuffer();
	
		FunctionsBean fb = FunctionsBean.instance();
	
		for(HashMap<String, Object> map : list){
			String numeroPratica = (String) map.get("numero");
	
			HashMap<String, Object> sdl = (HashMap<String, Object>) map.get("serviceDeliveryLocation");
			CodeValuePhi area = (CodeValuePhi) sdl.get("area");
			String areaCode = area.getCode();
	
			HashMap<String, Object> vigilanza = (HashMap<String, Object>) map.get("vigilanza");
	
			if ("WORKACCIDENT".equals(areaCode)) {
				Criteria infCrit = ca.createCriteria(Infortuni.class);
				// .setProjection(Projections.projectionList().add(Projections.property("infortuniExt")));
				infCrit.add(Restrictions.eq("procpratiche.internalId", map.get("internalId")));
				List<Infortuni> infortuni = infCrit.list();
	
				if(infortuni==null)
					continue;
	
				for (Infortuni infortunio: infortuni) {
					CodeValue place = infortunio.getPlace();
					InfortuniExt infortuniExt = infortunio.getInfortuniExt();
					String lat = infortuniExt.getLatitudine();
					String lng = infortuniExt.getLongitudine();
					if (place!=null && infortuniExt!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(lat).append(";").append(lng).append(";");
						sb.append("\r\n");
					}
				}
			} else if ("SUPERVISION".equals(areaCode) && vigilanza!=null) {
				HashMap<String, Object> type = (HashMap<String, Object>) vigilanza.get("type");
				String typeCode = (String)(type.get("code"));
	
				String latVigilanza = (String) vigilanza.get("latitudine");
				String lngVigilanza = (String) vigilanza.get("longitudine");
	
				HashMap<String, Object> cantiere = (HashMap<String, Object>) vigilanza.get("cantiere");
				String latCantiere = (String) cantiere.get("latitudine");
				String lngCantiere = (String) cantiere.get("longitudine");
	
				if ("Asbestos".equals(typeCode)) {
					HashMap<String, Object> sitoBonificaSede = (HashMap<String, Object>) vigilanza.get("sitoBonificaSede");
					String lat = (String) sitoBonificaSede.get("latitudine");
					String lng = (String) sitoBonificaSede.get("longitudine");
					if (sitoBonificaSede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(lat).append(";").append(lng).append(";");
						sb.append("\r\n");
					} else if (cantiere!=null && latCantiere!=null && !latCantiere.isEmpty() && lngCantiere!=null && !lngCantiere.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(latCantiere).append(";").append(lngCantiere).append(";");
						sb.append("\r\n");
					}  else if (latVigilanza!=null && !latVigilanza.isEmpty() && lngVigilanza!=null && !lngVigilanza.isEmpty()) {
						sb.append(numeroPratica).append(";");
						sb.append(latVigilanza).append(";").append(lngVigilanza).append(";");
						sb.append("\r\n");
					}
				} else if ("Yard".equals(typeCode) && latCantiere!=null && !latCantiere.isEmpty() && lngCantiere!=null && !lngCantiere.isEmpty()) {
					sb.append(numeroPratica).append(";");
					sb.append(latCantiere).append(";").append(lngCantiere).append(";");
					sb.append("\r\n");
				} else if ("Generic".equals(typeCode)) {
					Criteria pgCrit = ca.createCriteria(PersonaGiuridicaSede.class);
					pgCrit.add(Restrictions.eq("vigilanza.internalId", vigilanza.get("internalId")));
					List<PersonaGiuridicaSede> pgs = pgCrit.list();
	
					if(pgs==null)
						continue;
	
					for (PersonaGiuridicaSede pg: pgs) {
						Sedi sede = pg.getSede();
						String lat = sede!=null?sede.getLatitudine():null;
						String lng = sede!=null?sede.getLongitudine():null;
						if (sede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
							sb.append(numeroPratica).append(";");
							sb.append(lat).append(";").append(lng).append(";");
							sb.append("\r\n");
						}
					}
				}
			} else if ("WORKDISEASE".equals(areaCode)) {
				Map malattiaProfessionale = (Map)map.get("malattiaProfessionale");
				if (malattiaProfessionale != null && malattiaProfessionale.get("internalId") != null) {
					Criteria dmCrit = ca.createCriteria(DitteMalattie.class);
					dmCrit.add(Restrictions.eq("malattiaProfessionale.internalId", malattiaProfessionale.get("internalId")));
					List<DitteMalattie> dms = dmCrit.list();
	
					if(dms==null)
						continue;
	
					for (DitteMalattie dm: dms) {
						Sedi sede = dm.getSedi();
						String lat = sede!=null?sede.getLatitudine():null;
						String lng = sede!=null?sede.getLongitudine():null;
						if (sede!=null && lat!=null && !lat.isEmpty() && lng!=null && !lng.isEmpty()) {
							sb.append(numeroPratica).append(";");
							sb.append(lat).append(";").append(lng).append(";");
							sb.append("\r\n");
						}
					}
				}
			}
		}
		exportCsv.setFilename("PRATICHE_" + fb.formatDate(new Date(), "yyyyMMdd") + ".csv");
		exportCsv.setContentType("text/csv");
		exportCsv.setContent(sb.toString().getBytes());
	
		ImpiantiDocumentAction ida = ImpiantiDocumentAction.instance();
		ida.inject(exportCsv);
	}
}