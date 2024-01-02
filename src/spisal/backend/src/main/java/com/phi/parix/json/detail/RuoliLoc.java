package com.phi.parix.json.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "RUOLO_LOC"
})
public class RuoliLoc {
	@JsonProperty("RUOLO_LOC")
    private List<RuoloLoc> ruoloLoc = new ArrayList<RuoloLoc>();
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
    /**
     * 
     * @return
     *     The localizzazione
     */
    @JsonProperty("RUOLO_LOC")
    public List<RuoloLoc> getRuoloLoc() {
        return ruoloLoc;
    }

    /**
     * 
     * @param ruoloLoc
     *     The localizzazione
     */
    @JsonProperty("RUOLO_LOC")
    public void setRuoloLoc(List<RuoloLoc> ruoloLoc) {
        this.ruoloLoc = ruoloLoc;
    }
}
