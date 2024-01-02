package com.phi.security.selectItem;

import javax.faces.model.SelectItem;

/**
 * Select item with additional data:
 * code: EmployeeRole.code.code
 * isCoordinator: EmployeeRole.isCoordinator
 * 
 * Used by AccessControlAction to store employee roles
 * 
 * @author alex.zupan
 */
public class EmployeeRoleSelectItem extends SelectItem {

	private static final long serialVersionUID = -3216161318363719014L;
	
	private String code;
	private String codeId;
	private Boolean isCoordinator;
	private String specializationCode;
	
	public EmployeeRoleSelectItem(Object value, String label, String code, String codeId, Boolean isCoordinator,String specializationCode) {
		super(value, label);
		this.code = code;
		this.codeId = codeId;
		this.isCoordinator = isCoordinator;
		this.specializationCode=specializationCode;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}
	
	public Boolean getIsCoordinator() {
		return isCoordinator;
	}
	
	public void setIsCoordinator(Boolean isCoordinator) {
		this.isCoordinator = isCoordinator;
	}

	public String getSpecializationCode() {
		return specializationCode;
	}

	public void setSpecializationCode(String specializationCode) {
		this.specializationCode = specializationCode;
	}

}