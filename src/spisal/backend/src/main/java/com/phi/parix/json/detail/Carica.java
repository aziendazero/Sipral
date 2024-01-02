
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
    "C_CARICA",
    "DT_INIZIO",
    "DESCRIZIONE"
})
public class Carica {

    @JsonProperty("C_CARICA")
    private String ccarica;
    @JsonProperty("DT_INIZIO")
    private Long dtInizio;
    @JsonProperty("DESCRIZIONE")
    private String descrizione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cCarica
     */
    @JsonProperty("C_CARICA")
    public String getCcarica() {
        return ccarica;
    }

    /**
     * 
     * @param cCarica
     *     The c_carica
     */
    @JsonProperty("C_CARICA")
    public void setCcarica(String ccarica) {
        this.ccarica = ccarica;
    }

    /**
     * 
     * @return
     *     The dtInizio
     */
    @JsonProperty("DT_INIZIO")
    public Long getDtInizio() {
        return dtInizio;
    }

    /**
     * 
     * @param dtInizio
     *     The dt_inizio
     */
    @JsonProperty("DT_INIZIO")
    public void setDtInizio(Long dtInizio) {
        this.dtInizio = dtInizio;
    }

    /**
     * 
     * @return
     *     The descrizione
     */
    @JsonProperty("DESCRIZIONE")
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * 
     * @param descrizione
     *     The descrizione
     */
    @JsonProperty("DESCRIZIONE")
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
