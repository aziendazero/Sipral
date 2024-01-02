package com.phi.entities.baseEntity;

// Generated 15-lug-2015 13.47.19 by Hibernate Tools 3.4.0.CR1
import java.util.Date;

import java.io.Serializable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.OneToOne;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

@Entity
@Table(name = "situazione_professionale")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SituazioneProfessionale extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2400870065993002705L;


	/**
	*  javadoc for person
	*/
	private Person person;

	@OneToOne(fetch=FetchType.LAZY, mappedBy="professionalSituation")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SitProf_sequence")
	@SequenceGenerator(name = "SitProf_sequence", sequenceName = "SitProf_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue type;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="type")
	@ForeignKey(name="FK_SitProf_type")
	//@Index(name="IX_SitProf_type")
	public CodeValue getType() {
		return type;
	}

	public void setType(CodeValue type) {
		this.type = type;
	}
	
	private String note;
	
	@Column(name = "note")
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	protected Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_from")
	public Date getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	protected Date validTo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="valid_to")
	public Date getValidTo() {
		return this.validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

}
