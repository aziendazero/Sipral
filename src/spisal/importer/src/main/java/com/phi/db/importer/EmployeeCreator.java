package com.phi.db.importer;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.role.Employee;
import com.phi.security.Security;

import jxl.read.biff.BiffException;

/**
 * Read db properties from mySql.build.properties or oracle.build.properties and
 * crate an employee into pHi db.
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author Zupan
 */
@SuppressWarnings("rawtypes")
public class EmployeeCreator extends EntityManagerUtilities {

	//user to be created on rim
	/**
	 * 
	 */
	private static String username = "admin";
	private static String password = "admin";
	boolean passwordEncryption = false;

	//detail of user
	private static String givenName = "name";
	private static String familyName = "surname";

	//detail for roles
	private static String domainUserRole = "EmployeeFunction";
	private static String codeAdmin = "ADMIN";    //code of admin role. 
	private static String codeDoctor = "";  //leave it empty to do not set a second role as doctor to this user.



	protected static String loginHql = "FROM Employee WHERE username = :user";
	static final Logger thislog = Logger.getLogger(EmployeeCreator.class.getName());

	public static void main (String[] args) throws PersistenceException, DictionaryException, BiffException, IOException  {

		EmployeeCreator employeeCreator = new EmployeeCreator();

		employeeCreator.createEmployee();
		employeeCreator.closeResource();

	}

	public void createEmployee() throws PersistenceException,
	DictionaryException {
		thislog.info("creating user with username: \""+username+"\" and password: \""+password+"\"");
		Query q = targetEm.createQuery(loginHql);
		q.setParameter("user", username);

		List oldList = q.getResultList();

		if (!oldList.isEmpty()) {

			thislog.error("Already existing user!");

		} else {   

			targetEm.getTransaction().begin();


			Employee employee = new Employee();
			employee.setUsername(username);

			if(passwordEncryption)
				password = Security.crypt(password);

			employee.setPassword(password);
			
			Calendar cal = Calendar.getInstance(); // locale-specific
			cal.setTime(new Date());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			IVL<Date> empValidDate = new IVL<Date>();
			empValidDate.setLow(cal.getTime());

			employee.setEffectiveTime(empValidDate);

			EN en = new EN();
			en.setGiv(givenName);
			en.setFam(familyName);

			employee.setName(en);

			//Admin
			EmployeeRole roleAdmin = new EmployeeRole();

			VocabulariesImpl voc = new VocabulariesImpl(targetEm);
			CodeValueRole roleCodeAdmin = (CodeValueRole)voc.getCodeValueCsDomainCode("ROLES",domainUserRole,codeAdmin);

			if (roleCodeAdmin == null) {
				thislog.error("Unable to find code value: " + codeAdmin + " in domain "+domainUserRole);
				return;
			}

			roleAdmin.setCode(roleCodeAdmin);

			IVL<Date> roleValidDate = new IVL<Date>();
			roleValidDate.setLow(cal.getTime());

			roleAdmin.setEffectiveTime(roleValidDate);

			employee.addEmployeeRole(roleAdmin);
			thislog.info("added role "+roleCodeAdmin.getDisplayName()+" with code "+roleCodeAdmin.getCode());

			if (codeDoctor != null && !codeDoctor.isEmpty() ) {
				//Doctor
				EmployeeRole roleMedico = new EmployeeRole();

				CodeValueRole roleCodeMedico = (CodeValueRole)voc.getCodeValueCsDomainCode("ROLES",domainUserRole,codeDoctor);

				if (roleCodeMedico == null) {
					thislog.error("Unable to find code value: "  + codeAdmin + "in domain "+domainUserRole);
					return;
				}

				roleMedico.setCode(roleCodeMedico);

				IVL<Date> medicoValidDate = new IVL<Date>();
				medicoValidDate.setLow(cal.getTime());
				roleMedico.setEffectiveTime(medicoValidDate);

				employee.addEmployeeRole(roleMedico);
				thislog.info("added role "+roleCodeMedico.getDisplayName()+" with code "+roleCodeMedico.getCode());
			}


			thislog.info("persisting Employee...");
			targetEm.persist(employee);

			targetEm.flush();

			targetEm.getTransaction().commit();

			thislog.info("user created");
		}
	}	

	@Override
	protected void deleteImportedData(String ulss) {
		// TODO Auto-generated method stub
		
	}	
}