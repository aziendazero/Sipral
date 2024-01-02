package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ATTIVITA database table.
 * 
 */
@Entity
@Table(name="ATTIVITA")
public class AttivitaPrevnet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idattivita;
	private BigDecimal anno;
	private String catrischio;
	private String codice;
	private BigDecimal codiceth;
	private Date datavaliditaal;
	private Date datavaliditadal;
	private String descrizione;
	private BigDecimal idcompartoambito;
	private BigDecimal idindirizzoproduttivo;
	private BigDecimal idsettore;
	private String istat;
	private String livello;
	private String note;
	private AttivitaPrevnet attivitaPrevnetRiferimento;
	private AttivitaPrevnet attivitaPrevnetIstat;
	private Tabelle specieAllevata;
	private Tabelle codiceRegionale;
	private Tabelle codiceIstat;
	private Tabelle compartoSal;
	private Tabelle codRegionale2010;
	private Tabelle sottoIndirizzoProd;
	private List<Tabelle> gruppiAttivita;

	public AttivitaPrevnet() {
	}


	@Id
	public long getIdattivita() {
		return this.idattivita;
	}

	public void setIdattivita(long idattivita) {
		this.idattivita = idattivita;
	}


	public BigDecimal getAnno() {
		return this.anno;
	}

	public void setAnno(BigDecimal anno) {
		this.anno = anno;
	}


	public String getCatrischio() {
		return this.catrischio;
	}

	public void setCatrischio(String catrischio) {
		this.catrischio = catrischio;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public BigDecimal getCodiceth() {
		return this.codiceth;
	}

	public void setCodiceth(BigDecimal codiceth) {
		this.codiceth = codiceth;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavaliditaal() {
		return this.datavaliditaal;
	}

	public void setDatavaliditaal(Date datavaliditaal) {
		this.datavaliditaal = datavaliditaal;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavaliditadal() {
		return this.datavaliditadal;
	}

	public void setDatavaliditadal(Date datavaliditadal) {
		this.datavaliditadal = datavaliditadal;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getIdcompartoambito() {
		return this.idcompartoambito;
	}

	public void setIdcompartoambito(BigDecimal idcompartoambito) {
		this.idcompartoambito = idcompartoambito;
	}


	public BigDecimal getIdindirizzoproduttivo() {
		return this.idindirizzoproduttivo;
	}

	public void setIdindirizzoproduttivo(BigDecimal idindirizzoproduttivo) {
		this.idindirizzoproduttivo = idindirizzoproduttivo;
	}


	public BigDecimal getIdsettore() {
		return this.idsettore;
	}

	public void setIdsettore(BigDecimal idsettore) {
		this.idsettore = idsettore;
	}


	public String getIstat() {
		return this.istat;
	}

	public void setIstat(String istat) {
		this.istat = istat;
	}


	public String getLivello() {
		return this.livello;
	}

	public void setLivello(String livello) {
		this.livello = livello;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRIFERIMENTO")
	public AttivitaPrevnet getAttivitaPrevnetRiferimento() {
		return this.attivitaPrevnetRiferimento;
	}

	public void setAttivitaPrevnetRiferimento(AttivitaPrevnet attivitaPrevnetRiferimento) {
		this.attivitaPrevnetRiferimento = attivitaPrevnetRiferimento;
	}


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITAISTAT")
	public AttivitaPrevnet getAttivitaPrevnetIstat() {
		return this.attivitaPrevnetIstat;
	}

	public void setAttivitaPrevnetIstat(AttivitaPrevnet attivitaPrevnetIstat) {
		this.attivitaPrevnetIstat = attivitaPrevnetIstat;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSPECIEALLEVATA")
	public Tabelle getSpecieAllevata() {
		return this.specieAllevata;
	}

	public void setSpecieAllevata(Tabelle specieAllevata) {
		this.specieAllevata = specieAllevata;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCODICEREGIONALE")
	public Tabelle getCodiceRegionale() {
		return this.codiceRegionale;
	}

	public void setCodiceRegionale(Tabelle codiceRegionale) {
		this.codiceRegionale = codiceRegionale;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCODICEISTAT")
	public Tabelle getCodiceIstat() {
		return this.codiceIstat;
	}

	public void setCodiceIstat(Tabelle codiceIstat) {
		this.codiceIstat = codiceIstat;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMPARTOPSAL")
	public Tabelle getCompartoSal() {
		return this.compartoSal;
	}

	public void setCompartoSal(Tabelle compartoSal) {
		this.compartoSal = compartoSal;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCODICEREGIONALE2010")
	public Tabelle getCodRegionale2010() {
		return this.codRegionale2010;
	}

	public void setCodRegionale2010(Tabelle codRegionale2010) {
		this.codRegionale2010 = codRegionale2010;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOTTOINDIRIZZOPROD")
	public Tabelle getSottoIndirizzoProd() {
		return this.sottoIndirizzoProd;
	}

	public void setSottoIndirizzoProd(Tabelle sottoIndirizzoProd) {
		this.sottoIndirizzoProd = sottoIndirizzoProd;
	}

	//bi-directional many-to-many association to Tabelle
	@ManyToMany(mappedBy="attivitaPrevnet")
	public List<Tabelle> getGruppiAttivita() {
		return this.gruppiAttivita;
	}

	public void setGruppiAttivita(List<Tabelle> gruppiAttivita) {
		this.gruppiAttivita = gruppiAttivita;
	}

}