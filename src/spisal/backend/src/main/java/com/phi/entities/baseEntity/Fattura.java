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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.ServiceDeliveryLocation;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.PersoneGiuridiche;



@javax.persistence.Entity
@Table(name = "fattura")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Fattura extends BaseEntity implements LocatedEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8157029654248074740L;

	/**
	*  javadoc for esenteIva
	*/
	private Double esenteIva;

	@Column(name="esente_iva")
	public Double getEsenteIva(){
		return esenteIva;
	}

	public void setEsenteIva(Double esenteIva){
		this.esenteIva = esenteIva;
	}


	/**
	*  javadoc for personaGiuridicaAdd
	*/
	private PersoneGiuridiche personaGiuridicaAdd;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persona_giuridica_add_id")
	@ForeignKey(name="FK_Fattura_pgAdd")
	//@Index(name="IX_Fattur_personGiuridicAdd")
	public PersoneGiuridiche getPersonaGiuridicaAdd(){
		return personaGiuridicaAdd;
	}

	public void setPersonaGiuridicaAdd(PersoneGiuridiche personaGiuridicaAdd){
		this.personaGiuridicaAdd = personaGiuridicaAdd;
	}



	/**
	*  javadoc for imponibile
	*/
	private Double imponibile;

	@Column(name="imponibile")
	public Double getImponibile(){
		return imponibile;
	}

	public void setImponibile(Double imponibile){
		this.imponibile = imponibile;
	}


	/**
	*  javadoc for personaGiuridica
	*/
	private PersoneGiuridiche personaGiuridica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="persona_giuridica_id")
	@ForeignKey(name="FK_Fattura_personaGiuridica")
	//@Index(name="IX_Fattura_personaGiuridica")
	public PersoneGiuridiche getPersonaGiuridica(){
		return personaGiuridica;
	}

	public void setPersonaGiuridica(PersoneGiuridiche personaGiuridica){
		this.personaGiuridica = personaGiuridica;
	}



	/**
	*  javadoc for addebito
	*/
	private List<Addebito> addebito;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="fattura", cascade=CascadeType.PERSIST)
	public List<Addebito> getAddebito() {
		return addebito;
	}

	public void setAddebito(List<Addebito>list){
		addebito = list;
	}

	public void addAddebito(Addebito addebito) {
		if (this.addebito == null) {
			this.addebito = new ArrayList<Addebito>();
		}
		// add the association
		if(!this.addebito.contains(addebito)) {
			this.addebito.add(addebito);
			// make the inverse link
			addebito.setFattura(this);
		}
	}

	public void removeAddebito(Addebito addebito) {
		if (this.addebito == null) {
			this.addebito = new ArrayList<Addebito>();
			return;
		}
		//add the association
		if(this.addebito.contains(addebito)){
			this.addebito.remove(addebito);
			//make the inverse link
			addebito.setFattura(null);
		}
	}


	/**
	*  javadoc for gruppo
	*/
	private Integer gruppo;

	@Column(name="gruppo")
	public Integer getGruppo(){
		return gruppo;
	}

	public void setGruppo(Integer gruppo){
		this.gruppo = gruppo;
	}

	/**
	*  javadoc for statusCode
	*/
	private CodeValuePhi statusCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="statusCode")
	@ForeignKey(name="FK_Fattura_statusCode")
	//@Index(name="IX_Fattura_statusCode")
	public CodeValuePhi getStatusCode(){
		return statusCode;
	}

	public void setStatusCode(CodeValuePhi statusCode){
		this.statusCode = statusCode;
	}

	/**
	*  javadoc for archiviata
	*/
	private Boolean archiviata;

	@Column(name="archiviata")
	public Boolean getArchiviata(){
		return archiviata;
	}

	public void setArchiviata(Boolean archiviata){
		this.archiviata = archiviata;
	}

	/**
	*  javadoc for tipologiaDocumento
	*/
	private CodeValuePhi tipologiaDocumento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaDocumento")
	@ForeignKey(name="FK_Fattura_tipologiaDocumento")
	//@Index(name="IX_Fattura_tipologiaDocumento")
	public CodeValuePhi getTipologiaDocumento(){
		return tipologiaDocumento;
	}

	public void setTipologiaDocumento(CodeValuePhi tipologiaDocumento){
		this.tipologiaDocumento = tipologiaDocumento;
	}

	/**
	*  javadoc for tipologiaFattura
	*/
	private CodeValuePhi tipologiaFattura;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaFattura")
	@ForeignKey(name="FK_Fattura_tipologiaFattura")
	//@Index(name="IX_Fattura_tipologiaFattura")
	public CodeValuePhi getTipologiaFattura(){
		return tipologiaFattura;
	}

	public void setTipologiaFattura(CodeValuePhi tipologiaFattura){
		this.tipologiaFattura = tipologiaFattura;
	}

	/**
	*  javadoc for gruppo
	*
	private String gruppo;

	@Column(name="gruppo")
	public String getGruppo(){
		return gruppo;
	}

	public void setGruppo(String gruppo){
		this.gruppo = gruppo;
	}*/

	/**
	*  javadoc for meseAl
	*/
	private Integer meseAl;

	@Column(name="mese_al")
	public Integer getMeseAl(){
		return meseAl;
	}

	public void setMeseAl(Integer meseAl){
		this.meseAl = meseAl;
	}

	/**
	*  javadoc for meseDal
	*/
	private Integer meseDal;

	@Column(name="mese_dal")
	public Integer getMeseDal(){
		return meseDal;
	}

	public void setMeseDal(Integer meseDal){
		this.meseDal = meseDal;
	}

	/**
	*  javadoc for anno
	*/
	private Integer anno;

	@Column(name="anno")
	public Integer getAnno(){
		return anno;
	}

	public void setAnno(Integer anno){
		this.anno = anno;
	}

	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="fattura", cascade=CascadeType.PERSIST)
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
			verificaImp.setFattura(this);
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
			verificaImp.setFattura(null);
		}
	}
	
	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="sdl_id")
	@ForeignKey(name="FK_Fattura_srvcDlvryLocton")
	//@Index(name="IX_Fattura_srvcDlvryLocton")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	/**
	*  javadoc for auth
	*/
	private Employee auth;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="auth_id")
	@ForeignKey(name="FK_Fattura_auth")
	//@Index(name="IX_Fattura_auth")
	public Employee getAuth(){
		return auth;
	}

	public void setAuth(Employee auth){
		this.auth = auth;
	}

	/**
	*  javadoc for contentType
	*/
	private String contentType;

	@Column(name="content_type")
	public String getContentType(){
		return contentType;
	}

	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	/**
	*  javadoc for filesize
	*/
	private Integer filesize;

	@Column(name="filesize")
	public Integer getFilesize(){
		return filesize;
	}

	public void setFilesize(Integer filesize){
		this.filesize = filesize;
	}

	/**
	*  javadoc for filename
	*/
	private String filename;

	@Column(name="filename")
	public String getFilename(){
		return filename;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	
	/**
	*  javadoc for content
	*/
	private byte[] content;

	@Lob
	@Column(name="content")
	public byte[] getContent(){
		return content;
	}

	public void setContent(byte[] content){
		this.content = content;
	}


	/**
	*  javadoc for dataRif
	*/
	private Date dataRif;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_rif")
	public Date getDataRif(){
		return dataRif;
	}

	public void setDataRif(Date dataRif){
		this.dataRif = dataRif;
	}
	
	/**
	*  javadoc for descr
	*/
	private String descr;

	@Column(name="descr")
	public String getDescr(){
		return descr;
	}

	public void setDescr(String descr){
		this.descr = descr;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ConfGene_sequence")
	@SequenceGenerator(name = "ConfGene_sequence", sequenceName = "ConfGene_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_Fattura_operatore")
	//@Index(name="IX_Fattura_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}
}
