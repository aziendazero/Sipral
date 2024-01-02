package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.phi.json.CustomJsonEntity;
import com.phi.json.PrescriptionMedicineGenericDeserializer;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.json.JsonProxyGenerator;

/**
 * Many to many table between Prescription and Medicine
 * @author alex.zupan
 */

@Entity
@Table(name = "prescription_medicine")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=PrescriptionMedicine.class)
@CustomJsonEntity(deserializer = PrescriptionMedicineGenericDeserializer.class)
public class PrescriptionMedicine extends PrescriptionMedicineGeneric {

	private static final long serialVersionUID = 693885816L;
	
	protected static final Logger log = Logger.getLogger(PrescriptionMedicine.class);
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PrescriptionMedicine_sequence")
	@SequenceGenerator(name = "PrescriptionMedicine_sequence", sequenceName = "PrescriptionMedicine_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
		
	@JsonBackReference(value="prescription_prescriptionMedicine")
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=Prescription.class)
	@JoinColumn(name="prescription_id")
	@ForeignKey(name="FK_Prescription_Medicine")
	@Index(name="IX_Prescription_Medicine")
	@JsonDeserialize(as=Prescription.class)
	public PrescriptionGeneric getPrescription(){
		return prescription;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="medicine_id")
	@ForeignKey(name="FK_Medicine_Prescription")
	@Index(name="IX_Medicine_Prescription")
	public Medicine getMedicine(){
		return medicine;
	}
}