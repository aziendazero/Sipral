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
import javax.persistence.Lob;
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

import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "requisito")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**	
 * Requisito - Entità contenente la descrizione dettagliata dei requisiti e l’ordinamento con cui devono essere visualizzati e/o stampati.
 * @author 510087
 *
 */
public class Requisito extends BaseEntity {

	private static final long serialVersionUID = 1834443633L;

	/**
	*  javadoc for rispostaType
	*/
	private CodeValuePhi rispostaType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rispostaType")
	@ForeignKey(name="FK_Requisito_rispostaType")
	//@Index(name="IX_Requisito_rispostaType")
	public CodeValuePhi getRispostaType(){
		return rispostaType;
	}

	public void setRispostaType(CodeValuePhi rispostaType){
		this.rispostaType = rispostaType;
	}

	/**
	*  javadoc for valNotesObb
	*/
	private List<CodeValuePhi> valNotesObb;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="requisito_valnotesobb", joinColumns = { @JoinColumn(name="Requisito_id") }, inverseJoinColumns = { @JoinColumn(name="valNotesObb") })
	@ForeignKey(name="FK_valNotesObb_Requisito", inverseName="FK_Requisito_valNotesObb")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getValNotesObb(){
		return valNotesObb;
	}

	public void setValNotesObb(List<CodeValuePhi> valNotesObb){
		this.valNotesObb = valNotesObb;
	}

	/**
	*  javadoc for valDomain
	*/
	private CodeValuePhi valDomain;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="valDomain")
	@ForeignKey(name="FK_Requisito_valDomain")
	//@Index(name="IX_Requisito_valDomain")
	public CodeValuePhi getValDomain(){
		return valDomain;
	}

	public void setValDomain(CodeValuePhi valDomain){
		this.valDomain = valDomain;
	}

	/**
	*  definisce l'obbligatorietà della compilazione del campo note
	*/
	private boolean campoNoteObb = false;

	@Column(name="campo_note_obb")
	public boolean getCampoNoteObb(){
		return campoNoteObb;
	}

	public void setCampoNoteObb(boolean campoNoteObb){
		this.campoNoteObb = campoNoteObb;
	}

	/**
	*  definisce l'obbligatorietà della compilazione del campo numerico
	*/
	private boolean rispostaObb = true;

	@Column(name="risposta_obb")
	public boolean getRispostaObb(){
		return rispostaObb;
	}

	public void setRispostaObb(boolean rispostaObb){
		this.rispostaObb = rispostaObb;
	}

	/**
	*  Risposte in domanda associate a questo requisito.
	*/
	private List<Risposta> risposta;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="requisito")
	public List<Risposta> getRisposta() {
		return risposta;
	}

	public void setRisposta(List<Risposta>list){
		risposta = list;
	}

	public void addRisposta(Risposta risposta) {
		if (this.risposta == null) {
			this.risposta = new ArrayList<Risposta>();
		}
		// add the association
		if(!this.risposta.contains(risposta)) {
			this.risposta.add(risposta);
			// make the inverse link
			risposta.setRequisito(this);
		}
	}

	public void removeRisposta(Risposta risposta) {
		if (this.risposta == null) {
			this.risposta = new ArrayList<Risposta>();
			return;
		}
		//add the association
		if(this.risposta.contains(risposta)){
			this.risposta.remove(risposta);
			//make the inverse link
			risposta.setRequisito(null);
		}
	}


	/**
	*  Valori ammessi per il requisito
	*/
	private List<CodeValuePhi> valAdmitted;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="requisito_valadmitted", joinColumns = { @JoinColumn(name="Requisito_id") }, inverseJoinColumns = { @JoinColumn(name="valAdmitted") })
	@ForeignKey(name="FK_valAdmitted_Requisito", inverseName="FK_Requisito_valAdmitted")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getValAdmitted(){
		return valAdmitted;
	}

	public void setValAdmitted(List<CodeValuePhi> valAdmitted){
		this.valAdmitted = valAdmitted;
	}
	
	
	/**
	*  Include - Relazione che individua i requisiti previsti per una sottolista di controllo.
	*/
	private ControlSubLs controlSubLs;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="control_sub_list_id")
	@ForeignKey(name="FK_Requisito_controlSubLs")
	//@Index(name="IX_Requisito_controlSubLs")
	public ControlSubLs getControlSubLs(){
		return controlSubLs;
	}

	public void setControlSubLs(ControlSubLs controlSubLs){
		this.controlSubLs = controlSubLs;
	}


	/**
	*  Data fine validità dei dati
	*/
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	*  Data inizio validità dei dati
	*/
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}

	/**
	*  Descrizione della prestazione - non più usato
	*/
	private String descrPerf;

	@Column(name="descr_perf", length=4000)
	public String getDescrPerf(){
		return descrPerf;
	}

	public void setDescrPerf(String descrPerf){
		this.descrPerf = descrPerf;
	}

	/**
	*  Punteggio che deve essere attribuito al punteggio (corrispondente alla risposta affermativa)
	*/
	private Integer points;

	@Column(name="points")
	public Integer getPoints(){
		return points;
	}

	public void setPoints(Integer points){
		this.points = points;
	}

	/**
	*  Indica se al requisito deve essere attribuito un punteggio
	*/
	private boolean pointsFlag = false;

	@Column(name="points_flag")
	public boolean getPointsFlag(){
		return pointsFlag;
	}

	public void setPointsFlag(boolean pointsFlag){
		this.pointsFlag = pointsFlag;
	}

	/**
	*  Indica il requisito informativo "padre" da cui dipendono i valori ammessi per il requisito
	*/
	private Integer parent;

	@Column(name="parent")
	public Integer getParent(){
		return parent;
	}

	public void setParent(Integer parent){
		this.parent = parent;
	}

	/**
	*  Indica l'ordinamento dei requisiti
	*/
	private Integer ordering;

	@Column(name="ordering")
	public Integer getOrdering(){
		return ordering;
	}

	public void setOrdering(Integer ordering){
		this.ordering = ordering;
	}

	/**
	*  Indica se deve essere visualizzato a video anche se non prevede valori ammessi
	*/
	private boolean reqVis = false;

	@Column(name="req_vis")
	public boolean getReqVis(){
		return reqVis;
	}

	public void setReqVis(boolean reqVis){
		this.reqVis = reqVis;
	}

	/**
	*  Indica se deve essere associata la dicitura "Requisito da soddisfare" alla descrizione del codice requisito
	*/
	private boolean reqVar = false;

	@Column(name="req_var")
	public boolean getReqVar(){
		return reqVar;
	}

	public void setReqVar(boolean reqVar){
		this.reqVar = reqVar;
	}

	/**
	*  Descrizione del requisito
	*/
	private String description;
	
	@Lob
	@Column(name="description")
	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	/**
	*  Codice del requisito che viene visualizzato e/o stampato
	*/
	private String descrCode;

	@Column(name="descr_code")
	public String getDescrCode(){
		return descrCode;
	}

	public void setDescrCode(String descrCode){
		this.descrCode = descrCode;
	}

	/**
	*  Codice del requisito
	*/
	private String reqid;

	@Column(name="reqid")
	public String getReqid(){
		return reqid;
	}

	public void setReqid(String reqid){
		this.reqid = reqid;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Requisito_sequence")
	@SequenceGenerator(name = "Requisito_sequence", sequenceName = "Requisito_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
