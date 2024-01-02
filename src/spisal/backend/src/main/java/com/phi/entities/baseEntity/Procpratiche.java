package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.actions.MalattiaProfessionaleAction;
import com.phi.entities.actions.MedicinaLavoroAction;
import com.phi.entities.actions.PraticheRiferimentiAction;
import com.phi.entities.actions.ProtocolloAction;
import com.phi.entities.actions.VigilanzaAction;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueStatus;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.ServiceDeliveryLocation;

@Entity
@Table(name = "procpratiche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@NamedQueries(value = {
		@NamedQuery(name = "Procpratiche.getCurrentNumber", query = "select max(pp.nrPratica) from Procpratiche pp " +
																	"join pp.sdlStart wl " +
																	"join wl.parent distr " +
																	"join distr.parent ulss " +
																	"left join pp.vigilanza v " +
																	"where wl.area.id = :wlArea " +
																	"and pp.statusCode is not null " +
																	"and pp.data  >= :minDate and pp.data  <= :maxDate " +
																	"and (:ulssId is null or ulss.internalId = :ulssId) " +
																	"and (:wlId is null or wl.internalId = :wlId) " +
																	"and (:vType is null or v.type.id = :vType)")
	})
public class Procpratiche extends BaseEntity implements LocatedEntity, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8012362030510597853L;

	/**
	*  javadoc for sdlStart
	*/
	private ServiceDeliveryLocation sdlStart;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sdl_start_id")
	@ForeignKey(name="FK_Procpratiche_sdlStart")
	//@Index(name="IX_Procpratiche_sdlStart")
	public ServiceDeliveryLocation getSdlStart(){
		return sdlStart;
	}

	public void setSdlStart(ServiceDeliveryLocation sdlStart){
		this.sdlStart = sdlStart;
	}

	/**
	 *  javadoc for cantieriAssociazioni
	 */
	private List<CantieriAssociazioni> cantieriAssociazioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
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
			cantieriAssociazioni.setProcpratiche(this);
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
			cantieriAssociazioni.setProcpratiche(null);
		}

	}



	/**
	 *  javadoc for ditteAssociazioni
	 */
	private List<DitteAssociazioni> ditteAssociazioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
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
			ditteAssociazioni.setProcpratiche(this);
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
			ditteAssociazioni.setProcpratiche(null);
		}

	}



	/**
	 *  javadoc for personeAssociazioni
	 */
	private List<PersoneAssociazioni> personeAssociazioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<PersoneAssociazioni> getPersoneAssociazioni(){
		return personeAssociazioni;
	}

	public void setPersoneAssociazioni(List<PersoneAssociazioni> list){
		personeAssociazioni = list;
	}

	public void addPersoneAssociazioni(PersoneAssociazioni personeAssociazioni) {
		if (this.personeAssociazioni == null) {
			this.personeAssociazioni = new ArrayList<PersoneAssociazioni>();
		}
		// add the association
		if(!this.personeAssociazioni.contains(personeAssociazioni)) {
			this.personeAssociazioni.add(personeAssociazioni);
			// make the inverse link
			personeAssociazioni.setProcpratiche(this);
		}
	}

	public void removePersoneAssociazioni(PersoneAssociazioni personeAssociazioni) {
		if (this.personeAssociazioni == null) {
			this.personeAssociazioni = new ArrayList<PersoneAssociazioni>();
			return;
		}
		//add the association
		if(this.personeAssociazioni.contains(personeAssociazioni)){
			this.personeAssociazioni.remove(personeAssociazioni);
			//make the inverse link
			personeAssociazioni.setProcpratiche(null);
		}

	}


	/**
	 *  javadoc for conclusioniBl
	 */
	private Boolean conclusioniBl;

	@Column(name="conclusioni_bl")
	public Boolean getConclusioniBl(){
		return conclusioniBl;
	}

	public void setConclusioniBl(Boolean conclusioniBl){
		this.conclusioniBl = conclusioniBl;
	}


	/**
	 *  javadoc for conclusioniMdl
	 */
	private ValutazioneConclusivaMdl conclusioniMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="conclusioni_mdl_id")
	@ForeignKey(name="FK_Prcpratiche_cnclusiniMdl")
	//@Index(name="IX_Prcpratiche_cnclusiniMdl")
	public ValutazioneConclusivaMdl getConclusioniMdl(){
		return conclusioniMdl;
	}

	public void setConclusioniMdl(ValutazioneConclusivaMdl conclusioniMdl){
		this.conclusioniMdl = conclusioniMdl;
	}



	/**
	 *  javadoc for medicinaLavoro
	 */
	private MedicinaLavoro medicinaLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="medicina_lavoro_id")
	@ForeignKey(name="FK_Prcpratiche_medicinaLavr")
	//@Index(name="IX_Prcpratiche_medicinaLavr")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
		org.hibernate.annotations.CascadeType.DELETE,
		org.hibernate.annotations.CascadeType.DELETE_ORPHAN,
		org.hibernate.annotations.CascadeType.REFRESH})
	public MedicinaLavoro getMedicinaLavoro(){
		return medicinaLavoro;
	}

	public void setMedicinaLavoro(MedicinaLavoro medicinaLavoro){
		this.medicinaLavoro = medicinaLavoro;
	}



	/**
	 *  javadoc for observedBy
	 */
	private List<Observer> observedBy;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Observer> getObservedBy(){
		return observedBy;
	}

	public void setObservedBy(List<Observer> list){
		observedBy = list;
	}

	public void addObservedBy(Observer observedBy) {
		if (this.observedBy == null) {
			this.observedBy = new ArrayList<Observer>();
		}
		// add the association
		if(!this.observedBy.contains(observedBy)) {
			this.observedBy.add(observedBy);
			// make the inverse link
			observedBy.setProcpratiche(this);
		}
	}

	public void removeObservedBy(Observer observedBy) {
		if (this.observedBy == null) {
			this.observedBy = new ArrayList<Observer>();
			return;
		}
		//add the association
		if(this.observedBy.contains(observedBy)){
			this.observedBy.remove(observedBy);
			//make the inverse link
			observedBy.setProcpratiche(null);
		}

	}


	/**
	 *  javadoc for inScadenzaM
	 */
	private Boolean inScadenzaM;

	@Transient
	public Boolean getInScadenzaM(){
		return inScadenzaM;
	}

	public void setInScadenzaM(Boolean inScadenzaM){
		this.inScadenzaM = inScadenzaM;
	}

	/**
	 *  javadoc for inScadenzaP
	 */
	private Boolean inScadenzaP;

	@Transient
	public Boolean getInScadenzaP(){
		return inScadenzaP;
	}

	public void setInScadenzaP(Boolean inScadenzaP){
		this.inScadenzaP = inScadenzaP;
	}

	/**
	 *  javadoc for inScadenzaT
	 */
	private Boolean inScadenzaT;

	@Transient
	public Boolean getInScadenzaT(){
		return inScadenzaT;
	}

	public void setInScadenzaT(Boolean inScadenzaT){
		this.inScadenzaT = inScadenzaT;
	}

	/**
	 *  javadoc for nProtocolloMaster
	 */
	private BigDecimal protocolloMasterNumber;

	@Column(name="protocollo_master_number", precision = 22, scale = 0)
	public BigDecimal getProtocolloMasterNumber(){
		return protocolloMasterNumber;
	}

	public void setProtocolloMasterNumber(BigDecimal protocolloMasterNumber){
		this.protocolloMasterNumber = protocolloMasterNumber;
	}

	/**
	 *  javadoc for sospensioni
	 */
	private List<Sospensione> sospensioni;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="pratica", cascade=CascadeType.PERSIST)
	public List<Sospensione> getSospensioni() {
		return sospensioni;
	}

	public void setSospensioni(List<Sospensione>list){
		sospensioni = list;
	}

	public void addSospensioni(Sospensione sospensioni) {
		if (this.sospensioni == null) {
			this.sospensioni = new ArrayList<Sospensione>();
		}
		// add the association
		if(!this.sospensioni.contains(sospensioni)) {
			this.sospensioni.add(sospensioni);
			// make the inverse link
			sospensioni.setPratica(this);
		}
	}

	public void removeSospensioni(Sospensione sospensioni) {
		if (this.sospensioni == null) {
			this.sospensioni = new ArrayList<Sospensione>();
			return;
		}
		//add the association
		if(this.sospensioni.contains(sospensioni)){
			this.sospensioni.remove(sospensioni);
			//make the inverse link
			sospensioni.setPratica(null);
		}
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
	 *  javadoc for buonePratiche
	 */
	private List<CodeValueLaw> buonePratiche;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="pratiche_buonepratiche", joinColumns = { @JoinColumn(name="procpratiche_id") }, inverseJoinColumns = { @JoinColumn(name="buonepratiche") })
	@ForeignKey(name="FK_buonePratiche_Procpratiche", inverseName="FK_Procpratiche_buonePratiche")
	@IndexColumn(name="list_index")
	public List<CodeValueLaw> getBuonePratiche(){
		return buonePratiche;
	}

	public void setBuonePratiche(List<CodeValueLaw> buonePratiche){
		this.buonePratiche = buonePratiche;
	}


	/**
	 *  classe contenente un grosso campo descrittivo che raccoglie tutti i dati su prevnet che non si sapeva dove migrare
	 */
	private PrevnetNotes prevnetNotes;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prevnet_notes_id")
	@ForeignKey(name="FK_Procpratiche_prevnetNotes")
	//@Index(name="IX_Procpratiche_prevnetNotes")
	public PrevnetNotes getPrevnetNotes(){
		return prevnetNotes;
	}

	public void setPrevnetNotes(PrevnetNotes prevnetNotes){
		this.prevnetNotes = prevnetNotes;
	}


	/**
	 *  javadoc for conclusioni
	 */
	private String conclusioni;

	@Column(name="conclusioni",length=2500)
	public String getConclusioni(){
		return conclusioni;
	}

	public void setConclusioni(String conclusioni){
		this.conclusioni = conclusioni;
	}


	/**
	 *  javadoc for protocolloMulti
	 */
	private List<Protocollo> protocolloMulti;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="procpraticheMulti", cascade=CascadeType.PERSIST)
	public List<Protocollo> getProtocolloMulti(){
		return protocolloMulti;
	}

	public void setProtocolloMulti(List<Protocollo> list){
		protocolloMulti = list;
	}

	public void addProtocolloMulti(Protocollo protocolloMulti) {
		if (this.protocolloMulti == null) {
			this.protocolloMulti = new ArrayList<Protocollo>();
		}
		// add the association
		if(!this.protocolloMulti.contains(protocolloMulti)) {
			this.protocolloMulti.add(protocolloMulti);
			// make the inverse link
			if (protocolloMulti.getProcpraticheMulti() == null || !protocolloMulti.getProcpraticheMulti().contains(this))
				protocolloMulti.addProcpraticheMulti(this);
		}
	}

	public void removeProtocolloMulti(Protocollo protocolloMulti) {
		if (this.protocolloMulti == null) {
			this.protocolloMulti = new ArrayList<Protocollo>();
			return;
		}
		//add the association
		if(this.protocolloMulti.contains(protocolloMulti)){
			this.protocolloMulti.remove(protocolloMulti);
			//make the inverse link
			if (protocolloMulti.getProcpraticheMulti() != null && protocolloMulti.getProcpraticheMulti().contains(this))
				protocolloMulti.removeProcpraticheMulti(this);
		}

	}


	/**
	 *  javadoc for fineSosp
	 */
	private Date fineSosp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fine_sosp")
	public Date getFineSosp(){
		return fineSosp;
	}

	public void setFineSosp(Date fineSosp){
		this.fineSosp = fineSosp;
	}

	/**
	 *  javadoc for inizioSosp
	 */
	private Date inizioSosp;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="inizio_sosp")
	public Date getInizioSosp(){
		return inizioSosp;
	}

	public void setInizioSosp(Date inizioSosp){
		this.inizioSosp = inizioSosp;
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
	 *  javadoc for esitoConclusione
	 */
	private CodeValuePhi esitoConclusione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esitoConclusione")
	@ForeignKey(name="FK_Procpratiche_esitoConclusione")
	//@Index(name="IX_Procpratiche_esitoConclusione")
	public CodeValuePhi getEsitoConclusione(){
		return esitoConclusione;
	}

	public void setEsitoConclusione(CodeValuePhi esitoConclusione){
		this.esitoConclusione = esitoConclusione;
	}

	/**
	 *  javadoc for esitoPratica
	 */
	private CodeValuePhi esitoPratica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esitoPratica")
	@ForeignKey(name="FK_Procpratiche_esitoPratica")
	//@Index(name="IX_Procpratiche_esitoPratica")
	public CodeValuePhi getEsitoPratica(){
		return esitoPratica;
	}

	public void setEsitoPratica(CodeValuePhi esitoPratica){
		this.esitoPratica = esitoPratica;
	}


	/**
	 *  javadoc for praticheRiferimenti
	 */
	private PraticheRiferimenti praticheRiferimenti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="pratiche_riferimenti_id")
	@ForeignKey(name="FK_Prcprtche_prtcheRferment")
	//@Index(name="IX_Prcprtche_prtcheRferment")
	public PraticheRiferimenti getPraticheRiferimenti(){
		return praticheRiferimenti;
	}

	public void setPraticheRiferimenti(PraticheRiferimenti praticheRiferimenti){
		this.praticheRiferimenti = praticheRiferimenti;
	}



	/**
	 *  javadoc for informazione
	 */
	private List<Informazione> informazione;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Informazione> getInformazione(){
		return informazione;
	}

	public void setInformazione(List<Informazione> list){
		informazione = list;
	}

	public void addInformazione(Informazione informazione) {
		if (this.informazione == null) {
			this.informazione = new ArrayList<Informazione>();
		}
		// add the association
		if(!this.informazione.contains(informazione)) {
			this.informazione.add(informazione);
			// make the inverse link
			informazione.setProcpratiche(this);
		}
	}

	public void removeInformazione(Informazione informazione) {
		if (this.informazione == null) {
			this.informazione = new ArrayList<Informazione>();
			return;
		}
		//add the association
		if(this.informazione.contains(informazione)){
			this.informazione.remove(informazione);
			//make the inverse link
			informazione.setProcpratiche(null);
		}

	}



	/**
	 *  javadoc for formazione
	 */
	private List<Formazione> formazione;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Formazione> getFormazione(){
		return formazione;
	}

	public void setFormazione(List<Formazione> list){
		formazione = list;
	}

	public void addFormazione(Formazione formazione) {
		if (this.formazione == null) {
			this.formazione = new ArrayList<Formazione>();
		}
		// add the association
		if(!this.formazione.contains(formazione)) {
			this.formazione.add(formazione);
			// make the inverse link
			formazione.setProcpratiche(this);
		}
	}

	public void removeFormazione(Formazione formazione) {
		if (this.formazione == null) {
			this.formazione = new ArrayList<Formazione>();
			return;
		}
		//add the association
		if(this.formazione.contains(formazione)){
			this.formazione.remove(formazione);
			//make the inverse link
			formazione.setProcpratiche(null);
		}

	}



	/**
	 *  javadoc for lifestyle
	 */
	private List<Lifestyle> lifestyle;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Lifestyle> getLifestyle(){
		return lifestyle;
	}

	public void setLifestyle(List<Lifestyle> list){
		lifestyle = list;
	}

	public void addLifestyle(Lifestyle lifestyle) {
		if (this.lifestyle == null) {
			this.lifestyle = new ArrayList<Lifestyle>();
		}
		// add the association
		if(!this.lifestyle.contains(lifestyle)) {
			this.lifestyle.add(lifestyle);
			// make the inverse link
			lifestyle.setProcpratiche(this);
		}
	}

	public void removeLifestyle(Lifestyle lifestyle) {
		if (this.lifestyle == null) {
			this.lifestyle = new ArrayList<Lifestyle>();
			return;
		}
		//add the association
		if(this.lifestyle.contains(lifestyle)){
			this.lifestyle.remove(lifestyle);
			//make the inverse link
			lifestyle.setProcpratiche(null);
		}

	}




	/**
	 *  javadoc for benessereOrg
	 */
	private List<BenessereOrg> benessereOrg;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<BenessereOrg> getBenessereOrg() {
		return benessereOrg;
	}

	public void setBenessereOrg(List<BenessereOrg>list){
		benessereOrg = list;
	}

	public void addBenessereOrg(BenessereOrg benessereOrg) {
		if (this.benessereOrg == null) {
			this.benessereOrg = new ArrayList<BenessereOrg>();
		}
		// add the association
		if(!this.benessereOrg.contains(benessereOrg)) {
			this.benessereOrg.add(benessereOrg);
			// make the inverse link
			benessereOrg.setProcpratiche(this);
		}
	}

	public void removeBenessereOrg(BenessereOrg benessereOrg) {
		if (this.benessereOrg == null) {
			this.benessereOrg = new ArrayList<BenessereOrg>();
			return;
		}
		//add the association
		if(this.benessereOrg.contains(benessereOrg)){
			this.benessereOrg.remove(benessereOrg);
			//make the inverse link
			benessereOrg.setProcpratiche(null);
		}
	}


	/**
	 *  javadoc for malattiaProfessionale
	 */
	private MalattiaProfessionale malattiaProfessionale;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="malattia_professionale_id")
	@ForeignKey(name="FK_Prcprtiche_mlttPrfessnle")
	//@Index(name="IX_Prcprtiche_mlttPrfessnle")
	public MalattiaProfessionale getMalattiaProfessionale(){
		return malattiaProfessionale;
	}

	public void setMalattiaProfessionale(MalattiaProfessionale malattiaProfessionale){
		this.malattiaProfessionale = malattiaProfessionale;
	}

	/**
	 *  javadoc for provvedimenti
	 */
	private List<Provvedimenti> provvedimenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Provvedimenti> getProvvedimenti() {
		return provvedimenti;
	}

	public void setProvvedimenti(List<Provvedimenti>list){
		provvedimenti = list;
	}

	public void addProvvedimenti(Provvedimenti provvedimenti) {
		if (this.provvedimenti == null) {
			this.provvedimenti = new ArrayList<Provvedimenti>();
		}
		// add the association
		if(!this.provvedimenti.contains(provvedimenti)) {
			this.provvedimenti.add(provvedimenti);
			// make the inverse link
			provvedimenti.setProcpratiche(this);
		}
	}

	public void removeProvvedimenti(Provvedimenti provvedimenti) {
		if (this.provvedimenti == null) {
			this.provvedimenti = new ArrayList<Provvedimenti>();
			return;
		}
		//add the association
		if(this.provvedimenti.contains(provvedimenti)){
			this.provvedimenti.remove(provvedimenti);
			//make the inverse link
			provvedimenti.setProcpratiche(null);
		}
	}



	/**
	 *  javadoc for tagFascicolo
	 */
	private List<TagFascicolo> tagFascicolo;

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="procpratiche")
	public List<TagFascicolo> getTagFascicolo(){
		return tagFascicolo;
	}

	public void setTagFascicolo(List<TagFascicolo> list){
		tagFascicolo = list;
	}

	public void addTagFascicolo(TagFascicolo tagFascicolo) {


		if (this.tagFascicolo == null) {
			this.tagFascicolo = new ArrayList<TagFascicolo>();
		}
		// add the association
		if(!this.tagFascicolo.contains(tagFascicolo)) {
			this.tagFascicolo.add(tagFascicolo);
			// make the inverse link
			if (tagFascicolo.getProcpratiche() == null || !tagFascicolo.getProcpratiche().contains(this))
				tagFascicolo.addProcpratiche(this);

		}
	}

	public void removeTagFascicolo(TagFascicolo tagFascicolo) {

		if (this.tagFascicolo == null) {
			this.tagFascicolo = new ArrayList<TagFascicolo>();
			return;
		}
		//add the association
		if(this.tagFascicolo.contains(tagFascicolo)){
			this.tagFascicolo.remove(tagFascicolo);
			//make the inverse link
			if (tagFascicolo.getProcpratiche() != null && tagFascicolo.getProcpratiche().contains(this))
				tagFascicolo.removeProcpratiche(this);
		}

	}



	/**
	 *  javadoc for parereTecnico
	 */
	private List<ParereTecnico> parereTecnico;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<ParereTecnico> getParereTecnico(){
		return parereTecnico;
	}

	public void setParereTecnico(List<ParereTecnico> list){
		parereTecnico = list;
	}

	public void addParereTecnico(ParereTecnico parereTecnico) {
		if (this.parereTecnico == null) {
			this.parereTecnico = new ArrayList<ParereTecnico>();
		}
		// add the association
		if(!this.parereTecnico.contains(parereTecnico)) {
			this.parereTecnico.add(parereTecnico);
			// make the inverse link
			parereTecnico.setProcpratiche(this);
		}
	}

	public void removeParereTecnico(ParereTecnico parereTecnico) {
		if (this.parereTecnico == null) {
			this.parereTecnico = new ArrayList<ParereTecnico>();
			return;
		}
		//add the association
		if(this.parereTecnico.contains(parereTecnico)){
			this.parereTecnico.remove(parereTecnico);
			//make the inverse link
			parereTecnico.setProcpratiche(null);
		}

	}


	/**
	 *  javadoc for vigilanza
	 */
	private Vigilanza vigilanza;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_Procpratiche_vigilanza")
	//@Index(name="IX_Procpratiche_vigilanza")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}

	/**
	 *  javadoc for oggetti
	 */
	private List<Oggetto> oggetti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Oggetto> getOggetti() {
		return oggetti;
	}

	public void setOggetti(List<Oggetto>list){
		oggetti = list;
	}

	public void addOggetti(Oggetto oggetti) {
		if (this.oggetti == null) {
			this.oggetti = new ArrayList<Oggetto>();
		}
		// add the association
		if(!this.oggetti.contains(oggetti)) {
			this.oggetti.add(oggetti);
			// make the inverse link
			oggetti.setProcpratiche(this);
		}
	}

	public void removeOggetti(Oggetto oggetti) {
		if (this.oggetti == null) {
			this.oggetti = new ArrayList<Oggetto>();
			return;
		}
		//add the association
		if(this.oggetti.contains(oggetti)){
			this.oggetti.remove(oggetti);
			//make the inverse link
			oggetti.setProcpratiche(null);
		}
	}

	/**
	 *  javadoc for cantiere
	 */
	private List<Cantiere> cantiere;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="pratiche_cantieri", joinColumns = { @JoinColumn(name="pratica_id") }, inverseJoinColumns = { @JoinColumn(name="cantiere_id") })
	@ForeignKey(name="FK_pratiche_cantieri", inverseName="FK_cantieri_pratiche")
	@IndexColumn(name="IX_pratiche_cantieri")
	public List<Cantiere> getCantiere() {
		return cantiere;
	}

	public void setCantiere(List<Cantiere>list){
		cantiere = list;
	}

	public void addCantiere(Cantiere cantiere) {
		if (this.cantiere == null) {
			this.cantiere = new ArrayList<Cantiere>();
		}
		// add the association
		if(!this.cantiere.contains(cantiere)) {
			this.cantiere.add(cantiere);
			// make the inverse link
			if (cantiere.getProcpratiche() == null || !cantiere.getProcpratiche().contains(this))
				cantiere.addProcpratiche(this);
		}
	}

	public void removeCantiere(Cantiere cantiere) {
		if (this.cantiere == null) {
			this.cantiere = new ArrayList<Cantiere>();
			return;
		}
		//add the association
		if(this.cantiere.contains(cantiere)){
			this.cantiere.remove(cantiere);
			//make the inverse link
			if (cantiere.getProcpratiche() != null && cantiere.getProcpratiche().contains(this))
				cantiere.removeProcpratiche(this);
		}
	}


	/**
	 *  javadoc for attivita
	 */
	private List<Attivita> attivita;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Attivita> getAttivita() {
		return attivita;
	}

	public void setAttivita(List<Attivita>list){
		attivita = list;
	}

	public void addAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
		}
		// add the association
		if(!this.attivita.contains(attivita)) {
			this.attivita.add(attivita);
			// make the inverse link
			attivita.setProcpratiche(this);
		}
	}

	public void removeAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
			return;
		}
		//add the association
		if(this.attivita.contains(attivita)){
			this.attivita.remove(attivita);
			//make the inverse link
			attivita.setProcpratiche(null);
		}
	}

	/**
	 *  javadoc for upg
	 */
	private List<Operatore> upg;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="procpratiche_upg",
	joinColumns = { @JoinColumn(name="Procpratiche_id") },
	inverseJoinColumns = { @JoinColumn(name="Operatore_id") })
	@ForeignKey(name="FK_Procpratiche_upg", inverseName="FK_Upg_Procpratiche")
	@IndexColumn(name="Procpratiche_Upg")
	public List<Operatore> getUpg() {
		return upg;
	}

	public void setUpg(List<Operatore>list){
		upg = list;
	}


	/**
	 *  javadoc for inchiestaInfortunio
	 */
	private List<InchiestaInfortunio> inchiestaInfortunio;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<InchiestaInfortunio> getInchiestaInfortunio() {
		return inchiestaInfortunio;
	}

	public void setInchiestaInfortunio(List<InchiestaInfortunio>list){
		inchiestaInfortunio = list;
	}

	public void addInchiestaInfortunio(InchiestaInfortunio inchiestaInfortunio) {
		if (this.inchiestaInfortunio == null) {
			this.inchiestaInfortunio = new ArrayList<InchiestaInfortunio>();
		}
		// add the association
		if(!this.inchiestaInfortunio.contains(inchiestaInfortunio)) {
			this.inchiestaInfortunio.add(inchiestaInfortunio);
			// make the inverse link
			inchiestaInfortunio.setProcpratiche(this);
		}
	}

	public void removeInchiestaInfortunio(InchiestaInfortunio inchiestaInfortunio) {
		if (this.inchiestaInfortunio == null) {
			this.inchiestaInfortunio = new ArrayList<InchiestaInfortunio>();
			return;
		}
		//add the association
		if(this.inchiestaInfortunio.contains(inchiestaInfortunio)){
			this.inchiestaInfortunio.remove(inchiestaInfortunio);
			//make the inverse link
			inchiestaInfortunio.setProcpratiche(null);
		}
	}

	/**
	 *  javadoc for documenti
	 */
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Procpratiche_id")
	@ForeignKey(name="FK_documenti_Procpratiche")
	//@Index(name="IX_documenti_Procpratiche")
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
			//Remove from childs
			if (this.attivita != null) {
				for (Attivita a : this.attivita) {
					a.getDocumenti().remove(alfrescoDocument);
					if (a.getSoggetto() != null) {
						for (Soggetto s : a.getSoggetto()) {
							s.getDocumenti().remove(alfrescoDocument);
						}
					}
					if (a.getProvvedimenti() != null) {
						for (Provvedimenti p : a.getProvvedimenti()) {
							p.getDocumenti().remove(alfrescoDocument);
						}
					}
				}
			}
		}
	}

	/**
	 *  javadoc for oldNumero
	 */
	private String oldNumero;

	@Column(name="old_numero")
	public String getOldNumero(){
		return oldNumero;
	}

	public void setOldNumero(String oldNumero){
		this.oldNumero = oldNumero;
	}

	/**
	 *  javadoc for numero
	 */
	private String numero;

	@Column(name="numero")
	public String getNumero(){
		return numero;
	}

	public void setNumero(String numero){
		this.numero = numero;
	}

	/**
	 *  javadoc for serviceDeliveryLocation
	 */
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_Procpratiche_sdloc")
	//@Index(name="IX_Procpratiche_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){

		this.serviceDeliveryLocation = serviceDeliveryLocation;



	}

	/**
	 *  javadoc for rdp - Responsabile del procedimento
	 */
	private Employee rdp;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rdp_id")
	@ForeignKey(name="FK_Procpratiche_rdp")
	//@Index(name="IX_Procpratiche_rdp")
	public Employee getRdp(){
		return rdp;
	}

	public void setRdp(Employee rdp){
		this.rdp = rdp;
	}

	/**
	 *  javadoc for rdi - Responsabile dell'istruttoria
	 */
	private Employee rdi;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rdi_id")
	@ForeignKey(name="FK_Procpratiche_rdi")
	//@Index(name="IX_Procpratiche_rdi")
	public Employee getRdi(){
		return rdi;
	}

	public void setRdi(Employee rdi){
		this.rdi = rdi;
	}

	/**
	 *  javadoc for rfp - Referente della pratica
	 */
	private Employee rfp;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rfp_id")
	@ForeignKey(name="FK_Procpratiche_rfp")
	//@Index(name="IX_Procpratiche_rfp")
	public Employee getRfp(){
		return rfp;
	}

	public void setRfp(Employee rfp){
		this.rfp = rfp;
	}

	/**
	 *  javadoc for operatori
	 */
	private List<Operatore> operatori;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="pratica_operatori",
	joinColumns = { @JoinColumn(name="Pratica_id") },
	inverseJoinColumns = { @JoinColumn(name="Operatore_id") })
	@ForeignKey(name="FK_Pratica_operatori", inverseName="FK_Operatori_Pratica")
	@IndexColumn(name="Pratica_Operatori")
	public List<Operatore> getOperatori() {
		return operatori;
	}

	public void setOperatori(List<Operatore> list){
		operatori = list;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Procpratiche_sequence")
	@SequenceGenerator(name = "Procpratiche_sequence", sequenceName = "Procpratiche_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Proc_code")
	//@Index(name="IX_Proc_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueStatus.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Proc_sc")
	//@Index(name="IX_Proc_sc") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

	private String riferimento;

	@Column(name = "riferimento", length = 1000)
	public String getRiferimento() {
		return this.riferimento; 
	}

	public void setRiferimento(String riferimento) {

		this.riferimento = riferimento;
	}

	/**
	 *  javadoc for date
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
	 *  javadoc for completedDate
	 */
	private Date completedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="completed_date")
	public Date getCompletedDate(){
		return completedDate;
	}

	public void setCompletedDate(Date completedDate){
		this.completedDate = completedDate;
	}

	/**
	 *  javadoc for riservatezza
	 */
	private CodeValuePhi riservatezza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="riservatezza")
	@ForeignKey(name="FK_Procpratiche_riservatezza")
	//@Index(name="IX_Procpratiche_riservatezza")
	public CodeValuePhi getRiservatezza(){
		return riservatezza;
	}

	public void setRiservatezza(CodeValuePhi riservatezza){
		this.riservatezza = riservatezza;
	}

	private String note;

	@Column(name = "note", length = 4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/* Atti relativi ad una determinata pratica - Dovrebbero essere le varie notifiche in ingresso o in uscita. 
	 * Pg 25 all'orizzontale Gestione Generale Attività pg. 31 - Definizione a pg. 28
	 * 
	 * C'è sempre un riferimento verso la pratica (IDPROCPRATICA), ma non sempre un riferimento al protocollo (IDPROTOCOLLO NPROTOCOLLO)
	 * Dovrebbero quindi essere tutte le notifiche (che arrivano o meno da protocollo) che hanno dato viata ad una pratica */

	protected Employee author;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Proc_author")
	//@Index(name="IX_Proc_author")
	public Employee getAuthor() {
		return author;
	}

	public void setAuthor(Employee param) {
		this.author = param;
	}


	//@OneToMany - molte notifiche che arrivano da protocollo possono fare riferimento ad una stessa pratica
	private List<Protocollo> protocollo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procpratiche")
	public List<Protocollo> getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(List<Protocollo> protocollo) {

		this.protocollo = protocollo;
	}

	private List<Infortuni> infortuni;

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "procpratiche")
	@OneToMany(fetch=FetchType.LAZY, mappedBy="procpratiche", cascade=CascadeType.PERSIST)
	public List<Infortuni> getInfortuni() {
		return this.infortuni;
	}

	public void setInfortuni(List<Infortuni> infortuni) {
		this.infortuni = infortuni;
	}

	//@OneToMany - molti sopralluoghi possono essere associati ad una data pratica
	//	private List<Sopralluoghidip> sopralluoghidip;
	//	
	//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procpratiche")
	//	public List<Sopralluoghidip> getSopralluoghidip() {
	//		return this.sopralluoghidip;
	//	}
	//
	//	public void setSopralluoghidip(List<Sopralluoghidip> sopralluoghidip) {
	//		this.sopralluoghidip = sopralluoghidip;
	//	}

	/**
	 *  javadoc for UOC
	 */
	private ServiceDeliveryLocation uoc;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="uoc")
	@ForeignKey(name="FK_Proc_uoc")
	//@Index(name="IX_Proc_uoc")
	public ServiceDeliveryLocation getUoc(){
		return uoc;
	}

	public void setUoc(ServiceDeliveryLocation uoc){
		this.uoc = uoc;
	}

	private String heldReason;

	@Column(name = "held_reason")
	public String getHeldReason() {
		return this.heldReason;
	}

	public void setHeldReason(String heldReason) {
		this.heldReason = heldReason;
	}

	private String suspendedReason;

	@Column(name = "suspended_reason")
	public String getSuspendedReason() {
		return this.suspendedReason;
	}

	public void setSuspendedReason(String suspendedReason) {
		this.suspendedReason = suspendedReason;
	}

	private String cancelReason;

	@Column(name = "cancel_reason")
	public String getCancelReason() {
		return this.cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	
	private Integer nrPratica;

	@Column(name = "nr_pratica")
	@Index(name="IX_Procpratiche_nr_pratica")
	public Integer getNrPratica() {
		return nrPratica;
	}
	public void setNrPratica(Integer nrPratica) {
		this.nrPratica = nrPratica;
	}

	public String toString() {
		String workingLine = "";
		if (serviceDeliveryLocation != null && serviceDeliveryLocation.getArea() != null) {
			workingLine = serviceDeliveryLocation.getArea().getCurrentTranslation();
		}
		return "Pratica " + workingLine + " " + internalId;
	}

	@Transient
	public String getWorkingLine(){


		String workingLine = "";

		try{

			if (serviceDeliveryLocation != null && serviceDeliveryLocation.getArea() != null) {
				workingLine = serviceDeliveryLocation.getArea().getCurrentTranslation();
			} 

			if(serviceDeliveryLocation.getArea()!=null){
				if(serviceDeliveryLocation.getArea().getCode().equalsIgnoreCase("SUPERVISION") && getVigilanza()!=null){
					workingLine+="\n"+getVigilanza().getType().getCurrentTranslation();
				}
				if(serviceDeliveryLocation.getArea().getCode().equalsIgnoreCase("TECHNICALADVICE") && getParereTecnico()!=null){

					for(ParereTecnico pt:getParereTecnico()){
						workingLine+="\n"+pt.getType().getCurrentTranslation();
					}
				}
			}

		}catch(Exception e){}

		return workingLine;
	}

	@Transient
	public String getMittente(){

		String mittente = "";

		try{

			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){
					if(prot.getRichiedente()!=null && prot.getIsMaster()){
						if(prot.getRichiedente().getCode().equalsIgnoreCase("Ditta")){
							mittente+=prot.getRichiedenteDitta().getDenominazione();
							//						if(prot.getRichiedenteSede()!=null){
							//							mittente+=prot.getRichiedenteSede().getDenominazioneUnitaLocale();
							//							mittente+="\n"+prot.getRichiedenteSede().getAddr();
							//							if(prot.getRichiedenteSede().getTelecom()!=null){
							//								String tel = prot.getRichiedenteSede().getTelecom().getAs();
							//								mittente+="\nTel: "+((tel == null) ? "" : tel);
							//								String email = prot.getRichiedenteSede().getTelecom().getMail();
							//								mittente+="eMail: "+((email == null) ? "" : email);	
							//							}
						}
					}else if(prot.getRichiedente().getCode().equalsIgnoreCase("Utente")){

						mittente+=prot.getRichiedenteUtente().getName();
						//						String CF = prot.getRichiedenteUtente().getFiscalCode();
						//						mittente+="\nCF: "+((CF == null) ? " ----- " : CF);
						//						mittente+="\n"+prot.getRichiedenteUtente().getAddr();
						//						if(prot.getRichiedenteUtente().getTelecom()!=null){
						//							String tel = prot.getRichiedenteUtente().getTelecom().getAs();
						//							mittente+="\nTel: "+((tel == null) ? " ----- " : tel);
						//							String email = prot.getRichiedenteUtente().getTelecom().getMail();
						//							mittente+="eMail: "+((email == null) ? "" : email);
						//						}

					}else if(prot.getRichiedente().getCode().equalsIgnoreCase("Interno")){
						mittente+=prot.getRichiedenteInterno().getName();
						//						String CF = prot.getRichiedenteInterno().getFiscalCode();
						//						mittente+="\nCF: "+((CF == null) ? " ----- " : CF);
						//						mittente+="\n"+prot.getRichiedenteInterno().getAddr();
						//						if(prot.getRichiedenteInterno().getTelecom()!=null){
						//							String tel = prot.getRichiedenteInterno().getTelecom().getAs();
						//							mittente+="\nTel: "+((tel == null) ? " ----- " : tel);
						//							String email = prot.getRichiedenteInterno().getTelecom().getMail();
						//							mittente+="eMail: "+((email == null) ? "" : email);	
						//						}

					}else if(prot.getRichiedente().getCode().equalsIgnoreCase("Medico")){
						mittente+=prot.getRichiedenteMedico().getName();
						//						mittente+="\n CR: "+prot.getRichiedenteMedico().getRegionalCode();
						//						mittente+="\n"+prot.getRichiedenteMedico().getAddr();
						//						if(prot.getRichiedenteMedico().getTelecom()!=null){
						//							String tel = prot.getRichiedenteMedico().getTelecom().getAs();
						//							mittente+="\nTel: "+((tel == null) ? " ----- " : tel);
						//							String email = prot.getRichiedenteMedico().getTelecom().getMail();
						//							mittente+="eMail: "+((email == null) ? "" : email);		
						//						}
						//					}
					}
				}

			}
		}catch(Exception e){}

		return mittente;
	}

	@Transient
	public String getRiferito(){

		String riferito = "";

		try{
			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){ 
					if(prot.getRiferimento()!=null && prot.getIsMaster()){
						if(prot.getRiferimento().getCode().equalsIgnoreCase("Ditta")){
							riferito+=prot.getRiferimentoDitta().getDenominazione();
							//						String IVA = prot.getRiferimentoDitta().getCodiceFiscale();
							//						riferito+="\nP.IVA:"+((IVA == null) ? " ----- " : IVA);
							//						String CF = prot.getRiferimentoDitta().getCodiceFiscale();
							//						riferito+="\nCF: "+((CF == null) ? " ----- " : CF);
							//						if(prot.getRiferimentoSede()!=null){
							//							riferito+="\n"+prot.getRiferimentoSede().getDenominazioneUnitaLocale();
							//							riferito+="\n"+prot.getRiferimentoSede().getAddr();
							//							if(prot.getRiferimentoSede().getTelecom()!=null){
							//								String tel = prot.getRiferimentoSede().getTelecom().getAs();
							//								riferito+="\nTel: "+((tel == null) ? " ----- " : tel);
							//								String email = prot.getRiferimentoSede().getTelecom().getMail();
							//								riferito+="eMail: "+((email == null) ? " ----- " : email);		
							//							}
							//						}
						}else if(prot.getRiferimento().getCode().equalsIgnoreCase("Utente")){

							riferito+=prot.getRiferimentoUtente().getName();
							//						String CF = prot.getRiferimentoUtente().getFiscalCode();
							//						riferito+="\nCF: "+((CF == null) ? " ----- " : CF);
							//						riferito+="\n"+prot.getRiferimentoUtente().getAddr();
							//						if(prot.getRiferimentoUtente().getTelecom()!=null){
							//							String tel = prot.getRiferimentoUtente().getTelecom().getAs();
							//							riferito+="\nTel: "+((tel == null) ? " ----- " : tel);
							//							String email = prot.getRiferimentoUtente().getTelecom().getMail();
							//							riferito+="eMail: "+((email == null) ? " ----- " : email);			
							//						}

						}else if(prot.getRiferimento().getCode().equalsIgnoreCase("Interno")){
							riferito+=prot.getRiferimentoInterno().getName();
							//						riferito+="\nCF: "+prot.getRiferimentoInterno().getFiscalCode();
							//						riferito+="\n"+prot.getRiferimentoInterno().getAddr();
							//						if(prot.getRiferimentoInterno().getTelecom()!=null){
							//							String tel = prot.getRiferimentoInterno().getTelecom().getAs();
							//							riferito+="\nTel: "+((tel == null) ? " ----- " : tel);
							//							String email = prot.getRiferimentoInterno().getTelecom().getMail();
							//							riferito+="eMail: "+((email == null) ? " ----- " : email);				
							//						}

						}else if(prot.getRiferimento().getCode().equalsIgnoreCase("Medico")){
							//						riferito+="\n"+prot.getRif.getName();
							//						riferito+="\n Codice regionale: "+prot.getRiferimentoMedico().getRegionalCode();
							//						riferito+="\n"+prot.getRiferimentoMedico().getAddr();
							//						if(prot.getRichiedenteMedico().getTelecom()!=null){
							//							riferito+="\nTel: "+prot.getRichiedenteMedico().getTelecom().getAs()+" email: "+prot.getRichiedenteMedico().getTelecom().getMail();	
							//						}
						}else if(prot.getRiferimento().getCode().equalsIgnoreCase("Altro")){

							if(prot.getRiferimentoEntita()!=null){
								riferito+=prot.getRiferimentoEntita();

								if(prot.getRiferimentoEntita().getCode().equalsIgnoreCase("natante")){
									riferito+="\n"+prot.getRiferimentoIMO();
								}else if(prot.getRiferimentoEntita().getCode().equalsIgnoreCase("automobile")){
									riferito+="\n"+prot.getRiferimentoTarga();
								}else if(prot.getRiferimentoEntita().getCode().equalsIgnoreCase("altro")){
									riferito+="\n"+prot.getRiferimentoSpec();
								}
								riferito+="\n"+prot.getRiferimentoDenominazione();
								riferito+="\n"+prot.getRiferimentoNote();
							}


						}
					}
				}

			}

		}catch(Exception e){}

		return riferito;
	}

	@Transient
	public String getRiferitoBanner(){

		String ret = "";

		try {
			if(getProtocollo()!=null) {
				for(Protocollo prot:getProtocollo()) { 
					if(prot.getIsMaster() && prot.getRiferimento()!=null && prot.getRiferimento().getCode()!=null){
						//NonPrevisto, Ditta, Interno, Utente, Cantiere
						CodeValue riferimento = prot.getRiferimento();

						if (riferimento != null){
							String code = riferimento.getCode();
							if (code != null){
								if (code.equals("NonPrevisto"))
									return " (" + riferimento.getCurrentTranslation() + ")";

								if (code.equals("Altro")){
									if (prot.getRiferimentoEntita()!=null){
										String altro = this.getAltro(prot.getRiferimentoEntita().getCurrentTranslation(), prot.getRiferimentoIMO(), prot.getRiferimentoTarga());
										ret = altro.substring(2);
									}
									return ret;
								}

								if (code.equals("Cantiere")) {
									Cantiere c = prot.getRiferimentoCantiere();
									if (c!=null && c.getAddr()!=null){
										ret += " " + c.getAddr();

									}

									return ret + " (" + riferimento.getCurrentTranslation() + ")";
								}

								if (code.equals("Ditta")) {
									PersoneGiuridiche pg = prot.getRiferimentoDitta();
									if (pg!=null && pg.getDenominazione()!=null){
										ret += " " + pg.getDenominazione();

										Sedi sede = prot.getRiferimentoSede();
										if (sede!=null && sede.getDenominazioneUnitaLocale()!=null)
											ret += " sede: " + sede.getDenominazioneUnitaLocale();
									}

									return ret + " (" + riferimento.getCurrentTranslation() + ")";
								}

								if (code.equals("Interno")) {
									Employee interno = prot.getRiferimentoInterno();
									if (interno!=null){
										ret += " " + interno.getName().getFam() + " " + interno.getName().getGiv();
										//+ " CF: " + interno.getFiscalCode();
									}

									return ret + " (" + riferimento.getCurrentTranslation() + ")";
								}

								if (code.equals("Utente")) {
									Person person = prot.getRiferimentoUtente();
									if (person!=null){
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										ret += " " + person.getName().getFam() + " " + person.getName().getGiv();
										if(person.getBirthTime()!=null){
											ret+=" (nato il "+sdf.format(person.getBirthTime())+")";
										}
										//+ " CF: " + person.getFiscalCode();
									}

									return ret + " (" + riferimento.getCurrentTranslation() + ")";
								}

							}
						}


					}
				}
			}

		} catch(Exception e){}

		return ret;
	}

	@Transient
	public String getAltro(String riferimentoEntita, String riferimentoIMO, String riferimentoTarga){
		String ret = "A:";

		try{
			if (riferimentoEntita==null)
				return ret;

			ret += riferimentoEntita;

			if (riferimentoEntita.equals("Mezzo automobilistico") && riferimentoTarga!=null && riferimentoTarga!="")
				ret += " (" + riferimentoTarga + ")";
			else if (riferimentoEntita.equals("Natante") && riferimentoIMO!=null && riferimentoIMO!="")
				ret += " (" + riferimentoIMO + ")";



		} catch (Exception ex) {}
		return ret;
	}


	@Transient
	public Date getUtenteBday(){

		Date ret = null;

		try {
			if(getProtocollo()!=null) {
				for(Protocollo prot:getProtocollo()) { 
					if(prot.getIsMaster() && prot.getRiferimento()!=null && prot.getRiferimento().getCode()!=null){
						if(prot.getRiferimento().getCode().equalsIgnoreCase("Utente"))
							ret = prot.getRiferimentoUtente().getBirthTime();
					}
				}
			}

		} catch(Exception e){}

		return ret;
	}

	@Transient
	public String getPrintNumeroProtocollo(){

		String ret = ""; 

		try{	

			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){
					if(prot.getIsMaster()){
						if(prot.getNprotocollo()!=null)
							ret=prot.getNprotocollo().toString();
						break;
					}
				}
			}

		}catch(Exception e){}

		return ret;

	}

	@Transient
	public String getPrintNumeroAlt(){
		String ret = ""; 
		try{	
			if(numero!=null){
				String lst[] = (numero.split("\\_\\d{4}\\_"));
				if (lst!=null && lst.length==2)
					ret = lst[1];
			}
		} catch(Exception e){}

		return ret;
	}

	@Transient
	public String getPrintNumeroFascicolo(){

		String ret = ""; 

		try{	

			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){
					if(prot.getIsMaster()){
						Procpratiche p = prot.getProcpratiche();
						if(p!=null){
							ret = p.getNumero();
							if(ret!=null){
								//ret=ret.substring(ret.lastIndexOf("_")+1);
								ret=ret.substring(ret.indexOf("_")+1);
							}
						}
					}
				}
			}

		}catch(Exception e){}

		return ret;

	}

	@Transient
	public String getOggetto(){

		String ret =""; 

		try{

			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){
					if(prot.getIsMaster()){
						if(prot.getOggetto()!=null)
							ret=prot.getOggetto();
						break;
					}
				}
			}

		}catch(Exception e){}

		return ret;

	}

	@PostPersist
	@PostUpdate
	public void updateAssociations() {
		GenericAdapterLocalInterface ga=null;
		try {
			ga = GenericAdapter.instance();
		} catch (NamingException e1) {
			// sto eseguendo l'importer
		}
		if(ga==null)
			return;
		try {
			Map<String,Object> pars = new HashMap<String, Object>();
			pars.put("internalId",BigInteger.valueOf(internalId));
			FunctionsBean function = FunctionsBean.instance();
			
			PraticheRiferimenti praticheRiferimenti = this.praticheRiferimenti;
			if(praticheRiferimenti==null)
				praticheRiferimenti=PraticheRiferimentiAction.instance().getEntity();
			
			List<Attivita> attivita = this.attivita;
			if(attivita==null || attivita.isEmpty()) {
				if(Contexts.getConversationContext().get("AttivitaList")!=null){
					attivita=((IdataModel)Contexts.getConversationContext().get("AttivitaList")).getList();
				}
			}
				

			MedicinaLavoro medicinaLavoro = this.medicinaLavoro;
			if(medicinaLavoro==null)
				medicinaLavoro=MedicinaLavoroAction.instance().getEntity();
			
			Vigilanza vigilanza = this.vigilanza;
			if(vigilanza==null)
				vigilanza=VigilanzaAction.instance().getEntity();
			
			MalattiaProfessionale malattiaProfessionale = this.malattiaProfessionale;
			if(malattiaProfessionale==null)
				malattiaProfessionale=MalattiaProfessionaleAction.instance().getEntity();
			
			//GESTIONE FILTRI
 			ga.executeUpdateNative("delete from persone_associazioni where procpratiche_id = :internalId", pars);
			ga.executeUpdateNative("delete from ditte_associazioni where procpratiche_id = :internalId", pars);
			ga.executeUpdateNative("delete from cantieri_associazioni where procpratiche_id = :internalId", pars);

			if(serviceDeliveryLocation!=null && serviceDeliveryLocation.getArea()!=null && isActive && !("nullified".equals(statusCode.getCode()))){
				//PRATICHE

				String area = serviceDeliveryLocation.getArea().getCode();
				if("WORKACCIDENT".equals(area)){
					if(infortuni!=null){
						for(Infortuni inf : infortuni){
							if(inf.isActive){
								//infortunato
								if(inf.getPerson()!=null){
									ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+inf.getPerson().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.painfortuni1_V0')",null);
								}
								//ditta infortunato
								if(inf.getPersoneGiuridiche()!=null){
									ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+inf.getPersoneGiuridiche().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni1_V0')",null);
								}
								//luogo dell'infortunio-ditta
								if(inf.getPlace()!=null && "OwnCompany".equals(inf.getPlace().getCode()) && inf.getPersoneGiuridiche()!=null){
									ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+inf.getPersoneGiuridiche().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni2_V0')",null);
								}
								//luogo dell'infortunio-ditta
								if(inf.getPlace()!=null && "Company".equals(inf.getPlace().getCode()) && inf.getPersoneGiuridicheExt()!=null){
									ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+inf.getPersoneGiuridicheExt().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni2_V0')",null);
								}
								//luogo dell'infortunio-cantiere
								else if(inf.getPlace()!=null && "Yard".equals(inf.getPlace().getCode()) && inf.getCantiere()!=null){
									ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+inf.getCantiere().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.cainfortuni1_V0')",null);

								}
							}
						}
					}
				}
				else if("WORKDISEASE".equals(area)){
					if(malattiaProfessionale!=null && malattiaProfessionale.isActive){
						//malato utente
						if(malattiaProfessionale.getRiferimento()!=null && "Utente".equals(malattiaProfessionale.getRiferimento().getCode())
								&& malattiaProfessionale.getRiferimentoUtente()!=null){
							ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+malattiaProfessionale.getRiferimentoUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.pamalattia1_V0')",null);
						}
						//ditta di attribuzione malattia
						if(malattiaProfessionale.getDitteMalattie()!=null){
							for(DitteMalattie dmal : malattiaProfessionale.getDitteMalattie()){
								if(dmal.getPersoneGiuridiche()!=null && dmal.getIsActive() && dmal.getPersoneGiuridiche().getIsActive()){
									ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+dmal.getPersoneGiuridiche().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.damalattia1_V0')",null);

								}
							}			
						}
					}
				}
				else if("SUPERVISION".equals(area)){
					if(vigilanza!=null && vigilanza.isActive && vigilanza.getType()!=null){

						//ditta vigilata
						if("Generic".equals(vigilanza.getType().getCode()) && vigilanza.getPersonaGiuridicaSede()!=null){
							for(PersonaGiuridicaSede pgss : vigilanza.getPersonaGiuridicaSede()){
								if(pgss.getPersonaGiuridica()!=null && pgss.getIsActive()){
									ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+pgss.getPersonaGiuridica().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.davigiazienda1_V0')",null);
									}
							}
						}
						else if("Yard".equals(vigilanza.getType().getCode())){
							if(vigilanza.getCantiere()!=null && vigilanza.getCantiere().getIsActive()){
								Cantiere cant= vigilanza.getCantiere();

								if(cant.getIsActive() && cant.getCommittente()!=null ){
									//committente del cantiere
									for(Committente comm: cant.getCommittente()){
										if(comm.getPerson()!=null){
											ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
													"values (1, 'phi-esb', '"+comm.getPerson().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.pavigicantieri2_V0')",null);
										}
									}
								}
								//cantieri controllati
								if(cant.getIsActive()){
									ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
											"values (1, 'phi-esb', '"+cant.getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.cavigicantieri1_V0')",null);
								}

							}
							if(vigilanza.getPersonaGiuridicaSede()!=null){
								for(PersonaGiuridicaSede pgs : vigilanza.getPersonaGiuridicaSede()){
									//ditte controllate
									if(pgs.getPersonaGiuridica()!=null && pgs.getIsActive() && pgs.getChecked()){

										ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+pgs.getPersonaGiuridica().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.davigicantieri1_V0')",null);

									}
								}
							}
						}else if("Asbestos".equals(vigilanza.getType().getCode())){
							
							Map<String,Object> parsCv = new HashMap<String, Object>();
							parsCv.put("isActive",true);
							parsCv.put("vigilanza", vigilanza);

							List<CommittenteVigilanza> cvlist = ga.executeHQL("select cv from CommittenteVigilanza cv where cv.isActive=:isActive and cv.vigilanza=:vigilanza", parsCv);
							if (cvlist != null && cvlist.size() > 0) {
								CommittenteVigilanza cv = cvlist.get(0);
								if (cv != null && cv.getCommittente()!=null){
									//committente della bonifica utente
									if("Utente".equals(cv.getCommittente().getCode()) && cv.getCommittenteUtente()!=null) {
										ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+cv.getCommittenteUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.pavigiamianto2_V0')",null);
									//committente ditta della bonifica
									}else if("Ditta".equals(cv.getCommittente().getCode()) && cv.getCommittenteDitta() !=null) {
										ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+cv.getCommittenteDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto3_V0')",null);
									}
								}				
							}

							//ditta lavori
							if(vigilanza.getPersonaGiuridicaSede()!=null){
								for(PersonaGiuridicaSede pgs : vigilanza.getPersonaGiuridicaSede()){
									if(pgs!=null && pgs.getPersonaGiuridica()!=null && pgs.getIsActive()){
										ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+pgs.getPersonaGiuridica().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto1_V0')",null);
	
										break;//solo la prima
									}
								}
							}						
							
							Protocollo prot = ProtocolloAction.instance().getEntity();
							//Ditta sito della bonifica
							if(prot!=null && prot.getUbicazione()!=null && "Ditta".equals(prot.getUbicazione().getCode()) && 
									vigilanza.getSitoBonificaDitta()!=null && vigilanza.getSitoBonificaDitta().getIsActive()){
								ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
										"values (1, 'phi-esb', '"+vigilanza.getSitoBonificaDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.davigiamianto2_V0')",null);

							}
							
							//cantiere Sito della bonifica
							if(prot!=null && prot.getUbicazione()!=null && "Cantiere".equals(prot.getUbicazione().getCode()) && 
									vigilanza.getCantiere()!=null && vigilanza.getCantiere().getIsActive()){
								ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
										"values (1, 'phi-esb', '"+vigilanza.getCantiere().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.cavigiamianto1_v0')",null);
							}
						}
					}
				}
				else if("TECHNICALADVICE".equals(area) ){
					
					if(praticheRiferimenti!=null && praticheRiferimenti.isActive){
						//richiedente ditta
						if(praticheRiferimenti.getRichiedente()!=null && "Ditta".equals(praticheRiferimenti.getRichiedente().getCode()) && 
								praticheRiferimenti.getRichiedenteDitta()!=null){
							ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+praticheRiferimenti.getRichiedenteDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.daespressione1_V0')",null);


						}
						
						//richiedente utente
						if(praticheRiferimenti.getRichiedente()!=null && "Utente".equals(praticheRiferimenti.getRichiedente().getCode()) && 
								praticheRiferimenti.getRichiedenteUtente()!=null){
							ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+praticheRiferimenti.getRichiedenteUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.paespressione1_V0')",null);
						}

						//riferito a utente per il resto di area 
						if(praticheRiferimenti.getRiferimento()!=null && "Utente".equals(praticheRiferimenti.getRiferimento().getCode()) && 
								praticheRiferimenti.getRiferimentoUtente()!=null){
							ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.pavigiespressione2_V0')",null);
						}
				
						//riferito a Ditta per il resto di area
						if(praticheRiferimenti.getRiferimento()!=null && "Ditta".equals(praticheRiferimenti.getRiferimento().getCode()) && 
								praticheRiferimenti.getRiferimentoDitta()!=null){
							ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.daespressione2_V0')",null);
						}
						//riferito a cantiere per il resto di area
						if(praticheRiferimenti.getRiferimento()!=null && "Cantiere".equals(praticheRiferimenti.getRiferimento().getCode()) && 
								praticheRiferimenti.getRiferimentoCantiere()!=null){
							ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoCantiere().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.caespressione1_V0')",null);
						}
					}
					
				}
				else if(function.hasCodeIn(area, "GENERIC","INFORMATION","LIFESTYLE","TRAINING","COUNSELING")){
					
					//riferito a utente per il resto di area 
					if(praticheRiferimenti.getRiferimento()!=null && "Utente".equals(praticheRiferimenti.getRiferimento().getCode()) && 
							praticheRiferimenti.getRiferimentoUtente()!=null){
						ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.pavigiespressione2_V0')",null);
					}
			
					//riferito a Ditta per il resto di area
					if(praticheRiferimenti.getRiferimento()!=null && "Ditta".equals(praticheRiferimenti.getRiferimento().getCode()) && 
							praticheRiferimenti.getRiferimentoDitta()!=null){
						ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.daespressione2_V0')",null);
					}
					//riferito a cantiere per il resto di area
					if(function.hasCodeIn(area, "GENERIC","INFORMATION","TRAINING") && praticheRiferimenti.getRiferimento()!=null && "Cantiere".equals(praticheRiferimenti.getRiferimento().getCode()) && 
							praticheRiferimenti.getRiferimentoCantiere()!=null){
						ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+praticheRiferimenti.getRiferimentoCantiere().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.caespressione1_V0')",null);
					}
				}
				else if ("WORKMEDICINE".equals(area)){
					if(medicinaLavoro!=null && medicinaLavoro.isActive){
						//utente/lavoratore
						if(medicinaLavoro.getPatient()!=null){
							ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
									"values (1, 'phi-esb', '"+medicinaLavoro.getPatient().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.personeassociation.pamedicina1_V0')",null);
						}
					}		
					//ditta appartenenza
					if(medicinaLavoro.getDittaAttuale()!=null){
						ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+medicinaLavoro.getDittaAttuale().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.ditteassociation.damedicina1_V0')",null);
					}
					// ditta attuale del utente
					else if(protocollo.size() > 0 ){
						for(Protocollo prot: protocollo){
							if (prot.getDittaAttualeUtente()!=null){
								ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
										"values (1, 'phi-esb', '"+prot.getDittaAttualeUtente().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.ditteassociation.damedicina2_V0')",null);
							}
						}
					}
				}
				if(function.hasCodeIn(area,"INFORMATION","TRAINING","COUNSELING","GENERIC") ){
					
					//ubicazione della pratica ditta
					if(function.hasCodeIn(area,"INFORMATION","TRAINING","COUNSELING") && praticheRiferimenti.getUbicazione()!=null && "Ditta".equals(praticheRiferimenti.getUbicazione().getCode()) && 
							praticheRiferimenti.getUbicazioneDitta()!=null){
						ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+praticheRiferimenti.getUbicazioneDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.daformazione2_V0')",null);
					}
					if(function.hasCodeIn(area,"GENERIC","INFORMATION","TRAINING") && praticheRiferimenti.getUbicazione()!=null && "Cantiere".equals(praticheRiferimenti.getUbicazione().getCode()) && 
							praticheRiferimenti.getUbicazioneCantiere()!=null){
						ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
								"values (1, 'phi-esb', '"+praticheRiferimenti.getUbicazioneCantiere().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.cantieriassociation.caattivita2_V0')",null);
					}
				}
				if(attivita!=null){
					for(Attivita a : attivita){
						if(a.isActive){
							//attivita elementare
							//luogo ditta
							if(function.hasCodeIn(area, "WORKMEDICINE","GENERIC","TRAINING","INFORMATION","LIFESTYLE","COUNSELING","SUPERVISION", "TECHNICALADVICE", "WORKDISEASE", "WORKACCIDENT")  && a.getLuogo()!=null && "Ditta".equals(a.getLuogo().getCode()) && a.getLuogoDitta()!=null){
								ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
										"values (1, 'phi-esb', '"+a.getLuogoDitta().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni3_V0')",null);
							}
							//luogo cantiere
							if(function.hasCodeIn(area, "WORKMEDICINE","GENERIC","TRAINING","INFORMATION","LIFESTYLE","COUNSELING","SUPERVISION", "TECHNICALADVICE", "WORKDISEASE") && a.getLuogo()!=null && "Cantiere".equals(a.getLuogo().getCode()) && a.getLuogoCantiere()!=null){
								ga.executeUpdateNative("insert into cantieri_associazioni (is_active, created_by, cantiere_id, procpratiche_id, type) " +
										"values (1, 'phi-esb', '"+a.getLuogoCantiere().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.cantieriassociation.camalattia1_V0')",null);
							}
							if(a.getSoggetto()!=null){
								for(Soggetto s : a.getSoggetto()){
									//soggetto ditta
									if(function.hasCodeIn(area, "WORKMEDICINE","GENERIC","TRAINING","INFORMATION","LIFESTYLE","COUNSELING","SUPERVISION", "TECHNICALADVICE", "WORKDISEASE", "WORKACCIDENT") 
											&& s.getCode()!=null && "Ditta".equals(s.getCode().getCode()) && s.getDitta()!=null){
										ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+s.getDitta().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni3_V0')",null);


									}
									//soggetto utente
									else if(function.hasCodeIn(area, "WORKMEDICINE","GENERIC","TRAINING","INFORMATION","LIFESTYLE","COUNSELING") &&
											s.getCode()!=null && "Utente".equals(s.getCode().getCode()) && s.getUtente()!=null){
										ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
												"values (1, 'phi-esb', '"+s.getUtente().getInternalId()+"', "+internalId+",'phidic.spisal.tipoassociazioni.personeassociation.paattivita2_V0')",null);
									}
								}
							}
							//provvedimenti
							if(a.getProvvedimenti()!=null && a.isActive){
								for(Provvedimenti p : a.getProvvedimenti()){
									if(function.hasCodeIn(area, "SUPERVISION", "WORKDISEASE", "WORKACCIDENT") && p.getIsActive() && p.getSoggetto()!=null && p.getSoggetto().getIsActive() && p.getSoggetto().getCode()!=null){
										//ditta//provvedimento
										if("Ditta".equals(p.getSoggetto().getCode().getCode()) && p.getSoggetto().getDitta()!=null) {
											ga.executeUpdateNative("insert into ditte_associazioni (is_active, created_by, persone_giuridiche_id, procpratiche_id, type) " +
													"values (1, 'phi-esb', '"+p.getSoggetto().getDitta().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni4_V0')",null);
											
											//GUARDO ANCHE IL DESTINATARIO
											if(p.getCarica()!=null && p.getCarica().getSediPersone()!=null && p.getCarica().getSediPersone()!=null) {
												ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, sedi_persone_id, procpratiche_id, type) " +
														"values (1, 'phi-esb', null, '"+p.getCarica().getSediPersone().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.ditteassociation.dainfortuni2_V0')",null);
											}
											
											//utente
										}else if("Utente".equals(p.getSoggetto().getCode().getCode()) && p.getSoggetto().getUtente()!=null) {
											ga.executeUpdateNative("insert into persone_associazioni (is_active, created_by, person_id, procpratiche_id, type) " +
													"values (1, 'phi-esb', '"+p.getSoggetto().getUtente().getInternalId()+"', "+internalId+", 'phidic.spisal.tipoassociazioni.personeassociation.paattivita2_V0')",null);

										}
									}
								}
							}
						}
					}
				}


				//GESTIONE PALLINI (CONTATORI)

				ga.executeUpdateNative("delete from ditte_pratiche where pratica_id = :internalId", pars);
				ga.executeUpdateNative("delete from ditte_provvedimenti where pratica_id = :internalId", pars);
				ga.executeUpdateNative("delete from persone_pratiche where pratica_id = :internalId", pars);
				ga.executeUpdateNative("delete from persone_provvedimenti where pratica_id = :internalId", pars);
				if(serviceDeliveryLocation!=null && serviceDeliveryLocation.getArea()!=null && isActive && 
						!("nullified".equals(statusCode.getCode()))){
					//PRATICHE
					Set<String> cfsDitte = new HashSet<String>();
					Map<String, List<Long>> cfsDitteMap = new HashMap<String, List<Long>>();
					Set<String> cfsPersone = new HashSet<String>();
					Map<String, List<Long>> cfsPersoneMap = new HashMap<String, List<Long>>();


					if("WORKACCIDENT".equals(area)){
						if(infortuni!=null){
							for(Infortuni inf : infortuni){
								if(inf.isActive){
									if(inf.getPerson()!=null){
										String key=inf.getPerson().getFiscalCode();
										cfsPersone.add(key);
										if(cfsPersoneMap.get(key)==null)
											cfsPersoneMap.put(key, new ArrayList<Long>());
										cfsPersoneMap.get(key).add(inf.getPerson().getInternalId());
									}
									if(inf.getPersoneGiuridiche()!=null){
										String key=inf.getPersoneGiuridiche().getCodiceFiscale();
										cfsDitte.add(key);
										if(cfsDitteMap.get(key)==null)
											cfsDitteMap.put(key, new ArrayList<Long>());
										cfsDitteMap.get(key).add(inf.getPersoneGiuridiche().getInternalId());
									}
									if(inf.getPlace()!=null && "Company".equals(inf.getPlace().getCode()) && inf.getPersoneGiuridicheExt()!=null){
										String key=inf.getPersoneGiuridicheExt().getCodiceFiscale();
										cfsDitte.add(key);
										if(cfsDitteMap.get(key)==null)
											cfsDitteMap.put(key, new ArrayList<Long>());
										cfsDitteMap.get(key).add(inf.getPersoneGiuridicheExt().getInternalId());
									}
								}
							}
						}
					}else if("WORKDISEASE".equals(area)){
						if(malattiaProfessionale!=null && malattiaProfessionale.isActive){
							if(malattiaProfessionale.getRiferimento()!=null && "Utente".equals(malattiaProfessionale.getRiferimento().getCode())
									&& malattiaProfessionale.getRiferimentoUtente()!=null){
								String key=malattiaProfessionale.getRiferimentoUtente().getFiscalCode();
								cfsPersone.add(key);
								if(cfsPersoneMap.get(key)==null)
									cfsPersoneMap.put(key, new ArrayList<Long>());
								cfsPersoneMap.get(key).add(malattiaProfessionale.getRiferimentoUtente().getInternalId());
							}
							if(malattiaProfessionale.getDitteMalattie()!=null){
								for(DitteMalattie dmal : malattiaProfessionale.getDitteMalattie()){
									if(dmal.getPersoneGiuridiche()!=null){
										String key=dmal.getPersoneGiuridiche().getCodiceFiscale();
										cfsDitte.add(key);
										if(cfsDitteMap.get(key)==null)
											cfsDitteMap.put(key, new ArrayList<Long>());
										cfsDitteMap.get(key).add(dmal.getPersoneGiuridiche().getInternalId());
									}
								}			
							}
						}
					}else if("SUPERVISION".equals(area)){
						if(vigilanza!=null && vigilanza.isActive && vigilanza.getType()!=null){
							if(vigilanza.getPersonaGiuridicaSede()!=null){
								if("Yard".equals(vigilanza.getType().getCode())){
									for(PersonaGiuridicaSede pgs : vigilanza.getPersonaGiuridicaSede()){
										if(pgs.getPersonaGiuridica()!=null && pgs.getIsActive()){
											if(pgs.getChecked()){//ditte controllate
												String key=pgs.getPersonaGiuridica().getCodiceFiscale();
												cfsDitte.add(key);
												if(cfsDitteMap.get(key)==null)
													cfsDitteMap.put(key, new ArrayList<Long>());
												cfsDitteMap.get(key).add(pgs.getPersonaGiuridica().getInternalId());
											}
										}
									}
								}else if("Asbestos".equals(vigilanza.getType().getCode())){
									for(PersonaGiuridicaSede pgs : vigilanza.getPersonaGiuridicaSede()){
										if(pgs.getPersonaGiuridica()!=null && pgs.getIsActive()){
											String key=pgs.getPersonaGiuridica().getCodiceFiscale();
											cfsDitte.add(key);
											if(cfsDitteMap.get(key)==null)
												cfsDitteMap.put(key, new ArrayList<Long>());
											cfsDitteMap.get(key).add(pgs.getPersonaGiuridica().getInternalId());
											break;//solo la prima
										}
									}
								}else if("Generic".equals(vigilanza.getType().getCode())){
									for(PersonaGiuridicaSede pgs : vigilanza.getPersonaGiuridicaSede()){
										if(pgs.getPersonaGiuridica()!=null && pgs.getIsActive()){

											String key=pgs.getPersonaGiuridica().getCodiceFiscale();
											cfsDitte.add(key);
											if(cfsDitteMap.get(key)==null)
												cfsDitteMap.put(key, new ArrayList<Long>());
											cfsDitteMap.get(key).add(pgs.getPersonaGiuridica().getInternalId());
										}
									}
								}
							}
							if(attivita!=null){
								for(Attivita a : attivita){
									if(a.isActive){
										if(a.getLuogo()!=null && "Ditta".equals(a.getLuogo().getCode()) && a.getLuogoDitta()!=null){
											String key=a.getLuogoDitta().getCodiceFiscale();
											cfsDitte.add(key);
											if(cfsDitteMap.get(key)==null)
												cfsDitteMap.put(key, new ArrayList<Long>());
											cfsDitteMap.get(key).add(a.getLuogoDitta().getInternalId());
										}
										if(a.getSoggetto()!=null){
											for(Soggetto s : a.getSoggetto()){
												if(s.getCode()!=null && "Ditta".equals(s.getCode().getCode()) && s.getDitta()!=null){
													String key=s.getDitta().getCodiceFiscale();
													cfsDitte.add(key);
													if(cfsDitteMap.get(key)==null)
														cfsDitteMap.put(key, new ArrayList<Long>());
													cfsDitteMap.get(key).add(s.getDitta().getInternalId());
												}
											}
										}
									}
								}
							}
						}
					}else if("TECHNICALADVICE".equals(area)){
						
						if(praticheRiferimenti!=null && praticheRiferimenti.isActive){
							if(praticheRiferimenti.getRichiedente()!=null && "Ditta".equals(praticheRiferimenti.getRichiedente().getCode()) && 
									praticheRiferimenti.getRichiedenteDitta()!=null){
								String key=praticheRiferimenti.getRichiedenteDitta().getCodiceFiscale();
								cfsDitte.add(key);
								if(cfsDitteMap.get(key)==null)
									cfsDitteMap.put(key, new ArrayList<Long>());
								cfsDitteMap.get(key).add(praticheRiferimenti.getRichiedenteDitta().getInternalId());
							}
						}
					}else if ("WORKMEDICINE".equals(area)){
						if(medicinaLavoro!=null && medicinaLavoro.isActive){
							if(medicinaLavoro.getPatient()!=null){
								String key=medicinaLavoro.getPatient().getFiscalCode();
								cfsPersone.add(key);
								if(cfsPersoneMap.get(key)==null)
									cfsPersoneMap.put(key, new ArrayList<Long>());
								cfsPersoneMap.get(key).add(medicinaLavoro.getPatient().getInternalId());
							}
						}					
					}

					for(String cf : cfsDitte){
						if(cf!=null && !cf.isEmpty() && !cf.trim().isEmpty()){
							for(Long id : cfsDitteMap.get(cf)){
								ga.executeUpdateNative("insert into ditte_pratiche (is_active, created_by, cf, sdl_id, pratica_id, ditta_id) " +
										"values (1, 'phi-esb', '"+cf+"', "+serviceDeliveryLocation.getInternalId()+","+internalId+","+id+" )",null);
							}
						}
					}

					for(String cf : cfsPersone){
						if(cf!=null && !cf.isEmpty() && !cf.trim().isEmpty()){
							for(Long id : cfsPersoneMap.get(cf)){
								ga.executeUpdateNative("insert into persone_pratiche (is_active, created_by, cf, sdl_id, pratica_id, pers_id) " +
										"values (1, 'phi-esb', '"+cf+"', "+serviceDeliveryLocation.getInternalId()+","+internalId+","+id+" )",null);
							}
						}
					}

					//PROVVEDIMENTI
					if(attivita!=null){
						for(Attivita a : attivita){
							if(a.getProvvedimenti()!=null && a.isActive){
								for(Provvedimenti p : a.getProvvedimenti()){
									if(p.getIsActive() && p.getSoggetto()!=null && p.getSoggetto().getIsActive() && p.getSoggetto().getCode()!=null){
										if("Ditta".equals(p.getSoggetto().getCode().getCode()) && p.getSoggetto().getDitta()!=null
												&& p.getSoggetto().getDitta().getCodiceFiscale()!=null){

											String cf = p.getSoggetto().getDitta().getCodiceFiscale();
											if(cf!=null && !cf.isEmpty() && !cf.trim().isEmpty()){
												ga.executeUpdateNative("insert into ditte_provvedimenti (is_active, created_by, cf, sdl_id, pratica_id, prov_id, ditta_id) " +
														"values (1, 'phi-esb', '"+cf+"', "+serviceDeliveryLocation.getInternalId()+","+internalId+", "+
														p.getInternalId()+","+p.getSoggetto().getDitta().getInternalId()+" )",null);
											}
											//GUARDO ANCHE IL DESTINATARIO
											if(p.getCarica()!=null && p.getCarica().getSediPersone()!=null && p.getCarica().getSediPersone().getFiscalCode()!=null){
												cf = p.getCarica().getSediPersone().getFiscalCode();
												if(cf!=null && !cf.isEmpty() && !cf.trim().isEmpty()){
													ga.executeUpdateNative("insert into persone_provvedimenti (is_active, created_by, cf, sdl_id, pratica_id, prov_id) " +
															"values (1, 'phi-esb', '"+cf+"', "+serviceDeliveryLocation.getInternalId()+","+internalId+", "+
															p.getInternalId()+")",null);
												}
											}

										}else if("Utente".equals(p.getSoggetto().getCode().getCode()) && p.getSoggetto().getUtente()!=null
												&& p.getSoggetto().getUtente().getFiscalCode()!=null){

											String cf = p.getSoggetto().getUtente().getFiscalCode();
											if(cf!=null && !cf.isEmpty() && !cf.trim().isEmpty()){
												ga.executeUpdateNative("insert into persone_provvedimenti (is_active, created_by, cf, sdl_id, pratica_id, prov_id, pers_id) " +
														"values (1, 'phi-esb', '"+cf+"', "+serviceDeliveryLocation.getInternalId()+","+internalId+", "+
														p.getInternalId()+","+p.getSoggetto().getUtente().getInternalId()+" )",null);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Transient
	public String getTipoComunicazione(){
		String ret =""; 
		try {
			if(getProtocollo()!=null){
				for(Protocollo prot:getProtocollo()){
					if(prot.getIsMaster()){
						if(prot.getCode()!=null)
							ret = prot.getCode().getCurrentTranslation();
						break;
					}
				}
			}

		} catch (Exception e) { e.printStackTrace(); }

		return ret;

	}
}
