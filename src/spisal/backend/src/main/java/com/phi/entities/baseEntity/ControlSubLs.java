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
@javax.persistence.Entity
@Table(name = "control_sub_list")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Sottolista controllo - Entità contenente la descrizione delle suddivisioni delle liste di controllo.
 * @author 510087
 *
 */
public class ControlSubLs extends BaseEntity {

	private static final long serialVersionUID = 1833869686L;

	/**
	*  javadoc for bedRangeBl
	*/
	private boolean bedRangeBl = false;

	@Column(name="bed_range_bl")
	public boolean getBedRangeBl(){
		return bedRangeBl;
	}

	public void setBedRangeBl(boolean bedRangeBl){
		this.bedRangeBl = bedRangeBl;
	}
	
	/**
	*  javadoc for bedMax
	*/
	private Integer bedMax;

	@Column(name="bed_max")
	public Integer getBedMax(){
		return bedMax;
	}

	public void setBedMax(Integer bedMax){
		this.bedMax = bedMax;
	}

	/**
	*  javadoc for bedMin
	*/
	private Integer bedMin;

	@Column(name="bed_min")
	public Integer getBedMin(){
		return bedMin;
	}

	public void setBedMin(Integer bedMin){
		this.bedMin = bedMin;
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
	*  Sottoliste di controllo in domanda.
	*/
	private List<ControlSubLsReq> controlSubLsReq;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlSubLs")
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
			controlSubLsReq.setControlSubLs(this);
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
			controlSubLsReq.setControlSubLs(null);
		}
	}

	/**
	*  Richiama (inversa) - Relazione che individua le liste/sottoliste di controllo richiamate da ciascuna lista/sottolista.
	*/
	private List<ControlSubLs> parentSubList;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="childrenSubList", cascade=CascadeType.PERSIST)
	public List<ControlSubLs> getParentSubList(){
		return parentSubList;
	}

	public void setParentSubList(List<ControlSubLs> list){
		parentSubList = list;
	}

	public void addParentSubList(ControlSubLs parentSubList) {
		if (this.parentSubList == null) {
			this.parentSubList = new ArrayList<ControlSubLs>();
		}
		// add the association
		if(!this.parentSubList.contains(parentSubList)) {
			this.parentSubList.add(parentSubList);
			// make the inverse link
			if (parentSubList.getChildrenSubList() == null || !parentSubList.getChildrenSubList().contains(this))
				parentSubList.addChildrenSubList(this);
		}
	}

	public void removeParentSubList(ControlSubLs parentSubList) {
		if (this.parentSubList == null) {
			this.parentSubList = new ArrayList<ControlSubLs>();
			return;
		}
		//add the association
		if(this.parentSubList.contains(parentSubList)){
			this.parentSubList.remove(parentSubList);
			//make the inverse link
			if (parentSubList.getChildrenSubList() != null && parentSubList.getChildrenSubList().contains(this))
				parentSubList.removeChildrenSubList(this);
		}

	}



	/**
	*  Richiama (diretta) - Relazione che individua le liste/sottoliste di controllo richiamate da ciascuna lista/sottolista.
	*/
	private List<ControlSubLs> childrenSubList;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="sublist_parentchildren", joinColumns = { @JoinColumn(name="parent_id") }, inverseJoinColumns = { @JoinColumn(name="child_id") })
	@ForeignKey(name="FK_SubList_Parent", inverseName="FK_SubList_Children")
	@IndexColumn(name="IX_SubList_ParentChildren")
	public List<ControlSubLs> getChildrenSubList() {
		return childrenSubList;
	}

	public void setChildrenSubList(List<ControlSubLs>list){
		childrenSubList = list;
	}

	public void addChildrenSubList(ControlSubLs childrenSubList) {
		if (this.childrenSubList == null) {
			this.childrenSubList = new ArrayList<ControlSubLs>();
		}
		// add the association
		if(!this.childrenSubList.contains(childrenSubList)) {
			this.childrenSubList.add(childrenSubList);
			// make the inverse link
			if (childrenSubList.getParentSubList() == null || !childrenSubList.getParentSubList().contains(this))
				childrenSubList.addParentSubList(this);
		}
	}

	public void removeChildrenSubList(ControlSubLs childrenSubList) {
		if (this.childrenSubList == null) {
			this.childrenSubList = new ArrayList<ControlSubLs>();
			return;
		}
		//add the association
		if(this.childrenSubList.contains(childrenSubList)){
			this.childrenSubList.remove(childrenSubList);
			//make the inverse link
			if (childrenSubList.getParentSubList() != null && childrenSubList.getParentSubList().contains(this))
			childrenSubList.removeParentSubList(this);
		}
	}



	/**
	*  Appartiene (1) - Relazione che individua a quale lista di controllo appartiene una sottolista di controllo.
	*/
	private ControlLs controlLs;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="control_list_id")
	@ForeignKey(name="FK_ControlSubLs_cntrlList")
	//@Index(name="IX_ControlSubLs_cntrlList")
	public ControlLs getControlLs(){
		return controlLs;
	}

	public void setControlLs(ControlLs controlLs){
		this.controlLs = controlLs;
	}



	/**
	*  Include - Relazione che individua i requisiti previsti per una sottolista di controllo.
	*/
	private List<Requisito> requisito;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="controlSubLs", cascade=CascadeType.PERSIST)
	public List<Requisito> getRequisito() {
		return requisito;
	}

	public void setRequisito(List<Requisito>list){
		requisito = list;
	}

	public void addRequisito(Requisito requisito) {
		if (this.requisito == null) {
			this.requisito = new ArrayList<Requisito>();
		}
		// add the association
		if(!this.requisito.contains(requisito)) {
			this.requisito.add(requisito);
			// make the inverse link
			requisito.setControlSubLs(this);
		}
	}

	public void removeRequisito(Requisito requisito) {
		if (this.requisito == null) {
			this.requisito = new ArrayList<Requisito>();
			return;
		}
		//add the association
		if(this.requisito.contains(requisito)){
			this.requisito.remove(requisito);
			//make the inverse link
			requisito.setControlSubLs(null);
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ControlSubLs_sequence")
	@SequenceGenerator(name = "ControlSubLs_sequence", sequenceName = "ControlSubLs_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
