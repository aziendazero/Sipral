
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
    "STATO",
    "PROVINCIA",
    "DATA",
    "C_COMUNE",
    "COMUNE"
})
public class EstremiNascita {

    @JsonProperty("STATO")
    private String stato;
    @JsonProperty("PROVINCIA")
    private String provincia;
    @JsonProperty("DATA")
    private String data;
    @JsonProperty("C_COMUNE")
    private String ccomune;
    @JsonProperty("COMUNE")
    private String comune;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The stato
     */
    @JsonProperty("STATO")
    public String getStato() {
        return stato;
    }

    /**
     * 
     * @param stato
     *     The stato
     */
    @JsonProperty("STATO")
    public void setStato(String stato) {
        this.stato = stato;
    }

    /**
     * 
     * @return
     *     The provincia
     */
    @JsonProperty("PROVINCIA")
    public String getProvincia() {
        return provincia;
    }

    /**
     * 
     * @param provincia
     *     The provincia
     */
    @JsonProperty("PROVINCIA")
    public void setProvincia(String provincia) {
        this.provincia = provincia;
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
     *     The ccomune
     */
    @JsonProperty("C_COMUNE")
    public String getCcomune() {
        return ccomune;
    }

    /**
     * 
     * @param ccomune
     *     The ccomune
     */
    @JsonProperty("C_COMUNE")
    public void setCcomune(String ccomune) {
        this.ccomune = ccomune;
    }

    /**
     * 
     * @return
     *     The comune
     */
    @JsonProperty("COMUNE")
    public String getComune() {
        return comune;
    }

    /**
     * 
     * @param comune
     *     The comune
     */
    @JsonProperty("COMUNE")
    public void setComune(String comune) {
        this.comune = comune;
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
