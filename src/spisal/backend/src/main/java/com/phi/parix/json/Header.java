
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
    "ESITO",
    "SERVIZIO",
    "ESECUTORE"
})
public class Header {

    @JsonProperty("ESITO")
    private String esito;
    @JsonProperty("SERVIZIO")
    private String servizio;
    @JsonProperty("ESECUTORE")
    private String esecutore;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The esito
     */
    @JsonProperty("ESITO")
    public String getEsito() {
        return esito;
    }

    /**
     * 
     * @param esito
     *     The esito
     */
    @JsonProperty("ESITO")
    public void setEsito(String esito) {
        this.esito = esito;
    }

    /**
     * 
     * @return
     *     The servizio
     */
    @JsonProperty("SERVIZIO")
    public String getServizio() {
        return servizio;
    }

    /**
     * 
     * @param servizio
     *     The servizio
     */
    @JsonProperty("SERVIZIO")
    public void setServizio(String servizio) {
        this.servizio = servizio;
    }

    /**
     * 
     * @return
     *     The esecutore
     */
    @JsonProperty("ESECUTORE")
    public String getEsecutore() {
        return esecutore;
    }

    /**
     * 
     * @param esecutore
     *     The esecutore
     */
    @JsonProperty("ESECUTORE")
    public void setEsecutore(String esecutore) {
        this.esecutore = esecutore;
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
