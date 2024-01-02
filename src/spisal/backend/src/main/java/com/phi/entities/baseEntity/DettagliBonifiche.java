package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.Protocollo;

import com.phi.entities.dataTypes.CodeValue;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Person;

import com.phi.entities.baseEntity.PersoneGiuridiche;

import com.phi.entities.baseEntity.Sedi;
@javax.persistence.Entity
@Table(name = "dettagli_bonifiche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DettagliBonifiche extends BaseEntity {

	private static final long serialVersionUID = 2027849650L;	


	/**
	*  javadoc for committenteBonificaSede
	*/
	private Sedi committenteBonificaSede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_bonifica_sede_id")
	@ForeignKey(name="FK_DttglBnfch_cmmttntBnfcSd")
	//@Index(name="IX_DttglBnfch_cmmttntBnfcSd")
	public Sedi getCommittenteBonificaSede(){
		return committenteBonificaSede;
	}

	public void setCommittenteBonificaSede(Sedi committenteBonificaSede){
		this.committenteBonificaSede = committenteBonificaSede;
	}



	/**
	*  javadoc for committenteBonificaDitta
	*/
	private PersoneGiuridiche committenteBonificaDitta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_bonifica_ditta_id")
	@ForeignKey(name="FK_DttglBnfch_cmmttntBnfcDtt")
	//@Index(name="IX_DttglBnfch_cmmttntBnfcDtt")
	public PersoneGiuridiche getCommittenteBonificaDitta(){
		return committenteBonificaDitta;
	}

	public void setCommittenteBonificaDitta(PersoneGiuridiche committenteBonificaDitta){
		this.committenteBonificaDitta = committenteBonificaDitta;
	}



	/**
	*  javadoc for committenteBonificaUtente
	*/
	private Person committenteBonificaUtente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committente_bonifica_utente_id")
	@ForeignKey(name="FK_DttglBnfch_cmmttntBnfctnt")
	//@Index(name="IX_DttglBnfch_cmmttntBnfctnt")
	public Person getCommittenteBonificaUtente(){
		return committenteBonificaUtente;
	}

	public void setCommittenteBonificaUtente(Person committenteBonificaUtente){
		this.committenteBonificaUtente = committenteBonificaUtente;
	}


	/**
	*  javadoc for committenteBonifica
	*/
	private CodeValuePhi committenteBonifica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="committenteBonifica")
	@ForeignKey(name="FK_DettagliBonifiche_committenteBonifica")
	//@Index(name="IX_DettagliBonifiche_committenteBonifica")
	public CodeValuePhi getCommittenteBonifica(){
		return committenteBonifica;
	}

	public void setCommittenteBonifica(CodeValuePhi committenteBonifica){
		this.committenteBonifica = committenteBonifica;
	}

	/**
	*  javadoc for gg
	*/
	private CodeValuePhi gg;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gg")
	@ForeignKey(name="FK_DettagliBonifiche_gg")
	//@Index(name="IX_DettagliBonifiche_gg")
	public CodeValuePhi getGg(){
		return gg;
	}

	public void setGg(CodeValuePhi gg){
		this.gg = gg;
	}

	/**
	*  javadoc for terminiRisposta
	*/
	private Date terminiRisposta;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="termini_risposta")
	public Date getTerminiRisposta(){
		return terminiRisposta;
	}

	public void setTerminiRisposta(Date terminiRisposta){
		this.terminiRisposta = terminiRisposta;
	}

	/**
	*  javadoc for ruolo
	*/
	private CodeValuePhi ruolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ruolo")
	@ForeignKey(name="FK_DettagliBonifiche_ruolo")
	//@Index(name="IX_DettagliBonifiche_ruolo")
	public CodeValuePhi getRuolo(){
		return ruolo;
	}

	public void setRuolo(CodeValuePhi ruolo){
		this.ruolo = ruolo;
	}

	/**
	*  javadoc for ore
	*/
	private Integer ore;

	@Column(name="ore")
	public Integer getOre(){
		return ore;
	}

	public void setOre(Integer ore){
		this.ore = ore;
	}

	/**
	*  javadoc for lavoratori
	*/
	private Integer lavoratori;

	@Column(name="lavoratori")
	public Integer getLavoratori(){
		return lavoratori;
	}

	public void setLavoratori(Integer lavoratori){
		this.lavoratori = lavoratori;
	}

	/**
	*  javadoc for friabileRimosso
	*/
	private Integer friabileRimosso;

	@Column(name="friabile_rimosso")
	public Integer getFriabileRimosso(){
		return friabileRimosso;
	}

	public void setFriabileRimosso(Integer friabileRimosso){
		this.friabileRimosso = friabileRimosso;
	}

	/**
	*  javadoc for compattoRimosso
	*/
	private Integer compattoRimosso;

	@Column(name="compatto_rimosso")
	public Integer getCompattoRimosso(){
		return compattoRimosso;
	}

	public void setCompattoRimosso(Integer compattoRimosso){
		this.compattoRimosso = compattoRimosso;
	}

	/**
	*  javadoc for annoRelazione
	*/
	private Integer annoRelazione;

	@Column(name="anno_relazione")
	public Integer getAnnoRelazione(){
		return annoRelazione;
	}

	public void setAnnoRelazione(Integer annoRelazione){
		this.annoRelazione = annoRelazione;
	}

	/**
	*  javadoc for altro
	*/
	private String altro;

	@Column(name="altro")
	public String getAltro(){
		return altro;
	}

	public void setAltro(String altro){
		this.altro = altro;
	}

	/**
	*  javadoc for misure
	*/
	private String misure;

	@Column(name="misure")
	public String getMisure(){
		return misure;
	}

	public void setMisure(String misure){
		this.misure = misure;
	}

	/**
	*  javadoc for attivita
	*/
	private String attivita;

	@Column(name="attivita")
	public String getAttivita(){
		return attivita;
	}

	public void setAttivita(String attivita){
		this.attivita = attivita;
	}

	/**
	*  javadoc for codRifiuto
	*/
	private String codRifiuto;

	@Column(name="cod_rifiuto")
	public String getCodRifiuto(){
		return codRifiuto;
	}

	public void setCodRifiuto(String codRifiuto){
		this.codRifiuto = codRifiuto;
	}

	/**
	*  javadoc for nlav
	*/
	private Integer nlav;

	@Column(name="nlav")
	public Integer getNlav(){
		return nlav;
	}

	public void setNlav(Integer nlav){
		this.nlav = nlav;
	}

	/**
	*  javadoc for durata
	*/
	private Integer durata;

	@Column(name="durata")
	public Integer getDurata(){
		return durata;
	}

	public void setDurata(Integer durata){
		this.durata = durata;
	}

	/**
	*  javadoc for inizioLavori
	*/
	private Date inizioLavori;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="inizio_lavori")
	public Date getInizioLavori(){
		return inizioLavori;
	}

	public void setInizioLavori(Date inizioLavori){
		this.inizioLavori = inizioLavori;
	}

	/**
	*  javadoc for kg
	*/
	private Double kg;

	@Column(name="kg")
	public Double getKg(){
		return kg;
	}

	public void setKg(Double kg){
		this.kg = kg;
	}

	/**
	*  javadoc for mq
	*/
	private Double mq;

	@Column(name="mq")
	public Double getMq(){
		return mq;
	}

	public void setMq(Double mq){
		this.mq = mq;
	}

	/**
	*  javadoc for tipoConfinamento
	*/
	private CodeValuePhi tipoConfinamento;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoConfinamento")
	@ForeignKey(name="FK_DettagliBonifiche_tipoConfinamento")
	//@Index(name="IX_DettagliBonifiche_tipoConfinamento")
	public CodeValuePhi getTipoConfinamento(){
		return tipoConfinamento;
	}

	public void setTipoConfinamento(CodeValuePhi tipoConfinamento){
		this.tipoConfinamento = tipoConfinamento;
	}

	/**
	*  javadoc for tipoBonifica
	*/
	private CodeValuePhi tipoBonifica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoBonifica")
	@ForeignKey(name="FK_DettagliBonifiche_tipoBonifica")
	//@Index(name="IX_DettagliBonifiche_tipoBonifica")
	public CodeValuePhi getTipoBonifica(){
		return tipoBonifica;
	}

	public void setTipoBonifica(CodeValuePhi tipoBonifica){
		this.tipoBonifica = tipoBonifica;
	}

	/**
	*  javadoc for tipoMatrice
	*/
	private CodeValuePhi tipoMatrice;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoMatrice")
	@ForeignKey(name="FK_DettagliBonifiche_tipoMatrice")
	//@Index(name="IX_DettagliBonifiche_tipoMatrice")
	public CodeValuePhi getTipoMatrice(){
		return tipoMatrice;
	}

	public void setTipoMatrice(CodeValuePhi tipoMatrice){
		this.tipoMatrice = tipoMatrice;
	}

	/**
	*  javadoc for tipoMaterialeText
	*/
	private String tipoMaterialeText;

	@Column(name="tipo_materiale_text")
	public String getTipoMaterialeText(){
		return tipoMaterialeText;
	}

	public void setTipoMaterialeText(String tipoMaterialeText){
		this.tipoMaterialeText = tipoMaterialeText;
	}

	/**
	*  javadoc for tipoMateriale
	*/
	private CodeValuePhi tipoMateriale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoMateriale")
	@ForeignKey(name="FK_DettagliBonifiche_tipoMateriale")
	//@Index(name="IX_DettagliBonifiche_tipoMateriale")
	public CodeValuePhi getTipoMateriale(){
		return tipoMateriale;
	}

	public void setTipoMateriale(CodeValuePhi tipoMateriale){
		this.tipoMateriale = tipoMateriale;
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
	*  javadoc for indirizzo
	*/
	private String indirizzo;

	@Column(name="indirizzo")
	public String getIndirizzo(){
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo){
		this.indirizzo = indirizzo;
	}

	/**
	*  javadoc for localita
	*/
	private String localita;

	@Column(name="localita")
	public String getLocalita(){
		return localita;
	}

	public void setLocalita(String localita){
		this.localita = localita;
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
	*  javadoc for naturaOpera
	*/
	private String naturaOpera;

	@Column(name="naturaOpera", length=1000)
	public String getNaturaOpera(){
		return naturaOpera;
	}

	public void setNaturaOpera(String naturaOpera){
		this.naturaOpera = naturaOpera;
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
	@ForeignKey(name="FK_DettagliBonifiche_tipoSegnalazione")
	//@Index(name="IX_DettagliBonifiche_tipoSegnalazione")
	public CodeValuePhi getTipoSegnalazione(){
		return tipoSegnalazione;
	}

	public void setTipoSegnalazione(CodeValuePhi tipoSegnalazione){
		this.tipoSegnalazione = tipoSegnalazione;
	}


	/**
	*  javadoc for protocollo
	*/
	private Protocollo protocollo;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="protocollo_id")
	@ForeignKey(name="FK_Det_prot")
	//@Index(name="IX_Det_prot")
	public Protocollo getProtocollo(){
		return protocollo;
	}

	public void setProtocollo(Protocollo protocollo){
		this.protocollo = protocollo;
	}

	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DettagliBonifiche_sequence")
	@SequenceGenerator(name = "DettagliBonifiche_sequence", sequenceName = "DettagliBonifiche_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
