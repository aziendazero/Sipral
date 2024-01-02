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
 * The persistent class for the ALTRIDATIPRATICA database table.
 * 
 */
@Entity
public class Altridatipratica implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date dataarrivoasl;
	private Date datainf;
	private Date dataprofilassi;
	private String datarichiesta;
	private String descrizionerichiesta;
	private BigDecimal idallevamento;
	private BigDecimal idanimale;
	private BigDecimal idautomezzo;
	private BigDecimal idcoloniafelina;
	private BigDecimal idimpianto;
	private BigDecimal idmunicipioubicazione;
	private BigDecimal idstoricoutente;
	private BigDecimal idstoricoutenteinf;
	private BigDecimal idstoricoutenterich;
	private String idtipoanubicaz;
	private String indirizzoubicazione;
	private String numerorichiesta;
	private BigDecimal statopraticainvciv;
	private String stridoperatori;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private String tipoanagrafica;
	private BigDecimal tipoanagraficaanimale;
	private BigDecimal tipoanagraficarich;
	private BigDecimal tipoimpianto;
	private String ubicazione;
	private String ubicazionerrs;
	private Anagrafiche anagrafiche1;
	private Anagrafiche anagrafiche2;
	private Anagrafiche anagrafiche4;
	private Anagrafiche anagrafiche5;
	private Anagrafiche anagrafiche6;
	private Anagrafiche anagrafiche7;
	private Anagrafiche anagrafiche8;
	private Anagrafiche anagrafiche9;
	private AttivitaPrevnet attivita;
	private Cantieri cantieri1;
	private Cantieri cantieri2;
	private Comuni comuni1;
	private Comuni comuni2;
	private Ditte ditte1;
	private Ditte ditte2;
	private Ditte ditte3;
	private Ditte ditte4;
	private Ditte ditte5;
	private Ditte ditte6;
	private Ditte ditte7;
	private Localita localita;
	private Medici medici;
	private Pratiche pratica;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle tabelle1;
	private Tabelle tabelle2;
	private Tabelle tabelle3;
	private Tabelle tabelle4;
	private Tabelle tabelle5;
	private Tabelle tabelle6;
	private Tabelle tabelle7;
	private Tabelle tabelle8;
	private Utenti utenti;
	private Motivisopralluoghi motivisopralluoghi1;
	private Motivisopralluoghi motivisopralluoghi2;

	public Altridatipratica() {
	}


	@Temporal(TemporalType.DATE)
	public Date getDataarrivoasl() {
		return this.dataarrivoasl;
	}

	public void setDataarrivoasl(Date dataarrivoasl) {
		this.dataarrivoasl = dataarrivoasl;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainf() {
		return this.datainf;
	}

	public void setDatainf(Date datainf) {
		this.datainf = datainf;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprofilassi() {
		return this.dataprofilassi;
	}

	public void setDataprofilassi(Date dataprofilassi) {
		this.dataprofilassi = dataprofilassi;
	}


	public String getDatarichiesta() {
		return this.datarichiesta;
	}

	public void setDatarichiesta(String datarichiesta) {
		this.datarichiesta = datarichiesta;
	}


	public String getDescrizionerichiesta() {
		return this.descrizionerichiesta;
	}

	public void setDescrizionerichiesta(String descrizionerichiesta) {
		this.descrizionerichiesta = descrizionerichiesta;
	}


	public BigDecimal getIdallevamento() {
		return this.idallevamento;
	}

	public void setIdallevamento(BigDecimal idallevamento) {
		this.idallevamento = idallevamento;
	}


	public BigDecimal getIdanimale() {
		return this.idanimale;
	}

	public void setIdanimale(BigDecimal idanimale) {
		this.idanimale = idanimale;
	}


	public BigDecimal getIdautomezzo() {
		return this.idautomezzo;
	}

	public void setIdautomezzo(BigDecimal idautomezzo) {
		this.idautomezzo = idautomezzo;
	}


	public BigDecimal getIdcoloniafelina() {
		return this.idcoloniafelina;
	}

	public void setIdcoloniafelina(BigDecimal idcoloniafelina) {
		this.idcoloniafelina = idcoloniafelina;
	}


	public BigDecimal getIdimpianto() {
		return this.idimpianto;
	}

	public void setIdimpianto(BigDecimal idimpianto) {
		this.idimpianto = idimpianto;
	}


	public BigDecimal getIdmunicipioubicazione() {
		return this.idmunicipioubicazione;
	}

	public void setIdmunicipioubicazione(BigDecimal idmunicipioubicazione) {
		this.idmunicipioubicazione = idmunicipioubicazione;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdstoricoutenteinf() {
		return this.idstoricoutenteinf;
	}

	public void setIdstoricoutenteinf(BigDecimal idstoricoutenteinf) {
		this.idstoricoutenteinf = idstoricoutenteinf;
	}


	public BigDecimal getIdstoricoutenterich() {
		return this.idstoricoutenterich;
	}

	public void setIdstoricoutenterich(BigDecimal idstoricoutenterich) {
		this.idstoricoutenterich = idstoricoutenterich;
	}


	public String getIdtipoanubicaz() {
		return this.idtipoanubicaz;
	}

	public void setIdtipoanubicaz(String idtipoanubicaz) {
		this.idtipoanubicaz = idtipoanubicaz;
	}


	public String getIndirizzoubicazione() {
		return this.indirizzoubicazione;
	}

	public void setIndirizzoubicazione(String indirizzoubicazione) {
		this.indirizzoubicazione = indirizzoubicazione;
	}


	public String getNumerorichiesta() {
		return this.numerorichiesta;
	}

	public void setNumerorichiesta(String numerorichiesta) {
		this.numerorichiesta = numerorichiesta;
	}

	public BigDecimal getStatopraticainvciv() {
		return this.statopraticainvciv;
	}

	public void setStatopraticainvciv(BigDecimal statopraticainvciv) {
		this.statopraticainvciv = statopraticainvciv;
	}


	public String getStridoperatori() {
		return this.stridoperatori;
	}

	public void setStridoperatori(String stridoperatori) {
		this.stridoperatori = stridoperatori;
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


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public String getTipoanagrafica() {
		return this.tipoanagrafica;
	}

	public void setTipoanagrafica(String tipoanagrafica) {
		this.tipoanagrafica = tipoanagrafica;
	}


	public BigDecimal getTipoanagraficaanimale() {
		return this.tipoanagraficaanimale;
	}

	public void setTipoanagraficaanimale(BigDecimal tipoanagraficaanimale) {
		this.tipoanagraficaanimale = tipoanagraficaanimale;
	}


	public BigDecimal getTipoanagraficarich() {
		return this.tipoanagraficarich;
	}

	public void setTipoanagraficarich(BigDecimal tipoanagraficarich) {
		this.tipoanagraficarich = tipoanagraficarich;
	}


	public BigDecimal getTipoimpianto() {
		return this.tipoimpianto;
	}

	public void setTipoimpianto(BigDecimal tipoimpianto) {
		this.tipoimpianto = tipoimpianto;
	}


	public String getUbicazione() {
		return this.ubicazione;
	}

	public void setUbicazione(String ubicazione) {
		this.ubicazione = ubicazione;
	}


	public String getUbicazionerrs() {
		return this.ubicazionerrs;
	}

	public void setUbicazionerrs(String ubicazionerrs) {
		this.ubicazionerrs = ubicazionerrs;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZPROPALLEV")
	public Anagrafiche getAnagrafiche1() {
		return this.anagrafiche1;
	}

	public void setAnagrafiche1(Anagrafiche anagrafiche1) {
		this.anagrafiche1 = anagrafiche1;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAZIENDALUOGOINF")
	public Anagrafiche getAnagrafiche2() {
		return this.anagrafiche2;
	}

	public void setAnagrafiche2(Anagrafiche anagrafiche2) {
		this.anagrafiche2 = anagrafiche2;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANUBICAZIONE")
	public Anagrafiche getAnagrafiche4() {
		return this.anagrafiche4;
	}

	public void setAnagrafiche4(Anagrafiche anagrafiche4) {
		this.anagrafiche4 = anagrafiche4;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGDITTA")
	public Anagrafiche getAnagrafiche5() {
		return this.anagrafiche5;
	}

	public void setAnagrafiche5(Anagrafiche anagrafiche5) {
		this.anagrafiche5 = anagrafiche5;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICARICH")
	public Anagrafiche getAnagrafiche6() {
		return this.anagrafiche6;
	}

	public void setAnagrafiche6(Anagrafiche anagrafiche6) {
		this.anagrafiche6 = anagrafiche6;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZDETALLEV")
	public Anagrafiche getAnagrafiche7() {
		return this.anagrafiche7;
	}

	public void setAnagrafiche7(Anagrafiche anagrafiche7) {
		this.anagrafiche7 = anagrafiche7;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOGGETTO")
	public Anagrafiche getAnagrafiche8() {
		return this.anagrafiche8;
	}

	public void setAnagrafiche8(Anagrafiche anagrafiche8) {
		this.anagrafiche8 = anagrafiche8;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTUDIOTECNICO")
	public Anagrafiche getAnagrafiche9() {
		return this.anagrafiche9;
	}

	public void setAnagrafiche9(Anagrafiche anagrafiche9) {
		this.anagrafiche9 = anagrafiche9;
	}


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITA")
	public AttivitaPrevnet getAttivita() {
		return this.attivita;
	}

	public void setAttivita(AttivitaPrevnet attivita) {
		this.attivita = attivita;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDACANTIEREUBIC")
	public Cantieri getCantieri1() {
		return this.cantieri1;
	}

	public void setCantieri1(Cantieri cantieri1) {
		this.cantieri1 = cantieri1;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDACANTLUOGOINF")
	public Cantieri getCantieri2() {
		return this.cantieri2;
	}

	public void setCantieri2(Cantieri cantieri2) {
		this.cantieri2 = cantieri2;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBICAZIONE")
	public Comuni getComuni1() {
		return this.comuni1;
	}

	public void setComuni1(Comuni comuni1) {
		this.comuni1 = comuni1;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEINF")
	public Comuni getComuni2() {
		return this.comuni2;
	}

	public void setComuni2(Comuni comuni2) {
		this.comuni2 = comuni2;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASOGGETTO")
	public Ditte getDitte1() {
		return this.ditte1;
	}

	public void setDitte1(Ditte ditte1) {
		this.ditte1 = ditte1;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitte2() {
		return this.ditte2;
	}

	public void setDitte2(Ditte ditte2) {
		this.ditte2 = ditte2;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTADETTALLEV")
	public Ditte getDitte3() {
		return this.ditte3;
	}

	public void setDitte3(Ditte ditte3) {
		this.ditte3 = ditte3;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAPROPALLEV")
	public Ditte getDitte4() {
		return this.ditte4;
	}

	public void setDitte4(Ditte ditte4) {
		this.ditte4 = ditte4;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTARICH")
	public Ditte getDitte5() {
		return this.ditte5;
	}

	public void setDitte5(Ditte ditte5) {
		this.ditte5 = ditte5;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAAZIENDALUOGOINF")
	public Ditte getDitte6() {
		return this.ditte6;
	}

	public void setDitte6(Ditte ditte6) {
		this.ditte6 = ditte6;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASTUDIO")
	public Ditte getDitte7() {
		return this.ditte7;
	}

	public void setDitte7(Ditte ditte7) {
		this.ditte7 = ditte7;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITAUBICAZIONE")
	public Localita getLocalita() {
		return this.localita;
	}

	public void setLocalita(Localita localita) {
		this.localita = localita;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICO")
	public Medici getMedici() {
		return this.medici;
	}

	public void setMedici(Medici medici) {
		this.medici = medici;
	}


	//bi-directional many-to-one association to Procpratiche
	@Id
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGOINF")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}
	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMACROPRODOTTO")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVENIENZA")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDESTINAZIONEUSO")
	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINTERVENTO")
	public Tabelle getTabelle4() {
		return this.tabelle4;
	}

	public void setTabelle4(Tabelle tabelle4) {
		this.tabelle4 = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTOUBICAZIONE")
	public Tabelle getTabelle5() {
		return this.tabelle5;
	}

	public void setTabelle5(Tabelle tabelle5) {
		this.tabelle5 = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFONTEINF")
	public Tabelle getTabelle6() {
		return this.tabelle6;
	}

	public void setTabelle6(Tabelle tabelle6) {
		this.tabelle6 = tabelle6;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRODOTTO")
	public Tabelle getTabelle7() {
		return this.tabelle7;
	}

	public void setTabelle7(Tabelle tabelle7) {
		this.tabelle7 = tabelle7;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOACCESSO")
	public Tabelle getTabelle8() {
		return this.tabelle8;
	}

	public void setTabelle8(Tabelle tabelle8) {
		this.tabelle8 = tabelle8;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}


	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVO")
	public Motivisopralluoghi getMotivisopralluoghi1() {
		return this.motivisopralluoghi1;
	}

	public void setMotivisopralluoghi1(Motivisopralluoghi motivisopralluoghi1) {
		this.motivisopralluoghi1 = motivisopralluoghi1;
	}


	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOMOTIVO")
	public Motivisopralluoghi getMotivisopralluoghi2() {
		return this.motivisopralluoghi2;
	}

	public void setMotivisopralluoghi2(Motivisopralluoghi motivisopralluoghi2) {
		this.motivisopralluoghi2 = motivisopralluoghi2;
	}

}