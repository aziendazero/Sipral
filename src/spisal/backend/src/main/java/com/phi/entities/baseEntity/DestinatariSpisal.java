package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Aulss;
import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "dest_spisal")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DestinatariSpisal extends BaseEntity {

	private static final long serialVersionUID = 627185037L;

	/**
	*  javadoc for workingLine
	*/
	private CodeValuePhi workingLine;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="workingLine")
	@ForeignKey(name="FK_DestinatariSpisal_workingLine")
	@Index(name="IX_DestinatariSpisal_workingLine")
	public CodeValuePhi getWorkingLine(){
		return workingLine;
	}

	public void setWorkingLine(CodeValuePhi workingLine){
		this.workingLine = workingLine;
	}


	/**
	*  javadoc for aulss
	*/
	private List<Aulss> aulss;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="destinatariSpisal", cascade=CascadeType.PERSIST)
	public List<Aulss> getAulss(){
		return aulss;
	}

	public void setAulss(List<Aulss> list){
		aulss = list;
	}

	public void addAulss(Aulss aulss) {
		if (this.aulss == null) {
			this.aulss = new ArrayList<Aulss>();
		}
		// add the association
		if(!this.aulss.contains(aulss)) {
			this.aulss.add(aulss);
			// make the inverse link
			aulss.setDestinatariSpisal(this);
		}
	}

	public void removeAulss(Aulss aulss) {
		if (this.aulss == null) {
			this.aulss = new ArrayList<Aulss>();
			return;
		}
		//add the association
		if(this.aulss.contains(aulss)){
			this.aulss.remove(aulss);
			//make the inverse link
			aulss.setDestinatariSpisal(null);
		}

	}


	/**
	*  javadoc for messageType
	*/
	private CodeValuePhi messageType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="messageType")
	@ForeignKey(name="FK_DestSpisal_messageType")
	@Index(name="IX_DestSpisal_messageType")
	public CodeValuePhi getMessageType(){
		return messageType;
	}

	public void setMessageType(CodeValuePhi messageType){
		this.messageType = messageType;
	}
	

	/**
	*  javadoc for fineValidita
	*/
	private Date fineValidita;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fine_validita")
	public Date getFineValidita(){
		return fineValidita;
	}

	public void setFineValidita(Date fineValidita){
		this.fineValidita = fineValidita;
	}

	/**
	*  javadoc for pec
	*/
	private String pec;

	@Column(name="pec")
	public String getPec(){
		return pec;
	}

	public void setPec(String pec){
		this.pec = pec;
	}

	/**
	*  javadoc for tipoInvio
	*/
	private CodeValuePhi tipoInvio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoInvio")
	@ForeignKey(name="FK_DestSpisal_tipoInvio")
	//@Index(name="IX_DestinatariNotifiche_tipoInvio")
	public CodeValuePhi getTipoInvio(){
		return tipoInvio;
	}

	public void setTipoInvio(CodeValuePhi tipoInvio){
		this.tipoInvio = tipoInvio;
	}

	/**
	*  javadoc for descrizioneSipral
	*/
	private String descrizioneSipral;

	@Column(name="descrizione_sipral")
	public String getDescrizioneSipral(){
		return descrizioneSipral;
	}

	public void setDescrizioneSipral(String descrizioneSipral){
		this.descrizioneSipral = descrizioneSipral;
	}

	/**
	*  javadoc for descrizione
	*/
	private String descrizione;

	@Column(name="descrizione")
	public String getDescrizione(){
		return descrizione;
	}

	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DestSpisal_sequence")
	@SequenceGenerator(name = "DestSpisal_sequence", sequenceName = "DestSpisal_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
