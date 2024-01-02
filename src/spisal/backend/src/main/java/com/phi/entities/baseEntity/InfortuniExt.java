package com.phi.entities.baseEntity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "infortuni_ext")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class InfortuniExt extends BaseEntity {

	private static final long serialVersionUID = 627283921L;

	/**
	*  javadoc for notificaStatus
	*/
	private String notificaStatus;

	@Column(name="notifica_status")
	public String getNotificaStatus(){
		return notificaStatus;
	}

	public void setNotificaStatus(String notificaStatus){
		this.notificaStatus = notificaStatus;
	}

	/**
	*  javadoc for ageDeath
	*/
	private String ageDeath;

	@Column(name="age_death")
	public String getAgeDeath(){
		return ageDeath;
	}

	public void setAgeDeath(String ageDeath){
		this.ageDeath = ageDeath;
	}

	/**
	*  javadoc for notificaDecesso
	*/
	private Boolean notificaDecesso;

	@Column(name="notifica_decesso")
	public Boolean getNotificaDecesso(){
		return notificaDecesso;
	}

	public void setNotificaDecesso(Boolean notificaDecesso){
		this.notificaDecesso = notificaDecesso;
	}

	/**
	*  javadoc for modInformoNote
	*/
	private String modInformoNote;

	@Column(name="mod_informo_note")
	public String getModInformoNote(){
		return modInformoNote;
	}

	public void setModInformoNote(String modInformoNote){
		this.modInformoNote = modInformoNote;
	}

	/**
	*  javadoc for modInformo
	*/
	private CodeValuePhi modInformo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="modInformo")
	@ForeignKey(name="FK_InfExt_modInformo")
	@Index(name="IX_InfExt_modInformo")
	public CodeValuePhi getModInformo(){
		return modInformo;
	}

	public void setModInformo(CodeValuePhi modInformo){
		this.modInformo = modInformo;
	}

	/**
	*  javadoc for infortunioProf
	*/
	private CodeValuePhi infortunioProf;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="infortunioProf")
	@ForeignKey(name="FK_InfExt_infProf")
	@Index(name="IX_InfExt_infProf")
	public CodeValuePhi getInfortunioProf(){
		return infortunioProf;
	}

	public void setInfortunioProf(CodeValuePhi infortunioProf){
		this.infortunioProf = infortunioProf;
	}

	private String note;
	
	@Column(name = "note", length = 4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	private String streetDescription;
	
	@Column(name = "street_description", length = 2500)
	public String getStreetDescription() {
		return this.streetDescription;
	}
	
	public void setStreetDescription(String streetDescription) {
		this.streetDescription = streetDescription;
	}
	
	
	/**
	*  javadoc for rifNote
	*/
	private String rifNote;

	@Column(name="rif_note")
	public String getRifNote(){
		return rifNote;
	}

	public void setRifNote(String rifNote){
		this.rifNote = rifNote;
	}

	/**
	*  javadoc for rifDenominazione
	*/
	private String rifDenominazione;

	@Column(name="rif_denominazione")
	public String getRifDenominazione(){
		return rifDenominazione;
	}

	public void setRifDenominazione(String rifDenominazione){
		this.rifDenominazione = rifDenominazione;
	}

	/**
	*  javadoc for deceasedNoteFinal
	*/
	private String deceasedNoteFinal;

	@Column(name="deceased_note_final")
	public String getDeceasedNoteFinal(){
		return deceasedNoteFinal;
	}

	public void setDeceasedNoteFinal(String deceasedNoteFinal){
		this.deceasedNoteFinal = deceasedNoteFinal;
	}

	/**
	*  javadoc for deceasedCodeFinal
	*/
	private CodeValuePhi deceasedCodeFinal;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="deceasedCodeFinal")
	@ForeignKey(name="FK_InfortuniExt_deceasedCodeFinal")
	//@Index(name="IX_InfortuniExt_deceasedCodeFinal")
	public CodeValuePhi getDeceasedCodeFinal(){
		return deceasedCodeFinal;
	}

	public void setDeceasedCodeFinal(CodeValuePhi deceasedCodeFinal){
		this.deceasedCodeFinal = deceasedCodeFinal;
	}

	/**
	*  javadoc for deceasedPlaceFinal
	*/
	private AD deceasedPlaceFinal;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="deceasedPlaceFinal_code"))
	@AttributeOverrides({
	@AttributeOverride(name="adl", column=@Column(name="deceasedPlaceFinal_adl")),
	@AttributeOverride(name="bnr", column=@Column(name="deceasedPlaceFinal_bnr")),
	@AttributeOverride(name="cen", column=@Column(name="deceasedPlaceFinal_cen")),
	@AttributeOverride(name="cnt", column=@Column(name="deceasedPlaceFinal_cnt")),
	@AttributeOverride(name="cpa", column=@Column(name="deceasedPlaceFinal_cpa")),
	@AttributeOverride(name="cty", column=@Column(name="deceasedPlaceFinal_cty")),
	@AttributeOverride(name="sta", column=@Column(name="deceasedPlaceFinal_sta")),
	@AttributeOverride(name="stb", column=@Column(name="deceasedPlaceFinal_stb")),
	@AttributeOverride(name="str", column=@Column(name="deceasedPlaceFinal_str")),
	@AttributeOverride(name="zip", column=@Column(name="deceasedPlaceFinal_zip"))
	})
	public AD getDeceasedPlaceFinal(){
		return deceasedPlaceFinal;
	}

	public void setDeceasedPlaceFinal(AD deceasedPlaceFinal){
		this.deceasedPlaceFinal = deceasedPlaceFinal;
	}

	/**
	*  javadoc for deceasedTimeFinal
	*/
	private Date deceasedTimeFinal;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="deceased_time_final")
	public Date getDeceasedTimeFinal(){
		return deceasedTimeFinal;
	}

	public void setDeceasedTimeFinal(Date deceasedTimeFinal){
		this.deceasedTimeFinal = deceasedTimeFinal;
	}

	/**
	*  javadoc for disabilityFinal
	*/
	private Integer disabilityFinal;

	@Column(name="disability_final")
	public Integer getDisabilityFinal(){
		return disabilityFinal;
	}

	public void setDisabilityFinal(Integer disabilityFinal){
		this.disabilityFinal = disabilityFinal;
	}

	/**
	*  javadoc for gravitaFinale
	*/
	private CodeValuePhi gravitaFinale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gravitaFinale")
	@ForeignKey(name="FK_InfortuniExt_gravitaFinale")
	//@Index(name="IX_InfortuniExt_gravitaFinale")
	public CodeValuePhi getGravitaFinale(){
		return gravitaFinale;
	}

	public void setGravitaFinale(CodeValuePhi gravitaFinale){
		this.gravitaFinale = gravitaFinale;
	}

	/**
	*  javadoc for otherAddr
	*/
