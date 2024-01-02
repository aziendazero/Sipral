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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

@javax.persistence.Entity
@Table(name = "malattie_professionali")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MalattiaProfessionale extends BaseEntity {

	private static final long serialVersionUID = 643283767L;

	/**
	*  javadoc for tipoLavoratore
	*/
	private CodeValuePhi tipoLavoratore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo_lavoratore")
	@ForeignKey(name="FK_MalProf_tipoLav")
	@Index(name="IX_MalProf_tipoLav")
	public CodeValuePhi getTipoLavoratore(){
		return tipoLavoratore;
	}

	public void setTipoLavoratore(CodeValuePhi tipoLavoratore){
		this.tipoLavoratore = tipoLavoratore;
	}

	/**
	*  javadoc for gravitaFinale
	*/
	private CodeValuePhi gravitaFinale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gravitaFinale")
	@ForeignKey(name="FK_MalattiaProfessionale_gravitaFinale")
	//@Index(name="IX_MalattiaProfessionale_gravitaFinale")
	public CodeValuePhi getGravitaFinale(){
		return gravitaFinale;
	}

	public void setGravitaFinale(CodeValuePhi gravitaFinale){
		this.gravitaFinale = gravitaFinale;
	}

	/**
	*  javadoc for certDiagText
	*/
	private String certDiagText;

	@Column(name="cert_diag_text")
	public String getCertDiagText(){
		return certDiagText;
	}

	public void setCertDiagText(String certDiagText){
		this.certDiagText = certDiagText;
	}

	/**
	*  javadoc for certDiagDate
	*/
	private Date certDiagDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cert_diag_date")
	public Date getCertDiagDate(){
		return certDiagDate;
	}

	public void setCertDiagDate(Date certDiagDate){
		this.certDiagDate = certDiagDate;
	}

	/**
	*  javadoc for certezzaDiag
	*/
	private Boolean certezzaDiag;

	@Column(name="certezza_diag")
	public Boolean getCertezzaDiag(){
		return certezzaDiag;
	}

	public void setCertezzaDiag(Boolean certezzaDiag){
		this.certezzaDiag = certezzaDiag;
	}

	/**
	*  javadoc for condProf
	*/
	private CodeValuePhi condProf;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="condProf")
	@ForeignKey(name="FK_MalattiaProfessionale_condProf")
	//@Index(name="IX_MalattiaProfessionale_condProf")
	public CodeValuePhi getCondProf(){
		return condProf;
	}

	public void setCondProf(CodeValuePhi condProf){
		this.condProf = condProf;
	}

	/**
	*  javadoc for inchiesta
	*/
	private Boolean inchiesta;

	@Column(name="inchiesta")
	public Boolean getInchiesta(){
		return inchiesta;
	}

	public void setInchiesta(Boolean inchiesta){
		this.inchiesta = inchiesta;
	}

	/**
	*  javadoc for azioneIntrapresa
	*/
	private CodeValuePhi azioneIntrapresa;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="azioneIntrapresa")
	@ForeignKey(name="FK_MalattiaProfessionale_azioneIntrapresa")
	//@Index(name="IX_MalattiaProfessionale_azioneIntrapresa")
	public CodeValuePhi getAzioneIntrapresa(){
		return azioneIntrapresa;
	}

	public void setAzioneIntrapresa(CodeValuePhi azioneIntrapresa){
		this.azioneIntrapresa = azioneIntrapresa;
	}

	/**
	*  javadoc for deathPlace
	*/
	private CodeValuePhi deathPlace;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="deathPlace")
	@ForeignKey(name="FK_MalattiaProfessionale_deathPlace")
	//@Index(name="IX_MalattiaProfessionale_deathPlace")
	public CodeValuePhi getDeathPlace(){
		return deathPlace;
	}

	public void setDeathPlace(CodeValuePhi deathPlace){
		this.deathPlace = deathPlace;
	}

	/**
	*  javadoc for fonte
	*/
	private CodeValuePhi fonte;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="fonte")
	@ForeignKey(name="FK_MalattiaProfessionale_fonte")
	//@Index(name="IX_MalattiaProfessionale_fonte")
	public CodeValuePhi getFonte(){
		return fonte;
	}

	public void setFonte(CodeValuePhi fonte){
		this.fonte = fonte;
	}

	/**
	*  javadoc for valDate
	*/
	private Date valDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="val_date")
	public Date getValDate(){
		return valDate;
	}

	public void setValDate(Date valDate){
		this.valDate = valDate;
	}

	/**
	*  javadoc for valNote
	*/
	private String valNote;

	@Column(name="val_note")
	public String getValNote(){
		return valNote;
	}

	public void setValNote(String valNote){
		this.valNote = valNote;
	}

	/**
	*  javadoc for valutazione
	*/
	private CodeValuePhi valutazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="valutazione")
	@ForeignKey(name="FK_MalattiaProfessionale_valutazione")
	//@Index(name="IX_MalattiaProfessionale_valutazione")
	public CodeValuePhi getValutazione(){
		return valutazione;
	}

	public void setValutazione(CodeValuePhi valutazione){
		this.valutazione = valutazione;
	}


	/**
	*  javadoc for visitaMedica
	*/
	private List<VisitaMedica> visitaMedica;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="malattiaProfessionale", cascade=CascadeType.PERSIST)
	public List<VisitaMedica> getVisitaMedica() {
		return visitaMedica;
	}

	public void setVisitaMedica(List<VisitaMedica>list){
		visitaMedica = list;
	}

	public void addVisitaMedica(VisitaMedica visitaMedica) {
		if (this.visitaMedica == null) {
			this.visitaMedica = new ArrayList<VisitaMedica>();
		}
		// add the association
		if(!this.visitaMedica.contains(visitaMedica)) {
			this.visitaMedica.add(visitaMedica);
			// make the inverse link
			visitaMedica.setMalattiaProfessionale(this);
		}
	}

	public void removeVisitaMedica(VisitaMedica visitaMedica) {
		if (this.visitaMedica == null) {
			this.visitaMedica = new ArrayList<VisitaMedica>();
			return;
		}
		//add the association
		if(this.visitaMedica.contains(visitaMedica)){
			this.visitaMedica.remove(visitaMedica);
			//make the inverse link
			visitaMedica.setMalattiaProfessionale(null);
		}
	}



	/**
	*  javadoc for fattoreRischio
	*/
	private List<FattoreRischio> fattoreRischio;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="malattiaProfessionale", cascade=CascadeType.PERSIST)
	public List<FattoreRischio> getFattoreRischio() {
		return fattoreRischio;
	}

	public void setFattoreRischio(List<FattoreRischio>list){
		fattoreRischio = list;
	}

	public void addFattoreRischio(FattoreRischio fattoreRischio) {
		if (this.fattoreRischio == null) {
			this.fattoreRischio = new ArrayList<FattoreRischio>();
		}
		// add the association
		if(!this.fattoreRischio.contains(fattoreRischio)) {
			this.fattoreRischio.add(fattoreRischio);
			// make the inverse link
			fattoreRischio.setMalattiaProfessionale(this);
		}
	}

	public void removeFattoreRischio(FattoreRischio fattoreRischio) {
		if (this.fattoreRischio == null) {
			this.fattoreRischio = new ArrayList<FattoreRischio>();
			return;
		}
		//add the association
		if(this.fattoreRischio.contains(fattoreRischio)){
			this.fattoreRischio.remove(fattoreRischio);
			//make the inverse link
			fattoreRischio.setMalattiaProfessionale(null);
		}
	}


	/**
	*  javadoc for anamnesi
	*/
	private String anamnesi;

	@Column(name="anamnesi", length=2500)
	public String getAnamnesi(){
		return anamnesi;
	}

	public void setAnamnesi(String anamnesi){
		this.anamnesi = anamnesi;
	}

	/**
	*  javadoc for deathText
	*/
	private String deathText;

	@Column(name="death_text")
	public String getDeathText(){
		return deathText;
	}

	public void setDeathText(String deathText){
		this.deathText = deathText;
	}

	/**
	*  javadoc for deathCause
	*/
	private CodeValueIcd9 deathCause;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="deathCause")
	@ForeignKey(name="FK_MalattiaProfessionale_deathCause")
	//@Index(name="IX_MalattiaProfessionale_deathCause")
	public CodeValueIcd9 getDeathCause(){
		return deathCause;
	}

	public void setDeathCause(CodeValueIcd9 deathCause){
		this.deathCause = deathCause;
	}

	/**
	*  javadoc for deathDate
	*/
	private Date deathDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="death_date")
	public Date getDeathDate(){
		return deathDate;
	}

	public void setDeathDate(Date deathDate){
		this.deathDate = deathDate;
	}

	/**
	*  javadoc for aggrDate
	*/
	private Date aggrDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="aggr_date")
	public Date getAggrDate(){
		return aggrDate;
	}

	public void setAggrDate(Date aggrDate){
		this.aggrDate = aggrDate;
	}

	/**
	*  javadoc for diagDate
	*/
	private Date diagDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="diag_date")
	public Date getDiagDate(){
		return diagDate;
	}

	public void setDiagDate(Date diagDate){
		this.diagDate = diagDate;
	}

	/**
	*  javadoc for ricInailPerc
	*/
	private Integer ricInailPerc;

	@Column(name="ric_inail_perc")
	public Integer getRicInailPerc(){
		return ricInailPerc;
	}

	public void setRicInailPerc(Integer ricInailPerc){
		this.ricInailPerc = ricInailPerc;
	}

	/**
	*  javadoc for ricInailData
	*/
	private Date ricInailData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ric_inail_data")
	public Date getRicInailData(){
		return ricInailData;
	}

	public void setRicInailData(Date ricInailData){
		this.ricInailData = ricInailData;
	}

	/**
	*  javadoc for ricInail
	*/
	private CodeValuePhi ricInail;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ricInail")
	@ForeignKey(name="FK_MalattiaProfessionale_ricInail")
	//@Index(name="IX_MalattiaProfessionale_ricInail")
	public CodeValuePhi getRicInail(){
		return ricInail;
	}

	public void setRicInail(CodeValuePhi ricInail){
		this.ricInail = ricInail;
	}

	/**
	*  javadoc for mpInail
	*/
	private CodeValuePhi mpInail;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mpInail")
	@ForeignKey(name="FK_MalattiaProfessionale_mpInail")
	//@Index(name="IX_MalattiaProfessionale_mpInail")
	public CodeValuePhi getMpInail(){
		return mpInail;
	}

	public void setMpInail(CodeValuePhi mpInail){
		this.mpInail = mpInail;
	}

	/**
	*  javadoc for certMed
	*/
	private Physician certMed;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cert_med_id")
	@ForeignKey(name="FK_MalProf_certMed")
	//@Index(name="IX_MalProf_certMed")
	public Physician getCertMed(){
		return certMed;
	}

	public void setCertMed(Physician certMed){
		this.certMed = certMed;
	}


	/**
	*  javadoc for certDate
	*/
	private Date certDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cert_date")
	public Date getCertDate(){
		return certDate;
	}

	public void setCertDate(Date certDate){
		this.certDate = certDate;
	}

	/**
	*  javadoc for ditteMalattie
	*/
	private List<DitteMalattie> ditteMalattie;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="malattiaProfessionale", cascade=CascadeType.PERSIST)
	public List<DitteMalattie> getDitteMalattie() {
		return ditteMalattie;
	}

	public void setDitteMalattie(List<DitteMalattie>list){
		ditteMalattie = list;
	}

	public void addDitteMalattie(DitteMalattie ditteMalattie) {
		if (this.ditteMalattie == null) {
			this.ditteMalattie = new ArrayList<DitteMalattie>();
		}
		// add the association
		if(!this.ditteMalattie.contains(ditteMalattie)) {
			this.ditteMalattie.add(ditteMalattie);
			// make the inverse link
			ditteMalattie.setMalattiaProfessionale(this);
		}
	}

	public void removeDitteMalattie(DitteMalattie ditteMalattie) {
		if (this.ditteMalattie == null) {
			this.ditteMalattie = new ArrayList<DitteMalattie>();
			return;
		}
		//add the association
		if(this.ditteMalattie.contains(ditteMalattie)){
			this.ditteMalattie.remove(ditteMalattie);
			//make the inverse link
			ditteMalattie.setMalattiaProfessionale(null);
		}
	}



	/**
	*  javadoc for lastIdonDate
	*/
	private Date lastIdonDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_idon_date")
	public Date getLastIdonDate(){
		return lastIdonDate;
	}

	public void setLastIdonDate(Date lastIdonDate){
		this.lastIdonDate = lastIdonDate;
	}

	/**
	*  javadoc for lastIdonText
	*/
	private String lastIdonText;

	@Column(name="last_idon_text")
	public String getLastIdonText(){
		return lastIdonText;
	}

	public void setLastIdonText(String lastIdonText){
		this.lastIdonText = lastIdonText;
	}

	/**
	*  javadoc for attualeDitta
	*/
	private PersoneGiuridiche attualeDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="attuale_ditta_id")
	@ForeignKey(name="FK_MalProf_attDitta")
	//@Index(name="IX_MalProf_attDitta")
	public PersoneGiuridiche getAttualeDitta(){
		return attualeDitta;
	}

	public void setAttualeDitta(PersoneGiuridiche attualeDitta){
		this.attualeDitta = attualeDitta;
	}
	
	/**
	*  javadoc for attualeSede
	*/
	private Sedi attualeSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="attuale_sede_id")
	@ForeignKey(name="FK_MalProf_attSede")
	//@Index(name="IX_MalProf_attSede")
	public Sedi getAttualeSede(){
		return attualeSede;
	}

	public void setAttualeSede(Sedi attualeSede){
		this.attualeSede = attualeSede;
	}
	
	/**
	*  javadoc for mansioneAttuale
	*/
	private CodeValuePhi mansioneAttuale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mansione2")
	@ForeignKey(name="FK_MalProf_mansione2")
	//@Index(name="IX_MalProf_mansione2")
	public CodeValuePhi getMansioneAttuale(){
		return mansioneAttuale;
	}

	public void setMansioneAttuale(CodeValuePhi mansioneAttuale){
		this.mansioneAttuale = mansioneAttuale;
	}
	
	/**
	*  javadoc for mansioneAttribuita
	*/
	private CodeValuePhi mansioneAttribuita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mansione1")
	@ForeignKey(name="FK_MalProf_mansione1")
	//@Index(name="IX_MalProf_mansione1")
	public CodeValuePhi getMansioneAttribuita(){
		return mansioneAttribuita;
	}

	public void setMansioneAttribuita(CodeValuePhi mansioneAttribuita){
		this.mansioneAttribuita = mansioneAttribuita;
	}

	/**
	*  javadoc for comparto
	*/
	private CodeValueAteco comparto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comparto")
	@ForeignKey(name="FK_MalProf_comparto")
	//@Index(name="IX_MalProf_comparto")
	public CodeValueAteco getComparto(){
		return comparto;
	}

	public void setComparto(CodeValueAteco comparto){
		this.comparto = comparto;
	}

	private CodeValue riferimento;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="riferimento")
	@ForeignKey(name="FK_MalProf_riferimento")
	//@Index(name="IX_MalProf_riferimento")
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
	@ForeignKey(name="FK_MalProf_rifUtente")
	//@Index(name="IX_MalProf_rifUtente")
	public Person getRiferimentoUtente(){
		return riferimentoUtente;
	}

	public void setRiferimentoUtente(Person riferimentoUtente){
		this.riferimentoUtente = riferimentoUtente;
	}
	
	private CodeValue richiedente;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="richiedente")
	@ForeignKey(name="FK_MalProf_richiedente")
	//@Index(name="IX_MalProf_richiedente")
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
	@ForeignKey(name="FK_MalProf_ricMedico")
	//@Index(name="IX_MalProf_ricMedico")
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
	@ForeignKey(name="FK_MalProf_ricInterno")
	//@Index(name="IX_MalProf_ricInterno")
	public Employee getRichiedenteInterno(){
		return richiedenteInterno;
	}

	public void setRichiedenteInterno(Employee richiedenteInterno){
		this.richiedenteInterno = richiedenteInterno;
	}


	/**
	*  javadoc for richiedenteDitta
	*/
	private PersoneGiuridiche richiedenteDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_ditta_id")
	@ForeignKey(name="FK_MalProf_ricDitta")
	//@Index(name="IX_MalProf_ricDitta")
	public PersoneGiuridiche getRichiedenteDitta(){
		return richiedenteDitta;
	}

	public void setRichiedenteDitta(PersoneGiuridiche richiedenteDitta){
		this.richiedenteDitta = richiedenteDitta;
	}
	
	/**
	*  javadoc for richiedenteSede
	*/
	private Sedi richiedenteSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_sede_id")
	@ForeignKey(name="FK_MalProf_ricSede")
	//@Index(name="IX_MalProf_ricSede")
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
	@ForeignKey(name="FK_MalProf_ricUtente")
	//@Index(name="IX_MalProf_ricUtente")
	public Person getRichiedenteUtente(){
		return richiedenteUtente;
	}

	public void setRichiedenteUtente(Person richiedenteUtente){
		this.richiedenteUtente = richiedenteUtente;
	}
	
	/**
	*  javadoc for applicant
	*/
	private CodeValuePhi applicant;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="applicant")
	@ForeignKey(name="FK_MalProf_applicant")
	//@Index(name="IX_MalProf_applicant")
	public CodeValuePhi getApplicant(){
		return applicant;
	}

	public void setApplicant(CodeValuePhi applicant){
		this.applicant = applicant;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_MalProf_code")
	//@Index(name="IX_MalProf_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
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
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="malattiaProfessionale", cascade=CascadeType.PERSIST)
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
			procpratiche.setMalattiaProfessionale(this);
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
			procpratiche.setMalattiaProfessionale(null);
		}

	}


	/**
	*  javadoc for sedeText
	*/
	private String sedeText;

	@Column(name="sede_text")
	public String getSedeText(){
		return sedeText;
	}

	public void setSedeText(String sedeText){
		this.sedeText = sedeText;
	}

	/**
	*  javadoc for gravita
	*/
	private CodeValuePhi gravita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gravita")
	@ForeignKey(name="FK_MalProf_gravita")
	//@Index(name="IX_MalProf_gravita")
	public CodeValuePhi getGravita(){
		return gravita;
	}

	public void setGravita(CodeValuePhi gravita){
		this.gravita = gravita;
	}

	/**
	*  javadoc for diagCode
	*/
	private CodeValueIcd9 diagCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="diagCode")
	@ForeignKey(name="FK_MalProf_diagCode")
	//@Index(name="IX_MalProf_diagCode")
	public CodeValueIcd9 getDiagCode(){
		return diagCode;
	}

	public void setDiagCode(CodeValueIcd9 diagCode){
		this.diagCode = diagCode;
	}

	/**
	*  javadoc for diagText
	*/
	private String diagText;

	@Column(name="diag_text", length=2500)
	public String getDiagText(){
		return diagText;
	}

	public void setDiagText(String diagText){
		this.diagText = diagText;
	}


	/**
	*  javadoc for protocollo
	*/
	private List<Protocollo> protocollo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="malattiaProfessionale", cascade=CascadeType.PERSIST)
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
			protocollo.setMalattiaProfessionale(this);
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
			protocollo.setMalattiaProfessionale(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MalattiaProfessionale_sequence")
	@SequenceGenerator(name = "MalattiaProfessionale_sequence", sequenceName = "MalattiaProfessionale_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
