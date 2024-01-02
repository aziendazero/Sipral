package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.IndexColumn;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.phi.entities.baseEntity.TipoProdFinito;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Soggetto;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "lifestyle")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Lifestyle extends BaseEntity {

	private static final long serialVersionUID = 1061229058L;

	/**
	*  javadoc for argomentiLegge81
	*/
	private List<CodeValueLaw> argomentiLegge81;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="lifestyle_arglegge81", joinColumns = { @JoinColumn(name="LifeStyle_id") }, inverseJoinColumns = { @JoinColumn(name="argomentiLegge81") })
	@ForeignKey(name="FK_arglegge81_LifeStyle", inverseName="FK_LifeStyle_arglegge81")
	@IndexColumn(name="list_index")
	public List<CodeValueLaw> getArgomentiLegge81(){
		return argomentiLegge81;
	}

	public void setArgomentiLegge81(List<CodeValueLaw> argomentiLegge81){
		this.argomentiLegge81 = argomentiLegge81;
	}
	
	/**
	*  javadoc for soggetto
	*/
	private List<Soggetto> soggetto;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="lifestyle", cascade=CascadeType.PERSIST)
	public List<Soggetto> getSoggetto() {
		return soggetto;
	}

	public void setSoggetto(List<Soggetto>list){
		soggetto = list;
	}

	public void addSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
		}
		// add the association
		if(!this.soggetto.contains(soggetto)) {
			this.soggetto.add(soggetto);
			// make the inverse link
			soggetto.setLifestyle(this);
		}
	}

	public void removeSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
			return;
		}
		//add the association
		if(this.soggetto.contains(soggetto)){
			this.soggetto.remove(soggetto);
			//make the inverse link
			soggetto.setLifestyle(null);
		}
	}



	



	/**
	*  javadoc for tipologiaDestinatario
	*/
	private CodeValuePhi tipologiaDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaDestinatario")
	@ForeignKey(name="FK_Lifestyle_tipologiaDestinatario")
	//@Index(name="IX_Lifestyle_tipologiaDestinatario")
	public CodeValuePhi getTipologiaDestinatario(){
		return tipologiaDestinatario;
	}

	public void setTipologiaDestinatario(CodeValuePhi tipologiaDestinatario){
		this.tipologiaDestinatario = tipologiaDestinatario;
	}

	/**
	*  javadoc for tipologiaRichiedente
	*/
	private CodeValuePhi tipologiaRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaRichiedente")
	@ForeignKey(name="FK_Lifestyle_tipologiaRichiedente")
	//@Index(name="IX_Lifestyle_tipologiaRichiedente")
	public CodeValuePhi getTipologiaRichiedente(){
		return tipologiaRichiedente;
	}

	public void setTipologiaRichiedente(CodeValuePhi tipologiaRichiedente){
		this.tipologiaRichiedente = tipologiaRichiedente;
	}

	/**
	*  javadoc for tipoAttivita
	*/
	private List<CodeValuePhi> tipoAttivita;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="lifestyle_tipoattivita", joinColumns = { @JoinColumn(name="Lifestyle_id") }, inverseJoinColumns = { @JoinColumn(name="tipoAttivita") })
	@ForeignKey(name="FK_tipoAttivita_Lifestyle", inverseName="FK_Lifestyle_tipoAttivita")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(List<CodeValuePhi> tipoAttivita){
		this.tipoAttivita = tipoAttivita;
	}


	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Lifestyle_procpratiche")
	//@Index(name="IX_Lifestyle_procpratiche")
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

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Lifestyle_id")
	@ForeignKey(name="FK_tipoProdFinito_Lifestyle")
	//@Index(name="IX_tipoProdFinito_Lifestyle")
	public List<TipoProdFinito> getTipoProdFinito() {
		return tipoProdFinito;
	}

	public void setTipoProdFinito(List<TipoProdFinito>list){
		tipoProdFinito = list;
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
	@JoinTable(name="lifestyle_argomenti", joinColumns = { @JoinColumn(name="Lifestyle_id") }, inverseJoinColumns = { @JoinColumn(name="argomenti") })
	@ForeignKey(name="FK_argomenti_Lifestyle", inverseName="FK_Lifestyle_argomenti")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getArgomenti(){
		return argomenti;
	}

	public void setArgomenti(List<CodeValuePhi> argomenti){
		this.argomenti = argomenti;
	}

	/**
	*  javadoc for settoreDestinatario
	*/
	private CodeValuePhi settoreDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreDestinatario")
	@ForeignKey(name="FK_Lifestyle_settoreDestinatario")
	//@Index(name="IX_Lifestyle_settoreDestinatario")
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
	*  javadoc for setting
	*/
	private CodeValuePhi setting;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="setting")
	@ForeignKey(name="FK_Lifestyle_setting")
	//@Index(name="IX_Lifestyle_setting")
	public CodeValuePhi getSetting(){
		return setting;
	}

	public void setSetting(CodeValuePhi setting){
		this.setting = setting;
	}

	/**
	*  javadoc for settoreRichiedente
	*/
	private CodeValuePhi settoreRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreRichiedente")
	@ForeignKey(name="FK_Lifestyle_settoreRichiedente")
	//@Index(name="IX_Lifestyle_settoreRichiedente")
	public CodeValuePhi getSettoreRichiedente(){
		return settoreRichiedente;
	}

	public void setSettoreRichiedente(CodeValuePhi settoreRichiedente){
		this.settoreRichiedente = settoreRichiedente;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Lifestyle_sequence")
	@SequenceGenerator(name = "Lifestyle_sequence", sequenceName = "Lifestyle_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
