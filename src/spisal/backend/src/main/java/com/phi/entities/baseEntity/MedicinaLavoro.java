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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.Physician;
@javax.persistence.Entity
@Table(name = "medicina_lavoro")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MedicinaLavoro extends BaseEntity {

	private static final long serialVersionUID = 428748314L;


	/**
	*  javadoc for protocollo
	*/
	private List<Protocollo> protocollo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="medicinaLavoro", cascade=CascadeType.PERSIST)
	public List<Protocollo> getProtocollo(){
		return protocollo;
	}

	public void setProtocollo(List<Protocollo> list){
		protocollo = list;
	}

	public void addProtocollo(Protocollo protocollo) {
		if (this.protocollo == null) {
			this.protocollo = new ArrayList<Protocollo>();
		}
		// add the association
		if(!this.protocollo.contains(protocollo)) {
			this.protocollo.add(protocollo);
			// make the inverse link
			protocollo.setMedicinaLavoro(this);
		}
	}

	public void removeProtocollo(Protocollo protocollo) {
		if (this.protocollo == null) {
			this.protocollo = new ArrayList<Protocollo>();
			return;
		}
		//add the association
		if(this.protocollo.contains(protocollo)){
			this.protocollo.remove(protocollo);
			//make the inverse link
			protocollo.setMedicinaLavoro(null);
		}

	}


	/**
	*  javadoc for sedeText
	*/
	private String sedeText;

	@Column(name="sede_text")
	public String getSedeText(){
		return sedeText;
	}

	public void setSedeText(String sedeText){
		this.sedeText = sedeText;
	}

	/**
	*  javadoc for gravita
	*/
	private CodeValuePhi gravita;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gravita")
	@ForeignKey(name="FK_Mdl_gravita")
	//@Index(name="IX_MalProf_gravita")
	public CodeValuePhi getGravita(){
		return gravita;
	}

	public void setGravita(CodeValuePhi gravita){
		this.gravita = gravita;
	}

	/**
	*  javadoc for diagCode
	*/
	private CodeValueIcd9 diagCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="diagCode")
	@ForeignKey(name="FK_Mdl_diagCode")
	//@Index(name="IX_MalProf_diagCode")
	public CodeValueIcd9 getDiagCode(){
		return diagCode;
	}

	public void setDiagCode(CodeValueIcd9 diagCode){
		this.diagCode = diagCode;
	}

	/**
	*  javadoc for diagText
	*/
	private String diagText;

	@Column(name="diag_text", length=2500)
	public String getDiagText(){
		return diagText;
	}

	public void setDiagText(String diagText){
		this.diagText = diagText;
	}


	/**
	*  javadoc for ricorsoDa
	*/
	private CodeValuePhi ricorsoDa;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ricorsoDa")
	@ForeignKey(name="FK_MedicinaLavoro_ricorsoDa")
	@Index(name="IX_MedicinaLavoro_ricorsoDa")
	public CodeValuePhi getRicorsoDa(){
		return ricorsoDa;
	}

	public void setRicorsoDa(CodeValuePhi ricorsoDa){
		this.ricorsoDa = ricorsoDa;
	}

	/**
	*  javadoc for condProf
	*/
	private CodeValuePhi condProf;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="condProf")
	@ForeignKey(name="FK_MedicinaLavoro_condProf")
	//@Index(name="IX_MedicinaLavoro_condProf")
	public CodeValuePhi getCondProf(){
		return condProf;
	}

	public void setCondProf(CodeValuePhi condProf){
		this.condProf = condProf;
	}

	/**
	*  javadoc for dataAccertamento
	*/
	private Date dataAccertamento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_accertamento")
	public Date getDataAccertamento(){
		return dataAccertamento;
	}

	public void setDataAccertamento(Date dataAccertamento){
		this.dataAccertamento = dataAccertamento;
	}


	/**
	*  javadoc for medico
	*/
	private Physician medico;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="medico_id")
	@ForeignKey(name="FK_Mdl_medico")
	//@Index(name="IX_Mdl_medico")
	public Physician getMedico(){
		return medico;
	}

	public void setMedico(Physician medico){
		this.medico = medico;
	}


	/**
	*  javadoc for aMdlPratNumber
	*/
	private String amdlPratNumber;

	@Column(name="a_mdl_prat_number")
	public String getAmdlPratNumber(){
		return amdlPratNumber;
	}

	public void setAmdlPratNumber(String amdlPratNumber){
		this.amdlPratNumber = amdlPratNumber;
	}

	/**
	*  javadoc for aPatPratNumber
	*/
	private String apatPratNumber;

	@Column(name="a_pat_prat_number")
	public String getApatPratNumber(){
		return apatPratNumber;
	}

	public void setApatPratNumber(String apatPratNumber){
		this.apatPratNumber = apatPratNumber;
	}

	/**
	*  javadoc for afisPratNumber
	*/
	private String afisPratNumber;

	@Column(name="a_fis_prat_number")
	public String getAfisPratNumber(){
		return afisPratNumber;
	}

	public void setAfisPratNumber(String afisPratNumber){
		this.afisPratNumber = afisPratNumber;
	}

	/**
	*  javadoc for anamnesiProssima
	*/
	private String anamnesiProssima;

	@Column(name="anamnesi_prossima",length=2500)
	public String getAnamnesiProssima(){
		return anamnesiProssima;
	}

	public void setAnamnesiProssima(String anamnesiProssima){
		this.anamnesiProssima = anamnesiProssima;
	}

	/**
	*  javadoc for commissione
	*/
	private String commissione;

	@Column(name="commissione",length=2500)
	public String getCommissione(){
		return commissione;
	}

	public void setCommissione(String commissione){
		this.commissione = commissione;
	}

	/**
	*  javadoc for motivoRicorso
	*/
	private String motivoRicorso;

	@Column(name="motivo_ricorso",length=2500)
	public String getMotivoRicorso(){
		return motivoRicorso;
	}

	public void setMotivoRicorso(String motivoRicorso){
		this.motivoRicorso = motivoRicorso;
	}

	/**
	*  javadoc for orario
	*/
	private String orario;

	@Column(name="orario")
	public String getOrario(){
		return orario;
	}

	public void setOrario(String orario){
		this.orario = orario;
	}

	/**
	*  javadoc for mansione
	*/
	private String mansione;

	@Column(name="mansione")
	public String getMansione(){
		return mansione;
	}

	public void setMansione(String mansione){
		this.mansione = mansione;
	}

	/**
	*  javadoc for esitoCVM
	*/
	private CodeValueIcd9 esitoCVM;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="esitoCVM")
	@ForeignKey(name="FK_Mdl_esitoCVM")
	//@Index(name="IX_Mdl_esitoCVM")
	public CodeValueIcd9 getEsitoCVM(){
		return esitoCVM;
	}

	public void setEsitoCVM(CodeValueIcd9 esitoCVM){
		this.esitoCVM = esitoCVM;
	}

	/**
	*  javadoc for tipoExEsposto
	*/
	private CodeValuePhi tipoExEsposto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoExEsposto")
	@ForeignKey(name="FK_Mdl_tipoExEsposto")
	//@Index(name="IX_Mdl_tipoExEsposto")
	public CodeValuePhi getTipoExEsposto(){
		return tipoExEsposto;
	}

	public void setTipoExEsposto(CodeValuePhi tipoExEsposto){
		this.tipoExEsposto = tipoExEsposto;
	}

	/**
	*  javadoc for anamnesiPatologica
	*/
	private String anamnesiPatologica;

	@Column(name="anamnesi_patologica",length=4000)
	public String getAnamnesiPatologica(){
		return anamnesiPatologica;
	}

	public void setAnamnesiPatologica(String anamnesiPatologica){
		this.anamnesiPatologica = anamnesiPatologica;
	}

	/**
	*  javadoc for anamnesiFisiologica
	*/
	private String anamnesiFisiologica;

	@Column(name="anamnesi_fisiologica",length=2500)
	public String getAnamnesiFisiologica(){
		return anamnesiFisiologica;
	}

	public void setAnamnesiFisiologica(String anamnesiFisiologica){
		this.anamnesiFisiologica = anamnesiFisiologica;
	}


	/**
	*  javadoc for fattoreRischio
	*/
	private List<FattoreRischio> fattoreRischio;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="medicinaLavoro")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
				org.hibernate.annotations.CascadeType.DELETE,
				org.hibernate.annotations.CascadeType.DELETE_ORPHAN,
				org.hibernate.annotations.CascadeType.REFRESH})
	public List<FattoreRischio> getFattoreRischio() {
		return fattoreRischio;
	}

	public void setFattoreRischio(List<FattoreRischio>list){
		fattoreRischio = list;
	}

	public void addFattoreRischio(FattoreRischio fattoreRischio) {
		if (this.fattoreRischio == null) {
			this.fattoreRischio = new ArrayList<FattoreRischio>();
		}
		// add the association
		if(!this.fattoreRischio.contains(fattoreRischio)) {
			this.fattoreRischio.add(fattoreRischio);
			// make the inverse link
			fattoreRischio.setMedicinaLavoro(this);
		}
	}

	public void removeFattoreRischio(FattoreRischio fattoreRischio) {
		if (this.fattoreRischio == null) {
			this.fattoreRischio = new ArrayList<FattoreRischio>();
			return;
		}
		//add the association
		if(this.fattoreRischio.contains(fattoreRischio)){
			this.fattoreRischio.remove(fattoreRischio);
			//make the inverse link
			fattoreRischio.setMedicinaLavoro(null);
		}
	}



	/**
	*  javadoc for anamnesisMdl
	*/
	private List<AnamnesisMdl> anamnesisMdl;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="medicinaLavoro")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
				org.hibernate.annotations.CascadeType.DELETE,
				org.hibernate.annotations.CascadeType.DELETE_ORPHAN,
				org.hibernate.annotations.CascadeType.REFRESH})
	public List<AnamnesisMdl> getAnamnesisMdl() {
		return anamnesisMdl;
	}

	public void setAnamnesisMdl(List<AnamnesisMdl>list){
		anamnesisMdl = list;
	}

	public void addAnamnesisMdl(AnamnesisMdl anamnesisMdl) {
		if (this.anamnesisMdl == null) {
			this.anamnesisMdl = new ArrayList<AnamnesisMdl>();
		}
		// add the association
		if(!this.anamnesisMdl.contains(anamnesisMdl)) {
			this.anamnesisMdl.add(anamnesisMdl);
			// make the inverse link
			anamnesisMdl.setMedicinaLavoro(this);
		}
	}

	public void removeAnamnesisMdl(AnamnesisMdl anamnesisMdl) {
		if (this.anamnesisMdl == null) {
			this.anamnesisMdl = new ArrayList<AnamnesisMdl>();
			return;
		}
		//add the association
		if(this.anamnesisMdl.contains(anamnesisMdl)){
			this.anamnesisMdl.remove(anamnesisMdl);
			//make the inverse link
			anamnesisMdl.setMedicinaLavoro(null);
		}
	}


	/**
	*  javadoc for comparto
	*/
	private CodeValueAteco comparto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comparto")
	@ForeignKey(name="FK_Mdl_comparto")
	//@Index(name="IX_Mdl_comparto")
	public CodeValueAteco getComparto(){
		return comparto;
	}

	public void setComparto(CodeValueAteco comparto){
		this.comparto = comparto;
	}


	/**
	*  javadoc for sedeAttuale
	*/
	private Sedi sedeAttuale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_attuale_id")
	@ForeignKey(name="FK_Mdl_sedeAttual")
	//@Index(name="IX_Mdl_sedeAttual")
	public Sedi getSedeAttuale() {
		return sedeAttuale;
	}

	public void setSedeAttuale(Sedi sedeAttuale){
		this.sedeAttuale = sedeAttuale;
	}



	/**
	*  javadoc for dittaAttuale
	*/
	private PersoneGiuridiche dittaAttuale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_attuale_id")
	@ForeignKey(name="FK_Mdl_dttaAttual")
	//@Index(name="IX_Mdl_dttaAttual")
	public PersoneGiuridiche getDittaAttuale(){
		return dittaAttuale;
	}

	public void setDittaAttuale(PersoneGiuridiche dittaAttuale){
		this.dittaAttuale = dittaAttuale;
	}



	/**
	*  javadoc for patient
	*/
	private Person patient;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Mdl_person")
	//@Index(name="IX_Mdl_person")
	public Person getPatient(){
		return patient;
	}

	public void setPatient(Person patient){
		this.patient = patient;
	}


	/**
	*  javadoc for lavType
	*/
	private CodeValuePhi lavType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lavType")
	@ForeignKey(name="FK_Mdl_lavType")
	//@Index(name="IX_Mdl_lavType")
	public CodeValuePhi getLavType(){
		return lavType;
	}

	public void setLavType(CodeValuePhi lavType){
		this.lavType = lavType;
	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_Mdl_type")
	//@Index(name="IX_Mdl_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}


	/**
	*  javadoc for procpratiche
	*/
	private List<Procpratiche> procpratiche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="medicinaLavoro")
	public List<Procpratiche> getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(List<Procpratiche> list){
		procpratiche = list;
	}

	public void addProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
		}
		// add the association
		if(!this.procpratiche.contains(procpratiche)) {
			this.procpratiche.add(procpratiche);
			// make the inverse link
			procpratiche.setMedicinaLavoro(this);
		}
	}

	public void removeProcpratiche(Procpratiche procpratiche) {
		if (this.procpratiche == null) {
			this.procpratiche = new ArrayList<Procpratiche>();
			return;
		}
		//add the association
		if(this.procpratiche.contains(procpratiche)){
			this.procpratiche.remove(procpratiche);
			//make the inverse link
			procpratiche.setMedicinaLavoro(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MedicinaLavoro_sequence")
	@SequenceGenerator(name = "MedicinaLavoro_sequence", sequenceName = "MedicinaLavoro_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
