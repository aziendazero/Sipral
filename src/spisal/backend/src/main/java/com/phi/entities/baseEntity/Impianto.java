package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;

@MappedSuperclass
@javax.persistence.Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Impianto extends BaseEntity {

	private static final long serialVersionUID = 510813642L;


	/**
	*  javadoc for verificaLast
	*/
//	private VerificaImp verificaLast;
//
//	@OneToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="verifica_last_id")
//	@ForeignKey(name="FK_Impianto_verificaLast")
//	@Index(name="IX_Impianto_verificaLast")
//	public VerificaImp getVerificaLast(){
//		return verificaLast;
//	}
//
//	public void setVerificaLast(VerificaImp verificaLast){
//		this.verificaLast = verificaLast;
//	}


	/**
	*  javadoc for verificheLong
	*/
	private Long verificheLong;

	@Column(name="sede_addebito_id")
	public Long getVerificheLong(){
		return verificheLong;
	}

	public void setVerificheLong(Long verificheLong){
		this.verificheLong = verificheLong;
	}

	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_Impianto_sedi")
	//@Index(name="IX_Impianto_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}

	
	/**
	*  javadoc for isNew
	*/
	private Boolean isNew;

	@Column(name="is_new")
	public Boolean getIsNew(){
		return isNew;
	}

	public void setIsNew(Boolean isNew){
		this.isNew = isNew;
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
	*  javadoc for enteVerificatoreExt
	*
	private CodeValuePhi enteVerificatoreExt;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="enteVerificatoreExt")
	@ForeignKey(name="FK_Impianto_enteVerificatoreExt")
	//@Index(name="IX_Impianto_enteVerificatoreExt")
	public CodeValuePhi getEnteVerificatoreExt(){
		return enteVerificatoreExt;
	}

	public void setEnteVerificatoreExt(CodeValuePhi enteVerificatoreExt){
		this.enteVerificatoreExt = enteVerificatoreExt;
	}*/
	
	/**
	*  javadoc for targa
	*/
	private String targa;

	@Column(name="targa")
	public String getTarga(){
		return targa;
	}

	public void setTarga(String targa){
		this.targa = targa;
	}

	/**
	*  javadoc for telaio
	*/
	private String telaio;

	@Column(name="telaio")
	public String getTelaio(){
		return telaio;
	}

	public void setTelaio(String telaio){
		this.telaio = telaio;
	}
	
	/**
	*  javadoc for dataCollaudo
	*/
	private Date dataCollaudo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_collaudo")
	public Date getDataCollaudo(){
		return dataCollaudo;
	}

	public void setDataCollaudo(Date dataCollaudo){
		this.dataCollaudo = dataCollaudo;
	}

	/**
	*  javadoc for statoImpianto
	*/
	private CodeValuePhi statoImpianto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statoImpianto")
	@ForeignKey(name="FK_Impianto_statoImpianto")
	//@Index(name="IX_Impianto_statoImpianto")
	public CodeValuePhi getStatoImpianto(){
		return statoImpianto;
	}

	public void setStatoImpianto(CodeValuePhi statoImpianto){
		this.statoImpianto = statoImpianto;
	}

	/**
	*  javadoc for dataVariazioneStato
	*/
	private Date dataVariazioneStato;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_variazione_stato")
	public Date getDataVariazioneStato(){
		return dataVariazioneStato;
	}

	public void setDataVariazioneStato(Date dataVariazioneStato){
		this.dataVariazioneStato = dataVariazioneStato;
	}

	/**
	*  javadoc for utenteUltimaModifica
	*/
	private Employee utenteUltimaModifica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="utente_ultima_modifica_id")
	@ForeignKey(name="FK_mpanto_utenteUltmaModfca")
	//@Index(name="IX_mpanto_utenteUltmaModfca")
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
	*  javadoc for dataAssegnazione
	*/
	private Date dataAssegnazione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_assegnazione")
	public Date getDataAssegnazione(){
		return dataAssegnazione;
	}

	public void setDataAssegnazione(Date dataAssegnazione){
		this.dataAssegnazione = dataAssegnazione;
	}

	/**
	*  javadoc for nextVerifDate
	
	private Date nextVerifDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="next_verif_date")
	public Date getNextVerifDate(){
		return nextVerifDate;
	}

	public void setNextVerifDate(Date nextVerifDate){
		this.nextVerifDate = nextVerifDate;
	}/*

	/**
	*  javadoc for enteVerificatore
	*/
	private CodeValuePhi enteVerificatore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="enteVerificatore")
	@ForeignKey(name="FK_Impianto_enteVerificatore")
	//@Index(name="IX_Impianto_enteVerificatore")
	public CodeValuePhi getEnteVerificatore(){
		return enteVerificatore;
	}

	public void setEnteVerificatore(CodeValuePhi enteVerificatore){
		this.enteVerificatore = enteVerificatore;
	}

	/**
	*  javadoc for copy
	*/
	private boolean copy = false;

	@Column(name="copy")
	public boolean getCopy(){
		return copy;
	}

	public void setCopy(boolean copy){
		this.copy = copy;
	}
	
	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Impianto_code")
	//@Index(name="IX_Impianto_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}
	/**
	*  javadoc for subTypeSoll
	*/
	private CodeValuePhi subTypeSoll;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeSoll")
	@ForeignKey(name="FK_Impianto_subTypeSoll")
	//@Index(name="IX_Impianto_subTypeSoll")
	public CodeValuePhi getSubTypeSoll(){
		return subTypeSoll;
	}

	public void setSubTypeSoll(CodeValuePhi subTypeSoll){
		this.subTypeSoll = subTypeSoll;
	}

	/**
	*  javadoc for subTypeTerra
	*/
	private CodeValuePhi subTypeTerra;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeTerra")
	@ForeignKey(name="FK_Impianto_subTypeTerra")
	//@Index(name="IX_Impianto_subTypeTerra")
	public CodeValuePhi getSubTypeTerra(){
		return subTypeTerra;
	}

	public void setSubTypeTerra(CodeValuePhi subTypeTerra){
		this.subTypeTerra = subTypeTerra;
	}

