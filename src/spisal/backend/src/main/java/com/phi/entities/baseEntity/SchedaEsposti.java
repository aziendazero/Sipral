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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueInail;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.baseEntity.EventoAccidentale;
import com.phi.entities.baseEntity.Protocollo;

@javax.persistence.Entity
@Table(name = "scheda_esposti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SchedaEsposti extends BaseEntity implements LocatedEntity{

	private static final long serialVersionUID = 1379654204L;


	/**
	*  javadoc for protocollo
	*/
	private List<Protocollo> protocollo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="schedaEsposti", cascade=CascadeType.PERSIST)
	public List<Protocollo> getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(List<Protocollo>list){
		protocollo = list;
	}

	public void addProtocollo(Protocollo protocollo) {
		if (this.protocollo == null) {
			this.protocollo = new ArrayList<Protocollo>();
		}
		// add the association
		if(!this.protocollo.contains(protocollo)) {
			this.protocollo.add(protocollo);
			// make the inverse link
			protocollo.setSchedaEsposti(this);
		}
	}

	public void removeProtocollo(Protocollo protocollo) {
		if (this.protocollo == null) {
			this.protocollo = new ArrayList<Protocollo>();
			return;
		}
		//add the association
		if(this.protocollo.contains(protocollo)){
			this.protocollo.remove(protocollo);
			//make the inverse link
			protocollo.setSchedaEsposti(null);
		}
	}



	/**
	*  javadoc for eventoAccidentale
	*/
	private List<EventoAccidentale> eventoAccidentale;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="schedaEsposti", cascade=CascadeType.PERSIST)
	public List<EventoAccidentale> getEventoAccidentale(){
		return eventoAccidentale;
	}

	public void setEventoAccidentale(List<EventoAccidentale> list){
		eventoAccidentale = list;
	}

	public void addEventoAccidentale(EventoAccidentale eventoAccidentale) {
		if (this.eventoAccidentale == null) {
			this.eventoAccidentale = new ArrayList<EventoAccidentale>();
		}
		// add the association
		if(!this.eventoAccidentale.contains(eventoAccidentale)) {
			this.eventoAccidentale.add(eventoAccidentale);
			// make the inverse link
			eventoAccidentale.setSchedaEsposti(this);
		}
	}

	public void removeEventoAccidentale(EventoAccidentale eventoAccidentale) {
		if (this.eventoAccidentale == null) {
			this.eventoAccidentale = new ArrayList<EventoAccidentale>();
			return;
		}
		//add the association
		if(this.eventoAccidentale.contains(eventoAccidentale)){
			this.eventoAccidentale.remove(eventoAccidentale);
			//make the inverse link
			eventoAccidentale.setSchedaEsposti(null);
		}

	}


	/**
	*  javadoc for contact2
	*/
	private String contact2;

	@Column(name="contact2")
	public String getContact2(){
		return contact2;
	}

	public void setContact2(String contact2){
		this.contact2 = contact2;
	}

	/**
	*  javadoc for contact1
	*/
	private String contact1;

	@Column(name="contact1")
	public String getContact1(){
		return contact1;
	}

	public void setContact1(String contact1){
		this.contact1 = contact1;
	}

	/**
	*  javadoc for notInAll5
	*/
	private String notInAll5;

	@Column(name="not_in_all5")
	public String getNotInAll5(){
		return notInAll5;
	}

	public void setNotInAll5(String notInAll5){
		this.notInAll5 = notInAll5;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_SchedaEsposti_tipologia")
	//@Index(name="IX_SchedaEsposti_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
	}

	/**
	*  javadoc for endValidity
	*/
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	*  javadoc for totAmm
	*/
	private Integer totAmm;

	@Column(name="tot_amm")
	public Integer getTotAmm(){
		return totAmm;
	}

	public void setTotAmm(Integer totAmm){
		this.totAmm = totAmm;
	}

	/**
	*  javadoc for totProd
	*/
	private Integer totProd;

	@Column(name="tot_prod")
	public Integer getTotProd(){
		return totProd;
	}

	public void setTotProd(Integer totProd){
		this.totProd = totProd;
	}

	/**
	*  javadoc for espDonne
	*/
	private Integer espDonne;

	@Column(name="esp_donne")
	public Integer getEspDonne(){
		return espDonne;
	}

	public void setEspDonne(Integer espDonne){
		this.espDonne = espDonne;
	}

	/**
	*  javadoc for totDonne
	*/
	private Integer totDonne;

	@Column(name="tot_donne")
	public Integer getTotDonne(){
		return totDonne;
	}

	public void setTotDonne(Integer totDonne){
		this.totDonne = totDonne;
	}

	/**
	*  javadoc for espUomini
	*/
	private Integer espUomini;

	@Column(name="esp_uomini")
	public Integer getEspUomini(){
		return espUomini;
	}

	public void setEspUomini(Integer espUomini){
		this.espUomini = espUomini;
	}

	/**
	*  javadoc for totUomini
	*/
	private Integer totUomini;

	@Column(name="tot_uomini")
	public Integer getTotUomini(){
		return totUomini;
	}

	public void setTotUomini(Integer totUomini){
		this.totUomini = totUomini;
	}

	/**
	*  javadoc for cancerogeno
	*/
	private List<CodeValuePhi> cancerogeno;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name="schedaesp_cancerogeno", joinColumns = { @JoinColumn(name="scheda_id") }, inverseJoinColumns = { @JoinColumn(name="cancerogeno") })
	@ForeignKey(name="FK_cancer_schedaEsp", inverseName="FK_schedaEsp_cancer")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getCancerogeno(){
		return cancerogeno;
	}

	public void setCancerogeno(List<CodeValuePhi> cancerogeno){
		this.cancerogeno = cancerogeno;
	}

	/**
	*  javadoc for inail
	*/
	private CodeValueInail inail;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="inail")
	@ForeignKey(name="FK_schedaEsp_inail")
	//@Index(name="IX_schedaEsp_inail")
	public CodeValueInail getInail(){
		return inail;
	}

	public void setInail(CodeValueInail inail){
		this.inail = inail;
	}

	/**
	*  javadoc for lavUnica
	*/
	private CodeValueAteco lavUnica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lavUnica")
	@ForeignKey(name="FK_schedaEsp_lavUnica")
	//@Index(name="IX_schedaEsp_lavUnica")
	public CodeValueAteco getLavUnica(){
		return lavUnica;
	}

	public void setLavUnica(CodeValueAteco lavUnica){
		this.lavUnica = lavUnica;
	}

	/**
	*  javadoc for dataCompilazione
	*/
	private Date dataCompilazione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_compilazione")
	public Date getDataCompilazione(){
		return dataCompilazione;
	}

	public void setDataCompilazione(Date dataCompilazione){
		this.dataCompilazione = dataCompilazione;
	}

	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_schedaEsp_sdloc")
	//@Index(name="IX_schedaEsp_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	protected Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_schedaEsp_author")
	//@Index(name="IX_schedaEsp_author")
	public Employee getAuthor() {
	    return author;
	}

	public void setAuthor(Employee param) {
	    this.author = param;
	}
	
	/**
	*  javadoc for lavorazioniCorrelate
	*/
	private List<LavorazioniCorrelate> lavorazioniCorrelate;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="schedaEsposti", cascade=CascadeType.PERSIST)
	public List<LavorazioniCorrelate> getLavorazioniCorrelate(){
		return lavorazioniCorrelate;
	}

	public void setLavorazioniCorrelate(List<LavorazioniCorrelate> list){
		lavorazioniCorrelate = list;
	}

	public void addLavorazioniCorrelate(LavorazioniCorrelate lavorazioniCorrelate) {
		if (this.lavorazioniCorrelate == null) {
			this.lavorazioniCorrelate = new ArrayList<LavorazioniCorrelate>();
		}
		// add the association
		if(!this.lavorazioniCorrelate.contains(lavorazioniCorrelate)) {
			this.lavorazioniCorrelate.add(lavorazioniCorrelate);
			// make the inverse link
			lavorazioniCorrelate.setSchedaEsposti(this);
		}
	}

	public void removeLavorazioniCorrelate(LavorazioniCorrelate lavorazioniCorrelate) {
		if (this.lavorazioniCorrelate == null) {
			this.lavorazioniCorrelate = new ArrayList<LavorazioniCorrelate>();
			return;
		}
		//add the association
		if(this.lavorazioniCorrelate.contains(lavorazioniCorrelate)){
			this.lavorazioniCorrelate.remove(lavorazioniCorrelate);
			//make the inverse link
			lavorazioniCorrelate.setSchedaEsposti(null);
		}

	}



	/**
	*  javadoc for sostanze
	*/
	private List<Sostanze> sostanze;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="schedaEsposti", cascade=CascadeType.PERSIST)
	public List<Sostanze> getSostanze(){
		return sostanze;
	}

	public void setSostanze(List<Sostanze> list){
		sostanze = list;
	}

	public void addSostanze(Sostanze sostanze) {
		if (this.sostanze == null) {
			this.sostanze = new ArrayList<Sostanze>();
		}
		// add the association
		if(!this.sostanze.contains(sostanze)) {
			this.sostanze.add(sostanze);
			// make the inverse link
			sostanze.setSchedaEsposti(this);
		}
	}

	public void removeSostanze(Sostanze sostanze) {
		if (this.sostanze == null) {
			this.sostanze = new ArrayList<Sostanze>();
			return;
		}
		//add the association
		if(this.sostanze.contains(sostanze)){
			this.sostanze.remove(sostanze);
			//make the inverse link
			sostanze.setSchedaEsposti(null);
		}

	}



	/**
	*  javadoc for esposti
	*/
	private List<Esposti> esposti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="schedaEsposti", cascade=CascadeType.PERSIST)
	public List<Esposti> getEsposti(){
		return esposti;
	}

	public void setEsposti(List<Esposti> list){
		esposti = list;
	}

	public void addEsposti(Esposti esposti) {
		if (this.esposti == null) {
			this.esposti = new ArrayList<Esposti>();
		}
		// add the association
		if(!this.esposti.contains(esposti)) {
			this.esposti.add(esposti);
			// make the inverse link
			esposti.setSchedaEsposti(this);
		}
	}

	public void removeEsposti(Esposti esposti) {
		if (this.esposti == null) {
			this.esposti = new ArrayList<Esposti>();
			return;
		}
		//add the association
		if(this.esposti.contains(esposti)){
			this.esposti.remove(esposti);
			//make the inverse link
			esposti.setSchedaEsposti(null);
		}

	}



	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_schedaEsp_ditte")
	//@Index(name="IX_schedaEsp_ditte")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}



	/**
	*  javadoc for sedi
	*/
	private Sedi sedi;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_id")
	@ForeignKey(name="FK_schedaEsp_sedi")
	//@Index(name="IX_schedaEsp_sedi")
	public Sedi getSedi(){
		return sedi;
	}

	public void setSedi(Sedi sedi){
		this.sedi = sedi;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SchedaEsposti_sequence")
	@SequenceGenerator(name = "SchedaEsposti_sequence", sequenceName = "SchedaEsposti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
