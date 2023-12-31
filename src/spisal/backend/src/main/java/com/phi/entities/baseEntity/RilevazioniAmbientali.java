package com.phi.entities.baseEntity;

// Generated 15-lug-2015 13.47.19 by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;

/**
 * Sopralluoghi generated by hbm2java
 */

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import com.phi.entities.baseEntity.Attivita;

@Entity
@Table(name = "rilevazioni_ambientali")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class RilevazioniAmbientali extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -737145022101638324L;

	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@OneToOne(fetch=FetchType.LAZY, mappedBy="rilevazioneAmbientale", cascade=CascadeType.PERSIST)
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Rilevazioni_sequence")
	@SequenceGenerator(name = "Rilevazioni_sequence", sequenceName = "Rilevazioni_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private String elencoTesti;
	
	@Column(name = "elenco_testi")
	public String getElencoTesti() {
		return this.elencoTesti;
	}

	public void setElencoTesti(String elencoTesti) {
		this.elencoTesti = elencoTesti;
	}
	
	private String allegati;
	
	@Column(name = "allegati")
	public String getAllegati() {
		return this.allegati;
	}

	public void setAllegati(String allegati) {
		this.allegati = allegati;
	}
	
	private String descrizioneLuoghi;
	
	@Column(name = "descrizione_luoghi")
	public String getDescrizioneLuoghi() {
		return this.descrizioneLuoghi;
	}

	public void setDescrizioneLuoghi(String descrizioneLuoghi) {
		this.descrizioneLuoghi = descrizioneLuoghi;
	}
	
	private String descrizioneEvento;
	
	@Column(name = "descrizione_evento")
	public String getDescrizioneEvento() {
		return this.descrizioneEvento;
	}

	public void setDescrizioneEvento(String descrizioneEvento) {
		this.descrizioneEvento = descrizioneEvento;
	}
	
	private String attivitaSvolta;
	
	@Column(name = "attivita_svolta")
	public String getAttivitaSvolta() {
		return this.attivitaSvolta;
	}

	public void setAttivitaSvolta(String attivitaSvolta) {
		this.attivitaSvolta = attivitaSvolta;
	}
	
	private boolean violazioni;
	
	@Column(name="violazioni")
	public boolean getViolazioni() {
		return violazioni;
	}

	public void setViolazioni(boolean violazioni) {
		this.violazioni = violazioni;
	}	

	private String conclusioni;
	
	@Column(name = "conclusioni")
	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}
	
	private String note;
	
	@Column(name = "note", length=2500)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
