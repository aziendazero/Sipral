package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.Column;
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

@javax.persistence.Entity
@Table(name = "tag_ditta")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TagDitta extends BaseEntity {

	private static final long serialVersionUID = 594213086L;

	/**
	*  javadoc for dittaCf
	*/
	private String dittaCf;

	@Column(name="ditta_cf")
	public String getDittaCf(){
		return dittaCf;
	}

	public void setDittaCf(String dittaCf){
		this.dittaCf = dittaCf;
	}


	/**
	*  javadoc for tipologiaDitta
	*/
	private TipologiaDitta tipologiaDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia_ditta_id")
	@ForeignKey(name="FK_tagDit_type")
	//@Index(name="IX_tagDit_type")
	public TipologiaDitta getTipologiaDitta(){
		return tipologiaDitta;
	}

	public void setTipologiaDitta(TipologiaDitta tipologiaDitta){
		this.tipologiaDitta = tipologiaDitta;
	}

	/**
	*  javadoc for notes
	*/
	private String notes;

	@Column(name="notes",length=2000)
	public String getNotes(){
		return notes;
	}

	public void setNotes(String notes){
		this.notes = notes;
	}

	/**
	*  javadoc for endValidity
	*/
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	*  javadoc for startValidity
	*/
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TagDitta_sequence")
	@SequenceGenerator(name = "TagDitta_sequence", sequenceName = "TagDitta_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
