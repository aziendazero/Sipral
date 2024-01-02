package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.dataTypes.CodeValuePhi;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;

import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.TipoProdFinito;
import com.phi.entities.dataTypes.CodeValueLaw;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@javax.persistence.Entity
@Table(name = "formazione")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Formazione extends BaseEntity {

	private static final long serialVersionUID = 167218561L;

	/**
	*  javadoc for dateCorso
	*/
	private Date dateCorso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_corso")
	public Date getDateCorso(){
		return dateCorso;
	}

	public void setDateCorso(Date dateCorso){
		this.dateCorso = dateCorso;
	}

	/**
	*  javadoc for hhFormazione
	*/
	private Double hhFormazione;

	@Column(name="hh_formazione")
	public Double getHhFormazione(){
		return hhFormazione;
	}

	public void setHhFormazione(Double hhFormazione){
		this.hhFormazione = hhFormazione;
	}

	/**
	*  javadoc for argomentiLegge81
	*/
	private List<CodeValueLaw> argomentiLegge81;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="formazione_arglegge81", joinColumns = { @JoinColumn(name="Formazione_id") }, inverseJoinColumns = { @JoinColumn(name="argomentiLegge81") })
	@ForeignKey(name="FK_arglegge81_Formazione", inverseName="FK_Formazione_arglegge81")
	@IndexColumn(name="list_index")
	public List<CodeValueLaw> getArgomentiLegge81(){
		return argomentiLegge81;
	}

	public void setArgomentiLegge81(List<CodeValueLaw> argomentiLegge81){
		this.argomentiLegge81 = argomentiLegge81;
	}

	/**
	*  javadoc for numerosita
	*/
	private String numerosita;

	@Column(name="numerosita")
	public String getNumerosita(){
		return numerosita;
	}

	public void setNumerosita(String numerosita){
		this.numerosita = numerosita;
	}

	/**
	*  javadoc for dettagli
	*/
	private String dettagli;

	@Column(name="dettagli", length=4000)
	public String getDettagli(){
		return dettagli;
	}

	public void setDettagli(String dettagli){
		this.dettagli = dettagli;
	}

	/**
	*  javadoc for tipologiaRichiedente
	*/
	private CodeValuePhi tipologiaRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaRichiedente")
	@ForeignKey(name="FK_Formazione_tipologiaRichiedente")
	//@Index(name="IX_Formazione_tipologiaRichiedente")
	public CodeValuePhi getTipologiaRichiedente(){
		return tipologiaRichiedente;
	}

	public void setTipologiaRichiedente(CodeValuePhi tipologiaRichiedente){
		this.tipologiaRichiedente = tipologiaRichiedente;
	}


	/**
	*  javadoc for tipoProdFinito
	*/
	private List<TipoProdFinito> tipoProdFinito;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="formazione", cascade=CascadeType.PERSIST)
	public List<TipoProdFinito> getTipoProdFinito() {
		return tipoProdFinito;
	}

	public void setTipoProdFinito(List<TipoProdFinito>list){
		tipoProdFinito = list;
	}

	public void addTipoProdFinito(TipoProdFinito tipoProdFinito) {
		if (this.tipoProdFinito == null) {
			this.tipoProdFinito = new ArrayList<TipoProdFinito>();
		}
		// add the association
		if(!this.tipoProdFinito.contains(tipoProdFinito)) {
			this.tipoProdFinito.add(tipoProdFinito);
			// make the inverse link
			tipoProdFinito.setFormazione(this);
		}
	}

	public void removeTipoProdFinito(TipoProdFinito tipoProdFinito) {
		if (this.tipoProdFinito == null) {
			this.tipoProdFinito = new ArrayList<TipoProdFinito>();
			return;
		}
		//add the association
		if(this.tipoProdFinito.contains(tipoProdFinito)){
			this.tipoProdFinito.remove(tipoProdFinito);
			//make the inverse link
			tipoProdFinito.setFormazione(null);
		}
	}



	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Formazione_procpratiche")
	//@Index(name="IX_Formazione_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}


	/**
	*  javadoc for argomenti
	*/
	private List<CodeValuePhi> argomenti;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="formazione_argomenti", joinColumns = { @JoinColumn(name="formazione_id") }, inverseJoinColumns = { @JoinColumn(name="argomenti") })
	@ForeignKey(name="FK_argomenti_formazione", inverseName="FK_formazione_argomenti")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getArgomenti(){
		return argomenti;
	}

	public void setArgomenti(List<CodeValuePhi> argomenti){
		this.argomenti = argomenti;
	}

	/**
	*  javadoc for settoreRichiedente
	*/
	private CodeValuePhi settoreRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreRichiedente")
	@ForeignKey(name="FK_Formazione_settoreRichiedente")
	//@Index(name="IX_Formazione_settoreRichiedente")
	public CodeValuePhi getSettoreRichiedente(){
		return settoreRichiedente;
	}

	public void setSettoreRichiedente(CodeValuePhi settoreRichiedente){
		this.settoreRichiedente = settoreRichiedente;
	}

	/**
	*  javadoc for settoreDestinatario
	*/
	private CodeValuePhi settoreDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreDestinatario")
	@ForeignKey(name="FK_Formazione_settoreDestinatario")
	//@Index(name="IX_Formazione_settoreDestinatario")
	public CodeValuePhi getSettoreDestinatario(){
		return settoreDestinatario;
	}

	public void setSettoreDestinatario(CodeValuePhi settoreDestinatario){
		this.settoreDestinatario = settoreDestinatario;
	}

	/**
	*  javadoc for tipoAttivita
	*/
	private List<CodeValuePhi> tipoAttivita;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="formazione_tipoattivita", joinColumns = { @JoinColumn(name="formazione_id") }, inverseJoinColumns = { @JoinColumn(name="tipoattivita") })
	@ForeignKey(name="FK_tipoattivita_formazione", inverseName="FK_formazione_tipoattivita")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(List<CodeValuePhi> tipoAttivita){
		this.tipoAttivita = tipoAttivita;
	}

	/**
	*  javadoc for tipologiaDestinatario
	*/
	private CodeValuePhi tipologiaDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaDestinatario")
	@ForeignKey(name="FK_Formazione_tipologiaDestinatario")
	//@Index(name="IX_Formazione_tipologiaDestinatario")
	public CodeValuePhi getTipologiaDestinatario(){
		return tipologiaDestinatario;
	}

	public void setTipologiaDestinatario(CodeValuePhi tipologiaDestinatario){
		this.tipologiaDestinatario = tipologiaDestinatario;
	}


	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Formazione_sequence")
	@SequenceGenerator(name = "Formazione_sequence", sequenceName = "Formazione_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
