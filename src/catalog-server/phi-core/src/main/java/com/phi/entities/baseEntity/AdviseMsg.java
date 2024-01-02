package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueRole;

import javax.persistence.OneToOne;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "advise_msg")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AdviseMsg extends BaseEntity {

	private static final long serialVersionUID = 794818505L;


	/**
	*  javadoc for author
	*/
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_AdviseMsg_author")
	@Index(name="IX_AdviseMsg_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}


	/**
	*  javadoc for test
	*/
	private Boolean test;

	@Column(name="test")
	public Boolean getTest(){
		return test;
	}

	public void setTest(Boolean test){
		this.test = test;
	}

	/**
	*  javadoc for active
	*/
	private Boolean active;

	@Column(name="active")
	public Boolean getActive(){
		return active;
	}

	public void setActive(Boolean active){
		this.active = active;
	}


	/**
	*  javadoc for restrictTo
	*/
	private List<Employee> restrictTo;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="AdviseMsg_id")
	@ForeignKey(name="FK_restrictTo_AdviseMsg")
	@Index(name="IX_restrictTo_AdviseMsg")
	public List<Employee> getRestrictTo() {
		return restrictTo;
	}

	public void setRestrictTo(List<Employee>list){
		restrictTo = list;
	}


	/**
	*  javadoc for restrictToRol
	*/
	private List<CodeValueRole> restrictToRol;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="AdviseMsg_restrictToRol", joinColumns = { @JoinColumn(name="AdviseMsg_id") }, inverseJoinColumns = { @JoinColumn(name="restrictToRol") })
	@ForeignKey(name="FK_restrictToRol_AdviseMsg", inverseName="FK_AdviseMsg_restrictToRol")
	@IndexColumn(name="list_index")
	public List<CodeValueRole> getRestrictToRol(){
		return restrictToRol;
	}

	public void setRestrictToRol(List<CodeValueRole> restrictToRol){
		this.restrictToRol = restrictToRol;
	}

	

	/**
	*  javadoc for scheduleTo
	*/
	private Date scheduleTo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="schedule_to")
	public Date getScheduleTo(){
		return scheduleTo;
	}

	public void setScheduleTo(Date scheduleTo){
		this.scheduleTo = scheduleTo;
	}

	

	/**
	*  javadoc for scheduleFrom
	*/
	private Date scheduleFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="schedule_from")
	public Date getScheduleFrom(){
		return scheduleFrom;
	}

	public void setScheduleFrom(Date scheduleFrom){
		this.scheduleFrom = scheduleFrom;
	}

	/**
	*  javadoc for text
	*/
	private String text;

	@Column(name="text")
	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AdviseMsg_sequence")
	@SequenceGenerator(name = "AdviseMsg_sequence", sequenceName = "AdviseMsg_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
