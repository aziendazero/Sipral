package com.phi.db.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.impl.SessionImpl;

import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.prevnet.entities.Medici;
import com.prevnet.entities.SoImpattoAnagrafica;
import com.prevnet.entities.SoImpattoMedici;
import com.prevnet.mappings.MapAttivita;
import com.prevnet.mappings.MapMalprof;
import com.prevnet.mappings.MapMedici;

@SuppressWarnings({"unchecked"})
public class MediciImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(MediciImporter.class.getName());
	private SimpleDateFormat aurDateFormat;
	private Query queryAur;
	private Query queryPerson;
	
	private static MediciImporter instance = null;
	
	public static MediciImporter getInstance() {
		if(instance == null) {
			instance = new MediciImporter();
		}
		return instance;
	}
	
	public MediciImporter() {
		super();
		aurDateFormat = new SimpleDateFormat("yyyyMMdd");
		queryAur = sourceEm.createQuery("SELECT s FROM SoImpattoMedici s WHERE trim(s.idmedico) = :mpi ");
		queryPerson = targetEm.createQuery("SELECT p FROM Person p LEFT JOIN p.birthPlace.code bp WHERE "
				+ "p.name.giv = :nome AND "
				+ "p.name.fam = :cognome AND "
				+ "(p.birthTime = :dataNascita OR p.birthTime IS NULL) AND "
				+ "(bp.code = :istatNascita OR bp.code IS NULL) AND "
				+ "(p.fiscalCode = :cf OR p.fiscalCode IS NULL) AND "
				+ "p.isActive = :isActive "
				+ "ORDER BY p.creationDate DESC ");
		
		
	}
	
	public static void main(String[] args) {
		MediciImporter importer = new MediciImporter();
		Organization org = importer.findOrganization(ulss);
		
		importer.deleteImportedData(ulss);
		List<Medici> mediciSpisal = importer.readMediciSpisal();
		if(mediciSpisal!=null){
			for(Medici medicoSpisal : mediciSpisal){
				importer.importMedicoSpisal(medicoSpisal, org);
			}
		}
		
		List<SoImpattoMedici> mediciAur = importer.readMediciAur();
		if(mediciAur!=null){
			sourceEm.getTransaction().begin();
			
			
			for(SoImpattoMedici medicoAur : mediciAur){
				importer.importMedicoAur(medicoAur);
				//per il trim....
				((SessionImpl)sourceEm.getDelegate()).evict(medicoAur);
			}
			
			sourceEm.flush();
			sourceEm.getTransaction().commit();
		}
		importer.closeResource();
	}
	
	public Physician importMedicoSpisal(Medici medico, Organization org){
		if(!checkMapping(medico.getIdmedici())){
			Physician phys = new Physician();
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(0);
			
			phys.setCreatedBy(this.getClass().getSimpleName()+ulss);
			phys.setCreationDate(cal.getTime());
			phys.setCode(getCodeValue("phidic.generic.physicianroles.oth"));
			
			if(phys.getName()==null)
				phys.setName(new EN());
			
			phys.getName().setFam(medico.getCognome());
			phys.getName().setGiv(medico.getNome());

			phys.setType((CodeValuePhi)getMappedCode(medico.getTipologia(), ulss));
			
			if(phys.getValidity()==null)
				phys.setValidity(new IVL<Date>());
			
			phys.getValidity().setLow(cal.getTime());
			phys.setOrganization(org);
			
			if(phys.getAddr()==null)
				phys.setAddr(new AD());
			
			AD addr=phys.getAddr();
			manageAddrComune(addr, medico.getComune());
			addr.setStr(medico.getIndirizzo());
			
			if(phys.getTelecom()==null)
				phys.setTelecom(new TEL());
			
			TEL tel = phys.getTelecom();
			
			if(medico.getTelefono()!=null)
				tel.setHp(medico.getTelefono().trim());
			
			tel.setMail(medico.getEmail());
			
			String note = null;
			if(medico.getNote()!=null){
				int length = medico.getNote().length();
				note = medico.getNote().substring(0,length<=4000?length:4000);
			}
				
			phys.setNotes(note);
			
			
			
			saveOnTarget(phys);			
			saveMapping(medico, phys);
		}
		
		return getMapped(medico.getIdmedici());
	}
	
	public void importMedicoAur(SoImpattoMedici medico){
		if(!checkMapping(medico.getIdmedico())){
			Physician phys = new Physician();
			
			phys.setCreatedBy("MediciImporter_AUR");
			if(medico.getDatavaliditarecord()!=null){
				try {
					phys.setCreationDate(aurDateFormat.parse(medico.getDatavaliditarecord()));
				} catch (ParseException e) {
					thislog.error("Unable to parse AUR dataValiditaRecord: "+medico.getDatanascita());
				}
			}
			
			phys.setCode(getCodeValue("phidic.generic.physicianroles.mmg"));
			String mpi = medico.getIdmedico();
			
			if(phys.getName()==null)
				phys.setName(new EN());
			
			phys.getName().setFam(medico.getCognome());
			phys.getName().setGiv(medico.getNome());
			phys.setRegionalCode(medico.getCodiceregionale());
			if("MMG".equals(medico.getCategoria())){
				phys.setType(getCodeValue("phidic.generic.physiciantypes.med"));
				
			}else if("PLS".equals(medico.getCategoria())){
				phys.setType(getCodeValue("phidic.generic.physiciantypes.ped"));
				
			}
			phys.setSpecialization(medico.getSpecializzazione());
			
			Person person = findPerson(mpi);
			phys.setPerson(person);
			
			if(phys.getValidity()==null)
				phys.setValidity(new IVL<Date>());
			
			if(medico.getDatainiziorapporto()!=null){
				try {
					phys.getValidity().setLow(aurDateFormat.parse(medico.getDatainiziorapporto()));
				} catch (ParseException e) {
					thislog.error("Unable to parse AUR dataIniziorapporto: "+medico.getDatainiziorapporto());
				}
			}
			
			if(medico.getDatafinerapporto()!=null){
				try {
					phys.getValidity().setHigh(aurDateFormat.parse(medico.getDatafinerapporto()));
				} catch (ParseException e) {
					thislog.error("Unable to parse AUR dataFinerapporto: "+medico.getDatafinerapporto());
				}
			}

			
			phys.setOrganization(findOrganization(medico.getAsl()));
			
			
			if(phys.getTelecom()==null)
				phys.setTelecom(new TEL());
			
			TEL tel = phys.getTelecom();
			
			if(medico.getTelefono1()!=null)
				tel.setHp(medico.getTelefono1().trim());
			
			tel.setMail(medico.getEmail());			
			
			
			saveOnTarget(phys);
			
			((SessionImpl)sourceEm.getDelegate()).evict(medico);
			
			saveMapping(medico, phys);
		}
	}
	
	public List<Medici> readMediciSpisal() {
		List<Medici> medici = new ArrayList<Medici>();
		
		String hqlMedici = "SELECT m FROM Medici m";
		Query qMedici = sourceEm.createQuery(hqlMedici);
		medici = qMedici.getResultList();
		return medici;
	}
	
	public List<SoImpattoMedici> readMediciAur() {
		List<SoImpattoMedici> medici = new ArrayList<SoImpattoMedici>();
		
		String hqlMedici = "SELECT m FROM SoImpattoMedici m";
		Query qMedici = sourceEm.createQuery(hqlMedici);
		medici = qMedici.getResultList();
		return medici;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		List<MapMedici> maps = findMapping(MapMedici.class.getName());
		for(MapMedici m : maps){
			if(m.getIdprevnet()==id)
				return true;
		}
		
		String hqlMapping = "SELECT m FROM MapMedici m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapMedici> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapMedici map = list.get(0);
			thislog.warn("Already imported object. Source id: "+map.getIdprevnet()+". "+
													"Target id: "+map.getIdphi()+". "+
													"Imported by "+map.getCopiedBy()+" "+
													"on date "+map.getCopyDate());
			
			return true;
		}
		return false;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(String mpi){
		List<MapMedici> maps = findMapping(MapMedici.class.getName());
		for(MapMedici m : maps){
			if(mpi.equals(m.getMpi()))
				return true;
		}
		String hqlMapping = "SELECT m FROM MapMedici m WHERE m.mpi = :mpi";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("mpi", mpi.trim());
		List<MapMedici> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapMedici map = list.get(0);
			thislog.warn("Already imported object. Source mpi: "+map.getMpi()+". "+
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
	private Physician getMapped(long id){
		MapMedici map = null;
		List<MapMedici> maps = findMapping(MapMedici.class.getName());
		for(MapMedici m : maps){
			if(m.getIdprevnet()==id){
				map=m;
				break;
			}
		}
		if(map==null){
			String hqlMapping = "SELECT m FROM MapMedici m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapMedici> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Physician c = targetEm.find(Physician.class, map.getIdphi());
			if(c!=null){
				return c;
			}
			Query qPhysician = targetEm.createQuery("SELECT op FROM Physician op WHERE op.internalId = :id");
			qPhysician.setParameter("id", map.getIdphi());
			List<Physician> ops = qPhysician.getResultList();
			if(ops!=null && !ops.isEmpty()){
				return ops.get(0);
			}
		}
		
		return null;
	}

	private void saveMapping(Object source, Physician target){
		MapMedici map = new MapMedici();
		if(source instanceof Medici){
			map.setIdprevnet(((Medici)source).getIdmedici());
		
		}else if(source instanceof SoImpattoMedici){
			map.setMpi(((SoImpattoMedici)source).getIdmedico().trim());
		}
		
		
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(target.getCreationDate());
		map.setUlss(ulss);
				
		if(source instanceof Medici){
			saveOnSource(map);
		
		}else if(source instanceof SoImpattoMedici){
			sourceEm.persist(map);
		}
		
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Target id: "+map.getIdphi()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}

	/**
	 * Prima cerco sul db locale se ho già importato da aur la persona con l'mpi in input.
	 * Se non la trovo la cerco su aur e la importo 
	 * @param mpi
	 * @return
	 */
	private Person findPerson(String mpi){
		String searchPerson = "SELECT p FROM Person p WHERE p.isActive = 1 AND p.mpi = :mpi";
		Query qPerson = targetEm.createQuery(searchPerson);
		qPerson.setParameter("mpi", mpi);
		List<Person> list = qPerson.getResultList();
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}else{
			Person person = getFromAUR(mpi);
			setMpiOnHistoricVersions(person);
			
			return person;

		}
	}
	
	
	/**
	 * Cerca su AUR una persona con medesimo nome, cognome, cf, tessera sanitaria, sesso, data di nascita
	 * @param source
	 * @return
	 */
	private Person getFromAUR(String mpi){
		if(mpi!=null){
			queryAur.setParameter("mpi", mpi);//match esatto
			
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
				
				
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Data una Person importata da aur,
	 * cerco tutte le altre Person che rappresentano la stessa Person di esempio (stesso nome,cognome,cf,data e luogo di nascita) 
	 * ma storiche (isActive = 0) e imposto l'MPI 
	 * @param example
	 */
	private void setMpiOnHistoricVersions(Person example){
		if(example==null || example.getName()==null || example.getMpi()==null)
			return;
		
		String mpi = example.getMpi();
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
			targetEm.getTransaction().begin();
		}
		if(list!=null && !list.isEmpty()){

			for(Person p : list){
				p.setMpi(mpi);
				if(commit){
					targetEm.persist(p);
				}
			}
		}
		example.setMpi(mpi);
		example.setIsActive(true);
		if(commit){
			targetEm.persist(example);
			targetEm.flush();
			targetEm.getTransaction().commit();
		}
	}
	
	@Override
	protected void deleteImportedData(String ulss) {
		String hqlMedici = "SELECT mf.idphi FROM MapMedici mf ";
		if(ulss!=null && !ulss.isEmpty())
			hqlMedici+="WHERE mf.ulss = :ulss";
		
		Query qMedici = sourceEm.createQuery(hqlMedici);
		if(ulss!=null && !ulss.isEmpty())
			qMedici.setParameter("ulss", ulss);
		
		List<Long> idMedici = qMedici.getResultList();
		if(idMedici!=null && !idMedici.isEmpty()){
			if(commit){
				String deleteMedici = "DELETE FROM Physician p WHERE p.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelMedici = targetEm.createQuery(deleteMedici);
				qDelMedici.setParameter("ids", idMedici);
				qDelMedici.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_MEDICI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}

	}
}
