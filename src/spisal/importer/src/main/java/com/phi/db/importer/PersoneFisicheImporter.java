package com.phi.db.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.impl.SessionImpl;

import com.phi.entities.baseEntity.SituazioneProfessionale;
import com.phi.entities.baseEntity.SpisalAddr;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Person;
import com.prevnet.entities.Anagrafiche;
import com.prevnet.entities.Comuni;
import com.prevnet.entities.Interni;
import com.prevnet.entities.SoImpattoAnagrafica;
import com.prevnet.entities.Utenti;
import com.prevnet.mappings.MapPersoneFisiche;

@SuppressWarnings({"unchecked"})
public class PersoneFisicheImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(PersoneFisicheImporter.class.getName());
	private Query queryPerson;
	private Query queryAur;
	private SimpleDateFormat aurDateFormat;
	private boolean avoidDeadlocks = false;
	
	private static PersoneFisicheImporter instance = null;
	
	public static PersoneFisicheImporter getInstance() {
		if(instance == null) {
			instance = new PersoneFisicheImporter();
		}
		return instance;
	}

	public void setAvoidDeadlocks (boolean avoidDeadlocks) {
		this.avoidDeadlocks = avoidDeadlocks;
	}
	
	public PersoneFisicheImporter() {
		super();

		queryPerson = statelessTargetEm.createQuery("SELECT p FROM Person p WHERE "
				+ "p.name.giv = :nome AND "
				+ "p.name.fam = :cognome AND "
				+ "(:dataNascita IS NULL OR p.birthTime = :dataNascita) AND "
				+ "(:cf IS NULL OR p.fiscalCode = :cf) AND "
				+ "(p.birthPlace.code.id = concat('comuni.',:istatNascita,'_V0') OR p.birthPlace.code.id IS NULL) AND "
				+ "p.isActive = :isActive "
				+ "ORDER BY p.creationDate DESC ");

		queryAur = sourceEm.createQuery("SELECT s FROM SoImpattoAnagrafica s WHERE "
				+ "trim(s.cognome) = :cognome AND "
				+ "trim(lower(s.nome)) like :nome AND "
				+ "trim(s.datanascita) = :dataNascita AND "
				+ "trim(s.sesso) = :sesso AND "
				+ "(trim(s.codicefiscale) = :cf OR s.codicefiscale IS NULL) ");/*AND "
				+ "(trim(s.tesserasanitaria) = :tessera OR s.tesserasanitaria IS NULL) ");*/

		aurDateFormat = new SimpleDateFormat("yyyyMMdd");
	}
	
	/**
	 * Data l'anagrafica in source ritorna la sede corrispondente in target, ma importa TUTTA la ditta (PersoneGiuridiche e Sedi)
	 * @param source
	 * @return
	 */
	public Person importPerson(Anagrafiche source){
		//controllo che sia un utente
		if(source!=null && source.getTipo()!=null && source.getTipo().intValue()==4){
			Query qUtente = sourceEm.createQuery("SELECT u FROM Utenti u WHERE u.idanagrafica = :id");
			qUtente.setParameter("id", source.getIdanagrafiche());
			List<Utenti> list = qUtente.getResultList();
			if(list!=null && !list.isEmpty()){
				return importPerson(list.get(0));
			}
		}
			
		return null;
	}
	
	public void updateFromAur(Utenti source){
		Person person = checkAnagrafica(source);
		if(person!=null){
			manageExistingPerson(source, person);
		}
	}

	/**
	 * Ritorna l'oggetto importato di modo che possa essere usato per aggancio a pratiche
	 * @param source
	 * @return
	 */
	public Person importPerson(Utenti source){
		Person imported = null;
		if(!checkMapping(source.getIdanagrafica())){ // se non è mappata...
			Person person = checkAnagrafica(source); // ...cerco una Person con isActive=true e con gli stessi dati

			if(person == null){

				// 3) il soggetto non esiste: va inserito così com'è....
				imported = importNewPerson(source, new Person(), null); 

				if(disableAur) {
					//nothing to do.
				}else {
					//.... ma va poi cercato in AUR e, se trovato, va reinserito con i dati aggiornati => invoco il caso 2)
					imported = manageExistingPerson(source, imported);
				}
			}else{ // se ho trovato una Person con isActive=true e con gli stessi dati

				
				if(disableAur) {
					imported = person;
				}else {
					/**
					 * 1) Il soggetto esiste ed è agganciato ad AUR
					 * 2) Il soggetto esiste, ma non è agganciato ad AUR
					 */
					imported = manageExistingPerson(source, person);
				}
			}
			if(imported.getInternalId()==0) {
				if(avoidDeadlocks) {
					imported = (Person)saveOnStatelessTarget(imported);
					//imported = targetEm.find(Person.class, imported.getInternalId());
				} else {
					saveOnTarget(imported);
				}
			}
			
			saveMapping(source, imported);

			/**
			 * SE l'utente è già stato mappato, allora ritorna il corrispondente
			 */
		}else{			
			return getMapped(source.getIdanagrafica());
		}

		return imported;
	}

	/**
	 * 3) il soggetto non esiste
	 * @param source
	 * @param target
	 */
	private Person importNewPerson(Utenti source, Person target, String mpi){
		target.setCreatedBy(this.getClass().getSimpleName()+ulss);
		target.setCreationDate(source.getTimestampinsmod()==null?new Date():source.getTimestampinsmod());

		if(mpi!=null){
			target.setMpi(mpi);
			target.setIsActive(false);
		}

		target.setFiscalCode(source.getCf());
		target.setCs(source.getNumerossn());
		if(source.getPartitaiva()!=null && !source.getPartitaiva().isEmpty()){
			if(target.getProfessionalSituation()==null){
				SituazioneProfessionale situazioneProfessionale = new SituazioneProfessionale();
				situazioneProfessionale.setCreatedBy(this.getClass().getSimpleName()+ulss);
				situazioneProfessionale.setCreationDate(source.getTimestampinsmod()==null?new Date():source.getTimestampinsmod());
				
				target.setProfessionalSituation(situazioneProfessionale);
			}
			target.getProfessionalSituation().setNote("Partita IVA: "+source.getPartitaiva());
		}

		if("1".equals(source.getDeceduto())){
			target.setDeathIndicator(true);
			target.setDeathDate(source.getDatamorte());
		}

		if(target.getName()==null)
			target.setName(new EN());

		target.getName().setFam(source.getCognome());
		target.getName().setGiv(source.getNome());
		target.setGenderCode(getCodeValue("phidic.generic.gender."+source.getSesso()));

		if(target.getTelecom()==null)
			target.setTelecom(new TEL());

		if(source.getTelefono()!=null)
			target.getTelecom().setHp(source.getTelefono().trim());

		if(source.getTelefonodomicilio()!=null)
			target.getTelecom().setMc(source.getTelefonodomicilio().trim());

		target.getTelecom().setMail(source.getEmail());

		if(target.getAddr()==null)
			target.setAddr(new AD());

		AD residenza = target.getAddr();

		if(source.getLocalitaRes()!=null)
			residenza.setAdl(source.getLocalitaRes().getDescrlocalita());

		if(source.getAnagrafiche()!=null){
			residenza.setStr(source.getAnagrafiche().getIndirizzo());
			residenza.setCode(getComune(source.getAnagrafiche().getComuni()));
			if(residenza.getCode()!=null){
				residenza.setCpa(residenza.getCode().getProvince());
				residenza.setZip(residenza.getCode().getZip());
				residenza.setCty(residenza.getCode().getCurrentTranslation());
			}else if(source.getAnagrafiche().getComuni()!=null){
				Comuni com = source.getAnagrafiche().getComuni();
				if(com.getProvincia()!=null)
					residenza.setCpa(com.getProvincia().getDescrizione());

				residenza.setZip(com.getCapdelcomune());
				residenza.setCty(com.getDescrizione());
			}

			if(target.getAlternativeAddr()==null)
				target.setAlternativeAddr(new SpisalAddr());

			if(target.getAlternativeAddr().getTelecom()==null)
				target.getAlternativeAddr().setTelecom(new TEL());

			if(source.getTelcellulare()!=null)
				target.getAlternativeAddr().getTelecom().setMc(source.getTelcellulare().trim());

			if(source.getAnagrafiche().getInterni()!=null){
				Interni interni = source.getAnagrafiche().getInterni();
				if(interni.getTelefono()!=null)
					target.getAlternativeAddr().getTelecom().setHp(interni.getTelefono().trim());
				String note = "";
				if(interni.getFax()!=null){
					note+="Fax: "+interni.getFax().trim()+"\r\n";	
				}
				if(interni.getEmail()!=null){
					note+="E-mail: "+interni.getEmail()+"\r\n";	
				}
				target.getAlternativeAddr().getTelecom().setH(note);
			}
		}
		
		target.setCountryOfAddr((CodeValueCountry)getMappedCode(source.getEsteroResidenza(), ulss));

		target.setBirthTime(source.getDatanascita());
		if(target.getBirthPlace()==null)
			target.setBirthPlace(new AD());

		manageAddrComune(target.getBirthPlace(), source.getComuneNascita());		
		
		target.setCountryOfBirth((CodeValueCountry)getMappedCode(source.getEsteroNascita(), ulss));

		if(target.getDomicileAddr()==null)
			target.setDomicileAddr(new AD());

		manageAddrComune(target.getDomicileAddr(), source.getComuneDomicilio());

		if(source.getLocalitaDom()!=null)
			target.getDomicileAddr().setAdl(source.getLocalitaDom().getDescrlocalita());

		target.getDomicileAddr().setStr(source.getIndirizzodomicilio());

		target.setCountryOfDomicile((CodeValueCountry)getMappedCode(source.getEsteroDomicilio(), ulss));

		if(source.getAsl()!=null && source.getAsl().getUtcodice()!=null){
			String utcodice = source.getAsl().getUtcodice();
			if(utcodice.startsWith("P")){
				utcodice = utcodice.substring(1);

			}else if(utcodice.length()==5){
				utcodice = "0"+utcodice;

			}else if(utcodice.length()==2){
				utcodice = "0501"+utcodice;

			}
			target.setCurrentOrg(findOrganization(utcodice));
		}

		return target;
	}

	/**
	 * 1) Il soggetto esiste ed è agganciato ad AUR
	 * 2) Il soggetto esiste, ma non è agganciato ad AUR
	 * ATTENZIONE: target ha SEMPRE isActive = true perchè o è ritornato da checkAnagrafica o è una new Person
	 * @param source
	 * @param target
	 */
	private Person manageExistingPerson(Utenti source, Person target){	
		/**
		 * 1) Il soggetto esiste ed è agganciato ad AUR : va inserito un record già storicizzato con i dati del db spisal, 
		 * 		agganciato alla stessa anagrafica AUR => copio MPI 
		 */
		if(target.getMpi()!=null){
			Person versioneStoricizzata = importNewPerson(source, new Person(), target.getMpi());
			versioneStoricizzata.setToUpdate(compareForUpdate(versioneStoricizzata, target));
			return versioneStoricizzata; //versioneStoricizzata ha isActive = false


			/**
			 * 2a) Il soggetto esiste ma non è agganciato ad AUR, ed è presente in AUR
			 * 		=> la versione di AUR diventa quella attiva più recente, 
			 * 			mentre quella oggetto dell'import e tutte le altre storiche eventualmente presenti vengono agganciate ad AUR (mpi)
			 */
		}else {
			Person aur = getFromAUR(source);
			if(aur!=null && aur.getMpi()!=null){
				
				aur.setIsActive(true);
				target.setToUpdate(compareForUpdate(target, aur));
				target = setMpiOnHistoricVersions(target, aur.getMpi()); //...e setta target a false
				//E SALVO, COME UNICA VERSIONE ATTIVA, LA VERSIONE PROVENIENTE DA AUR
				saveOnTarget(aur);
				return target; //versioneStoricizzata ha isActive = false

				/**
				 * 2b) Il soggetto esiste ma non è agganciato ad AUR, e non è presente in AUR
				 * 		=> la versione attiva sarà quella con creationDate maggiore 
				 * 		(creationDate viene riempita con TIMESTAMPINSMOD che garantisce coerenza) 
				 */					
			}else{
				
				target.setToUpdate(true);//concordato con Martina il 17/1 al tel
				target = setIsActiveOnHistoricVersions(target);//salva anche target
				return target; //versioneStoricizzata ha isActive = falseimportNewPerson(source, new Person(), null); 			
			}
		}
	}

	/**
	 * Cerca su AUR una persona con medesimo nome, cognome, cf, tessera sanitaria, sesso, data di nascita
	 * @param source
	 * @return
	 */
	private Person getFromAUR(Utenti source){
		if(source!=null){
			queryAur.setParameter("cognome", source.getCognome());//match esatto
			queryAur.setParameter("nome", (source.getNome()+"%").toLowerCase());//match in like
			if(source.getDatanascita()!=null)
				queryAur.setParameter("dataNascita", aurDateFormat.format(source.getDatanascita()));//Data espressa nel formato yyyymmdd
			else
				queryAur.setParameter("dataNascita", "");

			queryAur.setParameter("sesso", source.getSesso());
			queryAur.setParameter("cf", source.getCf());
			//queryAur.setParameter("tessera", source.getNumerossn());

			List<SoImpattoAnagrafica> list = queryAur.getResultList();
			if(list!=null && !list.isEmpty()){
				SoImpattoAnagrafica s = list.get(0);

				Person p = new Person();
				p.setCreatedBy("PersoneFisicheImporter_AUR");
				if(s.getDatavaliditarecord()!=null){
					try {
						p.setCreationDate(aurDateFormat.parse(s.getDatavaliditarecord()));
					} catch (ParseException e) {
						thislog.error("Unable to parse AUR dataValiditaRecord: "+s.getDatanascita());
					}
				}

				//IDENTIFICATIVI
				p.setMpi(s.getIdregionale().trim());
				if(p.getName()==null)
					p.setName(new EN());

				p.getName().setFam(s.getCognome());
				p.getName().setGiv(s.getNome());

				p.setGenderCode(getCodeValue("phidic.generic.gender."+s.getSesso()));
				p.setFiscalCode(s.getCodicefiscale());
				p.setCs(s.getTesserasanitaria());
				p.setStp(s.getCodicestp());
				p.setEni(s.getCodiceeni());
				p.setTeamCode(s.getTesserateam());

				//GENERALITA DI NASCITA ED EVENTUALE DECESSO
				if(s.getDatanascita()!=null){
					try {
						p.setBirthTime(aurDateFormat.parse(s.getDatanascita()));
					} catch (ParseException e) {
						thislog.error("Unable to parse AUR dataNascita: "+s.getDatanascita());
					}
				}
				if(p.getBirthPlace()==null)
					p.setBirthPlace(new AD());

				p.getBirthPlace().setCode(getComune(s.getComunenascita()));
				p.setCitizen(getCountry("stati."+s.getCittadinanza()));

				if(s.getDatadecesso()!=null){
					p.setDeathIndicator(true);
					try {
						p.setDeathDate(aurDateFormat.parse(s.getDatadecesso()));
					} catch (ParseException e) {
						thislog.error("Unable to parse AUR dataDecesso: "+s.getDatanascita());
					}

				}

				//RESIDENZA
				if(p.getAddr()==null)
					p.setAddr(new AD());

				AD addr = p.getAddr();
				addr.setCode(getComune(s.getComuneresidenza()));
				addr.setStr(s.getIndirizzoresidenza());
				addr.setBnr(s.getNumerocivicoresidenza());
				addr.setZip(s.getCapresidenza());//nel caso di grandi città può esser diverso da quello del comune

				//DOMICILIO
				if(p.getDomicileAddr()==null)
					p.setDomicileAddr(new AD());

				AD dom = p.getAddr();
				dom.setCode(getComune(s.getComunedomicilio()));
				dom.setStr(s.getIndirizzodomicilio());
				dom.setBnr(s.getNumerocivicodomicilio());
				dom.setZip(s.getCapdomicilio());//nel caso di grandi città può esser diverso da quello del comune

				//CONTATTI
				if(p.getTelecom()==null)
					p.setTelecom(new TEL());

				TEL tel = p.getTelecom();
				tel.setHp(s.getTelefono1());
				tel.setMc(s.getTelefono2());
				tel.setEc(s.getTelefono3());

				tel.setH(s.getDescrizionetelefono1());
				tel.setBad(s.getDescrizionetelefono2());
				tel.setTmp(s.getDescrizionetelefono3());
				tel.setMail(s.getIndirizzoemail());

				//ASL DI ASSISTENZA
				p.setCurrentOrg(findOrganization(s.getUlssassistenza()));

				((SessionImpl)sourceEm.getDelegate()).evict(s);

				return p;
			}
		}
		return null;
	}

	/**
	 * Data una Person di esempio,
	 * cerco tutte le altre Person che rappresentano la stessa Person di esempio (stesso nome,cognome,cf,data e luogo di nascita) 
	 * ma storiche (isActive = 0): assieme a example formano lo storico della Person e solo la più recente avrà isActive = true
	 * Questo metodo viene invocato solo nel caso in cui l'anagrafica non ha un corrispondente AUR e a valle di un checkAnagrafica che ha ritornato null,
	 * quindi è corretto cercare solo anagrafiche con isActive = false (non può esisterne una con isActive=true). SOLO example ha isActive=true
	 * @param example
	 */
	private Person setIsActiveOnHistoricVersions(Person example){
		if(example==null || example.getName()==null)
			return example;

		String code = null;
		if(example.getBirthPlace()!=null && example.getBirthPlace().getCode()!=null){
			code = example.getBirthPlace().getCode().getCode();
		}

		queryPerson.setParameter("nome", example.getName().getGiv());
		queryPerson.setParameter("cognome", example.getName().getFam());
		queryPerson.setParameter("dataNascita", example.getBirthTime());
		queryPerson.setParameter("istatNascita", code);
		queryPerson.setParameter("cf", example.getFiscalCode());
		queryPerson.setParameter("isActive", false);

		List<Person> list = queryPerson.getResultList();

		if(list!=null && !list.isEmpty()){
			Person last = list.get(0);
			if(last.getCreationDate().after(example.getCreationDate())){
				last.setIsActive(true);
				example.setIsActive(false);

				if(commit){
					try { 
						if(!statelessTargetEm.getTransaction().isActive()) 
							statelessTargetEm.getTransaction().begin(); 
							
						Person lastMerged  = statelessTargetEm.merge(last);
						statelessTargetEm.flush();
						statelessTargetEm.getTransaction().commit();
						((SessionImpl)targetEm.getDelegate()).evict(last);
						last = targetEm.find(Person.class, lastMerged.getInternalId());
					} catch (Exception e) {
						log.error("Error persisting new Person", e);
						statelessTargetEm.getTransaction().rollback();
					}
				}
			}
		}
		
		return example;
	}

	/**
	 * Data una Person di esempio,
	 * cerco tutte le altre Person che rappresentano la stessa Person di esempio (stesso nome,cognome,cf,data e luogo di nascita) 
	 * ma storiche (isActive = 0) e imposto l'MPI 
	 * @param example
	 */
	private Person setMpiOnHistoricVersions(Person example, String mpi){
		if(example==null || example.getName()==null || mpi==null)
			return example;

		String code = null;
		if(example.getBirthPlace()!=null && example.getBirthPlace().getCode()!=null){
			code = example.getBirthPlace().getCode().getCode();
		}

		queryPerson.setParameter("nome", example.getName().getGiv());
		queryPerson.setParameter("cognome", example.getName().getFam());
		queryPerson.setParameter("dataNascita", example.getBirthTime());
		queryPerson.setParameter("istatNascita", code);
		queryPerson.setParameter("cf", example.getFiscalCode());
		queryPerson.setParameter("isActive", false);

		List<Person> list = queryPerson.getResultList();
		if(commit){
			if(!statelessTargetEm.getTransaction().isActive()) 
				statelessTargetEm.getTransaction().begin(); 
		}
		if(list!=null && !list.isEmpty()){

			for(Person p : list){
				p.setMpi(mpi);
				if(commit){
					statelessTargetEm.merge(p);
				}
			}
		}
		example.setMpi(mpi);
		example.setIsActive(false);
		/* NON C'E' BISOGNO DI SALVARLO SUBITO
		  if(commit){
			try { 
				Person exampleMerged  = statelessTargetEm.merge(example);
				statelessTargetEm.flush();
				statelessTargetEm.getTransaction().commit();
				((SessionImpl)targetEm.getDelegate()).evict(example);
				example = targetEm.find(Person.class, exampleMerged.getInternalId());
			} catch (Exception e) {
				log.error("Error persisting new Person", e);
				statelessTargetEm.getTransaction().rollback();
			}
		}*/
		return example;
	}

	/**
	 * Controlla se l'entità id sia già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapPersoneFisiche m = sourceEm.find(MapPersoneFisiche.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapPersoneFisiche m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapPersoneFisiche> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapPersoneFisiche map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
					"Target id: "+map.getIdphi()+". "+
					"Imported by "+map.getCopiedBy()+" "+
					"on date "+map.getCopyDate());

			return true;
		}
		return false;
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di input
	 * @param id
	 * @return
	 */
	private Person getMapped(long id){
		MapPersoneFisiche map = sourceEm.find(MapPersoneFisiche.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapPersoneFisiche m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapPersoneFisiche> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Person c = targetEm.find(Person.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qPerson = targetEm.createQuery("SELECT p FROM Person p WHERE p.internalId = :id");
			qPerson.setParameter("id", map.getIdphi());
			List<Person> lp = qPerson.getResultList();
			if(lp!=null && !lp.isEmpty()){
				return lp.get(0);
			}
		}
		
		return null;
	}

	/**
	 * Controlla se l'entità sia già stata inserita in Anagrafica locale, sulla base della corrispondenza di nome, cognome, data di nascita,
	 * istat nascita, cf e che isActive = true. Se sì ritorna la riga corrispondente.
	 * @param source
	 * @return
	 */
	private Person checkAnagrafica(Utenti source){
		if(source==null || source.getNome()==null || source.getNome().trim().isEmpty()
				|| source.getCognome()==null || source.getCognome().trim().isEmpty()
				|| (source.getDatanascita()==null && (source.getCf()==null || source.getCf().trim().isEmpty())))
			return null;

		String code = null;
		if(source.getComuneNascita()!=null){
			code = source.getComuneNascita().getCodiceistat();
		}

		queryPerson.setParameter("nome", source.getNome());
		queryPerson.setParameter("cognome", source.getCognome());
		queryPerson.setParameter("dataNascita", source.getDatanascita());
		queryPerson.setParameter("istatNascita", code);
		queryPerson.setParameter("cf", (source.getCf()==null || source.getCf().trim().isEmpty())?null:source.getCf());
		queryPerson.setParameter("isActive", true);

		List<Person> list = queryPerson.getResultList();
		if(list!=null && !list.isEmpty()){
			return targetEm.find(Person.class, list.get(0).getInternalId());
		}

		return null;
	}

	private void saveMapping(Utenti source, Person target){
		MapPersoneFisiche map = new MapPersoneFisiche();
		map.setIdprevnet(source.getIdanagrafica());
		map.setIdphi(target.getInternalId());
		map.setMpi(target.getMpi());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());
		map.setUlss(ulss);

		saveOnSource(map);
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}

	/**
	 * Controlla i campi significativi per impostare il flag to_update = true - I0050679
	 * @param oldPers
	 * @param newPers
	 * @return
	 */
	private boolean compareForUpdate(Person oldPers, Person newPers) {
		
		if(oldPers!=null && newPers!=null) {
			//per name usiamo sempre e solo fam e giv, quindi possiamo usare l'equals di EN
			if(oldPers.getName()==null && newPers.getName()!=null) {
				return true;
			}
			if(oldPers.getName()!=null && !oldPers.getName().equals(newPers.getName())) {
				return true;
			}
			
			if(oldPers.getBirthTime()==null && newPers.getBirthTime()!=null) {
				return true;
			}
			if(oldPers.getBirthTime()!=null && !oldPers.getBirthTime().equals(newPers.getBirthTime())) {
				return true;
			}
			
			if(oldPers.getBirthPlace()==null && newPers.getBirthPlace()!=null) {
				return true;
			
			} else if(oldPers.getBirthPlace()!=null && newPers.getBirthPlace()!=null) {
				if(oldPers.getBirthPlace().getCode()==null && newPers.getBirthPlace().getCode()!=null) {
					return true;
				}
				if(oldPers.getBirthPlace().getCode()!=null && !oldPers.getBirthPlace().getCode().equals(newPers.getBirthPlace().getCode())) {
					return true;
				}
			} else if(oldPers.getBirthPlace()!=null && newPers.getBirthPlace()==null) {
				return true;
			}
			
			if(oldPers.getGenderCode()==null && newPers.getGenderCode()!=null) {
				return true;
			}
			if(oldPers.getGenderCode()!=null && !oldPers.getGenderCode().equals(newPers.getGenderCode())) {
				return true;
			}
			
			if(oldPers.getFiscalCode()==null && newPers.getFiscalCode()!=null) {
				return true;
			}
			if(oldPers.getFiscalCode()!=null && !oldPers.getFiscalCode().equals(newPers.getFiscalCode())) {
				return true;
			}
			
			//per addr usiamo sempre e solo str, bnr, zip, cpa, cty quindi possiamo usare l'equals di AD
			if(oldPers.getAddr()==null && newPers.getAddr()!=null) {
				return true;
			}
			if(oldPers.getAddr()!=null && newPers.getAddr()!=null) {
				if(oldPers.getAddr().getCode()==null && newPers.getAddr().getCode()!=null) {
					return true;
				}
				if(oldPers.getAddr().getCode()!=null && !oldPers.getAddr().getCode().equals(newPers.getAddr().getCode())) {
					return true;
				}
			}
			if(oldPers.getAddr()!=null && !oldPers.getAddr().equals(newPers.getAddr())) {
				return true;
			}
			
			if(oldPers.getPhysician()==null && newPers.getPhysician()!=null) {
				return true;
			}
			if(oldPers.getPhysician()!=null && newPers.getPhysician()==null) {
				return true;
			}
			if(oldPers.getPhysician()!=null && newPers.getPhysician()!=null 
					&& oldPers.getPhysician().getInternalId()!=newPers.getPhysician().getInternalId()) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void deleteImportedData(String ulss) {
		String hqlPersoneFisiche = "SELECT mf.idphi FROM MapPersoneFisiche mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlPersoneFisiche+="WHERE mf.ulss = :ulss";

		Query qPersoneFisiche = sourceEm.createQuery(hqlPersoneFisiche);
		if(ulss!=null && !ulss.isEmpty())
			qPersoneFisiche.setParameter("ulss", ulss);

		List<Long> allIdPersons = qPersoneFisiche.getResultList();
		List<Long> idPersons = new ArrayList<Long>();
		while(allIdPersons!=null && !allIdPersons.isEmpty()){
			if(allIdPersons.size()>1000){
				idPersons.clear();
				idPersons.addAll(allIdPersons.subList(0, 1000));
				allIdPersons.removeAll(idPersons);
			}else{
				idPersons.clear();
				idPersons.addAll(allIdPersons);
				allIdPersons.removeAll(idPersons);
			}
			if(commit){
				String deletePersons = "DELETE FROM Person e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelPersons = targetEm.createQuery(deletePersons);
				qDelPersons.setParameter("ids", idPersons);
				qDelPersons.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}

		if(commit){
			String update = "DELETE FROM MAPPING_PERSONEFISICHE WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}

}
