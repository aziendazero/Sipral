package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the EVENTIPRATICA database table.
 * 
 */
@Entity
@Table(name="EVENTIPRATICA")
public class Eventi implements Serializable {
	private static final long serialVersionUID = 1L;
	private long ideventipratica;
	private String chiaveapplicazione;
	private Date data;
	private Date datarilevante;
	private String descrizione;
	private String eliminato;
	private BigDecimal idoperatore;
	private BigDecimal idoperultmodifica;
	private BigDecimal idpassoproc;
	private BigDecimal idprotocollo;
	private String inrilievo;
	private String note;
	private String privatolettura;
	private String privatomodifica;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipoevento;
	private Date ultimamodifica;
	private Pratiche pratiche;
	private Tabelle classeEvento;

	public Eventi() {
	}


	@Id
	@Column(unique=true, nullable=false, precision=22)
	public long getIdeventipratica() {
		return this.ideventipratica;
	}

	public void setIdeventipratica(long ideventipratica) {
		this.ideventipratica = ideventipratica;
	}


	@Column(length=255)
	public String getChiaveapplicazione() {
		return this.chiaveapplicazione;
	}

	public void setChiaveapplicazione(String chiaveapplicazione) {
		this.chiaveapplicazione = chiaveapplicazione;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"", nullable=false)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarilevante() {
		return this.datarilevante;
	}

	public void setDatarilevante(Date datarilevante) {
		this.datarilevante = datarilevante;
	}


	@Column(length=100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	@Column(length=1)
	public String getEliminato() {
		return this.eliminato;
	}

	public void setEliminato(String eliminato) {
		this.eliminato = eliminato;
	}


	@Column(precision=22)
	public BigDecimal getIdoperatore() {
		return this.idoperatore;
	}

	public void setIdoperatore(BigDecimal idoperatore) {
		this.idoperatore = idoperatore;
	}


	@Column(precision=22)
	public BigDecimal getIdoperultmodifica() {
		return this.idoperultmodifica;
	}

	public void setIdoperultmodifica(BigDecimal idoperultmodifica) {
		this.idoperultmodifica = idoperultmodifica;
	}


	@Column(precision=22)
	public BigDecimal getIdpassoproc() {
		return this.idpassoproc;
	}

	public void setIdpassoproc(BigDecimal idpassoproc) {
		this.idpassoproc = idpassoproc;
	}


	@Column(precision=22)
	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	@Column(nullable=false, length=1)
	public String getInrilievo() {
		return this.inrilievo;
	}

	public void setInrilievo(String inrilievo) {
		this.inrilievo = inrilievo;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Column(nullable=false, length=1)
	public String getPrivatolettura() {
		return this.privatolettura;
	}

	public void setPrivatolettura(String privatolettura) {
		this.privatolettura = privatolettura;
	}


	@Column(nullable=false, length=1)
	public String getPrivatomodifica() {
		return this.privatomodifica;
	}

	public void setPrivatomodifica(String privatomodifica) {
		this.privatomodifica = privatomodifica;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	@Column(length=1)
	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


	@Column(length=22)
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


	@Column(nullable=false, precision=22)
	public BigDecimal getTipoevento() {
		return this.tipoevento;
	}

	public void setTipoevento(BigDecimal tipoevento) {
		this.tipoevento = tipoevento;
	}


	@Temporal(TemporalType.DATE)
	public Date getUltimamodifica() {
		return this.ultimamodifica;
	}

	public void setUltimamodifica(Date ultimamodifica) {
		this.ultimamodifica = ultimamodifica;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche pratiche) {
		this.pratiche = pratiche;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSEEVENTO")
	public Tabelle getClasseEvento() {
		return this.classeEvento;
	}

	public void setClasseEvento(Tabelle tabelle) {
		this.classeEvento = tabelle;
	}

}