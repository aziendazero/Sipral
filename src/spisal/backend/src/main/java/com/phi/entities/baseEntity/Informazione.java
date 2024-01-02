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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.TipoProdFinito;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValueLaw;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@javax.persistence.Entity
@Table(name = "informazione")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Informazione extends BaseEntity {

	private static final long serialVersionUID = 516607459L;

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
	*  javadoc for argomentiLegge81
	*/
	private List<CodeValueLaw> argomentiLegge81;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="info_arglegge81", joinColumns = { @JoinColumn(name="Informazione_id") }, inverseJoinColumns = { @JoinColumn(name="argomentiLegge81") })
	@ForeignKey(name="FK_arglegge81_info", inverseName="FK_info_arglegge81")
	@IndexColumn(name="list_index")
	public List<CodeValueLaw> getArgomentiLegge81(){
		return argomentiLegge81;
	}

	public void setArgomentiLegge81(List<CodeValueLaw> argomentiLegge81){
		this.argomentiLegge81 = argomentiLegge81;
	}

	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Informazione_procpratiche")
	//@Index(name="IX_Informazione_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}



	/**
	*  javadoc for tipoProdFinito
	*/
	private List<TipoProdFinito> tipoProdFinito;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="informazione", cascade=CascadeType.PERSIST)
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
			tipoProdFinito.setInformazione(this);
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
			tipoProdFinito.setInformazione(null);
		}
	}


	/**
	*  javadoc for tipologiaRichiedente
	*/
	private CodeValuePhi tipologiaRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaRichiedente")
	@ForeignKey(name="FK_Informazione_tipologiaRichiedente")
	//@Index(name="IX_Informazione_tipologiaRichiedente")
	public CodeValuePhi getTipologiaRichiedente(){
		return tipologiaRichiedente;
	}

	public void setTipologiaRichiedente(CodeValuePhi tipologiaRichiedente){
		this.tipologiaRichiedente = tipologiaRichiedente;
	}

	/**
	*  javadoc for tipologiaDestinatario
	*/
	private CodeValuePhi tipologiaDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaDestinatario")
	@ForeignKey(name="FK_Informazione_tipologiaDestinatario")
	//@Index(name="IX_Informazione_tipologiaDestinatario")
	public CodeValuePhi getTipologiaDestinatario(){
		return tipologiaDestinatario;
	}

	public void setTipologiaDestinatario(CodeValuePhi tipologiaDestinatario){
		this.tipologiaDestinatario = tipologiaDestinatario;
	}

	/**
	*  javadoc for tipoAttivita
	*/
	private List<CodeValuePhi> tipoAttivita;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="informazione_tipoattivita", joinColumns = { @JoinColumn(name="informazione_id") }, inverseJoinColumns = { @JoinColumn(name="tipoattivita") })
	@ForeignKey(name="FK_tipoattivita_Informazione", inverseName="FK_informazione_tipoattivita")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(List<CodeValuePhi> tipoAttivita){
		this.tipoAttivita = tipoAttivita;
	}

	/**
	*  javadoc for settoreRichiedente
	*/
	private CodeValuePhi settoreRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreRichiedente")
	@ForeignKey(name="FK_Informazione_settoreRichiedente")
	//@Index(name="IX_Informazione_settoreRichiedente")
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
	@ForeignKey(name="FK_Informazione_settoreDestinatario")
	//@Index(name="IX_Informazione_settoreDestinatario")
	public CodeValuePhi getSettoreDestinatario(){
		return settoreDestinatario;
	}

	public void setSettoreDestinatario(CodeValuePhi settoreDestinatario){
		this.settoreDestinatario = settoreDestinatario;
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
	*  javadoc for argomenti
	*/
	private List<CodeValuePhi> argomenti;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="informazione_argomenti", joinColumns = { @JoinColumn(name="informazione_id") }, inverseJoinColumns = { @JoinColumn(name="argomenti") })
	@ForeignKey(name="FK_argomenti_informazione", inverseName="FK_informazione_argomenti")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getArgomenti(){
		return argomenti;
	}

	public void setArgomenti(List<CodeValuePhi> argomenti){
		this.argomenti = argomenti;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Informazione_sequence")
	@SequenceGenerator(name = "Informazione_sequence", sequenceName = "Informazione_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
