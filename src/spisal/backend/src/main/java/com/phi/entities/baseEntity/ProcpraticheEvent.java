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

import javax.persistence.OneToOne;

import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValuePhi;

import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "procpratiche_event")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ProcpraticheEvent extends BaseEntity {

	private static final long serialVersionUID = 669326338L;

	/**
	*  javadoc for employee
	*/
	private Employee employee;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_PrcpraticheEvent_emplyee")
	//@Index(name="IX_PrcpraticheEvent_emplyee")
	public Employee getEmployee(){
		return employee;
	}

	public void setEmployee(Employee employee){
		this.employee = employee;
	}

	/**
	*  javadoc for titolo
	*/
	private CodeValuePhi titolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="titolo")
	@ForeignKey(name="FK_ProcpraticheEvent_titolo")
	//@Index(name="IX_ProcpraticheEvent_titolo")
	public CodeValuePhi getTitolo(){
		return titolo;
	}

	public void setTitolo(CodeValuePhi titolo){
		this.titolo = titolo;
	}

	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_PrcprtcheEvent_prcprtche")
	//@Index(name="IX_PrcprtcheEvent_prcprtche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	/**
	*  javadoc for testo
	*/
	private String testo;

	@Column(name="testo", length=2000)
	public String getTesto(){
		return testo;
	}

	public void setTesto(String testo){
		this.testo = testo;
	}

	/**
	*  javadoc for inserimentoManuale
	*/
	private Boolean inserimentoManuale;

	@Column(name="inserimento_manuale")
	public Boolean getInserimentoManuale(){
		return inserimentoManuale;
	}

	public void setInserimentoManuale(Boolean inserimentoManuale){
		this.inserimentoManuale = inserimentoManuale;
	}


	/**
	*  javadoc for operatore

	private Operatore operatore;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_PrcpraticheEvent_peratre")
	//@Index(name="IX_PrcpraticheEvent_peratre")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}

	*/
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProcpraticheEvent_sequence")
	@SequenceGenerator(name = "ProcpraticheEvent_sequence", sequenceName = "ProcpraticheEvent_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
