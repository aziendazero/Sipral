package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the mansione database table.
 * 
 */
@Entity
@Table(name="mansione")
@NamedQuery(name="Mansione.findAll", query="SELECT m FROM Mansione m")
public class Mansione implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer ariaCompressa;
	private Integer condizioneDelMateriale;
	private Integer confinamentoAmbientale;
	private Integer confinamentoDelLavoro;
	private Integer confinamentoMateriale;
	private Integer distanzaDallaSorgente;
	private Integer disturboMeccanico;
	private Integer dpi;
	private Integer friabilita;
	private String mansione;
	private Integer materialeEPc;
	private Integer operazioni;
	private Integer percezionePolverosita;
	private Integer puliziaPostoLavoro;
	private Integer recinzioneZona;
	private Integer tipoFibra;
	private Integer velocitaRilascioPolvere;
	private Integer vicinanzaSorgente;

	public Mansione() {
	}


	@Column(name="aria_compressa")
	public Integer getAriaCompressa() {
		return this.ariaCompressa;
	}

	public void setAriaCompressa(Integer ariaCompressa) {
		this.ariaCompressa = ariaCompressa;
	}


	@Column(name="condizione_del_materiale")
	public Integer getCondizioneDelMateriale() {
		return this.condizioneDelMateriale;
	}

	public void setCondizioneDelMateriale(Integer condizioneDelMateriale) {
		this.condizioneDelMateriale = condizioneDelMateriale;
	}


	@Column(name="confinamento_ambientale")
	public Integer getConfinamentoAmbientale() {
		return this.confinamentoAmbientale;
	}

	public void setConfinamentoAmbientale(Integer confinamentoAmbientale) {
		this.confinamentoAmbientale = confinamentoAmbientale;
	}


	@Column(name="confinamento_del_lavoro")
	public Integer getConfinamentoDelLavoro() {
		return this.confinamentoDelLavoro;
	}

	public void setConfinamentoDelLavoro(Integer confinamentoDelLavoro) {
		this.confinamentoDelLavoro = confinamentoDelLavoro;
	}


	@Column(name="confinamento_materiale")
	public Integer getConfinamentoMateriale() {
		return this.confinamentoMateriale;
	}

	public void setConfinamentoMateriale(Integer confinamentoMateriale) {
		this.confinamentoMateriale = confinamentoMateriale;
	}


	@Column(name="distanza_dalla_sorgente")
	public Integer getDistanzaDallaSorgente() {
		return this.distanzaDallaSorgente;
	}

	public void setDistanzaDallaSorgente(Integer distanzaDallaSorgente) {
		this.distanzaDallaSorgente = distanzaDallaSorgente;
	}


	@Column(name="disturbo_meccanico")
	public Integer getDisturboMeccanico() {
		return this.disturboMeccanico;
	}

	public void setDisturboMeccanico(Integer disturboMeccanico) {
		this.disturboMeccanico = disturboMeccanico;
	}


	public Integer getDpi() {
		return this.dpi;
	}

	public void setDpi(Integer dpi) {
		this.dpi = dpi;
	}


	public Integer getFriabilita() {
		return this.friabilita;
	}

	public void setFriabilita(Integer friabilita) {
		this.friabilita = friabilita;
	}


	@Id
	@Column(length=50)
	public String getMansione() {
		return this.mansione;
	}

	public void setMansione(String mansione) {
		this.mansione = mansione;
	}


	@Column(name="materiale_e_pc")
	public Integer getMaterialeEPc() {
		return this.materialeEPc;
	}

	public void setMaterialeEPc(Integer materialeEPc) {
		this.materialeEPc = materialeEPc;
	}


	public Integer getOperazioni() {
		return this.operazioni;
	}

	public void setOperazioni(Integer operazioni) {
		this.operazioni = operazioni;
	}


	@Column(name="percezione_polverosita")
	public Integer getPercezionePolverosita() {
		return this.percezionePolverosita;
	}

	public void setPercezionePolverosita(Integer percezionePolverosita) {
		this.percezionePolverosita = percezionePolverosita;
	}


	@Column(name="pulizia_posto_lavoro")
	public Integer getPuliziaPostoLavoro() {
		return this.puliziaPostoLavoro;
	}

	public void setPuliziaPostoLavoro(Integer puliziaPostoLavoro) {
		this.puliziaPostoLavoro = puliziaPostoLavoro;
	}


	@Column(name="recinzione_zona")
	public Integer getRecinzioneZona() {
		return this.recinzioneZona;
	}

	public void setRecinzioneZona(Integer recinzioneZona) {
		this.recinzioneZona = recinzioneZona;
	}


	@Column(name="tipo_fibra")
	public Integer getTipoFibra() {
		return this.tipoFibra;
	}

	public void setTipoFibra(Integer tipoFibra) {
		this.tipoFibra = tipoFibra;
	}


	@Column(name="velocita_rilascio_polvere")
	public Integer getVelocitaRilascioPolvere() {
		return this.velocitaRilascioPolvere;
	}

	public void setVelocitaRilascioPolvere(Integer velocitaRilascioPolvere) {
		this.velocitaRilascioPolvere = velocitaRilascioPolvere;
	}


	@Column(name="vicinanza_sorgente")
	public Integer getVicinanzaSorgente() {
		return this.vicinanzaSorgente;
	}

	public void setVicinanzaSorgente(Integer vicinanzaSorgente) {
		this.vicinanzaSorgente = vicinanzaSorgente;
	}

}