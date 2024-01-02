package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;

import com.phi.entities.baseEntity.PNCCantiere;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import org.hibernate.annotations.Index;
@javax.persistence.Entity
@Table(name = "pnc_ditte_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Audited
public class PNCDitteCantiere extends BaseEntity {

	private static final long serialVersionUID = 816680708L;

	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="DitteCantiere_ruolo_FK")
	//@Index(name="IX_PNCDitteCantiere_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCDC_sequence")
	@SequenceGenerator(name = "PNCDC_sequence", sequenceName = "PNCDC_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for cantiere
	*/
	private PNCCantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_PND_cantiere")
	//@Index(name="IX_PND_cantiere")
	public PNCCantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(PNCCantiere cantiere){
		this.cantiere = cantiere;
	}
	
	/**
	*  javadoc for denominazione
	*/
	private String denominazione;

	@Column(name="denominazione")
	public String getDenominazione(){
		return denominazione;
	}

	public void setDenominazione(String denominazione){
		this.denominazione = denominazione;
	}
	
	/**
	*  Codice Fiscale
	*/
	private String codiceFiscale;

	@Column(name="codice_fiscale")
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	
	/**
	*  Partita IVA
	*/
	private String patritaIva;

	@Column(name="partita_iva")
	public String getPatritaIva() {
		return patritaIva;
	}

	public void setPatritaIva(String patritaIva) {
		this.patritaIva = patritaIva;
	}


}
