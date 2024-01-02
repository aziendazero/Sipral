
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
    "SEZIONI",
    "DATA",
    "NUMERO_RI"
})
public class DatiIscrizioneRi {

    @JsonProperty("SEZIONI")
    private Sezioni sezioni;
    @JsonProperty("DATA")
    private String data;
    @JsonProperty("NUMERO_RI")
    private String numeroRi;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The sezioni
     */
    @JsonProperty("SEZIONI")
    public Sezioni getSezioni() {
        return sezioni;
    }

    /**
     * 
     * @param sezioni
     *     The sezioni
     */
    @JsonProperty("SEZIONI")
    public void setSezioni(Sezioni sezioni) {
        this.sezioni = sezioni;
    }

    /**
     * 
     * @return
     *     The data
     */
    @JsonProperty("DATA")
    public String getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    @JsonProperty("DATA")
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 
     * @return
     *     The numeroRi
     */
    @JsonProperty("NUMERO_RI")
    public String getNumeroRi() {
        return numeroRi;
    }

    /**
     * 
     * @param numeroRi
     *     The numero_ri
     */
    @JsonProperty("NUMERO_RI")
    public void setNumeroRi(String numeroRi) {
        this.numeroRi = numeroRi;
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
