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

import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Procpratiche;

import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "atto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Atto extends BaseEntity {

	private static final long serialVersionUID = 1119354013L;

	/**
	*  javadoc for nProto
	*/
	private String numeroProt;

	@Column(name="n_proto")
	public String getNumeroProt(){
		return numeroProt;
	}

	public void setNumeroProt(String nProto){
		this.numeroProt = nProto;
	}


	/**
	*  javadoc for employee
	*/
	private Employee employee;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_Atto_employee")
	@Index(name="IX_Atto_employee")
	public Employee getEmployee(){
		return employee;
	}

	public void setEmployee(Employee employee){
		this.employee = employee;
	}



	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Atto_prat")
	@Index(name="IX_Atto_prat")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}


	/**
	*  javadoc for direzione
	*/
	private CodeValuePhi direzione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="direzione")
	@ForeignKey(name="FK_Atto_direzione")
	@Index(name="IX_Atto_direzione")
	public CodeValuePhi getDirezione(){
		return direzione;
	}

	public void setDirezione(CodeValuePhi direzione){
		this.direzione = direzione;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note", length=2500)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for numProt
	*/
//	private Integer numProt;
//
//	@Column(name="num_prot")
//	public Integer getNumProt(){
//		return numProt;
//	}
//
//	public void setNumProt(Integer numProt){
//		this.numProt = numProt;
//	}

	/**
	*  javadoc for dateProt
	*/
	private Date dateProt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_prot")
	public Date getDateProt(){
		return dateProt;
	}

	public void setDateProt(Date dateProt){
		this.dateProt = dateProt;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Atto_sequence")
	@SequenceGenerator(name = "Atto_sequence", sequenceName = "Atto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
