package com.phi.entities.baseEntity;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.actions.FattoreRischioAction;
import com.phi.entities.dataTypes.CodeValueLaw;
import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "fattori_rischio")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class FattoreRischio extends BaseEntity {

	private static final long serialVersionUID = 813990945L;

	/**
	*  javadoc for consumo
	*/
	private String consumo;

	@Column(name="consumo")
	public String getConsumo(){
		return consumo;
	}

	public void setConsumo(String consumo){
		this.consumo = consumo;
	}

	/**
	*  javadoc for legge81code
	*/
	private CodeValueLaw legge81code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="legge81code")
	@ForeignKey(name="FK_FattoreRischio_legge81code")
	//@Index(name="IX_FattoreRischio_legge81code")
	public CodeValueLaw getLegge81code(){
		return legge81code;
	}

	public void setLegge81code(CodeValueLaw legge81code){
		this.legge81code = legge81code;
	}



	/**
	*  javadoc for hhGg
	*/
	private Double hhGg;

	@Column(name="hh_gg")
	public Double getHhGg(){
		return hhGg;
	}

	public void setHhGg(Double hhGg){
		this.hhGg = hhGg;
	}

	/**
	*  javadoc for ggYear
	*/
	private Double ggYear;

	@Column(name="gg_year")
	public Double getGgYear(){
		return ggYear;
	}

	public void setGgYear(Double ggYear){
		this.ggYear = ggYear;
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
	*  javadoc for frequenza
	*/
	private Double frequenza;

	@Column(name="frequenza")
	public Double getFrequenza(){
		return frequenza;
	}

	public void setFrequenza(Double frequenza){
		this.frequenza = frequenza;
	}

	/**
	*  javadoc for coefficient
	*/
	private CodeValuePhi coefficient;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="coefficient")
	@ForeignKey(name="FK_FattoreRischio_coefficient")
	//@Index(name="IX_FattoreRischio_coefficient")
	public CodeValuePhi getCoefficient(){
		return coefficient;
	}

	public void setCoefficient(CodeValuePhi coefficient){
		this.coefficient = coefficient;
	}

	/**
	*  javadoc for sigRisk
	*/
	private Double sigRisk;

	@Column(name="sig_risk")
	public Double getSigRisk(){
		return sigRisk;
	}

	public void setSigRisk(Double sigRisk){
		this.sigRisk = sigRisk;
	}

	/**
	*  javadoc for sigarette
	*/
	private Integer sigarette;

	@Column(name="sigarette")
	public Integer getSigarette(){
		return sigarette;
	}

	public void setSigarette(Integer sigarette){
		this.sigarette = sigarette;
	}

	/**
	*  javadoc for multiplier
	*/
	private Double multiplier;

	@Column(name="multiplier")
	public Double getMultiplier(){
		return multiplier;
	}

	public void setMultiplier(Double multiplier){
		this.multiplier = multiplier;
	}

	/**
	*  javadoc for expType
	*/
	private CodeValuePhi expType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="expType")
	@ForeignKey(name="FK_FattoreRischio_expType")
	//@Index(name="IX_FattoreRischio_expType")
	public CodeValuePhi getExpType(){
		return expType;
	}

	public void setExpType(CodeValuePhi expType){
		this.expType = expType;
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
	@ForeignKey(name="FK_FttoreRischio_medicinLvr")
	//@Index(name="IX_FttoreRischio_medicinLvr")
	public MedicinaLavoro getMedicinaLavoro(){
		return medicinaLavoro;
	}

	public void setMedicinaLavoro(MedicinaLavoro medicinaLavoro){
		this.medicinaLavoro = medicinaLavoro;
	}



	/**
	*  javadoc for anamnesisMdl
	*/
	private AnamnesisMdl anamnesisMdl;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="anamnesis_mdl_id")
	@ForeignKey(name="FK_FttoreRischio_nmnesisMdl")
	//@Index(name="IX_FttoreRischio_nmnesisMdl")
	public AnamnesisMdl getAnamnesisMdl(){
		return anamnesisMdl;
	}

	public void setAnamnesisMdl(AnamnesisMdl anamnesisMdl){
		this.anamnesisMdl = anamnesisMdl;
	}


	/**
	*  javadoc for unitaPrefisso
	*/
	private CodeValuePhi unitaPrefisso;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="unitaPrefisso")
	@ForeignKey(name="FK_FattoreRischio_unitaPrefisso")
	//@Index(name="IX_FattoreRischio_unitaPrefisso")
	public CodeValuePhi getUnitaPrefisso(){
		return unitaPrefisso;
	}

	public void setUnitaPrefisso(CodeValuePhi unitaPrefisso){
		this.unitaPrefisso = unitaPrefisso;
	}

	/**
	*  javadoc for unitaMisura
	*/
	private CodeValuePhi unitaMisura;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="unitaMisura")
	@ForeignKey(name="FK_FattoreRischio_unitaMisura")
	//@Index(name="IX_FattoreRischio_unitaMisura")
	public CodeValuePhi getUnitaMisura(){
		return unitaMisura;
	}

	public void setUnitaMisura(CodeValuePhi unitaMisura){
		this.unitaMisura = unitaMisura;
	}

	/**
	*  javadoc for ext
	*/
	private CodeValuePhi ext;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ext")
	@ForeignKey(name="FK_FattoreRischio_ext")
	//@Index(name="IX_FattoreRischio_ext")
	public CodeValuePhi getExt(){
		return ext;
	}

	public void setExt(CodeValuePhi ext){
		this.ext = ext;
	}

	/**
	*  javadoc for intUnit
	*/
	private String intUnit;

	@Column(name="int_unit")
	public String getIntUnit(){
		return intUnit;
	}

	public void setIntUnit(String intUnit){
		this.intUnit = intUnit;
	}


	/**
	*  javadoc for malattiaProfessionale
	*/
	private MalattiaProfessionale malattiaProfessionale;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="malattia_professionale_id")
	@ForeignKey(name="FK_FttrRischi_mlttiPrfssinl")
	//@Index(name="IX_FttrRischi_mlttiPrfssinl")
	public MalattiaProfessionale getMalattiaProfessionale(){
		return malattiaProfessionale;
	}

	public void setMalattiaProfessionale(MalattiaProfessionale malattiaProfessionale){
		this.malattiaProfessionale = malattiaProfessionale;
	}


	/**
	*  javadoc for causa
	*/
	private CodeValuePhi causa;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="causa")
	@ForeignKey(name="FK_FattoreRischio_causa")
	//@Index(name="IX_FattoreRischio_causa")
	public CodeValuePhi getCausa(){
		return causa;
	}

	public void setCausa(CodeValuePhi causa){
		this.causa = causa;
	}

	/**
	*  javadoc for intensityQuant
	*/
	private Double intensityQuant;

	@Column(name="intensity_quant")
	public Double getIntensityQuant(){
		return intensityQuant;
	}

	public void setIntensityQuant(Double intensityQuant){
		this.intensityQuant = intensityQuant;
	}

	/**
	*  javadoc for intensityQual
	*/
	private CodeValuePhi intensityQual;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="intensityQual")
	@ForeignKey(name="FK_FattoreRischio_intensityQual")
	//@Index(name="IX_FattoreRischio_intensityQual")
	public CodeValuePhi getIntensityQual(){
		return intensityQual;
	}

	public void setIntensityQual(CodeValuePhi intensityQual){
		this.intensityQual = intensityQual;
	}

	/**
	*  javadoc for yearStop
	*/
	private Integer yearStop;

	@Column(name="year_stop")
	public Integer getYearStop(){
		return yearStop;
	}

	public void setYearStop(Integer yearStop){
		this.yearStop = yearStop;
	}

	/**
	*  javadoc for yearStart
	*/
	private Integer yearStart;

	@Column(name="year_start")
	public Integer getYearStart(){
		return yearStart;
	}

	public void setYearStart(Integer yearStart){
		this.yearStart = yearStart;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_FattoreRischio_code")
	//@Index(name="IX_FattoreRischio_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_FattoreRischio_type")
	//@Index(name="IX_FattoreRischio_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "FattoreRischio_sequence")
	@SequenceGenerator(name = "FattoreRischio_sequence", sequenceName = "FattoreRischio_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Transient
	public String getRischio(){
		
		FattoreRischioAction fra = new FattoreRischioAction();
		return fra.getRischio(this);
		
	}
}
