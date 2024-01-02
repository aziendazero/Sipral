package com.phi.db.arpav.importer;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import jxl.Cell;

import org.apache.log4j.Logger;

import com.phi.db.arpav.mapping.MapEmployee;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.II4Employee;
import com.phi.entities.role.Employee;

public class EmployeeImporter extends EntityManagerUtilities {
	private static final Logger thislog = Logger
			.getLogger(EmployeeImporter.class.getName());

	private static EmployeeImporter instance = null;

	public static EmployeeImporter getInstance() {
		if (instance == null) {
			instance = new EmployeeImporter();
		}
		return instance;
	}

	public void importRow(Cell[] row) {
		Employee emp = getMapped(row[0].getContents());
		if (emp == null) {
			emp = new Employee();
			emp.setCreatedBy(this.getClass().getSimpleName() + ulss);
			emp.setCreationDate(new Date());
			EN name = new EN();
			name.setFam(row[1].getContents());
			name.setGiv(row[2].getContents());
			emp.setFiscalCode(row[3].getContents());
			emp.setName(name);
			II4Employee id = new II4Employee();
			id.setRoot("PERSID");
			id.setExtension(row[4].getContents());
			emp.addId(id);

			saveOnTarget(emp);
			saveMapping(row[0].getContents(), emp);
		} else {
			log.info("Already imported Employee with id: "
					+ row[0].getContents());
		}
	}

	/**
	 * Ritorna l'entità mappata nel db di destinazione corrispondente all'id di
	 * input
	 * 
	 * @param idexcel
	 * @return
	 */
	public Employee getMapped(String idexcel) {
		// cerco nella tabella di mapping l'internal_id corrispondente a idexcel
		String hqlMapping = "SELECT m FROM MapEmployee m WHERE m.idexcel = :id";
		Query qMapping = targetEm.createQuery(hqlMapping);
		qMapping.setParameter("id", idexcel);
		List<MapEmployee> list = qMapping.getResultList();
		if (list != null && !list.isEmpty()) {
			MapEmployee map = list.get(0);

			// tiro su dal db phi l'entità
			Query qEmp = targetEm
					.createQuery("SELECT e FROM Employee e WHERE e.internalId = :id");
			qEmp.setParameter("id", map.getIdphi());
			List<Employee> lp = qEmp.getResultList();
			if (lp != null && !lp.isEmpty()) {
				return lp.get(0);
			}
		}
		return null;
	}

	private void saveMapping(String sourceId, Employee target) {
		MapEmployee map = new MapEmployee();
		map.setIdexcel(sourceId);
		map.setIdphi(target.getInternalId());
		map.setCopiedBy(target.getCreatedBy());
		map.setCopyDate(new Date());

		saveOnTarget(map);

		thislog.info("New imported object. Source id: "
				+ target.getInternalId() + ". " + "Employee id: "
				+ target.getId("PERSID").getExtension() + ". " + "Imported by "
				+ target.getCreatedBy() + " " + "on date "
				+ target.getCreationDate());
	}
}
