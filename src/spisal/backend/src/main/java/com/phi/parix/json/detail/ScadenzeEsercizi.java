
package com.phi.parix.json.detail;

import java.util.HashMap;
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
    "DT_PRIMO_ESERCIZIO"
})
public class ScadenzeEsercizi {

    @JsonProperty("DT_PRIMO_ESERCIZIO")
    private Long dtPrimoEsercizio;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The dtPrimoEsercizio
     */
    @JsonProperty("DT_PRIMO_ESERCIZIO")
    public Long getDtPrimoEsercizio() {
        return dtPrimoEsercizio;
    }

    /**
     * 
     * @param dtPrimoEsercizio
     *     The dt_primo_esercizio
     */
    @JsonProperty("DT_PRIMO_ESERCIZIO")
    public void setDtPrimoEsercizio(Long dtPrimoEsercizio) {
        this.dtPrimoEsercizio = dtPrimoEsercizio;
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
