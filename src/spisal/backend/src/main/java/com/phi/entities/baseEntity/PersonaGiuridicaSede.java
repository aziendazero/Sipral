package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "persona_giuridica_sede")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited

/**
 * Persone in cantiere: rappresenta le varie figure (persone fisiche) di riferimento presenti nel cantiere
 * @author 510087
 *
 */
public class PersonaGiuridicaSede extends BaseEntity {

	private static final long serialVersionUID = -1986383044344807549L;

	/**
	*  javadoc for checked
	*/
	private Boolean checked = false;

	@Column(name="checked")
	public Boolean getChecked(){
		return checked;
	}

	public void setChecked(Boolean checked){
		this.checked = checked;
	}

	/**
	*  javadoc for tipoDitta
	*/
	private CodeValuePhi tipoDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoDitta")
	@ForeignKey(name="FK_PersonaGiuridicaSede_tipoDitta")
	//@Index(name="IX_PersonaGiuridicaSede_tipoDitta")
	public CodeValuePhi getTipoDitta(){
		return tipoDitta;
	}

	public void setTipoDitta(CodeValuePhi tipoDitta){
		this.tipoDitta = tipoDitta;
	}


	/**
	*  javadoc for parereTecnico
	*/
	private ParereTecnico parereTecnico;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="parere_tecnico_id")
	@ForeignKey(name="FK_DittaSede_prrTcnic")
	//@Index(name="IX_DittaSede_prrTcnic")
	public ParereTecnico getParereTecnico(){
		return parereTecnico;
	}

	public void setParereTecnico(ParereTecnico parereTecnico){
		this.parereTecnico = parereTecnico;
	}


	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_PersonaGiuridicaSede_type")
	//@Index(name="IX_PersonaGiuridicaSede_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for vigilanza
	*/
	private Vigilanza vigilanza;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_PrsnaGiuridicaSd_vigilnz")
	//@Index(name="IX_PrsnaGiuridicaSd_vigilnz")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}

	/**
	*  javadoc for sede
	*/
	private Sedi sede;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_PersonaGiuridicaSede_sede")
	//@Index(name="IX_PersonaGiuridicaSede_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}

	/**
	*  javadoc for personaGiuridica
	*/
	private PersoneGiuridiche personaGiuridica;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persona_giuridica_id")
	@ForeignKey(name="FK_PrsnGiuridicSd_prsnGurdc")
	//@Index(name="IX_PrsnGiuridicSd_prsnGurdc")
	public PersoneGiuridiche getPersonaGiuridica(){
		return personaGiuridica;
	}

	public void setPersonaGiuridica(PersoneGiuridiche personaGiuridica){
		this.personaGiuridica = personaGiuridica;
	}
	
	//methods needed for baseEntity implementation
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Pgs_sequence")
	@SequenceGenerator(name = "Pgs_sequence", sequenceName = "Pgs_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
