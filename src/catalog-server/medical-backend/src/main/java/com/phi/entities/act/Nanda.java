package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.baseEntity.AbstractDiagnosis;
import com.phi.entities.dataTypes.CodeValueNanda;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.json.JsonProxyGenerator;


@javax.persistence.Entity
@Table(name = "nanda")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=Nanda.class)
public class Nanda extends AbstractDiagnosis {

	private static final long serialVersionUID = 240024306L;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_Nanda_Pat_Enc")
	@Index(name="IX_Nanda_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}
	
		
	private CodeValuePhi statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Nanda_sc")
	@Index(name="IX_Nanda_sc") 
	public CodeValuePhi getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValuePhi statusCode) {
		this.statusCode = statusCode;
	}

	
	private String nandaBFstr;

	@Column(name="nanda_bfstr", length = 2500)
	public String getNandaBFstr(){
		return nandaBFstr;
	}

	public void setNandaBFstr(String nandaBFstr){
		this.nandaBFstr = nandaBFstr;
	}


	private Integer progNumber;

	@Column(name="prog_number")
	public Integer getProgNumber(){
		return progNumber;
	}

	public void setProgNumber(Integer progNumber){
		this.progNumber = progNumber;
	}
	
	
	private CodeValuePhi typePCP;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="typePCP")
	@ForeignKey(name="FK_Nanda_typePCP")
	@Index(name="IX_Nanda_typePCP")	
	public CodeValuePhi getTypePCP() {
		return typePCP;
	}

	public void setTypePCP(CodeValuePhi typePCP) {
		this.typePCP = typePCP;
	}


	private String nandaBFelse;

	@Column(name="nanda_bfelse", length = 2500)
	public String getNandaBFelse(){
		return nandaBFelse;
	}

	public void setNandaBFelse(String nandaBFelse){
		this.nandaBFelse = nandaBFelse;
	}


	private String titleDiag;

	@Column(name="title_diag")
	public String getTitleDiag(){
		return titleDiag;
	}

	public void setTitleDiag(String titleDiag){
		this.titleDiag = titleDiag;
	}

	
	private String elseNandaBM;

	@Column(name="else_nanda_bm")
	public String getElseNandaBM(){
		return elseNandaBM;
	}

	public void setElseNandaBM(String elseNandaBM){
		this.elseNandaBM = elseNandaBM;
	}

	
	private String elseNandaRF;

	@Column(name="else_nanda_rf")
	public String getElseNandaRF(){
		return elseNandaRF;
	}

	public void setElseNandaRF(String elseNandaRF){
		this.elseNandaRF = elseNandaRF;
	}

	
	private String consequence;

	@Column(name="consequence")
	public String getConsequence(){
		return consequence;
	}

	public void setConsequence(String consequence){
		this.consequence = consequence;
	}

	
	private String resources;

	@Column(name="resources")
	public String getResources(){
		return resources;
	}

	public void setResources(String resources){
		this.resources = resources;
	}

	
	private CodeValueNanda nandaDiag;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="nandaDiag")
	@ForeignKey(name="FK_nanda_diag")
	@Index(name="IX_Nanda_diag")
	public CodeValueNanda getNandaDiag() {
		return nandaDiag;
	}

	public void setNandaDiag(CodeValueNanda nandaDiag) {
		this.nandaDiag = nandaDiag;
	}

	
	private Boolean diagType;

	@Column(name="diagType")
	public Boolean getDiagType() {
		return diagType;
	}
	public void setDiagType(Boolean diagType) {
		this.diagType = diagType;
	}


	private  List<CodeValueNanda> nandaBFSign;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "nanda_nandaBFSgn", joinColumns = { @JoinColumn(name = "nanda_id") }, inverseJoinColumns = { @JoinColumn(name = "nandaBFSign") })
	@ForeignKey(name = "FK_nandaBFSgn_nanda", inverseName = "FK_nanda_nandaBFSgn")	
	public  List<CodeValueNanda> getNandaBFSign() {
		return nandaBFSign;
	}

	public void setNandaBFSign(List<CodeValueNanda>  nandaBFSign) {
		this.nandaBFSign = nandaBFSign;
	}

	
	private  List<CodeValueNanda> nandaBM;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "nanda_nandabm", joinColumns = { @JoinColumn(name = "nanda_id") }, inverseJoinColumns = { @JoinColumn(name = "nandaBM") })
	@ForeignKey(name = "FK_nandaBM_nanda", inverseName = "FK_nanda_nandaBM")	
	public  List<CodeValueNanda> getNandaBM() {
		return nandaBM;
	}

	public void setNandaBM(List<CodeValueNanda>  nandaBM) {
		this.nandaBM = nandaBM;
	}

	
	private  List<CodeValuePhi> consequenceCode;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "nanda_consequenceCode", joinColumns = { @JoinColumn(name = "nanda_id") }, inverseJoinColumns = { @JoinColumn(name = "consequenceCode") })
	@ForeignKey(name = "FK_consequenceCode_nanda", inverseName = "FK_nanda_consequenceCode")
	public  List<CodeValuePhi> getConsequenceCode() {
		return consequenceCode;
	}
	
	public void setConsequenceCode(List<CodeValuePhi>  consequenceCode) {
		this.consequenceCode = consequenceCode;
	}
	
	
	private CodeValuePhi riskCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riskCode")
	@ForeignKey(name="FK_nanda_riskCode")
	@Index(name="IX_Nanda_riskCode")
	public CodeValuePhi getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(CodeValuePhi riskCode) {
		this.riskCode = riskCode;
	}


	private  List<CodeValueNanda> nandaRF;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "nanda_nandarf", joinColumns = { @JoinColumn(name = "nanda_id") }, inverseJoinColumns = { @JoinColumn(name = "nandaRF") })
	@ForeignKey(name = "FK_nandaRF_nanda", inverseName = "FK_nanda_nandaRF")
	public  List<CodeValueNanda> getNandaRF() {
		return nandaRF;
	}

	public void setNandaRF(List<CodeValueNanda>  nandaRF) {
		this.nandaRF = nandaRF;
	}


	private Date actNandaDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="actNanda_date")
	public Date getActNandaDate() {
		return actNandaDate;
	}

	public void setActNandaDate(Date  actNandaDate) {
		this.actNandaDate = actNandaDate;
	}


	private Boolean riskType;

	@Column(name="risk_type")
	public Boolean getRiskType(){	

		return riskType;
	}

	public void setRiskType(Boolean riskType){
		this.riskType = riskType;
	}

	
	private List<LEPActivity> activity;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="nanda", cascade=CascadeType.PERSIST)
	public List<LEPActivity> getActivity() {
		return activity;
	}

	public void setActivity(List<LEPActivity> activity) {
		this.activity = activity;
	}

	public void addActivity(LEPActivity activity) {
		// create the association set if it doesn't exist already
		if (this.activity == null) {
			this.activity  = new ArrayList<LEPActivity >();
		}
		// add the association to the association set
		if(!this.activity.contains(activity)) {
			this.activity.add(activity);
			// make the inverse link
			activity.setNanda(this);
		}
	}
	
	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;

	}
	
	
	private List<ObjectiveNanda> objective;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="nanda", cascade=CascadeType.PERSIST)
	public List<ObjectiveNanda> getObjective() {
		return objective;
	}

	public void setObjective(List<ObjectiveNanda> objective) {
		this.objective = objective;
	}

	public void addObjective(ObjectiveNanda objective) {
		// create the association set if it doesn't exist already
		if (this.objective == null) {
			this.objective  = new ArrayList<ObjectiveNanda >();
		}
		// add the association to the association set
		if(!this.objective.contains(objective)) {
			this.objective.add(objective);
			// make the inverse link
			objective.setNanda(this);
		}
	}

	
	private CodeValueNanda consequenceDiag;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="consequenceDiag")
	@ForeignKey(name="FK_consequence_diag")
	@Index(name="IX_consequence_diag")
	public CodeValueNanda getConsequenceDiag() {
		return consequenceDiag;
	}

	public void setConsequenceDiag(CodeValueNanda consequenceDiag) {
		this.consequenceDiag = consequenceDiag;
	}
}