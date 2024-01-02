package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.amianto.entities.Dlco;


/**
 * The persistent class for the anagrafica database table.
 * 
 */
@Entity
@Table(name="anagrafica")
public class Anagrafica implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String cap;
	private String citta;
	private String codiceFiscale;
	private String cognomeENome;
	private Date consegnaEsenzione;
	private Date dataCompilazioneQuestionario;
	private Date dataMorte;
	private Date dataNascita;
	private Date dataRichiestaVisita;
	private String esenzioneNonRitirata;
	private String esposizioneACvm;
	private Integer gestioneRadiografie;
	private String indirizzo;
	private String miglioramenti;
	private Double numeroLotus;
	private String operatoreSanitario;
	private Integer sesso;
	private String telefono;
	private String tesseraSanitaria;
	private TipologiaRichiesta tipo;
	private EsposizioneProfessionale esposizioneProfessionale;
	private PrimaVisita visitaMedica;
	private PrimoControllo primoControllo;
	private SecondoControllo secondoControllo;
	private TerzoControllo terzoControllo;
	private QuartoControllo quartoControllo;
	private QuintoControllo quintoControllo;
	private SestoControllo sestoControllo;
	private SettimoControllo settimoControllo;
	private OttavoControllo ottavoControllo;
	private ProssimoControllo prossimoControllo;
	private List<Visita> visita;
	private List<VisitaSpecialistica> visitaSpecialistica;
	private List<EsameSpirometrico> esameSpirometrico;
	private List<RadiografiaTorace> radiografiaTorace;
	private List<Tac> tac;
	private List<EsameIstologico> esameIstologico;
	private List<NoduloPolmonare> noduloPolmonare;
	private List<IndagineMalattiaProfessionale> indagineMalattiaProfessionale;
	
	private AbitudineAlFumo abitudineAlFumo;
	private List<Dlco> dlco;
	public Anagrafica() {
	}


	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(length=50)
	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	@Column(length=50)
	public String getCitta() {
		return this.citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}


	@Column(name="codice_fiscale", length=50)
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}


	@Column(name="cognome_e_nome", length=255)
	public String getCognomeENome() {
		return this.cognomeENome;
	}

	public void setCognomeENome(String cognomeENome) {
		this.cognomeENome = cognomeENome;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="consegna_esenzione")
	public Date getConsegnaEsenzione() {
		return this.consegnaEsenzione;
	}

	public void setConsegnaEsenzione(Date consegnaEsenzione) {
		this.consegnaEsenzione = consegnaEsenzione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_compilazione_questionario")
	public Date getDataCompilazioneQuestionario() {
		return this.dataCompilazioneQuestionario;
	}

	public void setDataCompilazioneQuestionario(Date dataCompilazioneQuestionario) {
		this.dataCompilazioneQuestionario = dataCompilazioneQuestionario;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_morte")
	public Date getDataMorte() {
		return this.dataMorte;
	}

	public void setDataMorte(Date dataMorte) {
		this.dataMorte = dataMorte;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_nascita")
	public Date getDataNascita() {
		return this.dataNascita;
	}

	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_richiesta_visita")
	public Date getDataRichiestaVisita() {
		return this.dataRichiestaVisita;
	}

	public void setDataRichiestaVisita(Date dataRichiestaVisita) {
		this.dataRichiestaVisita = dataRichiestaVisita;
	}


	@Column(name="esenzione_non_ritirata", length=255)
	public String getEsenzioneNonRitirata() {
		return this.esenzioneNonRitirata;
	}

	public void setEsenzioneNonRitirata(String esenzioneNonRitirata) {
		this.esenzioneNonRitirata = esenzioneNonRitirata;
	}


	@Column(name="esposizione_a_cvm", length=255)
	public String getEsposizioneACvm() {
		return this.esposizioneACvm;
	}

	public void setEsposizioneACvm(String esposizioneACvm) {
		this.esposizioneACvm = esposizioneACvm;
	}


	@Column(name="gestione_radiografie")
	public Integer getGestioneRadiografie() {
		return this.gestioneRadiografie;
	}

	public void setGestioneRadiografie(Integer gestioneRadiografie) {
		this.gestioneRadiografie = gestioneRadiografie;
	}


	@Column(length=50)
	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	@Column(length=250)
	public String getMiglioramenti() {
		return this.miglioramenti;
	}

	public void setMiglioramenti(String miglioramenti) {
		this.miglioramenti = miglioramenti;
	}


	@Column(name="numero_lotus")
	public Double getNumeroLotus() {
		return this.numeroLotus;
	}

	public void setNumeroLotus(Double numeroLotus) {
		this.numeroLotus = numeroLotus;
	}


	@Column(name="operatore_sanitario", length=50)
	public String getOperatoreSanitario() {
		return this.operatoreSanitario;
	}

	public void setOperatoreSanitario(String operatoreSanitario) {
		this.operatoreSanitario = operatoreSanitario;
	}


	public Integer getSesso() {
		return this.sesso;
	}

	public void setSesso(Integer sesso) {
		this.sesso = sesso;
	}


	@Column(length=50)
	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	@Column(name="tessera_sanitaria", length=255)
	public String getTesseraSanitaria() {
		return this.tesseraSanitaria;
	}

	public void setTesseraSanitaria(String tesseraSanitaria) {
		this.tesseraSanitaria = tesseraSanitaria;
	}


	@ManyToOne
	@JoinColumn(name = "tipologia_richiesta", referencedColumnName = "id")
	@NotFound(action=NotFoundAction.IGNORE)
	public TipologiaRichiesta getTipo() {
	    return tipo;
	}


	public void setTipo(TipologiaRichiesta param) {
	    this.tipo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public EsposizioneProfessionale getEsposizioneProfessionale() {
	    return esposizioneProfessionale;
	}


	public void setEsposizioneProfessionale(EsposizioneProfessionale param) {
	    this.esposizioneProfessionale = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public PrimaVisita getVisitaMedica() {
	    return visitaMedica;
	}


	public void setVisitaMedica(PrimaVisita param) {
	    this.visitaMedica = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public PrimoControllo getPrimoControllo() {
	    return primoControllo;
	}


	public void setPrimoControllo(PrimoControllo param) {
	    this.primoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public SecondoControllo getSecondoControllo() {
	    return secondoControllo;
	}


	public void setSecondoControllo(SecondoControllo param) {
	    this.secondoControllo = param;
	}
	

	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public TerzoControllo getTerzoControllo() {
	    return terzoControllo;
	}


	public void setTerzoControllo(TerzoControllo param) {
	    this.terzoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public QuartoControllo getQuartoControllo() {
	    return quartoControllo;
	}


	public void setQuartoControllo(QuartoControllo param) {
	    this.quartoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public QuintoControllo getQuintoControllo() {
	    return quintoControllo;
	}


	public void setQuintoControllo(QuintoControllo param) {
	    this.quintoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public SestoControllo getSestoControllo() {
	    return sestoControllo;
	}


	public void setSestoControllo(SestoControllo param) {
	    this.sestoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public SettimoControllo getSettimoControllo() {
	    return settimoControllo;
	}


	public void setSettimoControllo(SettimoControllo param) {
	    this.settimoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public OttavoControllo getOttavoControllo() {
	    return ottavoControllo;
	}


	public void setOttavoControllo(OttavoControllo param) {
	    this.ottavoControllo = param;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	@NotFound(action=NotFoundAction.IGNORE)
	public ProssimoControllo getProssimoControllo() {
	    return prossimoControllo;
	}


	public void setProssimoControllo(ProssimoControllo param) {
	    this.prossimoControllo = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<Visita> getVisita() {
	    return visita;
	}


	public void setVisita(List<Visita> param) {
	    this.visita = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<VisitaSpecialistica> getVisitaSpecialistica() {
	    return visitaSpecialistica;
	}


	public void setVisitaSpecialistica(List<VisitaSpecialistica> param) {
	    this.visitaSpecialistica = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<EsameSpirometrico> getEsameSpirometrico() {
	    return esameSpirometrico;
	}


	public void setEsameSpirometrico(List<EsameSpirometrico> param) {
	    this.esameSpirometrico = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<RadiografiaTorace> getRadiografiaTorace() {
	    return radiografiaTorace;
	}


	public void setRadiografiaTorace(List<RadiografiaTorace> param) {
	    this.radiografiaTorace = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<Tac> getTac() {
	    return tac;
	}


	public void setTac(List<Tac> param) {
	    this.tac = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<EsameIstologico> getEsameIstologico() {
	    return esameIstologico;
	}


	public void setEsameIstologico(List<EsameIstologico> param) {
	    this.esameIstologico = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<NoduloPolmonare> getNoduloPolmonare() {
	    return noduloPolmonare;
	}


	public void setNoduloPolmonare(List<NoduloPolmonare> param) {
	    this.noduloPolmonare = param;
	}


	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "id", referencedColumnName = "id_ex_esposto", insertable = false, updatable = false)
	public AbitudineAlFumo getAbitudineAlFumo() {
	    return abitudineAlFumo;
	}


	public void setAbitudineAlFumo(AbitudineAlFumo param) {
	    this.abitudineAlFumo = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	public List<Dlco> getDlco() {
	    return dlco;
	}


	public void setDlco(List<Dlco> param) {
	    this.dlco = param;
	}

	@OneToMany(mappedBy = "anagrafica")
	public List<IndagineMalattiaProfessionale> getIndagineMalattiaProfessionale() {
	    return indagineMalattiaProfessionale;
	}


	public void setIndagineMalattiaProfessionale(List<IndagineMalattiaProfessionale> param) {
	    this.indagineMalattiaProfessionale = param;
	}
}