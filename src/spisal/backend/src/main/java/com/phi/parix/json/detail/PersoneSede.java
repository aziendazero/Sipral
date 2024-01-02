
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
    "TOTALE",
    "PERSONA"
})
public class PersoneSede {

    @JsonProperty("TOTALE")
    private Integer totale;
    @JsonProperty("PERSONA")
    private List<Persona> persona = new ArrayList<Persona>();
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
     *     The persona
     */
    @JsonProperty("PERSONA")
    public List<Persona> getPersona() {
        return persona;
    }

    /**
     * 
     * @param persona
     *     The persona
     */
    @JsonProperty("PERSONA")
    public void setPersona(List<Persona> persona) {
        this.persona = persona;
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
