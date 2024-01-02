
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
    "C_SEZIONE",
    "DT_ISCRIZIONE",
    "DESCRIZIONE"
})
public class Sezione {

    @JsonProperty("C_SEZIONE")
    private String csezione;
    @JsonProperty("DT_ISCRIZIONE")
    private Long dtIscrizione;
    @JsonProperty("DESCRIZIONE")
    private String descrizione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cSezione
     */
    @JsonProperty("C_SEZIONE")
    public String getCsezione() {
        return csezione;
    }

    /**
     * 
     * @param csezione
     *     The c_sezione
     */
    @JsonProperty("C_SEZIONE")
    public void setCsezione(String csezione) {
        this.csezione = csezione;
    }

    /**
     * 
     * @return
     *     The dtIscrizione
     */
    @JsonProperty("DT_ISCRIZIONE")
    public Long getDtIscrizione() {
        return dtIscrizione;
    }

    /**
     * 
     * @param dtIscrizione
     *     The dt_iscrizione
     */
    @JsonProperty("DT_ISCRIZIONE")
    public void setDtIscrizione(Long dtIscrizione) {
        this.dtIscrizione = dtIscrizione;
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
