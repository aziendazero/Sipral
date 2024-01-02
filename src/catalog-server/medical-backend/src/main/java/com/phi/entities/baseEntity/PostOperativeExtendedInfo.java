package com.phi.entities.baseEntity;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "post_op_ext_info")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PostOperativeExtendedInfo extends AuditedEntity {

	private static final long serialVersionUID = 611188605L;

	/**
	*  javadoc for protocolType
	*/
	private CodeValuePhi protocolType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="protocolType")
	@ForeignKey(name="FK_PostOpCardExt_protocol")
	@Index(name="IX_PostOpCardExt_protocol")
	public CodeValuePhi getProtocolType(){
		return protocolType;
	}

	public void setProtocolType(CodeValuePhi protocolType){
		this.protocolType = protocolType;
	}
	
	/**
	*  javadoc for transferToPlace
	*/
	private List<CodeValuePhi> transferToPlace;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="PostOpExt_trnsfrTPlc", joinColumns = { @JoinColumn(name="PostOpExt_trnsfrTPlc_id") }, inverseJoinColumns = { @JoinColumn(name="transferToPlace") })
	@ForeignKey(name="FK_trnsfrTPlc_PostOpExt", inverseName="FK_PostOpExt_trnsfrTPlc")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTransferToPlace(){
		return transferToPlace;
	}

	public void setTransferToPlace(List<CodeValuePhi> transferToPlace){
		this.transferToPlace = transferToPlace;
	}

	/**
	*  javadoc for transferDate
	*/
	private Date transferDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="transfer_date")
	public Date getTransferDate(){
		return transferDate;
	}

	public void setTransferDate(Date transferDate){
		this.transferDate = transferDate;
	}

	/**
	*  javadoc for extraNote
	*/
	private String extraNote;

	@Column(name="extra_note", length=2500)
	public String getExtraNote(){
		return extraNote;
	}

	public void setExtraNote(String extraNote){
		this.extraNote = extraNote;
	}

	/**
	*  javadoc for antalgicTherapy
	*/
	private String antalgicTherapy;

	@Column(name="antalgic_therapy", length=2500)
	public String getAntalgicTherapy(){
		return antalgicTherapy;
	}

	public void setAntalgicTherapy(String antalgicTherapy){
		this.antalgicTherapy = antalgicTherapy;
	}

	/**
	*  javadoc for antalgicTherapyHour
	*/
	private String antalgicTherapyHour;

	@Column(name="antalgic_therapy_hour")
	public String getAntalgicTherapyHour(){
		return antalgicTherapyHour;
	}

	public void setAntalgicTherapyHour(String antalgicTherapyHour){
		this.antalgicTherapyHour = antalgicTherapyHour;
	}

	/**
	*  javadoc for infusion
	*/
	private String infusion;

	@Column(name="infusion", length=2500)
	public String getInfusion(){
		return infusion;
	}

	public void setInfusion(String infusion){
		this.infusion = infusion;
	}

	/**
	*  javadoc for instruction5
	*/
	private String instruction5;

	@Column(name="instruction5")
	public String getInstruction5(){
		return instruction5;
	}

	public void setInstruction5(String instruction5){
		this.instruction5 = instruction5;
	}

	/**
	*  javadoc for instruction4
	*/
	private String instruction4;

	@Column(name="instruction4")
	public String getInstruction4(){
		return instruction4;
	}

	public void setInstruction4(String instruction4){
		this.instruction4 = instruction4;
	}

	/**
	*  javadoc for instruction3
	*/
	private String instruction3;

	@Column(name="instruction3")
	public String getInstruction3(){
		return instruction3;
	}

	public void setInstruction3(String instruction3){
		this.instruction3 = instruction3;
	}

	/**
	*  javadoc for instruction2
	*/
	private String instruction2;

	@Column(name="instruction2")
	public String getInstruction2(){
		return instruction2;
	}

	public void setInstruction2(String instruction2){
		this.instruction2 = instruction2;
	}

	/**
	*  javadoc for instruction1
	*/
	private String instruction1;

	@Column(name="instruction1")
	public String getInstruction1(){
		return instruction1;
	}

	public void setInstruction1(String instruction1){
		this.instruction1 = instruction1;
	}

	/**
	*  javadoc for checkBoxDrainage
	*/
	private Boolean checkBoxDrainage;

	@Column(name="check_box_drainage")
	public Boolean getCheckBoxDrainage(){
		return checkBoxDrainage;
	}

	public void setCheckBoxDrainage(Boolean checkBoxDrainage){
		this.checkBoxDrainage = checkBoxDrainage;
	}

	/**
	*  javadoc for checkBoxCatVesc
	*/
	private Boolean checkBoxCatVesc;

	@Column(name="check_box_cat_vesc")
	public Boolean getCheckBoxCatVesc(){
		return checkBoxCatVesc;
	}

	public void setCheckBoxCatVesc(Boolean checkBoxCatVesc){
		this.checkBoxCatVesc = checkBoxCatVesc;
	}

	/**
	*  javadoc for checkBoxCatArt
	*/
	private Boolean checkBoxCatArt;

	@Column(name="check_box_cat_art")
	public Boolean getCheckBoxCatArt(){
		return checkBoxCatArt;
	}

	public void setCheckBoxCatArt(Boolean checkBoxCatArt){
		this.checkBoxCatArt = checkBoxCatArt;
	}

	/**
	*  javadoc for checkBoxCVC
	*/
	private Boolean checkBoxCVC;

	@Column(name="check_box_cvc")
	public Boolean getCheckBoxCVC(){
		return checkBoxCVC;
	}

	public void setCheckBoxCVC(Boolean checkBoxCVC){
		this.checkBoxCVC = checkBoxCVC;
	}

	/**
	*  javadoc for checkBoxDTX
	*/
	private Boolean checkBoxDTX;

	@Column(name="check_box_dtx")
	public Boolean getCheckBoxDTX(){
		return checkBoxDTX;
	}

	public void setCheckBoxDTX(Boolean checkBoxDTX){
		this.checkBoxDTX = checkBoxDTX;
	}

	/**
	*  javadoc for checkBoxDiur
	*/
	private Boolean checkBoxDiur;

	@Column(name="check_box_diur")
	public Boolean getCheckBoxDiur(){
		return checkBoxDiur;
	}

	public void setCheckBoxDiur(Boolean checkBoxDiur){
		this.checkBoxDiur = checkBoxDiur;
	}

	/**
	*  javadoc for checkBoxPVC
	*/
	private Boolean checkBoxPVC;

	@Column(name="check_box_pvc")
	public Boolean getCheckBoxPVC(){
		return checkBoxPVC;
	}

	public void setCheckBoxPVC(Boolean checkBoxPVC){
		this.checkBoxPVC = checkBoxPVC;
	}


	/**
	*  javadoc for checkBoxFR
	*/
	private Boolean checkBoxFR;

	@Column(name="check_box_fr")
	public Boolean getCheckBoxFR(){
		return checkBoxFR;
	}

	public void setCheckBoxFR(Boolean checkBoxFR){
		this.checkBoxFR = checkBoxFR;
	}

	/**
	*  javadoc for checkBoxFC
	*/
	private Boolean checkBoxFC;

	@Column(name="check_box_fc")
	public Boolean getCheckBoxFC(){
		return checkBoxFC;
	}

	public void setCheckBoxFC(Boolean checkBoxFC){
		this.checkBoxFC = checkBoxFC;
	}

	/**
	*  javadoc for checkBoxPA
	*/
	private Boolean checkBoxPA;

	@Column(name="check_box_pa")
	public Boolean getCheckBoxPA(){
		return checkBoxPA;
	}

	public void setCheckBoxPA(Boolean checkBoxPA){
		this.checkBoxPA = checkBoxPA;
	}
	
	
	
	//methods needed for BaseEntity extension
	
	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_PostOpExtInfo_sdloc")
	@Index(name = "IX_PostOpExtInfo_sdloc")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PostOpExtInfo_sequence")
	@SequenceGenerator(name = "PostOpExtInfo_sequence", sequenceName = "PostOpExtInfo_sequence")
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
	@ForeignKey(name="FK_PostOpExtInfo_cancByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_PostOpExtInfo_cancByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_PostOpExtInfo_cancBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_PostOpExtInfo_cancBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_PostOpExtInfo_authRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_PostOpExtInfo_authRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_PostOpExtInfo_auth")
	@JoinColumn(name="author_id")
	@Index(name="IX_PostOpExtInfo_auth")
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
	@ForeignKey(name="FK_PostOpExtInfo_lastAth")
	@Index(name="IX_PostOpExtInfo_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
