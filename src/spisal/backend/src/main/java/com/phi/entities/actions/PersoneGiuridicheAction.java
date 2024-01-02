package com.phi.entities.actions;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.Type;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.Addebito;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Articoli;
import com.phi.entities.baseEntity.ArticoliBL;
import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Cariche;
import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Protocollo;
import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.baseEntity.Ruoli;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.baseEntity.SediInstallazione;
import com.phi.entities.baseEntity.TagDitta;
import com.phi.entities.baseEntity.TipologiaDitta;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;
import com.phi.parix.ParixHttpClient;
import com.phi.parix.json.detail.Carica;
import com.phi.parix.json.detail.DatiImpresa;
import com.phi.parix.json.detail.DatiIscrizioneRea;
import com.phi.parix.json.detail.EstremiImpresa;
import com.phi.parix.json.detail.EstremiNascita;
import com.phi.parix.json.detail.Indirizzo;
import com.phi.parix.json.detail.Localizzazione;
import com.phi.parix.json.detail.Localizzazioni;
import com.phi.parix.json.detail.NumeroTipo;
import com.phi.parix.json.detail.Persona;
import com.phi.parix.json.detail.PersonaFisica;
import com.phi.parix.json.detail.PersonaGiuridica;
import com.phi.parix.json.detail.RuoloLoc;
import com.phi.parix.json.search.Riga;
import com.phi.ps.Node;
import com.phi.ps.ProcessManagerImpl;
import com.phi.ps.TreeBean;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("PersoneGiuridicheAction")
@Scope(ScopeType.CONVERSATION)
public class PersoneGiuridicheAction extends BaseAction<PersoneGiuridiche, Long> {

	private static final Logger log = Logger.getLogger(PersoneGiuridicheAction.class);
	private static final long serialVersionUID = -2043659847405719126L;
	private static String QUERY_PRATICHE = "select distinct d.pratica_id from ditte_pratiche d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String QUERY_PROVVEDIMENTI = "select distinct d.prov_id from ditte_provvedimenti d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String HAS_PRATICHE = "select count(distinct d.pratica_id) from ditte_pratiche d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";

	private static String HAS_PROVVEDIMENTI = "select count(distinct d.prov_id) from ditte_provvedimenti d " +
			"where d.cf=:cf and d.sdl_id in (:sdlocIds)";


	private static String IS_BLACKLISTED_QUERY = "select art,count(*) from Articoli art "
			+" join art.provvedimento pr join pr.soggetto sogg join sogg.ditta pg "
			+" where art.isActive = 1 and pr.isActive = 1 and art.isActive = 1 and sogg.isActive = 1 and art.code in (:blackListCv) and pg.internalId = :ditta_id "
			+" and pr.data >= :minDate group by art.code";

	private static String QUERY_DITTE_DA_CHIUDERE = "SELECT p FROM PersoneGiuridiche p " +
			"JOIN p.sedi s " +
			"WHERE p.codiceFiscale = :cf " +
			"AND s.numeroREA = :nRea " +
			"AND s.provinciaCCIAA = :prov " +
			"AND s.sedePrincipale = true " +
			"AND p.isActive = 1 " +
			"AND (p.app IS NULL OR p.app = '') " +
			"ORDER BY p.creationDate DESC";

	private static String QUERY_SEDI = "SELECT s FROM Sedi s " +
			"JOIN s.personaGiuridica p " +
			"WHERE p.internalId = :id " +
			"AND s.isActive = 1";

	private static String QUERY_SEDI_LOCALI = QUERY_SEDI +
			" AND s.deletable = 1";

	private static final String parixDatePattern = "yyyyMMdd";

	private Query queryHasPratiche,queryPratiche,queryProvvedimenti,queryHasProvvedimenti;

	public static PersoneGiuridicheAction instance() {
		return (PersoneGiuridicheAction) Component.getInstance(PersoneGiuridicheAction.class, ScopeType.CONVERSATION);
	}

	@Create
	public void init() throws PhiException{
		queryHasPratiche = ca.createNativeQuery(HAS_PRATICHE);
		queryPratiche = ca.createNativeQuery(QUERY_PRATICHE);
		queryProvvedimenti = ca.createNativeQuery(QUERY_PROVVEDIMENTI);
		queryHasProvvedimenti = ca.createNativeQuery(HAS_PROVVEDIMENTI);
	}

	@Override
	public void inject(Object obj) {
		super.inject(obj);

		PersoneGiuridiche pg = null;

		if (obj instanceof BaseEntity) 
			pg = (PersoneGiuridiche)obj;

		else if (obj instanceof Map) {
			pg = ca.load(entityClass, (Long)((Map)obj).get("internalId"));
		} 

		if (pg != null){
			//Sedi sedePrincipale = pg.getSedePrincipale();
			Sedi sedePrincipale = getSedePrincipale(pg);

			SediAction sAction = SediAction.instance();
			Sedi sede = sAction.getEntity();

			if(sede!=null && pg.getSedi()!=null && !pg.getSedi().contains(sede)){
				if (sedePrincipale != null)
					sAction.inject(sedePrincipale);
				else 
					sAction.eject();

			}else if (sede==null && sedePrincipale != null){
				sAction.inject(sedePrincipale);

			}else if (sede==null && sedePrincipale==null && pg.getSedi()!=null && !pg.getSedi().isEmpty()){
				sAction.inject(pg.getSedi().get(0));

			}
		}
	}

	public AD getAddrSedePrincipale(){
		PersoneGiuridiche pg = getEntity();
		return getAddrSedePrincipale(pg);
	}

	public AD getAddrSedePrincipale(Object pg){

		if (pg!=null){
			if(pg instanceof PersoneGiuridiche){
				List<Sedi> sedi = ((PersoneGiuridiche) pg).getSedi();
				if (sedi != null)
					for (Sedi sede : sedi) {
						if (sede.getSedePrincipale())
							return sede.getAddr();

					}
			}
		}

		return null;
	}

	public CodeValueCountry getCountrySedePrincipale(){
		PersoneGiuridiche pg = getEntity();
		if (pg!=null){
			List<Sedi> sedi = pg.getSedi();
			if (sedi != null)
				for (Sedi sede : sedi) {
					if (sede.getSedePrincipale())
						return sede.getStato();

				}
		}

		return null;
	}

	public void searchCompanyOnParix(String cf, String denominazione, Boolean dittaCessata){

		try{
			ParixHttpClient parix = ParixHttpClient.instance();
			List<EstremiImpresa> list = null;
			Object obj = null;

			String iva = (String) getTemporary().get("partitaIva");

			ejectList("LocalizzazioneList");
			ejectList("ParixCompanyList");
			ejectList("ParixCompanyListIva");

			if(iva!=null && !iva.isEmpty()){
				obj = parix.ricercaImpresePerPartitaiva(dittaCessata, iva);
				if(obj instanceof Riga){
					Riga riga = (Riga)obj;
					if(!riga.getCFISC1().isEmpty()) cf = riga.getCFISC1();
				}
			}

			if(cf!=null && !cf.isEmpty()){
				obj = parix.ricercaImpresePerCodiceFiscale(dittaCessata, cf);

			}else if (denominazione!=null && !denominazione.isEmpty()){
				obj = parix.ricercaImpresePerDenominazione(dittaCessata, denominazione);

			}

			if (obj != null) {
				if (obj instanceof List<?>){
					temporary.remove("exception");
					injectList((List<EstremiImpresa>)obj, "ParixCompanyList");

				} else if (obj instanceof Riga){

					temporary.remove("exception");
					Riga riga = (Riga)obj;
					if(riga.getID()!=null){
						List<Riga> rigaList = new ArrayList<Riga>();
						rigaList.add(riga);
						injectList(rigaList, "ParixCompanyListIva");
					} else {
						temporary.put("exception","nessun risultato");
					}

				} else if (obj instanceof String){
					temporary.put("exception", obj.toString());
					ejectList("ParixCompanyList");
				}

			}
		} catch(Exception e) {
			ejectList("LocalizzazioneList");
			ejectList("ParixCompanyList");
			ejectList("ParixCompanyListIva");
			temporary.put("exception", e.getMessage());
		}
	}

	public void searchBranchOnParix(String cia, String rea, String mnemonicName) throws PhiException, NamingException {
		try {
			Object obj = null;
			ParixHttpClient parix = ParixHttpClient.instance();
			if(!cia.isEmpty() && !rea.isEmpty()) {

				temporary.put("rea", rea);
				temporary.put("cia", cia); 

				//DatiIscrizioneRea dati = dittaParix.getDatiIscrizioneRea().get(0);
				obj = parix.dettaglioDittaCompleto(cia, rea);

				if (obj != null) {
					if (obj instanceof DatiImpresa){
						temporary.remove("exception");

						DatiImpresa datiImpresa = (DatiImpresa)obj;
						Contexts.getConversationContext().set("DatiImpresa", datiImpresa);
						List<Localizzazione> locList = new ArrayList<Localizzazione>();
						//SEDE PRINCIPALE
						if(datiImpresa.getInformazioniSede()!=null){
							Localizzazione loc = new Localizzazione();
							loc.setNumeroTipo(new NumeroTipo());
							loc.getNumeroTipo().setCciaa(cia);
							try {
								loc.getNumeroTipo().setNrea(Long.parseLong(rea));
							} catch (NumberFormatException e) {
								//nothing to do
							}
							loc.setIndirizzo(datiImpresa.getInformazioniSede().getIndirizzo());

							loc.setInternalId(0L);//0=sede principale
							locList.add(loc);
						}

						if(datiImpresa.getLocalizzazioni()!=null && !datiImpresa.getLocalizzazioni().isEmpty()){
							for(Localizzazioni locs : datiImpresa.getLocalizzazioni()){
								if(locs.getLocalizzazione()!=null && !locs.getLocalizzazione().isEmpty()){
									locList.addAll(locs.getLocalizzazione());
								}
							}

							//set internalIds
							/*
							 * QUESTE ENTITA NON SONO BASEENTITY, ma devono esser gestite dalle datagrid come tali.
							 */
							long i = 0;
							for(Localizzazione loc : locList){
								loc.setInternalId(i); 
								i++;
							}
						}

						//sedi inserite manualmente
						if(datiImpresa!=null && datiImpresa.getEstremiImpresa()!=null && datiImpresa.getEstremiImpresa().getCodiceFiscale()!=null){
							GenericAdapterLocalInterface ga = GenericAdapter.instance();
							Map<String, Object> pars = new HashMap<String, Object>();
							pars.put("cf", datiImpresa.getEstremiImpresa().getCodiceFiscale());
							pars.put("nRea", datiImpresa.getEstremiImpresa().getDatiIscrizioneRea().get(0).getNrea().toString());
							pars.put("prov", datiImpresa.getEstremiImpresa().getDatiIscrizioneRea().get(0).getCciaa());
							List<PersoneGiuridiche> listaDitteDaChiudere = (List<PersoneGiuridiche>)ga.executeHQL(QUERY_DITTE_DA_CHIUDERE, pars);
							if(listaDitteDaChiudere!=null && !listaDitteDaChiudere.isEmpty()){
								PersoneGiuridiche lastDitta = listaDitteDaChiudere.get(0);
								if(lastDitta!=null){
									Map<String, Object> parSedi = new HashMap<String, Object>();
									parSedi.put("id", lastDitta.getInternalId());
									List<Sedi> listaSediLocali = (List<Sedi>)ga.executeHQL(QUERY_SEDI_LOCALI, parSedi);
									for(Sedi oldSede : listaSediLocali){
										Localizzazione newLoc = new Localizzazione();
										newLoc.setNumeroTipo(new NumeroTipo());
										newLoc.setIndirizzo(new Indirizzo());

										newLoc.setInternalId(oldSede.getInternalId());
										newLoc.getNumeroTipo().setCciaa(oldSede.getProvinciaCCIAA());
										try {
											newLoc.getNumeroTipo().setNrea(Long.parseLong(oldSede.getNumeroREA()));
										} catch (NumberFormatException e) {
											// nothing to do
										}

										if(oldSede.getAddr()!=null){
											newLoc.getIndirizzo().setComune(oldSede.getAddr().getCty());
											newLoc.getIndirizzo().setVia(oldSede.getAddr().getStr());
											newLoc.getIndirizzo().setNcivico(oldSede.getAddr().getBnr());
										}
										newLoc.setLocale(true);
										locList.add(newLoc);
									}
								}							
							}
						}

						LocalizzazioneAction.instance().injectList(locList,"LocalizzazioneList");
						PhiDataModel dm = (PhiDataModel)Contexts.getConversationContext().get("LocalizzazioneList");
						dm.setSelectedIndex(0);//preseleziono la sede principale

					} else if (obj instanceof String) {
						temporary.put("exception", obj.toString());
						LocalizzazioneAction.instance().ejectList("LocalizzazioneList");
						mnemonicName =  "Link_1498739748008;connError";
					}
				}

			} else
				Contexts.getConversationContext().remove("DatiImpresa");

			if ( mnemonicName != null && !mnemonicName.isEmpty() ){
				ProcessManagerImpl.instance().manageTask(mnemonicName);
			}
		} catch(Exception e) {
			ejectList("LocalizzazioneList");
			ejectList("ParixCompanyList");
			ejectList("ParixCompanyListIva");
			temporary.put("exception", e.getMessage());
		}
	}

