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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "surgical_id_checking")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SurgicalIdChecking extends AuditedEntity {

	private static final long serialVersionUID = 1463516842L;


	/**
	*  javadoc for interventionType
	*/
	private String interventionType;

	@Column(name="intervention_type")
	public String getInterventionType(){
		return interventionType;
	}

	public void setInterventionType(String interventionType){
		this.interventionType = interventionType;
	}

	/**
	*  javadoc for priorityClass
	*/
	private String priorityClass;

	@Column(name="priority_class")
	public String getPriorityClass(){
		return priorityClass;
	}

	public void setPriorityClass(String priorityClass){
		this.priorityClass = priorityClass;
	}

	/**
	*  javadoc for checkAutPreRic1
	*/
	private Boolean checkAutPreRic1;

	@Column(name="checkAutPreRic1")
	public Boolean getCheckAutPreRic1(){
		return checkAutPreRic1;
	}

	public void setCheckAutPreRic1(Boolean checkAutPreRic1){
		this.checkAutPreRic1 = checkAutPreRic1;
	}

	/**
	*  javadoc for checkAutPreRic2
	*/
	private Boolean checkAutPreRic2;

	@Column(name="checkAutPreRic2")
	public Boolean getCheckAutPreRic2(){
		return checkAutPreRic2;
	}

	public void setCheckAutPreRic2(Boolean checkAutPreRic2){
		this.checkAutPreRic2 = checkAutPreRic2;
	}


	/**
	*  javadoc for checkAutRic
	*/
	private Boolean checkAutRic;

	@Column(name="checkAutRic")
	public Boolean getCheckAutRic(){
		return checkAutRic;
	}

	public void setCheckAutRic(Boolean checkAutRic){
		this.checkAutRic = checkAutRic;
	}

	

	/**
	*  javadoc for checkAutSurgDay
	*/
	private Boolean checkAutSurgDay;

	@Column(name="checkAutSurgDay")
	public Boolean getCheckAutSurgDay(){
		return checkAutSurgDay;
	}

	public void setCheckAutSurgDay(Boolean checkAutSurgDay){
		this.checkAutSurgDay = checkAutSurgDay;
	}

	/**
	*  javadoc for checkAutSoTras
	*/
	private Boolean checkAutSoTras;

	@Column(name="checkAutSoTras")
	public Boolean getCheckAutSoTras(){
		return checkAutSoTras;
	}

	public void setCheckAutSoTras(Boolean checkAutSoTras){
		this.checkAutSoTras = checkAutSoTras;
	}

	/**
	*  javadoc for checkAutSoArrive
	*/
	private Boolean checkAutSoArrive;

	@Column(name="checkAutSoArrive")
	public Boolean getCheckAutSoArrive(){
		return checkAutSoArrive;
	}

	public void setCheckAutSoArrive(Boolean checkAutSoArrive){
		this.checkAutSoArrive = checkAutSoArrive;
	}

	
	
	

	/**
	*  javadoc for authorPreRic1
	*/
	private Employee authorPreRic1;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_pre_ric1_id")
	@ForeignKey(name="FK_srgidcheck_autPreRc1")
	@Index(name="IX_srgidcheck_thorPreRc1")
	public Employee getAuthorPreRic1(){
		return authorPreRic1;
	}

	public void setAuthorPreRic1(Employee authorPreRic1){
		this.authorPreRic1 = authorPreRic1;
	}

	
	
	/**
	*  javadoc for authorPreRic2
	*/
	private Employee authorPreRic2;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_pre_ric2_id")
	@ForeignKey(name="FK_srgidcheck_autPreRc2")
	@Index(name="IX_srgidcheck_autPreRc2")
	public Employee getAuthorPreRic2(){
		return authorPreRic2;
	}

	public void setAuthorPreRic2(Employee authorPreRic2){
		this.authorPreRic2 = authorPreRic2;
	}
	
	
	/**
	*  javadoc for authorRic
	*/
	
	private Employee authorRic;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_ric_id")
	@ForeignKey(name="FK_srgidcheck_autRc")
	@Index(name="IX_srgidcheck_autRc")
	public Employee getAuthorRic(){
		return authorRic;
	}

	public void setAuthorRic(Employee authorRic){
		this.authorRic = authorRic;
	}
	/**
	*  javadoc for authorSurgDay
	*/
	private Employee authorSurgDay;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_surg_day_id")
	@ForeignKey(name="FK_srgidcheck_autsrgday")
	@Index(name="IX_srgidcheck_autsrgday")
	public Employee getAuthorSurgDay(){
		return authorSurgDay;
	}

	public void setAuthorSurgDay(Employee authorSurgDay){
		this.authorSurgDay = authorSurgDay;
	}
	/**
	*  javadoc for authorSoTras
	*/
	private Employee authorSoTras;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_so_tras_id")
	@ForeignKey(name="FK_srgidcheck_autSoTras")
	@Index(name="IX_srgidcheck_autSoTras")
	public Employee getAuthorSoTras(){
		return authorSoTras;
	}

	public void setAuthorSoTras(Employee authorSoTras){
		this.authorSoTras = authorSoTras;
	}
	/**
	*  javadoc for authorSoArrive
	*/
	private Employee authorSoArrive;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_so_arr_id")
	@ForeignKey(name="FK_srgidcheck_autSoArr")
	@Index(name="IX_srgidcheck_autSoArr")
	public Employee getAuthorSoArrive(){
		return authorSoArrive;
	}

	public void setAuthorSoArrive(Employee authorSoArrive){
		this.authorSoArrive = authorSoArrive;
	}

	/**
	*  javadoc for patientEncounter
	*/
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_encounter_id")
	@ForeignKey(name="FK_srgidcheck_patEnc")
	@Index(name="IX_srgidcheck_patEnc")
	public PatientEncounter getPatientEncounter(){
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}

	/**
	 * signTimePreRic1
	 */
	protected Date signTimePreRic1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_pre_ric1")
	public Date getSignTimePreRic1() {
		return signTimePreRic1;
	}

	public void setSignTimePreRic1(Date signTimePreRic1) {
		this.signTimePreRic1 = signTimePreRic1;
	}
	/**
	 * signTimePreRic2
	 */
	protected Date signTimePreRic2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_pre_ric2")
	public Date getSignTimePreRic2() {
		return signTimePreRic2;
	}

	public void setSignTimePreRic2(Date signTimePreRic2) {
		this.signTimePreRic2 = signTimePreRic2;
	}
	/**
	 * signTimeRic1
	 */
	protected Date signTimeRic1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_ric1")
	public Date getSignTimeRic1() {
		return signTimeRic1;
	}

	public void setSignTimeRic1(Date signTimeRic1) {
		this.signTimeRic1 = signTimeRic1;
	}
	/**
	 * signTimeSurgDay
	 */
	protected Date signTimeSurgDay;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_surg_day")
	public Date getSignTimeSurgDay() {
		return signTimeSurgDay;
	}

	public void setSignTimeSurgDay(Date signTimeSurgDay) {
		this.signTimeSurgDay = signTimeSurgDay;
	}


	/**
	 * signTimeSoTras
	 */
	protected Date signTimeSoTras;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_so_tras")
	public Date getSignTimeSoTras() {
		return signTimeSoTras;
	}

	public void setSignTimeSoTras(Date signTimeSoTras) {
		this.signTimeSoTras = signTimeSoTras;
	}
	
	
	/**
	 * signTimeSoArrive
	 */
	protected Date signTimeSoArrive;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_time_so_arrive")
	public Date getSignTimeSoArrive() {
		return signTimeSoArrive;
	}

	public void setSignTimeSoArrive(Date signTimeSoArrive) {
		this.signTimeSoArrive = signTimeSoArrive;
	}
	
	
	
	
	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SurgicalIdChecking_sequence")
	@SequenceGenerator(name = "SurgicalIdChecking_sequence", sequenceName = "SurgicalIdChecking_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	
	
	//methods needed for AuditedEntity extension
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SurgiclIdChckng_cnclldByRol")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_SurgiclIdChckng_cnclldByRol")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SurgiclIdChecking_cncelldBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_SurgiclIdChecking_cncelldBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SurgiclIdChecking_uthorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_SurgiclIdChecking_uthorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SurgicalIdChecking_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_SurgicalIdChecking_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}
	private Employee lastAuthor;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="last_author_id")
	@ForeignKey(name="FK_SurgicalIdChecking_lastAth")
	@Index(name="IX_SurgicalIdChecking_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
