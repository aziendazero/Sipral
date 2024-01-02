package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Table(name = "addebito")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Addebito extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2954947854327219643L;

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
	*  javadoc for isNew
	*/
	private Boolean isNew;

	//@Column(name="is_new")
	@Transient
	public Boolean getIsNew(){
		return isNew;
	}

	public void setIsNew(Boolean isNew){
		this.isNew = isNew;
	}

	/**
	*  javadoc for casualeAddExt
	*/
	private String causaleAddTxt;

	@Column(name="causale_add_txt")
	public String getCausaleAddTxt(){
		return causaleAddTxt;
	}

	public void setCausaleAddTxt(String causaleAddTxt){
		this.causaleAddTxt = causaleAddTxt;
	}


	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@OneToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_Addebito_sedi")
	//@Index(name="IX_Addebito_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}



	/**
	*  javadoc for sedeAddebito
	*/
//	private SediAddebito sedeAddebito;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="sede_addebito_id")
//	@ForeignKey(name="FK_Addebito_sedeAddebito")
//	//@Index(name="IX_Addebito_sedeAddebito")
//	public SediAddebito getSedeAddebito(){
//		return sedeAddebito;
//	}
//
//	public void setSedeAddebito(SediAddebito sedeAddebito){
//		this.sedeAddebito = sedeAddebito;
//	}


	/**
	*  javadoc for numeroDoc
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
	@ForeignKey(name="FK_Addebito_fattura")
	//@Index(name="IX_Addebito_fattura")
	public Fattura getFattura(){
		return fattura;
	}

	public void setFattura(Fattura fattura){
		this.fattura = fattura;
	}


	/**
	*  javadoc for casualeAdd
	*/
	private CodeValuePhi casualeAdd;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="casualeAdd")
	@ForeignKey(name="FK_Addebito_casualeAdd")
	//@Index(name="IX_Addebito_casualeAdd")
	public CodeValuePhi getCasualeAdd(){
		return casualeAdd;
	}

	public void setCasualeAdd(CodeValuePhi casualeAdd){
		this.casualeAdd = casualeAdd;
	}


	/**
	*  javadoc for utenteUltimaModifica
	*/
	private Employee utenteUltimaModifica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="utente_ultima_modifica_id")
	@ForeignKey(name="FK_ddbito_utntUltimaModifca")
	//@Index(name="IX_ddbito_utntUltimaModifca")
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
	*  javadoc for subTypeTerra
	*/
	private CodeValuePhi subTypeTerra;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeTerra")
	@ForeignKey(name="FK_Addebito_subTypeTerra")
	//@Index(name="IX_Addebito_subTypeTerra")
	public CodeValuePhi getSubTypeTerra(){
		return subTypeTerra;
	}

	public void setSubTypeTerra(CodeValuePhi subTypeTerra){
		this.subTypeTerra = subTypeTerra;
	}

	/**
	*  javadoc for subTypeSoll
	*/
	private CodeValuePhi subTypeSoll;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeSoll")
	@ForeignKey(name="FK_Addebito_subTypeSoll")
	//@Index(name="IX_Addebito_subTypeSoll")
	public CodeValuePhi getSubTypeSoll(){
		return subTypeSoll;
	}

	public void setSubTypeSoll(CodeValuePhi subTypeSoll){
		this.subTypeSoll = subTypeSoll;
	}


	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_ddebito_personeGiuridich")
	//@Index(name="IX_ddebito_personeGiuridich")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}


	/**
	*  javadoc for importo
	*/
	private String importo;

	@Column(name="importo")
	public String getImporto(){
		return importo;
	}

	public void setImporto(String importo){
		this.importo = importo;
	}

	/**
	*  javadoc for quantita
	*/
	private String quantita;

	@Column(name="quantita")
	public String getQuantita(){
		return quantita;
	}

	public void setQuantita(String quantita){
		this.quantita = quantita;
	}

	/**
	*  javadoc for type
	*/
