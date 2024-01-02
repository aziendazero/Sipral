
package com.phi.parix.json;

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
    "HEADER",
    "DATI"
})
public class Risposta {

    @JsonProperty("HEADER")
    private Header header;
    @JsonProperty("DATI")
    private Dati dati;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The header
     */
    @JsonProperty("HEADER")
    public Header getHeader() {
        return header;
    }

    /**
     * 
     * @param header
     *     The header
     */
    @JsonProperty("HEADER")
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * 
     * @return
     *     The dati
     */
    @JsonProperty("DATI")
    public Dati getDati() {
        return dati;
    }

    /**
     * 
     * @param dati
     *     The dati
     */
    @JsonProperty("DATI")
    public void setDati(Dati dati) {
        this.dati = dati;
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
