
package com.phi.parix.json.search;

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
    "TOTALE",
    "SCHEDA_PERSONA"
})
public class ListaPersone {

    @JsonProperty("TOTALE")
    private Integer totale;
    @JsonProperty("SCHEDA_PERSONA")
    private List<SchedaPersona> schedaPersona = new ArrayList<SchedaPersona>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The totale
     */
    @JsonProperty("TOTALE")
    public Integer getTotale() {
        return totale;
    }

    /**
     * 
     * @param totale
     *     The totale
     */
    @JsonProperty("TOTALE")
    public void setTotale(Integer totale) {
        this.totale = totale;
    }

    /**
     * 
     * @return
     *     The schedaPersona
     */
    @JsonProperty("SCHEDA_PERSONA")
    public List<SchedaPersona> getSchedaPersona() {
        return schedaPersona;
    }

    /**
     * 
     * @param schedaPersona
     *     The scheda_persona
     */
    @JsonProperty("SCHEDA_PERSONA")
    public void setSchedaPersona(List<SchedaPersona> schedaPersona) {
        this.schedaPersona = schedaPersona;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
