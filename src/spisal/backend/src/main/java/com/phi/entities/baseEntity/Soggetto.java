package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

@Entity
@Table(name = "soggetto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Soggetto extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7179889005975065540L;


	/**
	*  javadoc for medico
	*/
	private Physician medico;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="medico_id")
	@ForeignKey(name="FK_Soggetto_medico")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Physician getMedico(){
		return medico;
	}

	public void setMedico(Physician medico){
		this.medico = medico;
	}



	/**
	*  javadoc for documenti
	*/
	private List<AlfrescoDocument> documenti;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Soggetto_id")
	@ForeignKey(name="FK_documenti_Soggetto")
	//@Index(name="IX_documenti_Soggetto")
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
			if (this.attivita != null) {
				this.attivita.addDocumenti(alfrescoDocument);
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
			if (this.attivita != null) {
				this.attivita.removeDocumenti(alfrescoDocument);
			}
		}
	}
	
	/**
	*  javadoc for codiceIMO
	*/
	private String codiceIMO;

	@Column(name="codice_imo")
	public String getCodiceIMO(){
		return codiceIMO;
	}

	public void setCodiceIMO(String codiceIMO){
		this.codiceIMO = codiceIMO;
	}

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
	*  javadoc for lifestyle
	*/
	private Lifestyle lifestyle;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="lifestyle_id")
	@ForeignKey(name="FK_Soggetto_lifestyle")
	//@Index(name="IX_Soggetto_lifestyle")
	public Lifestyle getLifestyle(){
		return lifestyle;
	}

	public void setLifestyle(Lifestyle lifestyle){
		this.lifestyle = lifestyle;
	}



	/**
	*  javadoc for provvedimenti
	*/
	private List<Provvedimenti> provvedimenti;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="soggetto", cascade=CascadeType.PERSIST)
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
			provvedimenti.setSoggetto(this);
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
			provvedimenti.setSoggetto(null);
		}
	}

	/**
	*  javadoc for acquisizioneInformazioni
	*  
	*/
	private AcquisizioneInformazioni acquisizioneInformazioni;

	@OneToOne(fetch=FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinColumn(name="acquisizione_informazioni_id")
	@ForeignKey(name="FK_Sggtt_cquisizinInfrmzini")
	//@Index(name="IX_Sggtt_cquisizinInfrmzini")
	public AcquisizioneInformazioni getAcquisizioneInformazioni(){
		return acquisizioneInformazioni;
	}

	public void setAcquisizioneInformazioni(AcquisizioneInformazioni acquisizioneInformazioni){
		this.acquisizioneInformazioni = acquisizioneInformazioni;
	}

	/**
	*  javadoc for sedeUtente
	*/
	private Sedi sedeUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_utente_id")
	@ForeignKey(name="FK_Soggetto_sedeUtente")
	//@Index(name="IX_Soggetto_sedeUtente")
	public Sedi getSedeUtente(){
		return sedeUtente;
	}

	public void setSedeUtente(Sedi sedeUtente){
		this.sedeUtente = sedeUtente;
	}

	/**
	*  javadoc for dittaUtente
	*/
	private PersoneGiuridiche dittaUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_utente_id")
	@ForeignKey(name="FK_Soggetto_dittaUtente")
	//@Index(name="IX_Soggetto_dittaUtente")
	public PersoneGiuridiche getDittaUtente(){
		return dittaUtente;
	}

	public void setDittaUtente(PersoneGiuridiche dittaUtente){
		this.dittaUtente = dittaUtente;
	}


	/**
	*  javadoc for sede
	*/
	private Sedi sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_Soggetto_sede")
	//@Index(name="IX_Soggetto_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}

	/**
	*  javadoc for ditta
	*/
	private PersoneGiuridiche ditta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_id")
	@ForeignKey(name="FK_Soggetto_ditta")
	//@Index(name="IX_Soggetto_ditta")
	public PersoneGiuridiche getDitta(){
		return ditta;
	}

	public void setDitta(PersoneGiuridiche ditta){
		this.ditta = ditta;
	}

	/**
	*  javadoc for utente
	*/
	private Person utente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="utente_id")
	@ForeignKey(name="FK_Soggetto_utente")
	//@Index(name="IX_Soggetto_utente")
	public Person getUtente(){
		return utente;
	}

	public void setUtente(Person utente){
		this.utente = utente;
	}
	
	/**
	*  javadoc for utente
	*/
	private Cantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_Soggetto_cantiere")
	//@Index(name="IX_Soggetto_cantiere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}

	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Soggetto_attivita")
	//@Index(name="IX_Soggetto_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Soggetto_sequence")
	@SequenceGenerator(name = "Soggetto_sequence", sequenceName = "Soggetto_sequence")
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
	@ForeignKey(name="FK_Sogg_code")
	//@Index(name="IX_Sogg_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private CodeValue ruolo;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="ruolo")
	@ForeignKey(name="FK_Sogg_ruolo")
	//@Index(name="IX_Sogg_ruolo")
	public CodeValue getRuolo() {
		return ruolo;
	}

	public void setRuolo(CodeValue ruolo) {
		this.ruolo = ruolo;
	}
	
	private CodeValue tipoDitta;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_ditta")
	@ForeignKey(name="FK_Sogg_tipod")
	//@Index(name="IX_Sogg_tipod")
	public CodeValue getTipoDitta() {
		return tipoDitta;
	}

	public void setTipoDitta(CodeValue tipoDitta) {
		this.tipoDitta = tipoDitta;
	}
	
	private CodeValue tipoCantiere;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="tipo_cantiere")
	@ForeignKey(name="FK_Sogg_tipoc")
	//@Index(name="IX_Sogg_tipoc")
	public CodeValue getTipoCantiere() {
		return tipoCantiere;
	}

	public void setTipoCantiere(CodeValue tipoCantiere) {
		this.tipoCantiere = tipoCantiere;
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
	
//	private CodeValue tipoAddr;
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//    @JoinColumn(name="tipo_ad")
//	@ForeignKey(name="FK_Sogg_tipo_ad")
//	//@Index(name="IX_Sogg_tipo_ad")
//	public CodeValue getTipoAddr() {
//		return tipoAddr;
//	}
//
//	public void setTipoAddr(CodeValue tipoAddr) {
//		this.tipoAddr = tipoAddr;
//	}
//	
//	private CodeValue oggettoVerifica;
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//    @JoinColumn(name="oggetto_verifica")
//	@ForeignKey(name="FK_Sogg_oggver")
//	//@Index(name="IX_Sogg_oggver")
//	public CodeValue getOggettoVerifica() {
//		return oggettoVerifica;
//	}
//
//	public void setOggettoVerifica(CodeValue oggettoVerifica) {
//		this.oggettoVerifica = oggettoVerifica;
//	}
//	
//	/**
//	*  javadoc for reliability
//	*/
//	private String area;
//
//	@Column(name="area", length=2000)
//	public String getArea(){
//		return area;
//	}
//
//	public void setArea(String area){
//		this.area = area;
//	}
	
	private CodeValue riferimentoEntita;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="riferimento_entita")
	@ForeignKey(name="FK_Sogg_rifEnt")
	//@Index(name="IX_Sogg_rifEnt") 
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
	
	public String toString() {
		String codeTransl = "";
		if (code != null) {
			codeTransl = code.getCurrentTranslation();
		}
		return "Soggetto " + codeTransl + " " + internalId;
	}
}
