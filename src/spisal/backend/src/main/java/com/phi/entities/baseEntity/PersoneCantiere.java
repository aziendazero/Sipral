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
@javax.persistence.Entity
@Table(name = "persone_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
/**
 * Persone in cantiere: rappresenta le varie figure (persone fisiche) di riferimento presenti nel cantiere
 * @author 510087
 *
 */
public class PersoneCantiere extends BaseEntity {

	private static final long serialVersionUID = 827427515L;


	/**
	*  javadoc for cantiere
	*/
	private Cantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_PersoneCantiere_cantiere")
	//@Index(name="IX_PersoneCantiere_cantiere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}


	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="FK_PersoneCantiere_ruolo")
	//@Index(name="IX_PersoneCantiere_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
	}


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_PersoneCantiere_person")
	//@Index(name="IX_PersoneCantiere_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PersoneCantiere_sequence")
	@SequenceGenerator(name = "PersoneCantiere_sequence", sequenceName = "PersoneCantiere_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
