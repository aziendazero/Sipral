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
	"FORMA",
	"ENTE_RILASCIO",
	"PROVINCIA"
})
public class AltroRuoloNonCCIAA {
	@JsonProperty("FORMA")
	private String forma;
	@JsonProperty("ENTE_RILASCIO")
	private String enteRilascio;
	@JsonProperty("PROVINCIA")
	private String provincia;
	
	@JsonProperty("FORMA")
	public String getForma() {
		return forma;
	}
	@JsonProperty("FORMA")
	public void setForma(String forma) {
		this.forma = forma;
	}
	@JsonProperty("ENTE_RILASCIO")
	public String getEnteRilascio() {
		return enteRilascio;
	}
	@JsonProperty("ENTE_RILASCIO")
	public void setEnteRilascio(String enteRilascio) {
		this.enteRilascio = enteRilascio;
	}
	@JsonProperty("PROVINCIA")
	public String getProvincia() {
		return provincia;
	}
	@JsonProperty("PROVINCIA")
	public void setProvincia(String provincia) {
		this.provincia = provincia;
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
