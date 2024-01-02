package com.phi.entities.baseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.ImpMonta;
import com.phi.entities.baseEntity.ImpPress;
import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.baseEntity.ImpSoll;
import com.phi.entities.baseEntity.ImpTerra;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.baseEntity.ImpSearchCollector;
import com.phi.entities.role.Employee;
import com.phi.entities.baseEntity.Fattura;
import javax.persistence.OneToOne;
import com.phi.entities.baseEntity.Sedi;

import com.phi.entities.dataTypes.CodeValue;
import org.hibernate.annotations.Index;

import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.SediInstallazione;

@javax.persistence.Entity
@Table(name = "verifica_imp")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VerificaImp extends BaseEntity {

	private static final long serialVersionUID = 48307573L;

	/**
	*  javadoc for pre
	*/
	private Boolean pre;

	@Column(name="pre")
	public Boolean getPre(){
		return pre;
	}

	public void setPre(Boolean pre){
		this.pre = pre;
	}


	/**
	*  javadoc for sediInstallazione
	*/
	private SediInstallazione sediInstallazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_installazione_id")
	@ForeignKey(name="FK_VerImp_sedeInst")
	//@Index(name="IX_VrificaImp_sdInstallazon")
	public SediInstallazione getSediInstallazione(){
		return sediInstallazione;
	}

	public void setSediInstallazione(SediInstallazione sediInstallazione){
		this.sediInstallazione = sediInstallazione;
	}

	/**
	*  javadoc for isChecked
	*/
	private Boolean isChecked;
	
	@Transient
	public Boolean getIsChecked(){
		return isChecked;
	}

	public void setIsChecked(Boolean isChecked){
		this.isChecked = isChecked;
	}

	/**
	*  javadoc for isSel
	*/
	private Boolean isSel;

	@Column(name="is_sel")
	public Boolean getIsSel(){
		return isSel;
	}

	public void setIsSel(Boolean isSel){
		this.isSel = isSel;
	}


	
	/**
	*  javadoc for causale
	*/
	private CodeValuePhi causale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="causale")
	@ForeignKey(name="FK_VerificaImp_causale")
	@Index(name="IX_VerificaImp_causale")
	public CodeValuePhi getCausale(){
		return causale;
	}

	public void setCausale(CodeValuePhi causale){
		this.causale = causale;
	}

	/**
	*  javadoc for enteVerificatoreExt
	*/
	private CodeValuePhi enteVerificatoreExt;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="enteVerificatoreExt")
	@ForeignKey(name="FK_VerificaImp_enteVerificatoreExt")
	//@Index(name="IX_VerificaImp_enteVerificatoreExt")
	public CodeValuePhi getEnteVerificatoreExt(){
		return enteVerificatoreExt;
	}

	public void setEnteVerificatoreExt(CodeValuePhi enteVerificatoreExt){
		this.enteVerificatoreExt = enteVerificatoreExt;
	}

	/**
	*  javadoc for impManuale
	*/
	private Boolean impManuale;

	@Column(name="imp_manuale")
	public Boolean getImpManuale(){
		return impManuale;
	}

	public void setImpManuale(Boolean impManuale){
		this.impManuale = impManuale;
	}
	
	/**
	*  javadoc for noteImporto
	*/
	private String noteImporto;

	@Column(name="note_importo", length = 2500)
	public String getNoteImporto(){
		return noteImporto;
	}

	public void setNoteImporto(String noteImporto){
		this.noteImporto = noteImporto;
	}


	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_VerificaImp_sedi")
	//@Index(name="IX_VerificaImp_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}

	/**
	*  javadoc for tipoInOut
	*/
	private CodeValuePhi tipoInOut;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoInOut")
	@ForeignKey(name="FK_VerificaImp_tipoInOut")
	//@Index(name="IX_VerificaImp_tipoInOut")
	public CodeValuePhi getTipoInOut(){
		return tipoInOut;
	}

	public void setTipoInOut(CodeValuePhi tipoInOut){
		this.tipoInOut = tipoInOut;
	}

	/**
	*  Valorizzato in fase di creazione della fattura
	*/
	private Integer numeroDoc;

	@Column(name="numero_doc")
	public Integer getNumeroDoc(){
		return numeroDoc;
	}

	public void setNumeroDoc(Integer numeroDoc){
		this.numeroDoc = numeroDoc;
	}

	/**
	*  javadoc for fattura
	*/
	private Fattura fattura;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="fattura_id")
	@ForeignKey(name="FK_VerificaImp_fattura")
	//@Index(name="IX_VerificaImp_fattura")
	public Fattura getFattura(){
		return fattura;
	}

	public void setFattura(Fattura fattura){
		this.fattura = fattura;
	}

	/**
	*  javadoc for codiceConto
	*/
	private String codiceConto;

	@Column(name="codice_conto")
	public String getCodiceConto(){
		return codiceConto;
	}

	public void setCodiceConto(String codiceConto){
		this.codiceConto = codiceConto;
	}

	/**
	*  javadoc for importo
	*/
	private Double importo;

	@Column(name="importo")
	public Double getImporto(){
		if (importo!=null)
			return new BigDecimal(importo).setScale(2 , BigDecimal.ROUND_HALF_UP).doubleValue(); 
		
		return importo;
	}

	public void setImporto(Double importo){
		this.importo = importo;
	}
	
	/**
	*  javadoc for logImporto
	*/
	private String logImporto;

	@Column(name="log_importo", length = 4000)
	public String getLogImporto(){
		return logImporto;
	}

	public void setLogImporto(String logImporto){
		this.logImporto = logImporto;
	}

	/**
	*  javadoc for idrovore
	*/
	private Boolean idrovore;

	@Column(name="idrovore")
	public Boolean getIdrovore(){
		return idrovore;
	}

	public void setIdrovore(Boolean idrovore){
		this.idrovore = idrovore;
	}


	/**
	*  javadoc for utenteUltimaModifica
	*/
	private Employee utenteUltimaModifica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="utente_ultima_modifica_id")
	@ForeignKey(name="FK_VrfcaImp_utntUltmaModfca")
	//@Index(name="IX_VrfcaImp_utntUltmaModfca")
	public Employee getUtenteUltimaModifica(){
		return utenteUltimaModifica;
	}

	public void setUtenteUltimaModifica(Employee utenteUltimaModifica){
		this.utenteUltimaModifica = utenteUltimaModifica;
	}


	/**
	*  javadoc for dataUltimaModifica
	*/
	private Date dataUltimaModifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_modifica")
	public Date getDataUltimaModifica(){
		return dataUltimaModifica;
	}

	public void setDataUltimaModifica(Date dataUltimaModifica){
		this.dataUltimaModifica = dataUltimaModifica;
	}

	/**
	*  javadoc for impType
	*/
	private CodeValuePhi impType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="impType")
	@ForeignKey(name="FK_VerificaImp_impType")
	//@Index(name="IX_VerificaImp_impType")
	public CodeValuePhi getImpType(){
		return impType;
	}

	public void setImpType(CodeValuePhi impType){
		this.impType = impType;
	}


	/**
	*  javadoc for impSearchCollector
	*/
	private ImpSearchCollector impSearchCollector;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_search_collector_id")
	@ForeignKey(name="FK_VrfcaImp_mpSarchCollctor")
	//@Index(name="IX_VrfcaImp_mpSarchCollctor")
	public ImpSearchCollector getImpSearchCollector(){
		return impSearchCollector;
	}

	public void setImpSearchCollector(ImpSearchCollector impSearchCollector){
		this.impSearchCollector = impSearchCollector;
	}


	/**
	*  javadoc for prescrizioneBl
	*/
	private Boolean prescrizioneBl;

	@Column(name="prescrizione_bl")
	public Boolean getPrescrizioneBl(){
		return prescrizioneBl;
	}

	public void setPrescrizioneBl(Boolean prescrizioneBl){
		this.prescrizioneBl = prescrizioneBl;
	}

	/**
	*  javadoc for nextVerifDate3
	*/
	private Date nextVerifDate3;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="next_verif_date3")
	public Date getNextVerifDate3(){
		return nextVerifDate3;
	}

	public void setNextVerifDate3(Date nextVerifDate3){
		this.nextVerifDate3 = nextVerifDate3;
	}

	/**
	*  javadoc for nextVerifDate2
	*/
	private Date nextVerifDate2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="next_verif_date2")
	public Date getNextVerifDate2(){
		return nextVerifDate2;
	}

	public void setNextVerifDate2(Date nextVerifDate2){
		this.nextVerifDate2 = nextVerifDate2;
	}

	/**
	*  javadoc for nextVerifDate1
	*/
	private Date nextVerifDate1;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="next_verif_date1")
	public Date getNextVerifDate1(){
		return nextVerifDate1;
	}

	public void setNextVerifDate1(Date nextVerifDate1){
		this.nextVerifDate1 = nextVerifDate1;
	}

	/**
	*  javadoc for tecnicoOut
	*/
	private Boolean tecnicoOut;

	@Column(name="tecnico_out")
	public Boolean getTecnicoOut(){
		return tecnicoOut;
	}

	public void setTecnicoOut(Boolean tecnicoOut){
		this.tecnicoOut = tecnicoOut;
	}

	/**
	*  javadoc for mmServizio
	*/
	private String mmServizio;

	@Column(name="mm_servizio")
	public String getMmServizio(){
		return mmServizio;
	}

	public void setMmServizio(String mmServizio){
		this.mmServizio = mmServizio;
	}

	/**
	*  javadoc for hhServizio
	*/
	private String hhServizio;

	@Column(name="hh_servizio")
	public String getHhServizio(){
		return hhServizio;
	}

	public void setHhServizio(String hhServizio){
		this.hhServizio = hhServizio;
	}

	/**
	*  javadoc for statusCode
	*/
	private CodeValuePhi statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statusCode")
	@ForeignKey(name="FK_VerificaImp_statusCode")
	//@Index(name="IX_VerificaImp_statusCode")
	public CodeValuePhi getStatusCode(){
		return statusCode;
	}

	public void setStatusCode(CodeValuePhi statusCode){
		this.statusCode = statusCode;
	}

	/**
	*  javadoc for modC
	*/
	private Boolean modC;

	@Column(name="mod_c")
	public Boolean getModC(){
		return modC;
	}

	public void setModC(Boolean modC){
		this.modC = modC;
	}

	/**
	*  javadoc for modB
	*/
	private String modB;

	@Column(name="mod_b")
	public String getModB(){
		return modB;
	}

	public void setModB(String modB){
		this.modB = modB;
	}

	/**
	*  javadoc for modAb
	*/
	private String modAb;

	@Column(name="mod_ab")
	public String getModAb(){
		return modAb;
	}

	public void setModAb(String modAb){
		this.modAb = modAb;
	}



	/**
	*  javadoc for gru
	*/
	private Boolean gru;

	@Column(name="gru")
	public Boolean getGru(){
		return gru;
	}

	public void setGru(Boolean gru){
		this.gru = gru;
	}

	/**
	*  javadoc for sopralluogoBl
	*/
	private Boolean sopralluogoBl;

	@Column(name="sopralluogo_bl")
	public Boolean getSopralluogoBl(){
		return sopralluogoBl;
	}

	public void setSopralluogoBl(Boolean sopralluogoBl){
		this.sopralluogoBl = sopralluogoBl;
	}

	/**
	*  javadoc for esito02
	*/
	private CodeValuePhi esito02;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito02")
	@ForeignKey(name="FK_VerificaImp_esito02")
	//@Index(name="IX_VerificaImp_esito02")
	public CodeValuePhi getEsito02(){
		return esito02;
	}

	public void setEsito02(CodeValuePhi esito02){
		this.esito02 = esito02;
	}


	/**
	*  javadoc for impTerraCpy
	*/
	private ImpTerra impTerraCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_terra_cpy_id")
	@ForeignKey(name="FK_VerificaImp_impTerraCpy")
	//@Index(name="IX_VerificaImp_impTerraCpy")
	public ImpTerra getImpTerraCpy(){
		return impTerraCpy;
	}

	public void setImpTerraCpy(ImpTerra impTerraCpy){
		this.impTerraCpy = impTerraCpy;
	}



	/**
	*  javadoc for impSollCpy
	*/
	private ImpSoll impSollCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_soll_cpy_id")
	@ForeignKey(name="FK_VerificaImp_impSollCpy")
	//@Index(name="IX_VerificaImp_impSollCpy")
	public ImpSoll getImpSollCpy(){
		return impSollCpy;
	}

	public void setImpSollCpy(ImpSoll impSollCpy){
		this.impSollCpy = impSollCpy;
	}



	/**
	*  javadoc for impRiscCpy
	*/
	private ImpRisc impRiscCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_risc_cpy_id")
	@ForeignKey(name="FK_VerificaImp_impRiscCpy")
	//@Index(name="IX_VerificaImp_impRiscCpy")
	public ImpRisc getImpRiscCpy(){
		return impRiscCpy;
	}

	public void setImpRiscCpy(ImpRisc impRiscCpy){
		this.impRiscCpy = impRiscCpy;
	}



	/**
	*  javadoc for impPressCpy
	*/
	private ImpPress impPressCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_press_cpy_id")
	@ForeignKey(name="FK_VerificaImp_impPressCpy")
	//@Index(name="IX_VerificaImp_impPressCpy")
	public ImpPress getImpPressCpy(){
		return impPressCpy;
	}

	public void setImpPressCpy(ImpPress impPressCpy){
		this.impPressCpy = impPressCpy;
	}



	/**
	*  javadoc for impMontaCpy
	*/
	private ImpMonta impMontaCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_monta_cpy_id")
	@ForeignKey(name="FK_VerificaImp_impMontaCpy")
	//@Index(name="IX_VerificaImp_impMontaCpy")
	public ImpMonta getImpMontaCpy(){
		return impMontaCpy;
	}

	public void setImpMontaCpy(ImpMonta impMontaCpy){
		this.impMontaCpy = impMontaCpy;
	}


	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note", length=2500)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for tecnicoInOut
	*/
