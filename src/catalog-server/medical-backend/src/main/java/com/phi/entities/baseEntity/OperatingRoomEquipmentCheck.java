package com.phi.entities.baseEntity;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.EquipmentCheck;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "operating_room_equip_check")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class OperatingRoomEquipmentCheck extends BaseEntity {

	private static final long serialVersionUID = 2079020715L;
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "operating_room_equip_check_seq")
	@SequenceGenerator(name = "operating_room_equip_check_seq", sequenceName = "operating_room_equip_check_seq")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	protected Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	protected Boolean equipeChange;

	@Column(name="eqp_chng")
	public Boolean getEquipeChange() {
		return equipeChange;
	}
	public void setEquipeChange(Boolean equipeChange) {
		this.equipeChange = equipeChange;
	}
	
	private Date equipeChangeDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="eqp_chng_dt")	
	public Date getEquipeChangeDate() {
		return equipeChangeDate;
	}
	
	public void setEquipeChangeDate(Date equipeChangeDate) {
		this.equipeChangeDate = equipeChangeDate;
	}

	
	
	private EquipmentCheck bandages10x1032st;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="ban32_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="ban32_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="ban32_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="ban32_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="ban32_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="ban32_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="ban32_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="ban32_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="ban32_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="ban32_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="ban32_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="ban32_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="ban32_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="ban32_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="ban32_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="ban32_cff"))
	})
	public EquipmentCheck getBandages10x1032st() {
		return bandages10x1032st;
	}
	
	public void setBandages10x1032st(EquipmentCheck bandages10x1032st) {
		this.bandages10x1032st = bandages10x1032st;
	}
	
	private EquipmentCheck bandages10x108st;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="ban8_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="ban8_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="ban8_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="ban8_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="ban8_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="ban8_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="ban8_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="ban8_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="ban8_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="ban8_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="ban8_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="ban8_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="ban8_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="ban8_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="ban8_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="ban8_cff"))
	})
	public EquipmentCheck getBandages10x108st() {
		return bandages10x108st;
	}
	
	public void setBandages10x108st(EquipmentCheck bandages10x108st) {
		this.bandages10x108st = bandages10x108st;
	}
	
	private EquipmentCheck wads;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="wads_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="wads_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="wads_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="wads_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="wads_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="wads_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="wads_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="wads_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="wads_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="wads_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="wads_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="wads_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="wads_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="wads_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="wads_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="wads_cff"))
	})
	public EquipmentCheck getWads() {
		return wads;
	}

	public void setWads(EquipmentCheck wads) {
		this.wads = wads;
	}
	
	private EquipmentCheck abdominalBandages;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="abdban_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="abdban_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="abdban_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="abdban_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="abdban_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="abdban_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="abdban_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="abdban_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="abdban_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="abdban_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="abdban_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="abdban_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="abdban_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="abdban_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="abdban_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="abdban_cff"))
	})
	public EquipmentCheck getAbdominalBandages() {
		return abdominalBandages;
	}

	public void setAbdominalBandages(EquipmentCheck abdominalBandages) {
		this.abdominalBandages = abdominalBandages;
	}
	
	private EquipmentCheck cottonGloves;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="cottg_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="cottg_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="cottg_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="cottg_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="cottg_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="cottg_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="cottg_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="cottg_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="cottg_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="cottg_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="cottg_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="cottg_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="cottg_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="cottg_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="cottg_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="cottg_cff"))
	})
	public EquipmentCheck getCottonGloves() {
		return cottonGloves;
	}

	public void setCottonGloves(EquipmentCheck cottonGloves) {
		this.cottonGloves = cottonGloves;
	}

	private EquipmentCheck valveCover;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="vcov_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="vcov_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="vcov_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="vcov_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="vcov_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="vcov_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="vcov_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="vcov_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="vcov_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="vcov_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="vcov_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="vcov_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="vcov_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="vcov_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="vcov_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="vcov_cff"))
	})
	public EquipmentCheck getValveCover() {
		return valveCover;
	}

	public void setValveCover(EquipmentCheck valveCover) {
		this.valveCover = valveCover;
	}

	private EquipmentCheck cottonSheets;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="cotts_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="cotts_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="cotts_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="cotts_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="cotts_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="cotts_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="cotts_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="cotts_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="cotts_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="cotts_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="cotts_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="cotts_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="cotts_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="cotts_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="cotts_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="cotts_cff"))
	})
	public EquipmentCheck getCottonSheets() {
		return cottonSheets;
	}

	public void setCottonSheets(EquipmentCheck cottonSheets) {
		this.cottonSheets = cottonSheets;
	}

	private EquipmentCheck tampon;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="tmpn_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="tmpn_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="tmpn_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="tmpn_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="tmpn_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="tmpn_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="tmpn_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="tmpn_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="tmpn_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="tmpn_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="tmpn_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="tmpn_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="tmpn_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="tmpn_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="tmpn_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="tmpn_cff"))
	})
	public EquipmentCheck getTampon() {
		return tampon;
	}

	public void setTampon(EquipmentCheck tampon) {
		this.tampon = tampon;
	}

	private EquipmentCheck needle;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="ndl_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="ndl_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="ndl_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="ndl_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="ndl_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="ndl_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="ndl_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="ndl_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="ndl_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="ndl_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="ndl_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="ndl_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="ndl_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="ndl_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="ndl_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="ndl_cff"))
	})
	public EquipmentCheck getNeedle() {
		return needle;
	}

	public void setNeedle(EquipmentCheck needle) {
		this.needle = needle;
	}

	private EquipmentCheck blade;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="chargeP1", column=@Column(name="bld_chp1")),
		@AttributeOverride(name="chargeP2", column=@Column(name="bld_chp2")),
		@AttributeOverride(name="chargeP3", column=@Column(name="bld_chp3")),
		@AttributeOverride(name="chargeP4", column=@Column(name="bld_chp4")),
		@AttributeOverride(name="chargeP5", column=@Column(name="bld_chp5")),
		@AttributeOverride(name="dischargeP1", column=@Column(name="bld_dcp1")),
		@AttributeOverride(name="dischargeP2", column=@Column(name="bld_dcp2")),
		@AttributeOverride(name="dischargeP3", column=@Column(name="bld_dcp3")),
		@AttributeOverride(name="dischargeP4", column=@Column(name="bld_dcp4")),
		@AttributeOverride(name="dischargeP5", column=@Column(name="bld_dcp5")),
		@AttributeOverride(name="confirmP1", column=@Column(name="bld_cf1")),
		@AttributeOverride(name="confirmP2", column=@Column(name="bld_cf2")),
		@AttributeOverride(name="confirmP3", column=@Column(name="bld_cf3")),
		@AttributeOverride(name="confirmP4", column=@Column(name="bld_cf4")),
		@AttributeOverride(name="confirmP5", column=@Column(name="bld_cf5")),
		@AttributeOverride(name="confirmPf", column=@Column(name="bld_cff"))
	})
	public EquipmentCheck getblade() {
		return blade;
	}

	public void setBlade(EquipmentCheck blade) {
		this.blade = blade;
	}

	private String scrubNurse;

	public String getScrubNurse() {
		return scrubNurse;
	}

	public void setScrubNurse(String scrubNurse) {
		this.scrubNurse = scrubNurse;
	}

	private String orNurse;


	public String getOrNurse() {
		return orNurse;
	}

	public void setOrNurse(String orNurse) {
		this.orNurse = orNurse;
	}

	private String scrubNurse2;

	public String getScrubNurse2() {
		return scrubNurse2;
	}

	public void setScrubNurse2(String scrubNurse2) {
		this.scrubNurse2 = scrubNurse2;
	}

	private String orNurse2;


	public String getOrNurse2() {
		return orNurse2;
	}

	public void setOrNurse2(String orNurse2) {
		this.orNurse2 = orNurse2;
	}
	
	protected Date availabilityTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "availability_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}

	private CodeValueRole authorRole;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_orec_auth_Role")
	@Index(name="IX_orec_auth_Role")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	
	private Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_orec_author")
	@Index(name="IX_orec_author")
	public Employee getAuthor(){
		return author;
	}
	
	public void setAuthor(Employee author){
		this.author = author;
	}

}