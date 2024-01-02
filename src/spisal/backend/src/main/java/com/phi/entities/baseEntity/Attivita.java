package com.phi.entities.baseEntity;

/**
 * Attività generated by hbm2java
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Query;
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

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.actions.ProcpraticheAction;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Operatore;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;

import com.phi.entities.baseEntity.ControlLsReq;


import org.hibernate.envers.AuditJoinTable;
//import com.phi.entities.baseEntity.Questionario;
@Entity
@Table(name = "attivita")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Attivita extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8374947802356756345L;


	/**
	*  javadoc for questionario
	*/
//	private List<Questionario> questionario;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita"/*, cascade=CascadeType.PERSIST*/)
//	public List<Questionario> getQuestionario(){
//		return questionario;
//	}
//
//	public void setQuestionario(List<Questionario> list){
//		questionario = list;
//	}
//
//	public void addQuestionario(Questionario questionario) {
//		if (this.questionario == null) {
//			this.questionario = new ArrayList<Questionario>();
//		}
//		// add the association
//		if(!this.questionario.contains(questionario)) {
//			this.questionario.add(questionario);
//			// make the inverse link
//			questionario.setAttivita(this);
//		}
//	}
//
//	public void removeQuestionario(Questionario questionario) {
//		if (this.questionario == null) {
//			this.questionario = new ArrayList<Questionario>();
//			return;
//		}
//		//add the association
//		if(this.questionario.contains(questionario)){
//			this.questionario.remove(questionario);
//			//make the inverse link
//			questionario.setAttivita(null);
//		}
//
//	}


	/**
	*  javadoc for controlLsReq
	*/
	private ControlLsReq controlLsReq;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="control_ls_req_id")
	@ForeignKey(name="FK_Attivita_controlLsReq")
	@Index(name="IX_Attivita_controlLsReq")
	public ControlLsReq getControlLsReq(){
		return controlLsReq;
	}

	public void setControlLsReq(ControlLsReq controlLsReq){
		this.controlLsReq = controlLsReq;
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
	*  javadoc for latitudine
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
	*  javadoc for commissione
	*/
	private Commissioni commissione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="commissione_id")
	@ForeignKey(name="FK_Attivita_commissione")
	//@Index(name="IX_Attivita_commissione")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public Commissioni getCommissione(){
		return commissione;
	}

	public void setCommissione(Commissioni commissione){
		this.commissione = commissione;
	}



	/**
	*  javadoc for parent
	*/
	private Attivita parent;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="parent_id")
	@ForeignKey(name="FK_Attivita_parent")
	//@Index(name="IX_Attivita_parent")
	public Attivita getParent(){
		return parent;
	}

	public void setParent(Attivita parent){
		this.parent = parent;
	}

	/**
	*  javadoc for children
	*/
	private List<Attivita> children;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="parent", cascade=CascadeType.PERSIST)
	public List<Attivita> getChildren() {
		return children;
	}

	public void setChildren(List<Attivita>list){
		children = list;
	}

	public void addChildren(Attivita children) {
		if (this.children == null) {
			this.children = new ArrayList<Attivita>();
		}
		// add the association
		if(!this.children.contains(children)) {
			this.children.add(children);
			// make the inverse link
			children.setParent(this);
		}
	}

	public void removeChildren(Attivita children) {
		if (this.children == null) {
			this.children = new ArrayList<Attivita>();
			return;
		}
		//add the association
		if(this.children.contains(children)){
			this.children.remove(children);
			//make the inverse link
			children.setParent(null);
		}
	}



	/**
	*  javadoc for couselling
	*/
	private List<Couselling> couselling;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<Couselling> getCouselling() {
		return couselling;
	}

	public void setCouselling(List<Couselling>list){
		couselling = list;
	}

	public void addCouselling(Couselling couselling) {
		if (this.couselling == null) {
			this.couselling = new ArrayList<Couselling>();
		}
		// add the association
		if(!this.couselling.contains(couselling)) {
			this.couselling.add(couselling);
			// make the inverse link
			couselling.setAttivita(this);
		}
	}

	public void removeCouselling(Couselling couselling) {
		if (this.couselling == null) {
			this.couselling = new ArrayList<Couselling>();
			return;
		}
		//add the association
		if(this.couselling.contains(couselling)){
			this.couselling.remove(couselling);
			//make the inverse link
			couselling.setAttivita(null);
		}
	}



	/**
	*  javadoc for accertaMdl
	*/
	private List<AccertaMdl> accertaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<AccertaMdl> getAccertaMdl() {
		return accertaMdl;
	}

	public void setAccertaMdl(List<AccertaMdl>list){
		accertaMdl = list;
	}

	public void addAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
		}
		// add the association
		if(!this.accertaMdl.contains(accertaMdl)) {
			this.accertaMdl.add(accertaMdl);
			// make the inverse link
			accertaMdl.setAttivita(this);
		}
	}

	public void removeAccertaMdl(AccertaMdl accertaMdl) {
		if (this.accertaMdl == null) {
			this.accertaMdl = new ArrayList<AccertaMdl>();
			return;
		}
		//add the association
		if(this.accertaMdl.contains(accertaMdl)){
			this.accertaMdl.remove(accertaMdl);
			//make the inverse link
			accertaMdl.setAttivita(null);
		}
	}



	/**
	*  javadoc for visitaMdl
	*/
	private List<VisitaMdl> visitaMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<VisitaMdl> getVisitaMdl() {
		return visitaMdl;
	}

	public void setVisitaMdl(List<VisitaMdl>list){
		visitaMdl = list;
	}

	public void addVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
		}
		// add the association
		if(!this.visitaMdl.contains(visitaMdl)) {
			this.visitaMdl.add(visitaMdl);
			// make the inverse link
			visitaMdl.setAttivita(this);
		}
	}

	public void removeVisitaMdl(VisitaMdl visitaMdl) {
		if (this.visitaMdl == null) {
			this.visitaMdl = new ArrayList<VisitaMdl>();
			return;
		}
		//add the association
		if(this.visitaMdl.contains(visitaMdl)){
			this.visitaMdl.remove(visitaMdl);
			//make the inverse link
			visitaMdl.setAttivita(null);
		}
	}



	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Attivita_id")
	@ForeignKey(name="FK_documenti_Attivita")
	//@Index(name="IX_documenti_Attivita")
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
			//Add also to parent entity
			if (this.procpratiche != null) {
				this.procpratiche.addDocumenti(alfrescoDocument);
			}
		}
	}
	
	public void removeDocumenti(AlfrescoDocument alfrescoDocument) {
		if (this.documenti == null) {
			return;
		}
		if(this.documenti.contains(alfrescoDocument)) {
			this.documenti.remove(alfrescoDocument);
			//Add also to parent entity
			if (this.procpratiche != null) {
				this.procpratiche.removeDocumenti(alfrescoDocument);
			}
		}
	}
	
	/**
	*  javadoc for numero
	*/
	private Long numero;

	@Column(name="numero")//, unique=true)
	public Long getNumero(){
		return numero;
	}

	public void setNumero(Long numero){
		this.numero = numero;
	}

	/**
	*  javadoc for typeText
	*/
	private String typeText;

	@Column(name="type_text")
	public String getTypeText(){
		return typeText;
	}

	public void setTypeText(String typeText){
		this.typeText = typeText;
	}


	private String numeroVerbale;
	
	@Column(name = "numero_verbale"/*, length = 20*/)
	public String getNumeroVerbale() {
		return this.numeroVerbale;
	}

	public void setNumeroVerbale(String numeroVerbale) {
		this.numeroVerbale = numeroVerbale;
	}
	
	/**
	*  javadoc for luogoAltro
	*/
	private Soggetto luogoAltro;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="luogo_altro_id")
	@ForeignKey(name="FK_Attivita_luogoAltro")
	//@Index(name="IX_Attivita_luogoAltro")
	public Soggetto getLuogoAltro(){
		return luogoAltro;
	}

	public void setLuogoAltro(Soggetto luogoAltro){
		this.luogoAltro = luogoAltro;
	}

	/**
	*  javadoc for miglioramenti
	*/
	private List<Miglioramenti> miglioramenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	public List<Miglioramenti> getMiglioramenti(){
		return miglioramenti;
	}

	public void setMiglioramenti(List<Miglioramenti> list){
		miglioramenti = list;
	}

	public void addMiglioramenti(Miglioramenti miglioramenti) {
		if (this.miglioramenti == null) {
			this.miglioramenti = new ArrayList<Miglioramenti>();
		}
		// add the association
		if(!this.miglioramenti.contains(miglioramenti)) {
			this.miglioramenti.add(miglioramenti);
			// make the inverse link
			miglioramenti.setAttivita(this);
		}
	}

	public void removeMiglioramenti(Miglioramenti miglioramenti) {
		if (this.miglioramenti == null) {
			this.miglioramenti = new ArrayList<Miglioramenti>();
			return;
		}
		//add the association
		if(this.miglioramenti.contains(miglioramenti)){
			this.miglioramenti.remove(miglioramenti);
			//make the inverse link
			miglioramenti.setAttivita(null);
		}

	}



	/**
	*  javadoc for acquisizioneInformazioni
	*/
	private AcquisizioneInformazioni acquisizioneInformazioni;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="acquisizione_informazioni_id")
	@ForeignKey(name="FK_ttvta_acquszoneInformzon")
	//@Index(name="IX_ttvta_acquszoneInformzon")
	public AcquisizioneInformazioni getAcquisizioneInformazioni(){
		return acquisizioneInformazioni;
	}

	public void setAcquisizioneInformazioni(AcquisizioneInformazioni acquisizioneInformazioni){
		this.acquisizioneInformazioni = acquisizioneInformazioni;
	}

	/**
	*  javadoc for visitaMedica
	*/
	private VisitaMedica visitaMedica;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REFRESH})
	@JoinColumn(name="visita_medica_id")
	@ForeignKey(name="FK_Attivita_visitaMedica")
	//@Index(name="IX_Attivita_visitaMedica")
	public VisitaMedica getVisitaMedica(){
		return visitaMedica;
	}

	public void setVisitaMedica(VisitaMedica visitaMedica){
		this.visitaMedica = visitaMedica;
	}



	/**
	*  javadoc for luogoPerson
	*/
	private Person luogoUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="luogo_utente_id")
	@ForeignKey(name="FK_Attivita_luogoUtente")
	//@Index(name="IX_Attivita_luogoUtente")
	public Person getLuogoUtente(){
		return luogoUtente;
	}

	public void setLuogoUtente(Person luogoUtente){
		this.luogoUtente = luogoUtente;
	}

	/**
	*  javadoc for luogoCantiere
	*/
	private Cantiere luogoCantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="luogo_cantiere_id")
	@ForeignKey(name="FK_Attivita_luogoCantiere")
	//@Index(name="IX_Attivita_luogoCantiere")
	public Cantiere getLuogoCantiere(){
		return luogoCantiere;
	}

	public void setLuogoCantiere(Cantiere luogoCantiere){
		this.luogoCantiere = luogoCantiere;
	}



	/**
	*  javadoc for luogoSede
	*/
	private Sedi luogoSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="luogo_sede_id")
	@ForeignKey(name="FK_Attivita_luogoSede")
	//@Index(name="IX_Attivita_luogoSede")
	public Sedi getLuogoSede(){
		return luogoSede;
	}

	public void setLuogoSede(Sedi luogoSede){
		this.luogoSede = luogoSede;
	}



	/**
	*  javadoc for luogoDitta
	*/
	private PersoneGiuridiche luogoDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="luogo_ditta_id")
	@ForeignKey(name="FK_Attivita_luogoDitta")
	//@Index(name="IX_Attivita_luogoDitta")
	public PersoneGiuridiche getLuogoDitta(){
		return luogoDitta;
	}

	public void setLuogoDitta(PersoneGiuridiche luogoDitta){
		this.luogoDitta = luogoDitta;
	}


	/**
	*  javadoc for luogo
	*/
	private CodeValuePhi luogo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="luogo")
	@ForeignKey(name="FK_Attivita_luogo")
	//@Index(name="IX_Attivita_luogo")
	public CodeValuePhi getLuogo(){
		return luogo;
	}

	public void setLuogo(CodeValuePhi luogo){
		this.luogo = luogo;
	}


	/**
	*  javadoc for oggetti
	*/
	private List<Oggetto> oggetti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
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
			oggetti.setAttivita(this);
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
			oggetti.setAttivita(null);
		}
	}



	/**
	*  javadoc for provvedimenti
	*/
	private List<Provvedimenti> provvedimenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<Provvedimenti> getProvvedimenti(){
		return provvedimenti;
	}

	public void setProvvedimenti(List<Provvedimenti> list){
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
			provvedimenti.setAttivita(this);
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
			provvedimenti.setAttivita(null);
		}

	}









