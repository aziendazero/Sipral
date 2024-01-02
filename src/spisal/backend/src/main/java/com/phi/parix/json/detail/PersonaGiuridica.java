
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
    "INDIRIZZO",
    "CODICE_FISCALE",
    "DENOMINAZIONE"
})
public class PersonaGiuridica {

    @JsonProperty("INDIRIZZO")
    private Indirizzo indirizzo;
    @JsonProperty("CODICE_FISCALE")
    private String codiceFiscale;
    @JsonProperty("DENOMINAZIONE")
    private String denominazione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    /**
     * 
     * @param indirizzo
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * 
     * @return
     *     The codiceFiscale
     */
    @JsonProperty("CODICE_FISCALE")
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * 
     * @param codiceFiscale
     *     The codice_fiscale
     */
    @JsonProperty("CODICE_FISCALE")
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    /**
     * 
     * @return
     *     The denominazione
     */
    @JsonProperty("DENOMINAZIONE")
    public String getDenominazione() {
        return denominazione;
    }

    /**
     * 
     * @param denominazione
     *     The denominazione
     */
    @JsonProperty("DENOMINAZIONE")
    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
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
