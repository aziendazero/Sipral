package com.phi.entities.act;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.NotAudited;

import com.phi.entities.baseEntity.OperatingRoomEquipmentCheck;
import com.phi.entities.baseEntity.OperatingRoomFinalCheck;
import com.phi.entities.baseEntity.OperatingRoomSurgeryEquipment;
import com.phi.entities.baseEntity.SurgicalIdChecking;
import com.phi.entities.baseEntity.SurgicalSecurityCheck;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
@javax.persistence.Entity
@Table(name = "INTERVENTO")
public class LisaSurgicalIntervention extends ClinicalProcedure implements LocatedEntity {

	private static final long serialVersionUID = 1220419735L;
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_lisa_interv_SDL")
	@Index(name="IX_lisa_interv_SDL")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	/**
	*  javadoc for codeValueLisaIntervention
	*  
	*/
	private CodeValuePhi codeValueLisaIntervention;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code_value_lisa_interv")
	@ForeignKey(name="FK_LisaInt_codeLisaInterv")
	@Index(name="IX_LisaInt_codeLisaInterv")
	public CodeValuePhi getCodeValueLisaIntervention(){
		return codeValueLisaIntervention;
	}

	public void setCodeValueLisaIntervention(CodeValuePhi codeValueLisaIntervention){
		this.codeValueLisaIntervention = codeValueLisaIntervention;
	}
	
	
	/**
	 * 	javadoc for sdlLisaCode
	 * 	id del codice dell'intervento LISA
	 */
	private Long lisaInterventionCode;

	@Column(name="lisa_intervention_code")
	public Long getLisaInterventionCode(){
		return lisaInterventionCode;
	}

	public void setLisaInterventionCode(Long lisaInterventionCode){
		this.lisaInterventionCode = lisaInterventionCode;
	}
	
	/**
	 * 	javadoc for sdlLisaCode
	 * 	internal_id dell'unità erogante dell'intervento lato LISA
	 */
	private Long sdlLisaCode;

	@Column(name="sdl_lisa_code")
	public Long getSdlLisaCode(){
		return sdlLisaCode;
	}

	public void setSdlLisaCode(Long sdlLisaCode){
		this.sdlLisaCode = sdlLisaCode;
	}
	
	/**
	 * 	javadoc for otRoomCode
	 * internal_id della sala operatoria in cui sarà erogato l'intervento lato LISA
	 */
	private Long otRoomCode;

	@Column(name="ot_room_code")
	public Long getOtRoomCode(){
		return otRoomCode;
	}

	public void setOtRoomCode(Long otRoomCode){
		this.otRoomCode = otRoomCode;
	}

	/**
	*  javadoc for g2IdRico
	*/
	private String g2IdRico;

	@Column(name="g2_id_rico")
	public String getG2IdRico(){
		return g2IdRico;
	}

	public void setG2IdRico(String g2IdRico){
		this.g2IdRico = g2IdRico;
	}
	
	/**
	*  javadoc for g2_rico_Date
	*/
	private Date g2RicoDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="g2_rico_Date")
	public Date getG2RicoDate(){
		return g2RicoDate;
	}

	public void setG2RicoDate(Date g2RicoDate){
		this.g2RicoDate = g2RicoDate;
	}
	
