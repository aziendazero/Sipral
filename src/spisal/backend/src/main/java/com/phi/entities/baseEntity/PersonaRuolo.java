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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

import javax.persistence.OneToOne;
import com.phi.entities.baseEntity.Vigilanza;

@javax.persistence.Entity
@Table(name = "persona_ruolo")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Persone in cantiere: rappresenta le varie figure (persone fisiche) di riferimento presenti nel cantiere
 * @author 510087
 *
 */
public class PersonaRuolo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6905431706250216752L;


	/**
	*  javadoc for vigilanza
	*/
	private Vigilanza vigilanza;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_PersonaRuolo_vigilanza")
	//@Index(name="IX_PersonaRuolo_vigilanza")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}



	/**
	*  javadoc for person
	*/
	private Person person;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_PersonaRuolo_person")
	//@Index(name="IX_PersonaRuolo_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}
	
	//methods needed for baseEntity implementation
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PerRole_sequence")
	@SequenceGenerator(name = "PerRole_sequence", sequenceName = "PerRole_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="FK_PersonaRuolo_ruolo")
	//@Index(name="IX_PersonaRuolo_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
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
}
