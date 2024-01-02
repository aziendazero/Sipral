package com.phi.entities.act;

import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.PQ;

@Entity
@Audited
public abstract class AbstractProcedure extends ClinicalProcedure {

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3034382775652565789L;
	
	/**
	*  javadoc for charge
	*/
	private CodeValuePhi charge;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="charge")
	@ForeignKey(name="FK_ExtendedProcedure_charge")
	@Index(name="IX_ExtendedProcedure_charge")
	public CodeValuePhi getCharge(){
		return charge;
	}

	public void setCharge(CodeValuePhi charge){
		this.charge = charge;
	}
	
	/**
	*  javadoc for quantity
	*/
	private Integer quantity;

	@Column(name="quantity")
	public Integer getQuantity(){
		return quantity;
	}

	public void setQuantity(Integer quantity){
		this.quantity = quantity;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_AbsProc_code")
	@Index(name="IX_AbsProc_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private CodeValue codeIcd9;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
    @JoinColumn(name="code_icd9")
	@ForeignKey(name="FK_AbsProc_code_icd9")
	@Index(name="IX_AbsProc_code_icd9")
	public CodeValue getCodeIcd9() {
		return codeIcd9;
	}

	public void setCodeIcd9(CodeValue codeIcd9) {
		this.codeIcd9 = codeIcd9;
	}	
	
	private CodeValue classCode;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="class_code")
	@ForeignKey(name="FK_AbsProc_cc")
	@Index(name="IX_AbsProc_cc") 
	public CodeValue getClassCode() {
		return classCode;
	}

	public void setClassCode(CodeValue classCode) {
		this.classCode = classCode;
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
	
	
	private String text;

	@Column(name="text_string")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	private CodeValue levelCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="level_code")
	@ForeignKey(name="FK_Proc_lec")
	@Index(name="IX_Proc_lec") 
	public CodeValue getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(CodeValue levelCode) {
		this.levelCode = levelCode;
	}
	
	
	private Set<CodeValue> methodCode;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinTable(name="procedure_method_code", 
    	joinColumns = { @JoinColumn(name="procedure_id", nullable=false, updatable=false) }, 
        inverseJoinColumns = { @JoinColumn(name="method_code", nullable=false, updatable=false) })
	@ForeignKey(name="FK_methodCode_procedure", inverseName="FK_methodCode_cv")
	public Set<CodeValue> getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(Set<CodeValue> methodCode) {
		this.methodCode = methodCode;
	}

	private Set<CodeValue> approachSiteCode;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinTable(name="procedure_approach_code", 
    	joinColumns = { @JoinColumn(name="procedure_id", nullable=false, updatable=false) }, 
        inverseJoinColumns = { @JoinColumn(name="approach_site_code", nullable=false, updatable=false) })
	@ForeignKey(name="FK_approachSiteCode_procedure", inverseName="FK_approachSiteCode_cv")
	public Set<CodeValue> getApproachSiteCode() {
		return approachSiteCode;
	}

	public void setApproachSiteCode(Set<CodeValue> approachSiteCode) {
		this.approachSiteCode = approachSiteCode;
	}


	private CodeValue regionalCodeIcd9;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
    @JoinColumn(name="regionalCode_icd9")
	@ForeignKey(name="FK_Proc_regional_code_icd9")
	@Index(name="IX_Proc_regional_code_icd9")
	public CodeValue getRegionalCodeIcd9() {
		return regionalCodeIcd9;
	}

	public void setRegionalCodeIcd9(CodeValue regionalCodeIcd9) {
		this.regionalCodeIcd9 = regionalCodeIcd9;
	}
	
	private PQ hospitalPrice;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="hospital_price_value")),
	       @AttributeOverride(name="unit", column=@Column(name="hospital_price_unit"))
	})
	public PQ getHospitalPrice() {
		return hospitalPrice;
	}

	public void setHospitalPrice(PQ hospitalPrice) {
		this.hospitalPrice = hospitalPrice;
	}	
	
	private PQ fullPrice;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="full_price_value")),
	       @AttributeOverride(name="unit", column=@Column(name="full_price_unit"))
	})
	public PQ getFullPrice() {
		return fullPrice;
	}

	public void setFullPrice(PQ fullPrice) {
		this.fullPrice = fullPrice;
	}
	
	
	private PQ regionPrice;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="region_price_value")),
	       @AttributeOverride(name="unit", column=@Column(name="region_price_unit"))
	})
	public PQ getRegionPrice() {
		return regionPrice;
	}

	public void setRegionPrice(PQ regionPrice) {
		this.regionPrice = regionPrice;
	}
	
	
	private Set<CodeValue> targetSiteCode;

