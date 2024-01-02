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
 * The persistent class for the ASSEGNSOPRALL database table.
 * 
 */
@Entity
public class Assegnsoprall implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idassegnsoprall;
	private String chkdestnonpresente;
	private Date dataaffidamento;
	private Date datasopralluogo;
	private BigDecimal idcoloniefeline;
	private BigDecimal idprenotazione;
	private BigDecimal idscadsoprallass;
	private BigDecimal idsopralluoghidip;
	private BigDecimal idstoricoutente;
	private BigDecimal idufficio;
	private String note;
	private String notelocalizzato;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private String tipodestinatario;
	private String tipoubicazione;
	private String ulterioriassegnaz;
	private Anagrafiche ubicazione;
	private Anagrafiche destinatario;
	private Comuni comuni;
	private Ditte ditte;
	private Localita localita;
	private Operatori operatori;
	private Pratiche procpratiche;
	private Tabelle tipoSopralluogo;
	private Motivisopralluoghi motivisopralluoghi1;
	private Motivisopralluoghi motivisopralluoghi2;
	
	public Assegnsoprall() {
	}


	@Id
	public long getIdassegnsoprall() {
		return this.idassegnsoprall;
	}

	public void setIdassegnsoprall(long idassegnsoprall) {
		this.idassegnsoprall = idassegnsoprall;
	}


	public String getChkdestnonpresente() {
		return this.chkdestnonpresente;
	}

	public void setChkdestnonpresente(String chkdestnonpresente) {
		this.chkdestnonpresente = chkdestnonpresente;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaffidamento() {
		return this.dataaffidamento;
	}

	public void setDataaffidamento(Date dataaffidamento) {
		this.dataaffidamento = dataaffidamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasopralluogo() {
		return this.datasopralluogo;
	}

	public void setDatasopralluogo(Date datasopralluogo) {
		this.datasopralluogo = datasopralluogo;
	}


	public BigDecimal getIdcoloniefeline() {
		return this.idcoloniefeline;
	}

	public void setIdcoloniefeline(BigDecimal idcoloniefeline) {
		this.idcoloniefeline = idcoloniefeline;
	}


	public BigDecimal getIdprenotazione() {
		return this.idprenotazione;
	}

	public void setIdprenotazione(BigDecimal idprenotazione) {
		this.idprenotazione = idprenotazione;
	}


	public BigDecimal getIdscadsoprallass() {
		return this.idscadsoprallass;
	}

	public void setIdscadsoprallass(BigDecimal idscadsoprallass) {
		this.idscadsoprallass = idscadsoprallass;
	}


	public BigDecimal getIdsopralluoghidip() {
		return this.idsopralluoghidip;
	}

	public void setIdsopralluoghidip(BigDecimal idsopralluoghidip) {
		this.idsopralluoghidip = idsopralluoghidip;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdufficio() {
		return this.idufficio;
	}

	public void setIdufficio(BigDecimal idufficio) {
		this.idufficio = idufficio;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNotelocalizzato() {
		return this.notelocalizzato;
	}

	public void setNotelocalizzato(String notelocalizzato) {
		this.notelocalizzato = notelocalizzato;
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


	public String getTipodestinatario() {
		return this.tipodestinatario;
	}

	public void setTipodestinatario(String tipodestinatario) {
		this.tipodestinatario = tipodestinatario;
	}


	public String getTipoubicazione() {
		return this.tipoubicazione;
	}

	public void setTipoubicazione(String tipoubicazione) {
		this.tipoubicazione = tipoubicazione;
	}


	public String getUlterioriassegnaz() {
		return this.ulterioriassegnaz;
	}

	public void setUlterioriassegnaz(String ulterioriassegnaz) {
		this.ulterioriassegnaz = ulterioriassegnaz;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUBICAZIONE")
	public Anagrafiche getUbicazione() {
		return this.ubicazione;
	}

	public void setUbicazione(Anagrafiche ubicazione) {
		this.ubicazione = ubicazione;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDESTINATARIO")
	public Anagrafiche getDestinatario() {
		return this.destinatario;
	}

	public void setDestinatario(Anagrafiche destinatario) {
		this.destinatario = destinatario;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBIC")
	public Comuni getComuni() {
		return this.comuni;
	}

	public void setComuni(Comuni comuni) {
		this.comuni = comuni;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitte() {
		return this.ditte;
	}

	public void setDitte(Ditte ditte) {
		this.ditte = ditte;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITAUBIC")
	public Localita getLocalita() {
		return this.localita;
	}

	public void setLocalita(Localita localita) {
		this.localita = localita;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}


	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.procpratiche;
	}

	public void setPratiche(Pratiche procpratiche) {
		this.procpratiche = procpratiche;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="TIPOSOPRALLUOGO")
	public Tabelle getTipoSopralluogo() {
		return this.tipoSopralluogo;
	}

	public void setTipoSopralluogo(Tabelle tipoSopralluogo) {
		this.tipoSopralluogo = tipoSopralluogo;
	}

	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOSOPRALLUOGO")
	public Motivisopralluoghi getMotivisopralluoghi1() {
		return this.motivisopralluoghi1;
	}

	public void setMotivisopralluoghi1(Motivisopralluoghi motivisopralluoghi1) {
		this.motivisopralluoghi1 = motivisopralluoghi1;
	}


	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSPECIFICASOPRALLUOGO")
	public Motivisopralluoghi getMotivisopralluoghi2() {
		return this.motivisopralluoghi2;
	}

	public void setMotivisopralluoghi2(Motivisopralluoghi motivisopralluoghi2) {
		this.motivisopralluoghi2 = motivisopralluoghi2;
	}
}