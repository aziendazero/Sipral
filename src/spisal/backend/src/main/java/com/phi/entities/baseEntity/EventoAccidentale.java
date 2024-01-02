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
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.phi.entities.role.Person;

@javax.persistence.Entity
@Table(name = "evento_accidentale")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class EventoAccidentale extends BaseEntity {

	private static final long serialVersionUID = 1493104192L;


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_EventoAccidentale_person")
	//@Index(name="IX_EventoAccidentale_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}


	/**
	*  javadoc for provvedimenti
	*/
	private String provvedimenti;

	@Column(name="provvedimenti")
	public String getProvvedimenti(){
		return provvedimenti;
	}

	public void setProvvedimenti(String provvedimenti){
		this.provvedimenti = provvedimenti;
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
	*  javadoc for tipo
	*/
	private String tipo;

	@Column(name="tipo")
	public String getTipo(){
		return tipo;
	}

	public void setTipo(String tipo){
		this.tipo = tipo;
	}

	/**
	*  javadoc for dataEvento
	*/
	private Date dataEvento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_evento")
	public Date getDataEvento(){
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento){
		this.dataEvento = dataEvento;
	}


	/**
	*  javadoc for schedaEsposti
	*/
	private SchedaEsposti schedaEsposti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="scheda_esposti_id")
	@ForeignKey(name="FK_EventoAcc_SchedaEsp")
	//@Index(name="IX_EventoAcc_SchedaEsp")
	public SchedaEsposti getSchedaEsposti(){
		return schedaEsposti;
	}

	public void setSchedaEsposti(SchedaEsposti schedaEsposti){
		this.schedaEsposti = schedaEsposti;
	}

	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "EventoAcc_sequence")
	@SequenceGenerator(name = "EventoAcc_sequence", sequenceName = "EventoAcc_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
