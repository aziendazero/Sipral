package com.phi.json;

import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.db.importer.EntityManagerUtilities;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.dataTypes.CodeValueRole;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.act.Dosage;
import com.phi.entities.baseEntity.PrescriptionMedicine;
import com.phi.entities.dataTypes.PQ;

import java.util.*;

/**
 * Read db properties from mySql.build.properties or oracle.build.properties and
 * test json
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author Zupan
 */

public class TestPrescriptionMedicine extends EntityManagerUtilities {
	
	private static ObjectMapper mapper;

	private static final Logger log = Logger.getLogger(TestPrescriptionMedicine.class.getName());
	
	public static void main (String[] args) throws PersistenceException, DictionaryException {

		TestPrescriptionMedicine testPrescriptionMedicine = new TestPrescriptionMedicine();

		testPrescriptionMedicine.db = supportedDb.mySql;

		testPrescriptionMedicine.setupLog();
		testPrescriptionMedicine.setupEntityManagerFactory();
		testPrescriptionMedicine.setupEm();
		testPrescriptionMedicine.setupJson();

		testPrescriptionMedicine.test();
		testPrescriptionMedicine.closeResource();

	}

	public void test() {


		try {

			PrescriptionMedicine prescriptionMed = new PrescriptionMedicine();

			Dosage dosage = new Dosage();

			dosage.setDayInterval(1);
			dosage.setDaytime(new Date());
			PQ quantity = new PQ();
			quantity.setValue(100d);
			quantity.setUnit("tir");
			dosage.setQuantity(quantity);

			ArrayList<Dosage> dosages = new ArrayList<Dosage>();
			dosages.add(dosage);

			prescriptionMed.setDosage(dosages);

			Prescription presc = new Prescription();

			VocabulariesImpl voc = new VocabulariesImpl(em);
			CodeValueRole roleCodeAdmin = (CodeValueRole)voc.getCodeValueCsDomainCode("ROLES", "EmployeeFunction", "admin");

			presc.setAuthorRole(roleCodeAdmin);

			presc.addPrescriptionMedicine(prescriptionMed);
			//prescriptionMed.setPrescription(presc);


			String jSon = mapper.writeValueAsString(presc);

			Prescription prescHidratezd = mapper.readValue(jSon, Prescription.class);

			System.out.println("jSon");

			if (prescHidratezd.equals(presc)) {
				System.out.println("Top!");
			} else {
				System.out.println("Nooooouuu!");
			}


		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	public void setupJson() {
		
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//		df.setTimeZone(TimeZone.getTimeZone("UTC"));

		BaseEntityDeserializer.setEm(em);
		CodeValueDeserializer.setEm(em);
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
//		mapper.setDateFormat(df);
		mapper.registerModule(new HibernateModule());
		
	}

	
}