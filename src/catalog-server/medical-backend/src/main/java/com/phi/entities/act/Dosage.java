package com.phi.entities.act;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.PQ;
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name = "dosage")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=Dosage.class)
public class Dosage extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private PQ quantity;

	private CodeValue type;
	private CodeValue weekDayCode;
	private CodeValue daymomentCode;  	//moment of the day like morning, evening, lunch time,...
	private Date daytime;         		//specific time of the day, like 9:30, 15:00,..
	private Integer dayInterval;		//every 2, 3 or N days
	private CodeValue frequency;    	//specific if the dosage is only one time, or every time the time configuration is found (e.g.: every monday, or only one monday (the next one))

	private Prescription prescription;
	private LEPActivity lepActivity;
	private SubstanceAdministration substanceAdministration;
	
	private String doseDie;			//used in pharmacy requests (MOD_Integrations/Pharmacy)
	private String duration;		//used in pharmacy requests (MOD_Integrations/Pharmacy)
	private String quantityTxt;		//used in pharmacy requests (MOD_Integrations/Pharmacy)
	

	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Dosage_sequence")
	@SequenceGenerator(name = "Dosage_sequence", sequenceName = "Dosage_sequence")
	@Column(name = "internal_id")	
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="quantity_value")),
		@AttributeOverride(name="unit", column=@Column(name="quantiy_unit"))
	})
	public PQ getQuantity() {
		return quantity;
	}

	public void setQuantity(PQ quantity) {
		this.quantity = quantity;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="dosagetype_code")
	@ForeignKey(name="FK_Dosage_typeCode")
	@Index(name="IX_Dosage_typeCode")
	public CodeValue getType() {
		return type;
	}

	public void setType(CodeValue type) {
		this.type = type;
	}
	

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="frequency_code")
	@ForeignKey(name="FK_Dosage_frqCode")
	@Index(name="IX_Dosage_frqCode")
	public CodeValue getFrequency() {
		return frequency;
	}

	public void setFrequency(CodeValue frequency) {
		this.frequency = frequency;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="weekday_code")
	@ForeignKey(name="FK_Dosage_weekdayCode")
	@Index(name="IX_Dosage_weekdayCode")
	public CodeValue getWeekDayCode() {
		return weekDayCode;
	}
	
	public void setWeekDayCode(CodeValue weekDayCode) {
		this.weekDayCode = weekDayCode;
	}
	
		
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="daymoment_code")
	@ForeignKey(name="FK_Dosage_dayCode")
	@Index(name="IX_Dosage_dayCode")
	public CodeValue getDaymomentCode() {
		return daymomentCode;
	}

	public void setDaymomentCode(CodeValue daymomentCode) {
		this.daymomentCode = daymomentCode;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="daytime")
	public Date getDaytime() {
		return daytime;
	}

	public void setDaytime(Date daytime) {
		this.daytime = daytime;
	}

	
	@Column(name="day_interval")
	public Integer getDayInterval() {
		return dayInterval;
	}

	public void setDayInterval(Integer dayInterval) {
		this.dayInterval = dayInterval;
	}

	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Prescription")
	@ForeignKey(name="FK_Dosage_Prescription")
	@Index(name="IX_Dosage_Prescription")
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription Prescription) {
		this.prescription = Prescription;
	}

	
	@JsonBackReference(value="activity_dosage")
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lep_activity")
	@ForeignKey(name="FK_Dosage_lepAct")
	@Index(name="IX_Dosage_lepAct")
	public LEPActivity getLepActivity() {
		return lepActivity;
	}

	public void setLepActivity(LEPActivity lepActivity) {
		this.lepActivity = lepActivity;
	}
			
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SubstanceAdministration")
	@ForeignKey(name="FK_Dosage_SubstAdmin")
	@Index(name="IX_Dosage_SubstAdmin")
	public SubstanceAdministration getSubstanceAdministration() {
		return substanceAdministration;
	}

	public void setSubstanceAdministration(SubstanceAdministration SubstanceAdministration) {
		this.substanceAdministration = SubstanceAdministration;
	}
	
	
	@Column(name="dose_die")
	public String getDoseDie() {
		return doseDie;
	}

	public void setDoseDie(String doseDie) {
		this.doseDie = doseDie;
	}
	
	
	@Column(name="duration")
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
	

	@Column(name="quantity_txt")
	public String getQuantityTxt() {
		return quantityTxt;
	}

	public void setQuantityTxt(String quantityTxt) {
		this.quantityTxt = quantityTxt;
	}
}