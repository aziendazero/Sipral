package com.phi.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.aur.AurHttpClient;
import com.phi.aur.json.AurResponse;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.sequence.SequenceManager;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.BaseAction.FilterMap;
import com.phi.entities.actions.CantiereAction;
import com.phi.entities.actions.CodeValueParameterAction;
import com.phi.entities.actions.CommittenteAction;
import com.phi.entities.actions.DitteCantiereAction;
import com.phi.entities.actions.GruppiAction;
import com.phi.entities.actions.PNCDitteCantiereAction;
import com.phi.entities.actions.PNCPecNotificaDestAction;
import com.phi.entities.actions.PNCPersoneCantiereAction;
import com.phi.entities.actions.PersonAction;
import com.phi.entities.actions.PersoneCantiereAction;
import com.phi.entities.actions.PersoneGiuridicheAction;
import com.phi.entities.actions.ServiceDeliveryLocationAction;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.baseEntity.PNCCantiere;
import com.phi.entities.baseEntity.PNCDitteCantiere;
import com.phi.entities.baseEntity.PNCPecNotificaDest;
import com.phi.entities.baseEntity.PNCPersoneCantiere;
import com.phi.entities.baseEntity.PersoneCantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parix.ParixHttpClient;
import com.phi.parix.json.detail.DatiImpresa;
import com.phi.parix.json.detail.EstremiImpresa;
import com.phi.parix.json.detail.Localizzazione;

@BypassInterceptors
@Name("PNCCantiereRest")
@Scope(ScopeType.EVENT)
@Path("/pnc")
public class PNCCantiereRest {

	private boolean pncForced = false;

	private CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") long id) {

		ResponseBuilder responseBuilder = null;

		PNCCantiere pncCantiere = null;

		for(int i=0;i<5;i++){
			try{
				pncCantiere = ca.get(PNCCantiere.class, id);
				if (pncCantiere instanceof HibernateProxy) {
					pncCantiere = (PNCCantiere)((HibernateProxy)pncCantiere).getHibernateLazyInitializer().getImplementation();
				}

				responseBuilder = Response.ok();
				break;
			}catch(EntityNotFoundException nre){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(i==4){
					nre.printStackTrace();
					responseBuilder = Response.status(Status.NOT_FOUND);
					return responseBuilder.build();
				}
			}
		}

