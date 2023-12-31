package com.phi.entities.baseEntity;

/**
 * Vigilanza generated by hbm2java
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValuePhi;
@Entity
@Table(name = "vigilanza")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Vigilanza extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7037290729845149350L;

	/**
	*  javadoc for ubicazione
	*/
	private String ubicazione;

	@Column(name="ubicazione")
	public String getUbicazione(){
		return ubicazione;
	}

	public void setUbicazione(String ubicazione){
		this.ubicazione = ubicazione;
	}


	/**
	*  javadoc for sitoBonificaDitta
	*/
	private PersoneGiuridiche sitoBonificaDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sito_bonifica_ditta_id")
	@ForeignKey(name="FK_Vigilanza_sitoBonfcaDtta")
	//@Index(name="IX_Vigilanza_sitoBonfcaDtta")
	public PersoneGiuridiche getSitoBonificaDitta(){
		return sitoBonificaDitta;
	}

	public void setSitoBonificaDitta(PersoneGiuridiche sitoBonificaDitta){
		this.sitoBonificaDitta = sitoBonificaDitta;
	}



	/**
	*  javadoc for sitoBonificaSede
	*/
	private Sedi sitoBonificaSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sito_bonifica_sede_id")
	@ForeignKey(name="FK_Vigilanza_sitoBonfcaSede")
	//@Index(name="IX_Vigilanza_sitoBonfcaSede")
	public Sedi getSitoBonificaSede(){
		return sitoBonificaSede;
	}

	public void setSitoBonificaSede(Sedi sitoBonificaSede){
		this.sitoBonificaSede = sitoBonificaSede;
	}


	/**
	*  javadoc for comparto
	*/
	private CodeValueAteco comparto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comparto")
	@ForeignKey(name="FK_Vigilanza_comparto")
	//@Index(name="IX_Vigilanza_comparto")
	public CodeValueAteco getComparto(){
		return comparto;
	}

	public void setComparto(CodeValueAteco comparto){
		this.comparto = comparto;
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
	*  javadoc for specificazione
	*/
	private String specificazione;

	@Column(name="specificazione")
	public String getSpecificazione(){
		return specificazione;
	}

	public void setSpecificazione(String specificazione){
		this.specificazione = specificazione;
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
	*  javadoc for imo
	*/
	private String imo;

	@Column(name="imo")
	public String getImo(){
		return imo;
	}

	public void setImo(String imo){
		this.imo = imo;
	}

	/**
	*  javadoc for entita
	*/
	private CodeValuePhi entita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="entita")
	@ForeignKey(name="FK_Vigilanza_entita")
	//@Index(name="IX_Vigilanza_entita")
	public CodeValuePhi getEntita(){
		return entita;
	}

	public void setEntita(CodeValuePhi entita){
		this.entita = entita;
	}

	/**
	*  javadoc for committente
	*/
	private String committente;

	@Column(name="committente")
	public String getCommittente(){
		return committente;
	}

	public void setCommittente(String committente){
		this.committente = committente;
	}


	/**
	*  javadoc for tipoConfinamento
	*/
	private CodeValuePhi tipoConfinamento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoConfinamento")
	@ForeignKey(name="FK_Vigilanza_tipoConfinamento")
	//@Index(name="IX_Vigilanza_tipoConfinamento")
	public CodeValuePhi getTipoConfinamento(){
		return tipoConfinamento;
	}

	public void setTipoConfinamento(CodeValuePhi tipoConfinamento){
		this.tipoConfinamento = tipoConfinamento;
	}

	/**
	*  javadoc for numLavoratori
	*/
	private Integer numLavoratori;

	@Column(name="num_lavoratori")
	public Integer getNumLavoratori(){
		return numLavoratori;
	}

	public void setNumLavoratori(Integer numLavoratori){
		this.numLavoratori = numLavoratori;
	}

	/**
	*  javadoc for bonificatiMq
	*/
	private Double bonificatiMq;

	@Column(name="bonificati_mq")
	public Double getBonificatiMq(){
		return bonificatiMq;
	}

	public void setBonificatiMq(Double bonificatiMq){
		this.bonificatiMq = bonificatiMq;
	}

	/**
	*  javadoc for bonificatiKg
	*/
	private Double bonificatiKg;

	@Column(name="bonificati_kg")
	public Double getBonificatiKg(){
		return bonificatiKg;
	}

	public void setBonificatiKg(Double bonificatiKg){
		this.bonificatiKg = bonificatiKg;
	}

	/**
	*  javadoc for bonificatiMqEffettivi
	*/
	private Double bonificatiMqEffettivi;

	@Column(name="bonificati_mq_effettivi")
	public Double getBonificatiMqEffettivi(){
		return bonificatiMqEffettivi;
	}

	public void setBonificatiMqEffettivi(Double bonificatiMqEffettivi){
		this.bonificatiMqEffettivi = bonificatiMqEffettivi;
	}

	/**
	*  javadoc for bonificatiKgEffettivi
	*/
	private Double bonificatiKgEffettivi;

	@Column(name="bonificati_kg_effettivi")
	public Double getBonificatiKgEffettivi(){
		return bonificatiKgEffettivi;
	}

	public void setBonificatiKgEffettivi(Double bonificatiKgEffettivi){
		this.bonificatiKgEffettivi = bonificatiKgEffettivi;
	}

	/**
	*  javadoc for effettivoDurataLavori
	*/
	private String effettivoDurataLavori;

	@Column(name="effettivo_durata_lavori")
	public String getEffettivoDurataLavori(){
		return effettivoDurataLavori;
	}

	public void setEffettivoDurataLavori(String effettivoDurataLavori){
		this.effettivoDurataLavori = effettivoDurataLavori;
	}

	/**
	*  javadoc for effettivoFineLavori
	*/
	private Date effettivoFineLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="effettivo_fine_lavori")
	public Date getEffettivoFineLavori(){
		return effettivoFineLavori;
	}

	public void setEffettivoFineLavori(Date effettivoFineLavori){
		this.effettivoFineLavori = effettivoFineLavori;
	}

	/**
	*  javadoc for effettivoInizioLavori
	*/
	private Date effettivoInizioLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="effettivo_inizio_lavori")
	public Date getEffettivoInizioLavori(){
		return effettivoInizioLavori;
	}

	public void setEffettivoInizioLavori(Date effettivoInizioLavori){
		this.effettivoInizioLavori = effettivoInizioLavori;
	}

	/**
	*  javadoc for iscrizioneAlbo
	*/
	private String iscrizioneAlbo;

	@Column(name="iscrizione_albo")
	public String getIscrizioneAlbo(){
		return iscrizioneAlbo;
	}

	public void setIscrizioneAlbo(String iscrizioneAlbo){
		this.iscrizioneAlbo = iscrizioneAlbo;
	}

	/**
	*  javadoc for tipoSegnalazione
	*/
	private CodeValuePhi tipoSegnalazione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoSegnalazione")
	@ForeignKey(name="FK_Vigilanza_tipoSegnalazione")
	//@Index(name="IX_Vigilanza_tipoSegnalazione")
	public CodeValuePhi getTipoSegnalazione(){
		return tipoSegnalazione;
	}

	public void setTipoSegnalazione(CodeValuePhi tipoSegnalazione){
		this.tipoSegnalazione = tipoSegnalazione;
	}

	/**
	*  javadoc for reason
	*/
	private List<CodeValuePhi> reason;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="vigilanza_reason", joinColumns = { @JoinColumn(name="Vigilanza_id") }, inverseJoinColumns = { @JoinColumn(name="reason") })
	@ForeignKey(name="FK_reason_Vigilanza", inverseName="FK_Vigilanza_reason")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getReason(){
		return reason;
	}

	public void setReason(List<CodeValuePhi> reason){
		this.reason = reason;
	}


	/**
	*  javadoc for operaiAmianto
	*/
	private List<OperaioAmianto> operaiAmianto;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="vigilanza", cascade=CascadeType.PERSIST)
	public List<OperaioAmianto> getOperaiAmianto() {
		return operaiAmianto;
	}

	public void setOperaiAmianto(List<OperaioAmianto>list){
		operaiAmianto = list;
	}

	public void addOperaiAmianto(OperaioAmianto operaiAmianto) {
		if (this.operaiAmianto == null) {
			this.operaiAmianto = new ArrayList<OperaioAmianto>();
		}
		// add the association
		if(!this.operaiAmianto.contains(operaiAmianto)) {
			this.operaiAmianto.add(operaiAmianto);
			// make the inverse link
			operaiAmianto.setVigilanza(this);
		}
	}

	public void removeOperaiAmianto(OperaioAmianto operaiAmianto) {
		if (this.operaiAmianto == null) {
			this.operaiAmianto = new ArrayList<OperaioAmianto>();
			return;
		}
		//add the association
		if(this.operaiAmianto.contains(operaiAmianto)){
			this.operaiAmianto.remove(operaiAmianto);
			//make the inverse link
			operaiAmianto.setVigilanza(null);
		}
	}


	/**
	*  javadoc for miglioramentiIndotti
	*/
	private String miglioramentiIndotti;

	@Column(name="miglioramenti_indotti")
	public String getMiglioramentiIndotti(){
		return miglioramentiIndotti;
	}

	public void setMiglioramentiIndotti(String miglioramentiIndotti){
		this.miglioramentiIndotti = miglioramentiIndotti;
	}

	/**
	*  javadoc for presuntoInizioLavori
	*/
	private Date presuntoInizioLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="presunto_inizio_lavori")
	public Date getPresuntoInizioLavori(){
		return presuntoInizioLavori;
	}

	public void setPresuntoInizioLavori(Date presuntoInizioLavori){
		this.presuntoInizioLavori = presuntoInizioLavori;
	}

	/**
	*  javadoc for durataLavori
	*/
	private String durataLavori;

	@Column(name="durata_lavori")
	public String getDurataLavori(){
		return durataLavori;
	}

	public void setDurataLavori(String durataLavori){
		this.durataLavori = durataLavori;
	}

	/**
	*  javadoc for descrizione
	*/
	private String descrizione;

	@Column(name="descrizione", length=4000)
	public String getDescrizione(){
		return descrizione;
	}

	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}

	/**
	*  javadoc for tipoIntervento
	*/
	private CodeValuePhi tipoIntervento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoIntervento")
	@ForeignKey(name="FK_Vigilanza_tipoIntervento")
	//@Index(name="IX_Vigilanza_tipoIntervento")
	public CodeValuePhi getTipoIntervento(){
		return tipoIntervento;
	}

	public void setTipoIntervento(CodeValuePhi tipoIntervento){
		this.tipoIntervento = tipoIntervento;
	}

	/**
	*  javadoc for compatto
	*/
	private Boolean compatto;

	@Column(name="compatto")
	public Boolean getCompatto(){
		return compatto;
	}

	public void setCompatto(Boolean compatto){
		this.compatto = compatto;
	}

	/**
	*  javadoc for friabile
	*/
	private Boolean friabile;

	@Column(name="friabile")
	public Boolean getFriabile(){
		return friabile;
	}

	public void setFriabile(Boolean friabile){
		this.friabile = friabile;
	}

	/**
	*  javadoc for cantiere
	*/
	private Cantiere cantiere;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_Vigilanza_cantiere")
	//@Index(name="IX_Vigilanza_cantiere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}

	/**
	*  javadoc for personaRuolo
	*/
	private List<PersonaRuolo> personaRuolo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="vigilanza", cascade=CascadeType.PERSIST)
	public List<PersonaRuolo> getPersonaRuolo() {
		return personaRuolo;
	}

	public void setPersonaRuolo(List<PersonaRuolo>list){
		personaRuolo = list;
	}

	public void addPersonaRuolo(PersonaRuolo personaRuolo) {
		if (this.personaRuolo == null) {
			this.personaRuolo = new ArrayList<PersonaRuolo>();
		}
		// add the association
		if(!this.personaRuolo.contains(personaRuolo)) {
			this.personaRuolo.add(personaRuolo);
			// make the inverse link
			personaRuolo.setVigilanza(this);
		}
	}

	public void removePersonaRuolo(PersonaRuolo personaRuolo) {
		if (this.personaRuolo == null) {
			this.personaRuolo = new ArrayList<PersonaRuolo>();
			return;
		}
		//add the association
		if(this.personaRuolo.contains(personaRuolo)){
			this.personaRuolo.remove(personaRuolo);
			//make the inverse link
			personaRuolo.setVigilanza(null);
		}
	}

	/**
	*  javadoc for personaGiuridicaSede
	*/
	private List<PersonaGiuridicaSede> personaGiuridicaSede;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="vigilanza", cascade=CascadeType.PERSIST)
	public List<PersonaGiuridicaSede> getPersonaGiuridicaSede() {
		return personaGiuridicaSede;
	}

	public void setPersonaGiuridicaSede(List<PersonaGiuridicaSede>list){
		personaGiuridicaSede = list;
	}

	public void addPersonaGiuridicaSede(PersonaGiuridicaSede personaGiuridicaSede) {
		if (this.personaGiuridicaSede == null) {
			this.personaGiuridicaSede = new ArrayList<PersonaGiuridicaSede>();
		}
		// add the association
		if(!this.personaGiuridicaSede.contains(personaGiuridicaSede)) {
			this.personaGiuridicaSede.add(personaGiuridicaSede);
			// make the inverse link
			personaGiuridicaSede.setVigilanza(this);
		}
	}

	public void removePersonaGiuridicaSede(PersonaGiuridicaSede personaGiuridicaSede) {
		if (this.personaGiuridicaSede == null) {
			this.personaGiuridicaSede = new ArrayList<PersonaGiuridicaSede>();
			return;
		}
		//add the association
		if(this.personaGiuridicaSede.contains(personaGiuridicaSede)){
			this.personaGiuridicaSede.remove(personaGiuridicaSede);
			//make the inverse link
			personaGiuridicaSede.setVigilanza(null);
		}
	}

	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@OneToOne(fetch=FetchType.LAZY, mappedBy="vigilanza"/*, cascade=CascadeType.PERSIST*/)
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Vigilanza_sequence")
	@SequenceGenerator(name = "Vigilanza_sequence", sequenceName = "Vigilanza_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="type")
	@ForeignKey(name="FK_Vig_type")
	//@Index(name="IX_Vig_type")
	public CodeValuePhi getType() {
		return type;
	}

	public void setType(CodeValuePhi type) {
		this.type = type;
	}
	
	/**
	*  javadoc for date - Data vigilanza
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
	
}
