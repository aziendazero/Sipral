package com.phi.db.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.amianto.entities.Anagrafica;
import com.phi.db.model.UpdateOrCreateMappingTables;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.role.Person;

@SuppressWarnings({"unchecked","unused"})
public class Importer extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(Importer.class.getName());


	public Importer(){
		thislog.info("Starting Importer");

	}

	public static void main (String[] args) {

		String operation = "";
		Integer startId = null;
		String xlsFileName = null;

		if(args!=null){
			
			boolean nextIsUlss = false;
			boolean nextIsDist = false;
			for(String arg : args){
				if("-maps".equals(arg) && operation.isEmpty()){
					operation="mappings";
				}else if("-ulss".equals(arg) && operation.isEmpty()){
					nextIsUlss = true;
				}else if(nextIsUlss){
					EntityManagerUtilities.ulss=arg;
					//UpdateOrCreateMappingTables.ulss=arg;
					nextIsUlss=false;
				}else if("-dist".equals(arg) && operation.isEmpty()){
					nextIsDist = true;
				}else if(nextIsDist){
					EntityManagerUtilities.distretto=arg;
					UpdateOrCreateMappingTables.distretto=arg;
					nextIsDist=false;
				}else if("-eclipse".equals(arg)) {
					usingEclipse = true;
				}else if("-fix".equals(arg)) {
					operation="fix";
				}else if("-fixAnamnesi".equals(arg)) {
					operation="fixAnamnesi";
				}else if(arg != null && arg.contains("xls")) {
					xlsFileName = arg;
				}
				else {
					try {
						startId = Integer.parseInt(arg);
					} catch (NumberFormatException e) {
						startId = 0;
					}
				}
			}
		}

		if (operation.isEmpty()) {
			log.error("missing operation, use switch like -maps, -prat ... see manual.odt");
		}
		
		Importer importer = new Importer();
		
		if("mappings".equals(operation)){
			try {
				UpdateOrCreateMappingTables.main(new String[] {"usingEclipse="+usingEclipse, xlsFileName});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if("fix".equals(operation) && startId>0){
			List<Integer> pazientiId = importer.readPazienti(startId);
			AmiantoImporter pazientiImp = AmiantoImporter.getInstance();
			Anagrafica source = sourceEm.find(Anagrafica.class, startId);
			if(source.getCognomeENome()!=null && source.getDataNascita()!=null) {
				Person paziente = pazientiImp.importPerson(source);
				Procpratiche pratica = pazientiImp.getMappedPratica(startId);
				if(paziente!=null && pratica!=null) {
					pazientiImp.fixPratica(pratica, paziente, source);
				}
			}
			
			sourceEm.clear();
			targetEm.clear();
		}else if("fixAnamnesi".equals(operation)){
			List<Integer> pazientiId = importer.readPazienti(startId);
			if(pazientiId!=null){
				AmiantoImporter pazientiImp = AmiantoImporter.getInstance();
				for(Integer pId : pazientiId){
					Anagrafica source = sourceEm.find(Anagrafica.class, pId);
					if(source.getCognomeENome()!=null && source.getDataNascita()!=null) {
						Person paziente = pazientiImp.importPerson(source);
						Procpratiche pratica = pazientiImp.getMappedPratica(pId);
						if(paziente!=null && pratica!=null) {
							pazientiImp.fixAnamnesi(pratica, paziente, source);
						}
					}
					
					sourceEm.clear();
					targetEm.clear();
				}
			}	
		}else{
			List<Integer> pazientiId = importer.readPazienti(startId);
			if(pazientiId!=null){
				AmiantoImporter pazientiImp = AmiantoImporter.getInstance();
				for(Integer pId : pazientiId){
					Anagrafica paziente = sourceEm.find(Anagrafica.class, pId);
					if(paziente.getCognomeENome()!=null && paziente.getDataNascita()!=null) {
						Person patient = pazientiImp.importPerson(paziente);
					}
					
					sourceEm.clear();
					targetEm.clear();
				}
			}				
		}

		importer.closeResource();
	}

	public List<Integer> readPazienti(Integer startId) {
		if(startId==null)
			startId=0;
		List<Integer> pazienti = new ArrayList<Integer>();
		
		String hqlPazienti = "SELECT a.id FROM Anagrafica a WHERE a.id > "+startId+" order by a.id";
		Query qPazienti = sourceEm.createQuery(hqlPazienti);
		pazienti = qPazienti.getResultList();
		return pazienti;
	}
	
	@Override
	protected void deleteImportedData(String ulss) {
		if(ulss==null)
			return;		
	}
}