//	private Document document;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinTable(name="procedure_target_site_code", 
    	joinColumns = { @JoinColumn(name="procedure_id", nullable=false, updatable=false) }, 
        inverseJoinColumns = { @JoinColumn(name="target_site_code", nullable=false, updatable=false) })
	@ForeignKey(name="FK_targetSiteCode_procedure", inverseName="FK_targetSiteCode_cv")
	public Set<CodeValue> getTargetSiteCode() {
		return targetSiteCode;
	}

	public void setTargetSiteCode(Set<CodeValue> targetSiteCode) {
		this.targetSiteCode = targetSiteCode;
	}

	

	

//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="document")
//	@ForeignKey(name="FK_AbsProc_Document")
//	@Index(name="IX_AbsProc_Document")
//	public Document getDocument() {
//	    return document;
//	}
//
//	public void setDocument(Document param) {
//	    this.document = param;
//	}

	private ProcedureDefinition procedureDefinition;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procedure_definition")
	@ForeignKey(name="FK_AbsProc_Proc_Def")
	@Index(name="IX_AbsProc_Proc_Def")
	public ProcedureDefinition getProcedureDefinition() {
	    return procedureDefinition;
	}

	public void setProcedureDefinition(ProcedureDefinition param) {
	    this.procedureDefinition = param;
	}
	
	/**
	 * javadoc for mainProcedure
	 */
	private Boolean mainProcedure = false;

	@Column(name = "main_procedure")
	public Boolean getMainProcedure() {
		return mainProcedure;
	}

	public void setMainProcedure(Boolean mainProcedure) {
		this.mainProcedure = mainProcedure;
	}
	
	/**
	 * javadoc start date HL7 ORC-7.4
	 */	
	private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * javadoc priority HL7 ORC-7.6
	 */	
	private CodeValue priority;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="priority")
	@ForeignKey(name="FK_AbsProc_Priority")
	@Index(name="IX_AbsProc_Priority")
	public CodeValue getPriority() {
		return priority;
	}

	public void setPriority(CodeValue priority) {
		this.priority = priority;
	}	
	
	/**
	 * javadoc placerField1 HL7 OBR-18
	 */		
	private String placerField1;
	
	@Column(name = "placer_field1")
	public String getPlacerField1() {
		return placerField1;
	}
	
	public void setPlacerField1 (String placerField1) {
		this.placerField1 = placerField1;
	}		
	
	/**
	 * javadoc filler HL7 OBR-20
	 */		
	private String fillerField1;
	
	@Column(name = "filler_field1")
	public String getFillerField1() {
		return fillerField1;
	}
	
	public void setFillerField1 (String fillerField1) {
		this.fillerField1 = fillerField1;
	}
	
	/**
	 * javadoc placerGroupNumber HL7 ORC-4
	 */
	private String placerGroupNumber;

	@Column(name = "placer_group_number")
	public String getPlacerGroupNumber() {
		return placerGroupNumber;
	}

	public void setPlacerGroupNumber (String placerGroupNumber) { this.placerGroupNumber = placerGroupNumber; }

	/**
	 * javadoc placerField2 HL7 OBR-19
	 */
	private String placerField2;

	@Column(name = "placer_field2")
	public String getPlacerField2() {
		return placerField2;
	}

	public void setPlacerField2 (String placerField2) {
		this.placerField2 = placerField2;
	}

	/**
	 * javadoc resultHandling HL7 OBR-45
	 */
	private CodeValuePhi resultHandling;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="result_handling")
	@ForeignKey(name="FK_AbsProc_ResultHandling")
	@Index(name="IX_AbsProc_ResultHandling")
	public CodeValuePhi getResultHandling() { return resultHandling; }

	public void setResultHandling(CodeValuePhi resultHandling) { this.resultHandling = resultHandling; }
}