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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the SCADENZE database table.
 * 
 */
@Entity
@NamedQuery(name="Scadenze.findAll", query="SELECT s FROM Scadenze s")
public class Scadenze implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idscadenze;
	private String attiva;
	private String avviso;
	private Date dataevasione;
	private Date datascadenza;
	private String evaso;
	private BigDecimal idarchivio;
	private BigDecimal idattivita;
	private BigDecimal idattivitacontrollo;
	private BigDecimal idautorizzautomezzo;
	private BigDecimal idprogrammazione;
	private BigDecimal idprogrdettaglio;
	private BigDecimal idscadenzaperiodica;
	private BigDecimal idsettore;
	private BigDecimal importoscad;
	private String manuale;
	private String messaggio;
	private String notemanuale;
	private String soggetto;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipoarchivio;
	private String tiposcadenza;
	private List<ProvvedimentiPrevnet> provvedimentis1;
	private List<ProvvedimentiPrevnet> provvedimentis2;
	private Pratiche pratica;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle tabelle1;
	private Tabelle tabelle2;
	private List<Sopralluoghidip> sopralluoghidips1;
	private List<Sopralluoghidip> sopralluoghidips2;
	private List<Sopralluoghidip> sopralluoghidips3;
	private List<Articoliprovvedimenti> articoliprovvedimentis;
	private List<Sanzioni> sanzionis1;
	private List<Sanzioni> sanzionis2;
	private List<Sanzioni> sanzionis3;
	private List<Sanzioni> sanzionis4;
	private List<Sanzioni> sanzionis5;
	private List<Sanzioni> sanzionis6;
	
	public Scadenze() {
	}


	@Id
	public long getIdscadenze() {
		return this.idscadenze;
	}

	public void setIdscadenze(long idscadenze) {
		this.idscadenze = idscadenze;
	}


	public String getAttiva() {
		return this.attiva;
	}

	public void setAttiva(String attiva) {
		this.attiva = attiva;
	}


	public String getAvviso() {
		return this.avviso;
	}

	public void setAvviso(String avviso) {
		this.avviso = avviso;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataevasione() {
		return this.dataevasione;
	}

	public void setDataevasione(Date dataevasione) {
		this.dataevasione = dataevasione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascadenza() {
		return this.datascadenza;
	}

	public void setDatascadenza(Date datascadenza) {
		this.datascadenza = datascadenza;
	}


	public String getEvaso() {
		return this.evaso;
	}

	public void setEvaso(String evaso) {
		this.evaso = evaso;
	}


	public BigDecimal getIdarchivio() {
		return this.idarchivio;
	}

	public void setIdarchivio(BigDecimal idarchivio) {
		this.idarchivio = idarchivio;
	}


	public BigDecimal getIdattivita() {
		return this.idattivita;
	}

	public void setIdattivita(BigDecimal idattivita) {
		this.idattivita = idattivita;
	}


	public BigDecimal getIdattivitacontrollo() {
		return this.idattivitacontrollo;
	}

	public void setIdattivitacontrollo(BigDecimal idattivitacontrollo) {
		this.idattivitacontrollo = idattivitacontrollo;
	}


	public BigDecimal getIdautorizzautomezzo() {
		return this.idautorizzautomezzo;
	}

	public void setIdautorizzautomezzo(BigDecimal idautorizzautomezzo) {
		this.idautorizzautomezzo = idautorizzautomezzo;
	}


	public BigDecimal getIdprogrammazione() {
		return this.idprogrammazione;
	}

	public void setIdprogrammazione(BigDecimal idprogrammazione) {
		this.idprogrammazione = idprogrammazione;
	}


	public BigDecimal getIdprogrdettaglio() {
		return this.idprogrdettaglio;
	}

	public void setIdprogrdettaglio(BigDecimal idprogrdettaglio) {
		this.idprogrdettaglio = idprogrdettaglio;
	}


	public BigDecimal getIdscadenzaperiodica() {
		return this.idscadenzaperiodica;
	}

	public void setIdscadenzaperiodica(BigDecimal idscadenzaperiodica) {
		this.idscadenzaperiodica = idscadenzaperiodica;
	}


	public BigDecimal getIdsettore() {
		return this.idsettore;
	}

	public void setIdsettore(BigDecimal idsettore) {
		this.idsettore = idsettore;
	}


	public BigDecimal getImportoscad() {
		return this.importoscad;
	}

	public void setImportoscad(BigDecimal importoscad) {
		this.importoscad = importoscad;
	}


	public String getManuale() {
		return this.manuale;
	}

	public void setManuale(String manuale) {
		this.manuale = manuale;
	}


	@Lob
	public String getMessaggio() {
		return this.messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}


	public String getNotemanuale() {
		return this.notemanuale;
	}

	public void setNotemanuale(String notemanuale) {
		this.notemanuale = notemanuale;
	}


	public String getSoggetto() {
		return this.soggetto;
	}

	public void setSoggetto(String soggetto) {
		this.soggetto = soggetto;
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


	public BigDecimal getTipoarchivio() {
		return this.tipoarchivio;
	}

	public void setTipoarchivio(BigDecimal tipoarchivio) {
		this.tipoarchivio = tipoarchivio;
	}


	public String getTiposcadenza() {
		return this.tiposcadenza;
	}

	public void setTiposcadenza(String tiposcadenza) {
		this.tiposcadenza = tiposcadenza;
	}

	//bi-directional many-to-one association to Provvedimenti
	@OneToMany(mappedBy="scadenzaData")
	public List<ProvvedimentiPrevnet> getProvvedimentis1() {
		return this.provvedimentis1;
	}

	public void setProvvedimentis1(List<ProvvedimentiPrevnet> provvedimentis1) {
		this.provvedimentis1 = provvedimentis1;
	}

	public ProvvedimentiPrevnet addProvvedimentis1(ProvvedimentiPrevnet provvedimentis1) {
		getProvvedimentis1().add(provvedimentis1);
		provvedimentis1.setScadenzaData(this);

		return provvedimentis1;
	}

	public ProvvedimentiPrevnet removeProvvedimentis1(ProvvedimentiPrevnet provvedimentis1) {
		getProvvedimentis1().remove(provvedimentis1);
		provvedimentis1.setScadenzaData(null);

		return provvedimentis1;
	}


	//bi-directional many-to-one association to Provvedimenti
	@OneToMany(mappedBy="scadenzaNotifica301")
	public List<ProvvedimentiPrevnet> getProvvedimentis2() {
		return this.provvedimentis2;
	}

	public void setProvvedimentis2(List<ProvvedimentiPrevnet> provvedimentis2) {
		this.provvedimentis2 = provvedimentis2;
	}

	public ProvvedimentiPrevnet addProvvedimentis2(ProvvedimentiPrevnet provvedimentis2) {
		getProvvedimentis2().add(provvedimentis2);
		provvedimentis2.setScadenzaNotifica301(this);

		return provvedimentis2;
	}

	public ProvvedimentiPrevnet removeProvvedimentis2(ProvvedimentiPrevnet provvedimentis2) {
		getProvvedimentis2().remove(provvedimentis2);
		provvedimentis2.setScadenzaNotifica301(null);

		return provvedimentis2;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOSCADENZA")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}

	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="scadenzaSoprVerifica")
	public List<Sopralluoghidip> getSopralluoghidips1() {
		return this.sopralluoghidips1;
	}

	public void setSopralluoghidips1(List<Sopralluoghidip> sopralluoghidips1) {
		this.sopralluoghidips1 = sopralluoghidips1;
	}

	public Sopralluoghidip addSopralluoghidips1(Sopralluoghidip sopralluoghidips1) {
		getSopralluoghidips1().add(sopralluoghidips1);
		sopralluoghidips1.setScadenzaSoprVerifica(this);

		return sopralluoghidips1;
	}

	public Sopralluoghidip removeSopralluoghidips1(Sopralluoghidip sopralluoghidips1) {
		getSopralluoghidips1().remove(sopralluoghidips1);
		sopralluoghidips1.setScadenzaSoprVerifica(null);

		return sopralluoghidips1;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="scadenzaSoprProgr")
	public List<Sopralluoghidip> getSopralluoghidips2() {
		return this.sopralluoghidips2;
	}

	public void setSopralluoghidips2(List<Sopralluoghidip> sopralluoghidips2) {
		this.sopralluoghidips2 = sopralluoghidips2;
	}

	public Sopralluoghidip addSopralluoghidips2(Sopralluoghidip sopralluoghidips2) {
		getSopralluoghidips2().add(sopralluoghidips2);
		sopralluoghidips2.setScadenzaSoprProgr(this);

		return sopralluoghidips2;
	}

	public Sopralluoghidip removeSopralluoghidips2(Sopralluoghidip sopralluoghidips2) {
		getSopralluoghidips2().remove(sopralluoghidips2);
		sopralluoghidips2.setScadenzaSoprProgr(null);

		return sopralluoghidips2;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="scadenzaData")
	public List<Sopralluoghidip> getSopralluoghidips3() {
		return this.sopralluoghidips3;
	}

	public void setSopralluoghidips3(List<Sopralluoghidip> sopralluoghidips3) {
		this.sopralluoghidips3 = sopralluoghidips3;
	}

	public Sopralluoghidip addSopralluoghidips3(Sopralluoghidip sopralluoghidips3) {
		getSopralluoghidips3().add(sopralluoghidips3);
		sopralluoghidips3.setScadenzaData(this);

		return sopralluoghidips3;
	}

	public Sopralluoghidip removeSopralluoghidips3(Sopralluoghidip sopralluoghidips3) {
		getSopralluoghidips3().remove(sopralluoghidips3);
		sopralluoghidips3.setScadenzaData(null);

		return sopralluoghidips3;
	}


	//bi-directional many-to-one association to Articoliprovvedimenti
	@OneToMany(mappedBy="scadenze")
	public List<Articoliprovvedimenti> getArticoliprovvedimentis() {
		return this.articoliprovvedimentis;
	}

	public void setArticoliprovvedimentis(List<Articoliprovvedimenti> articoliprovvedimentis) {
		this.articoliprovvedimentis = articoliprovvedimentis;
	}

	public Articoliprovvedimenti addArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().add(articoliprovvedimenti);
		articoliprovvedimenti.setScadenze(this);

		return articoliprovvedimenti;
	}

	public Articoliprovvedimenti removeArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().remove(articoliprovvedimenti);
		articoliprovvedimenti.setScadenze(null);

		return articoliprovvedimenti;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenze1")
	public List<Sanzioni> getSanzionis1() {
		return this.sanzionis1;
	}

	public void setSanzionis1(List<Sanzioni> sanzionis1) {
		this.sanzionis1 = sanzionis1;
	}

	public Sanzioni addSanzionis1(Sanzioni sanzionis1) {
		getSanzionis1().add(sanzionis1);
		sanzionis1.setScadenze1(this);

		return sanzionis1;
	}

	public Sanzioni removeSanzionis1(Sanzioni sanzionis1) {
		getSanzionis1().remove(sanzionis1);
		sanzionis1.setScadenze1(null);

		return sanzionis1;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenzaOblazione")
	public List<Sanzioni> getSanzionis2() {
		return this.sanzionis2;
	}

	public void setSanzionis2(List<Sanzioni> sanzionis2) {
		this.sanzionis2 = sanzionis2;
	}

	public Sanzioni addSanzionis2(Sanzioni sanzionis2) {
		getSanzionis2().add(sanzionis2);
		sanzionis2.setScadenzaOblazione(this);

		return sanzionis2;
	}

	public Sanzioni removeSanzionis2(Sanzioni sanzionis2) {
		getSanzionis2().remove(sanzionis2);
		sanzionis2.setScadenzaOblazione(null);

		return sanzionis2;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenze3")
	public List<Sanzioni> getSanzionis3() {
		return this.sanzionis3;
	}

	public void setSanzionis3(List<Sanzioni> sanzionis3) {
		this.sanzionis3 = sanzionis3;
	}

	public Sanzioni addSanzionis3(Sanzioni sanzionis3) {
		getSanzionis3().add(sanzionis3);
		sanzionis3.setScadenze3(this);

		return sanzionis3;
	}

	public Sanzioni removeSanzionis3(Sanzioni sanzionis3) {
		getSanzionis3().remove(sanzionis3);
		sanzionis3.setScadenze3(null);

		return sanzionis3;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenze4")
	public List<Sanzioni> getSanzionis4() {
		return this.sanzionis4;
	}

	public void setSanzionis4(List<Sanzioni> sanzionis4) {
		this.sanzionis4 = sanzionis4;
	}

	public Sanzioni addSanzionis4(Sanzioni sanzionis4) {
		getSanzionis4().add(sanzionis4);
		sanzionis4.setScadenze4(this);

		return sanzionis4;
	}

	public Sanzioni removeSanzionis4(Sanzioni sanzionis4) {
		getSanzionis4().remove(sanzionis4);
		sanzionis4.setScadenze4(null);

		return sanzionis4;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenze5")
	public List<Sanzioni> getSanzionis5() {
		return this.sanzionis5;
	}

	public void setSanzionis5(List<Sanzioni> sanzionis5) {
		this.sanzionis5 = sanzionis5;
	}

	public Sanzioni addSanzionis5(Sanzioni sanzionis5) {
		getSanzionis5().add(sanzionis5);
		sanzionis5.setScadenze5(this);

		return sanzionis5;
	}

	public Sanzioni removeSanzionis5(Sanzioni sanzionis5) {
		getSanzionis5().remove(sanzionis5);
		sanzionis5.setScadenze5(null);

		return sanzionis5;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="scadenze6")
	public List<Sanzioni> getSanzionis6() {
		return this.sanzionis6;
	}

	public void setSanzionis6(List<Sanzioni> sanzionis6) {
		this.sanzionis6 = sanzionis6;
	}

	public Sanzioni addSanzionis6(Sanzioni sanzionis6) {
		getSanzionis6().add(sanzionis6);
		sanzionis6.setScadenze6(this);

		return sanzionis6;
	}

	public Sanzioni removeSanzionis6(Sanzioni sanzionis6) {
		getSanzionis6().remove(sanzionis6);
		sanzionis6.setScadenze6(null);

		return sanzionis6;
	}

}