//	/**
//	*  javadoc for subType2
//	*/
//	private CodeValuePhi subType2;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="subType2")
//	@ForeignKey(name="FK_Impianto_subType2")
//	//@Index(name="IX_Impianto_subType2")
//	public CodeValuePhi getSubType2(){
//		return subType2;
//	}
//
//	public void setSubType2(CodeValuePhi subType2){
//		this.subType2 = subType2;
//	}
//
//	/**
//	*  javadoc for subType1
//	*/
//	private CodeValueIcd9 subType1;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="subType1")
//	@ForeignKey(name="FK_Impianto_subType1")
//	//@Index(name="IX_Impianto_subType1")
//	public CodeValueIcd9 getSubType1(){
//		return subType1;
//	}
//
//	public void setSubType1(CodeValueIcd9 subType1){
//		this.subType1 = subType1;
//	}


	/**
	*  javadoc for indirizzoSped
	*/
	private IndirizzoSped indirizzoSped;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="indirizzo_sped_id")
	@ForeignKey(name="FK_Impianto_indirizzoSped")
	//@Index(name="IX_Impianto_indirizzoSped")
	public IndirizzoSped getIndirizzoSped(){
		return indirizzoSped;
	}

	public void setIndirizzoSped(IndirizzoSped indirizzoSped){
		this.indirizzoSped = indirizzoSped;
	}

	/**
	*  javadoc for denominazione
	*/
	private String denominazione;

	@Column(name="denominazione")
	public String getDenominazione(){
		return denominazione;
	}

	public void setDenominazione(String denominazione){
		this.denominazione = denominazione;
	}

	/**
	*  javadoc for addr
	*/
	private AD addr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="addr_code"))
	@AttributeOverrides({
	@AttributeOverride(name="adl", column=@Column(name="addr_adl")),
	@AttributeOverride(name="bnr", column=@Column(name="addr_bnr")),
	@AttributeOverride(name="cen", column=@Column(name="addr_cen")),
	@AttributeOverride(name="cnt", column=@Column(name="addr_cnt")),
	@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),
	@AttributeOverride(name="cty", column=@Column(name="addr_cty")),
	@AttributeOverride(name="sta", column=@Column(name="addr_sta")),
	@AttributeOverride(name="stb", column=@Column(name="addr_stb")),
	@AttributeOverride(name="str", column=@Column(name="addr_str")),
	@AttributeOverride(name="zip", column=@Column(name="addr_zip"))
	})
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
	}

	/**
	*  javadoc for anno
	*/
	private String anno;

	@Column(name="anno")
	public String getAnno(){
		return anno;
	}

	public void setAnno(String anno){
		this.anno = anno;
	}

	/**
	*  javadoc for matricola
	*/
	private String matricola;

	@Column(name="matricola")
	public String getMatricola(){
		return matricola;
	}

	public void setMatricola(String matricola){
		this.matricola = matricola;
	}

	/**
	*  javadoc for sigla
	*/
	private String sigla;

	@Column(name="sigla")
	public String getSigla(){
		return sigla;
	}

	public void setSigla(String sigla){
		this.sigla = sigla;
	}