//	/**
//	*  javadoc for provvedimenti
//	*/
//	private List<Provvedimenti> provvedimenti;
//
//	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
//	public List<Provvedimenti> getProvvedimenti() {
//		return provvedimenti;
//	}
//
//	public void setProvvedimenti(List<Provvedimenti>list){
//		provvedimenti = list;
//	}
//
//	public void addProvvedimenti(Provvedimenti provvedimenti) {
//		if (this.provvedimenti == null) {
//			this.provvedimenti = new ArrayList<Provvedimenti>();
//		}
//		// add the association
//		if(!this.provvedimenti.contains(provvedimenti)) {
//			this.provvedimenti.add(provvedimenti);
//			// make the inverse link
//			provvedimenti.setAttivita(this);
//		}
//	}
//
//	public void removeProvvedimenti(Provvedimenti provvedimenti) {
//		if (this.provvedimenti == null) {
//			this.provvedimenti = new ArrayList<Provvedimenti>();
//			return;
//		}
//		//add the association
//		if(this.provvedimenti.contains(provvedimenti)){
//			this.provvedimenti.remove(provvedimenti);
//			//make the inverse link
//			provvedimenti.setAttivita(null);
//		}
//	}



	/**
	*  javadoc for accessoAtti
	*/
	private AccessoAtti accessoAtti;

	@OneToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="accesso_atti_id")
	@ForeignKey(name="FK_Attivita_accessoAtti")
	//@Index(name="IX_Attivita_accessoAtti")
	public AccessoAtti getAccessoAtti(){
		return accessoAtti;
	}

	public void setAccessoAtti(AccessoAtti accessoAtti){
		this.accessoAtti = accessoAtti;
	}



	/**
	*  javadoc for partecipanti
	*/
	private List<Partecipanti> partecipanti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<Partecipanti> getPartecipanti() {
		return partecipanti;
	}

	public void setPartecipanti(List<Partecipanti> list){
		partecipanti = list;
	}

	public void addPartecipanti(Partecipanti partecipanti) {
		if (this.partecipanti == null) {
			this.partecipanti = new ArrayList<Partecipanti>();
		}
		// add the association
		if(!this.partecipanti.contains(partecipanti)) {
			this.partecipanti.add(partecipanti);
			// make the inverse link
			partecipanti.setAttivita(this);
		}
	}

	public void removePartecipanti(Partecipanti partecipanti) {
		if (this.partecipanti == null) {
			this.partecipanti = new ArrayList<Partecipanti>();
			return;
		}
		//add the association
		if(this.partecipanti.contains(partecipanti)){
			this.partecipanti.remove(partecipanti);
			//make the inverse link
			partecipanti.setAttivita(null);
		}
	}

	/**
	*  javadoc for sequestro
	*/
	private Sequestri sequestro;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REFRESH})
	@JoinColumn(name="sequestro_id")
	@ForeignKey(name="FK_Attivita_sequestro")
	//@Index(name="IX_Attivita_sequestro")
	public Sequestri getSequestro(){
		return sequestro;
	}

	public void setSequestro(Sequestri sequestro){
		this.sequestro = sequestro;
	}

	/**
	*  javadoc for misure
	*/
	private List<Misure> misure;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita", cascade=CascadeType.PERSIST)
	public List<Misure> getMisure() {
		return misure;
	}

	public void setMisure(List<Misure>list){
		misure = list;
	}

	public void addMisure(Misure misure) {
		if (this.misure == null) {
			this.misure = new ArrayList<Misure>();
		}
		// add the association
		if(!this.misure.contains(misure)) {
			this.misure.add(misure);
			// make the inverse link
			misure.setAttivita(this);
		}
	}

	public void removeMisure(Misure misure) {
		if (this.misure == null) {
			this.misure = new ArrayList<Misure>();
			return;
		}
		//add the association
		if(this.misure.contains(misure)){
			this.misure.remove(misure);
			//make the inverse link
			misure.setAttivita(null);
		}
	}

	/**
	*  javadoc for rilevazioneAmbientale
	*/
	private RilevazioniAmbientali rilevazioneAmbientale;

	@OneToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="rilevazione_ambientale_id")
	@ForeignKey(name="FK_ttivta_rlevazonembentale")
	//@Index(name="IX_ttivta_rlevazonembentale")
	public RilevazioniAmbientali getRilevazioneAmbientale(){
		return rilevazioneAmbientale;
	}

	public void setRilevazioneAmbientale(RilevazioniAmbientali rilevazioneAmbientale){
		this.rilevazioneAmbientale = rilevazioneAmbientale;
	}

