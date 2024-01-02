package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


/**
 * The persistent class for the visita_medica database table.
 * 
 */
@Entity
@Table(name="visita_medica")
@AttributeOverrides({
	@AttributeOverride(name="referto", column=@Column(name="referto_1")),
	@AttributeOverride(name="lesione", column=@Column(name="lesione_1")),
	@AttributeOverride(name="numero",  column=@Column(name="numero_1")),
	@AttributeOverride(name="diametro", column=@Column(name="diametro_1")),
	@AttributeOverride(name="folow_upMotivo", column=@Column(name="`folow-up_motivo`"))
})
public class PrimaVisita extends VisitaGenerica implements Serializable {
	private static final long serialVersionUID = 1L;
	private String altezza;
	private byte antiinfluenzale;
	private Date data;
	private String dlco;
	private String esposizioneAccidentaleARxORadionuclidi;
	private Integer etaFineFumo;
	private Integer etaInizioFumo;
	private String febbre;
	private String malattieInfettiveUltimi5Anni;
	private String malattieProfessionali;
	private String motivoRadioterapia;
	private byte n1AnnoFa;
	private byte n2AnniFa;
	private byte n3AnniFa;
	private byte n4AnniFa;
	private byte n5AnniFa;
	private Integer nRadioterapia;
	private Integer nRx;
	private Integer nSigaretteGiorno;
	private String periodoRadioterapia;
	private String peso;
	private Integer radioterapia;
	private Integer rxLavoroScuola;
	private String sedeRadioterapia;
	private Integer tbc;
	private String vaccinazioneTbc;
	public PrimaVisita() {
	}


	@Column(length=50)
	public String getAltezza() {
		return this.altezza;
	}

	public void setAltezza(String altezza) {
		this.altezza = altezza;
	}


	public byte getAntiinfluenzale() {
		return this.antiinfluenzale;
	}

