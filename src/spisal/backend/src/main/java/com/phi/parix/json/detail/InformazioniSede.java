
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
    "ATTIVITA",
    "INFO_ATTIVITA",
    "RUOLI_LOC",
    "CODICE_ATECO_UL"
})
public class InformazioniSede {

    @JsonProperty("INDIRIZZO")
    private Indirizzo indirizzo;
    @JsonProperty("ATTIVITA")
    private String attivita;
    @JsonProperty("INFO_ATTIVITA")
    private InfoAttivita infoAttivita;
    @JsonProperty("RUOLI_LOC")
    private RuoliLoc ruoliLoc;
    @JsonProperty("CODICE_ATECO_UL")
    private CodiceAtecoUl codiceAtecoUl;
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
     *     The attivita
     */
    @JsonProperty("ATTIVITA")
    public String getAttivita() {
        return attivita;
    }

    /**
     * 
     * @param attivita
     *     The attivita
     */
    @JsonProperty("ATTIVITA")
    public void setAttivita(String attivita) {
        this.attivita = attivita;
    }

    /**
     * 
     * @return
     *     The infoAttivita
     */
    @JsonProperty("INFO_ATTIVITA")
    public InfoAttivita getInfoAttivita() {
        return infoAttivita;
    }

    /**
     * 
     * @param infoAttivita
     *     The info_attivita
     */
    @JsonProperty("INFO_ATTIVITA")
    public void setInfoAttivita(InfoAttivita infoAttivita) {
        this.infoAttivita = infoAttivita;
    }
    
    /**
     * 
     * @return
     *     The ruoliLoc
     */
    @JsonProperty("RUOLI_LOC")
    public RuoliLoc getRuoliLoc() {
        return ruoliLoc;
    }

    /**
     * 
     * @param ruoliLoc
     *     The cessazione_loc
     */
    @JsonProperty("RUOLI_LOC")
    public void setRuoliLoc(RuoliLoc ruoliLoc) {
        this.ruoliLoc = ruoliLoc;
    }

    /**
     * 
     * @return
     *     The codiceAtecoUl
     */
    @JsonProperty("CODICE_ATECO_UL")
    public CodiceAtecoUl getCodiceAtecoUl() {
        return codiceAtecoUl;
    }

    /**
     * 
     * @param codiceAtecoUl
     *     The codice_ateco_ul
     */
    @JsonProperty("CODICE_ATECO_UL")
    public void setCodiceAtecoUl(CodiceAtecoUl codiceAtecoUl) {
        this.codiceAtecoUl = codiceAtecoUl;
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
