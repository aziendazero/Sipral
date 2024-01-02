package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.act.LisaSurgicalIntervention;
import com.phi.entities.act.NotePostOperativeCard;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
@javax.persistence.Entity
@Table(name = "post_operative_card")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PostOperativeCard extends AuditedEntity {

	private static final long serialVersionUID = 583461206L;
	/**
	*  javadoc for enterDate
	*/
	private Date enterDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="enter_date")
	public Date getEnterDate(){
		return enterDate;
	}

	public void setEnterDate(Date enterDate){
		this.enterDate = enterDate;
	}

	/**
	*  javadoc for lisaSurgInt
	*/
	private LisaSurgicalIntervention lisaSurgInt;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="lisa_surg_int_id")
	@ForeignKey(name="FK_PostOpCard_lisaSurgInt")
	@Index(name="IX_PostOpCard_lisaSurgInt")
	@NotAudited
	public LisaSurgicalIntervention getLisaSurgInt(){
		return lisaSurgInt;
	}

	public void setLisaSurgInt(LisaSurgicalIntervention lisaSurgInt){
		this.lisaSurgInt = lisaSurgInt;
	}

	/**
	*  javadoc for postOperativeExtendedInfo
	*/
	private PostOperativeExtendedInfo postOperativeExtendedInfo;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="post_op_ext_info_id")
	@ForeignKey(name="FK_PostOpCard_extInf")
	@Index(name="IX_PostOpCard_extInf")
	public PostOperativeExtendedInfo getPostOperativeExtendedInfo(){
		return postOperativeExtendedInfo;
	}

	public void setPostOperativeExtendedInfo(PostOperativeExtendedInfo postOperativeExtendedInfo){
		this.postOperativeExtendedInfo = postOperativeExtendedInfo;
	}



	/**
	*  javadoc for notePostOperativeCard
	*/
	private List<NotePostOperativeCard> notePostOperativeCard;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="postOperativeCard", cascade=CascadeType.PERSIST)
	public List<NotePostOperativeCard> getNotePostOperativeCard(){
		return notePostOperativeCard;
	}

	public void setNotePostOperativeCard(List<NotePostOperativeCard> list){
		notePostOperativeCard = list;
	}

	public void addNotePostOperativeCard(NotePostOperativeCard notePostOperativeCard) {
		if (this.notePostOperativeCard == null) {
			this.notePostOperativeCard = new ArrayList<NotePostOperativeCard>();
		}
		// add the association
		if(!this.notePostOperativeCard.contains(notePostOperativeCard)) {
			this.notePostOperativeCard.add(notePostOperativeCard);
			// make the inverse link
			notePostOperativeCard.setPostOperativeCard(this);
		}
	}

	public void removeNotePostOperativeCard(NotePostOperativeCard notePostOperativeCard) {
		if (this.notePostOperativeCard == null) {
			this.notePostOperativeCard = new ArrayList<NotePostOperativeCard>();
			return;
		}
		//add the association
		if(this.notePostOperativeCard.contains(notePostOperativeCard)){
			this.notePostOperativeCard.remove(notePostOperativeCard);
			//make the inverse link
			notePostOperativeCard.setPostOperativeCard(null);
		}

	}


	/**
	*  javadoc for entranceDate
	*/
	private Date entranceDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="entrance_date")
	public Date getEntranceDate(){
		return entranceDate;
	}

	public void setEntranceDate(Date entranceDate){
		this.entranceDate = entranceDate;
	}
	
	/**
	*  javadoc for exitDate
	*/
	private Date exitDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="exit_date")
	public Date getExitDate(){
		return exitDate;
	}

	public void setExitDate(Date exitDate){
		this.exitDate = exitDate;
	}

	/**
	*  javadoc for saO21
	*/
	private CodeValuePhi saO21;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="saO21")
	@ForeignKey(name="FK_PostOperativeCard_saO21")
	@Index(name="IX_PostOperativeCard_saO21")
	public CodeValuePhi getSaO21(){
		return saO21;
	}

	public void setSaO21(CodeValuePhi saO21){
		this.saO21 = saO21;
	}

	/**
	*  javadoc for total4
	*/
	private String total4;

	@Column(name="total4")
	public String getTotal4(){
		return total4;
	}

	public void setTotal4(String total4){
		this.total4 = total4;
	}

	/**
	*  javadoc for total3
	*/
	private String total3;

	@Column(name="total3")
	public String getTotal3(){
		return total3;
	}

	public void setTotal3(String total3){
		this.total3 = total3;
	}

	/**
	*  javadoc for total2
	*/
	private String total2;

	@Column(name="total2")
	public String getTotal2(){
		return total2;
	}

	public void setTotal2(String total2){
		this.total2 = total2;
	}

	/**
	*  javadoc for total1
	*/
	private String total1;

	@Column(name="total1")
	public String getTotal1(){
		return total1;
	}

	public void setTotal1(String total1){
		this.total1 = total1;
	}

	/**
	*  javadoc for total
	*/
	private String total;

	@Column(name="total")
	public String getTotal(){
		return total;
	}

	public void setTotal(String total){
		this.total = total;
	}

	/**
	*  javadoc for motoricActivity4
	*/
	private CodeValuePhi motoricActivity4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motoricActivity4")
	@ForeignKey(name="FK_PostOpCard_motAct4")
	@Index(name="IX_PostOpCard_motAct4")
	public CodeValuePhi getMotoricActivity4(){
		return motoricActivity4;
	}

	public void setMotoricActivity4(CodeValuePhi motoricActivity4){
		this.motoricActivity4 = motoricActivity4;
	}

	/**
	*  javadoc for motoricActivity3
	*/
	private CodeValuePhi motoricActivity3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motoricActivity3")
	@ForeignKey(name="FK_PostOpCard_motAct3")
	@Index(name="IX_PostOpCard_motAct3")
	public CodeValuePhi getMotoricActivity3(){
		return motoricActivity3;
	}

	public void setMotoricActivity3(CodeValuePhi motoricActivity3){
		this.motoricActivity3 = motoricActivity3;
	}

	/**
	*  javadoc for motoricActivity2
	*/
	private CodeValuePhi motoricActivity2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motoricActivity2")
	@ForeignKey(name="FK_PostOpCard_motAct2")
	@Index(name="IX_PostOpCard_motcAct2")
	public CodeValuePhi getMotoricActivity2(){
		return motoricActivity2;
	}

	public void setMotoricActivity2(CodeValuePhi motoricActivity2){
		this.motoricActivity2 = motoricActivity2;
	}

	/**
	*  javadoc for motoricActivity1
	*/
	private CodeValuePhi motoricActivity1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motoricActivity1")
	@ForeignKey(name="FK_PostOpCard_motAct1")
	@Index(name="IX_PostOpCard_motAct1")
	public CodeValuePhi getMotoricActivity1(){
		return motoricActivity1;
	}

	public void setMotoricActivity1(CodeValuePhi motoricActivity1){
		this.motoricActivity1 = motoricActivity1;
	}

	/**
	*  javadoc for motoricActivity
	*/
	private CodeValuePhi motoricActivity;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motoricActivity")
	@ForeignKey(name="FK_PostOpCard_motcAct")
	@Index(name="IX_PostOpCard_motAct")
	public CodeValuePhi getMotoricActivity(){
		return motoricActivity;
	}

	public void setMotoricActivity(CodeValuePhi motoricActivity){
		this.motoricActivity = motoricActivity;
	}

	/**
	*  javadoc for pas4
	*/
	private CodeValuePhi pas4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pas4")
	@ForeignKey(name="FK_PostOCard_pas4")
	@Index(name="IX_PostOpCard_pas4")
	public CodeValuePhi getPas4(){
		return pas4;
	}

	public void setPas4(CodeValuePhi pas4){
		this.pas4 = pas4;
	}

	/**
	*  javadoc for pas3
	*/
	private CodeValuePhi pas3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pas3")
	@ForeignKey(name="FK_PostOpCard_pas3")
	@Index(name="IX_PostOpCard_pas3")
	public CodeValuePhi getPas3(){
		return pas3;
	}

	public void setPas3(CodeValuePhi pas3){
		this.pas3 = pas3;
	}

	/**
	*  javadoc for pas2
	*/
	private CodeValuePhi pas2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pas2")
	@ForeignKey(name="FK_PostOpCard_pas2")
	@Index(name="IX_PostOpCard_pas2")
	public CodeValuePhi getPas2(){
		return pas2;
	}

	public void setPas2(CodeValuePhi pas2){
		this.pas2 = pas2;
	}

	/**
	*  javadoc for pas1
	*/
	private CodeValuePhi pas1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pas1")
	@ForeignKey(name="FK_PostOpCard_pas1")
	@Index(name="IX_PostOpCard_pas1")
	public CodeValuePhi getPas1(){
		return pas1;
	}

	public void setPas1(CodeValuePhi pas1){
		this.pas1 = pas1;
	}

	/**
	*  javadoc for pas
	*/
	private CodeValuePhi pas;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pas")
	@ForeignKey(name="FK_PostOpCard_pas")
	@Index(name="IX_PostOpCard_pas")
	public CodeValuePhi getPas(){
		return pas;
	}

	public void setPas(CodeValuePhi pas){
		this.pas = pas;
	}

	/**
	*  javadoc for saO24
	*/
	private CodeValuePhi saO24;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="saO24")
	@ForeignKey(name="FK_PostOpCard_saO24")
	@Index(name="IX_PostOpCard_saO24")
	public CodeValuePhi getSaO24(){
		return saO24;
	}

	public void setSaO24(CodeValuePhi saO24){
		this.saO24 = saO24;
	}

	/**
	*  javadoc for saO23
	*/
	private CodeValuePhi saO23;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="saO23")
	@ForeignKey(name="FK_PostOpCard_saO23")
	@Index(name="IX_PostOpCard_saO23")
	public CodeValuePhi getSaO23(){
		return saO23;
	}

	public void setSaO23(CodeValuePhi saO23){
		this.saO23 = saO23;
	}

	/**
	*  javadoc for saO22
	*/
	private CodeValuePhi saO22;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="saO22")
	@ForeignKey(name="FK_PostOpCard_saO22")
	@Index(name="IX_PostOpCard_saO22")
	public CodeValuePhi getSaO22(){
		return saO22;
	}

	public void setSaO22(CodeValuePhi saO22){
		this.saO22 = saO22;
	}

	/**
	*  javadoc for saO2
	*/
	private CodeValuePhi saO2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="saO2")
	@ForeignKey(name="FK_PostOpCard_saO2")
	@Index(name="IX_PostOpCard_saO2")
	public CodeValuePhi getSaO2(){
		return saO2;
	}

	public void setSaO2(CodeValuePhi saO2){
		this.saO2 = saO2;
	}

	/**
	*  javadoc for respiratory4
	*/
	private CodeValuePhi respiratory4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="respiratory4")
	@ForeignKey(name="FK_PostOpCard_resp4")
	@Index(name="IX_PostOpCard_resp4")
	public CodeValuePhi getRespiratory4(){
		return respiratory4;
	}

	public void setRespiratory4(CodeValuePhi respiratory4){
		this.respiratory4 = respiratory4;
	}

	/**
	*  javadoc for respiratory3
	*/
	private CodeValuePhi respiratory3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="respiratory3")
	@ForeignKey(name="FK_PostOpCard_resp3")
	@Index(name="IX_PostOpCard_resp3")
	public CodeValuePhi getRespiratory3(){
		return respiratory3;
	}

	public void setRespiratory3(CodeValuePhi respiratory3){
		this.respiratory3 = respiratory3;
	}

	/**
	*  javadoc for respiratory2
	*/
	private CodeValuePhi respiratory2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="respiratory2")
	@ForeignKey(name="FK_PostOpCard_resp2")
	@Index(name="IX_PostOpCard_resp2")
	public CodeValuePhi getRespiratory2(){
		return respiratory2;
	}

	public void setRespiratory2(CodeValuePhi respiratory2){
		this.respiratory2 = respiratory2;
	}

	/**
	*  javadoc for respiratory1
	*/
	private CodeValuePhi respiratory1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="respiratory1")
	@ForeignKey(name="FK_PostOpCard_resp1")
	@Index(name="IX_PostOpCard_resp1")
	public CodeValuePhi getRespiratory1(){
		return respiratory1;
	}

	public void setRespiratory1(CodeValuePhi respiratory1){
		this.respiratory1 = respiratory1;
	}

	/**
	*  javadoc for respiratory
	*/
	private CodeValuePhi respiratory;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="respiratory")
	@ForeignKey(name="FK_PostOpCard_resp")
	@Index(name="IX_PostOpCard_resp")
	public CodeValuePhi getRespiratory(){
		return respiratory;
	}

	public void setRespiratory(CodeValuePhi respiratory){
		this.respiratory = respiratory;
	}

	/**
	*  javadoc for consciousness4
	*/
	private CodeValuePhi consciousness4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consciousness4")
	@ForeignKey(name="FK_PostOpCard_conscious4")
	@Index(name="IX_PostOpCard_conscious4")
	public CodeValuePhi getConsciousness4(){
		return consciousness4;
	}

	public void setConsciousness4(CodeValuePhi consciousness4){
		this.consciousness4 = consciousness4;
	}

	/**
	*  javadoc for consciousness3
	*/
	private CodeValuePhi consciousness3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consciousness3")
	@ForeignKey(name="FK_PostOpCard_conscious3")
	@Index(name="IX_PostOpCard_conscious3")
	public CodeValuePhi getConsciousness3(){
		return consciousness3;
	}

	public void setConsciousness3(CodeValuePhi consciousness3){
		this.consciousness3 = consciousness3;
	}

	/**
	*  javadoc for consciousness1
	*/
	private CodeValuePhi consciousness1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consciousness1")
	@ForeignKey(name="FK_PostOpCard_conscious1")
	@Index(name="IX_PostOpCard_conscious1")
	public CodeValuePhi getConsciousness1(){
		return consciousness1;
	}

	public void setConsciousness1(CodeValuePhi consciousness1){
		this.consciousness1 = consciousness1;
	}

	/**
	*  javadoc for consciousness2
	*/
	private CodeValuePhi consciousness2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consciousness2")
	@ForeignKey(name="FK_PostOpCard_conscious2")
	@Index(name="IX_PostOCard_conscious2")
	public CodeValuePhi getConsciousness2(){
		return consciousness2;
	}

	public void setConsciousness2(CodeValuePhi consciousness2){
		this.consciousness2 = consciousness2;
	}

	/**
	*  javadoc for consciousness
	*/
	private CodeValuePhi consciousness;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consciousness")
	@ForeignKey(name="FK_PostOpCard_conscious")
	@Index(name="IX_PostOpCard_conscious")
	public CodeValuePhi getConsciousness(){
		return consciousness;
	}

	public void setConsciousness(CodeValuePhi consciousness){
		this.consciousness = consciousness;
	}
	
	/**
	*  javadoc for patientEncounter
	*/
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_encounter_id")
	@ForeignKey(name="FK_PostOpCard_Enc")
	@Index(name="IX_PostOpCard_Enc")
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
	@ForeignKey(name="FK_PostOpCard_patient")
	@Index(name="IX_PostOpCard_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}


	/**
	*  javadoc for complicance4
	*/
	private CodeValuePhi complicance4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complicance4")
	@ForeignKey(name="FK_PostOpCard_complic4")
	@Index(name="IX_PostOpCard_complic4")
	public CodeValuePhi getComplicance4(){
		return complicance4;
	}

	public void setComplicance4(CodeValuePhi complicance4){
		this.complicance4 = complicance4;
	}

	/**
	*  javadoc for complicance3
	*/
	private CodeValuePhi complicance3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complicance3")
	@ForeignKey(name="FK_PostOpCard_complic3")
	@Index(name="IX_PostOpCard_complic3")
	public CodeValuePhi getComplicance3(){
		return complicance3;
	}

	public void setComplicance3(CodeValuePhi complicance3){
		this.complicance3 = complicance3;
	}

	/**
	*  javadoc for complicance2
	*/
	private CodeValuePhi complicance2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complicance2")
	@ForeignKey(name="FK_PostOpCard_complic2")
	@Index(name="IX_PostOpCard_complic2")
	public CodeValuePhi getComplicance2(){
		return complicance2;
	}

	public void setComplicance2(CodeValuePhi complicance2){
		this.complicance2 = complicance2;
	}

	/**
	*  javadoc for complicance1
	*/
	private CodeValuePhi complicance1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complicance1")
	@ForeignKey(name="FK_PostOpCard_complic1")
	@Index(name="IX_PostOpCard_complic1")
	public CodeValuePhi getComplicance1(){
		return complicance1;
	}

	public void setComplicance1(CodeValuePhi complicance1){
		this.complicance1 = complicance1;
	}

	/**
	*  javadoc for complicance
	*/
	private CodeValuePhi complicance;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="complicance")
	@ForeignKey(name="FK_PostOpCard_complic")
	@Index(name="IX_PostOpCard_complic")
	public CodeValuePhi getComplicance(){
		return complicance;
	}

	public void setComplicance(CodeValuePhi complicance){
		this.complicance = complicance;
	}

	/**
	*  javadoc for bromageScore4
	*/
	private CodeValuePhi bromageScore4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bromageScore4")
	@ForeignKey(name="FK_PostOpCard_bromageScr4")
	@Index(name="IX_PostOpCard_bromageScr4")
	public CodeValuePhi getBromageScore4(){
		return bromageScore4;
	}

	public void setBromageScore4(CodeValuePhi bromageScore4){
		this.bromageScore4 = bromageScore4;
	}

	/**
	*  javadoc for bromageScore3
	*/
	private CodeValuePhi bromageScore3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bromageScore3")
	@ForeignKey(name="FK_PostOpCard_bromageScr3")
	@Index(name="IX_PostOpCard_bromageScr3")
	public CodeValuePhi getBromageScore3(){
		return bromageScore3;
	}

	public void setBromageScore3(CodeValuePhi bromageScore3){
		this.bromageScore3 = bromageScore3;
	}


	/**
	*  javadoc for bromageScore2
	*/
	private CodeValuePhi bromageScore2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bromageScore2")
	@ForeignKey(name="FK_PostOpCard_bromageScr2")
	@Index(name="IX_PostOpCard_bromageScr2")
	public CodeValuePhi getBromageScore2(){
		return bromageScore2;
	}

	public void setBromageScore2(CodeValuePhi bromageScore2){
		this.bromageScore2 = bromageScore2;
	}

	/**
	*  javadoc for bromageScore1
	*/
	private CodeValuePhi bromageScore1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bromageScore1")
	@ForeignKey(name="FK_PostOpCard_bromageScr1")
	@Index(name="IX_PostOpCard_bromageScr1")
	public CodeValuePhi getBromageScore1(){
		return bromageScore1;
	}

	public void setBromageScore1(CodeValuePhi bromageScore1){
		this.bromageScore1 = bromageScore1;
	}

	/**
	*  javadoc for bromageScore
	*/
	private CodeValuePhi bromageScore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bromageScore")
	@ForeignKey(name="FK_PostOpCard_bromageScr")
	@Index(name="IX_PostOpCard_bromageScr")
	public CodeValuePhi getBromageScore(){
		return bromageScore;
	}

	public void setBromageScore(CodeValuePhi bromageScore){
		this.bromageScore = bromageScore;
	}

	/**
	*  javadoc for analgesicEvalutaion4
	*/
	private String analgesicEvalutaion4;

	@Column(name="analgesic_evalutaion4")
	public String getAnalgesicEvalutaion4(){
		return analgesicEvalutaion4;
	}

	public void setAnalgesicEvalutaion4(String analgesicEvalutaion4){
		this.analgesicEvalutaion4 = analgesicEvalutaion4;
	}

	/**
	*  javadoc for analgesicEvalutaion2
	*/
	private String analgesicEvalutaion2;

	@Column(name="analgesic_evalutaion2")
	public String getAnalgesicEvalutaion2(){
		return analgesicEvalutaion2;
	}

	public void setAnalgesicEvalutaion2(String analgesicEvalutaion2){
		this.analgesicEvalutaion2 = analgesicEvalutaion2;
	}

	/**
	*  javadoc for analgesicEvalutaion3
	*/
	private String analgesicEvalutaion3;

	@Column(name="analgesic_evalutaion3")
	public String getAnalgesicEvalutaion3(){
		return analgesicEvalutaion3;
	}

	public void setAnalgesicEvalutaion3(String analgesicEvalutaion3){
		this.analgesicEvalutaion3 = analgesicEvalutaion3;
	}

	/**
	*  javadoc for analgesicEvalutaion1
	*/
	private String analgesicEvalutaion1;

	@Column(name="analgesic_evalutaion1")
	public String getAnalgesicEvalutaion1(){
		return analgesicEvalutaion1;
	}

	public void setAnalgesicEvalutaion1(String analgesicEvalutaion1){
		this.analgesicEvalutaion1 = analgesicEvalutaion1;
	}

	/**
	*  javadoc for analgesicEvalutaion
	*/
	private String analgesicEvalutaion;

	@Column(name="analgesic_evalutaion")
	public String getAnalgesicEvalutaion(){
		return analgesicEvalutaion;
	}

	public void setAnalgesicEvalutaion(String analgesicEvalutaion){
		this.analgesicEvalutaion = analgesicEvalutaion;
	}

	/**
	*  javadoc for drainage4
	*/
	private String drainage4;

	@Column(name="drainage4")
	public String getDrainage4(){
		return drainage4;
	}

	public void setDrainage4(String drainage4){
		this.drainage4 = drainage4;
	}

	/**
	*  javadoc for drainage3
	*/
	private String drainage3;

	@Column(name="drainage3")
	public String getDrainage3(){
		return drainage3;
	}

	public void setDrainage3(String drainage3){
		this.drainage3 = drainage3;
	}

	/**
	*  javadoc for drainage2
	*/
	private String drainage2;

	@Column(name="drainage2")
	public String getDrainage2(){
		return drainage2;
	}

	public void setDrainage2(String drainage2){
		this.drainage2 = drainage2;
	}

	/**
	*  javadoc for drainage1
	*/
	private String drainage1;

	@Column(name="drainage1")
	public String getDrainage1(){
		return drainage1;
	}

	public void setDrainage1(String drainage1){
		this.drainage1 = drainage1;
	}

	/**
	*  javadoc for drainage
	*/
	private String drainage;

	@Column(name="drainage")
	public String getDrainage(){
		return drainage;
	}

	public void setDrainage(String drainage){
		this.drainage = drainage;
	}

	/**
	*  javadoc for diuresis4
	*/
	private String diuresis4;

	@Column(name="diuresis4")
	public String getDiuresis4(){
		return diuresis4;
	}

	public void setDiuresis4(String diuresis4){
		this.diuresis4 = diuresis4;
	}

	/**
	*  javadoc for diuresis3
	*/
	private String diuresis3;

	@Column(name="diuresis3")
	public String getDiuresis3(){
		return diuresis3;
	}

	public void setDiuresis3(String diuresis3){
		this.diuresis3 = diuresis3;
	}

	/**
	*  javadoc for diuresis2
	*/
	private String diuresis2;

	@Column(name="diuresis2")
	public String getDiuresis2(){
		return diuresis2;
	}

	public void setDiuresis2(String diuresis2){
		this.diuresis2 = diuresis2;
	}

	/**
	*  javadoc for diuresis1
	*/
	private String diuresis1;

	@Column(name="diuresis1")
	public String getDiuresis1(){
		return diuresis1;
	}

	public void setDiuresis1(String diuresis1){
		this.diuresis1 = diuresis1;
	}

	/**
	*  javadoc for diuresis
	*/
	private String diuresis;

	@Column(name="diuresis")
	public String getDiuresis(){
		return diuresis;
	}

	public void setDiuresis(String diuresis){
		this.diuresis = diuresis;
	}

	/**
	*  javadoc for paFc4
	*/
	private String paFc4;

	@Column(name="pa_fc4")
	public String getPaFc4(){
		return paFc4;
	}

	public void setPaFc4(String paFc4){
		this.paFc4 = paFc4;
	}

	/**
	*  javadoc for paFc3
	*/
	private String paFc3;

	@Column(name="pa_fc3")
	public String getPaFc3(){
		return paFc3;
	}

	public void setPaFc3(String paFc3){
		this.paFc3 = paFc3;
	}

	/**
	*  javadoc for paFc2
	*/
	private String paFc2;

	@Column(name="pa_fc2")
	public String getPaFc2(){
		return paFc2;
	}

	public void setPaFc2(String paFc2){
		this.paFc2 = paFc2;
	}

	/**
	*  javadoc for paFc1
	*/
	private String paFc1;

	@Column(name="pa_fc1")
	public String getPaFc1(){
		return paFc1;
	}

	public void setPaFc1(String paFc1){
		this.paFc1 = paFc1;
	}

	/**
	*  javadoc for paFC
	*/
	private String paFC;

	@Column(name="pa_fc")
	public String getPaFC(){
		return paFC;
	}

	public void setPaFC(String paFC){
		this.paFC = paFC;
	}

	/**
	*  javadoc for frequency
	*/
	private String frequency;

	@Column(name="frequency")
	public String getFrequency(){
		return frequency;
	}

	public void setFrequency(String frequency){
		this.frequency = frequency;
	}
	
	/**
	*  javadoc for frequency1
	*/
	private String frequency1;

	@Column(name="frequency1")
	public String getFrequency1(){
		return frequency1;
	}

	public void setFrequency1(String frequency1){
		this.frequency1 = frequency1;
	}
	
	/**
	*  javadoc for frequency
	*/
	private String frequency2;

	@Column(name="frequency2")
	public String getFrequency2(){
		return frequency2;
	}

	public void setFrequency2(String frequency2){
		this.frequency2 = frequency2;
	}
	
	/**
	*  javadoc for frequency
	*/
	private String frequency3;

	@Column(name="frequency3")
	public String getFrequency3(){
		return frequency3;
	}

	public void setFrequency3(String frequency3){
		this.frequency3 = frequency3;
	}
	
	/**
	*  javadoc for frequency
	*/
	private String frequency4;

	@Column(name="frequency4")
	public String getFrequency4(){
		return frequency4;
	}

	public void setFrequency4(String frequency4){
		this.frequency4 = frequency4;
	}

	/**
	*  javadoc for ventilationEntrance4
	*/
	private CodeValuePhi ventilationEntrance4;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ventEntrance4")
	@ForeignKey(name="FK_PostOpCard_ventEntrance4")
	@Index(name="IX_PostOpCard_ventEntrance4")
	public CodeValuePhi getVentilationEntrance4(){
		return ventilationEntrance4;
	}

	public void setVentilationEntrance4(CodeValuePhi ventilationEntrance4){
		this.ventilationEntrance4 = ventilationEntrance4;
	}

	/**
	*  javadoc for ventilationEntrance3
	*/
	private CodeValuePhi ventilationEntrance3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ventEntrance3")
	@ForeignKey(name="FK_PostOpCard_ventEntrance3")
	@Index(name="IX_PostOpCard_ventEntrance3")
	public CodeValuePhi getVentilationEntrance3(){
		return ventilationEntrance3;
	}

	public void setVentilationEntrance3(CodeValuePhi ventilationEntrance3){
		this.ventilationEntrance3 = ventilationEntrance3;
	}

	/**
	*  javadoc for ventilationEntrance2
	*/
	private CodeValuePhi ventilationEntrance2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ventEntrance2")
	@ForeignKey(name="FK_PostOpCard_ventEntrance2")
	@Index(name="IX_PostOpCard_ventEntrance2")
	public CodeValuePhi getVentilationEntrance2(){
		return ventilationEntrance2;
	}

	public void setVentilationEntrance2(CodeValuePhi ventilationEntrance2){
		this.ventilationEntrance2 = ventilationEntrance2;
	}

	/**
	*  javadoc for ventilationEntrance1
	*/
	private CodeValuePhi ventilationEntrance1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="venEntrance1")
	@ForeignKey(name="FK_PostOpCard_ventEntrance1")
	@Index(name="IX_PostOpCard_ventEntrance1")
	public CodeValuePhi getVentilationEntrance1(){
		return ventilationEntrance1;
	}

	public void setVentilationEntrance1(CodeValuePhi ventilationEntrance1){
		this.ventilationEntrance1 = ventilationEntrance1;
	}

	/**
	*  javadoc for ventilationEntrance
	*/
	private CodeValuePhi ventilationEntrance;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ventEntrance")
	@ForeignKey(name="FK_PostOpCard_ventEntrance")
	@Index(name="IX_PostOpCard_ventEntrance")
	public CodeValuePhi getVentilationEntrance(){
		return ventilationEntrance;
	}

	public void setVentilationEntrance(CodeValuePhi ventilationEntrance){
		this.ventilationEntrance = ventilationEntrance;
	}
	
	
	//methods needed for BaseEntity extension
	
	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_PostOpCard_sdloc")
	@Index(name = "IX_PostOpCard_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PostOpCard_sequence")
	@SequenceGenerator(name = "PostOpCard_sequence", sequenceName = "PostOpCard_sequence")
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
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_PostOpCard_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_PostOpCard_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_PostOpeCard_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_PostOpCard_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_PostOpCard_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_PostOpCard_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_PostOpCard_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_PostOpCard_author")
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
	@ForeignKey(name="FK_PostOpCard_lastAth")
	@Index(name="IX_PostOpCard_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
