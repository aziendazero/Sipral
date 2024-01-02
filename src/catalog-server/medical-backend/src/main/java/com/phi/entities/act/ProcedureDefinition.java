package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

/**
 * Entity implementation class for Entity: ProcedureDefinition
 * 
 */
@Entity
@Table(name = "procedure_definition")
@Audited
public class ProcedureDefinition extends BaseEntity implements LocatedEntity {

	private static final long serialVersionUID = -1175719387966648507L;
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_ProcDef_s")
	@Index(name="IX_ProcDef_s") 
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation=serviceDeliveryLocation;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProcedureDefinition_sequence")
	@SequenceGenerator(name = "ProcedureDefinition_sequence", sequenceName = "ProcedureDefinition_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/*
	 * Contains a the effective procedure code, if contained in PHIDIC.
	 * (targetEntity CodeValuePhi). It's better to store the effective
	 * procedure code in codeIcd9, because the list of procedure codes
	 * should be contained into a separate CodeSystem, instead of PHIDIC. 
	 */
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_ProcDef_code")
	@Index(name="IX_ProcDef_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	/*
	 * classcode contains a 'categorization' of ProcedureDefinition.
	 * Each procedure can affect to a one common area, which is for example
	 * "macro procedure", "general", "indirect" or "cup"...
	 * phi_klink DSC: 
	 *   is used to distinguish between              CUSTOMER/PAC         /ICD9 codes
	 *   depending from classCode, respectly only one of code/regionalCode/codeIcd9 is used.
	 *   
	 * phi_klinic OMEGA:
	 *   classcode is always CUSTOMER.
	 *   codeIcd9 is used to store icd9 code selected by the final user
	 *   regionalCode is used to store 'Codice Provinciale'
	 */
	private CodeValue classCode;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="class_code")
	@ForeignKey(name="FK_ProcDef_cc")
	@Index(name="IX_ProcDef_cc") //FIXME: accorciato FK_Act_classCode
	public CodeValue getClassCode() {
		return classCode;
	}

	public void setClassCode(CodeValue classCode) {
		this.classCode = classCode;
	}
	
	
	/*
	 * contains the effective procedure code. It comes from a catalog provided
	 * by the customer, or is a national ICD9 procedure code. It's mapped by
	 * a code contained into CodeValueIc9 table, but it can be represented by
	 * codes of different CodeSystems (mapped by the same CodeValueIcd9 class).    
	 */
	private CodeValue codeIcd9;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
    @JoinColumn(name="code_icd9")
	@ForeignKey(name="FK_ProcDef_code_icd9")
	@Index(name="IX_ProcDef_code_icd9")
	public CodeValue getCodeIcd9() {
		return codeIcd9;
	}

	public void setCodeIcd9(CodeValue codeIcd9) {
		this.codeIcd9 = codeIcd9;
	}	
	
	/*
	 * Contains another ICD9 code, to map the same procedure in another catalog.
	 * Taking all the Procedure definition, looking to both codeIcd9 and regionalCodeIcd9
	 * you can identify the mapping between the two catalogs. 
	 */
	private CodeValue regionalCodeIcd9;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
    @JoinColumn(name="regionalCode_icd9")
	@ForeignKey(name="FK_ProcDef_regional_code_icd9")
	@Index(name="IX_ProcDef_regional_code_icd9")
	public CodeValue getRegionalCodeIcd9() {
		return regionalCodeIcd9;
	}

	public void setRegionalCodeIcd9(CodeValue regionalCodeIcd9) {
		this.regionalCodeIcd9 = regionalCodeIcd9;
	}	

	
	/**
	*  additional categorization needed by phi klinik
	*/
	private CodeValuePhi category;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="category")
	@ForeignKey(name="FK_ProcDef_category")
	@Index(name="IX_ProcDef_category")
	public CodeValuePhi getCategory(){
		return category;
	}

	public void setCategory(CodeValuePhi category){
		this.category = category;
	}

	/**
	*  additional sub-categorization needed by phi klinik
	*/
	private CodeValuePhi subCategory;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subCategory")
	@ForeignKey(name="FK_ProcDef_subCategory")
	@Index(name="IX_ProcDef_subCategory")
	public CodeValuePhi getSubCategory(){
		return subCategory;
	}

	public void setSubCategory(CodeValuePhi subCategory){
		this.subCategory = subCategory;
	}
	
	
	
	private String text;

	@Column(name="text_string")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	/**
	*  english translation of Text property. Used by final user
	*/
	private String textEn;

	@Column(name="text_en")
	public String getTextEn(){
		return textEn;
	}

	public void setTextEn(String textEn){
		this.textEn = textEn;
	}

	/**
	*  german translation of Text property. Used by final user
	*/
	private String textDe;

	@Column(name="text_de")
	public String getTextDe(){
		return textDe;
	}

	public void setTextDe(String textDe){
		this.textDe = textDe;
	}
	
	/**
	*  javadoc for daysValidity
	*/
	private Integer daysValidity;

	@Column(name="days_validity")
	public Integer getDaysValidity(){
		return daysValidity;
	}

