package com.phi.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.db.importer.EntityManagerUtilities;
import com.phi.entities.baseEntity.Medicine;
import com.phi.entities.dataTypes.CodeValueCodifa;
import org.apache.log4j.Logger;

/**
 * Test for json serialization/deserialization for Codevalues defined in backend and not in core
 *
 * Read db properties from mySql.build.properties or oracle.build.properties
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author Zupan
 */

public class TestCodeValueInBackend extends EntityManagerUtilities {
	
	private static ObjectMapper mapper;

	private static final Logger log = Logger.getLogger(TestCodeValueInBackend.class.getName());
	
	public static void main (String[] args) throws PersistenceException, DictionaryException {

		TestCodeValueInBackend testPrescriptionMedicine = new TestCodeValueInBackend();

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

			Medicine medicine = new Medicine();

			CodeValueCodifa cvCodifa = new CodeValueCodifa();

			cvCodifa.setId("bel.box");
			cvCodifa.setCode("un bel box");
			cvCodifa.setDescription("un sai bel box");

			medicine.setBox(cvCodifa);

			String jSon = mapper.writeValueAsString(medicine);

			Medicine medicineHidratezd = mapper.readValue(jSon, Medicine.class);

			System.out.println("jSon");

			if (medicineHidratezd.equals(medicine)) {
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