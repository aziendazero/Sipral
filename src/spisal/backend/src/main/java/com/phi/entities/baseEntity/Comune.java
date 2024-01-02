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

import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.entities.baseEntity.DestinatariNotifiche;

@javax.persistence.Entity
@Table(name = "comune")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Comune extends BaseEntity {

	private static final long serialVersionUID = 146429147L;


	/**
	*  javadoc for destinatariNotifiche
	*/
	private DestinatariNotifiche destinatariNotifiche;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="destinatari_notifiche_id")
	@ForeignKey(name="FK_Cmne_destinatariNtifiche")
	//@Index(name="IX_Cmne_destinatariNtifiche")
	public DestinatariNotifiche getDestinatariNotifiche(){
		return destinatariNotifiche;
	}

	public void setDestinatariNotifiche(DestinatariNotifiche destinatariNotifiche){
		this.destinatariNotifiche = destinatariNotifiche;
	}


	/**
	*  javadoc for sel
	*/
	private Boolean sel;

	@Column(name="sel")
	public Boolean getSel(){
		return sel;
	}

	public void setSel(Boolean sel){
		this.sel = sel;
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
	*  javadoc for comune
	*/
	private CodeValueCity comune;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comune")
	@ForeignKey(name="FK_Comune_comune")
	//@Index(name="IX_Comune_comune")
	public CodeValueCity getComune(){
		return comune;
	}

	public void setComune(CodeValueCity comune){
		this.comune = comune;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Comune_sequence")
	@SequenceGenerator(name = "Comune_sequence", sequenceName = "Comune_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
