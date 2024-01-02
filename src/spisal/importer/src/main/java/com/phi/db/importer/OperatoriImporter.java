package com.phi.db.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.entities.baseEntity.MalattiaProfessionale;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.prevnet.entities.Operatori;
import com.prevnet.mappings.MapLavs;
import com.prevnet.mappings.MapMalprof;
import com.prevnet.mappings.MapOperatori;

@SuppressWarnings({"unchecked"})
public class OperatoriImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger.getLogger(OperatoriImporter.class.getName());
	
	private static OperatoriImporter instance = null;
	
	public static OperatoriImporter getInstance() {
		if(instance == null) {
			instance = new OperatoriImporter();
		}
		return instance;
	}
	

	public static void main(String[] args) {
		OperatoriImporter importer = new OperatoriImporter();
		//importer.deleteImportedData(ulss);
		List<Operatori> operatori = importer.readOperatori();
		if(operatori!=null){
			for(Operatori op : operatori){
				importer.importOperatore(op);
			}
		}
		importer.closeResource();

	}
	
	public Operatore importOperatore(Operatori source){
		if(!checkMapping(source.getIdoperatori())){
			Employee emp = null;
			Operatore op = null;
			
			if(source.getPassword()!=null){
				emp = new Employee();
				emp.setCreatedBy(this.getClass().getSimpleName()+ulss);
				emp.setCreationDate(source.getTimestampinsmod()==null?new Date():source.getTimestampinsmod());
				
				if(emp.getName()==null)
					emp.setName(new EN());
				
				emp.getName().setGiv(source.getNome());
				emp.getName().setFam(source.getCognome());
				emp.setUsername(source.getUsernament());
				emp.setPassword(source.getPassword());
				
				if(emp.getTelecom()==null)
					emp.setTelecom(new TEL());
				
				emp.getTelecom().setHp(source.getTelefono());
				emp.getTelecom().setMail(source.getEmail());
				
				if("1".equals(source.getChkupg())){
					emp.setUpg(true);
				}
				
				if(!"1".equals(source.getChkabilitato())){
					emp.setIsActive(false);
				}
			}
			
			if("1".equals(source.getOperativo())){
				op = new Operatore();
				
				op.setCreatedBy(this.getClass().getSimpleName()+ulss);
				op.setCreationDate(new Date());

				if(op.getName()==null)
					op.setName(new EN());

				op.getName().setGiv(source.getNome());
				op.getName().setFam(source.getCognome());
				
				op.setCode(getMappedCode(source.getNomina(), ulss));
				
				if(!"1".equals(source.getChkabilitato())){
					op.setIsActive(false);
				}
				
				op.setServiceDeliveryLocation(findUlss(ulss));
				op.setEnte(getCodeValue("phidic.spisal.pratiche.ente.spisal"));//SPISAL
				
				if(emp!=null)
					op.setEmployee(emp);
			}else if(emp!=null){
				op = new Operatore();
				
				op.setCreatedBy(this.getClass().getSimpleName()+ulss);
				op.setCreationDate(new Date());
				
				op.setName(emp.getName());
				op.setEmployee(emp);
			}
			
			saveOnTarget(emp);
			saveOnTarget(op);
			
			saveMapping(source, emp, op);
		}
		
		return getMapped(source.getIdoperatori());
	}
	
	public List<Operatori> readOperatori() {
		List<Operatori> fascicoli = new ArrayList<Operatori>();
		
		String hqlOperatori = "SELECT f FROM Operatori f";
		Query qOperatori = sourceEm.createQuery(hqlOperatori);
		fascicoli = qOperatori.getResultList();
		return fascicoli;
	}
	
	/**
	 * Controlla se l'entità id è già stata inserita in precedenza. Se sì logga le informazioni
	 * @param id
	 * @return
	 */
	private boolean checkMapping(long id){
		MapOperatori m = sourceEm.find(MapOperatori.class, id);
		if(m!=null)
			return true;
		
		String hqlMapping = "SELECT m FROM MapOperatori m WHERE m.idprevnet = :id";
		Query qMapping = sourceEm.createQuery(hqlMapping);
		qMapping.setParameter("id", id);
		List<MapOperatori> list = qMapping.getResultList();
		if(list!=null && !list.isEmpty()){
			MapOperatori map = list.get(0);
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
	private Operatore getMapped(long id){
		MapOperatori map = sourceEm.find(MapOperatori.class, id);

		if(map==null){
			String hqlMapping = "SELECT m FROM MapOperatori m WHERE m.idprevnet = :id";
			Query qMapping = sourceEm.createQuery(hqlMapping);
			qMapping.setParameter("id", id);
			List<MapOperatori> list = qMapping.getResultList();
			if(list!=null && !list.isEmpty()){
				map = list.get(0);
			}
		}
		if(map!=null){
			Operatore c = targetEm.find(Operatore.class, map.getMigoperatore());
			if(c!=null){
				return c;
			}
			Query qOperatore = targetEm.createQuery("SELECT op FROM Operatore op WHERE op.internalId = :id");
			qOperatore.setParameter("id", map.getMigoperatore());
			List<Operatore> ops = qOperatore.getResultList();
			if(ops!=null && !ops.isEmpty()){
				return ops.get(0);
			}
		}
		
		return null;
	}

	private void saveMapping(Operatori source, Employee employee, Operatore operatore){
		if(employee==null && operatore==null)
			return;
		
		MapOperatori map = new MapOperatori();
		map.setIdprevnet(source.getIdoperatori());
		map.setUlss(ulss);
		
		if(employee!=null){
			map.setIdphi(employee.getInternalId());
			map.setCopiedBy(employee.getCreatedBy());
			map.setCopyDate(new Date());
		}

		if(operatore!=null){
			map.setMigoperatore(operatore.getInternalId());
			map.setCopiedBy(operatore.getCreatedBy());
			map.setCopyDate(new Date());
		}
		
		saveOnSource(map);
		
		thislog.info("New imported object. Source id: "+map.getIdprevnet()+". "+
				"Employee id: "+map.getIdphi()+". "+
				"Operatore id: "+map.getMigoperatore()+". "+
				"Imported by "+map.getCopiedBy()+" "+
				"on date "+map.getCopyDate());
	}
	
	protected void deleteImportedData(String ulss){
		
		String hqlOperatori = "SELECT mop.migoperatore FROM MapOperatori mop ";
		if(ulss!=null && !ulss.isEmpty())
			hqlOperatori+="WHERE mop.ulss = :ulss";
		
		Query qOperatori = sourceEm.createQuery(hqlOperatori);
		if(ulss!=null && !ulss.isEmpty())
			qOperatori.setParameter("ulss", ulss);
		
		List<Long> idOps = qOperatori.getResultList();
		if(idOps!=null && !idOps.isEmpty()){
			if(commit){
				String deleteOps = "DELETE FROM Operatore o WHERE o.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelOperatori = targetEm.createQuery(deleteOps);
				qDelOperatori.setParameter("ids", idOps);
				qDelOperatori.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		String hqlEmployees = "SELECT mop.idphi FROM MapOperatori mop ";
		if(ulss!=null && !ulss.isEmpty())
			hqlEmployees+="WHERE mop.ulss = :ulss";
		
		Query qEmployees = sourceEm.createQuery(hqlEmployees);
		if(ulss!=null && !ulss.isEmpty())
			qEmployees.setParameter("ulss", ulss);
		
		List<Long> idEmps = qEmployees.getResultList();
		if(idEmps!=null && !idEmps.isEmpty()){
			if(commit){
				String deleteEmps = "DELETE FROM Employee e WHERE e.internalId IN (:ids)";
				targetEm.getTransaction().begin();
				Query qDelEmployees = targetEm.createQuery(deleteEmps);
				qDelEmployees.setParameter("ids", idEmps);
				qDelEmployees.executeUpdate();
				targetEm.getTransaction().commit();
			}
		}
		
		if(commit){
			String update = "DELETE FROM MAPPING_OPERATORI WHERE ulss = :ulss";
			sourceEm.getTransaction().begin();
			Query q = sourceEm.createNativeQuery(update);
			q.setParameter("ulss", ulss);
			q.executeUpdate();
			sourceEm.getTransaction().commit();
		}
	}
}
