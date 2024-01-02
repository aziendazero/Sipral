package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.baseEntity.Procpratiche;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "sospensione")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Sospensione extends BaseEntity {

	private static final long serialVersionUID = 1340773937L;

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}


	/**
	*  javadoc for reactivatedBy
	*/
	private Employee reactivatedBy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="reactivated_by_id")
	@ForeignKey(name="FK_Sospensione_reactivatedBy")
	//@Index(name="IX_Sospensione_reactivatedBy")
	public Employee getReactivatedBy(){
		return reactivatedBy;
	}

	public void setReactivatedBy(Employee reactivatedBy){
		this.reactivatedBy = reactivatedBy;
	}



	/**
	*  javadoc for suspendedBy
	*/
	private Employee suspendedBy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="suspended_by_id")
	@ForeignKey(name="FK_Sospensione_suspendedBy")
	//@Index(name="IX_Sospensione_suspendedBy")
	public Employee getSuspendedBy(){
		return suspendedBy;
	}

	public void setSuspendedBy(Employee suspendedBy){
		this.suspendedBy = suspendedBy;
	}


	/**
	*  javadoc for dataFine
	*/
	private Date dataFine;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_fine")
	public Date getDataFine(){
		return dataFine;
	}

	public void setDataFine(Date dataFine){
		this.dataFine = dataFine;
	}

	/**
	*  javadoc for dataInizio
	*/
	private Date dataInizio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_inizio")
	public Date getDataInizio(){
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio){
		this.dataInizio = dataInizio;
	}


	/**
	*  javadoc for pratica
	*/
	private Procpratiche pratica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="pratica_id")
	@ForeignKey(name="FK_Sospensione_pratica")
	//@Index(name="IX_Sospensione_pratica")
	public Procpratiche getPratica(){
		return pratica;
	}

	public void setPratica(Procpratiche pratica){
		this.pratica = pratica;
	}


	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Sospensione_sequence")
	@SequenceGenerator(name = "Sospensione_sequence", sequenceName = "Sospensione_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