	public void setAntiinfluenzale(byte antiinfluenzale) {
		this.antiinfluenzale = antiinfluenzale;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(length=250)
	public String getDlco() {
		return this.dlco;
	}

	public void setDlco(String dlco) {
		this.dlco = dlco;
	}

	@Column(name="esposizione_accidentale_a_rx_o_radionuclidi", length=50)
	public String getEsposizioneAccidentaleARxORadionuclidi() {
		return this.esposizioneAccidentaleARxORadionuclidi;
	}

	public void setEsposizioneAccidentaleARxORadionuclidi(String esposizioneAccidentaleARxORadionuclidi) {
		this.esposizioneAccidentaleARxORadionuclidi = esposizioneAccidentaleARxORadionuclidi;
	}


	@Column(name="eta_fine_fumo")
	public Integer getEtaFineFumo() {
		return this.etaFineFumo;
	}

	public void setEtaFineFumo(Integer etaFineFumo) {
		this.etaFineFumo = etaFineFumo;
	}


	@Column(name="eta_inizio_fumo")
	public Integer getEtaInizioFumo() {
		return this.etaInizioFumo;
	}

	public void setEtaInizioFumo(Integer etaInizioFumo) {
		this.etaInizioFumo = etaInizioFumo;
	}


	@Column(length=50)
	public String getFebbre() {
		return this.febbre;
	}

	public void setFebbre(String febbre) {
		this.febbre = febbre;
	}

	@Column(name="malattie_infettive_ultimi_5_anni", length=250)
	public String getMalattieInfettiveUltimi5Anni() {
		return this.malattieInfettiveUltimi5Anni;
	}

	public void setMalattieInfettiveUltimi5Anni(String malattieInfettiveUltimi5Anni) {
		this.malattieInfettiveUltimi5Anni = malattieInfettiveUltimi5Anni;
	}


	@Column(name="malattie_professionali", length=50)
	public String getMalattieProfessionali() {
		return this.malattieProfessionali;
	}

	public void setMalattieProfessionali(String malattieProfessionali) {
		this.malattieProfessionali = malattieProfessionali;
	}


	@Column(name="motivo_radioterapia", length=50)
	public String getMotivoRadioterapia() {
		return this.motivoRadioterapia;
	}

	public void setMotivoRadioterapia(String motivoRadioterapia) {
		this.motivoRadioterapia = motivoRadioterapia;
	}


	@Column(name="n_1_anno_fa")
	public byte getN1AnnoFa() {
		return this.n1AnnoFa;
	}

	public void setN1AnnoFa(byte n1AnnoFa) {
		this.n1AnnoFa = n1AnnoFa;
	}


	@Column(name="n_2_anni_fa")
	public byte getN2AnniFa() {
		return this.n2AnniFa;
	}

	public void setN2AnniFa(byte n2AnniFa) {
		this.n2AnniFa = n2AnniFa;
	}


	@Column(name="n_3_anni_fa")
	public byte getN3AnniFa() {
		return this.n3AnniFa;
	}

	public void setN3AnniFa(byte n3AnniFa) {
		this.n3AnniFa = n3AnniFa;
	}


	@Column(name="n_4_anni_fa")
	public byte getN4AnniFa() {
		return this.n4AnniFa;
	}

	public void setN4AnniFa(byte n4AnniFa) {
		this.n4AnniFa = n4AnniFa;
	}


	@Column(name="n_5_anni_fa")
	public byte getN5AnniFa() {
		return this.n5AnniFa;
	}

	public void setN5AnniFa(byte n5AnniFa) {
		this.n5AnniFa = n5AnniFa;
	}

	@Column(name="n_radioterapia")
	public Integer getNRadioterapia() {
		return this.nRadioterapia;
	}

	public void setNRadioterapia(Integer nRadioterapia) {
		this.nRadioterapia = nRadioterapia;
	}


	@Column(name="n_rx")
	public Integer getNRx() {
		return this.nRx;
	}

	public void setNRx(Integer nRx) {
		this.nRx = nRx;
	}


	@Column(name="n_sigarette_giorno")
	public Integer getNSigaretteGiorno() {
		return this.nSigaretteGiorno;
	}

	public void setNSigaretteGiorno(Integer nSigaretteGiorno) {
		this.nSigaretteGiorno = nSigaretteGiorno;
	}

	
	@Column(name="periodo_radioterapia", length=50)
	public String getPeriodoRadioterapia() {
		return this.periodoRadioterapia;
	}

	public void setPeriodoRadioterapia(String periodoRadioterapia) {
		this.periodoRadioterapia = periodoRadioterapia;
	}


	@Column(length=50)
	public String getPeso() {
		return this.peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public Integer getRadioterapia() {
		return this.radioterapia;
	}

	public void setRadioterapia(Integer radioterapia) {
		this.radioterapia = radioterapia;
	}


	@Column(name="rx_lavoro_scuola")
	public Integer getRxLavoroScuola() {
		return this.rxLavoroScuola;
	}

	public void setRxLavoroScuola(Integer rxLavoroScuola) {
		this.rxLavoroScuola = rxLavoroScuola;
	}


	@Column(name="sede_radioterapia", length=50)
	public String getSedeRadioterapia() {
		return this.sedeRadioterapia;
	}

	public void setSedeRadioterapia(String sedeRadioterapia) {
		this.sedeRadioterapia = sedeRadioterapia;
	}

	
	public Integer getTbc() {
		return this.tbc;
	}

	public void setTbc(Integer tbc) {
		this.tbc = tbc;
	}


	@Column(name="vaccinazione_tbc", length=50)
	public String getVaccinazioneTbc() {
		return this.vaccinazioneTbc;
	}

	public void setVaccinazioneTbc(String vaccinazioneTbc) {
		this.vaccinazioneTbc = vaccinazioneTbc;
	}


	@Transient
	public String getEvoluzione() {
		return this.evoluzione;
	}

	@Transient
	public void setEvoluzione(String evoluzione2) {
		this.evoluzione = evoluzione2;
	}
	
	@Transient
	public String getVisitaMedica() {
		return this.visitaMedica;
	}

	@Transient
	public void setVisitaMedica(String secondaVisita) {
		this.visitaMedica = secondaVisita;
	}

}