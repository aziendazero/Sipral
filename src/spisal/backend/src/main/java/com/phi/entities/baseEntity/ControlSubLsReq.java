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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
@javax.persistence.Entity
@Table(name = "control_sub_ls_req")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Sottolista controllo in domanda - Entità contenente la descrizione delle suddivisioni delle liste di controllo IN DOMANDA.
 * Quando l'utente seleziona una funzione (strutturale,operativa,sanitaria), per ogni sottolista di controllo, 
 * viene controllata la lista di appartenenza: se non è presente, viene copiata in domanda come ControlLsReq, 
 * agganciata all'attività (strutturale,operativa,sanitaria) e quindi viene copiata la sottolista come ControlLsSubReq e 
 * associata alla lista appena creata. Se esiste già tale lista, viene semplicemente copiata la sottolista come ControlLsSubReq e 
 * associata alla lista già presente. Questo facilita le verifiche e le stampe.
 * @author 510087
 *
 */
public class ControlSubLsReq extends BaseEntity {

	private static final long serialVersionUID = 532197907L;
	
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
	*  javadoc for inCommon
	*/
	private Boolean inCommon;

	@Column(name="in_common")
	public Boolean getInCommon(){
		return inCommon;
	}

	public void setInCommon(Boolean inCommon){
		this.inCommon = inCommon;
	}


	/**
	*  Lista delle risposte ai requisiti della sottolista.
	*/
	private List<Risposta> risposta;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlSubLsReq") 
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE}) //eliminazioni in fase di compilazione allegato A in cascade
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
			risposta.setControlSubLsReq(this);
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
			risposta.setControlSubLsReq(null);
		}
	}



	/**
	*  Lista di controllo IN DOMANDA a cui appartiene questa sottolista. Può essere più di una se questa sottolista è in COMUNE.
	*/
	private List<ControlLsReq> controlLsReq;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="sublistreq_listreq", joinColumns = { @JoinColumn(name="SublistReq_id") }, inverseJoinColumns = { @JoinColumn(name="ListReq_id") })
	@ForeignKey(name="FK_SublistReq_ListReq", inverseName="FK_ListReq_SublistReq")
	@IndexColumn(name="IX_SublistReq_ListReq")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<ControlLsReq> getControlLsReq() {
		return controlLsReq;
	}

	public void setControlLsReq(List<ControlLsReq>list){
		controlLsReq = list;
	}

	public void addControlLsReq(ControlLsReq controlLsReq) {
		if (this.controlLsReq == null) {
			this.controlLsReq = new ArrayList<ControlLsReq>();
		}
		// add the association
		if(!this.controlLsReq.contains(controlLsReq)) {
			this.controlLsReq.add(controlLsReq);
			// make the inverse link
			if (controlLsReq.getControlSubLsReq() == null || !controlLsReq.getControlSubLsReq().contains(this))
				controlLsReq.addControlSubLsReq(this);
		}
	}

	public void removeControlLsReq(ControlLsReq controlLsReq) {
		if (this.controlLsReq == null) {
			this.controlLsReq = new ArrayList<ControlLsReq>();
			return;
		}
		//add the association
		if(this.controlLsReq.contains(controlLsReq)){
			this.controlLsReq.remove(controlLsReq);
			//make the inverse link
			if (controlLsReq.getControlSubLsReq() != null && controlLsReq.getControlSubLsReq().contains(this))
			controlLsReq.removeControlSubLsReq(this);
		}
	}
	
	/**
	*  Link alla sottolista originale.
	*/
	private ControlSubLs controlSubLs;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="control_sub_ls_id")
	@ForeignKey(name="FK_CntrlSubLsReq_cntrlSubLs")
	//@Index(name="IX_CntrlSubLsReq_cntrlSubLs")
	public ControlSubLs getControlSubLs(){
		return controlSubLs;
	}

	public void setControlSubLs(ControlSubLs controlSubLs){
		this.controlSubLs = controlSubLs;
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
	*  Descrizione della sottolista di controllo
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
	*  Codice della sottolista di controllo che viene presentato a video e/o in stampa
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
	*  Codice della sottolista di controllo
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ControlSubLsReq_sequence")
	@SequenceGenerator(name = "ControlSubLsReq_sequence", sequenceName = "ControlSubLsReq_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
