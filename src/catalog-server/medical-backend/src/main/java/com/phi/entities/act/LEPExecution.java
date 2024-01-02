package com.phi.entities.act;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.IVL;
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name = "lep_execution")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=LEPExecution.class)
public class LEPExecution extends ClinicalProcedure 
{		
	private static final long serialVersionUID = 3678454499176316609L;
	
	
	private LEPActivity lepActivity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lepact_id")
	@ForeignKey(name = "FK_lepexe_lepact")
	@Index(name="IX_lepexe_lepact")
	public LEPActivity getLepActivity() {
		return lepActivity;
	}
	
	public void setLepActivity(LEPActivity lepActivity) {
		this.lepActivity = lepActivity;
	}
	
	
	private CodeValue statusDetailsCode;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="status_details_code")
	@ForeignKey(name="FK_lepexe_statusDet")
	@Index(name="IX_lepexe_statusDet")
	public CodeValue getStatusDetailsCode() {
		return statusDetailsCode;
	}
	public void setStatusDetailsCode(CodeValue statusDetailsCode) {
		this.statusDetailsCode = statusDetailsCode;
	}

	private Date plannedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="planned_date")
	public Date getPlannedDate() {
		return plannedDate;
	}
	public void setPlannedDate(Date plannedDate) {
		this.plannedDate = plannedDate;
	}
	
	
	private Integer plannedTime;
	
	@Column(name="planned_time")
	public Integer getPlannedTime() {
		return plannedTime;
	}
	public void setPlannedTime(Integer plannedTime) {
		this.plannedTime = plannedTime;
	}
	
	
	private IVL<Date> executionDate;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="executionDate_time_low")),
		@AttributeOverride(name="high", column=@Column(name="executionDate_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="executionDate_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="executionDate_highClosed"))
	})
	public IVL<Date> getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(IVL<Date> executionDate) {
		this.executionDate = executionDate;
	}
	
	
	private Integer executionTime;
	
	@Column(name="execution_time")	
	public Integer getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(Integer executionTime) {
		this.executionTime = executionTime;
	}
		
	
	private String note;
	
	@Column(name="note")
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}	
}