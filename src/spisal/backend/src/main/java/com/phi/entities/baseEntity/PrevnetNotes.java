package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@javax.persistence.Entity
@Table(name = "prevnet_notes")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PrevnetNotes extends BaseEntity {

	private static final long serialVersionUID = 284586223L;

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	@Lob
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PrevnetNotes_sequence")
	@SequenceGenerator(name = "PrevnetNotes_sequence", sequenceName = "PrevnetNotes_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