//	/**
//	*  javadoc for acquisizioneInformazioni
//	*/
//	private AcquisizioneInformazioni acquisizioneInformazioni;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="acquisizione_informazioni_id")
//	@ForeignKey(name="FK_ttvta_acquszoneInformzon")
//	//@Index(name="IX_ttvta_acquszoneInformzon")
//	public AcquisizioneInformazioni getAcquisizioneInformazioni(){
//		return acquisizioneInformazioni;
//	}
//
//	public void setAcquisizioneInformazioni(AcquisizioneInformazioni acquisizioneInformazioni){
//		this.acquisizioneInformazioni = acquisizioneInformazioni;
//	}

	/**
	*  javadoc for sopralluogo
	*/
	private Sopralluoghi sopralluogo;

	@OneToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="sopralluogo_id")
	@ForeignKey(name="FK_Attivita_sopralluogo")
	//@Index(name="IX_Attivita_sopralluogo")
	public Sopralluoghi getSopralluogo(){
		return sopralluogo;
	}

	public void setSopralluogo(Sopralluoghi sopralluogo){
		this.sopralluogo = sopralluogo;
	}

	/**
	*  javadoc for soggetto
	*/
	private List<Soggetto> soggetto;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="attivita")
	public List<Soggetto> getSoggetto() {
		return soggetto;
	}

	public void setSoggetto(List<Soggetto>list){
		soggetto = list;
	}

	public void addSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
		}
		// add the association
		if(!this.soggetto.contains(soggetto)) {
			this.soggetto.add(soggetto);
			// make the inverse link
			soggetto.setAttivita(this);
		}
	}

	public void removeSoggetto(Soggetto soggetto) {
		if (this.soggetto == null) {
			this.soggetto = new ArrayList<Soggetto>();
			return;
		}
		//add the association
		if(this.soggetto.contains(soggetto)){
			this.soggetto.remove(soggetto);
			//make the inverse link
			soggetto.setAttivita(null);
		}
	}

	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Attivita_procpratiche")
	//@Index(name="IX_Attivita_procpratiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	/**
	*  javadoc for operatori
	*/
	private List<Operatore> operatori;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="attivita_operatori",
		joinColumns = { @JoinColumn(name="Attivita_id") },
		inverseJoinColumns = { @JoinColumn(name="Operatore_id") })
	@ForeignKey(name="FK_Attivita_operatori", inverseName="FK_Operatori_Attivita")
	@IndexColumn(name="Attivita_Operatori")
	public List<Operatore> getOperatori() {
		return operatori;
	}

	public void setOperatori(List<Operatore> list){
		operatori = list;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Attivita_sequence")
	@SequenceGenerator(name = "Attivita_sequence", sequenceName = "Attivita_sequence")
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
	@ForeignKey(name="FK_Att_code")
	//@Index(name="IX_Att_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_Att_status")
	//@Index(name="IX_Att_status") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	*  javadoc for dataInizio - Data inizio attività
	*/
	private Date dataInizio;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_inizio")
	public Date getDataInizio(){
		return dataInizio;
	}

	public void setDataInizio(Date dataInizio){
		this.dataInizio = dataInizio;
	}
	
	/**
	*  javadoc for dataFine - Data fine attività
	*/
	private Date dataFine;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_fine")
	public Date getDataFine(){
		return dataFine;
	}

	public void setDataFine(Date dataFine){
		this.dataFine = dataFine;
	}
	
	private String note;

	@Column(name = "NOTE", length=4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	/*private CodeValue oggetto;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="oggetto")
	@ForeignKey(name="FK_Att_oggetto")
	//@Index(name="IX_Att_oggetto")
	public CodeValue getOggetto() {
		return oggetto;
	}

	public void setOggetto(CodeValue oggetto) {
		this.oggetto = oggetto;
	}
	
	private String noteOggetto;

	@Column(name = "note_oggetto")
	public String getNoteOggetto() {
		return this.noteOggetto;
	}

	public void setNoteOggetto(String noteOggetto) {
		this.noteOggetto = noteOggetto;
	}*/
		
	protected Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Att_author")
	//@Index(name="IX_Att_author")
	public Employee getAuthor() {
	    return author;
	}

	public void setAuthor(Employee param) {
	    this.author = param;
	}

	/**
	 *  Address
	 */
	private AD addr;

	@Embedded
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
	
	private CodeValue tipoAddr;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_ad")
	@ForeignKey(name="FK_Att_tipo_ad")
	//@Index(name="IX_Att_tipo_ad")
	public CodeValue getTipoAddr() {
		return tipoAddr;
	}

	public void setTipoAddr(CodeValue tipoAddr) {
		this.tipoAddr = tipoAddr;
	}
	
	private CodeValue oggettoVerifica;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="oggetto_verifica")
	@ForeignKey(name="FK_Att_oggver")
	//@Index(name="IX_Att_oggver")
	public CodeValue getOggettoVerifica() {
		return oggettoVerifica;
	}

	public void setOggettoVerifica(CodeValue oggettoVerifica) {
		this.oggettoVerifica = oggettoVerifica;
	}
	
	/**
	*  javadoc for reliability
	*/
	private String area;

	@Column(name="area", length=2000)
	public String getArea(){
		return area;
	}

	public void setArea(String area){
		this.area = area;
	}
	
	public String toString() {
		String codeTransl = "";
		if (code != null) {
			codeTransl = code.getCurrentTranslation();
		}
		return "Attività " + codeTransl + " " + internalId;
	}
	
	
	@Transient
	public String getLaParte(){
		
		String ret = ""; 
		try{
		for(Soggetto s:getSoggetto()){
			if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				ret+=s.getDitta().getDenominazione();
				Sedi sede = s.getSede();
				if (sede != null)
					ret += " (sede: " + sede.getDenominazioneUnitaLocale().toUpperCase() + ")";
				break;
			}
		}
		}catch(Exception e){}
		return ret;
	}
	
	@Transient
	public String getLaParteIva(){
		
		String ret = ""; 
		try{
		for(Soggetto s:getSoggetto()){
			if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				if(s.getDitta().getPatritaIva()!=null){
					ret+=s.getDitta().getPatritaIva();
				}else{
					ret+=s.getDitta().getCodiceFiscale();
				}
				break;
			}
		}
		}catch(Exception e){}
		return ret;
	}
	
	@Transient
	public String getLaParteAddr(){
		
		String ret = ""; 
		try{
		for(Soggetto s:getSoggetto()){
			if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				if(s.getDitta()!=null && s.getDitta().getSedePrincipale()!=null){
					ret+=s.getDitta().getSedePrincipale().getAddr();	
				}				
				break;
			}
		}
		}catch(Exception e){}
		return ret;
	}
	
	
	@Transient
	public Boolean getIsUnitaProduttiva(){
		
		Boolean ret = false; 
		try{
		if(getLuogo()==null || getLuogo().getCode()==null || !getLuogo().getCode().equalsIgnoreCase("Ditta")) return false;
		
		for(Soggetto s:getSoggetto()){
			if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
				PersoneGiuridiche dittaAttivita = s.getDitta();
				if(getLuogoDitta()!=dittaAttivita){
					ret = true;
				} 
				
				break;
			}
		}
		}catch(Exception e){}
		return ret;
		
	}
	
	@Transient
	public Boolean getIsCantiere(){
		
		Boolean ret = false; 
		try{
		if(getLuogo()==null || getLuogo().getCode()==null) return false;
		
		if(getLuogo().getCode().equalsIgnoreCase("Cantiere")){
		
			for(Soggetto s:getSoggetto()){
				if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
					PersoneGiuridiche dittaAttivita = s.getDitta();
					if(getLuogoDitta()!=dittaAttivita){
						ret = true;
					} 
					
					break;
				}
			}
		}
		}catch(Exception e){}
		return ret;
		
	}
	
	@Transient
	public String getCommittenteCF(){
		
		String ret = "";
		try{

			
			for(Soggetto s:getSoggetto()){
				
				
				if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
					if(s.getTipoDitta().getCurrentTranslation().equalsIgnoreCase("committente")){
						if(s.getDitta()!=null) {
							ret+=s.getDitta().getDenominazione()+" ";
						}
						if(s.getDitta().getPatritaIva()!=null) {
							ret+="(P.IVA: "+s.getDitta().getPatritaIva()+")";
						}else{
							ret+="(C.F.: "+s.getDitta().getCodiceFiscale()+")";
						}
						break;
					}
					 
					
					
				}else if(s.getCode().getCurrentTranslation().equalsIgnoreCase("utente")){
					if(s.getRuolo()!=null && s.getRuolo().getCurrentTranslation().equalsIgnoreCase("committente") && s.getUtente()!=null){
						ret+=s.getUtente().getName()+" ";
						
						if(s.getUtente().getFiscalCode()!=null){
							ret+="("+s.getUtente().getFiscalCode() +")";
						}
						break;
					}
					
				}
			}	
			
		}catch(Exception e){}
		return ret;
		
	}
	
	
	@Transient
	public String getCommittenteAddr(){
		
		String ret = "";
		try{
		
			for(Soggetto s:getSoggetto()){
				if(s.getCode().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente")){
					
					if(s.getTipoDitta().getCurrentTranslation().equalsIgnoreCase("committente")){
						if(s.getDitta()!=null && s.getDitta().getSedePrincipale()!=null) {
							ret+=s.getDitta().getSedePrincipale().getAddr();
						}
						break;
					}
					 
				}else if(s.getCode().getCurrentTranslation().equalsIgnoreCase("utente")){
					if(s.getRuolo()!=null && s.getRuolo().getCurrentTranslation().equalsIgnoreCase("committente") && s.getUtente()!=null){
						ret+=s.getUtente().getAddr();
						break;
					}
					
				}
			}	
			
		}catch(Exception e){}
		return ret;
		
	}
	
	@Transient
	public String getAltrePersone(){
		
		String ret = "";
		try{
		boolean isFirst=true;
		
		
		for(Soggetto s:soggetto){
			
			if(s.getCode().getCode().equalsIgnoreCase("utente")){
				if(isFirst){
					isFirst=false;
				}else{
					ret+="\n";
				}
				ret+="- " + s.getUtente().getName();// +", Qualifica: "+s.getRuolo().getCurrentTranslation();
				
				
				if(s.getRuolo()!=null){
					ret+=" ("+s.getRuolo().getCurrentTranslation()+")";
				}
				
				if(s.getUtente().getBirthTime()!=null){
					ret+=", nato";
					
					if(s.getUtente().getBirthPlace()!=null && !s.getUtente().getBirthPlace().getCty().equals("")){
						ret+=" a " + s.getUtente().getBirthPlace().getCty()+"("+s.getUtente().getBirthPlace().getCpa()+")";
					}
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
					ret+=" il "+sdf.format(s.getUtente().getBirthTime());
					
				}
				
				AD addr = s.getUtente().getAddr();
				
				if(addr!=null && !addr.getCty().equals("")){
					ret+=" residente a " + addr.getCty()+"("+addr.getZip()+" "+addr.getCpa()+") in ";
					ret+=addr.getStr();
					if(addr.getBnr()!=null && !addr.getBnr().equalsIgnoreCase("null")){
						ret+=" n°"+addr.getBnr();
					}
					
				}
				
				ret+="\n";
				
			}

		}
		
		}catch(Exception e){}
		return ret;		
		
	}
	
	@Transient
	public String getPrintLegRapprName(){

		String ret = "";
		try{

			if(getLuogo()!=null && getLuogo().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente") && getLuogoSede()!=null){

				//stampa il nome del legale rappresentante della ditta soggetto del provvedimento
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				String query = "SELECT c FROM Cariche c where c.sede = :sede";
				Query q = ca.createQuery(query);
				q.setParameter("sede", getLuogoSede());
				List<Cariche> ruoList = q.getResultList();

				for(Cariche r:ruoList){
					if(r.getCarica()!=null && r.getCarica().getCode()!=null && r.getCarica().getCode().equals("ler")){
						SediPersone s = r.getSediPersone();
						ret+=s.getName();
						break;
					}

				}

			}
		}catch(Exception e){}
		return ret;
	}

	@Transient
	public String getPrintLegRapprDatiAnagr(){

		String ret = "";
		try{

			if(getLuogo()!=null && getLuogo().getCurrentTranslation().equalsIgnoreCase("Ditta/Ente") && getLuogoSede()!=null){

				//stampa il nome del legale rappresentante della ditta soggetto del provvedimento
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				String query = "SELECT c FROM Cariche c where c.sede = :sede";
				Query q = ca.createQuery(query);
				q.setParameter("sede", getLuogoSede());
				List<Cariche> ruoList = q.getResultList();
				
				SediPersone rappr = null;
				for(Cariche r:ruoList){
					if(r.getCarica()!=null && r.getCarica().getCode()!=null && r.getCarica().getCode().equals("ler")){
						rappr = r.getSediPersone();
						break;
					}

				}

				//Data di nascita
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				ret += sdf.format(rappr.getBirthTime()) + " ";

				//Luogo di nascita
				String luogoNascita = "";
				if(rappr.getBirthPlace()!=null){
					if(!"".equals(rappr.getBirthPlace().getCty())){
						luogoNascita = "a " + rappr.getBirthPlace().getCty() + " (" + rappr.getBirthPlace().getCpa() + ") ";
					}
				}else{
					if(rappr.getCountryOfBirth()!=null){
						luogoNascita = "in " + rappr.getCountryOfBirth().getCurrentTranslation() + " ";
					}
				}
				
				//Residenza
				ret += luogoNascita + "e residente a ";
				ret += (rappr.getAddr()!=null?rappr.getAddr().getCty():"") + " ";
				ret += "(" + (rappr.getAddr()!=null?rappr.getAddr().getCpa():"") + ") ";
				ret += "in " + (rappr.getAddr()!=null?rappr.getAddr().getStr():"") + " " + (rappr.getAddr()!=null?(rappr.getAddr().getBnr()!=null?rappr.getAddr().getBnr():""):"");

			}
		}catch(Exception e){}
		return ret;
	}
	
	@PostPersist
	@PostUpdate
	public void updateAssociations(){
		try {
			Procpratiche p = ProcpraticheAction.instance().getEntity();
			if(p!=null)
				p.updateAssociations();
		}catch(IllegalStateException e) {
			//sto eseguendo l'importer
		}
		
	}
}