package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.phi.entities.actions.OperatoreAction;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
import com.phi.entities.role.ServiceDeliveryLocation;

import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.MedicinaLavoro;
@Entity
@Table(name = "protocollo")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Protocollo extends BaseEntity implements LocatedEntity, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5544039064991246000L;


	/**
	*  javadoc for medicinaLavoro
	*/
	private MedicinaLavoro medicinaLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="medicina_lavoro_id")
	@ForeignKey(name="FK_Protocollo_medicinaLavoro")
	@Index(name="IX_Protocollo_medicinaLavoro")
	public MedicinaLavoro getMedicinaLavoro(){
		return medicinaLavoro;
	}

	public void setMedicinaLavoro(MedicinaLavoro medicinaLavoro){
		this.medicinaLavoro = medicinaLavoro;
	}


	/**
	*  javadoc for ricorsoDa
	*/
	private CodeValuePhi ricorsoDa;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ricorsoDa")
	@ForeignKey(name="FK_Protocollo_ricorsoDa")
	@Index(name="IX_Protocollo_ricorsoDa")
	public CodeValuePhi getRicorsoDa(){
		return ricorsoDa;
	}

	public void setRicorsoDa(CodeValuePhi ricorsoDa){
		this.ricorsoDa = ricorsoDa;
	}

	/**
	*  javadoc for tipoExEsposto
	*/
	private CodeValuePhi tipoExEsposto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoExEsposto")
	@ForeignKey(name="FK_Protocollo_tipoExEsposto")
	//@Index(name="IX_Protocollo_tipoExEsposto")
	public CodeValuePhi getTipoExEsposto(){
		return tipoExEsposto;
	}

	public void setTipoExEsposto(CodeValuePhi tipoExEsposto){
		this.tipoExEsposto = tipoExEsposto;
	}

	
	/**
	*  javadoc for sedeAttualeUtente
	*/
	private Sedi sedeAttualeUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_attuale_utente_id")
	@ForeignKey(name="FK_Prtcll_sdeAttualeUtente")
	//@Index(name="IX_Prtcll_sdeAttualeUtente")
	public Sedi getSedeAttualeUtente() {
		return sedeAttualeUtente;
	}

	public void setSedeAttualeUtente(Sedi sedeAttualeUtente){
		this.sedeAttualeUtente = sedeAttualeUtente;
	}

	/**
	*  javadoc for dittaAttualeUtente
	*/
	private PersoneGiuridiche dittaAttualeUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_attuale_utente_id")
	@ForeignKey(name="FK_Prtcll_dttaAttualeUtente")
	//@Index(name="IX_Prtcll_dttaAttualeUtente")
	public PersoneGiuridiche getDittaAttualeUtente(){
		return dittaAttualeUtente;
	}

	public void setDittaAttualeUtente(PersoneGiuridiche dittaAttualeUtente){
		this.dittaAttualeUtente = dittaAttualeUtente;
	}

	/**
	*  javadoc for dataNotificaGiudizio
	*/
	private Date dataNotificaGiudizio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_notifica_giudizio")
	public Date getDataNotificaGiudizio(){
		return dataNotificaGiudizio;
	}

	public void setDataNotificaGiudizio(Date dataNotificaGiudizio){
		this.dataNotificaGiudizio = dataNotificaGiudizio;
	}

	/**
	*  javadoc for dataGiudizio
	*/
	private Date dataGiudizio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_giudizio")
	public Date getDataGiudizio(){
		return dataGiudizio;
	}

	public void setDataGiudizio(Date dataGiudizio){
		this.dataGiudizio = dataGiudizio;
	}

	/**
	*  javadoc for ubicazioneLocalita
	*/
	private String ubicazioneLocalita;

	@Column(name="ubicazione_localita")
	public String getUbicazioneLocalita(){
		return ubicazioneLocalita;
	}

	public void setUbicazioneLocalita(String ubicazioneLocalita){
		this.ubicazioneLocalita = ubicazioneLocalita;
	}

	/**
	*  javadoc for ubicazioneUbicazione
	*/
	private String ubicazioneUbicazione;

	@Column(name="ubicazione_ubicazione")
	public String getUbicazioneUbicazione(){
		return ubicazioneUbicazione;
	}

	public void setUbicazioneUbicazione(String ubicazioneUbicazione){
		this.ubicazioneUbicazione = ubicazioneUbicazione;
	}

	/**
	*  javadoc for riferimentoUbicazione
	*/
	private String riferimentoUbicazione;

	@Column(name="riferimento_ubicazione")
	public String getRiferimentoUbicazione(){
		return riferimentoUbicazione;
	}

	public void setRiferimentoUbicazione(String riferimentoUbicazione){
		this.riferimentoUbicazione = riferimentoUbicazione;
	}

	/**
	*  javadoc for dataScadenzaNote
	*/
	private String dataScadenzaNote;

	@Column(name="data_scadenza_note")
	public String getDataScadenzaNote(){
		return dataScadenzaNote;
	}

	public void setDataScadenzaNote(String dataScadenzaNote){
		this.dataScadenzaNote = dataScadenzaNote;
	}

	/**
	*  javadoc for dataScadenza
	*/
	private Date dataScadenza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_scadenza")
	public Date getDataScadenza(){
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza){
		this.dataScadenza = dataScadenza;
	}

	/**
	*  javadoc for dataProtocollo
	*/
	private Date dataProtocollo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_protocollo")
	public Date getDataProtocollo(){
		return dataProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo){
		this.dataProtocollo = dataProtocollo;
	}

	/**
	*  javadoc for procpraticheMulti
	*/
	private List<Procpratiche> procpraticheMulti;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	public List<Procpratiche> getProcpraticheMulti() {
		return procpraticheMulti;
	}

	public void setProcpraticheMulti(List<Procpratiche>list){
		procpraticheMulti = list;
	}

	public void addProcpraticheMulti(Procpratiche procpraticheMulti) {
		if (this.procpraticheMulti == null) {
			this.procpraticheMulti = new ArrayList<Procpratiche>();
		}
		// add the association
		if(!this.procpraticheMulti.contains(procpraticheMulti)) {
			this.procpraticheMulti.add(procpraticheMulti);
			// make the inverse link
			if (procpraticheMulti.getProtocolloMulti() == null || !procpraticheMulti.getProtocolloMulti().contains(this))
				procpraticheMulti.addProtocolloMulti(this);
		}
	}

	public void removeProcpraticheMulti(Procpratiche procpraticheMulti) {
		if (this.procpraticheMulti == null) {
			this.procpraticheMulti = new ArrayList<Procpratiche>();
			return;
		}
		//add the association
		if(this.procpraticheMulti.contains(procpraticheMulti)){
			this.procpraticheMulti.remove(procpraticheMulti);
			//make the inverse link
			if (procpraticheMulti.getProtocolloMulti() != null && procpraticheMulti.getProtocolloMulti().contains(this))
			procpraticheMulti.removeProtocolloMulti(this);
		}
	}



	/**
	*  javadoc for schedaEsposti
	*/
	private SchedaEsposti schedaEsposti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="scheda_esposti_id")
	@ForeignKey(name="FK_Protocollo_schedaEsposti")
	//@Index(name="IX_Protocollo_schedaEsposti")
	public SchedaEsposti getSchedaEsposti(){
		return schedaEsposti;
	}

	public void setSchedaEsposti(SchedaEsposti schedaEsposti){
		this.schedaEsposti = schedaEsposti;
	}


	/**
	*  javadoc for urgente
	*/
	private Boolean urgente;

	@Column(name="urgente")
	public Boolean getUrgente(){
		return urgente;
	}

	public void setUrgente(Boolean urgente){
		this.urgente = urgente;
	}

	/**
	*  javadoc for richiedenteDitta
	*/
	private PersoneGiuridiche richiedenteDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_ditta_id")
	@ForeignKey(name="FK_Protcll_ricDitta")
	//@Index(name="IX_Protcll_ricDitta")
	public PersoneGiuridiche getRichiedenteDitta(){
		return richiedenteDitta;
	}

	public void setRichiedenteDitta(PersoneGiuridiche richiedenteDitta){
		this.richiedenteDitta = richiedenteDitta;
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
	*  javadoc for malattiaProfessionale
	*/
	private MalattiaProfessionale malattiaProfessionale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="malattia_professionale_id")
	@ForeignKey(name="FK_Prtcll_malattiPrfessinle")
	//@Index(name="IX_Prtcll_malattiPrfessinle")
	public MalattiaProfessionale getMalattiaProfessionale(){
		return malattiaProfessionale;
	}

	public void setMalattiaProfessionale(MalattiaProfessionale malattiaProfessionale){
		this.malattiaProfessionale = malattiaProfessionale;
	}

	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="Protocollo_id")
	@ForeignKey(name="FK_documenti_Protocollo")
	//@Index(name="IX_documenti_Protocollo")
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
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_Protocollo_sdloc")
	//@Index(name="IX_Protocollo_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	/**
	*  javadoc for UOS
	*/
	private ServiceDeliveryLocation uos;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="uos")
	@ForeignKey(name="FK_Protocollo_uos")
	//@Index(name="IX_Protocollo_sdloc")
	public ServiceDeliveryLocation getUos(){
		return uos;
	}

	public void setUos(ServiceDeliveryLocation uos){
		this.uos = uos;
	}
	
	private Date dataASL;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_ASL", length = 7)
	public Date getDataASL() {
		return this.dataASL;
	}

	public void setDataASL(Date dataASL) {
		this.dataASL = dataASL;
	}
	
	private BigDecimal nprotocollo;

	@Column(name = "NPROTOCOLLO", precision = 22, scale = 0)
	public BigDecimal getNprotocollo() {
		return this.nprotocollo;
	}

	public void setNprotocollo(BigDecimal nprotocollo) {
		this.nprotocollo = nprotocollo;
	}
	
	private BigDecimal annoProtocollo;

	@Column(name = "anno_protocollo", precision = 22, scale = 0)
	public BigDecimal getAnnoProtocollo() {
		return this.annoProtocollo;
	}

	public void setAnnoProtocollo(BigDecimal annoProtocollo) {
		this.annoProtocollo = annoProtocollo;
	}
	
	private BigDecimal nrichiesta;

	@Column(name = "NRICHIESTA", precision = 22, scale = 0)
	public BigDecimal getNrichiesta() {
		return this.nrichiesta;
	}

	public void setNrichiesta(BigDecimal nrichiesta) {
		this.nrichiesta = nrichiesta;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Prot_code")
	//@Index(name="IX_Prot_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueStatus.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Prot_sc")
	//@Index(name="IX_Prot_sc") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}
	
	private String note;

	@Column(name = "NOTE", length=4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	*  javadoc for reference
	*/
	private String reference;

	@Column(name="reference")
	public String getReference(){
		return reference;
	}

	public void setReference(String reference){
		this.reference = reference;
	}
	
	/**
	*  javadoc for isMaster
	*/
	private boolean isMaster = false;

	@Column(name="is_master")
	public boolean getIsMaster(){
		return isMaster;
	}

	public void setIsMaster(boolean isMaster){
		this.isMaster = isMaster;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Protocollo_sequence")
	@SequenceGenerator(name = "Protocollo_sequence", sequenceName = "Protocollo_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue applicant;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="applicant")
	@ForeignKey(name="FK_Prot_appl")
	//@Index(name="IX_Prot_appl")
	public CodeValue getApplicant() {
		return applicant;
	}

	public void setApplicant(CodeValue applicant) {
		this.applicant = applicant;
	}
	
	protected Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Prot_author")
	//@Index(name="IX_Prot_author")
	public Employee getAuthor() {
	    return author;
	}

	public void setAuthor(Employee param) {
	    this.author = param;
	}

	private Procpratiche procpratiche;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IDPRATICAASS")
	public Procpratiche getProcpratiche() {
		return this.procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche) {
		this.procpratiche = procpratiche;
	}

	/**
	*  javadoc for infortunio
	*/
	private Infortuni infortunio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="infortunio_id")
	@ForeignKey(name="FK_Protocollo_infortunio")
	//@Index(name="IX_Protocollo_infortunio")
	public Infortuni getInfortunio(){
		return infortunio;
	}

	public void setInfortunio(Infortuni infortunio){
		this.infortunio = infortunio;
	}

	private CodeValue richiedente;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="richiedente")
	@ForeignKey(name="FK_Prot_richiedente")
	//@Index(name="IX_Prot_richiedente")
	public CodeValue getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(CodeValue richiedente) {
		
		this.richiedente = richiedente;
	}

	/**
	*  javadoc for richiedenteMedico
	*/
	private Physician richiedenteMedico;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_medico_id")
	@ForeignKey(name="FK_Protcll_ricMedico")
	//@Index(name="IX_Protcll_ricMedico")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Physician getRichiedenteMedico(){
		return richiedenteMedico;
	}

	public void setRichiedenteMedico(Physician richiedenteMedico){
		this.richiedenteMedico = richiedenteMedico;
	}

	/**
	*  javadoc for richiedenteInterno
	*/
	private Employee richiedenteInterno;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_interno_id")
	@ForeignKey(name="FK_Prtcll_ricInterno")
	//@Index(name="IX_Prtcll_ricInterno")
	public Employee getRichiedenteInterno(){
		return richiedenteInterno;
	}

	public void setRichiedenteInterno(Employee richiedenteInterno){
		this.richiedenteInterno = richiedenteInterno;
	}

	/**
	*  javadoc for richiedenteSede
	*/
	private Sedi richiedenteSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_sede_id")
	@ForeignKey(name="FK_Protcll_ricSede")
	//@Index(name="IX_Protcll_ricSede")
	public Sedi getRichiedenteSede(){
		return richiedenteSede;
	}

	public void setRichiedenteSede(Sedi richiedenteSede){
		
		this.richiedenteSede = richiedenteSede;
	}
	
	/**
	 *  javadoc for richiedenteUtente
	 */
	private Person richiedenteUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_utente_id")
	@ForeignKey(name="FK_Protocollo_ricUtente")
	//@Index(name="IX_Protocollo_ricUtente")
	public Person getRichiedenteUtente(){
		return richiedenteUtente;
	}

	public void setRichiedenteUtente(Person richiedenteUtente){
		this.richiedenteUtente = richiedenteUtente;
	}

	private CodeValue riferimento;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="riferimento")
	@ForeignKey(name="FK_Prot_riferimento")
	//@Index(name="IX_Prot_riferimento")
	public CodeValue getRiferimento() {
		return riferimento;
	}

	public void setRiferimento(CodeValue riferimento) {
		this.riferimento = riferimento;
	}
	
	/**
	*  javadoc for richiedenteUtente
	*/
	private Person riferimentoUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento_utente_id")
	@ForeignKey(name="FK_Prtcll_rifUtente")
	//@Index(name="IX_Prtcll_rifUtente")
	public Person getRiferimentoUtente(){
		return riferimentoUtente;
	}

	public void setRiferimentoUtente(Person riferimentoUtente){
		
		this.riferimentoUtente = riferimentoUtente;
	}
	
	/**
	*  javadoc for richiedenteCantiere
	*/
	private Cantiere riferimentoCantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento_cantiere_id")
	@ForeignKey(name="FK_Prtcll_rifCantiere")
	//@Index(name="IX_Prtcll_rifCantiere")
	public Cantiere getRiferimentoCantiere(){
		return riferimentoCantiere;
	}

	public void setRiferimentoCantiere(Cantiere riferimentoCantiere){
		
		this.riferimentoCantiere = riferimentoCantiere;
	}

	/**
	*  javadoc for richiedenteInterno
	*/
	private Employee riferimentoInterno;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento_interno_id")
	@ForeignKey(name="FK_Prtcll_rifInterno")
	//@Index(name="IX_Prtcll_rifInterno")
	public Employee getRiferimentoInterno(){
		return riferimentoInterno;
	}

	public void setRiferimentoInterno(Employee riferimentoInterno){
		this.riferimentoInterno = riferimentoInterno;
	}

	/**
	*  javadoc for riferimentoDitta
	*/
	private PersoneGiuridiche riferimentoDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento_ditta_id")
	@ForeignKey(name="FK_Protcll_rifDitta")
	//@Index(name="IX_Protcll_rifDitta")
	public PersoneGiuridiche getRiferimentoDitta(){
		return riferimentoDitta;
	}

	public void setRiferimentoDitta(PersoneGiuridiche riferimentoDitta){
		this.riferimentoDitta = riferimentoDitta;
	}
	
	/**
	*  javadoc for riferimentoSede
	*/
	private Sedi riferimentoSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riferimento_sede_id")
	@ForeignKey(name="FK_Protcll_rifSede")
	//@Index(name="IX_Protcll_rifSede")
	public Sedi getRiferimentoSede(){
		return riferimentoSede;
	}

	public void setRiferimentoSede(Sedi riferimentoSede){
		this.riferimentoSede = riferimentoSede;
	}
	
	private CodeValue ubicazione;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="ubicazione")
	@ForeignKey(name="FK_Prot_ubicazione")
	//@Index(name="IX_Prot_ubicazione")
	public CodeValue getUbicazione() {
		return ubicazione;
	}

	public void setUbicazione(CodeValue ubicazione) {
		this.ubicazione = ubicazione;
	}
	
	/**
	*  javadoc for ubicazioneDitta
	*/
	private PersoneGiuridiche ubicazioneDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ubicazione_ditta_id")
	@ForeignKey(name="FK_Protcll_ubDitta")
	//@Index(name="IX_Protcll_ubDitta")
	public PersoneGiuridiche getUbicazioneDitta(){
		return ubicazioneDitta;
	}

	public void setUbicazioneDitta(PersoneGiuridiche ubicazioneDitta){
		this.ubicazioneDitta = ubicazioneDitta;
	}
	
	/**
	*  javadoc for ubicazioneSede
	*/
	private Sedi ubicazioneSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ubicazione_sede_id")
	@ForeignKey(name="FK_Protcll_ubSede")
	//@Index(name="IX_Protcll_ubSede")
	public Sedi getUbicazioneSede(){
		return ubicazioneSede;
	}

	public void setUbicazioneSede(Sedi ubicazioneSede){
		this.ubicazioneSede = ubicazioneSede;
	}
	
	/**
	*  javadoc for ubicazioneUtente
	*/
	private Person ubicazioneUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ubicazione_utente_id")
	@ForeignKey(name="FK_Prtcll_ubUtente")
	//@Index(name="IX_Prtcll_ubUtente")
	public Person getUbicazioneUtente(){
		return ubicazioneUtente;
	}

	public void setUbicazioneUtente(Person ubicazioneUtente){
		this.ubicazioneUtente = ubicazioneUtente;
	}
	
	/**
	*  javadoc for ubicazioneCantiere
	*/
	private Cantiere ubicazioneCantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ubicazione_cantiere_id")
	@ForeignKey(name="FK_Prtcll_ubCantiere")
	//@Index(name="IX_Prtcll_ubCantiere")
	public Cantiere getUbicazioneCantiere(){
		return ubicazioneCantiere;
	}

	public void setUbicazioneCantiere(Cantiere ubicazioneCantiere){
		this.ubicazioneCantiere = ubicazioneCantiere;
	}
	
	/**
	 *  Address
	 */
	private AD ubicazioneAddr;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="adl", column=@Column(name="ubicazione_adl")),
		@AttributeOverride(name="bnr", column=@Column(name="ubicazione_bnr")),
		@AttributeOverride(name="cen", column=@Column(name="ubicazione_cen")),
		@AttributeOverride(name="cnt", column=@Column(name="ubicazione_cnt")),
		@AttributeOverride(name="cpa", column=@Column(name="ubicazione_cpa")),
		@AttributeOverride(name="cty", column=@Column(name="ubicazione_cty")),
		@AttributeOverride(name="sta", column=@Column(name="ubicazione_sta")),
		@AttributeOverride(name="stb", column=@Column(name="ubicazione_stb")),
		@AttributeOverride(name="str", column=@Column(name="ubicazione_str")),
		@AttributeOverride(name="zip", column=@Column(name="ubicazione_zip"))
	})
	
	public AD getUbicazioneAddr(){
		return ubicazioneAddr;
	}

	public void setUbicazioneAddr(AD ubicazioneAddr){
		this.ubicazioneAddr = ubicazioneAddr;
	}
	
	private String ubicazioneX;

	@Column(name = "ubicazione_x")
	public String getUbicazioneX() {
		return this.ubicazioneX;
	}

	public void setUbicazioneX(String ubicazioneX) {
		this.ubicazioneX = ubicazioneX;
	}
	
	private String ubicazioneY;

	@Column(name = "ubicazione_y")
	public String getUbicazioneY() {
		return this.ubicazioneY;
	}

	public void setUbicazioneY(String ubicazioneY) {
		this.ubicazioneY = ubicazioneY;
	}
	
	private String mittente;

	@Column(name = "mittente")
	public String getMittente() {
		return this.mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	
	private String mailMittente;

	@Column(name = "mail_mittente")
	public String getMailMittente() {
		return this.mailMittente;
	}

	public void setMailMittente(String mailMittente) {
		this.mailMittente = mailMittente;
	}
	
	private String oggetto;

	@Column(name = "oggetto", length=4000)
	public String getOggetto() {
		return this.oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	private CodeValue supporto;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="supporto")
	@ForeignKey(name="FK_Prot_supp")
	//@Index(name="IX_Prot_supp")
	public CodeValue getSupporto() {
		return supporto;
	}

	public void setSupporto(CodeValue supporto) {
		this.supporto = supporto;
	}
	
	private CodeValue tipoSpedizione;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_spedizione")
	@ForeignKey(name="FK_Prot_tipoSped")
	//@Index(name="IX_Prot_tipoSped")
	public CodeValue getTipoSpedizione() {
		return tipoSpedizione;
	}

	public void setTipoSpedizione(CodeValue tipoSpedizione) {
		this.tipoSpedizione = tipoSpedizione;
	}
	
	private String collegamenti;

	@Column(name = "collegamenti")
	public String getCollegamenti() {
		return this.collegamenti;
	}

	public void setCollegamenti(String collegamenti) {
		this.collegamenti = collegamenti;
	}
	
	private CodeValue fonte;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="fonte")
	@ForeignKey(name="FK_Prot_fonte")
	//@Index(name="IX_Prot_fonte")
	public CodeValue getFonte() {
		return fonte;
	}

	public void setFonte(CodeValue fonte) {
		this.fonte = fonte;
	}
	
	private String nullifieReason;

	@Column(name = "nullifie_reason")
	public String getNullifieReason() {
		return this.nullifieReason;
	}

	public void setNullifieReason(String nullifieReason) {
		this.nullifieReason = nullifieReason;
	}
	
	private String cancelReason;

	@Column(name = "cancel_reason")
	public String getCancelReason() {
		return this.cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	*  javadoc for ubicazioneIMO
	*/
	private String ubicazioneIMO;

	@Column(name="ubicazione_imo")
	public String getUbicazioneIMO(){
		return ubicazioneIMO;
	}

	public void setUbicazioneIMO(String ubicazioneIMO){
		this.ubicazioneIMO = ubicazioneIMO;
	}
	
	/**
	*  javadoc for ubicazioneSpec
	*/
	private String ubicazioneSpec;

	@Column(name="ubicazione_spec")
	public String getUbicazioneSpec(){
		return ubicazioneSpec;
	}

	public void setUbicazioneSpec(String ubicazioneSpec){
		this.ubicazioneSpec = ubicazioneSpec;
	}

	/**
	*  javadoc for ubicazioneTarga
	*/
	private String ubicazioneTarga;

	@Column(name="ubicazione_targa")
	public String getUbicazioneTarga(){
		return ubicazioneTarga;
	}

	public void setUbicazioneTarga(String ubicazioneTarga){
		this.ubicazioneTarga = ubicazioneTarga;
	}
	
	private CodeValue ubicazioneEntita;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="ubicazione_entita")
	@ForeignKey(name="FK_Prt_ubiEnt")
	//@Index(name="IX_Prt_ubiEnt") 
	public CodeValue getUbicazioneEntita() {
		return ubicazioneEntita;
	}

	public void setUbicazioneEntita(CodeValue ubicazioneEntita) {
		this.ubicazioneEntita = ubicazioneEntita;
	}
	
	/**
	*  javadoc for riferimentoIMO
	*/
	private String riferimentoIMO;

	@Column(name="riferimento_imo")
	public String getRiferimentoIMO(){
		return riferimentoIMO;
	}

	public void setRiferimentoIMO(String riferimentoIMO){
		this.riferimentoIMO = riferimentoIMO;
	}
	
	/**
	*  javadoc for riferimentoSpec
	*/
	private String riferimentoSpec;

	@Column(name="riferimento_spec")
	public String getRiferimentoSpec(){
		return riferimentoSpec;
	}

	public void setRiferimentoSpec(String riferimentoSpec){
		this.riferimentoSpec = riferimentoSpec;
	}

	/**
	*  javadoc for riferimentoTarga
	*/
	private String riferimentoTarga;

	@Column(name="riferimento_targa")
	public String getRiferimentoTarga(){
		return riferimentoTarga;
	}

	public void setRiferimentoTarga(String riferimentoTarga){
		this.riferimentoTarga = riferimentoTarga;
	}
	
	private CodeValue riferimentoEntita;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="riferimento_entita")
	@ForeignKey(name="FK_Prt_rifEnt")
	//@Index(name="IX_Prt_rifEnt") 
	public CodeValue getRiferimentoEntita() {
		return riferimentoEntita;
	}

	public void setRiferimentoEntita(CodeValue riferimentoEntita) {
		this.riferimentoEntita = riferimentoEntita;
	}
	
	private String riferimentoDenominazione;

	@Column(name = "riferimento_denominazione")
	public String getRiferimentoDenominazione() {
		return this.riferimentoDenominazione;
	}

	public void setRiferimentoDenominazione(String riferimentoDenominazione) {
		this.riferimentoDenominazione = riferimentoDenominazione;
	}
	
	private String riferimentoNote;

	@Column(name="riferimento_note")
	public String getRiferimentoNote(){
		return riferimentoNote;
	}

	public void setRiferimentoNote(String riferimentoNote){
		this.riferimentoNote = riferimentoNote;
	}
	
	/**
	*  javadoc for dettagliBonifiche
	*/
	private List<DettagliBonifiche> dettagliBonifiche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="protocollo")
	public List<DettagliBonifiche> getDettagliBonifiche(){
		return dettagliBonifiche;
	}

	public void setDettagliBonifiche(List<DettagliBonifiche> list){
		dettagliBonifiche = list;
	}

	public void addDettagliBonifiche(DettagliBonifiche dettagliBonifiche) {
		if (this.dettagliBonifiche == null) {
			this.dettagliBonifiche = new ArrayList<DettagliBonifiche>();
		}
		// add the association
		if(!this.dettagliBonifiche.contains(dettagliBonifiche)) {
			this.dettagliBonifiche.add(dettagliBonifiche);
			// make the inverse link
			dettagliBonifiche.setProtocollo(this);
		}
	}

	public void removeDettagliBonifiche(DettagliBonifiche dettagliBonifiche) {
		if (this.dettagliBonifiche == null) {
			this.dettagliBonifiche = new ArrayList<DettagliBonifiche>();
			return;
		}
		//add the association
		if(this.dettagliBonifiche.contains(dettagliBonifiche)){
			this.dettagliBonifiche.remove(dettagliBonifiche);
			//make the inverse link
			dettagliBonifiche.setProtocollo(null);
		}

	}
	
	public String toString() {
		String codeTransl = "";
		if (code != null) {
			codeTransl = code.getCurrentTranslation();
		}
		return "Comunicazione " + codeTransl + " " + internalId;
	}
	
	
	@Transient
	public String getPrintMittente(){
		
		String ret = "";
		
		try{
				//Ditta, Utente, Interno, Medico, Anonimo
				CodeValue richiedente = getRichiedente();
			
				if (richiedente != null){
					String code = richiedente.getCode();
					if (code != null){
						if (code.equals("Anonimo"))
							return " (" + richiedente.getCurrentTranslation() + ")";
						
						if (code.equals("Medico")){
							
							Physician medico = getRichiedenteMedico();
							if (medico!=null){
								ret += " " + medico.getName().getFam() + " " + medico.getName().getGiv();
							}
							return ret;
						}
					
						if (code.equals("Ditta")) {
							
						
							PersoneGiuridiche pg = getRichiedenteDitta();
							if (pg!=null && pg.getDenominazione()!=null){
								ret += " " + pg.getDenominazione();
							
								Sedi sede = getRichiedenteSede();
								if (sede!=null && sede.getDenominazioneUnitaLocale()!=null){
									ret += " sede: " + sede.getDenominazioneUnitaLocale();
									ret += " "+sede.getAddr();
								}
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}
					
						if (code.equals("Interno")) {
							
						
							Employee interno = getRichiedenteInterno();
							if (interno!=null){
								ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
									//+ " CF: " + interno.getFiscalCode();
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}
					
						if (code.equals("Utente")) {
							
						
							Person person = getRichiedenteUtente();
							if (person!=null){
								ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
								//+ " CF: " + person.getFiscalCode();
							}

							return ret + " (" + richiedente.getCurrentTranslation() + ")";
						}

					}
				}
			

			

		} catch (Exception ex) {}
		
		return ret;
		 
		
	}


	@Transient
	public String getPrintRiferito(){
		String result = "";
		
		
		//NonPrevisto, Ditta, Interno, Utente, Cantiere
		CodeValue riferimento = getRiferimento();
	
		if (riferimento != null){
			String code = riferimento.getCode();
			if (code != null){
				if (code.equals("NonPrevisto"))
					return " (" + riferimento.getCurrentTranslation() + ")";
				
				if (code.equals("Altro")){
					String ret = "";
					if (getRiferimentoEntita()!=null){
						//String altro = .getAltro(getRiferimentoEntita().getCurrentTranslation(), getRiferimentoIMO(), getRiferimentoTarga());
						
						String altro = "A:";
						if (riferimentoEntita!=null){
							
							altro += riferimentoEntita;
							
						if (riferimentoEntita.equals("Mezzo automobilistico") && riferimentoTarga!=null && riferimentoTarga!="")
							altro += " (" + riferimentoTarga + ")";
						else if (riferimentoEntita.equals("Natante") && riferimentoIMO!=null && riferimentoIMO!="")
							altro += " (" + riferimentoIMO + ")";
									
						}
						
						if(altro!=null && altro.length()>2)
							ret += altro.substring(2);
					}
					return ret;
				}
				
				if (code.equals("Cantiere")) {
					String ret = "";
				
					Cantiere c = getRiferimentoCantiere();
					if (c!=null && c.getAddr()!=null){
						ret += " " + c.getAddr();
					
					}
					
					return ret + " (" + riferimento.getCurrentTranslation() + ")";
				}
				
				if (code.equals("Ditta")) {
					String ret = "";
				
					PersoneGiuridiche pg = getRiferimentoDitta();
					if (pg!=null && pg.getDenominazione()!=null){
						ret += " " + pg.getDenominazione();
					
						Sedi sede = getRiferimentoSede();
						if (sede!=null && sede.getDenominazioneUnitaLocale()!=null){
							ret += " sede: " + sede.getDenominazioneUnitaLocale();
							ret += " "+sede.getAddr();
						}
					}

					return ret + " (" + riferimento.getCurrentTranslation() + ")";
				}
			
				if (code.equals("Interno")) {
					String ret = "";
				
					Employee interno = getRiferimentoInterno();
					if (interno!=null){
						ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
							//+ " CF: " + interno.getFiscalCode();
					}

					return ret + " (" + riferimento.getCurrentTranslation() + ")";
				}
			
				if (code.equals("Utente")) {
					String ret = "";
				
					Person person = getRiferimentoUtente();
					if (person!=null){
						ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
						//+ " CF: " + person.getFiscalCode();
					}

					return ret + " (" + riferimento.getCurrentTranslation() + ")";
				}

			}
		}
		
		
		return result;
	}

	
	/// transient per frontespizio 
	

	@Transient
		public String getFrontespizioWorkingLine(){
			
			String workingLine = "";
			CodeValuePhi type = null;
			try{
				
				
				if(getUos()!=null && getUos()!=null && getUos().getArea()!=null){
					type = getUos().getArea();
					workingLine = type.getCurrentTranslation();
			
//					if (type.getCode() != null) {
//						//workingLine = type.getCurrentTranslation();
//						if(type.getCode().equalsIgnoreCase("SUPERVISION") && getVigilanza()!=null){
//							workingLine+="\n"+getVigilanza().getType().getCurrentTranslation();
//						}
//						if(serviceDeliveryLocation.getArea().getCode().equalsIgnoreCase("TECHNICALADVICE") && getParereTecnico()!=null){
//							
//							for(ParereTecnico pt:getParereTecnico()){
//								workingLine+="\n"+pt.getType().getCurrentTranslation();
//							}
//						}
//
//					}
				}
			
			}catch(Exception e){}
			
			return workingLine;
		}
		
	


		@Transient
		public String getFrontespizioMittente(){
			
			String mittente = "";
			
			try{

			
				
					if(getRichiedente()!=null){
						if(getRichiedente().getCode().equalsIgnoreCase("Ditta")){
							mittente+=getRichiedenteDitta().getDenominazione()+"\n";
							if(getRichiedenteSede()!=null){
								mittente+=getRichiedenteSede().getDenominazioneUnitaLocale();
							//	mittente+="\n"+getRichiedenteSede().getAddr();

							}
						}else if(getRichiedente().getCode().equalsIgnoreCase("Utente")){
							
							mittente+=getRichiedenteUtente().getName();

							
						}else if(getRichiedente().getCode().equalsIgnoreCase("Interno")){
							mittente+=getRichiedenteInterno().getName();

							
						}else if(getRichiedente().getCode().equalsIgnoreCase("Medico")){
							mittente+=getRichiedenteMedico().getName();

						}
					}
				
				
			
			}catch(Exception e){}
			
			return mittente;
		}
		
		@Transient
		public String getFrontespizioRiferito(){
			
			String riferito = "";
			
			try{

					if(getRiferimento()!=null){
						if(getRiferimento().getCode().equalsIgnoreCase("Ditta")){
							riferito+=getRiferimentoDitta().getDenominazione();
							//String CF = getRiferimentoDitta().getCodiceFiscale();

						}else if(getRiferimento().getCode().equalsIgnoreCase("Utente")){
							
							riferito+=getRiferimentoUtente().getName();
							//String CF = getRiferimentoUtente().getFiscalCode();

							
						}else if(getRiferimento().getCode().equalsIgnoreCase("Interno")){
							riferito+=getRiferimentoInterno().getName();

							
						}else if(getRiferimento().getCode().equalsIgnoreCase("Medico")){

						}else if(getRiferimento().getCode().equalsIgnoreCase("Altro")){
							
							if(getRiferimentoEntita()!=null){
								riferito+=getRiferimentoEntita();
								
								if(getRiferimentoEntita().getCode().equalsIgnoreCase("natante")){
									riferito+="\n"+getRiferimentoIMO();
								}else if(getRiferimentoEntita().getCode().equalsIgnoreCase("automobile")){
									riferito+="\n"+getRiferimentoTarga();
								}else if(getRiferimentoEntita().getCode().equalsIgnoreCase("altro")){
									riferito+="\n"+getRiferimentoSpec();
								}
								riferito+="\n"+getRiferimentoDenominazione();
								riferito+="\n"+getRiferimentoNote();
							}
							
							
						}
					}
				
			
			}catch(Exception e){}
			
			return riferito;
		}
		
		
		
		@Transient
		public String getFrontespizioNumeroProtocollo(){
			
			String ret = ""; 
			
			try{	
//				if(getIsMaster()){
					if(getNprotocollo()!=null)
						ret=getNprotocollo().toString();
//				}
			
			}catch(Exception e){}
			
			//ret = getUos().getServiceDeliveryLocation().get;
			
			
			
			return ret;
			
		}
		
		@Transient
		public String getRDP(){
			
			return OperatoreAction.instance().getRDP(getUos());
			
			
		}
	
}
