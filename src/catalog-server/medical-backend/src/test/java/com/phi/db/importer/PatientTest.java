package com.phi.db.importer;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.actions.PatientAction;
import com.phi.entities.role.Patient;

/**
 * Read db properties from mySql.build.properties or oracle.build.properties and
 * crate an employee into pHi db.
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author ROSSI
 */

public class PatientTest extends EntityManagerUtilities {

	static final Logger thislog = Logger.getLogger(PatientTest.class.getName());
	
	public PatientTest() {
		setupLog(thislog);
	}
	
	public static void main (String[] args) throws PersistenceException, DictionaryException  {
	
		PatientTest employeeCreator = new PatientTest();

		
		thislog.info("Starting Employee Creator");

		employeeCreator.setupEntityManagerFactory();
		employeeCreator.setupEm();

		employeeCreator.queryPatientEncounterAvailabilityTime();
		employeeCreator.getAPatientEncounter();
		employeeCreator.closeResource();

	}

	private PatientEncounter getAPatientEncounter() {
		Criteria patEncounterCriteria = ((Session)em.getDelegate()).createCriteria(PatientEncounter.class);
		
		patEncounterCriteria.add(Restrictions.eq("this.patient", em.getReference(Patient.class, 105L)));
		patEncounterCriteria.add(Restrictions.eq("this.isActive", true));
		patEncounterCriteria.add(Restrictions.isNotNull("this.availabilityTime"));
		patEncounterCriteria.createCriteria("code").add(Restrictions.eq("code", "IMP"));
		patEncounterCriteria.createCriteria("statusCode").add(Restrictions.eq("code", "active"));
		
		patEncounterCriteria.setMaxResults(1);
		List<PatientEncounter> listpatenc=patEncounterCriteria.list();
		PatientEncounter patEncounter;
		if (listpatenc == null || listpatenc.isEmpty()){
			patEncounter= null;}
		else{
			patEncounter=listpatenc.get(0);
		}
		
		return patEncounter;
	}

	public String queryPatientEncounterAvailabilityTime() throws PersistenceException,
			DictionaryException {
		Criteria patientCriteria = ((Session)em.getDelegate()).createCriteria(PatientEncounter.class);
		ProjectionList slotProjection = Projections.projectionList();
		patientCriteria.setProjection(slotProjection);
		slotProjection.add(Projections.property("availabilityTime"), "availabilityTime");
	
		patientCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(PatientEncounter.class));
		patientCriteria.add(Restrictions.eq("this.patient", em.getReference(Patient.class, 105L)));
		List<Map<String, Object>> listpatenc=patientCriteria.list();
		if (listpatenc == null)
			 return "";
		String ret="";
		for (Map<String, Object> patenc : listpatenc) {
			ret+=patenc.get("AvailabilityTime")+" <br/>";
		}
		
		return ret;
	}	
}