package com.phi.entities.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueExemption;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.II4Exemption;
import com.phi.entities.role.Patient;

@javax.persistence.Entity
@Table(name = "exemption")
@Audited
public class Exemption extends BaseEntity {

	private static final long serialVersionUID = 490836443L;


	/**
	 *  javadoc for patient
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Exemption_patient")
	@Index(name="IX_Exemption_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	private Set<II> id;

	//FIXME
	@OneToMany(targetEntity=II4Exemption.class ,fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="exemption_id")
	@ForeignKey(name="FK_Exemption_id")
	@Index(name="IX_Exemption_id")
	@AuditJoinTable(name="z_exemption_id_jt")
	public Set<II> getId() {
		return id;
	}

	public void setId(Set<II> id) {
		this.id = id;
	}

	public II getId(String root) {
		if (getId() != null) {
			for (II ii : getId()) {
				if (ii.getRoot().toString().equals(root))
					return ii;
			}
		}
		return null;
	}

	public void addId(II id) {
		if (id == null)
			return;

		if (this.id == null) {
			this.id = new HashSet<II>();
		}

		this.id.add(id);
	}



	/**
	 *  javadoc for suspensionDate
	 */
	private Date suspensionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="suspension_date")
	public Date getSuspensionDate(){
		return suspensionDate;
	}

	public void setSuspensionDate(Date suspensionDate){
		this.suspensionDate = suspensionDate;
	}

	/**
	 *  javadoc for endValidity
	 */
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	 *  javadoc for startValidity
	 */
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}

	/**
	 *  javadoc for diagnosis
	 */
	private List<CodeValue> diagnosis;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
	@JoinTable(name="Exemption_diagnosis", joinColumns = { @JoinColumn(name="Exemption_id") }, inverseJoinColumns = { @JoinColumn(name="diagnosis") })
	@ForeignKey(name="FK_diagnosis_Exemption", inverseName="FK_Exemption_diagnosis")
	public List<CodeValue> getDiagnosis(){
		return diagnosis;
	}

	public void setDiagnosis(List<CodeValue> diagnosis){
		this.diagnosis = diagnosis;
	}

	/**
	 *  javadoc for diagnosi
	 */
	private CodeValue diagnosi;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
	@JoinColumn(name="diagnosi")
	@ForeignKey(name="FK_Exemption_diagnosi")
	@Index(name="IX_Exemption_diagnosi")
	public CodeValue getDiagnosi(){
		return diagnosi;
	}

	public void setDiagnosi(CodeValue diagnosi){
		this.diagnosi = diagnosi;
	}

	/**
	 *  javadoc for code
	 */
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueExemption.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Exemption_code")
	@Index(name="IX_Exemption_code")
	public CodeValue getCode(){
		return code;
	}

	public void setCode(CodeValue code){
		this.code = code;
	}

	@Override
	@Id
	@Column(name = "internal_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Exemption_sequence")
	@SequenceGenerator(name = "Exemption_sequence", sequenceName = "Exemption_sequence")
	public long getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Exemption_sdloc")
	@Index(name="IX_Exemption_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation param) {
		this.serviceDeliveryLocation = param;
	}*/

	// CUSTOM PROPERTIES	

	/**
	 *  externalId used by integrations
	 *  store here id of an external system
	 */
	private String externalId;

	@Column(name="external_id", unique=true)
	public String getExternalId(){
		return externalId;
	}

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}
	
	// END CUSTOM PROPERTIES
	
	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="status_code")
	@ForeignKey(name="FK_Exemption_sc")
	@Index(name="IX_Exemption_sc")
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}
}