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
	"CAUSALE_CESSAZIONE",
	"DT_DOMANDA",
	"DT_DELIBERA",
	"DT_CESSAZIONE"
})
public class CessazioneRuolo {
	@JsonProperty("CAUSALE_CESSAZIONE")
	private String causaleCessazione;
	@JsonProperty("DT_DOMANDA")
	private String dtDomanda;
	@JsonProperty("DT_DELIBERA")
	private String dtDelibera;
	@JsonProperty("DT_CESSAZIONE")
	private String dtCessazione;
	
	@JsonProperty("CAUSALE_CESSAZIONE")
	public String getCausaleCessazione() {
		return causaleCessazione;
	}
	@JsonProperty("CAUSALE_CESSAZIONE")
	public void setCausaleCessazione(String causaleCessazione) {
		this.causaleCessazione = causaleCessazione;
	}
	@JsonProperty("DT_DOMANDA")
	public String getDtDomanda() {
		return dtDomanda;
	}
	@JsonProperty("DT_DOMANDA")
	public void setDtDomanda(String dtDomanda) {
		this.dtDomanda = dtDomanda;
	}
	@JsonProperty("DT_DELIBERA")
	public String getDtDelibera() {
		return dtDelibera;
	}
	@JsonProperty("DT_DELIBERA")
	public void setDtDelibera(String dtDelibera) {
		this.dtDelibera = dtDelibera;
	}
	@JsonProperty("DT_CESSAZIONE")
	public String getDtCessazione() {
		return dtCessazione;
	}
	@JsonProperty("DT_CESSAZIONE")
	public void setDtCessazione(String dtCessazione) {
		this.dtCessazione = dtCessazione;
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
