package com.phi.entities.baseEntity;

// Generated 15-lug-2015 13.47.19 by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;

import javax.persistence.AssociationOverride;
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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.TEL;

@javax.persistence.Entity
@Table(name = "spisal_addr")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SpisalAddr extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7267141209762658624L;

	/**
	*  javadoc for localita
	*/
	private String localita;

	@Column(name="localita")
	public String getLocalita(){
		return localita;
	}

	public void setLocalita(String localita){
		this.localita = localita;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SpisalAddr_sequence")
	@SequenceGenerator(name = "SpisalAddr_sequence", sequenceName = "SpisalAddr_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	 *  Country Of Address
	 */
	private CodeValueCountry countryOfAddr;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country_of_addr")
	@ForeignKey(name="FK_Alt_countryOfAddr")
	//@Index(name="IX_Alt_countryOfAddr")
	public CodeValueCountry getCountryOfAddr(){
		return countryOfAddr;
	}

	public void setCountryOfAddr(CodeValueCountry countryOfAddr){
		this.countryOfAddr = countryOfAddr;
	}
	
	/**
	 *  Domicile Address
	 */
	private AD addr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="dom_addr_code"))
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="addr_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="addr_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="addr_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="addr_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="addr_cty")),
		@AttributeOverride(name="sta", column=@Column(name="addr_sta")),
		@AttributeOverride(name="stb", column=@Column(name="addr_stb")),
		@AttributeOverride(name="str", column=@Column(name="addr_str")),
		@AttributeOverride(name="zip", column=@Column(name="addr_zip"))
	})
	
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
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

	public SpisalAddr cloneAddr(){
		SpisalAddr s = new SpisalAddr();
		if(this.addr!=null)
			s.addr = this.addr.cloneAd();
		if(this.telecom!=null)
			s.telecom = this.telecom.cloneTel();
		
		s.countryOfAddr = this.countryOfAddr;
		
		return s;
	}
}