//	private CodeValuePhi tecnicoInOut;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="tecnicoInOut")
//	@ForeignKey(name="FK_VerificaImp_tecnicoInOut")
//	//@Index(name="IX_VerificaImp_tecnicoInOut")
//	public CodeValuePhi getTecnicoInOut(){
//		return tecnicoInOut;
//	}
//
//	public void setTecnicoInOut(CodeValuePhi tecnicoInOut){
//		this.tecnicoInOut = tecnicoInOut;
//	}

	/**
	*  javadoc for aliquota
	*/
	private CodeValuePhi aliquota;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aliquota")
	@ForeignKey(name="FK_VerificaImp_aliquota")
	//@Index(name="IX_VerificaImp_aliquota")
	public CodeValuePhi getAliquota(){
		return aliquota;
	}

	public void setAliquota(CodeValuePhi aliquota){
		this.aliquota = aliquota;
	}

	/**
	*  javadoc for rischio
	*/
	private CodeValuePhi rischio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rischio")
	@ForeignKey(name="FK_VerificaImp_rischio")
	//@Index(name="IX_VerificaImp_rischio")
	public CodeValuePhi getRischio(){
		return rischio;
	}

	public void setRischio(CodeValuePhi rischio){
		this.rischio = rischio;
	}

	/**
	*  javadoc for fluido
	*/
	private CodeValuePhi fluido;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="fluido")
	@ForeignKey(name="FK_VerificaImp_fluido")
	//@Index(name="IX_VerificaImp_fluido")
	public CodeValuePhi getFluido(){
		return fluido;
	}

	public void setFluido(CodeValuePhi fluido){
		this.fluido = fluido;
	}

	/**
	*  javadoc for dataVar
	*/
	private Date dataVar;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_var")
	public Date getDataVar(){
		return dataVar;
	}

	public void setDataVar(Date dataVar){
		this.dataVar = dataVar;
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
	*  javadoc for statoImp
	*/
	private CodeValuePhi statoImp;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statoImp")
	@ForeignKey(name="FK_VerificaImp_statoImp")
	//@Index(name="IX_VerificaImp_statoImp")
	public CodeValuePhi getStatoImp(){
		return statoImp;
	}

	public void setStatoImp(CodeValuePhi statoImp){
		this.statoImp = statoImp;
	}

	/**
	*  javadoc for regimeFiscale
	*/
	private CodeValuePhi regimeFiscale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regimeFiscale")
	@ForeignKey(name="FK_VerificaImp_regimeFiscale")
	//@Index(name="IX_VerificaImp_regimeFiscale")
	public CodeValuePhi getRegimeFiscale(){
		return regimeFiscale;
	}

	public void setRegimeFiscale(CodeValuePhi regimeFiscale){
		this.regimeFiscale = regimeFiscale;
	}

	/**
	*  javadoc for esente
	*/
	private CodeValuePhi esente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esente")
	@ForeignKey(name="FK_VerificaImp_esente")
	//@Index(name="IX_VerificaImp_esente")
	public CodeValuePhi getEsente(){
		return esente;
	}

	public void setEsente(CodeValuePhi esente){
		this.esente = esente;
	}

	/**
	*  javadoc for prescrizione
	*/
	private CodeValuePhi prescrizione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prescrizione")
	@ForeignKey(name="FK_VerificaImp_prescrizione")
	//@Index(name="IX_VerificaImp_prescrizione")
	public CodeValuePhi getPrescrizione(){
		return prescrizione;
	}

	public void setPrescrizione(CodeValuePhi prescrizione){
		this.prescrizione = prescrizione;
	}

	/**
	*  javadoc for interna
	*/
	private Boolean interna;

	@Column(name="interna")
	public Boolean getInterna(){
		return interna;
	}

	public void setInterna(Boolean interna){
		this.interna = interna;
	}

	/**
	*  javadoc for dataEsercizio
	*/
	private Date dataEsercizio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_esercizio")
	public Date getDataEsercizio(){
		return dataEsercizio;
	}

	public void setDataEsercizio(Date dataEsercizio){
		this.dataEsercizio = dataEsercizio;
	}

	/**
	*  javadoc for dataInterna
	*/
	private Date dataInterna;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_interna")
	public Date getDataInterna(){
		return dataInterna;
	}

	public void setDataInterna(Date dataInterna){
		this.dataInterna = dataInterna;
	}

	/**
	*  javadoc for dataIdraulica
	*/
	private Date dataIdraulica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_idraulica")
	public Date getDataIdraulica(){
		return dataIdraulica;
	}

	public void setDataIdraulica(Date dataIdraulica){
		this.dataIdraulica = dataIdraulica;
	}

	/**
	*  javadoc for esercizio
	*/
	private Boolean esercizio;

	@Column(name="esercizio")
	public Boolean getEsercizio(){
		return esercizio;
	}

	public void setEsercizio(Boolean esercizio){
		this.esercizio = esercizio;
	}

	/**
	*  javadoc for idraulica
	*/
	private Boolean idraulica;

	@Column(name="idraulica")
	public Boolean getIdraulica(){
		return idraulica;
	}

	public void setIdraulica(Boolean idraulica){
		this.idraulica = idraulica;
	}


	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_VrfcaImp_srvcDlvryLocton")
	//@Index(name="IX_VrfcaImp_srvcDlvryLocton")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}



	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_VerificaImp_operatore")
	//@Index(name="IX_VerificaImp_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}

	/**
	*  javadoc for sopralluogo
	*/
	private CodeValuePhi sopralluogo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sopralluogo")
	@ForeignKey(name="FK_VerificaImp_sopralluogo")
	//@Index(name="IX_VerificaImp_sopralluogo")
	public CodeValuePhi getSopralluogo(){
		return sopralluogo;
	}

	public void setSopralluogo(CodeValuePhi sopralluogo){
		this.sopralluogo = sopralluogo;
	}

	/**
	*  javadoc for tipoProva
	*/
	
	private List<CodeValuePhi> tipoProva;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="VerificaImp_tipoProva", joinColumns = { @JoinColumn(name="VerificaImp_id") }, inverseJoinColumns = { @JoinColumn(name="tipoProva") })
	@ForeignKey(name="FK_tipoProva_VerificaImp", inverseName="FK_VerificaImp_tipoProva")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getTipoProva(){
		return tipoProva;
	}

	public void setTipoProva(List<CodeValuePhi> tipoProva){
		this.tipoProva = tipoProva;
	}

	/**
	*  javadoc for prima
	*/
	private Boolean prima;

	@Column(name="prima")
	public Boolean getPrima(){
		return prima;
	}

	public void setPrima(Boolean prima){
		this.prima = prima;
	}

	/**
	*  javadoc for tipoConst
	*/
	private CodeValuePhi tipoConst;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoConst")
	@ForeignKey(name="FK_VerificaImp_tipoConst")
	//@Index(name="IX_VerificaImp_tipoConst")
	public CodeValuePhi getTipoConst(){
		return tipoConst;
	}

	public void setTipoConst(CodeValuePhi tipoConst){
		this.tipoConst = tipoConst;
	}

	/**
	*  javadoc for tipoStr
	*/
	private CodeValuePhi tipoStr;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoStr")
	@ForeignKey(name="FK_VerificaImp_tipoStr")
	//@Index(name="IX_VerificaImp_tipoStr")
	public CodeValuePhi getTipoStr(){
		return tipoStr;
	}

	public void setTipoStr(CodeValuePhi tipoStr){
		this.tipoStr = tipoStr;
	}

	/**
	*  javadoc for tipo
	*/
	private CodeValuePhi tipo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo")
	@ForeignKey(name="FK_VerificaImp_tipo")
	//@Index(name="IX_VerificaImp_tipo")
	public CodeValuePhi getTipo(){
		return tipo;
	}

	public void setTipo(CodeValuePhi tipo){
		this.tipo = tipo;
	}

	/**
	*  javadoc for esito
	*/
	private CodeValuePhi esito;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esito")
	@ForeignKey(name="FK_VerificaImp_esito")
	//@Index(name="IX_VerificaImp_esito")
	public CodeValuePhi getEsito(){
		return esito;
	}

	public void setEsito(CodeValuePhi esito){
		this.esito = esito;
	}

	/**
	*  javadoc for stato
	*/
