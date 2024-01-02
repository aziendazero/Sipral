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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;







@javax.persistence.Entity
@Table(name = "tag_fascicolo")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TagFascicolo extends BaseEntity{

	private static final long serialVersionUID = 2101013631L;


	/**
	*  javadoc for linee
	*/
	private List<ServiceDeliveryLocation> linee;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="TagFascicolo_SDL_linee",
		joinColumns = { @JoinColumn(name="TagFascicolo_id") },
		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
	@ForeignKey(name="FK_TagFascicolo_linee", inverseName="FK_WorkingLines_TagFasccl")
	public List<ServiceDeliveryLocation> getLinee() {
		return linee;
	}

	public void setLinee(List<ServiceDeliveryLocation>list){
		linee = list;
	}



	/**
	*  javadoc for distretti
	*/
	private List<ServiceDeliveryLocation> distretti;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="TagFascicolo_ServiceDeliveryLocation",
		joinColumns = { @JoinColumn(name="TagFascicolo_id") },
		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
	@ForeignKey(name="FK_TagFascicolo_distretti", inverseName="FK_Distretti_TagFasccl")
	public List<ServiceDeliveryLocation> getDistretti() {
		return distretti;
	}

	public void setDistretti(List<ServiceDeliveryLocation>list){
		distretti = list;
	}



	/**
	*  javadoc for ulss
	*/
	private List<ServiceDeliveryLocation> ulss;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="TagFascicolo_ServiceDlUlss",
		joinColumns = { @JoinColumn(name="TagFascicolo_id") },
		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
	@ForeignKey(name="FK_TagFascicolo_ulss", inverseName="FK_SrvcDlUlss_TagFasccl")
	public List<ServiceDeliveryLocation> getUlss() {
		return ulss;
	}

	public void setUlss(List<ServiceDeliveryLocation>list){
		ulss = list;
	}
	


	/**
	*  javadoc for tagType
	*/
	private CodeValuePhi tagType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tagType")
	@ForeignKey(name="FK_TagFascicolo_tagType")
	//@Index(name="IX_TagFascicolo_tagType")
	public CodeValuePhi getTagType(){
		return tagType;
	}

	public void setTagType(CodeValuePhi tagType){
		this.tagType = tagType;
	}


	/**
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@ManyToMany(fetch=FetchType.LAZY)
	public List<Procpratiche> getProcpratiche() {
		return procpratiche;
	}

	public void setProcpratiche(List<Procpratiche>list){
		procpratiche = list;
	}

	public void addProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
		}
		// add the association
		if(!this.procpratiche.contains(procpratiche)) {
			this.procpratiche.add(procpratiche);
			// make the inverse link
			if (procpratiche.getTagFascicolo() == null || !procpratiche.getTagFascicolo().contains(this))
				procpratiche.addTagFascicolo(this);
		}
	}

	public void removeProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
			return;
		}
		//add the association
		if(this.procpratiche.contains(procpratiche)){
			this.procpratiche.remove(procpratiche);
			//make the inverse link
			if (procpratiche.getTagFascicolo() != null && procpratiche.getTagFascicolo().contains(this))
			procpratiche.removeTagFascicolo(this);
		}
	}


	/**
	*  javadoc for fascicolo
	*/
	private String fascicolo;

	@Column(name="fascicolo")
	public String getFascicolo(){
		Context conv = Contexts.getConversationContext();
		return fascicolo;
	}

	public void setFascicolo(String fascicolo){
		this.fascicolo = fascicolo;
	}


	


	/**
	*  javadoc for notes
	*/
	private String notes;

	@Column(name="notes",length=2000)
	public String getNotes(){
		return notes;
	}

	public void setNotes(String notes){
		this.notes = notes;
	}

	/**
	*  javadoc for startValidity
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
	*  javadoc for endValidity
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
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TagFascicolo_sequence")
	@SequenceGenerator(name = "TagFascicolo_sequence", sequenceName = "TagFascicolo_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	


}
