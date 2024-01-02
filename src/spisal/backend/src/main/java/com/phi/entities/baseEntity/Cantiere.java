package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;

import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.CantieriAssociazioni;
import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "cantieri")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Cantiere extends BaseEntity {

	private static final long serialVersionUID = 829950207L;

	/**
	*  javadoc for pncState
	*/
	private Integer pncState;

	@Column(name="pnc_state")
	public Integer getPncState(){
		return pncState;
	}

	public void setPncState(Integer pncState){
		this.pncState = pncState;
	}

	/**
	*  javadoc for pncForced
	*/
	private Boolean pncForced;

	@Column(name="pnc_forced")
	public Boolean getPncForced(){
		return pncForced;
	}

	public void setPncForced(Boolean pncForced){
		this.pncForced = pncForced;
	}

	/**
	*  javadoc for idPnc
	*/
	private String idPnc;

	@Column(name="id_pnc")
	public String getIdPnc(){
		return idPnc;
	}

	public void setIdPnc(String idPnc){
		this.idPnc = idPnc;
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


	/**
	*  javadoc for cantieriAssociazioni
	*/
	private List<CantieriAssociazioni> cantieriAssociazioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<CantieriAssociazioni> getCantieriAssociazioni(){
		return cantieriAssociazioni;
	}

	public void setCantieriAssociazioni(List<CantieriAssociazioni> list){
		cantieriAssociazioni = list;
	}

	public void addCantieriAssociazioni(CantieriAssociazioni cantieriAssociazioni) {
		if (this.cantieriAssociazioni == null) {
			this.cantieriAssociazioni = new ArrayList<CantieriAssociazioni>();
		}
		// add the association
		if(!this.cantieriAssociazioni.contains(cantieriAssociazioni)) {
			this.cantieriAssociazioni.add(cantieriAssociazioni);
			// make the inverse link
			cantieriAssociazioni.setCantiere(this);
		}
	}

	public void removeCantieriAssociazioni(CantieriAssociazioni cantieriAssociazioni) {
		if (this.cantieriAssociazioni == null) {
			this.cantieriAssociazioni = new ArrayList<CantieriAssociazioni>();
			return;
		}
		//add the association
		if(this.cantieriAssociazioni.contains(cantieriAssociazioni)){
			this.cantieriAssociazioni.remove(cantieriAssociazioni);
			//make the inverse link
			cantieriAssociazioni.setCantiere(null);
		}

	}


	/**
	*  javadoc for titoloIV
	*/
	private Boolean titoloIV;

	@Column(name="titolo_iv")
	public Boolean getTitoloIV(){
		return titoloIV;
	}

	public void setTitoloIV(Boolean titoloIV){
		this.titoloIV = titoloIV;
	}

	/**
	*  javadoc for localita
	*/
	private String localita;

	@Column(name="localita")
	public String getLocalita(){
		return localita;
	}

	public void setLocalita(String localita){
		this.localita = localita;
	}

	/**
	*  javadoc for id
	*/
	private String id;

	@Column(name="id")
	public String getId(){
		return id;
	}

	public void setId(String id){
		this.id = id;
	}

	/**
	*  javadoc for lastVersion
	*/
	private Boolean lastVersion = true;

	@Column(name="last_version")
	public Boolean getLastVersion(){
		return lastVersion;
	}

	public void setLastVersion(Boolean lastVersion){
		this.lastVersion = lastVersion;
	}


	/**
	*  javadoc for original
	*/
	private Cantiere original;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="original_id")
	@ForeignKey(name="FK_Cantiere_original")
	//@Index(name="IX_Cantiere_original")
	public Cantiere getOriginal(){
		return original;
	}

	public void setOriginal(Cantiere original){
		this.original = original;
	}


	/**
	*  javadoc for longitudine
	*/
	private String longitudine;

	@Column(name="longitudine")
	public String getLongitudine(){
		return longitudine;
	}

	public void setLongitudine(String longitudine){
		this.longitudine = longitudine;
	}

	/**
	*  javadoc for lat
	*/
	private String latitudine;

	@Column(name="latitudine")
	public String getLatitudine(){
		return latitudine;
	}

	public void setLatitudine(String latitudine){
		this.latitudine = latitudine;
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
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="cantiere")
	public List<Procpratiche> getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(List<Procpratiche> list){
		procpratiche = list;
	}

	public void addProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
		}
		// add the association
		if(!this.procpratiche.contains(procpratiche)) {
			this.procpratiche.add(procpratiche);
			// make the inverse link
			if (procpratiche.getCantiere() == null || !procpratiche.getCantiere().contains(this))
				procpratiche.addCantiere(this);
		}
	}

	public void removeProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
			return;
		}
		//add the association
		if(this.procpratiche.contains(procpratiche)){
			this.procpratiche.remove(procpratiche);
			//make the inverse link
			if (procpratiche.getCantiere() != null && procpratiche.getCantiere().contains(this))
				procpratiche.removeCantiere(this);
		}

	}



	/**
	*  javadoc for tagCantiere
	*/
	private List<TagCantiere> tagCantiere;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<TagCantiere> getTagCantiere() {
		return tagCantiere;
	}

	public void setTagCantiere(List<TagCantiere>list){
		tagCantiere = list;
	}

	public void addTagCantiere(TagCantiere tagCantiere) {
		if (this.tagCantiere == null) {
			this.tagCantiere = new ArrayList<TagCantiere>();
		}
		// add the association
		if(!this.tagCantiere.contains(tagCantiere)) {
			this.tagCantiere.add(tagCantiere);
			// make the inverse link
			tagCantiere.setCantiere(this);
		}
	}

	public void removeTagCantiere(TagCantiere tagCantiere) {
		if (this.tagCantiere == null) {
			this.tagCantiere = new ArrayList<TagCantiere>();
			return;
		}
		//add the association
		if(this.tagCantiere.contains(tagCantiere)){
			this.tagCantiere.remove(tagCantiere);
			//make the inverse link
			tagCantiere.setCantiere(null);
		}
	}

	/**
	*  javadoc for ditteCantiere
	*/
	private List<DitteCantiere> ditteCantiere;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<DitteCantiere> getDitteCantiere() {
		return ditteCantiere;
	}

	public void setDitteCantiere(List<DitteCantiere>list){
		ditteCantiere = list;
	}

	public void addDitteCantiere(DitteCantiere ditteCantiere) {
		if (this.ditteCantiere == null) {
			this.ditteCantiere = new ArrayList<DitteCantiere>();
		}
		// add the association
		if(!this.ditteCantiere.contains(ditteCantiere)) {
			this.ditteCantiere.add(ditteCantiere);
			// make the inverse link
			ditteCantiere.setCantiere(this);
		}
	}

	public void removeDitteCantiere(DitteCantiere ditteCantiere) {
		if (this.ditteCantiere == null) {
			this.ditteCantiere = new ArrayList<DitteCantiere>();
			return;
		}
		//add the association
		if(this.ditteCantiere.contains(ditteCantiere)){
			this.ditteCantiere.remove(ditteCantiere);
			//make the inverse link
			ditteCantiere.setCantiere(null);
		}
	}


	/**
	*  javadoc for numeroAutonomi
	*/
	private Integer numeroAutonomi;

	@Column(name="numero_autonomi")
	public Integer getNumeroAutonomi(){
		return numeroAutonomi;
	}

	public void setNumeroAutonomi(Integer numeroAutonomi){
		this.numeroAutonomi = numeroAutonomi;
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
	*  javadoc for fineLavori
	*/
	private Date fineLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fine_lavori")
	public Date getFineLavori(){
		return fineLavori;
	}

	public void setFineLavori(Date fineLavori){
		this.fineLavori = fineLavori;
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
	*  javadoc for personeCantiere
	*/
	private List<PersoneCantiere> personeCantiere;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<PersoneCantiere> getPersoneCantiere() {
		return personeCantiere;
	}

	public void setPersoneCantiere(List<PersoneCantiere>list){
		personeCantiere = list;
	}

	public void addPersoneCantiere(PersoneCantiere personeCantiere) {
		if (this.personeCantiere == null) {
			this.personeCantiere = new ArrayList<PersoneCantiere>();
		}
		// add the association
		if(!this.personeCantiere.contains(personeCantiere)) {
			this.personeCantiere.add(personeCantiere);
			// make the inverse link
			personeCantiere.setCantiere(this);
		}
	}

	public void removePersoneCantiere(PersoneCantiere personeCantiere) {
		if (this.personeCantiere == null) {
			this.personeCantiere = new ArrayList<PersoneCantiere>();
			return;
		}
		//add the association
		if(this.personeCantiere.contains(personeCantiere)){
			this.personeCantiere.remove(personeCantiere);
			//make the inverse link
			personeCantiere.setCantiere(null);
		}
	}



	/**
	*  javadoc for committente
	*/
	private List<Committente> committente;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="cantiere", cascade=CascadeType.PERSIST)
	public List<Committente> getCommittente(){
		return committente;
	}

	public void setCommittente(List<Committente> list){
		committente = list;
	}

	public void addCommittente(Committente committente) {
		if (this.committente == null) {
			this.committente = new ArrayList<Committente>();
		}
		// add the association
		if(!this.committente.contains(committente)) {
			this.committente.add(committente);
			// make the inverse link
			committente.setCantiere(this);
		}
	}

	public void removeCommittente(Committente committente) {
		if (this.committente == null) {
			this.committente = new ArrayList<Committente>();
			return;
		}
		//add the association
		if(this.committente.contains(committente)){
			this.committente.remove(committente);
			//make the inverse link
			committente.setCantiere(null);
		}

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

//	/**
//	*  javadoc for name
//	*/
//	private String name;
//
//	@Column(name="name")
//	public String getName(){
//		return name;
//	}
//
//	public void setName(String name){
//		this.name = name;
//	}

	/**
	*  javadoc for mail
	*/
	private String mail;

	@Column(name="mail")
	public String getMail(){
		return mail;
	}

	public void setMail(String mail){
		this.mail = mail;
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
	*  javadoc for tipologiaOther
	*/
	private String tipologiaOther;

	@Column(name="tipologia_other")
	public String getTipologiaOther(){
		return tipologiaOther;
	}

	public void setTipologiaOther(String tipologiaOther){
		this.tipologiaOther = tipologiaOther;
	}

	/**
	*  javadoc for tipologiaNotifica
	*/
	private CodeValuePhi tipologiaNotifica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologiaNotifica")
	@ForeignKey(name="FK_Cantiere_typeN")
	//@Index(name="IX_Cantiere_typeN")
	public CodeValuePhi getTipologiaNotifica(){
		return tipologiaNotifica;
	}

	public void setTipologiaNotifica(CodeValuePhi tipologiaNotifica){
		this.tipologiaNotifica = tipologiaNotifica;
	}

	/**
	*  Notifica: default=SI; per inserimenti extra notifica, in cui solo alcuni campi sono obbligatori=NO
	*/
	private boolean notifica = true;

	@Column(name="notifica")
	public boolean getNotifica(){
		return notifica;
	}

	public void setNotifica(boolean notifica){
		this.notifica = notifica;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Cantiere_sequence")
	@SequenceGenerator(name = "Cantiere_sequence", sequenceName = "Cantiere_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
