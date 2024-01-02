package com.phi.entities.baseEntity;

import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.actions.PersonaGiuridicaSedeAction;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
@javax.persistence.Entity
@Table(name = "parere_tecnico")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ParereTecnico extends BaseEntity {

	private static final long serialVersionUID = 569223176L;

	/**
	*  javadoc for mansione
	*/
	private CodeValuePhi mansione;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mansione")
	@ForeignKey(name="FK_ParereTecnico_mansione")
	//@Index(name="IX_ParereTecnico_mansione")
	public CodeValuePhi getMansione(){
		return mansione;
	}

	public void setMansione(CodeValuePhi mansione){
		this.mansione = mansione;
	}


	/**
	*  javadoc for patentini
	*/
	private List<Patentini> patentini;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="parereTecnico", cascade=CascadeType.PERSIST)
	public List<Patentini> getPatentini(){
		return patentini;
	}

	public void setPatentini(List<Patentini> list){
		patentini = list;
	}

	public void addPatentini(Patentini patentini) {
		if (this.patentini == null) {
			this.patentini = new ArrayList<Patentini>();
		}
		// add the association
		if(!this.patentini.contains(patentini)) {
			this.patentini.add(patentini);
			// make the inverse link
			patentini.setParereTecnico(this);
		}
	}

	public void removePatentini(Patentini patentini) {
		if (this.patentini == null) {
			this.patentini = new ArrayList<Patentini>();
			return;
		}
		//add the association
		if(this.patentini.contains(patentini)){
			this.patentini.remove(patentini);
			//make the inverse link
			patentini.setParereTecnico(null);
		}

	}


	/**
	*  javadoc for punteggioEsame
	*/
	private String punteggioEsame;

	@Column(name="punteggio_esame")
	public String getPunteggioEsame(){
		return punteggioEsame;
	}

	public void setPunteggioEsame(String punteggioEsame){
		this.punteggioEsame = punteggioEsame;
	}

	/**
	*  javadoc for dataEsame
	*/
	private Date dataEsame;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_esame")
	public Date getDataEsame(){
		return dataEsame;
	}

	public void setDataEsame(Date dataEsame){
		this.dataEsame = dataEsame;
	}

	/**
	*  javadoc for gas
	*/
	private List<CodeValuePhi> gas;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="partecnico_gas", joinColumns = { @JoinColumn(name="ParereTecnico_id") }, inverseJoinColumns = { @JoinColumn(name="gas") })
	@ForeignKey(name="FK_gas_ParereTecnico", inverseName="FK_ParereTecnico_gas")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getGas(){
		return gas;
	}

	public void setGas(List<CodeValuePhi> gas){
		this.gas = gas;
	}

	/**
	*  javadoc for matricola
	*/
	private String matricola;

	@Column(name="matricola")
	public String getMatricola(){
		return matricola;
	}

	public void setMatricola(String matricola){
		this.matricola = matricola;
	}

	/**
	*  javadoc for idoneita
	*/
	private CodeValuePhi idoneita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="idoneita")
	@ForeignKey(name="FK_ParereTecnico_idoneita")
	//@Index(name="IX_ParereTecnico_idoneita")
	public CodeValuePhi getIdoneita(){
		return idoneita;
	}

	public void setIdoneita(CodeValuePhi idoneita){
		this.idoneita = idoneita;
	}

	/**
	*  javadoc for class33
	*/
	private CodeValuePhi class33;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class33")
	@ForeignKey(name="FK_ParereTecnico_class33")
	//@Index(name="IX_ParereTecnico_class33")
	public CodeValuePhi getClass33(){
		return class33;
	}

	public void setClass33(CodeValuePhi class33){
		this.class33 = class33;
	}

	/**
	*  javadoc for class32
	*/
	private CodeValuePhi class32;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class32")
	@ForeignKey(name="FK_ParereTecnico_class32")
	//@Index(name="IX_ParereTecnico_class32")
	public CodeValuePhi getClass32(){
		return class32;
	}

	public void setClass32(CodeValuePhi class32){
		this.class32 = class32;
	}

	/**
	*  javadoc for class31
	*/
	private CodeValuePhi class31;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class31")
	@ForeignKey(name="FK_ParereTecnico_class31")
	//@Index(name="IX_ParereTecnico_class31")
	public CodeValuePhi getClass31(){
		return class31;
	}

	public void setClass31(CodeValuePhi class31){
		this.class31 = class31;
	}

	/**
	*  javadoc for class23
	*/
	private CodeValuePhi class23;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class23")
	@ForeignKey(name="FK_ParereTecnico_class23")
	//@Index(name="IX_ParereTecnico_class23")
	public CodeValuePhi getClass23(){
		return class23;
	}

	public void setClass23(CodeValuePhi class23){
		this.class23 = class23;
	}

	/**
	*  javadoc for class22
	*/
	private CodeValuePhi class22;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class22")
	@ForeignKey(name="FK_ParereTecnico_class22")
	//@Index(name="IX_ParereTecnico_class22")
	public CodeValuePhi getClass22(){
		return class22;
	}

	public void setClass22(CodeValuePhi class22){
		this.class22 = class22;
	}

	/**
	*  javadoc for class21
	*/
	private CodeValuePhi class21;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="class21")
	@ForeignKey(name="FK_ParereTecnico_class21")
	//@Index(name="IX_ParereTecnico_class21")
	public CodeValuePhi getClass21(){
		return class21;
	}

	public void setClass21(CodeValuePhi class21){
		this.class21 = class21;
	}

	/**
	*  javadoc for terminiOriginali
	*/
	private Date terminiOriginali;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="termini_originali")
	public Date getTerminiOriginali(){
		return terminiOriginali;
	}

	public void setTerminiOriginali(Date terminiOriginali){
		this.terminiOriginali = terminiOriginali;
	}

	/**
	*  javadoc for richiestaDettaglio
	*/
	private String richiestaDettaglio;

	@Column(name="richiesta_dettaglio", length=4000)
	public String getRichiestaDettaglio(){
		return richiestaDettaglio;
	}

	public void setRichiestaDettaglio(String richiestaDettaglio){
		this.richiestaDettaglio = richiestaDettaglio;
	}

	/**
	*  javadoc for terminiNote
	*/
	private String terminiNote;

	@Column(name="termini_note")
	public String getTerminiNote(){
		return terminiNote;
	}

	public void setTerminiNote(String terminiNote){
		this.terminiNote = terminiNote;
	}

	/**
	*  javadoc for terminiRisposta
	*/
	private Date terminiRisposta;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="termini_risposta")
	public Date getTerminiRisposta(){
		return terminiRisposta;
	}

	public void setTerminiRisposta(Date terminiRisposta){
		this.terminiRisposta = terminiRisposta;
	}


	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_ParereTecnico_person")
	//@Index(name="IX_ParereTecnico_person")
	@NotAudited
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}


	/**
	*  javadoc for destinazioneUso
	*/
	private String destinazioneUso;

	@Column(name="destinazione_uso")
	public String getDestinazioneUso(){
		return destinazioneUso;
	}

	public void setDestinazioneUso(String destinazioneUso){
		this.destinazioneUso = destinazioneUso;
	}

	/**
	*  javadoc for dataParto
	*/
	private Date dataParto;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_parto")
	public Date getDataParto(){
		return dataParto;
	}

	public void setDataParto(Date dataParto){
		this.dataParto = dataParto;
	}

	/**
	*  javadoc for patExpDate
	*/
	private Date patExpDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_exp_date")
	public Date getPatExpDate(){
		return patExpDate;
	}

	public void setPatExpDate(Date patExpDate){
		this.patExpDate = patExpDate;
	}

	/**
	*  javadoc for patRilDate
	*/
	private Date patRilDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_ril_date")
	public Date getPatRilDate(){
		return patRilDate;
	}

	public void setPatRilDate(Date patRilDate){
		this.patRilDate = patRilDate;
	}

	/**
	*  javadoc for patDomDate
	*/
	private Date patDomDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="pat_dom_date")
	public Date getPatDomDate(){
		return patDomDate;
	}

	public void setPatDomDate(Date patDomDate){
		this.patDomDate = patDomDate;
	}

	/**
	*  javadoc for patGas
	*/
	private String patGas;

	@Column(name="pat_gas")
	public String getPatGas(){
		return patGas;
	}

	public void setPatGas(String patGas){
		this.patGas = patGas;
	}

	/**
	*  javadoc for patType
	*/
	private CodeValuePhi patType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patType")
	@ForeignKey(name="FK_ParereTecnico_patType")
	//@Index(name="IX_ParereTecnico_patType")
	public CodeValuePhi getPatType(){
		return patType;
	}

	public void setPatType(CodeValuePhi patType){
		this.patType = patType;
	}


	/**
	*  javadoc for personaGiuridicaSede
	*/
	private List<PersonaGiuridicaSede> personaGiuridicaSede;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="parereTecnico", cascade=CascadeType.PERSIST)
	public List<PersonaGiuridicaSede> getPersonaGiuridicaSede(){
		return personaGiuridicaSede;
	}

	public void setPersonaGiuridicaSede(List<PersonaGiuridicaSede> list){
		personaGiuridicaSede = list;
	}

	public void addPersonaGiuridicaSede(PersonaGiuridicaSede personaGiuridicaSede) {
		if (this.personaGiuridicaSede == null) {
			this.personaGiuridicaSede = new ArrayList<PersonaGiuridicaSede>();
		}
		// add the association
		if(!this.personaGiuridicaSede.contains(personaGiuridicaSede)) {
			this.personaGiuridicaSede.add(personaGiuridicaSede);
			// make the inverse link
			personaGiuridicaSede.setParereTecnico(this);
		}
	}

	public void removePersonaGiuridicaSede(PersonaGiuridicaSede personaGiuridicaSede) {
		if (this.personaGiuridicaSede == null) {
			this.personaGiuridicaSede = new ArrayList<PersonaGiuridicaSede>();
			return;
		}
		//add the association
		if(this.personaGiuridicaSede.contains(personaGiuridicaSede)){
			this.personaGiuridicaSede.remove(personaGiuridicaSede);
			//make the inverse link
			personaGiuridicaSede.setParereTecnico(null);
		}

	}


	/**
	*  javadoc for durataGG
	*/
	private Integer duratagg;

	@Column(name="durata_gg")
	public Integer getDuratagg(){
		return duratagg;
	}

	public void setDuratagg(Integer duratagg){
		this.duratagg = duratagg;
	}

	/**
	*  javadoc for gradoPericolosita
	*/
	private CodeValuePhi gradoPericolosita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gradoPericolosita")
	@ForeignKey(name="FK_ParereTecnico_gradoPericolosita")
	//@Index(name="IX_ParereTecnico_gradoPericolosita")
	public CodeValuePhi getGradoPericolosita(){
		return gradoPericolosita;
	}

	public void setGradoPericolosita(CodeValuePhi gradoPericolosita){
		this.gradoPericolosita = gradoPericolosita;
	}

	/**
	*  javadoc for classText
	*/
	private String classText;

	@Column(name="class_text")
	public String getClassText(){
		return classText;
	}

	public void setClassText(String classText){
		this.classText = classText;
	}

	/**
	*  javadoc for classCode
	*/
	private CodeValuePhi classCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="classCode")
	@ForeignKey(name="FK_ParereTecnico_classCode")
	//@Index(name="IX_ParereTecnico_classCode")
	public CodeValuePhi getClassCode(){
		return classCode;
	}

	public void setClassCode(CodeValuePhi classCode){
		this.classCode = classCode;
	}

	/**
	*  javadoc for provType
	*/
	private CodeValuePhi provType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provType")
	@ForeignKey(name="FK_ParereTecnico_provType")
	//@Index(name="IX_ParereTecnico_provType")
	public CodeValuePhi getProvType(){
		return provType;
	}

	public void setProvType(CodeValuePhi provType){
		this.provType = provType;
	}

	/**
	*  javadoc for richiesta
	*/
	private CodeValuePhi richiesta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="richiesta")
	@ForeignKey(name="FK_ParereTecnico_richiesta")
	//@Index(name="IX_ParereTecnico_richiesta")
	public CodeValuePhi getRichiesta(){
		return richiesta;
	}

	public void setRichiesta(CodeValuePhi richiesta){
		this.richiesta = richiesta;
	}

	/**
	*  javadoc for tarDovuto
	*/
	private CodeValuePhi tarDovuto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tarDovuto")
	@ForeignKey(name="FK_ParereTecnico_tarDovuto")
	//@Index(name="IX_ParereTecnico_tarDovuto")
	public CodeValuePhi getTarDovuto(){
		return tarDovuto;
	}

	public void setTarDovuto(CodeValuePhi tarDovuto){
		this.tarDovuto = tarDovuto;
	}

	/**
	*  javadoc for tariffa
	*/
	private String tariffa;

	public String getTariffa(){
		return tariffa;
	}

	public void setTariffa(String tariffa){
		this.tariffa = tariffa;
	}

	/**
	*  javadoc for tarOreMq
	*/
	private Double tarOreMq;

	@Column(name="tar_ore_mq")
	public Double getTarOreMq(){
		return tarOreMq;
	}

	public void setTarOreMq(Double tarOreMq){
		this.tarOreMq = tarOreMq;
	}

	/**
	*  javadoc for protocolloData
	*/
	private Date protocolloData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="protocollo_data")
	public Date getProtocolloData(){
		return protocolloData;
	}

	public void setProtocolloData(Date protocolloData){
		this.protocolloData = protocolloData;
	}

	/**
	*  javadoc for protocolloUscita
	*/
	private String protocolloUscita;

	@Column(name="protocollo_uscita", length=4000)
	public String getProtocolloUscita(){
		return protocolloUscita;
	}

	public void setProtocolloUscita(String protocolloUscita){
		this.protocolloUscita = protocolloUscita;
	}

	/**
	*  javadoc for parereText
	*/
	private String parereText;

	@Column(name="parere_text", length=4000)
	public String getParereText(){
		return parereText;
	}

	public void setParereText(String parereText){
		this.parereText = parereText;
	}

	/**
	*  javadoc for parereData
	*/
	private Date parereData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="parere_data")
	public Date getParereData(){
		return parereData;
	}

	public void setParereData(Date parereData){
		this.parereData = parereData;
	}

	/**
	*  javadoc for parere
	*/
	private CodeValuePhi parere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parere")
	@ForeignKey(name="FK_ParereTecnico_parere")
	//@Index(name="IX_ParereTecnico_parere")
	public CodeValuePhi getParere(){
		return parere;
	}

	public void setParere(CodeValuePhi parere){
		this.parere = parere;
	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_ParereTecnico_type")
	//@Index(name="IX_ParereTecnico_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}


	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_PrereTecnico_procprtiche")
	//@Index(name="IX_PrereTecnico_procprtiche")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ParereTecnico_sequence")
	@SequenceGenerator(name = "ParereTecnico_sequence", sequenceName = "ParereTecnico_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	@Transient
	public String getDipendentePresso(){
    	String ret = "";
    	try {

    		PersonaGiuridicaSedeAction pgsa = PersonaGiuridicaSedeAction.instance();
    		ret = pgsa.getName("DTYPE03",personaGiuridicaSede);
    		
    		
		} catch (Exception e) {
		}
    	
    	
    	
    	return ret;
    	
    }
	
	@Transient
	public String getDipendentePressoAddr(){
    	String ret = "";
    	try {

    		PersonaGiuridicaSedeAction pgsa = PersonaGiuridicaSedeAction.instance();
    		AD ad = pgsa.getAddr("DTYPE03",personaGiuridicaSede);
    		if(ad.getAdl()!=null){
    			ret+=ad.getAdl()+", ";
    		}
    		ret = ad.getCty()+" in via "+ad.getStr()+" n° "+ad.getBnr();
    		
    		
    		
		} catch (Exception e) {
		}
    	
    	
    	
    	return ret;
    	
    }

}
