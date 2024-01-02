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
import com.phi.entities.dataTypes.CodeValuePhi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.OneToMany;

import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.baseEntity.CessioneImp;

import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "imp_terra")
@Audited
public class ImpTerra extends Impianto {

	private static final long serialVersionUID = 2431729L;

	/**
	*  javadoc for tipologiaTesto
	*/
	private String tipologiaTesto;

	@Column(name="tipologia_testo")
	public String getTipologiaTesto(){
		return tipologiaTesto;
	}

	public void setTipologiaTesto(String tipologiaTesto){
		this.tipologiaTesto = tipologiaTesto;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_ImpTerra_tipologia")
	@Index(name="IX_ImpTerra_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
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
	@ForeignKey(name="FK_ImpTerra_utenteLettera")
	@Index(name="IX_ImpTerra_utenteLettera")
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

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impTerra", cascade=CascadeType.PERSIST)
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
			cessioneImp.setImpTerra(this);
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
			cessioneImp.setImpTerra(null);
		}
	}





	/**
	*  javadoc for impAutoprod
	*/
	private CodeValuePhi impAutoprod;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="impAutoprod")
	@ForeignKey(name="FK_ImpTerra_impAutoprod")
	//@Index(name="IX_ImpTerra_impAutoprod")
	public CodeValuePhi getImpAutoprod(){
		return impAutoprod;
	}

	public void setImpAutoprod(CodeValuePhi impAutoprod){
		this.impAutoprod = impAutoprod;
	}

	/**
	*  javadoc for cabineNum
	*/
	private Integer cabineNum;

	@Column(name="cabine_num")
	public Integer getCabineNum(){
		return cabineNum;
	}

	public void setCabineNum(Integer cabineNum){
		this.cabineNum = cabineNum;
	}

	/**
	*  javadoc for cabineCode
	*/
	private CodeValuePhi cabineCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cabineCode")
	@ForeignKey(name="FK_ImpTerra_cabineCode")
	//@Index(name="IX_ImpTerra_cabineCode")
	public CodeValuePhi getCabineCode(){
		return cabineCode;
	}

	public void setCabineCode(CodeValuePhi cabineCode){
		this.cabineCode = cabineCode;
	}

	/**
	*  javadoc for struttAutopCode
	*/
//	private CodeValuePhi struttAutopCode;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="struttAutopCode")
//	@ForeignKey(name="FK_ImpTerra_struttAutopCode")
//	//@Index(name="IX_ImpTerra_struttAutopCode")
//	public CodeValuePhi getStruttAutopCode(){
//		return struttAutopCode;
//	}
//
//	public void setStruttAutopCode(CodeValuePhi struttAutopCode){
//		this.struttAutopCode = struttAutopCode;
//	}

	/**
	*  javadoc for struttAutopNum
	*/
	private Integer struttAutopNum;

	@Column(name="strutt_autop_num")
	public Integer getStruttAutopNum(){
		return struttAutopNum;
	}

	public void setStruttAutopNum(Integer struttAutopNum){
		this.struttAutopNum = struttAutopNum;
	}


	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impTerra"/*, cascade=CascadeType.PERSIST*/)
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
			verificaImp.setImpTerra(this);
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
			verificaImp.setImpTerra(null);
		}
	}


	/**
	*  javadoc for type01
	*/
