package com.phi.entities.act;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.AbstractDiagnosis;
import com.phi.entities.baseEntity.Report;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

/**
 * Entity implementation class for Entity: Diagnosis.
 *
 */
@Entity
@Table(name = "diagnosis")
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Diagnosis extends AbstractDiagnosis implements LocatedEntity {

	private static final long serialVersionUID = 2905784072381138535L;
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Diagnosis_sdloc")
	@Index(name = "IX_Diagnosis_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}


//	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_Diagnosis_Pat_Enc")
	@Index(name="IX_Diagnosis_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}

	/**
	*  javadoc for score
	*/
	private Integer score;

	@Column(name="score")
	public Integer getScore(){
		return score;
	}

	public void setScore(Integer score){
		this.score = score;
	}

	
	/**
	 * javadoc for mainDiagnosis
	 */
	private boolean mainDiagnosis = false;

	@Column(name = "main_diagnosis")
	public boolean getMainDiagnosis() {
		return mainDiagnosis;
	}

	public void setMainDiagnosis(boolean mainDiagnosis) {
		this.mainDiagnosis = mainDiagnosis;
	}
	
	/**
	 * javadoc Report
	 */
	private Report report;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="report")
	@ForeignKey(name="FK_Diag_Report")
	@Index(name="IX_Diag_Report")
	public Report getReport() {
	    return report;
	}

	public void setReport(Report report) {
	    this.report = report;
	}
		
	/**
	*  javadoc for text
	*/
	private String text;
	
	@Column(name="text_string", length=2500)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	*  javadoc for code
	*/
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Diagnosis_code")
	@Index(name="IX_Diagnosis_code")
	public CodeValue getCode(){
		return code;
	}

	public void setCode(CodeValue code){
		this.code = code;
	}
	
	/**
	*  javadoc for codeIcd9
	*/
	private CodeValueIcd9 codeIcd9;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code_icd9")
	@ForeignKey(name="FK_Diagnosis_code_icd9")
	@Index(name="IX_Diagnosis_code_icd9")
	public CodeValueIcd9 getCodeIcd9(){
		return codeIcd9;
	}

	public void setCodeIcd9(CodeValueIcd9 codeIcd9){
		this.codeIcd9 = codeIcd9;
	}
	
	
	/**
	 *  javadoc for description
	 */
	private String description;

	@Column(name="description",length=2500)
	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	
	
	/**
	*  body part reference for the Diagnosis
	*/
	private CodeValuePhi place;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="place")
	@ForeignKey(name="FK_Diagnosis_place")
	@Index(name="IX_Diagnosis_place")
	public CodeValuePhi getPlace(){
		return place;
	}

	public void setPlace(CodeValuePhi place){
		this.place = place;
	}

	/**
	*  body part reference for the Diagnosis
	*/
	private CodeValuePhi stadCond;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Stad_Cond")
	@ForeignKey(name="FK_Diagnosis_StadCond")
	@Index(name="IX_Diagnosis_StadCond")
	public CodeValuePhi getStadCond(){
		return stadCond;
	}

	public void setStadCond(CodeValuePhi stadCond){
		this.stadCond = stadCond;
	}
	
	/**
	*  start of Diagnosis occurrence 
	*/
	private Date endEvent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_event")
	public Date getEndEvent(){
		return endEvent;
	}

	public void setEndEvent(Date end){
		this.endEvent = end;
	}

	/**
	*  end of Diagnosis occurrence
	*/
	private Date startEvent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_event")
	public Date getStartEvent(){
		return startEvent;
	}

	public void setStartEvent(Date start){
		this.startEvent = start;
	}

	/**
	* importance 
	* for phi_klinik a number between 0 and 999
	*/
	private Integer importance;

	@Column(name="importance")
	public Integer getImportance(){
		return importance;
	}

	public void setImportance(Integer importance){
		this.importance = importance;
	}
	
	
	private CodeValuePhi severity;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="severity")
	@ForeignKey(name="FK_Diagnosis_severity")
	@Index(name="IX_Diagnosis_severity")
	public CodeValuePhi getSeverity(){
		return severity;
	}

	public void setSeverity(CodeValuePhi severity){
		this.severity = severity;
	}
	
	
	private Boolean alreadyPresentOnAdmission = false;

	@Column(name = "already_present")
	public Boolean getAlreadyPresentOnAdmission() {
		return alreadyPresentOnAdmission;
	}

	public void setAlreadyPresentOnAdmission(Boolean alreadyPresentOnAdmission) {
		this.alreadyPresentOnAdmission = alreadyPresentOnAdmission;
	}

}