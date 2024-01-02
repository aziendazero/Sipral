
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
    "ATTIVITA_ISTAT"
})
public class CodiceAtecoUl {

    @JsonProperty("ATTIVITA_ISTAT")
    private List<AttivitaIstat> attivitaIstat = new ArrayList<AttivitaIstat>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The attivitaIstat
     */
    @JsonProperty("ATTIVITA_ISTAT")
    public List<AttivitaIstat> getAttivitaIstat() {
        return attivitaIstat;
    }

    /**
     * 
     * @param attivitaIstat
     *     The attivita_istat
     */
    @JsonProperty("ATTIVITA_ISTAT")
    public void setAttivitaIstat(List<AttivitaIstat> attivitaIstat) {
        this.attivitaIstat = attivitaIstat;
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
