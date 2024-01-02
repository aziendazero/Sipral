package com.phi.entities.baseEntity;

import javax.persistence.Table;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;

import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.CessioneImp;

@javax.persistence.Entity
@Table(name = "imp_soll")
@Audited
public class ImpSoll extends Impianto {

	private static final long serialVersionUID = 2412087L;

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
	@ForeignKey(name="FK_ImpSoll_utenteLettera")
	@Index(name="IX_ImpSoll_utenteLettera")
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

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impSoll", cascade=CascadeType.PERSIST)
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
			cessioneImp.setImpSoll(this);
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
			cessioneImp.setImpSoll(null);
		}
	}

	/**
	*  javadoc for marcaturaCe
	*/
	private Boolean marcaturaCe;

	@Column(name="marcatura_ce")
	public Boolean getMarcaturaCe(){
		return marcaturaCe;
	}

	public void setMarcaturaCe(Boolean marcaturaCe){
		this.marcaturaCe = marcaturaCe;
	}


	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impSoll")
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
			verificaImp.setImpSoll(this);
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
			verificaImp.setImpSoll(null);
		}
	}


	/**
	*  javadoc for mezzo
	*/
	private String mezzo;

	@Column(name="mezzo")
	public String getMezzo(){
		return mezzo;
	}

	public void setMezzo(String mezzo){
		this.mezzo = mezzo;
	}

	/**
	*  javadoc for marcCE
	*/
	private String marcCE;

	@Column(name="marc_ce")
	public String getMarcCE(){
		return marcCE;
	}

	public void setMarcCE(String marcCE){
		this.marcCE = marcCE;
	}

	/**
	*  javadoc for numRadioc
	*/
	private String numRadioc;

	@Column(name="num_radioc")
	public String getNumRadioc(){
		return numRadioc;
	}

	public void setNumRadioc(String numRadioc){
		this.numRadioc = numRadioc;
	}

	/**
	*  javadoc for dataInstRadioc
	*/
	private Date dataInstRadioc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_inst_radioc")
	public Date getDataInstRadioc(){
		return dataInstRadioc;
	}

	public void setDataInstRadioc(Date dataInstRadioc){
		this.dataInstRadioc = dataInstRadioc;
	}

	/**
	*  javadoc for costrRadioc
	*/
	private String costrRadioc;

	@Column(name="costr_radioc")
	public String getCostrRadioc(){
		return costrRadioc;
	}

	public void setCostrRadioc(String costrRadioc){
		this.costrRadioc = costrRadioc;
	}

	/**
	*  javadoc for competenza
	*/
	private CodeValuePhi competenza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="competenza")
	@ForeignKey(name="FK_ImpSoll_competenza")
	//@Index(name="IX_ImpSoll_competenza")
	public CodeValuePhi getCompetenza(){
		return competenza;
	}

	public void setCompetenza(CodeValuePhi competenza){
		this.competenza = competenza;
	}

//	/**
//	*  javadoc for dataCollaudo
//	*/
//	private Date dataCollaudo;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="data_collaudo")
//	public Date getDataCollaudo(){
//		return dataCollaudo;
//	}
//
//	public void setDataCollaudo(Date dataCollaudo){
//		this.dataCollaudo = dataCollaudo;
//	}

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
	*  javadoc for tipoFabb
	*/
	private String tipoFabb;

	@Column(name="tipo_fabb")
	public String getTipoFabb(){
		return tipoFabb;
	}

	public void setTipoFabb(String tipoFabb){
		this.tipoFabb = tipoFabb;
	}
	
	/**
	*  javadoc for tipoFabbrica
	
	private CodeValuePhi tipoFabbrica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoFabbrica")
	@ForeignKey(name="FK_ImpSoll_tipoFabbrica")
	//@Index(name="IX_ImpSoll_tipoFabbrica")
	public CodeValuePhi getTipoFabbrica(){
		return tipoFabbrica;
	}

	public void setTipoFabbrica(CodeValuePhi tipoFabbrica){
		this.tipoFabbrica = tipoFabbrica;
	}*/

	/**
	*  javadoc for type
	*/
//	private CodeValuePhi type;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="type")
//	@ForeignKey(name="FK_ImpSoll_type")
//	//@Index(name="IX_ImpSoll_type")
//	public CodeValuePhi getType(){
//		return type;
//	}
//
//	public void setType(CodeValuePhi type){
//		this.type = type;
//	}

	/**
	*  javadoc for velocitaMax
	*/
	private String velocitaMax;

	@Column(name="velocita_max")
	public String getVelocitaMax(){
		return velocitaMax;
	}

	public void setVelocitaMax(String velocitaMax){
		this.velocitaMax = velocitaMax;
	}

	/**
	*  javadoc for subType
	*/
	private CodeValuePhi subType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subType")
	@ForeignKey(name="FK_ImpSoll_subType")
	//@Index(name="IX_ImpSoll_subType")
	public CodeValuePhi getSubType(){
		return subType;
	}

	public void setSubType(CodeValuePhi subType){
		this.subType = subType;
	}

	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="ImpSoll_id")
	@ForeignKey(name="FK_documenti_ImpSoll")
	//@Index(name="IX_documenti_ImpSoll")
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
