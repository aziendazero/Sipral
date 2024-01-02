package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.Medicine;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.role.Employee;

@Entity
@Table(name = "substanceadmin")
@Audited
public class SubstanceAdministration extends ClinicalProcedure {

	private static final long serialVersionUID = -535821941814575650L;

	private Prescription prescription;
	private IVL<Date> administratedDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prescription")
	@ForeignKey(name="FK_SubsAdm_prescription")
	@Index(name="IX_SubsAdm_prescription")
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}
	
	
	private CodeValue routeCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="route_code")
	@ForeignKey(name="FK_SubsAdm_rou")
	@Index(name="IX_SubsAdm_rou")
	public CodeValue getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(CodeValue routeCode) {
		this.routeCode = routeCode;
	}

	private CodeValue statusDetailsCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="status_details_code")
	@ForeignKey(name="FK_SubsAdm_statusDet")
	@Index(name="IX_SubsAdm_statusDet")
	public CodeValue getStatusDetailsCode() {
		return statusDetailsCode;
	}

	public void setStatusDetailsCode(CodeValue statusDetailsCode) {
		this.statusDetailsCode = statusDetailsCode;
	}
	
	private IVL<PQ> doseQuantity;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="low.value", column=@Column(name="doseQuantity_low_value")),
	       @AttributeOverride(name="low.unit", column=@Column(name="doseQuantity_low_unit")),
		   @AttributeOverride(name="high.value", column=@Column(name="doseQuantity_high_value")),
	       @AttributeOverride(name="high.unit", column=@Column(name="doseQuantity_high_unit")),
	       @AttributeOverride(name="lowClosed", column=@Column(name="doseQuantity_lowClosed")),
	       @AttributeOverride(name="highClosed", column=@Column(name="doseQuantity_highClosed"))
	})
	public IVL<PQ> getDoseQuantity() {
		return doseQuantity;
	}

	public void setDoseQuantity(IVL<PQ> doseQuantity) {
		this.doseQuantity = doseQuantity;
	}

	private IVL<PQ> rateQuantity;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="low.value", column=@Column(name="rateQuantity_low_value")),
	       @AttributeOverride(name="low.unit", column=@Column(name="rateQuantity_low_unit")),
		   @AttributeOverride(name="high.value", column=@Column(name="rateQuantity_high_value")),
	       @AttributeOverride(name="high.unit", column=@Column(name="rateQuantity_high_unit")),
	       @AttributeOverride(name="lowClosed", column=@Column(name="rateQuantity_lowClosed")),
	       @AttributeOverride(name="highClosed", column=@Column(name="rateQuantity_highClosed"))
	})
	public IVL<PQ> getRateQuantity() {
		return rateQuantity;
	}

	public void setRateQuantity(IVL<PQ> rateQuantity) {
		this.rateQuantity = rateQuantity;
	}

