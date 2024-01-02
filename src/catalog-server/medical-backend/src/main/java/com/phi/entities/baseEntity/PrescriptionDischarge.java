package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.entities.act.Therapy;

@Entity
@Table(name = "prescription_dschrg")
@Audited
public class PrescriptionDischarge extends PrescriptionGeneric {

	private static final long serialVersionUID = -4413522559681531336L;

	/**
	 *  javadoc for duration
	 */
	private Integer duration;

	@Column(name="duration")
	public Integer getDuration(){
		return duration;
	}

	public void setDuration(Integer duration){
		this.duration = duration;
	}

	/**
	 *  javadoc for therapy
	 */
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
	@JoinColumn(name="therapy")
	@ForeignKey(name="FK_Prscrpt_dsch_therapy")
	@Index(name="IX_Prscrpt_dsch_therapy")
	public Therapy getTherapy() {
		return therapy;
	}

	/**
	*  javadoc for prescriptionMedicine
	*/
	@JsonManagedReference(value="prescriptionDischarge_prescriptionMedicineDischarge")
	@JsonDeserialize(contentAs=PrescriptionDischargeMedicine.class)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="prescription", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, targetEntity=PrescriptionDischargeMedicine.class)
	@Fetch(FetchMode.SELECT)
	public List<PrescriptionMedicineGeneric> getPrescriptionMedicine(){
		return prescriptionMedicine;
	}
	
	public void addPrescriptionMedicine(PrescriptionDischargeMedicine prescriptionMedicine) {
		if (this.prescriptionMedicine == null) {
			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
		}
		// add the association
		if(!this.prescriptionMedicine.contains(prescriptionMedicine)) {
			this.prescriptionMedicine.add(prescriptionMedicine);
			// make the inverse link
			prescriptionMedicine.setPrescription(this);
		}
	}

	public void removePrescriptionMedicine(PrescriptionDischargeMedicine prescriptionMedicine) {
		if (this.prescriptionMedicine == null) {
			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
			return;
		}
		//add the association
		if(this.prescriptionMedicine.contains(prescriptionMedicine)){
			this.prescriptionMedicine.remove(prescriptionMedicine);
			//make the inverse link
			prescriptionMedicine.setPrescription(null);
		}

	}

	/**
	 *  javadoc for rootPrescription
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=PrescriptionDischarge.class)
	@JoinColumn(name="root_prescription_id")
	@ForeignKey(name="FK_Prscrpt_dsch_rtPrscrptn")
	@Index(name="IX_Prscrpt_dsch_rtPrscrptn")
	@JsonDeserialize(as=PrescriptionDischarge.class)
	public PrescriptionGeneric getRootPrescription(){
		return rootPrescription;
	}
}