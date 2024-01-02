package com.phi.entities.entity;



import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;

@javax.persistence.Entity
@Table(name = "organization")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Organization extends BaseEntity {

	private static final long serialVersionUID = -4544806978691400523L;

	//methods needed for BaseEntity extension
	/**
	*  javadoc for headerText
	*/
	private String headerText;

	@Column(name="header_text", length=1000)
	public String getHeaderText(){
		return headerText;
	}

	public void setHeaderText(String headerText){
		this.headerText = headerText;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Organization_sequence")
	@SequenceGenerator(name = "Organization_sequence", sequenceName = "Organization_sequence")
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
	@ForeignKey(name = "FK_Organization_sdloc")
	@Index(name = "IX_Organization_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	private AD addr;

	@Embedded
	public AD getAddr() {
		return addr;
	}

	public void setAddr(AD addr) {
		this.addr = addr;
	}

//	public AD getOrgaddr() {
//		return getAddr();
//	}
//
//	public void setOrgaddr(AD addr) {
//		setAddr(addr);
//	}

	private CodeValue standardIndustryClassCode;
//	private List<ServiceDeliveryLocation> serviceDeliveryLocation;


	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="standard_industry_class_code")
	@ForeignKey(name="FK_Organization_standardIndust")
	@Index(name="IX_Organization_standardIndust")
	public CodeValue getStandardIndustryClassCode() {
		return standardIndustryClassCode;
	}

	public void setStandardIndustryClassCode(CodeValue standardIndustryClassCode) {
		this.standardIndustryClassCode = standardIndustryClassCode;
	}

	//From Entity
	
	/**
	*  in Entity was SET[II] but used only one value with root 2.16.840.1.113883.2.9.4.1.1
	*/
	private String id;

	@Column(name="id")
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
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
	 *  Telecom
	 */
	private TEL telecom;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telecom_as")),
		@AttributeOverride(name="bad", column=@Column(name="telecom_bad")),
		@AttributeOverride(name="dir", column=@Column(name="telecom_dir")),
		@AttributeOverride(name="ec", column=@Column(name="telecom_ec")),
		@AttributeOverride(name="h", column=@Column(name="telecom_h")),
		@AttributeOverride(name="hp", column=@Column(name="telecom_hp")),
		@AttributeOverride(name="hv", column=@Column(name="telecom_hv")),
		@AttributeOverride(name="mail", column=@Column(name="telecom_mail")),
		@AttributeOverride(name="mc", column=@Column(name="telecom_mc")),
		@AttributeOverride(name="pg", column=@Column(name="telecom_pg")),
		@AttributeOverride(name="pub", column=@Column(name="telecom_pub")),
		@AttributeOverride(name="sip", column=@Column(name="telecom_sip")),
		@AttributeOverride(name="tmp", column=@Column(name="telecom_tmp")),
		@AttributeOverride(name="wp", column=@Column(name="telecom_wp"))
	})
	public TEL getTelecom(){
		return telecom;
	}

	public void setTelecom(TEL telecom){
		this.telecom = telecom;
	}
	
	/**
	*  javadoc for vatNumber
	*/
	private String vatNumber;

	@Column(name="vat_number")
	public String getVatNumber(){
		return vatNumber;
	}

	public void setVatNumber(String vatNumber){
		this.vatNumber = vatNumber;
	}
	
	private Date validFrom;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_from")
	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	private Date validTo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_to")
	public Date getValidTo() {
		return this.validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	
	/**
	*  javadoc for discountRate
	*/
	private Double discountRate;

	@Column(name="discount_rate")
	public Double getDiscountRate(){
		return discountRate;
	}

	public void setDiscountRate(Double discountRate){
		this.discountRate = discountRate;
	}

}