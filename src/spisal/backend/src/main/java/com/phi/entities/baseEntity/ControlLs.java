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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;

@javax.persistence.Entity
@Table(name = "control_list")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Lista controllo - Entità contenente la descrizione delle liste di controllo.
 * @author 510087
 *
 */
public class ControlLs extends BaseEntity {

	private static final long serialVersionUID = 1834043552L;

	/**
	*  javadoc for workingLine
	*/
	private List<CodeValuePhi> workingLine;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="ControlLs_workingLine", joinColumns = { @JoinColumn(name="ControlLs_id") }, inverseJoinColumns = { @JoinColumn(name="workingLine") })
	@ForeignKey(name="FK_workingLine_ControlLs", inverseName="FK_ControlLs_workingLine")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getWorkingLine(){
		return workingLine;
	}

	public void setWorkingLine(List<CodeValuePhi> workingLine){
		this.workingLine = workingLine;
	}

	/**
	*  javadoc for benessereTipoPratica
	*/
	private List<CodeValuePhi> benessereTipoPratica;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="controlls_benesseretipopratica", joinColumns = { @JoinColumn(name="ControlLs_id") }, inverseJoinColumns = { @JoinColumn(name="benessereTipoPratica") })
	@ForeignKey(name="FK_bt_ControlLs", inverseName="FK_ControlLs_bt")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getBenessereTipoPratica(){
		return benessereTipoPratica;
	}

	public void setBenessereTipoPratica(List<CodeValuePhi> benessereTipoPratica){
		this.benessereTipoPratica = benessereTipoPratica;
	}

	/**
	*  javadoc for supervisionCode
	*/
	private List<CodeValuePhi> supervisionCode;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="controlls_supervisioncode", joinColumns = { @JoinColumn(name="ControlLs_id") }, inverseJoinColumns = { @JoinColumn(name="supervisionCode") })
	@ForeignKey(name="FK_supervisionCode_ControlLs", inverseName="FK_ControlLs_supervisionCode")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getSupervisionCode(){
		return supervisionCode;
	}

	public void setSupervisionCode(List<CodeValuePhi> supervisionCode){
		this.supervisionCode = supervisionCode;
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
	*  Liste di controllo in domanda che contengono un sottoinsieme delle sottoliste definite in controlSubLs.
	*/
	private List<ControlLsReq> controlLsReq;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlLs")
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
			controlLsReq.setControlLs(this);
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
			controlLsReq.setControlLs(null);
		}
	}



	/**
	*  Appartiene (1) - Relazione che individua a quale lista di controllo appartiene una sottolista di controllo.
	*/
	private List<ControlSubLs> controlSubLs;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlLs", cascade=CascadeType.PERSIST)
	public List<ControlSubLs> getControlSubLs() {
		return controlSubLs;
	}

	public void setControlSubLs(List<ControlSubLs>list){
		controlSubLs = list;
	}

	public void addControlSubLs(ControlSubLs controlSubLs) {
		if (this.controlSubLs == null) {
			this.controlSubLs = new ArrayList<ControlSubLs>();
		}
		// add the association
		if(!this.controlSubLs.contains(controlSubLs)) {
			this.controlSubLs.add(controlSubLs);
			// make the inverse link
			controlSubLs.setControlLs(this);
		}
	}

	public void removeControlSubLs(ControlSubLs controlSubLs) {
		if (this.controlSubLs == null) {
			this.controlSubLs = new ArrayList<ControlSubLs>();
			return;
		}
		//add the association
		if(this.controlSubLs.contains(controlSubLs)){
			this.controlSubLs.remove(controlSubLs);
			//make the inverse link
			controlSubLs.setControlLs(null);
		}
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ControlLs_sequence")
	@SequenceGenerator(name = "ControlLs_sequence", sequenceName = "ControlLs_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
