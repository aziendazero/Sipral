package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@Table(name = "pnc_messaggi")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Audited
public class PNCMessaggi extends BaseEntity {

	private static final long serialVersionUID = 590354956L;

	/**
	*  javadoc for ordine
	*/
	private String ordine;

	@Column(name="ordine")
	public String getOrdine(){
		return ordine;
	}

	public void setOrdine(String ordine){
		this.ordine = ordine;
	}

	/**
	*  javadoc for fineValidita
	*/
	private Date fineValidita;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fine_validita")
	public Date getFineValidita(){
		return fineValidita;
	}

	public void setFineValidita(Date fineValidita){
		this.fineValidita = fineValidita;
	}

	/**
	*  javadoc for inizioValidita
	*/
	private Date inizioValidita;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="inizio_validita")
	public Date getInizioValidita(){
		return inizioValidita;
	}

	public void setInizioValidita(Date inizioValidita){
		this.inizioValidita = inizioValidita;
	}

	/**
	*  javadoc for testo
	*/
	private String testo;

	@Column(name="testo", length=2500)
	public String getTesto(){
		return testo;
	}

	public void setTesto(String testo){
		this.testo = testo;
	}

	/**
	*  javadoc for titolo
	*/
	private String titolo;

	@Column(name="titolo")
	public String getTitolo(){
		return titolo;
	}

	public void setTitolo(String titolo){
		this.titolo = titolo;
	}

	/**
	*  javadoc for modificationDate
	*/
	private Date modificationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modification_date")
	public Date getModificationDate(){
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate){
		this.modificationDate = modificationDate;
	}

	/**
	*  javadoc for modifiedBy
	*/
	private String modifiedBy;

	@Column(name="modified_by")
	public String getModifiedBy(){
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy){
		this.modifiedBy = modifiedBy;
	}

	/**
	*  javadoc for applicativo
	*/
	private String applicativo;

	@Column(name="applicativo")
	public String getApplicativo(){
		return applicativo;
	}

	public void setApplicativo(String applicativo){
		this.applicativo = applicativo;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCMessaggi_sequence")
	@SequenceGenerator(name = "PNCMessaggi_sequence", sequenceName = "PNCMessaggi_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