//  TODO: TEMPORARY COMMENTED: TO BE REWRITTEN THESE TWO SETS.
//	private Set<RTO<PQ, PQ>> doseCheckQuantity;
//
//	@OneToMany(targetEntity=RTO4DoseCheck.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@JoinColumn(name="substanceadministration_id")
//	@ForeignKey(name="FK_SubsAdm_dc")
//	@Index(name="IX_SubsAdm_dc") //FIXME: accorciato FK_SubsAdm_doseCheckQuantity
//	@AuditJoinTable(name="z_subadmin_dosequantity_jt")
//	public Set<RTO<PQ, PQ>> getDoseCheckQuantity() {
//		return doseCheckQuantity;
//	}
//
//	public void setDoseCheckQuantity(Set<RTO<PQ, PQ>> doseCheckQuantity) {
//		this.doseCheckQuantity = doseCheckQuantity;
//	}
//
//	private Set<RTO<PQ, PQ>> maxDoseQuantity;
//
//	@OneToMany(targetEntity=RTO4MaxDose.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@JoinColumn(name="substanceadministration_id")
//	@ForeignKey(name="FK_SubsAdm_md")
//	@Index(name="IX_SubsAdm_md") //FIXME: accorciato FK_SubsAdm_maxDoseQuantity
//	@AuditJoinTable(name="z_subadmin_maxdosequantity_jt")
//	public Set<RTO<PQ, PQ>> getMaxDoseQuantity() {
//		return maxDoseQuantity;
//	}
//
//	public void setMaxDoseQuantity(Set<RTO<PQ, PQ>> maxDoseQuantity) {
//		this.maxDoseQuantity = maxDoseQuantity;
//	}

	
	private PQ  plannedQuantity;
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="planned_quantity_value")),
	       @AttributeOverride(name="unit", column=@Column(name="planned_quantity_unit"))
	})
	public PQ getPlannedQuantity() {
		return plannedQuantity;
	}

	public void setPlannedQuantity(PQ plannedQuantity) {
		this.plannedQuantity = plannedQuantity;
	}

	private PQ administratedQuantity;
	
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="admin_quantity_value")),
	       @AttributeOverride(name="unit", column=@Column(name="admin_quantity_unit"))
	})
	public PQ getAdministratedQuantity() {
		return administratedQuantity;
	}

	public void setAdministratedQuantity(PQ administratedQuantity) {
		this.administratedQuantity = administratedQuantity;
	}
	
	
	private Date plannedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="planned_date")
	public Date getPlannedDate() {
		return plannedDate;
	}

	public void setPlannedDate(Date plannedDate) {
		this.plannedDate = plannedDate;
	}

		
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="administratedDate_time_low")),
		@AttributeOverride(name="high", column=@Column(name="administratedDate_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="administratedDate_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="administratedDate_highClosed"))
	})

	public IVL<Date> getAdministratedDate() {
		return administratedDate;
	}

	public void setAdministratedDate(IVL<Date> administratedDate) {
		this.administratedDate = administratedDate;
	}


	private CodeValue administrationUnitCode;

	


	private List<Medicine> medicine;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="administration_unit_code")
	@ForeignKey(name="FK_SubsAdm_adm")
	@Index(name="IX_SubsAdm_adm")
	public CodeValue getAdministrationUnitCode() {
		return administrationUnitCode;
	}

	public void setAdministrationUnitCode(CodeValue administrationUnitCode) {
		this.administrationUnitCode = administrationUnitCode;
	}

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="subadmin_medicine", 
		joinColumns = { @JoinColumn(name="substanceAdministration_id") }, 
		inverseJoinColumns = { @JoinColumn(name="medicine_id") })
	@ForeignKey(name="FK_SubsAdm_Medicine", inverseName="FK_Medicine_substAdmin")
	public List<Medicine> getMedicine() {
	    return medicine;
	}

	public void setMedicine(List<Medicine> param) {
	    this.medicine = param;
	}

	public void addMedicine(Medicine medicine) {
		if (this.medicine == null) {
			this.medicine = new ArrayList<Medicine>();
		}
		// add the association
		if(!this.medicine.contains(medicine)) {
			this.medicine.add(medicine);
		}
	}
	
	
	private List<Dosage> dosage;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "substanceAdministration")
	public List<Dosage> getDosage() {
	    return dosage;
	}

	public void setDosage(List<Dosage> param) {
	    this.dosage = param;
	}	
	
	private ED text;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="text_mediaType"))
	//FIXME: @ForeignKey(name="FK_Act_text_mediaType")
//	@Index(name="IX_Act_text_mediaType")
	@AttributeOverrides({
	       @AttributeOverride(name="string", column=@Column(name="text_string", length=2500)),
	       @AttributeOverride(name="bytes", column=@Column(name="text_bytes"))
	})
	public ED getText() {
		return text;
	}

	public void setText(ED text) {
		this.text = text;
	}
	
	private CodeValueRole stopAuthorRole;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_SubAdm_stopAuthorRole")
	@JoinColumn(name="stopAuthorRole")
	@Index(name="IX_SubAdm_stopAuthorRole")
	public CodeValueRole getStopAuthorRole(){
		return stopAuthorRole;
	}
	
	public void setStopAuthorRole(CodeValueRole stopAuthorRole){
		this.stopAuthorRole = stopAuthorRole;
	}
	
	private Employee stopAuthor;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SubAdm_stopAuthor")
	@JoinColumn(name="stop_author_id")
	@Index(name="IX_SubAdm_stopAuthor")
	public Employee getStopAuthor(){
		return stopAuthor;
	}
	
	public void setStopAuthor(Employee stopAuthor){
		this.stopAuthor = stopAuthor;
	}
}