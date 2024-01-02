package com.phi.entities.baseEntity;

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
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.SchedaGeneratori;
import com.phi.entities.baseEntity.SchedaVasi;

import com.phi.entities.baseEntity.VerificaImp;

import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import com.phi.entities.baseEntity.CessioneImp;

@javax.persistence.Entity
@Table(name = "imp_risc")
@Audited
public class ImpRisc extends Impianto {

	private static final long serialVersionUID = 2383276L;

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
	@ForeignKey(name="FK_ImpRisc_utenteLettera")
	@Index(name="IX_ImpRisc_utenteLettera")
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

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impRisc", cascade=CascadeType.PERSIST)
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
			cessioneImp.setImpRisc(this);
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
			cessioneImp.setImpRisc(null);
		}
	}






	/**
	*  javadoc for superficieRisc
	*/
	private Double superficieRisc;

	@Column(name="superficie_risc")
	public Double getSuperficieRisc(){
		return superficieRisc;
	}

	public void setSuperficieRisc(Double superficieRisc){
		this.superficieRisc = superficieRisc;
	}

	/**
	*  javadoc for tipoProva
	*
	private List<CodeValuePhi> tipoProva;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="ImpRisc_tipoProva", joinColumns = { @JoinColumn(name="ImpRisc_id") }, inverseJoinColumns = { @JoinColumn(name="tipoProva") })
	@ForeignKey(name="FK_tipoProva_ImpRisc", inverseName="FK_ImpRisc_tipoProva")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoProva(){
		return tipoProva;
	}

	public void setTipoProva(List<CodeValuePhi> tipoProva){
		this.tipoProva = tipoProva;
	}*/


	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impRisc")
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
			verificaImp.setImpRisc(this);
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
			verificaImp.setImpRisc(null);
		}
	}



	/**
	*  javadoc for schedaVasi
	*/
	private List<SchedaVasi> schedaVasi;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impRisc", cascade=CascadeType.PERSIST)
	public List<SchedaVasi> getSchedaVasi() {
		return schedaVasi;
	}

	public void setSchedaVasi(List<SchedaVasi>list){
		schedaVasi = list;
	}

	public void addSchedaVasi(SchedaVasi schedaVasi) {
		if (this.schedaVasi == null) {
			this.schedaVasi = new ArrayList<SchedaVasi>();
		}
		// add the association
		if(!this.schedaVasi.contains(schedaVasi)) {
			this.schedaVasi.add(schedaVasi);
			// make the inverse link
			schedaVasi.setImpRisc(this);
		}
	}

	public void removeSchedaVasi(SchedaVasi schedaVasi) {
		if (this.schedaVasi == null) {
			this.schedaVasi = new ArrayList<SchedaVasi>();
			return;
		}
		//add the association
		if(this.schedaVasi.contains(schedaVasi)){
			this.schedaVasi.remove(schedaVasi);
			//make the inverse link
			schedaVasi.setImpRisc(null);
		}
	}



	/**
	*  javadoc for schedaGeneratori
	*/
	private List<SchedaGeneratori> schedaGeneratori;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impRisc", cascade=CascadeType.PERSIST)
	public List<SchedaGeneratori> getSchedaGeneratori() {
		return schedaGeneratori;
	}

	public void setSchedaGeneratori(List<SchedaGeneratori>list){
		schedaGeneratori = list;
	}

	public void addSchedaGeneratori(SchedaGeneratori schedaGeneratori) {
		if (this.schedaGeneratori == null) {
			this.schedaGeneratori = new ArrayList<SchedaGeneratori>();
		}
		// add the association
		if(!this.schedaGeneratori.contains(schedaGeneratori)) {
			this.schedaGeneratori.add(schedaGeneratori);
			// make the inverse link
			schedaGeneratori.setImpRisc(this);
		}
	}

	public void removeSchedaGeneratori(SchedaGeneratori schedaGeneratori) {
		if (this.schedaGeneratori == null) {
			this.schedaGeneratori = new ArrayList<SchedaGeneratori>();
			return;
		}
		//add the association
		if(this.schedaGeneratori.contains(schedaGeneratori)){
			this.schedaGeneratori.remove(schedaGeneratori);
			//make the inverse link
			schedaGeneratori.setImpRisc(null);
		}
	}


	/**
	*  javadoc for dataAutocert2
	*/
	private Date dataAutocert2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_autocert2")
	public Date getDataAutocert2(){
		return dataAutocert2;
	}

	public void setDataAutocert2(Date dataAutocert2){
		this.dataAutocert2 = dataAutocert2;
	}

	/**
	*  javadoc for descrizLocali
	*/
	private CodeValuePhi descrizLocali;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="descrizLocali")
	@ForeignKey(name="FK_ImpRisc_descrizLocali")
	//@Index(name="IX_ImpRisc_descrizLocali")
	public CodeValuePhi getDescrizLocali(){
		return descrizLocali;
	}

	public void setDescrizLocali(CodeValuePhi descrizLocali){
		this.descrizLocali = descrizLocali;
	}

	/**
	*  javadoc for descrizImpianto
	*/
	private CodeValuePhi descrizImpianto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="descrizImpianto")
	@ForeignKey(name="FK_ImpRisc_descrizImpianto")
	//@Index(name="IX_ImpRisc_descrizImpianto")
	public CodeValuePhi getDescrizImpianto(){
		return descrizImpianto;
	}

	public void setDescrizImpianto(CodeValuePhi descrizImpianto){
		this.descrizImpianto = descrizImpianto;
	}

	/**
	*  javadoc for impianto
	*/
	private CodeValuePhi impianto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="impianto")
	@ForeignKey(name="FK_ImpRisc_impianto")
	//@Index(name="IX_ImpRisc_impianto")
	public CodeValuePhi getImpianto(){
		return impianto;
	}

	public void setImpianto(CodeValuePhi impianto){
		this.impianto = impianto;
	}

	/**
	*  javadoc for giorniAA
	*/
	private String giorniAA;

	@Column(name="giorni_aa")
	public String getGiorniAA(){
		return giorniAA;
	}

	public void setGiorniAA(String giorniAA){
		this.giorniAA = giorniAA;
	}

	/**
	*  javadoc for oreGG
	*/
	private String oreGG;

	@Column(name="ore_gg")
	public String getOreGG(){
		return oreGG;
	}

	public void setOreGG(String oreGG){
		this.oreGG = oreGG;
	}

	/**
	*  javadoc for numGen
	*/
	private String numGen;

	@Column(name="num_gen")
	public String getNumGen(){
		return numGen;
	}

	public void setNumGen(String numGen){
		this.numGen = numGen;
	}

	/**
	*  javadoc for vasiChiusi
	*/
	private String vasiChiusi;

	@Column(name="vasi_chiusi")
	public String getVasiChiusi(){
		return vasiChiusi;
	}

	public void setVasiChiusi(String vasiChiusi){
		this.vasiChiusi = vasiChiusi;
	}

	/**
	*  javadoc for vasiAperti
	*/
	private String vasiAperti;

	@Column(name="vasi_aperti")
	public String getVasiAperti(){
		return vasiAperti;
	}

	public void setVasiAperti(String vasiAperti){
		this.vasiAperti = vasiAperti;
	}

	/**
	*  javadoc for dataAutocert1
	*/
	private Date dataAutocert1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_autocert1")
	public Date getDataAutocert1(){
		return dataAutocert1;
	}

	public void setDataAutocert1(Date dataAutocert1){
		this.dataAutocert1 = dataAutocert1;
	}

	/**
	*  javadoc for potGlobNom
	*/
	private Double potGlobNom;

	@Column(name="pot_glob_nom")
	public Double getPotGlobNom(){
		return potGlobNom;
	}

	public void setPotGlobNom(Double potGlobNom){
		this.potGlobNom = potGlobNom;
	}

	/**
	*  javadoc for potGlob
	*/
	private Double potGlob;

	@Column(name="pot_glob")
	public Double getPotGlob(){
		return potGlob;
	}

	public void setPotGlob(Double potGlob){
		this.potGlob = potGlob;
	}

	/**
	*  javadoc for manutentore
	*/
	private CodeValuePhi manutentore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="manutentore")
	@ForeignKey(name="FK_ImpRisc_manutentore")
	//@Index(name="IX_ImpRisc_manutentore")
	public CodeValuePhi getManutentore(){
		return manutentore;
	}

	public void setManutentore(CodeValuePhi manutentore){
		this.manutentore = manutentore;
	}

	/**
	*  javadoc for destImp
	*/
	private CodeValuePhi destImp;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="destImp")
	@ForeignKey(name="FK_ImpRisc_destImp")
	//@Index(name="IX_ImpRisc_destImp")
	public CodeValuePhi getDestImp(){
		return destImp;
	}

	public void setDestImp(CodeValuePhi destImp){
		this.destImp = destImp;
	}
	
	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ImpRisc_id")
	@ForeignKey(name="FK_documenti_ImpRisc")
	//@Index(name="IX_documenti_ImpRisc")
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
