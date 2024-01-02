package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@Table(name = "costo_nomina")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CostoNomina extends BaseEntity {

	private static final long serialVersionUID = 1276097737L;

	/**
	*  javadoc for nomina
	*/
	private String nomina;

	@Column(name="nomina")
	public String getNomina(){
		return nomina;
	}

	public void setNomina(String nomina){
		this.nomina = nomina;
	}

	/**
	*  javadoc for indirettoRfpMed
	*/
	private Double indirettoRfpMed;

	@Column(name="indiretto_rfp_med")
	public Double getIndirettoRfpMed(){
		return this.round(indirettoRfpMed, 2);
	}

	public void setIndirettoRfpMed(Double indirettoRfpMed){
		this.indirettoRfpMed = indirettoRfpMed;
	}

	/**
	*  javadoc for indirettoRdpMed
	*/
	private Double indirettoRdpMed;

	@Column(name="indiretto_rdp_med")
	public Double getIndirettoRdpMed(){
		return this.round(indirettoRdpMed, 2);
	}

	public void setIndirettoRdpMed(Double indirettoRdpMed){
		this.indirettoRdpMed = indirettoRdpMed;
	}

	/**
	*  javadoc for direttoMed
	*/
	private Double direttoMed;

	@Column(name="diretto_med")
	public Double getDirettoMed(){
		return this.round(direttoMed, 2);
	}

	public void setDirettoMed(Double direttoMed){
		this.direttoMed = direttoMed;
	}
	
	/**
	*  javadoc for indirettoRfpMed
	*/
	private Double indirettoRfpMedMod;

	@Column(name="indiretto_rfp_medmod")
	public Double getIndirettoRfpMedMod(){
		return this.round(indirettoRfpMedMod, 2);
	}

	public void setIndirettoRfpMedMod(Double indirettoRfpMedMod){
		this.indirettoRfpMedMod = indirettoRfpMedMod;
	}

	/**
	*  javadoc for indirettoRdpMed
	*/
	private Double indirettoRdpMedMod;

	@Column(name="indiretto_rdp_medmod")
	public Double getIndirettoRdpMedMod(){
		return this.round(indirettoRdpMedMod, 2);
	}

	public void setIndirettoRdpMedMod(Double indirettoRdpMedMod){
		this.indirettoRdpMedMod = indirettoRdpMedMod;
	}

	/**
	*  javadoc for direttoMed
	*/
	private Double direttoMedMod;

	@Column(name="diretto_medmod")
	public Double getDirettoMedMod(){
		return this.round(direttoMedMod, 2);
	}

	public void setDirettoMedMod(Double direttoMedMod){
		this.direttoMedMod = direttoMedMod;
	}

	/**
	*  javadoc for direttoTot
	*/
	private Double direttoTot;

	@Column(name="diretto_tot")
	public Double getDirettoTot(){
		return this.round(direttoTot, 2);
	}

	public void setDirettoTot(Double direttoTot){
		this.direttoTot = direttoTot;
	}

	/**
	*  javadoc for indirettoRfpTot
	*/
	private Double indirettoRfpTot;

	@Column(name="indiretto_rfp_tot")
	public Double getIndirettoRfpTot(){
		return this.round(indirettoRfpTot, 2);
	}

	public void setIndirettoRfpTot(Double indirettoRfpTot){
		this.indirettoRfpTot = indirettoRfpTot;
	}

	/**
	*  javadoc for indirettoRdpTot
	*/
	private Double indirettoRdpTot;

	@Column(name="indiretto_rdp_tot")
	public Double getIndirettoRdpTot(){
		return this.round(indirettoRdpTot, 2);
	}

	public void setIndirettoRdpTot(Double indirettoRdpTot){
		this.indirettoRdpTot = indirettoRdpTot;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CostoNomina_sequence")
	@SequenceGenerator(name = "CostoNomina_sequence", sequenceName = "CostoNomina_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private Double round(Double value, int places) {
	    if (value ==null)
	    	return value;
	    
		Double scale = Math.pow(10, places);
	    return Math.round(value * scale) / scale;
	}

}
