package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.actions.PersonaGiuridicaSedeAction;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
@Entity
@Table(name = "persone_giuridiche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PersoneGiuridiche extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7094423414800539012L;

	/**
	*  javadoc for deletable
	*/
	private Boolean deletable;

	@Column(name="deletable")
	public Boolean getDeletable(){
		return deletable;
	}

	public void setDeletable(Boolean deletable){
		this.deletable = deletable;
	}

	/**
	*  javadoc for codiceDitta
	*/
	private String codiceDitta;

	@Column(name="codice_ditta")
	public String getCodiceDitta(){
		return codiceDitta;
	}

	public void setCodiceDitta(String codiceDitta){
		this.codiceDitta = codiceDitta;
	}


	/**
	*  javadoc for saEsterne
	*/
	private List<Sedi> saEsterne;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="pgEsterne", cascade=CascadeType.PERSIST)
	public List<Sedi> getSaEsterne(){
		return saEsterne;
	}

	public void setSaEsterne(List<Sedi> list){
		saEsterne = list;
	}

	public void addSaEsterne(Sedi saEsterne) {
		if (this.saEsterne == null) {
			this.saEsterne = new ArrayList<Sedi>();
		}
		// add the association
		if(!this.saEsterne.contains(saEsterne)) {
			this.saEsterne.add(saEsterne);
			// make the inverse link
			if (saEsterne.getPgEsterne() == null || !saEsterne.getPgEsterne().contains(this))
				saEsterne.addPgEsterne(this);
		}
	}

	public void removeSaEsterne(Sedi saEsterne) {
		if (this.saEsterne == null) {
			this.saEsterne = new ArrayList<Sedi>();
			return;
		}
		//add the association
		if(this.saEsterne.contains(saEsterne)){
			this.saEsterne.remove(saEsterne);
			//make the inverse link
			if (saEsterne.getPgEsterne() != null && saEsterne.getPgEsterne().contains(this))
				saEsterne.removePgEsterne(this);
		}

	}


	/**
	*  javadoc for dataCancellazioneRI
	*/
	private Date dataCancellazioneRI;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_canc_ri")
	public Date getDataCancellazioneRI(){
		return dataCancellazioneRI;
	}

	public void setDataCancellazioneRI(Date dataCancellazioneRI){
		this.dataCancellazioneRI = dataCancellazioneRI;
	}


	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="PersoneGiuridiche_id")
	@ForeignKey(name="FK_dcumenti_PersneGiridiche")
	// @Index(name="IX_dcumenti_PersneGiridiche")
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
	*  javadoc for ditteAssociazioni
	*/
	private List<DitteAssociazioni> ditteAssociazioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="personeGiuridiche", cascade=CascadeType.PERSIST)
	public List<DitteAssociazioni> getDitteAssociazioni(){
		return ditteAssociazioni;
	}

	public void setDitteAssociazioni(List<DitteAssociazioni> list){
		ditteAssociazioni = list;
	}

	public void addDitteAssociazioni(DitteAssociazioni ditteAssociazioni) {
		if (this.ditteAssociazioni == null) {
			this.ditteAssociazioni = new ArrayList<DitteAssociazioni>();
		}
		// add the association
		if(!this.ditteAssociazioni.contains(ditteAssociazioni)) {
			this.ditteAssociazioni.add(ditteAssociazioni);
			// make the inverse link
			ditteAssociazioni.setPersoneGiuridiche(this);
		}
	}

	public void removeDitteAssociazioni(DitteAssociazioni ditteAssociazioni) {
		if (this.ditteAssociazioni == null) {
			this.ditteAssociazioni = new ArrayList<DitteAssociazioni>();
			return;
		}
		//add the association
		if(this.ditteAssociazioni.contains(ditteAssociazioni)){
			this.ditteAssociazioni.remove(ditteAssociazioni);
			//make the inverse link
			ditteAssociazioni.setPersoneGiuridiche(null);
		}

	}




	/**
	*  javadoc for ditteProvvedimenti
	*/
	private List<DitteProvvedimenti> ditteProvvedimenti;

	@NotAudited
	@OneToMany(fetch=FetchType.LAZY, mappedBy="ditte")
	public List<DitteProvvedimenti> getDitteProvvedimenti(){
		return ditteProvvedimenti;
	}

	public void setDitteProvvedimenti(List<DitteProvvedimenti> list){
		ditteProvvedimenti = list;
	}

	/**
	*  javadoc for dittePratiche
	*/
	private List<DittePratiche> dittePratiche;

	@NotAudited
	@OneToMany(fetch=FetchType.LAZY, mappedBy="ditte")
	public List<DittePratiche> getDittePratiche(){
		return dittePratiche;
	}

	public void setDittePratiche(List<DittePratiche> list){
		dittePratiche = list;
	}
	

	/**
	*  javadoc for app
	*/
	private String app;

	@Column(name="app")
	public String getApp(){
		return app;
	}

	public void setApp(String app){
		this.app = app;
	}


	/**
	*  javadoc for sediAddebito
	*/