		try {

			CantiereAction cantAction = CantiereAction.instance();
			cantAction.getEqual().put("isActive", true);
			cantAction.getEqual().put("idPnc", String.valueOf(id));

			List<Cantiere> cantieriList = (List<Cantiere>) cantAction.select();
			if(cantieriList!=null && !cantieriList.isEmpty()){
				return responseBuilder.build();
			}


			CodeValueCity comuneCv = ca.get(CodeValueCity.class, pncCantiere.getAddr().getCode().getId());
			String ulss = comuneCv.getUlss();

			ServiceDeliveryLocationAction sdla = ServiceDeliveryLocationAction.instance();
			sdla.getEqual().put("isActive", true);
			sdla.getEqual().put("organization.id", ulss);
			ServiceDeliveryLocation ulssSdl = (ServiceDeliveryLocation) sdla.select().get(0);

			CodeValueParameter pncIntegration = null;
			try{
				pncIntegration = ca.get(CodeValueParameter.class, "p.general.pncintegration");
				if (pncIntegration instanceof HibernateProxy) {
					pncIntegration = (CodeValueParameter)((HibernateProxy)pncIntegration).getHibernateLazyInitializer().getImplementation();
				}
			}catch(EntityNotFoundException nre){
				responseBuilder = Response.ok("Parameter p.general.pncintegration not found.");
				return responseBuilder.build();

			}

			CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
			HashMap<String, Object> evaluatedParameter = (HashMap<String, Object>)cvpa.evaluate(pncIntegration, ulssSdl.getInternalId());
			String value = evaluatedParameter.get("value").toString();

			if(!Boolean.TRUE.equals(Boolean.parseBoolean(value))){
				responseBuilder = Response.ok();
				return responseBuilder.build();
			}

			Cantiere cantiereSipral = createNewCantiere(pncCantiere);

			PNCPecNotificaDestAction pecNotificaAction = PNCPecNotificaDestAction.instance();
			pecNotificaAction.getEqual().put("isActive", true);
			pecNotificaAction.getEqual().put("cantiere", pncCantiere);

			PNCPecNotificaDest notifica = (PNCPecNotificaDest) pecNotificaAction.select().get(0);

			String distretto = comuneCv.getHealthDistrict();

			String queryDistretto = "SELECT s FROM ServiceDeliveryLocation s JOIN s.id sdlid "
					+ "WHERE s.isActive = 1 AND sdlid.extension = :id";
			Query qd = ca.createQuery(queryDistretto);
			qd.setParameter("id", distretto);
			ServiceDeliveryLocation distrettoSdl = (ServiceDeliveryLocation) qd.getSingleResult();

			Protocollo comunicazioneCantiere = insertComunicazione(notifica, distrettoSdl);
			updateFromPNCCantiere(comunicazioneCantiere, pncCantiere);

			updateFromCantiereSipral(comunicazioneCantiere, cantiereSipral);
			ca.create(comunicazioneCantiere);

		} catch (Exception e) {
			//log.error("[cid="+Conversation.instance().getId()+"] Error importing Cantiere " + e.getMessage(), e);
			StringBuffer sb = new StringBuffer(5000);
			StackTraceElement[] st = e.getStackTrace();
			sb.append(e.getClass().getName() + ": " + e.getMessage() + "\n");
			for (int i = 0; i < st.length; i++) {
				sb.append("\t at " + st[i].toString() + "\n");
			}
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error importing Cantiere " + sb.toString());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}finally{
			try{
				ca.flushSession();
			}catch(Exception exe){}
		}
		return responseBuilder.build(); 
	}

	@GET
	@Path("/download/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getPdf(@PathParam("id") long id) {

		ResponseBuilder responseBuilder = null;
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		PNCCantiere pncCantiere = null;

		try{
			pncCantiere = ca.get(PNCCantiere.class, id);
			if (pncCantiere instanceof HibernateProxy) {
				pncCantiere = (PNCCantiere)((HibernateProxy)pncCantiere).getHibernateLazyInitializer().getImplementation();
			}

			responseBuilder = Response.ok();

		}catch(EntityNotFoundException nre){
			nre.printStackTrace();
			responseBuilder = Response.status(Status.NOT_FOUND);
			return responseBuilder.build();

		}

		try {

			responseBuilder.header("Content-Disposition", "attachment;filename='Cantiere'" + id + ".pdf");
			responseBuilder = Response.ok(pncCantiere.getPdf());


		} catch (Exception e) {
			e.printStackTrace();
			responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error downloading file " + e.getMessage());
			responseBuilder.type(MediaType.TEXT_PLAIN);
		}
		return responseBuilder.build();
	}

	private Cantiere createNewCantiere(PNCCantiere pncCantiere) throws PersistenceException,
	DictionaryException, PhiException {
		Cantiere cantiereSipral = importCantiere(pncCantiere);
		if(pncCantiere.getParent()!=null){
			CantiereAction cantiereSipralAction = CantiereAction.instance();
			cantiereSipralAction.getEqual().put("isActive", true);
			cantiereSipralAction.getEqual().put("idPnc", String.valueOf(pncCantiere.getParent()));

			List<Cantiere> cantieriParent = cantiereSipralAction.select();

			if(cantieriParent!=null && !cantieriParent.isEmpty()){
				Cantiere cantiereOriginal = cantieriParent.get(0);
				cantiereOriginal.setLastVersion(false);
				cantiereSipral.setOriginal(cantiereOriginal);
				cantiereSipral.setId(cantiereOriginal.getId());
			}else{
				//Importazione cantiere genitore precedentemente fallita
				//commentato perché ricorsivo
				//get(pncCantiere.getParent());
			}
		}else{
			SequenceManager sm = (SequenceManager)Component.getInstance(SequenceManager.class, ScopeType.EVENT);
			cantiereSipral.setId(sm.nextOf("cantieri"));
		}
		ca.create(cantiereSipral);

		PNCPersoneCantiereAction personeCantAction = PNCPersoneCantiereAction.instance();
		personeCantAction.getEqual().put("isActive", true);
		personeCantAction.getEqual().put("cantiere", pncCantiere);
		personeCantAction.getEqual().put("ruolo", "04_V0");
		List<PNCPersoneCantiere> pncCommittenti = personeCantAction.select();
		addCommmittenti(pncCommittenti, cantiereSipral);

		personeCantAction.cleanRestrictions();
		personeCantAction.getEqual().put("isActive", true);
		personeCantAction.getEqual().put("cantiere", pncCantiere);
		personeCantAction.getNotEqual().put("ruolo", "04_V0");
		List<PNCPersoneCantiere> pncAltrePersone = personeCantAction.select();
		addPersoneCantiere(pncAltrePersone, cantiereSipral);

		PNCDitteCantiereAction ditteCantAction = PNCDitteCantiereAction.instance();
		ditteCantAction.getEqual().put("isActive", true);
		ditteCantAction.getEqual().put("cantiere", pncCantiere);
		List<PNCDitteCantiere> pncDitte = ditteCantAction.select();
		addDitteCantiere(pncDitte, cantiereSipral);

		cantiereSipral.setPncForced(pncForced);
		ca.create(cantiereSipral);
		return cantiereSipral;
	}

	private List<DitteCantiere> addDitteCantiere(List<PNCDitteCantiere> pncDitte, Cantiere cantiereSipral) throws PhiException {
		List<DitteCantiere> ditteCantiere = new ArrayList<DitteCantiere>();

		if (pncDitte != null && pncDitte.size() > 0 ) {
			DitteCantiereAction dca = new DitteCantiereAction();

			for (PNCDitteCantiere dc : pncDitte) {
				DitteCantiere newDitteCantiere = new DitteCantiere();
				newDitteCantiere.setIsActive(true);
				newDitteCantiere.setCreatedBy(cantiereSipral.getCreatedBy());
				newDitteCantiere.setCreationDate(new Date());

				newDitteCantiere.setCantiere(cantiereSipral);

				PersoneGiuridiche ditta = getDitta(dc.getPatritaIva(), dc.getCodiceFiscale(), dc.getDenominazione());
				if(ditta==null){
					ditta = importDittaFromPncDitteCant(dc);
				}

				newDitteCantiere.setPersoneGiuridiche(ditta);
				newDitteCantiere.setRuolo(dc.getRuolo());

				ditteCantiere.add(newDitteCantiere);
			}
		}

		cantiereSipral.setDitteCantiere(ditteCantiere);				

		return ditteCantiere;
	}

	private PersoneGiuridiche getDitta(String patritaIva, String codiceFiscale, String denominazione) throws PhiException {

		if(denominazione!=null)
			denominazione = denominazione.toUpperCase();

		//Ricerca in Parix
		PersoneGiuridiche dittaParix = getDittaParix(patritaIva, codiceFiscale, denominazione);

		if(dittaParix!=null){
			return dittaParix;
		}

		PersoneGiuridicheAction ditteAct = PersoneGiuridicheAction.instance();

		//Ricerca in anagrafica locale
		ditteAct.cleanRestrictions();
		ditteAct.getEqual().put("isActive", true);
		((FilterMap) ditteAct.getEqual()).putOr("app", "",null);
		ditteAct.setFullLike(true);
		ditteAct.setDistinct(true);

		if(patritaIva!=null && !patritaIva.isEmpty()){
			ditteAct.getEqual().put("patritaIva", patritaIva);
		}
		if(codiceFiscale!=null && !codiceFiscale.isEmpty()){
			ditteAct.getEqual().put("codiceFiscale", codiceFiscale);
		}
		if(denominazione!=null && !denominazione.isEmpty()){
			ditteAct.getLike().put("denominazione", denominazione);
		}

		List<PersoneGiuridiche> ditteLocal = ditteAct.select();
		if(ditteLocal!=null && ditteLocal.size()==1){
			return ditteLocal.get(0);
		}else{
			return null;
		}
	}

	private PersoneGiuridiche getDittaParix(String patritaIva, String codiceFiscale, String denominazione) {
		PersoneGiuridiche ditta = null;

		PersoneGiuridicheAction pgAction = PersoneGiuridicheAction.instance();
		Object ditteFromParix = pgAction.searchCompanyOnParixPnc(patritaIva, codiceFiscale, denominazione, false);

		if(ditteFromParix == null){
			return null;
		}
		List<Localizzazione> locList = null;

		if(ditteFromParix instanceof EstremiImpresa){
			EstremiImpresa estremiImpresa = (EstremiImpresa) ditteFromParix;

			String cia = estremiImpresa.getDatiIscrizioneRea().get(0).getCciaa();
			String rea = estremiImpresa.getDatiIscrizioneRea().get(0).getNrea().toString();
			try {
				Object obj = null;
				ParixHttpClient parix = ParixHttpClient.instance();
				if(!cia.isEmpty() && !rea.isEmpty()) {

					obj = parix.dettaglioDittaCompleto(cia, rea);

					if (obj != null) {
						if (obj instanceof DatiImpresa){

							DatiImpresa datiImpresa = (DatiImpresa)obj;
							locList = pgAction.searchBranchOnParixPnc(datiImpresa, cia, rea);
							ditta = pgAction.copiaDaParixPnc(datiImpresa, 0L);

							return ditta;
						}
					}

				}
			} catch (PhiException e) {
				return null;
			} catch (NamingException e) {
				return null;
			} catch (InstantiationException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}

		}else{}
		return null;
	}

	private Cantiere importCantiere(PNCCantiere pncCantiere) throws PersistenceException, DictionaryException{
		Cantiere cantiere = new Cantiere();

		cantiere.setIsActive(true);
		cantiere.setCreatedBy(pncCantiere.getCreatedBy());
		cantiere.setCreationDate(new Date());

		if(pncCantiere.getAddr()!=null){
			cantiere.setAddr(pncCantiere.getAddr().cloneAd());
			fixAddress(cantiere.getAddr());
		}

		cantiere.setCost(pncCantiere.getCost());
		cantiere.setDataComunicazione(pncCantiere.getDataComunicazione());

		cantiere.setDurataLavori(pncCantiere.getDurataLavori());
		if(pncCantiere.getDurataLavori()!=null){
			cantiere.setFineLavori(GruppiAction.addDays(pncCantiere.getInizioLavori(), pncCantiere.getDurataLavori()));	
		}
		cantiere.setInizioLavori(pncCantiere.getInizioLavori());

		cantiere.setLatitudine(pncCantiere.getLatitudine());
		cantiere.setLongitudine(pncCantiere.getLongitudine());
		cantiere.setMail(pncCantiere.getPecSender());
		//cantiere.setLocalita(pncCantiere.);
		cantiere.setMaxWorkers(pncCantiere.getMaxWorkers());
		//cantiere.setName(toCopy.getName());
		cantiere.setNaturaOpera(pncCantiere.getNaturaOpera());
		//
		//		SequenceManager sm = (SequenceManager)Component.getInstance(SequenceManager.class, ScopeType.EVENT);
		//		cantiere.setId(sm.nextOf("cantieri"));
		cantiere.setIdPnc(String.valueOf(pncCantiere.getInternalId()));

		cantiere.setNotifica(true);
		cantiere.setTitoloIV(true);

		//cantiere.setNumeroAutonomi(pncCantiere.get);
		cantiere.setNumeroImprese(pncCantiere.getNumeroImprese());

		cantiere.setTipo(pncCantiere.getTipo());
		Vocabularies vocabularies = VocabulariesImpl.instance();
		cantiere.setTipologiaNotifica((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "TipologiaNotifica", "TYPECANT01"));

		cantiere.setLastVersion(true);

		return cantiere;
	}

	private Protocollo insertComunicazione(PNCPecNotificaDest notifica, ServiceDeliveryLocation distretto) throws PersistenceException, DictionaryException{
		Protocollo comunicazione = new Protocollo();

		comunicazione.setIsActive(true);
		comunicazione.setCreatedBy(notifica.getCreatedBy());
		comunicazione.setCreationDate(new Date());
		comunicazione.setVersion(0);

		comunicazione.setServiceDeliveryLocation(distretto);

		Vocabularies vocabularies = VocabulariesImpl.instance();
		comunicazione.setCode(vocabularies.getCodeValueCsDomainCode("PHIDIC", "ComplexType", "4"));

		for(ServiceDeliveryLocation wl : distretto.getChildren()){
			if("phidic.spisal.pratiche.type.supervision".equals(wl.getArea().getOid())){
				comunicazione.setUos(wl);
				break;
			}
		}

		comunicazione.setStatusCode(vocabularies.getCodeValueCsDomainCode("STATUS", "GENERIC", "new"));

		return comunicazione;
	}

	private Protocollo updateFromPNCCantiere (Protocollo comunicazione, PNCCantiere cantiere) throws PhiException{
		comunicazione.setData(cantiere.getDataComunicazione());
		comunicazione.setDataASL(cantiere.getDataComunicazione());

		Vocabularies vocabularies = VocabulariesImpl.instance();
		comunicazione.setRichiedente(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Utente"));
		comunicazione.setRiferimento(vocabularies.getCodeValueCsDomainCode("PHIDIC", "TargetSource", "Cantiere"));
		comunicazione.setRichiedenteUtente(getPerson(cantiere.getCreatedBy(), null, null));

		comunicazione.setOggetto("Inserimento automatico da PNC Notifica Cantiere in Titolo IV numero " + cantiere.getInternalId());
		return comunicazione;
	}

	private Protocollo updateFromCantiereSipral (Protocollo comunicazione, Cantiere cantiere) throws PersistenceException, DictionaryException{
		comunicazione.setRiferimentoCantiere(cantiere);
		return comunicazione;
	}

	private Person getPerson(String fiscalCode, String surname, String name) throws PhiException{
		PersonAction persAct = PersonAction.instance();

		if(surname!=null)
			surname = surname.toUpperCase();

		if(name!=null)
			name = name.toUpperCase();

		//Ricerca in anagrafica locale
		persAct.cleanRestrictions();
		persAct.setTrimFilters(true);
		persAct.setFullLike(true);
		persAct.setDistinct(true);
		persAct.getEqual().put("isActive", true);
		persAct.getEqual().put("fiscalCode", fiscalCode);

		if(surname != null){
			persAct.getLike().put("name.fam", surname);
		}
		if(name != null){
			persAct.getLike().put("name.giv", name);
		}

		List<Person> personsLocal = persAct.select();
		if(personsLocal!=null && personsLocal.size()==1){
			return personsLocal.get(0);
		}else{
			//Ricerca in AUR
			return getPersonAur(surname, name, fiscalCode);
		}
	}

	private List<Committente> addCommmittenti(List<PNCPersoneCantiere> pncCommittenti, Cantiere cantiereSipral) throws PhiException{
		List<Committente> committentiSipral = new ArrayList<Committente>();

		if (pncCommittenti != null && pncCommittenti.size() > 0) {
			CommittenteAction ca = new CommittenteAction();

			for (PNCPersoneCantiere c : pncCommittenti) {
				Committente newCommittente = new Committente();
				newCommittente.setIsActive(true);
				newCommittente.setCreatedBy(cantiereSipral.getCreatedBy());
				newCommittente.setCreationDate(new Date());

				newCommittente.setCantiere(cantiereSipral);

				Person committentePerson = getPerson(c.getCodiceFiscale(), c.getName().getFam(), c.getName().getGiv());
				if(committentePerson==null){
					committentePerson = importPersonFromPncPersCant(c);
				}

				newCommittente.setPerson(committentePerson);
				committentiSipral.add(newCommittente);
			}
		}

		cantiereSipral.setCommittente(committentiSipral);				

		return committentiSipral;
	}

	private List<PersoneCantiere> addPersoneCantiere(List<PNCPersoneCantiere> pncPersone, Cantiere cantiereSipral) throws PhiException{
		List<PersoneCantiere> personeSipral = new ArrayList<PersoneCantiere>();

		if (pncPersone != null && pncPersone.size() > 0) {
			PersoneCantiereAction pca = new PersoneCantiereAction();

			for (PNCPersoneCantiere c : pncPersone) {
				PersoneCantiere newPersonaCantiere = new PersoneCantiere();
				newPersonaCantiere.setIsActive(true);
				newPersonaCantiere.setCreatedBy(cantiereSipral.getCreatedBy());
				newPersonaCantiere.setCreationDate(new Date());

				newPersonaCantiere.setCantiere(cantiereSipral);

				Person altraPerson = getPerson(c.getCodiceFiscale(), c.getName().getFam(), c.getName().getGiv());
				if(altraPerson==null){
					altraPerson = importPersonFromPncPersCant(c);
				}

				newPersonaCantiere.setPerson(altraPerson);

				Vocabularies vocabularies = VocabulariesImpl.instance();
				newPersonaCantiere.setRuolo((CodeValuePhi)vocabularies.getCodeValueCsDomainCode("PHIDIC", "RuoloInCantiere", 
						("RUOLOCANT"+c.getRuolo().substring(0, 2))));

				personeSipral.add(newPersonaCantiere);
			}
		}

		cantiereSipral.setPersoneCantiere(personeSipral);				

		return personeSipral;
	}

	private Person importPersonFromPncPersCant(PNCPersoneCantiere pncPersonaCantiere) throws PersistenceException, DictionaryException {
		Person newPerson = new Person();
		newPerson.setIsActive(true);
		newPerson.setCreatedBy(pncPersonaCantiere.getCreatedBy());
		newPerson.setCreationDate(new Date());

		EN newName = new EN();
		newName.setFam(pncPersonaCantiere.getName().getFam().toUpperCase());
		newName.setGiv(pncPersonaCantiere.getName().getGiv().toUpperCase());

		newPerson.setName(newName);
		newPerson.setFiscalCode(pncPersonaCantiere.getCodiceFiscale());

		if(pncPersonaCantiere.getAddr()!=null){
			newPerson.setAddr(pncPersonaCantiere.getAddr().cloneAd());
			fixAddress(newPerson.getAddr());
		}
		newPerson.setCountryOfAddr(pncPersonaCantiere.getCountryOfAddr());

		ca.create(newPerson);

		pncForced = true;
		return newPerson;
	}

	private Person getPersonAur( String surname, String name, String fiscalCode) {
		PersonAction persAction = PersonAction.instance();

		List<Person> persons = null;

		try{
			persons = persAction.aurSearch(surname, name, fiscalCode, null, null, null, null, null, null);
		}catch(Exception e){
			return null;
		}

		if(persons!=null && persons.size()==1){

			Person importedPerson = null;
			try{
				List<com.phi.aur.json.Person> aurPersons = new ArrayList<com.phi.aur.json.Person>();
				List<com.phi.aur.json.Error> aurErrors = new ArrayList<com.phi.aur.json.Error>();

				AurResponse resp =  AurHttpClient.instance().aurImport(persons.get(0).getMpi()); 
				aurPersons = resp.getPersons();
				aurErrors = resp.getErrors();
				if(aurErrors==null || aurErrors.size()==0){

					long internalIdNew=Long.parseLong(resp.getInternalId());

					importedPerson = ca.get(Person.class, internalIdNew); 
				} 
			}catch(Exception exc){
				return null;
			}
			return importedPerson;
		}else{
			return null;
		}
	}

	private AD fixAddress(AD addr) throws PersistenceException, DictionaryException{

		return CantiereAction.instance().fixAddressPNC(addr);
	}

	private PersoneGiuridiche importDittaFromPncDitteCant(PNCDitteCantiere pncDitteCantiere) throws PersistenceException{
		PersoneGiuridiche newDitta = new PersoneGiuridiche();
		newDitta.setIsActive(true);
		newDitta.setCreatedBy(pncDitteCantiere.getCreatedBy());
		newDitta.setCreationDate(new Date());

		newDitta.setPatritaIva(pncDitteCantiere.getPatritaIva());
		newDitta.setCodiceFiscale(pncDitteCantiere.getCodiceFiscale());
		newDitta.setDenominazione(pncDitteCantiere.getDenominazione().toUpperCase());


		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		ca.create(newDitta);

		pncForced = true;
		return newDitta;
	}

}