//	private CodeValuePhi type01;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="type01")
//	@ForeignKey(name="FK_ImpTerra_type01")
//	//@Index(name="IX_ImpTerra_type01")
//	public CodeValuePhi getType01(){
//		return type01;
//	}
//
//	public void setType01(CodeValuePhi type01){
//		this.type01 = type01;
//	}

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
	*  javadoc for competenza
	*/
	private CodeValuePhi competenza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="competenza")
	@ForeignKey(name="FK_ImpTerra_competenza")
	//@Index(name="IX_ImpTerra_competenza")
	public CodeValuePhi getCompetenza(){
		return competenza;
	}

	public void setCompetenza(CodeValuePhi competenza){
		this.competenza = competenza;
	}

	/**
	*  javadoc for impColl
	*/
	private String impColl;

	@Column(name="imp_coll")
	public String getImpColl(){
		return impColl;
	}

	public void setImpColl(String impColl){
		this.impColl = impColl;
	}

	/**
	*  javadoc for ci3
	*/
	private CodeValuePhi ci3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ci3")
	@ForeignKey(name="FK_ImpTerra_ci3")
	//@Index(name="IX_ImpTerra_ci3")
	public CodeValuePhi getCi3(){
		return ci3;
	}

	public void setCi3(CodeValuePhi ci3){
		this.ci3 = ci3;
	}

	/**
	*  javadoc for ci2
	*/
	private CodeValuePhi ci2;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ci2")
	@ForeignKey(name="FK_ImpTerra_ci2")
	//@Index(name="IX_ImpTerra_ci2")
	public CodeValuePhi getCi2(){
		return ci2;
	}

	public void setCi2(CodeValuePhi ci2){
		this.ci2 = ci2;
	}

	/**
	*  javadoc for ci1
	*/
	private CodeValuePhi ci1;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ci1")
	@ForeignKey(name="FK_ImpTerra_ci1")
	//@Index(name="IX_ImpTerra_ci1")
	public CodeValuePhi getCi1(){
		return ci1;
	}

	public void setCi1(CodeValuePhi ci1){
		this.ci1 = ci1;
	}

	/**
	*  javadoc for ci0
	*/
	private CodeValuePhi ci0;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ci0")
	@ForeignKey(name="FK_ImpTerra_ci0")
	//@Index(name="IX_ImpTerra_ci0")
	public CodeValuePhi getCi0(){
		return ci0;
	}

	public void setCi0(CodeValuePhi ci0){
		this.ci0 = ci0;
	}

	/**
	*  javadoc for areaPeric
	*/
	private String areaPeric;

	@Column(name="area_peric")
	public String getAreaPeric(){
		return areaPeric;
	}

	public void setAreaPeric(String areaPeric){
		this.areaPeric = areaPeric;
	}

	/**
	*  javadoc for cabine
	*/
	private String cabine;

	@Column(name="cabine")
	public String getCabine(){
		return cabine;
	}

	public void setCabine(String cabine){
		this.cabine = cabine;
	}

	/**
	*  javadoc for subTypeC
	*/
	private CodeValuePhi subTypeC;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeC")
	@ForeignKey(name="FK_ImpTerra_subTypeC")
	//@Index(name="IX_ImpTerra_subTypeC")
	public CodeValuePhi getSubTypeC(){
		return subTypeC;
	}

	public void setSubTypeC(CodeValuePhi subTypeC){
		this.subTypeC = subTypeC;
	}

	/**
	*  javadoc for subTypeB
	*/
	private CodeValuePhi subTypeB;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeB")
	@ForeignKey(name="FK_ImpTerra_subTypeB")
	//@Index(name="IX_ImpTerra_subTypeB")
	public CodeValuePhi getSubTypeB(){
		return subTypeB;
	}

	public void setSubTypeB(CodeValuePhi subTypeB){
		this.subTypeB = subTypeB;
	}

	/**
	*  javadoc for struttAereop
	*/
//	private String struttAereop;
//
//	@Column(name="strutt_aereop")
//	public String getStruttAereop(){
//		return struttAereop;
//	}
//
//	public void setStruttAereop(String struttAereop){
//		this.struttAereop = struttAereop;
//	}

	/**
	*  javadoc for cantieri
	*/
//	private String cantieri;
//
//	@Column(name="cantieri")
//	public String getCantieri(){
//		return cantieri;
//	}
//
//	public void setCantieri(String cantieri){
//		this.cantieri = cantieri;
//	}

	/**
	*  javadoc for disperdenti
	*/
	private String disperdenti;

	@Column(name="disperdenti")
	public String getDisperdenti(){
		return disperdenti;
	}

	public void setDisperdenti(String disperdenti){
		this.disperdenti = disperdenti;
	}
	/**
	*  javadoc for dispersori
	*/
	private String dispersori;

	@Column(name="dispersori")
	public String getDispersori(){
		return dispersori;
	}

	public void setDispersori(String dispersori){
		this.dispersori = dispersori;
	}
	/**
	*  javadoc for serbatoi
	*/
