package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.phi.entities.FavoriteProfile;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValueNanda;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.role.Employee;
import com.phi.json.JsonProxyGenerator;


@Entity
@Table(name = "lep_activity")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=LEPActivity.class)
public class LEPActivity extends AuditedEntity {

	private static final long serialVersionUID = 1145777893L;
		
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "LEPActivity_sequence")
	@SequenceGenerator(name = "LEPActivity_sequence", sequenceName = "LEPActivity_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
		
	
	private List<LEPExecution> lepExecution;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "lepActivity", cascade=CascadeType.PERSIST)
	@Fetch(FetchMode.SELECT)
	public List<LEPExecution> getLepExecution() {
	    return lepExecution;
	}

	public void setLepExecution(List<LEPExecution> param) {
	    this.lepExecution = param;
	}
	
	public void addLepExecution(LEPExecution lepExecution) {
		if (this.lepExecution == null) {
			this.lepExecution = new ArrayList<LEPExecution>();
		}
		// add the association
		if(!this.lepExecution.contains(lepExecution)) {
			this.lepExecution.add(lepExecution);
			// make the inverse link
			lepExecution.setLepActivity(this);
		}
	}

	public void removeLepExecution(LEPExecution lepExecution) {
		if (this.lepExecution == null) {
			this.lepExecution = new ArrayList<LEPExecution>();
			return;
		}
		// remove the association
		if(this.lepExecution.contains(lepExecution)){
			this.lepExecution.remove(lepExecution);
			// remove the inverse link
			lepExecution.setLepActivity(null);
		}
	}
	
	
	private CodeValuePhi statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_ClinProc_sc")
	@Index(name="IX_ClinProc_sc") 
	public CodeValuePhi getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValuePhi statusCode) {
		this.statusCode = statusCode;
	}
	
	
	private CodeValueRole responsibleRole;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="responsible_role")
	@ForeignKey(name="FK_Lep_Responsible")
	@Index(name="IX_lepact_responsibleRole")	
	public CodeValueRole getResponsibleRole() {
		return responsibleRole;
	}

	public void setResponsibleRole(CodeValueRole responsibleRole) {
		this.responsibleRole = responsibleRole;
	}
	
	
	private CodeValueRole supportRole;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="support_role")
	@ForeignKey(name="FK_Lep_Support")
	@Index(name="IX_lepact_supportRole")
	public CodeValueRole getSupportRole() {
		return supportRole;
	}

	public void setSupportRole(CodeValueRole supportRole) {
		this.supportRole = supportRole;
	}
	
	
	private Integer supportNumber;

	@Column(name="support_number")
	public Integer getSupportNumber() {
		return supportNumber;
	}

	public void setSupportNumber(Integer supportNumber) {
		this.supportNumber = supportNumber;
	}
	
	
	private VitalSign vitalSign;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vitalSign_id")
	@ForeignKey(name = "FK_lepact_VS")
	@Index(name="IX_lepact_VS")
	public VitalSign getVitalSign() {
		return vitalSign;
	}
	
	public void setVitalSign(VitalSign vitalSign) {
		this.vitalSign = vitalSign;
	}

	
	private IVL<Date> effectiveDate;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="effective_Date_low")),
		@AttributeOverride(name="high", column=@Column(name="effective_Date_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="effective_Date_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="effective_Date_highClosed"))
	})
	public IVL<Date> getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(IVL<Date> effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	
	private CodeValueNanda nandaLep;

	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="nandaLep")
	@ForeignKey(name="FK_lep_nanda")
	@Index(name="IX_lep_nanda")
	public CodeValueNanda getNandaLep() {
		return nandaLep;
	}
	
	public void setNandaLep(CodeValueNanda nandaLep) {
		this.nandaLep = nandaLep;
	}

		
	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;

	}
	

	private Boolean modified;

	@Column(name="modified")
	public Boolean getModified() {
		return modified;
	}
	
	public void setModified(Boolean modified) {
		this.modified = modified;

	}
	
	
	private boolean extemporaneous = false;
	
	@Column(name="extemporaneous")
	public Boolean getExtemporaneous() {
		return extemporaneous;
	}

	public void setExtemporaneous(Boolean extemporaneous) {
		this.extemporaneous = extemporaneous;
	}

	
	private Integer timeSpent;

	@Column(name="time_spent")
	public Integer getTimeSpent() {
		return timeSpent;
	}
	
	public void setTimeSpent(Integer timeSpent) {
		this.timeSpent = timeSpent;
	}

	
	private CodeValuePhi lepFrequency;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lep_Frequency")
	@ForeignKey(name="FK_Lep_frequency")
	@Index(name="IX_Lep_frequency")
	public CodeValuePhi getLepFrequency(){
		return lepFrequency;
	}

	public void setLepFrequency(CodeValuePhi lepFrequency){
		this.lepFrequency = lepFrequency;
	}

	
	private String note;

	@Column(name="note")
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	
	private IVL<Date> validityPeriod;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="validity_period_time_low")),
		@AttributeOverride(name="high", column=@Column(name="validity_period_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="vperiod_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="validity_period_highClosed"))
	})

	public IVL<Date> getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(IVL<Date> validityPeriod) {
		this.validityPeriod = validityPeriod;
	}


	private List<Dosage> dosage;
	
	@JsonManagedReference(value="activity_dosage")
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "lepActivity", cascade=CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	public List<Dosage> getDosage() {
		return dosage;
	}

	public void setDosage(List<Dosage> param) {
		this.dosage = param;
	}

	public void addDosage(Dosage dosage) {
		if (this.dosage == null) {
			this.dosage = new ArrayList<Dosage>();
		}
		// add the association
		if(!this.dosage.contains(dosage)) {
			this.dosage.add(dosage);
			// make the inverse link
			dosage.setLepActivity(this);
		}
	}
	
	public void removeDosage(Dosage dosage) {
		if (this.dosage == null) {
			this.dosage = new ArrayList<Dosage>();
			return;
		}
		// remove the association
		if(this.dosage.contains(dosage)){
			this.dosage.remove(dosage);
			// remove the inverse link
			dosage.setLepActivity(null);
		}
	}
	
	
	private CodeValuePhi lepSource;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lepSource")
	@ForeignKey(name="FK_LEPAct_lepSrc")
	@Index(name="IX_Lep_source")
	public CodeValuePhi getLepSource() {
		return lepSource;
	}

	public void setLepSource(CodeValuePhi lepSource) {
		this.lepSource = lepSource;
	}
	
	
	private PatientEncounter patientEncounter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patenc")
	@ForeignKey(name="FK_lep_Enc")
	@Index(name="IX_lep_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}
	
	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}
		

	private Nanda nanda;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nanda_id")
	@ForeignKey(name="FK_lepact_nanda")
	@Index(name="IX_lepact_nanda")
	public Nanda getNanda() {
		return nanda;
	}
	
	public void setNanda(Nanda nanda) {
		this.nanda = nanda;
	}
	
	
	private ObjectiveNanda objective;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="objective_id")
	@ForeignKey(name="FK_LepAct_objective")
	@Index(name="IX_LepAct_objective")
	public ObjectiveNanda getObjective() {
		return objective;
	}

	public void setObjective(ObjectiveNanda objective) {
		this.objective = objective;
	}
	
	
	private FavoriteProfile profile;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="favoriteProfile_id")
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ForeignKey(name="FK_FavProf_Act")
	@Index(name="IX_Act_FavProf")
	public FavoriteProfile getProfile() 
	{
		return profile;
	}

	public void setProfile(FavoriteProfile profile) 
	{
			this.profile = profile;
	}
	
	
	// From auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_LEPAct_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_LEPAct_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_LEPAct_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_LEPAct_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_LEPAct_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_LEPAct_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_LEPAct_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_LEPAct_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}
}