//	private List<SediAddebito> sediAddebito;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="personaGiuridica", cascade=CascadeType.PERSIST)
//	public List<SediAddebito> getSediAddebito() {
//		return sediAddebito;
//	}
//
//	public void setSediAddebito(List<SediAddebito>list){
//		sediAddebito = list;
//	}
//
//	public void addSediAddebito(SediAddebito sediAddebito) {
//		if (this.sediAddebito == null) {
//			this.sediAddebito = new ArrayList<SediAddebito>();
//		}
//		// add the association
//		if(!this.sediAddebito.contains(sediAddebito)) {
//			this.sediAddebito.add(sediAddebito);
//			// make the inverse link
//			sediAddebito.setPersonaGiuridica(this);
//		}
//	}
//
//	public void removeSediAddebito(SediAddebito sediAddebito) {
//		if (this.sediAddebito == null) {
//			this.sediAddebito = new ArrayList<SediAddebito>();
//			return;
//		}
//		//add the association
//		if(this.sediAddebito.contains(sediAddebito)){
//			this.sediAddebito.remove(sediAddebito);
//			//make the inverse link
//			sediAddebito.setPersonaGiuridica(null);
//		}
//	}


	/**
	*  javadoc for numProvvedimenti
	*/
	private Integer numProvvedimenti;

	@Column(name="num_provvedimenti")
	public Integer getNumProvvedimenti(){
		return numProvvedimenti;
	}

	public void setNumProvvedimenti(Integer numProvvedimenti){
		this.numProvvedimenti = numProvvedimenti;
	}

	/**
	*  javadoc for numPratiche
	*/
	private Integer numPratiche;

	@Column(name="num_pratiche")
	public Integer getNumPratiche(){
		return numPratiche;
	}

	public void setNumPratiche(Integer numPratiche){
		this.numPratiche = numPratiche;
	}

	/**
	*  javadoc for praticheCount
	*/
	private Integer praticheCount;

	@Column(name="pratiche_count")
	public Integer getPraticheCount(){
		return praticheCount;
	}

	public void setPraticheCount(Integer praticheCount){
		this.praticheCount = praticheCount;
	}

	/**
	*  javadoc for attivitaIstat
	*/
	private List<AttivitaIstat> attivitaIstat;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="personeGiuridiche")
	public List<AttivitaIstat> getAttivitaIstat(){
		return attivitaIstat;
	}

	public void setAttivitaIstat(List<AttivitaIstat> list){
		attivitaIstat = list;
	}

	public void addAttivitaIstat(AttivitaIstat attivitaIstat) {
		if (this.attivitaIstat == null) {
			this.attivitaIstat = new ArrayList<AttivitaIstat>();
		}
		// add the association
		if(!this.attivitaIstat.contains(attivitaIstat)) {
			this.attivitaIstat.add(attivitaIstat);
			// make the inverse link
			attivitaIstat.setPersoneGiuridiche(this);
		}
	}

	public void removeAttivitaIstat(AttivitaIstat attivitaIstat) {
		if (this.attivitaIstat == null) {
			this.attivitaIstat = new ArrayList<AttivitaIstat>();
			return;
		}
		//add the association
		if(this.attivitaIstat.contains(attivitaIstat)){
			this.attivitaIstat.remove(attivitaIstat);
			//make the inverse link
			attivitaIstat.setPersoneGiuridiche(null);
		}

	}



	/**
	*  javadoc for protocollo
	*/
	private List<Protocollo> protocollo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="richiedenteDitta", cascade=CascadeType.PERSIST)
	public List<Protocollo> getProtocollo(){
		return protocollo;
	}

	public void setProtocollo(List<Protocollo> list){
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
			protocollo.setRichiedenteDitta(this);
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
			protocollo.setRichiedenteDitta(null);
		}

	}




	/**
	*  javadoc for ente
	*/
	private Boolean ente = false;

	@Column(name="ente")
	public Boolean getEnte(){
		return ente;
	}

	public void setEnte(Boolean ente){
		this.ente = ente;	
	}

	/**
	*  javadoc for internalId
	*/
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Persgiur_sequence")
	@SequenceGenerator(name = "Persgiur_sequence", sequenceName = "Persgiur_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for sedi
	*/
	private List<Sedi> sedi;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="personaGiuridica")
	@Cascade({org.hibernate.annotations.CascadeType.ALL})
	public List<Sedi> getSedi() {
		return sedi;
	}

	public void setSedi(List<Sedi>list){
		sedi = list;
	}

	public void addSedi(Sedi sedi) {
		if (this.sedi == null) {
			this.sedi = new ArrayList<Sedi>();
		}
		// add the association
		if(!this.sedi.contains(sedi)) {
			this.sedi.add(sedi);
			// make the inverse link
			sedi.setPersonaGiuridica(this);
		}
	}

	public void removeSedi(Sedi sedi) {
		if (this.sedi == null) {
			this.sedi = new ArrayList<Sedi>();
			return;
		}
		//add the association
		if(this.sedi.contains(sedi)){
			this.sedi.remove(sedi);
			//make the inverse link
			sedi.setPersonaGiuridica(null);
		}
	}
	
	/**
	 * Used in reports to show addr
	 */
	@Transient
	public Sedi getSedePrincipale(){
		if (sedi != null) {
			for (Sedi sede : sedi) {
				if (sede.getSedePrincipale() && sede.getIsActive()){
					return sede;
				}
			}
		}
		return null;
	}

	/**
	*  javadoc for denominazione
	*/
	private String denominazione;

	@Column(name="DENOMINAZIONE")
	public String getDenominazione(){
		return denominazione;
	}

	public void setDenominazione(String denominazione){
		this.denominazione = denominazione;
	}
	
	/**
	*  Codice Fiscale
	*/
	private String codiceFiscale;

	@Column(name="CODICE_FISCALE")
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	
	/**
	*  Partita IVA
	*/
	private String patritaIva;

	@Column(name="PARTITA_IVA")
	public String getPatritaIva() {
		return patritaIva;
	}

	public void setPatritaIva(String patritaIva) {
		this.patritaIva = patritaIva;
	}
	
	/**
	*  Forma Giuridica
	*/
	private CodeValue formaGiuridica;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="FORMA_GIURIDICA")
	@ForeignKey(name="FK_Form_giur")
	//@Index(name="IX_Form_giur")
	public CodeValue getFormaGiuridica() {
		return formaGiuridica;
	}

	public void setFormaGiuridica(CodeValue formaGiuridica) {
		this.formaGiuridica = formaGiuridica;
	}
	
	/**
	*  Sezioni
	*/
	private List<CodeValuePhi> sezioni;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "sezioni_iscrizione", joinColumns = { @JoinColumn(name = "persone_giuridiche_id") }, inverseJoinColumns = { @JoinColumn(name = "code_value_id") })
	@ForeignKey(name = "FK_PgId_CvId", inverseName = "FK_CvId_PgId")
	public List<CodeValuePhi> getSezioni(){
		return sezioni;
	}

	public void setSezioni(List<CodeValuePhi> sezioni){
		this.sezioni = sezioni;
	}
	 
	/**
	*  Numero RI (Registro delle Imprese)
	*/
	private String numeroRI;

	@Column(name="NUMERO_RI")
	public String getNumeroRI() {
		return numeroRI;
	}

	public void setNumeroRI(String numeroRI) {
		this.numeroRI = numeroRI;
	}
	
	/**
	*  Data iscrizione RI (Registro delle Imprese)
	*/
	private Date dataIscrizioneRI;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_ISCRIZIONE_RI")
	public Date getDataIscrizioneRI() {
		return dataIscrizioneRI;
	}

	public void setDataIscrizioneRI(Date dataIscrizioneRI) {
		this.dataIscrizioneRI = dataIscrizioneRI;
	}
	
	/**
	*  Data costituzione
	*/
	private Date dataCostituzione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_COSTITUZIONE")
	public Date getDataCostituzione() {
		return dataCostituzione;
	}

	public void setDataCostituzione(Date dataCostituzione) {
		this.dataCostituzione = dataCostituzione;
	}
	
	/**
	*  Data termine)
	*/
	private Date dataTermine;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DATA_TERMINE")
	public Date getDataTermine() {
		return dataTermine;
	}

	public void setDataTermine(Date dataTermine) {
		this.dataTermine = dataTermine;
	}
	
	@Transient
	public String getPivaOrCf(){
		
		String ret = "";
		if(patritaIva!=null){
			ret+="P.IVA: "+patritaIva;
		}else if(codiceFiscale!=null){
			ret+="C.F.: "+codiceFiscale;
		}
		return ret;
		
	}
	
	public String toString() {
		return "Persona giuridica " + internalId;
	}
	
}
