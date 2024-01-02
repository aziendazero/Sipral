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
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;

import java.util.ArrayList;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.CessioneImp;

import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;
import com.phi.entities.baseEntity.SchedaRecipientiIndiv;
import com.phi.entities.baseEntity.SchedaTubazioniIndiv;

@javax.persistence.Entity
@Table(name = "imp_press")
@Audited
public class ImpPress extends Impianto {

	private static final long serialVersionUID = 2367847L;

	/**
	*  javadoc for pressBar1
	*/
	private Double pressBar1;

	@Column(name="press_bar1")
	public Double getPressBar1(){
		return pressBar1;
	}
	
	public void setPressBar1(Double pressBar1){
		this.pressBar1 = pressBar1;
	}
	
	/**
	*  javadoc for pressBar2
	*/
	private Double pressBar2;

	@Column(name="press_bar2")
	public Double getPressBar2(){
		return pressBar2;
	}

	public void setPressBar2(Double pressBar2){
		this.pressBar2 = pressBar2;
	}
	
	/**
	*  javadoc for capacita
	*/
	private Double capacita;

	@Column(name="capacita")
	public Double getCapacita(){
		return capacita;
	}

	public void setCapacita(Double capacita){
		this.capacita = capacita;
	}
	
	/**
	*  javadoc for superficie
	*/
	private Double superficie;

	@Column(name="superficie")
	public Double getSuperficie(){
		return superficie;
	}

	public void setSuperficie(Double superficie){
		this.superficie = superficie;
	}
	
	/**
	*  javadoc for producibilita
	*/
	private Double producibilita;

	@Column(name="producibilita")
	public Double getProducibilita(){
		return producibilita;
	}

	public void setProducibilita(Double producibilita){
		this.producibilita = producibilita;
	}
	
