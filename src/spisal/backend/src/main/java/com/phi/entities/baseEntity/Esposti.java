package com.phi.entities.baseEntity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

@javax.persistence.Entity
@Table(name = "esposti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Esposti extends BaseEntity {

	private static final long serialVersionUID = 1383243158L;

	/**
	*  javadoc for um
	*/
	private CodeValuePhi um;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="um")
	@ForeignKey(name="FK_Esposti_um")
	//@Index(name="IX_Esposti_um")
	public CodeValuePhi getUm(){
		return um;
	}

	public void setUm(CodeValuePhi um){
		this.um = um;
	}

	/**
	*  javadoc for valore
	*/
	private Double valore;

	@Column(name="valore")
	public Double getValore(){
		return valore;
	}

	public void setValore(Double valore){
		this.valore = valore;
	}

	/**
	*  javadoc for sostanza
	*/
	private CodeValuePhi sostanza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sostanza")
	@ForeignKey(name="FK_Esposti_sostanza")
	//@Index(name="IX_Esposti_sostanza")
	public CodeValuePhi getSostanza(){
		return sostanza;
	}

	public void setSostanza(CodeValuePhi sostanza){
		this.sostanza = sostanza;
	}

	/**
	*  javadoc for agente
	*/
	private CodeValuePhi agente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="agente")
	@ForeignKey(name="FK_Esposti_agente")
	//@Index(name="IX_Esposti_agente")
	public CodeValuePhi getAgente(){
		return agente;
	}

	public void setAgente(CodeValuePhi agente){
		this.agente = agente;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_Esposti_tipologia")
	//@Index(name="IX_Esposti_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
	}


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_Esposti_person")
	//@Index(name="IX_Esposti_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}

	/**
	*  javadoc for notInAll5
	*/
	private String notInAll5;

	@Column(name="not_in_all5")
	public String getNotInAll5(){
		return notInAll5;
	}

	public void setNotInAll5(String notInAll5){
		this.notInAll5 = notInAll5;
	}

	/**
	*  javadoc for bio
	*/
	private CodeValuePhi bio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bio")
	@ForeignKey(name="FK_Esposti_bio")
	//@Index(name="IX_Esposti_bio")
	public CodeValuePhi getBio(){
		return bio;
	}

	public void setBio(CodeValuePhi bio){
		this.bio = bio;
	}
	
	/**
	*  true se gruppo 3, false se gruppo 4
	*/
	private Boolean gruppo3;

	@Column(name="gruppo3")
	public Boolean getGruppo3(){
		return gruppo3;
	}

	public void setGruppo3(Boolean gruppo3){
		this.gruppo3 = gruppo3;
	}

	/**
	*  javadoc for cessazioneDate
	*/
	private Date cessazioneDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cessazione_date")
	public Date getCessazioneDate(){
		return cessazioneDate;
	}

	public void setCessazioneDate(Date cessazioneDate){
		this.cessazioneDate = cessazioneDate;
	}

	/**
	*  javadoc for endDate
	*/
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate(){
		return endDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	/**
	*  javadoc for startDate
	*/
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	public Date getStartDate(){
		return startDate;
	}

	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	/**
	*  javadoc for tempo
	*/
	private Integer tempo;

	@Column(name="tempo")
	public Integer getTempo(){
		return tempo;
	}

	public void setTempo(Integer tempo){
		this.tempo = tempo;
	}

	/**
	*  javadoc for metodo
	*/
	private String metodo;

	@Column(name="metodo")
	public String getMetodo(){
		return metodo;
	}

	public void setMetodo(String metodo){
		this.metodo = metodo;
	}

	/**
	*  javadoc for cancerogeno
	*/
	private List<CodeValuePhi> cancerogeno;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name="singleesp_cancerogeno", joinColumns = { @JoinColumn(name="esposto_id") }, inverseJoinColumns = { @JoinColumn(name="cancerogeno") })
	@ForeignKey(name="FK_cancer_singleEsp", inverseName="FK_singleEsp_cancer")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getCancerogeno(){
		return cancerogeno;
	}

	public void setCancerogeno(List<CodeValuePhi> cancerogeno){
		this.cancerogeno = cancerogeno;
	}

	/**
	*  javadoc for codiceCE
	*/
	private CodeValuePhi codiceCE;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="codiceCE")
	@ForeignKey(name="FK_esp_codiceCE")
	//@Index(name="IX_esp_codiceCE")
	public CodeValuePhi getCodiceCE(){
		return codiceCE;
	}

	public void setCodiceCE(CodeValuePhi codiceCE){
		this.codiceCE = codiceCE;
	}

	/**
	*  javadoc for cas
	*/
	private CodeValuePhi cas;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="cas")
	@ForeignKey(name="FK_esp_cas")
	//@Index(name="IX_esp_cas")
	public CodeValuePhi getCas(){
		return cas;
	}

	public void setCas(CodeValuePhi cas){
		this.cas = cas;
	}

	/**
	*  javadoc for agenti
	*/
	private String agenti;

	@Column(name="agenti")
	public String getAgenti(){
		return agenti;
	}

	public void setAgenti(String agenti){
		this.agenti = agenti;
	}

	/**
	*  javadoc for tipo
	*/
	private CodeValuePhi tipo;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="tipo")
	@ForeignKey(name="FK_esp_tipo")
	//@Index(name="IX_esp_tipo")
	public CodeValuePhi getTipo(){
		return tipo;
	}

	public void setTipo(CodeValuePhi tipo){
		this.tipo = tipo;
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
	*  javadoc for mansione
	*/
	private CodeValuePhi mansione;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="mansione")
	@ForeignKey(name="FK_esp_mansione")
	//@Index(name="IX_esp_mansione")
	public CodeValuePhi getMansione(){
		return mansione;
	}

	public void setMansione(CodeValuePhi mansione){
		this.mansione = mansione;
	}

	/**
	*  javadoc for codClass
	*/
	private CodeValuePhi codClass;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="codClass")
	@ForeignKey(name="FK_esp_codClass")
	//@Index(name="IX_esp_codClass")
	public CodeValuePhi getCodClass(){
		return codClass;
	}

	public void setCodClass(CodeValuePhi codClass){
		this.codClass = codClass;
	}

	/**
	*  javadoc for schedaEsposti
	*/
	private SchedaEsposti schedaEsposti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="schedaEsposti_id")
	@ForeignKey(name="FK_esp_schedaEsp")
	//@Index(name="IX_esp_schedaEsp")
	public SchedaEsposti getSchedaEsposti(){
		return schedaEsposti;
	}

	public void setSchedaEsposti(SchedaEsposti schedaEsposti){
		this.schedaEsposti = schedaEsposti;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Esposti_sequence")
	@SequenceGenerator(name = "Esposti_sequence", sequenceName = "Esposti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
