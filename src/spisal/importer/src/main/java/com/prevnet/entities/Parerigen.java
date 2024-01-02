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
 * The persistent class for the PARERIGEN database table.
 * 
 */
@Entity
@NamedQuery(name="Parerigen.findAll", query="SELECT p FROM Parerigen p")
public class Parerigen implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idparerigen;
	private Date dataemissioneparere;
	private Date datavrsdirsan;
	private String descrizione;
	private BigDecimal dirittisanitari;
	private BigDecimal idcircoscrizione;
	private BigDecimal iddittasedeintervento;
	private BigDecimal idlocalita;
	private Pratiche pratica;
	private BigDecimal idprotocollo;
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutentesedeintervento;
	private BigDecimal idstoricoutentesoggetto;
	private BigDecimal idstoricoutentestudio;
	private String indirizzo;
	private String note;
	private String numprot;
	private List<Altripareri> altripareris;
	private Anagrafiche soggetto;
	private Anagrafiche sedeIntervento;
	private Anagrafiche studioTecnico;
	private Comuni comune;
	private Ditte dittaSoggetto;
	private Ditte dittaStudio;
	private Tabelle esito;
	private Tabelle distretto;
	private Tabelle destinazioneUso;
	private Tabelle intervento;
	private Tabelle tipoParere;

	public Parerigen() {
	}


	@Id
	public long getIdparerigen() {
		return this.idparerigen;
	}

	public void setIdparerigen(long idparerigen) {
		this.idparerigen = idparerigen;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataemissioneparere() {
		return this.dataemissioneparere;
	}

	public void setDataemissioneparere(Date dataemissioneparere) {
		this.dataemissioneparere = dataemissioneparere;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavrsdirsan() {
		return this.datavrsdirsan;
	}

	public void setDatavrsdirsan(Date datavrsdirsan) {
		this.datavrsdirsan = datavrsdirsan;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getDirittisanitari() {
		return this.dirittisanitari;
	}

	public void setDirittisanitari(BigDecimal dirittisanitari) {
		this.dirittisanitari = dirittisanitari;
	}


	public BigDecimal getIdcircoscrizione() {
		return this.idcircoscrizione;
	}

	public void setIdcircoscrizione(BigDecimal idcircoscrizione) {
		this.idcircoscrizione = idcircoscrizione;
	}


	public BigDecimal getIddittasedeintervento() {
		return this.iddittasedeintervento;
	}

	public void setIddittasedeintervento(BigDecimal iddittasedeintervento) {
		this.iddittasedeintervento = iddittasedeintervento;
	}


	public BigDecimal getIdlocalita() {
		return this.idlocalita;
	}

	public void setIdlocalita(BigDecimal idlocalita) {
		this.idlocalita = idlocalita;
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


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdstoricoutentesedeintervento() {
		return this.idstoricoutentesedeintervento;
	}

	public void setIdstoricoutentesedeintervento(BigDecimal idstoricoutentesedeintervento) {
		this.idstoricoutentesedeintervento = idstoricoutentesedeintervento;
	}


	public BigDecimal getIdstoricoutentesoggetto() {
		return this.idstoricoutentesoggetto;
	}

	public void setIdstoricoutentesoggetto(BigDecimal idstoricoutentesoggetto) {
		this.idstoricoutentesoggetto = idstoricoutentesoggetto;
	}


	public BigDecimal getIdstoricoutentestudio() {
		return this.idstoricoutentestudio;
	}

	public void setIdstoricoutentestudio(BigDecimal idstoricoutentestudio) {
		this.idstoricoutentestudio = idstoricoutentestudio;
	}


	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNumprot() {
		return this.numprot;
	}

	public void setNumprot(String numprot) {
		this.numprot = numprot;
	}


	//bi-directional many-to-one association to Altripareri
	@OneToMany(mappedBy="parerigen")
	public List<Altripareri> getAltripareris() {
		return this.altripareris;
	}

	public void setAltripareris(List<Altripareri> altripareris) {
		this.altripareris = altripareris;
	}

	public Altripareri addAltripareri(Altripareri altripareri) {
		getAltripareris().add(altripareri);
		altripareri.setParerigen(this);

		return altripareri;
	}

	public Altripareri removeAltripareri(Altripareri altripareri) {
		getAltripareris().remove(altripareri);
		altripareri.setParerigen(null);

		return altripareri;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="SOGGETTO")
	public Anagrafiche getSoggetto() {
		return this.soggetto;
	}

	public void setSoggetto(Anagrafiche soggetto) {
		this.soggetto = soggetto;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSEDEINTERVENTO")
	public Anagrafiche getSedeIntervento() {
		return this.sedeIntervento;
	}

	public void setSedeIntervento(Anagrafiche sedeIntervento) {
		this.sedeIntervento = sedeIntervento;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTUDIOTECNICO")
	public Anagrafiche getStudioTecnico() {
		return this.studioTecnico;
	}

	public void setStudioTecnico(Anagrafiche studioTecnico) {
		this.studioTecnico = studioTecnico;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNE")
	public Comuni getComune() {
		return this.comune;
	}

	public void setComune(Comuni comune) {
		this.comune = comune;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASOGGETTO")
	public Ditte getDittaSoggetto() {
		return this.dittaSoggetto;
	}

	public void setDittaSoggetto(Ditte dittaSoggetto) {
		this.dittaSoggetto = dittaSoggetto;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASTUDIO")
	public Ditte getDittaStudio() {
		return this.dittaStudio;
	}

	public void setDittaStudio(Ditte dittaStudio) {
		this.dittaStudio = dittaStudio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="ESITO")
	public Tabelle getEsito() {
		return this.esito;
	}

	public void setEsito(Tabelle esito) {
		this.esito = esito;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTO")
	public Tabelle getDistretto() {
		return this.distretto;
	}

	public void setDistretto(Tabelle distretto) {
		this.distretto = distretto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDESTINAZIONEUSO")
	public Tabelle getDestinazioneUso() {
		return this.destinazioneUso;
	}

	public void setDestinazioneUso(Tabelle destinazioneUso) {
		this.destinazioneUso = destinazioneUso;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINTERVENTO")
	public Tabelle getIntervento() {
		return this.intervento;
	}

	public void setIntervento(Tabelle intervento) {
		this.intervento = intervento;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="TIPOPARERE")
	public Tabelle getTipoParere() {
		return this.tipoParere;
	}

	public void setTipoParere(Tabelle tipoParere) {
		this.tipoParere = tipoParere;
	}

}