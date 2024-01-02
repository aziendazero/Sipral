package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;

import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.CessioneImp;

@javax.persistence.Entity
@Table(name = "imp_monta")
@Audited
public class ImpMonta extends Impianto {

	private static final long serialVersionUID = 2399232L;

	/**
	*  javadoc for matrcomune
	*/
	private String matrcomune;

	@Column(name="matrcomune")
	public String getMatrcomune(){
		return matrcomune;
	}

	public void setMatrcomune(String matrcomune){
		this.matrcomune = matrcomune;
	}

	/**
	*  javadoc for costruzione
	*/
	private Integer costruzione;

	@Column(name="costruzione")
	public Integer getCostruzione(){
		return costruzione;
	}

	public void setCostruzione(Integer costruzione){
		this.costruzione = costruzione;
	}
	
	/**
	*  javadoc for portata
	*/
	private Double portata;

	@Column(name="portata")
	public Double getPortata(){
		return portata;
	}

	public void setPortata(Double portata){
		this.portata = portata;
	}
	
	/**
	*  javadoc for fermate
	*/
	private Integer fermate;
	@Column(name="fermate")
	public Integer getFermate(){
		return fermate;
	}

	public void setFermate(Integer fermate){
		this.fermate = fermate;
	}
	
	/**
	*  javadoc for artEsonero
	*/
	private Integer artEsonero;

	@Column(name="art_esonero")
	public Integer getArtEsonero(){
		return artEsonero;
	}

	public void setArtEsonero(Integer artEsonero){
		this.artEsonero = artEsonero;
	}

	/**
	*  javadoc for letteraTrasm
	*/
	private Date letteraTrasm;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lettera_trasm")
	public Date getLetteraTrasm(){
		return letteraTrasm;
	}

	public void setLetteraTrasm(Date letteraTrasm){
		this.letteraTrasm = letteraTrasm;
	}

	/**
	*  javadoc for sezione
	*/
	private String sezione;

	@Column(name="sezione")
	public String getSezione(){
		return sezione;
	}

	public void setSezione(String sezione){
		this.sezione = sezione;
	}

	/**
	*  javadoc for utenteLettera
	*/
	private CodeValuePhi utenteLettera;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="utenteLettera")
	@ForeignKey(name="FK_ImpMonta_utenteLettera")
	@Index(name="IX_ImpMonta_utenteLettera")
	public CodeValuePhi getUtenteLettera(){
		return utenteLettera;
	}

	public void setUtenteLettera(CodeValuePhi utenteLettera){
		this.utenteLettera = utenteLettera;
	}

	/**
	*  javadoc for protNumero
	*/
	private String protNumero;

	@Column(name="prot_numero")
	public String getProtNumero(){
		return protNumero;
	}

	public void setProtNumero(String protNumero){
		this.protNumero = protNumero;
	}



	/**
	*  javadoc for cessioneImp
	*/
	private List<CessioneImp> cessioneImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impMonta", cascade=CascadeType.PERSIST)
	public List<CessioneImp> getCessioneImp() {
		return cessioneImp;
	}

	public void setCessioneImp(List<CessioneImp>list){
		cessioneImp = list;
	}

	public void addCessioneImp(CessioneImp cessioneImp) {
		if (this.cessioneImp == null) {
			this.cessioneImp = new ArrayList<CessioneImp>();
		}
		// add the association
		if(!this.cessioneImp.contains(cessioneImp)) {
			this.cessioneImp.add(cessioneImp);
			// make the inverse link
			cessioneImp.setImpMonta(this);
		}
	}

	public void removeCessioneImp(CessioneImp cessioneImp) {
		if (this.cessioneImp == null) {
			this.cessioneImp = new ArrayList<CessioneImp>();
			return;
		}
		//add the association
		if(this.cessioneImp.contains(cessioneImp)){
			this.cessioneImp.remove(cessioneImp);
			//make the inverse link
			cessioneImp.setImpMonta(null);
		}
	}


	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impMonta")
	public List<VerificaImp> getVerificaImp() {
		return verificaImp;
	}

	public void setVerificaImp(List<VerificaImp>list){
		verificaImp = list;
	}

	public void addVerificaImp(VerificaImp verificaImp) {
		if (this.verificaImp == null) {
			this.verificaImp = new ArrayList<VerificaImp>();
		}
		// add the association
		if(!this.verificaImp.contains(verificaImp)) {
			this.verificaImp.add(verificaImp);
			// make the inverse link
			verificaImp.setImpMonta(this);
		}
	}

	public void removeVerificaImp(VerificaImp verificaImp) {
		if (this.verificaImp == null) {
			this.verificaImp = new ArrayList<VerificaImp>();
			return;
		}
		//add the association
		if(this.verificaImp.contains(verificaImp)){
			this.verificaImp.remove(verificaImp);
			//make the inverse link
			verificaImp.setImpMonta(null);
		}
	}


	/**
	*  javadoc for porte
	*/
	private CodeValuePhi porte;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="porte")
	@ForeignKey(name="FK_ImpMonta_porte")
	//@Index(name="IX_ImpMonta_porte")
	public CodeValuePhi getPorte(){
		return porte;
	}

	public void setPorte(CodeValuePhi porte){
		this.porte = porte;
	}

	/**
	*  javadoc for manovra
	*/
	private CodeValuePhi manovra;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="manovra")
	@ForeignKey(name="FK_ImpMonta_manovra")
	//@Index(name="IX_ImpMonta_manovra")
	public CodeValuePhi getManovra(){
		return manovra;
	}

	public void setManovra(CodeValuePhi manovra){
		this.manovra = manovra;
	}

	/**
	*  javadoc for motore
	*/
	private CodeValuePhi motore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="motore")
	@ForeignKey(name="FK_ImpMonta_motore")
	//@Index(name="IX_ImpMonta_motore")
	public CodeValuePhi getMotore(){
		return motore;
	}

	public void setMotore(CodeValuePhi motore){
		this.motore = motore;
	}

	/**
	*  javadoc for trazione
	*/
	private CodeValuePhi trazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="trazione")
	@ForeignKey(name="FK_ImpMonta_trazione")
	//@Index(name="IX_ImpMonta_trazione")
	public CodeValuePhi getTrazione(){
		return trazione;
	}

	public void setTrazione(CodeValuePhi trazione){
		this.trazione = trazione;
	}

	/**
	*  javadoc for velocita
	*/
	private CodeValuePhi velocita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="velocita")
	@ForeignKey(name="FK_ImpMonta_velocita")
	//@Index(name="IX_ImpMonta_velocita")
	public CodeValuePhi getVelocita(){
		return velocita;
	}

	public void setVelocita(CodeValuePhi velocita){
		this.velocita = velocita;
	}

	/**
	*  javadoc for corsa
	*/
	private CodeValuePhi corsa;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="corsa")
	@ForeignKey(name="FK_ImpMonta_corsa")
	//@Index(name="IX_ImpMonta_corsa")
	public CodeValuePhi getCorsa(){
		return corsa;
	}

	public void setCorsa(CodeValuePhi corsa){
		this.corsa = corsa;
	}

	/**
	*  javadoc for distanza
	*/
	private CodeValuePhi distanza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="distanza")
	@ForeignKey(name="FK_ImpMonta_distanza")
	//@Index(name="IX_ImpMonta_distanza")
	public CodeValuePhi getDistanza(){
		return distanza;
	}

	public void setDistanza(CodeValuePhi distanza){
		this.distanza = distanza;
	}

