package com.phi.entities.role;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;

/**
 * Entità non storicizzata via ENVERS perchè aggiornata principalmente via integrazione
 * @author 510087
 *
 */

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.Soggetto;
@javax.persistence.Entity
@Table(name = "physician")

public class Physician extends BaseEntity {
		
	private static final long serialVersionUID = -6081884208206908710L;

	/**
	*  javadoc for soggetto
	*/
	private List<Soggetto> soggetto;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="medico", cascade=CascadeType.PERSIST)
	public List<Soggetto> getSoggetto(){
		return soggetto;
	}

	public void setSoggetto(List<Soggetto> list){
		soggetto = list;
	}

	public void addSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
		}
		// add the association
		if(!this.soggetto.contains(soggetto)) {
			this.soggetto.add(soggetto);
			// make the inverse link
			soggetto.setMedico(this);
		}
	}

	public void removeSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
			return;
		}
		//add the association
		if(this.soggetto.contains(soggetto)){
			this.soggetto.remove(soggetto);
			//make the inverse link
			soggetto.setMedico(null);
		}

	}

	/**
	*  javadoc for strAppartenenza
	*/
	private CodeValuePhi strAppartenenza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="strAppartenenza")
	@ForeignKey(name="FK_Physician_strAppartenenza")
	//@Index(name="IX_Physician_strAppartenenza")
	public CodeValuePhi getStrAppartenenza(){
		return strAppartenenza;
	}

	public void setStrAppartenenza(CodeValuePhi strAppartenenza){
		this.strAppartenenza = strAppartenenza;
	}

	/**
	*  javadoc for specialization
	*/
	private String specialization;

	@Column(name="specialization")
	public String getSpecialization(){
		return specialization;
	}

	public void setSpecialization(String specialization){
		this.specialization = specialization;
	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_Physician_type")
	//@Index(name="IX_Physician_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	*  javadoc for notes
	*/
	private String notes;

	@Column(name="notes", length=4000)
	public String getNotes(){
		return notes;
	}

	public void setNotes(String notes){
		this.notes = notes;
	}


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_Physician_person")
	//@Index(name="IX_Physician_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Physician_sequence")
	@SequenceGenerator(name = "Physician_sequence", sequenceName = "Physician_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	 *  Name
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
	
	private String regionalCode;

	@Column(name="regional_code")
	public String getRegionalCode(){
		return regionalCode;
	}

	public void setRegionalCode(String regionalCode){
		this.regionalCode = regionalCode;
	}
	
	private Date modifiedDate;
	
	@Column(name="modified_date")
	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	/**
	 *  effectiveTime: role valid date interval
	 */
	private IVL<Date> validity;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="validity_low")),
		@AttributeOverride(name="high", column=@Column(name="validity_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="validity_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="validity_highClosed"))
	})
	public IVL<Date> getValidity(){
		return validity;
	}

	public void setValidity(IVL<Date> validity){
		this.validity = validity;
	}

	/**
	 *  role code for mmg
	 */
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Physician_code")
	//@Index(name="IX_Physician_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private Organization organization;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	@ForeignKey(name = "FK_Phy_to_Organization")
	//@Index(name="IX_Phy_to_Organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	private AD addr;

	@Embedded
	public AD getAddr() {
		return addr;
	}

	public void setAddr(AD addr) {
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
	
	/**
	*  javadoc for toUpdate
	*/
	private boolean toUpdate = false;
	
    @Column(name="to_update", nullable=false)
	public boolean getToUpdate() {
		return toUpdate;
	}

	public void setToUpdate(boolean toUpdate) {
		this.toUpdate = toUpdate;
	}
	
	/**
	*  Contatore incrementale per storico AUR
	*/
	
	private long rev;

	@Column(name="rev")
	public long getRev(){
		return rev;
	}

	public void setRev(long rev){
		this.rev = rev;
	}
}
