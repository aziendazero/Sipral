package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.CascadeType;
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

//import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "appuntamento_sp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AppointmentSp extends BaseEntity {

	private static final long serialVersionUID = 1301786469L;

	/**
	*  javadoc for soggetto
	*/
	private Soggetto soggetto;

	//@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="soggetto_id")
	@ForeignKey(name="FK_AppointmentSp_soggetto")
	//@Index(name="IX_AppointmentSp_soggetto")
	public Soggetto getSoggetto(){
		return soggetto;
	}

	public void setSoggetto(Soggetto soggetto){
		this.soggetto = soggetto;
	}


	/**
	*  javadoc for codeAtt
	*/
	private CodeValuePhi codeAtt;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codeAtt")
	@ForeignKey(name="FK_AppointmentSp_codeAtt")
	//@Index(name="IX_AppointmentSp_codeAtt")
	public CodeValuePhi getCodeAtt(){
		return codeAtt;
	}

	public void setCodeAtt(CodeValuePhi codeAtt){
		this.codeAtt = codeAtt;
	}


	/**
	*  javadoc for accertSp
	*/
	private AccertSp accertSp;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="accert_sp_id")
	@ForeignKey(name="FK_AppointmentSp_accertSp")
	//@Index(name="IX_AppointmentSp_accertSp")
	public AccertSp getAccertSp(){
		return accertSp;
	}

	public void setAccertSp(AccertSp accertSp){
		this.accertSp = accertSp;
	}


	/**
	*  javadoc for end
	*/
	private Date end;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end")
	public Date getEnd(){
		return end;
	}

	public void setEnd(Date end){
		this.end = end;
	}

	/**
	*  javadoc for data
	*/
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}


	/**
	*  javadoc for agendaConf
	*/
	private AgendaConf agendaConf;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="agenda_conf_id")
	@ForeignKey(name="FK_AppointmentSp_agendaConf")
	//@Index(name="IX_AppointmentSp_agendaConf")
	public AgendaConf getAgendaConf(){
		return agendaConf;
	}

	public void setAgendaConf(AgendaConf agendaConf){
		this.agendaConf = agendaConf;
	}



	/**
	*  javadoc for employee
	*/
	private Employee employee;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_AppointmentSp_employee")
	//@Index(name="IX_AppointmentSp_employee")
	public Employee getEmployee(){
		return employee;
	}

	public void setEmployee(Employee employee){
		this.employee = employee;
	}



	/**
	*  javadoc for visitaSp
	*/
	private VisitaSp visitaSp;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="visita_sp_id")
	@ForeignKey(name="FK_AppointmentSp_visitaSp")
	//@Index(name="IX_AppointmentSp_visitaSp")
	public VisitaSp getVisitaSp(){
		return visitaSp;
	}

	public void setVisitaSp(VisitaSp visitaSp){
		this.visitaSp = visitaSp;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AppointmentSp_sequence")
	@SequenceGenerator(name = "AppointmentSp_sequence", sequenceName = "AppointmentSp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
