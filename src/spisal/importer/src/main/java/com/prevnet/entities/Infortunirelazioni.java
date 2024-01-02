package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the INFORTUNIRELAZIONI database table.
 * 
 */
@Entity
public class Infortunirelazioni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idinfortunio;
	private String attivitauo;
	private String chknessocasualita;
	private String conclusioni;
	private String considerazioni;
	private String descrizioneevento;
	private String descrizioneluoghi;
	private String elencoallegati;
	private String elencoviolazioni;
	private String infoindagine;
	private String infomodalitainfortunio;
	private String infomotivoviolazioni;
	private String infopresentiadinfortunio;
	private String optviolazioni;
	private String puntisanzionati;
	private InfortuniPrevnet infortuniPrevnet;

	public Infortunirelazioni() {
	}


	@Id
	public long getIdinfortunio() {
		return this.idinfortunio;
	}

	public void setIdinfortunio(long idinfortunio) {
		this.idinfortunio = idinfortunio;
	}


	public String getAttivitauo() {
		return this.attivitauo;
	}

	public void setAttivitauo(String attivitauo) {
		this.attivitauo = attivitauo;
	}


	public String getChknessocasualita() {
		return this.chknessocasualita;
	}

	public void setChknessocasualita(String chknessocasualita) {
		this.chknessocasualita = chknessocasualita;
	}


	@Lob
	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}


	@Lob
	public String getConsiderazioni() {
		return this.considerazioni;
	}

	public void setConsiderazioni(String considerazioni) {
		this.considerazioni = considerazioni;
	}


	@Lob
	public String getDescrizioneevento() {
		return this.descrizioneevento;
	}

	public void setDescrizioneevento(String descrizioneevento) {
		this.descrizioneevento = descrizioneevento;
	}


	public String getDescrizioneluoghi() {
		return this.descrizioneluoghi;
	}

	public void setDescrizioneluoghi(String descrizioneluoghi) {
		this.descrizioneluoghi = descrizioneluoghi;
	}


	public String getElencoallegati() {
		return this.elencoallegati;
	}

	public void setElencoallegati(String elencoallegati) {
		this.elencoallegati = elencoallegati;
	}


	public String getElencoviolazioni() {
		return this.elencoviolazioni;
	}

	public void setElencoviolazioni(String elencoviolazioni) {
		this.elencoviolazioni = elencoviolazioni;
	}


	public String getInfoindagine() {
		return this.infoindagine;
	}

	public void setInfoindagine(String infoindagine) {
		this.infoindagine = infoindagine;
	}


	public String getInfomodalitainfortunio() {
		return this.infomodalitainfortunio;
	}

	public void setInfomodalitainfortunio(String infomodalitainfortunio) {
		this.infomodalitainfortunio = infomodalitainfortunio;
	}


	public String getInfomotivoviolazioni() {
		return this.infomotivoviolazioni;
	}

	public void setInfomotivoviolazioni(String infomotivoviolazioni) {
		this.infomotivoviolazioni = infomotivoviolazioni;
	}


	public String getInfopresentiadinfortunio() {
		return this.infopresentiadinfortunio;
	}

	public void setInfopresentiadinfortunio(String infopresentiadinfortunio) {
		this.infopresentiadinfortunio = infopresentiadinfortunio;
	}


	public String getOptviolazioni() {
		return this.optviolazioni;
	}

	public void setOptviolazioni(String optviolazioni) {
		this.optviolazioni = optviolazioni;
	}


	public String getPuntisanzionati() {
		return this.puntisanzionati;
	}

	public void setPuntisanzionati(String puntisanzionati) {
		this.puntisanzionati = puntisanzionati;
	}


	//bi-directional one-to-one association to Infortuni
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDINFORTUNIO")
	public InfortuniPrevnet getInfortuniPrevnet() {
		return this.infortuniPrevnet;
	}

	public void setInfortuniPrevnet(InfortuniPrevnet infortuniPrevnet) {
		this.infortuniPrevnet = infortuniPrevnet;
	}

}