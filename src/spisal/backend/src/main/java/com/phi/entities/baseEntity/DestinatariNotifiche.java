package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValuePhi;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.Comune;

@javax.persistence.Entity
@Table(name = "destinatari_notifiche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DestinatariNotifiche extends BaseEntity {

	private static final long serialVersionUID = 66753933L;


	/**
	*  javadoc for comuni
	*/
	private List<Comune> comuni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="destinatariNotifiche", cascade=CascadeType.PERSIST)
	public List<Comune> getComuni() {
		return comuni;
	}

	public void setComuni(List<Comune>list){
		comuni = list;
	}

	public void addComuni(Comune comuni) {
		if (this.comuni == null) {
			this.comuni = new ArrayList<Comune>();
		}
		// add the association
		if(!this.comuni.contains(comuni)) {
			this.comuni.add(comuni);
			// make the inverse link
			comuni.setDestinatariNotifiche(this);
		}
	}

	public void removeComuni(Comune comuni) {
		if (this.comuni == null) {
			this.comuni = new ArrayList<Comune>();
			return;
		}
		//add the association
		if(this.comuni.contains(comuni)){
			this.comuni.remove(comuni);
			//make the inverse link
			comuni.setDestinatariNotifiche(null);
		}
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
	@ForeignKey(name="FK_DestinatariNotifiche_tipoInvio")
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

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_DestinatariNotifiche_type")
	//@Index(name="IX_DestinatariNotifiche_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DestinatariNotifiche_sequence")
	@SequenceGenerator(name = "DestinatariNotifiche_sequence", sequenceName = "DestinatariNotifiche_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