	public void setDaysValidity(Integer daysValidity){
		this.daysValidity = daysValidity;
	}

	/**
	*  javadoc for daysGap
	*/
	private Integer daysGap;

	@Column(name="days_gap")
	public Integer getDaysGap(){
		return daysGap;
	}

	public void setDaysGap(Integer daysGap){
		this.daysGap = daysGap;
	}

	/**
	*  javadoc for frequency
	*/
	private Integer frequency;

	@Column(name="frequency")
	public Integer getFrequency(){
		return frequency;
	}

	public void setFrequency(Integer frequency){
		this.frequency = frequency;
	}
	/**
	*  javadoc for daysBeforeOperation
	*/
	private Integer daysBeforeOperation;

	@Column(name="days_before_operation")
	public Integer getDaysBeforeOperation(){
		return daysBeforeOperation;
	}

	public void setDaysBeforeOperation(Integer daysBeforeOperation){
		this.daysBeforeOperation = daysBeforeOperation;
	}

	
	/**
	*  procedure validuty (e.g. from 1/1/2010 to 31/12/2013): start of validity
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
	*  procedure validuty (e.g. from 1/1/2010 to 31/12/2013): end of validity
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
	*  javadoc for exclusive  // indica se la procedura può o no, avere un'altra procedura erogata lo stesso giorno
	*/
	private boolean exclusiveProcedure = false;

	@Column(name="exclusive_procedure")
	public boolean getExclusiveProcedure(){
		return exclusiveProcedure;
	}

	public void setExclusiveProcedure(boolean exclusiveProcedure){
		this.exclusiveProcedure = exclusiveProcedure;
	}
	
	private List<ServiceDeliveryLocation> procedureSDL;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="procedureDef_SDL", 
		joinColumns = { @JoinColumn(name="ProcedureDef_id") }, 
		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
	@ForeignKey(name="FK_SDL_procDef", inverseName="FK_ProcDef_sdl")
	public List<ServiceDeliveryLocation> getProcedureSDL() {
		return procedureSDL;
	}

	public void setProcedureSDL(List<ServiceDeliveryLocation> procedureSDL) {
		this.procedureSDL = procedureSDL;
	}

	public void addProcedureSDL(ServiceDeliveryLocation procedureSDL) {
		if (this.procedureSDL == null) {
			this.procedureSDL = new ArrayList<ServiceDeliveryLocation>();
		}
		// add the association
		if(!this.procedureSDL.contains(procedureSDL)) {
			this.procedureSDL.add(procedureSDL);
			//procedureSDL.addProcedureDefinition(this);
		}	
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
	
	private List<CodeValue> brancaCode; // WE NEED TO USE LIST OR CONVERTER DOESN'T WORK!!!

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
    @JoinTable(name="proceDef_branca_code", joinColumns = { @JoinColumn(name="ProcDef_id") }, inverseJoinColumns = { @JoinColumn(name="ProcDef_code") })
	@ForeignKey(name="FK_procDef_branca", inverseName="FK_branca_procDef")

//	@OrderBy("id")
	//@IndexColumn(name="listIndex") //Not JPA
	public List<CodeValue> getBrancaCode() {
		return brancaCode;
	}

	public void setBrancaCode(List<CodeValue> brancaCode) {
		this.brancaCode = brancaCode;
	}

	/**
	*  represent the type of vat applied: 10%, 21%, 22%, free vat..., 
	*/
	private CodeValuePhi vatCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="vatCode")
	@ForeignKey(name="FK_ProcDef_vatCode")
	@Index(name="IX_ProcDef_vatCode")
	public CodeValuePhi getVatCode(){
		return vatCode;
	}

	public void setVatCode(CodeValuePhi vatCode){
		this.vatCode = vatCode;
	}

	
	/**
	*  contains an integer number which represents the anatomic order of the body part where the procedure is applied.
	*  e.g: shoulder = 1, forarm = 2, arm = 3, hand = 4...
	*/
	private Integer anatomicOrder;

	@Column(name="anatomic_order")
	public Integer getAnatomicOrder(){
		return anatomicOrder;
	}

	public void setAnatomicOrder(Integer anatomicOrder){
		this.anatomicOrder = anatomicOrder;
	}

	//**used in PHI KLINIK**//
	
	@Transient
	public CodeValue getProcedureCode() {
		if (classCode== null )
			return null;
		
		String classcode = classCode.getCode();
		if ("PAC".equals(classcode)) {
			return regionalCodeIcd9;
		}
		if ("CUSTOMER".equals(classcode)) {
			return code;
		}
		if ("ICD9".equals(classcode)){
			return codeIcd9;
		}
		
		return null;
	}
	
	
	
	/**
	 * Following 2 properties are used in dentistry report
	 */
	private boolean current = false;

	@Transient
	public boolean getCurrent(){
		return current;
	}

	public void setCurrent(boolean current){
		this.current = current;
	}
	
	private Integer quantity;

	@Transient
	public Integer getQuantity(){
		return quantity;
	}

	public void setQuantity(Integer quantity){
		this.quantity = quantity;
	}
}