
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
    "DT_TERMINE",
    "SCADENZE_ESERCIZI",
    "DT_COSTITUZIONE"
})
public class DurataSocieta {

    @JsonProperty("DT_TERMINE")
    private Long dtTermine;
    @JsonProperty("SCADENZE_ESERCIZI")
    private ScadenzeEsercizi scadenzeEsercizi;
    @JsonProperty("DT_COSTITUZIONE")
    private Long dtCostituzione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The dtTermine
     */
    @JsonProperty("DT_TERMINE")
    public Long getDtTermine() {
        return dtTermine;
    }

    /**
     * 
     * @param dtTermine
     *     The dt_termine
     */
    @JsonProperty("DT_TERMINE")
    public void setDtTermine(Long dtTermine) {
        this.dtTermine = dtTermine;
    }

    /**
     * 
     * @return
     *     The scadenzeEsercizi
     */
    @JsonProperty("SCADENZE_ESERCIZI")
    public ScadenzeEsercizi getScadenzeEsercizi() {
        return scadenzeEsercizi;
    }

    /**
     * 
     * @param scadenzeEsercizi
     *     The scadenze_esercizi
     */
    @JsonProperty("SCADENZE_ESERCIZI")
    public void setScadenzeEsercizi(ScadenzeEsercizi scadenzeEsercizi) {
        this.scadenzeEsercizi = scadenzeEsercizi;
    }

    /**
     * 
     * @return
     *     The dtCostituzione
     */
    @JsonProperty("DT_COSTITUZIONE")
    public Long getDtCostituzione() {
        return dtCostituzione;
    }

    /**
     * 
     * @param dtCostituzione
     *     The dt_costituzione
     */
    @JsonProperty("DT_COSTITUZIONE")
    public void setDtCostituzione(Long dtCostituzione) {
        this.dtCostituzione = dtCostituzione;
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
