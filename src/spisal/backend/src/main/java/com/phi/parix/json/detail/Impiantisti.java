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
	"LETTERA",
	"PROVINCIA",
	"NUMERO",
	"DT_ISCRIZIONE",
	"ENTE_RILASCIO"
})
public class Impiantisti {
	@JsonProperty("LETTERA")
	private String lettera;
	@JsonProperty("PROVINCIA")
	private String provincia;
	@JsonProperty("NUMERO")
	private String numero;
	@JsonProperty("DT_ISCRIZIONE")
	private String dtIscrizione;
	@JsonProperty("ENTE_RILASCIO")
	private String enteRilascio;
	
	@JsonProperty("LETTERA")
	public String getLettera() {
		return lettera;
	}
	@JsonProperty("LETTERA")
	public void setLettera(String lettera) {
		this.lettera = lettera;
	}
	@JsonProperty("PROVINCIA")
	public String getProvincia() {
		return provincia;
	}
	@JsonProperty("PROVINCIA")
	public void setProvincia(String provincia) {
		this.provincia = provincia;
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
	@JsonProperty("ENTE_RILASCIO")
	public String getEnteRilascio() {
		return enteRilascio;
	}
	@JsonProperty("ENTE_RILASCIO")
	public void setEnteRilascio(String enteRilascio) {
		this.enteRilascio = enteRilascio;
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