	/* public void searchBranchOnParixID(String id, String mnemonicName) throws PhiException{


		Object obj = null;
		ParixHttpClient parix = ParixHttpClient.instance();
		if(!id.isEmpty()) {

			temporary.put("pxID", id);


			//DatiIscrizioneRea dati = dittaParix.getDatiIscrizioneRea().get(0);
			obj = parix.dettaglioDittaCompletoID(id);


			if (obj != null) {
				if (obj instanceof DatiImpresa){
					temporary.remove("exception");

					DatiImpresa datiImpresa = (DatiImpresa)obj;
					Contexts.getConversationContext().set("DatiImpresa", datiImpresa);
					List<Localizzazione> locList = new ArrayList<Localizzazione>();
					if(datiImpresa.getLocalizzazioni()!=null && !datiImpresa.getLocalizzazioni().isEmpty()){
						for(Localizzazioni locs : datiImpresa.getLocalizzazioni()){
							if(locs.getLocalizzazione()!=null && !locs.getLocalizzazione().isEmpty()){
								locList.addAll(locs.getLocalizzazione());
							}
						}

						//set internalIds

						long i = 1;
						for(Localizzazione loc : locList){
							loc.setInternalId(i); 
							i++;
						}

						LocalizzazioneAction.instance().injectList(locList,"LocalizzazioneList");
					}
				} else if (obj instanceof Riga) {
					temporary.remove("exception");
					Riga riga = (Riga) obj;
					Contexts.getConversationContext().set("Riga", riga);

					List<Localizzazione> locList = new ArrayList<Localizzazione>();
					if(riga!=null){

						Localizzazione loc = new Localizzazione();
						loc.setDenominazione(riga.getDENOM());


						NumeroTipo nt = new NumeroTipo();
						//missing ciaa
						//nt.setCciaa("");
						if(riga.getNREA()!=null){
							try{
							nt.setNrea(Long.parseLong(riga.getNREA()));
							}catch (Exception e) {
								// TODO: handle exception
							}
						}

						loc.setNumeroTipo(nt);

						//missing insegna
						//loc.setInsegna("");

						Indirizzo indirizzo = new Indirizzo();
						indirizzo.setComune(riga.getLOCALUL());
						indirizzo.setToponimo(riga.getCVIAUL());
						indirizzo.setVia(riga.getDESVIAUL());
						indirizzo.setNcivico(riga.getNCIVS());
						loc.setIndirizzo(indirizzo);

						loc.setInternalId((long) 1); 
						locList.add(loc);
						LocalizzazioneAction.instance().injectList(locList,"LocalizzazioneList");


					}

//					List<Riga> rigaList = new ArrayList<Riga>();
//					rigaList.add(riga)

				} else if (obj instanceof String) {
					temporary.put("exception", obj.toString());
					LocalizzazioneAction.instance().ejectList("LocalizzazioneList");
					mnemonicName =  "Link_1498739748008;connError";
				}
			}

		} else
			Contexts.getConversationContext().remove("DatiImpresa");

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}
	} */

