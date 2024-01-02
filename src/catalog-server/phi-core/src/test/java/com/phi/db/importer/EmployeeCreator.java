package com.phi.db.importer;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.db.importer.EntityManagerUtilities.supportedDb;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.role.Employee;
import com.phi.security.Security;

/**
 * Read db properties from mySql.build.properties or oracle.build.properties and
 * crate an employee into pHi db.
 * 
 * db Variable must me set to mySql or oracle
 * 
 * @author Zupan
 */

public class EmployeeCreator extends EntityManagerUtilities {

	//user to be created on rim
	private static String username = "admin";
	private static String password = "admin";
	boolean passwordEncryption = false;
	
	//detail of user
	private static String givenName = "name";
	private static String familyName = "surname";

	//detail for roles
	private static String domainUserRole = "EmployeeFunction";
	private static String codeAdmin = "admin";    //code of admin role. 
	private static String codeDoctor = null;  //leave it empty to do not set a second role as doctor to this user.
	
	
	
	protected static String loginHql = "FROM Employee WHERE username = :user";
	static final Logger thislog = Logger.getLogger(EmployeeCreator.class.getName());
	
	public EmployeeCreator() {
		setupLog(thislog);
	}
	
	public static void main (String[] args) throws PersistenceException, DictionaryException  {
	
		EmployeeCreator employeeCreator = new EmployeeCreator();

		thislog.info("Base path: " + new File(".").getAbsolutePath());
		
		thislog.info("Starting Employee Creator");
		
		employeeCreator.db = supportedDb.mySql; 

		employeeCreator.setupEntityManagerFactory();
		employeeCreator.setupEm();

		employeeCreator.createEmployee();
		employeeCreator.closeResource();

	}

	public void createEmployee() throws PersistenceException,
			DictionaryException {
		thislog.info("creating user with username: \""+username+"\" and password: \""+password+"\"");
		Query q = em.createQuery(loginHql);
		q.setParameter("user", username);
        		
		List oldList = q.getResultList();
		
		if (!oldList.isEmpty()) {
			
			log.error("Already existing user!");
		
		} else {   
			
			em.getTransaction().begin();
			
			
			Employee employee = new Employee();
			employee.setUsername(username);
			
			 if(passwordEncryption)
		    		password = Security.crypt(password);
			
			employee.setPassword(password);
			
			IVL<Date> empValidDate = new IVL<Date>();
			empValidDate.setLow(new Date());
			
			employee.setEffectiveTime(empValidDate);

			EN en = new EN();
			en.setGiv(givenName);
			en.setFam(familyName);
			
			employee.setName(en);
			
			//Admin
			EmployeeRole roleAdmin = new EmployeeRole();
			
			VocabulariesImpl voc = new VocabulariesImpl(em);
			CodeValueRole roleCodeAdmin = (CodeValueRole)voc.getCodeValueCsDomainCode("ROLES", domainUserRole, codeAdmin);
			
			if (roleCodeAdmin == null) {
				thislog.error("Unable to find code value: " + codeAdmin + " in domain "+domainUserRole);
				return;
			}
			
			roleAdmin.setCode(roleCodeAdmin);
			
			IVL<Date> roleValidDate = new IVL<Date>();
			roleValidDate.setLow(new Date());
			
			roleAdmin.setEffectiveTime(roleValidDate);
			
			employee.addEmployeeRole(roleAdmin);
			thislog.info("added role "+roleCodeAdmin.getDisplayName()+" with code "+roleCodeAdmin.getCode());
			
			if (codeDoctor != null && !codeDoctor.isEmpty() ) {
				//Doctor
				EmployeeRole roleMedico = new EmployeeRole();
				
				CodeValueRole roleCodeMedico = (CodeValueRole)voc.getCodeValueCsDomainCode("ROLES", domainUserRole, codeDoctor);
				
				if (roleCodeMedico == null) {
					thislog.error("Unable to find code value: "  + codeAdmin + "in domain "+domainUserRole);
					return;
				}
				
				roleMedico.setCode(roleCodeMedico);
				
				IVL<Date> medicoValidDate = new IVL<Date>();
				medicoValidDate.setLow(new Date());
				roleMedico.setEffectiveTime(medicoValidDate);
				
				employee.addEmployeeRole(roleMedico);
				thislog.info("added role "+roleCodeMedico.getDisplayName()+" with code "+roleCodeMedico.getCode());
			}
			
			
			thislog.info("persisting Employee...");
			em.persist(employee);
			
			em.flush();
			
			em.getTransaction().commit();
			
			em.close();
			
			thislog.info("user created");
		}
	}	
}