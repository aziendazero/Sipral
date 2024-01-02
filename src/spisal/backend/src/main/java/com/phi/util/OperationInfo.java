package com.phi.util;

import java.util.List;

import com.phi.entities.actions.ActionInterface;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

public class OperationInfo {

	private Object root;
	private String type;
	private List<Object> refreshData;
	private String property;
	private String code;
	private String additionalLink;
	private String unlink;
	private Object updateAddr;
	private Boolean updateUbi;
	private ActionInterface copyAteco;
	private Class<?> returnType;
	
	private Person person;
	private Physician physician;
	private Employee employee;
	private Sedi sede;
	private PersoneGiuridiche ditta;
	private Cantiere cantiere;
	
	public Object getRoot() {
		return root;
	}
	public void setRoot(Object root) {
		this.root = root;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Object> getRefreshData() {
		return refreshData;
	}
	public void setRefreshData(List<Object> refreshData) {
		this.refreshData = refreshData;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAdditionalLink() {
		return additionalLink;
	}
	public void setAdditionalLink(String additionalLink) {
		this.additionalLink = additionalLink;
	}
	public String getUnlink() {
		return unlink;
	}
	public void setUnlink(String unlink) {
		this.unlink = unlink;
	}
	public Object getUpdateAddr() {
		return updateAddr;
	}
	public void setUpdateAddr(Object updateAddr) {
		this.updateAddr = updateAddr;
		
	}
	public Boolean getUpdateUbi() {
		return updateUbi;
	}
	public void setUpdateUbi(Boolean updateUbi) {
		this.updateUbi = updateUbi;
	}
	public ActionInterface getCopyAteco() {
		return copyAteco;
	}
	public void setCopyAteco(ActionInterface copyAteco) {
		this.copyAteco = copyAteco;
	}
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public Physician getPhysician() {
		return physician;
	}
	public void setPhysician(Physician physician) {
		this.physician = physician;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Sedi getSede() {
		return sede;
	}
	public void setSede(Sedi sede) {
		this.sede = sede;
	}
	public PersoneGiuridiche getDitta() {
		return ditta;
	}
	public void setDitta(PersoneGiuridiche ditta) {
		this.ditta = ditta;
	}
	public Cantiere getCantiere() {
		return cantiere;
	}
	public void setCantiere(Cantiere cantiere) {
		this.cantiere = cantiere;
	}
}
