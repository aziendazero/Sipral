package com.phi.entities.baseEntity;

// Generated 15-lug-2015 13.47.19 by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
@Entity
@Table(name = "commissioni")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Commissioni extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5285171900287964843L;

	/**
	*  javadoc for durataSessione
	*/
	private Date durataSessione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="durata_sessione")
	public Date getDurataSessione(){
		return durataSessione;
	}

	public void setDurataSessione(Date durataSessione){
		this.durataSessione = durataSessione;
	}

	/**
	*  javadoc for elencoSoggetti
	*/
	private String elencoSoggetti;

	@Column(name="elenco_soggetti", length=1000)
	public String getElencoSoggetti(){
		return elencoSoggetti;
	}

	public void setElencoSoggetti(String elencoSoggetti){
		this.elencoSoggetti = elencoSoggetti;
	}

	/**
	*  javadoc for nrSoggetti
	*/
	private String nrSoggetti;

	@Column(name="nr_soggetti")
	public String getNrSoggetti(){
		return nrSoggetti;
	}

	public void setNrSoggetti(String nrSoggetti){
		this.nrSoggetti = nrSoggetti;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Commissioni_sequence")
	@SequenceGenerator(name = "Commissioni_sequence", sequenceName = "Commissioni_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue tipoCommissione;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_commissione")
	@ForeignKey(name="FK_Comm_type")
	//@Index(name="IX_Comm_type")
	public CodeValue getTipoCommissione() {
		return tipoCommissione;
	}

	public void setTipoCommissione(CodeValue tipoCommissione) {
		this.tipoCommissione = tipoCommissione;
	}
	
	private String altraCommissione;
	
	@Column(name = "altra_commissione"/*, length = 20*/)
	public String getAltraCommissione() {
		return this.altraCommissione;
	}

	public void setAltraCommissione(String altraCommissione) {
		this.altraCommissione = altraCommissione;
	}
	
	private CodeValue tipoConferenza;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_conferenza")
	@ForeignKey(name="FK_Conf_type")
	//@Index(name="IX_Conf_type")
	public CodeValue getTipoConferenza() {
		return tipoConferenza;
	}

	public void setTipoConferenza(CodeValue tipoConferenza) {
		this.tipoConferenza = tipoConferenza;
	}
	
	private String motivo;
	
	@Column(name = "motivo"/*, length = 20*/)
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	private String esito;
	
	@Column(name = "esito"/*, length = 20*/)
	public String getEsito() {
		return this.esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}
	
	private String allegati;
	
	@Column(name = "allegati")
	public String getAllegati() {
		return this.allegati;
	}

	public void setAllegati(String allegati) {
		this.allegati = allegati;
	}
	
	private String conclusioni;
	
	@Column(name = "conclusioni")
	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}

}
