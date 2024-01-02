package com.phi.entities.baseEntity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.Procpratiche;

@Entity
@Table(name = "pratiche_riferimenti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PraticheRiferimenti extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 7889914910908933661L;

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
	*  javadoc for compartoUbicazione
	*/
	private CodeValueAteco compartoUbicazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="compartoUbicazione")
	@ForeignKey(name="FK_pr_compartoUbicazione")
	//@Index(name="IX_pr_compartoUbicazione")
	public CodeValueAteco getCompartoUbicazione(){
		return compartoUbicazione;
	}

	public void setCompartoUbicazione(CodeValueAteco compartoUbicazione){
		this.compartoUbicazione = compartoUbicazione;
	}

	/**
	*  javadoc for compartoRiferimento
	*/
	private CodeValueAteco compartoRiferimento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="compartoRiferimento")
	@ForeignKey(name="FK_pr_compartoRiferimento")
	//@Index(name="IX_pr_compartoRiferimento")
	public CodeValueAteco getCompartoRiferimento(){
		return compartoRiferimento;
	}

	public void setCompartoRiferimento(CodeValueAteco compartoRiferimento){
		this.compartoRiferimento = compartoRiferimento;
	}

	/**
	*  javadoc for compartoRichiedente
	*/
	private CodeValueAteco compartoRichiedente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="compartoRichiedente")
	@ForeignKey(name="FK_pr_compartoRichiedente")
	//@Index(name="IX_pr_compartoRichiedente")
	public CodeValueAteco getCompartoRichiedente(){
		return compartoRichiedente;
	}

	public void setCompartoRichiedente(CodeValueAteco compartoRichiedente){
		this.compartoRichiedente = compartoRichiedente;
	}

	/**
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="praticheRiferimenti")
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
			procpratiche.setPraticheRiferimenti(this);
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
			procpratiche.setPraticheRiferimenti(null);
		}

	}

	
	/**
	*  javadoc for richiedenteDitta
	*/
	private PersoneGiuridiche richiedenteDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiedente_ditta_id")
	@ForeignKey(name="FK_Prari_ricDitta")
	//@Index(name="IX_Prari_ricDitta")
	public PersoneGiuridiche getRichiedenteDitta(){
		return richiedenteDitta;
	}

	public void setRichiedenteDitta(PersoneGiuridiche richiedenteDitta){
		this.richiedenteDitta = richiedenteDitta;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Prari_sequence")
	@SequenceGenerator(name = "Prari_sequence", sequenceName = "Prari_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
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
	@ForeignKey(name="FK_Prtref_ubiEnt")
	//@Index(name="IX_Prtref_ubiEnt") 
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
	@ForeignKey(name="FK_Prtref_rifEnt")
	//@Index(name="IX_Prtref_rifEnt") 
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

	private CodeValue richiedente;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="richiedente")
	@ForeignKey(name="FK_Prari_richiedente")
	//@Index(name="IX_Prari_richiedente")
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
	@ForeignKey(name="FK_Prari_ricMedico")
	//@Index(name="IX_Prari_ricMedico")
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
	@ForeignKey(name="FK_Prari_ricInterno")
	//@Index(name="IX_Prari_ricInterno")
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
	@ForeignKey(name="FK_Prari_ricSede")
	//@Index(name="IX_Prari_ricSede")
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
	@ForeignKey(name="FK_Prari_ricUtente")
	//@Index(name="IX_Prari_ricUtente")
	public Person getRichiedenteUtente(){
		return richiedenteUtente;
	}

	public void setRichiedenteUtente(Person richiedenteUtente){
		this.richiedenteUtente = richiedenteUtente;
	}

	private CodeValue riferimento;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="riferimento")
	@ForeignKey(name="FK_Prari_riferimento")
	//@Index(name="IX_Prari_riferimento")
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
	@ForeignKey(name="FK_Prari_rifUtente")
	//@Index(name="IX_Prari_rifUtente")
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
	@ForeignKey(name="FK_Prari_rifCantiere")
	//@Index(name="IX_Prari_rifCantiere")
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
	@ForeignKey(name="FK_Prari_rifInterno")
	//@Index(name="IX_Prari_rifInterno")
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
	@ForeignKey(name="FK_Prari_rifDitta")
	//@Index(name="IX_Prari_rifDitta")
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
	@ForeignKey(name="FK_Prari_rifSede")
	//@Index(name="IX_Prari_rifSede")
	public Sedi getRiferimentoSede(){
		return riferimentoSede;
	}

	public void setRiferimentoSede(Sedi riferimentoSede){
		this.riferimentoSede = riferimentoSede;
	}
	
	private CodeValue ubicazione;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="ubicazione")
	@ForeignKey(name="FK_Prari_ubicazione")
	//@Index(name="IX_Prari_ubicazione")
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
	@ForeignKey(name="FK_Prari_ubDitta")
	//@Index(name="IX_Prari_ubDitta")
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
	@ForeignKey(name="FK_Prari_ubSede")
	//@Index(name="IX_Prari_ubSede")
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
	@ForeignKey(name="FK_Prari_ubUtente")
	//@Index(name="IX_Prari_ubUtente")
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
	@ForeignKey(name="FK_Prari_ubCantiere")
	//@Index(name="IX_Prari_ubCantiere")
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
}
