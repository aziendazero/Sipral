package com.phi.parix.json.detail;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"C_RUOLO",
	"DESCRIZIONE",
	"ULT_DESCRIZIONE",
	"NUMERO",
	"DT_ISCRIZIONE"
})
public class AltroRuoloLoc {
	@JsonProperty("C_RUOLO")
	private String cruolo;
	@JsonProperty("DESCRIZIONE")
	private String descrizione;
	@JsonProperty("ULT_DESCRIZIONE")
	private String ultDescrizione;
	@JsonProperty("NUMERO")
	private String numero;
	@JsonProperty("DT_ISCRIZIONE")
	private String dtIscrizione;
	@JsonProperty("ALTRO_RUOLO_NON_CCIAA")
	private AltroRuoloNonCCIAA altroRuoloNonCCIAA;
	@JsonProperty("ALTRO_RUOLO_CCIAA")
	private AltroRuoloCCIAA altroRuoloCCIAA;
	
	@JsonProperty("C_RUOLO")
	public String getCruolo() {
		return cruolo;
	}
	@JsonProperty("C_RUOLO")
	public void setCruolo(String cruolo) {
		this.cruolo = cruolo;
	}
	@JsonProperty("DESCRIZIONE")
	public String getDescrizione() {
		return descrizione;
	}
	@JsonProperty("DESCRIZIONE")
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	@JsonProperty("ULT_DESCRIZIONE")
	public String getUltDescrizione() {
		return ultDescrizione;
	}
	@JsonProperty("ULT_DESCRIZIONE")
	public void setUltDescrizione(String ultDescrizione) {
		this.ultDescrizione = ultDescrizione;
	}
	@JsonProperty("NUMERO")
	public String getNumero() {
		return numero;
	}
	@JsonProperty("NUMERO")
	public void setNumero(String numero) {
		this.numero = numero;
	}
	@JsonProperty("DT_ISCRIZIONE")
	public String getDtIscrizione() {
		return dtIscrizione;
	}
	@JsonProperty("DT_ISCRIZIONE")
	public void setDtIscrizione(String dtIscrizione) {
		this.dtIscrizione = dtIscrizione;
	}
	@JsonProperty("ALTRO_RUOLO_NON_CCIAA")
	public AltroRuoloNonCCIAA getAltroRuoloNonCCIAA() {
		return altroRuoloNonCCIAA;
	}
	@JsonProperty("ALTRO_RUOLO_NON_CCIAA")
	public void setAltroRuoloNonCCIAA(AltroRuoloNonCCIAA altroRuoloNonCCIAA) {
		this.altroRuoloNonCCIAA = altroRuoloNonCCIAA;
	}
	@JsonProperty("ALTRO_RUOLO_CCIAA")
	public AltroRuoloCCIAA getAltroRuoloCCIAA() {
		return altroRuoloCCIAA;
	}
	@JsonProperty("ALTRO_RUOLO_CCIAA")
	public void setAltroRuoloCCIAA(AltroRuoloCCIAA altroRuoloCCIAA) {
		this.altroRuoloCCIAA = altroRuoloCCIAA;
	}
	
	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
