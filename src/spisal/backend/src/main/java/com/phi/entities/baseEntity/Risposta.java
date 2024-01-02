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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "risposta")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Risposta - Entità che rappresenta la risposta di un utente ad un particolare questito proposto nel
 * requisito di una sottolista di controllo.
 * @author 510087
 *
 */
public class Risposta extends BaseEntity {

	private static final long serialVersionUID = 531430266L;

	/**
	*  javadoc for rispostaType
	*/
	private CodeValuePhi rispostaType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rispostaType")
	@ForeignKey(name="FK_Risposta_rispostaType")
	//@Index(name="IX_Risposta_rispostaType")
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
	@JoinTable(name="risposta_valnotesobb", joinColumns = { @JoinColumn(name="Risposta_id") }, inverseJoinColumns = { @JoinColumn(name="valNotesObb") })
	@ForeignKey(name="FK_valNotesObb_Risposta", inverseName="FK_Risposta_valNotesObb")
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
	@ForeignKey(name="FK_Risposta_valDomain")
	//@Index(name="IX_Risposta_valDomain")
	public CodeValuePhi getValDomain(){
		return valDomain;
	}

	public void setValDomain(CodeValuePhi valDomain){
		this.valDomain = valDomain;
	}

	/**
	*  javadoc for selected
	*/
	private boolean selected = false;

	@Column(name="selected")
	public boolean getSelected(){
		return selected;
	}

	public void setSelected(boolean selected){
		this.selected = selected;
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
	*  javadoc for campoNumVal
	*/
	private String campoNumVal;

	@Column(name="campo_num_val")
	public String getCampoNumVal(){
		return campoNumVal;
	}

	public void setCampoNumVal(String campoNumVal){
		this.campoNumVal = campoNumVal;
	}
	
	/**
	*  javadoc for campoTestoVal
	*/
	private String campoTestoVal;

	@Column(name="campo_testo_val", length=4000)
	public String getCampoTestoVal(){
		return campoTestoVal;
	}

	public void setCampoTestoVal(String campoTestoVal){
		this.campoTestoVal = campoTestoVal;
	}
	
	/**
	*  javadoc for campoDataVal
	*/
	private Date campoDataVal;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="campo_data_val")
	public Date getCampoDataVal(){
		return campoDataVal;
	}

	public void setCampoDataVal(Date campoDataVal){
		this.campoDataVal = campoDataVal;
	}

	/**
	*  definisce l'obbligatorietà della compilazione del campo numerico
	*/
	private boolean rispostaObb = false;

	@Column(name="risposta_obb")
	public boolean getRispostaObb(){
		return rispostaObb;
	}

	public void setRispostaObb(boolean rispostaObb){
		this.rispostaObb = rispostaObb;
	}
	
	/**
	*  javadoc for total
	*/
	private Integer total;

	@Column(name="total")
	public Integer getTotal(){
		return total;
	}

	public void setTotal(Integer total){
		this.total = total;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note", length=4000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for value
	*/
	private CodeValuePhi value;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="value")
	@ForeignKey(name="FK_Risposta_value")
	//@Index(name="IX_Risposta_value")
	public CodeValuePhi getValue(){
		return value;
	}

	public void setValue(CodeValuePhi value){
		this.value = value;
	}

	/**
	*  Link al requisito originale
	*/
	private Requisito requisito;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="requisito_id")
	@ForeignKey(name="FK_Risposta_requisito")
	//@Index(name="IX_Risposta_requisito")
	public Requisito getRequisito(){
		return requisito;
	}

	public void setRequisito(Requisito requisito){
		this.requisito = requisito;
	}

	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE}) //eliminazioni in fase di compilazione allegato A in cascade
	@JoinColumn(name="Risposta_id")
	@ForeignKey(name="FK_documenti_Risposta")
	//@Index(name="IX_documenti_Risposta")
	public List<AlfrescoDocument> getDocumenti() {
		return documenti;
	}

	public void setDocumenti(List<AlfrescoDocument>list){
		documenti = list;
	}

	/**
	*  Sottolista di controllo IN DOMANDA a cui appartiene questa risposta.
	*/
	private ControlSubLsReq controlSubLsReq;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="control_sub_ls_req_id")
	@ForeignKey(name="FK_Risposta_controlSubLsReq")
	//@Index(name="IX_Risposta_controlSubLsReq")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public ControlSubLsReq getControlSubLsReq(){
		return controlSubLsReq;
	}

	public void setControlSubLsReq(ControlSubLsReq controlSubLsReq){
		this.controlSubLsReq = controlSubLsReq;
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
	
	/**
	*  Valori ammessi per il requisito
	*/
	private List<CodeValuePhi> valAdmitted;

	@NotAudited
	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="risposta_valadmitted", joinColumns = { @JoinColumn(name="Risposta_id") }, inverseJoinColumns = { @JoinColumn(name="valAdmitted") })
	@ForeignKey(name="FK_valAdmitted_Risposta", inverseName="FK_Risposta_valAdmitted")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getValAdmitted(){
		return valAdmitted;
	}

	public void setValAdmitted(List<CodeValuePhi> valAdmitted){
		this.valAdmitted = valAdmitted;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Risposta_sequence")
	@SequenceGenerator(name = "Risposta_sequence", sequenceName = "Risposta_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
