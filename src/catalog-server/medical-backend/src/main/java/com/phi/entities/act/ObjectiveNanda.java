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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueNanda;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.role.Employee;
import com.phi.json.JsonProxyGenerator;

/**
 * Goals reached or to be reached for each diagnosis
 * 1) compile a Nanda questionnaire
 * 2) set goals to reach  
 * 3) executed LEP 
 * 4) return to goals list, so to confirm if it has been reached or not
 * @author francesco.rossi
 */

@Entity
@Table(name = "objective_nanda")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=ObjectiveNanda.class)
public class ObjectiveNanda extends AuditedEntity {

	private static final long serialVersionUID = 1743759417L;

	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ObjectiveNanda_sequence")
	@SequenceGenerator(name = "ObjectiveNanda_sequence", sequenceName = "ObjectiveNanda_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	
	private Nanda nanda;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nanda_id")
	@ForeignKey(name="FK_Objective_nanda")
	@Index(name="IX_Objective_nanda")
	public Nanda getNanda(){
	   return nanda;
	}
	
	public void setNanda(Nanda nanda){
	   this.nanda = nanda;
	}
	
	
	private CodeValue objLep;

	@ManyToOne(fetch=FetchType.LAZY,targetEntity=CodeValueNanda.class)
	@JoinColumn(name="objLep")
	@ForeignKey(name="FK_lep_Obj")
	@Index(name="IX_lep_Obj")
	public CodeValue getObjLep() {
		return objLep;
	}
	public void setObjLep(CodeValue objLep) {
		this.objLep = objLep;
	}
	
	
	private Date objInitDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="objInit_Date")
	public Date getObjInitDate() {
		return objInitDate;
	}
	
	public void setObjInitDate(Date  objInitDate) {
		this.objInitDate = objInitDate;
	}

	
	private Date objLimitDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="objLimit_Date")
	public Date getObjLimitDate() {
		return objLimitDate;
	}
	
	public void setObjLimitDate(Date  objLimitDate) {
		this.objLimitDate = objLimitDate;
	}

	
	private CodeValuePhi objReached;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="obj_Reached")
	@ForeignKey(name="FK_obj_Reached")
	@Index(name="IX_obj_Reached")
	public CodeValuePhi getObjReached(){
		return objReached;
	}

	public void setObjReached(CodeValuePhi objReached){
		this.objReached = objReached;
	}

	
	private Boolean confirmed;
		
	@Column(name="confirmed")
	public Boolean getConfirmed() {
		   return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		   this.confirmed = confirmed;
	}

	
	private Integer timeSpent;
	
	@Column(name="time_spent")
	public Integer getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Integer timeSpent) {
		this.timeSpent = timeSpent;
	}

	
	private String note;
	
	@Column(name="note", length = 2500)
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	
	private CodeValue objFrequency;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="obj_Frequency")
	@ForeignKey(name="FK_objnanda_freq")
	@Index(name="IX_objnanda_freq")
	public CodeValue getObjFrequency(){
	   return objFrequency;
	}
	
	public void setObjFrequency(CodeValue objFrequency){
	   this.objFrequency = objFrequency;
	}
	
	
	private LEPActivity activity;
	
	@OneToOne(fetch=FetchType.LAZY, mappedBy="objective", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public LEPActivity getActivity() {
		return activity;
	}

	public void setActivity(LEPActivity activity) {
		this.activity = activity;				
	}
	
	
	private CodeValue levelCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="level_code")
	@ForeignKey(name="FK_ObjNnd_lec")
	@Index(name="IX_ObjNnd_lec")
	public CodeValue getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(CodeValue levelCode) {
		this.levelCode = levelCode;
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
	
	
	private PatientEncounter patientEncounter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_ObjNnd_Pat_Enc")
	@Index(name="IX_ObjNnd_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}

	// From auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ObjNnd_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_ObjNnd_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_ObjNnd_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_ObjNnd_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ObjNnd_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_ObjNnd_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_ObjNnd_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_ObjNnd_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}
}