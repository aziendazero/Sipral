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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Physician;
@javax.persistence.Entity
@Table(name = "visite_mediche")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VisitaMedica extends BaseEntity {

	private static final long serialVersionUID = 814670375L;


	/**
	*  javadoc for prestazioniReg
	*/
	private List<PrestazioniReg> prestazioniReg;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaMedica", cascade=CascadeType.PERSIST)
	public List<PrestazioniReg> getPrestazioniReg() {
		return prestazioniReg;
	}

	public void setPrestazioniReg(List<PrestazioniReg>list){
		prestazioniReg = list;
	}

	public void addPrestazioniReg(PrestazioniReg prestazioniReg) {
		if (this.prestazioniReg == null) {
			this.prestazioniReg = new ArrayList<PrestazioniReg>();
		}
		// add the association
		if(!this.prestazioniReg.contains(prestazioniReg)) {
			this.prestazioniReg.add(prestazioniReg);
			// make the inverse link
			prestazioniReg.setVisitaMedica(this);
		}
	}

	public void removePrestazioniReg(PrestazioniReg prestazioniReg) {
		if (this.prestazioniReg == null) {
			this.prestazioniReg = new ArrayList<PrestazioniReg>();
			return;
		}
		//add the association
		if(this.prestazioniReg.contains(prestazioniReg)){
			this.prestazioniReg.remove(prestazioniReg);
			//make the inverse link
			prestazioniReg.setVisitaMedica(null);
		}
	}


	
	/**
	*  javadoc for prestazioniICD9
	*/
	private List<CodeValueIcd9> prestazioniICD9 = new ArrayList<CodeValueIcd9>() ;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="visitavedica_prest_icd9", joinColumns = { @JoinColumn(name="visitamedica_id") }, inverseJoinColumns = { @JoinColumn(name="prest_icd9") })
	@ForeignKey(name="FK_prest_icd9_visitamedica", inverseName="FK_visitamedica_prest_icd9")
	@IndexColumn(name="list_index")
	public List<CodeValueIcd9> getPrestazioniICD9(){
		return prestazioniICD9;
	}

	public void setPrestazioniICD9(List<CodeValueIcd9> prestazioniICD9){
		this.prestazioniICD9 = prestazioniICD9;
	}

	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note")
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	/**
	*  javadoc for sintomi
	*/
	private String sintomi;

	@Column(name="sintomi")
	public String getSintomi(){
		return sintomi;
	}

	public void setSintomi(String sintomi){
		this.sintomi = sintomi;
	}

	/**
	*  javadoc for esito
	*/
	private String esito;

	@Column(name="esito")
	public String getEsito(){
		return esito;
	}

	public void setEsito(String esito){
		this.esito = esito;
	}

	/**
	*  javadoc for anamnesi
	*/
	private String anamnesi;

	@Column(name="anamnesi")
	public String getAnamnesi(){
		return anamnesi;
	}

	public void setAnamnesi(String anamnesi){
		this.anamnesi = anamnesi;
	}
	
	/**
	*  javadoc for anamnesi
	*/
	private String motivo;

	@Column(name="motivo")
	public String getMotivo(){
		return motivo;
	}

	public void setMotivo(String motivo){
		this.motivo = motivo;
	}

	/**
	*  javadoc for attivita
	*/
	private List<Attivita> attivita;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaMedica", cascade=CascadeType.PERSIST)
	public List<Attivita> getAttivita(){
		return attivita;
	}

	public void setAttivita(List<Attivita> list){
		attivita = list;
	}

	public void addAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
		}
		// add the association
		if(!this.attivita.contains(attivita)) {
			this.attivita.add(attivita);
			// make the inverse link
			attivita.setVisitaMedica(this);
		}
	}

	public void removeAttivita(Attivita attivita) {
		if (this.attivita == null) {
			this.attivita = new ArrayList<Attivita>();
			return;
		}
		//add the association
		if(this.attivita.contains(attivita)){
			this.attivita.remove(attivita);
			//make the inverse link
			attivita.setVisitaMedica(null);
		}

	}



	/**
	*  javadoc for sintomo
	*/
	private List<Sintomo> sintomo;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="visitaMedica", cascade=CascadeType.PERSIST)
	public List<Sintomo> getSintomo() {
		return sintomo;
	}

	public void setSintomo(List<Sintomo>list){
		sintomo = list;
	}

	public void addSintomo(Sintomo sintomo) {
		if (this.sintomo == null) {
			this.sintomo = new ArrayList<Sintomo>();
		}
		// add the association
		if(!this.sintomo.contains(sintomo)) {
			this.sintomo.add(sintomo);
			// make the inverse link
			sintomo.setVisitaMedica(this);
		}
	}

	public void removeSintomo(Sintomo sintomo) {
		if (this.sintomo == null) {
			this.sintomo = new ArrayList<Sintomo>();
			return;
		}
		//add the association
		if(this.sintomo.contains(sintomo)){
			this.sintomo.remove(sintomo);
			//make the inverse link
			sintomo.setVisitaMedica(null);
		}
	}


	/**
	*  javadoc for anamRem
	*/
	private String anamRem;

	@Column(name="anam_rem", length=2500)
	public String getAnamRem(){
		return anamRem;
	}

	public void setAnamRem(String anamRem){
		this.anamRem = anamRem;
	}

	/**
	*  javadoc for anamProx
	*/
	private String anamProx;

	@Column(name="anam_prox", length=2500)
	public String getAnamProx(){
		return anamProx;
	}

	public void setAnamProx(String anamProx){
		this.anamProx = anamProx;
	}

	/**
	*  javadoc for anamFisio
	*/
	private String anamFisio;

	@Column(name="anam_fisio", length=2500)
	public String getAnamFisio(){
		return anamFisio;
	}

	public void setAnamFisio(String anamFisio){
		this.anamFisio = anamFisio;
	}

	/**
	*  javadoc for anamFam
	*/
	private String anamFam;

	@Column(name="anam_fam", length=2500)
	public String getAnamFam(){
		return anamFam;
	}

	public void setAnamFam(String anamFam){
		this.anamFam = anamFam;
	}

	/**
	*  javadoc for prestazioni
	*/
	private List<CodeValuePhi> prestazioni;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="visitamedica_prestazioni", joinColumns = { @JoinColumn(name="VisitaMedica_id") }, inverseJoinColumns = { @JoinColumn(name="prestazioni") })
	@ForeignKey(name="FK_prestazioni_VisitaMedica", inverseName="FK_VisitaMedica_prestazioni")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getPrestazioni(){
		return prestazioni;
	}

	public void setPrestazioni(List<CodeValuePhi> prestazioni){
		this.prestazioni = prestazioni;
	}


	/**
	*  javadoc for medico
	*/
	private Physician medico;

	@NotAudited
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="medico_id")
	@ForeignKey(name="FK_VisitaMedica_medico")
	//@Index(name="IX_VisitaMedica_medico")
	public Physician getMedico(){
		return medico;
	}

	public void setMedico(Physician medico){
		this.medico = medico;
	}


	/**
	*  javadoc for data
	*/
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}


	/**
	*  javadoc for malattiaProfessionale
	*/
	private MalattiaProfessionale malattiaProfessionale;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="malattia_professionale_id")
	@ForeignKey(name="FK_VstaMedc_mlttProfessonle")
	//@Index(name="IX_VstaMedc_mlttProfessonle")
	public MalattiaProfessionale getMalattiaProfessionale(){
		return malattiaProfessionale;
	}

	public void setMalattiaProfessionale(MalattiaProfessionale malattiaProfessionale){
		this.malattiaProfessionale = malattiaProfessionale;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VisitaMedica_sequence")
	@SequenceGenerator(name = "VisitaMedica_sequence", sequenceName = "VisitaMedica_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