//	/**
//	*  javadoc for collaudo
//	*/
//	private Date collaudo;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="collaudo")
//	public Date getCollaudo(){
//		return collaudo;
//	}
//
//	public void setCollaudo(Date collaudo){
//		this.collaudo = collaudo;
//	}

	/**
	*  javadoc for licenza
	*/
	private Date licenza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="licenza")
	public Date getLicenza(){
		return licenza;
	}

	public void setLicenza(Date licenza){
		this.licenza = licenza;
	}

	/**
	*  javadoc for amministratore
	*/
	private CodeValuePhi amministratore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="amministratore")
	@ForeignKey(name="FK_ImpMonta_amministratore")
	//@Index(name="IX_ImpMonta_amministratore")
	public CodeValuePhi getAmministratore(){
		return amministratore;
	}

	public void setAmministratore(CodeValuePhi amministratore){
		this.amministratore = amministratore;
	}

	/**
	*  javadoc for manutentore
	*/
	private CodeValuePhi manutentore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="manutentore")
	@ForeignKey(name="FK_ImpMonta_manutentore")
	//@Index(name="IX_ImpMonta_manutentore")
	public CodeValuePhi getManutentore(){
		return manutentore;
	}

	public void setManutentore(CodeValuePhi manutentore){
		this.manutentore = manutentore;
	}

	/**
	*  javadoc for numeroFabbrica
	*/
	private String numeroFabbrica;

	@Column(name="numero_fabbrica")
	public String getNumeroFabbrica(){
		return numeroFabbrica;
	}

	public void setNumeroFabbrica(String numeroFabbrica){
		this.numeroFabbrica = numeroFabbrica;
	}

	/**
	*  javadoc for costruttore
	*/
	private String costruttore;

	@Column(name="costruttore")
	public String getCostruttore(){
		return costruttore;
	}

	public void setCostruttore(String costruttore){
		this.costruttore = costruttore;
	}

	/**
	*  javadoc for categoria
	*/
	private CodeValuePhi categoria;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="categoria")
	@ForeignKey(name="FK_ImpMonta_categoria")
	//@Index(name="IX_ImpMonta_categoria")
	public CodeValuePhi getCategoria(){
		return categoria;
	}

	public void setCategoria(CodeValuePhi categoria){
		this.categoria = categoria;
	}

//	/**
//	*  javadoc for matricolaComune
//	*/
//	private String matricolaComune;
//
//	@Column(name="matricola_comune")
//	public String getMatricolaComune(){
//		return matricolaComune;
//	}
//
//	public void setMatricolaComune(String matricolaComune){
//		this.matricolaComune = matricolaComune;
//	}

	/**
	*  javadoc for destinazione
	*/
	private CodeValuePhi destinazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="destinazione")
	@ForeignKey(name="FK_ImpMonta_destinazione")
	//@Index(name="IX_ImpMonta_destinazione")
	public CodeValuePhi getDestinazione(){
		return destinazione;
	}

	public void setDestinazione(CodeValuePhi destinazione){
		this.destinazione = destinazione;
	}
	
	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ImpMonta_id")
	@ForeignKey(name="FK_documenti_ImpMonta")
	//@Index(name="IX_documenti_ImpMonta")
	public List<AlfrescoDocument> getDocumenti() {
		return documenti;
	}

	public void setDocumenti(List<AlfrescoDocument>list){
		documenti = list;
	}

	public void addDocumenti(AlfrescoDocument alfrescoDocument) {
		if (this.documenti == null) {
			this.documenti = new ArrayList<AlfrescoDocument>();
		}
		if(!this.documenti.contains(alfrescoDocument)) {
			this.documenti.add(alfrescoDocument);

		}
	}
	
	public void removeDocumenti(AlfrescoDocument alfrescoDocument) {
		if (this.documenti == null) {
			return;
		}
		if(this.documenti.contains(alfrescoDocument)) {
			this.documenti.remove(alfrescoDocument);

		}
	}

}
