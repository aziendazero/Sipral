package com.phi.entities.baseEntity;

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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCodifa;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;


@Entity
@Table(name = "medicine")
@Audited

public class Medicine extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8374538744057389057L;
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "medicine_sequence")
	@SequenceGenerator(name = "medicine_sequence", sequenceName = "medicine_sequence")
	@Column(name = "internal_id")
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
	@ForeignKey(name = "FK_medicine_sdloc")
	@Index(name="IX_medicine_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	private Manufacturer manufacturer;

	private List<Substance> substance;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="manufacturer")
	@ForeignKey(name="FK_Medicine_Manufacturer")
	@Index(name="IX_Medicine_Manufacturer")
	public Manufacturer getManufacturer() {
	    return manufacturer;
	}

	public void setManufacturer(Manufacturer param) {
	    this.manufacturer = param;
	}

	@ManyToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinTable(name="medicine_substance", 
		joinColumns = { @JoinColumn(name="medicine_id") }, 
		inverseJoinColumns = { @JoinColumn(name="substance_id") })
	@ForeignKey(name="FK_Substance_Medicine", inverseName="FK_Medicine_Substance")
	@IndexColumn(name="Medicine_Subst_Index")
	public List<Substance> getSubstance() {
	    return substance;
	}

	public void setSubstance(List<Substance> param) {
	    this.substance = param;
	}

	public void addSubstance(Substance substance) {
		if (this.substance == null) {
			this.substance = new ArrayList<Substance>();
		}
		// add the association
		if(!this.substance.contains(substance)) {
			this.substance.add(substance);
		}
	}
	
	private CodeValue ssnCode;
	private CodeValue terapeuticGroup;
	private CodeValue pharmaceuticFormCode;
	private CodeValue productClassification;
	private CodeValue temperatureStorageCode;
	private CodeValue expiringCode;
	private CodeValue recipeCode;
	private CodeValue box;
	private CodeValue atcCode;
	private CodeValue usageType; // chiuso, esaurimento, normale
	
	private String dosageInstruction; //posologia
	private double price=0f;
	private String quantityPerBox;
	private Date freezeDate;
	private IVL<Date> concessionPeriod;
	private boolean reference; //prontuario

	private CodeValue freezeCauseCode;

	private CodeValue routeCode;

	private CodeValue eqGroupCode;
	
	private String codifaId;

	private String governmentId;

	private String regionalId;

	private String goodsClassId;

	private String externalId;
	
	private Double tempMax;
	private Double tempMin;
	private String lifePeriod;
		
	@Column(name="temp_max")
	public Double getTempMax() {
		return tempMax;
	}

	public void setTempMax(Double tempMax) {
		this.tempMax = tempMax;
	}

	@Column(name="temp_min")
	public Double getTempMin() {
		return tempMin;
	}

	public void setTempMin(Double tempMin) {
		this.tempMin = tempMin;
	}
	
	@Column(name="life_period")
	public String getLifePeriod() {
		return lifePeriod;
	}

	public void setLifePeriod(String lifePeriod) {
		this.lifePeriod = lifePeriod;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="ssnCode")
	@ForeignKey(name="FK_Medicine_ssn")
	@Index(name="IX_Medicine_ssn")
	public CodeValue getSsnCode() {
		return ssnCode;
	}

	public void setSsnCode(CodeValue ssnCode) {
		this.ssnCode = ssnCode;
	}
	
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="terapeutic_group")
	@ForeignKey(name="FK_Medicine_terapGrp")
	@Index(name="IX_Medicine_terapGrp")
	public CodeValue getTerapeuticGroup() {
		return terapeuticGroup;
	}

	public void setTerapeuticGroup(CodeValue terapeuticGroup) {
		this.terapeuticGroup = terapeuticGroup;
	}
	
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="pharm_form")
	@ForeignKey(name="FK_Medicine_form")
	@Index(name="IX_Medicine_form") 
	public CodeValue getPharmaceuticFormCode() {
		return pharmaceuticFormCode;
	}

	public void setPharmaceuticFormCode(CodeValue formCode) {
		this.pharmaceuticFormCode = formCode;
	}
	
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="product_class")
	@ForeignKey(name="FK_Medicine_prodClass")
	@Index(name="IX_Medicine_prodClass")
	public CodeValue getProductClassification() {
		return productClassification;
	}

	public void setProductClassification(CodeValue productClassification) {
		this.productClassification = productClassification;
	}


	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="temp_storage")
	@ForeignKey(name="FK_Medicine_tempStor")
	@Index(name="IX_Medicine_tempStor")
	public CodeValue getTemperatureStorageCode() {
		return temperatureStorageCode;
	}

	public void setTemperatureStorageCode(CodeValue temperatureStorageCode) {
		this.temperatureStorageCode = temperatureStorageCode;
	}
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="expiring_code")
	@ForeignKey(name="FK_Medicine_expire")
	@Index(name="IX_Medicine_expire")
	public CodeValue getExpiringCode() {
		return expiringCode;
	}

	public void setExpiringCode(CodeValue expiringCode) {
		this.expiringCode = expiringCode;
	}

	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="recipe_code")
	@ForeignKey(name="FK_Medicine_recipe")
	@Index(name="IX_Medicine_recipe")
	public CodeValue getRecipeCode() {
		return recipeCode;
	}

	public void setRecipeCode(CodeValue recipeCode) {
		this.recipeCode = recipeCode;
	}

	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="box")
	@ForeignKey(name="FK_Medicine_box")
	@Index(name="IX_Medicine_box")
	public CodeValue getBox() {
		return box;
	}

	public void setBox(CodeValue box) {
		this.box = box;
	}

	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="atc_Code")
	@ForeignKey(name="FK_Medicine_atcCode")
	@Index(name="IX_Medicine_atcCode")
	public CodeValue getAtcCode() {
		return atcCode;
	}

	public void setAtcCode(CodeValue atcCode) {
		this.atcCode = atcCode;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="freeze_CauseCode")
	@ForeignKey(name="FK_Medicine_freezeCauseCode")
	@Index(name="IX_Medicine_freezeCauseCode")
	public CodeValue getFreezeCauseCode() {
		return freezeCauseCode;
	}

	public void setFreezeCauseCode(CodeValue freezeCauseCode) {
		this.freezeCauseCode = freezeCauseCode;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="route_Code")
	@ForeignKey(name="FK_Medicine_routeCode")
	@Index(name="IX_Medicine_routeCode")
	public CodeValue getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(CodeValue routeCode) {
		this.routeCode = routeCode;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="eq_group")
	@ForeignKey(name="FK_Medicine_eq_group")
	@Index(name="IX_Medicine_eq_group")
	public CodeValue getEqGroupCode() {
		return eqGroupCode;
	}

	public void setEqGroupCode(CodeValue eqGroupCode) {
		this.eqGroupCode = eqGroupCode;
	}
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueCodifa.class)
    @JoinColumn(name="usageType")
	@ForeignKey(name="FK_Medicine_usageType")
	@Index(name="IX_Medicine_usageType")
	public CodeValue getUsageType() {
		return usageType;
	}

	public void setUsageType(CodeValue usageType) {
		this.usageType = usageType;
	}
	
	//@JsonIgnore //Wrong encoding, fix content!
	@Lob
	@Column(name="dosage_instruction")
	public String getDosageInstruction() {
		return dosageInstruction;
	}

	public void setDosageInstruction(String dosage) {
		this.dosageInstruction = dosage;
	}
	
	@Column(name="price")
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Column(name="Quantity_per_box")
	public String getQuantityPerBox() {
		return quantityPerBox;
	}

	public void setQuantityPerBox(String quantityPerBox) {
		this.quantityPerBox = quantityPerBox;
	}

	/**
	* Units of measure from CODIFA:UnitaMisuraQuantita
	*/
	private CodeValueCodifa quantityPerBoxUnit;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="quantity_per_box_unit")
	@ForeignKey(name="FK_Medicine_quantityPerBoxUnit")
	@Index(name="IX_Medicine_quantityPerBoxUnit")
	public CodeValueCodifa getQuantityPerBoxUnit(){
		return quantityPerBoxUnit;
	}

	public void setQuantityPerBoxUnit(CodeValueCodifa quantityPerBoxUnit){
		this.quantityPerBoxUnit = quantityPerBoxUnit;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="freeze_date")
	public Date getFreezeDate() {
		return freezeDate;
	}

	public void setFreezeDate(Date freezeDate) {
		this.freezeDate = freezeDate;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="concession_Period_low")),
		@AttributeOverride(name="high", column=@Column(name="concession_Period_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="concession_Period_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="concession_Period_highClosed"))
	})
	public IVL<Date> getConcessionPeriod() {
		return concessionPeriod;
	}

	public void setConcessionPeriod(IVL<Date> concessionPeriod) {
		this.concessionPeriod = concessionPeriod;
	}

	
	@Column(name="reference")
	public boolean getReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}
	
	@Column(name="codifa_id")
	@Index(name="IX_medicine_codifa_id")
	public String getCodifaId() {
		return codifaId;
	}

	public void setCodifaId(String codifaId) {
		this.codifaId = codifaId;
	}
	
	@Column(name="government_id")
	@Index(name="IX_medicine_gov_id")
	public String getGovernmentId() {
		return governmentId;
	}

	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}
	
	@Column(name="regional_id")
	@Index(name="IX_medicine_regional_id")
	public String getRegionalId() {
		return regionalId;
	}

	public void setRegionalId(String regionalId) {
		this.regionalId = regionalId;
	}
	
	@Column(name="goods_class_id")
	@Index(name="IX_medicine_goods_class")
	public String getGoodsClassId() {
		return goodsClassId;
	}

	public void setGoodsClassId(String goodsClassId) {
		this.goodsClassId = goodsClassId;
	}
	
	@Column(name="external_id")
	@Index(name="IX_medicine_ext_id")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	private EN name;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="fam", column=@Column(name="name_fam")),
	       @AttributeOverride(name="giv", column=@Column(name="name_giv")),
	       @AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
	       @AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
	       @AttributeOverride(name="del", column=@Column(name="name_del")),
	       @AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
	})
	public EN getName() {
		return  name;
	}

	public void setName(EN name) {
		this.name = name;
	}
	
	/**
	*  javadoc for aifaNote
	*/
	private List<CodeValuePhi> aifaNote;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="medicine_aifaNote", joinColumns = { @JoinColumn(name="medicine_id") }, inverseJoinColumns = { @JoinColumn(name="aifaNote") })
	@ForeignKey(name="FK_aifaNote_medicine", inverseName="FK_medicine_aifaNote")
	//@IndexColumn(name="list_index")
	public List<CodeValuePhi> getAifaNote() {
		return aifaNote;
	}

	public void setAifaNote(List<CodeValuePhi> aifaNote) {
		this.aifaNote = aifaNote;
	}
}