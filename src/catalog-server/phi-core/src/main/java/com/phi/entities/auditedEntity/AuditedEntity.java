package com.phi.entities.auditedEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
/**
 * Mapped superclass is needed to have not conflictual internalIds in extended classes.
 * Needed to have the possibility to read all the audited entities.
 *  
 *
 */
@MappedSuperclass
public abstract class AuditedEntity extends BaseEntity  {

	private static final long serialVersionUID = -1362742679745625425L;

	/**
	 *  Author
	 */
	
	protected Employee author;
	@Transient
	public abstract Employee getAuthor();
	public abstract void setAuthor(Employee author);
	
	
	/**
	 *  CodeValue containing Role of Author
	 */
	
	protected CodeValueRole authorRole;
	@Transient
	public abstract CodeValueRole getAuthorRole();

	public abstract void setAuthorRole(CodeValueRole authorRole);
	
	/**
	 *  Cancelled By
	 */
	
	protected Employee cancelledBy;
	@Transient
	public abstract Employee getCancelledBy();
	
	public abstract void setCancelledBy(Employee cancelledBy);

	/**
	 *  CodeValue containing Role of Cancelled By
	 */
	
	protected CodeValueRole cancelledByRole;
	@Transient
	public abstract CodeValueRole getCancelledByRole();

	public abstract void setCancelledByRole(CodeValueRole cancelledByRole);
	
	/**
	*  Cancellation Date
	*/
	private Date cancellationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cancellation_date")
	public Date getCancellationDate(){
		return cancellationDate;
	}

	public void setCancellationDate(Date cancellationDate){
		this.cancellationDate = cancellationDate;
	}

	/**
	*  Cancellation Note
	*/
	private String cancellationNote;

	@Column(name="cancellation_note")
	public String getCancellationNote(){
		return cancellationNote;
	}

	public void setCancellationNote(String cancellationNote){
		this.cancellationNote = cancellationNote;
	}
	
	/**
	 * Availability Time
	 */
	protected Date availabilityTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "availability_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}
}
