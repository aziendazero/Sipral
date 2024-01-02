package com.phi.db.arpav.importer;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapOperatore;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;

public class OperatoreImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(OperatoreImporter.class.getName());

	private static OperatoreImporter instance = null;

	public static OperatoreImporter getInstance() {
		if (instance == null) {
			instance = new OperatoreImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) {
		Operatore oprt = getMapped(row[0].getContents());
		if (oprt == null) {
			oprt = new Operatore();
			oprt.setCreatedBy(this.getClass().getSimpleName() + ulss);
			oprt.setCreationDate(new Date());
			EN name = new EN();
			name.setFam(row[1].getContents());
			name.setGiv(row[2].getContents());

			oprt.setName(name);
			if(row[3].getContents()!=null && !row[3].getContents().trim().isEmpty()){
				oprt.setEmployee(getEmployee(row[3].getContents()));
			}
			oprt.setCode(getCodeValue(row[4].getContents()));
			oprt.setServiceDeliveryLocation(findArpav(row[5].getContents()));

			saveOnTarget(oprt);
			saveMapping(row[0].getContents(), oprt);
		} else {
			log.info("Already imported Operatore with id: "
					+ row[0].getContents());
		}
	}

	public void importMapRow(Cell[] row){
		MapOperatore map = new MapOperatore();
		map.setIdexcel(row[0].getContents());
		map.setIdphi(Long.valueOf(row[1].getContents()));
		map.setCopiedBy(this.getClass().getSimpleName() + ulss);
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ map.getIdexcel() + ". " + "Operatore id: "
				+ map.getIdphi() + ". " + "Imported by "
				+ map.getCopiedBy() + " " + "on date "
				+ map.getCopyDate());

	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public Operatore getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapOperatore m WHERE m.idexcel = :id";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idexcel);
		List<MapOperatore> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapOperatore map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM Operatore e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<Operatore> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}
	
	public Operatore getTarget(String idexcel) {
		// cerco tra gli operatori inseriti manualmente l'internal_id corrispondente a idexcel
			Query qEmp = targetEm
					.createQuery("SELECT e FROM Operatore e WHERE e.internalId = :id");
			qEmp.setParameter("id", Long.valueOf(idexcel));
			List<Operatore> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		return null;
	}

	private void saveMapping(String sourceId, Operatore target) {
		MapOperatore map = new MapOperatore();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "Operatore id: "
				+ target.getName() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}

	private Employee getEmployee(String id) {
		EmployeeImporter eImp = EmployeeImporter.getInstance();
		return eImp.getMapped(id);
	}

}