	public Date parseParixDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if(date!=null && !date.isEmpty() && date.matches("\\d{8}")){
			try {
				Date data = sdf.parse(date);
				return data;
			} catch (ParseException e) {
				log.error("Error on ParseParixDate, trying to parse: "+date);
			}

		}
		return null;
	}

	public Date getCancellationDate(EstremiImpresa estremiImpresa) {
		if (estremiImpresa.getDatiIscrizioneRea() != null) {
			for (DatiIscrizioneRea datiIscrizioneRea : estremiImpresa.getDatiIscrizioneRea()) {
				if (datiIscrizioneRea.getCessazione() != null) {
					if (datiIscrizioneRea.getCessazione().getDtCancellazione() != null) {
						try {
							return new SimpleDateFormat(parixDatePattern).parse(datiIscrizioneRea.getCessazione().getDtCancellazione().toString());
						} catch (ParseException e) {
							return null;
						}
					}
				}
			}
		}
		return null;
	}

	public Date getDataCessazione(Localizzazione localizzazione) {
		if (localizzazione.getCessazioneLoc() != null && localizzazione.getCessazioneLoc().getDtCessazione() != null) {
			try {
				return new SimpleDateFormat(parixDatePattern).parse(localizzazione.getCessazioneLoc().getDtCessazione().toString());
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	public void injectParixCompany(EstremiImpresa dittaParix, String mnemonicName) throws PhiException {
		Object obj = null;
		ParixHttpClient parix = ParixHttpClient.instance();
		if(dittaParix!=null && dittaParix.getDatiIscrizioneRea()!=null && !dittaParix.getDatiIscrizioneRea().isEmpty()) {
			/*
			 * Se la ditta è PLURILOCALIZZATA avrò più numeri REA, ma il dettaglio completo della ditta resta lo stesso 
			 * Quindi mi basta fornire la prima coppia n_rea / cciaa per ottenere il dettaglio completo
			 */
			DatiIscrizioneRea dati = dittaParix.getDatiIscrizioneRea().get(0);
			obj = parix.dettaglioDittaCompleto(dati.getCciaa().toString(), dati.getNrea().toString());

			if (obj != null) {
				if (obj instanceof DatiImpresa){
					temporary.remove("exception");

					DatiImpresa datiImpresa = (DatiImpresa)obj;
					Contexts.getConversationContext().set("DatiImpresa", datiImpresa);
					List<Localizzazione> locList = new ArrayList<Localizzazione>();
					if(datiImpresa.getLocalizzazioni()!=null && !datiImpresa.getLocalizzazioni().isEmpty()){
						for(Localizzazioni locs : datiImpresa.getLocalizzazioni()){
							if(locs.getLocalizzazione()!=null && !locs.getLocalizzazione().isEmpty()){
								locList.addAll(locs.getLocalizzazione());
							}
						}

						//set internalIds
						/*
						 * QUESTE ENTITA NON SONO BASEENTITY, ma devono esser gestite dalle datagrid come tali.
						 */
						long i = 1;
						for(Localizzazione loc : locList){
							loc.setInternalId(i); 
							i++;
						}

						LocalizzazioneAction.instance().injectList(locList,"LocalizzazioneList");
					}

				} else if (obj instanceof String) {
					temporary.put("exception", obj.toString());
					LocalizzazioneAction.instance().ejectList("LocalizzazioneList");
					mnemonicName =  "Link_1498739748008;connError";
				}
			}

		} else
			Contexts.getConversationContext().remove("DatiImpresa");

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}
	}

	public void copiaDaParix(DatiImpresa dati, Riga riga, IdataModel localizzazioneList, Long selectedLoc) throws InstantiationException, IllegalAccessException, PhiException, NamingException{
		Boolean isForArpav = (Boolean)getTemporary().get("isForArpav");

		if (isForArpav == null)
			isForArpav = false;

		if(dati==null && riga==null) {
			eject();
			return;
		}

		GenericAdapterLocalInterface ga = GenericAdapter.instance();

		PersoneGiuridiche newDitta = new PersoneGiuridiche();
		Vocabularies voc = VocabulariesImpl.instance();
		List<Sedi> sediDitta = new ArrayList<Sedi>();

		SediAction sAction = SediAction.instance();
		sAction.getTemporary().remove("selectedSede");
		Long selectedSede = null;

		if(dati != null) {
			if(dati.getEstremiImpresa() != null) {

				// CERCO LE DITTE DA CHIUDERE
				String queryDitteDaChiudere = QUERY_DITTE_DA_CHIUDERE;
				if (isForArpav) {
					queryDitteDaChiudere = queryDitteDaChiudere.replace("AND (p.app IS NULL OR p.app = '')", "AND p.app = 'ARPAV'");
				}

				Map<String, Object> pars = new HashMap<String, Object>();
				pars.put("cf", dati.getEstremiImpresa().getCodiceFiscale());
				pars.put("nRea", dati.getEstremiImpresa().getDatiIscrizioneRea().get(0).getNrea().toString());
				pars.put("prov", dati.getEstremiImpresa().getDatiIscrizioneRea().get(0).getCciaa());
				List<PersoneGiuridiche> listaDitteDaChiudere = (List<PersoneGiuridiche>)ga.executeHQL(queryDitteDaChiudere, pars);

				if(listaDitteDaChiudere != null && !listaDitteDaChiudere.isEmpty()) {

					if (isForArpav) {
						// nel caso ARPAV non c'è niente da chiudere.
						getTemporary().put("alreadyExistArpav", true);
						PersoneGiuridiche existing = listaDitteDaChiudere.get(0);
						localizzazioniToSedi(dati, existing, sediDitta, selectedLoc, selectedSede, voc, ga);
						inject(ca.get(entityClass, existing.getInternalId()));
						List<PersoneGiuridiche> l = new ArrayList<PersoneGiuridiche>();
						l.add(existing);
						injectList(l);
						ca.refresh(getEntity());
						return;

					} else {
						// CHIUDO LE DITTE PRECEDENTI
						for(PersoneGiuridiche p : listaDitteDaChiudere){
							p.setIsActive(false);
							ga.updateObject(p);
						}
					}
				}

				newDitta.setDenominazione(truncate(dati.getEstremiImpresa().getDenominazione(),255));
				newDitta.setCodiceFiscale(truncate(dati.getEstremiImpresa().getCodiceFiscale(),255));
				newDitta.setPatritaIva(truncate(dati.getEstremiImpresa().getPartitaIva(),255));
				if(dati.getEstremiImpresa().getDatiIscrizioneRi()!=null){
					newDitta.setNumeroRI(truncate(dati.getEstremiImpresa().getDatiIscrizioneRi().getNumeroRi(),255));
					newDitta.setDataIscrizioneRI(parseParixDate(dati.getEstremiImpresa().getDatiIscrizioneRi().getData()));
				}

				// H00200030
				newDitta.setDataCancellazioneRI(getCancellationDate(dati.getEstremiImpresa()));

				if(dati.getEstremiImpresa().getFormaGiuridica()!=null){
					String code = dati.getEstremiImpresa().getFormaGiuridica().getCformaGiuridica();
					String desc = dati.getEstremiImpresa().getFormaGiuridica().getDescrizione();

					CodeValue cvForma = getOrCreateCode("PHIDIC", "Company", "forma", code, desc, voc);
					if(cvForma != null){
						newDitta.setFormaGiuridica(cvForma);
					}
				}

				if(dati.getDurataSocieta()!=null){
					if(dati.getDurataSocieta().getDtCostituzione()!=null)
						newDitta.setDataCostituzione(parseParixDate(dati.getDurataSocieta().getDtCostituzione().toString()));

					if(dati.getDurataSocieta().getDtTermine()!=null)
						newDitta.setDataTermine(parseParixDate(dati.getDurataSocieta().getDtTermine().toString()));
				}

				if(dati.getInformazioniSede() != null) {
					Sedi sedePrincipale = new Sedi();
					if(dati.getInformazioniSede().getIndirizzo()!=null){
						sedePrincipale.setSedePrincipale(true);
						sedePrincipale.setDenominazioneUnitaLocale(truncate(dati.getEstremiImpresa().getDenominazione(),255));
						sedePrincipale.setProgressivoUnitaLocale(0);
						sedePrincipale.setElemento("0");
						AD addr = indirizzoToAddr(dati.getInformazioniSede().getIndirizzo(), voc);
						TEL telecom = new TEL();
						telecom.setMc(truncate(dati.getInformazioniSede().getIndirizzo().getIndirizzoPec(),255));
						if(dati.getEstremiImpresa().getDatiIscrizioneRea()!=null && !dati.getEstremiImpresa().getDatiIscrizioneRea().isEmpty()){
							List<DatiIscrizioneRea> datiRea = dati.getEstremiImpresa().getDatiIscrizioneRea();

							/*
							 * Se ho piu datiRea la ditta è PLURILOCALIZZATA (PIU SEDI SECONDARIE, OGNUNA CON PIU UNITA LOCALI)
							 * Per la sede principale devo cercare quella con CCIAA = provincia sede principale (forse poteva bastare guardare FLAG_SEDE=SI...non si sa)
							 */
							DatiIscrizioneRea reaPrinc = null;
							if(datiRea!=null && !datiRea.isEmpty()){
								String provinciaPrincipale = addr.getCpa();
								for(DatiIscrizioneRea rea : datiRea){
									if(rea.getCciaa().equals(provinciaPrincipale)){
										reaPrinc=rea;
										break;
									}
								}
							} else {
								reaPrinc=dati.getEstremiImpresa().getDatiIscrizioneRea().get(0);
							}

							if(reaPrinc!=null){
								sedePrincipale.setNumeroREA(truncate(reaPrinc.getNrea().toString(),255));
								sedePrincipale.setProvinciaCCIAA(truncate(reaPrinc.getCciaa(),255));
								if(reaPrinc.getCessazione()!=null){
									if(reaPrinc.getCessazione().getDtCessazione()!=null)
										sedePrincipale.setDataCessazione(parseParixDate(reaPrinc.getCessazione().getDtCessazione().toString()));

									sedePrincipale.setCausaleCessazione(reaPrinc.getCessazione().getCausale());
									if(reaPrinc.getCessazione().getDtDenunciaCess()!=null)
										sedePrincipale.setDataDenunciaCessazione(parseParixDate(reaPrinc.getCessazione().getDtDenunciaCess().toString()));
									if(reaPrinc.getCessazione().getDtCancellazione()!=null)
										sedePrincipale.setDataCancellazione(parseParixDate(reaPrinc.getCessazione().getDtCancellazione().toString()));
								}
							}
						}

						//SALVO LA SEDE PRINCIPALE
						sedePrincipale.setAddr(addr);
						sedePrincipale.setTelecom(telecom);
					}

					//SALVO LA DITTA CORRENTE
					if(newDitta!=null){
						newDitta = (PersoneGiuridiche) ga.updateObject(newDitta);
						sedePrincipale.setPersonaGiuridica(newDitta);
						sedePrincipale = (Sedi) ga.updateObject(sedePrincipale);
						sediDitta.add(sedePrincipale);
					}

					selectedSede = localizzazioniToSedi(dati, newDitta, sediDitta, selectedLoc, selectedSede, voc, ga);

					//cerco le sedi inserite manualmente e le copio (solo dalla ultima versione della ditta)
					if(listaDitteDaChiudere!=null && !listaDitteDaChiudere.isEmpty()){
						PersoneGiuridiche lastDitta = listaDitteDaChiudere.get(0);
						selectedSede = manageSediLocali(lastDitta, newDitta, sediDitta, selectedLoc, selectedSede, ga);
					}

					if(selectedSede==null){
						selectedSede = sedePrincipale.getInternalId();
					}

					newDitta.setSedi(sediDitta);

					if(dati.getInformazioniSede().getCodiceAtecoUl()!=null){
						attivitaSede(sediDitta.get(0), dati.getInformazioniSede().getCodiceAtecoUl().getAttivitaIstat(), voc, ga);
					}

					if(dati.getInformazioniSede().getRuoliLoc()!=null){
						ruoliSede(sediDitta.get(0), dati.getInformazioniSede().getRuoliLoc().getRuoloLoc(), voc, ga);
					}

				} //informazioniSede
			} //estremi impresa

			if(dati.getPersoneSede()!=null && dati.getPersoneSede().getPersona()!=null && !dati.getPersoneSede().getPersona().isEmpty()){

				SediPersoneAction spAction = SediPersoneAction.instance();
				CaricheAction carAction = CaricheAction.instance();

				for(Persona p : dati.getPersoneSede().getPersona()) {
					SediPersone sp = personaToSediPersona(p, voc);
					spAction.inject(sp);
					spAction.create();
					sp = spAction.getEntity();
					List<Cariche> spCariche = new ArrayList<Cariche>();
					if(p.getCariche()!=null && p.getCariche().getCarica()!=null){
						for(Carica c : p.getCariche().getCarica()){
							Cariche car = caricaToCariche(c, voc);
							car.setSediPersone(sp);
							if(p.getIdentificativo()!=null && p.getIdentificativo().getProgressivoLoc()!=null){
								Integer progressivo = Integer.parseInt(p.getIdentificativo().getProgressivoLoc());
								String cciaa = p.getIdentificativo().getPressoCciaa();
								String nrea = p.getIdentificativo().getPressoNRea();

								Sedi sedeSecondaria = findSedeSecondaria(cciaa, nrea, progressivo, sediDitta);
								car.setSede(sedeSecondaria);
							}
							carAction.inject(car);
							carAction.create();
							spCariche.add(carAction.getEntity());
						}
					}
					sp.setCariche(spCariche);
				}
			} //personeSede

		}  //dati != null

		if(isForArpav)
			newDitta.setApp("ARPAV");

		ga.updateObject(newDitta);

		if(newDitta!=null && newDitta.getInternalId()==0){
			newDitta = (PersoneGiuridiche) ga.create(newDitta);
			inject(newDitta);
			List<PersoneGiuridiche> l = new ArrayList<PersoneGiuridiche>();
			l.add(newDitta);
			injectList(l);
		}

		inject(ca.get(entityClass, newDitta.getInternalId()));
		ca.refresh(getEntity());
		if(selectedSede!=null){
			sAction.getTemporary().put("selectedSede", ca.get(sAction.entityClass, selectedSede));
			ca.refresh(sAction.getTemporary().get("selectedSede"));
		}
	}

	@SuppressWarnings("unchecked")
	private Long manageSediLocali(PersoneGiuridiche lastDitta, PersoneGiuridiche newDitta, List<Sedi> sediDitta, 
			Long selectedLoc, Long selectedSede, 
			GenericAdapterLocalInterface ga) throws PersistenceException{
		if(lastDitta!=null){
			Map<String, Object> parSedi = new HashMap<String, Object>();
			parSedi.put("id", lastDitta.getInternalId());
			List<Sedi> listaSediLocali = (List<Sedi>)ga.executeHQL(QUERY_SEDI_LOCALI, parSedi);
			for(Sedi oldSede : listaSediLocali){
				Sedi newSede = new Sedi();
				newSede.setAttivita(oldSede.getAttivita());
				newSede.setCausaleCessazione(oldSede.getCausaleCessazione());
				newSede.setDataCancellazione(oldSede.getDataCancellazione());
				newSede.setDataCessazione(oldSede.getDataCessazione());
				newSede.setDataDenunciaCessazione(oldSede.getDataDenunciaCessazione());
				newSede.setDataInizioAttivita(oldSede.getDataInizioAttivita());
				newSede.setDeletable(oldSede.getDeletable());
				newSede.setDenominazioneUnitaLocale(oldSede.getDenominazioneUnitaLocale());
				newSede.setInsegna(oldSede.getInsegna());
				newSede.setLatitudine(oldSede.getLatitudine());
				newSede.setLongitudine(oldSede.getLongitudine());
				newSede.setNumeroREA(oldSede.getNumeroREA());
				newSede.setProgressivoUnitaLocale(oldSede.getProgressivoUnitaLocale());
				newSede.setProvinciaCCIAA(oldSede.getProvinciaCCIAA());
				//newSede.setSedeid(s.getSedeid());
				newSede.setSedePrincipale(oldSede.getSedePrincipale());
				newSede.setStato(oldSede.getStato());
				newSede.setStatoAttivita(oldSede.getStatoAttivita());
				newSede.setTipo1(oldSede.getTipo1());
				newSede.setTipo2(oldSede.getTipo2());
				newSede.setTipo3(oldSede.getTipo3());
				newSede.setTipo4(oldSede.getTipo4());
				newSede.setTipo5(oldSede.getTipo5());
				newSede.setTipologia(oldSede.getTipologia());
				if(oldSede.getAddr()!=null)
					newSede.setAddr(oldSede.getAddr().cloneAd());
				if(oldSede.getTelecom()!=null)
					newSede.setTelecom(oldSede.getTelecom().cloneTel());

				newSede.setPersonaGiuridica(newDitta);
				newSede = (Sedi) ga.updateObject(newSede);
				sediDitta.add(newSede);		

				if(selectedLoc!=null && selectedLoc.equals(oldSede.getInternalId())){
					selectedSede = newSede.getInternalId();
				}

				//attivita istat
				String queryAttIstat = "SELECT a FROM AttivitaIstat a JOIN a.sedi s WHERE s.internalId = :id AND s.isActive = 1";
				Map<String, Object> parAtt = new HashMap<String, Object>();
				parAtt.put("id", oldSede.getInternalId());
				List<AttivitaIstat> listaAttIstat = (List<AttivitaIstat>)ga.executeHQL(queryAttIstat, parAtt);
				for(AttivitaIstat oldAtt : listaAttIstat){
					AttivitaIstat newAtt = new AttivitaIstat();
					newAtt.setAttivita(oldAtt.getAttivita());
					newAtt.setCfonte(oldAtt.getCfonte());
					newAtt.setCode(oldAtt.getCode());
					newAtt.setDataInizioAttivita(oldAtt.getDataInizioAttivita());
					newAtt.setDescrizione(oldAtt.getDescrizione());
					newAtt.setFonte(oldAtt.getFonte());
					newAtt.setImportanza(oldAtt.getImportanza());
					newAtt.setSedi(newSede);
					ga.createObject(newAtt);
				}

				//ruoli
				String queryRuoli = "SELECT r FROM Ruoli r JOIN r.sedi s WHERE s.internalId = :id AND s.isActive = 1";
				Map<String, Object> parRuoli = new HashMap<String, Object>();
				parRuoli.put("id", oldSede.getInternalId());
				List<Ruoli> listaRuoli = (List<Ruoli>)ga.executeHQL(queryRuoli, parRuoli);
				for(Ruoli oldRole : listaRuoli){
					Ruoli newRole = new Ruoli();
					newRole.setAltroRuolo(oldRole.getAltroRuolo());
					newRole.setCategoria(oldRole.getCategoria());
					newRole.setCausaleCessazione(oldRole.getCausaleCessazione());
					newRole.setCode(oldRole.getCode());
					newRole.setDataCessazione(oldRole.getDataCessazione());
					newRole.setDataDeliberaCessazione(oldRole.getDataDeliberaCessazione());
					newRole.setDataDenuncia(oldRole.getDataDenuncia());
					newRole.setDataDomandaCessazione(oldRole.getDataDomandaCessazione());
					newRole.setDataIscrizione(oldRole.getDataIscrizione());
					newRole.setDescrizione(oldRole.getDescrizione());
					newRole.setEnteRilascio(oldRole.getEnteRilascio());
					newRole.setFascia(oldRole.getFascia());
					newRole.setForma(oldRole.getForma());
					newRole.setLettera(oldRole.getLettera());
					newRole.setNumero(oldRole.getNumero());
					newRole.setProvincia(oldRole.getProvincia());
					newRole.setQualifica(oldRole.getQualifica());
					newRole.setTipo(oldRole.getTipo());
					newRole.setUltDescrizione(oldRole.getUltDescrizione());
					newRole.setVolume(oldRole.getVolume());
					newRole.setSedi(newSede);
					ga.createObject(newRole);
				}

				//cariche
				String queryCariche = "SELECT c, sp, p FROM Cariche c JOIN c.sede s JOIN c.sediPersone sp JOIN sp.person p WHERE s.internalId = :id AND s.isActive = 1";
				Map<String, Object> parCariche = new HashMap<String, Object>();
				parCariche.put("id", oldSede.getInternalId());
				List<Object[]> listaCariche = (List<Object[]>)ga.executeHQL(queryCariche, parCariche);
				for(Object[] obj : listaCariche){
					if(obj[0] instanceof Cariche){
						Cariche oldCar = (Cariche) obj[0];
						Cariche newCar = new Cariche();
						newCar.setCarica(oldCar.getCarica());
						newCar.setDataFine(oldCar.getDataFine());
						newCar.setDataInizio(oldCar.getDataInizio());
						newCar.setDescrizione(oldCar.getDescrizione());
						newCar.setRuolo(oldCar.getRuolo());
						newCar.setSede(newSede);


						if(obj[1] instanceof SediPersone){
							SediPersone oldSp = (SediPersone) obj[1];
							SediPersone newSp = new SediPersone();
							newSp.setCountryOfAddr(oldSp.getCountryOfAddr());
							newSp.setCountryOfBirth(oldSp.getCountryOfBirth());
							newSp.setCountryOfDomicile(oldSp.getCountryOfDomicile());
							newSp.setBirthTime(oldSp.getBirthTime());
							newSp.setCategory(oldSp.getCategory());
							newSp.setCitizen(oldSp.getCitizen());
							newSp.setCode(oldSp.getCode());
							newSp.setCs(oldSp.getCs());
							newSp.setCsRegion(oldSp.getCsRegion());
							newSp.setDeathDate(oldSp.getDeathDate());
							newSp.setDeathIndicator(oldSp.getDeathIndicator());
							newSp.setEni(oldSp.getEni());
							newSp.setFiscalCode(oldSp.getFiscalCode());
							newSp.setGenderCode(oldSp.getGenderCode());
							newSp.setHL7MsgDate(oldSp.getHL7MsgDate());
							newSp.setIsGiuridica(oldSp.getIsGiuridica());
							newSp.setMaritalStatusCode(oldSp.getMaritalStatusCode());
							newSp.setMaster(oldSp.getMaster());
							newSp.setMpi(oldSp.getMpi());
							newSp.setReliability(oldSp.getReliability());
							newSp.setStp(oldSp.getStp());
							newSp.setTeamCode(oldSp.getTeamCode());
							newSp.setTeamIdent(oldSp.getTeamIdent());
							newSp.setTeamInst(oldSp.getTeamInst());
							newSp.setTeamPers(oldSp.getTeamPers());
							newSp.setToUpdate(oldSp.getToUpdate());
							if(oldSp.getAddr()!=null)
								newSp.setAddr(oldSp.getAddr().cloneAd());
							if(oldSp.getDomicileAddr()!=null)
								newSp.setDomicileAddr(oldSp.getDomicileAddr().cloneAd());
							if(oldSp.getBirthPlace()!=null)
								newSp.setBirthPlace(oldSp.getBirthPlace().cloneAd());
							if(oldSp.getTelecom()!=null)
								newSp.setTelecom(oldSp.getTelecom().cloneTel());

							if(obj[2] instanceof Person)
								newSp.setPerson((Person) obj[2]);

							newCar.setSediPersone(newSp);							
							ga.createObject(newCar);
						}
					}
				}
			}								
		}
		return selectedSede;
	}

	public void linkUnlinkTagDitta(List<TagDitta> link, List<TagDitta> unlink) throws PhiException {
		if(entity==null)
			return;

		List<TagDitta> lista = new ArrayList<TagDitta>();

		TagDittaAction tagAction = TagDittaAction.instance();

		if(link!=null){			
			for(TagDitta tag : link){
				tag.setDittaCf(entity.getCodiceFiscale());
				tagAction.inject(tag);
				tagAction.create();
				lista.add(tagAction.getEntity());

			}
			tagAction.eject();
		}
		if(unlink!=null){
			for(TagDitta tag : unlink){
				lista.remove(tag);
				tagAction.inject(tag);
				if(tag.getInternalId()!=0){
					tagAction.delete();
				}
			}
			tagAction.eject();
		}

		tagAction.injectList(lista);
		tagAction.injectEmptyList("ToRemoveTagDittaList");
	}

	public void deleteAttivita(Sedi sede) throws PhiException{
		if(entity==null || sede==null)
			return;

		if(sede.getSedePrincipale()){
			AttivitaIstatAction attAction = AttivitaIstatAction.instance();
			List<AttivitaIstat> lista = entity.getAttivitaIstat();
			if(lista!=null){
				for(AttivitaIstat att : lista){
					if(att.getInternalId()>0 && !ca.contains(att)){
						att=ca.get(AttivitaIstat.class, att.getInternalId());
					}
					attAction.inject(att);
					attAction.delete();
				}
				attAction.eject();
			}

			refresh();
			attAction.injectEmptyList();
			attAction.injectEmptyList("ToRemoveAttivitaIstatList");
		}
	}

	public void linkUnlinkAttivita(List<AttivitaIstat> link, List<AttivitaIstat> unlink, boolean replaceListInConversation) throws PhiException {
		if(entity==null)
			return;

		Sedi sedePrincipale = entity.getSedePrincipale();

		List<AttivitaIstat> lista = new ArrayList<AttivitaIstat>();

		AttivitaIstatAction attAction = AttivitaIstatAction.instance();

		if(link!=null){			
			for(AttivitaIstat attivita : link){
				if(attivita.getInternalId()>0 && !ca.contains(attivita)){
					attivita=ca.get(AttivitaIstat.class, attivita.getInternalId());
				}
				attivita.setPersoneGiuridiche(entity);
				if(attivita.getSedi()==null)
					attivita.setSedi(sedePrincipale);

				attAction.inject(attivita);
				attAction.create();
				lista.add(attAction.getEntity());

			}
			attAction.eject();
		}
		if(unlink!=null){
			for(AttivitaIstat att : unlink){
				if(att.getInternalId()>0 && !ca.contains(att)){
					att=ca.get(AttivitaIstat.class, att.getInternalId());
				}
				lista.remove(att);
				attAction.inject(att);
				attAction.delete();
			}
			attAction.eject();
		}

		refresh();
		if(replaceListInConversation){
			attAction.injectList(lista);
			attAction.injectEmptyList("ToRemoveAttivitaIstatList");
		}else{
			link = lista;
		}

	}

	/**
	 * I controlli da fare sono:
	 * o sulla partita iva o denominazione (non assieme, basta che una delle due coincida) per le persone giuridiche
	 * @param p
	 * @return
	 */
	public boolean checkAlreadyPresent(PersoneGiuridiche p){
		if(p.getInternalId()<=0){
			String hql = "SELECT COUNT(p) FROM PersoneGiuridiche p " +
					"WHERE p.codiceFiscale = :cf OR p.patritaIva = :piva OR p.denominazione = :den";

			Query q = ca.createQuery(hql);
			q.setParameter("cf", p.getCodiceFiscale());
			q.setParameter("piva", p.getPatritaIva());
			q.setParameter("den", p.getDenominazione());
			Object count = q.getSingleResult();
			if(count instanceof Long && ((Long)count)>0){
				return true;
			}
		}

		return false;
	}

	private CodeValue getOrCreateCode(String csName, String domainParent, String domainCode, String code, String description, Vocabularies voc) {
		if(csName==null || domainParent==null || domainCode==null || code==null || description==null || voc==null)
			return null;

		code = code.replaceAll("\\W", "").toLowerCase();

		CodeValue cvForma = null;
		try{
			CodeValue domain = voc.getCodeValue(csName, domainParent, domainCode, "S");

			cvForma = voc.getCodeValue(csName, domain.getDisplayName(), code, "C");
			FacesMessages.instance().clear();
			if(cvForma == null){
				CodeSystem codeSystem = CodeSystemAction.instance().getCodeSystem(csName);
				if(codeSystem instanceof HibernateProxy)
					codeSystem = (((CodeSystem)((HibernateProxy)codeSystem).getHibernateLazyInitializer().getImplementation()));

				cvForma = CodeValueAction.instance().newEntity(codeSystem.getCodeValueClass());
				cvForma.setId(domain.getOid() + "." + code + "_V0");
				cvForma.setOid(domain.getOid() + "." + code);
				cvForma.setCreator(UserBean.instance().getUsername());
				cvForma.setDefaultChild(false);
				cvForma.setSequenceNumber(0);
				cvForma.setType("C");
				cvForma.setValidFrom(new Date());
				cvForma.setVersion(codeSystem.getVersion());
				cvForma.setCodeSystem(codeSystem);
				cvForma.setParent(domain);
				cvForma.setStatus(1);
				cvForma.setCode(code);
				cvForma.setDisplayName(code);
				cvForma.setLangIt(description);
				cvForma.setDescription(description);
				GenericAdapterLocalInterface ga = GenericAdapter.instance();
				ga.createObject(cvForma);
			}
		}catch (Exception e){
			log.error("Error on getOrCreateCode for code: "+ code);
			return null;//non ritornare il codice non flushato altrimenti poi si generano transientobjectexception			
		}


		return cvForma;
	}

	private Cariche caricaToCariche(Carica c, Vocabularies voc){
		Cariche car = new Cariche();
		CodeValue code = getOrCreateCode("PHIDIC", "Company", "cariche", c.getCcarica(), c.getDescrizione(), voc);
		car.setCarica(code);
		if(c.getDtInizio()!=null)
			car.setDataInizio(parseParixDate(c.getDtInizio().toString()));

		return car;
	}

	private SediPersone personaToSediPersona(Persona p, Vocabularies voc) throws PersistenceException, DictionaryException{
		SediPersone sp = new SediPersone();
		if(sp.getName()==null)
			sp.setName(new EN());

		if(p.getPersonaFisica()!=null){
			sp.setIsGiuridica(false);
			PersonaFisica det = p.getPersonaFisica();
			sp.setFiscalCode(det.getCodiceFiscale());

			sp.getName().setFam(det.getCognome());
			sp.getName().setGiv(det.getNome());
			AD addr = indirizzoToAddr(det.getIndirizzo(), voc);
			sp.setAddr(addr);
			if(det.getSesso()!=null && det.getSesso().length()>=1){
				sp.setGenderCode((CodeValuePhi)voc.getCodeValue("PHIDIC", "Gender", det.getSesso().substring(0, 1), "C"));
			}

			if(det.getEstremiNascita()!=null){
				EstremiNascita en = det.getEstremiNascita();
				AD birthPlace = new AD();
				birthPlace.setCpa(en.getProvincia());	//provincia
				birthPlace.setCty(en.getComune());		//città
				if(en.getCcomune()!=null){
					String ccomune = en.getCcomune();
					if(ccomune.length()==4)
						ccomune="00"+ccomune;
					else if(ccomune.length()==5)
						ccomune="0"+ccomune;

					try {
						addr.setCode((CodeValueCity)voc.getCodeValue("Comuni", "", ccomune, "C"));
						//SE HO IL CODICE DEL COMUNE, CERCO DI SOSTITUIRE LA PROVINCIA FORNITA (NON ABBREVIATA)
						//PARIX RITORNA PROVINCIA = "VICENZA" NON "VI"
						if(addr.getCode()!=null){
							addr.setCpa(addr.getCode().getProvince());
							addr.setZip(addr.getCode().getZip());
						}
					} catch (Exception e) {
						log.error("Error getting COMUNE: "+ ccomune);
					}
				}
				sp.setBirthPlace(birthPlace);
				if(en.getData()!=null){
					sp.setBirthTime(parseParixDate(en.getData()));
				}
			}
		}else if(p.getPersonaGiuridica()!=null){
			sp.setIsGiuridica(true);
			PersonaGiuridica det = p.getPersonaGiuridica();

			sp.setFiscalCode(det.getCodiceFiscale());
			sp.getName().setGiv(det.getDenominazione());
			AD addr = indirizzoToAddr(det.getIndirizzo(), voc);
			sp.setAddr(addr);
		}

		return sp;
	}

	private Long localizzazioniToSedi(DatiImpresa dati, PersoneGiuridiche ditta, List<Sedi> sediDitta, Long selectedLoc, Long selectedSede, Vocabularies voc, GenericAdapterLocalInterface ga) throws PhiException {
		if(dati.getLocalizzazioni() != null) {

			Map<String, Object> parSedi = new HashMap<String, Object>();
			parSedi.put("id", ditta.getInternalId());
			List<Sedi> listaSediAttuali = (List<Sedi>)ga.executeHQL(QUERY_SEDI, parSedi);

			for(Localizzazioni localizzazioni : dati.getLocalizzazioni()) {
				if(localizzazioni.getLocalizzazione() != null){
					for(Localizzazione loc : localizzazioni.getLocalizzazione()) {

						String locIndex = localizzazioni.getElemento() + "." + loc.getElemento();
						boolean alreadyPresent = false;

						if (listaSediAttuali != null) {
							for (Sedi sede : listaSediAttuali) {
								if (locIndex.equals(sede.getElemento())) {
									// Non sovrascrivere ditta già importata, vedi: I00083158 - [SPISAL/ARPAV-SIPRAL] INTEGRAZIONE CON PARIX - MODIFICA LOGICA RECUPERO DATI DA PARIX E AGGIORNAMENTO DITTA
									alreadyPresent = true;
									break;
								}
							}
						}

						if (!alreadyPresent) {

							Sedi sedeSecondaria = localizzazioneToSedi(loc, locIndex, voc);
							//se il servizio non mi ritorna la denominazione della sede secondaria, la valorizzo con quella della sede principale
							if(sedeSecondaria.getDenominazioneUnitaLocale()==null || sedeSecondaria.getDenominazioneUnitaLocale().isEmpty()){
								sedeSecondaria.setDenominazioneUnitaLocale(truncate(dati.getEstremiImpresa().getDenominazione(),255));
							}

							sedeSecondaria.setPersonaGiuridica(ditta);
							sedeSecondaria = (Sedi) ga.updateObject(sedeSecondaria);
							sediDitta.add(sedeSecondaria);

							//SEDE SELEZIONATA
							if(selectedLoc!=null && selectedLoc.equals(loc.getInternalId())){
								selectedSede = sedeSecondaria.getInternalId();
							}

							if(loc.getCodiceAtecoUl()!=null){
								attivitaSede(sedeSecondaria, loc.getCodiceAtecoUl().getAttivitaIstat(), voc, ga);
							}

							if(loc.getRuoliLoc()!=null){
								ruoliSede(sedeSecondaria, loc.getRuoliLoc().getRuoloLoc(), voc, ga);
							}

						}
					}
				}
			}
		}
		return selectedSede;
	}

	private Sedi localizzazioneToSedi(Localizzazione loc, String locIndex, Vocabularies voc) throws PersistenceException, DictionaryException{
		Sedi sedeSecondaria = new Sedi();
		sedeSecondaria.setElemento(locIndex);
		sedeSecondaria.setSedePrincipale(false);
		if(loc.getAttivita()!=null){
			String attivita = loc.getAttivita();
			int length = attivita.length();
			attivita = attivita.substring(0,length<=255?length:255);
			sedeSecondaria.setAttivita(attivita);
		}
		sedeSecondaria.setDenominazioneUnitaLocale(loc.getDenominazione());
		sedeSecondaria.setInsegna(loc.getInsegna());

		if(loc.getDtApertura()!=null)
			sedeSecondaria.setDataInizioAttivita(parseParixDate(loc.getDtApertura().toString()));

		if(loc.getIndirizzo()!=null){

			AD addr = indirizzoToAddr(loc.getIndirizzo(), voc);
			TEL telecom = new TEL();
			telecom.setMc(loc.getIndirizzo().getIndirizzoPec());
			sedeSecondaria.setAddr(addr);
			sedeSecondaria.setTelecom(telecom);
		}

		if(loc.getNumeroTipo()!=null){
			if(loc.getNumeroTipo().getNumero()!=null)
				sedeSecondaria.setProgressivoUnitaLocale(loc.getNumeroTipo().getNumero().intValue());

			if(loc.getNumeroTipo().getNrea()!=null)
				sedeSecondaria.setNumeroREA(loc.getNumeroTipo().getNrea().toString());

			sedeSecondaria.setProvinciaCCIAA(loc.getNumeroTipo().getCciaa());

			String code = loc.getNumeroTipo().getTipo1();	

			CodeValue tipo1 = getOrCreateCode("PHIDIC", "Company", "tipo1", code, code, voc);
			if(tipo1 != null){
				sedeSecondaria.setTipo1(tipo1);
			}
			code = loc.getNumeroTipo().getTipo2();
			CodeValue tipo2 = getOrCreateCode("PHIDIC", "Company", "tipo1", code, code, voc);
			if(tipo2 != null){
				sedeSecondaria.setTipo2(tipo2);
			}
			code = loc.getNumeroTipo().getTipo3();
			CodeValue tipo3 = getOrCreateCode("PHIDIC", "Company", "tipo1", code, code, voc);
			if(tipo3 != null){
				sedeSecondaria.setTipo3(tipo3);
			}
			code = loc.getNumeroTipo().getTipo4();
			CodeValue tipo4 = getOrCreateCode("PHIDIC", "Company", "tipo1", code, code, voc);
			if(tipo4 != null){
				sedeSecondaria.setTipo4(tipo4);
			}
			code = loc.getNumeroTipo().getTipo5();
			CodeValue tipo5 = getOrCreateCode("PHIDIC", "Company", "tipo1", code, code, voc);
			if(tipo5 != null){
				sedeSecondaria.setTipo5(tipo5);
			}
		}

		if(loc.getCessazioneLoc()!=null){
			if(loc.getCessazioneLoc().getDtCessazione()!=null)
				sedeSecondaria.setDataCessazione(parseParixDate(loc.getCessazioneLoc().getDtCessazione().toString()));

			sedeSecondaria.setCausaleCessazione(loc.getCessazioneLoc().getCausale());
			if(loc.getCessazioneLoc().getDtDenunciaCess()!=null)
				sedeSecondaria.setDataDenunciaCessazione(parseParixDate(loc.getCessazioneLoc().getDtDenunciaCess().toString()));
			if(loc.getCessazioneLoc().getDtCancellazione()!=null)
				sedeSecondaria.setDataCancellazione(parseParixDate(loc.getCessazioneLoc().getDtCancellazione().toString()));
		}

		return sedeSecondaria;
	}

	private void attivitaSede(Sedi sedeSecondaria, List<com.phi.parix.json.detail.AttivitaIstat> listaAttivita, 
			Vocabularies voc, GenericAdapterLocalInterface ga) throws PersistenceException, DictionaryException{

		if(listaAttivita!=null && sedeSecondaria!=null){
			boolean isMaster = sedeSecondaria.getSedePrincipale();

			for(com.phi.parix.json.detail.AttivitaIstat attIstat : listaAttivita){
				if(attIstat.getCattivita()!=null){
					String code = attIstat.getCattivita();
					String pointCode = "";
					if(code!=null){
						int i = 0;
						for(char c : code.toCharArray()){
							if(i!=0 && i%2==0){
								pointCode+=".";
							}
							pointCode+=c;
							i++;
						}

						CodeValue ateco = voc.getCodeValue("ATECO", "", pointCode, null);
						AttivitaIstat att = new AttivitaIstat();
						att.setCode((CodeValueAteco)ateco);
						if(attIstat.getDtInizioAttivita()!=null)
							att.setDataInizioAttivita(parseParixDate(attIstat.getDtInizioAttivita().toString()));


						String codeImp = attIstat.getCimportanza();
						CodeValue cvImp = getOrCreateCode("PHIDIC", "AttivitaIstat", "imp", codeImp, codeImp, voc);
						if(cvImp != null){
							att.setImportanza(cvImp);
						}

						String codeFonte = attIstat.getCfonte();
						CodeValue cvFonte = getOrCreateCode("PHIDIC", "AttivitaIstat", "fonte", codeFonte, codeFonte, voc);
						if(cvFonte != null){
							att.setCfonte((CodeValuePhi)cvFonte);
						}

						att.setSedi(sedeSecondaria);
						if(isMaster){
							att.setPersoneGiuridiche(sedeSecondaria.getPersonaGiuridica());
						}
						ga.createObject(att);
					}
				}
			}
		}
	}

	private void ruoliSede(Sedi sedeSecondaria, List<RuoloLoc> listaRuoli, Vocabularies voc, GenericAdapterLocalInterface ga) throws PhiException{
		if(listaRuoli!=null){
			for(RuoloLoc ruolo : listaRuoli){
				Ruoli newRole = new Ruoli();
				if(ruolo.getImpiantistiLoc()!=null){
					CodeValue code = voc.getCodeValue("PHIDIC","Role","Imp","C");
					newRole.setCode(code);

					CodeValue lettera = getOrCreateCode("PHIDIC", "Localization", "lettera", ruolo.getImpiantistiLoc().getLettera(), ruolo.getImpiantistiLoc().getLettera(), voc);
					newRole.setLettera((CodeValuePhi)lettera);

					newRole.setProvincia(ruolo.getImpiantistiLoc().getProvincia());
					newRole.setNumero(ruolo.getImpiantistiLoc().getNumero());
					if(ruolo.getImpiantistiLoc().getDtIscrizione()!=null)
						newRole.setDataIscrizione(parseParixDate(ruolo.getImpiantistiLoc().getDtIscrizione()));

					newRole.setEnteRilascio(ruolo.getImpiantistiLoc().getEnteRilascio());
				}else if(ruolo.getMeccanici()!=null){
					CodeValue code = voc.getCodeValue("PHIDIC","Role","Mec","C");
					newRole.setCode(code);

					CodeValue tipo = getOrCreateCode("PHIDIC", "Localization", "tiporuolo", ruolo.getMeccanici().getTipo(), ruolo.getMeccanici().getTipo(), voc);
					newRole.setTipo((CodeValuePhi)tipo);

					newRole.setProvincia(ruolo.getMeccanici().getProvincia());
					newRole.setNumero(ruolo.getMeccanici().getNumero());
					if(ruolo.getMeccanici().getDtIscrizione()!=null)
						newRole.setDataIscrizione(parseParixDate(ruolo.getMeccanici().getDtIscrizione()));

					newRole.setQualifica(ruolo.getMeccanici().getQualifica());
				}else if(ruolo.getImpresePulizia()!=null){
					CodeValue code = voc.getCodeValue("PHIDIC","Role","Pulizia","C");
					newRole.setCode(code);

					newRole.setFascia(ruolo.getImpresePulizia().getFascia());
					newRole.setVolume(ruolo.getImpresePulizia().getVolume());
					if(ruolo.getImpresePulizia().getDtDenuncia()!=null)
						newRole.setDataDenuncia(parseParixDate(ruolo.getImpresePulizia().getDtDenuncia()));
				}else if(ruolo.getAltroRuoloLoc()!=null){
					if(ruolo.getAltroRuoloLoc().getAltroRuoloNonCCIAA()!=null){
						CodeValue code = voc.getCodeValue("PHIDIC","Role","AltroNonCCIAA","C");
						newRole.setCode(code);

						newRole.setForma(ruolo.getAltroRuoloLoc().getAltroRuoloNonCCIAA().getForma());
						newRole.setProvincia(ruolo.getAltroRuoloLoc().getAltroRuoloNonCCIAA().getProvincia());
						newRole.setEnteRilascio(ruolo.getAltroRuoloLoc().getAltroRuoloNonCCIAA().getEnteRilascio());
					}else if(ruolo.getAltroRuoloLoc().getAltroRuoloCCIAA()!=null){
						CodeValue code = voc.getCodeValue("PHIDIC","Role","AltroCCIAA","C");
						newRole.setCode(code);

						newRole.setCategoria(ruolo.getAltroRuoloLoc().getAltroRuoloCCIAA().getCategoria());
						newRole.setForma(ruolo.getAltroRuoloLoc().getAltroRuoloCCIAA().getForma());
						newRole.setProvincia(ruolo.getAltroRuoloLoc().getAltroRuoloCCIAA().getProvincia());

					}else{
						CodeValue code = voc.getCodeValue("PHIDIC","Role","Altro","C");
						newRole.setCode(code);

						CodeValue altro = getOrCreateCode("PHIDIC", "Localization", "altroruolo", ruolo.getAltroRuoloLoc().getCruolo(), ruolo.getAltroRuoloLoc().getCruolo(), voc);
						newRole.setAltroRuolo((CodeValuePhi)altro);

						newRole.setDescrizione(ruolo.getAltroRuoloLoc().getDescrizione());
						newRole.setUltDescrizione(ruolo.getAltroRuoloLoc().getUltDescrizione());
						newRole.setNumero(ruolo.getAltroRuoloLoc().getNumero());
						if(ruolo.getAltroRuoloLoc().getDtIscrizione()!=null)
							newRole.setDataIscrizione(parseParixDate(ruolo.getAltroRuoloLoc().getDtIscrizione()));

					}
				}else if(ruolo.getCessazioneRuolo()!=null){
					CodeValue code = voc.getCodeValue("PHIDIC","Role","Cessazione","C");
					newRole.setCode(code);
					newRole.setCausaleCessazione(ruolo.getCessazioneRuolo().getCausaleCessazione());
					if(ruolo.getCessazioneRuolo().getDtCessazione()!=null)
						newRole.setDataCessazione(parseParixDate(ruolo.getCessazioneRuolo().getDtCessazione()));
					if(ruolo.getCessazioneRuolo().getDtDelibera()!=null)
						newRole.setDataDeliberaCessazione(parseParixDate(ruolo.getCessazioneRuolo().getDtDelibera()));
					if(ruolo.getCessazioneRuolo().getDtDomanda()!=null)
						newRole.setDataDomandaCessazione(parseParixDate(ruolo.getCessazioneRuolo().getDtDomanda()));

				}

				newRole.setSedi(sedeSecondaria);
				newRole = (Ruoli) ga.updateObject(newRole);
			}
		}
	}

	private Sedi findSedeSecondaria(String cciaa, String nrea, Integer progressivo, List<Sedi> sediDitta){

		if(sediDitta!=null && cciaa!=null && nrea!=null && progressivo!=null){
			for(Sedi s : sediDitta){
				if(cciaa.equals(s.getProvinciaCCIAA()) && nrea.equals(s.getNumeroREA()) && progressivo.equals(s.getProgressivoUnitaLocale())){
					return s;
				}
			}
		}
		return null;
	}

	private AD indirizzoToAddr(Indirizzo indirizzo, Vocabularies voc){
		AD addr = new AD();
		if(indirizzo==null)
			return addr;
		String via = "";
		if(indirizzo.getToponimo()!=null)
			via+=(indirizzo.getToponimo()+" ");

		via+=indirizzo.getVia();
		addr.setStr(via); 						//indirizzo
		addr.setBnr(indirizzo.getNcivico());	//numero
		addr.setCpa(indirizzo.getProvincia());	//provincia
		addr.setCty(indirizzo.getComune());		//città
		if(indirizzo.getCap()!=null)
			addr.setZip(indirizzo.getCap());	//cap

		if(indirizzo.getCcomune()!=null){
			String ccomune = indirizzo.getCcomune();
			if(ccomune.length()==4)
				ccomune="00"+ccomune;
			else if(ccomune.length()==5)
				ccomune="0"+ccomune;

			try {
				addr.setCode((CodeValueCity)voc.getCodeValue("Comuni", "", ccomune, "C"));
				//SE HO IL CODICE DEL COMUNE, CERCO DI SOSTITUIRE LA PROVINCIA FORNITA (NON ABBREVIATA)
				//PARIX RITORNA PROVINCIA = "VICENZA" NON "VI"
				if(addr.getCode()!=null){
					addr.setCpa(addr.getCode().getProvince());
				}
			} catch (Exception e) {
				log.error("Error getting COMUNE: "+ ccomune);
			}
		}

		return addr;
	}

	private List<Sedi> saveParixData(PersoneGiuridiche newDitta, List<PersoneGiuridiche> listaDitteDaSalvare,List<Sedi> sediDitta) throws PhiException{


		List<Sedi> newList = new ArrayList<Sedi>();
		if(sediDitta!=null){
			SediAction sAction = SediAction.instance();

			for(Sedi s : sediDitta){
				s.setPersonaGiuridica(getEntity());
				sAction.inject(s);
				sAction.create();
				newList.add(sAction.getEntity());
			}
			sediDitta = newList;
		}

		return newList;
	}

	public boolean hasPratiche(PersoneGiuridiche pg){

		boolean result = false;

		if(pg.getProtocollo()!=null && !pg.getProtocollo().isEmpty()){

			ProtocolloAction pa = new ProtocolloAction();
			for(Protocollo p : pg.getProtocollo()){
				if(result==false){
					if(p!=null && p.getProcpratiche()!=null && p.getIsActive() && pa.hasDitteAssociate(p)){
						result = true;
					}
				}
			}
		}

		return result;

	}

	public boolean hasProvvedimenti(PersoneGiuridiche pg){
		return hasProvvedimenti(pg,false);
	}

	public boolean hasProvvedimenti(PersoneGiuridiche pg, boolean checkOnlyIter758){

		boolean result = false;


		if(pg.getProtocollo()!=null && !pg.getProtocollo().isEmpty()){

			for(Protocollo p : pg.getProtocollo()){


				if(p!=null && p.getProcpratiche()!=null){


					List<Attivita> attivitaList = p.getProcpratiche().getAttivita();
					if(attivitaList!=null && !attivitaList.isEmpty()){

						for(Attivita a:attivitaList){
							List<Provvedimenti> provvedimentiAttivita = a.getProvvedimenti();
							if(provvedimentiAttivita != null && !provvedimentiAttivita.isEmpty()){

								for(Provvedimenti prov:provvedimentiAttivita){

									if(prov.getIsActive()){

										if(!checkOnlyIter758){
											if(result==false) result=true;

										}else{
											if(result==false && prov.getType()!=null && prov.getType().getCode().equals("758")) result=true;
										}
									}

								}

							}
						}

					}

				}
			}
		}


		return result;

	}

	private String truncate(String input, Integer length){
		if(length==null || input==null || input.isEmpty())
			return input;

		String output = null;
		int len = input.length();
		if(len>length)
			output=input.substring(0, length);
		else
			output=input;	

		return output;
	}

	public Sedi getSedePrincipale(PersoneGiuridiche pg){
		if (pg!=null){
			List<Sedi> sedi = pg.getSedi();
			if (sedi != null)
				for (Sedi sede : sedi) {
					if (sede.getSedePrincipale())
						return sede;
				}
		}

		return null;
	}

	/**
	 * filtra le sedi e ditte dati i filtri su attività istat/comparto ateco/specificazione
	 * 
	 * @param lista persone giuridiche
	 * @param entity legata al popup ateco (AttivitaIstat) con cui scegliere l'attività istat
	 * @return
	 */
	public void filterAteco(String atecoFilterMode){

		if("compartoAteco".equals(atecoFilterMode)){
			CodeValue cv = (CodeValue)getTemporary().get("CompartoAteco");
			if(cv!=null && cv.getId()!=null){
				String hql = "SELECT DISTINCT ateco.id FROM CompartoAteco ca JOIN ca.ateco ateco JOIN ca.comparto spec JOIN spec.parent comparto " +
						"WHERE spec.id = :comparto OR comparto.id = :comparto";
				Query q = ca.createQuery(hql);
				q.setParameter("comparto", cv.getId());
				List<String> ids = q.getResultList();
				if(ids!=null && !ids.isEmpty()){
					getIn().put("attivitaIstat.code.id", ids);
				}
			}
		}else if("specificazione".equals(atecoFilterMode)){
			CodeValue cv = (CodeValue)getTemporary().get("Specificazione");
			if(cv!=null && cv.getId()!=null){
				String hql = "SELECT DISTINCT ateco.id FROM CompartoAteco ca JOIN ca.ateco ateco JOIN ca.comparto spec " +
						"WHERE spec.id = :specificazione";
				Query q = ca.createQuery(hql);
				q.setParameter("specificazione", cv.getId());
				List<String> ids = q.getResultList();
				if(ids!=null && !ids.isEmpty()){
					getIn().put("attivitaIstat.code.id", ids);
				}
			}
		}
	}

	public void filterTag(TipologiaDitta tipo){

		getIn().remove("codiceFiscale");
		if(tipo!=null){
			String qryCf = "SELECT DISTINCT tag.dittaCf FROM TagDitta tag JOIN tag.tipologiaDitta tipo WHERE tipo.internalId = :tipoId";
			Query qCf = ca.createQuery(qryCf);
			qCf.setParameter("tipoId", tipo.getInternalId());
			List<String> cfs = qCf.getResultList();
			if(cfs!=null && !cfs.isEmpty()){
				getIn().put("codiceFiscale", cfs);
			}
		} 

	}

	public void filterMisc() throws NoSuchFieldException, SecurityException{
		getTemporary().put("hasPraticheIds",new ArrayList<Long>());
		getIn().remove("internalId");

		boolean conPratiche = Boolean.TRUE.equals(getTemporary().get("conPratiche"));
		boolean conProvvedimenti = Boolean.TRUE.equals(getTemporary().get("conProvvedimenti"));
		boolean blacklisted = Boolean.TRUE.equals(getTemporary().get("blacklisted"));

		removeExpression("this", "DittePratiche.sdlId");
		removeSubCriteria(entityCriteria, "DittePratiche");
		if(conPratiche){
			entityCriteria.createAlias("dittePratiche", "DittePratiche", Criteria.INNER_JOIN);
			entityCriteria.add(Property.forName("DittePratiche.sdlId").in(UserBean.instance().getSdLocs()));
			//entityCriteria.add(Restrictions.eqProperty("DittePratiche.cf", "this.codiceFiscale"));
		}
		removeExpression("this", "DitteProvvedimenti.sdlId");
		removeSubCriteria(entityCriteria, "DitteProvvedimenti");
		if(conProvvedimenti){
			entityCriteria.createAlias("ditteProvvedimenti", "DitteProvvedimenti", Criteria.INNER_JOIN);
			entityCriteria.add(Property.forName("DitteProvvedimenti.sdlId").in(UserBean.instance().getSdLocs()));
			//entityCriteria.add(Restrictions.eqProperty("DitteProvvedimenti.cf", "this.codiceFiscale"));
		}
		/*
		if(conPratiche){
			DetachedCriteria dittePraticheCriteria = DetachedCriteria.forClass(DittePratiche.class,"DittePratiche");
			dittePraticheCriteria.add(Property.forName("DittePratiche.cf").eqProperty("this.codiceFiscale"));
			dittePraticheCriteria.add(Property.forName("DittePratiche.sdlId").in(UserBean.instance().getSdLocs()));
			dittePraticheCriteria.setProjection(Projections.property("DittePratiche.internalId"));
			entityCriteria.add(Subqueries.exists(dittePraticheCriteria));
		}

		if(conProvvedimenti){
			DetachedCriteria ditteProvvedimentiCriteria = DetachedCriteria.forClass(DitteProvvedimenti.class,"DitteProvvedimenti");
			ditteProvvedimentiCriteria.add(Property.forName("DitteProvvedimenti.cf").eqProperty("this.codiceFiscale"));
			ditteProvvedimentiCriteria.add(Property.forName("DitteProvvedimenti.sdlId").in(UserBean.instance().getSdLocs()));
			ditteProvvedimentiCriteria.setProjection(Projections.property("DitteProvvedimenti.internalId"));
			entityCriteria.add(Subqueries.exists(ditteProvvedimentiCriteria));
		}*/


		removeExpression("this", "org.hibernate.criterion.ExistsSubqueryExpression");
		if(blacklisted){

			//TODO: provare a rendere tutta sta cosa o solo HQL o tutto senza HQL custom, c'è un po' troppo mescolamento 
			//perché se si cambiano le projections e i criteria (o il loro ordine) gli alias delle porzioni HQL non sono più gli stessi
			DetachedCriteria artCriteria = DetachedCriteria.forClass(Articoli.class,"art");
			DetachedCriteria provCriteria = artCriteria.createCriteria("provvedimento","pr",Criteria.LEFT_JOIN);
			DetachedCriteria sogCriteria = provCriteria.createCriteria("soggetto","sog",Criteria.LEFT_JOIN);
			DetachedCriteria pgCriteria = sogCriteria.createCriteria("ditta","pg",Criteria.LEFT_JOIN);
			pgCriteria.add(Restrictions.sqlRestriction("pg3_.internal_id = this_.internal_id"));
			provCriteria.add(Restrictions.sqlRestriction("DATEDIFF(pr1_.data,CURDATE() - interval 5 year) > 0"));
			artCriteria.add(Restrictions.sqlRestriction("art_.code in (select code from articoli_bl where is_active=true)"));
			artCriteria.add(Property.forName("isActive").eq(true));
			provCriteria.add(Property.forName("isActive").eq(true));
			sogCriteria.add(Property.forName("isActive").eq(true));
			pgCriteria.add(Property.forName("isActive").eq(true));
			String having = "art_.code having y0_ >= (SELECT max_iter FROM articoli_bl WHERE code = art_.code and is_active=TRUE)";
			String[] alias = new String[1]; 
			alias[0] = "art_"; 
			Type[] types = new Type[1]; 
			types[0] = Hibernate.INTEGER;

			artCriteria.setProjection(Projections.projectionList()
					.add(Projections.rowCount())
					.add(Projections.property("pr.data"))
					.add(Projections.sqlGroupProjection("art_.code", having, alias,types)) 
					);

			entityCriteria.add(Subqueries.exists(artCriteria));
		}
	}


	public void checkPratiche(Object o){
		if (o instanceof Map<?,?> ){
			Map<String,Object> pg = (Map<String,Object> )o;
			pg.put("numPratiche", checkPratiche((String)pg.get("codiceFiscale"), (String)pg.get("patritaIva"))[0].intValue());
			pg.put("numProvvedimenti", checkPratiche((String)pg.get("codiceFiscale"), (String)pg.get("patritaIva"))[1].intValue());

		}
	}

	public Long[] checkPratiche(String cf, String piva){
		Long[] ret = new Long[]{0L,0L};

		if(cf!=null && !"".equals(cf)){
			//if(piva==null)
			//	piva="";

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

	public List<Long> listProvvedimenti(PersoneGiuridiche pg){
		return listProvvedimenti(pg.getCodiceFiscale());
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

	public List<Long> listPratiche(PersoneGiuridiche pg){
		return listPratiche(pg.getCodiceFiscale());
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

	public void clearAtecoFilters(){
		getEqual().remove("attivitaIstat.code");
		getTemporary().remove("CompartoAteco");
		getTemporary().remove("Specificazione");

		getEqual().remove("attivitaIstat.code");
		getIn().remove("attivitaIstat.code.id");
	}


	public boolean checkBlackList(Object o){

		boolean result = false;

		if (o instanceof Map<?,?> ){
			Map<String,Object> pg = (Map<String,Object> )o;


			List<CodeValueLaw> blackListCv = new ArrayList<CodeValueLaw>();

			String blackListCodes = "select art from ArticoliBL art where art.isActive=1";
			Query qBlackListCodes = ca.createQuery(blackListCodes);
			List<Object> resBl = qBlackListCodes.getResultList();
			HashMap<CodeValueLaw, Integer>blackListMaximumReiterationsMap = new HashMap<CodeValueLaw, Integer>(); 

			if(resBl!=null && !resBl.isEmpty()){
				for(int i=0;i<resBl.size();i++){
					blackListCv.add(((ArticoliBL)resBl.get(i)).getCode());
					blackListMaximumReiterationsMap.put(((ArticoliBL)resBl.get(i)).getCode(),((ArticoliBL)resBl.get(i)).getMaxIter());

				}
			}else{
				return false;
			}

			Query qBlackList = ca.createQuery(IS_BLACKLISTED_QUERY);
			qBlackList.setParameter("ditta_id", pg.get("internalId"));
			qBlackList.setParameter("blackListCv", blackListCv);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -5);
			Date minDate = cal.getTime();

			qBlackList.setParameter("minDate", minDate);
			List<Object> res = qBlackList.getResultList();
			if(res!=null && !res.isEmpty()){

				for(int i=0;i<res.size();i++){

					Object[] wrap = (Object[])res.get(i);
					Articoli articoloViolato = (Articoli) wrap[0];
					Long count = (Long) wrap[1];
					if(count>=blackListMaximumReiterationsMap.get(articoloViolato.getCode())) result = true;

				}


			}
		}

		return result;

	}


	public boolean isArpavSelected()  {
		List<Long> arpavSdlIds = getSdlArpavIds();
		List<Long> selectedSdlIds = UserBean.instance().getSdLocs();

		for (Long arpavSdl : arpavSdlIds ){
			if (selectedSdlIds.contains(arpavSdl)) {
				return true;
			}
		}
		return false;
	}

	public List<Long> getSdlArpavIds() {
		String hql = "SELECT sdl.internalId FROM ServiceDeliveryLocation sdl where code.code = 'ARPAV'";

		List<Long> l;
		try {
			l = GenericAdapter.instance().executeHQL(hql, null);
			return l;
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void filterBy() {
		try {

			removeExpression("this", "denominazione");
			removeExpression("this", "codiceFiscale");
			removeExpression("this", "patritaIva");

			String denominazione = "";
			String codiceFiscale = "";
			String partitaIva = "";
			String corrispondenza = "";

			if (getTemporary().get("denominazione")!=null)
				denominazione = getTemporary().get("denominazione").toString();

			if (getTemporary().get("codiceFiscale")!=null)
				codiceFiscale = getTemporary().get("codiceFiscale").toString();

			if (getTemporary().get("partitaIva")!=null)
				partitaIva = getTemporary().get("partitaIva").toString();

			if (getTemporary().get("corrispondenza")!=null)
				corrispondenza = getTemporary().get("corrispondenza").toString();			

			if ("".equals(corrispondenza) || "Tutti".equals(corrispondenza)){
				if (!"".equals(denominazione))
					((FilterMap)getLike()).put("denominazione", denominazione);

				if (!"".equals(codiceFiscale))
					((FilterMap)getLike()).put("codiceFiscale", codiceFiscale);

				if (!"".equals(partitaIva))
					((FilterMap)getLike()).put("patritaIva", partitaIva);
			} else if (!"".equals(denominazione) || !"".equals(codiceFiscale) || !"".equals(partitaIva)){

				/* filtro con criteriaquery - mette in or i paramentri di ricerca */
				if (!"".equals(denominazione) && !denominazione.contains("%"))
					denominazione = "%" + denominazione + "%";

				if (!"".equals(codiceFiscale) && !codiceFiscale.contains("%"))
					codiceFiscale = "%" + codiceFiscale + "%";

				if (!"".equals(partitaIva) && !partitaIva.contains("%"))
					partitaIva = "%" + partitaIva + "%";

				LogicalExpression logicalDates = Restrictions.or(Restrictions.like("denominazione", denominazione).ignoreCase(),
						Restrictions.or(Restrictions.like("codiceFiscale", codiceFiscale).ignoreCase(),
								Restrictions.like("patritaIva", partitaIva).ignoreCase())	);

				entityCriteria.add(logicalDates);

			}

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

	//I0066939 COPIA SOLO ALCUNE PROPRIETA'
	/*
	public PersoneGiuridiche copyForArpav(PersoneGiuridiche toCopy){
		try {
			PersoneGiuridiche copy = new PersoneGiuridiche();

			copy.setDenominazione(toCopy.getDenominazione());
			copy.setCodiceFiscale(toCopy.getCodiceFiscale());
			copy.setPatritaIva(toCopy.getPatritaIva());

			ca.create(copy);
			return copy;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}*/

	/*
	public AD getSedeLegale(PersoneGiuridiche pg){

			if(pg == null)
				return null;

			String q = "SELECT se.addr FROM Sedi se WHERE se.isActive = 1 AND "
					+ "se.personaGiuridica = :pg AND se.sedePrincipale = 1";

			Query qDate = ca.createQuery(q);
			qDate.setParameter("pg", pg);

			AD mainAddr = null;
			try {
				mainAddr = (AD) qDate.getSingleResult();
			} catch (NoResultException e) {
				//dateAcq = null;
			}

			return mainAddr;

		}*/

	@SuppressWarnings("unchecked")
	public String getArpavField(Object obj, String fieldName){
		String rtn = "";

		PersoneGiuridiche pg = null;
		if(obj instanceof PersoneGiuridiche) {
			pg = (PersoneGiuridiche) obj;

		}else if(obj instanceof Map){
			Long internalId = ((Long)((Map<String,Object>)obj).get("internalId"));
			pg = ca.get(PersoneGiuridiche.class, internalId);
		}

		if(pg != null && fieldName != null){
			Sedi sp = pg.getSedePrincipale();
			if(sp != null){
				if("codiceUnivoco".equals(fieldName)) {
					if(sp.getCodiceUnivoco() != null){
						rtn = sp.getCodiceUnivoco();
					}
				}else if("codContabilita".equals(fieldName)) {
					if(sp.getCodContabilita() != null){
						rtn = sp.getCodContabilita();
					}
				}else if("telecom.mc".equals(fieldName)) {
					if(sp.getTelecom() != null){
						if(sp.getTelecom().getMc() != null){
							rtn = sp.getTelecom().getMc();
						}
					}
				}else if("note".equals(fieldName)) {
					if(sp.getNote() != null){
						rtn = sp.getNote();
						if(sp.getNote().length() > 30){
							rtn = rtn.substring(0, 30) + "...";
						}
					}
				}

			}
		}

		return rtn;
	}

	/**
	 * Returns a list containing all documents of PersoneGiuridiche, impianto, verifica and addebito
	 * @param personeGiuridiche
	 * @return
	 */
	public List<AlfrescoDocument> getAllDocuments(PersoneGiuridiche personeGiuridiche, List<Impianto> impiantoList, List<Addebito> addebitoList) throws PhiException {
		List<AlfrescoDocument> results = new ArrayList<AlfrescoDocument>();

		results.addAll(personeGiuridiche.getDocumenti());

		VerificaImpAction verificaImpAction = VerificaImpAction.instance();

		for (Impianto impianto : impiantoList) {

			verificaImpAction.cleanRestrictions();

			if (impianto instanceof ImpMonta) {
				results.addAll(((ImpMonta)impianto).getDocumenti());
				verificaImpAction.equal.put("impMonta.internalId", impianto.getInternalId());
			} else if (impianto instanceof ImpPress) {
				results.addAll(((ImpPress)impianto).getDocumenti());
				verificaImpAction.equal.put("impPress.internalId", impianto.getInternalId());
			} else if (impianto instanceof ImpRisc) {
				results.addAll(((ImpRisc)impianto).getDocumenti());
				verificaImpAction.equal.put("impRisc.internalId", impianto.getInternalId());
			} else if (impianto instanceof ImpSoll) {
				results.addAll(((ImpSoll)impianto).getDocumenti());
				verificaImpAction.equal.put("impSoll.internalId", impianto.getInternalId());
			} else if (impianto instanceof ImpTerra) {
				results.addAll(((ImpTerra)impianto).getDocumenti());
				verificaImpAction.equal.put("impTerra.internalId", impianto.getInternalId());
			}

			List<VerificaImp> verificaList = verificaImpAction.select();

			if (verificaList != null) {
				for (VerificaImp verifica : verificaList) {
					results.addAll(verifica.getDocumenti());
				}
			}
		}

		if (addebitoList != null) {
			for (Addebito addebito : addebitoList) {
				results.addAll(addebito.getDocumenti());
			}
		}
		return results;
	}

	public boolean isNotInception(){
		TreeBean treeBean = TreeBean.instance();
		if(treeBean!=null && treeBean.getProcessList()!=null && !treeBean.getProcessList().isEmpty()){
			if(treeBean.getProcessList().size()>=3 && "MOD_Company/CORE/PROCESSES/companyManagement".equals(treeBean.getProcessPath().get(3))){
				int count = 0;
				for(String process : treeBean.getProcessPath()){
					if("MOD_Company/CORE/PROCESSES/companyManagement".equals(process))
						count++;
				}

				return count<2;
			}else {
				return false;
			}			
		}

		return true;
	}

	public Object searchCompanyOnParixPnc(String iva, String cf, String denominazione, Boolean dittaCessata){

		try{
			ParixHttpClient parix = ParixHttpClient.instance();
			List<EstremiImpresa> list = null;
			Object obj = null;

			if(iva!=null && !iva.isEmpty()){
				obj = parix.ricercaImpresePerPartitaiva(dittaCessata, iva);
				if(obj instanceof Riga){
					Riga riga = (Riga)obj;
					if(!riga.getCFISC1().isEmpty()) cf = riga.getCFISC1();
				}
			}

			if(cf!=null && !cf.isEmpty()){
				obj = parix.ricercaImpresePerCodiceFiscale(dittaCessata, cf);

			}else if (denominazione!=null && !denominazione.isEmpty()){
				obj = parix.ricercaImpresePerDenominazione(dittaCessata, denominazione);

			}

			if (obj != null) {
				if (obj instanceof List<?>){
					List<EstremiImpresa> estremiImpresaList = (List<EstremiImpresa>)obj;
					if(estremiImpresaList!=null && estremiImpresaList.size()==1){

						return estremiImpresaList.get(0);
					}
				} else if (obj instanceof Riga){

					Riga riga = (Riga)obj;
					if(riga.getID()!=null){
						return riga;
					} else {
						return null;
					}

				} else if (obj instanceof String){
					return null;
				}

			}
		} catch(Exception e) {
			return null;
		}
		return null;
	}

	public List<Localizzazione> searchBranchOnParixPnc(DatiImpresa datiImpresa, String cia, String rea) throws PhiException, NamingException {
		try {
			List<Localizzazione> locList = new ArrayList<Localizzazione>();
			//SEDE PRINCIPALE
			if(datiImpresa.getInformazioniSede()!=null){
				Localizzazione loc = new Localizzazione();
				loc.setNumeroTipo(new NumeroTipo());
				loc.getNumeroTipo().setCciaa(cia);
				try {
					loc.getNumeroTipo().setNrea(Long.parseLong(rea));
				} catch (NumberFormatException e) {
					//nothing to do
				}
				loc.setIndirizzo(datiImpresa.getInformazioniSede().getIndirizzo());

				loc.setInternalId(0L);//0=sede principale
				locList.add(loc);
			}

			if(datiImpresa.getLocalizzazioni()!=null && !datiImpresa.getLocalizzazioni().isEmpty()){
				for(Localizzazioni locs : datiImpresa.getLocalizzazioni()){
					if(locs.getLocalizzazione()!=null && !locs.getLocalizzazione().isEmpty()){
						locList.addAll(locs.getLocalizzazione());
					}
				}

				//set internalIds
				/*
				 * QUESTE ENTITA NON SONO BASEENTITY, ma devono esser gestite dalle datagrid come tali.
				 */
				long i = 0;
				for(Localizzazione loc : locList){
					loc.setInternalId(i); 
					i++;
				}
			}

			//sedi inserite manualmente
			if(datiImpresa!=null && datiImpresa.getEstremiImpresa()!=null && datiImpresa.getEstremiImpresa().getCodiceFiscale()!=null){
				GenericAdapterLocalInterface ga = GenericAdapter.instance();
				Map<String, Object> pars = new HashMap<String, Object>();
				pars.put("cf", datiImpresa.getEstremiImpresa().getCodiceFiscale());
				pars.put("nRea", datiImpresa.getEstremiImpresa().getDatiIscrizioneRea().get(0).getNrea().toString());
				pars.put("prov", datiImpresa.getEstremiImpresa().getDatiIscrizioneRea().get(0).getCciaa());
				List<PersoneGiuridiche> listaDitteDaChiudere = (List<PersoneGiuridiche>)ga.executeHQL(QUERY_DITTE_DA_CHIUDERE, pars);
				if(listaDitteDaChiudere!=null && !listaDitteDaChiudere.isEmpty()){
					PersoneGiuridiche lastDitta = listaDitteDaChiudere.get(0);
					if(lastDitta!=null){
						Map<String, Object> parSedi = new HashMap<String, Object>();
						parSedi.put("id", lastDitta.getInternalId());
						List<Sedi> listaSediLocali = (List<Sedi>)ga.executeHQL(QUERY_SEDI_LOCALI, parSedi);
						for(Sedi oldSede : listaSediLocali){
							Localizzazione newLoc = new Localizzazione();
							newLoc.setNumeroTipo(new NumeroTipo());
							newLoc.setIndirizzo(new Indirizzo());

							newLoc.setInternalId(oldSede.getInternalId());
							newLoc.getNumeroTipo().setCciaa(oldSede.getProvinciaCCIAA());
							try {
								newLoc.getNumeroTipo().setNrea(Long.parseLong(oldSede.getNumeroREA()));
							} catch (NumberFormatException e) {
								// nothing to do
							}

							if(oldSede.getAddr()!=null){
								newLoc.getIndirizzo().setComune(oldSede.getAddr().getCty());
								newLoc.getIndirizzo().setVia(oldSede.getAddr().getStr());
								newLoc.getIndirizzo().setNcivico(oldSede.getAddr().getBnr());
							}
							newLoc.setLocale(true);
							locList.add(newLoc);
						}
					}							
				}
			}

			return locList;	

		} catch(Exception e) {
			return null;
		}
	}

	public PersoneGiuridiche copiaDaParixPnc(DatiImpresa dati, Long selectedLoc) throws InstantiationException, IllegalAccessException, PhiException, NamingException{

		if(dati==null) {
			return null;
		}

		GenericAdapterLocalInterface ga = GenericAdapter.instance();

		PersoneGiuridiche newDitta = new PersoneGiuridiche();
		Vocabularies voc = VocabulariesImpl.instance();
		List<Sedi> sediDitta = new ArrayList<Sedi>();

		Long selectedSede = null;

		if(dati != null) {
			if(dati.getEstremiImpresa() != null) {

				// CERCO LE DITTE DA CHIUDERE
				String queryDitteDaChiudere = QUERY_DITTE_DA_CHIUDERE;

				Map<String, Object> pars = new HashMap<String, Object>();
				pars.put("cf", dati.getEstremiImpresa().getCodiceFiscale());
				pars.put("nRea", dati.getEstremiImpresa().getDatiIscrizioneRea().get(0).getNrea().toString());
				pars.put("prov", dati.getEstremiImpresa().getDatiIscrizioneRea().get(0).getCciaa());
				List<PersoneGiuridiche> listaDitteDaChiudere = (List<PersoneGiuridiche>)ga.executeHQL(queryDitteDaChiudere, pars);

				if(listaDitteDaChiudere != null && !listaDitteDaChiudere.isEmpty()) {
					// CHIUDO LE DITTE PRECEDENTI
					for(PersoneGiuridiche p : listaDitteDaChiudere){
						p.setIsActive(false);
						ga.updateObject(p);
					}
				}

				newDitta.setDenominazione(truncate(dati.getEstremiImpresa().getDenominazione(),255));
				newDitta.setCodiceFiscale(truncate(dati.getEstremiImpresa().getCodiceFiscale(),255));
				newDitta.setPatritaIva(truncate(dati.getEstremiImpresa().getPartitaIva(),255));
				if(dati.getEstremiImpresa().getDatiIscrizioneRi()!=null){
					newDitta.setNumeroRI(truncate(dati.getEstremiImpresa().getDatiIscrizioneRi().getNumeroRi(),255));
					newDitta.setDataIscrizioneRI(parseParixDate(dati.getEstremiImpresa().getDatiIscrizioneRi().getData()));
				}

				// H00200030
				newDitta.setDataCancellazioneRI(getCancellationDate(dati.getEstremiImpresa()));

				if(dati.getEstremiImpresa().getFormaGiuridica()!=null){
					String code = dati.getEstremiImpresa().getFormaGiuridica().getCformaGiuridica();
					String desc = dati.getEstremiImpresa().getFormaGiuridica().getDescrizione();

					CodeValue cvForma = getOrCreateCode("PHIDIC", "Company", "forma", code, desc, voc);
					if(cvForma != null){
						newDitta.setFormaGiuridica(cvForma);
					}
				}

				if(dati.getDurataSocieta()!=null){
					if(dati.getDurataSocieta().getDtCostituzione()!=null)
						newDitta.setDataCostituzione(parseParixDate(dati.getDurataSocieta().getDtCostituzione().toString()));

					if(dati.getDurataSocieta().getDtTermine()!=null)
						newDitta.setDataTermine(parseParixDate(dati.getDurataSocieta().getDtTermine().toString()));
				}

				if(dati.getInformazioniSede() != null) {
					Sedi sedePrincipale = new Sedi();
					if(dati.getInformazioniSede().getIndirizzo()!=null){
						sedePrincipale.setSedePrincipale(true);
						sedePrincipale.setDenominazioneUnitaLocale(truncate(dati.getEstremiImpresa().getDenominazione(),255));
						sedePrincipale.setProgressivoUnitaLocale(0);
						sedePrincipale.setElemento("0");
						AD addr = indirizzoToAddr(dati.getInformazioniSede().getIndirizzo(), voc);
						TEL telecom = new TEL();
						telecom.setMc(truncate(dati.getInformazioniSede().getIndirizzo().getIndirizzoPec(),255));
						if(dati.getEstremiImpresa().getDatiIscrizioneRea()!=null && !dati.getEstremiImpresa().getDatiIscrizioneRea().isEmpty()){
							List<DatiIscrizioneRea> datiRea = dati.getEstremiImpresa().getDatiIscrizioneRea();

							/*
							 * Se ho piu datiRea la ditta è PLURILOCALIZZATA (PIU SEDI SECONDARIE, OGNUNA CON PIU UNITA LOCALI)
							 * Per la sede principale devo cercare quella con CCIAA = provincia sede principale (forse poteva bastare guardare FLAG_SEDE=SI...non si sa)
							 */
							DatiIscrizioneRea reaPrinc = null;
							if(datiRea!=null && !datiRea.isEmpty()){
								String provinciaPrincipale = addr.getCpa();
								for(DatiIscrizioneRea rea : datiRea){
									if(rea.getCciaa().equals(provinciaPrincipale)){
										reaPrinc=rea;
										break;
									}
								}
							} else {
								reaPrinc=dati.getEstremiImpresa().getDatiIscrizioneRea().get(0);
							}

							if(reaPrinc!=null){
								sedePrincipale.setNumeroREA(truncate(reaPrinc.getNrea().toString(),255));
								sedePrincipale.setProvinciaCCIAA(truncate(reaPrinc.getCciaa(),255));
								if(reaPrinc.getCessazione()!=null){
									if(reaPrinc.getCessazione().getDtCessazione()!=null)
										sedePrincipale.setDataCessazione(parseParixDate(reaPrinc.getCessazione().getDtCessazione().toString()));

									sedePrincipale.setCausaleCessazione(reaPrinc.getCessazione().getCausale());
									if(reaPrinc.getCessazione().getDtDenunciaCess()!=null)
										sedePrincipale.setDataDenunciaCessazione(parseParixDate(reaPrinc.getCessazione().getDtDenunciaCess().toString()));
									if(reaPrinc.getCessazione().getDtCancellazione()!=null)
										sedePrincipale.setDataCancellazione(parseParixDate(reaPrinc.getCessazione().getDtCancellazione().toString()));
								}
							}
						}

						//SALVO LA SEDE PRINCIPALE
						sedePrincipale.setAddr(addr);
						sedePrincipale.setTelecom(telecom);
					}

					//SALVO LA DITTA CORRENTE
					if(newDitta!=null){
						newDitta = (PersoneGiuridiche) ga.updateObject(newDitta);
						sedePrincipale.setPersonaGiuridica(newDitta);
						sedePrincipale = (Sedi) ga.updateObject(sedePrincipale);
						sediDitta.add(sedePrincipale);
					}

					selectedSede = localizzazioniToSedi(dati, newDitta, sediDitta, selectedLoc, selectedSede, voc, ga);

					//cerco le sedi inserite manualmente e le copio (solo dalla ultima versione della ditta)
					if(listaDitteDaChiudere!=null && !listaDitteDaChiudere.isEmpty()){
						PersoneGiuridiche lastDitta = listaDitteDaChiudere.get(0);
						selectedSede = manageSediLocali(lastDitta, newDitta, sediDitta, selectedLoc, selectedSede, ga);
					}

					if(selectedSede==null){
						selectedSede = sedePrincipale.getInternalId();
					}

					newDitta.setSedi(sediDitta);

					if(dati.getInformazioniSede().getCodiceAtecoUl()!=null){
						attivitaSede(sediDitta.get(0), dati.getInformazioniSede().getCodiceAtecoUl().getAttivitaIstat(), voc, ga);
					}

					if(dati.getInformazioniSede().getRuoliLoc()!=null){
						ruoliSede(sediDitta.get(0), dati.getInformazioniSede().getRuoliLoc().getRuoloLoc(), voc, ga);
					}

				} //informazioniSede
			} //estremi impresa

			if(dati.getPersoneSede()!=null && dati.getPersoneSede().getPersona()!=null && !dati.getPersoneSede().getPersona().isEmpty()){

				SediPersoneAction spAction = SediPersoneAction.instance();
				CaricheAction carAction = CaricheAction.instance();

				for(Persona p : dati.getPersoneSede().getPersona()) {
					SediPersone sp = personaToSediPersona(p, voc);
					spAction.create(sp);
					//sp = spAction.getEntity();
					List<Cariche> spCariche = new ArrayList<Cariche>();
					if(p.getCariche()!=null && p.getCariche().getCarica()!=null){
						for(Carica c : p.getCariche().getCarica()){
							Cariche car = caricaToCariche(c, voc);
							car.setSediPersone(sp);
							if(p.getIdentificativo()!=null && p.getIdentificativo().getProgressivoLoc()!=null){
								Integer progressivo = Integer.parseInt(p.getIdentificativo().getProgressivoLoc());
								String cciaa = p.getIdentificativo().getPressoCciaa();
								String nrea = p.getIdentificativo().getPressoNRea();

								Sedi sedeSecondaria = findSedeSecondaria(cciaa, nrea, progressivo, sediDitta);
								car.setSede(sedeSecondaria);
							}
							//carAction.inject(car);
							carAction.create(car);
							spCariche.add(car);
						}
					}
					sp.setCariche(spCariche);
				}
			} //personeSede

		}  //dati != null

		ga.updateObject(newDitta);

		if(newDitta!=null && newDitta.getInternalId()==0){
			newDitta = (PersoneGiuridiche) ga.create(newDitta);
		}

		return newDitta;
	}
	
	public void filterCodiceDitta(String codiceDitta){
		if(codiceDitta==null || codiceDitta.isEmpty()){
			this.getLike().remove("codiceDitta");
			return;
		}
		
		this.getLike().put("codiceDitta", codiceDitta);
	}
	
	public void refreshDocuments(PersoneGiuridiche pg, List<AlfrescoDocument> docList){
		
		if(docList!=null && !docList.isEmpty()){
			pg.setDocumenti(new ArrayList<AlfrescoDocument>());
			for(AlfrescoDocument doc : docList){
				if(doc.getPath().contains("PersoneGiuridiche/IntId"+pg.getInternalId())){
					pg.addDocumenti(doc);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean isDeletableArpav(PersoneGiuridiche pg){
		
		if(pg == null)
			pg = getEntity();
		
		if(pg == null)
			return false;
		
		boolean noInstall = false;
		SediInstallazioneAction siAction = SediInstallazioneAction.instance();
		List<SediInstallazione> siFullLst = siAction.getFullListFromPG(pg);
		if(siFullLst == null || siFullLst.isEmpty())
			noInstall = true;
		
		boolean noAdd = false;
		List<Addebito> addList = null;
		
		String qryAddebiti = "SELECT a FROM Addebito a WHERE a.personeGiuridiche.internalId =:pgId";
		Query qAddebiti = ca.createQuery(qryAddebiti);
		qAddebiti.setParameter("pgId", pg.getInternalId());

		try {
			addList = (List<Addebito>) qAddebiti.getResultList();
		} catch (NoResultException e) {

		}
		if(addList == null || addList.isEmpty())
			noAdd = true;
		
		boolean noSae = false;
		List<Sedi> saEsterne = pg.getSaEsterne();
		if(saEsterne == null || saEsterne.isEmpty())
			noSae = true;
		
		return noInstall && noAdd && noSae;
	}

}