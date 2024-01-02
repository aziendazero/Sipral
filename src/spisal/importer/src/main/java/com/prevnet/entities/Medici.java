package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the MEDICI database table.
 * 
 */
@Entity
public class Medici implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idmedici;
	private String centrocosto;
	private String chkmedfiscale;
	private String codice;
	private String codicefiscale;
	private String cognome;
	private String consulente;
	private Date datanascita;
	private Date datavalidita;
	private String email;
	private BigDecimal idoperatore;
	private String indirizzo;
	private String nome;
	private String note;
	private BigDecimal rimborsochilometrico;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private BigDecimal tariffaconsulente;
	private String telefono;
	private BigDecimal territoriocompetenza;
	private Comuni comune;
	private Comuni comuneNascita;
	private Tabelle tipologia;
	private Tabelle provincia;
	private Tabelle distretto;
	private Tabelle sede;
	private Tabelle esteroNascita;
	private Tabelle specializzazione;
	private List<Utenti> utentis;

	public Medici() {
	}


	@Id
	public long getIdmedici() {
		return this.idmedici;
	}

	public void setIdmedici(long idmedici) {
		this.idmedici = idmedici;
	}


	public String getCentrocosto() {
		return this.centrocosto;
	}

	public void setCentrocosto(String centrocosto) {
		this.centrocosto = centrocosto;
	}


	public String getChkmedfiscale() {
		return this.chkmedfiscale;
	}

	public void setChkmedfiscale(String chkmedfiscale) {
		this.chkmedfiscale = chkmedfiscale;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getConsulente() {
		return this.consulente;
	}

	public void setConsulente(String consulente) {
		this.consulente = consulente;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanascita() {
		return this.datanascita;
	}

	public void setDatanascita(Date datanascita) {
		this.datanascita = datanascita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavalidita() {
		return this.datavalidita;
	}

	public void setDatavalidita(Date datavalidita) {
		this.datavalidita = datavalidita;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public BigDecimal getIdoperatore() {
		return this.idoperatore;
	}

	public void setIdoperatore(BigDecimal idoperatore) {
		this.idoperatore = idoperatore;
	}


	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getRimborsochilometrico() {
		return this.rimborsochilometrico;
	}

	public void setRimborsochilometrico(BigDecimal rimborsochilometrico) {
		this.rimborsochilometrico = rimborsochilometrico;
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


	public BigDecimal getTariffaconsulente() {
		return this.tariffaconsulente;
	}

	public void setTariffaconsulente(BigDecimal tariffaconsulente) {
		this.tariffaconsulente = tariffaconsulente;
	}


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public BigDecimal getTerritoriocompetenza() {
		return this.territoriocompetenza;
	}

	public void setTerritoriocompetenza(BigDecimal territoriocompetenza) {
		this.territoriocompetenza = territoriocompetenza;
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


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNENASCITA")
	public Comuni getComuneNascita() {
		return this.comuneNascita;
	}

	public void setComuneNascita(Comuni comuneNascita) {
		this.comuneNascita = comuneNascita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLOGIA")
	public Tabelle getTipologia() {
		return this.tipologia;
	}

	public void setTipologia(Tabelle tipologia) {
		this.tipologia = tipologia;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVINCIA")
	public Tabelle getProvincia() {
		return this.provincia;
	}

	public void setProvincia(Tabelle provincia) {
		this.provincia = provincia;
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
	@JoinColumn(name="IDSEDE")
	public Tabelle getSede() {
		return this.sede;
	}

	public void setSede(Tabelle sede) {
		this.sede = sede;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESTERONASCITA")
	public Tabelle getEsteroNascita() {
		return this.esteroNascita;
	}

	public void setEsteroNascita(Tabelle esteroNascita) {
		this.esteroNascita = esteroNascita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSPECIALIZZAZIONE")
	public Tabelle getSpecializzazione() {
		return this.specializzazione;
	}

	public void setSpecializzazione(Tabelle specializzazione) {
		this.specializzazione = specializzazione;
	}


	//bi-directional many-to-one association to Utenti
	@OneToMany(mappedBy="medicoCurante")
	public List<Utenti> getUtentis() {
		return this.utentis;
	}

	public void setUtentis(List<Utenti> utentis) {
		this.utentis = utentis;
	}

	public Utenti addUtenti(Utenti utenti) {
		getUtentis().add(utenti);
		utenti.setMedicoCurante(this);

		return utenti;
	}

	public Utenti removeUtenti(Utenti utenti) {
		getUtentis().remove(utenti);
		utenti.setMedicoCurante(null);

		return utenti;
	}

}