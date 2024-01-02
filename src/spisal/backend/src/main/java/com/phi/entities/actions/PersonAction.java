package com.phi.entities.actions;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.aur.AurHttpClient;
import com.phi.aur.json.AurResponse;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Person;
import com.phi.security.UserBean;
import com.phi.util.CodiceFiscaleGenerator;

@BypassInterceptors
@Name("PersonAction")
@Scope(ScopeType.CONVERSATION)
public class PersonAction extends BaseAction<Person, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1508739192904580929L;
	private static final Logger log = Logger.getLogger(OperatoreAction.class);
	private static String QUERY_PRATICHE = "select distinct d.pratica_id from persone_pratiche d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String QUERY_PROVVEDIMENTI = "select distinct d.prov_id from persone_provvedimenti d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String HAS_PRATICHE = "select count(distinct d.pratica_id) from persone_pratiche d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String HAS_PROVVEDIMENTI = "select count(distinct d.prov_id) from persone_provvedimenti d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private Query queryHasPratiche,queryPratiche,queryProvvedimenti,queryHasProvvedimenti;

	public void checkPratiche(Long personaId){
		if(personaId!=null && personaId>0){
			String qryPratiche = "SELECT DISTINCT p FROM Procpratiche p " +
					"LEFT JOIN p.infortuni i LEFT JOIN i.person p1 " +
					"LEFT JOIN p.vigilanza v LEFT JOIN v.personaRuolo vp LEFT JOIN vp.person p2 " +
					"LEFT JOIN p.attivita a LEFT JOIN a.luogoUtente p3 " +
					"LEFT JOIN a.soggetto s LEFT JOIN s.utente p4 " +
					"LEFT JOIN p.malattiaProfessionale m LEFT JOIN m.riferimentoUtente p5 LEFT JOIN m.richiedenteUtente p6 " +
					"LEFT JOIN p.parereTecnico t LEFT JOIN t.person p7 " +
					"LEFT JOIN p.protocollo pr " +
					"LEFT JOIN pr.riferimentoUtente p8 " +
					"LEFT JOIN pr.richiedenteUtente p9 " +
					"LEFT JOIN pr.ubicazioneUtente p10 " +
					"WHERE p1.internalId = :id OR p2.internalId = :id OR p3.internalId = :id OR p4.internalId = :id " +
					"OR p5.internalId = :id OR p6.internalId = :id OR p7.internalId = :id OR p8.internalId = :id " +
					"OR p9.internalId = :id OR p10.internalId = :id";

			Query qPratiche = ca.createQuery(qryPratiche);
			qPratiche.setParameter("id", personaId);
			List<Procpratiche> lst = qPratiche.getResultList();
			if(lst!=null && !lst.isEmpty()){
				ProcpraticheAction pAction = ProcpraticheAction.instance();
				pAction.injectList(lst);
				getTemporary().put("hasPratiche",true);
			}
		}
	}

	public static PersonAction instance() {
		return (PersonAction) Component.getInstance(PersonAction.class, ScopeType.CONVERSATION);
	}

	@Create
	public void init() throws PhiException{
		queryHasPratiche = ca.createNativeQuery(HAS_PRATICHE);
		queryPratiche = ca.createNativeQuery(QUERY_PRATICHE);
		queryProvvedimenti = ca.createNativeQuery(QUERY_PROVVEDIMENTI);
		queryHasProvvedimenti = ca.createNativeQuery(HAS_PROVVEDIMENTI);
	}

	public void setOrganization() {
		try {

			HashMap<String, Object> temp = getTemporary();

			if (!temp.isEmpty()){
				Object organizatio_id = temp.get("selectedULSS");

				if (organizatio_id != null) {
					Long id = Long.parseLong(organizatio_id.toString());
					((FilterMap)getEqual()).put("currentOrg.internalId", id);
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

	public List<Person> aurSearch(String nomeFam, String nome, String codiceFiscale, String mpi, String cs, String stp,
			String eni, String team, Date dob){

		Vocabularies voc = VocabulariesImpl.instance();

		String birthPlaceCode = null;
		HashMap<String, Object> equals = getEqual();

		if (equals!=null && equals.get("birthPlace.code")!=null)
			birthPlaceCode = ((CodeValue)equals.get("birthPlace.code")).getCode();

		List<com.phi.aur.json.Person> aurPersons;
		List<com.phi.aur.json.Error> aurErrors;
		List<Person> persons = new ArrayList<Person>();

		AurResponse resp =  AurHttpClient.instance().aurSearch(nomeFam, nome, codiceFiscale, mpi, cs, stp, eni, team, dob, birthPlaceCode); 
		aurPersons = resp.getPersons();
		aurErrors = resp.getErrors();

		if(aurErrors==null || aurErrors.size()==0){
			if (temporary != null && temporary.containsKey("ERROR"))
				temporary.remove("ERROR"); //clear possible previous error
		} else {
			temporary.put("exception", "ERRORE! AUR non disponibile: " + aurErrors.get(0).getDescription());
			return null;
		}

		if(aurPersons!=null){
			for(com.phi.aur.json.Person ap:aurPersons){

				Person p = new Person();

				EN name = new EN();
				name.setFam(ap.getNameFam());
				name.setGiv(ap.getNameGiv());
				p.setName(name);

				p.setFiscalCode(ap.getFiscalCode());
				p.setMpi(ap.getMpi());
				p.setCs(ap.getCs());
				p.setStp(ap.getStp());
				p.setEni(ap.getEni());
				p.setTeamCode(ap.getTeamCode());

				try {
					p.setGenderCode((CodeValuePhi)voc.getCodeValue("PHIDIC", "Gender", ap.getGenderCode(), "C"));
				} catch (PersistenceException e) {
					log.error(e.getMessage());
				} catch (DictionaryException e) {
					log.error(e.getMessage());
				}

				String expectedPattern = "yyyy-MM-dd";
				SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

				try { 
					p.setBirthTime(formatter.parse(ap.getBirthTime()));
				} catch (ParseException e) {
					log.error(e.getMessage());
				}

				AD addrDom = new AD();
				try {
					String CountryOfDom = ap.getCountryOfDom();

					if (CountryOfDom!=null && CountryOfDom.equals("100")){
						String domAddrCode = ap.getDomAddrCode();
						if (domAddrCode!=null && domAddrCode.length()==6){
							CodeValueCity cityDomCv = (CodeValueCity) voc.getCodeValue("Comuni", "", domAddrCode,"C");
							if (cityDomCv != null) { 
								addrDom.setCty(cityDomCv.getCurrentTranslation());
								addrDom.setCode(cityDomCv);
								addrDom.setCpa(cityDomCv.getProvince());
								addrDom.setZip(cityDomCv.getZip());
							}
						}
					}
					if(CountryOfDom!=null && !CountryOfDom.isEmpty()){
						CodeValue cv = voc.getCodeValue("Stati", "", CountryOfDom, "C");
						if (cv != null) {
							addrDom.setSta(cv.getCurrentTranslation());
						}
					}

				} catch (PersistenceException e) {
					log.error(e.getMessage());
				} catch (DictionaryException e) {
					log.error(e.getMessage());
				}
				addrDom.setStr(ap.getDomAddrStr());
				addrDom.setBnr(ap.getDomAddrBnr());
				p.setDomicileAddr(addrDom);


				AD addrRes = new AD();
				try {
					String CountryOfAddr = ap.getCountryOfAddr();

					if (CountryOfAddr!=null && CountryOfAddr.equals("100")){
						String addrCode = ap.getAddrCode();
						if (addrCode!=null && addrCode.length()==6){
							CodeValueCity cityCv = (CodeValueCity) voc.getCodeValue("Comuni", "", addrCode,"C");
							if (cityCv != null) {
								addrRes.setCty(cityCv.getCurrentTranslation());
								addrRes.setCode(cityCv);
								addrRes.setCpa(cityCv.getProvince());
								addrRes.setZip(cityCv.getZip());
							}
						}
					}
					if(CountryOfAddr!=null && !CountryOfAddr.isEmpty()){
						CodeValue cv = voc.getCodeValue("Stati", "", CountryOfAddr,"C");
						if (cv != null) {
							addrRes.setSta(cv.getCurrentTranslation());
						}
					}

				} catch (PersistenceException e) {
					log.error(e.getMessage());
				} catch (DictionaryException e) {
					log.error(e.getMessage());
				}

				addrRes.setStr(ap.getAddrStr());
				addrRes.setBnr(ap.getAddrBnr());
				p.setAddr(addrRes);

				AD addrBirth = new AD();
				try {

					String countryOfBirth = ap.getCountryOfBirth();

					if(countryOfBirth!=null && countryOfBirth.equals("100")){
						String birthplaceCode = ap.getBirthplaceCode();
						if (birthplaceCode!=null && birthplaceCode.length()==6){
							CodeValueCity cityBirthCv = (CodeValueCity) voc.getCodeValue("Comuni", null, birthplaceCode,"C");
							if (cityBirthCv != null) {
								addrBirth.setCty(cityBirthCv.getCurrentTranslation());
								addrBirth.setCode(cityBirthCv);
								addrBirth.setCpa(cityBirthCv.getProvince());
								addrBirth.setZip(cityBirthCv.getZip());
							}
						}
					}

					if(countryOfBirth!=null && !countryOfBirth.isEmpty()) {
						CodeValue cv = voc.getCodeValue("Stati", null, countryOfBirth,"C");
						if (cv != null) {
							addrBirth.setStr(cv.getCurrentTranslation());
						}
					}

				} catch (PersistenceException e) {
					log.error(e.getMessage());
				} catch (DictionaryException e) {
					log.error(e.getMessage());
				}

				p.setBirthPlace(addrBirth);

				TEL telecom = new TEL();
				telecom.setH(ap.getTelecomH());
				telecom.setHp(ap.getTelecomHp());
				telecom.setBad(ap.getTelecomBad());
				telecom.setMc(ap.getTelecomMc());
				telecom.setMail(ap.getTelecomMail());
				telecom.setTmp(ap.getTelecomTmp());
				telecom.setEc(ap.getTelecomEc());
				telecom.setPg(ap.getTelecomPg());
				p.setTelecom(telecom);

				persons.add(p);
			}
		}

		if(persons.size()==10)
			temporary.put("exception","I risultati visualizzati sono al massimo 10, ma potrebbero essercene di più. Filtrare ulteriormente la ricerca.");

		return persons; 

	}

	public boolean aurUpdate(Person p) {

		Vocabularies voc = VocabulariesImpl.instance();
		List<com.phi.aur.json.Person> aurPersons;
		List<com.phi.aur.json.Error> aurErrors;

		AurResponse resp =  AurHttpClient.instance().aurUpdate(p.getName().getFam(), p.getName().getGiv(), p.getFiscalCode(), p.getBirthTime());

		aurPersons = resp.getPersons();
		aurErrors = resp.getErrors();

		if(aurErrors==null || aurErrors.size()==0){
			temporary.remove("ERROR");
		} else {
			temporary.put("exception", "ERRORE! AUR non disponibile: " + aurErrors.get(0).getDescription());
			return false;
		}

		if(aurPersons==null || aurPersons.size()!=1){
			temporary.put("exception", "ERRORE! Posizione anagrafica non presente in AUR");// + aurErrors.get(0).getDescription());
			return false;
		}

		com.phi.aur.json.Person ap = aurPersons.get(0);

		//		EN name = new EN();
		//		name.setFam(ap.getNameFam());
		//		name.setGiv(ap.getNameGiv());
		//		p.setName(name);

		//		p.setFiscalCode(ap.getFiscalCode());

		if(ap.getMpi()!=null && !"".equals(ap.getMpi()))
			p.setMpi(ap.getMpi());

		if(ap.getCs()!=null && !"".equals(ap.getCs()))
			p.setCs(ap.getCs());

		if(ap.getStp()!=null && !"".equals(ap.getStp()))
			p.setStp(ap.getStp());

		if(ap.getEni()!=null && !"".equals(ap.getEni()))
			p.setEni(ap.getEni());

		if(ap.getTeamCode()!=null && !"".equals(ap.getTeamCode()))
			p.setTeamCode(ap.getTeamCode());

		if(ap.getGenderCode()!=null && !"".equals(ap.getGenderCode())){
			try {
				p.setGenderCode((CodeValuePhi)voc.getCodeValue("PHIDIC", "Gender", ap.getGenderCode(), "C"));
			} catch (PersistenceException e) {
				log.error(e.getMessage());
			} catch (DictionaryException e) {
				log.error(e.getMessage());
			}
		}

		//		String expectedPattern = "yyyy-MM-dd";
		//		SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
		//
		//		try { 
		//			p.setBirthTime(formatter.parse(ap.getBirthTime()));
		//		} catch (ParseException e) {
		//			log.error(e.getMessage());
		//		}

		AD addrDom = new AD();
		try {
			String CountryOfDom = ap.getCountryOfDom();

			if (CountryOfDom!=null && CountryOfDom.equals("100")){
				String domAddrCode = ap.getDomAddrCode();
				if (domAddrCode!=null && domAddrCode.length()==6){
					CodeValueCity cityDomCv = (CodeValueCity) voc.getCodeValue("Comuni", "", domAddrCode,"C");
					if (cityDomCv != null) { 
						addrDom.setCty(cityDomCv.getCurrentTranslation());
						addrDom.setCode(cityDomCv);
						addrDom.setCpa(cityDomCv.getProvince());
						addrDom.setZip(cityDomCv.getZip());
					}
				}
			}
			CodeValue cv = voc.getCodeValue("Stati", "", CountryOfDom, "C");
			if (cv != null) {
				addrDom.setSta(cv.getCurrentTranslation());
			}

		} catch (PersistenceException e) {
			log.error(e.getMessage());
		} catch (DictionaryException e) {
			log.error(e.getMessage());
		}
		addrDom.setStr(ap.getDomAddrStr());
		addrDom.setBnr(ap.getDomAddrBnr());
		p.setDomicileAddr(addrDom);


		AD addrRes = new AD();
		try {
			String CountryOfAddr = ap.getCountryOfAddr();

			if (CountryOfAddr!=null && CountryOfAddr.equals("100")){
				String addrCode = ap.getAddrCode();
				if (addrCode!=null && addrCode.length()==6){
					CodeValueCity cityCv = (CodeValueCity) voc.getCodeValue("Comuni", "", addrCode,"C");
					if (cityCv != null) {
						addrRes.setCty(cityCv.getCurrentTranslation());
						addrRes.setCode(cityCv);
						addrRes.setCpa(cityCv.getProvince());
						addrRes.setZip(cityCv.getZip());
					}
				}
			}
			CodeValue cv = voc.getCodeValue("Stati", "", CountryOfAddr,"C");
			if (cv != null) {
				addrRes.setSta(cv.getCurrentTranslation());
			}

		} catch (PersistenceException e) {
			log.error(e.getMessage());
		} catch (DictionaryException e) {
			log.error(e.getMessage());
		}

		addrRes.setStr(ap.getAddrStr());
		addrRes.setBnr(ap.getAddrBnr());
		p.setAddr(addrRes);

		AD addrBirth = new AD();
		try {

			String countryOfBirth = ap.getCountryOfBirth();

			if(countryOfBirth!=null && countryOfBirth.equals("100")){
				String birthplaceCode = ap.getBirthplaceCode();
				if (birthplaceCode!=null && birthplaceCode.length()==6){
					CodeValueCity cityBirthCv = (CodeValueCity) voc.getCodeValue("Comuni", null, birthplaceCode,"C");
					if (cityBirthCv != null) {
						addrBirth.setCty(cityBirthCv.getCurrentTranslation());
						addrBirth.setCode(cityBirthCv);
						addrBirth.setCpa(cityBirthCv.getProvince());
						addrBirth.setZip(cityBirthCv.getZip());
					}
				}
			}

			if(countryOfBirth!=null && !countryOfBirth.isEmpty()) {
				CodeValue cv = voc.getCodeValue("Stati", null, countryOfBirth,"C");
				if (cv != null) {
					addrBirth.setStr(cv.getCurrentTranslation());
				}
			}

		} catch (PersistenceException e) {
			log.error(e.getMessage());
		} catch (DictionaryException e) {
			log.error(e.getMessage());
		}

		p.setBirthPlace(addrBirth);

		TEL telecom = new TEL();

		if(ap.getTelecomH()!=null && !"".equals(ap.getTelecomH()))
			telecom.setH(ap.getTelecomH());

		if(ap.getTelecomHp()!=null && !"".equals(ap.getTelecomHp()))
			telecom.setHp(ap.getTelecomHp());

		if(ap.getTelecomBad()!=null && !"".equals(ap.getTelecomBad()))
			telecom.setBad(ap.getTelecomBad());

		if(ap.getTelecomMc()!=null && !"".equals(ap.getTelecomMc()))
			telecom.setMc(ap.getTelecomMc());

		if(ap.getTelecomMail()!=null && !"".equals(ap.getTelecomMail()))
			telecom.setMail(ap.getTelecomMail());

		if(ap.getTelecomTmp()!=null && !"".equals(ap.getTelecomTmp()))
			telecom.setTmp(ap.getTelecomTmp());

		if(ap.getTelecomEc()!=null && !"".equals(ap.getTelecomEc()))
			telecom.setEc(ap.getTelecomEc());

		if(ap.getTelecomPg()!=null && !"".equals(ap.getTelecomPg()))
			telecom.setPg(ap.getTelecomPg());

		p.setTelecom(telecom);

		this.inject(p);
		return true;
	}

	@SuppressWarnings("unused")
	public void aurImport(Person p){

		List<com.phi.aur.json.Person> aurPersons = new ArrayList<com.phi.aur.json.Person>();
		List<com.phi.aur.json.Error> aurErrors = new ArrayList<com.phi.aur.json.Error>();
		List<Person> persons = new ArrayList<Person>();

		AurResponse resp =  AurHttpClient.instance().aurImport(p.getMpi()); 
		aurPersons = resp.getPersons();
		aurErrors = resp.getErrors();

		if(aurErrors==null || aurErrors.size()==0){
			temporary.remove("ERROR"); //clear possible previous error
			temporary.remove("exception");

			if(resp!=null && resp.getInternalId()!=null && !resp.getInternalId().isEmpty()){
				long internalIdNew=Long.parseLong(resp.getInternalId());

				Person newPerson = ca.get(Person.class, internalIdNew); 
				inject(newPerson);
			}else{
				String errorFacesMessage = "Non è stato possibile importare l'anagrafica selezionata: risposta all'interrogazione AUR vuota.";
				//FacesErrorUtils.addErrorMessage("commError", errorFacesMessage, errorFacesMessage);
				temporary.put("exception", errorFacesMessage);
			}
		} else
			//handle connection error for visualization in phi
			temporary.put("exception", "ERRORE: " + aurErrors.get(0).getCode()+", "+aurErrors.get(0).getDescription());
	}

	/**
	 * Questa logica controlla se la Person p ricercata in AUR è già presente in anagrafica locale
	 * Effettua in cascata prima la ricerca per mpi, e se con esito negativo la ricerca per 
	 * nome cognome e codice fiscale. Se viene trovata una riga in anagrafica locale, inibisce la selezione 
	 * dell'anagrafica da AUR
	 * @param p
	 */
	public void checkAnagraficaLocale(Person p){
		getTemporary().put("giaInAnagrafica",false);
		if(p!=null){
			if(p.getMpi()!=null){
				String hqlMpi = "SELECT COUNT(p) FROM Person p WHERE p.mpi = :mpi AND p.isActive=1";
				Query qMpi = ca.createQuery(hqlMpi);
				qMpi.setParameter("mpi", p.getMpi());
				Long count = (Long)qMpi.getSingleResult();
				if(count!=null && count>0){
					getTemporary().put("giaInAnagrafica",true);
					return;
				}
			}
			if(p.getName()!=null){
				String hqlNameSurname = "SELECT COUNT(p) FROM Person p WHERE LOWER(p.name.fam) = :cognome AND LOWER(p.name.giv) = :nome AND p.fiscalCode = :cf" +
						" AND p.isActive=1";
				Query qNameSurname = ca.createQuery(hqlNameSurname);
				if(p.getName().getFam()!=null)
					qNameSurname.setParameter("cognome", p.getName().getFam().toLowerCase());
				else
					qNameSurname.setParameter("cognome", "");

				if(p.getName().getGiv()!=null)
					qNameSurname.setParameter("nome", p.getName().getGiv().toLowerCase());
				else
					qNameSurname.setParameter("nome", "");

				qNameSurname.setParameter("cf", p.getFiscalCode());

				Long count = (Long)qNameSurname.getSingleResult();
				if(count!=null && count>0){
					getTemporary().put("giaInAnagrafica",true);
					return;
				}
			}
		}
	}

	public void generateCodiceFiscale(){

		String cf = CodiceFiscaleGenerator.instance().calcoloCodiceFiscale(getEntity());
		getEntity().setFiscalCode(cf);

	}

	public void checkPratiche(Object o){
		if (o instanceof Map<?,?> ){
			Map<String,Object> person = (Map<String,Object>)o;
			person.put("numPratiche", checkPratiche((String)person.get("fiscalCode"))[0].intValue());
			person.put("numProvvedimenti", checkPratiche((String)person.get("fiscalCode"))[1].intValue());

		}
	}

	public Long[] checkPratiche(String cf){
		Long[] ret = new Long[]{0L,0L};

		if(cf!=null && !"".equals(cf)){
			queryHasPratiche.setParameter("cf", cf);
			queryHasPratiche.setParameter("sdlocIds", UserBean.instance().getSdLocs());
			BigInteger countPrat = (BigInteger) queryHasPratiche.getSingleResult();
			if(countPrat!=null){
				ret[0]=countPrat.longValue();
			}

			queryHasProvvedimenti.setParameter("cf", cf);
			queryHasProvvedimenti.setParameter("sdlocIds", UserBean.instance().getSdLocs());
			BigInteger countProv = (BigInteger) queryHasProvvedimenti.getSingleResult();
			if(countProv!=null){
				ret[1]=countProv.longValue();
			}
		}

		return ret;
	}

	public void filterMisc() throws NoSuchFieldException, SecurityException{
		getTemporary().put("hasPraticheIds",new ArrayList<Long>());
		getIn().remove("internalId");

		boolean conPratiche = Boolean.TRUE.equals(getTemporary().get("conPratiche"));
		boolean conProvvedimenti = Boolean.TRUE.equals(getTemporary().get("conProvvedimenti"));

		removeExpression("this", "PersonePratiche.sdlId");
		removeSubCriteria(entityCriteria, "PersonePratiche");
		if(conPratiche){
			entityCriteria.createAlias("personePratiche", "PersonePratiche", Criteria.INNER_JOIN);
			entityCriteria.add(Property.forName("PersonePratiche.sdlId").in(UserBean.instance().getSdLocs()));
			//entityCriteria.add(Restrictions.eqProperty("PersonePratiche.cf", "this.fiscalCode"));
		}
		removeExpression("this", "PersoneProvvedimenti.sdlId");
		removeSubCriteria(entityCriteria, "PersoneProvvedimenti");
		if(conProvvedimenti){
			entityCriteria.createAlias("personeProvvedimenti", "PersoneProvvedimenti", Criteria.INNER_JOIN);
			entityCriteria.add(Property.forName("PersoneProvvedimenti.sdlId").in(UserBean.instance().getSdLocs()));
			//entityCriteria.add(Restrictions.eqProperty("PersoneProvvedimenti.cf", "this.fiscalCode"));
		}
		/*removeExpression("this", "org.hibernate.criterion.ExistsSubqueryExpression");
		if(conPratiche){
			DetachedCriteria personePraticheCriteria = DetachedCriteria.forClass(PersonePratiche.class,"PersonePratiche");
			personePraticheCriteria.add(Property.forName("PersonePratiche.cf").eqProperty("this.fiscalCode"));
			personePraticheCriteria.add(Property.forName("PersonePratiche.sdlId").in(UserBean.instance().getSdLocs()));
			personePraticheCriteria.setProjection(Projections.property("PersonePratiche.internalId"));
			entityCriteria.add(Subqueries.exists(personePraticheCriteria));
		}

		if(conProvvedimenti){
			DetachedCriteria personeProvvedimentiCriteria = DetachedCriteria.forClass(PersoneProvvedimenti.class,"PersoneProvvedimenti");
			personeProvvedimentiCriteria.add(Property.forName("PersoneProvvedimenti.cf").eqProperty("this.fiscalCode"));
			personeProvvedimentiCriteria.add(Property.forName("PersoneProvvedimenti.sdlId").in(UserBean.instance().getSdLocs()));
			personeProvvedimentiCriteria.setProjection(Projections.property("PersoneProvvedimenti.internalId"));
			entityCriteria.add(Subqueries.exists(personeProvvedimentiCriteria));
		}*/
	}

	public List<Long> listProvvedimenti(Person person){
		return listProvvedimenti(person.getFiscalCode());
	}

	public List<Long> listProvvedimenti(String cf){
		List<Long> result = new ArrayList<Long>();
		if(cf==null)
			cf="";

		queryProvvedimenti.setParameter("cf", cf);
		queryProvvedimenti.setParameter("sdlocIds", UserBean.instance().getSdLocs());
		List<BigInteger> resultSet = queryProvvedimenti.getResultList();
		if(resultSet!=null && !resultSet.isEmpty()){
			for(BigInteger b : resultSet){
				result.add(b.longValue());
			}
		}else{
			result.add(-1L);
		}

		return result;
	}

	public List<Long> listPratiche(Person person){
		return listPratiche(person.getFiscalCode());
	}

	public List<Long> listPratiche(String cf){
		List<Long> result = new ArrayList<Long>();
		if(cf==null)
			cf="";

		queryPratiche.setParameter("cf", cf);
		queryPratiche.setParameter("sdlocIds", UserBean.instance().getSdLocs());
		List<BigInteger> resultSet = queryPratiche.getResultList();
		if(resultSet!=null && !resultSet.isEmpty()){
			for(BigInteger b : resultSet){
				result.add(b.longValue());
			}
		}else{
			result.add(-1L);
		}

		return result;
	}
	/*
	// I0049488
	public Person copy(Person oldPerson){

		Person newPerson = null;
		if(oldPerson!=null){
			newPerson = new Person();

			newPerson.setId(oldPerson.getId());

			newPerson.setBirthTime(oldPerson.getBirthTime());
			newPerson.setCategory(oldPerson.getCategory());
			newPerson.setCitizen(oldPerson.getCitizen());
			newPerson.setCode(oldPerson.getCode());
			newPerson.setCountryOfAddr(oldPerson.getCountryOfAddr());
			newPerson.setCountryOfBirth(oldPerson.getCountryOfBirth());
			newPerson.setCountryOfDomicile(oldPerson.getCountryOfDomicile());
			newPerson.setCs(oldPerson.getCs());
			newPerson.setCsRegion(oldPerson.getCsRegion());
			newPerson.setCurrentOrg(oldPerson.getCurrentOrg());
			newPerson.setOriginalOrg(oldPerson.getOriginalOrg());
			newPerson.setPhysician(oldPerson.getPhysician());
			newPerson.setDeathDate(oldPerson.getDeathDate());
			newPerson.setDeathIndicator(oldPerson.getDeathIndicator());
			newPerson.setEni(oldPerson.getEni());
			newPerson.setFiscalCode(oldPerson.getFiscalCode());
			newPerson.setGenderCode(oldPerson.getGenderCode());
			newPerson.setHL7MsgDate(oldPerson.getHL7MsgDate());
			newPerson.setMaritalStatusCode(oldPerson.getMaritalStatusCode());
			newPerson.setMaster(oldPerson.getMaster());

			newPerson.setReliability(oldPerson.getReliability());
			newPerson.setRev(oldPerson.getRev());
			newPerson.setStp(oldPerson.getStp());
			newPerson.setTeamCode(oldPerson.getTeamCode());
			newPerson.setTeamIdent(oldPerson.getTeamIdent());
			newPerson.setTeamInst(oldPerson.getTeamInst());
			newPerson.setTeamPers(oldPerson.getTeamPers());
			newPerson.setToUpdate(oldPerson.getToUpdate());
			newPerson.setValidCF(oldPerson.getValidCF());

			if(oldPerson.getName()!=null){
				newPerson.setName(oldPerson.getName().cloneEN());
			}
			if(oldPerson.getBirthPlace()!=null){
				newPerson.setBirthPlace(oldPerson.getBirthPlace().cloneAd());
			}
			if(oldPerson.getTelecom()!=null){
				newPerson.setTelecom(oldPerson.getTelecom().cloneTel());
			}
			if(oldPerson.getTeamDate()!=null){
				newPerson.setTeamDate(oldPerson.getTeamDate());
			}
			if(oldPerson.getStpDate()!=null){
				newPerson.setStpDate(oldPerson.getStpDate());
			}
			if(oldPerson.getCsDate()!=null){
				newPerson.setCsDate(oldPerson.getCsDate());
			}
			if(oldPerson.getAlternativeAddr()!=null){
				newPerson.setAlternativeAddr(oldPerson.getAlternativeAddr().cloneAddr());
			}

			if(oldPerson.getProfessionalSituation()!=null){
				SituazioneProfessionale newSit = new SituazioneProfessionale();
				SituazioneProfessionale oldSit = oldPerson.getProfessionalSituation();

				newSit.setNote(oldSit.getNote());
				newSit.setPerson(newPerson);
				newSit.setType(oldSit.getType());
				newSit.setValidFrom(oldSit.getValidFrom());
				newSit.setValidTo(oldSit.getValidTo());

				newPerson.setProfessionalSituation(newSit);
			}

			if(oldPerson.getAddr()!=null)
				newPerson.setAddr(oldPerson.getAddr().cloneAd());
			if(oldPerson.getDomicileAddr()!=null)
				newPerson.setDomicileAddr(oldPerson.getDomicileAddr().cloneAd());
			if(oldPerson.getEniDate()!=null)
				newPerson.setEniDate(oldPerson.getEniDate().cloneIVL());

			oldPerson.setIsActive(false);
		}

		return newPerson;
	}


	// I0049488
	public boolean checkDuplicate(Person p){
		if(p.getFiscalCode()!=null){
			String hqlFiscalCode = "SELECT COUNT(p) FROM Person p WHERE p.fiscalCode = :cf";
			Query qFiscalCode = ca.createQuery(hqlFiscalCode);

			qFiscalCode.setParameter("cf", p.getFiscalCode());

			Long count = (Long)qFiscalCode.getSingleResult();
			if(count!=null && count>0){
				return true;
			}		
		}else{
			String hqlFullName = "SELECT COUNT(p) FROM Person p WHERE LOWER(p.name.fam) = :cognome AND LOWER(p.name.giv) = :nome AND p.birthTime = :birth";
			Query qFullName = ca.createQuery(hqlFullName);

			if(p.getName().getFam()!=null)
				qFullName.setParameter("cognome", p.getName().getFam().toLowerCase());
			else
				qFullName.setParameter("cognome", "");

			if(p.getName().getGiv()!=null)
				qFullName.setParameter("nome", p.getName().getGiv().toLowerCase());
			else
				qFullName.setParameter("nome", "");	

			if(p.getBirthTime()!=null){
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//String paramBirth = sdf.format(p.getBirthTime());
				qFullName.setParameter("birth", p.getBirthTime());				
			}
			else
				qFullName.setParameter("birth", null);	

			Long count = (Long)qFullName.getSingleResult();
			if(count!=null && count>0){
				return true;
			}			
		}
		return false;
	}*/
}