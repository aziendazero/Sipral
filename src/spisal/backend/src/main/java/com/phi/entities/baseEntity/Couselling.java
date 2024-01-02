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


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Attivita;
@javax.persistence.Entity
@Table(name = "couselling")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Couselling extends BaseEntity {

	private static final long serialVersionUID = 427675136L;

	/**
	*  javadoc for descrizione
	*/
	private String descrizione;

	@Column(name="descrizione", length=2500)
	public String getDescrizione(){
		return descrizione;
	}

	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}

	/**
	*  javadoc for disassuefazione
	*/
	private Boolean disassuefazione;

	@Column(name="disassuefazione")
	public Boolean getDisassuefazione(){
		return disassuefazione;
	}

	public void setDisassuefazione(Boolean disassuefazione){
		this.disassuefazione = disassuefazione;
	}


	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Couselling_attivita")
	//@Index(name="IX_Couselling_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Couselling_sequence")
	@SequenceGenerator(name = "Couselling_sequence", sequenceName = "Couselling_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
