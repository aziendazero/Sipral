package com.phi.db.importer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.impl.SessionImpl;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;

import com.phi.db.model.UpdateOrCreateMappingTables;
import com.phi.entities.baseEntity.AlfrescoDocument;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.entity.Organization;
import com.prevnet.entities.Attivitaditte;
import com.prevnet.entities.Cantieri;
import com.prevnet.entities.Comunicazionicancerogeni;
import com.prevnet.entities.Ditte;
import com.prevnet.entities.FascicoliPrevnet;
import com.prevnet.entities.InfortuniPrevnet;
import com.prevnet.entities.Medici;
import com.prevnet.entities.Pratiche;
import com.prevnet.entities.ProvvedimentiPrevnet;
import com.prevnet.entities.Rappresentantiditta;
import com.prevnet.entities.SoImpattoMedici;
import com.prevnet.entities.Sopralluoghidip;
import com.prevnet.entities.Tabelle;
import com.prevnet.entities.Utenti;

@SuppressWarnings({"unchecked","unused"})
public class Importer extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(Importer.class.getName());


	public Importer(){
		thislog.info("Starting Importer");

	}

	public static void main (String[] args) {

		String operation = "";
		Long startId = null;
		String xlsFileName = null;

		if(args!=null){
			
			boolean nextIsUlss = false;
			boolean nextIsDist = false;
			boolean nextIsIdUff= false;
			
			for(String arg : args){
				if("-del".equals(arg) && operation.isEmpty()){
					operation="delete";
					break;
				}else if("-prat".equals(arg) && operation.isEmpty()){
					operation="pratiche";
				}else if("-esp".equals(arg) && operation.isEmpty()){
					operation="esposti";
				}else if("-meds".equals(arg) && operation.isEmpty()){
					operation="medici";
				}else if("-fas".equals(arg) && operation.isEmpty()){
					operation="fascicoli";
				}else if("-maps".equals(arg) && operation.isEmpty()){
					operation="mappings";
				}else if("-docs".equals(arg) && operation.isEmpty()){
					operation="documents";
				}else if("-emp".equals(arg) && operation.isEmpty()){
					EmployeeCreator employeeCreator = new EmployeeCreator();

					try {
						employeeCreator.createEmployee();
					} catch (Exception e) {
						e.printStackTrace();
					} finally{
						employeeCreator.closeResource();
					}

				}else if("-noaur".equals(arg)){
					disableAur=true;
				}else if("-ulss".equals(arg) && operation.isEmpty()){
					nextIsUlss = true;
				}else if(nextIsUlss){
					EntityManagerUtilities.ulss=arg;
					UpdateOrCreateMappingTables.ulss=arg;
					nextIsUlss=false;
				}else if("-dist".equals(arg) && operation.isEmpty()){
					nextIsDist = true;
				}else if(nextIsDist){
					EntityManagerUtilities.distretto=arg;
					UpdateOrCreateMappingTables.distretto=arg;
					nextIsDist=false;
				}else if("-uff".equals(arg) && operation.isEmpty()){
					nextIsIdUff = true;
				}else if(nextIsIdUff){
					EntityManagerUtilities.ufficio=arg;
					nextIsIdUff=false;
				}else if("-eclipse".equals(arg)) {
					usingEclipse = true;
				}else if(arg != null && arg.contains("xls")) {
					xlsFileName = arg;
				}else if("-update".equals(arg)){
					operation="update";
				}else if("-bonificaInf".equals(arg)){
					operation="bonifica_infortuni";
				}else if("-bonificaRic".equals(arg)){
					operation="bonifica_ricorsi";
				}else if("-bonificaPol".equals(arg)){
					operation="bonifica_polesana";
				}else {
					try {
						startId = Long.parseLong(arg);
					} catch (NumberFormatException e) {
						startId = 0L;
					}
				}
			}
		}

		if (operation.isEmpty()) {
			log.error("missing operation, use switch like -maps, -prat ... see manual.odt");
		}
		
		Importer importer = new Importer();
		
		if("delete".equals(operation)){
			importer.deleteImportedData(ulss);

		}else if("update".equals(operation)){
			PersoneGiuridicheImporter ditteImporter = PersoneGiuridicheImporter.getInstance();
			SopralluoghiImporter sopImporter = SopralluoghiImporter.getInstance();
			
			List<Long> ditteId = importer.readDitte();
			if(ditteId!=null){
				for(Long pId : ditteId){
					Ditte  d = importer.sourceEm.find(Ditte.class, pId);
					ditteImporter.aggiornaDitta(d);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
			List<Long> rilId = importer.readRilevazioni();
			if(rilId!=null){
				for(Long rId : rilId){
					Sopralluoghidip  s = importer.sourceEm.find(Sopralluoghidip.class, rId);
					sopImporter.aggiornaSopralluogo(s);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
		}else if("bonifica_infortuni".equals(operation)){
			ProvvedimentiImporter provImporter = ProvvedimentiImporter.getInstance();
			InfortuniImporter infImporter = InfortuniImporter.getInstance();
			SopralluoghiImporter sopImporter = SopralluoghiImporter.getInstance();
			
			List<Long> provvId = importer.readProvvedimenti();
			if(provvId!=null){
				for(Long pId : provvId){
					ProvvedimentiPrevnet  p = importer.sourceEm.find(ProvvedimentiPrevnet.class, pId);
					provImporter.aggiornaProvvedimento(p);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
			List<Long> infId = importer.readInfortuni();
			if(infId!=null){
				for(Long iId : infId){
					InfortuniPrevnet  p = importer.sourceEm.find(InfortuniPrevnet.class, iId);
					infImporter.aggiornaInfortuni(p);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
			/*List<Long> rilId = importer.readSopralluoghi();
			if(rilId!=null){
				for(Long rId : rilId){
					Sopralluoghidip  source = importer.sourceEm.find(Sopralluoghidip.class, rId);
					sopImporter.aggiornaSopralluoghi(source);
					sourceEm.clear();
					targetEm.clear();
				}
			}*/
			
		}else if("bonifica_ricorsi".equals(operation)){
			PraticheImporter praticheImp = PraticheImporter.getInstance();
			List<Long> pratId = importer.readPraticheRicorsi();
			
			if(pratId!=null) {
				for(Long pId : pratId) {
					Pratiche p = importer.sourceEm.find(Pratiche.class, pId);
					praticheImp.aggiornaRicorsi(p);
					sourceEm.clear();
					targetEm.clear();					
				}
			}
			/*List<Long> infId = importer.readInfortuni();
			if(infId!=null){
				for(Long iId : infId){
					InfortuniPrevnet  p = importer.sourceEm.find(InfortuniPrevnet.class, iId);
					infImporter.aggiornaInfortuni(p);
					sourceEm.clear();
					targetEm.clear();
				}
			}*/

		}else if("bonifica_polesana".equals(operation)){
			ProvvedimentiImporter provImporter = ProvvedimentiImporter.getInstance();
			InfortuniImporter infImporter = InfortuniImporter.getInstance();
			SopralluoghiImporter sopImporter = SopralluoghiImporter.getInstance();
			
			/*
			List<Long> provvId = importer.readProvvedimenti();
			if(provvId!=null){
				for(Long pId : provvId){
					ProvvedimentiPrevnet  p = importer.sourceEm.find(ProvvedimentiPrevnet.class, pId);
					provImporter.aggiornaProvvedimento(p);
					sourceEm.clear();
					targetEm.clear();
				}
			}*/
			
			List<Long> infId = importer.readInfortuni();
			if(infId!=null){
				for(Long iId : infId){
					InfortuniPrevnet  p = importer.sourceEm.find(InfortuniPrevnet.class, iId);
					infImporter.aggiornaInfPolesana(p);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
			/**/
			List<Long> rilId = importer.readSopralluoghi();
			if(rilId!=null){
				for(Long rId : rilId){
					Sopralluoghidip  source = importer.sourceEm.find(Sopralluoghidip.class, rId);
					sopImporter.aggiornaSopralluoghi(source);
					sourceEm.clear();
					targetEm.clear();
				}
			}
			
		}else if("pratiche".equals(operation)){
			PraticheImporter praticheImp = PraticheImporter.getInstance();

			List<Long> praticheId = importer.readPratiche(startId);
			if(praticheId!=null){
				for(int i=0;i<praticheId.size();i++){
					try {
						Long pId=praticheId.get(i);
						sourceEm.getTransaction().begin();
						targetEm.getTransaction().begin();
						
						Pratiche p = importer.sourceEm.find(Pratiche.class, pId);
						praticheImp.importPratica(p);
						
						targetEm.flush();
						targetEm.getTransaction().commit();
						sourceEm.flush();
						sourceEm.getTransaction().commit();
						sourceEm.clear();
						targetEm.clear();
						statelessSourceEm.clear();
						statelessTargetEm.clear();
					}catch (Exception e){
						if(sourceEm.getTransaction().isActive())
							sourceEm.getTransaction().rollback();
						if(targetEm.getTransaction().isActive())
							targetEm.getTransaction().rollback();
						if(statelessSourceEm.getTransaction().isActive())
							statelessSourceEm.getTransaction().rollback();
						if(statelessTargetEm.getTransaction().isActive())
							statelessTargetEm.getTransaction().rollback();
						sourceEm.clear();
						targetEm.clear();
						statelessSourceEm.clear();
						statelessTargetEm.clear();
						e.printStackTrace();
						i--;
					}

				}
			}
		}else if("documents".equals(operation)){
			PraticheImporter praticheImp = PraticheImporter.getInstance();
			DocumentImporter docImp = DocumentImporter.getInstance();
			
			List<Long> praticheId = importer.readMappedPratiche(startId);
			if(praticheId!=null){
				for(Long pId : praticheId){
					sourceEm.getTransaction().begin();
					targetEm.getTransaction().begin();
					Pratiche source = importer.sourceEm.find(Pratiche.class, pId);
					Procpratiche target = praticheImp.importPratica(source);
					
					docImp.importDocuments(source, target);
					targetEm.flush();
					targetEm.getTransaction().commit();
					sourceEm.flush();
					sourceEm.getTransaction().commit();
					sourceEm.clear();
					targetEm.clear();
				}
			}
		}else if("esposti".equals(operation)){
			EspostiImporter espostiImp = EspostiImporter.getInstance();
			PersoneFisicheImporter.getInstance().setAvoidDeadlocks(true); //always avoid deadlocks with esposti
			List<Long> espId = importer.readEsposti();
			if(espId!=null){
				for(Long eId : espId){
					sourceEm.getTransaction().begin();
					targetEm.getTransaction().begin();
					Comunicazionicancerogeni c = importer.sourceEm.find(Comunicazionicancerogeni.class, eId);
					espostiImp.importEsposti(c);
					
					targetEm.flush();
					targetEm.getTransaction().commit();
					sourceEm.flush();
					sourceEm.getTransaction().commit();
					
					sourceEm.clear();
					targetEm.clear();
				}
			}
		}else if("medici".equals(operation)){
			MediciImporter medImp = MediciImporter.getInstance();
			
			Organization org = medImp.findOrganization(ulss);
			List<Medici> mediciSpisal = medImp.readMediciSpisal();
			sourceEm.getTransaction().begin();
			targetEm.getTransaction().begin();
			if(mediciSpisal!=null){
				for(Medici medicoSpisal : mediciSpisal){
					medImp.importMedicoSpisal(medicoSpisal, org);
				}
			}
			targetEm.flush();
			targetEm.getTransaction().commit();
			sourceEm.flush();
			sourceEm.getTransaction().commit();
			
			sourceEm.clear();
			targetEm.clear();
			if(!disableAur) {
				List<SoImpattoMedici> mediciAur = medImp.readMediciAur();
				if(mediciAur!=null){
					sourceEm.getTransaction().begin();
					
					
					for(SoImpattoMedici medicoAur : mediciAur){
						medImp.importMedicoAur(medicoAur);
						//per il trim....
						((SessionImpl)sourceEm.getDelegate()).evict(medicoAur);
					}
					
					sourceEm.flush();
					sourceEm.getTransaction().commit();
				}
			}
		}else if("fascicoli".equals(operation)){
			FascicoliImporter tagImp = FascicoliImporter.getInstance();
			List<FascicoliPrevnet> fascicoli = tagImp.readFascicoli();
			
			sourceEm.getTransaction().begin();
			targetEm.getTransaction().begin();
			if(fascicoli!=null){
				
				for(FascicoliPrevnet fascicolo : fascicoli){
					tagImp.importFascicolo(fascicolo);
				}
			}
			
			List<Tabelle> fascicoliTabelle = tagImp.readFascicoliTabelle();
			if(fascicoliTabelle!=null){
				for(Tabelle tab : fascicoliTabelle){
					tagImp.importFascicolo(tab);
				}
			}
			
			targetEm.flush();
			targetEm.getTransaction().commit();
			sourceEm.flush();
			sourceEm.getTransaction().commit();
			
			sourceEm.clear();
			targetEm.clear();
			
			
		}else if("mappings".equals(operation)){
			try {
				//UpdateOrCreateMappingTables.main(new String[] {"usingEclipse="+usingEclipse, xlsFileName});
				UpdateOrCreateMappingTables.main(new String[] {(usingEclipse?"-eclipse":""), xlsFileName});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if("test".equals(operation)){
			String prefix = "Pratica Vigilanza Spisal 19844";
			String templateName = "FRONTESPIZIO PRATICA.odt";
			
			int count = 0;
			Boolean exist = true;
			String nameToCheck = "";
			
			String query = "SELECT ad.internalId FROM AlfrescoDocument ad WHERE ad.name = :nameToCheck";
			
			try {
				/* Prefix: Pratica Medicina del lavoro 12345
				 * templateName: XYZ.odt
				 * 
				 * docName: prefix + "_count - " + templateName + ".odt" >>--> Pratica Medicina del lavoro 12345_count - XYZ.odt */  

				do {
					count++;
					String newPrefix = "";
					
					if (count==1){
						newPrefix = prefix;
					} else { 
						newPrefix = prefix + "_" + count;					
					}
					
					nameToCheck = newPrefix + " - " + templateName;

					org.hibernate.Query qry = ((Session)targetEm.getDelegate()).createQuery(query);
					qry.setParameter("nameToCheck", nameToCheck);
								
					@SuppressWarnings("unchecked")
					List<AlfrescoDocument> list = qry.list();
					
					if(list==null || list.size()==0) {
						log.info("Checked name: " + nameToCheck + " - OK!");
						exist = false;
					} else {
						log.info("Checked name: " + nameToCheck + " - Existing!");
					}
					
				} while (exist);
				
			} catch(Exception e){
				log.error(e.getMessage());
			}
						
		}else{
			PraticheImporter praticheImp = PraticheImporter.getInstance();
			
			List<Long> praticheId = importer.readPratiche(startId);
			if(praticheId!=null){
				for(Long pId : praticheId){
					sourceEm.getTransaction().begin();
					targetEm.getTransaction().begin();
					Pratiche p = sourceEm.find(Pratiche.class, pId);
					praticheImp.importPratica(p);
					
					targetEm.flush();
					targetEm.getTransaction().commit();
					sourceEm.flush();
					sourceEm.getTransaction().commit();
					
					sourceEm.clear();
					targetEm.clear();
				}
			}
		}
				

		importer.closeResource();
	}

	private void analyzeDictionaries(){
		Map<String, SingleTableEntityPersister> data = ((SessionImpl)sourceEm.getDelegate()).getSessionFactory().getAllClassMetadata() ;
		for(String key : data.keySet()){
			SingleTableEntityPersister meta = data.get(key);
			//NOME TABELLA
			String tableName = meta.getTableName();

			for(String prop : meta.getPropertyNames()){
				//COLONNA CORRISPONDENTE ALLA PROPRIETA'
				String[] columns = meta.getPropertyColumnNames(prop);
				//TIPO DI DATO
				Type type = meta.getPropertyType(prop);
				if(type instanceof ManyToOneType){
					ManyToOneType manyToOne = (ManyToOneType)type;
					if("com.prevnet.entities.Tabelle".equals(manyToOne.getAssociatedEntityName())){

						//CERCO IL CODICE TABELLA ASSOCIATO ALL'ATTRIBUTO
						String query = "SELECT DISTINCT tab.codicetabella FROM Tabelle tab WHERE tab.idtabelle IN ( "
								+ "SELECT DISTINCT ent."+prop+" FROM "+key+" ent)";

						Query qTab = sourceEm.createQuery(query);
						List<BigDecimal> codiciTabella = qTab.getResultList();
						if(codiciTabella!=null && !codiciTabella.isEmpty()){
							System.out.println(key+"\t"+prop+"\t"+tableName+"\t"+Arrays.toString(columns)+"\t"+codiciTabella);
						}
					}
				}
			}
		}
	}
	
	public List<Long> readMappedPratiche(Long startId) {
		if(startId==null)
			startId=0L;
		List<Long> pratiche = new ArrayList<Long>();
		
		String hqlPratiche = "SELECT idprevnet FROM MapPratiche WHERE idprevnet > "+startId+" order by idprevnet";
		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		pratiche = qPratiche.getResultList();
		return pratiche;
	}	

	public List<Long> readPratiche(Long startId) {
		if(startId==null)
			startId=0L;
		List<Long> pratiche = new ArrayList<Long>();
		
		String hqlPratiche = "SELECT f.idprocpratiche FROM Pratiche f where idprocedura IN (3, 6, 7, 8, 10, 16, 17, 48, 51, 53, 101, 153, 163, 166, 167, 170, 172, 173, 184) and idprocpratiche >= "+startId;

		// 54, 194
		if("050120".equals(ulss) || "050121".equals(ulss) || "050122".equals(ulss)){
			hqlPratiche = "SELECT f.idprocpratiche FROM Pratiche f where idprocedura IN (3, 6, 7, 8, 10, 16, 17, 48, 51, 53, 54, 101, 153, 163, 166, 167, 170, 172, 173, 184, 194) and idprocpratiche >= "+startId;
		}

		if(ufficio!=null) {
			hqlPratiche += " and f.idufficio = " + ufficio;
		}
		
		hqlPratiche += " order by idprocpratiche";
		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		pratiche = qPratiche.getResultList();
		return pratiche;
	}	
	
	
	public List<Long> readPraticheRicorsi() {
		
		List<Long> pratiche = new ArrayList<Long>();
		
		String hqlPratiche = "SELECT f.idprocpratiche FROM Pratiche f where idprocedura = 101 order by idprocpratiche";
		
		Query qPratiche = sourceEm.createQuery(hqlPratiche);
		pratiche = qPratiche.getResultList();
		return pratiche;
	}
	
	public List<Long> readEsposti() {

		List<Long> esposti = new ArrayList<Long>();
		
		String hqlEsp = "SELECT c.idcomunicazionicancerogeni FROM Comunicazionicancerogeni c order by c.idcomunicazionicancerogeni";
		Query qEsp = sourceEm.createQuery(hqlEsp);
		esposti = qEsp.getResultList();
		return esposti;
	}

	public List<Utenti> readUtenti(){
		List<Utenti> utenti = new ArrayList<Utenti>();

		String hqlUtenti = "SELECT u FROM Utenti u WHERE u.cf = 'BBCMLN55L31Z149G'";
		Query qUtenti = sourceEm.createQuery(hqlUtenti);
		utenti = qUtenti.getResultList();
		return utenti;
	}

	public List<Long> readDitte(){
		List<Long> ditte = new ArrayList<Long>();

		String hqlDitte = "SELECT d.idditte FROM Ditte d JOIN d.attivitaPrevnet a WHERE a.codice LIKE '%(2007)%'";
		Query qDitte = sourceEm.createQuery(hqlDitte);
		ditte = qDitte.getResultList();
		return ditte;
	}
	
	public List<Long> readProvvedimenti(){
		List<Long> prov = new ArrayList<Long>();

		String hqlProv = "SELECT p.idprevnet FROM MapProvvedimenti p";
		Query qProv = sourceEm.createQuery(hqlProv);
		prov = qProv.getResultList();
		return prov;
	}
	
	public List<Long> readInfortuni(){
		List<Long> inf = new ArrayList<Long>();

		String hqlInf = "SELECT m.idprevnet FROM MapInfortuni m";
		Query qInf = sourceEm.createQuery(hqlInf);
		inf = qInf.getResultList();
		return inf;
	}
	
	public List<Long> readSopralluoghi(){
		List<Long> inf = new ArrayList<Long>();

		String hqlInf = "SELECT m.idprevnet FROM MapSopralluoghi m";
		Query qInf = sourceEm.createQuery(hqlInf);
		inf = qInf.getResultList();
		return inf;
	}
	
	
	public List<Long> readRilevazioni(){
		List<Long> rilevazioni = new ArrayList<Long>();

		String hqlRilevazioni = "SELECT distinct s.idsopralluoghidip FROM Sopralluoghidip s WHERE s.misureambientalis is not empty";
		Query qRilevazioni = sourceEm.createQuery(hqlRilevazioni);
		rilevazioni = qRilevazioni.getResultList();
		return rilevazioni;
	}
	
	private List<Rappresentantiditta> readRappresentanti() {
		List<Rappresentantiditta> rappresentanti = new ArrayList<Rappresentantiditta>();

		String hqlRappresentanti = "SELECT f FROM Rappresentantiditta f WHERE f.idrappresentantiditta < 20";
		Query qRappresentanti = sourceEm.createQuery(hqlRappresentanti);
		rappresentanti = qRappresentanti.getResultList();
		return rappresentanti;
	}

	public List<Cantieri> readCantieri() {
		List<Cantieri> cantieri = new ArrayList<Cantieri>();

		String hqlCantieri = "SELECT f FROM Cantieri f WHERE f.idcantieri < 12";
		Query qCantieri = sourceEm.createQuery(hqlCantieri);
		cantieri = qCantieri.getResultList();
		return cantieri;
	}

	public List<Attivitaditte> readAttivita() {
		List<Attivitaditte> attivita = new ArrayList<Attivitaditte>();

		String hqlAttivita = "SELECT f FROM Attivitaditte f";
		Query qAttivita = sourceEm.createQuery(hqlAttivita);
		attivita = qAttivita.getResultList();
		return attivita;
	}

	@Override
	protected void deleteImportedData(String ulss) {
		if(ulss==null)
			return;
		
		targetEm.getTransaction().begin();
		targetEm.createNativeQuery("DELETE FROM z_protocollo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_articoli WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_gruppi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_provvedimenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_soggetto WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_attivita_operatori WHERE EXISTS (SELECT 1 FROM attivita a WHERE a.internal_id = attivita_id AND a.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_attivita WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_sopralluoghi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_acquisizione_informazioni WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_comp_spec WHERE EXISTS (SELECT 1 FROM infortuni i WHERE i.internal_id = infortuni_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_evitabilita WHERE EXISTS (SELECT 1 FROM infortuni i WHERE i.internal_id = infortuni_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_infortuni WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_infortuni_ext WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_formazione_arglegge81 WHERE EXISTS (SELECT 1 FROM formazione i WHERE i.internal_id = formazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_formazione_tipoattivita WHERE EXISTS (SELECT 1 FROM formazione i WHERE i.internal_id = formazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_formazione WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_info_arglegge81 WHERE EXISTS (SELECT 1 FROM informazione i WHERE i.internal_id = informazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_informazione_tipoattivita WHERE EXISTS (SELECT 1 FROM informazione i WHERE i.internal_id = informazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_informazione WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_lifestyle_arglegge81 WHERE EXISTS (SELECT 1 FROM lifestyle i WHERE i.internal_id = lifestyle_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_lifestyle_tipoattivita WHERE EXISTS (SELECT 1 FROM lifestyle i WHERE i.internal_id = lifestyle_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_lifestyle WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_benorg_arglegge81 WHERE EXISTS (SELECT 1 FROM benessere_org i WHERE i.internal_id = benorg_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_benessereorg_tipoattivita WHERE EXISTS (SELECT 1 FROM benessere_org i WHERE i.internal_id = benessereorg_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_benessere_org WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_persona_giuridica_sede WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_parere_tecnico WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_persona_ruolo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_operaio_amianto WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_vigilanza WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_tag_fascicolo_procpratiche WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = procpratiche_internal_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_pratiche_riferimenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_procpratiche_event WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_procpratiche_upg WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = procpratiche_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_pratica_operatori WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = pratica_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_inchiesta_infortunio WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_procpratiche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_fattori_rischio WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_ditte_malattie WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_malattie_professionali WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_committenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_ditte_cantiere WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_persone_cantiere WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_cantieri WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		//targetEm.createNativeQuery("DELETE FROM z_sedi_persone WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_partecipanti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_cariche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_attivita_istat WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_sedi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_persone_giuridiche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_operatore WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_employee WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM z_tag_fascicolo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		
		targetEm.createNativeQuery("DELETE FROM protocollo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM articoli WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM gruppi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM provvedimenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM soggetto WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM attivita_operatori WHERE EXISTS (SELECT 1 FROM attivita a WHERE a.internal_id = attivita_id AND a.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM attivita WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM sopralluoghi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM acquisizione_informazioni WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM comp_spec WHERE EXISTS (SELECT 1 FROM infortuni i WHERE i.internal_id = infortuni_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM evitabilita WHERE EXISTS (SELECT 1 FROM infortuni i WHERE i.internal_id = infortuni_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM infortuni WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM infortuni_ext WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM formazione_arglegge81 WHERE EXISTS (SELECT 1 FROM formazione i WHERE i.internal_id = formazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM formazione_tipoattivita WHERE EXISTS (SELECT 1 FROM formazione i WHERE i.internal_id = formazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM formazione WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM info_arglegge81 WHERE EXISTS (SELECT 1 FROM informazione i WHERE i.internal_id = informazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM informazione_tipoattivita WHERE EXISTS (SELECT 1 FROM informazione i WHERE i.internal_id = informazione_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM informazione WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM lifestyle_arglegge81 WHERE EXISTS (SELECT 1 FROM lifestyle i WHERE i.internal_id = lifestyle_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM lifestyle_tipoattivita WHERE EXISTS (SELECT 1 FROM lifestyle i WHERE i.internal_id = lifestyle_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM lifestyle WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM benorg_arglegge81 WHERE EXISTS (SELECT 1 FROM benessere_org i WHERE i.internal_id = benorg_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM benessereorg_tipoattivita WHERE EXISTS (SELECT 1 FROM benessere_org i WHERE i.internal_id = benessereorg_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM benessere_org WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM persona_giuridica_sede WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM parere_tecnico WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM persona_ruolo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM operaio_amianto WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM vigilanza WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM tag_fascicolo_procpratiche WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = procpratiche_internal_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM procpratiche_event WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM procpratiche_upg WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = procpratiche_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM pratica_operatori WHERE EXISTS (SELECT 1 FROM procpratiche i WHERE i.internal_id = pratica_id AND i.created_by like :createdBy)").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM inchiesta_infortunio WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM procpratiche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM pratiche_riferimenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM fattori_rischio WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM ditte_malattie WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM malattie_professionali WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM committenti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM ditte_cantiere WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM persone_cantiere WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM cantieri WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM cariche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM sedi_persone WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM partecipanti WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM attivita_istat WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM sedi WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM persone_giuridiche WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM person WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM operatore WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM employee WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM physician WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		targetEm.createNativeQuery("DELETE FROM tag_fascicolo WHERE created_by like :createdBy").setParameter("createdBy", "%Importer"+ulss).executeUpdate();
		
		targetEm.getTransaction().commit();
		
		
		sourceEm.getTransaction().begin();
		sourceEm.createQuery("DELETE FROM MapAttivita m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapCantieri m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapFascicoli m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapInfortuni m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapInterventi m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapMalprof m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapMedici m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapNips m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapOperatori m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapPersoneFisiche m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapPersoneGiuridiche m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapPratiche m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapProtocollo m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapProvvedimenti m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapRappresentanti m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapRiunioni m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapSopralluoghi m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.createQuery("DELETE FROM MapVisSpec m WHERE m.ulss = :ulss").setParameter("ulss", ulss).executeUpdate();
		sourceEm.getTransaction().commit();
	}
}
