
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
    "CAPITALE_SOCIALE",
    "TOTALE_QUOTE"
})
public class Capitali {

    @JsonProperty("CAPITALE_SOCIALE")
    private CapitaleSociale capitaleSociale;
    @JsonProperty("TOTALE_QUOTE")
    private TotaleQuote totaleQuote;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The capitaleSociale
     */
    @JsonProperty("CAPITALE_SOCIALE")
    public CapitaleSociale getCapitaleSociale() {
        return capitaleSociale;
    }

    /**
     * 
     * @param capitaleSociale
     *     The capitale_sociale
     */
    @JsonProperty("CAPITALE_SOCIALE")
    public void setCapitaleSociale(CapitaleSociale capitaleSociale) {
        this.capitaleSociale = capitaleSociale;
    }

    /**
     * 
     * @return
     *     The totaleQuote
     */
    @JsonProperty("TOTALE_QUOTE")
    public TotaleQuote getTotaleQuote() {
        return totaleQuote;
    }

    /**
     * 
     * @param totaleQuote
     *     The totale_quote
     */
    @JsonProperty("TOTALE_QUOTE")
    public void setTotaleQuote(TotaleQuote totaleQuote) {
        this.totaleQuote = totaleQuote;
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
