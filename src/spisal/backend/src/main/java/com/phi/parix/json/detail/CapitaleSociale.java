
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
    "SOTTOSCRITTO",
    "VALUTA",
    "VERSATO",
    "DELIBERATO"
})
public class CapitaleSociale {

    @JsonProperty("SOTTOSCRITTO")
    private String sottoscritto;
    @JsonProperty("VALUTA")
    private String valuta;
    @JsonProperty("VERSATO")
    private String versato;
    @JsonProperty("DELIBERATO")
    private String deliberato;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The sottoscritto
     */
    @JsonProperty("SOTTOSCRITTO")
    public String getSottoscritto() {
        return sottoscritto;
    }

    /**
     * 
     * @param sottoscritto
     *     The sottoscritto
     */
    @JsonProperty("SOTTOSCRITTO")
    public void setSottoscritto(String sottoscritto) {
        this.sottoscritto = sottoscritto;
    }

    /**
     * 
     * @return
     *     The valuta
     */
    @JsonProperty("VALUTA")
    public String getValuta() {
        return valuta;
    }

    /**
     * 
     * @param valuta
     *     The valuta
     */
    @JsonProperty("VALUTA")
    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    /**
     * 
     * @return
     *     The versato
     */
    @JsonProperty("VERSATO")
    public String getVersato() {
        return versato;
    }

    /**
     * 
     * @param versato
     *     The versato
     */
    @JsonProperty("VERSATO")
    public void setVersato(String versato) {
        this.versato = versato;
    }

    /**
     * 
     * @return
     *     The deliberato
     */
    @JsonProperty("DELIBERATO")
    public String getDeliberato() {
        return deliberato;
    }

    /**
     * 
     * @param deliberato
     *     The deliberato
     */
    @JsonProperty("DELIBERATO")
    public void setDeliberato(String deliberato) {
        this.deliberato = deliberato;
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
