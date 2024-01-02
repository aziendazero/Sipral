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
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.TipoProdFinito;
import com.phi.entities.dataTypes.CodeValueLaw;

@javax.persistence.Entity
@Table(name = "benessere_org")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class BenessereOrg extends BaseEntity {

	private static final long serialVersionUID = 233551037L;

	/**
	*  javadoc for sportello
	*/
	private CodeValuePhi sportello;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sportello")
	@ForeignKey(name="FK_BenessereOrg_sportello")
	//@Index(name="IX_BenessereOrg_sportello")
	public CodeValuePhi getSportello(){
		return sportello;
	}

	public void setSportello(CodeValuePhi sportello){
		this.sportello = sportello;
	}

	/**
	*  javadoc for argomentiLegge81
	*/
	private List<CodeValueLaw> argomentiLegge81;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="benorg_arglegge81", joinColumns = { @JoinColumn(name="benorg_id") }, inverseJoinColumns = { @JoinColumn(name="arglegge81") })
	@ForeignKey(name="FK_arglegge81_benorg", inverseName="FK_benorg_arglegge81")
	@IndexColumn(name="list_index")
	public List<CodeValueLaw> getArgomentiLegge81(){
		return argomentiLegge81;
	}

	public void setArgomentiLegge81(List<CodeValueLaw> argomentiLegge81){
		this.argomentiLegge81 = argomentiLegge81;
	}

	/**
	*  javadoc for tipologiaDestinatario
	*/
	private CodeValuePhi tipologiaDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaDestinatario")
	@ForeignKey(name="FK_benorg_tipologiaDestinatario")
	//@Index(name="IX_benorg_tipologiaDestinatario")
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
	@ForeignKey(name="FK_benorg_tipologiaRichiedente")
	//@Index(name="IX_benorg_tipologiaRichiedente")
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

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="BenessereOrg_id")
	@ForeignKey(name="FK_tpoProdFnto_BenessereOrg")
	//@Index(name="IX_tpoProdFnto_BenessereOrg")
	public List<TipoProdFinito> getTipoProdFinito() {
		return tipoProdFinito;
	}

	public void setTipoProdFinito(List<TipoProdFinito>list){
		tipoProdFinito = list;
	}


	/**
	*  javadoc for argomenti
	*/
	private List<CodeValuePhi> argomenti;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="benessereorg_argomenti", joinColumns = { @JoinColumn(name="benorg_id") }, inverseJoinColumns = { @JoinColumn(name="argomenti") })
	@ForeignKey(name="FK_argomenti_benorg", inverseName="FK_benorg_argomenti")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getArgomenti(){
		return argomenti;
	}

	public void setArgomenti(List<CodeValuePhi> argomenti){
		this.argomenti = argomenti;
	}

	

	/**
	*  javadoc for tipoAttivita
	*/
	private List<CodeValuePhi> tipoAttivita;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="benessereorg_tipoattivita", joinColumns = { @JoinColumn(name="BenessereOrg_id") }, inverseJoinColumns = { @JoinColumn(name="tipoattivita") })
	@ForeignKey(name="FK_tipoattivita_benorg", inverseName="FK_benorg_tipoattivita")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(List<CodeValuePhi> tipoAttivita){
		this.tipoAttivita = tipoAttivita;
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
	*  javadoc for storia
	*/
	private String storia;

	@Column(name="storia", length=4000)
	public String getStoria(){
		return storia;
	}

	public void setStoria(String storia){
		this.storia = storia;
	}

	
	
	/**
	*  javadoc for oggettoCtrl
	*/
	private String oggettoCtrl;

	@Column(name="oggetto_ctrl")
	public String getOggettoCtrl(){
		return oggettoCtrl;
	}

	public void setOggettoCtrl(String oggettoCtrl){
		this.oggettoCtrl = oggettoCtrl;
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
	*  javadoc for settoreDestinatario
	*/
	private CodeValuePhi settoreDestinatario;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreDestinatario")
	@ForeignKey(name="FK_benorg_settoreDestinatario")
	//@Index(name="IX_benorg_settoreDestinatario")
	public CodeValuePhi getSettoreDestinatario(){
		return settoreDestinatario;
	}

	public void setSettoreDestinatario(CodeValuePhi settoreDestinatario){
		this.settoreDestinatario = settoreDestinatario;
	}

	/**
	*  javadoc for settoreRichiedente
	*/
	private CodeValuePhi settoreRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settoreRichiedente")
	@ForeignKey(name="FK_benorg_settoreRichiedente")
	//@Index(name="IX_benorg_settoreRichiedente")
	public CodeValuePhi getSettoreRichiedente(){
		return settoreRichiedente;
	}

	public void setSettoreRichiedente(CodeValuePhi settoreRichiedente){
		this.settoreRichiedente = settoreRichiedente;
	}

	
	/**
	*  javadoc for tipoPratica
	*/
	private CodeValuePhi tipoPratica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoPratica")
	@ForeignKey(name="FK_benorg_tipoPratica")
	//@Index(name="IX_benorg_tipoPratica")
	public CodeValuePhi getTipoPratica(){
		return tipoPratica;
	}

	public void setTipoPratica(CodeValuePhi tipoPratica){
		this.tipoPratica = tipoPratica;
	}


	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_benorg_procpratiche")
	//@Index(name="IX_benorg_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "BenessereOrg_sequence")
	@SequenceGenerator(name = "BenessereOrg_sequence", sequenceName = "BenessereOrg_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
