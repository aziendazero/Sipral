package com.phi.entities.act;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.Report;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "discharge_data")
@Audited
public class DischargeData extends Report{

	private static final long serialVersionUID = 1800462700L;

	/**
	*  javadoc for dischargeResponsible
	*/
	private Employee dischargeResponsible;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="discharge_responsible_id")
	@ForeignKey(name="FK_DschrgeDt_dschrgRsponsbl")
	@Index(name="IX_DschrgeDt_dschrgRsponsbl")
	public Employee getDischargeResponsible(){
		return dischargeResponsible;
	}

	public void setDischargeResponsible(Employee dischargeResponsible){
		this.dischargeResponsible = dischargeResponsible;
	}


	private CodeValuePhi approval;
	private CodeValuePhi complexity;
	private Date dischargeDate;
	private String dischargeNote;

	private CodeValuePhi dischargeDateStatus;
	private Boolean optionTPN;
	private String warnings;
	private List<CodeValuePhi> dischargeServices;

	/**
	 *  javadoc for approval
	 */

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="approval")
	@ForeignKey(name="FK_Dischargedata_approval")
	@Index(name="IX_Dischargedata_approval")
	public CodeValuePhi getApproval(){
		return approval;
	}

	public void setApproval(CodeValuePhi approval){
		this.approval = approval;
	}

	/**
	 *  javadoc for dischargeDate
	 */

	@Column(name="discharge_date")
	public Date getDischargeDate(){
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate){
		this.dischargeDate = dischargeDate;
	}


	
	/**
	 *  javadoc for dischargeDateStatus
	 */

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="dischargeDateStatus")
	@ForeignKey(name="FK_DiscData_disDatState")
	@Index(name="IX_DiscData_disDatState")
	public CodeValuePhi getDischargeDateStatus(){
		return dischargeDateStatus;
	}

	public void setDischargeDateStatus(CodeValuePhi dischargeDateStatus){
		this.dischargeDateStatus = dischargeDateStatus;
	}

	/**
	 *  javadoc for complexity
	 */

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complexity")
	@ForeignKey(name="FK_DischargeData_complexity")
	@Index(name="IX_DischargeData_complexity")
	public CodeValuePhi getComplexity(){
		return complexity;
	}

	public void setComplexity(CodeValuePhi complexity){
		this.complexity = complexity;
	}

	/**
	 *  javadoc for optionTPN
	 */
	@Column(name="optionTPN")
	public Boolean getOptionTPN() {
		return optionTPN;
	}
	public void setOptionTPN(Boolean optionTPN) {
		this.optionTPN = optionTPN;
	}

//	
//	/**
//	 *  javadoc for dischargeResponsible
//	 */
//
//
//	@Column(name="discharge_responsible")
//	public String getsponsible(){
//		return dischargeResponsible;
//	}
//
//	public void setDischargeResponsible(String dischargeResponsible){
//		this.dischargeResponsible = dischargeResponsible;
//	}

	/**
	 *  javadoc for dischargeNote
	 */
	@Column(name="discharge_note", length = 2500)
	 public String getDischargeNote(){
		 return dischargeNote;
	 }

	 public void setDischargeNote(String dischargeNote){
		 this.dischargeNote = dischargeNote;
	 }

	 /**
	  *  javadoc for warnings
	  */


	 @Column(name="warnings")
	 public String getWarnings(){
		 return warnings;
	 }

	 public void setWarnings(String warnings){
		 this.warnings = warnings;
	 }


	 @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	 @JoinTable(name = "DischargeData_dischargeS", joinColumns = { @JoinColumn(name = "DischargeData_id") }, inverseJoinColumns = { @JoinColumn(name = "dischargeServices") })
	 @ForeignKey(name = "FK_dischargeS_DischargeD", inverseName = "FK_DischargeD_dischargeS")
	 public List<CodeValuePhi> getDischargeServices(){
		 return dischargeServices;
	 }

	 public void setDischargeServices(List<CodeValuePhi> dischargeServices){
		 this.dischargeServices = dischargeServices;
	 }

	 private String physicianText;
	 private String nurseText;
	 private String socialAssistantText;
	 private String palliativeCareText;
	 private String nutrDietService;
	 private String otherText;

	@Column(name="otherText")  
	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}

	@Column(name="physicianText") 
	public String getPhysicianText() {
		return physicianText;
	}

	public void setPhysicianText(String physicianText) {
		this.physicianText = physicianText;
	}

	@Column(name="nurseText")
	public String getNurseText() {
		return nurseText;
	}

	public void setNurseText(String nurseText) {
		this.nurseText = nurseText;
	}

	@Column(name="socialAssistantText")
	public String getSocialAssistantText() {
		return socialAssistantText;
	}

	public void setSocialAssistantText(String socialAssistantText) {
		this.socialAssistantText = socialAssistantText;
	}

	@Column(name="palliativeCareText")
	public String getPalliativeCareText() {
		return palliativeCareText;
	}

	public void setPalliativeCareText(String palliativeCareText) {
		this.palliativeCareText = palliativeCareText;
	}

	@Column(name="nutrDietService")
	public String getNutrDietService() {
		return nutrDietService;
	}

	public void setNutrDietService(String nutrDietService) {
		this.nutrDietService = nutrDietService;
	}

}
