package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the SO_IMPATTO_ANAGRAFICA database table.
 * 
 */
@Entity
@Table(name="SO_IMPATTO_ANAGRAFICA2")
public class SoImpattoAnagrafica implements Serializable {
	private static final long serialVersionUID = 1L;
	private String datavaliditarecord;
	private String idregionale;
	private String cognome;
	private String nome;
	private String sesso;
	private String codicefiscale;
	private String tesserasanitaria;
	private String codicestp;
	private String codiceeni;
	private String tesserateam;
	private String datanascita;
	private String comunenascita;
	private String cittadinanza;
	private String datadecesso;
	private String comuneresidenza;
	private String indirizzoresidenza;
	private String numerocivicoresidenza;
	private String capresidenza;
	private String comunedomicilio;
	private String indirizzodomicilio;
	private String numerocivicodomicilio;
	private String capdomicilio;
	private String telefono1;
	private String telefono2;
	private String telefono3;
	private String descrizionetelefono1;
	private String descrizionetelefono2;
	private String descrizionetelefono3;
	private String indirizzoemail;
	private String ulssassistenza;

	public SoImpattoAnagrafica() {
	}

	public String getDatavaliditarecord() {
		return datavaliditarecord;
	}

	public void setDatavaliditarecord(String datavaliditarecord) {
		this.datavaliditarecord = datavaliditarecord;
	}

	@Id
	public String getIdregionale() {
		return idregionale;
	}

	public void setIdregionale(String idregionale) {
		this.idregionale = idregionale;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getCodicefiscale() {
		return codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}

	public String getTesserasanitaria() {
		return tesserasanitaria;
	}

	public void setTesserasanitaria(String tesserasanitaria) {
		this.tesserasanitaria = tesserasanitaria;
	}

	public String getCodicestp() {
		return codicestp;
	}

	public void setCodicestp(String codicestp) {
		this.codicestp = codicestp;
	}

	public String getCodiceeni() {
		return codiceeni;
	}

	public void setCodiceeni(String codiceeni) {
		this.codiceeni = codiceeni;
	}

	public String getTesserateam() {
		return tesserateam;
	}

	public void setTesserateam(String tesserateam) {
		this.tesserateam = tesserateam;
	}

	public String getDatanascita() {
		return datanascita;
	}

	public void setDatanascita(String datanascita) {
		this.datanascita = datanascita;
	}

	public String getComunenascita() {
		return comunenascita;
	}

	public void setComunenascita(String comunenascita) {
		this.comunenascita = comunenascita;
	}

	public String getCittadinanza() {
		return cittadinanza;
	}

	public void setCittadinanza(String cittadinanza) {
		this.cittadinanza = cittadinanza;
	}

	public String getDatadecesso() {
		return datadecesso;
	}

	public void setDatadecesso(String datadecesso) {
		this.datadecesso = datadecesso;
	}

	public String getComuneresidenza() {
		return comuneresidenza;
	}

	public void setComuneresidenza(String comuneresidenza) {
		this.comuneresidenza = comuneresidenza;
	}

	public String getIndirizzoresidenza() {
		return indirizzoresidenza;
	}

	public void setIndirizzoresidenza(String indirizzoresidenza) {
		this.indirizzoresidenza = indirizzoresidenza;
	}

	public String getNumerocivicoresidenza() {
		return numerocivicoresidenza;
	}

	public void setNumerocivicoresidenza(String numerocivicoresidenza) {
		this.numerocivicoresidenza = numerocivicoresidenza;
	}

	public String getCapresidenza() {
		return capresidenza;
	}

	public void setCapresidenza(String capresidenza) {
		this.capresidenza = capresidenza;
	}

	public String getComunedomicilio() {
		return comunedomicilio;
	}

	public void setComunedomicilio(String comunedomicilio) {
		this.comunedomicilio = comunedomicilio;
	}

	public String getIndirizzodomicilio() {
		return indirizzodomicilio;
	}

	public void setIndirizzodomicilio(String indirizzodomicilio) {
		this.indirizzodomicilio = indirizzodomicilio;
	}

	public String getNumerocivicodomicilio() {
		return numerocivicodomicilio;
	}

	public void setNumerocivicodomicilio(String numerocivicodomicilio) {
		this.numerocivicodomicilio = numerocivicodomicilio;
	}

	public String getCapdomicilio() {
		return capdomicilio;
	}

	public void setCapdomicilio(String capdomicilio) {
		this.capdomicilio = capdomicilio;
	}

	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	public String getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	public String getTelefono3() {
		return telefono3;
	}

	public void setTelefono3(String telefono3) {
		this.telefono3 = telefono3;
	}

	public String getDescrizionetelefono1() {
		return descrizionetelefono1;
	}

	public void setDescrizionetelefono1(String descrizionetelefono1) {
		this.descrizionetelefono1 = descrizionetelefono1;
	}

	public String getDescrizionetelefono2() {
		return descrizionetelefono2;
	}

	public void setDescrizionetelefono2(String descrizionetelefono2) {
		this.descrizionetelefono2 = descrizionetelefono2;
	}

	public String getDescrizionetelefono3() {
		return descrizionetelefono3;
	}

	public void setDescrizionetelefono3(String descrizionetelefono3) {
		this.descrizionetelefono3 = descrizionetelefono3;
	}

	public String getIndirizzoemail() {
		return indirizzoemail;
	}

	public void setIndirizzoemail(String indirizzoemail) {
		this.indirizzoemail = indirizzoemail;
	}

	public String getUlssassistenza() {
		return ulssassistenza;
	}

	public void setUlssassistenza(String ulssassistenza) {
		this.ulssassistenza = ulssassistenza;
	}
	
}