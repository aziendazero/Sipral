package com.phi.entities.dataTypes;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.annotations.CodeValueExtension;

/**
 * Represent a φ parameter
 * 
 * Has 4 values:
 *  - value
 *  - visible
 *  - readOnly
 *  - required
 *  
 * And 5 conditions:
 *  - location
 *  - username
 *  - enabledRole
 *  - disabledRole
 *  - host
 *  
 * @author Alex
 */

@javax.persistence.Entity
@Table(name = "code_value_parameter", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_parameter", indexes = { @Index(name="IX_Code_Value_Parameter", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
//@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@Audited
public class CodeValueParameter extends CodeValue implements java.io.Serializable {

	private static final long serialVersionUID = 1569936281L;
	
	/**************************************VALUES****************************************/

	/**
	*  javadoc for value
	*/
	private String value;

	@Column(name="value")
	@CodeValueExtension(name="Value")
	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	/**
	*  javadoc for dataType
	*/
	private String dataType;

	@Column(name="data_type")
	@CodeValueExtension(name="DataType")
	public String getDataType(){
		return dataType;
	}

	public void setDataType(String dataType){
		this.dataType = dataType;
	}
	
	/**
	*  javadoc for currentValue
	*  if datatype equals date and currentValue is true evaluated value will be current date
	*  if datatype equals employee and currentValue is true evaluated value will be logged user
	*/
	private Boolean currentValue;

	@Column(name="current_value")
	@CodeValueExtension(name="currentValue")
	public Boolean getCurrentValue(){
		return currentValue;
	}

	public void setCurrentValue(Boolean currentValue){
		this.currentValue = currentValue;
	}
	
	/**
	*  javadoc for visible
	*/
	private Boolean visible;

	@Column(name="visible")
	@CodeValueExtension(name="Visible")
	public Boolean getVisible(){
		return visible;
	}

	public void setVisible(Boolean visible){
		this.visible = visible;
	}
	
	/**
	*  javadoc for readOnly
	*/
	private Boolean readOnly;

	@Column(name="readOnly")
	@CodeValueExtension(name="ReadOnly")
	public Boolean getReadOnly(){
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly){
		this.readOnly = readOnly;
	}
	
	/**
	*  javadoc for required
	*/
	private Boolean required;

	@Column(name="required")
	@CodeValueExtension(name="Required")
	public Boolean getRequired(){
		return required;
	}

	public void setRequired(Boolean required){
		this.required = required;
	}
	
	/**************************************CONDITIONS****************************************/

	/**
	*  javadoc for location
	*/
	private String location;

	@Column(name="location")
	@CodeValueExtension(name="Location")
	public String getLocation(){
		return location;
	}

	public void setLocation(String location){
		this.location = location;
	}
	
//	/**
//	*  javadoc for customer
//	*/
//	private String customer;
//
//	@Column(name="customer")
//	@CodeValueExtension(name="Customer")
//	public String getCustomer(){
//		return customer;
//	}
//
//	public void setCustomer(String customer){
//		this.customer = customer;
//	}

	/**
	*  javadoc for username
	*/
	private String username;

	@Column(name="username")
	@CodeValueExtension(name="Username")
	public String getUsername(){
		return username;
	}

	public void setUsername(String username){
		this.username = username;
	}

	/**
	*  javadoc for enabledRole
	*/
//	private CodeValueRole enabledRole;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="enabledRole")
//	@ForeignKey(name="FK_CodeValueParam_enabledRole")
//	@Index(name="IX_CodeValueParam_enabledRole")
//	public CodeValueRole getEnabledRole(){
//		return enabledRole;
//	}
//
//	public void setEnabledRole(CodeValueRole enabledRole){
//		this.enabledRole = enabledRole;
//	}
	
	/**
	*  javadoc for disabledRole
	*/
	private CodeValueRole role;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="param_role")
	@ForeignKey(name="FK_CodeValueParam_Role")
	@Index(name="IX_CodeValueParam_Role")
	public CodeValueRole getRole(){
		return role;
	}

	public void setRole(CodeValueRole role){
		this.role = role;
	}

	/**
	*  javadoc for host
	*/
	private String host;

	@Column(name="host")
	@CodeValueExtension(name="Host")
	public String getHost(){
		return host;
	}

	public void setHost(String host){
		this.host = host;
	}
	
	/**************************************Code value overrides****************************************/
	
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueParameter.class)
	@JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_code_value_parameter_Parent")
	@Index(name="IX_code_value_parameter_Parent")
	public CodeValue getParent() {
		return parent;
	}
	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}
	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueParameter.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}
	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}

}