//	/**
//	*  javadoc for verificaImpi
//	*/
//	private List<VerificaImpi> verificaImpi;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="impianto", cascade=CascadeType.PERSIST)
//	public List<VerificaImpi> getVerificaImpi() {
//		return verificaImpi;
//	}
//
//	public void setVerificaImpi(List<VerificaImpi>list){
//		verificaImpi = list;
//	}
//
//	public void addVerificaImpi(VerificaImpi verificaImpi) {
//		if (this.verificaImpi == null) {
//			this.verificaImpi = new ArrayList<VerificaImpi>();
//		}
//		// add the association
//		if(!this.verificaImpi.contains(verificaImpi)) {
//			this.verificaImpi.add(verificaImpi);
//			// make the inverse link
//			verificaImpi.setImpianto(this);
//		}
//	}
//
//	public void removeVerificaImpi(VerificaImpi verificaImpi) {
//		if (this.verificaImpi == null) {
//			this.verificaImpi = new ArrayList<VerificaImpi>();
//			return;
//		}
//		//add the association
//		if(this.verificaImpi.contains(verificaImpi)){
//			this.verificaImpi.remove(verificaImpi);
//			//make the inverse link
//			verificaImpi.setImpianto(null);
//		}
//	}


	/**
	*  javadoc for codiceImpianto
	*/
	private String codiceImpianto;

	@Column(name="codice_impianto")
	public String getCodiceImpianto(){
		return codiceImpianto;
	}

	public void setCodiceImpianto(String codiceImpianto){
		this.codiceImpianto = codiceImpianto;
	}


	/**
	*  javadoc for sedeAddebito
	*/
//	private SediAddebito sedeAddebito;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="sede_addebito_id")
//	@ForeignKey(name="FK_Impianto_sedeAddebito")
//	//@Index(name="IX_Impianto_sedeAddebito")
//	public SediAddebito getSedeAddebito(){
//		return sedeAddebito;
//	}
//
//	public void setSedeAddebito(SediAddebito sedeAddebito){
//		this.sedeAddebito = sedeAddebito;
//	}



	/**
	*  javadoc for sedeInstallazione
	*/
	private SediInstallazione sedeInstallazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_installazione_id")
	@ForeignKey(name="FK_mpianto_sedenstallazione")
	//@Index(name="IX_mpianto_sedenstallazione")
	public SediInstallazione getSedeInstallazione(){
		return sedeInstallazione;
	}

	public void setSedeInstallazione(SediInstallazione sedeInstallazione){
		this.sedeInstallazione = sedeInstallazione;
	}

	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Impianto_sequence")
	@SequenceGenerator(name = "Impianto_sequence", sequenceName = "Impianto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	public String toString() {

		return "Impianto " + internalId;
	}
	
//	/**
//	*  javadoc for documenti
//	*/
//	private List<AlfrescoDocument> documenti;
//
//	@OneToMany(fetch=FetchType.LAZY)
//	@JoinColumn(name="Impianto_id")
//	@ForeignKey(name="FK_documenti_Impianto")
//	public List<AlfrescoDocument> getDocumenti() {
//		return documenti;
//	}
//
//	public void setDocumenti(List<AlfrescoDocument>list){
//		documenti = list;
//	}
//	
//
//	public void addDocumenti(AlfrescoDocument alfrescoDocument) {
//		if (this.documenti == null) {
//			this.documenti = new ArrayList<AlfrescoDocument>();
//		}
//		if(!this.documenti.contains(alfrescoDocument)) {
//			this.documenti.add(alfrescoDocument);
//		}
//	}
//	
//	public void removeDocumenti(AlfrescoDocument alfrescoDocument) {
//		if (this.documenti == null) {
//			return;
//		}
//		if(this.documenti.contains(alfrescoDocument)) {
//			this.documenti.remove(alfrescoDocument);
//		}
//	}

}
