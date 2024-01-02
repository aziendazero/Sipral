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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.dataTypes.CodeValuePhi;

import javax.persistence.JoinTable;
import org.hibernate.annotations.IndexColumn;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.Attivita;

@javax.persistence.Entity
@Table(name = "control_ls_req")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Lista controllo in domanda - Entità contenente la descrizione delle liste di controllo IN DOMANDA. 
 * Quando l'utente seleziona una funzione (strutturale,operativa,sanitaria), per ogni sottolista di controllo, 
 * viene controllata la lista di appartenenza: se non è presente, viene copiata in domanda come ControlLsReq, 
 * agganciata all'attività (strutturale,operativa,sanitaria) e quindi viene copiata la sottolista come ControlSubLsReq e 
 * associata alla lista appena creata. Se esiste già tale lista, viene semplicemente copiata la sottolista come ControlSubLsReq e 
 * associata alla lista già presente. Questo facilita le verifiche e le stampe.
 * @author 510087
 *
 */
public class ControlLsReq extends BaseEntity {

	private static final long serialVersionUID = 533236807L;


	/**
	*  javadoc for attivita
	*/
	private List<Attivita> attivita;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlLsReq"/*, cascade=CascadeType.PERSIST*/)
	public List<Attivita> getAttivita(){
		return attivita;
	}

	public void setAttivita(List<Attivita> list){
		attivita = list;
	}

	public void addAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
		}
		// add the association
		if(!this.attivita.contains(attivita)) {
			this.attivita.add(attivita);
			// make the inverse link
			attivita.setControlLsReq(this);
		}
	}

	public void removeAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
			return;
		}
		//add the association
		if(this.attivita.contains(attivita)){
			this.attivita.remove(attivita);
			//make the inverse link
			attivita.setControlLsReq(null);
		}

	}


	/**
	*  javadoc for workingLine
	*/
	private List<CodeValuePhi> workingLine;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="ControlLsReq_workingLine", joinColumns = { @JoinColumn(name="ControlLsReq_id") }, inverseJoinColumns = { @JoinColumn(name="workingLine") })
	@ForeignKey(name="FK_workingLine_ControlLsReq", inverseName="FK_ControlLsReq_workingLine")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getWorkingLine(){
		return workingLine;
	}

	public void setWorkingLine(List<CodeValuePhi> workingLine){
		this.workingLine = workingLine;
	}
	
	/**
	*  javadoc for footer
	*/
	private String footer;

	@Column(name="footer")
	public String getFooter(){
		return footer;
	}

	public void setFooter(String footer){
		this.footer = footer;
	}

	/**
	*  javadoc for header
	*/
	private String header;

	@Column(name="header")
	public String getHeader(){
		return header;
	}

	public void setHeader(String header){
		this.header = header;
	}
	
	/**
	*  javadoc for compiled
	*/
	private Boolean compiled;

	@Column(name="compiled")
	public Boolean getCompiled(){
		return compiled;
	}

	public void setCompiled(Boolean compiled){
		this.compiled = compiled;
	}
	
	/**
	*  set to true the first time the list is saved
	*/
	private Boolean saved;

	@Column(name="saved")
	public Boolean getSaved(){
		return saved;
	}

	public void setSaved(Boolean saved){
		this.saved = saved;
	}

	/**
	*  javadoc for descrSmall
	*/
	private String descrSmall;

	@Column(name="descr_small")
	public String getDescrSmall(){
		return descrSmall;
	}

	public void setDescrSmall(String descrSmall){
		this.descrSmall = descrSmall;
	}

	/**
	*  Sottoliste in domanda associate a questa lista in domanda
	*/
	private List<ControlSubLsReq> controlSubLsReq;

	/*
	 * Eliminazioni in fase di compilazione allegato A in cascade: prima del cascade devo ricordarmi di sganciare le sottoliste in COMUNE
	 */
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="controlLsReq") 
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
	public List<ControlSubLsReq> getControlSubLsReq() {
		return controlSubLsReq;
	}

	public void setControlSubLsReq(List<ControlSubLsReq>list){
		controlSubLsReq = list;
	}

	public void addControlSubLsReq(ControlSubLsReq controlSubLsReq) {
		if (this.controlSubLsReq == null) {
			this.controlSubLsReq = new ArrayList<ControlSubLsReq>();
		}
		// add the association
		if(!this.controlSubLsReq.contains(controlSubLsReq)) {
			this.controlSubLsReq.add(controlSubLsReq);
			// make the inverse link
			if (controlSubLsReq.getControlLsReq() == null || !controlSubLsReq.getControlLsReq().contains(this))
				controlSubLsReq.addControlLsReq(this);
		}
	}

	public void removeControlSubLsReq(ControlSubLsReq controlSubLsReq) {
		if (this.controlSubLsReq == null) {
			this.controlSubLsReq = new ArrayList<ControlSubLsReq>();
			return;
		}
		//add the association
		if(this.controlSubLsReq.contains(controlSubLsReq)){
			this.controlSubLsReq.remove(controlSubLsReq);
			//make the inverse link
			if (controlSubLsReq.getControlLsReq() != null && controlSubLsReq.getControlLsReq().contains(this))
				controlSubLsReq.removeControlLsReq(this);
		}
	}



	/**
	*  Link alla lista originale.
	*/
	private ControlLs controlLs;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="control_ls_id")
	@ForeignKey(name="FK_ControlLsReq_controlLs")
	//@Index(name="IX_ControlLsReq_controlLs")
	public ControlLs getControlLs(){
		return controlLs;
	}

	public void setControlLs(ControlLs controlLs){
		this.controlLs = controlLs;
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
	*  Descrizione della lista di controllo
	*/
	private String description;

	@Column(name="description", length=4000)
	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	/**
	*  Codice della lista di controllo che viene presentato a video e/o in stampa
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
	*  Codice della lista di controllo
	*/
	private String listid;

	@Column(name="listid")
	public String getListid(){
		return listid;
	}

	public void setListid(String listid){
		this.listid = listid;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ControlLsReq_sequence")
	@SequenceGenerator(name = "ControlLsReq_sequence", sequenceName = "ControlLsReq_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
