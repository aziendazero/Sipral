package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.TEL;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import com.phi.entities.baseEntity.SediInstallazione;


import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Impianto;

import com.phi.entities.baseEntity.IndirizzoSped;

import com.phi.entities.baseEntity.PersoneGiuridiche;

@Entity
@Table(name = "sedi")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Sedi extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7094423414800539012L;


	/**
	*  javadoc for pgEsterne
	*/
	private List<PersoneGiuridiche> pgEsterne;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	public List<PersoneGiuridiche> getPgEsterne() {
		return pgEsterne;
	}

	public void setPgEsterne(List<PersoneGiuridiche>list){
		pgEsterne = list;
	}

	public void addPgEsterne(PersoneGiuridiche pgEsterne) {
		if (this.pgEsterne == null) {
			this.pgEsterne = new ArrayList<PersoneGiuridiche>();
		}
		// add the association
		if(!this.pgEsterne.contains(pgEsterne)) {
			this.pgEsterne.add(pgEsterne);
			// make the inverse link
			if (pgEsterne.getSaEsterne() == null || !pgEsterne.getSaEsterne().contains(this))
				pgEsterne.addSaEsterne(this);
		}
	}

	public void removePgEsterne(PersoneGiuridiche pgEsterne) {
		if (this.pgEsterne == null) {
			this.pgEsterne = new ArrayList<PersoneGiuridiche>();
			return;
		}
		//add the association
		if(this.pgEsterne.contains(pgEsterne)){
			this.pgEsterne.remove(pgEsterne);
			//make the inverse link
			if (pgEsterne.getSaEsterne() != null && pgEsterne.getSaEsterne().contains(this))
			pgEsterne.removeSaEsterne(this);
		}
	}




	/**
	*  javadoc for elemento
	*/
	private String elemento;

	@Column(name="elemento")
	public String getElemento(){
		return elemento;
	}

	public void setElemento(String elemento){
		this.elemento = elemento;
	}

	/**
	*  javadoc for principaleAdd
	*/
	private Boolean principaleAdd;

	@Column(name="principale_add")
	public Boolean getPrincipaleAdd(){
		return principaleAdd;
	}

	public void setPrincipaleAdd(Boolean principaleAdd){
		this.principaleAdd = principaleAdd;
	}

	/**
	*  javadoc for soloInstImp
	*/
	private Boolean soloInstImp;

	@Column(name="solo_inst_imp")
	public Boolean getSoloInstImp(){
		return soloInstImp;
	}

	public void setSoloInstImp(Boolean soloInstImp){
		this.soloInstImp = soloInstImp;
	}

	/**
	*  javadoc for codiceUnivoco
	*/
	private String codiceUnivoco;

	@Column(name="codice_univoco")
	public String getCodiceUnivoco(){
		return codiceUnivoco;
	}

	public void setCodiceUnivoco(String codiceUnivoco){
		this.codiceUnivoco = codiceUnivoco;
	}


	/**
	*  javadoc for indirizzoSped
	*/
	private List<IndirizzoSped> indirizzoSped;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	public List<IndirizzoSped> getIndirizzoSped() {
		return indirizzoSped;
	}

	public void setIndirizzoSped(List<IndirizzoSped>list){
		indirizzoSped = list;
	}

	public void addIndirizzoSped(IndirizzoSped indirizzoSped) {
		if (this.indirizzoSped == null) {
			this.indirizzoSped = new ArrayList<IndirizzoSped>();
		}
		// add the association
		if(!this.indirizzoSped.contains(indirizzoSped)) {
			this.indirizzoSped.add(indirizzoSped);
			// make the inverse link
			if (indirizzoSped.getSedi() == null || !indirizzoSped.getSedi().contains(this))
				indirizzoSped.addSedi(this);
		}
	}

	public void removeIndirizzoSped(IndirizzoSped indirizzoSped) {
		if (this.indirizzoSped == null) {
			this.indirizzoSped = new ArrayList<IndirizzoSped>();
			return;
		}
		//add the association
		if(this.indirizzoSped.contains(indirizzoSped)){
			this.indirizzoSped.remove(indirizzoSped);
			//make the inverse link
			if (indirizzoSped.getSedi() != null && indirizzoSped.getSedi().contains(this))
			indirizzoSped.removeSedi(this);
		}
	}


	/**
	*  javadoc for sedeAddebito
	*/
	private Boolean sedeAddebito;

	@Column(name="sede_addebito")
	public Boolean getSedeAddebito(){
		return sedeAddebito;
	}

	public void setSedeAddebito(Boolean sedeAddebito){
		this.sedeAddebito = sedeAddebito;
	}


	/**
	*  javadoc for impianto
	*/
	private List<Impianto> impianto;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sedi")
	public List<Impianto> getImpianto() {
		return impianto;
	}

	public void setImpianto(List<Impianto>list){
		impianto = list;
	}

	public void addImpianto(Impianto impianto) {
		if (this.impianto == null) {
			this.impianto = new ArrayList<Impianto>();
		}
		// add the association
		if(!this.impianto.contains(impianto)) {
			this.impianto.add(impianto);
			// make the inverse link
			impianto.setSedi(this);
		}
	}

	public void removeImpianto(Impianto impianto) {
		if (this.impianto == null) {
			this.impianto = new ArrayList<Impianto>();
			return;
		}
		//add the association
		if(this.impianto.contains(impianto)){
			this.impianto.remove(impianto);
			//make the inverse link
			impianto.setSedi(null);
		}
	}


	/**
	*  javadoc for copy
	*/
	private Boolean copy;

	@Column(name="copy")
	public Boolean getCopy(){
		return copy;
	}

	public void setCopy(Boolean copy){
		this.copy = copy;
	}

	/**
	*  javadoc for tipoUtente
	*/
	private CodeValuePhi tipoUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoUtente")
	@ForeignKey(name="FK_SediAddebito_tipoUtente")
	//@Index(name="IX_SediAddebito_tipoUtente")
	public CodeValuePhi getTipoUtente(){
		return tipoUtente;
	}

	public void setTipoUtente(CodeValuePhi tipoUtente){
		this.tipoUtente = tipoUtente;
	}
	
	/**
	*  javadoc for tipoAttivita
	*/
	private CodeValuePhi tipoAttivita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoAttivita")
	@ForeignKey(name="FK_SediAddebito_tipoAttivita")
	//@Index(name="IX_SediAddebito_tipoAttivita")
	public CodeValuePhi getTipoAttivita(){
		return tipoAttivita;
	}

	public void setTipoAttivita(CodeValuePhi tipoAttivita){
		this.tipoAttivita = tipoAttivita;
	}
	
	/**
	*  javadoc for settore
	*/
	private CodeValuePhi settore;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="settore")
	@ForeignKey(name="FK_SediAddebito_settore")
	//@Index(name="IX_SediAddebito_settore")
	public CodeValuePhi getSettore(){
		return settore;
	}

	public void setSettore(CodeValuePhi settore){
		this.settore = settore;
	}
	
	/**
	*  javadoc for esenzione
	*/
	private CodeValuePhi esenzione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esenzione")
	@ForeignKey(name="FK_SediAddebito_esenzione")
	//@Index(name="IX_SediAddebito_esenzione")
	public CodeValuePhi getEsenzione(){
		return esenzione;
	}

	public void setEsenzione(CodeValuePhi esenzione){
		this.esenzione = esenzione;
	}
	
	/**
	*  javadoc for classeEconomica
	*/
	private CodeValuePhi classeEconomica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="classeEconomica")
	@ForeignKey(name="FK_SediAddebito_classeEconomica")
	//@Index(name="IX_SediAddebito_classeEconomica")
	public CodeValuePhi getClasseEconomica(){
		return classeEconomica;
	}

	public void setClasseEconomica(CodeValuePhi classeEconomica){
		this.classeEconomica = classeEconomica;
	}
	
	/**
	*  javadoc for impSpesa
	*/
	private String impSpesa;

	@Column(name="imp_spesa")
	public String getImpSpesa(){
		return impSpesa;
	}

	public void setImpSpesa(String impSpesa){
		this.impSpesa = impSpesa;
	}
	
	/**
	*  javadoc for codContabilita
	*/
	private String codContabilita;

	@Column(name="cod_contabilita")
	public String getCodContabilita(){
		return codContabilita;
	}

	public void setCodContabilita(String codContabilita){
		this.codContabilita = codContabilita;
	}
	
	/**
	*  javadoc for cig
	*/
	private String cig;

	@Column(name="cig")
	public String getCig(){
		return cig;
	}

	public void setCig(String cig){
		this.cig = cig;
	}
	
	/**
	*  javadoc for indirizzoSpedPrinc
	*/
	private IndirizzoSped indirizzoSpedPrinc;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="indirizzo_sped_princ_id")
	@ForeignKey(name="FK_SdiAddbito_ndrzzoSpdPrnc")
	//@Index(name="IX_SdiAddbito_ndrzzoSpdPrnc")
	public IndirizzoSped getIndirizzoSpedPrinc(){
		return indirizzoSpedPrinc;
	}

	public void setIndirizzoSpedPrinc(IndirizzoSped indirizzoSpedPrinc){
		this.indirizzoSpedPrinc = indirizzoSpedPrinc;
	}
	
	/**
	*  javadoc for codiceVia
	*/
	private CodeValuePhi codiceVia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codiceVia")
	@ForeignKey(name="FK_SediAddebito_codiceVia")
	//@Index(name="IX_SediAddebito_codiceVia")
	public CodeValuePhi getCodiceVia(){
		return codiceVia;
	}

	public void setCodiceVia(CodeValuePhi codiceVia){
		this.codiceVia = codiceVia;
	}

	/**
	*  javadoc for zona
	*/
	private CodeValuePhi zona;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="zona")
	@ForeignKey(name="FK_SediAddebito_zona")
	//@Index(name="IX_SediAddebito_zona")
	public CodeValuePhi getZona(){
		return zona;
	}

	public void setZona(CodeValuePhi zona){
		this.zona = zona;
	}

	/**
	*  javadoc for territorio
	*
	private CodeValuePhi territorio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="territorio")
	@ForeignKey(name="FK_SediAddebito_territorio")
	//@Index(name="IX_SediAddebito_territorio")
	public CodeValuePhi getTerritorio(){
		return territorio;
	}

	public void setTerritorio(CodeValuePhi territorio){
		this.territorio = territorio;
	}*/

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}
	
	/**
	*  javadoc for deleatable
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
	*  javadoc for sediAddebito
	*/
	/*private List<SediAddebito> sediAddebito;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sede", cascade=CascadeType.PERSIST)
	public List<SediAddebito> getSediAddebito() {
		return sediAddebito;
	}

	public void setSediAddebito(List<SediAddebito>list){
		sediAddebito = list;
	}

	public void addSediAddebito(SediAddebito sediAddebito) {
		if (this.sediAddebito == null) {
			this.sediAddebito = new ArrayList<SediAddebito>();
		}
		// add the association
		if(!this.sediAddebito.contains(sediAddebito)) {
			this.sediAddebito.add(sediAddebito);
			// make the inverse link
			sediAddebito.setSede(this);
		}
	}

	public void removeSediAddebito(SediAddebito sediAddebito) {
		if (this.sediAddebito == null) {
			this.sediAddebito = new ArrayList<SediAddebito>();
			return;
		}
		//add the association
		if(this.sediAddebito.contains(sediAddebito)){
			this.sediAddebito.remove(sediAddebito);
			//make the inverse link
			sediAddebito.setSede(null);
		}
	}*/

	/**
	*  javadoc for sediInstallazione
	*/
	private List<SediInstallazione> sediInstallazione;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sede", cascade=CascadeType.PERSIST)
	public List<SediInstallazione> getSediInstallazione() {
		return sediInstallazione;
	}

	public void setSediInstallazione(List<SediInstallazione>list){
		sediInstallazione = list;
	}

	public void addSediInstallazione(SediInstallazione sediInstallazione) {
		if (this.sediInstallazione == null) {
			this.sediInstallazione = new ArrayList<SediInstallazione>();
		}
		// add the association
		if(!this.sediInstallazione.contains(sediInstallazione)) {
			this.sediInstallazione.add(sediInstallazione);
			// make the inverse link
			sediInstallazione.setSede(this);
		}
	}

	public void removeSediInstallazione(SediInstallazione sediInstallazione) {
		if (this.sediInstallazione == null) {
			this.sediInstallazione = new ArrayList<SediInstallazione>();
			return;
		}
		//add the association
		if(this.sediInstallazione.contains(sediInstallazione)){
			this.sediInstallazione.remove(sediInstallazione);
			//make the inverse link
			sediInstallazione.setSede(null);
		}
	}


	/**
	*  javadoc for internalId
	*/
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Sedi_sequence")
	@SequenceGenerator(name = "Sedi_sequence", sequenceName = "Sedi_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for personaGiuridica
	*/
	private PersoneGiuridiche personaGiuridica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persona_giuridica_id")
	@ForeignKey(name="FK_Sedi_personaGiuridica")
	//@Index(name="IX_Sedi_personaGiuridica")
	public PersoneGiuridiche getPersonaGiuridica(){
		return personaGiuridica;
	}

	public void setPersonaGiuridica(PersoneGiuridiche personaGiuridica){
		this.personaGiuridica = personaGiuridica;
	}
	
	/**
	*  Provincia CCIAA
	*/
	private String provinciaCCIAA;

	@Column(name="provincia_cciaa")
	public String getProvinciaCCIAA() {
		return provinciaCCIAA;
	}

	public void setProvinciaCCIAA(String provinciaCCIAA) {
		this.provinciaCCIAA = provinciaCCIAA;
	}
	
	/**
	*  Numero REA
	*/
	private String numeroREA;

	@Column(name="numero_rea")
	public String getNumeroREA() {
		return numeroREA;
	}

	public void setNumeroREA(String numeroREA) {
		this.numeroREA = numeroREA;
	}
	
	/**
	*  javadoc for SEDE PRINCIPALE Booleano true/false - Se true avrà progressivo 0
	*/
	private boolean sedePrincipale = false;
	
    @Column(name="sede_principale", nullable=false)
	public boolean getSedePrincipale() {
		return sedePrincipale;
	}

	public void setSedePrincipale(boolean sedePrincipale) {
		this.sedePrincipale = sedePrincipale;
	}
	
	/**
	 * javadoc for TIPOLOGIA (SEDE/UNITA LOCALE)
	 */
	private CodeValue tipologia;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "TIPOLOGIA")
	@ForeignKey(name = "FK_Tipologia")
	//@Index(name="IX_Tipologia")
	public CodeValue getTipologia() {
		return tipologia;
	}

	public void setTipologia(CodeValue tipologia) {
		this.tipologia = tipologia;
	}
	
	/**
	*  javadoc for Progressivo unità locale
	*/
	private Integer progressivoUnitaLocale;

	@Column(name="progressivo_unita_locale")
	public Integer getProgressivoUnitaLocale(){
		return progressivoUnitaLocale;
	}

	public void setProgressivoUnitaLocale(Integer progressivoUnitaLocale){
		this.progressivoUnitaLocale = progressivoUnitaLocale;
	}
	
	/**
	 * javadoc for TIPO 1
	 */
	private CodeValue tipo1;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "tipo_1")
	@ForeignKey(name = "FK_Tipo_1")
	//@Index(name="IX_Tipo_1")
	public CodeValue getTipo1() {
		return tipo1;
	}

	public void setTipo1(CodeValue tipo1) {
		this.tipo1 = tipo1;
	}
	
	/**
	 * javadoc for TIPO 2
	 */
	private CodeValue tipo2;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "tipo_2")
	@ForeignKey(name = "FK_Tipo_2")
	//@Index(name="IX_Tipo_2")
	public CodeValue getTipo2() {
		return tipo2;
	}
	
	public void setTipo2(CodeValue tipo2) {
		this.tipo2 = tipo2;
	}
	
	/**
	 * javadoc for TIPO 3
	 */
	private CodeValue tipo3;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "tipo_3")
	@ForeignKey(name = "FK_Tipo_3")
	//@Index(name="IX_Tipo_3")
	public CodeValue getTipo3() {
		return tipo3;
	}

	public void setTipo3(CodeValue tipo3) {
		this.tipo3 = tipo3;
	}
	
	/**
	 * javadoc for TIPO 4
	 */
	private CodeValue tipo4;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "tipo_4")
	@ForeignKey(name = "FK_Tipo_4")
	//@Index(name="IX_Tipo_4")
	public CodeValue getTipo4() {
		return tipo4;
	}

	public void setTipo4(CodeValue tipo4) {
		this.tipo4 = tipo4;
	}
	
	/**
	 * javadoc for TIPO 5
	 */
	private CodeValue tipo5;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "tipo_5")
	@ForeignKey(name = "FK_Tipo_5")
	//@Index(name="IX_Tipo_5")
	public CodeValue getTipo5() {
		return tipo5;
	}

	public void setTipo5(CodeValue tipo5) {
		this.tipo5 = tipo5;
	}
	
	/**
	*  Denominazione unità locale
	*/	
	private String denominazioneUnitaLocale;

	@Column(name="denominazione_unita_locale")
	public String getDenominazioneUnitaLocale() {
		return denominazioneUnitaLocale;
	}

	public void setDenominazioneUnitaLocale(String denominazioneUnitaLocale) {
		this.denominazioneUnitaLocale = denominazioneUnitaLocale;
	}
	
	/**
	*  Insegna
	*/	
	private String insegna;

	@Column(name="insegna")
	public String getInsegna() {
		return insegna;
	}

	public void setInsegna(String insegna) {
		this.insegna = insegna;
	}
	
	/**
	 * javadoc for STATO
	 */
	private CodeValueCountry stato;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValueCountry.class)
	@JoinColumn(name = "stato")
	@ForeignKey(name = "FK_Stato")
	//@Index(name="IX_Stato")
	public CodeValueCountry getStato() {
		return stato;
	}

	public void setStato(CodeValueCountry stato) {
		this.stato = stato;
	}
	
	/**
	 *  Address
	 */
	private AD addr;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="cen", column=@Column(name="toponimo")),
		@AttributeOverride(name="bnr", column=@Column(name="n_civico")),
		@AttributeOverride(name="adl", column=@Column(name="frazione")),
		@AttributeOverride(name="cnt", column=@Column(name="altre_indicazioni")),
		@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),//
		@AttributeOverride(name="cty", column=@Column(name="addr_cty")),//
		@AttributeOverride(name="sta", column=@Column(name="stradario")),
		@AttributeOverride(name="str", column=@Column(name="via")),
		@AttributeOverride(name="stb", column=@Column(name="addr_str")),
		@AttributeOverride(name="zip", column=@Column(name="addr_zip"))//
	})
	
	public AD getAddr(){
		return addr;
	}

	public void setAddr(AD addr){
		this.addr = addr;
	}
	
	/**
	 *  Telecom
	 */
	private TEL telecom;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="as", column=@Column(name="telefono")),
		@AttributeOverride(name="bad", column=@Column(name="fax")),
		@AttributeOverride(name="mail", column=@Column(name="pec")),
		//@AttributeOverride(name="dir", column=@Column(name="telecom_dir")),
		//@AttributeOverride(name="ec", column=@Column(name="telecom_ec")),
		//@AttributeOverride(name="h", column=@Column(name="telecom_h")),
		//@AttributeOverride(name="hp", column=@Column(name="telecom_hp")),
		//@AttributeOverride(name="hv", column=@Column(name="telecom_hv")),
		//@AttributeOverride(name="mc", column=@Column(name="telecom_mc")),
		//@AttributeOverride(name="pg", column=@Column(name="telecom_pg")),
		//@AttributeOverride(name="pub", column=@Column(name="telecom_pub")),
		//@AttributeOverride(name="sip", column=@Column(name="telecom_sip")),
		//@AttributeOverride(name="tmp", column=@Column(name="telecom_tmp")),
		//@AttributeOverride(name="wp", column=@Column(name="telecom_wp"))
	})
	
	public TEL getTelecom(){
		return telecom;
	}

	public void setTelecom(TEL telecom){
		this.telecom = telecom;
	}
	
	/**
	*  Attività
	*/	
	private String attivita;

	@Column(name="attivita")
	public String getAttivita() {
		return attivita;
	}

	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}
	
	/**
	*  Data inizio attività
	*/
	private Date dataInizioAttivita;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="data_inizio_attivita")
	public Date getDataInizioAttivita() {
		return dataInizioAttivita;
	}

	public void setDataInizioAttivita(Date dataInizioAttivita) {
		this.dataInizioAttivita = dataInizioAttivita;
	}
	
	/**
	*  Stato attività (I/S/Nullo)
	*/
	private CodeValue statoAttivita;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="stato_attivita")
	@ForeignKey(name="FK_Stat_att")
	//@Index(name="IX_Stat_att")
	public CodeValue getStatoAttivita() {
		return statoAttivita;
	}
	
	public void setStatoAttivita(CodeValue statoAttivita) {
	this.statoAttivita = statoAttivita;
	}
	
	/**
	*  Data cancellazione
	*/
	private Date dataCancellazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="data_cancellazione")
	public Date getDataCancellazione() {
		return dataCancellazione;
	}

	public void setDataCancellazione(Date dataCancellazione) {
		this.dataCancellazione = dataCancellazione;
	}
	
	/**
	*  Data cessazione
	*/
	private Date dataCessazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="data_cessazione")
	public Date getDataCessazione() {
		return dataCessazione;
	}

	public void setDataCessazione(Date dataCessazione) {
		this.dataCessazione = dataCessazione;
	}
	
	/**
	*  Data denuncia cessazione
	*/
	private Date dataDenunciaCessazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="data_denuncia_cessazione")
	public Date getDataDenunciaCessazione() {
		return dataDenunciaCessazione;
	}

	public void setDataDenunciaCessazione(Date dataDenunciaCessazione) {
		this.dataDenunciaCessazione = dataDenunciaCessazione;
	}
	
	/**
	*  Causale cessazione
	*/
	private String causaleCessazione;

	@Column(name="causale_cessazione")
	public String getCausaleCessazione() {
		return causaleCessazione;
	}

	public void setCausaleCessazione(String causaleCessazione) {
		this.causaleCessazione = causaleCessazione;
	}
	
}
