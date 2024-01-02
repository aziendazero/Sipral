package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;

import com.phi.entities.actions.VigilanzaAction;
import com.phi.entities.baseEntity.Vigilanza;

import com.phi.entities.role.Person;

import com.phi.entities.baseEntity.PersoneGiuridiche;

import com.phi.entities.baseEntity.Sedi;



@javax.persistence.Entity
@Table(name = "committente_vigilanza")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CommittenteVigilanza extends BaseEntity {

	private static final long serialVersionUID = 1681399000L;


	/**
	*  javadoc for committenteSede
	*/
	private Sedi committenteSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_sede_id")
	@ForeignKey(name="FK_CmmttntVglanza_cmmttntSd")
	//@Index(name="IX_CmmttntVglanza_cmmttntSd")
	public Sedi getCommittenteSede(){
		return committenteSede;
	}

	public void setCommittenteSede(Sedi committenteSede){
		this.committenteSede = committenteSede;
	}



	/**
	*  javadoc for committenteDitta
	*/
	private PersoneGiuridiche committenteDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_ditta_id")
	@ForeignKey(name="FK_CmmttntVglanz_cmmttntDtt")
	//@Index(name="IX_CmmttntVglanz_cmmttntDtt")
	public PersoneGiuridiche getCommittenteDitta(){
		return committenteDitta;
	}

	public void setCommittenteDitta(PersoneGiuridiche committenteDitta){
		this.committenteDitta = committenteDitta;
	}



	/**
	*  javadoc for committenteUtente
	*/
	private Person committenteUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_utente_id")
	@ForeignKey(name="FK_CmmttntVglnz_cmmttntUtnt")
	//@Index(name="IX_CmmttntVglnz_cmmttntUtnt")
	public Person getCommittenteUtente(){
		return committenteUtente;
	}

	public void setCommittenteUtente(Person committenteUtente){
		this.committenteUtente = committenteUtente;
	}

	/**
	*  javadoc for vigilanza
	*/
	private Vigilanza vigilanza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_CmmttenteVglanza_vglanza")
	//@Index(name="IX_CmmttenteVglanza_vglanza")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}


	/**
	*  javadoc for committente
	*/
	private CodeValuePhi committente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente")
	@ForeignKey(name="FK_CommittenteVigilanza_committente")
	//@Index(name="IX_CommittenteVigilanza_committente")
	public CodeValuePhi getCommittente(){
		return committente;
	}

	public void setCommittente(CodeValuePhi committente){
		this.committente = committente;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CommittenteVigilanza_sequence")
	@SequenceGenerator(name = "CommittenteVigilanza_sequence", sequenceName = "CommittenteVigilanza_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Transient
	public String getCommMultiOrigin(){
		
		String toPrint = "";
		if(committente!=null){
			if("Ditta".equals(committente.getCode())){
				return committenteDitta.getDenominazione();
			}
			if("Utente".equals(committente.getCode())){
				return committenteUtente.getName().getFam() + " " + committenteUtente.getName().getGiv();
			}			
		}

		if(committenteDitta==null && committenteUtente==null){
			Vigilanza currentVigilanza = VigilanzaAction.instance().getEntity();
			if(currentVigilanza!=null){
				return currentVigilanza.getCommittente();
			}
		}
		return toPrint;
	}
}