//	private CodeValuePhi type;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="type")
//	@ForeignKey(name="FK_Addebito_type")
//	//@Index(name="IX_Addebito_type")
//	public CodeValuePhi getType(){
//		return type;
//	}
//
//	public void setType(CodeValuePhi type){
//		this.type = type;
//	}

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
	*  javadoc for causaleTxt
	*/
	private String causaleTxt;

	@Column(name="causale_txt")
	public String getCausaleTxt(){
		return causaleTxt;
	}

	public void setCausaleTxt(String causaleTxt){
		this.causaleTxt = causaleTxt;
	}

	/**
	*  javadoc for causale
	*/
	private CodeValuePhi causale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="causale")
	@ForeignKey(name="FK_Addebito_causale")
	//@Index(name="IX_Addebito_causale")
	public CodeValuePhi getCausale(){
		return causale;
	}

	public void setCausale(CodeValuePhi causale){
		this.causale = causale;
	}

	/**
	*  javadoc for aliquota
	*/
	private CodeValuePhi aliquota;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aliquota")
	@ForeignKey(name="FK_Addebito_aliquota")
	//@Index(name="IX_Addebito_aliquota")
	public CodeValuePhi getAliquota(){
		return aliquota;
	}

	public void setAliquota(CodeValuePhi aliquota){
		this.aliquota = aliquota;
	}

	/**
	*  javadoc for regimeFiscale
	*/
	private CodeValuePhi regimeFiscale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="regimeFiscale")
	@ForeignKey(name="FK_Addebito_regimeFiscale")
	//@Index(name="IX_Addebito_regimeFiscale")
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
	@ForeignKey(name="FK_Addebito_esente")
	//@Index(name="IX_Addebito_esente")
	public CodeValuePhi getEsente(){
		return esente;
	}

	public void setEsente(CodeValuePhi esente){
		this.esente = esente;
	}

	/**
	*  javadoc for rischio
	*/
	private CodeValuePhi rischio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rischio")
	@ForeignKey(name="FK_Addebito_rischio")
	//@Index(name="IX_Addebito_rischio")
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
	@ForeignKey(name="FK_Addebito_fluido")
	//@Index(name="IX_Addebito_fluido")
	public CodeValuePhi getFluido(){
		return fluido;
	}

	public void setFluido(CodeValuePhi fluido){
		this.fluido = fluido;
	}

	/**
	*  javadoc for impType
	*/
	private CodeValuePhi impType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="impType")
	@ForeignKey(name="FK_Addebito_impType")
	//@Index(name="IX_Addebito_impType")
	public CodeValuePhi getImpType(){
		return impType;
	}

	public void setImpType(CodeValuePhi impType){
		this.impType = impType;
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
//	*  javadoc for meseRif
//	*/
//	private String meseRif;
//
//	@Column(name="mese_rif")
//	public String getMeseRif(){
//		return meseRif;
//	}
//
//	public void setMeseRif(String meseRif){
//		this.meseRif = meseRif;
//	}
//
//	/**
//	*  javadoc for annoRif
//	*/
//	private String annoRif;
//
//	@Column(name="anno_rif")
//	public String getAnnoRif(){
//		return annoRif;
//	}
//
//	public void setAnnoRif(String annoRif){
//		this.annoRif = annoRif;
//	}
	
	/**
	*  javadoc for impSearchCollector
	*/
	private ImpSearchCollector impSearchCollector;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="imp_search_collector_id")
	@ForeignKey(name="FK_Add_mpSarchCollctor")
	//@Index(name="IX_Add_mpSarchCollctor")
	public ImpSearchCollector getImpSearchCollector(){
		return impSearchCollector;
	}

	public void setImpSearchCollector(ImpSearchCollector impSearchCollector){
		this.impSearchCollector = impSearchCollector;
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
	*  javadoc for statusCode
	*/
	private CodeValuePhi statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statusCode")
	@ForeignKey(name="FK_Add_statusCode")
	//@Index(name="IX_Add_statusCode")
	public CodeValuePhi getStatusCode(){
		return statusCode;
	}

	public void setStatusCode(CodeValuePhi statusCode){
		this.statusCode = statusCode;
	}

	/**
	*  javadoc for impTerraCpy
	*/
	private ImpTerra impTerraCpy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="imp_terra_cpy_id")
	@ForeignKey(name="FK_Add_impTerraCpy")
	//@Index(name="IX_Add_impTerraCpy")
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
	@ForeignKey(name="FK_Add_impSollCpy")
	//@Index(name="IX_Add_impSollCpy")
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
	@ForeignKey(name="FK_Add_impRiscCpy")
	//@Index(name="IX_Add_impRiscCpy")
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
	@ForeignKey(name="FK_Add_impPressCpy")
	//@Index(name="IX_Add_impPressCpy")
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
	@ForeignKey(name="FK_Add_impMontaCpy")
	//@Index(name="IX_Add_impMontaCpy")
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

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for tecnicoInOut
	*/

	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_Add_srvcDlvryLocton")
	//@Index(name="IX_Add_srvcDlvryLocton")
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
	@ForeignKey(name="FK_Add_operatore")
	//@Index(name="IX_Add_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
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
	@ForeignKey(name="FK_Add_impTerra")
	//@Index(name="IX_Add_impTerra")
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
	@ForeignKey(name="FK_Add_impSoll")
	//@Index(name="IX_Add_impSoll")
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
	@ForeignKey(name="FK_Add_impRisc")
	//@Index(name="IX_Add_impRisc")
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
	@ForeignKey(name="FK_Add_impPress")
	//@Index(name="IX_Add_impPress")
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
	@ForeignKey(name="FK_Add_impMonta")
	//@Index(name="IX_Add_impMonta")
	public ImpMonta getImpMonta(){
		return impMonta;
	}

	public void setImpMonta(ImpMonta impMonta){
		this.impMonta = impMonta;
	}

		
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Addebito_sequence")
	@SequenceGenerator(name = "Addebito_sequence", sequenceName = "Addebito_sequence")
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
	@JoinColumn(name="Addebito_id")
	@ForeignKey(name="FK_documenti_Addebito")
	//@Index(name="IX_documenti_Addebito")
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

	public String toString() {

		return "Addebiti Vari Impianto " + internalId;
	}
}
