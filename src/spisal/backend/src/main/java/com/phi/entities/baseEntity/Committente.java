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

import com.phi.entities.role.Person;
import com.phi.entities.baseEntity.Vigilanza;

@javax.persistence.Entity
@Table(name = "committenti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Committente di un cantiere
 * @author 510087
 *
 */
public class Committente extends BaseEntity {

	private static final long serialVersionUID = 828683116L;


	/**
	*  javadoc for vigilanza
	*/
	private Vigilanza vigilanza;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_Committente_vigilanza")
	//@Index(name="IX_Committente_vigilanza")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}

	/**
	*  persona fisica: un committente può essere una persona fisica o una persona giuridica
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_committ_persGiurid")
	//@Index(name="IX_committ_persGiurid")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}



	/**
	*  persona fisica: un committente può essere una persona fisica o una persona giuridica
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_Committente_persFisica")
	//@Index(name="IX_Committente_persFisica")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}

	/**
	*  javadoc for cantiere
	*/
	private Cantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_Committente_cantiere")
	//@Index(name="IX_Committente_cantiere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Committente_sequence")
	@SequenceGenerator(name = "Committente_sequence", sequenceName = "Committente_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
