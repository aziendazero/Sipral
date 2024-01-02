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
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "observer")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Observer extends BaseEntity {

	private static final long serialVersionUID = 197965094L;

	/**
	*  javadoc for employee
	*/
	private Employee employee;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_Observer_employee")
	//@Index(name="IX_Observer_employee")
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

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Observer_procpratiche")
	//@Index(name="IX_Observer_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Observer_sequence")
	@SequenceGenerator(name = "Observer_sequence", sequenceName = "Observer_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
