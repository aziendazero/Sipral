
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
    "VALUTA",
    "NUMERO_AZIONI",
    "AMMONTARE"
})
public class TotaleQuote {

    @JsonProperty("VALUTA")
    private String valuta;
    @JsonProperty("NUMERO_AZIONI")
    private Long numeroAzioni;
    @JsonProperty("AMMONTARE")
    private String ammontare;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The numeroAzioni
     */
    @JsonProperty("NUMERO_AZIONI")
    public Long getNumeroAzioni() {
        return numeroAzioni;
    }

    /**
     * 
     * @param numeroAzioni
     *     The numero_azioni
     */
    @JsonProperty("NUMERO_AZIONI")
    public void setNumeroAzioni(Long numeroAzioni) {
        this.numeroAzioni = numeroAzioni;
    }

    /**
     * 
     * @return
     *     The ammontare
     */
    @JsonProperty("AMMONTARE")
    public String getAmmontare() {
        return ammontare;
    }

    /**
     * 
     * @param ammontare
     *     The ammontare
     */
    @JsonProperty("AMMONTARE")
    public void setAmmontare(String ammontare) {
        this.ammontare = ammontare;
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
