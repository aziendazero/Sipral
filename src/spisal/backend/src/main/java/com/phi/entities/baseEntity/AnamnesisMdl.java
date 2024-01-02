package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.phi.entities.dataTypes.CodeValueAteco;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "anamnesis_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AnamnesisMdl extends BaseEntity {

	private static final long serialVersionUID = 432711481L;

	/**
	*  javadoc for mansioneCode
	*/
	private CodeValuePhi mansioneCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="mansioneCode")
	@ForeignKey(name="FK_AnamnesisMdl_mansioneCode")
	//@Index(name="IX_AnamnesisMdl_mansioneCode")
	public CodeValuePhi getMansioneCode(){
		return mansioneCode;
	}

	public void setMansioneCode(CodeValuePhi mansioneCode){
		this.mansioneCode = mansioneCode;
	}

	/**
	*  javadoc for expAmi
	*/
	private Double expAmi;

	@Column(name="exp_ami")
	public Double getExpAmi(){
		return expAmi;
	}

	public void setExpAmi(Double expAmi){
		this.expAmi = expAmi;
	}

	/**
	*  javadoc for expCVM
	*/
	private Double expCVM;

	@Column(name="exp_cvm")
	public Double getExpCVM(){
		return expCVM;
	}

	public void setExpCVM(Double expCVM){
		this.expCVM = expCVM;
	}


	/**
	*  javadoc for fattoreRischio
	*/
	private List<FattoreRischio> fattoreRischio;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="anamnesisMdl")
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
			fattoreRischio.setAnamnesisMdl(this);
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
			fattoreRischio.setAnamnesisMdl(null);
		}
	}


	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note",length=2500)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
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
	*  javadoc for reparto
	*/
	private String reparto;

	@Column(name="reparto")
	public String getReparto(){
		return reparto;
	}

	public void setReparto(String reparto){
		this.reparto = reparto;
	}


	/**
	*  javadoc for sede
	*/
	private Sedi sede;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sede_id")
	@ForeignKey(name="FK_AnamnesisMdl_sede")
	//@Index(name="IX_AnamnesisMdl_sede")
	public Sedi getSede(){
		return sede;
	}

	public void setSede(Sedi sede){
		this.sede = sede;
	}



	/**
	*  javadoc for ditta
	*/
	private PersoneGiuridiche ditta;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ditta_id")
	@ForeignKey(name="FK_AnamnesisMdl_ditta")
	//@Index(name="IX_AnamnesisMdl_ditta")
	public PersoneGiuridiche getDitta(){
		return ditta;
	}

	public void setDitta(PersoneGiuridiche ditta){
		this.ditta = ditta;
	}

	/**
	*  javadoc for comparto
	*/
	private CodeValueAteco comparto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="comparto")
	@ForeignKey(name="FK_AnamnesisMdl_comparto")
	//@Index(name="IX_AnamnesisMdl_comparto")
	public CodeValueAteco getComparto(){
		return comparto;
	}

	public void setComparto(CodeValueAteco comparto){
		this.comparto = comparto;
	}

	/**
	*  javadoc for endValidity
	*/
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	*  javadoc for startValidity
	*/
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}


	/**
	*  javadoc for medicinaLavoro
	*/
	private MedicinaLavoro medicinaLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="medicina_lavoro_id")
	@ForeignKey(name="FK_namnesisMdl_medicinLvoro")
	//@Index(name="IX_namnesisMdl_medicinLvoro")
	public MedicinaLavoro getMedicinaLavoro(){
		return medicinaLavoro;
	}

	public void setMedicinaLavoro(MedicinaLavoro medicinaLavoro){
		this.medicinaLavoro = medicinaLavoro;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AnamnesisMdl_sequence")
	@SequenceGenerator(name = "AnamnesisMdl_sequence", sequenceName = "AnamnesisMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
