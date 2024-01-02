package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the RICHIESTAREGISTRAZIONE database table.
 * 
 */
@Entity
public class Richiestaregistrazione implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idrichregistrazione;
	private String chkcomuneprovenienza;
	private String chksportellounico;
	private Date dataarrivo;
	private Date dataregistrazione;
	private Date datarichiesta;
	private String descrlocalizzato;
	private String descrrichiesta;
		
	//UBICAZIONE
	private BigDecimal idtipoanubicaz;
	private Anagrafiche ubicazioneAnag;
	private Cantieri ubicazioneCant;
	private Ditte ubicazioneDitta;
	private Comuni comuneUbicaz;
	private BigDecimal idlocalubicaz;

	/* SEMPRE VUOTE
	private String dettagliocivicoubic;
	private BigDecimal idstoricoutenteubic;
	private BigDecimal idubicpuntoprel;
	private BigDecimal idviaubic;
	private BigDecimal numerocivicoubic;
	*/
	//RIFERITO A
	private BigDecimal idtipoanagrafica;
	private Ditte riferitoDitta;
	private Anagrafiche riferitoAnag;

	/* SEMPRE VUOTE
	private BigDecimal idimpianto;
	private BigDecimal idimpiantoriferitoa;
	*/
	
	//RICHIEDENTE
	private BigDecimal idtipoanrichied;
	private Anagrafiche richiedenteAnag;
	private BigDecimal idschedadittarichied;
	private BigDecimal idstoricoutenterichied;
	
	
	private Medici medico;	
	private BigDecimal idmunicipio;
	
	private Pratiche pratica;
	private ProtocolloPrevnet protocolloPrevnet;
			
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutente;
	
	private BigDecimal numaddettidiap;
	private String numeroprotocollo;
	private String numerorichiesta;
	private Date orarichiesta;
	private BigDecimal quantita;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private String versamentodiap;
	private Tabelle classeRischio;
	private Tabelle provenienza;
	private Tabelle tipoInterventoDiap;
	private Tabelle macroProdotto;
	private Tabelle asl;
	private Tabelle ruolo;

	public Richiestaregistrazione() {
	}


	@Id
	public long getIdrichregistrazione() {
		return this.idrichregistrazione;
	}

	public void setIdrichregistrazione(long idrichregistrazione) {
		this.idrichregistrazione = idrichregistrazione;
	}


	public String getChkcomuneprovenienza() {
		return this.chkcomuneprovenienza;
	}

	public void setChkcomuneprovenienza(String chkcomuneprovenienza) {
		this.chkcomuneprovenienza = chkcomuneprovenienza;
	}


	public String getChksportellounico() {
		return this.chksportellounico;
	}

	public void setChksportellounico(String chksportellounico) {
		this.chksportellounico = chksportellounico;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataarrivo() {
		return this.dataarrivo;
	}

	public void setDataarrivo(Date dataarrivo) {
		this.dataarrivo = dataarrivo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataregistrazione() {
		return this.dataregistrazione;
	}

	public void setDataregistrazione(Date dataregistrazione) {
		this.dataregistrazione = dataregistrazione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getDatarichiesta() {
		return this.datarichiesta;
	}

	public void setDatarichiesta(Date datarichiesta) {
		this.datarichiesta = datarichiesta;
	}


	public String getDescrlocalizzato() {
		return this.descrlocalizzato;
	}

	public void setDescrlocalizzato(String descrlocalizzato) {
		this.descrlocalizzato = descrlocalizzato;
	}


	public String getDescrrichiesta() {
		return this.descrrichiesta;
	}

	public void setDescrrichiesta(String descrrichiesta) {
		this.descrrichiesta = descrrichiesta;
	}


	/*public String getDettagliocivicoubic() {
		return this.dettagliocivicoubic;
	}

	public void setDettagliocivicoubic(String dettagliocivicoubic) {
		this.dettagliocivicoubic = dettagliocivicoubic;
	}*/

	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBICAZ")
	public Comuni getComuneUbicaz() {
		return this.comuneUbicaz;
	}

	public void setComuneUbicaz(Comuni comuneUbicaz) {
		this.comuneUbicaz = comuneUbicaz;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Anagrafiche getRiferitoAnag() {
		return this.riferitoAnag;
	}

	public void setRiferitoAnag(Anagrafiche riferitoAnag) {
		this.riferitoAnag = riferitoAnag;
	}


	/*public BigDecimal getIdimpianto() {
		return this.idimpianto;
	}

	public void setIdimpianto(BigDecimal idimpianto) {
		this.idimpianto = idimpianto;
	}


	public BigDecimal getIdimpiantoriferitoa() {
		return this.idimpiantoriferitoa;
	}

	public void setIdimpiantoriferitoa(BigDecimal idimpiantoriferitoa) {
		this.idimpiantoriferitoa = idimpiantoriferitoa;
	}*/


	public BigDecimal getIdlocalubicaz() {
		return this.idlocalubicaz;
	}

	public void setIdlocalubicaz(BigDecimal idlocalubicaz) {
		this.idlocalubicaz = idlocalubicaz;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICO")
	public Medici getMedico() {
		return this.medico;
	}

	public void setMedico(Medici medico) {
		this.medico = medico;
	}


	public BigDecimal getIdmunicipio() {
		return this.idmunicipio;
	}

	public void setIdmunicipio(BigDecimal idmunicipio) {
		this.idmunicipio = idmunicipio;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)//su prevnet12, richiestaregistrazione#11635 ha idprotocollo=11549 che non corrisponde
	@JoinColumn(name="IDPROTOCOLLO")
	public ProtocolloPrevnet getProtocolloPrevnet() {
		return this.protocolloPrevnet;
	}

	public void setProtocolloPrevnet(ProtocolloPrevnet protocolloPrevnet) {
		this.protocolloPrevnet = protocolloPrevnet;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRICHIEDENTE")
	public Anagrafiche getRichiedenteAnag() {
		return this.richiedenteAnag;
	}

	public void setRichiedenteAnag(Anagrafiche richiedenteAnag) {
		this.richiedenteAnag = richiedenteAnag;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDACANTIEREUBIC")
	public Cantieri getUbicazioneCant() {
		return this.ubicazioneCant;
	}

	public void setUbicazioneCant(Cantieri ubicazioneCant) {
		this.ubicazioneCant = ubicazioneCant;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getRiferitoDitta() {
		return this.riferitoDitta;
	}

	public void setRiferitoDitta(Ditte riferitoDitta) {
		this.riferitoDitta = riferitoDitta;
	}


	public BigDecimal getIdschedadittarichied() {
		return this.idschedadittarichied;
	}

	public void setIdschedadittarichied(BigDecimal idschedadittarichied) {
		this.idschedadittarichied = idschedadittarichied;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAUBIC")
	public Ditte getUbicazioneDitta() {
		return this.ubicazioneDitta;
	}

	public void setUbicazioneDitta(Ditte ubicazioneDitta) {
		this.ubicazioneDitta = ubicazioneDitta;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdstoricoutenterichied() {
		return this.idstoricoutenterichied;
	}

	public void setIdstoricoutenterichied(BigDecimal idstoricoutenterichied) {
		this.idstoricoutenterichied = idstoricoutenterichied;
	}


	/*public BigDecimal getIdstoricoutenteubic() {
		return this.idstoricoutenteubic;
	}

	public void setIdstoricoutenteubic(BigDecimal idstoricoutenteubic) {
		this.idstoricoutenteubic = idstoricoutenteubic;
	}*/


	public BigDecimal getIdtipoanagrafica() {
		return this.idtipoanagrafica;
	}

	public void setIdtipoanagrafica(BigDecimal idtipoanagrafica) {
		this.idtipoanagrafica = idtipoanagrafica;
	}


	public BigDecimal getIdtipoanrichied() {
		return this.idtipoanrichied;
	}

	public void setIdtipoanrichied(BigDecimal idtipoanrichied) {
		this.idtipoanrichied = idtipoanrichied;
	}


	public BigDecimal getIdtipoanubicaz() {
		return this.idtipoanubicaz;
	}

	public void setIdtipoanubicaz(BigDecimal idtipoanubicaz) {
		this.idtipoanubicaz = idtipoanubicaz;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUBICAZIONE")
	public Anagrafiche getUbicazioneAnag() {
		return this.ubicazioneAnag;
	}

	public void setUbicazioneAnag(Anagrafiche ubicazioneAnag) {
		this.ubicazioneAnag = ubicazioneAnag;
	}


	/*public BigDecimal getIdubicpuntoprel() {
		return this.idubicpuntoprel;
	}

	public void setIdubicpuntoprel(BigDecimal idubicpuntoprel) {
		this.idubicpuntoprel = idubicpuntoprel;
	}


	public BigDecimal getIdviaubic() {
		return this.idviaubic;
	}

	public void setIdviaubic(BigDecimal idviaubic) {
		this.idviaubic = idviaubic;
	}*/


	public BigDecimal getNumaddettidiap() {
		return this.numaddettidiap;
	}

	public void setNumaddettidiap(BigDecimal numaddettidiap) {
		this.numaddettidiap = numaddettidiap;
	}


	/*public BigDecimal getNumerocivicoubic() {
		return this.numerocivicoubic;
	}

	public void setNumerocivicoubic(BigDecimal numerocivicoubic) {
		this.numerocivicoubic = numerocivicoubic;
	}*/


	public String getNumeroprotocollo() {
		return this.numeroprotocollo;
	}

	public void setNumeroprotocollo(String numeroprotocollo) {
		this.numeroprotocollo = numeroprotocollo;
	}


	public String getNumerorichiesta() {
		return this.numerorichiesta;
	}

	public void setNumerorichiesta(String numerorichiesta) {
		this.numerorichiesta = numerorichiesta;
	}


	@Temporal(TemporalType.DATE)
	public Date getOrarichiesta() {
		return this.orarichiesta;
	}

	public void setOrarichiesta(Date orarichiesta) {
		this.orarichiesta = orarichiesta;
	}


	public BigDecimal getQuantita() {
		return this.quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


	public String getSynchid() {
		return this.synchid;
	}

	public void setSynchid(String synchid) {
		this.synchid = synchid;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public String getVersamentodiap() {
		return this.versamentodiap;
	}

	public void setVersamentodiap(String versamentodiap) {
		this.versamentodiap = versamentodiap;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSERISCHIO")
	public Tabelle getClasseRischio() {
		return this.classeRischio;
	}

	public void setClasseRischio(Tabelle classeRischio) {
		this.classeRischio = classeRischio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVENIENZA")
	public Tabelle getProvenienza() {
		return this.provenienza;
	}

	public void setProvenienza(Tabelle provenienza) {
		this.provenienza = provenienza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINTERVENTODIAP")
	public Tabelle getTipoInterventoDiap() {
		return this.tipoInterventoDiap;
	}

	public void setTipoInterventoDiap(Tabelle tipoInterventoDiap) {
		this.tipoInterventoDiap = tipoInterventoDiap;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMACROPRODOTTO")
	public Tabelle getMacroProdotto() {
		return this.macroProdotto;
	}

	public void setMacroProdotto(Tabelle macroProdotto) {
		this.macroProdotto = macroProdotto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getAsl() {
		return this.asl;
	}

	public void setAsl(Tabelle asl) {
		this.asl = asl;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRUOLO")
	public Tabelle getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(Tabelle ruolo) {
		this.ruolo = ruolo;
	}

}