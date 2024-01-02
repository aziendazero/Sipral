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
	"TIPO",
	"PROVINCIA",
	"NUMERO",
	"DT_ISCRIZIONE",
	"QUALIFICA"
})
public class Meccanici {
	@JsonProperty("TIPO")
	private String tipo;
	@JsonProperty("PROVINCIA")
	private String provincia;
	@JsonProperty("NUMERO")
	private String numero;
	@JsonProperty("DT_ISCRIZIONE")
	private String dtIscrizione;
	@JsonProperty("QUALIFICA")
	private String qualifica;
	
	@JsonProperty("TIPO")
	public String getTipo() {
		return tipo;
	}
	@JsonProperty("TIPO")
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	@JsonProperty("QUALIFICA")
	public String getQualifica() {
		return qualifica;
	}
	@JsonProperty("QUALIFICA")
	public void setQualifica(String qualifica) {
		this.qualifica = qualifica;
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