//	private AD otherAddr;
//
//	@Embedded
//	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="otherAddr_code"))
//	@AttributeOverrides({
//	@AttributeOverride(name="adl", column=@Column(name="otherAddr_adl")),
//	@AttributeOverride(name="bnr", column=@Column(name="otherAddr_bnr")),
//	@AttributeOverride(name="cen", column=@Column(name="otherAddr_cen")),
//	@AttributeOverride(name="cnt", column=@Column(name="otherAddr_cnt")),
//	@AttributeOverride(name="cpa", column=@Column(name="otherAddr_cpa")),
//	@AttributeOverride(name="cty", column=@Column(name="otherAddr_cty")),
//	@AttributeOverride(name="sta", column=@Column(name="otherAddr_sta")),
//	@AttributeOverride(name="stb", column=@Column(name="otherAddr_stb")),
//	@AttributeOverride(name="str", column=@Column(name="otherAddr_str")),
//	@AttributeOverride(name="zip", column=@Column(name="otherAddr_zip"))
//	})
//	public AD getOtherAddr(){
//		return otherAddr;
//	}
//
//	public void setOtherAddr(AD otherAddr){
//		this.otherAddr = otherAddr;
//	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "InfortuniExt_sequence")
	@SequenceGenerator(name = "InfortuniExt_sequence", sequenceName = "InfortuniExt_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for addr
	*/
	private AD addr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addr_code"))
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
	*  javadoc for longitudine
	*/
	private String longitudine;

	@Column(name="longitudine")
	public String getLongitudine(){
		return longitudine;
	}

	public void setLongitudine(String longitudine){
		this.longitudine = longitudine;
	}

	/**
	*  javadoc for latitudine
	*/
	private String latitudine;

	@Column(name="latitudine")
	public String getLatitudine(){
		return latitudine;
	}

	public void setLatitudine(String latitudine){
		this.latitudine = latitudine;
	}

	/**
	*  javadoc for specificazione
	*/
	private String specificazione;

	@Column(name="specificazione")
	public String getSpecificazione(){
		return specificazione;
	}

	public void setSpecificazione(String specificazione){
		this.specificazione = specificazione;
	}

	/**
	*  javadoc for targa
	*/
	private String targa;

	@Column(name="targa")
	public String getTarga(){
		return targa;
	}

	public void setTarga(String targa){
		this.targa = targa;
	}

	/**
	*  javadoc for imo
	*/
	private String imo;

	@Column(name="imo")
	public String getImo(){
		return imo;
	}

	public void setImo(String imo){
		this.imo = imo;
	}

	/**
	*  javadoc for entita
	*/
	private CodeValuePhi entita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="entita")
	@ForeignKey(name="FK_Inf_entita")
	//@Index(name="IX_Inf_entita")
	public CodeValuePhi getEntita(){
		return entita;
	}

	public void setEntita(CodeValuePhi entita){
		this.entita = entita;
	}

}
