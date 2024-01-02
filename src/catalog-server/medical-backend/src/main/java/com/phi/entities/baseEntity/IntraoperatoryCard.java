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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.phi.entities.act.LisaSurgicalIntervention;
import com.phi.entities.act.VitalSign;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "intraoperatory_card")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class IntraoperatoryCard extends BaseEntity {

	private static final long serialVersionUID = 1665699207L;

	//methods needed for BaseEntity extension
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "IntraoperatoryCard_sequence")
	@SequenceGenerator(name = "IntraoperatoryCard_sequence", sequenceName = "IntraoperatoryCard_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  author
	*/
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_IntraoperatoryCard_author")
	@Index(name="IX_IntraoperatoryCard_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}
	
	/**
	*  confirmed
	*/
	private Boolean confirmed;

	@Column(name = "confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	
	/**
	*  confirmation date
	*/
	private Date availabilityTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="availability_time")
	public Date getAvailabilityTime(){
		return availabilityTime;
	}
	
	public void setAvailabilityTime(Date availabilityTime){
		this.availabilityTime = availabilityTime;
	}
	
	/**
	*  javadoc for surgicalIntervention
	*/
	private LisaSurgicalIntervention surgicalIntervention;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="surgical_intervention_id")
	@ForeignKey(name="FK_ntrprtryCrd_srgclntrvntn")
	@Index(name="IX_ntrprtryCrd_srgclntrvntn")
	//@NotAudited
	public LisaSurgicalIntervention getSurgicalIntervention(){
		return surgicalIntervention;
	}

	public void setSurgicalIntervention(LisaSurgicalIntervention surgicalIntervention){
		this.surgicalIntervention = surgicalIntervention;
	}


	/**
	*  javadoc for level
	*/
	private Integer level;

	@Column(name="level_db")
	public Integer getLevel(){
		return level;
	}

	public void setLevel(Integer level){
		this.level = level;
	}


	/**
	*  javadoc for vitalSign
	*/
	private List<VitalSign> vitalSign;

	@JsonManagedReference
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="intraoperatoryCard", cascade=CascadeType.PERSIST)
	public List<VitalSign> getVitalSign() {
		return vitalSign;
	}

	public void setVitalSign(List<VitalSign>list){
		vitalSign = list;
	}
	
	public void addVitalSign(VitalSign vitalSign) {
		if (this.vitalSign == null) {
			this.vitalSign = new ArrayList<VitalSign>();
		}
		// add the association
		if(!this.vitalSign.contains(vitalSign)) {
			this.vitalSign.add(vitalSign);
			// make the inverse link
			vitalSign.setIntraoperatoryCard(this);
		}
	}

	public void removeVitalSign(VitalSign vitalSign) {
		if (this.vitalSign == null) {
			this.vitalSign = new ArrayList<VitalSign>();
			return;
		}
		//add the association
		if(this.vitalSign.contains(vitalSign)){
			this.vitalSign.remove(vitalSign);
			//make the inverse link
			vitalSign.setIntraoperatoryCard(null);
		}
	}

	//Valori da graficare

	/**
	*  anesthesiaStop
	*/
	private Date anesthesiaStop;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="anesthesia_stop")
	public Date getAnesthesiaStop(){
		return anesthesiaStop;
	}

	public void setAnesthesiaStop(Date anesthesiaStop){
		this.anesthesiaStop = anesthesiaStop;
	}
	
	/**
	*  anesthesiaStop data inserimento
	*/
	private Date anesthesiaStopDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="anesthesia_stop_date")
	public Date getAnesthesiaStopDate(){
		return anesthesiaStopDate;
	}

	public void setAnesthesiaStopDate(Date anesthesiaStopDate){
		this.anesthesiaStopDate = anesthesiaStopDate;
	}

	/**
	*  anesthesiaStart
	*/
	private Date anesthesiaStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="anesthesia_start")
	public Date getAnesthesiaStart(){
		return anesthesiaStart;
	}

	public void setAnesthesiaStart(Date anesthesiaStart){
		this.anesthesiaStart = anesthesiaStart;
	}
	
	/**
	*  anesthesiaStart data inserimento
	*/
	private Date anesthesiaStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="anesthesia_start_date")
	public Date getAnesthesiaStartDate(){
		return anesthesiaStartDate;
	}

	public void setAnesthesiaStartDate(Date anesthesiaStartDate){
		this.anesthesiaStartDate = anesthesiaStartDate;
	}
	
	/**
	*  intubationStop
	*/
	private Date intubationStop;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="intubation_stop")
	public Date getIntubationStop(){
		return intubationStop;
	}

	public void setIntubationStop(Date intubationStop){
		this.intubationStop = intubationStop;
	}
	
	/**
	*  intubationStop data inserimento
	*/
	private Date intubationStopDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="intubation_stop_date")
	public Date getIntubationStopDate(){
		return intubationStopDate;
	}

	public void setIntubationStopDate(Date intubationStopDate){
		this.intubationStopDate = intubationStopDate;
	}

	/**
	*  intubationStart
	*/
	private Date intubationStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="intubation_start")
	public Date getIntubationStart(){
		return intubationStart;
	}

	public void setIntubationStart(Date intubationStart){
		this.intubationStart = intubationStart;
	}
	
	/**
	*  intubationStart data inserimento
	*/
	private Date intubationStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="intubation_start_date")
	public Date getIntubationStartDate(){
		return intubationStartDate;
	}

	public void setIntubationStartDate(Date intubationStartDate){
		this.intubationStartDate = intubationStartDate;
	}

	/**
	*  operationStop
	*/
	private Date operationStop;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="operation_stop")
	public Date getOperationStop(){
		return operationStop;
	}

	public void setOperationStop(Date operationStop){
		this.operationStop = operationStop;
	}
	
	/**
	*  operationStop data inserimento
	*/
	private Date operationStopDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="operation_stop_date")
	public Date getOperationStopDate(){
		return operationStopDate;
	}

	public void setOperationStopDate(Date operationStopDate){
		this.operationStopDate = operationStopDate;
	}

	/**
	*  operationStart
	*/
	private Date operationStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="operation_start")
	public Date getOperationStart(){
		return operationStart;
	}

	public void setOperationStart(Date operationStart){
		this.operationStart = operationStart;
	}
	
	/**
	*  operationStart data inserimento
	*/
	private Date operationStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="operation_start_date")
	public Date getOperationStartDate(){
		return operationStartDate;
	}

	public void setOperationStartDate(Date operationStartDate){
		this.operationStartDate = operationStartDate;
	}
	
	/**
	*  laccio inizio
	*/
	private Date laceStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lace_start")
	public Date getLaceStart(){
		return laceStart;
	}

	public void setLaceStart(Date laceStart){
		this.laceStart = laceStart;
	}
	
	/**
	*  laccio inizio data inserimento
	*/
	private Date laceStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lace_start_date")
	public Date getLaceStartDate(){
		return laceStartDate;
	}

	public void setLaceStartDate(Date laceStartDate){
		this.laceStartDate = laceStartDate;
	}
	
	/**
	*  laccio fine
	*/
	private Date laceStop;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lace_stop")
	public Date getLaceStop(){
		return laceStop;
	}

	public void setLaceStop(Date laceStop){
		this.laceStop = laceStop;
	}
	
	/**
	*  laccio fine data inserimento
	*/
	private Date laceStopDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lace_stop_date")
	public Date getLaceStopDate(){
		return laceStopDate;
	}

	public void setLaceStopDate(Date laceStopDate){
		this.laceStopDate = laceStopDate;
	}
	
	//Fine valori da graficare

	/**
	*  javadoc for needleAnesthesiaType
	*/
	private List<CodeValuePhi> needleAnesthesiaType;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="intraoperatory_card_needleAn", joinColumns = { @JoinColumn(name="IntraoperatoryCard_id") }, inverseJoinColumns = { @JoinColumn(name="needleAnesthesiaType") })
	@ForeignKey(name="FK_needleAnes_IntraopCard", inverseName="FK_IntraopCard_needleAnes")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getNeedleAnesthesiaType(){
		return needleAnesthesiaType;
	}

	public void setNeedleAnesthesiaType(List<CodeValuePhi> needleAnesthesiaType){
		this.needleAnesthesiaType = needleAnesthesiaType;
	}
	
	/**
	*  javadoc for localAnesthesiaType
	*/
	private CodeValuePhi localAnesthesiaType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="localAnesthesiaType")
	@ForeignKey(name="FK_IntrpCrd_lclAnsthTyp")
	@Index(name="FK_IntrpCrd_lclAnsthTyp")
	public CodeValuePhi getLocalAnesthesiaType(){
		return localAnesthesiaType;
	}

	public void setLocalAnesthesiaType(CodeValuePhi localAnesthesiaType){
		this.localAnesthesiaType = localAnesthesiaType;
	}

	/**
	*  javadoc for blockType
	*/
	private String blockType;

	@Column(name="block_type")
	public String getBlockType(){
		return blockType;
	}

	public void setBlockType(String blockType){
		this.blockType = blockType;
	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	@ForeignKey(name="FK_IntraoperatoryCard_type")
	@Index(name="IX_IntraoperatoryCard_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	*  javadoc for lMin
	*/
	private Integer lmin;

	@Column(name="l_min")
	public Integer getLmin(){
		return lmin;
	}

	public void setLmin(Integer lmin){
		this.lmin = lmin;
	}

	/**
	*  javadoc for circuit
	*/
	private CodeValuePhi circuit;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="circuit")
	@ForeignKey(name="FK_IntraoperatoryCard_circuit")
	@Index(name="IX_IntraoperatoryCard_circuit")
	public CodeValuePhi getCircuit(){
		return circuit;
	}

	public void setCircuit(CodeValuePhi circuit){
		this.circuit = circuit;
	}

	/**
	*  javadoc for ventilation
	*/
	private CodeValuePhi ventilation;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ventilation")
	@ForeignKey(name="FK_IntrOpCrd_vntltn")
	@Index(name="IX_FK_IntrOpCrd_vntltn")
	public CodeValuePhi getVentilation(){
		return ventilation;
	}

	public void setVentilation(CodeValuePhi ventilation){
		this.ventilation = ventilation;
	}

	/**
	*  javadoc for arterialCat
	*/
	private Boolean arterialCat;

	@Column(name="arterial_cat")
	public Boolean getArterialCat(){
		return arterialCat;
	}

	public void setArterialCat(Boolean arterialCat){
		this.arterialCat = arterialCat;
	}

	/**
	*  javadoc for mal
	*/
	private String mal;

	@Column(name="mal")
	public String getMal(){
		return mal;
	}

	public void setMal(String mal){
		this.mal = mal;
	}

	/**
	*  javadoc for mAr
	*/
	private String mar;

	@Column(name="mar")
	public String getMar(){
		return mar;
	}

	public void setMar(String mar){
		this.mar = mar;
	}

	/**
	*  javadoc for hzMs
	*/
	private String hzMs;

	@Column(name="hz_ms")
	public String getHzMs(){
		return hzMs;
	}

	public void setHzMs(String hzMs){
		this.hzMs = hzMs;
	}

	/**
	*  javadoc for catheter
	*/
	private String catheter;

	@Column(name="catheter")
	public String getCatheter(){
		return catheter;
	}

	public void setCatheter(String catheter){
		this.catheter = catheter;
	}

	/**
	*  javadoc for description
	*/
	private String description;

	@Column(name="description")
	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	/**
	*  javadoc for ntTubeType
	*/
	private String ntTubeType;

	@Column(name="nt_tube_type")
	public String getNtTubeType(){
		return ntTubeType;
	}

	public void setNtTubeType(String ntTubeType){
		this.ntTubeType = ntTubeType;
	}

	/**
	*  javadoc for ntTubeDiameter
	*/
	private String ntTubeDiameter;

	@Column(name="nt_tube_diameter")
	public String getNtTubeDiameter(){
		return ntTubeDiameter;
	}

	public void setNtTubeDiameter(String ntTubeDiameter){
		this.ntTubeDiameter = ntTubeDiameter;
	}

	/**
	*  javadoc for otTubeType
	*/
	private String otTubeType;

	@Column(name="ot_tube_type")
	public String getOtTubeType(){
		return otTubeType;
	}

	public void setOtTubeType(String otTubeType){
		this.otTubeType = otTubeType;
	}

	/**
	*  javadoc for otTubeDiameter
	*/
	private Integer otTubeDiameter;

	@Column(name="ot_tube_diameter")
	public Integer getOtTubeDiameter(){
		return otTubeDiameter;
	}

	public void setOtTubeDiameter(Integer otTubeDiameter){
		this.otTubeDiameter = otTubeDiameter;
	}

	/**
	*  javadoc for equipmentCheck
	*/
	private Boolean equipmentCheck;

	@Column(name="equipment_check")
	public Boolean getEquipmentCheck(){
		return equipmentCheck;
	}

	public void setEquipmentCheck(Boolean equipmentCheck){
		this.equipmentCheck = equipmentCheck;
	}

	/**
	*  javadoc for cvc
	*/
	private Boolean cvc;

	@Column(name="cvc")
	public Boolean getCvc(){
		return cvc;
	}

	public void setCvc(Boolean cvc){
		this.cvc = cvc;
	}

	/**
	*  javadoc for needle
	*/
	private Integer needle;

	@Column(name="needle")
	public Integer getNeedle(){
		return needle;
	}

	public void setNeedle(Integer needle){
		this.needle = needle;
	}

	/**
	*  javadoc for lmaNumber
	*/
	private Integer lmaNumber;

	@Column(name="lma_number")
	public Integer getLmaNumber(){
		return lmaNumber;
	}

	public void setLmaNumber(Integer lmaNumber){
		this.lmaNumber = lmaNumber;
	}

	/**
	*  javadoc for cannulaNumber
	*/
	private Integer cannulaNumber;

	@Column(name="cannula_number")
	public Integer getCannulaNumber(){
		return cannulaNumber;
	}

	public void setCannulaNumber(Integer cannulaNumber){
		this.cannulaNumber = cannulaNumber;
	}

	/**
	*  javadoc for maskNumber
	*/
	private Integer maskNumber;

	@Column(name="mask_number")
	public Integer getMaskNumber(){
		return maskNumber;
	}

	public void setMaskNumber(Integer maskNumber){
		this.maskNumber = maskNumber;
	}

	/**
	*  javadoc for anesthesia
	*/
	private CodeValuePhi anesthesia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="anesthesia")
	@ForeignKey(name="FK_IntrOpCrd_ansth")
	@Index(name="IX_IntrOpCrd_ansth")
	public CodeValuePhi getAnesthesia(){
		return anesthesia;
	}

	public void setAnesthesia(CodeValuePhi anesthesia){
		this.anesthesia = anesthesia;
	}

	/**
	*  javadoc for operation
	*/
	private String operation;

	@Column(name="operation")
	public String getOperation(){
		return operation;
	}

	public void setOperation(String operation){
		this.operation = operation;
	}

	/**
	*  javadoc for surgicalTeam
	*/
	private String surgicalTeam;

	@Column(name="surgical_team")
	public String getSurgicalTeam(){
		return surgicalTeam;
	}

	public void setSurgicalTeam(String surgicalTeam){
		this.surgicalTeam = surgicalTeam;
	}

	/**
	*  javadoc for posture
	*/
	private String posture;

	@Column(name="posture")
	public String getPosture(){
		return posture;
	}

	public void setPosture(String posture){
		this.posture = posture;
	}

	/**
	*  javadoc for date
	*/
	private Date cardDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="card_date")
	public Date getCardDate(){
		return cardDate;
	}

	public void setCardDate(Date cardDate){
		this.cardDate = cardDate;
	}


	/**
	*  javadoc for nurse
	*/
	private Employee nurse;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="nurse_id")
	@ForeignKey(name="FK_IntraoperatoryCard_nurse")
	@Index(name="IX_IntraoperatoryCard_nurse")
	public Employee getNurse(){
		return nurse;
	}

	public void setNurse(Employee nurse){
		this.nurse = nurse;
	}



	/**
	*  javadoc for anesthetist
	*/
	private Employee anesthetist;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="anesthetist_id")
	@ForeignKey(name="FK_ntropertryCrd_nesthetist")
	@Index(name="IX_ntropertryCrd_nesthetist")
	public Employee getAnesthetist(){
		return anesthetist;
	}

	public void setAnesthetist(Employee anesthetist){
		this.anesthetist = anesthetist;
	}

	
	
	//CUSTOM SERIES FOR GRAPH


	/**
	*  javadoc for measureOrIO1
	*/
	private String measureOrIO1;

	@Column(name="measure_or_io1")
	public String getMeasureOrIO1(){
		return measureOrIO1;
	}

	public void setMeasureOrIO1(String measureOrIO1){
		this.measureOrIO1 = measureOrIO1;
	}

	/**
	*  javadoc for measureOrIO2
	*/
	private String measureOrIO2;

	@Column(name="measure_or_io2")
	public String getMeasureOrIO2(){
		return measureOrIO2;
	}

	public void setMeasureOrIO2(String measureOrIO2){
		this.measureOrIO2 = measureOrIO2;
	}

	/**
	*  javadoc for measureOrIO3
	*/
	private String measureOrIO3;

	@Column(name="measure_or_io3")
	public String getMeasureOrIO3(){
		return measureOrIO3;
	}

	public void setMeasureOrIO3(String measureOrIO3){
		this.measureOrIO3 = measureOrIO3;
	}

	/**
	*  javadoc for measureOrIO4
	*/
	private String measureOrIO4;

	@Column(name="measure_or_io4")
	public String getMeasureOrIO4(){
		return measureOrIO4;
	}

	public void setMeasureOrIO4(String measureOrIO4){
		this.measureOrIO4 = measureOrIO4;
	}

	/**
	*  javadoc for measureOrIO5
	*/
	private String measureOrIO5;

	@Column(name="measure_or_io5")
	public String getMeasureOrIO5(){
		return measureOrIO5;
	}

	public void setMeasureOrIO5(String measureOrIO5){
		this.measureOrIO5 = measureOrIO5;
	}

	/**
	*  javadoc for measureOrIO6
	*/
	private String measureOrIO6;

	@Column(name="measure_or_io6")
	public String getMeasureOrIO6(){
		return measureOrIO6;
	}

	public void setMeasureOrIO6(String measureOrIO6){
		this.measureOrIO6 = measureOrIO6;
	}


	/**
	*  javadoc for medicine1
	*/
	private Medicine medicine1;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine1_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine1")
	@Index(name="IX_ntropertoryCrd_medicine1")
	public Medicine getMedicine1(){
		return medicine1;
	}

	public void setMedicine1(Medicine medicine1){
		this.medicine1 = medicine1;
	}



	/**
	*  javadoc for medicine2
	*/
	private Medicine medicine2;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine2_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine2")
	@Index(name="IX_ntropertoryCrd_medicine2")
	public Medicine getMedicine2(){
		return medicine2;
	}

	public void setMedicine2(Medicine medicine2){
		this.medicine2 = medicine2;
	}



	/**
	*  javadoc for medicine3
	*/
	private Medicine medicine3;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine3_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine3")
	@Index(name="IX_ntropertoryCrd_medicine3")
	public Medicine getMedicine3(){
		return medicine3;
	}

	public void setMedicine3(Medicine medicine3){
		this.medicine3 = medicine3;
	}
	
	
	
	/**
	*  javadoc for medicine4
	*/
	private Medicine medicine4;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine4_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine4")
	@Index(name="IX_ntropertoryCrd_medicine4")
	public Medicine getMedicine4(){
		return medicine4;
	}

	public void setMedicine4(Medicine medicine4){
		this.medicine4 = medicine4;
	}



	/**
	*  javadoc for medicine5
	*/
	private Medicine medicine5;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine5_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine5")
	@Index(name="IX_ntropertoryCrd_medicine5")
	public Medicine getMedicine5(){
		return medicine5;
	}

	public void setMedicine5(Medicine medicine5){
		this.medicine5 = medicine5;
	}



	/**
	*  javadoc for medicine6
	*/
	private Medicine medicine6;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medicine6_id")
	@ForeignKey(name="FK_ntropertoryCrd_medicine6")
	@Index(name="IX_ntropertoryCrd_medicine6")
	public Medicine getMedicine6(){
		return medicine6;
	}

	public void setMedicine6(Medicine medicine6){
		this.medicine6 = medicine6;
	}
	
	//WATER BALANCE
	
	/**
	*  javadoc for waterBalance
	*/
	private Integer waterBalance;

	@Column(name="water_balance")
	public Integer getWaterBalance(){
		return waterBalance;
	}

	public void setWaterBalance(Integer waterBalance){
		this.waterBalance = waterBalance;
	}

	/**
	*  javadoc for perspiratio
	*/
	private Integer perspiratio;

	@Column(name="perspiratio")
	public Integer getPerspiratio(){
		return perspiratio;
	}

	public void setPerspiratio(Integer perspiratio){
		this.perspiratio = perspiratio;
	}

	/**
	*  javadoc for other
	*/
	private Integer other;

	@Column(name="other")
	public Integer getOther(){
		return other;
	}

	public void setOther(Integer other){
		this.other = other;
	}

	/**
	*  javadoc for sng
	*/
	private Integer sng;

	@Column(name="sng")
	public Integer getSng(){
		return sng;
	}

	public void setSng(Integer sng){
		this.sng = sng;
	}

	/**
	*  javadoc for plasma
	*/
	private Integer plasma;

	@Column(name="plasma")
	public Integer getPlasma(){
		return plasma;
	}

	public void setPlasma(Integer plasma){
		this.plasma = plasma;
	}

	/**
	*  javadoc for hematicLoss
	*/
	private Integer hematicLoss;

	@Column(name="hematic_loss")
	public Integer getHematicLoss(){
		return hematicLoss;
	}

	public void setHematicLoss(Integer hematicLoss){
		this.hematicLoss = hematicLoss;
	}

	/**
	*  javadoc for emazie
	*/
	private Integer emazie;

	@Column(name="emazie")
	public Integer getEmazie(){
		return emazie;
	}

	public void setEmazie(Integer emazie){
		this.emazie = emazie;
	}


	/**
	*  javadoc for diuresis
	*/
	private Integer diuresis;

	@Column(name="diuresis")
	public Integer getDiuresis(){
		return diuresis;
	}

	public void setDiuresis(Integer diuresis){
		this.diuresis = diuresis;
	}

	/**
	*  javadoc for infusion
	*/
	private Integer infusion;

	@Column(name="infusion")
	public Integer getInfusion(){
		return infusion;
	}

	public void setInfusion(Integer infusion){
		this.infusion = infusion;
	}
	
	/**
	*  Cormack Lehane scale
	*/
	private CodeValuePhi cormackLehane;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cormackLehane")
	@ForeignKey(name="FK_ntropertoryCrd_crmckLhn")
	@Index(name="IX_ntropertoryCrd_crmckLhn")
	public CodeValuePhi getCormackLehane(){
		return cormackLehane;
	}

	public void setCormackLehane(CodeValuePhi cormackLehane){
		this.cormackLehane = cormackLehane;
	}

	
}