/*	private CodeValuePhi stato;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="stato")
	@ForeignKey(name="FK_VerificaImp_stato")
	//@Index(name="IX_VerificaImp_stato")
	public CodeValuePhi getStato(){
		return stato;
	}

	public void setStato(CodeValuePhi stato){
		this.stato = stato;
	}*/

	/**
	*  javadoc for luogo
	*/
	private String luogo;

	@Column(name="luogo")
	public String getLuogo(){
		return luogo;
	}

	public void setLuogo(String luogo){
		this.luogo = luogo;
	}

	/**
	*  javadoc for data
	*/
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}

	/**
	*  javadoc for impTerra
	*/
	private ImpTerra impTerra;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_terra_id")
	@ForeignKey(name="FK_VerificaImp_impTerra")
	//@Index(name="IX_VerificaImp_impTerra")
	public ImpTerra getImpTerra(){
		return impTerra;
	}

	public void setImpTerra(ImpTerra impTerra){
		this.impTerra = impTerra;
	}



	/**
	*  javadoc for impSoll
	*/
	private ImpSoll impSoll;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_soll_id")
	@ForeignKey(name="FK_VerificaImp_impSoll")
	//@Index(name="IX_VerificaImp_impSoll")
	public ImpSoll getImpSoll(){
		return impSoll;
	}

	public void setImpSoll(ImpSoll impSoll){
		this.impSoll = impSoll;
	}



	/**
	*  javadoc for impRisc
	*/
	private ImpRisc impRisc;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_risc_id")
	@ForeignKey(name="FK_VerificaImp_impRisc")
	//@Index(name="IX_VerificaImp_impRisc")
	public ImpRisc getImpRisc(){
		return impRisc;
	}

	public void setImpRisc(ImpRisc impRisc){
		this.impRisc = impRisc;
	}



	/**
	*  javadoc for impPress
	*/
	private ImpPress impPress;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_press_id")
	@ForeignKey(name="FK_VerificaImp_impPress")
	//@Index(name="IX_VerificaImp_impPress")
	public ImpPress getImpPress(){
		return impPress;
	}

	public void setImpPress(ImpPress impPress){
		this.impPress = impPress;
	}



	/**
	*  javadoc for impMonta
	*/
	private ImpMonta impMonta;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_monta_id")
	@ForeignKey(name="FK_VerificaImp_impMonta")
	//@Index(name="IX_VerificaImp_impMonta")
	public ImpMonta getImpMonta(){
		return impMonta;
	}

	public void setImpMonta(ImpMonta impMonta){
		this.impMonta = impMonta;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VerificaImp_sequence")
	@SequenceGenerator(name = "VerificaImp_sequence", sequenceName = "VerificaImp_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="VerImp_id")
	@ForeignKey(name="FK_documenti_VerImp")
	//@Index(name="IX_documenti_VerImp")
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

	public String toString() {

		return "Verifica Impianto " + internalId;
	}
}
