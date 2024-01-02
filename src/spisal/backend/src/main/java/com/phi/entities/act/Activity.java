package com.phi.entities.act;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.role.Employee;

@Entity
@Table(name = "activity")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Activity extends AuditedEntity {

	//methods needed for BaseEntity extension

	/**
	 * 
	 */
	private static final long serialVersionUID = 5372110802405136903L;


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Activity_sequence")
	@SequenceGenerator(name = "Activity_sequence", sequenceName = "Activity_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private Procpratiche procpratiche;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Activity_Procpratiche")
	//@Index(name="IX_Activity_Procpratiche")
	public Procpratiche getProcpratiche() {
	    return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche) {
	    this.procpratiche = procpratiche;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Activity_author")
	//@Index(name="IX_Activity_author")
	public Employee getAuthor() {
	    return author;
	}

	public void setAuthor(Employee param) {
	    this.author = param;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_Activity_authorRole")
	//@Index(name="IX_Activity_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}

	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	/**
	*  javadoc for cancelled_by
	*/

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_Activity_canc_by")
	//@Index(name="IX_Activity_canc_by")
	public Employee getCancelledBy(){
		return cancelledBy;
	}

	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelledByRole")
	@ForeignKey(name="FK_Activity_canc_by_rl")
	//@Index(name="IX_Activity_canc_by_rl")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}

	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}

	private boolean confirmed;
	
	@Column(name="confirmed")
	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}	
	
	private Date observationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="observation_date")
	public Date getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(Date observationDate) {
		this.observationDate = observationDate;
	}
	
	private Boolean isDeleted;
	
	@Column(name="is_deleted")
	public Boolean getIsDeleted(){
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted){
		this.isDeleted = isDeleted;
	}
	
	private String note;
	
	@Column(name="note")
	@Lob
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	private ED title;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="title_mediaType"))
	@AttributeOverrides({
	       @AttributeOverride(name="string", column=@Column(name="title_string", length=2500)),
	       @AttributeOverride(name="bytes", column=@Column(name="title_bytes"))
	})
	public ED getTitle() {
		return title;
	}

	public void setTitle(ED title) {
		this.title = title;
	}
	
	private ED text;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="text_mediaType"))
	@AttributeOverrides({
	       @AttributeOverride(name="string", column=@Column(name="text_string", length=2500)),
	       @AttributeOverride(name="bytes", column=@Column(name="text_bytes"))
	})
	public ED getText() {
		return text;
	}

	public void setText(ED text) {
		this.text = text;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Activity_Code")
	//@Index(name="IX_Activity_Code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueStatus.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Activity_sc")
	//@Index(name="IX_Activity_sc") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

}