//	private String serbatoi;
//
//	@Column(name="serbatoi")
//	public String getSerbatoi(){
//		return serbatoi;
//	}
//
//	public void setSerbatoi(String serbatoi){
//		this.serbatoi = serbatoi;
//	}

	/**
	*  javadoc for metalliche
	*/
//	private String metalliche;
//
//	@Column(name="metalliche")
//	public String getMetalliche(){
//		return metalliche;
//	}
//
//	public void setMetalliche(String metalliche){
//		this.metalliche = metalliche;
//	}

	/**
	*  javadoc for raggruppati02
	*/
	private String raggruppati02;

	@Column(name="raggruppati02")
	public String getRaggruppati02(){
		return raggruppati02;
	}

	public void setRaggruppati02(String raggruppati02){
		this.raggruppati02 = raggruppati02;
	}

	/**
	*  javadoc for isolanti02
	*/
//	private String isolanti02;
//
//	@Column(name="isolanti02")
//	public String getIsolanti02(){
//		return isolanti02;
//	}
//
//	public void setIsolanti02(String isolanti02){
//		this.isolanti02 = isolanti02;
//	}

	/**
	*  javadoc for raggruppati
	*/
	private String raggruppati01;

	@Column(name="raggruppati01")
	public String getRaggruppati01(){
		return raggruppati01;
	}

	public void setRaggruppati01(String raggruppati01){
		this.raggruppati01 = raggruppati01;
	}

	/**
	*  javadoc for isolanti
	*/
//	private String isolanti01;
//
//	@Column(name="isolanti01")
//	public String getIsolanti01(){
//		return isolanti01;
//	}
//
//	public void setIsolanti01(String isolanti01){
//		this.isolanti01 = isolanti01;
//	}

	/**
	*  javadoc for superf03
	*/
	private String superf03;

	@Column(name="superf03")
	public String getSuperf03(){
		return superf03;
	}

	public void setSuperf03(String superf03){
		this.superf03 = superf03;
	}

	/**
	*  javadoc for superf02
	*/
	private String superf02;

	@Column(name="superf02")
	public String getSuperf02(){
		return superf02;
	}

	public void setSuperf02(String superf02){
		this.superf02 = superf02;
	}

	/**
	*  javadoc for superf01
	*/
	private String superf01;

	@Column(name="superf01")
	public String getSuperf01(){
		return superf01;
	}

	public void setSuperf01(String superf01){
		this.superf01 = superf01;
	}

	/**
	*  javadoc for aste
	*/
	private String aste;

	@Column(name="aste")
	public String getAste(){
		return aste;
	}

	public void setAste(String aste){
		this.aste = aste;
	}

	/**
	*  javadoc for parafulmini
	*/
//	private String parafulmini;
//
//	@Column(name="parafulmini")
//	public String getParafulmini(){
//		return parafulmini;
//	}
//
//	public void setParafulmini(String parafulmini){
//		this.parafulmini = parafulmini;
//	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_ImpTerra_type")
	//@Index(name="IX_ImpTerra_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	*  javadoc for pot
	*/
	private Double pot;

	@Column(name="pot")
	public Double getPot(){
		return pot;
	}

	public void setPot(Double pot){
		this.pot = pot;
	}

	/**
	*  javadoc for par
	*/
	private String par;

	@Column(name="par")
	public String getPar(){
		return par;
	}

	public void setPar(String par){
		this.par = par;
	}

	/**
	*  javadoc for subType
	*/
	private CodeValuePhi subType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subType")
	@ForeignKey(name="FK_ImpTerra_subType")
	//@Index(name="IX_ImpTerra_subType")
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
	@JoinColumn(name="ImpTerra_id")
	@ForeignKey(name="FK_documenti_ImpTerra")
	//@Index(name="IX_documenti_ImpTerra")
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
