package com.phi.entities.baseEntity;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;

import javax.persistence.CascadeType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.phi.entities.baseEntity.PNCPersoneCantiere;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.PNCCantiere;
@javax.persistence.Entity
@Table(name = "pnc_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Audited
public class PNCCantiere extends BaseEntity {

	private static final long serialVersionUID = 815676305L;

    private String longitudine;
	
	@Column(name = "longitudine")
    public String getLongitudine() {
		return longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}

    private String latitudine;

    @Column(name = "latitudine")
	public String getLatitudine() {
		return latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
	}

	/**
	*  javadoc for parent
	*/
	private Long parent;

	@Column(name="id_parent")
	public Long getParent(){
		return parent;
	}

	public void setParent(Long parent){
		this.parent = parent;
	}


	/**
	*  javadoc for comune
	*/
	private String comune;

	@Column(name="comune")
	public String getComune(){
		return comune;
	}

	public void setComune(String comune){
		this.comune = comune;
	}

	/**
	*  javadoc for civico
	*/
	private String civico;

	@Column(name="civico", length = 15)
	public String getCivico(){
		return civico;
	}

	public void setCivico(String civico){
		this.civico = civico;
	}

	/**
	*  javadoc for rispostaMirth
	*/
	private String rispostaMirth;

	@Column(name="risposta_mirth")
	public String getRispostaMirth(){
		return rispostaMirth;
	}

	public void setRispostaMirth(String rispostaMirth){
		this.rispostaMirth = rispostaMirth;
	}

	/**
	*  javadoc for createdUser
	*/
	private String createdUser;

	@Column(name="created_user")
	public String getCreatedUser(){
		return createdUser;
	}

	public void setCreatedUser(String createdUser){
		this.createdUser = createdUser;
	}

	/**
	*  javadoc for pecRecipient
	*/
	private String pecRecipient;

	@Column(name="pec_recipient")
	public String getPecRecipient(){
		return pecRecipient;
	}

	public void setPecRecipient(String pecRecipient){
		this.pecRecipient = pecRecipient;
	}

	/**
	*  javadoc for pecSender
	*/
	private String pecSender;

	@Column(name="pec_sender")
	public String getPecSender(){
		return pecSender;
	}

	public void setPecSender(String pecSender){
		this.pecSender = pecSender;
	}
	
	/**
	*  javadoc for tipo
	*/
	private CodeValuePhi tipo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo")
	@ForeignKey(name="FK_Cantiere_tipo")
	//@Index(name="IX_Cantiere_tipo")
	public CodeValuePhi getTipo(){
		return tipo;
	}

	public void setTipo(CodeValuePhi tipo){
		this.tipo = tipo;
	}

	private byte[] pdf;
	
	@Lob
	@Column(name="pdf_bcode")
	public byte[] getPdf() {
		return this.pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}

	/**
	*  javadoc for personeCantiere
	*/
	private List<PNCPersoneCantiere> personeCantiere;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<PNCPersoneCantiere> getPersoneCantiere() {
		return personeCantiere;
	}

	public void setPersoneCantiere(List<PNCPersoneCantiere>list){
		personeCantiere = list;
	}

	public void addPersoneCantiere(PNCPersoneCantiere personeCantiere) {
		if (this.personeCantiere == null) {
			this.personeCantiere = new ArrayList<PNCPersoneCantiere>();
		}
		// add the association
		if(!this.personeCantiere.contains(personeCantiere)) {
			this.personeCantiere.add(personeCantiere);
			// make the inverse link
			personeCantiere.setCantiere(this);
		}
	}

	public void removePersoneCantiere(PNCPersoneCantiere personeCantiere) {
		if (this.personeCantiere == null) {
			this.personeCantiere = new ArrayList<PNCPersoneCantiere>();
			return;
		}
		//add the association
		if(this.personeCantiere.contains(personeCantiere)){
			this.personeCantiere.remove(personeCantiere);
			//make the inverse link
			personeCantiere.setCantiere(null);
		}
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PNCC_sequence")
	@SequenceGenerator(name = "PNCC_sequence", sequenceName = "PNCC_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for dataComunicazione
	*/
	private Date dataComunicazione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_comunicazione")
	public Date getDataComunicazione(){
		return dataComunicazione;
	}

	public void setDataComunicazione(Date dataComunicazione){
		this.dataComunicazione = dataComunicazione;
	}
	
	/**
	*  javadoc for indirizzo cantiere
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
	*  javadoc for naturaOpera
	*/
	private String naturaOpera;

	@Column(name="natura_opera", length=1000)
	public String getNaturaOpera(){
		return naturaOpera;
	}

	public void setNaturaOpera(String naturaOpera){
		this.naturaOpera = naturaOpera;
	}
	
	/**
	*  javadoc for inizioLavori
	*/
	private Date inizioLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="inizio_lavori")
	public Date getInizioLavori(){
		return inizioLavori;
	}

	public void setInizioLavori(Date inizioLavori){
		this.inizioLavori = inizioLavori;
	}
	
	/**
	*  javadoc for durataLavori
	*/
	private Integer durataLavori;

	@Column(name="durata_lavori")
	public Integer getDurataLavori(){
		return durataLavori;
	}

	public void setDurataLavori(Integer durataLavori){
		this.durataLavori = durataLavori;
	}

	/**
	*  javadoc for maxWorkers
	*/
	private Integer maxWorkers;

	@Column(name="max_workers")
	public Integer getMaxWorkers(){
		return maxWorkers;
	}

	public void setMaxWorkers(Integer maxWorkers){
		this.maxWorkers = maxWorkers;
	}
	
	/**
	*  javadoc for numeroImprese
	*/
	private Integer numeroImprese;

	@Column(name="numero_imprese")
	public Integer getNumeroImprese(){
		return numeroImprese;
	}

	public void setNumeroImprese(Integer numeroImprese){
		this.numeroImprese = numeroImprese;
	}
	/**
	*  javadoc for cost
	*/
	private Double cost;

	@Column(name="cost")
	public Double getCost(){
		return cost;
	}

	public void setCost(Double cost){
		this.cost = cost;
	}
	
	/**
	*  javadoc for ditteCantiere
	*/
	private List<PNCDitteCantiere> ditteCantiere;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<PNCDitteCantiere> getDitteCantiere() {
		return ditteCantiere;
	}

	public void setDitteCantiere(List<PNCDitteCantiere>list){
		ditteCantiere = list;
	}

	public void addDitteCantiere(PNCDitteCantiere ditteCantiere) {
		if (this.ditteCantiere == null) {
			this.ditteCantiere = new ArrayList<PNCDitteCantiere>();
		}
		// add the association
		if(!this.ditteCantiere.contains(ditteCantiere)) {
			this.ditteCantiere.add(ditteCantiere);
			// make the inverse link
			ditteCantiere.setCantiere(this);
		}
	}

	public void removeDitteCantiere(PNCDitteCantiere ditteCantiere) {
		if (this.ditteCantiere == null) {
			this.ditteCantiere = new ArrayList<PNCDitteCantiere>();
			return;
		}
		//add the association
		if(this.ditteCantiere.contains(ditteCantiere)){
			this.ditteCantiere.remove(ditteCantiere);
			//make the inverse link
			ditteCantiere.setCantiere(null);
		}
	}
}
