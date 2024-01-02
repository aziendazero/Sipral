package com.phi.entities.baseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
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
import org.hibernate.envers.NotAudited;

import com.phi.entities.act.Identification;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.role.Patient;
@javax.persistence.Entity
@Table(name = "contact")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Contact extends BaseEntity {

	private static final long serialVersionUID = 2061644180L;

	//methods needed for BaseEntity extension

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Contact_sequence")
	@SequenceGenerator(name = "Contact_sequence", sequenceName = "Contact_sequence")
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
	@ForeignKey(name = "FK_Contact_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/

	/**
	 *  javadoc for name
	 */
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
	public EN getName(){
		return name;
	}

	public void setName(EN name){
		this.name = name;
	}

	/**
	 *  javadoc for telecom
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
	 *  javadoc for parentalCode
	 */
	private CodeValuePhi parentalCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parental_code")
	@ForeignKey(name="FK_Contact_parentalCode")
	@Index(name="IX_Contact_parentalCode")
	public CodeValuePhi getParentalCode(){
		return parentalCode;
	}

	public void setParentalCode(CodeValuePhi parentalCode){
		this.parentalCode = parentalCode;
	}


	private Boolean recieveInfRight; // diritto di ricevere informazioni

	@Column(name="recieveInfRight")
	public Boolean getRecieveInfRight(){	

		return recieveInfRight;
	}

	public void setRecieveInfRight(Boolean recieveInfRight){
		this.recieveInfRight = recieveInfRight;
	}

	private Boolean personReference;

	@Column(name="personReference")
	public Boolean getPersonReference() {
		return personReference;
	}

	public void setPersonReference(Boolean personReference) {
		this.personReference = personReference;
	}
	
	/**
	*  javadoc for identification
	*/
	
	private Identification identification;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="identification_id")
	@ForeignKey(name="FK_Contact_identification")
	@Index(name="IX_Contact_identification")
	public Identification getIdentification(){
		return identification;
	}

	public void setIdentification(Identification identification){
		this.identification = identification;
	}
	
	/**
	*  javadoc for Patient
	*/
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_contact_patient")
	@Index(name="IX_contact_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}
}