	/**
	*  javadoc for caratteristicheSpec
	*/
	private CodeValuePhi caratteristicheSpec;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="caratteristicheSpec")
	@ForeignKey(name="FK_ImpPress_caratteristicheSpec")
	@Index(name="IX_ImpPress_caratteristicheSpec")
	public CodeValuePhi getCaratteristicheSpec(){
		return caratteristicheSpec;
	}

	public void setCaratteristicheSpec(CodeValuePhi caratteristicheSpec){
		this.caratteristicheSpec = caratteristicheSpec;
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
	@ForeignKey(name="FK_ImpPress_utenteLettera")
	@Index(name="IX_ImpPress_utenteLettera")
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
	*  javadoc for schedaTubazioniIndiv
	*/
	private List<SchedaTubazioniIndiv> schedaTubazioniIndiv;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impPress", cascade=CascadeType.PERSIST)
	public List<SchedaTubazioniIndiv> getSchedaTubazioniIndiv() {
		return schedaTubazioniIndiv;
	}

	public void setSchedaTubazioniIndiv(List<SchedaTubazioniIndiv>list){
		schedaTubazioniIndiv = list;
	}

	public void addSchedaTubazioniIndiv(SchedaTubazioniIndiv schedaTubazioniIndiv) {
		if (this.schedaTubazioniIndiv == null) {
			this.schedaTubazioniIndiv = new ArrayList<SchedaTubazioniIndiv>();
		}
		// add the association
		if(!this.schedaTubazioniIndiv.contains(schedaTubazioniIndiv)) {
			this.schedaTubazioniIndiv.add(schedaTubazioniIndiv);
			// make the inverse link
			schedaTubazioniIndiv.setImpPress(this);
		}
	}

	public void removeSchedaTubazioniIndiv(SchedaTubazioniIndiv schedaTubazioniIndiv) {
		if (this.schedaTubazioniIndiv == null) {
			this.schedaTubazioniIndiv = new ArrayList<SchedaTubazioniIndiv>();
			return;
		}
		//add the association
		if(this.schedaTubazioniIndiv.contains(schedaTubazioniIndiv)){
			this.schedaTubazioniIndiv.remove(schedaTubazioniIndiv);
			//make the inverse link
			schedaTubazioniIndiv.setImpPress(null);
		}
	}



	/**
	*  javadoc for schedaRecipientiIndiv
	*/
	private List<SchedaRecipientiIndiv> schedaRecipientiIndiv;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impPress", cascade=CascadeType.PERSIST)
	public List<SchedaRecipientiIndiv> getSchedaRecipientiIndiv() {
		return schedaRecipientiIndiv;
	}

	public void setSchedaRecipientiIndiv(List<SchedaRecipientiIndiv>list){
		schedaRecipientiIndiv = list;
	}

	public void addSchedaRecipientiIndiv(SchedaRecipientiIndiv schedaRecipientiIndiv) {
		if (this.schedaRecipientiIndiv == null) {
			this.schedaRecipientiIndiv = new ArrayList<SchedaRecipientiIndiv>();
		}
		// add the association
		if(!this.schedaRecipientiIndiv.contains(schedaRecipientiIndiv)) {
			this.schedaRecipientiIndiv.add(schedaRecipientiIndiv);
			// make the inverse link
			schedaRecipientiIndiv.setImpPress(this);
		}
	}

	public void removeSchedaRecipientiIndiv(SchedaRecipientiIndiv schedaRecipientiIndiv) {
		if (this.schedaRecipientiIndiv == null) {
			this.schedaRecipientiIndiv = new ArrayList<SchedaRecipientiIndiv>();
			return;
		}
		//add the association
		if(this.schedaRecipientiIndiv.contains(schedaRecipientiIndiv)){
			this.schedaRecipientiIndiv.remove(schedaRecipientiIndiv);
			//make the inverse link
			schedaRecipientiIndiv.setImpPress(null);
		}
	}



	/**
	*  javadoc for schedaGeneratoriIndiv
	*/
	private List<SchedaGeneratoriIndiv> schedaGeneratoriIndiv;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impPress", cascade=CascadeType.PERSIST)
	//@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<SchedaGeneratoriIndiv> getSchedaGeneratoriIndiv() {
		return schedaGeneratoriIndiv;
	}

	public void setSchedaGeneratoriIndiv(List<SchedaGeneratoriIndiv>list){
		schedaGeneratoriIndiv = list;
	}

	public void addSchedaGeneratoriIndiv(SchedaGeneratoriIndiv schedaGeneratoriIndiv) {
		if (this.schedaGeneratoriIndiv == null) {
			this.schedaGeneratoriIndiv = new ArrayList<SchedaGeneratoriIndiv>();
		}
		// add the association
		if(!this.schedaGeneratoriIndiv.contains(schedaGeneratoriIndiv)) {
			this.schedaGeneratoriIndiv.add(schedaGeneratoriIndiv);
			// make the inverse link
			schedaGeneratoriIndiv.setImpPress(this);
		}
	}

	public void removeSchedaGeneratoriIndiv(SchedaGeneratoriIndiv schedaGeneratoriIndiv) {
		if (this.schedaGeneratoriIndiv == null) {
			this.schedaGeneratoriIndiv = new ArrayList<SchedaGeneratoriIndiv>();
			return;
		}
		//add the association
		if(this.schedaGeneratoriIndiv.contains(schedaGeneratoriIndiv)){
			this.schedaGeneratoriIndiv.remove(schedaGeneratoriIndiv);
			//make the inverse link
			schedaGeneratoriIndiv.setImpPress(null);
		}
	}



	/**
	*  javadoc for cessioneImp
	*/
	private List<CessioneImp> cessioneImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impPress", cascade=CascadeType.PERSIST)
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
			cessioneImp.setImpPress(this);
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
			cessioneImp.setImpPress(null);
		}
	}



	/**
	*  javadoc for tubazioni
	*/
	private String tubazioni;

	@Column(name="tubazioni")
	public String getTubazioni(){
		return tubazioni;
	}

	public void setTubazioni(String tubazioni){
		this.tubazioni = tubazioni;
	}

	/**
	*  javadoc for parteInsiemiSoggetti
	*/
	private CodeValuePhi parteInsiemiSoggetti;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parteInsiemiSoggetti")
	@ForeignKey(name="FK_ImpPress_parteInsiemiSoggetti")
	//@Index(name="IX_ImpPress_parteInsiemiSoggetti")
	public CodeValuePhi getParteInsiemiSoggetti(){
		return parteInsiemiSoggetti;
	}

	public void setParteInsiemiSoggetti(CodeValuePhi parteInsiemiSoggetti){
		this.parteInsiemiSoggetti = parteInsiemiSoggetti;
	}

	/**
	*  javadoc for dataEsonero
	*/
	private Date dataEsonero;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_esonero")
	public Date getDataEsonero(){
		return dataEsonero;
	}

	public void setDataEsonero(Date dataEsonero){
		this.dataEsonero = dataEsonero;
	}

	/**
	*  javadoc for esonero
	*/
	private CodeValuePhi esonero;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esonero")
	@ForeignKey(name="FK_ImpPress_esonero")
	//@Index(name="IX_ImpPress_esonero")
	public CodeValuePhi getEsonero(){
		return esonero;
	}

	public void setEsonero(CodeValuePhi esonero){
		this.esonero = esonero;
	}

	/**
	*  javadoc for tipoProva
	*
	private List<CodeValuePhi> tipoProva;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="ImpPress_tipoProva", joinColumns = { @JoinColumn(name="ImpPress_id") }, inverseJoinColumns = { @JoinColumn(name="tipoProva") })
	@ForeignKey(name="FK_tipoProva_ImpPress", inverseName="FK_ImpPress_tipoProva")
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

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impPress")
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
			verificaImp.setImpPress(this);
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
			verificaImp.setImpPress(null);
		}
	}


	/**
	*  javadoc for competenza
	*/
	private CodeValuePhi competenza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="competenza")
	@ForeignKey(name="FK_ImpPress_competenza")
	//@Index(name="IX_ImpPress_competenza")
	public CodeValuePhi getCompetenza(){
		return competenza;
	}

	public void setCompetenza(CodeValuePhi competenza){
		this.competenza = competenza;
	}

	/**
	*  javadoc for categoriaRischio
	*/
	private CodeValuePhi categoriaRischio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="categoriaRischio")
	@ForeignKey(name="FK_ImpPress_categoriaRischio")
	//@Index(name="IX_ImpPress_categoriaRischio")
	public CodeValuePhi getCategoriaRischio(){
		return categoriaRischio;
	}

	public void setCategoriaRischio(CodeValuePhi categoriaRischio){
		this.categoriaRischio = categoriaRischio;
	}

	/**
	*  javadoc for fluido
	*/
	private CodeValuePhi fluido;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="fluido")
	@ForeignKey(name="FK_ImpPress_fluido")
	//@Index(name="IX_ImpPress_fluido")
	public CodeValuePhi getFluido(){
		return fluido;
	}

	public void setFluido(CodeValuePhi fluido){
		this.fluido = fluido;
	}

	/**
	*  javadoc for tempV2
	*/
	private String tempV2;

	@Column(name="temp_v2")
	public String getTempV2(){
		return tempV2;
	}

	public void setTempV2(String tempV2){
		this.tempV2 = tempV2;
	}

	/**
	*  javadoc for tempV1
	*/
	private String tempV1;

	@Column(name="temp_v1")
	public String getTempV1(){
		return tempV1;
	}

	public void setTempV1(String tempV1){
		this.tempV1 = tempV1;
	}

	/**
	*  javadoc for tempS2
	*/
	private CodeValuePhi tempS2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tempS2")
	@ForeignKey(name="FK_ImpPress_tempS2")
	//@Index(name="IX_ImpPress_tempS2")
	public CodeValuePhi getTempS2(){
		return tempS2;
	}

	public void setTempS2(CodeValuePhi tempS2){
		this.tempS2 = tempS2;
	}

	/**
	*  javadoc for tempS1
	*/
	private CodeValuePhi tempS1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tempS1")
	@ForeignKey(name="FK_ImpPress_tempS1")
	//@Index(name="IX_ImpPress_tempS1")
	public CodeValuePhi getTempS1(){
		return tempS1;
	}

	public void setTempS1(CodeValuePhi tempS1){
		this.tempS1 = tempS1;
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
	*  javadoc for comodante
	*/
	private CodeValuePhi comodante;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comodante")
	@ForeignKey(name="FK_ImpPress_comodante")
	//@Index(name="IX_ImpPress_comodante")
	public CodeValuePhi getComodante(){
		return comodante;
	}

	public void setComodante(CodeValuePhi comodante){
		this.comodante = comodante;
	}

	/**
	*  javadoc for caratteristiche
	*
	private List<CodeValuePhi> caratteristiche;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="ImpPress_caratteristiche", joinColumns = { @JoinColumn(name="ImpPress_id") }, inverseJoinColumns = { @JoinColumn(name="caratteristiche") })
	@ForeignKey(name="FK_caratteristiche_ImpPress", inverseName="FK_ImpPress_caratteristiche")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getCaratteristiche(){
		return caratteristiche;
	}

	public void setCaratteristiche(List<CodeValuePhi> caratteristiche){
		this.caratteristiche = caratteristiche;
	}*/

	/**
	*  javadoc for tipoApparecchio
	*/
	private CodeValuePhi tipoApparecchio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoApparecchio")
	@ForeignKey(name="FK_ImpPress_tipoApparecchio")
	//@Index(name="IX_ImpPress_tipoApparecchio")
	public CodeValuePhi getTipoApparecchio(){
		return tipoApparecchio;
	}

	public void setTipoApparecchio(CodeValuePhi tipoApparecchio){
		this.tipoApparecchio = tipoApparecchio;
	}

	/**
	*  javadoc for bombole
	*/
	private String bombole;

	@Column(name="bombole")
	public String getBombole(){
		return bombole;
	}

	public void setBombole(String bombole){
		this.bombole = bombole;
	}

//	/**
//	*  javadoc for numeroFabrica
//	*/
//	private String numeroFabrica;
//
//	@Column(name="numero_fabrica")
//	public String getNumeroFabrica(){
//		return numeroFabrica;
//	}
//
//	public void setNumeroFabrica(String numeroFabrica){
//		this.numeroFabrica = numeroFabrica;
//	}

	/**
	*  javadoc for dataCostruzione
	*/
	private Date dataCostruzione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_costruzione")
	public Date getDataCostruzione(){
		return dataCostruzione;
	}

	public void setDataCostruzione(Date dataCostruzione){
		this.dataCostruzione = dataCostruzione;
	}
	
	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ImpPress_id")
	@ForeignKey(name="FK_documenti_ImpPress")
	//@Index(name="IX_documenti_ImpPress")
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
