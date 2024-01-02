package com.phi.db.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.db.model.UpdateOrCreateMappingTables;
import com.phi.entities.role.Person;
import com.sorves.entities.Anagrafica;

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
			for(String arg : args){
				if("-maps".equals(arg) && operation.isEmpty()){
					operation="mappings";
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
				}else if("-eclipse".equals(arg)) {
					usingEclipse = true;
				}else if(arg != null && arg.contains("xls")) {
					xlsFileName = arg;
				}
				else {
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
		
		if("mappings".equals(operation)){
			try {
				UpdateOrCreateMappingTables.main(new String[] {"usingEclipse="+usingEclipse, xlsFileName});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			List<Long> pazientiId = importer.readPazienti(startId);
			if(pazientiId!=null){
				SorvesImporter pazientiImp = SorvesImporter.getInstance();
				for(Long pId : pazientiId){
					try {
						Anagrafica paziente = sourceEm.find(Anagrafica.class, pId);
						Person patient = pazientiImp.importPerson(paziente);
						
						sourceEm.clear();
						targetEm.clear();
					} catch (Exception e){
						sourceEm.clear();
						targetEm.clear();
						importer.closeResource();
						String command = "java -jar sorves-importer.jar -ulss 050113 -dist DC -prat "+pId;
						try {
							Runtime.getRuntime().exec(command);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
						return;
					}
				}
			}				
		}

		importer.closeResource();
	}

	public List<Long> readPazienti(Long startId) {
		if(startId==null)
			startId=0L;
		List<Long> pazienti = new ArrayList<Long>();
		
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