//	/**
//	*  javadoc for interventionDate
//	*/
//	private Date interventionDate;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="intervention_Date")
//	public Date getInterventionDate(){
//		return interventionDate;
//	}
//
//	public void setInterventionDate(Date interventionDate){
//		this.interventionDate = interventionDate;
//	}
	
	/**
	*  javadoc for codeIcd9
	*/
	private CodeValueIcd9 codeIcd9;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codeIcd9")
	@ForeignKey(name="FK_LsSrgclInt_codeIcd9")
	@Index(name="IX_LsSrgclInt_codeIcd9")
	public CodeValueIcd9 getCodeIcd9(){
		return codeIcd9;
	}

	public void setCodeIcd9(CodeValueIcd9 codeIcd9){
		this.codeIcd9 = codeIcd9;
	}

	/**
	*  javadoc for endDate
	*/
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate(){
		return endDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	/**
	*  javadoc for startDate
	*/
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	public Date getStartDate(){
		return startDate;
	}

	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	/**
	*  javadoc for g2PatCF
	*/
	private String g2PatCF;

	@Column(name="g2_pat_cf")
	public String getG2PatCF(){
		return g2PatCF;
	}

	public void setG2PatCF(String g2PatCF){
		this.g2PatCF = g2PatCF;
	}

	/**
	*  javadoc for g2angr0
	*/
	private String g2angr0;

	@Column(name="g2angr0")
	public String getG2angr0(){
		return g2angr0;
	}

	public void setG2angr0(String g2angr0){
		this.g2angr0 = g2angr0;
	}

	/**
	*  javadoc for g2epis0
	*/
	private String g2epis0;

	@Column(name="g2epis0")
	public String getG2epis0(){
		return g2epis0;
	}

	public void setG2epis0(String g2epis0){
		this.g2epis0 = g2epis0;
	}

	/**
	*  javadoc for g2IdInter
	*/
	private String g2IdInter;

	@Column(name="g2_id_inter")
	public String getG2IdInter(){
		return g2IdInter;
	}

	public void setG2IdInter(String g2IdInter){
		this.g2IdInter = g2IdInter;
	}


	/**
	*  javadoc for patientEncounter
	*/
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_encounter_id")
	@ForeignKey(name="FK_LsSrgclnt_patEnc")
	@Index(name="IX_LsSrgclnt_patEnc")
	public PatientEncounter getPatientEncounter(){
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}



	/**
	*  javadoc for patient
	*/
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_LsSrgclInt_pat")
	@Index(name="IX_LsSrgclInt_pat")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}


	/**
	*  javadoc for sdlLisa
	*/
	private String sdlLisa;

	@Column(name="sdl_lisa")
	public String getSdlLisa(){
		return sdlLisa;
	}

	public void setSdlLisa(String sdlLisa){
		this.sdlLisa = sdlLisa;
	}

	/**
	*  javadoc for otRoom
	*/
	private String otRoom;

	@Column(name="ot_room")
	public String getOtRoom(){
		return otRoom;
	}

	public void setOtRoom(String otRoom){
		this.otRoom = otRoom;
	}

	/**
	*  javadoc for descInter
	*/
	private String descInter;

	@Column(name="descInter")
	public String getDescInter(){
		return descInter;
	}

	public void setDescInter(String descInter){
		this.descInter = descInter;
	}
	
	private OperatingRoomEquipmentCheck orEquipCheck;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="or_equip_check")
	@ForeignKey(name="FK_or_equip_check")
	@Index(name="IX_or_equip_check")
	public OperatingRoomEquipmentCheck getOrEquipCheck() {
		return orEquipCheck;
	}
	
	public void setOrEquipCheck(OperatingRoomEquipmentCheck orEquipCheck) {
		this.orEquipCheck = orEquipCheck;
	}

	private OperatingRoomSurgeryEquipment orSurgEquip;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="or_surg_equip")
	@ForeignKey(name="FK_or_surg_equip")
	@Index(name="IX_or_surg_equip")
	public OperatingRoomSurgeryEquipment getOrSurgEquip() {
		return orSurgEquip;
	}

	public void setOrSurgEquip(OperatingRoomSurgeryEquipment orSurgEquip) {
		this.orSurgEquip = orSurgEquip;
	}

	private OperatingRoomFinalCheck orCheckList;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="or_check_list")
	@ForeignKey(name="FK_or_check_list")
	@Index(name="IX_or_check_list")
	public OperatingRoomFinalCheck getOrCheckList() {
		return orCheckList;
	}

	public void setOrCheckList(OperatingRoomFinalCheck orCheckList) {
		this.orCheckList = orCheckList;
	}
	
	private SurgicalSecurityCheck surgSecurityCheck;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="surg_security_check")
	@ForeignKey(name="FK_surg_security_check")
	@Index(name="IX_surg_security_check")
	public SurgicalSecurityCheck getSurgSecurityCheck() {
		return surgSecurityCheck;
	}

	public void setSurgSecurityCheck(SurgicalSecurityCheck surgSecurityCheck) {
		this.surgSecurityCheck = surgSecurityCheck;
	}
	
	private SurgicalIdChecking surgIdChecking;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="surg_id_check")
	@ForeignKey(name="FK_surg_id_check")
	@Index(name="IX_surg_id_check")
	public SurgicalIdChecking getSurgIdChecking() {
		return surgIdChecking;
	}

	public void setSurgIdChecking(SurgicalIdChecking surgIdChecking) {
		this.surgIdChecking = surgIdChecking;
	}
	
	private List<SurgicalProcedure> procedure;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "surgicalIntervention")
	public List<SurgicalProcedure> getProcedure() {
		return procedure;
	}

	public void setProcedure(List<SurgicalProcedure> procedure) {
		this.procedure = procedure;
	}

}
