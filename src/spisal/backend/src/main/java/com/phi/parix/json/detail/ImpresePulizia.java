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
	"FASCIA",
	"DT_DENUNCIA",
	"VOLUME"
})
public class ImpresePulizia {
	@JsonProperty("FASCIA")
	private String fascia;
	@JsonProperty("DT_DENUNCIA")
	private String dtDenuncia;
	@JsonProperty("VOLUME")
	private String volume;
	
	@JsonProperty("FASCIA")
	public String getFascia() {
		return fascia;
	}
	@JsonProperty("FASCIA")
	public void setFascia(String fascia) {
		this.fascia = fascia;
	}
	@JsonProperty("DT_DENUNCIA")
	public String getDtDenuncia() {
		return dtDenuncia;
	}
	@JsonProperty("DT_DENUNCIA")
	public void setDtDenuncia(String dtDenuncia) {
		this.dtDenuncia = dtDenuncia;
	}
	@JsonProperty("VOLUME")
	public String getVolume() {
		return volume;
	}
	@JsonProperty("VOLUME")
	public void setVolume(String volume) {
		this.volume = volume;
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
